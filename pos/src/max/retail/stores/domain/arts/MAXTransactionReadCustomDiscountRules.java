/**
 * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
  Copyright (c) 2013 MAXHyperMarkets, Inc.    All Rights Reserved.
   Rev 1.0	Izhar		29/05/2013		Discount Rule
 
* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */

package max.retail.stores.domain.arts;

import java.util.HashMap;

import org.apache.log4j.Logger;

import max.retail.stores.domain.discount.MAXDiscountRuleConstantsIfc;
import oracle.retail.stores.domain.stock.PLUItemIfc;
import oracle.retail.stores.foundation.manager.data.DataAction;
import oracle.retail.stores.foundation.manager.data.DataException;
import oracle.retail.stores.foundation.manager.data.DataTransaction;
import oracle.retail.stores.foundation.manager.ifc.data.DataActionIfc;
import oracle.retail.stores.foundation.manager.ifc.data.DataTransactionIfc;
import oracle.retail.stores.foundation.utility.Util;

//-------------------------------------------------------------------------
/**
 * The DataTransaction to perform persistent read operations on the POS
 * Transaction object.
 * 
 * @version $Revision: 1.1 $
 * @see com.extendyourstore.domain.arts.TransactionWriteDataTransaction
 * @see com.extendyourstore.domain.arts.UpdateReturnedItemsDataTransaction
 * @see com.extendyourstore.domain.arts.TransactionHistoryDataTransaction
 **/
// -------------------------------------------------------------------------
public class MAXTransactionReadCustomDiscountRules extends DataTransaction implements DataTransactionIfc {
	// This id is used to tell
	// the compiler not to generate a
	// new serialVersionUID.
	//
	static final long serialVersionUID = -3159317257797343146L;

	/**
	 * The logger to which log messages will be sent.
	 **/
	private static Logger logger = Logger
			.getLogger(max.retail.stores.domain.arts.MAXTransactionReadCustomDiscountRules.class);

	/**
	 * revision number of this class
	 **/
	public static String revisionNumber = "$Revision: 1.1 $";
	/**
	 * The default name that links this transaction to a command within
	 * DataScript.
	 **/
	public static String dataCommandName = "MAXTransactionReadCustomDiscountRules";

	/**
	 * The name that reads the tax history.
	 */
	// public static final String READ_TAX_HISTORY = "ReadTaxHistory";

	/**
	 * layaway reference
	 **/
	// protected LayawayIfc layaway = null;

	// ---------------------------------------------------------------------
	/**
	 * Class constructor.
	 * <P>
	 **/
	// ---------------------------------------------------------------------
	public MAXTransactionReadCustomDiscountRules() {
		super(dataCommandName);
	}

	// ---------------------------------------------------------------------
	/**
	 * Class constructor.
	 * <P>
	 * 
	 * @param name
	 *            transaction name
	 **/
	// ---------------------------------------------------------------------
	public MAXTransactionReadCustomDiscountRules(String name) {
		super(name);
	}

	// ---------------------------------------------------------------------
	/**
	 * Checks to see if the given transaction has been voided.
	 * 
	 * @param transaction
	 *            A Transaction that contains the store id, workstation id,
	 *            transaction sequence number and business day date.
	 * @return True if the transaction has been voided, false otherwise
	 * @exception DataException
	 *                upon error
	 **/
	// ---------------------------------------------------------------------
	/*
	 * public boolean isTransactionVoided(TransactionIfc transaction) throws
	 * DataException { if (logger.isDebugEnabled()) logger.debug(
	 * "TransactionReadDataTransaction.isTransactionVoided");
	 * 
	 * boolean returnValue = false;
	 * 
	 * // set data actions and execute DataActionIfc[] dataActions = new
	 * DataActionIfc[1]; DataAction da = new DataAction();
	 * da.setDataOperationName("IsTransactionVoided");
	 * da.setDataObject(transaction); dataActions[0] = da;
	 * setDataActions(dataActions);
	 * 
	 * Boolean isVoided = (Boolean) getDataManager().execute(this); returnValue
	 * = isVoided.booleanValue();
	 * 
	 * if (logger.isDebugEnabled()) logger.debug(
	 * "TransactionReadDataTransaction.isTransactionVoided");
	 * 
	 * return(returnValue); }
	 */

