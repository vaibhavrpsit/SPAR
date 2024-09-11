::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::
::
:: Copyright (c) 2016 - 2017 MAX Hypermarket, Inc.    All Rights Reserved.
::
::	Rev	1.2 	Dec 16, 2016	Ashish Yadav	Changes to add jars for Store Credit
::	Rev	1.1 	Dec 16, 2016	Ashish Yadav	Changes to add jars for Employee Discount
::	Rev	1.0 	Nov 19, 2016	Mansi Goel		Changes to add jars for customized code
::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::
:: 
::
::   POSENV.BAT
::
:: Sets up the environment needed for the Oracle Retail POS application.
::
:::::::::::::::::::::::::::::::::::::::::::::::::::::::::

:::::::::::::::::::::::::::::::::::::::::::::::::::::::::
:: Sets the "java.home" System property at runtime
:::::::::::::::::::::::::::::::::::::::::::::::::::::::::
SET JAVA_HOME=C:\JAVA\jdk1.7.0_79\jre

:::::::::::::::::::::::::::::::::::::::::::::::::::::::::
:: Set the common environment
:::::::::::::::::::::::::::::::::::::::::::::::::::::::::
call ..\..\common\common_env.bat

:::::::::::::::::::::::::::::::::::::::::::::::::::::::::
:: Set the runtime path
:::::::::::::::::::::::::::::::::::::::::::::::::::::::::
SET PATH=.;%JAVA_HOME%\bin;%PATH%;

:::::::::::::::::::::::::::::::::::::::::::::::::::::::::
:: Sets the "java.class.path" System property at runtime
::
:: Sets the pos/bin/ dir and the pos/ dir for finding
:: properties and config files. Inherit the CLASSPATH 
:: for IBM and NCR as their installers set the system
:: wide CLASSPATH for their JPOS environment.
:::::::::::::::::::::::::::::::::::::::::::::::::::::::::
SET CLASSPATH=.;..;%CLASSPATH%

:::::::::::::::::::::::::::::::::::::::::::::::::::::::::
::   Set POS extensions directories
:::::::::::::::::::::::::::::::::::::::::::::::::::::::::
SET EXT_DIRS=%JAVA_HOME%\lib\ext
SET EXT_DIRS=%EXT_DIRS%;%COMMON_PATH%\lib\ext;%COMMON_PATH%\lib\ext\weblogic

:::::::::::::::::::::::::::::::::::::::::::::::::::::::::
::   Set POS patches directory
::
:: Using the addtoclasspath script, any directory
:: (p001-p999) will be included in the classpath. Also,
:: all jars in the (p00-1-p999) will be included
:::::::::::::::::::::::::::::::::::::::::::::::::::::::::
call addtoclasspath.bat ..\patches
call addtoclasspath.bat ..\3rdParty

:::::::::::::::::::::::::::::::::::::::::::::::::::::::::
::   Set config directory
:::::::::::::::::::::::::::::::::::::::::::::::::::::::::
call addtoclasspath.bat ..\config

:::::::::::::::::::::::::::::::::::::::::::::::::::::::::
::   Set pos lib directory and locale
:::::::::::::::::::::::::::::::::::::::::::::::::::::::::
SET CLASSPATH=%CLASSPATH%;..\lib
SET CLASSPATH=%CLASSPATH%;..\lib\max.stores.pos.jar
SET CLASSPATH=%CLASSPATH%;..\lib\oracle.stores.pos.jar
SET CLASSPATH=%CLASSPATH%;..\lib\oracle.stores.simjpos.jar
SET CLASSPATH=%CLASSPATH%;..\lib\locales
SET CLASSPATH=%CLASSPATH%;..\lib\locales\auditlog_resource_en.jar
SET CLASSPATH=%CLASSPATH%;..\lib\locales\maxen.jar
SET CLASSPATH=%CLASSPATH%;..\lib\locales\en.jar

