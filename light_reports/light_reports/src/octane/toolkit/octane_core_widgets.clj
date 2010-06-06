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
;;; Light Log was developed with a combination of Clojure 1.0, Java and Scala with use of libs, 
;;; SWT 3.4, JFreeChart, iText. 
;;; 
;;; Quickstart : the best way to run the Light Log viewer is to click on the 
;;; win32 batch script light_logs.bat
;;; (you may need to edit the Linux script for Unix/Linux environments).
;;; Edit the win32 script to add more heap memory or other parameters.

;;; The clojure source is contained in : HOME/src/octane
;;; The java source is contained in :  HOME/src/java/src

;;; To build the java source, see : HOME/src/java/build.xml and build_pdf_gui.xml

;;; Metrics: (as of 7/15/2009) Light Log Viewer consists of 6500 lines of Clojure code, and contains wrapper code
;;; around the Java source.  There are 2000+ lines of Java code in the Java library for Light Log Viewer.

;;; Additional Development Notes: The SWT gui and other libraries are launched from a dynamic classloader.  
;;; Clojure is also started from the same code, and reflection is used to dynamically initiate Clojure. 
;;; See the 'start' package.  The binary code is contained in the octane_start.jar library.

;;; Home Page: http://code.google.com/p/lighttexteditor/
;;;  
;;; Contact: Berlin Brown <berlin dot brown at gmail.com>
;;;

(ns octane.toolkit.octane_core_widgets
	(:use octane.toolkit.octane_utils_common
          octane.toolkit.octane_utils
          octane.toolkit.public_objects
          octane.toolkit.octane_config
          octane.toolkit.octane_main_constants
          octane.toolkit.octane_file_utils
          octane.toolkit.octane_gui_utils
          octane.toolkit.octane_tools
          octane.toolkit.octane_file_database
          octane.toolkit.octane_search_dialog
          octane.toolkit.octane_regex_search
          octane.toolkit.octane_archives
          octane.toolkit.octane_project_launcher
          octane.toolkit.octane_templates)
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
			 (org.eclipse.swt.events VerifyListener SelectionAdapter ModifyListener 
                                     SelectionListener
									 SelectionEvent ShellAdapter ShellEvent)
			 (org.eclipse.swt.widgets FileDialog DirectoryDialog MessageBox Composite)
			 (org.eclipse.swt SWT)
			 (org.eclipse.swt.widgets Display Shell Text Widget TabFolder TabItem)
			 (java.util ResourceBundle Vector Hashtable)
             (java.io BufferedReader File FileInputStream 
                      FileOutputStream BufferedOutputStream BufferedWriter
                      FileNotFoundException IOException InputStreamReader Reader PrintWriter)
             (java.util ResourceBundle Vector Hashtable)
             (org.eclipse.swt.widgets Display Shell Text Widget TabFolder TabItem)
             (java.util HashMap)))

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;; PDF handlers

(defn pdf-handler []
  "Open the HTML to PDF quick converter dialog box"
  ;; Dynamically import : (com.toolkit.util.gui SimpleXHtmlPDFWin)
  (.info *logger* "Launching PDF Handler")
  (try (let [my-shell *shell*
             settings (com.toolkit.util.gui.WinPDFSettings.)
             c "com.toolkit.util.gui.SimpleXHtmlPDFWin" ]
         (.setMainBufferWidget settings *styled-text*)
         (clojure.lang.Reflector/invokeStaticMethod c "createPDFWindowShellSettings" 
                                                    (to-array [ my-shell settings ])))
       (catch Exception ne (println "ERROR: Dynamic Loader error : " ne))))

(defn pdf-handler-deprecated- []
  ;; Dynamically import : (com.toolkit.util.gui SimpleXHtmlPDFWin)
  (.info *logger* "Launching PDF Handler")
  (try (let [my-shell *shell*]
         (com.octane.start.PDFDynamicStartWin/createPDFWindowShell my-shell))
       (catch Exception ne (println "ERROR: Dynamic Loader error : " ne))))

(def xhtml-to-pdf-listener
     (proxy [SelectionAdapter] []
			;; Open the find files dialog
			(widgetSelected [event] (pdf-handler))))

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

