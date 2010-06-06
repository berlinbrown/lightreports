/**
 * Copyright (c) 2006-2010 Berlin Brown and botnode.com  All Rights Reserved
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
 * Date: 1/5/2009, updated 5/5/2010, added Scala 2.8rc3
 *       7/15/2009 - Added Clojure 1.0, other performance fixes and cleanups.
 *       
 * Main Description: Light Log Viewer is a tool for making it easier to search log files.  
 * Light Log Viewer adds some text highlighting, quick key navigation to text files, simple graphs 
 * and charts for monitoring logs, file database to quickly navigate to files of interest, 
 * and HTML to PDF convert tool.  
 * Light Log was developed with a combination of Clojure 1.0, Java and Scala with use of libs, SWT 3.4, JFreeChart, iText. 
 * 
 * The clojure source is contained in : HOME/src/octane
 * The java source is contained in :  HOME/src/java/src
 * 
 * To build the java source, see : HOME/src/java/build.xml and build_pdf_gui.xml
 *  
 * Additional Development Notes: The SWT gui and other libraries are launched from a dynamic classloader.  Clojure is also
 *   started from the same code, and reflection is used to dynamically initiate Clojure. See the 'start' package.  The binary
 *   code is contained in the octane_start.jar library.
 *   
 * Home Page: http://code.google.com/p/lighttexteditor/
 * 
 * Contact: Berlin Brown <berlin dot brown at gmail.com>
 */
package org.berlin.light.toolkit.rpts

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;

import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.events.ShellAdapter;
import org.eclipse.swt.events.ShellEvent;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;

import com.octane.util.StringUtils;

/**
 * Create the Batch Reports Window.
 * 
 * @author bbrown
 */
class ReportWinWidgets(display:Display, shell:Shell) {
   
    val gridDataText = new GridData(SWT.FILL, SWT.NONE, true, false, 1, 1)  
    val gridDataLabel = new GridData(SWT.NONE, SWT.NONE, false, false, 1, 1) 
    
    /**
     * Implementation Routine addWidgets.
     */
    def putWidgets() : ReportWinWidgets = {
                        
        ////////////////////////
        // Row 1
        ////////////////////////
        val labelDir = new Label(shell, SWT.NONE)
        labelDir.setLayoutData(gridDataText)
        labelDir.setText("PDF Working Directory (dirpath):")
        labelDir.setLayoutData(gridDataLabel)              
                        
        ////////////////////////
        // Row 2
        ////////////////////////    
        val label_fname = new Label(shell, SWT.NONE)
        label_fname.setText("HTML Input Filename (filename.ext):")
        label_fname.setLayoutData(gridDataLabel)
        
        ////////////////////////
        // Row 3
        ////////////////////////
        val label_outputpdf = new Label(shell, SWT.NONE)
        label_outputpdf.setText("Output PDF Filename (filename.ext):")
        label_outputpdf.setLayoutData(gridDataLabel)      
            
        
        ////////////////////////
        // Row 4
        ////////////////////////
        val label_parserclass = new Label(shell, SWT.NONE)
        label_parserclass.setText("Html Resources Base Path:")
        label_parserclass.setLayoutData(gridDataLabel)
        this
    }
           
    /**
     * Create the window and set the SWT shell properties. 
     */
    def createWinShell() : ReportWinWidgets = {
        
        shell.setSize(510, 210)
        shell.setLocation(180, 250)
        
        val layout:GridLayout = new GridLayout     
        layout.numColumns = 2
        shell.setLayout(layout)
        this.putWidgets()
                
        shell.setText("Batch Create Reports");                
        shell.open()
        this
    }
    
    /**
     * Core Event Wait Loop.
     */
    def winLoop() {
        
        if (shell == null) {
            return
        }
        if (display == null) {
            return
        }        
        while (!shell.isDisposed()) {
            if (!display.readAndDispatch())
                display.sleep();
        }
        display.dispose()
    }
     
} // End of the Class //

object TestCreateWindow {
    
    def main(args : Array[String]) : Unit = {
            val disp = new Display()
            val shell = new Shell()
            val win = new ReportWinWidgets(disp, shell)
            win.createWinShell
            win.winLoop()
    }
    
} // End of the Object //