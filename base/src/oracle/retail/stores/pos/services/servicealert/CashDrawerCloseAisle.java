/* ===========================================================================
* Copyright (c) 1998, 2011, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/servicealert/CashDrawerCloseAisle.java /main/3 2011/02/16 09:13:29 cgreene Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    cgreene   02/15/11 - move constants into interfaces and refactor
 *    cgreene   05/26/10 - convert to oracle packaging
 *    blarsen   03/31/10 - Required for case when Till is already open and
 *                         simply needs to be inserted into cash drawer.
 *
 * ===========================================================================
 */
package oracle.retail.stores.pos.services.servicealert;

import oracle.retail.stores.foundation.manager.device.DeviceException;
import oracle.retail.stores.foundation.manager.ifc.UIManagerIfc;
import oracle.retail.stores.foundation.tour.application.Letter;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.foundation.tour.service.SessionBusIfc;
import oracle.retail.stores.pos.device.POSDeviceActions;
import oracle.retail.stores.pos.services.PosLaneActionAdapter;
import oracle.retail.stores.pos.services.common.CommonLetterIfc;
import oracle.retail.stores.pos.ui.POSUIManagerIfc;
import oracle.retail.stores.pos.ui.beans.POSBaseBeanModel;
import oracle.retail.stores.pos.ui.beans.StatusBeanModel;

/**
 * This aisle closes the cash drawer.
 */
public class CashDrawerCloseAisle extends PosLaneActionAdapter
{
    private static final long serialVersionUID = 6984436077265464059L;

    /**
     * Attempts to close cash drawer.
     * 
     * @param bus Service Bus
     */
    @Override
    public void traverse(BusIfc bus)
    {

        POSUIManagerIfc ui = (POSUIManagerIfc) bus.getManager(UIManagerIfc.TYPE);
        POSDeviceActions drawer = new POSDeviceActions((SessionBusIfc) bus);
        StatusBeanModel statusModel = new StatusBeanModel();

        try
        {
            // if cash drawer open then prompt to close
            if (drawer.isOpen().equals(Boolean.TRUE))
            {
                try
                {
                    drawer.waitForDrawerClose(); // blocks

                    // Update cash drawer status to ONLINE
                    statusModel.setStatus(POSUIManagerIfc.CASHDRAWER_STATUS, POSUIManagerIfc.ONLINE);
                }
                catch (DeviceException e)
                {
                    logger.warn( "Unable to wait for cash drawer.", e);

                    // Update cash drawer status to OFFLINE
                    statusModel.setStatus(POSUIManagerIfc.CASHDRAWER_STATUS, POSUIManagerIfc.OFFLINE);
                }
            }
        }
        catch (DeviceException e)
        {
            logger.warn( "Unable to close cash drawer.", e);

            // Update cash drawer status to OFFLINE
            statusModel.setStatus(POSUIManagerIfc.CASHDRAWER_STATUS, POSUIManagerIfc.OFFLINE);
        }

        POSBaseBeanModel baseModel = new POSBaseBeanModel();
        baseModel.setStatusBeanModel(statusModel);
        ui.setModel(POSUIManagerIfc.CLOSE_DRAWER, baseModel);

        //clear line display device of leftover tender information
        try
        {
            POSDeviceActions pda = new POSDeviceActions((SessionBusIfc) bus);
            pda.clearText();
        }
        catch (DeviceException e)
        {
            logger.warn("Unable to use Line Display.", e);
        }

        bus.mail(new Letter(CommonLetterIfc.SUCCESS), BusIfc.CURRENT);
    }
}
