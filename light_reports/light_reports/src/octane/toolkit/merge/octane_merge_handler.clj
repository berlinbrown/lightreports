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


(ns octane.toolkit.merge.octane_merge_handler
	(:use octane.toolkit.octane_utils_common
          octane.toolkit.octane_utils
          octane.toolkit.public_objects
          octane.toolkit.octane_config
          octane.toolkit.octane_main_constants
          octane.toolkit.octane_file_utils
          octane.toolkit.octane_gui_utils
          octane.toolkit.octane_tools
          octane.toolkit.merge.octane_merge_objects)
	(:import (java.util Date)
			 (org.eclipse.swt SWT)
			 (org.eclipse.swt.widgets Display Shell Text Widget TabFolder TabItem)
			 (org.eclipse.swt.widgets Label Menu MenuItem Control Listener)
			 (org.eclipse.swt.widgets FileDialog MessageBox TableItem Button
									  Composite Table TableColumn)
			 (org.eclipse.swt.custom LineStyleEvent StyledText
									 LineStyleListener StyleRange)
			 (org.eclipse.swt.graphics Color RGB FontData Font)
			 (org.eclipse.swt.layout GridData GridLayout RowLayout RowData)
			 (org.eclipse.swt.events VerifyListener SelectionAdapter ModifyListener SelectionListener
									 SelectionEvent ShellAdapter ShellEvent)
			 (org.eclipse.swt.widgets FileDialog DirectoryDialog MessageBox Composite)
			 (org.eclipse.swt SWT)
			 (org.eclipse.swt.widgets Display Shell Text Widget TabFolder TabItem)
			 (java.util ResourceBundle Vector Hashtable)
             (java.io ByteArrayInputStream InputStreamReader)
             (java.io LineNumberReader FileInputStream FileOutputStream ByteArrayOutputStream
                      BufferedOutputStream PrintWriter)))

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

;; Max number of lines to process during round robin
;; Max value might include = 860,000
(def *max-level-robin-log*   2800)

(defn new-reader-merge-file
  "Create a buffered reader"
  [in-filename]
  ;;;;;;;;;;;;;;
  (LineNumberReader. (InputStreamReader. (FileInputStream. in-filename))))

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

(defn marshall-line-data
  "Convert the line string to a line data object.
 The return object has the format:
 { :tstamp 1231231 :data 'The Line Data' }"
  [line ftype]
  ;;;;;;;;;;;;;
  ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
  (let [pattr-obj (cond (= ftype "System")      { :pattern *new-pattern-log-system*    :gid 1 }
                        (= ftype "DB")          { :pattern *new-pattern-log-db-serv* :gid 1 }
                        (= ftype "RequestLog")  { :pattern *new-pattern-req-log*       :gid 1 }
                        (= ftype "Log4j")       { :pattern *new-pattern-log4j-serv1*     :gid 1 } )
        pattr (:pattern pattr-obj)
        gid   (:gid pattr-obj)
        m     (.matcher pattr line)]
    ;; Build the data structure with the time-stamp and the line data
    (when (.find m)
      (let [date-str (.group m gid)
            ;; Parse the date to generate a date object
            tstamp (parse-basic-date *sysout-date-format* date-str)]
        { :tstamp tstamp :data line }))))

(defn pop-log-file
  "Pop, read data off the log file"
  [log-file-set fn-consume]
  ;;;;;;;;;;;;;;;;;;;;;;;;;
  ;; Ignore error and return nil when calling consume/peek function
  (try (when log-file-set (fn-consume log-file-set))
       (catch Exception e 
              (println (.getMessage e))
              nil)))

