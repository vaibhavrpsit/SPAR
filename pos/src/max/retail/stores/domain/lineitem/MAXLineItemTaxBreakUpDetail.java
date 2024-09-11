/* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
 *
 * Copyright (c) 2008, 2009, Oracle and/or its affiliates.All rights reserved. 
 *  
 *  ===========================================================================
 * $Log:Added as part of India Localization Changes related to Tax
 * ===========================================================================
 *  
 *  * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
package max.retail.stores.domain.lineitem;

import java.io.Serializable;
import java.math.BigDecimal;

import max.retail.stores.domain.tax.MAXTaxAssignmentIfc;
import oracle.retail.stores.commerceservices.common.currency.CurrencyIfc;
import oracle.retail.stores.domain.DomainGateway;
import oracle.retail.stores.domain.stock.PLUItem;
/**
 * Each Item will have a TaxCategory associated with it and in turn each ItemTax
 * object will have an array LineItemTaxBreakUpDetail based on the
 * TaxAssignments for that item.
 * 
 * <P>
 * Each Item will have ItemTax object, which wil be enhanced to have array of
 * LineItemTaxBreakUpDetail which will have TaxAssignment,TaxableAmount and
 * TaxAmount, which effectively represents the TaxAssignemnts for the Item. When
 * TaxAmount of all the LineItemTaxBreakUpDetail is summed it gives the total
 * item tax.
 * <P>
 * 
 * 
 * @author Oracle IDC
 * @since 12.0.1 India Localization Changes related to Tax
 */
public class MAXLineItemTaxBreakUpDetail implements MAXLineItemTaxBreakUpDetailIfc, Serializable {

	private static final long serialVersionUID = 7526472295622776147L;

	/*
	 * TaxAssignment Object which holds Taxable Factor,Tax
	 * Factor,TaxRate,TaxCode
	 */
	private MAXTaxAssignmentIfc taxAssignment;

	/*
	 * Selling Retail when multiplied with Taxable Factor gives Taxable Amount
	 */
	private CurrencyIfc taxableAmount;

	/* Selling Retail when multiplied with Tax Factor gives Tax Amount */
	private CurrencyIfc taxAmount;

	/*
	 * SaleReturnFlag will have the following valid values.
	 * 
	 * *0= Indicates dont Calculate Tax 1= Indicates will be used for Sales
	 * -1=Indicates Calculate for returns
	 */

	private int saleReturnTaxFlag = 0;

	private String taxCode;

	private String taxCodeDesc;

	private BigDecimal taxRate;

	public MAXLineItemTaxBreakUpDetail() {
		super();
		taxableAmount = DomainGateway.getBaseCurrencyInstance();
		taxAmount = DomainGateway.getBaseCurrencyInstance();
	}

	/**
	 * 
	 * @return
	 */
	public CurrencyIfc getTaxableAmount() {
		return taxableAmount;
	}

	/**
	 * 
	 * @param taxableAmount
	 */
	public void setTaxableAmount(CurrencyIfc taxableAmount) {
		this.taxableAmount = taxableAmount;
	}

	/**
	 * 
	 * @return
	 */
	public CurrencyIfc getTaxAmount() {
		return taxAmount;
	}

	/**
	 * 
	 * @param taxAmount
	 */
	public void setTaxAmount(CurrencyIfc taxAmount) {
		this.taxAmount = taxAmount;
	}

	/**
	 * 
	 * @return
	 */
	public MAXTaxAssignmentIfc getTaxAssignment() {
		return taxAssignment;
	}

	/**
	 * 
	 * @param taxAssignment
	 */
	public void setTaxAssignment(MAXTaxAssignmentIfc taxAssignment) {
		this.taxAssignment = taxAssignment;
	}

	/**
	 * 
	 * @return
	 */
	public int getSaleReturnTaxFlag() {
		return saleReturnTaxFlag;
	}

	/**
	 * 
	 * @param saleReturnTaxFlag
	 */
	public void setSaleReturnTaxFlag(int saleReturnTaxFlag) {
		this.saleReturnTaxFlag = saleReturnTaxFlag;
	}

	/**
	 * @see java.lang.Object#clone()
	 * @see #setCloneAttributes(PLUItem)
	 */
	public Object clone() {
		MAXLineItemTaxBreakUpDetail lineItemTaxBreakUpDetail = new MAXLineItemTaxBreakUpDetail();
		setCloneAttributes(lineItemTaxBreakUpDetail);
		return lineItemTaxBreakUpDetail;
	}

	/**
	 * Constructs copy of LineItemTaxBreakUpDetail.
	 * <P>
	 * 
	 * @return generic object copy of LineItemTaxBreakUpDetail object
	 **/
	private void setCloneAttributes(MAXLineItemTaxBreakUpDetail lineItemTaxBreakUpDetail) {
		lineItemTaxBreakUpDetail.setTaxableAmount(taxableAmount);
		lineItemTaxBreakUpDetail.setTaxAmount(taxAmount);
		lineItemTaxBreakUpDetail.setTaxCode(taxCode);
		lineItemTaxBreakUpDetail.setTaxRate(taxRate);
		lineItemTaxBreakUpDetail.setTaxCodeDescription(taxCodeDesc);
		lineItemTaxBreakUpDetail.taxAssignment = (MAXTaxAssignmentIfc) taxAssignment.clone();
	}

	/**
	 * 
	 */
	public String getTaxCode() {
		return taxAssignment != null ? taxAssignment.getTaxCode() : taxCode;
	}

	/**
	 * 
	 */
	public void setTaxCode(String taxCode) {
		this.taxCode = taxCode;
		if (this.taxAssignment != null) {
			this.taxAssignment.setTaxCode(taxCode);
		}

	}

	/**
	 * 
	 */
	public String getTaxCodeDescription() {
		return taxAssignment != null ? taxAssignment.getTaxCodeDescription() : taxCodeDesc;
	}

	/**
	 * 
	 */
	public void setTaxCodeDescription(String taxCodeDesc) {
		this.taxCodeDesc = taxCodeDesc;
		if (this.taxAssignment != null) {
			this.taxAssignment.setTaxCodeDescription(taxCodeDesc);
		}
	}

	/**
	 * 
	 */
	public String getTaxRate() {
		return taxAssignment != null ? taxAssignment.getTaxRate().toString() : taxRate.toString();
	}

	/**
	 * 
	 */
	public void setTaxRate(BigDecimal taxRate) {
		this.taxRate = taxRate;
		if (this.taxAssignment != null) {
			this.taxAssignment.setTaxRate(taxRate);
		}
	}

}
