package org.berlin.light.tests

import junit.framework._
import org.junit.Assert._
import junit.textui.TestRunner

import org.berlin.light.toolkit.TemplateParseFileLine

class TestParseLine extends TestCase {

    override def setUp() = {
    }
    override def tearDown() = {
    }
    
    def testParse1() = { 
        
        System.getProperties().put("octane.install.dir", "C:\\tmp\\test")
        val lx = new TemplateParseFileLine
        val res = lx.findTemplateLightHome("%LIGHT_HOME%/test")
        println(res)
        
        val res2 = lx.findTemplateLightHomeToExternal("%LIGHT_HOME%/test")
        println(res2)
        
    }
    
} // End of the Class //

object TestParseLineMain {
    
    def main(args : Array[String]) : Unit = {            
            TestRunner.run(classOf[TestParseLine]);

    }
    
} // End of the Object //