	// ---------------------------------------------------------------------
	/**
	 * Reads a POS Transaction from the data store.
	 * 
	 * @param transaction
	 *            A Transaction that contains the key values required to restore
	 *            the transaction from a persistent store. These key values
	 *            include the store id, workstation id, business day date,
	 *            transaction sequence number, and transaction ID.
	 * @return The Transaction that matches the key criteria, null if no
	 *         Transaction matches.
	 * @exception DataException
	 *                upon error
	 **/
	// ---------------------------------------------------------------------
	/*
	 * public TransactionIfc readTransaction(TransactionIfc transaction) throws
	 * DataException { if (logger.isDebugEnabled()) logger.debug(
	 * "TransactionReadDataTransaction.readTransaction");
	 * 
	 * // set data actions and execute DataActionIfc[] dataActions = new
	 * DataActionIfc[1]; DataAction da = new DataAction();
	 * da.setDataOperationName("ReadTransaction");
	 * da.setDataObject(transaction); dataActions[0] = da;
	 * setDataActions(dataActions);
	 * 
	 * TransactionIfc readTransaction = (TransactionIfc)
	 * getDataManager().execute(this);
	 * 
	 * if (logger.isDebugEnabled()) logger.debug(
	 * "TransactionReadDataTransaction.readTransaction");
	 * 
	 * return(readTransaction); }
	 */

	// ---------------------------------------------------------------------
	/**
	 * Reads POS Transactions from the data store.
	 * 
	 * @param transactionID
	 *            The transaction ID to search for.
	 * @param businessDay
	 *            Optional business day.
	 * @param trainingMode
	 *            The valid training mode value.
	 * @return An array of transactions with matching transaction ID and
	 *         training mode
	 * @exception DataException
	 *                upon error
	 **/
	// ---------------------------------------------------------------------
	/*
	 * public TransactionIfc[] readTransactionsByID(String transactionID,
	 * EYSDate businessDay, boolean trainingMode) throws DataException { if
	 * (logger.isDebugEnabled()) logger.debug(
	 * "TransactionReadDataTransaction.readTransactionsByID");
	 * 
	 * // initialize search criteria TransactionIfc transaction =
	 * instantiateTransaction(transactionID);
	 * transaction.setBusinessDay(businessDay);
	 * transaction.setTrainingMode(trainingMode);
	 * 
	 * // set data actions and execute DataActionIfc[] dataActions = new
	 * DataActionIfc[1]; DataAction dataAction = new DataAction();
	 * dataAction.setDataOperationName("ReadTransactionsByID");
	 * dataAction.setDataObject(transaction); dataActions[0] = dataAction;
	 * setDataActions(dataActions);
	 * 
	 * TransactionIfc[] transactionList =
	 * (TransactionIfc[])getDataManager().execute(this);
	 * 
	 * if (logger.isDebugEnabled()) logger.debug(
	 * "TransactionReadDataTransaction.readTransactionsByID");
	 * 
	 * return(transactionList); }
	 */

