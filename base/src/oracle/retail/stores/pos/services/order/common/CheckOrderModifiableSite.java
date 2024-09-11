/*===========================================================================
* Copyright (c) 2012, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/order/common/CheckOrderModifiableSite.java /main/1 2012/10/30 14:16:39 sgu Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 * sgu         10/30/12 - refactor sites to check order status for pickup and
 *                        cancel
 * sgu         10/30/12 - add new file
 * sgu         10/30/12 - add new file
 * sgu         10/30/12 - Creation
 * ===========================================================================
 */

package oracle.retail.stores.pos.services.order.common;

import oracle.retail.stores.domain.order.OrderConstantsIfc;
import oracle.retail.stores.domain.order.OrderIfc;
import oracle.retail.stores.foundation.manager.ifc.UIManagerIfc;
import oracle.retail.stores.foundation.tour.application.Letter;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.pos.manager.ifc.UtilityManagerIfc;
import oracle.retail.stores.pos.services.PosSiteActionAdapter;
import oracle.retail.stores.pos.services.common.CommonLetterIfc;
import oracle.retail.stores.pos.ui.DialogScreensIfc;
import oracle.retail.stores.pos.ui.POSUIManagerIfc;
import oracle.retail.stores.pos.ui.beans.DialogBeanModel;

public class CheckOrderModifiableSite extends PosSiteActionAdapter
{
    /**
     *
     */
    private static final long serialVersionUID = -530931246299153128L;

    /**
    Constant for error message screen id.
     **/
    public static final String CANNOT_MODIFY_ORDER = "CannotModifyOrder";

    /**
    Constant for error message argument text.
     **/
    public static final String COMPLETED = "Completed";

    /**
    Constant for error message argument text.
     **/
    public static final String CANCELED = "Canceled";

    /**
    Constant for error message argument text.
     **/
    public static final String VOIDED = "Voided";

    //----------------------------------------------------------------------
    /**
    Determines the current order status. If the order status is Completed,
    Canceled, or VOIDED then displays the Cannot Modify dialog screen,
    otherwise mails a Success letter to continue with cancelling the order.
    <p>
    @param  bus     Service Bus
     **/
    //----------------------------------------------------------------------

    public void arrive(BusIfc bus)
    {
        // get the current order from cargo
        OrderCargo cargo = (OrderCargo)bus.getCargo();
        OrderIfc order = cargo.getOrder();
        int status = order.getStatus().getStatus().getStatus();

        // get the ui manager
        POSUIManagerIfc       ui = (POSUIManagerIfc)bus.getManager(UIManagerIfc.TYPE);
        UtilityManagerIfc utility =
            (UtilityManagerIfc) bus.getManager(UtilityManagerIfc.TYPE);

        // setup arg text strings for error screen
        String args[] = new String[4];
        args[0] = cargo.getServiceName();
        args[1] = utility.retrieveDialogText("CannotModifyOrder.Completed",
                COMPLETED);
        args[2] = args[1];

        // test the order status - if Completed or Canceled do NOT allow cancel, display error
        if (status == OrderConstantsIfc.ORDER_STATUS_COMPLETED ||
                status == OrderConstantsIfc.ORDER_STATUS_CANCELED ||
                status == OrderConstantsIfc.ORDER_STATUS_VOIDED)
        {
            // test if order canceled to re-use screen, change ui argument
            if (status == OrderConstantsIfc.ORDER_STATUS_CANCELED)
            {
                args[1] = utility.retrieveDialogText("CannotModifyOrder.Canceled",
                        CANCELED);
                args[2] = args[1];
            }
            else if (status == OrderConstantsIfc.ORDER_STATUS_VOIDED)
            {
                args[1] = utility.retrieveDialogText("CannotModifyOrder.Voided",
                        VOIDED);
                args[2] = args[1];
            }
            // setup and display the Cannot Modify Dialog screen
            DialogBeanModel model = new DialogBeanModel();
            model.setResourceID(CANNOT_MODIFY_ORDER);
            model.setButtonLetter(DialogScreensIfc.BUTTON_OK,CommonLetterIfc.FAILURE);
            model.setType(DialogScreensIfc.ERROR);
            model.setArgs(args);
            ui.showScreen(POSUIManagerIfc.DIALOG_TEMPLATE, model);
        }
        else
        {
            // mail the Success letter
            bus.mail(new Letter (CommonLetterIfc.SUCCESS),BusIfc.CURRENT);
        }
    }
}

