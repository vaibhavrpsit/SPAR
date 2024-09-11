/* ===========================================================================
* Copyright (c) 1998, 2011, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/ui/beans/DisplayHtmlBeanModel.java /main/20 2011/12/05 12:16:23 cgreene Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    cgreene   12/05/11 - updated from deprecated packages and used more
 *                         bigdecimal constants
 *    cgreene   05/28/10 - convert to oracle packaging
 *    cgreene   05/27/10 - convert to oracle packaging
 *    cgreene   05/27/10 - convert to oracle packaging
 *    cgreene   05/26/10 - convert to oracle packaging
 *    cgreene   04/26/10 - XbranchMerge cgreene_tech43 from
 *                         st_rgbustores_techissueseatel_generic_branch
 *    cgreene   04/02/10 - remove deprecated LocaleContantsIfc and currencies
 *    asinton   01/07/10 - Modified resource lookup to use ClassLoader instead
 *                         of iterating thru the system classpath.
 *    abondala  01/03/10 - update header date
 *    mchellap  03/10/09 - Client hangs when trying to display online help
 *    sswamygo  02/20/09 - Modified to get BestMatch locale.
 *
 * ===========================================================================
 * $Log:
 *    3    360Commerce 1.2         3/31/2005 4:27:48 PM   Robert Pearse
 *    2    360Commerce 1.1         3/10/2005 10:21:03 AM  Robert Pearse
 *    1    360Commerce 1.0         2/11/2005 12:10:39 PM  Robert Pearse
 *
 *   Revision 1.6.2.2  2004/12/02 22:52:37  jdeleau
 *   @scr 7730 Correct getJarURL function to solve a problem with 4690 classpath extraction.
 *
 *   Revision 1.6.2.1  2004/11/16 17:48:05  rsachdeva
 *   @scr 7730 File Separator Used for file name search
 *
 *   Revision 1.6  2004/09/09 21:27:09  jdeleau
 *   @scr 6969 Fix the help file references
 *
 *   Revision 1.5  2004/04/09 16:56:00  cdb
 *   @scr 4302 Removed double semicolon warnings.
 *
 *   Revision 1.4  2004/03/16 17:15:22  build
 *   Forcing head revision
 *
 *   Revision 1.3  2004/03/16 17:15:17  build
 *   Forcing head revision
 *
 *   Revision 1.2  2004/02/11 20:56:27  rhafernik
 *   @scr 0 Log4J conversion and code cleanup
 *
 *   Revision 1.1.1.1  2004/02/11 01:04:22  cschellenger
 *   updating to pvcs 360store-current
 *
 *
 *
 *    Rev 1.0   Aug 29 2003 16:10:12   CSchellenger
 * Initial revision.
 *
 *    Rev 1.5   Jun 20 2003 17:11:26   baa
 * fix lock up when help is not available
 *
 *    Rev 1.4   Jun 19 2003 18:45:22   baa
 * move help files to locale bundle
 *
 *    Rev 1.3   Apr 11 2003 17:28:26   baa
 * partial fix for moving  help files
 * Resolution for POS SCR-2152: Organize locale sensitive Files
 *
 *    Rev 1.2   Mar 06 2003 10:00:28   RSachdeva
 * clean up
 * Resolution for POS SCR-1848: Help Support
 *
 *    Rev 1.1   Dec 10 2002 13:32:42   RSachdeva
 * JavaHelp Implementation and Internationalization Support
 * Resolution for POS SCR-1848: Help Support
 *
 *    Rev 1.0   Apr 29 2002 14:53:46   msg
 * Initial revision.
 *
 *    Rev 1.0   Mar 18 2002 11:54:54   msg
 * Initial revision.
 *
 *    Rev 1.6   05 Feb 2002 17:00:44   jbp
 * modify for Linux
 * Resolution for POS SCR-902: Selecting Help on Linux Beetle causes beep
 *
 *    Rev 1.5   31 Jan 2002 17:24:36   jbp
 * use default url if screen is not mapped
 * Resolution for POS SCR-888: Selecting Help on Register Status screen causes beep
 *
 *    Rev 1.4   Jan 19 2002 10:29:56   mpm
 * Initial implementation of pluggable-look-and-feel user interface.
 * Resolution for POS SCR-798: Implement pluggable-look-and-feel user interface
 *
 *    Rev 1.3   09 Nov 2001 16:32:30   jbp
 * gets htm files from jar
 * Resolution for POS SCR-211: HTML Help Functionality
 *
 *    Rev 1.1   12 Oct 2001 13:56:58   jbp
 * changed dir name for html files
 * Resolution for POS SCR-211: HTML Help Functionality
 *
 *    Rev 1.0   12 Oct 2001 11:56:18   jbp
 * Initial revision.
 * Resolution for POS SCR-211: HTML Help Functionality
 * ===========================================================================
 */
package oracle.retail.stores.pos.ui.beans;

// java imports
import java.io.IOException;
import java.net.URL;
import java.util.Enumeration;
import java.util.Locale;
import java.util.Properties;

import org.apache.log4j.Logger;

import oracle.retail.stores.domain.utility.LocaleConstantsIfc;
import oracle.retail.stores.foundation.manager.ifc.ResourceManagerIfc;
import oracle.retail.stores.foundation.tour.conduit.Dispatcher;
import oracle.retail.stores.common.utility.LocaleMap;
import oracle.retail.stores.foundation.utility.Util;

//--------------------------------------------------------------------------
/**
    This is the bean model used by the DisplayTextBean. Contains the transaction
    text to display. <P>

 **/