(defn create-tab-1 []
  (. tab-area-1 setText    tab-1-title)
  (. tab-area-1 setControl *styled-text*))

(defn create-tab-2 []
  (. tab-area-2 setText    tab-2-title)
  (. tab-area-2 setControl tab-text-2)
  (. tab-text-2 setFont    (styled-text-font)))

(defn create-tab-3 []
  (. tab-area-3 setText    tab-3-title)
  (. tab-area-3 setControl tab-text-3)
  (. tab-text-3 setFont    (styled-text-font)))

(defn create-tab-4 []
  (. tab-area-4 setText    tab-4-title)
  (. tab-area-4 setControl tab-text-4)
  (. tab-text-4 setFont    (styled-text-font)))

(defn create-all-tabs []
  (create-tab-1)
  (create-tab-2)
  (create-tab-3)
  (create-tab-4))

(def status-arm-listener
     (proxy [Listener] [] (handleEvent [event])))

(defn create-status-bar []
  (let [gd (new GridData SWT/FILL SWT/FILL true false)]
    (. status-bar setLayoutData gd)
    (. status-bar addListener SWT/Arm status-arm-listener)))

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;; Listeners and other misc action handlers
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

(def mkdirs-listener
     ;; Return a selection listener handler to a button
     ;; to be used for making directories
     (proxy [SelectionListener][]
            (widgetSelected [event] 
                            (win-simple-mkdirs-handler))
            (widgetDefaultSelected [event] (win-simple-mkdirs-handler))))

(def select-all-main-listener
     ;; Return a selection listener handler to a button
     ;; to be used for SELECT all
     ;; Note: select all applies to the main buffer only
     (proxy [SelectionListener][]
            (widgetSelected [event] 
                            (when *styled-text*
                              (.selectAll *styled-text*)))
            (widgetDefaultSelected [event]
                                   (when *styled-text*
                                     (.selectAll *styled-text*)))))

(defn show-heap-status-handler []
  ;; Return a selection listener handler to a button
  ;; to be used to print heap usage information
  (history-add-text (str (*memory-usage*) *newline*))
  (status-set-text  (str "Octane GUI " (date-time) " " (*memory-usage*))))

(def show-heap-status-listener
     ;; Return a selection listener handler to a button
     ;; to be used to print heap usage information
     (proxy [SelectionListener][]
            (widgetSelected [event] (show-heap-status-handler))
            (widgetDefaultSelected [event] (show-heap-status-handler))))

(defn next-tip-handler
  "Print the next tip to the history log"
  []
  ;;;
  (let [tip-2 (str (format-next-tip) \newline "--------" \newline)]
    (history-add-text tip-2)
    (status-set-text "Open the history tab to view the 'next tip'")))

(def next-tip-listener
     (proxy [SelectionListener][]
            (widgetSelected [event] (next-tip-handler))
            (widgetDefaultSelected [event] (next-tip-handler))))

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

