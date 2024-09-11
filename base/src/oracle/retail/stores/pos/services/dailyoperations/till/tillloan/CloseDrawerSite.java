/* ===========================================================================
* Copyright (c) 1998, 2011, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/dailyoperations/till/tillloan/CloseDrawerSite.java /rgbustores_13.4x_generic_branch/1 2011/05/05 14:06:17 mszekely Exp $
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
 *    3    360Commerce 1.2         3/31/2005 4:27:28 PM   Robert Pearse   
 *    2    360Commerce 1.1         3/10/2005 10:20:17 AM  Robert Pearse   
 *    1    360Commerce 1.0         2/11/2005 12:10:02 PM  Robert Pearse   
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
 *    Rev 1.0   Aug 29 2003 15:57:46   CSchellenger
 * Initial revision.
 * 
 *    Rev 1.0   Apr 29 2002 15:28:00   msg
 * Initial revision.
 * 
 *    Rev 1.0   Mar 18 2002 11:29:06   msg
 * Initial revision.
 * 
 *    Rev 1.0   Sep 21 2001 11:18:26   msg
 * Initial revision.
 * 
 *    Rev 1.1   Sep 17 2001 13:14:32   msg
 * header update
 * ===========================================================================
 */
package oracle.retail.stores.pos.services.dailyoperations.till.tillloan;
// Bedrock imports
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.pos.services.PosSiteActionAdapter;
import oracle.retail.stores.pos.services.dailyoperations.till.tilloptions.TillCashDrawer;

//------------------------------------------------------------------------------
/**
    Attempts to close the cash drawer after counting the float by calling
    TillCashDrawer.tillCloseCashDrawer
    <P>
    @version $Revision: /rgbustores_13.4x_generic_branch/1 $
    @see TillCashDrawer
**/
//------------------------------------------------------------------------------

public class CloseDrawerSite extends PosSiteActionAdapter
{
    /** revision number for this class */
    public static final String revisionNumber = "$Revision: /rgbustores_13.4x_generic_branch/1 $";

    //--------------------------------------------------------------------------
    /**
       Calls TillCashDrawer.tillCloseCashDrawer to close the cash drawer.
       <P>
       @param bus the bus arriving at this site
    **/
    //--------------------------------------------------------------------------
    public void arrive(BusIfc bus)
    {
        // calls TillCashDrawer.tillCloseCashDrawer
        TillCashDrawer.tillCloseCashDrawer(bus);
    }
}
