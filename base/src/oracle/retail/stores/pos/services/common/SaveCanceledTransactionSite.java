/* ===========================================================================
* Copyright (c) 2004, 2014, Oracle and/or its affiliates. All rights reserved.
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/common/SaveCanceledTransactionSite.java /main/22 2014/03/24 10:54:28 ohorne Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    ohorne    03/21/14 - ExternalOrderManager.cancel() now requires workstationId
 *    mkutiana  01/15/13 - timeout at Sale Screen reworked for extracted
 *                         cancelSale tour
 *    blarsen   08/27/12 - Merge from project Echo (MPOS) into trunk.
 *    cgreene   03/16/12 - split transaction-methods out of utilitymanager
 *    cgreene   03/09/12 - add support for journalling queues by current
 *                         register
 *    sgu       06/22/12 - cancel unused order ids during transaction cancel
 *    sgu       06/22/12 - refactor order id assignment
 *    cgreene   09/19/11 - move ExternalOrderManager to domain
 *    cgreene   09/16/11 - repackage commext
 *    blarsen   12/07/10 - Prevented site from cancelling/saving a transaction
 *                         that is already completed. This was happening when
 *                         the cash drawer is left open past the transaction
 *                         timeout period.
 *    abhayg    08/13/10 - STOPPING POS TRANSACTION IF REGISTER HDD IS FULL
 *    abhayg    08/04/10 - TRANSACTION NUMBER IS SKIPPED DUE TO APPLICATION
 *                         TIME OUT
 *    acadar    07/29/10 - performance logging
 *    ohorne    07/08/10 - external order is not canceled when in training mode
 *    sgu       06/16/10 - add cancel and reject order apis
 *    cgreene   05/26/10 - convert to oracle packaging
 *    acadar    05/21/10 - additional changes for process order flow
 *    abondala  01/03/10 - update header date
 *
 * ===========================================================================
 * $Log:
 *    5    360Commerce 1.4         3/12/2008 11:18:34 PM  Manikandan Chellapan
 *         CR#30670 Modified redeem service to save transaction after timeout
 *    4    360Commerce 1.3         5/14/2007 2:32:57 PM   Alan N. Sinton  CR
 *         26486 - EJournal enhancements for VAT.
 *    3    360Commerce 1.2         3/31/2005 4:29:49 PM   Robert Pearse
 *    2    360Commerce 1.1         3/10/2005 10:25:02 AM  Robert Pearse
 *    1    360Commerce 1.0         2/11/2005 12:14:02 PM  Robert Pearse
 *
 *   Revision 1.5.4.1  2004/10/20 19:34:16  jdeleau
 *   @scr 7382 Make sure transactions are marked as cancelled
 *   when they timeout.
 *
 *   Revision 1.5  2004/03/15 21:55:15  jdeleau
 *   @scr 4040 Automatic logoff after timeout
 *
 *   Revision 1.4  2004/03/03 23:15:08  bwf
 *   @scr 0 Fixed CommonLetterIfc deprecations.
 *
 *   Revision 1.3  2004/02/12 16:48:02  mcs
 *   Forcing head revision
 *
 *   Revision 1.2  2004/02/11 21:19:59  rhafernik
 *   @scr 0 Log4J conversion and code cleanup
 *
 *   Revision 1.1.1.1  2004/02/11 01:04:11  cschellenger
 *   updating to pvcs 360store-current
 *
 *
 *
 *    Rev 1.0   Jan 19 2004 14:04:24   DCobb
 * Initial revision.
 * ===========================================================================
 */
package oracle.retail.stores.pos.services.common;

