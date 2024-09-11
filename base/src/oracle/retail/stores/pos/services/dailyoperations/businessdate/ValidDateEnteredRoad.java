/* ===========================================================================
* Copyright (c) 1998, 2011, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/dailyoperations/businessdate/ValidDateEnteredRoad.java /rgbustores_13.4x_generic_branch/1 2011/05/05 14:06:17 mszekely Exp $
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
 *    3    360Commerce 1.2         3/31/2005 4:30:44 PM   Robert Pearse   
 *    2    360Commerce 1.1         3/10/2005 10:26:44 AM  Robert Pearse   
 *    1    360Commerce 1.0         2/11/2005 12:15:31 PM  Robert Pearse   
 *
 *   Revision 1.3  2004/02/12 16:49:35  mcs
 *   Forcing head revision
 *
 *   Revision 1.2  2004/02/11 21:45:31  rhafernik
 *   @scr 0 Log4J conversion and code cleanup
 *
 *   Revision 1.1.1.1  2004/02/11 01:04:15  cschellenger
 *   updating to pvcs 360store-current
 *
 *
 * 
 *    Rev 1.0   Aug 29 2003 15:56:18   CSchellenger
 * Initial revision.
 * 
 *    Rev 1.0   Apr 29 2002 15:31:54   msg
 * Initial revision.
 * 
 *    Rev 1.1   Mar 18 2002 23:13:28   msg
 * - updated copyright
 * 
 *    Rev 1.0   Mar 18 2002 11:26:20   msg
 * Initial revision.
 * 
 *    Rev 1.0   Sep 21 2001 11:16:52   msg
 * Initial revision.
 * 
 *    Rev 1.1   Sep 17 2001 13:05:54   msg
 * header update
 * ===========================================================================
 */
package oracle.retail.stores.pos.services.dailyoperations.businessdate;
// java imports
// foundation imports
import oracle.retail.stores.foundation.tour.application.LaneActionAdapter;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
//------------------------------------------------------------------------------
/**
    This class makes the input date the selected date. <P>
    @version $Revision: /rgbustores_13.4x_generic_branch/1 $
**/
//------------------------------------------------------------------------------
public class ValidDateEnteredRoad extends LaneActionAdapter
{                                       // begin class ChecKDateAisle
    /**
       lane name constant
    **/
    public static final String LANENAME = "ValidDateEnteredRoad";
    /**
       revision number supplied by source-code-control system
    **/
    public static String revisionNumber = "$Revision: /rgbustores_13.4x_generic_branch/1 $";

    //--------------------------------------------------------------------------
    /**
       Performs the traversal functionality for the aisle.  In this case,
       the input date is made the selected date. <P>
       @param bus the bus traversing this lane
    **/
    //--------------------------------------------------------------------------
    public void traverse(BusIfc bus)
    {                                   // begin traverse()

        // get default date from cargo
        BusinessDateCargo cargo = (BusinessDateCargo) bus.getCargo();

        // set selected date
        cargo.setSelectedBusinessDate(cargo.getInputBusinessDate());

    }                                   // end traverse()

    //----------------------------------------------------------------------
    /**
       Returns the revision number of the class. <P>
       @return String representation of revision number
    **/
    //----------------------------------------------------------------------
    public String getRevisionNumber()
    {                                   // begin getRevisionNumber()
        // return string
        return(revisionNumber);
    }                                   // end getRevisionNumber()
}                                       // end class ValidDateEnteredRoad
