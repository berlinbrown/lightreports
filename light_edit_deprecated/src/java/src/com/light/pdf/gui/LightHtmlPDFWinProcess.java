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

/**
 * Simple HTML to PDF converter. 
 */
public abstract class LightHtmlPDFWinProcess implements LightHtmlPDFWinThread  {

	/**
	 * Main PDF handler.
	 */
    private LightHtmlPDFWinAdapters adapter;
    
    /**
     * Run Parse HTML document.
     */
    public abstract void runParseHtmlDocument() throws Exception;

    /**
     * Main Constructor the Win Process.
     * 
     * @param adapter
     */
    public LightHtmlPDFWinProcess(final LightHtmlPDFWinAdapters adapter) {
        this.setWinAdapter(adapter);
    }

    /**
     * Win thread run implementation method.
     */
    public final void run() {

        if (this.getWinAdapter() == null) {
            System.out.println("ERR: invalid win adapter.");
        }
        System.out.println("Launching PDF Create Process.");
        try {
            this.runParseHtmlDocument();
        } catch (Exception e) {
            e.printStackTrace();
            final String msg = "<<ERR>> at run parse html document, err=\n" + e.getMessage();
            this.getWinAdapter().status("<<ERR>> at run parse html document");
            this.getWinAdapter().createErrorMsgBox(msg);
        }
    }

    /**
     * Set the Win Adapter.
     */
    public final void setWinAdapter(final LightHtmlPDFWinAdapters adapter) {
        if (this.adapter == null) {
            this.adapter = adapter;
        }
    }

    /**
     * Return the Win Adapter.
     */
    public LightHtmlPDFWinAdapters getWinAdapter() {
        return this.adapter;
    }

} // End of the Class

/////////////////////////////////////////////////
//End of File
////////////////////////////////////////////////