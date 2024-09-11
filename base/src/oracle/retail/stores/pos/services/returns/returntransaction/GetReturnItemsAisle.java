/* ===========================================================================
* Copyright (c) 1998, 2014, Oracle and/or its affiliates. All rights reserved.
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/returns/returntransaction/GetReturnItemsAisle.java /main/17 2014/06/14 17:26:44 mchellap Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    mchellap  06/14/14 - Fixed mpos NPE
 *    mchellap  05/22/14 - MPOS Returns: Use getInput method for getting
 *                         response text
 *    rgour     08/02/12 - Improper message on returning a Non-returnable Item
 *    sgu       12/20/10 - check in all after merge
 *    sgu       12/20/10 - XbranchMerge sgu_bug-10415467 from
 *                         rgbustores_13.3x_generic_branch
 *    sgu       12/20/10 - check in all
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
 *4    360Commerce 1.3         4/4/2008 3:12:27 AM    Sujay Beesnalli Forward
 *     porting CR# 30354 from v12x. Added flags to determine highlighting of
 *     rows.
 *3    360Commerce 1.2         3/31/2005 4:28:15 PM   Robert Pearse
 *2    360Commerce 1.1         3/10/2005 10:21:50 AM  Robert Pearse
 *1    360Commerce 1.0         2/11/2005 12:11:11 PM  Robert Pearse
 *
 Revision 1.15  2004/03/15 15:16:52  baa
 @scr 3561 refactor/clean item size code, search by tender changes
 *
 Revision 1.14  2004/03/05 23:27:58  baa
 @scr 3561 Retrieve size from scanned items
 *
 Revision 1.13  2004/03/05 21:46:58  epd
 @scr 3561 Updates to implement select highest price item
 *
 Revision 1.12  2004/03/05 16:01:17  epd
 @scr 3561 code reformatting and slight refactoring
 *
 Revision 1.11  2004/03/04 14:55:51  baa
 @scr 3561 return add flow to check for returnable items
 *
 Revision 1.10  2004/03/01 22:50:46  epd
 @scr 3561 Updates for returns - tax related
 *
 Revision 1.9  2004/02/27 01:43:29  baa
 @scr 3561 returns - selecting return items
 *
 Revision 1.8  2004/02/24 22:08:14  baa
 @scr 3561 continue returns dev
 *
 Revision 1.7  2004/02/24 15:15:34  baa
 @scr 3561 returns enter item
 *
 Revision 1.6  2004/02/23 13:54:52  baa
 @scr 3561 Return Enhancements to support item size
 *
 Revision 1.5  2004/02/18 20:36:20  baa
 @scr 3561 Returns changes to support size
 *
 Revision 1.4  2004/02/12 20:41:41  baa
 @scr 0 fixjavadoc
 *
 Revision 1.3  2004/02/12 16:51:53  mcs
 Forcing head revision
 *
 Revision 1.2  2004/02/11 21:52:30  rhafernik
 @scr 0 Log4J conversion and code cleanup
 *
 Revision 1.1.1.1  2004/02/11 01:04:20  cschellenger
 updating to pvcs 360store-current
 *
 *
 *
 *    Rev 1.1   26 Jan 2004 00:14:00   baa
 * continue return development
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

import java.util.HashSet;
import java.util.Iterator;
import java.util.List;

import oracle.retail.stores.pos.services.common.CommonLetterIfc;

import oracle.retail.stores.foundation.manager.ifc.UIManagerIfc;
import oracle.retail.stores.foundation.tour.application.Letter;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.common.utility.Util;

import oracle.retail.stores.domain.lineitem.SaleReturnLineItemIfc;
import oracle.retail.stores.domain.stock.PLUItemIfc;

import oracle.retail.stores.pos.services.returns.returnoptions.ValidateItemNumberAisle;
import oracle.retail.stores.pos.ui.DialogScreensIfc;
import oracle.retail.stores.pos.ui.POSUIManagerIfc;
import oracle.retail.stores.pos.ui.beans.DialogBeanModel;
import oracle.retail.stores.pos.ui.beans.LineItemsModel;
import oracle.retail.stores.pos.ui.beans.PromptAndResponseModel;

//--------------------------------------------------------------------------
/**
 * This aisle gets the items the user has selected from the UI.
 */
//--------------------------------------------------------------------------
public class GetReturnItemsAisle extends ValidateItemNumberAisle
{
    /** serialVersionUID */
    private static final long serialVersionUID = 8132832711575370133L;

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
        // Get the index of the selected item
        POSUIManagerIfc ui = (POSUIManagerIfc) bus.getManager(UIManagerIfc.TYPE);
        PromptAndResponseModel promptModel = null;

