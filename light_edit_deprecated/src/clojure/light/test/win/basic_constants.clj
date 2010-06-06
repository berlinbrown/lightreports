;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;;;
;;; Copyright (c) 2006-2007, 

;;; All rights reserved.
;;;
;;; Clojure version: Clojure release 200903

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

(ns light.test.win.basic_constants
    (:use light.test.win.basic_version)
    (:import (org.eclipse.swt.graphics Color RGB)
             (org.eclipse.swt SWT)
             (java.text MessageFormat)
             (java.util.regex Pattern)
             (java.nio CharBuffer MappedByteBuffer)
             (java.nio.channels FileChannel)
             (java.nio.charset Charset)
             (java.nio.charset CharsetDecoder)
             (java.util.regex Matcher)
             (java.util.regex Pattern)
             (java.util.regex PatternSyntaxException)
             (java.nio ByteBuffer)))

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;; Core Window static constant defines
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

(def *graph-size-width*   700)
(def *graph-size-height*  600)

(def win-size-width       890)
(def win-size-height      580)

(def *db-button-width*    150)
(def *db-bttn-med-width*  134)
(def *db-button-height*    28)

(def *database-compile-button*     "Compile Tests")
(def *database-runtests-button*    "Run Tests")
(def *database-single-test-button* "Single Test")
(def *database-memory-button*      "Memory Test")
(def *database-hprof-button*       "HPROF Test")
(def *database-quit-button*        "Exit")

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

(def *process-gentests-sh* "/usr/local/projects/light_edit/test/src/clojure/gentests.sh")

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

(def *about-version-msg*
"Light Text Editor

Version: {0}

{1}

Light is text editor application for editing, viewing 
and searching through plain text documents.  
It also contains graph and charting utilities.

At least Java Runtime 1.5 is required")

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

(def *Basic_Window_title* "Test Tool")

(def *Basic_About_1* "About")
(def *Basic_About_2* "About")

(def *about-version* (. MessageFormat format *about-version-msg*
						(to-array [*LIGHT_VERSION*  *Basic_About_2*])))

;; Note: the work path may be set automatically by 'light-config' get install directory.
(def *newline* "\n")

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;; RGB colors used when setting the color scheme for a text area.
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
(def orange-sel-color (new RGB 250 209 132))
(def lightgrey-color  (new RGB 100 100 100))
(def red-color        (new RGB 255 0     0))
(def green-color      (new RGB 18  152  14))
(def white-color      (new RGB 255 255 255))
(def cyan-sel-color   (new RGB 64  224 208))
(def dark-blue-color  (new RGB 34  38  167))
(def yellow-color     (new RGB 255 255   0))
(def black-color      (new RGB 10  10   10))

;; Hard code the style to avoid calling bitwise operator
;; SWT.BORDER | SWT.MULTI | SWT.V_SCROLL | SWT.H_SCROLL
(def swt-text-style (bit-or SWT/BORDER (bit-or SWT/MULTI (bit-or SWT/H_SCROLL SWT/V_SCROLL))))

(def *database-win-style*   (bit-or SWT/CLOSE (bit-or SWT/BORDER (bit-or SWT/TITLE SWT/MIN))))

(def *openfile-wildcard-seq* ["*.*" "*.log" "*.olog" "*.ologs" "*.octlog"
                              "*.Mon" "*.Tue" "*.Wed" "*.Thu" "*.Fri"])
(def *sysout-wildcard-seq*   ["*.log" "*.Mon" "*.Tue" "*.Wed" "*.Thu" "*.Fri" "*.*"])
(def *jar-wildcard-seq*      ["*.jar" "*.zip" "*.*"])
(def *zip-wildcard-seq*      [ "*.Z" "*.zip" "*.jar" "*.*" ])

;; For Regex Patterns, Flags may include CASE_INSENSITIVE, MULTILINE, DOTALL, UNICODE_CASE, and CANON_EQ
;; Establish the charset and decoder, used with grep functionality.
(def *regex-line-pattern* (. Pattern compile ".*\\r?\\n"))
(def *iso-8859-charset*   (. Charset forName "ISO-8859-15"))
(def *charset-decoder*    (. *iso-8859-charset* newDecoder))

(defn get-char-buf-decoder
  "Get java nio character buffer from decoder"
  [doc]
  ;;;;;;;;;;;
  (let [dummy1 (. *charset-decoder* reset)
        ;; BugFix/Hack, adding newline to end of document
        char-buf (. *charset-decoder* decode (. ByteBuffer wrap (. (str doc \newline) getBytes)))]
    char-buf))

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