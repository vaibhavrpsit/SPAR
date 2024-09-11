/* ===========================================================================
* Copyright (c) 1998, 2011, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/returns/returnfindtrans/RollbackTransactionSummaryRoad.java /rgbustores_13.4x_generic_branch/1 2011/05/05 14:05:55 mszekely Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    cgreene   05/26/10 - convert to oracle packaging
 *
 * ===========================================================================
 * $Log:
 *    3    360Commerce 1.2         3/31/2005 4:29:48 PM   Robert Pearse   
 *    2    360Commerce 1.1         3/10/2005 10:24:57 AM  Robert Pearse   
 *    1    360Commerce 1.0         2/11/2005 12:13:59 PM  Robert Pearse   
 *
 *   Revision 1.4  2004/03/17 16:38:19  epd
 *   @scr 3561 Fixed flow and cargo caching bug
 *
 *   Revision 1.3  2004/02/23 14:58:52  baa
 *   @scr 0 cleanup javadocs
 *
 *   Revision 1.2  2004/02/12 16:51:48  mcs
 *   Forcing head revision
 *
 *   Revision 1.1.1.1  2004/02/11 01:04:20  cschellenger
 *   updating to pvcs 360store-current
 *
 *
 * 
 *    Rev 1.1   Dec 30 2003 16:58:38   baa
 * cleanup for return feature
 * Resolution for 3561: Feature Enhacement: Return Search by Tender
 * ===========================================================================
 */
package oracle.retail.stores.pos.services.returns.returnfindtrans;

import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.pos.services.PosLaneActionAdapter;


/**
 * Interacts with the cargo to restore a previous version of the transaction summary list.  The 
 * current list will be deactivated and the previous list will now be the active summary.  If there 
 * is not a prior list available then the current list will remain active and the selection index 
 * will be reset.
 */
public class RollbackTransactionSummaryRoad extends PosLaneActionAdapter
{

    /**
     * Raw revision number string for the site.
     * <p></p>
     */
    public static final String revisionNumber = "$Revision: /rgbustores_13.4x_generic_branch/1 $";

    /**
     * Provides the cargo with the index of the selected transaction from the UI.
     * @param bus  provides the cargo & managers to handle the mission of the road.
     */
    public void traverse(BusIfc bus)
    {
        ReturnFindTransCargo cargo = (ReturnFindTransCargo) bus.getCargo();
        cargo.rollbackSummary();
        cargo.setSearchCriteria(null);
    }

}
