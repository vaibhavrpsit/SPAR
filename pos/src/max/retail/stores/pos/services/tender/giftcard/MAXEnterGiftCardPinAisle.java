/** * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
 * Copyright (c) 2010 Lifestyle India Pvt Ltd.    All Rights Reserved.
 *  Rev 1.1 akhilesh kumar     20 oct 2015 for prefilled value in price prompt 
 * Upgraded to ORPOS 14.0.1 from Lifestyle ORPOS 12.0.9IN: AAKASH GUPTA(EYLLP):Aug-11-2015
 * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */

package max.retail.stores.pos.services.tender.giftcard;


import oracle.retail.stores.domain.financial.RegisterIfc;
import oracle.retail.stores.domain.financial.StoreStatusIfc;
import oracle.retail.stores.domain.utility.GiftCardIfc;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.pos.services.PosLaneActionAdapter;
import oracle.retail.stores.pos.ui.POSUIManagerIfc;
import oracle.retail.stores.pos.ui.beans.POSBaseBeanModel;
import oracle.retail.stores.pos.ui.beans.PromptAndResponseModel;
public class MAXEnterGiftCardPinAisle extends PosLaneActionAdapter{

	private static final long serialVersionUID = -7926925928495763969L;
	protected GiftCardIfc giftCard = null;
	protected RegisterIfc register;
	protected StoreStatusIfc storeStatus;
	protected RegisterIfc registerID;

	public void traverse(BusIfc bus)
	{
		POSUIManagerIfc ui = (POSUIManagerIfc)bus.getManager("UIManager");
		POSBaseBeanModel model = (POSBaseBeanModel)ui.getModel();
		//Rev 1.1   start
		PromptAndResponseModel bean=new PromptAndResponseModel() ;
		model.setPromptAndResponseModel(bean);
		
		//Rev 1.1 end
		ui.showScreen("GIFT_CARD_PIN", model);
	}
}