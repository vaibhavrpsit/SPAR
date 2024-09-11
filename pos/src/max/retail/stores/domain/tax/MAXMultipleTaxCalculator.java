/* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
 *
 * Copyright (c) 2008, 2009, Oracle and/or its affiliates.All rights reserved. 
 *
 *  ===========================================================================
 * $Log:Added as part of India Localization Changes related to Tax
 * ===========================================================================
 *
 *	Rev 1.0		May 04, 2017		Kritica Agarwal 	GST Changes
 *  * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
package max.retail.stores.domain.tax;

import java.math.BigDecimal;

import max.retail.stores.domain.lineitem.MAXItemTax;
import max.retail.stores.domain.lineitem.MAXLineItemTaxBreakUpDetailIfc;
import max.retail.stores.domain.lineitem.MAXSaleReturnLineItemIfc;
import oracle.retail.stores.commerceservices.common.currency.CurrencyIfc;
import oracle.retail.stores.domain.DomainGateway;
import oracle.retail.stores.domain.lineitem.ReturnItemIfc;
import oracle.retail.stores.domain.lineitem.SaleReturnLineItemIfc;
import oracle.retail.stores.domain.lineitem.TaxLineItemInformationIfc;
import oracle.retail.stores.domain.tax.TaxInformationIfc;
import oracle.retail.stores.foundation.tour.gate.Gateway;

import org.apache.log4j.Logger;

/**
 * Each Item will have a TaxCategory associated with it and in turn each tax
 * category will have multiple taxAssignments.
 *
 * <P>
 * This MultipleTaxCalculator uses the various Tax Factors,Taxable Factors based
 * on the TaxAssignments and Tax Inclusive Selling Retail to arrive at the
 * Multiple Taxes for an Item. It calculates the Multiple Taxes for Sale,Post Void and
 * Return of items.
 *
 * <P>
 *
 *
 * @author Oracle IDC
 * @since 12.0.1 India Localization Changes related to Tax
 */
public class MAXMultipleTaxCalculator implements MAXMultipleTaxCalculatorIfc {
	/**
	 * Tax inclusive flag
	 */
	protected boolean taxInclusiveFlag = true;

	/**
	 * The logger to which log messages will be sent.
	 */
	private static transient Logger logger = Logger
			.getLogger(max.retail.stores.domain.tax.MAXMultipleTaxCalculator.class);

	public MAXMultipleTaxCalculator() {
		super();
		taxInclusiveFlag = Gateway.getBooleanProperty("application",
				"InclusiveTaxEnabled", true);

	}

	/**
	 * Returns the Item Taxable Amount by Subtracting the Total Item tax from
	 * the Selling Retail Inclusive of Tax.
	 *
	 * @param item
	 * @return CurrencyIfc
	 */
	public CurrencyIfc getItemTaxableAmount(TaxLineItemInformationIfc item) {

		CurrencyIfc taxInclusiveSellingRetail = getTaxInclusiveSellingRetail(item);
		return taxInclusiveSellingRetail.subtract(calculateTotalItemTax(item));
	}

	/**
	 * Calculates the TotalItemTax using the TaxLineItemInformation
	 *
	 *
	 * @param item
	 * @return CurrencyIfc
	 */
	public CurrencyIfc calculateTotalItemTax(TaxLineItemInformationIfc item) {
		CurrencyIfc totalTaxAmount = DomainGateway.getBaseCurrencyInstance();
		SaleReturnLineItemIfc srli = (MAXSaleReturnLineItemIfc) item;
		//For Returns Transaction
		if (srli.getReturnItem() != null) {
			//In Returns itemTotalTaxAmount is already present we just have to recalculate and negate the Tax.
		//	if (srli.isFromTransaction()) {  // if condition commented by - Karni
		//		totalTaxAmount = calculateTotalReturnsItemTax(srli);
		//	} else {
				// If it is a manual returns then calculate from the current tax assignments
				CurrencyIfc taxInclusiveSellingRetail = getTaxInclusiveSellingRetail(item);
				MAXLineItemTaxBreakUpDetailIfc[] lineItemTaxBreakUpDetails = ((MAXSaleReturnLineItemIfc) item).getLineItemTaxBreakUpDetails();
				// Calculate tax only when the quantity is negative.
				if (srli.getItemQuantityDecimal().signum() <= 0) {
					totalTaxAmount = calculateTotalItemTax(lineItemTaxBreakUpDetails,taxInclusiveSellingRetail,srli);
				}
			//}
			//Reset the Item TaxInformation Container before adding any new TaxInformation for the item.
			item.getTaxInformationContainer().reset();
		}
		else {
			//For Sale Transaction
			CurrencyIfc taxInclusiveSellingRetail = getTaxInclusiveSellingRetail(item);
			MAXLineItemTaxBreakUpDetailIfc[] lineItemTaxBreakUpDetails = ((MAXSaleReturnLineItemIfc) item).getLineItemTaxBreakUpDetails();
			//Calculate the itemTotalTaxAmount using the lineItemTaxBreakUpDetails and SellingRetail for Sale
			totalTaxAmount = calculateTotalItemTax(lineItemTaxBreakUpDetails,taxInclusiveSellingRetail,srli);
			}
		return totalTaxAmount;
	}



