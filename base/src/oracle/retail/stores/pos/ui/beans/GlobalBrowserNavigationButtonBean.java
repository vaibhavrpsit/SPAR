/* ===========================================================================
* Copyright (c) 2008, 2011, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/ui/beans/GlobalBrowserNavigationButtonBean.java /rgbustores_13.4x_generic_branch/1 2011/05/05 14:06:48 mszekely Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED (MM/DD/YY)
 *    cgreen 05/28/10 - convert to oracle packaging
 *    cgreen 05/27/10 - convert to oracle packaging
 *    cgreen 05/27/10 - convert to oracle packaging
 *    cgreen 05/26/10 - convert to oracle packaging
 *    abonda 01/03/10 - update header date
 *    cgreen 12/07/09 - update to setOrientation method
 *    nkgaut 11/14/08 - Changes to accomodate six buttons on Global panel only
 *                      for browser
 *    nkgaut 09/29/08 - A new class similiar to GlobalNavigationButtonBean for
 *                      BrowserFoundation
 * ===========================================================================
 */
package oracle.retail.stores.pos.ui.beans;

import oracle.retail.stores.pos.ui.behaviour.BrowserBackActionListener;
import oracle.retail.stores.pos.ui.behaviour.BrowserForwardActionListener;
import oracle.retail.stores.pos.ui.behaviour.BrowserHomeActionListener;
import oracle.retail.stores.pos.ui.behaviour.BrowserRefreshActionListener;
import oracle.retail.stores.pos.ui.behaviour.BrowserStopActionListener;

import org.apache.log4j.Logger;

import oracle.retail.stores.foundation.manager.gui.UIModelIfc;
import oracle.retail.stores.pos.ui.beans.ActionNotFoundException;
import oracle.retail.stores.pos.ui.beans.NavigationButtonBean;
import oracle.retail.stores.pos.ui.beans.POSBaseBeanModel;
import oracle.retail.stores.pos.ui.beans.UIAction;

/**
 * This class configures the GlobalNavigationButtons on the browserScreen
 */
public class GlobalBrowserNavigationButtonBean extends NavigationButtonBean
{
    private static final long serialVersionUID = -6063375887445532628L;

    private static final Logger logger = Logger.getLogger(GlobalBrowserNavigationButtonBean.class);

    /**
     * Constant for the BACK action.
     */
    public String BACK = "Back";

    /**
     * Constant for the FORWARD action.
     */
    public String FORWARD = "Forward";

    /**
     * Constant for the REFRESH action.
     */
    public String REFRESH = "Refresh";

    /**
     * Constant for the STOP action.
     */
    public String STOP = "Stop";

    /**
     * Constant for the HOME action.
     */
    public String HOME = "Home";

    /**
     * POS Base Bean Model instance
     */
    protected POSBaseBeanModel baseModel = null;

    /**
     * Default constructor.
     */
    public GlobalBrowserNavigationButtonBean()
    {
        super();
        setOrientation(HORIZONTAL);
    }

    /**
     * Creates an empty NavigationButtonBean.
     * 
     * @param actions two dimensional list of buttions
     */
    public GlobalBrowserNavigationButtonBean(UIAction[][] actions)
    {
        this();
        initialize(actions);
    }

    /**
     * Sets the model for the current settings of this bean.
     * 
     * @param model the model for the current values of this bean
     */
    public void setModel(UIModelIfc model)
    {
        if (model == null)
        {
            throw new NullPointerException("Attempt to set model to null");
        }
        if (model instanceof POSBaseBeanModel)
        {
            baseModel = (POSBaseBeanModel)model;
        }
        if (baseModel.getGlobalButtonBeanModel() != null)
        {
            buttonModel = baseModel.getGlobalButtonBeanModel();
            if (buttonModel.getNewButtons() != null)
            {
                configureButtons(buttonModel.getNewButtons());
            }

            if (buttonModel.getModifyButtons() != null)
            {
                modifyButtons(buttonModel.getModifyButtons());
            }
        }
    }

