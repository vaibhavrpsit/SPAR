/* ===========================================================================
* Copyright (c) 1998, 2011, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/inquiry/iteminquiry/AddItemSite.java /rgbustores_13.4x_generic_branch/1 2011/05/05 14:05:44 mszekely Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    jswan     11/01/10 - Fixed issues with UNDO and CANCEL letters; this
 *                         includes properly canceling transactions when a user
 *                         presses the cancel button in the item inquiry and
 *                         item inquiry sub tours.
 *    cgreene   05/26/10 - convert to oracle packaging
 *    mkutiana  02/09/10 - Adding conditions to make sure that the order/trans
 *                         is of specific type before blocking item
 *    abondala  01/03/10 - update header date
 *    mchellap  03/11/09 - Added condition to check whether the item is
 *                         authorized for sale
 *
 * ===========================================================================
 * $Log:
 *    3    360Commerce 1.2         3/31/2005 4:27:09 PM   Robert Pearse
 *    2    360Commerce 1.1         3/10/2005 10:19:31 AM  Robert Pearse
 *    1    360Commerce 1.0         2/11/2005 12:09:24 PM  Robert Pearse
 *
 *   Revision 1.4  2004/03/03 23:15:10  bwf
 *   @scr 0 Fixed CommonLetterIfc deprecations.
 *
 *   Revision 1.3  2004/02/12 16:50:30  mcs
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
 *    Rev 1.0   Aug 29 2003 16:00:02   CSchellenger
 * Initial revision.
 *
 *    Rev 1.0   Apr 29 2002 15:22:06   msg
 * Initial revision.
 *
 *    Rev 1.0   Mar 18 2002 11:33:38   msg
 * Initial revision.
 *
 *    Rev 1.1   Dec 11 2001 20:49:48   dfh
 * checks for special order able, displays special order item error
 * screen if not special order able
 * Resolution for POS SCR-260: Special Order feature for release 5.0
 *
 *    Rev 1.0   Sep 21 2001 11:30:00   msg
 * Initial revision.
 *
 *    Rev 1.1   Sep 17 2001 13:08:20   msg
 * header update
 * ===========================================================================
 */
package oracle.retail.stores.pos.services.inquiry.iteminquiry;
// foundation imports
import oracle.retail.stores.domain.order.OrderConstantsIfc;
import oracle.retail.stores.pos.services.common.CommonLetterIfc;
import oracle.retail.stores.domain.stock.GiftCardPLUItemIfc;
import oracle.retail.stores.domain.stock.ItemClassificationIfc;
import oracle.retail.stores.domain.stock.ItemKit;
import oracle.retail.stores.domain.stock.KitComponentIfc;
import oracle.retail.stores.domain.stock.PLUItemIfc;
import oracle.retail.stores.domain.transaction.OrderTransaction;
import oracle.retail.stores.domain.transaction.OrderTransactionIfc;
import oracle.retail.stores.domain.transaction.TransactionIfc;
import oracle.retail.stores.foundation.manager.ifc.UIManagerIfc;
import oracle.retail.stores.foundation.tour.application.Letter;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.pos.services.PosSiteActionAdapter;
import oracle.retail.stores.pos.ui.DialogScreensIfc;
import oracle.retail.stores.pos.ui.POSUIManagerIfc;
import oracle.retail.stores.pos.ui.beans.DialogBeanModel;

//--------------------------------------------------------------------------
/**
    This site adds an item to the transaction.
    @version $Revision: /rgbustores_13.4x_generic_branch/1 $
**/
//--------------------------------------------------------------------------
public class AddItemSite extends PosSiteActionAdapter
{
    /**
     * serialVersionUID
     */
    private static final long serialVersionUID = -2470154201090611163L;

    /**
        revision number
    **/
    public static final String revisionNumber = "$Revision: /rgbustores_13.4x_generic_branch/1 $";

    /**
        special order item error screen name
    **/
    public static final String ORDER_TYPE_SPECIAL_RESOURCE_ID = "SpecialOrderItemError";
    /**
        special order item error screen name
    **/
    public static final String NOT_AUTHORISED_FOR_SALE_RESOURCE_ID = "ItemNotAuthForSale";

    //----------------------------------------------------------------------
    /**
        Adds the item to the transaction.
        @param  bus     Service Bus
    **/
    //----------------------------------------------------------------------
    public void arrive(BusIfc bus)
    {
        // letter to be sent
        Letter letter = null;
        // salable item flag
        boolean isAuthorizedForSale = false;

        // Get the item from the cargo
        ItemInquiryCargo cargo = (ItemInquiryCargo) bus.getCargo();
        PLUItemIfc pluItem = cargo.getPLUItem();

        // Ensure the item is actually authorized for sale.
        if (pluItem != null)
        {
            isAuthorizedForSale = isItemAuthorizedForSale(pluItem, bus);
            if (!isAuthorizedForSale)
            {
                // Item not authorized for sale, remove the item from cargo
                cargo.setPLUItem(null);
                cargo.setModifiedFlag(false);
                // Show the error message
                showNotAuthorizedForSaleErrorDialog(bus);
            }
        }
        else
        {
            cargo.setModifiedFlag(false);
            letter = new Letter(CommonLetterIfc.NEXT);
        }

        if(isAuthorizedForSale)
        {
            // Check if special order in progress and if item can be special ordered
            if (!specialOrderError(bus))
            {
                // check if item is going to be added
                if (cargo.getModifiedFlag())
                {
                    if (cargo.getPLUItem() instanceof GiftCardPLUItemIfc)
                    {
                        letter = new Letter(CommonLetterIfc.GIFTCARD);
                    }
                    else
                    {
                        letter = new Letter(CommonLetterIfc.ADD);
                    }
                }
                else
                {
                    letter = new Letter(CommonLetterIfc.NEXT);
                    cargo.setPLUItem(null);
                }
            }
        }
        
        if (letter != null)
        {
            bus.mail(letter, BusIfc.CURRENT);
        }
    }