:::::::::::::::::::::::::::::::::::::::::::::::::::::::::
::   Set jpos lib classes
:::::::::::::::::::::::::::::::::::::::::::::::::::::::::
SET CLASSPATH=%CLASSPATH%;C:\installer\oracle\jpos113.jar

:::::::::::::::::::::::::::::::::::::::::::::::::::::::::
::   Set common lib directory
:::::::::::::::::::::::::::::::::::::::::::::::::::::::::
:: Rev 1.1 added by Ashish :start
SET CLASSPATH=%CLASSPATH%;%COMMON_PATH%\lib\ext\EmployeeDiscount\EmployeeDiscountPOS.jar
SET CLASSPATH=%CLASSPATH%;%COMMON_PATH%\lib\ext\EmployeeDiscount\retail-public-payload-java-beans-14.1.0.jar
SET CLASSPATH=%CLASSPATH%;%COMMON_PATH%\lib\ext\EmployeeDiscount\retail-public-payload-java-beans-base-14.1.0.jar
:: Rev 1.1 added by Ashish :end
:: Rev 1.2 added by Ashish :start
SET CLASSPATH=%CLASSPATH%;%COMMON_PATH%\lib\ext\StoreCredit\StoreCreditPOS.jar
:: Rev 1.2 added by Ashish :end
SET CLASSPATH=%CLASSPATH%;C:\qwikcilver\lib\svpos-giftcard-api.jar
SET CLASSPATH=%CLASSPATH%;C:\qwikcilver\lib\common360.jar
SET CLASSPATH=%CLASSPATH%;C:\qwikcilver\lib\AbsoluteLayout.jar
SET CLASSPATH=%CLASSPATH%;C:\qwikcilver\lib\commons-codec-1.4.jar
SET CLASSPATH=%CLASSPATH%;C:\qwikcilver\lib\commons-httpclient-3.1.jar
SET CLASSPATH=%CLASSPATH%;C:\qwikcilver\lib\commons-logging.jar
SET CLASSPATH=%CLASSPATH%;C:\qwikcilver\lib\svpos-giftcard-api.jar
SET CLASSPATH=%CLASSPATH%;C:\qwikcilver\lib\log4j-1.2.15.jar
SET CLASSPATH=%CLASSPATH%;C:\qwikcilver\lib\swing-layout-1.0.4.jar
SET CLASSPATH=%CLASSPATH%;C:\qwikcilver\lib\switch-loyalty-api.jar
SET CLASSPATH=%CLASSPATH%;C:\qwikcilver\lib\webpos-giftcard-api.jar
SET CLASSPATH=%CLASSPATH%;C:\qwikcilver\lib\webpos-loyalty-api.jar
SET CLASSPATH=%CLASSPATH%;C:\qwikcilver\lib\maxTerminalIDImpl.jar
SET CLASSPATH=%CLASSPATH%;C:\qwikcilver\lib\svpos-core-api.jar
SET CLASSPATH=%CLASSPATH%;C:\qwikcilver\lib\xstream-1.1.jar
SET CLASSPATH=%CLASSPATH%;%COMMON_PATH%\lib
SET CLASSPATH=%CLASSPATH%;%COMMON_PATH%\lib\max.stores.exportfile.jar
SET CLASSPATH=%CLASSPATH%;%COMMON_PATH%\lib\max.stores.common.jar
SET CLASSPATH=%CLASSPATH%;%COMMON_PATH%\lib\max.stores.domain.jar
SET CLASSPATH=%CLASSPATH%;%COMMON_PATH%\lib\max.stores.utility.jar
SET CLASSPATH=%CLASSPATH%;%COMMON_PATH%\lib\javax.ejb.jar
SET CLASSPATH=%CLASSPATH%;%COMMON_PATH%\lib\javax.jms.jar
SET CLASSPATH=%CLASSPATH%;%COMMON_PATH%\lib\javax.resource.jar
SET CLASSPATH=%CLASSPATH%;%COMMON_PATH%\lib\javax.servlet.jar
SET CLASSPATH=%CLASSPATH%;%COMMON_PATH%\lib\javax.servlet.jsp.jar
SET CLASSPATH=%CLASSPATH%;%COMMON_PATH%\lib\oracle.stores.commext.jar
SET CLASSPATH=%CLASSPATH%;%COMMON_PATH%\lib\oracle.stores.common.jar
SET CLASSPATH=%CLASSPATH%;%COMMON_PATH%\lib\oracle.stores.crosschannel.jar
SET CLASSPATH=%CLASSPATH%;%COMMON_PATH%\lib\oracle.stores.domain.jar
SET CLASSPATH=%CLASSPATH%;%COMMON_PATH%\lib\oracle.stores.encryptionclient.jar
SET CLASSPATH=%CLASSPATH%;%COMMON_PATH%\lib\oracle.stores.entities.jar
SET CLASSPATH=%CLASSPATH%;%COMMON_PATH%\lib\oracle.stores.foundation.jar
SET CLASSPATH=%CLASSPATH%;%COMMON_PATH%\lib\oracle.stores.jpafoundation.jar
SET CLASSPATH=%CLASSPATH%;%COMMON_PATH%\lib\oracle.stores.receipts.jar
SET CLASSPATH=%CLASSPATH%;%COMMON_PATH%\lib\oracle.stores.secure.utility.jar
SET CLASSPATH=%CLASSPATH%;%COMMON_PATH%\lib\oracle.stores.services.jar
SET CLASSPATH=%CLASSPATH%;%COMMON_PATH%\lib\oracle.stores.simkeystore.jar
SET CLASSPATH=%CLASSPATH%;%COMMON_PATH%\lib\oracle.stores.utility.jar
SET CLASSPATH=%CLASSPATH%;%COMMON_PATH%\lib\jdic.jar

