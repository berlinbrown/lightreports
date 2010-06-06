@echo off

if "%OS%" == "Windows_NT" setlocal

REM -----------------------------------------------------------------
REM 
REM WIN32 STARTUP SCRIPT FOR LIGHT TEXT EDITOR
REM 1/20/2009 
REM 
REM -----------------------------------------------------------------

set CURRENT_DIR=%cd%

set INSTALL_DIR=C:\usr\local\projects\light_edit

set JAVA_OPTS=-Xms128m -Xmx164m

REM -----------------------------------------------------------------
REM Set java runtime programs
REM -----------------------------------------------------------------
set _RUNJAVA="%JAVA_HOME%\bin\java"
set _RUNJAVAW="%JAVA_HOME%\bin\javaw"
set _RUNJAVAC="%JAVA_HOME%\bin\javac"

set CLASSPATH=%JAVA_HOME%\lib\tools.jar

REM -----------------------------------------------------------------
REM Append to the classpath 
REM -----------------------------------------------------------------
set OCT_LIB1=%INSTALL_DIR%\lib\clojure.jar
set OCT_LIB2=%INSTALL_DIR%\lib\swt\win32\swt.jar
set OCT_LIB3=%INSTALL_DIR%\lib\jline-0.9.94.jar
set OCT_LIB4=%INSTALL_DIR%\lib
set OCT_LIB5=%INSTALL_DIR%\conf
set OCT_LIB_SRC=%INSTALL_DIR%\src

set OCT_COMMONS=%INSTALL_DIR%\lib\light_commons.jar

REM -- Set JFree Chart Libraries --
set JFREE_LIB=%INSTALL_DIR%\lib\jfreechart
set JLIB6=%JFREE_LIB%\gnujaxp.jar
set JLIB7=%JFREE_LIB%\jcommon-1.0.15.jar
set JLIB8=%JFREE_LIB%\jfreechart-1.0.12-experimental.jar
set JLIB9=%JFREE_LIB%\jfreechart-1.0.12.jar
set JLIB10=%JFREE_LIB%\jfreechart-1.0.12-swt.jar
set JLIB11=%JFREE_LIB%\swtgraphics2d.jar

REM -- Set the jFree chart libraries --
set LIB_CP_JFREE=%JLIB6%;%JLIB7%;%JLIB8%;%JLIB9%;%JLIB10%;%JLIB11%

set CLASSPATH=%CLASSPATH%;%OCT_LIB1%;%OCT_LIB2%;%OCT_LIB3%;%OCT_LIB4%;%OCT_LIB5%;%OCT_LIB_SRC%;%LIB_CP_JFREE%;%OCT_COMMONS%;%INSTALL_DIR%

echo (SCRIPT) ------------------------------------
echo %CLASSPATH%
echo %INSTALL_DIR%
echo (SCRIPT) ------------------------------------

%_RUNJAVA% %JAVA_OPTS% -classpath "%CLASSPATH%" -Dlight.install.dir="%INSTALL_DIR%" clojure.lang.Script "%INSTALL_DIR%\%1" -- %2 %3 %4 %5 %6

:end

exit /b
