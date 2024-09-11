/* ===========================================================================
* Copyright (c) 1998, 2014, Oracle and/or its affiliates. All rights reserved.
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/returns/returntransaction/AddItemToBlindReturnSite.java /main/26 2014/03/11 17:13:56 cgreene Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    cgreene   03/11/14 - add support for returning ASA ordered items
 *    abananan  02/10/14 - Could not add same item numbers for return in
 *                         successive transactions.
 *    vtemker   04/16/13 - Moved constants in OrderLineItemIfc to
 *                         OrderConstantsIfc in common project
 *    rgour     08/02/12 - Improper message on returning a Non-returnable Item
 *    jswan     09/17/10 - Modified to fix an issue with management of the list
 *                         of items available for return.
 *    jswan     09/15/10 - Merged due to refresh.
 *    jswan     09/14/10 - Modified to support verification that serial number
 *                         entered by operator are contained in the external
 *                         order.
 *    ohorne    09/10/10 - return line items are not updated in external order
 *                         system
 *    mchellap  08/31/10 - BUG#9630775 Show select item screen in case of
 *                         multiple IMEI matches.
 *    jswan     08/20/10 - Provide a more descriptive error message where an
 *                         item is not found in the retrieved transaction.
 *    jswan     07/16/10 - Modifications to support the escape/undo
 *                         functionality on the ReturnItemInformation screen in
 *                         the retrieved transaction context.
 *    jswan     07/14/10 - Modifications to support pressing the escape key in
 *                         the EnterItemInformation screen during retrieved
 *                         transaction screen for external order integration.
 *    jswan     06/30/10 - Checkin for first promotion of External Order
 *                         integration.
 *    jswan     06/17/10 - Checkin external order integration files for
 *                         refresh.
 *    jswan     05/26/10 - Fixed warning messages.
 *    cgreene   05/27/10 - convert to oracle packaging
 *    cgreene   05/26/10 - convert to oracle packaging
 *    cgreene   04/28/10 - updating deprecated names
 *    abondala  01/03/10 - update header date
 *    aphulamb  12/17/08 - bug fixing of PDO
 *    aphulamb  12/10/08 - returns functionality changes for greying out
 *                         buttons
 *
 * ===========================================================================
 * $Log:
 *  3    360Commerce 1.2         3/31/2005 4:27:09 PM   Robert Pearse
 *  2    360Commerce 1.1         3/10/2005 10:19:31 AM  Robert Pearse
 *  1    360Commerce 1.0         2/11/2005 12:09:24 PM  Robert Pearse
 * $
 * Revision 1.3  2004/06/11 14:44:02  dfierling
 * @scr 5451 - item now flagged as having a receipt
 *
 * Revision 1.2  2004/03/05 14:39:37  baa
 * @scr 3561  Returns
 *
 * Revision 1.1  2004/03/04 20:52:46  epd
 * @scr 3561 Returns.  Updates for highest price item functionality and code cleanup
 *
 * Revision 1.5  2004/03/04 15:11:04  epd
 * @scr 3561 renamed local variable
 *
 * Revision 1.4  2004/02/27 19:51:16  baa
 * @scr 3561 Return enhancements
 *
 * Revision 1.3  2004/02/27 01:43:29  baa
 * @scr 3561 returns - selecting return items
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

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import oracle.retail.stores.pos.services.common.CommonLetterIfc;
import oracle.retail.stores.common.utility.Util;
import oracle.retail.stores.domain.lineitem.ReturnItemIfc;
import oracle.retail.stores.domain.lineitem.SaleReturnLineItemIfc;
import oracle.retail.stores.domain.order.OrderConstantsIfc;
import oracle.retail.stores.domain.stock.PLUItemIfc;
import oracle.retail.stores.domain.transaction.OrderTransaction;
import oracle.retail.stores.domain.transaction.TransactionIfc;
import oracle.retail.stores.foundation.manager.ifc.UIManagerIfc;
import oracle.retail.stores.foundation.tour.application.Letter;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.pos.services.PosSiteActionAdapter;
import oracle.retail.stores.pos.services.returns.returncommon.SaleReturnLineItemPriceComparator;
import oracle.retail.stores.pos.ui.DialogScreensIfc;
import oracle.retail.stores.pos.ui.POSUIManagerIfc;
import oracle.retail.stores.pos.ui.beans.DialogBeanModel;
import oracle.retail.stores.pos.ui.beans.ItemListBeanModel;

/**
 * This aisle gets the items the user has selected from the UI.
 */
