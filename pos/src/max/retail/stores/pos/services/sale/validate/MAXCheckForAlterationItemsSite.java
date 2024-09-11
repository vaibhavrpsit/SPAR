/* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
  Copyright (c) 2013	MAX HyperMarkets.    All Rights Reserved.
	Rev 1.0 	20/05/2013		Prateek		Initial Draft: Changes for TIC Customer Integration
 * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
package max.retail.stores.pos.services.sale.validate;

import max.retail.stores.domain.customer.MAXCustomerConstantsIfc;
import max.retail.stores.domain.customer.MAXCustomerIfc;
import max.retail.stores.domain.lineitem.MAXSaleReturnLineItemIfc;
import max.retail.stores.pos.services.sale.MAXSaleCargoIfc;
import oracle.retail.stores.domain.lineitem.AbstractTransactionLineItemIfc;
import oracle.retail.stores.domain.transaction.SaleReturnTransactionIfc;
import oracle.retail.stores.domain.transaction.TransactionConstantsIfc;
import oracle.retail.stores.foundation.tour.application.Letter;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.pos.services.PosSiteActionAdapter;

public class MAXCheckForAlterationItemsSite extends PosSiteActionAdapter {
	// ----------------------------------------------------------------------
	/**
	 * serialVersionUID long
	 **/
	// ----------------------------------------------------------------------
	private static final long serialVersionUID = 1808959853844953160L;

	public static final String revisionNumber = "$Revision: 1.2 $";

	// --------------------------------;--------------------------------------
	/**
	 * Check if the transaction contains alteration item(s) and mail a proper
	 * letter
	 * <P>
	 * 
	 * @param bus
	 *            Service Bus
	 **/
	// ----------------------------------------------------------------------
	public void arrive(BusIfc bus) {
		// Default the letter value to Continue
		String letter = "Continue";

		boolean hasAlterationItem = false;
		boolean crmCustomer = false;
		// SaleCargoIfc cargo = (SaleCargoIfc) bus.getCargo();
		MAXSaleCargoIfc cargo = (MAXSaleCargoIfc) bus.getCargo();
		SaleReturnTransactionIfc transaction = cargo.getTransaction();

		AbstractTransactionLineItemIfc[] lineItems = null;
		// MAX Change Rev :1.1 Start
		if (transaction.getCustomer() instanceof MAXCustomerIfc) {
			MAXCustomerIfc customer = (MAXCustomerIfc) transaction
					.getCustomer();

			if (transaction != null) {
				lineItems = transaction.getLineItems();
				// if customer is present then check customer is a loyalty
				// customer or not
				if (customer != null) {

					if (customer.getCustomerType() != null) {
						if (customer.getCustomerType().equals(
								MAXCustomerConstantsIfc.CRM)
								|| (customer.isCustomerTag())) {
							crmCustomer = true;
						}
					} else {
						crmCustomer = false;
					}
				} else
				// customer is not present.
				if (transaction.getTransactionType() == TransactionConstantsIfc.TYPE_RETURN) {
					crmCustomer = true;

				} else {
					crmCustomer = false;
				}

			}
		}
		// MAX Change Rev :1.1 End
		if (lineItems != null && lineItems.length > 0) {
			for (int i = 0; i < lineItems.length; i++) {
				if (((MAXSaleReturnLineItemIfc) lineItems[i])
						.getAlterationItemFlag()) {
					hasAlterationItem = true;
					break;
				}
			}
		}

		if (hasAlterationItem) {
			letter = "AlterationItems";
		}
		if (bus.getCurrentLetter().getName().equalsIgnoreCase("DoNotRedeem")) {
			letter = "DoNotReedem";
		} else if (!crmCustomer) {
			letter = "LinkCRMCustomer";
		} else {
			letter = "Success";
		}

		bus.mail(new Letter(letter), BusIfc.CURRENT);

	}

}
