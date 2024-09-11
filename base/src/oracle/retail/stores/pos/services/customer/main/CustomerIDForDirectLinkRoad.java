/* ===========================================================================
* Copyright (c) 1998, 2013, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/customer/main/CustomerIDForDirectLinkRoad.java /main/12 2013/06/27 16:39:56 rgour Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    rgour     06/27/13 - adding Manager override for customer Linkup
 *    cgreene   05/26/10 - convert to oracle packaging
 *    abondala  01/03/10 - update header date
 *
 * ===========================================================================
 * $Log:
 *    3    360Commerce 1.2         3/31/2005 4:27:36 PM   Robert Pearse   
 *    2    360Commerce 1.1         3/10/2005 10:20:39 AM  Robert Pearse   
 *    1    360Commerce 1.0         2/11/2005 12:10:22 PM  Robert Pearse   
 *
 *   Revision 1.3  2004/02/12 16:49:33  mcs
 *   Forcing head revision
 *
 *   Revision 1.2  2004/02/11 21:45:00  rhafernik
 *   @scr 0 Log4J conversion and code cleanup
 *
 *   Revision 1.1.1.1  2004/02/11 01:04:15  cschellenger
 *   updating to pvcs 360store-current
 *
 *
 * 
 *    Rev 1.0   Aug 29 2003 15:56:02   CSchellenger
 * Initial revision.
 * 
 *    Rev 1.2   May 27 2003 12:28:48   baa
 * rework offline flow for customer
 * Resolution for 2455: Layaway Customer screen, blank customer name is accepted
 * 
 *    Rev 1.1   May 27 2003 08:48:06   baa
 * rework customer offline flow
 * Resolution for 2387: Deleteing Busn Customer Lock APP- & Inc. Customer.
 * 
 *    Rev 1.0   Apr 29 2002 15:32:00   msg
 * Initial revision.
 * 
 *    Rev 1.1   Mar 18 2002 23:13:02   msg
 * - updated copyright
 * 
 *    Rev 1.0   Mar 18 2002 11:25:46   msg
 * Initial revision.
 * 
 *    Rev 1.0   Sep 21 2001 11:16:04   msg
 * Initial revision.
 * 
 *    Rev 1.1   Sep 17 2001 13:07:16   msg
 * header update
 * ===========================================================================
 */
package oracle.retail.stores.pos.services.customer.main;
// foundation imports
import oracle.retail.stores.domain.employee.RoleFunctionIfc;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.pos.services.customer.common.CustomerCargo;
import oracle.retail.stores.pos.services.customer.common.CustomerIDEnteredRoad;

//--------------------------------------------------------------------------
/**
    Stores customer ID entered by the user.
    @version $Revision: /main/12 $
**/
//--------------------------------------------------------------------------
public class CustomerIDForDirectLinkRoad extends CustomerIDEnteredRoad 
{
    /**
       revision number
    **/
    public static final String revisionNumber = "$Revision: /main/12 $";

    //----------------------------------------------------------------------
    /**
       Stores customer ID entered by the user. <p>
       @param bus the bus traversing this lane
    **/
    //----------------------------------------------------------------------
    public void traverse(BusIfc bus)
    {

       super.traverse(bus);

        // store the customer ID in the cargo
        CustomerCargo cargo = (CustomerCargo) bus.getCargo();
        cargo.setLink(true);
        cargo.setAccessFunctionID(RoleFunctionIfc.CUSTOMER_ADD_FIND);
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
