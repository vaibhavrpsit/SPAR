/* ===========================================================================
* Copyright (c) 2011, 2014, Oracle and/or its affiliates. All rights reserved.
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/tender/activation/ActivationSite.java /main/5 2014/02/18 11:27:05 swbhaska Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    swbhaska  02/17/14 - Removed entry method from EJournal for gift Card
 *                         activation
 *    jswan     01/31/14 - Prevent the Activation scree from showing if the
 *                         issue change.refund scree is the current screen.
 *    icole     02/28/13 - Forward Port Print trace number on receipt for gift
 *                         cards, required by ACI.
 *    cgreene   03/09/12 - add support for journalling queues by current
 *                         register
 *    cgreene   10/21/11 - added missing debit type and auth amount to journal
 *    mkutiana  10/05/11 - Typo in localization property bundle used
 *    jswan     10/04/11 - Fixed offline issues with SAFTOR.
 *    asinton   09/23/11 - When approval code or current balance, which can
 *                         happen if issue/reload occurs when pincomm is
 *                         offline, put 'Not Available' in the EJournal.
 *    cgreene   09/02/11 - check for null when journaling response balance
 *    asinton   09/01/11 - fixed a logic error on printing the gift card
 *                         balance to the ejournal
 *    asinton   08/18/11 - added journaling of activation, deactivation,
 *                         inquiring to tender.activation service.
 *    asinton   08/03/11 - prevent activation error dialogs for gift card
 *                         inquiry
 *    blarsen   06/30/11 - Setting ui's financial network status flag based on
 *                         payment manager response. This will update the
 *                         online/offline indicator on POS UI.
 *    cgreene   05/27/11 - move auth response objects into domain
 *    cgreene   05/20/11 - implemented enums for reponse code and giftcard
 *                         status code
 *    sgu       05/05/11 - change commext rounting on client side
 *    asinton   05/04/11 - New activation service for APF.
 *    asinton   05/03/11 - New activation service for APF.
 *    asinton   05/03/11 - New activation service for APF.
 *    asinton   03/25/11 - Moved APF request and response objects to common
 *                         module.
 *    asinton   03/21/11 - new tender authorization service
 *
 * ===========================================================================
 */
package oracle.retail.stores.pos.services.tender.activation;

import java.util.Locale;

import oracle.retail.stores.common.utility.LocaleMap;
import oracle.retail.stores.common.utility.Util;
import oracle.retail.stores.domain.manager.ifc.PaymentManagerIfc;
import oracle.retail.stores.domain.manager.payment.AuthorizationConstantsIfc;
import oracle.retail.stores.domain.manager.payment.AuthorizeTransferRequestIfc;
import oracle.retail.stores.domain.manager.payment.AuthorizeTransferRequestIfc.RequestSubType;
import oracle.retail.stores.domain.manager.payment.AuthorizeTransferResponseIfc;
import oracle.retail.stores.domain.manager.payment.PaymentServiceResponseIfc.ResponseCode;
import oracle.retail.stores.domain.utility.EYSDate;
import oracle.retail.stores.domain.utility.LocaleConstantsIfc;
import oracle.retail.stores.foundation.manager.device.DeviceException;
import oracle.retail.stores.foundation.manager.device.EncipheredCardData;
import oracle.retail.stores.foundation.manager.gui.UIException;
import oracle.retail.stores.foundation.manager.ifc.JournalManagerIfc;
import oracle.retail.stores.foundation.manager.ifc.KeyStoreEncryptionManagerIfc;
import oracle.retail.stores.foundation.manager.ifc.UIManagerIfc;
import oracle.retail.stores.foundation.manager.ifc.journal.JournalableIfc;
import oracle.retail.stores.foundation.tour.application.Letter;
import oracle.retail.stores.foundation.tour.gate.Gateway;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.foundation.tour.service.SessionBusIfc;
import oracle.retail.stores.keystoreencryption.EncryptionServiceException;
import oracle.retail.stores.pos.config.bundles.BundleConstantsIfc;
import oracle.retail.stores.pos.device.POSDeviceActions;
import oracle.retail.stores.pos.manager.ifc.UtilityManagerIfc;
import oracle.retail.stores.pos.services.PosSiteActionAdapter;
import oracle.retail.stores.pos.services.common.CommonLetterIfc;
import oracle.retail.stores.pos.services.giftcard.GiftCardConstantsIfc;
import oracle.retail.stores.pos.ui.POSUIManagerIfc;
import oracle.retail.stores.pos.ui.UIUtilities;
import oracle.retail.stores.pos.ui.beans.POSBaseBeanModel;
import oracle.retail.stores.pos.ui.beans.PromptAndResponseModel;
import oracle.retail.stores.utility.I18NConstantsIfc;
import oracle.retail.stores.utility.I18NHelper;
import oracle.retail.stores.utility.JournalConstantsIfc;

