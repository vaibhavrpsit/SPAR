/* ===========================================================================
* Copyright (c) 1998, 2013, Oracle and/or its affiliates. All rights reserved.
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/common/SaveRetailTransactionAisle.java /main/17 2013/12/30 11:44:42 cgreene Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    cgreene   12/27/13 - update error logging
 *    blarsen   03/29/12 - Add debug log.
 *    cgreene   03/16/12 - split transaction-methods out of utilitymanager
 *    cgreene   03/09/12 - add support for journalling queues by current
 *                         register
 *    cgreene   08/09/11 - formatting and removed deprecated code
 *    cgreene   06/07/11 - update to first pass of removing pospal project
 *    cgreene   02/15/11 - move constants into interfaces and refactor
 *    abhayg    08/13/10 - STOPPING POS TRANSACTION IF REGISTER HDD IS FULL
 *    sgu       07/22/10 - undepricate this class
 *    cgreene   05/26/10 - convert to oracle packaging
 *    cgreene   02/01/10 - removed and updated deprecated methods in Register
 *                         class
 *    abondala  01/03/10 - update header date
 *
 * ===========================================================================
 * $Log:
 *    3    360Commerce 1.2         3/31/2005 4:29:50 PM   Robert Pearse
 *    2    360Commerce 1.1         3/10/2005 10:25:03 AM  Robert Pearse
 *    1    360Commerce 1.0         2/11/2005 12:14:03 PM  Robert Pearse
 *
 *   Revision 1.15.2.1  2005/01/20 21:03:44  bwf
 *   @scr 5126 Handle QueueFull letter inorder to go back to main options screen.
 *
 *   Revision 1.15  2004/09/17 23:00:01  rzurga
 *   @scr 7218 Move CPOI screen name constants to CIDAction to make it more generic
 *
 *   Revision 1.14  2004/07/29 21:01:59  aachinfiev
 *   @scr 2746 - Fixed problem with Layaway pickup wasn't voided in training mode.
 *
 *   Revision 1.13  2004/07/26 13:02:38  kll
 *   @scr 5126: implementing different flow for DataException.QUEUE_FULL_ERROR
 *
 *   Revision 1.12  2004/07/24 16:43:52  rzurga
 *   @scr 6463 Items are showing on CPOI sell item from previous transaction
 *   Remove newly introduced automatic hiding of non-active CPOI screens
 *   Enable clearing of non-visible CPOI screens
 *   Improve appearance by clearing first, then setting fields and finally showing the CPOI screen
 *
 *   Revision 1.11  2004/07/17 19:39:08  rsachdeva
 *   @scr 5960 CPOI Thank You
 *
 *   Revision 1.10  2004/06/16 12:44:32  khassen
 *   @scr 5599, 5600 - Last transaction wasn't available in training mode.
 *
 *   Revision 1.9  2004/06/14 22:43:59  cdb
 *   @scr 5318 Updated such that failure writing to hard totals causes
 *   the application to exit. Main Functional Requirements v2 section 2.3 # 40.
 *
 *   Revision 1.8  2004/06/09 17:02:10  dfierling
 *   @scr 5280, 5281 - Fixed for not saving transactions in training mode
 *
 *   Revision 1.7  2004/06/03 14:47:43  epd
 *   @scr 5368 Update to use of DataTransactionFactory
 *
 *   Revision 1.6  2004/04/21 13:38:50  jriggins
 *   @scr 3979 Removed UpdatePriceAdjustedItemsDataTransaction reference
 *
 *   Revision 1.5  2004/04/20 12:52:57  jriggins
 *   @scr 3979 Added UpdatePriceAdjustedItemsDataTransaction and associated operations
 *
 *   Revision 1.4  2004/04/19 15:57:08  tmorris
 *   @scr 4332 -Replaced direct instantiation(new) with Factory call.
 *
 *   Revision 1.3  2004/02/12 16:49:08  mcs
 *   Forcing head revision
 *
 *   Revision 1.2  2004/02/11 20:56:28  rhafernik
 *   @scr 0 Log4J conversion and code cleanup
 *
 *   Revision 1.1.1.1  2004/02/11 01:04:14  cschellenger
 *   updating to pvcs 360store-current
 *
 *
 *
 *    Rev 1.2   Feb 09 2004 10:17:18   baa
 * deprecated
 *
 *    Rev 1.1   08 Nov 2003 01:33:00   baa
 * cleanup -sale refactoring
 *
 *    Rev 1.0   Aug 29 2003 15:54:48   CSchellenger
 * Initial revision.
 *
 *    Rev 1.1   Sep 25 2002 10:29:16   RSachdeva
 * Code Conversion
 * Resolution for POS SCR-1740: Code base Conversions
 *
 *    Rev 1.0   Apr 29 2002 15:35:48   msg
 * Initial revision.
 *
 *    Rev 1.0   Mar 18 2002 11:23:20   msg
 * Initial revision.
 *
 *    Rev 1.2   Feb 27 2002 17:27:26   mpm
 * Restructured end-of-transaction processing.
 * Resolution for POS SCR-1440: Enhance end-of-transaction processing for performance reasons
 *
 *    Rev 1.1   29 Jan 2002 09:54:18   epd
 * Deprecated all methods using accumulate parameter and added new methods without this parameter.  Also removed all reference to the parameter wherever used.
 * (The behavior is to accumulate totals)
 * Resolution for POS SCR-770: Remove the accumulate parameter and all references to it.
 *
 *    Rev 1.0   Sep 21 2001 11:14:10   msg
 * Initial revision.
 *
 *    Rev 1.1   Sep 17 2001 13:06:04   msg
 * header update
 * ===========================================================================
 */
