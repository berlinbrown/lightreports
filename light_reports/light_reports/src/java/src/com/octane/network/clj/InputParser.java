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
package com.octane.network.clj;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;

import com.octane.util.StringUtils;

/**
 */
public class InputParser {

    public static final RequestState parse(final String clientInputData) {
        return parse(clientInputData, true);
    }
    
    /**
     * Implementation Routine parse.
     * @param clientInputData String
     * @return RequestState
     */
    public static final RequestState parse(final String clientInputData, final boolean removeLeadingSlash) {
        
        if (clientInputData != null) {
        
            final String [] headerLines = clientInputData.split("[\\r\\n]+");
            if (headerLines != null) {
                final RequestState state = new RequestState();
                final String [] headerInputTokens = clientInputData.split("\\s+");
                if ((headerInputTokens != null) && headerInputTokens.length >= 2) {
                    state.setRequestType(StringUtils.trim(headerInputTokens[0]));
                    final String locRaw = StringUtils.trim(headerInputTokens[1]);   
                    // We want to remove the leading slash and the string is of the right size, remove slash
                    final String loc = removeLeadingSlash && (locRaw.length() >= 2) ? locRaw.substring(1) : locRaw;  
                    state.setRequestLocation(loc);
                    state.setLoaded(true);
                    parseURLAction(state);
                    parseURLActionCode(state);
                    return state;
                } // End of the if //
            }
        } // End of the if //
        
        // Return empty state //            
        return RequestState.emptyState();                    
        
    } // End of the Implementation Routine //
    
    private static final void parseURLAction(final RequestState state) {
        
        if (state != null) {
            final String loc = state.getRequestLocation();
                                    
            if ((loc != null) && (loc.length() >= ServerConsts.FIXED_URL_ACTION_MAX_LEN)) {
                // Check for '_osp_var_'
                final String prefixCheck = loc.substring(0, 9);                
                if (ServerConsts.URL_ACTION_PREFIX.equalsIgnoreCase(prefixCheck)) {
                    state.setUrlPrefix(prefixCheck);
                    // octane server page, variable enabled, check the action and the args
                    final String action = loc.substring(9, (9 + ServerConsts.FIXED_URL_ACTION_LEN));                                       
                    
                    /// Mutate/update the url action state.
                    state.setUrlAction(action);
                    final int withArgsLen = ServerConsts.FIXED_URL_ACTION_MAX_LEN + ServerConsts.FIXED_URL_ACTION_ARGS_OFF;
                    
                    // Check if we need to add arguments for the action.
                    if (loc.length() > withArgsLen) {
                        final String actionArgs = loc.substring(withArgsLen);                        
                        state.setUrlActionArgs(actionArgs);
                                                                        
                    }
                    
                } // End of the if //
            }
        } // End of State check //               
    }
    
    private static final void parseURLActionCode(final RequestState state) {
        
        if ((state != null) && (state.getUrlAction() != null)) {
            final String action = state.getUrlAction();
            
            // Handler for open file and dir
            if (action.startsWith(ServerConsts.ACTION_OPEN_FILE)
                    || action.startsWith(ServerConsts.ACTION_BROWSE_DIR)) {
                                
                if (action.startsWith(ServerConsts.ACTION_OPEN_FILE)) {
                    state.setUrlActionCode(ServerConsts.ENUM_ACTION_CODE_OPEN_FILE);
                    
                } else if (action.startsWith(ServerConsts.ACTION_BROWSE_DIR)) {
                    state.setUrlActionCode(ServerConsts.ENUM_ACTION_CODE_BROWSE_DIR);
                }
                
                // Decode //
                try {
                    final String decoded = URLDecoder.decode(state.getUrlActionArgs(), "UTF-8");
                    state.setUrlActionArgsDecoded(decoded);
                    System.out.println("Args decoded: " + decoded);
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }                
                                
            } // End of the if //
        }
    }
    
} // End of the Class //
