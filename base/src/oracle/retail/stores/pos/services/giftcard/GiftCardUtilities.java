/* ===========================================================================
* Copyright (c) 2008, 2013, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/giftcard/GiftCardUtilities.java /main/24 2013/01/07 11:08:03 yiqzhao Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED (MM/DD/YY)
 *    yiqzha 01/04/13 - Refactoring ItemManager
 *    icole  10/11/12 - Added storeID to inquiry as it's required for the PLU
 *                      with JPA.
 *    cgreen 12/05/11 - updated from deprecated packages and used more
 *                      bigdecimal constants
 *    cgreen 07/26/11 - updated methods with new repsonse codes
 *    cgreen 07/26/11 - removed tenderauth and giftcard.activation tours and
 *                      financialnetwork interfaces.
 *    cgreen 05/27/11 - move auth response objects into domain
 *    cgreen 05/20/11 - implemented enums for reponse code and giftcard status
 *                      code
 *    asinto 04/26/11 - Refactor gift card for APF
 *    npoola 08/12/10 - reverted back the transaction rsnayak_bug-9626720
 *    acadar 06/10/10 - use default locale for currency display
 *    acadar 06/09/10 - XbranchMerge acadar_tech30 from
 *                      st_rgbustores_techissueseatel_generic_branch
 *    cgreen 05/26/10 - convert to oracle packaging
 *    cgreen 04/26/10 - XbranchMerge cgreene_tech43 from
 *                      st_rgbustores_techissueseatel_generic_branch
 *    acadar 04/13/10 - display proper currency formatting
 *    acadar 04/08/10 - merge to tip
 *    acadar 04/06/10 - use default locale when displaying currency
 *    cgreen 04/02/10 - remove deprecated LocaleContantsIfc and currencies
 *    mpbarn 03/16/10 - Added methold createReturnErrorDialogModel()
 *    abonda 01/03/10 - update header date
 *    mahisi 02/27/09 - Fixed issue related to local navigation panal button
 *                      padding
 *    lslepe 01/07/09 - set letter to invalid gift card when trying to issue
 *                      with integration data (RMS doesn't support GCs)
 *    abonda 10/17/08 - I18Ning manufacturer name
 *    abonda 10/17/08 - I18Ning manufacturer name
 *    abonda 10/16/08 - I18Ning manufacturer name
 *    abonda 10/15/08 - I18Ning manufacturer name
 *
 *
 * ===========================================================================

     $Log:
      15   360Commerce 1.14        6/9/2008 4:04:39 PM    Alan N. Sinton  CR
           31972: The gift card selection is fixed in method
           GiftCardUtilities.getCurrentLineItem().  Code reviewed by Jack
           Swan.
      14   360Commerce 1.13        5/9/2008 6:52:15 PM    Alan N. Sinton  CR
           31648: Fixed PrepareRequestSite and GiftCardUtilities to prevent
           various application hangs and gift card processing.  Code reviewed
           by Tony Zgarba and Michael Barnett.
      13   360Commerce 1.12        4/7/2008 5:28:45 PM    Alan N. Sinton  CR
           30361: Gift Card Number retrieved from PromptAndResponseModel if
           swiped, from UI otherwise.  Code changes reviewed by Christian
           Greene.
      12   360Commerce 1.11        3/21/2008 5:12:57 AM   Vikram Gopinath CD
           #30706: changed createReloadErrorDialogModel to handle not found
           gift cards
      11   360Commerce 1.10        1/17/2008 5:24:06 PM   Alan N. Sinton  CR
           29954: Refactor of EncipheredCardData to implement interface and be
            instantiated using a factory.
      10   360Commerce 1.9         12/14/2007 8:59:59 AM  Alan N. Sinton  CR
           29761: Removed non-PABP compliant methods and modified card RuleIfc
            to take an instance of EncipheredCardData.
      9    360Commerce 1.8         12/12/2007 6:47:38 PM  Alan N. Sinton  CR
           29761: FR 8: Prevent repeated decryption of PAN data.
      8    360Commerce 1.7         8/14/2007 10:16:51 AM  Michael Boyd    v7.x
           merge - added logic to handle deactivate declines and also set
           request type to ISD as cash out
      7    360Commerce 1.6         7/11/2007 11:07:30 AM  Anda D. Cadar
           removed ISO currency code when using base currency
      6    360Commerce 1.5         5/30/2007 9:01:57 AM   Anda D. Cadar   code
           cleanup
      5    360Commerce 1.4         4/25/2007 8:52:27 AM   Anda D. Cadar   I18N
           merge

      4    360Commerce 1.3         1/22/2006 11:45:06 AM  Ron W. Haight
           removed references to com.ibm.math.BigDecimal
      3    360Commerce 1.2         3/31/2005 4:28:17 PM   Robert Pearse
      2    360Commerce 1.1         3/10/2005 10:21:56 AM  Robert Pearse
      1    360Commerce 1.0         2/11/2005 12:11:15 PM  Robert Pearse
     $
     Revision 1.18  2004/08/31 19:12:36  blj
     @scr 6855 - cleanup gift card credit code and fix defects found by PBY

     Revision 1.17  2004/08/19 21:55:41  blj
     @scr 6855 - Removed old code and fixed some flow issues with gift card credit.

     Revision 1.16  2004/07/17 16:46:56  epd
     @scr 4268 added null pointer check

     Revision 1.15  2004/07/17 16:24:35  epd
     @scr 4268 Fixed gift card item activation

     Revision 1.14  2004/07/16 22:12:05  epd
     @scr 4268 Changing flows to add gift card credit

     Revision 1.13  2004/06/24 15:31:38  blj
     @scr 5185 - Had to update gift card credit to get Amount from the tenderAttributes

     Revision 1.12  2004/06/21 22:19:26  lzhao
     @scr 5774, 5447: gift card return/reload.

     Revision 1.11  2004/06/11 19:10:35  lzhao
     @scr 4670: add customer present feature

     Revision 1.10  2004/06/09 14:23:37  lzhao
     @scr 5435: Gift Card Inquiry

     Revision 1.9  2004/06/03 14:47:43  epd
     @scr 5368 Update to use of DataTransactionFactory

     Revision 1.8  2004/04/29 22:16:59  lzhao
     @scr 4567: Gift Card Inquiry.

     Revision 1.7  2004/04/17 17:59:28  tmorris
     @scr 4332 -Replaced direct instantiation(new) with Factory call.

     Revision 1.6  2004/04/14 20:10:26  lzhao
     @scr  3872 Redeem, change gift card request type from String to in.

     Revision 1.5  2004/03/25 16:24:44  bwf
     @scr 4165 Update parameter name.

     Revision 1.4  2004/02/16 16:58:16  blj
     @scr 3824 added new method to handle request type.

     Revision 1.3  2004/02/12 16:50:20  mcs
     Forcing head revision

     Revision 1.2  2004/02/11 21:49:49  rhafernik
     @scr 0 Log4J conversion and code cleanup

     Revision 1.1.1.1  2004/02/11 01:04:16  cschellenger
     updating to pvcs 360store-current


 *
 *    Rev 1.8   Jan 30 2004 14:12:56   lzhao
 * add three new dialogs based on req. changes.
 * Resolution for 3371: Feature Enhancement:  Gift Card Enhancement
 *
 *    Rev 1.7   Dec 19 2003 15:21:34   lzhao
 * issue code review follow up
 * Resolution for 3371: Feature Enhancement:  Gift Card Enhancement
 *
 *    Rev 1.6   Dec 18 2003 09:43:56   lzhao
 * add decline and unknown error handle
 * Resolution for 3371: Feature Enhancement:  Gift Card Enhancement
 *
 *    Rev 1.5   Dec 16 2003 10:30:58   lzhao
 * add two methods for activation.
 * Resolution for 3371: Feature Enhancement:  Gift Card Enhancement
 *
 *    Rev 1.4   Dec 12 2003 14:09:56   lzhao
 * change the usage of the methods.
 * Resolution for 3371: Feature Enhancement:  Gift Card Enhancement
 *
 *    Rev 1.3   Dec 08 2003 09:17:08   lzhao
 * add method of createOfflineDialogModel() and para for binRangeCheck
 *
 *    Rev 1.2   Nov 26 2003 12:45:14   lzhao
 * remove ui as parameter in isValid() in gift card utility
 * Resolution for 3371: Feature Enhancement:  Gift Card Enhancement
 *
 *    Rev 1.1   Nov 26 2003 09:19:10   lzhao
 * use methods in utility, cleanup.
 * Resolution for 3371: Feature Enhancement:  Gift Card Enhancement
 *
 *    Rev 1.0   Nov 21 2003 14:46:12   lzhao
 * Initial revision.
 *
 * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */

