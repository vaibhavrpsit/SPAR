/* ===========================================================================
* Copyright (c) 1998, 2012, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/customer/add/CheckCustomerIDSite.java /main/1 2012/11/28 17:16:04 mkutiana Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    mkutiana  11/28/12 - Readding the file - was removed in error 
 *                          reqd in businessadd tour
 *    cgreene   05/26/10 - convert to oracle packaging
 *    abondala  01/03/10 - update header date
 *
 * ===========================================================================
 * $Log:
 *    3    360Commerce 1.2         3/31/2005 4:27:24 PM   Robert Pearse   
 *    2    360Commerce 1.1         3/10/2005 10:20:06 AM  Robert Pearse   
 *    1    360Commerce 1.0         2/11/2005 12:09:53 PM  Robert Pearse   
 *
 *   Revision 1.3  2004/02/12 16:49:10  mcs
 *   Forcing head revision
 *
 *   Revision 1.2  2004/02/11 21:41:08  rhafernik
 *   @scr 0 Log4J conversion and code cleanup
 *
 *   Revision 1.1.1.1  2004/02/11 01:04:14  cschellenger
 *   updating to pvcs 360store-current
 *
 *
 * 
 *    Rev 1.0   Aug 29 2003 15:54:58   CSchellenger
 * Initial revision.
 * 
 *    Rev 1.1   May 27 2003 08:47:58   baa
 * rework customer offline flow
 * Resolution for 2387: Deleteing Busn Customer Lock APP- & Inc. Customer.
 * 
 *    Rev 1.0   Apr 29 2002 15:34:10   msg
 * Initial revision.
 * 
 *    Rev 1.1   Mar 18 2002 23:11:00   msg
 * - updated copyright
 * 
 *    Rev 1.0   Mar 18 2002 11:23:44   msg
 * Initial revision.
 * 
 *    Rev 1.4   18 Feb 2002 18:43:36   baa
 * save original customer info
 * Resolution for POS SCR-1242: Selecting 'Enter' on Duplicate ID screen in Customer returns the wrong information
 *
 *    Rev 1.3   08 Jan 2002 09:37:20   baa
 * fix flow
 * Resolution for POS SCR-516: Customer Select screens missing parentheses around area code
 *
 *    Rev 1.2   16 Nov 2001 10:31:44   baa
 * Cleanup code & implement new security model on customer
 * Resolution for POS SCR-263: Apply new security model to Customer Service
 *
 *    Rev 1.1   23 Oct 2001 16:52:14   baa
 * updates for customer history and for getting rid of CustomerMasterCargo.
 * Resolution for POS SCR-209: Customer History
 *
 *    Rev 1.0   Sep 21 2001 11:14:36   msg
 * Initial revision.
 *
 *    Rev 1.1   Sep 17 2001 13:06:40   msg
 * header update
 * ===========================================================================
 */
package oracle.retail.stores.pos.services.customer.add;

// foundation imports
import oracle.retail.stores.domain.customer.CustomerIfc;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.pos.services.customer.common.CustomerCargo;
import oracle.retail.stores.pos.services.customer.common.LookupCustomerIDSite;

//--------------------------------------------------------------------------
/**
        Lookup the customer ID while saving the customer in the cargo.
    @version $Revision: /main/1 $
**/
//--------------------------------------------------------------------------
public class CheckCustomerIDSite extends LookupCustomerIDSite
{
    /**
        revision number
    **/
    public static final String revisionNumber = "$Revision: /main/1 $";

    //----------------------------------------------------------------------
    /**
        Checks for a customer with the given customer ID. <p>
        @param bus the bus arriving at this site
    **/
    //----------------------------------------------------------------------
    public void arrive(BusIfc bus)
    {
        super.arrive(bus);
        CustomerCargo cargo = (CustomerCargo)bus.getCargo();
        CustomerIfc customer = cargo.getCustomer();

        cargo.setCustomer(customer);
    }
}


