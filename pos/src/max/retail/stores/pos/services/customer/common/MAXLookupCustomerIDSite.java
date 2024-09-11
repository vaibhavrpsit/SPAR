/* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
 *  Copyright (c) 2016 - 2017 MAX Hypermarket, Inc.    All Rights Reserved.
 *	
 *	Rev 1.1     Dec 28, 2016		Ashish Yadav			Changes for Online points redemption FES
 *	Rev 1.0     Oct 19, 2016		Mansi Goel			Changes for Customer FES
 *
 * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */

package max.retail.stores.pos.services.customer.common;

// foundation imports
import java.util.Locale;

import max.retail.stores.domain.customer.MAXCustomer;
import max.retail.stores.domain.customer.MAXCustomerConstantsIfc;
import max.retail.stores.pos.services.customer.main.MAXCustomerMainCargo;
import max.retail.stores.domain.manager.customer.MAXCustomerManagerIfc;
import max.retail.stores.domain.utility.MAXCustomerSearchCriteria;
import max.retail.stores.domain.utility.MAXCustomerSearchCriteriaIfc;
import oracle.retail.stores.common.utility.LocaleMap;
import oracle.retail.stores.common.utility.LocaleRequestor;
import oracle.retail.stores.common.utility.Util;
import oracle.retail.stores.domain.customer.CustomerIfc;
import oracle.retail.stores.domain.manager.customer.CustomerManagerIfc;
import oracle.retail.stores.domain.utility.CustomerSearchCriteria;
import oracle.retail.stores.domain.utility.CustomerSearchCriteriaIfc;
import oracle.retail.stores.domain.utility.CustomerSearchCriteriaIfc.SearchType;
import oracle.retail.stores.foundation.manager.data.DataException;
import oracle.retail.stores.foundation.manager.ifc.UIManagerIfc;
import oracle.retail.stores.foundation.tour.application.Letter;
import oracle.retail.stores.foundation.tour.gate.Gateway;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.pos.manager.ifc.UtilityManagerIfc;
import oracle.retail.stores.pos.services.PosSiteActionAdapter;
import oracle.retail.stores.pos.services.common.CommonLetterIfc;
import oracle.retail.stores.pos.services.customer.common.CustomerCargo;
import oracle.retail.stores.pos.ui.DialogScreensIfc;
import oracle.retail.stores.pos.ui.POSUIManagerIfc;
import oracle.retail.stores.pos.ui.beans.DialogBeanModel;

public class MAXLookupCustomerIDSite extends PosSiteActionAdapter {

