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

(ns light.test.win.basic_test_window
    (:use light.test.win.basic_constants
          light.test.win.global_objects
          light.test.win.basic_test_utils
          light.test.win.basic_gui_utils)
	(:import (org.eclipse.swt SWT)             
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

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

(def shell-close-listener
     (proxy [ShellAdapter] [] 
	   (shellClosed [evt] (exit))))

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

;; Listeners and Event Handlers for Compiling and Running the Tests
;; The event will spawn a script process that compiles the tests of interest

;; Invoke the compile process and log the output to the main window"
;; The following tests are available:
;;     compile, runtests, singletest, singlemem, singlehprof
(defmacro def-start-process [test-type]
  `(let [test-props-bean# (. *spring-context* getBean "testWinProperties")]
     (println "Attempt to start process, single class =>"  (.getSingleTestClass test-props-bean#))
	 (start-process [ *process-gentests-sh* ~test-type 
					 (.getSingleTestClass test-props-bean#) ] buffer-1)))

(defmacro def-button-listener [test-type]
  `(proxy [~'SelectionListener][]
	 (widgetSelected [event#] (def-start-process ~test-type))
	 (widgetDefaultSelected [event#] (def-start-process ~test-type))))
  
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

(def button-close-listener
     (proxy [SelectionListener][]
	   (widgetSelected [event] (exit))
	   (widgetDefaultSelected [event] (exit))))

(defmacro doto-add-button [bttn text-bttn test-type]
  `(doto ~bttn
	 (. setText ~text-bttn)
	 (. setLayoutData ~'gd-button)
	 (.addSelectionListener (def-button-listener ~test-type))))
  
(defn init-buttons-composite 
  "Set the layout, width and height for the buttons"
  [sh]
  ;;;;;;;
  (let [comp db-button-comp
        gd-comp       (new GridData)
        gd-button (new RowData *db-bttn-med-width* *db-button-height*)]
    (. comp setLayoutData gd-comp)
    (. comp setLayout (new RowLayout))
	(doto-add-button db-compile-button  *database-compile-button* "compile")
	(doto-add-button db-runtests-button *database-runtests-button* "runtests")
	(doto-add-button db-single-test-button *database-single-test-button* "singletest")
	(doto-add-button db-memory-button *database-memory-button* "singlemem")
	(doto-add-button db-hprof-button *database-hprof-button* "singlehprof")
    (doto db-win-close-button
	  (.setText *database-quit-button*)
	  (.setLayoutData gd-button)
	  (.addSelectionListener button-close-listener))))

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;; Continue
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

(def find-text-listener
     (proxy [Listener] []
	   (handleEvent [event]
					(when (= (. event detail) SWT/TRAVERSE_RETURN)
					  (refresh-textarea)))))

(defn create-grid-layout []
  (let [gridLayout (new GridLayout)]
    (set! (. gridLayout numColumns) 1)
    gridLayout))


(defn create-menu-bar [disp sh]
  (let [bar (new Menu sh (. SWT BAR))]
    (. sh setMenuBar bar)
    bar))
            
(defn create-shell [disp sh]
  ;; Note change in 'doto' call, dot needed.
  (let [layout (create-grid-layout)]
    (doto sh
      (. setText *Basic_Window_title*)
      (. setLayout layout)
      (. addShellListener (proxy [ShellAdapter] []
                                 (shellClosed [evt] (exit)))))))

(defn init-gui-helper [disp sh]
  (create-menu-bar disp sh)
  (create-shell    disp sh)
  (init-buttons-composite sh)
  (status-set-text (str "Octane GUI loaded " (date-time) " " (*memory-usage*))))

(defn create-gui-window 
  "Initialize the SWT window, set the size add all components"
  [disp sh]
  ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
  
  ;; Set the tab folder and items with the main text area
  ;; and other SWT oriented inits.
  (init-gui-helper disp sh)
  ;; Modify already created objects
  (let [gd (new GridData SWT/FILL SWT/FILL true false)]
    (. search-box addListener SWT/Traverse find-text-listener)
    (. search-box setLayoutData gd)
    (. location-bar setLayoutData gd)
	(. status-bar setLayoutData gd))
  ;; Final init, set the window size and then open
  (doto sh
    (. setSize win-size-width win-size-height)
    (. open))
  ;; Debug
  (loop [] (if (. sh (isDisposed))
             (. disp (dispose))
             (let [] (when (not (. disp (readAndDispatch)))
                       (. disp (sleep)))
                  (recur)))))

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;; Application Main Entry Point
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
(defn main-1 
  " Application Entry Point, launch the main window and wait for events"
  []
  ;;;;;;;;;
  (println "Launching Octane Test Text Viewer...")
  (create-gui-window *display* *shell*)
  (let [o (new Object)] (locking o (. o (wait)))))

(defn -main [& args]
  (try (main-1)
	   (catch Exception e
		 (.printStackTrace e)
		 (println "ERR at <Main>: " e)
		 (exit))))

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;; Remove -main when running gen-class
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
(-main)

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;      
;;; End of Script
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;      
