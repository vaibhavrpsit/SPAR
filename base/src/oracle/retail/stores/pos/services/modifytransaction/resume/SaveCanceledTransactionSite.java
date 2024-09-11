/* ===========================================================================
* Copyright (c) 2004, 2014, Oracle and/or its affiliates. All rights reserved.
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/modifytransaction/resume/SaveCanceledTransactionSite.java /main/16 2014/05/14 14:41:28 cgreene Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    cgreene   05/14/14 - rename retrieve to resume
 *    jswan     06/21/13 - Modified to perform the status update of an Order in
 *                         the context of a transaction.
 *    icole     10/09/12 - Removed call to deprecated method,
 *                         updateTransactionStatus which is no longer
 *                         configured.
 *    cgreene   03/16/12 - split transaction-methods out of utilitymanager
 *    cgreene   03/09/12 - add support for journalling queues by current
 *                         register
 *    cgreene   05/26/10 - convert to oracle packaging
 *    jswan     01/21/10 - Fixed comments.
 *    jswan     01/21/10 - Fix an issue in which a returned gift card can be
 *                         modified during the period in which the transaction
 *                         has been suspended.
 *    abondala  01/03/10 - update header date
 *
 * ===========================================================================
 * $Log:
 *    4    360Commerce 1.3         5/14/2007 2:32:57 PM   Alan N. Sinton  CR
 *         26486 - EJournal enhancements for VAT.
 *    3    360Commerce 1.2         3/31/2005 4:29:49 PM   Robert Pearse   
 *    2    360Commerce 1.1         3/10/2005 10:25:01 AM  Robert Pearse   
 *    1    360Commerce 1.0         2/11/2005 12:14:02 PM  Robert Pearse   
 *
 *   Revision 1.7  2004/06/03 14:47:43  epd
 *   @scr 5368 Update to use of DataTransactionFactory
 *
 *   Revision 1.6  2004/04/20 13:17:06  tmorris
 *   @scr 4332 -Sorted imports
 *
 *   Revision 1.5  2004/04/14 15:17:10  pkillick
 *   @scr 4332 -Replaced direct instantiation(new) with Factory call.
 *
 *   Revision 1.4  2004/02/24 16:21:29  cdb
 *   @scr 0 Remove Deprecation warnings. Cleaned code.
 *
 *   Revision 1.3  2004/02/12 16:51:12  mcs
 *   Forcing head revision
 *
 *   Revision 1.2  2004/02/11 21:51:45  rhafernik
 *   @scr 0 Log4J conversion and code cleanup
 *
 *   Revision 1.1.1.1  2004/02/11 01:04:18  cschellenger
 *   updating to pvcs 360store-current
 *
 *
 * 
 *    Rev 1.0   Jan 20 2004 16:25:00   DCobb
 * Initial revision.
 * ===========================================================================
 */
package oracle.retail.stores.pos.services.modifytransaction.resume;

import oracle.retail.stores.domain.DomainGateway;
import oracle.retail.stores.domain.financial.FinancialTotalsIfc;
import oracle.retail.stores.domain.financial.RegisterIfc;
import oracle.retail.stores.domain.financial.TillIfc;
import oracle.retail.stores.domain.transaction.LayawayTransactionIfc;
import oracle.retail.stores.domain.transaction.OrderTransactionIfc;
import oracle.retail.stores.domain.transaction.RetailTransactionIfc;
import oracle.retail.stores.domain.transaction.StatusChangeTransactionIfc;
import oracle.retail.stores.domain.transaction.TransactionIfc;
import oracle.retail.stores.domain.transaction.TransactionSummaryIfc;
import oracle.retail.stores.domain.transaction.TransactionTotalsIfc;
import oracle.retail.stores.foundation.manager.data.DataException;
import oracle.retail.stores.foundation.manager.device.DeviceException;
import oracle.retail.stores.foundation.manager.ifc.JournalManagerIfc;
import oracle.retail.stores.foundation.tour.application.Letter;
import oracle.retail.stores.foundation.tour.gate.Gateway;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.pos.journal.JournalFormatterManagerIfc;
import oracle.retail.stores.pos.manager.ifc.TransactionUtilityManagerIfc;
import oracle.retail.stores.pos.services.PosSiteActionAdapter;
import oracle.retail.stores.pos.services.common.CommonLetterIfc;

/**
 * Saves the canceled transaction.
 */
@SuppressWarnings("serial")
public class SaveCanceledTransactionSite extends PosSiteActionAdapter
{
    /**
     * site name constant
     */
    public static final String SITENAME = "SaveCanceledTransactionSite ";

