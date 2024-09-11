/********************************************************************************
 *   
 *	Copyright (c) 2016 XYZ Ltd    All Rights Reserved.
 *
 *
 *	Rev 1.0    05 April 2016    Kritica Agarwal GST Changes
 *
 ********************************************************************************/
package max.retail.stores.pos.receipt;

import java.io.Serializable;
import java.math.BigDecimal;

import oracle.retail.stores.commerceservices.common.currency.CurrencyIfc;


public class MAXTaxSummaryDetailsBean implements Serializable{
	
	private static final long serialVersionUID = 75264722956776147L;
	
	private String taxDescription;
	
	private BigDecimal taxRate;
	
	private CurrencyIfc taxableAmount;
	
	private CurrencyIfc taxAmount;
	
	private CurrencyIfc totalTaxAmount;
	
	private String taxCodeValue;
	
	private String taxCode;
	
	
	public String getTaxCodeValue()
	{
		return taxCodeValue;
	}

	public void setTaxCodeValue(String taxCodeValue)
	{
		this.taxCodeValue = taxCodeValue;
	}

	public CurrencyIfc getTotalTaxAmount()
	{
		return totalTaxAmount;
	}

	public void setTotalTaxAmount(CurrencyIfc totalTaxAmount)
	{
		this.totalTaxAmount = totalTaxAmount;
	}

	

	public String getTaxCode() {
		return taxCode;
	}

	public void setTaxCode(String taxCode) {
		this.taxCode = taxCode;
	}

	public CurrencyIfc getTaxAmount() {
		return taxAmount;
	}

	public void setTaxAmount(CurrencyIfc taxAmount) {
		this.taxAmount = taxAmount;
	}

	public String getTaxDescription() {		
		return taxDescription;
	}

	public void setTaxDescription(String taxDescription) {
		this.taxDescription = taxDescription;
	}

	public BigDecimal getTaxRate() 
	{
		try
		{
		if((taxRate.intValueExact())==taxRate.doubleValue())
		{
			return new BigDecimal(taxRate.intValue());
		}
		return taxRate;
	}
		catch(ArithmeticException e)
		{
			return taxRate;
		}
	}

	public void setTaxRate(BigDecimal taxRate) {
		this.taxRate = taxRate;
	}

	public CurrencyIfc getTaxableAmount() {
		return taxableAmount;
	}

	public void setTaxableAmount(CurrencyIfc taxableAmount) {
		this.taxableAmount = taxableAmount;
	}
	
	public CurrencyIfc addTaxableAmount(CurrencyIfc taxableAmount){
		this.taxableAmount=this.taxableAmount.add(taxableAmount);
		return this.taxableAmount;
	}
	
	public CurrencyIfc addTaxAmount(CurrencyIfc taxAmount){
		this.taxAmount= this.taxAmount.add(taxAmount);
		return this.taxAmount;
	}
	
	public CurrencyIfc addTotalTaxAmount(CurrencyIfc totalTax){
		this.totalTaxAmount=this.totalTaxAmount.add(totalTax);
		return this.totalTaxAmount;
	}
	

	

}
