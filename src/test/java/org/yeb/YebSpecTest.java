/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.yeb;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

/**
 *
 * @author Christian
 */
public class YebSpecTest extends TestCase {
    
    public YebSpecTest(String testName) {
        super(testName);
    }

    public static Test suite() {
        Test suite = YebUtils.createSuite("org.yeb.allSpecSuite");
        System.out.println(suite.countTestCases());
        return suite;
    }

    protected void setUp() throws Exception {
        super.setUp();
    }

    protected void tearDown() throws Exception {
        super.tearDown();
    }

}
