/********************************************************************************
 *   
 *	Copyright (c) 2016 - 2017 MAX Hypermarket, Inc    All Rights Reserved.
 *	
 *  Rev 2.0     July 03,2023        Kumar Vaibhav   Capillary coupon max discount validation
 *  Rev 1.2     Sept 10,2018        Bhanu Priya     Merged build CR.
 *  Rev	1.1 	Apr 20, 2017		Hitesh Dua		Bug: unexpected exception for ABS type of capillary coupon
 *	Rev	1.0 	Nov 30, 2016		Mansi Goel		Changes for Discount Rule FES	
 *
 ********************************************************************************/

package max.retail.stores.pos.services.sale.validate;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;
import java.util.Arrays;

import max.retail.stores.domain.customer.MAXCustomerIfc;
import max.retail.stores.domain.discount.MAXDiscountRuleConstantsIfc;
import max.retail.stores.domain.discount.MAXItemDiscountByAmountStrategy;
import max.retail.stores.domain.discount.MAXItemDiscountByPercentageIfc;
import max.retail.stores.domain.discount.MAXItemDiscountStrategyIfc;
import max.retail.stores.domain.discount.MAXTransactionDiscountByAmountStrategy;
import max.retail.stores.domain.discount.MAXTransactionDiscountByPercentageIfc;
import max.retail.stores.domain.discountCoupon.MAXDiscountCoupon;
import max.retail.stores.domain.discountCoupon.MAXDiscountCouponIfc;
import max.retail.stores.domain.factory.MAXDomainObjectFactory;
import max.retail.stores.domain.stock.MAXPLUItemIfc;
import max.retail.stores.domain.transaction.MAXSaleReturnTransactionIfc;
import max.retail.stores.domain.utility.MAXCodeConstantsIfc;
import max.retail.stores.pos.manager.ifc.MAXUtilityManagerIfc;
import max.retail.stores.pos.services.capillary.MAXCapillaryHelperUtility;
import max.retail.stores.pos.services.sale.MAXSaleCargoIfc;
import max.retail.stores.pos.ui.MAXPOSUIManagerIfc;
import oracle.retail.stores.commerceservices.common.currency.CurrencyIfc;
import oracle.retail.stores.common.utility.StringUtils;
import oracle.retail.stores.domain.DomainGateway;
import oracle.retail.stores.domain.customer.CustomerInfoIfc;
import oracle.retail.stores.domain.discount.DiscountRuleConstantsIfc;
import oracle.retail.stores.domain.discount.DiscountRuleIfc;
import oracle.retail.stores.domain.discount.ItemDiscountStrategyIfc;
import oracle.retail.stores.domain.discount.TransactionDiscountByAmountIfc;
import oracle.retail.stores.domain.discount.TransactionDiscountByPercentageIfc;
import oracle.retail.stores.domain.lineitem.ItemPriceIfc;
import oracle.retail.stores.domain.lineitem.SaleReturnLineItemIfc;
import oracle.retail.stores.domain.transaction.SaleReturnTransactionIfc;
import oracle.retail.stores.domain.utility.CodeListIfc;
import oracle.retail.stores.domain.utility.DiscountUtility;
import oracle.retail.stores.domain.utility.PhoneIfc;
import oracle.retail.stores.foundation.manager.ifc.UIManagerIfc;
import oracle.retail.stores.foundation.tour.gate.Gateway;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.pos.manager.ifc.UtilityManagerIfc;
import oracle.retail.stores.pos.services.PosLaneActionAdapter;
import oracle.retail.stores.pos.services.common.CommonLetterIfc;
import oracle.retail.stores.pos.services.sale.SaleCargoIfc;
import oracle.retail.stores.pos.ui.DialogScreensIfc;
import oracle.retail.stores.pos.ui.POSUIManagerIfc;
import oracle.retail.stores.pos.ui.beans.DialogBeanModel;
import oracle.retail.stores.pos.ui.beans.POSBaseBeanModel;
import oracle.retail.stores.pos.ui.beans.PromptAndResponseModel;
import org.apache.log4j.Logger;

public class MAXValidateCouponAisle extends PosLaneActionAdapter {

	/**
	 * serialVersionUID long
	 */
	private static final long serialVersionUID = 6154477153756145239L;

	public String[] capCRMerrormsg = new String[1];
	public String couponNumber = "";
	private static Logger logger = Logger.getLogger(max.retail.stores.pos.services.sale.validate.MAXValidateCouponAisle.class);

