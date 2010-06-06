;;;
;;; Copyright (c) 2006-2010 Berlin Brown and botnode.com  All Rights Reserved
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

(ns octane.toolkit.octane_search_dialog
	(:use octane.toolkit.octane_utils_common
          octane.toolkit.octane_utils
          octane.toolkit.public_objects
          octane.toolkit.octane_gui_utils
          octane.toolkit.octane_config
          octane.toolkit.octane_tools
          octane.toolkit.octane_findfiles_dialog
          octane.toolkit.octane_batch_search)
	(:import 
	 (org.eclipse.swt SWT)
	 (org.eclipse.swt.widgets Display Shell Text Widget TabFolder TabItem)
	 (org.eclipse.swt.widgets FileDialog MessageBox TableItem Button
							  Composite Table TableColumn)
	 (org.eclipse.swt.layout GridData GridLayout RowLayout RowData)
	 (org.eclipse.swt.events VerifyListener SelectionAdapter ModifyListener SelectionListener
							 SelectionEvent ShellAdapter ShellEvent)
	 (org.eclipse.swt.widgets Label Menu MenuItem Control Listener)))

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

(def  *search-win-state*      (com.octane.util.ReadOnlyState.))
(defn *search-lock-win-state* [] (.lock *search-win-state*))
(defn *search-get-win-state*  [] (.getState *search-win-state*))
(defn search-win-loaded?      [] (*search-get-win-state*))
(defn search-win-not-loaded?  [] (not (*search-get-win-state*)))

(def *search-style* (bit-or SWT/CLOSE (bit-or SWT/BORDER (bit-or SWT/TITLE 1))))
  
(defn set-find-next-matcher
  "Create a new regex matcher for use with 'find-next'"
  [doc regex-str-term]
  ;;;;;;;;;;;;;;;;;;;;;
  (set-find-next-state (new-find-next-matcher doc regex-str-term)))

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;; Create the Widgets
;; Will be positioned on order of creation
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
(def search-shell          (new Shell *shell* *search-style*))
(def search-label          (new Label search-shell SWT/LEFT))
(def search-filter-box     (new Text search-shell SWT/BORDER))

(def regex-find-label      (new Label search-shell SWT/LEFT))
(def regex-check-box       (new Button search-shell SWT/CHECK))
(def casesens-find-label   (new Label search-shell SWT/LEFT))
(def casesens-check-box    (new Button search-shell SWT/CHECK))

(def search-composite      (new Composite search-shell SWT/NONE))
(def search-find-button    (new Button search-composite SWT/PUSH))
(def search-clear-button   (new Button search-composite SWT/PUSH))
(def search-close-button   (new Button search-composite SWT/PUSH))
(def search-status-label   (new Label search-shell (bit-or SWT/LEFT (bit-or SWT/BORDER 1))))

(defn create-search-grid-layout []
  (let [gridLayout (new GridLayout)]
    (set! (. gridLayout marginHeight)  8)
    (set! (. gridLayout marginWidth)  10)
    (set! (. gridLayout numColumns) 2) gridLayout))

(defn search-label-set-text
  "Helper function to set the search label text"
  []
  ;;;
  (. search-label setText (prop-str resources-win "Search_for_label"))
  (. regex-find-label setText "Regular Expressions:")
  (. casesens-find-label setText "Case Sensitive:")
  (. search-status-label setText (str "Find Keyword Dialog Opened - " (date-time))))

(def search-on-close-listener
     (proxy [SelectionListener][]
            (widgetSelected [event]
                            (set! (. event doit) false)
                            (. search-shell setVisible false))
            (widgetDefaultSelected [event]
                                   (set! (. event doit) false)
                                   (. search-shell setVisible false))))

(defn on-found-term-handler
  "Handler for when a term is found"
  [sdisp m term text]
  ;;;;;;;;;;;;;;;;;
  (try (Thread/sleep 40) 
       (let [msg-2 (str "Found term => " term " at " (. m start))]
         (println msg-2)
         (. search-status-label setText (str "Found term => " term " at " (. m start))))
       (. *styled-text* setSelection (. m start))
       (refresh-textarea)
       (catch Exception e 
              (println "ERR: On Found Term Handler : " e)
              (.printStackTrace e)
              (println "----------------"))))