package oracle.retail.stores.pos.services.giftcard;

import java.math.BigDecimal;
import java.util.Locale;

import oracle.retail.stores.commerceservices.common.currency.CurrencyIfc;
import oracle.retail.stores.commerceservices.common.currency.CurrencyServiceIfc;
import oracle.retail.stores.commerceservices.common.currency.CurrencyServiceLocator;
import oracle.retail.stores.common.utility.LocaleMap;
import oracle.retail.stores.common.utility.LocaleRequestor;
import oracle.retail.stores.common.utility.Util;
import oracle.retail.stores.domain.DomainGateway;
import oracle.retail.stores.domain.manager.item.ItemManagerIfc;
import oracle.retail.stores.domain.manager.payment.PaymentServiceResponseIfc.ResponseCode;
import oracle.retail.stores.domain.stock.GiftCardPLUItemIfc;
import oracle.retail.stores.domain.stock.ItemSearchCriteria;
import oracle.retail.stores.domain.stock.ItemSearchCriteriaIfc;
import oracle.retail.stores.domain.stock.PLUItemIfc;
import oracle.retail.stores.domain.utility.CardTypeIfc;
import oracle.retail.stores.domain.utility.LocaleConstantsIfc;
import oracle.retail.stores.domain.utility.LocaleUtilities;
import oracle.retail.stores.foundation.manager.data.DataException;
import oracle.retail.stores.foundation.manager.device.EncipheredCardDataIfc;
import oracle.retail.stores.foundation.manager.ifc.ParameterManagerIfc;
import oracle.retail.stores.foundation.manager.parameter.ParameterException;
import oracle.retail.stores.pos.config.bundles.BundleConstantsIfc;
import oracle.retail.stores.pos.manager.ifc.UtilityManagerIfc;
import oracle.retail.stores.pos.services.common.CommonLetterIfc;
import oracle.retail.stores.pos.ui.DialogScreensIfc;
import oracle.retail.stores.pos.ui.POSUIManagerIfc;
import oracle.retail.stores.pos.ui.beans.DialogBeanModel;
import oracle.retail.stores.pos.ui.beans.NavigationButtonBeanModel;
import oracle.retail.stores.pos.ui.beans.POSBaseBeanModel;
import oracle.retail.stores.pos.ui.beans.PromptAndResponseModel;
import oracle.retail.stores.pos.utility.CheckDigitUtility;

