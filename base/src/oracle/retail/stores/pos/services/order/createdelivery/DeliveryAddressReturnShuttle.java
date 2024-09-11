/* ===========================================================================
* Copyright (c) 2008, 2012, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/order/createdelivery/DeliveryAddressReturnShuttle.java /main/7 2012/05/02 14:07:48 jswan Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    jswan     04/13/12 - Moved to the ‘order’ package during the cross
 *                         channel project to provide better organization for
 *                         the order create process.
 *    cgreene   05/26/10 - convert to oracle packaging
 *    abondala  01/03/10 - update header date
 *    mahising  02/26/09 - Rework for PDO functionality
 *    aphulamb  01/02/09 - fix delivery issues
 *    aphulamb  11/22/08 - Checking files after code review by Naga
 *    aphulamb  11/13/08 - Check in all the files for Pickup Delivery Order
 *                         functionality
 *    aphulamb  11/13/08 - Delivery Address return shuttle
 *
 * ===========================================================================
 */
package oracle.retail.stores.pos.services.order.createdelivery;

import oracle.retail.stores.domain.lineitem.SaleReturnLineItemIfc;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.foundation.tour.ifc.ShuttleIfc;
import oracle.retail.stores.pos.services.order.createpickup.PickupDeliveryOrderCargo;

public class DeliveryAddressReturnShuttle implements ShuttleIfc
{

    // This id is used to tell
    // the compiler not to generate a
    // new serialVersionUID.
    //
    static final long serialVersionUID = -995613087414835708L;

    /**
     * revision number
     */
    public static final String revisionNumber = "$Revision: /main/7 $";

    /**
     send cargo
     **/
    protected PickupDeliveryOrderCargo pickupDeliveryOrderCargo = null;

    //----------------------------------------------------------------------
    /**
     Loads PickupDeliveryOrderCargo cargo from delivery address service. <P>
     @param  bus     Service Bus
     **/
    //----------------------------------------------------------------------
    public void load(BusIfc bus)
    { // begin load()
        pickupDeliveryOrderCargo = (PickupDeliveryOrderCargo)bus.getCargo();
    } // end load()

    //----------------------------------------------------------------------
    /**
     Loads data into modify item delivery service. <P>
     @param  bus  Service Bus
     **/
    //----------------------------------------------------------------------
    public void unload(BusIfc bus)
    { // begin unload()
        PickupDeliveryOrderCargo cargo = (PickupDeliveryOrderCargo)bus.getCargo();
        cargo.setTransaction(pickupDeliveryOrderCargo.getTransaction());
        SaleReturnLineItemIfc[] lineItems = pickupDeliveryOrderCargo.getLineItems();
        cargo.setLineItems(pickupDeliveryOrderCargo.getLineItems());
        cargo.setCustomer(pickupDeliveryOrderCargo.getCustomer());
    } // end unload()

    //----------------------------------------------------------------------
    /**
     Returns a string representation of this object.
     <P>
     @return String representation of object
     **/
    //----------------------------------------------------------------------
    public String toString()
    { // begin toString()
        // result string
        String strResult = new String("Class:  DeliveryAddressReturnShuttle (Revision " + getRevisionNumber() + ") @"
                + hashCode());
        // pass back result
        return (strResult);
    } // end toString()

    //---------------------------------------------------------------------
    /**
     Retrieves the Team Connection revision number. <P>
     @return String representation of revision number
     */
    //---------------------------------------------------------------------
    public String getRevisionNumber()
    {
        return (revisionNumber);
    }

}