@SuppressWarnings("serial")
public class AddItemToBlindReturnSite extends PosSiteActionAdapter
{

    /**
     * This site attempts to find the highest price item and add it to the
     * transaction.
     *
     * @param bus Service Bus
     */
    @Override
    public void arrive(BusIfc bus)
    { 
        // Get the index of the selected item
        ReturnTransactionCargo cargo = (ReturnTransactionCargo) bus.getCargo();
        POSUIManagerIfc ui = (POSUIManagerIfc)bus.getManager(UIManagerIfc.TYPE);
        boolean itemAvailable = true;

        // get list of matching items.  Use size if we've entered a size
        List<SaleReturnLineItemIfc> matchedItemList = null;
        if (cargo.getSearchCriteria() != null &&
            !Util.isEmpty(cargo.getSearchCriteria().getItemSizeCode()))
        {
            // Get the items with matching size.
            matchedItemList = cargo.getMatchingItemsFromItemsNotDisplayed(cargo.getPLUItemID(),
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
            // Just get the matching items.
            matchedItemList = cargo.getMatchingItemsFromItemsNotDisplayed(cargo.getPLUItemID());
            if (matchedItemList.size() == 0)
            {
                DialogBeanModel dialogModel = cargo.buildItemNotFoundDialogModel(
                    ReturnTransactionCargo.ITEM_NOT_IN_TRANS_MSG, cargo.getPLUItemID(), null, null);
                ui.showScreen(POSUIManagerIfc.DIALOG_TEMPLATE, dialogModel);
                itemAvailable = false;
            }
        }

        // If serial number is also required, try to find an item from the list generated above that contains
        // the serial number collected from the user.
        if (itemAvailable && cargo.getSearchCriteria() != null && cargo.isSerialNumberRequired(matchedItemList))
        {
            matchedItemList = cargo.
                getMatchingSerialNumberItems(matchedItemList, cargo.getSearchCriteria().getItemSerialNumber());

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

            if (itemAvailable && cargo.isExternalOrder())
            {
                itemAvailable = cargo.matchSerialNumberToOrderItem(cargo.getPLUItemID(),
                        cargo.getSearchCriteria().getItemSerialNumber());
                
                if (!itemAvailable)
                {
                    DialogBeanModel dialogModel = cargo.buildItemNotFoundDialogModel(
                            ReturnTransactionCargo.SERIAL_ITEM_NOT_IN_EX_ORDER_MSG, cargo.getPLUItemID(), 
                                cargo.getSearchCriteria().getItemSizeCode(), 
                                    cargo.getSearchCriteria().getItemSerialNumber());
                    ui.showScreen(POSUIManagerIfc.DIALOG_TEMPLATE, dialogModel);
                }
            }
        }

        // Reset the search criteria for the next item.
        cargo.setSearchCriteria(null);

        if (itemAvailable)
        {
            // sort the list according to extended discounted selling price
            // (in descending order to make it easier to iterate).
            Collections.sort(matchedItemList, new SaleReturnLineItemPriceComparator());

            ArrayList<SaleReturnLineItemIfc> itemsEligibleForReturn = new ArrayList<SaleReturnLineItemIfc>();

            // iterate through the list to find first highest price item that is available for return
            Iterator<SaleReturnLineItemIfc> iter = matchedItemList.iterator();
            boolean itemAdded = false, webManaged = false;                        
            OrderTransaction orderTransaction = null;
            int orderType = 0;
            int transactionType = 0;
            if (cargo.getOriginalTransaction() instanceof OrderTransaction)
            {
                orderTransaction = (OrderTransaction)cargo.getOriginalTransaction();
                orderType = orderTransaction.getOrderType();
                transactionType = orderTransaction.getTransactionType();
                webManaged = orderTransaction.isWebManagedOrder();
            }

            while (iter.hasNext())
            {
                SaleReturnLineItemIfc srli = (SaleReturnLineItemIfc) iter.next();
                if (!webManaged && orderType == OrderConstantsIfc.ORDER_TYPE_ON_HAND
                        && transactionType == TransactionIfc.TYPE_ORDER_INITIATE)
                {
                    if (srli.getOrderItemStatus().getItemDispositionCode() == OrderConstantsIfc.ORDER_ITEM_DISPOSITION_DELIVERY
                            || srli.getOrderItemStatus().getItemDispositionCode() == OrderConstantsIfc.ORDER_ITEM_DISPOSITION_PICKUP)
                    {
                        srli.getPLUItem().getItemClassification().setReturnEligible(false);
                    }
                }

                if (srli.isReturnable())
                {
                    itemsEligibleForReturn.add(srli);
                    itemAdded = true;
                }
            }

            int count = 0;

            ArrayList<PLUItemIfc> pluItems = new ArrayList<PLUItemIfc>();
            PLUItemIfc items[] = new PLUItemIfc[matchedItemList.size()];

            if (itemsEligibleForReturn.size() > 0)
            {
                //BUG#9630775 If two items has same serial/IMEI prompt the operator to
                // select an item
                for (SaleReturnLineItemIfc item : matchedItemList)
                {
                    // Check whether the operator entered serial/IMEI number instead of item id
                    if (cargo.getPLUItemID().equals(item.getItemSerial()) && item.isReturnable())
                    {
                        count++;
                    }

                    // Add all the plu items to show it to the user
                    pluItems.add(item.getPLUItem());
                }
                pluItems.toArray(items);
            }
            if (!itemAdded)
            {

                if (!cargo.isItemInDisplayList(cargo.getPLUItemID()))
                {
                    // the item quantity is exhausted in previous transactions
                    // or it isn't a return item
                    DialogBeanModel model = new DialogBeanModel();
                    model.setResourceID("INVALID_SELECTION");
                    model.setType(DialogScreensIfc.ACKNOWLEDGEMENT);
                    ui.showScreen(POSUIManagerIfc.DIALOG_TEMPLATE, model);
                }
                else
                {
                    // the item quantity is exhausted in current ongoing transaction
                    DialogBeanModel model = new DialogBeanModel();
                    String[] arg=new String[1];
                    arg[0]=cargo.getPLUItemID();
                    model.setResourceID("InvalidReturnItems");
                    model.setArgs(arg);
                    model.setType(DialogScreensIfc.ACKNOWLEDGEMENT);
                    ui.showScreen(POSUIManagerIfc.DIALOG_TEMPLATE, model);
                }
            }
            else
            {
                // BUG#9630775 If there are more than one items with same serial/IMEI nmber
                // show select item screen.
                if (count > 1)
                {
                    ItemListBeanModel beanModel = new ItemListBeanModel();
                    beanModel.setItemList(items);
                    ui.setModel(POSUIManagerIfc.SELECT_ITEM, beanModel);
                    ui.showScreen(POSUIManagerIfc.SELECT_ITEM, beanModel);
                }
                else
                {
                    // Clone the line item first so that the the order item
                    // does not get cloned.
                    SaleReturnLineItemIfc clone = (SaleReturnLineItemIfc)itemsEligibleForReturn.get(0).clone();

                    // This call has the side effect of deleting the item from the list of items available for returns.
                    // Since the Collection.remove() method calls equals() method on the SaleReturnLineItemIfc object, 
                    // this call must be made before adding the external order to the clone's PLUItem.
                    cargo.addLineItemToDisplay(clone);

                    // If this is an external order...
                    if (cargo.isExternalOrder())
                    {
                        //associate the current external order item with the current PLUItem.
                        clone.getPLUItem().setReturnExternalOrderItem
                            (cargo.getCurrentExternalOrderItemReturnStatusElement().
                                    getExternalOrderItem());
                        
                        //set Update Source flag 
                        clone.setExternalOrderItemUpdateSourceFlag(cargo.getCurrentExternalOrderItemReturnStatusElement().
                                getExternalOrderItem().isUpdateSourceFlag());
                    }
                    cargo.setHaveReceipt(true);

                    cargo.setReturnSaleLineItems(cargo.getLineItemsToDisplay());
                    int rowsSelected = cargo.getLineItemsToDisplayList().size();
                    cargo.setReturnItems(new ReturnItemIfc[rowsSelected]);
                    bus.mail(new Letter(CommonLetterIfc.CONTINUE), BusIfc.CURRENT);
                }
            }

        }
    }
}
