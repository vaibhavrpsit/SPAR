/* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
  Copyright (c) 2012-2013 MAXHyperMarket, Inc.    All Rights Reserved.
  Rev 1.1	28/May/2013	  	Tanmaya, Bug 6038 - POS Going offline select printing of Picklist from Special order.
  Rev 1.0	1/May/2013	  	Tanmaya, Home Delivery Special Order
 * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
package max.retail.stores.domain.arts;

// java imports
import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Iterator;

import max.retail.stores.domain.lineitem.MAXOrderLineItemIfc;
import max.retail.stores.domain.order.MAXOrderIfc;
import max.retail.stores.domain.stock.MAXPLUItemIfc;
import max.retail.stores.persistence.utility.MAXARTSDatabaseIfc;
import oracle.retail.stores.commerceservices.common.currency.CurrencyIfc;
import oracle.retail.stores.common.sql.SQLDeleteStatement;
import oracle.retail.stores.common.sql.SQLInsertStatement;
import oracle.retail.stores.common.sql.SQLSelectStatement;
import oracle.retail.stores.common.sql.SQLUpdateStatement;
import oracle.retail.stores.common.utility.LocaleMap;
import oracle.retail.stores.domain.arts.JdbcDataOperation;
import oracle.retail.stores.domain.discount.DiscountRuleConstantsIfc;
import oracle.retail.stores.domain.discount.ItemDiscountByAmountIfc;
import oracle.retail.stores.domain.discount.ItemDiscountByPercentageIfc;
import oracle.retail.stores.domain.discount.ItemDiscountStrategyIfc;
import oracle.retail.stores.domain.lineitem.AbstractTransactionLineItemIfc;
import oracle.retail.stores.domain.lineitem.ItemPriceIfc;
import oracle.retail.stores.domain.lineitem.KitComponentLineItemIfc;
import oracle.retail.stores.domain.lineitem.OrderItemStatusIfc;
import oracle.retail.stores.domain.lineitem.SaleReturnLineItemIfc;
import oracle.retail.stores.domain.order.OrderIfc;
import oracle.retail.stores.domain.order.OrderStatusIfc;
import oracle.retail.stores.domain.stock.ItemKitConstantsIfc;
import oracle.retail.stores.domain.transaction.TransactionIDIfc;
import oracle.retail.stores.domain.utility.EYSDate;
import oracle.retail.stores.foundation.manager.data.DataException;
import oracle.retail.stores.foundation.manager.data.JdbcDataConnection;
import oracle.retail.stores.foundation.manager.ifc.data.DataActionIfc;
import oracle.retail.stores.foundation.manager.ifc.data.DataConnectionIfc;
import oracle.retail.stores.foundation.manager.ifc.data.DataTransactionIfc;
import oracle.retail.stores.foundation.utility.Util;
import oracle.retail.stores.persistence.utility.ARTSDatabaseIfc;

import org.apache.log4j.Logger;

//--------------------------------------------------------------------------
/**
 * This operation updates the order, its order line item stati, and location
 * based upon the order. ID.
 * <P>
 * 
 * @version $Revision: 10$
 **/
// --------------------------------------------------------------------------
public class MAXJdbcInsertOrder extends JdbcDataOperation implements MAXARTSDatabaseIfc {
	/**
	 * 
	 */
	private static final long serialVersionUID = 6910556627238856400L;
	/**
	 * The logger to which log messages will be sent.
	 **/
	private static Logger logger = Logger.getLogger(max.retail.stores.domain.arts.MAXJdbcInsertOrder.class);

	// ----------------------------------------------------------------------
	/**
	 * Class constructor.
	 **/
	// ----------------------------------------------------------------------
	public MAXJdbcInsertOrder() {
		super();
		setName("MAXJdbcInsertOrder");
	}

	// ----------------------------------------------------------------------
	/**
	 * Executes the SQL statements against the database.
	 * <P>
	 * 
	 * @param dataTransaction
	 * @param dataConnection
	 * @param action
	 **/
	// ----------------------------------------------------------------------
	public void execute(DataTransactionIfc dataTransaction, DataConnectionIfc dataConnection, DataActionIfc action)
			throws DataException {
		if (logger.isDebugEnabled())
			logger.debug("MAXJdbcInsertOrder.execute");

		// set data connection
		JdbcDataConnection connection = (JdbcDataConnection) dataConnection;

		// grab arguments
		OrderIfc order = (OrderIfc) action.getDataObject();
		updateOrder(connection, order);

		if (logger.isDebugEnabled())
			logger.debug("MAXJdbcInsertOrder.execute");
	}

	// ----------------------------------------------------------------------
	/**
	 * Executes the SQL statements against the database.
	 * <P>
	 * 
	 * @param dataConnection
	 * @param order
	 *            object stati and location to update and enddate
	 * @exception DataException
	 *                upon error
	 **/
	// ----------------------------------------------------------------------
	public void updateOrder(JdbcDataConnection connection, OrderIfc order) throws DataException {
		if (logger.isDebugEnabled())
			logger.debug("MAXJdbcUpdateOrder.updateOrder()");

		SQLUpdateStatement sql = new SQLUpdateStatement();

		// calculate order total - do not include canceled items
		BigDecimal orderTotal = BigDecimal.ZERO;

		for (int i = 0; i < order.getOrderLineItems().length; i++) {
			if (((SaleReturnLineItemIfc) order.retrieveItemByIndex(i)).getOrderItemStatus().getStatus()
					.getStatus() != MAXOrderLineItemIfc.ORDER_ITEM_STATUS_CANCELED) {
				orderTotal = orderTotal.add(
						new BigDecimal(order.retrieveItemByIndex(i).getLineItemAmount().getDecimalValue().toString()));// toDecimalFormattedString()));
			}
		}

		// set table
		sql.setTable(TABLE_ORDER);

		// add columns and values
		sql.addColumn(FIELD_ORDER_STATUS, order.getOrderStatus());
		sql.addColumn(FIELD_ORDER_STATUS_PREVIOUS, order.getPreviousOrderStatus());
		sql.addColumn(FIELD_ORDER_STATUS_CHANGE, dateToSQLDateString(order.getLastStatusChange()));
		sql.addColumn(FIELD_ORDER_TOTAL, orderTotal.toString());
		sql.addColumn(ARTSDatabaseIfc.FIELD_RECORD_LAST_MODIFIED_TIMESTAMP, getSQLCurrentTimestampFunction());

		// add location if not empty - length > 0
		if (order.getStatus().getLocation().length() > 0) {
			sql.addColumn(FIELD_ORDER_LOCATION, "'" + order.getStatus().getLocation() + "'");
		}

		// add qualifiers for the order ID
		sql.addQualifier(FIELD_ORDER_ID + " = '" + order.getOrderID() + "'");

		try {
			connection.execute(sql.getSQLString());
		}

		catch (DataException de) {
			logger.warn("" + de + "");
			if (de.getErrorCode() == DataException.UNKNOWN) {
				throw new DataException(DataException.CONNECTION_ERROR, "Connection lost");
			} else {
				throw de;
			}
		}

		if (0 >= connection.getUpdateCount()) {
			logger.warn("No orders updated ");
			throw new DataException(DataException.NO_DATA, "No orders updated ");
		}

		// update the order line item price modifiers
		AbstractTransactionLineItemIfc[] orderItems = order.getLineItems();
		saveOrderLineItems(connection, order);

		deleteExistingLineItems(connection, order.getOrderID());
		for (int i = 0; i < orderItems.length; i++) {
			try {
				insertLineItem(connection, order, (SaleReturnLineItemIfc) orderItems[i], order.getOrderID());
				updatePriceModifier(connection, (SaleReturnLineItemIfc) orderItems[i], order.getOrderID());
			} catch (DataException de) {
				logger.warn("" + de + "");
				if (de.getErrorCode() == DataException.UNKNOWN) {
					throw new DataException(DataException.CONNECTION_ERROR, "Connection lost");
				} else {
					throw de;
				}
			}
		}
		if (logger.isDebugEnabled())
			logger.debug("JdbcOrderWriteDataTransaction.updateOrder()");

	} // end updateOrder()