import org.apache.commons.codec.binary.Base64;
import org.apache.log4j.Logger;

/**
 * This site calls the Payment Manager for authorization.
 *
 * @author asinton
 * @since 13.4
 */
@SuppressWarnings("serial")
public class ActivationSite extends PosSiteActionAdapter
{
    /**
     * The logger to which log messages will be sent
     */
    protected static final Logger logger = Logger.getLogger(ActivationSite.class);

    /** Constant for Approval letter */
    public static final String APPROVED_LETTER = "Approved";

    /** Constant for Declined letter */
    public static final String DECLINED_LETTER = "Declined";

    /** Constant for Offline letter */
    public static final String OFFLINE_LETTER = "Offline";
    
    /* (non-Javadoc)
     * @see oracle.retail.stores.foundation.tour.application.SiteActionAdapter#arrive(oracle.retail.stores.foundation.tour.ifc.BusIfc)
     */
    @Override
    public void arrive(BusIfc bus)
    {
        ActivationCargo cargo = (ActivationCargo)bus.getCargo();
        PaymentManagerIfc paymentManager = (PaymentManagerIfc)bus.getManager(PaymentManagerIfc.TYPE);
        AuthorizeTransferRequestIfc request = cargo.getCurrentRequest();
        AuthorizeTransferResponseIfc response = null;
        String letter = CommonLetterIfc.ERROR;

        // Check to see if the activation screen should be displayed.
        if (isShowActivationScreen(bus))
        {
            displayActivateScreen(bus, request);
        }
        
        try
        {
            response = (AuthorizeTransferResponseIfc)paymentManager.authorize(request);

            UIUtilities.setFinancialNetworkUIStatus(response, (POSUIManagerIfc) bus.getManager(UIManagerIfc.TYPE));
                        
        }
        catch(Exception e)
        {
            logger.error("Exception caught while calling the PaymentManagerIfc.authorize()", e);
        }
        if(response != null)
        {
            if (AuthorizationConstantsIfc.ONLINE != response.getFinancialNetworkStatus())
            {
                response.setResponseCode(ResponseCode.Offline);
            }
            
            cargo.addResponse(response);
            ResponseCode responseCode = response.getResponseCode();
            if(ResponseCode.Approved.equals(responseCode))
            {
                letter = APPROVED_LETTER;
            }
            // If the request was for in inquiry then don't present the activation
            // error dialogs, instead show the generic error dialog.
            if(!RequestSubType.Inquiry.equals(request.getRequestSubType()))
            {
                if(ResponseCode.Declined.equals(responseCode))
                {
                    letter = DECLINED_LETTER;
                }
                else
                if (ResponseCode.AlreadyActive.equals(responseCode) ||
                    ResponseCode.Duplicate.equals(responseCode) ||
                    ResponseCode.NotFound.equals(responseCode))
                {
                    RequestSubType requestSubType = request.getRequestSubType();
                    if (RequestSubType.Activate.equals(requestSubType) ||
                            RequestSubType.Redeem.equals(requestSubType) ||
                            RequestSubType.ReloadGiftCard.equals(requestSubType))
                    {
                        letter = DECLINED_LETTER;
                    }
                }
                else if(ResponseCode.Offline.equals(responseCode) ||
                        ResponseCode.Timeout.equals(responseCode) ||
                        ResponseCode.Referral.equals(responseCode))
                {
                    letter = OFFLINE_LETTER;
                }
            }
            journalResponse(bus, cargo, response, request);
        }
        bus.mail(new Letter(letter), BusIfc.CURRENT);
    }

    /**
     * Displays the appropriate activation screen.
     * @param bus
     * @param request
     */
    protected void displayActivateScreen(BusIfc bus, AuthorizeTransferRequestIfc request)
    {
        String promptText = getPromptText(bus, request);
        if(promptText != null)
        {
            POSUIManagerIfc ui = (POSUIManagerIfc)bus.getManager(UIManagerIfc.TYPE);
            PromptAndResponseModel parModel = new PromptAndResponseModel();

            parModel.setPromptText(promptText);
    
            POSBaseBeanModel baseModel = new POSBaseBeanModel();
            baseModel.setPromptAndResponseModel(parModel);
            ui.showScreen(POSUIManagerIfc.AUTHORIZATION, baseModel);
        }
    }

