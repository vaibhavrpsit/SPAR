/* ===========================================================================
* Copyright (c) 2012, 2013, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/sale/CreatePickupShipOrderReturnShuttle.java /main/2 2013/03/05 14:03:17 yiqzhao Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    yiqzhao   02/28/13 - Handle orderLineItems for cargos.
 *    jswan     05/14/12 - Added to support the Ship button functionality.
 * ===========================================================================
 */
package oracle.retail.stores.pos.services.sale;

import oracle.retail.stores.domain.lineitem.AbstractTransactionLineItemIfc;
import oracle.retail.stores.domain.lineitem.SaleReturnLineItemIfc;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.foundation.tour.ifc.ShuttleIfc;
import oracle.retail.stores.foundation.utility.Util;
import oracle.retail.stores.pos.services.order.createpickup.PickupDeliveryOrderCargo;

public class CreatePickupShipOrderReturnShuttle implements ShuttleIfc
{

    // This id is used to tell
    // the compiler not to generate a
    // new serialVersionUID.
    //
    static final long serialVersionUID = -82268067153917575L;

    /**
     * revision number
     */
    public static final String revisionNumber = "$Revision: /main/2 $";

    /** Calling Item cargo */
    protected PickupDeliveryOrderCargo orderCargo = null;

    // ----------------------------------------------------------------------
    /**
     * Loads the item cargo.
     * <P>
     *
     * @param bus Service Bus to copy cargo from.
     */
    // ----------------------------------------------------------------------
    public void load(BusIfc bus)
    {
        orderCargo = (PickupDeliveryOrderCargo)bus.getCargo();
    }

    // ----------------------------------------------------------------------
    /**
     * Transfers the item cargo to the pickup delivery order cargo for the
     * modify item service.
     * <P>
     *
     * @param bus Service Bus to copy cargo to.
     */
    // ----------------------------------------------------------------------
    public void unload(BusIfc bus)
    {
        SaleCargo saleCargo = (SaleCargo)bus.getCargo();
        if (orderCargo.getOrderTransaction() != null)
        {
            saleCargo.setTransaction(orderCargo.getOrderTransaction());
        }
        
        // Reset the line items
        AbstractTransactionLineItemIfc[] lineItems = saleCargo.getTransaction().
            getItemContainerProxy().getLineItems();
        for(int i = 0; i < lineItems.length; i++)
        {
            SaleReturnLineItemIfc srli = (SaleReturnLineItemIfc)lineItems[i];
            srli.setSelectedForItemModification(false);
        }
        int size = saleCargo.getOrderLineItems().size();
        for ( int i=0; i<size; i++ )
        {
            saleCargo.getOrderLineItems().remove(0);
        }
    }

    // ----------------------------------------------------------------------
    /**
     * Returns a string representation of this object.
     * <P>
     *
     * @return String representation of object
     */
    // ----------------------------------------------------------------------
    public String toString()
    {
        return "Class:  InquiryOptionsLaunchShuttle (Revision " + getRevisionNumber() + ")" + hashCode();
    }

    // ----------------------------------------------------------------------
    /**
     * Returns the revision number of the class.
     * <P>
     *
     * @return String representation of revision number
     */
    // ----------------------------------------------------------------------
    public String getRevisionNumber()
    {
        return (Util.parseRevisionNumber(revisionNumber));
    }

}
