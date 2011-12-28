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

package org.yeticl;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import yeti.lang.Fun;
import yeti.lang.MList;
import yeti.lang.Struct;
import yeti.lang.Tag;
import yeti.lang.compiler.CompileException;

/**
 *
 * @author Christian
 */
public class YetiShellUtils {


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


}
