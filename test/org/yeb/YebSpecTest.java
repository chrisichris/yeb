package org.yeb;

import junit.framework.Test;
import junit.framework.TestCase;

public class YebSpecTest extends TestCase {
    
    public YebSpecTest(String testName) {
        super(testName);
    }

    public static Test suite() {
        Test suite = YebUtils.createSuite("org.yeb.allSpecSuite");
        System.out.println(suite.countTestCases());
        return suite;
    }

}
