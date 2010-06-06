;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;;
;; Description: Example Octane/Clojure Script
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

;; Note: use CTRL-D or CTRL-C to exit out of the REPL clojure command line
;; prompt

(ns scripts.hello_world
	(:use    octane.toolkit.octane_version
			 octane.toolkit.octane_utils)	
	(:import (java.util Date)))

(defn script-main 
  "This is a function comment for the script-main function."
  []
  ;;;;
  (println "Running Hello World Main, octane version=" *OCTANE_VERSION*)
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