	// ---------------------------------------------------------------------
	/**
	 * Reads POS Transactions from the data store.
	 * 
	 * @param transactionID
	 *            The transaction ID to search for.
	 * @param businessDay
	 *            Optional business day.
	 * @param trainingMode
	 *            The valid training mode value.
	 * @param locale
	 *            Locale used for for SQL
	 * @return An array of transactions with matching transaction ID and
	 *         training mode
	 * @exception DataException
	 *                upon error
	 **/
	// ---------------------------------------------------------------------
	/*
	 * public TransactionIfc[] readTransactionsByID(String transactionID,
	 * EYSDate businessDay, boolean trainingMode, Locale locale) throws
	 * DataException { if (logger.isDebugEnabled()) logger.debug(
	 * "TransactionReadDataTransaction.readTransactionsByID");
	 * 
	 * // initialize search criteria TransactionIfc transaction =
	 * instantiateTransaction(transactionID);
	 * transaction.setBusinessDay(businessDay);
	 * transaction.setTrainingMode(trainingMode); transaction.setLocale(locale);
	 * 
	 * // set data actions and execute DataActionIfc[] dataActions = new
	 * DataActionIfc[1]; DataAction dataAction = new DataAction();
	 * dataAction.setDataOperationName("ReadTransactionsByID");
	 * dataAction.setDataObject(transaction); dataActions[0] = dataAction;
	 * setDataActions(dataActions);
	 * 
	 * TransactionIfc[] transactionList =
	 * (TransactionIfc[])getDataManager().execute(this);
	 * 
	 * if (logger.isDebugEnabled()) logger.debug(
	 * "TransactionReadDataTransaction.readTransactionsByID");
	 * 
	 * return(transactionList); }
	 */
	// ---------------------------------------------------------------------
	/**
	 * Reads POS Transactions from the data store.
	 * 
	 * @param transactionID
	 *            The transaction ID to search for.
	 * @param Rounding
	 *            Type of Rounding set.
	 * @param RoundingDenomination
	 *            Rounding Denomination.
	 * @param businessDay
	 *            Optional business day.
	 * @param trainingMode
	 *            The valid training mode value.
	 * @param locale
	 *            Locale used for for SQL
	 * @return An array of transactions with matching transaction ID and
	 *         training mode
	 * @exception DataException
	 *                upon error
	 **/
	// ---------------------------------------------------------------------
	/*
	 * public TransactionIfc[] readTransactionsByID(String transactionID, String
	 * Rounding, List RoundingDenomination, EYSDate businessDay, boolean
	 * trainingMode, Locale locale) throws DataException { if
	 * (logger.isDebugEnabled()) logger.debug(
	 * "TransactionReadDataTransaction.readTransactionsByID");
	 * 
	 * // initialize search criteria TransactionIfc transaction =
	 * instantiateTransaction(transactionID);
	 * transaction.setBusinessDay(businessDay);
	 * transaction.setTrainingMode(trainingMode); transaction.setLocale(locale);
	 * transaction.setRounding(Rounding);
	 * transaction.setRoundingDenominations(RoundingDenomination);
	 * 
	 * // set data actions and execute DataActionIfc[] dataActions = new
	 * DataActionIfc[1]; DataAction dataAction = new DataAction();
	 * dataAction.setDataOperationName("ReadTransactionsByID");
	 * dataAction.setDataObject(transaction); dataActions[0] = dataAction;
	 * setDataActions(dataActions);
	 * 
	 * TransactionIfc[] transactionList =
	 * (TransactionIfc[])getDataManager().execute(this);
	 * 
	 * if (logger.isDebugEnabled()) logger.debug(
	 * "TransactionReadDataTransaction.readTransactionsByID");
	 * 
	 * return(transactionList); }
	 */
	// ---------------------------------------------------------------------
	/**
	 * Reads a POS Transaction for batch processing from the data store. Unlike
	 * other requests in this class, this data will include training mode
	 * transactions and post-voided transactions.
	 * 
	 * @param transactionID
	 *            The transaction ID to search for.
	 * @param businessDay
	 *            Optional business day.
	 * @return transaction with matching transaction ID
	 * @exception DataException
	 *                upon error
	 **/
	// ---------------------------------------------------------------------
	/*
	 * public TransactionIfc readTransactionForBatch(String transactionID,
	 * EYSDate businessDay) throws DataException { if (logger.isDebugEnabled())
	 * logger.debug( "TransactionReadDataTransaction.readTransactionForBatch");
	 * 
	 * // initialize search criteria TransactionIfc searchTransaction =
	 * instantiateTransaction(transactionID);
	 * searchTransaction.setBusinessDay(businessDay);
	 * 
	 * // set data actions and execute DataActionIfc[] dataActions = new
	 * DataActionIfc[1]; dataActions[0] = createDataAction(searchTransaction,
	 * "ReadTransactionForBatch"); setDataActions(dataActions);
	 * 
	 * TransactionIfc transaction = (TransactionIfc)
	 * getDataManager().execute(this);
	 * 
	 * // if we are closing a till, register or store then get the tax history
	 * int transactionType = transaction.getTransactionType(); if
	 * (transactionType == TransactionConstantsIfc.TYPE_CLOSE_STORE ||
	 * transactionType == TransactionConstantsIfc.TYPE_CLOSE_TILL ||
	 * transactionType == TransactionConstantsIfc.TYPE_CLOSE_REGISTER) {
	 * getTaxHistory(transaction); }
	 * 
	 * if (logger.isDebugEnabled()) logger.debug(
	 * "TransactionReadDataTransaction.readTransactionForBatch");
	 * 
	 * return(transaction); }
	 */

	/**
	 * This method gets the Tax History.
	 * 
	 * @param transaction
	 * @return
	 * @throws DataException
	 */
	/*
	 * private void getTaxHistory(TransactionIfc transaction) throws
	 * DataException { int criteriaType = -1; DataActionIfc[] dataActions = new
	 * DataActionIfc[1]; TaxHistorySelectionCriteriaIfc criteria =
	 * DomainGateway.getFactory().getTaxHistorySelectionCriteriaInstance();
	 * 
	 * // depending on the transaction type, set the criteria type and any
	 * specific criteria int transactionType = transaction.getTransactionType();
	 * if (transactionType == TransactionConstantsIfc.TYPE_CLOSE_STORE) {
	 * criteriaType = TaxHistorySelectionCriteria.SEARCH_BY_STORE; } else if
	 * (transactionType == TransactionConstantsIfc.TYPE_CLOSE_TILL) {
	 * criteriaType = TaxHistorySelectionCriteria.SEARCH_BY_TILL;
	 * criteria.setTillId(((TillOpenCloseTransactionIfc)
	 * transaction).getTill().getTillID()); } else if (transactionType ==
	 * TransactionConstantsIfc.TYPE_CLOSE_REGISTER) { criteriaType =
	 * TaxHistorySelectionCriteria.SEARCH_BY_WORKSTATION;
	 * criteria.setWorkstationId(
	 * ((RegisterOpenCloseTransactionIfc)transaction).
	 * getRegister().getWorkstation().getWorkstationID()); }
	 * 
	 * // set the general criteria
	 * criteria.setStoreId(transaction.getWorkstation().getStoreID());
	 * criteria.setBusinessDate(transaction.getBusinessDay());
	 * criteria.setCriteriaType(criteriaType); dataActions[0] =
	 * createDataAction(criteria, READ_TAX_HISTORY);
	 * setDataActions(dataActions);
	 * 
	 * // execute and store into the correct place switch (criteriaType) { //
	 * for store opens and closes case
	 * TaxHistorySelectionCriteria.SEARCH_BY_STORE:
	 * ((StoreOpenCloseTransactionIfc
	 * )transaction).getEndOfDayTotals().setTaxes((TaxTotalsContainerIfc)
	 * getDataManager().execute(this)); break; // for till opens and closes case
	 * TaxHistorySelectionCriteria.SEARCH_BY_TILL:
	 * ((TillOpenCloseTransactionIfc) transaction).
	 * getTill().getTotals().setTaxes((TaxTotalsContainerIfc)
	 * getDataManager().execute(this)); break; // for register opens and close
	 * case TaxHistorySelectionCriteria.SEARCH_BY_WORKSTATION:
	 * ((RegisterOpenCloseTransactionIfc)transaction).
	 * getRegister().getTotals().setTaxes((TaxTotalsContainerIfc)
	 * getDataManager().execute(this)); break; // dont don anything for default
	 * default: break; }
	 * 
	 * return; }
	 */

