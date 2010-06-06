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
;;; Description:
;;;     Simple 'Find' keyword in File with SWT and Clojure
;;; Contact:  ... <berlin dot brown at >
;;; Usage:   java -cp $CP clojure.lang.Repl src/octane_main.clj
;;;          Enter a search term and then open a file, if the term
;;;          is found on the line then the line will be higlighted.
;;;
;;; Clojure version: Clojure release 200903

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

(ns light.test.win.basic_gui_utils
	(:use  light.test.win.basic_test_utils
		   light.test.win.global_objects
           light.test.win.basic_constants)    
	(:import (java.util Date)
			 (java.io InputStreamReader BufferedReader File)
			 (org.eclipse.swt SWT)
			 (org.eclipse.swt.widgets Display Shell Text Widget TabFolder TabItem)
			 (org.eclipse.swt.graphics Color RGB FontData Font)
			 (org.eclipse.swt.events VerifyListener SelectionAdapter ModifyListener SelectionListener
									 SelectionEvent ShellAdapter ShellEvent)
			 (org.eclipse.swt.widgets MessageBox Label Menu MenuItem Control Listener)))

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

(def status-set-text)

(defn display-error [msg]
  (doto (new MessageBox *shell* SWT/ICON_ERROR)
    (. setMessage msg)
    (. open)))

(defmacro async-call 
  "Asynchronous execute call.  Create a proxy Runnable object and then execute the 
 body code.  Calling code with async avoids illegal thread exceptions"
  [disp & body]
  ;;;;;;;;;;;;;;
  `(. ~disp asyncExec (proxy [Runnable] [] (run [] ~@body))))

(defmacro get-sync-call 
  "Synchronous execute call.  Create a proxy Runnable object and then execute the 
 body code"
  [disp & body]
  ;;;;;;;;;;;;;;
  `(let [val-res# (ref nil)]
     (. ~disp syncExec (proxy [Runnable] [] (run [] (dosync (ref-set val-res# ~@body)))))
     (. Thread sleep 50)
     (deref val-res#)))

(defn add-text-buffer 
  "Add FULL text to a buffer, clear the buffer and add the text"
  [text-field buffer str-data]
  ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
  (clear-buffer buffer)
  (. buffer append str-data)
  (. text-field setText (. buffer toString)))

(defn add-main-text
  "Add FULL text to the main buffer buffer, clear the buffer and add the text"
  [str-data]
  ;;;;;;;;;;
  (add-text-buffer *text-window* buffer-1 str-data))

(defn async-add-text [disp text-field buffer str-data]
  ;; For example, text-field = text-window
  (async-call disp (add-text-buffer text-field buffer str-data)))

(defn async-add-main-text [str-data]
  ;; For example, text-field = text-window
  (async-call *display* (add-main-text str-data)))

(defn add-main-text-nc [line]
  ;; Add the main text without clearing the core buffer
  ;; Note 'buffer-1' and text-window used as 
  ;; GUI components. (NC = no clear)
  (try (let [buffer buffer-1
                    text-gui *text-window*]
         (. buffer append (str line *newline*))
         (. text-gui setText (. buffer toString)))
       ;; Attempt to redraw and update
       ;;(. text-gui redraw)
       ;;(. text-gui update)
       ;; Set the caret position to the end
       ;;(. text-gui setSelection (. text-gui getCharCount)))
       (catch Exception e (println e))))

(defn create-menu-item [menu menu-text proxy-body]
  (let [menu-item (new MenuItem menu (. SWT PUSH))]
    (doto menu-item
      (. setText menu-text)
      (. addSelectionListener proxy-body))
    menu-item))

(defn async-status-text [disp msg]
  (async-call disp (status-set-text msg)))

(defn async-status-history [disp msg]
  ;; Set the status bar and history
  (async-call disp (status-set-text msg)))

(defn new-process-builder [process-line]
  (when-try 
   (let [p (new ProcessBuilder process-line)]
	 (.redirectErrorStream p true))))
  
(defn start-process [proc-args-lst buffer]
  ;; Example usage: (start-process [ "explorer.exe" ] buffer-1)
  (try
   (let [process-line (into-array proc-args-lst)
         process-bld  (when process-line (new-process-builder process-line))
         process      (when process-bld  (when-try (. process-bld start)))]
     (when process
       (async-status-history *display* (str "Invoking process => " proc-args-lst *newline*))
       ;; Wrap the request within a thread.
       (let [proc-thread
             (proxy [Runnable][]
                    (run []
                         (let [istream   (. process getInputStream)
                               ireader   (new InputStreamReader istream)
                               bufreader (new BufferedReader ireader)]
                           ;; First clear the main text buffer
                           (clear-buffer buffer)
                           (let [proc-time-info (proc-time (loop [line (. bufreader readLine)]
                                                             (when line
                                                               (async-call *display* (add-main-text-nc line))
                                                               (recur (. bufreader readLine)))))
                                 msg (str "<<Completed process>> " (proc-time-info :time-text))]
                             (async-call *display* (add-main-text-nc msg))
                             (async-call *display* (status-set-text msg))))))]
         ;; Launch the process thread
         (. (new Thread proc-thread) start))))
   (catch Exception e (. e printStackTrace))))

(defn status-set-text [text]
  (. status-bar setText text)
  (. status-bar update))

(defn location-set-text [text]
  (. location-bar setText text)
  (. location-bar update))

(defn shell-display-loop [disp sh dispose? msg]  
  (loop [] (if (. sh (isDisposed))
             (if dispose? (. disp dispose) (println msg))
             (let []
               (when (not (. disp (readAndDispatch)))
                 (. disp (sleep)))
               (recur)))))

(defn create-about-messagebox [sh]
  (let [msgbox (new MessageBox sh SWT/NONE)
        about1 *Basic_About_1*
        about2 *about-version*]
    (. msgbox setText about1)
    (. msgbox setMessage about2)
    (. msgbox open)
    msgbox))

(defn create-info-messagebox [sh titl msg]
  (let [msgbox (new MessageBox sh SWT/NONE)]
    (. msgbox setText titl)
    (. msgbox setMessage msg)
    (. msgbox open)
    msgbox))

(defn refresh-textarea []
  (. *text-window* redraw)
  (. *text-window* update))

(defn update-textarea []
  (. *text-window* update))

(defn shell-close-adapter 
  " Create a proxy object used with a SWT widget 'addShellListener'"
  [cur-shell]
  ;;;;;;;;;;;;;
  (proxy [ShellAdapter][]
		 (shellClosed [event]
					  (set! (. event doit) false)
					  (. cur-shell setVisible false))))

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;;; End of Script
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;