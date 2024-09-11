/* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
 *  Copyright (c) 2016 - 2017 MAX Hypermarket, Inc.    All Rights Reserved.
 *	
 *	Rev 1.0     Oct 18, 2016		Ashish Yadav		Code Merging
 *	Rev 1.1     07 Nov,2016			Ashish Yadav		changes for cardless loyalty FES
 *
 * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */

package max.retail.stores.pos.services.customer.tic;

import max.retail.stores.pos.services.customer.main.MAXCustomerMainCargo;
import max.retail.stores.pos.services.sale.MAXSaleCargo;
import max.retail.stores.pos.ui.MAXPOSUIManagerIfc;
import max.retail.stores.pos.ui.beans.MAXTICCustomerModel;
import oracle.retail.stores.domain.utility.Gender;
import oracle.retail.stores.foundation.manager.ifc.UIManagerIfc;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.foundation.tour.ifc.CargoIfc;
import oracle.retail.stores.pos.manager.ifc.UtilityManagerIfc;
import oracle.retail.stores.pos.services.PosSiteActionAdapter;
import oracle.retail.stores.pos.services.customer.common.CustomerCargo;
import oracle.retail.stores.pos.services.sale.SaleCargo;
import oracle.retail.stores.pos.ui.POSUIManagerIfc;
import oracle.retail.stores.pos.ui.beans.POSBaseBeanModel;

public class MAXAddTICCustomerSite extends PosSiteActionAdapter {

	private static final long serialVersionUID = 1132529737045210034L;

	public void arrive(BusIfc bus) {

		CargoIfc cargo = (CargoIfc) bus.getCargo();

		POSUIManagerIfc ifc = (POSUIManagerIfc) bus.getManager(UIManagerIfc.TYPE);
		MAXTICCustomerModel model = new MAXTICCustomerModel();

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

		if (cargo != null && cargo instanceof SaleCargo) {
			MAXSaleCargo saleCargo = (MAXSaleCargo) cargo;
			if (saleCargo.getTicCustomerPhoneNo() != null
					&& saleCargo.getTicCustomerPhoneNo().getPhoneNumber() != null
					&& !saleCargo.getTicCustomerPhoneNo().getPhoneNumber()
							.trim().equalsIgnoreCase("")) {
				// Changes start for cod emerging(removing getAreaCode() as it
				// is not present in base 14)
				// model.setMobile(saleCargo.getTicCustomerPhoneNo().getAreaCode()+saleCargo.getTicCustomerPhoneNo().getPhoneNumber());
				model.setMobile(saleCargo.getTicCustomerPhoneNo()
						+ saleCargo.getTicCustomerPhoneNo().getPhoneNumber());
				// Changes ends
			}
		} else if (cargo != null && cargo instanceof CustomerCargo) {
			// Changes start for Rev 1.1 (Cardless loyalty)
			MAXCustomerMainCargo customerCargo = (MAXCustomerMainCargo) cargo;
			// Changes ends for Rev 1.1 (Cardless loyalty)
			if (customerCargo.getTicCustomerPhoneNo() != null
					&& customerCargo.getTicCustomerPhoneNo().getPhoneNumber() != null
					&& !customerCargo.getTicCustomerPhoneNo().getPhoneNumber()
							.trim().equalsIgnoreCase("")) {
// Changes starts for Rev 1.1 (Cardless loyalty)
				// Changes start for cod emerging(removing getAreaCode() as it
				// is not present in base 14)
				// model.setMobile(customerCargo.getTicCustomerPhoneNo().getAreaCode()+customerCargo.getTicCustomerPhoneNo().getPhoneNumber());
			model.setMobile(customerCargo.getTicCustomerPhoneNo()
								.getPhoneNumber());
				// Changes ends for Rev 1.1 (Cardless loyalty)
				// Changes ends
			}
		}
		model.setGenderTypes(genderValues);
		ifc.showScreen(MAXPOSUIManagerIfc.ADD_TIC_CUSTOMER_OPTIONS, model);
	}
}
