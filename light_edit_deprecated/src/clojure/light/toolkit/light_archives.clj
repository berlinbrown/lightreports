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
;;; Contact: Berlin Brown <berlin dot brown at gmail.com
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

(ns light.toolkit.light_archives
	(:use
     light.toolkit.light_main_constants
	 light.toolkit.light_utils
	 light.toolkit.public_objects
	 light.toolkit.light_gui_utils
	 light.toolkit.light_config
     light.toolkit.light_file_utils
	 light.toolkit.light_jar_viewer)
	(:import 
	 (java.io File)
	 (org.eclipse.swt SWT)
	 (org.eclipse.swt.widgets Display Shell Text Widget TabFolder TabItem)
	 (org.eclipse.swt.widgets FileDialog MessageBox TableItem Button
							  Composite Table TableColumn)
	 (org.eclipse.swt.layout GridData GridLayout RowLayout RowData)
	 (org.eclipse.swt.events VerifyListener SelectionAdapter ModifyListener SelectionListener
							 SelectionEvent ShellAdapter ShellEvent)
	 (org.eclipse.swt.widgets Label Menu MenuItem Control Listener)
     (com.light.util.zip UncompressInputStream)
     (java.io FileInputStream InputStream ByteArrayOutputStream FileOutputStream)     
     (java.util.zip ZipInputStream InflaterInputStream)
	 (java.io InputStreamReader 
			  FileInputStream BufferedReader File FilenameFilter)))

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

(defn open-archive-has-file?
  "Open an archive file and determine if there is only one entry"
  [infile]
  ;;;;;;;;;;;;
  (try (let [zin (new ZipInputStream (new FileInputStream infile))]
         (loop [entry (. zin getNextEntry)
                ctr   0]
           (when entry
             (recur (. zin getNextEntry) (+ ctr 1))))
         (. zin close))
       (catch Exception e (. e printStackTrace))))

(defn open-compressed-file
  "Open an archive file and LZC unix compressed .Z extension"
  [infile]
  ;;;;;;;;;;;;
  (try (let [zin (new UncompressInputStream (new FileInputStream infile))
				  bbuf #^"[B" (make-array (. Byte TYPE) 20480)
				  bout (new ByteArrayOutputStream 20480)]
		 (loop [got (. zin read bbuf)
					tot 0]		  
		   (when (> got 0)
			 (. bout write bbuf, 0 got)
			 (recur (. zin read bbuf) (+ tot 1))))
		 ;; With the byte array outputstream
		 ;; Convert the bytes to string
		 (. bout flush)
		 (. zin close)
		 (new String (. bout toByteArray)))
       (catch Exception e 
			  (. e printStackTrace)
			  nil)))

(defn stream-compressed-file
  "Open an archive file and LZC unix compressed .Z extension"
  [infile #^java.io.OutputStream out]
  ;;;;;;;;;;;;
  (try (let [zin (new UncompressInputStream (new FileInputStream infile))
				 bbuf #^"[B" (make-array (. Byte TYPE) 20480)]
		 (loop [got (. zin read bbuf)
					tot 0]
		   (when (> got 0)
			 (. out write bbuf, 0 got)
			 (recur (. zin read bbuf) (+ tot 1))))
		 ;; With the byte array outputstream
		 ;; Convert the bytes to string
		 (. out flush)
		 (. zin close)
		 (. out close))
       (catch Exception e
			  (. e printStackTrace)
			  nil)))

(defn win-open-compressed-file
  "Open an archive file and LZC unix compressed .Z extension"
  [infile]
  ;;;;;;;;;;;;
  (let [data (open-compressed-file infile)]
	(async-status-history *display* (str "Open LZC (unix compressed) file => " infile *newline*))
	(async-add-main-text data)))

(defn check-archive-handler
  "Determine the archive type based on the extension and handle accordingly"
  [disp file path]
  ;;;;;;;;;;;;;;;;;
  (cond (. path endsWith ".Z")   (win-open-compressed-file file)
		(. path endsWith ".jar") (open-jar-file file)
		(. path endsWith ".zip") (println "Not implemented")
		:default                 (println "Not implemented")))
		                  
(defn open-archive-file-handler
  "Open the archive file.   Open the file  in the main buffer if only ONE text file exists.
 If more than one exists than just list the entries."
  [disp file path]
  ;;;;;;;;;;;;;;;;;
  (check-archive-handler disp file path))
  
(def open-archive-file-listener
     ;; Open the archive file.   Open the file  in the main buffer if only ONE text file exists.
     ;; If more than one exists than just list the entries."  
     ;;;;;;;
     (proxy [SelectionAdapter] []
            (widgetSelected [e] (simple-dialog-open-file
                                 *display* open-archive-file-handler  *zip-wildcard-seq* ))))


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

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;; End of Script
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;