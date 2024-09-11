@echo off
setlocal



REM KEYSTORE and KEYTOOL set by installer
set KEYSTORE=@KEYSTORE@
set KEYTOOL=@JAVA_HOME@/bin/keytool.exe

REM command and password always required
set COMMAND=%1
set PASS=%2

REM if alias is required, its parameter #2
set ALIAS=-alias %3

REM if ValidityDays is required, its parameter #3
set VALIDITY=%4

REM if DISTINGUISHED NAME is required, its parameter #4
set DN=%5

SET STORETYPE=""
REM WAS SET STORETYPE=-storetype pkcs12
REM NOTWAS SET STORETYPE=-storetype jks


REM input or output file is always in current directory with standard name for certificates or requests
set FILE=certfile.pem
set REQFILE=certfile.req


REM LISTALL requires only password
if .%2.==.. goto :ERROR
if %1==LISTALL  set COMMAND=-list&GOTO :command


REM ALL other commands requires an alias as well
if .%3.==.. goto :ERROR
if %1==REQUEST set COMMAND=-certreq %ALIAS% -file %REQFILE% -keypass %PASS%&GOTO :command
if %1==DELETE  set COMMAND=-delete %ALIAS%&GOTO :command
if %1==LIST  set COMMAND=-list %ALIAS%&GOTO :command
if %1==IMPORTCACERT  set COMMAND=-importcert %ALIAS% -trustcacerts -file %FILE%&GOTO :command
if %1==IMPORTKEYSTORE set COMMAND=-importcert %ALIAS% -file %FILE%&GOTO :command
if %1==EXPORT set COMMAND=-exportcert %ALIAS% -file %FILE%&GOTO :command



REM CREATESELFSIGNED requires an distinguished name and a validity length
if .%5.==.. goto :ERROR
if %1==CREATESELFSIGNED set COMMAND=-genkey %ALIAS% -dname %DN% -validity %VALIDITY% -keypass %PASS%&GOTO :command

GOTO :ERROR


:command
%KEYTOOL% %COMMAND% -keystore %KEYSTORE% -v -storepass %PASS%  %STORETYPE%
goto :END

:ERROR
echo cert.cmd "[LISTALL] <PASSWORD>"
echo cert.cmd "[REQUEST DELETE LIST IMPORTCACERT IMPORTKEYSTORE EXPORT] <PASSWORD> <ALIAS>"
echo cert.cmd "[CREATESELFSIGNED] <PASSWORD> <ALIAS> <numberofvaliddays> <DISTINQUISHED NAME>"
echo.
echo Sample Distinguished Name="CN=common name, OU=Org Unit, O=Org, L=City, ST=State, C=Country"
echo NOTE: Distinguished Name MUST BE QUOTED
echo.
echo your parameters were: 1:"%1" 2:"%2" 3:"%3" 4:"%4"
:END
endlocal

echo on