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

import java.io.InputStream;
import java.lang.reflect.Constructor;
import junit.framework.TestCase;

/**
 *
 * @author Christian
 */
public class TetiLoaderTest extends TestCase {
    
    public TetiLoaderTest(String testName) {
        super(testName);
    }

    @Override
    protected void setUp() throws Exception {
        super.setUp();
    }

    @Override
    protected void tearDown() throws Exception {
        super.tearDown();
    }

    static ClassLoader yetiClassLoader(ClassLoader parentNullable, String[] sourceDirsNullable, boolean useClassPath) {
        try {
            ClassLoader parent = parentNullable == null ? Thread.currentThread().getContextClassLoader() : parentNullable;
            String[] sourceDirs = sourceDirsNullable == null ? new String[]{} : sourceDirsNullable;
            Class cl = Thread.currentThread().getContextClassLoader().loadClass("org.yeticl.YetiClassLoader");
            Constructor c = cl.getConstructor(ClassLoader.class, String[].class, Boolean.TYPE);
            return (ClassLoader) c.newInstance(parent, sourceDirs, useClassPath);
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }

    public ClassLoader mkClassLoader() {
        return yetiClassLoader(null, null, true);
    }
 
    public void testLoadTetiTest() throws Exception {
        ClassLoader ycl = mkClassLoader();
        String r = YetiShellUtils.moduleLoad(ycl, "org.yeticl.tetiTest").toString();
        assertEquals("Hier kommt Kurt.",r.trim());
    }

    public void testLoadFootTetiTest() throws Exception {
        ClassLoader ycl = mkClassLoader();
        YetiShellUtils.moduleLoad(ycl, "org.yeticl.footeti").toString();
    }


    public void testEndWithDoneTetiTest() throws Exception {
        ClassLoader ycl = mkClassLoader();
        StringBuffer stB = new StringBuffer();
        YetiShellUtils.moduleRun(ycl, "org.yeticl.endWithDoneTest", stB);
        assertEquals("Does end with done?",stB.toString().trim());
    }

}
