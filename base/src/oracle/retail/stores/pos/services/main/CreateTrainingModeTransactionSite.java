/* ===========================================================================
* Copyright (c) 2004, 2014, Oracle and/or its affiliates. All rights reserved.
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/main/CreateTrainingModeTransactionSite.java /main/19 2014/06/06 15:03:14 abondala Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    abondala  06/06/14 - move training mode from main screen to admin screen
 *    blarsen   09/11/12 - Merge project Echo (MPOS) into Trunk.
 *    blarsen   08/28/12 - Merge project Echo (MPOS) into trunk.
 *    mjwallac  04/24/12 - Fixes for Fortify redundant null check
 *    asinton   09/21/11 - Fixed training mode and transaction reentry mode
 *                         screens.
 *    cgreene   05/26/10 - convert to oracle packaging
 *    abondala  01/03/10 - update header date
 *    vchengeg  12/02/08 - Formatted EJournal Entries for bug 7588283
 *
 * ===========================================================================
 * $Log:
 *   3    360Commerce 1.2         3/31/2005 4:27:32 PM   Robert Pearse
 *   2    360Commerce 1.1         3/10/2005 10:20:26 AM  Robert Pearse
 *   1    360Commerce 1.0         2/11/2005 12:10:14 PM  Robert Pearse
 *
 *  Revision 1.6  2004/10/06 14:43:43  kll
 *  @scr 7295: associate workstation's training mode status to journal management
 *
 *  Revision 1.5  2004/07/23 22:17:25  epd
 *  @scr 5963 (ServicesImpact) Major update.  Lots of changes to fix RegisterADO singleton references and fix training mode
 *
 *  Revision 1.4  2004/04/13 15:23:47  tfritz
 *  @scr 3884 - More training mode changes
 *
 *  Revision 1.3  2004/04/07 17:50:56  tfritz
 *  @scr 3884 - Training Mode rework
 *
 *  Revision 1.2  2004/03/17 19:05:42  tfritz
 *  @scr 3884 - Training Mode code review changes.
 *
 *  Revision 1.1  2004/03/14 21:12:40  tfritz
 *  @scr 3884 - New Training Mode Functionality
 *
 * ===========================================================================
 */
package oracle.retail.stores.pos.services.main;

//import oracle.retail.stores.pos.ado.store.StoreFactory;
import oracle.retail.stores.common.utility.Util;
import oracle.retail.stores.domain.DomainGateway;
import oracle.retail.stores.domain.transaction.TransactionIfc;
import oracle.retail.stores.foundation.manager.data.DataException;
import oracle.retail.stores.foundation.manager.device.DeviceException;
import oracle.retail.stores.foundation.manager.gui.ApplicationMode;
import oracle.retail.stores.foundation.manager.ifc.JournalManagerIfc;
import oracle.retail.stores.foundation.manager.ifc.ParameterManagerIfc;
import oracle.retail.stores.foundation.manager.ifc.UIManagerIfc;
import oracle.retail.stores.foundation.manager.parameter.ParameterException;
import oracle.retail.stores.foundation.tour.application.Letter;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.pos.manager.ifc.TransactionUtilityManagerIfc;
import oracle.retail.stores.pos.services.PosSiteActionAdapter;
import oracle.retail.stores.pos.services.admin.AdminCargo;
import oracle.retail.stores.pos.services.common.CommonLetterIfc;
import oracle.retail.stores.pos.ui.POSUIManagerIfc;
import oracle.retail.stores.utility.I18NConstantsIfc;
import oracle.retail.stores.utility.I18NHelper;
import oracle.retail.stores.utility.JournalConstantsIfc;

//--------------------------------------------------------------------------
/**
 Creates a Training Mode Transaction
 @version $Revision: /main/19 $
 **/
//--------------------------------------------------------------------------
@SuppressWarnings("serial")
public class CreateTrainingModeTransactionSite extends PosSiteActionAdapter
{
    /**
     revision number
     **/
    public static final String revisionNumber = "$Revision: /main/19 $";
    /**
     * Journal Training Mode Transactions.
     */
    protected String JOURNAL_TRAINING_MODE = "SendTrainingModeTransactionsToJournal";

