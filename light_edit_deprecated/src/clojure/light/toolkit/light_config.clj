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

(ns light.toolkit.light_config)

(import '(java.util ResourceBundle Vector))
(import '(java.io File))

(def *name-separator* (. File separator))
(def *work-path-1*    (str "conf" *name-separator* "sys"  *name-separator* "_work"))

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;; System property keys
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
(def *sys-prop-install-dir* "light.install.dir")

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;; Resource Files
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
(def resources-win      (ResourceBundle/getBundle "light_win"))
(def resources-core-sys (ResourceBundle/getBundle "light_core_sys"))
(def resources-win-opts (ResourceBundle/getBundle "light_win_options"))
(def resources-user     (ResourceBundle/getBundle "light_user"))

(defn prop-core-sys [key]      (. resources-core-sys getString key))
(defn prop-core-sys-int [key]  (. Integer parseInt (prop-core-sys key)))
(defn prop-core-sys-bool [key] (. Boolean parseBoolean (prop-core-sys key)))

(defn prop-str  [res key] (. res getString key))
(defn prop-int  [res key] (. Integer parseInt (prop-str res key)))
(defn prop-bool [res key] (. Boolean parseBoolean (prop-str res key)))

(defn res-win-str [key]   (. resources-win getString key))

(defn get-install-dir-1 []  
  ;; The install directory is set by the 'light.install.dir' property key
  ;; If this value is empty, attempt to use the current working directory
  (try (let [f  (new File (str (. System getProperty *sys-prop-install-dir*) *name-separator*))
             e? (. f exists)
             w? (. f canWrite)]
         (if (not (and e? w?))
           (throw (RuntimeException. (str "Invalid install directory =>" (. f getAbsolutePath))))
           (. f getAbsolutePath)))
       (catch Exception e (println "WARN <get-install-dir>: unexpected error opening install dir =>" e))))

(defn get-install-dir []
  (if-let [path (get-install-dir-1)]
      path
    (let [f    (new File ".")
          cnf  (new File (str "." *name-separator* *work-path-1*))]
      ;; Also mkdir the configuration/work directories
      (println "INFO: creating work directories =>" (. cnf getAbsolutePath))
      (. cnf mkdirs)
      (. f getAbsolutePath))))

(def *light-install-dir* (get-install-dir))

(def *work-path* (str *light-install-dir* *name-separator* *work-path-1*))

(def *SYS_INSTALL_DIR* "_SYS_INSTALL_DIR_")

(defn system-var-install-dir
  "Replace an instance of the system property within a given string"
  [str]
  ;;;;;;;
  (when str
	(. str replaceAll *SYS_INSTALL_DIR* *light-install-dir*)))

(defn system-variable
  "Replace an instance of the system property within a given string"
  [str var to]
  ;;;;;;;
  (when str (. str replaceAll var to)))

(defn user-variable [key]
  (system-var-install-dir (prop-str resources-user key)))

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;; External Application Constants
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

(def *system-os-name* (. (. System getProperty "os.name") toLowerCase))

(def *is-linux*   (> (. *system-os-name* indexOf "linux")   -1))
(def *is-windows* (> (. *system-os-name* indexOf "windows") -1))

(def *win-tools-dir* (str *name-separator* "tools" *name-separator* "unxutils" *name-separator* 
                          "usr" *name-separator* "local" *name-separator* "wbin" *name-separator*))

;; Note: only invoke the manager when the OS is windows.
(def *win-explorer-exe* "explorer.exe")

(def *win-cmd-exe* "cmd.exe")

(def *win-cygwin-exe* "C:\\cygwin\\Cygwin.bat")

(def *win-cat*    (str *light-install-dir* *win-tools-dir* "light_cat.exe"))
(def *win-cut*    (str *light-install-dir* *win-tools-dir* "light_cut.exe"))
(def *win-diff3*  (str *light-install-dir* *win-tools-dir* "light_diff3.exe"))
(def *win-diff*   (str *light-install-dir* *win-tools-dir* "light_diff.exe"))
(def *win-egrep*  (str *light-install-dir* *win-tools-dir* "light_egrep.exe"))
(def *win-find*   (str *light-install-dir* *win-tools-dir* "light_find.exe"))
(def *win-gawk*   (str *light-install-dir* *win-tools-dir* "light_gawk.exe"))
(def *win-grep*   (str *light-install-dir* *win-tools-dir* "light_grep.exe"))
(def *win-ls*     (str *light-install-dir* *win-tools-dir* "light_ls.exe"))
(def *win-patch*  (str *light-install-dir* *win-tools-dir* "light_patch.exe"))
(def *win-sed*    (str *light-install-dir* *win-tools-dir* "light_sed.exe"))
(def *win-touch*  (str *light-install-dir* *win-tools-dir* "light_touch.exe"))
(def *win-wc*     (str *light-install-dir* *win-tools-dir* "light_wc.exe"))
(def *win-xargs*  (str *light-install-dir* *win-tools-dir* "light_xargs.exe"))

(def *unix-cat*     "cat")
(def *unix-cut*     "cut")
(def *unix-diff3*   "diff3")
(def *unix-diff*    "diff")
(def *unix-egrep*   "egrep")
(def *unix-find*    "find")
(def *unix-gawk*    "gawk")
(def *unix-grep*    "grep")
(def *unix-ls*      "ls")
(def *unix-patch*   "patch")
(def *unix-sed*     "sed")
(def *unix-touch*   "touch")
(def *unix-wc*      "wc")
(def *unix-xargs*   "xargs")

;; Main data structure for holding the external search processes
(def *process-map* {
 :unix-cat   *unix-cat*   :win-cat    *win-cat*
 :unix-cut   *unix-cut*   :win-cut    *win-cut*
 :unix-diff3 *unix-diff3* :win-dif3   *win-diff3*
 :unix-diff  *unix-diff*  :win-diff   *win-diff*
 :unix-egrep *unix-egrep* :win-egrep  *win-egrep*
 :unix-find  *unix-find*  :win-find   *win-find*
 :unix-gawk  *unix-gawk*  :win-gawk   *win-gawk*
 :unix-grep  *unix-grep*  :win-grep   *win-grep*
 :unix-ls    *unix-ls*    :win-ls     *win-ls*
 :unix-patch *unix-patch* :win-patch  *win-patch*
 :unix-sed   *unix-sed*   :win-sed    *win-sed*
 :unix-touch *unix-touch* :win-touch  *win-touch*
 :unix-wc    *unix-wc*    :win-wc     *win-wc*
 :unix-xargs *unix-xargs* :win-xargs  *win-xargs*
})

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;; End Application Constants
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

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