/* ===========================================================================
* Copyright (c) 1998, 2011, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/returns/returnfindtrans/CheckDatabaseErrorAisle.java /rgbustores_13.4x_generic_branch/1 2011/05/05 14:05:55 mszekely Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    jswan     07/07/10 - Code review changes and fixes for Cancel button in
 *                         External Order integration.
 *    cgreene   05/26/10 - convert to oracle packaging
 *    cgreene   04/27/10 - XbranchMerge cgreene_refactor-duplicate-pos-classes
 *                         from st_rgbustores_techissueseatel_generic_branch
 *    abondala  01/03/10 - update header date
 *
 * ===========================================================================
 * $Log:
 *    3    360Commerce 1.2         3/31/2005 4:27:24 PM   Robert Pearse   
 *    2    360Commerce 1.1         3/10/2005 10:20:06 AM  Robert Pearse   
 *    1    360Commerce 1.0         2/11/2005 12:09:54 PM  Robert Pearse   
 *
 *   Revision 1.6  2004/07/30 17:36:07  khassen
 *   @scr 6620 - "Loop" should be "Retry".
 *
 *   Revision 1.5  2004/06/21 22:46:15  mweis
 *   @scr 5643 Returning when database is offline displays wrong error dialog
 *
 *   Revision 1.4  2004/02/23 14:58:52  baa
 *   @scr 0 cleanup javadocs
 *
 *   Revision 1.3  2004/02/12 16:51:48  mcs
 *   Forcing head revision
 *
 *   Revision 1.2  2004/02/11 21:52:28  rhafernik
 *   @scr 0 Log4J conversion and code cleanup
 *
 *   Revision 1.1.1.1  2004/02/11 01:04:20  cschellenger
 *   updating to pvcs 360store-current
 *
 *
 * 
 *    Rev 1.0   Aug 29 2003 16:05:58   CSchellenger
 * Initial revision.
 * 
 *    Rev 1.0   Apr 29 2002 15:05:46   msg
 * Initial revision.
 * 
 *    Rev 1.0   Mar 18 2002 11:45:36   msg
 * Initial revision.
 * 
 *    Rev 1.0   Sep 21 2001 11:25:00   msg
 * Initial revision.
 * 
 *    Rev 1.1   Sep 17 2001 13:12:42   msg
 * header update
 * ===========================================================================
 */
package oracle.retail.stores.pos.services.returns.returnfindtrans;

import oracle.retail.stores.pos.services.common.DBErrorCargoIfc;
import oracle.retail.stores.foundation.manager.data.DataException;
import oracle.retail.stores.foundation.tour.application.Letter;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.foundation.tour.ifc.CargoIfc;
import oracle.retail.stores.pos.services.PosLaneActionAdapter;

/**
 * This road mails a "ReturnItem" or "Loop" letter depending on dbError.
 * 
 * @version $Revision: /rgbustores_13.4x_generic_branch/1 $
 */
public class CheckDatabaseErrorAisle extends PosLaneActionAdapter
{
    private static final long serialVersionUID = -5137526696275159680L;

    /**
     * revision number
     */
    public static final String revisionNumber = "$Revision: /rgbustores_13.4x_generic_branch/1 $";

    /**
     * Constant for letter name
     */
    public static final String TRANSACTION_NOT_FOUND = "TransactionNotFound";

    /**
     * Constant for letter name
     */
    public static final String RETRY = "Retry";

    /**
     * This road mails a "ReturnItem" or "Loop" letter depending on dbError.
     * 
     * @param bus Service Bus
     */
    @Override
    public void traverse(BusIfc bus)
    {
        DBErrorCargoIfc cargo = (DBErrorCargoIfc) bus.getCargo();
        
        // if connection error return by item
        if (cargo.getDataExceptionErrorCode() == DataException.CONNECTION_ERROR)
        {
            cargo.setDataExceptionErrorCode(DataException.NONE);
            bus.mail(new Letter(TRANSACTION_NOT_FOUND), BusIfc.CURRENT);    
        }
        else
        {
            cargo.setDataExceptionErrorCode(DataException.NONE);
            bus.mail(new Letter(RETRY), BusIfc.CURRENT);
        }
    }

    /**
     * Returns whether we are in a "returns" situation.
     * @param cargo The current cargo.
     * @return Whether we are in a "returns" situation.
     */
    protected boolean isReturn(CargoIfc cargo)
    {
        return (cargo instanceof ReturnFindTransCargo);
    }
}
