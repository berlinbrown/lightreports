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

(ns octane.toolkit.projects.light_test_command
    (:use octane.toolkit.public_objects)
    (:import (java.util Date)
             (java.text MessageFormat)
             (java.util.regex Matcher Pattern)
             (org.eclipse.swt SWT)))

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

(def *commands-state* (com.octane.util.ProjectCommandsState.))

(defn my-date-time [] (str (java.util.Date.)))

;; The on application load header message 
(def command-list-msg
"
--------------------------------------------------
* Project Command Listing
* Total Projects : {0}
--------------------------------------------------
")
(def command-list-msg-end 
"--------------------------------------------------")

(def command-help
"
--------------------------------------------------
* Command Help
*
* Command     | Arguments  |    Description
* ==========  | =========  |    ===================
* 'create'    | <project-name>  Create a new project
* 'help'      | N/A             Print the help message
* 'projects'  | N/A             Print the current projects
* 'compile'   | N/A             Invoke the compile command
* 'run'       | N/A             Invoke the run command
* 'single'    | N/A             Invoke the single test command
--------------------------------------------------
")

(defn get-command-list-header []
  (. MessageFormat format command-list-msg
     (to-array [ (str (.count *commands-state*)) ])))

(defn new-command-state
  "Instantiate a new command state
 Seven constructor arguments.  Ensure that the arguments are of type string"
  [#^String fname #^String project-home #^String path #^String cmpl-cmd #^String run-cmd #^String singl-cmd #^String singl-class]
  ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
 (com.octane.util.CommandState. 
  #^String fname 
  #^String project-home 
  #^String path 
  #^String cmpl-cmd 
  #^String run-cmd 
  #^String singl-cmd 
  #^String singl-class))

;; command template
(def command-template-msg
"
**************************************************
* Octane Run Command Init
* Launched at {0}
**************************************************
")

(defn get-command-header []
  (. MessageFormat format command-template-msg
     (to-array [ (str (new java.util.Date)) ])))

(def *project-command-xml* "usr/local/projects/light_logs/conf/sys/_workspace_project_commands.xml")

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

(defn build-command-state
  [full-tag state]
  ;;;;;;;;;;;;
  (let [x-tag (full-tag :tag)
        x-content (full-tag :content)]
    (cond
     ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
     (= ":file_name" (str x-tag))
     (.setFileName state (.. (str (first x-content)) trim))
     (= ":project_home" (str x-tag))
     (.setProjectHome state (.. (str (first x-content)) trim))
     (= ":file_path" (str x-tag))
     (.setFilePath state (.. (str (first x-content)) trim))
     (= ":compile_command" (str x-tag))
     (.setCompileCommand state (.. (str (first x-content)) trim))
     (= ":run_command" (str x-tag))
     (.setRunCommand state (.. (str (first x-content)) trim))
     (= ":single_command" (str x-tag))
     (.setSingleCommand state (.. (str (first x-content)) trim))
     (= ":single_class" (str x-tag))
     (.setSingleClass state (.. (str (first x-content)) trim))
     (= ":active" (str x-tag))
     (if (= "true" (.. (str (first x-content)) trim))
       (.setActive state true)
       (.setActive state false)))
     ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
     ;;; Return the Updated Command State ;;;
    state))
  
(defn loop-xml-commands
  "Add the single commands to the command state object"
  [root-content]
  ;;;;;;;;;;;;;;;
  (when root-content
    (doseq [xml-cmd root-content]
        (let [all-proj-data (xml-cmd :content)
              state (com.octane.util.CommandState/createEmptyState)]
          (doseq [full-tag all-proj-data]
              ;; At this point, the state object is updated
              ;; with the information from the XML file
              ;; for a specific project tag.
              ;; Add to the project commands state
              (build-command-state full-tag state))
          (.addProjectCommandStateInit *commands-state* state)))
    ;; The init command state is complete and we
    ;; lock the project commands state
    (.lock *commands-state*)))
      
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
        
(defn parse-xml-command-file
  "Parse the XML file and return the java object data structure.
  @return  Java object data structure"
  [xml-data]
  ;;;;;;;;;;;
  (when xml-data
    (let [root (xml-data :content)]
      (doseq [root-set root]
          (let [root-tag (root-set :tag)
                root-content (root-set :content)]
            (when (= ":project_commands" (str root-tag))
              (loop-xml-commands root-content)))))))

(defn try-open-command-file
  "Open database command file, invoked on command project application load."
  []
  ;;;;;
  (try (let [xml-data (clojure.xml/parse (str "file:///" *project-command-xml*))]
         xml-data)
       (catch Exception e
              ;; Build error message and return nil.
              (let [err-buf (str "ERR: while opening project file : " (. e getMessage))]
                (println (. err-buf toString)))
              nil)))

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

(defn projects-listing
  "Print a formatted string of the projects
 @return  Formatted String with the project listing
 @see     'use of apply/format'"
  []
  ;;;
  (let [cmd-buf (StringBuffer. 256)
        ctr (com.octane.util.Counter.)]
    (doseq [state (.getProjectCommandStates *commands-state*)]        
        ;; Append the string/per project info as formatted string
          (do (.append cmd-buf
                       (str (apply format "* [%3s] | [%7s] | %28s | %34s\n"
                                 (list (str ctr)
                                       (.isActive state)
                                       (.getFileName state)                                 
                                       (.getFilePath state)))))
              (.inc ctr)))
    (.toString cmd-buf)))

(defn print-commands-msg
  "Print the header message related to the project commands state.
 @return  String with the command state information"
  []
  ;;;;;;
  (let [cmd-buf (StringBuffer. 256)]
    (.append cmd-buf (str "" (my-date-time)))
    ;; First, add the command list header
    (.append cmd-buf (get-command-list-header))
    (.append cmd-buf (projects-listing))
    (.append cmd-buf command-list-msg-end)
    (.append cmd-buf command-help)
    ;; Command list information set, return buffer data
    (.toString cmd-buf)))

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
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
;; -------------------------------------

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
 
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;;; End of Script
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;