/* ===========================================================================
* Copyright (c) 1998, 2012, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/order/createpickup/JournalCustomerInformationRoad.java /main/6 2012/05/02 14:07:48 jswan Exp $
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
 *    asinton   09/29/09 - Removed unused imports.
 *    asinton   09/29/09 - Forward port of 13.1.x defect where customer info
 *                         not journaled for pickup and delivery orders.
 *    asinton   09/04/09 - Refactored to make use of for(value : interable).
 *    asinton   09/04/09 - Added journaling of customer information for pickup
 *                         or delivery orders.
 *    asinton   09/04/09 - Added journaling of customer information for pickup
 *                         or delivery orders.
 *
 * ===========================================================================
 */
package oracle.retail.stores.pos.services.order.createpickup;

import oracle.retail.stores.domain.customer.CustomerIfc;
import oracle.retail.stores.domain.transaction.RetailTransactionIfc;
import oracle.retail.stores.foundation.manager.ifc.JournalManagerIfc;
import oracle.retail.stores.foundation.tour.application.LaneActionAdapter;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.pos.services.customer.common.CustomerUtilities;

/**
 * This road journals the linked customer's information.
 *
 */
public class JournalCustomerInformationRoad extends LaneActionAdapter
{

    /**
     * serialVersionUID
     */
    private static final long serialVersionUID = 1218735473060030847L;

    /* (non-Javadoc)
     * @see oracle.retail.stores.foundation.tour.application.LaneActionAdapter#traverse(oracle.retail.stores.foundation.tour.ifc.BusIfc)
     */
    @Override
    public void traverse(BusIfc bus)
    {
        PickupDeliveryOrderCargo cargo = (PickupDeliveryOrderCargo)bus.getCargo();
        RetailTransactionIfc transaction = cargo.getTransaction();
        CustomerIfc customer = transaction.getCustomer();
        JournalManagerIfc journalManager = (JournalManagerIfc)bus.getManager(JournalManagerIfc.TYPE);
        CustomerUtilities.journalCustomerInformation(customer, journalManager, transaction);
    }

}