//--------------------------------------------------------------------------
public class DisplayHtmlBeanModel extends DialogBeanModel
{
    /**
     * serialVersionUID
     */
    private static final long serialVersionUID = 3232443578181430538L;
    /**
        Transaction text to display
    **/
    URL fieldDisplayText;
    /**
        Help Properties File Location
    **/
    public static final String PROPERTY_FILE = "classpath://config/ui/help/helpscreens.properties";
    /**
        Properties file mappings
    **/
    Properties fileMapping = null;

    /**
        Jar file located in the properties file
    **/
    public static final String JAR_FILE = "JAR_FILE";

    /**
        Default help screen in the properties file
    **/
    public static final String DEFAULT_HELP_SCREEN = "DEFAULT_HELP_SCREEN";

    public static final String PROTOCOL    = "jar:file:";
    public static final String FILE_PATH    = "config/ui/help/";
    public static final String HELP_SET_FILE = "preferences.hs";

    /**
        The logger to which log messages will be sent.
    **/
    protected static Logger logger = Logger.getLogger(oracle.retail.stores.pos.ui.beans.DisplayHtmlBeanModel.class);
    /**
      Screen Name that needs to be active in the JavaHelp Table Of Contents
      as per the Screen from which Help was clicked
    **/
    private  String activeScreen = null;

    //--------------------------------------------------------------------------
    /**
       DisplayTextBeanModel default constructor <P>
     **/
    //--------------------------------------------------------------------------
    public DisplayHtmlBeanModel()
    {
        super();
    }

    //---------------------------------------------------------------------
    /**
       Retrieves transaction display text. <P>
       @return the URL.
    **/
    //---------------------------------------------------------------------
    public URL getURLForScreen(String screenname)
    {
        String activeScreenForHelp = screenname;
        //if properties null, get properties
        if (fileMapping == null)
        {
            readFileMap();
        }

        //The URL used by the Help Viewer.
        URL returnURL = null;
        try
        {
            String jarFileName = getJarName(LocaleConstantsIfc.USER_INTERFACE);
            String fileName = (fileMapping.getProperty(screenname));

            if(fileName == null)
            {
                activeScreenForHelp = DEFAULT_HELP_SCREEN;
            }
            setScreenName(activeScreenForHelp);

            returnURL = getResourceURL(jarFileName);

            // Make sure that a jar exist for this locale, if it does not exist
            // replace with the default language jar.
            if(returnURL == null)
            {
                returnURL = getResourceURL(getJarName(LocaleConstantsIfc.DEFAULT_LOCALE));
            }
        }
        catch(Exception e)
        {
            logger.error( "Malformed URL in beanmodel: ");
        }
        return returnURL;
    }

    /**
     * Returns the URL for the FILE_PATH + HELP_SET_FILE.
     * @param localeJarFilename
     * @return
     * @throws IOException
     */
    protected URL getResourceURL(String localeJarFilename) throws IOException
    {
        URL returnURL = null;
        ClassLoader classLoader = ClassLoader.getSystemClassLoader();
        Enumeration<URL> urlEnumeration = classLoader.getResources(FILE_PATH + HELP_SET_FILE);
        boolean done = false;
        URL url = null;
        while (!done && urlEnumeration.hasMoreElements())
        {
            url = urlEnumeration.nextElement();
            if(url.toString().contains(localeJarFilename))
            {
                returnURL = url;
                done = true;
            }
        }
        return returnURL;
    }

    //---------------------------------------------------------------------
    /**
     * Builds the name of the jar containing the help files based on the locale
       @param localeStr the locale string
       @returns String the name of the jar.
    **/
    //---------------------------------------------------------------------
    protected String getJarName(String localeStr)
    {
        Locale locale = LocaleMap.getLocale(localeStr);
        locale = LocaleMap.getBestMatch(locale);
        String jarPrefix = fileMapping.getProperty(JAR_FILE);
        String jarFileName = locale.toString() + ".jar";

        if (!Util.isEmpty(jarPrefix))
        {
            jarFileName = jarPrefix + jarFileName;
        }
        return jarFileName;
    }


    //---------------------------------------------------------------------
    /**
       Reads file into the map.
    **/
    //---------------------------------------------------------------------
    public void readFileMap()
    {
        // Read the properties file
        Dispatcher dispatcher = Dispatcher.getDispatcher();
        ResourceManagerIfc resourceManager = (ResourceManagerIfc)
        dispatcher.getManager(ResourceManagerIfc.TYPE);
        fileMapping = resourceManager.getProperties(PROPERTY_FILE);
    }

    //---------------------------------------------------------------------
    /**
       Retrieves transaction display text. <P>
       @return the URL.
    **/
    //---------------------------------------------------------------------
    public URL getDisplayURL()
    {                                   // begin getDisplayText()
        return(fieldDisplayText);
    }                                   // end getDisplayText()

    //---------------------------------------------------------------------
    /**
       Sets transaction display text. <P>
       @param URL  transaction display text
    **/
    //---------------------------------------------------------------------
    public void setDisplayURL(URL displayText)
    {                                   // begin setDisplayText()
        fieldDisplayText = displayText;
    }                                  // end getDisplayText()
    //---------------------------------------------------------------------
    /**
       To get the screen name to be active in JavaHelp Table of Contents<P>
       @param activeScreenForHelp  Screen Name that has been Identified to be active.
    **/
    //---------------------------------------------------------------------
    public void setScreenName(String activeScreenForHelp)
    {
        activeScreen = activeScreenForHelp;
    }
    //---------------------------------------------------------------------
    /**
       To get the screen name to be active in JavaHelp Table of Contents<P>

    **/
    //---------------------------------------------------------------------
    public String getScreenName()
    {
        return activeScreen;
    }
}
