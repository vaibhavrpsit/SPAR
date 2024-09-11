/** * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * ************************
 * Copyright (c) 2013 Max Hypermarket.    All Rights Reserved. 
 *  Rev 1.0  3/4/2013               Izhar                                       MAX-POS-Customer-FES_v1.2.doc requirement.
 * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * ***************************/


package max.retail.stores.pos.services.customer.common;

import max.retail.stores.domain.customer.MAXCustomerIfc;
import oracle.retail.stores.domain.customer.CustomerIfc;
import oracle.retail.stores.foundation.tour.application.LaneActionAdapter;
import oracle.retail.stores.foundation.tour.application.Letter;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.pos.services.customer.common.CustomerCargo;

//--------------------------------------------------------------------------
/**
    Query the database for customers based on the search
    criteria entered by the user.
    <p>
    $Revision: 3$
**/
//--------------------------------------------------------------------------
public class MAXLookupTICCustomerAisle extends LaneActionAdapter
{

    /**
       revision number
    **/
    public static final String revisionNumber = "$Revision: 3$";

    //<!-- MAX Rev 1.0 Change : Start -->
    public void traverse(BusIfc bus)
    {
    	 CustomerCargo cargo = (CustomerCargo)bus.getCargo();
    	 CustomerIfc customer = cargo.getCustomer();
    	 ((MAXCustomerIfc) customer).setCustomerType("T");
    	 //((MAXCustomerIfc) customer).setCustomerType("T");
    	 
    	 bus.mail(new Letter("SearchTICCustomer"), BusIfc.CURRENT);
    }
    //<!-- MAX Rev 1.0 Change : End -->
}