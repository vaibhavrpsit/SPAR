/* ===========================================================================
* Copyright (c) 2004, 2011, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/dailyoperations/poscount/DisplayForeignCurrencyCountSite.java /rgbustores_13.4x_generic_branch/1 2011/05/05 14:06:23 mszekely Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    cgreene   05/26/10 - convert to oracle packaging
 *    abondala  01/03/10 - update header date
 *    glwang    11/18/08 - forward port from sousingh_bug-7284731
 *
 * ===========================================================================
 * $Log:
 *    6    360Commerce 1.5         4/25/2007 8:52:32 AM   Anda D. Cadar   I18N
 *         merge
 *
 *    5    360Commerce 1.4         1/25/2006 4:10:58 PM   Brett J. Larsen merge
 *          7.1.1 changes (aka. 7.0.3 fixes) into 360Commerce view
 *    4    360Commerce 1.3         12/13/2005 4:42:39 PM  Barry A. Pape
 *         Base-lining of 7.1_LA
 *    3    360Commerce 1.2         3/31/2005 4:27:48 PM   Robert Pearse
 *    2    360Commerce 1.1         3/10/2005 10:21:03 AM  Robert Pearse
 *    1    360Commerce 1.0         2/11/2005 12:10:38 PM  Robert Pearse
 *:
 *    6    .v710     1.2.2.1     10/24/2005 15:38:30    Charles Suehs   Merge
 *         for 3977 .v700 to .v710 .
 *    5    .v710     1.2.2.0     10/24/2005 15:21:41    Charles Suehs   Merge
 *         from DisplayForeignCurrencyCountSite.java, Revision 1.2.1.0
 *    4    .v700     1.2.1.0     10/2/2005 16:54:36     Rohit Sachdeva  Till
 *         Reconcile- Foreign currency amounts are not updated on Foreign
 *         Currency Count screen
 *    3    360Commerce1.2         3/31/2005 15:27:48     Robert Pearse
 *    2    360Commerce1.1         3/10/2005 10:21:03     Robert Pearse
 *    1    360Commerce1.0         2/11/2005 12:10:38     Robert Pearse
 *
 *   Revision 1.3  2004/08/23 16:16:00  cdb
 *   @scr 4204 Removed tab characters
 *
 *   Revision 1.2  2004/06/07 18:29:38  dcobb
 *   @scr 4204 Feature Enhancement: Till Options
 *   Add foreign currency counts.
 *
 *   Revision 1.1  2004/05/20 22:48:39  dcobb
 *   @scr 4204 Feature Enhancement: Till Options
 *   Added Foreign Currency To Count dialog.
 *
 * ===========================================================================
 */
package oracle.retail.stores.pos.services.dailyoperations.poscount;

import oracle.retail.stores.commerceservices.common.currency.CurrencyIfc;
import oracle.retail.stores.commerceservices.common.currency.CurrencyTypeIfc;
import oracle.retail.stores.domain.DomainGateway;
import oracle.retail.stores.foundation.manager.ifc.UIManagerIfc;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.pos.config.bundles.BundleConstantsIfc;
import oracle.retail.stores.pos.manager.ifc.UtilityManagerIfc;
import oracle.retail.stores.pos.services.PosSiteActionAdapter;
import oracle.retail.stores.pos.ui.POSUIManagerIfc;
import oracle.retail.stores.pos.ui.beans.NavigationButtonBeanModel;
import oracle.retail.stores.pos.ui.beans.SummaryCountBeanModel;
import oracle.retail.stores.pos.ui.beans.SummaryForeignCurrencyCountMenuBeanModel;


//------------------------------------------------------------------------------
/**
     Checks to see if foreign currency has been collected into the till. This
     site is visited only if the count type is TILL.
     @version $Revision: /rgbustores_13.4x_generic_branch/1 $
**/
//------------------------------------------------------------------------------

public class DisplayForeignCurrencyCountSite extends PosSiteActionAdapter
{
    /** revision number */
    public static String revisionNumber = "$Revision: /rgbustores_13.4x_generic_branch/1 $";

