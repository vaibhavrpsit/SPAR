/* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
   Copyright (c) 2012 - 2013 MAXHyperMarket, Inc.    All Rights Reserved.
  	Rev 1.0  23/Apr/2013	Jyoti Rawal, Initial Draft: Changes for Gift Card Functionality 
 * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
package max.retail.stores.pos.services.sale;

import max.retail.stores.domain.stock.MAXGiftCardPLUItem;
import max.retail.stores.domain.transaction.MAXSaleReturnTransaction;
import max.retail.stores.pos.services.giftcard.MAXGiftCardUtilities;
import oracle.retail.stores.domain.lineitem.SaleReturnLineItemIfc;
import oracle.retail.stores.domain.stock.ProductGroupConstantsIfc;
import oracle.retail.stores.domain.transaction.RetailTransactionIfc;
import oracle.retail.stores.domain.utility.GiftCardIfc;
import oracle.retail.stores.foundation.manager.ifc.UIManagerIfc;
import oracle.retail.stores.foundation.tour.application.Letter;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.pos.services.PosLaneActionAdapter;
import oracle.retail.stores.pos.services.giftcard.GiftCardCargo;
import oracle.retail.stores.pos.ui.POSUIManagerIfc;


public class MAXCheckForSuitableTransactionTypeAisle extends PosLaneActionAdapter {	
	
	private static final long serialVersionUID = 1L;

	public void traverse(BusIfc bus)
	{
		boolean isReturn = false;
		boolean isGiftCard = false;
		String prevLetter = null;
		if(bus.getCurrentLetter()!= null)
		 prevLetter = bus.getCurrentLetter().getName();
		POSUIManagerIfc ui = (POSUIManagerIfc) bus.getManager(UIManagerIfc.TYPE);
		
		if(bus.getCargo() instanceof GiftCardCargo)
		{
			GiftCardCargo giftcardCargo = (GiftCardCargo) bus.getCargo();
				SaleReturnLineItemIfc[] items = null;
			
//			if(giftcardCargo.getTransaction() instanceof LayawayTransactionIfc)
//			{
//				
//				isReturn = true;
//			}
			//priyanka change if issue g.cthen d.c can not be redeem
				// Changes starts for code merging(commenting at below line)
		 //  RetailTransactionIfc retailTransaction = giftcardCargo.getRetailTransactionIfc();
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
//		        		String GiftCardId = "100036465";
//								if (GiftCardId!= null && !prevLetter.equalsIgnoreCase("GiftCard")){
		        	if (GiftCardId!= null && !prevLetter.equalsIgnoreCase("GiftCard")){
									isGiftCard =true;
								}	
							
								
							}
						}
		         
		        
			
			}
		}
	
			
		
		
		if(isReturn)
		{
			ui.showScreen(POSUIManagerIfc.DIALOG_TEMPLATE, MAXGiftCardUtilities.createInvalidTransactionDialogModel());
		}
	
		else if(isGiftCard)
		{
			ui.showScreen(POSUIManagerIfc.DIALOG_TEMPLATE, MAXGiftCardUtilities.createAlreadyGiftCardNumErrorDialogModel(true));	
			
		}
		
		else
		{
			String letter = null;
			
			 if(("GiftCard").equalsIgnoreCase(prevLetter))
			 letter = "GiftCardClear";
			else
			 letter = "ReturnClear";
			bus.mail(new Letter(letter), BusIfc.CURRENT);
		}
		
		
	}

}
