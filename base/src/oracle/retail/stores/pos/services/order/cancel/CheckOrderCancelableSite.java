/* ===========================================================================
* Copyright (c) 1998, 2013, Oracle and/or its affiliates. All rights reserved.
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/order/cancel/CheckOrderCancelableSite.java /main/15 2013/04/16 13:32:30 vtemker Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    vtemker   04/16/13 - Moved constants in OrderLineItemIfc to
 *                         OrderConstantsIfc in common project
 *    sgu       01/08/13 - add support for order picklist
 *    sgu       11/26/12 - determine if an order item can be picked up from
 *                         this store
 *    sgu       10/30/12 - refactor sites to check order status for pickup and
 *                         cancel
 *    sgu       10/29/12 - check cancel order status
 *    cgreene   05/26/10 - convert to oracle packaging
 *    cgreene   04/28/10 - updating deprecated names
 *
 * ===========================================================================
 * $Log:
 *    3    360Commerce 1.2         3/31/2005 4:27:26 PM   Robert Pearse
 *    2    360Commerce 1.1         3/10/2005 10:20:13 AM  Robert Pearse
 *    1    360Commerce 1.0         2/11/2005 12:09:59 PM  Robert Pearse
 *
 *   Revision 1.4  2004/03/03 23:15:12  bwf
 *   @scr 0 Fixed CommonLetterIfc deprecations.
 *
 *   Revision 1.3  2004/02/12 16:51:21  mcs
 *   Forcing head revision
 *
 *   Revision 1.2  2004/02/11 21:51:46  rhafernik
 *   @scr 0 Log4J conversion and code cleanup
 *
 *   Revision 1.1.1.1  2004/02/11 01:04:18  cschellenger
 *   updating to pvcs 360store-current
 *
 *
 *
 *    Rev 1.0   Aug 29 2003 16:03:24   CSchellenger
 * Initial revision.
 *
 *    Rev 1.0   Apr 29 2002 15:13:24   msg
 * Initial revision.
 *
 *    Rev 1.0   Mar 18 2002 11:40:46   msg
 * Initial revision.
 *
 *    Rev 1.2   Mar 10 2002 18:00:34   mpm
 * Externalized text in dialog messages.
 * Resolution for POS SCR-351: Internationalization
 *
 *    Rev 1.1   Jan 25 2002 17:28:56   dfh
 * updates to prevent modifications to canceled, completed, voided orders
 * Resolution for POS SCR-260: Special Order feature for release 5.0
 *
 *
 *    Rev 1.0   Sep 24 2001 13:00:10   MPM
 *
 * Initial revision.
 *
 *
 *    Rev 1.1   Sep 17 2001 13:10:20   msg
 * header update
 * ===========================================================================
 */
package oracle.retail.stores.pos.services.order.cancel;

//foundation imports
import oracle.retail.stores.domain.lineitem.OrderItemStatusIfc;
import oracle.retail.stores.domain.lineitem.OrderLineItemIfc;
import oracle.retail.stores.domain.lineitem.SplitOrderItemIfc;
import oracle.retail.stores.domain.order.OrderConstantsIfc;
import oracle.retail.stores.domain.order.OrderIfc;
import oracle.retail.stores.foundation.manager.ifc.UIManagerIfc;
import oracle.retail.stores.foundation.tour.application.Letter;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.pos.services.PosSiteActionAdapter;
import oracle.retail.stores.pos.services.common.CommonLetterIfc;
import oracle.retail.stores.pos.services.order.common.OrderCargo;
import oracle.retail.stores.pos.ui.DialogScreensIfc;
import oracle.retail.stores.pos.ui.POSUIManagerIfc;
import oracle.retail.stores.pos.ui.beans.DialogBeanModel;

//--------------------------------------------------------------------------
/**
    This site tests the current order status. If the order status can't be
    cancelled then displays the Cannot Cancel error screen, otherwise
    mails a Success letter.
    <p>
    @version $Revision: /main/15 $
 **/
