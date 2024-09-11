/* ===========================================================================
* Copyright (c) 2004, 2011, 2014, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/tender/storecredit/EnterStoreCreditNumberUISite.java /main/14 2011/12/05 12:16:23 cgreene Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    icole     11/07/14 - cleaned up code for setting store credit number.
 *    icole     08/20/14 - changed max prompt length to 18 to allow for mmdd in ID.
 *    cgreene   12/05/11 - updated from deprecated packages and used more
 *                         bigdecimal constants
 *    cgreene   07/07/11 - convert entryMethod to an enum
 *    cgreene   05/26/10 - convert to oracle packaging
 *    cgreene   04/26/10 - XbranchMerge cgreene_tech43 from
 *                         st_rgbustores_techissueseatel_generic_branch
 *    cgreene   04/02/10 - remove deprecated LocaleContantsIfc and currencies
 *    abondala  01/03/10 - update header date
 *    ohorne    03/24/09 - Max length of prompt is now based on
 *                         PrePrintedStoreCredit parameter value
 *
 * ===========================================================================
 * $Log:
 *    4    360Commerce 1.3         1/25/2006 4:11:00 PM   Brett J. Larsen merge
 *          7.1.1 changes (aka. 7.0.3 fixes) into 360Commerce view
 *    3    360Commerce 1.2         3/31/2005 4:28:05 PM   Robert Pearse   
 *    2    360Commerce 1.1         3/10/2005 10:21:27 AM  Robert Pearse   
 *    1    360Commerce 1.0         2/11/2005 12:10:55 PM  Robert Pearse   
 *:
 *    4    .v700     1.2.1.0     11/4/2005 13:09:13     Deepanshu       CR
 *         6103: Reset the refund tender letter
 *    3    360Commerce1.2         3/31/2005 15:28:05     Robert Pearse
 *    2    360Commerce1.1         3/10/2005 10:21:27     Robert Pearse
 *    1    360Commerce1.0         2/11/2005 12:10:55     Robert Pearse
 *
 *   Revision 1.3  2004/07/22 00:06:34  jdeleau
 *   @scr 3665 Standardize on I18N standards across all properties files.
 *   Use {0}, {1}, etc instead of remaining <ARG> or #ARG# variables.
 *
 *   Revision 1.2  2004/07/21 15:30:27  blj
 *   @scr 4461 - updated to match requirements
 *
 *   Revision 1.1  2004/02/17 17:56:49  nrao
 *   New site for Issue Store Credit
 *
 *
 * ===========================================================================
 */
package oracle.retail.stores.pos.services.tender.storecredit;

import java.util.Locale;

import oracle.retail.stores.domain.utility.EntryMethod;
import oracle.retail.stores.domain.utility.LocaleConstantsIfc;
import oracle.retail.stores.foundation.manager.ifc.ParameterManagerIfc;
import oracle.retail.stores.foundation.manager.ifc.UIManagerIfc;
import oracle.retail.stores.foundation.manager.parameter.ParameterException;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.foundation.tour.ifc.LetterIfc;
import oracle.retail.stores.common.parameter.ParameterConstantsIfc;
import oracle.retail.stores.common.utility.LocaleMap;
import oracle.retail.stores.pos.ado.tender.TenderConstants;
import oracle.retail.stores.pos.config.bundles.BundleConstantsIfc;
import oracle.retail.stores.pos.manager.ifc.UtilityManagerIfc;
import oracle.retail.stores.pos.services.PosSiteActionAdapter;
import oracle.retail.stores.pos.services.common.CommonLetterIfc;
import oracle.retail.stores.pos.services.tender.TenderCargo;
import oracle.retail.stores.pos.ui.POSUIManagerIfc;
import oracle.retail.stores.pos.ui.beans.POSBaseBeanModel;
import oracle.retail.stores.pos.ui.beans.PromptAndResponseModel;
import oracle.retail.stores.pos.ui.beans.StatusBeanModel;

/**
 * Displays the Enter Store Credit Number Screen.
 *
 * @version $Revision: /main/14 $
 */
public class EnterStoreCreditNumberUISite extends PosSiteActionAdapter
{
    private static final long serialVersionUID = -85931610909782388L;

