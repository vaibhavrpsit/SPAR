/* ===========================================================================
* Copyright (c) 1998, 2013, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/inquiry/iteminquiry/itemcheck/CheckItemTypeSite.java /main/12 2013/01/10 14:12:45 jswan Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    jswan     01/09/13 - Modified to support Item manager changes.
 *    cgreene   09/02/11 - check for related items when using quickwin multiple
 *                         qty shortcut
 *    cgreene   08/16/11 - clean up and use local variable instead of instance
 *                         member for dialog flag
 *    rrkohli   06/08/11 - Quickwin -Add multqty for below items, system needs
 *                         to display ITEM_NOT_ALLOWED_W_MLTY_QTY dialog
 *    cgreene   05/27/10 - convert to oracle packaging
 *    cgreene   05/26/10 - convert to oracle packaging
 *    abondala  01/03/10 - update header date
 *
 * ===========================================================================
 * $Log:
 *    4    360Commerce 1.3         1/25/2006 4:10:52 PM   Brett J. Larsen merge
 *          7.1.1 changes (aka. 7.0.3 fixes) into 360Commerce view
 *    3    360Commerce 1.2         3/31/2005 4:27:25 PM   Robert Pearse   
 *    2    360Commerce 1.1         3/10/2005 10:20:09 AM  Robert Pearse   
 *    1    360Commerce 1.0         2/11/2005 12:09:56 PM  Robert Pearse   
 *:
 *    4    .v700     1.2.1.0     9/13/2005 15:37:41     Jason L. DeLeau Ifan
 *         id_itm_pos maps to multiple id_itms, let the user choose which one
 *         to use.
 *    3    360Commerce1.2         3/31/2005 15:27:25     Robert Pearse
 *    2    360Commerce1.1         3/10/2005 10:20:09     Robert Pearse
 *    1    360Commerce1.0         2/11/2005 12:09:56     Robert Pearse
 *
 *   Revision 1.4  2004/03/03 23:15:12  bwf
 *   @scr 0 Fixed CommonLetterIfc deprecations.
 *
 *   Revision 1.3  2004/02/12 16:50:36  mcs
 *   Forcing head revision
 *
 *   Revision 1.2  2004/02/11 21:51:11  rhafernik
 *   @scr 0 Log4J conversion and code cleanup
 *
 *   Revision 1.1.1.1  2004/02/11 01:04:16  cschellenger
 *   updating to pvcs 360store-current
 *
 *
 *
 *    Rev 1.0   13 Nov 2003 10:35:06   jriggins
 * Initial revision.
 *
 * ===========================================================================
 */
package oracle.retail.stores.pos.services.inquiry.iteminquiry.itemcheck;

import max.retail.stores.pos.services.inquiry.iteminquiry.MAXItemInquiryCargo;
import oracle.retail.stores.domain.stock.PLUItem;
import oracle.retail.stores.domain.stock.ProductGroupConstantsIfc;
import oracle.retail.stores.foundation.manager.ifc.UIManagerIfc;
import oracle.retail.stores.foundation.tour.application.Letter;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.pos.services.PosSiteActionAdapter;
import oracle.retail.stores.pos.services.common.CommonActionsIfc;
import oracle.retail.stores.pos.services.common.CommonLetterIfc;
import oracle.retail.stores.pos.services.inquiry.iteminquiry.ItemInquiryCargo;
import oracle.retail.stores.pos.ui.DialogScreensIfc;
import oracle.retail.stores.pos.ui.POSUIManagerIfc;
import oracle.retail.stores.pos.ui.beans.DialogBeanModel;

/**
 * This site adds an item to the transaction.
 * 
 * @version $Revision: /main/12 $
 */
@SuppressWarnings("serial")
public class CheckItemTypeSite extends PosSiteActionAdapter implements ProductGroupConstantsIfc
{
    /**
     * revision number
     */
    public static final String revisionNumber = "$Revision: /main/12 $";
    /**
     * alterations letter name constant
     */
    public static final String ALTERATIONS = "Alterations";

    public static final String ITEM_NOT_ALLOWED_W_MLTY_QTY = "ItemNotAllowedWithMultipleQuantity";

    /**
     * Letter for multiple items found
     */
    public static final String LETTER_MULTIPLE_ITEMS_FOUND = "MultipleItemsFound";

    /**
     * Adds the item to the transaction.
     * 
     * @param bus Service Bus
     */
    @Override
    public void arrive(BusIfc bus)
    {
    	System.out.println("CheckItemTypeSite");
        // Get the product group from item cargo
        MAXItemInquiryCargo cargo = (MAXItemInquiryCargo)bus.getCargo();
        POSUIManagerIfc ui = (POSUIManagerIfc)bus.getManager(UIManagerIfc.TYPE);
        System.out.println("MAXItemInquiryCargo 107:"+cargo.getEmpID());
        PLUItem pluItem = (PLUItem) cargo.getPLUItem();
        pluItem.setEmpID(cargo.getEmpID());
        System.out.println("PLUItem 110:"+pluItem.getEmpID());
        String letter = null;
        String itemId = null;
        boolean multipleQuantityAllowed = true;
        if (cargo.getPLUItem() != null)
        {
            String productGroup = cargo.getPLUItem().getProductGroupID();
            boolean isPriceEntryRequired = cargo.getPLUItem().getItemClassification()
                    .isPriceEntryRequired();
            boolean quantityModifiable = cargo.getPLUItem().getItemClassification()
                    .isQuantityModifiable();
            itemId = cargo.getPLUItem().getItemID();

            if (cargo.getItemQuantity().intValue() > 1
                    && (isPriceEntryRequired || !quantityModifiable
                            || cargo.getPLUItem().isItemSizeRequired()
                            || cargo.getPLUItem().hasRelatedItems()))
            {
                cargo.setPLUItem(null);
                multipleQuantityAllowed = false;
            }
            else
            {

                if (productGroup != null && productGroup.equals(PRODUCT_GROUP_GIFT_CARD) && !isPriceEntryRequired)
                {
                    letter = CommonLetterIfc.GIFTCARD;
                }
                else if (productGroup != null && productGroup.equals(PRODUCT_GROUP_ALTERATION))
                {
                    letter = ALTERATIONS;
                }
                else
                {
                    letter = CommonLetterIfc.ADD;
                }
            }
        }
        else
        {
            letter = LETTER_MULTIPLE_ITEMS_FOUND;
        }

        if (!multipleQuantityAllowed)
        {
            DialogBeanModel dialogModel = new DialogBeanModel();
            String[] args = new String[1];
            args[0] = itemId;

            dialogModel.setResourceID(ITEM_NOT_ALLOWED_W_MLTY_QTY);
            dialogModel.setButtonLetter(DialogScreensIfc.BUTTON_OK, CommonActionsIfc.OK);
            dialogModel.setArgs(args);
            dialogModel.setType(DialogScreensIfc.ERROR);
            ui.showScreen(POSUIManagerIfc.DIALOG_TEMPLATE, dialogModel);
        }
        else
        {
            bus.mail(new Letter(letter), BusIfc.CURRENT);
        }
    }
}
