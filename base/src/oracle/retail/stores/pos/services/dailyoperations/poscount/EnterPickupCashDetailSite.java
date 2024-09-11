/* ===========================================================================
* Copyright (c) 2004, 2011, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/dailyoperations/poscount/EnterPickupCashDetailSite.java /rgbustores_13.4x_generic_branch/1 2011/05/05 14:06:23 mszekely Exp $
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
 *    5    360Commerce 1.4         6/11/2007 11:51:26 AM  Anda D. Cadar   SCR
 *         27206: replace getNationality with getCountryCode; Nationality
 *         column in co_cny was poulated previosly with the value for the
 *         country code. I18N change was to populate nationality with
 *         nationality value
 *    4    360Commerce 1.3         4/25/2007 8:52:30 AM   Anda D. Cadar   I18N
 *         merge
 *         
 *    3    360Commerce 1.2         3/31/2005 4:28:02 PM   Robert Pearse   
 *    2    360Commerce 1.1         3/10/2005 10:21:26 AM  Robert Pearse   
 *    1    360Commerce 1.0         2/11/2005 12:10:54 PM  Robert Pearse   
 *
 *   Revision 1.6  2004/09/23 00:07:14  kmcbride
 *   @scr 7211: Adding static serialVersionUIDs to all POS Serializable objects, minus the JComponents
 *
 *   Revision 1.5  2004/07/22 00:06:33  jdeleau
 *   @scr 3665 Standardize on I18N standards across all properties files.
 *   Use {0}, {1}, etc instead of remaining <ARG> or #ARG# variables.
 *
 *   Revision 1.4  2004/02/16 14:40:13  blj
 *   @scr 3838 - cleanup code
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
 *    Rev 1.1   Feb 09 2004 10:38:58   DCobb
 * Refresh the register field from the bean model.
 * Resolution for 3381: Feature Enhancement:  Till Pickup and Loan
 * 
 *    Rev 1.0   Feb 06 2004 17:09:32   DCobb
 * Initial revision.
 * ===========================================================================
 */
package oracle.retail.stores.pos.services.dailyoperations.poscount;

// foundation
import oracle.retail.stores.commerceservices.common.currency.CurrencyIfc;
import oracle.retail.stores.foundation.manager.ifc.UIManagerIfc;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.foundation.tour.ifc.SiteActionIfc;
import oracle.retail.stores.pos.config.bundles.BundleConstantsIfc;
import oracle.retail.stores.pos.manager.ifc.UtilityManagerIfc;
import oracle.retail.stores.pos.services.PosSiteActionAdapter;
import oracle.retail.stores.pos.ui.POSUIManagerIfc;
import oracle.retail.stores.pos.ui.beans.CurrencyDetailBeanModel;
import oracle.retail.stores.pos.ui.beans.PromptAndResponseModel;

//------------------------------------------------------------------------------
/**
    Display the count cash detail pickup screen.
    @version $Revision: /rgbustores_13.4x_generic_branch/1 $
**/
//------------------------------------------------------------------------------

public class EnterPickupCashDetailSite extends PosSiteActionAdapter implements SiteActionIfc
{
    // This id is used to tell
    // the compiler not to generate a
    // new serialVersionUID.
    //
    static final long serialVersionUID = 262823673821345984L;

    /**
       revision number
    **/
    public static String revisionNumber = "$Revision: /rgbustores_13.4x_generic_branch/1 $";
    /**
       Site name for logging
    **/
    public static final String SITENAME = "EnterPickupCashDetailSite";
    /**
        float suffix text tag
    **/
    protected static String FLOAT_SUFFIX_TAG = "FloatSuffix";
    /**
        float suffix text
    **/
    protected static String FLOAT_SUFFIX = " for float";
    /**
        currency text tag
    **/
    protected static String CURRENCY_TEXT_TAG = "CurrencyText";
    /**
        float suffix text
    **/
    protected static String CURRENCY_TEXT = "currency";
    /**
        spec name constant
    **/
    protected static String CURRENCY_DETAIL_PICKUP_SPEC_NAME = "CurrencyDetailPickupSpec";

    //--------------------------------------------------------------------------
    /**
       Display the count cash detail pickup screen.  Only local cash for pickup 
       will be counted at this site when operating without a safe.
       @param bus the bus arriving at this site
    **/
    //--------------------------------------------------------------------------

    public void arrive(BusIfc bus)
    {
        /*
         * Ask the UI Manager to display the screen
         */
        PosCountCargo cargo = (PosCountCargo)bus.getCargo();
        POSUIManagerIfc ui = (POSUIManagerIfc)bus.getManager(UIManagerIfc.TYPE);
        UtilityManagerIfc utility =
          (UtilityManagerIfc) bus.getManager(UtilityManagerIfc.TYPE);


        // If the user has already setup the beanModel going through this process
        // once, display the value they put in originally.  Otherwise create a
        // new bean model.
        String key                        = cargo.getExpectedAmount().getCountryCode();
        CurrencyDetailBeanModel beanModel = cargo.getCurrencyDetailBeanModel(key);
        if (beanModel == null)
        {
            // Set up the bean model; the total data member must be set so that
            // the bean knows what kind of currency it is counting and totaling.
            beanModel         = new CurrencyDetailBeanModel();
            CurrencyIfc total = (CurrencyIfc)cargo.getExpectedAmount().clone();
            total.setStringValue("0.00");
            beanModel.setTotal(total);
            if (cargo.getCountType() == PosCountCargo.TILL)
            {
                beanModel.setSummaryCurrencyDescription(cargo.getCurrentActivityOrCharge());
            }
            else
            {
                beanModel.setSummaryCurrencyDescription(cargo.getCurrentFLPTender());
            }

            beanModel.setOperateWithSafeFlag(false);
            beanModel.setPickupFlag(true);
            beanModel.setLoanFlag(false);
            beanModel.setRegister(cargo.getPickupAndLoanToRegister());
        }

        PromptAndResponseModel pandrModel = new PromptAndResponseModel();

        StringBuffer argumentText = new StringBuffer(beanModel.getTotal().getCountryCode());
        argumentText.append(" ")
                    .append(utility.retrieveText(CURRENCY_DETAIL_PICKUP_SPEC_NAME,
                                                 BundleConstantsIfc.POSCOUNT_BUNDLE_NAME,
                                                 CURRENCY_TEXT_TAG,
                                                 CURRENCY_TEXT));

        pandrModel.setArguments(argumentText.toString());
        beanModel.setPromptAndResponseModel(pandrModel);
        
        // Display the screen
        ui.showScreen(POSUIManagerIfc.CURRENCY_DETAIL_PICKUP, beanModel);
    }
}

