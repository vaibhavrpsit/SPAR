/* ===========================================================================
* Copyright (c) 1998, 2013, Oracle and/or its affiliates. All rights reserved.
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/order/fill/CheckAndUpdateOrderAisle.java /main/3 2013/04/16 13:32:29 vtemker Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    vtemker   04/16/13 - Moved constants in OrderLineItemIfc to
 *                         OrderConstantsIfc in common project
 *    sgu       01/14/13 - process pickup or cancel for store order items
 *    sgu       01/08/13 - add support for order picklist
 *    sgu       01/07/13 - add quantity pending
 *    sgu       01/04/13 - add new class
 *    sgu       01/03/13 - rename the class for xc only
 *    sgu       01/03/13 - add back order fill flow
 *    cgreene   05/26/10 - convert to oracle packaging
 *    abondala  01/03/10 - update header date
 *
 * ===========================================================================
 * $Log:
 *    3    360Commerce 1.2         3/31/2005 4:27:52 PM   Robert Pearse
 *    2    360Commerce 1.1         3/10/2005 10:21:12 AM  Robert Pearse
 *    1    360Commerce 1.0         2/11/2005 12:10:44 PM  Robert Pearse
 *
 *   Revision 1.4  2004/03/03 23:15:15  bwf
 *   @scr 0 Fixed CommonLetterIfc deprecations.
 *
 *   Revision 1.3  2004/02/12 16:51:23  mcs
 *   Forcing head revision
 *
 *   Revision 1.2  2004/02/11 21:51:49  rhafernik
 *   @scr 0 Log4J conversion and code cleanup
 *
 *   Revision 1.1.1.1  2004/02/11 01:04:18  cschellenger
 *   updating to pvcs 360store-current
 *
 *
 *
 *    Rev 1.0   Aug 29 2003 16:03:36   CSchellenger
 * Initial revision.
 *
 *    Rev 1.0   Apr 29 2002 15:12:30   msg
 * Initial revision.
 *
 *    Rev 1.0   Mar 18 2002 11:41:18   msg
 * Initial revision.
 *
 *    Rev 1.4   Jan 22 2002 22:12:20   dfh
 * updates for model, getlineitems, clone lineitems
 * Resolution for POS SCR-260: Special Order feature for release 5.0
 *
 *    Rev 1.3   Jan 18 2002 10:50:32   dfh
 * use edit item status screen model
 * Resolution for POS SCR-260: Special Order feature for release 5.0
 *
 *    Rev 1.2   Jan 10 2002 21:47:40   dfh
 * uses fill item status screen
 * Resolution for POS SCR-260: Special Order feature for release 5.0
 *
 *    Rev 1.1   14 Dec 2001 07:58:38   mpm
 * Changed getLineItems() to getOrderLineItems().
 * Resolution for POS SCR-260: Special Order feature for release 5.0
 *
 *    Rev 1.0   Sep 24 2001 13:01:08   MPM
 * Initial revision.
 *
 *    Rev 1.1   Sep 17 2001 13:10:34   msg
 * header update
 * ===========================================================================
 */
package oracle.retail.stores.pos.services.order.fill;

// foundation imports
import java.math.BigDecimal;

import oracle.retail.stores.domain.lineitem.OrderItemStatusIfc;
import oracle.retail.stores.domain.lineitem.OrderLineItemIfc;
import oracle.retail.stores.domain.lineitem.SaleReturnLineItemIfc;
import oracle.retail.stores.domain.lineitem.SplitOrderItemIfc;
import oracle.retail.stores.domain.order.OrderConstantsIfc;
import oracle.retail.stores.domain.order.OrderIfc;
import oracle.retail.stores.foundation.manager.ifc.UIManagerIfc;
import oracle.retail.stores.foundation.tour.application.Letter;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.pos.services.PosLaneActionAdapter;
import oracle.retail.stores.pos.services.common.CommonLetterIfc;
import oracle.retail.stores.pos.services.order.common.OrderCargoIfc;
import oracle.retail.stores.pos.ui.POSUIManagerIfc;
import oracle.retail.stores.pos.ui.beans.ListBeanModel;

//------------------------------------------------------------------------------
/**
    Sets the order status based upon the order line items status.
    <P>
    @version $Revision: /main/3 $
**/
//--------------------------------------------------------------------------

public class CheckAndUpdateOrderAisle extends PosLaneActionAdapter
{
    /**
     *
     */
    private static final long serialVersionUID = 1988045159494362689L;

    /**
       class name constant
    **/
    public static final String LANENAME = "EditItemStatusUpdateAisle";

    /**
       revision number for this class
    **/
    public static final String revisionNumber = "$KW=@(#); $Ver=pos_4.5.0:14; $EKW:";

    //----------------------------------------------------------------------
    /**
       Sets the order status based upon the changed item statuses. Updates the
       order status based upon the combination of order line item statuses.
       <P>
       @param  bus     Service Bus
    **/
    //----------------------------------------------------------------------

    public void traverse(BusIfc bus)
    {
        OrderCargoIfc cargo = (OrderCargoIfc)bus.getCargo();
        POSUIManagerIfc ui = (POSUIManagerIfc)bus.getManager(UIManagerIfc.TYPE);
        ListBeanModel model = (ListBeanModel)ui.getModel(POSUIManagerIfc.EDIT_ITEM_STATUS);

        SaleReturnLineItemIfc previousOrderLineItem = null;
        SaleReturnLineItemIfc orderLineItem = null;

        Object[] uiOli = (Object[])model.getListArray();
        for (int i = 0; i < uiOli.length; i++)
        {
            SplitOrderItemIfc splitOrderItem = (SplitOrderItemIfc)uiOli[i];
            previousOrderLineItem = orderLineItem;
            orderLineItem = splitOrderItem.getOriginalOrderLineItem();
            OrderItemStatusIfc orderItemStatus = orderLineItem.getOrderItemStatus();

            // If done processing the current order line item
            if (orderLineItem != previousOrderLineItem)
            {
            	orderItemStatus.setQuantityNew(BigDecimal.ZERO);
                orderItemStatus.setQuantityPending(BigDecimal.ZERO);
                orderItemStatus.setQuantityPicked(BigDecimal.ZERO);
            }

            int status = splitOrderItem.getStatus().getStatus();
            BigDecimal qty = splitOrderItem.getQuantity();
            switch (status)
            {
            case OrderConstantsIfc.ORDER_ITEM_STATUS_NEW:
                orderItemStatus.setQuantityNew(
                        orderItemStatus.getQuantityNew().add(qty));
                break;
            case OrderConstantsIfc.ORDER_ITEM_STATUS_PENDING:
                orderItemStatus.setQuantityPending(
                        orderItemStatus.getQuantityPending().add(qty));
                break;
            case OrderConstantsIfc.ORDER_ITEM_STATUS_FILLED:
                orderItemStatus.setQuantityPicked(
                        orderItemStatus.getQuantityPicked().add(qty));
                break;
            }
            
        }

        // set order item status
        OrderIfc order = cargo.getOrder();
        for (OrderLineItemIfc lineItem : order.getOrderLineItems())
        {
            lineItem.getOrderItemStatus().setStatusByQuantity();
        }

        // set order status
        order.setOrderStatus();

        bus.mail(new Letter(CommonLetterIfc.SUCCESS), BusIfc.CURRENT);
    }
}
