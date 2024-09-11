/* ===========================================================================
* Copyright (c) 2011, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    asinton   11/05/14 - renamed new laneaction per review comments
 *    asinton   11/04/14 - fixed issues with print of store credit when an
 *                         error occurs.
 *
 * ===========================================================================
 */

package oracle.retail.stores.pos.services.printing;

import oracle.retail.stores.domain.tender.TenderLineItemIfc;
import oracle.retail.stores.domain.tender.TenderStoreCredit;
import oracle.retail.stores.domain.transaction.TenderableTransactionIfc;
import oracle.retail.stores.foundation.manager.ifc.UIManagerIfc;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.pos.services.PosLaneActionAdapter;
import oracle.retail.stores.pos.services.common.CommonActionsIfc;
import oracle.retail.stores.pos.ui.DialogScreensIfc;
import oracle.retail.stores.pos.ui.POSUIManagerIfc;
import oracle.retail.stores.pos.ui.beans.DialogBeanModel;

/**
 * This aisle shows an error dialog indicating that the print of the
 * store credit resulted in an error.
 * @since 14.1
 *
 */
@SuppressWarnings("serial")
public class ShowStoreCreditPrintErrorAisle extends PosLaneActionAdapter
{
    /** constant for print store credit dialog name */
    public static final String STORE_CREDIT_PRINT_ERROR_DIALOG = "StoreCreditPrintError";

    /* (non-Javadoc)
     * @see oracle.retail.stores.foundation.tour.application.LaneActionAdapter#traverse(oracle.retail.stores.foundation.tour.ifc.BusIfc)
     */
    @Override
    public void traverse(BusIfc bus)
    {
        PrintingCargo cargo = (PrintingCargo)bus.getCargo();
        TenderableTransactionIfc transaction = cargo.getTransaction();
        String storeCreditId = "";
        for(TenderLineItemIfc tenderItem : transaction.getTenderLineItems())
        {
            if(tenderItem instanceof TenderStoreCredit)
            {
                storeCreditId = ((TenderStoreCredit)tenderItem).getStoreCreditID();
                break;
            }
        }
        String[] dialogArgs = { storeCreditId };
        DialogBeanModel dialogModel = new DialogBeanModel();
        dialogModel.setResourceID(STORE_CREDIT_PRINT_ERROR_DIALOG);
        dialogModel.setType(DialogScreensIfc.ERROR);
        dialogModel.setArgs(dialogArgs);
        dialogModel.setButtonLetter(DialogScreensIfc.BUTTON_OK, CommonActionsIfc.CONTINUE);
        // display dialog
        POSUIManagerIfc ui = (POSUIManagerIfc) bus.getManager(UIManagerIfc.TYPE);
        ui.showScreen(POSUIManagerIfc.DIALOG_TEMPLATE, dialogModel);
    }

}
