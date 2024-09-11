/* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
   Copyright (c) 2012 - 2013 MAXHyperMarket, Inc.    All Rights Reserved.
  	Rev 1.0  15/Apr/2013	Jyoti Rawal, Initial Draft: Changes for Gift Card Functionality 
 * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */

package max.retail.stores.pos.services.giftcard.reload;

import max.retail.stores.pos.ui.beans.MAXGiftCardBeanModel;
import oracle.retail.stores.common.utility.BigDecimalConstants;
import oracle.retail.stores.domain.utility.GiftCardIfc.StatusCode;
import oracle.retail.stores.foundation.manager.ifc.ParameterManagerIfc;
import oracle.retail.stores.foundation.manager.ifc.UIManagerIfc;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.pos.manager.ifc.UtilityManagerIfc;
import oracle.retail.stores.pos.services.PosSiteActionAdapter;
import oracle.retail.stores.pos.services.giftcard.GiftCardCargo;
import oracle.retail.stores.pos.services.giftcard.GiftCardUtilities;
import oracle.retail.stores.pos.ui.POSUIManagerIfc;
import oracle.retail.stores.pos.ui.beans.NavigationButtonBeanModel;

//--------------------------------------------------------------------------
/**
 * This site displays the Gift Card amount screen
 * 
 * @version $Revision: 1.2 $
 */
// --------------------------------------------------------------------------
public class MAXGetAmountForGiftCardReloadSite extends PosSiteActionAdapter {
	/**
	 * 
	 */
	private static final long serialVersionUID = -3366343272820840387L;
	/** revision number of this class */
	public static final String revisionNumber = "$Revision: 1.2 $";

	// ----------------------------------------------------------------------
	/**
	 * @param bus
	 *            Service Bus
	 */
	// ----------------------------------------------------------------------
	public void arrive(BusIfc bus) {
		GiftCardCargo cargo = (GiftCardCargo) bus.getCargo();
		// changes starts for code merging(commenting below line)
		//cargo.setItemQuantity(BigDecimalConstants.ZERO);
		cargo.setItemQuantity(BigDecimalConstants.ZERO_AMOUNT);
		// changes ends for code merging
		POSUIManagerIfc ui = (POSUIManagerIfc) bus.getManager(UIManagerIfc.TYPE);
		UtilityManagerIfc utility = (UtilityManagerIfc) bus.getManager(UtilityManagerIfc.TYPE);
		ParameterManagerIfc pm = (ParameterManagerIfc) bus.getManager(ParameterManagerIfc.TYPE);

		NavigationButtonBeanModel localNavigationButtonBeanModel = GiftCardUtilities.getGiftCardDenominationsModel(utility, pm, logger, bus.getServiceName());

		MAXGiftCardBeanModel giftCardModel = new MAXGiftCardBeanModel();
		// Changes starts for code merging(commenting below line as per MAX)
		//giftCardModel.setGiftCardStatus(MAXTenderAuthConstantsIfc.RELOAD);
		giftCardModel.setGiftCardStatus(StatusCode.Reload);
		// changes ends for code merging
		giftCardModel.setLocalButtonBeanModel(localNavigationButtonBeanModel);

		ui.showScreen(POSUIManagerIfc.GET_AMOUNT_FOR_GIFT_CARD, giftCardModel);
	}
}
