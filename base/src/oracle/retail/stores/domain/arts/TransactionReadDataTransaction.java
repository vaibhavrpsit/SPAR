/* ===========================================================================
* Copyright (c) 1998, 2014, Oracle and/or its affiliates. All rights reserved.
 * ===========================================================================
 * $Header: rgbustores/modules/domain/src/oracle/retail/stores/domain/arts/TransactionReadDataTransaction.java /main/26 2014/03/20 16:53:21 cgreene Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED (MM/DD/YY)
 *    crain  10/01/14 - Fix to prevent user to return items (non retrieved
 *                         receipted return) from training mode in the normal
 *                         mode and vice versa.
 *    cgreen 03/20/14 - implement search for tranasctions by external order id
 *    vtemke 04/16/13 - Moved constants in OrderLineItemIfc to
 *                      OrderConstantsIfc in common project
 *    cgreen 09/24/12 - Implement maximum customer record retrieval for dtm
 *                      export
 *    cgreen 08/31/11 - do not get layaway details for canceled transactions
 *    cgreen 01/27/11 - refactor creation of data transactions to use spring
 *                      context
 *    nkgaut 07/02/10 - bill pay report changes
 *    cgreen 05/27/10 - convert to oracle packaging
 *    cgreen 05/27/10 - convert to oracle packaging
 *    cgreen 05/26/10 - convert to oracle packaging
 *    cgreen 04/26/10 - XbranchMerge cgreene_tech43 from
 *                      st_rgbustores_techissueseatel_generic_branch
 *    cgreen 04/02/10 - remove deprecated LocaleContantsIfc and currencies
 *    abonda 01/03/10 - update header date
 *    ranojh 12/10/08 - Fixing performance defect using
 *                      maximumTransactionsToExport attribute
 *    mchell 12/02/08 - Changes to setOrderLineItemStatus
 *    aphula 11/27/08 - checking files after merging code for receipt printing
 *                      by Amrish
 *    mchell 11/14/08 - Duefiles moodule
 *    mchell 11/13/08 - Inventory Reservation Module
 *
 * ===========================================================================

     $Log:
      12   360Commerce 1.11        4/12/2008 5:44:57 PM   Christian Greene
           Upgrade StringBuffer to StringBuilder
      11   360Commerce 1.10        7/23/2007 1:11:24 PM   Maisa De Camargo
           Updated RetrieveTransactionIDsByTimePeriod to use the
           etrieveTransactionIDsByTimePeriod DataAction.
      10   360Commerce 1.9         5/1/2007 9:45:27 AM    Jack G. Swan
           Changes for merge to Trunk.
      9    360Commerce 1.8         4/2/2007 5:57:13 PM    Snowber Khan    Merge
            from v8x 1.6.1.0 - CR 21364 - Updated so that tax totals will be
           included in till reconcile transaction poslog., Merge from
           TransactionReadDataTransaction.java, Revision 1.4.1.0

      8    360Commerce 1.7         2/6/2007 11:03:26 AM   Anil Bondalapati
           Merge from TransactionReadDataTransaction.java, Revision 1.4.1.0
      7    360Commerce 1.6         12/19/2006 4:11:31 PM  Brendan W. Farrell
           Fixed for all closes instead of just till.
      6    360Commerce 1.5         12/8/2006 5:01:15 PM   Brendan W. Farrell
           Read the tax history when creating pos log for openclosetill
           transactions.  Rewrite of some code was needed.
      5    360Commerce 1.4         11/9/2006 7:28:30 PM   Jack G. Swan
           Modifided for XML Data Replication and CTR.
      4    360Commerce 1.3         4/24/2006 5:51:32 PM   Charles D. Baker
           Merge of NEP62
      3    360Commerce 1.2         3/31/2005 4:30:35 PM   Robert Pearse
      2    360Commerce 1.1         3/10/2005 10:26:24 AM  Robert Pearse
      1    360Commerce 1.0         2/11/2005 12:15:16 PM  Robert Pearse
     $
     Revision 1.8  2004/09/23 00:30:49  kmcbride
     @scr 7211: Inserting serialVersionUIDs in these Serializable classes

     Revision 1.7  2004/08/16 21:14:50  lzhao
     @scr 6654: remove the relationship for training mode sequence number from real training mode sequence number.

     Revision 1.6  2004/04/09 16:55:44  cdb
     @scr 4302 Removed double semicolon warnings.

     Revision 1.5  2004/02/17 17:57:35  bwf
     @scr 0 Organize imports.

     Revision 1.4  2004/02/17 16:18:45  rhafernik
     @scr 0 log4j conversion

     Revision 1.3  2004/02/12 17:13:19  mcs
     Forcing head revision

     Revision 1.2  2004/02/11 23:25:21  bwf
     @scr 0 Organize imports.

     Revision 1.1.1.1  2004/02/11 01:04:29  cschellenger
     updating to pvcs 360store-current


 *
 *    Rev 1.3   Oct 30 2003 14:04:04   lzhao
 * remove readTransactionByIDOnly.
 *
 *    Rev 1.2   Oct 28 2003 17:44:42   lzhao
 * add comments
 *
 *    Rev 1.1   Oct 28 2003 17:24:54   lzhao
 * add transaction and operation for gift card reload
 *
 *    Rev 1.0   Aug 29 2003 15:34:20   CSchellenger
 * Initial revision.
 *
 *    Rev 1.4   Apr 07 2003 10:31:30   bwf
 * Database Internationalization
 * Resolution for 1866: I18n Database  support
 *
 *    Rev 1.3   Feb 15 2003 17:26:10   mpm
 * Merged 5.1 changes.
 * Resolution for Domain SCR-104: Merge 5.1/5.5 into 6.0
 *
 *    Rev 1.2   Feb 14 2003 09:00:04   RSachdeva
 * Database Internationalization
 * Resolution for POS SCR-1866: I18n Database  support
 *
 *    Rev 1.1   Jan 22 2003 15:34:28   mpm
 * Preliminary merge of 5.1/5.5 code.
 * Resolution for Domain SCR-104: Merge 5.1/5.5 into 6.0
 *
 * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */

