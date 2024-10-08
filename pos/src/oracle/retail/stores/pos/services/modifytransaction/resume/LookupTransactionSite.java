/* ===========================================================================
* Copyright (c) 2008, 2014, Oracle and/or its affiliates. All rights reserved.
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/modifytransaction/resume/LookupTransactionSite.java /main/51 2014/06/03 17:06:11 asinton Exp $
 * ===========================================================================
 * NOTES <other useful comments, qualifications, etc.>
 *
 * MODIFIED (MM/DD/YY)
 *    jswan     07/17/14 - limit the number of Customer Gift Lists retrieved by ICE.
 *    asinto 06/03/14 - added extended customer data locale to the
 *                      CustomerSearchCriteriaIfc.
 *    cgreen 05/14/14 - rename retrieve to resume
 *    ohorne 03/07/14 - Removed logic to create new orderId in setNewTransaction(..)
 *    kavdur 02/18/14 - Fix for missing transaction in tr_trn table when client
 *                      closed abruptly after retrieval of suspended
 *                      transaction. setNextTransactionID( ) delegated to
 *                      TransactionUtilityManager for archiving process
 *    yiqzha 09/10/13 - Add journal info for the status change transaction:
 *                      cancel suspended transction.
 *    yiqzha 08/08/13 - Change setCustomer to linkCustomer to make the customer
 *                      associate to itemContainerPorxy and recalcuate item
 *                      price.
 *    jswan  06/21/13 - Modified to perform the status update of an Order in
 *                      the context of a transaction.
 *    vbongu 04/18/13 - added check to cancel suspended trans when it can be
 *                      resumed on mobile device
 *    rgour  02/19/13 - Changed the DataTransactionKey for Original
 *                      Transaction lookup
 *    sgu    02/19/13 - donot set blank customer id
 *    tkshar 12/10/12 - commons-lang update 3.1
 *    jswan  10/25/12 - Modified use method on SaleReturnLineItemIfc rather
 *                      than calcualtating the value in the site. OrderLineItem
 *                      has its own implementation of this method.
 *    blarse 08/29/12 - Merge from project Echo (MPOS) into Trunk.
 *    blarse 08/28/12 - Merge project Echo (MPOS) into trunk.
 *    cgreen 03/30/12 - get journalmanager from bus
 *    cgreen 03/16/12 - split transaction-methods out of utilitymanager
 *    cgreen 03/09/12 - add support for journalling queues by current register
 *    acadar 08/03/12 - moved customer search criteria
 *    acadar 06/26/12 - Cross Channel changes
 *    sgu    06/22/12 - refactor order id assignment
 *    sgu    06/20/12 - handle OMS offline during transaction retrieval
 *    sgu    06/20/12 - refactor get order id
 *    sgu    06/18/12 - refactor ORPOS to call order manager to get new order
 *                      id
 *    acadar 05/29/12 - changes for cross channel
 *    acadar 05/23/12 - CustomerManager refactoring
 *    vtemke 03/30/12 - Refactoring of getNumber() method of TenderCheck class
 *                      - returns sensitive data in byte[] instead of String
 *    asinto 03/15/12 - Retrieve customer from CustomerManager after retrieving
 *                      the transaction.
 *    jswan  01/05/12 - Refactor the status change of suspended transaction to
 *                      occur in a transaction so that status change can be
 *                      sent to CO as part of DTM.
 *    mjwall 01/31/12 - XbranchMerge mjwallac_forward_port_bug_13603967 from
 *                      rgbustores_13.4x_generic_branch
 *    mjwall 01/31/12 - incorporate review comments.
 *    mjwall 01/27/12 - Forward port: SQL Exception when trying to save a
 *                      resumed order transaction that had been linked to a
 *                      customer, but customer was deleted before resuming.
 *    sgu    09/08/11 - add house account as a refund tender
 *    mchell 08/12/11 - BUG#11854626 Customer Information not send to RM for
 *                      retrieved transactions
 *    cgreen 11/10/10 - cleanup
 *    asinto 09/01/10 - Fixed journaling format of tax modification when
 *                      transaction is retrieved.
 *    acadar 08/23/10 - show external order number in the EJ for retrieved
 *                      transaction
 *    cgreen 05/26/10 - convert to oracle packaging
 *    cgreen 04/28/10 - updating deprecated names
 *    jswan  02/25/10 - Fixed issue where the original transaction for a
 *                      retrieved return transaction has been voided.
 *    blarse 02/10/10 - Setting the retrieved and copied transaction post
 *                      processing flag to unprocesses so it will be processed
 *                      next time. This is a problem when the suspended
 *                      transaction is processed before it is retrived. The new
 *                      tranasction was copying the flag value from the
 *                      processed trans.
 *    blarse 02/09/10 - XbranchMerge shagoyal_bug-8418935 from
 *                      rgbustores_13.0x_branch
 *    abonda 01/03/10 - update header date
 *    vcheng 02/16/09 - Removed multiple occurrances of the string Link
 *                      Customer and retained only one for EJournalling
 *    mchell 01/08/09 - Merge checkin
 *    mchell 01/07/09 - Modified setNewTransaction to reset timestamps for
 *                      retrieved transactions
 *    deghos 01/07/09 - EJ i18n defect fixes
 *    deghos 12/02/08 - EJ i18n changes
 *    ranojh 11/04/08 - Code refreshed to tip
 *    acadar 11/03/08 - localization of transaction tax reason codes
 *    acadar 11/03/08 - localization of reason codes for discounts and merging
 *                      to tip
 *    acadar 11/02/08 - updated as per code review
 *
 * ===========================================================================
 * $Log:
 *  15   360Commerce 1.14        4/14/2008 6:22:03 PM   Deepti Sharma   CR
 *       30936: Changes for EJ and code clean up. Reviewed by Alan Sinton
 *  14   360Commerce 1.13        4/4/2008 10:45:10 AM   Christian Greene 31178
 *       setInitialTransactionBusinessDate(date) to the actual
 *       orderTransaction.getBusinessDay() instead of a new EYSDate instance
 *       so that data purge can find the orders properly.
 *  13   360Commerce 1.12        3/26/2008 11:49:37 PM  Vikram Gopinath
 *       Reverting changes.
 *  12   360Commerce 1.11        3/25/2008 5:50:49 AM   Vikram Gopinath CR
 *       #29903, ported changes from v12x.
 *  11   360Commerce 1.10        3/25/2008 3:58:34 AM   Vikram Gopinath CR
 *       #29903, ported changes from v12x
 *  10   360Commerce 1.9         8/22/2007 9:40:22 AM   Rohit Sachdeva  28173:
 *       Tax Exempt Tax Certificate EJ for Retrieved Suspended Transaction
 *  9    360Commerce 1.8         7/16/2007 4:53:42 PM   Alan N. Sinton  CR
 *       27643 Local file EJournal needs to have Sales Associate and Cashire
 *       linked.
 *  8    360Commerce 1.7         7/12/2007 10:55:19 AM  Anda D. Cadar
 *       replaced $ with Amt.
 *  7    360Commerce 1.6         7/10/2007 4:32:27 PM   Alan N. Sinton  CR
 *       27623 - Modified calls to SaleReturnTransaction.journalLineItems() to
 *        use the JournalFormatterManager instead.
 *  6    360Commerce 1.5         5/17/2007 2:49:36 PM   Owen D. Horne
 *       CR#23450 - Merged fix from v8.0.1
 *       *  6    .v8x       1.4.1.0     5/10/2007 11:38:31 AM  Sujay
 *       Purkayastha Fix
 *       *       for Cr 23,450
 *  5    360Commerce 1.4         1/25/2006 4:11:30 PM   Brett J. Larsen merge
 *       7.1.1 changes (aka. 7.0.3 fixes) into 360Commerce view
 *  4    360Commerce 1.3         1/22/2006 11:45:13 AM  Ron W. Haight   removed
 *        references to com.ibm.math.BigDecimal
 *  3    360Commerce 1.2         3/31/2005 4:28:59 PM   Robert Pearse
 *  2    360Commerce 1.1         3/10/2005 10:23:22 AM  Robert Pearse
 *  1    360Commerce 1.0         2/11/2005 12:12:29 PM  Robert Pearse
 * $
 * ===========================================================================
 */
