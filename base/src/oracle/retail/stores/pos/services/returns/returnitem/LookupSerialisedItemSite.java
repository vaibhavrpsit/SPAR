/* ===========================================================================
* Copyright (c) 1998, 2011, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/returns/returnitem/LookupSerialisedItemSite.java /rgbustores_13.4x_generic_branch/2 2011/08/18 08:44:03 jswan Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    jswan     08/17/11 - Modified to prevent the return of Gift Cards as
 *                         items and part of a transaction. Also cleaned up
 *                         references to gift cards objects in the return
 *                         tours.
 *    cgreene   05/27/10 - convert to oracle packaging
 *    cgreene   05/26/10 - convert to oracle packaging
 *    abondala  01/03/10 - update header date
 *    mchellap  12/16/09 - Moved the letters to CommonLetterIfc
 *    mchellap  12/15/09 - LookupSerialisedItemSite.java
 *
 * ===========================================================================
 */
package oracle.retail.stores.pos.services.returns.returnitem;

// foundation imports
import oracle.retail.stores.pos.services.common.CommonLetterIfc;
import oracle.retail.stores.domain.stock.GiftCardPLUItemIfc;
import oracle.retail.stores.domain.stock.GiftCertificateItemIfc;
import oracle.retail.stores.domain.stock.PLUItemIfc;
import oracle.retail.stores.foundation.manager.ifc.UIManagerIfc;
import oracle.retail.stores.foundation.tour.application.Letter;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;

import oracle.retail.stores.pos.services.PosSiteActionAdapter;
import oracle.retail.stores.pos.services.common.PLUItemCargoIfc;
import oracle.retail.stores.pos.services.returns.returncommon.NoTransactionsErrorIfc;
import oracle.retail.stores.pos.ui.DialogScreensIfc;
import oracle.retail.stores.pos.ui.POSUIManagerIfc;
import oracle.retail.stores.pos.ui.beans.DialogBeanModel;

/**
 * Site to check return eligibility of serialised item.
 */
public class LookupSerialisedItemSite extends PosSiteActionAdapter implements NoTransactionsErrorIfc
{
    /**
     * Proceed to next site or ailse
     *
     * @param bus Service Bus
     */
    public void arrive(BusIfc bus)
    {
        PLUItemCargoIfc cargo = (PLUItemCargoIfc) bus.getCargo();
        PLUItemIfc pluItem = cargo.getPLUItem();
        String letterName = CommonLetterIfc.CHECK_SIZE;

        if (pluItem != null && 
            (!pluItem.getItemClassification().getReturnEligible() ||
             pluItem instanceof GiftCertificateItemIfc            || 
             pluItem instanceof GiftCardPLUItemIfc))
        {
            cargo.setPLUItem(null);
            POSUIManagerIfc ui = (POSUIManagerIfc) bus.getManager(UIManagerIfc.TYPE);
            DialogBeanModel model = new DialogBeanModel();
            model.setResourceID(INVALID_RETURN_ITEMS);
            model.setType(DialogScreensIfc.ERROR);
            letterName = CommonLetterIfc.INVALID;
            model.setButtonLetter(0, letterName);
            ui.showScreen(POSUIManagerIfc.DIALOG_TEMPLATE, model);
        }
        else
        {
            bus.mail(new Letter(letterName), BusIfc.CURRENT);
        }

    }
}
