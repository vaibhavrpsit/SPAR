/* ===========================================================================
* Copyright (c) 1998, 2012, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/sale/ConfirmItemAisle.java /main/2 2012/12/10 12:59:27 rgour Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    rgour     12/10/12 - Enhancement in suspended transaction phase
 *    rgour     11/05/12 - Enhancements in Suspended Transactions
 *    rgour     11/02/12 - Enhancements in Suspended Transactions
 *    
 *
 * ===========================================================================
 *
 */
package oracle.retail.stores.pos.services.sale;

import org.apache.log4j.Logger;

import oracle.retail.stores.foundation.manager.gui.UIException;
import oracle.retail.stores.foundation.manager.gui.UISubsystem;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.foundation.utility.config.ConfigurationException;
import oracle.retail.stores.pos.services.PosLaneActionAdapter;
import oracle.retail.stores.pos.ui.DialogScreensIfc;
import oracle.retail.stores.pos.ui.POSJFCUISubsystem;
import oracle.retail.stores.pos.ui.POSUIManagerIfc;
import oracle.retail.stores.pos.ui.beans.DialogBeanModel;

@SuppressWarnings("serial")
public class ConfirmItemAisle extends PosLaneActionAdapter
{
    @Override
    public void traverse(BusIfc bus)
    {
        SaleCargoIfc cargo = (SaleCargoIfc)bus.getCargo();
        POSJFCUISubsystem ui = (POSJFCUISubsystem)UISubsystem.getInstance();
        DialogBeanModel dialogModel = new DialogBeanModel();
        dialogModel.setResourceID("ItemConfirm");
        dialogModel.setType(DialogScreensIfc.CONFIRMATION);
        dialogModel.setButtonLetter(DialogScreensIfc.BUTTON_YES, "Yes");
        dialogModel.setButtonLetter(DialogScreensIfc.BUTTON_NO, "No");
        String args[] = new String[1];
        args[0] = cargo.getPLUItemID();
        dialogModel.setArgs(args);

        try
        {
            ui.showScreen(POSUIManagerIfc.DIALOG_TEMPLATE, dialogModel);
        }
        catch (UIException uie)
        {
            Logger logger = Logger.getLogger(ConfirmItemAisle.class);
            logger.error("ConfirmItemAisle showScreen failed ");
        }
        catch (ConfigurationException ce)
        {
            Logger logger = Logger.getLogger(ConfirmItemAisle.class);
            logger.error("ConfirmItemAisle showScreen failed ");
        }

    }
}
