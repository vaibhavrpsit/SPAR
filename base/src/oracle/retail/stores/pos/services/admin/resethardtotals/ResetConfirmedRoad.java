/* ===========================================================================
* Copyright (c) 1998, 2011, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/admin/resethardtotals/ResetConfirmedRoad.java /rgbustores_13.4x_generic_branch/1 2011/05/05 14:06:07 mszekely Exp $
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
 * 3    360Commerce 1.2         3/31/2005 4:29:40 PM   Robert Pearse   
 * 2    360Commerce 1.1         3/10/2005 10:24:44 AM  Robert Pearse   
 * 1    360Commerce 1.0         2/11/2005 12:13:44 PM  Robert Pearse   
 *
 *Revision 1.3  2004/02/12 16:48:54  mcs
 *Forcing head revision
 *
 *Revision 1.2  2004/02/11 21:36:47  rhafernik
 *@scr 0 Log4J conversion and code cleanup
 *
 *Revision 1.1.1.1  2004/02/11 01:04:14  cschellenger
 *updating to pvcs 360store-current
 *
 *
 * 
 *    Rev 1.0   Aug 29 2003 15:53:20   CSchellenger
 * Initial revision.
 * 
 *    Rev 1.0   Apr 29 2002 15:38:08   msg
 * Initial revision.
 * 
 *    Rev 1.1   Mar 18 2002 23:06:36   msg
 * - updated copyright
 * 
 *    Rev 1.0   Mar 18 2002 11:20:42   msg
 * Initial revision.
 * 
 *    Rev 1.1   Mar 10 2002 10:09:02   mpm
 * Removed unnecessary reference to StatusBeanModel.
 * Resolution for POS SCR-351: Internationalization
 *
 *    Rev 1.0   07 Dec 2001 13:56:48   epd
 * Initial revision.
 * Resolution for POS SCR-158: Reset Totals screen never displays
 *
 * ===========================================================================
 */
package oracle.retail.stores.pos.services.admin.resethardtotals;

// foundation imports
import oracle.retail.stores.foundation.manager.ifc.UIManagerIfc;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.foundation.utility.Util;
import oracle.retail.stores.pos.services.PosLaneActionAdapter;
import oracle.retail.stores.pos.ui.POSUIManagerIfc;
import oracle.retail.stores.pos.ui.beans.POSBaseBeanModel;

//--------------------------------------------------------------------------
/**
    This road sets the current till and drawer status for manual drawer operation.
    @version $Revision: /rgbustores_13.4x_generic_branch/1 $
**/
//--------------------------------------------------------------------------
public class ResetConfirmedRoad extends PosLaneActionAdapter
{
    /**
       revision number supplied by Team Connection
    **/
    public static final String revisionNumber = "$Revision: /rgbustores_13.4x_generic_branch/1 $";

    //----------------------------------------------------------------------
    /**
        Set the function ID.
        @param bus Service Bus
    **/
    //----------------------------------------------------------------------
    public void traverse(BusIfc bus)
    {
        POSUIManagerIfc ui = (POSUIManagerIfc)bus.getManager(UIManagerIfc.TYPE);
        POSBaseBeanModel baseModel = new POSBaseBeanModel();
        ui.showScreen(POSUIManagerIfc.RESETTING_HARDTOTALS, baseModel);
    }

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
        return(Util.parseRevisionNumber(revisionNumber));
    }                                   // end getRevisionNumber()
}