	private void deleteExistingLineItems(JdbcDataConnection connection, String orderID) throws DataException {

		SQLDeleteStatement sql = new SQLDeleteStatement();
		sql.setTable(TABLE_ORDER_ITEM);
		sql.addQualifier(FIELD_ORDER_ID + " = '" + orderID + "'");
		try {
			connection.execute(sql.getSQLString());
		}

		catch (DataException de) {
			logger.warn("" + de + "");
			if (de.getErrorCode() == DataException.UNKNOWN) {
				throw new DataException(DataException.CONNECTION_ERROR, "Connection lost");
			} else {
				throw de;
			}
		}

		if (0 >= connection.getUpdateCount()) {
			logger.warn("No order line item updated ");
			throw new DataException(DataException.NO_DATA, "No order line item updated ");
		}

	}

	// ----------------------------------------------------------------------
	/**
	 * Executes the SQL statements against the database.
	 * <P>
	 * 
	 * @param dataConnection
	 * @param order
	 *            object stati and location to update and enddate
	 * @exception DataException
	 *                upon error
	 * @deprecated As of release 5.0.0, replaced by {@link #updateOrder()}
	 **/
	// ----------------------------------------------------------------------
	public void UpdateOrder(JdbcDataConnection connection, OrderIfc order) throws DataException {
		updateOrder(connection, order);
	}

	// ----------------------------------------------------------------------
	/**
	 * Updates order line items in the database.
	 * <P>
	 * 
	 * @param dataConnection
	 * @param orderItem
	 *            order item
	 * @exception DataException
	 *                upon error
	 **/
	// ----------------------------------------------------------------------
	public void insertLineItem(JdbcDataConnection connection, OrderIfc order, SaleReturnLineItemIfc orderItem,
			String orderID) throws DataException {
		// pull out order status
		OrderStatusIfc orderStatus = order.getStatus();
		OrderItemStatusIfc itemStatus = orderItem.getOrderItemStatus();

		// set up SQL
		SQLInsertStatement sql = new SQLInsertStatement();
		sql.setTable(TABLE_ORDER_ITEM);

		sql.addColumn(FIELD_ORDER_ID, inQuotes(orderID));
		sql.addColumn(FIELD_BUSINESS_DAY_DATE, dateToSQLDateString(orderStatus.getTimestampBegin().dateValue()));
		sql.addColumn(FIELD_ITEM_STATUS, Integer.toString(itemStatus.getStatus().getStatus()));
		sql.addColumn(FIELD_ITEM_STATUS_PREVIOUS, Integer.toString(itemStatus.getStatus().getPreviousStatus()));
		sql.addColumn(FIELD_ORDER_LINE_REFERENCE, orderItem.getOrderLineReference());
		sql.addColumn(FIELD_PARTY_ID, "0");
		sql.addColumn(FIELD_POS_ITEM_ID, inQuotes(orderItem.getItemID()));
		sql.addColumn(FIELD_POS_DEPARTMENT_ID, inQuotes(orderItem.getPLUItem().getDepartmentID()));
		sql.addColumn(FIELD_TAX_GROUP_ID, Integer.toString(orderItem.getTaxGroupID()));
		sql.addColumn(FIELD_SALE_RETURN_LINE_ITEM_QUANTITY, orderItem.getItemQuantity().toString());
		sql.addColumn(FIELD_SALE_RETURN_LINE_ITEM_EXTENDED_AMOUNT,
				orderItem.getItemPrice().getExtendedSellingPrice().getStringValue());
		sql.addColumn(FIELD_SALE_RETURN_LINE_ITEM_VAT_AMOUNT, orderItem.getItemTaxAmount().getStringValue());
		sql.addColumn(FIELD_SALE_RETURN_LINE_ITEM_TAX_INC_AMOUNT,
				orderItem.getItemInclusiveTaxAmount().getStringValue());
		sql.addColumn(FIELD_RETAIL_TRANSACTION_LINE_ITEM_SEQUENCE_NUMBER, Integer.toString(orderItem.getLineNumber()));
		sql.addColumn(FIELD_ITEM_DESCRIPTION,
				inQuotes(orderItem.getPLUItem().getDescription(LocaleMap.getLocale(LocaleMap.DEFAULT))));
		if (itemStatus.getStatus().getLastStatusChange() != null) {
			sql.addColumn(FIELD_ORDER_STATUS_CHANGE, dateToSQLDateString(itemStatus.getStatus().getLastStatusChange()));
		}
		/* Max Rev 1.1 changes start */
		else {
			sql.addColumn(FIELD_ORDER_STATUS_CHANGE, dateToSQLDateString(new EYSDate()));
		}
		/* Max Rev 1.1 changes end */
		sql.addColumn(FIELD_ITEM_QUANTITY_PICKED, itemStatus.getQuantityPicked().toString());
		sql.addColumn(FIELD_ITEM_QUANTITY_SHIPPED, itemStatus.getQuantityShipped().toString());
		sql.addColumn(FIELD_ORDER_DEPOSIT_AMOUNT, itemStatus.getDepositAmount().toString());
		sql.addColumn(FIELD_TRANSACTION_SEQUENCE_NUMBER,
				String.valueOf(orderStatus.getInitialTransactionID().getSequenceNumber()));

		sql.addColumn(FIELD_RETAIL_STORE_ID, orderStatus.getInitialTransactionID().getStoreID());

		sql.addColumn(FIELD_WORKSTATION_ID, orderStatus.getInitialTransactionID().getWorkstationID());

		try {
			connection.execute(sql.getSQLString());
		} catch (DataException de) {
			logger.error("" + de + "");
			throw de;
		} catch (Exception e) {
			logger.error("" + Util.throwableToString(e) + "");

			throw new DataException(DataException.UNKNOWN, "insertOrderLineItem", e);
		}
	}

