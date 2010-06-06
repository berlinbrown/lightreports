;;
;; Wrapper Source for compiling the Octane Application
;; Berlin Brown - 2/23/2009
;;

(defn main []
  (compile 'octane.toolkit.octane_main_window))

;; Invok Main
(try (main)
	(catch Exception e (. e printStackTrace)))

(. System exit 1)

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;; End of Script
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;