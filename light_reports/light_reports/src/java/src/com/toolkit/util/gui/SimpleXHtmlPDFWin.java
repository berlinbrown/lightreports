/**
 * Copyright (c) 2006-2010 Berlin Brown and botnode.com All Rights Reserved
 *
 * <p>http://www.opensource.org/licenses/bsd-license.php
 *
 * <p>All rights reserved.
 *
 * <p>Redistribution and use in source and binary forms, with or without modification, are permitted
 * provided that the following conditions are met:
 *
 * <p>* Redistributions of source code must retain the above copyright notice, this list of
 * conditions and the following disclaimer. * Redistributions in binary form must reproduce the
 * above copyright notice, this list of conditions and the following disclaimer in the documentation
 * and/or other materials provided with the distribution. * Neither the name of the Botnode.com
 * (Berlin Brown) nor the names of its contributors may be used to endorse or promote products
 * derived from this software without specific prior written permission.
 *
 * <p>THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND ANY EXPRESS OR
 * IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND
 * FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR
 * CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL
 * DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE,
 * DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY,
 * WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY
 * WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 *
 * <p>Date: 1/5/2009, updated 5/5/2010 7/15/2009 - Added Clojure 1.0, other performance fixes and
 * cleanups.
 *
 * <p>Main Description: Light Log Viewer is a tool for making it easier to search log files. Light
 * Log Viewer adds some text highlighting, quick key navigation to text files, simple graphs and
 * charts for monitoring logs, file database to quickly navigate to files of interest, and HTML to
 * PDF convert tool. Light Log was developed with a combination of Clojure 1.0, Java and Scala with
 * use of libs, SWT 3.4, JFreeChart, iText.
 *
 * <p>Quickstart : the best way to run the Light Log viewer is to click on the win32 batch script
 * light_logs.bat (you may need to edit the Linux script for Unix/Linux environments). Edit the
 * win32 script to add more heap memory or other parameters.
 *
 * <p>The clojure source is contained in : HOME/src/octane The java source is contained in :
 * HOME/src/java/src
 *
 * <p>To build the java source, see : HOME/src/java/build.xml and build_pdf_gui.xml
 *
 * <p>Additional Development Notes: The SWT gui and other libraries are launched from a dynamic
 * classloader. Clojure is also started from the same code, and reflection is used to dynamically
 * initiate Clojure. See the 'start' package. The binary code is contained in the octane_start.jar
 * library.
 *
 * <p>Home Page: http://code.google.com/p/lighttexteditor/
 *
 * <p>Contact: Berlin Brown <berlin dot brown at gmail.com>
 */
package com.toolkit.util.gui;

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
 *
 * @author Berlin Brown
 * @version 1.0 on 2/1/2009
 */
public class SimpleXHtmlPDFWin extends HtmlPDFWinAdapters {

  /**
   * Example Styles:
   *
   * <p>BORDER, CLOSE, MIN, MAX, NO_TRIM, RESIZE, TITLE, ON_TOP, TOOL APPLICATION_MODAL, MODELESS,
   * PRIMARY_MODAL, SYSTEM_MODAL
   */
  private final Shell shell = this.getShell();

  private final Display display = this.getDisplay();
  private final FileDialog fileDialog = new FileDialog(shell, SWT.CLOSE);

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
  private final GridData GRID_DATA_TEXT = new GridData(SWT.FILL, SWT.NONE, true, false, 1, 1);
  private final GridData GRID_DATA_LABEL = new GridData(SWT.NONE, SWT.NONE, false, false, 1, 1);

  /** Constructor for SimpleXHtmlPDFWin. */
  public SimpleXHtmlPDFWin() {
    super();
  }

  /**
   * Constructor for SimpleXHtmlPDFWin.
   *
   * @param inShell Shell
   */
  public SimpleXHtmlPDFWin(final Shell inShell) {
    super(inShell);
  }

  /**
   * Implementation Routine create.
   *
   * @return SimpleXHtmlPDFWin
   */
  public static final SimpleXHtmlPDFWin create() {
    SimpleXHtmlPDFWin win = new SimpleXHtmlPDFWin();
    return win;
  }

  /**
   * Implementation Routine create.
   *
   * @param inShell Shell
   * @return SimpleXHtmlPDFWin
   */
  public static final SimpleXHtmlPDFWin create(final Shell inShell) {
    SimpleXHtmlPDFWin win = new SimpleXHtmlPDFWin(inShell);
    return win;
  }

  /** Create a tmp working directory */
  private final void createTmpDirectory() {
    final File f = new File("./conf/sys/_work");
    try {
      f.mkdirs();
    } catch (Exception e) {
      e.printStackTrace();
    }

    // Put a tmp HTML file in the working directory
    writeFile("./conf/sys/_work/pdf_simple_doc.html", f.getAbsolutePath());
  }

  /**
   * Implementation Routine createShellWin.
   *
   * @return SimpleXHtmlPDFWin
   */
  public SimpleXHtmlPDFWin createShellWin() {

    try {
      createTmpDirectory();
    } catch (Exception e) {
      e.printStackTrace();
    }
    setAdobePath();

    shell.setSize(510, 210);
    shell.setLocation(180, 250);

    final GridLayout layout = new GridLayout();
    layout.numColumns = 2;
    shell.setLayout(layout);
    this.addWidgets();

    shell.setText("Quick Convert HTML to PDF Tool");
    shell.open();
    return this;
  }

