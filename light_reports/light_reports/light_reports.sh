#!/bin/sh

#############################
# Note: the cygwin launch is deprecated use ./octane.bat
# but the script can be used on linux/unix
#############################

OS=`uname -a`

# Install Directory Path (editable attribute)
INSTALL_DIR=`pwd`

# Put the configuration directory in the classpath
L1=$INSTALL_DIR/conf
L1B=$INSTALL_DIR/src
L1C=$INSTALL_DIR/src/java/src
L2=$INSTALL_DIR/lib/clojure.jar
L3=$INSTALL_DIR/lib/swt/3.5/linux/swt-debug.jar
L4=$INSTALL_DIR/lib/log4j-1.2.15.jar
L5=$INSTALL_DIR/lib/octane_commons.jar

P1=$INSTALL_DIR/lib/newpdf/core-renderer.jar
P2=$INSTALL_DIR/lib/newpdf/iText-2.0.8.jar
P3=$INSTALL_DIR/lib/newpdf/minium.jar
P4=$INSTALL_DIR/lib/newpdf/tagsoup-1.2.jar

PDF=$P1:$P2:$P3:$P4:
CORE=$L1:$L1B:$L1C:$L2:$L3:$L4:$L5:

CP=.:$CORE:$PDF

# Check the clojure library path
if [ -f $L2 ]
then
	echo "$L2 exists, continue"
else
	echo "$L2 does not exist, exiting"
	exit 1;
fi

# Check the SWT library path
if [ -f $L3 ]
then
	echo "$L3 exists, continue"
else
	echo "$L3 does not exist, exiting"
fi

echo $OS
case "$OS" in 
	CYGWIN* )
		#L1=`cygpath -w $LIB1` 
		#INSTALL_DIR=`cygpath -w $INSTALL_DIR`		
		# SET THE CLASSPATH for CYGWIN
		echo "Cygwin shell script deprecated, use the bat script"
		exit 
		;;
	*)
		CP=$CP
		;;
esac 

echo "--------------------------------"
echo " install directory= $INSTALL_DIR"
echo " classpath= $CP"
echo "--------------------------------"

java -Xms128m -Xmx224m -classpath $CP -Doctane.install.dir="$INSTALL_DIR" \
  clojure.main src/octane/toolkit/octane_main_window.clj

# End of Script
