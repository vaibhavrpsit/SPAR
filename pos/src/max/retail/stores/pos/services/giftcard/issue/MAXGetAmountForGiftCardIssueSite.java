/* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
   Copyright (c) 2012 - 2013 MAXHyperMarket, Inc.    All Rights Reserved.
  	Rev 1.0  15/Apr/2013	Jyoti Rawal, Initial Draft: Changes for Gift Card Functionality 
 * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */

package max.retail.stores.pos.services.giftcard.issue;

import max.retail.stores.pos.services.giftcard.MAXGiftCardUtilities;
import max.retail.stores.pos.ui.beans.MAXGiftCardBeanModel;
import oracle.retail.stores.foundation.manager.ifc.ParameterManagerIfc;
import oracle.retail.stores.foundation.manager.ifc.UIManagerIfc;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.pos.manager.ifc.UtilityManagerIfc;
import oracle.retail.stores.pos.services.PosSiteActionAdapter;
import oracle.retail.stores.pos.ui.POSUIManagerIfc;
import oracle.retail.stores.pos.ui.beans.NavigationButtonBeanModel;

//--------------------------------------------------------------------------
/**
 * This site displays the Gift Card amount screen
 * 
 * @version $Revision: 3$
 */
// --------------------------------------------------------------------------
public class MAXGetAmountForGiftCardIssueSite extends PosSiteActionAdapter {
	/**
	 * 
	 */
	private static final long serialVersionUID = -1444925590984510626L;
	/** revision number of this class */

	public static final String revisionNumber = "$Revision: 3$";

	// ----------------------------------------------------------------------
	/**
	 * @param bus
	 *            Service Bus
	 */
	// ----------------------------------------------------------------------
	public void arrive(BusIfc bus) {
		POSUIManagerIfc ui = (POSUIManagerIfc) bus.getManager(UIManagerIfc.TYPE);
    
		UtilityManagerIfc utility = (UtilityManagerIfc) bus.getManager(UtilityManagerIfc.TYPE);
		ParameterManagerIfc pm = (ParameterManagerIfc) bus.getManager(ParameterManagerIfc.TYPE);
		NavigationButtonBeanModel localNavigationButtonBeanModel = MAXGiftCardUtilities.getGiftCardDenominationsModel(utility, pm, logger, bus.getServiceName());
	
		MAXGiftCardBeanModel giftCardModel = new MAXGiftCardBeanModel();
		giftCardModel.setLocalButtonBeanModel(localNavigationButtonBeanModel);
		//giftCardModel.getLocalButtonBeanModel().
		ui.showScreen(POSUIManagerIfc.GET_AMOUNT_FOR_GIFT_CARD, giftCardModel);
	}
}