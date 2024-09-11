/* ===========================================================================
* Copyright (c) 2004, 2011, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/ui/behavior/NoBrowserHandler.java /rgbustores_13.4x_generic_branch/1 2011/05/05 14:07:00 mszekely Exp $
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
 *    3    360Commerce 1.2         3/31/2005 4:29:08 PM   Robert Pearse   
 *    2    360Commerce 1.1         3/10/2005 10:23:40 AM  Robert Pearse   
 *    1    360Commerce 1.0         2/11/2005 12:12:45 PM  Robert Pearse   
 *
 *   Revision 1.1.2.1  2004/10/18 19:34:37  jdeleau
 *   @scr 7291 Integrate ibV6 and remove Ib5 from installation procedure
 *
 * ===========================================================================
 */
package oracle.retail.stores.pos.ui.behavior;

import java.awt.event.ActionEvent;
import java.beans.PropertyChangeEvent;

import org.w3c.dom.events.Event;

import oracle.retail.stores.pos.ui.beans.OnlineOfficeBean;

/**
 * All methods do nothing, this is just a handler that doesnt know how to handle anything.
 * This is used in the case where no browsers are installed in the user's environment.
 * $Revision: /rgbustores_13.4x_generic_branch/1 $
 */
public class NoBrowserHandler implements ThirdPartyBrowserHandlerIfc
{
    OnlineOfficeBean bean;
    /**
     * Constructor
     *  
     * @param bean
     */
    public NoBrowserHandler(OnlineOfficeBean bean)
    {
        setOnlineOfficeBean(bean);
    }

    /**
     *  
     * @return
     * @see oracle.retail.stores.pos.ui.behavior.ThirdPartyBrowserHandlerIfc#getOnlineOfficeBean()
     */
    public OnlineOfficeBean getOnlineOfficeBean()
    {
        return bean;
    }

    /**
     *  
     * @param bean
     * @see oracle.retail.stores.pos.ui.behavior.ThirdPartyBrowserHandlerIfc#setOnlineOfficeBean(oracle.retail.stores.pos.ui.beans.OnlineOfficeBean)
     */
    public void setOnlineOfficeBean(OnlineOfficeBean bean)
    {
        this.bean = bean;
    }

    /**
     *  
     * 
     * @see oracle.retail.stores.pos.ui.behavior.ThirdPartyBrowserHandlerIfc#activate()
     */
    public void activate()
    {
    }

    /**
     *  
     * 
     * @see oracle.retail.stores.pos.ui.behavior.ThirdPartyBrowserHandlerIfc#deactivate()
     */
    public void deactivate()
    {
    }

    /**
     *  
     * @param e
     * @see oracle.retail.stores.pos.ui.behavior.ThirdPartyBrowserHandlerIfc#handleEvent(org.w3c.dom.events.Event)
     */
    public void handleEvent(Event e)
    {
    }

    /**
     *  
     * @param visible
     * @see oracle.retail.stores.pos.ui.behavior.ThirdPartyBrowserHandlerIfc#setVisible(boolean)
     */
    public void setVisible(boolean visible)
    {
    }

    /**
     *  
     * @param evt
     * @see oracle.retail.stores.pos.ui.behavior.ThirdPartyBrowserHandlerIfc#propertyChange(java.beans.PropertyChangeEvent)
     */
    public void propertyChange(PropertyChangeEvent evt)
    {
    }

    /**
     *  
     * @param evt
     * @see oracle.retail.stores.pos.ui.behavior.ThirdPartyBrowserHandlerIfc#actionPerformed(java.awt.event.ActionEvent)
     */
    public void actionPerformed(ActionEvent evt)
    {
    }

    /**
     *  
     * 
     * @see oracle.retail.stores.pos.ui.behavior.ThirdPartyBrowserHandlerIfc#configureButtons()
     */
    public void configureButtons()
    {
    }

    /**
     *  
     * @return false - No browser is installed
     * @see oracle.retail.stores.pos.ui.behavior.ThirdPartyBrowserHandlerIfc#isInstalled()
     */
    public boolean isInstalled()
    {
        return false;
    }

    /**
     *  
     * 
     * @see oracle.retail.stores.pos.ui.behavior.ThirdPartyBrowserHandlerIfc#enableCookies()
     */
    public void enableCookies()
    {
    }
}
