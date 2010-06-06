#!/bin/sh
############################################################
# 
# Compile and Run the Tests
#
############################################################

OS=`uname -a`

# Install Directory Path (editable attribute)
INSTALL_DIR=/usr/local/projects/light_edit
TOP_DIR=`pwd`

############################################################
# Put the configuration directory in the classpath
############################################################
CONF_LIB=$INSTALL_DIR/conf
LIB1=$INSTALL_DIR/lib/clojure.jar
LIB2=$INSTALL_DIR/lib/swt/win32/swt.jar
LIB3=$INSTALL_DIR/lib/jline-0.9.94.jar
LIB2_LINUX=$INSTALL_DIR/lib/swt/linux/swt.jar
LIB4=$INSTALL_DIR/lib/org.eclipse.jface.text_3.4.1.r341_v20080827-1100.jar
LIB5=$INSTALL_DIR/lib/junit-4.4.jar

############################################################

LIB6=$TOP_DIR/lib/jmock/jmock-2.5.1.jar 
LIB7=$TOP_DIR/lib/jmock/hamcrest-library-1.1.jar
LIB8=$TOP_DIR/lib/jmock/hamcrest-core-1.1.jar
LIB9=$TOP_DIR/lib/jmock/cglib-nodep-2.1_3.jar
LIB10=$TOP_DIR/lib/jmock/objenesis-1.0.jar
LIB11=$TOP_DIR/lib/jmock/jmock-legacy-2.5.1.jar
CLOJURE=$TOP_DIR/../../src

############################################################

JFREE_LIB=$TOP_DIR/../../lib/jfreechart
LIB6j=$JFREE_LIB/gnujaxp.jar
LIB7j=$JFREE_LIB/jcommon-1.0.15.jar
LIB8j=$JFREE_LIB/jfreechart-1.0.12-experimental.jar
LIB9j=$JFREE_LIB/jfreechart-1.0.12.jar
LIB10j=$JFREE_LIB/jfreechart-1.0.12-swt.jar
LIB11j=$JFREE_LIB/swtgraphics2d.jar
LIB_CP_JFREE=$LIB6j:$LIB7j:$LIB8j:$LIB9j:$LIB10j:$LIB11j

############################################################

ANTLR_LIB=$INSTALL_DIR/lib/antlr
ANT1=$ANTLR_LIB/antlr-3.1.2.jar

SYNTAX1=$INSTALL_DIR/lib/light_commons_syntax.jar
ANTLR_CP=$ANT1:$SYNTAX1

############################################################

# Check the clojure library path
if [ -f $LIB1 ]
then
	echo "$LIB1 exists, continue"
else
	echo "$LIB1 does not exist, exiting"
	exit 1;
fi

# Check the SWT library path
if [ -f $LIB2 ]
then
	echo "$LIB2 exists, continue"
else
	echo "$LIB2 does not exist, exiting"
fi

echo $OS
case "$OS" in 
	*)
		LIB2=$LIB2_LINUX
		RELCP=".:./test:src:./classes:${INSTALL_DIR}/test/src/clojure"
		CP=".:./test:src:./classes:${RELCP}:${INSTALL_DIR}/src/clojure:${LIB1}:${LIB2}:${LIB3}:${LIB5}:${LIB6}:${LIB7}:${LIB8}:${LIB9}:${LIB10}:${LIB11}:$LIB_CP_JFREE:${CLOJURE}:${ANTLR_CP}:$CONF_LIB" ;;
esac 

echo "(SCRIPT): -------------------------"
echo "(SCRIPT): install directory= $INSTALL_DIR"
echo "(SCRIPT): classpath= $CP"
echo "(SCRIPT): -------------------------"

FILE1=$INSTALL_DIR/test/src/clojure/test/compile_tests.clj
MAIN1=test.light_test_suite

FILE=$FILE1
MAIN=$MAIN1

MAIN_SINGLE=test.light_test_suite_single

mkdir -vp classes

# Simple if and case statements to determine
# which application to run; compile or run test
SWITCH_ARG=$1
echo $SWITCH_ARG
if [ -z $SWITCH_ARG ]
then
	# Run both the compile and test
	java -Xms80m -Xmx164m -classpath $CP -Doctane.install.dir="$INSTALL_DIR" clojure.lang.Repl $FILE $1 $2 $3 $4
	java -Xms80m -Xmx164m -classpath $CP -Doctane.install.dir="$INSTALL_DIR" $MAIN $1 $2 $3 $4
else
	case "$SWITCH_ARG" in 
		compile )
			echo $FILE1
			java -Xms80m -Xmx164m -classpath $CP -Doctane.install.dir="$INSTALL_DIR" clojure.lang.Script $FILE $1 $2 $3 $4
			;;

		runtests )

			java -Xms80m -Xmx164m -classpath $CP -Doctane.install.dir="$INSTALL_DIR" $MAIN $1 $2 $3 $4
			;;

		singletest )
			java -Xms80m -Xmx164m -classpath $CP -Doctane.install.dir="$INSTALL_DIR" $MAIN_SINGLE $1 $2 $3 $4
			;;

		singlemem )
			java -verbosegc -verbosegc -verbosegc -XX:+PrintGCDetails -XX:+PrintGCTimeStamps -Xms80m -Xmx164m -classpath $CP -Doctane.install.dir="$INSTALL_DIR" $MAIN_SINGLE $1 $2 $3 $4
			;;

		singlehprof )
			java -verbosegc -Xrunhprof:file=classes/hprof_dump.txt,format=a -Xms80m -Xmx164m -classpath $CP -Doctane.install.dir="$INSTALL_DIR" $MAIN_SINGLE $1 $2 $3 $4
			;;

		singletime )
			time java -Xms80m -Xmx164m -classpath $CP -Doctane.install.dir="$INSTALL_DIR" $MAIN_SINGLE $1 $2 $3 $4
			;;

		*)
			java -Xms80m -Xmx164m -classpath $CP -Doctane.install.dir="$INSTALL_DIR" clojure.lang.Script $FILE $1 $2 $3 $4
			java -Xms80m -Xmx164m -classpath $CP -Doctane.install.dir="$INSTALL_DIR" $MAIN $1 $2 $3 $4
			;;
	esac
fi


##################################################
# End of Script
##################################################

