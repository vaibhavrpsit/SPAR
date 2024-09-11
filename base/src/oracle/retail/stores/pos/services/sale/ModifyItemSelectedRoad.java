/* ===========================================================================
* Copyright (c) 1998, 2014, Oracle and/or its affiliates. All rights reserved.
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/sale/ModifyItemSelectedRoad.java /main/16 2014/05/22 09:40:21 icole Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    icole     05/21/14 - Changes to prevent NPE due to line item list being
 *                         null.
 *    mjwallac  05/01/12 - Fortify: fix redundant null checks, part 3
 *    cgreene   05/26/10 - convert to oracle packaging
 *    crain     03/09/10 - Check for no item
 *    crain     03/02/10 - System marks all items as PDO in transaction
 *    abondala  01/03/10 - update header date
 *    jswan     04/14/09 - Modified to fix conflict between multi quantity
 *                         items and items that have been marked for Pickup or
 *                         Delivery.
 *
 * ===========================================================================
 * $Log:
 *    4    360Commerce 1.3         3/24/2008 12:40:30 PM  Deepti Sharma   merge
 *          from v12.x to trunk
 *    3    360Commerce 1.2         3/31/2005 4:29:04 PM   Robert Pearse   
 *    2    360Commerce 1.1         3/10/2005 10:23:34 AM  Robert Pearse   
 *    1    360Commerce 1.0         2/11/2005 12:12:40 PM  Robert Pearse   
 *
 *   Revision 1.3  2004/02/12 16:48:17  mcs
 *   Forcing head revision
 *
 *   Revision 1.2  2004/02/11 21:22:50  rhafernik
 *   @scr 0 Log4J conversion and code cleanup
 *
 *   Revision 1.1.1.1  2004/02/11 01:04:11  cschellenger
 *   updating to pvcs 360store-current
 *
 *
 * 
 *    Rev 1.1   Nov 07 2003 12:37:24   baa
 * use SaleCargoIfc
 * Resolution for 3430: Sale Service Refactoring
 * 
 *    Rev 1.0   Nov 05 2003 14:14:24   baa
 * Initial revision.
 *
 * ===========================================================================
 */
package oracle.retail.stores.pos.services.sale;

// foundation imports
import oracle.retail.stores.domain.lineitem.AbstractTransactionLineItemIfc;
import oracle.retail.stores.domain.lineitem.SaleReturnLineItemIfc;
import oracle.retail.stores.domain.transaction.SaleReturnTransactionIfc;
import oracle.retail.stores.foundation.manager.ifc.UIManagerIfc;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.pos.services.PosLaneActionAdapter;
import oracle.retail.stores.pos.ui.POSUIManagerIfc;
import oracle.retail.stores.pos.ui.beans.LineItemsModel;

//--------------------------------------------------------------------------
/**
    This road is traversed when the user presses the
    Item key from the SELL_ITEM screen.
    <p>
    @version $Revision: /main/16 $
**/
//--------------------------------------------------------------------------
public class ModifyItemSelectedRoad extends PosLaneActionAdapter
{
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
        Highlighted line items
    **/

   // protected SaleReturnLineItemIfc[] items = null;

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
            }
            cargo.setLineItems(items);
        }
        else
        {
            cargo.setLineItems(null);
        }
    }
}
