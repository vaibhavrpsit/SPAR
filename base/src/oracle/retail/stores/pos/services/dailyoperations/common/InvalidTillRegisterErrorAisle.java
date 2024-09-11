/* ===========================================================================
* Copyright (c) 2014, Oracle and/or its affiliates. All rights reserved.
 * ===========================================================================
 * $Log:$
 * ===========================================================================
 */

package oracle.retail.stores.pos.services.dailyoperations.common;

import oracle.retail.stores.foundation.manager.ifc.UIManagerIfc;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.pos.services.PosLaneActionAdapter;
import oracle.retail.stores.pos.services.common.CommonLetterIfc;
import oracle.retail.stores.pos.services.common.StoreStatusCargo;
import oracle.retail.stores.pos.ui.DialogScreensIfc;
import oracle.retail.stores.pos.ui.POSUIManagerIfc;
import oracle.retail.stores.pos.ui.beans.DialogBeanModel;

import org.apache.log4j.Logger;


/**
 * This class added to show the dialog that the condition that the till associated with this register
 * is opened by another register.  This can occur if two registers at least one offline open the same till.
 * When the register/registers come online one of the registers will receive an error POS is 
 * selected from the main menu. Prior to this change there was an unexpected exception, with this 
 * change a message is displayed that there is a problem with the till being opened on another register and
 * to close the till on the other register then a new till can be assigned.
 * 
 *@since 14.1
 */
public class InvalidTillRegisterErrorAisle extends PosLaneActionAdapter
{
    private static final long serialVersionUID = -8955076693330013375L;
    protected static final Logger logger = Logger.getLogger(InvalidTillRegisterErrorAisle.class);
    /**
     * Resource id
     */
    protected static final String INVALID_TILL_REGISTER_ERROR = "InvalidTillRegisterError";

    public void traverse(BusIfc bus)
    {
        POSUIManagerIfc ui = (POSUIManagerIfc)bus.getManager(UIManagerIfc.TYPE);
        DialogBeanModel dialogModel = new DialogBeanModel();

        StoreStatusCargo cargo = (StoreStatusCargo) bus.getCargo();
        String[] args = new String[2];
        args[0] = cargo.getRegister().getCurrentTillID();
        args[1] = cargo.getInvalidWorkstationID();
        dialogModel.setResourceID(INVALID_TILL_REGISTER_ERROR);
        dialogModel.setArgs(args);
        dialogModel.setType(DialogScreensIfc.ERROR);
        dialogModel.setButtonLetter(
            DialogScreensIfc.BUTTON_OK,
            CommonLetterIfc.TILL_NOT_OWNED);
        ui.showScreen(POSUIManagerIfc.DIALOG_TEMPLATE, dialogModel);
    }
}