        LineItemsModel beanModel = (LineItemsModel) ui.getModel(POSUIManagerIfc.RETURN_TRANSACTION_DETAILS);
        if (beanModel != null)
            promptModel = beanModel.getPromptAndResponseModel();
       // String itemID = promptModel.getResponseText();
        String itemID = ui.getInput();
        ReturnTransactionCargo cargo = (ReturnTransactionCargo) bus.getCargo();
        String letter = CommonLetterIfc.CONTINUE;
        //Add previously mark return items to the list

        if (!Util.isEmpty(itemID))
        {
            String itemNumber = itemID;
            boolean isScanned = false;
            if (promptModel != null)
                isScanned = promptModel.isScanned();
            cargo.setItemScanned(isScanned);
            if (isScanned)
            {
                // if scanned parse for item no and size
                itemNumber = processScannedItemNumber(cargo, itemID);
            }
            cargo.setPLUItemID(itemNumber);
            // Check if item is in transaction
            PLUItemIfc returnPLUItem = cargo.getItemFromTransaction(itemNumber);
            if (returnPLUItem == null)
            {
                // This item is not in the transaction
                DialogBeanModel dialogModel = cargo.buildItemNotFoundDialogModel(
                    ReturnTransactionCargo.ITEM_NOT_IN_TRANS_MSG, cargo.getPLUItemID(), null, null);
                ui.showScreen(POSUIManagerIfc.DIALOG_TEMPLATE, dialogModel);
                return;
            }

            else if (returnPLUItem.isItemSizeRequired() && !isScanned)
            {
                letter = CommonLetterIfc.SIZE;
            }
            else
            if (isSerialNumberRequired(cargo, itemNumber))
            {
                letter = CommonLetterIfc.SERIAL_NUMBER;
            }
            else
            {
            	// For the first visit, clear the previous selections
            	// and add entered item into the selections. Otherwise mark entered item as
            	// selected along with old selections.
            	int[] rows = cargo.getSelectedIndexes();
            	HashSet<String> selectedItems = new HashSet<String>();
            	SaleReturnLineItemIfc[] osLineItems = cargo.getOriginalSaleLineItems();

            	if(cargo.isTransDetailFreshVisit())
            	{
            		rows = null;
            	}

		        for (int i = 0; ((rows != null) && (i < rows.length)); i++)
		        {
	            	selectedItems.add(String.valueOf(rows[i]));
		        }
			    //find the index of entered item
		        for (int i = 0; i < osLineItems.length; i++)
		        {
		        	if(Util.isObjectEqual(osLineItems[i].getItemID(), itemID))
		        	{
		        		selectedItems.add(String.valueOf(i));
		        		break;
		        	}
		        }
            	// refresh the selected items
		        cargo.setSelectedIndexes(null);
		        int index = 0;
	        	Iterator<String> iterator = selectedItems.iterator();
	        	while(iterator.hasNext())
	        	{
	        		index = Integer.parseInt(iterator.next());
	        		cargo.markItemSelected(osLineItems[index]);
        			cargo.setCurrentItem(cargo.getCurrentItem()+index);
	        	}

                letter = CommonLetterIfc.ADD;
            }
        }
        else
        {
            //  No items were added we are ready to complete the return
            // Clear selected index
            int[] selectedRows = null;
            if (beanModel != null)
                selectedRows = beanModel.getSelectedRows();
            if (selectedRows == null || selectedRows.length == 0)
            {
                // No items were selected for return
                DialogBeanModel model = new DialogBeanModel();
                model.setResourceID("NO_SELECTION");
                model.setType(DialogScreensIfc.ERROR);
                model.setButtonLetter(DialogScreensIfc.BUTTON_OK, "Retry");
                ui.showScreen(POSUIManagerIfc.DIALOG_TEMPLATE, model);
                return;
            }
            else
            {
                //check that if selected indexes are added to return list
                cargo.setSelectedIndexes(beanModel.getSelectedRows());
                cargo.setDoneSelectingDetailItems(true);
                letter = CommonLetterIfc.SELECT;
            }
        }

        bus.mail(new Letter(letter), BusIfc.CURRENT);
    }

    /*
     * This method determines if an item the original transaction needs to the
     * serial number to be collected.
     */
    private boolean isSerialNumberRequired(ReturnTransactionCargo cargo, String itemNumber)
    {
        boolean required = false;

        List<SaleReturnLineItemIfc> matchedItemList = cargo.getMatchingItemsFromItemsNotDisplayed(itemNumber);
        if (cargo.isSerialNumberRequired(matchedItemList))
        {
            required = true;
        }
        return required;
    }
}
