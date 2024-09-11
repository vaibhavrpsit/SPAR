/* ===========================================================================
* Copyright (c) 2003, 2011, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/instantcredit/enrollment/EnrollLaterAisle.java /rgbustores_13.4x_generic_branch/1 2011/05/05 14:06:14 mszekely Exp $
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
 * 4    360Commerce 1.3         3/18/2008 3:04:26 AM   Naveen Ganesh   Replaced
 *       the Default text for argText from 360Commerce to Oracle
 * 3    360Commerce 1.2         3/31/2005 4:28:00 PM   Robert Pearse   
 * 2    360Commerce 1.1         3/10/2005 10:21:23 AM  Robert Pearse   
 * 1    360Commerce 1.0         2/11/2005 12:10:53 PM  Robert Pearse   
 *
 *Revision 1.3  2004/02/12 16:50:42  mcs
 *Forcing head revision
 *
 *Revision 1.2  2004/02/11 21:51:22  rhafernik
 *@scr 0 Log4J conversion and code cleanup
 *
 *Revision 1.1.1.1  2004/02/11 01:04:16  cschellenger
 *updating to pvcs 360store-current
 *
 *
 * 
 *    Rev 1.1   Nov 24 2003 19:38:20   nrao
 * Code Review Changes.
 *
 * ===========================================================================
 */
package oracle.retail.stores.pos.services.instantcredit.enrollment;

// foundation imports
import org.apache.log4j.Logger;

import oracle.retail.stores.foundation.manager.ifc.UIManagerIfc;
import oracle.retail.stores.foundation.tour.application.LaneActionAdapter;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.pos.manager.ifc.UtilityManagerIfc;
import oracle.retail.stores.pos.manager.utility.UtilityManager;
import oracle.retail.stores.pos.ui.DialogScreensIfc;
import oracle.retail.stores.pos.ui.POSUIManagerIfc;
import oracle.retail.stores.pos.ui.UIUtilities;

//--------------------------------------------------------------------------
/**
    @version $Revision: /rgbustores_13.4x_generic_branch/1 $
**/
//--------------------------------------------------------------------------
public class EnrollLaterAisle extends LaneActionAdapter
{
    /** The logger to which log messages will be sent */
    protected static Logger logger = Logger.getLogger(oracle.retail.stores.pos.services.instantcredit.enrollment.EnrollLaterAisle.class);

    /** revision number */
    public static final String revisionNumber = "$Revision: /rgbustores_13.4x_generic_branch/1 $";

    //----------------------------------------------------------------------
    /**
        @param  bus Service Bus
    **/
    //----------------------------------------------------------------------
    public void traverse(BusIfc bus)
    {
        POSUIManagerIfc ui = (POSUIManagerIfc) bus.getManager(UIManagerIfc.TYPE);
        UtilityManager util = (UtilityManager) bus.getManager(UtilityManagerIfc.TYPE);
        
        String argText = util.retrieveDialogText("CallComplete.360Commerce",
                                                 "Oracle");
        String [] args = 
        {
            argText
        };
            
        UIUtilities.setDialogModel(ui, DialogScreensIfc.ERROR, "CallComplete", args, "Frank");
    }
}
