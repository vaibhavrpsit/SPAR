/* ===========================================================================
* Copyright (c) 2011, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/tender/ValidateGiftCardEntrySite.java /rgbustores_13.4x_generic_branch/1 2011/08/29 14:52:05 asinton Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    asinton   08/29/11 - Adding validation of the gift card number
 *
 * ===========================================================================
 */

package oracle.retail.stores.pos.services.tender;

import java.util.Map;

import oracle.retail.stores.domain.utility.CardTypeIfc;
import oracle.retail.stores.foundation.manager.device.EncipheredCardDataIfc;
import oracle.retail.stores.foundation.manager.ifc.UIManagerIfc;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.pos.ado.tender.TenderConstants;
import oracle.retail.stores.pos.ado.tender.TenderTypeEnum;
import oracle.retail.stores.pos.manager.ifc.UtilityManagerIfc;
import oracle.retail.stores.pos.services.PosSiteActionAdapter;
import oracle.retail.stores.pos.services.common.CommonLetterIfc;
import oracle.retail.stores.pos.ui.DialogScreensIfc;
import oracle.retail.stores.pos.ui.POSUIManagerIfc;
import oracle.retail.stores.pos.ui.beans.DialogBeanModel;

/**
 * This site validates the entered gift card.  If entered gift card is valid
 * then the tour should go on to authorize, else show an error dialog and
 * go back to tender options.
 * @author asinton
 * @since 13.4
 */
@SuppressWarnings("serial")
public class ValidateGiftCardEntrySite extends PosSiteActionAdapter
{
    /** Constant for the GiftCardTenderInvalid dialog */
    public static final String GIFT_CARD_INVALID_DIALOG = "GiftCardTenderInvalid";

    /* (non-Javadoc)
     * @see oracle.retail.stores.foundation.tour.application.SiteActionAdapter#arrive(oracle.retail.stores.foundation.tour.ifc.BusIfc)
     */
    @Override
    public void arrive(BusIfc bus)
    {
        TenderCargo tenderCargo = (TenderCargo)bus.getCargo();
        Map<String, Object> tenderAttributes = tenderCargo.getTenderAttributes();
        EncipheredCardDataIfc cardData = (EncipheredCardDataIfc)tenderAttributes.get(TenderConstants.ENCIPHERED_CARD_DATA);
        if(cardData == null || !isGiftCardValid(cardData, bus))
        {
            // remove the gift card data from tender attributes
            tenderAttributes.remove(TenderConstants.ENCIPHERED_CARD_DATA);
            // and show the error dialog
            DialogBeanModel dialogModel = new DialogBeanModel();
            dialogModel.setResourceID(GIFT_CARD_INVALID_DIALOG);
            dialogModel.setType(DialogScreensIfc.ERROR);
            dialogModel.setButtonLetter(DialogScreensIfc.BUTTON_OK, CommonLetterIfc.NO);
            // display dialog
            POSUIManagerIfc ui = (POSUIManagerIfc) bus.getManager(UIManagerIfc.TYPE);
            ui.showScreen(POSUIManagerIfc.DIALOG_TEMPLATE, dialogModel);
        }
        else
        {
            bus.mail(CommonLetterIfc.YES, BusIfc.CURRENT);
        }
    }

    /**
     * Tests to see if given card data represents a valid gift card number
     * by using the card type rules.
     * @param cardData
     * @return true if cardData represents a valid gift card, false otherwise.
     */
    protected boolean isGiftCardValid(EncipheredCardDataIfc cardData, BusIfc bus)
    {
        boolean isValid = false;
        UtilityManagerIfc utility = (UtilityManagerIfc)bus.getManager(UtilityManagerIfc.TYPE);
        CardTypeIfc cardType = utility.getConfiguredCardTypeInstance();
        if (cardType != null)
        {
            String cardTypeId = cardType.identifyCardType(cardData, TenderTypeEnum.GIFT_CARD.toString());
            if (!cardTypeId.equals(CardTypeIfc.UNKNOWN))
            {
                isValid = true;
            }
        }
        return isValid;
    }

}
