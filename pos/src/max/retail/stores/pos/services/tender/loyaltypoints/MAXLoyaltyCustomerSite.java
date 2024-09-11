/* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
 *     Copyright (c) 2010 Lifestyle India Pvt Ltd.    All Rights Reserved.
 *		
 * Rev 1.0  Jan 7, 2011 1:39:53 PM puneet.hasija
 * Initial revision.
 * Resolution for FES_LMG_India_Customer_Loyalty_v1.1
 * Managing ORPOS's Purchase Order functionality and MAX's Customer Loyalty functionality based on 
 * 'PurchaseOrderOrLoyaltyCustomer' parameter value.
 * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */

package max.retail.stores.pos.services.tender.loyaltypoints;

import oracle.retail.stores.foundation.manager.ifc.ParameterManagerIfc;
import oracle.retail.stores.foundation.manager.parameter.ParameterException;
import oracle.retail.stores.foundation.tour.application.Letter;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.pos.services.PosSiteActionAdapter;

public class MAXLoyaltyCustomerSite extends PosSiteActionAdapter {
	// ----------------------------------------------------------------------
	/**
	
	
	/**
	 * @param bus
	 *            the bus arriving at this site
	 */
	// --------------------------------------------------------------------------
	public void arrive(BusIfc bus) {
		ParameterManagerIfc pm = (ParameterManagerIfc) bus.getManager(ParameterManagerIfc.TYPE);
		String parameterValue = "LoyaltyCustomer";
		
		
		try {
			parameterValue = pm.getStringValue("LoyaltyCustomer");
		} catch (ParameterException pe) {
			logger.error("" + pe.getMessage() + "");
		}


		if (parameterValue.equalsIgnoreCase("LoyaltyCustomer")) {
			bus.mail(new Letter("LoyaltyCustomer"), BusIfc.CURRENT);
		} 
			bus.mail(new Letter("LoyaltyCustomer"), BusIfc.CURRENT);
		
	}
}
