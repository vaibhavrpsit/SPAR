/* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
 *     Copyright (c) 2010 MAx Hypermarkets Pvt Ltd.    All Rights Reserved.
 *
 * Rev 1.2		Nov 22nd, 2017		Himanshu Saini, CR :  OTP Based Loyalty Redemption	
 * Rev 1.1 		Dec 28th,2015		Priyanka Singh(EYLLP)
 * Merge code for (Loyality customer validation by OTP Number)	
 * Rev 1.0  Jan 7, 2011 1:51:53 PM puneet.hasija
 * Initial revision.
 * Resolution for FES_LMG_India_Customer_Loyalty_v1.1
 * Displaying EnterLoyaltyPointsAmount Screen
 * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */

package max.retail.stores.pos.services.tender.sbi;

import max.retail.stores.domain.customer.MAXCustomerIfc;
import max.retail.stores.domain.transaction.MAXLayawayPaymentTransaction;
import max.retail.stores.pos.ui.MAXPOSUIManagerIfc;
import oracle.retail.stores.domain.transaction.SaleReturnTransaction;
import oracle.retail.stores.foundation.manager.ifc.ParameterManagerIfc;
import oracle.retail.stores.foundation.manager.ifc.UIManagerIfc;
import oracle.retail.stores.foundation.manager.parameter.ParameterException;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.pos.services.PosSiteActionAdapter;
import oracle.retail.stores.pos.services.tender.TenderCargo;
import oracle.retail.stores.pos.ui.POSUIManagerIfc;
import oracle.retail.stores.pos.ui.beans.POSBaseBeanModel;
import oracle.retail.stores.pos.ui.beans.PromptAndResponseModel;

public class MAXEnterLoyaltySBIPointsAmountSite extends PosSiteActionAdapter {
	// ----------------------------------------------------------------------
	/**
	 * serialVersionUID long
	 */
	// ----------------------------------------------------------------------
	private static final long serialVersionUID = 7380631034386370683L;
	// ----------------------------------------------------------------------
	/**
	 * revision number
	 */
	// ----------------------------------------------------------------------
	public static final String revisionNumber = "$Revision: 1.2 $";

	// --------------------------------------------------------------------------
	/**
	 * @param bus
	 *            the bus arriving at this site
	 */
	// --------------------------------------------------------------------------
	public void arrive(BusIfc bus) {
		POSUIManagerIfc ui = (POSUIManagerIfc) bus.getManager(UIManagerIfc.TYPE);
		ParameterManagerIfc pm = (ParameterManagerIfc) bus.getManager(ParameterManagerIfc.TYPE);
		//Change Start for Rev 1.2
		TenderCargo cargo = (TenderCargo) bus.getCargo();	
		MAXCustomerIfc customer=null;
		if(cargo.getCurrentTransactionADO().toLegacy() instanceof SaleReturnTransaction )
		{
			SaleReturnTransaction transaction= ((SaleReturnTransaction) cargo.getCurrentTransactionADO().toLegacy());
			customer=(MAXCustomerIfc) transaction.getCustomer();

		}
		else if( cargo.getCurrentTransactionADO().toLegacy() instanceof MAXLayawayPaymentTransaction)
		{
			MAXLayawayPaymentTransaction transaction=((MAXLayawayPaymentTransaction)cargo.getCurrentTransactionADO().toLegacy());

			customer=(MAXCustomerIfc) transaction.getCustomer();	

		}
		else{
			SaleReturnTransaction transaction= ((SaleReturnTransaction) cargo.getCurrentTransactionADO().toLegacy());
			customer=(MAXCustomerIfc) transaction.getCustomer();
		}
		customer.setLoyaltyTimeout(0);
		customer.setLoyaltyRetryTimeout(0);
	//	customer.setLoyaltyotp(0); Changes  for rev 1.2
		customer.setOtpValidation(false);
		String modeOfLoyaltyPointsTender = "Points";
		try {
			modeOfLoyaltyPointsTender = pm.getStringValue("ModeOfLoyaltyPointsTender");
		} catch (ParameterException pe) {
			logger.error("Error retrieving parameter:" + pe.getMessage() + "");
		}

		POSBaseBeanModel model = new POSBaseBeanModel();
		PromptAndResponseModel prModel = new PromptAndResponseModel();
		prModel.setArguments(modeOfLoyaltyPointsTender);

		// set the model in case mode of loyalty points tender is Points
		if (modeOfLoyaltyPointsTender.equalsIgnoreCase("Points")) {
			prModel.setResponseTypeNumeric();
		}
		model.setPromptAndResponseModel(prModel);
		ui.showScreen(MAXPOSUIManagerIfc.ENTER_LOYALTY_POINTS_AMOUNT, model);
	}
}
