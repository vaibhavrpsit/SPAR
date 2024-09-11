package max.retail.stores.pos.services.customer.lookup;

import max.retail.stores.pos.services.customer.common.MAXCustomerCargo;
import oracle.retail.stores.foundation.manager.ifc.UIManagerIfc;
import oracle.retail.stores.foundation.tour.application.Letter;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.pos.services.PosSiteActionAdapter;
import oracle.retail.stores.pos.services.customer.common.CustomerCargo;
import oracle.retail.stores.pos.ui.POSUIManagerIfc;
import oracle.retail.stores.pos.ui.beans.CustomerSelectBeanModel;
import oracle.retail.stores.pos.ui.beans.NavigationButtonBeanModel;

public class MAXCustomerSearchOptionSite extends PosSiteActionAdapter {

	/**
	 * 
	 */
	private static final long serialVersionUID = -1086064507239573874L;

	// ----------------------------------------------------------------------
	/**
	 * Prompts the user for the type of customer search.
	 * <p>
	 * 
	 * @param bus
	 *            the bus arriving at this site
	 **/
	// ----------------------------------------------------------------------
	public void arrive(BusIfc bus) {
		// Set the screen ID and bean type
		MAXCustomerCargo cargo = (MAXCustomerCargo) bus.getCargo();
		POSUIManagerIfc ui = (POSUIManagerIfc) bus
				.getManager(UIManagerIfc.TYPE);

		CustomerSelectBeanModel model = new CustomerSelectBeanModel();
		NavigationButtonBeanModel nModel = new NavigationButtonBeanModel();

		if (cargo.isAddCustomerEnabled()) {
			nModel.setButtonEnabled(CustomerCargo.EMPID, true);
			nModel.setButtonEnabled(CustomerCargo.CUSTINFO, true);
		} else {
			nModel.setButtonEnabled(CustomerCargo.EMPID, false);
			nModel.setButtonEnabled(CustomerCargo.CUSTINFO, false);
		}

		if (cargo.isAddBusinessEnabled()) {
			nModel.setButtonEnabled(CustomerCargo.BUSINFO, true);
		} else {
			nModel.setButtonEnabled(CustomerCargo.BUSINFO, false);
		}

		model.setLocalButtonBeanModel(nModel);
		if (cargo.isTicCustomerPhoneNoFlag()) {
			//Added by Vaibhav for CRM customer serach withou SBI and wallet
         cargo.setCustomerCRMsearch(true);
			bus.mail(new Letter("CustomerSearchByNumber"), BusIfc.CURRENT);
		} else {
			ui.showScreen(POSUIManagerIfc.CUSTOMER_SEARCH_OPTIONS, model);
		}
	}

	// ----------------------------------------------------------------------
	/**
	 * Returns the revision number of the class.
	 * <P>
	 * 
	 * @return String representation of revision number
	 **/
	// ----------------------------------------------------------------------
	public String getRevisionNumber() {
		return (revisionNumber);
	}
}
