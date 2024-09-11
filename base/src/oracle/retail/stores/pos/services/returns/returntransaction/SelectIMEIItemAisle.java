/* ===========================================================================
* Copyright (c) 1998, 2011, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/returns/returntransaction/SelectIMEIItemAisle.java /rgbustores_13.4x_generic_branch/1 2011/05/05 14:05:57 mszekely Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    mchellap  08/31/10 - BUG#9630775 Show select item screen in case of
 *                         multiple IMEI matches.
 *
 * ===========================================================================
 */
package oracle.retail.stores.pos.services.returns.returntransaction;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import oracle.retail.stores.common.utility.Util;
import oracle.retail.stores.domain.lineitem.ReturnItemIfc;
import oracle.retail.stores.domain.lineitem.SaleReturnLineItemIfc;
import oracle.retail.stores.domain.stock.PLUItemIfc;
import oracle.retail.stores.foundation.manager.ifc.UIManagerIfc;
import oracle.retail.stores.foundation.tour.application.Letter;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.pos.services.PosLaneActionAdapter;
import oracle.retail.stores.pos.services.common.CommonLetterIfc;
import oracle.retail.stores.pos.services.returns.returncommon.SaleReturnLineItemPriceComparator;
import oracle.retail.stores.pos.ui.DialogScreensIfc;
import oracle.retail.stores.pos.ui.POSUIManagerIfc;
import oracle.retail.stores.pos.ui.beans.DialogBeanModel;
import oracle.retail.stores.pos.ui.beans.ItemListBeanModel;
import oracle.retail.stores.pos.ui.beans.PromptAndResponseModel;

public class SelectIMEIItemAisle extends PosLaneActionAdapter
{

    private static final long serialVersionUID = 1L;

    /**
     * Selects and addes item to return item display
     *
     * @param bus Service Bus
     */
    public void traverse(BusIfc bus)
    {
        // Get the index of the selected item
        ReturnTransactionCargo cargo = (ReturnTransactionCargo) bus.getCargo();
        // Display the screen
        POSUIManagerIfc ui = (POSUIManagerIfc) bus.getManager(UIManagerIfc.TYPE);

        List <SaleReturnLineItemIfc> itemsEligibleForReturn = (LinkedList<SaleReturnLineItemIfc>) cargo
                .getMatchingItemsFromItemsNotDisplayed(cargo.getPLUItemID());

        // sort the list according to extended discounted selling price
        // (in descending order to make it easier to iterate).
        Collections.sort(itemsEligibleForReturn, new SaleReturnLineItemPriceComparator());

        ItemListBeanModel model = (ItemListBeanModel) ui.getModel();
        PromptAndResponseModel pModel = model.getPromptAndResponseModel();
        String itemID = "";
        boolean itemAdded = false;

        // If user manually typed in an item#, match on that item#
        if (pModel.getResponseText() != null && pModel.getResponseText().trim().length() > 0)
        {
            String itemNumber = pModel.getResponseText().trim();
            PLUItemIfc[] items = model.getItemList();
            for (int i = 0; i < items.length; i++)
            {
                if (items[i].getItemID().equals(itemNumber))
                {
                    itemID = items[i].getItemID();
                    break;
                }
            }
        }

        // If no manually typed in number matches, or user never typed a number
        // in, then choose what was selected in the list.
        if (Util.isEmpty(itemID))
        {
            // Store item selected from item list
            itemID = model.getSelectedItem().getItemID();
        }

        for (SaleReturnLineItemIfc item : itemsEligibleForReturn)
        {
            if (item.getItemID().equals(itemID))
            {
                // Clone the line item first so that the the order item
                // does not get cloned.
                SaleReturnLineItemIfc clone = (SaleReturnLineItemIfc) item.clone();
                // If this is an external order, associate the current
                // external order item with the current PLUItem.
                if (cargo.isExternalOrder())
                {
                    clone.getPLUItem().setReturnExternalOrderItem(
                            cargo.getCurrentExternalOrderItemReturnStatusElement().getExternalOrderItem());
                }
                cargo.addLineItemToDisplay(clone);
                cargo.setHaveReceipt(true);
                itemAdded = true;
                break;
            }

        }

        // show a dialog and return if an item was not added
        if (!itemAdded)
        {
            // display Invalid Selection dialog
            DialogBeanModel dialogModel = new DialogBeanModel();
            dialogModel.setResourceID("INVALID_SELECTION");
            dialogModel.setType(DialogScreensIfc.ACKNOWLEDGEMENT);
            ui.showScreen(POSUIManagerIfc.DIALOG_TEMPLATE, model);
        }
        else
        {
            cargo.setReturnSaleLineItems(cargo.getLineItemsToDisplay());
            int rowsSelected = cargo.getLineItemsToDisplayList().size();
            cargo.setReturnItems(new ReturnItemIfc[rowsSelected]);
            bus.mail(new Letter(CommonLetterIfc.CONTINUE), BusIfc.CURRENT);
        }

    }
}
