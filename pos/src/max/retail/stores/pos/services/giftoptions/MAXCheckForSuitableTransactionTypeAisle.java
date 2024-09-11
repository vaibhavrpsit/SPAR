/* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
   Copyright (c) 2012 - 2013 MAXHyperMarket, Inc.    All Rights Reserved.
  	Rev 1.0  26/Apr/2013	Jyoti Rawal, Initial Draft: Changes for Gift Card Functionality 
 * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
package max.retail.stores.pos.services.giftoptions;

import max.retail.stores.domain.stock.MAXGiftCardPLUItem;
import max.retail.stores.domain.transaction.MAXSaleReturnTransaction;
import max.retail.stores.domain.transaction.MAXSaleReturnTransactionIfc;
import max.retail.stores.pos.services.giftcard.MAXGiftCardUtilities;
import oracle.retail.stores.domain.lineitem.SaleReturnLineItemIfc;
import oracle.retail.stores.domain.stock.ProductGroupConstantsIfc;
import oracle.retail.stores.domain.transaction.LayawayTransactionIfc;
import oracle.retail.stores.domain.transaction.RetailTransactionIfc;
import oracle.retail.stores.domain.utility.GiftCardIfc;
import oracle.retail.stores.foundation.manager.ifc.UIManagerIfc;
import oracle.retail.stores.foundation.tour.application.Letter;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.pos.services.PosLaneActionAdapter;
import oracle.retail.stores.pos.services.giftcard.GiftCardCargo;
import oracle.retail.stores.pos.services.modifytransaction.ModifyTransactionCargo;
import oracle.retail.stores.pos.ui.DialogScreensIfc;
import oracle.retail.stores.pos.ui.POSUIManagerIfc;
import oracle.retail.stores.pos.ui.beans.DialogBeanModel;


public class MAXCheckForSuitableTransactionTypeAisle extends PosLaneActionAdapter {	
	
	private static final long serialVersionUID = 1L;

	public void traverse(BusIfc bus)
	{
		boolean isLayaway = false;
		boolean isGiftCard = false;
		String prevLetter = null;
		if(bus.getCurrentLetter()!= null)
		 prevLetter = bus.getCurrentLetter().getName();
		POSUIManagerIfc ui = (POSUIManagerIfc) bus.getManager(UIManagerIfc.TYPE);
		
		if(bus.getCargo() instanceof GiftCardCargo)
		{
			GiftCardCargo giftcardCargo = (GiftCardCargo) bus.getCargo();
				SaleReturnLineItemIfc[] items = null;
			
			if(giftcardCargo.getTransaction() instanceof LayawayTransactionIfc)
			{
				
				isLayaway = true;
			}
			//priyanka change if issue g.cthen d.c can not be redeem
			// changes starts for code merging(commenting below line as per MAX)
		   //RetailTransactionIfc retailTransaction = giftcardCargo.getRetailTransactionIfc();
			RetailTransactionIfc retailTransaction = giftcardCargo.getRetailTransaction();
			// Changes ends for code merging
			
			if (retailTransaction instanceof MAXSaleReturnTransaction) {
		            
		        	if(retailTransaction!= null)
					{				 	 
					 items = retailTransaction.getProductGroupLineItems(ProductGroupConstantsIfc.PRODUCT_GROUP_GIFT_CARD);
					}
		        	/**
		        	 * TO DO
		        	 */
		        	if (items != null && items.length != 0)
					for (int j = 0; j < items.length; j++) {
							if (items[j].getPLUItem() instanceof MAXGiftCardPLUItem) {								
								GiftCardIfc GiftCardId = ((MAXGiftCardPLUItem) items[j].getPLUItem()).getGiftCard();								
								if (GiftCardId!= null && !prevLetter.equalsIgnoreCase("GiftCard")){
									isGiftCard =true;
								}	
							
								
							}
						}
		         
		        
			
			}
		}
	
		else if(bus.getCargo() instanceof ModifyTransactionCargo)
		{
//			MAXSaleReturnTransaction trans = null;
//			SaleReturnLineItemIfc[] items = null;
//			String discountCardId = null;
//			ModifyTransactionCargo modifyCargo = (ModifyTransactionCargo) bus.getCargo();
//			if(modifyCargo.getTransaction() instanceof MAXSaleReturnTransactionIfc)
//			trans = (MAXSaleReturnTransaction)modifyCargo.getTransaction();
//			if(trans!= null)
//			{}
			MAXSaleReturnTransaction trans = null;
			SaleReturnLineItemIfc[] items = null;
			String discountCardId = null;
			ModifyTransactionCargo modifyCargo = (ModifyTransactionCargo) bus.getCargo();
			if(modifyCargo.getTransaction() instanceof MAXSaleReturnTransactionIfc)
			trans = (MAXSaleReturnTransaction)modifyCargo.getTransaction();
			if(trans!= null)
			{
						 
			 items = trans.getProductGroupLineItems(ProductGroupConstantsIfc.PRODUCT_GROUP_GIFT_CARD);
			
			
			if (items != null && items.length != 0)
				for (int j = 0; j < items.length; j++) {
					if (items[j].getPLUItem() instanceof MAXGiftCardPLUItem) {						
//					discountCardId = ((MAXGiftCardPLUItem) items[j].getPLUItem()).getDiscountCardId();
						GiftCardIfc GiftCardId = ((MAXGiftCardPLUItem) items[j].getPLUItem()).getGiftCard();
					if (GiftCardId!=null)
					{
						isGiftCard = true;
					}
					
				}	
			
			
		}	
			}
		}
		if(isLayaway)
		{
			ui.showScreen(POSUIManagerIfc.DIALOG_TEMPLATE, MAXGiftCardUtilities.createInvalidTransactionDialogModel());
		}
	
		else if(isGiftCard)
		{
			  DialogBeanModel dialogModel = new DialogBeanModel();
	  			dialogModel.setResourceID("GC_NOT_ALLOWED_LAYAWAY");
	  			dialogModel.setType(DialogScreensIfc.ERROR);
	  			dialogModel.setButtonLetter(DialogScreensIfc.BUTTON_OK, "Undo");
	  			ui.showScreen(POSUIManagerIfc.DIALOG_TEMPLATE, dialogModel);
	  			return;	
			
		}
		
		else
		{
			String letter = null;
			
			 if(("GiftCard").equalsIgnoreCase(prevLetter))
			 letter = "GiftCardClear";
			else
			 letter = "LayawayClear";
			bus.mail(new Letter(letter), BusIfc.CURRENT);
		}
		
		
	}

}
