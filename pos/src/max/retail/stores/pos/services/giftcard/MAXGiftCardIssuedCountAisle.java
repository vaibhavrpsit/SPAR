
/* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
   Copyright (c) 2012 - 2013 MAXHyperMarket, Inc.    All Rights Reserved.
   	Rev 1.2  07/Aug/2013	Jyoti Rawal, Fix for Bug 7573 - Special Order : POS Crashed
   	Rev 1.1  08/Jul/2013	Jyoti Rawal, Bug 7206 - GC- POS is allowing more than the maximum card issued in transaction
  	Rev 1.0  15/Apr/2013	Jyoti Rawal, Initial Draft: Changes for Gift Card Functionality 
 * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */

package max.retail.stores.pos.services.giftcard;

import java.util.Vector;

import oracle.retail.stores.domain.lineitem.SaleReturnLineItemIfc;
import oracle.retail.stores.domain.stock.GiftCardPLUItem;
import oracle.retail.stores.domain.transaction.SaleReturnTransactionIfc;
import oracle.retail.stores.foundation.manager.ifc.ParameterManagerIfc;
import oracle.retail.stores.foundation.manager.ifc.UIManagerIfc;
import oracle.retail.stores.foundation.manager.parameter.ParameterException;
import oracle.retail.stores.foundation.tour.application.Letter;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.foundation.tour.ifc.LetterIfc;
import oracle.retail.stores.pos.services.PosLaneActionAdapter;
import oracle.retail.stores.pos.services.giftcard.GiftCardCargo;
import oracle.retail.stores.pos.ui.POSUIManagerIfc;

public class MAXGiftCardIssuedCountAisle extends PosLaneActionAdapter {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public void traverse(BusIfc bus) {
		POSUIManagerIfc ui = (POSUIManagerIfc) bus.getManager(UIManagerIfc.TYPE);
		GiftCardCargo giftCardCargo = (GiftCardCargo) bus.getCargo();
		int giftCardCountInTransaction = 0;
		int maxCardAllowed = 0;
		ParameterManagerIfc pm = (ParameterManagerIfc) bus.getManager(ParameterManagerIfc.TYPE);
		LetterIfc letter = new Letter("GoIssue");
	

		try {
			maxCardAllowed = pm.getIntegerValue("MaximumGiftCardIssued").intValue();
		} catch (ParameterException e) {
			maxCardAllowed = 100;
			e.printStackTrace();
		}

		SaleReturnTransactionIfc retailTransaction = (SaleReturnTransactionIfc) giftCardCargo.getTransaction();

		if (retailTransaction != null && retailTransaction.getLineItemsVector() != null && retailTransaction.getLineItemsVector().size() != 0) {
			Vector items = retailTransaction.getLineItemsVector();
			for (int i = 0; i < items.size(); i++) {
				SaleReturnLineItemIfc item = (SaleReturnLineItemIfc) items.get(i);
				if (item.getPLUItem() instanceof GiftCardPLUItem) {
					giftCardCountInTransaction++;
					
				}
			}
		}

		if (giftCardCountInTransaction < maxCardAllowed) { //Rev 1.1 change
			bus.mail(letter, BusIfc.CURRENT);
		} else {
			ui.showScreen(POSUIManagerIfc.DIALOG_TEMPLATE, MAXGiftCardUtilities.createMaximumGiftCardIssuedDialogModel());
		}
		
	}

}
