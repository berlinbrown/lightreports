;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;;;
;;; Copyright (c) 2006-2007, 

;;; All rights reserved.
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

(ns light.test.win.basic_test_utils
	(:import (java.util Date)
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

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

;; Spring Utils



;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

(defn exit [] (. System (exit 0)))

(defn length [s] (count s))

(defn date-time [] (str (new java.util.Date)))

(defn date-timel [l] (str (new java.util.Date l))) 

(defn floor [d] (. Math floor d))

;;;;;;;;;;;;;;;;;;;;
;;;; Patterns
;;;;;;;;;;;;;;;;;;;;
(defn test-pattern [s flags] (. Pattern compile s flags))

(defn test-pattern_ [s] (. Pattern compile s))

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

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;      
;;; End of Script
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;      
