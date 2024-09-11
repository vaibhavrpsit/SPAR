/* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
   Copyright (c) 2012 - 2013 MAXHyperMarket, Inc.    All Rights Reserved.
  	Rev 1.0  15/Apr/2013	Jyoti Rawal, Initial Draft: Changes for Gift Card Functionality 
 * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */

package max.retail.stores.pos.services.giftcard.reload;

import java.math.BigDecimal;

import max.retail.stores.pos.ui.beans.MAXGiftCardBeanModel;
import oracle.retail.stores.commerceservices.common.currency.CurrencyIfc;
import oracle.retail.stores.domain.DomainGateway;
import oracle.retail.stores.domain.utility.GiftCardIfc;
import oracle.retail.stores.domain.utility.GiftCardIfc.StatusCode;
import oracle.retail.stores.foundation.manager.gui.UIModelIfc;
import oracle.retail.stores.foundation.manager.ifc.ParameterManagerIfc;
import oracle.retail.stores.foundation.manager.ifc.UIManagerIfc;
import oracle.retail.stores.foundation.manager.parameter.ParameterException;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.pos.services.PosSiteActionAdapter;
import oracle.retail.stores.pos.services.giftcard.GiftCardCargo;
import oracle.retail.stores.pos.ui.POSUIManagerIfc;
import oracle.retail.stores.pos.ui.beans.GiftCardBeanModel;
import oracle.retail.stores.pos.ui.beans.PromptAndResponseModel;

//--------------------------------------------------------------------------
/**
 * @version $Revision: 1.2 $
 */
// --------------------------------------------------------------------------
public class MAXGetCardNumForGiftCardReloadSite extends PosSiteActionAdapter {
	/**
	 * 
	 */
	private static final long serialVersionUID = -6993653786436500660L;
	/** revision number of this class */
	public static final String revisionNumber = "$Revision: 1.2 $";

	// ----------------------------------------------------------------------
	/**
	 * @param bus
	 *            Service Bus
	 */
	// ----------------------------------------------------------------------
	public void arrive(BusIfc bus) {
		POSUIManagerIfc ui = (POSUIManagerIfc) bus.getManager(UIManagerIfc.TYPE);
		UIModelIfc uiModel = ui.getModel();
		GiftCardBeanModel model = null;

		if ((uiModel != null) && (uiModel instanceof GiftCardBeanModel)) {
			// get gift card model if previously invalid gift card number
			// entered
			// reload amount was saved in the model.
			model = (MAXGiftCardBeanModel) ui.getModel();
		} else {
			model = new MAXGiftCardBeanModel();
			model.setGiftCardStatus(StatusCode.Reload);
			CurrencyIfc amount = DomainGateway.getBaseCurrencyInstance();
			GiftCardCargo cargo = (GiftCardCargo) bus.getCargo();
			if (cargo != null) {
				GiftCardIfc giftCard = cargo.getGiftCard();
				if (giftCard != null) {
					amount = giftCard.getCurrentBalance();
				}
			}
			if(!(("Balance").equals(bus.getCurrentLetter().getName())))
			model.setGiftCardAmount(new BigDecimal(amount.toString()));
			else
				model.setGiftCardAmount(new BigDecimal("0.00"));
		}
		PromptAndResponseModel pModel = new PromptAndResponseModel();
		boolean enableResponse = true;
    	ParameterManagerIfc pm = (ParameterManagerIfc) bus
				.getManager(ParameterManagerIfc.TYPE);
    	try {
			enableResponse = pm.getBooleanValue("ManualEntryEnable").booleanValue();
		} catch (ParameterException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
    	pModel.setResponseEnabled(enableResponse);
		model.setPromptAndResponseModel(pModel);
		
		model.setLocalButtonBeanModel(null); // remove the reload amount buttons
		ui.showScreen(POSUIManagerIfc.GET_CARD_NUM_FOR_GIFT_CARD, model);
	}

}
