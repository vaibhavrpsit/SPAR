/* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
 *  Copyright (c) 2016 - 2017 MAX Hypermarket, Inc.    All Rights Reserved.
 *	
 *	Rev 1.1     Oct 19, 2016		Mansi Goel			Change code acc to 14 version
 *	Rev 1.0     Oct 17, 2016		Ashish Yadav		Changes for Code Merging
 *
 * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */

package max.retail.stores.pos.services.sale;

import java.util.Locale;

import max.retail.stores.domain.customer.MAXCustomerIfc;
import max.retail.stores.domain.customer.MAXTICCustomerIfc;
import max.retail.stores.domain.transaction.MAXSaleReturnTransaction;
import max.retail.stores.domain.transaction.MAXSaleReturnTransactionIfc;
import max.retail.stores.pos.services.customer.main.MAXCustomerMainCargo;
import oracle.retail.stores.common.utility.LocaleMap;
import oracle.retail.stores.domain.utility.LocaleConstantsIfc;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.pos.manager.ifc.TransactionUtilityManagerIfc;
import oracle.retail.stores.pos.services.sale.CustomerReturnShuttle;

public class MAXCustomerReturnShuttle extends CustomerReturnShuttle {

	/**
	 * 
	 */
	private static final long serialVersionUID = 5798695949672848155L;

	public void load(BusIfc bus) {
		super.load(bus);
	}

	public void unload(BusIfc bus) {
		super.unload(bus);

		MAXSaleCargoIfc cargo = (MAXSaleCargoIfc) bus.getCargo();
		MAXSaleReturnTransactionIfc transaction = (MAXSaleReturnTransactionIfc) cargo
				.getTransaction();

		/* to create new transaction start loyalty customer */
		// Changes for Rev 1.1 : Starts
		TransactionUtilityManagerIfc utility = (TransactionUtilityManagerIfc) bus
				.getManager(TransactionUtilityManagerIfc.TYPE);
		// Changes for Rev 1.1 : Ends

		if (cargo.getTransaction() == null) {
			MAXSaleReturnTransactionIfc transactionCreate = new MAXSaleReturnTransaction();

			// Initialize fields specific to SaleReturnTransaction
			transactionCreate.setCashier(cargo.getOperator());
			if (cargo.getSalesAssociate() != null) {
				transactionCreate.setSalesAssociate(cargo.getSalesAssociate());
			} else {
				transactionCreate.setSalesAssociate(cargo.getOperator());
			}
			// Initializes the fields common to all transactions.
			// Changes for Rev 1.1 : Starts
			utility.initializeTransaction(transactionCreate);
			// Changes for Rev 1.1 : Ends

			// Set up default locales for pole display and receipt
			Locale defaultLocale = LocaleMap.getLocale(LocaleConstantsIfc.DEFAULT_LOCALE);

			LocaleMap.putLocale(LocaleConstantsIfc.RECEIPT, defaultLocale);
			LocaleMap.putLocale(LocaleConstantsIfc.POLE_DISPLAY, defaultLocale);
			LocaleMap.putLocale(LocaleConstantsIfc.DEVICES, defaultLocale);
			transaction = transactionCreate;

		}
		/* to create new transaction END loyalty customer */
		// Changes ends for rev 1.0

		/* tic customer cr changes akhilesh */
		if (customerMainCargo instanceof MAXCustomerMainCargo) {
			MAXCustomerMainCargo maxcargo = (MAXCustomerMainCargo) customerMainCargo;
			MAXTICCustomerIfc ticcustomer = maxcargo.getTICCustomer();
			
			
			// Changes starts for rev 1.0
			if (transaction != null && ticcustomer != null) {
				transaction.setMAXTICCustomer(ticcustomer);
				transaction.setTicCustomerVisibleFlag(true);
				// Changes ends for rev 1.0
			}
			// Changes starts for rev 1.0
			if (ticcustomer == null
					&& maxcargo.getTransaction() != null
					&& maxcargo.getTransaction() instanceof MAXSaleReturnTransaction) {
				MAXSaleReturnTransaction maxTransaction = (MAXSaleReturnTransaction) maxcargo
						.getTransaction();
				ticcustomer = (MAXTICCustomerIfc) maxTransaction
						.getMAXTICCustomer();
				transaction.setMAXTICCustomer(ticcustomer);
				transaction.setTicCustomerVisibleFlag(true);
			}
			// Changes ends for rev 1.0
			MAXCustomerIfc customer = (MAXCustomerIfc) maxcargo.getCustomer();
			if (customer != null) {
				// Changes starts for rev 1.0
				if (transaction.getMAXTICCustomer() != null) {

					customer.setMAXTICCustomer(transaction.getMAXTICCustomer());
					customer.setTicCustomerVisibleFlag(transaction.isTicCustomerVisibleFlag());
				}
				// Changes ends for rev 1.0
				cargo.setTicCustomer(ticcustomer);
				transaction.setCustomer(customer);

			}
		}

		// Changes starts for rev 1.0
		cargo.setTransaction(transaction);
		/* tic customer cr changes akhilesh */
		// Changes ends for rev 1.0
	}
}
