/* ===========================================================================
* Copyright (c) 1998, 2013, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/giftcertificate/issue/GiftCertificateIssueActionSite.java /main/15 2013/01/04 16:42:28 asinton Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    asinton   01/04/13 - Fixed problem with pricing of a gift certificate
 *    blarsen   08/27/12 - Merge from project Echo (MPOS) into trunk.
 *    cgreene   03/30/12 - get journalmanager from bus
 *    cgreene   12/05/11 - updated from deprecated packages and used more
 *                         bigdecimal constants
 *    cgreene   02/15/11 - move constants into interfaces and refactor
 *    cgreene   05/26/10 - convert to oracle packaging
 *    cgreene   04/26/10 - XbranchMerge cgreene_tech43 from
 *                         st_rgbustores_techissueseatel_generic_branch
 *    cgreene   04/02/10 - remove deprecated LocaleContantsIfc and currencies
 *    abondala  01/03/10 - update header date
 *
 * ===========================================================================
 * $Log:
 *    5    360Commerce 1.4         3/21/2008 3:04:09 PM   Mathews Kochummen
 *         forward port v12x change to trunk. reviewed by alan
 *    4    360Commerce 1.3         4/25/2007 8:52:26 AM   Anda D. Cadar   I18N
 *         merge
 *
 *    3    360Commerce 1.2         3/31/2005 4:28:18 PM   Robert Pearse
 *    2    360Commerce 1.1         3/10/2005 10:21:56 AM  Robert Pearse
 *    1    360Commerce 1.0         2/11/2005 12:11:15 PM  Robert Pearse
 *
 *   Revision 1.6  2004/06/22 19:11:38  crain
 *   @scr 5470 add Gift Card or Gift Cert to Sell Item screen; select More->Gift Card/Cert->Undo - a twin of the first Gift Card or Cert is added to transaction
 *
 *   Revision 1.5  2004/05/17 20:45:07  crain
 *   @scr 4154 Issuing Gift Cert over 500.00
 *
 *   Revision 1.4  2004/04/01 01:46:32  crain
 *   @scr 4105 Foreign Currency
 *
 *   Revision 1.3  2004/03/25 20:00:18  crain
 *   @scr 3814 Issue Gift Certificate
 *
 *   Revision 1.2  2004/03/03 23:15:14  bwf
 *   @scr 0 Fixed CommonLetterIfc deprecations.
 *
 *   Revision 1.1  2004/02/20 14:15:17  crain
 *   @scr 3814 Issue Gift Certificate
 *
 * ===========================================================================
 */
package oracle.retail.stores.pos.services.giftcertificate.issue;

import java.util.Locale;

import oracle.retail.stores.commerceservices.common.currency.CurrencyIfc;
import oracle.retail.stores.common.parameter.ParameterConstantsIfc;
import oracle.retail.stores.common.utility.BigDecimalConstants;
import oracle.retail.stores.common.utility.LocaleMap;
import oracle.retail.stores.domain.DomainGateway;
import oracle.retail.stores.domain.event.PriceChangeIfc;
import oracle.retail.stores.domain.stock.GiftCertificateItemIfc;
import oracle.retail.stores.domain.utility.LocaleConstantsIfc;
import oracle.retail.stores.foundation.manager.ifc.JournalManagerIfc;
import oracle.retail.stores.foundation.manager.ifc.ParameterManagerIfc;
import oracle.retail.stores.foundation.manager.ifc.UIManagerIfc;
import oracle.retail.stores.foundation.manager.parameter.ParameterException;
import oracle.retail.stores.foundation.tour.application.Letter;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.pos.manager.ifc.UtilityManagerIfc;
import oracle.retail.stores.pos.services.PosSiteActionAdapter;
import oracle.retail.stores.pos.services.common.CommonLetterIfc;
import oracle.retail.stores.pos.services.sale.SaleCargo;
import oracle.retail.stores.pos.ui.DialogScreensIfc;
import oracle.retail.stores.pos.ui.POSUIManagerIfc;
import oracle.retail.stores.pos.ui.beans.DialogBeanModel;
import oracle.retail.stores.pos.ui.beans.NavigationButtonBeanModel;
import oracle.retail.stores.pos.ui.beans.POSBaseBeanModel;

/**
 * This site gets the amount and validates it

 */
