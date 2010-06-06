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

(ns light.toolkit.light_templates)

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
(defn LightFullTest-{0} [_]
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
2008-03-17 01:28:38,500 INFO [org.hibernate.cfg.SettingsFactory] - <JDBC driver: MySQL-AB JDBC Driver, version: mysql-connector-java-5.0.8 ( Revision: ${svn.Revision} )>
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


;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;;; End of Script
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;