#!/bin/sh

#####################################################################
# Startup Script for Light on Linux
#
# +Revision History
# Updated 3/5/2009 for linux and possibly mac
# Note: the cygwin launch is deprecated use ./octane.bat
#
#####################################################################
OS=`uname -a`

# Install Directory Path (editable attribute)
INSTALL_DIR=/usr/local/projects/light_edit

# Put the configuration directory in the classpath
CONF_LIB=$INSTALL_DIR/conf
LIB1=$INSTALL_DIR/lib/clojure.jar
LIB2=$INSTALL_DIR/lib/swt/win32/swt.jar
LIB3=$INSTALL_DIR/lib/jline-0.9.94.jar
LIB2_LINUX=$INSTALL_DIR/lib/swt/linux/swt.jar
LIB4=$INSTALL_DIR/lib/org.eclipse.jface.text_3.4.1.r341_v20080827-1100.jar

OCT_COMMONS=$INSTALL_DIR/lib/light_commons.jar

#####################################################################

JFREE_LIB=$INSTALL_DIR/lib/jfreechart
LIB6=$JFREE_LIB/gnujaxp.jar
LIB7=$JFREE_LIB/jcommon-1.0.15.jar
LIB8=$JFREE_LIB/jfreechart-1.0.12-experimental.jar
LIB9=$JFREE_LIB/jfreechart-1.0.12.jar
LIB10=$JFREE_LIB/jfreechart-1.0.12-swt.jar
LIB11=$JFREE_LIB/swtgraphics2d.jar

#####################################################################
PDF_LIB=$INSTALL_DIR/lib/pdf
PDF1=${PDF_LIB}/core-renderer.jar
PDF2=${PDF_LIB}/iText-2.0.8.jar
PDF3=${PDF_LIB}/minium.jar
PDF4=${PDF_LIB}/xercesImpl.jar
PDF5=${PDF_LIB}/tagsoup-1.2.jar
PDF_ALL=${PDF1}:${PDF2}:${PDF3}:${PDF4}:${PDF5}

#####################################################################
## -- Set SPRING Libraries --
#####################################################################
SPRING_LIB=$INSTALL_DIR/lib/spring
SPLIB1=$SPRING_LIB/spring-custom.jar
SPLIB2=$INSTALL_DIR/log4j.jar
SPLIB3=$SPRING_LIB/commons-logging.jar
SPR_CP=$SPLIB1:$SPLIB2:$SPLIB3

#####################################################################

LIGHT_SRC=$INSTALL_DIR/src/clojure

#####################################################################

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
		LIB_CP_JFREE=$LIB6:$LIB7:$LIB8:$LIB9:$LIB10:$LIB11
		CP=".:src/clojure:lib/spring/resources:${LIGHT_SRC}:${LIB1}:${LIB2}:${LIB3}:${OCT_COMMONS}:${SPR_CP}:${CONF_LIB}" ;;
esac 

echo "(SCRIPT): --------------------------------"
echo "(SCRIPT): install directory= $INSTALL_DIR"
echo "(SCRIPT): classpath= $CP"
echo "(SCRIPT): --------------------------------"

java -Xms128m -Xmx224m -classpath $CP -Dlight.install.dir="$INSTALL_DIR" \
	com.light.clojure.BasicTestWinMain %1 %2 %3 %4 %5 %6

####################
# End of Script
####################