/* ===========================================================================
* Copyright (c) 2016 MAX Hyper Market Inc.    All Rights Reserved.
* 
*  Rev 1.4 	Purushotham Reddy	03/06/2019		Changes done POS-Amazon Pay Integration
*  Rev 1.3 	Purushotham Reddy	17/09/2018		Changes done Code Merge CR Prod Defects
*  Rev 1.2 	Nitesh Kumar		04/01/2017		Changes done for Till Reconcillation
*  Rev 1.1	Ashish Yadav		14/09/2016		Changes done for code merging
*  Rev 1.0	Akhilesh Kumar		17/July/2016	Adding new tender type Ecom Prepaid and Ecom COD
*  
* ===========================================================================*/

package max.retail.stores.pos.ui.beans;

import org.apache.log4j.Logger;

import max.retail.stores.domain.tender.MAXTenderLineItemIfc;
import oracle.retail.stores.commerceservices.common.currency.CurrencyIfc;
import oracle.retail.stores.domain.DomainGateway;
import oracle.retail.stores.domain.tender.TenderLineItemIfc;
import oracle.retail.stores.domain.tender.TenderTypeMapIfc;
import oracle.retail.stores.pos.ui.beans.SummaryCountBeanModel;
import oracle.retail.stores.pos.ui.beans.SummaryTenderMenuBeanModel;

public class MAXSummaryTenderMenuBeanModel extends SummaryTenderMenuBeanModel {
	
	private static final long serialVersionUID = -6438299117432972410L;

	protected static Logger logger = Logger
			.getLogger(max.retail.stores.pos.ui.beans.MAXSummaryTenderMenuBeanModel.class);

	protected SummaryCountBeanModel[] summaryCountBeanModel = null;

