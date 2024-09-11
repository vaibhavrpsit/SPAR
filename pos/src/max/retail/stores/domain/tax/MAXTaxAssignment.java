/* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *

  /** * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * ************************
 * Copyright (c) 2013 Max Hypermarket.    All Rights Reserved.
 *  Rev 1.1		May 04, 2017		Kritica Agarwal 	GST Changes 
 *  Rev 1.0	 Prateek		30/08/2013		Changes for TAX issues while grouping of items.
 * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * ***************************/

package max.retail.stores.domain.tax;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * Each Item will have a TaxCategory associated with it and in turn each tax
 * category will have multiple taxAssignments.
 * 
 * @author Shavinki Goyal
 * 
 */

public class MAXTaxAssignment implements MAXTaxAssignmentIfc, Serializable {

	private int taxCategory;

	private String taxCode;

	private String taxCodeDescription;

	private BigDecimal taxRate;

	private BigDecimal taxableAmountFactor;

	private BigDecimal taxAmountFactor;

	private String appliedOn;

	private int applicationOrder;

	public int getApplicationOrder() {
		return applicationOrder;
	}

	public void setApplicationOrder(int applicationOrder) {
		this.applicationOrder = applicationOrder;
	}

	public String getAppliedOn() {
		return appliedOn;
	}

	public void setAppliedOn(String appliedOn) {
		this.appliedOn = appliedOn;
	}

	public BigDecimal getTaxableAmountFactor() {
		return taxableAmountFactor;
	}

	public void setTaxableAmountFactor(BigDecimal taxableFactor) {
		this.taxableAmountFactor = taxableFactor;
	}

	public int getTaxCategory() {
		return taxCategory;
	}

	public void setTaxCategory(int taxCategory) {
		this.taxCategory = taxCategory;
	}

	public String getTaxCode() {
		return taxCode;
	}

	public void setTaxCode(String taxCode) {
		this.taxCode = taxCode;
	}

	public String getTaxCodeDescription() {
		return taxCodeDescription;
	}

	public void setTaxCodeDescription(String taxCodeDescription) {
		this.taxCodeDescription = taxCodeDescription;
	}

	public BigDecimal getTaxRate() {
		return taxRate;
	}

	public void setTaxRate(BigDecimal taxRate) {
		this.taxRate = taxRate;
	}

	/**
	 * @see java.lang.Object#clone()
	 * @see #setCloneAttributes(PLUItem)
	 */
	public Object clone() {
		MAXTaxAssignment taxAssignment = new MAXTaxAssignment();
		setCloneAttributes(taxAssignment);
		return taxAssignment;
	}

	private void setCloneAttributes(MAXTaxAssignment taxAssignment) {
		taxAssignment.setApplicationOrder(applicationOrder);
		taxAssignment.setTaxableAmountFactor(taxableAmountFactor);
		taxAssignment.setTaxCategory(taxCategory);
		taxAssignment.setTaxCode(taxCode);
		taxAssignment.setTaxRate(taxRate);
		taxAssignment.setTaxAmountFactor(taxAmountFactor);
		taxAssignment.setTaxCodeDescription(taxCodeDescription);

	}

	public BigDecimal getTaxAmountFactor() {
		return taxAmountFactor;
	}

	public void setTaxAmountFactor(BigDecimal taxAmountFactor) {
		this.taxAmountFactor = taxAmountFactor;
	}
	
	// ------------------------------------------------------------------------
	/**
	 * MAX Customizations Determine if two MAXTaxAssignment objects are
	 * identical. Added by Prateek
	 * 
	 * @param obj
	 **/
	// ----------------------------------------------------------------------
	public boolean equals(Object obj) {
		boolean result = true;
		MAXTaxAssignment taxAsgmt = null;
		try {
			if (this == obj) {
				result = true;
			} else {
				// compare the required attributes of MGTaxAssignment
				taxAsgmt = (MAXTaxAssignment) obj;
				// compare all the attributes of MGTaxAssignment
				if (getTaxCategory() != taxAsgmt.getTaxCategory())
					result = false;
				else if ((getTaxCode() != null && taxAsgmt.getTaxCode() != null)
						&& !getTaxCode().equals(taxAsgmt.getTaxCode()))
					result = false;
				else if ((getTaxCode() != null && taxAsgmt.getTaxCode() == null)
						|| (getTaxCode() == null && taxAsgmt.getTaxCode() != null))
					result = false;

				else if ((getTaxCodeDescription() != null && taxAsgmt.getTaxCodeDescription() != null)
						&& !getTaxCodeDescription().equals(taxAsgmt.getTaxCodeDescription()))
					result = false;
				else if ((getTaxCodeDescription() != null && taxAsgmt.getTaxCodeDescription() == null)
						|| (getTaxCodeDescription() == null && taxAsgmt.getTaxCodeDescription() != null))
					result = false;

				else if ((getTaxRate() != null && taxAsgmt.getTaxRate() != null)
						&& !getTaxRate().equals(taxAsgmt.getTaxRate()))
					result = false;
				else if ((getTaxRate() != null && taxAsgmt.getTaxRate() == null)
						|| (getTaxRate() == null && taxAsgmt.getTaxRate() != null))
					result = false;

				else if ((getTaxableAmountFactor() != null && taxAsgmt.getTaxableAmountFactor() != null)
						&& !getTaxableAmountFactor().equals(taxAsgmt.getTaxableAmountFactor()))
					result = false;
				else if ((getTaxableAmountFactor() != null && taxAsgmt.getTaxableAmountFactor() == null)
						|| (getTaxableAmountFactor() == null && taxAsgmt.getTaxableAmountFactor() != null))
					result = false;

				else if ((getTaxAmountFactor() != null && taxAsgmt.getTaxAmountFactor() != null)
						&& !getTaxAmountFactor().equals(taxAsgmt.getTaxAmountFactor()))
					result = false;
				else if ((getTaxAmountFactor() != null && taxAsgmt.getTaxAmountFactor() == null)
						|| (getTaxAmountFactor() == null && taxAsgmt.getTaxAmountFactor() != null))
					result = false;

				else if ((getAppliedOn() != null && taxAsgmt.getAppliedOn() != null)
						&& !getAppliedOn().equals(taxAsgmt.getAppliedOn()))
					result = false;
				else if ((getAppliedOn() != null && taxAsgmt.getAppliedOn() == null)
						|| (getAppliedOn() == null && taxAsgmt.getAppliedOn() != null))
					result = false;

				if (getApplicationOrder() != taxAsgmt.getApplicationOrder())
					result = false;

			}
		} catch (Exception e) {
			result = false;
		}
		return result;
	}
	//Change for REV 1.1: Starts
	private String taxType;

	public String getTaxType()
	{
		return taxType;
	}

	public void setTaxType(String taxType)
	{
		this.taxType = taxType;
	}
	//Change for REV 1.1: Ends
}
