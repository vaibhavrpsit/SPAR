/* ===========================================================================
* Copyright (c) 1998, 2011, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/returns/returntransaction/CheckForItemInTransactionAisle.java /rgbustores_13.4x_generic_branch/1 2011/05/05 14:05:57 mszekely Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    jswan     08/20/10 - Provide a more descriptive error message where an
 *                         item is not found in the retrieved transaction.
 *    jswan     06/17/10 - Checkin external order integration files for
 *                         refresh.
 *    jswan     05/26/10 - Fixed warning messages.
 *    cgreene   05/26/10 - convert to oracle packaging
 *    abondala  01/03/10 - update header date
 *
 * ===========================================================================
 * $Log:
 *    3    360Commerce 1.2         3/31/2005 4:27:24 PM   Robert Pearse   
 *    2    360Commerce 1.1         3/10/2005 10:20:08 AM  Robert Pearse   
 *    1    360Commerce 1.0         2/11/2005 12:09:55 PM  Robert Pearse   
 *
 *   Revision 1.8  2004/03/05 21:46:58  epd
 *   @scr 3561 Updates to implement select highest price item
 *
 *   Revision 1.7  2004/03/04 20:52:46  epd
 *   @scr 3561 Returns.  Updates for highest price item functionality and code cleanup
 *
 *   Revision 1.6  2004/03/04 14:55:51  baa
 *   @scr 3561 return add flow to check for returnable items
 *
 *   Revision 1.5  2004/03/03 22:31:23  epd
 *   @scr 3561 Returns updates - select highest price item
 *
 *   Revision 1.4  2004/02/27 19:51:16  baa
 *   @scr 3561 Return enhancements
 *
 *   Revision 1.3  2004/02/24 15:15:33  baa
 *   @scr 3561 returns enter item
 *
 *   Revision 1.2  2004/02/23 13:54:52  baa
 *   @scr 3561 Return Enhancements to support item size
 *
 *   Revision 1.1  2004/02/18 20:36:20  baa
 *   @scr 3561 Returns changes to support size
 *
 * ===========================================================================
 */
package oracle.retail.stores.pos.services.returns.returntransaction;

// java imports
import java.util.ArrayList;
import java.util.List;

import oracle.retail.stores.pos.services.common.CommonLetterIfc;
import oracle.retail.stores.pos.services.returns.returncommon.ItemIndexContainer;

import oracle.retail.stores.domain.lineitem.SaleReturnLineItemIfc;
import oracle.retail.stores.foundation.manager.ifc.UIManagerIfc;
import oracle.retail.stores.foundation.tour.application.Letter;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;

import oracle.retail.stores.pos.services.PosLaneActionAdapter;
import oracle.retail.stores.pos.ui.DialogScreensIfc;
import oracle.retail.stores.pos.ui.POSUIManagerIfc;
import oracle.retail.stores.pos.ui.beans.DialogBeanModel;

//--------------------------------------------------------------------------
/**
    This aisle gets the items the user has selected from the UI.
**/
//--------------------------------------------------------------------------
public class CheckForItemInTransactionAisle extends PosLaneActionAdapter
{
    /** serialVersionUID */
    private static final long serialVersionUID = 2955895810712196769L;

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
        // Make sure there is at least one match for item and size in transaction.
        List<ItemIndexContainer> matchingItemList = cargo.createSortedListOfMatchingItems(
                cargo.getPLUItemID(), cargo.getSearchCriteria().getItemSizeCode());
        if (matchingItemList.size() > 0)
        {
            String letter = CommonLetterIfc.ADD;
            if (isSerialNumberRequired(cargo, matchingItemList))
            {
                letter = CommonLetterIfc.SERIAL_NUMBER;
            }
            bus.mail(new Letter(letter), BusIfc.CURRENT);
        }
        else
        {
            DialogBeanModel dialogModel = cargo.buildItemNotFoundDialogModel(
                    ReturnTransactionCargo.SIZE_ITEM_NOT_IN_TRANS_MSG, cargo.getPLUItemID(), 
                        cargo.getSearchCriteria().getItemSizeCode(), null);
            POSUIManagerIfc ui = (POSUIManagerIfc)bus.getManager(UIManagerIfc.TYPE);
            ui.showScreen(POSUIManagerIfc.DIALOG_TEMPLATE, dialogModel);
            return;
        }
    }

    /*
     * This method determines if there any items in this list that require the entry of a serial number.
     */
    private boolean isSerialNumberRequired(ReturnTransactionCargo cargo, List<ItemIndexContainer> matchingItemList)
    {
        ArrayList<SaleReturnLineItemIfc> list = new ArrayList<SaleReturnLineItemIfc>();
        for(ItemIndexContainer container: matchingItemList)
        {
            list.add(container.getItem());
        }
        
        return cargo.isSerialNumberRequired(list);
    }
}
