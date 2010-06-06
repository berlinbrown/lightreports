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

(ns octane.toolkit.octane_file_utils
	(:use octane.toolkit.octane_main_constants
		  octane.toolkit.octane_config
		  octane.toolkit.public_objects
          octane.toolkit.octane_utils_common
		  octane.toolkit.octane_utils
		  octane.toolkit.octane_gui_utils)
	(:import (org.eclipse.swt.graphics Color RGB)
			 (org.eclipse.swt SWT)
			 (java.text MessageFormat)
			 (java.util HashMap)
             (org.eclipse.swt.widgets FileDialog)
			 (org.eclipse.swt.events VerifyListener SelectionAdapter ModifyListener SelectionListener
									 SelectionEvent ShellAdapter ShellEvent)
			 (org.eclipse.swt.widgets Label Menu MenuItem Control Listener)
             (java.io BufferedReader LineNumberReader File FileInputStream
                      ObjectInputStream FileNotFoundException 
                      IOException InputStreamReader Reader
                      ByteArrayOutputStream ObjectOutputStream FileOutputStream)
             (java.util ResourceBundle Vector Hashtable)
             (java.util.jar JarFile)
             (java.util.jar JarInputStream)
             (java.util.zip ZipEntry)))

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

(def open-jar-file)
(def open-file-util)
(def open-file)
(def file-monitor-loop)
(def get-file-state)
(def set-file-state)
(def get-file-info-header)
(def add-recent-file)
(def add-recent-buffer-menu)
(def save-file-list)
(def load-file-list)

(def *sysout-timestamp-regex*  "(\\[(.*?)\\]\\s[0-9]{8})")

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;; Recent menu buffer routines
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

(def recent-menu-state   (new HashMap))
(def recent-buffer-state (new HashMap))

(def  buffer-menu-state            (ref {:menu-state nil}))
(defn get-buffer-menu-state []     (@buffer-menu-state :menu-state))
(defn set-buffer-menu-state [menu] (dosync (commute buffer-menu-state assoc :menu-state menu)))


(def  recent-file-table            (ref {:file-table nil}))
(defn get-recent-file-table []     (@recent-file-table :file-table))
(defn set-recent-file-table [tabl] (dosync (commute recent-file-table assoc :file-table tabl)))

(def recent-menu-listener
     (proxy [SelectionAdapter] []
            (widgetSelected [evt]
                            (let [widg (. evt widget)
                                  w-data (. recent-menu-state get widg)]
                              (when w-data
                                (let [path (w-data :path)]
                                  (open-file path false)))))))

(def recent-buffer-listener
     (proxy [SelectionAdapter] []
            (widgetSelected [evt]
                            (let [widg (. evt widget)
                                       w-data (. recent-buffer-state get widg)]
                              (when w-data
                                (let [path (w-data :path)]
                                  (open-file path false)))))))

(defn create-recent-menu-items [menu]
  ;; The recent items are a deserialized hashtable
  (let [file-table (load-file-list)
                   file-seq (when file-table (seq (. file-table entrySet)))]
    (when file-seq
      (doseq [i file-seq]
          ((fn [entry]
              (let [fname (. entry getKey)
                          fval (. entry getValue)
                          item-rec (new MenuItem menu (. SWT PUSH))
                          #^java.util.Hashtable rec-tabl (get-recent-file-table)
                          rec-path (.get rec-tabl fname)]
                (. item-rec setText fname)
                (. item-rec addSelectionListener recent-menu-listener)
                (. recent-menu-state put item-rec {:widget item-rec :path rec-path})))
              i)))))

(defn add-recent-buffer-menu [tabl-obj file]
  (try (let [menu (get-buffer-menu-state)
                  name (. file getName)
                  path (. tabl-obj get name)
             rec-buf-item (new MenuItem menu (. SWT PUSH))]
         (. rec-buf-item setText name)
         (. rec-buf-item addSelectionListener recent-buffer-listener)
         (. recent-buffer-state put rec-buf-item {:widget rec-buf-item :path path}))
       (catch Exception e
              (println "ERR: add-recent-buffer-menu" e))))

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;; Main open file dialog function
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

(defn on-file-open [file]
  ;; Use the file instance for further operations
  (set-file-state true)
  (let [info-txt (get-file-info-header)]
    (when info-txt (history-add-text info-txt)))
  (file-monitor-loop file)
  (let [rec-tabl (add-recent-file file)]
    ;; reuse the hashtable datastructure to add to the recent buffer
    ;; list.
    (add-recent-buffer-menu rec-tabl file))
  (save-file-list))

