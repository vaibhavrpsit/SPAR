/* ===========================================================================
* Copyright (c) 1998, 2014, Oracle and/or its affiliates. All rights reserved.
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/returns/returntransaction/GetBlindReturnItemsAisle.java /main/18 2014/06/03 13:25:28 mchellap Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    mchellap  05/09/14 - Using ui.getInput to get item id.
 *    abananan  02/10/14 - Display appropriate screen if item is present in
 *                         transaction but has no more return quantity left.
 *    jkoppolu  11/04/10 - DEFECT#965, Serial number is longer matched with the
 *                         item id to get the matching items list.
 *    jkoppolu  10/14/10 - Added serial number to the cargo search criteria.
 *    mchellap  08/31/10 - BUG#9630775 Show select item screen in case of
 *                         multiple IMEI matches.
 *    jswan     08/20/10 - Provide a more descriptive error message where an
 *                         item is not found in the retrieved transaction.
 *    jswan     06/17/10 - Checkin external order integration files for
 *                         refresh.
 *    jswan     05/26/10 - Fixed warning messages.
 *    cgreene   05/27/10 - convert to oracle packaging
 *    cgreene   05/26/10 - convert to oracle packaging
 *    abondala  01/03/10 - update header date
 *
 * ===========================================================================
 * $Log:
 *    4    360Commerce 1.3         1/25/2006 4:11:01 PM   Brett J. Larsen merge
 *          7.1.1 changes (aka. 7.0.3 fixes) into 360Commerce view
 *    3    360Commerce 1.2         3/31/2005 4:28:14 PM   Robert Pearse
 *    2    360Commerce 1.1         3/10/2005 10:21:47 AM  Robert Pearse
 *    1    360Commerce 1.0         2/11/2005 12:11:09 PM  Robert Pearse
 *:
 *    4    .v700     1.2.1.0     10/28/2005 11:01:02    Deepanshu       CR
 *         6093: Set the return sale line items in cargo.
 *    3    360Commerce1.2         3/31/2005 15:28:14     Robert Pearse
 *    2    360Commerce1.1         3/10/2005 10:21:47     Robert Pearse
 *    1    360Commerce1.0         2/11/2005 12:11:09     Robert Pearse
 *
 *   Revision 1.16  2004/05/03 21:09:43  epd
 *   @scr 4264 Gift Cards now returnable, but still trying to activate.  I'm working on that next
 *
 *   Revision 1.15  2004/03/22 22:39:46  epd
 *   @scr 3561 Refactored cargo to get rid of itemQuantities attribute.  Added it to ReturnItemIfc instead.  Refactored to reduce code complexity and confusion.
 *
 *   Revision 1.14  2004/03/22 06:17:49  baa
 *   @scr 3561 Changes for handling deleting return items
 *
 *   Revision 1.13  2004/03/15 15:16:52  baa
 *   @scr 3561 refactor/clean item size code, search by tender changes
 *
 *   Revision 1.12  2004/03/05 23:27:58  baa
 *   @scr 3561 Retrieve size from scanned items
 *
 *   Revision 1.11  2004/03/04 20:52:46  epd
 *   @scr 3561 Returns.  Updates for highest price item functionality and code cleanup
 *
 *   Revision 1.10  2004/03/01 22:50:46  epd
 *   @scr 3561 Updates for returns - tax related
 *
 *   Revision 1.9  2004/02/27 01:43:29  baa
 *   @scr 3561 returns - selecting return items
 *
 *   Revision 1.8  2004/02/24 15:15:34  baa
 *   @scr 3561 returns enter item
 *
 *   Revision 1.7  2004/02/23 13:54:52  baa
 *   @scr 3561 Return Enhancements to support item size
 *
 *   Revision 1.6  2004/02/19 15:37:27  baa
 *   @scr 3561 returns
 *
 *   Revision 1.5  2004/02/16 13:36:33  baa
 *   @scr 3561 returns enhancements
 *
 *   Revision 1.4  2004/02/12 20:41:41  baa
 *   @scr 0 fixjavadoc
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
import java.util.Iterator;
import java.util.List;

import oracle.retail.stores.domain.DomainGateway;
import oracle.retail.stores.domain.lineitem.ReturnItemIfc;
import oracle.retail.stores.domain.lineitem.SaleReturnLineItemIfc;
import oracle.retail.stores.domain.stock.PLUItemIfc;
import oracle.retail.stores.foundation.manager.ifc.UIManagerIfc;
import oracle.retail.stores.foundation.tour.application.Letter;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.common.utility.Util;
import oracle.retail.stores.pos.services.common.CommonLetterIfc;
import oracle.retail.stores.pos.services.returns.returnoptions.ValidateItemNumberAisle;
import oracle.retail.stores.pos.ui.DialogScreensIfc;
import oracle.retail.stores.pos.ui.POSUIManagerIfc;
import oracle.retail.stores.pos.ui.beans.DialogBeanModel;
import oracle.retail.stores.pos.ui.beans.LineItemsModel;
import oracle.retail.stores.pos.ui.beans.PromptAndResponseModel;

//--------------------------------------------------------------------------
/**
    This aisle gets the items the user has selected from the UI.
**/
//--------------------------------------------------------------------------
public class GetBlindReturnItemsAisle extends ValidateItemNumberAisle
{
    /** serialVersionUID */
    private static final long serialVersionUID = -4542437871647664329L;