package oracle.retail.stores.domain.arts;

import java.io.Serializable;
import java.util.ArrayList;

import oracle.retail.stores.common.utility.LocaleRequestor;
import oracle.retail.stores.domain.DomainGateway;
import oracle.retail.stores.domain.financial.LayawayIfc;
import oracle.retail.stores.domain.financial.RegisterIfc;
import oracle.retail.stores.domain.financial.TaxTotalsContainerIfc;
import oracle.retail.stores.domain.ixretail.log.POSLogTransactionEntryIfc;
import oracle.retail.stores.domain.lineitem.OrderItemStatusIfc;
import oracle.retail.stores.domain.lineitem.SaleReturnLineItemIfc;
import oracle.retail.stores.domain.order.OrderConstantsIfc;
import oracle.retail.stores.domain.store.StoreIfc;
import oracle.retail.stores.domain.tax.TaxHistorySelectionCriteria;
import oracle.retail.stores.domain.tax.TaxHistorySelectionCriteriaIfc;
import oracle.retail.stores.domain.transaction.LayawayTransactionIfc;
import oracle.retail.stores.domain.transaction.OrderLineItemSearchCriteria;
import oracle.retail.stores.domain.transaction.OrderLineItemSearchCriteriaIfc;
import oracle.retail.stores.domain.transaction.OrderTransactionIfc;
import oracle.retail.stores.domain.transaction.RegisterOpenCloseTransactionIfc;
import oracle.retail.stores.domain.transaction.SearchCriteria;
import oracle.retail.stores.domain.transaction.SearchCriteriaIfc;
import oracle.retail.stores.domain.transaction.StoreOpenCloseTransactionIfc;
import oracle.retail.stores.domain.transaction.TenderableTransactionIfc;
import oracle.retail.stores.domain.transaction.TillOpenCloseTransactionIfc;
import oracle.retail.stores.domain.transaction.TransactionConstantsIfc;
import oracle.retail.stores.domain.transaction.TransactionIfc;
import oracle.retail.stores.domain.transaction.TransactionSummaryIfc;
import oracle.retail.stores.domain.transaction.VoidTransactionIfc;
import oracle.retail.stores.domain.utility.EYSDate;
import oracle.retail.stores.foundation.manager.data.DataAction;
import oracle.retail.stores.foundation.manager.data.DataException;
import oracle.retail.stores.foundation.manager.data.DataTransaction;
import oracle.retail.stores.foundation.manager.ifc.data.DataActionIfc;
import oracle.retail.stores.foundation.manager.ifc.data.DataTransactionIfc;
import oracle.retail.stores.foundation.utility.Util;

import org.apache.log4j.Logger;

/**
 * The DataTransaction to perform persistent read operations on the POS
 * Transaction object.
 * 
 * @version $Revision: /main/26 $
 * @see oracle.retail.stores.domain.arts.TransactionWriteDataTransaction
 * @see oracle.retail.stores.domain.arts.UpdateReturnedItemsDataTransaction
 * @see oracle.retail.stores.domain.arts.TransactionHistoryDataTransaction
 */
public class TransactionReadDataTransaction extends DataTransaction implements DataTransactionIfc
{
    // This id is used to tell the compiler not to generate a new serialVersionUID.
    static final long serialVersionUID = -3159317257797343146L;

    /**
     * The logger to which log messages will be sent.
     */
    private static final Logger logger = Logger.getLogger(TransactionReadDataTransaction.class);

    /**
     * revision number of this class
     */
    public static final String revisionNumber = "$Revision: /main/26 $";

    /**
     * The default name that links this transaction to a command within
     * DataScript.
     */
    public static String dataCommandName = "TransactionReadDataTransaction";

    /**
     * The name that reads the tax history.
     */
    public static final String READ_TAX_HISTORY = "ReadTaxHistory";

    /**
     * layaway reference
     */
    protected LayawayIfc layaway = null;

    /**
     * Class constructor.
     * <P>
     */
    public TransactionReadDataTransaction()
    {
        super(dataCommandName);
    }

    /**
     * Class constructor.
     * 
     * @param name transaction name
     */
    public TransactionReadDataTransaction(String name)
    {
        super(name);
    }

