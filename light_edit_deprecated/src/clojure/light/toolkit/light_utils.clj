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

(ns light.toolkit.light_utils
	(:use    light.toolkit.public_objects
			 light.toolkit.light_main_constants)
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

(def history-add-text)
(def history-add-textln)	

(defn exit [] (. System (exit 0)))

(defn length [s] (count s))

;;;;;;;;;;;;;;;;;;;;
;;;; Patterns
;;;;;;;;;;;;;;;;;;;;
(defn light-pattern [s flags] (. Pattern compile s flags))

(defn light-pattern_ [s] (. Pattern compile s))

(defn clear-buffer [buf] (. buf setLength 0))

;;;;;;;;;;;;;;;;;;;;
;; Date Time
;;;;;;;;;;;;;;;;;;;;
(defn date-time [] (str (new java.util.Date)))

(defn date-timel [l] (str (new java.util.Date l))) 

(defn file-exists? [path] (let [file (new File path)] (. file exists)))

(defn light-trim [s] (when s (. s trim)))

(defn light-safe-pattern [s flags] 
  (when s    
    (try (. Pattern compile s flags)
         (catch Exception e nil))))         

(defn add-select-style [styles-vec cur-style] (. styles-vec addElement cur-style))

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

(defn col-vec-bg   []  (. colors-vec get 0))
(defn col-vec-grey []  (. colors-vec get 1))
(defn col-vec-red  []  (. colors-vec get 2))
(defn col-vec-cy   []  (. colors-vec get 3))
(defn col-vec-drkb []  (. colors-vec get 4))
(defn col-vec-wht  []  (. colors-vec get 5))
(defn col-vec-yllw []  (. colors-vec get 6))
(defn col-vec-blk  []  (. colors-vec get 7))

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
(defn get-dir-date [l]  (. *dir-date-format* format (new Date l)))

(defmacro when-try [body]
  `(try ~body
        (catch Exception ~'e
               (println "ERR <when-try> " ~'e)
			   (try (history-add-textln (str "ERR <when-try> " ~'e \newline))
					(catch Exception ~'e2 (println "ERR2 <when-try>")))
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


(defn simple-term-searchrepl [cmd-line, str-data]
  ;; Search and replace in the following format - " abc , 123 ; uuu , ggg"
  ;; Note: fix hack against lisp, (string buffer)
  (if (not (and cmd-line (> (length cmd-line) 1)))
    nil
    (let [splt (. cmd-line split ";")
               buf (new StringBuffer)]
      (. buf append str-data)
      (doseq [i splt]
          (let [for-repl (. i split ",")
                         f (. (first for-repl) trim)
                         s (. (second for-repl) trim)
                         cur-data (. buf toString)]            
            (. buf setLength 0)
            (. buf append (. cur-data replaceAll f s))))
      (. buf toString))))

(defn history-add-text [text]
  (println text)
  (. buffer-3 append (str (date-time) " : " text))
  (let [disp (. tab-text-3 getDisplay)]
    (. disp asyncExec
       (proxy [Runnable] []
              (run [] (. tab-text-3 setText (. buffer-3 toString)))))))

(defn history-add-textln [text] (history-add-text (str text \newline)))

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

(defn parse-sysout-date
  "Return the long representation for system out timestamp"
  [str-date]
  ;;;;;;;;;;;
  (when-try_  (.getTime (.parse *sysout-date-format* str-date))))

(defn doc-filter-regex
  "Return a string document with only the lines of interest"
  [doc regex-filter]
  ;;;;;;;;;;;;;;;;;;;
  (let [res-buffer (new StringBuffer 1024)
				   bit-pattr (bit-or Pattern/MULTILINE (bit-or Pattern/CASE_INSENSITIVE 1))
				   p     (light-pattern (str "^.*" regex-filter ".*$") bit-pattr)
				   cbuff (. CharBuffer wrap doc)
				   m     (. p matcher cbuff)]
	(loop [fnd? (. m find)]
	  (when fnd?
		(. res-buffer append (str (. m group) \newline))
		(recur (. m find))))
	;; Return the buffer string
	(. res-buffer toString)))

(defn flatten [x]
  (let [s? #(instance? clojure.lang.Sequential %)] 
	(filter (complement s?) (tree-seq s? seq x))))

(defn keyword-frequency [col]
  (reduce (fn [counts x] (merge-with + counts {x 1})) {} col))

(defn simple-grep-regex?
  "Determine if the text is found"
  [doc regex-str]
  ;;;;;;;;;;;;
  (let [pattr (light-safe-pattern regex-str
                                   (bit-or Pattern/CASE_INSENSITIVE (bit-or Pattern/DOTALL 1)))
        dummy1  (. *util-charset-decoder* reset)
        pattr-m (. pattr matcher doc)]
    (. pattr-m find)))

(defn simple-grep?
  "Determine if the text is found"
  [doc term]
  ;;;;;;;;;;;;;;;;;;;;
  (simple-grep-regex? doc (str term)))

(defn new-find-next-matcher
  "Create a new regex matcher for use with 'find-next'"
  [doc regex-str-term]
  ;;;;;;;;;;;;;;;
  (let [flags     (bit-or Pattern/CASE_INSENSITIVE (bit-or Pattern/DOTALL Pattern/MULTILINE))
        pattr     (light-safe-pattern regex-str-term flags)
        char-buf  (util-get-char-buf-decoder doc)
        m-pattr   (. pattr matcher char-buf)]
    m-pattr))

(defn doc-loop-handler
  "Loop through all the lines in a file and invoke the given handler
 function.   Where 'my-func' takes one parameter, the current line string."
  [doc my-func]
  ;;;;;;;;;;;;;;;
  (let [char-buf2 (util-get-char-buf-decoder doc)
        lm (. *util-regex-line-pattern* matcher char-buf2)]
    ;; Loop till end of file/document detected
    (loop [srch-res (. lm find)]
      (when srch-res
        (my-func (. lm group))
        (recur (. lm find))))))

(defn doc-file-loop-handler
  "Loop through all the lines in a file and invoke the given handler
 function.   Where 'my-func' takes two parameters, the current line string and line number."
  [filename my-func]
  ;;;;;;;;;;;;;;;;;;;;
  (let [fis (new FileInputStream (new File filename))
        fc  (. fis getChannel)
        sz  (. fc size)
        bb  (. fc map FileChannel$MapMode/READ_ONLY 0 sz)
        cb  (. *util-charset-decoder* decode bb)
        lm  (. *util-regex-line-pattern* matcher cb)]
    ;; Loop till end of file/document detected
    (loop [srch-res? (. lm find) line-no 0]
      (when srch-res?
        (my-func (. lm group) line-no)
        (recur (. lm find) (+ line-no 1))))))

(defn doc-file-grep
  "Loop through all the lines in a file and search for the term."
  [filename term]
  ;;;;;;;;;;;;;;;;;;;;
  (let [buf (new StringBuffer)]
    (doc-file-loop-handler filename 
                           (fn [line line-num]               
                               (when (simple-grep? line term)
                                 (. buf append (str filename ": line " line-num ": " line)))))
    (. buf toString)))

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;; Directory File Traversal
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
(defn create-filename-filter [cur-file-obj ext]
  (proxy [FilenameFilter] []
		 (accept [dir-file name]
				 (if (and name ext)	   
				   (. (str name) endsWith (str ext))
                   false))))

(defn traverse-accept-files [cur-file-obj full-ext]
  (if (and cur-file-obj (. cur-file-obj isFile))
    (let [name (. cur-file-obj getName)]
      (. name endsWith (str full-ext)))
    false))

(defn traverse-accept-files-regex [cur-file-obj regex-str]
  (if (and cur-file-obj (. cur-file-obj isFile))
    (let [name (. cur-file-obj getName)]
      (simple-grep-regex? name regex-str))
    false))

(defn traverse-directory 
  "Recursively search the directory"
  [f-cur-obj func-on-dir func-on-file regex-on-files]
  ;;;;;;;;;;;;;;
  (when f-cur-obj
	(if (. f-cur-obj isFile)
	  (when (and func-on-file (traverse-accept-files-regex f-cur-obj regex-on-files))
        (func-on-file f-cur-obj))
	  (let [files (. f-cur-obj listFiles)]
		;; Current file is a directory
		(when func-on-dir (func-on-dir f-cur-obj))
		(doseq [file files]
			(traverse-directory file func-on-dir func-on-file regex-on-files))))))
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;; End of Traverse Directory
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

(defn simple-mkdirs-handler
  "Simple utility to make directories.  Throws error on invalid directory."
  [dirname]
  ;;;;;;;;;;;;;;;;;
  (if (empty? dirname) (throw (RuntimeException. "ERR: <mkdirs> Invalid Directory"))
      (let [file (new File dirname)]
        (try (.mkdirs file)
             (catch Exception e
                    (throw (RuntimeException. (str "ERR: <mkdirs> Invalid Directory. Cannot mkdir => " 
                                                   (.getMessage e)))))))))

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

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;;; End of Script
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;