	public static final int SUMMARY_COUNT_MAX = 15;

	
	public MAXSummaryTenderMenuBeanModel() { // begin SummaryMenuBeanModel()

		TenderTypeMapIfc tenderMap = DomainGateway.getFactory().getTenderTypeMapInstance();

		// Setup the SummaryCountBeanModel with our tender list
		summaryCountBeanModel = new SummaryCountBeanModel[SUMMARY_COUNT_MAX];
		CurrencyIfc zero = null;
		try {
			zero = DomainGateway.getBaseCurrencyInstance();
			zero.setStringValue("0.00");
		} catch (Exception e) {
			logger.error("Currency Information is not available, database/server may be offline");

		}

		summaryCountBeanModel[0] = new SummaryCountBeanModel();
		summaryCountBeanModel[0].setDescription(tenderMap
				.getDescriptor(TenderLineItemIfc.TENDER_TYPE_CASH));
		summaryCountBeanModel[0]
				.setTenderType(TenderLineItemIfc.TENDER_TYPE_CASH);
		summaryCountBeanModel[0].setActionName("Cash");
		summaryCountBeanModel[0].setLabelTag("CashFieldLabel");
		summaryCountBeanModel[0].setLabel("Cash:");
		if (zero != null) {
			summaryCountBeanModel[0].setAmount(zero);
		}
		summaryCountBeanModel[0].setFieldDisabled(true);

		summaryCountBeanModel[1] = new SummaryCountBeanModel();
		summaryCountBeanModel[1].setDescription(tenderMap
				.getDescriptor(TenderLineItemIfc.TENDER_TYPE_CHECK));
		summaryCountBeanModel[1]
				.setTenderType(TenderLineItemIfc.TENDER_TYPE_CHECK);
		summaryCountBeanModel[1].setActionName("Check");
		summaryCountBeanModel[1].setLabelTag("CheckFieldLabel");
		summaryCountBeanModel[1].setLabel("Check:");

		if (zero != null) {
			summaryCountBeanModel[1].setAmount(zero);
		}
		summaryCountBeanModel[1].setFieldDisabled(true);

		summaryCountBeanModel[2] = new SummaryCountBeanModel();
		summaryCountBeanModel[2].setDescription(tenderMap
				.getDescriptor(TenderLineItemIfc.TENDER_TYPE_CHARGE));
		summaryCountBeanModel[2]
				.setTenderType(TenderLineItemIfc.TENDER_TYPE_CHARGE);
		summaryCountBeanModel[2].setActionName("Credit");
		summaryCountBeanModel[2].setLabelTag("CreditFieldLabel");
		summaryCountBeanModel[2].setLabel("Credit:");

		if (zero != null) {
			summaryCountBeanModel[2].setAmount(zero);
		}
		summaryCountBeanModel[2].setFieldDisabled(true);

		summaryCountBeanModel[3] = new SummaryCountBeanModel();
		summaryCountBeanModel[3].setDescription(tenderMap
				.getDescriptor(TenderLineItemIfc.TENDER_TYPE_GIFT_CARD));
		summaryCountBeanModel[3]
				.setTenderType(TenderLineItemIfc.TENDER_TYPE_GIFT_CARD);
		summaryCountBeanModel[3].setActionName("GiftCard");
		summaryCountBeanModel[3].setLabelTag("GiftCardFieldLabel");
		summaryCountBeanModel[3].setLabel("Gift Card:");
		if (zero != null) {
			summaryCountBeanModel[3].setAmount(zero);
		}
		summaryCountBeanModel[3].setFieldDisabled(true);

		/*
		 * summaryCountBeanModel[4] = new SummaryCountBeanModel();
		 * summaryCountBeanModel
		 * [4].setDescription(tenderMap.getDescriptor(TenderLineItemIfc
		 * .TENDER_TYPE_GIFT_CERTIFICATE));
		 * summaryCountBeanModel[4].setTenderType
		 * (TenderLineItemIfc.TENDER_TYPE_GIFT_CERTIFICATE);
		 * summaryCountBeanModel[4].setActionName("GiftCert");
		 * summaryCountBeanModel[4].setLabelTag("GiftCertificateFieldLabel");
		 * summaryCountBeanModel[4].setLabel("Gift Certificate:");
		 * 
		 * if(zero != null) { summaryCountBeanModel[4].setAmount(zero); }
		 * summaryCountBeanModel[4].setFieldDisabled(true);
		 */

		summaryCountBeanModel[4] = new SummaryCountBeanModel();
		summaryCountBeanModel[4].setDescription(tenderMap
				.getDescriptor(TenderLineItemIfc.TENDER_TYPE_COUPON));
		summaryCountBeanModel[4]
				.setTenderType(TenderLineItemIfc.TENDER_TYPE_COUPON);
		summaryCountBeanModel[4].setActionName("Coupon");
		summaryCountBeanModel[4].setLabelTag("CouponFieldLabel");
		summaryCountBeanModel[4].setLabel("Coupon:");
		if (zero != null) {
			summaryCountBeanModel[4].setAmount(zero);
		}
		summaryCountBeanModel[4].setFieldDisabled(true);

		summaryCountBeanModel[5] = new SummaryCountBeanModel();
		summaryCountBeanModel[5].setDescription(tenderMap
				.getDescriptor(TenderLineItemIfc.TENDER_TYPE_STORE_CREDIT));
		summaryCountBeanModel[5]
				.setTenderType(TenderLineItemIfc.TENDER_TYPE_STORE_CREDIT);
		summaryCountBeanModel[5].setActionName("StoreCredit");
		summaryCountBeanModel[5].setLabelTag("StoreCreditFieldLabel");
		summaryCountBeanModel[5].setLabel("Store Credit:");

		if (zero != null) {
			summaryCountBeanModel[5].setAmount(zero);
		}
		summaryCountBeanModel[5].setFieldDisabled(true);

		summaryCountBeanModel[6] = new SummaryCountBeanModel();
		summaryCountBeanModel[6]
				.setDescription(tenderMap
						.getDescriptor(TenderLineItemIfc.TENDER_TYPE_MALL_GIFT_CERTIFICATE));
		summaryCountBeanModel[6]
				.setTenderType(TenderLineItemIfc.TENDER_TYPE_MALL_GIFT_CERTIFICATE);
		summaryCountBeanModel[6].setActionName("MallCert");
		summaryCountBeanModel[6].setLabelTag("MallCertificateFieldLabel");
		summaryCountBeanModel[6].setLabel("Mall Certificate:");

		if (zero != null) {
			summaryCountBeanModel[6].setAmount(zero);
		}
		summaryCountBeanModel[6].setFieldDisabled(true);

		summaryCountBeanModel[7] = new SummaryCountBeanModel();
		summaryCountBeanModel[7].setDescription(tenderMap
				.getDescriptor(TenderLineItemIfc.TENDER_TYPE_PURCHASE_ORDER));
		summaryCountBeanModel[7]
				.setTenderType(TenderLineItemIfc.TENDER_TYPE_PURCHASE_ORDER);
		summaryCountBeanModel[7].setActionName("PurchaseOrder");
		summaryCountBeanModel[7].setLabelTag("PurchaseOrderFieldLabel");
		summaryCountBeanModel[7].setLabel("Purchase Order:");
		if (zero != null) {
			summaryCountBeanModel[7].setAmount(zero);
		}
		summaryCountBeanModel[7].setFieldDisabled(true);

		summaryCountBeanModel[8] = new SummaryCountBeanModel();
		summaryCountBeanModel[8]
				.setDescription(tenderMap
						.getDescriptor(MAXTenderLineItemIfc.TENDER_TYPE_LOYALTY_POINTS));
		summaryCountBeanModel[8]
				.setTenderType(MAXTenderLineItemIfc.TENDER_TYPE_LOYALTY_POINTS);
		summaryCountBeanModel[8].setActionName("LoyaltyPoints");
		summaryCountBeanModel[8].setLabelTag("LoyaltyPointsFieldLabel");
		summaryCountBeanModel[8].setLabel("Loyalty Points:");

		if (zero != null) {
			summaryCountBeanModel[8].setAmount(zero);
		}
		summaryCountBeanModel[8].setFieldDisabled(true);

		/* changes for Rev 1.0 start */

		summaryCountBeanModel[9] = new SummaryCountBeanModel();
		summaryCountBeanModel[9].setDescription(tenderMap
				.getDescriptor(MAXTenderLineItemIfc.TENDER_TYPE_ECOM_PREPAID));
		summaryCountBeanModel[9]
				.setTenderType(MAXTenderLineItemIfc.TENDER_TYPE_ECOM_PREPAID);
		summaryCountBeanModel[9].setActionName("EComPrepaid");
		summaryCountBeanModel[9].setLabelTag("EComPrepaidFieldLabel");
		summaryCountBeanModel[9].setLabel("ECom Prepaid:");

		if (zero != null) {
			summaryCountBeanModel[9].setAmount(zero);
		}
		summaryCountBeanModel[9].setFieldDisabled(true);

		summaryCountBeanModel[10] = new SummaryCountBeanModel();
		summaryCountBeanModel[10].setDescription(tenderMap
				.getDescriptor(MAXTenderLineItemIfc.TENDER_TYPE_ECOM_COD));
		summaryCountBeanModel[10]
				.setTenderType(MAXTenderLineItemIfc.TENDER_TYPE_ECOM_COD);
		summaryCountBeanModel[10].setActionName("EComCOD");
		summaryCountBeanModel[10].setLabelTag("EComCODFieldLabel");
		summaryCountBeanModel[10].setLabel("ECom COD:");

		if (zero != null) {
			summaryCountBeanModel[10].setAmount(zero);
		}
		summaryCountBeanModel[10].setFieldDisabled(true);
		/* changes for Rev 1.0 start */
		// code added by atul shukla
		summaryCountBeanModel[11] = new SummaryCountBeanModel();
		summaryCountBeanModel[11].setDescription(tenderMap
				.getDescriptor(MAXTenderLineItemIfc.TENDER_TYPE_PAYTM));
		summaryCountBeanModel[11]
				.setTenderType(MAXTenderLineItemIfc.TENDER_TYPE_PAYTM);
		summaryCountBeanModel[11].setActionName("Paytm");
		summaryCountBeanModel[11].setLabelTag("PaytmFieldLabel");
		summaryCountBeanModel[11].setLabel("Paytm:");

		if (zero != null) {
			summaryCountBeanModel[11].setAmount(zero);
		}
		summaryCountBeanModel[11].setFieldDisabled(true);

		// code added by atul shukla
		summaryCountBeanModel[12] = new SummaryCountBeanModel();
		summaryCountBeanModel[12].setDescription(tenderMap
				.getDescriptor(MAXTenderLineItemIfc.TENDER_TYPE_MOBIKWIK));
		summaryCountBeanModel[12]
				.setTenderType(MAXTenderLineItemIfc.TENDER_TYPE_MOBIKWIK);
		summaryCountBeanModel[12].setActionName("Mobikwik");
		summaryCountBeanModel[12].setLabelTag("MobikwikFieldLabel");
		summaryCountBeanModel[12].setLabel("Mobikwik:");

		if (zero != null) {
			summaryCountBeanModel[12].setAmount(zero);
		}
		summaryCountBeanModel[12].setFieldDisabled(true);
		
		
		// Rev 1.4 Changes done for POS-Amazon Pay Integration @Purushotham
		summaryCountBeanModel[13] = new SummaryCountBeanModel();
		summaryCountBeanModel[13].setDescription(tenderMap
				.getDescriptor(MAXTenderLineItemIfc.TENDER_TYPE_AMAZON_PAY));
		summaryCountBeanModel[13]
				.setTenderType(MAXTenderLineItemIfc.TENDER_TYPE_AMAZON_PAY);
		summaryCountBeanModel[13].setActionName("AmazonPay");
		summaryCountBeanModel[13].setLabelTag("AmazonPayFieldLabel");
		summaryCountBeanModel[13].setLabel("AmazonPay:");

		if (zero != null) {
			summaryCountBeanModel[13].setAmount(zero);
		}
		summaryCountBeanModel[13].setFieldDisabled(true);
		
		summaryCountBeanModel[14] = new SummaryCountBeanModel();
		summaryCountBeanModel[14].setDescription(tenderMap
				.getDescriptor(MAXTenderLineItemIfc.TENDER_TYPE_EWALLET));
		summaryCountBeanModel[14]
				.setTenderType(MAXTenderLineItemIfc.TENDER_TYPE_EWALLET);
		summaryCountBeanModel[14].setActionName("EWallet");
		summaryCountBeanModel[14].setLabelTag("EWalletFieldLabel");
		summaryCountBeanModel[14].setLabel("EWallet:");

		if (zero != null) {
			summaryCountBeanModel[14].setAmount(zero);
		}
		summaryCountBeanModel[14].setFieldDisabled(true);
	}

	// ----------------------------------------------------------------------------
	/**
	 * Retrieves description and expected amount for each tender or charge.
	 * <P>
	 * 
	 * @return holds description and expected amount for each tender or charge
	 **/
	// ----------------------------------------------------------------------------
	public SummaryCountBeanModel[] getSummaryCountBeanModel() {
		return (summaryCountBeanModel);
	}

	public void setSummaryCountBeanModel(SummaryCountBeanModel[] value) { // begin
																			// setSummaryCountBeanModel[]()
		summaryCountBeanModel = value;
	}
}
