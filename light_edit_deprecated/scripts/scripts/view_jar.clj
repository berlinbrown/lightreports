;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;;
;; Description: Example Octane/Clojure Script
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;


;; Note: use CTRL-D or CTRL-C to exit out of the REPL clojure command line
;; prompt

(ns scripts.view_jar
	(:use    octane.toolkit.octane_version
			 octane.toolkit.octane_utils
			 octane.toolkit.octane_jar_viewer)
	(:import (java.util Date)
			 (java.io File)))

(defn view-jar-file
  "This is a function comment, open the jar file"
  [filename]
  (println "Attempting to open jar file =>" filename)
  (println "Does the file exist? " (.exists (new File filename)))
  (println (open-jar-file-str filename)))

(defn script-main 
  "This is a function comment for the script-main function.
 The function does not have any arguments"
  []
  ;;;;
  (println "Running Jar Main, octane version=" *OCTANE_VERSION*)
  (view-jar-file "lib/light_commons.jar")
  (println "Done at " (str (new Date)))
  (exit))

;; Invoke Script Main, and exit on exceptions
(try (script-main)
	 (catch Exception e
			(.printStackTrace e)
			(exit)))

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;; End of Script
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;