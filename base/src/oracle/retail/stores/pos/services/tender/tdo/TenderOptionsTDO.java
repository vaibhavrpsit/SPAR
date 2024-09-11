/* ===========================================================================
* Copyright (c) 1998, 2014, Oracle and/or its affiliates. All rights reserved.
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/tender/tdo/TenderOptionsTDO.java /main/25 2014/05/28 19:28:41 yiqzhao Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    yiqzhao   05/28/14 - Disable instant credit tender when resuming from ASA
 *    abhinavs  08/06/13 - Fix to display correct business customer name
 *    rgour     06/17/13 - setting captured customer value for shipping method
 *                         screen if no customer is linked to the transaction
 *    jswan     04/12/13 - Removed check for Layaway and Order transactions
 *                         when enabling instant credit tender button.
 *    sgu       10/17/12 - use tender transaction total to support partial
 *                         order item pickup or cancel
 *    blarsen   10/20/11 - Reworked logic that conrols the credit/debit
 *                         button's state and label. Also removed software for
 *                         credit-only and debit-only buttons (these no longer
 *                         exist).
 *    rrkohli   08/18/11 - fix for 'credit/debit tender sometimes grayed out in
 *                         reentry mode'
 *    blarsen   07/28/11 - Changed house-account-button to use
 *                         HouseCardsAccepted parameter instead of CardTypes
 *                         parameter (which was deleted).
 *    cgreene   07/21/11 - remove DebitBinFileLookup and DebitCardsAccepted
 *                         parameters for APF
 *    cgreene   07/21/11 - remove ability to show separate Debit button
 *    ohorne    06/09/11 - added House Account button
 *    sgu       06/02/11 - enable/disable house account/instant credit button
 *    rrkohli   05/09/11 - adding getStatusBean() method in utility class for
 *                         POS UI quickwin
 *    rrkohli   05/06/11 - pos ui quickwin
 *    npoola    12/20/10 - action button texts are moved to CommonActionsIfc
 *    nkgautam  09/14/10 - disable instant credit for bill pay transaction and
 *                         changed bin file lookup setting
 *    cgreene   05/26/10 - convert to oracle packaging
 *    cgreene   04/26/10 - XbranchMerge cgreene_tech43 from
 *                         st_rgbustores_techissueseatel_generic_branch
 *    cgreene   04/02/10 - remove deprecated LocaleContantsIfc and currencies
 *    abondala  01/03/10 - update header date
 *    asinton   06/01/09 - Disable the Debit button when in transaction
 *                         re-entry mode.
 *    cgreene   03/11/09 - back out mahising fix in favor of fix to
 *                         PromtAndLiveResponseBean fix for promting amount due
 *    mahising  02/22/09 - Fixed subtotal tax model after tender for PDO
 *    mahising  02/20/09 - Fixed issue related to split tender balance due.
 *
 * ===========================================================================
 * $Log:
 *  5    360Commerce 1.4         6/6/2008 11:44:48 AM   Sima Patel      Disable
 *        Instant Credit button for Special Order pickup, Special Order
 *       partial, Layaway Pickup and Lawaway Payment
 *  4    360Commerce 1.3         2/22/2008 4:10:15 PM   Deepti Sharma   disable
 *        Instant Credit in transaction Reentry mode
 *  3    360Commerce 1.2         3/31/2005 4:30:25 PM   Robert Pearse
 *  2    360Commerce 1.1         3/10/2005 10:26:02 AM  Robert Pearse
 *  1    360Commerce 1.0         2/11/2005 12:14:56 PM  Robert Pearse
 * $
 * Revision 1.16  2004/08/23 16:16:01  cdb
 * @scr 4204 Removed tab characters
 *
 * Revision 1.15  2004/08/20 21:36:30  bwf
 * @scr 6553 Make it so that credit/debit and gift card are the only buttons
 *                   enabled during swipe anytime.
 *
 * Revision 1.14  2004/07/14 18:47:08  epd
 * @scr 5955 Addressed issues with Utility class by making constructor protected and changing all usages to use factory method rather than direct instantiation
 *
 * Revision 1.13  2004/06/28 21:18:22  crain
 * @scr 5591 Tender_Foreign Currency Label is Incorrect on Tender Options Screen
 *
 * Revision 1.12  2004/06/08 18:25:53  dfierling
 * @scr 5358 - Fixed Foriegn Tender button for params
 *
 * Revision 1.11  2004/04/21 15:08:58  blj
 * @scr 3872 - cleanup from code review
 *
 * Revision 1.10  2004/04/09 21:09:35  bwf
 * @scr 3377 Make sure Credit/Debit is not an option if credit cards
 * are not accepted.
 *
 * Revision 1.8  2004/04/09 19:03:58  bwf
 * @scr 4350 Changed from gateway.getProperites to util.getparamter.
 *
 * Revision 1.7  2004/03/31 19:52:59  crain
 * @scr 4105 Foreign Currency
 *
 * Revision 1.6  2004/03/26 21:18:19  cdb
 * @scr 4204 Removing Tabs.
 *
 * Revision 1.5  2004/03/24 23:23:55  bjosserand
 * @scr 4093 Transaction Reentry
 * Revision 1.4 2004/02/18 17:00:21 nrao @scr 3722 Fixed so that Instant Credit is no
 * longer an option when making a House Account Payment.
 *
 * Revision 1.3 2004/02/12 16:48:25 mcs Forcing head revision
 *
 * Revision 1.2 2004/02/11 21:23:20 rhafernik @scr 0 Log4J conversion and code cleanup
 *
 * Revision 1.1.1.1 2004/02/11 01:04:12 cschellenger updating to pvcs 360store-current
 *
 *
 *
 * Rev 1.4 Jan 06 2004 09:52:08 rsachdeva Refactoring Resolution for POS SCR-3551: Tender using Canadian Cash/Canadian
 * Travelers Check/Canadian Check
 *
 * Rev 1.3 Dec 10 2003 08:47:50 rsachdeva Alternate Currency Resolution for POS SCR-3551: Tender using Canadian Cash
 *
 * Rev 1.2 Dec 08 2003 09:23:22 rsachdeva Alternate Currency Resolution for POS SCR-3551: Tender using Canadian Cash
 *
 * Rev 1.1 Nov 19 2003 14:11:30 epd TDO refactoring to use factory
 *
 * Rev 1.0 Nov 04 2003 11:19:14 epd Initial revision.
 *
 * Rev 1.4 Nov 03 2003 17:39:58 nrao Added button for Instant Credit.
 *
 * Rev 1.3 Oct 30 2003 13:28:40 epd enabled alternate tenders
 *
 * Rev 1.2 Oct 26 2003 14:23:42 blj updated for money order tender
 *
 * Rev 1.1 Oct 20 2003 16:34:10 epd Updated logic determining which buttons are enabled/disabled
 *
 * Rev 1.0 Oct 17 2003 12:45:28 epd Initial revision.
 * ===========================================================================
 */