    /** revision number **/
    public static final String revisionNumber = "$Revision: /main/14 $";

    // Static strings
    public static final String STORE_CREDIT_LABEL = "storecredit";
    public static final String STORE_CREDIT_DEFAULT = "store credit";
    public static final String STATUS_PANEL_SPEC = "StatusPanelSpec";
    public static final String SCREEN_NAME_LABEL = "IssueTenderNumberScreenName";
    public static final String SCREEN_NAME_DEFAULT = "Issue Tender Number";

    /* (non-Javadoc)
     * @see oracle.retail.stores.foundation.tour.application.SiteActionAdapter#arrive(oracle.retail.stores.foundation.tour.ifc.BusIfc)
     */
    @Override
    public void arrive(BusIfc bus)
    {
        POSUIManagerIfc ui = (POSUIManagerIfc) bus.getManager(UIManagerIfc.TYPE);
        POSBaseBeanModel beanModel = new POSBaseBeanModel();
        PromptAndResponseModel promptModel = new PromptAndResponseModel();

        UtilityManagerIfc utilManager = (UtilityManagerIfc)bus.getManager(UtilityManagerIfc.TYPE);
        String arg = utilManager.retrieveCommonText(STORE_CREDIT_LABEL, STORE_CREDIT_DEFAULT);
        Locale locale = LocaleMap.getLocale(LocaleConstantsIfc.USER_INTERFACE);
        arg = arg.toLowerCase(locale);

        // set the arg text in the prompt area
        promptModel.setArguments(arg);
        try
        {
            ParameterManagerIfc pm = (ParameterManagerIfc) bus.getManager(ParameterManagerIfc.TYPE);
            if (pm.getBooleanValue(ParameterConstantsIfc.TENDER_PrePrintedStoreCredit))
            {
                promptModel.setMaxLength("12");
            }
            else
            {
                promptModel.setMaxLength("18");
            }
        }
        catch (ParameterException e)
        {
            // Log error, set non-preprinted max length, and continue.
            promptModel.setMaxLength("18");
            logger.error("Error retrieving PrePrintedStoreCredit parameter, using non-PrePrintedStoreCredit maximum length of 18",e);
        }        
        beanModel.setPromptAndResponseModel(promptModel);

        // set the screen name in the status area
        StatusBeanModel status = new StatusBeanModel();
        status.setScreenName(utilManager.retrieveText(STATUS_PANEL_SPEC, BundleConstantsIfc.TENDER_BUNDLE_NAME,
                                                        SCREEN_NAME_LABEL, SCREEN_NAME_DEFAULT));
        beanModel.setStatusBeanModel(status);

        ui.showScreen(POSUIManagerIfc.STORE_CREDIT_NUMBER_ENTRY, beanModel);
    }

    /* (non-Javadoc)
     * @see oracle.retail.stores.foundation.tour.application.SiteActionAdapter#depart(oracle.retail.stores.foundation.tour.ifc.BusIfc)
     */
    @Override
    public void depart(BusIfc bus)
    {
        LetterIfc letter = bus.getCurrentLetter();
        TenderCargo cargo = (TenderCargo)bus.getCargo();

        // If the user entered a store credit number
        if (letter.getName().equals(CommonLetterIfc.NEXT))
        {
            POSUIManagerIfc ui = (POSUIManagerIfc) bus.getManager(UIManagerIfc.TYPE);
            boolean isScanned = ((POSBaseBeanModel)ui.getModel()).getPromptAndResponseModel().isScanned();

            // Get the store credit number and put in the cargo
            cargo.getTenderAttributes().put(TenderConstants.NUMBER, ui.getInput().trim());
            if (isScanned)
            {
                cargo.getTenderAttributes().put(TenderConstants.ENTRY_METHOD, EntryMethod.Automatic);
            }
            else
            {
                cargo.getTenderAttributes().put(TenderConstants.ENTRY_METHOD, EntryMethod.Manual);
            }
        }
        else if (letter.getName().equals("Undo"))
        {
            cargo.setRefundTenderLetter(null);  // reset the refund tender letter
        }
    }
}
