/* ===========================================================================
* Copyright (c) 2008, 2012, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/pickup/CustomerLaunchShuttle.java /main/1 2012/09/12 11:57:17 blarsen Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    cgreene   03/09/12 - add support for journalling queues by current
 *                         register
 *    cgreene   05/26/10 - convert to oracle packaging
 *    abondala  01/03/10 - update header date
 *    asinton   06/01/09 - Set the offline indicator to OFFLINE_ADD instead of
 *                         OFFLINE_LINK as orders require the attached customer
 *                         object.
 *    aphulamb  11/24/08 - Checking files after code review by amrish
 *    aphulamb  11/22/08 - Checking files after code review by Naga
 *    aphulamb  11/18/08 - Pickup Delivery Order
 *    aphulamb  11/18/08 - Customer Launch Shuttle
 *
 * ===========================================================================
 */
package oracle.retail.stores.pos.services.pickup;

import org.apache.log4j.Logger;

import oracle.retail.stores.pos.services.sale.SaleCargoIfc;
import oracle.retail.stores.domain.employee.EmployeeIfc;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.foundation.tour.ifc.ShuttleIfc;
import oracle.retail.stores.pos.services.customer.common.CustomerCargo;
import oracle.retail.stores.pos.services.customer.common.CustomerUtilities;
import oracle.retail.stores.pos.services.customer.main.CustomerMainCargo;

public class CustomerLaunchShuttle implements ShuttleIfc
{

    // This id is used to tell
    // the compiler not to generate a
    // new serialVersionUID.
    //
    static final long serialVersionUID = 3017429145000038504L;

    /**
     The logger to which log messages will be sent.
     **/
    protected static Logger logger = Logger.getLogger(oracle.retail.stores.pos.services.sale.CustomerLaunchShuttle.class);

    /**
     * revision number
     */
    public static final String revisionNumber = "$Revision: /main/1 $";

    protected PickupDeliveryOrderCargo pickupDeliveryOrderCargo = null;

    //----------------------------------------------------------------------
    /**
     ##COMMENT-LOAD##
     <P>
     @param  bus     Service Bus
     **/
    //----------------------------------------------------------------------
    public void load(BusIfc bus)
    {
        pickupDeliveryOrderCargo = (PickupDeliveryOrderCargo)bus.getCargo();
    }

    //----------------------------------------------------------------------
    /**
     ##COMMENT-UNLOAD##
     <P>
     @param  bus     Service Bus
     **/
    //----------------------------------------------------------------------
    public void unload(BusIfc bus)
    {
        CustomerMainCargo cargo = (CustomerMainCargo)bus.getCargo();
        String transactionID = null;
        EmployeeIfc operator = pickupDeliveryOrderCargo.getOperator();

        if (pickupDeliveryOrderCargo.getTransaction() != null)
        {
            transactionID = pickupDeliveryOrderCargo.getTransaction().getTransactionID();
            if (pickupDeliveryOrderCargo.getTransaction().getCustomer() != null)
            {
                cargo.setCustomerLink(true);
                cargo.setOriginalCustomer(pickupDeliveryOrderCargo.getTransaction().getCustomer());
            }
        }
        else
        {
            CustomerUtilities.journalCustomerEnter(bus, operator.getEmployeeID(), transactionID);
        }

        // test comment

        cargo.setRegister(pickupDeliveryOrderCargo.getRegister());
        cargo.setTransactionID(transactionID);
        cargo.setEmployee(operator);
        cargo.setOperator(operator);
        cargo.setOfflineIndicator(CustomerCargo.OFFLINE_ADD);
        cargo.setOfflineExit(false);

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
        String strResult = new String("Class:  getClass().getName() (Revision " + getRevisionNumber() + ")"
                + hashCode());
        return (strResult);
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
        return (revisionNumber);
    }

}