package oracle.retail.stores.pos.services.tender.tdo;

import java.util.ArrayList;
import java.util.HashMap;

import oracle.retail.stores.common.parameter.ParameterConstantsIfc;
import oracle.retail.stores.domain.customer.CaptureCustomer;
import oracle.retail.stores.domain.customer.CaptureCustomerIfc;
import oracle.retail.stores.domain.customer.CustomerIfc;
import oracle.retail.stores.domain.order.OrderConstantsIfc;
import oracle.retail.stores.domain.transaction.OrderTransactionIfc;
import oracle.retail.stores.domain.transaction.TenderableTransactionIfc;
import oracle.retail.stores.domain.transaction.TransactionConstantsIfc;
import oracle.retail.stores.domain.transaction.TransactionIfc;
import oracle.retail.stores.domain.transaction.SaleReturnTransactionIfc;
import oracle.retail.stores.domain.utility.LocaleConstantsIfc;
import oracle.retail.stores.domain.utility.LocaleUtilities;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.pos.ado.ADO;
import oracle.retail.stores.pos.ado.ADOException;
import oracle.retail.stores.pos.ado.tender.TenderTypeEnum;
import oracle.retail.stores.pos.ado.transaction.RetailTransactionADOIfc;
import oracle.retail.stores.pos.ado.utility.Utility;
import oracle.retail.stores.pos.ado.utility.UtilityIfc;
import oracle.retail.stores.pos.config.bundles.BundleConstantsIfc;
import oracle.retail.stores.pos.manager.ifc.UtilityManagerIfc;
import oracle.retail.stores.pos.services.common.AbstractFinancialCargo;
import oracle.retail.stores.pos.services.common.CommonActionsIfc;
import oracle.retail.stores.pos.services.common.TagConstantsIfc;
import oracle.retail.stores.pos.tdo.TDOAdapter;
import oracle.retail.stores.pos.tdo.TDOUIIfc;
import oracle.retail.stores.pos.ui.UIUtilities;
import oracle.retail.stores.pos.ui.beans.NavigationButtonBeanModel;
import oracle.retail.stores.pos.ui.beans.POSBaseBeanModel;
import oracle.retail.stores.pos.ui.beans.StatusBeanModel;
import oracle.retail.stores.pos.ui.beans.TenderBeanModel;

