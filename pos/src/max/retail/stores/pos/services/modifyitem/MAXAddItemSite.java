/* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
   Copyright (c) 2012 - 2013 MAXHyperMarket, Inc.    All Rights Reserved.
   	Rev 1.1  12/Aug/2013	Jyoti Rawal		Fix for Bug 7641 - Service Item : POS Hanged
  	Rev 1.0  06/Aug/2013	Jyoti Rawal		Initial Draft: Bug 7522 - Reload Gift Card : Service items can be added to the transaction
 * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
package max.retail.stores.pos.services.modifyitem;

import java.util.Iterator;

import oracle.retail.stores.domain.lineitem.SaleReturnLineItemIfc;
import oracle.retail.stores.domain.stock.ProductGroupConstantsIfc;
import oracle.retail.stores.foundation.manager.ifc.UIManagerIfc;
import oracle.retail.stores.foundation.tour.application.Letter;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.pos.services.PosSiteActionAdapter;
import oracle.retail.stores.pos.services.modifyitem.ItemCargo;
import oracle.retail.stores.pos.ui.DialogScreensIfc;
import oracle.retail.stores.pos.ui.POSUIManagerIfc;
import oracle.retail.stores.pos.ui.beans.DialogBeanModel;

public class MAXAddItemSite extends PosSiteActionAdapter{
	  /**
	 * 
	 */
	private static final long serialVersionUID = -6698619700075481713L;

	public void arrive(BusIfc bus)
	    {
	        // Grab the item from the cargo
	        ItemCargo cargo = (ItemCargo) bus.getCargo();
	        String letter = "Continue";//Rev 1.1 changes
	        if(cargo.getTransaction()!=null){
	    		Iterator itr = cargo.getTransaction().getLineItemsIterator();
	    		POSUIManagerIfc ui = (POSUIManagerIfc) bus.getManager(UIManagerIfc.TYPE);
	       		while(itr.hasNext())
	    		{ 
	    			SaleReturnLineItemIfc itemObj = (SaleReturnLineItemIfc)itr.next();
	    			if(itemObj.getPLUItem().getProductGroupID().equals(ProductGroupConstantsIfc.PRODUCT_GROUP_GIFT_CARD)&&(! (cargo.getPLUItem().getItemID().equals("70071000"))))
	    			{
	    				DialogBeanModel dialogModel = new DialogBeanModel();
	    				dialogModel.setResourceID("ITEM_NOT_ALLOWED");  //Rev 1.0 changes
	    				dialogModel.setType(DialogScreensIfc.ERROR);
	    				dialogModel.setButtonLetter(DialogScreensIfc.BUTTON_OK, "Invalid");
	    				ui.showScreen(POSUIManagerIfc.DIALOG_TEMPLATE, dialogModel);
	    				return;
	    			}else{
	    				
	    				letter = "Continue";
	    			}
	    		}
	    		/**
	    		 * Rev 1.0 changes start here
	    		 */
	       		cargo.setAddPLUItem(true); //Rev 1.1 changes
	        }
	        else{
	        	cargo.setAddPLUItem(true);
	        	letter = "Continue";
	        }
	        // Proceed to next site
	        bus.mail(new Letter(letter), BusIfc.CURRENT);
//	        bus.mail(new Letter(CommonLetterIfc.CONTINUE), BusIfc.CURRENT);
	    }

	    //----------------------------------------------------------------------
	    /**
	        Returns a string representation of this object.
	        @return String representation of object
	    **/
	    //----------------------------------------------------------------------
	    public String toString()
	    {
	        String strResult = new String("Class:  AddItemSite (Revision " +
	                                      getRevisionNumber() +
	                                      ")" + hashCode());
	        return(strResult);
	    }

	    //----------------------------------------------------------------------
	    /**
	        Returns the revision number of the class.
	        @return String representation of revision number
	    **/
	    //----------------------------------------------------------------------
	    public String getRevisionNumber()
	    {
	        return(revisionNumber);
	    }
}
