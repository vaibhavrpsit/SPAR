/* ===========================================================================
* Copyright (c) 1998, 2011, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/customer/common/RemoveEmployeeIDRoad.java /rgbustores_13.4x_generic_branch/1 2011/05/05 14:06:26 mszekely Exp $
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
 *    3    360Commerce 1.2         3/31/2005 4:29:38 PM   Robert Pearse   
 *    2    360Commerce 1.1         3/10/2005 10:24:40 AM  Robert Pearse   
 *    1    360Commerce 1.0         2/11/2005 12:13:40 PM  Robert Pearse   
 *
 *   Revision 1.3  2004/02/12 16:49:25  mcs
 *   Forcing head revision
 *
 *   Revision 1.2  2004/02/11 21:40:12  rhafernik
 *   @scr 0 Log4J conversion and code cleanup
 *
 *   Revision 1.1.1.1  2004/02/11 01:04:14  cschellenger
 *   updating to pvcs 360store-current
 *
 *
 * 
 *    Rev 1.0   Aug 29 2003 15:55:28   CSchellenger
 * Initial revision.
 * 
 *    Rev 1.1   24 May 2002 18:54:34   vxs
 * Removed unncessary concatenations from log statements.
 * Resolution for POS SCR-1632: Updates for Gap - Logging
 *
 *    Rev 1.0   Apr 29 2002 15:33:42   msg
 * Initial revision.
 *
 *    Rev 1.1   Mar 18 2002 23:11:46   msg
 * - updated copyright
 *
 *    Rev 1.0   Mar 18 2002 11:24:32   msg
 * Initial revision.
 *
 *    Rev 1.0   Sep 21 2001 11:14:52   msg
 * Initial revision.
 *
 *    Rev 1.1   Sep 17 2001 13:06:44   msg
 * header update
 * ===========================================================================
 */
package oracle.retail.stores.pos.services.customer.common;

// foundation imports
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.pos.services.PosLaneActionAdapter;

//--------------------------------------------------------------------------
/**
    Removes the employee ID from the customer
    <p>
    @version $Revision: /rgbustores_13.4x_generic_branch/1 $
**/
//--------------------------------------------------------------------------
public class RemoveEmployeeIDRoad extends PosLaneActionAdapter
{
    /**
       revision number
    **/
    public static final String revisionNumber = "$Revision: /rgbustores_13.4x_generic_branch/1 $";

    //----------------------------------------------------------------------
    /**
       Removes the employee ID from the customer.
       <P>
       @param  bus     Service Bus
    **/
    //----------------------------------------------------------------------
    public void traverse(BusIfc bus)
    {
        // retrieve cargo
        CustomerCargo cargo = (CustomerCargo)bus.getCargo();

        if (logger.isInfoEnabled()) logger.info(
                    "Removing employee ID (" + cargo.getCustomer().getEmployeeID() + ") from customer (" + cargo.getCustomer().getCustomerID() + ")");

        cargo.getCustomer().setEmployeeID(null);
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
