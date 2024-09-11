/* ===========================================================================
* Copyright (c) 1998, 2014, Oracle and/or its affiliates. All rights reserved.
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/returns/returntransaction/SelectReturnItemAisle.java /main/15 2014/02/13 11:27:45 abananan Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    abananan  02/11/14 - Reverted last bug changes as a return item with
 *                         quantity>1 couldn't be returned in separate
 *                         return transactions.
 *    rgour     08/02/12 - Improper message on returning a Non-returnable Item
 *    jswan     06/17/10 - Checkin external order integration files for
 *                         refresh.
 *    jswan     05/26/10 - Fixed warning messages.
 *    cgreene   05/27/10 - convert to oracle packaging
 *    cgreene   05/26/10 - convert to oracle packaging
 *    npoola    04/28/10 - set the QuantityPurchased and QuantityReturnable for
 *                         the return items
 *    abondala  01/03/10 - update header date
 *
 * ===========================================================================
 * $Log:
 *    3    360Commerce 1.2         3/31/2005 4:29:55 PM   Robert Pearse   
 *    2    360Commerce 1.1         3/10/2005 10:25:10 AM  Robert Pearse   
 *    1    360Commerce 1.0         2/11/2005 12:14:08 PM  Robert Pearse   
 *
 *   Revision 1.12  2004/07/28 23:37:47  jdeleau
 *   @scr 6532 If any items are selected which are not valid
 *   return items, the error dialog should appear, even if some of
 *   the selected items are valid.
 *
 *   Revision 1.11  2004/05/03 21:09:43  epd
 *   @scr 4264 Gift Cards now returnable, but still trying to activate.  I'm working on that next
 *
 *   Revision 1.10  2004/03/22 22:39:46  epd
 *   @scr 3561 Refactored cargo to get rid of itemQuantities attribute.  Added it to ReturnItemIfc instead.  Refactored to reduce code complexity and confusion.
 *
 *   Revision 1.9  2004/03/19 19:13:57  epd
 *   @scr 3561 fixed non-returnable items staying selected
 *
 *   Revision 1.8  2004/03/09 17:30:29  epd
 *   @scr 3561 fixed bug
 *
 *   Revision 1.7  2004/03/05 21:46:58  epd
 *   @scr 3561 Updates to implement select highest price item
 *
 *   Revision 1.6  2004/03/04 14:55:51  baa
 *   @scr 3561 return add flow to check for returnable items
 *
 *   Revision 1.5  2004/02/27 19:51:16  baa
 *   @scr 3561 Return enhancements
 *
 *   Revision 1.4  2004/02/27 01:43:29  baa
 *   @scr 3561 returns - selecting return items
 *
 *   Revision 1.3  2004/02/12 16:51:53  mcs
 *   Forcing head revision
 *
 *   Revision 1.2  2004/02/11 21:52:30  rhafernik
 *   @scr 0 Log4J conversion and code cleanup
 *
 *   Revision 1.1.1.1  2004/02/11 01:04:20  cschellenger
 *   updating to pvcs 360store-current
 *
 *
 * 
 *    Rev 1.2   05 Feb 2004 23:31:44   baa
 * return multiple items
 * 
 *    Rev 1.1   26 Jan 2004 00:57:58   baa
 * return development
 * 
 *    Rev 1.0   26 Jan 2004 00:18:10   baa
 * Initial revision.
 * 
 *    Rev 1.0   Aug 29 2003 16:06:22   CSchellenger
 * Initial revision.
 * 
 *    Rev 1.0   Apr 29 2002 15:04:20   msg
 * Initial revision.
 * 
 *    Rev 1.1   25 Apr 2002 18:52:12   pdd
 * Removed unnecessary BigDecimal instantiations.
 * Resolution for POS SCR-1610: Remove inefficient instantiations of BigDecimal
 * 
 *    Rev 1.0   Mar 18 2002 11:46:34   msg
 * Initial revision.
 * 
 *    Rev 1.3   13 Mar 2002 15:25:26   pjf
 * Kit enhancements.
 * Resolution for POS SCR-1554: After returning one item from a kit you cannot retrieve the remaing kit items
 * Resolution for POS SCR-1555: System stuck on Invalid Quantity Error after attempting a second return.
 *
 *    Rev 1.2   10 Mar 2002 11:48:16   pjf
 * Maintain kit inventory at header level.
 * Resolution for POS SCR-1444: Selling then returning a kit does not upadate the inventory count
 * Resolution for POS SCR-1503: When all kit items are returned and attempt to retrieve trans no error displays
 *
 *    Rev 1.1   Feb 05 2002 16:43:24   mpm
 * Modified to use IBM BigDecimal.
 * Resolution for POS SCR-1121: Employ IBM BigDecimal
 *
 *    Rev 1.0   Sep 21 2001 11:25:38   msg
 * Initial revision.
 *
 *    Rev 1.1   Sep 17 2001 13:13:00   msg
 * header update
 * ===========================================================================
 */
package oracle.retail.stores.pos.services.returns.returntransaction;

// java imports
import java.util.ArrayList;
import java.util.Iterator;

import oracle.retail.stores.pos.services.common.CommonLetterIfc;

import oracle.retail.stores.foundation.manager.ifc.UIManagerIfc;
import oracle.retail.stores.foundation.tour.application.Letter;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;

import oracle.retail.stores.domain.DomainGateway;
import oracle.retail.stores.domain.lineitem.KitHeaderLineItemIfc;
import oracle.retail.stores.domain.lineitem.ReturnItemIfc;
import oracle.retail.stores.domain.lineitem.SaleReturnLineItemIfc;

