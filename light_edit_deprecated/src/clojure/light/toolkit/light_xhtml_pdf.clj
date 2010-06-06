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

(ns light.toolkit.light_xhtml_pdf
    (:use
     light.toolkit.light_codegen_templates
	 light.toolkit.light_utils
	 light.toolkit.public_objects
	 light.toolkit.light_gui_utils)
    (:import
     (java.text MessageFormat)
	 (com.light.pdf.gui LightHtmlPDFCreateWin)
     (com.light.pdf XHTMLRendererBase XHTMLParserFactory)
     (org.eclipse.swt SWT)
     (org.eclipse.swt.events VerifyListener SelectionAdapter ModifyListener SelectionListener
							 SelectionEvent ShellAdapter ShellEvent)))

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;       

(def xhtml-to-pdf-listener
     (proxy [SelectionAdapter] []
	   (widgetSelected [event]
					   (. LightHtmlPDFCreateWin createPDFWindowShell *shell*))))

(defn buffer-to-pdf-handler
  "Convert the main buffer to a PDF document."
  []
  ;;;;;
  ;; Take the main buffer text and convert to PDF.
  (try 
   (let [doc (.getText *styled-text*)]
     (if (not (empty? doc))
       (let [pdf-base     (new XHTMLRendererBase)
             output-path  "/tmp/simple_output.pdf"]     
         (. XHTMLRendererBase setXhtmlProperties)
         (. pdf-base setKeyValue "SOME_KEY" "TestData")
         (. pdf-base setApplicationObject nil)
         (. pdf-base setHtmlContent doc)
         (. pdf-base parseDocumentFile output-path  (. XHTMLParserFactory create nil doc)))
       (println "Invalid Document")))
   (catch Exception e
          (.printStackTrace e))))
    
(defn buffer-to-xhtml-handler
  "Convert the main buffer to a XHTML document to be used for PDF output."
  []
  ;;;;;
  (try 
   (let [doc (.getText *styled-text*)]
     (if (not (empty? doc))
       ;; Piece together the HTML document.
       (let [sect1 (str (. MessageFormat format *codegen-templ-xhtml-tmpl1*
                           (to-array [*templ-current-datetime*]))
                        *codegen-templ-xhtml-tmp3b*)
             sect2 *codegen-templ-xhtml-footer*
             full-doc (str sect1 doc sect2)]
         (async-add-main-text full-doc))
       (println "Invalid Document")))
   (catch Exception e
          (.printStackTrace e)))) 

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

(def buffer-to-pdf-listener
     (proxy [SelectionAdapter] []
	   (widgetSelected [event] (buffer-to-pdf-handler))))

(def buffer-to-xhtml-listener
     (proxy [SelectionAdapter] []
	   (widgetSelected [event] (buffer-to-xhtml-handler))))

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
;;; End of Script
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;      