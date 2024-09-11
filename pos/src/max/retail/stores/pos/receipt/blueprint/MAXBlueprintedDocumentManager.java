/* ===========================================================================
 * Copyright (c) 2016 MAX Hyper Market Inc.    All Rights Reserved.
 * Rev 1.7    20 Sept 2018    Till pickup unexpected error
 * Rev 1.6    05 April 2016    Kritica Agarwal GST Changes
 * Rev 1.5  hitesh dua   19 Apr,2017 
 * removed extra code of employee discount
 * 
 * Rev 1.4 Nitika Arora     05 Apr,2017
 * Changes done for post void of Till pickup issue.
 * 
 * Rev 1.3 Hitesh.dua 		29Mar,2017	
 * bug_fix : On receipt discount should only appear on target item. In system it will be prorate among source and target items.. 
 * 
 * Rev 1.2   Nitika Arora   22 Mar 2017      
 * Code done for EComPrepaid and EComCOD tender functionality.
 *
 * Rev 1.1 Nitika Arora     01 Mar 2017
 * Changes for printing the csp mrp difference on discount column of receipt.
 *
 * Rev 1.0 Hitesh.dua 		15dec,2016	Initial revision.
 * Changes for printing customized receipt. 
 * ===========================================================================
 */
package max.retail.stores.pos.receipt.blueprint;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

import org.apache.log4j.Logger;

import max.retail.stores.common.data.MAXTAXUtils;
import max.retail.stores.domain.lineitem.MAXSaleReturnLineItem;
import max.retail.stores.domain.lineitem.MAXSaleReturnLineItemIfc;
import max.retail.stores.domain.transaction.MAXLayawayTransaction;
import max.retail.stores.domain.transaction.MAXSaleReturnTransaction;
import max.retail.stores.pos.receipt.MAXReceiptParameterBean;
import max.retail.stores.pos.receipt.MAXReceiptParameterBeanIfc;
import max.retail.stores.pos.receipt.MAXReceiptTypeConstantsIfc;
import oracle.retail.stores.commerceservices.common.currency.AbstractCurrency;
import oracle.retail.stores.commerceservices.common.currency.CurrencyIfc;
import oracle.retail.stores.common.context.BeanLocator;
import oracle.retail.stores.common.parameter.ParameterConstantsIfc;
import oracle.retail.stores.common.utility.LocaleMap;
import oracle.retail.stores.domain.DomainGateway;
import oracle.retail.stores.domain.discount.DiscountRuleIfc;
import oracle.retail.stores.domain.discount.ItemDiscountStrategyIfc;
import oracle.retail.stores.domain.lineitem.AbstractTransactionLineItemIfc;
import oracle.retail.stores.domain.lineitem.ItemContainerProxy;
import oracle.retail.stores.domain.lineitem.SaleReturnLineItemIfc;
import oracle.retail.stores.domain.tender.TenderGiftCertificateIfc;
import oracle.retail.stores.domain.transaction.LayawayPaymentTransaction;
import oracle.retail.stores.domain.transaction.RetailTransactionIfc;
import oracle.retail.stores.domain.transaction.TenderableTransactionIfc;
import oracle.retail.stores.domain.transaction.TillAdjustmentTransaction;
import oracle.retail.stores.domain.transaction.TransactionIfc;
import oracle.retail.stores.domain.transaction.VoidTransaction;
import oracle.retail.stores.domain.utility.GiftCertificateDocument;
import oracle.retail.stores.domain.utility.LocaleConstantsIfc;
import oracle.retail.stores.foundation.manager.device.DeviceException;
import oracle.retail.stores.foundation.manager.ifc.ParameterManagerIfc;
import oracle.retail.stores.foundation.manager.parameter.ParameterException;
import oracle.retail.stores.foundation.tour.gate.Gateway;
import oracle.retail.stores.foundation.tour.service.SessionBusIfc;
import oracle.retail.stores.foundation.utility.Util;
import oracle.retail.stores.pos.device.POSDeviceActions;
import oracle.retail.stores.pos.receipt.PrintableDocumentException;
import oracle.retail.stores.pos.receipt.PrintableDocumentManagerIfc;
import oracle.retail.stores.pos.receipt.PrintableDocumentParameterBeanIfc;
import oracle.retail.stores.pos.receipt.ReceiptParameterBeanIfc;
import oracle.retail.stores.pos.receipt.blueprint.BlueprintedDocumentManager;
import oracle.retail.stores.pos.receipt.blueprint.BlueprintedFiscalReceipt;
import oracle.retail.stores.pos.receipt.blueprint.BlueprintedReceipt;
import oracle.retail.stores.receipts.model.Blueprint;

