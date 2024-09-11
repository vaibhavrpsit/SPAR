/********************************************************************************
 *   
 *	Copyright (c) 2018 - 2019 MAX Hypermarket, Inc    All Rights Reserved.
 *  Rev 1.5 	Aug 27, 2021		Atul Shukla      EWallet FES Implementation
 *  Rev 1.2 	Sep 01, 2019		Kumar Vaibhav       Pinelabs integratio 
 *	Rev 1.1		Jun 01, 2019		Purushotham Reddy   Changes done for POS-Amazon Pay Integration 
 *	Rev 1.1		Feb 23, 2016		Ashish Yadav		Changes for added new col for gift card inorder to make same for 12 version
 *	Rev 1.0		Dec 20, 2016		Mansi Goel			Changes for Gift Card FES
 *
 ********************************************************************************/

package max.retail.stores.domain.arts;


import java.awt.Point;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;

import max.retail.stores.domain.paytm.MAXPaytmResponse;
import max.retail.stores.domain.tender.MAXAuthorizableTenderIfc;
import max.retail.stores.domain.tender.MAXTenderAmazonPayIfc;
import max.retail.stores.domain.tender.MAXTenderChargeIfc;
import max.retail.stores.domain.tender.MAXTenderDebitIfc;
import max.retail.stores.domain.tender.MAXTenderEComCODIfc;
import max.retail.stores.domain.tender.MAXTenderEComPrepaidIfc;
import max.retail.stores.domain.tender.MAXTenderGiftCardIfc;
import max.retail.stores.domain.tender.MAXTenderLoyaltyPointsIfc;
import max.retail.stores.domain.tender.MAXTenderMobikwikIfc;
import max.retail.stores.domain.tender.MAXTenderPaytmIfc;
import max.retail.stores.domain.tender.MAXTenderPurchaseOrder;
import max.retail.stores.domain.transaction.MAXSaleReturnTransaction;
import max.retail.stores.domain.transaction.MAXTenderableTransactionIfc;
import max.retail.stores.domain.utility.MAXGiftCard;
import max.retail.stores.domain.utility.MAXGiftCardIfc;
import max.retail.stores.persistence.utility.MAXARTSDatabaseIfc;
import oracle.retail.stores.commerceservices.common.currency.CurrencyIfc;
import oracle.retail.stores.common.context.BeanLocator;
import oracle.retail.stores.common.data.JdbcUtilities;
import oracle.retail.stores.common.sql.SQLInsertStatement;
import oracle.retail.stores.common.sql.SQLUpdateStatement;
import oracle.retail.stores.common.utility.ImageUtils;
import oracle.retail.stores.common.utility.LocaleMap;
import oracle.retail.stores.domain.arts.JdbcSaveTenderLineItems;
import oracle.retail.stores.domain.tender.TenderAlternateCurrencyIfc;
import oracle.retail.stores.domain.tender.TenderCash;
import oracle.retail.stores.domain.tender.TenderCashIfc;
import oracle.retail.stores.domain.tender.TenderChargeIfc;
import oracle.retail.stores.domain.tender.TenderCheckIfc;
import oracle.retail.stores.domain.tender.TenderCoupon;
import oracle.retail.stores.domain.tender.TenderCouponIfc;
import oracle.retail.stores.domain.tender.TenderGiftCardIfc;
import oracle.retail.stores.domain.tender.TenderGiftCertificateIfc;
import oracle.retail.stores.domain.tender.TenderLineItemIfc;
import oracle.retail.stores.domain.tender.TenderMailBankCheckIfc;
import oracle.retail.stores.domain.tender.TenderMoneyOrderIfc;
import oracle.retail.stores.domain.tender.TenderPurchaseOrderIfc;
import oracle.retail.stores.domain.tender.TenderStoreCreditIfc;
import oracle.retail.stores.domain.tender.TenderTravelersCheckIfc;
import oracle.retail.stores.domain.transaction.TenderableTransactionIfc;
import oracle.retail.stores.domain.utility.EntryMethod;
import oracle.retail.stores.domain.utility.LocaleConstantsIfc;
import oracle.retail.stores.foundation.manager.data.DataException;
import oracle.retail.stores.foundation.manager.data.JdbcDataConnection;
import oracle.retail.stores.foundation.tour.gate.Gateway;
import oracle.retail.stores.foundation.utility.Util;
import oracle.retail.stores.persistence.dao.tender.IntegratedChipCardDetailsDAOIfc;
import oracle.retail.stores.persistence.utility.DatabaseBlobHelperFactory;
import oracle.retail.stores.persistence.utility.DatabaseBlobHelperIfc;

import org.apache.log4j.Logger;

//-------------------------------------------------------------------------
/**
 * Save all tender line items of the Transaction to the database.
 * <P>
 * 
 * @version $Revision: 20$
 */
// -------------------------------------------------------------------------
public class MAXJdbcSaveTenderLineItems extends JdbcSaveTenderLineItems implements MAXARTSDatabaseIfc
{
	
	static int paytmTenderCount=0;
	/**
	 * 
	 */
	private static final long serialVersionUID = -3974585317967643746L;
	/**
	 * The logger to which log messages will be sent.
	 */
	private static Logger logger = Logger.getLogger(max.retail.stores.domain.arts.MAXJdbcSaveTenderLineItems.class);

	protected static char maskChar = 0;
	// ---------------------------------------------------------------------
	/**
	 * Class constructor.
	 * <P>
	 */
	// ---------------------------------------------------------------------
	public MAXJdbcSaveTenderLineItems() {
		super();
		setName("MGJdbcSaveTenderLineItems");
	}

	// ---------------------------------------------------------------------
	/**
	 * Saves one tender line item.
	 * <P>
	 * 
	 * @param dataConnection
	 *            connection to the db
	 * @param transaction
	 *            retail transaction
	 * @param lieItemSequenceNumber
	 *            sequence number associated with this line item
	 * @param lineItem
	 *            the tender line item
	 * @exception DataException
	 *                error saving data to the db
	 */
	// ---------------------------------------------------------------------
	public void saveTenderLineItem(JdbcDataConnection dataConnection, TenderableTransactionIfc transaction,
			int lineItemSequenceNumber, TenderLineItemIfc lineItem) throws DataException {

		if (lineItem instanceof TenderCashIfc || lineItem instanceof MAXTenderEComPrepaidIfc
				|| lineItem instanceof MAXTenderEComCODIfc)

		{
			/*
			 * Cash tender updates the Tender Line Item and Retail Transaction
			 * Line Item tables.
			 */
			insertTenderLineItem(dataConnection, transaction, lineItemSequenceNumber, lineItem);
		} else if (lineItem instanceof TenderMoneyOrderIfc) {
			/*
			 * Cash tender updates the Tender Line Item and Retail Transaction
			 * Line Item tables.
			 */
			insertMoneyOrderTenderLineItem(dataConnection, transaction, lineItemSequenceNumber,
					(TenderMoneyOrderIfc) lineItem);
		} else if (lineItem instanceof TenderGiftCardIfc) {
			/*
			 * This one MUST come before TenderChargeIfc because
			 * TenderGiftCardIfc extends it. Gift Card tender updates the Gift
			 * Card Tender Line Item, Tender Line Item, and Retail Transaction
			 * Line Item tables.
			 */
			insertGiftCardTenderLineItem(dataConnection, transaction, lineItemSequenceNumber,
					(TenderGiftCardIfc) lineItem);
		} else if (lineItem instanceof TenderChargeIfc) {
			/*
			 * Charge tender updates the Credit/Debit Card Tender Line Item,
			 * Tender Line Item, and Retail Transaction Line Item tables.
			 */
			insertCreditDebitCardTenderLineItem(dataConnection, transaction, lineItemSequenceNumber,
					(MAXTenderChargeIfc) lineItem);
		}
		else if (lineItem instanceof MAXTenderDebitIfc) {
			insertCreditDebitCardTenderLineItem(dataConnection, transaction, lineItemSequenceNumber,
					(MAXTenderDebitIfc) lineItem);
		}
		else if (lineItem instanceof TenderCheckIfc) {
			/*
			 * Check tender updates the Check Tender Line Item, Tender Line
			 * Item, and Retail Transaction Line Item tables.
			 */
			insertCheckTenderLineItem(dataConnection, transaction, lineItemSequenceNumber, (TenderCheckIfc) lineItem);
		} else if (lineItem instanceof TenderGiftCertificateIfc) {
			/*
			 * Gift Certificate tender updates the Gift Certificate Tender Line
			 * Item, Tender Line Item, and Retail Transaction Line Item tables.
			 */
			if (!transaction.isTrainingMode()) {
				insertGiftCertificateTenderLineItem(dataConnection, transaction, lineItemSequenceNumber,
						(TenderGiftCertificateIfc) lineItem);
			}
		} else if (lineItem instanceof TenderMailBankCheckIfc) {
			/*
			 * Mail Bank Check tender updates the Send Check Tender Line Item,
			 * Tender Line Item, and Retail Transaction Line Item tables.
			 */
			insertSendCheckTenderLineItem(dataConnection, transaction, lineItemSequenceNumber,
					(TenderMailBankCheckIfc) lineItem);
		} else if (lineItem instanceof TenderTravelersCheckIfc) {
			/*
			 * Travelers Check tender updates the Send Check Tender Line Item,
			 * Tender Line Item, and Retail Transaction Line Item tables.
			 */
			insertTravelersCheckTenderLineItem(dataConnection, transaction, lineItemSequenceNumber,
					(TenderTravelersCheckIfc) lineItem);
		} else if (lineItem instanceof TenderCouponIfc) {
			/*
			 * Coupon tender updates the Coupon Tender Line Item, Tender Line
			 * Item, and Retail Transaction Line Item tables.
			 */
			insertCouponTenderLineItem(dataConnection, transaction, lineItemSequenceNumber, (TenderCouponIfc) lineItem);
		} else if (lineItem instanceof TenderStoreCreditIfc) {
			/*
			 * Store credit tender updates the Store Credit Tender Line Item,
			 * Tender Line Item, and Retail Transaction Line Item tables.
			 */
	
			insertStoreCreditTenderLineItem(dataConnection, transaction, lineItemSequenceNumber,
					(TenderStoreCreditIfc) lineItem);
			
		} else if (lineItem instanceof TenderPurchaseOrderIfc) {
			/*
			 * Purchase Order tender updates the Send Check Tender Line Item,
			 * Tender Line Item, and Retail Transaction Line Item tables.
			 */
			
			insertPurchaseOrderTenderLineItem(dataConnection, transaction, lineItemSequenceNumber,
					(MAXTenderPurchaseOrder) lineItem);
			
		} else if (lineItem instanceof MAXTenderLoyaltyPointsIfc) {
			insertLoyaltyPointsTenderLineItem(dataConnection, transaction, lineItemSequenceNumber,
					(MAXTenderLoyaltyPointsIfc) lineItem);

		}
		
		else if (lineItem instanceof MAXTenderPaytmIfc)
		{
	/*	int str=transaction.getTransactionType(); 
		// below code added by atul shukla for bug ID 18559
		if(transaction instanceof MAXVoidTransaction && transaction.getTransactionType()==3)
		{
		}else
		{ */
			insertPaytmTenderLineItem(dataConnection, transaction, lineItemSequenceNumber,
					(MAXTenderPaytmIfc) lineItem);

		//}
		}
		
		// Changes done for POS-Amazon Pay Integration @Purushotham Reddy
		
		else if (lineItem instanceof MAXTenderAmazonPayIfc)
		{
			insertAmazonPayTenderLineItem(dataConnection, transaction, lineItemSequenceNumber,
					(MAXTenderAmazonPayIfc) lineItem);

		}
		
		
		else if (lineItem instanceof MAXTenderMobikwikIfc)
		{
	/*	int str=transaction.getTransactionType(); 
		// below code added by atul shukla for bug ID 18559
		if(transaction instanceof MAXVoidTransaction && transaction.getTransactionType()==3)
		{
		}else
		{ */
			insertMobikwikTenderLineItem(dataConnection, transaction, lineItemSequenceNumber,
					(MAXTenderMobikwikIfc) lineItem);

		// }
		}
		
	}

