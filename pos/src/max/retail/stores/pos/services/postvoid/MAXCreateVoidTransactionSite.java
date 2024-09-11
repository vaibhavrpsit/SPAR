/* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
*   Copyright (c) 2012 - 2013 MAXHyperMarket, Inc.    All Rights Reserved.
*  Rev 1.0  28/May/2013	Jyoti Rawal, Initial Draft: Changes for Credit Card Functionality 
* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
package max.retail.stores.pos.services.postvoid;

import max.retail.stores.domain.transaction.MAXTransactionTotalsIfc;
import oracle.retail.stores.commerceservices.common.currency.CurrencyIfc;
import oracle.retail.stores.domain.DomainGateway;
import oracle.retail.stores.domain.customer.CustomerIfc;
import oracle.retail.stores.domain.transaction.TransactionConstantsIfc;
import oracle.retail.stores.domain.transaction.TransactionIfc;
import oracle.retail.stores.domain.transaction.VoidTransaction;
import oracle.retail.stores.foundation.manager.ifc.ParameterManagerIfc;
import oracle.retail.stores.foundation.manager.ifc.UIManagerIfc;
import oracle.retail.stores.foundation.tour.application.Letter;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.pos.ado.ADOException;
import oracle.retail.stores.pos.ado.context.ContextFactory;
import oracle.retail.stores.pos.ado.journal.JournalActionEnum;
import oracle.retail.stores.pos.ado.journal.JournalFactory;
import oracle.retail.stores.pos.ado.journal.JournalFactoryIfc;
import oracle.retail.stores.pos.ado.journal.JournalFamilyEnum;
import oracle.retail.stores.pos.ado.journal.RegisterJournalIfc;
import oracle.retail.stores.pos.ado.lineitem.TenderLineItemCategoryEnum;
import oracle.retail.stores.pos.ado.store.RegisterADO;
import oracle.retail.stores.pos.ado.tender.TenderADOIfc;
import oracle.retail.stores.pos.ado.tender.TenderDebitADO;
import oracle.retail.stores.pos.ado.tender.TenderErrorCodeEnum;
import oracle.retail.stores.pos.ado.tender.TenderException;
import oracle.retail.stores.pos.ado.transaction.TransactionPrototypeEnum;
import oracle.retail.stores.pos.ado.transaction.VoidTransactionADO;
import oracle.retail.stores.pos.ado.utility.AuthorizationException;
import oracle.retail.stores.pos.services.PosSiteActionAdapter;
import oracle.retail.stores.pos.services.postvoid.VoidCargo;
import oracle.retail.stores.pos.ui.DialogScreensIfc;
import oracle.retail.stores.pos.ui.POSUIManagerIfc;
import oracle.retail.stores.pos.ui.beans.DialogBeanModel;

//--------------------------------------------------------------------------
/**
 * This site creates a new void transaction.
 * 
 * @version $Revision: 1.7 $
 */
// --------------------------------------------------------------------------
public class MAXCreateVoidTransactionSite extends PosSiteActionAdapter {
	/**
	 * 
	 */
	private static final long serialVersionUID = -5196100674448864274L;

	/**
	 * revision number of this class
	 */
	public static final String revisionNumber = "$Revision: 1.7 $";

	private String dialogId = null;
	private String letter;
	private String transactionType;
		
	public static final String POST_VOID_LABEL = "POST VOID";

