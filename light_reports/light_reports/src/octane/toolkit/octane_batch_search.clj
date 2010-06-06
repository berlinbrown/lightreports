;;;
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
;;;

(ns octane.toolkit.octane_batch_search
	(:use octane.toolkit.octane_utils_common
          octane.toolkit.octane_utils
          octane.toolkit.public_objects
          octane.toolkit.octane_gui_utils
          octane.toolkit.octane_config
          octane.toolkit.octane_tools
          octane.toolkit.octane_file_utils
          octane.toolkit.octane_archives)
	(:import 
	 (java.io File)
	 (org.eclipse.swt SWT)
	 (org.eclipse.swt.widgets Display Shell Text Widget TabFolder TabItem)
	 (org.eclipse.swt.widgets FileDialog MessageBox TableItem Button
							  Composite Table TableColumn)
	 (org.eclipse.swt.layout GridData GridLayout RowLayout RowData)
	 (org.eclipse.swt.events VerifyListener SelectionAdapter ModifyListener SelectionListener
							 SelectionEvent ShellAdapter ShellEvent)
	 (org.eclipse.swt.widgets Label Menu MenuItem Control Listener)))

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

(def  *batch-win-state*      (com.octane.util.ReadOnlyState.))
(defn *batch-lock-win-state* [] (.lock *batch-win-state*))
(defn *batch-get-win-state*  [] (.getState *batch-win-state*))
(defn batch-win-loaded?      [] (*batch-get-win-state*))
(defn batch-win-not-loaded?  [] (not (*batch-get-win-state*)))

(def on-search-archive-listener)

(def on-exractonly-archive-listener)

(def *batch-search-style* (bit-or SWT/CLOSE (bit-or SWT/BORDER (bit-or SWT/TITLE 1))))

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;; Create the Widgets
;; Will be positioned on order of creation
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
(def *batch-search-shell* (new Shell *shell* *batch-search-style*))

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;; Layout:
;; (1) Find Text Box
;; (2) Date Box
;; (3) Search Term Replace
;; (4) Server Selection
;; (5) Filename Pattern
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
(def *batch-find-label*       (new Label *batch-search-shell* SWT/LEFT))
(def *batch-find-box*         (new Text  *batch-search-shell* SWT/BORDER))

(def *batch-date-label*       (new Label *batch-search-shell* SWT/LEFT))
(def *batch-date-box*         (new Text  *batch-search-shell* SWT/BORDER))

(def *batch-fromtime-label*   (new Label *batch-search-shell* SWT/LEFT))
(def *batch-fromtime-box*     (new Text  *batch-search-shell* SWT/BORDER))

(def *batch-totime-label*     (new Label *batch-search-shell* SWT/LEFT))
(def *batch-totime-box*       (new Text  *batch-search-shell* SWT/BORDER))

(def *batch-term-label*       (new Label *batch-search-shell* SWT/LEFT))
(def *batch-term-box*         (new Text  *batch-search-shell* SWT/BORDER))

(def *batch-server-label*     (new Label *batch-search-shell* SWT/LEFT))
(def *batch-server-box*       (new Text  *batch-search-shell* SWT/BORDER))

(def *batch-pattern-label*    (new Label *batch-search-shell* SWT/LEFT))
(def *batch-pattern-box*      (new Text  *batch-search-shell* SWT/BORDER))

(def *regex-batch-label*      (new Label  *batch-search-shell* SWT/LEFT))
(def *regex-batch-check-box*  (new Button *batch-search-shell* SWT/CHECK))

(def *casesens-batch-label*     (new Label  *batch-search-shell* SWT/LEFT))
(def *casesens-batch-check-box* (new Button *batch-search-shell* SWT/CHECK))

(def *process-batch-label*      (new Label  *batch-search-shell* SWT/LEFT))
(def *process-batch-check-box*  (new Button *batch-search-shell* SWT/CHECK))

;; Adding dry run and extract only functionality
;; Dry run allows to just print the operations
;; Extract only extracts the files to the tmp directory
(def *batch-composite*          (new Composite *batch-search-shell* SWT/NONE))
(def *batch-find-button*        (new Button    *batch-composite* SWT/PUSH))
(def *batch-extract-button*     (new Button    *batch-composite* SWT/PUSH))
(def *batch-dryrun-button*      (new Button    *batch-composite* SWT/PUSH))
(def *batch-close-button*       (new Button    *batch-composite* SWT/PUSH))
(def *batch-status-label*       (new Label     *batch-search-shell* (bit-or SWT/LEFT (bit-or SWT/BORDER 1))))