SET CLASSPATH=%CLASSPATH%;C:\capillary_jar\capillary-landmark-pos-api-3.0.jar
SET CLASSPATH=%CLASSPATH%;C:\Paytm\checksum_2.0.0.jar
SET CLASSPATH=%CLASSPATH%;C:\mobikwik\checksum.jar
:: Rev 1.3 added by Ashish :start
SET CLASSPATH=%CLASSPATH%;C:\capillary_jar\capillary-dvs-1.2_10th_Aug_2016.jar
:: Rev 1.3 added by Ashish :end


::SET CLASSPATH=%CLASSPATH%;C:\JPOS_Win1723h_x64\JPOS_Win1723h_x64\posiflex\jpos172.jar
::SET CLASSPATH=%CLASSPATH%;C:\JPOS_Win1723h_x64\JPOS_Win1723h_x64\posiflex\xercesImpl.jar
::SET CLASSPATH=%CLASSPATH%;C:\JPOS_Win1723h_x64\JPOS_Win1723h_x64\posiflex\xml-apis.jar

::SET CLASSPATH=%CLASSPATH%;C:\JPOS_Win1723h_x64\JPOS_Win1723h_x64\posiflex\posiflex.jar
::SET CLASSPATH=%CLASSPATH%;C:\JPOS_Win1723h_x64\JPOS_Win1723h_x64\posiflex\jpos17.jar
::SET CLASSPATH=%CLASSPATH%;C:\JPOS_Win1723h_x64\JPOS_Win1723h_x64\posiflex\jpos17-controls.jar
::SET CLASSPATH=%CLASSPATH%;C:\JPOS_Win1723h_x64\JPOS_Win1723h_x64\posiflex\jcl.jar
::SET CLASSPATH=%CLASSPATH%;C:\JPOS_Win1723h_x64\JPOS_Win1723h_x64\posiflex\jcl_editor.jar
::SET CLASSPATH=%CLASSPATH%;C:\JPOS_Win1723h_x64\JPOS_Win1723h_x64\posiflex\xerces.jar
::SET CLASSPATH=%CLASSPATH%;C:\JPOS_Win1723h_x64\JPOS_Win1723h_x64\posiflex\comm.jar
::SET CLASSPATH=%CLASSPATH%;C:\JPOS_Win1723h_x64\JPOS_Win1723h_x64\posiflex\Serialio.jar

