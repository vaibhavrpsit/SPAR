/* ===========================================================================
* Copyright (c) 1998, 2013, Oracle and/or its affiliates. All rights reserved.
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/ui/POSJFCUISubsystem.java /main/16 2013/08/28 14:28:08 mkutiana Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    mkutiana  08/27/13 - prevent the button disablement for non-wait and
 *                         non-mailletter dialogs
 *    mkutiana  08/05/13 - Disable the ESC and NEXT buttons on the background
 *                         screen befroe showing the dialog. Prevents unmatched
 *                         letters.
 *    cgreene   06/04/13 - implement manager override as dialogs
 *    vbongu    01/02/13 - initialize instance variable in constructor
 *    asinton   09/21/11 - Fixed training mode and transaction reentry mode
 *                         screens.
 *    asinton   09/16/11 - Update ApplicationFrame using UIManager and
 *                         UISubsystem for Transaction Re-Entry Mode.
 *    cgreene   09/01/11 - corrected override of setModel so that timer is set
 *                         even on main menu
 *    cgreene   08/18/11 - Refactor code to not unlock screen when setting
 *                         model to avoid unwanted letters.
 *    cgreene   08/16/11 - implement timeout capability for admin menu
 *    cgreene   05/26/10 - convert to oracle packaging
 *    blarsen   03/09/10 - Passing in unlockContainer flag to prevent some
 *                         status bean updates from unlocking user input. Some
 *                         of the updates are for online/offline updates. These
 *                         are asynchronous and were allowing user input in
 *                         unexpected places in the tour. (Causing POS
 *                         crashes.)
 *    abondala  01/03/10 - update header date
 *    ranojha   02/23/09 - Forward Port Defect 2443
 *
 * ===========================================================================
 * $Log:
 *    3    360Commerce 1.2         3/31/2005 4:29:23 PM   Robert Pearse   
 *    2    360Commerce 1.1         3/10/2005 10:24:12 AM  Robert Pearse   
 *    1    360Commerce 1.0         2/11/2005 12:13:07 PM  Robert Pearse   
 *
 *   Revision 1.8  2004/07/08 20:17:51  lzhao
 *   @scr 6033: fix the problem of not setModel() when status changes.
 *
 *   Revision 1.7  2004/04/28 16:01:29  cdb
 *   @scr 3784 Temporary rollback of race condition fix pending repair of lockup problem.
 *
 *
 * ===========================================================================
 */
package oracle.retail.stores.pos.ui;

import oracle.retail.stores.common.utility.Util;
import oracle.retail.stores.foundation.manager.gui.ApplicationMode;
import oracle.retail.stores.foundation.manager.gui.UIException;
import oracle.retail.stores.foundation.manager.gui.UIModelIfc;
import oracle.retail.stores.foundation.manager.gui.UISubsystem;
import oracle.retail.stores.foundation.tour.application.Letter;
import oracle.retail.stores.foundation.utility.config.ConfigurationException;
import oracle.retail.stores.pos.services.common.CommonActionsIfc;
import oracle.retail.stores.pos.ui.beans.ApplicationFrame;
import oracle.retail.stores.pos.ui.beans.NavigationButtonBeanModel;
import oracle.retail.stores.pos.ui.beans.POSBaseBeanModel;
import oracle.retail.stores.pos.ui.beans.PromptAndResponseModel;
import oracle.retail.stores.pos.ui.timer.DefaultTimedAction;
import oracle.retail.stores.pos.ui.timer.DefaultTimedController;
import oracle.retail.stores.pos.ui.timer.ScreenTimeoutIfc;
import oracle.retail.stores.pos.ui.timer.TimedActionIfc;
import oracle.retail.stores.pos.ui.timer.TimedControllerIfc;
import oracle.retail.stores.pos.ui.timer.TimerModelIfc;

import org.apache.log4j.Logger;

/**
 * This class is the UI Subsystem for the POS application. It adds the following
 * functionality: 1. Maintains the value of the current screen name and provides
 * its value to classes in the UI. It is used to display a screen temporarily
 * and then return to the current screen. 2. Uses the current screen name to
 * display new values in the status panel only. 3. Uses the current screen name
 * to retrieve the model from the current screen.
 * 
 * @version $Revision: /main/16 $
 */
public class POSJFCUISubsystem extends UISubsystem
{
    private static final long serialVersionUID = 1821922415996649137L;

    /** revision number supplied by Team Connection */
    public static final String revisionNumber = "$Revision: /main/16 $";

    /** Store instance of logger here */
    protected static final Logger logger = Logger.getLogger(POSJFCUISubsystem.class);