package oracle.retail.stores.pos.services.common;

import java.util.ArrayList;

import oracle.retail.stores.domain.arts.DataTransactionFactory;
import oracle.retail.stores.domain.arts.DataTransactionKeys;
import oracle.retail.stores.domain.arts.UpdateReturnedItemsCommandDataTransaction;
import oracle.retail.stores.domain.financial.FinancialTotalsIfc;
import oracle.retail.stores.domain.financial.RegisterIfc;
import oracle.retail.stores.domain.financial.TillIfc;
import oracle.retail.stores.domain.lineitem.AbstractTransactionLineItemIfc;
import oracle.retail.stores.domain.transaction.OrderTransactionIfc;
import oracle.retail.stores.domain.transaction.RetailTransactionIfc;
import oracle.retail.stores.domain.transaction.SaleReturnTransactionIfc;
import oracle.retail.stores.domain.transaction.VoidTransactionIfc;
import oracle.retail.stores.foundation.manager.data.DataException;
import oracle.retail.stores.foundation.manager.device.DeviceException;
import oracle.retail.stores.foundation.manager.ifc.UIManagerIfc;
import oracle.retail.stores.foundation.tour.application.Letter;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.pos.manager.ifc.TransactionUtilityManagerIfc;
import oracle.retail.stores.pos.manager.ifc.UtilityManagerIfc;
import oracle.retail.stores.pos.services.PosLaneActionAdapter;
import oracle.retail.stores.pos.ui.DialogScreensIfc;
import oracle.retail.stores.pos.ui.POSUIManagerIfc;
import oracle.retail.stores.pos.ui.beans.DialogBeanModel;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;

/**
 * This aisle does all of the necessary processing needed when saving a
 * transaction.
 *
 * @version $Revision: /main/17 $
 */
public class SaveRetailTransactionAisle extends PosLaneActionAdapter
{
    private static final long serialVersionUID = -8679816756068789293L;

    /**
     * lane name constants
     */
    public static final String LANENAME = "SaveRetailTransactionAisle";

    /**
     * logger
     */
    public static final Logger logger = Logger.getLogger(SaveRetailTransactionAisle.class);

    /**
     * thank you tag
     */
    public static final String CPOI_THANK_YOU_TAG = "ThankYou";

    /**
     * thank you default
     */
    public static final String CPOI_THANK_YOU_DEFAULT = "Thank You";

