/* ===========================================================================
* Copyright (c) 1998, 2011, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/customer/lookup/IsOfflineExitSignal.java /rgbustores_13.4x_generic_branch/1 2011/05/05 14:06:27 mszekely Exp $
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
 *    3    360Commerce 1.2         3/31/2005 4:28:28 PM   Robert Pearse   
 *    2    360Commerce 1.1         3/10/2005 10:22:19 AM  Robert Pearse   
 *    1    360Commerce 1.0         2/11/2005 12:11:34 PM  Robert Pearse   
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
 *    Rev 1.0   Aug 29 2003 15:56:00   CSchellenger
 * Initial revision.
 * 
 *    Rev 1.0   Apr 29 2002 15:32:34   msg
 * Initial revision.
 * 
 *    Rev 1.1   Mar 18 2002 23:12:56   msg
 * - updated copyright
 * 
 *    Rev 1.0   Mar 18 2002 11:25:42   msg
 * Initial revision.
 * 
 *    Rev 1.1   16 Nov 2001 10:34:02   baa
 * Cleanup code & implement new security model on customer
 * Resolution for POS SCR-263: Apply new security model to Customer Service
 *
 *    Rev 1.0   Sep 21 2001 11:15:44   msg
 * Initial revision.
 *
 *    Rev 1.1   Sep 17 2001 13:07:06   msg
 * header update
 * ===========================================================================
 */
package oracle.retail.stores.pos.services.customer.lookup;

// foundation imports
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.pos.services.customer.common.CustomerCargo;
import oracle.retail.stores.pos.services.customer.common.IsOfflineSignal;

//--------------------------------------------------------------------------
/**
    This determines if there was a dataconnection failure.
    <P>
    @version $Revision: /rgbustores_13.4x_generic_branch/1 $
**/
//--------------------------------------------------------------------------
public class IsOfflineExitSignal extends IsOfflineSignal
{
    /**
        revision number of this class
    **/
    public static final String revisionNumber = "$Revision: /rgbustores_13.4x_generic_branch/1 $";

    //----------------------------------------------------------------------
    /**
        Determines whether it is safe for the bus to proceed.
        <p>
        @param bus the bus trying to proceed
        @return true if the data exception error code is CONNECTION_ERROR
                and the offline indicator is OFFLINE_EXIT
    **/
    //----------------------------------------------------------------------
    public boolean roadClear(BusIfc bus)
    {
        boolean offlineExit = false;
        CustomerCargo cargo = (CustomerCargo)bus.getCargo();
        // Check the data exception error code
        if (super.roadClear(bus)
            && cargo.getOfflineIndicator() == CustomerCargo.OFFLINE_EXIT)
        {
            offlineExit = true;
        }

        return(offlineExit);
    }


}
