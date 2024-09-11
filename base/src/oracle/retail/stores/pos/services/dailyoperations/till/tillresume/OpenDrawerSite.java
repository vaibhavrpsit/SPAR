/* ===========================================================================
* Copyright (c) 1998, 2011, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/dailyoperations/till/tillresume/OpenDrawerSite.java /main/10 2011/02/16 09:13:26 cgreene Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    cgreene   02/15/11 - move constants into interfaces and refactor
 *    cgreene   05/26/10 - convert to oracle packaging
 *    abondala  01/03/10 - update header date
 *
 * ===========================================================================
 * $Log:
 *    4    360Commerce 1.3         3/13/2008 11:25:43 AM  Mathews Kochummen
 *         forward port from v12x to trunk. reviewed by michael
 *    3    360Commerce 1.2         3/31/2005 4:29:11 PM   Robert Pearse   
 *    2    360Commerce 1.1         3/10/2005 10:23:47 AM  Robert Pearse   
 *    1    360Commerce 1.0         2/11/2005 12:12:50 PM  Robert Pearse   
 *
 *   Revision 1.4  2004/04/12 17:37:48  dcobb
 *   @scr 4205 Feature Enhancement: Till Options
 *   Removed redundant methods.
 *
 *   Revision 1.3  2004/02/12 16:50:08  mcs
 *   Forcing head revision
 *
 *   Revision 1.2  2004/02/11 21:45:18  rhafernik
 *   @scr 0 Log4J conversion and code cleanup
 *
 *   Revision 1.1.1.1  2004/02/11 01:04:16  cschellenger
 *   updating to pvcs 360store-current
 *
 *
 * 
 *    Rev 1.0   Aug 29 2003 15:58:34   CSchellenger
 * Initial revision.
 * 
 *    Rev 1.2   Mar 04 2003 17:15:36   RSachdeva
 * Clean Up Code Conversion
 * Resolution for POS SCR-1740: Code base Conversions
 * 
 *    Rev 1.1   Aug 27 2002 14:55:14   RSachdeva
 * Code conversion
 * Resolution for POS SCR-1740: Code base Conversions
 * 
 *    Rev 1.0   Apr 29 2002 15:25:56   msg
 * Initial revision.
 * 
 *    Rev 1.0   Mar 18 2002 11:31:00   msg
 * Initial revision.
 * 
 *    Rev 1.0   Sep 21 2001 11:20:02   msg
 * Initial revision.
 * 
 *    Rev 1.1   Sep 17 2001 13:15:10   msg
 * header update
 * ===========================================================================
 */
package oracle.retail.stores.pos.services.dailyoperations.till.tillresume;

import oracle.retail.stores.domain.financial.AbstractStatusEntityIfc;
import oracle.retail.stores.domain.financial.DrawerIfc;
import oracle.retail.stores.foundation.tour.application.Letter;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.pos.manager.ifc.UtilityManagerIfc;
import oracle.retail.stores.pos.services.PosSiteActionAdapter;
import oracle.retail.stores.pos.services.common.CommonLetterIfc;
import oracle.retail.stores.pos.services.dailyoperations.till.tilloptions.TillCashDrawer;

/**
 * Attempts to open the cash drawer to allow resuming of the Till by calling
 * TillCashDrawer.tillOpenCashDrawer
 * 
 * @version $Revision: /main/10 $
 * @see TillCashDrawer
 */
public class OpenDrawerSite extends PosSiteActionAdapter
{
    private static final long serialVersionUID = -6844156156451952676L;

    /**
     * revision number supplied by Team Connection
     */
    public static final String revisionNumber = "$Revision: /main/10 $";

    /**
     * openDrawerSite
     */
    public static final String SITENAME = "OpenDrawerSite";

    /**
     * Cash Drawer Retry Continue Cancel resume tag
     */
    public static final String CASH_DRAWER_RETRY_CONT_CANCEL_RESUME_TAG = "CashDrawerRetryContinueCancel.resume";

    /**
     * Cash Drawer Retry Continue Cancel resume default text
     */
    public static final String CASH_DRAWER_RETRY_CONT_CANCEL_RESUME_TEXT = "the till resume";

    /**
     * Calls TillCashDrawer.tillOpenCashDrawer to open the cash drawer.
     * 
     * @param bus the bus arriving at this site
     */
    @Override
    public void arrive(BusIfc bus)
    {
	    //skip drawer open if the till is still in
    	if (((TillResumeCargo)(bus.getCargo())).getRegister().getDrawer(DrawerIfc.DRAWER_PRIMARY)
    	        .getDrawerStatus() == AbstractStatusEntityIfc.DRAWER_STATUS_OCCUPIED)
    	{
    		bus.mail(new Letter(CommonLetterIfc.SKIP), BusIfc.CURRENT);
    	}
    	else
    	{
        // calls TillCashDrawer.tillOpenCashDrawer
        UtilityManagerIfc utility = 
          (UtilityManagerIfc) bus.getManager(UtilityManagerIfc.TYPE);
        TillCashDrawer.
          tillOpenCashDrawer(bus,utility.retrieveDialogText(CASH_DRAWER_RETRY_CONT_CANCEL_RESUME_TAG,
                                                            CASH_DRAWER_RETRY_CONT_CANCEL_RESUME_TEXT));
    	}
    }
}