	public void traverse(BusIfc bus) {

		MAXSaleCargoIfc cargo = (MAXSaleCargoIfc) bus.getCargo();
		MAXSaleReturnTransactionIfc trans = (MAXSaleReturnTransactionIfc) cargo.getTransaction();
		POSUIManagerIfc uiManager = (POSUIManagerIfc) bus.getManager(UIManagerIfc.TYPE);
		POSBaseBeanModel model = (POSBaseBeanModel) uiManager.getModel(MAXPOSUIManagerIfc.ENTER_COUPON_NUMBER);
		PromptAndResponseModel promptResponseModel = model.getPromptAndResponseModel();
		boolean isBillBusterTransactionDisc = false;
		MAXCustomerIfc crmCustomer = trans.getMAXTICCustomer();
		String mobileNumber = "";
		String externalId = "";
		String resourceID = "";
		couponNumber = promptResponseModel.getResponseText().trim();
		char[] couponArray = couponNumber.toCharArray();
		for (int i = 0; i <= couponArray.length - 1; i++) {
			if (couponArray[i] == ' ') {
				logger.info("space are not allowed in coupon number");
				displayInvalidNumberMessage(uiManager);
				return;
			}
		}
		promptResponseModel.setResponseText("");
		SaleReturnLineItemIfc[] saleItem = (SaleReturnLineItemIfc[]) trans.getLineItems();
		int saleLength = saleItem.length - 1;
		for (int i = 0; i <= saleLength; i++) {
			ItemPriceIfc itemPrice = saleItem[i].getItemPrice();
			if (itemPrice.getItemDiscounts() != null && itemPrice.getItemDiscounts().length > 0) {
				ItemDiscountStrategyIfc[] discounts = itemPrice.getItemDiscounts();
				if (discounts != null) {
					for (int j = 0; j < discounts.length; j++) {
						DiscountRuleIfc discount = discounts[j];
						ItemDiscountStrategyIfc itemDiscount = (ItemDiscountStrategyIfc) discount;
						if (discount.getReasonCode() == 19 || discount.getReasonCode() == 20) {
							isBillBusterTransactionDisc = true;
						}

					}
				}
			}
		}
		if (!isCouponAlreadyRedeemed(trans, couponNumber)) {

			MAXCustomerIfc customer = null;
			if (trans.getCustomer() instanceof MAXCustomerIfc) {
				customer = (MAXCustomerIfc) trans.getCustomer();
			}
			promptResponseModel.setResponseText("");

			HashMap request = new HashMap();
			// added null check
			if (customer != null && customer.getCustomerType().equalsIgnoreCase("T")) {
				externalId = customer.getCustomerID();
				request.put("customer id", externalId);
			} else if (crmCustomer != null && crmCustomer.getCustomerType().equalsIgnoreCase("T")) {
				String customerId = crmCustomer.getCustomerID();
				request.put("customer id", customerId);
			} else {
				CustomerInfoIfc customerInfo = (CustomerInfoIfc) trans.getCustomerInfo();
				PhoneIfc phone = trans.getCustomer().getContact().getPhoneList().get(0);
				//PhoneIfc phone = customerInfo.getPhoneNumber();
				mobileNumber = phone.getPhoneNumber();
				// Changes neds for code merging
				request.put("mobile number", mobileNumber);
			}

			request.put("coupon number", couponNumber);
			HashMap responseMap = new HashMap();
			MAXCapillaryHelperUtility helperUtility = new MAXCapillaryHelperUtility();
			boolean capCRM = helperUtility.isRedeemCoupon(request, responseMap);
			String ResponseCode = "";
			String Responsemsg = "";
			String ItemStatusCode = "";
			String ItemStatusMessage = "";
			if (responseMap.get("Response Code") != null && responseMap.get("Response Code") != "") {
				ResponseCode = responseMap.get("Response Code").toString();
			}
			if (responseMap.get("Response Message") != null && responseMap.get("Response Message") != "") {
				Responsemsg = responseMap.get("Response Message").toString();
			}
			if (responseMap.get("ItemStatusCode") != null && responseMap.get("ItemStatusCode") != "") {
				ItemStatusCode = responseMap.get("ItemStatusCode").toString();
			}
			if (responseMap.get("ItemStatusMessage") != null && responseMap.get("ItemStatusMessage") != "") {
				ItemStatusMessage = responseMap.get("ItemStatusMessage").toString();
			}
			if ((ResponseCode.equals("200")) || ResponseCode.equals("500")) {

				if (ItemStatusCode.equals("700")) {
					List company = (List) responseMap.get("COUPON_ITEM_HRCHY_CMPNY");
					List division = ((List) responseMap.get("COUPON_ITEM_HRCHY_DIV"));
					List group = ((List) responseMap.get("COUPON_ITEM_HRCHY_GRP"));
					List brand = ((List) responseMap.get("COUPON_BRND_NAME"));
					List dept = ((List) responseMap.get("COUPON_ITEM_HRCHY_DEP"));
					List clss = ((List) responseMap.get("COUPON_ITEM_HRCHY_CLS"));
					List subclss = ((List) responseMap.get("COUPON_ITEM_HRCHY_SBCLS"));
					List skulist = new ArrayList();
					if(responseMap.get("COUPON_PRODUCTS") != null){
						String items = responseMap.get("COUPON_PRODUCTS").toString();
						skulist = new ArrayList(Arrays.asList(items.split(",")));					
					}
					if ((company.isEmpty())
							&& (division.isEmpty() && group.isEmpty() && dept.isEmpty() && brand.isEmpty()
									&& clss.isEmpty() && subclss.isEmpty() && (skulist==null || skulist.isEmpty()))) {
						couponInEligibleMessage(uiManager);
						return;
					}
					if ((!company.isEmpty())
							&& (division.isEmpty() && group.isEmpty() && dept.isEmpty() && brand.isEmpty()
									&& clss.isEmpty() && subclss.isEmpty() && (skulist==null || skulist.isEmpty()))) {
						responseMap.put("COUPON_DISC_ON", "BILL");
					} else {
						responseMap.put("COUPON_DISC_ON", "ITEM");
					}

					String campaignId = ((String) responseMap.get("COUPON_SERIES_CODE"));
					Vector couponApplied = trans.getCapillaryCouponsApplied();
					if (!isCouponUnique(couponApplied, campaignId)) {
						// check for discount on discounted items flag if flag
						// is no then check for discount already present
						if (((String) responseMap.get("VALID_WITH_DISCOUNTED")).toString().equalsIgnoreCase("true")) {
							applyCouponDsicount(responseMap, bus);
						} else if (((String) responseMap.get("VALID_WITH_DISCOUNTED")).toString().equalsIgnoreCase(
								"false"))

						{
							if (((String) responseMap.get("COUPON_DISC_ON")).equals("BILL")
									&& !(couponApplied.isEmpty())) {
								displayHierarchyErrorMessage(uiManager);
								return;
							} else if (((String) responseMap.get("COUPON_DISC_ON")).equals("ITEM")
									&& !(couponApplied.isEmpty())) {
								displayHierarchyErrorMessage(uiManager);
								return;
							} else if (((String) responseMap.get("COUPON_DISC_ON")).equals("BILL")
									&& (isBillBusterTransactionDisc)) {
								displayHierarchyErrorMessage(uiManager);
								return;
							} else if (((String) responseMap.get("COUPON_DISC_ON")).equals("ITEM")
									&& (isBillBusterTransactionDisc)) {
								displayHierarchyErrorMessage(uiManager);
								return;
							}

							else if ((((String) responseMap.get("COUPON_DISC_ON")).equals("BILL"))
									&& (trans.getAdvancedPricingDiscountTotal().getDoubleValue() != 0.00)) {
								displayHierarchyErrorMessage(uiManager);
								return;
							}

							else if ((((String) responseMap.get("COUPON_DISC_ON")).equals("ITEM"))
									&& (trans.getAdvancedPricingDiscountTotal().getDoubleValue() != 0.00)) {
								displayHierarchyErrorMessage(uiManager);
								return;
							} else {
								applyCouponDsicount(responseMap, bus);
							}

						} else {
							displayHierarchyErrorMessage(uiManager);
							return;
						}
					} else {

						displayCouponSeriesAlreadyUsedMessage(uiManager);
						return;

					}
				} else {
					capCRMerrormsg[0] = (String) responseMap.get("ItemStatusMessage");
					resourceID = "CRMCapillaryIsRedeemRequestError";
					displayErrorMessage(resourceID, CommonLetterIfc.INVALID, uiManager, capCRMerrormsg);
					promptResponseModel.setResponseText("");
					return;
				}
			} else {
				capCRMerrormsg[0] = (String) responseMap.get("Response Message");
				resourceID = "InvalidCapliaryUserId";
				displayErrorMessage(resourceID, CommonLetterIfc.INVALID, uiManager, capCRMerrormsg);
				promptResponseModel.setResponseText("");
				return;
			}
		} else
			displayCouponAlreadyUsedMessage(uiManager);
	}

