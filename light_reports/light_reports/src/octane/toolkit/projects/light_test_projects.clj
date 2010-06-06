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

(ns octane.toolkit.projects.light_test_projects
    (:use octane.toolkit.public_objects
          octane.toolkit.projects.light_test_command
          octane.toolkit.projects.light_test_parser
          octane.toolkit.projects.test.test_process_commands)
    (:import (java.util Date)
             (java.text MessageFormat)
             (java.util.regex Matcher Pattern)
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
             (java.util ResourceBundle Vector Hashtable)
             (java.nio CharBuffer)
             (java.text SimpleDateFormat)
             (java.lang Runtime)
             (java.text SimpleDateFormat)
             (java.nio.channels FileChannel FileChannel$MapMode)
             (java.io InputStreamReader FileInputStream BufferedReader File FilenameFilter)
             (java.util.regex Pattern)
             (java.nio CharBuffer MappedByteBuffer)
             (java.nio.channels FileChannel)
             (java.nio.charset Charset)
             (java.nio.charset CharsetDecoder)
             (java.util.regex Matcher)
             (java.util.regex Pattern)
             (java.util.regex PatternSyntaxException)
             (java.nio ByteBuffer)))

;;(run-cmd-tests)
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;; Core Window static constant defines
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

;; constants

(def *MAJOR_VERSION*
	 0
	 )

(def *MINOR_VERSION*
	 6
	 )

(def *TYPE_VERSION*
	 "alpha"
	 )

(def *DATE_STR_VERSION*
     ;;"20090305"
	 "20090614"
	 )

(def *LIGHT_VERSION* (str  *MAJOR_VERSION* "." *MINOR_VERSION* "." *DATE_STR_VERSION* "." *TYPE_VERSION*))

(def *win-counter* (com.octane.util.Counter.))
(defn win-inc [] (.inc *win-counter*))
(defn win-counter [] (.getValue *win-counter*))

(def  *win-state* (com.octane.util.ReadOnlyState.))
(defn *lock-win-state* [] (.lock *win-state*))
(defn *get-win-state*  [] (.getState *win-state*))
(defn win-loaded? [] (*get-win-state*))

(def win-size-width       640)
(def win-size-height      460)

(def *db-bttn-med-width*  114)
(def *db-button-height*    28)

(def *database-run-cmd-button*     "Run Command")
(def *database-compile-button*     "Compile Tests")
(def *database-runtests-button*    "Run Tests")
(def *database-single-test-button* "Single Test")
(def *database-quit-button*        "Exit")

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

(def *process-gentests-sh* "C:\\usr\\local\\projects\\light_logs\\test\\src\\new_win_tests\\gentests.bat")

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