  /** Implementation Routine dialogOpenFile. */
  public void dialogOpenFile() {

    fileDialog.setText("Open File");
    fileDialog.setFilterExtensions(new String[] {"*.html", "*.xhtml", "*.xml", "*.*"});

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

  /** Implementation Routine addWidgets. */
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
    this.getWorkDirText().setText("./conf/sys/_work");

    ////////////////////////
    // Row 2
    // Filename
    ////////////////////////
    final Label label_fname = new Label(shell, SWT.NONE);
    label_fname.setText("HTML Input Filename (filename.ext):");
    label_fname.setLayoutData(GRID_DATA_LABEL);
    this.setFilenameText();
    this.getFilenameText().setText("./conf/sys/_work/pdf_simple_doc.html");

    ////////////////////////
    // Row 3
    // Output PDF filename
    ////////////////////////
    final Label label_outputpdf = new Label(shell, SWT.NONE);
    label_outputpdf.setText("Output PDF Filename (filename.ext):");
    label_outputpdf.setLayoutData(GRID_DATA_LABEL);
    this.setOutputPDFText();
    this.getOutputPDFText().setText("xhtml_generated_pdf.pdf");

    ////////////////////////
    // Row 4
    // Classname parser
    ////////////////////////
    final Label label_parserclass = new Label(shell, SWT.NONE);
    label_parserclass.setText("Html Resources Base Path:");
    label_parserclass.setLayoutData(GRID_DATA_LABEL);
    this.setStylesRootTextWidget();
    this.getStylesRootText().setText("%LIGHT_HOME%");
    this.createPDFButton();

    // Now position the status label bar.
    this.setStatusBar();
  }

  /**
   * Create the four PDF buttons.
   *
   * @return Button
   */
  public Button createPDFButton() {

    // Set the grid data width hint
    final GridData GRID_DATA_BUTTON = new GridData();

    ////////////////////////////////////
    // Create second button with parser
    ////////////////////////////////////
    final Button button_withparser = new Button(shell, SWT.PUSH);
    button_withparser.setText("Create PDF (w/ parser)");
    // Set the button to span 2 columns and at a width of 300 px
    GRID_DATA_BUTTON.widthHint = 140;
    GRID_DATA_BUTTON.heightHint = 24;
    button_withparser.setLayoutData(GRID_DATA_BUTTON);
    button_withparser.addSelectionListener(
        this.createPDFSelectListenerWParse()); /* Set the handler */

    final Button button = new Button(shell, SWT.PUSH);
    button.setText("Create PDF (main buffer)");
    GRID_DATA_BUTTON.widthHint = 160;
    GRID_DATA_BUTTON.heightHint = 24;
    button.setLayoutData(GRID_DATA_BUTTON);
    button.addSelectionListener(this.createPDFSelectListenerBuffer()); /* Set the handler */

    ////////////////////////////////////
    // Open Button and Usage
    ////////////////////////////////////
    final Button button_open = new Button(shell, SWT.PUSH);
    button_open.setText("Open HTML File");
    // Set the button to span 2 columns and at a width of 300 px
    GRID_DATA_BUTTON.widthHint = 160;
    GRID_DATA_BUTTON.heightHint = 24;
    button_open.setLayoutData(GRID_DATA_BUTTON);
    button_open.addSelectionListener(
        new SelectionListener() {

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

    ////////////////////////////////////
    // About Button and Usage
    ////////////////////////////////////
    final Button button_about = new Button(shell, SWT.PUSH);
    button_about.setText("Help");
    // Set the button to span 2 columns and at a width of 300 px
    GRID_DATA_BUTTON.widthHint = 160;
    GRID_DATA_BUTTON.heightHint = 24;
    button_about.setLayoutData(GRID_DATA_BUTTON);
    button_about.addSelectionListener(
        new SelectionListener() {
          public void widgetDefaultSelected(SelectionEvent e) {
            createAboutBox(shell);
          }

          public void widgetSelected(SelectionEvent e) {
            createAboutBox(shell);
          }
        });

    // Add the listener for the window
    if (this.shell != null) {
      this.shell.addShellListener(
          new ShellAdapter() {
            public void shellClosed(ShellEvent event) {
              System.out.println("INFO: closing shell xhtml to pdf window. shell=" + shell);
              event.doit = false;
              shell.setVisible(false);
            }
          });
    } // End of the If

    return button;
  }

  /** Core Event Wait Loop. */
  public void winLoop() {

    if (shell == null) {
      return;
    }
    if (display == null) {
      return;
    }
    while (!shell.isDisposed()) {
      if (!display.readAndDispatch()) display.sleep();
    }
    display.dispose();
  }

  /** Implementation Routine createPDFWindow. */
  public static final void createPDFWindow() {
    System.out.println("Launching XHTML to PDF window");
    (create()).createShellWin().winLoop();
  }

  /**
   * Implementation Routine createPDFWindowShell.
   *
   * @param shell Shell
   */
  public static final void createPDFWindowShell(final Shell shell) {
    System.out.println("Launching XHTML to PDF window");
    (create(shell)).createShellWin().winLoop();
  }

  public static final void createPDFWindowShellSettings(
      final Shell shell, final WinPDFSettings settings) {

    System.out.println("Launching XHTML to PDF window, settings = " + settings);
    final SimpleXHtmlPDFWin win = (create(shell)).createShellWin();
    win.setEnvSettings(settings);
    win.winLoop();
  }

  /**
   * Implementation Routine createPDFWindowShell.
   *
   * @param shell Shell
   */
  public static final void createPDFWindowShellDyna(final Object shell) {
    System.out.println("Launching XHTML to PDF window (dynamic)");
    (create((Shell) shell)).createShellWin().winLoop();
  }

  /**
   * Main Entry Point.
   *
   * @param args
   */
  private static void main(String[] args) {
    createPDFWindow();
  }
} // End of the Class
