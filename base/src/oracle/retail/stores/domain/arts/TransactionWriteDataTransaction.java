/* ===========================================================================
* Copyright (c) 1998, 2014, Oracle and/or its affiliates. All rights reserved.
 * ===========================================================================
 * $Header: rgbustores/modules/domain/src/oracle/retail/stores/domain/arts/TransactionWriteDataTransaction.java /main/42 2014/07/23 15:44:28 rhaight Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    rhaight   07/03/14 - store offline open revisions
 *    yiqzhao   06/12/14 - Change updateorder to saveorder for database upsert.
 *    yiqzhao   06/11/14 - Handle new columns for original transaction id for
 *                         order.
 *    ohorne    03/07/14 - Suspended Orders are not saved to OR_* tables
 *    yiqzhao   03/07/14 - Remove FinancialTotals update for line items. It has
 *                         been done when the method is called.
 *    rahravin  02/24/14 - Added a new case BillPay in case of cancelled
 *                         transactions
 *    vtemker   12/23/13 - Forward port - Partial order transactions should
 *                         show up in Assoc. Prod. register reports
 *    asinton   11/05/13 - make sure the totals is not null for instant cerdit
 *                         enrollment
 *    abondala  09/04/13 - initialize collections
 *    rgour     08/22/13 - putting a check if RTlog write is performed then
 *                         only increment the batch count
 *    abhinavs  07/08/13 - Modified to insert a row in order status table when
 *                         order status changed to printed or filled
 *    jswan     06/19/13 - Modified to perform the status update of an Order in
 *                         the context of a transaction.
 *    mkutiana  04/17/13 - handling suspended order w sale item - sale item
 *                         should not be added to summary tables
 *    vtemker   04/16/13 - Moved constants in OrderLineItemIfc to
 *                         OrderConstantsIfc in common project
 *    vtemker   01/16/13 - Fixed code reivew - CR204
 *    arabalas  12/26/12 - Tender History table not getting updated correctly
 *                         for Pickup/Delivery transaction
 *    vtemker   11/30/12 - CR204 - Added method to update SIM TLog batch id
 *    sgu       05/07/12 - rename crossChannel to XChannel
 *    sgu       05/07/12 - read/write order status table
 *    jswan     03/21/12 - Modified to support centralized gift certificate and
 *                         store credit.
 *    asinton   03/19/12 - removed dependancy on deprecated method.
 *    jswan     01/05/12 - Refactor the status change of suspended transaction
 *                         to occur in a transaction so that status change can
 *                         be sent to CO as part of DTM.
 *    ohorne    06/24/11 - fix for missing summary totals on Instant Credit
 *                         transactions
 *    kelesika  10/06/10 - Send Cust Link
 *    acadar    09/08/10 - add call to save contract signature
 *    nkgautam  06/22/10 - bill pay changes
 *    acadar    06/07/10 - changes for signature capture
 *    cgreene   05/26/10 - convert to oracle packaging
 *    cgreene   04/28/10 - updating deprecated names
 *    abondala  01/03/10 - update header date
 *    mahising  01/13/09 - fix QA issue
 *    mchellap  11/27/08 - Calling insertLayawayStatus to save the transaction
 *                         status
 *    mchellap  11/14/08 - Changes for junit failures
 *    mchellap  11/13/08 - Merge changes
 *    mchellap  11/13/08 - Inventory Reservation Module
 *
 * ===========================================================================
 * $Log:
 *    8    360Commerce 1.7         11/9/2006 7:28:31 PM   Jack G. Swan
 *         Modifided for XML Data Replication and CTR.
 *    7    360Commerce 1.6         7/25/2006 4:09:56 PM   Nathan Syfrig
 *         CR18124:  Merged M. Wisbauer's check for giftcard item, and if so
 *         do not include in dept count
 *    6    360Commerce 1.5         5/19/2006 3:07:05 PM   Brett J. Larsen CR
 *         17307 - remove inventory
 *
 *         a call to setupReversal() was buried in getInventoryUpdate() (which
 *          was removed as part of inventory removal)
 *
 *         setupReversal() is not part of inventory and is required
 *
 *         this change adds a call to setupReversal() to
 *         addSaveVoidTransactionActions()
 *    5    360Commerce 1.4         4/27/2006 7:27:00 PM   Brett J. Larsen CR
 *         17307 - remove inventory functionality - stage 2
 *    4    360Commerce 1.3         12/13/2005 4:43:46 PM  Barry A. Pape
 *         Base-lining of 7.1_LA
 *    3    360Commerce 1.2         3/31/2005 4:30:36 PM   Robert Pearse
 *    2    360Commerce 1.1         3/10/2005 10:26:27 AM  Robert Pearse
 *    1    360Commerce 1.0         2/11/2005 12:15:18 PM  Robert Pearse
 *
 *   Revision 1.22  2004/09/23 00:30:50  kmcbride
 *   @scr 7211: Inserting serialVersionUIDs in these Serializable classes
 *
 *   Revision 1.21  2004/09/22 19:11:17  kll
 *   @scr 7236: save transaction reentry as a control transaction
 *
 *   Revision 1.20  2004/08/18 15:48:38  rsachdeva
 *   @scr 6791 Transaction Level Send Capture Customer Insert Failure Fixed
 *
 *   Revision 1.19  2004/08/18 15:41:40  rsachdeva
 *   @scr 6719 Transaction Level Send Capture Customer Insert Failure Fixed
 *
 *   Revision 1.18  2004/06/29 15:12:23  cdb
 *   @scr 5860 Cleaned a little more.
 *
 *   Revision 1.17  2004/06/29 00:36:08  cdb
 *   @scr 5860 Corrected problem caused by lack of ArtsTransaction in TransactionWriteDataTransaction
 *   that caused QueueExceptionReport to suffer a null pointer exception.
 *
 *   Revision 1.16  2004/06/17 19:52:07  khassen
 *   @scr 5684 - Feature enhancements for capture customer info use case.
 *
 *   Revision 1.15  2004/06/15 00:44:30  jdeleau
 *   @scr 2775 Support register reports and financial totals with the new
 *   tax engine.
 *
 *   Revision 1.14  2004/06/04 22:36:20  cdb
 *   @scr 5371 Updated so that till payroll payout transactions will be saved using the SaveTillPayment data operation.
 *
 *   Revision 1.13  2004/06/04 21:38:18  cdb
 *   @scr 5371 Updated with an error message if data operation name cannot be determined.
 *
 *   Revision 1.12  2004/06/03 14:47:35  epd
 *   @scr 5368 Update to use of DataTransactionFactory
 *
 *   Revision 1.11  2004/05/19 16:50:12  awilliam
 *   @scr 4652 printing of payroll payout receipt
 *
 *   Revision 1.10  2004/04/19 14:38:47  tmorris
 *   @scr 4332 -Replaced direct instantiation(new) with Factory call.
 *
 *   Revision 1.9  2004/04/15 20:49:22  blj
 *   @scr 3871 - fixed problems with postvoid.
 *
 *   Revision 1.8  2004/04/09 16:55:47  cdb
 *   @scr 4302 Removed double semicolon warnings.
 *
 *   Revision 1.7  2004/03/22 17:26:41  blj
 *   @scr 3872 - added redeem security, receipt printing and saving redeem transactions.
 *
 *   Revision 1.6  2004/03/14 20:37:15  tfritz
 *   @scr 3884 - New Training Mode Funcionality
 *
 *   Revision 1.5  2004/02/17 17:57:38  bwf
 *   @scr 0 Organize imports.
 *
 *   Revision 1.4  2004/02/17 16:18:49  rhafernik
 *   @scr 0 log4j conversion
 *
 *   Revision 1.3  2004/02/12 17:13:20  mcs
 *   Forcing head revision
 *
 *   Revision 1.2  2004/02/11 23:25:26  bwf
 *   @scr 0 Organize imports.
 *
 *   Revision 1.1.1.1  2004/02/11 01:04:29  cschellenger
 *   updating to pvcs 360store-current
 *
 *
 *
 *    Rev 1.3   Jan 02 2004 10:16:36   nrao
 * Save OrderTransaction to the instant credit table. Removed saving of cancelled SaleReturnTransaction to the instant credit table.
 *
 *    Rev 1.2   Dec 31 2003 10:22:28   nrao
 * Modified methods to save transaction information to the new Instant Credit table.
 *
 *    Rev 1.1   Nov 13 2003 15:18:00   nrao
 * Added SaveInstantCreditTransactionActions for saving Instant Credit Enrollment transaction to the database.
 *
 *    Rev 1.0   Aug 29 2003 15:34:22   CSchellenger
 * Initial revision.
 *
 *    Rev 1.18   26 Jul 2003 12:20:26   mpm
 * Modified to save detail lines on in-process voids.
 *
 *    Rev 1.17   Jul 24 2003 17:02:50   bwf
 * Dont allow partial orders to update department or assoc reports.
 * Resolution for 2479: Partial Sp. order /web order pickup show on Department, Associate Prod. and Hourlt Prod. reports
 *
 *    Rev 1.16   Jul 21 2003 10:51:20   jgs
 * The method addSaveTillOpenTransactionActions()  was getting an the actual register object used by the POS app and setting the totals object from the till on it.  As a result both the till and the register were updating the same totals object which resulted in doubling the values in hard totals.
 * Resolution for 3079: Hard Totals amount are doubled
 *
 *    Rev 1.15   Jul 18 2003 14:38:06   sfl
 * Took away not necessary method call.
 * Resolution for POS SCR-2764: Till Summary Report - Net Trans. Taxable and Tax line items count fields incorrect
 *
 *    Rev 1.14   Jul 14 2003 19:06:10   sfl
 * Need to assign the values to the void transaction counts when transaction is being voided. These counts value were not assigned before, therefore the Net Trans. Taxable data filed in till/register reports was not correct.
 * Resolution for POS SCR-2764: Till Summary Report - Net Trans. Taxable and Tax line items count fields incorrect
 *
 *    Rev 1.13   24 Jun 2003 20:09:08   mpm
 * Added actions for canceled transactions.
 *
 *    Rev 1.12   Jun 24 2003 17:09:58   DCobb
 * Added method updateTransactionStatus to also update the till and register totals.
 * Resolution for POS SCR-2399: Canceled Suspended Transactions not being included in Summary Reports
 *
 *    Rev 1.11   May 22 2003 08:13:08   mpm
 * Merged in K*B fix.
 * Resolution for Domain SCR-104: Merge 5.1/5.5 into 6.0
 *
 *    Rev 1.10   May 19 2003 11:00:28   mpm
 * Commented call to SaveTillStartingFloat data operation.
 * Resolution for Domain SCR-104: Merge 5.1/5.5 into 6.0
 *
 *    Rev 1.9   May 18 2003 09:06:26   mpm
 * Merged 5.1 changes into 6.0
 * Resolution for Domain SCR-104: Merge 5.1/5.5 into 6.0
 *
 *    Rev 1.8   May 07 2003 08:17:46   RSachdeva
 * Till Pay In/Out Reprint Receipt
 * Resolution for POS SCR-2226: Till Pay In/Out Reprint Receipt - Print Offline Message
 *
 *    Rev 1.7   Apr 29 2003 16:55:22   RSachdeva
 * In reports queue trans. , causes app to crash  for till pay out and till pay in- with database offline
 * Resolution for POS SCR-2228: Offline Testing of Reports- Crash -Lockup
 *
 *    Rev 1.6   Feb 18 2003 14:54:00   DCobb
 * Added floating till.
 * Resolution for POS SCR-1867: POS 6.0 Floating Till
 *
 *    Rev 1.5   Feb 16 2003 11:44:08   mpm
 * Removed SaveStartingFloat data action from till open.
 * Resolution for POS SCR-2053: Merge 5.1 changes into 6.0
 *
 *    Rev 1.4   Feb 15 2003 17:26:10   mpm
 * Merged 5.1 changes.
 * Resolution for Domain SCR-104: Merge 5.1/5.5 into 6.0
 *
 *    Rev 1.3   07 Jun 2002 17:11:28   vpn-mpm
 * Corrected till-close transaction problems.
 *
 * ===========================================================================
 */
package oracle.retail.stores.domain.arts;
// java imports
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import oracle.retail.stores.common.utility.LocaleMap;
import oracle.retail.stores.common.utility.LocaleRequestor;
import oracle.retail.stores.common.utility.Util;
import oracle.retail.stores.domain.DomainGateway;
import oracle.retail.stores.domain.customer.CaptureCustomerIfc;
import oracle.retail.stores.domain.financial.AbstractFinancialEntityIfc;
import oracle.retail.stores.domain.financial.FinancialCountIfc;
import oracle.retail.stores.domain.financial.FinancialCountTenderItemIfc;
import oracle.retail.stores.domain.financial.FinancialTotalsIfc;
import oracle.retail.stores.domain.financial.LayawayIfc;
import oracle.retail.stores.domain.financial.RegisterIfc;
import oracle.retail.stores.domain.financial.StoreStatusIfc;
import oracle.retail.stores.domain.financial.TillIfc;
import oracle.retail.stores.domain.ixretail.log.POSLogTransactionEntryIfc;
import oracle.retail.stores.domain.lineitem.AbstractTransactionLineItemIfc;
import oracle.retail.stores.domain.lineitem.SaleReturnLineItemIfc;
import oracle.retail.stores.domain.order.OrderConstantsIfc;
import oracle.retail.stores.domain.order.OrderIfc;
import oracle.retail.stores.domain.stock.GiftCertificateItemIfc;
import oracle.retail.stores.domain.store.StoreIfc;
import oracle.retail.stores.domain.store.WorkstationIfc;
import oracle.retail.stores.domain.tax.TaxIfc;
import oracle.retail.stores.domain.tender.TenderGiftCertificateIfc;
import oracle.retail.stores.domain.tender.TenderLineItemIfc;
import oracle.retail.stores.domain.tender.TenderStoreCreditIfc;
import oracle.retail.stores.domain.transaction.BankDepositTransactionIfc;
import oracle.retail.stores.domain.transaction.BillPayTransactionIfc;
import oracle.retail.stores.domain.transaction.InstantCreditTransactionIfc;
import oracle.retail.stores.domain.transaction.LayawayPaymentTransactionIfc;
import oracle.retail.stores.domain.transaction.LayawayTransactionIfc;
import oracle.retail.stores.domain.transaction.NoSaleTransactionIfc;
import oracle.retail.stores.domain.transaction.OrderStatusChangeTransactionIfc;
import oracle.retail.stores.domain.transaction.OrderTransaction;
import oracle.retail.stores.domain.transaction.OrderTransactionIfc;
import oracle.retail.stores.domain.transaction.PaymentTransactionIfc;
import oracle.retail.stores.domain.transaction.RedeemTransactionIfc;
import oracle.retail.stores.domain.transaction.RegisterOpenCloseTransactionIfc;
import oracle.retail.stores.domain.transaction.RetailTransactionIfc;
import oracle.retail.stores.domain.transaction.SaleReturnTransaction;
import oracle.retail.stores.domain.transaction.SaleReturnTransactionIfc;
import oracle.retail.stores.domain.transaction.StatusChangeTransactionIfc;
import oracle.retail.stores.domain.transaction.StoreOpenCloseTransactionIfc;
import oracle.retail.stores.domain.transaction.TenderableTransactionIfc;
import oracle.retail.stores.domain.transaction.TillAdjustmentTransactionIfc;
import oracle.retail.stores.domain.transaction.TillOpenCloseTransactionIfc;
import oracle.retail.stores.domain.transaction.TransactionConstantsIfc;
import oracle.retail.stores.domain.transaction.TransactionIfc;
import oracle.retail.stores.domain.transaction.TransactionSummaryIfc;
import oracle.retail.stores.domain.transaction.VoidTransactionIfc;
import oracle.retail.stores.domain.utility.EYSDate;
import oracle.retail.stores.domain.utility.LocaleConstantsIfc;
import oracle.retail.stores.foundation.manager.data.DataAction;
import oracle.retail.stores.foundation.manager.data.DataException;
import oracle.retail.stores.foundation.manager.data.DataTransaction;
import oracle.retail.stores.foundation.manager.ifc.data.DataActionIfc;
import oracle.retail.stores.foundation.manager.ifc.data.DataTransactionIfc;

