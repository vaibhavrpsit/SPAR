/*===========================================================================
* Copyright (c) 2012, 2014, Oracle and/or its affiliates. All rights reserved.
* ===========================================================================
* $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/sale/AddShippingItemToDeleteRoad.java /main/4 2014/07/10 13:23:08 vtemker Exp $
* ===========================================================================
* NOTES
* <other useful comments, qualifications, etc.>
*
* MODIFIED    (MM/DD/YY)
* vtemker     07/09/14 - Extracted addShippingItemsToDelete method to override
*                        in MPOS
* vtemker     07/08/14 - Refactored referenes to UI beanmodel into overridable
*                        methods to promote resuability from MPOS
* vtemker     04/16/13 - Moved constants in OrderLineItemIfc to
*                        OrderConstantsIfc in common project
* sgu         07/03/12 - replace item disposition code to use delivery instead
*                        of ship
* yiqzhao     06/29/12 - Add dialog for deleting ship item, disable change
*                        price for ship item
* yiqzhao     06/25/12 - Creation
* ===========================================================================
*/

package oracle.retail.stores.pos.services.sale;

import java.util.ArrayList;

import oracle.retail.stores.domain.lineitem.OrderItemStatusIfc;
import oracle.retail.stores.domain.lineitem.SaleReturnLineItemIfc;
import oracle.retail.stores.domain.order.OrderConstantsIfc;
import oracle.retail.stores.domain.transaction.SaleReturnTransactionIfc;
import oracle.retail.stores.foundation.manager.ifc.UIManagerIfc;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.pos.services.PosLaneActionAdapter;
import oracle.retail.stores.pos.ui.POSUIManagerIfc;
import oracle.retail.stores.pos.ui.beans.LineItemsModel;

/**
 * This road is traversed when the user presses the Delete key from the
 * SELL_ITEM screen.
 *
 * @version $Revision: /main/4 $
 */
public class AddShippingItemToDeleteRoad extends PosLaneActionAdapter
{
    private static final long serialVersionUID = 1L;
    /**
     * revision number
     */
    public static final String revisionNumber = "$Revision: /main/4 $";

    /**
     * Adds the item to the transaction. Mails Continue letter is special order
     * to not ask for serial numbers, else mails GetSerialNumbers letter to
     * possibly ask for serial numbers.
     *
     * @param bus Service Bus
     */
    @Override
    public void traverse(BusIfc bus)
    {
        // Grab the item from the cargo

    	SaleCargo cargo = (SaleCargo)bus.getCargo();
        SaleReturnTransactionIfc transaction = cargo.getTransaction();

        /*
         * Get the indices of all selected items
         */

        int[] rowsToDelete = getItemsToDelete(bus);
        ArrayList<Integer> allSelectedList = toIntegerList(rowsToDelete);

        addShippingItemsToDelete(transaction, rowsToDelete, allSelectedList);

        //generate new line indexes for deleting items
        int rowIndicesToDelete[] = toIntArray(allSelectedList);

        setItemsToDelete(bus, rowIndicesToDelete);
    }

    /**
     * Add the Shipping items to delete into the allSelectedList
     * @param transaction
     * @param rowsToDelete
     * @param allSelectedList
     */
    protected void addShippingItemsToDelete(SaleReturnTransactionIfc transaction, int[] rowsToDelete,
            ArrayList<Integer> allSelectedList)
    {
        SaleReturnLineItemIfc[] lineItems = (SaleReturnLineItemIfc[])transaction.getLineItems();
        for (int deleteItemIndex: rowsToDelete)
        {
        	SaleReturnLineItemIfc itemToDelete = (SaleReturnLineItemIfc)transaction.getLineItems()[deleteItemIndex];
            OrderItemStatusIfc orderItemStatus = itemToDelete.getOrderItemStatus();
            boolean isDeliveryItem = orderItemStatus.getItemDispositionCode()==OrderConstantsIfc.ORDER_ITEM_DISPOSITION_DELIVERY;
        	if (orderItemStatus.isCrossChannelItem() && isDeliveryItem)
        	{
        		for (SaleReturnLineItemIfc lineItem: lineItems )
    	        {
        			if (itemToDelete.getLineNumber()!=lineItem.getLineNumber() && //not the same line
        				lineItem.getOrderItemStatus().getDeliveryDetails().getDeliveryDetailID()==itemToDelete.getOrderItemStatus().getDeliveryDetails().getDeliveryDetailID())
        			{
        				allSelectedList.add(lineItem.getLineNumber());
        			}
    	        }
        	}

        }
    }
    
    /**
     * Set the list of items to delete
     * @param bus
     * @param rowsToDelete
     */
    protected void setItemsToDelete(BusIfc bus, int[] rowsToDelete)
    {
        POSUIManagerIfc ui;
        ui = (POSUIManagerIfc)bus.getManager(UIManagerIfc.TYPE);
        /*
         * Get the indices of all selected items
         */
        LineItemsModel beanModel = (LineItemsModel)ui.getModel(POSUIManagerIfc.SELL_ITEM);
        beanModel.setRowsToDelete(rowsToDelete);
    }
    
    /**
     * Get the list of indices of the items to delete
     * @param bus
     * @return
     */
    protected int[] getItemsToDelete(BusIfc bus)
    {
        POSUIManagerIfc ui;
        ui = (POSUIManagerIfc)bus.getManager(UIManagerIfc.TYPE);
        /*
         * Get the indices of all selected items
         */
        LineItemsModel beanModel = (LineItemsModel)ui.getModel(POSUIManagerIfc.SELL_ITEM);
              
        int[] allSelected = beanModel.getRowsToDelete();
        return allSelected;
    }

    /**
     * Convert int array to Integer Array List
     * @param intArray
     * @return
     */
    protected ArrayList<Integer> toIntegerList(int[] intArray)
    {
    	ArrayList<Integer> integerList = new ArrayList<Integer>(0);
    	for (int i=0; i<intArray.length; i++)
    	{
    		integerList.add(new Integer(intArray[i]));
    	}
    	return integerList;
    }

    /**
     * Convert Integer ArrayList to int array
     * @param integerList
     * @return
     */
    protected int[] toIntArray(ArrayList<Integer> integerList)
    {
    	int intArray[] = new int[integerList.size()];

    	for (int i=0; i<integerList.size(); i++)
    	{
    		intArray[i] = integerList.get(i).intValue();
    	}
    	return intArray;
    }
}