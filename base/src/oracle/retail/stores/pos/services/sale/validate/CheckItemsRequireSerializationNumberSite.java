/* ===========================================================================
* Copyright (c) 2011, 2013, Oracle and/or its affiliates. All rights reserved.
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/sale/validate/CheckItemsRequireSerializationNumberSite.java /main/5 2013/10/01 15:05:14 yiqzhao Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    yiqzhao   10/01/13 - A kit header does not need to enter serial number.
 *    vtemker   04/16/13 - Moved constants in OrderLineItemIfc to
 *                         OrderConstantsIfc in common project
 *    sgu       07/03/12 - replace item disposition code to use delivery
 *                         instead of ship
 *    jswan     05/14/12 - Modified to support Ship button feature.
 *    asinton   02/13/12 - prompt for serial numbers when entering tender if
 *                         items are missing this data
 *
 * ===========================================================================
 */

package oracle.retail.stores.pos.services.sale.validate;

import oracle.retail.stores.common.utility.Util;
import oracle.retail.stores.domain.lineitem.AbstractTransactionLineItemIfc;
import oracle.retail.stores.domain.lineitem.OrderLineItemIfc;
import oracle.retail.stores.domain.lineitem.SaleReturnLineItemIfc;
import oracle.retail.stores.domain.order.OrderConstantsIfc;
import oracle.retail.stores.domain.transaction.OrderTransactionIfc;
import oracle.retail.stores.domain.transaction.SaleReturnTransactionIfc;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.pos.services.PosSiteActionAdapter;
import oracle.retail.stores.pos.services.common.CommonLetterIfc;
import oracle.retail.stores.pos.services.sale.SaleCargo;

/**
 * This site iterates over the line items to see if any require
 * the serialization number.
 * @author asinton
 * @since 13.4.1
 */
@SuppressWarnings("serial")
public class CheckItemsRequireSerializationNumberSite extends PosSiteActionAdapter
{
    /** constant for Serial letter */
    public static final String SERIAL_LETTER = "Serial";

    /* (non-Javadoc)
     * @see oracle.retail.stores.foundation.tour.application.SiteActionAdapter#arrive(oracle.retail.stores.foundation.tour.ifc.BusIfc)
     */
    @Override
    public void arrive(BusIfc bus)
    {
        String letter = CommonLetterIfc.DONE;
        SaleCargo cargo = (SaleCargo)bus.getCargo();
        SaleReturnTransactionIfc transaction = cargo.getTransaction();
        AbstractTransactionLineItemIfc[] abstractItems = transaction.getLineItems();
        int index = cargo.getSerializedItemIndex();
        boolean itemNotFound = true;
        // get the order type, if this transaction is an order
        int orderType = -1;
        if(transaction instanceof OrderTransactionIfc)
        {
            OrderTransactionIfc orderTransaction = (OrderTransactionIfc)transaction;
            orderType = orderTransaction.getOrderType();
        }
        // iterate through the items to see if one requires a serial number
        for(;index < abstractItems.length && itemNotFound; index++)
        {
            if(abstractItems[index] instanceof SaleReturnLineItemIfc && !abstractItems[index].isKitHeader())
            {
                SaleReturnLineItemIfc srli = (SaleReturnLineItemIfc)abstractItems[index];
                if(srli.isSerializedItem() && Util.isEmpty(srli.getItemSerial()))
                {
                    // check that if the item has an order status, that it is not pickup nor delivery
                    // and that the order type is ORDER_TYPE_ON_HAND
                    if(srli.getOrderItemStatus() != null &&
                            srli.getOrderItemStatus().getItemDispositionCode() != OrderConstantsIfc.ORDER_ITEM_DISPOSITION_PICKUP &&
                            srli.getOrderItemStatus().getItemDispositionCode() != OrderConstantsIfc.ORDER_ITEM_DISPOSITION_DELIVERY &&
                            orderType == OrderConstantsIfc.ORDER_TYPE_ON_HAND)
                    {
                        cargo.setSerializedItemIndex(index);
                        letter = SERIAL_LETTER;
                        itemNotFound = false;
                    }
                }
            }
        }
        bus.mail(letter, BusIfc.CURRENT);
    }

}
