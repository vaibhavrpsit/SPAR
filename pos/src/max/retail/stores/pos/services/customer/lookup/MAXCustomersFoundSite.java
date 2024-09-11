/* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
*  Copyright (c) 2016 - 2017 MAX Hypermarket, Inc.    All Rights Reserved.
*
*
*
*	Rev 1.0     Oct 19, 2016		Mansi Goel			Changes for Customer FES
*
* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */

package max.retail.stores.pos.services.customer.lookup;

import java.util.Vector;

import oracle.retail.stores.domain.customer.CustomerIfc;
import oracle.retail.stores.foundation.manager.ifc.UIManagerIfc;
import oracle.retail.stores.foundation.tour.application.Letter;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.pos.services.PosSiteActionAdapter;
import oracle.retail.stores.pos.services.common.CommonLetterIfc;
import oracle.retail.stores.pos.services.customer.common.CustomerCargo;
import oracle.retail.stores.pos.ui.POSUIManagerIfc;
import oracle.retail.stores.pos.ui.beans.DualListBeanModel;

//--------------------------------------------------------------------------
/**
 * Determines how to process the customers found by the search.
 **/
// --------------------------------------------------------------------------
public class MAXCustomersFoundSite extends PosSiteActionAdapter {

	/**
	 * 
	 */
	private static final long serialVersionUID = -1561391754814575104L;

	// ----------------------------------------------------------------------
	/**
	 * Checks the number of customers found and does the appropriate action.
	 * <p>
	 * 
	 * @param bus
	 *            the bus arriving at this site
	 **/
	// ----------------------------------------------------------------------
	public void arrive(BusIfc bus) {
		CustomerCargo cargo = (CustomerCargo) bus.getCargo();

		// grab the customers returned
		CustomerIfc[] customerList = ((CustomerIfc[]) cargo.getCustomerList()
				.toArray());

		if (customerList.length == 1) {
			cargo.setCustomer(customerList[0]);
			bus.mail(new Letter(CommonLetterIfc.CONTINUE), BusIfc.CURRENT);
		} else {

			// setup model to display data using new UI format
			DualListBeanModel model = new DualListBeanModel();
			Vector<CustomerIfc> topList = new Vector<CustomerIfc>();
			
			//Changes for Rev 1.0 : Starts
			/*if (cargo.getOriginalCustomer() != null) {
				topList.addElement(cargo.getOriginalCustomer());
			} else {*/
				topList.addElement(customerList[0]);
			//}
			//Changes for Rev 1.0 : Ends
			model.setTopListModel(topList);
			model.setListModel(cargo.getCustomerList());

			// Display the screen
			POSUIManagerIfc ui = (POSUIManagerIfc) bus
					.getManager(UIManagerIfc.TYPE);
			ui.showScreen(cargo.getScreen(), model);
		}
	}

}
