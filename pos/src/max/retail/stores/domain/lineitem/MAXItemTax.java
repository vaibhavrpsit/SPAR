/********************************************************************************
 *   
 *	Copyright (c) 2016 - 2017 MAX Hypermarket, Inc    All Rights Reserved.
 *	
 *	Rev 1.2		May 02, 2017	Mansi Goel		Changes to resolve tax amount is coming wrong in 
 *												tax history(HST_TX) table during postvoid transaction
 * 	Rev 1.1		Nov 10, 2016	Ashish Yadav	changes for Home Delivery Send FES
 * 	Rev 1.0		Aug 26, 2016	Nitesh Kumar	changes for code merging 
 *
 ********************************************************************************/

package max.retail.stores.domain.lineitem;

import oracle.retail.stores.commerceservices.common.currency.CurrencyIfc;
import oracle.retail.stores.domain.DomainGateway;
import oracle.retail.stores.domain.lineitem.ItemTax;

public class MAXItemTax extends ItemTax implements MAXItemTaxIfc {

	static final long serialVersionUID = -3049485652757787021L;

	public static String revisionNumber = "$Revision: /rgbustores_12.0.9in_branch/1 $";

	protected MAXLineItemTaxBreakUpDetailIfc[] taxBreakUpDetails;

	public MAXLineItemTaxBreakUpDetailIfc[] getLineItemTaxBreakUpDetail() {
		// TODO Auto-generated method stub
		return taxBreakUpDetails;
	}

	public void setLineItemTaxBreakUpDetail(MAXLineItemTaxBreakUpDetailIfc[] itemTaxBreakUpDetails) {
		taxBreakUpDetails = itemTaxBreakUpDetails;

	}

	// Changes start for Rev 1.1 (Send)

	public Object clone() {
		MAXItemTax t = new MAXItemTax();
		setCloneAttributes(t);
		return t;
	}

	// Changes start for Rev 1.1 (Send)
	public void setCloneAttributes(MAXItemTax newClass) {
		super.setCloneAttributes(newClass);
		//newClass.setLineItemTaxBreakUpDetail(taxBreakUpDetails);
		// Using deep copy clone the LineItemTaxBreakupDetails.
				if (this.taxBreakUpDetails != null) {
					MAXLineItemTaxBreakUpDetailIfc[] litbd = new MAXLineItemTaxBreakUpDetail[this.taxBreakUpDetails.length];
					for (int i = 0; i < taxBreakUpDetails.length; i++) {
						litbd[i] = (MAXLineItemTaxBreakUpDetailIfc) this.taxBreakUpDetails[i].clone();
					}
					newClass.setLineItemTaxBreakUpDetail(litbd);
				}
	}
	
	//Changes for Rev 1.2 : Starts
	public void setItemTaxableAmount(CurrencyIfc value) {
		itemTaxableAmount = value;
		if (itemTaxableAmount != null && itemTaxableAmount.signum() == CurrencyIfc.NEGATIVE) {
			this.getTaxInformationContainer().negate();
		}
	}
	//Changes for Rev 1.2 : Ends
}
