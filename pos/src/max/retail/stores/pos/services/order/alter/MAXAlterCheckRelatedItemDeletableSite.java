/* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
  Copyright (c) 2012-2013 MAXHyperMarket, Inc.    All Rights Reserved.
  Rev 1.1	28/Jun/2013	  	Bug 6709 - Special Order : POS Crashed on Pickup
  Rev 1.0	01/May/2013	  	Tanmaya, Home Delivery Special Order
* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
package max.retail.stores.pos.services.order.alter;

import max.retail.stores.pos.services.order.common.MAXOrderCargo;
import oracle.retail.stores.domain.lineitem.AbstractTransactionLineItemIfc;
import oracle.retail.stores.domain.lineitem.OrderLineItem;
import oracle.retail.stores.domain.lineitem.OrderLineItemIfc;
import oracle.retail.stores.domain.order.OrderIfc;
import oracle.retail.stores.foundation.manager.ifc.UIManagerIfc;
import oracle.retail.stores.foundation.tour.application.Letter;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.pos.services.PosSiteActionAdapter;
import oracle.retail.stores.pos.services.common.CommonLetterIfc;
import oracle.retail.stores.pos.ui.DialogScreensIfc;
import oracle.retail.stores.pos.ui.POSUIManagerIfc;
import oracle.retail.stores.pos.ui.UIUtilities;
import oracle.retail.stores.pos.ui.beans.LineItemsModel;

//--------------------------------------------------------------------------
/**
     This site checks if a related item is deletable.  If it is not, then
     we display a message saying it is not.
     $Revision: 1$
 **/
//--------------------------------------------------------------------------
public class MAXAlterCheckRelatedItemDeletableSite extends PosSiteActionAdapter
{
    
    /**
	 * 
	 */
	private static final long serialVersionUID = -590694135609859500L;
	/**
     * This defines the dialog screen to display from the bundles.
     */
    public static String DELETE_INVALID = "DeleteInvalid";
    
    //----------------------------------------------------------------------
    /**
        This method checks if any of the items are not deletable because
        of the related item flag.
        @param bus
        @see com.extendyourstore.foundation.tour.ifc.SiteActionIfc#arrive(com.extendyourstore.foundation.tour.ifc.BusIfc)
    **/
    //----------------------------------------------------------------------
    public void arrive(BusIfc bus)
    {
        POSUIManagerIfc ui = (POSUIManagerIfc)bus.getManager(UIManagerIfc.TYPE);
        LineItemsModel beanModel                 = (LineItemsModel)ui.getModel();
        int[] allSelected                        = beanModel.getSelectedRows();
        boolean deleteAllowed = true;
        MAXOrderCargo cargo = (MAXOrderCargo)bus.getCargo();
        OrderIfc order = cargo.getOrder();
        int count=0;
        int maxCount=0;
        for(int i = 0;i < allSelected.length;i++)
        {
            AbstractTransactionLineItemIfc item = order.retrieveItemByIndex(allSelected[i]);
            if(item instanceof OrderLineItem)
            {
            	int status = ((OrderLineItem)item).getOrderItemStatus().getStatus().getStatus();
            	
            	if(status==0||status==1||status==2)
            	{
            		if(!((OrderLineItem)item).isRelatedItemDeleteable())
                    {
                        deleteAllowed = false;
                        break;
                    }
            		count++;
            	}
            	else
            	{
            		deleteAllowed = false;
                    break;
            	}
            }
            
        }
        
        OrderLineItemIfc[] orderLineItems = order.getOrderLineItems();
        for(int j=0; j<orderLineItems.length;j++)
        {
        	int lineItemStatus=orderLineItems[j].getOrderItemStatus().getStatus().getStatus();
        	if(lineItemStatus==0||lineItemStatus==1||lineItemStatus==2)
        	{
        		maxCount++;
        	}
        }
        
        if (!deleteAllowed)
        {
            UIUtilities.setDialogModel(ui, 
                            DialogScreensIfc.ERROR, 
                            DELETE_INVALID, 
                            null, 
                            CommonLetterIfc.FAILURE);
            return;                
        }
        else if(count>=maxCount)
        {
        	UIUtilities.setDialogModel(ui, 
                    DialogScreensIfc.ERROR, 
                    "CannotDeleteAllItems", 
                    null, 
                    CommonLetterIfc.FAILURE);
        	return;
        }
        
        Letter letter = new Letter("Continue");
        bus.mail(letter, BusIfc.CURRENT);                
    }
}
