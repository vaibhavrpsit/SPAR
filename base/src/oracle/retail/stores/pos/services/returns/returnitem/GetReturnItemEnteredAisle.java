/* ===========================================================================
* Copyright (c) 1998, 2013, Oracle and/or its affiliates. All rights reserved.
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/returns/returnitem/GetReturnItemEnteredAisle.java /main/11 2013/09/10 16:29:58 yiqzhao Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    yiqzhao   09/10/13 - Only support upper case for item id, like sale in
 *                         ItemNumberEnteredAisle.java
 *    jswan     06/17/10 - Checkin external order integration files for
 *                         refresh.
 *    jswan     05/27/10 - First pass changes to return item for external order
 *                         project.
 *    cgreene   05/26/10 - convert to oracle packaging
 *
 * ===========================================================================
 * $Log:
 *    4    360Commerce 1.3         3/10/2008 3:51:48 PM   Sandy Gu
 *         Specify store id for non receipted return item query.
 *    3    360Commerce 1.2         3/31/2005 4:28:15 PM   Robert Pearse   
 *    2    360Commerce 1.1         3/10/2005 10:21:50 AM  Robert Pearse   
 *    1    360Commerce 1.0         2/11/2005 12:11:11 PM  Robert Pearse   
 *
 *   Revision 1.11  2004/07/22 23:08:57  blj
 *   @scr 6258 - changed the flow so that if UNDO is pressed, we dont lookup the item again we use the information previously entered.
 *
 *   Revision 1.10  2004/06/07 19:59:00  mkp1
 *   @scr 2775 Put correct header on files
 *
 *   Revision 1.9  2004/03/26 05:39:05  baa
 *   @scr 3561 Returns - modify flow to support entering price code for not found gift receipt
 *
 *   Revision 1.8  2004/03/22 06:17:50  baa
 *   @scr 3561 Changes for handling deleting return items
 *
 *   Revision 1.7  2004/03/12 16:26:09  baa
 *   @scr 3561 fix bugs with flow esc item size
 *
 *   Revision 1.6  2004/03/05 23:27:58  baa
 *   @scr 3561 Retrieve size from scanned items
 *
 *   Revision 1.5  2004/02/27 01:43:29  baa
 *   @scr 3561 returns - selecting return items
 *
 *   Revision 1.4  2004/02/19 15:37:31  baa
 *   @scr 3561 returns
 *
 *   Revision 1.3  2004/02/12 16:51:49  mcs
 *   Forcing head revision
 *
 *   Revision 1.2  2004/02/11 21:52:29  rhafernik
 *   @scr 0 Log4J conversion and code cleanup
 *
 *   Revision 1.1.1.1  2004/02/11 01:04:20  cschellenger
 *   updating to pvcs 360store-current
 *
 *
 * 
 *    Rev 1.2   05 Feb 2004 23:22:08   baa
 * returns multi items
 * 
 *    Rev 1.1   Dec 19 2003 13:22:46   baa
 * more return enhancements
 * Resolution for 3561: Feature Enhacement: Return Search by Tender
 * 
 *    Rev 1.0   Aug 29 2003 16:06:02   CSchellenger
 * Initial revision.
 * 
 *    Rev 1.2   Jul 15 2003 14:46:26   baa
 * allow alphanumeric values on sale associate field
 * Resolution for 3121: sales associate field not editable
 * 
 *    Rev 1.1   Feb 16 2003 10:43:32   mpm
 * Merged 5.1 changes.
 * Resolution for POS SCR-2053: Merge 5.1 changes into 6.0
 *
 *    Rev 1.0   Apr 29 2002 15:05:22   msg
 * Initial revision.
 * ===========================================================================
 */
package oracle.retail.stores.pos.services.returns.returnitem;

import oracle.retail.stores.pos.services.common.CommonLetterIfc;

import oracle.retail.stores.foundation.manager.ifc.UIManagerIfc;
import oracle.retail.stores.foundation.tour.application.Letter;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.foundation.utility.Util;

