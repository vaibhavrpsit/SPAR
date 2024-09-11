/* ===========================================================================
* Copyright (c) 1998, 2011, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/layaway/find/LayawayNumberSearchRoad.java /rgbustores_13.4x_generic_branch/1 2011/05/05 14:06:13 mszekely Exp $
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
 *    3    360Commerce 1.2         3/31/2005 4:28:50 PM   Robert Pearse   
 *    2    360Commerce 1.1         3/10/2005 10:23:02 AM  Robert Pearse   
 *    1    360Commerce 1.0         2/11/2005 12:12:16 PM  Robert Pearse   
 *
 *   Revision 1.3  2004/02/12 16:50:52  mcs
 *   Forcing head revision
 *
 *   Revision 1.2  2004/02/11 21:51:22  rhafernik
 *   @scr 0 Log4J conversion and code cleanup
 *
 *   Revision 1.1.1.1  2004/02/11 01:04:17  cschellenger
 *   updating to pvcs 360store-current
 *
 *
 * 
 *    Rev 1.0   Aug 29 2003 16:00:44   CSchellenger
 * Initial revision.
 * 
 *    Rev 1.0   Apr 29 2002 15:20:48   msg
 * Initial revision.
 * 
 *    Rev 1.0   Mar 18 2002 11:35:18   msg
 * Initial revision.
 * 
 *    Rev 1.0   Sep 21 2001 11:21:30   msg
 * Initial revision.
 * 
 *    Rev 1.1   Sep 17 2001 13:08:34   msg
 * header update
 * ===========================================================================
 */
package oracle.retail.stores.pos.services.layaway.find; 

// foundation imports
import oracle.retail.stores.foundation.manager.ifc.UIManagerIfc;
import oracle.retail.stores.foundation.tour.application.LaneActionAdapter;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.pos.ui.POSUIManagerIfc;

//------------------------------------------------------------------------------
/**
    This class gets the layaway id entered in the UI and stores it in
    LayawayCargo. <P>
    @version $Revision: /rgbustores_13.4x_generic_branch/1 $
**/
//------------------------------------------------------------------------------
public class LayawayNumberSearchRoad extends LaneActionAdapter 
{                                       // begin class LayawayNumberSearchRoad
    /**
        lane name constant
    **/
    public static final String LANENAME = "LayawayNumberSearchRoad";
    
    /**
        revision number supplied by source-code-control system
    **/
    public static String revisionNumber = "$Revision: /rgbustores_13.4x_generic_branch/1 $";

    //--------------------------------------------------------------------------
    /**
        Performs the traversal functionality for the aisle.  In this case,
        the input is retrieved from the user interface and placed
        in the cargo. <P>
        @param bus the bus traversing this lane
    **/
    //--------------------------------------------------------------------------
    public void traverse(BusIfc bus)
    {                                   // begin traverse()
        FindLayawayCargoIfc cargo = (FindLayawayCargoIfc) bus.getCargo();
        
        POSUIManagerIfc ui = (POSUIManagerIfc)bus.getManager(UIManagerIfc.TYPE);
 
        cargo.setLayawaySearchID(ui.getInput());
        
    }                                   // end traverse()

}                                       // end class LayawayNumberSearchRoad