	// ----------------------------------------------------------------------
	/**
	 * Updates price modifier items in the database.
	 * <P>
	 * 
	 * @param dataConnection
	 * @param orderItem
	 *            order item
	 * @exception DataException
	 *                upon error
	 **/
	// ----------------------------------------------------------------------
	public void updatePriceModifier(JdbcDataConnection connection, SaleReturnLineItemIfc orderItem, String orderID)
			throws DataException {
		ItemDiscountStrategyIfc[] modifiers = orderItem.getItemPrice().getItemDiscounts();
		ItemDiscountStrategyIfc discountLineItem;
		int discountSequenceNumber = 0;
		boolean insertFailed = false;

		// get number of discounts for loop
		int numDiscounts = 0;
		if (modifiers != null) {
			numDiscounts = modifiers.length;
		}
		/*
		 * Loop through each line item. Continue through them all even if one
		 * has failed.
		 */
		for (int i = 0; i < numDiscounts; i++) {
			discountLineItem = modifiers[i];
			/*
			 * Skip the Transaction level discounts because they are handled by
			 * the Discount Line Item entity
			 */
			if (discountLineItem.getDiscountScope() != DiscountRuleConstantsIfc.DISCOUNT_SCOPE_TRANSACTION) {
				SQLInsertStatement sqlInsertStatement = new SQLInsertStatement();

				// set table
				sqlInsertStatement.setTable(TABLE_ORDER_LINE_ITEM_RETAIL_PRICE_MODIFIER);

				// set columns
				sqlInsertStatement.addColumn(FIELD_ORDER_ID, makeSafeString(orderID));
				try {
					Integer orderReference = new Integer(orderItem.getOrderItemStatus().getReference());
					sqlInsertStatement.addColumn(FIELD_ORDER_LINE_REFERENCE, orderReference.intValue());
				} catch (NumberFormatException ne) {
					logger.error(ne);
					throw new DataException(DataException.DATA_FORMAT,
							"NumberFormatException with order reference number");
				}
				sqlInsertStatement.addColumn(FIELD_RETAIL_PRICE_MODIFIER_SEQUENCE_NUMBER, discountSequenceNumber);
				sqlInsertStatement.addColumn(FIELD_RETAIL_PRICE_MODIFIER_REASON_CODE,
						makeSafeString(Integer.toString(discountLineItem.getReasonCode())));
				sqlInsertStatement.addColumn(FIELD_RETAIL_PRICE_MODIFIER_PERCENT,
						getItemDiscountPercent(discountLineItem));
				sqlInsertStatement.addColumn(FIELD_RETAIL_PRICE_MODIFIER_AMOUNT,
						getItemDiscountAmount(discountLineItem));

				try {
					connection.execute(sqlInsertStatement.getSQLString());
				} catch (DataException de) {
					logger.warn("" + de + "");
					insertFailed = true;
				}

				// try update if insert failed
				if (insertFailed == true) {
					SQLUpdateStatement sqlUpdateStatement = new SQLUpdateStatement();

					// set table
					sqlUpdateStatement.setTable(TABLE_ORDER_LINE_ITEM_RETAIL_PRICE_MODIFIER);

					// set columns
					sqlUpdateStatement.addColumn(FIELD_RETAIL_PRICE_MODIFIER_REASON_CODE,
							makeSafeString(Integer.toString(discountLineItem.getReasonCode())));

					sqlUpdateStatement.addColumn(FIELD_RETAIL_PRICE_MODIFIER_PERCENT,
							getItemDiscountPercent(discountLineItem));
					sqlUpdateStatement.addColumn(FIELD_RETAIL_PRICE_MODIFIER_AMOUNT,
							getItemDiscountAmount(discountLineItem));

					// add qualifiers
					sqlUpdateStatement.addQualifier(FIELD_ORDER_ID, makeSafeString(orderID));
					sqlUpdateStatement.addQualifier(FIELD_RETAIL_PRICE_MODIFIER_SEQUENCE_NUMBER,
							Integer.toString(discountSequenceNumber));
					sqlUpdateStatement.addQualifier(FIELD_ORDER_LINE_REFERENCE,
							orderItem.getOrderItemStatus().getReference());

					try {
						connection.execute(sqlUpdateStatement.getSQLString());
						insertFailed = false;
					} catch (DataException de) {
						logger.warn("" + de + "");
						if (de.getErrorCode() == DataException.UNKNOWN) {
							throw new DataException(DataException.CONNECTION_ERROR, "Connection lost");
						} else {
							throw de;
						}
					}

					// hosed if update fails
					if (0 >= connection.getUpdateCount()) {
						throw new DataException(DataException.NO_DATA,
								" unable to update order line item price modifier table");
					}
				}
			}
			++discountSequenceNumber;
		}
	}

	// ----------------------------------------------------------------------
	/**
	 * Updates price modifier items in the database.
	 * <P>
	 * 
	 * @param dataConnection
	 * @param orderItem
	 *            order item
	 * @exception DataException
	 *                upon error
	 * @deprecated As of release 5.0.0, replaced by
	 *             {@link #updatePriceModifier()}
	 **/
	// ----------------------------------------------------------------------
	public void UpdatePriceModifier(JdbcDataConnection connection, SaleReturnLineItemIfc orderItem, String orderID)
			throws DataException {
		updatePriceModifier(connection, orderItem, orderID);
	}

	// ---------------------------------------------------------------------
	/**
	 * Calculate discount percent for a discount line item.
	 * <p>
	 * 
	 * @param discount
	 *            line item
	 * @return discount percent
	 **/
	// ---------------------------------------------------------------------
	public String getItemDiscountPercent(ItemDiscountStrategyIfc lineItem) {
		BigDecimal percent = Util.I_BIG_DECIMAL_ZERO;

		switch (lineItem.getDiscountMethod()) {
		case DiscountRuleConstantsIfc.DISCOUNT_METHOD_PERCENTAGE:
			ItemDiscountByPercentageIfc discount = (ItemDiscountByPercentageIfc) lineItem;
			percent = discount.getDiscountRate();
			break;

		case DiscountRuleConstantsIfc.DISCOUNT_METHOD_AMOUNT:
		default:
			break;
		}

		return (percent.toString());
	}

	// ---------------------------------------------------------------------
	/**
	 * Calculate discount amount for a discount line item.
	 * <p>
	 * 
	 * @param discount
	 *            line item
	 * @return discount amount
	 **/
	// ---------------------------------------------------------------------
	public String getItemDiscountAmount(ItemDiscountStrategyIfc lineItem) {
		String amount = "0";

		switch (lineItem.getDiscountMethod()) {
		case DiscountRuleConstantsIfc.DISCOUNT_METHOD_AMOUNT:
			ItemDiscountByAmountIfc discount = (ItemDiscountByAmountIfc) lineItem;
			amount = discount.getDiscountAmount().getDecimalValue().toString();// toDecimalFormattedString();
			break;

		case DiscountRuleConstantsIfc.DISCOUNT_METHOD_PERCENTAGE:
		default:
			amount = "0";
			break;
		}

		return (amount);
	}

