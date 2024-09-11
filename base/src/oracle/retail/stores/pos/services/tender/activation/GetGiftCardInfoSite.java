/* ===========================================================================
* Copyright (c) 2011, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/tender/activation/GetGiftCardInfoSite.java /rgbustores_13.4x_generic_branch/3 2011/08/09 18:59:55 ohorne Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    ohorne    08/09/11 - APF:foreign currency support
 *    rrkohli   07/27/11 - using StatusCode
 * 
 * ===========================================================================
 */
package oracle.retail.stores.pos.services.tender.activation;

import java.math.BigDecimal;

import oracle.retail.stores.domain.manager.payment.AuthorizeTransferRequestIfc;
import oracle.retail.stores.domain.manager.payment.AuthorizeTransferRequestIfc.RequestSubType;
import oracle.retail.stores.domain.utility.GiftCardIfc.StatusCode;
import oracle.retail.stores.foundation.manager.ifc.UIManagerIfc;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.pos.services.PosSiteActionAdapter;
import oracle.retail.stores.pos.services.common.CommonActionsIfc;
import oracle.retail.stores.pos.ui.POSUIManagerIfc;
import oracle.retail.stores.pos.ui.beans.GiftCardBeanModel;
import oracle.retail.stores.pos.ui.beans.NavigationButtonBeanModel;


/**
 * This site displays the SELL_GIFT_CARD screen to allow the user to enter the
 * required information for the gift card.
 * 
 */
@SuppressWarnings("serial")
public class GetGiftCardInfoSite extends PosSiteActionAdapter
{
    /*
     * (non-Javadoc)
     * @see oracle.retail.stores.foundation.tour.application.SiteActionAdapter#arrive(oracle.retail.stores.foundation.tour.ifc.BusIfc)
     */
    @Override
    public void arrive(BusIfc bus)
    {
        ActivationCargo cargo = (ActivationCargo) bus.getCargo();
        AuthorizeTransferRequestIfc request = cargo.getCurrentRequest();

        // create a gift card bean model
        GiftCardBeanModel model = new GiftCardBeanModel();

        // set the gift card amount in the model
        if (request.getBaseAmount() != null)
        {
            BigDecimal amt = new BigDecimal(request.getBaseAmount().getStringValue());
            model.setGiftCardAmount(amt);
        }

        if (RequestSubType.ReloadGiftCard.equals(request.getRequestSubType()))
        {
            model.setGiftCardStatus(StatusCode.Reload);
        }

        if (logger.isInfoEnabled())
        {
            logger.info( "GetGiftCardInfoSite.arrive(), request = " + request);
        }

        // Disable undo and cancel, ask the UI Manager to display the menu screen
        NavigationButtonBeanModel globalNavigationModel = new NavigationButtonBeanModel();
        globalNavigationModel.setButtonEnabled(CommonActionsIfc.UNDO, false);
        globalNavigationModel.setButtonEnabled(CommonActionsIfc.CANCEL, true);
        model.setGlobalButtonBeanModel(globalNavigationModel);

        POSUIManagerIfc ui = (POSUIManagerIfc) bus.getManager(UIManagerIfc.TYPE);
        ui.showScreen(POSUIManagerIfc.SELL_GIFT_CARD, model);
    }

}