	 // Bhanu Priya Changes Start
	public void  insertMobikwikTenderLineItem(JdbcDataConnection dataConnection,
			TenderableTransactionIfc transaction, int lineItemSequenceNumber,
			MAXTenderMobikwikIfc lineItem) throws DataException {
		
		insertTenderLineItem(dataConnection, transaction, lineItemSequenceNumber, lineItem);
		SQLInsertStatement sql = new SQLInsertStatement();

		// Table
		sql.setTable(TABLE_WALLET_TENDER_LINE_ITEM);

		// Fields
		sql.addColumn(FIELD_RETAIL_STORE_ID, getStoreID(transaction));
		sql.addColumn(FIELD_WORKSTATION_ID, getWorkstationID(transaction));
		sql.addColumn(FIELD_BUSINESS_DAY_DATE, getBusinessDayString(transaction));
		sql.addColumn(FIELD_TRANSACTION_SEQUENCE_NUMBER, getTransactionSequenceNumber(transaction));
		sql.addColumn(FIELD_RETAIL_TRANSACTION_LINE_ITEM_SEQUENCE_NUMBER, getSequenceNumber(lineItemSequenceNumber));
	    sql.addColumn(FIELD_TENDER_TYPE_CODE, getTenderType(lineItem));
	    sql.addColumn(FIELD_WALLET_ORDER_ID, getMobikwikWalletOrderID(lineItem));
	    sql.addColumn(FIELD_TRANS_ID, getMobikwikTransactionID(lineItem));
		sql.addColumn(FIELD_MOBILE_NUMBER, getMobikwikMobileNumber(lineItem));
		sql.addColumn(FIELD_TENDER_LINE_ITEM_AMOUNT, getMobikwikAmountPaid(lineItem));
	    sql.addColumn(FIELD_RECORD_LAST_MODIFIED_TIMESTAMP, getSQLCurrentTimestampFunction());
        sql.addColumn(FIELD_RECORD_CREATION_TIMESTAMP, getSQLCurrentTimestampFunction());
			
	

		try {
			dataConnection.execute(sql.getSQLString());
		} catch (DataException de) {
			logger.error("" + de.toString() + "");
			throw de;
		} catch (Exception e) {
			throw new DataException(DataException.UNKNOWN, "insertMobikwikTenderLineItem", e);
		}
		// Bhanu Priya Changes Ends	
		}
		
	//}

	
	 // Bhanu Priya Changes Start
	public void  insertPaytmTenderLineItem(JdbcDataConnection dataConnection,
			TenderableTransactionIfc transaction, int lineItemSequenceNumber,
			MAXTenderPaytmIfc lineItem) throws DataException {
		
		insertTenderLineItem(dataConnection, transaction, lineItemSequenceNumber, lineItem);
		SQLInsertStatement sql = new SQLInsertStatement();

		// Table
		sql.setTable(TABLE_WALLET_TENDER_LINE_ITEM);

		// Fields
		sql.addColumn(FIELD_RETAIL_STORE_ID, getStoreID(transaction));
		sql.addColumn(FIELD_WORKSTATION_ID, getWorkstationID(transaction));
		sql.addColumn(FIELD_BUSINESS_DAY_DATE, getBusinessDayString(transaction));
		sql.addColumn(FIELD_TRANSACTION_SEQUENCE_NUMBER, getTransactionSequenceNumber(transaction));
		sql.addColumn(FIELD_RETAIL_TRANSACTION_LINE_ITEM_SEQUENCE_NUMBER, getSequenceNumber(lineItemSequenceNumber));
	    sql.addColumn(FIELD_TENDER_TYPE_CODE, getTenderType(lineItem));
	    sql.addColumn(FIELD_WALLET_ORDER_ID, getPaytmWalletOrderID(lineItem));
	    sql.addColumn(FIELD_TRANS_ID, getpaytmTransactionID(lineItem));
		sql.addColumn(FIELD_MOBILE_NUMBER, getMobileNumber(lineItem));
		sql.addColumn(FIELD_TENDER_LINE_ITEM_AMOUNT, getAmountPaid(lineItem));
	    sql.addColumn(FIELD_RECORD_LAST_MODIFIED_TIMESTAMP, getSQLCurrentTimestampFunction());
        sql.addColumn(FIELD_RECORD_CREATION_TIMESTAMP, getSQLCurrentTimestampFunction());
			
	

		try {
			dataConnection.execute(sql.getSQLString());
		} catch (DataException de) {
			logger.error("" + de.toString() + "");
			throw de;
		} catch (Exception e) {
			throw new DataException(DataException.UNKNOWN, "insertPaytmTenderLineItem", e);
		}
		// Bhanu Priya Changes Ends	
		}
		
	// Changes for POS-Amazon Pay Integration @Purushotham Reddy
	public void  insertAmazonPayTenderLineItem(JdbcDataConnection dataConnection,
			TenderableTransactionIfc transaction, int lineItemSequenceNumber,
			MAXTenderAmazonPayIfc lineItem) throws DataException {
		
		insertTenderLineItem(dataConnection, transaction, lineItemSequenceNumber, lineItem);
		SQLInsertStatement sql = new SQLInsertStatement();

		// Table
		sql.setTable(TABLE_WALLET_TENDER_LINE_ITEM);

		// Fields
		sql.addColumn(FIELD_RETAIL_STORE_ID, getStoreID(transaction));
		sql.addColumn(FIELD_WORKSTATION_ID, getWorkstationID(transaction));
		sql.addColumn(FIELD_BUSINESS_DAY_DATE, getBusinessDayString(transaction));
		sql.addColumn(FIELD_TRANSACTION_SEQUENCE_NUMBER, getTransactionSequenceNumber(transaction));
		sql.addColumn(FIELD_RETAIL_TRANSACTION_LINE_ITEM_SEQUENCE_NUMBER, getSequenceNumber(lineItemSequenceNumber));
	    sql.addColumn(FIELD_TENDER_TYPE_CODE, getTenderType(lineItem));
	    sql.addColumn(FIELD_WALLET_ORDER_ID, getAmazonPayWalletOrderID(lineItem));
	    sql.addColumn(FIELD_TRANS_ID, getAmazonPayTransactionID(lineItem));
		sql.addColumn(FIELD_MOBILE_NUMBER, getAPMobileNumber(lineItem));
		sql.addColumn(FIELD_TENDER_LINE_ITEM_AMOUNT, getAPAmountPaid(lineItem));
		sql.addColumn(FIELD_RECORD_LAST_MODIFIED_TIMESTAMP, getSQLCurrentTimestampFunction());
	    sql.addColumn(FIELD_RECORD_CREATION_TIMESTAMP, getSQLCurrentTimestampFunction());
			
		try 
		{
			dataConnection.execute(sql.getSQLString());
		} 
		catch (DataException de) 
		{
			logger.error("" + de.toString() + "");
			throw de;
		} catch (Exception e) 
		{
			throw new DataException(DataException.UNKNOWN,"insertAmazonPayTenderLineItem", e);
		}
	}
		
	
	private String getpaytmTransactionID(MAXTenderPaytmIfc lineItem) {

			return(lineItem.getPaytmWalletTransactionID());
	}
	
	private String getAmazonPayTransactionID(MAXTenderAmazonPayIfc lineItem) {

		return(makeSafeString(lineItem.getAmazonPayWalletTransactionID()));
	}

	private String getPaytmWalletOrderID(MAXTenderPaytmIfc lineItem) {
	
			return(lineItem.getOrderID());
	}