(defn build-new-search-matcher
  "If a new search term is found, determine if we need to
 build a new matcher object"
  [fns text term]
  ;;;;;;;;;;;;;;;;
  ;; If the terms are different then new search term
  (let [m    (if fns fns (new-find-next-matcher text term))
        new? (if fns false true)]
    ;; If new, then return the matcher, otherwise...
    (if new? m
        (when m
          (try (let [mterm (.group m)]
                 ;; If the matcher term is the same as the search term
                 ;; we are in default mode, otherwise, new matcher
                 (if (= mterm term) m
                     (new-find-next-matcher text term)))
               (catch Exception e 
                      (println "debug-err: build matcher : " e " matcher:" m)
                      m))))))

(defn print-msg-find-next [text term]
  ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
  ;; On ERR:
  ;; Send status error message, could not find
  ;; (Bug with branching logic)
  ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
  (if (empty? term)
    (.setText search-status-label "Invalid Search Term (empty)")
    (.setText search-status-label "Invalid Search Term (empty)"))
  (if (empty? text)
    (.setText search-status-label "Invalid Buffer Document (empty)")
    (.setText search-status-label "Invalid Buffer Document (empty)")))
            
(defn search-find-next-handler
  "When the user selects the find next button, invoke this find next handler.
 Search the main buffer for the term in the 'find' box."
  [event]
  ;;;;;;;;;;;;
  (let [disp (.getDisplay search-shell)
        term (.getText search-filter-box)
        text (.getText *styled-text*)]
    (if (and disp (not (empty? term)) (not (empty? text)))
      ;; Create the find next matcher from the document and term
      (let [fns (get-find-next-state)
            m   (build-new-search-matcher fns text term)]
        (when m
          (println (str "debug: search find [search dialog] : " fns " m:" m " term:" term " end:" (.hitEnd m)))
          ;; Also set the 'quick' search text bar at the bottom of the main window
          (async-call *display* (.setText search-box term))
          (async-call *display* (update-textarea))
          ;; Set the public matcher object, if it doesnt exist
          (set-find-next-state m)
          (if (.find m)
            (on-found-term-handler disp m term text)
            ;; Otherwise case, not one term found
            (do (.setText search-status-label (str "Could not find term => " term))
                (.reset m text)))))
      (print-msg-find-next text term))))

(defn search-find-clear-handler
  "Clear the find criteria"
  [event]
  ;;;;;;;;;;;;;;;;;;
  (async-call *display* (.setText search-filter-box ""))
  (clear-find-next-state))              
  
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

(def search-find-clear-listener
     ;; Clear the find selection
     (proxy [SelectionListener][]
            (widgetSelected [event] (search-find-clear-handler event))
            (widgetDefaultSelected [event] (search-find-clear-handler event))))

(def search-find-next-listener
     (proxy [SelectionListener][]
            (widgetSelected [event] (search-find-next-handler event))
            (widgetDefaultSelected [event] (search-find-next-handler event))))

(def search-find-next-traverse
     (proxy [Listener][]
            (handleEvent [event] (search-find-next-handler event))))

(defn init-search-buttons
  "Set the default properties for the search buttons"
  []
  ;; Set the composite buttons
  (let [rowd-find (new RowData 98 24)]
    (. search-find-button  setText "Find Next")
    (. search-find-button  setLayoutData rowd-find)
    (. search-find-button  setEnabled true)
    (. search-find-button  addSelectionListener search-find-next-listener)
    ;; Continue to clear button
    ;;;;;;;;;;;;;;;;;;;;;;;;;;;
    (.setText       search-clear-button "Clear")
    (.setLayoutData search-clear-button rowd-find)
    (.setEnabled    search-clear-button true)
    (.addSelectionListener search-clear-button search-find-clear-listener)
    ;; Also add the on return listener to invoke 'find next'
    (. search-filter-box   addListener SWT/Traverse search-find-next-traverse)
    (. search-close-button setText "Close")
    (. search-close-button setLayoutData rowd-find)
    (. search-close-button setEnabled true)
    (. search-close-button addSelectionListener search-on-close-listener)))

