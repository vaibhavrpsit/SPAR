/* ===========================================================================
* Copyright (c) 1998, 2011, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/servicealert/SetRetrieveListFailedRoad.java /rgbustores_13.4x_generic_branch/1 2011/05/05 14:06:11 mszekely Exp $
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
 *    3    360Commerce 1.2         3/31/2005 4:29:57 PM   Robert Pearse   
 *    2    360Commerce 1.1         3/10/2005 10:25:15 AM  Robert Pearse   
 *    1    360Commerce 1.0         2/11/2005 12:14:12 PM  Robert Pearse   
 *
 *   Revision 1.3  2004/02/12 16:51:58  mcs
 *   Forcing head revision
 *
 *   Revision 1.2  2004/02/11 21:52:29  rhafernik
 *   @scr 0 Log4J conversion and code cleanup
 *
 *   Revision 1.1.1.1  2004/02/11 01:04:20  cschellenger
 *   updating to pvcs 360store-current
 *
 *
 * 
 *    Rev 1.0   Aug 29 2003 16:07:00   CSchellenger
 * Initial revision.
 * 
 *    Rev 1.0   Apr 29 2002 15:03:22   msg
 * Initial revision.
 * 
 *    Rev 1.0   Mar 18 2002 11:47:40   msg
 * Initial revision.
 * 
 *    Rev 1.0   Jan 09 2002 12:35:30   dfh
 * Initial revision.
 * Resolution for POS SCR-179: CR/Order, after Svc Alert queue empty, db error, then app hung
 *   
 * ===========================================================================
 */
package oracle.retail.stores.pos.services.servicealert;

// foundation imports
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.pos.services.PosLaneActionAdapter;
//--------------------------------------------------------------------------
/**
    Road to set the service alert cargo flag retreive list failed to false.
    <P>
    @version $Revision: /rgbustores_13.4x_generic_branch/1 $
**/
//--------------------------------------------------------------------------
public class SetRetrieveListFailedRoad extends PosLaneActionAdapter
{
    /**
        road name constant
    **/
    public static final String LANENAME = "SetRetrieveListFailedRoad";
    /**
       revision number
    **/
    public static final String revisionNumber = "$Revision: /rgbustores_13.4x_generic_branch/1 $";

    //----------------------------------------------------------------------
    /**
       Resets the service alert cargo flag retreive list failed to false.
       This allows the database request to be retried after the user presses
       Refresh on the Service Alert screen.
       <P>
       @param  bus     Service Bus
    **/
    //----------------------------------------------------------------------
    public void traverse(BusIfc bus)
    {
        ServiceAlertCargo cargo  = (ServiceAlertCargo) bus.getCargo();
        
        cargo.setRetrieveListFailed(false);
    }//end traverse
}
