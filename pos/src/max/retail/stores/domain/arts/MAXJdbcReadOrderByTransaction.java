/* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
  Copyright (c) 2012-2013 MAXHyperMarket, Inc.    All Rights Reserved.
  Rev 1.1	13/Aug/2013	  	Prateek, Changes done for Special Order CR - Suggested Tender Type
  Rev 1.0	1/May/2013	  	Tanmaya, Home Delivery Special Order
* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
package max.retail.stores.domain.arts;

// java imports
import java.util.Iterator;

import max.retail.stores.domain.order.MAXOrderIfc;
import max.retail.stores.domain.transaction.MAXOrderTransactionIfc;
import oracle.retail.stores.domain.DomainGateway;
import oracle.retail.stores.domain.lineitem.ItemContainerProxyIfc;
import oracle.retail.stores.domain.lineitem.SaleReturnLineItemIfc;
import oracle.retail.stores.domain.order.OrderConstantsIfc;
import oracle.retail.stores.domain.order.OrderIfc;
import oracle.retail.stores.domain.order.OrderStatusIfc;
import oracle.retail.stores.domain.transaction.OrderTransactionIfc;
import oracle.retail.stores.domain.transaction.TransactionIfc;
import oracle.retail.stores.foundation.manager.data.DataException;
import oracle.retail.stores.foundation.manager.data.JdbcDataConnection;
import oracle.retail.stores.foundation.manager.ifc.data.DataActionIfc;
import oracle.retail.stores.foundation.manager.ifc.data.DataConnectionIfc;
import oracle.retail.stores.foundation.manager.ifc.data.DataTransactionIfc;
import oracle.retail.stores.foundation.utility.Util;

import org.apache.log4j.Logger;

//--------------------------------------------------------------------------
/**
 * This operation reads order data from the transaction tables. It expects that
 * an OrderStatusIfc object is already in the result object.
 * <P>
 * <P>
 * 
 * @version $Revision: 5$
 **/
// --------------------------------------------------------------------------
public class MAXJdbcReadOrderByTransaction extends MAXJdbcReadTransaction {
	/**
	 * 
	 */
	private static final long serialVersionUID = 2897511306662930972L;

	/**
	 * The logger to which log messages will be sent.
	 **/
	private static Logger logger = Logger.getLogger(max.retail.stores.domain.arts.MAXJdbcReadOrderByTransaction.class);

	/**
	 * The performance logger
	 **/
	protected static final Logger perf = Logger.getLogger("PERF." + MAXJdbcReadOrderByTransaction.class.getName());

	// ----------------------------------------------------------------------
	/**
	 * Class constructor.
	 **/
	// ----------------------------------------------------------------------
	public MAXJdbcReadOrderByTransaction() {
		super();
		setName("JdbcReadOrderByTransaction");
	}

	// ----------------------------------------------------------------------
	/**
	 * Executes the SQL statements against the database.
	 * <P>
	 * 
	 * @param dataTransaction
	 *            The data transaction
	 * @param dataConnection
	 *            The connection to the data source
	 * @param action
	 *            The information passed by the valet
	 * @exception DataException
	 *                upon error
	 **/
	// ----------------------------------------------------------------------
	public void execute(DataTransactionIfc dataTransaction, DataConnectionIfc dataConnection, DataActionIfc action)
			throws DataException {
		if (logger.isDebugEnabled())
			logger.debug("JdbcReadOrderByTransaction.execute");
		if (perf.isDebugEnabled()) {
			perf.debug("Entering JdbcReadOrderByTransaction.execute");
		}
		JdbcDataConnection connection = (JdbcDataConnection) dataConnection;

		try {
			Object resultObject = dataTransaction.getResult();
			if (resultObject != null) {
				// get order status as seed
				OrderStatusIfc orderStatus = (OrderStatusIfc) resultObject;
				// use recording transaction to set key for transaction read
				TransactionIfc recordingTransaction = DomainGateway.getFactory().getTransactionInstance();
				recordingTransaction.initialize(orderStatus.getRecordingTransactionID());
				recordingTransaction.setBusinessDay(orderStatus.getRecordingTransactionBusinessDate());
				if (orderStatus.getLocale() != null) {
					((OrderStatusIfc) recordingTransaction).setLocale(orderStatus.getLocale());
				}

				// read transaction
				TransactionIfc transaction = selectTransaction(connection, recordingTransaction,
						orderStatus.getOrderID(), recordingTransaction.getLocaleRequestor());

				OrderTransactionIfc orderTransaction = (OrderTransactionIfc) transaction;
				// load transaction data into order object
				OrderIfc order = DomainGateway.getFactory().getOrderInstance();
				// fix for bug 6615
				((MAXOrderIfc) order).setOriginalTransaction(transaction);
				// end fix
				order.setCustomer(orderTransaction.getCustomer());
				ItemContainerProxyIfc proxy = orderTransaction.getItemContainerProxy();
				if (orderStatus.getInitiatingChannel() == OrderConstantsIfc.ORDER_CHANNEL_WEB) {
					removeAdvancedPricingDiscounts(proxy.getLineItemsIterator());
					proxy.clearAdvancedPricingRules();
					orderTransaction.calculateBestDeal();
				}
				order.setItemContainerProxy(orderTransaction.getItemContainerProxy());
				order.setStatus(orderStatus);
				order.setTotals(orderTransaction.getTransactionTotals());

				if (orderTransaction instanceof MAXOrderTransactionIfc && order instanceof MAXOrderIfc) {
					((MAXOrderIfc) order).setExpectedDeliveryDate(
							((MAXOrderTransactionIfc) orderTransaction).getExpectedDeliveryDate());
					((MAXOrderIfc) order).setExpectedDeliveryTime(
							((MAXOrderTransactionIfc) orderTransaction).getExpectedDeliveryTime());
					((MAXOrderIfc) order)
							.setSuggestedTender(((MAXOrderTransactionIfc) orderTransaction).getSuggestedTender());
				}
				if (orderTransaction.getPaymentHistoryInfoCollection() != null
						&& orderTransaction.getPaymentHistoryInfoCollection().size() > 0) {
					order.getPaymentHistoryInfoCollection().addAll(orderTransaction.getPaymentHistoryInfoCollection());
				}
				dataTransaction.setResult(order);
			}
		} catch (Exception e) {
			logger.error("" + Util.throwableToString(e) + "");
			throw new DataException(DataException.UNKNOWN, "read order transaction");
		}

		if (perf.isDebugEnabled()) {
			perf.debug("Exiting JdbcReadOrderByTransaction.execute");
		}
		if (logger.isDebugEnabled())
			logger.debug("JdbcReadOrderByTransaction.execute");
	}

	// ---------------------------------------------------------------------
	/**
	 * Removes advanced pricing discounts currently applied to the transaction
	 * regardless of their participation in a best deal group.
	 **/
	// ---------------------------------------------------------------------
	public void removeAdvancedPricingDiscounts(Iterator i) {
		while (i.hasNext()) {
			((SaleReturnLineItemIfc) i.next()).removeAdvancedPricingDiscount();
		}
	}
}
