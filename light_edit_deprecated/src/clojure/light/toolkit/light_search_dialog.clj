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

(ns light.toolkit.light_search_dialog
	(:use
	 light.toolkit.light_utils
	 light.toolkit.public_objects
	 light.toolkit.light_gui_utils
	 light.toolkit.light_config
	 light.toolkit.light_tools
	 light.toolkit.light_findfiles_dialog)
	(:import 
	 (org.eclipse.swt SWT)
	 (org.eclipse.swt.widgets Display Shell Text Widget TabFolder TabItem)
	 (org.eclipse.swt.widgets FileDialog MessageBox TableItem Button
							  Composite Table TableColumn)
	 (org.eclipse.swt.layout GridData GridLayout RowLayout RowData)
	 (org.eclipse.swt.events VerifyListener SelectionAdapter ModifyListener SelectionListener
							 SelectionEvent ShellAdapter ShellEvent)
	 (org.eclipse.swt.widgets Label Menu MenuItem Control Listener)))

(def *search-style* (bit-or SWT/CLOSE (bit-or SWT/BORDER (bit-or SWT/TITLE 1))))

;; Find next 'matcher' state
(def  *find-next-state*           (ref nil))
(defn get-find-next-state []      (deref *find-next-state*))
(defn set-find-next-state [match] (dosync (ref-set *find-next-state* match)))

(defn set-find-next-matcher
  "Create a new regex matcher for use with 'find-next'"
  [doc regex-str-term]
  ;;;;;;;;;;;;;;;;;;;;;
  (set-find-next-state (new-find-next-matcher doc regex-str-term)))

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;; Create the Widgets
;; Will be positioned on order of creation
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
(def search-shell          (new Shell *shell* *search-style*))
(def search-label          (new Label search-shell SWT/LEFT))
(def search-filter-box     (new Text search-shell SWT/BORDER))

(def regex-find-label      (new Label search-shell SWT/LEFT))
(def regex-check-box       (new Button search-shell SWT/CHECK))
(def casesens-find-label   (new Label search-shell SWT/LEFT))
(def casesens-check-box    (new Button search-shell SWT/CHECK))

(def search-composite      (new Composite search-shell SWT/NONE))
(def search-find-button    (new Button search-composite SWT/PUSH))
(def search-close-button   (new Button search-composite SWT/PUSH))
(def search-status-label   (new Label search-shell (bit-or SWT/LEFT (bit-or SWT/BORDER 1))))

(defn create-search-grid-layout []
  (let [gridLayout (new GridLayout)]
    (set! (. gridLayout marginHeight)  8)
    (set! (. gridLayout marginWidth)  10)
    (set! (. gridLayout numColumns) 2) gridLayout))

(defn search-label-set-text
  "Helper function to set the search label text"
  []
  ;;;
  (. search-label setText (prop-str resources-win "Search_for_label"))
  (. regex-find-label setText "Regular Expressions:")
  (. casesens-find-label setText "Case Sensitive:")
  (. search-status-label setText (str "Find Keyword Dialog Opened - " (date-time))))

(def search-on-close-listener
     (proxy [SelectionListener][]
            (widgetSelected [event]
                            (set! (. event doit) false)
                            (. search-shell setVisible false))
            (widgetDefaultSelected [event]
                                   (set! (. event doit) false)
                                   (. search-shell setVisible false))))

(defn on-found-term-handler
  "Handler for when a term is found"
  [sdisp m term text]
  ;;;;;;;;;;;;;;;;;
  (. search-status-label setText (str "Found term => " term " at " (. m start)))
  (. *styled-text* setSelection (. m start))
  (refresh-textarea))

(defn search-find-next-handler
  "When the user selects the find next button, invoke this find next handler.
 Search the main buffer for the term in the 'find' box."
  [event]
  ;;;;;;;;;;;;
  (let [disp (. search-shell getDisplay)
        term (. search-filter-box getText)
        text (. *styled-text* getText)]
    (if (and disp term text (> (length text) 0) (> (length term) 0))
      ;; Create the find next matcher from the document and term
      (let [fns (get-find-next-state)
            m (if fns fns (new-find-next-matcher text term))]
        ;; Also set the 'quick' search text bar at the bottom of the main window
        (async-call *display* (. search-box setText term))
        (async-call *display* (update-textarea))
        ;; Set the public matcher object, if it doesnt exist
        (when (not fns) (set-find-next-state m))
        (if (. m find)
		  (do	   
			;; On the valid case, has a match
			(on-found-term-handler disp m term text))
		  ;; Otherwise case, not one term found
		  (do (. search-status-label setText
				 (str "Could not find term => " term))
			  (. m reset))))
      (let []
        ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
        ;; On ERR:
        ;; Send status error message, could not find
        ;; (Bug with branching logic)
        ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
        (if (not term) 
          (. search-status-label setText "Invalid Search Term (empty)")
          (. search-status-label setText "Invalid Search Term (empty)"))
        (if (not text) 
          (. search-status-label setText "Invalid Buffer Document (empty)")
          (. search-status-label setText "Invalid Buffer Document (empty)"))))))

(def search-find-next-listener
     (proxy [SelectionListener][]
            (widgetSelected [event] (search-find-next-handler event))
            (widgetDefaultSelected [event] (search-find-next-handler event))))

