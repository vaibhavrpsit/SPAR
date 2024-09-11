@echo off

if not exist %0 goto ERROR
if "%1"=="ADDJAR" goto ADDJAR

REM add the specified dir to the classpath
set CLASSPATH=%CLASSPATH%;%1

REM find each jar file in this dir and call ADDJAR
for %%i in ("%1\*.jar") do call addtoclasspath.bat ADDJAR %%i
goto DONE

:ADDJAR
REM add the specified jar to the classpath
set CLASSPATH=%CLASSPATH%;%2
goto DONE

:ERROR
echo.
echo You must run this batch file with it's full name (including the extension).
echo.

:DONE