import oracle.retail.stores.pos.services.PosLaneActionAdapter;
import oracle.retail.stores.pos.ui.DialogScreensIfc;
import oracle.retail.stores.pos.ui.POSUIManagerIfc;
import oracle.retail.stores.pos.ui.beans.DialogBeanModel;

//--------------------------------------------------------------------------
/**
    This aisle gets the items the user has selected from the UI.
**/
//--------------------------------------------------------------------------
public class SelectReturnItemAisle extends PosLaneActionAdapter
{
    /** serialVersionUID */
    private static final long serialVersionUID = 6050465194544587703L;

    //----------------------------------------------------------------------
    /**
       This aisle gets the items the user has selected from the UI.
       <P>
       @param  bus     Service Bus
    **/
    //----------------------------------------------------------------------
    public void traverse(BusIfc bus)
    {
        // Get the index of the selected item
        ReturnTransactionCargo cargo = (ReturnTransactionCargo) bus.getCargo();
        String letter = CommonLetterIfc.CONTINUE;

        //This Aisle should make the selected item selected on the list of return items
        //Add previously mark return items to the list

        // 1. Get item info
        // loop through the list of items
        // find the index of the selected item and add it to the cargo if item is returnable.

        int[] rows = cargo.getSelectedIndexes();
        // Get all the selected sales return line items that have returnable
        // items.
        SaleReturnLineItemIfc[] osLineItems = cargo.getOriginalSaleLineItems();
        //create a Collection containing the user selected line items
        ArrayList<SaleReturnLineItemIfc> selected = new ArrayList<SaleReturnLineItemIfc>();
        for (int i = 0; i < rows.length; i++)
        {
            selected.add(osLineItems[rows[i]]);
        }

        if (cargo.isDoneSelectingDetailItems())
        {
            letter = CommonLetterIfc.DONE;
        }

        //check any header items that were in the original transaction
        //if all of a particular header's component items were selected
        //add the header to the collection of items to return
        //this is necessary because the header items are not displayed on a return
        //but the header must be used to track inventory
        KitHeaderLineItemIfc header = null;
        ArrayList<SaleReturnLineItemIfc> lineItems = new ArrayList<SaleReturnLineItemIfc>();
        Iterator<KitHeaderLineItemIfc> iterator = cargo.getKitHeaderItems().iterator();
        while(iterator.hasNext())
        {
            header = iterator.next();
            if (header.areAllComponentsIn(selected))
            {
                lineItems.add(header);
            }
        }
        boolean itemsAdded=false;
        for (SaleReturnLineItemIfc item: selected)
        {
            if(item.isReturnable())
            {
                lineItems.add(item); 
                itemsAdded=true;
            }
            
        }
        if(itemsAdded==false)
            {
         // No items were selected for return
            POSUIManagerIfc ui = (POSUIManagerIfc)bus.getManager(UIManagerIfc.TYPE);
                DialogBeanModel model = new DialogBeanModel();
                model.setResourceID("INVALID_SELECTION");
                model.setType(DialogScreensIfc.ERROR);
                model.setButtonLetter(DialogScreensIfc.BUTTON_OK, "Retry");
                ui.showScreen(POSUIManagerIfc.DIALOG_TEMPLATE, model);
                return;
            }
        

        buildSelectedReturnItemList(cargo, lineItems, lineItems.size());

        bus.mail(new Letter(letter), BusIfc.CURRENT);
     }

    /**
     * Finds the index of the current item in the list of all sale return
     * line items
     * @param item The item for which we want the index
     * @param allItems The array of all the items
     * @return The index into the array of items at which the specified item resides.
     */
    protected int getSelectedIndexOfItem(SaleReturnLineItemIfc item, SaleReturnLineItemIfc[] allItems)
    {
        int result = -1;
        for (int i=0; i<allItems.length; i++)
        {
            if (item.equals(allItems[i]))
            {
                result = i;
                break;
            }
        }
        return result;
    }
    
    //----------------------------------------------------------------------
    /**
     * Builds list of return items
     * @param cargo
     * @param lineItems
     * @param rowsSelected
     */
    //----------------------------------------------------------------------
    private void buildSelectedReturnItemList(ReturnTransactionCargo cargo, ArrayList<SaleReturnLineItemIfc> lineItems, int rowsSelected)
    {
        // Fill the Original and Return Sale Line Item arrays using the
        // vector of Integers from the UI and array of Sale Return Items
        // from the original transaction.
        SaleReturnLineItemIfc[] tsLineItems = new SaleReturnLineItemIfc[rowsSelected];
        SaleReturnLineItemIfc[] rsLineItems = new SaleReturnLineItemIfc[rowsSelected];
        ReturnItemIfc[] returnItems = new ReturnItemIfc[rowsSelected];
        for (int i = 0; i < rowsSelected; i++)
        {
            SaleReturnLineItemIfc item = lineItems.get(i);
            tsLineItems[i] = item;
            rsLineItems[i] = (SaleReturnLineItemIfc) item.clone();
            returnItems[i] = DomainGateway.getFactory().getReturnItemInstance();
            returnItems[i].setItemQuantity(item.getQuantityReturnable());
            returnItems[i].setFromRetrievedTransaction(true);
            returnItems[i].setQuantityPurchased(item.getQuantityReturnable());
            returnItems[i].setQuantityReturnable(item.getQuantityReturnable());              
        }

        // Update the cargo with new arrays.
        cargo.setOriginalSaleLineItems(tsLineItems);
        cargo.setReturnSaleLineItems(rsLineItems);
        cargo.setReturnItems(returnItems);
    }
}
