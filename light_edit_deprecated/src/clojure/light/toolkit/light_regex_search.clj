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

(ns light.toolkit.light_regex_search
	(:use
	 light.toolkit.light_utils
	 light.toolkit.public_objects
	 light.toolkit.light_gui_utils
	 light.toolkit.light_config
	 light.toolkit.light_tools
	 light.toolkit.light_main_constants
	 light.toolkit.light_templates)
	(:import 
	 (java.util Vector)
	 (org.eclipse.swt SWT)
	 (org.eclipse.swt.graphics Color RGB FontData Font)
	 (org.eclipse.swt.widgets Display Shell Text Widget TabFolder TabItem)
	 (org.eclipse.swt.widgets FileDialog MessageBox TableItem Button
							  Composite Table TableColumn)
	 (org.eclipse.swt.custom LineStyleEvent StyledText
							 LineStyleListener StyleRange)
	 (org.eclipse.swt.layout GridData GridLayout RowLayout RowData)
	 (org.eclipse.swt.events VerifyListener SelectionAdapter ModifyListener SelectionListener
							 SelectionEvent ShellAdapter ShellEvent)
	 (org.eclipse.swt.widgets Label Menu MenuItem Control Listener)))

(def refresh-regex-example)
(def *regex-style* (bit-or SWT/CLOSE (bit-or SWT/BORDER (bit-or SWT/TITLE 1))))

(def regex-shell        (new Shell *shell* *regex-style*))
(def regex-label        (new Label regex-shell SWT/LEFT))
(def regex-edit-box     (new Text  regex-shell swt-text-style))
(def regex-colors-vec   (new Vector))

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;; Regex Dialog Window Functions
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

(defn rcol-vec-bg   [] (. regex-colors-vec get 0))
(defn rcol-vec-grey [] (. regex-colors-vec get 1))
(defn rcol-vec-red  [] (. regex-colors-vec get 2))
(defn rcol-vec-cy   [] (. regex-colors-vec get 3))
(defn rcol-vec-drkb [] (. regex-colors-vec get 4))
(defn rcol-vec-wht  [] (. regex-colors-vec get 5))

(defn regex-get-text [] (str (. regex-edit-box getText)))

(defn create-regex-grid-layout []
  (let [gridLayout (new GridLayout)]
    (set! (. gridLayout numColumns) 1) gridLayout))

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;; Styled Text Creation for the Regex Window
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

(defn init-regex-colors []  
  ;; Orange highlight color = 250, 209, 132
  ;; Light grey for default text.
  (let [disp (.  regex-shell getDisplay)]
    (doto regex-colors-vec
      (. addElement (new Color disp orange-sel-color))
      (. addElement (new Color disp lightgrey-color))
      (. addElement (new Color disp red-color))
      (. addElement (new Color disp cyan-sel-color))
      (. addElement (new Color disp dark-blue-color))
      (. addElement (new Color disp white-color)))))

(defn regex-search-term? []
  (if  (> (count (. regex-edit-box getText)) 2) true false))

(defn regex-search-keyword [keyword line]
  (try (re-seq (light-pattern_ keyword) line)
       (catch Exception e 
              (println "WARN: invalid regex expression => " keyword)
              false)))

(defn regex-match-group [keyword line]
  ;; Another call (redo) to regex match, find the main group, start and end.
  (let [p (light-pattern_ keyword)
        m (re-matcher p line)]
    (when (. m find) m)))

(defn regex-match-style [keyword line lo]
  (let [m (regex-match-group keyword line)]
    (when m
      (let [pt1 (+ lo (. m start))
            pt2 (+ lo (. m end))
            len (- pt2 pt1)
            styl-tok (new StyleRange pt1 len (rcol-vec-wht) (rcol-vec-drkb) SWT/BOLD)]
        styl-tok))))
       
(defn regex-style-handler 
  " Regex handler for the styled text window."
  [event]
  (let [styles-vec (new Vector)
        line (. event lineText)
        lo   (. event lineOffset)
        len  (. line length)
        all-bold (new StyleRange lo len nil (rcol-vec-bg) SWT/BOLD)
        light    (new StyleRange lo len (rcol-vec-grey) nil SWT/NORMAL)]
    (when (regex-search-term?)
      (if (regex-search-keyword (regex-get-text) line)
        (let [dummy1 (add-select-style styles-vec all-bold)]
          (when-let [reg-fnd-style (regex-match-style (regex-get-text) line lo)]
                    ;; Check if Match found, so add the style range
                    (add-select-style styles-vec reg-fnd-style)))
        (add-select-style styles-vec light)))
    ;; Associate the even style with the display
    (let [arr (make-array StyleRange (. styles-vec size))]
      (set! (. event styles) arr)
      (. styles-vec copyInto (. event styles)))))

(def regex-style-listener
     (proxy [LineStyleListener] []
            (lineGetStyle [event] (regex-style-handler event))))
  
(defn create-regex-styled-text [sh]
  (let [text (new StyledText sh swt-text-style)
        gd   (new GridData GridData/FILL GridData/FILL true true)
        disp (Display/getDefault)
        bg   (. disp (getSystemColor SWT/COLOR_WHITE))]
    (doto text
      (. setLayoutData gd)
      (. setFont (styled-text-font))
      (. addLineStyleListener regex-style-listener)
      (. setEditable false)
      (. setBackground bg))
    text))

(def regex-example-text (create-regex-styled-text regex-shell))

(defn refresh-regex-example []
  (. regex-example-text redraw)
  (. regex-example-text update))

(defn refresh-regex-example-deprecated []
  (let [disp (. regex-example-text getDisplay)]             
    (. disp asyncExec
       (proxy [Runnable] [] (run [] (. regex-example-text setText example-regex-document))))))

(defn find-regex-listener []
  (proxy [ModifyListener] []
         (modifyText [event] (refresh-regex-example))))

(defn init-regex-helper [sh]
  (let [gd-textarea (new GridData GridData/FILL GridData/FILL true true)]
    ;; Set both text area with expanding grid data layout.
    (. regex-edit-box setLayoutData gd-textarea)
    (. regex-example-text setLayoutData gd-textarea)
    (. regex-example-text setText example-regex-document)
    (. regex-edit-box     setFont (styled-text-font))
    (. regex-example-text setFont (styled-text-font))
    (. regex-edit-box addModifyListener (find-regex-listener))))

(defn create-regex-window
    "Initialize the file database SWT window, set the size add all components"
	[]
    ;;;;;;;;;;;;;;;;;;;;;;

    (history-add-textln "Opening regex tool (Search -> Regex Search Tool)")
    (let [layout (create-regex-grid-layout)]
      (. regex-shell setText (. resources-win getString "Regex_win_title"))
      (init-regex-colors)
      (init-regex-helper regex-shell)
      (. regex-shell setLayout layout)
      (. regex-shell addShellListener
         (proxy [ShellAdapter][]
                (shellClosed [event]
                             (set! (. event doit) false)
                             (. regex-shell setVisible false))))
      (. regex-shell setSize *regex-size-width* *regex-size-height*)
      (. regex-shell open)
      (. regex-shell setVisible true)
      ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
      ;; Enter display/shell loop for this window
      ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
      (let [disp (. regex-shell getDisplay)]
        (shell-display-loop disp regex-shell false "Regex tool shell disposed"))))

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