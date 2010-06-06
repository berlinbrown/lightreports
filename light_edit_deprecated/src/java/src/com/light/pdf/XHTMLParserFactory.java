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
package com.light.pdf;

/**
 * Utility class for taking an input HTML document, 
 * formatting the content and then ensuring that is properly 
 * formed for XHTMLRenderer and TagSoup.
 */
public final class XHTMLParserFactory {
	
	/**
	 * Min HTML document length.
	 */
	private static final int MIN_DATA_LEN = 8;
	
	/**
	 * The default constructor is disabled.
	 */
	private XHTMLParserFactory() {
		
	}
	
    /**
     * Return and instance of a HTML PDF Renderer.
     * 
     * @param full_classname
     * @param origHtmlData
     * @return
     */
    public static final LightHTMLRendererText create(final String fullClassname, 
                            final String origHtmlData) {

        // Resolve bug where error message may flow through to parsing objects.
        if ((origHtmlData == null) || (origHtmlData.length() <= MIN_DATA_LEN)) {
            XHTMLRendererBase.println("<Factory parser create> invalid document, falling back to error document");
            return null;
        }        
        XHTMLRendererBase.println("<Factory parser create> attempting to create parser, classname=" + fullClassname + " size=" + origHtmlData.length());
        if ((fullClassname != null) && (fullClassname.length() != 0)) {

            // Dynamically add parser
            Class parserClass;
            try {
                parserClass = Class.forName(fullClassname);
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
                throw new RuntimeException("Invalid parser class (ClassNotFoundException) err=" + e.getMessage());
            }
            LightHTMLRendererText parser;
            try {
                parser = (LightHTMLRendererText) parserClass.getConstructors()[0].newInstance(new Object [] { origHtmlData});

            } catch (Exception x) {
                x.printStackTrace();
                throw new RuntimeException("Invalid parser class (" + x.getClass().getName() + ") err=" + x.getMessage());
            }
            parser.setText(origHtmlData);
            return parser;

        } else {
        	XHTMLRendererBase.println("<Factory parser create> not using PDF parser");
            return null;
        } // End of the if - else

    } // End of the method

} // End of the Class

/////////////////////////////////////////////////
//End of File
/////////////////////////////////////////////////