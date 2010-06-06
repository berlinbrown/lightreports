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
 * Main Description: Light is a simple text editor in clojure
 * Contact: Berlin Brown <berlin dot brown at gmail.com>
 *********************************************************************/

package com.light.pdf.gui;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

import com.light.pdf.XHTMLParserFactory;
import com.light.pdf.XHTMLRendererBase;

/**
 * Simple HTML to PDF converter.
 */
public abstract class LightHtmlPDFWinAdapters extends LightHtmlPDFWinTemplates {
	
	/**
	 * Grid Data for text.
	 */
    private final GridData GRID_DATA_TEXT  = new GridData(SWT.FILL, SWT.NONE, true, false, 1, 1);
    
    /**
     * Grid Data for Label.
     */
    private final GridData GRID_DATA_LABEL = new GridData(SWT.NONE, SWT.NONE, false, false, 1, 1);

    /**
     * Grid Data Full.
     */    
    private final GridData GRID_DATA_LABEL_FULL = new GridData(SWT.NONE, SWT.NONE, true, false, 2, 1);

    /**
     * Main DisplacurMsgy.
     */
    private final Display display;

    /**
     * Adobe Exe Path.
     */
    private String adobePathExe;

    /**
     * Main Shell.
     */
    private final Shell shell;
 
    /**
     * Status Bar Text.
     */
    private Text statusBar;
    
    /**
     * Working Directory Text.
     */
    private Text workDirText;

    /**
     * Where the filename is located text.     
     */
    private Text filenameText;

    /**
     * Output PDF Text Location.
     */
    private Text outputPDFText;

    /**
     * Parser Text Location.
     */
    private Text parserText;

    public static final String PDF_WORK_LOC_WIN   = "C:\\tmp\\pdf_work\\_work";
    public static final String PDF_WORK_LOC_LINUX = "/tmp/pdf_work/_work";
        
    public static final String systemOsName   = (System.getProperty("os.name") != null) ? System.getProperty("os.name").toLowerCase() : "";

    public static final boolean isLinux       = (systemOsName.indexOf("linux")   >= 0);   
    public static final boolean isWindows     = (systemOsName.indexOf("windows") >= 0);
    
    public static final String PDF_WORK_CONF_DIR = !isWindows ? PDF_WORK_LOC_LINUX : PDF_WORK_LOC_WIN; 
    
    public static final String _SEP_ = File.separator;

    
    /**
     * Constructor, set the default shell and display.
     */
    public LightHtmlPDFWinAdapters() {
        this.display = new Display();
        this.shell   = new Shell(this.display, SWT.CLOSE | SWT.MIN);
    }
    
    /**
     * Constructor, set the default shell and display.
     * @param inShell
     */
    public LightHtmlPDFWinAdapters(final Shell inShell) {
        this.shell   = new Shell(inShell, SWT.CLOSE | SWT.MIN);
        this.display = this.shell.getDisplay();
    }

    /**
     * Create simple error box message.
     * 
     * @param shell
     * @param msg
     * @return
     */
    protected static final MessageBox createErrorMsgBox(final Shell shell, final String msg) {
         MessageBox messageBox = new MessageBox(shell, SWT.ICON_ERROR);
         messageBox.setText("Error");
         messageBox.setMessage("An error has occurred.\n" + msg);
         int buttonID = messageBox.open();
         return messageBox;
    }

    /**
     * Create the about box.
     * 
     * @param shell
     * @return
     */
    protected static final MessageBox createAboutBox(final Shell shell) {
         MessageBox messageBox = new MessageBox(shell, SWT.ICON_INFORMATION);
         messageBox.setText("About XHTML to PDF Converter");
         messageBox.setMessage(LightHtmlPDFWinTemplates._ABOUT_MSG_TEMPL_.toString());
         int buttonID = messageBox.open();
         return messageBox;
    }
    
