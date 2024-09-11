/* ===========================================================================
* Copyright (c) 1998, 2013, Oracle and/or its affiliates. All rights reserved.
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/modifytransaction/suspend/MAXPrintSuspendedAcknowledgeAisle.java /main/16 2013/10/08 11:01:44 ohorne Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    ohorne    10/07/13 - fix for classcast exception
 *    sgu       10/03/13 - set receipt printed flag for a suspended txn
 *    cgreene   06/26/13 - use RetailTransationCargoIfc interface instead
 *    icole     04/11/12 - Forward port of spurkaya_bug-13114278 , SUSPENDED
 *                         TRANSACTIONS THAT ARE ALLOWED TO TIMEOUT CAUSE A
 *                         QUEUE EXCEPTION.
 *    cgreene   05/26/10 - convert to oracle packaging
 *    abondala  01/03/10 - update header date
 *    cgreene   11/24/08 - set doc type for suspend trans
 *
 * ===========================================================================
 * $Log:
 *    4    360Commerce 1.3         4/30/2007 7:01:38 PM   Alan N. Sinton  CR
 *         26485 - Merge from v12.0_temp.
 *    3    360Commerce 1.2         3/31/2005 4:29:31 PM   Robert Pearse   
 *    2    360Commerce 1.1         3/10/2005 10:24:25 AM  Robert Pearse   
 *    1    360Commerce 1.0         2/11/2005 12:13:27 PM  Robert Pearse   
 *
 *   Revision 1.3  2004/02/12 16:51:16  mcs
 *   Forcing head revision
 *
 *   Revision 1.2  2004/02/11 21:51:47  rhafernik
 *   @scr 0 Log4J conversion and code cleanup
 *
 *   Revision 1.1.1.1  2004/02/11 01:04:18  cschellenger
 *   updating to pvcs 360store-current
 *
 *
 * 
 *    Rev 1.1   Jan 12 2004 14:34:28   DCobb
 * Removed the printer offline halt behavior.
 * Resolution for 3502: Remove "Printer Offline Behavior" parameter
 * 
 *    Rev 1.0   Aug 29 2003 16:02:46   CSchellenger
 * Initial revision.
 * 
 *    Rev 1.0   Jan 10 2003 17:08:00   DCobb
 * Initial revision.
 * Resolution for POS SCR-1892: The printer offline message does not appear for transaction suspension or retrieval
 * ===========================================================================
 */
package max.retail.stores.pos.services.modifytransaction.suspend;

import oracle.retail.stores.foundation.tour.application.Letter;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.pos.services.PosLaneActionAdapter;

/**
 * Print the receipt.
 * 
 * @version $Revision: /main/16 $
 */
public class MAXPrintSuspendedAcknowledgeAisle extends PosLaneActionAdapter
{
    private static final long serialVersionUID = -5714983071236493101L;

    public static final String LANENAME = "MAXPrintSuspendedAcknowledgeAisle";
    public static final String revisionNumber = "$Revision: /main/16 $";

    /**
     * Print the receipt and send a letter
     * 
     * @param bus the bus traversing this lane
     */
    @Override
    public void traverse(BusIfc bus)
    {
            bus.mail(new Letter("SuspendedTransaction"), BusIfc.CURRENT);
    }
}