	/**
	 * This method recalculates the tax when there is a return. Quantity
	 * Purchased, Returnedquantity, ReturnableQuantity and Returning Quantity
	 * are considered while recalculating the Tax.
	 *
	 * The calculation is this: 1) Find out how many items were already returned
	 * <BR>
	 * 2) Figure out the tax due from the already returned items <BR>
	 * 3) Find out the tax for the already returned items + the currently being
	 * returned items <BR>
	 * 4) Subtract the already returned total from the tax <BR>
	 *
	 * @param SaleReturnLineItemIfc
	 * @return prorated amount
	 */
	public CurrencyIfc calculateTotalReturnsItemTax(SaleReturnLineItemIfc srli) {

		CurrencyIfc totalItemTaxAmount = DomainGateway.getBaseCurrencyInstance();
		try {
			ReturnItemIfc returnItem = (ReturnItemIfc) srli.getReturnItem().clone();

			BigDecimal purchasedQuantity = returnItem.getQuantityPurchased();
			BigDecimal returnableQuantity = returnItem.getQuantityReturnable();
			BigDecimal returningQuantity = srli.getItemQuantityDecimal();
			
			if(purchasedQuantity != returningQuantity ){  // this if added for return issue - partial quantity return of single item - karni
				srli.setTaxChanged(false);
			}

			if (taxInclusiveFlag) {
				/* Rev 1.0 changes starts */
				if(! Gateway.getBooleanProperty("application",
						"GSTEnabled", true))
				{
					/* Rev 1.0 changes ends */
				totalItemTaxAmount = (CurrencyIfc) srli.getItemPrice().getItemInclusiveTaxAmount().clone();
				}
				/* Rev 1.0 changes starts */
				else
				{
					for(int index = 0; index < ((MAXItemTax)(srli.getItemPrice().getItemTax())).getLineItemTaxBreakUpDetail().length ; index++)
					{
						totalItemTaxAmount= totalItemTaxAmount.add(((MAXItemTax)(srli.getItemPrice().getItemTax())).getLineItemTaxBreakUpDetail()[index].getTaxAmount());
						srli.getItemPrice().setItemInclusiveTaxAmount(totalItemTaxAmount);
					}
				}
				/* Rev 1.0 changes ends */
			} else {
				totalItemTaxAmount = (CurrencyIfc) srli.getItemPrice().getItemTaxAmount().clone();
			}

			 // If the Tax is Changed then return the TotalItemTax as the recalculated
			if (!srli.isTaxChanged()) {
				BigDecimal quantityItemsAlreadyReturned = purchasedQuantity.subtract(returnableQuantity);
				CurrencyIfc taxAlreadyRefunded = totalItemTaxAmount.multiply(quantityItemsAlreadyReturned.abs()).divide(purchasedQuantity);
				BigDecimal quantityItemsToTax = quantityItemsAlreadyReturned.add(returningQuantity).abs();
				CurrencyIfc totalTaxToRefund = totalItemTaxAmount.multiply(quantityItemsToTax).divide(purchasedQuantity);
				CurrencyIfc netTotalTaxToRefund = totalTaxToRefund.subtract(taxAlreadyRefunded);
				totalItemTaxAmount = netTotalTaxToRefund;
				srli.setTaxChanged(true);
			}
		} catch (Exception e) {
			logger
					.warn("Some of the attributes missing in ReturnItem Object, hence returning zero TotalItemTaxAmount");
		}
		return totalItemTaxAmount;
	}

	/**
	 * Create a TaxInformationIfc object. This data is saved to the database
	 * with the transaction
	 *
	 * @param taxableAmount
	 *            Taxable amount
	 * @param taxAmount
	 *            Tax charged
	 * @param lineItemBreakUpDetails
	 *            BreakUp of Tax Details)
	 * @return TaxInformationIfc object
	 */
	public TaxInformationIfc createTaxInformation(CurrencyIfc taxableAmount,
			CurrencyIfc taxAmount,
			MAXLineItemTaxBreakUpDetailIfc[] lineItemBreakUpDetails) {
		TaxInformationIfc taxInformation = DomainGateway.getFactory()
				.getTaxInformationInstance();
		taxInformation.setTaxableAmount(taxableAmount);
		taxInformation.setTaxAmount(taxAmount);
		 ((MAXTaxInformationIfc)taxInformation).setLineItemTaxBreakUpDetails(lineItemBreakUpDetails);
		/* For India Localization tax is always Inclusive */
		taxInformation.setInclusiveTaxFlag(taxInclusiveFlag);
// Changes starts (Ashish: updating tax E when 0% in tx_asgmt table)
		/*if(taxAmount.getDecimalValue().toString().equals("0.00")){
			taxInformation.setTaxMode(1);
		}*/
// Changes ends (Ashish: updating tax E when 0% in tx_asgmt table)

		return taxInformation;
	}