import org.apache.log4j.Logger;

/**
 * This class is a utility for gift card operations
 */
public class GiftCardUtilities implements GiftCardConstantsIfc
{
    /**
     * currencyService
     */
    public static CurrencyServiceIfc currencyService = CurrencyServiceLocator.getCurrencyService();

    /**
     * check the amount is less than min issue amount
     * 
     * @param pm parameter manager
     * @param amount amount to compare to
     * @param logger Message Logger, for debugging
     * @param serviceName service name, for debugging
     * @return true: is less than, false: is equal to or greater than
     */
    public static boolean isLessThanMin(ParameterManagerIfc pm, CurrencyIfc amount, Logger logger, String serviceName)
    {
        boolean lessThan = false;

        // set initial values
        CurrencyIfc minIssueAmount = DomainGateway.getBaseCurrencyInstance();
        minIssueAmount.setStringValue("0.0");
        try
        {
            minIssueAmount.setStringValue(pm.getStringValue("MinimumGiftCardIssueAmount"));
        }
        catch (ParameterException pe)
        {
            logger.warn(pe.getMessage());
        }
        if (amount.compareTo(minIssueAmount) == -1)
            lessThan = true;

        return lessThan;
    }

    /**
     * check the amount is more than max issue amount
     * 
     * @param pm parameter manager
     * @param amount amount to compare to
     * @param logger Message Logger, for debugging
     * @param serviceName service name, for debugging
     * @return boolean is valid amount
     */
    public static boolean isMoreThanMax(ParameterManagerIfc pm, CurrencyIfc amount, Logger logger, String serviceName)
    {
        boolean moreThan = false;

        // set initial values
        CurrencyIfc maxIssueAmount = DomainGateway.getBaseCurrencyInstance();
        maxIssueAmount.setStringValue("500.0");
        try
        {
            maxIssueAmount.setStringValue(pm.getStringValue("MaximumGiftCardIssueAmount"));
        }
        catch (ParameterException pe)
        {
            logger.warn(pe.getMessage());
        }
        if (amount.compareTo(maxIssueAmount) == 1)
            moreThan = true;

        return moreThan;
    }