    /**
     * Saves the canceled transaction.
     * 
     * @param bus the bus arriving at this site
     */
    @Override
    public void arrive(BusIfc bus)
    {
        String letterName = CommonLetterIfc.SUCCESS;

        // retrieve transaction data from cargo
        ModifyTransactionResumeCargo cargo =
            (ModifyTransactionResumeCargo) bus.getCargo();

        JournalManagerIfc journal = (JournalManagerIfc)
            bus.getManager(JournalManagerIfc.TYPE);
        // get transaction from cargo
        RetailTransactionIfc trans = cargo.getTransaction();
        
        // reset transaction to null so that it will not be added to transaction
        cargo.setTransaction(null);

        // Save the transaction to persistent storage
        try
        {
            // cancel existing transaction
            cancelTransaction(trans,
                              cargo,
                              journal,
                              bus.getServiceName(),
                              bus);

            /*cargo trans. should be null so that it will not be added to transaction
            However, if receipt is required for trans. cancellation, then trans. in
            cargo should be set to retrieveTransaction and retreive.xml adjusted for
            redirection to PrintReceipt site.
            cargo.setTransaction(retrieveTransaction);*/

            TransactionUtilityManagerIfc utility = (TransactionUtilityManagerIfc) bus.getManager(TransactionUtilityManagerIfc.TYPE);
            utility.writeHardTotals();
        }
        catch (DataException e)
        {
            cargo.setDataExceptionErrorCode(e.getErrorCode());
            letterName = CommonLetterIfc.DB_ERROR;
        }
        catch (DeviceException e)
        {
            letterName = CommonLetterIfc.HARD_TOTALS_ERROR;
        }

        bus.mail(new Letter(letterName), BusIfc.CURRENT);
    }

    /**
     * Cancel the suspended transaction.
     * 
     * @param retrieveTransaction retrieved transaction
     * @param cargo cargo class
     * @param journal JournalManagerIfc reference
     * @param serviceName service name used in logging
     * @exception DataException thrown if error occurs writing canceled
     *                transaction to database
     */
    public void cancelTransaction(RetailTransactionIfc retrieveTransaction,
            ModifyTransactionResumeCargo cargo,
            JournalManagerIfc journal, String serviceName, BusIfc bus) throws DataException
    {
        // update financials
        RegisterIfc register = cargo.getRegister();
        register.addNumberCancelledTransactions(1);
        TransactionTotalsIfc totals = retrieveTransaction.getTransactionTotals();
        register.addAmountCancelledTransactions
            (totals.getSubtotal().subtract(totals.getDiscountTotal()).abs());

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
          (totals.getSubtotal().subtract(totals.getDiscountTotal()).abs());
        till.setTotals(accTotals);
        reg.setTotals(accTotals);

        // set transaction status to canceled and update
        StatusChangeTransactionIfc transaction = DomainGateway.getFactory().
            getStatusChangeTransactionInstance();
        TransactionUtilityManagerIfc transactionUtility = (TransactionUtilityManagerIfc)bus.getManager(TransactionUtilityManagerIfc.TYPE);
        transactionUtility.initializeTransaction(transaction, -1, null);
        TransactionSummaryIfc summary = DomainGateway.getFactory().
            getTransactionSummaryInstance();
        summary.setTransactionStatus(TransactionIfc.STATUS_SUSPENDED_CANCELED);
        summary.setTransactionID(retrieveTransaction.getTransactionIdentifier());
        if (retrieveTransaction instanceof OrderTransactionIfc)
        {
            summary.setInternalOrderID(((OrderTransactionIfc) retrieveTransaction).getOrderID());
        }
        if (retrieveTransaction instanceof LayawayTransactionIfc)
        {
            summary.setLayawayID(((LayawayTransactionIfc) retrieveTransaction).getLayaway().getLayawayID());
        }
        
        transaction.addTransactionSummary(summary);
 
        JournalFormatterManagerIfc formatter =
            (JournalFormatterManagerIfc)Gateway.getDispatcher().getManager(JournalFormatterManagerIfc.TYPE);
        if (journal != null && formatter != null)
        {
            String transactionID = retrieveTransaction.getTransactionID();
            String taxCancel = formatter.journalCanceledSuspendedTransaction(retrieveTransaction);
            journal.journal(cargo.getOperator().getLoginID(),transactionID,taxCancel);
            if (logger.isInfoEnabled()) logger.info("Transaction " + transactionID + " canceled");
        }
        else
        {
            logger.error("No JournalManager found");
        }
        transactionUtility.saveTransaction(transaction, till, reg);
    }                                   // end cancelTransaction()
    
}                                                                               // end class SaveCanceledTransactionSite


