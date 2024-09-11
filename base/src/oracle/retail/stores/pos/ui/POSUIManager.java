/* ===========================================================================
* Copyright (c) 1998, 2014, Oracle and/or its affiliates. All rights reserved.
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/ui/POSUIManager.java /main/21 2014/03/18 17:51:20 abondala Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    abondala  03/18/14 - OnlineStatusContainer update method is called only
 *                         if the model is null, otherwise the bean will the
 *                         update on status container.
 *    abondala  03/18/14 - fix for the online status when the client is started
 *                         with out server running.
 *    mkutiana  07/26/13 - fixed typo
 *    cgreene   06/04/13 - implement manager override as dialogs
 *    hyin      10/27/11 - fixed sending cancel letter twice problem when user
 *                         double click on cancel button
 *    asinton   09/21/11 - Fixed training mode and transaction reentry mode
 *                         screens.
 *    asinton   09/16/11 - Update ApplicationFrame using UIManager and
 *                         UISubsystem for Transaction Re-Entry Mode.
 *    cgreene   08/18/11 - Refactor code to not unlock screen when setting
 *                         model to avoid unwanted letters.
 *    cgreene   05/26/10 - convert to oracle packaging
 *    cgreene   04/26/10 - XbranchMerge cgreene_tech11_techissueseatel from
 *                         st_rgbustores_techissueseatel_generic_branch
 *    cgreene   03/24/10 - rethrow caught runtime exceptions
 *    blarsen   03/09/10 - Added overloaded statusChanged method that accepts
 *                         boolean unlockContainer param. This param is
 *                         required to prevent the premature unlocking of user
 *                         input when an asynchrounus online/offline update is
 *                         sent.
 *    abondala  01/03/10 - update header date
 *    cgreene   09/17/09 - added method to be able to show a dialog and block
 *                         the thread
 *    mdecama   02/12/09 - Added LookAndFeel support by Locale
 *    cgreene   01/30/09 - remove methods deprecated in 4.0
 *
 * ===========================================================================
 * $Log:
 *    4    360Commerce 1.3         3/27/2008 12:04:13 PM  Christian Greene
 *         Update collections with Generics castings.
 *    3    360Commerce 1.2         3/31/2005 4:29:27 PM   Robert Pearse
 *    2    360Commerce 1.1         3/10/2005 10:24:18 AM  Robert Pearse
 *    1    360Commerce 1.0         2/11/2005 12:13:21 PM  Robert Pearse
 *
 *   Revision 1.5  2004/04/09 16:56:00  cdb
 *   @scr 4302 Removed double semicolon warnings.
 *
 *   Revision 1.4  2004/03/24 19:37:10  mweis
 *   @scr 0 JavaDoc cleanup.
 *
 *   Revision 1.3  2004/02/12 16:52:11  mcs
 *   Forcing head revision
 *
 *   Revision 1.2  2004/02/11 21:52:28  rhafernik
 *   @scr 0 Log4J conversion and code cleanup
 *
 *   Revision 1.1.1.1  2004/02/11 01:04:21  cschellenger
 *   updating to pvcs 360store-current
 *
 *
 *
 *    Rev 1.0   Aug 29 2003 16:09:26   CSchellenger
 * Initial revision.
 *
 *    Rev 1.2   May 27 2003 09:10:10   baa
 * uncomment change to statusChange method
 * Resolution for 2483: MBC Customer Sceen
 *
 *    Rev 1.1   May 27 2003 08:49:30   baa
 * rework customer offline flow
 * Resolution for 2387: Deleteing Busn Customer Lock APP- & Inc. Customer.
 *
 *    Rev 1.0   Apr 29 2002 14:45:14   msg
 * Initial revision.
 *
 *    Rev 1.0   Mar 18 2002 11:51:50   msg
 * Initial revision.
 *
 *    Rev 1.2   Jan 19 2002 10:28:50   mpm
 * Initial implementation of pluggable-look-and-feel user interface.
 * Resolution for POS SCR-798: Implement pluggable-look-and-feel user interface
 *
 *    Rev 1.1   12 Oct 2001 12:03:42   jbp
 * Modified for screen level help functionality
 * Resolution for POS SCR-211: HTML Help Functionality
 *
 *    Rev 1.0   Sep 21 2001 11:33:28   msg
 * Initial revision.
 *
 *    Rev 1.1   Sep 17 2001 13:16:02   msg
 * header update
 * ===========================================================================
 */
package oracle.retail.stores.pos.ui;

import java.io.Serializable;
import java.rmi.RemoteException;