/**
 * A printable document manager that uses {@link Blueprint}s to print the
 * desired receipt or report. The blueprints are retrieved from the file system
 * and cached for reuse.
 * 
 * @author cgreene
 * @since 13.1
 */
public class MAXBlueprintedDocumentManager extends BlueprintedDocumentManager
{
    /** debug logger */
    private static final Logger logger = Logger.getLogger(MAXBlueprintedDocumentManager.class);
    
    private StringBuilder previewText;
    
	/**
	 * override method to reflect the values of parameters on receipt.
	 *
	 *
	 * @param bus
	 * @param transaction
	 * @return An instance of the ReceiptParameterBean initialized for the given
	 *         transaction.
	 * @throws ParameterException
	 */
	@Override
	public MAXReceiptParameterBean getReceiptParameterBeanInstance(
			SessionBusIfc bus, TenderableTransactionIfc transaction)
			throws ParameterException {
		
		// locale should be set by Spring

		MAXReceiptParameterBean receiptParameter = (MAXReceiptParameterBean) super
				.getReceiptParameterBeanInstance(bus, transaction);
		if(transaction.getTransactionType() == TransactionIfc.TYPE_SALE ){
			receiptParameter.setDuplicateReceipt(transaction.isDuplicateReceipt());
		}
		//changes for rev 1.3 start
		TenderableTransactionIfc transaction1 = (TenderableTransactionIfc) transaction
				.clone();
		changesForReceipt(transaction1);
		receiptParameter.setTransaction(transaction1);
		//changes for rev 1.3 end
		//Changes start for Rev 1.4(Added instance check of TillAdjustmentTransaction)
		if (transaction1 instanceof RetailTransactionIfc
				&& !(transaction1 instanceof VoidTransaction && ((VoidTransaction) transaction1)
						.getOriginalTransaction() instanceof TillAdjustmentTransaction))
			for (Enumeration e = ((RetailTransactionIfc) transaction1)
					.getLineItemsVector().elements(); e.hasMoreElements();) {
			MAXSaleReturnLineItemIfc item = (MAXSaleReturnLineItemIfc) e.nextElement();
			receiptParameter.setDiscountColumn(item);
			}
		ParameterManagerIfc pm = (ParameterManagerIfc) bus
				.getManager(ParameterManagerIfc.TYPE);
			// Read Group Like Items on Receipt parameter value.
		receiptParameter.setGroupLikeItems(pm.getBooleanValue(ParameterConstantsIfc.PRINTING_GroupLikeItemsOnReceipt));

		//loyalty tender slip
		receiptParameter
		.setLoyaltyPointsSlipFooter(pm.getStringValues(MAXParameterConstantsIfc.PRINTING_LoyaltyPointsSlipFooter));
	
		//receipt footer
		receiptParameter.setReceiptFooter(pm.getStringValues(MAXParameterConstantsIfc.PRINTING_ReceiptFooter));
		//Changes start for Rev 1.2 starts
		receiptParameter.setPrintAddDetailsEcomReceipt(pm.getBooleanValue(MAXParameterConstantsIfc.PRINTING_PrintAdditionalDetailsForEComOrder));
		//Changes start for Rev 1.2 ends
		
		//Change for Rev 1.6 : Starts
		if(transaction1 instanceof RetailTransactionIfc){
			MAXTAXUtils.getItemTaxType((RetailTransactionIfc) transaction1);
			}
	    	 receiptParameter.printTaxSummaryDetails();
		//Change for Rev 1.6 : Ends
		return receiptParameter;
	}
	
