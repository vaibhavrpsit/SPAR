/* ===========================================================================
* Copyright (c) 1998, 2011, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/giftcard/reload/GetAmountForGiftCardReloadSite.java /rgbustores_13.4x_generic_branch/3 2011/07/26 16:57:40 cgreene Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    cgreene   07/26/11 - moved StatusCode to GiftCardifc
 *    cgreene   05/27/11 - move auth response objects into domain
 *    asinton   04/26/11 - Refactor gift card for APF
 *    cgreene   05/26/10 - convert to oracle packaging
 *    abondala  01/03/10 - update header date
 *
 * ===========================================================================
 * $Log:
 *    5    360Commerce 1.4         5/12/2006 5:25:29 PM   Charles D. Baker
 *         Merging with v1_0_0_53 of Returns Managament
 *    4    360Commerce 1.3         1/22/2006 11:45:06 AM  Ron W. Haight
 *         removed references to com.ibm.math.BigDecimal
 *    3    360Commerce 1.2         3/31/2005 4:28:14 PM   Robert Pearse   
 *    2    360Commerce 1.1         3/10/2005 10:21:47 AM  Robert Pearse   
 *    1    360Commerce 1.0         2/11/2005 12:11:09 PM  Robert Pearse   
 *
 *   Revision 1.10  2004/08/19 21:55:41  blj
 *   @scr 6855 - Removed old code and fixed some flow issues with gift card credit.
 *
 *   Revision 1.9  2004/06/21 22:19:26  lzhao
 *   @scr 5774, 5447: gift card return/reload.
 *
 *   Revision 1.8  2004/04/14 20:10:51  lzhao
 *   @scr  3872 Redeem, change gift card request type from String to in.
 *
 *   Revision 1.7  2004/03/30 20:34:12  bwf
 *   @scr 4165 Gift Card Rework
 *
 *   Revision 1.6  2004/03/16 18:30:46  cdb
 *   @scr 0 Removed tabs from all java source code.
 *
 *   Revision 1.5  2004/03/03 23:15:12  bwf
 *   @scr 0 Fixed CommonLetterIfc deprecations.
 *
 *   Revision 1.4  2004/02/12 17:11:58  blj
 *   @scr 3824 - added a check for request type.
 *
 *   Revision 1.2  2004/02/11 21:51:11  rhafernik
 *   @scr 0 Log4J conversion and code cleanup
 *
 *   Revision 1.1.1.1  2004/02/11 01:04:16  cschellenger
 *   updating to pvcs 360store-current
 *
 * 
 *    Rev 1.7   Feb 10 2004 15:16:30   blj
 * gift card refund
 * 
 *    Rev 1.6   Feb 04 2004 15:25:56   blj
 * more gift card refund work.
 * 
 *    Rev 1.5   Dec 19 2003 15:21:42   lzhao
 * issue code review follow up
 * Resolution for 3371: Feature Enhancement:  Gift Card Enhancement
 * 
 *    Rev 1.4   Dec 16 2003 11:15:50   lzhao
 * code review follow up
 * Resolution for 3371: Feature Enhancement:  Gift Card Enhancement
 * 
 *    Rev 1.3   Dec 12 2003 14:17:36   lzhao
 * format
 * Resolution for 3371: Feature Enhancement:  Gift Card Enhancement
 * 
 *    Rev 1.2   Nov 26 2003 09:27:02   lzhao
 * cleanup, use the mehods in gift card utilties.
 * Resolution for 3371: Feature Enhancement:  Gift Card Enhancement
 * 
 *    Rev 1.1   Nov 21 2003 15:05:32   lzhao
 * refactory gift card using sale, completesale, giftoptions services and giftcardutilites.
 * Resolution for 3371: Feature Enhancement:  Gift Card Enhancement
 * 
 *    Rev 1.0   Oct 30 2003 10:57:28   lzhao
 * Initial revision.
 * ===========================================================================
 */
package oracle.retail.stores.pos.services.giftcard.reload;

import java.math.BigDecimal;

import oracle.retail.stores.foundation.manager.ifc.ParameterManagerIfc;
import oracle.retail.stores.foundation.manager.ifc.UIManagerIfc;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.domain.utility.GiftCardIfc.StatusCode;
import oracle.retail.stores.pos.manager.ifc.UtilityManagerIfc;
import oracle.retail.stores.pos.services.PosSiteActionAdapter;
import oracle.retail.stores.pos.services.giftcard.GiftCardCargo;
import oracle.retail.stores.pos.services.giftcard.GiftCardUtilities;
import oracle.retail.stores.pos.ui.POSUIManagerIfc;
import oracle.retail.stores.pos.ui.beans.GiftCardBeanModel;
import oracle.retail.stores.pos.ui.beans.NavigationButtonBeanModel;

/**
 *  This site displays the Gift Card amount screen
 */
@SuppressWarnings("serial")
public class GetAmountForGiftCardReloadSite extends PosSiteActionAdapter
{
    /*
     * (non-Javadoc)
     * @see oracle.retail.stores.foundation.tour.application.SiteActionAdapter#arrive(oracle.retail.stores.foundation.tour.ifc.BusIfc)
     */
    public void arrive(BusIfc bus)
    {        
        GiftCardCargo cargo = (GiftCardCargo) bus.getCargo();
        cargo.setItemQuantity(BigDecimal.ZERO);
        
        POSUIManagerIfc ui = (POSUIManagerIfc) bus.getManager(UIManagerIfc.TYPE);
        UtilityManagerIfc utility =
            (UtilityManagerIfc) bus.getManager(UtilityManagerIfc.TYPE);
        ParameterManagerIfc pm =
            (ParameterManagerIfc) bus.getManager(ParameterManagerIfc.TYPE);
        
        NavigationButtonBeanModel localNavigationButtonBeanModel = 
            GiftCardUtilities.getGiftCardDenominationsModel(utility, pm, logger, bus.getServiceName());
        
        GiftCardBeanModel giftCardModel = new GiftCardBeanModel();
        giftCardModel.setGiftCardStatus(StatusCode.Reload);                        
        giftCardModel.setLocalButtonBeanModel(localNavigationButtonBeanModel);
        
        ui.showScreen(POSUIManagerIfc.GET_AMOUNT_FOR_GIFT_CARD , giftCardModel);        
    }
}
