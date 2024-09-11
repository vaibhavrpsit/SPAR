/* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
  Copyright (c) 2012-2013 MAXHyperMarket, Inc.    All Rights Reserved.
  Rev 1.0	Kamlesh Pant	24 march 2022			Initial Draft: Changes for Till Reconcilation FES
* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */

package max.retail.stores.domain.arts;

import max.retail.stores.domain.financial.MAXFinancialTotals;
import oracle.retail.stores.domain.DomainGateway;
import oracle.retail.stores.domain.arts.ARTSStore;
import oracle.retail.stores.domain.arts.ARTSTill;
import oracle.retail.stores.domain.arts.ARTSTransaction;
import oracle.retail.stores.domain.arts.TransactionWriteDataTransaction;
import oracle.retail.stores.domain.financial.AbstractFinancialEntityIfc;
import oracle.retail.stores.domain.financial.FinancialTotalsIfc;
import oracle.retail.stores.domain.financial.RegisterIfc;
import oracle.retail.stores.domain.financial.TillIfc;
import oracle.retail.stores.domain.transaction.TillOpenCloseTransactionIfc;
import oracle.retail.stores.domain.transaction.TransactionIfc;
import oracle.retail.stores.foundation.manager.ifc.data.DataActionIfc;

public class MAXTransactionWriteDataTransaction extends TransactionWriteDataTransaction {

	// ---------------------------------------------------------------------
	/**
	 * Adds the data actions needed to save a till close transaction.
	 * 
	 * @param transaction
	 *            a till close transaction
	 **/
	// ---------------------------------------------------------------------
	protected void addSaveTillCloseTransactionActions(TransactionIfc transaction) {
		artsTransaction = new ARTSTransaction((TransactionIfc) transaction);

		// save the control transaction
		DataActionIfc dataAction = createDataAction(artsTransaction, "SaveControlTransaction");
		actions.add(dataAction);

		// this ensures that the change is backward compatible, because
		// only if till open-close transaction is used will the new data
		// operations
		// be executed
		if (transaction instanceof TillOpenCloseTransactionIfc) {
			TillOpenCloseTransactionIfc tocTransaction = (TillOpenCloseTransactionIfc) transaction;
			// save the till open/close transaction
			dataAction = createDataAction(transaction, "SaveTillOpenCloseTransaction");
			actions.add(dataAction);
			// build ARTS till for other operations
			TillIfc till = ((TillOpenCloseTransactionIfc) transaction).getTill();
			RegisterIfc register = ((TillOpenCloseTransactionIfc) transaction).getRegister();

			if (till.getStatus() == AbstractFinancialEntityIfc.STATUS_RECONCILED) {
				// update the safe as needed
				dataAction = createDataAction(transaction, "UpdateSafeFromTillOpenCloseTransaction");
				actions.add(dataAction);

				// Get deep copies of the till and register so they can be
				// loaded
				// with the till-close totals
				TillIfc aTill = (TillIfc) till.clone();
				RegisterIfc aRegister = (RegisterIfc) register.clone();

				// Combine the till and float totals objects
				FinancialTotalsIfc totals = DomainGateway.getFactory().getFinancialTotalsInstance();
				totals.addEndingFloatCount(tocTransaction.getEndingFloatCount());
				totals.getCombinedCount().setEntered(tocTransaction.getEndingCombinedEnteredCount());
				if (totals instanceof MAXFinancialTotals) {
					((MAXFinancialTotals) totals).setCouponDenominationCount(
							((MAXFinancialTotals) till.getTotals()).getCouponDenominationCount());
					((MAXFinancialTotals) totals)
							.setAcquirerBankDetails((((MAXFinancialTotals) till.getTotals()).getAcquirerBankDetails()));
					((MAXFinancialTotals) totals).setGiftCertificateDenomination(
							(((MAXFinancialTotals) till.getTotals()).getGiftCertificateDenomination()));
					((MAXFinancialTotals) totals)
							.setCashDenomination((((MAXFinancialTotals) till.getTotals()).getCashDenomination()));
				}
				// Set the counted totals on the till and register.
				aTill.setTotals(totals);
				aRegister.setTotals(totals);
				ARTSTill artsTill = new ARTSTill(aTill, aRegister);

				// creates or updates the till as needed
				dataAction = createDataAction(artsTill, "UpdateTillStatus");
				actions.add(dataAction);

				// creates or updates the till totals as needed
				dataAction = createDataAction(artsTill, "UpdateTillTotals");
				actions.add(dataAction);
				// Change done by prateek to save coupon details
				dataAction = createDataAction(artsTill, "SaveReconcileTenderDetails");
				actions.add(dataAction);

				// add to register totals
				dataAction = createDataAction(aRegister, "AddRegisterTotals");
				actions.add(dataAction);
				// add to store totals
				ARTSStore aStore = new ARTSStore(register.getWorkstation().getStore(), register.getBusinessDate());
				aStore.setFinancialTotals(aRegister.getTotals());
				dataAction = createDataAction(aStore, "AddStoreTotals");

				actions.add(dataAction);

			} else {
				ARTSTill artsTill = new ARTSTill(till, register);

				// creates or updates the till as needed
				dataAction = createDataAction(artsTill, "UpdateTillStatus");
				actions.add(dataAction);
			}

			// update the register and drawer
			dataAction = createDataAction(register, "UpdateRegisterStatus");
			actions.add(dataAction);
			// update the drawer
			dataAction = createDataAction(register, "UpdateDrawerStatus");
			actions.add(dataAction);
		}
	}
}