(defn peek-log-file-read
  "Peek, read data off the log file.  Note: we may have issues
 with mark and reset"
  [#^java.io.InputStreamReader log-file-set]
  ;;;;;;;;;;;;;;;;;;;;;;;;;
  ((fn [#^java.io.InputStreamReader reader]
       (when reader
         (let [_ (.mark reader 4048)
               line (.readLine reader)
               line-num  (.getLineNumber reader)
               _ (.reset reader)]
           (if line {:line line :line-num line-num } nil))))
   log-file-set))

(defn pop-log-file-read
  "Pop, read data off the log file
 log-file-set is the input reader"
  [#^java.io.InputStreamReader log-file-set]
  ;;;;;;;;;;;;;;;;;;;;;;;;;
  ((fn [#^java.io.InputStreamReader reader]
       (when reader
         (let [line (.readLine reader)
               line-num  (.getLineNumber reader)]
           {:line line :line-num line-num })))
   log-file-set))

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

(defn round-robin-detect-eof
  "Hack to detect end of file.  Iterate through each stream
 and call the peek method.  If any of the lines are NOT null then
 we are not at the end.
 TODO: fix this code"
  [streams]
  ;;;;;;;;;;;
  (let [lst-chk (for [reader-data streams]
                  (let [r   (:reader reader-data)
                        nm  (:name   reader-data)
                        typ (:type   reader-data)]
                    (peek-log-file-read r)))]
    ;; If this check is true (empty?) then we are are the end of all of the streams
    (if lst-chk (empty? (remove nil? lst-chk))
        true)))

(defn round-robin-line-log
  "Perform a peek on each reader and then build a list of the current lines.
 If the line is less than or equal to the current min then perform a read.
 :min    <min-tstamp-data> -- Minimum timestamp
 :tstamp <min-tstamp-data> -- Timestamp"
  [streams min-tstamp-data print-writer line-prefix]
  ;;;;;;;;;;;;;;;;;;;;;;;;;;
  ;; Note: possible tail recursion/stack error with variable arg
  (let [m   (:min    min-tstamp-data)
        t   (:tstamp min-tstamp-data)
        lst (for [reader-data streams]
              (let [r   (:reader reader-data)
                    nm  (:name   reader-data)
                    typ (:type   reader-data)]
                ;; TODO: Possible error if no more lines to read?
                (when-let [line (peek-log-file-read r)]
                    ;; Line-data = :line, :line-num
                    (let [line-data (marshall-line-data (:line line) typ)
                          tstamp    (:tstamp line-data)]
                      ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
                      ;; Build another new data structure
                      ;; WITH the TIMESTAMP and READER DATA
                      ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
                      (if tstamp { :tstamp tstamp :reader-data reader-data :line line }
                          (pop-log-file-read r) )))))]
    ;; List should consist of a list of timestamp datastructures
    (let [line-data-min (when-let [raw-tlist-data (remove nil? lst)]
                            (first (sort-by :tstamp raw-tlist-data)))]
      (if (nil? (:tstamp line-data-min))
        (do (pop-log-file-read (:reader (:reader-data line-data-min)))
            min-tstamp-data)
        ;; Check for time stamp min, if so then write
        (if (:tstamp line-data-min)
          ;; If the line data is less than the min, then return new min data
          (do (let [my-reader-data (:reader-data line-data-min)
                    _ (pop-log-file-read (:reader my-reader-data))])
              ;; WRITE line data to merge file and return min data
              (when print-writer 
                (.println print-writer (str 
                                        (if (empty? line-prefix) "" line-prefix)
                                        (let [xp (:path (:reader-data line-data-min))]
                                          (if (empty? xp) "" (str xp "|")))
                                        (:name (:reader-data line-data-min)) "/" (:line-num (:line line-data-min)) ": ]" 
                                        (:line (:line line-data-min)))))
              { :min (:tstamp line-data-min) :tstamp (:tstamp line-data-min) })
          ;; Otherwise, return the current line-data-min
          min-tstamp-data)))))

(defn round-robin-log
  "Perform a peek on each reader and then build a list of the current lines.
 If the line is less than or equal to the current min then perform a read.
 :min    <min-tstamp-data> -- Minimum timestamp
 :tstamp <min-tstamp-data> -- Timestamp"
  [nest-level streams min-tstamp-data print-writer line-prefix]
  ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
  ;; Stop when end of streams detected or nested level too deep
  (if (or (round-robin-detect-eof streams) (> nest-level *max-level-robin-log*))
    nil
    (let [new-min-data (round-robin-line-log streams min-tstamp-data print-writer line-prefix)
          m (if (:min new-min-data) new-min-data min-tstamp-data)]
      (round-robin-log (+ 1 nest-level) streams m print-writer line-prefix))))

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;; Deprecated Function - parse all file
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

(defn parse-all-log-file-deprecated
  [reader run-fn]
  ;;;;;;;;;;;;;;;;;;
  (loop [_ (.mark reader 4048)
         line (.readLine reader) line-num (.getLineNumber reader)]
    (.reset reader)
    (when line (run-fn line line-num))
    (if (empty? line) ()
        (recur nil 
               (.readLine reader)
               (.getLineNumber reader)))))

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

(defn merge-table-files-streams
  "Process the files from the table list"
  [sav-filename]
  ;;;;;;
  (let [tabl-items (.getItems merge-file-table)
        cols       (.getColumnCount merge-file-table)]
    ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
    ;; Open an input stream for each log file
    ;; Create a list of readers
    ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
    (loop [log-streams []
           items tabl-items]
      (if-let [item (first items)]
          (let [fname (.getText item 0)
                path  (.getText item 1)
                typ   (.getText item 2)]
            (println "Processing merge table item => " path)
            (recur (cons { :reader (new-reader-merge-file path) :name fname :type typ :path path } log-streams)
                   (rest items)))
        log-streams))))

(defn merge-table-only-files
  "Process the files from the table list"
  [sav-filename]
  ;;;;;;
  (let [tabl-items (.getItems merge-file-table)
        cols       (.getColumnCount merge-file-table)]
    (loop [log-streams []
           items tabl-items]
      (if-let [item (first items)]
          (let [fname (.getText item 0)
                path  (.getText item 1)
                typ   (.getText item 2)]
            (println "Processing merge table item => " path)
            (recur (cons { :name fname :type typ :path path } log-streams)
                   (rest items)))
        log-streams))))

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

(defn merge-set-files-streams
  "Process the files from a list "
  [file-array-set log-type]
  ;;;;;;
  ;; Open an input stream for each log file
  ;; Create a list of readers
  (loop [log-streams []
         items file-array-set]
    (if-let [file-data (first items)]
        (let [fname     (.getName file-data)
              path      (.getAbsolutePath file-data)
              typ       log-type]
          (println "Processing merge table item =>" path)
          (recur (cons {:reader (new-reader-merge-file path) :name fname :type typ :path path} log-streams)
             (rest items)))
      log-streams)))

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

(defn merge-process-table-files
  "Process the files from the table list and save to file."  
  [sav-filename]
  ;;;;;;;;;;;;;;;;;
  ;; Make use of java.io FileOutputStream/BufferedOutputStream
  ;; to save to file
  (let [outs (FileOutputStream. sav-filename)
        bufs (BufferedOutputStream. outs)
        pw   (PrintWriter. bufs)
        streams (merge-table-files-streams sav-filename)]
    ;; Loop through all of the streams and then do a 
    ;; comparison on the timestamps
    ;; Perform a peek on each line
    ;; reader-data:
    ;; :reader <reader-data> -- The InputStream Reader
    ;; :name   <reader-data> -- Name of the file
    ;; :type   <reader-data> -- Log File Type
    (round-robin-log 0 streams { :min *timestamp-long-max* :tstamp 0 } pw "#")
    ;; Close the outputstream
    (.flush pw)
    (.close pw)))

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

(defn merge-process-files-to-stream
  "Process the files from a list and save memory."  
  [file-array strm]
  ;;;;;;;;;;;;;;;;;
  ;; Make use of java.io FileOutputStream/BufferedOutputStream
  ;; to save to file
  (let [bufs (BufferedOutputStream. strm)
        pw   (PrintWriter. bufs)
        streams (merge-set-files-streams file-array "System")]
    ;; Loop through all of the streams and then do a 
    ;; comparison on the timestamps
    (round-robin-log 0 streams { :min *timestamp-long-max* :tstamp 0 } pw "#")
    ;; Close the outputstream
    (.flush pw)
    (.close pw)))

(defn merge-process-files-to-memory
  "Process the files from a list and save memory."  
  [file-array]
  ;;;;;;;;;;;;;;;;;
  (let [strm (ByteArrayOutputStream.)]
    (merge-process-files-to-stream file-array strm)
    (when-let [str-data (.toString strm)]
        (async-add-main-text str-data)))
  nil)

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

(defn quick-merge-files-list
  "Given the stream/files list, build a list of file paths"
  [files-data]
  ;;;;;;;;;;;;;;
  (let [list (java.util.ArrayList. 10)]
    (doseq [log-data files-data]
        (.add list (java.io.File. (:path log-data))))
    list))

(defn quick-merge-table-files
  "Process the files from quick merge and save to file."  
  [sav-filename]
  ;;;;;;;;;;;;;;;;;
  ;; Make use of java.io FileOutputStream/BufferedOutputStream
  ;; to save to file
  (let [outs (FileOutputStream. sav-filename)
        bufs (BufferedOutputStream. outs)
        pw   (PrintWriter. bufs)
        files-data (merge-table-only-files sav-filename)]
    (let [list (quick-merge-files-list files-data)]
      (.print pw (com.octane.util.FileUtils/concatFiles list 1 "#")))      
    ;; Close the outputstream
    (.flush pw)
    (.close pw)))

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

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;; End of Script
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;