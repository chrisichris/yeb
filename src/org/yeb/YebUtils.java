/*
 * Copyright 2011 Christian Essl
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions
 * and limitations under the License.
 */
package org.yeb;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.regex.Pattern;
import junit.framework.Test;

import org.yeticl.YetiCompileHelper;
import yeti.lang.Fun;

/**
 *
 * @author Christian
 */
public class YebUtils {

    static public Object moduleLoad(ClassLoader classLoader,String moduleName) throws ClassNotFoundException, NoSuchMethodException, IllegalAccessException, IllegalArgumentException, InvocationTargetException {
        classLoader = classLoader == null ? Thread.currentThread().getContextClassLoader() : classLoader;
        ClassLoader oldCl = Thread.currentThread().getContextClassLoader();
        Thread.currentThread().setContextClassLoader(classLoader);
        try {
            Class cl = classLoader.loadClass(moduleName);
            Method evalM = cl.getMethod("eval", new Class[]{});
            return evalM.invoke(null, new Object[]{});
        } finally {
            Thread.currentThread().setContextClassLoader(oldCl);
        }
    }

    static public Object moduleRun(ClassLoader classLoader,String moduleName, Object param) throws ClassNotFoundException, NoSuchMethodException, IllegalAccessException, IllegalArgumentException, InvocationTargetException {
        ClassLoader oldCl = Thread.currentThread().getContextClassLoader();
        if(classLoader != null)
            Thread.currentThread().setContextClassLoader(classLoader);
        try {
            Fun fun = (Fun) moduleLoad(classLoader,moduleName);
            return fun.apply(param);
        } finally {
            Thread.currentThread().setContextClassLoader(oldCl);
        }
    }

    public static Test createSuite(String specModuleName) {
        YetiCompileHelper helper = new YetiCompileHelper();
        Test test = (Test) helper.evaluate("m = load org.yeb.yebspec; sm = load "+specModuleName+"; m.junitTest sm");
        return test;
    }

    public static String camelCaseToUnderline(String name) {
        final int length = name.length();
        boolean inupper = false;
        StringBuilder stb = new StringBuilder(length);
        int i = 0;
        while (i < length) {
            char c = name.charAt(i);
            i = i + 1;
            if(inupper) {
                if(Character.isLowerCase(c)){
                    stb.append(c);
                    inupper = false;
                }else if(i < length && Character.isUpperCase(c) && Character.isLowerCase(name.charAt(i))){
                    stb.append('_');
                    stb.append(Character.toLowerCase(c));
                    inupper = false;
                }else{
                    stb.append(Character.toLowerCase(c));
                }
            }else{
                if(Character.isUpperCase(c)) {
                    stb.append('_');
                    stb.append(Character.toLowerCase(c));
                    inupper = true;
                }else{
                    stb.append(c);
                }
            }
        }
        return stb.toString();
    }

    public static String patternToRegex(String pattern) {
        //tested in handlerSpec
        int prefixCut = pattern.indexOf("::");
        StringBuilder result = new StringBuilder(pattern.length() + 8);
        char[] chars = null;
        if(prefixCut != - 1) {
            result.append(pattern.substring(0, prefixCut+2));
            chars = pattern.substring(prefixCut+2).toCharArray();
        }else {
            chars = pattern.toCharArray();
        };

        int i = 0;
        while (i < chars.length) {
            char c = chars[i];
            if (c == '{' && (i+1) <chars.length && chars[i+1] == '}') {
                result.append("([^/]+)");
                i = i + 2;
            }else if (c == '{' && (i + 2) < chars.length && chars[i+2] == '}') {
                c = chars[i];
                switch(chars[i+1]){
                    case '*': result.append("(.*)"); break;
                    case 'd': result.append("([\\d^/]+)"); break;
                    case 'w': result.append("([\\w^/]+)"); break;
                    default: throw new IllegalArgumentException("Pattern is not valid at char "+i+" must only contain curly braces where appropriate: "+pattern);
                }
                i = i + 3;
            }else if (c == '{') {
                throw new IllegalArgumentException("Pattern is not valid at char "+i+" must only contain curly braces where appropriate: "+pattern);
            }else if (c == '<') {
                int oi = i;
                while (i < chars.length && chars[i] != '>')
                    i = i +1;

                if(chars[i] == '>') {//we have a regex
                    result.append('(').append(chars, oi + 1, i - (oi+1)).append(')');
                    i = i +1; //consume '>'
                }else {
                    throw new IllegalArgumentException("Pattern is not valid at char "+i+" must only contain < braces where appropriate: "+pattern);
                }

            } else {
                int oi = i;
                i = i + 1;
                while (i < chars.length && chars[i] != '<' && chars[i] != '{')
                    i = i +1;
                result.append(quoteRegex(chars,oi, i - oi));
            }
        }
        return result.toString();
    }

    private static String quoteRegex(char[] chars, int start, int length) {
        String str = new String(chars,start,length);
        str = Pattern.quote(str);
        return str;
    }



}