import oracle.retail.stores.common.utility.Util;
import oracle.retail.stores.foundation.comm.CommException;
import oracle.retail.stores.foundation.manager.gui.AddLetterListenerValet;
import oracle.retail.stores.foundation.manager.gui.ApplicationMode;
import oracle.retail.stores.foundation.manager.gui.CurrentScreenIDValet;
import oracle.retail.stores.foundation.manager.gui.GetModelValet;
import oracle.retail.stores.foundation.manager.gui.RemoveLetterListenerValet;
import oracle.retail.stores.foundation.manager.gui.SetApplicationModeValet;
import oracle.retail.stores.foundation.manager.gui.SetLookAndFeelValet;
import oracle.retail.stores.foundation.manager.gui.SetModelValet;
import oracle.retail.stores.foundation.manager.gui.ShowDialogModelValet;
import oracle.retail.stores.foundation.manager.gui.ShowScreenModelValet;
import oracle.retail.stores.foundation.manager.gui.ShowScreenValet;
import oracle.retail.stores.foundation.manager.gui.UIException;
import oracle.retail.stores.foundation.manager.gui.UIManager;
import oracle.retail.stores.foundation.manager.gui.UIModelIfc;
import oracle.retail.stores.foundation.naming.MailboxAddress;
import oracle.retail.stores.foundation.tour.gate.ValetException;
import oracle.retail.stores.foundation.tour.manager.ValetIfc;
import oracle.retail.stores.pos.ui.beans.POSBaseBeanModel;
import oracle.retail.stores.pos.ui.beans.PromptAndResponseModel;
import oracle.retail.stores.pos.ui.beans.StatusBeanModel;

import org.apache.log4j.Logger;

/**
 * This is the UI manager for the POS application. One is started for each
 * service that is created. The UI manager communicates with a UI Technician
 * which updates the screen for a client running the UI.
 *
 * @version $Revision: /main/21 $
 */
public class POSUIManager extends UIManager implements POSUIManagerIfc
{
    /**
     * The logger to which log messages will be sent.
     */
    protected static final Logger logger = Logger.getLogger(POSUIManager.class);

    /** revision number */
    public static final String revisionNumber = "$Revision: /main/21 $";

    /**
     * Default constructor.
     */
    public POSUIManager()
    {
        super();
    }

    /**
     * Calls the UIManager$transportValet method with the valet of this object.
     * <P>
     * <B>Pre-conditions</B>
     * <UL>
     * <LI>This object's valet has been initialized.
     * </UL>
     * <B>Post-conditions</B>
     * <UL>
     * <LI>The valet is executed at the technician for this application.
     * </UL>
     *
     * @param valet The valet
     * @return the result of executing the valet
     */
    protected Serializable transport(ValetIfc valet)
    {
        Serializable retObj = null;
        try
        {
            retObj = transportValet(valet);

        }
        catch (RemoteException e)
        {
            logger.error(Util.throwableToString(e));
        }
        catch (ValetException e)
        {
            logger.error(Util.throwableToString(e));
        }
        catch (CommException e)
        {
            logger.error(Util.throwableToString(e));
        }

        return retObj;
    }

    /**
     * Gets the model from the specified screen.
     *
     * @param screenId the id of the screen
     * @return the bean model of the specified screen
     */
    public UIModelIfc getModel(String screenId)
    {
        GetModelValet valet = new GetModelValet(screenId);

        return (UIModelIfc)transport(valet);
    }

    /**
     * Retrieves the active screens ID
     *
     * @return String the active screens ID.
     * @throws UIException if the active screen is not currently defined.
     */
    public String getActiveScreenID() throws UIException
    {
        try
        {
            return ((String)transportValet(new CurrentScreenIDValet()));
        }
        catch (RemoteException re)
        {
            Throwable detail = re.detail;
            if (detail instanceof UIException)
            {
                throw (UIException)detail;
            }
            throw new UIException("Unable to retrieve the active screen ID", re);
        }
        catch (CommException ce)
        {
            Throwable detail = ce.getCause();
            if (detail instanceof UIException)
            {
                throw (UIException)detail;
            }
            throw new UIException("Unable to retrieve the active screen ID", ce);
        }
        catch (ValetException ve)
        {
            Throwable detail = ve.getCause();
            if (detail instanceof UIException)
            {
                throw (UIException)detail;
            }
            throw new UIException("Unable to retrieve the active screen ID", ve);
        }
    }

    /**
     * Gets the current model
     *
     * @return the bean model of the current screen
     */
    public UIModelIfc getModel()
    {
        String activeScreenId = null;
        try
        {
            activeScreenId = getActiveScreenID();
        }
        catch (UIException e)
        {
            logger.error("" + Util.throwableToString(e) + "");
        }
        return getModel(activeScreenId);
    }

    /**
     * This method gets the contents of the Response area as a string.
     *
     * @return the string value of the respose area.
     */
    public String getInput()
    {
        String response = "";
        POSBaseBeanModel model = (POSBaseBeanModel)getModel();
        PromptAndResponseModel prModel = model.getPromptAndResponseModel();
        if (prModel != null)
        {
            response = prModel.getResponseText();
        }
        return response;
    }