	private String getAmazonPayWalletOrderID(MAXTenderAmazonPayIfc lineItem) {
	
			return(lineItem.getAmazonPayOrderID());
	}
	
	
	private String getMobileNumber(MAXTenderPaytmIfc lineItem) {
		
			return(lineItem.getPaytmMobileNumber());
	}
	
	private String getAPMobileNumber(MAXTenderAmazonPayIfc lineItem) {
		
		return(lineItem.getAmazonPayMobileNumber());
	}
	
	private String getAmountPaid(MAXTenderPaytmIfc lineItem) {
		
			return(lineItem.getPaytmAmount());
	}
	
	private String getAPAmountPaid(MAXTenderAmazonPayIfc lineItem) {
		
			return(lineItem.getAmazonPayAmount());
	}
	
	
	private String getMobikwikTransactionID(MAXTenderMobikwikIfc lineItem) {

			return(lineItem.getMobikwikWalletTransactionID());
	}

	private String getMobikwikWalletOrderID(MAXTenderMobikwikIfc lineItem) {
		
			return(lineItem.getMobikwikOrderID());
		
	}
	private String getMobikwikMobileNumber(MAXTenderMobikwikIfc lineItem) {
		
			return(lineItem.getMobikwikMobileNumber());
	}
	private String getMobikwikAmountPaid(MAXTenderMobikwikIfc lineItem) {
		
			return(lineItem.getMobikwikAmount());
		
	}
	
	
	
	

	public void insertPurchaseOrderTenderLineItem(JdbcDataConnection dataConnection,
			TenderableTransactionIfc transaction, int lineItemSequenceNumber, MAXTenderPurchaseOrder lineItem)
			throws DataException {
		/*
		 * Update the Tender Line Item table first.
		 */
		insertTenderLineItem(dataConnection, transaction, lineItemSequenceNumber, lineItem);

		SQLInsertStatement sql = new SQLInsertStatement();

		// Table
		sql.setTable(TABLE_PURCHASE_ORDER_TENDER_LINE_ITEM);
		String purchaseOrderNo = "";
		if (getPurchaseOrderNumber(lineItem) != null) {
			purchaseOrderNo = getPurchaseOrderNumber(lineItem);
		} else {
			purchaseOrderNo = "-1";
		}

		// Fields
		sql.addColumn(FIELD_RETAIL_STORE_ID, getStoreID(transaction));
		sql.addColumn(FIELD_WORKSTATION_ID, getWorkstationID(transaction));
		sql.addColumn(FIELD_TRANSACTION_SEQUENCE_NUMBER, getTransactionSequenceNumber(transaction));
		sql.addColumn(FIELD_RETAIL_TRANSACTION_LINE_ITEM_SEQUENCE_NUMBER, getSequenceNumber(lineItemSequenceNumber));
		sql.addColumn(FIELD_BUSINESS_DAY_DATE, getBusinessDayString(transaction));
		sql.addColumn(PURCHASE_ORDER_NUMBER, purchaseOrderNo);
		sql.addColumn(PURCHASE_ORDER_AMOUNT, getPurchaseOrderAmount(lineItem));
		sql.addColumn(PURCHASE_ORDER_AGENCY_NAME, getPurchaseOrderAgencyName(lineItem));
		// sql.addColumn(PURCHASE_ORDER_APPROVAL_CODE,
		// getPurchaseOrderApprovalCode(lineItem));

		try {
			dataConnection.execute(sql.getSQLString());
		} catch (DataException de) {
			logger.error("" + de.toString() + "");
			throw de;
		} catch (Exception e) {
			throw new DataException(DataException.UNKNOWN, "insertPurchaseOrderTenderLineItem", e);
		}
	}

	public String getPurchaseOrderApprovalCode(MAXTenderPurchaseOrder lineItem) {
		return (makeSafeString(lineItem.getApprovalCode()));
	}

	public String getDBStringValue(String value) {
		return (makeSafeString(value));
	}


	protected String getGiftCardAuthorizationCode(MAXTenderGiftCardIfc lineItem) {
		String authCode = lineItem.getAuthorizationCode();
		if (Util.isEmpty(authCode)) {
			if (lineItem.getGiftCard() != null) {
				authCode = lineItem.getGiftCard().getApprovalCode();
			}
		}
		if (Util.isEmpty(authCode)) {
			authCode = "";
		}
		return (inQuotes(authCode));
	}

	protected String getEntryMethod(MAXTenderGiftCardIfc lineItem) { 
		EntryMethod entryMethod = lineItem.getEntryMethod();
		if (Util.isEmpty(entryMethod.toString())) {
			entryMethod = lineItem.getGiftCard().getEntryMethod();
		}
		return (inQuotes(entryMethod.toString()));
	}

	protected String getGiftCardAuthorizationMethod(MAXTenderGiftCardIfc lineItem) { 
		String authMethod = lineItem.getAuthorizationMethod();
		if (authMethod == null) {
			authMethod = "";
		}
		return inQuotes(authMethod);
	} // end getGiftCardAuthorizationCode()

	// ---------------------------------------------------------------------
	/**
	 * Returns initial balance.
	 * 
	 * @param lineItem
	 *            tender gift card line item
	 * @return initial balance string
	 */
	// ---------------------------------------------------------------------
	protected String getGiftCardInitialBalance(TenderGiftCardIfc lineItem) {
		String initialBalance = "0.00";
		if (lineItem.getGiftCard() != null) {
			CurrencyIfc balance = lineItem.getGiftCard().getInitialBalance();

			if (balance.signum() > 0) {
				initialBalance = balance.getStringValue();
			}

		}
		return initialBalance;
	}

	// ---------------------------------------------------------------------
	/**
	 * Returns remaining balance.
	 * 
	 * @param lineItem
	 *            tender gift card line item
	 * @return remaining balance string
	 */
	// ---------------------------------------------------------------------

	protected String getGiftCardRemainingBalance(MAXTenderGiftCardIfc lineItem) {
		String remainingBalance = "0.00";
		if (lineItem.getGiftCard() != null) {
			CurrencyIfc balance = lineItem.getGiftCard().getCurrentBalance();
			remainingBalance = balance.getStringValue();
		}
		return remainingBalance;
	}

	public String getAuthorizationSettlementData(MAXTenderChargeIfc lineItem) {
		return (makeSafeString(lineItem.getSettlementData()));
	}

	/**
	 * Get a safe string for Authorization Date
	 * 
	 * @param lineItem
	 * @return
	 */
	public String getAuthorizationDateTime(MAXTenderChargeIfc lineItem) {
		String date = "null";
		if (lineItem.getAuthorizedDateTime() != null) {
			date = dateToSQLTimestampString(lineItem.getAuthorizedDateTime().dateValue());
		}

		return date;
	}

	protected String getGiftCardRemainingBalance(TenderGiftCardIfc lineItem) {
		String remainingBalance = "0.00";
		if (lineItem.getGiftCard() != null) {
			CurrencyIfc balance = lineItem.getGiftCard().getCurrentBalance();

			if (balance.signum() > 0) {
				remainingBalance = balance.getStringValue();
			}

		}
		return remainingBalance;
	}

	public String getGiftCardSerialNumber(MAXTenderGiftCardIfc lineItem) {
		String cardNumber = null;
		if (lineItem.getCardNumber().length() > 16) {
			cardNumber = lineItem.getCardNumber().substring(0, 16);
		} else {
			cardNumber = lineItem.getCardNumber();
		}
		return makeSafeString(cardNumber);

	}

	public void insertGiftCardTenderLineItem(JdbcDataConnection dataConnection, TenderableTransactionIfc transaction,
			int lineItemSequenceNumber, TenderGiftCardIfc lineItem) throws DataException {
		/*
		 * Update the Tender Line Item table first.
		 */
		String cardNumber = null;
		if (lineItem.getCardNumber().length() > 16) {
			cardNumber = lineItem.getCardNumber().substring(0, 16);
		} else {
			cardNumber = lineItem.getCardNumber();
		}
		insertTenderLineItem(dataConnection, transaction, lineItemSequenceNumber, lineItem);

		insertGiftCard(dataConnection, transaction, lineItem);

		SQLInsertStatement sql = new SQLInsertStatement();

		// Table
		sql.setTable(TABLE_GIFT_CARD_TENDER_LINE_ITEM);

		// Fields
		sql.addColumn(FIELD_RETAIL_STORE_ID, getStoreID(transaction));
		sql.addColumn(FIELD_WORKSTATION_ID, getWorkstationID(transaction));
		sql.addColumn(FIELD_TRANSACTION_SEQUENCE_NUMBER, getTransactionSequenceNumber(transaction));
		sql.addColumn(FIELD_RETAIL_TRANSACTION_LINE_ITEM_SEQUENCE_NUMBER, getSequenceNumber(lineItemSequenceNumber));
		sql.addColumn(FIELD_BUSINESS_DAY_DATE, getBusinessDayString(transaction));
		sql.addColumn(FIELD_GIFT_CARD_SERIAL_NUMBER, cardNumber);
		// Changes starts for Rev 1.1 (Ashish)
		sql.addColumn(FIELD_GIFT_CARD_SERIAL_NUMBER_OLD, cardNumber);
		// Changes ends for Rev 1.1 (Ashish)
		// sql.addColumn(FIELD_ISSUING_STORE_NUMBER,
		// getIssuingStoreNumber(lineItem));
		sql.addColumn(FIELD_GIFT_CARD_ADJUDICATION_CODE, getGiftCardAuthorizationCode(lineItem));
		sql.addColumn(FIELD_TENDER_AUTHORIZATION_CARD_NUMBER_SWIPED_OR_KEYED_CODE, getEntryMethod(lineItem));
		sql.addColumn(FIELD_AUTHORIZATION_METHOD_CODE, getGiftCardAuthorizationMethod(lineItem));
		sql.addColumn(FIELD_GIFT_CARD_INITIAL_BALANCE, getGiftCardInitialBalance(lineItem));
		sql.addColumn(FIELD_GIFT_CARD_CURRENT_BALANCE, getGiftCardRemainingBalance(lineItem));
		sql.addColumn(FIELD_GIFT_CARD_CREDIT_FLAG, makeStringFromBoolean(lineItem.isGiftCardCredit()));
		// Commented For upgradation
		// sql.addColumn(FIELD_GIFT_CARD_REQUEST_TYPE,
		// inQuotes(String.valueOf(lineItem.getRequestType())));
		sql.addColumn(FIELD_CURRENCY_ID, lineItem.getAmountTender().getType().getCurrencyId());
		sql.addColumn(FIELD_TENDER_AUTHORIZATION_SETTLEMENT_DATA, getAuthorizationSettlementData(lineItem));
		sql.addColumn(FIELD_TENDER_AUTHORIZATION_DATE_TIME, getAuthorizationDateTime(lineItem));
		sql.addColumn(FIELD_MASKED_GIFT_CARD_SERIAL_NUMBER, getMaskedGiftCardSerialNumber(lineItem));

		try {
			MAXGiftCard gc = (MAXGiftCard) lineItem.getGiftCard(); 
			sql.addColumn(FIELD_GIFT_CARD_AUTHORIZATION_CODE, makeSafeString(gc.getQcApprovalCode()));

			sql.addColumn(FIELD_GIFT_CARD_EXP_DATE, getAuthorizationDateTime(gc.getExpirationDate()));

			sql.addColumn(FIELD_GIFT_CARD_TYPE, makeSafeString(gc.getQcCardType()));

			sql.addColumn(FIELD_GIFT_CARD_TRANS_ID, makeSafeString(gc.getQcTransactionId()));
			sql.addColumn(FIELD_GIFT_CARD_INVOICE_ID, makeSafeString((gc).getQcInvoiceNumber()));

			sql.addColumn(FIELD_GIFT_CARD_BATCH_ID, makeSafeString((gc).getQcBatchNumber()));
		} catch (Exception e) {

		}

		try {
			dataConnection.execute(sql.getSQLString());
		} catch (DataException de) {
			logger.error("" + de.toString() + "");
			throw de;
		} catch (Exception e) {
			throw new DataException(DataException.UNKNOWN, "insertGiftCardTenderLineItem", e);
		}
	}

