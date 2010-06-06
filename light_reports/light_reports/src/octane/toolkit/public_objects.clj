;;;
;;; Copyright (c) 2006-2010 Berlin Brown and botnode.com  All Rights Reserved
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
;;;

(ns octane.toolkit.public_objects
	(:import (java.util Date)
             (org.apache.log4j Logger)
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

(def #^org.apache.log4j.Logger *logger* (Logger/getLogger "com.octane.start.OctaneLauncherMain"))

(def tab-folder)

(def swt-textarea-style (bit-or SWT/BORDER (bit-or SWT/MULTI (bit-or SWT/H_SCROLL (bit-or SWT/V_SCROLL 1)))))
(def swt-tabtext-style  (bit-or SWT/BORDER (bit-or SWT/MULTI (bit-or SWT/H_SCROLL (bit-or SWT/V_SCROLL 1)))))

;; see init-colors in gui-utils for where the colors vec is populated.
;; colors are defined in main-constants
(def colors-vec  (Vector.))
;; Where buffer-1 is the main buffer
;; buffer-3 is the history buffer
(def buffer-1    (new StringBuffer 6296))
(def buffer-2    (new StringBuffer 6296))
(def buffer-3    (new StringBuffer 6296))
(def buffer-4    (new StringBuffer 6296))

(def *main-text-buffer* buffer-1)
(def *history-buffer* buffer-3)
(def *command-buffer* buffer-4)

(def *display*   (Display.))
(def *shell*     (Shell. *display*))

(defn styled-text-font [] (new Font (. *shell* getDisplay) "Courier New" 9 SWT/NORMAL))

(def fileDialog       (new FileDialog *shell* SWT/CLOSE))
(def directory-dialog (new DirectoryDialog *shell* SWT/CLOSE))

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;; Creation of main GUI components (order of instantiation is important here)
;; Including main tabs, location bar and search box.
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
(def location-bar (new Text *shell* SWT/BORDER))
(def tab-folder   (new TabFolder *shell* SWT/NULL))
(def tab-area-1   (new TabItem tab-folder SWT/NULL))
(def tab-area-2   (new TabItem tab-folder SWT/NULL))
(def tab-area-3   (new TabItem tab-folder SWT/NULL))
(def tab-area-4   (new TabItem tab-folder SWT/NULL))
(def search-box   (new Text *shell* SWT/BORDER))

;; Tab-text-2 is associated with the secondary text area
(def tab-text-2   (new Text tab-folder swt-tabtext-style))
(def tab-text-3   (new Text tab-folder swt-tabtext-style))
(def tab-text-4   (new Text tab-folder swt-tabtext-style))

(def status-bar   (new Label *shell* SWT/BORDER))

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;; Create the styled text area
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

(defn create-styled-text-area
  " The styled text area is the main(first tab) text area on the window.  Most
 text will get displayed in this text area.  The text area is also attached to a tab. 
 @see octane_main_window.clj"
  [sh]
  ;;;;;;;;;;;;;;;;;;;;
  (let [text   (new StyledText sh swt-textarea-style)
        gd-tab (new GridData GridData/FILL GridData/FILL true true)
        disp   (Display/getDefault)
        bg     (. disp (getSystemColor SWT/COLOR_WHITE))]
    (. tab-folder setLayoutData gd-tab)
    (doto text
      (.setLayoutData gd-tab)
      (.setFont (styled-text-font))
      (.setEditable true)
      (.setBackground bg))
    text))

(def *styled-text* (create-styled-text-area tab-folder))

;;;;;;;;;;;;;;;;;
;; Keep the state on when a directory/open has been set
;;;;;;;;;;;;;;;;;
(def  *directory-open-state*    (ref nil))
(defn get-directory-open []     (deref *directory-open-state*))
(defn set-directory-open [path] (dosync (ref-set *directory-open-state* path)))

(def  *curfile-open-state*    (ref nil))
(defn get-curfile-open []     (deref *curfile-open-state*))
(defn set-curfile-open [path] (dosync (ref-set *curfile-open-state* path)))

(defn get-current-dir [] 
  (if (get-directory-open) (get-directory-open) "."))

(defn get-current-dirquote [] 
  (if (get-directory-open) (str "'" (get-directory-open) "'") "."))

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;; Find Grep Widgets
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

;; Use ref for public findgrep widgets, and dynamic keyword
;; Note, the widgets must be loaded at runtime.

(def  *findgrep-def-state*  (ref {:FindGrep_grep_menuitem   nil}))
(def  *findgrep-15m-state*  (ref {:FindGrep_15min_menuitem  nil}))
(def  *findgrep-2hr-state*  (ref {:FindGrep_2hrs_menuitem   nil}))
(def  *findgrep-jav-state*  (ref {:FindGrep_java_menuitem   nil}))
(def  *findgrep-log-state*  (ref {:FindGrep_logs_menuitem   nil}))
(def  *findgrep-60m-state*  (ref {:Findfiles_60min_menuitem nil}))
(def  *findgrep-clj-state*  (ref {:FindGrep_clj_menuitem    nil}))

(defn findgrep-widg-state [fkey]
  (cond (= fkey :FindGrep_grep_menuitem)   *findgrep-def-state*
        (= fkey :FindGrep_15min_menuitem)  *findgrep-15m-state*
        (= fkey :FindGrep_2hrs_menuitem)   *findgrep-2hr-state*
        (= fkey :FindGrep_java_menuitem)   *findgrep-jav-state* 
        (= fkey :FindGrep_logs_menuitem)   *findgrep-log-state*
        (= fkey :Findfiles_60min_menuitem) *findgrep-60m-state*
        (= fkey :FindGrep_clj_menuitem)    *findgrep-clj-state*))

(defn get-findgrep-widg-state [fkey]      (deref  (findgrep-widg-state fkey)))
(defn set-findgrep-widg-state [fkey widg] (dosync (commute (findgrep-widg-state fkey) assoc fkey widg)))

(defn get-findgrep-helper [key] ((get-findgrep-widg-state key) key))

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;; Find next 'matcher' state
;; Used with octane_search_dialog and the 'find' tool
(def  *find-next-state*           (ref nil))
(defn get-find-next-state []      (deref *find-next-state*))
(defn set-find-next-state [match] (dosync (ref-set *find-next-state* match)))

(defn clear-find-next-state 
  "Clear the previous matcher after files are loaded, etc."
  []
  ;;;
  (set-find-next-state nil))

(defn xml-tag-content
  "Simple utility to get the tag content.  Return a list"  
  [x-set]
  ;;;;;;;;
  (when x-set (list (x-set :content) (x-set :tag))))

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

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;;; End of Script
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;