    //----------------------------------------------------------------------
    /**
       This aisle gets the items the user has selected from the UI.
       <P>
       @param  bus     Service Bus
    **/
    //----------------------------------------------------------------------
    public void traverse(BusIfc bus)
    {

        //Get entered item from
        // Get the index of the selected item
        POSUIManagerIfc ui = (POSUIManagerIfc) bus.getManager(UIManagerIfc.TYPE);
        LineItemsModel model = (LineItemsModel) ui.getModel(POSUIManagerIfc.RETURN_SELECT_ITEM);
        PromptAndResponseModel promptModel =  model.getPromptAndResponseModel();
        String itemID = ui.getInput();
        ReturnTransactionCargo cargo = (ReturnTransactionCargo) bus.getCargo();
        String letter = CommonLetterIfc.DONE;
        if (Util.isEmpty(itemID))
        {
            // No more items will be returned, process the current transaction.
            // Update quantities from the return screen
            List<SaleReturnLineItemIfc> returnItemsList = cargo.getLineItemsToDisplayList();

             if (returnItemsList != null && returnItemsList.size() > 0)
             {
            	 SaleReturnLineItemIfc[] rsLineItems = new SaleReturnLineItemIfc[returnItemsList.size()];
                 ReturnItemIfc[] returnItems = new ReturnItemIfc[returnItemsList.size()];
                 SaleReturnLineItemIfc item;
                 int index = 0;
                 for (Iterator<SaleReturnLineItemIfc> i = returnItemsList.iterator(); i.hasNext();)
                 {
                     item = i.next();
                     rsLineItems[index] = (SaleReturnLineItemIfc) item.clone();
                     returnItems[index] = DomainGateway.getFactory().getReturnItemInstance();
                     returnItems[index].setItemQuantity(item.getQuantityReturnable());
                     returnItems[index].setFromRetrievedTransaction(true);
                     index++;
                 }
                 cargo.setReturnSaleLineItems(rsLineItems);
                 cargo.setReturnItems(returnItems);
                 bus.mail(new Letter(letter), BusIfc.CURRENT);
             }
             else
             {
                 // No items were selected for return
                 DialogBeanModel dialogModel = new DialogBeanModel();
                 dialogModel.setResourceID("NoSelectedItem");
                 dialogModel.setType(DialogScreensIfc.ERROR);
                 dialogModel.setButtonLetter(DialogScreensIfc.BUTTON_OK,"Invalid");
                 // display the screen
                 ui.showScreen(POSUIManagerIfc.DIALOG_TEMPLATE, dialogModel);
             }
        }
        else
        {
            String itemNumber = itemID;
            boolean isScanned = promptModel.isScanned();
            cargo.setItemScanned(isScanned);
            if (isScanned)
            {
                // if scanned parse for item no and size
                itemNumber = processScannedItemNumber(cargo, itemID);
            }
            cargo.setPLUItemID(itemNumber);
            cargo.setItemScanned(isScanned);
            // Is there a matching item in the list of items not yet displayed?
            List<SaleReturnLineItemIfc> matchedItemList = cargo.getMatchingItemsFromItemsNotDisplayed(itemNumber);
            PLUItemIfc returnPLUItem = null;

            if (matchedItemList.size() > 0)
            {
                // we only need one PLUitem from any of the matched items. take
                // the first
                returnPLUItem = matchedItemList.get(0).getPLUItem();
            }
            if (returnPLUItem == null)
            {
                // it is possible that the item entered now has no return
                // quantity left and is deleted from itemsToDisplaylist ,if so
                // display invalid selection screen
                // instead of item not found in transaction screen
               if (cargo.isItemInDisplayList(itemID) == true)
                {
                    
                    DialogBeanModel dialogModel = cargo.buildItemNotFoundDialogModel(
                            ReturnTransactionCargo.INVALID_RETURN_MSG, cargo.getPLUItemID(), null, null);
                    ui.showScreen(POSUIManagerIfc.DIALOG_TEMPLATE, dialogModel);
                }

                else
                {
                DialogBeanModel dialogModel = cargo.buildItemNotFoundDialogModel(
                        ReturnTransactionCargo.ITEM_NOT_IN_TRANS_MSG, cargo.getPLUItemID(),
                            null, null);
                ui.showScreen(POSUIManagerIfc.DIALOG_TEMPLATE, dialogModel);
            }
            }
            else if (returnPLUItem.isItemSizeRequired() && !isScanned)
            {
                // retrieve size info
                letter = CommonLetterIfc.SIZE;
                bus.mail(new Letter(letter), BusIfc.CURRENT);
            }
            else
            if(!Util.isEmpty(matchedItemList.get(0).getItemSerial()))
            {
                bus.mail(new Letter(CommonLetterIfc.SERIAL_NUMBER), BusIfc.CURRENT);
            }
            else
            {
                letter = CommonLetterIfc.ADD;
                bus.mail(new Letter(letter), BusIfc.CURRENT);
            }

        }

    }

}
