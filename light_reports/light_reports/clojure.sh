#!/bin/sh
###########################################################

# mkdir -vp /usr/local/projects/light_logs/conf/sys/_work

OS=`uname -a`
LIB1=./lib/clojure.jar
LIB2=./lib/swt/win32/swt.jar
LIB3=./lib/jline-0.9.94.jar
LIB4=./lib
LIB2_LINUX=./lib/swt/linux/swt.jar

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
	CYGWIN* )
		LIB1=`cygpath -w $LIB1` 
		LIB2=`cygpath -w $LIB2`
		LIB3=`cygpath -w $LIB3`
		LIB4=`cygpath -w $LIB4`
		CP=".;${LIB1};${LIB2};${LIB3};${LIB4}" ;;
	*)
		LIB2=$LIB2_LINUX
		CP=".:${LIB1}:${LIB2}:${LIB3}" ;;
esac 

echo $CP
java -classpath $CP jline.ConsoleRunner clojure.lang.Repl $1 $2 $3 $4
#java -classpath $CP jline.OctaneConsoleRunner clojure.lang.Repl $1 $2 $3


###########################################################
# End of Script
###########################################################
