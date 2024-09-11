/*===========================================================================
* Copyright (c) 2012, 2014, Oracle and/or its affiliates. All rights reserved.
* ===========================================================================
* $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/order/common/OrderOfflineAisle.java /main/2 2014/05/14 14:41:28 cgreene Exp $
* ===========================================================================
* NOTES
* <other useful comments, qualifications, etc.>
*
* MODIFIED    (MM/DD/YY)
*    cgreene   05/14/14 - rename retrieve to resume
*    sgu       06/20/12 - handle OMS offline during transaction retrieval
*    sgu       06/20/12 - refactor ORPOS flow to handle get a cross channel
*                         order id
*    sgu       06/19/12 - handle xc order id creation failure
*    sgu       06/18/12 - refactor ORPOS to call order manager to get new order
*                         id
* ===========================================================================
*/
package oracle.retail.stores.pos.services.order.common;

import oracle.retail.stores.foundation.manager.ifc.UIManagerIfc;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.pos.services.PosLaneActionAdapter;
import oracle.retail.stores.pos.services.common.CommonLetterIfc;
import oracle.retail.stores.pos.services.modifytransaction.resume.ModifyTransactionResumeCargo;
import oracle.retail.stores.pos.ui.DialogScreensIfc;
import oracle.retail.stores.pos.ui.POSUIManagerIfc;
import oracle.retail.stores.pos.ui.beans.DialogBeanModel;

@SuppressWarnings("serial")
public class OrderOfflineAisle extends PosLaneActionAdapter
{
    /**
     * Order offline screen name
     */
    private static final String RESOURCE_ID = "OmsOrInvOffline";

    /**
     * Displays the order offline screen. Sets the Ok button letter to Failure.
     * 
     * @param bus the bus arriving at this site.
     */
    @Override
    public void traverse(BusIfc bus)
    {
        POSUIManagerIfc ui=(POSUIManagerIfc)bus.getManager(UIManagerIfc.TYPE);

        String letter = CommonLetterIfc.ERROR;  // error during order create
        if (bus.getCargo() instanceof ModifyTransactionResumeCargo)
        {
            letter = CommonLetterIfc.FAILURE; // error during retrieval of a suspended order
        }

        // Using "generic dialog bean". display the error dialog
        DialogBeanModel model = new DialogBeanModel();

        // Set buttons and arguments
        model.setResourceID(RESOURCE_ID);
        model.setType(DialogScreensIfc.ERROR);
        model.setButtonLetter(DialogScreensIfc.BUTTON_OK, letter);

        // set and display the model
        ui.showScreen(POSUIManagerIfc.DIALOG_TEMPLATE, model);
    }
}
