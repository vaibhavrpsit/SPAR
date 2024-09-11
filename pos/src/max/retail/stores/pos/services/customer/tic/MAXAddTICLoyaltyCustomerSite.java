/* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
 *  Copyright (c) 2016 - 2017 MAX Hypermarket, Inc.    All Rights Reserved.
 *	
 *	Rev 1.0     Oct 18, 2016		Ashish Yadav		Code Merging
 *
 * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */

package max.retail.stores.pos.services.customer.tic;

import max.retail.stores.pos.services.customer.common.MAXCustomerCargo;
import max.retail.stores.pos.services.sale.MAXSaleCargo;
import max.retail.stores.pos.ui.MAXPOSUIManagerIfc;
import max.retail.stores.pos.ui.beans.MAXTICCustomerModel;
import oracle.retail.stores.domain.customer.CustomerInfoIfc;
import oracle.retail.stores.domain.utility.Gender;
import oracle.retail.stores.foundation.manager.ifc.UIManagerIfc;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.foundation.tour.ifc.CargoIfc;
import oracle.retail.stores.pos.manager.ifc.UtilityManagerIfc;
import oracle.retail.stores.pos.services.PosSiteActionAdapter;
import oracle.retail.stores.pos.services.customer.common.CustomerCargo;
import oracle.retail.stores.pos.ui.POSUIManagerIfc;
import oracle.retail.stores.pos.ui.beans.POSBaseBeanModel;

public class MAXAddTICLoyaltyCustomerSite extends PosSiteActionAdapter {

	private static final long serialVersionUID = 1132529737045210034L;

	public void arrive(BusIfc bus) {
		CargoIfc cargo = (CargoIfc) bus.getCargo();

		POSUIManagerIfc ifc = (POSUIManagerIfc) bus
				.getManager(UIManagerIfc.TYPE);
		MAXTICCustomerModel model = new MAXTICCustomerModel();

		// model.getGenderTypes()

		UtilityManagerIfc utility = (UtilityManagerIfc) bus
				.getManager(UtilityManagerIfc.TYPE);

		String[] genderValues = new String[Gender.getGenderValues().length];
		for (int i = 0; i < Gender.getGenderValues().length; i++) {
			genderValues[i] = utility
					.retrieveCommonText(Gender.GENDER_DESCRIPTOR[i]);
		}

		POSBaseBeanModel beanModel = (POSBaseBeanModel) ifc
				.getModel(MAXPOSUIManagerIfc.ADD_TIC_CUSTOMER_OPTIONS);
		if (beanModel instanceof MAXTICCustomerModel
				&& (bus.getCurrentLetter().getName()
						.equalsIgnoreCase("Failure") || bus.getCurrentLetter()
						.getName().equalsIgnoreCase("ReturnAdd"))) {
			model = (MAXTICCustomerModel) beanModel;
		}

		if (cargo != null && cargo instanceof MAXSaleCargo) {
			MAXSaleCargo saleCargo = (MAXSaleCargo) cargo;
			saleCargo.getAppID();

			if (saleCargo.getCustomerInfo() != null
					&& saleCargo.getCustomerInfo().getCustomerInfoType() == CustomerInfoIfc.CUSTOMER_INFO_TYPE_PHONE_NUMBER
					&& saleCargo.getCustomerInfo().getPhoneNumber() != null) {
				saleCargo.setTicCustomerPhoneNo(saleCargo.getCustomerInfo()
						.getPhoneNumber());
			}
			if (saleCargo.getTicCustomerPhoneNo() != null
					&& saleCargo.getTicCustomerPhoneNo().getPhoneNumber() != null
					&& !saleCargo.getTicCustomerPhoneNo().getPhoneNumber()
							.trim().equalsIgnoreCase("")) {
				// Changes start for cod emerging(removing getAreaCode() as it
				// is not present in base 14)
				// model.setMobile(saleCargo.getTicCustomerPhoneNo().getAreaCode()+saleCargo.getTicCustomerPhoneNo().getPhoneNumber());
				/*model.setMobile(saleCargo.getTicCustomerPhoneNo()
						+ saleCargo.getTicCustomerPhoneNo().getPhoneNumber());*/
				model.setMobile(saleCargo.getTicCustomerPhoneNo().getPhoneNumber());
				// changes ends
			}
		} else if (cargo != null && cargo instanceof CustomerCargo) {
			MAXCustomerCargo customerCargo = (MAXCustomerCargo) cargo;
			if (customerCargo.getTicCustomerPhoneNo() != null
					&& customerCargo.getTicCustomerPhoneNo().getPhoneNumber() != null
					&& !customerCargo.getTicCustomerPhoneNo().getPhoneNumber()
							.trim().equalsIgnoreCase("")) {
				// Changes start for cod emerging(removing getAreaCode() as it
				// is not present in base 14)
				// model.setMobile(customerCargo.getTicCustomerPhoneNo().getAreaCode()+customerCargo.getTicCustomerPhoneNo().getPhoneNumber());
				model.setMobile(customerCargo.getTicCustomerPhoneNo()
						+ customerCargo.getTicCustomerPhoneNo()
								.getPhoneNumber());
				// Changes ends
			}
		}
		model.setGenderTypes(genderValues);
		ifc.showScreen(MAXPOSUIManagerIfc.ADD_TIC_CUSTOMER_OPTIONS, model);
	}
}
