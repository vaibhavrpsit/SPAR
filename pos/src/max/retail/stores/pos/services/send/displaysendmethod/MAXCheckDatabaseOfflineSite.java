/********************************************************************************
 *   
 *	Copyright (c) 2016 - 2017 MAX Hypermarket, Inc    All Rights Reserved.
 *	
 *
 *  Rev 1.0     1 Dec , 2016	        Ashish Yadav		Changes for SEND FES
 *
 ********************************************************************************/
package max.retail.stores.pos.services.send.displaysendmethod;

import max.retail.stores.domain.arts.MAXReadShippingMethodTransaction;
import oracle.retail.stores.common.utility.LocaleRequestor;
import oracle.retail.stores.domain.DomainGateway;
import oracle.retail.stores.domain.arts.DataTransactionFactory;
import oracle.retail.stores.domain.arts.DataTransactionKeys;
import oracle.retail.stores.domain.arts.ReadShippingMethodTransaction;
import oracle.retail.stores.domain.shipping.ShippingMethodIfc;
import oracle.retail.stores.domain.shipping.ShippingMethodSearchCriteriaIfc;
import oracle.retail.stores.foundation.manager.data.DataException;
import oracle.retail.stores.foundation.manager.ifc.UIManagerIfc;
import oracle.retail.stores.foundation.tour.application.Letter;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.pos.manager.ifc.UtilityManagerIfc;
import oracle.retail.stores.pos.services.PosSiteActionAdapter;
import oracle.retail.stores.pos.services.common.CommonLetterIfc;
import oracle.retail.stores.pos.services.send.address.SendCargo;
import oracle.retail.stores.pos.ui.POSUIManagerIfc;

//------------------------------------------------------------------------------
/**
 * Site to check if database is offline $Revision: 4$
 **/
// ------------------------------------------------------------------------------
public class MAXCheckDatabaseOfflineSite extends PosSiteActionAdapter {
	/**
	 * A signal to tell if the database is online. Set the default to be true.
	 **/
	protected boolean signal = true;
	/**
	 * revision number supplied by Team Connection
	 **/
	public static final String revisionNumber = "$Revision: 4$";

	// --------------------------------------------------------------------------
	/**
	 * For good postal code, checks database offline
	 * 
	 * @param BusIfc
	 *            bus
	 **/
	// --------------------------------------------------------------------------
	public void arrive(BusIfc bus) {
		POSUIManagerIfc ui;
		ui = (POSUIManagerIfc) bus.getManager(UIManagerIfc.TYPE);

		// A signal to tell if the database is online. Set the default to be
		// true.
		signal = true;
		SendCargo cargo = (SendCargo) bus.getCargo();

		if (cargo.isTransactionLevelSendInProgress()) {
			bus.mail(new Letter(CommonLetterIfc.NEXT), BusIfc.CURRENT);
			return;
		}
		// Try to do a database read.
		try {
			MAXReadShippingMethodTransaction shippingTransaction = null;

			shippingTransaction = (MAXReadShippingMethodTransaction) DataTransactionFactory
					.create(DataTransactionKeys.READ_SHIPPING_METHOD_TRANSACTION);
			// Changes start for Rev 1.0 (Ashish :Send)
			UtilityManagerIfc utility =
		            (UtilityManagerIfc) bus.getManager(UtilityManagerIfc.TYPE);
			LocaleRequestor localeReq = utility.getRequestLocales();
            ShippingMethodSearchCriteriaIfc searchCriteria = DomainGateway.getFactory().getShippingMethodSearchCriteria();
            searchCriteria.setLocaleRequestor(localeReq);
			// read the shipping mehtods
			ShippingMethodIfc[] methods = shippingTransaction
					.readShippingMethod(searchCriteria);
			// Changes start for Rev 1.0 (Ashish :Send)
		} catch (DataException e) {
			signal = false;
		}

		// Send letter NEXT to move to next step, the shipping method screen
		bus.mail(new Letter(CommonLetterIfc.NEXT), BusIfc.CURRENT);
	}
}