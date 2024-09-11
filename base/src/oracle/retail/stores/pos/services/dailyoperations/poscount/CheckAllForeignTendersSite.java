/* ===========================================================================
* Copyright (c) 2004, 2011, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/dailyoperations/poscount/CheckAllForeignTendersSite.java /main/14 2011/02/16 09:13:26 cgreene Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    cgreene   02/15/11 - move constants into interfaces and refactor
 *    jswan     12/10/10 - Fixed comments.
 *    jswan     12/10/10 - Fixed issue with BlindCount at Till Reconcile whith
 *                         tenders which are not in the tenders to be counted
 *                         list.
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
 *    6    360Commerce1.5         4/10/2007 2:55:10 PM   Michael Boyd    CR
 *         26172 - v7.2.2 merge to trunk
 *
 *
 *         6    .v7x      1.4.1.0     7/27/2006 8:04:51 AM   Keith L. Lesikar
 *         Do not
 *         check charge models when reconciling foreign currencies.
 *    5    360Commerce1.4         1/25/2006 4:10:51 PM   Brett J. Larsen merge
 *         7.1.1 changes (aka. 7.0.3 fixes) into 360Commerce view
 *    4    360Commerce1.3         12/13/2005 4:42:39 PM  Barry A. Pape
 *         Base-lining of 7.1_LA
 *    3    360Commerce1.2         3/31/2005 4:27:23 PM   Robert Pearse   
 *    2    360Commerce1.1         3/10/2005 10:20:04 AM  Robert Pearse   
 *    1    360Commerce1.0         2/11/2005 12:09:53 PM  Robert Pearse   
 *:
 *    6    .v710     1.2.2.1     10/24/2005 15:38:30    Charles Suehs   Merge
 *         for 3977 .v700 to .v710 .
 *    5    .v710     1.2.2.0     10/24/2005 15:18:51    Charles Suehs   Merge
 *         from CheckAllForeignTendersSite.java, Revision 1.2.1.0
 *    4    .v700     1.2.1.0     10/2/2005 16:54:18     Rohit Sachdeva  Till
 *         Reconcile- Foreign currency amounts are not updated on Foreign
 *         Currency Count screen
 *    3    360Commerce1.2         3/31/2005 15:27:23     Robert Pearse
 *    2    360Commerce1.1         3/10/2005 10:20:04     Robert Pearse
 *    1    360Commerce1.0         2/11/2005 12:09:53     Robert Pearse
 *
 *   Revision 1.2  2004/06/18 22:19:34  dcobb
 *   @scr 4205 Feature Enhancement: Till Options
 *   Add Foreign currency count.
 *
 *   Revision 1.1  2004/06/10 16:24:06  dcobb
 *   @scr 4204 Feature Enhancement: Till Options
 *   Add foreign currency count.
 *
 * ===========================================================================
 */
package oracle.retail.stores.pos.services.dailyoperations.poscount;

import oracle.retail.stores.commerceservices.common.currency.CurrencyTypeIfc;
import oracle.retail.stores.domain.DomainGateway;
import oracle.retail.stores.foundation.tour.application.Letter;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.pos.services.PosSiteActionAdapter;
import oracle.retail.stores.pos.services.common.CommonLetterIfc;
import oracle.retail.stores.pos.ui.beans.CurrencyDetailBeanModel;

/**
 * Update all foreign tender counts.
 * 
 * @version $Revision: /main/14 $
 */
public class CheckAllForeignTendersSite extends PosSiteActionAdapter
{
    private static final long serialVersionUID = -2892627841806397484L;
    /**
     * revision number
     */
    public static String revisionNumber = "$Revision: /main/14 $";

    /**
     * Updates the totals with the counted amounts when counting the till.
     * 
     * @param bus the bus arriving at this site
     */
    @Override
    public void arrive(BusIfc bus)
    {
        // Get the cargo
        PosCountCargo cargo = (PosCountCargo)bus.getCargo();

        // If the count is for float, loan or pick, exit.
        if (cargo.getCountType() != PosCountCargo.TILL)
        {
            bus.mail(new Letter(CommonLetterIfc.SUCCESS), BusIfc.CURRENT);
        }
        else // Count is for Till; perform the check.
        {
            cargo.setTillReconcileValuesFromParameters(bus);
            if (cargo.getSummaryFlag())
            {
                //Taking into account all the currencies that were entered/expected during reconcile process
                CurrencyTypeIfc[]  altCurrencies = DomainGateway.getAlternateCurrencyTypes();
                for (int i = 0; i < altCurrencies.length; i++)
                {
                    cargo.setCurrentForeignCurrency(altCurrencies[i].getCurrencyCode());
                    // set the tender models for the current foreign currency
                    cargo.setTenderModels(cargo.getForeignTenderModelsForTillCount());
                    // update the financial totals from the tender models
                    cargo.updateTillSummaryInTotalsNoChargeModels();
                }
            }
            else
            {
                // update cash detail count for all foreign currencies
                CurrencyDetailBeanModel[] model = cargo.getForeignCashCurrencyDetailBeanModels();
                for(int i = 0; i < model.length; i++)
                {
                    cargo.updateCashDetailAmountInTotals(model[i]);
                }
                //Taking into account all the currencies that were entered/expected during reconcile process
                CurrencyTypeIfc[]  altCurrencies = DomainGateway.getAlternateCurrencyTypes();
                for (int i = 0; i < altCurrencies.length; i++)
                {
                    cargo.setCurrentForeignCurrency(altCurrencies[i].getCurrencyCode());
                    // update the other tender detail counts
                    cargo.updateTillTenderDetailAmountsInTotals();
                }
            }
            bus.mail(new Letter(CommonLetterIfc.SUCCESS), BusIfc.CURRENT);
        }
    }
}