    /**
     * Generates the truncated gift card number
     * @param request
     * @return the truncated gift card number
     */
    protected String getTruncatedGiftCardNumber(AuthorizeTransferRequestIfc request)
    {
        KeyStoreEncryptionManagerIfc encryptionManager = (KeyStoreEncryptionManagerIfc)Gateway.getDispatcher().getManager(KeyStoreEncryptionManagerIfc.TYPE);
        String truncatedCardNumber = "";
        try
        {
            byte[] accountNumber = encryptionManager.decrypt(Base64.decodeBase64(request.getAccountNumber().getBytes()));
            EncipheredCardData cardData = new EncipheredCardData(accountNumber);
            truncatedCardNumber = cardData.getTruncatedAcctNumber();
        }
        catch(EncryptionServiceException ese)
        {
            logger.error("Unable to decrypt card number", ese);
        }
        return truncatedCardNumber;
    }

    /**
     * Returns the last four of the gift card
     * @param request
     * @return
     */
    protected String getLastFour(AuthorizeTransferRequestIfc request)
    {
        String truncatedGiftCardNumber = getTruncatedGiftCardNumber(request);
        String lastFour = "";
        if(truncatedGiftCardNumber.length() > 4)
        {
            lastFour = truncatedGiftCardNumber.substring(truncatedGiftCardNumber.length() - 4);
        }
        return lastFour;
    }
    /**
     * Returns the appropriate action for the type of activation (eg. Activating,
     * Deactivating, Reloading, or none).
     * @param bus
     * @param request
     * @return
     */
    protected String getPromptText(BusIfc bus, AuthorizeTransferRequestIfc request)
    {
        String key = null;
        String defaultText = null;
        switch(request.getRequestSubType())
        {
            case ReloadGiftCard :
                key = GiftCardConstantsIfc.RELOADING_SCREEN_NAME_TAG;
                defaultText = GiftCardConstantsIfc.RELOADING_SCREEN_NAME;
                break;
            case Activate :
            case RedeemVoid :
                key = GiftCardConstantsIfc.ACTIVATION_SCREEN_NAME_TAG;
                defaultText = GiftCardConstantsIfc.ACTIVATION_SCREEN_NAME;
                break;
            case Deactivate :
            case Redeem :
            case ReloadVoid :
            case VoidGiftCard :
                key = GiftCardConstantsIfc.DEACTIVATION_SCREEN_NAME_TAG;
                defaultText = GiftCardConstantsIfc.DEACTIVATION_SCREEN_NAME;
                break;
            default :
                // do nothing
                break;
        }
        String promptText = null;
        if(key != null && defaultText != null)
        {
            UtilityManagerIfc utility = (UtilityManagerIfc)bus.getManager(UtilityManagerIfc.TYPE);
            String action = utility.retrieveText(
                    POSUIManagerIfc.PROMPT_AND_RESPONSE_SPEC,
                    BundleConstantsIfc.GIFTCARD_BUNDLE_NAME,
                    key,
                    defaultText);
            String lastFour = getLastFour(request);
            Object[] dataArgs = {action, lastFour};
            String prompt = utility.retrieveText(
                    POSUIManagerIfc.PROMPT_AND_RESPONSE_SPEC,
                    BundleConstantsIfc.GIFTCARD_BUNDLE_NAME,
                    GiftCardConstantsIfc.CONNECTING_PROMPT_TAG,
                    GiftCardConstantsIfc.CONNECTING_PROMPT);
            promptText = I18NHelper.getString(I18NConstantsIfc.COMMON_TYPE, prompt, dataArgs);
    
        }
        return promptText;
    }

