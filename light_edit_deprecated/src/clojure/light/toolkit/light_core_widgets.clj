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

(ns light.toolkit.light_core_widgets
	(:use light.toolkit.light_utils
          light.toolkit.public_objects
          light.toolkit.light_config
          light.toolkit.light_main_constants
          light.toolkit.light_file_utils
          light.toolkit.light_gui_utils
          light.toolkit.light_tools
          light.toolkit.light_file_database
          light.toolkit.light_search_dialog
          light.toolkit.light_regex_search
          light.toolkit.light_analytics
          light.toolkit.light_jar_viewer
          light.toolkit.light_archives
          light.toolkit.light_xhtml_pdf
          light.toolkit.light_save_file)
	(:import (java.util Date)
			 (org.eclipse.swt SWT)
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
			 (java.util ResourceBundle Vector Hashtable)))

(import '(java.io BufferedReader File FileInputStream
                  FileNotFoundException IOException InputStreamReader Reader))
(import '(java.util ResourceBundle Vector Hashtable))
(import '(org.eclipse.swt.widgets Display Shell Text Widget TabFolder TabItem))
(import '(java.util HashMap))

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;; Function Impl
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

(defn create-tab-1 []
  (. tab-area-1 setText    tab-1-title)
  (. tab-area-1 setControl *styled-text*))

(defn create-tab-2 []
  (. tab-area-2 setText    tab-2-title)
  (. tab-area-2 setControl tab-text-2)
  (. tab-text-2 setFont    (styled-text-font)))

(defn create-tab-3 []
  (. tab-area-3 setText    tab-3-title)
  (. tab-area-3 setControl tab-text-3)
  (. tab-text-3 setFont    (styled-text-font)))

(defn create-all-tabs []
  (create-tab-1)
  (create-tab-2)
  (create-tab-3))

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

(def status-arm-listener
     (proxy [Listener] [] (handleEvent [event])))

(defn create-status-bar []
  (let [gd (new GridData SWT/FILL SWT/FILL true false)]
    (. status-bar setLayoutData gd)
    (. status-bar addListener SWT/Arm status-arm-listener)))

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

(def mkdirs-listener
     (proxy [SelectionListener][]
            (widgetSelected [event] 
                            (win-simple-mkdirs-handler))
            (widgetDefaultSelected [event] (win-simple-mkdirs-handler))))

(def select-all-main-listener
     (proxy [SelectionListener][]
            (widgetSelected [event] 
                            (when *styled-text*
                              (.selectAll *styled-text*)))
            (widgetDefaultSelected [event]
                                   (when *styled-text*
                                     (.selectAll *styled-text*)))))

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

(defn create-file-menu 
  "Add accelator CTRL + O, for OPEN MENU"
  [disp sh]
  ;;;;;;;;;;
  ;; Note change in 'doto' call, dot needed.
  (let [bar       (. sh getMenuBar)
        menu      (new Menu bar)
        item      (new MenuItem menu (. SWT PUSH))
        dir-item  (new MenuItem menu (. SWT PUSH))
        dummy3    (new MenuItem menu SWT/SEPARATOR)
        save-item (new MenuItem menu (. SWT PUSH))        
        save-as-item (new MenuItem menu (. SWT PUSH))
        dummy4    (new MenuItem menu SWT/SEPARATOR)
        all-item  (new MenuItem menu (. SWT PUSH))
        dummy1    (new MenuItem menu SWT/SEPARATOR)
        run-expl  (new MenuItem menu (. SWT PUSH))
        cmd-item  (new MenuItem menu (. SWT PUSH))
        cyg-item  (new MenuItem menu (. SWT PUSH))
        dummy2    (new MenuItem menu SWT/SEPARATOR)]
    (doto item
      ;;;;;;;;;;;;;;;;;;;;;;;;;;
      ;; Open File Menu Option
      ;;;;;;;;;;;;;;;;;;;;;;;;;
      (. setText (. resources-win getString "Open_menuitem"))
	  (. setAccelerator (+ SWT/MOD1 (int \O)))
      (. addSelectionListener 
         (proxy [SelectionAdapter] []
                (widgetSelected [e] (dialog-open-file)
                                println "Opening File"))))
    (doto dir-item
      ;; Open the directory dialog box
      (. setText (. resources-win getString "Opendir_menuitem"))
      (. addSelectionListener
         (proxy [SelectionAdapter] []
                (widgetSelected [e] (dialog-open-dir)))))
    (doto save-item
      ;; Open the directory dialog box
      (. setText (. resources-win getString "Save_menuitem"))
      (. setAccelerator (+ SWT/MOD1 (int \S)))
      (. addSelectionListener save-file-listener))
    (doto save-as-item
      ;; Open the directory dialog box
      (. setText (. resources-win getString "Save_as_menuitem"))
      (. addSelectionListener save-file-as-listener))
    (doto all-item
      ;; Select All
      (. setText (. resources-win getString "Select_all_menuitem"))
      (. setAccelerator (+ SWT/MOD1 (int \A)))
      (. addSelectionListener select-all-main-listener))
    (doto run-expl
      ;; Win explorer option
      ;; see light_tools.clj for the file manager launcher
      (. setText (. resources-win getString "Filemanager_menuitem"))
	  (. setAccelerator (+ SWT/MOD1 (int \E)))
      (. addSelectionListener 
         (proxy [SelectionAdapter] []
                (widgetSelected [e] (start-filemanager-proc)))))
    (doto cmd-item
      ;; Win explorer option
      ;; see light_tools.clj for the file manager launcher
      (. setText (. resources-win getString "Command_prompt_menuitem"))
	  (. setAccelerator (+ SWT/MOD1 (int \W)))
      (. addSelectionListener
         (proxy [SelectionAdapter] []
                (widgetSelected [e] (start-cmdprompt-proc)))))
    (doto cyg-item
      ;; Win explorer option
      ;; see light_tools.clj for the file manager launcher
      (. setText (. resources-win getString "Cygwin_prompt_menuitem"))
	  (. setAccelerator (+ SWT/MOD1 (int \M)))
      (. addSelectionListener
         (proxy [SelectionAdapter] []
                (widgetSelected [e] (start-cygwinprompt-proc)))))
    (doto (new MenuItem menu (. SWT PUSH))
      ;; Jar viewers
      (. setText (. resources-win getString "Jarfileview_menuitem"))
	  (. setAccelerator (+ SWT/MOD1 (int \J)))
      (. addSelectionListener 
         (proxy [SelectionAdapter] []
                (widgetSelected [e] (jar-viewer-handler)))))
    (doto (new MenuItem menu (. SWT PUSH))
      (. setText (. resources-win getString "Open_archive_menuitem"))
	  (. setAccelerator (+ SWT/MOD1 (int \Z)))
      (. addSelectionListener open-archive-file-listener))
    (new MenuItem menu SWT/SEPARATOR)
    (doto (new MenuItem menu (. SWT PUSH))
      (. setText (. resources-win getString "Make_dirs_menuitem"))
      (. addSelectionListener mkdirs-listener))
    ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
    ;; Create the recent file menu options
    ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
    (new MenuItem menu SWT/SEPARATOR)
    (create-recent-menu-items menu)    
    (new MenuItem menu SWT/SEPARATOR)
    ;; Create exit menu item last.
    (let [item-exit (new MenuItem menu (. SWT PUSH))]
      (doto item-exit
        (. setText (. resources-win getString "Exit_menuitem"))
		(. setAccelerator (+ SWT/MOD1 (int \Q)))
        (. addSelectionListener 
           (proxy [SelectionAdapter] []
                  (widgetSelected [evt] (exit) println "Exiting")))))
    menu))

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;; End of Create File Menu
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
    
(defn create-help-menu [disp sh]
  ;; Note change in 'doto' call, dot needed.
  (let [bar (. sh getMenuBar)
            menu (new Menu bar)
            item (new MenuItem menu (. SWT PUSH))]
    (doto item
      (. setText (. resources-win getString "About_menuitem"))
      (. addSelectionListener
         (proxy [SelectionAdapter] []
                (widgetSelected [event] (create-about-messagebox sh)))))
    menu))

(defn create-tools-menu 
  "Add the accelerator CTRL - D for the database viewer"
  [disp sh]
  ;; Note change in 'doto' call, dot needed.
  (let [bar (. sh getMenuBar)
            menu   (new Menu bar)]
    (doto (new MenuItem menu (. SWT PUSH))
      (. setText (. resources-win getString "Database_viewer_menuitem"))
	  (. setAccelerator (+ SWT/MOD1 (int \D)))
      (. addSelectionListener
         (proxy [SelectionAdapter] []
                (widgetSelected [event] (create-database-window *shell* true)))))
	(doto (new MenuItem menu (. SWT PUSH))
	  (. setText (. resources-win getString "File_properties_menuitem"))
	  (. setAccelerator (+ SWT/MOD1 (int \I)))
	  (. addSelectionListener win-file-prop-listener))
	(doto (new MenuItem menu (. SWT PUSH))
	  (. setText (. resources-win getString "Search_statistics_menuitem")))
    (doto (new MenuItem menu (. SWT PUSH))
      (. setText (. resources-win getString "Tools_pdf_menuitem"))
      (. addSelectionListener xhtml-to-pdf-listener))
    (doto (new MenuItem menu (. SWT PUSH))
      (. setText (. resources-win getString "Tools_buffer_pdf_menuitem"))
      (. addSelectionListener buffer-to-pdf-listener))
    (doto (new MenuItem menu (. SWT PUSH))
      (. setText (. resources-win getString "Tools_buffer_xhtml_menuitem"))
      (. addSelectionListener buffer-to-xhtml-listener))
    ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
    ;; Code Generation Tools
    ;; Code gen source is located in @see light_tools.clj
    ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
	(new MenuItem menu SWT/SEPARATOR)
    (add-codegen-menu-items menu)
    ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
    (new MenuItem menu SWT/SEPARATOR)
    menu))

(defn create-search-menu 
  "Add accelator CTRL + O, for FIND FILE"
  [disp sh]
  ;; Note change in 'doto' call, dot needed.
  (let [bar (. sh getMenuBar)
            menu (new Menu bar)
            item (new MenuItem menu (. SWT PUSH))
            regex-item (new MenuItem menu (. SWT PUSH))]	
    (doto item
      (. setText (. resources-win getString "Find_menuitem"))
	  (. setAccelerator (+ SWT/MOD1 (int \F)))
      (. addSelectionListener
         (proxy [SelectionAdapter] []
                (widgetSelected [event] (create-search-dialog sh)))))
    (doto regex-item
      (. setText (. resources-win getString "Regextool_menuitem"))
      (. addSelectionListener
         (proxy [SelectionAdapter] []
                (widgetSelected [event] (create-regex-window)))))
    ;; Add the 'find/grep' menu items
    (add-findgrep-options menu)
    menu))

(defn quick-open-file
  [user-prop-key]
  ;;;
  (let [default-file "/tmp/test.txt"
        fname        (if (empty? (prop-str resources-user user-prop-key)) 
                       default-file (prop-str resources-user user-prop-key))]
    (open-file fname false)))

(defn quick-file-menu-listener
  [quick-prop-key]
  ;;;;;;;;;;;;;;;;;
  (proxy [SelectionAdapter] []
         (widgetSelected [evt] (quick-open-file quick-prop-key))))                         

(defn add-buffer-menuitems [menu]
  (doto (new MenuItem menu (. SWT CASCADE))
	(. setText (res-win-str "Swap_buffer_to_second_menuitem"))
	(. setAccelerator SWT/F4)
	(. addSelectionListener swap-buffer-to-listener))
  (doto (new MenuItem menu (. SWT CASCADE))
	(. setText (res-win-str "Swap_buffer_from_second_menuitem"))
	(. setAccelerator SWT/F3)
	(. addSelectionListener swap-buffer-from-listener))
  ;; Add the quick buffer items
  (doto (new MenuItem menu (. SWT CASCADE))
    (. setText (res-win-str "Quick_file1_menuitem"))
	(. setAccelerator SWT/F5)
	(. addSelectionListener (quick-file-menu-listener "Quick_key_file1")))
  (doto (new MenuItem menu (. SWT CASCADE))
    (. setText (res-win-str "Quick_file2_menuitem"))
	(. setAccelerator SWT/F6)
	(. addSelectionListener (quick-file-menu-listener "Quick_key_file2")))
  (doto (new MenuItem menu (. SWT CASCADE))
    (. setText (res-win-str "Quick_file3_menuitem"))
	(. setAccelerator SWT/F7)
	(. addSelectionListener (quick-file-menu-listener "Quick_key_file3")))
  (new MenuItem menu SWT/SEPARATOR))

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;; End of add buffer menu items
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

(defn create-menu-bar [disp sh]
  (let [bar  (new Menu sh (. SWT BAR))
        item (new MenuItem bar (. SWT CASCADE))
        search-item    (new MenuItem bar (. SWT CASCADE))
        recent-buffers (new MenuItem bar (. SWT CASCADE))
        tools-item     (new MenuItem bar (. SWT CASCADE))
        analytics-item (new MenuItem bar (. SWT CASCADE))
        graphs-item    (new MenuItem bar (. SWT CASCADE))
        help-item      (new MenuItem bar (. SWT CASCADE))]        
    (. sh setMenuBar bar)
    ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
    ;; Build the Main Window Menu Bar
    ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
    (doto item
      (. setText (. resources-win getString "File_menuitem"))
      (. setMenu (create-file-menu disp sh)))
    (doto help-item
      (. setText (. resources-win getString "Help_menu_title"))
      (. setMenu (create-help-menu disp sh)))
    (doto search-item
      (. setText (. resources-win getString "Search_menu_title"))
      (. setMenu (create-search-menu disp sh)))
    (doto tools-item
      (. setText (. resources-win getString "Tools_menu_title"))
      (. setMenu (create-tools-menu disp sh)))
    (doto analytics-item
      (. setText (. resources-win getString "Analytics_menu_title"))
	  (. setMenu (create-analytics-menu disp sh)))
    (doto graphs-item
      (. setText (. resources-win getString "Graphs_menu_title")))
    (let [buf-menu (new Menu bar)]
	  (add-buffer-menuitems buf-menu)
      (doto recent-buffers
        (. setText (. resources-win getString "RecentBuffers_menu_title"))
        (. setMenu buf-menu))
      (set-buffer-menu-state buf-menu))))

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