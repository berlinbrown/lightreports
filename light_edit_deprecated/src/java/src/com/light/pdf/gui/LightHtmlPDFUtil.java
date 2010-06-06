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

/**
 * Misc PDF utils.
 */
public final class LightHtmlPDFUtil {

	/**
	 * Adobe property configuration
	 */
    public static final String _AD_PROP_ = "xhtmlrenderer_adobe";
    
    /**
     * Another Adobe Home Location.
     */
    public static final String _AD_8_    = "C:\\Program Files\\Adobe\\Reader 8.0\\Reader\\AcroRd32.exe";
    
    /**
     * Default Adobe Home.
     */
    public static final String _AD_HOME_ = "C:\\Program Files\\Adobe";

    private LightHtmlPDFUtil() {
    }
    
    /////////////////////////////////////////////////////////////////
    // Adobe Reader Detect UTils
    // We will detect reader first by system property
    // Then by the Adobe 8
    // Then by what is found in the directory
    // Returns 'null' on error.
    /////////////////////////////////////////////////////////////////
    
    /**
     * Check Adobe Reader.
     */
    private static final String checkAdobe(final String path, final String msg) {
        if (path != null) {
           final File propf = new File(path);
            if (propf.exists() && (!propf.isDirectory())) {
                // Note: file may not be an executable
                System.out.println("INFO: Found Adobe Reader at <" + msg + "> : " + path);
                return propf.getAbsolutePath();
            } else {
                System.out.println("WARN: Could not find Adobe Reader at <" + msg + "> : " + path);
            } // End of
        } else {
            return null;
        }
        return null;
    }
    
    /**
     * Find Adobe Reader.
     * @return
     */
    public static final String findAdobeReader() {
        try {
            final String adobeReaderProp = System.getProperty(_AD_PROP_);
            final String res1 = checkAdobe(adobeReaderProp, "Prop check");
            if (res1 != null) {
                return res1;
            } else {
                // Prop check failed
                // Adobe 8 check
                final String res2 = checkAdobe(_AD_8_, "Find Adobe 8");
                if (res2 != null) {
                    return res2;
                } else {
                    // Adobe 8 check failed.
                    return findAdobeFiles();
                }
            } // End of if

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        } // End of try catch

    } // End of Method //

    /**
     * Find Adobe Files.
     * 
     * @return
     */
    public static final String findAdobeFiles() {
        final File dir = new File(_AD_HOME_);
        if (dir.isDirectory()) {
            // Iterate through all the files until a match is found.
            final File dirs [] = dir.listFiles();
            for (int i = 0; i < dirs.length; i++) {
                if (dirs[i].isDirectory() && dirs[i].canRead()) {
                    final File readerExe = new File(dirs[i].getAbsolutePath() + File.separator
                                + "Reader" + File.separator + "AcroRd32.exe");
                    final String res3 = checkAdobe(readerExe.getAbsolutePath(), "Find Adobe Reader");
                    if (res3 != null) {
                        // fast fail on first find
                        return res3;
                    }
                }
            } // End of for //
        } else {
            return null;
        }
        return null;
    }
}

/////////////////////////////////////////////////
//End of File
////////////////////////////////////////////////