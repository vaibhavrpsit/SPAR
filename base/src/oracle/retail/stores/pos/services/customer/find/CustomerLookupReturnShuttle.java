/* ===========================================================================
* Copyright (c) 1998, 2011, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/customer/find/CustomerLookupReturnShuttle.java /rgbustores_13.4x_generic_branch/1 2011/05/05 14:06:27 mszekely Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    cgreene   05/26/10 - convert to oracle packaging
 *    abondala  01/03/10 - update header date
 *    mahising  11/19/08 - Updated for review comments
 *    mahising  11/13/08 - Added for Customer module for both ORPOS and ORCO
 *
 * ===========================================================================
 * $Log:
 *    3    360Commerce 1.2         3/31/2005 4:27:37 PM   Robert Pearse
 *    2    360Commerce 1.1         3/10/2005 10:20:40 AM  Robert Pearse
 *    1    360Commerce 1.0         2/11/2005 12:10:23 PM  Robert Pearse
 *
 *   Revision 1.5  2004/09/23 00:07:15  kmcbride
 *   @scr 7211: Adding static serialVersionUIDs to all POS Serializable objects, minus the JComponents
 *
 *   Revision 1.4  2004/04/09 16:56:02  cdb
 *   @scr 4302 Removed double semicolon warnings.
 *
 *   Revision 1.3  2004/02/12 16:49:27  mcs
 *   Forcing head revision
 *
 *   Revision 1.2  2004/02/11 21:41:51  rhafernik
 *   @scr 0 Log4J conversion and code cleanup
 *
 *   Revision 1.1.1.1  2004/02/11 01:04:15  cschellenger
 *   updating to pvcs 360store-current
 *
 *
 *
 *    Rev 1.0   Aug 29 2003 15:55:42   CSchellenger
 * Initial revision.
 *
 *    Rev 1.2   May 27 2003 12:28:48   baa
 * rework offline flow for customer
 * Resolution for 2455: Layaway Customer screen, blank customer name is accepted
 *
 *    Rev 1.1   May 09 2003 12:50:48   baa
 * more fixes to business customer
 * Resolution for POS SCR-2366: Busn Customer - Tax Exempt- Does not display Tax Cert #
 *
 *    Rev 1.0   Apr 29 2002 15:33:00   msg
 * Initial revision.
 *
 *    Rev 1.1   Mar 18 2002 23:12:24   msg
 * - updated copyright
 *
 *    Rev 1.0   Mar 18 2002 11:25:08   msg
 * Initial revision.
 *
 *    Rev 1.3   10 Dec 2001 15:56:04   baa
 * Fix minor defects in customer
 * Resolution for POS SCR-99: Data on Customer Delete screen should be non-editable
 *
 *    Rev 1.2   16 Nov 2001 10:33:02   baa
 * Cleanup code & implement new security model on customer
 * Resolution for POS SCR-209: Customer History
 * Resolution for POS SCR-263: Apply new security model to Customer Service
 *
 *    Rev 1.1   23 Oct 2001 16:53:22   baa
 * updates for customer history and for getting rid of CustomerMasterCargo.
 * Resolution for POS SCR-209: Customer History
 *
 *    Rev 1.0   Sep 21 2001 11:15:32   msg
 * Initial revision.
 *
 *    Rev 1.1   Sep 17 2001 13:07:02   msg
 * header update
 * ===========================================================================
 */
package oracle.retail.stores.pos.services.customer.find;
// foundation imports
import org.apache.log4j.Logger;

import oracle.retail.stores.domain.DomainGateway;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.foundation.tour.ifc.ShuttleIfc;
import oracle.retail.stores.pos.services.customer.common.CustomerCargo;



//--------------------------------------------------------------------------
/**
    Transfer data from the CustomerLookup service back
    to the CustomerFind service.
    $Revision: /rgbustores_13.4x_generic_branch/1 $
**/
//--------------------------------------------------------------------------
public class CustomerLookupReturnShuttle implements ShuttleIfc

{
    // This id is used to tell
    // the compiler not to generate a
    // new serialVersionUID.
    //
    static final long serialVersionUID = 3758127790741765896L;

    /**
        The logger to which log messages will be sent.
    **/
    protected static Logger logger = Logger.getLogger(oracle.retail.stores.pos.services.customer.find.CustomerLookupReturnShuttle.class);

    /**
       revision number
    **/
    public static final String revisionNumber = "$Revision: /rgbustores_13.4x_generic_branch/1 $";

    protected CustomerCargo customerCargo = null;

    //----------------------------------------------------------------------
    /**
       Transfers data from one customer service to another.
       <P>
       @param  bus     Service Bus
    **/
    //----------------------------------------------------------------------
    public void load(BusIfc bus)
    {
          customerCargo = (CustomerCargo)bus.getCargo();
    }
    //----------------------------------------------------------------------
    /**
       Transfers data from one customer service to another.
       <P>
       @param  bus     Service Bus
    **/
    //----------------------------------------------------------------------
    public void unload(BusIfc bus)
    {

        CustomerCargo cargo = (CustomerCargo)bus.getCargo();
        if ( customerCargo.getCustomer() == null)
        {
              if (customerCargo.isLink())
              {
                  cargo.setCustomer(DomainGateway.getFactory().getCustomerInstance());
                  cargo.getCustomer().setCustomerID(customerCargo.getCustomerID());
                  cargo.getCustomer().setCustomerName(customerCargo.getCustomerID());
              }
        }

        if (customerCargo.getCustomer() != null)
        {   // set the Customer reference in the cargo
            cargo.setCustomer(customerCargo.getCustomer());
            cargo.setOfflineIndicator(customerCargo.getOfflineIndicator());
        }
        if (customerCargo.getOriginalCustomer() !=null)
        {
           cargo.setOriginalCustomer(customerCargo.getOriginalCustomer());
        }
        cargo.setCustomerLink(customerCargo.isCustomerLink());
        cargo.setLink(customerCargo.isLink());
        cargo.setNewCustomer(customerCargo.isNewCustomer());
        // Need to know if db is offline
        cargo.setDataExceptionErrorCode(customerCargo.getDataExceptionErrorCode());
        // setting boolean variable to track if its customer search flow
        cargo.setCustomerSearchSpec(true);
    }
     //----------------------------------------------------------------------
    /**
       Returns a string representation of this object.
       @return String representation of object
    **/
    //----------------------------------------------------------------------
    public String toString()
    {
        String strResult = new String("Class: " +  getClass().getName() + " (Revision " +
                                      getRevisionNumber() +
                                      ")" + hashCode());
        return(strResult);
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
