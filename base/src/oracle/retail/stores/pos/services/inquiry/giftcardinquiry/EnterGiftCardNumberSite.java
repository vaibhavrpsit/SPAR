/* ===========================================================================
* Copyright (c) 1998, 2011, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/inquiry/giftcardinquiry/EnterGiftCardNumberSite.java /rgbustores_13.4x_generic_branch/4 2011/07/26 16:57:40 cgreene Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    cgreene   07/26/11 - moved StatusCode to GiftCardIfc
 *    cgreene   06/07/11 - update to first pass of removing pospal project
 *    cgreene   05/27/11 - move auth response objects into domain
 *    asinton   04/25/11 - Refactored giftcard inquiry for APF
 *    blarsen   12/08/10 - cpoi msr not being activated. added call to
 *                         pda.showMSREntryPrompt() which actually does the
 *                         activation.
 *    cgreene   05/26/10 - convert to oracle packaging
 *    abondala  01/03/10 - update header date
 *
 * ===========================================================================
 * $Log:
 *    6    360Commerce 1.5         5/10/2008 4:53:47 PM   Alan N. Sinton  CR
 *         31653: Fixed null pointer by setting the GIFT_CARD screen's model
 *         to null - irony?  Code reviewed by Tony Zgarba.
 *    5    360Commerce 1.4         3/19/2008 11:34:52 PM  Manikandan Chellapan
 *         CR#30959 Modified code to use encrypted card number instead of
 *         clear text number.
 *    4    360Commerce 1.3         1/22/2006 11:45:09 AM  Ron W. Haight
 *         removed references to com.ibm.math.BigDecimal
 *    3    360Commerce 1.2         3/31/2005 4:28:01 PM   Robert Pearse   
 *    2    360Commerce 1.1         3/10/2005 10:21:25 AM  Robert Pearse   
 *    1    360Commerce 1.0         2/11/2005 12:10:54 PM  Robert Pearse   
 *
 *   Revision 1.4  2004/06/29 19:59:03  lzhao
 *   @scr 5477: add gift card inquiry in training mode.
 *
 *   Revision 1.3  2004/06/25 21:46:51  lzhao
 *   @scr 5453: show item number automatically.
 *
 *   Revision 1.2  2004/06/25 14:16:57  lzhao
 *   @scr 5811: issue and reload reverse.
 *
 *   Revision 1.1  2004/04/07 21:10:08  lzhao
 *   @scr 3872: gift card redeem and revise gift card activation
 *
 *   Revision 1.3  2004/02/12 16:50:22  mcs
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
 *    Rev 1.3   Feb 06 2004 16:43:22   lzhao
 * change display message for different request.
 * Resolution for 3371: Feature Enhancement:  Gift Card Enhancement
 * 
 *    Rev 1.2   Dec 08 2003 09:26:32   lzhao
 * remove expiration date
 * 
 *    Rev 1.1   Nov 07 2003 16:50:14   lzhao
 * add new inquiry multple time and print offline features.
 * Resolution for 3371: Feature Enhancement:  Gift Card Enhancement
 * 
 *    Rev 1.0   Aug 29 2003 15:59:38   CSchellenger
 * Initial revision.
 * 
 *    Rev 1.0   Apr 29 2002 15:23:02   msg
 * Initial revision.
 * 
 *    Rev 1.0   Mar 18 2002 11:32:54   msg
 * Initial revision.
 * 
 *    Rev 1.0   Sep 21 2001 11:20:40   msg
 * Initial revision.
 * 
 *    Rev 1.1   Sep 17 2001 13:08:02   msg
 * header update
 * ===========================================================================
 */
package oracle.retail.stores.pos.services.inquiry.giftcardinquiry;

import java.math.BigDecimal;

import oracle.retail.stores.domain.utility.GiftCardIfc;
import oracle.retail.stores.domain.utility.GiftCardIfc.StatusCode;
import oracle.retail.stores.foundation.manager.ifc.UIManagerIfc;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.pos.config.bundles.BundleConstantsIfc;
import oracle.retail.stores.pos.manager.ifc.UtilityManagerIfc;
import oracle.retail.stores.pos.services.PosSiteActionAdapter;
import oracle.retail.stores.pos.services.giftcard.GiftCardConstantsIfc;
import oracle.retail.stores.pos.ui.POSUIManagerIfc;
import oracle.retail.stores.pos.ui.beans.GiftCardBeanModel;
import oracle.retail.stores.pos.ui.beans.POSBaseBeanModel;

/**
 * This site displays the GIFT_CARD screen.
 */
public class EnterGiftCardNumberSite extends PosSiteActionAdapter
{
    private static final long serialVersionUID = -4630721788738850217L;

    /**
     * Displays the GIFT_CARD screen.
     * @param  bus     Service Bus
     */
    @Override
    public void arrive(BusIfc bus)
    {
        InquiryCargo cargo = (InquiryCargo) bus.getCargo();
        UtilityManagerIfc utility =
          (UtilityManagerIfc) bus.getManager(UtilityManagerIfc.TYPE);
        POSUIManagerIfc ui = (POSUIManagerIfc) bus.getManager(UIManagerIfc.TYPE);
       
        GiftCardIfc giftCard = cargo.getGiftCard();
        
        if (giftCard == null)
        {
            // Inquiry a gift card for the first time
            ui.showScreen(POSUIManagerIfc.GIFT_CARD, new POSBaseBeanModel());
        }
        else
        {
            // GiftCardNumberEnteredAisle looks at the model for the values from PromptAndResponse. 
            // Need to clear the GIFT_CARD's PromptAndResponse in order to get the right PromptAndResponse
            // in the afore mentioned Aisle.
            ui.setModel(POSUIManagerIfc.GIFT_CARD, null);
            GiftCardBeanModel model = new GiftCardBeanModel();

            String giftCardAmountLabel =
              utility.retrieveText( GiftCardConstantsIfc.GIFT_CARD_INQUIRY_SPEC_TAG,
                                    BundleConstantsIfc.GIFTCARD_BUNDLE_NAME,
                                    GiftCardConstantsIfc.REMAINING_BALANCE_LABEL_TAG,
                                    GiftCardConstantsIfc.REMAINING_BALANCE_LABEL_TEXT );

            model.setOpenAmount(giftCard.getOpenAmount());  // true or false
            model.setGiftCardNumber(giftCard.getEncipheredCardData().getTruncatedAcctNumber());
            if (giftCard.getStatus() == StatusCode.Unknown)
            {
                model.setGiftCardInitialBalance(new BigDecimal(0.0));
                model.setGiftCardAmount(new BigDecimal(0.0));
                model.setValidInquiry(false);
            }
            else
            {
                model.setGiftCardInitialBalance(new BigDecimal(giftCard.getInitialBalance().getStringValue()));
                model.setGiftCardAmount(new BigDecimal(giftCard.getCurrentBalance().getStringValue()));
            }
            model.setGiftCardAmountLabel(giftCardAmountLabel);
            model.setGiftCardStatus(null);
            // Display inquiry result and inquiry another gift card without exit inquiry service
            ui.showScreen(POSUIManagerIfc.GIFT_CARD_INQUIRY, model);
        }
    }
}