;; Initial Edit Box Values
(def *init-server-box*    (if (empty? (prop-str resources-user "Archive_search_servers")) "" (prop-str resources-user "Archive_search_servers")))
(def *init-file-pattern*  "^SystemOut.*$")
(def *init-from-time*     "00:01")
(def *init-to-time*       "23:59")
(def *init-term-replace*  "%LOGS% -> serv01, %SERVER% -> tmp ; %LOGS% -> serv01, %SERVER% -> tmp")

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

(defn create-batch-grid-layout []
  (let [gridLayout (new GridLayout)]
    (set! (. gridLayout marginHeight)  8)
    (set! (. gridLayout marginWidth)  10)
    (set! (. gridLayout numColumns) 2) gridLayout))

(def uncompress-on-close-listener
     (proxy [SelectionListener][]
            (widgetSelected [event]
                            (set! (. event doit) false)
                            (. *batch-search-shell* setVisible false))
            (widgetDefaultSelected [event]
                                   (set! (. event doit) false)
                                   (. *batch-search-shell* setVisible false))))

(defn uncompress-label-set-text
  "Helper function to set the search label text"
  []
  ;;;
  (. *batch-find-label*       setText (prop-str resources-win "Search_for_label"))
  (. *batch-date-label*       setText (prop-str resources-win "Uncompress_Search_date_label"))
  (. *batch-term-label*       setText (prop-str resources-win "Uncompress_Search_termrepl_label"))
  (. *batch-server-label*     setText (prop-str resources-win "Uncompress_Search_path_label"))
  (. *batch-pattern-label*    setText (prop-str resources-win "Dialog_Search_ext_label"))
  (. *regex-batch-label*      setText (prop-str resources-win "Dialog_Search_regex_label"))
  (. *casesens-batch-label*   setText (prop-str resources-win "Dialog_Search_case_label"))
  (. *batch-fromtime-label*   setText (prop-str resources-win "Dialog_Search_fromtime_label"))
  (. *batch-totime-label*     setText (prop-str resources-win "Dialog_Search_totime_label"))
  (. *process-batch-label*    setText (prop-str resources-win "Dialog_Search_enableproc_label"))
  ;; Also set the text data for initial query terms
  (. *batch-status-label*     setText (str "Batch Archive Search Opened - " (date-time)))
  (. *batch-server-box*       setText *init-server-box*)
  (. *batch-pattern-box*      setText *init-file-pattern*)
  (. *batch-fromtime-box*     setText *init-from-time*)
  (. *batch-totime-box*       setText *init-to-time*)
  (. *batch-term-box*         setText *init-term-replace*)
  (. *batch-date-box*         setText (str *current-date*)))

(defn init-uncompress-buttons
  "Set the default properties for the search buttons"
  []
  ;; Set the composite buttons
  (let [rowd-find (new RowData 140 24)]
    (. *batch-find-button*   setText "Search Archives")
    (. *batch-find-button*   setLayoutData rowd-find)
    (. *batch-find-button*   setEnabled true)
    (. *batch-find-button*   addSelectionListener on-search-archive-listener)
    ;; Extract button
    (. *batch-extract-button*   setText "Extract Archives")
    (. *batch-extract-button*   setLayoutData rowd-find)
    (. *batch-extract-button*   setEnabled true)
    (. *batch-extract-button*   addSelectionListener on-exractonly-archive-listener)
    ;; Dry Run Button
    (. *batch-dryrun-button*   setText "Dry Run")
    (. *batch-dryrun-button*   setLayoutData rowd-find)
    (. *batch-dryrun-button*   setEnabled true)
    ;;(. *batch-dryrun-button*   addSelectionListener on-search-archive-listener)
    ;; continue to close
    (. *batch-close-button*  setText "Close")
    (. *batch-close-button*  setLayoutData rowd-find)
    (. *batch-close-button*  setEnabled true)
    (. *batch-close-button*  addSelectionListener uncompress-on-close-listener)))