	/**
	 * Get the TaxInculsive Selling Retail after discounts are applied, if Item
	 * is discountable.
	 *
	 * @param item
	 * @return taxable amount
	 */
	public CurrencyIfc getTaxInclusiveSellingRetail(
			TaxLineItemInformationIfc item) {
		CurrencyIfc retValue = DomainGateway.getBaseCurrencyInstance();

		if(item.getExtendedDiscountedSellingPrice()!=null){
			retValue = item.getExtendedDiscountedSellingPrice();
		}
		return retValue;
	}

	/**
	 * Returns the Total Item Tax Amount by Summing the taxable amount from the
	 * lineItemTaxBreakUpDetail
	 *
	 *
	 * @param item
	 * @return CurrencyIfc
	 */
	/*
	 * public CurrencyIfc calculateTotalItemTax( LineItemTaxBreakUpDetail[]
	 * lineItemTaxBreakUpDetails, CurrencyIfc taxInclusiveSellingRetail) {
	 *
	 * CurrencyIfc totalTaxAmount =DomainGateway.getBaseCurrencyInstance();
	 * BigDecimal totxamtDecimal = totalTaxAmount.getDecimalValue();
	 * totxamtDecimal.setScale(TaxConstantsIfc.TAX_CALCULATION_SCALE_FACTOR);
	 *
	 * BigDecimal
	 * txincslngreDecimal=taxInclusiveSellingRetail.getDecimalValue();
	 * txincslngreDecimal.setScale(TaxConstantsIfc.TAX_CALCULATION_SCALE_FACTOR);
	 *
	 * if (lineItemTaxBreakUpDetails != null) { for (int i = 0; i <
	 * lineItemTaxBreakUpDetails.length; i++) { CurrencyIfc taxAmount =
	 * DomainGateway.getBaseCurrencyInstance(); CurrencyIfc
	 * taxableAmount=DomainGateway.getBaseCurrencyInstance();
	 *
	 * BigDecimal txAmtDecimal = taxAmount.getDecimalValue(); BigDecimal
	 * txblAmtDecimal= taxableAmount.getDecimalValue();
	 *
	 * txAmtDecimal=txincslngreDecimal.multiply(lineItemTaxBreakUpDetails[i]
	 * .getTaxAssignment().getTaxAmountFactor());
	 *
	 * totxamtDecimal = totxamtDecimal.add(txAmtDecimal);
	 * taxAmount.setDecimalValue(txAmtDecimal);
	 * txblAmtDecimal=txincslngreDecimal.multiply(lineItemTaxBreakUpDetails[i]
	 * .getTaxAssignment().getTaxableAmountFactor());
	 * taxableAmount.setDecimalValue(txblAmtDecimal);
	 * lineItemTaxBreakUpDetails[i] .setTaxableAmount(taxableAmount);
	 * lineItemTaxBreakUpDetails[i].setTaxAmount(taxAmount);
	 *  } } totalTaxAmount.setDecimalValue(totxamtDecimal); return
	 * totalTaxAmount; }
	 */
	/**
	 * Returns the Total Item Tax Amount by Summing the tax amount from the
	 * lineItemTaxBreakUpDetail
	 *
	 *
	 * @param item
	 * @param taxInclusiveSellingRetail
	 * @return CurrencyIfc
	 */
	public CurrencyIfc calculateTotalItemTax(
			MAXLineItemTaxBreakUpDetailIfc[] lineItemTaxBreakUpDetails,
			CurrencyIfc taxInclusiveSellingRetail,SaleReturnLineItemIfc srli) {

		CurrencyIfc totalTaxAmount = DomainGateway.getBaseCurrencyInstance();
		if (lineItemTaxBreakUpDetails != null) {
			for (int i = 0; i < lineItemTaxBreakUpDetails.length; i++) {
				CurrencyIfc taxAmount = DomainGateway.getBaseCurrencyInstance();
				// If the TaxAmountFactor is null then get the ItemTaxAmount
				if (lineItemTaxBreakUpDetails[i].getTaxAssignment().getTaxAmountFactor() != null) {
					taxAmount = taxInclusiveSellingRetail.multiply(lineItemTaxBreakUpDetails[i].getTaxAssignment().getTaxAmountFactor());
					lineItemTaxBreakUpDetails[i].setTaxableAmount(taxInclusiveSellingRetail.multiply(lineItemTaxBreakUpDetails[i].getTaxAssignment().getTaxableAmountFactor()));
				} else {
					taxAmount = lineItemTaxBreakUpDetails[i].getTaxAmount();
				}
				totalTaxAmount = totalTaxAmount.add(taxAmount);
				lineItemTaxBreakUpDetails[i].setTaxAmount(taxAmount);
			}
			//If a Non-Zero Tax Amount is calculated then reset the ItemTax Information Container. - Karni - Promo tax issue fix
			if (totalTaxAmount.signum() != CurrencyIfc.ZERO||totalTaxAmount.signum() == CurrencyIfc.ZERO||taxInclusiveSellingRetail.signum()==CurrencyIfc.ZERO) {
				srli.getTaxInformationContainer().reset();
			}
			srli.setTaxChanged(true);
		}
		return totalTaxAmount;
	}
}