::SET CLASSPATH=%CLASSPATH%;C:\Program Files (x86)\JavaPOS\StdJavaPOS1.90\installJars.jar
::SET CLASSPATH=%CLASSPATH%;C:\Users\test\Desktop\java7jar\jai_codec.jar
::SET CLASSPATH=%CLASSPATH%;C:\Users\test\Desktop\java7jar\jai_core.jar
::SET CLASSPATH=%CLASSPATH%;C:\Users\test\Desktop\java7jar\jcl.jar
::SET CLASSPATH=%CLASSPATH%;C:\Users\test\Desktop\java7jar\jpos110.jar
::SET CLASSPATH=%CLASSPATH%;C:\Users\test\Desktop\java7jar\jpos110-controls.jar
::SET CLASSPATH=%CLASSPATH%;C:\Users\test\Desktop\java7jar\Sample\lkjpos.jar
::SET CLASSPATH=%CLASSPATH%;C:\Program Files (x86)\JavaPOS\StdJavaPOS1.90\ReceiptTest.jar
::SET CLASSPATH=%CLASSPATH%;C:\Users\test\Desktop\java7jar\xercesImpl.jar
::SET CLASSPATH=%CLASSPATH%;C:\Users\test\Desktop\java7jar\xml-apis.jar

:::::::::::::::::::::::::::::::::::::::::::::::::::::::::
::   DigitalPersona fingerprint classes
:::::::::::::::::::::::::::::::::::::::::::::::::::::::::
SET CLASSPATH=C:\installer\ORPOS-14.1.2\;%CLASSPATH




::honeywell Scanner Classes
::SET CLASSPATH=%CLASSPATH%;C:\JPOS\TestApp\Windows\RXTXcomm.jar
::SET CLASSPATH=%CLASSPATH%;C:\JPOS\TestApp\Windows\honeywelljpos.jar
::SET CLASSPATH=%CLASSPATH%;C:\JPOS\TestApp\Windows\log4j-1.2.15.jar
::SET CLASSPATH=%CLASSPATH%;C:\JPOS\TestApp\Windows\JavaPOSSuite.jar
::SET CLASSPATH=%CLASSPATH%;C:\JPOS\TestApp\Windows\xerces.jar

::honeywell Scanner Classes
SET CLASSPATH=%CLASSPATH%;C:\JPOS\TestApp\Windows\RXTXcomm.jar
SET CLASSPATH=%CLASSPATH%;C:\JPOS\TestApp\Windows\honeywelljpos.jar
SET CLASSPATH=%CLASSPATH%;C:\JPOS\TestApp\Windows\log4j-1.2.15.jar
SET CLASSPATH=%CLASSPATH%;C:\JPOS\TestApp\Windows\JavaPOSSuite.jar
SET CLASSPATH=%CLASSPATH%;C:\JPOS\TestApp\Windows\xerces.jar

:::::::::::::::::::::::::::::::::::::::::::::::::::::::::
:: Sets the "jfxrt.jar" to classpath
:::::::::::::::::::::::::::::::::::::::::::::::::::::::::
SET CLASSPATH=%CLASSPATH%;%JAVAFX_WIN_LOCATION%\jfxrt.jar
SET CLASSPATH=%CLASSPATH%;%JAVAFX_LIN_LOCATION%\jfxrt.jar
SET CLASSPATH=%CLASSPATH%;%JAVA_HOME%\lib\jfxrt.jar

:: pass the classpath to a log file for records
echo %CLASSPATH% > ..\logs\classpath.log