    /**
     * Sets the model of the current bean.
     *
     * @param screenId the id of the bean whose model is to be set
     * @param beanModel The new model of the current bean. View the current
     *            bean's documentation to find the correct type associate with
     *            the bean.
     */
    @Override
    public void setModel(String screenId, UIModelIfc beanModel)
    {
        SetModelValet valet = new SetModelValet(screenId, beanModel);
        transport(valet);
    }

    /**
     * Sets the model of the current bean.
     *
     * @param screenId the id of the bean whose model is to be set
     * @param beanModel The new model of the current bean. View the current
     *            bean's documentation to find the correct type associate with
     *            the bean.
     * @param unlockContainer Set to true if the screen should be unlocked for user events.
     */
    @Override
    public void setModel(String screenId, UIModelIfc beanModel, boolean unlockContainer)
    {
        SetModelValet valet = new SetModelValet(screenId, beanModel, unlockContainer);
        transport(valet);
    }

    /**
     * This method should be called when the cashier name has changed in the
     * SaleReturnTransaction. It sets the string that shows up in the "Cashier"
     * field of the frame.
     *
     * @param emplName the new cashier's name
     */
    public void cashierNameChanged(String emplName)
    {
        POSBaseBeanModel base = (POSBaseBeanModel)getModel();

        if (base == null)
        {
            base = new POSBaseBeanModel();
        }

        StatusBeanModel statusModel = new StatusBeanModel();

        statusModel.setCashierName(emplName);
        base.setStatusBeanModel(statusModel);

        // Send the data to the bean
        showScreen(POSUIManagerIfc.SHOW_STATUS_ONLY, base);
    }

    /**
     * This method should be called when the customer name has changed in the
     * SaleReturnTransaction. It sets the string that shows up in the "Customer"
     * field of the frame.
     *
     * @param customerName the new customer's name
     */
    public void customerNameChanged(String customerName)
    {
        POSBaseBeanModel base = (POSBaseBeanModel)getModel();

        if (base == null)
        {
            base = new POSBaseBeanModel();
        }

        StatusBeanModel statusModel = new StatusBeanModel();

        statusModel.setCustomerName(customerName);
        base.setStatusBeanModel(statusModel);

        // Send the data to the bean
        showScreen(POSUIManagerIfc.SHOW_STATUS_ONLY, base);
    }

    /**
     * This method should be called when the customer name has changed in the
     * SaleReturnTransaction. It sets the string that shows up in the "Customer"
     * field of the frame. The difference is this method provides a way to let 
     * caller to decide whether to unlock container or not.
     *
     * @param customerName the new customer's name
     * @param unlockContainer whether to unlock container or not
     */
    public void customerNameChanged(String customerName, boolean unlockContainer)
    {
        POSBaseBeanModel base = (POSBaseBeanModel)getModel();

        if (base == null)
        {
            base = new POSBaseBeanModel();
        }

        StatusBeanModel statusModel = new StatusBeanModel();

        statusModel.setCustomerName(customerName);
        base.setStatusBeanModel(statusModel);

        setModel(POSUIManagerIfc.SHOW_STATUS_ONLY, base, unlockContainer);
    }
    
    /**
     * This method should be called when the workstationID has changed in the
     * SaleReturnTransaction. It sets the string that shows up in the "Register"
     * field of the frame.
     *
     * @param workstationID the new workstationID
     */
    public void registerChanged(String workstationID)
    {
        POSBaseBeanModel base = (POSBaseBeanModel)getModel();

        if (base == null)
        {
            base = new POSBaseBeanModel();
        }

        StatusBeanModel statusModel = new StatusBeanModel();

        statusModel.setRegisterId(workstationID);
        base.setStatusBeanModel(statusModel);

        // Send the data to the bean
        showScreen(POSUIManagerIfc.SHOW_STATUS_ONLY, base);
    }

    /**
     * This method should be called when the salesperson name has changed in the
     * SaleReturnTransaction. It sets the string that shows up in the
     * "SalesPerson" field of the frame.
     *
     * @param emplName the new salesperson's name
     */
    public void salesAssociateNameChanged(String emplName)
    {
        POSBaseBeanModel base = (POSBaseBeanModel)getModel();

        if (base == null)
        {
            base = new POSBaseBeanModel();
        }

        StatusBeanModel statusModel = new StatusBeanModel();

        statusModel.setSalesAssociateName(emplName);
        base.setStatusBeanModel(statusModel);

        // Send the data to the bean
        showScreen(POSUIManagerIfc.SHOW_STATUS_ONLY, base);
    }

