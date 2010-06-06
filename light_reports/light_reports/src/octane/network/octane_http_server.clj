;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;;; Copyright (c) 2006-2007 Berlin Brown and botnode.com  All Rights Reserved
;;;
;;; http://www.opensource.org/licenses/bsd-license.php

;;; All rights reserved.

;;; Redistribution and use in source and binary forms, with or without modification,
;;; are permitted provided that the following conditions are met:

;;; * Redistributions of source code must retain the above copyright notice,
;;; this list of conditions and the following disclaimer.
;;; * Redistributions in binary form must reproduce the above copyright notice,
;;; this list of conditions and the following disclaimer in the documentation
;;; and/or other materials provided with the distribution.
;;; * Neither the name of the Botnode.com (Berlin Brown) nor
;;; the names of its contributors may be used to endorse or promote
;;; products derived from this software without specific prior written permission.

;;; THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
;;; "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT
;;; LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR
;;; A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR
;;; CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL,
;;; EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO,
;;; PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR
;;; PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF
;;; LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
;;; NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
;;; SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
;;;

;;; Date: 1/5/2009
;;;       7/15/2009 - Added Clojure 1.0, other performance fixes and cleanups.
;;;      
;;; Main Description: Light Log Viewer is a tool for making it easier to search log files.  
;;; Light Log Viewer adds some text highlighting, quick key navigation to text files, simple graphs 
;;; and charts for monitoring logs, file database to quickly navigate to files of interest, 
;;; and HTML to PDF convert tool.  
;;; Light Log was developed with a combination of Clojure 1.0, Java and Scala with use of libs, SWT 3.4, JFreeChart, iText. 
;;; 
;;; Quickstart : the best way to run the Light Log viewer is to click on the win32 batch script light_logs.bat
;;; (you may need to edit the Linux script for Unix/Linux environments).
;;; Edit the win32 script to add more heap memory or other parameters.

;;; The clojure source is contained in : HOME/src/octane
;;; The java source is contained in :  HOME/src/java/src

;;; To build the java source, see : HOME/src/java/build.xml and build_pdf_gui.xml

;;; Metrics: (as of 7/15/2009) Light Log Viewer consists of 6500 lines of Clojure code, and contains wrapper code
;;; around the Java source.  There are 2000+ lines of Java code in the Java library for Light Log Viewer.

;;; Additional Development Notes: The SWT gui and other libraries are launched from a dynamic classloader.  Clojure is also
;;;  started from the same code, and reflection is used to dynamically initiate Clojure. See the 'start' package.  The binary
;;;  code is contained in the octane_start.jar library.

;;; Home Page: http://code.google.com/p/lighttexteditor/
;;;  
;;; Contact: Berlin Brown <berlin dot brown at gmail.com>
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

(ns octane.network.octane_http_server
	(:use octane.toolkit.octane_utils_common)
	(:import (java.util Date)
             (java.io File)))
                        
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

(defn client-browse-dir-action [self-handler]  
  (when (.isActionBrowseDir self-handler)
    (let [req-state (.getRequestState self-handler)]
      (println "debug [open dir] : request state : " req-state)
      (let [url-2 (.getUrlActionArgsDecoded req-state)
            #^java.io.File file (java.io.File. url-2)]
        (println (.getAbsolutePath file))        
        (let [files (com.octane.util.FileUtils/listLogFiles file)
              *dir-list-buf* (StringBuffer. 128)]
          (.append *dir-list-buf* (str "<html>" \newline))
          (.append *dir-list-buf* (str "<table>" \newline))
          (doseq [file files]
              (.append *dir-list-buf* (str "<tr>" \newline))
              (.append *dir-list-buf* (str "<td>" (.getAbsolutePath file) "</td>" \newline))
              (.append *dir-list-buf* (str "</tr>" \newline)))
          (.append *dir-list-buf* (str "</table>" \newline))
          (.append *dir-list-buf* (str "</html>" \newline))
          ;; Write the browsing data
          (.writeHtmlDoc self-handler "200" "text/html" (.toString *dir-list-buf*)))))))
              
(defn client-write-output-action [self-handler]  
  (client-browse-dir-action self-handler)
  (when (.isActionOpenFile self-handler)
    (let [req-state (.getRequestState self-handler)]
      (println "debug [open file] : request state : " req-state)
      (let [url-2 (.getUrlActionArgsDecoded req-state)
            #^java.io.File file (java.io.File. url-2)]
        (println (.getAbsolutePath file))
        ;; read the raw data and then write back
        (let [html-data (com.octane.util.FileUtils/readLinesRaw file)]
          (.writeHtmlDoc self-handler "200" "text/plain" html-data))))))
          
(def client-handler-thread
     (proxy [com.octane.network.clj.OctaneClientHandlerThread] []
            (writeClientOutput [self-handler] (client-write-output-action self-handler))))

(defn server-socket-request
  "Handle a socket client request"
  [#^com.octane.network.clj.OctaneServerSocket server]
  ;;;;;;;;;;;;
  (let [socket-accept  (.accept (.getServerSocket server))
        handler-thread client-handler-thread]
    (.init handler-thread socket-accept)
    (when-let [handler-t (java.lang.Thread. handler-thread)]
        (.start handler-t))))
            
(def server-socket-handler
     (proxy [com.octane.network.clj.OctaneServerSocket] []
            (handleRequest [server] (server-socket-request server))))

(defn create-server-socket
  "Create an instance of the server socket"
  []
  ;;;;;;
  (let [handler server-socket-handler]
    (.runServer handler)))

(defn main-1 
  " Application Entry Point, launch the main window and wait for events"
  []
  ;;;;
  (println "Launching Octane HTTP Server...")
  (create-server-socket))

(defn -main [& args]
  (try (main-1)
	   (catch Exception e
			  (println "ERR at <Main [1]>: " e)
              (.printStackTrace e))))

;; Invoke entry point
;; Remove -main when running gen-class
(-main)

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;; REVISION HISTORY - Light Logs Clojure Source
;;
;; -------------------------------------
;; + 1/5/2009  Berlin Brown : Project Create Date

;; + 1/5/2009  Berlin Brown : Add new headers
;; + 6/23/2009 : Major bug fixes
;; + 6/23/2009 : Move database file to classpath
;; + 6/23/2009 : Colorize log file
;; + 6/23/2009 : Show number of lines in a file
;; + 6/23/2009 : Quick Merge Files Together
;; + 6/23/2009 : Filter only the lines that have search terms and which line number
;; + 6/23/2009 : Have an additional merger but no true time merge (see cat command)
;; + 6/23/2009 : Print number of lines in buffer
;; -------------------------------------

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;;; End of Script
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;