	public void displayDiscountSuccessMessage(MAXSaleReturnTransactionIfc transaction, String newCoupon,
			POSUIManagerIfc uiManager) {
		Object[] appliedCoupons = transaction.getCapillaryCouponsApplied().toArray();
		String[] args = new String[2];
		for (int i = 0; i < appliedCoupons.length; i++) {
			if (newCoupon.equalsIgnoreCase(((MAXDiscountCouponIfc) appliedCoupons[i]).getCouponNumber())) {
				MAXDiscountCouponIfc newDiscountCoupon = (MAXDiscountCouponIfc) appliedCoupons[i];
				args[0] = newCoupon;
				if (newDiscountCoupon.getDiscountType().equalsIgnoreCase("ABS"))
					args[1] = "Rs" + newDiscountCoupon.getCouponDiscountAmountPercent();
				else
					args[1] = newDiscountCoupon.getCouponDiscountAmountPercent() + "%";
				i = appliedCoupons.length; // break out of loop
			}
		}
		DialogBeanModel model = new DialogBeanModel();
		model.setType(DialogScreensIfc.ACKNOWLEDGEMENT);
		model.setArgs(args);
		model.setResourceID("DiscountSuccess");
		model.setButtonLetter(DialogScreensIfc.BUTTON_OK, "DoNotRedeem");  
		//model.setButtonLetter(DialogScreensIfc.BUTTON_OK, CommonLetterIfc.OK);
		uiManager.showScreen(POSUIManagerIfc.DIALOG_TEMPLATE, model);
	}

	public void diplayCapillaryOfflineMessage(POSUIManagerIfc uiManager) {
		String resourceID = "CapillaryOffline";
		displayErrorMessage(resourceID, CommonLetterIfc.OFFLINE, uiManager, capCRMerrormsg);
	}

	public void displayInvalidNumberMessage(POSUIManagerIfc uiManager) {
		String resourceID = "InvalidCoupon";
		displayErrorMessage(resourceID, CommonLetterIfc.INVALID, uiManager, capCRMerrormsg);
	}

	public void displayInvalidAmountMessage(POSUIManagerIfc uiManager) {
		String resourceID = "ThresholdValidate";
		displayErrorMessage(resourceID, CommonLetterIfc.INVALID, uiManager, capCRMerrormsg);
	}

	public void displayHierarchyErrorMessage(POSUIManagerIfc uiManager) {
		String resourceID = "HierarchyValidate";
		displayErrorMessage(resourceID, CommonLetterIfc.INVALID, uiManager, capCRMerrormsg);
	}

	public void itemInEligibleMessage(POSUIManagerIfc uiManager) {
		String resourceID = "ItemEligibilityValidate";
		displayErrorMessage(resourceID, CommonLetterIfc.INVALID, uiManager, capCRMerrormsg);
	}

	public void couponInEligibleMessage(POSUIManagerIfc uiManager) {
		String resourceID = "CouponEligibilityValidate";
		displayErrorMessage(resourceID, CommonLetterIfc.INVALID, uiManager, capCRMerrormsg);
	}

	public void displayCouponAlreadyUsedMessage(POSUIManagerIfc uiManager) {
		String resourceID = "CouponAlreadyUsed";
		displayErrorMessage(resourceID, CommonLetterIfc.INVALID, uiManager, capCRMerrormsg);
	}

	public void displayCouponSeriesAlreadyUsedMessage(POSUIManagerIfc uiManager) {
		String resourceID = "CouponSeriesAlreadyUsed";
		displayErrorMessage(resourceID, CommonLetterIfc.INVALID, uiManager, capCRMerrormsg);
	}

	public void displayErrorMessage(String resourceID, String letter, POSUIManagerIfc uiManager, String[] capCRMerrormsg) {
		DialogBeanModel model = new DialogBeanModel();
		model.setType(DialogScreensIfc.ERROR);
		model.setArgs(capCRMerrormsg);
		model.setResourceID(resourceID);
		model.setButtonLetter(DialogScreensIfc.BUTTON_OK, letter);
		uiManager.showScreen(POSUIManagerIfc.DIALOG_TEMPLATE, model);
	}

