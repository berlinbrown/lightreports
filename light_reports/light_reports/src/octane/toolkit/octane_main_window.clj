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
;;; FILE: MAIN ENTRY POINT
;;; ====================================     
;;; Update Date: 
;;;              5/25/2010 - Added Clojure 1.1, new SWT jars
;;; ====================================     
;;; Date: 1/5/2009
;;;       7/15/2009 - Added Clojure 1.0, other performance fixes and cleanups.
;;;      
;;; Main Description: Light Log Viewer is a tool for making it easier to search log files.  
;;; Light Log Viewer adds some text highlighting, quick key navigation to text files, simple graphs 
;;; and charts for monitoring logs, file database to quickly navigate to files of interest, 
;;; and HTML to PDF convert tool.  
;;; Light Log was developed with a combination of Clojure 1.0, Java and Scala with use of 
;;; libs, SWT 3.4, JFreeChart, iText. 
;;; 
;;; Quickstart : the best way to run the Light Log viewer is to click on the win32 batch script light_logs.bat
;;; (you may need to edit the Linux script for Unix/Linux environments).
;;; Edit the win32 script to add more heap memory or other parameters.

;;; The clojure source is contained in : HOME/src/octane
;;; The java source is contained in :  HOME/src/java/src

;;; To build the java source, see : HOME/src/java/build.xml and build_pdf_gui.xml

;;; Additional Development Notes: The SWT gui and other libraries are launched from a dynamic classloader.  
;;; Clojure is also started from the same code, and reflection is used to dynamically initiate Clojure. 
;;; See the 'start' package.  The binary code is contained in the octane_start.jar library.

;;; Home Page: http://code.google.com/p/lighttexteditor/
;;;  
;;; Contact: Berlin Brown <berlin dot brown at gmail.com>
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

;; Use warn-on reflection with def above the 'ns'
;; must be disabled for compilation
;; (def *warn-on-reflection* false)

(ns octane.toolkit.octane_main_window
    (:gen-class)
	(:use octane.toolkit.octane_utils_common
          octane.toolkit.octane_utils
          octane.toolkit.octane_config
          octane.toolkit.octane_core_widgets
          octane.toolkit.octane_file_database
          octane.toolkit.octane_file_utils
          octane.toolkit.octane_gui_utils
          octane.toolkit.octane_main_constants
          octane.toolkit.octane_regex_search
          octane.toolkit.octane_search_dialog
          octane.toolkit.octane_templates
          octane.toolkit.octane_testing
          octane.toolkit.octane_tools
          octane.toolkit.octane_utils
          octane.toolkit.public_objects
          octane.toolkit.command.octane_command
          octane.toolkit.octane_version)
	(:import (com.octane.util LogUtils)
             (org.eclipse.swt SWT)
             (org.eclipse.swt.internal SWTEventListener)
             (org.eclipse.swt.events SelectionListener)
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
			 (java.util.regex Pattern)))

;;**************************************
;; Begin Routines
;;**************************************
(def search-keyword)