	public void insertGiftCard(JdbcDataConnection dataConnection, TenderableTransactionIfc transaction,
			TenderGiftCardIfc lineItem) throws DataException {
		// Get gift card
		MAXGiftCardIfc giftCard = (MAXGiftCardIfc) lineItem.getGiftCard();

		SQLInsertStatement sql = new SQLInsertStatement();

		// Table
		sql.setTable(TABLE_GIFT_CARD);

		// Fields
		sql.addColumn(FIELD_RETAIL_STORE_ID, getStoreID(transaction));
		sql.addColumn(FIELD_WORKSTATION_ID, getWorkstationID(transaction));
		sql.addColumn(FIELD_TRANSACTION_SEQUENCE_NUMBER, getTransactionSequenceNumber(transaction));
		sql.addColumn(FIELD_BUSINESS_DAY_DATE, getBusinessDayString(transaction));
		sql.addColumn(FIELD_RETAIL_TRANSACTION_LINE_ITEM_SEQUENCE_NUMBER, getSequenceNumber(lineItem.getLineNumber()));
		// Changes for Rev 1.0 : Starts
		sql.addColumn(FIELD_GIFT_CARD_SERIAL_NUMBER, getGiftCardSerialNumber(lineItem));
		// Changes starts for Rev 1.2 (Ashish)
		sql.addColumn(FIELD_GIFT_CARD_SERIAL_NUMBER_OLD, getGiftCardSerialNumber(lineItem));
				// Changes ends for Rev 1.2 (Ashish)
		sql.addColumn(FIELD_MASKED_GIFT_CARD_SERIAL_NUMBER, getMaskedGiftCardSerialNumber(lineItem));
		// Changes for Rev 1.0 : Ends
		sql.addColumn(FIELD_GIFT_CARD_ACTIVATION_ADJUDICATION_CODE, makeSafeString(giftCard.getApprovalCode()));
		sql.addColumn(FIELD_GIFT_CARD_ENTRY_METHOD, inQuotes(giftCard.getEntryMethod().toString()));
		sql.addColumn(FIELD_GIFT_CARD_REQUEST_TYPE, inQuotes(String.valueOf(giftCard.getRequestType())));

		if (giftCard.getCurrentBalance().signum() < 0) {
			sql.addColumn(FIELD_GIFT_CARD_CURRENT_BALANCE, "0.00");
		} else
			sql.addColumn(FIELD_GIFT_CARD_CURRENT_BALANCE, giftCard.getCurrentBalance().getStringValue());
		sql.addColumn(FIELD_GIFT_CARD_INITIAL_BALANCE, giftCard.getInitialBalance().getStringValue());
		// +I18N
		sql.addColumn(FIELD_CURRENCY_ID, giftCard.getInitialBalance().toString());
		// -I18N
		if (giftCard.getSettlementData() == null)
			sql.addColumn(FIELD_TENDER_AUTHORIZATION_SETTLEMENT_DATA, makeSafeString(giftCard.getSettlementData()));
		sql.addColumn(FIELD_TENDER_AUTHORIZATION_DATE_TIME, getAuthorizationDateTime(giftCard.getAuthorizedDateTime()));

		try {
			MAXGiftCardIfc gc = (MAXGiftCardIfc) lineItem.getGiftCard();

			sql.addColumn(FIELD_GIFT_CARD_AUTHORIZATION_CODE, makeSafeString(gc.getQcApprovalCode()));

			sql.addColumn(FIELD_GIFT_CARD_INVOICE_ID, makeSafeString((gc).getQcInvoiceNumber()));

			sql.addColumn(FIELD_GIFT_CARD_TRANS_ID, makeSafeString((gc).getQcTransactionId()));

			sql.addColumn(FIELD_GIFT_CARD_BATCH_ID, makeSafeString((gc).getQcBatchNumber()));

			sql.addColumn(FIELD_GIFT_CARD_EXP_DATE, getAuthorizationDateTime(gc.getExpirationDate()));

			sql.addColumn(FIELD_GIFT_CARD_TYPE, makeSafeString(gc.getQcCardType()));

		} catch (Exception e) {

		}

		try {
			dataConnection.execute(sql.getSQLString());
		} catch (DataException de) {
			// logger.error("" + de + "");
			throw de;
		} catch (Exception e) {
			throw new DataException(DataException.UNKNOWN, "insertGiftCard", e);
		}
	}

	public void insertLoyaltyPointsTenderLineItem(JdbcDataConnection dataConnection,
			TenderableTransactionIfc transaction, int lineItemSequenceNumber, MAXTenderLoyaltyPointsIfc lineItem)
			throws DataException {
		/*
		 * Update the Tender Line Item table first.
		 */
		insertTenderLineItem(dataConnection, transaction, lineItemSequenceNumber, lineItem);
		SQLInsertStatement sql = new SQLInsertStatement();

		// Table
		sql.setTable(TABLE_LOYALTY_POINT_TENDER_LINE_ITEM);

		// Fields
		sql.addColumn(FIELD_RETAIL_STORE_ID, getStoreID(transaction));
		sql.addColumn(FIELD_WORKSTATION_ID, getWorkstationID(transaction));
		sql.addColumn(FIELD_TRANSACTION_SEQUENCE_NUMBER, getTransactionSequenceNumber(transaction));
		sql.addColumn(FIELD_RETAIL_TRANSACTION_LINE_ITEM_SEQUENCE_NUMBER, getSequenceNumber(lineItemSequenceNumber));
		sql.addColumn(FIELD_BUSINESS_DAY_DATE, getBusinessDayString(transaction));
		sql.addColumn(FIELD_LOYALTY_CARD_NUMBER, getLoyaltyCardNumber(lineItem));
		sql.addColumn(FIELD_LOYALTY_POINT_AMOUNT, getLoyaltyPointAmount(lineItem));

		try {
			dataConnection.execute(sql.getSQLString());
		} catch (DataException de) {
			logger.error("" + de.toString() + "");
			throw de;
		} catch (Exception e) {
			throw new DataException(DataException.UNKNOWN, "insertLoyaltyPointsTenderLineItem", e);
		}
	}

	public String getLoyaltyCardNumber(MAXTenderLoyaltyPointsIfc lineItem) {
		return makeSafeString(lineItem.getLoyaltyCardNumber());
	}

	public String getLoyaltyPointAmount(MAXTenderLoyaltyPointsIfc lineItem) {
		return lineItem.getLoyaltyPointAmount().toString();
	}