(defn init-search-helper
  "Create the layout and place with the widgets for the search box
 Notes On Grid Data (useful for search widgets):
 GridData(int horizontalAlignment, int verticalAlignment, boolean grabExcessHorizontalSpace, 
               boolean grabExcessVerticalSpace, int horizontalSpan, int verticalSpan)"
  [sh]
  ;;;;;
  (let [gd-textbox (new GridData GridData/FILL_HORIZONTAL)
        gd-composite   (new GridData SWT/NONE)
        gd-status-bar  (new GridData SWT/FILL SWT/FILL true false 2 1)
        comp-layout    (new RowLayout)]
    (search-label-set-text)
    (set! (. gd-textbox widthHint) 200)
    (. search-filter-box setLayoutData gd-textbox)
    (. search-status-label setLayoutData gd-status-bar)
    ;; Set the button composite widget
    (set! (. gd-composite horizontalSpan) 2)
    ;; verticalAlignment specifies how controls will be positioned vertically within a cell
    (set! (. gd-composite horizontalAlignment) SWT/LEFT)
    (. search-composite setLayoutData gd-composite)
    ;; Position the buttons a couple of pixels away.
    (set! (. comp-layout marginTop)  4)
    (set! (. comp-layout marginLeft) 2)
    (. search-composite setLayout comp-layout)    
    (init-search-buttons)
	;; Check the state of the matcher, if available, reset.
	(let [fns (get-find-next-state)] (when fns (.reset fns)))))
			  
;;;;;;;;;;;;;;;;;;;;
;; End of Function
;;;;;;;;;;;;;;;;;;;;

(defn create-search-handler
  "Initialize the file database SWT window, set the size add all components"
  [parent-sh]
  ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
  (history-add-textln "Opening search screen (Search -> Find)")
  (let [layout (create-search-grid-layout)]
    (. search-shell setText (. resources-win getString "Find_dialog_title"))
    (init-search-helper search-shell)
    (. search-shell setLayout layout)
    (. search-shell addShellListener
       (proxy [ShellAdapter][]
              (shellClosed [event]
                           (set! (. event doit) false)
                           (. search-shell setVisible false))))
    (. search-shell pack)
    (. search-shell open)
    (. search-shell setVisible true)))

(defn create-search-dialog
  "Initialize the file database SWT window, set the size add all components"
  [parent-sh]
  ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
  (if (search-win-not-loaded?)
    (do (create-search-handler parent-sh)
        (*search-lock-win-state*))
    (do (.open search-shell)
        (.setVisible search-shell true))))

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;; Additional Search Utilities
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

(def findgrep-listener
     (proxy [SelectionAdapter] []
            (widgetSelected [event]
                            ;; With the async call, set the agent value
                            ;; based on the search box value                            
                            (. *display* syncExec 
                               (proxy [Runnable] []
                                      (run [] 
                                           (let [val (. search-box getText)]
                                             (send *search-text-state* (fn [_] val))))))
                            (let [widg-str (str (. event widget))]
                              (. (new Thread
                                      (start-findgrep-thread widg-str @*search-text-state* true)) start)))))

(def new-findfiles-listener
     (proxy [SelectionAdapter] []
			;; Open the find files dialog
			(widgetSelected [event]
							(create-findfiles-dialog *shell*))))
                                                   
(defn add-findgrep-options [menu]
  ;; Load the normal find/grep menu items
  (let [item-findgrep (create-menu-item menu "FindGrep_newgrep_menuitem" new-findfiles-listener)]
	(. item-findgrep setAccelerator (+ SWT/MOD1 (int \G))))
  (let [item2 (create-menu-item menu "Search_archivebydate_menuitem" uncompress-search-listener)]
	(. item2 setAccelerator (+ SWT/MOD1 (int \U))))
  (new MenuItem menu SWT/SEPARATOR)
  ;; Run non lazy sequence operation
  (doseq [menu-key [{:name "FindGrep_grep_menuitem"   :proc findgrep-listener }
                    {:name "FindGrep_2hrs_menuitem"   :proc findgrep-listener }
                    {:name "FindGrep_clj_menuitem"    :proc findgrep-listener } ]]
      (let [mitem (create-menu-item menu (menu-key :name) (menu-key :proc))]
        ;; Menu item created, now associate the widget with the global 'ref'
        (set-findgrep-widg-state (keyword (menu-key :name)) (str mitem))
        (get-findgrep-widg-state (keyword (menu-key :name))))))

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