	private static final long serialVersionUID = -8458849959324199242L;

public static final String revisionNumber = "$Revision: /main/18 $";
	// ----------------------------------------------------------------------
	/**
	 * Checks for a customer with the given customer ID.
	 * <p>
	 * 
	 * @param bus
	 *            the bus arriving at this site
	 **/
	// ----------------------------------------------------------------------
	public void arrive(BusIfc bus) {
		//MAXCustomerMainCargo mainCargo = (MAXCustomerMainCargo) bus.getCargo();
		//CustomerCargo cargo = (CustomerCargo) mainCargo;
		CustomerCargo cargo = (CustomerCargo)bus.getCargo();
		String letter = CommonLetterIfc.SUCCESS;
		// do the database lookup
		try {
			UtilityManagerIfc utility = (UtilityManagerIfc) bus
					.getManager(UtilityManagerIfc.TYPE);
			// Changes starts for Rev 1.0 (Ashish onlien points redemption)
            MAXCustomerManagerIfc customerManager = (MAXCustomerManagerIfc)bus.getManager(MAXCustomerManagerIfc.TYPE);
            // Changes ends for Rev 1.0 (Ashish onlien points redemption)
			LocaleRequestor localeRequestor = utility.getRequestLocales();
			//Changes for Rev 1.0 : Starts
			CustomerIfc customer = cargo.getCustomer();
			String customerID = customer.getCustomerID();
			if (Util.isEmpty(customerID)) {
				cargo.setNewCustomer(true);
				letter = CommonLetterIfc.FAILURE;
			} else {
				// Changes starts for Rev 1.0 (Ashish onlien points redemption)
                MAXCustomerSearchCriteriaIfc criteria = new MAXCustomerSearchCriteria(SearchType.SEARCH_BY_CUSTOMER_ID, customerID, localeRequestor);
                // Changes ends for Rev 1.0 (Ashish onlien points redemption)
				Locale extendedDataRequestLocale = cargo.getOperator()
						.getPreferredLocale();
				if (extendedDataRequestLocale == null) {
					extendedDataRequestLocale = LocaleMap
							.getLocale(LocaleMap.DEFAULT);
				}
				criteria.setExtendedDataRequestLocale(extendedDataRequestLocale);
				int maxCustomerItemsPerListSize = Integer.parseInt(Gateway
						.getProperty(Gateway.APPLICATION_PROPERTIES_GROUP,
								"MaxCustomerItemsPerListSize", "10"));
				criteria.setMaxCustomerItemsPerListSize(maxCustomerItemsPerListSize);
				int maxTotalCustomerItemsSize = Integer.parseInt(Gateway
						.getProperty(Gateway.APPLICATION_PROPERTIES_GROUP,
								"MaxTotalCustomerItemsSize", "40"));
				criteria.setMaxTotalCustomerItemsSize(maxTotalCustomerItemsSize);
				int maxNumberCustomerGiftLists = Integer.parseInt(Gateway
						.getProperty(Gateway.APPLICATION_PROPERTIES_GROUP,
								"MaxNumberCustomerGiftLists", "4"));
				criteria.setMaxNumberCustomerGiftLists(maxNumberCustomerGiftLists);
				customer = customerManager.getCustomer(criteria);

				if (customer instanceof MAXCustomer) {
					if (!((MAXCustomer) customer).getCustomerType()
							.equalsIgnoreCase(MAXCustomerConstantsIfc.CRM)
							&& ((MAXCustomerMainCargo)cargo).isTICCustomerLookup) {
						showAcknowledgeDialog(bus);
					} else {
						cargo.setCustomer(customer);
						bus.mail(new Letter(letter), BusIfc.CURRENT);
					}
				} else {
					cargo.setCustomer(customer);
					bus.mail(new Letter(letter), BusIfc.CURRENT);
				}
				//Changes for Rev 1.0 : Ends
				// HPQC 13_1 4007: duplicate customer added when customer looked
				// up after adding a customer
				// clearing flag so looked-up customer is not considered "new"
				// by SaveCustomerSite
				cargo.setNewCustomer(false);
			}
		} catch (DataException ce) {
			int error = ce.getErrorCode();
			if (((MAXCustomerMainCargo)cargo).isTICCustomerLookup) {
				if (error == 3)
					showErrorDialog(bus, "TICDBOFFLINE");
				else if (error == 6)
					showErrorDialog(bus, "NOTICDATA");
				else {

					cargo.setDataExceptionErrorCode(error);
					bus.mail(new Letter(letter), BusIfc.CURRENT);
				}
			} else {
				cargo.setDataExceptionErrorCode(error);
				letter = CommonLetterIfc.FAILURE;
				bus.mail(new Letter(letter), BusIfc.CURRENT);
			}			
		}
	}

	private void showErrorDialog(BusIfc bus, String message) {
		POSUIManagerIfc ui = (POSUIManagerIfc) bus
				.getManager(UIManagerIfc.TYPE);
		DialogBeanModel model = new DialogBeanModel();
		model.setResourceID(message);
		model.setType(DialogScreensIfc.YES_NO);
		model.setButtonLetter(DialogScreensIfc.BUTTON_YES, CommonLetterIfc.YES);
		model.setButtonLetter(DialogScreensIfc.BUTTON_NO, CommonLetterIfc.NO);
		ui.showScreen(POSUIManagerIfc.DIALOG_TEMPLATE, model);
	}

	private void showAcknowledgeDialog(BusIfc bus) {
		POSUIManagerIfc ui = (POSUIManagerIfc) bus
				.getManager(UIManagerIfc.TYPE);
		DialogBeanModel model = new DialogBeanModel();
		model.setResourceID("InvalidTICCustomer");
		model.setType(DialogScreensIfc.ERROR);
		ui.showScreen(POSUIManagerIfc.DIALOG_TEMPLATE, model);
	}

}
