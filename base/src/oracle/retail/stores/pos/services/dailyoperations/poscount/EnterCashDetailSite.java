/* ===========================================================================
* Copyright (c) 1998, 2011, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/dailyoperations/poscount/EnterCashDetailSite.java /rgbustores_13.4x_generic_branch/1 2011/05/05 14:06:24 mszekely Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    cgreene   05/26/10 - convert to oracle packaging
 *    cgreene   04/26/10 - XbranchMerge cgreene_tech43 from
 *                         st_rgbustores_techissueseatel_generic_branch
 *    cgreene   04/02/10 - remove deprecated LocaleContantsIfc and currencies
 *    abondala  01/03/10 - update header date
 *    mchellap  03/13/09 - Fixed errors on Detail Currency (foreign currency)
 *                         screen
 *    mdecama   01/29/09 - Using ISO Currency Codes to represent the Currency
 *                         in the GUI
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
 *    3    360Commerce 1.2         3/31/2005 4:28:01 PM   Robert Pearse
 *    2    360Commerce 1.1         3/10/2005 10:21:24 AM  Robert Pearse
 *    1    360Commerce 1.0         2/11/2005 12:10:53 PM  Robert Pearse
 *
 *   Revision 1.7  2004/09/23 00:07:13  kmcbride
 *   @scr 7211: Adding static serialVersionUIDs to all POS Serializable objects, minus the JComponents
 *
 *   Revision 1.6  2004/07/22 00:06:33  jdeleau
 *   @scr 3665 Standardize on I18N standards across all properties files.
 *   Use {0}, {1}, etc instead of remaining <ARG> or #ARG# variables.
 *
 *   Revision 1.5  2004/06/17 22:36:28  dcobb
 *   @scr 4205 Feature Enhancement: Till Options
 *   Add foreign currency to tender detail count interface.
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
 *    Rev 1.0   Aug 29 2003 15:56:44   CSchellenger
 * Initial revision.
 *
 *    Rev 1.1   Sep 03 2002 16:03:40   baa
 * externalize domain  constants and parameter values
 * Resolution for POS SCR-1740: Code base Conversions
 *
 *    Rev 1.0   Apr 29 2002 15:30:50   msg
 * Initial revision.
 *
 *    Rev 1.1   Mar 18 2002 23:14:26   msg
 * - updated copyright
 *
 *    Rev 1.0   Mar 18 2002 11:27:08   msg
 * Initial revision.
 *
 *    Rev 1.2   Mar 09 2002 16:02:54   mpm
 * Tweaked text externalization.
 * Resolution for POS SCR-351: Internationalization
 *
 *    Rev 1.0   Sep 21 2001 11:17:12   msg
 * Initial revision.
 *
 *    Rev 1.1   Sep 17 2001 13:11:24   msg
 * header update
 * ===========================================================================
 */
package oracle.retail.stores.pos.services.dailyoperations.poscount;

// foundation
import oracle.retail.stores.commerceservices.common.currency.CurrencyIfc;
import oracle.retail.stores.common.utility.LocaleMap;
import oracle.retail.stores.pos.services.tender.tdo.TenderTDOConstants;
import oracle.retail.stores.domain.utility.LocaleConstantsIfc;
import oracle.retail.stores.domain.utility.LocaleUtilities;
import oracle.retail.stores.foundation.manager.ifc.UIManagerIfc;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.foundation.tour.ifc.SiteActionIfc;
import oracle.retail.stores.foundation.utility.Util;
import oracle.retail.stores.pos.config.bundles.BundleConstantsIfc;
import oracle.retail.stores.pos.manager.ifc.UtilityManagerIfc;
import oracle.retail.stores.pos.services.PosSiteActionAdapter;
import oracle.retail.stores.pos.ui.POSUIManagerIfc;
import oracle.retail.stores.pos.ui.beans.CurrencyDetailBeanModel;
import oracle.retail.stores.pos.ui.beans.PromptAndResponseModel;
import oracle.retail.stores.pos.ui.beans.StatusBeanModel;

//------------------------------------------------------------------------------
/**


     @version $Revision: /rgbustores_13.4x_generic_branch/1 $
**/
//------------------------------------------------------------------------------

public class EnterCashDetailSite extends PosSiteActionAdapter implements SiteActionIfc
{
    // This id is used to tell
    // the compiler not to generate a
    // new serialVersionUID.
    //
    static final long serialVersionUID = -7809723266055789311L;


    /**
       revision number
    **/
    public static String revisionNumber = "$Revision: /rgbustores_13.4x_generic_branch/1 $";
    /**
        float suffix text tag
    **/
    protected static final String FLOAT_SUFFIX_TAG = "FloatSuffix";
    /**
        float suffix text
    **/
    protected static final String FLOAT_SUFFIX = " for float";
    /**
        currency text tag
    **/
    protected static final String CURRENCY_TEXT_TAG = "CurrencyText";
    /**
        float suffix text
    **/
    protected static final String CURRENCY_TEXT = "currency";
    /**
        spec name constant
    **/
    protected static final String CURRENCY_DETAIL_SPEC_NAME = "CurrencyDetailSpec";
    /**
        suffix for looking up the nationality of the country code in the bundles
        @deprecated as of 13.1 Use are using the Currency ISO, instead of the nationality
    **/
    protected static final String NATIONALITY_SUFFIX = "_Nationality";

    /**
     * tag for screen name constant
     */
    public static final String SCREEN_NAME_TAG = "_ScreenName";

    //--------------------------------------------------------------------------
    /**
       Display the count cash detail screen.  This bean will count either
       local or alternate cash.
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
        }

        PromptAndResponseModel pandrModel = new PromptAndResponseModel();
        String countType = new String("");
        if (cargo.getCountType() == PosCountCargo.START_FLOAT || cargo.getCountType() == PosCountCargo.END_FLOAT)
        {
            countType = utility.retrieveText(CURRENCY_DETAIL_SPEC_NAME,
                                             BundleConstantsIfc.POSCOUNT_BUNDLE_NAME,
                                             FLOAT_SUFFIX_TAG,
                                             FLOAT_SUFFIX);
        }
        StringBuffer argumentText = new StringBuffer();
        String currencyCode = beanModel.getTotal().getType().getCurrencyCode();

        argumentText.append(utility.retrieveCommonText(currencyCode));
        argumentText.append(" ")
                    .append(utility.retrieveText(CURRENCY_DETAIL_SPEC_NAME,
                                                 BundleConstantsIfc.POSCOUNT_BUNDLE_NAME,
                                                 CURRENCY_TEXT_TAG,
                                                 CURRENCY_TEXT));
        if (!Util.isEmpty(countType))
        {
            argumentText.append(" ").append(countType);
        }
        // For currencies other than base currency, add currency code to screen name
        if (!beanModel.getTotal().getType().isBaseFlag())
        {
            StatusBeanModel beanStatusModel = new StatusBeanModel();

            String screenName = utility.retrieveText(POSUIManagerIfc.STATUS_SPEC,
                    BundleConstantsIfc.POSCOUNT_BUNDLE_NAME, "CurrencyDetailScreenName", "Currency Detail");
            String currencyScreenName = utility.retrieveCommonText(currencyCode + SCREEN_NAME_TAG, currencyCode);

            beanStatusModel.setScreenName(currencyScreenName + " " + screenName);
            beanModel.setStatusBeanModel(beanStatusModel);
        }
        pandrModel.setArguments(argumentText.toString());
        beanModel.setPromptAndResponseModel(pandrModel);
        // Display the bean model
        ui.showScreen(POSUIManagerIfc.CURRENCY_DETAIL, beanModel);

    }
}