    /**
     * Journal the activation or deactivation results.
     * 
     * @param cargo
     * @param response
     * @param request 
     * @param activationSucceeded
     */
    protected void journalResponse(BusIfc bus, ActivationCargo cargo, AuthorizeTransferResponseIfc response, AuthorizeTransferRequestIfc request)
    {
        JournalManagerIfc jmi = (JournalManagerIfc) bus.getManager(JournalManagerIfc.TYPE);
        RequestSubType requestSubType = request.getRequestSubType();
        // Get current gift card

        if ((jmi != null) && (response != null))
        {
            StringBuilder message = new StringBuilder(Util.EOL);
            message.append(getFormattedRequestMessage(requestSubType));
            message.append(": ");
            message.append(getResponseMessage(response.getResponseCode()));

            if (RequestSubType.Activate.equals(requestSubType) ||
                    RequestSubType.Deactivate.equals(requestSubType) ||
                    RequestSubType.Redeem.equals(requestSubType) ||
                    RequestSubType.RedeemVoid.equals(requestSubType) ||
                    RequestSubType.ReloadGiftCard.equals(requestSubType) ||
                    RequestSubType.ReloadVoid.equals(requestSubType))
            {
                jmi.setEntryType(JournalableIfc.ENTRY_TYPE_TRANS);
                // journal the card number
                message.append(Util.EOL);
                Object dataArgs[] = new Object[1];
                dataArgs[0] = getTruncatedGiftCardNumber(request);
                message.append(I18NHelper.getString(I18NConstantsIfc.EJOURNAL_TYPE, JournalConstantsIfc.GIFT_CARD_ID_LABEL, dataArgs));
                // journal the Approval Code
                message.append(Util.EOL);
                if(!Util.isEmpty(response.getAuthorizationCode()))
                {
                    dataArgs[0] = response.getAuthorizationCode();
                }
                else
                {
                    dataArgs[0] = I18NHelper.getString(I18NConstantsIfc.EJOURNAL_TYPE, JournalConstantsIfc.NOT_AVAILABLE_LABEL, null);
                }
                message.append(I18NHelper.getString(I18NConstantsIfc.EJOURNAL_TYPE, JournalConstantsIfc.APPROVAL_CODE_LABEL, dataArgs));
                // journal the authorization method
                message.append(Util.EOL);
                dataArgs[0] = response.getTraceNumber();
                message.append(I18NHelper.getString(I18NConstantsIfc.EJOURNAL_TYPE, JournalConstantsIfc.TRACE_NUMBER_LABEL,
                        dataArgs));
                message.append(Util.EOL);
                dataArgs[0] = I18NHelper.getString(I18NConstantsIfc.EJOURNAL_TYPE,JournalConstantsIfc.AUTOMATIC_LABEL, null);
                message.append(I18NHelper.getString(I18NConstantsIfc.EJOURNAL_TYPE, JournalConstantsIfc.AUTHORIZATION_METHOD_LABEL, dataArgs));
                // journal financial network status
                message.append(Util.EOL);
                if(response.getFinancialNetworkStatus() == AuthorizationConstantsIfc.ONLINE)
                {
                    dataArgs[0] = I18NHelper.getString(I18NConstantsIfc.EJOURNAL_TYPE,JournalConstantsIfc.ONLINE_LABEL, null);
                }
                else
                {
                    dataArgs[0] = I18NHelper.getString(I18NConstantsIfc.EJOURNAL_TYPE,JournalConstantsIfc.OFFLINE_LABEL, null);
                }
                message.append(I18NHelper.getString(I18NConstantsIfc.EJOURNAL_TYPE, JournalConstantsIfc.FIN_NET_STATUS_LABEL, dataArgs, null));
                // journal the available balance if not declined
                if (!ResponseCode.Declined.equals(response.getResponseCode()))
                {
                    message.append(Util.EOL);
                    if(response.getCurrentBalance() != null)
                    {
                        dataArgs[0] = response.getCurrentBalance().toGroupFormattedString();
                    }
                    else
                    {
                        dataArgs[0] = I18NHelper.getString(I18NConstantsIfc.EJOURNAL_TYPE, JournalConstantsIfc.NOT_AVAILABLE_LABEL, null);
                    }
                    message.append(I18NHelper.getString(I18NConstantsIfc.EJOURNAL_TYPE,JournalConstantsIfc.GC_AVAILABLE_BALANCE_LABEL, dataArgs));
                }
                // journal the date sold
                message.append(Util.EOL);
                Locale locale = LocaleMap.getLocale(LocaleConstantsIfc.JOURNAL);
                dataArgs[0] = response.getResponseTime().toFormattedString(locale);
                message.append(I18NHelper.getString(I18NConstantsIfc.EJOURNAL_TYPE, JournalConstantsIfc.DATE_SOLD_LABEL, dataArgs));
                // journal the date activated, reloaded, etc.
                String eventDate = getEventDate(requestSubType);
                String dateSold = getDateSold(requestSubType, response);
                if(eventDate != null && dateSold != null)
                {
                    message.append(Util.EOL);
                    dataArgs = new Object[]{eventDate, dateSold};
                    message.append(I18NHelper.getString(I18NConstantsIfc.EJOURNAL_TYPE, JournalConstantsIfc.GIFTCARD_EVENT_LABEL, dataArgs));
                }
                jmi.journal(cargo.getTransaction().getCashier().getLoginID(), cargo.getTransaction().getTransactionID(), message.toString());
            }
            else
            {
                // journal the card number
                message.append(Util.EOL);
                Object[] dataArgs = {getTruncatedGiftCardNumber(request)};
                message.append(I18NHelper.getString(I18NConstantsIfc.EJOURNAL_TYPE, JournalConstantsIfc.GIFT_CARD_ID_LABEL, dataArgs));
                // add the above generated message
                if (cargo.getTransaction() != null || cargo.isTransactionInProgress())
                {
                    jmi.setEntryType(JournalableIfc.ENTRY_TYPE_TRANS);
                }
                else
                {
                    jmi.setEntryType(JournalableIfc.ENTRY_TYPE_NOTTRANS);
                }
                jmi.journal(message.toString());
            }
        }
        else
        {
            if (jmi == null)
            {
                logger.warn("No journal manager found.");
            }
            if (response == null)
            {
                logger.warn("No activation response");
            }
        }
    }

