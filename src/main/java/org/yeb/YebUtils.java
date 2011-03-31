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

import junit.framework.Test;
import org.yeticl.YetiShellUtils;

/**
 *
 * @author Christian
 */
public class YebUtils {


    public static Test createSuite(String specModuleName) {
        Test test = (Test) YetiShellUtils.evalWithResult("m = load org.yeb.yebspec; sm = load "+specModuleName+"; m.junitTest sm");
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

}
