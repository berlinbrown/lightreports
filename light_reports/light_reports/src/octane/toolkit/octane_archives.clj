;;;
;;; Copyright (c) 2006-2007 Berlin Brown and berlin2research.com  All Rights Reserved
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
;;;       Misc updates 1/1/2013
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
;;;

(ns octane.toolkit.octane_archives
	(:use
     octane.toolkit.octane_main_constants
     octane.toolkit.octane_utils_common
	 octane.toolkit.octane_utils
	 octane.toolkit.public_objects
	 octane.toolkit.octane_gui_utils
	 octane.toolkit.octane_config
     octane.toolkit.octane_file_utils)
	(:import 
	 (java.io File)
	 (org.eclipse.swt SWT)
	 (org.eclipse.swt.widgets Display Shell Text Widget TabFolder TabItem)
	 (org.eclipse.swt.widgets FileDialog MessageBox TableItem Button
							  Composite Table TableColumn)
	 (org.eclipse.swt.layout GridData GridLayout RowLayout RowData)
	 (org.eclipse.swt.events VerifyListener SelectionAdapter ModifyListener SelectionListener
							 SelectionEvent ShellAdapter ShellEvent)
	 (org.eclipse.swt.widgets Label Menu MenuItem Control Listener)
     (com.octane.util.zip UncompressInputStream)
     (java.io FileInputStream InputStream ByteArrayOutputStream FileOutputStream)     
     (java.util.zip ZipInputStream InflaterInputStream)
	 (java.io InputStreamReader 
			  FileInputStream BufferedReader File FilenameFilter)))

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

(defn open-archive-has-file?
  "Open an archive file and determine if there is only one entry"
  [infile]
  ;;;;;;;;;;;;
  (try (let [zin (new ZipInputStream (new FileInputStream infile))]
         (loop [entry (. zin getNextEntry)
                ctr   0]
           (when entry
             (recur (. zin getNextEntry) (+ ctr 1))))
         (. zin close))
       (catch Exception e (. e printStackTrace))))

(defn open-compressed-file
  "Open an archive file and LZC unix compressed .Z extension"
  [infile]
  ;;;;;;;;;;;;
  (try (let [zin (new UncompressInputStream (new FileInputStream infile))
             bbuf #^"[B" (make-array (. Byte TYPE) 20480)
             bout (new ByteArrayOutputStream 20480)]
		 (loop [got (. zin read bbuf)
                tot 0]		  
		   (when (> got 0)
			 (. bout write bbuf, 0 got)
			 (recur (. zin read bbuf) (+ tot 1))))
		 ;; With the byte array outputstream
		 ;; Convert the bytes to string
		 (. bout flush)
		 (. zin close)
		 (new String (. bout toByteArray)))
       (catch Exception e 
			  (. e printStackTrace)
			  nil)))

