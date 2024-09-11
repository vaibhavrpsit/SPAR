/********************************************************************************
 *   
 *	Copyright (c) 2016 - 2017 MAX Hypermarket, Inc    All Rights Reserved.
 *
 *	Rev 2.9		Jun 01, 2019		Purushotham		Changes done for POS-Amazon Pay Integration
 *	Rev 2.8		Sep 18, 2018		Purushotham		BugFix for catch weight items: Code Merge CR Prod Defects 
 *	Rev 2.7		July 7, 2017		Hitesh Dua		BugFix: Gift card redemption through card swipe is showing as manual in receipt reprint
 *  Rev 2.6		Jun 24, 2017		Jyoti Yadav		M-coupon with % discount > Tran reprint is not printing coupon discount value 
 *  Rev 2.5     June 20,2017        Nayya           defect gift certificate was not getting retrieved in suspend retrieve flow
 *	rev 2.4     June 18,2017        Nayya           GST defect fix
 *  Rev 1.9		May 09, 2017		Kritica Agarwal	Changes for GST
 *	Rev 1.8		Apr 06, 2017		Nitesh Kumar	Changes to fix food total properties in Layaway read transaction
 *	Rev 1.7		Mar 23, 2017		Mansi Goel		Changes to fix discount price is not calculated properly for suspended transaction
 *  Rev 1.6		Feb 20, 2017	    Nadia Arora		Changes for price of item not coming on return
 *	Rev 1.5 	Jan 31, 2017		Hitesh.dua		Bugfix: Error message displayed on reprinted any transaction in which Item Level discount applied. 
 *	Rev 1.4		Jan 23, 2017        Ashish Yadav    Changes for Loyalty FES (issue when post void loyalty transaction)
 *	Rev 1.3		Dec 05, 2016        Ashish Yadav    Changes for Employee Discount FES
 *	Rev 1.2		Nov 08, 2016        Nadia Arora     MAX-StoreCredi_Return requirement.
 *	Rev	1.1 	Nov 07, 2016		Mansi Goel		Changes for Discount Rule FES
 *	Rev 1.0		Aug 26,2016			Nitesh Kumar	Changes for code merging
 *
 ********************************************************************************/

package max.retail.stores.domain.arts;

import java.awt.Point;
import java.io.InputStream;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Vector;

import max.retail.stores.domain.MAXUtils.MAXUtils;
import max.retail.stores.domain.discount.MAXDiscountRuleConstantsIfc;
import max.retail.stores.domain.discount.MAXItemDiscountStrategyIfc;
import max.retail.stores.domain.discountCoupon.MAXDiscountCoupon;
import max.retail.stores.domain.discountCoupon.MAXDiscountCouponIfc;
import max.retail.stores.domain.factory.MAXDomainObjectFactory;
import max.retail.stores.domain.gstin.MAXGSTINValidationResponse;
import max.retail.stores.domain.gstin.MAXGSTINValidationResponseIfc;
import max.retail.stores.domain.lineitem.MAXItemPriceIfc;
import max.retail.stores.domain.lineitem.MAXItemTaxIfc;
import max.retail.stores.domain.lineitem.MAXLineItemTaxBreakUpDetail;
import max.retail.stores.domain.lineitem.MAXLineItemTaxBreakUpDetailIfc;
import max.retail.stores.domain.lineitem.MAXSaleReturnLineItemIfc;
import max.retail.stores.domain.stock.MAXPLUItem;
import max.retail.stores.domain.stock.MAXPLUItemIfc;
import max.retail.stores.domain.tax.MAXTaxAssignment;
import max.retail.stores.domain.tender.MAXTenderAmazonPay;
import max.retail.stores.domain.tender.MAXTenderCharge;
import max.retail.stores.domain.tender.MAXTenderChargeIfc;
import max.retail.stores.domain.tender.MAXTenderLineItemIfc;
import max.retail.stores.domain.tender.MAXTenderLoyaltyPoints;
import max.retail.stores.domain.tender.MAXTenderLoyaltyPointsIfc;
import max.retail.stores.domain.tender.MAXTenderMobikwikIfc;
import max.retail.stores.domain.tender.MAXTenderPaytmIfc;
import max.retail.stores.domain.transaction.MAXOrderTransactionIfc;
import max.retail.stores.domain.transaction.MAXSaleReturnTransaction;
import max.retail.stores.domain.transaction.MAXSaleReturnTransactionIfc;
import max.retail.stores.domain.utility.MAXCodeConstantsIfc;
import max.retail.stores.domain.utility.MAXCodeListMapIfc;
import max.retail.stores.domain.utility.MAXGiftCardIfc;
import max.retail.stores.persistence.utility.MAXARTSDatabaseIfc;
import oracle.retail.stores.commerceservices.common.currency.CurrencyIfc;
import oracle.retail.stores.commerceservices.common.datetime.DateTimeServiceLocator;
import oracle.retail.stores.common.data.JdbcUtilities;
import oracle.retail.stores.common.sql.SQLParameterIfc;
import oracle.retail.stores.common.sql.SQLParameterValue;
import oracle.retail.stores.common.sql.SQLSelectStatement;
import oracle.retail.stores.common.utility.BigDecimalConstants;
import oracle.retail.stores.common.utility.LocaleMap;
import oracle.retail.stores.common.utility.LocaleRequestor;
import oracle.retail.stores.common.utility.LocalizedCodeIfc;
import oracle.retail.stores.common.utility.StringUtils;
import oracle.retail.stores.domain.DomainGateway;
import oracle.retail.stores.domain.arts.ARTSCustomer;
import oracle.retail.stores.domain.arts.DataTransactionFactory;
import oracle.retail.stores.domain.arts.JdbcReadTransaction;
import oracle.retail.stores.domain.customer.CustomerIfc;
import oracle.retail.stores.domain.customer.CustomerInfoIfc;
import oracle.retail.stores.domain.customer.IRSCustomerIfc;
import oracle.retail.stores.domain.discount.AdvancedPricingRuleIfc;
import oracle.retail.stores.domain.discount.DiscountRuleConstantsIfc;
import oracle.retail.stores.domain.discount.DiscountTargetIfc;
import oracle.retail.stores.domain.discount.ItemDiscountAuditIfc;
import oracle.retail.stores.domain.discount.ItemDiscountAuditStrategyIfc;
import oracle.retail.stores.domain.discount.ItemDiscountStrategyIfc;
import oracle.retail.stores.domain.discount.PromotionLineItemIfc;
import oracle.retail.stores.domain.discount.TransactionDiscountStrategyIfc;
import oracle.retail.stores.domain.employee.EmployeeIfc;
import oracle.retail.stores.domain.financial.LayawayIfc;
import oracle.retail.stores.domain.lineitem.AbstractTransactionLineItem;
import oracle.retail.stores.domain.lineitem.ItemPriceIfc;
import oracle.retail.stores.domain.lineitem.ItemTaxIfc;
import oracle.retail.stores.domain.lineitem.KitComponentLineItemIfc;
import oracle.retail.stores.domain.lineitem.OrderLineItemIfc;
import oracle.retail.stores.domain.lineitem.ReturnItemIfc;
import oracle.retail.stores.domain.lineitem.SaleReturnLineItem;
import oracle.retail.stores.domain.lineitem.SaleReturnLineItemIfc;
import oracle.retail.stores.domain.registry.RegistryIDIfc;
import oracle.retail.stores.domain.returns.ReturnTenderDataElementIfc;
import oracle.retail.stores.domain.stock.AlterationPLUItemIfc;
import oracle.retail.stores.domain.stock.GiftCardPLUItemIfc;
import oracle.retail.stores.domain.stock.ItemClassificationIfc;
import oracle.retail.stores.domain.stock.ItemKitConstantsIfc;
import oracle.retail.stores.domain.stock.KitComponentIfc;
import oracle.retail.stores.domain.stock.MerchandiseClassificationIfc;
import oracle.retail.stores.domain.stock.PLUItem;
import oracle.retail.stores.domain.stock.PLUItemIfc;
import oracle.retail.stores.domain.stock.ProductGroupIfc;
import oracle.retail.stores.domain.stock.UnitOfMeasureIfc;
import oracle.retail.stores.domain.store.StoreIfc;
import oracle.retail.stores.domain.store.WorkstationIfc;
import oracle.retail.stores.domain.tax.TaxConstantsIfc;
import oracle.retail.stores.domain.tax.TaxIfc;
import oracle.retail.stores.domain.tax.TaxInformationContainerIfc;
import oracle.retail.stores.domain.tax.TaxInformationIfc;
import oracle.retail.stores.domain.tender.TenderAlternateCurrencyIfc;
import oracle.retail.stores.domain.tender.TenderCashIfc;
import oracle.retail.stores.domain.tender.TenderChargeIfc;
import oracle.retail.stores.domain.tender.TenderCheckIfc;
import oracle.retail.stores.domain.tender.TenderCouponIfc;
import oracle.retail.stores.domain.tender.TenderGiftCardIfc;
import oracle.retail.stores.domain.tender.TenderGiftCertificateIfc;
import oracle.retail.stores.domain.tender.TenderLineItemIfc;
import oracle.retail.stores.domain.tender.TenderMailBankCheckIfc;
import oracle.retail.stores.domain.tender.TenderPurchaseOrderIfc;
import oracle.retail.stores.domain.tender.TenderStoreCreditIfc;
import oracle.retail.stores.domain.tender.TenderTravelersCheckIfc;
import oracle.retail.stores.domain.tender.TenderTypeMapIfc;
import oracle.retail.stores.domain.transaction.BankDepositTransactionIfc;
import oracle.retail.stores.domain.transaction.BillPayTransactionIfc;
import oracle.retail.stores.domain.transaction.InstantCreditTransactionIfc;
import oracle.retail.stores.domain.transaction.LayawayPaymentTransactionIfc;
import oracle.retail.stores.domain.transaction.LayawayTransactionIfc;
import oracle.retail.stores.domain.transaction.NoSaleTransactionIfc;
import oracle.retail.stores.domain.transaction.OrderTransaction;
import oracle.retail.stores.domain.transaction.OrderTransactionIfc;
import oracle.retail.stores.domain.transaction.PaymentTransactionIfc;
import oracle.retail.stores.domain.transaction.RedeemTransactionIfc;
import oracle.retail.stores.domain.transaction.RegisterOpenCloseTransactionIfc;
import oracle.retail.stores.domain.transaction.SaleReturnTransactionIfc;
import oracle.retail.stores.domain.transaction.SearchCriteriaIfc;
import oracle.retail.stores.domain.transaction.StatusChangeTransactionIfc;
import oracle.retail.stores.domain.transaction.StoreOpenCloseTransactionIfc;
import oracle.retail.stores.domain.transaction.TillAdjustmentTransactionIfc;
import oracle.retail.stores.domain.transaction.TillOpenCloseTransactionIfc;
import oracle.retail.stores.domain.transaction.Transaction;
import oracle.retail.stores.domain.transaction.TransactionConstantsIfc;
import oracle.retail.stores.domain.transaction.TransactionIDIfc;
import oracle.retail.stores.domain.transaction.TransactionIfc;
import oracle.retail.stores.domain.transaction.TransactionTaxIfc;
import oracle.retail.stores.domain.transaction.VoidTransactionIfc;
import oracle.retail.stores.domain.utility.CodeConstantsIfc;
import oracle.retail.stores.domain.utility.CodeListIfc;
import oracle.retail.stores.domain.utility.DiscountUtility;
import oracle.retail.stores.domain.utility.EYSDate;
import oracle.retail.stores.domain.utility.EYSTime;
import oracle.retail.stores.domain.utility.EntryMethod;
import oracle.retail.stores.domain.utility.GiftCardIfc;
import oracle.retail.stores.domain.utility.LocaleConstantsIfc;
import oracle.retail.stores.domain.utility.LocaleUtilities;
import oracle.retail.stores.domain.utility.PersonName;
import oracle.retail.stores.domain.utility.PersonNameIfc;
import oracle.retail.stores.domain.utility.SecurityOverrideIfc;
import oracle.retail.stores.foundation.factory.FoundationObjectFactory;
import oracle.retail.stores.foundation.manager.data.DataException;
import oracle.retail.stores.foundation.manager.data.JdbcDataConnection;
import oracle.retail.stores.foundation.manager.device.EncipheredCardDataIfc;
import oracle.retail.stores.foundation.manager.device.EncipheredDataIfc;
import oracle.retail.stores.foundation.manager.ifc.KeyStoreEncryptionManagerIfc;
import oracle.retail.stores.foundation.manager.ifc.data.DataActionIfc;
import oracle.retail.stores.foundation.manager.ifc.data.DataConnectionIfc;
import oracle.retail.stores.foundation.manager.ifc.data.DataTransactionIfc;
import oracle.retail.stores.foundation.tour.gate.Gateway;
import oracle.retail.stores.foundation.util.DBUtils;
import oracle.retail.stores.foundation.utility.Util;
import oracle.retail.stores.keystoreencryption.EncryptionServiceException;
import oracle.retail.stores.persistence.utility.DBConstantsIfc;

import org.apache.commons.codec.binary.Base64;
import org.apache.log4j.Logger;