    /**
     * convert String to Currency
     * 
     * @param String amount to convert
     * @return CurrencyIfc amount to return
     */
    public static CurrencyIfc getCurrency(String amount)
    {
        CurrencyIfc currencyAmount = DomainGateway.getBaseCurrencyInstance();
        currencyAmount.setStringValue(amount);
        return currencyAmount;
    }

    /**
     * Check the card number length is equal to zero.
     * 
     * @param model Pos base bean model
     * @param input gift card amount or card number
     * @param logger message log
     * @return boolean return true if it is equal to zero, otherwise return
     *         false
     */
    public static boolean isEmpty(POSBaseBeanModel model, String input, Logger logger, String serviceName)
    {
        boolean isEmpty = false;

        if (Util.isEmpty(input))
        {
            isEmpty = true;
            PromptAndResponseModel parModel = model.getPromptAndResponseModel();
            if ((parModel != null) && (parModel.isSwiped()))
            {
                if (logger.isInfoEnabled())
                    logger.info("Bad MSR Data received. Prompting user to enter the information manually...");
            }
            else
            {
                if (logger.isInfoEnabled())
                    logger.info("Invalid number received. Prompting user to re-enter the information ...");
            }
        }
        return isEmpty;
    }

    /**
     * Check the card is in valid bin range.
     * 
     * @param pm parameter manager
     * @param utility utility manager
     * @param cardData the EncipheredCardData instance that need to be checked
     * @param logger message log
     * @param serviceName service name
     * @return boolean return true if valid, otherwise return false
     */
    public static boolean isValidBinRange(ParameterManagerIfc pm, UtilityManagerIfc utility,
            EncipheredCardDataIfc cardData, Logger logger, String serviceName)
    {
        CardTypeIfc cardType = utility.getConfiguredCardTypeInstance();
        boolean isValid = true;

        boolean checkBinRange = false;
        try
        {
            String check = pm.getStringValue("GiftCardBinFileLookup");
            if (check.equals("Y"))
                checkBinRange = true;
        }
        catch (ParameterException pe)
        {
            logger.warn(pe.getMessage());
        }

        if (checkBinRange)
        {
            if (cardType != null)
            {
                String retCardType = cardType.identifyCardType(cardData, CardTypeIfc.GiftCard);
                if (retCardType.equals(CardTypeIfc.UNKNOWN))
                {
                    if (logger.isInfoEnabled())
                        logger.info("Invalid number received. Bin Range check is invalid. Prompting user to re-enter the information ...");
                    isValid = false;
                }
            }
            else
            {
                isValid = false;
            }
        }
        else
        {
            isValid = true;
        }
        return isValid;
    }

    /**
     * Check digit validation.
     * 
     * @param utility utility manager
     * @param cardNumber the gift card number that need to be checked
     * @param logger message log
     * @param serviceName service name
     * @return boolean return true if valid, otherwise return false
     */
    public static boolean isValidCheckDigit(UtilityManagerIfc utility, String cardNumber, Logger logger,
            String serviceName)
    {
        boolean isValid = false;
        if (!utility.validateCheckDigit(CheckDigitUtility.CHECK_DIGIT_FUNCTION_GIFTCARD, cardNumber))
        {
            // If check digit is not configured for gift card, the check digit
            // function will always return true
            if (logger.isInfoEnabled())
                logger.info("Invalid number received. check digit is invalid. Prompting user to re-enter the information ...");
        }
        else
        {
            isValid = true;
        }
        return isValid;
    }

    /**
     * Check digit validation.
     * 
     * @param utility utility manager
     * @param cardData the enciphered card data that need to be checked
     * @param logger message log
     * @param serviceName service name
     * @return boolean return true if valid, otherwise return false
     */
    public static boolean isValidCheckDigit(UtilityManagerIfc utility, EncipheredCardDataIfc cardData, Logger logger,
            String serviceName)
    {
        boolean isValid = false;
        if (!utility.validateCheckDigit(CheckDigitUtility.CHECK_DIGIT_FUNCTION_GIFTCARD, cardData))
        {
            // If check digit is not configured for gift card, the check digit
            // function will always return true
            if (logger.isInfoEnabled())
            {
                logger.info("Invalid number received. check digit is invalid. Prompting user to re-enter the information ...");
            }
        }
        else
        {
            isValid = true;
        }
        return isValid;
    }