	public void applyCouponDsicount(HashMap responseMap, BusIfc bus) {

		SaleCargoIfc cargo = (SaleCargoIfc) bus.getCargo();

		MAXSaleReturnTransactionIfc trans = (MAXSaleReturnTransactionIfc) cargo.getTransaction();
		POSUIManagerIfc uiManager = (POSUIManagerIfc) bus.getManager(UIManagerIfc.TYPE);
		BigDecimal couponPercentage = new BigDecimal(0.0);
		CurrencyIfc couponAmount = DomainGateway.getBaseCurrencyInstance();

		logger.info("Response from capillary..." + responseMap);
		MAXDiscountCouponIfc discountCoupon = new MAXDiscountCoupon();
		discountCoupon.setCampaignId((String) responseMap.get("COUPON_SERIES_CODE"));
		discountCoupon.setDiscountOn((String) responseMap.get("COUPON_DISC_ON"));
		discountCoupon.setDiscountType((String) responseMap.get("COUPON_DISC_TYPE"));

		String discount = responseMap.get("COUPON_DISC_VALUE").toString();
		//Rev 2.0 -start   
		 Double total;
		 Double disc = Double.valueOf(discount);
         String uptodiscount = responseMap.get("COUPON_UPTO_DISC_VALUE").toString();
         Double uptodisc = Double.valueOf(uptodiscount);
         if(disc >= uptodisc) {
        	  total = Double.valueOf(uptodiscount);
         }else {
        	  total = Double.valueOf(discount);
         }
        		 
		//Double total = Double.valueOf(discount);
       //Rev 2.0 -end
		discountCoupon.setCouponDiscountAmountPercent(total);
		String threshold = responseMap.get("COUPON_MIN_BILL_AMNT").toString();
		Double threshAmt = Double.valueOf(threshold);
		discountCoupon.setMinThresholdValue(threshAmt);
		String discountOn = responseMap.get("VALID_WITH_DISCOUNTED").toString();

		Boolean discOn = Boolean.valueOf(discountOn);
		discountCoupon.setDiscountOnDiscountedItems(discOn.booleanValue());
		discountCoupon.setCompany((List) responseMap.get("COUPON_ITEM_HRCHY_CMPNY"));
		discountCoupon.setDivision((List) responseMap.get("COUPON_ITEM_HRCHY_DIV"));
		discountCoupon.setGroup((List) responseMap.get("COUPON_ITEM_HRCHY_GRP"));
		discountCoupon.setBrand((List) responseMap.get("COUPON_BRND_NAME"));
		discountCoupon.setDepartment((List) responseMap.get("COUPON_ITEM_HRCHY_DEP"));
		discountCoupon.setClass1((List) responseMap.get("COUPON_ITEM_HRCHY_CLS"));
		discountCoupon.setSubClass((List) responseMap.get("COUPON_ITEM_HRCHY_SBCLS"));
		if(responseMap.get("COUPON_PRODUCTS") != null){
			discountCoupon.setSkuList(new ArrayList(Arrays.asList((responseMap.get("COUPON_PRODUCTS").toString()).split(","))));
		}
		discountCoupon.setDiscountOn(responseMap.get("COUPON_DISC_ON").toString());
		discountCoupon.setCouponNumber(couponNumber);
		logger.info("Rules validation started in code ...");
		logger.info("Extract the discount amount in capillary coupon");
		if (discountCoupon.getDiscountType().equalsIgnoreCase("ABS")) {
			couponAmount = DomainGateway.getBaseCurrencyInstance(discountCoupon.getCouponDiscountAmountPercent()
					.toString());

		} else {
			double percentage = Double.parseDouble(discountCoupon.getCouponDiscountAmountPercent().toString());
			double dp = percentage / 100.0;
			couponPercentage = new BigDecimal(dp);
			BigDecimal scaleOne = new BigDecimal(1);
			couponPercentage = couponPercentage.divide(scaleOne, 2, BigDecimal.ROUND_HALF_UP);

		}
		logger.info("Response from capillary...Coupon is Unique and Discount on Discounted flag is true");
		//Changes for rev 1.1 starts
		UtilityManagerIfc utility = (UtilityManagerIfc) bus.getManager(UtilityManagerIfc.TYPE);
		//CodeListIfc reasonCodes = utility.getReasonCodes("CORP",MAXCodeConstantsIfc.CODE_LIST_CAPILLARY_COUPON_DISCOUNT);
		//Changes for rev 1.1 Ends

		//if (reasonCodes != null) {
			//Vector vReasonCode = reasonCodes.getKeyEntries();
			//String reasonCode = (String) vReasonCode.get(0);
			//discountCoupon.setReasonCode(reasonCode);
		//}

		// to check if discount is on transaction
		if (discountCoupon.getDiscountOn() != null && discountCoupon.getDiscountOn().equalsIgnoreCase("BILL")) {
			logger.info("Response from capillary...Coupon is of type Transaction-BILL");
			double transTotal = 0.0;
			if (trans != null && trans instanceof MAXSaleReturnTransactionIfc)
				transTotal = trans.getTransactionTotals().getBalanceDue().getDoubleValue();
			logger.info("Transaction amount before discount" + trans.getTransactionTotals().getBalanceDue());
			// check for minimum bill value
			if (transTotal >= discountCoupon.getMinThresholdValue().doubleValue()) {
				logger.info("Balance due is greater than min threshold value so discount can be applied");
				// check whether discount is amount or percentage
				if (discountCoupon.getDiscountType() != null
						&& discountCoupon.getDiscountType().equalsIgnoreCase("PERC")) {
					logger.info("Response from capillary...Coupon is of type Percentage-PERCENTAGE");
					TransactionDiscountByPercentageIfc percentDiscount = DomainGateway.getFactory()
							.getTransactionDiscountByPercentageInstance();
					percentDiscount.setDiscountRate(couponPercentage);
					percentDiscount.setAssignmentBasis(MAXDiscountRuleConstantsIfc.ASSIGNMENT_CAPILLARYCOUPON);
					percentDiscount.setDiscountMethod(DiscountRuleConstantsIfc.DISCOUNT_METHOD_PERCENTAGE);
					percentDiscount.setTypeCode(DiscountRuleConstantsIfc.DISCOUNT_METHOD_PERCENTAGE);
					if(discountCoupon != null && discountCoupon.getReasonCode() != null && !StringUtils.isEmpty(discountCoupon.getReasonCode()))
					{
					percentDiscount.setReasonCode(Integer.parseInt(discountCoupon.getReasonCode()));
					}
					else
					{
						percentDiscount.setReasonCode(5170);
					}
					if(percentDiscount instanceof MAXTransactionDiscountByPercentageIfc)
					{
					HashMap map = ((MAXTransactionDiscountByPercentageIfc) percentDiscount).getCapillaryCoupon();
					map.put(MAXCodeConstantsIfc.CAPILLARY_COUPON_NUM, discountCoupon.getCouponNumber());
					map.put(MAXCodeConstantsIfc.CAPILLARY_COUPON_CAMPAIGN_ID, discountCoupon.getCampaignId());
					map.put(MAXCodeConstantsIfc.CAPILLARY_COUPON_REASON_CODE, discountCoupon.getReasonCode());
					map.put(MAXCodeConstantsIfc.CAPILLARY_COUPON_DISCOUNT_TYPE, discountCoupon.getDiscountType());
					((MAXTransactionDiscountByPercentageIfc) percentDiscount).setCapillaryCoupon(map);
					}
					if (trans != null) {
						trans.addTransactionDiscount(percentDiscount);
						logger.info("Transaction level coupon applied");
						logger.info("Transaction amount after discount" + trans.getTransactionTotals().getBalanceDue());
					}
				} else if (discountCoupon.getDiscountType() != null
						&& discountCoupon.getDiscountType().equalsIgnoreCase("ABS")) {
					logger.info("Response from capillary...Coupon is of type Amount-ABSOLUTE");
					TransactionDiscountByAmountIfc amountDiscount = DomainGateway.getFactory()
							.getTransactionDiscountByAmountInstance();
				
					amountDiscount.setDiscountAmount(couponAmount);
					amountDiscount.setAssignmentBasis(MAXDiscountRuleConstantsIfc.ASSIGNMENT_CAPILLARYCOUPON);
					amountDiscount.setDiscountMethod(DiscountRuleConstantsIfc.DISCOUNT_METHOD_AMOUNT);
					amountDiscount.setTypeCode(DiscountRuleConstantsIfc.DISCOUNT_METHOD_AMOUNT);
					if (!discountCoupon.getReasonCode().equals("")) {
						amountDiscount.setReasonCode(Integer.parseInt(discountCoupon.getReasonCode()));
					}
					//chnages for rev 1.2 
					HashMap map = ((MAXTransactionDiscountByAmountStrategy) amountDiscount).getCapillaryCoupon();
					map.put(MAXCodeConstantsIfc.CAPILLARY_COUPON_NUM, discountCoupon.getCouponNumber());
					map.put(MAXCodeConstantsIfc.CAPILLARY_COUPON_CAMPAIGN_ID, discountCoupon.getCampaignId());
					map.put(MAXCodeConstantsIfc.CAPILLARY_COUPON_REASON_CODE, discountCoupon.getReasonCode());
					map.put(MAXCodeConstantsIfc.CAPILLARY_COUPON_DISCOUNT_TYPE, discountCoupon.getDiscountType());
					((MAXTransactionDiscountByAmountStrategy) amountDiscount).setCapillaryCoupon(map);
					amountDiscount.setIncludedInBestDeal(false);
					if (trans != null) {
						trans.addTransactionDiscount(amountDiscount);
						logger.info("Transaction level coupon applied");
						logger.info("Transaction amount after discount" + trans.getTransactionTotals().getBalanceDue());

					}
				} else {
					logger.info("Transaction level coupon cannot be applied as Coupon response is invalid ");
					displayInvalidNumberMessage(uiManager);
					return;
				}
			} else {
				// transaction total is less than min threshold
				logger.info("Transaction level coupon cannot be applied as transaction total is less than minimum threshold amount");
				displayInvalidAmountMessage(uiManager);
				return;

			}
		}

		else if (discountCoupon.getDiscountOn().equalsIgnoreCase("ITEM")) {
			logger.info("Response from capillary...Coupon is of type Transaction-ITEM");
			if (cargo.getTransaction() instanceof SaleReturnTransactionIfc) {

				SaleReturnLineItemIfc[] alllineItems = (SaleReturnLineItemIfc[]) cargo.getTransaction().getLineItems();
				double eligbleItemsTotal = 0.0;
				SaleReturnLineItemIfc[] allowedlineItems = null;
				SaleReturnLineItemIfc[] checkForEligibleItems = checkForEligibleItems(alllineItems);

				if (discountCoupon.isDiscountOnDiscountedItems() == false
						&& trans.getTransactionTotals().getDiscountTotal().getDoubleValue() == 0.0
						&& trans.getAdvancedPricingDiscountTotal().getDoubleValue() != 0.0d) {
					if (checkForEligibleItems.length == 0) {
						displayHierarchyErrorMessage(uiManager);
						return;
					}
					allowedlineItems = checkForDiscountAllowedItems(checkForEligibleItems);
				} else {
					allowedlineItems = checkForDiscountAllowedItems(alllineItems);
				}

				// to check item hierarchy whether dept,company,brand etc
				SaleReturnLineItemIfc[] eligbleLineItems = getDiscountEligbleItems(allowedlineItems, discountCoupon);
				if (eligbleLineItems.length == 0) {
					itemInEligibleMessage(uiManager);
					return;
				}
				// to check for minimum threshold
				for (int i = 0; i < eligbleLineItems.length; i++) {
					eligbleItemsTotal = eligbleItemsTotal
							+ eligbleLineItems[i].getExtendedDiscountedSellingPrice().getDoubleValue();
				}
				// check for eligible item threshold
				if (eligbleItemsTotal >= discountCoupon.getMinThresholdValue().doubleValue()) {

					CurrencyIfc remainingTotal = (CurrencyIfc) couponAmount.clone();
					remainingTotal.setZero();
					// this is used to prorate the discount when response is ABS
					CurrencyIfc remainingDiscountedTotal = (CurrencyIfc) couponAmount.clone();
					remainingDiscountedTotal.setZero();

					SaleReturnLineItemIfc[] cloneLineItems = new SaleReturnLineItemIfc[eligbleLineItems.length];
					for (int x = 0; x < eligbleLineItems.length; x++) {
						if (eligbleLineItems[x] != null) {
							cloneLineItems[x] = (SaleReturnLineItemIfc) eligbleLineItems[x].clone();
							remainingTotal = remainingTotal.add(cloneLineItems[x].getExtendedSellingPrice().abs());
							// this is used to prorate the discount when
							// response is ABS
							remainingDiscountedTotal = remainingDiscountedTotal.add(cloneLineItems[x]
									.getExtendedDiscountedSellingPrice().abs());
						}

					}
					if (discountCoupon.getDiscountType() != null
							&& discountCoupon.getDiscountType().equalsIgnoreCase("PERC")) {

						if (eligbleLineItems.length > 0) {
							CurrencyIfc totalDiscSameCoupon = DomainGateway.getBaseCurrencyInstance();
							for (int y = 0; y < eligbleLineItems.length; y++) {
								if (eligbleLineItems[y] != null) {
									MAXItemDiscountStrategyIfc sgy =(MAXItemDiscountByPercentageIfc) (((MAXDomainObjectFactory)(DomainGateway
											.getFactory()))
									.getItemDiscountByPercentageInstance());
									sgy.setDiscountRate(couponPercentage);
									sgy.setAssignmentBasis(MAXDiscountRuleConstantsIfc.ASSIGNMENT_CAPILLARYCOUPON);
									sgy.setDiscountMethod(DiscountRuleConstantsIfc.DISCOUNT_METHOD_PERCENTAGE);
									sgy.setTypeCode(DiscountRuleConstantsIfc.DISCOUNT_METHOD_PERCENTAGE);
									//sgy.setReasonCode(Integer.parseInt(discountCoupon.getReasonCode()));
									sgy.setIncludedInBestDeal(false);
									HashMap map = sgy.getCapillaryCoupon();
									map.put(MAXCodeConstantsIfc.CAPILLARY_COUPON_NUM, discountCoupon.getCouponNumber());
									map.put(MAXCodeConstantsIfc.CAPILLARY_COUPON_CAMPAIGN_ID,
											discountCoupon.getCampaignId());
									map.put(MAXCodeConstantsIfc.CAPILLARY_COUPON_REASON_CODE,
											discountCoupon.getReasonCode());

									map.put(MAXCodeConstantsIfc.CAPILLARY_COUPON_DISCOUNT_TYPE,
											discountCoupon.getDiscountType());
									sgy.setCapillaryCoupon(map);

									SaleReturnLineItemIfc item = (SaleReturnLineItemIfc) eligbleLineItems[y];
									item.addItemDiscount(sgy);
									totalDiscSameCoupon = totalDiscSameCoupon.add(sgy.getDiscountAmount());
									item.calculateLineItemPrice();
									eligbleLineItems[y] = item;
									if (cargo.getTransaction() != null) {
										cargo.getTransaction()
												.getTransactionTotals()
												.updateTransactionTotals(trans.getItemContainerProxy().getLineItems(),
														trans.getItemContainerProxy().getTransactionDiscounts(),
														trans.getItemContainerProxy().getTransactionTax());
									}

								} else {
									itemInEligibleMessage(uiManager);
									return;
								}
							}
							discountCoupon.setCouponDiscountAmountByPerc(new Double(totalDiscSameCoupon
									.getDoubleValue()));
						} else {
							itemInEligibleMessage(uiManager);
							return;
						}

					} else if (discountCoupon.getDiscountType() != null
							&& discountCoupon.getDiscountType().equalsIgnoreCase("ABS")) {
						SaleReturnLineItemIfc[] cloneLineItemsNew = new SaleReturnLineItemIfc[eligbleLineItems.length];

						if (eligbleLineItems.length > 0) {
							for (int y = 0; y < eligbleLineItems.length; y++) {
								if (eligbleLineItems[y] != null) {
									cloneLineItemsNew[y] = (SaleReturnLineItemIfc) eligbleLineItems[y].clone();

									SaleReturnLineItemIfc item = (SaleReturnLineItemIfc) eligbleLineItems[y];
									CurrencyIfc currentPrice = (CurrencyIfc) item.getExtendedDiscountedSellingPrice()
											.clone();
									CurrencyIfc remainingDiscount = (CurrencyIfc) couponAmount.clone();
									CurrencyIfc itemDiscount = currentPrice.prorate(remainingDiscount,
											remainingDiscountedTotal);
									MAXItemDiscountStrategyIfc igy = (MAXItemDiscountByAmountStrategy) DomainGateway
											.getFactory().getItemDiscountByAmountInstance();
									igy.setDiscountAmount(itemDiscount);
									igy.setAssignmentBasis(MAXDiscountRuleConstantsIfc.ASSIGNMENT_CAPILLARYCOUPON);
									igy.setDiscountMethod(DiscountRuleConstantsIfc.DISCOUNT_METHOD_AMOUNT);
									igy.setTypeCode(DiscountRuleConstantsIfc.DISCOUNT_METHOD_AMOUNT);
									igy.setIncludedInBestDeal(false);
									//changes for rev 1.2 
									//igy.setReasonCode(Integer.parseInt(discountCoupon.getReasonCode()));
									HashMap map = igy.getCapillaryCoupon();
									map.put(MAXCodeConstantsIfc.CAPILLARY_COUPON_NUM, discountCoupon.getCouponNumber());
									map.put(MAXCodeConstantsIfc.CAPILLARY_COUPON_CAMPAIGN_ID,
											discountCoupon.getCampaignId());
									map.put(MAXCodeConstantsIfc.CAPILLARY_COUPON_REASON_CODE,
											discountCoupon.getReasonCode());
									map.put(MAXCodeConstantsIfc.CAPILLARY_COUPON_DISCOUNT_TYPE,
											discountCoupon.getDiscountType());
									igy.setCapillaryCoupon(map);
									item.addItemDiscount(igy);
									item.calculateLineItemPrice();
									eligbleLineItems[y] = item;

								} else {
									itemInEligibleMessage(uiManager);
									return;
								}
							}

							if (cargo.getTransaction() != null) {
								cargo.getTransaction()
										.getTransactionTotals()
										.updateTransactionTotals(trans.getItemContainerProxy().getLineItems(),
												trans.getItemContainerProxy().getTransactionDiscounts(),
												trans.getItemContainerProxy().getTransactionTax());
							}
						} else {
							itemInEligibleMessage(uiManager);
							return;
						}
					}

					else {
						logger.info("Item level coupon cannot be applied as Coupon response is invalid ");
						displayInvalidNumberMessage(uiManager);
						return;
					}

				} else {
					// transaction total is less than min threshold
					logger.info("Item level coupon cannot be applied as Items total is less than minimum threshold amount");
					displayInvalidAmountMessage(uiManager);
					return;
				}
			}

		} else {
			displayInvalidNumberMessage(uiManager);
			return;
		}
		trans.addCapillaryCouponsApplied(discountCoupon);

		displayDiscountSuccessMessage(trans, couponNumber, uiManager);
		// bus.mail(CommonLetterIfc.OK);

		/*
		 * else { displayCouponAlreadyUsedMessage(uiManager); return; }
		 */

	}