    protected final MessageBox createErrorMsgBox(final String msg) {
         return createErrorMsgBox(shell, msg);
    }
    private final boolean checkInputOutputPath(final Shell shell, final String inputPath, final String outputPath, final String dirpath) {

        // Do a small check to see if the file exists.
        final File inputFile = new File(inputPath);
        if (!inputFile.exists()) {
            createErrorMsgBox(shell, "<checkInputOutputPath> ((" + inputPath + ")) does not exist.  Check the input file path.\nUse the 'Open File' dialog to set the input file path");
            return false;
        }
        final File outputFileDir = new File(dirpath);
        if (!outputFileDir.canWrite()) {
            createErrorMsgBox(shell, "<checkInputOutputPath> ((" + outputFileDir.getAbsolutePath()
                        + ")) cannot write to directory. Check the output PDF file path and working directory.\nUse the 'Open File' dialog to set the input file path."
                        + " perm=" + outputFileDir.canWrite());
            return false;
        }
        return true;
    }


    protected final SelectionListener createPDFSelectListenerWParse() {
        return new SelectionListener() {

            private void handlePushButton(SelectionEvent e) {
                System.out.println("<INFO> <createPDFSelectListenerNoParse> Running renderer.");
                display.asyncExec(createPDFProcessWithParse());
            }

          public void widgetSelected(SelectionEvent e) {
              try {
                  this.handlePushButton(e);
              } catch (Exception _e) {
                createErrorMsgBox(shell, "Error with createPDF parse. Err: " + _e.getMessage());
              }
          }

          public void widgetDefaultSelected(SelectionEvent e) {
              try {
                  this.handlePushButton(e);
              } catch (Exception _e) {
                createErrorMsgBox(shell, "Error with createPDF parse. Err: " + _e.getMessage());
              }

          }
        }; // End of new listener //
    }

    protected final SelectionListener createPDFSelectListenerNoParse() {
        return new SelectionListener() {

            private void handlePushButton(SelectionEvent e) {
                System.out.println("<INFO> <createPDFSelectListenerNoParse> Running renderer.");
                display.asyncExec(createPDFProcessNoParse());
            }

            public void widgetSelected(SelectionEvent e) {
                try {
                      this.handlePushButton(e);
                  } catch (Exception _e) {
                    createErrorMsgBox(shell, "Error <createPDFSelectListener>: \n" + _e.getMessage());
                  }
            }

            public void widgetDefaultSelected(SelectionEvent e) {
                try {
                      this.handlePushButton(e);
                  } catch( Exception _e) {
                    createErrorMsgBox(shell, "Error <createPDFSelectListener>: \n" + _e.getMessage());
                  }

            }
        }; // End of new listener //
    }

    private final Runnable createPDFProcessNoParse() {
        
        final LightHtmlPDFWinProcess process = new LightHtmlPDFWinProcess(this) {

            public void runParseHtmlDocument() throws Exception {

                final String workDirTextStr = workDirText.getText();
                final String filenameTextStr = filenameText.getText();
                final String outputPDFTextStr = outputPDFText.getText();

                final String inputPath  = filenameTextStr;
                final String outputPath = workDirTextStr + File.separator + outputPDFTextStr;

                if (!checkInputOutputPath(shell, inputPath, outputPath, workDirTextStr)) {
                    return;
                }

                this.getWinAdapter().status("Loading input file...");
                final String doc = XHTMLRendererBase.loadFile(inputPath);

                this.getWinAdapter().status("Parsing document...");

                final XHTMLRendererBase base = new XHTMLRendererBase();
                XHTMLRendererBase.setXhtmlProperties();
                base.setKeyValue("SOME_APP_DATA", "TestData");
                base.setApplicationObject(null);
                long startTime1 = 0;
                long endTime1 = 0;
                try {
                    startTime1 = System.currentTimeMillis();
                    base.setHtmlContent(doc);
                    base.parseDocumentFile(outputPath,  null);
                    endTime1 = System.currentTimeMillis();

                    if ((new File(outputPath)).exists()) {
                        launchProcess(adobePathExe, outputPath);
                    } else {
                        throw new RuntimeException("Invalid PDF Document File, does not exist.");
                    }
                } catch (Exception e1) {
                    e1.printStackTrace();
                    throw new RuntimeException("\nErr: " + e1.getMessage());
                } finally {
                    this.getWinAdapter().status("parse document complete (process time= " + (startTime1 - endTime1) + " ms)");
                }
            }
        };

        // Create and start the thread.
        //(new Thread(process)).start();
        return process;
    }

