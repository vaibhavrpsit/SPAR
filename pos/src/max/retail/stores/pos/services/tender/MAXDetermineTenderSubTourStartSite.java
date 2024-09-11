/* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
*  Copyright (c) 2016	MAX HyperMarkets.    All Rights Reserved.
*
*	Rev 1.0 	27 Oct 2017		Jyoti Yadav		Changes for Innoviti Integration CR
*
* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
package max.retail.stores.pos.services.tender;

import max.retail.stores.pos.services.giftcard.MAXGiftCardUtilities;
import max.retail.stores.pos.ui.MAXPOSUIManagerIfc;
import oracle.retail.stores.common.utility.Util;
import oracle.retail.stores.domain.transaction.LayawayTransactionIfc;
import oracle.retail.stores.foundation.manager.ifc.DataManagerIfc;
import oracle.retail.stores.foundation.manager.ifc.ParameterManagerIfc;
import oracle.retail.stores.foundation.manager.ifc.UIManagerIfc;
import oracle.retail.stores.foundation.manager.parameter.ParameterException;
import oracle.retail.stores.foundation.tour.application.Letter;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.pos.services.PosSiteActionAdapter;
import oracle.retail.stores.pos.services.common.DataManagerOnlineStatus;
import oracle.retail.stores.pos.services.common.EventOriginatorInfoBean;
import oracle.retail.stores.pos.services.tender.TenderCargo;
import oracle.retail.stores.pos.ui.POSUIManagerIfc;
import oracle.retail.stores.pos.ui.beans.NavigationButtonBeanModel;
import oracle.retail.stores.pos.ui.beans.POSBaseBeanModel;

public class MAXDetermineTenderSubTourStartSite extends PosSiteActionAdapter {

	public void arrive(BusIfc bus) {
		TenderCargo cargo = (TenderCargo) bus.getCargo();
		//System.out.println("MAXDetermineTenderSubTourStartSite 31:"+cargo.getCurrentTransactionADO().getBalanceDue());
		EventOriginatorInfoBean.setEventOriginator("MAXDetermineTenderSubTourStartSite.arrive");
		ParameterManagerIfc pm = (ParameterManagerIfc) bus.getManager(ParameterManagerIfc.TYPE);
		boolean giftVoucherAllowed = false;
		try {
			giftVoucherAllowed = pm.getBooleanValue("UseGiftCertificateAsVouchers").booleanValue();
		} catch (ParameterException e) {
			logger.error(Util.throwableToString(e));
		}
		if (giftVoucherAllowed && cargo.getSubTourLetter().equalsIgnoreCase("GiftCert")) {
			cargo.setSubTourLetter("GiftVoucher");
			bus.mail(new Letter("GiftVoucher"), BusIfc.CURRENT);
		} else {
			bus.mail(new Letter(cargo.getSubTourLetter()), BusIfc.CURRENT);
		}
		boolean isLayaway = false;
		if (cargo.getTransaction() instanceof LayawayTransactionIfc)
			isLayaway = true;
		// Changes for online/offline credit card tender

		DataManagerIfc dataManager = (DataManagerIfc) bus.getManager(DataManagerIfc.TYPE);
		// This Status is as per the Last Database Transaction Done
		boolean databaseStatus = DataManagerOnlineStatus.getStatus(dataManager);

		POSUIManagerIfc ui = (POSUIManagerIfc) bus.getManager(UIManagerIfc.TYPE);

		// Changes for disabling online button in re-entry mode
		POSBaseBeanModel model = new POSBaseBeanModel();
		NavigationButtonBeanModel localModel = new NavigationButtonBeanModel();
		boolean transReentryMode = cargo.getRegister().getWorkstation().isTransReentryMode();
		if (transReentryMode) {
			// POSBaseBeanModel model=new POSBaseBeanModel();
			// NavigationButtonBeanModel
			// localModel = new NavigationButtonBeanModel();
			localModel.setButtonEnabled("OnlineCredit", false);
			model.setLocalButtonBeanModel(localModel);
			if (isLayaway)
				ui.showScreen(POSUIManagerIfc.DIALOG_TEMPLATE,
						MAXGiftCardUtilities.createInvalidTransactionDialogModel());
			else if (("CreditDebit").equals(cargo.getSubTourLetter()))
				ui.showScreen(MAXPOSUIManagerIfc.CREDIT_DEBIT_ONLINE_OFFLINE, model);
		} else {
			localModel.setButtonEnabled("OnlineCredit", true);
			model.setLocalButtonBeanModel(localModel);
			if (isLayaway)
				ui.showScreen(POSUIManagerIfc.DIALOG_TEMPLATE,
						MAXGiftCardUtilities.createInvalidTransactionDialogModel());
			else if (("CreditDebit").equals(cargo.getSubTourLetter()))
				ui.showScreen(MAXPOSUIManagerIfc.CREDIT_DEBIT_ONLINE_OFFLINE, model);
		}
	}
}