import org.apache.log4j.Logger;

/**
 * TenderOptionsTDO builds the TenderBeanModel and calculates the appropriate
 * enabled-ness of the tender options buttons on the TENDER_OPTIONS screen.
 */
public class TenderOptionsTDO extends TDOAdapter implements TDOUIIfc
{
    protected static final Logger logger = Logger.getLogger(TenderOptionsTDO.class);

    // attributeMap constants
    public static final String BUS = "Bus";
    public static final String TRANSACTION = "Transaction";
    public static final String TRANSACTION_REENTRY_MODE = "TransactionReentry";
    public static final String SWIPE_ANYTIME = "SwipeAnytime";
    protected final String APPLICATION_PROP_KEY = "application";

    /* (non-Javadoc)
     * @see oracle.retail.stores.pos.tdo.TDOUIIfc#buildBeanModel(java.util.HashMap)
     */
    public POSBaseBeanModel buildBeanModel(HashMap attributeMap)
    {
        BusIfc bus = (BusIfc) attributeMap.get(BUS);
        RetailTransactionADOIfc txnADO = (RetailTransactionADOIfc) attributeMap.get(TRANSACTION);

        // Get RDO version of transaction for use in some processing
        TenderableTransactionIfc txnRDO = (TenderableTransactionIfc) ((ADO) txnADO).toLegacy();

        // get new tender bean model
        TenderBeanModel model = new TenderBeanModel();
        // populate tender bean model w/ tender and totals info
        model.setTenderLineItems(txnRDO.getTenderLineItemsVector());

        if (txnRDO instanceof OrderTransactionIfc
                && ((OrderTransactionIfc)txnRDO).getOrderType() == OrderConstantsIfc.ORDER_TYPE_ON_HAND
                && ((OrderTransactionIfc)txnRDO).getTransactionType() == TransactionIfc.TYPE_ORDER_INITIATE)
        {
            model.setTransactionTotals(txnRDO.getTransactionTotals());
        }
        else
        {
            model.setTransactionTotals(txnRDO.getTenderTransactionTotals());
        }


        // set customer information
        StatusBeanModel sModel = getStatusBean(bus, txnRDO.getCustomer());
        if (txnRDO.getCustomer() == null)
        {
            if (txnRDO.getCaptureCustomer() != null)
            {
                CaptureCustomerIfc captureCustomer = (CaptureCustomer)txnRDO.getCaptureCustomer();
                String customerName = captureCustomer.getFirstLastName();
                sModel.setCustomerName(customerName);
            }
        }
        if (sModel != null)
        {
            model.setStatusBeanModel(sModel);
        }

        // set the local navigation button bean model
        model.setLocalButtonBeanModel(getNavigationBeanModel(txnADO.getEnabledTenderOptions(), bus, attributeMap));

        // This is not a return
        model.setReturn(false);

        return model;
    }