    /**
     * Checks to see if the given transaction has been voided.
     * 
     * @param transaction A Transaction that contains the store id, workstation
     *            id, transaction sequence number and business day date.
     * @return True if the transaction has been voided, false otherwise
     * @exception DataException upon error
     */
    public boolean isTransactionVoided(TransactionIfc transaction) throws DataException
    {
        if (logger.isDebugEnabled())
            logger.debug("TransactionReadDataTransaction.isTransactionVoided");

        boolean returnValue = false;

        // set data actions and execute
        DataActionIfc[] dataActions = new DataActionIfc[1];
        DataAction da = new DataAction();
        da.setDataOperationName("IsTransactionVoided");
        da.setDataObject(transaction);
        dataActions[0] = da;
        setDataActions(dataActions);

        Boolean isVoided = (Boolean) getDataManager().execute(this);
        returnValue = isVoided.booleanValue();

        if (logger.isDebugEnabled())
            logger.debug("TransactionReadDataTransaction.isTransactionVoided");

        return (returnValue);
    }

    /**
     * Reads a POS Transaction from the data store.
     * 
     * @param transaction A Transaction that contains the key values required to
     *            restore the transaction from a persistent store. These key
     *            values include the store id, workstation id, business day
     *            date, transaction sequence number, and transaction ID.
     * @return The Transaction that matches the key criteria, null if no
     *         Transaction matches.
     * @exception DataException upon error
     */
    public TransactionIfc readTransaction(TransactionIfc transaction) throws DataException
    {
        if (logger.isDebugEnabled())
            logger.debug("TransactionReadDataTransaction.readTransaction");

        // set data actions and execute
        DataActionIfc[] dataActions = new DataActionIfc[1];
        DataAction da = new DataAction();
        da.setDataOperationName("ReadTransaction");
        da.setDataObject(transaction);
        dataActions[0] = da;
        setDataActions(dataActions);

        TransactionIfc readTransaction = (TransactionIfc) getDataManager().execute(this);

        if (logger.isDebugEnabled())
            logger.debug("TransactionReadDataTransaction.readTransaction");

        return (readTransaction);
    }

    /**
     * Reads POS Transactions from the data store.
     * 
     * @param transactionID The transaction ID to search for.
     * @param businessDay Optional business day.
     * @param trainingMode The valid training mode value.
     * @param localeReq Locales used for for SQL
     * @return An array of transactions with matching transaction ID and
     *         training mode
     * @exception DataException upon error
     */
    public TransactionIfc[] readTransactionsByID(String transactionID, EYSDate businessDay, boolean trainingMode,
            LocaleRequestor localeReq) throws DataException
    {
        if (logger.isDebugEnabled())
            logger.debug("TransactionReadDataTransaction.readTransactionsByID");

        // initialize search criteria
        TransactionIfc transaction = instantiateTransaction(transactionID);
        transaction.setBusinessDay(businessDay);
        transaction.setTrainingMode(trainingMode);
        transaction.setLocaleRequestor(localeReq);

        // set data actions and execute
        DataActionIfc[] dataActions = new DataActionIfc[1];
        DataAction dataAction = new DataAction();
        dataAction.setDataOperationName("ReadTransactionsByID");
        dataAction.setDataObject(transaction);
        dataActions[0] = dataAction;
        setDataActions(dataActions);

        TransactionIfc[] transactionList = (TransactionIfc[]) getDataManager().execute(this);

        if (logger.isDebugEnabled())
            logger.debug("TransactionReadDataTransaction.readTransactionsByID");

        return (transactionList);
    }
    
    /**
     * Reads POS Transactions from the data store.
     * 
     * @param transactionID The transaction ID to search for.
     * @param businessDay Optional business day.
     * @param trainingMode The valid training mode value.
     * @param localeReq Locales used for for SQL
     * @return An array of transactions with matching transaction ID and
     *         training mode
     * @exception DataException upon error
     * @since 14.1
     */
    public TransactionIfc[] readTrainingTransactionsByID(String transactionID, EYSDate businessDay, boolean trainingMode,
            LocaleRequestor localeReq) throws DataException
    {
        if (logger.isDebugEnabled())
            logger.debug("TransactionReadDataTransaction.readTrainingTransactionsByID");

        // initialize search criteria
        TransactionIfc transaction = instantiateTransaction(transactionID);
        transaction.setBusinessDay(businessDay);
        transaction.setTrainingMode(trainingMode);
        transaction.setLocaleRequestor(localeReq);

        // set data actions and execute
        DataActionIfc[] dataActions = new DataActionIfc[1];
        DataAction dataAction = new DataAction();
        dataAction.setDataOperationName("ReadTrainingTransactionsByID");
        dataAction.setDataObject(transaction);
        dataActions[0] = dataAction;
        setDataActions(dataActions);

        TransactionIfc[] transactionList = (TransactionIfc[]) getDataManager().execute(this);

        if (logger.isDebugEnabled())
            logger.debug("TransactionReadDataTransaction.readTrainingTransactionsByID");

        return (transactionList);
    }

