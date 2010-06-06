;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;;; Copyright (c) ....:. All rights reserved.
;;;
;;; Copyright (c) 2006-2007, 

;;; All rights reserved.

;;; Redistribution and use in source and binary forms, with or without modification,
;;; is NOT permitted.
;;; PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.

;;;
;;; Date:  1/5/2009
;;;
;;; Clojure version: Clojure release 200903

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

(ns light.test.win.global_objects
  (:use light.test.win.basic_constants)
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
		   (java.util ResourceBundle Vector Hashtable)))

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

(def *spring-context* light.test.win.spring_globals/*spring-context*)

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;; Creation of main GUI components (order of instantiation is important here)
;; Including main tabs, location bar and search box.
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

(def *display*     (Display.))
(def *shell*       (Shell. *display*))

(defn styled-text-font [] (new Font (. *shell* getDisplay) "Courier New" 9 SWT/NORMAL))

(def swt-textarea-style (bit-or SWT/BORDER (bit-or SWT/MULTI (bit-or SWT/H_SCROLL SWT/V_SCROLL))))

(defn create-main-text-area
  " The text area is the main(first tab) text area on the window.  Most
 text will get displayed in this text area.  The text area is also attached to a tab."
  [sh]
  ;;;;;;;;;;;;;;;;;;;;
  (let [text (new Text sh swt-textarea-style)
		gd-tab (new GridData GridData/FILL GridData/FILL true true)
		disp (Display/getDefault)
		bg   (. disp (getSystemColor SWT/COLOR_WHITE))]
    (doto text
      (. setLayoutData gd-tab)
      (. setFont (styled-text-font))
      (. setEditable true)
      (. setBackground bg))
    text))
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

(def location-bar  (new Text *shell* SWT/BORDER))
(def *text-window* (create-main-text-area *shell*))
(def search-box    (new Text *shell* SWT/BORDER))

;; Main Buttons ;;
(def db-button-comp      (new Composite *shell* SWT/NONE))
(def db-compile-button   (new Button db-button-comp SWT/PUSH))
(def db-runtests-button  (new Button db-button-comp SWT/PUSH))
(def db-single-test-button  (new Button db-button-comp SWT/PUSH))
(def db-memory-button    (new Button db-button-comp SWT/PUSH))
(def db-hprof-button     (new Button db-button-comp SWT/PUSH))
(def db-win-close-button (new Button db-button-comp SWT/PUSH))

(def status-bar (new Label *shell* SWT/BORDER))

;;;;;;;;;;;;;;;;;;;
;; End of Main Window Widget Components
;;;;;;;;;;;;;;;;;;;

(def swt-textarea-style (bit-or SWT/BORDER (bit-or SWT/MULTI (bit-or SWT/H_SCROLL SWT/V_SCROLL))))
(def swt-tabtext-style  (bit-or SWT/BORDER (bit-or SWT/MULTI (bit-or SWT/H_SCROLL SWT/V_SCROLL))))

(def buffer-1 (StringBuffer. 4096))

(defn styled-text-font [] (new Font (. *shell* getDisplay) "Courier New" 9 SWT/NORMAL))

(def fileDialog       (new FileDialog *shell* SWT/CLOSE))
(def directory-dialog (new DirectoryDialog *shell* SWT/CLOSE))

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;; REVISION HISTORY
;;
;; -------------------------------------
;; + 1/5/2009  Berlin Brown
;; Description: Project Create Date

;; + 1/5/2009  Berlin Brown
;; Description: Add new headers
;; 
;; -------------------------------------

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;;; End of Script
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;