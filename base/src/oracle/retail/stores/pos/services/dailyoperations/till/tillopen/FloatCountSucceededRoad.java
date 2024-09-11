/* ===========================================================================
* Copyright (c) 1998, 2011, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/dailyoperations/till/tillopen/FloatCountSucceededRoad.java /rgbustores_13.4x_generic_branch/1 2011/05/05 14:06:21 mszekely Exp $
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
 *    3    360Commerce 1.2         3/31/2005 4:28:12 PM   Robert Pearse   
 *    2    360Commerce 1.1         3/10/2005 10:21:44 AM  Robert Pearse   
 *    1    360Commerce 1.0         2/11/2005 12:11:07 PM  Robert Pearse   
 *
 *   Revision 1.4  2004/07/07 18:27:11  dcobb
 *   @scr 1734 Wrong error message when attempt to open another till in reg acct.
 *   Fixed in CheckTillStatusSite. Moved deprecated TillOpenCargo to the deprecation tree and imported new TillCargo from _360commerce tree..
 *
 *   Revision 1.3  2004/02/12 16:50:00  mcs
 *   Forcing head revision
 *
 *   Revision 1.2  2004/02/11 21:46:45  rhafernik
 *   @scr 0 Log4J conversion and code cleanup
 *
 *   Revision 1.1.1.1  2004/02/11 01:04:15  cschellenger
 *   updating to pvcs 360store-current
 *
 *
 * 
 *    Rev 1.0   Aug 29 2003 15:57:56   CSchellenger
 * Initial revision.
 * 
 *    Rev 1.0   Apr 29 2002 15:27:32   msg
 * Initial revision.
 * 
 *    Rev 1.0   Mar 18 2002 11:29:30   msg
 * Initial revision.
 * 
 *    Rev 1.0   Sep 21 2001 11:18:44   msg
 * Initial revision.
 * 
 *    Rev 1.1   Sep 17 2001 13:14:42   msg
 * header update
 * ===========================================================================
 */
package oracle.retail.stores.pos.services.dailyoperations.till.tillopen;

import oracle.retail.stores.pos.services.dailyoperations.till.tillopen.TillOpenCargo;
import oracle.retail.stores.domain.financial.FinancialTotalsIfc;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.pos.services.PosLaneActionAdapter;

//------------------------------------------------------------------------------
/**

    @version $Revision: /rgbustores_13.4x_generic_branch/1 $
**/
//------------------------------------------------------------------------------
public class FloatCountSucceededRoad extends PosLaneActionAdapter
{

    public static final String LANENAME = "FloatCountSucceededRoad";

    //--------------------------------------------------------------------------
    /**


       @param bus the bus traversing this lane
    **/
    //--------------------------------------------------------------------------
    public void traverse(BusIfc bus)
    {

        // Count was a success, add financial count to till

        TillOpenCargo cargo = (TillOpenCargo)bus.getCargo();

        FinancialTotalsIfc ft = cargo.getFloatTotals();

        cargo.getTill().addTotals(ft);
        cargo.getRegister().addTotals(ft);


    }

    //--------------------------------------------------------------------------
    /**


       @param bus the bus traversing this lane
    **/
    //--------------------------------------------------------------------------
    public void backup(BusIfc bus)
    {




    }

}
