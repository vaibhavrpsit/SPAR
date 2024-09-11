/* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
  Copyright (c) 2012-2013 MAXHyperMarket, Inc.    All Rights Reserved.
  Rev 1.1	13/Aug/2013	  	Prateek, Change done for Special Order CR - Suggested Tender Type
  Rev 1.0	1/May/2013	  	Tanmaya, Home Delivery Special Order
* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
package max.retail.stores.domain.arts;

import max.retail.stores.domain.transaction.MAXOrderTransactionIfc;
import max.retail.stores.persistence.utility.MAXARTSDatabaseIfc;
import oracle.retail.stores.common.sql.SQLUpdatableStatementIfc;
import oracle.retail.stores.domain.arts.JdbcSaveOrderByTransaction;
import oracle.retail.stores.domain.transaction.OrderTransactionIfc;
import oracle.retail.stores.persistence.utility.ARTSDatabaseIfc;

public abstract class MAXJdbcSaveOrderByTransaction extends JdbcSaveOrderByTransaction {

	private static final long serialVersionUID = 1494706423644314666L;

	protected void addInsertColumns(SQLUpdatableStatementIfc sql, OrderTransactionIfc orderTransaction) {
		// add insert columns
		addUpdateColumns(sql, orderTransaction);

		// add field for order ID
		sql.addColumn(ARTSDatabaseIfc.FIELD_ORDER_ID, inQuotes(orderTransaction.getOrderID()));
		sql.addColumn(ARTSDatabaseIfc.FIELD_ORDER_TOTAL,
				orderTransaction.getTransactionTotals().getGrandTotal().toString());
		sql.addColumn(ARTSDatabaseIfc.FIELD_ORDER_LOCATION, inQuotes(orderTransaction.getOrderStatus().getLocation()));
		sql.addColumn(ARTSDatabaseIfc.FIELD_RECORD_CREATION_TIMESTAMP, getSQLCurrentTimestampFunction());
		sql.addColumn(ARTSDatabaseIfc.FIELD_ORDER_CREATE_TIMESTAMP,
				dateToSQLTimestampFunction(orderTransaction.getOrderStatus().getTimestampCreated()));
		sql.addColumn(ARTSDatabaseIfc.FIELD_ORDER_BEGIN_TIMESTAMP,
				dateToSQLTimestampFunction(orderTransaction.getOrderStatus().getTimestampBegin()));

		// fix for bug 7278
		if (orderTransaction instanceof MAXOrderTransactionIfc) {
			if (((MAXOrderTransactionIfc) orderTransaction).getExpectedDeliveryDate() != null)
				sql.addColumn(MAXARTSDatabaseIfc.FIELD_EXPECTED_ORDER_DELIVERY_DATE, dateToSQLTimestampFunction(
						((MAXOrderTransactionIfc) orderTransaction).getExpectedDeliveryDate()));

			if (((MAXOrderTransactionIfc) orderTransaction).getExpectedDeliveryTime() != null)
				sql.addColumn(MAXARTSDatabaseIfc.FIELD_EXPECTED_ORDER_DELIVERY_TIME, makeSafeString(
						((MAXOrderTransactionIfc) orderTransaction).getExpectedDeliveryTime().toString()));
			/** MAX Rev 1.1 Change : start **/
			if (((MAXOrderTransactionIfc) orderTransaction).getSuggestedTender() != null)
				sql.addColumn(MAXARTSDatabaseIfc.FIELD_SUGGESTED_TENDER_SPL_ORD,
						makeSafeString(((MAXOrderTransactionIfc) orderTransaction).getSuggestedTender()));
			/** MAX Rev 1.1 Change : End **/
		}
		// end fix for bug 7278
	}

}
