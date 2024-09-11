/* ===========================================================================
* Copyright (c) 1998, 2011, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/giftoptions/SelectGiftOptionSite.java /rgbustores_13.4x_generic_branch/2 2011/06/19 10:05:36 asinton Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    asinton   06/18/11 - Turned off gift card button when GiftCardsAccepted
 *                         parameter returns false
 *    cgreene   05/26/10 - convert to oracle packaging
 *    abondala  01/03/10 - update header date
 *
 * ===========================================================================
 * $Log:
 *    4    360Commerce 1.3         2/26/2008 12:22:15 AM  Manikandan Chellapan
 *         CR#30628 Fixed GC Options timeout
 *    3    360Commerce 1.2         3/31/2005 4:29:54 PM   Robert Pearse   
 *    2    360Commerce 1.1         3/10/2005 10:25:08 AM  Robert Pearse   
 *    1    360Commerce 1.0         2/11/2005 12:14:08 PM  Robert Pearse   
 *
 *   Revision 1.2  2004/02/12 16:50:25  mcs
 *   Forcing head revision
 *
 *   Revision 1.1.1.1  2004/02/11 01:04:16  cschellenger
 *   updating to pvcs 360store-current
 *
 *
 * 
 *    Rev 1.0   Nov 21 2003 15:09:22   lzhao
 * Initial revision.
 * ===========================================================================
 */
package oracle.retail.stores.pos.services.giftoptions;

import oracle.retail.stores.common.parameter.ParameterConstantsIfc;
import oracle.retail.stores.foundation.manager.ifc.ParameterManagerIfc;
import oracle.retail.stores.foundation.manager.ifc.UIManagerIfc;
import oracle.retail.stores.foundation.manager.parameter.ParameterException;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.pos.services.PosSiteActionAdapter;
import oracle.retail.stores.pos.ui.POSUIManagerIfc;
import oracle.retail.stores.pos.ui.beans.NavigationButtonBeanModel;
import oracle.retail.stores.pos.ui.beans.POSBaseBeanModel;

/**
 *  This site displays the Gift options
 *  @version $Revision: /rgbustores_13.4x_generic_branch/2 $
 */
@SuppressWarnings("serial")
public class SelectGiftOptionSite extends PosSiteActionAdapter
{
    /**
     * Gift Card button name.
     */
    public static final String GIFT_CARD_BUTTON = "GiftCard";

    /*
     * (non-Javadoc)
     * @see oracle.retail.stores.foundation.tour.application.SiteActionAdapter#arrive(oracle.retail.stores.foundation.tour.ifc.BusIfc)
     */
    public void arrive(BusIfc bus)
    {
        POSUIManagerIfc ui = (POSUIManagerIfc) bus.getManager(UIManagerIfc.TYPE);
        POSBaseBeanModel model = (POSBaseBeanModel)ui.getModel(POSUIManagerIfc.GIFT_OPTIONS);
        boolean giftCardsAccepted = true;
        try
        {
            ParameterManagerIfc pm = (ParameterManagerIfc)bus.getManager(ParameterManagerIfc.TYPE);
            giftCardsAccepted = pm.getBooleanValue(ParameterConstantsIfc.TENDER_GiftCardsAccepted);
        }
        catch(ParameterException pe)
        {
            logger.warn("ParameterManager failed with exception: " + pe);
        }
        NavigationButtonBeanModel localNavigation = new NavigationButtonBeanModel();
        localNavigation.setButtonEnabled(GIFT_CARD_BUTTON, giftCardsAccepted);
        model.setLocalButtonBeanModel(localNavigation);
        ui.showScreen(POSUIManagerIfc.GIFT_OPTIONS,model);
    }

}
