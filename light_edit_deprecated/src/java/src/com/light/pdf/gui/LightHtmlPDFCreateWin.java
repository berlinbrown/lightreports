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

import java.io.File;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.events.ShellAdapter;
import org.eclipse.swt.events.ShellEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;

/**
 * Simple HTML to PDF converter.
 */
public class LightHtmlPDFCreateWin extends LightHtmlPDFWinAdapters {

    /**
     * Main SWT shell.
     */
    private final Shell shell = this.getShell();
    
    /**
     * Main SWT display.
     */
    private final Display display = this.getDisplay();
    
    /**
     * File Dialog for opening the input HTML file.
     */
    private final FileDialog fileDialog = new FileDialog(shell, SWT.CLOSE);

    /**
     * Grid Data for Text.
     */
    private final GridData GRID_DATA_TEXT  = new GridData(SWT.FILL, SWT.NONE, true, false, 1, 1);
    
    /**
     * Grid Data for Label.
     */
    private final GridData GRID_DATA_LABEL = new GridData(SWT.NONE, SWT.NONE, false, false, 1, 1);

    /**
     * PDF Dialog Width.
     */
    public static final int DIALOG_PDF_WIDTH   = 540;
    
    /**
     * PDF Dialog Height.
     */
    public static final int DIALOG_PDF_HEIGHT  = 220;
        
    /**
     * Dialog PDF Location.
     */
    public static final int DIALOG_PDF_LOCX    = 300;
    
    /**
     * Dialog PDF Location.
     */
    public static final int DIALOG_PDF_LOCY    = 200;
    
    public static final int DIALOG_BUTTON_WIDTH     = 160;
    public static final int DIALOG_BUTTON_HEIGHT    = 32;
    
   
    /**
     * Default Constructor.
     */
    public LightHtmlPDFCreateWin() {
        super();
    }

    /**
     * Constructor, pass an input shell instance.
     * @param inShell
     */
    public LightHtmlPDFCreateWin(final Shell inShell) {
        super(inShell);
    }
    
    /**
     * Create Win instance.
     * @return
     */
    public static final LightHtmlPDFCreateWin create() {
        LightHtmlPDFCreateWin win = new LightHtmlPDFCreateWin();
        return win;
    }

    /**
     * Create win instance with an input shell.
     * 
     * @param inShell
     * @return
     */
    public static final LightHtmlPDFCreateWin create(final Shell inShell) {
        LightHtmlPDFCreateWin win = new LightHtmlPDFCreateWin(inShell);
        return win;
    }

    /**
     * Create a tmp working directory
     */
    private final void createTmpDirectory() {
        final File f = new File(PDF_WORK_CONF_DIR);
        try {
            f.mkdirs();
        } catch(Exception e) {
            e.printStackTrace();
        }

        // Put a tmp HTML file in the working directory
        writeFile(PDF_WORK_CONF_DIR + _SEP_  + "simple_doc.html", f.getAbsolutePath());
    }
    
    public void dialogOpenFile() {
        fileDialog.setText("Open File");
        fileDialog.setFilterExtensions(new String [] { "*.*", "*.html", "*.xhtml" });

        final String filename = fileDialog.open();
        if (filename != null) {
            final File f = new File(filename);
            if (!f.exists()) {
                throw new RuntimeException("Invalid File, does not exist: \n" + f.getAbsolutePath());
            }
            this.getFilenameText().setText(f.getAbsolutePath());
            status("Opened file " + f.getAbsolutePath());
        }
    }

    /**
     * Add the additional widgets.
     */
    public void addWidgets() {

        ////////////////////////
        // Row 1
        // Working directory
        ////////////////////////
        final Label label_dir = new Label(shell, SWT.NONE);
        label_dir.setLayoutData(GRID_DATA_TEXT);
        label_dir.setText("PDF Working Directory (dirpath):");
        label_dir.setLayoutData(GRID_DATA_LABEL);
        this.setWorkDirText();
        this.getWorkDirText().setText(PDF_WORK_CONF_DIR);

        ////////////////////////
        // Row 2
        // Filename
        ////////////////////////
        final Label label_fname = new Label(shell, SWT.NONE);
        label_fname.setText("HTML Input Filename (filename.ext):");
        label_dir.setLayoutData(GRID_DATA_LABEL);
        this.setFilenameText();
        this.getFilenameText().setText(PDF_WORK_CONF_DIR + _SEP_ + "simple_doc.html");

        ////////////////////////
        // Row 3
        // Output PDF filename
        ////////////////////////
        final Label label_outputpdf = new Label(shell, SWT.NONE);
        label_outputpdf.setText("Output PDF Filename (filename.ext):");
        label_dir.setLayoutData(GRID_DATA_LABEL);
        this.setOutputPDFText();
        this.getOutputPDFText().setText("xhtml_generated_pdf.pdf");

        ////////////////////////
        // Row 4
        // Classname parser
        ////////////////////////
        final Label label_parserclass = new Label(shell, SWT.NONE);
        label_parserclass.setText("Parser Class:");
        label_parserclass.setLayoutData(GRID_DATA_LABEL);
        this.setParserText();
        this.getParserText().setText("SimpleParse");

        this.createPDFButton();

        // Now position the status label bar.
        this.setStatusBar();
    }