	public void insertOrderLineItem(JdbcDataConnection dataConnection, OrderIfc order, SaleReturnLineItemIfc lineItem,
			int lineItemSequenceNumber) throws DataException { // begin
																// insertOrderLineItem()
																// pull out
																// order status
		OrderStatusIfc orderStatus = order.getStatus();
		OrderItemStatusIfc itemStatus = lineItem.getOrderItemStatus();
		insertRetailTransactionLineItem(dataConnection, orderStatus, lineItem.getLineNumber(), "SR");

		SQLInsertStatement sql = new SQLInsertStatement();

		// Table
		sql.setTable(TABLE_SALE_RETURN_LINE_ITEM);

		// Fields
		sql.addColumn(FIELD_RETAIL_STORE_ID, "'" + orderStatus.getInitialTransactionID().getStoreID() + "'");
		sql.addColumn(FIELD_WORKSTATION_ID, orderStatus.getInitialTransactionID().getWorkstationID());
		sql.addColumn(FIELD_TRANSACTION_SEQUENCE_NUMBER,
				String.valueOf(orderStatus.getInitialTransactionID().getSequenceNumber()));
		sql.addColumn(FIELD_BUSINESS_DAY_DATE, dateToSQLDateString(orderStatus.getRecordingTransactionBusinessDate()));
		sql.addColumn(FIELD_RETAIL_TRANSACTION_LINE_ITEM_SEQUENCE_NUMBER, Integer.toString(lineItem.getLineNumber()));
		sql.addColumn(FIELD_GIFT_REGISTRY_ID, getGiftRegistryString(lineItem));
		sql.addColumn(FIELD_ITEM_ID, "'" + lineItem.getPLUItem().getItemID() + "'");
		sql.addColumn(FIELD_POS_ITEM_ID, inQuotes(lineItem.getPosItemID()));
		sql.addColumn(FIELD_SERIAL_NUMBER, getItemSerial(lineItem));
		sql.addColumn(FIELD_TAX_GROUP_ID, getTaxGroupID(lineItem));
		sql.addColumn(FIELD_SALE_RETURN_LINE_ITEM_QUANTITY, getItemQuantity(lineItem));
		sql.addColumn(FIELD_SALE_RETURN_LINE_ITEM_EXTENDED_AMOUNT, getItemExtendedAmount(lineItem));
		sql.addColumn(FIELD_SALE_RETURN_LINE_ITEM_EXTENDED_DISCOUNTED_AMOUNT,
				getItemExtendedDiscountedAmount(lineItem));
		sql.addColumn(FIELD_SALE_RETURN_LINE_ITEM_VAT_AMOUNT, lineItem.getItemTaxAmount().getStringValue());
		sql.addColumn(FIELD_SALE_RETURN_LINE_ITEM_TAX_INC_AMOUNT,
				lineItem.getItemInclusiveTaxAmount().getStringValue());
		sql.addColumn(FIELD_SEND_LABEL_COUNT, getSendLabelCount(lineItem));
		sql.addColumn(FIELD_MERCHANDISE_RETURN_FLAG, getReturnFlag(lineItem));
		sql.addColumn(FIELD_MERCHANDISE_RETURN_REASON_CODE, getReturnReasonCode(lineItem));
		sql.addColumn(FIELD_POS_ORIGINAL_TRANSACTION_ID, getOriginalTransactionId(lineItem));
		sql.addColumn(FIELD_ORIGINAL_BUSINESS_DAY_DATE, getOriginalDate(lineItem));
		sql.addColumn(FIELD_ORIGINAL_RETAIL_TRANSACTION_LINE_ITEM_SEQUENCE_NUMBER, getOriginalLineNumber(lineItem));
		sql.addColumn(FIELD_ORIGINAL_RETAIL_STORE_ID, getOriginalStoreID(lineItem));
		sql.addColumn(FIELD_POS_DEPARTMENT_ID, getDepartmentID(lineItem));
		sql.addColumn(FIELD_SEND_FLAG, getSendFlag(lineItem));
		sql.addColumn(FIELD_RECORD_LAST_MODIFIED_TIMESTAMP, getSQLCurrentTimestampFunction());
		sql.addColumn(FIELD_RECORD_CREATION_TIMESTAMP, getSQLCurrentTimestampFunction());
		sql.addColumn(FIELD_GIFT_RECEIPT_FLAG, getGiftReceiptFlag(lineItem));
		sql.addColumn(FIELD_ORDER_REFERENCE_ID, lineItem.getOrderLineReference());
		sql.addColumn(FIELD_ITEM_ID_ENTRY_METHOD_CODE, inQuotes(lineItem.getEntryMethod().toString()));
		sql.addColumn(FIELD_SIZE_CODE, getItemSizeCode(lineItem));

		sql.addColumn(FIELD_RETURN_RELATED_ITEM_FLAG, getReturnRelatedItemFlag(lineItem));
		sql.addColumn(FIELD_RELATED_ITEM_TRANSACTION_LINE_ITEM_SEQ_NUMBER, getRelatedSeqNum(lineItem));
		sql.addColumn(FIELD_REMOVE_RELATED_ITEM_FLAG, getRemoveRelatedItemFlag(lineItem));
		sql.addColumn(FIELD_RETAIL_TRANSACTION_LINE_ITEM_SALES_ASSC_FLAG, getSalesAssociateModifiedFlag(lineItem));
		sql.addColumn(FIELD_RETAIL_TRANSACTION_LINE_ITEM_PREMANENT_RETAIL_PRICE, getPermanentSellingPrice(lineItem));
		/* India Localization - Tax Changes Starts Here */
		sql.addColumn(FIELD_SALE_RETURN_LINE_ITEM_MRP_AMOUNT,
				((MAXPLUItemIfc) lineItem.getPLUItem()).getMaximumRetailPrice().toString());
		/* India Localization - Tax Changes Ends Here */
		String extendedRestockingFee = getItemExtendedRestockingFee(lineItem);
		if (extendedRestockingFee != null) {
			sql.addColumn(FIELD_SALE_RETURN_LINE_ITEM_RESTOCKING_FEE_AMOUNT, extendedRestockingFee);
		}

		if (lineItem.isKitHeader()) {
			sql.addColumn(FIELD_ITEM_KIT_SET_CODE, inQuotes(ItemKitConstantsIfc.ITEM_KIT_CODE_HEADER));
			sql.addColumn(FIELD_ITEM_COLLECTION_ID, getItemID(lineItem));
			sql.addColumn(FIELD_ITEM_KIT_HEADER_REFERENCE_ID, lineItem.getKitHeaderReference());
		} else if (lineItem.isKitComponent()) {
			sql.addColumn(FIELD_ITEM_KIT_SET_CODE, inQuotes(ItemKitConstantsIfc.ITEM_KIT_CODE_COMPONENT));
			sql.addColumn(FIELD_ITEM_COLLECTION_ID, getItemKitID((KitComponentLineItemIfc) lineItem));
			sql.addColumn(FIELD_ITEM_KIT_HEADER_REFERENCE_ID, lineItem.getKitHeaderReference());
		}

		if (lineItem.isPriceAdjustmentLineItem()) {
			sql.addColumn(FIELD_ITEM_PRICEADJ_LINE_ITEM_FLAG, makeCharFromBoolean(true));
			sql.addColumn(FIELD_ITEM_PRICEADJ_REFERENCE_ID, lineItem.getPriceAdjustmentReference());
		} else if (lineItem.isPartOfPriceAdjustment()) {
			sql.addColumn(FIELD_ITEM_PRICEADJ_REFERENCE_ID, lineItem.getPriceAdjustmentReference());
		}

		try {
			dataConnection.execute(sql.getSQLString());
		} catch (DataException de) {
			logger.error("" + de + "");
			throw de;
		} catch (Exception e) {
			throw new DataException(DataException.UNKNOWN, "insertSaleReturnLineItem", e);
		}
	} // end insertOrderLineItem()

