/********************************************************************************
 *   
 *	Copyright (c) 2016 - 2017 MAX Hypermarket, Inc    All Rights Reserved.
 *	
 *
 *
 *	Rev	1.0 	Dec 20, 2016		Mansi Goel		Changes for Gift Card FES	
 *
 ********************************************************************************/

package max.retail.stores.pos.services.tender.giftcard;

//foundation imports
import java.util.HashMap;

import max.retail.stores.pos.ui.MAXPOSUIManagerIfc;
import oracle.retail.stores.commerceservices.common.currency.CurrencyIfc;
import oracle.retail.stores.domain.DomainGateway;
import oracle.retail.stores.domain.financial.RegisterIfc;
import oracle.retail.stores.domain.financial.StoreStatusIfc;
import oracle.retail.stores.domain.utility.GiftCardIfc;
import oracle.retail.stores.foundation.manager.ifc.UIManagerIfc;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.pos.services.tender.TenderCargo;
import oracle.retail.stores.pos.services.tender.giftcard.GiftCardActivationLaunchShuttle;
import oracle.retail.stores.pos.ui.POSUIManagerIfc;
import oracle.retail.stores.pos.ui.beans.POSBaseBeanModel;

import org.apache.log4j.Logger;

//--------------------------------------------------------------------------
/**
 * This shuttle copies information from the cargo used in the redeem gift card
 * service to the cargo used in the Gift Card Activation service.
 * <p>
 * <p>
 * 
 * @version $Revision: 1.3 $
 */
// --------------------------------------------------------------------------
public class MAXGiftCardActivationLaunchShuttle extends GiftCardActivationLaunchShuttle {
	static final long serialVersionUID = -5356542265323718729L;

	/**
	 * revision number supplied by Team Connection
	 */
	public static final String revisionNumber = "$Revision: 1.3 $";

	/**
	 * gift card data for activation
	 */
	protected GiftCardIfc giftCard = null;

	/**
	 * the financial data for the register
	 */
	protected RegisterIfc register;

	/**
	 * The financial data for the store
	 */
	protected StoreStatusIfc storeStatus;

	protected RegisterIfc registerID;

	protected boolean isRefund = false;

	private static final Logger logger = Logger.getLogger(MAXGiftCardActivationLaunchShuttle.class);

	public void load(BusIfc bus) {
        	super.load(bus);
        	TenderCargo tenderCargo = (TenderCargo)bus.getCargo();
        	giftCard = tenderCargo.getGiftCard();
        	//Changes for Rev 1.0 : Starts
            if(tenderCargo.getTenderAttributes().get("AMOUNT")!= null &&  tenderCargo.getTenderAttributes().get("AMOUNT").toString().indexOf("-") != -1)
            	isRefund = true;
            //Changes for Rev 1.0 : Ends
        }
    


	public void unload(BusIfc bus) {
		super.unload(bus);
		POSBaseBeanModel model = new POSBaseBeanModel();
		POSUIManagerIfc ui = (POSUIManagerIfc) bus.getManager(UIManagerIfc.TYPE);
		HashMap balanceEnquiryMap = null;
		//Changes for Rev 1.0 : Starts
		ui.showScreen(MAXPOSUIManagerIfc.GIFT_CARD_VALIDATING_SCREEN, model);
		if (balanceEnquiryMap != null && balanceEnquiryMap.size() != 0
				&& ("0").equals(balanceEnquiryMap.get("ResponseCode").toString())) {
			String amount = balanceEnquiryMap.get("Amount").toString();
			CurrencyIfc amt = DomainGateway.getBaseCurrencyInstance(amount);
			giftCard.setCurrentBalance(amt);
		}
		
		giftCard.setRequestType(GiftCardIfc.GIFT_CARD_REDEEM);
		//Changes for Rev 1.0 : Ends
	}
}