    /**
     * Get item classification reference
     * 
     * @param ui UI Manager
     * @param cargo gift card cargo
     * @param itemID default gift card item id from properties file
     * @param logger log manager
     * @param serviceName service name in the tour
     * @param localeRequestor LocaleRequestor
     * @return gift card plu item
     */
    public static GiftCardPLUItemIfc getPluItem(POSUIManagerIfc ui, GiftCardCargo cargo, String defaultItemID,
            Logger logger, String serviceName, LocaleRequestor localeRequestor)
    {
        GiftCardPLUItemIfc item = null;
        ItemSearchCriteriaIfc inquiry = DomainGateway.getFactory().getItemSearchCriteriaInstance();
        inquiry.setLocaleRequestor(localeRequestor);
        inquiry.setSearchItemByItemID(true);
        inquiry.setStoreNumber(cargo.getStoreID());
        inquiry.setRetrieveFromStore(true);
        String itemID = "";
        item = (GiftCardPLUItemIfc)cargo.getPLUItem();
        if (item != null)
        {
            itemID = item.getItemID();
        }

        if (itemID.length() != 0)
        {
            inquiry.setItemID(itemID);
        }
        else
        {
            inquiry.setItemID(defaultItemID);
        }

        try
        {
            ItemManagerIfc mgr = (ItemManagerIfc)ui.getBus().getManager(ItemManagerIfc.TYPE);
            PLUItemIfc pluItem = mgr.getPluItem(inquiry);
            if (pluItem instanceof GiftCardPLUItemIfc)
            {
                item = (GiftCardPLUItemIfc)pluItem;
            }
        }
        catch (DataException de)
        {
            logger.warn("Unable to find pluItem " + de.getMessage());

            int errorCode = de.getErrorCode();
            cargo.setDataExceptionErrorCode(errorCode);
            DialogBeanModel model = GiftCardUtilities.createGenericDataBaseErrorDialogModel(DataException
                    .getErrorCodeString(errorCode));
            ui.showScreen(POSUIManagerIfc.DIALOG_TEMPLATE, model);
        }
        return item;
    }

    /**
     * create local navigation button bean model with gift card denominators the
     * buttons
     * 
     * @param utility utility manager
     * @param pm parameter manager
     * @param logger message log
     * @param serviceName service name
     * @return NavigationButtonBeanModel
     */
    public static NavigationButtonBeanModel getGiftCardDenominationsModel(UtilityManagerIfc utility,
            ParameterManagerIfc pm, Logger logger, String serviceName)
    {
        NavigationButtonBeanModel localNavigationButtonBeanModel = new NavigationButtonBeanModel();

        try
        {
            // load currency symbol based on locale
            Locale locale = LocaleMap.getLocale(LocaleMap.DEFAULT);

            for (int i = 0; i < ButtonLabelKeys.length; i++)
            {
                String buttonLabelParm = pm.getStringValue(ButtonLabelKeys[i] + GIFT_CARD_RELOAD_AMOUNT);

                BigDecimal amount = new BigDecimal(buttonLabelParm);
                // I18N change - remove currency symbol/ ISO currency code
                Object buttonAmountFormatParms[] = { "", currencyService.formatCurrency(amount, locale) };

                String buttonAmountFormatPattern = utility.retrieveText(GIFT_CARD_RELOAD_AMOUNT_BUTTON_SPEC,
                        BundleConstantsIfc.GIFTCARD_BUNDLE_NAME, AMOUNT_BUTTON_FORMAT_MESSAGE_TAG,
                        AMOUNT_BUTTON_FORMAT_MESSAGE_TEXT);

                // Format the amount
                String textAmount = LocaleUtilities.formatComplexMessage(buttonAmountFormatPattern,
                        buttonAmountFormatParms);

                // changes done for removing padding translation on currency
                // amount buttons at local navigation panel
                localNavigationButtonBeanModel.addButton(ButtonLabelKeys[i], buttonLabelParm, true, null,
                        "F" + (i + 2), null);
                localNavigationButtonBeanModel.setButtonLabel(ButtonLabelKeys[i], textAmount);
            }
        }
        catch (ParameterException pe)
        {
            logger.warn(pe.getMessage());
        }
        return localNavigationButtonBeanModel;
    }

    /**
     * get currency value showing on a denomination button
     * 
     * @param pm Parameter Manager
     * @param letterName letterName (key)
     * @param logger Message log
     * @param serviceName service name in tour
     * @return CurrencyIfc
     */
    public static CurrencyIfc getButtonDenomination(ParameterManagerIfc pm, String letterName, Logger logger,
            String serviceName)
    {
        CurrencyIfc currencyAmount = DomainGateway.getBaseCurrencyInstance();
        try
        {
            String amount = pm.getStringValue(letterName + GIFT_CARD_RELOAD_AMOUNT);
            currencyAmount.setStringValue(amount);
        }
        catch (ParameterException pe)
        {
            logger.warn(pe.getMessage());
        }
        return currencyAmount;
    }

