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

(ns test.light_utils_test
    (:import (junit.framework Assert)
             (java.nio CharBuffer MappedByteBuffer)
             (java.nio.channels FileChannel)
             (java.nio.charset Charset)
             (java.nio.charset CharsetDecoder)
             (java.util.regex Matcher)
             (java.util.regex Pattern)
             (java.util.regex PatternSyntaxException)
             (java.nio ByteBuffer)
			 (java.io File))
     (:use
      light.toolkit.light_config
      light.toolkit.light_main_constants
      light.toolkit.light_utils
      light.toolkit.light_search_dialog)     
     (:gen-class
      :extends junit.framework.TestCase
      :methods
      [    
       [ test_replace_sys_prop [] void ]
       [ test_doc_filter_regex [] void ]
       [ test_regex_grep_prototype [] void ]
       [ test_regex_find_next [] void ]
	   [ test_traverse_dir [] void ]
       ]))

 (defn -init [_] ()) 

 (def example-doc "[[[start]] lkjdlfjslkdjflksjdfs
 lksjdklfjslkdjfklsjd 123 abc
 lksjdklfslkdjflks 123
 lkljdfljsdf
   filter
 kjsldjfs abc
 srevle
 servlet 
AFilter
 12312 90i121
 Dogs cats meat abc  123
 dog cat 123 baby
 chickens 123 [[[[end]]]")

 (defn -test_doc_filter_regex [_]
   (let [doc example-doc]
     (doc-filter-regex doc "(123|abc)")))

 (defn -test_replace_sys_prop [_]
   (Assert/assertNotNull *light-install-dir*)
   (Assert/assertEquals "/usr/local/projects/light/dogs/cats" 
                        (system-var-install-dir "_SYS_INSTALL_DIR_/dogs/cats"))
   (Assert/assertEquals "/usr/local/projects/light/dogs/cats" 
                        (system-variable "_SYS_INSTALL_DIR_/dogs/cats" *SYS_INSTALL_DIR* *light-install-dir*)))

 (defn -test_regex_grep_prototype [_]
   (let [l         *regex-line-pattern*
         test1       "filter"
         p         (light-safe-pattern (str "^" test1 "$")  (bit-or Pattern/CASE_INSENSITIVE (bit-or Pattern/DOTALL 1)))
         tmp1      (. *charset-decoder* reset)
         char-buf  (. *charset-decoder* decode (. ByteBuffer wrap (. (str example-doc "\n ") getBytes)))
         char-buf2 (get-char-buf-decoder example-doc)]    
     (Assert/assertNotNull l)
     (Assert/assertNotNull p)
     (Assert/assertNotNull char-buf)
     (Assert/assertNotNull char-buf2)
     ;; Match the lines
     (let [lm (. *regex-line-pattern* matcher char-buf2)
           buf (new StringBuffer)]
       ;; Loop till end of file detected
       (loop [srch-res (. lm find)]
         (when srch-res
           (. buf append (. lm group))
           (recur (. lm find)))))
     (let [buf2 (new StringBuffer)]
       (doc-loop-handler example-doc 
                         (fn [line]
                             (. buf2 append line))))
     ;; use same approach with file (the script that launched the tests)
     (let [buf2 (new StringBuffer)]
       (doc-file-loop-handler "./gentests.sh"
                         (fn [line x]
                             (. buf2 append line))))
     ;; File grep
     (Assert/assertNotNull (doc-file-grep "./gentests.sh" "End"))))

 (defn -test_regex_find_next [_]
   (let [term "filter"
         regex-str term
         flags     (bit-or Pattern/CASE_INSENSITIVE (bit-or Pattern/DOTALL Pattern/MULTILINE))
         pattr     (light-safe-pattern regex-str flags)
         char-buf  (get-char-buf-decoder example-doc)
         m-pattr   (. pattr matcher char-buf)
         m-pattr2  (new-find-next-matcher example-doc term)]
     (set-find-next-state m-pattr2)
     (Assert/assertNotNull (get-find-next-state))
     ;; Test the set-find
     (set-find-next-matcher example-doc term)
     (let [m (get-find-next-state)]
       (Assert/assertTrue (. m find)))))

(defn -test_traverse_dir [_]
  (let [a (fn [x] (println x))
		  b (fn [file]
				(println file)
				(println (doc-file-grep (. file getAbsolutePath) "test")))]
	  (traverse-directory (new File ".") a b)))
         
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;; End of Test Case
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

