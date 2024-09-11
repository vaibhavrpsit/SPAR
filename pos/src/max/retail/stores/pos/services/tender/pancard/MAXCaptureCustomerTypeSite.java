/********************************************************************************
 *   
 *	Copyright (c) 2018 MAX Hypermarket, Inc    All Rights Reserved.
 *	
 *
 *	Rev	1.0 	Aug 21, 2018		Bhanu Priya		Changes for Capture PAN CARD CR
 *
 ********************************************************************************/
package max.retail.stores.pos.services.tender.pancard;

import java.util.Arrays;
import java.util.Vector;

import max.retail.stores.domain.transaction.MAXSaleReturnTransaction;
import max.retail.stores.pos.ui.MAXPOSUIManagerIfc;
import max.retail.stores.pos.ui.beans.MAXSelectCustomerTypeBeanModel;
import oracle.retail.stores.commerceservices.common.currency.CurrencyIfc;
import oracle.retail.stores.domain.DomainGateway;
import oracle.retail.stores.foundation.manager.ifc.ParameterManagerIfc;
import oracle.retail.stores.foundation.manager.ifc.UIManagerIfc;
import oracle.retail.stores.foundation.manager.parameter.ParameterException;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.pos.services.PosSiteActionAdapter;
import oracle.retail.stores.pos.services.tender.TenderCargo;
import oracle.retail.stores.pos.ui.POSUIManagerIfc;

public class MAXCaptureCustomerTypeSite extends PosSiteActionAdapter

{
	private static final long serialVersionUID = 1L;

	public void arrive(BusIfc bus) {
		TenderCargo cargo = (TenderCargo) bus.getCargo();
		String parameter = null;
		ParameterManagerIfc parameterManager = (ParameterManagerIfc) bus
				.getManager(ParameterManagerIfc.TYPE);
		POSUIManagerIfc ui = (POSUIManagerIfc) bus
				.getManager(UIManagerIfc.TYPE);
		if (cargo.getTransaction() instanceof MAXSaleReturnTransaction
				&& cargo.getTransaction().getTransactionType() == 1) {
			try {
				parameter = "AllowCapturePANCardDetails";
				String capturePAN = parameterManager.getStringValue(parameter);
				if (capturePAN.equalsIgnoreCase("Y")) {
					parameter = "ThreasholdValueForCapturePAN";
					CurrencyIfc thresoldValue = DomainGateway
							.getBaseCurrencyInstance(parameterManager
									.getStringValue(parameter));

					CurrencyIfc grandTotal = cargo.getTransaction()
							.getTenderTransactionTotals().getGrandTotal();

					if (grandTotal.compareTo(thresoldValue) == 1) {
						MAXSelectCustomerTypeBeanModel beanModel = new MAXSelectCustomerTypeBeanModel();
						parameter = "CaptureCustomerType";
						Vector<String> custTypeVac = null;

						String[] custType = parameterManager
								.getStringValues(parameter);
						custTypeVac = new Vector<String>(
								Arrays.asList(custType));
						beanModel.setCustomerType(custTypeVac);
						ui.showScreen(MAXPOSUIManagerIfc.CUSTOMER_TYPE_CAPTURE,
								beanModel);

					} else {
						bus.mail("NotExceed");
					}
				}
				else {
					bus.mail("NotExceed");
				}
			} catch (ParameterException pe) {
				bus.mail("NotExceed");
				logger.warn("Couldn't retrieve parameter: " + parameter, pe);
			}
		} else {
			bus.mail("NotExceed");
		}
	}
}
