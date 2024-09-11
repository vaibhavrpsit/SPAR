/*===========================================================================
* Copyright (c) 2014, Oracle and/or its affiliates. All rights reserved.
* ===========================================================================
* $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/order/xchannelcreateshipping/SelectStoreShippingOptionSite.java /main/1 2014/06/10 12:04:11 abhinavs Exp $
* ===========================================================================
* NOTES
* <other useful comments, qualifications, etc.>
*
* MODIFIED    (MM/DD/YY)
* abhinavs    06/09/14 - CAE add available date during order create enhancement
*                        phase II
* abhinavs    06/06/14 - Initial Version
* abhinavs    06/06/14 - Initial Version
* abhinavs    06/06/14 - Creation
* ===========================================================================
*/
package oracle.retail.stores.pos.services.order.xchannelcreateshipping;

import java.util.List;

import oracle.retail.stores.domain.DomainGateway;
import oracle.retail.stores.domain.lineitem.SaleReturnLineItemIfc;
import oracle.retail.stores.domain.order.OrderDeliveryDetailIfc;
import oracle.retail.stores.foundation.tour.application.Letter;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.pos.services.PosSiteActionAdapter;
import oracle.retail.stores.pos.services.common.CommonLetterIfc;

/**
 * This site is traveled while displaying the shipping options
 * @since 14.1
 * @author abhinavs
 */
@SuppressWarnings("serial")
public class SelectStoreShippingOptionSite extends PosSiteActionAdapter
{
    /**
     * class name constant
     **/
    public static final String SITENAME = "SelectStoreShippingOptionSite";

    /**
     * 
     * @param bus the bus arriving at this site
     **/
    public void arrive(BusIfc bus)
    {
        XChannelShippingCargo shipCargo = (XChannelShippingCargo)bus.getCargo();

        OrderDeliveryDetailIfc deliveryDetail = shipCargo.getDeliveryDetail();
        if (deliveryDetail == null)
        {
            deliveryDetail = DomainGateway.getFactory().getOrderDeliveryDetailInstance();
        }
        shipCargo.setDeliveryID(shipCargo.getDeliveryID() + 1);
        List<SaleReturnLineItemIfc> lines = shipCargo.getLineItemsForDelivery();
        for (SaleReturnLineItemIfc line : lines)
        {
            line.getOrderItemStatus().setDeliveryDetails(deliveryDetail);
        }
        shipCargo.removeAllLineItemsForDelivery();

        shipCargo.setCurrentOptionIndex(shipCargo.getCurrentOptionIndex() + 1);

        String currentLetterName = bus.getCurrentLetter().getName();
        if (currentLetterName.equals(CommonLetterIfc.DONE))
            bus.mail(new Letter(CommonLetterIfc.DONE), BusIfc.CURRENT);
        else
            bus.mail(new Letter(CommonLetterIfc.NEXT), BusIfc.CURRENT);
    }
}
