/********************************************************************************
 *   
 *	Copyright (c) 2015  Lifestyle India pvt Ltd    All Rights Reserved.
 *
 *	Rev 1.2		May 02, 2017	Mansi Goel		Changes to resolve tax amount is coming wrong in 
 *												tax history(HST_TX) table during postvoid transaction
 *	Rev 1.1 	Feb 20, 2017    hitesh dua   	on receipt , every line item should print tax type. 
 *	Rev 1.0 	Dec 20, 2016    hitesh dua   	set tax breakup details
 *
 ********************************************************************************/
package max.retail.stores.domain.tax;

import max.retail.stores.domain.lineitem.MAXLineItemTaxBreakUpDetailIfc;
import oracle.retail.stores.commerceservices.common.currency.CurrencyIfc;
import oracle.retail.stores.domain.DomainGateway;
import oracle.retail.stores.domain.tax.TaxConstantsIfc;
import oracle.retail.stores.domain.tax.TaxInformation;

/**
 * @author user
 * 
 */
public class MAXTaxInformation extends TaxInformation implements MAXTaxInformationIfc {

	/**
	 * 
	 */
	private static final long serialVersionUID = -6063142401678943042L;
	/* India Localization - Tax Changes Starts Here */
	private MAXLineItemTaxBreakUpDetailIfc[] taxBreakUpDetails;
	/* India Localization - Tax Changes Ends Here */

	/*
	 * (non-Javadoc)
	 * 
	 * @see ssl.retail.stores.domain.tax.SSLTaxInformationIfc#
	 * getLineItemTaxBreakUpDetails()
	 */
	public MAXLineItemTaxBreakUpDetailIfc[] getLineItemTaxBreakUpDetails() {
		return taxBreakUpDetails;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see ssl.retail.stores.domain.tax.SSLTaxInformationIfc#
	 * setLineItemTaxBreakUpDetails
	 * (ssl.retail.stores.domain.lineitem.SSLLineItemTaxBreakUpDetailIfc[])
	 */
	public void setLineItemTaxBreakUpDetails(MAXLineItemTaxBreakUpDetailIfc[] taxBreakUpDetails) {
		this.taxBreakUpDetails = taxBreakUpDetails;
	}

	// changes for rev 1.0 start
	private CurrencyIfc taxableAmt = DomainGateway.getBaseCurrencyInstance();

	private CurrencyIfc taxAmt = DomainGateway.getBaseCurrencyInstance();

	/**
	 * @return the taxableAmt
	 */
	public CurrencyIfc getTaxableAmt() {
		return taxableAmt;
	}

	/**
	 * @param taxableAmt
	 *            the taxableAmt to set
	 */
	public void setTaxableAmt(CurrencyIfc taxableAmt) {
		this.taxableAmt = taxableAmt;
	}

	/**
	 * @return the taxAmt
	 */
	public CurrencyIfc getTaxAmt() {
		return taxAmt;
	}

	/**
	 * @param taxAmt
	 *            the taxAmt to set
	 */
	public void setTaxAmt(CurrencyIfc taxAmt) {
		this.taxAmt = taxAmt;
	}

	public CurrencyIfc addTaxableAmt(CurrencyIfc taxableAmt) {
		this.taxableAmt = this.taxableAmt.add(taxableAmt);
		return this.taxableAmt;
	}

	public CurrencyIfc addTaxAmt(CurrencyIfc taxAmt) {
		this.taxAmt = this.taxAmt.add(taxAmt);
		return this.taxAmt;
	}

	// changes for rev 1.0 end

	public Object clone() {
		MAXTaxInformation newClass = new MAXTaxInformation();
		setCloneAttributes(newClass);
		return newClass;
	}

	/**
	 * Set the attributes for a clone
	 * <P>
	 * 
	 * @param newClass
	 *            Class to put this information into
	 */
	public void setCloneAttributes(MAXTaxInformation newClass) {
		super.setCloneAttributes(newClass);
		newClass.setLineItemTaxBreakUpDetails(taxBreakUpDetails);
		newClass.setTaxableAmt(taxableAmt);
		newClass.setTaxAmt(taxAmt);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see oracle.retail.stores.domain.tax.TaxInformationIfc#getReceiptCode()
	 * overrided for rev 1.1
	 */
	public String getReceiptCode() {
		String receiptCode = TaxConstantsIfc.TAX_MODE_CHAR[getTaxMode()];
		/*
		 * if (getInclusiveTaxFlag()) { receiptCode = getTaxRuleName(); }
		 */
		return receiptCode;
	}

	//Changes for Rev 1.2 : Starts
	@Override
	public void negate() {
		if (this.getTaxableAmount().signum() == CurrencyIfc.POSITIVE)
			negateTaxableAmount();
		if (this.getTaxAmount().signum() == CurrencyIfc.POSITIVE)
			negateTaxAmount();
	}
	//Changes for Rev 1.2 : Ends
}
