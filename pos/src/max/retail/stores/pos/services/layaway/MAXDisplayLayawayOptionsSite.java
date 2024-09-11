/**
 * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
  Copyright (c) 2013 MAXHyperMarkets, Inc.    All Rights Reserved.
   Rev 1.0	Nitika Arora		23/02/2017	  Disable the Layaway Find button if TIC Customer is present.
 
* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
package max.retail.stores.pos.services.layaway;

import max.retail.stores.domain.customer.MAXCustomer;
import max.retail.stores.domain.customer.MAXTICCustomer;
import oracle.retail.stores.foundation.manager.ifc.UIManagerIfc;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.pos.services.PosSiteActionAdapter;
import oracle.retail.stores.pos.services.common.CommonActionsIfc;
import oracle.retail.stores.pos.services.layaway.LayawayCargo;
import oracle.retail.stores.pos.ui.POSUIManagerIfc;
import oracle.retail.stores.pos.ui.beans.NavigationButtonBeanModel;
import oracle.retail.stores.pos.ui.beans.POSBaseBeanModel;

/**
 * @author Nitika1.Arora
 *
 */
public class MAXDisplayLayawayOptionsSite extends PosSiteActionAdapter {
	
	private static final long serialVersionUID = 5568495128313284762L;
	/**
	 * class name constant
	 **/
	public static final String SITENAME = "MAXDisplayLayawayOptionsSite";
	

	// --------------------------------------------------------------------------
	/**
	 * Displays the layaway options screen. Enables/disables Find button and
	 * Cancel button based upon whether a transaction is in progress.
	 * <P>
	 * 
	 * @param bus
	 *            the bus arriving at this site
	 **/
	// --------------------------------------------------------------------------
	public void arrive(BusIfc bus) {
		POSUIManagerIfc ui = (POSUIManagerIfc) bus.getManager(UIManagerIfc.TYPE);
		POSBaseBeanModel pModel = new POSBaseBeanModel();
		NavigationButtonBeanModel gModel = new NavigationButtonBeanModel();
		NavigationButtonBeanModel nModel = new NavigationButtonBeanModel();

		LayawayCargo cargo = (LayawayCargo) bus.getCargo();
		// disable Find if transaction in progress
		if (cargo.getSaleTransaction() != null || (cargo.getTenderableTransaction() != null)) { // disable
																								// Cancel
																								// if
																								// sale
																								// transaction
																								// in
																								// progress
			if (cargo.getSaleTransaction() != null) {
				gModel.setButtonEnabled(CommonActionsIfc.CANCEL, false);
			} else {
				gModel.setButtonEnabled(CommonActionsIfc.CANCEL, true);
			}
		}
		if (cargo.getCustomer() != null && ((cargo.getCustomer() instanceof MAXCustomer
				&& ((MAXCustomer) cargo.getCustomer()).getCustomerType().equalsIgnoreCase("T"))
				|| (cargo.getCustomer() instanceof MAXTICCustomer
						&& ((MAXTICCustomer) cargo.getCustomer()).getCustomerType().equalsIgnoreCase("T")))) {
			nModel.setButtonEnabled(CommonActionsIfc.FIND, false);
		} else
			nModel.setButtonEnabled(CommonActionsIfc.FIND, true);
		pModel.setLocalButtonBeanModel(nModel);
		pModel.setGlobalButtonBeanModel(gModel);
		ui.showScreen(POSUIManagerIfc.LAYAWAY_OPTIONS, pModel);
	}
}