    /**
     * Returns the formatted request message for the given request sub type.
     * @param requestSubType
     * @return the formatted request message for the given request sub type.
     */
    protected String getFormattedRequestMessage(RequestSubType requestSubType)
    {
        String formattedRequestString = null;
        switch(requestSubType)
        {
            case Activate :
                formattedRequestString = I18NHelper.getString(I18NConstantsIfc.EJOURNAL_TYPE, JournalConstantsIfc.GIFT_CARD_ISSUE_LABEL, null);
                break;
            case ReloadGiftCard :
                formattedRequestString = I18NHelper.getString(I18NConstantsIfc.EJOURNAL_TYPE, JournalConstantsIfc.GIFT_CARD_RELOAD_LABEL, null);
                break;
            case Inquiry :
                formattedRequestString = I18NHelper.getString(I18NConstantsIfc.EJOURNAL_TYPE, JournalConstantsIfc.GIFT_CARD_INQUIRY_LABEL, null);
                break;
            case Redeem :
                formattedRequestString = I18NHelper.getString(I18NConstantsIfc.EJOURNAL_TYPE, JournalConstantsIfc.GIFT_CARD_REDEEM_LABEL, null);
                break;
            case RedeemVoid :
                formattedRequestString = I18NHelper.getString(I18NConstantsIfc.EJOURNAL_TYPE, JournalConstantsIfc.GIFT_CARD_REDEEM_VOID_LABEL, null);
                break;
            case Deactivate :
                formattedRequestString = I18NHelper.getString(I18NConstantsIfc.EJOURNAL_TYPE, JournalConstantsIfc.GIFT_CARD_ISSUE_VOID_LABEL, null);
                break;
            case ReloadVoid :
                formattedRequestString = I18NHelper.getString(I18NConstantsIfc.EJOURNAL_TYPE, JournalConstantsIfc.GIFT_CARD_RELOAD_VOID_LABEL, null);
                break;
            default :
                // nothing
                break;
        }
        return formattedRequestString;
    }

    /**
     * Returns  the event date for this journal item.
     * @param requestSubType
     * @return
     */
    protected String getEventDate(RequestSubType requestSubType)
    {
        String eventDate = null;
        switch(requestSubType)
        {
            case Activate :
                eventDate = I18NHelper.getString(I18NConstantsIfc.EJOURNAL_TYPE, JournalConstantsIfc.DATE_ACTIVATED_LABEL, null);
                break;
            case ReloadGiftCard :
                eventDate = I18NHelper.getString(I18NConstantsIfc.EJOURNAL_TYPE, JournalConstantsIfc.DATE_RELOADED_LABEL, null);
                break;
            case Redeem :
                eventDate = I18NHelper.getString(I18NConstantsIfc.EJOURNAL_TYPE, JournalConstantsIfc.DATE_DEACTIVATED_LABEL, null);
                break;
            case Deactivate :
                eventDate = I18NHelper.getString(I18NConstantsIfc.EJOURNAL_TYPE, JournalConstantsIfc.DATE_DEACTIVATED_LABEL, null);
                break;
            default :
                // do nothing
                break;
        }
        return eventDate;
    }