    /**
     * Create the four PDF buttons.
     *
     * @return
     */
    public Button createPDFButton() {
        
        // Set the grid data width hint
        final GridData GRID_DATA_BUTTON = new GridData();
        final Button button = new Button(shell, SWT.PUSH);
        button.setText("Create PDF");
        GRID_DATA_BUTTON.widthHint   = DIALOG_BUTTON_WIDTH;
        GRID_DATA_BUTTON.heightHint  = DIALOG_BUTTON_HEIGHT;
        button.setLayoutData(GRID_DATA_BUTTON);
        button.addSelectionListener(createPDFSelectListenerNoParse());

        ////////////////////////////////////
        // Open Button and Usage
        ////////////////////////////////////
        final Button button_open = new Button(shell, SWT.PUSH);
        button_open.setText("Open HTML File");
        // Set the button to span 2 columns and at a width of 300 px
        GRID_DATA_BUTTON.widthHint   = DIALOG_BUTTON_WIDTH;
        GRID_DATA_BUTTON.heightHint  = DIALOG_BUTTON_HEIGHT;
        button_open.setLayoutData(GRID_DATA_BUTTON);
        button_open.addSelectionListener(new SelectionListener() {
            public void widgetSelected(SelectionEvent e) {
                System.out.println("Attempting open file");
                try {
                    dialogOpenFile();
                } catch (Exception _e) {
                    createErrorMsgBox(shell, "Error opening file: \n" + _e.getMessage());
                }
            }
            public void widgetDefaultSelected(SelectionEvent e) {
                System.out.println("Attempting open file");
                try {
                    dialogOpenFile();
                } catch (Exception _e) {
                    createErrorMsgBox(shell, "Error opening file: \n" + _e.getMessage());
                }
            }
        }); // End of new listener //
        
        // Add the listener for the window
        if (this.shell != null) {
            this.shell.addShellListener(new ShellAdapter() {
                public void shellClosed(ShellEvent event) {
                    System.out.println("INFO: closing shell xhtml to pdf window. shell=" + shell);
                    event.doit = false;
                    shell.setVisible(false);
                }
            });

        } // End of the If

        return button;
    }
    
    ///////////////////////////////////////////////////////////////
    // Main Create Window Method.
    /////////////////////////////////////////////////////////////////
    
    /**
     * Create the PDF dialog box.
     * 
     * @return
     */
    public LightHtmlPDFCreateWin createShellWin() {

        try {
            createTmpDirectory();
        } catch (Exception e) {
            e.printStackTrace();
        }
        setAdobePath();

        shell.setSize(DIALOG_PDF_WIDTH, DIALOG_PDF_HEIGHT);
        shell.setLocation(DIALOG_PDF_LOCX, DIALOG_PDF_LOCY);

        final GridLayout layout = new GridLayout();
        layout.numColumns = 2;
        shell.setLayout(layout);
        this.addWidgets();

        shell.setText("Convert HTML Document to PDF");
        shell.open();
        return this;
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

    ///////////////////////////////////////////////////////////////
    // Main Entry Point.
    /////////////////////////////////////////////////////////////////
    
    /**
     * Main Entry Point.
     *
     * @param args
     */
    public static final void main(String [] args) {
        createPDFWindow();
    }

    /**
     * Create PDF Window.
     */
    public static final void createPDFWindow() {
        System.out.println("Launching XHTML to PDF window");
        (create()).createShellWin().winLoop();
    }

    /**
     * Create PDF Win Shell.
     * @param shell
     */
    public static final void createPDFWindowShell(final Shell shell) {
        System.out.println("Launching XHTML to PDF window");
        (create(shell)).createShellWin().winLoop();
    }

} // End of the Class

/////////////////////////////////////////////////
// End of File
/////////////////////////////////////////////////
