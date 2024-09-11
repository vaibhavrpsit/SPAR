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
SET JAVA_HOME=C:\installer\JAVA\jdk1.7.0_79\jre

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
SET CLASSPATH=.;..;

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
SET CLASSPATH=%CLASSPATH%;%COMMON_PATH%\lib\ext\giftcard\svpos-giftcard-api.jar
SET CLASSPATH=%CLASSPATH%;%COMMON_PATH%\lib\ext\giftcard\switch-loyalty-api.jar
SET CLASSPATH=%CLASSPATH%;%COMMON_PATH%\lib\ext\qwikcilver\AbsoluteLayout.jar
SET CLASSPATH=%CLASSPATH%;%COMMON_PATH%\lib\ext\qwikcilver\commons-codec-1.4.jar
SET CLASSPATH=%CLASSPATH%;%COMMON_PATH%\lib\ext\qwikcilver\commons-httpclient-3.1.jar
SET CLASSPATH=%CLASSPATH%;%COMMON_PATH%\lib\ext\qwikcilver\commons-logging.jar
SET CLASSPATH=%CLASSPATH%;%COMMON_PATH%\lib\ext\qwikcilver\svpos-giftcard-api.jar
SET CLASSPATH=%CLASSPATH%;%COMMON_PATH%\lib\ext\qwikcilver\log4j-1.2.15.jar
SET CLASSPATH=%CLASSPATH%;%COMMON_PATH%\lib\ext\qwikcilver\swing-layout-1.0.4.jar
SET CLASSPATH=%CLASSPATH%;%COMMON_PATH%\lib\ext\qwikcilver\switch-loyalty-api.jar
SET CLASSPATH=%CLASSPATH%;%COMMON_PATH%\lib\ext\qwikcilver\webpos-giftcard-api.jar
SET CLASSPATH=%CLASSPATH%;%COMMON_PATH%\lib\ext\qwikcilver\webpos-loyalty-api.jar
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

SET CLASSPATH=%CLASSPATH%;C:\capillary_jar\capillary-landmark-pos-api-2.3.jar


:::::::::::::::::::::::::::::::::::::::::::::::::::::::::
::   DigitalPersona fingerprint classes
:::::::::::::::::::::::::::::::::::::::::::::::::::::::::
SET CLASSPATH=C:\installer\ORPOS-14.1.2\;%CLASSPATH%

:::::::::::::::::::::::::::::::::::::::::::::::::::::::::
:: Sets the "jfxrt.jar" to classpath
:::::::::::::::::::::::::::::::::::::::::::::::::::::::::
SET CLASSPATH=%CLASSPATH%;%JAVAFX_WIN_LOCATION%\jfxrt.jar
SET CLASSPATH=%CLASSPATH%;%JAVAFX_LIN_LOCATION%\jfxrt.jar
SET CLASSPATH=%CLASSPATH%;%JAVA_HOME%\lib\jfxrt.jar

:: pass the classpath to a log file for records
echo %CLASSPATH% > ..\logs\classpath.log
