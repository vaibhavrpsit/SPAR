/* ===========================================================================
* Copyright (c) 1998, 2011, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/returns/returntransaction/AddItemToReturnAisle.java /rgbustores_13.4x_generic_branch/1 2011/05/05 14:05:57 mszekely Exp $
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
 *    cgreene   05/27/10 - convert to oracle packaging
 *    cgreene   05/26/10 - convert to oracle packaging
 *    abondala  01/03/10 - update header date
 *    miparek   11/13/08 - FWD PORT 7432376 :RETURN-TRANSACTION DETAILS
 *                         SCREEN,NOT ABLE TO PROCEED
 *
 * ===========================================================================
 * $Log:
 *  3    360Commerce 1.2         3/31/2005 4:27:09 PM   Robert Pearse
 *  2    360Commerce 1.1         3/10/2005 10:19:31 AM  Robert Pearse
 *  1    360Commerce 1.0         2/11/2005 12:09:24 PM  Robert Pearse
 * $
 * Revision 1.5  2004/03/19 19:13:57  epd
 * @scr 3561 fixed non-returnable items staying selected
 *
 * Revision 1.4  2004/03/05 21:46:58  epd
 * @scr 3561 Updates to implement select highest price item
 *
 * Revision 1.3  2004/03/05 16:01:17  epd
 * @scr 3561 code reformatting and slight refactoring
 *
 * Revision 1.2  2004/02/27 01:43:29  baa
 * @scr 3561 returns - selecting return items
 *
 * Revision 1.1  2004/02/24 22:08:14  baa
 * @scr 3561 continue returns dev
 *
 * Revision 1.2  2004/02/24 15:15:34  baa
 * @scr 3561 returns enter item
 *
 * Revision 1.1  2004/02/23 13:54:52  baa
 * @scr 3561 Return Enhancements to support item size
 * Revision 1.1 2004/02/19 15:37:27 baa @scr 3561
 * returns
 *
 * Revision 1.1 2004/02/18 20:36:20 baa @scr 3561 Returns changes to support size
 * ===========================================================================
 */
package oracle.retail.stores.pos.services.returns.returntransaction;

// java imports
import java.util.Iterator;
import java.util.List;

import oracle.retail.stores.pos.services.common.CommonLetterIfc;

import oracle.retail.stores.foundation.manager.ifc.UIManagerIfc;
import oracle.retail.stores.foundation.tour.application.Letter;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.foundation.utility.Util;

import oracle.retail.stores.pos.services.PosLaneActionAdapter;
import oracle.retail.stores.pos.services.returns.returncommon.ItemIndexContainer;
import oracle.retail.stores.pos.ui.DialogScreensIfc;
import oracle.retail.stores.pos.ui.POSUIManagerIfc;
import oracle.retail.stores.pos.ui.beans.DialogBeanModel;

//--------------------------------------------------------------------------
/**
 * This aisle gets the items the user has selected from the UI.
 */
//--------------------------------------------------------------------------
public class AddItemToReturnAisle extends PosLaneActionAdapter
{
    /** serialVersionUID */
    private static final long serialVersionUID = 2769737465663411360L;

