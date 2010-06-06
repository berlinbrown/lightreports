
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;;; Copyright (c) 2006-2007 Berlin Brown and botnode.com  All Rights Reserved
;;;
;;; http://www.opensource.org/licenses/bsd-license.php

;;; All rights reserved.

;;; Redistribution and use in source and binary forms, with or without modification,
;;; are permitted provided that the following conditions are met:

;;; * Redistributions of source code must retain the above copyright notice,
;;; this list of conditions and the following disclaimer.
;;; * Redistributions in binary form must reproduce the above copyright notice,
;;; this list of conditions and the following disclaimer in the documentation
;;; and/or other materials provided with the distribution.
;;; * Neither the name of the Botnode.com (Berlin Brown) nor
;;; the names of its contributors may be used to endorse or promote
;;; products derived from this software without specific prior written permission.

;;; THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
;;; "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT
;;; LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR
;;; A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR
;;; CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL,
;;; EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO,
;;; PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR
;;; PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF
;;; LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
;;; NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
;;; SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
;;;
;;; Date: 1/5/2009
;;; Main Description: Light is a simple text editor in clojure
;;; Contact: Berlin Brown <berlin dot brown at gmail.com>
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

(ns light.toolkit.light_codegen_templates
    (:import (java.util Date)
             (java.text MessageFormat)))

(def *templ-current-datetime* (str (new Date)))

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

