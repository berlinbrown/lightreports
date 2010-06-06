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
;;; Light Log was developed with a combination of Clojure 1.0, Java and Scala with use of libs, 
;;; SWT 3.4, JFreeChart, iText. 
;;; 
;;; Quickstart : the best way to run the Light Log viewer is to click on the win32 batch script light_logs.bat
;;; (you may need to edit the Linux script for Unix/Linux environments).
;;; Edit the win32 script to add more heap memory or other parameters.

;;; The clojure source is contained in : HOME/src/octane
;;; The java source is contained in :  HOME/src/java/src

;;; To build the java source, see : HOME/src/java/build.xml and build_pdf_gui.xml

;;; Additional Development Notes: The SWT gui and other libraries are launched from a dynamic classloader.  Clojure is also
;;;  started from the same code, and reflection is used to dynamically initiate Clojure. See the 'start' package.  The binary
;;;  code is contained in the octane_start.jar library.

;;; Home Page: http://code.google.com/p/lighttexteditor/
;;;  
;;; Contact: Berlin Brown <berlin dot brown at gmail.com>
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

(ns octane.toolkit.octane_utils
	(:use octane.toolkit.public_objects
		  octane.toolkit.octane_main_constants
          octane.toolkit.octane_utils_common)
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
			 (java.nio ByteBuffer)
             (org.eclipse.swt.custom StyleRange)))

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

(def history-add-text)
(def history-add-textln)	