package oracle.retail.stores.pos.services.modifytransaction.resume;

import java.math.BigDecimal;
import java.util.Locale;

import oracle.retail.stores.common.utility.LocaleMap;
import oracle.retail.stores.common.utility.LocaleRequestor;
import oracle.retail.stores.common.utility.Util;
import oracle.retail.stores.domain.DomainGateway;
import oracle.retail.stores.domain.arts.DataTransactionFactory;
import oracle.retail.stores.domain.arts.DataTransactionKeys;
import oracle.retail.stores.domain.arts.TransactionReadDataTransaction;
import oracle.retail.stores.domain.customer.CustomerIfc;
import oracle.retail.stores.domain.discount.TransactionDiscountStrategyIfc;
import oracle.retail.stores.domain.financial.RegisterIfc;
import oracle.retail.stores.domain.lineitem.AbstractTransactionLineItemIfc;
import oracle.retail.stores.domain.lineitem.PriceAdjustmentLineItemIfc;
import oracle.retail.stores.domain.lineitem.ReturnItemIfc;
import oracle.retail.stores.domain.lineitem.SaleReturnLineItemIfc;
import oracle.retail.stores.domain.manager.customer.CustomerManagerIfc;
import oracle.retail.stores.domain.manager.order.OrderManagerIfc;
import oracle.retail.stores.domain.order.OrderStatusIfc;
import oracle.retail.stores.domain.returns.ReturnTenderDataElementIfc;
import oracle.retail.stores.domain.stock.AlterationPLUItemIfc;
import oracle.retail.stores.domain.stock.GiftCardPLUItemIfc;
import oracle.retail.stores.domain.stock.PLUItemIfc;
import oracle.retail.stores.domain.tax.TaxIfc;
import oracle.retail.stores.domain.tender.TenderChargeIfc;
import oracle.retail.stores.domain.tender.TenderLineItemIfc;
import oracle.retail.stores.domain.transaction.LayawayTransactionIfc;
import oracle.retail.stores.domain.transaction.OrderTransaction;
import oracle.retail.stores.domain.transaction.OrderTransactionIfc;
import oracle.retail.stores.domain.transaction.RetailTransactionIfc;
import oracle.retail.stores.domain.transaction.SaleReturnTransactionIfc;
import oracle.retail.stores.domain.transaction.StatusChangeTransactionIfc;
import oracle.retail.stores.domain.transaction.TenderableTransactionIfc;
import oracle.retail.stores.domain.transaction.TransactionConstantsIfc;
import oracle.retail.stores.domain.transaction.TransactionIDIfc;
import oracle.retail.stores.domain.transaction.TransactionIfc;
import oracle.retail.stores.domain.transaction.TransactionSummaryIfc;
import oracle.retail.stores.domain.transaction.TransactionTaxIfc;
import oracle.retail.stores.domain.transaction.TransactionTotalsIfc;
import oracle.retail.stores.domain.utility.CustomerSearchCriteria;
import oracle.retail.stores.domain.utility.CustomerSearchCriteriaIfc;
import oracle.retail.stores.domain.utility.CustomerSearchCriteriaIfc.SearchType;
import oracle.retail.stores.domain.utility.EYSDate;
import oracle.retail.stores.domain.utility.GiftCardIfc;
import oracle.retail.stores.domain.utility.LocaleConstantsIfc;
import oracle.retail.stores.foundation.manager.data.DataException;
import oracle.retail.stores.foundation.manager.device.DeviceException;
import oracle.retail.stores.foundation.manager.ifc.JournalManagerIfc;
import oracle.retail.stores.foundation.manager.ifc.ParameterManagerIfc;
import oracle.retail.stores.foundation.manager.ifc.UIManagerIfc;
import oracle.retail.stores.foundation.manager.ifc.journal.JournalableIfc;
import oracle.retail.stores.foundation.tour.application.Letter;
import oracle.retail.stores.foundation.tour.gate.Gateway;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.pos.journal.JournalFormatterManagerIfc;
import oracle.retail.stores.pos.manager.archive.ArchiveException;
import oracle.retail.stores.pos.manager.ifc.TransactionUtilityManagerIfc;
import oracle.retail.stores.pos.manager.ifc.UtilityManagerIfc;
import oracle.retail.stores.pos.services.PosSiteActionAdapter;
import oracle.retail.stores.pos.services.alterations.AlterationsUtilities;
import oracle.retail.stores.pos.services.common.CommonLetterIfc;
import oracle.retail.stores.pos.ui.DialogScreensIfc;
import oracle.retail.stores.pos.ui.POSUIManagerIfc;
import oracle.retail.stores.pos.ui.beans.DialogBeanModel;
import oracle.retail.stores.pos.utility.GiftCardUtility;
import oracle.retail.stores.pos.utility.TransactionUtility;
import oracle.retail.stores.utility.I18NConstantsIfc;
import oracle.retail.stores.utility.I18NHelper;
import oracle.retail.stores.utility.JournalConstantsIfc;

import org.apache.commons.lang3.StringUtils;

import max.retail.stores.domain.arts.MAXTransactionReadDataTransaction;
/**
 * Retrieves list of suspended transactions.
 */
@SuppressWarnings("serial")
public class LookupTransactionSite extends PosSiteActionAdapter
{
    /**
     * site name constant
     */
    public static final String SITENAME = "LookupTransactionSite";

