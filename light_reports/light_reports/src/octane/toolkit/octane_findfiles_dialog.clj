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

(ns octane.toolkit.octane_findfiles_dialog
	(:use octane.toolkit.octane_utils_common
          octane.toolkit.octane_utils
          octane.toolkit.public_objects
          octane.toolkit.octane_gui_utils
          octane.toolkit.octane_config
          octane.toolkit.octane_tools)
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

(def *findfiles-style* (bit-or SWT/CLOSE (bit-or SWT/BORDER (bit-or SWT/TITLE 1))))

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;; Create the Widgets
;; Will be positioned on order of creation
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
(def *findfiles-shell*        (new Shell *shell* *findfiles-style*))
(def findfiles-label          (new Label *findfiles-shell* SWT/LEFT))
(def findfiles-filter-box     (new Text  *findfiles-shell* SWT/BORDER))

(def ff-location-label         (new Label *findfiles-shell* SWT/LEFT))
(def ff-location-box           (new Text  *findfiles-shell* SWT/BORDER))

(def ff-wildcard-label         (new Label *findfiles-shell* SWT/LEFT))
(def ff-wildcard-box           (new Text  *findfiles-shell* SWT/BORDER))

(def ff-program-mods-label     (new Label *findfiles-shell* SWT/LEFT))
(def ff-program-mods-box       (new Text  *findfiles-shell* SWT/BORDER))

(def external-ff-label         (new Label  *findfiles-shell* SWT/LEFT))
(def external-ff-check-box     (new Button *findfiles-shell* SWT/CHECK))

(def regex-findfiles-label     (new Label  *findfiles-shell* SWT/LEFT))
(def regex-ff-check-box        (new Button *findfiles-shell* SWT/CHECK))

(def casesens-findfiles-label  (new Label  *findfiles-shell* SWT/LEFT))
(def casesens-ff-check-box     (new Button *findfiles-shell* SWT/CHECK))

(def findfiles-composite      (new Composite *findfiles-shell* SWT/NONE))
(def findfiles-find-button    (new Button findfiles-composite SWT/PUSH))
(def findfiles-close-button   (new Button findfiles-composite SWT/PUSH))
(def findfiles-status-label   (new Label *findfiles-shell* (bit-or SWT/LEFT (bit-or SWT/BORDER 1))))

(defn create-findfiles-grid-layout []
  (let [gridLayout (new GridLayout)]
    (set! (. gridLayout marginHeight)  8)
    (set! (. gridLayout marginWidth)  10)
    (set! (. gridLayout numColumns) 2) gridLayout))

(defn findfiles-label-set-text
  "Helper function to set the search label text"
  []
  ;;;
  (. findfiles-label setText (prop-str resources-win "Search_for_label"))
  (. regex-findfiles-label setText "Regular Expressions:")
  (. casesens-findfiles-label setText "Case Sensitive:")
  (. findfiles-status-label setText (str "Find in Files Dialog Opened - " (date-time)))
  (. ff-location-label setText "Find Directory:")
  (. ff-wildcard-label setText "File Name Patterns:")
  (. ff-location-box       setText ".")
  (. ff-wildcard-box       setText "^.*clj$")
  (. ff-program-mods-label setText "Program Modifiers:")
  (. ff-program-mods-box   setText "-mmin 360")
  (. external-ff-label     setText "Enable External Process:"))

(def findfiles-on-close-listener
     (proxy [SelectionListener][]
            (widgetSelected [event]
                            (set! (. event doit) false)
                            (. *findfiles-shell* setVisible false))
            (widgetDefaultSelected [event]
                                   (set! (. event doit) false)
                                   (. *findfiles-shell* setVisible false))))

(def on-findfiles-dir-func (fn [d] false))

(def on-findfiles-file-func
     ;; Return lambda function, on file handler
     (fn [file]
         (let [filename (. file getAbsolutePath)
               disp     (. *findfiles-shell* getDisplay)
               term     (get-sync-call disp (. findfiles-filter-box getText))]
           (doc-file-loop-handler filename
                                  (fn [line line-num]
                                      ;; Lambda function for on find string
                                      ;; Update the display with the 'grep' information.
                                      (when (simple-grep? line term)
                                        (async-call *display*
                                                    (add-main-text-nc
                                                     (str filename ": line " line-num ": " (.trim line))))))))))

(defn lib-findfiles-find-handler
  "When the user selects the find next button, invoke this find next handler.
 Search the main buffer for the term in the 'find' box."
  [disp term regx-files dir-frm-box cur-dir]
  ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
  (if (and disp term cur-dir (> (length cur-dir) 0) (> (length term) 0)
           (not (empty? regx-files)))
    (let [tstart (. System currentTimeMillis)]
      ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
      ;; Establish the directory and file functions.
      ;; First, send out startup messages and init routines
      ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
      (async-status-history *display* (str "Begin Find in Files Search For (Please wait for process to complete) => " term " at "(date-time)))
      (async-add-main-text (str "<<< Begin Find in Files Search >>>" \newline)) 
      ;; Also set the 'quick' search text bar at the bottom of the main window
      (async-call *display* (. search-box setText term))
      (async-call *display* (update-textarea))
      ;; Continue with traverse directory
      (let [dir-func  on-findfiles-dir-func
            file-func on-findfiles-file-func]
        (traverse-directory (new File ".") dir-func file-func regx-files))
      (let [tend (. System currentTimeMillis)
            diff (- tend tstart)]
		  (async-status-history *display* (str "End Find File Search, process time => " diff " ms"))))
    (do
      ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
      ;; Err:
      ;; Send status error message, could not find
      ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
      (async-call disp (. findfiles-status-label setText "Invalid Search")))))
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;; End of lib-findfiles-find-handler
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

(defn proc-findfiles-find-handler
  "When the user selects the find next button, invoke this find next handler.
 Search the main buffer for the term in the 'find' box."
  [disp term regx-files dir-frm-box cur-dir]
  ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
  (if (and disp term cur-dir (> (length cur-dir) 0) (> (length term) 0)
           (not (empty? regx-files)))
    (let [tstart (. System currentTimeMillis)]
      ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
      ;; Establish the directory and file functions.
      ;; First, send out startup messages and init routines
      ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
      (async-status-history *display* (str "Begin Find in Files Search For (Please wait for process to complete) => " term " at "(date-time) " Use *.XXX wildcard when process enabled"))
      (async-add-main-text (str "<<< Begin Find in Files Search >>>" \newline))               
      ;; Also set the 'quick' search text bar at the bottom of the main window
      (async-call *display* (. search-box setText term))
      (async-call *display* (update-textarea))
      (start-findgrep-cmd cur-dir regx-files term)
      (let [tend (. System currentTimeMillis)
            diff (- tend tstart)]
		  (async-status-history *display* (str "End Find File Search, process time => " diff " ms"))))
    (do
      ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
      ;; Err:
      ;; Send status error message, could not find
      ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
      (async-call disp (. findfiles-status-label setText "Invalid Search")))))
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;; End of PROC-findfiles-find-handler
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

(defn findfiles-find-handler
  "When the user selects the find next button, invoke this find next handler.
 Search the main buffer for the term in the 'find' box."
  [event]
  ;;;;;;;;;;;;
  (let [disp        (. *findfiles-shell* getDisplay)
        term        (get-sync-call disp (. findfiles-filter-box getText))
        regx-files  (get-sync-call disp (. ff-wildcard-box getText))
        dir-frm-box (get-sync-call disp (. ff-location-box getText))
        cur-dir     (if (empty? dir-frm-box) "." dir-frm-box)
        has-process (get-sync-call disp (.getSelection external-ff-check-box))]
    (if has-process
      (proc-findfiles-find-handler disp term regx-files dir-frm-box cur-dir)
      (lib-findfiles-find-handler disp term regx-files dir-frm-box cur-dir))))

(defn findfiles-find-thread
  "When the user selects the find next button, invoke this find next handler.
 Search the main buffer for the term in the 'find' box."
  [event]
  ;;;;;;;;;;;;
  (let [prox (proxy [Runnable][] (run [] (findfiles-find-handler event)))]
    (. (new Thread prox) start)))

(def findfiles-find-next-listener
     (proxy [SelectionListener][]
            (widgetSelected [event] (findfiles-find-thread event))
            (widgetDefaultSelected [event] (findfiles-find-thread event))))

(defn init-findfiles-buttons
  "Set the default properties for the search buttons"
  []
  ;; Set the composite buttons
  (let [rowd-find (new RowData 98 24)]
    (. findfiles-find-button  setText "Find in Files")
    (. findfiles-find-button  setLayoutData rowd-find)
    (. findfiles-find-button  setEnabled true)
    (. findfiles-find-button  addSelectionListener findfiles-find-next-listener)
    (. findfiles-close-button setText "Close")
    (. findfiles-close-button setLayoutData rowd-find)
    (. findfiles-close-button setEnabled true)
    (. findfiles-close-button addSelectionListener findfiles-on-close-listener)))

(defn init-findfiles-helper
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
    (findfiles-label-set-text)
    (set! (. gd-textbox widthHint) 200)
    (. findfiles-filter-box setLayoutData gd-textbox)
    (. findfiles-status-label setLayoutData gd-status-bar)
	(. ff-location-box setLayoutData (new GridData SWT/FILL SWT/NONE true false))
	(. ff-wildcard-box setLayoutData (new GridData SWT/FILL SWT/NONE true false))
    (. ff-program-mods-box setLayoutData (new GridData SWT/FILL SWT/NONE true false))
    ;; Set the button composite widget
    (set! (. gd-composite horizontalSpan) 2)
    ;; verticalAlignment specifies how controls will be positioned vertically within a cell
    (set! (. gd-composite horizontalAlignment) SWT/LEFT)
    (. findfiles-composite setLayoutData gd-composite)
    ;; Position the buttons a couple of pixels away.
    (set! (. comp-layout marginTop)  4)
    (set! (. comp-layout marginLeft) 2)
    (. findfiles-composite setLayout comp-layout)    
    (init-findfiles-buttons)))
			  
;;;;;;;;;;;;;;;;;;;;
;; End of Function
;;;;;;;;;;;;;;;;;;;;

(defn create-findfiles-dialog 
  "Initialize the file database SWT window, set the size add all components"
  [parent-sh]
  ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

  (history-add-textln "Opening find files screen (Search -> Open New Find in Files)")
  (let [layout (create-findfiles-grid-layout)]
	(. *findfiles-shell* setText "Find in Files")
	(init-findfiles-helper *findfiles-shell*)
	(. *findfiles-shell* setLayout layout)
	(. *findfiles-shell* addShellListener
	   (proxy [ShellAdapter][]
			  (shellClosed [event]
						   (set! (. event doit) false)
						   (. *findfiles-shell* setVisible false))))
	(. *findfiles-shell* pack)
	(. *findfiles-shell* open)
	(. *findfiles-shell* setVisible true)))

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