	public boolean isCouponUnique(Vector couponApplied, String campaignId) {
		boolean isUnique = false;
		Iterator i = couponApplied.iterator();
		while (i.hasNext()) {
			MAXDiscountCouponIfc dCoupon = (MAXDiscountCouponIfc) i.next();
			if (dCoupon.getCampaignId().equalsIgnoreCase(campaignId))

			{
				isUnique = true;
				break;
			}
		}
		return isUnique;
	}

	public SaleReturnLineItemIfc[] getDiscountEligbleItems(SaleReturnLineItemIfc[] lineItems,
			MAXDiscountCouponIfc discountCoupon) {

		ArrayList eligbleLineItemsNew = new ArrayList();

		List itemCompany = discountCoupon.getCompany();
		List itemGroup = discountCoupon.getGroup();
		List itemDivision = discountCoupon.getDivision();
		List itemDepartment = discountCoupon.getDepartment();
		List tempitemBrand = discountCoupon.getBrand();
		List itemBrand = new ArrayList();
		List itemClass = discountCoupon.getClass1();
		List itemSubClass = discountCoupon.getSubClass();
	    List skuList =discountCoupon.getSkuList();

		// just to get the brand name of size 10 only.
		Iterator iter = tempitemBrand.iterator();
		while (iter.hasNext()) {
			String brand = iter.next().toString();
			String brandvalue;
			if (brand.length() > 10) {

				brandvalue = brand.substring(0, 10);
			} else {
				brandvalue = brand;

			}
			itemBrand.add(brandvalue);
		}
		int discountScenario = 0;

		if (!itemCompany.isEmpty())
			discountScenario = 1;

		else if (!itemDivision.isEmpty())
			discountScenario = 2;

		else if (!itemGroup.isEmpty())
			discountScenario = 3;

		else if (!itemDepartment.isEmpty())
			discountScenario = 4;

		else if (!itemClass.isEmpty())
			discountScenario = 5;

		else if (!itemSubClass.isEmpty())
			discountScenario = 6;

		else if (!itemBrand.isEmpty())
			discountScenario = 7;
		else if (!skuList.isEmpty())
			discountScenario = 8;		
		else if (!itemBrand.isEmpty() && !itemDepartment.isEmpty() && !itemGroup.isEmpty() && !itemDivision.isEmpty()
				&& !itemCompany.isEmpty() && !itemClass.isEmpty() && !itemSubClass.isEmpty() && !skuList.isEmpty())
			discountScenario = 9;

		else if (!itemBrand.isEmpty() && itemDepartment.isEmpty() && itemGroup.isEmpty() && itemDivision.isEmpty()
				&& itemCompany.isEmpty() && itemClass.isEmpty() && itemSubClass.isEmpty() && skuList.isEmpty())

			discountScenario = 7;

		else
			discountScenario = 9;

		switch (discountScenario) {
		case 1:
			for (int i = 0; i < lineItems.length; i++) {

				if (!itemBrand.isEmpty()) {
					String brand = ((MAXPLUItemIfc) lineItems[i].getPLUItem()).getBrandName();
					if (itemBrand.contains(brand))
						eligbleLineItemsNew.add(lineItems[i]);
				} else
					eligbleLineItemsNew.add(lineItems[i]);
			}
			return verifiedEligbleItems(eligbleLineItemsNew);
		case 2:
			for (int i = 0; i < lineItems.length; i++) {
				String div = ((MAXPLUItemIfc) lineItems[i].getPLUItem()).getItemDivision();

				if (!itemBrand.isEmpty()) {
					String brand = ((MAXPLUItemIfc) lineItems[i].getPLUItem()).getBrandName();
					if (itemDivision.contains(div) && itemBrand.contains(brand))
						eligbleLineItemsNew.add(lineItems[i]);

				} else {
					if (itemDivision.contains(div))
						eligbleLineItemsNew.add(lineItems[i]);
				}
			}
			return verifiedEligbleItems(eligbleLineItemsNew);

		case 3:
			for (int i = 0; i < lineItems.length; i++) {
				String group = ((MAXPLUItemIfc) lineItems[i].getPLUItem()).getItemGroup();

				if (!itemBrand.isEmpty()) {
					String brand = ((MAXPLUItemIfc) lineItems[i].getPLUItem()).getBrandName();
					if (itemGroup.contains(group) && itemBrand.contains(brand))
						eligbleLineItemsNew.add(lineItems[i]);

				} else {
					if (itemGroup.contains(group))
						eligbleLineItemsNew.add(lineItems[i]);
				}
			}
			return verifiedEligbleItems(eligbleLineItemsNew);

		case 4:
			for (int i = 0; i < lineItems.length; i++) {
				String dept = lineItems[i].getPLUItem().getDepartmentID();

				if (!itemBrand.isEmpty()) {
					String brand = ((MAXPLUItemIfc) lineItems[i].getPLUItem()).getBrandName();

					if (itemDepartment.contains(dept) && itemBrand.contains(brand))
						eligbleLineItemsNew.add(lineItems[i]);

				} else {
					if (itemDepartment.contains(dept))
						eligbleLineItemsNew.add(lineItems[i]);
				}
			}
			return verifiedEligbleItems(eligbleLineItemsNew);

		case 5:
			for (int i = 0; i < lineItems.length; i++) {
				String classid = ((MAXPLUItemIfc) lineItems[i].getPLUItem()).getClassId();
				String classs = classid.substring(classid.length() - 4, classid.length());
				if (!itemBrand.isEmpty()) {
					String brand = ((MAXPLUItemIfc) lineItems[i].getPLUItem()).getBrandName();

					if (itemClass.contains(classs) && itemBrand.contains(brand))
						eligbleLineItemsNew.add(lineItems[i]);

				} else {
					if (itemClass.contains(classs))
						eligbleLineItemsNew.add(lineItems[i]);
				}
			}
			return verifiedEligbleItems(eligbleLineItemsNew);

		case 6:
			for (int i = 0; i < lineItems.length; i++) {
				String subclass = "";
				String subClassId = ((MAXPLUItemIfc) lineItems[i].getPLUItem()).getSubClass();
				if (subClassId != null && !subClassId.equals(""))
					subclass = subClassId.substring(subClassId.length() - 4, subClassId.length());

				if (!itemBrand.isEmpty()) {
					String brand = ((MAXPLUItemIfc) lineItems[i].getPLUItem()).getBrandName();

					if (itemSubClass.contains(subclass) && itemBrand.contains(brand))
						eligbleLineItemsNew.add(lineItems[i]);

				} else {
					if (itemSubClass.contains(subclass))
						eligbleLineItemsNew.add(lineItems[i]);
				}
			}
			return verifiedEligbleItems(eligbleLineItemsNew);

		case 7:
			for (int i = 0; i < lineItems.length; i++) {
				String brand = ((MAXPLUItemIfc) lineItems[i].getPLUItem()).getBrandName();
				if (itemBrand.contains(brand))
					eligbleLineItemsNew.add(lineItems[i]);
			}
			return verifiedEligbleItems(eligbleLineItemsNew);
		case 8:
			for (int i = 0; i < lineItems.length; i++) 
				for (int j=0 ; j< skuList.size(); j++)
				{
					if (lineItems[i].getItemID().equals(skuList.get(j).toString().trim()))
						eligbleLineItemsNew.add(lineItems[i]);
			}
			return verifiedEligbleItems(eligbleLineItemsNew);
		default:

			return verifiedEligbleItems(eligbleLineItemsNew);
		}

	}