(def *about-version-msg*
"Light Project Test Tool

Version: {0}

{1}

At least Java Runtime 1.5 is required")

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

(def *Basic_Window_title* "Octane Log Viewer - Test Tool (vers 4/10/2009)")

(def *Basic_About_1* "About")
(def *Basic_About_2* "About")

(def *about-version* (. MessageFormat format *about-version-msg*
						(to-array [*LIGHT_VERSION*  *Basic_About_2*])))

;; Note: the work path may be set automatically by 'light-config' get install directory.
(def *newline* "\n")

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;; RGB colors used when setting the color scheme for a text area.
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
(def orange-sel-color (new RGB 250 209 132))
(def lightgrey-color  (new RGB 100 100 100))
(def red-color        (new RGB 255 0     0))
(def green-color      (new RGB 18  152  14))
(def white-color      (new RGB 255 255 255))
(def cyan-sel-color   (new RGB 64  224 208))
(def dark-blue-color  (new RGB 34  38  167))
(def yellow-color     (new RGB 255 255   0))
(def black-color      (new RGB 10  10   10))

;; Hard code the style to avoid calling bitwise operator
;; SWT.BORDER | SWT.MULTI | SWT.V_SCROLL | SWT.H_SCROLL
(def test-swt-text-style (bit-or SWT/BORDER (bit-or SWT/MULTI (bit-or SWT/H_SCROLL SWT/V_SCROLL))))

(def test-swt-textarea-style (bit-or SWT/BORDER (bit-or SWT/MULTI (bit-or SWT/H_SCROLL SWT/V_SCROLL))))
(def test-swt-tabtext-style  (bit-or SWT/BORDER (bit-or SWT/MULTI (bit-or SWT/H_SCROLL SWT/V_SCROLL))))
(def *database-win-style*    (bit-or SWT/CLOSE (bit-or SWT/BORDER (bit-or SWT/TITLE SWT/MIN))))

(def *openfile-wildcard-seq* ["*.*" "*.log" "*.olog" "*.ologs" "*.octlog"
                              "*.Mon" "*.Tue" "*.Wed" "*.Thu" "*.Fri"])
(def *sysout-wildcard-seq*   ["*.log" "*.Mon" "*.Tue" "*.Wed" "*.Thu" "*.Fri" "*.*"])
(def *jar-wildcard-seq*      ["*.jar" "*.zip" "*.*"])
(def *zip-wildcard-seq*      [ "*.Z" "*.zip" "*.jar" "*.*" ])

;; For Regex Patterns, Flags may include CASE_INSENSITIVE, MULTILINE, DOTALL, UNICODE_CASE, and CANON_EQ
;; Establish the charset and decoder, used with grep functionality.
(def *regex-line-pattern* (. Pattern compile ".*\\r?\\n"))
(def *iso-8859-charset*   (. Charset forName "ISO-8859-15"))
(def *charset-decoder*    (. *iso-8859-charset* newDecoder))

(defn get-char-buf-decoder
  "Get java nio character buffer from decoder"
  [doc]
  ;;;;;;;;;;;
  (let [dummy1 (. *charset-decoder* reset)
        ;; BugFix/Hack, adding newline to end of document
        char-buf (. *charset-decoder* decode (. ByteBuffer wrap (. (str doc \newline) getBytes)))]
    char-buf))

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;; globals
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;; Creation of main GUI components (order of instantiation is important here)
;; Including main tabs, location bar and search box.
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

(defn test-styled-text-font [] (new Font (. *shell* getDisplay) "Courier New" 9 SWT/NORMAL))

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

(defn create-main-text-area
  " The text area is the main(first tab) text area on the window.  Most
 text will get displayed in this text area.  The text area is also attached to a tab."
  [sh]
  ;;;;;;;;;;;;;;;;;;;;
  (let [text (new Text sh test-swt-textarea-style)
		gd-tab (new GridData GridData/FILL GridData/FILL true true)
		disp (Display/getDefault)
		bg   (. disp (getSystemColor SWT/COLOR_WHITE))]
    (doto text
      (. setLayoutData gd-tab)
      (. setFont (test-styled-text-font))
      (. setEditable true)
      (. setBackground bg))
    text))

(defn create-command-text-area
  " The command text area is the main(first tab) text area on the window.  Most
 text will get displayed in this text area.  The text area is also attached to a tab."
  [sh]
  ;;;;;;;;;;;;;;;;;;;;
  (let [text (new Text sh test-swt-textarea-style)
		gd-tab (new GridData GridData/FILL GridData/FILL true true)
		disp   (Display/getDefault)
		bg     (. disp (getSystemColor SWT/COLOR_WHITE))]
    (.setLayoutData text gd-tab)
    (.setFont text (test-styled-text-font))
    (.setEditable text true)
    (.setBackground text bg)
    text))

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

(def *test-win-shell*     (new Shell *shell* *database-win-style*))
(def *test-location-bar*  (Text. *test-win-shell* SWT/BORDER))
(def *text-window*        (create-main-text-area *test-win-shell*))
(def *text-command-area*  (create-command-text-area *test-win-shell*))
(def *test-search-box*    (Text. *test-win-shell* SWT/BORDER))

;; Main Buttons ;;
(def db-button-comp        (Composite. *test-win-shell* SWT/NONE))
(def db-run-cmd-button     (Button. db-button-comp SWT/PUSH))
(def db-compile-button     (Button. db-button-comp SWT/PUSH))
(def db-runtests-button    (Button. db-button-comp SWT/PUSH))
(def db-single-test-button (Button. db-button-comp SWT/PUSH))
(def db-win-close-button   (Button. db-button-comp SWT/PUSH))

(def *test-status-bar* (new Label *test-win-shell* SWT/BORDER))

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;; End of Main Window Widget Components
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

(def *test-buffer-1*   (StringBuffer. 4096))
(def *test-buffer-cmd* (StringBuffer. 2096))
(def *test-fileDialog* (FileDialog. *shell* SWT/CLOSE))

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

(defn test-win-exit [evt]
  ;; Mutate the value for doit ;;
  (set! (. evt doit) false)
  (.setVisible *test-win-shell* false))

(defn length [s]     (count s))
(defn date-time []   (str (java.util.Date.)))
(defn date-timel [l] (str (java.util.Date. l))) 
(defn floor [d] (. Math floor d))

;;;;;;;;;;;;;;;;;;;;
;;;; Patterns
;;;;;;;;;;;;;;;;;;;;
(defn test-pattern [s flags] (. Pattern compile s flags))
(defn test-pattern_ [s]  (. Pattern compile s))
(defn clear-buffer [buf] (. buf setLength 0))

(defn test-trim [s] (when s (. s trim)))

(def  *megabytes*      (* 1024.0 1024.0))
(def  *java-runtime*   (. Runtime getRuntime))

(defn *free-memory-b*  [] (. *java-runtime* freeMemory))
(defn *total-memory-b* [] (. *java-runtime* totalMemory))
(defn *max-memory-b*   [] (. *java-runtime* maxMemory))
(defn *used-memory-b*  [] (- (*total-memory-b*) (*free-memory-b*)))

;; Note: used memory == total memory - free memory
(defn *free-memory-m*  [] (int (floor (/ (*free-memory-b*)  *megabytes*))))
(defn *total-memory-m* [] (int (floor (/ (*total-memory-b*) *megabytes*))))
(defn *max-memory-m*   [] (int (floor (/ (*max-memory-b*)   *megabytes*))))
(defn *used-memory-m*  [] (int (floor (/ (*used-memory-b*)  *megabytes*))))

(defn *file-size-m*    [file-size] (/ file-size  *megabytes*))

(defn *memory-usage* []
  (str "(" (*used-memory-m*) "M/" (*free-memory-m*) "M [" (*total-memory-m*) "M," (*max-memory-m*) "M ])"))

(def  *dir-date-format*         (new SimpleDateFormat "MM-dd-yyyy hh:mm.ss a"))
(def  *simple-date-format*      (new SimpleDateFormat "MM/dd/yyyy"))
(def  *simple-date-format-t*    (new SimpleDateFormat "MM/dd/yyyy HH:mm:ss"))
(def  *simple-date-format-pack* (new SimpleDateFormat "MMddyyyy"))
(def  *current-date*            (.format *simple-date-format* (new Date)))

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

(defmacro when-try [body]
  `(try ~body
        (catch Exception ~'e
               (println "ERR <when-try> " ~'e)
               nil)))

(defmacro when-try_ [body]
  `(try ~body
        (catch Exception ~'e
               (println "ERR <when-try> " ~'e)
               nil)))

(defmacro proc-time [expr]
  `(let [start# (. System (nanoTime))
                ret#     ~expr
                res-t#   (/ (double (- (. System (nanoTime)) start#)) 1000000.0)
                str-res# (str "Elapsed time: " res-t# " msecs")]
     {:return ret# :time-text str-res# :timed res-t#}))

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

(def status-set-text)

(defn set-cursor-cmd-end
  "Set the cursor to the end of the textarea for
 the command window"
  []
  ;;;;;
  (.setSelection *text-command-area* (.getCharCount *text-command-area*)))

(defn display-error [msg]
  (doto (MessageBox. *test-win-shell* SWT/ICON_ERROR)
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
     (. Thread sleep 40)
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
  (add-text-buffer *text-window* *test-buffer-1* str-data))

(defn async-add-text [disp text-field buffer str-data]
  ;; For example, text-field = text-window
  (async-call disp (add-text-buffer text-field buffer str-data)))

(defn async-add-main-text [str-data]
  ;; For example, text-field = text-window
  (async-call *display* (add-main-text str-data)))

(defn add-main-text-nc [line]
  ;; Add the main text without clearing the core buffer
  ;; Note '*test-buffer-1*' and text-window used as 
  ;; GUI components. (NC = no clear)
  (try (let [buffer *test-buffer-1*
             text-gui *text-window*]
         (. buffer append (str line *newline*))
         (. text-gui setText (. buffer toString)))
       ;; Attempt to redraw and update
       (catch Exception e (println e))))

(defn add-command-text-nc [line]
  ;; Add the command text without clearing buffer
  ;; Note '*test-buffer-cmd*' (command buffer) and text-window used as 
  ;; GUI components. (NC = no clear)
  (try (let [buffer *test-buffer-cmd*
             text-gui *text-command-area*]
         (.append buffer (str line *newline*))
         (async-call *display* (.setText text-gui (. buffer toString)))
         (set-cursor-cmd-end))
       ;; Attempt to redraw and update
       (catch Exception e (println e))))

(defn set-command-text-nc [str-data]
  ;; Set the command text, clear the buffer first.
  ;; Note '*test-buffer-cmd*' (command buffer) and text-window used as 
  ;; GUI components. (NC = no clear)
  (try (let [buffer *test-buffer-cmd*
             text-gui *text-command-area*]
         (clear-buffer buffer)
         (.append buffer str-data)
         (async-call *display* (.setText text-gui (. buffer toString)))
         (set-cursor-cmd-end))
       ;; Attempt to redraw and update
       (catch Exception e (println e))))

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

(defn create-menu-item [menu menu-text proxy-body]
  (let [menu-item (MenuItem. menu (. SWT PUSH))]
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
   (let [p (ProcessBuilder. process-line)]
	 (.redirectErrorStream p true))))
  
(defn start-process [proc-args-lst buffer]
  ;; Example usage: (start-process [ "explorer.exe" ] *test-buffer-1*)
  (try
   (let [process-line (into-array proc-args-lst)
         process-bld  (when process-line (new-process-builder process-line))
         process      (when process-bld  (when-try (. process-bld start)))]
     (when process
       (async-status-history *display* (str "Invoking process => " proc-args-lst *newline*))
       (.info *logger* (str "Invoking process => " proc-args-lst *newline*))
       ;; Wrap the request within a thread.
       (let [proc-thread
             (proxy [Runnable][]
                    (run []
                         (let [istream   (. process getInputStream)
                               ireader   (InputStreamReader. istream)
                               bufreader (BufferedReader. ireader)]
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
  (. *test-status-bar* setText text)
  (. *test-status-bar* update))

(defn location-set-text [text]
  (. *test-location-bar* setText text)
  (. *test-location-bar* update))

(defn shell-display-loop [disp sh dispose? msg]  
  (loop [] (if (. sh (isDisposed))
             (if dispose? (. disp dispose) (println msg))
             (let []
               (when (not (. disp (readAndDispatch)))
                 (. disp (sleep)))
               (recur)))))

(defn create-about-messagebox [sh]
  (let [msgbox (MessageBox. sh SWT/NONE)
        about1 *Basic_About_1*
        about2 *about-version*]
    (. msgbox setText about1)
    (. msgbox setMessage about2)
    (. msgbox open)
    msgbox))

(defn create-info-messagebox [sh titl msg]
  (let [msgbox (MessageBox. sh SWT/NONE)]
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

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

(def shell-close-listener
     (proxy [ShellAdapter] [] 
	   (shellClosed [evt] (test-win-exit evt))))

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

;; Listeners and Event Handlers for Compiling and Running the Tests
;; The event will spawn a script process that compiles the tests of interest

;; Invoke the compile process and log the output to the main window"
;; The following tests are available:
;;     compile, runtests, singletest, singlemem, singlehprof
(defmacro def-start-process [test-type]
  `(let [test-props-bean# ""]
     (println "Attempt to start process =>" test-props-bean#)
	 (start-process [ *process-gentests-sh* ~test-type "" ] *test-buffer-1*)))

(defmacro cmd-start-process [cmd]
  ;; Use the macro '#' syntax to create unique let vars
  `(when (.hasActiveState *commands-state*)
     (status-set-text (str "Invoking command : " ~cmd))
     (let [command#  (.getActiveState *commands-state*)
           msg-2# (str "[command-start-proc] Attempt to start process =>" command# " cmd:" ~cmd)]
       (.info *logger* msg-2#)
       (println msg-2#)
       (start-process [ "C:\\ant\\bin\\ant.bat" "-f" (.getFilePath command#) ~cmd ] *test-buffer-1*))))

(defmacro def-button-listener [test-type]
  `(proxy [~'SelectionListener][]
	 (widgetSelected [event#] (cmd-start-process ~test-type))
	 (widgetDefaultSelected [event#] (cmd-start-process ~test-type))))

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

(def button-close-listener
     (proxy [SelectionListener][]
	   (widgetSelected [event] (test-win-exit event))
	   (widgetDefaultSelected [event] (test-win-exit event))))

(defmacro doto-add-button [bttn text-bttn test-type]
  `(doto ~bttn
	 (. setText ~text-bttn)
	 (. setLayoutData ~'gd-button)
	 (.addSelectionListener (def-button-listener ~test-type))))

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;;; COMMAND HANDLER ;;;

(defn main-command-handler
  [cmd args]
  ;;;
  (when cmd
    (cond 
     (= "help" cmd)
     (do (add-command-text-nc (print-commands-msg))
         (set-cursor-cmd-end))
     (= "projects" cmd)
     (do (add-command-text-nc (projects-listing)))
     (= "compile" cmd)
     (do (cmd-start-process "compile"))
     (= "run" cmd)
     (do (cmd-start-process "run"))
     (= "single" cmd)
     (do (cmd-start-process "single"))
     (= "exit" cmd)
     (do (.setVisible *test-win-shell* false)))))
     
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

(defn run-command-handler
  []
  ;;;
  (let [cmd-line (get-sync-call *display* (.getText *test-search-box*))]
    (if (empty? cmd-line)
      (status-set-text "Invalid command, make sure that the command-line is not empty")
      (do (status-set-text (str "Invoking command : " cmd-line))
          (parse-project-command cmd-line main-command-handler)))))

(def run-command-listener
     (proxy [SelectionListener][]
            (widgetSelected [event] (run-command-handler))
            (widgetDefaultSelected [event] (run-command-handler))))

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
  
(defn init-buttons-composite 
  "Set the layout, width and height for the buttons"
  [sh]
  ;;;;;;;
  (let [comp db-button-comp
        gd-comp   (new GridData)
        gd-button (new RowData *db-bttn-med-width* *db-button-height*)]
    (. comp setLayoutData gd-comp)
    (. comp setLayout (new RowLayout))
    ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
    (doto db-run-cmd-button
      (. setText *database-run-cmd-button*)    
      (.setLayoutData gd-button)
      (. addSelectionListener run-command-listener))
	(doto-add-button db-compile-button  *database-compile-button* "compile")
	(doto-add-button db-runtests-button *database-runtests-button* "run")
	(doto-add-button db-single-test-button *database-single-test-button* "singletest")
    (doto db-win-close-button
	  (.setText *database-quit-button*)
	  (.setLayoutData gd-button)
	  (.addSelectionListener button-close-listener))))

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;; Continue
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

(def find-text-listener
     (proxy [Listener] []
            (handleEvent [event]
                         (when (= (. event detail) SWT/TRAVERSE_RETURN)
                           ;; When using the traverse event
                           ;; when the user hits enter while at the
                           ;; edit entry box.
                           (run-command-handler)
                           (refresh-textarea)))))

(defn create-grid-layout []
  (let [gridLayout (GridLayout.)]
    (set! (. gridLayout numColumns) 1)
    gridLayout))

(defn create-menu-bar [disp sh]
  (when (not (win-loaded?))
    (let [bar (new Menu sh (. SWT BAR))]
      (. sh setMenuBar bar)
      bar)))
  
(defn create-shell [disp sh]
  ;; Note change in 'doto' call, dot needed.
  (let [layout (create-grid-layout)]
    (doto sh
      (. setText *Basic_Window_title*)
      (. setLayout layout)
      (. addShellListener (proxy [ShellAdapter] []
                                 (shellClosed [evt] (test-win-exit evt)))))))

(defn init-gui-helper [disp sh]
  (create-menu-bar disp sh)
  (create-shell    disp sh)
  (init-buttons-composite sh)
  ;; Add the command header message:
  (set-command-text-nc (get-command-header))
  ;; After gui helper method added, parse the command XML
  (parse-xml-command-file (try-open-command-file))
  (add-command-text-nc (print-commands-msg))
  (set-cursor-cmd-end)
  ;; Init complete, set the startup message
  (status-set-text (str "Run Project Tool loaded " (date-time) " " (*memory-usage*))))

(defn create-gui-window 
  "Initialize the SWT window, set the size add all components"
  [disp sh]
  ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;  
  ;; Set the tab folder and items with the main text area
  ;; and other SWT oriented inits.
  (init-gui-helper disp sh)    
  ;; Modify already created objects
  (let [gd (GridData. SWT/FILL SWT/FILL true false)]
    (. *test-search-box* addListener SWT/Traverse find-text-listener)
    (. *test-search-box* setLayoutData gd)
    (. *test-location-bar* setLayoutData gd)
	(. *test-status-bar* setLayoutData gd))
  ;; Final init, set the window size and then open
  (doto sh
    (. setSize win-size-width win-size-height)
    (. open))
  (*lock-win-state*)
  (println "test project loop disposed"))

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;; Application Main Entry Point
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
(defn main-1 
  " Application Entry Point for project window, launch the main window and wait for events"
  []
  ;;;;;;;;;
  (println "Launching Octane Test Text Viewer...[" (win-counter) "]")
  (win-inc)
  (if (win-loaded?) 
    (do (println "Window already loaded, opening")
        (.setVisible *test-win-shell* true)
        (.setFocus *test-search-box*))
    (do (create-gui-window *display* *test-win-shell*)
        (.setFocus *test-search-box*))))

(defn projects-win-main [& args]
  (try (main-1)
	   (catch Exception e
              (.error *logger* "ERR at light-test-projects <Main [3]>: " e)
              (.printStackTrace e)
              (println "ERR at light-test-projects <Main [3]>: " e))))

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