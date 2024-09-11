/* ===========================================================================
* Copyright (c) 1998, 2011, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/dailyoperations/till/tillloan/LoanCountSucceededRoad.java /rgbustores_13.4x_generic_branch/1 2011/05/05 14:06:17 mszekely Exp $
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
 *    3    360Commerce 1.2         3/31/2005 4:28:52 PM   Robert Pearse   
 *    2    360Commerce 1.1         3/10/2005 10:23:08 AM  Robert Pearse   
 *    1    360Commerce 1.0         2/11/2005 12:12:20 PM  Robert Pearse   
 *
 *   Revision 1.5  2004/09/23 00:07:12  kmcbride
 *   @scr 7211: Adding static serialVersionUIDs to all POS Serializable objects, minus the JComponents
 *
 *   Revision 1.4  2004/04/30 18:16:03  dcobb
 *   @scr 4098 Open drawer before detail count screens.
 *   Loan changed to open drawer before detail count screens.
 *
 *   Revision 1.3  2004/02/12 16:49:59  mcs
 *   Forcing head revision
 *
 *   Revision 1.2  2004/02/11 21:46:54  rhafernik
 *   @scr 0 Log4J conversion and code cleanup
 *
 *   Revision 1.1.1.1  2004/02/11 01:04:15  cschellenger
 *   updating to pvcs 360store-current
 *
 *
 * 
 *    Rev 1.0   Aug 29 2003 15:57:48   CSchellenger
 * Initial revision.
 * 
 *    Rev 1.0   Apr 29 2002 15:28:02   msg
 * Initial revision.
 * 
 *    Rev 1.0   Mar 18 2002 11:29:08   msg
 * Initial revision.
 * 
 *    Rev 1.0   Sep 21 2001 11:18:30   msg
 * Initial revision.
 * 
 *    Rev 1.1   Sep 17 2001 13:14:32   msg
 * header update
 * ===========================================================================
 */
package oracle.retail.stores.pos.services.dailyoperations.till.tillloan;

// foundation imports
import oracle.retail.stores.domain.financial.FinancialTotalsIfc;
import oracle.retail.stores.foundation.tour.application.LaneActionAdapter;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.foundation.tour.ifc.LaneActionIfc;

//------------------------------------------------------------------------------
/**

    @version $Revision: /rgbustores_13.4x_generic_branch/1 $
**/
//------------------------------------------------------------------------------

public class LoanCountSucceededRoad extends LaneActionAdapter implements LaneActionIfc
{
    // This id is used to tell
    // the compiler not to generate a
    // new serialVersionUID.
    //
    static final long serialVersionUID = 9188775374394018110L;


    public static final String LANENAME = "LoanCountSucceededRoad";

    //--------------------------------------------------------------------------
    /**


       @param bus the bus traversing this lane
    **/
    //--------------------------------------------------------------------------
    public void traverse(BusIfc bus)
    {

        // Count was a success, add financial count to till
        TillLoanCargo cargo = (TillLoanCargo)bus.getCargo();

        FinancialTotalsIfc ft = cargo.getLoanTotals();

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
