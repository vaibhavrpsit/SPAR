/* ===========================================================================
* Copyright (c) 2004, 2011, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/redeem/EnterRedeemForeignNumberUISite.java /main/15 2011/12/05 12:16:21 cgreene Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    cgreene   12/05/11 - updated from deprecated packages and used more
 *                         bigdecimal constants
 *    cgreene   07/07/11 - convert entryMethod to an enum
 *    acadar    06/10/10 - use default locale for currency display
 *    acadar    06/09/10 - XbranchMerge acadar_tech30 from
 *                         st_rgbustores_techissueseatel_generic_branch
 *    cgreene   05/26/10 - convert to oracle packaging
 *    cgreene   04/26/10 - XbranchMerge cgreene_tech43 from
 *                         st_rgbustores_techissueseatel_generic_branch
 *    acadar    04/08/10 - merge to tip
 *    acadar    04/06/10 - use default locale when displaying currency
 *    cgreene   04/02/10 - remove deprecated LocaleContantsIfc and currencies
 *    abondala  01/03/10 - update header date
 *    asinton   08/12/09 - Configure Redeem Number screen to use barcode
 *                         scanner
 *
 * ===========================================================================
 * $Log:
 *    4    360Commerce 1.3         7/23/2007 11:54:52 AM  Ashok.Mondal    CR
 *         27862 :Correcting amount format on redeem screen.
 *    3    360Commerce 1.2         3/31/2005 4:28:04 PM   Robert Pearse
 *    2    360Commerce 1.1         3/10/2005 10:21:27 AM  Robert Pearse
 *    1    360Commerce 1.0         2/11/2005 12:10:54 PM  Robert Pearse
 *
 *   Revision 1.8  2004/05/20 19:48:52  crain
 *   @scr 5108 Tender Redeem_Redeem Foreign Gift Certificate Receipt Incorrect
 *
 *   Revision 1.7  2004/05/02 01:54:05  crain
 *   @scr 4553 Redeem Gift Certificate
 *
 *   Revision 1.6  2004/04/29 23:48:50  crain
 *   @scr 4553 Redeem Gift Certificate
 *
 *   Revision 1.5  2004/04/29 15:07:19  crain
 *   @scr 4553 Redeem Gift Certificate
 *
 *   Revision 1.4  2004/04/21 15:08:58  blj
 *   @scr 3872 - cleanup from code review
 *
 *   Revision 1.3  2004/04/16 14:58:25  blj
 *   @scr 3872 - fixed a few flow and screen text issues.
 *
 *   Revision 1.2  2004/04/12 18:37:47  blj
 *   @scr 3872 - fixed a problem with validation occuring after foreign currency has been converted.
 *
 *   Revision 1.1  2004/04/07 22:49:40  blj
 *   @scr 3872 - fixed problems with foreign currency, fixed ui labels, redesigned to do validation and adding tender to transaction in separate sites.
 *
 * ===========================================================================
 */
package oracle.retail.stores.pos.services.redeem;

import java.util.Locale;

import oracle.retail.stores.commerceservices.common.currency.CurrencyServiceIfc;
import oracle.retail.stores.commerceservices.common.currency.CurrencyServiceLocator;
import oracle.retail.stores.common.utility.Util;
import oracle.retail.stores.domain.utility.EntryMethod;
import oracle.retail.stores.domain.utility.LocaleConstantsIfc;
import oracle.retail.stores.domain.utility.LocaleUtilities;
import oracle.retail.stores.foundation.manager.ifc.UIManagerIfc;
import oracle.retail.stores.foundation.tour.application.SiteActionAdapter;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.common.utility.LocaleMap;
import oracle.retail.stores.pos.ado.tender.TenderConstants;
import oracle.retail.stores.pos.manager.ifc.UtilityManagerIfc;
import oracle.retail.stores.pos.services.common.CommonLetterIfc;
import oracle.retail.stores.pos.ui.POSUIManagerIfc;
import oracle.retail.stores.pos.ui.beans.DataInputBeanModel;
import oracle.retail.stores.pos.ui.beans.POSBaseBeanModel;
import oracle.retail.stores.pos.ui.beans.PromptAndResponseModel;