    /**
     * Tests the item to see if it is authorized for sale.
     * @param pluItem
     * @param bus
     * @return true if the item is authorized for sale.
     */
    private boolean isItemAuthorizedForSale(PLUItemIfc pluItem, BusIfc bus)
    {
        boolean isAuthorizedForSale = true;
        if (pluItem.isKitHeader())
        {
            isAuthorizedForSale = isItemKitAuthForSale((ItemKit) pluItem, bus);
        }
        else
        {
            isAuthorizedForSale = isItemAuthForSale(pluItem, bus);
        }
        return isAuthorizedForSale;
    }

    //----------------------------------------------------------------------
    /**
     *   Check the kit item is authorized for sale <p>
     *   @param itemKit ItemKit
     *   @return boolean true if it is authorized, otherwise return false
     */
    //----------------------------------------------------------------------
    protected boolean isItemKitAuthForSale(ItemKit itemKit, BusIfc bus)
    {
        boolean authorized = true;
        KitComponentIfc kitComponents[] = itemKit.getComponentItems();
        if ( kitComponents != null )
        {
            for ( int i=0; i<kitComponents.length; i++ )
            {
                if ( kitComponents[i].isKitHeader() )
                {
                    if ( !isItemKitAuthForSale((ItemKit)kitComponents[i], bus) )
                    {
                        authorized = false;
                        i = kitComponents.length;
                    }
                }
                else
                {
                    if ( !isItemAuthForSale(kitComponents[i], bus) )
                    {
                        authorized = false;
                        i = kitComponents.length;
                    }
                }
            }
        }
        
        return authorized;
    }

    //----------------------------------------------------------------------
    /**
     *   Check the item is authorized for sale <p>
     *   @param pluItem PLUItemIfc
     *   @return boolean true if it is authorized, otherwise return false
     */
    //----------------------------------------------------------------------
    protected boolean isItemAuthForSale(PLUItemIfc pluItem, BusIfc bus)
    {
        boolean authorized = true;
        ItemClassificationIfc classification = pluItem.getItemClassification();
        if (classification != null && !classification.isAuthorizedForSale() )
        {
            authorized = false;
        }
        
        return authorized;
    }

    //----------------------------------------------------------------------
    /**
     *   Displays error Dialog
     *   @param bus
     */
    //----------------------------------------------------------------------
    private void showNotAuthorizedForSaleErrorDialog(BusIfc bus)
    {
        POSUIManagerIfc ui = (POSUIManagerIfc) bus.getManager(UIManagerIfc.TYPE);

        // Using "generic dialog bean". display the error dialog
        DialogBeanModel model = new DialogBeanModel();

        model.setResourceID(NOT_AUTHORISED_FOR_SALE_RESOURCE_ID);
        model.setType(DialogScreensIfc.ERROR);
        model.setButtonLetter(DialogScreensIfc.BUTTON_OK, CommonLetterIfc.NEXT);
        ui.showScreen(POSUIManagerIfc.DIALOG_TEMPLATE, model);
    }

    /**
     * Check to see if the special order error dialog must be displayed
     * @param cargo
     * @return true if the special order error dialog is displayed
     */
    protected boolean specialOrderError(BusIfc bus)
    {
        boolean specialOrderError = false;
        ItemInquiryCargo cargo = (ItemInquiryCargo)bus.getCargo();
        if(cargo.getTransaction() instanceof OrderTransaction)
        {
            OrderTransactionIfc orderTransaction = (OrderTransactionIfc)cargo.getTransaction();
            if (((cargo.getTransactionType() == TransactionIfc.TYPE_ORDER_INITIATE) && 
                 (orderTransaction.getOrderType() == OrderConstantsIfc.ORDER_TYPE_SPECIAL)) && 
                 (cargo.getPLUItem().isSpecialOrderEligible() == false))
            {
                specialOrderError = true;
                
                cargo.setModifiedFlag(false);
        
                POSUIManagerIfc ui = (POSUIManagerIfc) bus.getManager(UIManagerIfc.TYPE);
        
                // Using "generic dialog bean". display the error dialog
                DialogBeanModel model = new DialogBeanModel();
        
                // Set model to same name as dialog
                // Set button and arguments
                model.setResourceID(ORDER_TYPE_SPECIAL_RESOURCE_ID);
                model.setType(DialogScreensIfc.ACKNOWLEDGEMENT);
                model.setButtonLetter(DialogScreensIfc.BUTTON_OK, CommonLetterIfc.NEXT);
        
                // set and display the model
                ui.showScreen(POSUIManagerIfc.DIALOG_TEMPLATE, model);
            }
        }        
        return specialOrderError;
    }
}