    /////////////////////////////////////////////////////////////////
    // Create PDF Process Utilities
    /////////////////////////////////////////////////////////////////
    
    private final Runnable createPDFProcessWithParse() {
        
        final LightHtmlPDFWinProcess process = new LightHtmlPDFWinProcess(this) {

            public void runParseHtmlDocument() throws Exception {

                final String workDirTextStr = workDirText.getText();
                final String filenameTextStr = filenameText.getText();
                final String outputPDFTextStr = outputPDFText.getText();
                final String parser = parserText.getText();
                	
                final String inputPath  = filenameTextStr;
                final String outputPath = workDirTextStr + File.separator + outputPDFTextStr;

                /////////////////////////////////////////////////////
                
                if (!checkInputOutputPath(shell, inputPath, outputPath, workDirTextStr)) {
                    return;
                }

                this.getWinAdapter().status("Loading input file...");
                final String doc = XHTMLRendererBase.loadFile(inputPath);

                this.getWinAdapter().status("Parsing document...");
                final XHTMLRendererBase base = new XHTMLRendererBase();
                XHTMLRendererBase.setXhtmlProperties();
                base.setKeyValue("SB_AGENT_ID", "TestData");
                base.setApplicationObject(null);
                long s_ = 0;
                long e_ = 0;
                try {
                    s_ = System.currentTimeMillis();
                    base.parseDocumentFile(outputPath, XHTMLParserFactory.create(parser, doc));
                    e_ = System.currentTimeMillis();

                    if ((new File(outputPath)).exists()) {
                        launchProcess(adobePathExe, outputPath);
                    } else {
                        throw new RuntimeException("Invalid PDF Document File, does not exist.");
                    } // End of if - else
                    
                } catch (NoClassDefFoundError e3) {
                    throw new RuntimeException("\n<ClassNotFound> [" + parser + "]:\n " + e3.getMessage() + _CLASS_NOT_FOUND_TEMPL_);
                } catch (ClassNotFoundException e2) {
                    throw new RuntimeException("\n<ClassNotFound> [" + parser + "]:\n " + e2.getMessage() + _CLASS_NOT_FOUND_TEMPL_);
                } catch (Exception e1) {
                    e1.printStackTrace();
                    throw new RuntimeException("\nErr: " + e1.getMessage());
                } finally {
                    this.getWinAdapter().status("parse document complete (process time= " + (e_ - s_) + " ms)");
                }
            }
        };

        // Create and start the thread.
        //(new Thread(process)).start();
        return process;
    }

    /**
     * Core Event Wait Loop.
     */
    public void winLoop() {

        if (shell == null) {
            return;
        }

        if (display == null) {
            return;
        }
        while (!shell.isDisposed()) {
            if (!display.readAndDispatch())
                display.sleep();
        }
        display.dispose();
    }
    
    /////////////////////////////////////////////////////////////////
    // Additional PDF Dialog Utiltiies
    /////////////////////////////////////////////////////////////////
    
    /**
     * @param filenameText the filenameText to set
     */
    public final void setFilenameText() {
        this.filenameText = new Text(shell, SWT.BORDER);
        this.filenameText.setLayoutData(GRID_DATA_TEXT);
    }

    /**
     * Set the output PDF text.
     */
    public final void setOutputPDFText() {
        this.outputPDFText = new Text(shell, SWT.BORDER);
        this.outputPDFText.setLayoutData(GRID_DATA_TEXT);
    }