	public void insertStoreCreditTenderLineItem(JdbcDataConnection dataConnection,
			TenderableTransactionIfc transaction, int lineItemSequenceNumber, TenderStoreCreditIfc lineItem)
			throws DataException {
		logger.info("Inside insertStoreCreditTenderLineItem");
		/*
		 * Update the Tender Line Item table first.
		 */
		insertTenderLineItem(dataConnection, transaction, lineItemSequenceNumber, lineItem);
		/**
		 * if (transaction.getTransactionType() == TransactionIfc.TYPE_VOID) {
		 * //TenderableTransactionIfc origTransaction = ((VoidTransactionIfc)
		 * transaction).getOriginalTransaction();
		 * //deleteVoidStoreCredit(dataConnection, origTransaction, lineItem);
		 * // TODO: left in for debugging, please remove once code is fixed. }
		 * else {
		 **/
		// Changes starts for Rev 1.5
	//	boolean eWalletFlag=false;
		logger.info("Before EWallet Tender");
		logger.info("Condition Value:- "+!((MAXSaleReturnTransaction)transaction).isEWalletTenderFlag());
		if(!((MAXSaleReturnTransaction)transaction).isEWalletTenderFlag())
		{
			logger.info(" (insertStoreCreditTenderLineItem) Inside EWallet Tender");
			// Changes End for Rev 1.5
		SQLInsertStatement sql = new SQLInsertStatement();

		// Table
		sql.setTable(TABLE_STORE_CREDIT_TENDER_LINE_ITEM);

		// Fields
		sql.addColumn(FIELD_RETAIL_STORE_ID, getStoreID(transaction));
		sql.addColumn(FIELD_WORKSTATION_ID, getWorkstationID(transaction));
		sql.addColumn(FIELD_TRANSACTION_SEQUENCE_NUMBER, getTransactionSequenceNumber(transaction));
		sql.addColumn(FIELD_RETAIL_TRANSACTION_LINE_ITEM_SEQUENCE_NUMBER, getSequenceNumber(lineItemSequenceNumber));
		sql.addColumn(FIELD_BUSINESS_DAY_DATE, getBusinessDayString(transaction));

		sql.addColumn(FIELD_STORE_CREDIT_ID, getStoreCreditNumber(lineItem));
		sql.addColumn(FIELD_STORE_CREDIT_BALANCE, getStoreCreditAmount(lineItem));
		//sql.addColumn(FIELD_STORE_CREDIT_TENDER_STATE, getStoreCreditState(lineItem));
		if(!(getStoreCreditState(lineItem).equals("") || getStoreCreditState(lineItem).equals("''"))){
			sql.addColumn(FIELD_STORE_CREDIT_TENDER_STATE, getStoreCreditState(lineItem));
		}else{
			sql.addColumn(FIELD_STORE_CREDIT_TENDER_STATE, makeSafeString("REDEEM"));
		}
		sql.addColumn(FIELD_STORE_CREDIT_FIRST_NAME, getStoreCreditFirstName(lineItem));
		sql.addColumn(FIELD_STORE_CREDIT_LAST_NAME, getStoreCreditLastName(lineItem));
		sql.addColumn(FIELD_STORE_CREDIT_ID_TYPE, makeSafeString("PAN Card"));
		if (((TenderAlternateCurrencyIfc) lineItem).getAlternateCurrencyTendered() != null) {
			sql.addColumn(FIELD_STORE_CREDIT_FOREIGN_FACE_VALUE_AMOUNT, ((TenderAlternateCurrencyIfc) lineItem)
					.getAlternateCurrencyTendered().toString());
			// +I18N
			sql.addColumn(FIELD_CURRENCY_ID, ((TenderAlternateCurrencyIfc) lineItem).getAlternateCurrencyTendered()
					.getType().getCurrencyId());
			// -I18N
		} else {
			// NOT NULL field designation
			sql.addColumn(FIELD_STORE_CREDIT_FOREIGN_FACE_VALUE_AMOUNT, "0.00");
			// +I18N
			sql.addColumn(FIELD_CURRENCY_ID, lineItem.getAmount().getType().getCurrencyId());
			// -I18N
		}

		try {
			dataConnection.execute(sql.getSQLString());
		} catch (DataException de) {
			logger.error(de.toString());
			throw de;
		} catch (Exception e) {
			throw new DataException(DataException.UNKNOWN, "insertStoreCreditTenderLineItem", e);
		}
		}
		logger.info("Outside EWallet Tender");
	}

	public String getStoreCreditFirstName(TenderStoreCreditIfc lineItem) {
		String fName = lineItem.getFirstName();
		if (fName == null || fName.equals(""))
			fName = "First Name";
		return (makeSafeString(fName));
	}

	public String getStoreCreditLastName(TenderStoreCreditIfc lineItem) {
		String lName = lineItem.getLastName();
		if (lName == null || lName.equals(""))
			lName = "Last Name";
		return (makeSafeString(lName));
	}

