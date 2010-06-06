
(ns light.test.win.basic_version)

(def *MAJOR_VERSION*
	 ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
	 ;; Edit the Major Version
     ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;	 
	 0
     ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
	 )

(def *MINOR_VERSION*
	 ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
	 ;; Edit the minor version
     ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;	 
	 5
     ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
	 )

(def *TYPE_VERSION*
	 ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
	 ;; Edit the minor version
     ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;	 
	 "alpha"
     ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
	 )

(def *DATE_STR_VERSION*
	 ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
	 ;; Edit the date string version
	 ;; YYYYmmdd
     ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;	 
     ;;"20090305"
	 "20090308"
     ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
	 )

(def *LIGHT_VERSION* (str  *MAJOR_VERSION* "." *MINOR_VERSION* "." *DATE_STR_VERSION* "." *TYPE_VERSION*))

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