/* ===========================================================================
* Copyright (c) 2008, 2012, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/modifyitem/PickupLaunchShuttle.java /main/9 2012/09/04 16:12:00 yiqzhao Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    yiqzhao   09/04/12 - Set PreSplitLineNumber for kit components.
 *    jswan     05/14/12 - Modified to fix issue with split of multi-quantity
 *                         line items.
 *    jswan     04/13/12 - Modified to support the change in location of the
 *                         pickup and delivery tours.
 *    cgreene   05/26/10 - convert to oracle packaging
 *    cgreene   04/27/10 - XbranchMerge cgreene_refactor-duplicate-pos-classes
 *                         from st_rgbustores_techissueseatel_generic_branch
 *    abondala  01/03/10 - update header date
 *    aphulamb  11/22/08 - Checking files after code review by Naga
 *    aphulamb  11/13/08 - Check in all the files for Pickup Delivery Order
 *                         functionality
 *    aphulamb  11/13/08 - Pickup Launch shuttle
 *
 * ===========================================================================
 */
package oracle.retail.stores.pos.services.modifyitem;

import oracle.retail.stores.domain.lineitem.AbstractTransactionLineItemIfc;
import oracle.retail.stores.domain.lineitem.KitHeaderLineItemIfc;
import oracle.retail.stores.domain.lineitem.SaleReturnLineItemIfc;
import oracle.retail.stores.domain.transaction.SaleReturnTransactionIfc;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.foundation.tour.ifc.ShuttleIfc;
import oracle.retail.stores.foundation.utility.Util;
import oracle.retail.stores.pos.services.common.FinancialCargoShuttle;
import oracle.retail.stores.pos.services.order.createpickup.PickupDeliveryOrderCargo;

public class PickupLaunchShuttle extends FinancialCargoShuttle implements ShuttleIfc
{

    // This id is used to tell
    // the compiler not to generate a
    // new serialVersionUID.
    //
    static final long serialVersionUID = -82268067153917575L;

    /**
     * revision number
     */
    public static final String revisionNumber = "$Revision: /main/9 $";

    // Calling Item cargo
    protected ItemCargo itemCargo = null;

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
        itemCargo = (ItemCargo)bus.getCargo();
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
        pickupDeliveryOrderCargo.setRegister(itemCargo.getRegister());
        pickupDeliveryOrderCargo.setTransactionType(itemCargo.getTransactionType());
        pickupDeliveryOrderCargo.setTransaction(itemCargo.getTransaction());
        pickupDeliveryOrderCargo.setCustomer(itemCargo.getCustomer());
        pickupDeliveryOrderCargo.setLineItems(itemCargo.getItems());
        pickupDeliveryOrderCargo.setItem(itemCargo.getItem());
        pickupDeliveryOrderCargo.setStoreStatus(itemCargo.getStoreStatus());
        pickupDeliveryOrderCargo.setOperator(itemCargo.getOperator());
        pickupDeliveryOrderCargo.setTenderLimits(itemCargo.getTenderLimits());
        
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
                    SaleReturnLineItemIfc srli = (SaleReturnLineItemIfc)lineItem;
                    srli.setPreSplitLineNumber(srli.getLineNumber());
                    if ( srli.isKitHeader() )
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
