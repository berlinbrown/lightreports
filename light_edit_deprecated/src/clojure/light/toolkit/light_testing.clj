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


(ns light.toolkit.light_testing
	(:use    light.toolkit.light_utils
			 light.toolkit.light_main_constants
			 light.toolkit.light_config
			 light.toolkit.public_objects
			 light.toolkit.light_templates)
	(:import (java.io BufferedReader File FileInputStream FileOutputStream
					  FileNotFoundException IOException InputStreamReader Reader)
			 (java.util ResourceBundle Vector Hashtable)
			 (org.eclipse.swt.widgets Display Shell Text Widget TabFolder TabItem)
			 (java.text MessageFormat)
			 (java.util HashMap)))

(import '(java.io BufferedOutputStream PrintWriter))

(defn print-test-case [test-name]
  (. MessageFormat format *simple-testcase* (to-array [test-name])))

(defn main-generate-testgen []
  ;; Auto generate the test cases for JUnit
  (println "<INFO main-generate-test gen main> " (date-time))
  (try (let [filename (str "test" *name-separator* "_work" *name-separator* "LightTestGen.clj")
             fos      (new FileOutputStream filename)
             bos      (new BufferedOutputStream fos)
             pw       (new PrintWriter bos)]
         ;; First, write the header
         (. pw print (str *header-testgen* *newline*))         
         ;;(doseq [pub-func-arr (ns-publics 'light)] 
         ;;    (let [pub-func (first pub-func-arr)]
         ;;     (. pw print (print-test-case pub-func))))
         (. pw println *footer-testcase*)
         (. pw flush)
         (doto fos (. flush) (. close)))
       (catch Exception e
              (. e printStackTrace))
       (finally
        (println "<INFO main-generate-testgen> : completed. " (date-time))
        (exit))))

(defn main-generate-test-cases []
  ;; Auto generate the test cases for JUnit
  (println "<INFO main-generate-test-cases> : Code generating test cases. " (date-time))
  (try (let [filename (str "test" *name-separator* "_work" *name-separator* "LightFullTest.clj")
             fos      (new FileOutputStream filename)
             bos      (new BufferedOutputStream fos)
             pw       (new PrintWriter bos)]
         ;; First, write the header
         (. pw print (str *header-testcase* *newline*))
         (doseq [pub-func-arr (ns-publics 'light)] 
             (let [pub-func (first pub-func-arr)]
               (. pw print (print-test-case pub-func))))
         (. pw println *footer-testcase*)
         (. pw flush)
         (doto fos (. flush) (. close)))
       (catch Exception e
              (. e printStackTrace))
       (finally
        (println "<INFO main-generate-test-cases> : completed. " (date-time))
        (exit))))
          
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;;; End of Script
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;