    //----------------------------------------------------------------------
    /**
     * This aisle gets the items the user has selected from the UI.
     * <P>
     *
     * @param bus
     *            Service Bus
     */
    //----------------------------------------------------------------------
    public void traverse(BusIfc bus)
    {
        ReturnTransactionCargo cargo = (ReturnTransactionCargo) bus.getCargo();
        POSUIManagerIfc ui = (POSUIManagerIfc)bus.getManager(UIManagerIfc.TYPE);
        boolean itemAvailable = true;
        
        List<ItemIndexContainer> matchedItemList = null;
        
        if (cargo.getSearchCriteria() != null &&
                !Util.isEmpty(cargo.getSearchCriteria().getItemSizeCode()))
        {
            matchedItemList = cargo.createSortedListOfMatchingItems(
                    cargo.getPLUItemID(),
                    cargo.getSearchCriteria().getItemSizeCode());
            if (matchedItemList.size() == 0)
            {
                DialogBeanModel dialogModel = cargo.buildItemNotFoundDialogModel(
                    ReturnTransactionCargo.SIZE_ITEM_NOT_IN_TRANS_MSG, cargo.getPLUItemID(), 
                        cargo.getSearchCriteria().getItemSizeCode(), null);
                ui.showScreen(POSUIManagerIfc.DIALOG_TEMPLATE, dialogModel);
                itemAvailable = false;
            }
        }
        else
        {
            matchedItemList = cargo.createSortedListOfMatchingItems(
                    cargo.getPLUItemID());
            if (matchedItemList.size() == 0)
            {
                DialogBeanModel dialogModel = cargo.buildItemNotFoundDialogModel(
                    ReturnTransactionCargo.ITEM_NOT_IN_TRANS_MSG, cargo.getPLUItemID(), null, null);
                ui.showScreen(POSUIManagerIfc.DIALOG_TEMPLATE, dialogModel);
                itemAvailable = false;
            }
        }

        if (cargo.getSearchCriteria() != null &&
                !Util.isEmpty(cargo.getSearchCriteria().getItemSerialNumber()))
        {
            matchedItemList = cargo.getListOfMatchingSerialNumberItems(
                    matchedItemList,
                    cargo.getSearchCriteria().getItemSerialNumber());

            if (matchedItemList.size() == 0 && Util.isEmpty(cargo.getSearchCriteria().getItemSizeCode()))
            {
                DialogBeanModel dialogModel = cargo.buildItemNotFoundDialogModel(
                    ReturnTransactionCargo.SERIAL_ITEM_NOT_IN_TRANS_MSG, cargo.getPLUItemID(), 
                        cargo.getSearchCriteria().getItemSizeCode(), 
                            cargo.getSearchCriteria().getItemSerialNumber());
                ui.showScreen(POSUIManagerIfc.DIALOG_TEMPLATE, dialogModel);
                itemAvailable = false;
            }

            if (matchedItemList.size() == 0 && !Util.isEmpty(cargo.getSearchCriteria().getItemSizeCode()))
            {
                DialogBeanModel dialogModel = cargo.buildItemNotFoundDialogModel(
                        ReturnTransactionCargo.SIZE_SERIAL_ITEM_NOT_IN_TRANS_MSG, cargo.getPLUItemID(), 
                            cargo.getSearchCriteria().getItemSizeCode(), 
                                cargo.getSearchCriteria().getItemSerialNumber());
                ui.showScreen(POSUIManagerIfc.DIALOG_TEMPLATE, dialogModel);
                itemAvailable = false;
            }
        }
        
        if (itemAvailable)
        {
            // find out if highest price item is already selected.
            // if not, select it.  If so, then see if next highest price
            // item is selected, etc, etc.
            if (cargo.getSelectedIndexes() != null)
            {
                // indexes of all items
                int[] indexes = cargo.getSelectedIndexes();
                // we have to have a nested loop for this
                for (Iterator<ItemIndexContainer> iter = matchedItemList.iterator(); iter.hasNext(); )
                {
                    ItemIndexContainer currentMatchedItem = iter.next();
                    int currentHighPriceIndex = currentMatchedItem.getIndex();
                    boolean alreadySelected = false;
                    for (int i = 0; i<indexes.length; i++)
                    {
                        if (currentHighPriceIndex == indexes[i])
                        {
                            // this item already selected
                            alreadySelected = true;
                            break; // break inner loop to go to next highest price item
                        }
                    }
                    // if not already selected, then mark this as selected,
                    // and break loop
                    if (!alreadySelected)
                    {
                        cargo.addSelectedItemIndex(currentMatchedItem.getIndex());
                        cargo.setCurrentItem(cargo.getCurrentItem() + 1);
                        break; // breaks outer for loop
                    }
                }
            }
            else
            {
                cargo.addSelectedItemIndex(
                        ((ItemIndexContainer)matchedItemList.get(0)).getIndex());
                cargo.setCurrentItem(((ItemIndexContainer)matchedItemList.get(0)).getIndex());
            }
            cargo.setDoneSelectingDetailItems(true);
            bus.mail(new Letter(CommonLetterIfc.SELECT), BusIfc.CURRENT);
        }
    }

}
