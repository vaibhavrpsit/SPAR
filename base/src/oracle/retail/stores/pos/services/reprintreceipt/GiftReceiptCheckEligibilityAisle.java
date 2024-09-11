/* ===========================================================================
* Copyright (c) 2004, 2012, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/reprintreceipt/GiftReceiptCheckEligibilityAisle.java /main/15 2012/10/29 12:55:22 jswan Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    jswan     10/25/12 - Modified use method on SaleReturnLineItemIfc rather
 *                         than calcualtating the value in the site.
 *                         OrderLineItem has its own implementation of this
 *                         method.
 *    cgreene   12/05/11 - updated from deprecated packages and used more
 *                         bigdecimal constants
 *    cgreene   05/26/10 - convert to oracle packaging
 *    cgreene   04/26/10 - XbranchMerge cgreene_tech43 from
 *                         st_rgbustores_techissueseatel_generic_branch
 *    cgreene   04/02/10 - remove deprecated LocaleContantsIfc and currencies
 *    abondala  01/03/10 - update header date
 *    arathore  02/20/09 - Updated to display Reprint options screen when items
 *                         are Ineligible for gift receipt.
 *
 * ===========================================================================
 * $Log:
 *5    360Commerce 1.4         1/11/2008 5:03:04 AM   Subhasis Gorai  File
 *     edited for fixing the CR#28255 :SHOULD NOT ALLOW TO REPRINT GIFT
 *     RECEIPT FOR RETURN OR EXCH TRANS
 *4    360Commerce 1.3         4/30/2007 4:56:45 PM   Alan N. Sinton  CR 26484
 *     - Merge from v12.0_temp.
 *3    360Commerce 1.2         3/31/2005 4:28:18 PM   Robert Pearse   
 *2    360Commerce 1.1         3/10/2005 10:21:58 AM  Robert Pearse   
 *1    360Commerce 1.0         2/11/2005 12:11:16 PM  Robert Pearse   
 *
 Revision 1.3  2004/04/27 22:25:53  dcobb
 @scr 4452 Feature Enhancement: Printing
 Code review updates.
 *
 Revision 1.2  2004/04/26 21:55:02  dcobb
 @scr 4452 Feature Enhancement: Printing
 Add Reprint Select flow.
 *
 Revision 1.1  2004/04/26 19:51:14  dcobb
 @scr 4452 Feature Enhancement: Printing
 Add Reprint Select flow.
 *
 * ===========================================================================
 */
package oracle.retail.stores.pos.services.reprintreceipt;

import java.math.BigDecimal;

import oracle.retail.stores.common.utility.BigDecimalConstants;
import oracle.retail.stores.common.utility.LocaleMap;
import oracle.retail.stores.domain.utility.LocaleConstantsIfc;
import oracle.retail.stores.domain.lineitem.SaleReturnLineItemIfc;
import oracle.retail.stores.domain.transaction.SaleReturnTransactionIfc;
import oracle.retail.stores.foundation.manager.ifc.UIManagerIfc;
import oracle.retail.stores.foundation.tour.application.Letter;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.pos.services.PosLaneActionAdapter;
import oracle.retail.stores.pos.ui.DialogScreensIfc;
import oracle.retail.stores.pos.ui.POSUIManagerIfc;
import oracle.retail.stores.pos.ui.beans.DialogBeanModel;
import oracle.retail.stores.pos.ui.beans.LineItemsModel;

/**
 * This class retrieves the selection from the UIModel and saves the selection
 * list in the cargo. Checks to see if gift receipt is being applied to a damage
 * discounted item.
 * 
 * @version $Revision: /main/15 $
 */
public class GiftReceiptCheckEligibilityAisle extends PosLaneActionAdapter
{
    private static final long serialVersionUID = 4392893979377119313L;
    /** revision number for this class */
    public static final String revisionNumber = "$Revision: /main/15 $";

    /**
     * This method retrieves the selected rows from the UIModel and saves the
     * selection list in the cargo. Each item selected is checked to see if gift
     * receipt is being applied to a damage discounted item. If so, an error
     * dialog is displayed. The GiftReceiptEligible letter is mailed.
     * 
     * @param bus the bus traversing this lane
     */
    public void traverse(BusIfc bus)
    {
        ReprintReceiptCargo cargo = (ReprintReceiptCargo) bus.getCargo();

        // Retrieve the selection from the UIModel.
        // Get the indexes of the selected items
        POSUIManagerIfc ui = (POSUIManagerIfc) bus.getManager(UIManagerIfc.TYPE);
        LineItemsModel beanModel = (LineItemsModel) ui.getModel(getScreenID());

        // Save the indexes of the selected items to the cargo
        cargo.setSelectedIndexes(beanModel.getSelectedRows());

        // Check each item selected for a damage discount
        SaleReturnTransactionIfc transaction = (SaleReturnTransactionIfc) cargo.getTenderableTransaction();
        SaleReturnLineItemIfc[] lineItems = (SaleReturnLineItemIfc[]) transaction.getLineItems();
        int index[] = cargo.getSelectedIndexes();

        boolean showDialogScreen = false;
        String[] args = { "", "", "" };
        // Retrieve descriptions of ineligible gift receipt items to show on
        // error screen
        int filledSlots = 0;
        int itemCount = 0;
        for (int i = 0; i < index.length; i++)
        {
            int selectedIndex = index[i];
            SaleReturnLineItemIfc srli = lineItems[selectedIndex];
            BigDecimal numberReturnable = srli.getQuantityReturnable();
            if (lineItems[selectedIndex].hasDamageDiscount() || numberReturnable.compareTo(BigDecimalConstants.ZERO_AMOUNT) <= 0)
            {
                itemCount++;
                if (filledSlots < 3)
                {
                    args[filledSlots] = lineItems[selectedIndex].getItemDescription(LocaleMap
                            .getLocale(LocaleConstantsIfc.USER_INTERFACE));
                    filledSlots++;
                    showDialogScreen = true;
                }
                else
                {
                    break;
                }
            }
        }

        if (showDialogScreen)
        {
            // Display error message
            DialogBeanModel dialogModel = new DialogBeanModel();
            dialogModel.setType(DialogScreensIfc.ERROR);
            dialogModel.setButtonLetter(0, "GiftReceiptIneligible");

            if (itemCount < index.length)
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

    /**
     * Returns REPRINT_SELECT screen ID.
     * 
     * @return
     */
    protected String getScreenID()
    {
        return POSUIManagerIfc.REPRINT_SELECT;
    }
}
