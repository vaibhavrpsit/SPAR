/* ===========================================================================
* Copyright (c) 2008, 2013, Oracle and/or its affiliates. All rights reserved.
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/order/pickup/ConfirmPickupDeliveryOrderSite.java /main/9 2013/04/16 13:32:31 vtemker Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    vtemker   04/16/13 - Moved constants in OrderLineItemIfc to
 *                         OrderConstantsIfc in common project
 *    abhinavs  03/20/13 - Fix to not display customer signature capture screen
 *                         in case of cancellation of order
 *    cgreene   02/15/11 - move constants into interfaces and refactor
 *    cgreene   05/26/10 - convert to oracle packaging
 *    abondala  01/03/10 - update header date
 *    asinton   06/04/09 - Reverted back to using order type to confirm if
 *                         order is PDO or special order.
 *    mweis     03/19/09 - code review revisions
 *    mweis     03/19/09 - re-entry mode should not prompt for customer
 *                         signature
 *    kulu      02/19/09 - Fix the bug that Signature Capture not appearing for
 *                         Orders that have been picked up
 *    aphulamb  11/22/08 - Checking files after code review by Naga
 *    aphulamb  11/18/08 - Pickup Delivery Order
 *    aphulamb  11/13/08 - Check in all the files for Pickup Delivery Order
 *                         functionality
 *    aphulamb  11/13/08 - Confirm pickup delivery order
 *
 * ===========================================================================
 */
package oracle.retail.stores.pos.services.order.pickup;

import oracle.retail.stores.domain.lineitem.OrderLineItemIfc;
import oracle.retail.stores.domain.order.OrderConstantsIfc;
import oracle.retail.stores.domain.order.OrderIfc;
import oracle.retail.stores.foundation.tour.application.Letter;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.pos.services.PosSiteActionAdapter;
import oracle.retail.stores.pos.services.common.CommonLetterIfc;

public class ConfirmPickupDeliveryOrderSite extends PosSiteActionAdapter
{
    /**
     * for any serialized versions of this object 
     */
    private static final long serialVersionUID = -2398665333541333459L;
    
    /**
     * revision number
     */
    public static final String revisionNumber = "$Revision: /main/9 $";

    /**
     * If order type is pickup delivery order then go for signature capture.
     *
     * @param bus the bus arriving at this site
     */
    @Override
    public void arrive(BusIfc bus)
    {
    	PickupOrderCargo cargo = (PickupOrderCargo)bus.getCargo();       
    	OrderIfc order = cargo.getOrder();
    	boolean reentryMode = cargo.getTransaction().isReentryMode();
    	OrderLineItemIfc[] orderitem=cargo.getTransaction().getOrderLineItems();
    	boolean isNoPickUp=true;
    	for(OrderLineItemIfc ofc:orderitem)
    	{
    		if(ofc.getItemStatus() == OrderConstantsIfc.ORDER_ITEM_STATUS_PICK_UP)
    		{
    			isNoPickUp=false;
    		}
    	}

    	/*
    	 * Send "Print" if order type is not ORDER_TYPE_ON_HAND or transaction is re-entry mode or item status is not pickup
    	 * else send "PickupDeliveryOrder"
    	 */

    	Letter letter = new Letter("PickupDeliveryOrder");
    	if (order.getOrderType() != OrderConstantsIfc.ORDER_TYPE_ON_HAND || reentryMode == true || isNoPickUp)
    	{
    		letter = new Letter(CommonLetterIfc.PRINT);
    	}
    	bus.mail(letter, BusIfc.CURRENT);
    }

}
