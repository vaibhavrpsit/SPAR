/* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
 *     Copyright (c) 2010 Lifestyle India Pvt Ltd.    All Rights Reserved.
 *		
 * 
 * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
package max.retail.stores.pos.services.tender.creditdebit;

import java.util.HashMap;

import oracle.retail.stores.commerceservices.common.currency.CurrencyIfc;
import oracle.retail.stores.domain.DomainGateway;
import oracle.retail.stores.domain.tender.AuthorizableTenderIfc;
import oracle.retail.stores.foundation.manager.ifc.UIManagerIfc;
import oracle.retail.stores.foundation.tour.application.Letter;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.pos.ado.ADOException;
import oracle.retail.stores.pos.ado.tender.TenderConstants;
import oracle.retail.stores.pos.ado.utility.Utility;
import oracle.retail.stores.pos.ado.utility.UtilityIfc;
import oracle.retail.stores.pos.services.PosSiteActionAdapter;
import oracle.retail.stores.pos.services.tender.TenderCargo;
import oracle.retail.stores.pos.ui.POSUIManagerIfc;
import oracle.retail.stores.pos.ui.beans.CreditReferralBeanModel;

/**
 * Displays referral screen under certain conditions
 */
public class MAXPineLabCallOCCForApprovalUISite extends PosSiteActionAdapter {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	protected static final String TDO = "tdo.tender.CallOCC";

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * */
	public void arrive(BusIfc bus) {
		
			if ((("WithOutExpVal").equals(bus.getCurrentLetter().getName())))
				bus.mail(new Letter("WithOutExpValOCC"), BusIfc.CURRENT);
			else
				bus.mail(new Letter("Continue"), BusIfc.CURRENT);

	}

	
	public void depart(BusIfc bus) {
		if (bus.getCurrentLetter().getName().equals("Approved")) {
			POSUIManagerIfc ui = (POSUIManagerIfc) bus.getManager(UIManagerIfc.TYPE);
			CreditReferralBeanModel model = (CreditReferralBeanModel) ui.getModel(POSUIManagerIfc.CALL_OCC);
			TenderCargo cargo = (TenderCargo) bus.getCargo();
			cargo.getTenderAttributes().put(TenderConstants.OCC_APPROVAL_CODE, model.getApprovalCode());
			cargo.getTenderAttributes().put(TenderConstants.AUTH_METHOD, AuthorizableTenderIfc.AUTHORIZATION_METHOD_MANUAL);
		}
	}

	/**
	 * Determine whether tender amount exceeds authorization threshold amount
	 * 
	 * @param tenderAttributes
	 * @return
	 */
	protected boolean exceedsAuthorizationThreshold(HashMap tenderAttributes) {
		// Get the amounts to compare
		UtilityIfc util;
		try {
			util = Utility.createInstance();
		} catch (ADOException e) {
			String message = "Configuration problem: could not instantiate UtilityIfc instance";
			logger.error(message, e);
			throw new RuntimeException(message, e);
		}
		CurrencyIfc authThreshold = DomainGateway.getBaseCurrencyInstance(util.getParameterValue("AuthorizationThreshold", "100.00"));
		CurrencyIfc tenderAmount = null;
		tenderAmount = DomainGateway.getBaseCurrencyInstance((String) tenderAttributes.get(TenderConstants.AMOUNT));

		// compare the amounts
		boolean result = false;
		if (tenderAmount.compareTo(authThreshold) == CurrencyIfc.GREATER_THAN) {
			result = true;
		}

		return result;
	}

	
	protected boolean occApprovalNotObtained(HashMap tenderAttributes) {
		String code = (String) tenderAttributes.get(TenderConstants.OCC_APPROVAL_CODE);
		boolean result = true;
		if (code != null) {
			result = false;
		}
		return result;
	}
}