    //--------------------------------------------------------------------------
    /**
        Displays the Foreign Currency Count screen.
        @param bus the bus arriving at this site
    **/
    //--------------------------------------------------------------------------
    public void arrive(BusIfc bus)
    {                                   // begin arrive()
        // Get the cargo
        PosCountCargo cargo = (PosCountCargo)bus.getCargo();

        CurrencyTypeIfc[]   altCurrencies = DomainGateway.getAlternateCurrencyTypes();

        SummaryForeignCurrencyCountMenuBeanModel sfccbm = new SummaryForeignCurrencyCountMenuBeanModel(altCurrencies);

        // Get the SummaryCountBeanModel from the cargo
        SummaryCountBeanModel sc[] = cargo.getForeignCurrencyBeanModels(sfccbm, bus);
        //This calculates the total for each currency for all tender types
        //Note that the entered values are hence displayed and taken into account for this screen
        if (cargo.isForeignTenderCountModelAssigned())
        {
            for (int i=0; i< sc.length; i++)
            {
                if (sc[i].getDescription().equals(cargo.getCurrentForeignCurrency()))
                {
                    //adding amount values
                    CurrencyIfc totalForSpecificCurrency  = DomainGateway.getBaseCurrencyInstance();
                    SummaryCountBeanModel summaryCountCurrent[] = cargo.getForeignTenderModels();
                    for (int j=0; j< summaryCountCurrent.length; j++)
                    {
                        totalForSpecificCurrency = totalForSpecificCurrency.add(summaryCountCurrent[j].getAmount());
                    }
                    sc[i].setAmount(totalForSpecificCurrency);
                }
            }
        }

        // display buttons based on which currencies are accepted
        UtilityManagerIfc utility = (UtilityManagerIfc) bus.getManager(UtilityManagerIfc.TYPE);
        NavigationButtonBeanModel navModel = getLocalNavigationModel(utility, sc);

        sfccbm.setLocalButtonBeanModel(navModel);
        sfccbm.setSummaryCountBeanModel(sc);
        /*
         * Ask the UI Manager to display the screen
         */
        POSUIManagerIfc ui = (POSUIManagerIfc)bus.getManager(UIManagerIfc.TYPE);
        ui.showScreen(POSUIManagerIfc.FOREIGN_CURRENCY_COUNT, sfccbm);
    }                                   // end arrive()

    //--------------------------------------------------------------------------
    /**
        Sets the local navigation button bean model from the alternate currencies list.
        @param utility      The utility manager
        @param sc           The summary count model array
        @return             The local navigation button bean model
    **/
    //--------------------------------------------------------------------------
    protected NavigationButtonBeanModel getLocalNavigationModel(UtilityManagerIfc utility,
                                                                SummaryCountBeanModel[] sc)
    {
        NavigationButtonBeanModel navModel = new NavigationButtonBeanModel();
        int j=2;
        for (int i = 0; i < sc.length; i++)
        {
            String keyName    = "F" + (j);
            addButton(utility, navModel, keyName, sc[i]);
            if(j%8==0){
            	j=1;
            }
            j++;
        }

        return navModel;
    }

    //--------------------------------------------------------------------------
    /**
        Adds the foreign currency button to the local navigation button bean model.
        @param utility      The utility manager
        @param model        The local navigation button bean model
        @param functionKey  The function key name
        @param sc           The summary count model for the currency
    **/
    //--------------------------------------------------------------------------
    protected void addButton(UtilityManagerIfc utility,
                             NavigationButtonBeanModel model,
                             String functionKey,
                             SummaryCountBeanModel sc)
    {
        StringBuffer labelTag = new StringBuffer(SummaryForeignCurrencyCountMenuBeanModel.LABEL_TAG_PREFIX)
                                .append(sc.getDescription());

        String currencyLabel = utility.retrieveText("Common",
                BundleConstantsIfc.TILL_BUNDLE_NAME,
                labelTag.toString(),
                sc.getDescription());
        model.addButton(sc.getActionName(), currencyLabel, true, null, functionKey, null);
        model.setButtonLabel(sc.getActionName(), currencyLabel);
    }

}