/**
 * @author blj
 */
public class EnterRedeemForeignNumberUISite extends SiteActionAdapter
{
    private static final long serialVersionUID = 5018699983502773254L;

    /**
     * This site displays the Redeem Number Site and collects this number from
     * the ui in the depart method.
     * 
     * @param bus the bus arriving at this site
     */
    @Override
    public void arrive(BusIfc bus)
    {
        POSUIManagerIfc ui = (POSUIManagerIfc) bus.getManager(UIManagerIfc.TYPE);
        RedeemCargo cargo = (RedeemCargo)bus.getCargo();
        POSBaseBeanModel beanModel = new POSBaseBeanModel();
        // This screen has <ARG> text in the prompt and response region so we
        // must retrieve the correct text for the <ARG>
        UtilityManagerIfc utility = (UtilityManagerIfc) bus.getManager(UtilityManagerIfc.TYPE);
        /**
         * Currency Service
         */
        CurrencyServiceIfc currencyService = CurrencyServiceLocator.getCurrencyService(); //CR 27862

        // Append transaction id to prompt response
        String pattern =
            utility.retrieveText(
                    "PromptAndResponsePanelSpec",
                    "redeemText",
                    "RedeemForeignTenderNumberPrompt",
                    "Enter foreign {0} number, then press Next.");

        String redeemTypeText = utility.retrieveText(
                "common",
                "commonText",
                cargo.getRedeemTypeSelected(),
                cargo.getRedeemTypeSelected());

        Locale locale = LocaleMap.getLocale(LocaleConstantsIfc.USER_INTERFACE);
        String message = LocaleUtilities.formatComplexMessage(pattern, redeemTypeText.toLowerCase(locale));

        PromptAndResponseModel promptModel = new PromptAndResponseModel();
        promptModel.setPromptText(message);

        beanModel.setPromptAndResponseModel(promptModel);

        // This screen has <ARG> text in the workpanel as well.
        String amount = (String)cargo.getTenderAttributes().get(TenderConstants.AMOUNT);
        amount = currencyService.formatCurrency(amount, LocaleMap.getLocale(LocaleMap.DEFAULT)); //CR 27862
        String issuingStore = (String)cargo.getTenderAttributes().get(TenderConstants.STORE_NUMBER);
        DataInputBeanModel dModel = new DataInputBeanModel();
        dModel.setValue("FaceValueAmountLabel", amount);
        if (Util.isEmpty(issuingStore) || issuingStore != null)
        {
            dModel.setValue("IssuingStoreLabel", issuingStore);
        }
        dModel.setPromptAndResponseModel(promptModel);
        ui.showScreen(POSUIManagerIfc.REDEEM_FOREIGN_NUMBER, dModel);
    }

    /* (non-Javadoc)
     * @see oracle.retail.stores.foundation.tour.application.SiteActionAdapter#depart(oracle.retail.stores.foundation.tour.ifc.BusIfc)
     */
    @Override
    public void depart(BusIfc bus)
    {
        if (bus.getCurrentLetter().getName().equals(CommonLetterIfc.NEXT))
        {
            // Get information from UI
            RedeemCargo cargo = (RedeemCargo)bus.getCargo();
            POSUIManagerIfc ui = (POSUIManagerIfc)bus.getManager(UIManagerIfc.TYPE);
            PromptAndResponseModel parModel = ((POSBaseBeanModel)ui.getModel()).getPromptAndResponseModel();
            cargo.setForeign(true);

            // the amount entered is the foreign amount
            String foreignAmount = (String)cargo.getTenderAttributes().get(TenderConstants.AMOUNT);
            cargo.getTenderAttributes().put(TenderConstants.ALTERNATE_AMOUNT, foreignAmount);

            cargo.getTenderAttributes().put(TenderConstants.NUMBER, ui.getInput());
            if(parModel.isScanned())
            {
                cargo.getTenderAttributes().put(TenderConstants.ENTRY_METHOD, EntryMethod.Automatic);
            }
            else
            {
                cargo.getTenderAttributes().put(TenderConstants.ENTRY_METHOD, EntryMethod.Manual);
            }
        }
    }
}