	public void insertCreditDebitCardTenderLineItem(JdbcDataConnection dataConnection,
			TenderableTransactionIfc transaction, int lineItemSequenceNumber, MAXTenderChargeIfc lineItem)
			throws DataException {
		// Update the Tender Line Item table first.
		insertTenderLineItem(dataConnection, transaction, lineItemSequenceNumber, lineItem);
		String cardStatus=null;
		if (lineItem.getResponseDate() != null && lineItem.getResponseDate().size() != 0) {
		cardStatus=lineItem.getResponseDate().get("HostResponseMessage").toString(); }

		SQLInsertStatement sql = new SQLInsertStatement();

		// Table
		sql.setTable(TABLE_CREDIT_DEBIT_CARD_TENDER_LINE_ITEM);

		// Fields
		sql.addColumn(FIELD_RETAIL_STORE_ID, getStoreID(transaction));
		sql.addColumn(FIELD_WORKSTATION_ID, getWorkstationID(transaction));
		sql.addColumn(FIELD_TRANSACTION_SEQUENCE_NUMBER, getTransactionSequenceNumber(transaction));
		sql.addColumn(FIELD_RETAIL_TRANSACTION_LINE_ITEM_SEQUENCE_NUMBER, getSequenceNumber(lineItemSequenceNumber));
		sql.addColumn(FIELD_BUSINESS_DAY_DATE, getBusinessDayString(transaction));
		sql.addColumn(FIELD_TENDER_TYPE_CODE, getTenderType(lineItem));
		sql.addColumn(FIELD_TENDER_AUTHORIZATION_DEBIT_CREDIT_CARD_ACCOUNT_MASKED,
				makeSafeString(lineItem.getMaskedCardNumber()));
		sql.addColumn(FIELD_TENDER_AUTHORIZATION_ACCOUNT_NUMBER_TOKEN, makeSafeString(lineItem.getAccountNumberToken()));
		//sql.addColumn(FIELD_TENDER_AUTHORIZATION_CREDIT_DEBIT_CARD_ADJUDICATION_CODE, getAuthorizationCode(lineItem));
		if (cardStatus!=null && cardStatus.equals("APPROVED")) {
			sql.addColumn(FIELD_TENDER_AUTHORIZATION_CARD_NUMBER_SWIPED_OR_KEYED_CODE, makeSafeString("Swipe"));
			sql.addColumn(FIELD_TENDER_AUTHORIZATION_METHOD_CODE, makeSafeString("Online"));
		}
		//changes for paytmQR
		else if(getCardType(lineItem) != null && (getCardType(lineItem).equalsIgnoreCase("PAYTM_QR"))){
			sql.addColumn(FIELD_TENDER_AUTHORIZATION_METHOD_CODE, lineItem.getAuthCode());
        	logger.info("FIELD_TENDER_AUTHORIZATION_METHOD_CODE 915 :"+lineItem.getAuthCode());
			sql.addColumn(FIELD_MASKED_ACC_ID, makeSafeString("506275******4444"));
		}
		else {
		sql.addColumn(FIELD_TENDER_AUTHORIZATION_CARD_NUMBER_SWIPED_OR_KEYED_CODE, getEntryMethod(lineItem));
			sql.addColumn(FIELD_TENDER_AUTHORIZATION_METHOD_CODE, getMethodCode(lineItem));
		}		
		sql.addColumn(FIELD_TENDER_AUTHORIZATION_TENDER_MEDIA_ISSUER_ID, getCardType(lineItem));
		sql.addColumn(FIELD_TENDER_AUTHORIZATION_CARD_TYPE, getTenderDescription(lineItem));
		//sql.addColumn(FIELD_TENDER_AUTHORIZATION_METHOD_CODE, getMethodCode(lineItem));
		sql.addColumn(FIELD_TENDER_AUTHORIZATION_DEBIT_CREDIT_ID_TYPE, makeSafeString(lineItem.getPersonalIDType()
				.getCode()));
		sql.addColumn(FIELD_TENDER_AUTHORIZATION_DEBIT_CREDIT_ID_COUNTRY, getIDCountry(lineItem));
		sql.addColumn(FIELD_TENDER_AUTHORIZATION_DEBIT_CREDIT_ID_STATE, getIDState(lineItem));
		sql.addColumn(FIELD_TENDER_AUTHORIZATION_DEBIT_CREDIT_ID_EXPIRATION_DATE, getIDExpirationDate(lineItem));
		sql.addColumn(FIELD_TENDER_AUTHORIZATION_SETTLEMENT_DATA, getAuthorizationSettlementData(lineItem));
		sql.addColumn(FIELD_TENDER_AUTHORIZATION_DATE_TIME, getAuthorizationDateTime(lineItem));
		
		sql.addColumn(FIELD_ACQUIRER_BANK_CODE, getCreditDebitBankCode(lineItem));
		String responseCode=null;
		if (lineItem.getResponseDate() != null && lineItem.getResponseDate().size() != 0) {
			HashMap responseDataAfterCardSwipe = lineItem.getResponseDate();
			if (responseDataAfterCardSwipe.get("MerchantID") != null)
				sql.addColumn(FIELD_MERCHANT_ID_INNOVITI,
						makeSafeString(responseDataAfterCardSwipe.get("MerchantID").toString()));
			if(responseDataAfterCardSwipe.get("StateTID") != null)
				sql.addColumn(FIELD_TERMINAL_ID, makeSafeString(responseDataAfterCardSwipe.get("StateTID").toString()));
			if(responseDataAfterCardSwipe.get("StateTransactionTime") != null)
				sql.addColumn(FIELD_TRANSACTION_APPROVAL_TIME,
					makeSafeString(responseDataAfterCardSwipe.get("StateTransactionTime").toString()));
			if(responseDataAfterCardSwipe.get("StateBatchNumber") != null)
				sql.addColumn(FIELD_BATCH_NUMBER,
					makeSafeString(responseDataAfterCardSwipe.get("StateBatchNumber").toString()));
			if(responseDataAfterCardSwipe.get("HostResponseCode") != null){
				sql.addColumn(FIELD_HOST_RESPONSE_ID,
					makeSafeString(responseDataAfterCardSwipe.get("HostResponseCode").toString()));
			}
			if(responseDataAfterCardSwipe.get("StateInvoiceNumber") != null)
				sql.addColumn(FIELD_INVOICE_NUMBERCRE,
					makeSafeString(responseDataAfterCardSwipe.get("StateInvoiceNumber").toString()));
			if(responseDataAfterCardSwipe.get("HostResponseMessage") != null){
				responseCode = makeSafeString(responseDataAfterCardSwipe.get("HostResponseMessage").toString());
				sql.addColumn(FIELD_HOST_RESPONSE_MESSAGE,
					makeSafeString(responseDataAfterCardSwipe.get("HostResponseMessage").toString()));
			}
			if(responseDataAfterCardSwipe.get("HostResponseApprovalCode") != null){
				sql.addColumn(FIELD_APPROVAL_ID,
					makeSafeString(responseDataAfterCardSwipe.get("HostResponseApprovalCode").toString()));
			sql.addColumn(FIELD_TENDER_AUTHORIZATION_CREDIT_DEBIT_CARD_ADJUDICATION_CODE, makeSafeString(responseDataAfterCardSwipe.get("HostResponseApprovalCode").toString()));
			}
			if(responseDataAfterCardSwipe.get("HostResponseRetrievelRefNumber") != null)
				sql.addColumn(FIELD_RETURN_REFERENCE_NUMBER,
					makeSafeString(responseDataAfterCardSwipe.get("HostResponseRetrievelRefNumber").toString()));
			//Changes starts for Rev 1.2 (Vaibhav ; PineLab)
			if(responseDataAfterCardSwipe.get("EDCType") != null && responseDataAfterCardSwipe.get("EDCType") != "" && responseDataAfterCardSwipe.get("EDCType").toString().equalsIgnoreCase("PLUTUS")){
				sql.addColumn(FIELD_CRDB_DEVICE_DETAIL,makeSafeString("P"));
			}
			else{
				sql.addColumn(FIELD_CRDB_DEVICE_DETAIL,makeSafeString("U"));
			}
			//Changes ends for Rev 1.2 (Vaibhav ; PineLab)
		}
		else if(getCardType(lineItem) != null && ((lineItem.getCardType()).equalsIgnoreCase("PAYTM_QR"))) {
         logger.info("Inside PAYTM_QR 976:");
			sql.addColumn(FIELD_APPROVAL_ID,makeSafeString(lineItem.getRrnNumber()));
			logger.info("FIELD_APPROVAL_ID :"+makeSafeString(lineItem.getRrnNumber()));
			sql.addColumn(FIELD_TENDER_AUTHORIZATION_METHOD_CODE, makeSafeString("Online"));
			sql.addColumn(FIELD_BATCH_NUMBER,makeSafeString(lineItem.getBatchNumber()));
			logger.info("Batch number :"+lineItem.getBatchNumber());
			sql.addColumn(FIELD_TERMINAL_ID,makeSafeString(lineItem.getTID()));
			logger.info("Terminal ID :"+lineItem.getTID());
			sql.addColumn(FIELD_ACQUIRER_BANK_CODE, getCreditDebitBankCode(lineItem));
			sql.addColumn(FIELD_MERCHANT_ID_INNOVITI,makeSafeString(lineItem.getMerchID()));
			sql.addColumn(FIELD_TRANSACTION_APPROVAL_TIME,makeSafeString(lineItem.getTenderDateTime()));
			sql.addColumn(FIELD_HOST_RESPONSE_ID,makeSafeString(lineItem.getHostReference()));
			sql.addColumn(FIELD_INVOICE_NUMBERCRE,makeSafeString(lineItem.getInvoiceNumber()));
			sql.addColumn(FIELD_RETURN_REFERENCE_NUMBER,makeSafeString(lineItem.getRetrievalReferenceNumber()));
			sql.addColumn(FIELD_LAST_FOUR_DIGIT,makeSafeString(((MAXTenderChargeIfc) lineItem).getLastFourDigits()));
			
			sql.addColumn(FIELD_MASKED_ACC_ID, makeSafeString("506275******4444"));		}
		else{
			sql.addColumn(FIELD_TENDER_AUTHORIZATION_CREDIT_DEBIT_CARD_ADJUDICATION_CODE, getCreditDebitOfflineAuthCode(lineItem));
		}
		if (lineItem.isEmiTransaction()) {
			sql.addColumn(FIELD_TENDER_AUTHORIZATION_DEBIT_CREDIT_ID_TYPE, "1");
		}
		//changes for paytmQR
		else if(lineItem.getPaytmUPIorWalletPaytment() != null && lineItem.getPaytmUPIorWalletPaytment().equalsIgnoreCase("UPI")) {
			sql.addColumn(FIELD_TENDER_AUTHORIZATION_DEBIT_CREDIT_ID_TYPE, "2");
		}else if(lineItem.getPaytmUPIorWalletPaytment() != null && lineItem.getPaytmUPIorWalletPaytment().equalsIgnoreCase("WALLET")) {
			sql.addColumn(FIELD_TENDER_AUTHORIZATION_DEBIT_CREDIT_ID_TYPE, "3");
		}
		else {
			sql.addColumn(FIELD_TENDER_AUTHORIZATION_DEBIT_CREDIT_ID_TYPE, "0");
		}
		sql.addColumn(FIELD_TENDER_AUTHORIZATION_MESSAGE_SEQUENCE_NUMBER, getReferenceCode(lineItem));
		sql.addColumn(FIELD_TENDER_AUTHORIZATION_DATE, getAuthorizationDate(lineItem));
		sql.addColumn(FIELD_TENDER_AUTHORIZATION_TIME, getAuthorizationTime(lineItem));
		sql.addColumn(FIELD_TENDER_AUTHORIZATION_RETRIEVAL_REFERENCE_NUMBER, getRetrievalReferenceNumber(lineItem));
		if(getAuthResponseCode(lineItem) != null && !getAuthResponseCode(lineItem).equalsIgnoreCase("")){
		sql.addColumn(FIELD_TENDER_AUTHORIZATION_RESPONSE_CODE, getAuthResponseCode(lineItem));
		}else{
			sql.addColumn(FIELD_TENDER_AUTHORIZATION_RESPONSE_CODE, responseCode);  
		}
		sql.addColumn(FIELD_TENDER_AUTHORIZATION_ORIGINAL_AUTHORIZATION_ACCOUNT_DATA_SOURCE_CODE,
				getAccountDataSource(lineItem));
		sql.addColumn(FIELD_TENDER_AUTHORIZATION_ORIGINAL_AUTHORIZATION_PAYMENT_SERVICE_INDICATOR,
				getPaymentServiceIndicator(lineItem));
		sql.addColumn(FIELD_TENDER_AUTHORIZATION_ORIGINAL_AUTHORIZATION_TRANSACTION_IDENTIFICATION_NUMBER,
				getTransactionIdentificationNumber(lineItem));
		sql.addColumn(FIELD_TENDER_AUTHORIZATION_ORIGINAL_AUTHORIZATION_VALIDATION_CODE, getValidationCode(lineItem));
		sql.addColumn(FIELD_TENDER_AUTHORIZATION_ORIGINAL_AUTHORIZATION_SOURCE_CODE, getAuthorizationSource(lineItem));
		sql.addColumn(FIELD_TENDER_AUTHORIZATION_ORIGINAL_HOST_TRANSACTION_REFERENCE_NUMBER, getHostReference(lineItem));
		sql.addColumn(FIELD_TENDER_AUTHORIZATION_TRANSACTION_TRACE_NUMBER, getTraceNumber(lineItem));
		sql.addColumn(FIELD_TENDER_REMAINING_PREPAID_BALANCE_AMOUNT, getPrepaidRemainingBalanceAmount(lineItem));
		sql.addColumn(FIELD_TENDER_AUTHORIZATION_TRANSACTION_KSN_NUMBER, getKSN_20(lineItem));
		sql.addColumn(FIELD_TENDER_AUTHORIZATION_AFTER_PROMOTION_ACCOUNT_APR, getAccountAPR(lineItem));
		sql.addColumn(FIELD_TENDER_AUTHORIZATION_AFTER_PROMOTION_ACCOUNT_APR_TYPE, getAccountAPRType(lineItem));
		sql.addColumn(FIELD_TENDER_AUTHORIZATION_DURING_PROMOTION_APR, getPromotionAPR(lineItem));
		sql.addColumn(FIELD_TENDER_AUTHORIZATION_DURING_PROMOTION_APR_TYPE, getPromotionAPRType(lineItem));
		sql.addColumn(FIELD_TENDER_AUTHORIZATION_PROMOTION_DESCRIPTION, getPromotionDescription(lineItem));
		sql.addColumn(FIELD_TENDER_AUTHORIZATION_PROMOTION_DURATION, getPromotionDuration(lineItem));
		sql.addColumn(FIELD_TENDER_AUTHORIZATION_JOURNAL_KEY, getAuthorizationJournalKey(lineItem));
		//sql.addColumn(FIELD_ACQUIRER_BANK_CODE, makeSafeString(((MAXTenderChargeIfc) lineItem).getAcquiringBankCode()));
		//sql.addColumn(FIELD_INVOICE_NUMBERCRE, makeSafeString(((MAXTenderChargeIfc) lineItem).getInvoiceNumber()));		
		if(makeSafeString(((MAXTenderChargeIfc) lineItem).getTransactionAcquirer()) != null && !makeSafeString(((MAXTenderChargeIfc) lineItem).getTransactionAcquirer()).equalsIgnoreCase("")){
		sql.addColumn(FIELD_TRANSACTION_ACQUIRER,
				makeSafeString(((MAXTenderChargeIfc) lineItem).getTransactionAcquirer()));
		}else{
			sql.addColumn(FIELD_TRANSACTION_ACQUIRER,getCardType(lineItem)); 
		}
		//sql.addColumn(FIELD_BATCH_NUMBER, makeSafeString(((MAXTenderChargeIfc) lineItem).getBatchNumber()));
		//sql.addColumn(FIELD_TERMINAL_ID, makeSafeString(((MAXTenderChargeIfc) lineItem).getTID()));
		sql.addColumn(FIELD_RETRIVAL_REF_NO, makeSafeString(((MAXTenderChargeIfc) lineItem).getRetrievalRefNumber()));
		sql.addColumn(FIELD_LAST_FOUR_DIGIT, makeSafeString(((MAXTenderChargeIfc) lineItem).getLastFourDigits()));
		sql.addColumn(FIELD_CARD_HOLDER_NAME, makeSafeString(lineItem.getBearerName().getFirstName()));
		try {
			dataConnection.execute(sql.getSQLString());
			HashMap<String, Object> map = new HashMap<String, Object>(5);
			map.put(FIELD_RETAIL_STORE_ID, transaction.getWorkstation().getStoreID());
			map.put(FIELD_WORKSTATION_ID, transaction.getWorkstation().getWorkstationID());
			map.put(FIELD_TRANSACTION_SEQUENCE_NUMBER, Integer.valueOf(getTransactionSequenceNumber(transaction)));
			map.put(FIELD_RETAIL_TRANSACTION_LINE_ITEM_SEQUENCE_NUMBER,
					Integer.valueOf(getSequenceNumber(lineItemSequenceNumber)));
			SimpleDateFormat format = new SimpleDateFormat(JdbcUtilities.YYYYMMDD_DATE_FORMAT_STRING);
			String businessDate = format.format(transaction.getBusinessDay().dateValue());
			map.put(FIELD_BUSINESS_DAY_DATE, businessDate);
			DatabaseBlobHelperIfc helper = DatabaseBlobHelperFactory.getInstance().getDatabaseBlobHelper(
					dataConnection.getConnection());
			Point[] signaturePoints = (Point[]) lineItem.getSignatureData();
			String signatureData = ImageUtils.getInstance().convertPointArrayToXYString(signaturePoints);
			if (helper != null) {
				helper.updateBlob(dataConnection.getConnection(), TABLE_CREDIT_DEBIT_CARD_TENDER_LINE_ITEM,
						FIELD_TENDER_AUTHORIZATION_CUSTOMER_SIGNATURE_IMAGE, signatureData.getBytes(), map);
			}
			// save ICC details of chip card
			if (lineItem.getICCDetails() != null) {
				IntegratedChipCardDetailsDAOIfc dao = (IntegratedChipCardDetailsDAOIfc) BeanLocator
						.getPersistenceBean(IntegratedChipCardDetailsDAOIfc.DAO_BEAN_KEY);
				dao.persist(transaction.getWorkstation().getStoreID(), transaction.getWorkstation().getWorkstationID(),
						businessDate, transaction.getTransactionSequenceNumber(), lineItemSequenceNumber,
						lineItem.getICCDetails());
			}
		} catch (DataException de) {
			logger.error(de);
			throw de;
		} catch (SQLException se) {
			throw new DataException(DataException.SQL_ERROR, "insertCreditDebitCardTenderLineItem", se);
		} catch (Exception e) {
			throw new DataException(DataException.UNKNOWN, "insertCreditDebitCardTenderLineItem", e);
		}
	}

