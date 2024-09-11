/* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
  Copyright (c) 2013 MAX, Inc.    All Rights Reserved.
  Rev 1.3	Prateek		13/08/2013		Changes done for BUG 7633
  Rev 1.2	Prateek		1/08/2013		Changes done for BUG 7445
  Rev 1.1	Prateek		1/07/2013		Changes done for BUG 6767
  Rev 1.0	Prateek		23/03/2013		Initial Draft: Changes for Quantity Button
* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */

package max.retail.stores.pos.services.modifyitem;

import oracle.retail.stores.domain.lineitem.AbstractTransactionLineItemIfc;
import oracle.retail.stores.domain.lineitem.SaleReturnLineItem;
import oracle.retail.stores.domain.stock.GiftCardPLUItem;
import oracle.retail.stores.domain.stock.GiftCertificateItem;
import oracle.retail.stores.foundation.manager.ifc.UIManagerIfc;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.pos.services.PosSiteActionAdapter;
import oracle.retail.stores.pos.services.modifyitem.ItemCargo;
import oracle.retail.stores.pos.ui.DialogScreensIfc;
import oracle.retail.stores.pos.ui.POSUIManagerIfc;
import oracle.retail.stores.pos.ui.beans.DialogBeanModel;

//--------------------------------------------------------------------------
/**
  This site displays the main menu for the Modify Item service.
  <p>
  @version $Revision: 1.0 $
**/


public class MAXDetermineModifyItemQuantitySite extends PosSiteActionAdapter {


	 //----------------------------------------------------------------------
    /**
     *   Displays the ITEM_OPTIONS screen.
     *   <P>
     *   @param  bus     Service bus.
     */
    //----------------------------------------------------------------------
    public void arrive(BusIfc bus)
    {
        MAXItemCargo itemCargo = (MAXItemCargo) bus.getCargo();
        String letter = itemCargo.getParentLetter();
        AbstractTransactionLineItemIfc[] lineItems = itemCargo.getItems();
        
        if(letter.equalsIgnoreCase("Quantity"))
        {
        	if(lineItems!=null && lineItems.length >1)
	        {
	        	POSUIManagerIfc ui = (POSUIManagerIfc) bus.getManager(UIManagerIfc.TYPE);
	            DialogBeanModel dialogModel = new DialogBeanModel();
	            dialogModel.setResourceID("Item");             
	            dialogModel.setType(DialogScreensIfc.ERROR);
	            dialogModel.setButtonLetter(DialogScreensIfc.BUTTON_OK,"Failure");
	            ui.showScreen(POSUIManagerIfc.DIALOG_TEMPLATE, dialogModel);
	            
	        }
			/**MAX Rev 1.1 Change : Start**/
	        else if(lineItems.length == 1 && lineItems[0] instanceof SaleReturnLineItem)
	        {
	        	
	        	if(((SaleReturnLineItem)lineItems[0]).getReturnItem() != null)
	        	{
	        		POSUIManagerIfc ui = (POSUIManagerIfc) bus.getManager(UIManagerIfc.TYPE);
		            DialogBeanModel dialogModel = new DialogBeanModel();
		            dialogModel.setResourceID("ReturnItemSelected");             
		            dialogModel.setType(DialogScreensIfc.ERROR);
		            dialogModel.setButtonLetter(DialogScreensIfc.BUTTON_OK,"Failure");
		            ui.showScreen(POSUIManagerIfc.DIALOG_TEMPLATE, dialogModel);
	        	}
				/**MAX Rev 1.2 Change : Start**/
				else if(((SaleReturnLineItem) lineItems[0]).getPLUItem() instanceof GiftCertificateItem) 
	        	{
	        		POSUIManagerIfc ui = (POSUIManagerIfc) bus.getManager(UIManagerIfc.TYPE);
		            DialogBeanModel dialogModel = new DialogBeanModel();
		            dialogModel.setResourceID("GiftCertificateItemSelected");             
		            dialogModel.setType(DialogScreensIfc.ERROR);
		            dialogModel.setButtonLetter(DialogScreensIfc.BUTTON_OK,"Failure");
		            ui.showScreen(POSUIManagerIfc.DIALOG_TEMPLATE, dialogModel);
	        	}
				else if(((SaleReturnLineItem) lineItems[0]).getPLUItem() instanceof GiftCardPLUItem) 
	        	{
	        		POSUIManagerIfc ui = (POSUIManagerIfc) bus.getManager(UIManagerIfc.TYPE);
		            DialogBeanModel dialogModel = new DialogBeanModel();
		            dialogModel.setResourceID("GiftCardItemSelected");             
		            dialogModel.setType(DialogScreensIfc.ERROR);
		            dialogModel.setButtonLetter(DialogScreensIfc.BUTTON_OK,"Failure");
		            ui.showScreen(POSUIManagerIfc.DIALOG_TEMPLATE, dialogModel);
	        	}
				/**MAX Rev 1.3 Change : End**/
				/**MAX Rev 1.3 Change : Start**/
				else if (((SaleReturnLineItem) lineItems[0]).getItemSerial() != null)
	        	{
	        		POSUIManagerIfc ui = (POSUIManagerIfc) bus.getManager(UIManagerIfc.TYPE);
		            DialogBeanModel dialogModel = new DialogBeanModel();
		            dialogModel.setResourceID("SerialItemQuantityUnmodifiable");             
		            dialogModel.setType(DialogScreensIfc.ERROR);
		            dialogModel.setButtonLetter(DialogScreensIfc.BUTTON_OK,"Failure");
		            ui.showScreen(POSUIManagerIfc.DIALOG_TEMPLATE, dialogModel);
	        	}
				/**MAX Rev 1.3 Change : End**/
	        	else
	        	{ System.out.println("Abhi::"+lineItems[0].getItemQuantity());
	        		letter="ModifyQuantity";
			    	bus.mail(letter, BusIfc.CURRENT);
	        	}
	        }
	        /**MAX Rev 1.1 Change : End**/	
	        else
	        {
	        	letter="ModifyQuantity";
		    	bus.mail(letter, BusIfc.CURRENT);
	        }
        }
        else
        {
	    	if(letter.equals("Item"))
	    	{
	    		letter = "Item";
	    	}
	    	bus.mail(letter, BusIfc.CURRENT);
        }
        
    }
	
}