(def search-find-next-traverse
     (proxy [Listener][]
            (handleEvent [event] (search-find-next-handler event))))

(defn init-search-buttons
  "Set the default properties for the search buttons"
  []
  ;; Set the composite buttons
  (let [rowd-find (new RowData 98 24)]
    (. search-find-button  setText "Find Next")
    (. search-find-button  setLayoutData rowd-find)
    (. search-find-button  setEnabled true)
    (. search-find-button  addSelectionListener search-find-next-listener)
    ;; Also add the on return listener to invoke 'find next'
    (. search-filter-box   addListener SWT/Traverse search-find-next-traverse)
    (. search-close-button setText "Close")
    (. search-close-button setLayoutData rowd-find)
    (. search-close-button setEnabled true)
    (. search-close-button addSelectionListener search-on-close-listener)))

(defn init-search-helper
  "Create the layout and place with the widgets for the search box
 Notes On Grid Data (useful for search widgets):
 GridData(int horizontalAlignment, int verticalAlignment, boolean grabExcessHorizontalSpace, 
               boolean grabExcessVerticalSpace, int horizontalSpan, int verticalSpan)"
  [sh]
  ;;;;;
  (let [gd-textbox (new GridData GridData/FILL_HORIZONTAL)
        gd-composite   (new GridData SWT/NONE)
        gd-status-bar  (new GridData SWT/FILL SWT/FILL true false 2 1)
        comp-layout    (new RowLayout)]
    (search-label-set-text)
    (set! (. gd-textbox widthHint) 200)
    (. search-filter-box setLayoutData gd-textbox)
    (. search-status-label setLayoutData gd-status-bar)
    ;; Set the button composite widget
    (set! (. gd-composite horizontalSpan) 2)
    ;; verticalAlignment specifies how controls will be positioned vertically within a cell
    (set! (. gd-composite horizontalAlignment) SWT/LEFT)
    (. search-composite setLayoutData gd-composite)
    ;; Position the buttons a couple of pixels away.
    (set! (. comp-layout marginTop)  4)
    (set! (. comp-layout marginLeft) 2)
    (. search-composite setLayout comp-layout)    
    (init-search-buttons)
	;; Check the state of the matcher, if available, reset.
	(let [fns (get-find-next-state)]  (when fns (. fns reset)))))
			  
;;;;;;;;;;;;;;;;;;;;
;; End of Function
;;;;;;;;;;;;;;;;;;;;

(defn
    #^{:doc "Initialize the file database SWT window, set the size add all components"}
    create-search-dialog [parent-sh]
    ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
    (history-add-textln "Opening search screen (Search -> Find)")
    (let [layout (create-search-grid-layout)]
      (. search-shell setText (. resources-win getString "Find_dialog_title"))
      (init-search-helper search-shell)
      (. search-shell setLayout layout)
      (. search-shell addShellListener
         (proxy [ShellAdapter][]
                (shellClosed [event]
                             (set! (. event doit) false)
                             (. search-shell setVisible false))))
      (. search-shell pack)
      (. search-shell open)
      (. search-shell setVisible true)
      ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
      ;; Enter display/shell loop for this window
      ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
      (let [disp (. search-shell getDisplay)]
        (shell-display-loop disp search-shell false "Search shell disposed"))))

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;; Additional Search Utilities
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

(def findgrep-listener
     (proxy [SelectionAdapter] []
            (widgetSelected [event]
                            ;; With the async call, set the agent value
                            ;; based on the search box value                            
                            (. *display* syncExec 
                               (proxy [Runnable] []
                                      (run [] 
                                           (let [val (. search-box getText)]
                                             (send *search-text-state* (fn [_] val))))))
                            (let [widg-str (str (. event widget))]
                              (. (new Thread
                                      (start-findgrep-thread widg-str @*search-text-state* true)) start)))))

(def new-findfiles-listener
     (proxy [SelectionAdapter] []
			;; Open the find files dialog
			(widgetSelected [event]
							(create-findfiles-dialog *shell*))))
                                                   
(defn add-findgrep-options [menu]
  ;; Load the normal find/grep menu items
  (let [item-findgrep (create-menu-item menu "FindGrep_newgrep_menuitem" new-findfiles-listener)]
	(. item-findgrep setAccelerator (+ SWT/MOD1 (int \G))))
  (new MenuItem menu SWT/SEPARATOR)
  ;; Run non lazy sequence operation
  (doseq [menu-key [{:name "FindGrep_grep_menuitem"   :proc findgrep-listener }
                    {:name "FindGrep_15min_menuitem"  :proc findgrep-listener }
                    {:name "FindGrep_2hrs_menuitem"   :proc findgrep-listener }
                    {:name "FindGrep_java_menuitem"   :proc findgrep-listener }  
                    {:name "FindGrep_logs_menuitem"   :proc findgrep-listener }
                    {:name "Findfiles_60min_menuitem" :proc findgrep-listener }
                    {:name "FindGrep_clj_menuitem"    :proc findgrep-listener } ]]
      (let [mitem (create-menu-item menu (menu-key :name) (menu-key :proc))]
        ;; Menu item created, now associate the widget with the global 'ref'
        (set-findgrep-widg-state (keyword (menu-key :name)) (str mitem))
        (get-findgrep-widg-state (keyword (menu-key :name))))))

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