    /**
     * builds status bean based on customer information
     *
     * @param bus
     * @param customer
     * @return
     */
    protected StatusBeanModel getStatusBean(BusIfc bus, CustomerIfc customer)
    {
      StatusBeanModel sModel = UIUtilities.getStatusBean((AbstractFinancialCargo) bus.getCargo());
        if (customer != null)
        {
            String[] vars = { customer.getFirstName(), customer.getLastName()};
            if(customer.isBusinessCustomer())
            {
                vars[0]=customer.getLastName();
                vars[1]="";
            }
            UtilityManagerIfc utility = (UtilityManagerIfc) bus.getManager(UtilityManagerIfc.TYPE);
            String pattern =
                utility.retrieveText(
                    "CustomerAddressSpec",
                    BundleConstantsIfc.CUSTOMER_BUNDLE_NAME,
                    TagConstantsIfc.CUSTOMER_NAME_TAG,
                    TagConstantsIfc.CUSTOMER_NAME_PATTERN_TAG,
                    LocaleConstantsIfc.USER_INTERFACE);
            String customerName = LocaleUtilities.formatComplexMessage(pattern, vars);
            sModel.setCustomerName(customerName);
        }
        return sModel;
    }

    /**
     * enables/disables tender buttons as they exist in enabledTypes array.
     *
     * @param enabledTypes
     * @return
     */

