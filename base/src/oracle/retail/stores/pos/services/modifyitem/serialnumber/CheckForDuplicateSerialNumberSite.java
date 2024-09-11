/* ===========================================================================
* Copyright (c) 2010, 2012, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    tksharma  12/10/12 - commons-lang update 3.1
 *    cgreene   05/08/12 - implement force use of entered serial number instead
 *                         of checking with SIM
 *    sgu       12/17/10 - check in all
 *    sgu       12/17/10 - XbranchMerge sgu_bug-10373675 from
 *                         rgbustores_13.3x_generic_branch
 *    sgu       12/16/10 - fix tab
 *    sgu       12/16/10 - rework the logic to check serial number dueplicates
 *    rrkohli   12/01/10 - duplicate serailized item fix
 *    rrkohli   12/01/10 - file renamed
 * ===========================================================================
 */

package oracle.retail.stores.pos.services.modifyitem.serialnumber;

import oracle.retail.stores.domain.lineitem.SaleReturnLineItemIfc;
import oracle.retail.stores.foundation.manager.ifc.UIManagerIfc;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.pos.services.PosSiteActionAdapter;
import oracle.retail.stores.pos.services.common.CommonLetterIfc;
import oracle.retail.stores.pos.ui.DialogScreensIfc;
import oracle.retail.stores.pos.ui.POSUIManagerIfc;
import oracle.retail.stores.pos.ui.beans.DialogBeanModel;
import oracle.retail.stores.pos.utility.TransactionUtility;

import org.apache.commons.lang3.StringUtils;

@SuppressWarnings("serial")
public class CheckForDuplicateSerialNumberSite extends PosSiteActionAdapter
{
    /* (non-Javadoc)
     * @see oracle.retail.stores.foundation.tour.application.SiteActionAdapter#arrive(oracle.retail.stores.foundation.tour.ifc.BusIfc)
     */
    @Override
    public void arrive(BusIfc bus)
    {
        SerializedItemCargo cargo = (SerializedItemCargo)bus.getCargo();

        boolean isDuplicate = checkDuplicateSerialNumber(cargo);
        if (isDuplicate)
        {
            DialogBeanModel model = new DialogBeanModel();

            model.setResourceID("DuplicateSerial");
            model.setType(DialogScreensIfc.ACKNOWLEDGEMENT);
            model.setButtonLetter(DialogScreensIfc.BUTTON_OK, CommonLetterIfc.RETRY);

            POSUIManagerIfc ui = (POSUIManagerIfc) bus.getManager(UIManagerIfc.TYPE);
            ui.showScreen(POSUIManagerIfc.DIALOG_TEMPLATE, model);
        }
        else
        {
            bus.mail(CommonLetterIfc.NEXT);
        }
    }

    /**
     * Check for item serial number duplicate
     * 
     * @param cargo the serialized item cargo
     * @return a boolean indicating if a duplicate is found
     */
    protected boolean checkDuplicateSerialNumber(SerializedItemCargo cargo)
    {
        SaleReturnLineItemIfc lineItem = cargo.getItem();
        int lineNumber = lineItem.getLineNumber();
        String itemID = lineItem.getItemID();
        String itemSerial = lineItem.getItemSerial();

        if (!StringUtils.isEmpty(itemID) && !StringUtils.isEmpty(itemSerial))
        {
            if (cargo.getTransaction() != null)
            {
                SaleReturnLineItemIfc[] lineItems = (SaleReturnLineItemIfc[])cargo.getTransaction().getLineItems();
                if (TransactionUtility.checkDuplicateSerialNumber(lineNumber, itemID, itemSerial, lineItems))
                {
                    return true;
                }
            }

            if (cargo.isForReturn())
            {
                if (TransactionUtility.checkDuplicateSerialNumber(lineNumber, itemID, itemSerial, cargo.getReturnSaleLineItems()))
                {
                    return true;
                }
            }
        }
        return false;
    }
}

