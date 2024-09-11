/* ===========================================================================
* Copyright (c) 1998, 2011, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/dailyoperations/poscount/EnterTenderDetailSite.java /rgbustores_13.4x_generic_branch/1 2011/05/05 14:06:23 mszekely Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    cgreene   05/27/10 - convert to oracle packaging
 *    cgreene   05/26/10 - convert to oracle packaging
 *    abondala  01/03/10 - update header date
 *    mdecama   01/29/09 - Using ISO Currency Codes to represent the Currency
 *                         in the GUI
 *
 * ===========================================================================
 * $Log:
 *    3    360Commerce 1.2         3/31/2005 4:28:05 PM   Robert Pearse
 *    2    360Commerce 1.1         3/10/2005 10:21:28 AM  Robert Pearse
 *    1    360Commerce 1.0         2/11/2005 12:10:55 PM  Robert Pearse
 *
 *   Revision 1.6  2004/09/23 00:07:14  kmcbride
 *   @scr 7211: Adding static serialVersionUIDs to all POS Serializable objects, minus the JComponents
 *
 *   Revision 1.5  2004/07/22 00:06:33  jdeleau
 *   @scr 3665 Standardize on I18N standards across all properties files.
 *   Use {0}, {1}, etc instead of remaining <ARG> or #ARG# variables.
 *
 *   Revision 1.4  2004/06/17 22:36:28  dcobb
 *   @scr 4205 Feature Enhancement: Till Options
 *   Add foreign currency to tender detail count interface.
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
 *    Rev 1.0   Aug 29 2003 15:56:46   CSchellenger
 * Initial revision.
 *
 *    Rev 1.3   Jun 30 2003 16:33:40   bwf
 * Internationalize description when putting into prompt and response bean.
 * Resolution for 2503: OTHER_TENDER_DETAIL doesnt use bundles
 *
 *    Rev 1.2   May 14 2003 11:25:56   bwf
 * Removed check of bundle in this location.  This caused problems with Totals Reports.
 * Resolution for 2432: Select Tender screen does not update when a different tender amount is entered, financials incorrect
 *
 *    Rev 1.1   Mar 11 2003 17:00:12   bwf
 * Read from correct bundle for Type of currency descriptor.
 * Resolution for 1866: I18n Database  support
 *
 *    Rev 1.0   Apr 29 2002 15:30:54   msg
 * Initial revision.
 *
 *    Rev 1.1   Mar 18 2002 23:14:28   msg
 * - updated copyright
 *
 *    Rev 1.0   Mar 18 2002 11:27:10   msg
 * Initial revision.
 *
 *    Rev 1.0   Sep 21 2001 11:17:08   msg
 * Initial revision.
 *
 *    Rev 1.1   Sep 17 2001 13:11:24   msg
 * header update
 * ===========================================================================
 */
package oracle.retail.stores.pos.services.dailyoperations.poscount;

import oracle.retail.stores.domain.DomainGateway;
import oracle.retail.stores.foundation.manager.ifc.UIManagerIfc;
import oracle.retail.stores.foundation.tour.gate.Gateway;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.foundation.tour.ifc.SiteActionIfc;
import oracle.retail.stores.pos.manager.ifc.UtilityManagerIfc;
import oracle.retail.stores.pos.services.PosSiteActionAdapter;
import oracle.retail.stores.pos.ui.POSUIManagerIfc;
import oracle.retail.stores.pos.ui.beans.OtherTenderDetailBeanModel;
import oracle.retail.stores.pos.ui.beans.PromptAndResponseModel;

//------------------------------------------------------------------------------
/**
     Displays the OTHER_TENDER_DETAIL screen.
     @version $Revision: /rgbustores_13.4x_generic_branch/1 $
**/
//------------------------------------------------------------------------------

public class EnterTenderDetailSite extends PosSiteActionAdapter implements SiteActionIfc
{
    // This id is used to tell
    // the compiler not to generate a
    // new serialVersionUID.
    //
    static final long serialVersionUID = 8665656555290754755L;


    /**
        revision number
    **/
    public static String revisionNumber = "$Revision: /rgbustores_13.4x_generic_branch/1 $";
    /**
        Site name for logging
    **/
    public static final String SITENAME = "EnterTenderDetailSite";
    /**
        suffix for looking up the nationality of the country code in the bundles
    **/
    protected static final String NATIONALITY_SUFFIX = "_Nationality";

    //--------------------------------------------------------------------------
    /**
        Displays the OTHER_TENDER_DETAIL screen. The prompt argument is constructed
        from the tender nationality and the tender description.

        @param bus the bus arriving at this site
    **/
    //--------------------------------------------------------------------------

    public void arrive(BusIfc bus)
    {

        /*
         * Ask the UI Manager to display the screen
         */
        UtilityManagerIfc utility =
            (UtilityManagerIfc) Gateway.getDispatcher().getManager(UtilityManagerIfc.TYPE);
        PosCountCargo cargo = (PosCountCargo)bus.getCargo();
        OtherTenderDetailBeanModel beanModel = cargo.getOtherTenderDetailBeanModel();
        String currencyCode = cargo.getCurrentForeignCurrency();
        if (currencyCode.equals(PosCountCargo.NONE))
        {
            currencyCode = DomainGateway.getBaseCurrencyInstance().getType().getCurrencyCode();
        }

        PromptAndResponseModel pandrModel = new PromptAndResponseModel();
        StringBuffer argumentText = new StringBuffer();
        argumentText.append(utility.retrieveCommonText(currencyCode));
        argumentText.append(" ")
                    .append(utility.retrieveCommonText(cargo.getCurrentActivityOrCharge()));
        pandrModel.setArguments(argumentText.toString());
        beanModel.setPromptAndResponseModel(pandrModel);
        POSUIManagerIfc ui = (POSUIManagerIfc)bus.getManager(UIManagerIfc.TYPE);
        ui.showScreen(POSUIManagerIfc.OTHER_TENDER_DETAIL, beanModel);

    }

}
