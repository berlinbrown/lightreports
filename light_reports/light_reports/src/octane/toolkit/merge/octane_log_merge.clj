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


(ns octane.toolkit.merge.octane_log_merge
	(:use octane.toolkit.octane_utils_common
          octane.toolkit.octane_utils
          octane.toolkit.public_objects
          octane.toolkit.octane_config
          octane.toolkit.octane_main_constants
          octane.toolkit.octane_file_utils
          octane.toolkit.octane_gui_utils
          octane.toolkit.octane_tools
          octane.toolkit.merge.octane_merge_handler
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
             (java.io LineNumberReader FileInputStream)))

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

(def  *merge-win-state*      (com.octane.util.ReadOnlyState.))
(defn *merge-lock-win-state* [] (.lock *merge-win-state*))
(defn *merge-get-win-state*  [] (.getState *merge-win-state*))
(defn merge-win-loaded?      [] (*merge-get-win-state*))
(defn merge-win-not-loaded?  [] (not (*merge-get-win-state*)))

(defn detect-log-line
  "Determine the type of log file based on this line"
  [line num]
  ;;;;;;;;;;;;
  (when (not (empty? line))
    (let [sys?   (.find (.matcher *new-pattern-log-system* line))
          db2?   (.find (.matcher *new-pattern-log-db-serv* line))
          req?   (.find (.matcher *new-pattern-req-log* line))
          log4j? (.find (.matcher *new-pattern-log4j-serv1* line))]
      (cond sys?   :sys
            db2?   :db2
            req?   :req
            log4j? :log4j))))

(defn detect-log-file
  "Determine the log file type"
  [stream max-num run-fn]
  ;;;;;;;;;;;;;;;;;;;;;;;;
  (let [reader (LineNumberReader. (InputStreamReader. stream))]
    (loop [line (.readLine reader) 
           line-num (.getLineNumber reader)
           last-valid-type nil]
      ;; Iterate through the lines and determine
      ;; the type of log file ;;
      (when line (run-fn line line-num))
      (if (or (empty? line) (> line-num max-num))
        last-valid-type
        (recur (.readLine reader)
               (.getLineNumber reader)
               (if last-valid-type last-valid-type (run-fn line line-num)))))))

;; Five table columns, loaded from database configuration file.
(def merge-table-col-names ["Filename" "Path" "Log Type" ])

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;; Proxy Helper for Database Buttons
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

(defn merge-loc-bar [] (. merge-location-box getText))

(defn create-merge-grid-layout []
  (let [gridLayout (new GridLayout)]
    (set! (. gridLayout numColumns) 1)
    gridLayout))

(defn merge-add-table-item [item-seq]
  (let [item (new TableItem merge-file-table SWT/NONE)]
    (. item setText (into-array item-seq))))

(defn merge-table-select-listener []
  (proxy [SelectionAdapter][]
         (widgetDefaultSelected [event]
                                (let [items (. merge-file-table getSelection)]
                                  (when (> (count items) 0))))))

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;; Merge File Listener and Handler
;; When the user selects the add file button, invoke
;; the following events
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

