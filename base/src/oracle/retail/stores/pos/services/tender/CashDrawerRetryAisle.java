/* ===========================================================================
* Copyright (c) 1998, 2011, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/tender/CashDrawerRetryAisle.java /rgbustores_13.4x_generic_branch/1 2011/05/05 14:05:48 mszekely Exp $
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
 *    3    360Commerce 1.2         3/31/2005 4:27:22 PM   Robert Pearse   
 *    2    360Commerce 1.1         3/10/2005 10:20:02 AM  Robert Pearse   
 *    1    360Commerce 1.0         2/11/2005 12:09:50 PM  Robert Pearse   
 *
 *   Revision 1.3  2004/02/12 16:48:22  mcs
 *   Forcing head revision
 *
 *   Revision 1.2  2004/02/11 21:22:51  rhafernik
 *   @scr 0 Log4J conversion and code cleanup
 *
 *   Revision 1.1.1.1  2004/02/11 01:04:12  cschellenger
 *   updating to pvcs 360store-current
 *
 *
 * 
 *    Rev 1.0   Nov 04 2003 11:17:36   epd
 * Initial revision.
 * 
 *    Rev 1.0   Oct 23 2003 17:29:44   epd
 * Initial revision.
 * 
 *    Rev 1.0   Oct 17 2003 13:06:42   epd
 * Initial revision.
 *   
 * ===========================================================================
 */
package oracle.retail.stores.pos.services.tender;

// foundation imports
import oracle.retail.stores.foundation.tour.application.Letter;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.pos.services.PosLaneActionAdapter;

//--------------------------------------------------------------------------
/**
    This aisle is traversed when the user requests to try to open the
    cash drawer again.

     <P>
     @version $Revision: /rgbustores_13.4x_generic_branch/1 $
**/
//--------------------------------------------------------------------------
public class CashDrawerRetryAisle extends PosLaneActionAdapter
{
    /**
       revision number supplied by Team Connection
    **/
    public static final String revisionNumber = "$Revision: /rgbustores_13.4x_generic_branch/1 $";

    //----------------------------------------------------------------------
    /**
       Send the Open letter
       @param  bus     Service Bus
    **/
    //----------------------------------------------------------------------
    public void traverse(BusIfc bus)
    {

        bus.mail(new Letter("Open"), BusIfc.CURRENT);

    }

}
