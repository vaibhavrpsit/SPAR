/********************************************************************************
 *   
 *	Copyright (c) 2016 - 2017 MAX Hypermarket, Inc    All Rights Reserved.
 *  Rev 2.7     27 JULY,2020        Karni Singh			Changes made to save the tax category at the line item level. 
 *  Rev 2.6     26 May,2018         Atul Shukla			Changes made for employee discount CR, saving the employee ID and company name 
 *  Rev 2.5     08 Aug,2017         Hitesh Dua			in case of multiple coupon redemption, one coupon no is saving in db for all coupon.  
 *  Rev 2.4     28 June,2017        Nayya Gupta			Defect fixes of Mcoupon
 *  Rev 2.3     June 27,2017        Nayya Gupta		Incorrect tax coming on reprint receipt
 *  Rev 2.2     Jun 17, 2017	    Jyoti Yadav 	ClassCastException
 *  Rev 2.1     May 04, 2017	    Kritica Agarwal GST Changes
 *	Rev 2.0.1	May 09, 2017		Mansi Goel		Changes to resolve missing transaction issue in production
 *  Rev 2.0     Mar 28, 2017        Nitika Arora    Changes for fixing the rtlog issue using Gift Cert(Update the MRP in db)
 *  Rev 1.9     Mar 16, 2017        Nitika Arora    Changes for saving the transaction in database when fixed price type promotion is applied.
 *	Rev 1.8		Feb 23, 2016		Ashish Yadav	Changes for added new col for gift card inorder to make same for 12 version
 *  Rev 1.7		Feb 20,2017			Nadia Arora		Changes for price of item not coming on return
 *  Rev 1.6     Feb 09,2017         Nitika Arora    Changes for Id 233
 *	Rev 1.5		Jan 12, 2017		Nitesh Kumar	Changes for Return without receipt
 *	Rev 1.4		Dec 20, 2016		Mansi Goel		Changes for Gift Card FES
 *	Rev	1.3 	Dec 05, 2016		Ashish Yadav	Changes for Employee Discount FES
 *	Rev	1.2 	Nov 07, 2016		Ashish Yadav	Changes for Home Delivery Send FES
 *	Rev	1.1 	Nov 07, 2016		Mansi Goel		Changes for Discount Rule FES
 *	Rev 1.0		Aug 26,2016			Nitesh Kumar	Changes for code merging
 *
 ********************************************************************************/

package max.retail.stores.domain.arts;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Map;
import java.util.Vector;

import max.retail.stores.domain.discount.MAXAdvancedPricingRuleIfc;
import max.retail.stores.domain.discount.MAXItemDiscountStrategyIfc;
import max.retail.stores.domain.discountCoupon.MAXDiscountCoupon;
import max.retail.stores.domain.lineitem.MAXItemTaxIfc;
import max.retail.stores.domain.lineitem.MAXLineItemTaxBreakUpDetailIfc;
import max.retail.stores.domain.lineitem.MAXSaleReturnLineItem;
import max.retail.stores.domain.lineitem.MAXSaleReturnLineItemIfc;
import max.retail.stores.domain.stock.MAXGiftCardPLUItem;
import max.retail.stores.domain.stock.MAXPLUItem;
import max.retail.stores.domain.stock.MAXPLUItemIfc;
import max.retail.stores.domain.transaction.MAXLayawayTransaction;
import max.retail.stores.domain.transaction.MAXSaleReturnTransaction;
import max.retail.stores.domain.transaction.MAXSaleReturnTransactionIfc;
import max.retail.stores.domain.utility.MAXCodeConstantsIfc;
import max.retail.stores.domain.utility.MAXGiftCardIfc;
import max.retail.stores.persistence.utility.MAXARTSDatabaseIfc;
import oracle.retail.stores.common.sql.SQLInsertStatement;
import oracle.retail.stores.common.sql.SQLUpdateStatement;
import oracle.retail.stores.domain.DomainGateway;
import oracle.retail.stores.domain.arts.JdbcSaveRetailTransactionLineItems;
import oracle.retail.stores.domain.discount.DiscountRuleConstantsIfc;
import oracle.retail.stores.domain.discount.DiscountRuleIfc;
import oracle.retail.stores.domain.discount.ItemDiscountStrategyIfc;
import oracle.retail.stores.domain.discount.PromotionLineItemIfc;
import oracle.retail.stores.domain.discount.TransactionDiscountByAmountStrategy;
import oracle.retail.stores.domain.discount.TransactionDiscountByPercentageStrategy;
import oracle.retail.stores.domain.discount.TransactionDiscountStrategyIfc;
import oracle.retail.stores.domain.lineitem.AbstractTransactionLineItemIfc;
import oracle.retail.stores.domain.lineitem.KitComponentLineItemIfc;
import oracle.retail.stores.domain.lineitem.OrderLineItemIfc;
import oracle.retail.stores.domain.lineitem.ReturnItemIfc;
import oracle.retail.stores.domain.lineitem.SaleReturnLineItemIfc;
import oracle.retail.stores.domain.stock.GiftCardPLUItem;
import oracle.retail.stores.domain.stock.GiftCardPLUItemIfc;
import oracle.retail.stores.domain.stock.GiftCertificateItemIfc;
import oracle.retail.stores.domain.stock.ItemKitConstantsIfc;
import oracle.retail.stores.domain.stock.UnknownItemIfc;
import oracle.retail.stores.domain.transaction.LayawayTransactionIfc;
import oracle.retail.stores.domain.transaction.OrderTransactionIfc;
import oracle.retail.stores.domain.transaction.PaymentTransactionIfc;
import oracle.retail.stores.domain.transaction.RetailTransactionIfc;
import oracle.retail.stores.domain.transaction.SaleReturnTransactionIfc;
import oracle.retail.stores.domain.transaction.TenderableTransactionIfc;
import oracle.retail.stores.domain.transaction.TransactionConstantsIfc;
import oracle.retail.stores.domain.transaction.TransactionIfc;
import oracle.retail.stores.domain.utility.SecurityOverrideIfc;
import oracle.retail.stores.foundation.manager.data.DataException;
import oracle.retail.stores.foundation.manager.data.JdbcDataConnection;
import oracle.retail.stores.foundation.manager.device.EncipheredCardDataIfc;

import org.apache.log4j.Logger;