(defn stream-compressed-file
  "Open an archive file and LZC unix compressed .Z extension"
  [infile #^java.io.OutputStream out]
  ;;;;;;;;;;;;
  (println "DEBUG: attempting to open archive file => " infile)
  (try (let [zin (new UncompressInputStream (new FileInputStream infile))
				 bbuf #^"[B" (make-array (. Byte TYPE) 20480)]
		 (loop [got (. zin read bbuf)
					tot 0]
		   (when (> got 0)
			 (. out write bbuf, 0 got)
			 (recur (. zin read bbuf) (+ tot 1))))
		 ;; With the byte array outputstream
		 ;; Convert the bytes to string
		 (. out flush)
		 (. zin close)
		 (. out close))
       (catch Exception e
			  (. e printStackTrace)
			  nil)))

(defn win-open-compressed-file
  "Open an archive file and LZC unix compressed .Z extension"
  [infile]
  ;;;;;;;;;;;;
  (let [data (open-compressed-file infile)]
	(async-status-history *display* (str "Open LZC (unix compressed) file => " infile *newline*))
	(async-add-main-text data)))

(defn check-archive-handler
  "Determine the archive type based on the extension and handle accordingly"
  [disp file path]
  ;;;;;;;;;;;;;;;;;
  (cond (. path endsWith ".Z")   (win-open-compressed-file file)
		(. path endsWith ".jar") (open-jar-file file)
		(. path endsWith ".zip") (println "Not implemented")
		:default                 (println "Not implemented")))
		                  
(defn open-archive-file-handler
  "Open the archive file.   Open the file  in the main buffer if only ONE text file exists.
 If more than one exists than just list the entries."
  [disp file path]
  ;;;;;;;;;;;;;;;;;
  (check-archive-handler disp file path))
  
(def open-archive-file-listener
     ;; Open the archive file.   Open the file  in the main buffer if only ONE text file exists.
     ;; If more than one exists than just list the entries."  
     ;;;;;;;
     (proxy [SelectionAdapter] []
            (widgetSelected [e] (simple-dialog-open-file
                                 *display* open-archive-file-handler  *zip-wildcard-seq* ))))

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;; Archive Directory Handlers
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

(def uncompress-filename-filter
	 (proxy [FilenameFilter] []
			(accept [dir-file name]
					(if name
					  (.endsWith name  ".Z")
					  false))))

(defn zfiles-dir-handler
  "zfile-func takes two arguments, the input 'file' object, the output tmp directory"
  [file-path zfile-func tmp-dir]
  (let [file (new File file-path)]
	(doseq [cur-fil (.listFiles file uncompress-filename-filter)]
		(zfile-func cur-fil tmp-dir))))

(def *invalid-dir-chars* "(\\\\|/|:|\\s|\\.)")

(defn get-unique-path-name 
  "We need to ensure that the tmp archive files have unique names.
 Take the directory name and append to the file name"
  [#^java.io.File file]
  ;;;;;;;;;;;;;;;;;;;;;;;
  (let [pfile (.getParentFile file)
        path  (.getAbsolutePath pfile)]
    ;; Replace forward and backward slash with underscore
    (str "_" (.replaceAll path *invalid-dir-chars* "_") "_")))

(defn stream-zfile-handler
  "Where -stream-func takes two arguments: 
        File and FileOutputStream
 -create-file-func takes one argument File and returns a boolean"
  [my-date-str stream-func & [create-file-func? start-t end-t]]
  (fn [#^java.io.File file tmp-dir]
      ;; Create the output stream object.
      (let [fname (str tmp-dir *name-separator* (.getName file) (get-unique-path-name file) ".log")
            last-mod (. file lastModified)
            my-date-str-1 (str my-date-str " " (if (not (empty? start-t)) (.trim start-t) "00:01") ":00")
            my-date-str-2 (str my-date-str " " (if (not (empty? end-t)) (.trim end-t)     "23:59") ":00")
            my-date-1 (.parse *simple-date-format-t* my-date-str-1)
            my-date-2 (.parse *simple-date-format-t* my-date-str-2)]
        ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
        ;; Also check if the last modified time is between 
        ;; the current date we are interested in.
        ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
        (when (and (> last-mod (.getTime my-date-1)) 
                   (< last-mod (.getTime my-date-2))
                   (create-file-func? file))
          ;; create the new text file unarchived text file
          (let [fos (new FileOutputStream fname)]
            (try (stream-func file fos)
                 (catch Exception e (.printStackTrace e))
                 (finally (.flush fos) (.close fos) (println "Closing stream file"))))))))

(defn simple-stream-zfile-handler
  [my-date-str]
  (stream-zfile-handler my-date-str
   (fn [#^java.io.File file #^java.io.FileOutputStream fos]
       (println "Streaming uncompressed file to tmp directory")
       (stream-compressed-file file fos)
       (println "Done Streaming file =>" (.getAbsolutePath file)))))

(defn archive-search-grep
  [#^String filename term]
  ;;;;;;;;;;;;;
  (let [buf (new StringBuffer)]
    (doc-file-loop-handler filename
                           (fn [line line-num]
                               ;; Lambda function for on find string
                               ;; Update the display with the 'grep' information.
                               (when (simple-grep? line term)
                                 (.append buf
                                          (str filename ": line " line-num ": " 
                                               (.trim line) \newline)))))
    (if (empty? (.toString buf)) nil (.toString buf))))

;;
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
;;

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;; End of Script
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
