/**
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
 * Light Log was developed with a combination of Clojure 1.0, Java and Scala with use of libs, 
 * SWT 3.4, JFreeChart, iText. 
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
 * Additional Development Notes: The SWT gui and other libraries are launched from a dynamic classloader.  
 * Clojure is also started from the same code, and reflection is used to dynamically initiate Clojure. 
 * See the 'start' package.  The binary
 * code is contained in the octane_start.jar library.
 *   
 * Home Page: http://code.google.com/p/lighttexteditor/
 * 
 * Contact: Berlin Brown <berlin dot brown at gmail.com>
 */

package com.toolkit.util.gui;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.MalformedURLException;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

import com.octane.util.StringUtils;
import com.toolkit.util.pdf.XHTMLParserFactory;
import com.toolkit.util.pdf.XHTMLRendererBase;

import org.berlin.light.toolkit.TemplateParseFileLine;

/**
 * Simple HTML to PDF converter.
 *
 * @author Berlin Brown
 * @version 1.0 on 2/1/2009
 */
public abstract class HtmlPDFWinAdapters extends XHtmlPDFWinTemplates {
	
    public static final String DEFAULT_PARSER = "com.toolkit.util.pdf.filter.Parser";
	
	/*
	 * @param horizontalAlignment how control will be positioned horizontally within a cell,
	 * 		one of: SWT.BEGINNING (or SWT.LEFT), SWT.CENTER, SWT.END (or SWT.RIGHT), or SWT.FILL
	 * @param verticalAlignment how control will be positioned vertically within a cell,
	 * 		one of: SWT.BEGINNING (or SWT.TOP), SWT.CENTER, SWT.END (or SWT.BOTTOM), or SWT.FILL
	 * @param grabExcessHorizontalSpace whether cell will be made wide enough to fit the remaining horizontal space
	 * @param grabExcessVerticalSpace whether cell will be made high enough to fit the remaining vertical space
	 * @param horizontalSpan the number of column cells that the control will take up
	 * @param verticalSpan the number of row cells that the control will take up
	 */
	private final GridData GRID_DATA_TEXT  = new GridData(SWT.FILL, SWT.NONE, true, false, 1, 1);	
	private final GridData GRID_DATA_LABEL = new GridData(SWT.NONE, SWT.NONE, false, false, 1, 1);
	
	private final GridData GRID_DATA_LABEL_FULL = new GridData(SWT.NONE, SWT.NONE, true, false, 2, 1);
	
	private final Display display;	
	private String adobePathExe;
	
	private int lastErrStatus = -1;
	
	/**
	 * Example Styles:
	 * 
	 * BORDER, CLOSE, MIN, MAX, NO_TRIM, RESIZE, TITLE, ON_TOP, TOOL
     * APPLICATION_MODAL, MODELESS, PRIMARY_MODAL, SYSTEM_MODAL
	 */
	private final Shell shell;
	private Text statusBar;	
	private Text workDirText;
	private Text filenameText;
	private Text outputPDFText;
	private Text stylesRootText;
	
	/**
	 * Set external clojure environment settings.
	 */
    private WinPDFSettings envSettings;
	
    /**
     * Constructor for XHtmlPDFWinAdapters.
     */
    public HtmlPDFWinAdapters() {        
        this.display = new Display();
        this.shell   = new Shell(this.display, SWT.CLOSE | SWT.MIN);
    }
    /**
     * Constructor for XHtmlPDFWinAdapters.
     * @param inShell Shell
     */
    public HtmlPDFWinAdapters(final Shell inShell) {        
        this.shell   = new Shell(inShell, SWT.CLOSE | SWT.MIN);
        this.display = this.shell.getDisplay();
    }
    
	/**
	 * Implementation Routine createErrorMsgBox.
	 * @param shell Shell
	 * @param msg String
	 * @return MessageBox
	 */
	protected static final MessageBox createErrorMsgBox(final Shell shell, final String msg) {
		
	     MessageBox messageBox = new MessageBox(shell, SWT.ICON_ERROR);	        
	     messageBox.setText("Error");
	     messageBox.setMessage("An error has occurred.\n" + msg);
	     int buttonID = messageBox.open();
	     return messageBox;
	}
	