	// ---------------------------------------------------------------------
	/**
	 * Reads a list of POS transactions by store, business date and status.
	 * Initially, the primary purpose of this request is to retrieve a list of
	 * suspended transactions.
	 * <P>
	 * 
	 * @param storeID
	 *            store identifier
	 * @param businessDate
	 *            business date
	 * @param status
	 *            status code from TransactionIfc
	 * @return array of transaction summary objects
	 * @exception DataException
	 *                thrown if error occurs
	 * @see com.extendyourstore.domain.transaction.TransactionSummaryIfc
	 * @see com.extendyourstore.domain.transaction.TransactionIfc
	 **/
	// ---------------------------------------------------------------------
	/*
	 * public TransactionSummaryIfc[] readTransactionListByStatus(String
	 * storeID, EYSDate businessDate, int status) throws DataException {
	 * TransactionSummaryIfc[] summaryList =
	 * readTransactionListByStatus(storeID, businessDate, status, null);
	 * return(summaryList); }
	 */

	// ---------------------------------------------------------------------
	/**
	 * Reads a list of POS transactions by store, business date and status.
	 * Initially, the primary purpose of this request is to retrieve a list of
	 * suspended transactions.
	 * <P>
	 * 
	 * @param storeID
	 *            store identifier
	 * @param businessDate
	 *            business date
	 * @param status
	 *            status code from TransactionIfc
	 * @param tillID
	 *            till identifier
	 * @return array of transaction summary objects
	 * @exception DataException
	 *                thrown if error occurs
	 * @see com.extendyourstore.domain.transaction.TransactionSummaryIfc
	 * @see com.extendyourstore.domain.transaction.TransactionIfc
	 **/
	// ---------------------------------------------------------------------
	/*
	 * public TransactionSummaryIfc[] readTransactionListByStatus(String
	 * storeID, EYSDate businessDate, int status, String tillID) throws
	 * DataException { TransactionSummaryIfc[] summaryList =
	 * readTransactionListByStatus(storeID, businessDate, status, tillID,
	 * false); return(summaryList); }
	 */

	// ---------------------------------------------------------------------
	/**
	 * Reads a list of POS transactions by store, business date, status and till
	 * identifier. Initially, the primary purpose of this request is to retrieve
	 * a list of suspended transactions.
	 * <P>
	 * 
	 * @param storeID
	 *            store identifier
	 * @param businessDate
	 *            business date
	 * @param status
	 *            status code from TransactionIfc
	 * @param tillID
	 *            till identifier
	 * @param trainingMode
	 *            training mode flag
	 * @return array of transaction summary objects
	 * @exception DataException
	 *                thrown if error occurs
	 * @see com.extendyourstore.domain.transaction.TransactionSummaryIfc
	 * @see com.extendyourstore.domain.transaction.TransactionIfc
	 **/
	// ---------------------------------------------------------------------
	/*
	 * public TransactionSummaryIfc[] readTransactionListByStatus(String
	 * storeID, EYSDate businessDate, int status, String tillID, boolean
	 * trainingMode) throws DataException { if (logger.isDebugEnabled())
	 * logger.debug(
	 * "TransactionReadDataTransaction.readTransactionListByStatus");
	 * 
	 * TransactionSummaryIfc key = instantiateTransactionSummaryIfc(); StoreIfc
	 * store = instantiateStoreIfc(); store.setStoreID(storeID);
	 * key.setStore(store); key.setBusinessDate(businessDate);
	 * key.setTransactionStatus(status); key.setTillID(tillID);
	 * key.setTrainingMode(trainingMode);
	 * 
	 * // set data actions and execute DataActionIfc[] dataActions = new
	 * DataActionIfc[1]; DataAction da = new DataAction();
	 * da.setDataOperationName("ReadTransactionListByStatus");
	 * da.setDataObject(key); dataActions[0] = da; setDataActions(dataActions);
	 * 
	 * TransactionSummaryIfc[] summaryList = (TransactionSummaryIfc[])
	 * getDataManager().execute(this);
	 * 
	 * if (logger.isDebugEnabled()) logger.debug(
	 * "TransactionReadDataTransaction.readTransactionListByStatus");
	 * 
	 * return(summaryList); }
	 */

