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

(ns light.toolkit.light_file_database
	(:use light.toolkit.light_utils
          light.toolkit.public_objects
          light.toolkit.light_config
          light.toolkit.light_main_constants
          light.toolkit.light_file_utils
          light.toolkit.light_gui_utils
          light.toolkit.light_tools)
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

(def  load-default-database)

(def  database-shell      (new Shell *shell* *database-win-style*))
(def  db-location-box     (new Text database-shell SWT/BORDER))
(def  db-file-table       (new Table database-shell (bit-or SWT/SINGLE (bit-or SWT/BORDER SWT/FULL_SELECTION))))
(def  db-search-box       (new Text database-shell SWT/BORDER))
(def  db-button-comp      (new Composite database-shell SWT/NONE))
(def  db-totext-button    (new Button db-button-comp SWT/PUSH))
(def  db-filternm-button  (new Button db-button-comp SWT/PUSH))
(def  db-filtergrp-button (new Button db-button-comp SWT/PUSH))
(def  db-filtersrv-button (new Button db-button-comp SWT/PUSH))
(def  db-win-close-button (new Button db-button-comp SWT/PUSH))

;; Five table columns, loaded from database configuration file.
(def  table-col-names ["Name" "Path" "Group" "Server" "Description"])

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;; Proxy Helper for Database Buttons
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

(defn db-loc-bar [] (. db-location-box getText))

(defn format-db-table []
  ;; Format the db table to text and add to the buffer
  (when  db-file-table 
    (let [items  (. db-file-table getItems)
          col-ct (. db-file-table getColumnCount)]
      (when items
        ;; Iterate through the items and append to the buffer
        (let [buf (new StringBuffer)]
          (doseq [item items]
              ;; Pretty format the table text line
              (. buf append
                 (str (apply format "%40s %60s %20s %20s %20s\n" 
                             (list (. item getText 0) (. item getText 1) (. item getText 2)
                                   (. item getText 3) (. item getText 4))))))
          (add-text-buffer *styled-text* buffer-1 (. buf toString)))))))

(defn format-db-button-listener []
  (proxy [SelectionListener][]
         (widgetSelected [e] (format-db-table))
         (widgetDefaultSelected [e] (format-db-table))))

(defn create-db-grid-layout []
  (let [gridLayout (new GridLayout)]
    (set! (. gridLayout numColumns) 1)
    gridLayout))

(defn add-table-item [item-seq]
  (let [item (new TableItem db-file-table SWT/NONE)]
    (. item setText (into-array item-seq))))

(defn table-select-listener []
  (proxy [SelectionAdapter][]
         (widgetDefaultSelected 
          [event]
          (let [items (. db-file-table getSelection)]
            (when (> (count items) 0)                      
              ;; We can extract the open path
              ;; straight from the table item data.
              (let [item (first items)
                    db-path (. item getText 1)]
                (let [cmd-line-repl (simple-term-searchrepl (db-loc-bar) db-path)]
                  (if cmd-line-repl
                    (let []
                      (history-add-textln(str *newline* "Database screen, command-line search replace: " db-path " -> " cmd-line-repl *newline*))
                      (open-file-or-dir cmd-line-repl))
                    (open-file-or-dir db-path)))))))))

(defn create-database-table 
  "Create the database viewer table"
  [db-sh]
  ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
  
  (. db-file-table setHeaderVisible true)  
  (. db-file-table addSelectionListener (table-select-listener))
  ;; Add the table column headers
  ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
  ;; GridData = (int horizontalAlignment, int verticalAlignment, 
  ;;        boolean grabExcessHorizontalSpace, boolean grabExcessVerticalSpace) 
  ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
  (let [gd (new GridData SWT/FILL SWT/FILL true true)]
    (doseq [t table-col-names]
        (let [column (new TableColumn db-file-table SWT/NONE)]
          (. column setText  t)
          (. column setWidth *db-col-size*)))
    (. db-file-table setLayoutData gd)))

(def db-on-close-listener
     (proxy [SelectionListener][]
            (widgetSelected [event]
                            (set! (. event doit) false)
                            (. database-shell setVisible false))
            (widgetDefaultSelected [event]
                                   (set! (. event doit) false)
                                   (. database-shell setVisible false))))

(defn init-buttons-composite [sh]
  (let [comp db-button-comp
        gd-comp       (new GridData)
        gd-button     (new RowData *db-button-width* *db-button-height*)
        gd-med-button (new RowData *db-bttn-med-width* *db-button-height*)]
    (. comp setLayoutData gd-comp)
    (. comp setLayout (new RowLayout))
    (doto db-totext-button    (. setText *database-text-button*) (. setLayoutData gd-button))
    (doto db-filternm-button  (. setText *database-name-button*) (. setLayoutData gd-med-button))
    (doto db-filtersrv-button (. setText *database-grp-button*)  (. setLayoutData gd-button))
    (doto db-filtergrp-button (. setText *database-serv-button*) (. setLayoutData gd-med-button))
    (doto db-win-close-button (. setText *database-quit-button*) (. setLayoutData gd-med-button)
          (. addSelectionListener  db-on-close-listener))))

(defn init-database-helper [sh]
  (let [gd (new GridData SWT/FILL SWT/FILL true false)]
    (init-buttons-composite sh)
    (. db-location-box setLayoutData gd)
    (. db-location-box setText "term1 , term2 ; term3 , term4")
    (. db-search-box setLayoutData gd)   
    (. db-search-box setText "group")
    (. db-totext-button addSelectionListener (format-db-button-listener))
    (create-database-table sh)))

(defn get-db-table-size
  []
  ;;;;
  (when db-file-table
    (count (.getItems db-file-table))))

(defn add-table-size-message
  []
  ;;;;;
  (history-add-textln (str "<<< File Database Elements => " (get-db-table-size) " >>>")))

(defn create-database-window 
  "Initialize the file database SWT window, set the size add all components"
  [parent-sh open-win?]
    ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
  
    (history-add-textln "Opening file database screen (Tools -> Database Viewer)")
    (let [layout (create-db-grid-layout)]
      (. database-shell setText (. resources-win getString "Database_title"))
      (init-database-helper database-shell)
      (. database-shell setLayout layout)
      (. database-shell setSize *db-size-width* *db-size-height*)
      (. database-shell addShellListener
         (proxy [ShellAdapter][]
                (shellClosed [event]
                             (set! (. event doit) false)
                             (. database-shell setVisible false))))
      (if open-win? (let []
                      (. database-shell open)
                      (. database-shell setVisible open-win?))
          (. database-shell setVisible true))
      ;; Load the XML database      
      (when-let [tabl-sz (get-db-table-size)]
          (when (< tabl-sz 1)
            (load-default-database)
            (add-table-size-message)))
      ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
      ;; Enter display/shell loop for this window
      ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
      (let [disp (. database-shell getDisplay)]
        (shell-display-loop disp database-shell false "Database shell disposed"))))

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;; XML Database processing
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

(defn add-items-db-view [file-content]
  (let [tmp-db-obj (new Hashtable)]
    (doseq [xml-file file-content]
        (let [x-tag (xml-file :tag)
              x-content (xml-file :content)]       
          (cond
           (= ":file_name"   (str x-tag))
           (. tmp-db-obj put ":file_name" (.. (str (first x-content)) trim))
           (= ":file_path"   (str x-tag))
           (. tmp-db-obj put ":file_path" (.. (str (first x-content)) trim))
           (= ":file_group"  (str x-tag))
           (. tmp-db-obj put ":file_group" (.. (str (first x-content)) trim))
           (= ":file_server" (str x-tag))
           (. tmp-db-obj put ":file_server" (.. (str (first x-content)) trim))
           (= ":file_descr"  (str x-tag))
           (. tmp-db-obj put ":file_descr" (.. (str (first x-content)) trim)))))
    ;; Build a sequence to populate the database viewer        
    (let [tab-seq [(. tmp-db-obj get ":file_name")
                   (. tmp-db-obj get ":file_path")
                   (. tmp-db-obj get ":file_group")
                   (. tmp-db-obj get ":file_server")
                   (. tmp-db-obj get ":file_descr")]]
      (add-table-item tab-seq))))

(defn loop-db-files [xml-content]
  (doseq [xml-file xml-content]
      (let [x-tag (xml-file :tag)
            x-content (xml-file :content)]
        (when (= ":db_file" (str x-tag))
          (add-items-db-view x-content)))))

(defn try-open-db-file
  "Attempt to open database XML file."
  [db-filename db-path]
  ;;;;;;;;;;;;;;
  ;; Strange BUG: why is 'file://' needed.
  (try (let [xml-data (clojure.xml/parse (str "file:///" db-path))]
         xml-data)
       (catch Exception e
              ;; Build error message and return nil.
              (let [err-buf (new StringBuffer)]
                (. err-buf append
                   (str "ERR: while opening DB file : " (. e getMessage)
                        *newline* "ERR: DB-Filename => " db-filename *newline* "ERR: path => " db-path))
                (history-add-textln (. err-buf toString)))
              nil)))
      
(defn load-default-database []
  (let [db-filename (prop-str resources-user *prop-main-database*)
        db-path  (str *light-install-dir* *name-separator* db-filename)
        xml-data (try-open-db-file db-filename db-path)]
    (when xml-data
      (history-add-textln (str "Adding main file database data, path=>" db-path))
      (let [root (xml-data :content)]
        ;; Root contains a list of tags
        (doseq [root-set root]
            (let [root-tag (root-set :tag)
                           root-content (root-set :content)]
              (when (= ":file_set" (str root-tag))
                (loop-db-files root-content))))))))

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