	public String getAuthorizationCode(MAXAuthorizableTenderIfc lineItem) {
		return (makeSafeString(lineItem.getAuthorizationCode()));
	}

	public String getEntryMethod(MAXTenderChargeIfc lineItem) {
		return (makeSafeString(lineItem.getEntryMethod().toString()));
	}

	public String getCardType(MAXTenderChargeIfc lineItem) {
		return (makeSafeString(lineItem.getCardType()));
	}

	public String getTenderDescription(MAXTenderChargeIfc lineItem) {
		return (makeSafeString(lineItem.getTypeDescriptorString()));
	}

	public String getExpirationDate(MAXTenderChargeIfc lineItem) throws ParseException {
		String dateString = lineItem.getExpirationDateString(); // short date
																// string
		// Expiration date is parameter driven, so it may not be set
		if (dateString != null) {
			if (dateString.indexOf('/') == -1 && dateString.length() > 4) {
				dateString = dateString.substring(0, 2) + "/" + dateString.substring(2);
			}

			Date expDate = null;
			try {
				expDate = dateTimeService.parseDate(dateString, LocaleMap.getLocale(LocaleConstantsIfc.DEFAULT_LOCALE),
						JdbcUtilities.CREDIT_CARD_EXPIRATION_DATE_FORMAT);
			} catch (ParseException e) {
				expDate = dateTimeService.parseDate(dateString, LocaleMap.getLocale(LocaleConstantsIfc.DEFAULT_LOCALE),
						JdbcUtilities.CREDIT_CARD_EXPIRATION_YYMM_DATE_FORMAT);
			}
			dateString = dateToSQLDateFunction(expDate); // format
		}
		return dateString;
	}

	public String getSignatureData(MAXTenderChargeIfc lineItem) {
		Point[] data = (Point[]) lineItem.getSignatureData();
		StringBuffer value = new StringBuffer();

		if (data != null) {
			Point p = null;
			for (int i = 0; i < data.length; i++) {
				p = data[i];
				value.append("x" + Integer.toString(p.x) + "y" + Integer.toString(p.y));
			}
		} else {
			value.append("null");
		}
		return value.toString();
	}

	public String getMethodCode(MAXAuthorizableTenderIfc lineItem) {
		return makeSafeString(lineItem.getAuthorizationMethod());
	}

	public String getIDExpirationDate(MAXTenderChargeIfc lineItem) {
		String date = "null";
		if (lineItem.getIDExpirationDate() != null) {
			date = dateToSQLTimestampString(lineItem.getIDExpirationDate().dateValue());
		}

		return date;
	}

	public String getIDCountry(MAXTenderChargeIfc lineItem) {
		return (makeSafeString(lineItem.getIDCountry()));
	}

	/**
	 * Returns the type of ID
	 * <p>
	 * 
	 * @param lineItem
	 *            the tender line item
	 * @return the state of origin of ID
	 */
	// ---------------------------------------------------------------------
	public String getIDState(MAXTenderChargeIfc lineItem) {
		return (makeSafeString(lineItem.getIDState()));
	}

	/**
	 * Returns the type of ID
	 * <p>
	 * 
	 * @param lineItem
	 *            the tender line item
	 * @return the type of ID
	 */
	// ---------------------------------------------------------------------
	public String getIDType(MAXTenderChargeIfc lineItem) {
		// Commented For upgradation
		// return (makeSafeString(lineItem.getIDType()));
		return null;
	}
	
	public void insertCreditDebitCardTenderLineItem(JdbcDataConnection dataConnection,
			TenderableTransactionIfc transaction, int lineItemSequenceNumber, MAXTenderDebitIfc lineItem)
			throws DataException {
		insertTenderLineItem(dataConnection, transaction, lineItemSequenceNumber, lineItem);
	 // Changes Start by Bhanu Priya 
		SQLInsertStatement sql = new SQLInsertStatement();
		sql.setTable(TABLE_CREDIT_DEBIT_CARD_TENDER_LINE_ITEM);
		sql.addColumn(FIELD_RETAIL_STORE_ID, getStoreID(transaction));
		sql.addColumn(FIELD_WORKSTATION_ID, getWorkstationID(transaction));
		sql.addColumn(FIELD_TRANSACTION_SEQUENCE_NUMBER, getTransactionSequenceNumber(transaction));
		sql.addColumn(FIELD_RETAIL_TRANSACTION_LINE_ITEM_SEQUENCE_NUMBER, getSequenceNumber(lineItemSequenceNumber));
		sql.addColumn(FIELD_BUSINESS_DAY_DATE, getBusinessDayString(transaction));
		sql.addColumn(FIELD_TENDER_TYPE_CODE, getTenderType(lineItem));
		sql.addColumn(FIELD_TENDER_AUTHORIZATION_DEBIT_CREDIT_CARD_ACCOUNT_NUMBER,
				makeSafeString(lineItem.getMaskedCardNumber()));
		sql.addColumn(FIELD_TENDER_AUTHORIZATION_DEBIT_CREDIT_CARD_CRYPTO_KEY_ID,
				makeSafeString(lineItem.getMaskedCardNumber()));
		sql.addColumn(FIELD_TENDER_AUTHORIZATION_CREDIT_DEBIT_CARD_ADJUDICATION_CODE,  getDebitAuthCode(lineItem));
		sql.addColumn(FIELD_TENDER_AUTHORIZATION_CARD_NUMBER_SWIPED_OR_KEYED_CODE, getEntryMethod(lineItem));
		sql.addColumn(FIELD_TENDER_AUTHORIZATION_TENDER_MEDIA_ISSUER_ID, getCardType(lineItem));
		try {
			sql.addColumn(FIELD_TENDER_AUTHORIZATION_DEBIT_CREDIT_CARD_EXPIRATION_DATE, getExpirationDate(lineItem));
			sql.addColumn(FIELD_TENDER_AUTHORIZATION_CARD_TYPE, getTenderDescription(lineItem));
			sql.addColumn(FIELD_TENDER_AUTHORIZATION_METHOD_CODE, getMethodCode(lineItem));
			sql.addColumn(FIELD_TENDER_AUTHORIZATION_DEBIT_CREDIT_ID_COUNTRY, getIDCountry(lineItem));
			sql.addColumn(FIELD_TENDER_AUTHORIZATION_DEBIT_CREDIT_ID_STATE, getIDState(lineItem));
			sql.addColumn(FIELD_TENDER_AUTHORIZATION_DEBIT_CREDIT_ID_EXPIRATION_DATE, getIDExpirationDate(lineItem));
			sql.addColumn(FIELD_TENDER_AUTHORIZATION_SETTLEMENT_DATA, getAuthorizationSettlementData(lineItem));
			sql.addColumn(FIELD_TENDER_AUTHORIZATION_DATE_TIME, getAuthorizationDateTime(lineItem));
			sql.addColumn(FIELD_ACQUIRER_BANK_CODE, getDebitBankCode(lineItem));
			sql.addColumn(FIELD_MASKED_ACC_ID, getMaskedValue(lineItem));
			if (lineItem.isEmiTransaction()) {
				sql.addColumn(FIELD_TENDER_AUTHORIZATION_DEBIT_CREDIT_ID_TYPE, "1");
			} else {
				sql.addColumn(FIELD_TENDER_AUTHORIZATION_DEBIT_CREDIT_ID_TYPE, "0");
			}
			dataConnection.execute(sql.getSQLString());
		} catch (DataException de) {
			logger.error(de);
			throw de;
		}
		catch (Exception e) {
			throw new DataException(DataException.UNKNOWN, "insertCreditDebitCardTenderLineItem", e);
		}
	}
	
