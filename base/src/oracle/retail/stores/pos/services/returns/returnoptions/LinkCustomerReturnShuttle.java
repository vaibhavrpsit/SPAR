/* ===========================================================================
* Copyright (c) 1998, 2011, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/returns/returnoptions/LinkCustomerReturnShuttle.java /rgbustores_13.4x_generic_branch/1 2011/05/05 14:05:54 mszekely Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    cgreene   05/26/10 - convert to oracle packaging
 *    abondala  01/03/10 - update header date
 *
 * ===========================================================================
 * $Log:
 *    3    360Commerce 1.2         3/31/2005 4:28:51 PM   Robert Pearse   
 *    2    360Commerce 1.1         3/10/2005 10:23:06 AM  Robert Pearse   
 *    1    360Commerce 1.0         2/11/2005 12:12:19 PM  Robert Pearse   
 *
 *   Revision 1.7  2004/09/23 00:07:12  kmcbride
 *   @scr 7211: Adding static serialVersionUIDs to all POS Serializable objects, minus the JComponents
 *
 *   Revision 1.6  2004/07/27 19:45:40  jdeleau
 *   @scr 6305 Flow corrections on return without a receipt for linked customer.
 *
 *   Revision 1.5  2004/06/25 15:32:16  cdb
 *   @scr 4286 Updated flow of Returns with customer required when offline.
 *
 *   Revision 1.4  2004/02/23 14:58:52  baa
 *   @scr 0 cleanup javadocs
 *
 *   Revision 1.3  2004/02/12 16:51:52  mcs
 *   Forcing head revision
 *
 *   Revision 1.2  2004/02/11 21:52:25  rhafernik
 *   @scr 0 Log4J conversion and code cleanup
 *
 *   Revision 1.1.1.1  2004/02/11 01:04:20  cschellenger
 *   updating to pvcs 360store-current
 *
 *
 * 
 *    Rev 1.0   Aug 29 2003 16:06:18   CSchellenger
 * Initial revision.
 * 
 *    Rev 1.0   Apr 29 2002 15:05:00   msg
 * Initial revision.
 * 
 *    Rev 1.0   Mar 18 2002 11:46:22   msg
 * Initial revision.
 * 
 *    Rev 1.0   Sep 21 2001 11:25:26   msg
 * Initial revision.
 * 
 *    Rev 1.1   Sep 17 2001 13:12:50   msg
 * header update
 * ===========================================================================
 */
package oracle.retail.stores.pos.services.returns.returnoptions;

import org.apache.log4j.Logger;

import oracle.retail.stores.domain.customer.CustomerIfc;
import oracle.retail.stores.domain.transaction.SearchCriteriaIfc;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.foundation.tour.ifc.ShuttleIfc;
import oracle.retail.stores.pos.services.customer.main.CustomerMainCargo;

//--------------------------------------------------------------------------
/**
    This shuttle updates the parent cargo with information from the child cargo.
    <p>
    @version $Revision: /rgbustores_13.4x_generic_branch/1 $
**/
//--------------------------------------------------------------------------
public class LinkCustomerReturnShuttle implements ShuttleIfc
{
    // This id is used to tell
    // the compiler not to generate a
    // new serialVersionUID.
    //
    static final long serialVersionUID = 1565077600654189222L;

    /** 
        The logger to which log messages will be sent.
    **/
    protected static Logger logger = Logger.getLogger(oracle.retail.stores.pos.services.returns.returnoptions.LinkCustomerReturnShuttle.class);

    /**
       revision number
    **/
    public static String revisionNumber = "$Revision: /rgbustores_13.4x_generic_branch/1 $";
    /**
       CustomerIfc customer
    **/
    CustomerIfc customer = null;

    //----------------------------------------------------------------------
    /**
       Copies information needed from child service.
       <P>
       @param  bus    Child Service Bus to copy cargo from.
    **/
    //----------------------------------------------------------------------
    public void load(BusIfc bus)
    {

        // retrieve cargo from the child service
        CustomerMainCargo cmCargo = (CustomerMainCargo)bus.getCargo();

        // get the customer
        customer = cmCargo.getCustomer();

    }

    //----------------------------------------------------------------------
    /**
       Stores information needed by parent service.
       <P>
       @param  bus     Parent Service Bus to copy cargo to.
    **/
    //----------------------------------------------------------------------
    public void unload(BusIfc bus)
    {

        // retrieve cargo from the parent
        ReturnOptionsCargo cargo = (ReturnOptionsCargo)bus.getCargo();
        SearchCriteriaIfc searchCriteria = cargo.getSearchCriteria();

        if ((searchCriteria != null) && (customer != null))
        {
            searchCriteria.setCustomer(customer);
        }
        // set the customer in the options cargo
        cargo.setCustomer(customer);
        // cargo.setCustomerMustLink(false);
    }

    //----------------------------------------------------------------------
    /**
       Returns a string representation of this object.
       <P>
       @return String representation of object
    **/
    //----------------------------------------------------------------------
    public String toString()
    {                                   // begin toString()
        // result string
        String strResult = new String("Class:  LinkCustomerReturnShuttle (Revision " +
                                      getRevisionNumber() +
                                      ")" + hashCode());

        // pass back result
        return(strResult);
    }                                   // end toString()

    //----------------------------------------------------------------------
    /**
       Returns the revision number of the class.
       <P>
       @return String representation of revision number
    **/
    //----------------------------------------------------------------------
    public String getRevisionNumber()
    {                                   // begin getRevisionNumber()
        // return string
        return(revisionNumber);
    }                                   // end getRevisionNumber()
}