	// ---------------------------------------------------------------------
	/**
	 * Reads a list of POS transactions by store, business date, status and till
	 * identifier. Initially, the primary purpose of this request is to retrieve
	 * a list of suspended transactions.
	 * <P>
	 * 
	 * @param storeID
	 *            store identifier
	 * @param businessDate
	 *            business date
	 * @param status
	 *            status code from TransactionIfc
	 * @param tillID
	 *            till identifier
	 * @param trainingMode
	 *            training mode flag
	 * @return array of transaction summary objects
	 * @exception DataException
	 *                thrown if error occurs
	 * @see com.extendyourstore.domain.transaction.TransactionSummaryIfc
	 * @see com.extendyourstore.domain.transaction.TransactionIfc
	 **/
	// ---------------------------------------------------------------------
	/*
	 * public TransactionSummaryIfc[] readTransactionListByStatus(String
	 * storeID, EYSDate businessDate, int status, String tillID, boolean
	 * trainingMode, Locale locale) throws DataException { if
	 * (logger.isDebugEnabled()) logger.debug(
	 * "TransactionReadDataTransaction.readTransactionListByStatus");
	 * 
	 * TransactionSummaryIfc key = instantiateTransactionSummaryIfc(); StoreIfc
	 * store = instantiateStoreIfc(); store.setStoreID(storeID);
	 * key.setStore(store); key.setBusinessDate(businessDate);
	 * key.setTransactionStatus(status); key.setTillID(tillID);
	 * key.setTrainingMode(trainingMode);
	 * 
	 * SearchCriteriaIfc inquiry =
	 * DomainGateway.getFactory().getSearchCriteriaInstance();
	 * inquiry.setTransactionSummary(key); inquiry.setLocale(locale);
	 * 
	 * // set data actions and execute DataActionIfc[] dataActions = new
	 * DataActionIfc[1]; DataAction da = new DataAction();
	 * da.setDataOperationName("ReadTransactionListByStatus");
	 * da.setDataObject(inquiry); dataActions[0] = da;
	 * setDataActions(dataActions);
	 * 
	 * TransactionSummaryIfc[] summaryList = (TransactionSummaryIfc[])
	 * getDataManager().execute(this);
	 * 
	 * if (logger.isDebugEnabled()) logger.debug(
	 * "TransactionReadDataTransaction.readTransactionListByStatus");
	 * 
	 * return(summaryList); }
	 */
	// ---------------------------------------------------------------------
	/**
	 * Retrieves layaway-initiate transactions associated with a layaway. The
	 * entire layaway is used as a parameter for this so that the training-mode
	 * flag can be passed along.
	 * <P>
	 * 
	 * @param layaway
	 *            layaway with which layaway-initiate transaction is to be
	 *            associated
	 * @return layaway-initiate transaction associated with layaway
	 * @exception DataException
	 *                thrown if error occurs
	 **/
	// ---------------------------------------------------------------------
	/*
	 * public LayawayTransactionIfc retrieveLayawayTransactionByLayaway
	 * (LayawayIfc layaway) throws DataException { // begin
	 * retrieveLayawayTransactionByLayaway() if (logger.isDebugEnabled())
	 * logger.debug(
	 * "TransactionReadDataTransaction.retrieveLayawayTransactionByLayaway");
	 * 
	 * // set data actions and execute DataActionIfc[] dataActions = new
	 * DataActionIfc[2]; DataAction da = new DataAction();
	 * da.setDataOperationName("ReadLayawayForTransaction");
	 * da.setDataObject(layaway); dataActions[0] = da;
	 * 
	 * da = new DataAction();
	 * da.setDataOperationName("ReadLayawayTransactionByLayaway");
	 * da.setDataObject((Serializable) null); dataActions[1] = da;
	 * 
	 * setDataActions(dataActions);
	 * 
	 * LayawayTransactionIfc layawayTransaction = (LayawayTransactionIfc)
	 * getDataManager().execute(this);
	 * 
	 * if (logger.isDebugEnabled()) logger.debug(
	 * "TransactionReadDataTransaction.retrieveLayawayTransactionByLayaway");
	 * 
	 * return(layawayTransaction); } // end
	 * retrieveLayawayTransactionByLayaway()
	 */
	// ---------------------------------------------------------------------
	/**
	 * Retrieves transaction identifiers for TLog creation tlog batch code,
	 * business date and store ID. The business date and store identifier
	 * parameters are optional.
	 * <P>
	 * 
	 * @param storeID
	 *            store identifier
	 * @param businessDate
	 *            business date (optional)
	 * @param batchID
	 *            TLog batch identifier
	 * @return array of transaction tlog entry objects
	 * @exception DataException
	 *                thrown if error occurs
	 **/
	// ---------------------------------------------------------------------
	/*
	 * public POSLogTransactionEntryIfc[] retrieveTransactionIDsByBatchID
	 * (String storeID, EYSDate businessDate, String batchID) throws
	 * DataException { return retrieveTransactionIDsByBatchID(storeID,
	 * businessDate, batchID, POSLogTransactionEntryIfc.USE_BATCH_ARCHIVE); }
	 */
	// ---------------------------------------------------------------------
	/**
	 * Retrieves transaction identifiers for TLog creation tlog batch code,
	 * business date and store ID. The business date and store identifier
	 * parameters are optional.
	 * <P>
	 * 
	 * @param storeID
	 *            store identifier
	 * @param businessDate
	 *            business date (optional)
	 * @param batchID
	 *            TLog batch identifier
	 * @param columnID
	 *            indcates if the TLog or Batch Archive column will be updated.
	 * @return array of transaction tlog entry objects
	 * @exception DataException
	 *                thrown if error occurs
	 **/
	// ---------------------------------------------------------------------
	/*
	 * public POSLogTransactionEntryIfc[] retrieveTransactionIDsByBatchID
	 * (String storeID, EYSDate businessDate, String batchID, int columnID)
	 * throws DataException { // begin retrieveTransactionIDsByBatchID()
	 * 
	 * if (logger.isDebugEnabled()) logger.debug(
	 * "TransactionReadDataTransaction.retrieveTransactionIDsByBatchID");
	 * 
	 * // set data actions and execute POSLogTransactionEntryIfc searchKey =
	 * DomainGateway.getFactory().getPOSLogTransactionEntryInstance();
	 * searchKey.setStoreID(storeID); searchKey.setBusinessDate(businessDate);
	 * searchKey.setBatchID(batchID); searchKey.setColumnID(columnID);
	 * 
	 * DataActionIfc[] dataActions = new DataActionIfc[1]; dataActions[0] =
	 * createDataAction(searchKey, "RetrieveTransactionIDsByBatchID");
	 * setDataActions(dataActions);
	 * 
	 * POSLogTransactionEntryIfc[] entries = (POSLogTransactionEntryIfc[])
	 * getDataManager().execute(this);
	 * 
	 * if (logger.isDebugEnabled()) { logger.debug(
	 * "TransactionReadDataTransaction.retrieveTransactionIDsByBatchID from data manager: "
	 * + getDataManager().getName()); for (int i = 0; i < dataActions.length;
	 * i++) { logger.debug("DataAction[" + i + "] - " +
	 * dataActions[i].getDataOperationName() + ", " +
	 * dataActions[i].getDataObject()); } } return(entries); } // end
	 * retrieveTransactionIDsByBatchID()
	 */
	// ---------------------------------------------------------------------
	/**
	 * Retrieves transaction identifiers for TLog creation tlog batch code,
	 * business date and store ID. The store identifier parameters is optional.
	 * <P>
	 * 
	 * @param storeID
	 *            store identifier
	 * @param businessDate
	 *            business date (optional)
	 * @param batchID
	 *            TLog batch identifier
	 * @param columnID
	 *            indcates if the TLog or Batch Archive column will be updated.
	 * @return array of transaction tlog entry objects
	 * @exception DataException
	 *                thrown if error occurs
	 **/
	// ---------------------------------------------------------------------
	/*
	 * public POSLogTransactionEntryIfc[]
	 * retrieveTransactionsByTimePeriod(String storeID, EYSDate start, EYSDate
	 * end) throws DataException { if (logger.isDebugEnabled()) logger.debug(
	 * "TransactionReadDataTransaction.retrieveTransactionsByTimePeriod");
	 * 
	 * // set data actions and execute POSLogTransactionEntryIfc searchKey =
	 * DomainGateway.getFactory().getPOSLogTransactionEntryInstance();
	 * searchKey.setStoreID(storeID); searchKey.setStartTime(start);
	 * searchKey.setEndTime(end);
	 * 
	 * DataActionIfc[] dataActions = new DataActionIfc[1]; dataActions[0] =
	 * createDataAction(searchKey, "RetrieveTransactionIDsByTimePeriod");
	 * setDataActions(dataActions);
	 * 
	 * POSLogTransactionEntryIfc[] entries = (POSLogTransactionEntryIfc[])
	 * getDataManager().execute(this);
	 * 
	 * if (logger.isDebugEnabled()) { logger.debug(
	 * "TransactionReadDataTransaction.retrieveTransactionIDsByTimePeriod from data manager: "
	 * + getDataManager().getName()); for (int i = 0; i < dataActions.length;
	 * i++) { logger.debug("DataAction[" + i + "] - " +
	 * dataActions[i].getDataOperationName() + ", " +
	 * dataActions[i].getDataObject()); } } return(entries); }
	 */

