/* ===========================================================================
* Copyright (c) 2012, 2013, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/sale/CreatePickupShipDeliverOrderLaunchShuttle.java /main/3 2013/03/05 14:03:16 yiqzhao Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    yiqzhao   03/01/13 - Set the first item to lookup.
 *    yiqzhao   02/26/13 - Check for order item after adding related item(s).
 *    yiqzhao   09/04/12 - set presplitLineNumber for kit components.
 *    jswan     05/14/12 - Added to support the Ship button functionality.
 * ===========================================================================
 */
package oracle.retail.stores.pos.services.sale;


import oracle.retail.stores.domain.customer.CustomerIfc;
import oracle.retail.stores.domain.lineitem.AbstractTransactionLineItemIfc;
import oracle.retail.stores.domain.lineitem.KitHeaderLineItemIfc;
import oracle.retail.stores.domain.lineitem.SaleReturnLineItemIfc;
import oracle.retail.stores.domain.transaction.RetailTransactionIfc;
import oracle.retail.stores.domain.transaction.SaleReturnTransactionIfc;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.foundation.tour.ifc.ShuttleIfc;
import oracle.retail.stores.foundation.utility.Util;
import oracle.retail.stores.pos.services.order.createpickup.PickupDeliveryOrderCargo;

public class CreatePickupShipDeliverOrderLaunchShuttle implements ShuttleIfc
{

    /** serialVersionUID */
    private static final long serialVersionUID = -1176922261423110277L;

    /** revision number */
    public static final String revisionNumber = "$Revision: /main/3 $";
    // Calling Item cargo
    protected SaleCargo saleCargo = null;

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
        saleCargo = (SaleCargo)bus.getCargo();
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
        PickupDeliveryOrderCargo pickupDeliveryOrderCargo = (PickupDeliveryOrderCargo)bus.getCargo();
        int size = saleCargo.getOrderLineItems().size();
        SaleReturnLineItemIfc[] saleReturnLineItems = new SaleReturnLineItemIfc[size];
        for (int i= 0; i<size; i++)
        {
            saleReturnLineItems[i]= saleCargo.getOrderLineItems().get(i);
        }
        
        //classCastException here
        pickupDeliveryOrderCargo.setLineItems(saleReturnLineItems);
        if ( saleCargo.getOrderLineItems().size() > 0 )
        {
            pickupDeliveryOrderCargo.setItem(saleCargo.getOrderLineItems().get(0));
        }

        // Set up the rest of the data for the launch 
        pickupDeliveryOrderCargo.setTransaction((RetailTransactionIfc)saleCargo.getTransaction().clone());
        pickupDeliveryOrderCargo.setRegister(saleCargo.getRegister());
        pickupDeliveryOrderCargo.setTransactionType(saleCargo.getTransaction().getTransactionType());
        pickupDeliveryOrderCargo.setStoreStatus(saleCargo.getStoreStatus());
        pickupDeliveryOrderCargo.setOperator(saleCargo.getOperator());
        pickupDeliveryOrderCargo.setTenderLimits(saleCargo.getTenderLimits());
        if (saleCargo.getTransaction().getCustomer() != null)
        {
            pickupDeliveryOrderCargo.setCustomer((CustomerIfc)saleCargo.getTransaction().getCustomer().clone());
        }

        // Set up line items so that if a line item split occurs, the tour
        // can find all the items that need to be updated.
        if (pickupDeliveryOrderCargo.getTransaction() instanceof SaleReturnTransactionIfc)
        {
            SaleReturnTransactionIfc trans = (SaleReturnTransactionIfc)pickupDeliveryOrderCargo.getTransaction();
            AbstractTransactionLineItemIfc[] lineItems = trans.getItemContainerProxy().getLineItems();
            for (AbstractTransactionLineItemIfc lineItem:lineItems)
            {
                if (lineItem instanceof SaleReturnLineItemIfc)
                {
                    SaleReturnLineItemIfc saleReturnLineItem = (SaleReturnLineItemIfc)lineItem;
                    saleReturnLineItem.setPreSplitLineNumber(saleReturnLineItem.getLineNumber());
                    if ( saleReturnLineItem.isKitHeader() )
                    {
                        for (SaleReturnLineItemIfc compItem: ((KitHeaderLineItemIfc)lineItem).getKitComponentLineItemArray())
                        {
                        	compItem.setPreSplitLineNumber(compItem.getLineNumber());
                        }
                    }
                }
            }
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
