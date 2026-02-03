/********************************************************************
 *
 * Copyright (c) 2006-2007 Berlin Brown and botnode.com  All Rights Reserved
 *
 * http://www.opensource.org/licenses/bsd-license.php
 *
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without modification,
 * are permitted provided that the following conditions are met:
 *
 * * Redistributions of source code must retain the above copyright notice,
 * this list of conditions and the following disclaimer.
 * * Redistributions in binary form must reproduce the above copyright notice,
 * this list of conditions and the following disclaimer in the documentation
 * and/or other materials provided with the distribution.
 * * Neither the name of the Botnode.com (Berlin Brown) nor
 * the names of its contributors may be used to endorse or promote
 * products derived from this software without specific prior written permission.
 *
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
package com.octane.util.junit.awtui;

import com.octane.util.junit.runner.Version;
import java.awt.Button;
import java.awt.Dialog;
import java.awt.Font;
import java.awt.Frame;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Label;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

class AboutDialog extends Dialog {
  public AboutDialog(Frame parent) {
    super(parent);

    setResizable(false);
    setLayout(new GridBagLayout());
    setSize(330, 138);
    setTitle("About");

    Button button = new Button("Close");
    button.addActionListener(
        new ActionListener() {
          public void actionPerformed(ActionEvent e) {
            dispose();
          }
        });

    Label label1 = new Label("JUnit");
    label1.setFont(new Font("dialog", Font.PLAIN, 36));

    Label label2 = new Label("JUnit " + Version.id() + " by Kent Beck and Erich Gamma");
    label2.setFont(new Font("dialog", Font.PLAIN, 14));

    Logo logo = new Logo();

    GridBagConstraints constraintsLabel1 = new GridBagConstraints();
    constraintsLabel1.gridx = 3;
    constraintsLabel1.gridy = 0;
    constraintsLabel1.gridwidth = 1;
    constraintsLabel1.gridheight = 1;
    constraintsLabel1.anchor = GridBagConstraints.CENTER;
    add(label1, constraintsLabel1);

    GridBagConstraints constraintsLabel2 = new GridBagConstraints();
    constraintsLabel2.gridx = 2;
    constraintsLabel2.gridy = 1;
    constraintsLabel2.gridwidth = 2;
    constraintsLabel2.gridheight = 1;
    constraintsLabel2.anchor = GridBagConstraints.CENTER;
    add(label2, constraintsLabel2);

    GridBagConstraints constraintsButton1 = new GridBagConstraints();
    constraintsButton1.gridx = 2;
    constraintsButton1.gridy = 2;
    constraintsButton1.gridwidth = 2;
    constraintsButton1.gridheight = 1;
    constraintsButton1.anchor = GridBagConstraints.CENTER;
    constraintsButton1.insets = new Insets(8, 0, 8, 0);
    add(button, constraintsButton1);

    GridBagConstraints constraintsLogo1 = new GridBagConstraints();
    constraintsLogo1.gridx = 2;
    constraintsLogo1.gridy = 0;
    constraintsLogo1.gridwidth = 1;
    constraintsLogo1.gridheight = 1;
    constraintsLogo1.anchor = GridBagConstraints.CENTER;
    add(logo, constraintsLogo1);

    addWindowListener(
        new WindowAdapter() {
          public void windowClosing(WindowEvent e) {
            dispose();
          }
        });
  }
}