	// ---------------------------------------------------------------------
	/**
	 * Retrieves transaction identifiers for transactions which haven't been
	 * assigned to a batch. The business date and store identifier parameters
	 * are optional.
	 * <P>
	 * 
	 * @param storeID
	 *            store identifier
	 * @param businessDate
	 *            business date (optional)
	 * @return array of tlog transaction entry objects
	 * @exception DataException
	 *                thrown if error occurs
	 **/
	// ---------------------------------------------------------------------
	/*
	 * public POSLogTransactionEntryIfc[] retrieveTransactionsNotInBatch (String
	 * storeID, EYSDate businessDate) throws DataException { // begin
	 * retrieveTransactionsNotInBatch()
	 * 
	 * if (logger.isDebugEnabled()) logger.debug(
	 * "TransactionReadDataTransaction.retrieveTransactionsNotInBatch");
	 * 
	 * POSLogTransactionEntryIfc[] entries =
	 * retrieveTransactionIDsByBatchID(storeID, businessDate,
	 * POSLogTransactionEntryIfc.NO_BATCH_IDENTIFIED);
	 * 
	 * if (logger.isDebugEnabled()) logger.debug(
	 * "TransactionReadDataTransaction.retrieveTransactionsNotInBatch");
	 * 
	 * return(entries); } // end retrieveTransactionsNotInBatch()
	 */
	// ---------------------------------------------------------------------
	/**
	 * Retrieves transaction identifiers for transactions which haven't been
	 * assigned to a batch. The business date and store identifier parameters
	 * are optional.
	 * <P>
	 * 
	 * @param storeID
	 *            store identifier
	 * @return array of tlog transaction entry objects
	 * @exception DataException
	 *                thrown if error occurs
	 **/
	// ---------------------------------------------------------------------
	/*
	 * public POSLogTransactionEntryIfc[] retrieveTransactionsNotInBatch (String
	 * storeID) throws DataException { // begin retrieveTransactionsNotInBatch()
	 * 
	 * if (logger.isDebugEnabled()) logger.debug(
	 * "TransactionReadDataTransaction.retrieveTransactionsNotInBatch");
	 * 
	 * POSLogTransactionEntryIfc[] entries =
	 * retrieveTransactionIDsByBatchID(storeID, null,
	 * POSLogTransactionEntryIfc.NO_BATCH_IDENTIFIED);
	 * 
	 * if (logger.isDebugEnabled()) logger.debug(
	 * "TransactionReadDataTransaction.retrieveTransactionsNotInBatch");
	 * 
	 * return(entries); } // end retrieveTransactionsNotInBatch()
	 */
	// ---------------------------------------------------------------------
	/**
	 * Retrieves transaction identifiers for transactions which haven't been
	 * assigned to a batch. The business date and store identifier parameters
	 * are optional.
	 * <P>
	 * 
	 * @param storeID
	 *            store identifier
	 * @param columnID
	 *            indcates if the TLog or Batch Archive column will be used.
	 * @return array of tlog transaction entry objects
	 * @exception DataException
	 *                thrown if error occurs
	 **/
	// ---------------------------------------------------------------------
	/*
	 * public POSLogTransactionEntryIfc[] retrieveTransactionsNotInBatch (String
	 * storeID, int columnID) throws DataException { // begin
	 * retrieveTransactionsNotInBatch()
	 * 
	 * if (logger.isDebugEnabled()) logger.debug(
	 * "TransactionReadDataTransaction.retrieveTransactionsNotInBatch");
	 * 
	 * POSLogTransactionEntryIfc[] entries =
	 * retrieveTransactionIDsByBatchID(storeID, null,
	 * POSLogTransactionEntryIfc.NO_BATCH_IDENTIFIED, columnID);
	 * 
	 * if (logger.isDebugEnabled()) logger.debug(
	 * "TransactionReadDataTransaction.retrieveTransactionsNotInBatch");
	 * 
	 * return(entries); } // end retrieveTransactionsNotInBatch()
	 */
	// ---------------------------------------------------------------------
	/**
	 * Retrieves transaction identifiers for transactions for a particular
	 * business date. This is used for post-processing testing.
	 * 
	 * @param storeID
	 *            store identifier
	 * @param businessDate
	 *            business date
	 * @return array of tlog transaction entry objects
	 * @exception DataException
	 *                thrown if error occurs
	 **/
	// ---------------------------------------------------------------------
	/*
	 * public POSLogTransactionEntryIfc[] retrieveTransactionIDsByBusinessDate
	 * (String storeID, EYSDate businessDate) throws DataException { // begin
	 * retrieveTransactionIDsByBusinessDate()
	 * 
	 * if (logger.isDebugEnabled()) logger.debug(
	 * "TransactionReadDataTransaction.retrieveTransactionIDsByBusinessDate");
	 * 
	 * // set data actions and execute POSLogTransactionEntryIfc searchKey =
	 * DomainGateway.getFactory().getPOSLogTransactionEntryInstance();
	 * searchKey.setStoreID(storeID); searchKey.setBusinessDate(businessDate);
	 * 
	 * DataActionIfc[] dataActions = new DataActionIfc[1]; dataActions[0] =
	 * createDataAction(searchKey, "RetrieveTransactionIDsByBusinessDate");
	 * setDataActions(dataActions);
	 * 
	 * POSLogTransactionEntryIfc[] entries = (POSLogTransactionEntryIfc[])
	 * getDataManager().execute(this);
	 * 
	 * if (logger.isDebugEnabled()) logger.debug(
	 * "TransactionReadDataTransaction.retrieveTransactionIDsByBusinessDate");
	 * 
	 * return(entries); } // end retrieveTransactionsByBusinessDate()
	 */
	// ---------------------------------------------------------------------
	/**
	 * Gets last training mode transaction sequence number.
	 * 
	 * @return last training mode transaction sequence number
	 * @exception DataException
	 *                upon error
	 **/
	// ---------------------------------------------------------------------
	/*
	 * public int getLastTrainingTransactionSequenceNumber(RegisterIfc register)
	 * throws DataException { if ( logger.isDebugEnabled() ) { logger.debug(
	 * "TransactionReadDataTransaction.getLastTrainingTransactionSequenceNumber"
	 * ); }
	 * 
	 * // set data actions and execute DataActionIfc[] dataActions = new
	 * DataActionIfc[1]; DataAction dataAction = new DataAction();
	 * dataAction.setDataOperationName
	 * ("GetLastTrainingTransactionSequenceNumber");
	 * dataAction.setDataObject(register); dataActions[0] = dataAction;
	 * setDataActions(dataActions);
	 * 
	 * Integer seqNumber = (Integer) getDataManager().execute(this);
	 * 
	 * return(seqNumber.intValue()); }
	 */

