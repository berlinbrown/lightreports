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
;;;       7/15/2009 - Added Clojure 1.0, other performance fixes and cleanups.
;;;      
;;; Main Description: Light Log Viewer is a tool for making it easier to search log files.  
;;; Light Log Viewer adds some text highlighting, quick key navigation to text files, simple graphs 
;;; and charts for monitoring logs, file database to quickly navigate to files of interest, 
;;; and HTML to PDF convert tool.  
;;; Light Log was developed with a combination of Clojure 1.0, Java and Scala with use of libs, SWT 3.4, JFreeChart, iText. 
;;; 
;;; Quickstart : the best way to run the Light Log viewer is to click on the win32 batch script light_logs.bat
;;; (you may need to edit the Linux script for Unix/Linux environments).
;;; Edit the win32 script to add more heap memory or other parameters.

;;; The clojure source is contained in : HOME/src/octane
;;; The java source is contained in :  HOME/src/java/src

;;; To build the java source, see : HOME/src/java/build.xml and build_pdf_gui.xml

;;; Metrics: (as of 7/15/2009) Light Log Viewer consists of 6500 lines of Clojure code, and contains wrapper code
;;; around the Java source.  There are 2000+ lines of Java code in the Java library for Light Log Viewer.

;;; Additional Development Notes: The SWT gui and other libraries are launched from a dynamic classloader.  Clojure is also
;;;  started from the same code, and reflection is used to dynamically initiate Clojure. See the 'start' package.  The binary
;;;  code is contained in the octane_start.jar library.

;;; Home Page: http://code.google.com/p/lighttexteditor/
;;;  
;;; Contact: Berlin Brown <berlin dot brown at gmail.com>
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

(ns octane.toolkit.octane_templates)

