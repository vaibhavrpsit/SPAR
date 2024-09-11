/* ===========================================================================
* Copyright (c) 2004, 2011, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/ui/behavior/IceBrowserHandler.java /rgbustores_13.4x_generic_branch/1 2011/05/05 14:07:00 mszekely Exp $
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
 *    4    360Commerce 1.3         11/7/2006 11:41:18 AM  Brendan W. Farrell
 *         Remove ICE browser jars, stub out code to fix compile errors.
 *    3    360Commerce 1.2         3/31/2005 4:28:21 PM   Robert Pearse   
 *    2    360Commerce 1.1         3/10/2005 10:22:02 AM  Robert Pearse   
 *    1    360Commerce 1.0         2/11/2005 12:11:22 PM  Robert Pearse   
 *
 *   Revision 1.1.2.1  2004/10/18 19:34:37  jdeleau
 *   @scr 7291 Integrate ibV6 and remove Ib5 from installation procedure
 *
 * ===========================================================================
 */
package oracle.retail.stores.pos.ui.behavior;

import java.awt.event.ActionEvent;

import org.w3c.dom.events.Event;

import oracle.retail.stores.pos.ui.beans.BrowserBean;

/**
 * This class handles the ice browser specific integration point with the BrowserBean.
 * 
 * All the code in this class has been stubbed out.  
 * $Revision: /rgbustores_13.4x_generic_branch/1 $
 */
public class IceBrowserHandler extends IceBrowserOnlineOfficeHandler
{
    /**
     * Constructor
     *  
     * @param bean
     */
    public IceBrowserHandler(BrowserBean bean)
    {
        super(bean);
    }
    
    /**
     * Get the online office bean and cast it to a
     * BrowserBean
     *  
     * @return browser bean
     */
    public BrowserBean getBrowserBean()
    {
        return (BrowserBean) super.getOnlineOfficeBean();
    }
    /**
     * Activate the bean
     *  
     * 
     * @see oracle.retail.stores.pos.ui.behavior.ThirdPartyBrowserHandlerIfc#activate()
     */
    public void activate()
    {
    }
    
    /**
     * Configure Buttons
     *  
     * 
     * @see oracle.retail.stores.pos.ui.behavior.ThirdPartyBrowserHandlerIfc#configureButtons()
     */
    public void configureButtons()
    {
    }

    /**
     * Handle mouse presses on the buttons on the browser's button bar (action events)
     *  
     * @param evt ActionEvent
     * @see oracle.retail.stores.pos.ui.behavior.ThirdPartyBrowserHandlerIfc#actionPerformed(java.awt.event.ActionEvent)
     */
     public void actionPerformed(ActionEvent evt)
     {
     }

  
     /**
      * Handle key events from the ICE browser
      *  
      * @param e Event containing the keyPressed
      * @see oracle.retail.stores.pos.ui.behavior.ThirdPartyBrowserHandlerIfc#handleEvent(org.w3c.dom.events.Event)
      */
     public void handleEvent(Event e)
     {
     }
}

