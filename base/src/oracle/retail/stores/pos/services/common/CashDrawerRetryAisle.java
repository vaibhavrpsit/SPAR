/* ===========================================================================
* Copyright (c) 1998, 2011, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/common/CashDrawerRetryAisle.java /rgbustores_13.4x_generic_branch/1 2011/05/05 14:05:53 mszekely Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    cgreene   05/27/10 - convert to oracle packaging
 *    cgreene   05/27/10 - convert to oracle packaging
 *    cgreene   05/26/10 - convert to oracle packaging
 *    abondala  01/03/10 - update header date
 *
 * ===========================================================================
 * $Log:
 *    3    360Commerce 1.2         3/31/2005 4:27:22 PM   Robert Pearse   
 *    2    360Commerce 1.1         3/10/2005 10:20:02 AM  Robert Pearse   
 *    1    360Commerce 1.0         2/11/2005 12:09:50 PM  Robert Pearse   
 *
 *   Revision 1.4  2004/03/03 23:15:06  bwf
 *   @scr 0 Fixed CommonLetterIfc deprecations.
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
 *    Rev 1.0   Aug 29 2003 15:54:12   CSchellenger
 * Initial revision.
 * 
 *    Rev 1.0   Apr 29 2002 15:34:34   msg
 * Initial revision.
 * 
 *    Rev 1.1   Mar 18 2002 23:08:28   msg
 * - updated copyright
 * 
 *    Rev 1.0   Mar 18 2002 11:22:14   msg
 * Initial revision.
 * 
 *    Rev 1.0   Sep 21 2001 11:13:20   msg
 * Initial revision.
 * 
 *    Rev 1.1   Sep 17 2001 13:06:28   msg
 * header update
 * ===========================================================================
 */
package oracle.retail.stores.pos.services.common;
// foundation imports
import oracle.retail.stores.pos.services.common.CommonLetterIfc;
import oracle.retail.stores.foundation.tour.application.Letter;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.pos.services.PosLaneActionAdapter;

//--------------------------------------------------------------------------
/**
    This aisle is traversed when the user requests to try to open the
    cash drawer again.
    @version $Revision: /rgbustores_13.4x_generic_branch/1 $
**/
//--------------------------------------------------------------------------
public class CashDrawerRetryAisle extends PosLaneActionAdapter
{
    /**
       revision number supplied by source-code control system
    **/
    public static final String revisionNumber = "$Revision: /rgbustores_13.4x_generic_branch/1 $";

    //----------------------------------------------------------------------
    /**
       Sends a cash-drawer-open letter. <P>
       @param  bus     Service Bus
    **/
    //----------------------------------------------------------------------
    public void traverse(BusIfc bus)
    {

        bus.mail(new Letter(CommonLetterIfc.OPEN_CASH_DRAWER), BusIfc.CURRENT);

    }

}