(def *codegen-templ-junit-suite*
"/*******************************************************************************
 *
 * Copyright (c) 2008-2009 Developer
 *
 * Description: Junit Test Suite
 * Tested with: junit-4.4.
 *
 * REVISION HISTORY
 * -------------------------
 *******************************************************************************/

package org.light.testframework;

import junit.framework.Test;
import junit.framework.TestSuite;

import org.light.testframework.PROJECT.TEST;

public final class TextTestRunnerSuite {
 
  /**
   * Create the test suite.
   */
  public static final Test suite() {

    TestSuite suite = new TestSuite();
 
    // The ShoppingCartTest we created above.
    suite.addTestSuite(Test.class);

  }

  /**
   * Runs the test suite using the textual runner.
   */
  public static final void main(final String[] args) {
        junit.textui.TestRunner.run(suite());
  } // End of Main

} // End of Test Suite Class
")

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

(def *codegen-templ-junit-test*
"/*******************************************************************************
 *
 * Copyright (c) 2008-2009 Developer
 *
 * Description: Junit Test Suite
 * Tested with: junit-4.4.
 *
 *******************************************************************************/

package org.light.testframework.PROJECT;

import junit.framework.Test;
import junit.framework.TestSuite;
import junit.framework.TestCase;

public final class MySimpleTest extends TestCase {

    /**
     * Sets up the test fixture.
     *
     * Called before every test case method.
     */
    protected void setUp() {

    }

    /**
     * Tears down the test fixture.
     *
     * Called after every test case method.
     */
    protected void tearDown() {
    }

    public void testMySimple() {
        assertNotNull(\"My Simple Test Message\", null);
    } // End of Test

} // End of Test Suite Class
")

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

(def *codegen-templ-build-xml-tmpl1*
"<?xml version=\"1.0\" encoding=\"UTF-8\"?>
<!-- ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;; -->
<!-- Copyright (c) 2009           -->
<!-- Code generated Ant Script    -->
<!-- Created on : {0} -->
<!-- ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;; -->
")
(def *codegen-templ-build-xml-tmpl2*
"<project name=\"Build File\" default=\"package\" basedir=\".\">
    <property file=\"build.properties\" /> 
    <property name=\"build.dir\"            value=\"${basedir}/build\" />
    <property name=\"build.classes.dir\"    value=\"${basedir}/build/classes\" />
    <property name=\"src.dir\"              value=\"${basedir}/JavaSource\" />
    <property name=\"lib.dir\"              value=\"${basedir}/lib\" />

<target name=\"help\" description=\"Initialization\">
            <tstamp/>
            <echo>
 Additional Targets and Usage:
 -----------------
 compile   - to compile the source, use this target.  
             To build the complete release package, 
             you must compile this manually.
 run.tests - Run a java target to invoke the junit test suite.
 run.junit - Run a junit target to invoke the junit test suite.
             Pretty print the output to XML output.

</echo>
    </target>    
    
    <path id=\"classpath\">
        <pathelement location=\"${lib.dir}/junit-4.4.jar\" />
    </path>

    <!-- ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;; -->
    <!-- Target: Prepare -->
    <!-- ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;; -->
    <target name=\"prepare\" depends=\"help\">
        <mkdir dir=\"${build.dir}\"/>
        <mkdir dir=\"${build.classes.dir}\"/>
    </target>

    <!-- ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;; -->
    <!-- Target: Compile -->
    <!-- ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;; -->
    <target name=\"compile\" depends=\"prepare\">
        <javac deprecation=\"true\"
                target=\"1.5\"
                srcdir=\"${src.dir}\"
                destdir=\"${build.classes.dir}\">

            <include name=\"org\\light\\testframework\\TextTestRunnerSuite.java\" />

            <classpath refid=\"classpath\"/>
        </javac>
    </target>

    <!-- ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;; -->        
    <!-- Target: Build -->
    <!-- ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;; -->
    <target name=\"package\" depends=\"compile\">
    </target>

    <!-- ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;; -->
    <!-- Target: Run Tests -->
    <!-- ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;; -->
    <target name=\"run.tests\" depends=\"compile\">
    	<java classname=\"org.light.testframework.TextTestRunnerSuite\">
			<classpath refid=\"classpath\" />
			<classpath>
				<pathelement location=\"${build.classes.dir}\" />
			</classpath>    	            
			<arg value=\"--empty\" />
		</java>
    </target>

    <!-- ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;; -->
    <!-- Target: Run Tests Set 2 (ant junit task) -->
    <!-- ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;; -->
    <target name=\"run.junit\" depends=\"compile\">
        <junit printsummary=\"yes\" fork=\"yes\" haltonfailure=\"no\">
           <formatter type=\"xml\"/>
           <test name=\"org.light.testframework.Test\" />

			<classpath refid=\"classpath\" />
			<classpath>
				<pathelement location=\"${build.classes.dir}\" />
			</classpath>    	            
        </junit>
    </target>
   
    <!-- ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;; -->
    <!-- Main Clean -->
    <!-- ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;; -->
    <target name=\"clean\">
        <delete dir=\"${build.dir}\" />
    </target>

</project>
")


;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

(def *codegen-templ-xhtml-tmpl1*
"<?xml version=\"1.0\" encoding=\"UTF-8\"?>
<!DOCTYPE html PUBLIC \"-//W3C//DTD XHTML 1.0 Strict//EN\" \"http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd\">

<html xmlns=\"http://www.w3.org/1999/xhtml\" xml:lang=\"en\" lang=\"en\">
<head>
    <!-- XHTML Template Created at {0} -->
")
(def *codegen-templ-xhtml-tmpl2*
"
    <title>Title</title>
    <style type=\"text/css\">

     #main_content #header_sect {
        background-color:   #F8F8F8;
        border-bottom:      1px solid #CCC;
     }

     @page { 
        size: 8.5in 11in;

         @bottom-right {
           content: \"Page \" counter(page) \" of \" counter(pages);
         }                          
     }

     @media print {
		table { 
   			-fs-table-paginate: paginate;
		}
     }

	</style>
</head>
<body>
 
 <div id=\"main_content\">
    <div id=\"header_sect\">
      Simple Header for PDF Output
    </div>
    
    <table>
     <thead>
       <tr>
         <th> 
            Col 1
         </th>
       </tr>
     </thead>
 
     <tbody>
       <tr> 
         <td>Data Col 1</td>
       </tr>
     </body>
    </table>

 </div>

</body>
</html>")

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

(def *codegen-templ-xhtml-tmp3b*
"
    <title>Title</title>
    <style type=\"text/css\">

     #main_content #header_sect {
        border-top:      1px solid #CCC;
        border-bottom:   1px solid #CCC;
     }

     @page { 
         size: 8.5in 11in;

         @bottom-right {
           content: \"Page \" counter(page) \" of \" counter(pages);
         }                          
     }

     @media print {
		table { 
   			-fs-table-paginate: paginate;
		}
     }
	</style>
</head>
<body> 
 <div id=\"main_content\">
    <div id=\"header_sect\"></div>
    <div> <!-- Report Section -->
")

(def *codegen-templ-xhtml-footer*
     "   
    </div>
 </div>

</body>
</html>")

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

(def *codegen-templ-build-xml*
     (str (. MessageFormat format *codegen-templ-build-xml-tmpl1*
             (to-array [*templ-current-datetime*]))
          *codegen-templ-build-xml-tmpl2*))

(def *codegen-templ-xhtml*
     (str (. MessageFormat format *codegen-templ-xhtml-tmpl1*
             (to-array [*templ-current-datetime*]))
          *codegen-templ-xhtml-tmpl2*))

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;; REVISION HISTORY
;;
;; -------------------------------------
;; + 1/5/2009  Berlin Brown
;; Description: Project Create Date

;; + 1/5/2009  Berlin Brown
;; Description: Add new headers
;; 
;; -------------------------------------

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;;; End of Script
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;      