    /**
     * get less than minimum issue amount error dialog model
     * 
     * @param utility Utility Manager
     * @return error dialog model
     */
    public static DialogBeanModel getLessThanMinDialogModel(UtilityManagerIfc utility)
    {
        Locale locale = LocaleMap.getLocale(LocaleConstantsIfc.USER_INTERFACE);
        String[] args = new String[2];
        args[0] = utility.retrieveDialogText(GIFT_CARD_TAG, GIFT_CARD).toLowerCase(locale);
        args[1] = args[0];

        DialogBeanModel dialogModel = new DialogBeanModel();
        dialogModel.setResourceID(LESS_THAN_MIM_AMOUNT_DIALOG_ID);
        dialogModel.setType(DialogScreensIfc.ACKNOWLEDGEMENT);
        dialogModel.setArgs(args);
        return dialogModel;
    }

    /**
     * get more than maximum issue amount error dialog model
     * 
     * @param utility Utility Manager
     * @return error dialog model
     */
    public static DialogBeanModel getMoreThanMaxDialogModel(UtilityManagerIfc utility)
    {
        Locale locale = LocaleMap.getLocale(LocaleConstantsIfc.USER_INTERFACE);
        String[] args = new String[2];
        args[0] = utility.retrieveDialogText(GIFT_CARD_TAG, GIFT_CARD).toLowerCase(locale);
        args[1] = args[0];

        DialogBeanModel dialogModel = new DialogBeanModel();
        dialogModel.setResourceID(MORE_THAN_MAX_AMOUNT_DIALOG_ID);
        dialogModel.setType(DialogScreensIfc.ACKNOWLEDGEMENT);
        dialogModel.setArgs(args);
        return dialogModel;
    }

    /**
     * Shows the Bad MSR Read error dialog.
     * 
     * @param utility Utility Manager
     * @return error dialog model
     */
    public static DialogBeanModel createBadMSRReadDialogModel(UtilityManagerIfc utility)
    {
        // set the prompt argument
        String[] args = new String[2];
        args[1] = utility.retrieveDialogText(GIFT_CARD_TAG, GIFT_CARD);
        Locale locale = LocaleMap.getLocale(LocaleConstantsIfc.USER_INTERFACE);
        args[0] = args[1].toLowerCase(locale);

        DialogBeanModel dialogModel = new DialogBeanModel();
        dialogModel.setResourceID(BAD_MSR_READ_ERROR_DIALOG_ID);
        dialogModel.setType(DialogScreensIfc.ERROR);
        dialogModel.setButtonLetter(DialogScreensIfc.BUTTON_OK, INVALID_CARD_NUM_LETTER);
        dialogModel.setArgs(args);

        return dialogModel;
    }

    /**
     * If the gift card number does not pass bin range check or check digit, the
     * system will display is dialog.
     * 
     * @param utility utility manager
     * @return error dialog model
     */
    public static DialogBeanModel createInvalidGiftCardNumErrorDialogModel()
    {
        DialogBeanModel dialogModel = new DialogBeanModel();

        dialogModel.setResourceID(INVALID_GIFT_CARD_NUMBER_ERROR);
        dialogModel.setType(DialogScreensIfc.ERROR);

        dialogModel.setButtonLetter(DialogScreensIfc.BUTTON_OK, INVALID_CARD_NUM_LETTER);
        return dialogModel;
    }

    /**
     * If the gift card number does not pass bin range check or check digit, the
     * system will display is dialog.
     * 
     * @param utility utility manager
     * @param responseCode "Offline" or "Timeout"
     * @return error dialog model
     */
    public static DialogBeanModel createProcessorOfflineForInquiryModel(UtilityManagerIfc utility, ResponseCode responseCode)
    {
        String errorMessage = null;
        if (ResponseCode.Timeout == responseCode)
            errorMessage = utility.retrieveText(POSUIManagerIfc.DIALOG_SPEC, BundleConstantsIfc.DIALOG_BUNDLE_NAME,
                    ERROR_TIMEOUT_TAG, ERROR_TIMEOUT);
        else if (ResponseCode.Offline == responseCode)
            errorMessage = utility.retrieveText(POSUIManagerIfc.DIALOG_SPEC, BundleConstantsIfc.DIALOG_BUNDLE_NAME,
                    ERROR_OFFLINE_TAG, ERROR_OFFLINE);
        String args[] = new String[1];
        args[0] = errorMessage;
        DialogBeanModel dialogModel = new DialogBeanModel();
        dialogModel.setResourceID(GIFT_CARD_PROCESSOR_OFFLINE_ERRIR_DIALOG_ID);
        dialogModel.setType(DialogScreensIfc.ACKNOWLEDGEMENT);
        dialogModel.setButtonLetter(DialogScreensIfc.BUTTON_OK, CommonLetterIfc.FAILURE);
        dialogModel.setArgs(args);

        return dialogModel;
    }

