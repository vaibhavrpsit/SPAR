/* ===========================================================================
* Copyright (c) 2006, 2011, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/ui/beans/AboutBean.java /rgbustores_13.4x_generic_branch/1 2011/05/05 14:06:44 mszekely Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    cgreene   05/28/10 - convert to oracle packaging
 *    cgreene   05/27/10 - convert to oracle packaging
 *    cgreene   05/27/10 - convert to oracle packaging
 *    cgreene   05/26/10 - convert to oracle packaging
 *    cgreene   04/26/10 - XbranchMerge cgreene_tech43 from
 *                         st_rgbustores_techissueseatel_generic_branch
 *    nkgautam  01/29/10 - Modified resource lookup to use ClassLoader instead
 *                         of iterating thru the system classpath.
 *    abondala  01/03/10 - update header date
 *    mahising  02/18/09 - fixed copyright about issue
 *    mahising  02/17/09 - fixed copyright page issue
 *
 * ===========================================================================
 * $Log:
 *  2    360Commerce 1.1         11/16/2006 6:53:48 PM  Keith L. Lesikar
 *       Rebranding update.
 *  1    360Commerce 1.0         11/8/2006 8:53:29 AM   Keith L. Lesikar
 * $
 * ===========================================================================
 */
package oracle.retail.stores.pos.ui.beans;

import java.awt.BorderLayout;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Enumeration;
import java.util.Locale;
import java.util.Properties;
import java.util.StringTokenizer;

import javax.swing.JEditorPane;
import javax.swing.JScrollPane;

import org.apache.log4j.Logger;

import oracle.retail.stores.common.utility.LocaleMap;
import oracle.retail.stores.domain.utility.LocaleConstantsIfc;
import oracle.retail.stores.foundation.manager.gui.UIModelIfc;
import oracle.retail.stores.foundation.manager.ifc.ResourceManagerIfc;
import oracle.retail.stores.foundation.tour.conduit.Dispatcher;
import oracle.retail.stores.foundation.utility.Util;

public class AboutBean extends BaseBeanAdapter
{
	/**
	 *
	 */
	private static final long serialVersionUID = 1L;

	/**
	    revision number
	**/
	public static final String revisionNumber = "$Revision: /rgbustores_13.4x_generic_branch/1 $";

	/** the bean name **/
	protected String beanName = "AboutBean";

	/** The bean model **/
	protected DisplayHtmlBeanModel beanModel = new DisplayHtmlBeanModel();
	/**
	    The logger to which log messages will be sent.
	**/
	protected static Logger logger = Logger.getLogger(oracle.retail.stores.pos.ui.beans.AboutBean.class);
	/*
	 * JEditorPane
	 */
	JEditorPane htmlPane = null;

	Properties fileMapping = null;

	public static final String JAR_FILE = "JAR_FILE";

	public static final String PROTOCOL    = "jar:file:";
    public static final String FILE_PATH    = "config/ui/help/";
    public static final String ABOUT_HTML_TITLE    = "about.html";
    public static final String PROPERTY_FILE = "classpath://config/ui/help/helpscreens.properties";

    //---------------------------------------------------------------------
    /**
       Default class Constructor and initializes its components.
     **/
    //---------------------------------------------------------------------
    public AboutBean()
    {
        super();
        UI_PREFIX = "DisplayBean";
    }
    //---------------------------------------------------------------------
    /**
       Initialize the class and its screen members.
     **/
    //---------------------------------------------------------------------
    public void configure()
    {
        // Intialize the panel
        setName(beanName);
        uiFactory.configureUIComponent(this, UI_PREFIX);
        setLayout(new BorderLayout());

        htmlPane = new JEditorPane();
        add(htmlPane,"Center");
    }
    //---------------------------------------------------------------------
    /**
       Gets the POSBeanModel associated with this bean.
       @return the POSBaseBeanModel associated with this bean
   **/
    //---------------------------------------------------------------------
    public POSBaseBeanModel getPOSBaseBeanModel()
    {
        return beanModel;
    }
    //---------------------------------------------------------------------
    /**
       Activate this screen and listeners.
    **/
    //---------------------------------------------------------------------
    public void activate()
    {
        super.activate();
        // call commented out, extract html from locales jar
        setDisplayURL();
    }
    //---------------------------------------------------------------------
    /**
       Deactivate this screen and listeners.
    **/
    //---------------------------------------------------------------------
    public void deactivate()
    {
        super.deactivate();
    }