	// ---------------------------------------------------------------------
	/**
	 * Instantiates TransactionSummaryIfc object.
	 * <P>
	 * 
	 * @return TransactionSummaryIfc object
	 **/

	// ---------------------------------------------------------------------
	/*
	 * public TransactionSummaryIfc instantiateTransactionSummaryIfc() {
	 * return(DomainGateway.getFactory().getTransactionSummaryInstance()); }
	 */
	// ---------------------------------------------------------------------
	/**
	 * Instantiates StoreIfc object.
	 * <P>
	 * 
	 * @return StoreIfc object
	 **/
	// ---------------------------------------------------------------------
	/*
	 * public StoreIfc instantiateStoreIfc() {
	 * return(DomainGateway.getFactory().getStoreInstance()); }
	 * 
	 * //---------------------------------------------------------------------
	 * /** Instantiates TransactionIfc object. <P>
	 * 
	 * @param transactionID The transaction ID to initialize
	 * 
	 * @return TransactionIfc object
	 */
	// ---------------------------------------------------------------------
	/*
	 * public TransactionIfc instantiateTransaction(String transactionID) {
	 * TransactionIfc transaction =
	 * DomainGateway.getFactory().getTransactionInstance();
	 * transaction.initialize(transactionID); return(transaction); }
	 * 
	 * //---------------------------------------------------------------------
	 * /** Returns the revision number of this class. <p>
	 * 
	 * @return String representation of revision number
	 */
	// ---------------------------------------------------------------------
	public String getRevisionNumber() {
		return (Util.parseRevisionNumber(revisionNumber));
	}

	// ---------------------------------------------------------------------
	/**
	 * Method to default display string function.
	 * <p>
	 * 
	 * @return String representation of object
	 **/
	// ---------------------------------------------------------------------
	public String toString() {
		StringBuilder strResult = Util.classToStringHeader(getClass().getName(), getRevisionNumber(), hashCode());
		return (strResult.toString());
	}

	public String getHCPromotionComaprisonValue(PLUItemIfc pluitem, int comparisonBasis) {

		StringBuffer value = new StringBuffer("");
		// set data actions and execute
		DataActionIfc[] dataActions = new DataActionIfc[1];
		DataAction da = new DataAction();
		da.setDataOperationName("GetPromotionComparisonValue");

		HashMap map = new HashMap();
		map.put(MAXDiscountRuleConstantsIfc.PLUITEM_ID, pluitem);
		map.put(MAXDiscountRuleConstantsIfc.COMPARISION_BASIS, String.valueOf(comparisonBasis));

		da.setDataObject(map);
		dataActions[0] = da;
		setDataActions(dataActions);

		try {
			value = (StringBuffer) getDataManager().execute(this);

		} catch (DataException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return value.toString();

	}

}
