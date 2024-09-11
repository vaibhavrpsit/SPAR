/* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
   Copyright (c) 2012 - 2013 MAXHyperMarket, Inc.    All Rights Reserved.
  	Rev 1.0  15/Apr/2013	Jyoti Rawal, Initial Draft: Changes for Gift Card Functionality 
 * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */


package max.retail.stores.pos.services.tender.giftcard;

import max.retail.stores.pos.services.giftcard.MAXGiftCardUtilities;
import max.retail.stores.pos.ui.MAXPOSUIManagerIfc;
import oracle.retail.stores.domain.transaction.LayawayTransactionIfc;
import oracle.retail.stores.foundation.manager.ifc.UIManagerIfc;
import oracle.retail.stores.foundation.tour.application.Letter;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.pos.services.PosSiteActionAdapter;
import oracle.retail.stores.pos.services.tender.TenderCargo;
import oracle.retail.stores.pos.ui.POSUIManagerIfc;
import oracle.retail.stores.pos.ui.beans.POSBaseBeanModel;

//--------------------------------------------------------------------------
/**
 * This class determines the correct start position. $Revision: 1.2 $
 **/
// --------------------------------------------------------------------------
public class MAXDetermineTenderSubTourStartSite extends PosSiteActionAdapter {
	/**
	 * 
	 */
	private static final long serialVersionUID = 2935700172506794127L;
	// ----------------------------------------------------------------------
	/**
	 * This method just mails the correct letter.
	 * 
	 * @param bus
	 * @see com.extendyourstore.foundation.tour.ifc.SiteActionIfc#arrive(com.extendyourstore.foundation.tour.ifc.BusIfc)
	 **/
	// ----------------------------------------------------------------------
	protected TenderCargo cargo = null;

	public void arrive(BusIfc bus) {
		cargo = (TenderCargo) bus.getCargo();
		bus.mail(new Letter(cargo.getSubTourLetter()), BusIfc.CURRENT);
		// here this screen is added by Jyoti for online/offline credit card
		// tender
		boolean isLayaway = false;
		if (cargo.getTransaction() instanceof LayawayTransactionIfc)
			isLayaway = true;



		//DataManagerIfc dataManager = (DataManagerIfc) bus.getManager(DataManagerIfc.TYPE);
		// This Status is as per the Last Database Transaction Done
		//boolean databaseStatus = DataManagerOnlineStatus.getStatus(dataManager);

		POSUIManagerIfc ui = (POSUIManagerIfc) bus.getManager(UIManagerIfc.TYPE);
		// ui.showScreen(POSUIManagerIfc.CREDIT_DEBIT_CARD,new
		// POSBaseBeanModel());
		if(isLayaway) 
			ui.showScreen(POSUIManagerIfc.DIALOG_TEMPLATE, MAXGiftCardUtilities.createInvalidTransactionDialogModel());
		else if (("Credit").equals(cargo.getSubTourLetter()))
			ui.showScreen(MAXPOSUIManagerIfc.CREDIT_DEBIT_ONLINE_OFFLINE, new POSBaseBeanModel());
	
}
}
