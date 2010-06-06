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

;;; Date: 1/5/2009
;;;       7/15/2009 - Added Clojure 1.0, other performance fixes and cleanups.
;;;      
;;; Main Description: Light Log Viewer is a tool for making it easier to search log files.  
;;; Light Log Viewer adds some text highlighting, quick key navigation to text files, 
;;; simple graphs and charts for monitoring logs, file database to quickly navigate to files of interest, 
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

;;; Metrics: (as of 7/15/2009) Light Log Viewer consists of 6500 lines of Clojure code, and contains wrapper code
;;; around the Java source.  There are 2000+ lines of Java code in the Java library for Light Log Viewer.

;;; Additional Development Notes: The SWT gui and other libraries are launched from a dynamic classloader.  
;;; Clojure is also started from the same code, and reflection is used to dynamically initiate Clojure. 
;;; See the 'start' package.  The binary code is contained in the octane_start.jar library.

;;; Home Page: http://code.google.com/p/lighttexteditor/
;;;  
;;; Contact: Berlin Brown <berlin dot brown at gmail.com>
;;;

(ns octane.toolkit.octane_config)

(import '(java.util ResourceBundle Vector))
(import '(java.io File))

(def *name-separator* (. File separator))
(def *work-path-1*    (str "conf" *name-separator* "sys"  *name-separator* "_work"))

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;; System property keys
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
(def *sys-prop-install-dir* "octane.install.dir")

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;; Resource Files
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;; Note: OCTANE_WIN is loaded from the CLASSPATH (see src/conf/*)
;;       Java classpath (possibly bundled in jar binary)
(def resources-win      (ResourceBundle/getBundle "conf/octane_win"))  
(def resources-core-sys (ResourceBundle/getBundle "octane_core_sys"))
(def resources-win-opts (ResourceBundle/getBundle "octane_win_options"))
(def resources-user     (ResourceBundle/getBundle "octane_user"))

;; GLOBAL STATE (not in public because properties are needed)
(def *main-global-state* (let [gstate (com.octane.global.OctaneGlobalState.)]
                           (.setMergeSetPrimary gstate resources-user)
                           (.setMergeSetSecondary gstate resources-user)
                           gstate))

;; Add type hint to avoid reflection call
(defn prop-core-sys [key]      (.getString #^ResourceBundle resources-core-sys #^String key))
(defn prop-core-sys-int [key]  (. Integer parseInt (prop-core-sys key)))
(defn prop-core-sys-bool [key] (. Boolean parseBoolean (prop-core-sys key)))

(defn prop-str  [res key] (.getString #^ResourceBundle res #^String key))
(defn prop-int  [res key] (. Integer parseInt (prop-str res key)))
(defn prop-bool [res key] (. Boolean parseBoolean (prop-str res key)))

(defn res-win-str [key]   (.getString #^ResourceBundle resources-win #^String key))

(defn get-install-dir-1 []  
  ;; The install directory is set by the 'octane.install.dir' property key
  ;; If this value is empty, attempt to use the current working directory
  (try (let [f  (new File (str (. System getProperty *sys-prop-install-dir*) *name-separator*))
             e? (. f exists)
             w? (. f canWrite)]
         (if (not (and e? w?))
           (throw (Error. (str "Invalid install directory =>" (. f getAbsolutePath))))
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

(def *octane-install-dir* (get-install-dir))

(def *work-path* (str *octane-install-dir* *name-separator* *work-path-1*))

(def *SYS_INSTALL_DIR* "_SYS_INSTALL_DIR_")

(defn system-var-install-dir
  "Replace an instance of the system property within a given string"
  [str]
  ;;;;;;;
  (when str
	(.replaceAll #^String str *SYS_INSTALL_DIR* *octane-install-dir*)))

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

(def *save-history-log* (str *octane-install-dir* *name-separator* "ZZ-octane-sav-history.log"))

(def *win-cat*    (str *octane-install-dir* *win-tools-dir* "octane_cat.exe"))
(def *win-cut*    (str *octane-install-dir* *win-tools-dir* "octane_cut.exe"))
(def *win-diff3*  (str *octane-install-dir* *win-tools-dir* "octane_diff3.exe"))
(def *win-diff*   (str *octane-install-dir* *win-tools-dir* "octane_diff.exe"))
(def *win-egrep*  (str *octane-install-dir* *win-tools-dir* "octane_egrep.exe"))
(def *win-find*   (str *octane-install-dir* *win-tools-dir* "octane_find.exe"))
(def *win-gawk*   (str *octane-install-dir* *win-tools-dir* "octane_gawk.exe"))
(def *win-grep*   (str *octane-install-dir* *win-tools-dir* "octane_grep.exe"))
(def *win-ls*     (str *octane-install-dir* *win-tools-dir* "octane_ls.exe"))
(def *win-patch*  (str *octane-install-dir* *win-tools-dir* "octane_patch.exe"))
(def *win-sed*    (str *octane-install-dir* *win-tools-dir* "octane_sed.exe"))
(def *win-touch*  (str *octane-install-dir* *win-tools-dir* "octane_touch.exe"))
(def *win-wc*     (str *octane-install-dir* *win-tools-dir* "octane_wc.exe"))
(def *win-xargs*  (str *octane-install-dir* *win-tools-dir* "octane_xargs.exe"))

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