	public void saveOrderLineItems(JdbcDataConnection dataConnection, OrderIfc order) throws DataException {
		/*
		 * if (orderTransaction.getTransactionType() !=
		 * TransactionIfc.TYPE_ORDER_INITIATE) {
		 * removeOrderLineItems(dataConnection, orderTransaction); }
		 */

		// insert new line items into order line item table
		AbstractTransactionLineItemIfc[] lineItems = order.getLineItems();
		int numItems = 0;
		if (lineItems != null) {
			numItems = lineItems.length;
		}

		saveVoidLineItems(dataConnection, order);
		deleteSaleReturnLineItem(dataConnection, order.getStatus());
		deleteRetailTransactionLineItem(dataConnection, order.getStatus());

		// loop through line items
		for (int i = 0; i < numItems; i++) {
			SaleReturnLineItemIfc lineItem = (SaleReturnLineItemIfc) lineItems[i];

			insertOrderLineItem(dataConnection, order, lineItem, i);

		}
		insertRetailTransactionLineItem(dataConnection, order.getStatus(), numItems, "TX");

	} // end saveOrderLineItems()

	private void saveVoidLineItems(JdbcDataConnection dataConnection, OrderIfc order) throws DataException {

		OrderStatusIfc orderStatus = order.getStatus();
		TransactionIDIfc transactionID = orderStatus.getInitialTransactionID();

		SQLSelectStatement selectSql = new SQLSelectStatement();
		selectSql.setTable(TABLE_RETAIL_TRANSACTION_LINE_ITEM);
		selectSql.addColumn(FIELD_RETAIL_TRANSACTION_LINE_ITEM_SEQUENCE_NUMBER);
		selectSql.addQualifier(FIELD_TRANSACTION_SEQUENCE_NUMBER + " = '" + transactionID.getSequenceNumber() + "'");
		selectSql.addQualifier(FIELD_RETAIL_STORE_ID + " = '" + transactionID.getStoreID() + "'");
		selectSql.addQualifier(FIELD_WORKSTATION_ID + " = '" + transactionID.getWorkstationID() + "'");
		selectSql.addQualifier(FIELD_BUSINESS_DAY_DATE + " = "
				+ dateToSQLDateString(orderStatus.getRecordingTransactionBusinessDate()));
		selectSql.addQualifier(FIELD_RETAIL_TRANSACTION_LINE_ITEM_VOID_FLAG + " = '1'");

		try {

			dataConnection.execute(selectSql.getSQLString());
			ResultSet rs = (ResultSet) dataConnection.getResult();
			String lineItemNumber = "";
			while (rs.next()) {
				lineItemNumber = lineItemNumber + rs.getString(1) + ",";
			}
			if (!lineItemNumber.equals("")) {
				lineItemNumber = lineItemNumber.substring(0, lineItemNumber.length() - 1);

				SQLSelectStatement sql = new SQLSelectStatement();
				sql.setTable(TABLE_RETAIL_TRANSACTION_LINE_ITEM);
				sql.addColumn("*");
				sql.addQualifier(FIELD_TRANSACTION_SEQUENCE_NUMBER + " = '" + transactionID.getSequenceNumber() + "'");
				sql.addQualifier(FIELD_RETAIL_STORE_ID + " = '" + transactionID.getStoreID() + "'");
				sql.addQualifier(FIELD_WORKSTATION_ID + " = '" + transactionID.getWorkstationID() + "'");
				sql.addQualifier(FIELD_BUSINESS_DAY_DATE + " = "
						+ dateToSQLDateString(orderStatus.getRecordingTransactionBusinessDate()));
				sql.addQualifier(FIELD_RETAIL_TRANSACTION_LINE_ITEM_VOID_FLAG + " = '1'");

				SQLSelectStatement sql2 = new SQLSelectStatement();
				sql2.setTable(TABLE_SALE_RETURN_LINE_ITEM);
				sql2.addColumn("*");
				sql2.addQualifier(FIELD_TRANSACTION_SEQUENCE_NUMBER + " = '" + transactionID.getSequenceNumber() + "'");
				sql2.addQualifier(FIELD_RETAIL_STORE_ID + " = '" + transactionID.getStoreID() + "'");
				sql2.addQualifier(FIELD_WORKSTATION_ID + " = '" + transactionID.getWorkstationID() + "'");
				sql2.addQualifier(FIELD_BUSINESS_DAY_DATE + " = "
						+ dateToSQLDateString(orderStatus.getRecordingTransactionBusinessDate()));
				sql2.addQualifier(FIELD_RETAIL_TRANSACTION_LINE_ITEM_SEQUENCE_NUMBER + " IN (" + lineItemNumber + ")");

				String newSQL = "INSERT INTO " + TABLE_RETAIL_TRANSACTION_LINE_ITEM + "_VOID " + sql.getSQLString();
				String newSQL2 = "INSERT INTO " + TABLE_SALE_RETURN_LINE_ITEM + "_VOID " + sql2.getSQLString();
				dataConnection.execute(newSQL);
				dataConnection.execute(newSQL2);
			}
		}

		catch (DataException de) {
			logger.warn("" + de + "");
			if (de.getErrorCode() == DataException.UNKNOWN) {
				throw new DataException(DataException.CONNECTION_ERROR, "Connection lost");
			} else {
				throw de;
			}
		} catch (SQLException se) {
			dataConnection.logSQLException(se, "order line item table");
			throw new DataException(DataException.SQL_ERROR, "order line item table", se);
		}

		if (order instanceof MAXOrderIfc && ((MAXOrderIfc) order).getDeletedItems().size() > 0) {

			Iterator iter = ((MAXOrderIfc) order).getDeletedItems().iterator();
			int counter = 0;
			while (iter.hasNext()) {

				SaleReturnLineItemIfc lineItem = (SaleReturnLineItemIfc) iter.next();

				insertDeletedOrderLineItem(dataConnection, order, lineItem, counter++);

			}
		}

	}