    /**
     * Reads a POS Transaction from the data store that is linked to the
     * specified external order id.
     *
     * @param externalOrderID
     * @param localeReq
     * @return
     * @throws DataException
     */
    public TransactionIfc[] retrieveTransactionByExternalOrderID(String externalOrderID,
            LocaleRequestor localeReq) throws DataException
    {
        if (logger.isDebugEnabled())
            logger.debug("TransactionReadDataTransaction.retrieveTransactionByExternalOrderID");

        // initialize search criteria
        SearchCriteriaIfc criteria = new SearchCriteria();
        criteria.setExternalOrderID(externalOrderID);
        criteria.setLocaleRequestor(localeReq);

        // set data actions and execute
        DataActionIfc[] dataActions = new DataActionIfc[1];
        DataAction dataAction = new DataAction();
        dataAction.setDataOperationName("ReadTransactionsByExternalOrderID");
        dataAction.setDataObject(criteria);
        dataActions[0] = dataAction;
        setDataActions(dataActions);

        TransactionIfc[] transactionList = (TransactionIfc[]) getDataManager().execute(this);

        if (logger.isDebugEnabled())
            logger.debug("TransactionReadDataTransaction.retrieveTransactionByExternalOrderID");

        return (transactionList);
    }

    /**
     * Reads a POS Transaction for batch processing from the data store. Unlike
     * other requests in this class, this data will include training mode
     * transactions and post-voided transactions.
     * 
     * @param transactionID The transaction ID to search for.
     * @param businessDay Optional business day.
     * @return transaction with matching transaction ID
     * @exception DataException upon error
     */
    public TransactionIfc readTransactionForBatch(String transactionID, EYSDate businessDay, LocaleRequestor localeReq)
            throws DataException
    {
        // initialize search criteria
        TransactionIfc searchTransaction = instantiateTransaction(transactionID);
        searchTransaction.setBusinessDay(businessDay);
        searchTransaction.setLocaleRequestor(localeReq);

        // set data actions and execute
        DataActionIfc[] dataActions = new DataActionIfc[1];
        dataActions[0] = createDataAction(searchTransaction, "ReadTransactionForBatch");
        setDataActions(dataActions);

        TransactionIfc transaction = (TransactionIfc) getDataManager().execute(this);

        // if we are closing a till, register or store then get the tax history
        int transactionType = transaction.getTransactionType();
        if (transactionType == TransactionConstantsIfc.TYPE_CLOSE_STORE
                || transactionType == TransactionConstantsIfc.TYPE_CLOSE_TILL
                || transactionType == TransactionConstantsIfc.TYPE_CLOSE_REGISTER)
        {
            getTaxHistory(transaction);
        }
        // if its an order transaction reset the order line item status
        else if (transactionType == TransactionConstantsIfc.TYPE_ORDER_CANCEL
                || transactionType == TransactionConstantsIfc.TYPE_ORDER_COMPLETE
                || transactionType == TransactionConstantsIfc.TYPE_ORDER_INITIATE
                || transactionType == TransactionConstantsIfc.TYPE_ORDER_PARTIAL)
        {
            transaction = setOrderLineItemStatus((OrderTransactionIfc) transaction);
        }

        else if ((transactionType == TransactionConstantsIfc.TYPE_LAYAWAY_INITIATE
                || transactionType == TransactionConstantsIfc.TYPE_LAYAWAY_COMPLETE
                || transactionType == TransactionConstantsIfc.TYPE_LAYAWAY_DELETE) &&
                transaction.getTransactionStatus() != TransactionIfc.STATUS_CANCELED)
        {
            transaction = setLayawayTransactionStatus((LayawayTransactionIfc) transaction);
        }

        else if (transactionType == TransactionConstantsIfc.TYPE_VOID)
        {
            VoidTransactionIfc voidTransaction = (VoidTransactionIfc) transaction;
            if (voidTransaction.getOriginalTransaction() instanceof OrderTransactionIfc)
            {
                TenderableTransactionIfc txn = (TenderableTransactionIfc) setOrderLineItemStatus((OrderTransactionIfc) voidTransaction
                        .getOriginalTransaction());
                voidTransaction.setOriginalTransaction(txn);
            }
            else if (voidTransaction.getOriginalTransaction() instanceof LayawayTransactionIfc)
            {
                TenderableTransactionIfc txn = (TenderableTransactionIfc) setLayawayTransactionStatus((LayawayTransactionIfc) voidTransaction
                        .getOriginalTransaction());
                voidTransaction.setOriginalTransaction(txn);
            }
        }

        return (transaction);
    }

