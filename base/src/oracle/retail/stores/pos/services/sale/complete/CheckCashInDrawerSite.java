/* ===========================================================================
* Copyright (c) 2008, 2011, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/sale/complete/CheckCashInDrawerSite.java /main/13 2011/12/05 12:16:21 cgreene Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED (MM/DD/YY)
 *    cgreen 12/05/11 - updated from deprecated packages and used more
 *                      bigdecimal constants
 *    cgreen 02/15/11 - move constants into interfaces and refactor
 *    nkgaut 09/20/10 - refractored code to use a single class for checking
 *                      cash in drawer
 *    cgreen 05/27/10 - convert to oracle packaging
 *    cgreen 05/26/10 - convert to oracle packaging
 *    cgreen 04/26/10 - XbranchMerge cgreene_tech43 from
 *                      st_rgbustores_techissueseatel_generic_branch
 *    cgreen 04/02/10 - remove deprecated LocaleContantsIfc and currencies
 *    abonda 01/03/10 - update header date
 *    nkgaut 02/25/09 - Fix for getting correct cash amount present in till
 *    mdecam 02/09/09 - Refactored to use non-deprecated methods.
 *    cgreen 02/04/09 - convert site from just displaying a message to actually
 *                      checking the drawer amounts and optionally displaying
 *                      the message.
 *    nkgaut 09/23/08 - Added Banner colour
 *    nkgaut 09/18/08 - A new site class for cash drawer modal warning message

 * ===========================================================================
 */
package oracle.retail.stores.pos.services.sale.complete;

import java.awt.Color;

import oracle.retail.stores.domain.utility.LocaleConstantsIfc;
import oracle.retail.stores.pos.services.common.AbstractFinancialCargoIfc;
import oracle.retail.stores.pos.services.common.CommonLetterIfc;
import oracle.retail.stores.domain.financial.FinancialCountIfc;
import oracle.retail.stores.domain.financial.FinancialCountTenderItemIfc;
import oracle.retail.stores.domain.financial.FinancialTotalsIfc;
import oracle.retail.stores.foundation.manager.ifc.ParameterManagerIfc;
import oracle.retail.stores.foundation.manager.ifc.UIManagerIfc;
import oracle.retail.stores.foundation.manager.parameter.ParameterException;
import oracle.retail.stores.foundation.tour.application.Letter;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.common.utility.LocaleMap;
import oracle.retail.stores.pos.services.PosSiteActionAdapter;
import oracle.retail.stores.pos.ui.DialogScreensIfc;
import oracle.retail.stores.pos.ui.POSUIManagerIfc;
import oracle.retail.stores.pos.ui.beans.DialogBeanModel;
import oracle.retail.stores.pos.ui.plaf.UIFactory;

/**
 * This site is called by UI Framework and checks if a cash drawer OVER Warning
 * message should be displayed.
 */
public class CheckCashInDrawerSite extends PosSiteActionAdapter
{
    private static final long serialVersionUID = 5835401133241101966L;

    /**
     * revision number supplied by source-code-control system
     */
    public static final String revisionNumber = "$Revision:";
    /**
     * Called by UI framework to display dialog message.
     *
     * @param bus Service Bus
     */
    @Override
    public void arrive(BusIfc bus)
    {
        ParameterManagerIfc paramManager = (ParameterManagerIfc)bus.getManager(ParameterManagerIfc.TYPE);
        AbstractFinancialCargoIfc cargo = null;
        double totalCurrency = 0.00f;
        boolean totalCurrencyCalculated = false;

        // variable for parameter cash amount OVER
        double cashAmountOverFloat = 0.00f;
        // variable for parameter cash amount UNDER
        double cashAmountUnderFloat = 0.00f;
        // boolean for cash amount OVER Notification
        boolean bOverCashAmtNotificationReqd = false;


        if (bus.getCargo() instanceof AbstractFinancialCargoIfc)
        {
            FinancialTotalsIfc fc = null;
            cargo = (AbstractFinancialCargoIfc)bus.getCargo();
            fc = cargo.getRegister().getTillByID(cargo.getRegister().getCurrentTillID()).getTotals();
            totalCurrencyCalculated = true;
            FinancialCountIfc enter = fc.getCombinedCount().getExpected();
            FinancialCountTenderItemIfc[] sumTenders = enter.getSummaryTenderItems();

            if (sumTenders != null)
            {
                for (int i = 0; i < sumTenders.length; i++)
                {
                    if (sumTenders[i].getDescription().equals("Cash"))
                    {
                        totalCurrency = sumTenders[i].getAmountTotal().getDoubleValue();
                    }
                }
                
                try
                {
                    cashAmountOverFloat = Float.parseFloat(paramManager.getStringValue("CashAmountOverWarningFloat"));
                    cashAmountUnderFloat = Float.parseFloat(paramManager.getStringValue("CashAmountUnderWarningFloat"));
                }
                catch (NumberFormatException e1)
                {
                    logger.warn("Cash drawer warning parameters not in correct format.");
                }
                catch (ParameterException e1)
                {
                    logger.warn("Unable to get the parameters for cash drawer warning message");
                }

                //Check for over cash notification
                if (cashAmountOverFloat != 0.0 
                        && totalCurrency >= cashAmountOverFloat)
                {
                    bOverCashAmtNotificationReqd = true;
                }
                
                //Check for under cash notification
                if (totalCurrencyCalculated 
                        && cashAmountUnderFloat != 0.0 && totalCurrency <= cashAmountUnderFloat)
                {
                    cargo.setCashDrawerUnderWarning(true);
                }
            }
        }

        if (bOverCashAmtNotificationReqd)
        {
            // show the over warning screen else follow the default path
            POSUIManagerIfc ui = (POSUIManagerIfc)bus.getManager(UIManagerIfc.TYPE);
            DialogBeanModel dialogBean = new DialogBeanModel();
            Color BannerColor = Color.RED;
            String strBannerColor = UIFactory.getInstance().getUIProperty("Color.attention",
            		LocaleMap.getLocale(LocaleConstantsIfc.USER_INTERFACE));
            if (!strBannerColor.equals(""))
            {
                BannerColor = Color.decode(strBannerColor);
            }
            dialogBean.setResourceID("OverCashDrawerWarning");
            dialogBean.setType(DialogScreensIfc.ACKNOWLEDGEMENT);
            dialogBean.setButtonLetter(DialogScreensIfc.BUTTON_OK, "Continue");
            dialogBean.setBannerColor(BannerColor);

            ui.showScreen(POSUIManagerIfc.DIALOG_TEMPLATE, dialogBean);
        }
        else
        {
            // cash levels ok
            bus.mail(new Letter(CommonLetterIfc.OK), BusIfc.CURRENT);
        }
    }
}