    /**
     * If the gift card number information is not found while returning it, the
     * system will display this dialog.
     * 
     * @return error dialog model
     */
    public static DialogBeanModel createReturnErrorDialogModel()
    {
        DialogBeanModel dialogModel = new DialogBeanModel();
        dialogModel.setResourceID(RETURN_GIFT_CARD_NOT_FOUND_ERROR_DIALOG_ID);
        dialogModel.setType(DialogScreensIfc.ERROR);
        dialogModel.setButtonLetter(DialogScreensIfc.BUTTON_OK, CommonLetterIfc.FAILURE);

        return dialogModel;
    }

    /**
     * If the gift card number does not pass bin range check or check digit, the
     * system will display is dialog.
     * 
     * @param utility utility manager
     * @param responseCode "Offline" or "Timeout"
     * @return error dialog model
     */
    public static DialogBeanModel createProcessorOfflineDialogModel(UtilityManagerIfc utility, ResponseCode responseCode)
    {
        String errorMessage = null;
        if (ResponseCode.Timeout == responseCode)
            errorMessage = utility.retrieveText(POSUIManagerIfc.DIALOG_SPEC, BundleConstantsIfc.DIALOG_BUNDLE_NAME,
                    ERROR_TIMEOUT_TAG, ERROR_TIMEOUT);
        else if (ResponseCode.Offline == responseCode)
            errorMessage = utility.retrieveText(POSUIManagerIfc.DIALOG_SPEC, BundleConstantsIfc.DIALOG_BUNDLE_NAME,
                    ERROR_OFFLINE_TAG, ERROR_OFFLINE);
        else if (ResponseCode.Declined == responseCode)
        {
            errorMessage = utility.retrieveText(POSUIManagerIfc.DIALOG_SPEC, BundleConstantsIfc.DIALOG_BUNDLE_NAME,
                    ERROR_DECLINED_TAG, ERROR_DECLINED);
        }
        String args[] = new String[1];
        args[0] = errorMessage;
        DialogBeanModel dialogModel = new DialogBeanModel();
        dialogModel.setResourceID(GIFT_CARD_PROCESSOR_OFFLINE_ERRIR_DIALOG_ID);
        dialogModel.setType(DialogScreensIfc.ACKNOWLEDGEMENT);
        dialogModel.setButtonLetter(DialogScreensIfc.BUTTON_OK, "Referral");
        dialogModel.setArgs(args);

        return dialogModel;
    }

    /**
     * If issue request activation fail, show dialog based on the response code.
     * 
     * @param utility utility manager
     * @param resposeCode response code from gift card activation center
     * @return error dialog model
     */
    public static DialogBeanModel createIssueErrorDialogModel(UtilityManagerIfc utility, ResponseCode responseCode)
    {
        String errorMessage = null;
        if (ResponseCode.NotFound == responseCode)
        {
            errorMessage = utility.retrieveText(POSUIManagerIfc.DIALOG_SPEC, BundleConstantsIfc.DIALOG_BUNDLE_NAME,
                    ERROR_UNKNOWN_CARD_TAG, ERROR_UNKNOWN_CARD);
        }
        else if (ResponseCode.Invalid == responseCode)
        {
            errorMessage = utility.retrieveText(POSUIManagerIfc.DIALOG_SPEC, BundleConstantsIfc.DIALOG_BUNDLE_NAME,
                    ERROR_INVALID_REQUEST_TAG, ERROR_INVALID_REQUEST);
        }
        else if (ResponseCode.Declined == responseCode)
        {
            errorMessage = utility.retrieveText(POSUIManagerIfc.DIALOG_SPEC, BundleConstantsIfc.DIALOG_BUNDLE_NAME,
                    ERROR_DECLINED_TAG, ERROR_DECLINED);
        }
        else if (ResponseCode.Unknown == responseCode)
        {
            errorMessage = utility.retrieveText(POSUIManagerIfc.DIALOG_SPEC, BundleConstantsIfc.DIALOG_BUNDLE_NAME,
                    ERROR_UNKNOWN_TAG, ERROR_UNKNOWN);
        }
        else
        {
            errorMessage = utility.retrieveText(POSUIManagerIfc.DIALOG_SPEC, BundleConstantsIfc.DIALOG_BUNDLE_NAME,
                    ERROR_UNKNOWN_TAG, ERROR_UNKNOWN);
        }

        String args[] = new String[1];
        args[0] = errorMessage;
        DialogBeanModel dialogModel = new DialogBeanModel();
        dialogModel.setResourceID(GIFT_CARD_ISSUE_ERROR_DIALOG_ID);
        dialogModel.setType(DialogScreensIfc.ACKNOWLEDGEMENT);
        dialogModel.setButtonLetter(DialogScreensIfc.BUTTON_OK, ACTIVATION_RETRY_LETTER);
        dialogModel.setArgs(args);

        return dialogModel;
    }