import oracle.retail.stores.pos.services.returns.returnoptions.ValidateItemNumberAisle;
import oracle.retail.stores.pos.ui.DialogScreensIfc;
import oracle.retail.stores.pos.ui.POSUIManagerIfc;
import oracle.retail.stores.pos.ui.beans.DialogBeanModel;
import oracle.retail.stores.pos.ui.beans.POSBaseBeanModel;
import oracle.retail.stores.pos.ui.beans.PromptAndResponseModel;

//--------------------------------------------------------------------------
/**
    This aisle gets the Return item information from
    the UI and mails a Continue letter.
**/
//--------------------------------------------------------------------------
public class GetReturnItemEnteredAisle extends ValidateItemNumberAisle
{

    /** serialVersionUID */
    private static final long serialVersionUID = 188580486127201303L;

    //----------------------------------------------------------------------
    /**
       This aisle gets the number of the return item from the response area
       and mails a Continue letter.
       <P>
       @param  bus     Service Bus
    **/
    //----------------------------------------------------------------------
    public void traverse(BusIfc bus)
    {

        // retrieve cargo
        ReturnItemCargo cargo = (ReturnItemCargo) bus.getCargo();
        // get the ui manager
        POSUIManagerIfc ui = (POSUIManagerIfc) bus.getManager(UIManagerIfc.TYPE);
        // Continue to lookup item
        String letter = CommonLetterIfc.CONTINUE;
        String itemID = cargo.getPLUItemID();
        boolean isScanned = cargo.isItemScanned();
         
        String screenType = POSUIManagerIfc.RETURN_ITEM_NON_RETRIEVED;
        if (!cargo.haveReceipt() && !cargo.isGiftReceiptSelected())
        {
            screenType = POSUIManagerIfc.RETURN_ITEM_NO_RECEIPT;
        }
        
        // If no item is coming from the cargo, check the input from
        // the response area
        if (Util.isEmpty(itemID))
        {
           PromptAndResponseModel parModel =
            ((POSBaseBeanModel) ui.getModel(screenType)).getPromptAndResponseModel();
           itemID = parModel.getResponseText();
           isScanned = parModel.isScanned();
         
        }
        
        
        // Store the item number in the cargo
        if (!Util.isEmpty(itemID))
        {
            // as of 13.1 we only support upper case POS ID, BugDB 8295250
            itemID = itemID.toUpperCase();
            String itemNumber = itemID.toUpperCase();
            if (isScanned)
            {
                // if scanned parse for item no and size
                itemNumber = processScannedItemNumber(cargo, itemID);
            }
            cargo.setPLUItemID(itemNumber);
            cargo.setItemScanned(isScanned);
            
            //Have to set the GeoCode to get the tax rules back
            if(cargo.getStoreStatus() != null &&
                    cargo.getStoreStatus().getStore() != null )
            {
                cargo.setGeoCode(cargo.getStoreStatus().getStore().getGeoCode());
                cargo.setStoreID(cargo.getStoreStatus().getStore().getStoreID());
            }
            // If the ReturnData object is updated, we have
            // the item information, no item lookup needed.
            // This may happen if the user selects UNDO on a
            // previous screen.
            if (cargo.getReturnData() != null)
            {
                letter = CommonLetterIfc.SUCCESS;
            }
            bus.mail(new Letter(letter), BusIfc.CURRENT);
        }
        else
        {
            // Check if there  have been selection made
            if (cargo.getReturnItems() != null)
            {
                cargo.setCurrentItem(-1);
                letter = CommonLetterIfc.SUCCESS;
                // mail a letter to Continue; go and lookup the item
                bus.mail(new Letter(letter), BusIfc.CURRENT);
            }
            else
            {
                // Get the ui manager
                DialogBeanModel dialogModel = new DialogBeanModel();
                dialogModel.setResourceID("NoSelectedItem");
                dialogModel.setType(DialogScreensIfc.ERROR);
                dialogModel.setButtonLetter(DialogScreensIfc.BUTTON_OK,CommonLetterIfc.INVALID);
                // display the screen
                ui.showScreen(POSUIManagerIfc.DIALOG_TEMPLATE, dialogModel);
            }
                
        }
    }

}