(defn search-term? [] (if (> (length (.getText #^Text search-box)) 2) true false))

(defn srchbox-get-text [] (str (.getText #^Text search-box)))

(defn main-match-style 
  "A match has been found on the particular keyword.  Add the style with light foreground, 
 dark (dark blue) background."
  [keyword line lo]
  ;;;;;;;;;;;;;;;;;;
  (let [#^java.util.regex.Matcher m (regex-match-group keyword line)]
    (when m
      (let [pt1 (+ lo (.start m))
            pt2 (+ lo (.end m))
            len (- pt2 pt1)
            styl-tok (StyleRange. pt1 len (col-vec-wht) (col-vec-drkb) SWT/BOLD)]
        styl-tok))))

(defn findnext-match-style 
  "A match has been found on the particular keyword.  Add the style with light foreground, 
 dark (dark blue) background."
  []
  ;;;;;;;;;;;;;;;;;;
  ;; 'm' is a regex matcher object
  ;; Wrap in an try/catch to detect when no match found
  ;; Javadoc for stylerange:
  ;; StyleRange(int start, int length, Color foreground, Color background, int fontStyle) 
  ;;;;;;;;;;;;;;;;;;
  (let [#^java.util.regex.Matcher m (get-find-next-state)]
    (when m
      (try (let [pt1 (.start m)
                 pt2 (.end m)
                 len (- pt2 pt1)
                 styl-tok (new StyleRange pt1 len (col-vec-blk) (col-vec-yllw) SWT/BOLD)]
             styl-tok)
           (catch Exception e (println "ERR: findnext match style [no match] : " e))))))

(defn style-keyword-match
  "When a search term is available and the keyword matches, add the selection style on the
 main buffer text area.  Otherwise, add the fail style"
  [styles-vec line l-offset sty-on-sel sty-fail]
  ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
  (when (search-term?)
    (if (search-keyword (srchbox-get-text) line)
	  ;; Add the select style and attempt to do keyword style
      (do (when-let [fnd-style (main-match-style (srchbox-get-text) line l-offset)]
              ;; Check if match found, so add the style range for the keyword selection
              (add-select-style styles-vec sty-on-sel)
            (add-select-style styles-vec fnd-style))
          ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
          ;; Add style if 'find-next' term is available
          ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
          (when-let [fnd-style (findnext-match-style)]
              (add-select-style styles-vec fnd-style)))
      ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
      ;; FAIL on search term
      ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
	  (add-select-style styles-vec sty-fail))))

;; Using outside let idiom for faster local variable access
(let [#^Vector loc-colors-vec colors-vec]
  (defn style-handler [#^LineStyleEvent event]
    " Preload the colors-vec "
    (when (= (count loc-colors-vec) 0) (init-colors))
    (let [styles-vec (new Vector)
          line (.lineText event)
          lo   (.lineOffset event)
          len  (.length line)
          bg   (.get loc-colors-vec 0)
          fgl  (.get loc-colors-vec 1)
          all-bold (StyleRange. lo len nil bg SWT/BOLD)
          light    (StyleRange. lo len fgl nil SWT/NORMAL)]
    ;;************************************
    ;; Continue with after the 'when' search term
    ;; Search for the log date pattern
    ;;************************************
    (when-let [match-bean (LogUtils/getLogDatePattern line)]
        (add-new-style-bean styles-vec match-bean (col-vec-pdrkgrn) (col-vec-pltgrn) lo SWT/BOLD))
    (when-let [match-bean (LogUtils/getErrorPattern line)]
        (add-new-style-bean styles-vec match-bean (col-vec-pltred) (col-vec-pdrkred) lo SWT/BOLD))
    ;; Add the event styles if needed   
    (style-keyword-match styles-vec line lo all-bold light)
    ;; Associate the even style with the display
    (let [arr (make-array StyleRange (. styles-vec size))]
      (set! (. event styles) arr)
      (. styles-vec copyInto (. event styles))))))

(defn search-keyword [keyword line]
  (re-seq (octane-pattern keyword Pattern/CASE_INSENSITIVE) line))
      
;; Event.detail line start offset (input) Event.text line text (input)
;; LineStyleEvent.styles Enumeration of StyleRanges, need to be in order.
;; (output) LineStyleEvent.background line background color (output)
(def text-style-listener
     (proxy [LineStyleListener] []
            (lineGetStyle [event] (style-handler event))))

(def shell-close-listener
     (proxy [ShellAdapter] [] 
            (shellClosed [evt] (exit))))

(defn refresh-textarea-deprecated []
  (let [disp (.getDisplay #^org.eclipse.swt.widgets.Widget *styled-text*)]
    (. disp asyncExec
       (proxy [Runnable] []
              (run [] (. #^org.eclipse.swt.custom.StyledText *styled-text* setText 
                         (.toString #^StringBuffer buffer-1)))))))

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;; Continue
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

(def find-text-listener
     (proxy [Listener] []
            (handleEvent [event]
                         (when (= (. event detail) SWT/TRAVERSE_RETURN)
                           (history-add-text (str "Searching for " (. search-box getText) "\n"))
                           (refresh-textarea)))))
                           
(defn create-grid-layout []
  (let [gridLayout (new GridLayout)]
    (set! (. gridLayout numColumns) 1)
    gridLayout))
        
(defn create-shell [disp sh]
  ;; Create shell and continue with other inits
  (.addLineStyleListener *styled-text* text-style-listener)
  ;; Note change in 'doto' call, dot needed.
  (let [layout (create-grid-layout)]
    (doto sh
      (. setText (str (. #^ResourceBundle resources-win getString "Window_title") " - " *OCTANE_VERSION*))
      (. setLayout layout)
      (. addShellListener (proxy [ShellAdapter] []
                                 (shellClosed [evt] (exit)))))))

(defn init-startup-msgs
  "Add text to the history view as well as status bar and other areas"x
  []
  ;;;
  ;;; See template file for startup tips
  (let [msg-2 (str "Light Reports GUI loaded " (date-time) " " (*memory-usage*))
        tip-2 (str (format-next-tip) \newline "--------" \newline "Select an application from the menu bar to continue")]
    (.info *logger* msg-2)
    (history-add-text (get-hist-header))
    (history-add-text tip-2)
    (add-main-text tip-2)
    (status-set-text msg-2)
    (add-commands-msg)))

(defn init-gui-helper [disp sh]
  (create-all-tabs)
  (create-menu-bar disp sh)
  (create-shell disp sh)
  (init-startup-msgs))

(defn create-gui-window 
  "Initialize the SWT window, set the size add all components"
  [ #^org.eclipse.swt.widgets.Display disp #^org.eclipse.swt.widgets.Shell sh]
  ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;  
  ;; Set the tab folder and items with the main text area
  ;; and other SWT oriented inits.
  (init-gui-helper disp sh)
  ;; Modify already created objects
  (let [gd (new GridData SWT/FILL SWT/FILL true false)]
    (. search-box addListener SWT/Traverse find-text-listener)
    (. search-box setLayoutData gd)
    (. location-bar setLayoutData gd)
    (. location-bar addListener SWT/Traverse location-text-listener))
  (create-status-bar)
  ;; Final init, set the window size and then open
  (doto sh
    (.setSize win-size-width win-size-height)
    (.open))
  (parse-system-args)
  (loop [] (if (. sh (isDisposed))
             (. disp (dispose))
             (let [] (when (not (. #^org.eclipse.swt.widgets.Display disp readAndDispatch))
                       (. #^org.eclipse.swt.widgets.Display disp (sleep)))
                  (recur)))))

;;**************************************
;; Application Main Entry Point
;**************************************
(defn main-1 
  " Application Entry Point, launch the main window and wait for events"
  []
  ;;;;
  (println "Launching Light Reports Editor...")
  ;;;;;;;;;;;;;;;;;;;;;;;
  ;; Launch the window ;;
  ;;;;;;;;;;;;;;;;;;;;;;;
  (create-gui-window *display* *shell*)
  (let [o (new Object)] (locking o (. o (wait)))))

(defn -main [& args]
  (try (main-1)
	   (catch Exception e
			  (println "ERR at <Main [1]>: " e)
              (.printStackTrace e)
              (.error *logger* "Critical Error at Main" e)
			  (exit))))

;; Invoke entry point
;; Remove -main when running gen-class
(-main)

;;
;; REVISION HISTORY - Light Logs Clojure Source
;;
;; + 5/20/2010 Berlin Brown : Added Clojure 1.1
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
