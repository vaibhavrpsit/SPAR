/* ===========================================================================
* Copyright (c) 1998, 2011, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/common/CashDrawerClosedAisle.java /main/12 2011/02/16 09:13:25 cgreene Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    cgreene   02/15/11 - move constants into interfaces and refactor
 *    cgreene   05/27/10 - convert to oracle packaging
 *    cgreene   05/27/10 - convert to oracle packaging
 *    cgreene   05/26/10 - convert to oracle packaging
 *    abondala  01/03/10 - update header date
 *
 * ===========================================================================
 * $Log:
 *    3    360Commerce 1.2         3/31/2005 4:27:21 PM   Robert Pearse   
 *    2    360Commerce 1.1         3/10/2005 10:20:01 AM  Robert Pearse   
 *    1    360Commerce 1.0         2/11/2005 12:09:50 PM  Robert Pearse   
 *
 *   Revision 1.3  2004/02/12 16:49:08  mcs
 *   Forcing head revision
 *
 *   Revision 1.2  2004/02/11 21:38:50  rhafernik
 *   @scr 0 Log4J conversion and code cleanup
 *
 *   Revision 1.1.1.1  2004/02/11 01:04:14  cschellenger
 *   updating to pvcs 360store-current
 *
 *
 * 
 *    Rev 1.0   Aug 29 2003 15:54:08   CSchellenger
 * Initial revision.
 * 
 *    Rev 1.1   Jul 14 2003 15:09:12   mrm
 * Print after closing drawer
 * Resolution for POS SCR-3042: Device is Offline message does not appear during No Sale with Drawer offline
 * 
 *    Rev 1.0   Apr 29 2002 15:34:28   msg
 * Initial revision.
 * 
 *    Rev 1.1   Mar 18 2002 23:08:20   msg
 * - updated copyright
 * 
 *    Rev 1.0   Mar 18 2002 11:22:08   msg
 * Initial revision.
 * 
 *    Rev 1.0   Sep 21 2001 11:13:26   msg
 * Initial revision.
 * 
 *    Rev 1.1   Sep 17 2001 13:06:30   msg
 * header update
 * ===========================================================================
 */
package oracle.retail.stores.pos.services.common;

import oracle.retail.stores.foundation.tour.application.Letter;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.pos.services.PosLaneActionAdapter;

/**
 * This aisle is traversed when the cash drawer responds as closed.
 * 
 * @version $Revision: /main/12 $
 */
public class CashDrawerClosedAisle extends PosLaneActionAdapter
{
    private static final long serialVersionUID = 5420954666784240988L;
    /**
     * revision number supplied by Team Connection
     */
    public static final String revisionNumber = "$Revision: /main/12 $";

    /**
     * Set statuses and mails a letter if neccesary.
     * 
     * @param bus Service Bus
     */
    @Override
    public void traverse(BusIfc bus)
    {
        CashDrawerCargoIfc cargo = (CashDrawerCargoIfc) bus.getCargo();
        cargo.setCashDrawerHasBeenClosed(true);
        bus.mail(new Letter(CommonLetterIfc.PRINT), BusIfc.CURRENT);
    }

    /**
     * Returns the revision number of the class.
     * 
     * @return String representation of revision number
     */
    public String getRevisionNumber()
    {
        // return string
        return (revisionNumber);
    }
}