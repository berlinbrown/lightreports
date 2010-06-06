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

(ns light.toolkit.light_tools
	(:use    light.toolkit.light_utils
			 light.toolkit.public_objects
			 light.toolkit.light_config
			 light.toolkit.light_gui_utils
			 light.toolkit.light_templates
			 light.toolkit.light_codegen_templates
			 light.toolkit.light_main_constants)
	(:import (java.io BufferedReader File FileInputStream
					  FileNotFoundException IOException InputStreamReader Reader)
             (org.eclipse.swt SWT)
			 (java.util ResourceBundle Vector Hashtable)
             (org.eclipse.swt.events VerifyListener SelectionAdapter ModifyListener SelectionListener
									 SelectionEvent ShellAdapter ShellEvent)
             (org.eclipse.swt.widgets Label Menu MenuItem Control Listener)
			 (org.eclipse.swt.widgets Display Shell Text Widget TabFolder TabItem)
			 (java.util HashMap)))

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;; Codegen Utilties, also @see light_core_widgets.clj
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

(defn run-codegen-build-xml []
   (add-main-text *codegen-templ-build-xml*))

(defn run-codegen-junit-test []
   (add-main-text *codegen-templ-junit-test*))

(defn run-codegen-junit-suite []
   (add-main-text *codegen-templ-junit-suite*))

(defn run-codegen-xhtml []
   (add-main-text *codegen-templ-xhtml*))

(defn add-codegen-menu-items
  "Add the code gen menu items to the tool menu bar"
  [menu]
  ;;;;;;;
  (doto (new MenuItem menu (. SWT PUSH))
    (. setText (. resources-win getString "Codegen_buildxml_menuitem"))
    (. addSelectionListener
       (proxy [SelectionAdapter] []
              (widgetSelected [event] (run-codegen-build-xml)))))
  (doto (new MenuItem menu (. SWT PUSH))
    (. setText (. resources-win getString "Codegen_junit_test_menuitem"))
    (. addSelectionListener
       (proxy [SelectionAdapter] []
              (widgetSelected [event] (run-codegen-junit-test)))))
  (doto (new MenuItem menu (. SWT PUSH))
    (. setText (. resources-win getString "Codegen_junit_suite_menuitem"))
    (. addSelectionListener
       (proxy [SelectionAdapter] []
              (widgetSelected [event] (run-codegen-junit-suite)))))
  (doto (new MenuItem menu (. SWT PUSH))
    (. setText (. resources-win getString "Codegen_xhtmlpdf_menuitem"))
    (. addSelectionListener
       (proxy [SelectionAdapter] []
              (widgetSelected [event] (run-codegen-xhtml)))))
  :done)
;;; End of Function

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;; Process Launch Utilities
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

(def *search-text-state* (agent nil))

(defn get-process [proc-atom-str]
  ;; Where proc-str equals E.g. 'cat', 'cut'
  ;; Check if this a win32, is so use that particular process.
  ;; And then check for other oses and default to the linux system
  (cond
   *is-windows* (*process-map* (keyword (str "win"  "-" proc-atom-str)))
   *is-linux*   (*process-map* (keyword (str "unix" "-" proc-atom-str)))
   :else        (*process-map* (keyword (str "unix" "-" proc-atom-str)))))


(defn build-findgrep-arr [cmd cur-dir wildcard grep-args & [ mmin-args ]]
  ;; Complex approach for building the arguments to 'find'
  (let [grp (get-process "grep")
        fst   [cmd cur-dir "-name" (str "" wildcard "") ]
        more1 (if (empty? mmin-args) fst
                  (conj fst mmin-args))
        more  (if grep-args (conj more1 "-exec" grp "-Hn" (str "" grep-args "") "{}" ";") 
                  more1)
        s (apply str (interpose " " more))]
    {:array more :text s}))

(defn simple-tdiff [tstart]
  (let [tend (. System currentTimeMillis)
			 diff (- tend tstart)]
	diff))

