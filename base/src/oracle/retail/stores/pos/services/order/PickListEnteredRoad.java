/* ===========================================================================
* Copyright (c) 1998, 2013, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/order/PickListEnteredRoad.java /main/1 2013/01/10 14:03:51 sgu Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    sgu       01/04/13 - add new road
 *    sgu       01/03/13 - rename the class for xc only
 *    sgu       01/03/13 - add back order picklist road
 *    cgreene   05/26/10 - convert to oracle packaging
 *    cgreene   04/28/10 - updating deprecated names
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
 *    Rev 1.0   Aug 29 2003 16:03:18   CSchellenger
 * Initial revision.
 * 
 *    Rev 1.2   Aug 28 2002 10:08:18   jriggins
 * Introduced the OrderCargo.serviceType property complete with accessor and mutator methods.  Replaced places where service names were being compared (via String.equals()) to String constants in OrderCargoIfc with comparisons to the newly-created serviceType constants which are ints.
 * Resolution for POS SCR-1740: Code base Conversions
 * 
 *    Rev 1.1   Aug 22 2002 10:55:32   jriggins
 * Now attempting to pull service name from the bundle.
 * Resolution for POS SCR-1740: Code base Conversions
 * 
 *    Rev 1.0   Apr 29 2002 15:11:30   msg
 * Initial revision.
 * 
 *    Rev 1.0   Mar 18 2002 11:40:36   msg
 * Initial revision.
 * 
 *    Rev 1.0   Sep 24 2001 13:00:04   MPM
 * Initial revision.
 * 
 *    Rev 1.1   Sep 17 2001 13:10:18   msg
 * header update
 * ===========================================================================
 */
package oracle.retail.stores.pos.services.order;

//foundation imports
import oracle.retail.stores.domain.order.OrderConstantsIfc;
import oracle.retail.stores.foundation.tour.application.LaneActionAdapter;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.pos.services.order.common.OrderCargo;
import oracle.retail.stores.pos.services.order.common.OrderCargoIfc;
import oracle.retail.stores.pos.services.order.common.OrderSearchCargoIfc;

//------------------------------------------------------------------------------
/**
    Sets the order service name to Print, sets view order flag to true,
    sets the order search method to New order search, and sets the order
    status flag to New.
    <P>
    @version $Revision: /main/1 $
**/
//------------------------------------------------------------------------------

public class PickListEnteredRoad extends LaneActionAdapter
{
    /**
       class name constant
    **/
    public static final String LANENAME = "PickListEnteredRoad";

    /**
       revision number for this class
    **/
    public static final String revisionNumber = "$Revision: /main/1 $";

    /**
        Service Print bundle tag
    **/
    public static final String SERVICE_PRINT_TAG = "ServicePrint";
    
    //------------------------------------------------------------------------------
    /**
       Sets the order service name to Print, sets view order flag to true,
       sets the order search method to New order search, and sets the order
       status flag to New.
       <P>
       @param bus the bus arriving at this site
    **/
    //------------------------------------------------------------------------------

    public void traverse(BusIfc bus)
    {

        OrderCargo cargo = (OrderCargo) bus.getCargo();
        cargo.setServiceType(OrderCargoIfc.SERVICE_PRINT_TYPE);          
        cargo.setViewOrder(true);

        //set the cargo search criteria for lookup order service to retrieve new orders
        cargo.setSearchMethod(OrderSearchCargoIfc.SEARCH_FOR_NEW_ORDERS);
        cargo.setStatus(OrderConstantsIfc.ORDER_STATUS_NEW);

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
        String strResult = new String("Class: PickListEnteredRoad  (Revision " +
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
