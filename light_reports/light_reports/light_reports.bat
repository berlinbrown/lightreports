@echo off

if "%OS%" == "Windows_NT" setlocal

REM -----------------------------------------------------------------
REM Win/Cygwin script updated 5/10/2010
REM tested with new clojure1.1 and new SWT 3.5.2
REM -----------------------------------------------------------------

set JAVA_OPTS=-Xms140m -Xmx240m

set CURRENT_DIR=%CD%
set INSTALL_DIR=%CURRENT_DIR%
set _RUNJAVA="java"

REM -----------------------------------------------------------------
REM CORE LIBS (swt, clojure)
REM -----------------------------------------------------------------
set L1=%INSTALL_DIR%\conf
set L1B=%INSTALL_DIR%\src
set L1C=%INSTALL_DIR%\src\java\src
set L2=%INSTALL_DIR%\lib\clojure.jar
set L3=%INSTALL_DIR%\lib\swt\3.5\win32\swt-debug.jar
set L4=%INSTALL_DIR%\lib\log4j-1.2.15.jar
set L5=%INSTALL_DIR%\lib\octane_commons.jar

REM -----------------------------------------------------------------
REM PDF LIBS
REM iText 2.0.8 released 2008
REM -----------------------------------------------------------------
set P1=%INSTALL_DIR%\lib\newpdf\core-renderer.jar
set P2=%INSTALL_DIR%\lib\newpdf\iText-2.0.8.jar
set P3=%INSTALL_DIR%\lib\newpdf\minium.jar
set P4=%INSTALL_DIR%\lib\newpdf\tagsoup-1.2.jar

REM -----------------------------------------------------------------
set S1=%INSTALL_DIR%\lib\scala\scala280r3\scala-library.jar

REM -----------------------------------------------------------------

set PDF=%P1%;%P2%;%P3%;%P4%;
set CORE=%L1%;%L1B%;%L1C%;%L2%;%L3%;%L4%;%L5%

set CP=.;%CORE%;%PDF%;%S1%;

echo _CURRENT_DIR:
echo %CURRENT_DIR%
echo _CLASSPATH:
echo %CP%
echo ------

%_RUNJAVA% %JAVA_OPTS% -Doctane.install.dir="%INSTALL_DIR%"\ -cp %CP% clojure.main "%INSTALL_DIR%\src\octane\toolkit\octane_main_window.clj" -- %1 %2 %3 %4 %5 %6 

:end
exit /b
