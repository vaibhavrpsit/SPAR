/* ===========================================================================
* Copyright (c) 2011, 2013, Oracle and/or its affiliates. All rights reserved.
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/tender/activation/GiftCardInfoEnteredAisle.java /main/2 2013/05/16 12:20:13 asinton Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    jswan     10/29/14 - Modified to valid gift card number for bin range
 *                         and check digit.
 *    asinton   05/16/13 - updated entry method on the gift card when the
 *                         operator must enter new card for activation or
 *                         reload
 *    asinton   11/14/11 - Prevent NullPointerException when
 *                         cargo.getCurrentLineNumber() returns null.
 *    jswan     10/21/11 - Modified to support changing the gift card number
 *                         during activation. No longer depends and matching
 *                         the masked account number, which maybe masked with
 *                         different characters or number of digits.
 *    cgreene   09/30/11 - also replace card data for other events besides just
 *                         declines
 *    cgreene   09/21/11 - added ability to set new card number onto the card
 *                         that was declined and then reswiped.
 *    asinton   08/02/11 - fixed case where new response for failed request was
 *                         not updated at the matching index in
 *                         ActivationCargo.
 *
 * ===========================================================================
 */
package oracle.retail.stores.pos.services.tender.activation;

import oracle.retail.stores.common.utility.Util;
import oracle.retail.stores.domain.lineitem.AbstractTransactionLineItemIfc;
import oracle.retail.stores.domain.lineitem.SaleReturnLineItemIfc;
import oracle.retail.stores.domain.manager.payment.AuthorizeTransferRequestIfc;
import oracle.retail.stores.domain.stock.GiftCardPLUItemIfc;
import oracle.retail.stores.domain.transaction.SaleReturnTransactionIfc;
import oracle.retail.stores.domain.transaction.TenderableTransactionIfc;
import oracle.retail.stores.domain.utility.EntryMethod;
import oracle.retail.stores.foundation.factory.FoundationObjectFactory;
import oracle.retail.stores.foundation.manager.device.EncipheredCardDataIfc;
import oracle.retail.stores.foundation.manager.ifc.JournalManagerIfc;
import oracle.retail.stores.foundation.manager.ifc.ParameterManagerIfc;
import oracle.retail.stores.foundation.manager.ifc.UIManagerIfc;
import oracle.retail.stores.foundation.tour.application.Letter;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.keystoreencryption.EncryptionServiceException;
import oracle.retail.stores.pos.manager.ifc.UtilityManagerIfc;
import oracle.retail.stores.pos.services.PosLaneActionAdapter;
import oracle.retail.stores.pos.services.common.CommonLetterIfc;
import oracle.retail.stores.pos.services.giftcard.GiftCardUtilities;
import oracle.retail.stores.pos.ui.POSUIManagerIfc;
import oracle.retail.stores.pos.ui.beans.POSBaseBeanModel;
import oracle.retail.stores.pos.ui.beans.PromptAndResponseModel;
import oracle.retail.stores.utility.I18NConstantsIfc;
import oracle.retail.stores.utility.I18NHelper;
import oracle.retail.stores.utility.JournalConstantsIfc;

/**
 * This aisle is traversed when the user enters a new gift card number in the
 * "Sell Gift Card" screen. It sets the new gift card swipe info.
 */
@SuppressWarnings("serial")
public class GiftCardInfoEnteredAisle extends PosLaneActionAdapter
{
    /**
     * gift card tag
     */
    protected static String GIFT_CARD_TAG = "GiftCard";

    /**
     * gift card message
     */
    protected static String GIFT_CARD = "Gift Card";

