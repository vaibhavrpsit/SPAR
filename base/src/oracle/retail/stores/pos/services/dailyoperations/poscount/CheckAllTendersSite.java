/* ===========================================================================
* Copyright (c) 1998, 2011, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/dailyoperations/poscount/CheckAllTendersSite.java /main/15 2011/02/16 09:13:26 cgreene Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    cgreene   02/15/11 - move constants into interfaces and refactor
 *    jswan     12/10/10 - Fixed issue with BlindCount at Till Reconcile whith
 *                         tenders which are not in the tenders to be counted
 *                         list.
 *    abhayg    10/15/10 - Fix for TILL SUMMARY REPORTS DO NOT SHOW A SHORTAGE
 *                         OF CHECKS
 *    cgreene   05/27/10 - convert to oracle packaging
 *    cgreene   05/26/10 - convert to oracle packaging
 *    abondala  01/03/10 - update header date
 *    jswan     07/10/09 - Fix issues with Detail Tender Count and foreign
 *                         currency.
 *    jswan     07/10/09 - XbranchMerge jswan_bug-8669309 from
 *                         rgbustores_13.1x_branch
 *    jswan     07/09/09 - Fixed issues with detailed till tender counts;
 *                         foreign currency tender was causing base currency
 *                         tender to be counted twice. Also when the operator
 *                         did not count the cash, the reconciled = 0.
 *
 * ===========================================================================
 * $Log:
 *    3    360Commerce 1.2         3/31/2005 4:27:23 PM   Robert Pearse   
 *    2    360Commerce 1.1         3/10/2005 10:20:04 AM  Robert Pearse   
 *    1    360Commerce 1.0         2/11/2005 12:09:53 PM  Robert Pearse   
 *
 *   Revision 1.6  2004/09/23 00:07:14  kmcbride
 *   @scr 7211: Adding static serialVersionUIDs to all POS Serializable objects, minus the JComponents
 *
 *   Revision 1.5  2004/06/18 22:19:34  dcobb
 *   @scr 4205 Feature Enhancement: Till Options
 *   Add Foreign currency count.
 *
 *   Revision 1.4  2004/06/07 18:29:38  dcobb
 *   @scr 4204 Feature Enhancement: Till Options
 *   Add foreign currency counts.
 *
 *   Revision 1.3  2004/02/12 16:49:38  mcs
 *   Forcing head revision
 *
 *   Revision 1.2  2004/02/11 21:45:40  rhafernik
 *   @scr 0 Log4J conversion and code cleanup
 *
 *   Revision 1.1.1.1  2004/02/11 01:04:15  cschellenger
 *   updating to pvcs 360store-current
 *
 *
 * 
 *    Rev 1.0   Aug 29 2003 15:56:40   CSchellenger
 * Initial revision.
 * 
 *    Rev 1.0   Apr 29 2002 15:30:14   msg
 * Initial revision.
 * 
 *    Rev 1.1   Mar 18 2002 23:14:12   msg
 * - updated copyright
 * 
 *    Rev 1.0   Mar 18 2002 11:26:58   msg
 * Initial revision.
 * 
 *    Rev 1.2   07 Jan 2002 13:01:54   epd
 * removed unused function call
 * Resolution for POS SCR-216: Making POS changes to accommodate OnlineOffice
 * 
 *    Rev 1.1   02 Jan 2002 15:40:14   epd
 * removed code which checks which tenders have been counted
 * Resolution for POS SCR-216: Making POS changes to accommodate OnlineOffice
 * 
 *    Rev 1.0   Sep 21 2001 11:16:58   msg
 * Initial revision.
 * 
 *    Rev 1.1   Sep 17 2001 13:11:26   msg
 * header update
 * ===========================================================================
 */
package oracle.retail.stores.pos.services.dailyoperations.poscount;

import oracle.retail.stores.foundation.tour.application.Letter;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.foundation.tour.ifc.SiteActionIfc;
import oracle.retail.stores.pos.services.PosSiteActionAdapter;
import oracle.retail.stores.pos.services.common.CommonLetterIfc;
import oracle.retail.stores.pos.ui.beans.CurrencyDetailBeanModel;

/**
 * Check to make sure that all tender have been counted.
 * 
 * @version $Revision: /main/15 $
 */
public class CheckAllTendersSite extends PosSiteActionAdapter implements SiteActionIfc
{
    // This id is used to tell the compiler not to generate a new serialVersionUID.
    static final long serialVersionUID = -8961678876151073252L;

    /**
     * revision number
     **/
    public static final String revisionNumber = "$Revision: /main/15 $";

    /**
     * Site name for logging
     */
    public static final String SITENAME = "CheckAllTendersSite";

    /**
     * Updates the totals with the counted amounts when counting the till.
     * 
     * @param bus the bus arriving at this site
     */
    @Override
    public void arrive(BusIfc bus)
    {
        // Get the cargo
        PosCountCargo cargo = (PosCountCargo) bus.getCargo();

        // If the count is for float, loan or pick, exit.
        if (cargo.getCountType() != PosCountCargo.TILL)
        {
            bus.mail(new Letter(CommonLetterIfc.SUCCESS), BusIfc.CURRENT);
        }
        else
        // Count is for Till; perform the check.
        {
            cargo.setTillReconcileValuesFromParameters(bus);
            if (cargo.getSummaryFlag())
            {
                cargo.updateTillSummaryInTotals();
            }
            else
            {
                // update the cash detail counts
                CurrencyDetailBeanModel[] model = cargo.getCashCurrencyDetailBeanModels();
                for (int i = 0; i < model.length; i++)
                {
                    cargo.updateTillCashDetailAmountInTotals(model[i]);
                }
                // clear the cash detail hashtable for use with foreign
                // currencies
                cargo.resetCurrencyDetailBeanModels();

                // update the other tender detail counts
                cargo.updateTillTenderDetailAmountsInTotals();
                // clear the other tender detail map for use with foreign
                // currencies
                cargo.resetTenderDetails();
            }
            bus.mail(new Letter(CommonLetterIfc.SUCCESS), BusIfc.CURRENT);
        }
    }
}