    /**
     * This method sets item status for each order line items.
     * 
     * @param transaction
     * @return TransactionIfc
     * @throws DataException
     */
    private TransactionIfc setOrderLineItemStatus(OrderTransactionIfc transaction) throws DataException
    {
        // Get the line items
        SaleReturnLineItemIfc[] lineItems = transaction.getOrderLineItems();
        // Parse the line items and set the item status
        for (int itemCounter = 0; itemCounter < lineItems.length; itemCounter++)
        {
            OrderItemStatusIfc orderStatus = lineItems[itemCounter].getOrderItemStatus();

            // Don't have to reset the line item status if disposition code is
            // not PICKP or Delivery
            if (orderStatus != null
                    && (OrderConstantsIfc.ORDER_ITEM_DISPOSITION_PICKUP == orderStatus.getItemDispositionCode() || OrderConstantsIfc.ORDER_ITEM_DISPOSITION_DELIVERY == orderStatus
                            .getItemDispositionCode()))
            {
                // Read the item status from StoreOrderLineItem table
                // set data actions and execute
                OrderLineItemSearchCriteriaIfc criteria = new OrderLineItemSearchCriteria();
                criteria.setStoreID(transaction.getFormattedStoreID());
                criteria.setOrderID(transaction.getOrderID());
                criteria.setWorkStationID(transaction.getWorkstation().getWorkstationID());
                criteria.setBusinessDay(transaction.getBusinessDay());
                criteria.setTransactionSequenceNo(Long.toString((transaction.getTransactionSequenceNumber())));
                criteria.setLineItemSequenceNo(lineItems[itemCounter].getLineNumber());
                criteria.setOrderBusinessDate(transaction.getOrderStatus().getTimestampBegin().dateValue());
                criteria.setOriginalLineNumber(lineItems[itemCounter].getOriginalLineNumber());

                DataActionIfc[] dataActions = new DataActionIfc[1];
                DataAction dataAction = new DataAction();
                dataAction.setDataOperationName("ReadOrderLineItemStatus");
                dataAction.setDataObject(criteria);
                dataActions[0] = dataAction;
                setDataActions(dataActions);

                int lineItemStatus = (Integer) getDataManager().execute(this);
                orderStatus.getStatus().setStatus(lineItemStatus);
                lineItems[itemCounter].setOrderItemStatus(orderStatus);
            }
        }
        // Set the line items with new status
        transaction.setLineItems(lineItems);
        return transaction;
    }

    /**
     * This method sets layaway status.
     * 
     * @param transaction
     * @return TransactionIfc
     * @throws DataException
     */
    @SuppressWarnings("unchecked")
    private TransactionIfc setLayawayTransactionStatus(LayawayTransactionIfc transaction) throws DataException
    {

        OrderLineItemSearchCriteriaIfc criteria = new OrderLineItemSearchCriteria();
        criteria.setStoreID(transaction.getFormattedStoreID());
        criteria.setWorkStationID(transaction.getWorkstation().getWorkstationID());
        criteria.setBusinessDay(transaction.getBusinessDay());
        criteria.setTransactionSequenceNo(Long.toString((transaction.getTransactionSequenceNumber())));
        criteria.setLayawayID(transaction.getLayaway().getLayawayID());

        DataActionIfc[] dataActions = new DataActionIfc[1];
        DataAction dataAction = new DataAction();
        dataAction.setDataOperationName("ReadLayawayTransactionStatus");
        dataAction.setDataObject(criteria);
        dataActions[0] = dataAction;
        setDataActions(dataActions);

        ArrayList<Integer> status = (ArrayList<Integer>)getDataManager().execute(this);
        transaction.getLayaway().setStatus(status.get(0));
        transaction.getLayaway().setPreviousStatus(status.get(1));

        return transaction;
    }

    /**
     * This method gets the Tax History.
     * 
     * @param transaction
     * @return
     * @throws DataException
     */
    private void getTaxHistory(TransactionIfc transaction) throws DataException
    {
        int criteriaType = -1;
        DataActionIfc[] dataActions = new DataActionIfc[1];
        TaxHistorySelectionCriteriaIfc criteria = DomainGateway.getFactory().getTaxHistorySelectionCriteriaInstance();

        // depending on the transaction type, set the criteria type and any
        // specific criteria
        int transactionType = transaction.getTransactionType();
        if (transactionType == TransactionConstantsIfc.TYPE_CLOSE_STORE)
        {
            criteriaType = TaxHistorySelectionCriteria.SEARCH_BY_STORE;
        }
        else if (transactionType == TransactionConstantsIfc.TYPE_CLOSE_TILL)
        {
            criteriaType = TaxHistorySelectionCriteria.SEARCH_BY_TILL;
            criteria.setTillId(((TillOpenCloseTransactionIfc) transaction).getTill().getTillID());
        }
        else if (transactionType == TransactionConstantsIfc.TYPE_CLOSE_REGISTER)
        {
            criteriaType = TaxHistorySelectionCriteria.SEARCH_BY_WORKSTATION;
            criteria.setWorkstationId(((RegisterOpenCloseTransactionIfc) transaction).getRegister().getWorkstation()
                    .getWorkstationID());
        }

        // set the general criteria
        criteria.setStoreId(transaction.getWorkstation().getStoreID());
        criteria.setBusinessDate(transaction.getBusinessDay());
        criteria.setCriteriaType(criteriaType);
        dataActions[0] = createDataAction(criteria, READ_TAX_HISTORY);
        setDataActions(dataActions);

        // execute and store into the correct place
        switch (criteriaType)
        {
        // for store opens and closes
        case TaxHistorySelectionCriteria.SEARCH_BY_STORE:
            ((StoreOpenCloseTransactionIfc) transaction).getEndOfDayTotals().setTaxes(
                    (TaxTotalsContainerIfc) getDataManager().execute(this));
            break;
        // for till opens and closes
        case TaxHistorySelectionCriteria.SEARCH_BY_TILL:
            ((TillOpenCloseTransactionIfc) transaction).getTill().getTotals().setTaxes(
                    (TaxTotalsContainerIfc) getDataManager().execute(this));
            break;
        // for register opens and close
        case TaxHistorySelectionCriteria.SEARCH_BY_WORKSTATION:
            ((RegisterOpenCloseTransactionIfc) transaction).getRegister().getTotals().setTaxes(
                    (TaxTotalsContainerIfc) getDataManager().execute(this));
            break;
        // don't do anything for default
        default:
            break;
        }

        return;
    }