    /**
     * Retrieves list of suspended transactions.
     *
     * @param bus the bus arriving at this site
     */
    @Override
    public void arrive(BusIfc bus)
    {
        // True if link customer still exists when transaction is
        boolean linkedCustomerOk = true;
        // flag showing at least one item not eligible for tender
        boolean allItemsOk = true;
        // flag indicating if OMS is offline for xc orders
        boolean offline = false;

        String letterName = CommonLetterIfc.SUCCESS;
        // pull selected summary from cargo
        ModifyTransactionResumeCargo cargo =
            (ModifyTransactionResumeCargo) bus.getCargo();
        TransactionSummaryIfc selected = cargo.getSelectedSummary();

        // get utility manager
        UtilityManagerIfc utility =
            (UtilityManagerIfc) bus.getManager(UtilityManagerIfc.TYPE);

        // get order manager
        OrderManagerIfc orderMgr = (OrderManagerIfc)bus.getManager(OrderManagerIfc.TYPE);

        TransactionIfc searchTransaction =
            DomainGateway.getFactory().getTransactionInstance();
        searchTransaction.initialize(
            selected.getTransactionID().getTransactionIDString());
        if (searchTransaction.getBusinessDay() == null)
        {
            searchTransaction.setBusinessDay(selected.getBusinessDate());
        }
        //Set searchTrans trainingMode according to the current trainingMode
        // so suspended training
        //trans can only be retrieved in trainingMode and vice versa
        boolean trainingModeOn =
            cargo.getRegister().getWorkstation().isTrainingMode();
        searchTransaction.setTrainingMode(trainingModeOn);
        searchTransaction.setTransactionStatus(TransactionIfc.STATUS_SUSPENDED);
        searchTransaction.setLocaleRequestor(utility.getRequestLocales());

        RetailTransactionIfc retrieveTransaction = null;

        // Read the transaction from persistent storage
        try
        {
            // read transaction from database
            MAXTransactionReadDataTransaction readTransaction = null;

            readTransaction = (MAXTransactionReadDataTransaction) DataTransactionFactory.create(DataTransactionKeys.TRANSACTION_READ_DATA_TRANSACTION);

            retrieveTransaction =
                (RetailTransactionIfc) readTransaction.readTransaction(searchTransaction);
            CustomerManagerIfc customerManager = (CustomerManagerIfc)bus.getManager(CustomerManagerIfc.TYPE);
            getCustomerForTransaction(utility.getRequestLocales(), customerManager, retrieveTransaction, cargo);

            //If this is an order transaction and the linked customer
            //no longer exists,
            if (retrieveTransaction instanceof OrderTransaction &&
                !StringUtils.isBlank(retrieveTransaction.getCustomerId()) &&
                (retrieveTransaction.getCustomer() == null))
            {
                linkedCustomerOk = false;
            }

            // customer linked to suspended transaction still exists
            if(linkedCustomerOk == true)
            {
                // get original transaction for each returned lineitem
                getOriginalTransactions(retrieveTransaction, cargo, utility.getRequestLocales(), customerManager);
                
                // update the Original Transaction line items with the number of
                // items returned
                allItemsOk = setQtyReturned(retrieveTransaction, cargo, bus.getServiceName());
                if(allItemsOk)
                {
                    allItemsOk = isItemsOk(retrieveTransaction, cargo);
                }
                //Cancel the original suspended transaction if all Items are ok to resume, default value is true.  
                StringBuilder originalTransactionJournalString = new StringBuilder();
                if(cargo.isCancelSuspendedTransaction())
                {  
                    originalTransactionJournalString = getSuspendTransactionJournalString(retrieveTransaction);
                    cancelSuspendedTransaction(retrieveTransaction, cargo, originalTransactionJournalString, utility, bus);  
                }

                // if an item is ineligible for tendering
                if (allItemsOk == false)
                { // Begin at least one item can't be tendered
                    // display error message, original suspended transaction may
                    // have been modified
                    //therefore suspended transaction is not retrievable
                    displayIneligibleItemError(bus);

                } // End at least one item can't be tendered
                else
                { // Begin all items ok to be tendered
                    ParameterManagerIfc pmManager =
                        (ParameterManagerIfc) bus.getManager(
                            ParameterManagerIfc.TYPE);

                    // process Gift Card line items
                    processGiftCardLineItems(retrieveTransaction, cargo, pmManager, bus.getServiceName());
                    processPriceAdjustmentLineItems(retrieveTransaction, cargo);

                    // set attributes on newly retrieved transaction
                    try
                    {
                        setNewTransaction(bus, retrieveTransaction, cargo, utility, orderMgr);
                    }
                    catch (DataException | ArchiveException e)
                    {
                        //The OMS is offline. No new order id can be assigned.
                        offline = true;
                    }
    
                    if (!offline)
                    {
                        // if postprocessing occurs between suspend and retrieve, the status must be reset
                        retrieveTransaction.setPostProcessingStatus(TransactionConstantsIfc.POST_PROCESSING_STATUS_UNPROCESSED);
    
                        // set transaction in cargo
                        cargo.setTransaction(retrieveTransaction);
    
                        // journal retrieve transaction
                        journalRetrieveTransaction(retrieveTransaction, originalTransactionJournalString, cargo, bus.getServiceName(), bus);
    
                        // write hard totals
                        TransactionUtilityManagerIfc util = (TransactionUtilityManagerIfc) bus.getManager(TransactionUtilityManagerIfc.TYPE);
                        util.writeHardTotals();
                    }
                } // End all items ok to be tendered
            }

        }
        catch (DataException e)
        {

            if (e.getErrorCode() == DataException.NO_DATA)
            {
                letterName = CommonLetterIfc.NOT_FOUND;
            }
            else
            {
                cargo.setDataExceptionErrorCode(e.getErrorCode());
                letterName = CommonLetterIfc.DB_ERROR;
            }
        }
        catch (DeviceException e)
        {
            letterName = CommonLetterIfc.HARD_TOTALS_ERROR;
        }

        // mail a letter unless we are displaying an error message
        if (offline)
        {
            bus.mail(new Letter(CommonLetterIfc.OFFLINE), BusIfc.CURRENT);
        }
        else if (linkedCustomerOk == false)
        {
            // customer linked to suspended transaction was not found
            bus.mail(new Letter(CommonLetterIfc.LINKED_CUSTOMER_NOT_FOUND), BusIfc.CURRENT);
        }
        else if (allItemsOk == true)
        {
            bus.mail(new Letter(letterName), BusIfc.CURRENT);
        }
    } // end arrive()

    /**
     * Method for evaulating items if appropriate for retrieval
     * @param transaction the transaction to evaluate
     * @param cargo the ModifyTransactionResumeCargo instance
     * @return true if transaction's items are appropriate for retrieval, false otherwise.
     */
    protected boolean isItemsOk(RetailTransactionIfc retrieveTransaction, ModifyTransactionResumeCargo cargo)
    {
        // default implementation returns true
        return true;
    }

    /**
     * Fetches the customer by customer ID for the given transaction.
     * @param localeRequestor the locale requestor instance.
     * @param customerManager the <code>CustomerManager</code> instance.
     * @param transaction the transaction instance.
     * @param cargo the <code>ModifyTransasctionResumeCargo</code> instance.
     */
    protected void getCustomerForTransaction(LocaleRequestor localeRequestor, CustomerManagerIfc customerManager, TransactionIfc transaction, ModifyTransactionResumeCargo cargo)
    {
        if(transaction instanceof TenderableTransactionIfc && StringUtils.isNotBlank(((TenderableTransactionIfc)transaction).getCustomerId()))
        {
            TenderableTransactionIfc tenderableTransaction = (TenderableTransactionIfc)transaction;
            try
            {
                CustomerSearchCriteriaIfc criteria = new CustomerSearchCriteria(SearchType.SEARCH_BY_CUSTOMER_ID, tenderableTransaction.getCustomerId(), localeRequestor);
                Locale extendedDataRequestLocale = cargo.getOperator().getPreferredLocale();
                if(extendedDataRequestLocale == null)
                {
                    extendedDataRequestLocale = LocaleMap.getLocale(LocaleMap.DEFAULT);
                }
                criteria.setExtendedDataRequestLocale(extendedDataRequestLocale);
                int maxCustomerItemsPerListSize = Integer.parseInt(Gateway.getProperty(Gateway.APPLICATION_PROPERTIES_GROUP, "MaxCustomerItemsPerListSize", "10"));
                criteria.setMaxCustomerItemsPerListSize(maxCustomerItemsPerListSize);
                int maxTotalCustomerItemsSize = Integer.parseInt(Gateway.getProperty(Gateway.APPLICATION_PROPERTIES_GROUP, "MaxTotalCustomerItemsSize", "40"));
                criteria.setMaxTotalCustomerItemsSize(maxTotalCustomerItemsSize);
                int maxNumberCustomerGiftLists = Integer.parseInt(Gateway.getProperty(Gateway.APPLICATION_PROPERTIES_GROUP, "MaxNumberCustomerGiftLists", "4"));
                criteria.setMaxNumberCustomerGiftLists(maxNumberCustomerGiftLists);

                //search for customer records
                CustomerIfc customer = customerManager.getCustomer(criteria);
                tenderableTransaction.linkCustomer(customer);
            }
            catch (DataException de)
            {
                logger.warn("Could not retrieve customer: " + tenderableTransaction.getCustomerId(), de);
            }
        }
    }