	private String getDebitAuthCode(MAXTenderDebitIfc lineItem) {
		return (makeSafeString(lineItem.getAuthCode()));
	}
	private String getDebitBankCode(MAXTenderDebitIfc lineItem) {
		if (!Util.isEmpty(lineItem.getBankCode())) {
			return (makeSafeString(lineItem.getBankCode()));
		} else {
			return new String("3010");
		}
	}
	public String getMaskedValue(MAXTenderDebitIfc lineItem) {
		String value = applyMask(lineItem.getCardNumber().getBytes());
		return (makeSafeString(value));
	}
	protected String applyMask(byte[] clear) {
		byte[] mask = new byte[clear.length];
		int index = 0;
		if (mask.length > 10) {
			for (; index < mask.length - (mask.length - 6); index++) {
				mask[index] = clear[index];
			}
		}
		for (; index < mask.length - 4; index++) {
			mask[index] = (byte) getMaskChar();
		}
		for (; index < mask.length; index++) {
			mask[index] = clear[index];
		}
		return new String(mask);
	}
	public static char getMaskChar() {
		if (maskChar == 0) {
			maskChar = '*';
			String tempMask = Gateway.getProperty("foundation", "pan.mask", "*");
			if ("*".equals(tempMask) || "X".equals(tempMask)) {
				maskChar = (char) tempMask.getBytes()[0];
			}
		}
		return maskChar;
	}
	public String getCreditDebitAuthCode(MAXTenderChargeIfc lineItem) {
		return (makeSafeString(lineItem.getAuthCode()));
	}
	public String getCreditDebitOfflineAuthCode(MAXTenderChargeIfc lineItem) {
		return (makeSafeString(lineItem.getAuthorizationCode()));
	}
	public String getCreditDebitBankCode(MAXTenderChargeIfc lineItem) {
		if (!Util.isEmpty(lineItem.getBankCode())) {
			return (makeSafeString(lineItem.getBankCode()));
		} else {
			return new String("3010");
		}
	}
	public String getPaytmMobileNo(TenderLineItemIfc lineItem) {
		return (makeSafeString(((MAXTenderPaytmIfc) lineItem).getPaytmMobileNumber()));
	}

	@Override
	public void updateStoreCreditTenderlineitem(JdbcDataConnection dataConnection, TenderableTransactionIfc transaction,
			int lineItemSequenceNumber, TenderStoreCreditIfc lineItem) throws DataException {
        /*
         * Update the Tender Line Item table first.
         */
        updateTenderLineItem(dataConnection, transaction, lineItemSequenceNumber, lineItem);

        SQLUpdateStatement sql = new SQLUpdateStatement();
        
        logger.info("Before EWallet Tender");
		logger.info("(updateStoreCreditTenderlineitem) Condition Value:- "+!((MAXSaleReturnTransaction)transaction).isEWalletTenderFlag());
		if(!((MAXSaleReturnTransaction)transaction).isEWalletTenderFlag())
		{
			logger.info(" (updateStoreCreditTenderlineitem) Inside EWallet Tender");
        

        // Table
        sql.setTable(TABLE_STORE_CREDIT_TENDER_LINE_ITEM);

        // Fields
        sql.addColumn(FIELD_STORE_CREDIT_ID, getStoreCreditNumber(lineItem));

        sql.addColumn(FIELD_STORE_CREDIT_BALANCE, lineItem.getAmountTender().toString());
        if (((TenderAlternateCurrencyIfc)lineItem).getAlternateCurrencyTendered() != null)
        {
            sql.addColumn(
                    FIELD_STORE_CREDIT_FOREIGN_FACE_VALUE_AMOUNT,
                    ((TenderAlternateCurrencyIfc)lineItem).getAlternateCurrencyTendered().toString());
            //+I18N
            sql.addColumn(FIELD_CURRENCY_ID, ((TenderAlternateCurrencyIfc)lineItem).getAlternateCurrencyTendered().getType().getCurrencyId());
            //-I18N
        }
        else
        {
            // NOT NULL field designation
            sql.addColumn(
                    FIELD_STORE_CREDIT_FOREIGN_FACE_VALUE_AMOUNT,
                    "0.00");
            //+I18N
            sql.addColumn(FIELD_CURRENCY_ID, lineItem.getAmount().getType().getCurrencyId());
            //-I18N
        }

        // Qualifiers
        sql.addQualifier(FIELD_RETAIL_STORE_ID + " = " + getStoreID(transaction));
        sql.addQualifier(FIELD_WORKSTATION_ID + " = " + getWorkstationID(transaction));
        sql.addQualifier(FIELD_TRANSACTION_SEQUENCE_NUMBER + " = " + getTransactionSequenceNumber(transaction));
        sql.addQualifier(
            FIELD_RETAIL_TRANSACTION_LINE_ITEM_SEQUENCE_NUMBER + " = " + getSequenceNumber(lineItemSequenceNumber));
        sql.addQualifier(FIELD_BUSINESS_DAY_DATE + " = " + getBusinessDayString(transaction));

        try
        {
            dataConnection.execute(sql.getSQLString());
        }
        catch (DataException de)
        {
            logger.error( de.toString());
            throw de;
        }
        catch (Exception e)
        {
            throw new DataException(DataException.UNKNOWN, "updateStoreCreditTenderLineItem", e);
        }

        if (0 >= dataConnection.getUpdateCount())
        {
            throw new DataException(DataException.NO_DATA, "Update StoreCreditTenderLineItem");
        }
		}
    }

	@Override
	public void insertTenderLineItem(JdbcDataConnection dataConnection, TenderableTransactionIfc transaction,
			int lineItemSequenceNumber, TenderLineItemIfc lineItem) throws DataException {
        /*
         * Update the Retail Transaction Line Item table first
         */
		
		// changes by shyvanshu mehra
		
        insertRetailTransactionLineItem(dataConnection, transaction, lineItemSequenceNumber, TYPE_TENDER);

        SQLInsertStatement sql = new SQLInsertStatement();

        // Table
        sql.setTable(TABLE_TENDER_LINE_ITEM);

        // Fields
        sql.addColumn(FIELD_RETAIL_STORE_ID, getStoreID(transaction));
        sql.addColumn(FIELD_WORKSTATION_ID, getWorkstationID(transaction));
        sql.addColumn(FIELD_TRANSACTION_SEQUENCE_NUMBER, getTransactionSequenceNumber(transaction));
        sql.addColumn(FIELD_RETAIL_TRANSACTION_LINE_ITEM_SEQUENCE_NUMBER, getSequenceNumber(lineItemSequenceNumber));
        sql.addColumn(FIELD_BUSINESS_DAY_DATE, getBusinessDayString(transaction));
        
        
		 if (lineItem instanceof TenderCash) {
			 
			 TenderCash cashTender = (TenderCash) lineItem;
			 if (cashTender.isEWalletTenderType()) {
				 sql.addColumn(FIELD_TENDER_TYPE_CODE, makeSafeString("EWLT"));
			}
			 else {
				 sql.addColumn(FIELD_TENDER_TYPE_CODE, getTenderType(lineItem));
			 }
		}
		 else
			{
	        sql.addColumn(FIELD_TENDER_TYPE_CODE, getTenderType(lineItem));
			}
        sql.addColumn(FIELD_TENDER_LINE_ITEM_AMOUNT, getTenderAmount(lineItem));

        // alternate currency support --
        if ((lineItem instanceof TenderAlternateCurrencyIfc)
            && ((TenderAlternateCurrencyIfc) lineItem).getAlternateCurrencyTendered() != null)
        {
            CurrencyIfc lineItemiAlt = ((TenderAlternateCurrencyIfc) lineItem).getAlternateCurrencyTendered();
            sql.addColumn(FIELD_TENDER_LOCAL_CURRENCY_DESCRIPTION, getLocalCurrencyDescription());
            sql.addColumn(FIELD_TENDER_FOREIGN_CURRENCY_DESCRIPTION, getAlternateCurrencyDescription(lineItemiAlt));
            sql.addColumn(FIELD_EXCHANGE_RATE_TO_BUY_AMOUNT, lineItemiAlt.getBaseConversionRate().toString());
            sql.addColumn(FIELD_TENDER_FOREIGN_CURRENCY_COUNTRY_CODE, getCountryCode(lineItemiAlt));
            sql.addColumn(FIELD_TENDER_FOREIGN_CURRENCY_AMOUNT_TENDERED, lineItemiAlt.toString());
            sql.addColumn(FIELD_CURRENCY_ID, lineItemiAlt.getType().getCurrencyId());
        }
        else
        {
            sql.addColumn(FIELD_CURRENCY_ID, lineItem.getAmountTender().getType().getCurrencyId());
        }
        sql.addColumn(FIELD_RECORD_LAST_MODIFIED_TIMESTAMP, getSQLCurrentTimestampFunction());
        sql.addColumn(FIELD_RECORD_CREATION_TIMESTAMP, getSQLCurrentTimestampFunction());

        try
        {
            dataConnection.execute(sql.getSQLString());
        }
        catch (DataException de)
        {
            logger.error( de.toString());
            throw de;
        }
        catch (Exception e)
        {
            throw new DataException(DataException.UNKNOWN, "insertTenderLineItem", e);
        }
    }
	
	 // Changes End by Bhanu Priya 
}
