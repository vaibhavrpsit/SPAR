/* ===========================================================================
* Copyright (c) 1998, 2014, Oracle and/or its affiliates. All rights reserved.
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/layaway/find/SerialNumberEnteredAisle.java /main/1 2014/02/27 20:10:18 mchellap Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    mchellap  02/27/14 - Added support for checking duplicate serial numbers
 *
 * ===========================================================================
 */
package oracle.retail.stores.pos.services.layaway.find;

// foundation imports
import org.apache.commons.lang3.StringUtils;

import oracle.retail.stores.pos.services.common.CommonLetterIfc;
import oracle.retail.stores.domain.lineitem.AbstractTransactionLineItemIfc;
import oracle.retail.stores.domain.lineitem.SaleReturnLineItemIfc;
import oracle.retail.stores.foundation.manager.ifc.UIManagerIfc;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.foundation.tour.ifc.LaneActionIfc;
import oracle.retail.stores.pos.services.PosLaneActionAdapter;
import oracle.retail.stores.pos.services.layaway.LayawayCargo;
import oracle.retail.stores.pos.ui.DialogScreensIfc;
import oracle.retail.stores.pos.ui.POSUIManagerIfc;
import oracle.retail.stores.pos.ui.beans.DialogBeanModel;

public class SerialNumberEnteredAisle extends PosLaneActionAdapter implements LaneActionIfc
{
    // This id is used to tell
    // the compiler not to generate a
    // new serialVersionUID.
    //
    private static final long serialVersionUID = 6268685595732561206L;

    private static final String DUPLICATE_LETTER = "Duplicate";

    /**
     * Validates entered serial number
     * 
     * @param bus the bus traversing this lane
     */
    public void traverse(BusIfc bus)
    {
        String letter = CommonLetterIfc.VALIDATE;
        boolean isDuplicate = false;

        POSUIManagerIfc ui = (POSUIManagerIfc) bus.getManager(UIManagerIfc.TYPE);
        LayawayCargo cargo = (LayawayCargo) bus.getCargo();

        SaleReturnLineItemIfc lineItem = cargo.getLineItem();
        int lineNumber = lineItem.getLineNumber();
        String itemID = lineItem.getItemID();
        String itemSerial = ui.getInput();

        if (!StringUtils.isEmpty(itemID) && !StringUtils.isEmpty(itemSerial))
        {

            AbstractTransactionLineItemIfc[] lineItems = cargo.getSerializedItems();

            for (int i = 0; i < lineItems.length; i++)
            {

                if (lineItems[i] instanceof SaleReturnLineItemIfc)
                {
                    SaleReturnLineItemIfc saleItem = (SaleReturnLineItemIfc) lineItems[i];
                    if (lineNumber != saleItem.getLineNumber() && itemID.equalsIgnoreCase(saleItem.getItemID())
                            && itemSerial.equalsIgnoreCase(saleItem.getItemSerial()))
                    {
                        isDuplicate = true;
                        break;
                    }
                }
            }

        }

        if (isDuplicate)
        {
            DialogBeanModel model = new DialogBeanModel();

            model.setResourceID("DuplicateSerial");
            model.setType(DialogScreensIfc.ACKNOWLEDGEMENT);
            model.setButtonLetter(DialogScreensIfc.BUTTON_OK, DUPLICATE_LETTER);

            ui = (POSUIManagerIfc) bus.getManager(UIManagerIfc.TYPE);
            ui.showScreen(POSUIManagerIfc.DIALOG_TEMPLATE, model);
        }
        else
        {
            bus.mail(letter, BusIfc.CURRENT);
        }

    }

}
