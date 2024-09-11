/* ===========================================================================
* Copyright (c) 1998, 2013, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/order/picklist/CountOrderSummariesSite.java /main/1 2013/01/10 14:03:54 sgu Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    sgu       01/04/13 - add new class
 *    sgu       01/03/13 - rename the class for xc only
 *    sgu       01/03/13 - add back order pick list flow
 *    cgreene   02/15/11 - move constants into interfaces and refactor
 *    cgreene   05/26/10 - convert to oracle packaging
 *    abondala  01/03/10 - update header date
 *
 * ===========================================================================
 * $Log:
 *    3    360Commerce 1.2         3/31/2005 4:27:31 PM   Robert Pearse   
 *    2    360Commerce 1.1         3/10/2005 10:20:24 AM  Robert Pearse   
 *    1    360Commerce 1.0         2/11/2005 12:10:13 PM  Robert Pearse   
 *
 *   Revision 1.3  2004/02/12 16:51:25  mcs
 *   Forcing head revision
 *
 *   Revision 1.2  2004/02/11 21:51:49  rhafernik
 *   @scr 0 Log4J conversion and code cleanup
 *
 *   Revision 1.1.1.1  2004/02/11 01:04:19  cschellenger
 *   updating to pvcs 360store-current
 *
 *
 * 
 *    Rev 1.0   Aug 29 2003 16:03:44   CSchellenger
 * Initial revision.
 * 
 *    Rev 1.0   Apr 29 2002 15:12:12   msg
 * Initial revision.
 * 
 *    Rev 1.0   Mar 18 2002 11:41:32   msg
 * Initial revision.
 * 
 *    Rev 1.0   Sep 24 2001 13:01:16   MPM
 * Initial revision.
 * 
 *    Rev 1.1   Sep 17 2001 13:10:38   msg
 * header update
 * ===========================================================================
 */
package oracle.retail.stores.pos.services.order.picklist;

import oracle.retail.stores.foundation.tour.application.Letter;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.pos.services.PosSiteActionAdapter;
import oracle.retail.stores.pos.services.common.CommonLetterIfc;
import oracle.retail.stores.pos.services.order.common.OrderCargo;

/**
 * Prints the Order.
 * 
 * @version $Revision: /main/1 $
 */
public class CountOrderSummariesSite extends PosSiteActionAdapter
{
    private static final long serialVersionUID = -5958663078972291717L;

    public static final String SITENAME = "CountOrderSummariesSite";

    /**
     * Mails a Next letter if OrderSummaries remain in cargo or Undo letter
     * otherwise.
     * 
     * @param bus the bus arriving at this site
     */
    @Override
    public void arrive(BusIfc bus)
    {
        Letter letter = null;
        OrderCargo cargo = (OrderCargo) bus.getCargo();

        if (cargo.countSummaries() > 0)
        {
            letter = new Letter(CommonLetterIfc.NEXT);
        }
        else
        {
            letter = new Letter(CommonLetterIfc.UNDO);
        }

        bus.mail(letter, BusIfc.CURRENT);
    }

}
