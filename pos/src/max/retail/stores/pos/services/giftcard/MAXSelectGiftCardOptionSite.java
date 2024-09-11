/* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
*  Copyright (c) 2012 - 2013 MAXHyperMarket, Inc.    All Rights Reserved.
*    Rev 1.3    07/08/2013   Jyoti Fix for Bug 7573 - Special Order : POS Crashed
*    Rev 1.2    03/07/2013   Jyoti Fix for Bug 6838 Fix for Bug 6838  Gift Option screen displayed rather than sale item screen
*  Rev 1.1  15/Apr/2013	Jyoti Rawal, Fixed the issue Dialog is getting skipped when there is some 
*  item at sale screen and press Gift card
*  	Rev 1.0  15/Apr/2013	Jyoti Rawal, Initial Draft: Changes for Gift Card Functionality 
* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */

package max.retail.stores.pos.services.giftcard;

// foundation imports
import java.util.Iterator;

import max.retail.stores.domain.lineitem.MAXSaleReturnLineItem;
import oracle.retail.stores.domain.stock.GiftCertificateItem;
import oracle.retail.stores.domain.stock.ProductGroupConstantsIfc;
import oracle.retail.stores.domain.transaction.LayawayTransaction;
import oracle.retail.stores.domain.transaction.SaleReturnTransactionIfc;
import oracle.retail.stores.foundation.manager.ifc.UIManagerIfc;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.pos.services.PosSiteActionAdapter;
import oracle.retail.stores.pos.services.giftcard.GiftCardCargo;
import oracle.retail.stores.pos.ui.DialogScreensIfc;
import oracle.retail.stores.pos.ui.POSUIManagerIfc;
import oracle.retail.stores.pos.ui.beans.DialogBeanModel;

//--------------------------------------------------------------------------
/**
 *  This site displays the Gift Card options
 *  @version $Revision: 3$
 */
//--------------------------------------------------------------------------
public class MAXSelectGiftCardOptionSite extends PosSiteActionAdapter
{
    /**
	 * 
	 */
	private static final long serialVersionUID = 3328460530428786704L;
	/** revision number of this class */
    public static final String revisionNumber = "$Revision: 3$";

    //----------------------------------------------------------------------
    /**
     *  @param  bus     Service Bus
     */
    //----------------------------------------------------------------------
    public void arrive(BusIfc bus)
    {
    	POSUIManagerIfc ui = (POSUIManagerIfc) bus.getManager(UIManagerIfc.TYPE);
          GiftCardCargo giftCardCargo = (GiftCardCargo) bus.getCargo();
          if((giftCardCargo.getTransaction() instanceof LayawayTransaction)){
        	  DialogBeanModel dialogModel = new DialogBeanModel();
  			dialogModel.setResourceID("GC_NOT_ALLOWED_LAYAWAY");
  			dialogModel.setType(DialogScreensIfc.ERROR);
  			dialogModel.setButtonLetter(DialogScreensIfc.BUTTON_OK, "Undo");
  			ui.showScreen(POSUIManagerIfc.DIALOG_TEMPLATE, dialogModel);
  			return;
          }
          else{
          SaleReturnTransactionIfc retailTransaction = (SaleReturnTransactionIfc) giftCardCargo.getTransaction(); //Rev 1.3 changes
          if(retailTransaction!=null){
		Iterator itr = retailTransaction.getLineItemsIterator();
		if(itr.hasNext()){  //Fixed the issue Dialog is getting skipped when there is some item at sale screen and press Gift card
		while(itr.hasNext())
		{ 
			MAXSaleReturnLineItem itemObj = (MAXSaleReturnLineItem)itr.next();
			if(itemObj.getPLUItem().getProductGroupID().equals(ProductGroupConstantsIfc.PRODUCT_GROUP_UNDEFINED) || itemObj.getPLUItem() instanceof GiftCertificateItem)
			{
				DialogBeanModel dialogModel = new DialogBeanModel();
				dialogModel.setResourceID("GC_NOT_ALLOWED");
				dialogModel.setType(DialogScreensIfc.ERROR);
				dialogModel.setButtonLetter(DialogScreensIfc.BUTTON_OK, "UndoGift");  //rev 1.2 changes
				ui.showScreen(POSUIManagerIfc.DIALOG_TEMPLATE, dialogModel);
				break;
			}else{
 
		          ui.showScreen(POSUIManagerIfc.GIFT_CARD_OPTIONS_SCREEN);
			}
		}
          }else{
        	  ui.showScreen(POSUIManagerIfc.GIFT_CARD_OPTIONS_SCREEN);
          }
          }
          else{
        	  
	          ui.showScreen(POSUIManagerIfc.GIFT_CARD_OPTIONS_SCREEN);
		}
    }
       //   ui.showScreen(POSUIManagerIfc.GIFT_CARD_OPTIONS);
}
}
