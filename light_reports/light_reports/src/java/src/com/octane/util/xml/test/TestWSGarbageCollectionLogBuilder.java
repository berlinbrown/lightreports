/********************************************************************
 *
 * Copyright (c) 2006-2007 Berlin Brown and botnode.com  All Rights Reserved
 *
 * http://www.opensource.org/licenses/bsd-license.php

 * All rights reserved.

 * Redistribution and use in source and binary forms, with or without modification,
 * are permitted provided that the following conditions are met:

 * * Redistributions of source code must retain the above copyright notice,
 * this list of conditions and the following disclaimer.
 * * Redistributions in binary form must reproduce the above copyright notice,
 * this list of conditions and the following disclaimer in the documentation
 * and/or other materials provided with the distribution.
 * * Neither the name of the Botnode.com (Berlin Brown) nor
 * the names of its contributors may be used to endorse or promote
 * products derived from this software without specific prior written permission.

 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
 * "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT
 * LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR
 * A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR
 * CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL,
 * EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO,
 * PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR
 * PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF
 * LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
 * NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 *
 * Date: 1/5/2009
 *       7/15/2009 - Added Clojure 1.0, other performance fixes and cleanups.
 *       
 * Main Description: Light Log Viewer is a tool for making it easier to search log files.  
 * Light Log Viewer adds some text highlighting, quick key navigation to text files, simple graphs 
 * and charts for monitoring logs, file database to quickly navigate to files of interest, 
 * and HTML to PDF convert tool.  
 * Light Log was developed with a combination of Clojure 1.0, Java and Scala with use of libs, SWT 3.4, JFreeChart, iText. 
 * 
 * Quickstart : the best way to run the Light Log viewer is to click on the win32 batch script light_logs.bat
 * (you may need to edit the Linux script for Unix/Linux environments).
 * Edit the win32 script to add more heap memory or other parameters.
 * 
 * The clojure source is contained in : HOME/src/octane
 * The java source is contained in :  HOME/src/java/src
 * 
 * To build the java source, see : HOME/src/java/build.xml and build_pdf_gui.xml
 * 
 * Metrics: (as of 7/15/2009) Light Log Viewer consists of 6500 lines of Clojure code, and contains wrapper code
 *  around the Java source.  There are 2000+ lines of Java code in the Java library for Light Log Viewer.
 *  
 * Additional Development Notes: The SWT gui and other libraries are launched from a dynamic classloader.  Clojure is also
 *   started from the same code, and reflection is used to dynamically initiate Clojure. See the 'start' package.  The binary
 *   code is contained in the octane_start.jar library.
 *   
 * Home Page: http://code.google.com/p/lighttexteditor/
 * 
 * Contact: Berlin Brown <berlin dot brown at gmail.com>
 *********************************************************************/
package com.octane.util.xml.test;

import java.util.regex.Matcher;

import com.octane.test.BasicTestCase;
import com.octane.util.junit.framework.TestCase;
import com.octane.util.junit.framework.TestSuite;
import com.octane.util.junit.textui.TestRunner;
import com.octane.util.xml.WSGarbageCollectionLogBuilder;

public class TestWSGarbageCollectionLogBuilder extends BasicTestCase {
    
    /**
     * An instance of the class being tested.
     *
     * @see WSGarbageCollectionLogBuilder
     *
     * @generatedBy CodePro at 6/25/09 10:16 AM
     */
    private WSGarbageCollectionLogBuilder fixture;
    
    @Override
    protected void loadOptimisticTestObjects() {
        // TODO        
    }

    @Override
    protected void loadPessimisticTestObjects() {
        // TODO
        
    }
    
    public void testSimple() {
        assertEquals(1, 1);
    }
    
