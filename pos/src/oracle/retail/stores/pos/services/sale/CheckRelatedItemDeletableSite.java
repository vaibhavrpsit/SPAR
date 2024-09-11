/* ===========================================================================
* Copyright (c) 2005, 2013, Oracle and/or its affiliates. All rights reserved.
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/sale/CheckRelatedItemDeletableSite.java /main/17 2013/05/01 15:29:13 yiqzhao Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    yiqzhao   04/26/13 - Make not removable related item to be deleted when
 *                         its primary item is going to delete or is already
 *                         deleted.
 *    vtemker   04/16/13 - Moved constants in OrderLineItemIfc to
 *                         OrderConstantsIfc in common project
 *    yiqzhao   01/29/13 - Check the related item is return item.
 *    yiqzhao   01/29/13 - Remove unused import.
 *    yiqzhao   01/28/13 - Related Item's RemoveAllowed flag only apply to Sale
 *                         Transaction.
 *    sgu       07/03/12 - replace item disposition code to use delivery
 *                         instead of ship
 *    yiqzhao   06/29/12 - Add dialog for deleting ship item, disable change
 *                         price for ship item
 *    yiqzhao   06/28/12 - Add delete shipping item feature
 *    blarsen   03/08/12 - Moved beanModel.getRowsToDelete() into its own
 *                         method so it could be overriden for MPOS (since it
 *                         does not have beanModels).
 *    cgreene   02/15/11 - move constants into interfaces and refactor
 *    cgreene   05/26/10 - convert to oracle packaging
 *    abondala  01/03/10 - update header date
 *
 * ===========================================================================
 * $Log:
 1    360Commerce 1.0         12/13/2005 4:47:03 PM  Barry A. Pape
 *
 *
 * ===========================================================================
 */
package oracle.retail.stores.pos.services.sale;

import java.math.BigDecimal;

import max.retail.stores.domain.transaction.MAXSaleReturnTransactionIfc;
import oracle.retail.stores.domain.lineitem.AbstractTransactionLineItemIfc;
import oracle.retail.stores.domain.lineitem.OrderItemStatusIfc;
import oracle.retail.stores.domain.lineitem.OrderLineItemIfc;
import oracle.retail.stores.domain.lineitem.SaleReturnLineItemIfc;
import oracle.retail.stores.domain.order.OrderConstantsIfc;
import oracle.retail.stores.domain.transaction.SaleReturnTransactionIfc;
import oracle.retail.stores.domain.transaction.TransactionIfc;
import oracle.retail.stores.foundation.manager.ifc.UIManagerIfc;
import oracle.retail.stores.foundation.tour.application.Letter;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.pos.services.PosSiteActionAdapter;
import oracle.retail.stores.pos.services.common.CommonLetterIfc;
import oracle.retail.stores.pos.ui.DialogScreensIfc;
import oracle.retail.stores.pos.ui.POSUIManagerIfc;
import oracle.retail.stores.pos.ui.UIUtilities;
import oracle.retail.stores.pos.ui.beans.LineItemsModel;

/**
 * This site checks if a related item is deletable. If it is not, then we
 * display a message saying it is not.
 *
 */
public class CheckRelatedItemDeletableSite extends PosSiteActionAdapter
{
    private static final long serialVersionUID = 2073061673413249847L;
    /**
     * This defines the dialog screen to display from the bundles.
     */
    public static String DELETE_INVALID = "DeleteInvalid";

    /**
     * This method checks if any of the items are not deletable because
     * of the related item flag.
     *
     * @param bus
     * @see oracle.retail.stores.foundation.tour.ifc.SiteActionIfc#arrive(oracle.retail.stores.foundation.tour.ifc.BusIfc)
     */
    public void arrive(BusIfc bus)
    {
        POSUIManagerIfc ui = (POSUIManagerIfc)bus.getManager(UIManagerIfc.TYPE);

        int[] allSelected = getItemsToDelete(bus);
        boolean deleteAllowed = true;
        SaleCargoIfc cargo = (SaleCargoIfc)bus.getCargo();
        SaleReturnTransactionIfc transaction = (SaleReturnTransactionIfc)cargo.getTransaction();

        for(int i = 0;i < allSelected.length;i++)
        {
            AbstractTransactionLineItemIfc item = transaction.retrieveItemByIndex(allSelected[i]);
            if (item instanceof SaleReturnLineItemIfc )
            {
                SaleReturnLineItemIfc lineItem = (SaleReturnLineItemIfc)item;
                if(!lineItem.isRelatedItemDeleteable() && !lineItem.isReturnLineItem() )
                {
                    if (lineItem.getRelatedItemSequenceNumber()!=-1)
                    {
                        //primary item has not been deleted
                        if ( !isPrimaryItemSelectToBeDeleted(allSelected, lineItem.getRelatedItemSequenceNumber()) ) 
                        {
                            deleteAllowed = false;
                            break;
                        }
                    }
                }
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

        boolean isShippingToCustomerItemSelected = false;
        for(int i = 0;i < allSelected.length;i++)
        {
            AbstractTransactionLineItemIfc item = transaction.retrieveItemByIndex(allSelected[i]);
            if (item instanceof SaleReturnLineItemIfc)
            {
                OrderItemStatusIfc orderItemStatus = ((SaleReturnLineItemIfc)item).getOrderItemStatus();
                boolean isDeliveryItem = orderItemStatus.getItemDispositionCode()==OrderConstantsIfc.ORDER_ITEM_DISPOSITION_DELIVERY;
            	if (orderItemStatus.isCrossChannelItem() && isDeliveryItem)
                {
            		isShippingToCustomerItemSelected = true;
                    break;
                }
            }
        }

        if (isShippingToCustomerItemSelected)
        {
        	Letter letter = new Letter(CommonLetterIfc.SHIPPING);
        	bus.mail(letter, BusIfc.CURRENT);
        }
        else
        {
        	Letter letter = new Letter(CommonLetterIfc.CONTINUE);
        	bus.mail(letter, BusIfc.CURRENT);
        }
    }

    /*
     * Get the items to delete.
     */
    protected int[] getItemsToDelete(BusIfc bus)
    {
        POSUIManagerIfc ui = (POSUIManagerIfc)bus.getManager(UIManagerIfc.TYPE);
        /*
         * Get the indices of all selected items
         */
        LineItemsModel beanModel                 = (LineItemsModel)ui.getModel();
        int[] allSelected                        = beanModel.getRowsToDelete();
        return allSelected;
    }
    
    /**
     * Check the related items's primary item is selected for deleting
     * @param allSelected all selected line items' indices
     * @param index the line index of the related item's primary item
     * @return
     */
    protected boolean isPrimaryItemSelectToBeDeleted(int[] allSelected, int index ) 
    {
        boolean isSelected = false;
        for ( int i=0; i<allSelected.length; i++ )
        {
            if ( allSelected[i]==index )
                return true;
        }
        
        return isSelected;
    }

}
