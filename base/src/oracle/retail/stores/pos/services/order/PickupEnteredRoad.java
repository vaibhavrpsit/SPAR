/* ===========================================================================
* Copyright (c) 1998, 2011, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/order/PickupEnteredRoad.java /rgbustores_13.4x_generic_branch/1 2011/05/05 14:06:32 mszekely Exp $
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
 *    3    360Commerce 1.2         3/31/2005 4:29:21 PM   Robert Pearse   
 *    2    360Commerce 1.1         3/10/2005 10:24:05 AM  Robert Pearse   
 *    1    360Commerce 1.0         2/11/2005 12:13:03 PM  Robert Pearse   
 *
 *   Revision 1.3  2004/02/12 16:51:20  mcs
 *   Forcing head revision
 *
 *   Revision 1.2  2004/02/11 21:51:48  rhafernik
 *   @scr 0 Log4J conversion and code cleanup
 *
 *   Revision 1.1.1.1  2004/02/11 01:04:18  cschellenger
 *   updating to pvcs 360store-current
 *
 *
 * 
 *    Rev 1.0   Aug 29 2003 16:03:20   CSchellenger
 * Initial revision.
 * 
 *    Rev 1.2   Aug 28 2002 10:08:20   jriggins
 * Introduced the OrderCargo.serviceType property complete with accessor and mutator methods.  Replaced places where service names were being compared (via String.equals()) to String constants in OrderCargoIfc with comparisons to the newly-created serviceType constants which are ints.
 * Resolution for POS SCR-1740: Code base Conversions
 * 
 *    Rev 1.1   Aug 22 2002 10:55:14   jriggins
 * Now attempting to pull service name from the bundle.
 * Resolution for POS SCR-1740: Code base Conversions
 * 
 *    Rev 1.0   Apr 29 2002 15:11:32   msg
 * Initial revision.
 * 
 *    Rev 1.0   Mar 18 2002 11:40:38   msg
 * Initial revision.
 * 
 *    Rev 1.0   Sep 24 2001 13:00:06   MPM
 * Initial revision.
 * 
 *    Rev 1.1   Sep 17 2001 13:10:18   msg
 * header update
 * ===========================================================================
 */
package oracle.retail.stores.pos.services.order;

//foundation imports
import oracle.retail.stores.foundation.tour.application.LaneActionAdapter;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.pos.services.order.common.OrderCargo;
import oracle.retail.stores.pos.services.order.common.OrderCargoIfc;

//------------------------------------------------------------------------------
/**
    Sets the order service name to Pickup.
    <P>
    @version $Revision: /rgbustores_13.4x_generic_branch/1 $
**/
//------------------------------------------------------------------------------

public class PickupEnteredRoad extends LaneActionAdapter
{
    /**
       class name constant
    **/
    public static final String LANENAME = "PickupEnteredRoad";

    /**
       revision number for this class
    **/
    public static final String revisionNumber = "$Revision: /rgbustores_13.4x_generic_branch/1 $";

    /**
        Service Pickup bundle tag
    **/
    public static final String SERVICE_PICKUP_TAG = "ServicePickup";
    
    //------------------------------------------------------------------------------
    /**
       Sets the order service name to Pickup.
       <P>
       @param bus the bus arriving at this site
    **/
    //------------------------------------------------------------------------------

    public void traverse(BusIfc bus)
    {

        OrderCargo cargo = (OrderCargo) bus.getCargo();
        cargo.setServiceType(OrderCargoIfc.SERVICE_PICKUP_TYPE);

        //doesn't use printorder service so no need to setViewOrder

    }

    //----------------------------------------------------------------------
    /**
       Returns a string representation of the object.
       <P>
       @return String representation of object
    **/
    //----------------------------------------------------------------------

    public String toString()
    {                                   // begin toString()
        // result string
        String strResult = new String("Class: PickupEnteredRoad  (Revision " +
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
