/* ===========================================================================
* Copyright (c) 2008, 2012, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/order/createpickup/EnterCustomerInformationSite.java /main/8 2012/05/02 14:07:47 jswan Exp $
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
 *    mahising  03/06/09 - fixed issue for suspended transaction
 *    aphulamb  01/02/09 - fix delivery issues
 *    aphulamb  12/23/08 - Mock padding fix and PDO flow related changes for
 *                         buttons enable/disable
 *    aphulamb  11/22/08 - Checking files after code review by Naga
 *    aphulamb  11/18/08 - Pickup Delivery Order
 *    aphulamb  11/17/08 - Pickup Delivery order
 *    aphulamb  11/13/08 - Check in all the files for Pickup Delivery Order
 *                         functionality
 *    aphulamb  11/13/08 - enter customer info
 *
 * ===========================================================================
 */
package oracle.retail.stores.pos.services.order.createpickup;

import oracle.retail.stores.domain.transaction.SaleReturnTransaction;
import oracle.retail.stores.foundation.tour.application.Letter;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.pos.services.PosSiteActionAdapter;

@SuppressWarnings("serial")
public class EnterCustomerInformationSite extends PosSiteActionAdapter
{

    /**
     * revision number
     */
    public static final String revisionNumber = "$Revision: /main/8 $";

    public static final String CUSTOMER_PRESENT = "CustomerPresent";

    public boolean isActionPickupDElivery;

    // ----------------------------------------------------------------------
    /**
     * set the customer into cargo if customer is linked with transaction.
     * <P>
     *
     * @param bus Service bus.
     */
    // ----------------------------------------------------------------------
    public void arrive(BusIfc bus)
    {
        PickupDeliveryOrderCargo pickupDeliveryOrderCargo = (PickupDeliveryOrderCargo)bus.getCargo();
        SaleReturnTransaction transaction = (SaleReturnTransaction)pickupDeliveryOrderCargo.getTransaction();
        // it is capture customer
        if (transaction.getCustomer() == null)
        {
            // does not have captured customer info

            // Sets isPickupDelivery flag true for Pickup or Delivery
            // items
            pickupDeliveryOrderCargo.setActionPickupDelivery(true);
            bus.mail(new Letter("AddCustomer"), BusIfc.CURRENT);
            return;
        }
        else if (transaction.getCustomer() != null)
        {
            pickupDeliveryOrderCargo.setCustomer(transaction.getCustomer());
            bus.mail(new Letter("AskForDate"), BusIfc.CURRENT);

        }

    }
}