import org.apache.log4j.Logger;

//-------------------------------------------------------------------------
/**
    The DataTransaction to perform persistent operations on the POS
    Transaction object.
    @version $Revision: /main/42 $
    @see oracle.retail.stores.domain.arts.TransactionReadDataTransaction
    @see oracle.retail.stores.domain.arts.ReadTransactionsByIDDataTransaction
    @see oracle.retail.stores.domain.arts.UpdateReturnedItemsDataTransaction
    @see oracle.retail.stores.domain.arts.TransactionHistoryDataTransaction
**/
//-------------------------------------------------------------------------
public class TransactionWriteDataTransaction
extends DataTransaction
implements DataTransactionIfc, AccumulatorTransactionIfc
{
    // This id is used to tell
    // the compiler not to generate a
    // new serialVersionUID.
    //
    static final long serialVersionUID = 5059600135302642485L;

    /**
        The logger to which log messages will be sent.
    **/
    private static Logger logger = Logger.getLogger(oracle.retail.stores.domain.arts.TransactionWriteDataTransaction.class);
    /**
        revision number of this class
    **/
    public static String revisionNumber = "$Revision: /main/42 $";
    /**
        The default name that links this transaction to a command within DataScript.
    **/
    public static String dataCommandName  = "TransactionWriteDataTransaction";
    /**
        The default name that links a save transaction to a command within DataScript.
    **/
    public static String dataSaveName      = "SaveTransactionWriteDataTransaction";
    /**
        The name that writes data directly to the DB even when most transactions are being queued.
    **/
    public static String notQueuedSaveName = "NotQueuedTransactionWriteDataTransaction";
    /**
        A list used to build the DataAction array
    **/
    protected List<DataActionIfc> actions = null;
    /**
        A Transaction that is passed as input
    **/
    protected TransactionIfc posTransaction = null;
    /**
        An ARTSTransaction constructed from a Transaction.
    **/
    protected ARTSTransaction artsTransaction = null;
    /**
        An vector of ARTSDepartmentTotals
    **/
    protected List<ARTSDepartmentTotals> departments = null;
    /**
        customer identifier
    **/
    protected String customerID = null;
    /**
        transaction-ID-date-range key
    **/
    protected ARTSTransactionIDDateRange transactionIDDateRange = null;

    //---------------------------------------------------------------------
    /**
        Class constructor. <P>
    **/
    //---------------------------------------------------------------------
    public TransactionWriteDataTransaction()
    {
        super(dataCommandName);
    }

    //---------------------------------------------------------------------
    /**
        Class constructor. <P>
        @param name transaction name
    **/
    //---------------------------------------------------------------------
    public TransactionWriteDataTransaction(String name)
    {
        super(name);
    }

    //---------------------------------------------------------------------
    /**
        Get the POS Transaction reference
        @return the POS Stransaction
    **/
    //---------------------------------------------------------------------
    public TransactionIfc getPOSTransaction()
    {
        return(posTransaction);
    }

    //---------------------------------------------------------------------
    /**
        Get the ARTS Transaction reference
        @return the ARTS Stransaction
    **/
    //---------------------------------------------------------------------
    public ARTSTransaction getARTSTransaction()
    {
        return(artsTransaction);
    }

    //---------------------------------------------------------------------
    /**
        Saves a POS Transaction to the data store.
        @param  transaction The Transaction object to save
        @exception  DataException when an error occurs.
    **/
    //---------------------------------------------------------------------
    public void saveTransaction(TransactionIfc transaction) throws DataException
    {
        // Setting the till and register to null and accumlateTotals to false
        // preserves current behavior.
        saveTransaction(transaction, null, null, null);
    }

    //---------------------------------------------------------------------
    /**
        Saves a POS Transaction to the data store.
        @param  transaction The Transaction object to save
        @param till till to record transaction totals
        @param register register against which total sare to be recorded
        @exception  DataException when an error occurs.
    **/
    //---------------------------------------------------------------------
    public void saveTransaction(TransactionIfc transaction,
                                TillIfc              till,
                                RegisterIfc          register)
                                throws DataException
    {
        saveTransaction(transaction,
                        null,
                        till,
                        register);
    }

    //---------------------------------------------------------------------
    /**
        Saves a POS Transaction to the data store.
        @param  transaction The Transaction object to save
        @param totals financial totals for transaction
        @param till till to record transaction totals
        @param register register against which total sare to be recorded
        @exception  DataException when an error occurs.
    **/
    //---------------------------------------------------------------------
    public void saveTransaction(TransactionIfc transaction,
                                FinancialTotalsIfc   totals,
                                TillIfc              till,
                                RegisterIfc          register)
                                throws DataException
    {
        if (logger.isDebugEnabled()) logger.debug(
                     "TransactionWriteDataTransaction.saveTransaction");

        actions = new ArrayList<DataActionIfc>();
        int transactionType = transaction.getTransactionType();
        /*
         * Check for Canceled transactions first, because they can
         * be of any type.
         */
        if (transaction.getTransactionStatus() == TransactionIfc.STATUS_CANCELED)
        {
            switch(transactionType)
            {                           // begin add actions based on type
                case TransactionIfc.TYPE_SALE:
                case TransactionIfc.TYPE_RETURN:
                case TransactionIfc.TYPE_LAYAWAY_INITIATE:
                    addSaveSaleReturnCanceledTransactionActions
                      ((SaleReturnTransactionIfc) transaction,
                       totals,
                       till,
                       register);
                    break;
                case TransactionIfc.TYPE_BILL_PAY:
                    saveBillPayTransactionActions
                    ((BillPayTransactionIfc) transaction,
                     totals,
                     till,
                     register);
                    break;
                case TransactionIfc.TYPE_ORDER_INITIATE:
                case TransactionIfc.TYPE_ORDER_PARTIAL:
                case TransactionIfc.TYPE_ORDER_COMPLETE:
                    addSaveOrderCanceledTransactionActions
                      ((OrderTransactionIfc) transaction,
                       totals,
                       till,
                       register);
                    break;
                case TransactionIfc.TYPE_INSTANT_CREDIT_ENROLLMENT:
                    addSaveInstantCreditTransactionActions(
                                                (InstantCreditTransactionIfc) transaction,
                                                totals,
                                                till,
                                                register);
                    break;
                default:
                    addSaveCanceledTransactionActions(transaction, null, till, register);
                    break;
            } // end switch

        } // if trans canceled
        else
        {                               // begin handle completed transaction
            switch(transactionType)
            {                           // begin add actions based on type
                case TransactionIfc.TYPE_SALE:
                case TransactionIfc.TYPE_RETURN:
                    addSaveSaleReturnTransactionActions((SaleReturnTransactionIfc) transaction,
                                                         totals,
                                                         till,
                                                         register);
                    break;
                case TransactionIfc.TYPE_LAYAWAY_INITIATE:
                case TransactionIfc.TYPE_LAYAWAY_DELETE:
                case TransactionIfc.TYPE_LAYAWAY_COMPLETE:
                    addSaveLayawayTransactionActions((LayawayTransactionIfc) transaction,
                                                     totals,
                                                     till,
                                                     register);
                    break;
                case TransactionIfc.TYPE_VOID:
                    addSaveVoidTransactionActions((VoidTransactionIfc) transaction,
                                                  totals,
                                                  till,
                                                  register);
                    break;
                case TransactionIfc.TYPE_NO_SALE:
                    addSaveNoSaleTransactionActions((NoSaleTransactionIfc) transaction,
                                                    totals,
                                                    till,
                                                    register);
                    break;
                case TransactionIfc.TYPE_HOUSE_PAYMENT:
                case TransactionIfc.TYPE_LAYAWAY_PAYMENT:
                    addSavePaymentTransactionActions((PaymentTransactionIfc) transaction,
                                                     totals,
                                                     till,
                                                     register);
                    break;
                case TransactionIfc.TYPE_OPEN_STORE:
                case TransactionIfc.TYPE_CLOSE_STORE:
                    addSaveStoreOpenCloseTransactionActions(transaction);
                    break;
                case TransactionIfc.TYPE_OPEN_REGISTER:
                case TransactionIfc.TYPE_CLOSE_REGISTER:
                    addSaveRegisterOpenCloseTransactionActions(transaction);
                    break;
                case TransactionIfc.TYPE_BANK_DEPOSIT_STORE:
                    addSaveBankDepositTransactionActions(transaction);
                    break;
                case TransactionIfc.TYPE_SUSPEND_TILL:
                case TransactionIfc.TYPE_RESUME_TILL:
                    addSaveTillSuspendResumeTransactionActions(transaction);
                    break;
                case TransactionIfc.TYPE_OPEN_TILL:
                    addSaveTillOpenTransactionActions(transaction);
                    break;
                case TransactionIfc.TYPE_CLOSE_TILL:
                    addSaveTillCloseTransactionActions(transaction);
                    break;
                case TransactionIfc.TYPE_PAYIN_TILL:
                case TransactionIfc.TYPE_PAYOUT_TILL:
                case TransactionIfc.TYPE_PAYROLL_PAYOUT_TILL:
                case TransactionIfc.TYPE_LOAN_TILL:
                case TransactionIfc.TYPE_PICKUP_TILL:
                    addSaveTillAdjustmentTransactionActions((TillAdjustmentTransactionIfc) transaction,
                                                            totals,
                                                            till,
                                                            register);
                    break;
                case TransactionIfc.TYPE_ORDER_INITIATE:
                case TransactionIfc.TYPE_ORDER_PARTIAL:
                case TransactionIfc.TYPE_ORDER_COMPLETE:
                case TransactionIfc.TYPE_ORDER_CANCEL:
                    addSaveOrderTransactionActions((OrderTransactionIfc) transaction,
                                                   totals,
                                                   till,
                                                   register);
                    break;
                case TransactionIfc.TYPE_INSTANT_CREDIT_ENROLLMENT:
                    addSaveInstantCreditTransactionActions((InstantCreditTransactionIfc) transaction,
                                                        totals,
                                                        till,
                                                        register);
                    break;
                case TransactionIfc.TYPE_ENTER_TRAINING_MODE:
                case TransactionIfc.TYPE_EXIT_TRAINING_MODE:
                    addSaveEnterExitTrainingModeTransactionActions(transaction,
                                                                   totals,
                                                                   till,
                                                                   register);
                    break;
                case TransactionIfc.TYPE_REDEEM:
                    addSaveRedeemTransactionActions((RedeemTransactionIfc) transaction,
                                                 totals,
                                                 till,
                                                 register);
                    break;
                case TransactionIfc.TYPE_ENTER_TRANSACTION_REENTRY:
                case TransactionIfc.TYPE_EXIT_TRANSACTION_REENTRY:
                    addSaveControlTransactionActions(transaction);
                    break;
                case TransactionIfc.TYPE_BILL_PAY:
                    saveBillPayTransactionActions( (BillPayTransactionIfc)transaction,
                                                    totals,
                                                    till,
                                                    register);
                    break;
                case TransactionIfc.TYPE_STATUS_CHANGE:
                    if (transaction instanceof OrderStatusChangeTransactionIfc)
                    {
                        addSaveOrderStatusActions((OrderStatusChangeTransactionIfc)transaction);
                    }
                    else
                    {
                        addSaveTransactionStatusActions((StatusChangeTransactionIfc)transaction, till, register);
                    }
                    break;
                default:
                    logger.error(
                                 "Invalid transaction type : " + transaction.getClass().getName() + "");
                    throw new DataException(JdbcDataOperation.CONFIG_ERROR, "Unknown Transaction type.");
            }                           // end add actions based on type
        }
        if (((transaction != null) && transaction.getCaptureCustomer() != null))
        {
            addSaveCaptureCustomerTransactionActions((TransactionIfc) transaction, totals, till, register);
        }
        // end handle completed transaction

        DataActionIfc[] dataActions = new DataActionIfc[actions.size()];
        dataActions = actions.toArray(dataActions);
        setDataActions(dataActions);
        getDataManager().execute(this);

        if (logger.isDebugEnabled()) logger.debug(
                    "TransactionWriteDataTransaction.saveTransaction");
    }

    /**
     * Adds the data action required to save a capture customer.
     * @param transaction
     * @param totals
     * @param till
     * @param register
     */
    protected void addSaveCaptureCustomerTransactionActions(TransactionIfc transaction,
                                                            FinancialTotalsIfc totals,
                                                            TillIfc till,
                                                            RegisterIfc register)
    {
        DataAction dataAction = new DataAction();
        dataAction.setDataOperationName("WriteCaptureCustomer");
        CaptureCustomerIfc customer = transaction.getCaptureCustomer();
        //if transaction id is not there then the transaction will rollback
        //this was happening with suspend/retrieve as the transaction was not saving correctly
        if (Util.isEmpty(customer.getTransactionID())
              || Util.isEmpty(customer.getWsID()) || Util.isEmpty(customer.getStoreID())
              || customer.getBusinessDay() == null)
        {
            if (transaction != null
                && transaction.getWorkstation() != null && transaction.getWorkstation().getStore() != null)
            {
                customer.setTransactionID(String.valueOf(transaction.getTransactionSequenceNumber()));
                customer.setWsID(transaction.getWorkstation().getWorkstationID());
                customer.setStoreID(transaction.getWorkstation().getStore().getStoreID());
                customer.setBusinessDay(transaction.getBusinessDay());
            }
        }
        dataAction.setDataObject(transaction.getCaptureCustomer());
        actions.add(dataAction);
    }


    //---------------------------------------------------------------------
    /**
        When saving a PaymentTransactionIfc, you need to:
            <li> Save to the transaction tables
            <li> Save payment pertinent information to line item table.
            <li> Save each tender line item
        <p>
        @param  transaction the transaction
        @param totals financial totals for transaction
        @param till till on which payment was processed
        @param register register on which payment was processed
    **/
    //---------------------------------------------------------------------
    protected void addSavePaymentTransactionActions(PaymentTransactionIfc transaction,
                                                    FinancialTotalsIfc totals,
                                                    TillIfc till,
                                                    RegisterIfc register)
    {
        artsTransaction = new ARTSTransaction(transaction);

        // Add a DataAction to save the PaymentTransactionIfc
        DataAction dataAction = new DataAction();

        if(transaction.getTransactionType() == TransactionIfc.TYPE_LAYAWAY_PAYMENT &&
            transaction.getIRSCustomer() != null)
        {
            dataAction.setDataOperationName("SaveIRSCustomer");
            dataAction.setDataObject(artsTransaction);
            actions.add(dataAction);
        }

        dataAction = new DataAction();
        dataAction.setDataOperationName("SaveRetailTransaction");
        dataAction.setDataObject(artsTransaction);
        actions.add(dataAction);

        if(transaction.getTransactionType() == TransactionIfc.TYPE_LAYAWAY_PAYMENT)
        {
            dataAction = new DataAction();
            dataAction.setDataOperationName("UpdateLayawayForPayment");
            dataAction.setDataObject(artsTransaction);
            actions.add(dataAction);
        }

        if (transaction.getTransactionType() == TransactionIfc.TYPE_LAYAWAY_PAYMENT &&
            transaction instanceof LayawayPaymentTransactionIfc)
        {
            LayawayPaymentTransactionIfc layawayPaymentTransaction =
              (LayawayPaymentTransactionIfc)transaction;
            if(layawayPaymentTransaction.getLayaway().getPaymentHistoryInfoCollection() != null &&
               layawayPaymentTransaction.getLayaway().getPaymentHistoryInfoCollection().size() > 0)
            {
                dataAction = createDataAction(layawayPaymentTransaction.getLayaway(), "SaveLayawayPaymentHistoryInfo");
                actions.add(dataAction);
            }
        }


        // Add a DataAction to save all the line items in the Transaction
        dataAction = new DataAction();
        dataAction.setDataOperationName("SaveRetailTransactionLineItems");
        dataAction.setDataObject(artsTransaction);
        actions.add(dataAction);

        addSaveGiftCertificateDataAction(artsTransaction);

        // Add a DataAction to save all the tender line items in the Transaction
        DataActionIfc da  = new SaveTenderLineItemsAction(this, artsTransaction);
        actions.add(da);

        //Add a DataAction to save store credit in the Transaction
        addSaveStoreCreditDataAction(artsTransaction);

        //Save financial totals to database to be used by hourly productivity report
        if (!transaction.isTrainingMode())
        {
            posTransaction = transaction;
            dataAction = new DataAction();
            dataAction.setDataOperationName("AddTimeIntervalTotals");
            dataAction.setDataObject(posTransaction);
            actions.add(dataAction);

        }

        //Save financial totals to database to be used in summary reports for till, register, store
        if (!transaction.isTrainingMode() && till != null && register != null)
        {
            buildSummaryTotals(transaction, totals, till, register);
        }
    }

    //---------------------------------------------------------------------
    /**
        When saving a SaleReturnTransactionIfc, you need to:
            <li> Save to the transaction tables
            <li> Save each line item
            <li> Save each tender line item
            <li> Save Accumulator information for Operator, Workstation,
                 and Department
            <li> Optionally Save Accumulator information for Till and Register
        <p>
        @param  transaction the transaction
        @param totals financial totals for transaction
        @param till till on which payment was processed
        @param register register on which payment was processed
    **/
    //---------------------------------------------------------------------
    protected void addSaveSaleReturnTransactionActions(SaleReturnTransactionIfc transaction,
                                                       FinancialTotalsIfc totals,
                                                       TillIfc till,
                                                       RegisterIfc register)
    {
        artsTransaction = new ARTSTransaction(transaction);
        // Add a DataAction to save the SaleReturnTransactionIfc
        DataAction dataAction = new DataAction();

        if(transaction.getIRSCustomer() != null)
        {
            dataAction.setDataOperationName("SaveIRSCustomer");
            dataAction.setDataObject(artsTransaction);
            actions.add(dataAction);
        }

        dataAction = new DataAction();
        dataAction.setDataOperationName("SaveRetailTransaction");
        dataAction.setDataObject(artsTransaction);
        actions.add(dataAction);

        // Add a DataAction to save all the line items in the Transaction
        dataAction = new DataAction();
        dataAction.setDataOperationName("SaveRetailTransactionLineItems");
        dataAction.setDataObject(artsTransaction);
        actions.add(dataAction);

        addSaveGiftCertificateDataAction(artsTransaction);

        // Add a DataAction to save all the tender line items in the Transaction
        DataActionIfc da = new SaveTenderLineItemsAction(this, artsTransaction);
        actions.add(da);

        //Add a DataAction to save store credit in the Transaction
        addSaveStoreCreditDataAction(artsTransaction);

        // Add a DataAction to save instant credit in the Transaction
        SaleReturnTransaction srt = (SaleReturnTransaction) transaction;
        if (srt.getInstantCredit() != null)
        {
            dataAction = createDataAction(transaction, "SaveInstantCreditTransaction");
            actions.add(dataAction);
        }

        //if sale return contains an external order and has contract signature captured, save the signature in
        //the database
        if(srt.hasExternalOrder() && srt.requireServiceContract() && srt.isContractSignatureCaptured())
        {
            dataAction = createDataAction(artsTransaction, "SaveRetailTransactionDocument");
            actions.add(dataAction);
        }

        if (!transaction.isTrainingMode() && transaction.getTransactionStatus() != TransactionIfc.STATUS_SUSPENDED)
        {
            // Add accumulator information
            addAccumulatorActions(transaction);
        }

        if (!transaction.isTrainingMode() && till != null && register != null)
        {
            buildSummaryTotals(transaction, totals, till, register);
        }

    }

    //---------------------------------------------------------------------
    /**
        When saving a LayawayTransactionIfc, you need to:
            <li> Save to the transaction tables
            <li> Save each line item
            <li> Save each tender line item
            <li> Save layaway to LayawayTransaction table
            <li> Save Accumulator information for Operator, Workstation,
                 and Department
            <li> Optionally Save Accumulator information for Till and Register
        <p>
        @param  transaction the transaction
        @param totals financial totals for transaction
        @param till till on which payment was processed
        @param register register on which payment was processed
    **/
    //---------------------------------------------------------------------
    protected void addSaveLayawayTransactionActions(LayawayTransactionIfc transaction,
                                                    FinancialTotalsIfc totals,
                                                    TillIfc till,
                                                    RegisterIfc register) throws DataException
    {
        artsTransaction = new ARTSTransaction(transaction);

        DataAction dataAction = new DataAction();

        if(transaction.getIRSCustomer() != null)
        {
            dataAction.setDataOperationName("SaveIRSCustomer");
            dataAction.setDataObject(artsTransaction);
            actions.add(dataAction);
        }

        // Add a DataAction to save the SaleReturnTransactionIfc
        dataAction = createDataAction(artsTransaction, "SaveRetailTransaction");
        actions.add(dataAction);

        // Add a DataAction to save all the line items in the Transaction
        dataAction = createDataAction(artsTransaction, "SaveRetailTransactionLineItems");
        actions.add(dataAction);

        addSaveGiftCertificateDataAction(artsTransaction);

        String layawayDataOperationName = null;
        switch(artsTransaction.getPosTransaction().getTransactionType())
        {                               // begin set operations by type
            case TransactionIfc.TYPE_LAYAWAY_INITIATE:
                layawayDataOperationName = "InsertLayaway";
                break;
            case TransactionIfc.TYPE_LAYAWAY_COMPLETE:
            case TransactionIfc.TYPE_LAYAWAY_DELETE:
                layawayDataOperationName = "UpdateLayaway";
                break;
            default:
                break;
        }                               // end set operations by type

        // Add a DataAction to save the layaway
        LayawayTransactionIfc layawayTransaction = (LayawayTransactionIfc) artsTransaction.getPosTransaction();
        LayawayIfc layaway = layawayTransaction.getLayaway();

        // Set the current transaction details to the layaway, these details are used
        // to save the layaway transaction status in the layaway status table
        layaway.setWorkStationID(layawayTransaction.getWorkstation().getWorkstationID());
        layaway.setCurrentTransactionBusinessDate(layawayTransaction.getBusinessDay());
        layaway.setCurrentTransactionSequenceNo(layawayTransaction.getFormattedTransactionSequenceNumber());


        dataAction = createDataAction(layaway, layawayDataOperationName);
        actions.add(dataAction);

        if(layawayTransaction.getLayaway().getPaymentHistoryInfoCollection() != null &&
           layawayTransaction.getLayaway().getPaymentHistoryInfoCollection().size() > 0)
        {
            dataAction = createDataAction(layawayTransaction.getLayaway(), "SaveLayawayPaymentHistoryInfo");
            actions.add(dataAction);
        }

        // if no payment, this is a suspended transaction
        if (layawayTransaction.getPayment() != null)
        {
            // add a data action to save the payment
            dataAction = createDataAction(layawayTransaction.getPayment(), "InsertPayment");
            actions.add(dataAction);
        }

        // Add a DataAction to save all the tender line items in the Transaction
        DataActionIfc da = new SaveTenderLineItemsAction(this, artsTransaction);
        actions.add(da);

        //Add a DataAction to save store credit in the Transaction
        addSaveStoreCreditDataAction(artsTransaction);

        if (!transaction.isTrainingMode())
        {
            // Add accumulator information
            addAccumulatorActions(transaction);
        }

        if (!transaction.isTrainingMode() && till != null && register != null)
        {
            buildSummaryTotals(transaction, totals, till, register);
        }

    }

    //---------------------------------------------------------------------
    /**
        When saving an OrderTransactionIfc, you need to:
            <li> Save to the transaction tables
            <li> Save each line item
            <li> Save each tender line item
            <li> Save order to the Order table
            <li> Save order line items
            <li> Save Accumulator information for Operator, Workstation,
                 and Department
            <li> Optionally Save Accumulator information for Till and Register
        <p>
        @param  transaction the transaction
        @param totals financial totals for transaction
        @param till till on which payment was processed
        @param register register on which payment was processed
        @exception throws data exception if error occurs
    **/
    //---------------------------------------------------------------------
    protected void addSaveOrderTransactionActions(OrderTransactionIfc transaction,
                                                  FinancialTotalsIfc totals,
                                                  TillIfc till,
                                                  RegisterIfc register) throws DataException
    {
        artsTransaction = new ARTSTransaction(transaction);
        AbstractTransactionLineItemIfc[] lineItems = null;
        lineItems = ((RetailTransactionIfc) transaction).getLineItems();
        DataAction dataAction = new DataAction();

        if(transaction.getIRSCustomer() != null)
        {
            dataAction.setDataOperationName("SaveIRSCustomer");
            dataAction.setDataObject(artsTransaction);
            actions.add(dataAction);
        }

        // Add a DataAction to save the SaleReturnTransactionIfc
        dataAction = createDataAction(artsTransaction, "SaveRetailTransaction");
        actions.add(dataAction);

        // Add a DataAction to save all the line items in the Transaction
        dataAction = createDataAction(artsTransaction, "SaveRetailTransactionLineItems");
        actions.add(dataAction);

        addSaveGiftCertificateDataAction(artsTransaction);

        // if sale return contains an external order and has contract signature captured, save the signature in
        // the database
        if(transaction.hasExternalOrder() && transaction.requireServiceContract() && transaction.isContractSignatureCaptured())
        {
            dataAction = createDataAction(artsTransaction, "SaveRetailTransactionDocument");
            actions.add(dataAction);
        }

        // Add a DataAction to save the order
        OrderTransactionIfc orderTransaction =
            (OrderTransactionIfc) artsTransaction.getPosTransaction();

        //Insert order status at the time of the order transaction
        dataAction = createDataAction(orderTransaction,
                "InsertOrderStatusByTransaction");
        actions.add(dataAction);

        // If this order is a cross channel order only, and is not suspended, ORPOS database does not keep a
        // master record of the order; therefore no insert/update is necessary.
        if (!transaction.containsXChannelOrderLineItemOnly() && !transaction.isSuspended())
        {
            String orderDataOperationName = null;
            switch(artsTransaction.getPosTransaction().getTransactionType())
            {                               // begin set operations by type
            case TransactionIfc.TYPE_ORDER_INITIATE:
                orderDataOperationName = "InsertOrderByTransaction";
                break;
            case TransactionIfc.TYPE_ORDER_PARTIAL:
            case TransactionIfc.TYPE_ORDER_COMPLETE:
            case TransactionIfc.TYPE_ORDER_CANCEL:
                    orderDataOperationName = "SaveOrderByTransaction";
                break;
            default:
                break;
            }                               // end set operations by type

            dataAction = createDataAction(orderTransaction,
                    orderDataOperationName);
            actions.add(dataAction);
        }

        // if no payment, this is a suspended transaction
        if (orderTransaction.getPayment() != null)
        {
            // add a data action to save the payment
            dataAction = createDataAction(orderTransaction.getPayment(), "InsertPayment");
            actions.add(dataAction);
        }


        if(orderTransaction.getPaymentHistoryInfoCollection() != null &&
           orderTransaction.getPaymentHistoryInfoCollection().size() > 0)
        {
            dataAction = createDataAction(orderTransaction, "SaveOrderPaymentHistoryInfo");
            actions.add(dataAction);
        }

        // Add a DataAction to save all the tender line items in the Transaction
        DataActionIfc da = new SaveTenderLineItemsAction(this, artsTransaction);
        actions.add(da);

        //Add a DataAction to save store credit in the Transaction
        addSaveStoreCreditDataAction(artsTransaction);

        // Add a DataAction to save instant credit in the Transaction
        OrderTransaction oTrans = (OrderTransaction) transaction;
        if (oTrans.getInstantCredit() != null)
        {
            dataAction = createDataAction(transaction, "SaveInstantCreditTransaction");
            actions.add(dataAction);
        }

        if (!transaction.isTrainingMode() &&
            (orderTransaction.getTransactionType() == TransactionIfc.TYPE_ORDER_PARTIAL ||
             orderTransaction.getTransactionType() == TransactionIfc.TYPE_ORDER_COMPLETE)||
             orderTransaction.getOrderType() == OrderConstantsIfc.ORDER_TYPE_ON_HAND)
        {
            // Add accumulator information
            addAccumulatorActions(transaction);
        }

        if (!transaction.isTrainingMode() &&
            till != null &&
            register != null)
        {
            buildSummaryTotals(transaction, totals, till, register);
        }

    }

    //---------------------------------------------------------------------
    /**
        When saving an InstantCreditTransactionIfc, you need to:
            <li> Save to the transaction tables
        <p>
        @param  transaction the transaction
        @param totals financial totals for transaction
        @param till till on which payment was processed
        @param register register on which payment was processed
        @exception throws data exception if error occurs
    **/
    //---------------------------------------------------------------------

    protected void addSaveInstantCreditTransactionActions(InstantCreditTransactionIfc transaction,
                                                    FinancialTotalsIfc totals,
                                                    TillIfc till,
                                                    RegisterIfc register)
    {
        artsTransaction = new ARTSTransaction(transaction);

        // Add a DataAction to save the InstantCreditTransactionIfc
        DataAction dataAction = createDataAction(transaction, "SaveInstantCreditTransaction");
        actions.add(dataAction);

        if (!transaction.isTrainingMode() && till != null && register != null)
        {
            if (totals == null)
            {
                // Clone the financial totals object.
                totals = (FinancialTotalsIfc)transaction.getFinancialTotals().clone();
            }
            buildSummaryTotals(totals, till, register);
        }

    }

    //---------------------------------------------------------------------
    /**
        Adds the data actions needed to save a VoidTransactionIfc.
        @param  transaction The VoidTransactionIfc to save
        @param totals financial totals for transaction
        @param till till on which payment was processed
        @param register register on which payment was processed
    **/
    //---------------------------------------------------------------------
    protected void addSaveVoidTransactionActions(VoidTransactionIfc transaction,
                                                 FinancialTotalsIfc totals,
                                                 TillIfc till,
                                                 RegisterIfc register)
    {
        if (logger.isDebugEnabled()) logger.debug(
                     "TransactionWriteDataTransaction.addSaveVoidTransactionActions");
        artsTransaction = new ARTSTransaction(transaction);

        DataAction dataAction = null;

        transaction.setupReversal();

        // create action to save void transaction
        dataAction = createDataAction(artsTransaction, "SaveVoidTransaction");
        actions.add(dataAction);

        if (transaction.getTenderLineItemsSize() != 0)
        {
            // Add a DataAction to save all the tender line items in the Transaction
            DataActionIfc da = new SaveTenderLineItemsAction(this, artsTransaction);
            actions.add(da);

            //Add a DataAction to save store credit in the Transaction
            addSaveStoreCreditDataAction(artsTransaction);

            addSaveGiftCertificateDataAction(artsTransaction);
        }

        // handle additional data actions
        LayawayIfc layaway = null;
        boolean updatePaymentHistory = false;
        int originaltransType = transaction.getOriginalTransaction().getTransactionType();

        switch(originaltransType)
        {
            case TransactionIfc.TYPE_LAYAWAY_INITIATE:
            case TransactionIfc.TYPE_LAYAWAY_DELETE:
            case TransactionIfc.TYPE_LAYAWAY_COMPLETE:
                LayawayTransactionIfc layawayTransaction =
                    (LayawayTransactionIfc) transaction.getOriginalTransaction();
                layaway = layawayTransaction.getLayaway();
                break;
            case TransactionIfc.TYPE_LAYAWAY_PAYMENT:
                LayawayPaymentTransactionIfc layawayPaymentTransaction =
                    (LayawayPaymentTransactionIfc) transaction.getOriginalTransaction();
                layaway = layawayPaymentTransaction.getLayaway();
                updatePaymentHistory = true;
                break;
            case TransactionIfc.TYPE_ORDER_COMPLETE:
            case TransactionIfc.TYPE_ORDER_PARTIAL:
            case TransactionIfc.TYPE_ORDER_CANCEL:
            case TransactionIfc.TYPE_ORDER_INITIATE:
                 // update the order to reflect voided status
                 dataAction = createDataAction((OrderTransactionIfc) transaction.getOriginalTransaction(),
                                               "UpdateOrderByTransaction");
                 actions.add(dataAction);

                 // update the order line items to reflect voided status
                 dataAction = createDataAction((OrderTransactionIfc)transaction.getOriginalTransaction(),
                                               "SaveOrderLineItems");
                 actions.add(dataAction);

                 OrderTransactionIfc orderTransaction =
                     (OrderTransactionIfc) transaction.getOriginalTransaction();
                 if(orderTransaction.getPaymentHistoryInfoCollection() != null &&
                     orderTransaction.getPaymentHistoryInfoCollection().size() > 0)
                  {
                      dataAction = createDataAction(orderTransaction, "SaveOrderPaymentHistoryInfo");
                      actions.add(dataAction);
                  }

                 break;
            case TransactionIfc.TYPE_REDEEM:
                // update the redeem to reflect voided status
                dataAction = createDataAction((RedeemTransactionIfc)transaction.getOriginalTransaction(),
                "SaveVoidRedeemTransaction");
                actions.add(dataAction);
                break;
            default:
                break;
        }
        // add layaway action, if needed
        if (layaway != null)
        {
            dataAction = createDataAction(layaway, "UpdateLayaway");
            actions.add(dataAction);

            if(updatePaymentHistory &&
                layaway.getPaymentHistoryInfoCollection() != null &&
                layaway.getPaymentHistoryInfoCollection().size() > 0)
             {
                 dataAction = createDataAction(layaway, "SaveLayawayPaymentHistoryInfo");
                 actions.add(dataAction);
             }
        }

        if (!transaction.isTrainingMode() &&
           originaltransType != TransactionIfc.TYPE_ORDER_CANCEL &&
           originaltransType != TransactionIfc.TYPE_ORDER_INITIATE &&
           originaltransType != TransactionIfc.TYPE_LAYAWAY_INITIATE &&
           originaltransType != TransactionIfc.TYPE_LAYAWAY_DELETE &&
           originaltransType != TransactionIfc.TYPE_LAYAWAY_PAYMENT)
        {
            // Add accumulator information
            addAccumulatorActions(transaction);
        }

        if (!transaction.isTrainingMode() && till != null && register != null)
        {
            buildSummaryTotals(transaction, totals, till, register);
        }

        if (logger.isDebugEnabled()) logger.debug(
                    "TransactionWriteDataTransaction.addSaveVoidTransactionActions");
    }

    //---------------------------------------------------------------------
    /**
        Adds the data actions needed to save a canceled transaction.
        @param  transaction The canceled transaction to save
        @param totals financial totals for transaction
        @param till till on which payment was processed
        @param register register on which payment was processed
    **/
    //---------------------------------------------------------------------
    protected void addSaveCanceledTransactionActions(TransactionIfc transaction,
                                                     FinancialTotalsIfc totals,
                                                     TillIfc till,
                                                     RegisterIfc register)
    {

        /*
         * Save transaction to instance variable
         */
        artsTransaction = new ARTSTransaction((TransactionIfc) transaction);

        // Add a DataAction to save the canceled transaction
        DataActionIfc dataAction = createDataAction(artsTransaction, "SaveCanceledTransaction");
        actions.add(dataAction);

        if (!transaction.isTrainingMode() && till != null && register != null)
        {
            addSaveSummaryTotals(till, register, register.getTotals());
        }

    }

    //---------------------------------------------------------------------
    /**
     Adds the data actions needed to save a Enter/Exit Training Mode Transaction.
     @param  transaction The TransactionIfc to save
     @param totals financial totals for transaction
     @param till till on which payment was processed
     @param register register on which payment was processed
     **/
    //---------------------------------------------------------------------
    protected void addSaveEnterExitTrainingModeTransactionActions(TransactionIfc transaction,
            FinancialTotalsIfc totals,
            TillIfc till,
            RegisterIfc register)
    {
        /*
         * Save transaction to instance variable
         */
        artsTransaction = new ARTSTransaction(transaction);

        // Add a DataAction to save the enter/exit training mode transaction
        DataActionIfc dataAction = createDataAction(artsTransaction, "SaveEnterExitTrainingModeTransaction");
        actions.add(dataAction);
    }

    //---------------------------------------------------------------------
    /**
        Adds the data actions needed to save a NoSaleTransactionIfc.
        @param  transaction The NoSaleTransactionIfc to save
        @param totals financial totals for transaction
        @param till till on which payment was processed
        @param register register on which payment was processed
    **/
    //---------------------------------------------------------------------
    protected void addSaveNoSaleTransactionActions(NoSaleTransactionIfc transaction,
                                                   FinancialTotalsIfc totals,
                                                   TillIfc till,
                                                   RegisterIfc register)
    {
        /*
         * Save transaction to instance variable
         */
        artsTransaction = new ARTSTransaction(transaction);

        // Add a DataAction to save the no sale transaction
        DataActionIfc dataAction = createDataAction(artsTransaction, "SaveNoSaleTransaction");
        actions.add(dataAction);

        if (!transaction.isTrainingMode() && till != null && register != null)
        {
            totals = instantiateFinancialTotalsIfc();
            TillIfc              useTill = (TillIfc) till.clone();
            RegisterIfc      useRegister = (RegisterIfc) register.clone();
            totals.addNumberNoSales(1);

            useRegister.setTotals(totals);
            useTill.setTotals(totals);
            addSaveSummaryTotals(useTill, useRegister, totals);
        }
    }

    //---------------------------------------------------------------------
    /**
        Adds the data actions needed to save a control transaction
        @param  transaction a transaction
    **/
    //---------------------------------------------------------------------
    protected void addSaveControlTransactionActions(TransactionIfc transaction)
    {
        /*
         * Save transaction to instance variable
         */
        artsTransaction = new ARTSTransaction((TransactionIfc) transaction);

        // Add a DataAction to save the transaction
        DataActionIfc dataAction = createDataAction(artsTransaction, "SaveControlTransaction");
        actions.add(dataAction);
    }

    //---------------------------------------------------------------------
    /**
        Adds the data actions needed to save a StoreOpenCloseTransaction.
        @param  transaction a StoreOpenCloseTransaction
    **/
    //---------------------------------------------------------------------
    protected void addSaveStoreOpenCloseTransactionActions(TransactionIfc transaction)
    {
        //artsTransaction = new ARTSTransaction((TransactionIfc) transaction);
        
        StoreOpenCloseTransactionIfc storeTran = (StoreOpenCloseTransactionIfc)transaction;
    
        if (storeTran.getTransactionType() == TransactionIfc.TYPE_OPEN_STORE)
        {
        	switch (storeTran.getStoreOpenMode())
        	{
        		case StoreOpenCloseTransactionIfc.STORE_OPEN_MODE_ONLINE:
        		{
        			addStoreOpenCloseActions(storeTran);
        			break;
        		}
        		case StoreOpenCloseTransactionIfc.STORE_OPEN_MODE_OFFLINE:
        		{
        			addStoreOpenOfflineActions(storeTran);
        			break;
        		}
        		case StoreOpenCloseTransactionIfc.STORE_OPEN_MODE_DUPLICATE:
        		{
        			addStoreOpenDuplicateActions(storeTran);
        			break;
        		}
        	}
        }
        else
        {
        	addStoreOpenCloseActions(storeTran);
        	
        	// Following code was moved to the addStoreOpenCloseActions method to 
        	// support the store open modes
        	
            // save the control transaction
            //dataAction = createDataAction(artsTransaction,"SaveControlTransaction");
            //actions.add(dataAction);

            // this ensures that the change is backward compatible, because
            // only if store open-close transaction is used will the new data operations
            // be executed
            //if (transaction instanceof StoreOpenCloseTransactionIfc)
            //{
                // save the store open/close transaction
            //    dataAction = createDataAction(transaction,
            //                                  "SaveStoreOpenCloseTransaction");
            //    actions.add(dataAction);
                // update the store safe from the store open/close transaction
            //    dataAction = createDataAction(transaction,
            //                                  "UpdateSafeFromStoreOpenCloseTransaction");
            //    actions.add(dataAction);

                // save the tender media counts
            //    dataAction = createDataAction(transaction,
            //                                 "SaveStoreOpenCloseSafeTenderMedia");
            //    actions.add(dataAction);

            //    StoreStatusIfc storeStatus =
            //      ((StoreOpenCloseTransactionIfc) transaction).getStoreStatus();
                // if store open, create store totals
            //    if (transaction.getTransactionType() == TransactionIfc.TYPE_OPEN_STORE)
            //    {
            //        ARTSStore artsStore = new ARTSStore
            //          (storeStatus.getStore(),
            //           storeStatus.getBusinessDate());

            //        dataAction = createDataAction
            //          (artsStore,
            //           "CreateStoreTotals");
            //        actions.add(dataAction);
            //    }

                // update the store status
            //    dataAction = createDataAction
            //      (storeStatus,
            //       "UpdateStoreStatus");
            //    actions.add(dataAction);
            //}
        }
    }

    protected void addStoreOpenCloseActions(TransactionIfc transaction)
    {
        artsTransaction = new ARTSTransaction((TransactionIfc) transaction);

        // save the control transaction
        DataActionIfc dataAction = createDataAction(artsTransaction,"SaveControlTransaction");
        actions.add(dataAction);

        // this ensures that the change is backward compatible, because
        // only if store open-close transaction is used will the new data operations
        // be executed
        if (transaction instanceof StoreOpenCloseTransactionIfc)
        {
            // save the store open/close transaction
            dataAction = createDataAction(transaction,
                                          "SaveStoreOpenCloseTransaction");
            actions.add(dataAction);
            // update the store safe from the store open/close transaction
            dataAction = createDataAction(transaction,
                                          "UpdateSafeFromStoreOpenCloseTransaction");
            actions.add(dataAction);

            // save the tender media counts
            dataAction = createDataAction(transaction,
                                          "SaveStoreOpenCloseSafeTenderMedia");
            actions.add(dataAction);

            StoreStatusIfc storeStatus =
              ((StoreOpenCloseTransactionIfc) transaction).getStoreStatus();
            // if store open, create store totals
            if (transaction.getTransactionType() == TransactionIfc.TYPE_OPEN_STORE)
            {
                ARTSStore artsStore = new ARTSStore
                  (storeStatus.getStore(),
                   storeStatus.getBusinessDate());

                dataAction = createDataAction
                  (artsStore,
                   "CreateStoreTotals");
                actions.add(dataAction);
            }

            // update the store status
            dataAction = createDataAction
              (storeStatus,
               "UpdateStoreStatus");
            actions.add(dataAction);
        }
    	
    }
    
    protected void addStoreOpenOfflineActions(StoreOpenCloseTransactionIfc storeTran)
    {
        // Reguires special processing if opened offline
        DataActionIfc dataAction = createDataAction(storeTran, "OfflineStoreOpen");
        actions.add(dataAction);
    }
    
    protected void addStoreOpenDuplicateActions(StoreOpenCloseTransactionIfc transaction)
    {
        //This will save the duplicate StoreOpenTransaction without updating the 
        // financials and store the transaction as cancelled.
        
        // Make sure the status is cancelled
        transaction.setTransactionStatus(TransactionIfc.STATUS_CANCELED);
        
        // save the control transaction
        DataActionIfc dataAction = createDataAction(artsTransaction,"SaveControlTransaction");
        actions.add(dataAction);

        // this ensures that the change is backward compatible, because
        // only if store open-close transaction is used will the new data operations
        // be executed
            // save the store open/close transaction
            dataAction = createDataAction(transaction,
                                          "SaveStoreOpenCloseTransaction");
            
            // Don't update the financials or the store status information. Saving the 
            // duplicate to preserve the sequence numbers
            
            /*
            actions.add(dataAction);
            // update the store safe from the store open/close transaction
            dataAction = createDataAction(transaction,
                                          "UpdateSafeFromStoreOpenCloseTransaction");
            actions.add(dataAction);
           // save the tender media counts
            dataAction = createDataAction(transaction,
                                          "SaveStoreOpenCloseSafeTenderMedia");
            actions.add(dataAction);

            StoreStatusIfc storeStatus = transaction.getStoreStatus();
            // if store open, create store totals
            if (transaction.getTransactionType() == TransactionIfc.TYPE_OPEN_STORE)
            {
                ARTSStore artsStore = new ARTSStore
                  (storeStatus.getStore(),
                   storeStatus.getBusinessDate());

                dataAction = createDataAction
                  (artsStore,
                   "CreateStoreTotals");
                actions.add(dataAction);
            }

            // update the store status
            dataAction = createDataAction
              (storeStatus,
               "UpdateStoreStatus");
            actions.add(dataAction);
*/
    }
    
    
    //---------------------------------------------------------------------
    /**
        Adds the data actions needed to save a BankDepositTransaction.
        @param  transaction a BankDepositTransaction
    **/
    //---------------------------------------------------------------------
    protected void addSaveBankDepositTransactionActions(TransactionIfc transaction)
    {
        artsTransaction = new ARTSTransaction((TransactionIfc) transaction);

        // save the control transaction
        DataActionIfc dataAction = createDataAction(artsTransaction,
                                                    "SaveControlTransaction");
        actions.add(dataAction);

        // this ensures that the change is backward compatible, because
        // only if bank-deposit transaction is used will the new data operations
        // be executed
        if (transaction instanceof BankDepositTransactionIfc)
        {
            // update the store safe from the store open/close transaction
            dataAction = createDataAction(transaction,
                                          "UpdateSafeFromBankDepositTransaction");
            actions.add(dataAction);

            // save the tender media counts
            dataAction = createDataAction(transaction,
                                          "SaveBankDepositSafeTenderMedia");
            actions.add(dataAction);
        }
    }

    //---------------------------------------------------------------------
    /**
        Adds the data actions needed to save a register open/closeTransaction.
        @param  transaction a register open/closeTransaction
    **/
    //---------------------------------------------------------------------
    protected void addSaveRegisterOpenCloseTransactionActions(TransactionIfc transaction)
    {
        artsTransaction = new ARTSTransaction((TransactionIfc) transaction);

        // save the control transaction
        DataActionIfc dataAction = createDataAction(artsTransaction,
                                                    "SaveControlTransaction");
        actions.add(dataAction);

        // this ensures that the change is backward compatible, because
        // only if register open-close transaction is used will the new data operations
        // be executed
        if (transaction instanceof RegisterOpenCloseTransactionIfc)
        {
            // save the register open/close transaction
            dataAction = createDataAction(transaction,
                                          "SaveRegisterOpenCloseTransaction");
            actions.add(dataAction);
            RegisterIfc register =
              ((RegisterOpenCloseTransactionIfc) transaction).getRegister();
            // update the register status
            dataAction = createDataAction(register,
                                          "UpdateRegisterStatus");
            actions.add(dataAction);
            if (transaction.getTransactionType() == TransactionIfc.TYPE_OPEN_REGISTER)
            {
                // don't update totals in training mode
                if (!register.getWorkstation().isTrainingMode())
                {
                    // update the register totals
                    dataAction = createDataAction(register,
                                                  "AddRegisterTotals");
                    actions.add(dataAction);
                }
                // update the drawer
                dataAction = createDataAction(register,
                                              "UpdateDrawerStatus");
                actions.add(dataAction);
            }
            else
            {
                register.resetTotals();
                // don't update totals in training mode
                if (!register.getWorkstation().isTrainingMode())
                {
                    // update the register totals
                    dataAction = createDataAction(register,
                                                  "AddRegisterTotals");
                    actions.add(dataAction);
                }
            }
        }

    }

    //---------------------------------------------------------------------
    /**
        Adds the data actions needed to save a till open transaction.
        @param  transaction a till open transaction
    **/
    //---------------------------------------------------------------------
    protected void addSaveTillOpenTransactionActions(TransactionIfc transaction)
    {
        artsTransaction = new ARTSTransaction((TransactionIfc) transaction);

        // save the control transaction
        DataActionIfc dataAction = createDataAction(artsTransaction,
                                                    "SaveControlTransaction");
        actions.add(dataAction);

        // this ensures that the change is backward compatible, because
        // only if till open-close transaction is used will the new data operations
        // be executed
        if (transaction instanceof TillOpenCloseTransactionIfc)
        {
            if (!transaction.isTrainingMode())
            {
                // save the till open/close transaction
                dataAction = createDataAction(transaction,
                                              "SaveTillOpenCloseTransaction");
                actions.add(dataAction);
                // update the safe as needed
                dataAction = createDataAction(transaction,
                                              "UpdateSafeFromTillOpenCloseTransaction");
                actions.add(dataAction);
            }
            // build ARTS till for other operations
            TillIfc till = (TillIfc)
              ((TillOpenCloseTransactionIfc) transaction).getTill().clone();
            RegisterIfc register = (RegisterIfc)
              ((TillOpenCloseTransactionIfc) transaction).getRegister().clone();
            register.setTotals(till.getTotals());

            ARTSTill aTill = new ARTSTill(till, register);
            // creates or updates the till as needed
            dataAction = createDataAction(aTill,
                                          "CreateUpdateTill");
            actions.add(dataAction);
            // creates or updates the till totals as needed
            dataAction = createDataAction(aTill,
                                          "CreateUpdateTillTotals");
            actions.add(dataAction);

            if (!transaction.isTrainingMode())
            {
                dataAction = createDataAction(register, "SaveTillStartingFloat");
                actions.add(dataAction);
            }

            // update register status on till-open
            if (transaction.getTransactionType() == TransactionIfc.TYPE_OPEN_TILL)
            {
                // update the register and drawer
                dataAction = createDataAction(register,
                                              "UpdateRegisterStatus");
                actions.add(dataAction);
                // update the drawer
                dataAction = createDataAction(register,
                                              "UpdateDrawerStatus");
                actions.add(dataAction);
            }
        }

    }

    //---------------------------------------------------------------------
    /**
        Adds the data actions needed to save a till suspend/resume transaction.
        @param  transaction a till suspend/resume transaction
    **/
    //---------------------------------------------------------------------
    protected void addSaveTillSuspendResumeTransactionActions(TransactionIfc transaction)
    {
        artsTransaction = new ARTSTransaction((TransactionIfc) transaction);

        // save the control transaction
        DataActionIfc dataAction = createDataAction(artsTransaction,
                                                    "SaveControlTransaction");
        actions.add(dataAction);

        // this ensures that the change is backward compatible, because
        // only if till open-close transaction is used will the new data operations
        // be executed
        if (transaction instanceof TillOpenCloseTransactionIfc)
        {
            // save the till open/close transaction
            dataAction = createDataAction(transaction,
                                          "SaveTillOpenCloseTransaction");
            actions.add(dataAction);
            // build ARTS till for other operations
            TillIfc till =
              ((TillOpenCloseTransactionIfc) transaction).getTill();
            RegisterIfc register =
              ((TillOpenCloseTransactionIfc) transaction).getRegister();
            ARTSTill artsTill = new ARTSTill(till, register);

            // creates or updates the till as needed
            dataAction = createDataAction(artsTill,
                                          "UpdateTillStatus");
            actions.add(dataAction);

            // update the register
            dataAction = createDataAction(register,
                                          "UpdateRegisterStatus");
            actions.add(dataAction);

            // update the drawer
            dataAction = createDataAction(register,
                                          "UpdateDrawerStatus");
            actions.add(dataAction);
        }
    }

    //---------------------------------------------------------------------
    /**
        Adds the data actions needed to save a till close transaction.
        @param  transaction a till close transaction
    **/
    //---------------------------------------------------------------------
    protected void addSaveTillCloseTransactionActions(TransactionIfc transaction)
    {
        artsTransaction = new ARTSTransaction((TransactionIfc) transaction);

        // save the control transaction
        DataActionIfc dataAction = createDataAction(artsTransaction,
                                                    "SaveControlTransaction");
        actions.add(dataAction);

        // this ensures that the change is backward compatible, because
        // only if till open-close transaction is used will the new data operations
        // be executed
        if (transaction instanceof TillOpenCloseTransactionIfc)
        {
            TillOpenCloseTransactionIfc tocTransaction =
              (TillOpenCloseTransactionIfc) transaction;
            // save the till open/close transaction
            dataAction = createDataAction(transaction,
                                          "SaveTillOpenCloseTransaction");
            actions.add(dataAction);
            // build ARTS till for other operations
            TillIfc till =
              ((TillOpenCloseTransactionIfc) transaction).getTill();
            RegisterIfc register =
              ((TillOpenCloseTransactionIfc) transaction).getRegister();

            if (till.getStatus() == AbstractFinancialEntityIfc.STATUS_RECONCILED)
            {
                // update the safe as needed
                dataAction = createDataAction(transaction,
                                              "UpdateSafeFromTillOpenCloseTransaction");
                actions.add(dataAction);

                // Get deep copies of the till and register so they can be loaded
                // with the till-close totals
                TillIfc         aTill = (TillIfc) till.clone();
                RegisterIfc aRegister = (RegisterIfc) register.clone();

                // Combine the till and float totals objects
                FinancialTotalsIfc totals =
                  DomainGateway.getFactory().getFinancialTotalsInstance();
                totals.addEndingFloatCount(tocTransaction.getEndingFloatCount());
                totals.getCombinedCount().setEntered
                  (tocTransaction.getEndingCombinedEnteredCount());

                // Set the counted totals on the till and register.
                aTill.setTotals(totals);
                aRegister.setTotals(totals);
                ARTSTill artsTill = new ARTSTill(aTill, aRegister);

                // creates or updates the till as needed
                dataAction = createDataAction(artsTill,
                                              "UpdateTillStatus");
                actions.add(dataAction);

                // creates or updates the till totals as needed
                dataAction = createDataAction(artsTill,
                                              "UpdateTillTotals");
                actions.add(dataAction);
                // add to register totals
                dataAction = createDataAction(aRegister,
                                              "AddRegisterTotals");
                actions.add(dataAction);
                // add to store totals
                ARTSStore aStore = new ARTSStore(register.getWorkstation().getStore(),
                                                 register.getBusinessDate());
                aStore.setFinancialTotals(aRegister.getTotals());
                dataAction = createDataAction(aStore,
                                              "AddStoreTotals");
                actions.add(dataAction);

            }
            else
            {
                ARTSTill artsTill = new ARTSTill(till, register);

                // creates or updates the till as needed
                dataAction = createDataAction(artsTill,
                                              "UpdateTillStatus");
                actions.add(dataAction);
            }

            // update the register and drawer
            dataAction = createDataAction(register,
                                          "UpdateRegisterStatus");
            actions.add(dataAction);
            // update the drawer
            dataAction = createDataAction(register,
                                          "UpdateDrawerStatus");
            actions.add(dataAction);
        }

    }

    //---------------------------------------------------------------------
    /**
        When saving a TillAdjustmentTransactionIfc, you need to:
             Save to the transaction tables.
        @param  transaction the transaction
        @param totals financial totals for transaction
        @param till till on which payment was processed
        @param register register on which payment was processed
    **/
    //---------------------------------------------------------------------
    protected void addSaveTillAdjustmentTransactionActions(TillAdjustmentTransactionIfc transaction,
                                                           FinancialTotalsIfc totals,
                                                           TillIfc till,
                                                           RegisterIfc register)
    {
        // Save transaction to instance variable
        artsTransaction = new ARTSTransaction((TransactionIfc) transaction);

        String dataOperationName = null;

        switch(transaction.getTransactionType())
        {                               // begin set operations by type
            case TransactionIfc.TYPE_PAYIN_TILL:
            case TransactionIfc.TYPE_PAYOUT_TILL:
            case TransactionIfc.TYPE_PAYROLL_PAYOUT_TILL:
                dataOperationName = "SaveTillPayment";
                break;
            case TransactionIfc.TYPE_LOAN_TILL:
                dataOperationName = "SaveTillLoan";
                break;
            case TransactionIfc.TYPE_PICKUP_TILL:
                dataOperationName = "SaveTillPickup";
                break;
            default:
                logger.error("Unable to determine data operation for transaction of type " + TillAdjustmentTransactionIfc.TYPE_DESCRIPTORS[transaction.getTransactionType()]);
                break;
        }                               // end set operations by type

        actions.add(createDataAction(transaction, dataOperationName));

        // save tender media line items, if necessary
        if ((transaction.getTransactionType() == TransactionIfc.TYPE_LOAN_TILL ||
             transaction.getTransactionType() == TransactionIfc.TYPE_PICKUP_TILL) &&
            !(Util.isEmpty(dataOperationName)) &&
            transaction.getCountType() != FinancialCountIfc.COUNT_TYPE_NONE ||
            transaction.getTender().getTenderType() == TenderLineItemIfc.TENDER_TYPE_CHECK)
        {
            dataOperationName = "SaveTenderMediaLineItems";
            actions.add(createDataAction(transaction,
                                                     dataOperationName));
        }

        // update the financial totals in the database
        if (!transaction.isTrainingMode() && till != null && register != null)
        {
            buildSummaryTotals(transaction, totals, till, register);
        }

    }

    //---------------------------------------------------------------------
    /**
        Adds the data actions needed to save accumulator information
        for a transaction.
        @param  transaction The transaction to save
    **/
    //---------------------------------------------------------------------
    protected void addAccumulatorActions(TenderableTransactionIfc transaction)
    {
        int transType = transaction.getTransactionType();
        OrderTransaction orderTransaction=null;
        int orderType=0;
        if(transaction instanceof OrderTransaction)
        {
            orderTransaction=(OrderTransaction)transaction;
            orderType=orderTransaction.getOrderType();
        }
        AbstractTransactionLineItemIfc[] lineItems = null;
        boolean isNotVoid = true;
        boolean orderVoid = false;
        AbstractTransactionLineItemIfc[] orderLineItems = null;
        OrderIfc order = null;

        // the line item financial totals for a void are different
        if (transType == TransactionIfc.TYPE_VOID
            || orderType==OrderConstantsIfc.ORDER_TYPE_ON_HAND
            || transType == TransactionIfc.TYPE_SALE
            || transType == TransactionIfc.TYPE_RETURN
            || transType == TransactionIfc.TYPE_ORDER_PARTIAL
            || transType == TransactionIfc.TYPE_ORDER_COMPLETE
            || transType == TransactionIfc.TYPE_LAYAWAY_COMPLETE
            )
        {
            if (transType == TransactionIfc.TYPE_VOID)
            {
                VoidTransactionIfc voidTransaction = (VoidTransactionIfc)transaction;
                isNotVoid = false;
                lineItems = voidTransaction.getLineItems();
                if (voidTransaction.getOriginalTransaction().getTransactionType() ==
                    TransactionIfc.TYPE_ORDER_PARTIAL ||
                    voidTransaction.getOriginalTransaction().getTransactionType() ==
                    TransactionIfc.TYPE_ORDER_COMPLETE)
                {
                     orderVoid = true;
                }
            }
            else
            {
                lineItems = ((RetailTransactionIfc) transaction).getLineItems();
                // if order is complete, get all other items for order
                if(lineItems != null &&
                   transType == TransactionIfc.TYPE_ORDER_COMPLETE  &&
                   ((OrderTransactionIfc) transaction).getOrderStatus().getStatus().getPreviousStatus() !=
                                                  OrderConstantsIfc.ORDER_STATUS_NEW)
                   {

                       OrderReadDataTransaction readDataTransaction = null;

                       readDataTransaction = (OrderReadDataTransaction) DataTransactionFactory.create(DataTransactionKeys.ORDER_READ_DATA_TRANSACTION);

                       try
                       {
                    	   // use default locale in this case since accumulation of financial totals is not locale sensitive
                    	   LocaleRequestor localeReq = new LocaleRequestor(LocaleMap.getLocale(LocaleConstantsIfc.DEFAULT_LOCALE));
                           order = readDataTransaction.readOrder(
                                   ((OrderTransactionIfc)transaction).getOrderID(),
                                   ((OrderTransactionIfc)transaction).getOrderStatus().getInitiatingChannel(),
                                   localeReq,
                                   transaction.isTrainingMode());
                           orderLineItems = order.getLineItems();
                           if(orderLineItems != null)
                           {
                               lineItems = makeNewLineItemArray(orderLineItems, lineItems);
                           }
                       }
                       catch(DataException de)
                       {
                           logger.error(
                                        "TransactionWriteDataTransaction: addAccumulatorActions : " + de.getMessage() + "");
                       }
                   }
            }
        }

        /*
         * Combine the line items to make the departments unique
         */
        Map<String, FinancialTotalsIfc> deptTable = new HashMap<String, FinancialTotalsIfc>(1);
        int numItems = 0;

        if (lineItems != null)
        {
            numItems = lineItems.length;
        }

        ARTSAssociateProductivity assocProd = new ARTSAssociateProductivity();
        assocProd.setStoreID(transaction.getWorkstation().getStoreID());
        assocProd.setBusinessDate(transaction.getBusinessDay());
        if(((RetailTransactionIfc) transaction).getSalesAssociate() != null)
        {
            String empId = ((RetailTransactionIfc) transaction).getSalesAssociate().getEmployeeID();
            if ("1".equals(empId))//DEFAULT_ID for Employee
            {
            	//assign cashier employee
            	assocProd.setTransactionSalesAssociateID(((RetailTransactionIfc) transaction).
                        getCashier().getEmployeeID());
            }
            else
            {
            	assocProd.setTransactionSalesAssociateID(empId);
            }

        }
        assocProd.setWorkstationID(transaction.getWorkstation().getWorkstationID());

        ArrayList<SaleReturnLineItemIfc> lineItemsArrayList = new ArrayList<SaleReturnLineItemIfc>();
        int transStatus = transaction.getTransactionStatus();

        for (int i = 0; i < numItems; i++)
        {
            SaleReturnLineItemIfc lineItem = (SaleReturnLineItemIfc) lineItems[i];
            if (((transType == TransactionIfc.TYPE_ORDER_PARTIAL ||
                transType == TransactionIfc.TYPE_ORDER_COMPLETE) &&
                (lineItem.getOrderItemStatus().getStatus().getStatus() ==
                OrderConstantsIfc.ORDER_ITEM_STATUS_PICKED_UP)) ||
                ((transType == TransactionIfc.TYPE_VOID && orderVoid)  &&
                (lineItem.getOrderItemStatus().getStatus().getPreviousStatus() ==
                OrderConstantsIfc.ORDER_ITEM_STATUS_PICKED_UP)) ||
                ((transType == TransactionIfc.TYPE_VOID && !orderVoid) ||
                transType == TransactionIfc.TYPE_SALE ||
                transType == TransactionIfc.TYPE_RETURN ||
                transType == TransactionIfc.TYPE_LAYAWAY_COMPLETE) &&
                !lineItem.isKitHeader() && !lineItem.isGiftItem()
                || (lineItem.getOrderItemStatus().getItemDispositionCode() == OrderConstantsIfc.ORDER_ITEM_DISPOSITION_SALE 
                    && transStatus != TransactionIfc.STATUS_SUSPENDED
                    && orderType == OrderConstantsIfc.ORDER_TYPE_ON_HAND))
            {
                FinancialTotalsIfc itemTotals = lineItem.getFinancialTotals(isNotVoid);
                FinancialTotalsIfc deptTotals = null;
                String deptID = lineItem.getPLUItem().getDepartmentID();

                // if no department ID, create "unknown"
                if (deptID == null || deptID.length() == 0)
                {
                    deptID = new String("none");
                }
                if (deptTable.containsKey(deptID))
                {
                    deptTotals = (FinancialTotalsIfc)deptTable.get(deptID);
                }
                else
                {
                    deptTotals = instantiateFinancialTotalsIfc();
                    deptTable.put(deptID, deptTotals);
                }

                deptTotals.add(itemTotals);
                // Build array list for JdbcSaveAssociateProductivity
                // This is used to update the AssociateProductivity table and
                if (lineItem.getSalesAssociate() != null &&
                        lineItem.getSalesAssociate().getEmployeeID() != null &&
                        lineItem.getSalesAssociate().getEmployeeID().equals("1")) //DEFAULT_ID
                 {
                	lineItem.getSalesAssociate().setEmployeeID(assocProd.getTransactionSalesAssociateID());
                 }

                lineItemsArrayList.add(lineItem);
            }
        }
        assocProd.setLineItems(lineItemsArrayList);

        /*
         * Use the hashtable to build the vector to be passed to the
         * data transaction
         */
        departments = new ArrayList<ARTSDepartmentTotals>();
        Set<String> deptIDs = deptTable.keySet();
        for (String deptID : deptIDs)
        {
            FinancialTotalsIfc deptTotals = (FinancialTotalsIfc)deptTable.get(deptID);
            ARTSDepartmentTotals totals = new ARTSDepartmentTotals();
            totals.setDepartmentID(deptID);
            totals.setFinancialTotals(deptTotals);
            EYSDate businessDate = transaction.getBusinessDay();
            businessDate.setType(EYSDate.TYPE_DATE_ONLY);
            totals.setBusinessDate(businessDate);
            departments.add(totals);
        }

        DataActionIfc dataAction = null;
        if(!(transType == TransactionIfc.TYPE_ORDER_PARTIAL ||  // do not update department totals for partial order
             (transType == TransactionIfc.TYPE_VOID &&
              ((VoidTransactionIfc) transaction).getOriginalTransaction().getTransactionType() ==
                             TransactionIfc.TYPE_ORDER_PARTIAL)))
        {
            dataAction = createDataAction((Serializable)this.departments, "AddDepartmentTotals");
            actions.add(dataAction);
        }

        /*
         * Add accumulator information for Time Interval Activity
         */
        posTransaction = transaction;

        dataAction = createDataAction(this.posTransaction, "AddTimeIntervalTotals");
        actions.add(dataAction);

        if ((!(transType == TransactionIfc.TYPE_ORDER_PARTIAL || (transType == TransactionIfc.TYPE_VOID && ((VoidTransactionIfc)transaction)
                .getOriginalTransaction().getTransactionType() == TransactionIfc.TYPE_ORDER_PARTIAL)))
                || transType == TransactionIfc.TYPE_ORDER_PARTIAL && transaction instanceof OrderTransactionIfc)
        {
            dataAction = createDataAction(assocProd, "SaveAssociateProductivity");
            actions.add(dataAction);
        }
    }
    //---------------------------------------------------------------------
    /**
        Makes a new line item array with order and line items.
        @param lineItems all line items
        @param orderLineItems all order line items
    **/
    //---------------------------------------------------------------------
    protected AbstractTransactionLineItemIfc[] makeNewLineItemArray(AbstractTransactionLineItemIfc[] orderLineItems,
                                                                    AbstractTransactionLineItemIfc[] lineItems)
    {
        int newSize = orderLineItems.length + lineItems.length;
        AbstractTransactionLineItemIfc[] newLineItemArray = new AbstractTransactionLineItemIfc[newSize];
        int count = 0;
        for(int i = 0;i < lineItems.length;i++)
        {
            newLineItemArray[count] = lineItems[i];
            count++;
        }
        for(int i = 0;i < orderLineItems.length;i++)
        {
            newLineItemArray[count] = orderLineItems[i];
            count++;
        }
        return(newLineItemArray);
    }

    //---------------------------------------------------------------------
    /**
        Sets up the the summary totals data actions
        @param  transaction  The retail transaction that contains the totals
                             to save
        @param  totals financial totals for the transaction
        @param  TillIfc      till the till to save
        @param  RegisterIfc  register the register to save
    **/
    //---------------------------------------------------------------------
    protected void buildSummaryTotals(TenderableTransactionIfc transaction,
                                      FinancialTotalsIfc totals,
                                      TillIfc till,
                                      RegisterIfc register)
    {
        if (totals == null)
        {
            // Clone the financial totals object.
            totals =
                (FinancialTotalsIfc)transaction.getFinancialTotals().clone();
        }

        TillIfc              useTill = (TillIfc) till.clone();
        RegisterIfc      useRegister = (RegisterIfc) register.clone();
        totals = obtainVoidCount(transaction, totals);
        useRegister.setTotals(totals);
        useTill.setTotals(totals);
        addSaveSummaryTotals(useTill, useRegister, totals);
    }

    //---------------------------------------------------------------------
    /**
        Sets up the the summary totals with totals already calculated
        @param  totals financial totals for the transaction
        @param  TillIfc      till the till to save
        @param  RegisterIfc  register the register to save
    **/
    //---------------------------------------------------------------------
    protected void buildSummaryTotals(FinancialTotalsIfc totals,
                                      TillIfc till,
                                      RegisterIfc register)
    {
        TillIfc              useTill = (TillIfc) till.clone();
        RegisterIfc      useRegister = (RegisterIfc) register.clone();
        useRegister.setTotals(totals);
        useTill.setTotals(totals);
        addSaveSummaryTotals(useTill, useRegister, totals);
    }

    //---------------------------------------------------------------------
    /**
        Adds the data actions needed to save accumulator information
        for a transaction.
        @param  TillIfc      till the till to save
        @param  RegisterIfc  register the register to save
    **/
    //---------------------------------------------------------------------
    protected void addSaveSummaryTotals(TillIfc till,
                                        RegisterIfc register,
                                        FinancialTotalsIfc totals)
    {
        // Create the Update Till Totals data action
        ARTSTill aTill = new ARTSTill(till, register);
        actions.add(createDataAction(aTill, UPDATE_TILL_TOTALS));

        // Create the Update Register Totals data action
        actions.add(createDataAction(register, ADD_REGISTER_TOTALS));

        // Create the Update Store Totals data action
        ARTSStore aStore = new ARTSStore(register.getWorkstation().getStore(), register.getBusinessDate());
        aStore.setFinancialTotals(totals);
        actions.add(createDataAction(aStore, ADD_STORE_TOTALS));

       // Save Tax History
       totals.getTaxes().setBusinessDate(register.getBusinessDate());
       totals.getTaxes().setWorkstationId(register.getWorkstation().getWorkstationID());
       totals.getTaxes().setStoreId(register.getWorkstation().getStoreID());
       totals.getTaxes().setTillId(till.getTillID());
       DataActionIfc dataAction = createDataAction(totals.getTaxes(), "SaveTaxHistory");
       actions.add(dataAction);
    }

    //---------------------------------------------------------------------
    /**
        Updates transaction status and update timestamp (but no other
        columns). <P>
        @param transaction TransactionIfc object
        @param till till to record transaction totals
        @param register register against which totals are to be recorded
        @exception DataException is thrown if error occurs
    **/
    //---------------------------------------------------------------------
    public void addSaveTransactionStatusActions(StatusChangeTransactionIfc transaction,
            TillIfc till, RegisterIfc register)
            throws DataException
    {                                   // begin updateTransactionStatus()
        if (logger.isDebugEnabled()) logger.debug(
                     "TransactionWriteDataTransaction.addSaveTransactionStatusActions");

        // Add a DataAction to update the transaction
        DataAction da = createDataAction(transaction, "SaveTransactionStatus");
        actions.add(da);

        TransactionSummaryIfc summary = transaction.getTransactionSummaries().get(0);
        if (summary.getTransactionStatus() == TransactionConstantsIfc.STATUS_SUSPENDED_CANCELED)
        {
            List<TransactionSummaryIfc> summaries = transaction.getTransactionSummaries();
            for(TransactionSummaryIfc ts: summaries)
            {
                if (!Util.isEmpty(ts.getInternalOrderID()) ||
                    !Util.isEmpty(ts.getLayawayID()))
                {
                    //This is required for queue reporting purposes.
                    artsTransaction = new ARTSTransaction(transaction);

                    // Add a DataAction to update the transaction
                    da = createDataAction(transaction, "CancelSuspendedOrdersAndLayaways");
                    actions.add(da);
                    break;
                }
            }
        }

        if (!transaction.isTrainingMode() && till != null && register != null)
        {
            addSaveSummaryTotals(till, register, register.getTotals());
        }

        if (logger.isDebugEnabled()) logger.debug(
                    "TransactionWriteDataTransaction.addSaveTransactionStatusActions");
    }                                   // end updateTransactionStatus()

    /**
     * Update the order status using the Order Status Change Transaction.
     * @param transaction
     * @param till
     * @param register
     */
    protected void addSaveOrderStatusActions(OrderStatusChangeTransactionIfc transaction)
    {
        if (logger.isDebugEnabled()) logger.debug(
                "TransactionWriteDataTransaction.addSaveOrderStatusActions");
        
        // Add the DataActions to update the transaction
        actions.add(createDataAction(transaction, "SaveTransactionStatus"));
        actions.add(createDataAction(transaction.getOrder(), "UpdateOrder"));
        actions.add(createDataAction(transaction, "InsertOrderStatusByTransaction"));
        if (logger.isDebugEnabled()) logger.debug(
                "TransactionWriteDataTransaction.addSaveOrderStatusActions");
    }

    //---------------------------------------------------------------------
    /**
        Updates transaction status and update timestamp (but no other
        columns). <P>
        @param transaction TransactionIfc object
        @exception DataException is thrown if error occurs
        @deprecated in 14.0 no longer used.
    **/
    //---------------------------------------------------------------------
    public void updateTransactionStatus(TransactionIfc transaction) throws DataException
    {                                   // begin updateTransactionStatus()
        //This is required for queue reporting purposes.
        artsTransaction = new ARTSTransaction(transaction);

        // Add a DataAction to update the transaction
        DataActionIfc[] dataActions = new DataActionIfc[1];
        DataAction da = new DataAction();
        da.setDataOperationName("UpdateTransactionStatus");
        da.setDataObject(transaction);
        dataActions[0] = da;
        setDataActions(dataActions);
        getDataManager().execute(this);
    }                                   // end updateTransactionStatus()

    //---------------------------------------------------------------------
    /**
        Updates transaction status and update timestamp (but no other
        columns). <P>
        @param transaction TransactionIfc object
        @param till till to record transaction totals
        @param register register against which totals are to be recorded
        @exception DataException is thrown if error occurs
        @deprecated in 14.0 no longer used.
    **/
    //---------------------------------------------------------------------
    public void updateTransactionStatus(TransactionIfc transaction, TillIfc till, RegisterIfc register)
            throws DataException
    {                                   // begin updateTransactionStatus()
        if (logger.isDebugEnabled()) logger.debug(
                     "TransactionWriteDataTransaction.updateTransactionStatus");

        //This is required for queue reporting purposes.
        artsTransaction = new ARTSTransaction(transaction);

        actions = new ArrayList<DataActionIfc>();

        // Add a DataAction to update the transaction
        DataAction da = new DataAction();
        da.setDataOperationName("UpdateTransactionStatus");
        da.setDataObject(transaction);
        actions.add(da);

        if (!transaction.isTrainingMode() && till != null && register != null)
        {
            addSaveSummaryTotals(till, register, register.getTotals());
        }

        DataActionIfc[] dataActions = new DataActionIfc[actions.size()];
        dataActions = actions.toArray(dataActions);
        setDataActions(dataActions);
        getDataManager().execute(this);

        if (logger.isDebugEnabled()) logger.debug(
                    "TransactionWriteDataTransaction.updateTransactionStatus");
    }                                   // end updateTransactionStatus()

    //---------------------------------------------------------------------
    /**
        Cancels all suspended transactions, setting their status to
        suspended-canceled.
        @param transaction TransactionIfc object
        @exception DataException is thrown if error occurs
    **/
    //---------------------------------------------------------------------
    public void cancelSuspendedTransactions(TransactionIfc transaction) throws DataException
    {                                   // begin cancelSuspendedTransactions()
        //This is required for queue reporting purposes.
        artsTransaction = new ARTSTransaction(transaction);

        // Add a DataAction to update the transaction
        DataActionIfc[] dataActions = new DataActionIfc[1];
        DataAction da = createDataAction(transaction, "CancelSuspendedTransactions");
        dataActions[0] = da;
        setDataActions(dataActions);
        getDataManager().execute(this);
    }                                   // end cancelSuspendedTransactions()

    //---------------------------------------------------------------------
    /**
        Cancels all suspended transactions, setting their status to
        suspended-canceled.
        @param storeID store identifier
        @param business date
        @exception DataException is thrown if error occurs
    **/
    //---------------------------------------------------------------------
    public void cancelSuspendedTransactions(String storeID,
                                            EYSDate businessDate) throws DataException
    {                                   // begin cancelSuspendedTransactions()
        // set up transaction to use as key
        TransactionIfc transaction = instantiateTransactionIfc();
        WorkstationIfc workstation = instantiateWorkstationIfc();
        StoreIfc store = DomainGateway.getFactory().getStoreInstance();
        store.setStoreID(storeID);
        workstation.setStore(store);
        transaction.setWorkstation(workstation);
        transaction.setBusinessDay(businessDate);
        cancelSuspendedTransactions(transaction);
    }                                   // end cancelSuspendedTransactions()

    /**
     * Add the save gift certificate data action if required.
     * @param transaction
     */
    protected void addSaveGiftCertificateDataAction(ARTSTransaction artsTransaction)
    {
        boolean actionAdded = false;
        // Save Gift Certificate information associated with tender line items.
        TenderableTransactionIfc transaction = (TenderableTransactionIfc)artsTransaction.getPosTransaction();
        TenderLineItemIfc[] tenderLineItems = transaction.getTenderLineItems();
        for (TenderLineItemIfc tenderLineItem: tenderLineItems)
        {
            if (tenderLineItem instanceof TenderGiftCertificateIfc)
            {
                DataAction dataAction = createDataAction(artsTransaction, "SaveGiftCertificate");
                actions.add(dataAction);
                actionAdded = true;
                break;
            }
        }

        // Save gift certificate data associated with a redeemed tender
        if (!actionAdded && transaction instanceof RedeemTransactionIfc)
        {
            if (((RedeemTransactionIfc)transaction).getRedeemTender() instanceof TenderGiftCertificateIfc)
            {
                DataAction dataAction = createDataAction(artsTransaction, "SaveGiftCertificate");
                actions.add(dataAction);
                actionAdded = true;
            }
        }

        // If the transaction is a void transaction, get the original transaction
        // which contains the sale return line items.
        TenderableTransactionIfc localTransaction = transaction;
        if (transaction instanceof VoidTransactionIfc)
        {
            localTransaction = ((VoidTransactionIfc)transaction).getOriginalTransaction();
        }

        // Save Gift Certificate information associated with sale return line items.
        if (!actionAdded && localTransaction instanceof SaleReturnTransactionIfc)
        {
            AbstractTransactionLineItemIfc[] transLineItems =
                ((SaleReturnTransactionIfc)localTransaction).getItemContainerProxy().getLineItems();

            for (AbstractTransactionLineItemIfc transLineItem: transLineItems)
            {
                if (transLineItem instanceof SaleReturnLineItemIfc)
                {
                    if (((SaleReturnLineItemIfc)transLineItem).getPLUItem() instanceof GiftCertificateItemIfc)
                    {
                        DataAction dataAction = createDataAction(artsTransaction, "SaveGiftCertificate");
                        actions.add(dataAction);
                        break;
                    }
                }
            }

        }
    }

    /**
     * Add the save Store Credit data action if required.
     * @param transaction
     */
    protected void addSaveStoreCreditDataAction(ARTSTransaction artsTransaction)
    {
        boolean actionAdded = false;
        TenderableTransactionIfc transaction = (TenderableTransactionIfc)artsTransaction.getPosTransaction();

        // Save gift certificate data associated with a redeemed tender
        if (transaction instanceof RedeemTransactionIfc)
        {
            if (((RedeemTransactionIfc)transaction).getRedeemTender() instanceof TenderStoreCreditIfc)
            {
                DataAction dataAction = createDataAction(artsTransaction, "SaveGiftCertificate");
                actions.add(dataAction);
                actionAdded = true;
            }
        }

        if (!actionAdded)
        {
            // Save store credit information associated with tender line items.
            TenderLineItemIfc[] tenderLineItems = transaction.getTenderLineItems();
            for (TenderLineItemIfc tenderLineItem: tenderLineItems)
            {
                if (tenderLineItem instanceof TenderStoreCreditIfc)
                {
                    DataAction dataAction = createDataAction(artsTransaction, "SaveStoreCredit");
                    actions.add(dataAction);
                    break;
                }
            }
        }
    }

    //---------------------------------------------------------------------
    /**
        Increments Workstation Sequence Number to be used by the
        transaction. This method is only used by BackOffice
        @param transaction
        @param business date
        @exception DataException is thrown if error occurs
    **/
    //---------------------------------------------------------------------
    public Long incrementWorkstationSequenceNumber(TransactionIfc transaction)
        throws DataException
    {
        Long seqNumber = new Long(0);

        //This is required for queue reporting purposes.
        artsTransaction = new ARTSTransaction(transaction);

        // Add a DataAction to update the transaction
        DataActionIfc[] dataActions = new DataActionIfc[1];
        DataAction da = createDataAction(transaction, "IncrementWorkstationSequenceNumber");
        dataActions[0] = da;
        setDataActions(dataActions);
        seqNumber = (Long) getDataManager().execute(this);
        return (seqNumber);

    }

    //---------------------------------------------------------------------
    /**
       Updates batch IDs for the listed transactions.  A count of records
       updated is returned. <P>
       @param transactionEntries array of transaction entries
       @return count of records updated
       @exception DataException thrown if error occurs
    **/
    //---------------------------------------------------------------------
    public Integer updateTransactionBatchIDs
      (POSLogTransactionEntryIfc[] transactions)
    throws DataException
    {
    	return updateTransactionBatch(transactions, "UpdateTransactionBatchIDs");
    }

    //---------------------------------------------------------------------
    /**
       Updates batch IDs for the listed transactions.  A count of records
       updated is returned. <P>
       @param transactionEntries array of transaction entries
       @return count of records updated
       @exception DataException thrown if error occurs
    **/
    //---------------------------------------------------------------------
    public Integer updateTransactionTLogIDs
      (POSLogTransactionEntryIfc[] transactions)
    throws DataException
    {
    	return updateTransactionBatch(transactions, "UpdateTransactionTLogIDs");
    }

    //---------------------------------------------------------------------
    /**
       Updates batch IDs for the listed transactions.  A count of records
       updated is returned. <P>
       @param transactionEntries array of transaction entries
       @return count of records updated
       @exception DataException thrown if error occurs
    **/
    //---------------------------------------------------------------------
    public Integer updateTransactionRTLogIDs
      (POSLogTransactionEntryIfc[] transactions,boolean incrementCount)
    throws DataException
    {
        Integer batchCount = Integer.valueOf(0);
        // set batch ID in initial transaction in list
        if (transactions.length > 0)
        {
            DataActionIfc[] dataActions = new DataActionIfc[2];
            DataAction da = createDataAction(transactions,
                    "UpdateTransactionRTLogIDs");
            dataActions[0] = da;
            setDataActions(dataActions);

            if(incrementCount)
            {
            da = createDataAction(transactions[0],
                    "IncrementRTLogBatchCount");
            dataActions[1] = da;
            setDataActions(dataActions);
            }

            batchCount = (Integer) getDataManager().execute(this);
        }
        return(batchCount);
    }
    
    //---------------------------------------------------------------------
    /**
       Updates batch IDs for the listed transactions.  A count of records
       updated is returned. <P>
       @param transactionEntries array of transaction entries
       @return count of records updated
       @exception DataException thrown if error occurs
    **/
    //---------------------------------------------------------------------
    public Integer updateTransactionSIMTLogIDs
      (POSLogTransactionEntryIfc[] transactions)
    throws DataException
    {
        Integer batchCount = Integer.valueOf(0);
        // set batch ID in initial transaction in list
        if (transactions.length > 0)
        {
            DataActionIfc[] dataActions = new DataActionIfc[1];
            DataAction da = createDataAction(transactions,
                    "UpdateTransactionSIMTLogIDs");
            dataActions[0] = da;
            setDataActions(dataActions);

            batchCount = (Integer) getDataManager().execute(this);
        }
        return(batchCount);
    }

    //---------------------------------------------------------------------
    /**
       Updates batch IDs for the listed transactions.  A count of records
       updated is returned. <P>
       @param transactionEntries array of transaction entries
       @param batchType the name of the data operation/action
       @return count of records updated
       @exception DataException thrown if error occurs
    **/
    //---------------------------------------------------------------------
    public Integer updateTransactionBatch
      (POSLogTransactionEntryIfc[] transactions, String batchType)
    throws DataException
    {                                   // begin updateTransactionBatchIDs()
        Integer batchCount = Integer.valueOf(0);
        // set batch ID in initial transaction in list
        if (transactions.length > 0)
        {
            DataActionIfc[] dataActions = new DataActionIfc[1];
            DataAction da = createDataAction(transactions,
                                             batchType);
            dataActions[0] = da;
            setDataActions(dataActions);
            batchCount = (Integer) getDataManager().execute(this);
        }
        return(batchCount);
    }                                   // end updateTransactionBatchIDs()

    //---------------------------------------------------------------------
    /**
       Updates batch ID for the listed transaction.  A count of records
       updated is returned. <P>
       @param transactionEntry transaction entry
       @return count of records updated
       @exception DataException thrown if error occurs
    **/
    //---------------------------------------------------------------------
    public Integer updateTransactionBatchID
      (POSLogTransactionEntryIfc transaction)
    throws DataException
    {                                   // begin updateTransactionBatchID()
        POSLogTransactionEntryIfc[] entries =
          new POSLogTransactionEntryIfc[1];
        entries[0] = transaction;
        return(updateTransactionBatchIDs(entries));
    }                                   // end updateTransactionBatchID()

    //---------------------------------------------------------------------
    /**
       Updates batch IDs for the order transactions.  A count of records
       updated is returned. <P>
       @param transactionEntries array of transaction entries
       @return count of records updated
       @exception DataException thrown if error occurs
    **/
    //---------------------------------------------------------------------
    public Integer markInventoryReservationOrderList
      (POSLogTransactionEntryIfc[] transactions)
    throws DataException
    {
        return updateTransactionBatch(transactions, "MarkInventoryReservationOrderList");
    }

    //---------------------------------------------------------------------
    /**
        Adds the data actions needed to save a canceled sale/return transaction.
        @param  transaction The canceled transaction to save
        @param totals financial totals for transaction
        @param till till on which payment was processed
        @param register register on which payment was processed
        When saving you need to:
            <li> Save to the transaction tables
            <li> Save each line item
            <li> Save each tender line item
            <li> Optionally Save Accumulator information for Till and Register
        <p>
        @param  transaction the transaction
        @param totals financial totals for transaction
        @param till till on which payment was processed
        @param register register on which payment was processed
    **/
    //---------------------------------------------------------------------
    protected void addSaveSaleReturnCanceledTransactionActions(SaleReturnTransactionIfc transaction,
                                                       FinancialTotalsIfc totals,
                                                       TillIfc till,
                                                       RegisterIfc register)
    {
        artsTransaction = new ARTSTransaction(transaction);
        // Add a DataAction to save the SaleReturnTransactionIfc
        DataAction dataAction = new DataAction();
        dataAction.setDataOperationName("SaveRetailTransaction");
        dataAction.setDataObject(artsTransaction);
        actions.add(dataAction);

        // Add a DataAction to save all the line items in the Transaction
        dataAction = new DataAction();
        dataAction.setDataOperationName("SaveRetailTransactionLineItems");
        dataAction.setDataObject(artsTransaction);
        actions.add(dataAction);

        // Add a DataAction to save all the tender line items in the Transaction
        DataActionIfc da = new SaveTenderLineItemsAction(this, artsTransaction);
        actions.add(da);

        //Add a DataAction to save store credit in the Transaction
        addSaveStoreCreditDataAction(artsTransaction);

        if (!transaction.isTrainingMode() && till != null && register != null)
        {
            buildSummaryTotals(transaction, totals, till, register);
        }

    }

    //---------------------------------------------------------------------
    /**
        When saving a canceled OrderTransactionIfc, you need to:
            <li> Save the retail transaction
            <li> Save each tender line item
            <li> Optionally Save Accumulator information for Till and Register
        <p>
        @param  transaction the transaction
        @param totals financial totals for transaction
        @param till till on which payment was processed
        @param register register on which payment was processed
        @exception throws data exception if error occurs
    **/
    //---------------------------------------------------------------------
    protected void addSaveOrderCanceledTransactionActions(OrderTransactionIfc transaction,
                                                  FinancialTotalsIfc totals,
                                                  TillIfc till,
                                                  RegisterIfc register) throws DataException
    {
        artsTransaction = new ARTSTransaction(transaction);

        // Add a DataAction to save the SaleReturnTransactionIfc
        DataAction dataAction = createDataAction(artsTransaction, "SaveRetailTransaction");
        actions.add(dataAction);

        // Add a DataAction to save all the tender line items in the Transaction
        DataActionIfc da = new SaveTenderLineItemsAction(this, artsTransaction);
        actions.add(da);

        if (!transaction.isTrainingMode() &&
            till != null &&
            register != null)
        {
            buildSummaryTotals(transaction, totals, till, register);
        }

    }

    /**
     * When saving a RedeemTransactionIfc, you need to:
     * <li> save the redeem transaction
     * <li> save the tenderlineitems
     * <li> save the store credits
     * <li> and build summary totals
     * @param transaction
     * @param totals
     * @param till
     * @param register
     * @throws DataException
     */
    protected void addSaveRedeemTransactionActions(RedeemTransactionIfc transaction,
                                FinancialTotalsIfc totals,
                                TillIfc till,
                                RegisterIfc register) throws DataException
    {
        artsTransaction = new ARTSTransaction(transaction);

        // Add a DataAction to save the SaleReturnTransactionIfc
        DataAction dataAction = createDataAction(artsTransaction, "SaveRedeemTransaction");
        actions.add(dataAction);

        // Add a DataAction to save all the tender line items in the Transaction
        DataActionIfc da = new SaveTenderLineItemsAction(this, artsTransaction);
        actions.add(da);

        //Add a DataAction to save store credit in the Transaction
        addSaveStoreCreditDataAction(artsTransaction);
        addSaveGiftCertificateDataAction(artsTransaction);

        if (!transaction.isTrainingMode() &&
                till != null &&
                register != null)
        {

        	if (totals == null)
            {
                // Clone the financial totals object.
                totals =
                    (FinancialTotalsIfc)transaction.getFinancialTotals().clone();
            }

        	TenderLineItemIfc redeemTender = transaction.getRedeemTender();

        	if (redeemTender instanceof TenderStoreCreditIfc)
            {
        		TenderStoreCreditIfc storeCredit = (TenderStoreCreditIfc) transaction.getRedeemTender();
                if(storeCredit.getAlternateCurrencyTendered() != null)
                {
                	Iterator iterator = totals.getCombinedCount().getExpected().getTenderItemsIterator();
            		while(iterator.hasNext())
            		{
            			FinancialCountTenderItemIfc tender = (FinancialCountTenderItemIfc)iterator.next();
            			if (tender.getTenderType() == storeCredit.getTypeCode())
            			{
            				tender.setAmountIn(storeCredit.getAlternateCurrencyTendered());
            				tender.getTenderDescriptor().setCountryCode(storeCredit.getAlternateCurrencyTendered().getCountryCode());
            				tender.getTenderDescriptor().setCurrencyID(storeCredit.getAlternateCurrencyTendered().getType().getCurrencyId());
            			}
            		}
                }
            }
            TillIfc              useTill = (TillIfc) till.clone();
            RegisterIfc      useRegister = (RegisterIfc) register.clone();
            totals = obtainVoidCount(transaction, totals);
            useRegister.setTotals(totals);
            useTill.setTotals(totals);
            addSaveSummaryTotals(useTill, useRegister, totals);
        }
    }

    //---------------------------------------------------------------------
    /**
        Creates a new TransactionIfc object.
        @return a new TransactionIfc object.
    **/
    //---------------------------------------------------------------------
    public TransactionIfc instantiateTransactionIfc()
    {
        return(DomainGateway.getFactory().getTransactionInstance());
    }

    //---------------------------------------------------------------------
    /**
        Creates a new WorkstationIfc object.
        @return a new WorkstationIfc object.
    **/
    //---------------------------------------------------------------------
    public WorkstationIfc instantiateWorkstationIfc()
    {
        return(DomainGateway.getFactory().getWorkstationInstance());
    }

    //---------------------------------------------------------------------
    /**
        Creates a new FinancialTotalsIfc object.
        @return a new FinancialTotalsIfc object.
    **/
    //---------------------------------------------------------------------
    public FinancialTotalsIfc instantiateFinancialTotalsIfc()
    {
        return(DomainGateway.getFactory().getFinancialTotalsInstance());
    }

    //---------------------------------------------------------------------
    /**
         Returns the Financial total with the void count updated
         @param totals FinancialTotalsIfc object
         @param transaction TenderableTransactionIfc object
         @return totals FinancialTotalsIfc object
    **/
    //----------------------------------------------------------------------
    protected FinancialTotalsIfc obtainVoidCount(TenderableTransactionIfc transaction, FinancialTotalsIfc totals)
    {
        FinancialTotalsIfc totalsWithVoidCount = null;
        if (transaction.getTransactionStatus() == TransactionIfc.STATUS_CANCELED)
        {
            boolean taxable = true;
            if (transaction instanceof SaleReturnTransactionIfc)
            {
                if (((SaleReturnTransactionIfc)transaction).getTransactionTax() != null)
                {
                    if (((SaleReturnTransactionIfc)transaction).getTransactionTax().getTaxMode() == TaxIfc.TAX_MODE_EXEMPT
                            || ((SaleReturnTransactionIfc)transaction).getTransactionTax().getTaxMode() == TaxIfc.TAX_MODE_NON_TAXABLE
                            || (((SaleReturnTransactionIfc)transaction).getTransactionTax().getTaxMode() == TaxIfc.TAX_MODE_OVERRIDE_RATE && ((SaleReturnTransactionIfc)transaction)
                                    .getTransactionTax().getOverrideRate() == 0.0))
                    {
                        taxable = false;
                    }
                }
                else if (((SaleReturnTransactionIfc)transaction).getTransactionTax() == null)
                {
                    taxable = false;
                }

                if (taxable)
                {
                    if (transaction.getTransactionType() == TransactionIfc.TYPE_SALE)
                    {
                        totals.setCountGrossTaxableTransactionSalesVoided(totals.getNumberPostVoids());
                    }
                    if (transaction.getTransactionType() == TransactionIfc.TYPE_RETURN)
                    {
                        totals.setCountGrossTaxableTransactionReturnsVoided(totals.getNumberPostVoids());
                    }
                }
                else
                {
                    if (transaction.getTransactionType() == TransactionIfc.TYPE_SALE)
                    {
                        totals.setCountGrossNonTaxableTransactionSalesVoided(totals.getNumberPostVoids());
                    }
                    if (transaction.getTransactionType() == TransactionIfc.TYPE_RETURN)
                    {
                        totals.setCountGrossNonTaxableTransactionReturnsVoided(totals.getNumberPostVoids());
                    }
                }
            }
        }
        totalsWithVoidCount = totals;
        return totalsWithVoidCount;
    }

    /**
     *  Saves a POS Bill Pay Transaction to the data store.
    **/
    private void saveBillPayTransactionActions(TransactionIfc transaction, FinancialTotalsIfc totals, TillIfc till, RegisterIfc register) {

        artsTransaction = new ARTSTransaction(transaction);
         // Add a DataAction to save the SaleReturnTransactionIfc
         DataAction dataAction = new DataAction();
         dataAction.setDataOperationName("SaveBillPayTransaction");
         dataAction.setDataObject(artsTransaction);
         actions.add(dataAction);

         // Add a DataAction to save all the tender line items in the Transaction
         DataActionIfc da = new SaveTenderLineItemsAction(this, artsTransaction);
         actions.add(da);

         //Add a DataAction to save store credit in the Transaction
         //dataAction = createDataAction(artsTransaction, "SaveStoreCredit");
         //actions.add(dataAction);

         if (!transaction.isTrainingMode() && till != null && register != null)
         {
             buildSummaryTotals((TenderableTransactionIfc)transaction, totals, till, register);
         }
    }

    //---------------------------------------------------------------------
    /**
        Returns the revision number of this class.
        @return String representation of revision number
    **/
    //---------------------------------------------------------------------
    public String getRevisionNumber()
    {
        return(Util.parseRevisionNumber(revisionNumber));
    }

    //---------------------------------------------------------------------
    /**
       Method to default display string function.
       @return String representation of object
    **/
    //---------------------------------------------------------------------
    public String toString()
    {
        // result string
        String strResult = new String("Class: TransactionWriteDataTransaction (Revision "
                                      + getRevisionNumber() + ") @" + hashCode());
        return(strResult);
    }
}
