/* ===========================================================================
* Copyright (c) 2011, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/tender/GiftCardEntryUISite.java /rgbustores_13.4x_generic_branch/2 2011/09/23 14:12:53 asinton Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    asinton   09/23/11 - show gift card entry screen if in transaction
 *                         reentry mode
 *    asinton   08/19/11 - Added configurable flow to prompt for gift card
 *                         number during tender.
 *
 * ===========================================================================
 */

package oracle.retail.stores.pos.services.tender;

import oracle.retail.stores.foundation.manager.ifc.UIManagerIfc;
import oracle.retail.stores.foundation.tour.gate.Gateway;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.pos.services.PosSiteActionAdapter;
import oracle.retail.stores.pos.services.common.CommonLetterIfc;
import oracle.retail.stores.pos.ui.POSUIManagerIfc;
import oracle.retail.stores.pos.ui.beans.POSBaseBeanModel;

/**
 * This site displays the gift card entry screen if the configuration allows.
 * @author asinton
 * @since 13.4
 */
@SuppressWarnings("serial")
public class GiftCardEntryUISite extends PosSiteActionAdapter
{
    /** Constant for application property group */
    public static final String APPLICATION_PROPERTY_GROUP = "application";

    /** Constant for manual entry gift card property name */
    public static final String POS_GFCARD_TENDER_ENTRY_REQUIRED = "POSGFCardTenderEntryRequired";

    /* (non-Javadoc)
     * @see oracle.retail.stores.foundation.tour.application.SiteActionAdapter#arrive(oracle.retail.stores.foundation.tour.ifc.BusIfc)
     */
    @Override
    public void arrive(BusIfc bus)
    {
        // get the posGFCardTenderEntryRequired value
        boolean posGFCardTenderEntryRequired = Gateway.getBooleanProperty(APPLICATION_PROPERTY_GROUP, POS_GFCARD_TENDER_ENTRY_REQUIRED, false);
        // get the transaction reentry mode value
        TenderCargo cargo = (TenderCargo)bus.getCargo();
        boolean transactionReentryMode = cargo.getRegister().getWorkstation().isTransReentryMode();
        // show ui if either posGFCardTenderEntryRequired or transactionReentryMode
        boolean showUI = posGFCardTenderEntryRequired || transactionReentryMode;
        if(showUI)
        {
            POSUIManagerIfc uiManager = (POSUIManagerIfc)bus.getManager(UIManagerIfc.TYPE);
            uiManager.showScreen(POSUIManagerIfc.GIFT_CARD, new POSBaseBeanModel());
        }
        else
        {
            bus.mail(CommonLetterIfc.CONTINUE, BusIfc.CURRENT);
        }
    }

}