    /**
     * Returns the date sold for this journal item.
     * @param requestSubType
     * @param response
     * @return
     */
    protected String getDateSold(RequestSubType requestSubType, AuthorizeTransferResponseIfc response)
    {
        String dateSold = null;
        Locale locale = LocaleMap.getLocale(LocaleConstantsIfc.JOURNAL);
        switch(requestSubType)
        {
            case Activate :
                dateSold = response.getResponseTime().toFormattedString(locale);
                break;
            case ReloadGiftCard :
                dateSold = new EYSDate().toFormattedString(locale);
                break;
            case Redeem :
                dateSold = new EYSDate().toFormattedString(locale);
                break;
            case Deactivate :
                dateSold = new EYSDate().toFormattedString(locale);
                break;
            default :
                // do nothing
                break;
        }
        return dateSold;
    }

    /**
     * Returns the response message based on the given response code.
     * @param responseCode
     * @return the response message.
     */
    protected String getResponseMessage(ResponseCode responseCode)
    {
        String message;
        switch(responseCode)
        {
            case Approved :
                message = I18NHelper.getString(I18NConstantsIfc.EJOURNAL_TYPE, JournalConstantsIfc.SUCCESSFUL_LABEL, null);
                break;
            case Duplicate :
                message = I18NHelper.getString(I18NConstantsIfc.EJOURNAL_TYPE, JournalConstantsIfc.DUPLICATE_CARD_LABEL, null);
                break;
            case CardNumError :
                message = I18NHelper.getString(I18NConstantsIfc.EJOURNAL_TYPE, JournalConstantsIfc.CARD_NOT_FOUND_LABEL, null);
                break;
            case Declined :
                message = I18NHelper.getString(I18NConstantsIfc.EJOURNAL_TYPE, JournalConstantsIfc.DECLINED_LABEL, null);
                break;
            case Offline :
                message = I18NHelper.getString(I18NConstantsIfc.EJOURNAL_TYPE, JournalConstantsIfc.OFFLINE_LABEL, null);
                break;
            case Timeout :
                message = I18NHelper.getString(I18NConstantsIfc.EJOURNAL_TYPE, JournalConstantsIfc.TIMEOUT_LABEL, null);
                break;
            case Invalid :
                message = I18NHelper.getString(I18NConstantsIfc.EJOURNAL_TYPE, JournalConstantsIfc.INVALID_REQUEST_LABEL, null);
                break;
            case NotFound :
                message = I18NHelper.getString(I18NConstantsIfc.EJOURNAL_TYPE, JournalConstantsIfc.CARD_NOT_FOUND_LABEL, null);
                break;
            default :
                message = I18NHelper.getString(I18NConstantsIfc.EJOURNAL_TYPE, JournalConstantsIfc.UNKNOWN_ERROR_LABEL, null);
                break;
        }
        return message;
    }

    /**
     * Get the cash drawer status and the ID of the currently
     * displayed screen.  If the drawer is open and the 
     * @param bus
     * @return
     */
    protected boolean isShowActivationScreen(BusIfc bus)
    {
        boolean showScreen = true;
        POSUIManagerIfc ui = (POSUIManagerIfc)bus.getManager(UIManagerIfc.TYPE);
        POSDeviceActions drawer = new POSDeviceActions((SessionBusIfc)bus);
        String currentScreen = null;
        try
        {
            // if cash drawer open then prompt to close and wait
            if (Boolean.TRUE.equals(drawer.isOpen()))
            {
                try
                {
                    currentScreen = ui.getActiveScreenID();

                    // If the screen asking the operator to give the customer money,
                    // don't display the activation screen.
                    if (currentScreen.equals(POSUIManagerIfc.ISSUE_CHANGE)
                            || currentScreen.equals(POSUIManagerIfc.ISSUE_REFUND))
                    {
                        showScreen = false;
                    }
                }
                catch (UIException uie)
                {
                    logger.warn("Unable to get the current screen ID.");
                }
            }
        }
        catch (DeviceException e)
        {
            logger.warn("Unable to get cash drawer status.", e);
        }
        
        return showScreen;
    }
}