(defn open-file-listener [file-str-data]
  (proxy [Runnable] []
         (run []
              (add-main-text file-str-data)
              ;; Attempt to set cursor/caret to the end
              ;; when refresh is enabled.
              (when (prop-bool resources-win-opts "file_monitor_enabled")
                (. *styled-text* setSelection (. *styled-text* getCharCount))))))

(defn check-file-handler
  "Determine the archive type based on the extension and handle accordingly"
  [#^File file path]
  ;;;;;;;;;;;;;;;;;
  (cond (.endsWith path ".jar") (do (open-jar-file file)
                                    :open-jar-file)
		:else nil))

(defn open-file
  " Attempt to open a file and set the content to the main buffer."
  [name quiet & [disable-refresh]]
  ;;;;;;;;;;;;;;;;;;;;;;
  (when name
    (when (not quiet)
	  (async-status-history *display* (str "Loading file => " name)))
    (let [#^File file (File. #^String name)]
      (set-curfile-open name)
      (location-set-text name)
      (if (not (. file exists)) (display-error "File does not exist")
          ;; File exists, also check the extension for other
          ;; file opening utils
		  (let [#^org.eclipse.swt.widgets.Display disp (.getDisplay #^org.eclipse.swt.widgets.Widget *styled-text*)
                file-str-data (open-file-util file (. file getPath))]
            (if (check-file-handler file (.getPath file)) :open-file-ext
                (do 
                  ;; Set the file state opened, and start monitor loop
                  (on-file-open file)
                  (println "debug: memory usage after file open : " (*memory-usage*))
                  ;; Check for file last modified
                  (. disp asyncExec (open-file-listener file-str-data)))))))))

(defn dialog-open-file 
  "On some input widget event, invoke the open dialog.  When the user
 selects a file, the filename is returned from FileDialog.open."
  []
  ;;;;;;
  (let [my-dialog #^org.eclipse.swt.widgets.FileDialog fileDialog]
    (.setText my-dialog "Open File")
    (.setFilterExtensions my-dialog (into-array *openfile-wildcard-seq*))
    (open-file (.open my-dialog) false)))

(defn dialog-save-as-file
  "On some input widget event, invoke the save as dialog.  When the user
 selects a file, the filename is returned from FileDialog.open."
  []
  ;;;;;;
  (let [fd (FileDialog. *shell* SWT/SAVE)]
    (.setText fd "Save File As")
    (.setFilterExtensions fd (into-array ["*.*"]))
    (.open fd)))

(defn simple-openfile-handler 
  "This function is used with the simple dialog file opener, when the dialog
 opener is invoked, this handler function will get invoked with the path of the
 file to open.  
 Where file-handler takes the following arguments : <DISPLAY> <FILE> <PATH>"
  [my-disp path file-handler] 
  ;;;;;;;;;;;;;;;;
  (async-status-history my-disp (str "Loading file => " path))
  (let [file (new File #^String path)]
    ;; TODO: add place to clear the FIND search
    ;; normally we would put this somewhere else.  CLEAR FIND.
    (clear-find-next-state)
	(set-curfile-open path)
	(location-set-text path)
	(file-handler my-disp file path)))

(defn simple-dialog-open-file
  "On some input widget event, invoke the open dialog.  When the user
 selects a file, the filename is returned from FileDialog.open
 Where file-handler takes the following arguments : <DISPLAY> <FILE> <PATH>"
  [disp fn-handler wild-cards]
  ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
  (. fileDialog setText "Open File")
  (. fileDialog setFilterExtensions (into-array wild-cards))
  (when fn-handler
	(when-let [path-file (. fileDialog open)]
        (clear-find-next-state)
        (simple-openfile-handler disp path-file fn-handler))))

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;; Note: open file dialog is in octane_core_widgets.clj
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
(def cur-file-info (ref {:file-name nil :file-path nil :last-mod nil :line-num 0 :file-size 0
						:parent-dirname nil :writeable false :exists false}))
(defn set-file-info [nm pth mod-t n prnt w xsts sz]
  (dosync (commute cur-file-info assoc 
                   :file-name nm :file-path pth :last-mod mod-t :file-size sz
                       :line-num n :parent-dirname prnt :writeable w :exists xsts)))

(defn get-file-info-header []
  (when (and (not (nil? @cur-file-info)) (get-file-state))
    (. MessageFormat format 
       file-info-msg (to-array [(date-timel (@cur-file-info :last-mod)) (@cur-file-info :line-num)
                                (@cur-file-info :file-name) (@cur-file-info :parent-dirname)
                                (@cur-file-info :file-path) (@cur-file-info :file-size)
                                (*memory-usage*)]))))

(defn get-simple-file-info [msg]
  (when (and (not (nil? @cur-file-info)) (get-file-state))
    (. MessageFormat format 
       msg (to-array [(date-timel (@cur-file-info :last-mod)) (@cur-file-info :line-num)
					  (@cur-file-info :file-name) (@cur-file-info :parent-dirname)
					  (@cur-file-info :file-path) (@cur-file-info :file-size)
					  (*memory-usage*) 
					  (*file-size-m* (@cur-file-info :file-size))
					  ]))))
     
(def  file-state             (ref {:open-state false}))
(defn get-file-state []      (@file-state :open-state))
(defn set-file-state [state] (dosync (commute file-state assoc :open-state state)))

(def  file-last-mod          (ref {:last-mod 0 :file-path nil}))
(defn get-file-last-mod  []  (@file-last-mod :last-mod))
(defn get-last-file-path []  (@file-last-mod :file-path))
(defn set-file-last-mod  [t name] (dosync (commute file-last-mod assoc :last-mod t :file-path name)))

(defn file-modified? [file]
  (let [mod-t  (. file lastModified)
        prev-t (get-file-last-mod)
        diff   (- mod-t prev-t)
        pth    (. file getAbsolutePath)]
    (when (> diff 0)
      (set-file-last-mod mod-t pth)
      true)))
                
(defn file-monitor-loop [#^File file]
  (let [delay-t (prop-int #^java.util.ResourceBundle resources-core-sys "Octane_Sys_filemonitor_delay")
        enable-file-mon (prop-bool resources-win-opts "file_monitor_enabled")
        pth    (. #^File file getAbsolutePath)]
    (when enable-file-mon
      (.start (new Thread (fn []
                              (while (not (. #^org.eclipse.swt.widgets.Shell *shell* isDisposed))
                                     (Thread/sleep delay-t)
                                     (when (file-modified? file)
                                       ;; Reload the file as it grows and refresh
                                       ;; the file.
                                       (open-file pth true)))))))))

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;; File Utilities
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
(defn open-file-util 
  "Use java oriented approach for loading a file into memory"
  [#^File file file-path]
  ;;;;;;;;;;;;;;;;;;
  ;; Java oriented approach for opening file
  (let [stream (new FileInputStream #^String file-path)
        instr (new LineNumberReader (new InputStreamReader stream))
        ;; Use type hints to ensure a character type.
        readBuffer #^"[C" (make-array (Character/TYPE) 6448)
        buf (StringBuffer. 7000)]
    (loop [n (.read instr readBuffer)]
      (when (> n 0)
        (.append buf readBuffer 0 n)
        (recur (.read instr readBuffer))))
    ;; File info data has been collected, set some of the file properties
    (set-file-info (. file getName) 
				   (. file getAbsolutePath) 
                   (. file lastModified) 
				   (. instr getLineNumber) 
				   (. file getParent)
                   (. file canWrite) 
				   (. file exists) 
				   (. file length))
    (.close instr)
    (.close stream)
    (. buf toString)))

(defn serialize-object-1 [obj path]
  (let [fos (new FileOutputStream #^String path)
        oos (new ObjectOutputStream fos)]
    (try (.writeObject oos obj)
         (finally (.close oos)))))

(defn deserialize-object-1 [path]
  (let [fis (new FileInputStream #^String path)
            ins (new ObjectInputStream fis)]
    (try (.readObject ins)
         (finally (.close ins)))))

(defn serialize-object [obj path]
  (try (serialize-object-1 obj path)
       (catch Exception e
         (println "WARN <serialize-object>: Could not serialize file: "  path " error=" e))))

(defn deserialize-object [path]
  (try (deserialize-object-1 path)
       (catch Exception e
         (println "WARN <deserialize-object>: Could not load serialized file: "  path " error=" e)))) 
      
(defn save-file-list []
  (let [#^java.util.Hashtable obj (get-recent-file-table)]
    (when obj
      (serialize-object obj *recent-file-list*))))

(defn load-file-list []
  (let [obj (deserialize-object *recent-file-list*)]    
    (if (nil? obj)
      (set-recent-file-table (Hashtable.))
      (set-recent-file-table obj)))
  (get-recent-file-table))

(defn add-recent-file [#^File file]
  (let [name (. file getName)
             path (. file getAbsolutePath)
             #^java.util.Hashtable rec-tabl (get-recent-file-table)]
    (when rec-tabl
      (. rec-tabl put name path)
      rec-tabl)))

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;; Open the directory dialog and utilities
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

(defn format-has-val [bval c] (if bval c "-"))

(defn format-isdir-name [name is-d] (if is-d (str "<" name ">") name))

(defn format-dir-file [#^File file]
  (let [is-d (format-has-val (. file isDirectory)   "d")
        can-r (format-has-val (. file canRead) "r")
        can-w (format-has-val (. file canRead) "w")
        len   (. file length)
        name  (format-isdir-name (. file getName) (. file isDirectory))
        lmod  (. file lastModified)]
    ;; Use the format syntax to get fixed width columns
    (str (apply format "%3s %15s  %25s  %s" (list (str is-d can-r can-w) len (get-dir-date lmod) name)) *newline*)))
             
(defn open-directory [path]
  ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
  ;; Open the directory and then store the contents
  ;; in the main buffer
  ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
  (async-status-history *display* (str "Opening directory => " path *newline*))
  (let [file-dir (File. #^File path)]
    (when (. file-dir exists)
      ;; Set the open directory global
      (set-directory-open path)      
      (location-set-text path)
      (clear-buffer buffer-1)      
      ;; ADD the file path as the fist line.
      (. buffer-1 #^StringBuffer append (str "<< Current Directory: " path *newline*))
      (doseq [fil (. file-dir listFiles)]
          (let [] (. buffer-1 append (format-dir-file fil))))
      (. *styled-text* setText (. buffer-1 toString)))))        

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;; Location Bar and Utilities
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

(defn open-file-or-dir 
  " Detect if the path is a file or a directory and open accordingly."
  [name]
  ;; Check if the file exists, open the file or directory
  (let [file (new File name)]
    (if (not (. file exists))
      (async-status-history *display* (str "File does not exist => " name))
      (if (. file isDirectory)
        (open-directory (. file getAbsolutePath))
        (open-file (. file getAbsolutePath) true)))))

(def location-text-listener
     ;; Use (. box addListener SWT/Traverse location-text-listener)
     ;; to add the action listener.
     (proxy [Listener] []
            (handleEvent [event]
                         (when (= (. event detail) SWT/TRAVERSE_RETURN)
                           (async-status-history *display* (str "Opening from location bar " 
                                                                (. location-bar getText)))
                           (open-file-or-dir (octane-trim (. location-bar getText)))))))

(defn dialog-open-dir []
  (. directory-dialog setText "Open Directory")
  (when-let [file (. directory-dialog open)]
            (open-directory file)))

(defn parse-system-args []
  (let [args *command-line-args*]
    (when (not (nil? args))
      (when (> (count args) 0)
        ;; Open the file if it exists
        (if (not (file-exists? (first args)))
          (println "WARN: Invalid file path argument =>" (first args))
          (let []
            (println "Opening file from command-line argument list")
            (open-file (first args) false)))))))

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;; Document and Search Properties
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

(defn win-prop-err-msg [msg filename]
  (let [fin-msg (str "Err opening file : " msg " => " filename \newline "At " (date-time))]
	(async-status-history *display* fin-msg)
	(display-error fin-msg)))

(defn win-prop-open-file
  "Open a text file and retur file information stats"
  [file path]
  ;;;;;;;;;;;;;
  (open-file-util file path)
  (let [fin-msg (get-simple-file-info simple-file-info-msg)]
	(add-secondary-text fin-msg)
	;; Open the info box
	(create-info-messagebox *shell* "File Properties" fin-msg)))

(defn win-file-prop-handler
  "Determine the file type and print the document properties.
 The current file is taken from the location bar."
  []
  ;;;;;;
  (let [filename (. location-bar getText)]
	(if (and filename (> (. filename length) 3))
	  (let [ext (file-extension filename)
				file    (new File #^String filename)
				is-dir? (.isDirectory file)]
		(if (not (. file exists))
		  (do (win-prop-err-msg "File does not exist" filename))
		  (cond (= "jar" ext) (println "jar")
				(= "Z"   ext) (println "Z")
				(= "zip" ext) (println "d")
				:default      (win-prop-open-file file filename))))
	  (do (win-prop-err-msg "Get Properties - Invalid filename from location bar (empty)" filename)))))

(def win-file-prop-listener
	 (proxy [SelectionAdapter] []
			(widgetSelected [e] (win-file-prop-handler))))

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;; Build Simple XY Plot of Search Term Data
;; No GUI libraries are required.  Open the file, and
;; only collect the lines with found search terms.
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

(defn on-sysout-line-func [list-ref]
  (fn [line]
	  (let [m (.matcher (octane-pattern_ *sysout-timestamp-regex*) line)]
		(when (.find m)
		  (let [ref-data {:timestamp (parse-sysout-date (.group m 2))
						  :line (.substring line (+ 1 (.end m))) } ]
			;; Update the state of 'list-ref' as
			;; as a new list with the new element.
			;; In common lisp, this would be 'push'
			(sync nil (ref-set list-ref
							   (conj @list-ref ref-data))))))))
							
(defn get-log-search-terms
  "Create a list of data structures with the found term and a timestamp
 on when that that search term was found"
  [filename term-regex]
  ;;;;;;;;;;; 
  (let [file (new File #^String filename)
			 file-data (open-file-util file filename)]
	(let [lines-found (doc-filter-regex file-data term-regex)
					  list-ref (ref [])]
	  (doc-loop-handler lines-found (on-sysout-line-func list-ref))
	  @list-ref)))

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;; Temporary File Creation
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
(defn create-temp-work-dir
  "Create the large file work directory."
  [filename]
  ;;;;
  (let [file (new File filename)]
	(if (.exists file)
      (async-status-history *display* (str "Working directory exists => " (.getAbsolutePath file)))
	  (do (async-status-history *display* (str "Creating working directory => " (.getAbsolutePath file)))
		  (.mkdirs file)))
	(if (.canWrite file)
	  true
	  (do (println "Cannot write tmp directory => " (.getAbsolutePath file))
		  false))))

(defn create-temp-work-dirp
  "Create the large file work directory from property."
  []
  ;;;;
  (create-temp-work-dir (prop-core-sys "Octane_Sys_largefile_work")))

(defn create-archive-work-dir
  "Create the archive working directory by date"
  [my-date-str]
  ;;;;;
  (when (create-temp-work-dirp)
	(let [tmp-work (prop-core-sys "Octane_Sys_largefile_work")
				   date-file *simple-date-format*
				   date-pack    *simple-date-format-pack*
				   tmp-date     (when-try (.parse date-file my-date-str))
				   tmp-date-str (when-try (.format date-pack tmp-date))]
	  ;; Create the date work directory
	  (let [ndir (str tmp-work *name-separator* "archives" *name-separator* tmp-date-str)]
		(create-temp-work-dir ndir)
        ;; Set the history message and location bar
        (async-status-history *display* (str "Created archive temp directory => " ndir))
        (async-set-location *display* (str ndir))
		ndir))))

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;; Jar Utilities
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
(defn open-jar-file-str
  "Open the jar files and read the contents, return a string"
  [file]
  ;;;;;;;;;;;
  (let [jar-file (JarFile. file)
        buf (StringBuffer. 1024)]
    (when-let [entries (. jar-file entries)]
        (loop [entry (. entries nextElement)]
          (when (and entry (. entries hasMoreElements))
            (let [name (. entry getName)]
              (. buf append (str name \newline)))
            (recur (. entries nextElement)))))
    (. buf toString)))

(defn open-jar-file
  "Open the jar files and read the contents, return a string"
  [file]
  ;;;;;;;;;;;
  (let [#^org.eclipse.swt.widgets.Display disp *display*]
    (when-let [listing (open-jar-file-str file)]
        (async-status-history
         disp (str "Opening jar file => " file *newline*))
      (clear-buffer buffer-1)
      (add-main-text-nc (str "<<< Opening jar file : " file " >>>"))
      (async-call disp (add-main-text-nc listing)))))
  
(def open-jar-file-handler
     ;; Where file-handler takes the following arguments : <DISPLAY> <FILE> <PATH>
     (fn [disp file path]
         (let [jar-thread (proxy [Runnable] [] 
                                 (run []
                                      (try (open-jar-file file)
                                           (catch Exception e
                                                  (. e printStackTrace)))))]
           (. (new Thread jar-thread) start))))

(defn jar-viewer-handler
  "Invoke the simple dialog handler and handle the jar file"
  []
  ;;;;;;;;;;;;
  (simple-dialog-open-file *display* open-jar-file-handler *jar-wildcard-seq*))

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