	//chnages for rev 1.3 start
	
	private void changesForReceipt(TenderableTransactionIfc transaction1) {
		// added for printing discounts on target
		// print the line item detail
		ArrayList discountAppliedTargets1 = new ArrayList();
		discountAppliedTargets1.clear();
		int counttgtItems2 = 0;
		int countsrcItems2 = 0;
		List bdw2 = new ArrayList();
		CurrencyIfc disOnTgt2 = null;
		CurrencyIfc totaldiscountonTarget2 = null;
		CurrencyIfc srcDiscount2 = null;
		CurrencyIfc tgtDiscount2 = null;
		CurrencyIfc totalDiscount2 = null;
		// izharDR
		if (transaction1 instanceof MAXSaleReturnTransaction)
			bdw2 = ((MAXSaleReturnTransaction) transaction1).getBdwList();
		if (transaction1 instanceof MAXLayawayTransaction) {
			bdw2 = ((MAXLayawayTransaction) transaction1).getBdwList();

		}
		if (bdw2.size() > 0) {

			// continue with Max customized flow
			// for loop introduced for iteration when more then one discount
			// rules are present in a transaction
			for (int z1 = 0; z1 < bdw2.size(); z1++) {
				discountAppliedTargets1.clear();
				srcDiscount2 = null;
				tgtDiscount2 = null;
				totalDiscount2 = null;
				counttgtItems2 = 0;
				countsrcItems2 = 0;
				String[] s1 = bdw2.get(z1).toString().split("_");

				String promoId1 = s1[2];

				for (Enumeration e = ((RetailTransactionIfc) transaction1)
						.getLineItemsVector().elements(); e.hasMoreElements();) {
					MAXSaleReturnLineItemIfc item = (MAXSaleReturnLineItemIfc) e
							.nextElement();
					{

						// this loop is for deal discount rules
						if (((MAXSaleReturnLineItemIfc) item)
								.getBestDealWinnerName() != null
								&& item.getAdvancedPricingDiscount() != null
								&& item.getAdvancedPricingDiscount()
										.getRuleID().equalsIgnoreCase(promoId1)
								&& (item.getAdvancedPricingDiscount()
										.getReasonCode() == 1
										|| item.getAdvancedPricingDiscount()
												.getReasonCode() == 2
										|| item.getAdvancedPricingDiscount()
												.getReasonCode() == 3
										|| item.getAdvancedPricingDiscount()
												.getReasonCode() == 7
										|| item.getAdvancedPricingDiscount()
												.getReasonCode() == 8
										|| item.getAdvancedPricingDiscount()
												.getReasonCode() == 9
										|| item.getAdvancedPricingDiscount()
												.getReasonCode() == 10
										|| item.getAdvancedPricingDiscount()
												.getReasonCode() == 11
										|| item.getAdvancedPricingDiscount()
												.getReasonCode() == 23
										|| item.getAdvancedPricingDiscount()
												.getReasonCode() == 24 || item
										.getAdvancedPricingDiscount()
										.getReasonCode() == 25)) {
							// when item is source
							if (!((MAXSaleReturnLineItemIfc) item)
									.isTargetIdentifier()) {
								countsrcItems2 = countsrcItems2 + 1;
								if (srcDiscount2 == null)
									srcDiscount2 = item
											.getAdvancedPricingDiscount()
											.getDiscountAmount();
								else
									srcDiscount2 = srcDiscount2.add(item
											.getAdvancedPricingDiscount()
											.getDiscountAmount());

							} else {
								// when item is target
								discountAppliedTargets1.add(item);
								counttgtItems2 = counttgtItems2 + 1;
								if (tgtDiscount2 == null)
									tgtDiscount2 = item
											.getAdvancedPricingDiscount()
											.getDiscountAmount();

								else
									tgtDiscount2 = tgtDiscount2.add(item
											.getAdvancedPricingDiscount()
											.getDiscountAmount());
							}
						}
						// loop added for counting items for rules which dont
						// hav target
						if (((MAXSaleReturnLineItemIfc) item)
								.getBestDealWinnerName() != null
								&& item.getAdvancedPricingDiscount() != null
								&& item.getAdvancedPricingDiscount()
										.getRuleID().equalsIgnoreCase(promoId1)
								&& (item.getAdvancedPricingDiscount()
										.getReasonCode() == 4
										|| item.getAdvancedPricingDiscount()
												.getReasonCode() == 5
										|| item.getAdvancedPricingDiscount()
												.getReasonCode() == 6
										|| item.getAdvancedPricingDiscount()
												.getReasonCode() == 12
										|| item.getAdvancedPricingDiscount()
												.getReasonCode() == 13
										|| item.getAdvancedPricingDiscount()
												.getReasonCode() == 14
										|| item.getAdvancedPricingDiscount()
												.getReasonCode() == 15
										|| item.getAdvancedPricingDiscount()
												.getReasonCode() == 16 || item
										.getAdvancedPricingDiscount()
										.getReasonCode() == 17)) {

							if (totalDiscount2 == null)
								totalDiscount2 = item
										.getAdvancedPricingDiscount()
										.getDiscountAmount();
							else
								totalDiscount2 = totalDiscount2.add(item
										.getAdvancedPricingDiscount()
										.getDiscountAmount());
							item.setPromoDiscountForReceipt(item
									.getAdvancedPricingDiscount()
									.getDiscountAmount());
						}

					}

				}
				CurrencyIfc runningTotal = ItemContainerProxy
						.calculateTotalSellingPrice(discountAppliedTargets1);
				if (srcDiscount2 != null && tgtDiscount2 != null) {
					// sum total of the discount on both source and target
					BigDecimal bd = new BigDecimal(counttgtItems2);
					totaldiscountonTarget2 = srcDiscount2.add(tgtDiscount2);
					disOnTgt2 = totaldiscountonTarget2.divide(bd);

				}

				// addition ends
				for (Enumeration e = ((RetailTransactionIfc) transaction1)
						.getLineItemsVector().elements(); e.hasMoreElements();) {
					MAXSaleReturnLineItemIfc item = (MAXSaleReturnLineItemIfc) e
							.nextElement();

					List bdwList2 = ((MAXSaleReturnLineItem) item).getBdwList();
					if (bdwList2.size() > 0) {
						if (!((MAXSaleReturnLineItemIfc) item)
								.isTargetIdentifier()
								&& item.getAdvancedPricingDiscount() != null
								&& item.getAdvancedPricingDiscount()
										.getRuleID().equalsIgnoreCase(promoId1)
								&& (item.getAdvancedPricingDiscount()
										.getReasonCode() == 1
										|| item.getAdvancedPricingDiscount()
												.getReasonCode() == 2
										|| item.getAdvancedPricingDiscount()
												.getReasonCode() == 3
										|| item.getAdvancedPricingDiscount()
												.getReasonCode() == 7
										|| item.getAdvancedPricingDiscount()
												.getReasonCode() == 8
										|| item.getAdvancedPricingDiscount()
												.getReasonCode() == 9
										|| item.getAdvancedPricingDiscount()
												.getReasonCode() == 10
										|| item.getAdvancedPricingDiscount()
												.getReasonCode() == 11
										|| item.getAdvancedPricingDiscount()
												.getReasonCode() == 23
										|| item.getAdvancedPricingDiscount()
												.getReasonCode() == 24 || item
										.getAdvancedPricingDiscount()
										.getReasonCode() == 25)) {

							// comes here when item is source
							if (disOnTgt2 != null) {
								DiscountRuleIfc d = null;
								srcDiscount2 = disOnTgt2.subtract(disOnTgt2);
								tgtDiscount2 = disOnTgt2.subtract(disOnTgt2);
								ItemDiscountStrategyIfc[] discounts = item
										.getItemPrice().getItemDiscounts();// rajeev
								if (discounts != null) {
									for (int i = 0; i < discounts.length; i++) {
										d = discounts[i];
										if (d.getRuleID() != null
												&& !d.getRuleID()
														.equalsIgnoreCase("")) {
											item.setItemDiscountTotal(item
													.getItemDiscountTotal()
													.subtract(
															d.getDiscountAmount()));
											d.setDiscountAmount(srcDiscount2);
											item.getItemPrice()
													.setItemDiscountAmount(
															srcDiscount2);
											item.setPromoDiscountForReceipt(srcDiscount2);
											// here for items in source of the
											// discount rule.
											item.recalculateItemTotal();

										}
									}
								}
							}
						}
						// added for printing discounts on target
						if (((MAXSaleReturnLineItemIfc) item)
								.isTargetIdentifier()
								&& item.getAdvancedPricingDiscount() != null
								&& item.getAdvancedPricingDiscount()
										.getRuleID().equalsIgnoreCase(promoId1)
								&& (item.getAdvancedPricingDiscount()
										.getReasonCode() == 1
										|| item.getAdvancedPricingDiscount()
												.getReasonCode() == 2
										|| item.getAdvancedPricingDiscount()
												.getReasonCode() == 3
										|| item.getAdvancedPricingDiscount()
												.getReasonCode() == 7
										|| item.getAdvancedPricingDiscount()
												.getReasonCode() == 8
										|| item.getAdvancedPricingDiscount()
												.getReasonCode() == 9
										|| item.getAdvancedPricingDiscount()
												.getReasonCode() == 10
										|| item.getAdvancedPricingDiscount()
												.getReasonCode() == 11
										|| item.getAdvancedPricingDiscount()
												.getReasonCode() == 23
										|| item.getAdvancedPricingDiscount()
												.getReasonCode() == 24 || item
										.getAdvancedPricingDiscount()
										.getReasonCode() == 25)) {

							// comes here when item is target
							if (disOnTgt2 != null) {
								DiscountRuleIfc d = null;
								srcDiscount2 = disOnTgt2;
								tgtDiscount2 = disOnTgt2;
								ItemDiscountStrategyIfc[] discounts = item
										.getItemPrice().getItemDiscounts();
								if (discounts != null) {
									for (int i = 0; i < discounts.length; i++) {
										d = discounts[i];
										if (d.getRuleID() != null
												&& !d.getRuleID()
														.equalsIgnoreCase("") && !d.getDescription().equalsIgnoreCase("")) {
											d.setDiscountAmount(calculateProratedDiscount(
													item, runningTotal,
													totaldiscountonTarget2));
											item.setPromoDiscountForReceipt(d
													.getDiscountAmount());
											item.getItemPrice()
													.setItemDiscountAmount(
															d.getDiscountAmount());
											item.recalculateItemTotal();
										}
									}
								}
							}
						}

						if (item.getAdvancedPricingDiscount() != null
								&& item.getAdvancedPricingDiscount()
										.getRuleID().equalsIgnoreCase(promoId1)
								&& (item.getAdvancedPricingDiscount()
										.getReasonCode() == 22)) {
							item.setPromoDiscountForReceipt(item.getItemPrice()
									.getItemDiscountAmount());
						}

					}
				}
			}
		} else {
			if (!(transaction1 instanceof LayawayPaymentTransaction)) { // /
				//Changes start for Rev 1.4(Added instance check of TillAdjustmentTransaction)
				if (!(transaction1 instanceof VoidTransaction && (((VoidTransaction) transaction1)
						.getOriginalTransaction() instanceof LayawayPaymentTransaction || ((VoidTransaction)transaction1).getOriginalTransaction() instanceof TillAdjustmentTransaction))) {
					// Bug fix: Till pickup unexpected error , changes starts by Bhanu priya gupta 
					if (transaction1 instanceof RetailTransactionIfc )
					{
					// Bug fix: Till pickup unexpected error , changes End by Bhanu priya gupta 
					for (Enumeration e = ((RetailTransactionIfc) transaction1)
							.getLineItemsVector().elements(); e
							.hasMoreElements();) {
						MAXSaleReturnLineItemIfc item = (MAXSaleReturnLineItemIfc) e
								.nextElement();
						if (item.getPromoDiscountForReceipt() != null) {
							item.getItemPrice().setItemDiscountAmount(
									item.getPromoDiscountForReceipt());
							item.recalculateItemTotal();
						}
					}
					}
				}
			}

		}

		if (transaction1 instanceof MAXSaleReturnTransaction) {
			AbstractTransactionLineItemIfc[] originalTransactionLineItems = ((MAXSaleReturnTransaction) transaction1)
					.getItemContainerProxy().getLineItems();
			for (int i = 0; i < originalTransactionLineItems.length; i++) {
				AbstractTransactionLineItemIfc[] cloneTransactionLineItems = ((MAXSaleReturnTransaction) transaction1)
						.getItemContainerProxy().getLineItems();
				CurrencyIfc promoDiscount = ((MAXSaleReturnLineItemIfc) cloneTransactionLineItems[i])
						.getPromoDiscountForReceipt();
				if (promoDiscount != null) {
					((MAXSaleReturnLineItemIfc) originalTransactionLineItems[i])
							.setPromoDiscountForReceipt(promoDiscount);
				}
				else {
					MAXSaleReturnLineItemIfc item = (MAXSaleReturnLineItemIfc) originalTransactionLineItems[i];
					if (item.getAdvancedPricingDiscount() != null
							&& item.getAdvancedPricingDiscount()
									.getDiscountAmount() != null)
						item.setPromoDiscountForReceipt(item
								.getAdvancedPricingDiscount()
								.getDiscountAmount());
				}
			}
		}

		else if (transaction1 instanceof MAXLayawayTransaction) {
			AbstractTransactionLineItemIfc[] originalTransactionLineItems = ((MAXLayawayTransaction) transaction1)
					.getItemContainerProxy().getLineItems();
			for (int i = 0; i < originalTransactionLineItems.length; i++) {
				AbstractTransactionLineItemIfc[] cloneTransactionLineItems = ((MAXLayawayTransaction) transaction1)
						.getItemContainerProxy().getLineItems();
				CurrencyIfc promoDiscount = ((MAXSaleReturnLineItemIfc) cloneTransactionLineItems[i])
						.getPromoDiscountForReceipt();
				if (promoDiscount != null) {
					((MAXSaleReturnLineItemIfc) originalTransactionLineItems[i])
							.setPromoDiscountForReceipt(promoDiscount);
				}
				else {
					MAXSaleReturnLineItemIfc item = (MAXSaleReturnLineItemIfc) originalTransactionLineItems[i];
					if (item.getAdvancedPricingDiscount() != null
							&& item.getAdvancedPricingDiscount()
									.getDiscountAmount() != null)
						item.setPromoDiscountForReceipt(item
								.getAdvancedPricingDiscount()
								.getDiscountAmount());
				}
			}
		}
	}