(def *header-testgen*
";;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;;; Copyright (c) ....:. All rights reserved.
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
")

(def *header-testcase*
";;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;;; Copyright (c) ....:. All rights reserved.
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

(ns test
    (import (junit.framework Assert)))
")

(def *footer-testcase*
"
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;; End of Test Case
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
")

(def *simple-testcase*
"  
;;;;;;;;;;;;;;;;;;;;;;;;;
;; Test case for \"{0}\"
;;;;;;;;;;;;;;;;;;;;;;;;;
(defn OctaneFullTest-{0} [_]
  (Assert/fail \"Test not implemented ({0})\" ))
")
     
(def example-regex-document
"
-------------------
 Example Regex Notes and Textpad.  Text will highlight when a regex
 match is found.  Select the 'Find' button to query against the main buffer.
 See the History Console for additional messages.

 Note: when applying the regex in the regex window, use [SINGLE BACKSLASH] to escape
       a particular character.
-------------------
------------------
 Predefined Character Classes
------------------
.               Any character (may or may not match line terminators)
\\d              A digit: [0-9]
\\D              A non-digit: [^0-9]
\\s              A whitespace character: [ \\t\\n\\x0B\\f\\r]
\\S              A non-whitespace character: [^\\s]
\\w              A word character: [a-zA-Z_0-9]
\\W              A non-word character: [^\\w]
-------------------
 Quantifiers 
-------------------
X?       X??      X?+    X, once or not at all
X*       X*?     X*+     X, zero or more times
X+       X+?     X++     X, one or more times
X{n}     X{n}?      X{n}+   X, exactly n times
X{n,}    X{n,}?     X{n,}+  X, at least n times
X{n,m}   X{n,m}? X{n,m}+ X, at least n but not more than m times

-------------------
 Bounday Matches
-------------------
^              The beginning of a line
$            The end of a line
\\b            A word boundary
\\B            A non-word boundary
\\A            The beginning of the input
\\G            The end of the previous match
\\Z            The end of the input but for the final terminator, if any
\\z            The end of the input

[abc]          a, b, or c (simple class)
[^abc]         Any character except a, b, or c (negation)
[a-zA-Z]       a through z, or A through Z, inclusive (range)
[a-d[m-p]]     a through d, or m through p: [a-dm-p] (union)
[a-z&&[def]]   d, e, or f (intersection)

-------------------
 Examples
-------------------

^.*(<style type='text/css'>)(.*?)(</style>).*$

<style type='text/css'> div-header { stuff } </style>

Use (.*?) to get a non-greedy expression, which will allow 
the trailing (</style>) to match at the first opportunity.

<div id='header_title'> sfdfdfsdf  Detail   sdffdskljl</div>

Example Text below:
--------------------
20.049: [GC 28.200: [DefNew: 1921K->137K(1984K), 0.0006890 secs] 23030K->21247K(27320K), 0.0007550 secs]  [Times: user=0.00 sys=0.00, real=0.00 secs]
--------------------
Example Patterns:
(\\S*):\\s*\\[GC\\s*(\\S*):\\s*
\\[DefNew:\\s*(\\S*)(K|M)\\-\\>(\\S*)(K|M)\\((.*)$

------------------------------------------------------------
222.222.22.222 - XXXXX - [13/Feb/2009:00:19:58 -0400] \"POST /Servlet HTTP/1.1\" 200 - 

 | app01 | someapp | some.sql | whatid | theurl | 2009.02.13:13:24:58:936 | 2009.02.13:13:24:58:940 | 4 | data=YYYY,x_template=1

------------------------------------------------------------
The House of Representatives shall be composed of Members chosen every 
second Year by the People of the several States, and the Electors in each 
State shall have the Qualifications requisite for Electors of the 
most numerous Branch of the State Legislature.
...
...
123.3 239 .0 $2393 494.3 1,333,333.44
1/1/2009  01/01/2007  3.4.2008
(343) 323-3333  332-4434
SELECT * FROM test.test.com where inner join on 4 = 
http://www.google.com/search?hl=en&q=url+regex+examples&btnG=Search
http://www.test.com/path/path3/test.html?aj=434
...
...
Lorem ipsum dolor sit amet, consectetur adipiscing elit. Suspendisse 
laoreet porta ante. Nulla quam mauris, dapibus quis, eleifend sed, 
pellentesque vel, arcu. Donec pede diam, feugiat vel, posuere sed, 
ultricies a, lorem.
------------------------------------------------------------
------------------------------------------------------------
2008-03-17 01:28:38,460 INFO [org.hibernate.cfg.HbmBinder] - <Mapping collection: org.spirit.bean.impl.BotListPostSections.listings -> post_listing>
2008-03-17 01:28:38,460 INFO [org.hibernate.cfg.HbmBinder] - <Mapping collection: org.spirit.bean.impl.BotListDocFile.docs -> doc_file_metadata>
2008-03-17 01:28:38,460 INFO [org.hibernate.cfg.HbmBinder] - <Mapping collection: org.spirit.bean.impl.BotListLinkGroups.links -> group_links>
2008-03-17 01:28:38,461 INFO [org.hibernate.cfg.HbmBinder] - <Mapping collection: org.spirit.bean.impl.BotListCatLinkGroups.terms -> cat_group_terms>
2008-03-17 01:28:38,461 INFO [org.springframework.orm.hibernate3.LocalSessionFactoryBean] - <Building new Hibernate SessionFactory>
2008-03-17 01:28:38,465 WARN [org.hibernate.cfg.Environment] - <Property [hibernate.cglib.use_reflection_optimizer] has been renamed to [hibernate.bytecode.use_reflection_optimizer]; update your properties appropriately>
2008-03-17 01:28:38,466 INFO [org.hibernate.connection.ConnectionProviderFactory] - <Initializing connection provider: org.springframework.orm.hibernate3.LocalDataSourceConnectionProvider>
2008-03-17 01:28:38,500 INFO [org.hibernate.cfg.SettingsFactory] - <RDBMS: MySQL, version: 5.0.45-Debian_1ubuntu3.1-log>
2008-03-17 01:28:38,500 INFO [org.hibernate.dialect.Dialect] - <Using dialect: org.hibernate.dialect.MySQLDialect>
2008-03-17 01:28:38,501 INFO [org.hibernate.transaction.TransactionFactoryFactory] - <Using default transaction strategy (direct JDBC transactions)>
2008-03-17 01:28:38,501 INFO [org.hibernate.transaction.TransactionManagerLookupFactory] - <No TransactionManagerLookup configured (in JTA environment, use of read-write or transactional second-level cache is not recommended)>
2008-03-17 01:28:38,501 INFO [org.hibernate.cfg.SettingsFactory] - <Automatic flush during beforeCompletion(): disabled>
2008-03-17 01:28:38,501 INFO [org.hibernate.cfg.SettingsFactory] - <Automatic session close at end of transaction: disabled>
2008-03-17 01:28:38,501 INFO [org.hibernate.cfg.SettingsFactory] - <JDBC batch size: 15>
2008-03-17 01:28:38,501 INFO [org.hibernate.cfg.SettingsFactory] - <JDBC batch updates for versioned data: disabled>
2008-03-17 01:28:38,501 INFO [org.hibernate.cfg.SettingsFactory] - <Scrollable result sets: enabled>
2008-03-17 01:28:38,501 INFO [org.hibernate.cfg.SettingsFactory] - <JDBC3 getGeneratedKeys(): enabled>
2008-03-17 01:28:38,501 INFO [org.hibernate.cfg.SettingsFactory] - <Connection release mode: on_close>
1/16/14 15:45:14 hj qST [zNFw] [zntzwsdwsqhgqnt.szwsqztzqs] zntzwsdwsq.hgqnt.hgqnthltwzqnhjzngzntqzzhlznjznltqs=2
1/16/14 15:45:14 hj qST [zNFw] [zntzwsdwsqhgqnt.szwsqztzqs] zntzwsdwsq.hgqnt.hgqntNhjq=WqqSshqzq hgqnt
1/16/14 15:45:14 hj qST [zNFw] [zntzwsdwsqhgqnt.szwsqztzqs] zntzwsdwsq.hgqnt.hgqntNhjqSfstqjszwsqztfKqf=
1/16/14 15:45:14 hj qST [zNFw] [zntzwsdwsqhgqnt.szwsqztzqs] zntzwsdwsq.hgqnt.qlhjq.tfsq=sthndhzd
1/16/14 15:45:14 hj qST [zNFw] [zntzwsdwsqhgqnt.szwsqztzqs] zntzwsdwsq.hgqnt.dhnhltwNhjqhgqnt=tzlq
1/16/14 15:45:14 hj qST [zNFw] [zntzwsdwsqhgqnt.szwsqztzqs] zntzwsdwsq.hgqnt.dlwnqdhgqnt=fhlsq
1/16/14 15:45:14 hj qST [zNFw] [zntzwsdwsqhgqnt.szwsqztzqs] zntzwsdwsq.hgqnt.dlstwjszwdqssNhjq=WqqSshqzq
1/16/14 15:45:14 hj qST [zNFw] [zntzwsdwsqhgqnt.szwsqztzqs] zntzwsdwsq.hgqnt.dqfhlltszwdqssNhjq=lnknwwnszwdqss
1/16/14 15:45:14 hj qST [zNFw] [zntzwsdwsqhgqnt.szwsqztzqs] zntzwsdwsq.hgqnt.dzshqlqLwgFzlqhltwNhjzng=fhlsq
1/16/14 15:45:14 hj qST [zNFw] [zntzwsdwsqhgqnt.szwsqztzqs] zntzwsdwsq.hgqnt.qntqzszzsqjhnhgqz.dwnnqdtzwnwzdqz=DqFhlLT
1/16/14 15:45:14 hj qST [zNFw] [zntzwsdwsqhgqnt.szwsqztzqs] zntzwsdwsq.hgqnt.qntqzszzsqjhnhgqz.tzhnsswzt.tds.hwst.DqFhlLT=wzlfqj.tqst.sfshq.dwj
1/16/14 15:45:14 hj qST [zNFw] [zntzwsdwsqhgqnt.szwsqztzqs] zntzwsdwsq.hgqnt.qntqzszzsqjhnhgqz.tzhnsswzt.tds.swzt.DqFhlLT=15111
1/16/14 15:45:14 hj qST [zNFw] [zntzwsdwsqhgqnt.szwsqztzqs] zntzwsdwsq.hgqnt.qntqzszzsqjhnhgqz.tzhnsswzt.tds.swdkqtfhdtwzf.DqFhlLT=dwj.wzlf.zsqnghzd.swstwffzdqhlq.lznk.nqt.DqfhlltSwdkqtFhdtwzf
1/16/14 15:45:14 hj qST [zNFw] [zntzwsdwsqhgqnt.szwsqztzqs] zntzwsdwsq.hgqnt.qzzwzsnhsshwts.qnhqlq=tzlq
1/16/14 15:45:14 hj qST [zNFw] [zntzwsdwsqhgqnt.szwsqztzqs] zntzwsdwsq.hgqnt.qzzwzsnhsshwts.thzwttlq=11
1/16/14 15:45:14 hj qST [zNFw] [zntzwsdwsqhgqnt.szwsqztzqs] zntzwsdwsq.hgqnt.qxtqnszwns.dzzqdtwzf=qxt
2114-11-12 12:16:53,241 DqqlG [NSlHqwlz - sDq53] (dwj.dh.lqj.fzltqzs.lqjzqqlqstszwdqsswz [414]:121) - lqjzqqlqstszwdqsswz: zqqlqstlzl=httss://swltqst.szzjqzzdh.dwj/lqj/hdtzzztf/szwssqdts.dw?jqthwd=qdzt&zndqx=1
2114-11-12 12:16:53,241 DqqlG [NSlHqwlz - sDq53] (dwj.dh.lqj.fzltqzs.lqjzqqlqstszwdqsswz [414]:243) - Thq jqthwd shzhj zs qdzt
2114-11-12 12:16:53,241 DqqlG [NSlHqwlz - sDq53] (dwj.dh.lqj.fzltqzs.lqjzqqlqstszwdqsswz [414]:262) - Thq hdtzwn zd zs dwj.dh.lqj.hdtzwns.hdtzzztf.szwssqdts
2114-11-12 12:16:53,242 DqqlG [NSlHqwlz - sDq53] (dwj.dh.lqj.fzltqzs.lqjzqqlqstszwdqsswz [414]:263) - Thq hdtlhl hdtzwn zd zs dwj.dh.lqj.hdtzwns.hdtzzztf.szwssqdts.qdzt()
2114-11-12 12:16:53,244 zNFw [NSlHqwlz - sDq53] (dwj.dh.lqj.fzltqzs.lqjzqqlqstszwdqsswz [414]:224) - Thq lsqz hhs hddqss tw /hdtzzztf/szwssqdts
2114-11-12 12:16:53,245 WhzN [NSlHqwlz - sDq53] (dwj.dh.lqj.hdtzwns.hdtzzztf.szwssqdts:234) - Nw Lwdhtzwn Gzzqn wz sqsszwn tzjqwlt
2114-11-12 12:16:53,313 DqqlG [NSlHqwlz - sDq53] (dwj.dh.lqj.hdtzwns.hdtzzztf.szwssqdts:125) - qdzt tFwzj.35
2114-11-12 12:16:53,323 DqqlG [NSlHqwlz - sDq53] (dwj.dh.lqj.tzlqs.Thqdwntzwllqz:42) - Thzs zs thq 6/16 zqzszwn wf thzs fzlq.
2114-11-12 12:16:53,324 DqqlG [NSlHqwlz - sDq53] (dwj.dh.lqj.tzlqs.Thqdwntzwllqz:24) - zqqlqstqd lzL: httss://swltqst.szzjqzzdh.dwj/lqj/hdtzzztf/szwssqdts.dw?jqthwd=qdzt&zndqx=1
2114-11-12 12:16:53,324 DqqlG [NSlHqwlz - sDq53] (dwj.dh.lqj.tzlqs.Thqdwntzwllqz:25) - zqfqzqz lwdhtzwn shth: nlll
2114-11-12 12:16:53,331 DqqlG [NSlHqwlz - sDq53] (dwj.dh.lqj.tzlqs.Thqdwntzwllqz:32) - hdtzwnzd=dwj.dh.lqj.hdtzwns.hdtzzztf.szwssqdts.qdzt()
2114-11-12 12:16:53,331 DqqlG [NSlHqwlz - sDq53] (dwj.dh.lqj.tzlqs.Thqdwntzwllqz:111) - shzqnthlThq zs stzll nlll
2114-11-12 12:16:53,332 DqqlG [NSlHqwlz - sDq53] (dwj.dh.lqj.tzlqs.Thqdwntzwllqz:131) - hdtzwn zD: dwj.dh.lqj.hdtzwns.hdtzzztf.szwssqdts.sljjhzf()
2114-11-12 12:16:53,333 DqqlG [NSlHqwlz - sDq53] (dwj.dh.lqj.tzlqs.Thqdwntzwllqz:134) - zqqlqst zs dqhlzng wzth h dzffqzqnt thq
2114-11-12 12:16:53,333 DqqlG [NSlHqwlz - sDq53] (dwj.dh.lqj.tzlqs.Thqdwntzwllqz:221) - zqqlqst zs dqhlzng wzth lwdhtzwns slq thq wzth h lwdhtzwn zd qlqzf stzzng
2114-11-12 12:16:53,334 DqqlG [NSlHqwlz - sDq53] (dwj.dh.lqj.tzlqs.Thqdwntzwllqz:256) - dwj.dh.lqj.hdtzwns.hdtzzztf.szwssqdts.sljjhzf() zs zn thq slqthqs wf thq shzqnt /hdtzzztf/szwssqdts?jqthwd=sljjhzf&znzt=1
2114-11-12 12:16:53,335 DqqlG [NSlHqwlz - sDq53] (dwj.dh.lqj.tzlqs.Thqdwntzwllqz:311) - Sqhzdhzng fwz shzqnt nwdq zn Thqdwntzwllqz...
2114-11-12 12:16:53,336 DqqlG [NSlHqwlz - sDq53] (dwj.dh.lqj.tzlqs.Thqdwntzwllqz:311) - hdtzwn zD: dwj.dh.lqj.hdtzwns.hdtzzztf.szwssqdts.sljjhzf()
2114-11-12 12:16:53,332 DqqlG [NSlHqwlz - sDq53] (dwj.dh.lqj.tzlqs.Thqdwntzwllqz:333) - zqqlqst zs dqhlzng wzth h dzffqzqnt thq
------------------------------------------------------------
------------------------------------------------------------
")

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;;
;; Startup Tips Templates
;; Array of Arrays:
;; [id, feature id, tip
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

(def *help-startup-tips*
[
[ "Tip-1" "F-0001-jar" 
  "To view the jar file entries in an archived jar file, see the menu option [File -> Jar File Viewer]" ]
[ "Tip-2" "F-0002-history" 
  "Click on the history tab to view a detailed log of the application's operations.\n  Use the File menu option to save the history log to file" ]  
[ "Tip-3" "F-0003-location" 
  "Enter '.'/dot in the location bar at the top text edit entry field to see your current location" ]
[ "Tip-4" "F-0004-dbview"
  "The database view contains a table listing of key log files.\n  Click on a log file to view the contents.\n  Edit the [INSTALL-DIR]/conf/sys/*.xml file to change the database listings.\n  [see Tools -> Database File Viewer]" ]
[ "Tip-5" "F-0005-regextool" 
  "Use the regex test tool to explore different 'Java' regex combinations.  Enter the regex expression in the top text area. [Search -> Regex Search Tool]" ]
[ "Tip-6" "F-0006-mergetool"
  "You can use the merge log file tool to combine 'log' files into one file based on timestamps on each line. [Tools -> Merge Log File Viewer]" ]
[ "Tip-7" ""
  "When you launch a java application, you can add '-verbose' to the jvm args for class loading information.  Example 'java -verbose:class Run'" ]
[ "Tip-8" "F-0007-archivesearch"
  "Use the archive search tool to extract *.Z unix compressed files [Search -> Search Arhive File By Date]" ]
[ "Tip-9" "F-0008-dir"
  "To see the file details or directories from some base directory, use the Open Directory menu option" ]
[ "Tip-10" "F-0006-mergetool"
  "When using the GUI merge log tool, the tool may limit the number of lines that are merged due to memory constraints.  Use the virtual file search or use the quick merge to only concatenate multiple files" ]
[ "Tip-11" ""
  "Regularly check the history tab to view the application log" ]
[ "Tip-12" "F-0010-server"
  "Octane launches a http server at startup.  Use it to browse log files or the other admin functions.  Visit http://localhost:7771 in your favorite browser" ]
])

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

(def *startup-tips-random*  (java.util.Random. (System/currentTimeMillis)))

(defn next-startup-tip
  "Select the next startup tip data structure"
  []
  ;;;;
  (let [nid (.nextInt *startup-tips-random* (count *help-startup-tips*))
        tip-data (nth *help-startup-tips* nid)]
    tip-data))

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

(defn get-tip-id
  [tip-data]
  (when tip-data
    (first tip-data)))

(defn get-tip-feature
  [tip-data]
  (when tip-data
    (second tip-data)))

(defn get-tip-string
  [tip-data]
  (when tip-data
    (nth tip-data 2)))

(defn format-next-tip
  []
  ;;;
  (let [tip (next-startup-tip)]
    (if (not tip) "No Tip Data Found"
        (str "[!] Did you know? " \newline "[!] "  
             (get-tip-id tip) " : " (get-tip-string tip) \newline
             "[!] (feature id : " (get-tip-feature tip) ")"))))
          
;;; End of Startup Tips ;;;

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;; REVISION HISTORY - Light Logs Clojure Source
;;
;; -------------------------------------
;; + 1/5/2009  Berlin Brown : Project Create Date

;; + 1/5/2009  Berlin Brown : Add new headers
;; + 6/23/2009 : Major bug fixes
;; + 6/23/2009 : Move database file to classpath
;; + 6/23/2009 : Colorize log file
;; + 6/23/2009 : Show number of lines in a file
;; + 6/23/2009 : Quick Merge Files Together
;; + 6/23/2009 : Filter only the lines that have search terms and which line number
;; + 6/23/2009 : Have an additional merger but no true time merge (see cat command)
;; + 6/23/2009 : Print number of lines in buffer
;; -------------------------------------

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;;; End of Script
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;