/* ===========================================================================
* Copyright (c) 2004, 2011, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/ui/behavior/IceBrowserOnlineOfficeHandler.java /rgbustores_13.4x_generic_branch/1 2011/05/05 14:07:00 mszekely Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    cgreene   05/26/10 - convert to oracle packaging
 *    abondala  01/03/10 - update header date
 *
 * ===========================================================================
 * $Log:
 *4    360Commerce 1.3         11/7/2006 11:41:18 AM  Brendan W. Farrell Remove
 *      ICE browser jars, stub out code to fix compile errors.
 *3    360Commerce 1.2         3/31/2005 4:28:21 PM   Robert Pearse   
 *2    360Commerce 1.1         3/10/2005 10:22:03 AM  Robert Pearse   
 *1    360Commerce 1.0         2/11/2005 12:11:22 PM  Robert Pearse   
 *
 Revision 1.1.2.1  2004/10/18 19:34:37  jdeleau
 @scr 7291 Integrate ibV6 and remove Ib5 from installation procedure
 *
 * ===========================================================================
 */
package oracle.retail.stores.pos.ui.behavior;

import java.awt.Container;
import java.awt.event.ActionEvent;
import java.beans.PropertyChangeEvent;

import javax.swing.JLabel;
import javax.swing.JPanel;

import oracle.retail.stores.pos.ui.beans.OnlineOfficeBean;

/**
 * All of the code has been stubbed out with the removal of ice browser jars from POS.
 * This will all need to be rewritten if a customer wants to use this browser as
 * POS no longer supports ICE Browser.
 * $Revision: /rgbustores_13.4x_generic_branch/1 $
 */
public class IceBrowserOnlineOfficeHandler implements ThirdPartyBrowserHandlerIfc
{
    /**
     * Default text to appear in the label, in case I18N
     * lookup fails.
     **/
    protected static final String[] labelText =
    {
            "Requesting information... ",
            "Error loading source... "
    };
    
    /**
     * Name of the properties to lookup for various label text
     **/
    protected static final String[] labelTags =
    {
            "RequestingInformation",
            "ErrorLoadingSource"
    };
    
    /**
     * status label
     **/
    protected static JLabel status;
    
    /**
     * status Panel
     **/
    protected static JPanel statusPanel;
    
    /**
     * Online office bean
     */
    protected OnlineOfficeBean bean;
    
    /**
     * Constructor needs an onlineOfficeBean
     *  
     * @param bean
     */
    public IceBrowserOnlineOfficeHandler(OnlineOfficeBean bean)
    {
        this.bean = bean;
    }
    /**
     * Init method, required by interface
     *  
     * @param base
     * @see ice.storm.ViewportCallback#init(ice.storm.StormBase)
     */
    public void init()
    {
        
    }
    
    /**
     * Required by ice browser callback interface
     *  
     * @param viewPort
     * @return Top Level container
     * @see ice.storm.ViewportCallback#createTopLevelContainer(ice.storm.Viewport)
     */
    public Container createTopLevelContainer()
    {
        return setBrowserBase();
    }
    
    /**
     * Required for ViewportCallbackInterface, not used
     *  
     * @param viewPort
     * @see ice.storm.ViewportCallback#disposeTopLevelContainer(ice.storm.Viewport)
     */
    public void disposeTopLevelContainer() {}
    
    /**
     * Required for ViewportCallbackInterface, not used
     *  
     * @param v
     * @param s
     * @param o
     * @param o2
     * @see ice.storm.ViewportCallback#processViewportMessage(ice.storm.Viewport, java.lang.String, java.lang.Object, java.lang.Object)
     */
    public void processViewportMessage(String s,Object o, Object o2){}
    
    /**
     *  Sets the base browser of this bean; creates a new window if
     *  one already exists.
     *  @param viewPort the current viewport
     *  @return the parent of the online office bean
     */
    protected synchronized Container setBrowserBase()
    {
        return getOnlineOfficeBean().getParent();
    }
    
    /**
    * Handle the property changes within the StormBase
    * @param evt PropertyChangeEvent that caused the callback
    **/
    public void propertyChange(PropertyChangeEvent evt)
    {

    }
    
    /**
     * Get the onlineoffice bean
     *  
     *  @return
     */
    public OnlineOfficeBean getOnlineOfficeBean()
    {
        return this.bean;
    }
    
    /**
     * Set the online office bean
     *  
     * @param bean
     */
    public void setOnlineOfficeBean(OnlineOfficeBean bean)
    {
        this.bean = bean;
    }
    /**
    * Enables the Ice Browser managers for cookies, authentication, and proxy.
    **/
    protected void enableIceManagers()
    {

    }
    /**
    * Activates the bean when it is added to the screen.
    * This is where a bean can "initialize" itself by registering necessary
    * listeners, setting appropriate action states, etc.
    **/
    public void activate()
    {
        bean.addFocusListener(bean);
        
        // Configure Ice Browser managers
        enableIceManagers();
    }
    /**
    * Deactivates the bean when it is removed from the screen.
    * This is where a bean can "free" itself by unregistering necessary
    * listeners, clearing appropriate action states, etc.
    **/
    public void deactivate()
    {
        bean.removeFocusListener(bean);
    }
    
    /**
    * Handles key events from the ice browser
    * @param e the event.
    **/
    public void handleEvent(org.w3c.dom.events.Event e)
    {

    }
    /**
    * Overrides parant setVisible.
    * @param visible true if the object should be visible
    **/
    public void setVisible(boolean visible)
    {

    }
    
    /**
     * No action events are listened to here.
     *  
     * @param evt
     * @see oracle.retail.stores.pos.ui.behavior.ThirdPartyBrowserHandlerIfc#actionPerformed(java.awt.event.ActionEvent)
     */
    public void actionPerformed(ActionEvent evt)
    {
        
    }
    
    /**
     * Does nothing in this implementation
     * 
     * @see oracle.retail.stores.pos.ui.behavior.ThirdPartyBrowserHandlerIfc#configureButtons()
     */
    public void configureButtons()
    {
        getOnlineOfficeBean().configureButtons();
    }
    
    /**
     * Determine if the ICE browser is installed for POS
     *  
     * @return true or false
     * @see oracle.retail.stores.pos.ui.behavior.ThirdPartyBrowserHandlerIfc#isInstalled()
     */
    public boolean isInstalled()
    {
        boolean result = false;
        try
        {
            Class.forName("ice.net.proxy.ProxyManager");
            result = true;
        }
        catch(ClassNotFoundException cnfe)
        {
            result = false;
        }
        return result;
    }
    
    /**
     * Enable cookies - no use in the * online office bean but but subclasses
     * that implement browsers will want to use this.
     * 
     * @see oracle.retail.stores.pos.ui.behavior.ThirdPartyBrowserHandlerIfc#enableCookies()
     */
    public void enableCookies()
    {
    }

}
