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

(ns light.toolkit.light_main_window
    (:gen-class)
	(:use light.toolkit.light_utils
          light.toolkit.light_analytics
          light.toolkit.light_codegen_templates
          light.toolkit.light_config
          light.toolkit.light_core_widgets
          light.toolkit.light_file_database
          light.toolkit.light_file_utils
          light.toolkit.light_gui_utils
          light.toolkit.light_main_constants
          light.toolkit.light_regex_search
          light.toolkit.light_search_dialog
          light.toolkit.light_templates
          light.toolkit.light_testing
          light.toolkit.light_tools
          light.toolkit.light_utils
          light.toolkit.public_objects)
	(:import (org.eclipse.swt SWT)
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
			 (java.util.regex Pattern)))

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;; Begin Routines
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
(def search-keyword)

(defn search-term? [] (if (> (length (. search-box getText)) 2) true false))

(defn srchbox-get-text [] (str (. search-box getText)))

(defn main-match-style 
  "A match has been found on the particular keyword.  Add the style with light foreground, 
 dark (dark blue) background."
  [keyword line lo]
  ;;;;;;;;;;;;;;;;;;
  (let [m (regex-match-group keyword line)]
    (when m
      (let [pt1 (+ lo (. m start))
            pt2 (+ lo (. m end))
            len (- pt2 pt1)
            styl-tok (new StyleRange pt1 len (col-vec-wht) (col-vec-drkb) SWT/BOLD)]
        styl-tok))))

(defn findnext-match-style 
  "A match has been found on the particular keyword.  Add the style with light foreground, 
 dark (dark blue) background."
  []
  ;;;;;;;;;;;;;;;;;;
  (let [m (get-find-next-state)]
    (when m
      (let [pt1 (. m start)
            pt2 (. m end)
            len (- pt2 pt1)
            styl-tok (new StyleRange pt1 len (col-vec-blk) (col-vec-yllw) SWT/BOLD)]
        styl-tok))))

(defn style-keyword-match 
  "When a search term is available and the keyword matches, add the selection style on the
 main buffer text area.  Otherwise, add the fail style"
  [styles-vec line l-offset sty-on-sel sty-fail]
  ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
  (when (search-term?)
    (if (search-keyword (srchbox-get-text) line)
	  ;; Add the select style and attempt to do keyword style
      (let [dummy1 (add-select-style styles-vec sty-on-sel)]
        (when-let [fnd-style (main-match-style (srchbox-get-text) line l-offset)]
            ;; Check if match found, so add the style range for the keyword
            ;; selection
            (add-select-style styles-vec fnd-style))
		;; Add style if 'find-next' term is available
		(when-let [fnd-style (findnext-match-style)]
            (add-select-style styles-vec fnd-style)))
	  (add-select-style styles-vec sty-fail))))
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;; End of style keyword match
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

(defn style-handler [event]
  (let [styles-vec (new Vector)
                   line (. event lineText)
                   lo   (. event lineOffset)
                   len  (. line length)
                   bg   (. colors-vec get 0)
                   fgl  (. colors-vec get 1)
                   all-bold (new StyleRange lo len nil bg   SWT/BOLD)
                   light    (new StyleRange lo len fgl nil  SWT/NORMAL)]
    ;; Add the event styles if needed   
    (style-keyword-match styles-vec line lo all-bold light)
    ;; Associate the even style with the display
    (let [arr (make-array StyleRange (. styles-vec size))]
      (set! (. event styles) arr)
      (. styles-vec copyInto (. event styles)))))

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;; End of Style Keyword Match
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

(defn search-keyword [keyword line]
  (re-seq (light-pattern keyword Pattern/CASE_INSENSITIVE) line))
      
;; Event.detail line start offset (input) Event.text line text (input)
;; LineStyleEvent.styles Enumeration of StyleRanges, need to be in order.
;; (output) LineStyleEvent.background line background color (output)
(def text-style-listener
     (proxy [LineStyleListener] []
            (lineGetStyle [event] (style-handler event))))

(def shell-close-listener
     (proxy [ShellAdapter] [] 
            (shellClosed [evt] (exit))))

(defn refresh-textarea-deprecated []
  (let [disp (. *styled-text* getDisplay)]             
    (. disp asyncExec
       (proxy [Runnable] []
              (run [] (. *styled-text* setText (. buffer-1 toString)))))))

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;; Continue
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

(def find-text-listener
     (proxy [Listener] []
            (handleEvent [event]
                         (when (= (. event detail) SWT/TRAVERSE_RETURN)
                           (history-add-text (str "Searching for " (. search-box getText) "\n"))
                           (refresh-textarea)))))
                           
(defn create-grid-layout []
  (let [gridLayout (new GridLayout)]
    (set! (. gridLayout numColumns) 1)
    gridLayout))
        
(defn create-shell [disp sh]
  ;; Create shell and continue with other inits
  (. *styled-text* addLineStyleListener text-style-listener)
  ;; Note change in 'doto' call, dot needed.
  (let [layout (create-grid-layout)]
    (doto sh
      (. setText (. resources-win getString "Window_title"))
      (. setLayout layout)
      (. addShellListener (proxy [ShellAdapter] []
                                 (shellClosed [evt] (exit)))))))

(defn init-gui-helper [disp sh]
  (create-all-tabs)
  (create-menu-bar disp sh)
  (create-shell    disp sh)
  (init-colors)
  (history-add-text (get-hist-header))
  (status-set-text  (str "Light GUI loaded " (date-time) " " (*memory-usage*))))

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

(defn 
  #^{:doc "Initialize the SWT window, set the size add all components"}
  create-gui-window [disp sh]
  ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
  
  ;; Set the tab folder and items with the main text area
  ;; and other SWT oriented inits.
  (init-gui-helper disp sh)
  ;; Modify already created objects
  (let [gd (new GridData SWT/FILL SWT/FILL true false)]
    (. search-box addListener SWT/Traverse find-text-listener)
    (. search-box setLayoutData gd)
    (. location-bar setLayoutData gd)
    (. location-bar addListener SWT/Traverse location-text-listener))
  (create-status-bar)
  ;; Final init, set the window size and then open
  (doto sh
    (. setSize win-size-width win-size-height)
    (. open))
  (parse-system-args)
  ;; Debug
  (loop [] (if (. sh (isDisposed))
             (. disp (dispose))
             (let [] (when (not (. disp (readAndDispatch)))
                       (. disp (sleep)))
                  (recur)))))

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;; Application Main Entry Point
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
(defn main-1 
  " Application Entry Point, launch the main window and wait for events"
  []
  ;;;;
  (println "Launching Light Text Viewer...")
  (create-gui-window *display* *shell*)
  (let [o (new Object)] (locking o (. o (wait)))))

(defn -main [& args]
  (try (main-1)
	   (catch Exception e
			  (println "ERR at <Main>: " e)
			  (exit))))

;; Remove -main when running gen-class
(-main)

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
