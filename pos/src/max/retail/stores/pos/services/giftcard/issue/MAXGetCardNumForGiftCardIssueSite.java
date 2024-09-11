/* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
   Copyright (c) 2012 - 2013 MAXHyperMarket, Inc.    All Rights Reserved.
  	Rev 1.0  15/Apr/2013	Jyoti Rawal, Initial Draft: Changes for Gift Card Functionality 
 * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */

package max.retail.stores.pos.services.giftcard.issue;

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
 * This site displays get gift card number screen
 * 
 * @version $Revision: 1.2 $
 */
// --------------------------------------------------------------------------
public class MAXGetCardNumForGiftCardIssueSite extends PosSiteActionAdapter {
	/**
	 * 
	 */
	private static final long serialVersionUID = -2920971465974086124L;
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
		GiftCardBeanModel model = null;// MAXGiftCardBeanModel model = null;
		PromptAndResponseModel pModel = new PromptAndResponseModel();
		// bhanu Changes Starts for Giftcard Swipe 
		pModel.setResponseEditable(true);
		pModel.setSwiped(true);
		// bhanu Changes End for Giftcard Swipe	
		
		if ((uiModel != null) && (uiModel instanceof MAXGiftCardBeanModel)) {
			// get gift card model if previously invalid gift card number
			// entered
			// issue amount was saved in the model.
			model = (GiftCardBeanModel) ui.getModel();
		} else {
			model = new GiftCardBeanModel();
// Changes start for code merging(commenting below line as per MAX)
			//model.setGiftCardStatus(MAXTenderAuthConstantsIfc.ACTIVE);
			model.setGiftCardStatus(StatusCode.Active);
// Changes ends for code merging
			CurrencyIfc amount = DomainGateway.getBaseCurrencyInstance();
			GiftCardCargo cargo = (GiftCardCargo) bus.getCargo();
			if (cargo != null) {
				GiftCardIfc giftCard = cargo.getGiftCard();
				if (giftCard != null) {
					amount = giftCard.getCurrentBalance();
				}
			}
			model.setGiftCardAmount(new BigDecimal(amount.getStringValue()));
		}
		
		boolean enableResponse = true;
    	ParameterManagerIfc pm = (ParameterManagerIfc) bus
				.getManager(ParameterManagerIfc.TYPE);
    	try {
    		// bhanu Changes Starts for Giftcard Swipe 
			enableResponse = pm.getBooleanValue("ManualEntryEnable").booleanValue();
		} catch (ParameterException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
    	pModel.setResponseEnabled(enableResponse);
		model.setPromptAndResponseModel(pModel);

		// MAXGiftVoucherRangeModel model = new MAXGiftVoucherRangeModel();
		// DataInputBeanModel model1 = new DataInputBeanModel();
		// remove the issue amount buttons
		ui.setModel(POSUIManagerIfc.GET_CARD_NUM_FOR_GIFT_CARD, model);
		model.setLocalButtonBeanModel(null);
		//model.setPromptAndResponseModel(mod);
		ui.showScreen(POSUIManagerIfc.GET_CARD_NUM_FOR_GIFT_CARD, model);
		// ui.setModel(MAXPOSUIManagerIfc.GIFT_CARD_RANGE, model1);
		// ui.showScreen(MAXPOSUIManagerIfc.GIFT_CARD_RANGE, model1);
	}
}