    /**
     * Cancel the suspended transaction.
     *
     * @param retrieveTransaction retrieved transaction
     * @param cargo cargo class
     * @param journal JournalManagerIfc reference
     * @param journalString retrieved transaction journal fragment
     * @exception DataException thrown if error occurs writing canceled
     *                transaction to database
     */
    public void cancelSuspendedTransaction(RetailTransactionIfc retrieveTransaction,
            ModifyTransactionResumeCargo cargo, StringBuilder originalTransactionJournalString, UtilityManagerIfc utility, BusIfc bus)
            throws DataException
    {
        // update financials
        RegisterIfc register = cargo.getRegister();
        register.addNumberCancelledTransactions(1);
        TransactionTotalsIfc totals = retrieveTransaction.getTransactionTotals();
        register.addAmountCancelledTransactions(
            totals.getSubtotal().subtract(totals.getDiscountTotal()).abs());

        // set search transaction status to canceled and update
        StatusChangeTransactionIfc transaction = DomainGateway.getFactory().
            getStatusChangeTransactionInstance();
        TransactionUtilityManagerIfc transactionUtility = (TransactionUtilityManagerIfc)bus.getManager(TransactionUtilityManagerIfc.TYPE);
        transactionUtility.initializeTransaction(transaction, -1, null);
        TransactionSummaryIfc summary = DomainGateway.getFactory().
            getTransactionSummaryInstance();
        summary.setTransactionStatus(TransactionIfc.STATUS_SUSPENDED_RETRIEVED);
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
        
        // note: there is no journal of cancellation of suspended
        // transaction, because we are journaling the retrieval
        // of the transaction right here.
        // build journal string
        // get journal reference
        JournalManagerIfc journal = (JournalManagerIfc)bus.getManager(JournalManagerIfc.TYPE);

        StringBuilder transactionType = new StringBuilder();
        transactionType
        .append(Util.EOL)
        .append(I18NHelper.getString(I18NConstantsIfc.EJOURNAL_TYPE, JournalConstantsIfc.TRANS_CANCEL_SUSPENDED_LABEL, null))
        .append(Util.EOL);
        
        journal.journal(
                cargo.getOperator().getLoginID(),
                retrieveTransaction.getTransactionID(),
                transactionType.append(originalTransactionJournalString).toString());
        // Totals (Store/Register/Till) updates will be handled by the retrieved transaction.
        transactionUtility.saveTransaction(transaction);

    } // end cancelSuspendedTransaction()
    
    
    protected StringBuilder getSuspendTransactionJournalString(RetailTransactionIfc retrieveTransaction)
    {
        StringBuilder journalString = new StringBuilder();
        
        Object[] dataArgs = new Object[1];
        dataArgs[0] = retrieveTransaction.getTransactionID();
        journalString.append(I18NHelper.getString(I18NConstantsIfc.EJOURNAL_TYPE, JournalConstantsIfc.ORIGINAL_TRANS_LABEL, dataArgs))
            .append(Util.EOL);
        dataArgs[0] = retrieveTransaction.getTillID();
        journalString.append(I18NHelper.getString(I18NConstantsIfc.EJOURNAL_TYPE, JournalConstantsIfc.ORIGINAL_TILL_LABEL, dataArgs))
            .append(Util.EOL);
        dataArgs[0] = retrieveTransaction.getCashier().getEmployeeID();
        journalString.append(I18NHelper.getString(I18NConstantsIfc.EJOURNAL_TYPE, JournalConstantsIfc.ORIGINAL_CASHIER_LABEL, dataArgs))
            .append(Util.EOL);
        dataArgs[0] = retrieveTransaction.getSalesAssociate().getEmployeeID();
        journalString.append(I18NHelper.getString(I18NConstantsIfc.EJOURNAL_TYPE, JournalConstantsIfc.ORIGINAL_SALES_LABEL, dataArgs));
        
        return journalString;
    }

    /**
     * Sets attributes on newly retrieved transaction based on cargo.
     *
     * @param transaction newly retrieved transaction
     * @param cargo Cargo class
     * @param journal JournalManagerIfc reference
     * @param utility UtilityManagerIfc reference
     * @throws ArchiveException 
     */
    protected void setNewTransaction(BusIfc bus, RetailTransactionIfc transaction, ModifyTransactionResumeCargo cargo,
            UtilityManagerIfc utility, OrderManagerIfc orderMgr) throws DataException, ArchiveException
    {
        TransactionUtilityManagerIfc utilMgr = (TransactionUtilityManagerIfc)bus.getManager(TransactionUtilityManagerIfc.TYPE);
        
        
        transaction.setTrainingMode(cargo.getRegister().getWorkstation().isTrainingMode());

        transaction.setTransactionStatus(TransactionIfc.STATUS_IN_PROGRESS);
        transaction.setTrainingMode(cargo.getRegister().getWorkstation().isTrainingMode());
        
        transaction.setTillID(cargo.getRegister().getCurrentTillID());
        transaction.setCashier(cargo.getOperator());
        
        // set the TenderLimits object for transaction level checking
        transaction.setTenderLimits(cargo.getTenderLimits());
        
        //set next transaction is delegated to TransactionUtilityManager          
        utilMgr.setNextTransactionID(transaction, cargo.getRegister(), cargo.getStoreStatus().getBusinessDate());
        
        // reset the timestamps, otherwise the new transaction will use the old transactions timestamps
        transaction.setTimestampBegin();
        transaction.setTimestampEnd(null);

        switch (transaction.getTransactionType())
        {
            case TransactionIfc.TYPE_LAYAWAY_INITIATE :
                LayawayTransactionIfc layawayTransaction =
                    (LayawayTransactionIfc) transaction;
                // set ID of initial transaction on layaway
                layawayTransaction.getLayaway().setInitialTransactionID(
                    transaction.getTransactionIdentifier());
                // set layaway ID to original value from suspended layaway
                layawayTransaction.getLayaway().setLayawayID(
                    ((LayawayTransactionIfc) transaction)
                        .getLayaway()
                        .getLayawayID());
                break;
            case TransactionIfc.TYPE_ORDER_INITIATE :

                // set order ID to next unique id due to db issues
                OrderTransactionIfc orderTransaction =(OrderTransactionIfc) transaction;
                orderTransaction.setUniqueID(cargo.getRegister().getCurrentUniqueID());

                // update the order status object for the order transaction
                OrderStatusIfc orderStatus = orderTransaction.getOrderStatus();
                orderStatus.setTimestampBegin();
                orderStatus.setTimestampCreated();
                orderStatus.setInitialTransactionBusinessDate(
                    orderTransaction.getBusinessDay());
                orderStatus.setInitialTransactionID(
                    orderTransaction.getTransactionIdentifier());
                orderStatus.setRecordingTransactionID(
                    orderTransaction.getTransactionIdentifier());
                orderStatus.setRecordingTransactionBusinessDate(
                    orderTransaction.getBusinessDay());
                break;
            default :
                break;
        }

        JournalManagerIfc journal = (JournalManagerIfc)bus.getManager(JournalManagerIfc.TYPE);
        String sequenceNo = journal.getSequenceNumber();
        if (sequenceNo != null && sequenceNo != "")
        {
            TransactionUtilityManagerIfc util = (TransactionUtilityManagerIfc) bus.getManager(TransactionUtilityManagerIfc.TYPE);
            util.indexTransactionInJournal(sequenceNo);
        }

        // set the sequence number in the journal
        journal.setSequenceNumber(transaction.getTransactionID());
        journal.setCashierID(cargo.getOperator().getLoginID());
        journal.setSalesAssociateID(cargo.getSalesAssociate().getLoginID());

    } // end setNewTransaction()

