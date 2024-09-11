/* ===========================================================================
* Copyright (c) 1998, 2012, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/returns/returnitem/ValidateSerialNumberAisle.java /main/5 2012/12/10 19:16:35 tksharma Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    tksharma  12/10/12 - commons-lang update 3.1
 *    sgu       12/17/10 - check in all
 *    sgu       12/17/10 - XbranchMerge sgu_bug-10373675 from
 *                         rgbustores_13.3x_generic_branch
 *    sgu       12/16/10 - fix tabs
 *    sgu       12/16/10 - rework the logic to check serial number dueplicates
 *    acadar    09/24/10 - use correct info to display in the item not in the
 *                         order prompt
 *    jswan     09/14/10 - Modified to support verification that serial number
 *                         entered by operator are contained in the external
 *                         order.
 *    jswan     09/14/10 - Added to verify that serial number matches one of
 *                         the order items.
 * ===========================================================================
 */
package oracle.retail.stores.pos.services.returns.returnitem;

// foundation imports
import oracle.retail.stores.domain.lineitem.SaleReturnLineItemIfc;
import oracle.retail.stores.foundation.manager.ifc.UIManagerIfc;
import oracle.retail.stores.foundation.tour.application.LaneActionAdapter;
import oracle.retail.stores.foundation.tour.application.Letter;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.pos.services.common.CommonLetterIfc;
import oracle.retail.stores.pos.services.returns.returntransaction.ReturnTransactionCargo;
import oracle.retail.stores.pos.ui.DialogScreensIfc;
import oracle.retail.stores.pos.ui.POSUIManagerIfc;
import oracle.retail.stores.pos.ui.beans.DialogBeanModel;
import oracle.retail.stores.pos.utility.TransactionUtility;

import org.apache.commons.lang3.StringUtils;

//--------------------------------------------------------------------------
/**
    This aisle validates the serial number.
    <p>
    @version $Revision: /main/5 $
**/
//--------------------------------------------------------------------------
public class ValidateSerialNumberAisle extends LaneActionAdapter
{
    /**
     * serialVersionUID
     */
    private static final long serialVersionUID = -6777739349960936953L;

    /**
       revision number
    **/
    public static final String revisionNumber = "$Revision: /main/5 $";

    /**
       Constant for invalid serial quantity error screen
    **/
    public static final String QUANTITY_NOTICE = "QuantityNotice";

    /**
       Constant for invalid quantity cannot be zero
    **/
    public static final String QUANTITY_CANNOT_BE_ZERO = "QuantityCannotBeZero";

    //----------------------------------------------------------------------
    /**
        This method validates the serial number.
       <P>
       @param  bus     Service Bus
    **/
    //----------------------------------------------------------------------
    public void traverse(BusIfc bus)
    {
        ReturnItemCargo cargo = (ReturnItemCargo)bus.getCargo();
        POSUIManagerIfc ui    = (POSUIManagerIfc)bus.getManager(UIManagerIfc.TYPE);
        String serialNumber   = ui.getInput();

        // Verify that there is no dulicate serial number in the same txn.
        boolean isDuplicate = checkDuplicateSerialNumber(cargo, serialNumber);
        if(isDuplicate)
        {
            DialogBeanModel model = new DialogBeanModel();
            model.setResourceID("DuplicateSerial");
            model.setType(DialogScreensIfc.ACKNOWLEDGEMENT);
            model.setButtonLetter(DialogScreensIfc.BUTTON_OK, CommonLetterIfc.RETRY);
            ui.showScreen(POSUIManagerIfc.DIALOG_TEMPLATE, model);
        }
        else if (cargo.isExternalOrder() &&
            !cargo.matchSerialNumberToOrderItem(cargo.getPLUItem().getItemID(), serialNumber))
        {
            // Verify the the serial number entered is in one of the external order
            // line items.
            DialogBeanModel dialogModel = new DialogBeanModel();
            dialogModel.setType(DialogScreensIfc.ACKNOWLEDGEMENT);
            dialogModel.setButtonLetter(DialogScreensIfc.BUTTON_OK, CommonLetterIfc.RETRY);
            String[]args = new String[2];
            args[0] = cargo.getPLUItem().getPosItemID();
            args[1] = serialNumber;
            dialogModel.setArgs(args);
            dialogModel.setResourceID(ReturnTransactionCargo.SERIAL_ITEM_NOT_IN_EX_ORDER);
            ui.showScreen(POSUIManagerIfc.DIALOG_TEMPLATE, dialogModel);
        }
        else
        {
            cargo.setItemSerial(serialNumber);
            bus.mail(new Letter(CommonLetterIfc.CONTINUE), BusIfc.CURRENT);
        }
    }

    /**
     * Check for item serial number duplicate
     * @param cargo the return item cargo
     * @param itemSerial the item serial number entered
     * @return a boolean indicating if a duplicate is found
     */
    protected boolean checkDuplicateSerialNumber(ReturnItemCargo cargo, String itemSerial)
    {
        String itemID = cargo.getPLUItem().getItemID();
        if (!StringUtils.isEmpty(itemID) && !StringUtils.isEmpty(itemSerial))
        {
            if (cargo.getTransaction() != null)
            {
                SaleReturnLineItemIfc[] lineItems = (SaleReturnLineItemIfc[])cargo.getTransaction().getLineItems();
                if (TransactionUtility.checkDuplicateSerialNumber(-1, itemID, itemSerial, lineItems))
                {
                    return true;
                }
            }

            if (TransactionUtility.checkDuplicateSerialNumber(-1, itemID, itemSerial, cargo.getReturnSaleLineItems()))
            {
                return true;
            }
        }
        return false;
    }
}