    /**
     * Contains the screen ID of the current screen. The class maintains this
     * data member in order to be able to obtain the previous screen ID.
     */
    protected String currentScreenSpecName = "";

    protected TimedControllerIfc timedController;

    /**
     * Class constructor. Creates an instance of UISubsystem.
     */
    public POSJFCUISubsystem()
    {
        super();
        instance = this;
    }

    /**
     * This method saves the screen spec name and calls the super.
     * 
     * @param screenSpecName the name of the screen specification
     * @throws UIException if the screen cannot be created at runtime.
     * @throws ConfigurationException if the screen cannot be configured at
     *             startup.
     */
    @Override
    public void showScreen(String screenSpecName) throws UIException, ConfigurationException
    {
        currentScreenSpecName = screenSpecName;
        super.showScreen(screenSpecName);
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
    @Override
    public void showScreen(String screenSpecName, UIModelIfc model) throws UIException, ConfigurationException
    {
        // special case for showing status
        if (screenSpecName.equals(POSUIManagerIfc.SHOW_STATUS_ONLY))

        {
            // get the current model and set the status model onto it
            UIModelIfc uiModel = getModel(currentScreenSpecName);
            if (uiModel instanceof POSBaseBeanModel && model instanceof POSBaseBeanModel)
            {
                POSBaseBeanModel currentModel = (POSBaseBeanModel)uiModel;
                POSBaseBeanModel modelContainingStatus = (POSBaseBeanModel)model;
                currentModel.setStatusBeanModel(modelContainingStatus.getStatusBeanModel());
                // status set, reset current model
                setModel(currentScreenSpecName, currentModel, true);
            }
            else
            {
                // current model is not available, call super with model available
                setModel(currentScreenSpecName, model, true);
            }
        }
        else
        {
            currentScreenSpecName = screenSpecName;
            super.showScreen(screenSpecName, model);
        }
    }

    /**
     * This method allows for updating the status panel only.
     * 
     * @param screenSpecName the name of the screen specification
     * @param model the model for the screen identified by screenSpecName
     * @param unlockContainer whether or not to unlock the screen.
     * @throws UIException if the screen cannot be created at runtime.
     */
    @Override
    public void setModel(String screenSpecName, UIModelIfc model, boolean unlockContainer) throws UIException
    {
        // special case for showing status
        if (screenSpecName.equals(POSUIManagerIfc.SHOW_STATUS_ONLY) && model instanceof POSBaseBeanModel)
        {
            // get the current model and set the status model onto it
            UIModelIfc currentModel = getModel(currentScreenSpecName);
            if (currentModel instanceof POSBaseBeanModel)
            {
                POSBaseBeanModel currentPOSModel = (POSBaseBeanModel)currentModel;
                POSBaseBeanModel modelContainingStatus = (POSBaseBeanModel)model;
                // status set onto current model
                currentPOSModel.setStatusBeanModel(modelContainingStatus.getStatusBeanModel());
                // only unlock if current screen was unlocked. We may be in site/aisle transition
                unlockContainer = unlockContainer && currentPOSModel.isUnlockContainer();
                super.setModel(currentScreenSpecName, currentModel, unlockContainer);
            }
            else
            {
                // current model is not available, call super with model available
                super.setModel(currentScreenSpecName, model, unlockContainer);
            }
        }
        else
        {
            currentScreenSpecName = screenSpecName;
            super.setModel(screenSpecName, model, unlockContainer);
        }
    }

    /**
     * Overridden to ensure timer model is reset when model is changed.
     * 
     * @see oracle.retail.stores.foundation.manager.gui.UISubsystem#setModelImpl(java.lang.String, oracle.retail.stores.foundation.manager.gui.UIModelIfc)
     */
    @Override
    protected void setModelImpl(String screenId, UIModelIfc model) throws UIException
    {
        if (model instanceof ScreenTimeoutIfc)
        {
            TimerModelIfc timerModel = ((ScreenTimeoutIfc)model).getTimerModel();
            timerModel.setController(getTimedController());
            getTimedController().clearActions();

            String actionClass = timerModel.getActionClass();
            TimedActionIfc action = null;

            if (actionClass != null)
            {
                action = (TimedActionIfc)UIUtilities.getNamedClass(actionClass);
            }
            if (action == null)
            {
                action = new DefaultTimedAction();
            }
            action.setTimeInterval(timerModel.getTimerInterval());
            // Set what letter to mail when a timeout occurs
            action.setTimedActionName(timerModel.getActionName());
            // Tell controller who to notify
            timedController.addTimedAction(action);

            if (timerModel.isTimerEnabled())
            {
                getTimedController().startController();
            }
            else
            {
                getTimedController().stopController();
            }
        }
        super.setModelImpl(screenId, model);
    }

    /**
     * Gets the model from the specified screen; if the specification name is
     * GET_CURRENT_SCREEN, then get the model from the current screen.
     * 
     * @param specName the id of the screen
     * @return the bean model of the specified screen
     * @throws UIException
     */
    @Override
    public UIModelIfc getModel(String specName) throws UIException
    {
        String screenSpecName = specName;
        if (specName.equals(POSUIManagerIfc.GET_CURRENT_SCREEN))
        {
            screenSpecName = currentScreenSpecName;
        }

        return super.getModel(screenSpecName);
    }

    /**
     * Gets the current screen name
     * 
     * @return the screen name of the current screen
     */
    public String getCurrentScreenSpecName()
    {
        String screenId = null;
        try
        {
            // changed to use foundations implementation of getActiveScreenID
            // was currentScreenSpecName.
            screenId = getActiveScreenID();
        }
        catch (UIException e)
        {
            logger.error(Util.throwableToString(e));
        }
        return screenId;
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

    /*
     * (non-Javadoc)
     * @see oracle.retail.stores.foundation.manager.ifc.ui.UISubsystemIfc#setApplicationMode(oracle.retail.stores.foundation.manager.gui.ApplicationMode)
     */
    @Override
    public void setApplicationMode(ApplicationMode applicationMode)
    {
        if(appContainer instanceof ApplicationFrame)
        {
            ((ApplicationFrame)appContainer).setApplicationMode(applicationMode);
        }
        else
        {
            logger.error("Could not set application mode on UI.");
        }
    }

    /* (non-Javadoc)
     * @see oracle.retail.stores.foundation.manager.gui.UISubsystem#mailDialogLetter(java.lang.String)
     */
    @Override
    protected void mailDialogLetter(String screenId)
    {
        super.mailDialogLetter(screenId);
        try
        {
            UIModelIfc model = getDialogModel();
            if (model instanceof POSBaseBeanModel)
            {
                POSBaseBeanModel pmodel = (POSBaseBeanModel)model;
                PromptAndResponseModel parModel = pmodel.getPromptAndResponseModel();
                if (parModel != null && !Util.isEmpty(parModel.getResponseCommand()))
                {
                    enableLetters();
                    mail(new Letter(parModel.getResponseCommand()));
                }
                else
                {
                    logger.warn("Unable to mail dialog letter. " + parModel);
                }
            }
        }
        catch (UIException e)
        {
            logger.error("Unable to mail dialog letter because could not get model.", e);
        }
    }
    
    
    /**
     * Overridden to update active screen for dialog. Specifically disabling
     * the ENTER and ESCAPE buttons so they are not inadvertently triggered by events
     * form the dialog. Do this only if mailLetter and wait are true i.e. the system waits for letters.
     * 
     * @param screenId the id of the screen to show.
     * @param wait should the method block while the dialog is showing.
     * @param mailLetter mail the resulting actionCommand as a letter
     * @throws UIException if an error occurs while attempting to show the dialog.
     */
    protected void installAndShowDialog(String screenId, boolean wait, boolean mailLetter) throws UIException
    {
        // get the active screen's model
        if (activeScreen.getScreenModel() instanceof POSBaseBeanModel && wait && mailLetter)
        {
            POSBaseBeanModel posBeanModel = (POSBaseBeanModel)activeScreen.getScreenModel();            
            
            NavigationButtonBeanModel savedGlobalButtonBeanModel = posBeanModel.getGlobalButtonBeanModel();
            
            if (savedGlobalButtonBeanModel != null)
            {
                // set the global nav buttons to disabled
                posBeanModel.getGlobalButtonBeanModel().setButtonEnabled(CommonActionsIfc.NEXT, false);
                posBeanModel.getGlobalButtonBeanModel().setButtonEnabled(CommonActionsIfc.UNDO, false);
                activeScreenRefresh();
                
                super.installAndShowDialog(screenId, wait, mailLetter);
                //reset the global nav buttons to original state.
                posBeanModel.setGlobalButtonBeanModel(savedGlobalButtonBeanModel);
                
                activeScreenRefresh();
            }
            else
            {
                super.installAndShowDialog(screenId, wait, mailLetter);
            }
        }
        else
        {
            super.installAndShowDialog(screenId, wait, mailLetter);
        }
    }

    /**
     * Refreshs the active screen
     * @throws UIException
     */
    private void activeScreenRefresh() throws UIException
    {
        try
        {
            activeScreen.refresh();
        }
        catch (ConfigurationException e)
        {
            throw new UIException("Error in default model configuration for screen: " + activeScreen.getScreenID(), e);
        }
    }


}