/* ===========================================================================
* Copyright (c) 1998, 2011, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/ui/beans/OnlineOfficeBean.java /rgbustores_13.4x_generic_branch/1 2011/05/05 14:06:56 mszekely Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    cgreene   05/27/10 - convert to oracle packaging
 *    cgreene   05/26/10 - convert to oracle packaging
 *    abondala  01/03/10 - update header date
 *
 * ===========================================================================
 * $Log:
 *    3    360Commerce 1.2         3/31/2005 4:29:11 PM   Robert Pearse   
 *    2    360Commerce 1.1         3/10/2005 10:23:46 AM  Robert Pearse   
 *    1    360Commerce 1.0         2/11/2005 12:12:49 PM  Robert Pearse   
 *
 *   Revision 1.3.4.1  2004/10/18 19:34:37  jdeleau
 *   @scr 7291 Integrate ibV6 and remove Ib5 from installation procedure
 *
 *   Revision 1.3  2004/03/16 17:15:18  build
 *   Forcing head revision
 *
 *   Revision 1.2  2004/02/11 20:56:26  rhafernik
 *   @scr 0 Log4J conversion and code cleanup
 *
 *   Revision 1.1.1.1  2004/02/11 01:04:22  cschellenger
 *   updating to pvcs 360store-current
 *
 *
 * 
 *    Rev 1.1   Sep 16 2003 17:52:48   dcobb
 * Migrate to JVM 1.4.1
 * Resolution for 3361: New Feature:  JVM 1.4.1_03 (Windows) Migration
 * 
 *    Rev 1.0   Aug 29 2003 16:11:20   CSchellenger
 * Initial revision.
 * 
 *    Rev 1.4   Jul 08 2003 18:38:58   baa
 * enable focus with tab key
 * Resolution for 2259: Web Store -No focus w/in website does not allow user to navigate w/out mouse
 * 
 *    Rev 1.3   Apr 23 2003 13:07:18   HDyer
 * Set up Ice Browser managers to enable cookies and others.
 * Resolution for POS SCR-2202: Support Ice Browser Protocol Handler and upgrade to Ice Browser 5.4.0
 *
 *    Rev 1.2   Aug 14 2002 18:18:06   baa
 * format currency
 * Resolution for POS SCR-1740: Code base Conversions
 *
 *    Rev 1.1   24 Jul 2002 11:25:04   jbp
 * removed deprecated dependencies
 * Resolution for POS SCR-1761: Icebrowser update
 *
 *    Rev 1.0   Apr 29 2002 14:54:38   msg
 * Initial revision.
 *
 *    Rev 1.0   Mar 18 2002 11:56:28   msg
 * Initial revision.
 *
 *    Rev 1.6   11 Mar 2002 13:29:06   jbp
 * impement ice.storm.ViewportCallback
 * Resolution for POS SCR-1351: New IceBrowser Jar
 *
 *    Rev 1.5   Mar 09 2002 12:25:32   mpm
 * More text externalization.
 * Resolution for POS SCR-351: Internationalization
 *
 *    Rev 1.4   05 Mar 2002 20:01:44   jbp
 * enable buttons for WebStore.
 * Resolution for POS SCR-1351: New IceBrowser Jar
 *
 *    Rev 1.3   19 Feb 2002 12:26:16   jbp
 * changes for new IceBrowser jar
 * Resolution for POS SCR-1351: New IceBrowser Jar
 *
 *    Rev 1.2   Jan 25 2002 07:29:52   mpm
 * Corrected merge failure in new UI integration.
 * Resolution for POS SCR-798: Implement pluggable-look-and-feel user interface
 *
 *    Rev 1.1   Jan 19 2002 10:31:08   mpm
 * Initial implementation of pluggable-look-and-feel user interface.
 * Resolution for POS SCR-798: Implement pluggable-look-and-feel user interface
 *
 *    Rev 1.0   05 Nov 2001 12:12:44   jbp
 * Initial revision.
 * Resolution for POS SCR-217: Combine CrossReach, POS, and OnlineOffice
 *
 *    Rev 1.0   Sep 21 2001 11:34:10   msg
 * Initial revision.
 *
 *    Rev 1.1   Sep 17 2001 13:18:10   msg
 * header update
 * ===========================================================================
 */
package oracle.retail.stores.pos.ui.beans;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import javax.swing.JLabel;

import org.w3c.dom.events.EventListener;