import oracle.retail.stores.commerceservices.logging.PerformanceLevel;
import oracle.retail.stores.domain.DomainGateway;
import oracle.retail.stores.domain.financial.FinancialTotalsIfc;
import oracle.retail.stores.domain.financial.RegisterIfc;
import oracle.retail.stores.domain.financial.TillIfc;
import oracle.retail.stores.domain.manager.externalorder.ExternalOrderException;
import oracle.retail.stores.domain.manager.externalorder.ExternalOrderManagerIfc;
import oracle.retail.stores.domain.manager.order.OrderManagerIfc;
import oracle.retail.stores.domain.transaction.OrderTransactionIfc;
import oracle.retail.stores.domain.transaction.SaleReturnTransactionIfc;
import oracle.retail.stores.domain.transaction.TenderableTransactionIfc;
import oracle.retail.stores.domain.transaction.TransactionConstantsIfc;
import oracle.retail.stores.domain.transaction.TransactionIfc;
import oracle.retail.stores.domain.transaction.TransactionTotalsIfc;
import oracle.retail.stores.foundation.manager.data.DataException;
import oracle.retail.stores.foundation.manager.device.DeviceException;
import oracle.retail.stores.foundation.manager.ifc.JournalManagerIfc;
import oracle.retail.stores.foundation.manager.ifc.UIManagerIfc;
import oracle.retail.stores.foundation.tour.application.Letter;
import oracle.retail.stores.foundation.tour.gate.Gateway;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.foundation.tour.service.SessionBusIfc;
import oracle.retail.stores.pos.device.POSDeviceActions;
import oracle.retail.stores.pos.journal.JournalFormatterManagerIfc;
import oracle.retail.stores.pos.manager.ifc.TransactionUtilityManagerIfc;
import oracle.retail.stores.pos.manager.ifc.UtilityManagerIfc;
import oracle.retail.stores.pos.services.PosSiteActionAdapter;
import oracle.retail.stores.pos.services.order.common.OrderUtilities;
import oracle.retail.stores.pos.services.sale.SaleCargo;
import oracle.retail.stores.pos.services.sale.SaleCargoIfc;
import oracle.retail.stores.pos.ui.POSUIManagerIfc;
import oracle.retail.stores.pos.ui.beans.DialogBeanModel;

/**
 * This site saves the current canceled transaction.
 *
 */
@SuppressWarnings("serial")
public class SaveCanceledTransactionSite extends PosSiteActionAdapter
{