    protected NavigationButtonBeanModel getNavigationBeanModel(
        TenderTypeEnum[] enabledTypes,
        BusIfc bus,
        HashMap attributeMap)
    {
        // convert to list
        ArrayList<TenderTypeEnum> typeList = new ArrayList<TenderTypeEnum>(enabledTypes.length);
        for (int i = 0; i < enabledTypes.length; i++)
        {
            typeList.add(enabledTypes[i]);
        }

        NavigationButtonBeanModel navModel = new NavigationButtonBeanModel();
        navModel.setButtonEnabled(CommonActionsIfc.CASH, typeList.contains(TenderTypeEnum.CASH));
        navModel.setButtonEnabled(CommonActionsIfc.CHECK, typeList.contains(TenderTypeEnum.CHECK));
        navModel.setButtonEnabled(CommonActionsIfc.COUPON, typeList.contains(TenderTypeEnum.COUPON));
        navModel.setButtonEnabled(CommonActionsIfc.HOUSEACCOUNT, typeList.contains(TenderTypeEnum.HOUSE_ACCOUNT));
        navModel.setButtonEnabled(CommonActionsIfc.GIFT_CARD, typeList.contains(TenderTypeEnum.GIFT_CARD));
        navModel.setButtonEnabled(CommonActionsIfc.GIFT_CERT, typeList.contains(TenderTypeEnum.GIFT_CERT));
        navModel.setButtonEnabled(CommonActionsIfc.MALL_CERT, typeList.contains(TenderTypeEnum.MALL_CERT));
        navModel.setButtonEnabled(CommonActionsIfc.PURCHASE_ORDER, typeList.contains(TenderTypeEnum.PURCHASE_ORDER));
        navModel.setButtonEnabled(CommonActionsIfc.STORE_CREDIT, typeList.contains(TenderTypeEnum.STORE_CREDIT));
        navModel.setButtonEnabled(CommonActionsIfc.TRAVEL_CHECK, typeList.contains(TenderTypeEnum.TRAVELERS_CHECK));
        navModel.setButtonEnabled(CommonActionsIfc.MONEY_ORDER, typeList.contains(TenderTypeEnum.MONEY_ORDER));

        // if debit is enabled AND checking BIN file, then enable credit/debit button
        // Also, we must use the correct lable for the Alternate button, depending
        // on this setting.

        UtilityIfc util = null;
        try
        {
            util = Utility.createInstance();
        }
        catch (ADOException e)
        {
            String message = "Configuration problem: could not instantiate UtilityIfc instance";
            logger.error(message, e);
            throw new RuntimeException(message, e);
        }

        String creditCards = util.getParameterValue(ParameterConstantsIfc.TENDER_CreditDebitCardsAccepted, "Y");
        boolean allowCreditCards = creditCards.equalsIgnoreCase("Y");
        Boolean transReentry = (Boolean) attributeMap.get(TRANSACTION_REENTRY_MODE);

        UtilityManagerIfc utility = (UtilityManagerIfc) bus.getManager(UtilityManagerIfc.TYPE);

        updateCreditDebitButton(utility, allowCreditCards, transReentry, typeList, navModel);


        String alternateButtonLabelKey = "";
        String alternateButtonLabelDefault = "";

        if(typeList.contains(TenderTypeEnum.DEBIT) || typeList.contains(TenderTypeEnum.CREDIT))
        {
            if (allowCreditCards)
            {
                alternateButtonLabelKey = "AlternateButtonKeyLabel";
                alternateButtonLabelDefault = "F6";
            }
            else
            {
                alternateButtonLabelKey = "AlternateButtonKeyLabel2";
                alternateButtonLabelDefault = "F7";
            }
        }

        // set button labels for alternate if alternate listed
        if (typeList.contains(TenderTypeEnum.ALTERNATE))
        {
            navModel.setButtonEnabled(CommonActionsIfc.ALTERNATE, true);
            String keyLabel =
                utility.retrieveText(
                        CommonActionsIfc.COMMON,
                        BundleConstantsIfc.TENDER_BUNDLE_NAME,
                        alternateButtonLabelKey,
                        alternateButtonLabelDefault,
                        LocaleConstantsIfc.USER_INTERFACE);
            navModel.setButtonKeyLabel(CommonActionsIfc.ALTERNATE, keyLabel);
            navModel.setButtonLabel(
                    CommonActionsIfc.ALTERNATE,
                    utility.retrieveText(CommonActionsIfc.COMMON, BundleConstantsIfc.TENDER_BUNDLE_NAME, "ForeignCurrency", "Foreign Currency"));

            if (util.getParameterValue("ForeignCurrency", "Y").equalsIgnoreCase("N"))
            {
                navModel.setButtonEnabled(CommonActionsIfc.ALTERNATE,false);
            }

        }

        RetailTransactionADOIfc txnADO = (RetailTransactionADOIfc) attributeMap.get(TRANSACTION);
        TenderableTransactionIfc txnRDO = (TenderableTransactionIfc) ((ADO) txnADO).toLegacy();

        // Enable the button for Instant Credit except for House Account payment and re-entry mode
        // Disable Instance Credit button for special order pickup, special order partial, layaway pickup and layaway payment
        boolean isWebManagedOrder = false;
        if ( txnRDO instanceof SaleReturnTransactionIfc && ((SaleReturnTransactionIfc)txnRDO).isWebManagedOrder())
        {
            isWebManagedOrder = true;
        }

        if (!transReentry.booleanValue() && !isWebManagedOrder
                && (util.getParameterValue("InstantCreditEnrollment", "Y").equalsIgnoreCase("Y"))
                && txnRDO.getTransactionType() != TransactionConstantsIfc.TYPE_HOUSE_PAYMENT
                && txnRDO.getTransactionType()!=TransactionConstantsIfc.TYPE_BILL_PAY)
        {
            String houseCardsAccepted = util.getParameterValue(ParameterConstantsIfc.TENDER_HouseCardsAccepted, "Y");
            boolean enableInstantCredit = "Y".equalsIgnoreCase(houseCardsAccepted);
            navModel.setButtonEnabled(CommonActionsIfc.INSTANT_CREDIT, enableInstantCredit);
        }
        else
        {
            navModel.setButtonEnabled(CommonActionsIfc.INSTANT_CREDIT, false);
        }

        // if a card was swiped for swipe anytime only allow credit, debit, or gift card
        Boolean swipeAnytime = (Boolean) attributeMap.get(SWIPE_ANYTIME);
        if (swipeAnytime.booleanValue())
        {
            navModel.setButtonEnabled(CommonActionsIfc.CASH, false);
            navModel.setButtonEnabled(CommonActionsIfc.CHECK, false);
            navModel.setButtonEnabled(CommonActionsIfc.COUPON, false);
            navModel.setButtonEnabled(CommonActionsIfc.GIFT_CERT, false);
            navModel.setButtonEnabled(CommonActionsIfc.MALL_CERT, false);
            navModel.setButtonEnabled(CommonActionsIfc.PURCHASE_ORDER, false);
            navModel.setButtonEnabled(CommonActionsIfc.STORE_CREDIT, false);
            navModel.setButtonEnabled(CommonActionsIfc.TRAVEL_CHECK, false);
            navModel.setButtonEnabled(CommonActionsIfc.MONEY_ORDER, false);
            navModel.setButtonEnabled(CommonActionsIfc.ALTERNATE,false);
            navModel.setButtonEnabled(CommonActionsIfc.INSTANT_CREDIT, false);
            navModel.setButtonEnabled(CommonActionsIfc.HOUSEACCOUNT, false);
        }

        return navModel;
    }