import oracle.retail.stores.foundation.manager.gui.UIModelIfc;
import oracle.retail.stores.foundation.utility.Util;
import oracle.retail.stores.pos.ui.behavior.BrowserStatusListener;
import oracle.retail.stores.pos.ui.behavior.IceBrowserOnlineOfficeHandler;
import oracle.retail.stores.pos.ui.behavior.NoBrowserHandler;
import oracle.retail.stores.pos.ui.behavior.ThirdPartyBrowserHandlerIfc;

/**
*   This class is used to display online office in the POS application.
**/
public class OnlineOfficeBean extends CycleRootPanel

{
    /**
     *   revision number supplied by Team Connection
     */
    public static final String revisionNumber = "$Revision: /rgbustores_13.4x_generic_branch/1 $";
    
    /**
     *   constant for the http protocol
     */
    protected static final String HTTP_PREFIX = "http://";

    /**
     *  home url
     */
    protected String homeUrl = "";
    /**
        Browser Bean Model
    **/
    protected BrowserBeanModel bbModel;
    /**
        Browser Status Listener
    **/
    BrowserStatusListener browserStatusListener = null;

    /**
     * This is the integration point between the third party
     * browser and POS.
     */
    protected ThirdPartyBrowserHandlerIfc handler;
    
    /**
     * This is the event listener, it listens for keyboard
     * events on the ICE browser, other third party browsers
     * may ignore it or use it, as they see fit. Calls are
     * passed to the ThirdPartyBrowserHandlerIfc.
     */
    protected EventListener eventListener;
    
    /**
     * This is the property change listener to use, browsers
     * may use or ignore it as they see fit.  It merely will
     * pass calls to the ThirdPartyBrowserHandlerIfc.
     */
    protected PropertyChangeListener propertyChangeListener;
    
    /**
    *  Class Constructor
    **/
    public OnlineOfficeBean()
    {
        try
        {
            setHandler(new IceBrowserOnlineOfficeHandler(this));
        }
        catch(NoClassDefFoundError err)
        {
            setHandler(new NoBrowserHandler(this));
        }
    }

    /**
    *   This method is used to get the home URL for the web site.
    *   @return String the string value for the home URL
    **/
    public String getHomeUrl()
    {
        return homeUrl;
    }

    /**
    *   This method is used to set the home URL for the web site.
    *   @param value the string value for the url
    **/
    public void setHomeUrl(String value)
    {
        homeUrl = value;
    }

    /**
    *   Activates the bean when it is added to the screen.
    *   This is where a bean can "initialize" itself by registering necessary
    *   listeners, setting appropriate action states, etc.
    **/
    public void activate()
    {
        getHandler().activate();
    }

    /**
    *   Deactivates the bean when it is removed from the screen.
    *   This is where a bean can "free" itself by unregistering necessary
    *   listeners, clearing appropriate action states, etc.
    **/
    public void deactivate()
    {
        getHandler().deactivate();
    }

    /**
    *   Sets the model of this bean.
    *   @param model the model of the bean.
    **/
    public void setModel(UIModelIfc model)
    {
        bbModel = (BrowserBeanModel)model;
        setHomeUrl(bbModel.getHomeUrl());
    }

    /**
    *   Configures the buttons for local navigation of the browser.
    **/
    //---------------------------------------------------------------------
    public void configureButtons()
    {
       // No buttons for online office
    }

    /**
    *  Sets the new status to the status listener
    *  @param newStatus JLabel containing new status
    **/
    public void statusActionPerformed(JLabel newStatus)
    {
        // Useful debugging
        // System.out.println("the action is " + newStatus.getText() + " --> BrowserBean");
        browserStatusListener.setStatus(newStatus);
    }

    /**
     * Return an event listener, this listens for keyboard events on
     * the ICE browser. 
     *  
     * @return
     */
    public EventListener getEventListener()
    {
        if(this.eventListener == null)
        {
            this.eventListener = new DefaultEventListener(getHandler());
        }
        return this.eventListener;
    }
    
    /**
     * Return a property change listener to use with the online office.
     *  
     *  @return propertyChangeListener
     */
    public PropertyChangeListener getPropertyChangeListener()
    {
        if(this.propertyChangeListener == null)
        {
            this.propertyChangeListener = new DefaultPropertyChangeListener(getHandler());
        }
        return this.propertyChangeListener;
    }

    /**
    *   Handles key events from the ice browser
    *   @param e the event.
    **/
    public void handleEvent(org.w3c.dom.events.Event e)
    {
        getHandler().handleEvent(e);
    }

