/* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
   Copyright (c) 2012 - 2013 MAXHyperMarket, Inc.    All Rights Reserved.
    Rev 1.1  29/May/2017    Nitika Arora Added the interface implementation and override the clone method for the class
  	Rev 1.0  08/May/2013	Jyoti Rawal, Initial Draft: Changes for Hire Purchase Functionality 
 * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */

package max.retail.stores.domain.tender;

import oracle.retail.stores.commerceservices.common.currency.CurrencyIfc;
import oracle.retail.stores.domain.tender.TenderPurchaseOrder;
import oracle.retail.stores.foundation.utility.Util;

public class MAXTenderPurchaseOrder extends TenderPurchaseOrder implements MAXTenderPurchaseOrderIfc{

	/**
	 * 
	 */
	private static final long serialVersionUID = -5067444375731595781L;

	/**
	 * Approval code
	 **/
	protected String approvalCode = null;

	public Object clone() {
		MAXTenderPurchaseOrder tpo = new MAXTenderPurchaseOrder();
		// set attributes in clone
		setCloneAttributes(tpo);

		return tpo;
	}

	
	public void setCloneAttributes(MAXTenderPurchaseOrder newClass) {
		super.setCloneAttributes(newClass);
		if (purchaseOrderNumber != null) {
			newClass.setPurchaseOrderNumber(getPurchaseOrderNumber());
		}
		if (approvalCode != null) {
			newClass.setApprovalCode(getApprovalCode());
		}
		if (faceValue != null) {
			newClass.setFaceValueAmount((CurrencyIfc) faceValue.clone());
		}
		newClass.setAgencyName(agencyName);
		newClass.setTaxableStatus(taxableStatus);

	}

	public boolean equals(Object obj) { // begin equals()
		boolean isEqual = true;
		// confirm object instanceof this object
		if (obj instanceof TenderPurchaseOrder) { // begin compare objects
			MAXTenderPurchaseOrder c = (MAXTenderPurchaseOrder) obj; // downcast
																		// the
																		// input
																		// object

			// compare all the attributes of TenderPurchaseOrder
			if (super.equals(obj) && Util.isObjectEqual(getPurchaseOrderNumber(), c.getPurchaseOrderNumber())
					&& Util.isObjectEqual(getFaceValueAmount(), c.getFaceValueAmount())
					&& Util.isObjectEqual(getAgencyName(), c.getAgencyName())
					&& Util.isObjectEqual(getTaxableStatus(), c.getTaxableStatus())
					&& Util.isObjectEqual(getApprovalCode(), c.getApprovalCode())) {
				isEqual = true; // set the return code to true
			} else {
				isEqual = false; // set the return code to false
			}
		} // end compare objects
		else {
			isEqual = false;
		}
		return (isEqual);
	}

	/**
	 * Retrieves Approval code.
	 * <P>
	 * 
	 * @return Approval code
	 **/
	public String getApprovalCode() {
		return (approvalCode);
	}

	/**
	 * Sets approval code.
	 * <P>
	 * 
	 * @param value
	 *            approval code
	 **/
	public void setApprovalCode(String value) {
		approvalCode = value;
	}

	public static void main(String args[]) { // begin main()
		// instantiate class
		MAXTenderPurchaseOrder c = new MAXTenderPurchaseOrder();
		// output toString()
		System.out.println(c.toString());
	} // end main()
}
