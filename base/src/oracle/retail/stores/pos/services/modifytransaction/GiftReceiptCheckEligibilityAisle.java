/* ===========================================================================
* Copyright (c) 1998, 2011, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/modifytransaction/GiftReceiptCheckEligibilityAisle.java /rgbustores_13.4x_generic_branch/1 2011/05/05 14:06:31 mszekely Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    cgreene   05/26/10 - convert to oracle packaging
 *    cgreene   04/26/10 - XbranchMerge cgreene_tech43 from
 *                         st_rgbustores_techissueseatel_generic_branch
 *    cgreene   04/02/10 - remove deprecated LocaleContantsIfc and currencies
 *    abondala  01/03/10 - update header date
 *
 * ===========================================================================
 * $Log:
 *    3    360Commerce 1.2         3/31/2005 4:28:18 PM   Robert Pearse   
 *    2    360Commerce 1.1         3/10/2005 10:21:57 AM  Robert Pearse   
 *    1    360Commerce 1.0         2/11/2005 12:11:16 PM  Robert Pearse   
 *
 *   Revision 1.1  2004/06/28 16:53:44  aschenk
 *   @scr 4864 - Added Gift receipt option to Transaction menu
 *
 *   Revision 1.3  2004/02/12 16:51:01  mcs
 *   Forcing head revision
 *
 *   Revision 1.2  2004/02/11 21:39:28  rhafernik
 *   @scr 0 Log4J conversion and code cleanup
 *
 *   Revision 1.1.1.1  2004/02/11 01:04:17  cschellenger
 *   updating to pvcs 360store-current
 *
 *
 * 
 *    Rev 1.0   Feb 05 2004 14:23:36   bwf
 * Initial revision.
 * ===========================================================================
 */
package oracle.retail.stores.pos.services.modifytransaction;


import oracle.retail.stores.common.utility.LocaleMap;
import oracle.retail.stores.domain.utility.LocaleConstantsIfc;
import oracle.retail.stores.domain.lineitem.AbstractTransactionLineItemIfc;
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
    This class checks to see if gift receipt is being applied to a damage
    discounted item.
    $Revision: /rgbustores_13.4x_generic_branch/1 $
**/
//--------------------------------------------------------------------------
public class GiftReceiptCheckEligibilityAisle extends PosLaneActionAdapter
{    
    public static final String LANENAME = "GiftReceiptCheckEligibilityAisle";

    //--------------------------------------------------------------------------
    /**
         This method traverses the aisle.
         @param bus the bus traversing this lane
     **/
    //--------------------------------------------------------------------------

    public void traverse(BusIfc bus)
    {
        ModifyTransactionCargo cargo = (ModifyTransactionCargo)bus.getCargo();
        AbstractTransactionLineItemIfc[] lineItems = cargo.getTransaction().getLineItems();
        boolean showDialogScreen = false;
        String[] args = {"","",""};
        //Retrieve descriptions of ineligible gift receipt items to show on error screen
        int filledSlots = 0;
        int itemCount = 0;
        for (int i=0; i < lineItems.length; i++)
        {
            if(((SaleReturnLineItemIfc)lineItems[i]).hasDamageDiscount())
            {
                itemCount++;
                if(filledSlots < 3)
                {
                    args[filledSlots] = ((SaleReturnLineItemIfc)lineItems[i]).getItemDescription(LocaleMap.getLocale(LocaleConstantsIfc.USER_INTERFACE));
                    filledSlots++;
                    showDialogScreen = true;
                }
               else
               {
                   break;
               }
            }
        }
        
        if(showDialogScreen)
        {
            //get ui manager
            POSUIManagerIfc ui = (POSUIManagerIfc)bus.getManager(UIManagerIfc.TYPE);
            // Display error message
            DialogBeanModel dialogModel = new DialogBeanModel();
            dialogModel.setType(DialogScreensIfc.ERROR);
            dialogModel.setButtonLetter(0,"GiftReceiptEligible");
            
            if (itemCount < lineItems.length)
            {
                dialogModel.setResourceID("IneligibleForGiftReceipt");
            }
            else
            {
                dialogModel.setResourceID("AllItemsIneligibleForGiftReceipt");
            }
            dialogModel.setArgs(args);
            ui.showScreen(POSUIManagerIfc.DIALOG_TEMPLATE, dialogModel);
        }
        else
        {
            bus.mail(new Letter("GiftReceiptEligible"), BusIfc.CURRENT);
        }
    }
    
}