    /**
     * If reload request activation fail due to decline or invalid request, show
     * the dialog based on the response code.
     * 
     * @param utility utility manager
     * @param resposeCode response code from gift card activation center
     * @return error dialog model
     */
    public static DialogBeanModel createReloadErrorDialogModel(UtilityManagerIfc utility, ResponseCode responseCode)
    {
        String errorMessage = null;
        if (ResponseCode.Invalid == responseCode)
        {
            errorMessage = utility.retrieveText(POSUIManagerIfc.DIALOG_SPEC, BundleConstantsIfc.DIALOG_BUNDLE_NAME,
                    ERROR_INVALID_REQUEST_TAG, ERROR_INVALID_REQUEST);
        }
        else if (ResponseCode.Declined == responseCode)
        {
            errorMessage = utility.retrieveText(POSUIManagerIfc.DIALOG_SPEC, BundleConstantsIfc.DIALOG_BUNDLE_NAME,
                    ERROR_DECLINED_TAG, ERROR_DECLINED);
        }
        else if (ResponseCode.NotFound == responseCode)
        {
            errorMessage = utility.retrieveText(POSUIManagerIfc.DIALOG_SPEC, BundleConstantsIfc.DIALOG_BUNDLE_NAME,
                    ERROR_UNKNOWN_CARD_TAG, ERROR_UNKNOWN_CARD);
        }

        String args[] = new String[1];
        args[0] = errorMessage;
        DialogBeanModel dialogModel = new DialogBeanModel();
        dialogModel.setResourceID(RELOAD_GIFT_CARD_ERROR_DIALOG_ID);
        dialogModel.setType(DialogScreensIfc.ACKNOWLEDGEMENT);
        dialogModel.setButtonLetter(DialogScreensIfc.BUTTON_OK, RELOAD_DECLINED_LETTER);
        dialogModel.setArgs(args);

        return dialogModel;
    }

    /**
     * If reload request activation fail due to gift card number error, show the
     * dialog based on the response code.
     * 
     * @return error dialog model
     */
    public static DialogBeanModel createReloadNumbErrorDialogModel()
    {

        DialogBeanModel dialogModel = new DialogBeanModel();
        dialogModel.setResourceID(RELOAD_GIFT_CARD_NUMB_ERROR_DIALOG_ID);
        dialogModel.setType(DialogScreensIfc.CONFIRMATION);
        dialogModel.setButtonLetter(DialogScreensIfc.BUTTON_YES, ACTIVATION_RETRY_LETTER);
        dialogModel.setButtonLetter(DialogScreensIfc.BUTTON_NO, RELOAD_DECLINED_LETTER);

        return dialogModel;
    }

    /**
     * create data base connection error dialog model.
     * 
     * @param input error message
     * @return error dialog model
     */
    public static DialogBeanModel createGenericDataBaseErrorDialogModel(String input)
    {
        String[] args = new String[1];

        args[0] = input;

        DialogBeanModel dialogModel = new DialogBeanModel();
        dialogModel.setResourceID(DATABASE_ERROR_DIALOG_ID);
        dialogModel.setType(DialogScreensIfc.ERROR);
        dialogModel.setButtonLetter(DialogScreensIfc.BUTTON_OK, INVALID_CARD_NUM_LETTER);
        dialogModel.setArgs(args);

        return dialogModel;
    }

}
