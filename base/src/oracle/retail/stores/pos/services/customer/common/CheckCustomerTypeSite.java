/* ===========================================================================
* Copyright (c) 1998, 2011, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/customer/common/CheckCustomerTypeSite.java /rgbustores_13.4x_generic_branch/1 2011/05/05 14:06:26 mszekely Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    cgreene   05/26/10 - convert to oracle packaging
 *    abondala  01/03/10 - update header date
 *
 * ===========================================================================
 */
package oracle.retail.stores.pos.services.customer.common;

// foundation imports
import oracle.retail.stores.domain.customer.CustomerIfc;
import oracle.retail.stores.foundation.tour.application.Letter;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.pos.services.PosSiteActionAdapter;

//--------------------------------------------------------------------------
/**
        Check the customer type - business or regular. <P>
**/
//--------------------------------------------------------------------------
public class CheckCustomerTypeSite extends PosSiteActionAdapter
{
    /**
       revision number
    **/
    public static final String revisionNumber = "";

    //----------------------------------------------------------------------
    /**
       Check the customer type - business or regular.
       If its a business customer, mails a BusCustomer letter. 
       Else it mails a regular customer.<P>
       @param bus the bus arriving at this site
    **/
    //----------------------------------------------------------------------
    public void arrive(BusIfc bus)
    {
        String letterName = "RegCustomer";

        CustomerCargo cargo = (CustomerCargo)bus.getCargo();
        CustomerIfc customer = cargo.getCustomer();

        if (customer.isBusinessCustomer())
        {
            letterName = "BusCustomer";
        }

        bus.mail(new Letter(letterName), BusIfc.CURRENT);
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
        return(revisionNumber);
    }
}
