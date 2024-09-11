/* ===========================================================================
* Copyright (c) 1998, 2012, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/giftcard/issue/GetCardNumForGiftCardIssueSite.java /main/13 2012/09/12 11:57:10 blarsen Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    asinton   03/30/12 - implemented giftcard issue
 *    jswan     09/06/11 - Fixed issues with gift card balance when
 *                         issuing/reloading multiple gift cards and one card
 *                         fails.
 *    cgreene   07/26/11 - moved StatusCode to GiftCardifc
 *    cgreene   06/07/11 - update to first pass of removing pospal project
 *    cgreene   05/27/11 - move auth response objects into domain
 *    asinton   04/26/11 - Refactor gift card for APF
 *    blarsen   12/09/10 - cpoi msr not being activated. added call to
 *                         pda.showMSREntryPrompt() which actually does the
 *                         activation.
 *    acadar    09/10/10 - always set the amount in the model
 *    cgreene   05/26/10 - convert to oracle packaging
 *    abondala  01/03/10 - update header date
 *
 * ===========================================================================
 * $Log:
 *    5    360Commerce 1.4         4/25/2007 8:52:27 AM   Anda D. Cadar   I18N
 *         merge
 *
 *    4    360Commerce 1.3         1/22/2006 11:45:06 AM  Ron W. Haight
 *         removed references to com.ibm.math.BigDecimal
 *    3    360Commerce 1.2         3/31/2005 4:28:14 PM   Robert Pearse
 *    2    360Commerce 1.1         3/10/2005 10:21:47 AM  Robert Pearse
 *    1    360Commerce 1.0         2/11/2005 12:11:09 PM  Robert Pearse
 *
 *   Revision 1.3  2004/02/12 16:50:23  mcs
 *   Forcing head revision
 *
 *   Revision 1.2  2004/02/11 21:51:11  rhafernik
 *   @scr 0 Log4J conversion and code cleanup
 *
 *   Revision 1.1.1.1  2004/02/11 01:04:16  cschellenger
 *   updating to pvcs 360store-current
 *
 *
 *
 *    Rev 1.5   Feb 06 2004 16:43:22   lzhao
 * change display message for different request.
 * Resolution for 3371: Feature Enhancement:  Gift Card Enhancement
 *
 *    Rev 1.4   Dec 19 2003 15:21:38   lzhao
 * issue code review follow up
 * Resolution for 3371: Feature Enhancement:  Gift Card Enhancement
 *
 *    Rev 1.3   Dec 18 2003 09:41:04   lzhao
 * format
 * Resolution for 3371: Feature Enhancement:  Gift Card Enhancement
 *
 *    Rev 1.2   Dec 16 2003 11:11:50   lzhao
 * issue refactory
 *
 *    Rev 1.1   Dec 12 2003 14:15:28   lzhao
 * format
 * Resolution for 3371: Feature Enhancement:  Gift Card Enhancement
 *
 *    Rev 1.0   Dec 08 2003 09:09:16   lzhao
 * Initial revision.
 * Resolution for 3371: Feature Enhancement:  Gift Card Enhancement
 *
 * ===========================================================================
 */
package oracle.retail.stores.pos.services.giftcard.issue;

import java.math.BigDecimal;

import oracle.retail.stores.commerceservices.common.currency.CurrencyIfc;
import oracle.retail.stores.domain.DomainGateway;
import oracle.retail.stores.domain.utility.GiftCardIfc;
import oracle.retail.stores.domain.utility.GiftCardIfc.StatusCode;
import oracle.retail.stores.foundation.manager.device.EncipheredCardDataIfc;
import oracle.retail.stores.foundation.manager.gui.UIModelIfc;
import oracle.retail.stores.foundation.manager.ifc.KeyStoreEncryptionManagerIfc;
import oracle.retail.stores.foundation.manager.ifc.UIManagerIfc;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.keystoreencryption.EncryptionServiceException;
import oracle.retail.stores.pos.services.PosSiteActionAdapter;
import oracle.retail.stores.pos.services.giftcard.GiftCardCargo;
import oracle.retail.stores.pos.ui.POSUIManagerIfc;
import oracle.retail.stores.pos.ui.beans.GiftCardBeanModel;
import oracle.retail.stores.pos.ui.beans.PromptAndResponseModel;

import org.apache.commons.codec.binary.Base64;

/**
 * This site displays get gift card number screen
 */
public class GetCardNumForGiftCardIssueSite extends PosSiteActionAdapter
{
    private static final long serialVersionUID = 1124145778880791629L;

    /* (non-Javadoc)
     * @see oracle.retail.stores.foundation.tour.application.SiteActionAdapter#arrive(oracle.retail.stores.foundation.tour.ifc.BusIfc)
     */
    @Override
    public void arrive(BusIfc bus)
    {
        POSUIManagerIfc ui = (POSUIManagerIfc)bus.getManager(UIManagerIfc.TYPE);
        UIModelIfc uiModel = ui.getModel();
        GiftCardBeanModel model = null;
        CurrencyIfc amount = DomainGateway.getBaseCurrencyInstance();
        GiftCardCargo cargo = (GiftCardCargo) bus.getCargo();
        GiftCardIfc giftCard = cargo.getGiftCard();
        if ((uiModel != null) && (uiModel instanceof GiftCardBeanModel))
        {
            // get gift card model if previously invalid gift card number entered
            // issue amount was saved in the model.
            model = (GiftCardBeanModel) ui.getModel();
        }
        else
        {
            model = new GiftCardBeanModel();
            model.setGiftCardStatus(StatusCode.Active);
        }

        if (giftCard != null)
        {
            amount = giftCard.getReqestedAmount();
        }

        model.setGiftCardAmount(new BigDecimal(amount.getStringValue()));

        // remove the issue amount buttons
        model.setLocalButtonBeanModel(null);
        if(cargo.getCardData() != null)
        {
            try
            {
                EncipheredCardDataIfc cardData = cargo.getCardData();
                KeyStoreEncryptionManagerIfc encryptionManager = (KeyStoreEncryptionManagerIfc)bus.getManager(KeyStoreEncryptionManagerIfc.TYPE);
                byte[] accountNumber = encryptionManager.decrypt(Base64.decodeBase64(cardData.getEncryptedAcctNumber().getBytes()));
                PromptAndResponseModel promptAndResponseModel = new PromptAndResponseModel();
                promptAndResponseModel.setResponseBytes(accountNumber);
                model.setPromptAndResponseModel(promptAndResponseModel);
            }
            catch(EncryptionServiceException ese)
            {
                logger.error("Could not decrypt gift card number", ese);
            }
        }
        ui.showScreen(POSUIManagerIfc.GET_CARD_NUM_FOR_GIFT_CARD, model);
    }
}