(defn add-valid-log-file
  "A log file has been found, add to the table"
  [#^java.io.File file fname path log-type]
  ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
  (let [type-str (cond (= :sys   log-type)  "System"
                       (= :db2   log-type)  "DB"
                       (= :req   log-type)  "RequestLog"
                       (= :log4j log-type)  "Log4j")]
    (when type-str
       (async-call *display* (merge-add-table-item [ fname path type-str ])))))

(defn merge-add-file
  "Add the file to the table based on the input path"
  [#^java.io.File file]
  ;;;;;;;;;;;;
  (println "Merge add - file => " file)
  (let [fname (.getName file)
        path  (.getAbsolutePath file)
        strm  (FileInputStream. file)]
    ;; First, attempt to determine the type of log file by analyzing
    ;; the first couple of files.
    (if-let [log-type (detect-log-file strm *max-detect-lines* detect-log-line)]
        (add-valid-log-file file fname path log-type)
      (println "Invalid Log File at merge-add-file => " path))))
    
(def merge-open-file-handler
     ;; Where file-handler takes the following arguments : <DISPLAY> <FILE> <PATH>
     (fn [disp file path]
         (let [merge-thread (proxy [Runnable][]
                                   (run []
                                        (try (merge-add-file file)
                                             (catch Exception e
                                                    (. e printStackTrace)))))]
           (. (new Thread merge-thread) start))))

(defn merge-add-file-handler
  "On the add file event, invoke the handler.  Invoke the open file dialog
 box to allow the user to select a file to add.  At that point, detect the log file type
 and add to the table"
  []
  ;;;;;
  (simple-dialog-open-file *display* merge-open-file-handler *merge-wildcard-seq*))
  
(def merge-add-file-listener
     (proxy [SelectionListener][]
            (widgetSelected [event] (merge-add-file-handler))
            (widgetDefaultSelected [event] (merge-add-file-handler))))

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;; Merge Save File Listener

(defn merge-log-file
  "Window wrapper for write data to file."
  [out-filename]
  ;;;;;;;;;;;;;;;;;;;;
  (try (do (merge-process-table-files out-filename)
           (async-status-history *display* (str "Merge File saved to => " out-filename " at " (date-time)))
           (.info *logger* (str "Merge File saved to => " out-filename " at " (date-time) *newline*)))
       (catch Exception e
              (.printStackTrace e)
              (async-status-history *display* (.getMessage e)))))

(defn quick-merge-log-file
  "Window wrapper for write data to file."
  [out-filename]
  ;;;;;;;;;;;;;;;;;;;;
  (try (do (quick-merge-table-files out-filename)
           (async-status-history *display* (str "Quick Merge File saved to => " out-filename " at " (date-time)))
           (.info *logger* (str "Quick Merge File saved to => " out-filename " at " (date-time) *newline*)))
       (catch Exception e
              (.printStackTrace e)
              (async-status-history *display* (.getMessage e)))))

(defn merge-save-as-file-handler
  "Handler for saving a file to disk.  Check the curfile-open if it is 
 available.  If it is not available then open the 'save-as' dialog"
  []
  ;;;;;
  ;; First check if there are items in the table
  ;; On valid == continue with save merge-file.
  ;; On faild == display alert box with error.
  (if (> (.getItemCount merge-file-table) 0)
    (if-let [cur-file (dialog-save-as-file)]
        (merge-log-file cur-file)
      (async-status-history *display* "Please open a file to save"))
    (do (async-status-history *display* "No files available to merge, please add a log file")
        ;; Display alert box with error message
        (display-error "No files available to merge. Please add a log file to continue."))))

(def merge-save-file-as-listener
     (proxy [SelectionAdapter][]
            (widgetSelected [event] (merge-save-as-file-handler))))

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

(defn quick-merge-save-as-handler
  "Quick Merge Handler for saving a file to disk.  Check the curfile-open if it is 
 available.  If it is not available then open the 'save-as' dialog"
  []
  ;;;;;
  ;; First check if there are items in the table
  ;; On valid == continue with save merge-file.
  ;; On faild == display alert box with error.
  (if (> (.getItemCount merge-file-table) 0)
    (if-let [cur-file (dialog-save-as-file)]
        (quick-merge-log-file cur-file)
      (async-status-history *display* "Please open a file to save"))
    (do (async-status-history *display* "No files available to merge, please add a log file")
        ;; Display alert box with error message
        (display-error "No files available to merge. Please add a log file to continue."))))

(def quick-merge-file-as-listener
     (proxy [SelectionAdapter][]
            (widgetSelected [event] (quick-merge-save-as-handler))))

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

(defn merge-create-database-table 
  "Create the database viewer table"
  [merge-sh]
  ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
  (. merge-file-table setHeaderVisible true)  
  (. merge-file-table addSelectionListener (merge-table-select-listener))
  ;; Add the table column headers
  (let [gd (new GridData SWT/FILL SWT/FILL true true)]
    (doseq [t merge-table-col-names]
        (let [column (new TableColumn merge-file-table SWT/NONE)]
          (. column setText  t)
          (. column setWidth *merge-col-size*)))
    (. merge-file-table setLayoutData gd)))

(def merge-on-close-listener
     (proxy [SelectionListener][]
            (widgetSelected [event]
                            (set! (. event doit) false)
                            (. merge-shell setVisible false))
            (widgetDefaultSelected [event]
                                   (set! (. event doit) false)
                                   (. merge-shell setVisible false))))

(defn merge-init-buttons-composite 
  "Set the layout and listeners for the buttons" 
  [sh]
  ;;;;;;;
  (let [comp merge-button-comp
        gd-comp       (new GridData)
        gd-button     (new RowData *merge-button-width* *merge-button-height*)
        gd-med-button (new RowData *merge-bttn-med-width* *merge-button-height*)]
    (. comp setLayoutData gd-comp)
    (. comp setLayout (new RowLayout))
    (doto merge-action-button
      (. setText *merge-action-text*) 
      (. setLayoutData gd-button)
      (.addSelectionListener merge-save-file-as-listener))
    (doto merge-add-file-button  
      (. setText *merge-add-file-text*) 
      (. setLayoutData gd-med-button)
      (.addSelectionListener merge-add-file-listener))
    (doto merge-quick-button
      (. setText *merge-quick-text*) 
      (. setLayoutData gd-med-button)
      (.addSelectionListener quick-merge-file-as-listener))
    (doto merge-win-close-button 
      (. setText *database-quit-button*) 
      (. setLayoutData gd-med-button)
      (. addSelectionListener  merge-on-close-listener))))

(defn merge-init-database-helper [sh]
  (let [gd (new GridData SWT/FILL SWT/FILL true false)]
    (merge-init-buttons-composite sh)
    (. merge-location-box setLayoutData gd)
    (. merge-search-box setLayoutData gd)
    (println "Database column count =>" (.getColumnCount merge-file-table))
    (when (= 0 (.getColumnCount merge-file-table))
      (merge-create-database-table sh))))

(defn get-merge-table-size
  []
  ;;;;
  (when merge-file-table
    (count (.getItems merge-file-table))))

(defn merge-add-table-size-message
  []
  ;;;;;
  (history-add-textln (str "<<< File Database Elements => " (get-merge-table-size) " >>>")))

(defn merge-create-database-handler 
  "Initialize the file database SWT window, set the size add all components"
  [parent-sh open-win?]
  ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
  (history-add-textln "Opening file database screen (Tools -> Database Viewer)")
  (let [layout (create-merge-grid-layout)]
    (. merge-shell setText  *merge-window-title*)
    (merge-init-database-helper merge-shell)
    (. merge-shell setLayout layout)
    (. merge-shell setSize *merge-size-width* *merge-size-height*)
    (. merge-shell addShellListener
       (proxy [ShellAdapter][]
              (shellClosed [event]
                           (set! (. event doit) false)
                           (. merge-shell setVisible false))))
    (if open-win? (let []
                    (. merge-shell open)
                    (. merge-shell setVisible open-win?))
        (. merge-shell setVisible true))
    (*merge-lock-win-state*)
    ;; Load the XML database      
    (when-let [tabl-sz (get-merge-table-size)]
        (when (< tabl-sz 1)
          (merge-add-table-size-message)))))

(defn merge-create-database-window 
  "Initialize the file database SWT window, set the size add all components"
  [parent-sh open-win?]
  ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
  (history-add-textln "Opening file database screen (Tools -> Database Viewer)")
  (if (merge-win-not-loaded?)
    (merge-create-database-handler parent-sh open-win?)
    (.setVisible merge-shell true)))

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;; Merge a collection of files handlers

(defn merge-memory-handler
  [files]
  ;;;;;;;;
  (when files
    (doseq [file files]
        (let [msg-2 (str "Attempting to merge the following file to memory => " file)]
          (println msg-2)
          (.info *logger* msg-2)))
    ;; Continue with the logic to actually merge the files to memory
    (try (when-let [strm (merge-process-files-to-memory files)]
             (println "..."))
         (catch Exception e (let [err-msg (str "Error while performing memory merge =>" e)]
                              (.error *logger* err-msg)
                              (println err-msg))))))

(defn merge-memory-handler-primary
  "Handler for saving a file to memory with the merged files."
  []
  ;;;;;
  (let [prim-files (.getMergeFilesPrimary *main-global-state*)]
    (merge-memory-handler prim-files)))

(defn merge-memory-handler-secondary
  "Handler for saving a file to memory with the merged files."
  []
  ;;;;;
  (let [seco-files (.getMergeFilesPrimary *main-global-state*)]
    (merge-memory-handler seco-files)))

(def merge-set-primary-listener
     (proxy [SelectionAdapter] [] 
            (widgetSelected [event] (merge-memory-handler-primary))))

(def merge-set-secondary-listener
     (proxy [SelectionAdapter] [] 
            (widgetSelected [event] (merge-memory-handler-secondary))))

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

(defn quick-merge-files
  "Perform a quick merge"
  [files]
  ;;;;
  (when files
    (let [str-data (com.octane.util.FileUtils/concatFiles files 0 nil)]
      (async-add-main-text str-data))))

(defn quick-merge-primary
  "Perform a quick merge"
  []
  ;;;;
  (quick-merge-files (.getMergeFilesPrimary *main-global-state*)))
  
(defn quick-merge-secondary
  "Perform a quick merge"
  []
  ;;;
  (quick-merge-files (.getMergeFilesSecondary *main-global-state*)))

(def quick-merge-primary-listener
     (proxy [SelectionAdapter] [] 
            (widgetSelected [event] (quick-merge-primary))))

(def quick-merge-secondary-listener
     (proxy [SelectionAdapter] [] 
            (widgetSelected [event] (quick-merge-secondary))))

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