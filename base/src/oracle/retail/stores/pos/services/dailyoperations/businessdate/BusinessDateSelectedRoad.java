/* ===========================================================================
* Copyright (c) 1998, 2011, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/dailyoperations/businessdate/BusinessDateSelectedRoad.java /rgbustores_13.4x_generic_branch/1 2011/05/05 14:06:17 mszekely Exp $
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
 *    3    360Commerce 1.2         3/31/2005 4:27:18 PM   Robert Pearse   
 *    2    360Commerce 1.1         3/10/2005 10:19:54 AM  Robert Pearse   
 *    1    360Commerce 1.0         2/11/2005 12:09:42 PM  Robert Pearse   
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
 *    Rev 1.0   Aug 29 2003 15:56:14   CSchellenger
 * Initial revision.
 * 
 *    Rev 1.0   Apr 29 2002 15:31:40   msg
 * Initial revision.
 * 
 *    Rev 1.1   Mar 18 2002 23:13:20   msg
 * - updated copyright
 * 
 *    Rev 1.0   Mar 18 2002 11:26:08   msg
 * Initial revision.
 * 
 *    Rev 1.0   Sep 21 2001 11:16:52   msg
 * Initial revision.
 * 
 *    Rev 1.1   Sep 17 2001 13:05:58   msg
 * header update
 * ===========================================================================
 */
package oracle.retail.stores.pos.services.dailyoperations.businessdate;
// java imports
// foundation imports
import oracle.retail.stores.domain.utility.EYSDate;
import oracle.retail.stores.foundation.manager.ifc.UIManagerIfc;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.pos.services.PosLaneActionAdapter;
import oracle.retail.stores.pos.ui.POSUIManagerIfc;
import oracle.retail.stores.pos.ui.beans.SelectBusinessDateBeanModel;
//------------------------------------------------------------------------------
/**
    This class retrieves the SelectBusinessDateBean input and places it in the
    cargo. <P>
    @version $Revision: /rgbustores_13.4x_generic_branch/1 $
**/
//------------------------------------------------------------------------------
public class BusinessDateSelectedRoad extends PosLaneActionAdapter
{                                       // begin class ChecKDateAisle
    /**
       lane name constant
    **/
    public static final String LANENAME = "BusinessDateSelectedRoad";
    /**
       revision number supplied by source-code-control system
    **/
    public static String revisionNumber = "$Revision: /rgbustores_13.4x_generic_branch/1 $";

    //--------------------------------------------------------------------------
    /**
       Performs the traversal functionality for the aisle.  In this case,
       the selected date is retrieved from the user interface and placed
       in the cargo. <P>
       @param bus the bus traversing this lane
    **/
    //--------------------------------------------------------------------------
    public void traverse(BusIfc bus)
    {                                   // begin traverse()

        // get default date from cargo
        BusinessDateCargo cargo = (BusinessDateCargo) bus.getCargo();

        // get selected date from UI
        POSUIManagerIfc ui = (POSUIManagerIfc)bus.getManager(UIManagerIfc.TYPE);
        SelectBusinessDateBeanModel beanModel =
            (SelectBusinessDateBeanModel) ui.getModel(POSUIManagerIfc.SELECT_BUSINESS_DATE);
        EYSDate selectedDate = beanModel.getSelectedDate();
        if (logger.isInfoEnabled()) logger.info(
                    "Selected date:  [" + selectedDate + "]");

        // set input date in cargo
        cargo.setSelectedBusinessDate(selectedDate);

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
}                                       // end class BusinessDateSelectedRoad
