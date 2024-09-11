
/** * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * ************************
 * Copyright (c) 2013 Max Hypermarket.    All Rights Reserved. 
 * Rev. 1.1  Deepshikha     08/06/2015   Changes for Capillary Discount Coupons Implementation

 *  Rev 1.0     10/03/2015      Akhilesh kumar          		Loyalty Customer
 * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * ***************************/

package max.retail.stores.pos.services.sale;

import max.retail.stores.domain.customer.MAXTICCustomer;
import max.retail.stores.domain.transaction.MAXLayawayTransaction;
import max.retail.stores.domain.transaction.MAXSaleReturnTransactionIfc;
import oracle.retail.stores.commerceservices.common.currency.CurrencyIfc;
import oracle.retail.stores.domain.DomainGateway;
import oracle.retail.stores.domain.transaction.SaleReturnTransactionIfc;
import oracle.retail.stores.foundation.manager.ifc.ParameterManagerIfc;
import oracle.retail.stores.foundation.tour.application.Letter;
import oracle.retail.stores.foundation.tour.gate.Gateway;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.pos.services.PosLaneActionAdapter;
import oracle.retail.stores.pos.services.sale.SaleCargoIfc;

public class MAXCheckAmtEligibleAisle extends PosLaneActionAdapter {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	/**
	 * 
	 */
	
public	static String MINIMUM_LOYALTY_AMT = "LoyaltyMemberInviteMinimumValue";

	public void traverse(BusIfc bus) {

		String eligibleAmount = "0";
		
		boolean trainingMode=false;
		boolean reentryMode=false;
		
		SaleCargoIfc cargo = (SaleCargoIfc) bus.getCargo();

		if (cargo.getRegister() != null
				&& cargo.getRegister().getWorkstation() != null) {
			trainingMode = cargo.getRegister().getWorkstation()
					.isTrainingMode();
			reentryMode = cargo.getRegister().getWorkstation()
					.isTransReentryMode();

		}

		String TICCustomerButton = Gateway.getProperty("application",
				"TICCustomerButton", "NO");
		String RedemptionForNonTICCustomer = Gateway.getProperty("application",
				"RedemptionForNonTICCustomer", "Y");

		MAXSaleReturnTransactionIfc transaction = null;
		MAXLayawayTransaction layawaytransaction = null;
		if (cargo.getTransaction() instanceof SaleReturnTransactionIfc) {
			transaction = (MAXSaleReturnTransactionIfc) cargo.getTransaction();
		} else if (cargo.getTransaction() instanceof MAXLayawayTransaction) {
			layawaytransaction = (MAXLayawayTransaction) cargo.getTransaction();
		}
		ParameterManagerIfc parameterManager = (ParameterManagerIfc) bus
		.getManager(ParameterManagerIfc.TYPE);

		try {
			eligibleAmount = parameterManager
			.getStringValue(MINIMUM_LOYALTY_AMT);
		} catch (Exception e) {
			eligibleAmount = "100";
		}

	

		CurrencyIfc totalPrice = null;
		if (transaction != null) {
			totalPrice = transaction.getTransactionTotals().getBalanceDue();
		} else if (layawaytransaction != null) {
			totalPrice = layawaytransaction.getTransactionTotals()
					.getBalanceDue();
		}
		CurrencyIfc eligiblePrice = DomainGateway
		.getBaseCurrencyInstance(eligibleAmount);

		MAXTICCustomer customer=null;
		if(transaction!=null && transaction.getMAXTICCustomer()!=null 
				&& transaction.getMAXTICCustomer() instanceof MAXTICCustomer )
		{
			customer=(MAXTICCustomer)transaction.getMAXTICCustomer();
		}
		else if(layawaytransaction!=null && layawaytransaction.getMAXTICCustomer()!=null 
				&& layawaytransaction.getMAXTICCustomer() instanceof MAXTICCustomer )
		{
			customer=(MAXTICCustomer)layawaytransaction.getMAXTICCustomer();
		}

		if ((totalPrice.compareTo(eligiblePrice) == CurrencyIfc.EQUALS || totalPrice.compareTo(eligiblePrice) == CurrencyIfc.GREATER_THAN) 
				&& !(customer!=null && customer.getTICCustomerID()!=null && !customer.getTICCustomerID().equalsIgnoreCase("")) 
				&& (TICCustomerButton!=null && TICCustomerButton.equalsIgnoreCase("YES")) && !reentryMode && !trainingMode) {

			bus.mail(new Letter("RequireTICCustomer"), BusIfc.CURRENT);
			//changes start for rev 1.1
		} else if (RedemptionForNonTICCustomer.equalsIgnoreCase("Y") && !(transaction.getTransactionType()==19 || transaction.getTransactionType()==20 || 
				transaction.getTransactionType()==21 || transaction.getTransactionType()==22) && !(transaction.getTransactionType()==2)) {
			bus.mail("CapillaryCouponNonTIC");
			//changes end for rev 1.1
		} else {

			bus.mail(new Letter("Success"), BusIfc.CURRENT);

		}
	}

}