    /**
     * Reads a list of POS transactions by store, business date, status and till
     * identifier. Initially, the primary purpose of this request is to retrieve
     * a list of suspended transactions.
     * 
     * @param inquiry the search criteria
     * @return array of transaction summary objects
     * @throws DataException thrown if error occurs
     */
    public TransactionSummaryIfc[] readTransactionListByStatus(SearchCriteriaIfc inquiry) throws DataException
    {
        // set data actions and execute
        DataActionIfc[] dataActions = new DataActionIfc[1];
        DataAction da = new DataAction();
        da.setDataOperationName("ReadTransactionListByStatus");
        da.setDataObject(inquiry);
        dataActions[0] = da;
        setDataActions(dataActions);

        TransactionSummaryIfc[] summaryList = (TransactionSummaryIfc[]) getDataManager().execute(this);
        return summaryList;
    }

    /**
     * Reads a list of POS transactions by store, business date, status and till
     * identifier. Initially, the primary purpose of this request is to retrieve
     * a list of suspended transactions.
     * 
     * @param inquiry the search criteria
     * @return array of transaction summary objects
     * @throws DataException thrown if error occurs
     */
    public TransactionSummaryIfc retrieveBillPayments(SearchCriteriaIfc inquiry) throws DataException
    {
        // set data actions and execute
        DataActionIfc[] dataActions = new DataActionIfc[1];
        DataAction da = new DataAction();
        da.setDataOperationName("ReadBillPayments");
        da.setDataObject(inquiry);
        dataActions[0] = da;
        setDataActions(dataActions);

        TransactionSummaryIfc summaryList = (TransactionSummaryIfc) getDataManager().execute(this);
        return summaryList;
    }

    /**
     * Retrieves layaway-initiate transactions associated with a layaway. The
     * entire layaway is used as a parameter for this so that the training-mode
     * flag can be passed along.
     * 
     * @param layaway layaway with which layaway-initiate transaction is to be
     *            associated
     * @return layaway-initiate transaction associated with layaway
     * @exception DataException thrown if error occurs
     */
    public LayawayTransactionIfc retrieveLayawayTransactionByLayaway(LayawayIfc layaway) throws DataException
    {
        if (logger.isDebugEnabled())
            logger.debug("TransactionReadDataTransaction.retrieveLayawayTransactionByLayaway");

        // set data actions and execute
        DataActionIfc[] dataActions = new DataActionIfc[2];
        DataAction da = new DataAction();
        da.setDataOperationName("ReadLayawayForTransaction");
        da.setDataObject(layaway);
        dataActions[0] = da;

        da = new DataAction();
        da.setDataOperationName("ReadLayawayTransactionByLayaway");
        da.setDataObject((Serializable) null);
        dataActions[1] = da;

        setDataActions(dataActions);

        LayawayTransactionIfc layawayTransaction = (LayawayTransactionIfc) getDataManager().execute(this);

        if (logger.isDebugEnabled())
            logger.debug("TransactionReadDataTransaction.retrieveLayawayTransactionByLayaway");

        return (layawayTransaction);
    }

    /**
     * Retrieves transaction identifiers for TLog creation tlog batch code,
     * business date and store ID. The business date and store identifier
     * parameters are optional.
     * 
     * @param storeID store identifier
     * @param businessDate business date (optional)
     * @param batchID TLog batch identifier
     * @return array of transaction tlog entry objects
     * @exception DataException thrown if error occurs
     */
    public POSLogTransactionEntryIfc[] retrieveTransactionIDsByBatchID(String storeID, EYSDate businessDate,
            String batchID) throws DataException
    {
        return retrieveTransactionIDsByBatchID(storeID, businessDate, batchID,
                POSLogTransactionEntryIfc.USE_BATCH_ARCHIVE);
    }

    /**
     * Retrieves transaction identifiers for TLog creation tlog batch code,
     * business date and store ID. The business date and store identifier
     * parameters are optional.
     * 
     * @param storeID store identifier
     * @param businessDate business date (optional)
     * @param batchID TLog batch identifier
     * @param columnID indcates if the TLog or Batch Archive column will be
     *            updated.
     * @return array of transaction tlog entry objects
     * @exception DataException thrown if error occurs
     */
    public POSLogTransactionEntryIfc[] retrieveTransactionIDsByBatchID(String storeID, EYSDate businessDate,
            String batchID, int columnID) throws DataException
    {
        if (logger.isDebugEnabled())
            logger.debug("TransactionReadDataTransaction.retrieveTransactionIDsByBatchID");

        // set data actions and execute
        POSLogTransactionEntryIfc searchKey = DomainGateway.getFactory().getPOSLogTransactionEntryInstance();
        searchKey.setStoreID(storeID);
        searchKey.setBusinessDate(businessDate);
        searchKey.setBatchID(batchID);
        searchKey.setColumnID(columnID);

        DataActionIfc[] dataActions = new DataActionIfc[1];
        dataActions[0] = createDataAction(searchKey, "RetrieveTransactionIDsByBatchID");
        setDataActions(dataActions);

        POSLogTransactionEntryIfc[] entries = (POSLogTransactionEntryIfc[]) getDataManager().execute(this);

        if (logger.isDebugEnabled())
        {
            logger.debug("TransactionReadDataTransaction.retrieveTransactionIDsByBatchID from data manager: "
                    + getDataManager().getName());
            for (int i = 0; i < dataActions.length; i++)
            {
                logger.debug("DataAction[" + i + "] - " + dataActions[i].getDataOperationName() + ", "
                        + dataActions[i].getDataObject());
            }
        }
        return (entries);
    }

