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

import junit.framework.TestCase;

/**
 *
 * @author Christian
 */
public class YetiClassLoaderTest extends TestCase {
    
    public YetiClassLoaderTest(String testName) {
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

    public ClassLoader mkClassLoader() {
        return TetiLoaderTest.yetiClassLoader(null, null, true);
    }


    /**
     * Test of loadClass method, of class YeitClassLoader.
     */
    public void testLoadClassError() throws Exception {
        ClassLoader ycl = mkClassLoader();
        try{
            ycl.loadClass("org.foo.foo");
            fail();
        }catch(Exception ex) {}
    }
    
    public void testLoadClassCompile() throws Exception {
        ClassLoader ycl = mkClassLoader();
        Class cl = ycl.loadClass("org.yeticl.test");
        assertNotNull(cl);
    }


    public void testThreadClassLoader() throws Exception {
        ClassLoader cl = Thread.currentThread().getContextClassLoader();
        assertNotNull(cl.loadClass("yeti.lang.compiler.eval"));
    }

    public void testYetiLoadClass() throws Exception {
        ClassLoader ycl = mkClassLoader();
        assertNotNull(ycl.loadClass("yeti.lang.compiler.eval"));
    }



}
