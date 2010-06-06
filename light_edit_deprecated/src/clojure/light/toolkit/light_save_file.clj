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
;;; Date: 3/5/2009
;;; Main Description: Light is a simple text editor in clojure
;;;
;;; Description: Save file dialog box and functionality.

;;; Contact: Berlin Brown <berlin dot brown at gmail.com>
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

(ns light.toolkit.light_save_file
    (:use
	 light.toolkit.light_utils
	 light.toolkit.public_objects
	 light.toolkit.light_gui_utils
     light.toolkit.light_file_utils)
    (:import 
	 (java.io File BufferedWriter OutputStreamWriter FileOutputStream PrintWriter)
	 (org.eclipse.swt SWT)
     (org.eclipse.swt.events VerifyListener SelectionAdapter ModifyListener SelectionListener
                             SelectionEvent ShellAdapter ShellEvent)))

(defn write-string-file
  "Write the string content to file.
 @throws Error  -- Throws error on cannot write to file errors"
  [data out-filename]
  ;;;;;;;;;;;;;;;;;;;; 
  (try (let [file (File. out-filename)
             buf-out (BufferedWriter. (OutputStreamWriter. (FileOutputStream. file)))
             out (PrintWriter. buf-out)]
         (.print out data)
         (.flush buf-out) (.close buf-out))
       (catch Exception e
              (throw (RuntimeException. (str "Could not write to file => " 
                                             out-filename " error => " (.getMessage e)))))))

(defn win-save-file
  "Window wrapper for write data to file."
  [data out-filename]
  ;;;;;;;;;;;;;;;;;;;;
  (try (do (write-string-file data out-filename)
           (async-status-history *display* (str "File saved to => " out-filename " at " (date-time))))
       (catch Exception e
              (async-status-history *display* (.getMessage e)))))
  
(defn win-save-file-handler
  "Handler for saving a file to disk.  Check the curfile-open if it is 
 available.  If it is not available then open the 'save-as' dialog"
  []
  ;;;;;
  (if-let [cur-file (get-curfile-open)]
      (win-save-file (.getText *styled-text*) cur-file)
    (async-status-history *display* "Please open a file to save")))


(defn win-save-as-file-handler
  "Handler for saving a file to disk.  Check the curfile-open if it is 
 available.  If it is not available then open the 'save-as' dialog"
  []
  ;;;;;
  (if-let [cur-file  (dialog-save-as-file)]
      (win-save-file (.getText *styled-text*) cur-file)
    (async-status-history *display* "Please open a file to save")))

(def save-file-listener
     (proxy [SelectionAdapter][]
            (widgetSelected [event] (win-save-file-handler))))

(def save-file-as-listener
     (proxy [SelectionAdapter][]
            (widgetSelected [event] (win-save-as-file-handler))))

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;; REVISION HISTORY
;;
;; -------------------------------------
;; + 1/5/2009  Berlin Brown
;; Description: Project Create Date

;; + 1/5/2009  Berlin Brown
;; Description: Add new headers

;; + 3/5/2009  Berlin Brown
;; Description: Adding save file functionality, listeners
;; 
;; -------------------------------------

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;;; End of Script
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;      