    /* (non-Javadoc)
     * @see oracle.retail.stores.foundation.tour.application.LaneActionAdapter#traverse(oracle.retail.stores.foundation.tour.ifc.BusIfc)
     */
    @Override
    public void traverse(BusIfc bus)
    {
        // read the data from the UI
        POSUIManagerIfc ui = (POSUIManagerIfc)bus.getManager(UIManagerIfc.TYPE);
        UtilityManagerIfc utility = (UtilityManagerIfc)bus.getManager(UtilityManagerIfc.TYPE);
        ParameterManagerIfc pm = (ParameterManagerIfc)bus.getManager(ParameterManagerIfc.TYPE);
        POSBaseBeanModel model = (POSBaseBeanModel)ui.getModel(POSUIManagerIfc.SELL_GIFT_CARD);
        PromptAndResponseModel parModel = model.getPromptAndResponseModel();
        ActivationCargo cargo = (ActivationCargo)bus.getCargo();
        AuthorizeTransferRequestIfc request = cargo.getCurrentRequest();

        if (logger.isInfoEnabled())
        {
            logger.info("GiftCardInfoEnteredAisle.traverse(), request = " + request);
        }

        EncipheredCardDataIfc cardData = null;
        EntryMethod entryMethod = EntryMethod.Manual;
        if (parModel.isSwiped())
        {
            cardData = parModel.getMSRModel().getEncipheredCardData();
            entryMethod = EntryMethod.Swipe;
        }
        else
        {
            try
            {
                cardData = FoundationObjectFactory.getFactory().createEncipheredCardDataInstance(parModel.getResponseBytes());
            }
            catch (EncryptionServiceException ese)
            {
                logger.error("Couldn't encrypt gift card number", ese);
            }
        }

        boolean noError = true;
        if (cardData != null)
        {
            // Used to determine if
            String encryptedCardNumber = cardData.getEncryptedAcctNumber();

            if (GiftCardUtilities.isEmpty(model, cardData.getMaskedAcctNumber(), logger, bus.getServiceName()))
            {
                noError = false;
                if (parModel.isSwiped())
                {
                    ui.showScreen(POSUIManagerIfc.DIALOG_TEMPLATE, GiftCardUtilities.createBadMSRReadDialogModel(utility));
                }
                else
                {
                    ui.showScreen(POSUIManagerIfc.DIALOG_TEMPLATE,
                            GiftCardUtilities.createInvalidGiftCardNumErrorDialogModel());
                }
            }
            else
            if (encryptedCardNumber == null ||
               (!GiftCardUtilities.isValidBinRange(pm, utility, cardData, logger, bus.getServiceName())) ||
               (!GiftCardUtilities.isValidCheckDigit(utility, cardData, logger, bus.getServiceName())))
            {
                noError = false;
                ui.showScreen(POSUIManagerIfc.DIALOG_TEMPLATE, GiftCardUtilities.createInvalidGiftCardNumErrorDialogModel());
            }
        }
        else // cardData is null.
        {
            noError = false;
            ui.showScreen(POSUIManagerIfc.DIALOG_TEMPLATE, GiftCardUtilities.createInvalidGiftCardNumErrorDialogModel());
        }

        // Proceed to the next step
        if (noError)
        {
            // journal setting of gift card number before proceeding
            journalSetting(bus, cardData);

            // replace current giftcard card details
            updateGiftCardNumber(cargo, cardData, entryMethod);

            request.setAccountNumber(cardData.getEncryptedAcctNumber());
            request.setEntryMethod(entryMethod);

            // remove the previously entered response as it will be replaced with a new attempt
            cargo.removeCurrentResponse();

            bus.mail(new Letter(CommonLetterIfc.CONTINUE), BusIfc.CURRENT);
        }
    }

    /**
     * Journal the setting of the new gift card number.
     *
     * @param bus
     * @param cardData
     */
    protected void journalSetting(BusIfc bus, EncipheredCardDataIfc cardData)
    {
        // get the Journal manager
        JournalManagerIfc jmi = (JournalManagerIfc)bus.getManager(JournalManagerIfc.TYPE);
        StringBuilder entry = new StringBuilder();
        Object[] dataArgs = new Object[2];
        if (jmi != null)
        {
            entry.append(Util.EOL);
            entry.append(I18NHelper.getString(I18NConstantsIfc.EJOURNAL_TYPE,
                    JournalConstantsIfc.GIFT_CARD_ID_CHANGE_LABEL, null));
            entry.append(Util.EOL);
            dataArgs[0] = cardData.getLastFourAcctNumber();
            entry.append(I18NHelper.getString(I18NConstantsIfc.EJOURNAL_TYPE,
                    JournalConstantsIfc.NEW_GIFT_CARD_ID_LABEL, dataArgs));
        }
    }

    /**
     * Update the current gift card item with new card data that was just
     * entered and given to this aisle. The current line to update is determined
     * by the current line item sequence number from the cargo.
     * 
     * @param cargo instance of the <code>ActivationCargo</code
     * @param newCardData instance of the <code>EncipheredCardDataIfc</code> that contains the new encrypted card number
     * @deprecated As of 14.0 this method is deprecated, please use {@link GiftCardInfoEnteredAisle#updateGiftCardNumber(ActivationCargo, EncipheredCardDataIfc, EntryMethod)} instead.
     */
    protected void updateGiftCardNumber(ActivationCargo cargo, EncipheredCardDataIfc newCardData)
    {
        updateGiftCardNumber(cargo, newCardData, EntryMethod.Manual);
    }

    /**
     * Update the current gift card item with new card data that was just
     * entered and given to this aisle. The current line to update is determined
     * by the current line item sequence number from the cargo.
     * 
     * @param cargo instance of the <code>ActivationCargo</code
     * @param newCardData instance of the <code>EncipheredCardDataIfc</code> that contains the new encrypted card number
     * @param entryMethod the entry method that was used to enter the card number
     */
    protected void updateGiftCardNumber(ActivationCargo cargo, EncipheredCardDataIfc newCardData, EntryMethod entryMethod)
    {
        TenderableTransactionIfc tenderTrans = cargo.getTransaction();
        if (tenderTrans instanceof SaleReturnTransactionIfc)
        {
            SaleReturnTransactionIfc saleTrans = (SaleReturnTransactionIfc)tenderTrans;
            Integer lineNumber = cargo.getCurrentLineNumber();
            if(lineNumber != null)
            {
                AbstractTransactionLineItemIfc lineItem = saleTrans.retrieveLineItemByID(lineNumber);
                if (lineItem instanceof SaleReturnLineItemIfc)
                {
                    SaleReturnLineItemIfc saleItem = (SaleReturnLineItemIfc)lineItem;
                    if (saleItem.getPLUItem() instanceof GiftCardPLUItemIfc)
                    {
                        GiftCardPLUItemIfc giftCardItem = (GiftCardPLUItemIfc)saleItem.getPLUItem();
                        // replace the bad card number that was declined with the new number
                        giftCardItem.getGiftCard().setEncipheredCardData(newCardData);
                        // replace the entry method with the entry method associated with the new card
                        giftCardItem.getGiftCard().setEntryMethod(entryMethod);
                    }
                }
            }
        }
    }
}
