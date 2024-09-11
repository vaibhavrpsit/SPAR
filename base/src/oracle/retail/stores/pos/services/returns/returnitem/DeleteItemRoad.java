/* ===========================================================================
* Copyright (c) 1998, 2014, Oracle and/or its affiliates. All rights reserved.
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/returns/returnitem/DeleteItemRoad.java /main/10 2014/06/10 15:26:17 arabalas Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    arabalas  05/26/14 - added alternative way to get the Selected Items
 *                         Indexes
 *    cgreene   05/26/10 - convert to oracle packaging
 *
 * ===========================================================================
 * $Log:
 *    3    360Commerce 1.2         3/31/2005 4:27:43 PM   Robert Pearse   
 *    2    360Commerce 1.1         3/10/2005 10:20:53 AM  Robert Pearse   
 *    1    360Commerce 1.0         2/11/2005 12:10:33 PM  Robert Pearse   
 *
 *   Revision 1.1  2004/03/22 06:17:50  baa
 *   @scr 3561 Changes for handling deleting return items
 *
 * ===========================================================================
 */
package oracle.retail.stores.pos.services.returns.returnitem;

// foundation imports
import oracle.retail.stores.foundation.manager.ifc.UIManagerIfc;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;

import oracle.retail.stores.domain.lineitem.SaleReturnLineItemIfc;

import oracle.retail.stores.pos.services.PosLaneActionAdapter;
import oracle.retail.stores.pos.services.returns.returnitem.ReturnItemCargo;
import oracle.retail.stores.pos.ui.POSUIManagerIfc;
import oracle.retail.stores.pos.ui.beans.LineItemsModel;

//--------------------------------------------------------------------------
/**
    This road is traversed when the user presses the
    Delete key from the SELL_ITEM screen.
    <p>
    @version $Revision: /main/10 $
**/
//--------------------------------------------------------------------------
public class DeleteItemRoad extends PosLaneActionAdapter
{
    /**
       revision number supplied by Team Connection
    **/
    public static final String revisionNumber = "$Revision: /main/10 $";

    /**
       static index value indicating no selected row
    **/
    protected static final int NO_SELECTION = -1;

    //----------------------------------------------------------------------
    /**
       Deletes the selected item from the transaction.
       <P>
       <B>Pre-Condition(s)</B>
       <UL>
       <LI>
       </UL>
       <B>Post-Condition(s)</B>
       <UL>
       <LI>
       </UL>
       @param  bus     Service Bus
    **/
    //----------------------------------------------------------------------
    public void traverse(BusIfc bus)
    {

        ReturnItemCargo cargo             = (ReturnItemCargo)bus.getCargo();
        int[] allSelected                 = null;
        int selected                      = NO_SELECTION; 

        if (cargo.getSelectedItems() == null)
        {
            POSUIManagerIfc ui;
            ui = (POSUIManagerIfc)bus.getManager(UIManagerIfc.TYPE);
            /*
             * Get the indices of all selected items
             */
            LineItemsModel beanModel = (LineItemsModel)ui.getModel();
            allSelected = beanModel.getRowsToDelete();
        }
        else
        {
            allSelected = cargo.getSelectedItems();
            cargo.setExitAfterItemDeletion(true);
        }
        
        // for each item in the array.  Process from the back of the array,
        // because if you process from the front, items will not be in the
        // expected locations in the transaction when processing the end of the array.
        for (int i = allSelected.length - 1; i > -1; i--)
        {
            selected = allSelected[i];

            //Remove item from selected list
            cargo.setCurrentItem(selected);
            SaleReturnLineItemIfc lineItem = cargo.getSaleLineItem();
            System.out.println("i:: "+i+" selected:: "+selected+" pluItem:: "+lineItem.getPLUItemID());
            cargo.removeReturnItem(lineItem.getReturnItem());
            cargo.removeReturnSaleLineItem(lineItem);
        }
    }


}