    /**
     * Saves the transaction to database and updates Financial Totals. A letter
     * is sent if it succeeds.
     *
     * @param bus the bus traversing this lane
     */
    @Override
    public void traverse(BusIfc bus)
    {
        RetailTransactionCargoIfc rtCargo = (RetailTransactionCargoIfc) bus.getCargo();
        RetailTransactionIfc trans = rtCargo.getRetailTransaction();
        POSUIManagerIfc ui = (POSUIManagerIfc) bus.getManager(UIManagerIfc.TYPE);
        TransactionUtilityManagerIfc utility = (TransactionUtilityManagerIfc)bus.getManager(TransactionUtilityManagerIfc.TYPE);
        boolean saveTransSuccess   = true;
        boolean saveHardTotSuccess = true;

        // Save the transaction to persistent storage
        // and update financial totals
        try
        {
            AbstractFinancialCargoIfc afCargo = (AbstractFinancialCargoIfc) bus.getCargo();
            TillIfc                   till = afCargo.getRegister().getCurrentTill();
            RegisterIfc           register = afCargo.getRegister();
            FinancialTotalsIfc totals = null;

            // Don't do this if we are in training mode
            if (!trans.isTrainingMode())
            {
                // This updates the financial totals for the register
                totals = afCargo.getRegister().addTransaction(trans);
            }

            if (logger.isDebugEnabled())
            {
                String wsID = register.getWorkstation().getWorkstationID();
                long sequenceNumber = trans.getTransactionSequenceNumber();
                logger.debug("(save trans): workstation ID: " + wsID + ",  trans seq num: " + sequenceNumber);
            }

            utility.saveTransaction(trans, totals, till, register);

            afCargo.setLastReprintableTransactionID(trans.getTransactionID());
            utility.writeHardTotals();
        }
        catch (DataException ex)
        {
        	saveTransSuccess = false;
            UtilityManagerIfc util = (UtilityManagerIfc)bus.getManager(UtilityManagerIfc.TYPE);
            DialogBeanModel dialogModel = util.createErrorDialogBeanModel(ex);

            // display dialog
            ui.showScreen(POSUIManagerIfc.DIALOG_TEMPLATE,dialogModel);
            logger.error("DataException occurred saving transaction.", ex);
        }
        // catch exception in writing hard totals
        catch (DeviceException dex)
        {
            saveHardTotSuccess = false;
            // set bean model
            DialogBeanModel model = new DialogBeanModel();
            model.setResourceID("WriteHardTotalsError");
            model.setButtonLetter(DialogScreensIfc.BUTTON_OK, "Failure");
            model.setType(DialogScreensIfc.ERROR);

            // show dialog
            ui.showScreen(POSUIManagerIfc.DIALOG_TEMPLATE, model);
            logger.error("DeviceException writing hardTotals.", dex);
        }

        // These additional updates should be in a single transaction,
        // but can't be because they might go to different locations.
        // They CANNOT be in the same try block because if they are, and
        // the second one fails, the transaction sequence numbers get out
        // of wack.
        if (saveTransSuccess && trans instanceof VoidTransactionIfc)
        {
            try
            {
                // If this is a void transaciton, call this object to update
                // the original Transaction.
                AbstractTransactionLineItemIfc[] itemArray =
                    ((VoidTransactionIfc) trans).getLineItems();

                UpdateReturnedItemsCommandDataTransaction dbTrans = null;

                dbTrans = (UpdateReturnedItemsCommandDataTransaction) DataTransactionFactory.create(DataTransactionKeys.UPDATE_RETURNED_ITEMS_COMMAND_DATA_TRANSACTION);
                dbTrans.updateVoidedReturnedItems(itemArray);
            }
            catch (DataException e)
            {
                logger.error( "Could not update Voided Return Line Items.");
                logger.error( "" + e + "");
            }
        }

        // If the db write is ok, update the origninal transaction.
        if (saveTransSuccess)
        {
            // SCR: 2746
            // the following call needs to take place in both live & training modes
            // if it doesn't, values in DB get messed up and post void doesn't
            // work for Layway Pickup.
            processOriginalTransactions(rtCargo.getOriginalReturnTransactions(),
                                        bus.getServiceName());

            if (saveHardTotSuccess)
            {
                // if all updates succeeded, mail the continue letter.
                bus.mail(new Letter(CommonLetterIfc.CONTINUE), BusIfc.CURRENT);
            }
        }

    }

    /**
     * Saves the original transactions to database if there are any. This is not
     * required to complete the transaction, so if there is an error log it but
     * continue on.
     *
     * @param transactions array of transactions
     * @param serviceName service name (used for log)
     */
    public void processOriginalTransactions(SaleReturnTransactionIfc[] transactions, String serviceName)
    {
        if (transactions != null)
        {
            SaleReturnTransactionIfc[] validTransactions = getValidTransactions(transactions);
            if (validTransactions.length > 0)
            {
                try
                {
                    // Return Items
                    UpdateReturnedItemsCommandDataTransaction dbTrans = null;
    
                    dbTrans = (UpdateReturnedItemsCommandDataTransaction) DataTransactionFactory.create(
                            DataTransactionKeys.UPDATE_RETURNED_ITEMS_COMMAND_DATA_TRANSACTION);
    
                    dbTrans.updateReturnedItems(validTransactions);
    
                }
                catch (DataException de)
                {
                    logger.error(de.toString());
                }
            }
        }
    }

    /*
     * Cross channel orders do not have complete transaction ID information.  This method filters
     * out the transactions that cannot update the transaction in the store DB.  The return of
     * a completed order line item does not depend on quantity returned in the Transaction Line
     * Item table.  Orders maintain their own count of quantity returned. 
     */
    protected SaleReturnTransactionIfc[] getValidTransactions(SaleReturnTransactionIfc[] transactions)
    {
        ArrayList<SaleReturnTransactionIfc> validTransactions = new ArrayList<SaleReturnTransactionIfc>();
        for(SaleReturnTransactionIfc transaction: transactions)
        {
            if (transaction instanceof OrderTransactionIfc)
            {
                if (transaction.getTransactionIdentifier() != null &&
                    !StringUtils.isEmpty(transaction.getTransactionIdentifier().getStoreID()) &&
                    !StringUtils.isEmpty(transaction.getTransactionIdentifier().getWorkstationID()) &&
                    transaction.getTransactionIdentifier().getSequenceNumber() != -1)
                {
                    validTransactions.add(transaction);
                }
            }
            else
            {
                validTransactions.add(transaction);
            }
        }
        
        
        SaleReturnTransactionIfc[] validTransactionArray = new SaleReturnTransactionIfc[validTransactions.size()];
        if (validTransactions.size() > 0)
        {
            validTransactions.toArray(validTransactionArray);
        }
        return validTransactionArray;
    }
}