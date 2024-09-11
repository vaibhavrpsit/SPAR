/* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
  Copyright (c) 2012-2013 MAXHyperMarket, Inc.    All Rights Reserved.
  Rev 1.3  Prateek   25/09/2013	Changes done for Special Order BUG - Defect Doc 17386607 Patch receivved from oracle
  Rev 1.2  Prateek   13/08/2013	Changes done for Special Order CR - Suggested Totals
  Rev 1.1  Jyoti     12/08/2013	Fix for Bug 7001 - Shipping methods at the time of home deilvery special order pickup needs to be captured in database as well as rtlogs
  Rev 1.0	Tanmaya				04/06/2013		Bug 6087 - Incorrect balance due at the time of order pickup. 
  
* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */

package max.retail.stores.pos.services.order.pickup;

import max.retail.stores.domain.lineitem.MAXSendPackageLineItemIfc;
import max.retail.stores.domain.order.MAXOrderIfc;
import max.retail.stores.domain.transaction.MAXOrderTransaction;
import max.retail.stores.domain.transaction.MAXTransactionTotalsIfc;
import max.retail.stores.pos.manager.ifc.MAXUtilityManagerIfc;
import oracle.retail.stores.domain.DomainGateway;
import oracle.retail.stores.domain.financial.FinancialTotalsIfc;
import oracle.retail.stores.domain.financial.ShippingMethodIfc;
import oracle.retail.stores.domain.lineitem.SendPackageLineItemIfc;
import oracle.retail.stores.domain.order.OrderIfc;
import oracle.retail.stores.domain.transaction.OrderTransactionIfc;
import oracle.retail.stores.foundation.manager.ifc.JournalManagerIfc;
import oracle.retail.stores.foundation.manager.ifc.ParameterManagerIfc;
import oracle.retail.stores.foundation.tour.application.Letter;
import oracle.retail.stores.foundation.tour.gate.Gateway;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.pos.journal.JournalFormatterManagerIfc;
import oracle.retail.stores.pos.manager.ifc.UtilityManagerIfc;
import oracle.retail.stores.pos.services.PosSiteActionAdapter;
import oracle.retail.stores.pos.services.common.CommonLetterIfc;
import oracle.retail.stores.pos.services.order.pickup.PickupOrderCargo;

public class MAXCreateTransactionFromOrderSite extends PosSiteActionAdapter { // begin
																				// class
																				// CreateTransactionFromOrderSite
	private static final long serialVersionUID = -4904962722696402176L;
	public static final String SITENAME = "CreateTransactionFromOrderSite";
	public static final String revisionNumber = "$Revision: 8$";
	public static final long GENERATE_SEQUENCE_NUMBER = -1;

	public void arrive(BusIfc bus) { // begin arrive()
		PickupOrderCargo cargo = (PickupOrderCargo) bus.getCargo();
		OrderIfc order = cargo.getOrder();

		MAXUtilityManagerIfc utility = (MAXUtilityManagerIfc) bus
				.getManager(UtilityManagerIfc.TYPE);

		// set up initial transaction tax on the order
		order.setTransactionTax(utility.getInitialTransactionTax(bus));
		((MAXTransactionTotalsIfc)(cargo.getOrder().getTotals())).getSendPackageVector();//setTotals(sendCargo.getTransaction().getTransactionTotals()); //Rev 1.1 changes

		OrderTransactionIfc transaction = order.createOrderTransaction(true);
		((MAXTransactionTotalsIfc)(cargo.getOrder().getTotals())).getSendPackages();
		//Rev 1.1 changes start here
		SendPackageLineItemIfc[] splis = ((MAXTransactionTotalsIfc)(cargo.getOrder().getTotals())).getSendPackages();
	    for (int i=0; i<splis.length; i++)
	      {
	        	FinancialTotalsIfc financialTotals = DomainGateway.getFactory().getFinancialTotalsInstance();
	        	financialTotals.add(((MAXSendPackageLineItemIfc)splis[i]).getFinancialTotals(true));
	        	((MAXTransactionTotalsIfc)(transaction.getTransactionTotals())).setShippingMethod((ShippingMethodIfc) splis[i].getShippingMethod());
	        	((MAXTransactionTotalsIfc)(transaction.getTransactionTotals())).addSendPackage(splis[i]);//cargo.getOrder().getTotals().getSendPackages())
	      }
	    ((MAXTransactionTotalsIfc)(transaction.getTransactionTotals())).getShippingMethod();
	//Rev 1.1 changes end
	    transaction.getLineItemsFinancialTotals();
		transaction.setSalesAssociate(cargo.getOperator());
		transaction.getPayment().setBusinessDate(
				cargo.getRegister().getBusinessDate());
		utility.initializeTransaction(transaction, bus,
				GENERATE_SEQUENCE_NUMBER);
		/**MAX Rev 1.3 Change : Start**/
		transaction.getOrderStatus().setRecordingTransactionID(transaction.getTransactionIdentifier());
        /**MAX Rev 1.3 Change : End**/
		/**MAX Rev 1.2 Change : Start**/
		if(transaction instanceof MAXOrderTransaction)
		{
			((MAXOrderTransaction)transaction).setSuggestedTender(((MAXOrderIfc)order).getSuggestedTender());
		}
		/**MAX Rev 1.2 Change : End**/
		cargo.setTransaction(transaction);
		writeToJournal(cargo, transaction, bus);

		// mail a Continue letter
		bus.mail(new Letter(CommonLetterIfc.CONTINUE), BusIfc.CURRENT);
	} // end arrive()

	// ----------------------------------------------------------------------
	/**
	 * Writes a transaction containing Pickup Order items to the Journal.
	 * <P>
	 * 
	 * @param cargo
	 *            The pickup order cargo.
	 * @param transaction
	 *            The newly create sale return transaction containing.
	 * @param bus
	 *            The bus. order line items
	 **/
	// ----------------------------------------------------------------------
	public void writeToJournal(PickupOrderCargo cargo,
			OrderTransactionIfc transaction, BusIfc bus) { // begin
															// writeToJournal()
		OrderIfc order = cargo.getOrder();
		JournalManagerIfc journal = (JournalManagerIfc) Gateway.getDispatcher()
				.getManager(JournalManagerIfc.TYPE);
		JournalFormatterManagerIfc formatter = (JournalFormatterManagerIfc) Gateway
				.getDispatcher().getManager(JournalFormatterManagerIfc.TYPE);
		if (journal != null && formatter != null) {
			ParameterManagerIfc pm = (ParameterManagerIfc) bus
					.getManager(ParameterManagerIfc.TYPE);
			journal.journal(transaction.getCashier().getLoginID(),
					transaction.getTransactionID(),
					formatter.journalOrder(transaction, order, pm));
		}
	} // end writeToJournal
} // end class CreateTransactionFromOrderSite