	public CurrencyIfc calculateProratedDiscount(SaleReturnLineItemIfc target,
			CurrencyIfc runningTotal, CurrencyIfc discount) {
		CurrencyIfc targetTotal = target.getExtendedSellingPrice();

		// convert to big decimal values
		BigDecimal totalDecimal = new BigDecimal(runningTotal.getStringValue());
		BigDecimal discountDecimal = new BigDecimal(discount.getStringValue());
		BigDecimal targetDecimal = new BigDecimal(targetTotal.getStringValue());

		// get the percentage - use BIG_ZERO to avoid divide by zero exceptions
		BigDecimal b1 = new BigDecimal(AbstractCurrency.ZERO);
		if (runningTotal.compareTo(DomainGateway.getBaseCurrencyInstance()) != 0) {
			b1 = targetDecimal
					.divide(totalDecimal, 4, BigDecimal.ROUND_HALF_UP);
		}
		BigDecimal b2 = b1.multiply(discountDecimal);

		CurrencyIfc proratedDiscount = DomainGateway.getBaseCurrencyInstance();
		proratedDiscount.setStringValue(b2.toString());

		return proratedDiscount;
	}
	//chnages for rev 1.3 end
	public void printReceipt(SessionBusIfc bus, PrintableDocumentParameterBeanIfc receiptDataBean)
            throws PrintableDocumentException
    {
        boolean isFiscalPrintingEnabled = Gateway.getBooleanProperty("application", "FiscalPrintingEnabled", false);

        persistBean(receiptDataBean);

        BlueprintedReceipt receipt = null;

        // Print receipts using BlueprintedIFiscalReceipts if fiscal printer is configured
        if (isFiscalPrintingEnabled)
        {
            // Print the fiscal receipt
            boolean fiscalReceiptPrinted = printFiscalReceipt(bus, receiptDataBean);

            // Print the non fiscal copies of the receipt (eg. store copy)
            if(fiscalReceiptPrinted && receiptDataBean instanceof ReceiptParameterBeanIfc)
            {
                ((ReceiptParameterBeanIfc) receiptDataBean).setPrintStoreReceipt(true);
                ((ReceiptParameterBeanIfc) receiptDataBean).setAutoPrintCustomerCopy(false);
            }
            // get the blueprint for this receipt from a repository
            Blueprint blueprint = getBlueprint(receiptDataBean.getDocumentType(), receiptDataBean.getLocale(),
                    receiptDataBean.getDefaultLocale());
            
            // alter any setting on this blueprint based upon parameters
            blueprint = prepareBlueprint(blueprint, bus, receiptDataBean);

            // get a BlueprintedReceipt for this blueprint
            receipt = (BlueprintedFiscalReceipt) getBlueprintedFiscalReceipt(blueprint);
            // set data object as bean onto receipt
            
            if(receipt != null)
            {
            receipt.setParameterBean(receiptDataBean);
            
            POSDeviceActions pda = new POSDeviceActions(bus);
            try
            {
                // print non-fiscal copies
                if ((blueprint.getCopies() > 1 || !fiscalReceiptPrinted))
                {
                    pda.printDocument(receipt);
                }
                // getPreviewText() should only come after the print is done
                // the previewText attribute of Receipt is populated only after
                previewText = receipt.getPreviewText();
            }
            catch (DeviceException e)
            {
                logger.warn("Unable to print receipt. " + e.getMessage());

                if (e.getCause() != null)
                {
                    logger.warn("DeviceException caused by: " + Util.throwableToString(e.getCause()));
                }

                throw new PrintableDocumentException("DeviceException caught while attempting to print document type: "
                        + receiptDataBean.getDocumentType(), e);
            }
            }

        }
        else
        {

            // get the blueprint for this receipt from a repository
            Blueprint blueprint = getBlueprint(receiptDataBean.getDocumentType(), receiptDataBean.getLocale(),
                    receiptDataBean.getDefaultLocale());
            // alter any setting on this blueprint based upon parameters
            blueprint = prepareBlueprint(blueprint, bus, receiptDataBean);

            // get a BlueprintedReceipt for this blueprint
            receipt = getBlueprintedReceipt(blueprint);
            // set data object as bean onto receipt
            //Null check is performed
            if(receipt !=null)
            {
            receipt.setParameterBean(receiptDataBean);
            boolean vlrEnabled = Gateway.getBooleanProperty("application", "VLREnabled", false);
            
            if (vlrEnabled)
            {
                // Add properties for fixed length receipt printing
                receipt.setRepeatHeader(repeatHeader);
                receipt.setRepeatFooter(repeatFooter);
                receipt.setHeaderBlueprints(headerBlueprints);
                receipt.setFooterBlueprints(footerBlueprints);
                receipt.setFixedLengthReceipt(vlrEnabled);
            }
            
            POSDeviceActions pda = new POSDeviceActions(bus);
            try
            {
            	
                pda.printDocument(receipt);
                // getPreviewText() should only come after the print is done
                // the previewText attribute of Receipt is populated only after
                previewText = receipt.getPreviewText();
            }
            catch (DeviceException e)
            {
                logger.warn("Unable to print receipt. " + e.getMessage());
                System.out.println(e.getMessage());

                if (e.getCause() != null)
                {
                    logger.warn("DeviceException caused by: " + Util.throwableToString(e.getCause()));
                    System.out.println(e.getCause());
                }

                throw new PrintableDocumentException("DeviceException caught while attempting to print document type: "
                        + receiptDataBean.getDocumentType(), e);
            }  
        }  
        }
    }

	
}