    /**
     * This method is called to set the status on the UI screen.
     *
     * @param systemID identifies the system to which this status belongs.
     * @param online true means the system is online.
     */
    public void statusChanged(int systemID, boolean online)
    {
        statusChanged(systemID, online, POSUIManagerIfc.UNLOCK_CONTAINER);
    }

    /**
     * This method is called to set the status on the UI screen.
     *
     * @param systemID identifies the system to which this status belongs.
     * @param online true means the system is online.
     * @param unlockContainer should user input be unlocked by this status change?
     *        If the change is asynchronous and not part of a tour, it is a good idea
     *        not to unlock the container out from under the tour and allow the user
     *        to click on unexpected buttons etc.
     */
    public void statusChanged(int systemID, boolean online, boolean unlockContainer)
    {
        StatusBeanModel statusModel = new StatusBeanModel();
        statusModel.setStatus(systemID, online);

        POSBaseBeanModel base = (POSBaseBeanModel)getModel();
        
        // Model is null as there is no screen yet displayed and calling a setModel will throw a ValetException, 
        // so call the OnlineStatusContainer.update directly to set the offline flag. 
        if (base == null)
        {
            base = new POSBaseBeanModel();
            OnlineStatusContainer.getSharedInstance().update(statusModel.getStatusContainer());
        }
        else
        {
            base.setStatusBeanModel(statusModel);
            base.setUnlockContainer(unlockContainer);

            // Send the data to the bean
            setModel(POSUIManagerIfc.SHOW_STATUS_ONLY, base);
        }

    }
    
    /* (non-Javadoc)
     * @see oracle.retail.stores.pos.ui.POSUIManagerIfc#showDialog(java.lang.String, oracle.retail.stores.foundation.manager.gui.UIModelIfc)
     */
    public void showDialog(String screenID, UIModelIfc beanModel)
    {
        ShowDialogModelValet valet = new ShowDialogModelValet(screenID, beanModel);
        transport(valet);
    }

    /* (non-Javadoc)
     * @see oracle.retail.stores.pos.ui.POSUIManagerIfc#showDialogAndWait(java.lang.String, oracle.retail.stores.foundation.manager.gui.UIModelIfc)
     */
    public void showDialogAndWait(String screenID, UIModelIfc beanModel)
    {
        showDialogAndWait(screenID, beanModel, false);
    }

    /* (non-Javadoc)
     * @see oracle.retail.stores.pos.ui.POSUIManagerIfc#showDialogAndWait(java.lang.String, oracle.retail.stores.foundation.manager.gui.UIModelIfc, boolean)
     */
    @Override
    public void showDialogAndWait(String screenID, UIModelIfc beanModel, boolean mailLetter)
    {
        ShowDialogModelValet valet = new ShowDialogModelValet(screenID, beanModel);
        valet.setWait(true);
        valet.setMailLetter(mailLetter);
        transport(valet);        
    }

    /* (non-Javadoc)
     * @see oracle.retail.stores.pos.ui.POSUIManagerIfc#showScreen(java.lang.String)
     */
    public void showScreen(String screenID)
    {
        ShowScreenValet valet = new ShowScreenValet(screenID);
        transport(valet);
    }

    /* (non-Javadoc)
     * @see oracle.retail.stores.pos.ui.POSUIManagerIfc#showScreen(java.lang.String, oracle.retail.stores.foundation.manager.gui.UIModelIfc)
     */
    public void showScreen(String screenID, UIModelIfc beanModel)
    {
        ShowScreenModelValet valet = new ShowScreenModelValet(screenID, beanModel);
        transport(valet);
    }

    /**
     * Add an object that will receive letters from this manager.
     *
     * @param address the address of the object to recieve letters
     */
    public void addLetterListener(MailboxAddress address)
    {
        AddLetterListenerValet valet = new AddLetterListenerValet(address);
        transport(valet);
    }

    /**
     * Remove an object that will receive letters from this manager.
     *
     * @param address the address of the object to recieve letters
     */
    public void removeLetterListener(MailboxAddress address)
    {
        RemoveLetterListenerValet valet = new RemoveLetterListenerValet(address);
        transport(valet);
    }

    /* (non-Javadoc)
     * @see oracle.retail.stores.pos.ui.POSUIManagerIfc#setLookAndFeel()
     */
    public void setLookAndFeel()
    {
        SetLookAndFeelValet valet = new SetLookAndFeelValet();
        transport(valet);
    }

    /*
     * (non-Javadoc)
     * @see oracle.retail.stores.pos.ui.POSUIManagerIfc#setApplicationMode(oracle.retail.stores.foundation.manager.gui.ApplicationMode)
     */
    @Override
    public void setApplicationMode(ApplicationMode applicationMode)
    {
        SetApplicationModeValet valet = new SetApplicationModeValet(applicationMode);
        transport(valet);
    }
}
