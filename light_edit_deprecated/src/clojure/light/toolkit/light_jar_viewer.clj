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

(ns light.toolkit.light_jar_viewer
	(:use light.toolkit.light_main_constants
			 light.toolkit.light_config
			 light.toolkit.public_objects light.toolkit.light_utils
			 light.toolkit.light_gui_utils
			 light.toolkit.light_file_utils)
	(:import
     (java.util.jar JarFile)
     (java.util.jar JarInputStream)
     (java.util.zip ZipEntry)
	 (java.util Date)
	 (org.eclipse.swt.graphics Color RGB)
	 (org.eclipse.swt SWT)
	 (java.text MessageFormat)
	 (java.util HashMap)
	 (org.eclipse.swt.widgets Display Shell Text Widget TabFolder TabItem)
	 (org.eclipse.swt.events VerifyListener SelectionAdapter ModifyListener SelectionListener
							 SelectionEvent ShellAdapter ShellEvent)
	 (org.eclipse.swt.widgets Label Menu MenuItem Control Listener)	 
	 (org.eclipse.swt.layout FillLayout)
	 (org.jfree.chart ChartFactory JFreeChart)
	 (org.jfree.chart.axis DateAxis)
	 (org.jfree.chart.plot XYPlot)
	 (org.jfree.chart.renderer.xy XYItemRenderer)
	 (org.jfree.chart.renderer.xy XYLineAndShapeRenderer)
	 (org.jfree.data.time Month Second TimeSeries TimeSeriesCollection)
	 (org.jfree.data.xy XYDataset)
	 (org.jfree.experimental.chart.swt ChartComposite)
	 (org.jfree.ui RectangleInsets)))

(defn open-jar-file-str
  "Open the jar files and read the contents, return a string"
  [file]
  ;;;;;;;;;;;
  (let [jar-file (new JarFile file)
        buf (new StringBuffer)]
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
  (when-let [listing (open-jar-file-str file)]
      (async-status-history
       *display* (str "Opening jar file => " file *newline*))
    (clear-buffer buffer-1)
    (add-main-text-nc (str "<<< Opening jar file : " file " >>>"))
    (async-call *display* (add-main-text-nc listing))))

(def open-jar-file-handler
     ;; Where file-handler takes the following arguments : <DISPLAY> <FILE> <PATH>
     (fn [disp file path]
         (let [jar-thread (proxy [Runnable] [] 
                                 (run [] (try (open-jar-file file)
                                              (catch Exception e
                                                     (. e printStackTrace)))))]
           (. (new Thread jar-thread) start))))

(defn jar-viewer-handler
  "Invoke the simple dialog handler and handle the jar file"
  []
  ;;;;;;;;;;;;
  (simple-dialog-open-file *display* open-jar-file-handler *jar-wildcard-seq*))

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