    /**
     * enables/disables credit/debit tender button considering tender types list, reentry mode and parameters
     *
     * @param utility
     * @param allowCreditCards
     * @param transReentry
     * @param typeList
     * @param navModel
     */
    protected void updateCreditDebitButton(UtilityManagerIfc utility, boolean allowCreditCards, Boolean transReentry, ArrayList<TenderTypeEnum> typeList, NavigationButtonBeanModel navModel)
    {
        // enable/disable credit/debit button considering parameter, reentry mode and allowed tenders
        // in some cases the "debit" label must be removed from the botton
        if (allowCreditCards && (typeList.contains(TenderTypeEnum.CREDIT) || typeList.contains(TenderTypeEnum.DEBIT)))
        {
            if (transReentry.booleanValue())
            {
                if (typeList.contains(TenderTypeEnum.CREDIT) && typeList.contains(TenderTypeEnum.DEBIT))
                {
                    navModel.setButtonEnabled(CommonActionsIfc.CREDIT_DEBIT, true);
                    navModel.setButtonLabel(CommonActionsIfc.CREDIT_DEBIT,
                        utility.retrieveText(CommonActionsIfc.COMMON, BundleConstantsIfc.TENDER_BUNDLE_NAME, CommonActionsIfc.CREDIT, "Credit"));
                }
                else if (typeList.contains(TenderTypeEnum.CREDIT))
                {
                    navModel.setButtonEnabled(CommonActionsIfc.CREDIT_DEBIT, true);
                    navModel.setButtonLabel(CommonActionsIfc.CREDIT_DEBIT,
                                    utility.retrieveText(CommonActionsIfc.COMMON, BundleConstantsIfc.TENDER_BUNDLE_NAME, CommonActionsIfc.CREDIT, "Credit"));

                }
                else // debit only - not allowed in reentry mode
                {
                    navModel.setButtonEnabled(CommonActionsIfc.CREDIT_DEBIT, false);
                }
            }
            else // not in reentry mode
            {
                if (typeList.contains(TenderTypeEnum.CREDIT) && typeList.contains(TenderTypeEnum.DEBIT))
                {
                    navModel.setButtonEnabled(CommonActionsIfc.CREDIT_DEBIT, true);
                }
                else if (typeList.contains(TenderTypeEnum.CREDIT))
                {
                    navModel.setButtonEnabled(CommonActionsIfc.CREDIT_DEBIT, true);
                    navModel.setButtonLabel(CommonActionsIfc.CREDIT_DEBIT,
                                    utility.retrieveText(CommonActionsIfc.COMMON, BundleConstantsIfc.TENDER_BUNDLE_NAME, CommonActionsIfc.CREDIT, "Credit"));
                }
                else // debit only
                {
                    navModel.setButtonEnabled(CommonActionsIfc.CREDIT_DEBIT, true);
                    navModel.setButtonLabel(CommonActionsIfc.CREDIT_DEBIT,
                                    utility.retrieveText(CommonActionsIfc.COMMON, BundleConstantsIfc.TENDER_BUNDLE_NAME, CommonActionsIfc.DEBIT, "Debit"));
                }
            }
        }
        else // credit or debit not allowed
        {
            navModel.setButtonEnabled(CommonActionsIfc.CREDIT_DEBIT, false);
        }
    }

    /* (non-Javadoc)
     * @see oracle.retail.stores.pos.tdo.TDOUIIfc#formatPoleDisplayLine1(oracle.retail.stores.pos.ado.transaction.RetailTransactionADOIfc)
     */
    public String formatPoleDisplayLine1(RetailTransactionADOIfc txnADO)
    {
        return null;
    }

    /* (non-Javadoc)
     * @see oracle.retail.stores.pos.tdo.TDOUIIfc#formatPoleDisplayLine2(oracle.retail.stores.pos.ado.transaction.RetailTransactionADOIfc)
     */
    public String formatPoleDisplayLine2(RetailTransactionADOIfc txnADO)
    {
        return null;
    }
}