    /**
     * Adds (actually sets) the validation listener on the Back button.
     * 
     * @Param listener the Validate Action Listener
     */
    public void addBrowserBackActionListener(BrowserBackActionListener listener)
    {
        try
        {
            getUIAction(BACK).setActionListener(listener);
        }
        catch (ActionNotFoundException e)
        {
            logger.warn("Did not find the BACK action.");
        }
    }

    /**
     * Removes (actually resets) the validation listener on the Back button.
     * 
     * @Param listener the Validate Action Listener
     */
    public void removeBrowserBackActionListener(BrowserBackActionListener listener)
    {
        try
        {
            getUIAction(BACK).resetActionListener();
        }
        catch (ActionNotFoundException e)
        {
            logger.warn("Did not find the BACK action.");
        }
    }

    /**
     * Adds (actually sets) the clear listener on the Forward button.
     * 
     * @Param listener the Clear Action Listener
     */
    public void addBrowserForwardActionListener(BrowserForwardActionListener listener)
    {
        try
        {
            getUIAction(FORWARD).setActionListener(listener);
        }
        catch (ActionNotFoundException e)
        {
            logger.warn("Did not find the FORWARD action.");
        }
    }

    /**
     * Removes (actually resets) the Clear listener on the Forward button.
     * 
     * @Param listener the Clear Action Listener
     */
    public void removeBrowserForwardActionListener(BrowserForwardActionListener listener)
    {
        try
        {
            getUIAction(FORWARD).resetActionListener();
        }
        catch (ActionNotFoundException e)
        {
            logger.warn("Did not find the FORWARD action.");
        }
    }

    /**
     * Adds (actually sets) the Browser refresh listener on the refresh button.
     * 
     * @Param listener the Clear Action Listener
     */
    public void addBrowserRefreshActionListener(BrowserRefreshActionListener listener)
    {
        try
        {
            getUIAction(REFRESH).setActionListener(listener);
        }
        catch (ActionNotFoundException e)
        {
            logger.warn("Did not find the REFRESH action.");
        }
    }

    /**
     * Removes (actually resets) the Clear listener on the Forward button.
     * 
     * @Param listener the Clear Action Listener
     */
    public void removeBrowserRefreshActionListener(BrowserRefreshActionListener listener)
    {
        try
        {
            getUIAction(REFRESH).resetActionListener();
        }
        catch (ActionNotFoundException e)
        {
            logger.warn("Did not find the REFRESH action.");
        }
    }

    /**
     * Adds (actually sets) the clear listener on the Forward button.
     * 
     * @Param listener the Clear Action Listener
     */
    public void addBrowserStopActionListener(BrowserStopActionListener listener)
    {
        try
        {
            getUIAction(STOP).setActionListener(listener);
        }
        catch (ActionNotFoundException e)
        {
            logger.warn("Did not find the STOP action.");
        }
    }

    /**
     * Removes (actually resets) the Clear listener on the Forward button.
     * 
     * @Param listener the Clear Action Listener
     */
    public void removeBrowserStopActionListener(BrowserStopActionListener listener)
    {
        try
        {
            getUIAction(STOP).resetActionListener();
        }
        catch (ActionNotFoundException e)
        {
            logger.warn("Did not find the STOP action.");
        }
    }

    /**
     * Adds (actually sets) the clear listener on the Forward button.
     * 
     * @Param listener the Clear Action Listener
     */
    public void addBrowserHomeActionListener(BrowserHomeActionListener listener)
    {
        try
        {
            getUIAction(HOME).setActionListener(listener);
        }
        catch (ActionNotFoundException e)
        {
            logger.warn("Did not find the HOME action.");
        }
    }

    /**
     * Removes (actually resets) the Clear listener on the Forward button.
     * 
     * @Param listener the Clear Action Listener
     */
    public void removeBrowserHomeActionListener(BrowserHomeActionListener listener)
    {
        try
        {
            getUIAction(HOME).resetActionListener();
        }
        catch (ActionNotFoundException e)
        {
            logger.warn("Did not find the HOME action.");
        }
    }

}
