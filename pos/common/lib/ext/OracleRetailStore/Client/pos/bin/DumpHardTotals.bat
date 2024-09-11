@echo off
rem dum hard totals 

rem set the conduit script to load for POS client
set CONDUIT_CONFIG=classpath://config/conduit/DumpHTConduit.xml


rem Execute common client start script
call ClientConduit.bat %1
