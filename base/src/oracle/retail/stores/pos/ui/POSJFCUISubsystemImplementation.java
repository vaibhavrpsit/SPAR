/* ===========================================================================
* Copyright (c) 2008, 2011, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/ui/POSJFCUISubsystemImplementation.java /rgbustores_13.4x_generic_branch/2 2011/08/16 13:50:22 cgreene Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    cgreene   08/16/11 - implement timeout capability for admin menu
 *    cgreene   05/26/10 - convert to oracle packaging
 *    abondala  01/03/10 - update header date
 *
 * ===========================================================================
 */
package oracle.retail.stores.pos.ui;

import oracle.retail.stores.foundation.manager.gui.EYSRootPaneContainer;
import oracle.retail.stores.foundation.manager.gui.UIException;
import oracle.retail.stores.foundation.manager.gui.UIModelIfc;
import oracle.retail.stores.foundation.manager.gui.UISubsystemImplementation;
import oracle.retail.stores.foundation.manager.gui.loader.CatalogIfc;
import oracle.retail.stores.foundation.utility.config.ConfigurationException;
import oracle.retail.stores.pos.ui.timer.DefaultTimedAction;
import oracle.retail.stores.pos.ui.timer.DefaultTimedController;
import oracle.retail.stores.pos.ui.timer.ScreenTimeoutIfc;
import oracle.retail.stores.pos.ui.timer.TimedActionIfc;
import oracle.retail.stores.pos.ui.timer.TimedControllerIfc;
import oracle.retail.stores.pos.ui.timer.TimerModelIfc;

/**
 * Class that implements the UISubsystem.  All calls in this object should
 * be on the AWT dispatch thread.
 */
public class POSJFCUISubsystemImplementation extends UISubsystemImplementation
{
    protected TimedControllerIfc timedController;
    
    
    /**
     * @param appContainerValue
     * @param dialogValue
     * @param beanCatalogValue
     * @param displayCatalogValue
     * @param templateCatalogValue
     * @param defaultScreenCatalogValue
     * @param overlayScreenCatalogValue
     */
    public POSJFCUISubsystemImplementation(
        EYSRootPaneContainer appContainerValue,
        EYSRootPaneContainer dialogValue,
        CatalogIfc beanCatalogValue,
        CatalogIfc displayCatalogValue,
        CatalogIfc templateCatalogValue,
        CatalogIfc defaultScreenCatalogValue,
        CatalogIfc overlayScreenCatalogValue)
    {
        super(
            appContainerValue,
            dialogValue,
            beanCatalogValue,
            displayCatalogValue,
            templateCatalogValue,
            defaultScreenCatalogValue,
            overlayScreenCatalogValue);
    }

    /**
     * This method allows for updating the status panel only.
     * 
     * @param screenSpecName the name of the screen specification
     * @param model the model for the screen identified by screenSpecName
     * @throws UIException if the screen cannot be created at runtime.
     * @throws ConfigurationException if the screen cannot be configured at
     *             startup.
     */
    public void showScreen(String screenSpecName, UIModelIfc model) throws UIException, ConfigurationException
    {
        if (screenSpecName.equals(POSUIManagerIfc.SHOW_STATUS_ONLY))
        {
            //by default this will set the model on the current screen
            //since we are only updating the status we do not want 
            //mail to be enabled -- if it is disabled (ie stay the same)            
            super.setModel(getActiveScreenID(), model, false);
        }
        else
        {
            super.showScreen(screenSpecName, model);
        }
    }

    /**
     * This method allows for updating the status panel only.
     * 
     * @param screenSpecName the name of the screen specification
     * @param model the model for the screen identified by screenSpecName
     * @throws UIException if the screen cannot be created at runtime.
     */
    public void setModel(String screenSpecName, UIModelIfc model, boolean enableMail) throws UIException, ConfigurationException
    {
        boolean setUIModel = true;
        // Ok models changing make sure to check for any timeout stuff
        if(model instanceof ScreenTimeoutIfc)
        {
            TimerModelIfc timerModel = ((ScreenTimeoutIfc)model).getTimerModel();
            timerModel.setController(getTimedController());
            getTimedController().clearActions();

            String actionClass = timerModel.getActionClass();
            TimedActionIfc action = null;

            if(actionClass != null)
            {
                action = (TimedActionIfc)UIUtilities.getNamedClass(actionClass);
            }
            if(action == null)
            {
                action = new DefaultTimedAction();
            }
            action.setTimeInterval(timerModel.getTimerInterval());
            // Set what letter to mail when a timeout occurs
            action.setTimedActionName(timerModel.getActionName()); 
            // Tell controller who to notify
            timedController.addTimedAction(action);

            if(timerModel.isTimerEnabled())
            {
                getTimedController().startController();
            }
            else
            {
                getTimedController().stopController();
            }
        }

        if(setUIModel)
        {
            if (screenSpecName.equals(POSUIManagerIfc.SHOW_STATUS_ONLY))
            {
                //by default this will set the model on the current screen
                //since we are only updating the status we do not want 
                //mail to be enabled -- if it is disabled (ie stay the same)
                
                super.setModel(getActiveScreenID(), model, false);
            }
            else
            {
                super.setModel(screenSpecName, model, true);
            }
        }
    }

    /**
     * Gets the model from the specified screen; if the specification name is
     * GET_CURRENT_SCREEN, then get the model from the current screen.
     * 
     * @param specName the id of the screen
     * @return the bean model of the specified screen
     * @throws UIException
     */
    public UIModelIfc getModel(String specName) throws UIException
    {
        String screenSpecName = specName;
        if (specName.equals(POSUIManagerIfc.GET_CURRENT_SCREEN))
        {
            screenSpecName = getActiveScreenID();
        }

        return (super.getModel(screenSpecName));
    }

    /**
     * Create the Controller for UI timeouts if it doesnt exist, otherwise
     * return the already existing controller
     * 
     * @return timedController
     */
    public TimedControllerIfc getTimedController()
    {
        if (this.timedController == null)
        {
            this.timedController = new DefaultTimedController();
        }
        return this.timedController;
    }

    /**
     * This sets the timedController. This is not used in product, but provided
     * for services to override the default with their own implementation.
     * 
     * @param aTimedController TimedController to use
     */
    public void setTimedController(TimedControllerIfc aTimedController)
    {
        this.timedController = aTimedController;
    }

}