    /**
     * Retrieves transaction identifiers for TLog creation tlog batch code,
     * business date and store ID. The business date and store identifier
     * parameters are optional.
     * 
     * @param storeID store identifier
     * @param businessDate business date (optional)
     * @param batchID TLog batch identifier
     * @param columnID indcates if the TLog or Batch Archive column will be
     *            updated.
     * @param maximumTransactionsToExport indicates the maxTrans to be exported
     *            at one batch.
     * @return array of transaction tlog entry objects
     * @exception DataException thrown if error occurs
     */
    public POSLogTransactionEntryIfc[] retrieveTransactionIDsByBatchID(String storeID, EYSDate businessDate,
            String batchID, int columnID, int maxTransactionsToExport) throws DataException
    {
        if (logger.isDebugEnabled())
            logger.debug("TransactionReadDataTransaction.retrieveTransactionIDsByBatchID");

        // set data actions and execute
        POSLogTransactionEntryIfc searchKey = DomainGateway.getFactory().getPOSLogTransactionEntryInstance();
        searchKey.setStoreID(storeID);
        searchKey.setBusinessDate(businessDate);
        searchKey.setBatchID(batchID);
        searchKey.setColumnID(columnID);
        searchKey.setMaximumTransactionsToExport(maxTransactionsToExport);

        DataActionIfc[] dataActions = new DataActionIfc[1];
        dataActions[0] = createDataAction(searchKey, "RetrieveTransactionIDsByBatchID");
        setDataActions(dataActions);

        POSLogTransactionEntryIfc[] entries = (POSLogTransactionEntryIfc[]) getDataManager().execute(this);

        if (logger.isDebugEnabled())
        {
            logger.debug("TransactionReadDataTransaction.retrieveTransactionIDsByBatchID from data manager: "
                    + getDataManager().getName());
            for (int i = 0; i < dataActions.length; i++)
            {
                logger.debug("DataAction[" + i + "] - " + dataActions[i].getDataOperationName() + ", "
                        + dataActions[i].getDataObject());
            }
        }
        return (entries);
    }

    /**
     * Retrieves transaction identifiers for TLog creation tlog batch code,
     * business date and store ID. The store identifier parameters is optional.
     * 
     * @param storeID store identifier
     * @param businessDate business date (optional)
     * @param batchID TLog batch identifier
     * @param columnID indcates if the TLog or Batch Archive column will be
     *            updated.
     * @return array of transaction tlog entry objects
     * @exception DataException thrown if error occurs
     */
    public POSLogTransactionEntryIfc[] retrieveTransactionsByTimePeriod(String storeID, EYSDate start, EYSDate end)
            throws DataException
    {
        if (logger.isDebugEnabled())
            logger.debug("TransactionReadDataTransaction.retrieveTransactionsByTimePeriod");

        // set data actions and execute
        POSLogTransactionEntryIfc searchKey = DomainGateway.getFactory().getPOSLogTransactionEntryInstance();
        searchKey.setStoreID(storeID);
        searchKey.setStartTime(start);
        searchKey.setEndTime(end);

        DataActionIfc[] dataActions = new DataActionIfc[1];
        dataActions[0] = createDataAction(searchKey, "RetrieveTransactionIDsByTimePeriod");
        setDataActions(dataActions);

        POSLogTransactionEntryIfc[] entries = (POSLogTransactionEntryIfc[]) getDataManager().execute(this);

        if (logger.isDebugEnabled())
        {
            logger.debug("TransactionReadDataTransaction.retrieveTransactionIDsByTimePeriod from data manager: "
                    + getDataManager().getName());
            for (int i = 0; i < dataActions.length; i++)
            {
                logger.debug("DataAction[" + i + "] - " + dataActions[i].getDataOperationName() + ", "
                        + dataActions[i].getDataObject());
            }
        }
        return (entries);
    }

    /**
     * Retrieves transaction identifiers for transactions which haven't been
     * assigned to a batch. The business date and store identifier parameters
     * are optional.
     * 
     * @param storeID store identifier
     * @param businessDate business date (optional)
     * @return array of tlog transaction entry objects
     * @exception DataException thrown if error occurs
     */
    public POSLogTransactionEntryIfc[] retrieveTransactionsNotInBatch(String storeID, EYSDate businessDate)
            throws DataException
    {
        if (logger.isDebugEnabled())
            logger.debug("TransactionReadDataTransaction.retrieveTransactionsNotInBatch");

        POSLogTransactionEntryIfc[] entries = retrieveTransactionIDsByBatchID(storeID, businessDate,
                POSLogTransactionEntryIfc.NO_BATCH_IDENTIFIED);

        if (logger.isDebugEnabled())
            logger.debug("TransactionReadDataTransaction.retrieveTransactionsNotInBatch");

        return (entries);
    }