    /**
     * Reads original transaction for each returned lineitem
     *
     * @param transaction newly retrieved transaction
     * @param cargo Cargo class
     * @param localeReq
     * @param customerManager
     */
    protected void getOriginalTransactions(RetailTransactionIfc transaction, ModifyTransactionResumeCargo cargo,
            LocaleRequestor localeReq, CustomerManagerIfc customerManager) throws DataException
    {
        AbstractTransactionLineItemIfc[] lineitems;
        // lineitems on retrieved transaction
        SaleReturnLineItemIfc item; // lineitem being processed
        ReturnItemIfc returnItem; // return item associated with lineitem
        TransactionIfc originalTransaction = null;
        // original trans associated with a returned item

        boolean transAlreadyRead;
        // indicates if an original trans has already been found

        int i; // counter through array of lineitems

        // get lineitems on retrieved transaction
        lineitems = transaction.getLineItems();

        // if there are any lineitems on the retrieved transaction
        if (lineitems != null)
        { // Begin transaction contains lineitems

            // for each lineitem, if the lineitem is a return then get the
            // original transaction
            for (i = 0; i < lineitems.length; i++)
            { // Begin get original transactions

                // if the lineitem is a SaleReturnLineItem
                if (SaleReturnLineItemIfc.class.isInstance(lineitems[i]))
                { // Begin lineitem is a SaleReturnLineItem

                    // get a specific lineitem
                    item = (SaleReturnLineItemIfc) lineitems[i];

                    // get the return item
                    returnItem = item.getReturnItem();

                    // if the return item exists (and was not a no-receipt
                    // entry)
                    if (returnItem != null
                        && returnItem.getOriginalLineNumber() != -1)
                    { // Begin lineitem is a return

                        transAlreadyRead =
                            cargo.isTransactionInList(
                                returnItem.getOriginalTransactionID(),
                                returnItem
                                    .getOriginalTransactionBusinessDate());
                        // if the transaction hasn't already been looked up
                        if (transAlreadyRead == false)
                        { // Begin transaction not yet in list of original
                          // trans

                            originalTransaction =
                                getOriginalTrans(
                                    returnItem.getOriginalTransactionID(),
                                    returnItem
                                        .getOriginalTransactionBusinessDate(),
                                    transaction.isTrainingMode(),
                                    localeReq,
                                    customerManager,
                                    cargo);

                            // if the transaction is a
                            // SaleReturnTransactionIfc, as it should be.
                            // if not, no big problem, the returned items won't
                            // get updated but
                            // since it's not a srt there aren't returned items
                            // anyway
                            if (SaleReturnTransactionIfc
                                .class
                                .isInstance(originalTransaction))
                            { // Begin add transaction to the list

                                cargo.addOrignalReturnTransaction(
                                    (SaleReturnTransactionIfc) originalTransaction);

                            } // End add transaction to the list

                        } // End transaction not yet in list of original trans

                    } // End lineitem is a return

                } // End lineitem is a SaleReturnLineItem

            } // End get original transactions

        } // End transaction contains lineitems

    } // end getOriginalTransactions()

    /**
     * Get original transaction associated with a returned item.
     * <P>
     * If transaction can't be found, it is assumed the return was entered
     * without a receipt.
     *
     * @param transID original transaction's ID
     * @param businessDay original transaction business day
     * @param trainingModeOn the training mode qualifier value to retrieve the originalTransaction
     * @param localeReq
     * @param customerManager
     * @param cargo instance of the <code>ModifyTransactionResumeCargo</code>.
     */
    protected TransactionIfc getOriginalTrans(TransactionIDIfc transID, EYSDate businessDay, boolean trainingModeOn,
            LocaleRequestor localeReq, CustomerManagerIfc customerManager, ModifyTransactionResumeCargo cargo) throws DataException
    {
        // returned original transaction
        // return null if error occurs
        TransactionIfc originalTransaction = null;
        // instantiate a transaction with search parameters
        TransactionIfc searchTransaction =
            DomainGateway.getFactory().getTransactionInstance();
        searchTransaction.initialize(transID);
        searchTransaction.setBusinessDay(businessDay);
        searchTransaction.setTrainingMode(trainingModeOn);
        searchTransaction.setLocaleRequestor(localeReq);

        // Read the transaction from persistent storage
        TransactionReadDataTransaction readTransaction = null;

        readTransaction = (TransactionReadDataTransaction) DataTransactionFactory.create(DataTransactionKeys.READ_TRANSACTIONS_FOR_RETURN);

        originalTransaction =
            readTransaction.readTransaction(searchTransaction);

        getCustomerForTransaction(localeReq, customerManager, originalTransaction, cargo);

        return originalTransaction; // return the original transaction
    } // end getOriginalTrans()

    /**
     * For each Gift Card line item, restore the expiration date and the date
     * sold.
     *
     * @param transaction newly retrieved transaction
     * @param cargo ModifyTransactionResumeCargo class
     * @param serviceName service name used in log
     */
    protected void processGiftCardLineItems(RetailTransactionIfc transaction, ModifyTransactionResumeCargo cargo,
            ParameterManagerIfc pmManager, String serviceName)
    {
        AbstractTransactionLineItemIfc[] lineitems;
        // lineitems on retrieved transaction

        // get lineitems on retrieved transaction
        lineitems = transaction.getLineItems();

        // if there are any lineitems on the retrieved transaction
        if (lineitems != null)
        { // Begin transaction contains lineitems
            for (int i = 0; i < lineitems.length; i++)
            {
                SaleReturnLineItemIfc srli = null;
                PLUItemIfc pluItem = null;
                GiftCardIfc giftCard = null;

                srli = (SaleReturnLineItemIfc) lineitems[i];
                pluItem = srli.getPLUItem();
                if ((pluItem != null))
                {
                    if (pluItem instanceof GiftCardPLUItemIfc)
                    {
                        giftCard = ((GiftCardPLUItemIfc) pluItem).getGiftCard();

                        // set date sold for the gift card
                        EYSDate eysDate =
                            DomainGateway.getFactory().getEYSDateInstance();
                        giftCard.setDateSold(eysDate);

                        // set the expiration date
                        if (giftCard.getExpirationDate() == null)
                        {
                            EYSDate expirationDate =
                                GiftCardUtility.computeExpirationDate(
                                    eysDate,
                                    pmManager,
                                    serviceName);
                            giftCard.setExpirationDate(expirationDate);
                        }
                    }
                }
            }
        } // End transaction contains lineitems
    }

