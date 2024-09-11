/* ===========================================================================
* Copyright (c) 2003, 2014, Oracle and/or its affiliates. All rights reserved.
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/tender/giftcard/EnterGiftCardNumberUISite.java /main/15 2014/06/03 13:25:35 mchellap Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    jswan     10/29/14 - Modified to valid gift card number for bin range
 *                         and check digit.
 *    mchellap  05/22/14 - MPOS Returns Changes: Use ui.getInput() for getting
 *                         response text.
 *    cgreene   07/12/11 - update generics
 *    cgreene   07/07/11 - convert entryMethod to an enum
 *    asinton   06/29/11 - Refactored to use EntryMethod and
 *                         AuthorizationMethod enums.
 *    cgreene   06/07/11 - update to first pass of removing pospal project
 *    cgreene   05/26/10 - convert to oracle packaging
 *    abondala  01/03/10 - update header date
 *
 * ===========================================================================
 * $Log:
 * ===========================================================================
 */
package oracle.retail.stores.pos.services.tender.giftcard;

import oracle.retail.stores.foundation.manager.device.MSRModel;
import oracle.retail.stores.foundation.manager.ifc.UIManagerIfc;
import oracle.retail.stores.foundation.tour.application.Letter;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.pos.services.PosSiteActionAdapter;
import oracle.retail.stores.pos.services.common.CommonLetterIfc;
import oracle.retail.stores.pos.services.tender.TenderCargo;
import oracle.retail.stores.pos.ui.POSUIManagerIfc;
import oracle.retail.stores.pos.ui.beans.POSBaseBeanModel;

/**
 * @author blj
 */
public class EnterGiftCardNumberUISite extends PosSiteActionAdapter
{
    private static final long serialVersionUID = -4582187633786155629L;

    /**
     * Displays the GIFT_CARD screen.
     * 
     * @param bus Service Bus
     */
    @Override
    public void arrive(BusIfc bus)
    {
        // Check first for eventual card already swiped
        TenderCargo cargo = (TenderCargo)bus.getCargo();
        MSRModel msr = cargo.getPreTenderMSRModel();
        if (msr != null)
        {
            bus.mail(new Letter(CommonLetterIfc.NEXT), BusIfc.CURRENT);
        }
        else
        {
            POSUIManagerIfc ui = (POSUIManagerIfc)bus.getManager(UIManagerIfc.TYPE);
            // show the screen
            ui.showScreen(POSUIManagerIfc.GIFT_CARD, new POSBaseBeanModel());
        }
    }
}
