/* ===========================================================================
* Copyright (c) 1998, 2011, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/returns/returnoptions/UndoSelectedRoad.java /rgbustores_13.4x_generic_branch/1 2011/05/05 14:05:54 mszekely Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    jswan     06/30/10 - Checkin for first promotion of External Order
 *                         integration.
 *    cgreene   05/26/10 - convert to oracle packaging
 *    abondala  01/03/10 - update header date
 *
 * ===========================================================================
 * $Log:
 *    5    360Commerce 1.4         1/25/2006 4:11:54 PM   Brett J. Larsen merge
 *          7.1.1 changes (aka. 7.0.3 fixes) into 360Commerce view
 *    4    360Commerce 1.3         12/13/2005 4:42:43 PM  Barry A. Pape
 *         Base-lining of 7.1_LA
 *    3    360Commerce 1.2         3/31/2005 4:30:38 PM   Robert Pearse   
 *    2    360Commerce 1.1         3/10/2005 10:26:31 AM  Robert Pearse   
 *    1    360Commerce 1.0         2/11/2005 12:15:22 PM  Robert Pearse   
 *:
 *    4    .v700     1.2.1.0     9/7/2005 13:46:36      Rohit Sachdeva  Escape
 *         from rtn, no rcpt after entering store # or purchase date.  Press
 *         receipt .  Crash
 *    3    360Commerce1.2         3/31/2005 15:30:38     Robert Pearse
 *    2    360Commerce1.1         3/10/2005 10:26:31     Robert Pearse
 *    1    360Commerce1.0         2/11/2005 12:15:22     Robert Pearse
 *
 *Log:
 *    5    360Commerce 1.4         1/25/2006 4:11:54 PM   Brett J. Larsen merge
 *          7.1.1 changes (aka. 7.0.3 fixes) into 360Commerce view
 *    4    360Commerce 1.3         12/13/2005 4:42:43 PM  Barry A. Pape
 *         Base-lining of 7.1_LA
 *    3    360Commerce 1.2         3/31/2005 4:30:38 PM   Robert Pearse   
 *    2    360Commerce 1.1         3/10/2005 10:26:31 AM  Robert Pearse   
 *    1    360Commerce 1.0         2/11/2005 12:15:22 PM  Robert Pearse   
 *: UndoSelectedRoad.java,v $
 *Log:
 *    5    360Commerce 1.4         1/25/2006 4:11:54 PM   Brett J. Larsen merge
 *          7.1.1 changes (aka. 7.0.3 fixes) into 360Commerce view
 *    4    360Commerce 1.3         12/13/2005 4:42:43 PM  Barry A. Pape
 *         Base-lining of 7.1_LA
 *    3    360Commerce 1.2         3/31/2005 4:30:38 PM   Robert Pearse   
 *    2    360Commerce 1.1         3/10/2005 10:26:31 AM  Robert Pearse   
 *    1    360Commerce 1.0         2/11/2005 12:15:22 PM  Robert Pearse   
 *:
 *    5    .v710     1.2.2.1     10/19/2005 18:25:36    Charles Suehs   Merge
 *         from .v700
 *    4    .v710     1.2.2.0     10/18/2005 11:03:53    Charles Suehs   Merge
 *         from UndoSelectedRoad.java, Revision 1.2.1.0
 *    3    360Commerce1.2         3/31/2005 15:30:38     Robert Pearse
 *    2    360Commerce1.1         3/10/2005 10:26:31     Robert Pearse
 *    1    360Commerce1.0         2/11/2005 12:15:22     Robert Pearse
 *
 *   Revision 1.1.2.1  2004/10/27 20:36:00  cdb
 *   @scr 7535	Corrected flow - restored Return Options Cargo when Undo selected from
 *   CheckForPersonalIDRequiredSite.
 *
 *
 * ===========================================================================
 */
package oracle.retail.stores.pos.services.returns.returnoptions;

import oracle.retail.stores.domain.returns.ReturnTenderDataElementIfc;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.pos.services.PosLaneActionAdapter;

//------------------------------------------------------------------------------
/**
    This road will turn off the transfer cargo flag.
    @version $Revision: /rgbustores_13.4x_generic_branch/1 $
**/
//------------------------------------------------------------------------------

public class UndoSelectedRoad extends PosLaneActionAdapter
{

    //--------------------------------------------------------------------------
    /**


            @param bus the bus traversing this lane
    **/
    //--------------------------------------------------------------------------

    public void traverse(BusIfc bus)
    {
        ReturnOptionsCargo cargo = (ReturnOptionsCargo) bus.getCargo();

        // This is an Undo so don't transfer the cargo.
        cargo.setReturnData(null);
        cargo.setOriginalTransaction(null);
        cargo.setOriginalTransactionId(null);
        cargo.setHaveReceipt(false);
        cargo.setGiftReceiptSelected(false);
        cargo.setSearchCriteria(null);
    }

}
