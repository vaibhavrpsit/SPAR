/* ===========================================================================
* Copyright (c) 2008, 2012, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/modifyitem/DeliveryLaunchShuttle.java /main/8 2012/05/02 14:07:48 jswan Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    jswan     04/13/12 - Modified to support the change in location of the
 *                         pickup and delivery tours.
 *    cgreene   05/27/10 - convert to oracle packaging
 *    cgreene   05/26/10 - convert to oracle packaging
 *    cgreene   04/27/10 - XbranchMerge cgreene_refactor-duplicate-pos-classes
 *                         from st_rgbustores_techissueseatel_generic_branch
 *    abondala  01/03/10 - update header date
 *    aphulamb  11/22/08 - Checking files after code review by Naga
 *    aphulamb  11/13/08 - Check in all the files for Pickup Delivery Order
 *                         functionality
 *    aphulamb  11/13/08 - Delivery launch shuttle
 *
 * ===========================================================================
 */
package oracle.retail.stores.pos.services.modifyitem;

import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.foundation.tour.ifc.ShuttleIfc;
import oracle.retail.stores.foundation.utility.Util;
import oracle.retail.stores.pos.services.common.FinancialCargoShuttle;
import oracle.retail.stores.pos.services.order.createpickup.PickupDeliveryOrderCargo;

public class DeliveryLaunchShuttle extends FinancialCargoShuttle implements ShuttleIfc
{

    // This id is used to tell
    // the compiler not to generate a
    // new serialVersionUID.
    //
    static final long serialVersionUID = -82268067153917575L;

    /**
     * revision number
     */
    public static final String revisionNumber = "$Revision: /main/8 $";

    // Calling service's cargo
    protected ItemCargo itemCargo = null;

    //----------------------------------------------------------------------
    /**
     Loads the item cargo.
     <P>
     @param  bus     Service Bus to copy cargo from.
     **/
    //----------------------------------------------------------------------
    public void load(BusIfc bus)
    {
        // load the financial cargo
        super.load(bus);

        itemCargo = (ItemCargo)bus.getCargo();
    }

    //----------------------------------------------------------------------
    /**
     Transfers the item cargo to the pickup delivery order cargo for the delivery service.
     <P>
     @param  bus     Service Bus to copy cargo to.
     **/
    //----------------------------------------------------------------------
    public void unload(BusIfc bus)
    {
        // unload the financial cargo
        super.unload(bus);

        PickupDeliveryOrderCargo pickupDeliveryOrderCargo = (PickupDeliveryOrderCargo)bus.getCargo();
        pickupDeliveryOrderCargo.setRegister(itemCargo.getRegister());
        pickupDeliveryOrderCargo.setTransactionType(itemCargo.getTransactionType());
        pickupDeliveryOrderCargo.setTransaction(itemCargo.getTransaction());
        pickupDeliveryOrderCargo.setCustomer(itemCargo.getCustomer());
        pickupDeliveryOrderCargo.setLineItems(itemCargo.getItems());
        pickupDeliveryOrderCargo.setItem(itemCargo.getItem());
        pickupDeliveryOrderCargo.setStoreStatus(itemCargo.getStoreStatus());
        pickupDeliveryOrderCargo.setOperator(itemCargo.getOperator());
        pickupDeliveryOrderCargo.setTenderLimits(itemCargo.getTenderLimits());
    }

    //----------------------------------------------------------------------
    /**
     Returns a string representation of this object.
     <P>
     @return String representation of object
     **/
    //----------------------------------------------------------------------
    public String toString()
    {
        return "Class:  InquiryOptionsLaunchShuttle (Revision " + getRevisionNumber() + ")" + hashCode();
    }

    //----------------------------------------------------------------------
    /**
     Returns the revision number of the class.
     <P>
     @return String representation of revision number
     **/
    //----------------------------------------------------------------------
    public String getRevisionNumber()
    {
        return (Util.parseRevisionNumber(revisionNumber));
    }

}