(defn start-findgrep-cmd 
  "Start the find external process"
  [cur-dir wildcard grep-args & [ mmin-args ]]
  ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
  (let [fnd-proc (get-process "find")
                 proc-data    (build-findgrep-arr fnd-proc cur-dir wildcard grep-args mmin-args)
                 process-line (when fnd-proc     (into-array (proc-data :array)))
                 process-bld  (when process-line (when-try (new ProcessBuilder process-line)))
                 process      (when process-bld  (when-try (. process-bld start)))]
    (when process
      (history-add-text (str "Invoking find/grep command (Note: Use wildcard file pattern, e.g. *.clj. => " (proc-data :text) *newline*))
      (let [istream (. process getInputStream)
                    ireader   (new InputStreamReader istream)
                    bufreader (new BufferedReader ireader)
					tstart (. System currentTimeMillis) ]
        ;; First clear the main text buffer
        (clear-buffer buffer-1)
        (async-call *display* (status-set-text "Begin find/grep search (Please wait for process to complete).  Use wildcard file pattern, e.g. *.clj."))
        (let [proc-time-info (proc-time (loop [line (. bufreader readLine)]
                                          (when line
                                            (async-call *display* (add-main-text-nc line))
                                            (recur (. bufreader readLine)))))
                             msg (str "<<Completed find search>> " (simple-tdiff tstart) " ms")]
          (async-call *display* (add-main-text-nc msg))
          (async-call *display* (status-set-text msg)))))))

(defn start-findgrep-thread-java [widget]
  (proxy [Runnable] [] (run [] (start-findgrep-cmd "." "*.java" nil))))

(defn run-findgrep-widget [obj-inst search-str]
  ;; We don't have access to the keyword, have to compare with the
  ;; actual object (string)
  ;; TODO: fix the following code
  (if (and search-str (> (length search-str) 1))
    (let [obj (str obj-inst)
              cur-dir (get-current-dir)]
      (cond (= obj (get-findgrep-helper      :FindGrep_grep_menuitem))
            (start-findgrep-cmd cur-dir "*.*"    search-str)
            (= obj (get-findgrep-helper      :FindGrep_15min_menuitem))
            (start-findgrep-cmd cur-dir "*.*"    search-str "-mmin -15")
            (= obj (get-findgrep-helper      :FindGrep_2hrs_menuitem))
            (start-findgrep-cmd cur-dir "*.*"    search-str "-mmin -120")
            (= obj (get-findgrep-helper      :FindGrep_java_menuitem))
            (start-findgrep-cmd cur-dir "*.java" search-str)
            (= obj (get-findgrep-helper      :FindGrep_clj_menuitem))
            (start-findgrep-cmd cur-dir "*.clj"  search-str)
            (= obj (get-findgrep-helper      :FindGrep_logs_menuitem))
            (start-findgrep-cmd cur-dir "*.log"  search-str)))
    (async-status-history *display* (str "Invalid Search Text at " (date-time)))))
    
(defn start-findgrep-thread [widget search-str delay?]
  ;; See light_tools findgrep-listener for when this function
  ;; gets invoked.
  (proxy [Runnable] []
         (run [] (run-findgrep-widget widget search-str))))

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;; Launch External Command Prompt and Other Processes
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

(defn start-filemanager-proc []
  ;; Open the explorer if the directory is available
  (let [loc-text (. location-bar getText)]
    (let [file (new File loc-text)]
      (if (not (. file exists))
        (async-status-history *display* (str "File does not exist => " loc-text))
        (when (and (. file isDirectory) *is-windows*)
          (start-process [ *win-explorer-exe* (. file getAbsolutePath) ] buffer-1))))))

(defn start-cmdprompt-proc []
  ;; Open the explorer if the directory is available
  (let [loc-text (. location-bar getText)
        my-path  (if (empty? loc-text) "C:\\usr\\local\\projects\\light_edit\\" loc-text)]
    (let [file (new File my-path)]
      (if (not (. file exists))
        (async-status-history *display* (str "File does not exist => " loc-text))
        (if  (and (. file isDirectory) *is-windows*)
          (start-process [ *win-cmd-exe* "/c" "start" "/D" (.getAbsolutePath file) ] buffer-1)
          (println "UNKNOWN ERROR"))))))

(defn start-cygwinprompt-proc []
  ;; Open the explorer if the directory is available
  (let [*my-cyg-path*  (str *light-install-dir* *name-separator* "conf" *name-separator* "cygwin_light.bat")
        loc-text       (. location-bar getText)
        my-path        (if (empty? loc-text) "C:\\usr\\local\\projects\\light_edit\\" loc-text)]
    (let [file (new File my-path)]
      (if (not (. file exists))
        (async-status-history *display* (str "File does not exist => " loc-text))
        (if  (and (. file isDirectory) *is-windows*)
          (start-process [ *my-cyg-path* (.getAbsolutePath file) ] buffer-1)
          (println "UNKNOWN ERROR"))))))

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