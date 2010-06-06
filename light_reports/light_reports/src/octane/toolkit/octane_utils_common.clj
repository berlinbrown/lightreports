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

(ns octane.toolkit.octane_utils_common
	(:import (java.util Date)
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

(defn prev-exit [] (. System (exit 0)))

(defn exit [] (System/exit 1))

(defn clear-buffer [buf] (.setLength #^StringBuffer buf 0))


(defn length [s] (count s))

(defn date-time [] (str (new java.util.Date)))

(defn date-timel [l] (str (new java.util.Date (long l))))

(defn file-exists? [path] (let [file (File. #^String path)] (. file exists)))

(defn octane-pattern [s flags] (. Pattern compile s flags))

(defn octane-pattern_ [s] (. Pattern compile s))

(defn octane-trim [s] (when s (.trim #^String s)))

(defn octane-safe-pattern [s flags] 
  (when s    
    (try (. Pattern compile s flags)
         (catch Exception e nil))))         

(defn floor [d] (. Math floor d))

(defn println-obj [o] (println o) o)

(defn file-extension-
  "Get the file-extension from a filename"
  [filename n] 
  ;;;;;;;;;;;;;
  (when (> (. filename length) (+ n 1))
	(. filename substring (- (. filename length) n))))

(defn file-extension
  "Get the file-extension from a filename.
 Also synonymous with (re-find #'[^.]*$' 'foo.bar.baz.jar)"
  [filename]
  ;;;;;;;;;;;;
  (last (.split filename "\\.")))

(defn parse-long [str]
  (try (Long/parseLong str) 
       (catch NumberFormatException nfe 0)))

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

(def  *megabytes*      (* 1024.0 1024.0))
(def  *java-runtime*   (. Runtime getRuntime))

(defn *free-memory-b*  [] (. #^Runtime *java-runtime* freeMemory))
(defn *total-memory-b* [] (. #^Runtime *java-runtime* totalMemory))
(defn *max-memory-b*   [] (. #^Runtime *java-runtime* maxMemory))
(defn *used-memory-b*  [] (- (*total-memory-b*) (*free-memory-b*)))

;; Note: used memory == total memory - free memory
(defn *free-memory-m*  [] (int (floor (/ (*free-memory-b*)  *megabytes*))))
(defn *total-memory-m* [] (int (floor (/ (*total-memory-b*) *megabytes*))))
(defn *max-memory-m*   [] (int (floor (/ (*max-memory-b*)   *megabytes*))))
(defn *used-memory-m*  [] (int (floor (/ (*used-memory-b*)  *megabytes*))))

(defn *file-size-m*    [file-size] (/ file-size  *megabytes*))

(defn *memory-usage* []
  (str "(used:" (*used-memory-m*) "M/" (*free-memory-m*) "M [" (*total-memory-m*) "M," (*max-memory-m*) "M ])"))

;; Long timestamp for 2072, for min/max comparisons
(def *timestamp-long-max* 3239375951040)

(def  *dir-date-format*         (new SimpleDateFormat "MM-dd-yyyy hh:mm.ss a"))
(def  *simple-date-format*      (new SimpleDateFormat "MM/dd/yyyy"))
(def  *simple-date-format-t*    (new SimpleDateFormat "MM/dd/yyyy HH:mm:ss"))
(def  *simple-date-format-pack* (new SimpleDateFormat "MMddyyyy"))
(def  *current-date*            (.format *simple-date-format* (new Date)))

;; For Regex Patterns, Flags may include CASE_INSENSITIVE, MULTILINE, DOTALL, UNICODE_CASE, and CANON_EQ
;; Establish the charset and decoder, used with grep functionality.
(def *util-regex-line-pattern* (. Pattern compile ".*\\r?\\n"))
(def *util-iso-8859-charset*   (. Charset forName "ISO-8859-15"))
(def *util-charset-decoder*    (. *util-iso-8859-charset* newDecoder))
(defn util-get-char-buf-decoder
  "Get java nio character buffer from decoder"
  [doc]
  (let [dummy1 (. *util-charset-decoder* reset)
			   ;; BugFix/Hack, adding newline to end of document
			   char-buf (. *util-charset-decoder* decode 
						   (. ByteBuffer wrap (. (str doc \newline) getBytes)))]
    char-buf))

;; Example sysout date format = '2/12/09 13:11:12:784 EST'
(def  *sysout-date-format* (new SimpleDateFormat "MM/dd/yy HH:mm:ss:SSS z"))
(defn get-dir-date [l]  (.format #^SimpleDateFormat *dir-date-format* #^java.util.Date (java.util.Date. (long l))))

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

(defn pprint-list
  "Simple pretty print a list"
  [lst]
  ;;;;;;;;;;
  (doseq [x lst]
	  (let []
		(println "<Begin Element>")
		(println x)
		(println "<End Element>" \newline))))


(defn parse-timestamp-date
  "Return the long representation for the timestamp"
  [str-date]
  ;;;;;;;;;;;;;;;  
  (let [sdf (new SimpleDateFormat "MMM d HH:mm:ss yyyy")]
	(try (. (. sdf parse str-date) getTime)
		 (catch Exception e (println "Warn: invalid date format =>" str-date)
				str-date))))

(defn parse-basic-date
  "Return the long representation for the timestamp"
  [date-format-obj str-date]
  ;;;;;;;;;;;;;;;
  (try (. (. date-format-obj parse str-date) getTime)
       (catch Exception e (println "Warn: invalid date format => " str-date)
              str-date)))

(defn parse-sysout-date
  "Return the long representation for system out timestamp"
  [str-date]
  ;;;;;;;;;;;
  (when-try_  (.getTime (.parse *sysout-date-format* str-date))))

(defn flatten [x]
  (let [s? #(instance? clojure.lang.Sequential %)] 
	(filter (complement s?) (tree-seq s? seq x))))

(defn keyword-frequency [col]
  (reduce (fn [counts x] (merge-with + counts {x 1})) {} col))

(defn clean-split
 "Split based on 'string-split', then trim each element and remove empty
 members and return that sequence"
 [str str-split]
 ;;;;;;;;;;;;;;;;;
 (remove empty? (map #(.trim %) (.split str str-split))))

(defn octane-new-by-name
  "Constructs a Java object whose class is specified by a String."
  [class-name & args]
  (clojure.lang.Reflector/invokeConstructor
   (clojure.lang.RT/classForName class-name)
   (into-array Object args)))

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

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;;; End of Script
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;