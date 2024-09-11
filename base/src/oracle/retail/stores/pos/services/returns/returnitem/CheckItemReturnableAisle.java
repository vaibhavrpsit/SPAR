/* ===========================================================================
* Copyright (c) 2007, 2011, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/returns/returnitem/CheckItemReturnableAisle.java /rgbustores_13.4x_generic_branch/2 2011/08/18 08:44:03 jswan Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    jswan     08/17/11 - Modified to prevent the return of Gift Cards as
 *                         items and part of a transaction. Also cleaned up
 *                         references to gift cards objects in the return
 *                         tours.
 *    jswan     06/17/10 - Checkin external order integration files for
 *                         refresh.
 *    jswan     05/27/10 - First pass changes to return item for external order
 *                         project.
 *    cgreene   05/27/10 - convert to oracle packaging
 *    cgreene   05/26/10 - convert to oracle packaging
 *
 * ===========================================================================
 * $Log:
 *  1    360Commerce 1.0         8/3/2007 3:38:52 PM    Alan N. Sinton  CR
 *       28082 Made it so non-returnable items are non-returnable.
 * $
 * ===========================================================================
 */
package oracle.retail.stores.pos.services.returns.returnitem;

import oracle.retail.stores.domain.stock.GiftCardPLUItemIfc;
import oracle.retail.stores.domain.stock.GiftCertificateItemIfc;
import oracle.retail.stores.domain.stock.PLUItemIfc;
import oracle.retail.stores.foundation.manager.ifc.UIManagerIfc;
import oracle.retail.stores.foundation.tour.application.Letter;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.pos.services.PosLaneActionAdapter;
import oracle.retail.stores.pos.services.returns.returncommon.NoTransactionsErrorIfc;
import oracle.retail.stores.pos.services.returns.returncommon.ReturnItemCargoIfc;
import oracle.retail.stores.pos.ui.DialogScreensIfc;
import oracle.retail.stores.pos.ui.POSUIManagerIfc;
import oracle.retail.stores.pos.ui.beans.DialogBeanModel;

/**
 * Checks to see that the item is returnable.
 */
public class CheckItemReturnableAisle extends PosLaneActionAdapter implements NoTransactionsErrorIfc
{
    /** serialVersionUID */
    private static final long serialVersionUID = 3382136163990681101L;

    /**
     * Checks to see that the item is returnable.
     * @param bus
     * @see oracle.retail.stores.foundation.tour.application.LaneActionAdapter#traverse(oracle.retail.stores.foundation.tour.ifc.BusIfc)
     */
    public void traverse(BusIfc bus)
    {
        ReturnItemCargoIfc cargo = (ReturnItemCargoIfc)bus.getCargo();
        PLUItemIfc pluItem = cargo.getPLUItem();
        String letterName = "CheckSize";
        
        if (pluItem != null) 
        {
            if (cargo.isExternalOrder())
            {
                pluItem.setReturnExternalOrderItem(cargo.
                        getCurrentExternalOrderItemReturnStatusElement().getExternalOrderItem());
            }
            
            if (!pluItem.getItemClassification().getReturnEligible() ||
                 pluItem instanceof GiftCertificateItemIfc ||
                 pluItem instanceof GiftCardPLUItemIfc)
            {
                cargo.setPLUItem(null);
                POSUIManagerIfc ui = (POSUIManagerIfc)bus.getManager(UIManagerIfc.TYPE);
                DialogBeanModel model = new DialogBeanModel();
                model.setResourceID(INVALID_RETURN_ITEMS);
                model.setType(DialogScreensIfc.ERROR);
                letterName ="Invalid";
                model.setButtonLetter(0, letterName);
                ui.showScreen(POSUIManagerIfc.DIALOG_TEMPLATE, model);
            }
            else
            {
                bus.mail(new Letter(letterName), BusIfc.CURRENT);
            }
        }
    }
}
