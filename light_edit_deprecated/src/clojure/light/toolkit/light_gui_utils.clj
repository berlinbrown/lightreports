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
;;; Main Description: Light is a simple text editor in clojure
;;; Contact: Berlin Brown <berlin dot brown at gmail.com>
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

(ns light.toolkit.light_gui_utils
	(:use    light.toolkit.light_utils
			 light.toolkit.public_objects
			 light.toolkit.light_main_constants
			 light.toolkit.light_config)
	(:import (java.util Date)
			 (java.io InputStreamReader BufferedReader File)
			 (org.eclipse.swt SWT)
			 (org.eclipse.swt.widgets Display Shell Text Widget TabFolder TabItem)
			 (org.eclipse.swt.graphics Color RGB FontData Font)
			 (org.eclipse.swt.events VerifyListener SelectionAdapter ModifyListener SelectionListener
									 SelectionEvent ShellAdapter ShellEvent)
			 (org.eclipse.swt.widgets MessageBox Label Menu MenuItem Control Listener)))

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
  (add-text-buffer *styled-text* buffer-1 str-data))

(defn add-secondary-text
  "Add FULL text to the main buffer buffer, clear the buffer and add the text"
  [str-data]
  ;;;;;;;;;;
  (add-text-buffer tab-text-2 buffer-2 str-data))

(defn async-add-text [disp text-field buffer str-data]
  ;; For example, text-field = styled-text
  (async-call disp (add-text-buffer text-field buffer str-data)))

(defn async-add-main-text [str-data]
  ;; For example, text-field = styled-text
  (async-call *display* (add-main-text str-data)))

(defn add-main-text-nc [line]
  ;; Add the main text without clearing the core buffer
  ;; Note 'buffer-1' and styled-text used as 
  ;; GUI components. (NC = no clear)
  (try (let [buffer buffer-1
                    text-gui *styled-text*]
         (. buffer append (str line *newline*))
         (. text-gui setText (. buffer toString)))
       ;; Attempt to redraw and update
       ;;(. text-gui redraw)
       ;;(. text-gui update)
       ;; Set the caret position to the end
       ;;(. text-gui setSelection (. text-gui getCharCount)))
       (catch Exception e (println e))))

(defn create-menu-item [menu res-menuitem proxy-body]
  (let [menu-item (new MenuItem menu (. SWT PUSH))]
    (doto menu-item
      (. setText (. resources-win getString res-menuitem))
      (. addSelectionListener proxy-body))
    menu-item))

(defn async-status-text [disp msg]
  (async-call disp (status-set-text msg)))

(defn async-status-history [disp msg]
  ;; Set the status bar and history
  (async-call disp (status-set-text msg))
  (async-call disp (history-add-text (str msg *newline*))))

(defn start-process [proc-args-lst buffer]
  ;; Example usage: (start-process [ "explorer.exe" ] buffer-1)
  (try
   (let [process-line (into-array proc-args-lst)
         process-bld  (when process-line (when-try (new ProcessBuilder process-line)))
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
               about1 (. resources-win getString "About_1")
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

(defn init-colors []  
  ;; Orange highlight color = 250, 209, 132
  ;; Light grey for default text.
  (let [disp (. Display getDefault)]
    (doto colors-vec
	  (. addElement (new Color disp orange-sel-color))
	  (. addElement (new Color disp lightgrey-color))
	  (. addElement (new Color disp red-color))
	  (. addElement (new Color disp cyan-sel-color))
	  (. addElement (new Color disp dark-blue-color))
	  (. addElement (new Color disp white-color))
	  (. addElement (new Color disp yellow-color))
	  (. addElement (new Color disp black-color)))))

(defn refresh-textarea []
  (. *styled-text* redraw)
  (. *styled-text* update))

(defn update-textarea []
  (. *styled-text* update))

(defn shell-close-adapter 
  " Create a proxy object used with a SWT widget 'addShellListener'"
  [cur-shell]
  ;;;;;;;;;;;;;
  (proxy [ShellAdapter][]
		 (shellClosed [event]
					  (set! (. event doit) false)
					  (. cur-shell setVisible false))))

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;; Swap Buffer Utilities
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
(defn swap-buffer-to-secondary
  []
  ;;;;;
  (let [main-text (. *styled-text* getText)]
	(when (and main-text (> (.length main-text) 0))
	  (async-status-history *display* "Swapping Buffer, from main buffer to secondary")
	  (add-secondary-text main-text))))

(def swap-buffer-to-listener
	 (proxy [SelectionAdapter] []
			(widgetSelected [event] (swap-buffer-to-secondary))))

(defn swap-buffer-from-secondary
  []
  ;;;;;
  (let [second-text (. tab-text-2 getText)]
	(when (and second-text (> (.length second-text) 0))
	  (async-status-history *display* "Swapping Buffer, from secondary to main buffer")
	  (add-main-text second-text))))

(def swap-buffer-from-listener
	 (proxy [SelectionAdapter] []
			(widgetSelected [event] (swap-buffer-from-secondary))))

(defn win-simple-mkdirs-handler 
  "Simple utility to make directories.  Throws error on invalid directory."
  []
  ;;;;;;;;;;;;;;;;;
  (try (let [loc-text (.getText location-bar)]
         (simple-mkdirs-handler loc-text)
         (async-status-history *display* (str "Mkdirs process completed => " loc-text \newline)))
       (catch Exception e
              (async-status-history *display* (str (.getMessage e) \newline)))))

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;;; End of Script
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;