    /**
     * Saves the current canceled transaction.
     *
     * @param bus Service Bus
     */
    @Override
    public void arrive(BusIfc bus)
    {
    	POSUIManagerIfc ui = (POSUIManagerIfc) bus.getManager(UIManagerIfc.TYPE);
        TransactionUtilityManagerIfc utility = (TransactionUtilityManagerIfc) bus.getManager(TransactionUtilityManagerIfc.TYPE);

        // retrieve cargo, transaction
        TenderableTransactionCargoIfc tenderableCargo =
          (TenderableTransactionCargoIfc) bus.getCargo();
        AbstractFinancialCargoIfc afCargo = (AbstractFinancialCargoIfc)bus.getCargo();

        TenderableTransactionIfc transaction =
          tenderableCargo.getTenderableTransaction();
        boolean wasTransCancelled = false;

        // This happens when a timeout occurs after the the transactions is completed
        //    (say, if the cash drawer isn't closed for 15 minutes).
        boolean transCompleted = transaction != null &&
                transaction.getTransactionStatus() == TransactionConstantsIfc.STATUS_COMPLETED;

        if (transaction != null && !transCompleted && !transaction.isCanceledTransactionSaved())
        {
            wasTransCancelled = true;

            // set canceled status, normally this is set in the PrintCancelTransaction
            // site, but in the case of timeout, printCancelTransaction does not
            // get called so the status must be set here.
            transaction.setTransactionStatus(TransactionIfc.STATUS_CANCELED);
            if(transaction.getTimestampEnd() == null)
            {
                transaction.setTimestampEnd();
            }

            RegisterIfc register = afCargo.getRegister();
            register.addNumberCancelledTransactions(1);
            TransactionTotalsIfc totals = transaction.getTransactionTotals();
            register.addAmountCancelledTransactions
                (totals.getSubtotal().subtract(totals.getDiscountTotal()).abs());

            if (afCargo instanceof SaleCargoIfc)
            {
                // The following line is particular to sale service.
                ((SaleCargoIfc)afCargo).setItemModifiedIndex(SaleCargo.NO_SELECTION);
            }

            // write a transaction to the database
            try
            {
                // Create a totals object that contains only the cancel
                // transaction numbers.  Set the total on clones of the register
                // and till.  These will be used to accumulate the data
                // in the database.
                TillIfc    till = (TillIfc)register.getCurrentTill().clone();
                RegisterIfc reg = (RegisterIfc)register.clone();
                FinancialTotalsIfc accTotals =
                  DomainGateway.getFactory().getFinancialTotalsInstance();
                accTotals.setNumberCancelledTransactions(1);
                accTotals.setAmountCancelledTransactions
                  (totals.getSubtotal().subtract
                    (totals.getDiscountTotal()).abs());
                till.setTotals(accTotals);
                reg.setTotals(accTotals);

                // Pass the transaction, a null totals object, the till,
                // register, and false indicating that the transaction has
                // not completed its journaling yet.
                utility.saveTransaction(transaction, null, till, reg, false);

                //set the flag to true to indicate that the transaction has been saved
                transaction.setCanceledTransactionSaved(true);

                // Cancel unused order id for an order initiate transaction
                if (transaction.getTransactionType() == TransactionIfc.TYPE_ORDER_INITIATE)
                {
                    OrderManagerIfc orderMgr = (OrderManagerIfc)bus.getManager(OrderManagerIfc.TYPE);
                    OrderUtilities.cancelNewOrderIDs((OrderTransactionIfc)transaction, orderMgr);
                }

                //if transaction contains an external order, call the ExternalOrder API to unlock the order
                if(transaction instanceof SaleReturnTransactionIfc
                        && ((SaleReturnTransactionIfc)transaction).hasExternalOrder()
                        && !register.getWorkstation().isTrainingMode())
                {
                    ExternalOrderManagerIfc externalOrderManager = (ExternalOrderManagerIfc)bus.getManager(ExternalOrderManagerIfc.TYPE);

                    String orderId = ((SaleReturnTransactionIfc)transaction).getExternalOrderID();
                    //performance logging
                    perfLogger.log(PerformanceLevel.PERF, "SaveCanceledTransactionSite: cancel() starts  for order id: " + orderId);

                    //cancel order
                    externalOrderManager.cancel(orderId, transaction.getWorkstation().getWorkstationID());

                    //performance logging
                    perfLogger.log(PerformanceLevel.PERF, "SaveCanceledTransactionSite: cancel() ends  for order id: " + orderId);
                }
            }
            catch (DataException de)
            {
            	wasTransCancelled = false;
                UtilityManagerIfc util = (UtilityManagerIfc) bus.getManager(UtilityManagerIfc.TYPE);
            	DialogBeanModel dialogModel = util.createErrorDialogBeanModel(de);

                // display dialog
                ui.showScreen(POSUIManagerIfc.DIALOG_TEMPLATE,dialogModel);
                logger.error( "" + de + "");
            }
            catch (ExternalOrderException eoe)
            {
                logger.error("External Order was not unlocked, need to manually unlock the order", eoe);
            }
        }

        // if the transaction was cancelled, then update hard totals, write ejournal, update line display
        if (wasTransCancelled)
        {
            // write the hard totals
            try
            {
                utility.writeHardTotals();
            }
            catch (Exception e)
            {
                logger.error( "Unable to save hard totals.");
                logger.error( "" + e + "");
            }

            // print journal
            JournalManagerIfc journal =
                (JournalManagerIfc)bus.getManager(JournalManagerIfc.TYPE);
            JournalFormatterManagerIfc formatter =
                (JournalFormatterManagerIfc)Gateway.getDispatcher().getManager(JournalFormatterManagerIfc.TYPE);

            if (journal != null && formatter != null)
            {
                String cancelJournal = formatter.journalCanceledTransaction(transaction);
                String transactionID = transaction.getTransactionID();
                // Adding transaction tax total for cancel journal
                journal.journal(afCargo.getOperator().getLoginID(),
                                transactionID,
                                cancelJournal);
                if (logger.isInfoEnabled())
                {
                    logger.info("Transaction " + transactionID + " Canceled");
                }
                utility.completeTransactionJournaling(transaction);
            }
            else
            {
                logger.error( "No JournalManager found");
            }

            //clear line display device
	        try
	        {
	            POSDeviceActions pda = new POSDeviceActions((SessionBusIfc) bus);
	            pda.clearText();
	        }
	        catch (DeviceException e)
	        {
	            logger.warn("Unable to use Line Display: " + e.getMessage() + "");
	        }
        }

        
        // Want all other sites to be able to realize they have a timed out transaction
        if(bus.getCurrentLetter().getName().equals(CommonLetterIfc.TIMEOUT))
        {
            if(bus.getCargo() instanceof TimedCargoIfc)
            {
                ((TimedCargoIfc) bus.getCargo()).setTimeout(true);
            }
            else
            {
                logger.warn("Cargo was not prepared for timeout"+bus.getCargo().getClass().getName());
            }
            //Send a Timeout letter
            bus.mail(new Letter(CommonLetterIfc.TIMEOUT), BusIfc.CURRENT);
        }
        else if (bus.getCargo() instanceof TimedCargoIfc && ((TimedCargoIfc) bus.getCargo()).isTimeout())
        {
            //Send a Timeout letter
            bus.mail(new Letter(CommonLetterIfc.TIMEOUT), BusIfc.CURRENT);
        }
        else
        {
            //Send a Continue Letter, or possibly TimeoutComplete
            bus.mail(new Letter(CommonLetterIfc.CONTINUE), BusIfc.CURRENT);
        }

    }

}