    /**
     * For each item, set the quantity returned in the original transaction
     * object. This is necessary to enable the original lineitem's return
     * quantity to be updated when the retrieved transaction is tendered.
     *
     * Also, check if there are any items on the retrieved transaction that
     * would make it inelgible to tender.
     *
     * At this point, the only type of item that would render the transaction
     * ineligible would be a return item that has already been returned. This
     * would occur if a return is suspended, then on another transaction the
     * item is returned. The suspended transaction, when retrieved, should not
     * be permitted to be completed.
     *
     * This method could be broken up into two - one to set the quantities and
     * one to check for ineligible items. These two functions were coupled due
     * to the complexity and error-prone code required to find an item in the
     * original transaction. Since that must be done for both functions, it was
     * decided to combine both into this one method.
     *
     *
     * @param transaction
     *            newly retrieved transaction
     * @param cargo
     *            ModifyTransactionResumeCargo class
     * @param serviceName
     *            service name used in log
     * @return boolean true if all items are eligible, false otherwise
     */
    protected boolean setQtyReturned(
        RetailTransactionIfc transaction,
        ModifyTransactionResumeCargo cargo,
        String serviceName)
    {
        // loop through items on retrieved transaction
        // for each SaleReturnLineItem that is a return, add to the original
        // item's qtyRtrned

        AbstractTransactionLineItemIfc[] lineitems;
        // lineitems on retrieved transaction
        SaleReturnLineItemIfc item; // lineitem being processed
        ReturnItemIfc returnItem; // return item associated with lineitem
        SaleReturnTransactionIfc originalTransaction;
        // original trans associated with a returned item
        SaleReturnLineItemIfc originalItem; // lineitem in original transaction
        BigDecimal oldQuantity; // qty returned in original transaction line
        BigDecimal addQuantity; // amount returned in the retrieved transaction
        BigDecimal numberReturnable; // quantity returnable on the lineitem

        int i; // counter through array of lineitems

        boolean allItemsOk = true;
        // flag cleared if a return item has insufficient qty

        // get lineitems on retrieved transaction
        lineitems = transaction.getLineItems();

        // if there are any lineitems on the retrieved transaction
        if (lineitems != null)
        { // Begin transaction contains lineitems

            // for each lineitem, if the lineitem is a return then update the
            // original transaction with the returned amount
            for (i = 0; i < lineitems.length; i++)
            { // Begin get original transactions

                // if the lineitem is a SaleReturnLineItem
                if (SaleReturnLineItemIfc.class.isInstance(lineitems[i]))
                { // Begin lineitem is a SaleReturnLineItem
                    // get a specific lineitem
                    item = (SaleReturnLineItemIfc) lineitems[i];
                    // get the associated return item
                    returnItem = item.getReturnItem();

                    // if the return item exists (and was not a no-receipt
                    // entry)
                    if (returnItem != null
                        && returnItem.getOriginalLineNumber() != -1)
                    { // Begin lineitem is a return

                        // get the original transaction based on the original
                        // transaction ID
                        // and original business date found in the returnItem
                        // object
                        originalTransaction =
                            findOrigTransByID(
                                cargo,
                                returnItem.getOriginalTransactionID(),
                                returnItem
                                    .getOriginalTransactionBusinessDate());

                        if (originalTransaction.getTransactionStatus() ==
                            TransactionConstantsIfc.STATUS_VOIDED)
                        {
                            allItemsOk = false;
                            // not a valid transaction anymore
                            i = lineitems.length; // exit the loop

                            logger.warn("The original transaction for a return " +
                                    "item has been voided; the retrieved return " +
                                    "transaction is no longer valid.");
                        }
                        else
                        {
                            // get the original item from the original transaction
                            // using the
                            // "original line number" found in the returnItem
                            // object
                            originalItem =
                                (SaleReturnLineItemIfc)
                                    (originalTransaction)
                                    .getLineItems()[returnItem
                                    .getOriginalLineNumber()];
                            // calculate qty still available to return
                            numberReturnable =
                                originalItem.getQuantityReturnable();

                            // compare number returnable to qty on the retrieved
                            // transaction
                            if (numberReturnable
                                .compareTo(item.getItemQuantityDecimal().abs())
                                < 0)
                            { // Begin no quantity available to return
                                allItemsOk = false;
                                // not a valid transaction anymore
                                i = lineitems.length; // exit the loop

                                logger.warn(
                                    "LookupTransactionSite: setQtyReturned(): NOT ok to return\nnumberReturnable = "
                                        + numberReturnable
                                        + "\nnumber to return = "
                                        + item.getItemQuantityDecimal().abs()
                                        + "");

                            } // End no quantity available to return
                            else
                            { // Begin there is enough qty for a valid return

                                // add the quantity of the retrieved lineitem
                                oldQuantity =
                                    originalItem.getQuantityReturnedDecimal();
                                addQuantity = item.getItemQuantityDecimal().abs();
                                originalItem.setQuantityReturned(
                                    oldQuantity.add(addQuantity));

                            } // End there is enough qty for a valid return
                        } // end the orignial transaction has not been voided
                    } // End lineitem is a return
                } // End lineitem is a SaleReturnLineItem
            } // End get original transactions
        } // End transaction contains lineitems

        return allItemsOk; // return the status code
    } // end setQtyReturned()

    /**
     * Find transaction in the list.
     *
     * @param cargo ModifyTransactionResumeCargo class
     * @param transID original transaction's ID
     * @param businessDay original transaction business day
     */
    protected SaleReturnTransactionIfc findOrigTransByID(ModifyTransactionResumeCargo cargo,
            TransactionIDIfc transID, EYSDate businessDay)
    {
        SaleReturnTransactionIfc srt; // transaction in the list
        SaleReturnTransactionIfc[] list; // list of original transactions
        SaleReturnTransactionIfc originalTransaction;
        // returned original transaction
        int i; // index into list of original transactions

        originalTransaction = null; // initialize the original transaction

        list = cargo.getOriginalReturnTransactions();
        // get the list of original transactions

        // if the list has been instantiated
        if (list != null)
        { // Begin transaction list exists
            for (i = 0; i < list.length; i++)
            { // Begin search for a matching transaction
                srt = list[i]; // get the current SaleReturnTransaction

                if (Util.isObjectEqual(transID, srt.getTransactionIdentifier())
                    && Util.isObjectEqual(businessDay, srt.getBusinessDay()))
                { // Begin found a match
                    originalTransaction = srt; // set the returned transaction
                    i = list.length; // don't need to search anymore
                } // End found a match
            } // End search for a matching transaction
        } // End transaction list exists

        return originalTransaction; // return the original transaction
    } // end findOrigTransByID

    /**
     * Display an error message showing that at least one item is ineligible
     * for tender. Original suspended transaction may have been modified
     *
     * @param bus the bus arriving at this site
     */
    protected void displayIneligibleItemError(BusIfc bus)
    {
        // get cargo
        ModifyTransactionResumeCargo cargo =
            (ModifyTransactionResumeCargo) bus.getCargo();
        // Get the ui manager
        POSUIManagerIfc ui =
            (POSUIManagerIfc) bus.getManager(UIManagerIfc.TYPE);
        // Use the "generic dialog bean".
        DialogBeanModel model = new DialogBeanModel();
        model.setResourceID("InvalidModifiedTransaction");
        if (!(cargo.isVisitedSuspendedListSite()))
        {
            model.setButtonLetter(0, CommonLetterIfc.FAILURE);
        }
        model.setType(DialogScreensIfc.ERROR);

        // set and display the model
        ui.showScreen(POSUIManagerIfc.DIALOG_TEMPLATE, model);
    }

