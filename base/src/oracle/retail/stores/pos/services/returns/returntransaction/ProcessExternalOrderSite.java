/* ===========================================================================
* Copyright (c) 2001, 2011, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/returns/returntransaction/ProcessExternalOrderSite.java /rgbustores_13.4x_generic_branch/1 2011/05/05 14:05:57 mszekely Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    jswan     07/21/10 - Fixed issues around transaction search by tender.
 *    jswan     06/17/10 - Checkin external order integration files for
 *                         refresh.
 *    jswan     06/05/10 - Added for External Order integration.
 *    jswan     06/01/10 - Checked in for refresh to latest lable.
 *    jswan     05/27/10 - First pass changes to return item for external order
 *                         project.
 *    jswan     05/19/10 - Add transaction reentry classes
 *    jswan     05/19/10 - Add site for returns external order refactoring.
 *
 * ===========================================================================
 */
package oracle.retail.stores.pos.services.returns.returntransaction;

import oracle.retail.stores.pos.services.common.CommonLetterIfc;
import oracle.retail.stores.foundation.manager.ifc.UIManagerIfc;
import oracle.retail.stores.foundation.tour.application.Letter;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.pos.services.PosSiteActionAdapter;
import oracle.retail.stores.pos.services.returns.returncommon.ExternalOrderItemReturnStatusElement;
import oracle.retail.stores.pos.services.returns.returncommon.ReturnItemCargoIfc;
import oracle.retail.stores.pos.ui.DialogScreensIfc;
import oracle.retail.stores.pos.ui.POSUIManagerIfc;
import oracle.retail.stores.pos.ui.beans.DialogBeanModel;

//--------------------------------------------------------------------------
/**
   This method gets the next available external order item, updates 
   the cargo with POS Item ID an mails a continue letter.  This causes
   the tour to process the item ID as if the user had entered it.
   
   This will be called iteratively until all external order items have
   been processed.
**/
//--------------------------------------------------------------------------
public class ProcessExternalOrderSite extends PosSiteActionAdapter
{

    /** serialVersionUID */
    private static final long serialVersionUID = 6430044420573899581L;
    private static final String EXT_ORDER_NOT_IN_RETRIEVED_RTRN = "ExtOrderNotInRetrievedRtrn";

    //----------------------------------------------------------------------
    /**
       This method gets the next available external order item, updates 
       the cargo with POS Item ID an mails a continue letter.  This causes
       the tour to process the item ID as if the user had entered it.
       
       This will be called iteratively until all external order items have
       been processed.
       <P>
       @param  bus     Service Bus
    **/
    //----------------------------------------------------------------------
    public void arrive(BusIfc bus)
    {
        ReturnItemCargoIfc cargo = (ReturnItemCargoIfc)bus.getCargo();
        boolean itemIDFound      = false;
        String letter            = CommonLetterIfc.SUCCESS;

        // If there are still external order item process...
        // Get the next external order item that has not been returned.
        for(int i = 0; i < cargo.getExternalOrderItemReturnStatusElements().size() && !itemIDFound; i++)
        {
            ExternalOrderItemReturnStatusElement statusElement = cargo.
                getExternalOrderItemReturnStatusElements().get(i);
            if (!statusElement.isSelected())
            {
                statusElement.setSelected(true);
                cargo.setPLUItemID(statusElement.getExternalOrderItem().getPOSItemId());
                cargo.setCurrentExternalOrderItemReturnStatusElement(statusElement);
                cargo.setItemScanned(false);
                itemIDFound = true;
                letter      = CommonLetterIfc.CONTINUE;
            }
        }
        
        if (!itemIDFound && (cargo.getReturnSaleLineItems() == null || cargo.getReturnSaleLineItems().length == 0))
        {
            DialogBeanModel dialogModel = new DialogBeanModel();
            dialogModel.setResourceID(EXT_ORDER_NOT_IN_RETRIEVED_RTRN);
            dialogModel.setType(DialogScreensIfc.ACKNOWLEDGEMENT);
            dialogModel.setButtonLetter(DialogScreensIfc.BUTTON_OK, "Undo");
            POSUIManagerIfc ui = (POSUIManagerIfc)bus.getManager(UIManagerIfc.TYPE);
            ui.showScreen(POSUIManagerIfc.DIALOG_TEMPLATE, dialogModel);
        }
        else 
        {    
            if (!itemIDFound)
            {
                cargo.setCurrentItem(cargo.getLastLineItemReturnedIndex());
            }

            bus.mail(new Letter(letter), BusIfc.CURRENT);
        }
    }
}