    /**
     * Set the parser text.
     */
    public final void setParserText() {
        this.parserText = new Text(shell, SWT.BORDER);
        this.parserText.setLayoutData(GRID_DATA_TEXT);
    }

    /**
     * Set the working directory text.
     */
    public final void setWorkDirText() {
        this.workDirText = new Text(shell, SWT.BORDER);
        this.workDirText.setLayoutData(GRID_DATA_TEXT);
    }

    /**
     * Create an instance of a status bar label.  This should be create after the button and edit fields
     * are created.
     */
    public final void setStatusBar() {
        this.statusBar = new Text(shell, SWT.NONE);
        this.statusBar.setLayoutData(GRID_DATA_LABEL_FULL);
        GRID_DATA_LABEL_FULL.grabExcessHorizontalSpace = true;
        GRID_DATA_LABEL_FULL.widthHint = 800;
        status("[os=" + systemOsName + "]");
    }

    /**
     * Set the status.
     * @param msg
     */
    public final void status(final String msg) {
        final String curMsg = ("HtmlPDF Creator - " + msg + " - " + LightHtmlPDFWinTemplates.memory_usage());
        System.out.println(curMsg);
        this.statusBar.setText(curMsg);

    }

    /////////////////////////////////////////////////////////////////
    // Bean Utilities
    /////////////////////////////////////////////////////////////////
    
    /**
     * Return working directory text. 
     */
    public final Text getWorkDirText() {
        return workDirText;
    }

    /** 
     * Get the filename text.
     * @return
     */
    public final Text getFilenameText() {
        return filenameText;
    }

    /**
     * Get the output PDF text.
     * @return
     */
    public final Text getOutputPDFText() {
        return outputPDFText;
    }

    /**
     * Get the parser text.
     * @return
     */
    public final Text getParserText() {
        return parserText;
    }

    /**
     * Get the shell.
     * @return
     */
    public final Shell getShell() {
        return shell;
    }

    /**
     * Get the display.
     * @return
     */
    public final Display getDisplay() {
        return display;
    }
    
    /////////////////////////////////////////////////////////////////
    // Simple File Utilities
    /////////////////////////////////////////////////////////////////

    /**
     * Write document to file.
     * @return
     */
    public static final boolean writeFile(final String outputpath, final String dir) {
    	
        final File file     = new File(outputpath);
        final File file_dir = new File(dir);
        if (!file.exists() && file_dir.canWrite()) {
            FileOutputStream fos = null;
            try {
                fos = new FileOutputStream(outputpath);

                final BufferedOutputStream bos = new BufferedOutputStream(fos);
                final PrintWriter pw = new PrintWriter(bos);
                pw.println(LightHtmlPDFWinTemplates._EXAMPLE_HTML_TEMPL_);
                pw.flush();
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } finally {
                if (fos != null) {
                    try {
                        fos.close();
                    } catch (IOException e) { }
                }
            } // End of try catch
        } else {
            System.out.println("INFO: Example HTML file exists or issue with writing, not creating... path=" + outputpath);
        }
        return false;
    }
    
    /////////////////////////////////////////////////////////////////
    // Simple Process Utilities
    /////////////////////////////////////////////////////////////////
    
    /**
     * Set the adobe exe path.
     */
    public void setAdobePath() {
        this.adobePathExe = LightHtmlPDFUtil.findAdobeReader();
    }

    public static final void launchProcess(final String adobePath, final String filename) {
        if (adobePath != null) {
            Runtime runtime = Runtime.getRuntime();
            final String proc_str = "\"" + adobePath + "\" " + "\"" +  filename + "\"";
            try {
                Process process = runtime.exec(proc_str);
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            System.out.println("Could not find Adobe Exec Path. Not opening reader.");
        }
    }  

} // End of the Class

/////////////////////////////////////////////////
// End of File
/////////////////////////////////////////////////