    /**
     * Journals newly retrieved transaction.
     *
     * @param transaction newly retrieved transaction
     * @param cargo ModifyTransactionResumeCargo class
     * @param journal JournalManagerIfc reference
     * @param journalString journal fragment based on original transaction
     * @param serviceName service name used in log
     */
    protected void journalRetrieveTransaction(RetailTransactionIfc retrieveTransaction, StringBuilder suspendedTransactionJournalString, ModifyTransactionResumeCargo cargo,
                                              String serviceName, BusIfc bus)
    {
        JournalFormatterManagerIfc formatter =
            (JournalFormatterManagerIfc)Gateway.getDispatcher().getManager(JournalFormatterManagerIfc.TYPE);
        // cast transaction to get details
        SaleReturnTransactionIfc srt = (SaleReturnTransactionIfc) retrieveTransaction;

        JournalManagerIfc journal = (JournalManagerIfc)bus.getManager(JournalManagerIfc.TYPE);

        //StringBuilder journalString = new StringBuilder();
        
        /*journalString
        .append(Util.EOL)
        .append(I18NHelper.getString(I18NConstantsIfc.EJOURNAL_TYPE, JournalConstantsIfc.TRANS_RETRIEVED_LABEL, null))
        .append(Util.EOL);
        
        Object[] dataArgs = new Object[2];
        dataArgs[0] = retrieveTransaction.getTransactionID();
        journalString.append(I18NHelper.getString(I18NConstantsIfc.EJOURNAL_TYPE, JournalConstantsIfc.ORIGINAL_TRANS_LABEL, dataArgs))
            .append(Util.EOL);
        dataArgs[0] = retrieveTransaction.getTillID();
        journalString.append(I18NHelper.getString(I18NConstantsIfc.EJOURNAL_TYPE, JournalConstantsIfc.ORIGINAL_TILL_LABEL, dataArgs))
            .append(Util.EOL);
        dataArgs[0] = retrieveTransaction.getCashier().getEmployeeID();
        journalString.append(I18NHelper.getString(I18NConstantsIfc.EJOURNAL_TYPE, JournalConstantsIfc.ORIGINAL_CASHIER_LABEL, dataArgs))
            .append(Util.EOL);
        dataArgs[0] = retrieveTransaction.getSalesAssociate().getEmployeeID();
        journalString.append(I18NHelper.getString(I18NConstantsIfc.EJOURNAL_TYPE, JournalConstantsIfc.ORIGINAL_SALES_LABEL, dataArgs));
   */
        journal.setEntryType(JournalableIfc.ENTRY_TYPE_START);
        journal.journal(
                cargo.getOperator().getLoginID(),
                retrieveTransaction.getTransactionID(),
                srt.journalHeader(LocaleMap.getLocale(LocaleConstantsIfc.JOURNAL)));
        journal.setEntryType(JournalableIfc.ENTRY_TYPE_TRANS);
        
        StringBuilder transactionType = new StringBuilder();
        transactionType
        .append(Util.EOL)
        .append(I18NHelper.getString(I18NConstantsIfc.EJOURNAL_TYPE, JournalConstantsIfc.TRANS_RESUMED_LABEL, null))
        .append(Util.EOL);
        
        journal.journal(
                cargo.getOperator().getLoginID(),
                retrieveTransaction.getTransactionID(),
                transactionType.append(suspendedTransactionJournalString).toString());
                
        //journal the order number
        if (retrieveTransaction instanceof SaleReturnTransactionIfc && ((SaleReturnTransactionIfc)retrieveTransaction).hasExternalOrder())
        {
            TransactionUtility.journalExternalOrder(journal, ((SaleReturnTransactionIfc)retrieveTransaction).getExternalOrderNumber());
        }

        journal.journal(
                cargo.getOperator().getLoginID(),
                retrieveTransaction.getTransactionID(),
                journalTransactionModifiers(retrieveTransaction, cargo));

        journal.journal(
                cargo.getOperator().getLoginID(),
                retrieveTransaction.getTransactionID(),
                formatter.journalLineItems(srt));

    }

    /**
     * Creates the journal string for transaction modifications: linked
     * customer, discounts, non-standard tax modifications, and gift registry.
     *
     * @return string to journal
     */
    protected String journalTransactionModifiers(
        RetailTransactionIfc transaction,
        ModifyTransactionResumeCargo cargo)
    {
        StringBuilder strResult = new StringBuilder();
        if (transaction.getCustomer() != null)
        {
            strResult.append(Util.EOL);
            Object dataArgs[] = {transaction.getCustomer().getCustomerID().trim()};
            String customer = I18NHelper.getString(I18NConstantsIfc.EJOURNAL_TYPE, JournalConstantsIfc.LINK_CUSTOMER_LABEL, dataArgs);

            strResult.append(customer);

            // journal alteration items
            if (transaction instanceof SaleReturnTransactionIfc)
            {
                SaleReturnTransactionIfc srt =
                    (SaleReturnTransactionIfc) transaction;
                if (srt.hasAlterationItems())
                {
                    strResult.append(Util.EOL);
                    AbstractTransactionLineItemIfc lineItem[] =
                        transaction.getLineItems();
                    for (int j = 0; j < lineItem.length; j++)
                    {
                        if (lineItem[j] instanceof SaleReturnLineItemIfc)
                        {
                            SaleReturnLineItemIfc srli =
                                (SaleReturnLineItemIfc) lineItem[j];
                            if (srli.isAlterationItem())
                            {
                                AlterationPLUItemIfc alterationItem =
                                    (AlterationPLUItemIfc) srli.getPLUItem();
                                strResult.append(
                                    AlterationsUtilities.journalAlterationItem(
                                        alterationItem));
                            }
                        }
                    }
                }
            }

        }

        // journal discounts
        TransactionDiscountStrategyIfc[] discounts =
            transaction.getTransactionDiscounts();
        if (discounts != null && discounts.length > 0)
        {
            for (int i = 0; i < discounts.length; i++)
            {
                strResult.append(discounts[i].toJournalString(LocaleMap.getLocale(LocaleConstantsIfc.JOURNAL)));
            }
        }

        // journal tax modifications
        TransactionTaxIfc tax = transaction.getTransactionTax();
        if (tax.getTaxMode() != TaxIfc.TAX_MODE_STANDARD)
        {
            Object dataArgs[] = {""};
            String reasonCode = I18NHelper.getString(I18NConstantsIfc.EJOURNAL_TYPE, JournalConstantsIfc.REASON_CODE_LABEL, dataArgs);

            StringBuilder message = new StringBuilder();
            String reasonText = "";
            switch (tax.getTaxMode())
            {
                case TaxIfc.TAX_MODE_EXEMPT :
                    {
                        String customer = "";
                        if (transaction.getCustomer() != null)
                        {

                            Object customerDataArgs[] = {transaction.getCustomer().getCustomerID()};
                            customer = I18NHelper.getString(I18NConstantsIfc.EJOURNAL_TYPE, JournalConstantsIfc.CUSTOMER_ID, customerDataArgs);

                        }
                        // To be Modified
                        reasonText = tax.getReason().getText(LocaleMap.getLocale(LocaleConstantsIfc.JOURNAL));

                        // Need to be modified

                        Object taxDataArgs[] = {tax.getTaxExemptCertificateID()};
                        String taxCertificate = I18NHelper.getString(I18NConstantsIfc.EJOURNAL_TYPE, JournalConstantsIfc.TRANSACTION_TAX_CERTIFICATE_LABEL, taxDataArgs);


                        message.append("\n")
                            .append(I18NHelper.getString(I18NConstantsIfc.EJOURNAL_TYPE, JournalConstantsIfc.TRANSACTION_TAX_EXEMPT, null))
                            .append(customer)
                            .append("\n")
                            .append(taxCertificate)
                            .append("\n")
                            .append(reasonCode)
                            .append(reasonText);

                        break;
                    }
                case TaxIfc.TAX_MODE_OVERRIDE_AMOUNT :
                    {

                        reasonText = tax.getReason().getText(LocaleMap.getLocale(LocaleConstantsIfc.JOURNAL));
                        message.append("\n")
                            .append(I18NHelper.getString(I18NConstantsIfc.EJOURNAL_TYPE, JournalConstantsIfc.TRANSACTION_TAX_OVERRIDE, null))
                            .append("\n")
                            .append(I18NHelper.getString(I18NConstantsIfc.EJOURNAL_TYPE, JournalConstantsIfc.TRANSACTION_TAX_OVERRIDE_AMT, null))
                            .append(tax.getOverrideAmount())
                            .append("\n")
                            .append(reasonCode)
                            .append(reasonText);

                        break;
                    }
                case TaxIfc.TAX_MODE_OVERRIDE_RATE :
                    {

                        reasonText = tax.getReason().getText(LocaleMap.getLocale(LocaleConstantsIfc.JOURNAL));

                        message.append("\n")
                            .append(I18NHelper.getString(I18NConstantsIfc.EJOURNAL_TYPE, JournalConstantsIfc.TRANSACTION_TAX_OVERRIDE, null))
                            .append("\n")
                            .append(I18NHelper.getString(I18NConstantsIfc.EJOURNAL_TYPE, JournalConstantsIfc.TRANSACTION_TAX_OVERRIDE_PERC, null))
                            .append(String.valueOf(tax.getOverrideRate() * 100))
                            .append("\n")
                            .append(reasonCode)
                            .append(reasonText);

                        break;
                    }
            } // switch tax mode

            strResult.append(message.toString());
        } // standard

        // journal gift registry
        if (transaction instanceof SaleReturnTransactionIfc
            && ((SaleReturnTransactionIfc) transaction).getDefaultRegistry()
                != null)
        {
            strResult
                .append(Util.EOL)
                .append(I18NHelper.getString(I18NConstantsIfc.EJOURNAL_TYPE, JournalConstantsIfc.TRANSACTION_GIFT_REG, null))
                .append(Util.EOL)
                .append(I18NHelper.getString(I18NConstantsIfc.EJOURNAL_TYPE, JournalConstantsIfc.GIFT_REG, null))
                .append(((SaleReturnTransactionIfc) transaction).getDefaultRegistry().getID());
        }

        return (strResult.toString());
    } // journalTransactionModifiers