	// ----------------------------------------------------------------------
	/**
	 * Creates a new void transaction.
	 * <P>
	 * 
	 * @param bus
	 *            Service Bus
	 */
	// ----------------------------------------------------------------------
	public void arrive(BusIfc bus) {
		// set bean model
		VoidCargo cargo = (VoidCargo) bus.getCargo();
		ParameterManagerIfc pm = (ParameterManagerIfc) bus
				.getManager(ParameterManagerIfc.TYPE);
		CustomerIfc postVoidCustomer = null;
		if (cargo.getOriginalTransactionADO() != null) {
			if (cargo.getOriginalTransactionADO().getCustomer() != null) {
				postVoidCustomer = cargo.getOriginalTransactionADO()
						.getCustomer();
			}
		}

		
		// create and set data on void ADO transaction
		VoidTransactionADO voidTxn = createVoidTranasction(cargo, pm);
		voidTxn.setOriginalTransaction(cargo.getOriginalTransactionADO());
		// set on cargo
		cargo.setCurrentTransactionADO(voidTxn);
//		String transactionID = cargo.getCurrentTransactionADO().getTransactionID().substring(8, 12);
//		
//		String orgtransactionID = cargo.getOriginalTransactionADO().getTransactionID().substring(8, 12);
	
		VoidTransaction rdo = (VoidTransaction) cargo
				.getCurrentTransactionADO().toLegacy();
		if (rdo.getCustomer() == null) {
			rdo.setCustomer(postVoidCustomer);
		}

		int traType = rdo.getTransactionType();
		if (traType == TransactionIfc.TYPE_VOID) {
			/* transactionType = POST_VOID_LABEL; */
			transactionType = TransactionConstantsIfc.TYPE_DESCRIPTORS[traType];
		}

		// journal reason code
		JournalFactoryIfc jrnlFact = null;
		try {
			jrnlFact = JournalFactory.getInstance();
		} catch (ADOException e) {
			logger.error(JournalFactoryIfc.INSTANTIATION_ERROR, e);
			throw new RuntimeException(JournalFactoryIfc.INSTANTIATION_ERROR, e);
		}
		RegisterJournalIfc registerJournal = jrnlFact.getRegisterJournal();

		registerJournal.journal(voidTxn, JournalFamilyEnum.TRANSACTION,
				JournalActionEnum.VOID_REASON_CODE);
		registerJournal.journal(voidTxn, JournalFamilyEnum.TRANSACTION,
				JournalActionEnum.ORIG_TRANS);
		registerJournal.journal(voidTxn, JournalFamilyEnum.LINEITEM,
				JournalActionEnum.VOID);
		registerJournal.journal(voidTxn, JournalFamilyEnum.TRANSACTION,
				JournalActionEnum.ORIG_TOTAL);

		// process the voided transaction (reverse tenders, etc)
		try {
			voidTxn.process();
		} catch (AuthorizationException e) {
			// TODO: Change flow for call center screen if authorization failed
		}
        
		CurrencyIfc offAmt = ((MAXTransactionTotalsIfc) rdo.getOriginalTransaction().getTransactionTotals()).getOffTotal();
		((MAXTransactionTotalsIfc) rdo.getTransactionTotals()).setOffTotal(offAmt);
		String letterName = "Success";
		POSUIManagerIfc ui = (POSUIManagerIfc) bus
				.getManager(UIManagerIfc.TYPE);
	
		if (letter != null && !letter.equals("VoidOfflineTransaction")) {
			// check to see if we need to go to pin pad station
			TenderADOIfc[] voidTenders = voidTxn
					.getTenderLineItems(TenderLineItemCategoryEnum.VOID_AUTH_PENDING);
			for (int i = 0; i < voidTenders.length; i++) {
				if (voidTenders[i] instanceof TenderDebitADO) {
					cargo.setNextTender(voidTenders[i]);
					letterName = "DebitRefund";
					break;
				}
			}
			bus.mail(new Letter(letterName), BusIfc.CURRENT);
		} else {
			// yash added if condition.
			letter = "Success";
			if (dialogId != null) {
				displayDialog(ui, dialogId, DialogScreensIfc.ERROR, letter);
				return;// for bug no 1092 : roshana
			} else {
				bus.mail(new Letter(letter), BusIfc.CURRENT);
			}
		}

	}

	/**
	 * Creates the void ADO transaction
	 * 
	 * @param cargo
	 * @param pm
	 * @return
	 */
	protected VoidTransactionADO createVoidTranasction(VoidCargo cargo,
			ParameterManagerIfc pm) {
		// create new void transaction

		RegisterADO registerADO = ContextFactory.getInstance().getContext()
				.getRegisterADO();
		registerADO.setParameterManager(pm);
		VoidTransactionADO voidTxn = (VoidTransactionADO) registerADO
				.createTransaction(TransactionPrototypeEnum.VOID, cargo
						.getCustomerInfo(), cargo.getOperator());
		voidTxn.setVoidReasonCode(cargo.getReasonCode());
		voidTxn.setCaptureCustomer(cargo.getOriginalTransactionADO()
				.getCaptureCustomer());

		return voidTxn;
	}

	protected void displayDialog(POSUIManagerIfc ui, String name,
			int dialogType, String letter) {
		DialogBeanModel dialogModel = new DialogBeanModel();
		dialogModel.setResourceID(name);
		dialogModel.setType(dialogType);
		dialogModel.setButtonLetter(DialogScreensIfc.BUTTON_OK, letter);
		ui.showScreen(POSUIManagerIfc.DIALOG_TEMPLATE, dialogModel);
	}

	/**
	 * Attempts to create a CurrencyIfc object from a passed in amount
	 * 
	 * @param amountString
	 *            String representing amount
	 * @return a CurrencyIfc instance representing the proper amount
	 * @throws TenderException
	 */
	protected CurrencyIfc parseAmount(String amountString)
			throws TenderException {
		CurrencyIfc amount = null;
		try {
			amount = DomainGateway.getBaseCurrencyInstance(amountString);
		} catch (Exception e) {
			throw new TenderException("Attempted to parse amount string",
					TenderErrorCodeEnum.INVALID_AMOUNT, e);
		}
		return amount;
	}
}
