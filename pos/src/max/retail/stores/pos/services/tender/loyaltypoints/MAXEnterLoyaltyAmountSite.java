/* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
 *  Copyright (c) 2016 - 2017 MAX Hypermarket, Inc.    All Rights Reserved.
 *	
 *	Rev 1.0     Jan 06, 2016		Ashish Yadav		Online Points Redemption FES
 * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */

package max.retail.stores.pos.services.tender.loyaltypoints;

import max.retail.stores.domain.customer.MAXCustomer;
import max.retail.stores.domain.customer.MAXCustomerIfc;
import max.retail.stores.pos.ui.MAXPOSUIManagerIfc;
import oracle.retail.stores.domain.transaction.LayawayPaymentTransaction;
import oracle.retail.stores.domain.transaction.SaleReturnTransaction;
import oracle.retail.stores.foundation.manager.ifc.UIManagerIfc;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.pos.services.PosSiteActionAdapter;
import oracle.retail.stores.pos.services.tender.TenderCargo;
import oracle.retail.stores.pos.ui.POSUIManagerIfc;

public class MAXEnterLoyaltyAmountSite extends PosSiteActionAdapter {

	private static final long serialVersionUID = 1L;

	public void arrive(BusIfc bus) {
		POSUIManagerIfc ui = (POSUIManagerIfc) bus
				.getManager(UIManagerIfc.TYPE);

		/* Rev 1.1 Start */
		TenderCargo cargo = (TenderCargo) bus.getCargo();
		MAXCustomerIfc customer = null;
		if (cargo.getCurrentTransactionADO().toLegacy() instanceof SaleReturnTransaction) {
			SaleReturnTransaction transaction = ((SaleReturnTransaction) cargo
					.getCurrentTransactionADO().toLegacy());
			customer = (MAXCustomerIfc) transaction.getCustomer();

		} else if (cargo.getCurrentTransactionADO().toLegacy() instanceof LayawayPaymentTransaction) {
			LayawayPaymentTransaction transaction = ((LayawayPaymentTransaction) cargo
					.getCurrentTransactionADO().toLegacy());

			customer = (MAXCustomerIfc) transaction.getCustomer();

		} else {
			SaleReturnTransaction transaction = ((SaleReturnTransaction) cargo
					.getCurrentTransactionADO().toLegacy());
			customer = (MAXCustomerIfc) transaction.getCustomer();
		}
		if (cargo.getTransaction().getCustomer() != null
				&& cargo.getTransaction().getCustomer() instanceof MAXCustomer
				&& cargo.getTransaction().getCustomer().getCustomerID() == null
				&& ((((MAXCustomer) (cargo.getTransaction().getCustomer()))
						.getLoyaltyCardNumber()) != null)) {
			cargo.getTransaction()
					.getCustomer()
					.setCustomerID(
							((MAXCustomer) (cargo.getTransaction()
									.getCustomer())).getLoyaltyCardNumber());
		}
		customer.setLoyaltyTimeout(0);
		customer.setLoyaltyRetryTimeout(0);
		customer.setLoyaltyotp(0);
		customer.setOtpValidation(false);

		ui.showScreen(MAXPOSUIManagerIfc.ENTER_LOYALTY_POINTS_AMOUNT);
		// bus.mail("Next");Commented by Vaibhav
	}
}