(defn add-select-style [#^java.util.Vector styles-vec cur-style] (.addElement styles-vec cur-style))

(defn add-new-style-bean 
  "Add a new style range based on the log utils bean.  Style code could be SWT.BOLD, NONE, etc."
  [#^java.util.Vector styles-vec #^com.octane.util.beans.LogUtilsMatcherBean bean col-fg col-bg offset style-code]
  ;;;;;;;;;;;
  (when bean
    (let [pt1 (+ offset (.getStart bean))
          pt2 (+ offset (.getEnd bean))
          len (- pt2 pt1)]
      (add-select-style styles-vec (StyleRange. pt1 len col-fg col-bg style-code)))))
     
(defn col-vec-bg   []  (.get #^java.util.Vector colors-vec 0))
(defn col-vec-grey []  (.get #^java.util.Vector colors-vec 1))
(defn col-vec-red  []  (.get #^java.util.Vector colors-vec 2))
(defn col-vec-cy   []  (.get #^java.util.Vector colors-vec 3))
(defn col-vec-drkb []  (.get #^java.util.Vector colors-vec 4))
(defn col-vec-wht  []  (.get #^java.util.Vector colors-vec 5))
(defn col-vec-yllw []  (.get #^java.util.Vector colors-vec 6))
(defn col-vec-blk  []  (.get #^java.util.Vector colors-vec 7))
;; Pastel colors for use with log highlights
(defn col-vec-pltgrn  [] (.get #^java.util.Vector colors-vec 8))
(defn col-vec-pdrkgrn [] (.get #^java.util.Vector colors-vec 9))
(defn col-vec-pltred  [] (.get #^java.util.Vector colors-vec 10))
(defn col-vec-pdrkred [] (.get #^java.util.Vector colors-vec 11))

(defn simple-term-searchrepl [cmd-line, str-data]
  ;; Search and replace in the following format - " abc , 123 ; uuu , ggg"
  ;; Note: fix hack against lisp, (string buffer)
  (if (not (and cmd-line (> (length cmd-line) 1)))
    nil
    (let [splt (.split cmd-line ";")
               buf (StringBuffer. 30)]
      (. buf append str-data)
      (doseq [i splt]
          (let [for-repl (. i split ",")
                         f (. (first for-repl) trim)
                         s (. (second for-repl) trim)
                         cur-data (. buf toString)]            
            (. buf setLength 0)
            (. buf append (. cur-data replaceAll f s))))
      (. buf toString))))

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

(defn history-add-text [text]
  (println "History : " text)
  (. #^StringBuffer buffer-3 append (str (date-time) " : " text))
  (let [disp (. #^org.eclipse.swt.widgets.Widget tab-text-3 getDisplay)]
    (. disp asyncExec
       (proxy [Runnable] []
              (run [] (. tab-text-3 setText (. #^StringBuffer buffer-3 toString)))))))

(defn history-add-textln [text] (history-add-text (str text \newline)))

(defn command-add-text [text]
  (println "Command : " text)
  (let [my-buffer *command-buffer*
        my-text   tab-text-4]
  (. #^StringBuffer my-buffer append (str (date-time) " : " text))
  (let [disp (. #^org.eclipse.swt.widgets.Widget my-text getDisplay)]
    (. disp asyncExec
       (proxy [Runnable] []
              (run [] (. my-text setText (. #^StringBuffer my-buffer toString))))))))

(defn command-add-textln [text] (command-add-text (str text \newline)))

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

(defn doc-filter-regex
  "Return a string document with only the lines of interest"
  [doc regex-filter]
  ;;;;;;;;;;;;;;;;;;;
  (let [res-buffer (new StringBuffer 1024)
				   bit-pattr (bit-or Pattern/MULTILINE (bit-or Pattern/CASE_INSENSITIVE 1))
				   p     (octane-pattern (str "^.*" regex-filter ".*$") bit-pattr)
				   cbuff (. CharBuffer wrap doc)
				   m     (. p matcher cbuff)]
	(loop [fnd? (. m find)]
	  (when fnd?
		(. res-buffer append (str (. m group) \newline))
		(recur (. m find))))
	;; Return the buffer string
	(. res-buffer toString)))

(defn simple-grep-regex?
  "Determine if the text is found"
  [doc regex-str]
  ;;;;;;;;;;;;
  (let [pattr (octane-safe-pattern regex-str
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
        pattr     (octane-safe-pattern regex-str-term flags)
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
  (let [fis (new FileInputStream (new File #^String filename))
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
  (let [buf (new StringBuffer 1024)]
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

(defn simple-mkdirs-handler
  "Simple utility to make directories.  Throws error on invalid directory."
  [dirname]
  ;;;;;;;;;;;;;;;;;
  (if (empty? dirname) (throw (RuntimeException. "ERR: <mkdirs> Invalid Directory"))
      (let [file (new File #^String dirname)]
        (try (.mkdirs file)
             (catch Exception e
                    (throw (RuntimeException. (str "ERR: <mkdirs> Invalid Directory. Cannot mkdir => " 
                                                   (.getMessage e)))))))))

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;; Archive, Search and Replace Utils

;; Also use ',(map #(.trim %) (.split "abc ; 1 2 3" ";"))
;; ,(macroexpand '#(+ %1 %2))
(def *archive-sr-multi-char*   ";")
(def *archive-sr-oneline-char* ",")
(def *archive-sr-fromto-char*  "->")

(defn archive-sr-multi-seq
  "Using the multi character, create a list 
 split by the multi char"
  [repl-format]
  ;;;;;;;;;;;;;;;;;;;;;;;;;
  (clean-split repl-format *archive-sr-multi-char*))

(defn archive-sr-oneline
  "Return a sequence separated by the one line char"
  [repl-format]
  ;;;;;;;;;;;;;;;;;;;;;;;;;
  (clean-split repl-format *archive-sr-oneline-char*))

(defn archive-sr-fromto
  "Return a sequence with two items, from -> to"
  [repl-format]
  ;;;;;;;;;;;;;;;;;;;;;;;;;
  (let [lst (clean-split repl-format *archive-sr-fromto-char*)]
    (when (not (empty? lst))
      ;; If the list is greater than equal to, get first and second vals
      (if (>= (count lst) 2)
        (take 2 lst)
        (list (first lst) "")))))

(defn archive-search-replace-seq
  "Replace a search term with the replace format"
  [repl-format]
  ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
  (for [multi (archive-sr-multi-seq repl-format)]
      (for [single (archive-sr-oneline multi)]
        (archive-sr-fromto single))))

(defn archive-search-replace
  "Replace a search term with the replace format"
  [orig-term-line repl-format]
  ;;;;;;;;;;;;;;;;;;;;;;;;;;;;
  (let [res-lst (for [multi (archive-search-replace-seq repl-format)]
                  (loop [lst   multi
                         line  orig-term-line]
                    (if-let [single (first lst)]
                        (recur (rest lst)
                               (.replaceAll line (first single) (second single)))
                      line)))]
    ;; if the list is available, also remove duplicates
    (when res-lst
      (distinct res-lst))))

(defmacro when-try [body]
  `(try ~body
        (catch Exception ~'e
               (println "ERR <when-try> " ~'e)
			   (try (history-add-textln (str "ERR <when-try> " ~'e \newline))
					(catch Exception ~'e2 (println "ERR2 <when-try>")))
               nil)))

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
;; -------------------------------------

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;;; End of Script
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;