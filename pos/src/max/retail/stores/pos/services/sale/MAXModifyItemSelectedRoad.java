/* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
 *  Copyright (c) 2016 - 2017 MAX Hypermarket, Inc.    All Rights Reserved.
 *	
 *  Rev 1.0     Mar 03, 2017        Nitika Arora       Changes for Issue- Once we scan the plu items, qty should not be editable.
 *  
* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */

package max.retail.stores.pos.services.sale;

import oracle.retail.stores.domain.lineitem.AbstractTransactionLineItemIfc;
import oracle.retail.stores.domain.lineitem.SaleReturnLineItemIfc;
import oracle.retail.stores.domain.transaction.SaleReturnTransactionIfc;
import oracle.retail.stores.foundation.manager.ifc.UIManagerIfc;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.pos.services.PosLaneActionAdapter;
import oracle.retail.stores.pos.services.sale.SaleCargoIfc;
import oracle.retail.stores.pos.ui.POSUIManagerIfc;
import oracle.retail.stores.pos.ui.beans.LineItemsModel;

/**
 * @author Nitika1.Arora
 *
 */
public class MAXModifyItemSelectedRoad extends PosLaneActionAdapter{
    /** serialVersionUID */
    private static final long serialVersionUID = -8155681303160825397L;

    /**
       revision number supplied by Team Connection
    **/
    public static final String revisionNumber = "$Revision: /main/16 $";

    /**
       static index value indicating no selected row
    **/
    protected static final int NO_SELECTION = -1;



    /**
     *   Temp line item used in loop
     */
    protected SaleReturnLineItemIfc item = null;

    //----------------------------------------------------------------------
    /**
       Include the highlighted line item from the transaction.
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

        POSUIManagerIfc ui;
        ui = (POSUIManagerIfc)bus.getManager(UIManagerIfc.TYPE);
        /*
         * Get the indices of all selected items
         */
        LineItemsModel beanModel                    = (LineItemsModel)ui.getModel();
        int[] allSelected                           = beanModel.getSelectedRows();
        AbstractTransactionLineItemIfc[] lineItems  = beanModel.getLineItems();
        int selected                                = NO_SELECTION;
        SaleCargoIfc cargo                          = (SaleCargoIfc)bus.getCargo();
        SaleReturnTransactionIfc transaction        = cargo.getTransaction();
        int lineNumber                              = -1;
        
        if (allSelected.length > lineItems.length)
        {
        	allSelected = new int[lineItems.length];
        }
        
        // set selectedForItemModification flag to true for the items selected for modification and 
        // clone them
        if (allSelected.length > 0)
        {
            // reset the selectedForItemModification flag for all items in the transaction since some items 
            // might have it set to true from a previous modification
            AbstractTransactionLineItemIfc[] transItems = transaction.getItemContainerProxy().getLineItems();
            for(int i = 0; i < transItems.length; i++)
            {
            	((SaleReturnLineItemIfc)transItems[i]).setSelectedForItemModification(false);
            }

            SaleReturnLineItemIfc[] items = new SaleReturnLineItemIfc[allSelected.length];
            for (int i = 0; i < allSelected.length; i++)
            {
                selected = allSelected[i];
                /*
                 * Add the highlighted items to the highlight line item array in the cargo
                 */
                 // since kit header is invisible, need to adjust for its line number
                if (lineItems != null)
                {
                    lineNumber = lineItems[selected].getLineNumber();
                }
                
                // Get a reference to the line item which has not been cloned already.
                item = (SaleReturnLineItemIfc) transaction.getItemContainerProxy().retrieveLineItemByID(lineNumber);
                // Multi-quantity line items can be expanded to individual line items
                // when a customer is added to the transaction.  As result pickup and 
                // delivery functionality must know which items in the ItemContainerProxy
                // have been selected to item modification.
                item.setSelectedForItemModification(true);
                items[i] = (SaleReturnLineItemIfc)item.clone();
                SaleReturnLineItemIfc tempItem = (SaleReturnLineItemIfc)transaction.getItemContainerProxy().getLineItems()[lineNumber];
                items[i].setPLUItem(tempItem.getPLUItem());
            }
            cargo.setLineItems(items);
        }
        else
        {
            cargo.setLineItems(null);
        }
    }
}
