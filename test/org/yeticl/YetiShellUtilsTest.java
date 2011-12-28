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
import yeti.lang.compiler.CompileException;

/**
 *
 * @author Christian
 */
public class YetiShellUtilsTest extends TestCase {
    
    public YetiShellUtilsTest(String testName) {
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

    
    public void testModuleValue() throws Exception {
        ClassLoader ycl = mkClassLoader();
        Object r = YetiShellUtils.moduleLoad(ycl,"org.yeticl.test");
        assertEquals("test",r);
    }


 

}
