@echo off

if "%OS%" == "Windows_NT" setlocal

REM -----------------------------------------------------------------
REM 
REM WIN32 STARTUP SCRIPT FOR LIGHT VIEWER
REM 1/20/2009 
REM 
REM -----------------------------------------------------------------

cd ..

octscripts.bat scripts/scripts/view_jar.clj %1 %2 %3 %4 %5

:end

exit /b