	private void deleteRetailTransactionLineItem(JdbcDataConnection connection, OrderStatusIfc orderStatus)
			throws DataException {
		TransactionIDIfc transactionID = orderStatus.getInitialTransactionID();

		SQLDeleteStatement sql = new SQLDeleteStatement();
		sql.setTable(TABLE_RETAIL_TRANSACTION_LINE_ITEM);
		sql.addQualifier(FIELD_TRANSACTION_SEQUENCE_NUMBER + " = '" + transactionID.getSequenceNumber() + "'");
		sql.addQualifier(FIELD_RETAIL_STORE_ID + " = '" + transactionID.getStoreID() + "'");
		sql.addQualifier(FIELD_WORKSTATION_ID + " = '" + transactionID.getWorkstationID() + "'");
		sql.addQualifier(FIELD_BUSINESS_DAY_DATE + " = "
				+ dateToSQLDateString(orderStatus.getRecordingTransactionBusinessDate()));
		try {
			connection.execute(sql.getSQLString());
		}

		catch (DataException de) {
			logger.warn("" + de + "");
			if (de.getErrorCode() == DataException.UNKNOWN) {
				throw new DataException(DataException.CONNECTION_ERROR, "Connection lost");
			} else {
				throw de;
			}
		}

		if (0 >= connection.getUpdateCount()) {
			logger.warn("No order line item updated ");
			throw new DataException(DataException.NO_DATA, "No order line item updated ");
		}

	}

	private void deleteSaleReturnLineItem(JdbcDataConnection connection, OrderStatusIfc orderStatus)
			throws DataException {

		TransactionIDIfc transactionID = orderStatus.getInitialTransactionID();

		SQLDeleteStatement sql = new SQLDeleteStatement();
		sql.setTable(TABLE_SALE_RETURN_LINE_ITEM);
		sql.addQualifier(FIELD_TRANSACTION_SEQUENCE_NUMBER + " = '" + transactionID.getSequenceNumber() + "'");
		sql.addQualifier(FIELD_RETAIL_STORE_ID + " = '" + transactionID.getStoreID() + "'");
		sql.addQualifier(FIELD_WORKSTATION_ID + " = '" + transactionID.getWorkstationID() + "'");
		sql.addQualifier(FIELD_BUSINESS_DAY_DATE + " = "
				+ dateToSQLDateString(orderStatus.getRecordingTransactionBusinessDate()));
		try {
			connection.execute(sql.getSQLString());
		}

		catch (DataException de) {
			logger.warn("" + de + "");
			if (de.getErrorCode() == DataException.UNKNOWN) {
				throw new DataException(DataException.CONNECTION_ERROR, "Connection lost");
			} else {
				throw de;
			}
		}

		if (0 >= connection.getUpdateCount()) {
			logger.warn("No order line item updated ");
			throw new DataException(DataException.NO_DATA, "No order line item updated ");
		}

	}

	public void insertRetailTransactionLineItem(JdbcDataConnection dataConnection, OrderStatusIfc orderStatus,
			int lineItemSequenceNumber, String lineItemType) throws DataException {
		SQLInsertStatement sql = new SQLInsertStatement();

		// Table
		sql.setTable(TABLE_RETAIL_TRANSACTION_LINE_ITEM);

		// Fields
		sql.addColumn(FIELD_RETAIL_STORE_ID, "'" + orderStatus.getInitialTransactionID().getStoreID() + "'");
		sql.addColumn(FIELD_WORKSTATION_ID, orderStatus.getInitialTransactionID().getWorkstationID());
		sql.addColumn(FIELD_TRANSACTION_SEQUENCE_NUMBER,
				String.valueOf(orderStatus.getInitialTransactionID().getSequenceNumber()));
		sql.addColumn(FIELD_BUSINESS_DAY_DATE, dateToSQLDateString(orderStatus.getRecordingTransactionBusinessDate()));
		sql.addColumn(FIELD_RETAIL_TRANSACTION_LINE_ITEM_SEQUENCE_NUMBER, Integer.toString(lineItemSequenceNumber));
		sql.addColumn(FIELD_RETAIL_TRANSACTION_LINE_ITEM_TYPE_CODE, inQuotes(lineItemType));
		sql.addColumn(FIELD_RECORD_LAST_MODIFIED_TIMESTAMP, getSQLCurrentTimestampFunction());
		sql.addColumn(FIELD_RECORD_CREATION_TIMESTAMP, getSQLCurrentTimestampFunction());
		try {
			dataConnection.execute(sql.getSQLString());
		} catch (DataException de) {
			logger.error("" + de + "");
			throw de;
		} catch (Exception e) {
			throw new DataException(DataException.UNKNOWN, "insertRetailTransactionLineItem", e);
		}
	}

	protected String getItemSerial(SaleReturnLineItemIfc lineItem) {
		String ret = "null";
		if (lineItem.getItemSerial() != null) {
			ret = "'" + lineItem.getItemSerial() + "'";
		}
		return (ret);
	}

	protected String getTaxGroupID(SaleReturnLineItemIfc lineItem) {
		return (Integer.toString(lineItem.getPLUItem().getTaxGroupID()));
	}

	protected String getItemQuantity(SaleReturnLineItemIfc lineItem) {
		return (lineItem.getItemQuantityDecimal().toString());
	}

	protected String getItemExtendedAmount(SaleReturnLineItemIfc lineItem) {
		return (lineItem.getExtendedSellingPrice().getStringValue());
	}

	public String getGiftRegistryString(SaleReturnLineItemIfc lineItem) {
		String value = "null";

		// If there is not gift registry associated with this object, we
		// need to set the registry value to null in the database.
		if (lineItem.getRegistry() != null) {
			value = "'" + lineItem.getRegistry().getID() + "'";
		}

		return (value);
	}

	protected String getItemExtendedDiscountedAmount(SaleReturnLineItemIfc lineItem) {
		return (lineItem.getExtendedDiscountedSellingPrice().getStringValue());
	}

	protected String getSendLabelCount(SaleReturnLineItemIfc lineItem) {
		return String.valueOf(lineItem.getSendLabelCount());
	}

	protected String getReturnFlag(SaleReturnLineItemIfc lineItem) {
		String value = "'0'";
		if (lineItem.getItemQuantityDecimal().signum() < 0) {
			value = "'1'";
		}
		return (value);
	}

	protected String getReturnReasonCode(SaleReturnLineItemIfc lineItem) {
		String value = "null";
		if (lineItem.getReturnItem() != null) {
			value = "'" + String.valueOf(lineItem.getReturnItem().getReasonCode()) + "'";
		}
		return (value);
	}

