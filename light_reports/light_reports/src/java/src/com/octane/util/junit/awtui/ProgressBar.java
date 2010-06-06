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
package com.octane.util.junit.awtui;

import java.awt.Canvas;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Rectangle;
import java.awt.SystemColor;

public class ProgressBar extends Canvas {
	public boolean fError= false;
	public int fTotal= 0;
	public int fProgress= 0;
	public int fProgressX= 0;

	public ProgressBar() {
		super();
		setSize(20, 30);
	}
	
	private Color getStatusColor() {
		if (fError)
			return Color.red;
		return Color.green;
	}
	
	public void paint(Graphics g) {
		paintBackground(g);
		paintStatus(g);
	}
	
	public void paintBackground(Graphics g) {
		g.setColor(SystemColor.control);
		Rectangle r= getBounds();
		g.fillRect(0, 0, r.width, r.height);
		g.setColor(Color.darkGray);
		g.drawLine(0, 0, r.width-1, 0);
		g.drawLine(0, 0, 0, r.height-1);
		g.setColor(Color.white);
		g.drawLine(r.width-1, 0, r.width-1, r.height-1);
		g.drawLine(0, r.height-1, r.width-1, r.height-1);
	}
	
	public void paintStatus(Graphics g) {
		g.setColor(getStatusColor());
		Rectangle r= new Rectangle(0, 0, fProgressX, getBounds().height);
		g.fillRect(1, 1, r.width-1, r.height-2);
	}
	
	private void paintStep(int startX, int endX) {
		repaint(startX, 1, endX-startX, getBounds().height-2);
	}
	
	public void reset() {
		fProgressX= 1;
		fProgress= 0;
		fError= false;
		paint(getGraphics());
	}
	
	public int scale(int value) {
		if (fTotal > 0)
			return Math.max(1, value*(getBounds().width-1)/fTotal);
		return value; 
	}
	
	public void setBounds(int x, int y, int w, int h) {
		super.setBounds(x, y, w, h);
		fProgressX= scale(fProgress);
	}
	
	public void start(int total) {
		fTotal= total;
		reset();
	}
	
	public void step(boolean successful) {
		fProgress++;
		int x= fProgressX;

		fProgressX= scale(fProgress);

		if (!fError && !successful) {
			fError= true;
			x= 1;
		}
		paintStep(x, fProgressX);
	}
}