(defn filter-term-buffer-handler
  []
  ;;;
  (let [disp #^org.eclipse.swt.widgets.Widget *display*
        buf-data (get-sync-call disp (.getText #^org.eclipse.swt.custom.StyledText *styled-text*))
        term (get-sync-call disp (.getText #^org.eclipse.swt.widgets.Text search-box))]
    (if (or (empty? term) (empty? buf-data))
      (println "debug: " "Invalid Filter Search")
      (when-let [new-buf-data (com.octane.util.StringUtils/filterTermBuffer buf-data term 0)]
          (add-main-text new-buf-data)))))

(def filter-term-listener     
     ;; Return the listener handler for
     ;; the filter text call
     (proxy [SelectionListener][]
            (widgetSelected [event] (filter-term-buffer-handler))
            (widgetDefaultSelected [event] (filter-term-buffer-handler))))
 
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;; History Save File Utilties
;; Save the history buffer to a text file.
;; Functionality added 6/11/2009
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

(defn history-save-as-file
  "With the history buffer, save to file"
  [save-filename]
  ;;;;;;
  ;; Make use of java.io FileOutputStream/BufferedOutputStream
  ;; to save to file
  (let [outs (FileOutputStream. save-filename)
        bufs (BufferedOutputStream. outs)
        pw   (PrintWriter. bufs)]
    (println "Saving file : " save-filename)
    (.println pw (.toString *history-buffer*))
    ;; Close the output-stream
    (.flush pw)
    (.close pw)))

(defn history-save-as-handler
  "With the history buffer, save to file"
  []
  ;;;;;;
  (if-let [cur-file (dialog-save-as-file)]
      (history-save-as-file cur-file)
    (async-status-history *display* "Please open a file to save")))

(defn history-save-handler
  "With the history buffer, save to file"
  []
  ;;;;;;
  (if-let [cur-file *save-history-log*]
      (history-save-as-file cur-file)
    (async-status-history *display* "Please open a file to save")))

(def history-save-as-listener
     ;; With the history buffer, save to file
     (proxy [SelectionListener][]
            (widgetSelected [event] (history-save-as-handler))
            (widgetDefaultSelected [event] (history-save-as-handler))))

(def history-save-listener
     ;; With the history buffer, save to file
     (proxy [SelectionListener][]
            (widgetSelected [event] (history-save-handler))
            (widgetDefaultSelected [event] (history-save-handler))))

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

(defn create-file-menu 
  "Add accelator CTRL + O, for OPEN MENU"
  [disp sh]
  ;;;;;;;;;;
  ;; Note change in 'doto' call, dot needed.
  ;; order of menuitem instances is important
  ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
  (let [#^ResourceBundle my-resources-win resources-win
        bar       (. sh getMenuBar)
        menu      (new Menu bar)
        item      (new MenuItem menu (. SWT PUSH))
        dir-item  (new MenuItem menu (. SWT PUSH))
        hsav-item (new MenuItem menu (. SWT PUSH))
        hist-item (new MenuItem menu (. SWT PUSH))
        all-item  (new MenuItem menu (. SWT PUSH))
        _         (new MenuItem menu SWT/SEPARATOR)
        run-expl  (new MenuItem menu (. SWT PUSH))
        cmd-item  (new MenuItem menu (. SWT PUSH))
        _         (new MenuItem menu SWT/SEPARATOR)]
    ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
    ;; BUILD FILEMENUITEMS
    ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
    (doto item
      ;;;;;;;;;;;;;;;;;;;;;;;;;;
      ;; Open File Menu Option
      ;;;;;;;;;;;;;;;;;;;;;;;;;
      (. setText (. #^ResourceBundle my-resources-win getString "Open_menuitem"))
	  (. setAccelerator (+ SWT/MOD1 (int \O)))
      (. addSelectionListener 
         (proxy [SelectionAdapter] []
                (widgetSelected [e] (dialog-open-file)
                                println "Opening File"))))
    (doto dir-item
      ;; Open the directory dialog box
      (. setText (. #^ResourceBundle my-resources-win getString "Opendir_menuitem"))
      (. addSelectionListener
         (proxy [SelectionAdapter] []
                (widgetSelected [e] (dialog-open-dir)))))
    (doto hsav-item
      ;; History item, save to HISTORY file
      (. setText (. #^ResourceBundle my-resources-win getString "HistorySave_menuitem"))
      (. setAccelerator SWT/F2)
      (. addSelectionListener history-save-listener))
    (doto hist-item
      ;; History item, save as
      (. setText (. #^ResourceBundle my-resources-win getString "HistorySaveAs_menuitem"))
      (. addSelectionListener history-save-as-listener))
    (doto all-item
      ;; Select All
      (. setText (. #^ResourceBundle my-resources-win getString "Select_all_menuitem"))
      (. setAccelerator (+ SWT/MOD1 (int \A)))
      (. addSelectionListener select-all-main-listener))
    (doto run-expl
      ;; Win explorer option
      ;; see octane_tools.clj for the file manager launcher
      (. setText (. my-resources-win getString "Filemanager_menuitem"))
	  (. setAccelerator (+ SWT/MOD1 (int \E)))
      (. addSelectionListener 
         (proxy [SelectionAdapter] []
                (widgetSelected [e] (start-filemanager-proc)))))
    (doto cmd-item
      ;; Win explorer option
      ;; see octane_tools.clj for the file manager launcher
      (. setText (. my-resources-win getString "Command_prompt_menuitem"))
	  (. setAccelerator (+ SWT/MOD1 (int \W)))
      (. addSelectionListener
         (proxy [SelectionAdapter] []
                (widgetSelected [e] (start-cmdprompt-proc)))))
    (doto (new MenuItem menu (. SWT PUSH))
      ;; Jar viewers
      (. setText (. my-resources-win getString "Jarfileview_menuitem"))
	  (. setAccelerator (+ SWT/MOD1 (int \J)))
      (. addSelectionListener 
         (proxy [SelectionAdapter] []
                (widgetSelected [e] (jar-viewer-handler)))))
    (new MenuItem menu SWT/SEPARATOR)
    (doto (new MenuItem menu (. SWT PUSH))
      (. setText (. my-resources-win getString "Make_dirs_menuitem"))
      (. addSelectionListener mkdirs-listener))
    ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
    ;; Create the recent file menu options
    ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
    (new MenuItem menu SWT/SEPARATOR)
    (create-recent-menu-items menu)    
    (new MenuItem menu SWT/SEPARATOR)
    ;; Create exit menu item last.
    (let [item-exit (new MenuItem menu (. SWT PUSH))]
      (doto item-exit
        (. setText (. my-resources-win getString "Exit_menuitem"))
		(. setAccelerator (+ SWT/MOD1 (int \Q)))
        (. addSelectionListener 
           (proxy [SelectionAdapter] []
                  (widgetSelected [evt] (exit) println "Exiting")))))
    menu))

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
    
(defn create-help-menu [disp sh]
  ;; Note change in 'doto' call, dot needed.
  (let [bar  (. sh getMenuBar)
        menu (new Menu bar)]
    (doto (new MenuItem menu (. SWT PUSH))
      (. setText (. resources-win getString "Next_random_tip_menuitem"))
      (. addSelectionListener next-tip-listener))
    (new MenuItem menu SWT/SEPARATOR)
    (doto (new MenuItem menu (. SWT PUSH))
      (. setText (. resources-win getString "About_menuitem"))
      (. addSelectionListener
         (proxy [SelectionAdapter] []
                (widgetSelected [event] (create-about-messagebox sh)))))
    menu))

(defn create-tools-menu 
  "Create and add the tools menu items. Add the accelerator CTRL - D for the database viewer"
  [disp sh]
  ;; Note change in 'doto' call, dot needed.
  (let [#^ResourceBundle my-resources-win resources-win
        bar (. sh getMenuBar)
        menu (new Menu bar)]
    ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
    ;; POPULATE MENU ITEMS FOR TOOLS
    ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
    (doto (new MenuItem menu (. SWT PUSH))
      (.setText (. resources-win getString "Tools_pdf_menuitem"))
      (.setAccelerator SWT/F12)
      (.addSelectionListener xhtml-to-pdf-listener))
    (doto (new MenuItem menu (. SWT PUSH))
      (.setText (. my-resources-win getString "Database_viewer_menuitem"))
	  (.setAccelerator (+ SWT/MOD1 (int \D)))
      (.addSelectionListener
         (proxy [SelectionAdapter] []
                (widgetSelected [event] (create-database-window *shell* true)))))
	(doto (new MenuItem menu (. SWT PUSH))
	  (.setText (. my-resources-win getString "File_properties_menuitem"))
	  (.setAccelerator (+ SWT/MOD1 (int \I)))
	  (.addSelectionListener win-file-prop-listener))
	(doto (new MenuItem menu (. SWT PUSH))
	  (.setText (. my-resources-win getString "Search_statistics_menuitem")))
    (doto (new MenuItem menu (. SWT PUSH))
      (.setText (. my-resources-win getString "ShowHeapStatus_menuitem"))
      (.addSelectionListener show-heap-status-listener))
    (doto (new MenuItem menu (. SWT PUSH))
      (.setText (. my-resources-win getString "RunProjectWorkspace_menuitem"))
      (.addSelectionListener run-projects-listener))
    ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
    ;; Continue to separator, additional tools and actions
    ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
    (new MenuItem menu SWT/SEPARATOR)
    ;; END OF ADD MENU ITEMS, RETURN ;;
    menu))

(defn create-search-menu 
  "Add accelator CTRL + O, for FIND FILE"
  [disp sh]
  ;; Note change in 'doto' call, dot needed.
  (let [bar (. sh getMenuBar)
            menu (new Menu bar)
            item (new MenuItem menu (. SWT PUSH))
            filter-item (new MenuItem menu (. SWT PUSH))
            regex-item  (new MenuItem menu (. SWT PUSH))]	
    (doto item
      (. setText (. resources-win getString "Find_menuitem"))
	  (. setAccelerator (+ SWT/MOD1 (int \F)))
      (. addSelectionListener
         (proxy [SelectionAdapter] []
                (widgetSelected [event] (create-search-dialog sh)))))    
    (doto filter-item
      (. setText (. resources-win getString "Filter_search_term_menuitem"))
      (.addSelectionListener filter-term-listener))
    (doto regex-item
      (. setText (. resources-win getString "Regextool_menuitem"))
      (. addSelectionListener
         (proxy [SelectionAdapter] []
                (widgetSelected [event] (create-regex-window)))))
    ;; Add the 'find/grep' menu items
    (add-findgrep-options menu)
    menu))

(defn quick-open-file
  [user-prop-key]
  ;;;
  (let [default-file "\\\\tmp\\logs\\serv01\\Out.log"
        fname (if (empty? (prop-str resources-user user-prop-key)) 
                default-file (prop-str resources-user user-prop-key))]
    (open-file fname false)))

(defn quick-file-menu-listener
  [quick-prop-key]
  ;;;;;;;;;;;;;;;;;
  (proxy [SelectionAdapter] []
         (widgetSelected [evt] (quick-open-file quick-prop-key))))                         

(defn add-buffer-menuitems [menu]
  (doto (new MenuItem menu (. SWT CASCADE))
	(. setText (res-win-str "Swap_buffer_to_second_menuitem"))
	(. setAccelerator SWT/F4)
	(. addSelectionListener swap-buffer-to-listener))
  (doto (new MenuItem menu (. SWT CASCADE))
	(. setText (res-win-str "Swap_buffer_from_second_menuitem"))
	(. setAccelerator SWT/F3)
	(. addSelectionListener swap-buffer-from-listener))
  ;; Add the quick buffer items
  (doto (new MenuItem menu (. SWT CASCADE))
    (. setText (res-win-str "Quick_file1_menuitem"))
	(. setAccelerator SWT/F5)
	(. addSelectionListener (quick-file-menu-listener "Quick_key_file1")))
  (doto (new MenuItem menu (. SWT CASCADE))
    (. setText (res-win-str "Quick_file2_menuitem"))
	(. setAccelerator SWT/F6)
	(. addSelectionListener (quick-file-menu-listener "Quick_key_file2")))
  (doto (new MenuItem menu (. SWT CASCADE))
    (. setText (res-win-str "Quick_file3_menuitem"))
	(. setAccelerator SWT/F7)
	(. addSelectionListener (quick-file-menu-listener "Quick_key_file3")))
  (new MenuItem menu SWT/SEPARATOR))

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

(defn create-menu-bar [disp sh]
  (let [#^ResourceBundle my-resources-win resources-win
        bar  (new Menu sh (. SWT BAR))
        item (new MenuItem bar (. SWT CASCADE))
        search-item    (new MenuItem bar (. SWT CASCADE))
        recent-buffers (new MenuItem bar (. SWT CASCADE))
        tools-item     (new MenuItem bar (. SWT CASCADE))
        help-item      (new MenuItem bar (. SWT CASCADE))]        
    (. sh setMenuBar bar)
    ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
    ;; Build the Main Window Menu Bar
    ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
    (doto item
      (. setText (. my-resources-win getString "File_menuitem"))
      (. setMenu (create-file-menu disp sh)))    
    (doto help-item
      (. setText (. my-resources-win getString "Help_menu_title"))
      (. setMenu (create-help-menu disp sh)))
    (doto search-item
      (. setText (. my-resources-win getString "Search_menu_title"))
      (. setMenu (create-search-menu disp sh)))
    (doto tools-item
      (. setText (. my-resources-win getString "Tools_menu_title"))
      (. setMenu (create-tools-menu disp sh)))
    (let [buf-menu (new Menu bar)]
	  (add-buffer-menuitems buf-menu)
      (doto recent-buffers
        (. setText (. my-resources-win getString "RecentBuffers_menu_title"))
        (. setMenu buf-menu))
      (set-buffer-menu-state buf-menu))))

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