@SuppressWarnings("deprecation")
public class MAXJdbcReadTransaction extends JdbcReadTransaction implements
		MAXARTSDatabaseIfc, MAXDiscountRuleConstantsIfc {

	/**
		 * 
		 */
	private static final long serialVersionUID = 8977762701342553664L;
	private static Logger logger = Logger
			.getLogger(MAXJdbcReadTransaction.class);

	public MAXJdbcReadTransaction() {
		super();
		setName("MAXJdbcReadTransaction");
	}

	public void execute(DataTransactionIfc dataTransaction,
			DataConnectionIfc dataConnection, DataActionIfc action)
			throws DataException {
		if (logger.isDebugEnabled())
			logger.debug("JdbcReadTransaction.execute");

		JdbcDataConnection connection = (JdbcDataConnection) dataConnection;
		TransactionIfc searchTransaction = (TransactionIfc) action
				.getDataObject();
		LocaleRequestor localeRequestor = getLocaleRequestor(searchTransaction);
		// Remove locale and locale requestor from transaction to enforce
		// explicitly
		// specifying the desired locale. In the future, use a
		// TransactionSearchCriteria
		// to specify a transaction and locale requestor explicitly.
		searchTransaction.setLocaleRequestor(null);

		// Send back the correct transaction (or lack thereof)
		TransactionIfc transaction = selectTransaction(connection,
				searchTransaction, localeRequestor);

		// if void transaction, handle original transaction
		if (transaction instanceof VoidTransactionIfc) {
			setOriginalTransaction(connection,
					(VoidTransactionIfc) transaction, localeRequestor);
		}
		try {
			setCapillaryCoupon(transaction, connection);
		} catch (Exception e) {
			e.printStackTrace();
		}
		dataTransaction.setResult(transaction);

		if (logger.isDebugEnabled())
			logger.debug("JdbcReadTransaction.execute");
	}

	// Ashish : Added below method(which is not present in base 14)
	public TransactionIfc[] readTransactionsByID(
			JdbcDataConnection dataConnection, TransactionIfc transaction,
			LocaleRequestor localeRequestor) throws DataException {
		if (logger.isDebugEnabled())
			logger.debug("JdbcReadTransaction.readTransactionsByID()");
		SQLSelectStatement sql = new SQLSelectStatement();

		// add tables
		sql.addTable(TABLE_TRANSACTION, ALIAS_TRANSACTION);

		// add columns
		readTransactionsAddColumns(sql);

		// add qualifiers for the transaction ID
		sql.addQualifier(ALIAS_TRANSACTION, FIELD_RETAIL_STORE_ID,
				getStoreID(transaction));
		sql.addQualifier(ALIAS_TRANSACTION, FIELD_WORKSTATION_ID,
				getWorkstationID(transaction));
		sql.addQualifier(ALIAS_TRANSACTION, FIELD_TRANSACTION_SEQUENCE_NUMBER,
				getTransactionSequenceNumber(transaction));

		// add qualifiers if status is completed or voided
		if (transaction.getTransactionStatus() == TransactionIfc.STATUS_COMPLETED
				|| transaction.getTransactionStatus() == TransactionIfc.STATUS_VOIDED) {
			sql.addQualifier("(" + FIELD_TRANSACTION_STATUS_CODE + " = "
					+ TransactionIfc.STATUS_COMPLETED + " OR "
					+ FIELD_TRANSACTION_STATUS_CODE + " = "
					+ TransactionIfc.STATUS_VOIDED + ")");
		}

		// match training mode
		sql.addQualifier(ALIAS_TRANSACTION, FIELD_TRANSACTION_TRAINING_FLAG,
				getTrainingMode(transaction));

		// see if businessDate is specified
		if (transaction.getBusinessDay() != null) {
			sql.addQualifier(ALIAS_TRANSACTION, FIELD_BUSINESS_DAY_DATE,
					getBusinessDayString(transaction));
		}

		sql.addOrdering(ALIAS_TRANSACTION, FIELD_BUSINESS_DAY_DATE);

		// set up transaction array
		TransactionIfc[] transactions = null;

		Vector<TransactionIfc> transVector = new Vector<TransactionIfc>(2);
		transVector = readTransactionsExecuteAndParse(dataConnection, sql,
				false, localeRequestor);

		transactions = new TransactionIfc[transVector.size()];
		transVector.copyInto(transactions);

		readStoreLocations(dataConnection, transVector, localeRequestor);

		if (logger.isDebugEnabled())
			logger.debug("JdbcReadTransaction.readTransactionsByID()");

		return (transactions);
	}

	protected void setCapillaryCoupon(TransactionIfc transaction,
			JdbcDataConnection connection) {
		Connection connectionSQL = connection.getConnection();
		String storeID = getStoreID(transaction);
		String workStationID = getWorkstationID(transaction);
		String transactionNumber = getTransactionSequenceNumber(transaction);
		String businessDateString = getBusinessDayString(transaction);
		/* Change for Rev 2.6: Start */
		/*
		 * String itemLevelCouponCountSQL =
		 * "SELECT DISTINCT(ID_DSC_REF),PE_MDFR_RT_PRC,sum(mo_mdfr_rt_prc) as mo_mdfr_rt_prc FROM CO_MDFR_RTL_PRC WHERE AI_TRN = '"
		 */
		String itemLevelCouponCountSQL = "SELECT DISTINCT(ID_DSC_REF),PE_MDFR_RT_PRC,sum(rtlog_amt) as rtlog_amt FROM CO_MDFR_RTL_PRC WHERE AI_TRN = '"
				/* Change for Rev 2.6: End */
				+ transactionNumber
				+ "' AND DC_DY_BSN="
				+ businessDateString
				+ " AND ID_WS="
				+ workStationID
				+ " AND ID_STR_RT="
				+ storeID
				+ "AND RC_MDFR_RT_PRC='5170' group by ID_DSC_REF,PE_MDFR_RT_PRC";
		try {
			PreparedStatement stmtItemLevelCouponCount = connectionSQL
					.prepareStatement(itemLevelCouponCountSQL);
			ResultSet resultSetItemLevelCount = stmtItemLevelCouponCount
					.executeQuery();
			/*
			 * connection.execute(itemLevelCouponCountSQL); ResultSet
			 * resultSetItemLevelCount = (ResultSet) connection.getResult();
			 */
			while (resultSetItemLevelCount.next()) {
				String referenceCode = resultSetItemLevelCount.getString(1);
				if (referenceCode != null) {
					String couponNumber[] = referenceCode.split("-");
					String percentageLevelDisc = resultSetItemLevelCount
							.getString(2);
					String totalPercentageDiscount = resultSetItemLevelCount
							.getString(3);
					MAXDiscountCouponIfc discountCoupon = new MAXDiscountCoupon();
					if (percentageLevelDisc != null
							&& !percentageLevelDisc.equalsIgnoreCase("0")) {
						discountCoupon.setDiscountType("PERC");
						discountCoupon.setDiscountOn("ITEM");
						discountCoupon
								.setCouponDiscountAmountPercent(new Double(
										percentageLevelDisc));
						discountCoupon
								.setCouponDiscountAmountByPerc(new Double(
										totalPercentageDiscount));

					} else {
						// String itemLevelCouponAmountSQL = "SELECT
						// SUM(MO_MDFR_RT_PRC) FROM CO_MDFR_RTL_PRC WHERE AI_TRN
						// = '"+transactionNumber+"' AND
						// DC_DY_BSN="+businessDateString+" AND
						// ID_WS="+workStationID+" AND ID_STR_RT="+storeID+" AND
						// RC_MDFR_RT_PRC='5170' AND ID_DSC_REF =
						// '"+referenceCode+"'";
						String itemLevelCouponAmountSQL = "SELECT SUM(TR.QU_ITM_LM_RTN_SLS*CO.MO_MDFR_RT_PRC) FROM CO_MDFR_RTL_PRC CO, TR_LTM_SLS_RTN TR WHERE CO.AI_TRN =  '"
								+ transactionNumber
								+ "' AND CO.ID_WS="
								+ workStationID
								+ " AND CO.DC_DY_BSN ="
								+ businessDateString
								+ " AND TR.ID_STR_RT="
								+ storeID
								+ " AND CO.ID_DSC_REF = '"
								+ referenceCode
								+ "' AND CO.RC_MDFR_RT_PRC='5170' AND CO.AI_TRN=TR.AI_TRN AND CO.ID_WS=TR.ID_WS AND CO.DC_DY_BSN=TR.DC_DY_BSN AND CO.AI_LN_ITM = TR.AI_LN_ITM";
						/*
						 * connection.execute(itemLevelCouponAmountSQL);
						 * ResultSet resultSetItemLevelAmount = (ResultSet)
						 * connection.getResult();
						 */
						PreparedStatement stmtItemLevelCouponAmount = connectionSQL
								.prepareStatement(itemLevelCouponAmountSQL);
						ResultSet resultSetItemLevelAmount = stmtItemLevelCouponAmount
								.executeQuery();
						resultSetItemLevelAmount.next();
						String amount = resultSetItemLevelAmount.getString(1);
						discountCoupon.setDiscountType("ABS");
						discountCoupon.setDiscountOn("ITEM");
						discountCoupon
								.setCouponDiscountAmountPercent(new Double(
										amount));
					}
					discountCoupon.setCouponNumber(couponNumber[1]);
					((MAXSaleReturnTransaction) transaction)
							.addCapillaryCouponsApplied(discountCoupon);
				}

			}
		} catch (SQLException e) {
			logger.error(e.getMessage());
		}

		String transactionLevelCouponCountSQL = "SELECT DISTINCT(ID_DSC_REF),PE_MDFR_RT_PRC FROM TR_MDFR_SLS_RTN_PRC WHERE AI_TRN = '"
				+ transactionNumber
				+ "' AND DC_DY_BSN="
				+ businessDateString
				+ " AND ID_WS="
				+ workStationID
				+ " AND ID_STR_RT="
				+ storeID
				+ "AND RC_MDFR_RT_PRC='5170'";
		try {
			/*
			 * connection.execute(transactionLevelCouponCountSQL); ResultSet
			 * resultSetTransactionLevelCount = (ResultSet)
			 * connection.getResult();
			 */
			PreparedStatement stmtTranLevelCouponCount = connectionSQL
					.prepareStatement(transactionLevelCouponCountSQL);
			ResultSet resultSetTransactionLevelCount = stmtTranLevelCouponCount
					.executeQuery();
			while (resultSetTransactionLevelCount.next()) {
				String referenceCode = resultSetTransactionLevelCount
						.getString(1);
				if (referenceCode != null) {
					String couponNumber[] = referenceCode.split("-");
					String percentageLevelDisc = resultSetTransactionLevelCount
							.getString(2);
					MAXDiscountCouponIfc discountCoupon = new MAXDiscountCoupon();
					if (percentageLevelDisc != null
							&& !percentageLevelDisc.equalsIgnoreCase("0")) {
						discountCoupon.setDiscountType("PERC");
						discountCoupon.setDiscountOn("BILL");
						discountCoupon
								.setCouponDiscountAmountPercent(new Double(
										percentageLevelDisc));

					} else {
						String transactionLevelCouponAmountSQL = "SELECT SUM(MO_MDFR_RT_PRC) FROM TR_MDFR_SLS_RTN_PRC WHERE AI_TRN = '"
								+ transactionNumber
								+ "' AND DC_DY_BSN="
								+ businessDateString
								+ " AND ID_WS="
								+ workStationID
								+ " AND ID_STR_RT="
								+ storeID
								+ "AND RC_MDFR_RT_PRC='5170' AND ID_DSC_REF = '"
								+ referenceCode + "'";
						PreparedStatement stmtTranLevelCouponAmount = connectionSQL
								.prepareStatement(transactionLevelCouponAmountSQL);
						ResultSet resultSetTransactionLevelAmount = stmtTranLevelCouponAmount
								.executeQuery();
						resultSetTransactionLevelAmount.next();
						String amount = resultSetTransactionLevelAmount
								.getString(1);
						discountCoupon.setDiscountType("ABS");
						discountCoupon.setDiscountOn("BILL");
						discountCoupon
								.setCouponDiscountAmountPercent(new Double(
										amount));
					}
					discountCoupon.setCouponNumber(couponNumber[1]);
					((MAXSaleReturnTransaction) transaction)
							.addCapillaryCouponsApplied(discountCoupon);
				}
			}
		} catch (SQLException e) {
			logger.error(e.getMessage());
		}
	}

	protected void selectGiftCard(JdbcDataConnection dataConnection,
			SaleReturnTransactionIfc transaction, int sequenceNumber,
			GiftCardPLUItemIfc giftCardItem, SaleReturnLineItemIfc lineItem)
			throws DataException {
		// build SQL statement to select gift card
		SQLSelectStatement sql = new SQLSelectStatement();
		sql.addTable(TABLE_GIFT_CARD);
		sql.addColumn(FIELD_GIFT_CARD_SERIAL_NUMBER);
		sql.addColumn(FIELD_MASKED_GIFT_CARD_SERIAL_NUMBER);
		sql.addColumn(FIELD_CURRENCY_ID); // I18N
		sql.addColumn(FIELD_GIFT_CARD_ACTIVATION_ADJUDICATION_CODE);
		sql.addColumn(FIELD_GIFT_CARD_ENTRY_METHOD);
		sql.addColumn(FIELD_GIFT_CARD_REQUEST_TYPE);
		sql.addColumn(FIELD_GIFT_CARD_CURRENT_BALANCE);
		sql.addColumn(FIELD_GIFT_CARD_INITIAL_BALANCE);
		sql.addColumn(FIELD_TENDER_AUTHORIZATION_SETTLEMENT_DATA);
		sql.addColumn(FIELD_TENDER_AUTHORIZATION_DATE_TIME);
		// Ashish : Start Added below lines
		sql.addColumn(FIELD_GIFT_CARD_AUTHORIZATION_CODE);
		sql.addColumn(FIELD_GIFT_CARD_INVOICE_ID);
		sql.addColumn(FIELD_GIFT_CARD_BATCH_ID);
		sql.addColumn(FIELD_GIFT_CARD_TYPE);
		sql.addColumn(FIELD_GIFT_CARD_TRANS_ID);
		sql.addColumn(FIELD_GIFT_CARD_EXP_DATE);
		// Ashish : End Added below lines
		sql.addColumn(FIELD_TENDER_AUTHORIZATION_JOURNAL_KEY);
		sql.addColumn(FIELD_TENDER_AUTHORIZATION_TRANSACTION_TRACE_NUMBER);

		sql.addQualifier(FIELD_RETAIL_STORE_ID, getStoreID(transaction));
		sql.addQualifier(FIELD_WORKSTATION_ID, getWorkstationID(transaction));
		sql.addQualifier(FIELD_TRANSACTION_SEQUENCE_NUMBER,
				getTransactionSequenceNumber(transaction));
		sql.addQualifier(FIELD_RETAIL_TRANSACTION_LINE_ITEM_SEQUENCE_NUMBER,
				Integer.toString(sequenceNumber));
		sql.addQualifier(FIELD_BUSINESS_DAY_DATE,
				getBusinessDayString(transaction));

		try {
			dataConnection.execute(sql.getSQLString());
			ResultSet rs = (ResultSet) dataConnection.getResult();

			if (rs.next()) {
				int index = 0;
				String cardNumber = getSafeString(rs, ++index);
				String maskedCardNumber = getSafeString(rs, ++index);
				EncipheredCardDataIfc cardData = FoundationObjectFactory
						.getFactory().createEncipheredCardDataInstance(
								cardNumber, maskedCardNumber, null);
				giftCardItem.getGiftCard().setEncipheredCardData(cardData);
				giftCardItem.getGiftCard().setCurrencyID(rs.getInt(++index)); // I18N
				giftCardItem.getGiftCard().setApprovalCode(
						getSafeString(rs, ++index));
				giftCardItem.getGiftCard().setEntryMethod(
						EntryMethod.valueOf(getSafeString(rs, ++index)));
				giftCardItem.getGiftCard().setRequestType(rs.getInt(++index));
				CurrencyIfc currentBalance = DomainGateway
						.getBaseCurrencyInstance(getSafeString(rs, ++index));
				giftCardItem.getGiftCard().setCurrentBalance(currentBalance);
				CurrencyIfc initialBalance = DomainGateway
						.getBaseCurrencyInstance(getSafeString(rs, ++index));
				giftCardItem.getGiftCard().setInitialBalance(initialBalance);
				giftCardItem.getGiftCard().setSettlementData(
						getSafeString(rs, ++index));
				Timestamp authDate = rs.getTimestamp(++index);
				if (authDate != null) {
					giftCardItem.getGiftCard().setAuthorizedDateTime(
							new EYSDate(authDate));
				}
				// Ashish : Start Added below lines
				try {
					MAXGiftCardIfc gc = (MAXGiftCardIfc) giftCardItem
							.getGiftCard();
					String authCode = rs.getString(++index);
					gc.setQcApprovalCode(authCode);
					String invID = rs.getString(++index);
					gc.setQcInvoiceNumber(invID);
					String btchID = rs.getString(++index);
					gc.setQcBatchNumber(btchID);
					String cardType = rs.getString(++index);
					gc.setQcCardType(cardType);
					String transID = rs.getString(++index);
					gc.setQcTransactionId(transID);
					Timestamp expDate = rs.getTimestamp(++index);
					giftCardItem.getGiftCard().setExpirationDate(
							new EYSDate(expDate));

				} catch (Exception e) {

				}
				// Ashish : End above lines
				giftCardItem.getGiftCard().setJournalKey(
						getSafeString(rs, ++index));
				giftCardItem.getGiftCard().setTraceNumber(
						getSafeString(rs, ++index));

				// if this read is of a suspended transaction, then set the
				// giftCard.requestedAmount
				// to the extendended selling price of the line item. This
				// ensures that the gift card
				// can be activated for the desired amount.
				if (TransactionConstantsIfc.STATUS_SUSPENDED == transaction
						.getTransactionStatus() && lineItem != null) {
					giftCardItem.getGiftCard().setReqestedAmount(
							lineItem.getExtendedSellingPrice());
				}
			}
			rs.close();
		} catch (DataException de) {
			// ignore no-data data exception
			if (de.getErrorCode() != DataException.NO_DATA) {
				throw de;
			}
		} catch (Exception e) {
			throw new DataException(DataException.UNKNOWN,
					"Error reading gift card: " + e.toString());
		}
	}

	protected void readOrderInfo(JdbcDataConnection dataConnection,
			OrderTransactionIfc transaction) throws DataException {
		SQLSelectStatement sql = new SQLSelectStatement();

		sql.addTable(TABLE_ORDER_STATUS);

		sql.addColumn(FIELD_EMAIL_ADDRESS);
		sql.addColumn(FIELD_ORDER_DESCRIPTION);
		sql.addColumn(FIELD_ORDER_TYPE_CODE);
		// Ashish : Start added below lines
		sql.addColumn(FIELD_EXPECTED_ORDER_DELIVERY_DATE);
		sql.addColumn(FIELD_EXPECTED_ORDER_DELIVERY_TIME);
		sql.addColumn(FIELD_SUGGESTED_TENDER_SPL_ORD);
		// Ashish : End added avove lines

		TransactionIDIfc transactionID = transaction.getTransactionIdentifier();
		sql.addQualifier(FIELD_RETAIL_STORE_ID,
				inQuotes(transactionID.getStoreID()));
		sql.addQualifier(FIELD_WORKSTATION_ID,
				inQuotes(transactionID.getWorkstationID()));
		sql.addQualifier(FIELD_BUSINESS_DAY_DATE,
				dateToSQLDateString(transactionID.getBusinessDate()));
		sql.addQualifier(FIELD_TRANSACTION_SEQUENCE_NUMBER,
				Long.toString(transactionID.getSequenceNumber()));
		try {
			dataConnection.execute(sql.getSQLString());

			ResultSet rs = (ResultSet) dataConnection.getResult();

			if (rs != null) {
				if (rs.next()) {
					int index = 0;

					String customerEmailAddr = getSafeString(rs, ++index);
					String orderDescription = getSafeString(rs, ++index);
					// Ashish : Start added below lines
					if (transaction instanceof MAXOrderTransactionIfc) {
						EYSDate deliveryDate = getEYSDateFromString(rs, ++index);
						String deliveryTimeString = getSafeString(rs, ++index);
						String suggestedTender = getSafeString(rs, ++index);
						int deliveryHour = Integer.parseInt(deliveryTimeString
								.substring(0, deliveryTimeString.indexOf(":")));
						int deliveryMin = Integer.parseInt(deliveryTimeString
								.substring(deliveryTimeString.indexOf(":") + 1,
										deliveryTimeString.indexOf(":") + 3));
						String deliveryAMPM = deliveryTimeString.substring(
								deliveryTimeString.length() - 2,
								deliveryTimeString.length());

						if (deliveryAMPM.equalsIgnoreCase("PM"))
							deliveryHour += 12;

						EYSTime deliveryTime = new EYSTime(deliveryHour,
								deliveryMin, 0);

						((MAXOrderTransactionIfc) transaction)
								.setExpectedDeliveryDate(deliveryDate);
						((MAXOrderTransactionIfc) transaction)
								.setExpectedDeliveryTime(deliveryTime);
						((MAXOrderTransactionIfc) transaction)
								.setSuggestedTender(suggestedTender);

					}
					// Ashish : End added above lines
					int orderType = rs.getInt(++index);

					transaction.setOrderCustomerEmailAddress(customerEmailAddr);
					transaction.setOrderDescription(orderDescription);
					transaction.setOrderType(orderType);
				}
			}
		} catch (DataException de) {
			logger.error("" + de + "");
			throw de;
		} catch (SQLException se) {
			dataConnection.logSQLException(se, "order table");
			throw new DataException(DataException.SQL_ERROR, "order table", se);
		}
	}

	protected TenderLineItemIfc[] selectTenderLineItems(
			JdbcDataConnection dataConnection, TransactionIfc transaction)
			throws DataException {
		if (logger.isDebugEnabled())
			logger.debug("JdbcReadTransaction.selectTenderLineItems()");

		SQLSelectStatement sql = new SQLSelectStatement();
		// Table
		sql.setTable(TABLE_TENDER_LINE_ITEM);
		// Fields
		sql.addColumn(FIELD_RETAIL_TRANSACTION_LINE_ITEM_SEQUENCE_NUMBER);
		sql.addColumn(FIELD_TENDER_TYPE_CODE);
		sql.addColumn(FIELD_CURRENCY_ID); // I18N
		sql.addColumn(FIELD_TENDER_LINE_ITEM_AMOUNT);
		// alternate tender support
		sql.addColumn(FIELD_TENDER_LOCAL_CURRENCY_DESCRIPTION);
		sql.addColumn(FIELD_TENDER_FOREIGN_CURRENCY_DESCRIPTION);
		sql.addColumn(FIELD_EXCHANGE_RATE_TO_BUY_AMOUNT);
		sql.addColumn(FIELD_TENDER_FOREIGN_CURRENCY_COUNTRY_CODE);
		sql.addColumn(FIELD_TENDER_FOREIGN_CURRENCY_AMOUNT_TENDERED);
		// Qualifiers
		sql.addQualifier(FIELD_RETAIL_STORE_ID + " = "
				+ getStoreID(transaction));
		sql.addQualifier(FIELD_WORKSTATION_ID + " = "
				+ getWorkstationID(transaction));
		sql.addQualifier(FIELD_TRANSACTION_SEQUENCE_NUMBER + " = "
				+ getTransactionSequenceNumber(transaction));
		sql.addQualifier(FIELD_BUSINESS_DAY_DATE + " = "
				+ getBusinessDayString(transaction));
		// Ordering
		sql.addOrdering(FIELD_RETAIL_TRANSACTION_LINE_ITEM_SEQUENCE_NUMBER
				+ " ASC");

		Vector<TenderLineItemIfc> tenderLineItems = new Vector<TenderLineItemIfc>(
				2);

		try {
			dataConnection.execute(sql.getSQLString());
			TenderLineItemIfc tenderLineItem = null;
			int firstSequenceNumber = -1;
			ResultSet rs = (ResultSet) dataConnection.getResult();

			while (rs.next()) {
				int index = 0;
				int sequenceNumber = rs.getInt(++index);
				String tenderType = getSafeString(rs, ++index);
				int currencyID = rs.getInt(++index);
				// alternate currency support
				CurrencyIfc tenderAmount = getCurrencyFromDecimal(rs, ++index);
				String localCurrencyDescription = getSafeString(rs, ++index);
				String altCurrencyDescription = getSafeString(rs, ++index);
				BigDecimal exchangeRate = getBigDecimal(rs, ++index, 4);
				CurrencyIfc altAmount = null;

				if (altCurrencyDescription.length() > 0) {
					String altCountryCode = getSafeString(rs, ++index);
					String altValue = getSafeString(rs, ++index);
					altAmount = getAltCurrencyFromCountryCode(altValue,
							altCountryCode);
					if (logger.isInfoEnabled())
						logger.info("Local currency: "
								+ new Object[] { localCurrencyDescription
										+ " Alternate currency: "
										+ altCurrencyDescription
										+ " Exchange Rate: " + exchangeRate
										+ "Country Code: " + altCountryCode
										+ " Val: " + altAmount.getStringValue() }
								+ "");
				}
				// Changes starts for Rev 1.4 (Ashish : Loyalty OTP)
				tenderLineItem = instantiateTenderLineItem(tenderType);
				// Changes ends for Rev 1.4 (Ashish : Loyalty OTP)
				tenderLineItem.setAmountTender(tenderAmount);
				tenderLineItem.setCurrencyID(currencyID); // I18N

				/*
				 * If change is issued for overtendered transaction, then a
				 * record in the TenderLineItem table with a negative amount is
				 * logged Negative amount is also logged for return
				 * transactions, hence excluding return transactions and setting
				 * property collected to false for other transactions.
				 */
				if (transaction.getTransactionType() != TransactionConstantsIfc.TYPE_RETURN
						&& (tenderAmount.signum() == CurrencyIfc.NEGATIVE && tenderLineItem instanceof TenderCashIfc)) {
					tenderLineItem.setCollected(false);
				}

				if (tenderLineItem instanceof TenderAlternateCurrencyIfc) {
					if (altAmount != null) {
						((TenderAlternateCurrencyIfc) tenderLineItem)
								.setAlternateCurrencyTendered(altAmount);
					}
				}

				if (firstSequenceNumber < 0) {
					/*
					 * Save the value of the first sequence number because we
					 * need to know it to match up the tender specific records
					 * if there are multiples
					 */
					firstSequenceNumber = sequenceNumber;
				}

				/*
				 * Add the line item to the vector of retrieved line items
				 */
				tenderLineItems.addElement(tenderLineItem);
			}
			rs.close();
			// For each tender line item, retrieve other information that
			// is applicable to the type of tender
			Enumeration<TenderLineItemIfc> lineItems = tenderLineItems
					.elements();

			int index = 0;
			while (lineItems.hasMoreElements()) {
				tenderLineItem = lineItems.nextElement();
				int sequenceNumber = firstSequenceNumber + index;

				// Do Gift Card before Charge because it extends it.
				if (tenderLineItem instanceof TenderGiftCardIfc) {
					readGiftCardTenderLineItem(dataConnection, transaction,
							(TenderGiftCardIfc) tenderLineItem, sequenceNumber);
				} else if (tenderLineItem instanceof TenderChargeIfc) {
					readCreditDebitTenderLineItem(dataConnection, transaction,
							(TenderChargeIfc) tenderLineItem, sequenceNumber);
				} else if (tenderLineItem instanceof TenderCheckIfc) {
					readCheckTenderLineItem(dataConnection, transaction,
							(TenderCheckIfc) tenderLineItem, sequenceNumber);
				} else if (tenderLineItem instanceof TenderGiftCertificateIfc) {
					readGiftCertificateTenderLineItem(dataConnection,
							transaction,
							(TenderGiftCertificateIfc) tenderLineItem,
							sequenceNumber);
				} else if (tenderLineItem instanceof TenderMailBankCheckIfc) {
					readSendCheckTenderLineItem(dataConnection, transaction,
							(TenderMailBankCheckIfc) tenderLineItem,
							sequenceNumber);
				} else if (tenderLineItem instanceof TenderTravelersCheckIfc) {
					readTravelersCheckTenderLineItem(dataConnection,
							transaction,
							(TenderTravelersCheckIfc) tenderLineItem,
							sequenceNumber);
				} else if (tenderLineItem instanceof TenderCouponIfc) {
					readCouponTenderLineItem(dataConnection, transaction,
							(TenderCouponIfc) tenderLineItem, sequenceNumber);
				} else if (tenderLineItem instanceof TenderStoreCreditIfc) {
					readStoreCreditTenderLineItem(dataConnection, transaction,
							(TenderStoreCreditIfc) tenderLineItem,
							sequenceNumber);
					// Ashish : Start added belo lines
					TenderStoreCreditIfc tsc = (TenderStoreCreditIfc) tenderLineItem;
					String storeCreditID = tsc.getStoreCreditID();

					try {
						tsc.setStoreCredit(readStoreCredit(dataConnection,
								storeCreditID,
								((TenderStoreCreditIfc) tenderLineItem)
										.getStoreNumber()));
					} catch (DataException e) {
						// Do nothing here
					}
					// Ashish : End above lines
				} else if (tenderLineItem instanceof TenderPurchaseOrderIfc) {
					readPurchaseOrderTenderLineItem(dataConnection,
							transaction,
							(TenderPurchaseOrderIfc) tenderLineItem,
							sequenceNumber);
				}
				// code added by atul shukla for paytm tender post void
				/*
				 * else if (tenderLineItem instanceof MAXTenderPaytm) {
				 * readpaytmtenderLineItem(dataConnection, transaction,
				 * (MAXTenderPaytmIfc) tenderLineItem, sequenceNumber); //String
				 * loyaltyCardNumber = readLoyaltyPointInfo(dataConnection,
				 * transaction); //((MAXTenderLoyaltyPoints)
				 * tenderLineItem).setLoyaltyCardNumber(loyaltyCardNumber); }
				 */
				// Ashish : Start added below lines
				else if (tenderLineItem instanceof MAXTenderLoyaltyPoints) {
					String loyaltyCardNumber = readLoyaltyPointInfo(
							dataConnection, transaction);

					((MAXTenderLoyaltyPoints) tenderLineItem)
							.setLoyaltyCardNumber(loyaltyCardNumber);
				}
				// Ashish : End added above lines
				else if (!(tenderLineItem instanceof TenderCashIfc)) {
					logger.error("don't know how to read " + ""
							+ tenderLineItem.getClass().getName() + "");
				}
				index++;

			}
		} catch (SQLException exc) {
			dataConnection.logSQLException(exc, "Processing result set.");
			throw new DataException(DataException.SQL_ERROR,
					"readTenderLineItems", exc);
		}

		int numItems = tenderLineItems.size();
		TenderLineItemIfc[] lineItems = new TenderLineItemIfc[numItems];
		tenderLineItems.copyInto(lineItems);

		if (logger.isDebugEnabled())
			logger.debug("JdbcReadTransaction.readTenderLineItems()");

		return (lineItems);
	}

	// added by atul for patym tender details
	/*
	 * protected void readpaytmtenderLineItem(JdbcDataConnection dataConnection,
	 * TransactionIfc transaction, MAXTenderPaytmIfc lineItem, int
	 * lineItemSequenceNumber) throws DataException { if
	 * (logger.isDebugEnabled()) {
	 * logger.debug("JdbcReadTransaction.readpaytmtenderLineItem()"); }
	 * SQLSelectStatement sql = new SQLSelectStatement();
	 * 
	 * 
	 * sql.setTable("TR_LTM_WALLET_TND");
	 * 
	 * sql.addColumn("MOB_NO"); sql.addColumn("WALLET_ORDER_ID");
	 * sql.addColumn("TRANS_ID");
	 * 
	 * sql.addQualifier("ID_STR_RT = " + getStoreID(transaction));
	 * sql.addQualifier("ID_WS = " + getWorkstationID(transaction));
	 * sql.addQualifier("AI_TRN = " +
	 * getTransactionSequenceNumber(transaction));
	 * sql.addQualifier("DC_DY_BSN = " + getBusinessDayString(transaction));
	 * sql.addQualifier("AI_LN_ITM = " +
	 * String.valueOf(lineItemSequenceNumber));
	 * 
	 * 
	 * try { dataConnection.execute(sql.getSQLString()); ResultSet rs =
	 * (ResultSet)dataConnection.getResult();
	 * 
	 * if (rs.next()) { int index = 0; String poNumber = getSafeString(rs,
	 * ++index); CurrencyIfc poAmount =
	 * DomainGateway.getBaseCurrencyInstance(getSafeString(rs, ++index)); //
	 * String poAgencyName = getSafeString(rs, ++index); //
	 * lineItem.setPaytmMobileNumber(moblieno);; //
	 * lineItem.setPaytmAmount((MAXTenderPaytmIfc)poAmount);; //
	 * lineItem.setAgencyName(poAgencyName); } else { throw new DataException(6,
	 * "readpaytmtenderLineItem"); } rs.close(); } catch (DataException de) {
	 * logger.warn("" + de + ""); throw de; } catch (SQLException se) {
	 * dataConnection.logSQLException(se, "readPurchaseOrderTenderLineItem");
	 * throw new DataException(1, "readPurchaseOrderTenderLineItem", se); }
	 * catch (Exception e) { throw new DataException(0,
	 * "readpaytmtenderLineItem", e); } if (logger.isDebugEnabled()) {
	 * logger.debug("JdbcReadTransaction.readpaytmtenderLineItem()"); } }
	 */

	// Ashish : Start added below method
	protected String readLoyaltyPointInfo(JdbcDataConnection dataConnection,
			TransactionIfc transaction) throws DataException {
		SQLSelectStatement sql = new SQLSelectStatement();
		// Table
		sql.setTable(TABLE_LOYALTY_POINT_TENDER_LINE_ITEM);
		sql.addColumn(FIELD_LOYALTY_CARD_NUMBER);
		// sql.addColumn(FIELD_LOYALTY_POINT_AMOUNT);
		sql.addQualifier(FIELD_RETAIL_STORE_ID + " = "
				+ getStoreID(transaction));
		sql.addQualifier(FIELD_WORKSTATION_ID + " = "
				+ getWorkstationID(transaction));
		sql.addQualifier(FIELD_TRANSACTION_SEQUENCE_NUMBER + " = "
				+ getTransactionSequenceNumber(transaction));
		sql.addQualifier(FIELD_BUSINESS_DAY_DATE + " = "
				+ getBusinessDayString(transaction));
		dataConnection.execute(sql.getSQLString());
		ResultSet rs = (ResultSet) dataConnection.getResult();
		String loyaltyPoint = null;
		try {
			rs.next();
			loyaltyPoint = rs.getString(1);
		} catch (SQLException e) {
			logger.error(e.getMessage());
		}
		return loyaltyPoint;
	}

	// Changes Start for Bhanu Priya Gupta
	protected ArrayList<String> checkForPANDetails(
			JdbcDataConnection dataConnection, TransactionIfc transaction)
			throws DataException {
		SQLSelectStatement sql = new SQLSelectStatement();
		// Table
		sql.addTable(TABLE_TRAN_PAN_DETAILS, ALIAS_TRAN_PAN_DETAILS);

		sql.addColumn(ALIAS_TRAN_PAN_DETAILS + "." + FIELD_PAN_NUM);
		sql.addColumn(ALIAS_TRAN_PAN_DETAILS + "." + FIELD_FORM60_IDNUM);
		sql.addColumn(ALIAS_TRAN_PAN_DETAILS + "." + FIELD_PASSPORT_NUM);
		// sql.addColumn(FIELD_LOYALTY_POINT_AMOUNT);
		sql.addQualifier(ALIAS_TRAN_PAN_DETAILS,
				FIELD_TRANSACTION_SEQUENCE_NUMBER,
				getTransactionSequenceNumber(transaction));
		sql.addQualifier(ALIAS_TRAN_PAN_DETAILS, FIELD_WORKSTATION_ID,
				getWorkstationID(transaction));
		sql.addQualifier(ALIAS_TRAN_PAN_DETAILS, FIELD_RETAIL_STORE_ID,
				getStoreID(transaction));

		sql.addQualifier(ALIAS_TRAN_PAN_DETAILS, FIELD_BUSINESS_DAY_DATE,
				getBusinessDayString(transaction));
		dataConnection.execute(sql.getSQLString());
		ResultSet rs = (ResultSet) dataConnection.getResult();

		ArrayList<String> list = null;
		String panno = null;
		String uid = null;
		String passportno = null;
		String pandetails = null;
		try {
			list = new ArrayList<String>();

			if (rs.next()) {
				for (int i = 1; i <= 3; i++) {
					pandetails = getSafeString(rs, i);
					list.add(pandetails);
				}

			}

		} catch (SQLException e) {
			logger.error(e.getMessage());
		}
		return list;

	}

	// Changes End by bhanu priya Gupta

	protected void readCreditDebitTenderLineItem(
			JdbcDataConnection dataConnection, TransactionIfc transaction,
			TenderChargeIfc lineItem, int lineItemSequenceNumber)
			throws DataException {

		String expirationDate; // credit card expiration date

		if (logger.isDebugEnabled()) {
			logger.debug("JdbcReadTransaction.readCreditDebitTenderLineItem()");
		}

		SQLSelectStatement sql = new SQLSelectStatement();
		// Table
		sql.setTable(TABLE_CREDIT_DEBIT_CARD_TENDER_LINE_ITEM);
		// Fields
		// Field updated For upgradation
		sql.addColumn(FIELD_TENDER_TYPE_CODE);
		sql.addColumn(FIELD_TENDER_AUTHORIZATION_DEBIT_CREDIT_CARD_ACCOUNT_MASKED);
		sql.addColumn(FIELD_TENDER_AUTHORIZATION_CREDIT_DEBIT_CARD_ADJUDICATION_CODE);
		sql.addColumn(FIELD_TENDER_AUTHORIZATION_CARD_NUMBER_SWIPED_OR_KEYED_CODE);
		sql.addColumn(FIELD_TENDER_AUTHORIZATION_TENDER_MEDIA_ISSUER_ID);
		sql.addColumn(FIELD_TENDER_AUTHORIZATION_DEBIT_CREDIT_CARD_EXPIRATION_DATE);
		sql.addColumn(FIELD_TENDER_AUTHORIZATION_CUSTOMER_SIGNATURE_IMAGE);
		sql.addColumn(FIELD_TENDER_AUTHORIZATION_METHOD_CODE);
		sql.addColumn(FIELD_TENDER_AUTHORIZATION_DEBIT_CREDIT_ID_TYPE);
		sql.addColumn(FIELD_TENDER_AUTHORIZATION_DEBIT_CREDIT_ID_COUNTRY);
		sql.addColumn(FIELD_TENDER_AUTHORIZATION_DEBIT_CREDIT_ID_STATE);
		sql.addColumn(FIELD_TENDER_AUTHORIZATION_DEBIT_CREDIT_ID_EXPIRATION_DATE);
		sql.addColumn(FIELD_TENDER_AUTHORIZATION_SETTLEMENT_DATA);
		sql.addColumn(FIELD_TENDER_AUTHORIZATION_DATE_TIME);
		sql.addColumn(FIELD_TENDER_AUTHORIZATION_DEBIT_CREDIT_CARD_CRYPTO_KEY_ID);
		sql.addColumn(FIELD_ACQUIRER_BANK_CODE);
		sql.addColumn(FIELD_INVOICE_NUMBERCRE);
		sql.addColumn(FIELD_TRANSACTION_ACQUIRER);
		sql.addColumn(FIELD_BATCH_NUMBER);
		sql.addColumn(FIELD_TERMINAL_ID);
		sql.addColumn(FIELD_RETRIVAL_REF_NO);
		sql.addColumn(FIELD_CARD_HOLDER_NAME);
		sql.addColumn(FIELD_MERCHANT_ID_INNOVITI);
		sql.addColumn(FIELD_TRANSACTION_APPROVAL_TIME);
		sql.addColumn(FIELD_HOST_RESPONSE_ID);
		sql.addColumn(FIELD_HOST_RESPONSE_MESSAGE);
		sql.addColumn(FIELD_APPROVAL_ID);
		sql.addColumn(FIELD_RETURN_REFERENCE_NUMBER);
		// Qualifiers
		sql.addQualifier(FIELD_RETAIL_STORE_ID + " = "
				+ getStoreID(transaction));
		sql.addQualifier(FIELD_WORKSTATION_ID + " = "
				+ getWorkstationID(transaction));
		sql.addQualifier(FIELD_TRANSACTION_SEQUENCE_NUMBER + " = "
				+ getTransactionSequenceNumber(transaction));
		sql.addQualifier(FIELD_BUSINESS_DAY_DATE + " = "
				+ getBusinessDayString(transaction));
		sql.addQualifier(FIELD_RETAIL_TRANSACTION_LINE_ITEM_SEQUENCE_NUMBER
				+ " = " + String.valueOf(lineItemSequenceNumber));

		try {
			dataConnection.execute(sql.getSQLString());
			ResultSet rs = (ResultSet) dataConnection.getResult();

			if (rs.next()) {
				int index = 0;
				String tenderType = getSafeString(rs, ++index);
				String maskAccountNumber = getSafeString(rs, ++index);
				String authorizationCode = rs.getString(++index);
				String entryMethod = rs.getString(++index);

				// Changed by Mohamed for defect 138
				String issuerID = handleIssuerID(rs.getString(++index));

				Date expDate = rs.getDate(++index);
				// I18N change - use the DateTimeServiceIfc to convert the date
				if (expDate != null) {
					expirationDate = DateTimeServiceLocator
							.getDateTimeService()
							.formatDate(
									expDate,
									Locale.getDefault(),
									JdbcUtilities.CREDIT_CARD_EXPIRATION_DATE_FORMAT);
				} else {
					expirationDate = null;
				}

				String merchantId = rs.getString(FIELD_MERCHANT_ID_INNOVITI);
				String terminalID = rs.getString(FIELD_TERMINAL_ID);
				String batchNumber = rs.getString(FIELD_BATCH_NUMBER);
				String invoiceID = rs.getString(FIELD_INVOICE_NUMBERCRE);
				String transApprovalTime = rs
						.getString(FIELD_TRANSACTION_APPROVAL_TIME);
				String hostResponseID = rs.getString(FIELD_HOST_RESPONSE_ID);
				String hostResponseMsg = rs
						.getString(FIELD_HOST_RESPONSE_MESSAGE);
				String approvalID = rs.getString(FIELD_APPROVAL_ID);
				String returnReferenceNumber = rs
						.getString(FIELD_RETURN_REFERENCE_NUMBER);
				/*
				 * Don't do any special processing on these, they are converted
				 * bitmaps
				 */
				// render a StringBuffer from the Blob
				// Blob content = rs.getBlob(++index);
				InputStream is = rs.getBinaryStream(++index);
				StringBuffer imageData = new StringBuffer();
				if (is != null) {
					imageData = getStringBufferFromStream(is);
				}

				String authorizationMethodCode = getSafeString(rs, ++index);
				String idType = getSafeString(rs, ++index);
				String idCountry = getSafeString(rs, ++index);
				String idState = getSafeString(rs, ++index);
				EYSDate idExpirationDate = timestampToEYSDate(rs, ++index);
				String authorizationSettlememntData = getSafeString(rs, ++index);
				Timestamp authorzationDateTime = rs.getTimestamp(++index);
				// Added new Fields for TND 001
				// Commented For upgradation
				/*
				 * Number keyId = new Integer(rs.getInt(
				 * FIELD_TENDER_AUTHORIZATION_DEBIT_CREDIT_CARD_CRYPTO_KEY_ID));
				 * String decryptedCardNumber =
				 * decryptCardNumber(encryptedCardNumber, keyId);
				 */
				EncipheredCardDataIfc cardData = FoundationObjectFactory
						.getFactory().createEncipheredCardDataInstance(null,
								maskAccountNumber, null);
				cardData.setCardName(issuerID);
				cardData.setCardType(tenderType);
				cardData.setCardNumberValid(true);
				lineItem.setEncipheredCardData(cardData);
				++index; // increasing the Value by Mahendra
				String bankCode = getSafeString(rs, ++index);
				String invoiceNo = getSafeString(rs, ++index);
				String transactionAcq = getSafeString(rs, ++index);
				String batchNo = getSafeString(rs, ++index);
				String tId = getSafeString(rs, ++index);
				String retrvl_ref = getSafeString(rs, ++index);
				String card_holder = getSafeString(rs, ++index);
				((MAXTenderChargeIfc) lineItem).setAcquiringBankCode(bankCode);
				((MAXTenderChargeIfc) lineItem).setInvoiceNumber(invoiceNo);
				((MAXTenderChargeIfc) lineItem)
						.setTransactionAcquirer(transactionAcq);
				((MAXTenderChargeIfc) lineItem).setBatchNumber(batchNo);
				((MAXTenderChargeIfc) lineItem).setTID(tId);
				((MAXTenderChargeIfc) lineItem)
						.setRetrievalRefNumber(retrvl_ref);
				PersonNameIfc person = new PersonName();
				person.setFirstName(card_holder);
				lineItem.setBearerName(person);
				// Commented For upgradation
				// lineItem.setCardNumber(decryptedCardNumber);
				lineItem.setAuthorizationCode(authorizationCode);
				lineItem.setEntryMethod(EntryMethod.valueOf(entryMethod));
				lineItem.setCardType(issuerID);

				lineItem.setExpirationDateString(expirationDate);
				lineItem.setAuthorizationMethod(authorizationMethodCode);
				// Commented For upgradation
				// lineItem.setIDType(idType);
				lineItem.setIDCountry(idCountry);
				lineItem.setIDState(idState);
				lineItem.setIDExpirationDate(idExpirationDate);
				lineItem.setSettlementData(authorizationSettlememntData);
				if (authorzationDateTime != null) {
					lineItem.setAuthorizedDateTime(new EYSDate(
							authorzationDateTime));
				}

				if (imageData.toString() != null
						&& !imageData.toString().equals("null")) {
					lineItem.setSignatureData(convertStringToPointArray(imageData
							.toString()));
				}

				if (transApprovalTime != null && hostResponseID != null) {
					if (merchantId != null)
						((MAXTenderCharge) lineItem).getResponseDate().put(
								"merchantID", merchantId);
					((MAXTenderCharge) lineItem).getResponseDate().put(
							"terminalID", terminalID);
					((MAXTenderCharge) lineItem).getResponseDate().put(
							"transApprovalTime", transApprovalTime);
					((MAXTenderCharge) lineItem).getResponseDate().put(
							"batchNumber", batchNumber);
					((MAXTenderCharge) lineItem).getResponseDate().put(
							"invoiceID", invoiceID);
					((MAXTenderCharge) lineItem).getResponseDate().put(
							"hostResponseID", hostResponseID);
					((MAXTenderCharge) lineItem).getResponseDate().put(
							"hostResponseMsg", hostResponseMsg);
					((MAXTenderCharge) lineItem).getResponseDate().put(
							"approvalID", approvalID);
					((MAXTenderCharge) lineItem).getResponseDate().put(
							"returnReferenceNumber", returnReferenceNumber);
				}
				if (idType != null) {
					if (idType.equals("1"))
						((MAXTenderCharge) lineItem).setEmiTransaction(true);
					else if (idType.equals("0"))
						((MAXTenderCharge) lineItem).setEmiTransaction(false);
				}
			} else {
				throw new DataException(DataException.NO_DATA,
						"readCreditDebitTenderLineItem");
			}
			rs.close();
		} catch (DataException de) {
			logger.warn("" + de + "");
			throw de;
		} catch (SQLException se) {
			dataConnection.logSQLException(se, "readCreditDebitTenderLineItem");
			throw new DataException(DataException.SQL_ERROR,
					"readCreditDebitTenderLineItem", se);
		} catch (Exception e) {
			logger.error(
					"An exception occurred reading CreditDebitTenderLineItem: ",
					e);
			throw new DataException(DataException.UNKNOWN,
					"readCreditDebitTenderLineItem", e);
		}
		if (logger.isDebugEnabled()) {
			logger.debug("JdbcReadTransaction.readCreditDebitTenderLineItem()");
		}
	}

	/**
	 * Method added for Credit Card Post Void
	 * 
	 * @param issuerID
	 * @return
	 */
	// Ashish : Start( added below method as it is not present in base 14)
	private String handleIssuerID(String issuerID) {
		// On post voiding if the txn is made by credit card
		// we need to handle the issuer id if the string contains PlutusOffline
		if (issuerID.equalsIgnoreCase("PlutusOffline")) {
			issuerID = "";
		}
		return issuerID;
	}

	// Ashish : End

	public TransactionIfc selectTransaction(JdbcDataConnection dataConnection,
			TransactionIfc inputTransaction, String orderID,
			LocaleRequestor localeRequestor) throws DataException {
		if (logger.isDebugEnabled())
			logger.debug("JdbcReadTransaction.selectTransaction()");
		SQLSelectStatement sql = new SQLSelectStatement();

		/*
		 * Add Table(s)
		 */
		sql.addTable(TABLE_TRANSACTION);

		/*
		 * Add Columns
		 */
		sql.addColumn(FIELD_TRANSACTION_TRAINING_FLAG);
		sql.addColumn(FIELD_TRANSACTION_END_DATE_TIMESTAMP);
		sql.addColumn(FIELD_TRANSACTION_BEGIN_DATE_TIMESTAMP);
		sql.addColumn(FIELD_TRANSACTION_TYPE_CODE);
		sql.addColumn(FIELD_OPERATOR_ID);
		sql.addColumn(FIELD_TRANSACTION_STATUS_CODE);
		sql.addColumn(FIELD_TENDER_REPOSITORY_ID);
		sql.addColumn(FIELD_TRANSACTION_POST_PROCESSING_STATUS_CODE);
		sql.addColumn(FIELD_TRANSACTION_SALES_ASSOCIATE_MODIFIED);

		/*
		 * Add Qualifier(s)
		 */
		sql.addQualifier(FIELD_RETAIL_STORE_ID + " = "
				+ getStoreID(inputTransaction));
		sql.addQualifier(FIELD_WORKSTATION_ID + " = "
				+ getWorkstationID(inputTransaction));
		sql.addQualifier(FIELD_TRANSACTION_SEQUENCE_NUMBER + " = "
				+ getTransactionSequenceNumber(inputTransaction));
		sql.addQualifier(FIELD_BUSINESS_DAY_DATE + " = "
				+ getBusinessDayString(inputTransaction));
		sql.addQualifier(FIELD_TRANSACTION_TRAINING_FLAG + " = "
				+ getTrainingMode(inputTransaction));

		// if a status code was passed in, use it
		if (inputTransaction.getTransactionStatus() != TransactionIfc.STATUS_UNKNOWN) {
			sql.addQualifier(FIELD_TRANSACTION_STATUS_CODE + " = "
					+ getStatusCode(inputTransaction));
		}

		TransactionIfc transaction = null;
		try {
			dataConnection.execute(sql.getSQLString());

			ResultSet rs = (ResultSet) dataConnection.getResult();

			if (!rs.next()) {
				logger.warn("JdbcReadTransaction: transaction not found!");
				throw new DataException(DataException.NO_DATA,
						"transaction not found");
			}

			/*
			 * Grab the fields selected from the database
			 */
			int index = 0;
			boolean trainingFlag = getBooleanFromString(rs, ++index);
			Timestamp endTimestamp = rs.getTimestamp(++index);
			Timestamp beginTimestamp = rs.getTimestamp(++index);
			int transType = rs.getInt(++index);
			String operatorID = getSafeString(rs, ++index);
			int statusCode = rs.getInt(++index);
			String tillID = rs.getString(++index);
			int postProcessingStatusCode = rs.getInt(++index);
			boolean saleAssociateModifiedFlag = getBooleanFromString(rs,
					++index);

			transaction = createTransaction(transType);

			transaction.setTrainingMode(trainingFlag);
			transaction.setTimestampBegin(timestampToEYSDate(beginTimestamp));
			transaction.setTimestampEnd(timestampToEYSDate(endTimestamp));
			transaction.setWorkstation(inputTransaction.getWorkstation());
			transaction.setTransactionSequenceNumber(inputTransaction
					.getTransactionSequenceNumber());
			transaction.setBusinessDay(inputTransaction.getBusinessDay());
			transaction.setTillID(tillID);
			transaction.setTransactionStatus(statusCode);
			transaction.setPostProcessingStatus(postProcessingStatusCode);
			// Changes start for Rev 1.3(Ashish: Employee Discount)
			/* India Localization-Rounding Logic Changes Starts Here */
			/*
			 * ((MAXSaleReturnTransactionIfc) transaction)
			 * .setRounding(((MAXSaleReturnTransactionIfc)
			 * inputTransaction).getRounding()); ((MAXSaleReturnTransactionIfc)
			 * transaction)
			 * .setRoundingDenominations(((MAXSaleReturnTransactionIfc)
			 * inputTransaction).getRoundingDenominations());
			 */
			/* India Localization-Rounding Logic Changes Ends Here */
			// Changes start for Rev 1.3(Ashish: Employee Discount)

			if (transaction instanceof SaleReturnTransactionIfc) {
				((SaleReturnTransactionIfc) transaction)
						.setSalesAssociateModifiedFlag(saleAssociateModifiedFlag);
			}

			if (transaction instanceof Transaction) {
				((Transaction) transaction).buildTransactionID();
			}

			rs.close();

			if (transaction.getTransactionType() == TransactionIfc.TYPE_ORDER_CANCEL
					|| transaction.getTransactionType() == TransactionIfc.TYPE_ORDER_COMPLETE
					|| transaction.getTransactionType() == TransactionIfc.TYPE_ORDER_PARTIAL
					|| transaction.getTransactionType() == TransactionIfc.TYPE_ORDER_INITIATE) {
				((OrderTransactionIfc) transaction).setOrderID(orderID);
			}

			// Store the cashier information in the transaction
			transaction.setCashier(getEmployee(dataConnection, operatorID));
			// Store the capture customer information in the transaction, if it
			// exists.
			transaction.setCaptureCustomer(selectCaptureCustomer(
					dataConnection, transaction, localeRequestor));

			readAllTransactionData(dataConnection, transaction,
					localeRequestor, false);

		} catch (DataException de) {
			logger.warn("" + de + "");
			throw de;
		} catch (SQLException se) {
			dataConnection.logSQLException(se, "transaction table");
			throw new DataException(DataException.SQL_ERROR,
					"transaction table", se);
		} catch (Exception e) {
			throw new DataException(DataException.UNKNOWN, "transaction table",
					e);
		}

		if (logger.isDebugEnabled())
			logger.debug("JdbcReadTransaction.selectTransaction()");

		return (transaction);
	}

	protected void readGiftCardTenderLineItem(
			JdbcDataConnection dataConnection, TransactionIfc transaction,
			TenderGiftCardIfc lineItem, int lineItemSequenceNumber)
			throws DataException {
		if (logger.isDebugEnabled())
			logger.debug("JdbcReadTransaction.readGiftCardTenderLineItem()");

		SQLSelectStatement sql = new SQLSelectStatement();
		// Table
		sql.setTable(TABLE_GIFT_CARD_TENDER_LINE_ITEM);
		// Fields
		sql.addColumn(FIELD_GIFT_CARD_SERIAL_NUMBER);
		sql.addColumn(FIELD_MASKED_GIFT_CARD_SERIAL_NUMBER);
		sql.addColumn(FIELD_CURRENCY_ID); // I18N
		sql.addColumn(FIELD_GIFT_CARD_ADJUDICATION_CODE);
		sql.addColumn(FIELD_TENDER_AUTHORIZATION_CARD_NUMBER_SWIPED_OR_KEYED_CODE);
		sql.addColumn(FIELD_AUTHORIZATION_METHOD_CODE);
		sql.addColumn(FIELD_GIFT_CARD_INITIAL_BALANCE);
		sql.addColumn(FIELD_GIFT_CARD_CURRENT_BALANCE);
		sql.addColumn(FIELD_GIFT_CARD_CREDIT_FLAG);
		sql.addColumn(FIELD_GIFT_CARD_REQUEST_TYPE);
		sql.addColumn(FIELD_TENDER_AUTHORIZATION_SETTLEMENT_DATA);
		sql.addColumn(FIELD_TENDER_AUTHORIZATION_DATE_TIME);
		// Ashish : Start added below lines
		sql.addColumn(FIELD_GIFT_CARD_AUTHORIZATION_CODE);
		sql.addColumn(FIELD_GIFT_CARD_EXP_DATE);
		sql.addColumn(FIELD_GIFT_CARD_TYPE);
		sql.addColumn(FIELD_GIFT_CARD_TRANS_ID);
		sql.addColumn(FIELD_GIFT_CARD_INVOICE_ID);
		sql.addColumn(FIELD_GIFT_CARD_BATCH_ID);
		// Ashish : End added above lines
		sql.addColumn(FIELD_TENDER_AUTHORIZATION_JOURNAL_KEY);
		sql.addColumn(FIELD_TENDER_AUTHORIZATION_MESSAGE_SEQUENCE_NUMBER);
		sql.addColumn(FIELD_TENDER_AUTHORIZATION_DATE);
		sql.addColumn(FIELD_TENDER_AUTHORIZATION_TIME);
		sql.addColumn(FIELD_GIFT_CARD_ACCOUNT_TYPE);
		sql.addColumn(FIELD_TENDER_AUTHORIZATION_TRANSACTION_TRACE_NUMBER);
		// sql.addColumn(FIELD_ISSUING_STORE_NUMBER,
		// getIssuingStoreNumber(lineItem));
		// Qualifiers
		sql.addQualifier(FIELD_RETAIL_STORE_ID + " = "
				+ getStoreID(transaction));
		sql.addQualifier(FIELD_WORKSTATION_ID + " = "
				+ getWorkstationID(transaction));
		sql.addQualifier(FIELD_TRANSACTION_SEQUENCE_NUMBER + " = "
				+ getTransactionSequenceNumber(transaction));
		sql.addQualifier(FIELD_BUSINESS_DAY_DATE + " = "
				+ getBusinessDayString(transaction));
		sql.addQualifier(FIELD_RETAIL_TRANSACTION_LINE_ITEM_SEQUENCE_NUMBER
				+ " = " + String.valueOf(lineItemSequenceNumber));

		try {
			dataConnection.execute(sql.getSQLString());
			ResultSet rs = (ResultSet) dataConnection.getResult();

			if (rs.next()) {
				int index = 0;
				String serialNumber = getSafeString(rs, ++index);
				String maskedGiftCardNumber = getSafeString(rs, ++index);
				EncipheredCardDataIfc cardData = FoundationObjectFactory
						.getFactory().createEncipheredCardDataInstance(
								serialNumber, maskedGiftCardNumber, null);
				lineItem.setEncipheredCardData(cardData);
				int currencyID = rs.getInt(++index); // I18N
				lineItem.setAuthorizationCode(getSafeString(rs, ++index));
				lineItem.setEntryMethod(EntryMethod.valueOf(getSafeString(rs,
						++index)));
				lineItem.setAuthorizationMethod(getSafeString(rs, ++index));
				CurrencyIfc initialBalance = DomainGateway
						.getBaseCurrencyInstance(getSafeString(rs, ++index));
				CurrencyIfc remainingBalance = DomainGateway
						.getBaseCurrencyInstance(getSafeString(rs, ++index));
				lineItem.setGiftCardCredit(getBooleanFromString(rs, ++index));
				lineItem.setRequestCode(rs.getString(++index));
				lineItem.setSettlementData(getSafeString(rs, ++index));
				Timestamp authDate = rs.getTimestamp(++index);
				if (authDate != null) {
					lineItem.setAuthorizedDateTime(new EYSDate(authDate));
				}
				// Ashish : Start added belo lines
				try {
					// MAXGiftCardIfc gc = (MAXGiftCardIfc) lineItem
					// .getGiftCard();
					MAXGiftCardIfc gc = (MAXGiftCardIfc) DomainGateway
							.getFactory().getGiftCardInstance();
					// MAXGiftCardIfc gc = instantiateGiftCard();
					String authCode = rs.getString(++index);
					gc.setQcApprovalCode(authCode);
					Timestamp expDate = rs.getTimestamp(++index);
					if (expDate != null) {
						gc.setExpirationDate(new EYSDate(expDate));
					}
					String cardType = rs.getString(++index);
					gc.setQcCardType(cardType);
					String transID = rs.getString(++index);
					gc.setQcTransactionId(transID);
					String invID = rs.getString(++index);
					gc.setQcInvoiceNumber(invID);
					String btchID = rs.getString(++index);
					lineItem.setJournalKey(getSafeString(rs, ++index));
					lineItem.setReferenceCode(getSafeString(rs, ++index));
					lineItem.setAuthorizationDate(getSafeString(rs, ++index));
					lineItem.setAuthorizationTime(getSafeString(rs, ++index));
					lineItem.setAccountType(getSafeString(rs, ++index));
					gc.setQcBatchNumber(btchID);
					gc.setInitialBalance(initialBalance);
					gc.setInitialBalance(initialBalance);
					gc.setCurrentBalance(remainingBalance);
					gc.setCardNumber(serialNumber);
					gc.setCurrencyID(currencyID); // I18N
					// changes for 2.7
					gc.setEntryMethod(lineItem.getEntryMethod());
					lineItem.setGiftCard(gc);

				} catch (Exception e) {
					logger.error(e);
				}
				// Ashish : Ends added above lines
				// Ashish : Start commenting below lines
				// GiftCardIfc giftCard = instantiateGiftCard();
				// giftCard.setInitialBalance(initialBalance);
				// giftCard.setCurrentBalance(Balance);
				// giftCard.getEncipheredCardData().setEncryptedAcctNumber(serialNumber);
				// giftCard.setCurrencyID(currencyID); // I18N
				// giftCard.setTraceNumber(getSafeString(rs, ++index));
				// lineItem.setGiftCard(giftCard);
				// Ashish : End commenting above lines
			} else {
				throw new DataException(DataException.NO_DATA,
						"readGiftCardTenderLineItem");
			}
			rs.close();
		} catch (DataException de) {
			logger.warn("" + de + "");
			throw de;
		} catch (SQLException se) {
			dataConnection.logSQLException(se, "readGiftCardTenderLineItem");
			throw new DataException(DataException.SQL_ERROR,
					"readGiftCardTenderLineItem", se);
		} catch (Exception e) {
			throw new DataException(DataException.UNKNOWN,
					"readGiftCardTenderLineItem", e);
		}

		if (logger.isDebugEnabled())
			logger.debug("JdbcReadTransaction.readGiftCardTenderLineItem()");
	}

	// ---------------------------------------------------------------------
	/**
	 * Selects advanced pricing rule from database. This action is in a separate
	 * method to facilitate extensibility.
	 * <p>
	 * 
	 * @param dataConnection
	 *            database connection
	 * @param rule
	 *            advanced pricing rule key
	 * @return advanced pricing rule
	 * @exception DataException
	 *                thrown if error occurs
	 **/
	// ---------------------------------------------------------------------
	// Ashish : Start added below method
	protected AdvancedPricingRuleIfc readAdvancedPricingRule(
			JdbcDataConnection dataConnection, AdvancedPricingRuleIfc rule)
			throws DataException {
		// return MAXJdbcPLUOperation.selectAdvancedPricingRule(dataConnection,
		// rule);
		// Return has to be cheched as per business flow during issue fix in
		// Upgradation
		return null;
	}

	// Ashish : End added above method

	// Added by Sakshi to deactivate the DR when suspend retrieve trxn
	// performed: Starts

	// Added by Rajeev for Wighted Item
	protected List<ItemDiscountStrategyIfc> selectRetailPriceModifiers(
			JdbcDataConnection dataConnection, TransactionIfc transaction,
			SaleReturnLineItemIfc lineItem, LocaleRequestor localeRequestor)
			throws DataException {

		if (logger.isDebugEnabled())
			logger.debug("JdbcReadTransaction.selectRetailPriceModifiers()");

		SQLSelectStatement sql = new SQLSelectStatement();
		/*
		 * Add Table(s)
		 */
		sql.addTable(TABLE_RETAIL_PRICE_MODIFIER);
		/*
		 * Add Column(s)
		 */
		sql.addColumn(FIELD_RETAIL_PRICE_EXTENDED_DISCOUNT_AMOUNT);
		sql.addColumn(FIELD_RETAIL_PRICE_USE_EXTENDED_DISCOUNT_FLAG);
		sql.addColumn(FIELD_RETAIL_PRICE_MODIFIER_DERIVATION_RULE_ID);
		sql.addColumn(FIELD_RETAIL_PRICE_MODIFIER_REASON_CODE);
		sql.addColumn(FIELD_RETAIL_PRICE_MODIFIER_PERCENT);
		sql.addColumn(FIELD_RETAIL_PRICE_MODIFIER_AMOUNT);
		sql.addColumn(FIELD_RETAIL_PRICE_MODIFIER_SEQUENCE_NUMBER);
		sql.addColumn(FIELD_PRICE_DERIVATION_RULE_SCOPE_CODE);
		sql.addColumn(FIELD_PRICE_DERIVATION_RULE_METHOD_CODE);
		sql.addColumn(FIELD_PRICE_DERIVATION_RULE_ASSIGNMENT_BASIS_CODE);
		sql.addColumn(FIELD_RETAIL_PRICE_MODIFIER_DISCOUNT_EMPLOYEE_ID);
		sql.addColumn(FIELD_RETAIL_PRICE_MODIFIER_DAMAGE_DISCOUNT);
		sql.addColumn(FIELD_PCD_INCLUDED_IN_BEST_DEAL);
		sql.addColumn(FIELD_ADVANCED_PRICING_RULE);
		sql.addColumn(FIELD_RETAIL_PRICE_MODIFIER_DISCOUNT_REFERENCE_ID);
		sql.addColumn(FIELD_RETAIL_PRICE_MODIFIER_DISCOUNT_REFERENCE_ID_TYPE_CODE);
		sql.addColumn(FIELD_RETAIL_PRICE_MODIFIER_DISCOUNT_TYPE_CODE);
		sql.addColumn(FIELD_RETAIL_PRICE_MODIFIER_STOCK_LEDGER_ACCOUNTING_DISPOSITION_CODE);
		sql.addColumn(FIELD_RETAIL_PRICE_MODIFIER_OVERRIDE_EMPLOYEE_ID);
		sql.addColumn(FIELD_RETAIL_PRICE_MODIFIER_OVERRIDE_ENTRY_METHOD_CODE);
		sql.addColumn(FIELD_PROMOTION_ID);
		sql.addColumn(FIELD_PROMOTION_COMPONENT_ID);
		sql.addColumn(FIELD_PROMOTION_COMPONENT_DETAIL_ID);
		sql.addColumn(FIELD_ORDER_LINE_ITEM_RETAIL_PRICE_MODIFIER_REFERENCE);

		/*
		 * Add Qualifier(s)
		 */
		sql.addQualifier(FIELD_RETAIL_STORE_ID + " = "
				+ getStoreID(transaction));
		sql.addQualifier(FIELD_WORKSTATION_ID + " = "
				+ getWorkstationID(transaction));
		sql.addQualifier(FIELD_TRANSACTION_SEQUENCE_NUMBER + " = "
				+ getTransactionSequenceNumber(transaction));
		sql.addQualifier(FIELD_RETAIL_TRANSACTION_LINE_ITEM_SEQUENCE_NUMBER
				+ " = " + lineItem.getLineNumber());
		sql.addQualifier(FIELD_BUSINESS_DAY_DATE + " = "
				+ getBusinessDayString(transaction));
		/*
		 * Add Ordering
		 */
		sql.addOrdering(FIELD_RETAIL_PRICE_MODIFIER_SEQUENCE_NUMBER + " ASC");

		Vector<ItemDiscountStrategyIfc> itemDiscounts = new Vector<ItemDiscountStrategyIfc>(
				2);
		String reasonCodeString = "";
		// Ashish : Start added below lines
		CurrencyIfc fixedPrice = null;
		CurrencyIfc discountAmount = null;
		// Ashish : End added above lines
		try {
			// System.out.println(sql.getSQLString());
			dataConnection.execute(sql.getSQLString());
			ResultSet rs = (ResultSet) dataConnection.getResult();

			while (rs.next()) {
				int index = 0;
				// Ashish : Start added below line
				CurrencyIfc runningTotal = DomainGateway
						.getBaseCurrencyInstance(BigDecimalConstants.ZERO_AMOUNT);
				// Ashish : End added above line
				CurrencyIfc extendedDiscountAmount = getCurrencyFromDecimal(rs,
						++index);
				boolean useExtendedDiscountFlag = getBooleanFromString(rs,
						++index);
				int ruleID = rs.getInt(++index);
				// Ashish : start added below line
				int reasonCode = rs.getInt(++index);
				// Ashish : end
				// changes for rev 1.5
				reasonCodeString = String.valueOf(reasonCode);// getSafeString(rs,
																// ++index);
				BigDecimal percent = getBigDecimal(rs, ++index);
				// CurrencyIfc amount = getCurrencyFromDecimal(rs, ++index);
				CurrencyIfc amount = getLongerCurrencyFromDecimal(rs, ++index);
				index = index + 1;
				int scopeCode = rs.getInt(++index);
				int methodCode = rs.getInt(++index);
				int assignmentBasis = rs.getInt(++index);
				String discountEmployeeID = getSafeString(rs, ++index);
				boolean isDamageDiscount = getBooleanFromString(rs, ++index);
				boolean isIncludedInBestDealFlag = getBooleanFromString(rs,
						++index);
				boolean isAdvancedPricingRuleFlag = getBooleanFromString(rs,
						++index);
				String referenceID = rs.getString(++index);
				String referenceIDCodeStr = getSafeString(rs, ++index);
				int typeCode = rs.getInt(++index);
				int accountingCode = rs.getInt(++index);
				String overrideEmployeeID = getSafeString(rs, ++index);
				int overrideEntryMethod = rs.getInt(++index);
				int promotionId = rs.getInt(++index);
				int promotionComponentId = rs.getInt(++index);
				int promotionComponentDetailId = rs.getInt(++index);
				int orderItemDiscountLineReference = rs.getInt(++index);

				LocalizedCodeIfc localizedCode = DomainGateway.getFactory()
						.getLocalizedCode();

				// Determine type
				if (ruleID == 0) // price override
				{
					localizedCode = getInitializedLocalizedReasonCode(
							dataConnection,
							transaction.getTransactionIdentifier().getStoreID(),
							reasonCodeString,
							CodeConstantsIfc.CODE_LIST_PRICE_OVERRIDE_REASON_CODES,
							localeRequestor);
					lineItem.modifyItemPrice(amount, localizedCode);
					if (!Util.isEmpty(overrideEmployeeID)) {
						SecurityOverrideIfc override = DomainGateway
								.getFactory().getSecurityOverrideInstance();
						override.setAuthorizingEmployee(overrideEmployeeID);
						override.setEntryMethod(EntryMethod
								.getEntryMethod(overrideEntryMethod));
						lineItem.getItemPrice().setPriceOverrideAuthorization(
								override);
					}
				} else
				// item discount
				{
					// If this flag is ture, use the extended discount amount to
					// reconstruct the
					// discount line item; no recalculation of the discount is
					// performed.
					ItemDiscountStrategyIfc itemDiscount = null;
					if (useExtendedDiscountFlag) {
						if (scopeCode == DiscountRuleConstantsIfc.DISCOUNT_SCOPE_TRANSACTION) // return
																								// item
																								// transaction
																								// discount
																								// audit
						{
							itemDiscount = DomainGateway
									.getFactory()
									.getReturnItemTransactionDiscountAuditInstance();
						} else // item discount audit
						{
							itemDiscount = DomainGateway.getFactory()
									.getItemDiscountAuditInstance();
							// For an item discount audit, unit discount amount
							// is for display
							// purpose only (on receipt and screen). The unit
							// amount is always positive.
							if (amount != null) {
								((ItemDiscountAuditIfc) itemDiscount)
										.setUnitDiscountAmount(amount.abs());
							}
						}
						itemDiscount.setDiscountAmount(extendedDiscountAmount);
						((ItemDiscountAuditStrategyIfc) itemDiscount)
								.setOriginalDiscountMethod(methodCode);
						// For an item discount audit, rate is for display
						// purpose only (on receipt and screen).
						if (methodCode == DISCOUNT_METHOD_PERCENTAGE) {
							itemDiscount.setDiscountRate(percent
									.movePointLeft(2));
						}
					} else {
						switch (methodCode) {
						case DISCOUNT_METHOD_PERCENTAGE: {
							itemDiscount = DomainGateway.getFactory()
									.getItemDiscountByPercentageInstance();
							itemDiscount.setDiscountRate(percent
									.movePointLeft(2));

							break;
						}
						case DISCOUNT_METHOD_AMOUNT: {
							if (amount.signum() == CurrencyIfc.POSITIVE
									|| amount.signum() == CurrencyIfc.ZERO) {
								itemDiscount = DomainGateway.getFactory()
										.getItemDiscountByAmountInstance();
								// Ashish : Start added below lines
								// itemDiscount.setDiscountAmount(amount);

								BigDecimal decimalQuantity = ((SaleReturnLineItem) ((DiscountTargetIfc) lineItem))
										.getItemQuantityDecimal();
								HashMap capillaryCoupon = new HashMap();
								// Below line added by Arif for total discount
								// in
								// case of post void
								if (transaction.getTransactionType() == 1
										&& reasonCode == 5170) {

									CurrencyIfc totalSaveAmount = amount
											.multiply(decimalQuantity);

									BigDecimal totalSaveAmountBD = new BigDecimal(
											totalSaveAmount.getDoubleValue());
									BigDecimal bigOne = new BigDecimal(1);
									totalSaveAmountBD = totalSaveAmountBD
											.divide(bigOne,
													1,
													totalSaveAmountBD.ROUND_HALF_EVEN);
									CurrencyIfc totalSaveAmountRounded = DomainGateway
											.getBaseCurrencyInstance(totalSaveAmountBD);
									itemDiscount
											.setDiscountAmount(totalSaveAmountRounded); // mohan
									capillaryCoupon
											.put(MAXCodeConstantsIfc.CAPILLARY_COUPON_DISCOUNT_TYPE,
													"ABS");
								} else {
									itemDiscount.setDiscountAmount(amount);
								}

								// Changes for Rev 1.1 : Starts
								// Changes for Rev 1.5 : Starts
								if (!capillaryCoupon.isEmpty())
									((MAXItemDiscountStrategyIfc) itemDiscount)
											.setCapillaryCoupon(capillaryCoupon);
								// Changes for Rev 1.1 : Ends
							} else if (amount.signum() == CurrencyIfc.NEGATIVE) {
								itemDiscount = DomainGateway
										.getFactory()
										.getReturnItemTransactionDiscountAuditInstance();
								BigDecimal decimalQuantity = ((SaleReturnLineItem) ((DiscountTargetIfc) lineItem))
										.getItemQuantityDecimal();
								if (transaction.getTransactionType() == 1
										&& reasonCode == 7) {
									CurrencyIfc totalSaveAmount = amount
											.multiply(decimalQuantity.abs());

									BigDecimal totalSaveAmountBD = new BigDecimal(
											totalSaveAmount.getDoubleValue());
									BigDecimal bigOne = new BigDecimal(1);
									totalSaveAmountBD = totalSaveAmountBD
											.divide(bigOne,
													1,
													totalSaveAmountBD.ROUND_HALF_EVEN);
									CurrencyIfc totalSaveAmountRounded = DomainGateway
											.getBaseCurrencyInstance(totalSaveAmountBD);
									itemDiscount
											.setDiscountAmount(totalSaveAmountRounded);
								} else {
									itemDiscount.setDiscountAmount(amount);
								}

								itemDiscount
										.setDiscountMethod(DiscountRuleConstantsIfc.DISCOUNT_METHOD_AMOUNT);
								itemDiscount.setReasonCode(reasonCode);
								itemDiscount
										.setAssignmentBasis(assignmentBasis);
								itemDiscount
										.setDiscountEmployee(discountEmployeeID);
								itemDiscount
										.setDamageDiscount(isDamageDiscount);
								itemDiscount.setTypeCode(typeCode);
								itemDiscount
										.setAccountingMethod(accountingCode);
							}
							// Ashish : end below line

							break;
						}
						case DISCOUNT_METHOD_FIXED_PRICE: {
							itemDiscount = DomainGateway
									.getFactory()
									.getItemDiscountByFixedPriceStrategyInstance();
							itemDiscount.setDiscountAmount(amount);

							break;
						}
						// Ashish : Start add below lines
						// Added by Rajeev for Wighted Item
						case DISCOUNT_METHOD_WEIGHTED_ITEM: {
							// tempStrategy =
							// DomainGateway.getFactory().getItemDiscountByFixedPriceStrategyInstance();
							itemDiscount = DomainGateway
									.getFactory()
									.getItemDiscountByFixedPriceStrategyInstance();
							CurrencyIfc targetPrice = ((DiscountTargetIfc) lineItem)
									.getExtendedDiscountedSellingPrice();
							BigDecimal decimalQuantity = ((SaleReturnLineItem) ((DiscountTargetIfc) lineItem))
									.getItemQuantityDecimal();
							PLUItemIfc plu = ((SaleReturnLineItemIfc) ((DiscountTargetIfc) lineItem))
									.getPLUItem();
							AdvancedPricingRuleIfc aPR[] = plu
									.getAdvancedPricingRules();
							BigDecimal checkQty = new BigDecimal("0.00");
							if (aPR != null) {
								for (int i = 0; i < aPR.length; i++) {
									BigDecimal thresQty = aPR[i]
											.getSourceThreshold()
											.getDecimalValue();
									int v = decimalQuantity.compareTo(thresQty);
									if (v == 1 || v == 0) {
										int t = thresQty.compareTo(checkQty);
										if (t == 1 || t == 0) {
											fixedPrice = aPR[i].getFixedPrice();
											checkQty = thresQty;
										}
									}
								}

							}
							discountAmount = amount; // Added by karni for
														// return issue
														//
							// targetPrice.divide(decimalQuantity);
							CurrencyIfc csp = targetPrice.add(discountAmount); // fixedPrice.multiply(decimalQuantity);
							// discountAmount = targetPrice.subtract(csp);
							// End here karni
							// changes for the wrong discount and groupin in
							// case of
							// weighted promo items

							// Changes for Code Merge CR prod Defects - Rev 2.8 @Purushotham Reddy
							itemDiscount.setDiscountAmount(discountAmount
									.divide(decimalQuantity));

							itemDiscount.setAssignmentBasis(assignmentBasis);
							fixedPrice = csp.subtract(targetPrice
									.subtract(discountAmount));
							// runningTotal =
							// runningTotal.subtract(targetPrice);
							runningTotal = runningTotal.add(csp);
							runningTotal = runningTotal.divide(decimalQuantity);
							break;
						}
						// Ashish : End added above lines
						}// end switch methodCode
						if (itemDiscount != null) {
							itemDiscount.setDiscountMethod(methodCode);
						}
					}

					// ReferenceID and TypeCode
					if (itemDiscount != null) {
						String ruleIDString = Integer.toString(ruleID);
						itemDiscount.setRuleID(ruleIDString);

						itemDiscount.setAssignmentBasis(assignmentBasis);
						itemDiscount.setDiscountEmployee(discountEmployeeID);
						setDiscountEmployeeIDOnTransaction(transaction,
								discountEmployeeID);
						itemDiscount.setDamageDiscount(isDamageDiscount);
						itemDiscount.setTypeCode(typeCode);
						itemDiscount.setAccountingMethod(accountingCode);

						itemDiscount.setReferenceID(referenceID);
						if (referenceIDCodeStr == null) {
							itemDiscount.setReferenceIDCode(0);
						} else {
							for (int i = 0; i < DiscountRuleConstantsIfc.REFERENCE_ID_TYPE_CODE.length; i++) {
								if (referenceIDCodeStr
										.equalsIgnoreCase(DiscountRuleConstantsIfc.REFERENCE_ID_TYPE_CODE[i])) {
									itemDiscount.setReferenceIDCode(i);
								}
							}
						}
						itemDiscount
								.setAdvancedPricingRule(isAdvancedPricingRuleFlag);
						if (isAdvancedPricingRuleFlag) {
							((DiscountTargetIfc) lineItem)
									.applyAdvancedPricingDiscount(itemDiscount);
						}

						itemDiscount
								.setIncludedInBestDeal(isIncludedInBestDealFlag);

						String codeListType = DiscountUtility
								.getDiscountReasonCodeList(itemDiscount);
						localizedCode = getLocalizedReasonCode(dataConnection,
								transaction.getTransactionIdentifier()
										.getStoreID(), reasonCodeString,
								codeListType, localeRequestor, ruleIDString);

						// discount names and reason code names are the
						// same, so set it here .
						if (localizedCode != null) {
							itemDiscount.setLocalizedNames(localizedCode
									.getText());
						} else {
							localizedCode = DomainGateway.getFactory()
									.getLocalizedCode();
							localizedCode.setCode(reasonCodeString);
						}
						itemDiscount.setReason(localizedCode);

						// Set Temporary Price Change Promotion IDs
						itemDiscount.setPromotionId(promotionId);
						itemDiscount
								.setPromotionComponentId(promotionComponentId);
						itemDiscount
								.setPromotionComponentDetailId(promotionComponentDetailId);
						itemDiscount
								.setOrderItemDiscountLineReference(orderItemDiscountLineReference);

						itemDiscounts.addElement(itemDiscount);
					} else
					// itemDiscount == null
					{
						logger.error("Unknown type of itemDiscount:  reasonCode="
								+ reasonCodeString
								+ " percent="
								+ percent
								+ " amount=" + amount + "");
					}
				}
			} // end while (rs.next())
			rs.close();
		} catch (SQLException exc) {
			dataConnection.logSQLException(exc, "Processing result set.");
			throw new DataException(DataException.SQL_ERROR,
					"selectRetailPriceModifiers", exc);
		}
		// changes for rev 1.5 start
		// Ashish : Start added below lines(below lines are removed from base
		// 14)
		// put vector into array
		/*
		 * int numDiscounts = itemDiscounts.size();
		 * Vector<ItemDiscountStrategyIfc> discounts = null; if (numDiscounts >
		 * 0) { discounts = new Vector<ItemDiscountStrategyIfc>(numDiscounts);
		 * itemDiscounts.addElement((ItemDiscountStrategyIfc) discounts); }
		 */
		// Ashish : End added above lines
		// changes for rev 1.5 end
		if (logger.isDebugEnabled())
			logger.debug("JdbcReadTransaction.selectRetailPriceModifiers()");

		return itemDiscounts;
	}

	/**
	 * Reads the sale return line items.
	 * <P>
	 * 
	 * @param dataConnection
	 *            a connection to the database
	 * @param transaction
	 *            the retail transaction
	 * @param retrieveStoreCoupons
	 *            designates whether or not to retrieve sotre coupon line items
	 * @return Array of SaleReturn Line items
	 * @exception DataException
	 *                thrown when an error occurs executing the SQL against the
	 *                DataConnection, or when processing the ResultSet
	 **/
	// ----------------------------------------------------------------------
	protected SaleReturnLineItemIfc[] selectSaleReturnLineItems(
			JdbcDataConnection dataConnection,
			SaleReturnTransactionIfc transaction,
			LocaleRequestor localeRequestor, boolean retrieveStoreCoupons)
			throws DataException {
		if (logger.isDebugEnabled())
			logger.debug("JdbcReadTransaction.selectSaleReturnLineItems()");

		SQLSelectStatement sql = new SQLSelectStatement();
		/*
		 * Add Table(s)
		 */
		sql.addTable(TABLE_SALE_RETURN_LINE_ITEM, ALIAS_SALE_RETURN_LINE_ITEM);
		sql.addTable(TABLE_RETAIL_TRANSACTION_LINE_ITEM,
				ALIAS_RETAIL_TRANSACTION_LINE_ITEM);
		sql.addTable(TABLE_ITEM, ALIAS_ITEM);
		/*
		 * Add Column(s)
		 */
		sql.addColumn(ALIAS_SALE_RETURN_LINE_ITEM + "."
				+ FIELD_GIFT_REGISTRY_ID);
		sql.addColumn(ALIAS_SALE_RETURN_LINE_ITEM + "." + FIELD_POS_ITEM_ID);
		sql.addColumn(ALIAS_SALE_RETURN_LINE_ITEM + "." + FIELD_ITEM_ID);
		sql.addColumn(ALIAS_SALE_RETURN_LINE_ITEM + "."
				+ FIELD_SALE_RETURN_LINE_ITEM_QUANTITY);
		sql.addColumn(ALIAS_SALE_RETURN_LINE_ITEM + "."
				+ FIELD_SALE_RETURN_LINE_ITEM_EXTENDED_AMOUNT);
		sql.addColumn(ALIAS_SALE_RETURN_LINE_ITEM + "."
				+ FIELD_SALE_RETURN_LINE_ITEM_VAT_AMOUNT);
		sql.addColumn(ALIAS_SALE_RETURN_LINE_ITEM + "."
				+ FIELD_SALE_RETURN_LINE_ITEM_TAX_INC_AMOUNT);
		// Ashish :Start added below line
		sql.addColumn(ALIAS_SALE_RETURN_LINE_ITEM + "."
				+ FIELD_SALE_RETURN_LINE_ITEM_MRP_AMOUNT);
		// Ashish : End added above line
		sql.addColumn(ALIAS_SALE_RETURN_LINE_ITEM + "."
				+ FIELD_RETAIL_TRANSACTION_LINE_ITEM_SEQUENCE_NUMBER);
		sql.addColumn(ALIAS_SALE_RETURN_LINE_ITEM + "." + FIELD_SERIAL_NUMBER);
		sql.addColumn(ALIAS_SALE_RETURN_LINE_ITEM + "."
				+ FIELD_SALE_RETURN_LINE_ITEM_RETURN_QUANTITY);
		sql.addColumn(ALIAS_SALE_RETURN_LINE_ITEM + "."
				+ FIELD_POS_ORIGINAL_TRANSACTION_ID);
		sql.addColumn(ALIAS_SALE_RETURN_LINE_ITEM + "."
				+ FIELD_ORIGINAL_BUSINESS_DAY_DATE);
		sql.addColumn(ALIAS_SALE_RETURN_LINE_ITEM + "."
				+ FIELD_ORIGINAL_RETAIL_TRANSACTION_LINE_ITEM_SEQUENCE_NUMBER);
		sql.addColumn(ALIAS_SALE_RETURN_LINE_ITEM + "."
				+ FIELD_ORIGINAL_RETAIL_STORE_ID);
		sql.addColumn(ALIAS_SALE_RETURN_LINE_ITEM + "."
				+ FIELD_MERCHANDISE_RETURN_FLAG);
		sql.addColumn(ALIAS_SALE_RETURN_LINE_ITEM + "."
				+ FIELD_MERCHANDISE_RETURN_REASON_CODE);
		sql.addColumn(ALIAS_SALE_RETURN_LINE_ITEM + "."
				+ FIELD_MERCHANDISE_RETURN_ITEM_CONDITION_CODE);
		sql.addColumn(ALIAS_SALE_RETURN_LINE_ITEM + "."
				+ FIELD_SALE_RETURN_LINE_ITEM_RESTOCKING_FEE_AMOUNT);
		sql.addColumn(ALIAS_SALE_RETURN_LINE_ITEM + "."
				+ FIELD_SALE_RETURN_LINE_ITEM_DEPOSIT_AMOUNT);
		sql.addColumn(ALIAS_SALE_RETURN_LINE_ITEM + "."
				+ FIELD_SALE_RETURN_LINE_ITEM_BALANCE_DUE);
		sql.addColumn(ALIAS_SALE_RETURN_LINE_ITEM + "."
				+ FIELD_SALE_RETURN_LINE_ITEM_PICKUP_CANCEL_PRICE);
		sql.addColumn(ALIAS_SALE_RETURN_LINE_ITEM + "."
				+ FIELD_SALE_RETURN_LINE_ITEM_PICKUP_INSTORE_PRICE);
		sql.addColumn(ALIAS_SALE_RETURN_LINE_ITEM + "."
				+ FIELD_ITEM_KIT_SET_CODE);
		sql.addColumn(ALIAS_SALE_RETURN_LINE_ITEM + "."
				+ FIELD_ITEM_COLLECTION_ID);
		sql.addColumn(ALIAS_SALE_RETURN_LINE_ITEM + "."
				+ FIELD_ITEM_KIT_HEADER_REFERENCE_ID);
		sql.addColumn(ALIAS_SALE_RETURN_LINE_ITEM + "." + FIELD_SEND_FLAG);
		sql.addColumn(ALIAS_SALE_RETURN_LINE_ITEM + "."
				+ FIELD_SHIPPING_CHARGE_FLAG);
		sql.addColumn(ALIAS_SALE_RETURN_LINE_ITEM + "."
				+ FIELD_SEND_LABEL_COUNT);
		sql.addColumn(ALIAS_SALE_RETURN_LINE_ITEM + "."
				+ FIELD_GIFT_RECEIPT_FLAG);
		sql.addColumn(ALIAS_SALE_RETURN_LINE_ITEM + "." + FIELD_ORDER_ID);
		sql.addColumn(ALIAS_SALE_RETURN_LINE_ITEM + "."
				+ FIELD_ORDER_LINE_ITEM_SEQUENCE_NUMBER);
		sql.addColumn(ALIAS_SALE_RETURN_LINE_ITEM + "."
				+ FIELD_ITEM_ID_ENTRY_METHOD_CODE);
		sql.addColumn(ALIAS_SALE_RETURN_LINE_ITEM + "." + FIELD_SIZE_CODE);
		sql.addColumn(ALIAS_RETAIL_TRANSACTION_LINE_ITEM + "."
				+ FIELD_RETAIL_TRANSACTION_LINE_ITEM_VOID_FLAG);
		sql.addColumn(ALIAS_SALE_RETURN_LINE_ITEM + "."
				+ FIELD_ITEM_PRICEADJ_LINE_ITEM_FLAG);
		sql.addColumn(ALIAS_SALE_RETURN_LINE_ITEM + "."
				+ FIELD_ITEM_PRICEADJ_REFERENCE_ID);
		sql.addColumn(ALIAS_SALE_RETURN_LINE_ITEM + "."
				+ FIELD_RETURN_RELATED_ITEM_FLAG);
		sql.addColumn(ALIAS_SALE_RETURN_LINE_ITEM + "."
				+ FIELD_RELATED_ITEM_TRANSACTION_LINE_ITEM_SEQ_NUMBER);
		sql.addColumn(ALIAS_SALE_RETURN_LINE_ITEM + "."
				+ FIELD_REMOVE_RELATED_ITEM_FLAG);
		sql.addColumn(ALIAS_SALE_RETURN_LINE_ITEM + "."
				+ FIELD_RETAIL_TRANSACTION_RETRIEVED_FLAG);
		sql.addColumn(ALIAS_SALE_RETURN_LINE_ITEM + "."
				+ FIELD_RETAIL_TRANSACTION_LINE_ITEM_SALES_ASSC_FLAG);
		sql.addColumn(ALIAS_SALE_RETURN_LINE_ITEM + "."
				+ FIELD_RETAIL_TRANSACTION_LINE_ITEM_PREMANENT_RETAIL_PRICE);
		sql.addColumn(ALIAS_SALE_RETURN_LINE_ITEM + "."
				+ FIELD_RETAIL_TRANSACTION_LINE_ITEM_RECEIPT_DESCRIPTION);
		sql.addColumn(ALIAS_SALE_RETURN_LINE_ITEM + "."
				+ FIELD_RETAIL_TRANSACTION_LINE_ITEM_RECEIPT_DESCRIPTION_LOCALE);
		sql.addColumn(ALIAS_SALE_RETURN_LINE_ITEM + "."
				+ FIELD_RETAIL_TRANSACTION_LINE_ITEM_RESTOCKING_FEE_FLAG);
		sql.addColumn(ALIAS_SALE_RETURN_LINE_ITEM + "."
				+ FIELD_SERIALIZED_ITEM_VALIDATION_FLAG);
		sql.addColumn(ALIAS_SALE_RETURN_LINE_ITEM + "."
				+ FIELD_EXTERNAL_VALIDATION_SERIALIZED_ITEM_FLAG);
		sql.addColumn(ALIAS_SALE_RETURN_LINE_ITEM + "."
				+ FIELD_SERIALIZED_ITEM_EXTERNAL_SYSTEM_CREATE_UIN);
		sql.addColumn(ALIAS_SALE_RETURN_LINE_ITEM
				+ "."
				+ FIELD_RETAIL_TRANSACTION_LINE_ITEM_MERCHANDISE_HIERARCHY_LEVEL_CODE);
		sql.addColumn(ALIAS_SALE_RETURN_LINE_ITEM + "."
				+ FIELD_RETAIL_TRANSACTION_LINE_ITEM_SIZE_REQUIRED_FLAG);
		sql.addColumn(ALIAS_SALE_RETURN_LINE_ITEM + "."
				+ FIELD_RETAIL_TRANSACTION_LINE_ITEM_SALE_UNIT_OF_MEASURE_CODE);
		sql.addColumn(ALIAS_SALE_RETURN_LINE_ITEM + "."
				+ FIELD_RETAIL_TRANSACTION_LINE_ITEM_POS_DEPARTMENT_ID);
		sql.addColumn(ALIAS_SALE_RETURN_LINE_ITEM + "."
				+ FIELD_RETAIL_TRANSACTION_LINE_ITEM_SALE_ITEM_TYPE_ID);
		sql.addColumn(ALIAS_SALE_RETURN_LINE_ITEM + "."
				+ FIELD_RETAIL_TRANSACTION_LINE_ITEM_RETURN_PROHIBITED_FLAG);
		sql.addColumn(ALIAS_SALE_RETURN_LINE_ITEM
				+ "."
				+ FIELD_RETAIL_TRANSACTION_LINE_ITEM_EMPLOYEE_DISCOUNT_ALOWED_FLAG);
		sql.addColumn(ALIAS_SALE_RETURN_LINE_ITEM + "." + FIELD_TAX_GROUP_ID);
		sql.addColumn(ALIAS_SALE_RETURN_LINE_ITEM + "."
				+ FIELD_RETAIL_TRANSACTION_LINE_ITEM_TAXABLE_FLAG);
		sql.addColumn(ALIAS_SALE_RETURN_LINE_ITEM + "."
				+ FIELD_ITEM_DISCOUNT_FLAG);
		sql.addColumn(ALIAS_SALE_RETURN_LINE_ITEM + "."
				+ FIELD_ITEM_DAMAGE_DISCOUNT_FLAG);
		sql.addColumn(ALIAS_SALE_RETURN_LINE_ITEM
				+ "."
				+ FIELD_RETAIL_TRANSACTION_LINE_ITEM_MERCHANDISE_HIERARCHY_GROUP_ID);
		sql.addColumn(ALIAS_SALE_RETURN_LINE_ITEM + "."
				+ FIELD_RETAIL_TRANSACTION_LINE_ITEM_MANUFACTURER_ITEM_UPC);
		sql.addColumn(ALIAS_SALE_RETURN_LINE_ITEM + "."
				+ NON_RETRIEVED_ORIGINAL_RECEIPT_ID);
		sql.addColumn(ALIAS_SALE_RETURN_LINE_ITEM + "."
				+ FIELD_SALE_AGE_RESTRICTION_ID);
		sql.addColumn(ALIAS_SALE_RETURN_LINE_ITEM + "."
				+ FIELD_CLEARANCE_INDICATOR);
		sql.addColumn(ALIAS_SALE_RETURN_LINE_ITEM + "."
				+ FIELD_ITEM_PRICE_ENTRY_REQUIRED_FLAG);
		// Ashish : Start added below line
		sql.addColumn(ALIAS_SALE_RETURN_LINE_ITEM + "."
				+ FIELD_PROMO_DISCOUNT_ON_RECEIPT);
		// Ashish : End added above line
		// Changes for rev 1.8 starts
		sql.addColumn(ALIAS_ITEM + "."
				+ FIELD_ITEM_MERCHANDISE_CLASSIFICATION_CODE0);
		sql.addColumn(ALIAS_ITEM + "."
				+ FIELD_ITEM_MERCHANDISE_CLASSIFICATION_CODE1);
		sql.addColumn(ALIAS_ITEM + "."
				+ FIELD_ITEM_MERCHANDISE_CLASSIFICATION_CODE2);
		sql.addColumn(ALIAS_ITEM + "."
				+ FIELD_ITEM_MERCHANDISE_CLASSIFICATION_CODE3);
		sql.addColumn(ALIAS_ITEM + "."
				+ FIELD_ITEM_MERCHANDISE_CLASSIFICATION_CODE4);
		sql.addColumn(ALIAS_ITEM + "."
				+ FIELD_ITEM_MERCHANDISE_CLASSIFICATION_CODE5);
		sql.addColumn(ALIAS_ITEM + "."
				+ FIELD_ITEM_MERCHANDISE_CLASSIFICATION_CODE6);
		sql.addColumn(ALIAS_ITEM + "."
				+ FIELD_ITEM_MERCHANDISE_CLASSIFICATION_CODE7);
		sql.addColumn(ALIAS_ITEM + "."
				+ FIELD_ITEM_MERCHANDISE_CLASSIFICATION_CODE8);
		sql.addColumn(ALIAS_ITEM + "."
				+ FIELD_ITEM_MERCHANDISE_CLASSIFICATION_CODE9);
		// Changes for rev 1.8 ends
		/* Rev 1.9 changes starts */
		if (Gateway.getBooleanProperty("application", "GSTEnabled", true)) {
			sql.addColumn(ALIAS_ITEM + "." + FIELD_TAX_CATGEORY);
			// defect fix Rev 2.5 commented the below for defect gift
			// certificate was not getting retrieved in suspend retrieve flow
			// sql.addQualifier(ALIAS_ITEM + "." + FIELD_ITEM_ID + " = " +
			// ALIAS_SALE_RETURN_LINE_ITEM + "." + FIELD_ITEM_ID );
		}
		/* Rev 1.9 changes ends */
		/*
		 * Add Qualifier(s)
		 */
		// For the specific transaction only
		sql.addQualifier(ALIAS_SALE_RETURN_LINE_ITEM + "."
				+ FIELD_RETAIL_STORE_ID + " = " + getStoreID(transaction));
		sql.addQualifier(ALIAS_SALE_RETURN_LINE_ITEM + "."
				+ FIELD_WORKSTATION_ID + " = " + getWorkstationID(transaction));
		sql.addQualifier(ALIAS_SALE_RETURN_LINE_ITEM + "."
				+ FIELD_TRANSACTION_SEQUENCE_NUMBER + " = "
				+ getTransactionSequenceNumber(transaction));
		sql.addQualifier(ALIAS_SALE_RETURN_LINE_ITEM + "."
				+ FIELD_BUSINESS_DAY_DATE + " = "
				+ getBusinessDayString(transaction));
		sql.addQualifier(ALIAS_RETAIL_TRANSACTION_LINE_ITEM + "."
				+ FIELD_RETAIL_STORE_ID + "=" + ALIAS_SALE_RETURN_LINE_ITEM
				+ "." + FIELD_RETAIL_STORE_ID);
		sql.addQualifier(ALIAS_RETAIL_TRANSACTION_LINE_ITEM + "."
				+ FIELD_WORKSTATION_ID + " = " + ALIAS_SALE_RETURN_LINE_ITEM
				+ "." + FIELD_WORKSTATION_ID);
		sql.addQualifier(ALIAS_RETAIL_TRANSACTION_LINE_ITEM + "."
				+ FIELD_TRANSACTION_SEQUENCE_NUMBER + " = "
				+ ALIAS_SALE_RETURN_LINE_ITEM + "."
				+ FIELD_TRANSACTION_SEQUENCE_NUMBER);
		sql.addQualifier(ALIAS_RETAIL_TRANSACTION_LINE_ITEM + "."
				+ FIELD_RETAIL_TRANSACTION_LINE_ITEM_SEQUENCE_NUMBER + " = "
				+ ALIAS_SALE_RETURN_LINE_ITEM + "."
				+ FIELD_RETAIL_TRANSACTION_LINE_ITEM_SEQUENCE_NUMBER);
		sql.addQualifier(ALIAS_RETAIL_TRANSACTION_LINE_ITEM + "."
				+ FIELD_BUSINESS_DAY_DATE + " = " + ALIAS_SALE_RETURN_LINE_ITEM
				+ "." + FIELD_BUSINESS_DAY_DATE);
		// Changes for rev 1.8 starts
		sql.addQualifier(ALIAS_SALE_RETURN_LINE_ITEM + "." + FIELD_ITEM_ID
				+ " = " + ALIAS_ITEM + "." + FIELD_ITEM_ID + "(+)");
		// Changes for rev 1.8 ends
		// order by line item sequence number
		sql.addOrdering(ALIAS_SALE_RETURN_LINE_ITEM + "."
				+ FIELD_RETAIL_TRANSACTION_LINE_ITEM_SEQUENCE_NUMBER + " ASC");

		Vector<SaleReturnLineItemIfc> saleReturnLineItems = new Vector<SaleReturnLineItemIfc>(
				2);

		try {
			//System.out.println(sql.getSQLString());
			dataConnection.execute(sql.getSQLString());
			ResultSet rs = (ResultSet) dataConnection.getResult();
			TransactionTaxIfc transactionTax = transaction.getTransactionTax();

			HashMap<Integer, String> reasonCodeMap = new HashMap<Integer, String>(
					1);
			HashMap<Integer, String> itemConditionCodeMap = new HashMap<Integer, String>(
					1);
			while (rs.next()) {
				int index = 0;
				String hsn = rs
						.getString(FIELD_ITEM_MERCHANDISE_CLASSIFICATION_CODE6);
				
				String liquom = rs.getString(FIELD_ITEM_MERCHANDISE_CLASSIFICATION_CODE7);
				String liqcat = rs.getString(FIELD_ITEM_MERCHANDISE_CLASSIFICATION_CODE8);
				
				String giftRegistryID = getSafeString(rs, ++index);
				String posItemID = getSafeString(rs, ++index);
				String itemID = getSafeString(rs, ++index);
				// Ashish :Start added below lines
				// BigDecimal quantity = getBigDecimal(rs, ++index);
				String quantity1 = getSafeString(rs, ++index);
				BigDecimal quantity = new BigDecimal(quantity1);
				// Ashish :End added above line
				CurrencyIfc amount = getCurrencyFromDecimal(rs, ++index);
				/*
				 * CurrencyIfc itemTaxAmount = getLongerCurrencyFromDecimal(rs,
				 */++index;
				/*
				 * CurrencyIfc itemIncTaxAmount =
				 * getLongerCurrencyFromDecimal(rs,
				 */++index;
				// Ashish :Start added below line
				CurrencyIfc soldMRP = getCurrencyFromDecimal(rs, ++index);
				// Ashish :End added above line
				int sequenceNumber = rs.getInt(++index);
				String serialNumber = getSafeString(rs, ++index);
				BigDecimal quantityReturned = getBigDecimal(rs, ++index);
				String originalTransactionID = getSafeString(rs, ++index);
				EYSDate originalTransactionBusinessDay = getEYSDateFromString(
						rs, ++index);
				int originalTransactionLineNumber = rs.getInt(++index);
				String originalStoreID = getSafeString(rs, ++index);
				boolean returnFlag = getBooleanFromString(rs, ++index);
				String returnReasonCode = getSafeString(rs, ++index);
				String returnItemConditionCode = getSafeString(rs, ++index);
				if (StringUtils.isBlank(returnItemConditionCode)) {
					returnItemConditionCode = CodeConstantsIfc.CODE_UNDEFINED;
				}
				CurrencyIfc restockingFee = getCurrencyFromDecimal(rs, ++index);
				CurrencyIfc depositAmount = getCurrencyFromDecimal(rs, ++index);
				CurrencyIfc balanceDue = getCurrencyFromDecimal(rs, ++index);
				boolean pickupCancelledPrice = getBooleanFromString(rs, ++index);
				boolean pickupInStorePrice = getBooleanFromString(rs, ++index);
				int kitCode = rs.getInt(++index);
				String itemKitID = getSafeString(rs, ++index);
				int kitReference = rs.getInt(++index);
				String sendFlag = getSafeString(rs, ++index);
				String shippingChargeFlag = getSafeString(rs, ++index);
				int sendLabelCount = rs.getInt(++index);
				String giftReceiptStr = getSafeString(rs, ++index);
				String orderID = rs.getString(++index);
				int orderLineReference = rs.getInt(++index);
				String entryMethodCode = getSafeString(rs, ++index);
				String sizeCode = getSafeString(rs, ++index);
				String itemVoidFlag = getSafeString(rs, ++index);
				boolean isPriceAdjLineItem = rs.getBoolean(++index);
				int priceAdjReferenceID = rs.getInt(++index);
				boolean returnRelatedItemFlag = rs.getBoolean(++index);
				int relatedSeqNumber = rs.getInt(++index);
				boolean deleteRelatedItemFlag = rs.getBoolean(++index);
				boolean retrievedFlag = rs.getBoolean(++index);

				boolean saleAsscModifiedFlag = getBooleanFromString(rs, ++index);
				CurrencyIfc beforeOverride = getCurrencyFromDecimal(rs, ++index);
				String receiptDescription = getSafeString(rs, ++index);
				Locale receiptDescriptionLocale = LocaleUtilities
						.getLocaleFromString(getSafeString(rs, ++index));
				boolean restockingFeeFlag = rs.getBoolean(++index);
				boolean serializedItemFlag = rs.getBoolean(++index);
				boolean externalValidationSerializedItemFlag = rs
						.getBoolean(++index);
				boolean isPOSAllowedToCreateUIN = rs.getBoolean(++index);
				String productGroupID = getSafeString(rs, ++index);
				boolean sizeRequiredFlag = rs.getBoolean(++index);
				String unitOfMeasureCode = getSafeString(rs, ++index);
				String posDepartmentID = getSafeString(rs, ++index);
				int itemTypeID = rs.getInt(++index);
				boolean returnEligible = !(rs.getBoolean(++index));
				boolean employeeDiscountEligible = (rs.getBoolean(++index));
				int taxGroupId = rs.getInt(++index);
				boolean taxable = (rs.getBoolean(++index));
				boolean discountable = (rs.getBoolean(++index));
				boolean damageDiscountable = (rs.getBoolean(++index));
				String merchandiseHierarchyGroupID = getSafeString(rs, ++index);
				String manufacturerItemUPC = getSafeString(rs, ++index);
				String nonRetrievedOriginalReceiptId = getSafeString(rs,
						++index);
				int restrictiveAge = rs.getInt(++index);
				boolean clearanceIndicator = (rs.getBoolean(++index));
				boolean priceEntryRequired = (rs.getBoolean(++index));

				CurrencyIfc lineItemTaxAmount = DomainGateway
						.getBaseCurrencyInstance(BigDecimal.ZERO);
				CurrencyIfc lineItemIncTaxAmount = DomainGateway
						.getBaseCurrencyInstance(BigDecimal.ZERO);
				// Ashish :Start added below lines
				// Added by vaibhav
				String promodiscountForReceipt_new = getSafeString(rs, ++index);
				CurrencyIfc promoDiscountForReceipt;
				if (promodiscountForReceipt_new.equalsIgnoreCase("")) {
					promoDiscountForReceipt = null;
				} else {
					promoDiscountForReceipt = getCurrencyFromDecimal(rs, index);
				}
				// Ashish :End added above lines
				// Changes for rev 1.8 starts
				String merchandiseCode = null;
				ArrayList<MerchandiseClassificationIfc> classificationList = new ArrayList<MerchandiseClassificationIfc>();
				MerchandiseClassificationIfc merchandiseClassification = null;
				for (int i = 0; i < 10; i++) {
					merchandiseCode = getSafeString(rs, ++index);
					if (!Util.isEmpty(merchandiseCode)) {
						merchandiseClassification = DomainGateway.getFactory()
								.getMerchandiseClassificationInstance();
						merchandiseClassification
								.setIdentifier(merchandiseCode);
						classificationList.add(merchandiseClassification);
					} else {
						merchandiseClassification = DomainGateway.getFactory()
								.getMerchandiseClassificationInstance();
						merchandiseClassification.setIdentifier("");
						classificationList.add(merchandiseClassification);
					}
				}
				// Changes for rev 1.8 ends
				// create and initialize item price object
				ItemPriceIfc price = DomainGateway.getFactory()
						.getItemPriceInstance();
				ItemTaxIfc itemTax = DomainGateway.getFactory()
						.getItemTaxInstance();
				price.setExtendedSellingPrice(amount);
				price.setDiscountEligible(discountable);
				price.setExtendedRestockingFee(restockingFee);

				if (quantity.signum() != 0) {
					amount = amount.divide(new BigDecimal(quantity.toString()));
					if (restockingFee != null) {
						restockingFee = restockingFee.divide(new BigDecimal(
								quantity.toString()));
					}
				}

				price.setSellingPrice(amount);
				price.setPermanentSellingPrice(beforeOverride);
				// Ashish :Start added below lines
				// Setting SoldMRP
				((MAXItemPriceIfc) price).setSoldMRP(soldMRP);
				// Ashish :End added above lines
				price.setRestockingFee(restockingFee);

				// Obtain the previously calculated and saved line item tax
				itemTax = price.getItemTax();
				itemTax.setDefaultRate(transactionTax.getDefaultRate());
				itemTax.setDefaultTaxRules(transactionTax.getDefaultTaxRules());
				itemTax.setItemTaxAmount(lineItemTaxAmount);
				itemTax.setItemInclusiveTaxAmount(lineItemIncTaxAmount);
				// The tax mode is unknown at this point; It can be set from
				// SaleReturnTaxLineItem Table or the TaxModifier Table;
				// however,
				// if neither is available it must be explicitly set
				// depending on item taxability.
				itemTax.setTaxMode(TAX_MODE_NOT_SET);
				price.setItemTax(itemTax);

				// price.setItemTaxAmount(itemTaxAmount);
				price.setItemTaxAmount(lineItemTaxAmount);
				price.setItemInclusiveTaxAmount(lineItemIncTaxAmount);

				// price.setTaxGroupId(taxGroupID);
				price.setItemQuantity(quantity);

				SaleReturnLineItemIfc lineItem;
				// create and initialize appropriate line item object
				if (transaction.getTransactionType() == TransactionIfc.TYPE_ORDER_CANCEL
						|| transaction.getTransactionType() == TransactionIfc.TYPE_ORDER_COMPLETE
						|| transaction.getTransactionType() == TransactionIfc.TYPE_ORDER_PARTIAL
						|| transaction.getTransactionType() == TransactionIfc.TYPE_ORDER_INITIATE) {

					// code added for item realted to PDO
					if (transaction.getTransactionType() == TransactionIfc.TYPE_RETURN
							|| transaction.getTransactionStatus() == TransactionIfc.STATUS_SUSPENDED) {
						switch (kitCode) {
						case ItemKitConstantsIfc.ITEM_KIT_CODE_HEADER:
							lineItem = DomainGateway.getFactory()
									.getKitHeaderLineItemInstance();
							break;
						case ItemKitConstantsIfc.ITEM_KIT_CODE_COMPONENT:
							lineItem = DomainGateway.getFactory()
									.getKitComponentLineItemInstance();
							((KitComponentLineItemIfc) lineItem)
									.setItemKitID(itemKitID);
							break;
						default:
							lineItem = DomainGateway.getFactory()
									.getOrderLineItemInstance();
							break;
						}
					} else {
						lineItem = DomainGateway.getFactory()
								.getOrderLineItemInstance();
					}

				} else {
					switch (kitCode) {
					case ItemKitConstantsIfc.ITEM_KIT_CODE_HEADER:
						lineItem = DomainGateway.getFactory()
								.getKitHeaderLineItemInstance();
						break;
					case ItemKitConstantsIfc.ITEM_KIT_CODE_COMPONENT:
						lineItem = DomainGateway.getFactory()
								.getKitComponentLineItemInstance();
						((KitComponentLineItemIfc) lineItem)
								.setItemKitID(itemKitID);
						break;
					default:
						lineItem = DomainGateway.getFactory()
								.getSaleReturnLineItemInstance();
						break;
					}
				}

				lineItem.setPLUItemID(itemID);
				lineItem.setItemPrice(price);
				lineItem.setItemTaxAmount(lineItemTaxAmount);
				lineItem.setItemInclusiveTaxAmount(lineItemIncTaxAmount);
				lineItem.modifyItemQuantity(quantity);
				lineItem.setLineNumber(sequenceNumber);

				String defaultCategory = Gateway.getProperty("application",
						"DefaultCategory", "Bakery Item");
				((MAXSaleReturnLineItemIfc) lineItem)
						.setScansheetCategoryDesc(defaultCategory);
				((MAXSaleReturnLineItemIfc) lineItem)
						.setScansheetCategoryID("0");
				// set the order id & line item reference number
				lineItem.setOrderID(orderID);
				lineItem.setOrderLineReference(orderLineReference);
				// Ashish :Start added below lines
				((MAXSaleReturnLineItemIfc) lineItem)
						.setPromoDiscountForReceipt(promoDiscountForReceipt);
				// Ashish : End added above lines

				lineItem.setReceiptDescription(receiptDescription);
				lineItem.setReceiptDescriptionLocale(receiptDescriptionLocale);
				lineItem.getItemPrice().setEmployeeDiscountEligible(
						employeeDiscountEligible);

				lineItem.setDepositAmount(depositAmount);

				if (lineItem instanceof OrderLineItemIfc) {
					((OrderLineItemIfc) lineItem).setItemBalanceDue(balanceDue);
					((OrderLineItemIfc) lineItem)
							.setPriceCancelledDuringPickup(pickupCancelledPrice);
					((OrderLineItemIfc) lineItem)
							.setInStorePriceDuringPickup(pickupInStorePrice);
				}

				// set the KitHeaderReference
				lineItem.setKitHeaderReference(kitReference);

				if (serialNumber != null && serialNumber.length() > 0) {
					lineItem.setItemSerial(serialNumber);
				}
				lineItem.setQuantityReturned(quantityReturned);
				/*
				 * Should probably be a relationship fetched from the gift
				 * registry table
				 */
				if (giftRegistryID.length() > 0) {
					RegistryIDIfc registry = instantiateGiftRegistry();
					registry.setID(giftRegistryID);
					lineItem.modifyItemRegistry(registry, true);
				}

				// Return Item Original Transaction information is available
				if (returnFlag) {
					ReturnItemIfc ri = DomainGateway.getFactory()
							.getReturnItemInstance();

					if (originalTransactionID != null
							&& originalTransactionID.length() > 0) {
						// Create the transaction id.
						TransactionIDIfc id = DomainGateway.getFactory()
								.getTransactionIDInstance();
						id.setTransactionID(originalTransactionID);
						ri.setOriginalTransactionID(id);
						ri.setHaveReceipt(true);
					}
					ri.setNonRetrievedOriginalReceiptId(nonRetrievedOriginalReceiptId);

					if (originalTransactionBusinessDay != null) {
						ri.setOriginalTransactionBusinessDate(originalTransactionBusinessDay);
					}
					ri.setOriginalLineNumber(originalTransactionLineNumber);

					// DB2 does not support nested result sets, so the localized
					// reason codes needs to be retrieved after closing the
					// current
					// result set. Add the reason code to a map for later
					// retrieval.
					reasonCodeMap.put(sequenceNumber, returnReasonCode);
					itemConditionCodeMap.put(sequenceNumber,
							returnItemConditionCode);

					if (originalStoreID.equals(transaction.getWorkstation()
							.getStoreID())) {
						ri.setStore(transaction.getWorkstation().getStore());
					} else {
						StoreIfc store = DomainGateway.getFactory()
								.getStoreInstance();
						store.setStoreID(originalStoreID);
						ri.setStore(store);
						// Ashish : Start added below line
						// boolean retrievedFlag = false;
						if (transaction.getTransactionStatus() == TransactionIfc.STATUS_SUSPENDED) {
							// we preserve this value only if we are fetching a
							// transaction out
							// of the suspended state.
							retrievedFlag = rs
									.getBoolean(FIELD_RETAIL_TRANSACTION_RETRIEVED_FLAG);
							ri.setFromRetrievedTransaction(retrievedFlag);
						}
						lineItem.setReturnItem(ri);
						// Ashish :End added above line
					}
					if (transaction.getTransactionStatus() == TransactionIfc.STATUS_SUSPENDED
							|| transaction.getTransactionStatus() == TransactionIfc.STATUS_COMPLETED) {
						// we preserve this value only if we are fetching a
						// transaction out
						// of the suspended state.
						ri.setFromRetrievedTransaction(retrievedFlag);
					}
					lineItem.setReturnItem(ri);
				}

				/*
				 * Sales Associate defaults to the cashier. If it's different,
				 * that will be accounted for by the selectCommissionModifier
				 * below.
				 */
				if (transaction.getSalesAssociate() != null) {
					lineItem.setSalesAssociate(transaction.getSalesAssociate());
				} else {
					lineItem.setSalesAssociate(transaction.getCashier());
				}

				// Send Flag
				if (transaction.getTransactionType() != TransactionIfc.TYPE_RETURN) {
					if (sendFlag.equals("0")) {
						lineItem.setItemSendFlag(false);
					} else if (sendFlag.equals("1")) {
						lineItem.setItemSendFlag(true);
					}
					lineItem.setSendLabelCount(sendLabelCount);
					/*
					 * Shipping Charge Flag
					 */
					if (shippingChargeFlag.equals("0")) {
						lineItem.setShippingCharge(false);
					} else if (shippingChargeFlag.equals("1")) {
						lineItem.setShippingCharge(true);
					}
				}

				// Gift Receipt Flag
				if (giftReceiptStr.equals("1")) {
					lineItem.setGiftReceiptItem(true);
				}

				// Price Adjustment Flags
				lineItem.setPriceAdjustmentReference(priceAdjReferenceID);
				lineItem.setIsPriceAdjustmentLineItem(isPriceAdjLineItem);

				// set entry method (parse string from database to entry code
				EntryMethod entryMethod = EntryMethod.Manual;
				if (!Util.isEmpty(entryMethodCode)) {
					for (EntryMethod code : EntryMethod.values()) {
						if (entryMethod.equals(code.getIxRetailCode())
								|| entryMethod.equals(String.valueOf(code
										.getLegacyCode()))) {
							entryMethod = code;
							break;
						}
					}
				}

				lineItem.setEntryMethod(entryMethod);
				lineItem.setItemSizeCode(sizeCode);

				// defect fix by Nayya rev 2.4 starts
				if (lineItem instanceof MAXSaleReturnLineItemIfc) {
					// set HSN Number
					((MAXSaleReturnLineItemIfc) lineItem).setHSNNumber(hsn);
					//Added by vaibhav for solving retrieve issue
					if(!liquom.equalsIgnoreCase(" ")||! liqcat.equalsIgnoreCase(" ")) {
					((MAXSaleReturnLineItemIfc) lineItem).setliquom(liquom);
					((MAXSaleReturnLineItemIfc) lineItem).setliqcat(liqcat);
					}
				}
				// defect fix by Nayya rev 2.4 ends
				if (!itemVoidFlag.equals("1")) {
					saleReturnLineItems.addElement(lineItem);
				}

				lineItem.setRelatedItemReturnable(returnRelatedItemFlag);
				lineItem.setRelatedItemSequenceNumber(relatedSeqNumber);
				lineItem.setRelatedItemDeleteable(deleteRelatedItemFlag);
				lineItem.setSalesAssociateModifiedFlag(saleAsscModifiedFlag);
				// Generate a pluItem from the data in the Sale Return Line Item
				// table
				PLUItemIfc pluItem = instantiatePLUItem(productGroupID,
						kitCode, transaction.isTrainingMode());

				// defect fix by Nayya rev 2.4 starts
				if (pluItem != null && pluItem instanceof MAXPLUItemIfc) {
					((MAXPLUItemIfc) pluItem).setHsnNum(hsn);
					((MAXPLUItemIfc) pluItem).setliquom(liquom);
					((MAXPLUItemIfc) pluItem).setliqcat(liqcat);
				}
				// defect fix by Nayya rev 2.4 ends
				pluItem.setItemID(itemID);
				pluItem.setPosItemID(posItemID);
				pluItem.setItemSizeRequired(sizeRequiredFlag);
				pluItem.setDepartmentID(posDepartmentID);
				pluItem.setTaxable(taxable);
				pluItem.setTaxGroupID(taxGroupId);
				pluItem.setManufacturerItemUPC(manufacturerItemUPC);
				/* Rev 1.9 changes starts */
				if (Gateway.getBooleanProperty("application", "GSTEnabled",
						true)) {
					int taxCategory = rs.getInt(++index);
					if (pluItem instanceof MAXPLUItemIfc)
						((MAXPLUItemIfc) pluItem).setTaxCategory(taxCategory);
				}
				/* Rev 1.9 changes ends */
				getItemLevelMessages(dataConnection, pluItem);
				// Set kitid, if the line item is a KitComponentLineItem
				if (lineItem.isKitComponent()) {
					((KitComponentIfc) pluItem)
							.setItemKitID(((KitComponentLineItemIfc) lineItem)
									.getItemKitID());
				}
				ItemClassificationIfc itemClassification = DomainGateway
						.getFactory().getItemClassificationInstance();
				itemClassification.setRestockingFeeFlag(restockingFeeFlag);
				itemClassification.setSerializedItem(serializedItemFlag);
				if (externalValidationSerializedItemFlag) {
					itemClassification
							.setSerialEntryTime(STORE_RECEIVING_SERIALIZED_CAPTURE_TIME);
				} else {
					itemClassification
							.setSerialEntryTime(SALE_SERIALIZED_CAPTURE_TIME);
				}

				// If external validation is true and the serial number is not
				// available in the external system (e.g SIM)
				// then the following flag will allow POS to go through the sale
				// and request SIM to add the missing serial number.
				itemClassification
						.setExternalSystemCreateUIN(isPOSAllowedToCreateUIN);

				ProductGroupIfc pg = DomainGateway.getFactory()
						.getProductGroupInstance();
				pg.setGroupID(productGroupID);
				itemClassification.setGroup(pg);
				itemClassification.setItemType(itemTypeID);
				itemClassification.setReturnEligible(returnEligible);
				itemClassification
						.setEmployeeDiscountAllowedFlag(employeeDiscountEligible);
				itemClassification.setDiscountEligible(discountable);
				itemClassification
						.setDamageDiscountEligible(damageDiscountable);
				itemClassification.setPriceEntryRequired(priceEntryRequired);

				itemClassification
						.setMerchandiseHierarchyGroup(merchandiseHierarchyGroupID);
				// Changes for rev 1.8 starts
				itemClassification
						.setMerchandiseClassifications(classificationList);
				// Changes for rev 1.8 ends
				pluItem.setItemClassification(itemClassification);
				lineItem.setOnClearance(clearanceIndicator);
				UnitOfMeasureIfc pluUOM = DomainGateway.getFactory()
						.getUnitOfMeasureInstance();
				pluUOM.setUnitID(unitOfMeasureCode);
				pluItem.setUnitOfMeasure(pluUOM);
				pluItem.setRestrictiveAge(restrictiveAge);
				selectOptionalI18NPLUData(dataConnection, pluItem,
						localeRequestor, lineItem);
				// Changes for Rev 1.7 : Starts
				if (pluItem instanceof MAXPLUItem)
					getItemGroups(pluItem, dataConnection, localeRequestor);
				if (pluItem.getAdvancedPricingRules().length > 0) {
					lineItem.getItemPrice().setExtendedSellingPrice(
							((MAXPLUItemIfc) pluItem).getMaximumRetailPrice());
					lineItem.getItemPrice().setSellingPrice(
							((MAXPLUItemIfc) pluItem).getMaximumRetailPrice());
				}
				// Changes for Rev 1.7 : Ends
				lineItem.setPLUItem(pluItem);
			}

			// Set the localized reason code for return line items
			for (int lineItemCounter = 0; lineItemCounter < saleReturnLineItems
					.size(); lineItemCounter++) {
				/* Rev 1.9 changes starts */
				if (Gateway.getBooleanProperty("application", "GSTEnabled",
						true)) {
					PLUItemIfc[] pluItems = new PLUItem[saleReturnLineItems
							.size()];
					for (int index = 0; index < saleReturnLineItems.size(); index++) {
						pluItems[index] = new PLUItem();
						pluItems[index] = saleReturnLineItems.get(index)
								.getPLUItem();
					}
					/*
					 * if((transaction instanceof MAXSaleReturnTransactionIfc )
					 * &&
					 * ((MAXSaleReturnTransactionIfc)transaction).isIgstApplicable
					 * ()) assignTaxAssignments(dataConnection, pluItems,
					 * ((MAXSaleReturnTransactionIfc
					 * )transaction).getHomeState(),
					 * ((MAXSaleReturnTransactionIfc)transaction).getToState());
					 * else assignTaxAssignments(dataConnection, pluItems);
					 */
					for (int index = 0; index < saleReturnLineItems.size(); index++) {
						saleReturnLineItems.get(index).setPLUItem(
								pluItems[index]);
					}
				}
				/* Rev 1.9 changes ends */
				if (saleReturnLineItems.get(lineItemCounter).isReturnLineItem()) {
					int sequenceNumber = saleReturnLineItems.get(
							lineItemCounter).getLineNumber();

					// Get the reason code from the map
					String reasonCode = reasonCodeMap.get(sequenceNumber);

					// Retrieve localized reason code
					saleReturnLineItems
							.get(lineItemCounter)
							.getReturnItem()
							.setReason(
									getInitializedLocalizedReasonCode(
											dataConnection,
											transaction
													.getTransactionIdentifier()
													.getStoreID(),
											reasonCode,
											CodeConstantsIfc.CODE_LIST_RETURN_REASON_CODES,
											localeRequestor));

					// Get the item condition code from the map
					String itemConditionCode = itemConditionCodeMap
							.get(sequenceNumber);

					// retreve localized item condition code
					saleReturnLineItems
							.get(lineItemCounter)
							.getReturnItem()
							.setItemCondition(
									getInitializedLocalizedReasonCode(
											dataConnection,
											transaction
													.getTransactionIdentifier()
													.getStoreID(),
											itemConditionCode,
											CodeConstantsIfc.CODE_LIST_RETURN_ITEM_CONDITION_CODES,
											localeRequestor));
				}
			}

			// To relate related items with the lineitems
			for (int k = 0; k < saleReturnLineItems.size(); k++) {
				if (saleReturnLineItems.get(k).getRelatedItemSequenceNumber() != -1) {
					for (int l = 0; l < saleReturnLineItems.size(); l++) {
						if (saleReturnLineItems.get(l)
								.getRelatedItemSequenceNumber() == -1
								&& (saleReturnLineItems.get(k)
										.getRelatedItemSequenceNumber() == saleReturnLineItems
										.get(l).getLineNumber())) {
							{
								// line item k is line item l's related item
								saleReturnLineItems
										.get(l)
										.addRelatedItemLineItem(
												(SaleReturnLineItemIfc) saleReturnLineItems
														.get(k));
							}
						}
					}
				}
			}

			// Do separate query to find the possible line item tax amount from
			// tr_ltm_sls_rtn_tx table.
			for (int i = 0; i < saleReturnLineItems.size(); i++) {
				SaleReturnLineItemIfc srli = saleReturnLineItems.elementAt(i);
				int lineItemSequenceNumber = srli.getLineNumber();
				TaxInformationIfc[] taxInfoArray = selectSaleReturnLineItemTaxInformation(
						dataConnection, transaction, lineItemSequenceNumber);
				TaxInformationContainerIfc container = DomainGateway
						.getFactory().getTaxInformationContainerInstance();
				for (int j = 0; j < taxInfoArray.length; j++) {
					container.addTaxInformation(taxInfoArray[j]);
					srli.getItemPrice().getItemTax()
							.setTaxMode(taxInfoArray[j].getTaxMode());
					if (srli.getReturnItem() != null) {
						srli.getReturnItem().setTaxRate(
								taxInfoArray[j].getTaxPercentage()
										.movePointLeft(2).doubleValue());
					}
				}
				srli.getItemPrice().getItemTax()
						.setTaxInformationContainer(container);
				CurrencyIfc[] taxAmount = selectSaleReturnLineItemTaxAmount(
						dataConnection, transaction, lineItemSequenceNumber);
				srli.setItemTaxAmount(taxAmount[0]); // the first element is add
														// on item tax
				srli.setItemInclusiveTaxAmount(taxAmount[1]); // the second
																// element is
																// inclusive
																// item tax
				// Ashish :Start added below lines
				/* India Localization -Changes for Tax Starts here */
				MAXLineItemTaxBreakUpDetailIfc[] itemTaxBreakUpDetail = selectSaleReturnLineItemTaxBreakupInformation(
						dataConnection, srli, transaction);
				((MAXItemTaxIfc) srli.getItemPrice().getItemTax())
						.setLineItemTaxBreakUpDetail(itemTaxBreakUpDetail);
				/* India Localization -Changes for Tax Ends here */
				// Ashish :End added above lines
				if (transaction.getTransactionStatus() == TransactionIfc.STATUS_SUSPENDED) {
					if ((srli.getReturnItem() != null)
							&& srli.getReturnItem()
									.isFromRetrievedTransaction()) {
						// set the flag indicating that the line item is read
						// from database for any retrieved return
						// line item in a suspended transaction. This imples
						// that no more price modification can be
						// applied to the line item.
						srli.setFromTransaction(true);
					} else {
						// set the flag indicating that the line item may
						// subject to more price modification for
						// any line item in a suspended transactin except a
						// retrieved return item.
						srli.setFromTransaction(false);
					}
				} else {
					// set the flag indicating that the line item is read from
					// database for any non-suspended
					// transaction line item
					srli.setFromTransaction(true);
				}
			}

			// Grab auxiliary elements
			List<ItemDiscountStrategyIfc> itemDiscounts = null;
			for (SaleReturnLineItemIfc lineItem : saleReturnLineItems) {
				PLUItemIfc pluItem = lineItem.getPLUItem();

				// For tax exempt transactions add the tax rules to the plu
				// item. The tax rules are used by the tax engine while updating
				// the transaction totals.
				if (TaxConstantsIfc.TAX_MODE_EXEMPT == transaction
						.getTransactionTax().getTaxMode()) {
					pluItem = selectPLUItemTaxRules(dataConnection,
							transaction, pluItem);
				}

				if (lineItem.isKitComponent()
						&& pluItem instanceof KitComponentIfc) {
					((KitComponentIfc) pluItem).setKitComponent(true);
					pluItem.setItemID(lineItem.getPLUItemID());
					((KitComponentIfc) pluItem)
							.setItemKitID(((KitComponentLineItemIfc) lineItem)
									.getItemKitID());
				}

				// if gift card, find gift card number
				if (pluItem instanceof GiftCardPLUItemIfc) {
					selectGiftCard(dataConnection, transaction,
							lineItem.getLineNumber(),
							(GiftCardPLUItemIfc) pluItem, lineItem);
				}

				// if alterations item, set line item alteration item flag,
				// alteration item price and alteration
				if (pluItem instanceof AlterationPLUItemIfc) {
					lineItem.setAlterationItemFlag(true);
					AlterationPLUItemIfc altItem = (AlterationPLUItemIfc) pluItem;
					altItem.setPrice(lineItem.getSellingPrice());
					selectAlteration(dataConnection, transaction,
							lineItem.getLineNumber(), altItem);
				}
				// Ashish :Start added below lines
				if (pluItem instanceof MAXPLUItemIfc)
					((MAXPLUItemIfc) pluItem)
							.setMaximumRetailPrice(((MAXItemPriceIfc) lineItem
									.getItemPrice()).getSoldMRP());
				/*
				 * else ((PLUItemIfc)
				 * pluItem).setMaximumRetailPrice(((MAXItemPriceIfc)
				 * lineItem.getItemPrice()).getSoldMRP());
				 */
				// Ashish : End added above lines

				lineItem.setPLUItem(pluItem);
				lineItem.getItemTax().setTaxGroupId(pluItem.getTaxGroupID());

				if (lineItem.getReturnItem() != null) {
					lineItem.getReturnItem().setPLUItem(pluItem);
					lineItem.getReturnItem().setPrice(pluItem.getPrice());
				}

				// See if there is a commission modifier
				int sequenceNumber = lineItem.getLineNumber();

				try {
					String employeeID = selectCommissionModifier(
							dataConnection, transaction, sequenceNumber);
					String transactionLevelSalesAssociateEmployeeID = lineItem
							.getSalesAssociate().getEmployeeID();
					lineItem.setSalesAssociate(getEmployee(dataConnection,
							employeeID));
					if (!transactionLevelSalesAssociateEmployeeID
							.equals(employeeID)) {
						lineItem.setSalesAssociateModifiedAtLineItem(true);
					}
				} catch (DataException de) {
					// ignore
				}

				// Add item discounts for each line item
				itemDiscounts = selectRetailPriceModifiers(dataConnection,
						transaction, lineItem, localeRequestor);
				itemDiscounts
						.addAll(selectSaleReturnPriceModifiers(dataConnection,
								transaction, lineItem, localeRequestor));
				// Ahsih : Start added below lines
				PromotionLineItemIfc[] promotionLineItems = null;
				promotionLineItems = selectPromotionLineItems(dataConnection,
						transaction, lineItem);
				// Ashish : End added above line
				lineItem.getItemPrice().setItemDiscounts(
						itemDiscounts.toArray(new ItemDiscountStrategyIfc[0]));

				// Add item promotions for each line item
				/*
				 * PromotionLineItemIfc[] promotionLineItems =
				 * selectPromotionLineItems(dataConnection, transaction,
				 * lineItem);
				 */
				lineItem.getItemPrice().setPromotionLineItems(
						promotionLineItems);

				// See if there is an item tax entry
				// ItemTaxIfc tax = selectSaleReturnTaxModifier(dataConnection,
				// transaction, lineItem, localeRequestor);
				/* Rev 1.9 changes starts */
				ItemTaxIfc tax = null;
				if (!Gateway.getBooleanProperty("application", "GSTEnabled",
						true))
					tax = selectSaleReturnTaxModifier(dataConnection,
							transaction, lineItem, localeRequestor);
				/* Rev 1.9 changes starts */
				if (tax != null) {
					if (lineItem.getItemTaxMethod() == ItemTaxIfc.ITEM_TAX_EXTERNAL_RATE) {
						tax.setItemTaxAmount(lineItem.getItemPrice()
								.getItemTaxAmount());
						tax.setItemInclusiveTaxAmount(lineItem.getItemPrice()
								.getItemInclusiveTaxAmount());
					}

					lineItem.getItemPrice().setItemTax(tax);

					if (lineItem.getReturnItem() != null) {
						lineItem.getReturnItem().setTaxRate(
								tax.getDefaultRate());
					}
				}

				// When the sale return line item record was read, the tax mode
				// was unknown.
				// The mode can be set from vales in either
				// SaleReturnTaxLineItem
				// Table or the TaxModifier Table; however, if neither is
				// available,
				// this code set it explicitly based on the on item taxability.
				if (lineItem.getItemPrice().getItemTax().getTaxMode() == TAX_MODE_NOT_SET) {
					if (pluItem.getTaxable()) {
						lineItem.getItemPrice().getItemTax()
								.setTaxMode(TaxIfc.TAX_MODE_STANDARD);
					} else {
						lineItem.getItemPrice().getItemTax()
								.setTaxMode(TaxIfc.TAX_MODE_NON_TAXABLE);
					}
				}

				// add external order line item information
				selectExternalOrderLineItem(dataConnection, transaction,
						lineItem);

				lineItem.getItemPrice().calculateItemTotal();
			}
		} catch (SQLException exc) {
			dataConnection.logSQLException(exc, "Processing result set.");
			throw new DataException(DataException.SQL_ERROR,
					"error processing sale return line items", exc);
		}

		associateKitComponents(saleReturnLineItems);

		int numItems = saleReturnLineItems.size();
		SaleReturnLineItemIfc[] lineItems = new SaleReturnLineItemIfc[numItems];
		saleReturnLineItems.copyInto(lineItems);
		if (logger.isDebugEnabled())
			logger.debug("JdbcReadTransaction.selectSaleReturnLineItems");

		return (lineItems);
	}

	// Ashish :Start added below method
	protected MAXLineItemTaxBreakUpDetailIfc[] selectSaleReturnLineItemTaxBreakupInformation(
			JdbcDataConnection dataConnection, SaleReturnLineItemIfc lineItem,
			SaleReturnTransactionIfc transaction) throws DataException {
		if (logger.isDebugEnabled())
			logger.debug("JdbcReadTransaction.selectSaleReturnLineItemTaxBreakupInformation()");
		SQLSelectStatement sql = new SQLSelectStatement();
		sql.addTable("TR_LTM_TX_BRKUP");
		sql.addColumn("TX_BRKUP_TX_CD");
		sql.addColumn("TX_BRKUP_TX_CD_DSCR");
		sql.addColumn("TX_BRKUP_TX_RT");
		sql.addColumn("QU_ITM_LM_RTN_SLS");
		sql.addColumn("TX_BRKUP_TXBL_AMT");
		sql.addColumn("TX_BRKUP_TX_AMT");
		sql.addColumn("TX_FCT");
		sql.addColumn("TXBL_FCT");
		sql.addQualifier("AI_TRN = "
				+ getTransactionSequenceNumber(transaction));
		sql.addQualifier("ID_WS = " + getWorkstationID(transaction));
		sql.addQualifier("ID_STR_RT = " + getStoreID(transaction));
		sql.addQualifier("DC_DY_BSN = " + getBusinessDayString(transaction));
		sql.addQualifier("AI_LN_ITM = " + getItemSequenceNumber(lineItem));
		ArrayList lineItemTaxBreakUpList = new ArrayList();
		try {
			dataConnection.execute(sql.getSQLString());
			ResultSet rs;
			MAXLineItemTaxBreakUpDetailIfc lineItemTaxBreakUp;
			for (rs = (ResultSet) dataConnection.getResult(); rs.next(); lineItemTaxBreakUpList
					.add(lineItemTaxBreakUp)) {
				lineItemTaxBreakUp = new MAXLineItemTaxBreakUpDetail();
				MAXTaxAssignment taxAssignment = new MAXTaxAssignment();
				int index = 0;
				String taxCode = getSafeString(rs, ++index);
				String taxCodeDesc = getSafeString(rs, ++index);
				taxAssignment.setTaxCode(taxCode);
				taxAssignment.setTaxCodeDescription(taxCodeDesc);
				BigDecimal rate = getBigDecimal(rs, ++index);
				taxAssignment.setTaxRate(rate);
				getBigDecimal(rs, ++index);
				lineItemTaxBreakUp.setTaxAssignment(taxAssignment);
				lineItemTaxBreakUp.setTaxCode(taxCode);
				lineItemTaxBreakUp.setTaxCodeDescription(taxCodeDesc);
				lineItemTaxBreakUp.setTaxRate(rate);
				lineItemTaxBreakUp.setTaxableAmount(getCurrencyFromDecimal(rs,
						++index));
				lineItemTaxBreakUp.setTaxAmount(getCurrencyFromDecimal(rs,
						++index));
				BigDecimal txfct = getBigDecimal(rs, ++index, 10);
				BigDecimal txblfct = getBigDecimal(rs, ++index, 10);
				taxAssignment.setTaxAmountFactor(txfct);
				taxAssignment.setTaxableAmountFactor(txblfct);
				lineItemTaxBreakUp.setTaxAssignment(taxAssignment);
			}

			rs.close();
		} catch (SQLException se) {
			throw new DataException(1,
					"selectSaleReturnLineItemTaxBreakupInformation", se);
		} catch (DataException de) {
			throw de;
		} catch (Exception e) {
			throw new DataException(0,
					"selectSaleReturnLineItemTaxBreakupInformation", e);
		}
		MAXLineItemTaxBreakUpDetailIfc lineItemTaxBreakUpDetail[] = new MAXLineItemTaxBreakUpDetail[lineItemTaxBreakUpList
				.size()];
		for (int i = 0; i < lineItemTaxBreakUpDetail.length; i++)
			lineItemTaxBreakUpDetail[i] = (MAXLineItemTaxBreakUpDetailIfc) lineItemTaxBreakUpList
					.get(i);

		return lineItemTaxBreakUpDetail;
	}

	// Ashish :End added above method

	protected CustomerIfc readCustomer(JdbcDataConnection dataConnection,
			String customerID, LocaleRequestor localeRequestor)
			throws DataException, SQLException {
		if (logger.isDebugEnabled())
			logger.debug("JdbcReadTransaction.readCustomer()");

		ARTSCustomer customer;
		// Read all the customer information
		MAXJdbcReadCustomer customerOp = new MAXJdbcReadCustomer();
		customer = customerOp.selectCustomer(dataConnection, customerID);

		if (customer.getPosCustomer() != null) {
			customerOp.selectContactInfo(dataConnection, customer);
			customerOp.selectAddressInfo(dataConnection, customer);
			customerOp.selectEmailInfo(dataConnection, customer);
			customerOp.selectPhoneInfo(dataConnection, customer);
			customerOp.selectGroupInfo(dataConnection, customer);
			customerOp.selectBusinessInfo(dataConnection, customer,
					localeRequestor);
			if (!customer.getPosCustomer().isBusinessCustomer()) {
				customerOp.readCustomerLocale(dataConnection, customer);
			}
		}

		if (logger.isDebugEnabled())
			logger.debug("JdbcReadTransaction.readCustomer()");

		return (customer.getPosCustomer());
	}

	protected CustomerIfc readCustomer(JdbcDataConnection dataConnection,
			String customerID, boolean isLayawayTransaction,
			LocaleRequestor locale) throws DataException, SQLException {
		if (logger.isDebugEnabled())
			logger.debug("JdbcReadTransaction.readCustomer()");

		ARTSCustomer customer;
		// Read all the customer information
		// Ashish :Start added below line
		// JdbcReadCustomer customerOp = new JdbcReadCustomer();
		MAXJdbcReadCustomer customerOp = new MAXJdbcReadCustomer();
		customer = customerOp.selectCustomer(dataConnection, customerID);
		// Ashsih : End added above line
		if (isLayawayTransaction) {
			CustomerIfc customerSearchCriteria = DomainGateway.getFactory()
					.getCustomerInstance();
			customerSearchCriteria.setCustomerLinkedWithLayaway(true);
			customerSearchCriteria.setCustomerID(customerID);
			customer = customerOp.selectCustomer(dataConnection,
					customerSearchCriteria);
		} else {
			customer = customerOp.selectCustomer(dataConnection, customerID);
		}

		if (customer.getPosCustomer() != null) {
			customer.getPosCustomer().setLocaleRequestor(locale);
			customerOp.selectContactInfo(dataConnection, customer);
			customerOp.selectAddressInfo(dataConnection, customer);
			customerOp.selectEmailInfo(dataConnection, customer);
			customerOp.selectPhoneInfo(dataConnection, customer);
			customerOp.selectGroupInfo(dataConnection, customer);
			customerOp.selectBusinessInfo(dataConnection, customer, locale);
		}

		if (logger.isDebugEnabled())
			logger.debug("JdbcReadTransaction.readCustomer()");

		return (customer.getPosCustomer());
	}

	// Ashish : Start added below method
	protected TenderLineItemIfc instantiateTenderLineItem(String tenderType) {
		if (logger.isDebugEnabled())
			logger.debug("JdbcReadTransaction.instantiateTenderLineItem()");

		TenderLineItemIfc tenderLineItem = null;
		TenderTypeMapIfc tenderTypeMap = DomainGateway.getFactory()
				.getTenderTypeMapInstance();

		if (tenderType.equals(tenderTypeMap
				.getCode(TenderLineItemIfc.TENDER_TYPE_CASH))) {
			if (logger.isInfoEnabled())
				logger.info("Instantiating TenderCash");
			tenderLineItem = DomainGateway.getFactory().getTenderCashInstance();
			
		} else if (tenderType.equals(tenderTypeMap
				.getCode(TenderLineItemIfc.TENDER_TYPE_CHARGE))) {
			if (logger.isInfoEnabled())
				logger.info("Instantiating TenderCharge");
			tenderLineItem = DomainGateway.getFactory()
					.getTenderChargeInstance();
		} else if (tenderType.equals(tenderTypeMap
				.getCode(TenderLineItemIfc.TENDER_TYPE_CHECK))) {
			if (logger.isInfoEnabled())
				logger.info("Instantiating TenderCheck");
			tenderLineItem = DomainGateway.getFactory()
					.getTenderCheckInstance();
		} else if (tenderType.equals(tenderTypeMap
				.getCode(TenderLineItemIfc.TENDER_TYPE_TRAVELERS_CHECK))) {
			if (logger.isInfoEnabled())
				logger.info("Instantiating TenderTravelersCheck");
			tenderLineItem = DomainGateway.getFactory()
					.getTenderTravelersCheckInstance();
		} else if (tenderType.equals(tenderTypeMap
				.getCode(TenderLineItemIfc.TENDER_TYPE_GIFT_CARD))) {
			if (logger.isInfoEnabled())
				logger.info("Instantiating TenderGiftCard");
			tenderLineItem = DomainGateway.getFactory()
					.getTenderGiftCardInstance();
		} else if (tenderType.equals(tenderTypeMap
				.getCode(TenderLineItemIfc.TENDER_TYPE_GIFT_CERTIFICATE))) {
			if (logger.isInfoEnabled())
				logger.info("Instantiating TenderGiftCertificate");
			tenderLineItem = DomainGateway.getFactory()
					.getTenderGiftCertificateInstance();
		} else if (tenderType.equals(tenderTypeMap
				.getCode(TenderLineItemIfc.TENDER_TYPE_MAIL_BANK_CHECK))) {
			if (logger.isInfoEnabled())
				logger.info("Instantiating TenderMailBankCheck");
			tenderLineItem = DomainGateway.getFactory()
					.getTenderMailBankCheckInstance();
		} else if (tenderType.equals(tenderTypeMap
				.getCode(TenderLineItemIfc.TENDER_TYPE_DEBIT))) {
			if (logger.isInfoEnabled())
				logger.info("Instantiating TenderDebit");
			tenderLineItem = DomainGateway.getFactory()
					.getTenderDebitInstance();
		} else if (tenderType.equals(tenderTypeMap
				.getCode(TenderLineItemIfc.TENDER_TYPE_COUPON))) {
			if (logger.isInfoEnabled())
				logger.info("Instantiating TenderCoupon");
			tenderLineItem = DomainGateway.getFactory()
					.getTenderCouponInstance();
		} else if (tenderType.equals(tenderTypeMap
				.getCode(TenderLineItemIfc.TENDER_TYPE_STORE_CREDIT))) {
			if (logger.isInfoEnabled())
				logger.info("Instantiating TenderStoreCredit");
			tenderLineItem = DomainGateway.getFactory()
					.getTenderStoreCreditInstance();
		} else if (tenderType.equals(tenderTypeMap
				.getCode(TenderLineItemIfc.TENDER_TYPE_MALL_GIFT_CERTIFICATE))) {
			if (logger.isInfoEnabled())
				logger.info("Instantiating TenderMallCertificate");
			tenderLineItem = DomainGateway.getFactory()
					.getTenderGiftCertificateInstance();
			((TenderGiftCertificateIfc) tenderLineItem)
					.setTypeCode(TenderLineItemIfc.TENDER_TYPE_MALL_GIFT_CERTIFICATE);
		} else if (tenderType.equals(tenderTypeMap
				.getCode(TenderLineItemIfc.TENDER_TYPE_PURCHASE_ORDER))) {
			if (logger.isInfoEnabled())
				logger.info("Instantiating TenderPurchaseOrder");
			tenderLineItem = (TenderLineItemIfc) DomainGateway.getFactory()
					.getTenderPurchaseOrderInstance();
		} else if (tenderType.equals(tenderTypeMap
				.getCode(TenderLineItemIfc.TENDER_TYPE_MONEY_ORDER))) {
			if (logger.isInfoEnabled())
				logger.info("Instantiating TenderMoneyOrder");
			tenderLineItem = DomainGateway.getFactory()
					.getTenderMoneyOrderInstance();
		} else if (tenderType.equals(tenderTypeMap
				.getCode(TenderLineItemIfc.TENDER_TYPE_E_CHECK))) {
			if (logger.isInfoEnabled())
				logger.info("Instantiating TenderECheck");
			tenderLineItem = DomainGateway.getFactory()
					.getTenderCheckInstance();
			((TenderCheckIfc) tenderLineItem)
					.setTypeCode(TenderLineItemIfc.TENDER_TYPE_E_CHECK);
			// code added by atul shukla for paytm post void
		} else if (tenderType.equals(tenderTypeMap
				.getCode(MAXTenderLineItemIfc.TENDER_TYPE_PAYTM))) {
			if (logger.isInfoEnabled())
				logger.info("Instantiating TenderPaytm");
			if (DomainGateway.getFactory() instanceof MAXDomainObjectFactory)
				tenderLineItem = ((MAXDomainObjectFactory) DomainGateway
						.getFactory()).getTenderPaytmInstance();

			((MAXTenderPaytmIfc) tenderLineItem)
					.setTypeCode(MAXTenderLineItemIfc.TENDER_TYPE_PAYTM);
		}
		// Code added by Bhanu Priya
		else if (tenderType.equals(tenderTypeMap
				.getCode(MAXTenderLineItemIfc.TENDER_TYPE_MOBIKWIK))) {
			if (logger.isInfoEnabled())
				logger.info("Instantiating TenderMobikwik");
			if (DomainGateway.getFactory() instanceof MAXDomainObjectFactory)
				tenderLineItem = ((MAXDomainObjectFactory) DomainGateway
						.getFactory()).getTenderMobikwikInstance();

			((MAXTenderMobikwikIfc) tenderLineItem)
					.setTypeCode(MAXTenderLineItemIfc.TENDER_TYPE_MOBIKWIK);
		}

		else if (tenderType.equals(tenderTypeMap
				.getCode(MAXTenderLineItemIfc.TENDER_TYPE_LOYALTY_POINTS))) {
			if (logger.isInfoEnabled())
				logger.info("Instantiating TenderLoyaltyPoints");
			if (DomainGateway.getFactory() instanceof MAXDomainObjectFactory)
				tenderLineItem = ((MAXDomainObjectFactory) DomainGateway
						.getFactory()).getTenderLoyaltyPointsInstance();

			((MAXTenderLoyaltyPointsIfc) tenderLineItem)
					.setTypeCode(MAXTenderLineItemIfc.TENDER_TYPE_LOYALTY_POINTS);
		}

		else if (tenderType.equals(tenderTypeMap
				.getCode(MAXTenderLineItemIfc.TENDER_TYPE_ECOM_PREPAID))) {
			if (logger.isInfoEnabled())
				logger.info("Instantiating TenderEComPrepaid");
			if (DomainGateway.getFactory() instanceof MAXDomainObjectFactory)
				tenderLineItem = ((MAXDomainObjectFactory) DomainGateway
						.getFactory()).getTenderEComPrepaidInstance();
		} else if (tenderType.equals(tenderTypeMap
				.getCode(MAXTenderLineItemIfc.TENDER_TYPE_ECOM_COD))) {
			if (logger.isInfoEnabled())
				logger.info("Instantiating TenderEComCOD");
			if (DomainGateway.getFactory() instanceof MAXDomainObjectFactory)
				tenderLineItem = ((MAXDomainObjectFactory) DomainGateway
						.getFactory()).getTenderEComCODInstance();
		}
		
		// Rev 2.9 Changes done for POS-Amazon Pay Integration @Purushotham Reddy
		else if (tenderType.equals(tenderTypeMap
				.getCode(MAXTenderLineItemIfc.TENDER_TYPE_AMAZON_PAY ))) {
			if (logger.isInfoEnabled())
				logger.info("Instantiating TenderAmazonPay");
			if (DomainGateway.getFactory() instanceof MAXDomainObjectFactory)
				tenderLineItem = ((MAXDomainObjectFactory) DomainGateway
						.getFactory()).getTenderAmazonPayInstance();

			((MAXTenderAmazonPay) tenderLineItem)
					.setTypeCode(MAXTenderLineItemIfc.TENDER_TYPE_AMAZON_PAY);
		}
		else if (tenderType.equalsIgnoreCase("EWLT")) {
			if (logger.isInfoEnabled())
				logger.info("Instantiating TenderEWallet");
			if (DomainGateway.getFactory() instanceof MAXDomainObjectFactory)
				tenderLineItem = ((MAXDomainObjectFactory) DomainGateway.getFactory()).getTenderCashInstance();
		}

		else {
			logger.error("don't know how to instantiate tender type: "
					+ tenderType + "");
		}

		if (logger.isDebugEnabled())
			logger.debug("JdbcReadTransaction.instantiateTenderLineItem()");

		return (tenderLineItem);
	}

	// Ashish : End added above method

	// ---------------------------------------------------------------------
	/**
	 * Instantiate a GiftCardIfc object
	 * 
	 * @return GiftCardIfc
	 **/
	// ---------------------------------------------------------------------
	protected GiftCardIfc instantiateGiftCard() {
		if (logger.isDebugEnabled())
			logger.debug("JdbcReadTransaction.instantiateGiftCard()");

		GiftCardIfc giftCard = DomainGateway.getFactory().getGiftCardInstance();

		if (logger.isDebugEnabled())
			logger.debug("JdbcReadTransaction.instantiateGiftCard()");

		return giftCard;
	}

	static {
		logger = Logger
				.getLogger(max.retail.stores.domain.arts.MAXJdbcReadTransaction.class);
	}

	protected void selectSaleReturnTransaction(
			JdbcDataConnection dataConnection,
			SaleReturnTransactionIfc transaction,
			LocaleRequestor localeRequestor, boolean retrieveStoreCoupons)
			throws DataException {
		if (logger.isDebugEnabled())
			logger.debug("JdbcReadTransaction.selectSaleReturnTransaction()");
		SQLSelectStatement sql = new SQLSelectStatement();

		/*
		 * Add Table(s)
		 */
		sql.addTable(TABLE_RETAIL_TRANSACTION, ALIAS_RETAIL_TRANSACTION);
		sql.addTable(TABLE_ADDRESS, ALIAS_ADDRESS);
		sql.addTable(TABLE_RETAIL_STORE_GSTIN, ALIAS_RETAIL_STORE);

		/*
		 * Add Columns
		 */
		sql.addColumn(ALIAS_RETAIL_TRANSACTION + "." + FIELD_CUSTOMER_ID);
		sql.addColumn(ALIAS_RETAIL_TRANSACTION + "." + FIELD_IRS_CUSTOMER_ID);
		sql.addColumn(ALIAS_RETAIL_TRANSACTION + "." + FIELD_GIFT_REGISTRY_ID);
		sql.addColumn(ALIAS_RETAIL_TRANSACTION + "."
				+ FIELD_SUSPENDED_TRANSACTION_REASON_CODE);
		sql.addColumn(ALIAS_RETAIL_TRANSACTION + "." + FIELD_EMPLOYEE_ID);
		sql.addColumn(ALIAS_RETAIL_TRANSACTION + "." + FIELD_SEND_PACKAGE_COUNT);
		sql.addColumn(ALIAS_RETAIL_TRANSACTION + "." + FIELD_SEND_CUSTOMER_TYPE);
		sql.addColumn(ALIAS_RETAIL_TRANSACTION + "."
				+ FIELD_SEND_CUSTOMER_PHYSICALLY_PRESENT);
		sql.addColumn(ALIAS_RETAIL_TRANSACTION + "."
				+ FIELD_TRANSACTION_LEVEL_SEND);
		sql.addColumn(ALIAS_RETAIL_TRANSACTION + "." + FIELD_ORDER_ID);
		sql.addColumn(ALIAS_RETAIL_TRANSACTION + "."
				+ FIELD_ENCRYPTED_PERSONAL_ID_NUMBER);
		sql.addColumn(ALIAS_RETAIL_TRANSACTION + "."
				+ FIELD_MASKED_PERSONAL_ID_NUMBER);
		sql.addColumn(ALIAS_RETAIL_TRANSACTION + "."
				+ FIELD_PERSONAL_ID_REQUIRED_TYPE);
		sql.addColumn(ALIAS_RETAIL_TRANSACTION + "." + FIELD_PERSONAL_ID_STATE);
		sql.addColumn(ALIAS_RETAIL_TRANSACTION + "."
				+ FIELD_PERSONAL_ID_COUNTRY);
		sql.addColumn(ALIAS_RETAIL_TRANSACTION + "." + FIELD_AGE_RESTRICTED_DOB);
		// Ashish :Start added below lines

		sql.addColumn(ALIAS_RETAIL_TRANSACTION + "." + FIELD_ECOM_ORDER_NO);
		sql.addColumn(ALIAS_RETAIL_TRANSACTION + "." + FIELD_ECOM_ORDER_AMOUNT);
		sql.addColumn(ALIAS_RETAIL_TRANSACTION + "." + FIELD_ECOM_ORDER_TRANS_NO);
		
		sql.addColumn(ALIAS_RETAIL_TRANSACTION + "."+ FIELD_ERECEIPT_OTP);

		// Ashish : End above lines
		sql.addColumn(ALIAS_RETAIL_TRANSACTION + "." + FIELD_EXTERNAL_ORDER_ID);
		sql.addColumn(ALIAS_RETAIL_TRANSACTION + "."
				+ FIELD_EXTERNAL_ORDER_NUMBER);
		sql.addColumn(ALIAS_RETAIL_TRANSACTION + "."
				+ FIELD_EXTERNAL_ORDER_TYPE);
		sql.addColumn(ALIAS_RETAIL_TRANSACTION + "."
				+ FIELD_CONTRACT_SIGNATURE_REQUIRED_FLAG);
		sql.addColumn(ALIAS_ADDRESS + "." + FIELD_CONTACT_CITY);
		sql.addColumn(ALIAS_ADDRESS + "." + FIELD_CONTACT_STATE);
		sql.addColumn(ALIAS_ADDRESS + "." + FIELD_CONTACT_COUNTRY);
		sql.addColumn(ALIAS_ADDRESS + "." + FIELD_CONTACT_POSTAL_CODE);
		sql.addColumn(ALIAS_RETAIL_TRANSACTION + "."
				+ FIELD_TRANSACTION_RETURN_TICKET);
		sql.addColumn(ALIAS_RETAIL_TRANSACTION + "."
				+ FIELD_TRANSACTION_LEVEL_GIFT_RECEIPT_FLAG);
		sql.addColumn(ALIAS_RETAIL_TRANSACTION + "."
				+ FIELD_TRANSACTION_CURRENCY);
		sql.addColumn(ALIAS_RETAIL_TRANSACTION + "."
				+ FIELD_TRANSACTION_COUNTRY);

		/*
		 * Add Qualifier(s)
		 */
		sql.addQualifier(ALIAS_RETAIL_TRANSACTION + "." + FIELD_RETAIL_STORE_ID
				+ " = " + getStoreID(transaction));
		sql.addQualifier(ALIAS_RETAIL_TRANSACTION + "." + FIELD_WORKSTATION_ID
				+ " = " + getWorkstationID(transaction));
		sql.addQualifier(ALIAS_RETAIL_TRANSACTION + "."
				+ FIELD_TRANSACTION_SEQUENCE_NUMBER + " = "
				+ getTransactionSequenceNumber(transaction));
		sql.addQualifier(ALIAS_RETAIL_TRANSACTION + "."
				+ FIELD_BUSINESS_DAY_DATE + " = "
				+ getBusinessDayString(transaction));
		sql.addQualifier(ALIAS_RETAIL_TRANSACTION + "." + FIELD_RETAIL_STORE_ID
				+ " = " + ALIAS_RETAIL_STORE + "." + FIELD_RETAIL_STORE_ID);
		sql.addQualifier(ALIAS_RETAIL_STORE + "." + FIELD_PARTY_ID + " = "
				+ ALIAS_ADDRESS + "." + FIELD_PARTY_ID);
		sql.addQualifier(ALIAS_ADDRESS + "." + FIELD_CONTACT_CITY +" IS NOT NULL");
		// Ashish : Start added below lines
		// sql.addQualifier(ALIAS_ADDRESS + "." + FIELD_ADDRESS_TYPE_CODE +
		// " = " + "'0'");
		// Ashish : End added above lines

		try {
		//	System.out.println(sql.getSQLString()+"---------------------Anuj----------");
			dataConnection.execute(sql.getSQLString());

			ResultSet rs = (ResultSet) dataConnection.getResult();

			if (!rs.next()) {
				logger.warn("retail transaction not found!");
				throw new DataException(DataException.NO_DATA,
						"transaction not found");
			}

			int index = 0;
			String customerId = getSafeString(rs, ++index);
			String irsCustomerId = getSafeString(rs, ++index);
			String giftRegistryID = getSafeString(rs, ++index);
			String suspendReasonCode = getSafeString(rs, ++index);
			String salesAssociateID = getSafeString(rs, ++index);
			int sendPackagesCount = rs.getInt(++index);
			String sendCustomerType = getSafeString(rs, ++index);
			boolean sendCustomerPhysicallyPresent = getBooleanFromString(rs,
					++index);
			boolean transactionLevelSend = getBooleanFromString(rs, ++index);
			String orderID = getSafeString(rs, ++index);
			String encryptedPersonalIDNumber = getSafeString(rs, ++index);
			String maskedPersonalIDNumber = getSafeString(rs, ++index);
			String personalIDType = getSafeString(rs, ++index);
			String personalIDState = getSafeString(rs, ++index);
			String personalIDCountry = getSafeString(rs, ++index);
			EYSDate ageRestrictedDOB = getEYSDateFromString(rs, ++index);
			// Ashish : Start Added below lines

			String orderNo = getSafeString(rs, ++index);
			CurrencyIfc orderAmount = getCurrencyFromDecimal(rs, ++index);
			String onlineTransNo = getSafeString(rs, ++index);
			
			String eReceiptOTP = getSafeString(rs, ++index);
			
			// Ashish : End aded above lines
			String externalOrderID = getSafeString(rs, ++index);
			String externalOrderNumber = getSafeString(rs, ++index);
		//	System.out.println(externalOrderID+"-------------------------------------------");
			int externalOrderType = getExternalOrderType(getSafeString(rs,
					++index));
			boolean requireServiceContractFlag = getBooleanFromString(rs,
					++index);
			String storeCity = getSafeString(rs, ++index);
			String storeState = getSafeString(rs, ++index);
			String storeCountry = getSafeString(rs, ++index);
			String storePostalCode = getSafeString(rs, ++index);
			String returnTicket = getSafeString(rs, ++index);
			boolean transactionGiftReceiptAssigned = getBooleanFromString(rs,
					++index);
			String transactionCurrencyType = getSafeString(rs, ++index);
			String transactionCountryCode = getSafeString(rs, ++index);
			rs.close();
			// Ashish : Start added below lines

			if (transaction instanceof MAXSaleReturnTransaction) {
				((MAXSaleReturnTransaction) transaction)
						.seteComOrderNumber(orderNo);
				((MAXSaleReturnTransaction) transaction)
						.seteComOrderAmount(orderAmount);
				((MAXSaleReturnTransaction) transaction)
						.seteComOrderTransNumber(onlineTransNo);
				if (orderNo != null && !orderNo.trim().equals(""))
					((MAXSaleReturnTransaction) transaction)
							.seteComSendTransaction(true);
				((MAXSaleReturnTransaction) transaction)
				.setGSTINNumber(externalOrderID);
				
			}
			// Changes for  E-Receipt Integration With Karnival
			if (transaction instanceof MAXSaleReturnTransaction && eReceiptOTP!=null) {
				((MAXSaleReturnTransaction) transaction).setEReceiptOTP(eReceiptOTP);
			}

			// Ashish : Added above lines
			transaction.setCustomerId(customerId);
			if (irsCustomerId != null && irsCustomerId.length() > 0) {
				IRSCustomerIfc irsCustomer = readIRSCustomer(dataConnection,
						irsCustomerId);

				// Read Localized personald ID Code
				irsCustomer
						.setLocalizedPersonalIDCode(getInitializedLocalizedReasonCode(
								dataConnection,
								transaction.getTransactionIdentifier()
										.getStoreID(),
								irsCustomer.getLocalizedPersonalIDCode()
										.getCode(),
								CodeConstantsIfc.CODE_LIST_PAT_CUSTOMER_ID_TYPES,
								localeRequestor));

				transaction.setIRSCustomer(irsCustomer);
			}

			// If there is a default gift registry associated with the
			// transaction, instantiate the GiftRegistry
			if (!(Util.isEmpty(giftRegistryID))) {
				RegistryIDIfc registry = instantiateGiftRegistry();
				registry.setID(giftRegistryID);
				transaction.setDefaultRegistry(registry);
			}

			// Read Localized Reason Code
			transaction
					.setSuspendReason(getInitializedLocalizedReasonCode(
							dataConnection,
							transaction.getTransactionIdentifier().getStoreID(),
							suspendReasonCode,
							CodeConstantsIfc.CODE_LIST_TRANSACTION_SUSPEND_REASON_CODES,
							localeRequestor));

			try {
				transaction.setSalesAssociate(getEmployeeHeader(dataConnection,
						salesAssociateID));
			} catch (DataException checkEmployeeNotFound) {
				// Since empty/not found Sales Associate id could exist in
				// transaction and the
				// sales associate id here is retrieved from particular
				// transaction saved,
				// transaction is set with employee object using the sales
				// associate id
				// retrieved. For error codes other than for not found, data
				// exception is thrown
				if (checkEmployeeNotFound.getErrorCode() == DataException.NO_DATA) {
					PersonNameIfc name = DomainGateway.getFactory()
							.getPersonNameInstance();
					EmployeeIfc employee = DomainGateway.getFactory()
							.getEmployeeInstance();
					employee.setEmployeeID(salesAssociateID);
					name.setFirstName(salesAssociateID);
					employee.setPersonName(name);
					transaction.setSalesAssociate(employee);
				} else {
					throw checkEmployeeNotFound;
				}
			}

			/*
			  Transaction Tax MUST BE FIRST! When we add the line items or send
			  items, the default tax information has to be setup.
			 
			TransactionTaxIfc transactionTax = selectTaxLineItem(
					dataConnection, transaction,
					getLocaleRequestor(transaction));

			if (transactionTax.getTaxMode() == TaxIfc.TAX_MODE_EXEMPT) {
				selectTaxExemptionModifier(dataConnection, transaction,
						transactionTax);
			}
			transaction.setTransactionTax(transactionTax);

			// Set the shipping information.
			if (sendPackagesCount > 0) {
				transaction.setSendPackageCount(sendPackagesCount);
				readTransactionShippings(dataConnection, transaction,
						localeRequestor);
			}

			if (sendCustomerType.equals("0")) {
				transaction.setSendCustomerLinked(true);
			} else {
				transaction.setSendCustomerLinked(false);
			}
			transaction
					.setCustomerPhysicallyPresent(sendCustomerPhysicallyPresent);
			transaction.setTransactionLevelSendAssigned(transactionLevelSend);
			// Set the store address information
			transaction.getWorkstation().getStore().getAddress()
					.setCity(storeCity);
			transaction.getWorkstation().getStore().getAddress()
					.setState(storeState);
			transaction.getWorkstation().getStore().getAddress()
					.setCountry(storeCountry);
			transaction.getWorkstation().getStore().getAddress()
					.setPostalCode(storePostalCode);

			transaction.setReturnTicket(returnTicket);
			transaction
					.setTransactionGiftReceiptAssigned(transactionGiftReceiptAssigned);

			// Set the personal ID information
			if (!(Util.isEmpty(maskedPersonalIDNumber))) {
				CustomerInfoIfc customerInfo = transaction.getCustomerInfo();
				if (customerInfo == null) {
					customerInfo = DomainGateway.getFactory()
							.getCustomerInfoInstance();
				}

				// Read Localized Reason Code
				if (!Util.isEmpty(personalIDType)) {
					customerInfo
							.setLocalizedPersonalIDType(getInitializedLocalizedReasonCode(
									dataConnection,
									transaction.getTransactionIdentifier()
											.getStoreID(),
									personalIDType,
									CodeConstantsIfc.CODE_LIST_CAPTURE_CUSTOMER_ID_TYPES,
									localeRequestor));
				}

				EncipheredDataIfc personalIDNumber = FoundationObjectFactory
						.getFactory().createEncipheredDataInstance(
								encryptedPersonalIDNumber,
								maskedPersonalIDNumber);
				customerInfo.setPersonalID(personalIDNumber);
				customerInfo.setPersonalIDState(personalIDState);
				customerInfo.setPersonalIDCountry(personalIDCountry);
				transaction.setCustomerInfo(customerInfo);
			}

			// Set the age restricted DOB
			transaction.setAgeRestrictedDOB(ageRestrictedDOB);
			transaction.setTransactionCountryCode(transactionCountryCode);

			// Set external order info
			// Ashish : Start commenting below line as per MAX 12
			/*
			 * transaction.setExternalOrderID(externalOrderID);
			 * transaction.setExternalOrderNumber(externalOrderNumber);
			 * transaction.setExternalOrderType(externalOrderType);
			 * transaction.setRequireServiceContractFlag
			 * (requireServiceContractFlag); CurrencyTypeIfc currencyType =
			 * getCurrencyType(transactionCurrencyType);
			 * transaction.setCurrencyType(currencyType);
			 * transaction.setTransactionCountryCode(transactionCountryCode);
			 */
			// Ashish : End commenting above lines

			// Read sale return line items
			SaleReturnLineItemIfc[] lineItems = selectSaleReturnLineItems(
					dataConnection, transaction, localeRequestor,
					retrieveStoreCoupons);

			if (transaction instanceof OrderTransaction
					&& transaction.getTransactionStatus() != TransactionConstantsIfc.STATUS_SUSPENDED)// logic
																										// added
																										// to
			// eliminate kitItem.
			{
				ArrayList<SaleReturnLineItemIfc> arrayOfLineItem = new ArrayList<SaleReturnLineItemIfc>();
				for (int i = 0; i < lineItems.length; i++) {
					if (!(lineItems[i].getPLUItem().isKitHeader())) {
						arrayOfLineItem.add(lineItems[i]);
					}

				}
				SaleReturnLineItemIfc[] pdoLineItems = new SaleReturnLineItemIfc[arrayOfLineItem
						.size()];
				arrayOfLineItem.toArray(pdoLineItems);
				lineItems = pdoLineItems;
			}
			SaleReturnLineItemIfc[] deletedLineItems = selectDeletedSaleReturnLineItems(
					dataConnection, transaction, localeRequestor);

			// Set line items without updating transaction totals. The totals
			// will be updated
			// when transaction discounts are added. Transaction totals must not
			// be updated here
			// since it will erase the item level transaction discount
			// information which is needed
			// to aggregate into transaction discount rules.
			transaction.getItemContainerProxy().setLineItems(lineItems);
			if (deletedLineItems != null) {
				if (deletedLineItems.length > 0) {
					for (int i = 0; i < deletedLineItems.length; i++) {
						transaction.addDeletedLineItems(deletedLineItems[i]);
					}
				}
			}

			// Read transaction discounts
			TransactionDiscountStrategyIfc[] transactionDiscounts;
			transactionDiscounts = selectDiscountLineItems(dataConnection,
					transaction, localeRequestor);

			// A return line item can also contain order item information, so
			// look for order
			// item info for all SaleReturnTransactions.
			selectOrderLineItemsByRef(dataConnection, transaction);
			selectOrderLineItemDiscountStatusByRef(dataConnection, transaction);
			selectOrderLineItemTaxStatusByRef(dataConnection, transaction);

			// Aggregate Transaction Discounts must happen after
			// selectSaleReturnLineItems since in some cases
			// the transaction discounts are aggregated from item level
			// discounts. It also must be called after
			// selectOrderLineItemsByRef since information such as if the line
			// item is a pickup or cancel item
			// is initialized after this call.
			transactionDiscounts = aggregateTransactionDiscounts(transaction,
					transactionDiscounts);
			transaction.addTransactionDiscounts(transactionDiscounts);

			// if the transaction is an order transaction
			if (transaction.getTransactionType() == TransactionIfc.TYPE_ORDER_CANCEL
					|| transaction.getTransactionType() == TransactionIfc.TYPE_ORDER_PARTIAL
					|| transaction.getTransactionType() == TransactionIfc.TYPE_ORDER_COMPLETE
					|| transaction.getTransactionType() == TransactionIfc.TYPE_ORDER_INITIATE) {
				OrderTransactionIfc orderTransaction = (OrderTransactionIfc) transaction;
				orderTransaction.setOrderID(orderID);
				orderTransaction.getOrderStatus().setTrainingModeFlag(
						transaction.isTrainingMode());
				selectOrderStatusForTransaction(dataConnection,
						orderTransaction);
				selectDeliveryDetails(dataConnection, transaction);
				selectRecipientDetail(dataConnection, transaction);
			}

			// Read tender line items
			TenderLineItemIfc[] tenderLineItems = selectTenderLineItems(
					dataConnection, transaction);
			transaction.setTenderLineItems(tenderLineItems);

			// Read cash change rounding adjustment
			selectRoundingTenderChangeLineItem(dataConnection, transaction);

			// Read tenders for return items in the trans
			if (transaction.hasReturnItems()) {
				ReturnTenderDataElementIfc[] returnTenders = readReturnTenders(
						dataConnection, transaction);
				transaction.appendReturnTenderElements(returnTenders);
			}
		} catch (DataException de) {
			logger.error("" + de + "");
			throw de;
		} catch (SQLException se) {
			dataConnection.logSQLException(se, "retail transaction table");
			throw new DataException(DataException.SQL_ERROR,
					"retail transaction table", se);
		} catch (Exception e) {
			logger.error("" + Util.throwableToString(e) + "");
			throw new DataException(DataException.UNKNOWN,
					"retail transaction table", e);
		}

		if (logger.isDebugEnabled())
			logger.debug("JdbcReadTransaction.selectSaleReturnTransaction()");
	}

	private String getCustgstin(SaleReturnTransactionIfc transaction) {
		// TODO Auto-generated method stub
		return null;
	}

	protected SaleReturnLineItemIfc[] selectSaleReturnLineItems(
			JdbcDataConnection dataConnection,
			SaleReturnTransactionIfc transaction,
			LocaleRequestor localeRequestor) throws DataException {
		if (logger.isDebugEnabled())
			logger.debug("JdbcReadTransaction.selectSaleReturnLineItems()");

		SQLSelectStatement sql = new SQLSelectStatement();
		/*
		 * Add Table(s)
		 */
		sql.addTable(TABLE_SALE_RETURN_LINE_ITEM, ALIAS_SALE_RETURN_LINE_ITEM);
		sql.addTable(TABLE_RETAIL_TRANSACTION_LINE_ITEM,
				ALIAS_RETAIL_TRANSACTION_LINE_ITEM);

		/*
		 * Add Column(s)
		 */
		sql.addColumn(ALIAS_SALE_RETURN_LINE_ITEM + "."
				+ FIELD_GIFT_REGISTRY_ID);
		sql.addColumn(ALIAS_SALE_RETURN_LINE_ITEM + "." + FIELD_POS_ITEM_ID);
		sql.addColumn(ALIAS_SALE_RETURN_LINE_ITEM + "." + FIELD_ITEM_ID);
		sql.addColumn(ALIAS_SALE_RETURN_LINE_ITEM + "."
				+ FIELD_SALE_RETURN_LINE_ITEM_QUANTITY);
		sql.addColumn(ALIAS_SALE_RETURN_LINE_ITEM + "."
				+ FIELD_SALE_RETURN_LINE_ITEM_EXTENDED_AMOUNT);
		sql.addColumn(ALIAS_SALE_RETURN_LINE_ITEM + "."
				+ FIELD_SALE_RETURN_LINE_ITEM_VAT_AMOUNT);
		sql.addColumn(ALIAS_SALE_RETURN_LINE_ITEM + "."
				+ FIELD_SALE_RETURN_LINE_ITEM_TAX_INC_AMOUNT);
		sql.addColumn(ALIAS_SALE_RETURN_LINE_ITEM + "."
				+ FIELD_RETAIL_TRANSACTION_LINE_ITEM_SEQUENCE_NUMBER);
		sql.addColumn(ALIAS_SALE_RETURN_LINE_ITEM + "." + FIELD_SERIAL_NUMBER);
		sql.addColumn(ALIAS_SALE_RETURN_LINE_ITEM + "."
				+ FIELD_SALE_RETURN_LINE_ITEM_RETURN_QUANTITY);
		sql.addColumn(ALIAS_SALE_RETURN_LINE_ITEM + "."
				+ FIELD_POS_ORIGINAL_TRANSACTION_ID);
		sql.addColumn(ALIAS_SALE_RETURN_LINE_ITEM + "."
				+ FIELD_ORIGINAL_BUSINESS_DAY_DATE);
		sql.addColumn(ALIAS_SALE_RETURN_LINE_ITEM + "."
				+ FIELD_ORIGINAL_RETAIL_TRANSACTION_LINE_ITEM_SEQUENCE_NUMBER);
		sql.addColumn(ALIAS_SALE_RETURN_LINE_ITEM + "."
				+ FIELD_ORIGINAL_RETAIL_STORE_ID);
		sql.addColumn(ALIAS_SALE_RETURN_LINE_ITEM + "."
				+ FIELD_MERCHANDISE_RETURN_FLAG);
		sql.addColumn(ALIAS_SALE_RETURN_LINE_ITEM + "."
				+ FIELD_MERCHANDISE_RETURN_REASON_CODE);
		sql.addColumn(ALIAS_SALE_RETURN_LINE_ITEM + "."
				+ FIELD_MERCHANDISE_RETURN_ITEM_CONDITION_CODE);
		sql.addColumn(ALIAS_SALE_RETURN_LINE_ITEM + "."
				+ FIELD_SALE_RETURN_LINE_ITEM_RESTOCKING_FEE_AMOUNT);
		sql.addColumn(ALIAS_SALE_RETURN_LINE_ITEM + "."
				+ FIELD_SALE_RETURN_LINE_ITEM_DEPOSIT_AMOUNT);
		sql.addColumn(ALIAS_SALE_RETURN_LINE_ITEM + "."
				+ FIELD_SALE_RETURN_LINE_ITEM_BALANCE_DUE);
		sql.addColumn(ALIAS_SALE_RETURN_LINE_ITEM + "."
				+ FIELD_SALE_RETURN_LINE_ITEM_PICKUP_CANCEL_PRICE);
		sql.addColumn(ALIAS_SALE_RETURN_LINE_ITEM + "."
				+ FIELD_SALE_RETURN_LINE_ITEM_PICKUP_INSTORE_PRICE);
		sql.addColumn(ALIAS_SALE_RETURN_LINE_ITEM + "."
				+ FIELD_ITEM_KIT_SET_CODE);
		sql.addColumn(ALIAS_SALE_RETURN_LINE_ITEM + "."
				+ FIELD_ITEM_COLLECTION_ID);
		sql.addColumn(ALIAS_SALE_RETURN_LINE_ITEM + "."
				+ FIELD_ITEM_KIT_HEADER_REFERENCE_ID);
		sql.addColumn(ALIAS_SALE_RETURN_LINE_ITEM + "." + FIELD_SEND_FLAG);
		sql.addColumn(ALIAS_SALE_RETURN_LINE_ITEM + "."
				+ FIELD_SHIPPING_CHARGE_FLAG);
		sql.addColumn(ALIAS_SALE_RETURN_LINE_ITEM + "."
				+ FIELD_SEND_LABEL_COUNT);
		sql.addColumn(ALIAS_SALE_RETURN_LINE_ITEM + "."
				+ FIELD_GIFT_RECEIPT_FLAG);
		sql.addColumn(ALIAS_SALE_RETURN_LINE_ITEM + "." + FIELD_ORDER_ID);
		sql.addColumn(ALIAS_SALE_RETURN_LINE_ITEM + "."
				+ FIELD_ORDER_LINE_ITEM_SEQUENCE_NUMBER);
		sql.addColumn(ALIAS_SALE_RETURN_LINE_ITEM + "."
				+ FIELD_ITEM_ID_ENTRY_METHOD_CODE);
		sql.addColumn(ALIAS_SALE_RETURN_LINE_ITEM + "." + FIELD_SIZE_CODE);
		sql.addColumn(ALIAS_RETAIL_TRANSACTION_LINE_ITEM + "."
				+ FIELD_RETAIL_TRANSACTION_LINE_ITEM_VOID_FLAG);
		sql.addColumn(ALIAS_SALE_RETURN_LINE_ITEM + "."
				+ FIELD_ITEM_PRICEADJ_LINE_ITEM_FLAG);
		sql.addColumn(ALIAS_SALE_RETURN_LINE_ITEM + "."
				+ FIELD_ITEM_PRICEADJ_REFERENCE_ID);
		sql.addColumn(ALIAS_SALE_RETURN_LINE_ITEM + "."
				+ FIELD_RETURN_RELATED_ITEM_FLAG);
		sql.addColumn(ALIAS_SALE_RETURN_LINE_ITEM + "."
				+ FIELD_RELATED_ITEM_TRANSACTION_LINE_ITEM_SEQ_NUMBER);
		sql.addColumn(ALIAS_SALE_RETURN_LINE_ITEM + "."
				+ FIELD_REMOVE_RELATED_ITEM_FLAG);
		sql.addColumn(ALIAS_SALE_RETURN_LINE_ITEM + "."
				+ FIELD_RETAIL_TRANSACTION_LINE_ITEM_SALES_ASSC_FLAG);
		sql.addColumn(ALIAS_SALE_RETURN_LINE_ITEM + "."
				+ FIELD_RETAIL_TRANSACTION_LINE_ITEM_PREMANENT_RETAIL_PRICE);
		sql.addColumn(ALIAS_SALE_RETURN_LINE_ITEM + "."
				+ FIELD_RETAIL_TRANSACTION_LINE_ITEM_RECEIPT_DESCRIPTION);
		sql.addColumn(ALIAS_SALE_RETURN_LINE_ITEM + "."
				+ FIELD_RETAIL_TRANSACTION_LINE_ITEM_RECEIPT_DESCRIPTION_LOCALE);
		sql.addColumn(ALIAS_SALE_RETURN_LINE_ITEM + "."
				+ FIELD_RETAIL_TRANSACTION_LINE_ITEM_RESTOCKING_FEE_FLAG);
		sql.addColumn(ALIAS_SALE_RETURN_LINE_ITEM + "."
				+ FIELD_SERIALIZED_ITEM_VALIDATION_FLAG);
		sql.addColumn(ALIAS_SALE_RETURN_LINE_ITEM + "."
				+ FIELD_EXTERNAL_VALIDATION_SERIALIZED_ITEM_FLAG);
		sql.addColumn(ALIAS_SALE_RETURN_LINE_ITEM + "."
				+ FIELD_SERIALIZED_ITEM_EXTERNAL_SYSTEM_CREATE_UIN);
		sql.addColumn(ALIAS_SALE_RETURN_LINE_ITEM
				+ "."
				+ FIELD_RETAIL_TRANSACTION_LINE_ITEM_MERCHANDISE_HIERARCHY_LEVEL_CODE);
		sql.addColumn(ALIAS_SALE_RETURN_LINE_ITEM + "."
				+ FIELD_RETAIL_TRANSACTION_LINE_ITEM_SIZE_REQUIRED_FLAG);
		sql.addColumn(ALIAS_SALE_RETURN_LINE_ITEM + "."
				+ FIELD_RETAIL_TRANSACTION_LINE_ITEM_SALE_UNIT_OF_MEASURE_CODE);
		sql.addColumn(ALIAS_SALE_RETURN_LINE_ITEM + "."
				+ FIELD_RETAIL_TRANSACTION_LINE_ITEM_POS_DEPARTMENT_ID);
		sql.addColumn(ALIAS_SALE_RETURN_LINE_ITEM + "."
				+ FIELD_RETAIL_TRANSACTION_LINE_ITEM_SALE_ITEM_TYPE_ID);
		sql.addColumn(ALIAS_SALE_RETURN_LINE_ITEM + "."
				+ FIELD_RETAIL_TRANSACTION_LINE_ITEM_RETURN_PROHIBITED_FLAG);
		sql.addColumn(ALIAS_SALE_RETURN_LINE_ITEM
				+ "."
				+ FIELD_RETAIL_TRANSACTION_LINE_ITEM_EMPLOYEE_DISCOUNT_ALOWED_FLAG);
		sql.addColumn(ALIAS_SALE_RETURN_LINE_ITEM + "." + FIELD_TAX_GROUP_ID);
		sql.addColumn(ALIAS_SALE_RETURN_LINE_ITEM + "."
				+ FIELD_RETAIL_TRANSACTION_LINE_ITEM_TAXABLE_FLAG);
		sql.addColumn(ALIAS_SALE_RETURN_LINE_ITEM + "."
				+ FIELD_ITEM_DISCOUNT_FLAG);
		sql.addColumn(ALIAS_SALE_RETURN_LINE_ITEM + "."
				+ FIELD_ITEM_DAMAGE_DISCOUNT_FLAG);
		sql.addColumn(ALIAS_SALE_RETURN_LINE_ITEM
				+ "."
				+ FIELD_RETAIL_TRANSACTION_LINE_ITEM_MERCHANDISE_HIERARCHY_GROUP_ID);
		sql.addColumn(ALIAS_SALE_RETURN_LINE_ITEM + "."
				+ FIELD_RETAIL_TRANSACTION_LINE_ITEM_MANUFACTURER_ITEM_UPC);
		sql.addColumn(ALIAS_SALE_RETURN_LINE_ITEM + "."
				+ NON_RETRIEVED_ORIGINAL_RECEIPT_ID);
		sql.addColumn(ALIAS_SALE_RETURN_LINE_ITEM + "."
				+ FIELD_SALE_AGE_RESTRICTION_ID);
		sql.addColumn(ALIAS_SALE_RETURN_LINE_ITEM + "."
				+ FIELD_CLEARANCE_INDICATOR);
		sql.addColumn(ALIAS_SALE_RETURN_LINE_ITEM + "."
				+ FIELD_ITEM_PRICE_ENTRY_REQUIRED_FLAG);

		/*
		 * Add Qualifier(s)
		 */
		// For the specific transaction only
		sql.addQualifier(ALIAS_SALE_RETURN_LINE_ITEM + "."
				+ FIELD_RETAIL_STORE_ID + " = " + getStoreID(transaction));
		sql.addQualifier(ALIAS_SALE_RETURN_LINE_ITEM + "."
				+ FIELD_WORKSTATION_ID + " = " + getWorkstationID(transaction));
		sql.addQualifier(ALIAS_SALE_RETURN_LINE_ITEM + "."
				+ FIELD_TRANSACTION_SEQUENCE_NUMBER + " = "
				+ getTransactionSequenceNumber(transaction));
		sql.addQualifier(ALIAS_SALE_RETURN_LINE_ITEM + "."
				+ FIELD_BUSINESS_DAY_DATE + " = "
				+ getBusinessDayString(transaction));
		sql.addQualifier(ALIAS_RETAIL_TRANSACTION_LINE_ITEM + "."
				+ FIELD_RETAIL_STORE_ID + "=" + ALIAS_SALE_RETURN_LINE_ITEM
				+ "." + FIELD_RETAIL_STORE_ID);
		sql.addQualifier(ALIAS_RETAIL_TRANSACTION_LINE_ITEM + "."
				+ FIELD_WORKSTATION_ID + " = " + ALIAS_SALE_RETURN_LINE_ITEM
				+ "." + FIELD_WORKSTATION_ID);
		sql.addQualifier(ALIAS_RETAIL_TRANSACTION_LINE_ITEM + "."
				+ FIELD_TRANSACTION_SEQUENCE_NUMBER + " = "
				+ ALIAS_SALE_RETURN_LINE_ITEM + "."
				+ FIELD_TRANSACTION_SEQUENCE_NUMBER);
		sql.addQualifier(ALIAS_RETAIL_TRANSACTION_LINE_ITEM + "."
				+ FIELD_RETAIL_TRANSACTION_LINE_ITEM_SEQUENCE_NUMBER + " = "
				+ ALIAS_SALE_RETURN_LINE_ITEM + "."
				+ FIELD_RETAIL_TRANSACTION_LINE_ITEM_SEQUENCE_NUMBER);
		sql.addQualifier(ALIAS_RETAIL_TRANSACTION_LINE_ITEM + "."
				+ FIELD_BUSINESS_DAY_DATE + " = " + ALIAS_SALE_RETURN_LINE_ITEM
				+ "." + FIELD_BUSINESS_DAY_DATE);

		// order by line item sequence number
		sql.addOrdering(ALIAS_SALE_RETURN_LINE_ITEM + "."
				+ FIELD_RETAIL_TRANSACTION_LINE_ITEM_SEQUENCE_NUMBER + " ASC");

		Vector<SaleReturnLineItemIfc> saleReturnLineItems = new Vector<SaleReturnLineItemIfc>(
				2);

		try {
			dataConnection.execute(sql.getSQLString());
			ResultSet rs = (ResultSet) dataConnection.getResult();
			TransactionTaxIfc transactionTax = transaction.getTransactionTax();

			HashMap<Integer, String> reasonCodeMap = new HashMap<Integer, String>();
			HashMap<Integer, String> itemConditionCodeMap = new HashMap<Integer, String>();
			while (rs.next()) {
				int index = 0;
				String giftRegistryID = getSafeString(rs, ++index);
				String posItemID = getSafeString(rs, ++index);
				String itemID = getSafeString(rs, ++index);
				// Ashish : Start added below line
				// BigDecimal quantity = getBigDecimal(rs, ++index);
				BigDecimal quantity = getBigDecimal(rs, ++index, 3);
				// Ashish : End added above line
				CurrencyIfc amount = getCurrencyFromDecimal(rs, ++index);
				/*
				 * CurrencyIfc itemTaxAmount = getLongerCurrencyFromDecimal(rs,
				 */++index;
				/*
				 * CurrencyIfc itemIncTaxAmount =
				 * getLongerCurrencyFromDecimal(rs,
				 */++index;
				int sequenceNumber = rs.getInt(++index);
				String serialNumber = getSafeString(rs, ++index);
				BigDecimal quantityReturned = getBigDecimal(rs, ++index);
				String originalTransactionID = getSafeString(rs, ++index);
				EYSDate originalTransactionBusinessDay = getEYSDateFromString(
						rs, ++index);
				int originalTransactionLineNumber = rs.getInt(++index);
				String originalStoreID = getSafeString(rs, ++index);
				boolean returnFlag = getBooleanFromString(rs, ++index);
				String returnReasonCode = getSafeString(rs, ++index);
				String returnItemConditionCode = getSafeString(rs, ++index);
				if (StringUtils.isBlank(returnItemConditionCode)) {
					returnItemConditionCode = CodeConstantsIfc.CODE_UNDEFINED;
				}
				CurrencyIfc restockingFee = getCurrencyFromDecimal(rs, ++index);
				CurrencyIfc depositAmount = getCurrencyFromDecimal(rs, ++index);
				CurrencyIfc balanceDue = getCurrencyFromDecimal(rs, ++index);
				boolean pickupCancelledPrice = getBooleanFromString(rs, ++index);
				boolean pickupInStorePrice = getBooleanFromString(rs, ++index);
				int kitCode = rs.getInt(++index);
				String itemKitID = getSafeString(rs, ++index);
				int kitReference = rs.getInt(++index);
				String sendFlag = getSafeString(rs, ++index);
				String shippingChargeFlag = getSafeString(rs, ++index);
				int sendLabelCount = rs.getInt(++index);
				String giftReceiptStr = getSafeString(rs, ++index);
				String orderID = rs.getString(++index);
				int orderLineReference = rs.getInt(++index);
				/* String entryMethod = */getSafeString(rs, ++index);
				String sizeCode = getSafeString(rs, ++index);
				String itemVoidFlag = getSafeString(rs, ++index);
				boolean isPriceAdjLineItem = rs.getBoolean(++index);
				int priceAdjReferenceID = rs.getInt(++index);
				boolean returnRelatedItemFlag = rs.getBoolean(++index);
				int relatedSeqNumber = rs.getInt(++index);
				boolean deleteRelatedItemFlag = rs.getBoolean(++index);
				boolean saleAsscModifiedFlag = getBooleanFromString(rs, ++index);
				CurrencyIfc beforeOverride = getCurrencyFromDecimal(rs, ++index);
				String receiptDescription = getSafeString(rs, ++index);
				Locale receiptDescriptionLocale = LocaleUtilities
						.getLocaleFromString(getSafeString(rs, ++index));
				boolean restockingFeeFlag = rs.getBoolean(++index);
				boolean serializedItemFlag = rs.getBoolean(++index);
				boolean externalValidationSerializedItemFlag = rs
						.getBoolean(++index);
				boolean isPOSAllowedtoCreateUIN = rs.getBoolean(++index);
				String productGroupID = getSafeString(rs, ++index);
				boolean sizeRequiredFlag = rs.getBoolean(++index);
				String unitOfMeasureCode = getSafeString(rs, ++index);
				String posDepartmentID = getSafeString(rs, ++index);
				int itemTypeID = rs.getInt(++index);
				boolean returnEligible = !(rs.getBoolean(++index));
				boolean employeeDiscountEligible = (rs.getBoolean(++index));
				int taxGroupId = rs.getInt(++index);
				boolean taxable = (rs.getBoolean(++index));
				boolean discountable = (rs.getBoolean(++index));
				boolean damageDiscountable = (rs.getBoolean(++index));
				String merchandiseHierarchyGroupID = getSafeString(rs, ++index);
				String manufacturerItemUPC = getSafeString(rs, ++index);
				String nonRetrievedOriginalReceiptId = getSafeString(rs,
						++index);
				int restrictiveAge = rs.getInt(++index);
				boolean clearanceIndicator = (rs.getBoolean(++index));
				boolean priceEntryRequired = (rs.getBoolean(++index));
				// Ashish : Start added below line
				// CurrencyIfc lineItemTaxAmount =
				// DomainGateway.getBaseCurrencyInstance(BigDecimal.ZERO);
				// CurrencyIfc lineItemIncTaxAmount =
				// DomainGateway.getBaseCurrencyInstance(BigDecimal.ZERO);
				CurrencyIfc soldMRP = getCurrencyFromDecimal(rs, ++index);
				CurrencyIfc lineItemTaxAmount = DomainGateway
						.getBaseCurrencyInstance(BigDecimalConstants.ZERO_AMOUNT);
				CurrencyIfc lineItemIncTaxAmount = DomainGateway
						.getBaseCurrencyInstance(BigDecimalConstants.ZERO_AMOUNT);
				// Ashish : End added above line
				// create and initialize item price object
				ItemPriceIfc price = DomainGateway.getFactory()
						.getItemPriceInstance();
				ItemTaxIfc itemTax = DomainGateway.getFactory()
						.getItemTaxInstance();
				price.setExtendedSellingPrice(amount);
				price.setDiscountEligible(discountable);
				price.setExtendedRestockingFee(restockingFee);
				// Ashish : Start Added below line(as below line is not present
				// in base14)
				((MAXItemPriceIfc) price).setSoldMRP(soldMRP);
				// Ashish : End added above line
				if (quantity.signum() != 0) {
					amount = amount.divide(new BigDecimal(quantity.toString()));
					if (restockingFee != null) {
						restockingFee = restockingFee.divide(new BigDecimal(
								quantity.toString()));
					}
				}

				price.setSellingPrice(amount);
				price.setPermanentSellingPrice(beforeOverride);
				price.setRestockingFee(restockingFee);

				// Obtain the previously calculated and saved line item tax
				itemTax = price.getItemTax();
				itemTax.setDefaultRate(transactionTax.getDefaultRate());
				itemTax.setDefaultTaxRules(transactionTax.getDefaultTaxRules());
				itemTax.setItemTaxAmount(lineItemTaxAmount);
				itemTax.setItemInclusiveTaxAmount(lineItemIncTaxAmount);
				// The tax mode is unknown at this point; It can be set from
				// SaleReturnTaxLineItem Table or the TaxModifier Table;
				// however,
				// if neither is available it must be explicitly set
				// depending on item taxability.
				itemTax.setTaxMode(TAX_MODE_NOT_SET);
				price.setItemTax(itemTax);

				// price.setItemTaxAmount(itemTaxAmount);
				price.setItemTaxAmount(lineItemTaxAmount);
				price.setItemTaxAmount(lineItemIncTaxAmount);

				// price.setTaxGroupId(taxGroupID);
				price.setItemQuantity(quantity);

				SaleReturnLineItemIfc lineItem;
				// create and initialize appropriate line item object
				if (transaction.getTransactionType() == TransactionIfc.TYPE_ORDER_CANCEL
						|| transaction.getTransactionType() == TransactionIfc.TYPE_ORDER_COMPLETE
						|| transaction.getTransactionType() == TransactionIfc.TYPE_ORDER_PARTIAL
						|| transaction.getTransactionType() == TransactionIfc.TYPE_ORDER_INITIATE) {
					lineItem = DomainGateway.getFactory()
							.getOrderLineItemInstance();
				} else {
					switch (kitCode) {
					case ItemKitConstantsIfc.ITEM_KIT_CODE_HEADER:
						lineItem = DomainGateway.getFactory()
								.getKitHeaderLineItemInstance();
						break;
					case ItemKitConstantsIfc.ITEM_KIT_CODE_COMPONENT:
						lineItem = DomainGateway.getFactory()
								.getKitComponentLineItemInstance();
						((KitComponentLineItemIfc) lineItem)
								.setItemKitID(itemKitID);
						break;
					default:
						lineItem = DomainGateway.getFactory()
								.getSaleReturnLineItemInstance();
						break;
					}
				}

				lineItem.setPLUItemID(itemID);
				lineItem.setItemPrice(price);
				lineItem.setItemTaxAmount(lineItemTaxAmount);
				lineItem.setItemInclusiveTaxAmount(lineItemIncTaxAmount);
				lineItem.modifyItemQuantity(quantity);
				lineItem.setLineNumber(sequenceNumber);
				lineItem.setItemSizeCode(sizeCode);

				// set the order id & line item reference number
				lineItem.setOrderID(orderID);
				lineItem.setOrderLineReference(orderLineReference);

				lineItem.setReceiptDescription(receiptDescription);
				lineItem.setReceiptDescriptionLocale(receiptDescriptionLocale);
				lineItem.getItemPrice().setEmployeeDiscountEligible(
						employeeDiscountEligible);

				lineItem.setDepositAmount(depositAmount);

				if (lineItem instanceof OrderLineItemIfc) {
					((OrderLineItemIfc) lineItem).setItemBalanceDue(balanceDue);
					((OrderLineItemIfc) lineItem)
							.setPriceCancelledDuringPickup(pickupCancelledPrice);
					((OrderLineItemIfc) lineItem)
							.setInStorePriceDuringPickup(pickupInStorePrice);
				}

				// set the KitHeaderReference
				lineItem.setKitHeaderReference(kitReference);

				if (serialNumber != null && serialNumber.length() > 0) {
					lineItem.setItemSerial(serialNumber);
				}
				lineItem.setQuantityReturned(quantityReturned);
				/*
				 * Should probably be a relationship fetched from the gift
				 * registry table
				 */
				if (giftRegistryID.length() > 0) {
					RegistryIDIfc registry = instantiateGiftRegistry();
					registry.setID(giftRegistryID);
					lineItem.modifyItemRegistry(registry, true);
				}

				// Return Item Original Transaction information is available
				if (returnFlag) {
					ReturnItemIfc ri = DomainGateway.getFactory()
							.getReturnItemInstance();

					if (originalTransactionID != null
							&& originalTransactionID.length() > 0) {
						// Create the transaction id.
						TransactionIDIfc id = DomainGateway.getFactory()
								.getTransactionIDInstance();
						id.setTransactionID(originalTransactionID);
						ri.setOriginalTransactionID(id);
					}
					ri.setNonRetrievedOriginalReceiptId(nonRetrievedOriginalReceiptId);

					if (originalTransactionBusinessDay != null) {
						ri.setOriginalTransactionBusinessDate(originalTransactionBusinessDay);
					}
					ri.setOriginalLineNumber(originalTransactionLineNumber);

					// DB2 does not support nested result sets, so the localized
					// reason codes needs to be retrieved after closing the
					// current
					// result set. Add the reason code to a map for later
					// retrieval.
					reasonCodeMap.put(sequenceNumber, returnReasonCode);
					itemConditionCodeMap.put(sequenceNumber,
							returnItemConditionCode);

					if (originalStoreID.equals(transaction.getWorkstation()
							.getStoreID())) {
						ri.setStore(transaction.getWorkstation().getStore());
					} else {
						StoreIfc store = DomainGateway.getFactory()
								.getStoreInstance();
						store.setStoreID(originalStoreID);
						ri.setStore(store);
					}
					lineItem.setReturnItem(ri);
				}
				/*
				 * Sales Associate defaults to the cashier. If it's different,
				 * that will be accounted for by the selectCommissionModifier
				 * below.
				 */
				if (transaction.getSalesAssociate() != null) {
					lineItem.setSalesAssociate(transaction.getSalesAssociate());
				} else {
					lineItem.setSalesAssociate(transaction.getCashier());
				}
				/*
				 * Send Flag
				 */
				if (transaction.getTransactionType() != TransactionIfc.TYPE_RETURN) {
					if (sendFlag.equals("0")) {
						lineItem.setItemSendFlag(false);
					} else if (sendFlag.equals("1")) {
						lineItem.setItemSendFlag(true);
					}
					lineItem.setSendLabelCount(sendLabelCount);
					/*
					 * Shipping Charge Flag
					 */
					if (shippingChargeFlag.equals("0")) {
						lineItem.setShippingCharge(false);
					} else if (shippingChargeFlag.equals("1")) {
						lineItem.setShippingCharge(true);
					}
				}

				/**
				 * Gift Receipt Flag
				 */
				if (giftReceiptStr.equals("1")) {
					lineItem.setGiftReceiptItem(true);
				}

				/**
				 * Price Adjustment Flags
				 */
				lineItem.setPriceAdjustmentReference(priceAdjReferenceID);
				lineItem.setIsPriceAdjustmentLineItem(isPriceAdjLineItem);

				if (!itemVoidFlag.equals(DBConstantsIfc.TRUE)) {
					saleReturnLineItems.addElement(lineItem);
				}

				lineItem.setRelatedItemReturnable(returnRelatedItemFlag);
				lineItem.setRelatedItemSequenceNumber(relatedSeqNumber);
				lineItem.setRelatedItemDeleteable(deleteRelatedItemFlag);
				lineItem.setSalesAssociateModifiedFlag(saleAsscModifiedFlag);

				// Generate a pluItem from the data in the Sale Return Line Item
				// table
				PLUItemIfc pluItem = instantiatePLUItem(productGroupID,
						kitCode, transaction.isTrainingMode());
				pluItem.setItemID(itemID);
				pluItem.setPosItemID(posItemID);
				pluItem.setItemSizeRequired(sizeRequiredFlag);
				pluItem.setDepartmentID(posDepartmentID);
				pluItem.setTaxable(taxable);
				pluItem.setTaxGroupID(taxGroupId);
				pluItem.setManufacturerItemUPC(manufacturerItemUPC);
				ItemClassificationIfc itemClassification = DomainGateway
						.getFactory().getItemClassificationInstance();
				itemClassification.setRestockingFeeFlag(restockingFeeFlag);
				itemClassification.setSerializedItem(serializedItemFlag);
				if (externalValidationSerializedItemFlag) {
					itemClassification
							.setSerialEntryTime(STORE_RECEIVING_SERIALIZED_CAPTURE_TIME);
				} else {
					itemClassification
							.setSerialEntryTime(SALE_SERIALIZED_CAPTURE_TIME);
				}
				itemClassification
						.setExternalSystemCreateUIN(isPOSAllowedtoCreateUIN);
				ProductGroupIfc pg = DomainGateway.getFactory()
						.getProductGroupInstance();
				pg.setGroupID(productGroupID);
				itemClassification.setGroup(pg);
				itemClassification.setItemType(itemTypeID);
				itemClassification.setReturnEligible(returnEligible);
				itemClassification
						.setEmployeeDiscountAllowedFlag(employeeDiscountEligible);
				itemClassification.setDiscountEligible(discountable);
				itemClassification
						.setDamageDiscountEligible(damageDiscountable);
				itemClassification
						.setMerchandiseHierarchyGroup(merchandiseHierarchyGroupID);
				itemClassification.setPriceEntryRequired(priceEntryRequired);
				pluItem.setItemClassification(itemClassification);
				lineItem.setOnClearance(clearanceIndicator);
				pluItem.setSellingPrice(lineItem.getItemPrice()
						.getPermanentSellingPrice());
				UnitOfMeasureIfc pluUOM = DomainGateway.getFactory()
						.getUnitOfMeasureInstance();
				pluUOM.setUnitID(unitOfMeasureCode);
				pluItem.setUnitOfMeasure(pluUOM);
				pluItem.setRestrictiveAge(restrictiveAge);
				selectOptionalI18NPLUData(dataConnection, pluItem,
						localeRequestor, lineItem);
				lineItem.setPLUItem(pluItem);
			}
			rs.close();

			// Set the localized reason code for return line items
			for (int lineItemCounter = 0; lineItemCounter < saleReturnLineItems
					.size(); lineItemCounter++) {
				if (saleReturnLineItems.get(lineItemCounter).isReturnLineItem()) {
					int sequenceNumber = saleReturnLineItems.get(
							lineItemCounter).getLineNumber();

					// Get the reason code from the map
					String reasonCode = reasonCodeMap.get(sequenceNumber);

					// Retrieve localized reason code
					saleReturnLineItems
							.get(lineItemCounter)
							.getReturnItem()
							.setReason(
									getInitializedLocalizedReasonCode(
											dataConnection,
											transaction
													.getTransactionIdentifier()
													.getStoreID(),
											reasonCode,
											CodeConstantsIfc.CODE_LIST_RETURN_REASON_CODES,
											localeRequestor));

					// Get the item condition code from the map
					String itemConditionCode = itemConditionCodeMap
							.get(sequenceNumber);

					// retreve localized item condition code
					saleReturnLineItems
							.get(lineItemCounter)
							.getReturnItem()
							.setItemCondition(
									getInitializedLocalizedReasonCode(
											dataConnection,
											transaction
													.getTransactionIdentifier()
													.getStoreID(),
											itemConditionCode,
											CodeConstantsIfc.CODE_LIST_RETURN_ITEM_CONDITION_CODES,
											localeRequestor));

				}
			}

			// Do separate query to find the possible line item tax amount from
			// tr_ltm_sls_rtn_tx table.
			for (int i = 0; i < saleReturnLineItems.size(); i++) {
				SaleReturnLineItemIfc srli = saleReturnLineItems.elementAt(i);
				int lineItemSequenceNumber = srli.getLineNumber();
				TaxInformationIfc[] taxInfoArray = selectSaleReturnLineItemTaxInformation(
						dataConnection, transaction, lineItemSequenceNumber);
				TaxInformationContainerIfc container = DomainGateway
						.getFactory().getTaxInformationContainerInstance();
				for (int j = 0; j < taxInfoArray.length; j++) {
					container.addTaxInformation(taxInfoArray[j]);
					srli.getItemPrice().getItemTax()
							.setTaxMode(taxInfoArray[j].getTaxMode());
				}
				srli.getItemPrice().getItemTax()
						.setTaxInformationContainer(container);
				CurrencyIfc[] taxAmount = selectSaleReturnLineItemTaxAmount(
						dataConnection, transaction, lineItemSequenceNumber);
				srli.setItemTaxAmount(taxAmount[0]); // the first element is add
														// on item tax
				srli.setItemInclusiveTaxAmount(taxAmount[1]); // the second
																// element is
																// inclusive
																// item
																// tax
				srli.setFromTransaction(true);
			}

			/*
			 * Grab auxilliary elements
			 */
			List<ItemDiscountStrategyIfc> itemDiscounts = null;
			for (SaleReturnLineItemIfc lineItem : saleReturnLineItems) {
				/*
				 * Grab the PLUItem
				 */
				PLUItemIfc pluItem = lineItem.getPLUItem();
				;

				if (lineItem.isKitComponent() && pluItem.isKitComponent()) {
					pluItem.setItemID(lineItem.getPLUItemID());
					((KitComponentIfc) pluItem)
							.setItemKitID(((KitComponentLineItemIfc) lineItem)
									.getItemKitID());
				}

				// if gift card, find gift card number
				if (pluItem instanceof GiftCardPLUItemIfc) {
					selectGiftCard(dataConnection, transaction,
							lineItem.getLineNumber(),
							(GiftCardPLUItemIfc) pluItem, lineItem);
				}

				// if alterations item, set line item alteration item flag,
				// alteration item price
				// and alteration
				if (pluItem instanceof AlterationPLUItemIfc) {
					lineItem.setAlterationItemFlag(true);
					AlterationPLUItemIfc altItem = (AlterationPLUItemIfc) pluItem;
					altItem.setPrice(lineItem.getSellingPrice());
					selectAlteration(dataConnection, transaction,
							lineItem.getLineNumber(), altItem);
				}

				lineItem.setPLUItem(pluItem);
				lineItem.getItemTax().setTaxGroupId(pluItem.getTaxGroupID());

				if (lineItem.getReturnItem() != null) {
					lineItem.getReturnItem().setPLUItem(pluItem);
					lineItem.getReturnItem().setPrice(pluItem.getPrice());
				}

				/*
				 * See if there is a commission modifier
				 */
				int sequenceNumber = lineItem.getLineNumber();

				try {
					String employeeID = selectCommissionModifier(
							dataConnection, transaction, sequenceNumber);
					// Detect if the sales associate is another person than the
					// cashier
					if (!employeeID.equals(transaction.getCashier()
							.getEmployeeID())) {
						((AbstractTransactionLineItem) lineItem)
								.setSalesAssociateModifiedFlag(true);
					}
					lineItem.setSalesAssociate(getEmployee(dataConnection,
							employeeID));

					if (!employeeID.equals(transaction.getSalesAssociate())) {
						lineItem.setSalesAssociateModifiedAtLineItem(true);
					}
				} catch (DataException de) {
					// ignore
				}
				/*
				 * Add item discounts for each line item
				 */
				// Ashish : Start added below lines
				// itemDiscounts =
				// (ItemDiscountStrategyIfc[])selectRetailPriceModifiers(dataConnection,
				// transaction, lineItem, localeRequestor);
				itemDiscounts = selectRetailPriceModifiers(dataConnection,
						transaction, lineItem, localeRequestor);

				// Ashish : End above lines
				itemDiscounts
						.addAll(selectSaleReturnPriceModifiers(dataConnection,
								transaction, lineItem, localeRequestor));
				lineItem.getItemPrice().setItemDiscounts(
						itemDiscounts.toArray(new ItemDiscountStrategyIfc[0]));

				/*
				 * See if there is an item tax entry
				 */
				ItemTaxIfc tax = selectSaleReturnTaxModifier(dataConnection,
						transaction, lineItem, localeRequestor);

				if (tax != null) {
					if (lineItem.getItemTaxMethod() == ItemTaxIfc.ITEM_TAX_EXTERNAL_RATE) {
						tax.setItemTaxAmount(lineItem.getItemPrice()
								.getItemTaxAmount());
						tax.setItemInclusiveTaxAmount(lineItem.getItemPrice()
								.getItemInclusiveTaxAmount());
					}

					lineItem.getItemPrice().setItemTax(tax);

					if (lineItem.getReturnItem() != null) {
						lineItem.getReturnItem().setTaxRate(
								tax.getDefaultRate());
					}
				}
				// When the sale return line item record was read, the tax mode
				// was unknown.
				// The mode can be set from vales in either
				// SaleReturnTaxLineItem
				// Table or the TaxModifier Table; however, if neither is
				// available,
				// this code set it explicitly based on the on item taxability.
				if (lineItem.getItemPrice().getItemTax().getTaxMode() == TAX_MODE_NOT_SET) {
					if (pluItem.getTaxable()) {
						lineItem.getItemPrice().getItemTax()
								.setTaxMode(TaxIfc.TAX_MODE_STANDARD);
					} else {
						lineItem.getItemPrice().getItemTax()
								.setTaxMode(TaxIfc.TAX_MODE_NON_TAXABLE);
					}
				}

				// add external order line item information
				selectExternalOrderLineItem(dataConnection, transaction,
						lineItem);

				lineItem.getItemPrice().calculateItemTotal();
			}
		} catch (SQLException exc) {
			dataConnection.logSQLException(exc, "Processing result set.");
			throw new DataException(DataException.SQL_ERROR,
					"error processing sale return line items", exc);
		}

		associateKitComponents(saleReturnLineItems);

		int numItems = saleReturnLineItems.size();
		SaleReturnLineItemIfc[] lineItems = new SaleReturnLineItemIfc[numItems];
		saleReturnLineItems.copyInto(lineItems);
		if (logger.isDebugEnabled())
			logger.debug("JdbcReadTransaction.selectSaleReturnLineItems");

		return (lineItems);
	}

	// Ashish : Start ( no change in below method)
	protected SaleReturnLineItemIfc[] selectDeletedSaleReturnLineItems(
			JdbcDataConnection dataConnection,
			SaleReturnTransactionIfc transaction,
			LocaleRequestor localeRequestor) throws DataException {
		if (logger.isDebugEnabled())
			logger.debug("JdbcReadTransaction.selectDeletedSaleReturnLineItems()");

		SQLSelectStatement sql = new SQLSelectStatement();
		/*
		 * Add Table(s)
		 */
		sql.addTable(TABLE_SALE_RETURN_LINE_ITEM, ALIAS_SALE_RETURN_LINE_ITEM);
		sql.addTable(TABLE_RETAIL_TRANSACTION_LINE_ITEM,
				ALIAS_RETAIL_TRANSACTION_LINE_ITEM);
		/*
		 * Add Column(s)
		 */
		sql.addColumn(ALIAS_SALE_RETURN_LINE_ITEM + "."
				+ FIELD_GIFT_REGISTRY_ID);
		sql.addColumn(ALIAS_SALE_RETURN_LINE_ITEM + "." + FIELD_POS_ITEM_ID);
		sql.addColumn(ALIAS_SALE_RETURN_LINE_ITEM + "." + FIELD_ITEM_ID);
		sql.addColumn(ALIAS_SALE_RETURN_LINE_ITEM + "."
				+ FIELD_SALE_RETURN_LINE_ITEM_QUANTITY);
		sql.addColumn(ALIAS_SALE_RETURN_LINE_ITEM + "."
				+ FIELD_SALE_RETURN_LINE_ITEM_EXTENDED_AMOUNT);
		sql.addColumn(ALIAS_SALE_RETURN_LINE_ITEM + "."
				+ FIELD_SALE_RETURN_LINE_ITEM_VAT_AMOUNT);
		sql.addColumn(ALIAS_SALE_RETURN_LINE_ITEM + "."
				+ FIELD_SALE_RETURN_LINE_ITEM_TAX_INC_AMOUNT);
		sql.addColumn(ALIAS_SALE_RETURN_LINE_ITEM + "."
				+ FIELD_RETAIL_TRANSACTION_LINE_ITEM_SEQUENCE_NUMBER);
		sql.addColumn(ALIAS_SALE_RETURN_LINE_ITEM + "." + FIELD_SERIAL_NUMBER);
		sql.addColumn(ALIAS_SALE_RETURN_LINE_ITEM + "."
				+ FIELD_SALE_RETURN_LINE_ITEM_RETURN_QUANTITY);
		sql.addColumn(ALIAS_SALE_RETURN_LINE_ITEM + "."
				+ FIELD_POS_ORIGINAL_TRANSACTION_ID);
		sql.addColumn(ALIAS_SALE_RETURN_LINE_ITEM + "."
				+ FIELD_ORIGINAL_BUSINESS_DAY_DATE);
		sql.addColumn(ALIAS_SALE_RETURN_LINE_ITEM + "."
				+ FIELD_ORIGINAL_RETAIL_TRANSACTION_LINE_ITEM_SEQUENCE_NUMBER);
		sql.addColumn(ALIAS_SALE_RETURN_LINE_ITEM + "."
				+ FIELD_ORIGINAL_RETAIL_STORE_ID);
		sql.addColumn(ALIAS_SALE_RETURN_LINE_ITEM + "."
				+ FIELD_MERCHANDISE_RETURN_FLAG);
		sql.addColumn(ALIAS_SALE_RETURN_LINE_ITEM + "."
				+ FIELD_MERCHANDISE_RETURN_REASON_CODE);
		sql.addColumn(ALIAS_SALE_RETURN_LINE_ITEM + "."
				+ FIELD_MERCHANDISE_RETURN_ITEM_CONDITION_CODE);
		sql.addColumn(ALIAS_SALE_RETURN_LINE_ITEM + "."
				+ FIELD_SALE_RETURN_LINE_ITEM_RESTOCKING_FEE_AMOUNT);
		sql.addColumn(ALIAS_SALE_RETURN_LINE_ITEM + "."
				+ FIELD_SALE_RETURN_LINE_ITEM_DEPOSIT_AMOUNT);
		sql.addColumn(ALIAS_SALE_RETURN_LINE_ITEM + "."
				+ FIELD_SALE_RETURN_LINE_ITEM_BALANCE_DUE);
		sql.addColumn(ALIAS_SALE_RETURN_LINE_ITEM + "."
				+ FIELD_SALE_RETURN_LINE_ITEM_PICKUP_CANCEL_PRICE);
		sql.addColumn(ALIAS_SALE_RETURN_LINE_ITEM + "."
				+ FIELD_SALE_RETURN_LINE_ITEM_PICKUP_INSTORE_PRICE);
		sql.addColumn(ALIAS_SALE_RETURN_LINE_ITEM + "."
				+ FIELD_ITEM_KIT_SET_CODE);
		sql.addColumn(ALIAS_SALE_RETURN_LINE_ITEM + "."
				+ FIELD_ITEM_COLLECTION_ID);
		sql.addColumn(ALIAS_SALE_RETURN_LINE_ITEM + "."
				+ FIELD_ITEM_KIT_HEADER_REFERENCE_ID);
		sql.addColumn(ALIAS_SALE_RETURN_LINE_ITEM + "." + FIELD_SEND_FLAG);
		sql.addColumn(ALIAS_SALE_RETURN_LINE_ITEM + "."
				+ FIELD_SHIPPING_CHARGE_FLAG);
		sql.addColumn(ALIAS_SALE_RETURN_LINE_ITEM + "."
				+ FIELD_SEND_LABEL_COUNT);
		sql.addColumn(ALIAS_SALE_RETURN_LINE_ITEM + "."
				+ FIELD_GIFT_RECEIPT_FLAG);
		sql.addColumn(ALIAS_SALE_RETURN_LINE_ITEM + "." + FIELD_ORDER_ID);
		sql.addColumn(ALIAS_SALE_RETURN_LINE_ITEM + "."
				+ FIELD_ORDER_LINE_ITEM_SEQUENCE_NUMBER);
		sql.addColumn(ALIAS_SALE_RETURN_LINE_ITEM + "."
				+ FIELD_ITEM_ID_ENTRY_METHOD_CODE);
		sql.addColumn(ALIAS_SALE_RETURN_LINE_ITEM + "." + FIELD_SIZE_CODE);
		sql.addColumn(ALIAS_RETAIL_TRANSACTION_LINE_ITEM + "."
				+ FIELD_RETAIL_TRANSACTION_LINE_ITEM_VOID_FLAG);
		sql.addColumn(ALIAS_SALE_RETURN_LINE_ITEM + "."
				+ FIELD_ITEM_PRICEADJ_LINE_ITEM_FLAG);
		sql.addColumn(ALIAS_SALE_RETURN_LINE_ITEM + "."
				+ FIELD_ITEM_PRICEADJ_REFERENCE_ID);
		sql.addColumn(ALIAS_SALE_RETURN_LINE_ITEM + "."
				+ FIELD_RETURN_RELATED_ITEM_FLAG);
		sql.addColumn(ALIAS_SALE_RETURN_LINE_ITEM + "."
				+ FIELD_RELATED_ITEM_TRANSACTION_LINE_ITEM_SEQ_NUMBER);
		sql.addColumn(ALIAS_SALE_RETURN_LINE_ITEM + "."
				+ FIELD_REMOVE_RELATED_ITEM_FLAG);
		sql.addColumn(ALIAS_SALE_RETURN_LINE_ITEM + "."
				+ FIELD_RETAIL_TRANSACTION_LINE_ITEM_SALES_ASSC_FLAG);
		sql.addColumn(ALIAS_SALE_RETURN_LINE_ITEM + "."
				+ FIELD_RETAIL_TRANSACTION_LINE_ITEM_PREMANENT_RETAIL_PRICE);
		sql.addColumn(ALIAS_SALE_RETURN_LINE_ITEM + "."
				+ FIELD_RETAIL_TRANSACTION_LINE_ITEM_RECEIPT_DESCRIPTION);
		sql.addColumn(ALIAS_SALE_RETURN_LINE_ITEM + "."
				+ FIELD_RETAIL_TRANSACTION_LINE_ITEM_RECEIPT_DESCRIPTION_LOCALE);
		sql.addColumn(ALIAS_SALE_RETURN_LINE_ITEM + "."
				+ FIELD_RETAIL_TRANSACTION_LINE_ITEM_RESTOCKING_FEE_FLAG);
		sql.addColumn(ALIAS_SALE_RETURN_LINE_ITEM + "."
				+ FIELD_SERIALIZED_ITEM_VALIDATION_FLAG);
		sql.addColumn(ALIAS_SALE_RETURN_LINE_ITEM + "."
				+ FIELD_EXTERNAL_VALIDATION_SERIALIZED_ITEM_FLAG);
		sql.addColumn(ALIAS_SALE_RETURN_LINE_ITEM + "."
				+ FIELD_SERIALIZED_ITEM_EXTERNAL_SYSTEM_CREATE_UIN);
		sql.addColumn(ALIAS_SALE_RETURN_LINE_ITEM
				+ "."
				+ FIELD_RETAIL_TRANSACTION_LINE_ITEM_MERCHANDISE_HIERARCHY_LEVEL_CODE);
		sql.addColumn(ALIAS_SALE_RETURN_LINE_ITEM + "."
				+ FIELD_RETAIL_TRANSACTION_LINE_ITEM_SIZE_REQUIRED_FLAG);
		sql.addColumn(ALIAS_SALE_RETURN_LINE_ITEM + "."
				+ FIELD_RETAIL_TRANSACTION_LINE_ITEM_SALE_UNIT_OF_MEASURE_CODE);
		sql.addColumn(ALIAS_SALE_RETURN_LINE_ITEM + "."
				+ FIELD_RETAIL_TRANSACTION_LINE_ITEM_POS_DEPARTMENT_ID);
		sql.addColumn(ALIAS_SALE_RETURN_LINE_ITEM + "."
				+ FIELD_RETAIL_TRANSACTION_LINE_ITEM_SALE_ITEM_TYPE_ID);
		sql.addColumn(ALIAS_SALE_RETURN_LINE_ITEM + "."
				+ FIELD_RETAIL_TRANSACTION_LINE_ITEM_RETURN_PROHIBITED_FLAG);
		sql.addColumn(ALIAS_SALE_RETURN_LINE_ITEM
				+ "."
				+ FIELD_RETAIL_TRANSACTION_LINE_ITEM_EMPLOYEE_DISCOUNT_ALOWED_FLAG);
		sql.addColumn(ALIAS_SALE_RETURN_LINE_ITEM + "." + FIELD_TAX_GROUP_ID);
		sql.addColumn(ALIAS_SALE_RETURN_LINE_ITEM + "."
				+ FIELD_RETAIL_TRANSACTION_LINE_ITEM_TAXABLE_FLAG);
		sql.addColumn(ALIAS_SALE_RETURN_LINE_ITEM + "."
				+ FIELD_ITEM_DISCOUNT_FLAG);
		sql.addColumn(ALIAS_SALE_RETURN_LINE_ITEM + "."
				+ FIELD_ITEM_DAMAGE_DISCOUNT_FLAG);
		sql.addColumn(ALIAS_SALE_RETURN_LINE_ITEM
				+ "."
				+ FIELD_RETAIL_TRANSACTION_LINE_ITEM_MERCHANDISE_HIERARCHY_GROUP_ID);
		sql.addColumn(ALIAS_SALE_RETURN_LINE_ITEM + "."
				+ FIELD_RETAIL_TRANSACTION_LINE_ITEM_MANUFACTURER_ITEM_UPC);
		sql.addColumn(ALIAS_SALE_RETURN_LINE_ITEM + "."
				+ NON_RETRIEVED_ORIGINAL_RECEIPT_ID);
		sql.addColumn(ALIAS_SALE_RETURN_LINE_ITEM + "."
				+ FIELD_SALE_AGE_RESTRICTION_ID);
		sql.addColumn(ALIAS_SALE_RETURN_LINE_ITEM + "."
				+ FIELD_CLEARANCE_INDICATOR);
		sql.addColumn(ALIAS_SALE_RETURN_LINE_ITEM + "."
				+ FIELD_ITEM_PRICE_ENTRY_REQUIRED_FLAG);

		/*
		 * Add Qualifier(s)
		 */
		// For the specific transaction only
		sql.addQualifier(ALIAS_SALE_RETURN_LINE_ITEM + "."
				+ FIELD_RETAIL_STORE_ID + " = " + getStoreID(transaction));
		sql.addQualifier(ALIAS_SALE_RETURN_LINE_ITEM + "."
				+ FIELD_WORKSTATION_ID + " = " + getWorkstationID(transaction));
		sql.addQualifier(ALIAS_SALE_RETURN_LINE_ITEM + "."
				+ FIELD_TRANSACTION_SEQUENCE_NUMBER + " = "
				+ getTransactionSequenceNumber(transaction));
		sql.addQualifier(ALIAS_SALE_RETURN_LINE_ITEM + "."
				+ FIELD_BUSINESS_DAY_DATE + " = "
				+ getBusinessDayString(transaction));
		sql.addQualifier(ALIAS_RETAIL_TRANSACTION_LINE_ITEM + "."
				+ FIELD_RETAIL_STORE_ID + "=" + ALIAS_SALE_RETURN_LINE_ITEM
				+ "." + FIELD_RETAIL_STORE_ID);
		sql.addQualifier(ALIAS_RETAIL_TRANSACTION_LINE_ITEM + "."
				+ FIELD_WORKSTATION_ID + " = " + ALIAS_SALE_RETURN_LINE_ITEM
				+ "." + FIELD_WORKSTATION_ID);
		sql.addQualifier(ALIAS_RETAIL_TRANSACTION_LINE_ITEM + "."
				+ FIELD_TRANSACTION_SEQUENCE_NUMBER + " = "
				+ ALIAS_SALE_RETURN_LINE_ITEM + "."
				+ FIELD_TRANSACTION_SEQUENCE_NUMBER);
		sql.addQualifier(ALIAS_RETAIL_TRANSACTION_LINE_ITEM + "."
				+ FIELD_RETAIL_TRANSACTION_LINE_ITEM_SEQUENCE_NUMBER + " = "
				+ ALIAS_SALE_RETURN_LINE_ITEM + "."
				+ FIELD_RETAIL_TRANSACTION_LINE_ITEM_SEQUENCE_NUMBER);
		sql.addQualifier(ALIAS_RETAIL_TRANSACTION_LINE_ITEM + "."
				+ FIELD_BUSINESS_DAY_DATE + " = " + ALIAS_SALE_RETURN_LINE_ITEM
				+ "." + FIELD_BUSINESS_DAY_DATE);

		// order by line item sequence number
		sql.addOrdering(ALIAS_SALE_RETURN_LINE_ITEM + "."
				+ FIELD_RETAIL_TRANSACTION_LINE_ITEM_SEQUENCE_NUMBER + " ASC");

		Vector<SaleReturnLineItemIfc> saleReturnLineItems = new Vector<SaleReturnLineItemIfc>(
				2);

		try {
			dataConnection.execute(sql.getSQLString());
			ResultSet rs = (ResultSet) dataConnection.getResult();
			HashMap<Integer, String> reasonCodeMap = new HashMap<Integer, String>(
					1);
			HashMap<Integer, String> itemConditionCodeMap = new HashMap<Integer, String>(
					1);

			while (rs.next()) {
				int index = 0;
				String giftRegistryID = getSafeString(rs, ++index);
				String posItemID = getSafeString(rs, ++index);
				String itemID = getSafeString(rs, ++index);
				BigDecimal quantity = getBigDecimal(rs, ++index);
				CurrencyIfc amount = getCurrencyFromDecimal(rs, ++index);
				CurrencyIfc itemTaxAmount = getCurrencyFromDecimal(rs, ++index);
				CurrencyIfc itemIncTaxAmount = getCurrencyFromDecimal(rs,
						++index);
				int sequenceNumber = rs.getInt(++index);
				String serialNumber = getSafeString(rs, ++index);
				BigDecimal quantityReturned = getBigDecimal(rs, ++index);
				String originalTransactionID = getSafeString(rs, ++index);
				EYSDate originalTransactionBusinessDay = getEYSDateFromString(
						rs, ++index);
				int originalTransactionLineNumber = rs.getInt(++index);
				String originalStoreID = getSafeString(rs, ++index);
				boolean returnFlag = getBooleanFromString(rs, ++index);
				String returnReasonCode = getSafeString(rs, ++index);
				String returnItemConditionCode = getSafeString(rs, ++index);
				if (StringUtils.isBlank(returnItemConditionCode)) {
					returnItemConditionCode = CodeConstantsIfc.CODE_UNDEFINED;
				}
				CurrencyIfc restockingFee = getCurrencyFromDecimal(rs, ++index);
				CurrencyIfc depositAmount = getCurrencyFromDecimal(rs, ++index);
				CurrencyIfc balanceDue = getCurrencyFromDecimal(rs, ++index);
				boolean pickupCancelledPrice = getBooleanFromString(rs, ++index);
				boolean pickupInStorePrice = getBooleanFromString(rs, ++index);
				int kitCode = rs.getInt(++index);
				String itemKitID = getSafeString(rs, ++index);
				int kitReference = rs.getInt(++index);
				String sendFlag = getSafeString(rs, ++index);
				String shippingChargeFlag = getSafeString(rs, ++index);
				int sendLabelCount = rs.getInt(++index);
				String giftReceiptStr = getSafeString(rs, ++index);
				String orderID = rs.getString(++index);
				int orderLineReference = rs.getInt(++index);
				String entryMethod = getSafeString(rs, ++index);
				String sizeCode = getSafeString(rs, ++index);
				String itemVoidFlag = getSafeString(rs, ++index);
				boolean isPriceAdjLineItem = rs.getBoolean(++index);
				int priceAdjReferenceID = rs.getInt(++index);
				boolean returnRelatedItemFlag = rs.getBoolean(++index);
				int relatedSeqNumber = rs.getInt(++index);
				boolean deleteRelatedItemFlag = rs.getBoolean(++index);
				boolean saleAsscModifiedFlag = getBooleanFromString(rs, ++index);
				CurrencyIfc beforeOverride = getCurrencyFromDecimal(rs, ++index);
				String receiptDescription = getSafeString(rs, ++index);
				Locale receiptDescriptionLocale = LocaleUtilities
						.getLocaleFromString(getSafeString(rs, ++index));
				boolean restockingFeeFlag = rs.getBoolean(++index);
				boolean serializedItemFlag = rs.getBoolean(++index);
				boolean externalValidationSerializedItemFlag = rs
						.getBoolean(++index);
				boolean isPOSAllowedToCreateUIN = rs.getBoolean(++index);
				String productGroupID = getSafeString(rs, ++index);
				boolean sizeRequiredFlag = rs.getBoolean(++index);
				String unitOfMeasureCode = getSafeString(rs, ++index);
				String posDepartmentID = getSafeString(rs, ++index);
				int itemTypeID = rs.getInt(++index);
				boolean returnEligible = !(rs.getBoolean(++index));
				boolean employeeDiscountEligible = (rs.getBoolean(++index));
				int taxGroupId = rs.getInt(++index);
				boolean taxable = (rs.getBoolean(++index));
				boolean discountable = (rs.getBoolean(++index));
				boolean damageDiscountable = (rs.getBoolean(++index));
				String merchandiseHierarchyGroupID = getSafeString(rs, ++index);
				String manufacturerItemUPC = getSafeString(rs, ++index);
				String nonRetrievedOriginalReceiptId = getSafeString(rs,
						++index);
				int restrictiveAge = rs.getInt(++index);
				boolean clearanceIndicator = (rs.getBoolean(++index));
				boolean priceEntryRequired = (rs.getBoolean(++index));

				// create and initialize item price object
				ItemPriceIfc price = DomainGateway.getFactory()
						.getItemPriceInstance();
				price.setExtendedSellingPrice(amount);
				price.setDiscountEligible(discountable);
				price.setExtendedRestockingFee(restockingFee);
				// The tax mode is unknown at this point; It can be set from
				// SaleReturnTaxLineItem Table or the TaxModifier Table;
				// however,
				// if neither is available it must be explicitly set
				// depending on item taxability.
				price.getItemTax().setTaxMode(TAX_MODE_NOT_SET);

				if (quantity.signum() != 0) {
					amount = amount.divide(new BigDecimal(quantity.toString()));
					if (restockingFee != null) {
						restockingFee = restockingFee.divide(new BigDecimal(
								quantity.toString()));
					}
				}

				price.setSellingPrice(amount);
				price.setPermanentSellingPrice(beforeOverride);
				price.setRestockingFee(restockingFee);
				price.setItemTaxAmount(itemTaxAmount);
				price.setItemInclusiveTaxAmount(itemIncTaxAmount);
				// price.setTaxGroupId(taxGroupID);
				price.setItemQuantity(quantity);

				SaleReturnLineItemIfc lineItem;
				// create and initialize appropriate line item object
				if (transaction.getTransactionType() == TransactionIfc.TYPE_ORDER_CANCEL
						|| transaction.getTransactionType() == TransactionIfc.TYPE_ORDER_COMPLETE
						|| transaction.getTransactionType() == TransactionIfc.TYPE_ORDER_PARTIAL
						|| transaction.getTransactionType() == TransactionIfc.TYPE_ORDER_INITIATE) {
					lineItem = DomainGateway.getFactory()
							.getOrderLineItemInstance();
				} else {
					switch (kitCode) {
					case ItemKitConstantsIfc.ITEM_KIT_CODE_HEADER:
						lineItem = DomainGateway.getFactory()
								.getKitHeaderLineItemInstance();
						break;
					case ItemKitConstantsIfc.ITEM_KIT_CODE_COMPONENT:
						lineItem = DomainGateway.getFactory()
								.getKitComponentLineItemInstance();
						((KitComponentLineItemIfc) lineItem)
								.setItemKitID(itemKitID);
						break;
					default:
						lineItem = DomainGateway.getFactory()
								.getSaleReturnLineItemInstance();
						break;
					}
				}

				lineItem.setPLUItemID(itemID);
				lineItem.setItemPrice(price);
				lineItem.modifyItemQuantity(quantity);
				lineItem.setLineNumber(sequenceNumber);

				// set the order id & line item reference number
				lineItem.setOrderID(orderID);
				lineItem.setOrderLineReference(orderLineReference);

				lineItem.setReceiptDescription(receiptDescription);
				lineItem.setReceiptDescriptionLocale(receiptDescriptionLocale);
				lineItem.getItemPrice().setEmployeeDiscountEligible(
						employeeDiscountEligible);

				// set the KitHeaderReference
				lineItem.setKitHeaderReference(kitReference);

				lineItem.setDepositAmount(depositAmount);

				if (lineItem instanceof OrderLineItemIfc) {
					((OrderLineItemIfc) lineItem).setItemBalanceDue(balanceDue);
					((OrderLineItemIfc) lineItem)
							.setPriceCancelledDuringPickup(pickupCancelledPrice);
					((OrderLineItemIfc) lineItem)
							.setInStorePriceDuringPickup(pickupInStorePrice);
				}

				if (serialNumber != null && serialNumber.length() > 0) {
					lineItem.setItemSerial(serialNumber);
				}
				lineItem.setQuantityReturned(quantityReturned);
				/*
				 * Should probably be a relationship fetched from the gift
				 * registry table
				 */
				if (giftRegistryID.length() > 0) {
					RegistryIDIfc registry = instantiateGiftRegistry();
					registry.setID(giftRegistryID);
					lineItem.modifyItemRegistry(registry, true);
				}

				// Return Item Original Transaction information is available
				if (returnFlag) {
					ReturnItemIfc ri = DomainGateway.getFactory()
							.getReturnItemInstance();

					if (originalTransactionID != null
							&& originalTransactionID.length() > 0) {
						// Create the transaction id.
						TransactionIDIfc id = DomainGateway.getFactory()
								.getTransactionIDInstance();
						id.setTransactionID(originalTransactionID);
						ri.setOriginalTransactionID(id);
					}
					ri.setNonRetrievedOriginalReceiptId(nonRetrievedOriginalReceiptId);

					if (originalTransactionBusinessDay != null) {
						ri.setOriginalTransactionBusinessDate(originalTransactionBusinessDay);
					}
					ri.setOriginalLineNumber(originalTransactionLineNumber);

					// DB2 does not support nested result sets, so the localized
					// reason codes needs to be retrieved after closing the
					// current
					// result set. Add the reason code to a map for later
					// retrieval.
					reasonCodeMap.put(sequenceNumber, returnReasonCode);
					itemConditionCodeMap.put(sequenceNumber,
							returnItemConditionCode);

					if (originalStoreID.equals(transaction.getWorkstation()
							.getStoreID())) {
						ri.setStore(transaction.getWorkstation().getStore());
					} else {
						StoreIfc store = DomainGateway.getFactory()
								.getStoreInstance();
						store.setStoreID(originalStoreID);
						ri.setStore(store);
					}

					// ri.setTaxRate(lineItem.getItemTax().getDefaultRate()); -
					// external tax mgr
					// this field should be unnecessary, but it isn't
					// if really want sales assoc. tied with a return item, then
					// execute this code.

					lineItem.setReturnItem(ri);
				}
				/*
				 * Sales Associate defaults to the cashier. If it's different,
				 * that will be accounted for by the selectCommissionModifier
				 * below.
				 */
				if (transaction.getSalesAssociate() != null) {
					lineItem.setSalesAssociate(transaction.getSalesAssociate());
				} else {
					lineItem.setSalesAssociate(transaction.getCashier());
				}
				/*
				 * Send Flag
				 */
				if (transaction.getTransactionType() != TransactionIfc.TYPE_RETURN) {
					if (sendFlag.equals("0")) {
						lineItem.setItemSendFlag(false);
					} else if (sendFlag.equals("1")) {
						lineItem.setItemSendFlag(true);
					}
					lineItem.setSendLabelCount(sendLabelCount);
					/*
					 * Shipping Charge Flag
					 */
					if (shippingChargeFlag.equals("0")) {
						lineItem.setShippingCharge(false);
					} else if (sendFlag.equals("1")) {
						lineItem.setShippingCharge(true);
					}
				}

				/**
				 * Gift Receipt Flag
				 */
				if (giftReceiptStr.equals("1")) {
					lineItem.setGiftReceiptItem(true);
				}

				/**
				 * Price Adjustment Flags
				 */
				lineItem.setPriceAdjustmentReference(priceAdjReferenceID);
				lineItem.setIsPriceAdjustmentLineItem(isPriceAdjLineItem);

				lineItem.setItemSizeCode(sizeCode);

				if (itemVoidFlag.equals(DBConstantsIfc.TRUE)) {
					saleReturnLineItems.addElement(lineItem);
				}

				lineItem.setRelatedItemReturnable(returnRelatedItemFlag);
				lineItem.setRelatedItemSequenceNumber(relatedSeqNumber);
				lineItem.setRelatedItemDeleteable(deleteRelatedItemFlag);
				lineItem.setSalesAssociateModifiedFlag(saleAsscModifiedFlag);

				// Generate a pluItem from the data in the Sale Return Line Item
				// table
				PLUItemIfc pluItem = instantiatePLUItem(productGroupID,
						kitCode, transaction.isTrainingMode());
				pluItem.setItemID(itemID);
				pluItem.setPosItemID(posItemID);
				pluItem.setItemSizeRequired(sizeRequiredFlag);
				pluItem.setDepartmentID(posDepartmentID);
				pluItem.setTaxable(taxable);
				pluItem.setTaxGroupID(taxGroupId);
				pluItem.setManufacturerItemUPC(manufacturerItemUPC);
				ItemClassificationIfc itemClassification = DomainGateway
						.getFactory().getItemClassificationInstance();
				itemClassification.setRestockingFeeFlag(restockingFeeFlag);
				itemClassification.setSerializedItem(serializedItemFlag);
				if (externalValidationSerializedItemFlag) {
					itemClassification
							.setSerialEntryTime(STORE_RECEIVING_SERIALIZED_CAPTURE_TIME);
				} else {
					itemClassification
							.setSerialEntryTime(SALE_SERIALIZED_CAPTURE_TIME);
				}
				itemClassification
						.setExternalSystemCreateUIN(isPOSAllowedToCreateUIN);
				itemClassification.setPriceEntryRequired(priceEntryRequired);
				ProductGroupIfc pg = DomainGateway.getFactory()
						.getProductGroupInstance();
				pg.setGroupID(productGroupID);
				itemClassification.setGroup(pg);
				itemClassification.setItemType(itemTypeID);
				itemClassification.setReturnEligible(returnEligible);
				itemClassification
						.setEmployeeDiscountAllowedFlag(employeeDiscountEligible);
				itemClassification.setDiscountEligible(discountable);
				itemClassification
						.setDamageDiscountEligible(damageDiscountable);
				itemClassification
						.setMerchandiseHierarchyGroup(merchandiseHierarchyGroupID);
				pluItem.setItemClassification(itemClassification);
				lineItem.setOnClearance(clearanceIndicator);
				pluItem.setSellingPrice(lineItem.getItemPrice()
						.getPermanentSellingPrice());
				UnitOfMeasureIfc pluUOM = DomainGateway.getFactory()
						.getUnitOfMeasureInstance();
				pluUOM.setUnitID(unitOfMeasureCode);
				pluItem.setUnitOfMeasure(pluUOM);
				pluItem.setRestrictiveAge(restrictiveAge);
				selectOptionalI18NPLUData(dataConnection, pluItem,
						localeRequestor, lineItem);
				lineItem.setPLUItem(pluItem);
			}
			rs.close();

			// Set the localized reason code for return line items
			for (SaleReturnLineItemIfc saleReturnLineItem : saleReturnLineItems) {
				if (saleReturnLineItem.isReturnLineItem()) {
					int sequenceNumber = saleReturnLineItem.getLineNumber();

					// Get the reason code from the map
					String reasonCode = reasonCodeMap.get(sequenceNumber);

					// Retrieve localized reason code
					saleReturnLineItem
							.getReturnItem()
							.setReason(
									getInitializedLocalizedReasonCode(
											dataConnection,
											transaction
													.getTransactionIdentifier()
													.getStoreID(),
											reasonCode,
											CodeConstantsIfc.CODE_LIST_RETURN_REASON_CODES,
											localeRequestor));

					// Get the item condition code from the map
					String itemConditionCode = itemConditionCodeMap
							.get(sequenceNumber);

					// retreve localized item condition code
					saleReturnLineItem
							.getReturnItem()
							.setItemCondition(
									getInitializedLocalizedReasonCode(
											dataConnection,
											transaction
													.getTransactionIdentifier()
													.getStoreID(),
											itemConditionCode,
											CodeConstantsIfc.CODE_LIST_RETURN_ITEM_CONDITION_CODES,
											localeRequestor));
				}
			}

			/*
			 * Grab auxilliary elements
			 */
			List<ItemDiscountStrategyIfc> itemDiscounts = null;

			for (SaleReturnLineItemIfc lineItem : saleReturnLineItems) {
				PLUItemIfc pluItem = lineItem.getPLUItem();
				;

				if (lineItem.isKitComponent() && pluItem.isKitComponent()) {
					pluItem.setItemID(lineItem.getPLUItemID());
					((KitComponentIfc) pluItem)
							.setItemKitID(((KitComponentLineItemIfc) lineItem)
									.getItemKitID());
				}

				// if gift card, find gift card number
				if (pluItem instanceof GiftCardPLUItemIfc) {
					selectGiftCard(dataConnection, transaction,
							lineItem.getLineNumber(),
							(GiftCardPLUItemIfc) pluItem, lineItem);
				}

				// if alterations item, set line item alteration item flag,
				// alteration item price,
				// and alteration
				if (pluItem instanceof AlterationPLUItemIfc) {
					lineItem.setAlterationItemFlag(true);
					AlterationPLUItemIfc altItem = (AlterationPLUItemIfc) pluItem;
					altItem.setPrice(lineItem.getSellingPrice());
					selectAlteration(dataConnection, transaction,
							lineItem.getLineNumber(), altItem);
				}

				lineItem.setPLUItem(pluItem);
				lineItem.getItemTax().setTaxGroupId(pluItem.getTaxGroupID());

				if (lineItem.getReturnItem() != null) {
					lineItem.getReturnItem().setPLUItem(pluItem);
					lineItem.getReturnItem().setPrice(pluItem.getPrice());
				}
				/*
				 * See if there is a commission modifier
				 */
				int sequenceNumber = lineItem.getLineNumber();

				try {
					String employeeID = selectCommissionModifier(
							dataConnection, transaction, sequenceNumber);
					// Detect if the sales associate is another person than the
					// cashier
					if (!employeeID.equals(transaction.getCashier()
							.getEmployeeID())) {
						((AbstractTransactionLineItem) lineItem)
								.setSalesAssociateModifiedFlag(true);
					}
					lineItem.setSalesAssociate(getEmployee(dataConnection,
							employeeID));
					if (!employeeID.equals(transaction.getSalesAssociate()
							.getEmployeeID())) {
						lineItem.setSalesAssociateModifiedAtLineItem(true);
					}
				} catch (DataException de) {
					// ignore
				}
				/*
				 * Add item discounts for each line item
				 */
				itemDiscounts = selectRetailPriceModifiers(dataConnection,
						transaction, lineItem, localeRequestor);
				itemDiscounts
						.addAll(selectSaleReturnPriceModifiers(dataConnection,
								transaction, lineItem, localeRequestor));
				lineItem.getItemPrice().setItemDiscounts(
						itemDiscounts.toArray(new ItemDiscountStrategyIfc[0]));

				/*
				 * See if there is an item tax entry
				 */
				ItemTaxIfc tax = selectSaleReturnTaxModifier(dataConnection,
						transaction, lineItem, localeRequestor);

				if (tax != null) {
					if (lineItem.getItemTaxMethod() == ItemTaxIfc.ITEM_TAX_EXTERNAL_RATE) {
						tax.setItemTaxAmount(lineItem.getItemPrice()
								.getItemTaxAmount());
						tax.setItemInclusiveTaxAmount(lineItem.getItemPrice()
								.getItemInclusiveTaxAmount());
					}
					lineItem.getItemPrice().setItemTax(tax);

					if (lineItem.getReturnItem() != null) {
						lineItem.getReturnItem().setTaxRate(
								tax.getDefaultRate());
					}
				}

				// When the sale return line item record was read, the tax mode
				// was unknown.
				// The mode can be set from vales in either
				// SaleReturnTaxLineItem
				// Table or the TaxModifier Table; however, if neither is
				// available,
				// this code set it explicitly based on the on item taxability.
				if (lineItem.getItemPrice().getItemTax().getTaxMode() == TAX_MODE_NOT_SET) {
					if (pluItem.getTaxable()) {
						lineItem.getItemPrice().getItemTax()
								.setTaxMode(TaxIfc.TAX_MODE_STANDARD);
					} else {
						lineItem.getItemPrice().getItemTax()
								.setTaxMode(TaxIfc.TAX_MODE_NON_TAXABLE);
					}
				}

				// add external order line item information
				selectExternalOrderLineItem(dataConnection, transaction,
						lineItem);

				lineItem.getItemPrice().calculateItemTotal();
			}
		} catch (SQLException exc) {
			dataConnection.logSQLException(exc, "Processing result set.");
			throw new DataException(DataException.SQL_ERROR,
					"error processing sale return line items", exc);
		}

		associateKitComponents(saleReturnLineItems);

		int numItems = saleReturnLineItems.size();
		SaleReturnLineItemIfc[] lineItems = new SaleReturnLineItemIfc[numItems];
		saleReturnLineItems.copyInto(lineItems);

		if (logger.isDebugEnabled())
			logger.debug("JdbcReadTransaction.selectDeletedSaleReturnLineItems");

		return (lineItems);
	}

	// Ashish : End ( No Change in above method)

	public void setDiscountEmployeeIDOnTransaction(TransactionIfc transaction,
			String discountEmployeeID) {
		if (!Util.isEmpty(discountEmployeeID)
				&& transaction instanceof SaleReturnTransactionIfc
				&& ((SaleReturnTransactionIfc) transaction)
						.getEmployeeDiscountID() == null) {
			((SaleReturnTransactionIfc) transaction)
					.setEmployeeDiscountID(discountEmployeeID);
		}
	}

	// Ashih : Start added below method(these methods are not present in base
	// 14)
	static protected Point[] convertStringToPointArray(String value) {

		// Defense
		if (value == null || value.length() == 0) {
			// Force to the empty string, and we'll handle below.
			value = "";
		} else if (value.endsWith("'")) {
			// Strip off any trailing apostrophe.
			value = value.substring(0, value.length() - 1);
		}

		// Find the number of occurences of 'x' (and assume equal number of
		// 'y').
		int found = 0;
		int indexPos = 0; // where we last found our character
		int index = 0;
		while ((index = value.indexOf('x', indexPos)) > -1) {
			found++;
			indexPos = index + 1;
		}

		// The size of the Point array is the number of 'x' (and 'y') found.
		int size = found;
		Point[] points = new Point[size];
		for (int i = 0; i < size; i++) {
			points[i] = new Point(0, 0);
			if (value.indexOf("x") != -1) {
				points[i].x = Integer.parseInt(value.substring(
						value.indexOf("x") + 1, value.indexOf("y")));
				value = value.substring(value.indexOf("y"), value.length());
			}

			if (value.indexOf("y") != -1) {
				if (value.indexOf("x") != -1) {

					points[i].y = Integer.parseInt(value.substring(
							value.indexOf("y") + 1, value.indexOf("x")));
					value = value.substring(value.indexOf("x"), value.length());
				} else if (value.indexOf("y") == 0) {
					points[i].y = Integer.parseInt(value.substring(
							value.indexOf("y") + 1, value.length()));
					value = "";
				}
			}
		}

		return points;
	}

	protected CodeListIfc getReasonCodes(String storeID, int transactionType) {
		// Get the reason codes from the remote DB; this is the default behavior
		// for this
		// transaction.
		MAXCodeListMapIfc mapCodeList = null;
		CodeListIfc reasonCodeList = null;
		try {
			MAXCodeDataTransaction dt = null;
			dt = (MAXCodeDataTransaction) DataTransactionFactory
					.create(MAXDataTransactionKeys.MAX_CODE_DATA_TRANSACTION);
			Locale locale = LocaleMap
					.getLocale(LocaleConstantsIfc.USER_INTERFACE);

			// set locale and storeID
			SearchCriteriaIfc inquiry = DomainGateway.getFactory()
					.getSearchCriteriaInstance();
			inquiry.setLocaleRequestor(new LocaleRequestor(locale));
			inquiry.setStoreNumber(storeID);
			mapCodeList = dt.retrieveCodeListMap(inquiry);
			if (transactionType == TransactionConstantsIfc.TYPE_PAYOUT_TILL) {
				reasonCodeList = mapCodeList
						.get(CodeConstantsIfc.CODE_LIST_TILL_PAY_OUT_REASON_CODES);
			} else if (transactionType == TransactionConstantsIfc.TYPE_PAYIN_TILL) {
				reasonCodeList = mapCodeList
						.get(CodeConstantsIfc.CODE_LIST_TILL_PAY_IN_REASON_CODES);
			} else if (transactionType == TransactionConstantsIfc.TYPE_PAYROLL_PAYOUT_TILL) {
				reasonCodeList = mapCodeList
						.get(CodeConstantsIfc.CODE_LIST_TILL_PAYROLL_PAY_OUT_REASON_CODES);
			}
		} catch (DataException e) {
			// This is not a fatal error; log it as an error though.
			logger.error("Remote Code list map lookup failed: " + Util.EOL + ""
					+ e + "");

			// Now get the reason codes from local storeage. If there is an
			// error, the exeption will be caught in the call method.

		}
		return reasonCodeList;
	}

	/**
	 * 
	 * @param storeID
	 * @param transactionType
	 * @return CodeListIfc
	 * 
	 *         Builds the list of TillAdjustmentApprovalCodes
	 */

	protected CodeListIfc getApprovalCodes(String storeID, int transactionType) {
		// Get the reason codes from the remote DB; this is the default behavior
		// for this
		// transaction.
		MAXCodeListMapIfc mapCodeList = null;
		CodeListIfc approvalCodeList = null;
		try {
			MAXCodeDataTransaction dt = null;
			dt = (MAXCodeDataTransaction) DataTransactionFactory
					.create(MAXDataTransactionKeys.MAX_CODE_DATA_TRANSACTION);
			Locale locale = LocaleMap
					.getLocale(LocaleConstantsIfc.USER_INTERFACE);

			// set locale and storeID
			SearchCriteriaIfc inquiry = DomainGateway.getFactory()
					.getSearchCriteriaInstance();
			inquiry.setLocaleRequestor(new LocaleRequestor(locale));
			inquiry.setStoreNumber(storeID);
			mapCodeList = dt.retrieveCodeListMap(inquiry);
			if (transactionType == TransactionConstantsIfc.TYPE_PAYOUT_TILL) {
				approvalCodeList = mapCodeList
						.get(CodeConstantsIfc.CODE_LIST_TILL_PAY_OUT_APPROVAL_CODES);
			} else if (transactionType == TransactionConstantsIfc.TYPE_PAYROLL_PAYOUT_TILL) {
				approvalCodeList = mapCodeList
						.get(CodeConstantsIfc.CODE_LIST_TILL_PAYROLL_PAY_OUT_APPROVAL_CODES);
			}
		} catch (DataException e) {
			// This is not a fatal error; log it as an error though.
			logger.error("Remote Code list map lookup failed: " + Util.EOL + ""
					+ e + "");

			// Now get the reason codes from local storeage. If there is an
			// error, the exeption will be caught in the call method.

		}
		return approvalCodeList;
	}

	protected void selectAdvancedPricingRules(
			JdbcDataConnection dataConnection,
			SaleReturnTransactionIfc transaction) throws DataException {
		if (logger.isDebugEnabled())
			logger.debug("JdbcReadTransaction.selectAdvancedPricingRules()");
		AdvancedPricingRuleIfc rule = null;
		SQLSelectStatement sql = new SQLSelectStatement();

		/*
		 * Add Table(s)
		 */
		sql.setTable(TABLE_TRANSACTION_PRICE_DERIVATION_RULES);

		// Fields
		sql.addColumn(FIELD_RETAIL_STORE_ID);
		// sql.addColumn(FIELD_WORKSTATION_ID);
		// sql.addColumn(FIELD_BUSINESS_DAY_DATE);
		// sql.addColumn(FIELD_TRANSACTION_SEQUENCE_NUMBER);
		sql.addColumn(FIELD_PRICE_DERIVATION_RULE_ID);
		sql.addColumn(FIELD_COMPARISON_BASIS_CODE);
		sql.addColumn(FIELD_DISCOUNT_REFERENCE_ID);
		sql.addColumn(FIELD_DISCOUNT_REFERENCE_ID_TYPE_CODE);

		/*
		 * Add Qualifier(s)
		 */
		sql.addQualifier(FIELD_RETAIL_STORE_ID + " = "
				+ getStoreID(transaction));
		sql.addQualifier(FIELD_WORKSTATION_ID + " = "
				+ getWorkstationID(transaction));
		sql.addQualifier(FIELD_TRANSACTION_SEQUENCE_NUMBER + " = "
				+ getTransactionSequenceNumber(transaction));
		sql.addQualifier(FIELD_BUSINESS_DAY_DATE + " = "
				+ getBusinessDayString(transaction));
		try {
			dataConnection.execute(sql.getSQLString());
			ResultSet rs = (ResultSet) dataConnection.getResult();
			Vector rules = new Vector();

			while (rs.next()) {
				int index = 0;
				/* String storeId = getSafeString(rs, */++index;
				int ruleID = rs.getInt(++index);
				int comparisonBasis = rs.getInt(++index);

				String referenceID = rs.getString(++index);
				String referenceIDCodeStr = getSafeString(rs, ++index);

				AdvancedPricingRuleIfc advRule = DomainGateway.getFactory()
						.getAdvancedPricingRuleInstance();
				advRule.setRuleID(Integer.toString(ruleID));
				advRule.setSourceComparisonBasis(comparisonBasis);
				advRule.setReferenceID(referenceID);
				if (referenceIDCodeStr == null) {
					advRule.setReferenceIDCode(0);
				} else {
					for (int i = 0; i < DiscountRuleConstantsIfc.REFERENCE_ID_TYPE_CODE.length; i++) {
						if (referenceIDCodeStr
								.equalsIgnoreCase(DiscountRuleConstantsIfc.REFERENCE_ID_TYPE_CODE[i])) {
							advRule.setReferenceIDCode(i);
						}
					}

				}

				rules.add(advRule);
			} // end while (rs.next())

			rule = null;
			AdvancedPricingRuleIfc newRule = null;
			// DiscountListIfc currDiscList = null;
			for (int i = 0; i < rules.size(); i++) {
				rule = (AdvancedPricingRuleIfc) rules.elementAt(i);
				newRule = readAdvancedPricingRule(dataConnection, rule);
				if (rule != null && newRule != null) {
					switch (newRule.getAssignmentBasis()) {
					case DiscountRuleConstantsIfc.ASSIGNMENT_CUSTOMER:
					case DiscountRuleConstantsIfc.ASSIGNMENT_STORE_COUPON: {
						// Copy referenceID and ReferenceIDCode to newRule
						newRule.setReferenceID(rule.getReferenceID());
						newRule.setReferenceIDCode(rule.getReferenceIDCode());
						/*
						 * Removing this id, breaks
						 * ItemContainerProxy#areAllStoreCouponsApplied() since
						 * the coupon id is no longer a source. Why is this
						 * done? It is commented out in
						 * JdbcSCLUOperation#configureStoreCouponRules too
						 */
						// currDiscList = newRule.getSourceList();
						// if (currDiscList != null)
						// {
						// if
						// (currDiscList.containsEntry(newRule.getReferenceID()))
						// {
						// currDiscList.removeEntry(newRule.getReferenceID());
						// }
						// }
						// If the StoreCoupon is a Transaction Level Store
						// Coupon, must
						// set the checkSources to false and SourcesAreTargets
						// to true
						// for Advanced Pricing Logic.
						if (newRule.getDiscountScope() == DiscountRuleConstantsIfc.DISCOUNT_SCOPE_TRANSACTION) {
							newRule.setCheckSources(false);
							newRule.setSourcesAreTargets(true);
						}
						break;
					}
					default: {
						// Nothing
						break;
					}
					}
					transaction.addAdvancedPricingRule(newRule);
				} // if (rule != null)
			} // for (int i= 0; i< rules.size(); i++)
			rs.close();
		} catch (DataException de) {
			logger.error(de);
			throw de;
		} catch (SQLException se) {
			dataConnection.logSQLException(se,
					"transaction price derivation rules");
			throw new DataException(DataException.SQL_ERROR,
					"transaction price derivation rules", se);
		} catch (Exception e) {
			logger.error(e);
			throw new DataException(DataException.UNKNOWN,
					"transaction price derivation rules", e);
		}

		if (logger.isDebugEnabled())
			logger.debug("JdbcReadTransaction.selectAdvancedPricingRules()");
	}

	// Ashish : End added above method

	/* Nadia : Changes for price of item not coming on return starts */

	public TransactionIfc[] readTrainingTransactionsByID(
			JdbcDataConnection dataConnection, TransactionIfc transaction,
			LocaleRequestor localeRequestor) throws DataException {
		if (logger.isDebugEnabled())
			logger.debug("JdbcReadTransaction.readTrainingTransactionsByID()");
		SQLSelectStatement sql = new SQLSelectStatement();

		// add tables
		sql.addTable(TABLE_TRANSACTION, ALIAS_TRANSACTION);

		// add columns
		readTransactionsAddColumns(sql);

		// add qualifiers for the transaction ID
		sql.addQualifier(ALIAS_TRANSACTION, FIELD_TRANSACTION_SEQUENCE_NUMBER,
				getTransactionSequenceNumber(transaction));
		sql.addQualifier(ALIAS_TRANSACTION, FIELD_WORKSTATION_ID,
				getWorkstationID(transaction));
		sql.addQualifier(ALIAS_TRANSACTION, FIELD_RETAIL_STORE_ID,
				getStoreID(transaction));

		// add qualifiers if status is completed or voided
		if (transaction.getTransactionStatus() == TransactionIfc.STATUS_COMPLETED
				|| transaction.getTransactionStatus() == TransactionIfc.STATUS_VOIDED) {
			sql.addQualifier("(" + FIELD_TRANSACTION_STATUS_CODE + " = "
					+ TransactionIfc.STATUS_COMPLETED + " OR "
					+ FIELD_TRANSACTION_STATUS_CODE + " = "
					+ TransactionIfc.STATUS_VOIDED + ")");
		}

		// see if businessDate is specified
		if (transaction.getBusinessDay() != null) {
			sql.addQualifier(ALIAS_TRANSACTION, FIELD_BUSINESS_DAY_DATE,
					getBusinessDayString(transaction));
		}

		sql.addOrdering(ALIAS_TRANSACTION, FIELD_BUSINESS_DAY_DATE);

		// set up transaction array
		TransactionIfc[] transactions = null;

		Vector<TransactionIfc> transVector = new Vector<TransactionIfc>();
		transVector = readTransactionsExecuteAndParse(dataConnection, sql,
				false, localeRequestor);

		transactions = new TransactionIfc[transVector.size()];
		transVector.copyInto(transactions);

		readStoreLocations(dataConnection, transVector, localeRequestor);

		if (logger.isDebugEnabled())
			logger.debug("JdbcReadTransaction.readTrainingTransactionsByID()");

		return (transactions);
	}

	public Vector<TransactionIfc> readTransactionsExecuteAndParse(
			JdbcDataConnection dataConnection, SQLSelectStatement sql,
			boolean retrieveStoreCoupons, LocaleRequestor localeRequestor)
			throws DataException {
		Vector<TransactionIfc> transVector = new Vector<TransactionIfc>(2);
		Vector<TransactionIfc> completedTransVector = new Vector<TransactionIfc>(
				2);
		try {
			// execute sql and get result set
			dataConnection.execute(sql.getSQLString());
			ResultSet rs = (ResultSet) dataConnection.getResult();

			// loop through result set
			while (rs.next()) {
				// parse the data from the database
				int index = 0;
				boolean trainingFlag = getBooleanFromString(rs, ++index);
				Timestamp endTimestamp = rs.getTimestamp(++index);
				Timestamp beginTimestamp = rs.getTimestamp(++index);
				int transType = rs.getInt(++index);
				int sequenceNumber = rs.getInt(++index);
				String operatorID = getSafeString(rs, ++index);
				String storeID = getSafeString(rs, ++index);
				String workstationID = getSafeString(rs, ++index);
				EYSDate businessDate = getEYSDateFromString(rs, ++index);
				int status = rs.getInt(++index);
				String tillID = getSafeString(rs, ++index);
				String customerInfoType = getSafeString(rs, ++index);
				String customerInfoData = getSafeString(rs, ++index);
				int postProcessingStatus = rs.getInt(++index);
				boolean reentryMode = getBooleanFromString(rs, ++index);

				// Instantiate Store
				StoreIfc store = instantiateStore();
				store.setStoreID(storeID);

				// Instantiate Workstation
				WorkstationIfc workstation = instantiateWorkstation();
				workstation.setWorkstationID(workstationID);
				workstation.setStore(store);

				// Instantiate Employee
				// Note: operator is a reserved word
				EmployeeIfc operatingEmployee = instantiateEmployee();
				operatingEmployee.setEmployeeID(operatorID);

				// Instantiate Transaction
				TransactionIfc transaction = createTransaction(transType);

				transaction
						.setTimestampBegin(timestampToEYSDate(beginTimestamp));
				transaction.setTimestampEnd(timestampToEYSDate(endTimestamp));
				transaction.setTransactionSequenceNumber(sequenceNumber);
				transaction.setBusinessDay(businessDate);
				transaction.setTransactionStatus(status);
				transaction.setTillID(tillID);
				transaction.setTrainingMode(trainingFlag);

				transaction.setWorkstation(workstation);
				transaction.setCashier(operatingEmployee);
				transaction.setPostProcessingStatus(postProcessingStatus);
				transaction.setReentryMode(reentryMode);

				// set customer info, as needed
				if (!Util.isEmpty(customerInfoType)) {
					CustomerInfoIfc customerInfo = transaction
							.getCustomerInfo();
					if (customerInfo == null) {
						customerInfo = DomainGateway.getFactory()
								.getCustomerInfoInstance();
					}
					int infoType = Integer.parseInt(customerInfoType);
					// if skipped, set variable accordingly
					if (Util.isObjectEqual(customerInfoData,
							CustomerInfoIfc.SKIPPED)) {
						customerInfo.setSkipped(true);
					}
					// disregard if type is none
					if (infoType != CustomerInfoIfc.CUSTOMER_INFO_TYPE_NONE) {
						customerInfo.setCustomerInfoType(infoType);
						customerInfo
								.setCustomerInfo(infoType, customerInfoData);
						transaction.setCustomerInfo(customerInfo);
					}
				}

				if (transaction instanceof Transaction) {
					((Transaction) transaction).buildTransactionID();
				}

				// add the transaction to the vector
				transVector.addElement(transaction);
			}

			// close result set
			rs.close();

			TransactionIfc trans = null;
			// walk through vector and retrieve additional data
			Enumeration<TransactionIfc> e = transVector.elements();
			while (e.hasMoreElements()) {
				trans = e.nextElement();

				// Get the employee information
				trans.setCashier(getEmployee(dataConnection, trans.getCashier()
						.getEmployeeID()));

				// read all additional data
				try {
					readAllTransactionData(dataConnection, trans,
							localeRequestor, retrieveStoreCoupons);
					completedTransVector.add(trans);
				} catch (DataException de) {
					if (e.hasMoreElements() || completedTransVector.size() > 0) {
						logger.error("Encounterd an exception attempting to read multiple transactions; continuing on.");
					} else {
						throw de;
					}
				}
			}
		} catch (DataException de) {
			logger.warn("" + de + "");
			throw de;
		} catch (SQLException se) {
			dataConnection.logSQLException(se, "transaction table");
			throw new DataException(DataException.SQL_ERROR,
					"transaction table", se);
		} catch (Exception e) {
			throw new DataException(DataException.UNKNOWN, "transaction table",
					e);
		}

		if (completedTransVector.isEmpty()) {
			logger.warn("No transactions found");
			throw new DataException(DataException.NO_DATA,
					"No transactions found");
		}

		return (completedTransVector);
	}

	public TransactionIfc readAllTransactionData(
			JdbcDataConnection dataConnection, TransactionIfc transaction,
			LocaleRequestor localeRequestor, boolean retrieveStoreCoupons)
			throws DataException {
		if (logger.isDebugEnabled())
			logger.debug("JdbcReadTransaction.readAllTransactionData()");
		if (perf.isDebugEnabled()) {
			perf.debug("Entering readAllTransactionData(JdbcDataConnection, TransactionIfc, boolean)");
		}
		// add capture customer information:
		transaction.setCaptureCustomer(selectCaptureCustomer(dataConnection,
				transaction, localeRequestor));

		if (transaction instanceof SaleReturnTransactionIfc) {

			MAXSaleReturnTransactionIfc retailTransaction = (MAXSaleReturnTransactionIfc) transaction;
			// do not read the transaction details if a canceled layaway
			// transaction
			if (transaction instanceof LayawayTransactionIfc
					|| transaction instanceof OrderTransactionIfc) {
				if (transaction.getTransactionStatus() != TransactionIfc.STATUS_CANCELED) {
					selectSaleReturnTransaction(dataConnection,
							retailTransaction, localeRequestor,
							retrieveStoreCoupons);
				}
			} else {
				selectSaleReturnTransaction(dataConnection, retailTransaction,
						localeRequestor, retrieveStoreCoupons);
				// Changes Start By Bhanu Priya
				ArrayList<String> pandetails = new ArrayList<String>();
				pandetails = checkForPANDetails(dataConnection, transaction);
				// Iterator<String> itr=pandetails.

				if (!(pandetails.isEmpty())) {

					if (pandetails.get(0) != null && pandetails.get(0) != "") {
						String panno = pandetails.get(0);
						KeyStoreEncryptionManagerIfc encryptionManager = (KeyStoreEncryptionManagerIfc) Gateway
								.getDispatcher().getManager(
										KeyStoreEncryptionManagerIfc.TYPE);
						byte[] clearCard;
						try {
							clearCard = encryptionManager
									.decrypt(Base64.decodeBase64(pandetails
											.get(0).getBytes()));
							EncipheredCardDataIfc cardData = FoundationObjectFactory
									.getFactory()
									.createEncipheredCardDataInstance(
											clearCard, null, null, null);
							retailTransaction.setPanNumber(cardData
									.getMaskedAcctNumber().toString());
						} catch (EncryptionServiceException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}

					}

					else if ((pandetails.get(1) != null && pandetails.get(1) != "")) {
						String uid = pandetails.get(1);
						retailTransaction.setForm60IDNumber(uid.toString());
					}

					else if ((pandetails.get(2) != null && pandetails.get(2) != "")) {
						String ackNum = pandetails.get(2);
						retailTransaction.setPassportNumber(ackNum);
					}

				}

			}
			// Changes End By Bhanu Priya

			if (transaction instanceof LayawayTransactionIfc) {
				// read layaway for transaction
				LayawayTransactionIfc layawayTransaction = (LayawayTransactionIfc) transaction;
				// no payment required if it's a suspended transaction or
				// canceled transaction
				if (transaction.getTransactionStatus() != TransactionIfc.STATUS_SUSPENDED
						&& transaction.getTransactionStatus() != TransactionIfc.STATUS_CANCELED
						&& transaction.getTransactionStatus() != TransactionIfc.STATUS_SUSPENDED_CANCELED
						&& transaction.getTransactionStatus() != TransactionIfc.STATUS_SUSPENDED_RETRIEVED) {
					selectPaymentForLayawayTransaction(dataConnection,
							layawayTransaction);
					// set tender transaction totals for payment
					layawayTransaction.getTransactionTotals()
							.updateTransactionTotalsForPayment(
									layawayTransaction.getPayment()
											.getPaymentAmount());
				}
				// if layaway initiate, we can use the transaction ID to find
				// the layaway; otherwise, we use the payment record. Since a
				// suspended layaway initiate transaction will not have a
				// payment
				// transaction, we need to use the transaction to find the
				// layaway.
				if (transaction.getTransactionStatus() != TransactionIfc.STATUS_CANCELED) {
					if (transaction.getTransactionType() == TransactionIfc.TYPE_LAYAWAY_INITIATE) {
						selectLayawayForTransaction(dataConnection,
								layawayTransaction, localeRequestor);
					} else {
						selectLayawayForPayment(dataConnection,
								layawayTransaction, localeRequestor);
					}
					// get payment history info for layaway
					LayawayIfc layaway = layawayTransaction.getLayaway();
					layaway = selectLayawayPaymentHistoryInfo(dataConnection,
							layaway);
					layawayTransaction.setLayaway(layaway);

					layawayTransaction.getLayaway().setCustomer(
							layawayTransaction.getCustomer());
				}
			} else if (transaction instanceof OrderTransactionIfc
					&& transaction.getTransactionStatus() != TransactionIfc.STATUS_CANCELED) {
				// read in payment for order transaction if not suspended
				OrderTransactionIfc orderTransaction = (OrderTransactionIfc) transaction;
				// no payment required if it's a suspended transaction
				if (transaction.getTransactionStatus() != TransactionIfc.STATUS_SUSPENDED
						&& transaction.getTransactionStatus() != TransactionIfc.STATUS_SUSPENDED_CANCELED
						&& transaction.getTransactionStatus() != TransactionIfc.STATUS_SUSPENDED_RETRIEVED) {
					selectPaymentForOrderTransaction(dataConnection,
							orderTransaction);
					readOrderInfo(dataConnection, orderTransaction);
				}
				// get payment history info for order
				orderTransaction = selectOrderPaymentHistoryInfo(
						dataConnection, orderTransaction);
			}

			// Make sure we update all the necessary information
			retailTransaction.getTransactionTotals().updateTransactionTotals(
					retailTransaction.getLineItems(),
					retailTransaction.getTransactionDiscounts(),
					retailTransaction.getTransactionTax());
		} else if (transaction instanceof VoidTransactionIfc) {
			VoidTransactionIfc voidTransaction = (VoidTransactionIfc) transaction;
			selectVoidTransaction(dataConnection, voidTransaction,
					localeRequestor);
			// retrieve the reason code text
		} else if (transaction instanceof PaymentTransactionIfc) {
			// if transaction is canceled , there is no payment being saved in
			// the database
			if (transaction.getTransactionStatus() != Transaction.STATUS_CANCELED) {

				PaymentTransactionIfc retailTransaction = (PaymentTransactionIfc) transaction;
				selectPaymentTransaction(dataConnection, retailTransaction,
						localeRequestor);

				/*
				 * Make sure we update transaction totals for payment
				 * transaction
				 */
				retailTransaction.getTransactionTotals()
						.updateTransactionTotalsForPayment(
								retailTransaction.getPaymentAmount());

				if (transaction instanceof LayawayPaymentTransactionIfc) {
					selectLayawayForPaymentTransaction(dataConnection,
							transaction, localeRequestor);

					// read layaway for transaction
					LayawayPaymentTransactionIfc layawayTransaction = (LayawayPaymentTransactionIfc) transaction;
					// get payment history info for layaway
					LayawayIfc layaway = layawayTransaction.getLayaway();
					layaway = selectLayawayPaymentHistoryInfo(dataConnection,
							layaway);
					layawayTransaction.setLayaway(layaway);
					layawayTransaction.getLayaway().setCustomer(
							layawayTransaction.getCustomer());
				}
			}
		}

		else if (transaction instanceof BillPayTransactionIfc) {
			selectBillPayTransaction(dataConnection,
					(BillPayTransactionIfc) transaction);
		} else if (transaction instanceof InstantCreditTransactionIfc) {
			InstantCreditTransactionIfc icTrans = (InstantCreditTransactionIfc) transaction;
			selectInstantCreditTransaction(dataConnection, icTrans);
		} else if (transaction instanceof TillAdjustmentTransactionIfc) {
			TillAdjustmentTransactionIfc tillAdjustmentTransaction = (TillAdjustmentTransactionIfc) transaction;
			selectTillAdjustmentTransaction(dataConnection,
					tillAdjustmentTransaction);
		} else if (transaction instanceof NoSaleTransactionIfc) {
			selectNoSaleTransaction(dataConnection,
					(NoSaleTransactionIfc) transaction, localeRequestor);
		} else if (transaction instanceof StoreOpenCloseTransactionIfc) {
			selectStoreOpenCloseTransaction(dataConnection,
					(StoreOpenCloseTransactionIfc) transaction);
		} else if (transaction instanceof RegisterOpenCloseTransactionIfc) {
			selectRegisterOpenCloseTransaction(dataConnection,
					(RegisterOpenCloseTransactionIfc) transaction);
		} else if (transaction instanceof TillOpenCloseTransactionIfc) {
			selectTillOpenCloseTransaction(dataConnection,
					(TillOpenCloseTransactionIfc) transaction);
		} else if (transaction instanceof BankDepositTransactionIfc) {
			selectBankDepositTransaction(dataConnection,
					(BankDepositTransactionIfc) transaction);
		} else if (transaction instanceof RedeemTransactionIfc) {
			if (transaction.getTransactionStatus() != TransactionIfc.STATUS_CANCELED) {
				selectRedeemTransaction(dataConnection,
						(RedeemTransactionIfc) transaction);
			}
		} else if (transaction instanceof StatusChangeTransactionIfc) {
			selectStatusChangeTransaction(dataConnection,
					(StatusChangeTransactionIfc) transaction);
		}

		else {
			/*
			 * The listed transaction types below require no additional
			 * information from the database
			 */
			int transactionType = transaction.getTransactionType();
			if (transactionType != TransactionIfc.TYPE_NO_SALE
					&& transactionType != TransactionIfc.TYPE_OPEN_TILL
					&& transactionType != TransactionIfc.TYPE_CLOSE_TILL
					&& transactionType != TransactionIfc.TYPE_SUSPEND_TILL
					&& transactionType != TransactionIfc.TYPE_RESUME_TILL) {
				logger.error("JdbcReadTransaction: Unsupported transaction type "
						+ Integer.toString(transactionType)
						+ " ("
						+ transaction.getClass().getName() + ")");
			}
		}

		if (logger.isDebugEnabled())
			logger.debug("JdbcReadTransaction.readAllTransactionData()");
		if (perf.isDebugEnabled()) {
			perf.debug("Exiting readAllTransactionData(JdbcDataConnection, TransactionIfc, boolean)");
		}
		return (transaction);
	}

	/* Nadia : Changes for price of item not coming on return ends */

	// Changes for Rev 1.7 : Starts
	public void getItemGroups(PLUItemIfc pluItem,
			JdbcDataConnection dataConnection, LocaleRequestor localeRequestor)
			throws DataException {
		if (logger.isDebugEnabled())
			logger.debug("JdbcReadlocalizedDescription.readLocalizedItemDescriptions()");

		SQLSelectStatement sql = new SQLSelectStatement();
		sql.setDistinctFlag(true);
		// add tables
		sql.addTable(TABLE_GROUP_ITEM_LIST, ALIAS_TABLE_GROUP_ITEM_LIST);
		sql.addColumn(FIELD_ITEM_GROUP_ID);

		List<SQLParameterIfc> orQualifiers = new ArrayList<SQLParameterIfc>(2);
		orQualifiers.add(new SQLParameterValue(FIELD_ITEM_ID, pluItem
				.getItemID()));
		orQualifiers.add(new SQLParameterValue(FIELD_ITEM_ID, MAXUtils
				.getDepartmentID(pluItem.getDepartmentID())));
		orQualifiers.add(new SQLParameterValue(FIELD_ITEM_ID, MAXUtils
				.getMerchandiseClass(pluItem.getItemClassification()
						.getMerchandiseHierarchyGroup())));
		orQualifiers.add(new SQLParameterValue(FIELD_ITEM_ID, MAXUtils
				.getBrand(pluItem)));

		sql.addOrQualifiers(orQualifiers);
		ResultSet rs = null;
		StringBuffer itemGroup = new StringBuffer();
		try {
			// execute sql
			String sqlString = sql.getSQLString();
			dataConnection.execute(sqlString, sql.getParameterValues());
			rs = (ResultSet) dataConnection.getResult();

			int index = 1;
			while (rs.next()) {
				String itmgrp = getSafeString(rs, index);
				itemGroup.append(makeSafeString(itmgrp));
				itemGroup.append(",");
			}

			if (itemGroup.length() < 1) {
				while (rs.next()) {
					String itmgrp = getSafeString(rs, index);
					itemGroup.append(makeSafeString(itmgrp));
					itemGroup.append(",");
				}
				if (itemGroup.length() < 1) {
					itemGroup.append("'NO'");
				}
			} else {
				// for deleting last char from string
				itemGroup.deleteCharAt(itemGroup.length() - 1);
			}

		} catch (DataException e) {
			if (itemGroup.length() < 1) {
				itemGroup.append("");
			} else {
				// for deleting last char from string
				itemGroup.deleteCharAt(itemGroup.length() - 1);
			}
			e.printStackTrace();
		} catch (SQLException e) {
			if (itemGroup.length() < 1) {
				itemGroup.append("");
			} else {
				// for deleting last char from string
				itemGroup.deleteCharAt(itemGroup.length() - 1);
			}
		} finally {
			DBUtils.getInstance().closeResultSet(rs);
		}
		((MAXPLUItemIfc) pluItem).setItemGroups(itemGroup.toString());
	}
	// Changes for Rev 1.7 : Ends
	/*
	 * public MAXGSTINValidationResponseIfc
	 * getGstinInvoiceDetails(JdbcDataConnection connection,String storeID,String
	 * WorkstationID, String BusinessDayString, String TransactionSequenceNumber)
	 * throws DataException {
	 * 
	 * MAXGSTINValidationResponseIfc response = new MAXGSTINValidationResponse();
	 * ResultSet rs = null; try {
	 * 
	 * 
	 * String query = "select * from GSTIN_E_INVOICE_DETAILS where ID_STR_RT= '"+
	 * storeID +"' and ID_WS= '"+WorkstationID+"' and DC_DY_BSN='"+
	 * BusinessDayString +"'and AI_TRN='"+TransactionSequenceNumber+"'";///not
	 * correct for now its ok better use prepared statemnt
	 * connection.execute(query); rs = (ResultSet)connection.getResult(); String
	 * gstName= rs.getString(FIELD_CUST_GSTIN ); String
	 * gstName1=rs.getString(FIELD_INVOICE_TO_GSTIN);
	 * 
	 * } catch (Exception exception) { logger.error("NOT GETTING INVOICE DETAILS.");
	 * logger.error("" + exception + "");
	 * System.out.println(exception+" INVOICE DETAILS"); } return response;
	 * 
	 * } public TransactionIfc readTransactionsByTransactionSequenceNumber(
	 * JdbcDataConnection connection, TransactionIfc transaction,String
	 * storeID,String WorkstationID, String BusinessDayString, String
	 * TransactionSequenceNumber) throws DataException { TransactionIfc response=new
	 * Transaction(); ResultSet rs=null; try { String query =
	 * "select * from GSTIN_E_INVOICE_DETAILS where ID_STR_RT= '"+ storeID
	 * +"' and ID_WS= '"+WorkstationID+"' and DC_DY_BSN='"+ BusinessDayString
	 * +"'and AI_TRN='"+TransactionSequenceNumber+"'";///not correct for now its ok
	 * better use prepared statemnt connection.execute(query);
	 * System.out.println(query +"data"); rs = (ResultSet)connection.getResult();
	 * String gstName= rs.getString(FIELD_CUST_GSTIN ); String
	 * gstName1=rs.getString(FIELD_INVOICE_TO_GSTIN);
	 * 
	 * } catch(Exception exception) { logger.error("NOT GETTING INVOICE DETAILS.");
	 * logger.error("" + exception + "");
	 * 
	 * } return response; }
	 */
}