    public void testMatchHeader() {
        final String [] doc =  {
                null, 
                "<?xml version=\"1.0\" ?>",
                "",
                " sdfsd <?xml version=\"1.0\" ?> \n\n\n kjdfld \n <dogs /> \n <cats /> \n  <?xml version=\"1.0\" ?> "
        };
        for (int i = 0; i < doc.length; i++) {
            WSGarbageCollectionLogBuilder builder = new WSGarbageCollectionLogBuilder(doc[i]);
            Matcher match = builder.matchHeader();            
        }        
        
        WSGarbageCollectionLogBuilder builder = new WSGarbageCollectionLogBuilder(doc[3]);
        builder.repairInit();
        builder.repairRemoveHeader();
        builder.repairOnlyXMLTags();
        builder.repairAddOuterTags();
        System.out.println("--" + builder.getRepairDocument());
    }    

    /**
     * Return an instance of the class being tested.
     *
     * @return an instance of the class being tested
     *
     * @see WSGarbageCollectionLogBuilder
     *
     * @generatedBy CodePro at 6/25/09 10:16 AM
     */
    public WSGarbageCollectionLogBuilder getFixture()
        throws Exception {
        if (fixture == null) {
            fixture = new WSGarbageCollectionLogBuilder("1");
        }
        return fixture;
    }

    /**
     * Run the WSGarbageCollectionLogBuilder(String) constructor test.
     *
     * @generatedBy CodePro at 6/25/09 10:16 AM
     */
    public void testWSGarbageCollectionLogBuilder_1()
        throws Exception {
        String indoc = "1";
        WSGarbageCollectionLogBuilder result = new WSGarbageCollectionLogBuilder(indoc);
        // add additional test code here
        assertNotNull(result);
    }

    /**
     * Run the WSGarbageCollectionLogBuilder(String) constructor test.
     *
     * @generatedBy CodePro at 6/25/09 10:16 AM
     */
    public void testWSGarbageCollectionLogBuilder_2()
        throws Exception {
        String indoc = null;
        WSGarbageCollectionLogBuilder result = new WSGarbageCollectionLogBuilder(indoc);
        // add additional test code here
        assertNotNull(result);
    }

    /**
     * Run the Matcher matchHeader() method test.
     *
     * @generatedBy CodePro at 6/25/09 10:16 AM
     */
    public void testMatchHeader_fixture_1()
        throws Exception {
        WSGarbageCollectionLogBuilder fixture2 = getFixture();
        Matcher result = fixture2.matchHeader();
        // add additional test code here
        assertNotNull(result);
        assertEquals(false, result.find());
        assertEquals(1, result.groupCount());
        assertEquals(true, result.hasAnchoringBounds());
        assertEquals(false, result.hasTransparentBounds());
        assertEquals(false, result.hitEnd());
        assertEquals(false, result.lookingAt());
        assertEquals(false, result.matches());
        assertEquals(1, result.regionEnd());
        assertEquals(0, result.regionStart());
        assertEquals(false, result.requireEnd());
        assertEquals("java.util.regex.Matcher[pattern=<\\?xml version(.*?)\\?> region=0,1 lastmatch=]", result.toString());
    }

    /**
     * Perform pre-test initialization.
     *
     * @throws Exception
     *         if the initialization fails for some reason
     *
     * @see TestCase#setUp()
     *
     * @generatedBy CodePro at 6/25/09 10:16 AM
     */
    protected void setUp() throws Exception {
        super.setUp();
        // add additional set up code here
    }

    /**
     * Perform post-test clean-up.
     *
     * @throws Exception
     *         if the clean-up fails for some reason
     *
     * @see TestCase#tearDown()
     *
     * @generatedBy CodePro at 6/25/09 10:16 AM
     */
    protected void tearDown() throws Exception {
        super.tearDown();
        // Add additional tear down code here
    }

    /**
     * Launch the test.
     *
     * @param args the command line arguments
     *
     * @generatedBy CodePro at 6/25/09 10:16 AM
     */
    public static void main(String[] args) {
        if (args.length == 0) {
            // Run all of the tests
            TestRunner.run(TestWSGarbageCollectionLogBuilder.class);
        } else {
            // Run only the named tests
            TestSuite suite = new TestSuite("Selected tests");
            for (int i = 0; i < args.length; i++) {
                TestCase test = new TestWSGarbageCollectionLogBuilder();
                test.setName(args[i]);
                suite.addTest(test);
            }
            TestRunner.run(suite);
        }
    }
       
} // End of the Class //
