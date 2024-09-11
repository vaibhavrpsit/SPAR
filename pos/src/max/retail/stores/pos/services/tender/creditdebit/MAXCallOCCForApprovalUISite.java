/** * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * ************************
 * Copyright (c) 2016-2017 Max Hypermarket.    All Rights Reserved. 
 *
 * Rev  	1.0  	21 Dec, 2016              Ashish Yadav              Credit Card FES
 * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * ***************************/
package max.retail.stores.pos.services.tender.creditdebit;

import java.util.HashMap;

import max.retail.stores.pos.manager.ifc.MAXUtilityManagerIfc;
import oracle.retail.stores.commerceservices.common.currency.CurrencyIfc;
import oracle.retail.stores.domain.DomainGateway;
import oracle.retail.stores.foundation.manager.device.MSRModel;
import oracle.retail.stores.foundation.tour.application.Letter;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.pos.ado.ADOException;
import oracle.retail.stores.pos.ado.tender.CreditTypeEnum;
import oracle.retail.stores.pos.ado.utility.Utility;
import oracle.retail.stores.pos.ado.utility.UtilityIfc;
import oracle.retail.stores.pos.manager.ifc.UtilityManagerIfc;
import oracle.retail.stores.pos.services.PosSiteActionAdapter;
import oracle.retail.stores.pos.services.tender.TenderCargo;
import oracle.retail.stores.pos.tdo.TDOException;
import oracle.retail.stores.pos.tdo.TDOFactory;
import oracle.retail.stores.pos.tdo.TDOUIIfc;
import oracle.retail.stores.pos.ui.POSUIManagerIfc;
import oracle.retail.stores.pos.ui.beans.CreditReferralBeanModel;

public class MAXCallOCCForApprovalUISite extends PosSiteActionAdapter {
	protected static final String TDO = "tdo.tender.CallOCC";

	public void arrive(BusIfc bus) {
		TenderCargo cargo = (TenderCargo) bus.getCargo();
		if ((("WithOutExpVal").equals(bus.getCurrentLetter().getName())))
			bus.mail(new Letter("WithOutExpValOCC"), BusIfc.CURRENT);
		else if ((cargo.getCurrentTransactionADO().containsSendItems())
				&& (determineCardType(cargo.getTenderAttributes(), bus) != CreditTypeEnum.HOUSECARD)
				&& (!(cargo.getCurrentTransactionADO().isCustomerPresent()))
				&& (exceedsAuthorizationThreshold(cargo.getTenderAttributes()))
				&& (occApprovalNotObtained(cargo.getTenderAttributes()))) {
			POSUIManagerIfc ui = (POSUIManagerIfc) bus.getManager("UIManager");

			TDOUIIfc tdo = null;
			try {
				tdo = (TDOUIIfc) TDOFactory.create("tdo.tender.CallOCC");
			} catch (TDOException e) {
				logger.warn("Could not create appropriate TDO: tdo.tender.CallOCC");
			}

			ui.showScreen("CALL_OCC", tdo.buildBeanModel(cargo.getTenderAttributes()));
		}
		else {
			bus.mail(new Letter("Continue"), BusIfc.CURRENT);
		}
	}

	public void depart(BusIfc bus) {
		if (!(bus.getCurrentLetter().getName().equals("Approved")))
			return;
		POSUIManagerIfc ui = (POSUIManagerIfc) bus.getManager("UIManager");

		CreditReferralBeanModel model = (CreditReferralBeanModel) ui.getModel("CALL_OCC");

		TenderCargo cargo = (TenderCargo) bus.getCargo();
		cargo.getTenderAttributes().put("OCC_APPROVAL_CODE", model.getApprovalCode());

		cargo.getTenderAttributes().put("AUTH_METHOD", "Manual");
	}

	protected boolean exceedsAuthorizationThreshold(HashMap tenderAttributes) {
		UtilityIfc util;
		try {
			util = Utility.createInstance();
		} catch (ADOException e) {
			String message = "Configuration problem: could not instantiate UtilityIfc instance";
			logger.error(message, e);
			throw new RuntimeException(message, e);
		}
		CurrencyIfc authThreshold = DomainGateway
				.getBaseCurrencyInstance(util.getParameterValue("AuthorizationThreshold", "100.00"));

		CurrencyIfc tenderAmount = null;
		tenderAmount = DomainGateway.getBaseCurrencyInstance((String) tenderAttributes.get("AMOUNT"));

		boolean result = false;
		if (tenderAmount.compareTo(authThreshold) == 1) {
			result = true;
		}

		return result;
	}

	protected CreditTypeEnum determineCardType(HashMap tenderAttributes, BusIfc bus) {
		UtilityManagerIfc utility = (UtilityManagerIfc) bus.getManager("UtilityManager");

		String number = (String) tenderAttributes.get("NUMBER");
		if (number == null) {
			number = ((MSRModel) tenderAttributes.get("MSR_MODEL")).getAccountNumber();
		}
		return ((MAXUtilityManagerIfc)utility).determineCreditType(number);
	}

	protected boolean occApprovalNotObtained(HashMap tenderAttributes) {
		String code = (String) tenderAttributes.get("OCC_APPROVAL_CODE");

		boolean result = true;
		if (code != null) {
			result = false;
		}
		return result;
	}
}