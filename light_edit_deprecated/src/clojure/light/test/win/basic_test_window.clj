;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;;;
;;; Copyright (c) 2006-2007, 

;;; All rights reserved.

;;;
;;; Date:  1/5/2009
;;;
;;; Clojure version: Clojure release 200903

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
  (println "Launching Octane Text Viewer...")
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