	protected String getOriginalTransactionId(SaleReturnLineItemIfc lineItem) {
		String ret = "null";

		if (lineItem.getReturnItem() != null && lineItem.getReturnItem().getOriginalTransactionID() != null) {
			ret = "'" + lineItem.getReturnItem().getOriginalTransactionID().getTransactionIDString() + "'";
		}

		return ret;

	}

	protected String getOriginalDate(SaleReturnLineItemIfc lineItem) {
		String ret = "null";

		if (lineItem.getReturnItem() != null && lineItem.getReturnItem().getOriginalTransactionBusinessDate() != null) {
			ret = dateToSQLDateString(lineItem.getReturnItem().getOriginalTransactionBusinessDate().dateValue());
		}

		return ret;
	}

	protected String getOriginalStoreID(SaleReturnLineItemIfc lineItem) {
		String ret = "null";

		if (lineItem.getReturnItem() != null && lineItem.getReturnItem().getStore() != null) {
			ret = "'" + lineItem.getReturnItem().getStore().getStoreID() + "'";
		}

		return ret;
	}

	protected String getOriginalLineNumber(SaleReturnLineItemIfc lineItem) {
		String ret = "-1";

		if (lineItem.getReturnItem() != null) {
			ret = String.valueOf(lineItem.getReturnItem().getOriginalLineNumber());
		}

		return ret;
	}

	protected String getDepartmentID(SaleReturnLineItemIfc lineItem) {
		return ("'" + lineItem.getPLUItem().getDepartmentID() + "'");
	}

	protected String getSendFlag(SaleReturnLineItemIfc lineItem) {
		String value = "'0'";
		if (lineItem.getItemSendFlag()) {
			value = "'1'";
		}
		return (value);
	}

	protected String getItemSizeCode(SaleReturnLineItemIfc lineItem) {
		String value = "";
		if (lineItem.getItemSizeCode() != null) {
			value = lineItem.getItemSizeCode();
		}
		value = makeSafeString(value);
		return (value);
	}

	protected String getGiftReceiptFlag(SaleReturnLineItemIfc lineItem) {
		String value = "'0'";
		if (lineItem.isGiftReceiptItem()) {
			value = "'1'";
		}
		return (value);
	}

	protected String getReturnRelatedItemFlag(SaleReturnLineItemIfc lineItem) {
		String value = "'0'";
		if (lineItem.isRelatedItemReturnable()) {
			value = "'1'";
		}
		return (value);
	}

	protected String getRelatedSeqNum(SaleReturnLineItemIfc lineItem) {
		return String.valueOf(lineItem.getRelatedItemSequenceNumber());
	}

	protected String getRemoveRelatedItemFlag(SaleReturnLineItemIfc lineItem) {
		String value = "'0'";
		if (lineItem.isRelatedItemDeleteable()) {
			value = "'1'";
		}
		return (value);
	}

	public String getSalesAssociateModifiedFlag(SaleReturnLineItemIfc lineItem) {
		if (lineItem.getSalesAssociateModifiedFlag())
			return ("'1'");
		return ("'0'");
	}

	protected String getPermanentSellingPrice(SaleReturnLineItemIfc lineItem) {
		return (lineItem.getItemPrice().getPermanentSellingPrice().getStringValue());
	}

	protected String getItemExtendedRestockingFee(SaleReturnLineItemIfc lineItem) {
		String restockingFeeString = null;

		ItemPriceIfc price = lineItem.getItemPrice();
		if (price != null) {
			CurrencyIfc restockingFee = price.getExtendedRestockingFee();

			if (restockingFee != null) {
				restockingFeeString = restockingFee.getStringValue();
			}
		}

		return (restockingFeeString);
	}

	protected String getItemID(SaleReturnLineItemIfc lineItem) {
		return ("'" + lineItem.getPLUItem().getItemID() + "'");
	}

	protected String getItemKitID(KitComponentLineItemIfc lineItem) {
		return ("'" + lineItem.getItemKitID() + "'");
	}