public class GiftCertificateIssueActionSite extends PosSiteActionAdapter
    implements ParameterConstantsIfc
{
    private static final long serialVersionUID = -7910498341412570342L;
    /**
     * less than minimum amount dialog id
     */
    public static final String LESS_THAN_MIM_AMOUNT_DIALOG_ID = "LessThanMinimumAmount";

    /**
     * more than maximum amount dialog id
     */
    public static final String MORE_THAN_MAX_AMOUNT_DIALOG_ID = "MoreThanMaximumAmount";

    /**
     * gift certificate tag
     */
    public static final String GIFT_CERTIFICATE_TAG = "GiftCertificate";

    /**
     * gift certificate text
     */
    public static final String GIFT_CERTIFICATE = "Gift Certificate";

    /* (non-Javadoc)
     * @see oracle.retail.stores.foundation.tour.application.SiteActionAdapter#arrive(oracle.retail.stores.foundation.tour.ifc.BusIfc)
     */
    @Override
    public void arrive(BusIfc bus)
    {
        SaleCargo cargo = (SaleCargo) bus.getCargo();
        UtilityManagerIfc utility = (UtilityManagerIfc) bus.getManager(UtilityManagerIfc.TYPE);
        ParameterManagerIfc pm =
            (ParameterManagerIfc) bus.getManager(ParameterManagerIfc.TYPE);
        POSUIManagerIfc ui =
            (POSUIManagerIfc) bus.getManager(UIManagerIfc.TYPE);
        String letterName = bus.getCurrentLetter().getName();
        GiftCertificateItemIfc pluItem = (GiftCertificateItemIfc) cargo.getPLUItem();

        NavigationButtonBeanModel navModel = ((POSBaseBeanModel)ui.getModel()).getLocalButtonBeanModel();
        if(navModel.checkLetter(letterName))
        {
            CurrencyIfc amount = DomainGateway.getBaseCurrencyInstance();
            StringBuffer sb = new StringBuffer(letterName);
            sb.append(ParameterConstantsIfc.TENDER_GiftCardReloadAmount_suffix);
            String amountS = getParameter(pm, sb.toString());
            if (amountS != null)
            {
                amount.setStringValue(amountS);
                PriceChangeIfc priceChange = DomainGateway.getFactory().getPriceChangeInstance();
                priceChange.setOverridePriceAmount(amount);
                pluItem.setPermanentPriceChanges(new PriceChangeIfc[]{priceChange});
            }
        }

        // set the max and min issue amounts
        setMaxMinAmounts(pm, pluItem);

        // check amount
        if (pluItem.isMoreThanMax())
        {
            ui.showScreen(
                POSUIManagerIfc.DIALOG_TEMPLATE, getDialogModel(utility, MORE_THAN_MAX_AMOUNT_DIALOG_ID));
        }
        else if (pluItem.isLessThanMin())
        {
            ui.showScreen(
                POSUIManagerIfc.DIALOG_TEMPLATE, getDialogModel(utility, LESS_THAN_MIM_AMOUNT_DIALOG_ID));
        }
        else
        {
            cargo.setItemQuantity(BigDecimalConstants.ONE_AMOUNT);
            JournalManagerIfc jmi = (JournalManagerIfc)bus.getManager(JournalManagerIfc.TYPE);
            jmi.journal(
                cargo.getTransaction().getCashier().getLoginID(),
                cargo.getTransaction().getTransactionID(),
                pluItem.toJournalString(LocaleMap.getLocale(LocaleConstantsIfc.JOURNAL)));

           bus.mail(new Letter(CommonLetterIfc.CONTINUE), BusIfc.CURRENT);
        }
    }

    /**
     * Gets the parameter passed in as argument.
     * 
     * @param pm ParameterManagerIfc
     * @param parameterName String
     * @return String
     */
    protected String getParameter(ParameterManagerIfc pm, String parameterName)
    {
        String parameter = null;
        try
        {
            parameter = pm.getStringValue(parameterName);
        }
        catch (ParameterException pe)
        {
            logger.error(pe.getMessage());
        }

        return parameter;
    }

    /**
     * Sets the maximum and minimum amounts for issue gift certificate.
     * 
     * @param pm ParameterManagerIfc
     * @param pluItem GiftCertificateItemIfc
     */
    protected void setMaxMinAmounts(ParameterManagerIfc pm, GiftCertificateItemIfc pluItem)
    {
        // set initial values
        CurrencyIfc maxIssueAmount = DomainGateway.getBaseCurrencyInstance();
        maxIssueAmount.setStringValue("500.0");
        String maxIssue = getParameter(pm, ParameterConstantsIfc.TENDER_MaximumGiftCertificateIssueAmount);
        if ( maxIssue != null)
        {
            maxIssueAmount.setStringValue(maxIssue);
        }

        pluItem.setMaxAmount(maxIssueAmount);

        CurrencyIfc minIssueAmount = DomainGateway.getBaseCurrencyInstance();
        minIssueAmount.setStringValue("5.0");
        String minIssue = getParameter(pm, ParameterConstantsIfc.TENDER_MinimumGiftCertificateIssueAmount);
        if ( minIssue != null)
        {
            minIssueAmount.setStringValue(minIssue);
        }

        pluItem.setMinAmount(minIssueAmount);
    }

    /**
     * Get more than maximum issue amount error dialog model
     * 
     * @param utility Utility Manager
     * @param resourceID String
     * @return DialogBeanModel
     */
    public DialogBeanModel getDialogModel(UtilityManagerIfc utility, String resourceID)
    {
        Locale locale = LocaleMap.getLocale(LocaleConstantsIfc.USER_INTERFACE);
        String[] args = new String[2];
        args[0] = utility.retrieveDialogText(GIFT_CERTIFICATE_TAG,
                                             GIFT_CERTIFICATE).toLowerCase(locale);
        args[1] = args[0];

        DialogBeanModel dialogModel = new DialogBeanModel();
        dialogModel.setResourceID(resourceID);
        dialogModel.setType(DialogScreensIfc.ACKNOWLEDGEMENT);
        dialogModel.setArgs(args);
        return dialogModel;
    }

}
