/* ===========================================================================
* Copyright (c) 1998, 2011, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/customer/lookup/AddCustomerOfflineRoad.java /rgbustores_13.4x_generic_branch/1 2011/05/05 14:06:28 mszekely Exp $
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
 *    3    360Commerce 1.2         3/31/2005 4:27:09 PM   Robert Pearse   
 *    2    360Commerce 1.1         3/10/2005 10:19:31 AM  Robert Pearse   
 *    1    360Commerce 1.0         2/11/2005 12:09:24 PM  Robert Pearse   
 *
 *   Revision 1.3  2004/02/12 16:49:32  mcs
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
 *    Rev 1.0   Aug 29 2003 15:55:50   CSchellenger
 * Initial revision.
 * 
 *    Rev 1.1   May 27 2003 08:48:04   baa
 * rework customer offline flow
 * Resolution for 2387: Deleteing Busn Customer Lock APP- & Inc. Customer.
 * 
 *    Rev 1.0   Apr 29 2002 15:32:16   msg
 * Initial revision.
 * 
 *    Rev 1.1   Mar 18 2002 23:12:40   msg
 * - updated copyright
 * 
 *    Rev 1.0   Mar 18 2002 11:25:24   msg
 * Initial revision.
 * 
 *    Rev 1.1   16 Nov 2001 10:33:12   baa
 * Cleanup code & implement new security model on customer
 * Resolution for POS SCR-263: Apply new security model to Customer Service
 *
 *    Rev 1.0   Sep 21 2001 11:15:50   msg
 * Initial revision.
 *
 *    Rev 1.1   Sep 17 2001 13:07:12   msg
 * header update
 * ===========================================================================
 */
package oracle.retail.stores.pos.services.customer.lookup;

// foundation imports
import oracle.retail.stores.foundation.tour.application.LaneActionAdapter;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.pos.services.customer.common.CustomerCargo;
//--------------------------------------------------------------------------
/**
    Sets the offline indicator in the cargo to add.
    $Revision: /rgbustores_13.4x_generic_branch/1 $
    @deprecated as of release 6.0 obsolete class due to a change in flow
**/
//--------------------------------------------------------------------------
public class AddCustomerOfflineRoad extends LaneActionAdapter
{
    /**
       revision number
    **/
    public static final String revisionNumber = "$Revision: /rgbustores_13.4x_generic_branch/1 $";

    //----------------------------------------------------------------------
    /**
       Sets the offline indicator in the cargo to add.
       @param bus the bus traversing this lane
    **/
    //----------------------------------------------------------------------
    public void traverse(BusIfc bus)
    {

        // set the offline indicator in the cargo
        CustomerCargo cargo = (CustomerCargo) bus.getCargo();
        if (cargo.getOfflineIndicator() != CustomerCargo.OFFLINE_EXIT)
        {
            cargo.setOfflineIndicator(CustomerCargo.OFFLINE_ADD);
        }

    }

}