    /**
    *   Adds (actually sets) the status listener on the browser panel.
    *   @param listener the BrowserStatusListener
    **/
    public void addBrowserStatusListener(BrowserStatusListener listener)
    {
        browserStatusListener = listener;
    }

    /**
    *   Removes (actually resets) the browser listner on the browser panel.
    *   @param listener the BrowserStatusListener
    **/
    public void removeBrowserStatusListener(BrowserStatusListener listener)
    {
        browserStatusListener = null;
    }

    /**
    *   Overrides parant setVisible.
    *   @param visible true if the object should be visible
    **/
    public void setVisible(boolean visible)
    {
        super.setVisible(visible);
        if (!(homeUrl.startsWith(HTTP_PREFIX)))
        {
            homeUrl = HTTP_PREFIX + homeUrl;
        }
        getHandler().setVisible(visible);

        // set focus to the browser        
        requestFocusInWindow();
    }

    /**
    *   Method to default display string function. <P>
    *   @return String representation of object
    **/
    public String toString()
    {
        // result string
        String strResult = new String("Class: " + getClass().getName() + " (Revision " +
                                       getRevisionNumber() +
                                       ")" +
                                       hashCode());
        // pass back result
        return(strResult);
    }

    /**
     * Retrieve I18N text from the properties files.  This just passes it through
     * to the superclass, but is public in scope so that the browser specific 
     * handlers can have access to the I18N text.
     *  
     * @param propertyName
     * @param defaultValue
     * @return I18N compliant string
     * @see oracle.retail.stores.pos.ui.beans.BaseBeanAdapter#retrieveText(java.lang.String, java.lang.String)
     */
    public String retrieveText(String propertyName, String defaultValue)
    {
        return super.retrieveText(propertyName, defaultValue);
    }
    
    /**
     * @return Returns the handler.
     */
    public ThirdPartyBrowserHandlerIfc getHandler()
    {
        return handler;
    }
    
    /**
     * @param handler The handler to set.
     */
    public void setHandler(ThirdPartyBrowserHandlerIfc handler)
    {
        this.handler = handler;
    }
    
    /**
    *   Retrieves the Team Connection revision number. <P>
    *   @return String representation of revision number
    **/
    public String getRevisionNumber()
    {
        return(Util.parseRevisionNumber(revisionNumber));
    }
    
    /**
     * Return whether or not a 3rd party browser is 
     * installed on this system.
     *  
     * @return true or false
     */
    public boolean isInstalled()
    {
        return handler.isInstalled();
    }

    /**
     * Default EventListener for this class, uses the proxy class
     * to call its handleEvent method.
     *
     * $Revision: /rgbustores_13.4x_generic_branch/1 $
     */
    private class DefaultEventListener implements EventListener
    {
        /**
         * Handle to the third party browser handler
         */
        private ThirdPartyBrowserHandlerIfc handler;
        
        /**
         * Constructor
         *  
         * @param thirdPartyBrowserHandler
         */
        public DefaultEventListener(ThirdPartyBrowserHandlerIfc thirdPartyBrowserHandler)
        {
            handler = thirdPartyBrowserHandler;
        }
        
        /**
         * Handle a document object model event
         *  
         * @param e
         * @see org.w3c.dom.events.EventListener#handleEvent(org.w3c.dom.events.Event)
         */
        public void handleEvent(org.w3c.dom.events.Event e)
        {
            handler.handleEvent(e);
        }
    }
    
    /**
     * Default PropertyChangeListener for this class, uses the proxy class
     * to call its handleEvent method.
     *
     * $Revision: /rgbustores_13.4x_generic_branch/1 $
     */
    private class DefaultPropertyChangeListener implements PropertyChangeListener
    {
        /**
         * Handle to the third party browser handler
         */
        private ThirdPartyBrowserHandlerIfc handler;
        
        /**
         * Constructor
         *  
         * @param thirdPartyBrowserHandler
         */
        public DefaultPropertyChangeListener(ThirdPartyBrowserHandlerIfc thirdPartyBrowserHandler)
        {
            handler = thirdPartyBrowserHandler;
        }
        
        /**
         * PropertyChangeEvent, this gets propogated to the thirdy party browser
         *  
         * @param evt
         * @see java.beans.PropertyChangeListener#propertyChange(java.beans.PropertyChangeEvent)
         */
        public void propertyChange(PropertyChangeEvent evt)
        {
            handler.propertyChange(evt);
        }
    }

}