	public void insertDeletedOrderLineItem(JdbcDataConnection dataConnection, OrderIfc order,
			SaleReturnLineItemIfc lineItem, int lineItemSequenceNumber) throws DataException { // begin
																								// insertOrderLineItem()
																								// pull
																								// out
																								// order
																								// status
		OrderStatusIfc orderStatus = order.getStatus();
		OrderItemStatusIfc itemStatus = lineItem.getOrderItemStatus();
		insertDeletedRetailTransactionLineItem(dataConnection, orderStatus, lineItem.getLineNumber(), "SR");

		SQLInsertStatement sql = new SQLInsertStatement();

		// Table
		sql.setTable(TABLE_SALE_RETURN_LINE_ITEM + "_VOID");

		// Fields
		sql.addColumn(FIELD_RETAIL_STORE_ID, "'" + orderStatus.getInitialTransactionID().getStoreID() + "'");
		sql.addColumn(FIELD_WORKSTATION_ID, orderStatus.getInitialTransactionID().getWorkstationID());
		sql.addColumn(FIELD_TRANSACTION_SEQUENCE_NUMBER,
				String.valueOf(orderStatus.getInitialTransactionID().getSequenceNumber()));
		sql.addColumn(FIELD_BUSINESS_DAY_DATE, dateToSQLDateString(orderStatus.getRecordingTransactionBusinessDate()));
		sql.addColumn(FIELD_RETAIL_TRANSACTION_LINE_ITEM_SEQUENCE_NUMBER, Integer.toString(lineItem.getLineNumber()));
		sql.addColumn(FIELD_GIFT_REGISTRY_ID, getGiftRegistryString(lineItem));
		sql.addColumn(FIELD_ITEM_ID, "'" + lineItem.getPLUItem().getItemID() + "'");
		sql.addColumn(FIELD_POS_ITEM_ID, inQuotes(lineItem.getPosItemID()));
		sql.addColumn(FIELD_SERIAL_NUMBER, getItemSerial(lineItem));
		sql.addColumn(FIELD_TAX_GROUP_ID, getTaxGroupID(lineItem));
		sql.addColumn(FIELD_SALE_RETURN_LINE_ITEM_QUANTITY, getItemQuantity(lineItem));
		sql.addColumn(FIELD_SALE_RETURN_LINE_ITEM_EXTENDED_AMOUNT, getItemExtendedAmount(lineItem));
		sql.addColumn(FIELD_SALE_RETURN_LINE_ITEM_EXTENDED_DISCOUNTED_AMOUNT,
				getItemExtendedDiscountedAmount(lineItem));
		sql.addColumn(FIELD_SALE_RETURN_LINE_ITEM_VAT_AMOUNT, lineItem.getItemTaxAmount().getStringValue());
		sql.addColumn(FIELD_SALE_RETURN_LINE_ITEM_TAX_INC_AMOUNT,
				lineItem.getItemInclusiveTaxAmount().getStringValue());
		sql.addColumn(FIELD_SEND_LABEL_COUNT, getSendLabelCount(lineItem));
		sql.addColumn(FIELD_MERCHANDISE_RETURN_FLAG, getReturnFlag(lineItem));
		sql.addColumn(FIELD_MERCHANDISE_RETURN_REASON_CODE, getReturnReasonCode(lineItem));
		sql.addColumn(FIELD_POS_ORIGINAL_TRANSACTION_ID, getOriginalTransactionId(lineItem));
		sql.addColumn(FIELD_ORIGINAL_BUSINESS_DAY_DATE, getOriginalDate(lineItem));
		sql.addColumn(FIELD_ORIGINAL_RETAIL_TRANSACTION_LINE_ITEM_SEQUENCE_NUMBER, getOriginalLineNumber(lineItem));
		sql.addColumn(FIELD_ORIGINAL_RETAIL_STORE_ID, getOriginalStoreID(lineItem));
		sql.addColumn(FIELD_POS_DEPARTMENT_ID, getDepartmentID(lineItem));
		sql.addColumn(FIELD_SEND_FLAG, getSendFlag(lineItem));
		sql.addColumn(FIELD_RECORD_LAST_MODIFIED_TIMESTAMP, getSQLCurrentTimestampFunction());
		sql.addColumn(FIELD_RECORD_CREATION_TIMESTAMP, getSQLCurrentTimestampFunction());
		sql.addColumn(FIELD_GIFT_RECEIPT_FLAG, getGiftReceiptFlag(lineItem));
		sql.addColumn(FIELD_ORDER_REFERENCE_ID, lineItem.getOrderLineReference());
		sql.addColumn(FIELD_ITEM_ID_ENTRY_METHOD_CODE, inQuotes(lineItem.getEntryMethod().toString()));
		sql.addColumn(FIELD_SIZE_CODE, getItemSizeCode(lineItem));

		sql.addColumn(FIELD_RETURN_RELATED_ITEM_FLAG, getReturnRelatedItemFlag(lineItem));
		sql.addColumn(FIELD_RELATED_ITEM_TRANSACTION_LINE_ITEM_SEQ_NUMBER, getRelatedSeqNum(lineItem));
		sql.addColumn(FIELD_REMOVE_RELATED_ITEM_FLAG, getRemoveRelatedItemFlag(lineItem));
		sql.addColumn(FIELD_RETAIL_TRANSACTION_LINE_ITEM_SALES_ASSC_FLAG, getSalesAssociateModifiedFlag(lineItem));
		sql.addColumn(FIELD_RETAIL_TRANSACTION_LINE_ITEM_PREMANENT_RETAIL_PRICE, getPermanentSellingPrice(lineItem));
		/* India Localization - Tax Changes Starts Here */
		sql.addColumn(FIELD_SALE_RETURN_LINE_ITEM_MRP_AMOUNT,
				((MAXPLUItemIfc) lineItem.getPLUItem()).getMaximumRetailPrice().getStringValue());
		/* India Localization - Tax Changes Ends Here */
		String extendedRestockingFee = getItemExtendedRestockingFee(lineItem);
		if (extendedRestockingFee != null) {
			sql.addColumn(FIELD_SALE_RETURN_LINE_ITEM_RESTOCKING_FEE_AMOUNT, extendedRestockingFee);
		}

		if (lineItem.isKitHeader()) {
			sql.addColumn(FIELD_ITEM_KIT_SET_CODE, inQuotes(ItemKitConstantsIfc.ITEM_KIT_CODE_HEADER));
			sql.addColumn(FIELD_ITEM_COLLECTION_ID, getItemID(lineItem));
			sql.addColumn(FIELD_ITEM_KIT_HEADER_REFERENCE_ID, lineItem.getKitHeaderReference());
		} else if (lineItem.isKitComponent()) {
			sql.addColumn(FIELD_ITEM_KIT_SET_CODE, inQuotes(ItemKitConstantsIfc.ITEM_KIT_CODE_COMPONENT));
			sql.addColumn(FIELD_ITEM_COLLECTION_ID, getItemKitID((KitComponentLineItemIfc) lineItem));
			sql.addColumn(FIELD_ITEM_KIT_HEADER_REFERENCE_ID, lineItem.getKitHeaderReference());
		}

		if (lineItem.isPriceAdjustmentLineItem()) {
			sql.addColumn(FIELD_ITEM_PRICEADJ_LINE_ITEM_FLAG, makeCharFromBoolean(true));
			sql.addColumn(FIELD_ITEM_PRICEADJ_REFERENCE_ID, lineItem.getPriceAdjustmentReference());
		} else if (lineItem.isPartOfPriceAdjustment()) {
			sql.addColumn(FIELD_ITEM_PRICEADJ_REFERENCE_ID, lineItem.getPriceAdjustmentReference());
		}

		try {
			dataConnection.execute(sql.getSQLString());
		} catch (DataException de) {
			logger.error("" + de + "");
			throw de;
		} catch (Exception e) {
			throw new DataException(DataException.UNKNOWN, "insertSaleReturnLineItem", e);
		}
	} // end insertOrderLineItem()

	public void insertDeletedRetailTransactionLineItem(JdbcDataConnection dataConnection, OrderStatusIfc orderStatus,
			int lineItemSequenceNumber, String lineItemType) throws DataException {
		SQLInsertStatement sql = new SQLInsertStatement();

		// Table
		sql.setTable(TABLE_RETAIL_TRANSACTION_LINE_ITEM + "_VOID");

		// Fields
		sql.addColumn(FIELD_RETAIL_STORE_ID, "'" + orderStatus.getInitialTransactionID().getStoreID() + "'");
		sql.addColumn(FIELD_WORKSTATION_ID, orderStatus.getInitialTransactionID().getWorkstationID());
		sql.addColumn(FIELD_TRANSACTION_SEQUENCE_NUMBER,
				String.valueOf(orderStatus.getInitialTransactionID().getSequenceNumber()));
		sql.addColumn(FIELD_BUSINESS_DAY_DATE, dateToSQLDateString(orderStatus.getRecordingTransactionBusinessDate()));
		sql.addColumn(FIELD_RETAIL_TRANSACTION_LINE_ITEM_SEQUENCE_NUMBER, Integer.toString(lineItemSequenceNumber));
		sql.addColumn(FIELD_RETAIL_TRANSACTION_LINE_ITEM_TYPE_CODE, inQuotes(lineItemType));
		sql.addColumn(FIELD_RECORD_LAST_MODIFIED_TIMESTAMP, getSQLCurrentTimestampFunction());
		sql.addColumn(FIELD_RECORD_CREATION_TIMESTAMP, getSQLCurrentTimestampFunction());
		try {
			dataConnection.execute(sql.getSQLString());
		} catch (DataException de) {
			logger.error("" + de + "");
			throw de;
		} catch (Exception e) {
			throw new DataException(DataException.UNKNOWN, "insertRetailTransactionLineItem", e);
		}
	}
}
