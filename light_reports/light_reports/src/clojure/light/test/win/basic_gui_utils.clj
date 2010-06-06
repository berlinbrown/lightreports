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