//--------------------------------------------------------------------------
public class CheckOrderCancelableSite extends PosSiteActionAdapter
{
    private static final long serialVersionUID = -384962308385320513L;

    /**
       revision number
     **/
    public static final String revisionNumber = "$Revision: /main/15 $";

    /**
     * Constant for error message screen id.
     */
    public static final String CANNOT_CANCEL_ORDER = "CannotCancelOrder";

    //----------------------------------------------------------------------
    /**
       Determines the current order status. If the order status is Completed,
       Canceled, or VOIDED then displays the Cannot Modify dialog screen,
       otherwise mails a Success letter to continue with cancelling the order.
       <p>
       @param  bus     Service Bus
     **/
    //----------------------------------------------------------------------

    public void arrive(BusIfc bus)
    {
        // get the current order from cargo
        OrderCargo cargo = (OrderCargo)bus.getCargo();
        OrderIfc order = cargo.getOrder();
        String storeID = cargo.getStoreStatus().getStore().getStoreID();

        // get the ui manager
        POSUIManagerIfc ui = (POSUIManagerIfc)bus.getManager(UIManagerIfc.TYPE);

        //recalculate the split order items if the order has been reset
        SplitOrderItemIfc[] splitOrderItems = cargo.getSplitOrderItems();
        if (splitOrderItems == null)
        {
            splitOrderItems = order.getSplitLineItemsByStatus();
            cargo.setSplitOrderItems(splitOrderItems);
        }

        // Check if the order can be completely cancelled from this store
        if (!isAllowedToCancelFromThisStore(splitOrderItems, storeID))
        {
            // setup and display the Cannot Cancel Dialog screen
            DialogBeanModel model = new DialogBeanModel();
            model.setResourceID(CANNOT_CANCEL_ORDER);
            model.setButtonLetter(DialogScreensIfc.BUTTON_OK,CommonLetterIfc.FAILURE);
            model.setType(DialogScreensIfc.ERROR);
            ui.showScreen(POSUIManagerIfc.DIALOG_TEMPLATE, model);
        }
        else
        {
            // mail the Success letter
            bus.mail(new Letter (CommonLetterIfc.SUCCESS),BusIfc.CURRENT);
        }

    }                           // end getRevisionNumber()

    //----------------------------------------------------------------------
    /**
     * Determine if the order can be completely cancelled from this store
     * @param splitOrderItems the split order items
     * @param storeID this store ID
     * @return a boolean flag
     */
    //----------------------------------------------------------------------
    protected boolean isAllowedToCancelFromThisStore(SplitOrderItemIfc[] splitOrderItems, String storeID)
    {
        boolean isAllowedToCancel = true;
        for (SplitOrderItemIfc splitOrderItem : splitOrderItems)
        {
            int status = splitOrderItem.getStatus().getStatus();
            if ((status == OrderConstantsIfc.ORDER_ITEM_STATUS_PICKED_UP) ||
                (status == OrderConstantsIfc.ORDER_ITEM_STATUS_SHIPPED) ||
                (status == OrderConstantsIfc.ORDER_ITEM_STATUS_CANCELED))
            {
                continue;
            }
            else
            {
                OrderItemStatusIfc originalOrderItemStatus = splitOrderItem.getOriginalOrderLineItem().getOrderItemStatus();
                boolean pickupFromThisStore = storeID.equalsIgnoreCase(originalOrderItemStatus.getPickupStoreID());
                if (pickupFromThisStore)
                {
                    // For an xc order item, it can only be cancelled if it is in filled status
                    // For a store order item, it can be cancelled in any status that is not picked up or cancelled already.
                    if ((status == OrderConstantsIfc.ORDER_ITEM_STATUS_FILLED) ||
                        !originalOrderItemStatus.isCrossChannelItem())
                    {
                        continue;
                    }
                    else
                    {
                        isAllowedToCancel = false;
                        break;
                    }
                }
                else
                {
                    // Cannot cancel any order item that is not to be picked up from this store.
                    isAllowedToCancel = false;
                    break;
                }
            }
        }
        return isAllowedToCancel;
    }

} // CheckStatusSite
