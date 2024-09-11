/* ===========================================================================
* Copyright (c) 1998, 2011, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/dailyoperations/businessdate/SelectBusinessDateSite.java /rgbustores_13.4x_generic_branch/1 2011/05/05 14:06:17 mszekely Exp $
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
 *    3    360Commerce 1.2         3/31/2005 4:29:53 PM   Robert Pearse   
 *    2    360Commerce 1.1         3/10/2005 10:25:08 AM  Robert Pearse   
 *    1    360Commerce 1.0         2/11/2005 12:14:07 PM  Robert Pearse   
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
 *    Rev 1.0   Apr 29 2002 15:31:52   msg
 * Initial revision.
 * 
 *    Rev 1.1   Mar 18 2002 23:13:28   msg
 * - updated copyright
 * 
 *    Rev 1.0   Mar 18 2002 11:26:18   msg
 * Initial revision.
 * 
 *    Rev 1.0   Sep 21 2001 11:16:50   msg
 * Initial revision.
 * 
 *    Rev 1.1   Sep 17 2001 13:05:54   msg
 * header update
 * ===========================================================================
 */
package oracle.retail.stores.pos.services.dailyoperations.businessdate;

// foundation imports
import oracle.retail.stores.foundation.manager.gui.UIModelIfc;
import oracle.retail.stores.foundation.manager.ifc.UIManagerIfc;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.pos.services.PosSiteActionAdapter;
import oracle.retail.stores.pos.ui.POSUIManagerIfc;
import oracle.retail.stores.pos.ui.beans.SelectBusinessDateBeanModel;

//------------------------------------------------------------------------------
/**
    This site displays the select businees date screen, using multiple 
    business dates from cargo. <P>
    @version $Revision: /rgbustores_13.4x_generic_branch/1 $
**/
//------------------------------------------------------------------------------
public class SelectBusinessDateSite extends PosSiteActionAdapter
{                                       // begin class SelectBusinessDateSite
    /**
       revision number supplied by source-code-control system
    **/
    public static String revisionNumber = "$Revision: /rgbustores_13.4x_generic_branch/1 $";
    /**
       site name constant
    **/
    public static final String SITENAME = "SelectBusinessDateSite";

    //--------------------------------------------------------------------------
    /**
       Displays list of open business dates for the user's selection. <P>
       @param bus the bus arriving at this site
    **/
    //--------------------------------------------------------------------------
    public void arrive(BusIfc bus)
    {                                   // begin arrive()
        // get the cargo
        BusinessDateCargo cargo = (BusinessDateCargo) bus.getCargo();

        // get the user interface manager, bean model
        POSUIManagerIfc ui = (POSUIManagerIfc) bus.getManager(UIManagerIfc.TYPE);
        SelectBusinessDateBeanModel model = new SelectBusinessDateBeanModel();

        // get the business dates from cargo to display
        model.setBusinessDates(cargo.getBusinessDateList());

        // display user interface
        ui.showScreen(POSUIManagerIfc.SELECT_BUSINESS_DATE,(UIModelIfc)model);
    }                                   // end arrive()

    //---------------------------------------------------------------------
    /**
       Retrieves the source-code-control system revision number. <P>
       @return String representation of revision number
    **/
    //---------------------------------------------------------------------
    public String getRevisionNumber()
    {                                   // begin getRevisionNumber()
        // return string
        return(revisionNumber);
    }                                  // end getRevisionNumber()

}                                       // end class SelectBusinessDateSite