    //----------------------------------------------------------------------
    /**
     Determines if training mode is being entered or exited
     and creates a training mode transaction.
     <P>
     @param bus the bus arriving at this site
     **/
    //----------------------------------------------------------------------
    public void arrive(BusIfc bus)
    {
        TransactionUtilityManagerIfc utility = (TransactionUtilityManagerIfc) bus.getManager(TransactionUtilityManagerIfc.TYPE);

        AdminCargo cargo = (AdminCargo) bus.getCargo();
        boolean trainingModeOn = !cargo.isTrainingMode();

        // create transaction
        TransactionIfc transaction =
            DomainGateway.getFactory().getTransactionInstance();

        if (trainingModeOn)
        {
            transaction.setTransactionType(TransactionIfc.TYPE_ENTER_TRAINING_MODE);
        }
        else
        {
            transaction.setTransactionType(TransactionIfc.TYPE_EXIT_TRAINING_MODE);
        }
        utility.initializeTransaction((TransactionIfc)transaction, -1);
        transaction.setTimestampEnd();
        transaction.setTransactionStatus(TransactionIfc.STATUS_COMPLETED);

        // Save the transaction to the database
        try
        {
            /*
             * Write a journal entry
             */
            JournalManagerIfc journal = (JournalManagerIfc)bus.getManager(JournalManagerIfc.TYPE);

            // toggle JournalManager's trainingModeStatus for TransactionIfc.TYPE_EXIT_TRAINING_MODE
            if (transaction.getTransactionType() == TransactionIfc.TYPE_EXIT_TRAINING_MODE && journal != null)
            {
                journal.setTrainingModeStatus(false);
            }
            if (journal != null)
            {
                if (cargo.getOperator() != null)
                {
                    /*
                     * Write an entry to the journal to indicate that
                     * training mode was changed
                     */
                    StringBuilder trainingModeDescription = new StringBuilder();
                    if(trainingModeOn)
                    {
                        trainingModeDescription.append(I18NHelper.getString(I18NConstantsIfc.EJOURNAL_TYPE, JournalConstantsIfc.TRAINING_MODE_BEGINNING, null));
                        trainingModeDescription.append(Util.EOL);
                        trainingModeDescription.append(I18NHelper.getString(I18NConstantsIfc.EJOURNAL_TYPE, JournalConstantsIfc.TRAINING_MODE_HEADING, null));
                        trainingModeDescription.append(Util.EOL);
                    }
                    else
                    {
                        trainingModeDescription.append(I18NHelper.getString(I18NConstantsIfc.EJOURNAL_TYPE, JournalConstantsIfc.TRAINING_MODE_EXITING, null));
                        trainingModeDescription.append(Util.EOL);
                    }
                    journal.journal(cargo.getOperator().getLoginID(), null, trainingModeDescription.toString());
                }
            }

            // save the training mode transaction
            utility.saveTransaction(transaction);
            // update the hard totals
            utility.writeHardTotals();

            // update the JournalManager as to TrainingMode status
            ParameterManagerIfc pm = (ParameterManagerIfc)bus.getManager(ParameterManagerIfc.TYPE);
            boolean journalTrainingModeTransaction = false;
            try
            {
                String value = pm.getStringValue(JOURNAL_TRAINING_MODE);
                if (value.equalsIgnoreCase("Y"))
                {
                    journalTrainingModeTransaction = true;
                    if (journal != null)
                    {
                        journal.setJournalTrainingModeTransactions(journalTrainingModeTransaction);
                    }
                }
            }
            catch(ParameterException pe)
            {
                logger.error("Error retrieving parameter value: " + pe);
            }
            if (journal != null)
            {
                journal.setTrainingModeStatus(trainingModeOn);
            }

        }
        catch (DataException e)
        {
            logger.error(
                    e.toString());
        }
        // catch exception from hard totals
        catch (DeviceException e)
        {
            logger.error(
                    e.toString());
        }

        // Make sure to switch the state in cargo
        cargo.setTrainingMode(trainingModeOn);

        // update the UI
        POSUIManagerIfc ui = (POSUIManagerIfc)bus.getManager(UIManagerIfc.TYPE);
        if(trainingModeOn)
        {
            ui.setApplicationMode(ApplicationMode.TRAINING_MODE);
        }
        else
        {
            ui.setApplicationMode(ApplicationMode.NORMAL_MODE);
        }
        bus.mail(new Letter(CommonLetterIfc.SUCCESS), BusIfc.CURRENT);
    }
}