    /**
     * Retrieves transaction identifiers for transactions which haven't been
     * assigned to a batch. The business date and store identifier parameters
     * are optional.
     * 
     * @param storeID store identifier
     * @return array of tlog transaction entry objects
     * @exception DataException thrown if error occurs
     */
    public POSLogTransactionEntryIfc[] retrieveTransactionsNotInBatch(String storeID) throws DataException
    {
        if (logger.isDebugEnabled())
            logger.debug("TransactionReadDataTransaction.retrieveTransactionsNotInBatch");

        POSLogTransactionEntryIfc[] entries = retrieveTransactionIDsByBatchID(storeID, null,
                POSLogTransactionEntryIfc.NO_BATCH_IDENTIFIED);

        if (logger.isDebugEnabled())
            logger.debug("TransactionReadDataTransaction.retrieveTransactionsNotInBatch");

        return (entries);
    }

    /**
     * Retrieves transaction identifiers for transactions which haven't been
     * assigned to a batch. The business date and store identifier parameters
     * are optional.
     * 
     * @param storeID store identifier
     * @param columnID indcates if the TLog or Batch Archive column will be
     *            used.
     * @param maximumTransactionsToExport
     * @return array of tlog transaction entry objects
     * @exception DataException thrown if error occurs
     */
    public POSLogTransactionEntryIfc[] retrieveTransactionsNotInBatch(String storeID, int columnID,
            int maximumTransactionsToExport)

    throws DataException
    {

        if (logger.isDebugEnabled())
            logger.debug("TransactionReadDataTransaction.retrieveTransactionsNotInBatch");

        POSLogTransactionEntryIfc[] entries = retrieveTransactionIDsByBatchID(storeID, null,
                POSLogTransactionEntryIfc.NO_BATCH_IDENTIFIED, columnID, maximumTransactionsToExport);

        if (logger.isDebugEnabled())
            logger.debug("TransactionReadDataTransaction.retrieveTransactionsNotInBatch");

        return (entries);
    }

    /**
     * Retrieves transaction identifiers for transactions for a particular
     * business date. This is used for post-processing testing.
     * 
     * @param storeID store identifier
     * @param businessDate business date
     * @return array of tlog transaction entry objects
     * @exception DataException thrown if error occurs
     */
    public POSLogTransactionEntryIfc[] retrieveTransactionIDsByBusinessDate(String storeID, EYSDate businessDate)
            throws DataException
    {
        if (logger.isDebugEnabled())
            logger.debug("TransactionReadDataTransaction.retrieveTransactionIDsByBusinessDate");

        // set data actions and execute
        POSLogTransactionEntryIfc searchKey = DomainGateway.getFactory().getPOSLogTransactionEntryInstance();
        searchKey.setStoreID(storeID);
        searchKey.setBusinessDate(businessDate);

        DataActionIfc[] dataActions = new DataActionIfc[1];
        dataActions[0] = createDataAction(searchKey, "RetrieveTransactionIDsByBusinessDate");
        setDataActions(dataActions);

        POSLogTransactionEntryIfc[] entries = (POSLogTransactionEntryIfc[]) getDataManager().execute(this);

        if (logger.isDebugEnabled())
            logger.debug("TransactionReadDataTransaction.retrieveTransactionIDsByBusinessDate");

        return (entries);
    }

    /**
     * Gets last training mode transaction sequence number.
     * 
     * @return last training mode transaction sequence number
     * @exception DataException upon error
     */
    public int getLastTrainingTransactionSequenceNumber(RegisterIfc register) throws DataException
    {
        if (logger.isDebugEnabled())
        {
            logger.debug("TransactionReadDataTransaction.getLastTrainingTransactionSequenceNumber");
        }

        // set data actions and execute
        DataActionIfc[] dataActions = new DataActionIfc[1];
        DataAction dataAction = new DataAction();
        dataAction.setDataOperationName("GetLastTrainingTransactionSequenceNumber");
        dataAction.setDataObject(register);
        dataActions[0] = dataAction;
        setDataActions(dataActions);

        Integer seqNumber = (Integer) getDataManager().execute(this);

        return (seqNumber.intValue());
    }

    /**
     * Instantiates TransactionSummaryIfc object.
     * 
     * @return TransactionSummaryIfc object
     */
    public TransactionSummaryIfc instantiateTransactionSummaryIfc()
    {
        return (DomainGateway.getFactory().getTransactionSummaryInstance());
    }

    /**
     * Instantiates StoreIfc object.
     * 
     * @return StoreIfc object
     */
    public StoreIfc instantiateStoreIfc()
    {
        return (DomainGateway.getFactory().getStoreInstance());
    }

    /**
     * Instantiates TransactionIfc object.
     * 
     * @param transactionID The transaction ID to initialize
     * @return TransactionIfc object
     */
    public TransactionIfc instantiateTransaction(String transactionID)
    {
        TransactionIfc transaction = DomainGateway.getFactory().getTransactionInstance();
        transaction.initialize(transactionID);
        return (transaction);
    }

    /**
     * Returns the revision number of this class.
     * 
     * @return String representation of revision number
     */
    public String getRevisionNumber()
    {
        return (Util.parseRevisionNumber(revisionNumber));
    }

    /* (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString()
    {
        StringBuilder strResult = Util.classToStringHeader(getClass().getName(), getRevisionNumber(), hashCode());
        return (strResult.toString());
    }
}