    /**
     * For Price Adjustment line items
     *
     * @param transaction
     *            newly retrieved transaction
     */
    protected void processPriceAdjustmentLineItems(RetailTransactionIfc transaction, ModifyTransactionResumeCargo cargo)
    {
        AbstractTransactionLineItemIfc[] lineitems;
        // lineitems on retrieved transaction
        SaleReturnLineItemIfc item; // lineitem being processed
        ReturnItemIfc returnItem; // return item associated with lineitem
        SaleReturnTransactionIfc originalTransaction;
        // original trans associated with a returned item
        SaleReturnLineItemIfc originalLineItem; // lineitem in original transaction
        int i; // counter through array of lineitems
        // get lineitems on retrieved transaction
        lineitems = transaction.getLineItems();
        // if there are any lineitems on the retrieved transaction
        if (lineitems != null)
        { // Begin transaction contains lineitems
            // for each lineitem, if the lineitem is a return then update the
            // original transaction with the returned amount
            for (i = 0; i < lineitems.length; i++)
            { // Begin get original transactions
                // if the lineitem is a SaleReturnLineItem
                if (SaleReturnLineItemIfc.class.isInstance(lineitems[i]))
                { // Begin lineitem is a SaleReturnLineItem
                    // get a specific lineitem
                    item = (SaleReturnLineItemIfc) lineitems[i];
                    // get the associated return item
                    returnItem = item.getReturnItem();
                    // if the return item exists (and was not a no-receipt
                    // entry)
                    if (returnItem != null
                        && returnItem.getOriginalLineNumber() != -1)
                    { // Begin lineitem is a return
                        // get the original transaction based on the original
                        // transaction ID
                        // and original business date found in the returnItem
                        // object
                        originalTransaction =
                            findOrigTransByID(
                                cargo,
                                returnItem.getOriginalTransactionID(),
                                returnItem
                                    .getOriginalTransactionBusinessDate());
                        // get the original item from the original transaction
                        // using the
                        // "original line number" found in the returnItem
                        // object
                        originalLineItem =
                            (SaleReturnLineItemIfc)
                                (originalTransaction)
                                .getLineItems()[returnItem
                                .getOriginalLineNumber()];
                        //Check item for PriceAdjustment
                        if (SaleReturnTransactionIfc.class.isInstance(transaction) && item.isPartOfPriceAdjustment())
                        {
                            SaleReturnTransactionIfc currentTransaction = (SaleReturnTransactionIfc)transaction;
                            SaleReturnLineItemIfc currentLineItem = (SaleReturnLineItemIfc)(transaction).getLineItems()[returnItem.getOriginalLineNumber()];
                            // Save the original line and transaction numbers for use
                            // when updating the
                            // original line item
                            currentLineItem.setOriginalLineNumber(originalLineItem.getLineNumber());
                            currentLineItem.setOriginalTransactionSequenceNumber(originalTransaction.getTransactionSequenceNumber());
                            // Set the sales associate to the current associate
                            currentLineItem.setSalesAssociate(currentTransaction.getSalesAssociate());
                            // The PriceAdjustmentLineItemIfc instance is added for use by the UI and other facilities
                            PriceAdjustmentLineItemIfc priceAdjustmentLineItem = null;
                            priceAdjustmentLineItem = DomainGateway.getFactory().getPriceAdjustmentLineItemInstance();
                            //sale component of price adjustment
                            SaleReturnLineItemIfc saleItem = (SaleReturnLineItemIfc)lineitems[i+1];
                            priceAdjustmentLineItem.initialize(saleItem, item);
                            currentTransaction.addLineItem((AbstractTransactionLineItemIfc)priceAdjustmentLineItem);
                            // get the original tenders used for refund options calculations
                            currentTransaction.setReturnTenderElements(getOriginalTenders(originalTransaction.getTenderLineItems()));
                        } // End of PriceAdjustmentLineItemIfc instance is added
                    } // End there is enough qty for a valid return
                } // End lineitem is a return
            } // End lineitem is a SaleReturnLineItem
        } // End get original transactions
    } // End transaction contains lineitems

    /**
     * Retrieve tenders from original transaction
     * @param tenderList
     * @return ReturnTenderDataElement[] list of tenders
     */
    protected ReturnTenderDataElementIfc[] getOriginalTenders(TenderLineItemIfc[] tenderList)
    {
        ReturnTenderDataElementIfc [] tenders = new ReturnTenderDataElementIfc[tenderList.length];
        for (int i =0; i < tenderList.length; i++)
        {
            tenders[i]=DomainGateway.getFactory().getReturnTenderDataElementInstance();
            tenders[i].setTenderType(tenderList[i].getTypeCode());
            if (tenderList[i].getTypeCode() == TenderLineItemIfc.TENDER_TYPE_CHARGE)
            {
                tenders[i].setCardType(((TenderChargeIfc)tenderList[i]).getCardType());
            }
            tenders[i].setAccountNumber(new String(tenderList[i].getNumber()));
            tenders[i].setTenderAmount(tenderList[i].getAmountTender());
        }
        return tenders;
    }
}