public class MAXJdbcSaveRetailTransactionLineItems extends JdbcSaveRetailTransactionLineItems implements
		MAXARTSDatabaseIfc {

	/**
	 * 
	 */
	private static final long serialVersionUID = 2281938873660236693L;
	/**
	 * 
	 */
	// private static final long serialVersionUID = 1L;
	private static Logger logger = Logger
			.getLogger(max.retail.stores.domain.arts.MAXJdbcSaveRetailTransactionLineItems.class);

	public void insertGiftCard(JdbcDataConnection dataConnection, RetailTransactionIfc transaction,
			SaleReturnLineItemIfc lineItem) throws DataException {
		// Get gift card
		MAXGiftCardIfc giftCard = (MAXGiftCardIfc) ((GiftCardPLUItemIfc) (lineItem.getPLUItem())).getGiftCard();
		SQLInsertStatement sql = new SQLInsertStatement();

		// Table
		sql.setTable(TABLE_GIFT_CARD);

		// Fields
		sql.addColumn(FIELD_RETAIL_STORE_ID, getStoreID(transaction));
		sql.addColumn(FIELD_WORKSTATION_ID, getWorkstationID(transaction));
		sql.addColumn(FIELD_TRANSACTION_SEQUENCE_NUMBER, getTransactionSequenceNumber(transaction));
		sql.addColumn(FIELD_BUSINESS_DAY_DATE, getBusinessDayString(transaction));
		sql.addColumn(FIELD_RETAIL_TRANSACTION_LINE_ITEM_SEQUENCE_NUMBER, getSequenceNumber(lineItem.getLineNumber()));
		// Changes for Rev 1.4 : Starts
		EncipheredCardDataIfc cardData = giftCard.getEncipheredCardData();
		if (cardData != null) {
			sql.addColumn(FIELD_GIFT_CARD_SERIAL_NUMBER, inQuotes(cardData.getEncryptedAcctNumber()));
			sql.addColumn(FIELD_MASKED_GIFT_CARD_SERIAL_NUMBER, inQuotes(cardData.getMaskedAcctNumber()));
			// Changes starts for Rev 1.8 (Ashish)
			sql.addColumn(FIELD_GIFT_CARD_SERIAL_NUMBER_OLD, inQuotes(cardData.getEncryptedAcctNumber()));
			// Changes ends for Rev 1.8 (Ashish)
		}
		// Changes for Rev 1.4 : Ends
		sql.addColumn(FIELD_GIFT_CARD_ACTIVATION_ADJUDICATION_CODE, makeSafeString(giftCard.getApprovalCode()));
		// }
		sql.addColumn(FIELD_GIFT_CARD_ENTRY_METHOD, inQuotes(giftCard.getEntryMethod().toString()));
		sql.addColumn(FIELD_GIFT_CARD_REQUEST_TYPE, inQuotes(String.valueOf(giftCard.getRequestType())));
		sql.addColumn(FIELD_GIFT_CARD_CURRENT_BALANCE, giftCard.getCurrentBalance().getStringValue());

		sql.addColumn(FIELD_GIFT_CARD_INITIAL_BALANCE, giftCard.getInitialBalance().getStringValue());
		// +I18N
		sql.addColumn(FIELD_CURRENCY_ID, lineItem.getExtendedSellingPrice().getType().getCurrencyId());
		// -I18N
		if (giftCard.getSettlementData() == null)
			sql.addColumn(FIELD_TENDER_AUTHORIZATION_SETTLEMENT_DATA, makeSafeString(giftCard.getSettlementData()));
		sql.addColumn(FIELD_TENDER_AUTHORIZATION_DATE_TIME, getAuthorizationDateTime(giftCard.getAuthorizedDateTime()));

		try {

			MAXGiftCardIfc gc = (MAXGiftCardIfc) ((MAXGiftCardPLUItem) lineItem.getPLUItem()).getGiftCard();

			sql.addColumn(FIELD_GIFT_CARD_AUTHORIZATION_CODE, makeSafeString(((GiftCardPLUItem) lineItem.getPLUItem())
					.getGiftCard().getApprovalCode()));

			sql.addColumn(FIELD_GIFT_CARD_INVOICE_ID, makeSafeString((gc).getQcInvoiceNumber()));

			sql.addColumn(FIELD_GIFT_CARD_TRANS_ID, makeSafeString((gc).getQcTransactionId()));

			sql.addColumn(FIELD_GIFT_CARD_BATCH_ID, makeSafeString((gc).getQcBatchNumber()));

			sql.addColumn(FIELD_GIFT_CARD_EXP_DATE, getAuthorizationDateTime(((GiftCardPLUItem) lineItem.getPLUItem())
					.getGiftCard().getExpirationDate()));

			sql.addColumn(FIELD_GIFT_CARD_TYPE, makeSafeString(((GiftCardPLUItem) lineItem.getPLUItem()).getGiftCard()
					.getSettlementData()));
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

	public void insertRetailPriceModifier(JdbcDataConnection dataConnection, RetailTransactionIfc transaction,
			SaleReturnLineItemIfc lineItem, int sequenceNumber, ItemDiscountStrategyIfc discountLineItem)
			throws DataException

	{
		SQLInsertStatement sql = new SQLInsertStatement();

		// Table
		sql.setTable(TABLE_RETAIL_PRICE_MODIFIER);

		// Get the line and transaction number.
		String lineNumber = getLineItemSequenceNumber(lineItem);
		String tranNumber = getTransactionSequenceNumber(transaction);
		((MAXSaleReturnTransactionIfc) transaction).setItemLevelDiscount(true);
		// Fields
		sql.addColumn(FIELD_RETAIL_STORE_ID, getStoreID(transaction));
		sql.addColumn(FIELD_WORKSTATION_ID, getWorkstationID(transaction));
		sql.addColumn(FIELD_TRANSACTION_SEQUENCE_NUMBER, tranNumber);
		sql.addColumn(FIELD_BUSINESS_DAY_DATE, getBusinessDayString(transaction));
		sql.addColumn(FIELD_RETAIL_TRANSACTION_LINE_ITEM_SEQUENCE_NUMBER, lineNumber);
		sql.addColumn(FIELD_RETAIL_PRICE_MODIFIER_SEQUENCE_NUMBER, getSequenceNumber(sequenceNumber));
		sql.addColumn(FIELD_RECORD_CREATION_TIMESTAMP, getSQLCurrentTimestampFunction());
		sql.addColumn(FIELD_RECORD_LAST_MODIFIED_TIMESTAMP, getSQLCurrentTimestampFunction());

		if (discountLineItem != null) { // Item discount
			String promotionId = String.valueOf(discountLineItem.getPromotionId());
			sql.addColumn(FIELD_RETAIL_PRICE_MODIFIER_DERIVATION_RULE_ID, getDiscountRuleID(discountLineItem));
			// Changes start for Rev 1.3 (Ashish : Employee Discount (Check
			// whether insatce is of ItemDiscountByAmountStrategy)
			//Changes for Rev 2.0.1 : Starts
			//if (!(discountLineItem instanceof ItemDiscountByAmountStrategy || discountLineItem instanceof ItemDiscountByFixedPriceStrategy)) {
				// Changes ends for Rev 1.3 (Ashish : Employee Discount)
				// Changes for Rev 1.1 : Starts
			/*Change for Rev 2.2 : Start*/
			/*Change for Rev 2.3 commented the below bcoz of this if condition discount amt was not getting saved in co_mdf table: starts*/
			//if(discountLineItem instanceof MAXItemDiscountStrategyIfc){
				/*Change for Rev 2.2 : End*/
				if (discountLineItem instanceof MAXItemDiscountStrategyIfc &&
						((MAXItemDiscountStrategyIfc)discountLineItem).getCapillaryCoupon()!= null 
						&& !((MAXItemDiscountStrategyIfc)discountLineItem).getCapillaryCoupon().isEmpty()
						&& ((MAXItemDiscountStrategyIfc)discountLineItem).getCapillaryCoupon()
								.get(MAXCodeConstantsIfc.CAPILLARY_COUPON_DISCOUNT_TYPE).equals("ABS")) {
					// Changes for Rev 1.1 : Ends
					//Changes for Rev 2.0.1 : Ends
					try {
						// Changes for Return with M-Coupon @Purushotham Reddy
						/*BigDecimal itemQty = lineItem.getItemQuantityDecimal().abs();
						BigDecimal itemDisc= new BigDecimal(getPriceModifierAmount(discountLineItem));
						BigDecimal discValue= itemDisc.divide(itemQty, 4, BigDecimal.ROUND_HALF_UP);*/
						
						long as = lineItem.getItemQuantityDecimal().abs().longValue();
						if(as==0){
							as = 1;
						}
						double f1 = (double) as;
						Double l = Double.valueOf(getPriceModifierAmount(discountLineItem));
						double l1 = l.floatValue();
						f1 = l1 / f1;
						double roundOff = Math.round(f1 * 100.0) / 100.0;
						
						sql.addColumn(FIELD_RETAIL_PRICE_MODIFIER_AMOUNT, String.valueOf(roundOff));
					} catch (Exception e) {
						logger.warn(e);
					}
				}
				/*Change for Rev 2.2 : Start*/
			//}/*Change for Rev 2.3 : ends*/
			/*Change for Rev 2.2 : End*/
			else {
					sql.addColumn(FIELD_RETAIL_PRICE_MODIFIER_AMOUNT,
							String.valueOf(Math.round(Double.valueOf(getPriceModifierAmount(discountLineItem)) * 100.0) / 100.0));
				}
				// Changes start for Rev 1.3 (Ashish : Employee Discount)
				//Changes for Rev 2.0.1 : Starts
			//}
			//Changes for Id 233 starts
			//else
				//sql.addColumn(FIELD_RETAIL_PRICE_MODIFIER_AMOUNT, getPriceModifierAmount(discountLineItem));
			//Changes for Id 233 ends
			// Changes ends for Rev 1.3 (Ashish : Employee Discount)
			if (promotionId != null && !promotionId.equals("0")){
				sql.addColumn(FIELD_RETAIL_PRICE_MODIFIER_REASON_CODE, 7);
			}
						
			else if (!((MAXItemDiscountStrategyIfc) discountLineItem).getCapillaryCoupon().isEmpty()) {
				// Changes for Rev 2.4
				sql.addColumn(FIELD_RETAIL_PRICE_MODIFIER_REASON_CODE, 5170);
			}
			//Changes for Rev 2.0.1 : Ends
			// Changes for Rev 1.1 : Starts
			/*else if (!((MAXItemDiscountByPercentageIfc) discountLineItem).getCapillaryCoupon().isEmpty()) {
				// Changes for Rev 1.1 : Ends
				sql.addColumn(FIELD_RETAIL_PRICE_MODIFIER_REASON_CODE, getPriceModifierReasonCode(discountLineItem));
			}*/ else
				/*sql.addColumn(FIELD_RETAIL_PRICE_MODIFIER_REASON_CODE, 5);*/ // Promo reason code issue - Karni
				sql.addColumn(FIELD_RETAIL_PRICE_MODIFIER_REASON_CODE, getPriceModifierReasonCode(discountLineItem)); 
			sql.addColumn(FIELD_RETAIL_PRICE_MODIFIER_PERCENT, getPriceModifierPercent(discountLineItem));
			if (discountLineItem.getReasonCode() == 22)
				sql.addColumn(FIELD_PRICE_DERIVATION_RULE_METHOD_CODE, 4);
			else
				sql.addColumn(FIELD_PRICE_DERIVATION_RULE_METHOD_CODE, getPriceModifierMethodCode(discountLineItem));
			sql.addColumn(FIELD_PRICE_DERIVATION_RULE_ASSIGNMENT_BASIS_CODE,
					getPriceModifierAssignmentBasis(discountLineItem));
			sql.addColumn(FIELD_RETAIL_PRICE_MODIFIER_DISCOUNT_EMPLOYEE_ID,
					makeSafeString(discountLineItem.getDiscountEmployeeID()));
			sql.addColumn(FIELD_RETAIL_PRICE_MODIFIER_DAMAGE_DISCOUNT,
					getPriceModifierDamageDiscountFlag(discountLineItem));
			sql.addColumn(FIELD_PCD_INCLUDED_IN_BEST_DEAL, getIncludedInBestDealFlag(discountLineItem));
			sql.addColumn(FIELD_ADVANCED_PRICING_RULE, getAdvancedPricingRuleFlag(discountLineItem));

			try {
				sql.addColumn(FIELD_RETAIL_PRICE_MODIFIER_DISCOUNT_REFERENCE_ID,
						getPriceModifierReferenceID(transaction, discountLineItem,false));
			} catch (Exception e) {
				logger.error(e.getMessage());
			}

			sql.addColumn(FIELD_RETAIL_PRICE_MODIFIER_DISCOUNT_REFERENCE_ID_TYPE_CODE,
					getPriceModifierReferenceIDTypeCode(discountLineItem));
			sql.addColumn(FIELD_RETAIL_PRICE_MODIFIER_DISCOUNT_TYPE_CODE, discountLineItem.getTypeCode());
			sql.addColumn(FIELD_RETAIL_PRICE_MODIFIER_STOCK_LEDGER_ACCOUNTING_DISPOSITION_CODE,
					inQuotes(discountLineItem.getAccountingMethod()));

			sql.addColumn(FIELD_PROMOTION_ID, ((promotionId != null) ? promotionId : "0"));
			sql.addColumn(FIELD_PROMOTION_COMPONENT_ID, discountLineItem.getPromotionComponentId());
			sql.addColumn(FIELD_PROMOTION_COMPONENT_DETAIL_ID, discountLineItem.getPromotionComponentDetailId());
			// added by Izhar
			if (((MAXSaleReturnLineItemIfc) lineItem).getDiscountAmountforRTLOG() != null)
				sql.addColumn("RTLOG_AMT", ((MAXSaleReturnLineItemIfc) lineItem).getDiscountAmountforRTLOG().abs()
						.toString());
			else
				sql.addColumn("RTLOG_AMT", getPriceModifierAmount(discountLineItem));

			if (((MAXSaleReturnLineItemIfc) lineItem).getAmountVFP() != null)
				sql.addColumn("VFP_AMT", ((MAXSaleReturnLineItemIfc) lineItem).getAmountVFP().toString());
			else
				sql.addColumn("VFP_AMT", "0");

			if (((MAXSaleReturnLineItemIfc) lineItem).getVendorID() != null)
				sql.addColumn("VENDOR_ID", ((MAXSaleReturnLineItemIfc) lineItem).getVendorID());
			else
				sql.addColumn("VENDOR_ID", "0");
			// end
		} else { // Price Override
			sql.addColumn(FIELD_RETAIL_PRICE_MODIFIER_DERIVATION_RULE_ID, "0");
			sql.addColumn(FIELD_RETAIL_PRICE_MODIFIER_REASON_CODE, getPriceModifierReasonCode(lineItem));
			sql.addColumn(FIELD_RETAIL_PRICE_MODIFIER_AMOUNT, getPriceModifierAmount(lineItem));
			// added by Izhar
			sql.addColumn("RTLOG_AMT", getPriceModifierAmount(lineItem));
			sql.addColumn("VFP_AMT", "0");
			sql.addColumn("VENDOR_ID", "0");
			// end
			// if security override data exists, use it
			SecurityOverrideIfc priceOverrideAuthorization = lineItem.getItemPrice().getPriceOverrideAuthorization();
			if (priceOverrideAuthorization != null) {
				sql.addColumn(FIELD_RETAIL_PRICE_MODIFIER_OVERRIDE_EMPLOYEE_ID,
						makeSafeString(priceOverrideAuthorization.getAuthorizingEmployeeID()));
				sql.addColumn(FIELD_RETAIL_PRICE_MODIFIER_OVERRIDE_ENTRY_METHOD_CODE, priceOverrideAuthorization
						.getEntryMethod().toString());
			}
		}
		try {
			dataConnection.execute(sql.getSQLString());
		} catch (DataException de) {
			logger.error("" + de + "");
			throw de;
		} catch (Exception e) {
			throw new DataException(DataException.UNKNOWN, "insertRetailPriceModifier", e);
		}
	}

	public void updateRetailPriceModifier(JdbcDataConnection dataConnection, RetailTransactionIfc transaction,
			SaleReturnLineItemIfc lineItem, int sequenceNumber, ItemDiscountStrategyIfc discountLineItem)
			throws DataException {
		SQLUpdateStatement sql = new SQLUpdateStatement();

		// Table
		sql.setTable(TABLE_RETAIL_PRICE_MODIFIER);

		// Fields
		if (discountLineItem != null) { // Item discount
			String promotionId = String.valueOf(discountLineItem.getPromotionId());
			sql.addColumn(FIELD_RETAIL_PRICE_MODIFIER_DERIVATION_RULE_ID, getDiscountRuleID(discountLineItem));
			// modidied to send reason code as 7 for all discount rules and to
			// get ORCAP in rtlogs
			if (promotionId == null)
				sql.addColumn(FIELD_RETAIL_PRICE_MODIFIER_REASON_CODE, getPriceModifierReasonCode(discountLineItem));
			else
				sql.addColumn(FIELD_RETAIL_PRICE_MODIFIER_REASON_CODE, 7);
			// end
			sql.addColumn(FIELD_RETAIL_PRICE_MODIFIER_PERCENT, getPriceModifierPercent(discountLineItem));
			sql.addColumn(FIELD_RETAIL_PRICE_MODIFIER_AMOUNT, getPriceModifierAmount(discountLineItem));
			sql.addColumn(FIELD_PRICE_DERIVATION_RULE_METHOD_CODE, getPriceModifierMethodCode(discountLineItem));
			sql.addColumn(FIELD_PRICE_DERIVATION_RULE_ASSIGNMENT_BASIS_CODE,
					getPriceModifierAssignmentBasis(discountLineItem));
			sql.addColumn(FIELD_RETAIL_PRICE_MODIFIER_DISCOUNT_EMPLOYEE_ID,
					makeSafeString(discountLineItem.getDiscountEmployeeID()));
			sql.addColumn(FIELD_RETAIL_PRICE_MODIFIER_DAMAGE_DISCOUNT,
					getPriceModifierDamageDiscountFlag(discountLineItem));
			sql.addColumn(FIELD_PCD_INCLUDED_IN_BEST_DEAL, getIncludedInBestDealFlag(discountLineItem));
			sql.addColumn(FIELD_ADVANCED_PRICING_RULE, getAdvancedPricingRuleFlag(discountLineItem));

			try {
				sql.addColumn(FIELD_RETAIL_PRICE_MODIFIER_DISCOUNT_REFERENCE_ID,
						getPriceModifierReferenceID(transaction, discountLineItem,false));
			} catch (Exception e) {
				logger.error(e.getMessage());
			}

			sql.addColumn(FIELD_RETAIL_PRICE_MODIFIER_DISCOUNT_REFERENCE_ID_TYPE_CODE,
					getPriceModifierReferenceIDTypeCode(discountLineItem));
			sql.addColumn(FIELD_RETAIL_PRICE_MODIFIER_DISCOUNT_TYPE_CODE, discountLineItem.getTypeCode());
			sql.addColumn(FIELD_RETAIL_PRICE_MODIFIER_STOCK_LEDGER_ACCOUNTING_DISPOSITION_CODE,
					inQuotes(discountLineItem.getAccountingMethod()));
			// String promotionId = discountLineItem.getPromotionId();
			sql.addColumn(FIELD_PROMOTION_ID, ((promotionId != null) ? promotionId : "0"));
			sql.addColumn(FIELD_PROMOTION_COMPONENT_ID, discountLineItem.getPromotionComponentId());
			sql.addColumn(FIELD_PROMOTION_COMPONENT_DETAIL_ID, discountLineItem.getPromotionComponentDetailId());
			// added by Izhar
			sql.addColumn("RTLOG_AMT", getPriceModifierAmount(discountLineItem));
			sql.addColumn("VFP_AMT", "0");
			sql.addColumn("VENDOR_ID", "0");
			// end
		} else { // Price Override
			sql.addColumn(FIELD_RETAIL_PRICE_MODIFIER_DERIVATION_RULE_ID, "0");
			sql.addColumn(FIELD_RETAIL_PRICE_MODIFIER_REASON_CODE, getPriceModifierReasonCode(lineItem));
			sql.addColumn(FIELD_RETAIL_PRICE_MODIFIER_AMOUNT, getPriceModifierAmount(lineItem));
			// added by Izhar
			sql.addColumn("RTLOG_AMT", getPriceModifierAmount(lineItem));
			sql.addColumn("VFP_AMT", "0");
			sql.addColumn("VENDOR_ID", "0");
			// end
			// if security override data exists, use it
			SecurityOverrideIfc priceOverrideAuthorization = lineItem.getItemPrice().getPriceOverrideAuthorization();
			if (priceOverrideAuthorization != null) {
				sql.addColumn(FIELD_RETAIL_PRICE_MODIFIER_OVERRIDE_EMPLOYEE_ID,
						makeSafeString(priceOverrideAuthorization.getAuthorizingEmployeeID()));
				sql.addColumn(FIELD_RETAIL_PRICE_MODIFIER_OVERRIDE_ENTRY_METHOD_CODE, priceOverrideAuthorization
						.getEntryMethod().toString());
			}
		}

		sql.addColumn(FIELD_RECORD_LAST_MODIFIED_TIMESTAMP, getSQLCurrentTimestampFunction());

		// Qualifiers
		sql.addQualifier(FIELD_RETAIL_STORE_ID + " = " + getStoreID(transaction));
		sql.addQualifier(FIELD_WORKSTATION_ID + " = " + getWorkstationID(transaction));
		sql.addQualifier(FIELD_TRANSACTION_SEQUENCE_NUMBER + " = " + getTransactionSequenceNumber(transaction));
		sql.addQualifier(FIELD_RETAIL_TRANSACTION_LINE_ITEM_SEQUENCE_NUMBER + " = "
				+ getLineItemSequenceNumber(lineItem));
		sql.addQualifier(FIELD_BUSINESS_DAY_DATE + " = " + getBusinessDayString(transaction));
		sql.addQualifier(FIELD_RETAIL_PRICE_MODIFIER_SEQUENCE_NUMBER + " = " + getSequenceNumber(sequenceNumber));

		try {
			dataConnection.execute(sql.getSQLString());
		} catch (DataException de) {
			logger.error("" + de + "");
			throw de;
		} catch (Exception e) {
			throw new DataException(DataException.UNKNOWN, "updateRetailPriceModifier", e);
		}

		if (0 >= dataConnection.getUpdateCount()) {
			throw new DataException(DataException.NO_DATA, "Update RetailPriceModifier");
		}
	}

	// ---------------------------------------------------------------------
	/**
	 * Inserts a Transaction Price Modifier. For now, this table is used for
	 * ReSA. In POS, the transaction discounts go to the TR_LTM_DSC table but it
	 * is not split up by line item. In order to figure out the discount amount
	 * for each line item, a complex calculation must be implemented. It is
	 * already implemented in POS, but it is not visible by ReSA (ReSA -export
	 * module- doesn't depend on the domain module). Since we already have all
	 * the correct amounts in the lineItem object, we can avoid replicating the
	 * complex calculation for ReSA by writing the data to this new table.
	 * <P>
	 * 
	 * @param dataConnection
	 *            Data Source
	 * @param transaction
	 *            The retail transaction
	 * @param lineItem
	 *            the sales/return line item
	 * @param sequenceNumber
	 *            The sequence number of the modifier
	 * @param discountLineItem
	 *            discount
	 * @exception DataException
	 *                upon error
	 */
	// ---------------------------------------------------------------------
	public void insertSaleReturnPriceModifier(JdbcDataConnection dataConnection, RetailTransactionIfc transaction,
			SaleReturnLineItemIfc lineItem, int sequenceNumber, ItemDiscountStrategyIfc discountLineItem)
			throws DataException {
		SQLInsertStatement sql = new SQLInsertStatement();

		// Table
		sql.setTable(TABLE_SALE_RETURN_PRICE_MODIFIER);

		// Get the line and transaction number.
		String lineNumber = getLineItemSequenceNumber(lineItem);
		String tranNumber = getTransactionSequenceNumber(transaction);
		((MAXSaleReturnTransactionIfc) transaction).setItemLevelDiscount(false);
		// Fields
		sql.addColumn(FIELD_RETAIL_STORE_ID, getStoreID(transaction));
		sql.addColumn(FIELD_WORKSTATION_ID, getWorkstationID(transaction));
		sql.addColumn(FIELD_TRANSACTION_SEQUENCE_NUMBER, tranNumber);
		sql.addColumn(FIELD_BUSINESS_DAY_DATE, getBusinessDayString(transaction));
		sql.addColumn(FIELD_RETAIL_TRANSACTION_LINE_ITEM_SEQUENCE_NUMBER, lineNumber);
		sql.addColumn(FIELD_RETAIL_PRICE_MODIFIER_SEQUENCE_NUMBER, getSequenceNumber(sequenceNumber));
		sql.addColumn(FIELD_RECORD_CREATION_TIMESTAMP, getSQLCurrentTimestampFunction());
		sql.addColumn(FIELD_RECORD_LAST_MODIFIED_TIMESTAMP, getSQLCurrentTimestampFunction());
		boolean mcouponDscFlag = false; 
		if (discountLineItem != null) { // Item discount
			sql.addColumn(FIELD_RETAIL_PRICE_MODIFIER_DERIVATION_RULE_ID, getDiscountRuleID(discountLineItem));
			sql.addColumn(FIELD_RETAIL_PRICE_MODIFIER_REASON_CODE, getPriceModifierReasonCode(discountLineItem));
			if(getPriceModifierReasonCode(discountLineItem).equals("'5170'")){
				mcouponDscFlag = true;
			}
			if(discountLineItem.getReason().getCode().equalsIgnoreCase("-1") && discountLineItem.getDiscountEmployeeID().isEmpty()
					&& !(((MAXSaleReturnTransactionIfc) transaction).getCapillaryCouponsApplied().isEmpty()) )
			{
						// Changes for Rev 2.4
						sql.addColumn(FIELD_RETAIL_PRICE_MODIFIER_REASON_CODE, "5170");
						mcouponDscFlag = true;
			}
			sql.addColumn(FIELD_RETAIL_PRICE_MODIFIER_PERCENT, getPriceModifierPercent(discountLineItem));
			sql.addColumn(FIELD_RETAIL_PRICE_MODIFIER_AMOUNT, getPriceModifierAmount(discountLineItem));
			sql.addColumn(FIELD_PRICE_DERIVATION_RULE_METHOD_CODE, getPriceModifierMethodCode(discountLineItem));
			sql.addColumn(FIELD_PRICE_DERIVATION_RULE_ASSIGNMENT_BASIS_CODE,
					getPriceModifierAssignmentBasis(discountLineItem));
			sql.addColumn(FIELD_RETAIL_PRICE_MODIFIER_DISCOUNT_EMPLOYEE_ID,
					makeSafeString(discountLineItem.getDiscountEmployeeID()));
			// Changes for Rev 2.6 : Starts
			if(!discountLineItem.getDiscountEmployeeID().trim().toString().equalsIgnoreCase(""))
			{
				sql.addColumn(FIELD_RETAIL_PRICE_MODIFIER_DISCOUNT_EMPLOYEE_ID,
						getEmployeIdCompanyDetails(transaction, discountLineItem));
			}
			
			// Changes for Rev 2.6 : END
			sql.addColumn(FIELD_RETAIL_PRICE_MODIFIER_DAMAGE_DISCOUNT,
					getPriceModifierDamageDiscountFlag(discountLineItem));
			sql.addColumn(FIELD_PCD_INCLUDED_IN_BEST_DEAL, getIncludedInBestDealFlag(discountLineItem));
			sql.addColumn(FIELD_ADVANCED_PRICING_RULE, getAdvancedPricingRuleFlag(discountLineItem));
			try {
				sql.addColumn(FIELD_RETAIL_PRICE_MODIFIER_DISCOUNT_REFERENCE_ID,
						getPriceModifierReferenceID(transaction, discountLineItem,discountLineItem.getDiscountEmployeeID().isEmpty()));
			} catch (Exception e) {
				logger.error(e.getMessage());
			}
			sql.addColumn(FIELD_RETAIL_PRICE_MODIFIER_DISCOUNT_REFERENCE_ID_TYPE_CODE,
					getPriceModifierReferenceIDTypeCode(discountLineItem));
			sql.addColumn(FIELD_RETAIL_PRICE_MODIFIER_DISCOUNT_TYPE_CODE, discountLineItem.getTypeCode());
			sql.addColumn(FIELD_RETAIL_PRICE_MODIFIER_STOCK_LEDGER_ACCOUNTING_DISPOSITION_CODE,
					inQuotes(discountLineItem.getAccountingMethod()));
			String promotionId = String.valueOf(discountLineItem.getPromotionId());
			// modified for sending promoId in rtlogs for bill buster discounts

			try {
				if (lineItem.getPLUItem() instanceof MAXPLUItemIfc) {
					// Changes for Rev 1.1 : Starts
					ArrayList<MAXAdvancedPricingRuleIfc> invoiceRules = ((MAXPLUItemIfc) lineItem.getPLUItem())
							.getInvoiceDiscounts();
					if (invoiceRules != null && invoiceRules.size() > 0
							&& (discountLineItem.getReasonCode() == 19 || discountLineItem.getReasonCode() == 20))
						promotionId = String.valueOf(invoiceRules.get(0).getPromotionId());
					// Changes for Rev 1.1 : Ends
				}
				// end
			} catch (Exception e) {
				logger.error(e.getMessage());
			}

			sql.addColumn(FIELD_PROMOTION_ID, ((promotionId != null) ? promotionId : "0"));
			sql.addColumn(FIELD_PROMOTION_COMPONENT_ID, discountLineItem.getPromotionComponentId());
			sql.addColumn(FIELD_PROMOTION_COMPONENT_DETAIL_ID, discountLineItem.getPromotionComponentDetailId());
		} else { // Price Override
			sql.addColumn(FIELD_RETAIL_PRICE_MODIFIER_DERIVATION_RULE_ID, "0");
			sql.addColumn(FIELD_RETAIL_PRICE_MODIFIER_REASON_CODE, getPriceModifierReasonCode(lineItem));
			sql.addColumn(FIELD_RETAIL_PRICE_MODIFIER_AMOUNT, getPriceModifierAmount(lineItem));
			// if security override data exists, use it
			SecurityOverrideIfc priceOverrideAuthorization = lineItem.getItemPrice().getPriceOverrideAuthorization();
			if (priceOverrideAuthorization != null) {
				sql.addColumn(FIELD_RETAIL_PRICE_MODIFIER_OVERRIDE_EMPLOYEE_ID,
						makeSafeString(priceOverrideAuthorization.getAuthorizingEmployeeID()));
				sql.addColumn(FIELD_RETAIL_PRICE_MODIFIER_OVERRIDE_ENTRY_METHOD_CODE, priceOverrideAuthorization
						.getEntryMethod().toString());
			}
		}
		try {
			dataConnection.execute(sql.getSQLString());
		} catch (DataException de) {
			logger.error("" + de + "");
			throw de;
		} catch (Exception e) {
			throw new DataException(DataException.UNKNOWN, "Insert SaleReturnPriceModifier", e);
		}
	}
	// Changes for Rev 2.6 : Starts
	
	private String getEmployeIdCompanyDetails(RetailTransactionIfc transaction,
			ItemDiscountStrategyIfc discountLineItem) {
		
		String companyName=null;
		String employeeId=null;
		String employeeIDCompanyName=null;
					if(transaction instanceof MAXSaleReturnTransaction)
					{
						MAXSaleReturnTransaction maxLs=(MAXSaleReturnTransaction)transaction;
						if(maxLs.getEmployeeCompanyName() != null)
						{
						companyName=maxLs.getEmployeeCompanyName().trim().toString();
						}
						employeeId=discountLineItem.getDiscountEmployeeID();
					}
					employeeIDCompanyName=employeeId+"-"+companyName;
					// atul's changes end here.
		return makeSafeString(employeeIDCompanyName);
	}
	// Changes for Rev 2.6 : End

	// ---------------------------------------------------------------------
	/**
	 * Updates a Transaction Price Modifier. For now, this table is used for
	 * ReSA. In POS, the transaction discounts go to the TR_LTM_DSC table but it
	 * is not split up by line item. In order to figure out the discount amount
	 * for each line item, a complex calculation must be implemented. It is
	 * already implemented in POS, but it is not visible by ReSA (ReSA -export
	 * module- doesn't depend on the domain module). Since we already have all
	 * the correct amounts in the lineItem object, we can avoid replicating the
	 * complex calculation for ReSA by writing the data to this new table.
	 * <P>
	 * 
	 * @param dataConnection
	 *            Data Source
	 * @param transaction
	 *            The retail transaction
	 * @param lineItem
	 *            the sales/return line item
	 * @param sequenceNumber
	 *            The sequence number of the modifier
	 * @param discountLineItem
	 *            discount
	 * @exception DataException
	 *                upon error
	 */
	// ---------------------------------------------------------------------
	public void updateSaleReturnPriceModifier(JdbcDataConnection dataConnection, RetailTransactionIfc transaction,
			SaleReturnLineItemIfc lineItem, int sequenceNumber, ItemDiscountStrategyIfc discountLineItem)
			throws DataException {
		SQLUpdateStatement sql = new SQLUpdateStatement();

		// Table
		sql.setTable(TABLE_SALE_RETURN_PRICE_MODIFIER);

		// Fields
		if (discountLineItem != null) { // Item discount
			sql.addColumn(FIELD_RETAIL_PRICE_MODIFIER_DERIVATION_RULE_ID, getDiscountRuleID(discountLineItem));
			sql.addColumn(FIELD_RETAIL_PRICE_MODIFIER_REASON_CODE, getPriceModifierReasonCode(discountLineItem));
			sql.addColumn(FIELD_RETAIL_PRICE_MODIFIER_PERCENT, getPriceModifierPercent(discountLineItem));
			sql.addColumn(FIELD_RETAIL_PRICE_MODIFIER_AMOUNT, getPriceModifierAmount(discountLineItem));
			sql.addColumn(FIELD_PRICE_DERIVATION_RULE_METHOD_CODE, getPriceModifierMethodCode(discountLineItem));
			sql.addColumn(FIELD_PRICE_DERIVATION_RULE_ASSIGNMENT_BASIS_CODE,
					getPriceModifierAssignmentBasis(discountLineItem));
			sql.addColumn(FIELD_RETAIL_PRICE_MODIFIER_DISCOUNT_EMPLOYEE_ID,
					makeSafeString(discountLineItem.getDiscountEmployeeID()));
			sql.addColumn(FIELD_RETAIL_PRICE_MODIFIER_DAMAGE_DISCOUNT,
					getPriceModifierDamageDiscountFlag(discountLineItem));
			sql.addColumn(FIELD_PCD_INCLUDED_IN_BEST_DEAL, getIncludedInBestDealFlag(discountLineItem));
			sql.addColumn(FIELD_ADVANCED_PRICING_RULE, getAdvancedPricingRuleFlag(discountLineItem));

			try {
				sql.addColumn(FIELD_RETAIL_PRICE_MODIFIER_DISCOUNT_REFERENCE_ID,
						getPriceModifierReferenceID(transaction, discountLineItem,false));
			} catch (Exception e) {
				logger.error(e.getMessage());
			}

			sql.addColumn(FIELD_RETAIL_PRICE_MODIFIER_DISCOUNT_REFERENCE_ID_TYPE_CODE,
					getPriceModifierReferenceIDTypeCode(discountLineItem));
			sql.addColumn(FIELD_RETAIL_PRICE_MODIFIER_DISCOUNT_TYPE_CODE, discountLineItem.getTypeCode());
			sql.addColumn(FIELD_RETAIL_PRICE_MODIFIER_STOCK_LEDGER_ACCOUNTING_DISPOSITION_CODE,
					inQuotes(discountLineItem.getAccountingMethod()));
			String promotionId = String.valueOf(discountLineItem.getPromotionId());
			// Changes for Rev 1.1 : Starts
			ArrayList<MAXAdvancedPricingRuleIfc> invoiceRules = ((MAXPLUItemIfc) lineItem.getPLUItem())
					.getInvoiceDiscounts();
			if (discountLineItem.getReasonCode() == 19 || discountLineItem.getReasonCode() == 20)
				if (invoiceRules != null && invoiceRules.size() > 0) {
				promotionId = String.valueOf(invoiceRules.get(0).getPromotionId());
				}
			// Changes for Rev 1.1 : Ends
			sql.addColumn(FIELD_PROMOTION_ID, ((promotionId != null) ? promotionId : "0"));
			sql.addColumn(FIELD_PROMOTION_COMPONENT_ID, discountLineItem.getPromotionComponentId());
			sql.addColumn(FIELD_PROMOTION_COMPONENT_DETAIL_ID, discountLineItem.getPromotionComponentDetailId());
		} else { // Price Override
			sql.addColumn(FIELD_RETAIL_PRICE_MODIFIER_DERIVATION_RULE_ID, "0");
			sql.addColumn(FIELD_RETAIL_PRICE_MODIFIER_REASON_CODE, getPriceModifierReasonCode(lineItem));
			sql.addColumn(FIELD_RETAIL_PRICE_MODIFIER_AMOUNT, getPriceModifierAmount(lineItem));
			// if security override data exists, use it
			SecurityOverrideIfc priceOverrideAuthorization = lineItem.getItemPrice().getPriceOverrideAuthorization();
			if (priceOverrideAuthorization != null) {
				sql.addColumn(FIELD_RETAIL_PRICE_MODIFIER_OVERRIDE_EMPLOYEE_ID,
						makeSafeString(priceOverrideAuthorization.getAuthorizingEmployeeID()));
				sql.addColumn(FIELD_RETAIL_PRICE_MODIFIER_OVERRIDE_ENTRY_METHOD_CODE, priceOverrideAuthorization
						.getEntryMethod().toString());
			}
		}

		sql.addColumn(FIELD_RECORD_LAST_MODIFIED_TIMESTAMP, getSQLCurrentTimestampFunction());
		
		// Qualifiers
		sql.addQualifier(FIELD_RETAIL_STORE_ID + " = " + getStoreID(transaction));
		sql.addQualifier(FIELD_WORKSTATION_ID + " = " + getWorkstationID(transaction));
		sql.addQualifier(FIELD_TRANSACTION_SEQUENCE_NUMBER + " = " + getTransactionSequenceNumber(transaction));
		sql.addQualifier(FIELD_RETAIL_TRANSACTION_LINE_ITEM_SEQUENCE_NUMBER + " = "
				+ getLineItemSequenceNumber(lineItem));
		sql.addQualifier(FIELD_BUSINESS_DAY_DATE + " = " + getBusinessDayString(transaction));
		sql.addQualifier(FIELD_RETAIL_PRICE_MODIFIER_SEQUENCE_NUMBER + " = " + getSequenceNumber(sequenceNumber));

		try {
			dataConnection.execute(sql.getSQLString());
		} catch (DataException de) {
			logger.error("" + de + "");
			throw de;
		} catch (Exception e) {
			throw new DataException(DataException.UNKNOWN, "UpdateSaleReturnPriceModifier", e);
		}

		if (0 >= dataConnection.getUpdateCount()) {
			throw new DataException(DataException.NO_DATA, "Update SaleReturnPriceModifier");
		}
	}

	// ---------------------------------------------------------------------
	/**
	 * Inserts a deleted Sale Return Line Item.
	 * <p>
	 * A line item component of a Retail transaction that records the exchange
	 * in ownership of a merchandise item (i.e. a sale or return) or the sale or
	 * refund related to a service.
	 * <p>
	 * 
	 * @param dataConnection
	 *            Data source connection to use
	 * @param transaction
	 *            The retail transaction
	 * @param lineItem
	 *            The sale/return line item
	 * @exception DataException
	 *                upon error
	 */
	// ---------------------------------------------------------------------
	public void insertDeletedSaleReturnLineItem(JdbcDataConnection dataConnection, RetailTransactionIfc transaction,
			SaleReturnLineItemIfc lineItem) throws DataException {
		SQLInsertStatement sql = new SQLInsertStatement();

		// Table
		sql.setTable(TABLE_SALE_RETURN_LINE_ITEM);

		// Fields
		sql.addColumn(FIELD_RETAIL_STORE_ID, getStoreID(transaction));
		sql.addColumn(FIELD_WORKSTATION_ID, getWorkstationID(transaction));
		sql.addColumn(FIELD_TRANSACTION_SEQUENCE_NUMBER, getTransactionSequenceNumber(transaction));
		sql.addColumn(FIELD_BUSINESS_DAY_DATE, getBusinessDayString(transaction));
		sql.addColumn(FIELD_RETAIL_TRANSACTION_LINE_ITEM_SEQUENCE_NUMBER, getLineItemSequenceNumber(lineItem));
		sql.addColumn(FIELD_GIFT_REGISTRY_ID, getGiftRegistryString(lineItem));
		sql.addColumn(FIELD_ITEM_ID, getItemID(lineItem));
		sql.addColumn(FIELD_POS_ITEM_ID, inQuotes(lineItem.getPosItemID()));
		sql.addColumn(FIELD_SERIAL_NUMBER, getItemSerial(lineItem));
		sql.addColumn(FIELD_TAX_GROUP_ID, getTaxGroupID(lineItem));
		sql.addColumn(FIELD_SALE_RETURN_LINE_ITEM_QUANTITY, getItemQuantity(lineItem));
		sql.addColumn(FIELD_SALE_RETURN_LINE_ITEM_EXTENDED_AMOUNT, getItemExtendedAmount(lineItem));
		sql.addColumn(FIELD_SALE_RETURN_LINE_ITEM_EXTENDED_DISCOUNTED_AMOUNT, getItemExtendedDiscountedAmount(lineItem));
		sql.addColumn(FIELD_SALE_RETURN_LINE_ITEM_VAT_AMOUNT, lineItem.getItemTaxAmount().getStringValue());
		sql.addColumn(FIELD_SALE_RETURN_LINE_ITEM_TAX_INC_AMOUNT, lineItem.getItemInclusiveTaxAmount().getStringValue());
		sql.addColumn(FIELD_MERCHANDISE_RETURN_FLAG, getReturnFlag(lineItem));
		sql.addColumn(FIELD_MERCHANDISE_RETURN_REASON_CODE, getReturnReasonCode(lineItem));
		sql.addColumn(FIELD_POS_ORIGINAL_TRANSACTION_ID, getOriginalTransactionId(lineItem));
		sql.addColumn(FIELD_ORIGINAL_BUSINESS_DAY_DATE, getOriginalDate(lineItem));
		sql.addColumn(FIELD_ORIGINAL_RETAIL_TRANSACTION_LINE_ITEM_SEQUENCE_NUMBER, getOriginalLineNumber(lineItem));
		sql.addColumn(FIELD_ORIGINAL_RETAIL_STORE_ID, getOriginalStoreID(lineItem));
		sql.addColumn(FIELD_POS_DEPARTMENT_ID, getDepartmentID(lineItem));
		sql.addColumn(FIELD_SEND_FLAG, getSendFlag(lineItem));
		sql.addColumn(FIELD_SEND_LABEL_COUNT, getSendLabelCount(lineItem));
		sql.addColumn(FIELD_RECORD_LAST_MODIFIED_TIMESTAMP, getSQLCurrentTimestampFunction());
		sql.addColumn(FIELD_RECORD_CREATION_TIMESTAMP, getSQLCurrentTimestampFunction());
		sql.addColumn(FIELD_GIFT_RECEIPT_FLAG, getGiftReceiptFlag(lineItem));
		sql.addColumn(FIELD_ORDER_REFERENCE_ID, lineItem.getOrderLineReference());
		sql.addColumn(FIELD_ITEM_ID_ENTRY_METHOD_CODE, inQuotes(lineItem.getEntryMethod().getIxRetailCode()));
		sql.addColumn(FIELD_SIZE_CODE, getItemSizeCode(lineItem));

		sql.addColumn(FIELD_RETURN_RELATED_ITEM_FLAG, getReturnRelatedItemFlag(lineItem));
		sql.addColumn(FIELD_RELATED_ITEM_TRANSACTION_LINE_ITEM_SEQ_NUMBER, getRelatedSeqNum(lineItem));
		sql.addColumn(FIELD_REMOVE_RELATED_ITEM_FLAG, getRemoveRelatedItemFlag(lineItem));
		sql.addColumn(FIELD_RETAIL_TRANSACTION_LINE_ITEM_PREMANENT_RETAIL_PRICE, getPermanentSellingPrice(lineItem));
		/* India Localization - Tax Changes Starts Here */
		if (lineItem.getPLUItem() instanceof MAXPLUItem) {
			sql.addColumn(FIELD_SALE_RETURN_LINE_ITEM_MRP_AMOUNT, ((MAXPLUItem) lineItem.getPLUItem())
					.getMaximumRetailPrice().getStringValue());
		} else if (lineItem.getPLUItem() instanceof MAXGiftCardPLUItem) {
			sql.addColumn(FIELD_SALE_RETURN_LINE_ITEM_MRP_AMOUNT, ((MAXGiftCardPLUItem) lineItem.getPLUItem())
					.getMaximumRetailPrice().getStringValue());
		}
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
		
		/* Rev 2.1 changes starts*/
		if(lineItem.getPLUItem() != null && lineItem.getPLUItem() instanceof MAXPLUItemIfc) {
			if(((MAXPLUItemIfc) lineItem.getPLUItem()).getHsnNum() != null && !((MAXPLUItemIfc) lineItem.getPLUItem()).getHsnNum().equalsIgnoreCase("'null'"))
				sql.addColumn(FIELD_ITEM_COLLECTION_ID, inQuotes(((MAXPLUItemIfc) lineItem.getPLUItem()).getHsnNum()) );
			else
			sql.addColumn(FIELD_ITEM_COLLECTION_ID, "0" );
		/* Rev 2.1 changes ends*/
		/* Rev 2.7 changes starts*/
		if(((MAXPLUItemIfc) lineItem.getPLUItem()).getTaxCategory() != -1)
			sql.addColumn(FIELD_ITEM_KIT_HEADER_REFERENCE_ID, ((MAXPLUItemIfc) lineItem.getPLUItem()).getTaxCategory());
		else
			sql.addColumn(FIELD_ITEM_KIT_HEADER_REFERENCE_ID, 0);
		/* Rev 2.7 changes ends*/
		}
		if(transaction instanceof MAXSaleReturnTransaction){
	        sql.addColumn(FIELD_MERCHANDISE_HIERARCHY_GROUP_ID, makeSafeString(((MAXSaleReturnLineItem)((MAXSaleReturnTransaction)transaction).getDeletedLineItems().elementAt(0)).getPLUItem().getItemClassification().getMerchandiseHierarchyGroup()));
	        }
	        if(transaction instanceof MAXLayawayTransaction){
	        	sql.addColumn(FIELD_MERCHANDISE_HIERARCHY_GROUP_ID, makeSafeString(((MAXSaleReturnLineItem)((MAXLayawayTransaction)transaction).getDeletedLineItems().elementAt(0)).getPLUItem().getItemClassification().getMerchandiseHierarchyGroup()));
	            
	        }
	       sql.addColumn(FIELD_RETAIL_TRANSACTION_LINE_ITEM_VOID_FLAG, makeSafeString("1"));

		// Added by vaibhav
		// added null pointer check by Pradeep
		if (((MAXSaleReturnLineItemIfc) lineItem).getPromoDiscountForReceipt() != null)
			sql.addColumn(FIELD_PROMO_DISCOUNT_ON_RECEIPT, ((MAXSaleReturnLineItemIfc) lineItem)
					.getPromoDiscountForReceipt().getStringValue());
		else
			sql.addColumn(FIELD_PROMO_DISCOUNT_ON_RECEIPT, "0.00");

		try {
			dataConnection.execute(sql.getSQLString());
		} catch (DataException de) {
			logger.error("" + de + "");
			throw de;
		} catch (Exception e) {
			throw new DataException(DataException.UNKNOWN, "insertDeletedSaleReturnLineItem", e);
		}
		// do not create IDISC for deleted items
		// saveRetailPriceModifiers(dataConnection, transaction, lineItem);
		saveSaleReturnTaxModifier(dataConnection, transaction, lineItem);

		/*
		 * Track commission properly
		 */
		String employee = transaction.getSalesAssociate().getEmployeeID();
		if (lineItem.getSalesAssociate() != null && !employee.equals(lineItem.getSalesAssociate().getEmployeeID())) {
			saveCommissionModifier(dataConnection, transaction, lineItem);
		}

		/*
		 * See if it's an unknown item that we need to save
		 */
		if (lineItem.getPLUItem() instanceof UnknownItemIfc) {
			saveUnknownItem(dataConnection, transaction, lineItem);
		}
	}

	// Changes for Rev 1.4 : Starts
	@Override
	public void insertSaleReturnLineItem(JdbcDataConnection dataConnection, RetailTransactionIfc transaction,
			SaleReturnLineItemIfc lineItem, String lineItemTypeCode) throws DataException {
		insertRetailTransactionLineItem(dataConnection, transaction, lineItem.getLineNumber(), lineItemTypeCode);

		// Don't save the sale components of price adjustments or price
		// adjustment instances.
		// We'll update the sale components in
		// UpdatePriceAdjustedLineItemsTransaction
		if (lineItem.isPriceAdjustmentLineItem()) {
			return;
		}

		SQLInsertStatement sql = new SQLInsertStatement();

		// Table
		sql.setTable(TABLE_SALE_RETURN_LINE_ITEM);

		// Fields
		sql.addColumn(FIELD_RETAIL_STORE_ID, getStoreID(transaction));
		sql.addColumn(FIELD_WORKSTATION_ID, getWorkstationID(transaction));
		sql.addColumn(FIELD_TRANSACTION_SEQUENCE_NUMBER, getTransactionSequenceNumber(transaction));
		sql.addColumn(FIELD_BUSINESS_DAY_DATE, getBusinessDayString(transaction));
		sql.addColumn(FIELD_RETAIL_TRANSACTION_LINE_ITEM_SEQUENCE_NUMBER, getLineItemSequenceNumber(lineItem));
		sql.addColumn(FIELD_GIFT_REGISTRY_ID, getGiftRegistryString(lineItem));
		sql.addColumn(FIELD_ITEM_ID, getItemID(lineItem));
		sql.addColumn(FIELD_POS_ITEM_ID, inQuotes(lineItem.getPosItemID()));
		sql.addColumn(FIELD_SERIAL_NUMBER, getItemSerial(lineItem));
		sql.addColumn(FIELD_TAX_GROUP_ID, getTaxGroupID(lineItem));
		sql.addColumn(FIELD_SALE_RETURN_LINE_ITEM_QUANTITY, getItemQuantity(lineItem));
		sql.addColumn(FIELD_SALE_RETURN_LINE_ITEM_EXTENDED_AMOUNT, getItemExtendedAmount(lineItem));
		sql.addColumn(FIELD_SALE_RETURN_LINE_ITEM_EXTENDED_DISCOUNTED_AMOUNT, getItemExtendedDiscountedAmount(lineItem));
		sql.addColumn(FIELD_SALE_RETURN_LINE_ITEM_VAT_AMOUNT, lineItem.getItemTaxAmount().getStringValue());
		sql.addColumn(FIELD_SALE_RETURN_LINE_ITEM_TAX_INC_AMOUNT, lineItem.getItemInclusiveTaxAmount().getStringValue());
		sql.addColumn(FIELD_SEND_LABEL_COUNT, getSendLabelCount(lineItem));
		sql.addColumn(FIELD_MERCHANDISE_RETURN_FLAG, getReturnFlag(lineItem));
		sql.addColumn(FIELD_MERCHANDISE_RETURN_REASON_CODE, getReturnReasonCode(lineItem));
		sql.addColumn(FIELD_MERCHANDISE_RETURN_ITEM_CONDITION_CODE, getReturnItemConditionCode(lineItem));

		sql.addColumn(FIELD_POS_ORIGINAL_TRANSACTION_ID, getOriginalTransactionId(lineItem));
		sql.addColumn(FIELD_ORIGINAL_BUSINESS_DAY_DATE, getOriginalDate(lineItem));
		sql.addColumn(FIELD_ORIGINAL_RETAIL_TRANSACTION_LINE_ITEM_SEQUENCE_NUMBER, getOriginalLineNumber(lineItem));
		sql.addColumn(FIELD_ORIGINAL_RETAIL_STORE_ID, getOriginalStoreID(lineItem));

		sql.addColumn(FIELD_POS_DEPARTMENT_ID, getDepartmentID(lineItem));
		sql.addColumn(FIELD_SEND_FLAG, getSendFlag(lineItem));
		sql.addColumn(FIELD_SHIPPING_CHARGE_FLAG, getShippingChargeFlag(lineItem));
		sql.addColumn(FIELD_RECORD_LAST_MODIFIED_TIMESTAMP, getSQLCurrentTimestampFunction());
		sql.addColumn(FIELD_RECORD_CREATION_TIMESTAMP, getSQLCurrentTimestampFunction());
		sql.addColumn(FIELD_GIFT_RECEIPT_FLAG, getGiftReceiptFlag(lineItem));
		sql.addColumn(FIELD_ORDER_ID, inQuotes(getOrderID(transaction, lineItem)));
		sql.addColumn(FIELD_ORDER_LINE_ITEM_SEQUENCE_NUMBER, lineItem.getOrderLineReference());
		sql.addColumn(FIELD_ITEM_ID_ENTRY_METHOD_CODE, inQuotes(lineItem.getEntryMethod().getIxRetailCode()));
		sql.addColumn(FIELD_SIZE_CODE, getItemSizeCode(lineItem));

		sql.addColumn(FIELD_RETURN_RELATED_ITEM_FLAG, getReturnRelatedItemFlag(lineItem));
		sql.addColumn(FIELD_RELATED_ITEM_TRANSACTION_LINE_ITEM_SEQ_NUMBER, getRelatedSeqNum(lineItem));
		sql.addColumn(FIELD_REMOVE_RELATED_ITEM_FLAG, getRemoveRelatedItemFlag(lineItem));
		sql.addColumn(FIELD_RETAIL_TRANSACTION_LINE_ITEM_SALES_ASSC_FLAG, getSalesAssociateModifiedFlag(lineItem));
		sql.addColumn(FIELD_RETAIL_TRANSACTION_LINE_ITEM_PREMANENT_RETAIL_PRICE, getPermanentSellingPrice(lineItem));
		sql.addColumn(FIELD_RETAIL_TRANSACTION_LINE_ITEM_RECEIPT_DESCRIPTION, getReceiptDescription(lineItem));
		sql.addColumn(FIELD_RETAIL_TRANSACTION_LINE_ITEM_RECEIPT_DESCRIPTION_LOCALE,
				getReceiptDescriptionLocal(lineItem));
		sql.addColumn(FIELD_RETAIL_TRANSACTION_LINE_ITEM_RESTOCKING_FEE_FLAG, getRestockingFeeFlag(lineItem));
		sql.addColumn(FIELD_SERIALIZED_ITEM_VALIDATION_FLAG, getSerializedItemFlag(lineItem));
		sql.addColumn(FIELD_EXTERNAL_VALIDATION_SERIALIZED_ITEM_FLAG, getExternalValidationSerializedItemFlag(lineItem));
		sql.addColumn(FIELD_SERIALIZED_ITEM_EXTERNAL_SYSTEM_CREATE_UIN, isPOSAllowedToCreateUIN(lineItem));
		sql.addColumn(FIELD_RETAIL_TRANSACTION_LINE_ITEM_MERCHANDISE_HIERARCHY_LEVEL_CODE, getProductGroupID(lineItem));
		sql.addColumn(FIELD_RETAIL_TRANSACTION_LINE_ITEM_SIZE_REQUIRED_FLAG, getSizeRequiredFlag(lineItem));
		sql.addColumn(FIELD_RETAIL_TRANSACTION_LINE_ITEM_SALE_UNIT_OF_MEASURE_CODE, getLineItemUOMCode(lineItem));
		sql.addColumn(FIELD_RETAIL_TRANSACTION_LINE_ITEM_POS_DEPARTMENT_ID, getPosDepartmentID(lineItem));
		sql.addColumn(FIELD_RETAIL_TRANSACTION_LINE_ITEM_SALE_ITEM_TYPE_ID, getItemTypeID(lineItem));
		sql.addColumn(FIELD_RETAIL_TRANSACTION_LINE_ITEM_RETURN_PROHIBITED_FLAG, getReturnProhibited(lineItem));
		sql.addColumn(FIELD_RETAIL_TRANSACTION_LINE_ITEM_EMPLOYEE_DISCOUNT_ALOWED_FLAG,
				getEmployeeDiscountAllowed(lineItem));
		sql.addColumn(FIELD_RETAIL_TRANSACTION_LINE_ITEM_TAXABLE_FLAG, getTaxable(lineItem));
		sql.addColumn(FIELD_ITEM_DISCOUNT_FLAG, getDiscountable(lineItem));
		sql.addColumn(FIELD_ITEM_DAMAGE_DISCOUNT_FLAG, getDamageDiscountable(lineItem));
		sql.addColumn(FIELD_RETAIL_TRANSACTION_LINE_ITEM_MERCHANDISE_HIERARCHY_GROUP_ID,
				getMerchandiseHierarchyGroupID(lineItem));
		sql.addColumn(FIELD_RETAIL_TRANSACTION_LINE_ITEM_MANUFACTURER_ITEM_UPC, getManufacturerItemUPC(lineItem));
		sql.addColumn(FIELD_CLEARANCE_INDICATOR, getClearanceIndicator(lineItem));
		sql.addColumn(NON_RETRIEVED_ORIGINAL_RECEIPT_ID, getUserSuppliedReceiptID(lineItem));
		sql.addColumn(FIELD_SALE_AGE_RESTRICTION_ID, getRestrictiveAge(lineItem));
		sql.addColumn(FIELD_ITEM_PRICE_ENTRY_REQUIRED_FLAG, getPriceEntryRequired(lineItem));
		//System.out.println("MAXJdbcSaveRetailTransactionLineItems 939================="+lineItem.getPLUItem().getEmpID());
		if(lineItem.getPLUItem().getEmpID())
		{
		  sql.addColumn(FIELD_DELIVERY_ORDER_SPECIAL_INSTRUCTIONS,makeSafeString("SpecialEmpDisc"));
		}

		if (((MAXSaleReturnLineItemIfc) lineItem).getPromoDiscountForReceipt() != null) {
			sql.addColumn(FIELD_PROMO_DISCOUNT_ON_RECEIPT, ((MAXSaleReturnLineItemIfc) lineItem).getPromoDiscountForReceipt().getStringValue());
		}

		String extendedRestockingFee = getItemExtendedRestockingFee(lineItem);
		if (extendedRestockingFee != null) {
			sql.addColumn(FIELD_SALE_RETURN_LINE_ITEM_RESTOCKING_FEE_AMOUNT, extendedRestockingFee);
		}

		//Changes for rev 1.5 Starts
		if(lineItem.getDepositAmount() == null)
			lineItem.setDepositAmount(DomainGateway.getBaseCurrencyInstance("0"));
		sql.addColumn(FIELD_SALE_RETURN_LINE_ITEM_DEPOSIT_AMOUNT, lineItem.getDepositAmount().getStringValue());
		//Changes for rev 1.5 ends
		
		if (lineItem instanceof OrderLineItemIfc) {
			sql.addColumn(FIELD_SALE_RETURN_LINE_ITEM_BALANCE_DUE, ((OrderLineItemIfc) lineItem).getItemBalanceDue()
					.getStringValue());
			sql.addColumn(FIELD_SALE_RETURN_LINE_ITEM_PICKUP_CANCEL_PRICE,
					makeCharFromBoolean(((OrderLineItemIfc) lineItem).isPriceCancelledDuringPickup()));
			sql.addColumn(FIELD_SALE_RETURN_LINE_ITEM_PICKUP_INSTORE_PRICE,
					makeCharFromBoolean(((OrderLineItemIfc) lineItem).isInStorePriceDuringPickup()));
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

		if (transaction instanceof SaleReturnTransactionIfc) {
			if (transaction.getTransactionStatus() == TransactionIfc.STATUS_SUSPENDED
					|| transaction.getTransactionStatus() == TransactionIfc.STATUS_COMPLETED) {
				ReturnItemIfc theReturnItem = lineItem.getReturnItem();
				if (theReturnItem != null) {
					boolean wasRetrieved = theReturnItem.isFromRetrievedTransaction();
					if (wasRetrieved) {
						sql.addColumn(FIELD_RETAIL_TRANSACTION_RETRIEVED_FLAG, "'1'");
					} else {
						sql.addColumn(FIELD_RETAIL_TRANSACTION_RETRIEVED_FLAG, "'0'");
					}
				}
			}
		}
		/* Rev 1.7 changes starts*/
		if (lineItem.getPLUItem() instanceof MAXPLUItem) {
			sql.addColumn(FIELD_SALE_RETURN_LINE_ITEM_MRP_AMOUNT, ((MAXPLUItem) lineItem.getPLUItem())
					.getMaximumRetailPrice().getStringValue());
		} else if (lineItem.getPLUItem() instanceof MAXGiftCardPLUItem) {
			sql.addColumn(FIELD_SALE_RETURN_LINE_ITEM_MRP_AMOUNT, ((MAXGiftCardPLUItem) lineItem.getPLUItem())
					.getMaximumRetailPrice().getStringValue());
		}
			else if (lineItem.getPLUItem() instanceof GiftCertificateItemIfc) {
				sql.addColumn(FIELD_SALE_RETURN_LINE_ITEM_MRP_AMOUNT,"0");
			}
		/* Rev 1.7 changes ends*/
		/* Rev 2.1 changes starts*/
		if(lineItem.getPLUItem() != null && lineItem.getPLUItem() instanceof MAXPLUItemIfc){
		if(((MAXPLUItemIfc) lineItem.getPLUItem()).getHsnNum() != null && !((MAXPLUItemIfc) lineItem.getPLUItem()).getHsnNum().equalsIgnoreCase("'null'"))
			sql.addColumn(FIELD_ITEM_COLLECTION_ID, inQuotes(((MAXPLUItemIfc) lineItem.getPLUItem()).getHsnNum()) );
		else 
			sql.addColumn(FIELD_ITEM_COLLECTION_ID,"0");
		/* Rev 2.1 changes ends*/
		/* Rev 2.7 changes starts*/
		if(((MAXPLUItemIfc) lineItem.getPLUItem()).getTaxCategory() != -1)
			sql.addColumn(FIELD_ITEM_KIT_HEADER_REFERENCE_ID, ((MAXPLUItemIfc) lineItem.getPLUItem()).getTaxCategory());
		else
			sql.addColumn(FIELD_ITEM_KIT_HEADER_REFERENCE_ID, 0);
		/* Rev 2.7 changes ends*/
		}
		try {
			dataConnection.execute(sql.getSQLString());
		} catch (DataException de) {
			logger.error(de.toString());
			throw de;
		} catch (Exception e) {
			throw new DataException(DataException.UNKNOWN, "insertSaleReturnLineItem", e);
		}
		
		if (!lineItem.getDiscountEligible()) {
			lineItem.removeAdvancedPricingDiscount();
		}

		saveRetailPriceModifiers(dataConnection, transaction, lineItem);
		saveSaleReturnTaxModifier(dataConnection, transaction, lineItem);

		saveSaleReturnLineItemTaxInformation(dataConnection, transaction, lineItem);
		saveExternalOrderLineItem(dataConnection, transaction, lineItem);

		/*
		 * Track commission properly
		 */
		String employee = transaction.getSalesAssociate().getEmployeeID();
		if (lineItem.getSalesAssociate() != null && !employee.equals(lineItem.getSalesAssociate().getEmployeeID())) {
			saveCommissionModifier(dataConnection, transaction, lineItem);
		}

		/*
		 * See if it's an unknown item that we need to save
		 */
		if (lineItem.getPLUItem() instanceof UnknownItemIfc) {
			saveUnknownItem(dataConnection, transaction, lineItem);
		}
	}
	// Changes for Rev 1.4 : Ends
	
	public void updateSaleReturnLineItem(JdbcDataConnection dataConnection, RetailTransactionIfc transaction,
			SaleReturnLineItemIfc lineItem, String lineItemTypeCode) throws DataException {
		updateRetailTransactionLineItem(dataConnection, transaction, lineItem.getLineNumber(), lineItemTypeCode);
		SQLUpdateStatement sql = new SQLUpdateStatement();

		sql.setTable(TABLE_SALE_RETURN_LINE_ITEM);

		sql.addColumn(FIELD_GIFT_REGISTRY_ID, getGiftRegistryString(lineItem));
		sql.addColumn(FIELD_ITEM_ID, getItemID(lineItem));
		sql.addColumn(FIELD_POS_ITEM_ID, inQuotes(lineItem.getPosItemID()));
		sql.addColumn(FIELD_SERIAL_NUMBER, getItemSerial(lineItem));
		sql.addColumn(FIELD_TAX_GROUP_ID, getTaxGroupID(lineItem));
		sql.addColumn(FIELD_SALE_RETURN_LINE_ITEM_QUANTITY, getItemQuantity(lineItem));
		sql.addColumn(FIELD_SALE_RETURN_LINE_ITEM_EXTENDED_AMOUNT, getItemExtendedAmount(lineItem));
		sql.addColumn(FIELD_SALE_RETURN_LINE_ITEM_VAT_AMOUNT, lineItem.getItemTaxAmount().getStringValue());
		sql.addColumn(FIELD_SALE_RETURN_LINE_ITEM_TAX_INC_AMOUNT, lineItem.getItemInclusiveTaxAmount().getStringValue());
		sql.addColumn(FIELD_MERCHANDISE_RETURN_FLAG, getReturnFlag(lineItem));
		sql.addColumn(FIELD_MERCHANDISE_RETURN_REASON_CODE, getReturnReasonCode(lineItem));
		sql.addColumn(FIELD_POS_ORIGINAL_TRANSACTION_ID, getOriginalTransactionId(lineItem));
		sql.addColumn(FIELD_ORIGINAL_BUSINESS_DAY_DATE, getOriginalDate(lineItem));
		sql.addColumn(FIELD_ORIGINAL_RETAIL_TRANSACTION_LINE_ITEM_SEQUENCE_NUMBER, getOriginalLineNumber(lineItem));
		sql.addColumn(FIELD_ORIGINAL_RETAIL_STORE_ID, getOriginalStoreID(lineItem));
		sql.addColumn(FIELD_POS_DEPARTMENT_ID, getDepartmentID(lineItem));
		sql.addColumn(FIELD_SEND_FLAG, getSendFlag(lineItem));
		sql.addColumn(FIELD_SEND_LABEL_COUNT, getSendLabelCount(lineItem));
		sql.addColumn(FIELD_MODIFICATION_TIMESTAMP, getSQLCurrentTimestampFunction());
		sql.addColumn(FIELD_TRANSACTION_GIFT_RECEIPT_FLAG, getGiftReceiptFlag(lineItem));
		sql.addColumn(FIELD_ORDER_REFERENCE_ID, lineItem.getOrderLineReference());
		sql.addColumn(FIELD_ITEM_ID_ENTRY_METHOD_CODE, inQuotes(lineItem.getEntryMethod().getIxRetailCode()));
		sql.addColumn(FIELD_RETURN_RELATED_ITEM_FLAG, getReturnRelatedItemFlag(lineItem));
		sql.addColumn(FIELD_RELATED_ITEM_TRANSACTION_LINE_ITEM_SEQ_NUMBER, getRelatedSeqNum(lineItem));
		sql.addColumn(FIELD_REMOVE_RELATED_ITEM_FLAG, getRemoveRelatedItemFlag(lineItem));
		sql.addColumn(FIELD_RETAIL_TRANSACTION_LINE_ITEM_SALES_ASSC_FLAG, getSalesAssociateModifiedFlag(lineItem));
		sql.addColumn(FIELD_RETAIL_TRANSACTION_LINE_ITEM_PREMANENT_RETAIL_PRICE, getPermanentSellingPrice(lineItem));

		/* India Localization - Tax Changes Starts Here */

		if (lineItem.getPLUItem() instanceof MAXPLUItem) {
			sql.addColumn(FIELD_SALE_RETURN_LINE_ITEM_MRP_AMOUNT, ((MAXPLUItem) lineItem.getPLUItem())
					.getMaximumRetailPrice().getStringValue());
		} else if (lineItem.getPLUItem() instanceof MAXGiftCardPLUItem) {
			sql.addColumn(FIELD_SALE_RETURN_LINE_ITEM_MRP_AMOUNT, ((MAXGiftCardPLUItem) lineItem.getPLUItem())
					.getMaximumRetailPrice().getStringValue());
		}
		else if (lineItem.getPLUItem() instanceof GiftCertificateItemIfc) {
			sql.addColumn(FIELD_SALE_RETURN_LINE_ITEM_MRP_AMOUNT,"0");
		}

		// Changes start for Rev 1.2 (Send : Extra field)
		// sql.addColumn(FIELD_VAT_EXTRA, getVatExtra(transaction, lineItem));

		// sql.addColumn(FIELD_VAT_COLLECTION_FLAG, getVatColFlag(transaction,
		// lineItem));

		if ((((MAXSaleReturnLineItem) lineItem).getVatCollectionAmount() == null)) {
			((MAXSaleReturnLineItemIfc) lineItem).setVatCollectionAmount(new BigDecimal(0.00));
		}
		// change end start by akanksha for BUG 10029
		// sql.addColumn(FIELD_VAT_COLLECTION_AMOUNT, getVatColAmt(transaction,
		// lineItem));
		// Changes start for Rev 1.2 (Send)

		/*
		 * India Localization - Tax Changes Ends Here not in v14
		 */

		String extendedRestockingFee = getItemExtendedRestockingFee(lineItem);
		if (extendedRestockingFee != null) {
			sql.addColumn(FIELD_SALE_RETURN_LINE_ITEM_RESTOCKING_FEE_AMOUNT, extendedRestockingFee);
		}

		sql.addQualifier(FIELD_SUPPLY_ORDER_STORE_ID + " = " + getStoreID(transaction));
		sql.addQualifier(FIELD_WORKSTATION_ID + " = " + getWorkstationID(transaction));
		sql.addQualifier(FIELD_BUSINESS_DAY_DATE + " = " + getBusinessDayString(transaction));
		sql.addQualifier(FIELD_TRANSACTION_SEQUENCE_NUMBER + " = " + getTransactionSequenceNumber(transaction));
		sql.addQualifier(FIELD_LINE_ITEM_SEQUENCE_NUMBER + " = " + getLineItemSequenceNumber(lineItem));

		if (lineItem.isPriceAdjustmentLineItem()) {
			sql.addColumn(FIELD_ITEM_PRICEADJ_LINE_ITEM_FLAG, makeCharFromBoolean(true));
			sql.addColumn(FIELD_ITEM_PRICEADJ_REFERENCE_ID, lineItem.getPriceAdjustmentReference());
		} else if (lineItem.isPartOfPriceAdjustment()) {
			sql.addColumn(FIELD_ITEM_PRICEADJ_REFERENCE_ID, lineItem.getPriceAdjustmentReference());
		}

		if ((transaction instanceof SaleReturnTransactionIfc)) {
			if (transaction.getTransactionStatus() == TransactionIfc.STATUS_SUSPENDED) {
				ReturnItemIfc theReturnItem = lineItem.getReturnItem();
				if (theReturnItem != null) {
					boolean wasRetrieved = theReturnItem.isFromRetrievedTransaction();
					if (wasRetrieved) {
						sql.addColumn(FIELD_RETAIL_TRANSACTION_RETRIEVED_FLAG, "'1'");
					} else {
						sql.addColumn(FIELD_RETAIL_TRANSACTION_RETRIEVED_FLAG, "'0'");
					}
				}
			}
		}
		try {			
			dataConnection.execute(sql.getSQLString());
		} catch (DataException de) {
			logger.error(de);
			throw de;
		} catch (Exception e) {
			throw new DataException(DataException.UNKNOWN, "updateSaleReturnLineItem", e);
		}

		if (dataConnection.getUpdateCount() <= 0) {
			throw new DataException(DataException.NO_DATA, "Update SaleReturnLineItem");
		}

		saveRetailPriceModifiers(dataConnection, transaction, lineItem);
		saveSaleReturnTaxModifier(dataConnection, transaction, lineItem);

		String employee = transaction.getSalesAssociate().getEmployeeID();
		if ((lineItem.getSalesAssociate() != null) && (!employee.equals(lineItem.getSalesAssociate().getEmployeeID()))) {
			saveCommissionModifier(dataConnection, transaction, lineItem);
		}
	}

	public void insertTaxBreakupLineItem(JdbcDataConnection dataConnection, RetailTransactionIfc transaction,
			SaleReturnLineItemIfc lineItem, MAXLineItemTaxBreakUpDetailIfc lineItemTaxBreakUp) throws DataException {
		SQLInsertStatement sql = new SQLInsertStatement();

		sql.setTable(TABLE_TAX_LINE_ITEM_BREAKUP);

		sql.addColumn(FIELD_RETAIL_STORE_ID, getStoreID(transaction));
		sql.addColumn(FIELD_WORKSTATION_ID, getWorkstationID(transaction));
		sql.addColumn(FIELD_TRANSACTION_SEQUENCE_NUMBER, getTransactionSequenceNumber(transaction));
		sql.addColumn(FIELD_BUSINESS_DAY_DATE, getBusinessDayString(transaction));
		sql.addColumn(FIELD_RETAIL_TRANSACTION_LINE_ITEM_SEQUENCE_NUMBER, getLineItemSequenceNumber(lineItem));
		sql.addColumn(FIELD_ITEM_ID, getItemID(lineItem));
		sql.addColumn(FIELD_POS_ITEM_ID, inQuotes(lineItem.getPosItemID()));
		sql.addColumn(FIELD_SALE_RETURN_LINE_ITEM_QUANTITY, getItemQuantity(lineItem));

		sql.addColumn(FIELD_TAX_BREAKUP_CODE, getTaxCodeString(lineItemTaxBreakUp.getTaxAssignment().getTaxCode()));
		sql.addColumn(FIELD_TAX_BREAKUP_CODE_DESC, getTaxCodeString(lineItemTaxBreakUp.getTaxAssignment()
				.getTaxCodeDescription()));
		sql.addColumn(FIELD_TAX_BREAKUP_RATE, getTaxRate(lineItemTaxBreakUp));
		sql.addColumn(FIELD_TAX_BREAKUP_TAXABLE_AMOUNT, lineItemTaxBreakUp.getTaxableAmount().getStringValue());
		sql.addColumn(FIELD_TAX_BREAKUP_TAX_AMOUNT, lineItemTaxBreakUp.getTaxAmount().getStringValue());

		sql.addColumn(FIELD_TX_FCT, lineItemTaxBreakUp.getTaxAssignment().getTaxAmountFactor().toString());
		sql.addColumn(FIELD_TXBL_FCT, lineItemTaxBreakUp.getTaxAssignment().getTaxableAmountFactor().toString());

		sql.addColumn(FIELD_RECORD_LAST_MODIFIED_TIMESTAMP, getSQLCurrentTimestampFunction());
		sql.addColumn(FIELD_RECORD_CREATION_TIMESTAMP, getSQLCurrentTimestampFunction());
		try {
			dataConnection.execute(sql.getSQLString());
		} catch (DataException de) {
			logger.error(de);
			throw de;
		} catch (Exception e) {
			throw new DataException(DataException.UNKNOWN, "insertTaxBreakupLineItem", e);
		}
	}

	public String getPriceModifierReferenceID(RetailTransactionIfc transaction, DiscountRuleIfc lineItem,boolean mcouponDscFlag)
			throws Exception {
		String returnString = null;
		if (transaction.getTransactionDiscounts() instanceof TransactionDiscountStrategyIfc[]
				&& !(((MAXSaleReturnTransactionIfc) transaction).isItemLevelDiscount())) {
			//Changes for Rev 2.0.1 : Starts
			TransactionDiscountStrategyIfc[] transactionDiscount = (TransactionDiscountStrategyIfc[]) transaction
					.getTransactionDiscounts();
			
			// Changes for Rev 2.5 starts
			if(transaction instanceof MAXSaleReturnTransaction
					 && ((MAXSaleReturnTransaction) transaction).getCapillaryCouponsApplied() != null)
			{
				int couponCount = ((MAXSaleReturnTransaction) transaction).getCapillaryCouponsApplied().size();
				/*if (couponCount <= transactionDiscount.length) 
				{*/
					//if (transactionDiscount[couponCount] instanceof TransactionDiscountByPercentageStrategy && !((TransactionDiscountByPercentageStrategy) transactionDiscount[couponCount]).getCapillaryCoupon().isEmpty()) {
					//Map couponDetail = ((MAXTransactionDiscountStrategyIfc) transactionDiscount[couponCount]).getCapillaryCoupon();
					Vector vector = ((MAXSaleReturnTransaction) transaction).getCapillaryCouponsApplied();
					for (Iterator iterator = vector.iterator(); iterator
							.hasNext();) {
						MAXDiscountCoupon coupnDetail = (MAXDiscountCoupon)iterator.next();
						if (coupnDetail.getDiscountOn().equals("ITEM")) {
							iterator.remove();
						}	
					}
					//changes for rev 2.5 start
					for (int i = 0; i < couponCount; i++) {
						MAXDiscountCoupon coupnDetail = (MAXDiscountCoupon)vector.get(i);
						/*	int j = i;
							if(((MAXSaleReturnTransaction) transaction).getEmployeeDiscountID() != null && empDscFlag){
								j = i+1;
							}
						//deal only with percent	
						if(transactionDiscount[j] instanceof TransactionDiscountByPercentageStrategy &&	"PERC".equalsIgnoreCase(coupnDetail.getDiscountType()) 
								&& coupnDetail.getCouponDiscountAmountPercent().equals(transactionDiscount[j].getDiscountRate().multiply(new BigDecimal(100)).doubleValue())
								&& lineItem.getDiscountRate().equals(transactionDiscount[j].getDiscountRate())){
							
							returnString = new String("'" + coupnDetail.getCampaignId() + "-" + coupnDetail.getCouponNumber() + "'");
							if (transactionDiscount.length > 1) {
								couponCount = couponCount + 1;
								((MAXSaleReturnTransaction) transaction).setNthCoupon(couponCount);
							}
							return returnString;
						}
						else if(transactionDiscount[j] instanceof TransactionDiscountByAmountStrategy && 
								"ABS".equalsIgnoreCase(coupnDetail.getDiscountType()) && lineItem.getDiscountRate().compareTo(new BigDecimal("0.00"))==0){
							
							returnString = new String("'" + coupnDetail.getCampaignId() + "-" + coupnDetail.getCouponNumber() + "'");
							if (transactionDiscount.length > 1) {
								couponCount = couponCount + 1;
								((MAXSaleReturnTransaction) transaction).setNthCoupon(couponCount);
							}
							return returnString;
							
						}*/
						if(coupnDetail != null && mcouponDscFlag){
							returnString = new String("'" + coupnDetail.getCampaignId() + "-" + coupnDetail.getCouponNumber() + "'");
						}
						return returnString;
					}
				//} else {
					//changes for rev 2.5 end			
					if (transactionDiscount.length > 1) {
						couponCount = couponCount + 1;
						((MAXSaleReturnTransaction) transaction).setNthCoupon(couponCount);
					}
				//}
			}
		}
		//Changes for Rev 2.0.1 : Starts
		if (lineItem instanceof MAXItemDiscountStrategyIfc) {
			// Changes for Rev 1.1 : Starts
			if (!((MAXItemDiscountStrategyIfc) lineItem).getCapillaryCoupon().isEmpty()) {
				int itemLevelCouponCount = ((MAXSaleReturnTransaction) transaction).getItemLevelCouponCount();
				Map couponsMap = ((MAXItemDiscountStrategyIfc) lineItem).getCapillaryCoupon();
				//Changes for Rev 2.0.1 : Ends
				// Changes for Rev 1.1 : Ends
				String campaignID = (String) couponsMap.get(MAXCodeConstantsIfc.CAPILLARY_COUPON_CAMPAIGN_ID);
				String couponNumber = (String) couponsMap.get(MAXCodeConstantsIfc.CAPILLARY_COUPON_NUM);
				returnString = new String("'" + campaignID + "-" + couponNumber + "'");
				itemLevelCouponCount = itemLevelCouponCount + 1;
				((MAXSaleReturnTransaction) transaction).setItemLevelCouponCount(itemLevelCouponCount);
				return returnString;
			}
		} else if (lineItem.getReferenceID() != null) {
			returnString = new String("'" + lineItem.getReferenceID() + "'");
		}
		return returnString;

	}

	public void saveRetailPriceModifiers(JdbcDataConnection dataConnection, RetailTransactionIfc transaction,
			SaleReturnLineItemIfc lineItem) throws DataException {
		int discountSequenceNumber = 0;

		if (lineItem.getItemPrice().isPriceOverride()) {
			try {
				insertRetailPriceModifier(dataConnection, transaction, lineItem, discountSequenceNumber, null);
			} catch (DataException e) {
				updateRetailPriceModifier(dataConnection, transaction, lineItem, discountSequenceNumber, null);
			}

			++discountSequenceNumber;
		}

		ItemDiscountStrategyIfc[] modifiers = lineItem.getItemPrice().getItemDiscounts();
		ItemDiscountStrategyIfc discountLineItem;

		// get number of discounts for loop
		int numDiscounts = 0;
		if (modifiers != null) {
			numDiscounts = modifiers.length;
		}

		/*
		 * Loop through each line item.
		 */
		for (int i = 0; i < numDiscounts; i++) {
			discountLineItem = modifiers[i];

			/*
			 * Skip the Transaction level discounts because they are handled by
			 * the Discount Line Item entity
			 */
			if (discountLineItem.getDiscountScope() != DiscountRuleConstantsIfc.DISCOUNT_SCOPE_TRANSACTION) {
				/*
				 * If the insert fails, then try to update the line item
				 */
				try {
					insertRetailPriceModifier(dataConnection, transaction, lineItem, discountSequenceNumber,
							discountLineItem);
				} catch (DataException e) {
					updateRetailPriceModifier(dataConnection, transaction, lineItem, discountSequenceNumber,
							discountLineItem);
				}
			} else {
				/*
				 * If the insert fails, then try to update the line item
				 */
				try {
					insertSaleReturnPriceModifier(dataConnection, transaction, lineItem, discountSequenceNumber,
							discountLineItem);
				} catch (DataException e) {
					updateSaleReturnPriceModifier(dataConnection, transaction, lineItem, discountSequenceNumber,
							discountLineItem);
				}
			}

			++discountSequenceNumber;
		}
		if (!(transaction instanceof MAXLayawayTransaction)) {
			((MAXSaleReturnTransaction) transaction).setNthCoupon(0);
			((MAXSaleReturnTransaction) transaction).setItemLevelCouponCount(0);
		}
		// Save the Promotions
		PromotionLineItemIfc[] promotionLineItems = lineItem.getItemPrice().getPromotionLineItems();
		PromotionLineItemIfc promotionLineItem;

		if (promotionLineItems != null && promotionLineItems.length > 0) {
			for (int sequenceNumber = 0; sequenceNumber < promotionLineItems.length; sequenceNumber++) {
				promotionLineItem = promotionLineItems[sequenceNumber];
				try {
					insertPromotionLineItem(dataConnection, transaction, lineItem, promotionLineItem, sequenceNumber);
				} catch (DataException e) {
					updatePromotionLineItem(dataConnection, transaction, lineItem, promotionLineItem);
				}

			}
		}
	}

	public String getTaxCodeString(String value) {
		if (value != null) {
			value = new String("'" + value + "'");
		}
		return value;
	}

	public String getTaxRate(MAXLineItemTaxBreakUpDetailIfc lineItemTaxBreakUp) {
		String returnValue = null;
		if (lineItemTaxBreakUp != null) {
			returnValue = String.valueOf(lineItemTaxBreakUp.getTaxAssignment().getTaxRate());
		}
		return returnValue;
	}

	public void saveRetailTransactionLineItems(JdbcDataConnection dataConnection, TenderableTransactionIfc transaction)
			throws DataException {
		if (transaction instanceof PaymentTransactionIfc &&
		// layaway transactions insert their own payments
				!(transaction instanceof LayawayTransactionIfc)) {
			try {
				// save payment line item
				savePaymentLineItem(dataConnection, transaction);
			} catch (DataException de) {
				throw de;
			}
		} else {
			if (transaction instanceof RetailTransactionIfc) {
				RetailTransactionIfc rt = (RetailTransactionIfc) transaction;
				int lineItemSequenceNumber = rt.getLineItems().length;
				saveSaleReturnLineItems(dataConnection, rt);
				/* India Localization - Tax Changes Starts Here */
				saveTaxBreakupLineItem(dataConnection, rt);
				/* India Localization - Tax Changes Ends Here */
				saveTaxLineItem(dataConnection, rt, lineItemSequenceNumber);
				saveDiscountLineItems(dataConnection, rt, ++lineItemSequenceNumber);
				if (transaction instanceof SaleReturnTransactionIfc) {
					saveReturnTendersData(dataConnection, (SaleReturnTransactionIfc) transaction);
				}
			}

			// if order, insert order line item data
			if (transaction instanceof OrderTransactionIfc) {
				saveOrderLineItems(dataConnection, (OrderTransactionIfc) transaction);
			} else {
				// This code must be in this else statement because
				// an OrderTransactionIfc object is also a
				// SaleReturnTransactionIfc.
				// This code should be executed for non order transactions only.
				if (transaction instanceof SaleReturnTransactionIfc
						&& transaction.getTransactionStatus() != TransactionConstantsIfc.STATUS_CANCELED) {
					// This method iterates through each sale return line item
					// to determine
					// if the return status sould be written to the order line
					// item table.
					saveReturnOrderLineItemStatus(dataConnection, (SaleReturnTransactionIfc) transaction);
				}
			}
		}
	}

	public void saveTaxBreakupLineItem(JdbcDataConnection dataConnection, RetailTransactionIfc transaction)
			throws DataException {
		AbstractTransactionLineItemIfc[] lineItems = transaction.getLineItems();

		int numItems = 0;
		if (lineItems != null) {
			numItems = lineItems.length;
		}

		SaleReturnLineItemIfc lineItem;
		MAXLineItemTaxBreakUpDetailIfc lineItemTaxBreakUp;
		/*
		 * Loop through each line item. Continue through them all even if one
		 * has failed.
		 */
		for (int i = 0; i < numItems; i++) {
			lineItem = (SaleReturnLineItemIfc) lineItems[i];
			MAXLineItemTaxBreakUpDetailIfc[] lineItemTaxBreakUpDetail = ((MAXItemTaxIfc) (lineItem.getItemPrice()
					.getItemTax())).getLineItemTaxBreakUpDetail();
			int numLineItemTaxBreakUpDetail = 0;
			if (lineItemTaxBreakUpDetail != null) {
				numLineItemTaxBreakUpDetail = lineItemTaxBreakUpDetail.length;
			}
			for (int j = 0; j < numLineItemTaxBreakUpDetail; j++) {
				lineItemTaxBreakUp = lineItemTaxBreakUpDetail[j];
				/*
				 * Insert Only when the TaxAmount is Non Zero.Tax will be update
				 * in TR_LTM_BRKUP
				 */
				// if (lineItemTaxBreakUp.getTaxAmount().signum() != 0)
				// {
				try {
					if (lineItem != null && !(lineItem.getPLUItem() instanceof GiftCardPLUItem))
						insertTaxBreakupLineItem(dataConnection, transaction, lineItem, lineItemTaxBreakUp);
				} catch (DataException e) {
					updateTaxBreakupLineItem(dataConnection, transaction, lineItem, lineItemTaxBreakUp);
				}
			}
		}
		// }
	}

	/* India Localization - Tax Changes Starts Here */
	// ---------------------------------------------------------------------
	/**
	 * Updates a tax line breakup. Used for transaction level tax information
	 * <P>
	 * 
	 * @param dataConnection
	 *            Data Source
	 * @param transaction
	 *            The retail transaction
	 * @exception DataException
	 *                upon error
	 */
	// ---------------------------------------------------------------------
	// v12 base method not used in v14
	// change parameter data type LineItemTaxBreakUpDetailIfc to
	// MAXLineItemTaxBreakUpDetailIfc after comparison of v12
	public void updateTaxBreakupLineItem(JdbcDataConnection dataConnection, RetailTransactionIfc transaction,
			SaleReturnLineItemIfc lineItem, MAXLineItemTaxBreakUpDetailIfc lineItemTaxBreakUp) throws DataException {

		SQLUpdateStatement sql = new SQLUpdateStatement();

		// Table
		sql.setTable(TABLE_TAX_LINE_ITEM_BREAKUP);

		// Fields
		sql.addColumn(FIELD_RETAIL_STORE_ID, getStoreID(transaction));
		sql.addColumn(FIELD_WORKSTATION_ID, getWorkstationID(transaction));
		sql.addColumn(FIELD_TRANSACTION_SEQUENCE_NUMBER, getTransactionSequenceNumber(transaction));
		sql.addColumn(FIELD_BUSINESS_DAY_DATE, getBusinessDayString(transaction));
		sql.addColumn(FIELD_RETAIL_TRANSACTION_LINE_ITEM_SEQUENCE_NUMBER, getLineItemSequenceNumber(lineItem));
		sql.addColumn(FIELD_ITEM_ID, getItemID(lineItem));
		sql.addColumn(FIELD_POS_ITEM_ID, getItemID(lineItem));
		sql.addColumn(FIELD_SALE_RETURN_LINE_ITEM_QUANTITY, getItemQuantity(lineItem));

		sql.addColumn(FIELD_TAX_BREAKUP_CODE, getTaxCodeString(lineItemTaxBreakUp.getTaxAssignment().getTaxCode()));
		sql.addColumn(FIELD_TAX_BREAKUP_CODE_DESC, getTaxCodeString(lineItemTaxBreakUp.getTaxAssignment()
				.getTaxCodeDescription()));
		sql.addColumn(FIELD_TAX_BREAKUP_RATE, getTaxRate(lineItemTaxBreakUp));
		sql.addColumn(FIELD_TAX_BREAKUP_TAXABLE_AMOUNT, lineItemTaxBreakUp.getTaxableAmount().getStringValue());
		sql.addColumn(FIELD_TAX_BREAKUP_TAX_AMOUNT, lineItemTaxBreakUp.getTaxAmount().getStringValue());

		sql.addColumn(FIELD_RECORD_LAST_MODIFIED_TIMESTAMP, getSQLCurrentTimestampFunction());

		// Qualifiers
		sql.addQualifier(FIELD_RETAIL_STORE_ID + " = " + getStoreID(transaction));
		sql.addQualifier(FIELD_WORKSTATION_ID + " = " + getWorkstationID(transaction));
		sql.addQualifier(FIELD_TRANSACTION_SEQUENCE_NUMBER + " = " + getTransactionSequenceNumber(transaction));
		sql.addQualifier(FIELD_RETAIL_TRANSACTION_LINE_ITEM_SEQUENCE_NUMBER + " = "
				+ getLineItemSequenceNumber(lineItem));
		sql.addQualifier(FIELD_BUSINESS_DAY_DATE + " = " + getBusinessDayString(transaction));
		sql.addQualifier(FIELD_TAX_BREAKUP_CODE + " = "
				+ getTaxCodeString(lineItemTaxBreakUp.getTaxAssignment().getTaxCode()));

		try {
			dataConnection.execute(sql.getSQLString());
		} catch (DataException de) {
			logger.error("" + de + "");
			throw de;
		} catch (Exception e) {
			throw new DataException(DataException.UNKNOWN, "updateTaxBreakupLineItem", e);
		}

		if (0 >= dataConnection.getUpdateCount()) {
			throw new DataException(DataException.NO_DATA, "Update TaxBreakupLineItem");
		}
	}

	/* India Localization - Tax Changes Ends Here */

	/* India Localization - Tax Changes Starts Here */
	// ---------------------------------------------------------------------
	/**
	 * Inserts a tax line breakup. Used for transaction level tax information.
	 * <p>
	 * Apart from the total tax for an item. The Tax BreakUp Details for the
	 * line item needs to be saved and will be used for auditing purpose.
	 * <p>
	 * 
	 * @param dataConnection
	 *            Data source connection to use
	 * @param transaction
	 *            The Transaction that contains the line item
	 * @exception DataException
	 *                upon error
	 */
	// v12 customization - new method
	protected String getVatColAmt(RetailTransactionIfc transaction, SaleReturnLineItemIfc lineItem) {
		String result = null;
		transaction.getTransactionStatus();
		if (transaction.getTransactionStatus() == TransactionConstantsIfc.STATUS_SUSPENDED) {
			ReturnItemIfc theReturnItem = lineItem.getReturnItem();
			if (theReturnItem != null) {

				if (((MAXSaleReturnLineItem) lineItem).isVatCollectionApplied()
						&& !(((MAXSaleReturnLineItem) lineItem).getVatCollectionAmount() == null)) {
					if (((MAXSaleReturnLineItem) lineItem).getVatCollectionAmount().compareTo(new BigDecimal("0.00")) > 0)

					{
						result = ((MAXSaleReturnLineItem) lineItem).getVatCollectionAmount().negate().toString();
					} else if (((MAXSaleReturnLineItem) lineItem).getVatCollectionAmount().compareTo(
							new BigDecimal("0.00")) == -1) {
						result = ((MAXSaleReturnLineItem) lineItem).getVatCollectionAmount().toString();
					}
				} else {
					result = ((MAXSaleReturnLineItem) lineItem).getVatCollectionAmount().toString();
				}
			} else {
				result = "'0'";
			}
		} else {
			if (lineItem instanceof MAXSaleReturnLineItem) {
				if (((MAXSaleReturnLineItem) lineItem).isVatCollectionApplied()
						&& !(((MAXSaleReturnLineItem) lineItem).getVatCollectionAmount() == null)) {
					ReturnItemIfc theReturnItem = lineItem.getReturnItem();
					if (theReturnItem != null) {
						if (((MAXSaleReturnLineItem) lineItem).getVatCollectionAmount().compareTo(
								new BigDecimal("0.00")) > 0) {
							result = ((MAXSaleReturnLineItem) lineItem).getVatCollectionAmount().negate().toString();
						} else {
							result = ((MAXSaleReturnLineItem) lineItem).getVatCollectionAmount().toString();
						}
					}

					else {
						result = ((MAXSaleReturnLineItem) lineItem).getVatCollectionAmount().toString();
					}

				} else {
					result = "0";
				}
			}
		}
		return result;
	}

	// v12 compare changes end

	// v12 customization - new method
	protected String getVatColFlag(RetailTransactionIfc transaction, SaleReturnLineItemIfc lineItem) {
		String result = "''";
		transaction.getTransactionStatus();
		if (transaction.getTransactionStatus() == TransactionConstantsIfc.STATUS_SUSPENDED) {
			ReturnItemIfc theReturnItem = lineItem.getReturnItem();
			if (theReturnItem != null) {

				if (((MAXSaleReturnLineItem) lineItem).isVatCollectionApplied()) {
					result = "'Y'";
				} else {
					result = "'N'";
				}
			} else {
				result = "'N'";
			}
		} else {
			if (lineItem instanceof MAXSaleReturnLineItem) {
				if (((MAXSaleReturnLineItem) lineItem).isVatCollectionApplied()) {
					result = "'Y'";
				} else {
					result = "'N'";
				}
			}
		}
		return result;
	}

	protected String getVatExtra(RetailTransactionIfc transaction, SaleReturnLineItemIfc lineItem) {
		String result = "''";
		transaction.getTransactionStatus();
		if (transaction.getTransactionStatus() == TransactionConstantsIfc.STATUS_SUSPENDED) {
			ReturnItemIfc theReturnItem = lineItem.getReturnItem();
			if (theReturnItem != null) {

				if (((MAXSaleReturnLineItem) lineItem).isVatExtraApplied()) {
					result = "'Y'";
				} else {
					result = "'N'";
				}
			} else {
				result = "''";
			}
		} else {
			if (lineItem instanceof MAXSaleReturnLineItem) {
				if (((MAXSaleReturnLineItem) lineItem).isVatExtraApplied()) {
					result = "'Y'";
				} else {
					result = "'N'";
				}
			}
		}
		return result;
	}

	private String getEmployeeDiscountAllowed(SaleReturnLineItemIfc lineItem) {
		String value = "'0'";
		if (lineItem.isEmployeeDiscountEligible()) {
			value = "'1'";
		}
		return (value);
	}

	// Changes for Rev 1.4 : Starts
	private String getTaxable(SaleReturnLineItemIfc lineItem) {
		String value = "'0'";
		if (lineItem.getPLUItem().getTaxable()) {
			value = "'1'";
		}
		return (value);
	}

	private String getDamageDiscountable(SaleReturnLineItemIfc lineItem) {
		String value = "'0'";
		if (lineItem.isDamageDiscountEligible()) {
			value = "'1'";
		}
		return (value);
	}

	
	private String getDiscountable(SaleReturnLineItemIfc lineItem) {
		String value = "'0'";
		if (lineItem.isDiscountEligible()) {
			value = "'1'";
		}
		return (value);
	}
	// Changes for Rev 1.4 : Ends


}
