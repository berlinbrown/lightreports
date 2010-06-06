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
;;; Light Log was developed with a combination of Clojure 1.0, Java and Scala with use of libs, 
;;; SWT 3.4, JFreeChart, iText. 
;;; 
;;; Quickstart : the best way to run the Light Log viewer is to click on the win32 batch script light_logs.bat
;;; (you may need to edit the Linux script for Unix/Linux environments).
;;; Edit the win32 script to add more heap memory or other parameters.

;;; The clojure source is contained in : HOME/src/octane
;;; The java source is contained in :  HOME/src/java/src

;;; To build the java source, see : HOME/src/java/build.xml and build_pdf_gui.xml

;;; Additional Development Notes: The SWT gui and other libraries are launched from a dynamic classloader.  
;;; Clojure is also started from the same code, and reflection is used to dynamically initiate Clojure. 
;;; See the 'start' package.  The binary
;;; code is contained in the octane_start.jar library.

;;; Home Page: http://code.google.com/p/lighttexteditor/
;;;  
;;; Contact: Berlin Brown <berlin dot brown at gmail.com>
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

(ns octane.toolkit.octane_gui_utils
	(:use octane.toolkit.octane_utils_common
          octane.toolkit.octane_utils
		  octane.toolkit.public_objects
		  octane.toolkit.octane_main_constants
		  octane.toolkit.octane_config)
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

(defn display-error
  "Generic alert/message box.  Can be used in most cases"
  [msg]  
  ;;;;;;;;
  (doto (new MessageBox *shell* SWT/ICON_ERROR)
    (. setMessage msg)
    (. open)))

(defn display-info
  "Generic alert/message box.  Can be used in most cases"
  [msg]  
  ;;;;;;;;
  (doto (new MessageBox *shell* SWT/ICON_INFORMATION)
    (. setMessage msg)
    (. open)))

(defmacro async-call 
  "Asynchronous execute call.  Create a proxy Runnable object and then execute the 
 body code.  Calling code with async avoids illegal thread exceptions"
  [#^org.eclipse.swt.widgets.Display disp & body]
  ;;;;;;;;;;;;;;
  `(. ~disp asyncExec (proxy [Runnable] [] (run [] ~@body))))

(defmacro get-sync-call 
  "Synchronous execute call.  Create a proxy Runnable object and then execute the 
 body code"
  [#^org.eclipse.swt.widgets.Display disp & body]
  ;;;;;;;;;;;;;;
  `(let [val-res# (ref nil)]
     (. ~disp syncExec (proxy [Runnable] [] (run [] (dosync (ref-set val-res# ~@body)))))
     (. Thread sleep 30)
     (deref val-res#)))

(defn add-text-buffer 
  "Add FULL text to a buffer, clear the buffer and add the text"
  [text-field buffer str-data]
  ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
  (clear-buffer buffer)
  (.append #^StringBuffer buffer str-data)
  (.setText text-field (.toString #^StringBuffer buffer)))

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

(defn async-add-text [#^org.eclipse.swt.widgets.Display disp text-field buffer str-data]
  ;; For example, text-field = styled-text
  (async-call disp (add-text-buffer text-field buffer str-data)))

(defn async-add-main-text [str-data]
  ;; For example, text-field = styled-text
  (async-call *display* (add-main-text str-data)))

(defn add-main-text-nc [line]
  ;; Add the main text without clearing the core buffer
  ;; Note 'buffer-1' and styled-text used as 
  ;; GUI components. (NC = no clear)
  (try (let [buffer #^StringBuffer buffer-1
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

(defn async-status-text [#^org.eclipse.swt.widgets.Display disp msg]
  (async-call disp (status-set-text msg)))

(defn async-status-history [#^org.eclipse.swt.widgets.Display disp msg]
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
  (.setText status-bar text)
  (.update status-bar))

(defn location-set-text [text]
  (. location-bar setText text)
  (. location-bar update))

(defn async-set-location [#^org.eclipse.swt.widgets.Display disp msg]
  (async-call disp (location-set-text msg)))

(defn shell-display-loop [#^org.eclipse.swt.widgets.Display disp sh dispose? msg]  
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
  (let [#^org.eclipse.swt.widgets.Display disp (. Display getDefault)]
    (doto colors-vec
	  (.addElement (Color. disp orange-sel-color))
	  (.addElement (Color. disp lightgrey-color))
	  (.addElement (Color. disp red-color))
	  (.addElement (Color. disp cyan-sel-color))
	  (.addElement (Color. disp dark-blue-color))
	  (.addElement (Color. disp white-color))
	  (.addElement (Color. disp yellow-color))
	  (.addElement (Color. disp black-color))
      (.addElement (Color. disp p-light-green-color))
      (.addElement (Color. disp p-dark-green-color))
      (.addElement (Color. disp p-light-red-color))
      (.addElement (Color. disp p-dark-red-color)))))

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
  (let [main-text (.getText *styled-text*)]
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