/* ===========================================================================
* Copyright (c) 2008, 2011, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/order/common/WaitForCashDrawerCloseRoad.java /rgbustores_13.4x_generic_branch/1 2011/05/05 14:06:33 mszekely Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    cgreene   05/26/10 - convert to oracle packaging
 *    abondala  01/03/10 - update header date
 *    asinton   06/04/09 - Check in of new road to wait on close of cash
 *                         drawer.
 *    asinton   06/04/09 - New road to wait on close of cash drawer.
 *
 * ===========================================================================
 */
package oracle.retail.stores.pos.services.order.common;

import oracle.retail.stores.foundation.manager.device.DeviceException;
import oracle.retail.stores.foundation.manager.ifc.UIManagerIfc;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.foundation.tour.service.SessionBusIfc;
import oracle.retail.stores.pos.device.POSDeviceActions;
import oracle.retail.stores.pos.services.PosLaneActionAdapter;
import oracle.retail.stores.pos.ui.POSUIManagerIfc;
import oracle.retail.stores.pos.ui.beans.POSBaseBeanModel;
import oracle.retail.stores.pos.ui.beans.StatusBeanModel;

/**
 * This road causes the tour to wait for the cash draw to be closed.
 */
public class WaitForCashDrawerCloseRoad extends PosLaneActionAdapter
{
    /**
     * serialVersionUID
     */
    private static final long serialVersionUID = -5827454031920421424L;

    /* (non-Javadoc)
     * @see oracle.retail.stores.foundation.tour.application.LaneActionAdapter#traverse(oracle.retail.stores.foundation.tour.ifc.BusIfc)
     */
    @Override
    public void traverse(BusIfc bus)
    {
        POSUIManagerIfc ui = (POSUIManagerIfc)bus.getManager(UIManagerIfc.TYPE);
        POSDeviceActions drawer = new POSDeviceActions((SessionBusIfc)bus);
        POSBaseBeanModel baseModel = new POSBaseBeanModel();
        StatusBeanModel statusModel = new StatusBeanModel();
        try
        {
            drawer.waitForDrawerClose(); // blocks
            // Update cash drawer status to ONLINE
            statusModel.setStatus(POSUIManagerIfc.CASHDRAWER_STATUS, POSUIManagerIfc.ONLINE);
        }
        catch (DeviceException e)
        {
            logger.warn("Unable to wait for cash drawer. " + e.getMessage());
            // Update cash drawer status to OFFLINE
            statusModel.setStatus(POSUIManagerIfc.CASHDRAWER_STATUS, POSUIManagerIfc.OFFLINE);
            baseModel.setStatusBeanModel(statusModel);
            ui.setModel(POSUIManagerIfc.CLOSE_DRAWER, baseModel);
        }
    }
    
}