	// Checking the eligible already not discounted items :
	public SaleReturnLineItemIfc[] checkForEligibleItems(SaleReturnLineItemIfc[] lineItems) {
		List eligibleLineItems = new ArrayList();

		for (int i = 0; i < lineItems.length; i++) {
			if (lineItems[i].getItemDiscountTotal().getDoubleValue() == 0.00) {
				eligibleLineItems.add(lineItems[i]);
			}
		}
		return ((SaleReturnLineItemIfc[]) (eligibleLineItems.toArray(new SaleReturnLineItemIfc[0])));
	}

	public SaleReturnLineItemIfc[] checkForDiscountAllowedItems(SaleReturnLineItemIfc[] lineItems) {
		List allowableLineItems = new ArrayList();

		int disallowedItems = 0;
		for (int i = 0; i < lineItems.length; i++) {
			if (!isDiscountAllowed(lineItems[i])) {
				disallowedItems++;
			} else {
				allowableLineItems.add(lineItems[i]);
			}

		}
		return ((SaleReturnLineItemIfc[]) (allowableLineItems.toArray(new SaleReturnLineItemIfc[0])));
	}

	public boolean isDiscountAllowed(SaleReturnLineItemIfc srli) {
		return DiscountUtility.isDiscountAllowed(srli, true);
	}

	public SaleReturnLineItemIfc[] verifiedEligbleItems(ArrayList eligbleLineItemsNew) {
		return ((SaleReturnLineItemIfc[]) (eligbleLineItemsNew.toArray(new SaleReturnLineItemIfc[0])));
	}

	/**
	 * Returns true if a coupon is already used in this transaction;false
	 * otherwise.
	 * 
	 * @param transaction
	 * @return boolean
	 */
	public boolean isCouponAlreadyRedeemed(MAXSaleReturnTransactionIfc transaction, String newCoupon) {
		Vector discountCouponVector = transaction.getCapillaryCouponsApplied();
		if (!discountCouponVector.isEmpty()) {
			Object[] appliedCoupons = discountCouponVector.toArray();
			for (int i = 0; i < appliedCoupons.length; i++)
				if (newCoupon.equals(((MAXDiscountCouponIfc) appliedCoupons[i]).getCouponNumber()))
					return true;
			return false;
		}
		return false;
	}

}