	/**
	 * Implementation Routine createAboutBox.
	 * @param shell Shell
	 * @return MessageBox
	 */
	protected static final MessageBox createAboutBox(final Shell shell) {
	     MessageBox messageBox = new MessageBox(shell, SWT.ICON_INFORMATION);	        
	     messageBox.setText("About XHTML/HTML Quick Converter");
	     messageBox.setMessage(XHtmlPDFWinTemplates._ABOUT_MSG_TEMPL_.toString());
	     int buttonID = messageBox.open();
	     return messageBox;
	}
	/**
	 * Implementation Routine createErrorMsgBox.
	 * @param msg String
	 * @return MessageBox
	 */
	protected MessageBox createErrorMsgBox(final String msg) {
	     return createErrorMsgBox(shell, msg);
	}
	
	 public void setEnvSettings(final WinPDFSettings envSettings) {
		 this.envSettings = envSettings;
	 }
	
	/**
	 * Implementation Routine checkInputOutputPath.
	 * @param shell Shell
	 * @param inputPath String
	 * @param outputPath String
	 * @param dirpath String
	 * @return boolean
	 */
	private boolean checkInputOutputPath(final Shell shell, final String inputPath, final String outputPath, final String dirpath) {
		
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
	
	
	/**
	 * Implementation Routine createPDFSelectListenerWParse.
	 * @return SelectionListener
	 */
	public SelectionListener createPDFSelectListenerWParse() { 
		return new SelectionListener() {
			
			private void handlePushButton(SelectionEvent e) {
				System.out.println("<INFO> <createPDFSelectListenerNoParse> Running renderer.");
				display.asyncExec(createPDFProcessWithParse());								
			}
			
	      public void widgetSelected(SelectionEvent e) {
	    	  try {
	    		  this.handlePushButton(e);
	    	  } catch(Exception _e) {
				createErrorMsgBox(shell, "Error with createPDF parse. Err: " + _e.getMessage());
	    	  }
	      }

	      public void widgetDefaultSelected(SelectionEvent e) {
	    	  try {
	    		  this.handlePushButton(e);
	    	  } catch(Exception _e) {
				createErrorMsgBox(shell, "Error with createPDF parse. Err: " + _e.getMessage());
	    	  }
			
	      }
		}; // End of new listener //
	}
	
	/**
	 * Implementation Routine createPDFSelectListenerNoParse.
	 * @return SelectionListener
	 */
	public SelectionListener createPDFSelectListenerBuffer() {
		
		return new SelectionListener() {
			
			private void handlePushButton(SelectionEvent e) {
				System.out.println("<INFO> <createPDFSelectListenerNoParse> creating document from main buffer");
				display.asyncExec(createPDFProcessFromBuffer());
			}
			
			public void widgetSelected(SelectionEvent e) {
				try {
		    		  this.handlePushButton(e);
		    	  } catch(Exception _e) {
					createErrorMsgBox(shell, "Error <createPDFSelectListener>: \n" + _e.getMessage());
		    	  }
			}

			public void widgetDefaultSelected(SelectionEvent e) {
				try {
		    		  this.handlePushButton(e);
		    	  } catch(Exception _e) {
					createErrorMsgBox(shell, "Error <createPDFSelectListener>: \n" + _e.getMessage());
		    	  }
	    
			}
		}; // End of new listener //
	}
	
	/**
	 * Implementation Routine createPDFProcessNoParse.
	 * 
	 * @deprecated
	 * @return Runnable
	 */
	private Runnable createPDFProcessNoParse() {		
		return null;
	}
	
	public String getExternalEnvText(final WinPDFSettings envSettings) {
		
		if (envSettings == null) {
			lastErrStatus = 10001;
			return "";
		}
		
		if (!envSettings.hasStyledText()) {
			lastErrStatus = 10002;
			return "";
		}
		final String doc = envSettings.getMainBufferText();		
		return doc;
	}
	
	
	/**
	 * Environment settings should be not null.
	 * @param envSettings
	 * @return
	 */
	private Runnable createPDFProcessFromBuffer() {
		
		final XHtmlPDFWinProcess process = new XHtmlPDFWinProcess(this) {

			public void runParseHtmlDocument() throws Exception {

				if (envSettings == null) {
					System.out.println("<HtmlPDFWinAdapters 351> Invalid environment settings");
					return;
				}
				
				final String workDirTextStr = workDirText.getText(); 
				final String filenameTextStr = filenameText.getText();
				final String outputPDFTextStr = outputPDFText.getText();
				
				final String inputPath  = filenameTextStr;
				final String outputPath = workDirTextStr + File.separator + outputPDFTextStr;
				
				if (!checkInputOutputPath(shell, inputPath, outputPath, workDirTextStr)) {
					return;
				}
				
				this.getWinAdapter().status("Loading input file...");
				final String doc = HtmlPDFWinAdapters.this.getExternalEnvText(envSettings);				
				this.getWinAdapter().status("Parsing document...");
				
				parseConvertDocument(this, doc, outputPath, true);
			} 			
		}; /* End of Create Process */
		
		// Create and start the thread.
		//(new Thread(process)).start();
		return process;
		
	}
	
	/**
	 * Parse and convert to PDF the document.
	 * 
	 * @param process
	 * @param doc
	 * @param outputPath
	 * @param hasLaunchProcess
	 */
	public void parseConvertDocument(final XHtmlPDFWinProcess process, final String doc, final String outputPath, final boolean hasLaunchProcess) {
		
	    final TemplateParseFileLine parser = new TemplateParseFileLine(); 
	    
	    final String stylesRoot = stylesRootText.getText();
	    final String forBasePath = parser.findTemplateLightHomeToExternal(stylesRoot);
	    final boolean isPathEmpty = StringUtils.isEmpty(forBasePath) || ".".equals(forBasePath);
	    String curWorkDir = "";
	    System.out.println("<TRACE> styles root input : " + stylesRoot);
	    
	    if (isPathEmpty) {
	        try {
	            curWorkDir = new File(".").toURI().toURL().toExternalForm();
	        } catch(MalformedURLException me) {	        
	        } // End of try - catch //
	    } // End of if path empty
	    
		final String workDir = isPathEmpty ? curWorkDir : forBasePath;
		System.out.println("<TRACE> working resource directory : " + forBasePath);		
		
		process.getWinAdapter().status("Parsing document... ");				
		final XHTMLRendererBase base = new XHTMLRendererBase();								
		XHTMLRendererBase.setXhtmlProperties();		
		base.setKeyValue("SOME_DATA", "TestData");
		base.setApplicationObject(null);
		
		long sTime = 0;
		long eTime = 0;
		try {
			sTime = System.currentTimeMillis();
			base.parseDocumentFile(outputPath, XHTMLParserFactory.create(DEFAULT_PARSER, doc), workDir);
			eTime = System.currentTimeMillis();
			
			if ((new File(outputPath)).exists()) {
				
				if (hasLaunchProcess) {
					launchProcess(adobePathExe, outputPath);
				} // End of the if //
				
			} else {
				throw new RuntimeException("Invalid PDF Document File, does not exist.");
			} // End of the if, file exist //
					
		} catch (Exception e1) {
			e1.printStackTrace();
			throw new RuntimeException("\nErr: " + e1.getMessage());					
		} finally {
			process.getWinAdapter().status("parse document complete (process time= " + (eTime - sTime) + " ms)");
		} // End of the finally //
		
	}
		
	/**
	 * Implementation Routine createPDFProcessWithParse.
	 * @return Runnable
	 */
	private Runnable createPDFProcessWithParse() {
		
		final XHtmlPDFWinProcess process = new XHtmlPDFWinProcess(this) {

			public void runParseHtmlDocument() throws Exception {

				final String workDirTextStr   = workDirText.getText(); 
				final String filenameTextStr  = filenameText.getText();
				final String outputPDFTextStr = outputPDFText.getText();
								
				final String inputPath  = filenameTextStr;
				final String outputPath = workDirTextStr + File.separator + outputPDFTextStr;
				
				if (!checkInputOutputPath(shell, inputPath, outputPath, workDirTextStr)) {
					return;
				}
				
				this.getWinAdapter().status("Loading input file...");
				final String doc = XHTMLRendererBase.loadFile(inputPath);				
				parseConvertDocument(this, doc, outputPath, true);
				
			} // End of the Method // 
			
		};  /* End of Process */
		
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

	/**
	 * Implementation Routine getWorkDirText.
	 * @return Text
	 */
	public Text getWorkDirText() {
		return workDirText;
	}

	/**
	 * Implementation Routine getFilenameText.
	 * @return Text
	 */
	public Text getFilenameText() {
		return filenameText;
	}

	/**
	 * Implementation Routine getOutputPDFText.
	 * @return Text
	 */
	public Text getOutputPDFText() {
		return outputPDFText;
	}

	/**
	 * Implementation Routine getParserText.
	 * @return Text
	 */
	public Text getStylesRootText() {
		return stylesRootText;
	}

	/**
	 * Implementation Routine getShell.
	 * @return Shell
	 */
	public Shell getShell() {
		return shell;
	}

	/**
	 * Implementation Routine getDisplay.
	 * @return Display
	 */
	public Display getDisplay() {
		return display;
	}

	/**
	 */
	public void setFilenameText() {
		this.filenameText = new Text(shell, SWT.BORDER);
		this.filenameText.setLayoutData(GRID_DATA_TEXT);
	}

	/**
	 */
	public void setOutputPDFText() {
		this.outputPDFText = new Text(shell, SWT.BORDER);
		this.outputPDFText.setLayoutData(GRID_DATA_TEXT);
	}

	/**
	 */
	public void setStylesRootTextWidget() {
		this.stylesRootText = new Text(shell, SWT.BORDER);
		this.stylesRootText.setLayoutData(GRID_DATA_TEXT);
	}

	/**
	 */
	public void setWorkDirText() {
		this.workDirText = new Text(shell, SWT.BORDER);
		this.workDirText.setLayoutData(GRID_DATA_TEXT);
	}
	
	/**
	 * Create an instance of a status bar label.  This should be create after the button and edit fields
	 * are created.
	 */
	public void setStatusBar() {
		this.statusBar = new Text(shell, SWT.NONE);
		this.statusBar.setLayoutData(GRID_DATA_LABEL_FULL);
		GRID_DATA_LABEL_FULL.grabExcessHorizontalSpace = true;
		GRID_DATA_LABEL_FULL.widthHint = 800;		
		status("");
	}
		
	/**
	 * Implementation Routine status.
	 * @param msg String
	 */
	public final void status(final String msg) {
		final String msg_ = ("XHtmlPDF Creator - " + msg + " - " + XHtmlPDFWinTemplates.memory_usage());
		System.out.println(msg_);
		this.statusBar.setText(msg_);
		
	}
	
	/////////////////////////////////////////////////////////////////
	// Simple Process Utilities
	/////////////////////////////////////////////////////////////////
	/**
	 * Implementation Routine setAdobePath.
	 */
	public void setAdobePath() {
		
		final XHtmlPDFUtil pdfUtil = new XHtmlPDFUtil();
		this.adobePathExe = pdfUtil.findAdobeReader();
	}
		
	/**
	 * Implementation Routine launchProcess.
	 * @param adobePath String
	 * @param filename String
	 */
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
	
	/////////////////////////////////////////////////////////////////
	// Simple File Utilities
	/////////////////////////////////////////////////////////////////
	/**
	 * Implementation Routine writeFile.
	 * @param outputpath String
	 * @param dir String
	 * @return boolean
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
				pw.println(XHtmlPDFWinTemplates._EXAMPLE_HTML_TEMPL_);
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

} // End of the Class