    //---------------------------------------------------------------------
    /**
       Set the URL to be displayed as per the User Interface Locale.
       Sets the HelpSet that calls individual Help Topics and activates
       the Active Screen in the Table of contents of the JavaHelp Help Viewer.
       <P>
       @return none
     **/
    //---------------------------------------------------------------------
    public void setDisplayURL(URL value)
    {
    	// extract URL from locales jar
    	String url = "C:/temp/temp.html";
    	try {
    	  JEditorPane htmlPane = new JEditorPane(url);
    	  htmlPane.setEditable(false);
    	  // someWindow.add(new JScrollPane(htmlPane);
    	} catch(IOException ioe) {
    	  System.err.println("Error displaying " + url);
    	}

    }
    //---------------------------------------------------------------------
    /**
       Sets the current model to the updated model..
       @param UIModelIfc A DisplayTextBeanModel
       @see oracle.retail.stores.pos.ui.beans.DisplayTextBeanModel
    **/
    //---------------------------------------------------------------------
    public void setModel(UIModelIfc model)
    {
        if(model == null)
        {
            throw new NullPointerException("Attempt to set DisplayTextBean model to null.");
        }
        else
        if(model instanceof DisplayHtmlBeanModel)
        {
            beanModel = (DisplayHtmlBeanModel)model;
            updateBean();
        }
    }
    //---------------------------------------------------------------------
    /**
       Update the bean with fresh data
     * @throws MalformedURLException
    **/
    //---------------------------------------------------------------------
    protected void updateBean()
    {
    }

    //---------------------------------------------------------------------
    /**
       Set the focus for the screen.
    **/
    //---------------------------------------------------------------------
    public void setVisible(boolean aFlag)
    {
        super.setVisible(aFlag);
    }
    //---------------------------------------------------------------------
    public void setDisplayURL()
    {
    	readFileMap();

        URL returnURL = null;
        try
        {
            String jarFileName = getJarName(LocaleConstantsIfc.USER_INTERFACE);
            URL jarURL = getResourceURL(jarFileName);

            // Make sure that a jar exist for this locale, if it does not exist
            // replace with the default language jar.
            if (jarURL == null)
            {
                jarFileName = getJarName(LocaleConstantsIfc.DEFAULT_LOCALE);
                jarURL = getResourceURL(jarFileName);

                // leave null if default locale jar is not available
                if (jarURL != null)
                {
                    returnURL = jarURL;
                }
            }
            else
            {
                returnURL = jarURL;
            }

        }
        catch(Exception e)
        {
            logger.error( "Malformed URL in beanmodel: ");
        }
    	// extract URL from locales jar
    	// String url = "file:///" + "/about.html";
    	try
    	{
    	  htmlPane = new JEditorPane(returnURL);
    	  htmlPane.setEditable(false);
    	  add(new JScrollPane(htmlPane));
    	  htmlPane.setPage(returnURL);
    	}
    	catch(IOException ioe)
    	{
    	  System.err.println("Malformed URL");
    	}
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
        Enumeration<URL> urlEnumeration = classLoader.getResources(FILE_PATH + ABOUT_HTML_TITLE);
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

    protected String getJarName(String localeStr)
    {
        Locale locale = LocaleMap.getBestMatch(LocaleMap.getLocale(localeStr));


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

    protected String getJarURL(String jarName)
    {
        // get java class path
        String classPath = System.getProperty("java.class.path");
        String classPathSeparator = System.getProperty("path.separator");
        StringTokenizer st = new StringTokenizer(classPath, classPathSeparator);
        String jarURL = null;
        String classPathElement = "";
        while (st.hasMoreTokens())
        {
            classPathElement = st.nextToken();
            if (classPathElement.endsWith(jarName))
            {
                jarURL = classPathElement;
                // compare the jar name with local jar name
                if (!jarURL.endsWith("_" + jarName))
                {
                    break;
                }
            }
        }
        return jarURL;
    }

    /**
     * Builds the name of the jar containing the help files based on the locale
       @param jarURL the jarfile and path
       @returns String the path name of the help preferences.
    **/
    //---------------------------------------------------------------------
    protected String getURLString(String jarURL)
    {
        if(PROTOCOL.endsWith(":") && jarURL.startsWith(":"))
        {

            return (PROTOCOL + jarURL.substring(1) + "!/" + FILE_PATH + ABOUT_HTML_TITLE);
        }
        else
        {
            return (PROTOCOL + jarURL + "!/" + FILE_PATH + ABOUT_HTML_TITLE);
        }
    }
}
