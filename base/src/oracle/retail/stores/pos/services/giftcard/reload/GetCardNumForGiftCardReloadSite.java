/* ===========================================================================
* Copyright (c) 1998, 2011, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/giftcard/reload/GetCardNumForGiftCardReloadSite.java /rgbustores_13.4x_generic_branch/5 2011/10/23 17:05:50 jswan Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    jswan     10/23/11 - Fixed issue with displaying gift card amount after
 *                         displaying an error screen.
 *    cgreene   07/26/11 - moved StatusCode to GiftCardifc
 *    cgreene   06/07/11 - update to first pass of removing pospal project
 *    cgreene   05/27/11 - move auth response objects into domain
 *    asinton   04/26/11 - Refactor gift card for APF
 *    blarsen   12/09/10 - cpoi msr not being activated. added call to
 *                         pda.showMSREntryPrompt() which actually does the
 *                         activation.
 *    cgreene   05/26/10 - convert to oracle packaging
 *    abondala  01/03/10 - update header date
 *
 * ===========================================================================
 * $Log:
 *    5    360Commerce 1.4         4/25/2007 8:52:26 AM   Anda D. Cadar   I18N
 *         merge
 *         
 *    4    360Commerce 1.3         1/22/2006 11:45:06 AM  Ron W. Haight
 *         removed references to com.ibm.math.BigDecimal
 *    3    360Commerce 1.2         3/31/2005 4:28:14 PM   Robert Pearse   
 *    2    360Commerce 1.1         3/10/2005 10:21:47 AM  Robert Pearse   
 *    1    360Commerce 1.0         2/11/2005 12:11:09 PM  Robert Pearse   
 *
 *   Revision 1.3  2004/02/12 16:50:24  mcs
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
 *    Rev 1.4   Dec 16 2003 11:15:50   lzhao
 * code review follow up
 * Resolution for 3371: Feature Enhancement:  Gift Card Enhancement
 * 
 *    Rev 1.3   Dec 08 2003 11:51:34   lzhao
 * use getGiftCardAmount.
 * 
 *    Rev 1.2   Nov 26 2003 09:27:00   lzhao
 * cleanup, use the mehods in gift card utilties.
 * Resolution for 3371: Feature Enhancement:  Gift Card Enhancement
 * 
 *    Rev 1.1   Nov 21 2003 15:05:32   lzhao
 * refactory gift card using sale, completesale, giftoptions services and giftcardutilites.
 * Resolution for 3371: Feature Enhancement:  Gift Card Enhancement
 * 
 *    Rev 1.0   Oct 30 2003 10:57:38   lzhao
 * Initial revision.
 *
 * ===========================================================================
 */
package oracle.retail.stores.pos.services.giftcard.reload;

import java.math.BigDecimal;

import oracle.retail.stores.commerceservices.common.currency.CurrencyIfc;
import oracle.retail.stores.domain.DomainGateway;
import oracle.retail.stores.domain.utility.GiftCardIfc;
import oracle.retail.stores.domain.utility.GiftCardIfc.StatusCode;
import oracle.retail.stores.foundation.manager.gui.UIModelIfc;
import oracle.retail.stores.foundation.manager.ifc.UIManagerIfc;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.pos.services.PosSiteActionAdapter;
import oracle.retail.stores.pos.services.giftcard.GiftCardCargo;
import oracle.retail.stores.pos.ui.POSUIManagerIfc;
import oracle.retail.stores.pos.ui.beans.GiftCardBeanModel;

/**
 * This site retrieves the gift card number from the UI or card swipe.
 */
public class GetCardNumForGiftCardReloadSite extends PosSiteActionAdapter
{
    private static final long serialVersionUID = 9096212676920273768L;

    /* (non-Javadoc)
     * @see oracle.retail.stores.foundation.tour.application.SiteActionAdapter#arrive(oracle.retail.stores.foundation.tour.ifc.BusIfc)
     */
    @Override
    public void arrive(BusIfc bus)
    {
        POSUIManagerIfc ui = (POSUIManagerIfc)bus.getManager(UIManagerIfc.TYPE);
        UIModelIfc uiModel = ui.getModel();
        GiftCardBeanModel model = null;

        if ((uiModel != null) && (uiModel instanceof GiftCardBeanModel))
        {
            // get gift card model if previously invalid gift card number entered
            // reload amount was saved in the model.
            model = (GiftCardBeanModel)ui.getModel();
        }
        else
        {
            model = new GiftCardBeanModel();
            model.setGiftCardStatus(StatusCode.Reload);
            CurrencyIfc amount = DomainGateway.getBaseCurrencyInstance();
            GiftCardCargo cargo = (GiftCardCargo)bus.getCargo();
            if (cargo != null)
            {
                GiftCardIfc giftCard = cargo.getGiftCard();
                if (giftCard != null)
                {
                    amount = giftCard.getReqestedAmount();
                }
            }
            model.setGiftCardAmount(new BigDecimal(amount.toString()));
        }

        model.setLocalButtonBeanModel(null); // remove the reload amount buttons
        ui.showScreen(POSUIManagerIfc.GET_CARD_NUM_FOR_GIFT_CARD, model);
    }
}
