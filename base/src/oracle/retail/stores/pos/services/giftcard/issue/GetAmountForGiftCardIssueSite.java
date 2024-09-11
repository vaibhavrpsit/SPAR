/* ===========================================================================
* Copyright (c) 1998, 2012, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/giftcard/issue/GetAmountForGiftCardIssueSite.java /main/11 2012/09/12 11:57:10 blarsen Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    asinton   03/30/12 - implemented giftcard issue
 *    asinton   04/26/11 - Refactor gift card for APF
 *    cgreene   05/26/10 - convert to oracle packaging
 *    abondala  01/03/10 - update header date
 *
 * ===========================================================================
 * $Log:
 *    3    360Commerce 1.2         3/31/2005 4:28:14 PM   Robert Pearse   
 *    2    360Commerce 1.1         3/10/2005 10:21:47 AM  Robert Pearse   
 *    1    360Commerce 1.0         2/11/2005 12:11:09 PM  Robert Pearse   
 *
 *   Revision 1.12  2004/08/20 19:51:49  blj
 *   @scr 6855 - update after code review
 *
 *   Revision 1.11  2004/08/19 21:55:41  blj
 *   @scr 6855 - Removed old code and fixed some flow issues with gift card credit.
 *
 *   Revision 1.10  2004/06/21 22:19:26  lzhao
 *   @scr 5774, 5447: gift card return/reload.
 *
 *   Revision 1.9  2004/06/11 19:13:15  lzhao
 *   @scr 5396 fix the problem in return exchange
 *
 *   Revision 1.8  2004/05/11 22:07:14  lzhao
 *   @scr 4887: fix the problem of showing card number in amount screen.
 *
 *   Revision 1.7  2004/04/22 20:52:17  epd
 *   @scr 4513 FIxes to tender, especially gift card, gift cert, and store credit
 *
 *   Revision 1.6  2004/04/14 20:10:43  lzhao
 *   @scr  3872 Redeem, change gift card request type from String to in.
 *
 *   Revision 1.5  2004/03/03 23:15:10  bwf
 *   @scr 0 Fixed CommonLetterIfc deprecations.
 *
 *   Revision 1.4  2004/02/12 17:01:08  blj
 *   @scr 3824 - added a check to set the request type.
 *
 *   Revision 1.2  2004/02/11 21:51:11  rhafernik
 *   @scr 0 Log4J conversion and code cleanup
 *
 *   Revision 1.1.1.1  2004/02/11 01:04:16  cschellenger
 *   updating to pvcs 360store-current
 *
 *
 * 
 *    Rev 1.6   Feb 04 2004 15:25:54   blj
 * more gift card refund work.
 * 
 *    Rev 1.5   Jan 29 2004 12:00:40   blj
 * added gift card refund issue.
 * 
 *    Rev 1.4   Dec 19 2003 15:21:36   lzhao
 * issue code review follow up
 * Resolution for 3371: Feature Enhancement:  Gift Card Enhancement
 * 
 *    Rev 1.3   Dec 18 2003 09:41:04   lzhao
 * format
 * Resolution for 3371: Feature Enhancement:  Gift Card Enhancement
 * 
 *    Rev 1.2   Dec 16 2003 11:11:48   lzhao
 * issue refactory
 * 
 *    Rev 1.1   Dec 12 2003 14:15:26   lzhao
 * format
 * Resolution for 3371: Feature Enhancement:  Gift Card Enhancement
 * 
 *    Rev 1.0   Dec 08 2003 09:09:02   lzhao
 * Initial revision.
 * Resolution for 3371: Feature Enhancement:  Gift Card Enhancement
 *
 * ===========================================================================
 */
package oracle.retail.stores.pos.services.giftcard.issue;

import oracle.retail.stores.foundation.manager.ifc.ParameterManagerIfc;
import oracle.retail.stores.foundation.manager.ifc.UIManagerIfc;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.pos.manager.ifc.UtilityManagerIfc;
import oracle.retail.stores.pos.services.PosSiteActionAdapter;
import oracle.retail.stores.pos.services.giftcard.GiftCardCargo;
import oracle.retail.stores.pos.services.giftcard.GiftCardUtilities;
import oracle.retail.stores.pos.ui.POSUIManagerIfc;
import oracle.retail.stores.pos.ui.beans.GiftCardBeanModel;
import oracle.retail.stores.pos.ui.beans.NavigationButtonBeanModel;
import oracle.retail.stores.pos.ui.beans.PromptAndResponseModel;

/**
 *  This site displays the Gift Card amount screen
 */
@SuppressWarnings("serial")
public class GetAmountForGiftCardIssueSite extends PosSiteActionAdapter
{
    /*
     * (non-Javadoc)
     * @see oracle.retail.stores.foundation.tour.application.SiteActionAdapter#arrive(oracle.retail.stores.foundation.tour.ifc.BusIfc)
     */
    public void arrive(BusIfc bus)
    {
        POSUIManagerIfc ui = (POSUIManagerIfc) bus.getManager(UIManagerIfc.TYPE);

        UtilityManagerIfc utility =
            (UtilityManagerIfc) bus.getManager(UtilityManagerIfc.TYPE);
        ParameterManagerIfc pm =
            (ParameterManagerIfc) bus.getManager(ParameterManagerIfc.TYPE);
        NavigationButtonBeanModel localNavigationButtonBeanModel =
            GiftCardUtilities.getGiftCardDenominationsModel(
                    utility,
                    pm,
                    logger,
                    bus.getServiceName());
        GiftCardBeanModel giftCardModel = new GiftCardBeanModel();
        giftCardModel.setLocalButtonBeanModel(localNavigationButtonBeanModel);
        GiftCardCargo cargo = (GiftCardCargo)bus.getCargo();
        if(cargo.getGiftCardAmount() != null)
        {
            PromptAndResponseModel promptAndResponseModel = giftCardModel.getPromptAndResponseModel();
            if(promptAndResponseModel == null)
            {
                promptAndResponseModel = new PromptAndResponseModel();
            }
            promptAndResponseModel.setResponseText(cargo.getGiftCardAmount().abs().toGroupFormattedString());
            giftCardModel.setPromptAndResponseModel(promptAndResponseModel);
        }
        ui.showScreen(POSUIManagerIfc.GET_AMOUNT_FOR_GIFT_CARD, giftCardModel);
    }
}
