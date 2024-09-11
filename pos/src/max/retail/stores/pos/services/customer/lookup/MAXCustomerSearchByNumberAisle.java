/* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
*  Copyright (c) 2016 - 2017 MAX Hypermarket, Inc.    All Rights Reserved.
*	
*
*	Rev 1.0     Oct 19, 2016		Mansi Goel			Changes for Customer FES
*
* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */

package max.retail.stores.pos.services.customer.lookup;

import max.retail.stores.pos.services.customer.common.MAXCustomerCargo;
import oracle.retail.stores.domain.customer.CustomerIfc;
import oracle.retail.stores.domain.utility.Phone;
import oracle.retail.stores.domain.utility.PhoneIfc;
import oracle.retail.stores.foundation.manager.ifc.ParameterManagerIfc;
import oracle.retail.stores.foundation.tour.application.Letter;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.pos.manager.ifc.UtilityManagerIfc;
import oracle.retail.stores.pos.services.customer.common.CustomerInfoEnteredAisle;
import oracle.retail.stores.pos.services.customer.common.CustomerUtilities;
import oracle.retail.stores.pos.ui.beans.CustomerInfoBeanModel;

public class MAXCustomerSearchByNumberAisle extends CustomerInfoEnteredAisle {

	/**
	 * 
	 */
	private static final long serialVersionUID = 5842288313213006939L;
	public static int PHONE_TYPE_ALL = -2;

	public void traverse(BusIfc bus) {

		MAXCustomerCargo cargo = (MAXCustomerCargo) bus.getCargo();

		// Store the search criteria in cargo.
		CustomerIfc customer = cargo.getCustomer();

		/* object get start as them */
		UtilityManagerIfc utility = (UtilityManagerIfc) bus
				.getManager(UtilityManagerIfc.TYPE);
		ParameterManagerIfc pm = (ParameterManagerIfc) bus
				.getManager(ParameterManagerIfc.TYPE);
		CustomerInfoBeanModel model = new CustomerInfoBeanModel();
		model.setPhoneTypes(CustomerUtilities.getPhoneTypes(utility));
		model.setCountries(utility.getCountriesAndStates(pm));
		// State info is not used for customer lookup.
		model.setStateIndex(-1);

		/* object get END as them */

		PhoneIfc phoneArr[] = new PhoneIfc[1];
		Phone phone = new Phone();
		phone.setPhoneNumber(cargo.getTicCustomerPhoneNo().getPhoneNumber());
		//Changes for Rev 1.0 : Starts
		phone.setPhoneType(cargo.getTicCustomerPhoneNo().getPhoneType());
		//Changes for Rev 1.0 : Ends
		phoneArr[0] = phone;
		model.setPhoneList(phoneArr);
		// update the customer from the model
		cargo.setCustomer(updateCustomer(customer, model));
		cargo.setOriginalCustomer(cargo.getCustomer());
		//Added by Vaibhav for CRM customer serach withou SBI and wallet
		cargo.setCustomerCRMsearch(true);
		bus.mail(new Letter("CustomerLookup"), BusIfc.CURRENT);
	}
}