(defn init-uncompress-textbox
  [#^GridData gd]
  ;;;;;;;;;;;;;;;  
  (. *batch-find-box* setLayoutData gd)
  (let [gd-text (new GridData SWT/FILL SWT/NONE true false)]
	(. *batch-date-box*      setLayoutData gd-text)
	(. *batch-term-box*      setLayoutData gd-text)
	(. *batch-server-box*    setLayoutData gd-text)
    (. *batch-fromtime-box*  setLayoutData gd-text)
    (. *batch-totime-box*    setLayoutData gd-text)
	(. *batch-pattern-box*   setLayoutData gd-text)))

(defn init-uncompress-search-helper
  "Create the layout and place with the widgets for the search box"
  [sh]
  ;;;;;;;;;
  (let [gd-textbox (new GridData GridData/FILL_HORIZONTAL)
				   gd-composite   (new GridData SWT/NONE)
				   gd-status-bar  (new GridData SWT/FILL SWT/FILL true false 2 1)
				   comp-layout    (new RowLayout)]
	(uncompress-label-set-text)
	(set! (. gd-textbox widthHint) 280)
	(. *batch-status-label* setLayoutData gd-status-bar)
	(init-uncompress-textbox gd-textbox)
	;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
	;; Set the button composite widget
	;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
    (set! (. gd-composite horizontalSpan) 2)
    ;; verticalAlignment specifies how controls will be positioned vertically within a cell
    (set! (. gd-composite horizontalAlignment) SWT/LEFT)
    (. *batch-composite* setLayoutData gd-composite)
    ;; Position the buttons a couple of pixels away.
    (set! (. comp-layout marginTop)  4)
    (set! (. comp-layout marginLeft) 2)
    (. *batch-composite* setLayout comp-layout)	
	(init-uncompress-buttons)))

(defn create-batchfind-handler
  "Initialize the file database SWT window, set the size add all components"
  []
  ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;  
  (history-add-textln "Opening batch uncompress screen (Search -> Open New Find in Files)")
  (let [layout (create-batch-grid-layout)]
	(. *batch-search-shell* setText "Batch Uncompress and Find")
	(init-uncompress-search-helper *batch-search-shell*)
	(. *batch-search-shell* setLayout layout)
	(. *batch-search-shell* addShellListener
	   (proxy [ShellAdapter][]
			  (shellClosed [event]
						   (set! (. event doit) false)
						   (. *batch-search-shell* setVisible false))))
	(. *batch-search-shell* pack)
	(. *batch-search-shell* open)
	(. *batch-search-shell* setVisible true)))

(defn create-batchfind-dialog 
  "Initialize the file database SWT window, set the size add all components"
  []
  ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
  (if (batch-win-not-loaded?)
    (do (create-batchfind-handler)
        (*batch-lock-win-state*))
    (do (.open *batch-search-shell*)
        (.setVisible *batch-search-shell* true))))

(def uncompress-search-listener
     (proxy [SelectionAdapter] []
			;; Open the find files dialog
			(widgetSelected [event] (create-batchfind-dialog))))

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;; Main Event Handlers for When the user presses the 'Search' Button
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

(defn win-archive-search-listfiles
  [#^String dir-filename file-pattr term]
  ;;;;;;;;;;;;;;;;;;;;
  (doseq [file (.listFiles (new File dir-filename))]
      (let [res (archive-search-grep (.getAbsolutePath file) term)]
        (when (not (empty? res))
          (try (async-call *display*
                           (add-main-text-nc (archive-search-grep (.getAbsolutePath file) term)))
               (catch Exception e
                      (history-add-textln (str "ERR <archive-search listfiles> : " (.getMessage e)))))))))

(defn bydate-stream-zfile-handler
  "Implementation of a stream zfile handler.
 TODO: fix bug with creating text file when not needed"
  [my-date-str pattrn & [start-t end-t]]
  ;;;;;;;;;;;;;;;;;;;;;
  (stream-zfile-handler my-date-str
                        (fn [#^java.io.File file #^java.io.FileOutputStream fos]
                            (let [has-pattr? (simple-grep-regex? (.getName file) pattrn)]
                              ;; Only stream when then regex pattern is found
                              (when has-pattr?
                                (let [msg-2 (str (str "Streaming uncompressed file to tmp directory, filename pattern => " pattrn))]
                                  (add-main-text-nc msg-2)
                                  (async-status-history *display* msg-2)
                                  (println msg-2)
                                  (.info *logger* (str msg-2 \newline)))
                                (stream-compressed-file file fos)
                                (add-main-text-nc (str "Done Streaming file =>" (.getAbsolutePath file))))))
                        (fn [#^java.io.File file]
                            (simple-grep-regex? (.getName file) pattrn))
                        start-t end-t))
                           
(defn begin-search-archive
  [filename-dir date-str file-pattr search-term & [start-t end-t disable-search? enable-proc?]]
  ;;;;;;;;;;;;;;
  ;; Stream the files to the tmp directory
  (let [tmp-dir (create-archive-work-dir date-str)]
    (let [msg-2 (str "DEBUG: begin-search-archive dir=> " filename-dir " date-str =>" date-str " pattern=>" file-pattr)]
      (println msg-2) (.info *logger* (str msg-2 \newline)))
	(zfiles-dir-handler filename-dir (bydate-stream-zfile-handler date-str file-pattr start-t end-t) tmp-dir)
    ;; Perform the archive search
    (if (not disable-search?)
      (if (not enable-proc?)
        (win-archive-search-listfiles tmp-dir file-pattr search-term)
        (do (async-status-history *display* (str "Archive search enabling processing tmp directory  => " tmp-dir))
            (start-findgrep-cmd tmp-dir "*.*" search-term)))
      (async-status-history *display* (str "Archive search disabled, see the tmp directory  => " tmp-dir)))))

(defn build-get-batch-text
  "Build a data structure to get the batch text elements.
 Return a data structure with the following elemnts:
 :term    - search term
 :date    - date
 :pattern - 
 :path    - server path"
 [disp]
 ;;;;;;;
 {
 :term        (get-sync-call disp (.getText *batch-find-box*))     
 :search-repl (get-sync-call disp (.getText *batch-term-box*))
 :date        (get-sync-call disp (.getText *batch-date-box*))
 :pattern     (get-sync-call disp (.getText *batch-pattern-box*))
 :path        (get-sync-call disp (.getText *batch-server-box*))
 :fromtime    (get-sync-call disp (.getText *batch-fromtime-box*))
 :totime      (get-sync-call disp (.getText *batch-totime-box*))
 :enable-proc (get-sync-call disp (.getSelection *process-batch-check-box*))
 })

(defn on-search-archive-handler
  "When the user selects the find next button, invoke this find next handler.
 Search the main buffer for the term in the 'find' box."
  [event & disable-search?]
  ;;;;;;;;;;;;
  (let [disp     (.getDisplay *batch-search-shell*)
        props    (build-get-batch-text disp)
        term         (:term     props)
        date-str     (:date     props)
        pattrn       (:pattern  props)
        start-t      (:fromtime props)
        end-t        (:totime   props)
        sr-repl      (:search-repl props)
        enable-proc? (:enable-proc props)
        servpath     (:path     props)]
    ;; Enable the archive handler logic when the search is NOT empty
    ;; or when the disable search functionality
    (if (or (not (empty? term)) disable-search?)
      (do
        ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
        ;; Set the status messages
        ;; And continue with search
        ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
        (async-add-main-text (str "<<< Begin Archive Search >>> process-enabled flag =>" enable-proc? \newline))
        (async-status-history *display* (str "Begin Archive Search For (Please wait for process to complete) => " 
                                             term " date: (" date-str ") at " (date-time)))
        (.setText *batch-status-label* "Begin Archive Search")
        ;; Add exception handler at high level, something went wrong click.
        ;; If the search/replace is empty, do the regular search archive
        ;; otherwise
        (try (if (empty? sr-repl) (begin-search-archive servpath date-str pattrn term start-t end-t disable-search? enable-proc?)
                 (doseq [sp (archive-search-replace servpath sr-repl)]
                     (async-status-history *display* (str "Archive search, processing server path =>" sp))
                   (begin-search-archive sp date-str pattrn term start-t end-t disable-search? enable-proc?)))
             (catch Exception e
                    (async-status-history *display* (str "System error on search archive => " (.getMessage e)))
                    (.printStackTrace e)))
        (.setText *batch-status-label* (str "End Archive Search at " (date-time))))
      (do (.setText *batch-status-label* "Please Enter a Search Term")))))

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;           

(def on-search-archive-listener
     (proxy [SelectionListener][]
            (widgetSelected [event] (on-search-archive-handler event))
            (widgetDefaultSelected [event] (on-search-archive-handler event))))

(def on-exractonly-archive-listener
     (proxy [SelectionListener][]
            (widgetSelected [event] (on-search-archive-handler event true))
            (widgetDefaultSelected [event] (on-search-archive-handler event true))))

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
;;; End of Script
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;      