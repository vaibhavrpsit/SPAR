/* ===========================================================================
* Copyright (c) 2011, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/printing/PrinterStatusOnlineAisle.java /rgbustores_13.4x_generic_branch/1 2011/06/03 09:46:42 cgreene Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    cgreene   06/02/11 - Tweaks to support Servebase chipnpin
 *
 * ===========================================================================
 */
package oracle.retail.stores.pos.services.printing;

import oracle.retail.stores.foundation.manager.ifc.UIManagerIfc;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.pos.services.PosLaneActionAdapter;
import oracle.retail.stores.pos.ui.POSUIManagerIfc;
import oracle.retail.stores.pos.ui.beans.POSBaseBeanModel;
import oracle.retail.stores.pos.ui.beans.StatusBeanModel;

/**
 * Set the printer status to online. This assumes the printer just succeeded in
 * printing.
 *
 * @author cgreene
 * @since 13.4
 */
public class PrinterStatusOnlineAisle extends PosLaneActionAdapter
{
    private static final long serialVersionUID = 1146256229885027972L;

    public static final String LANENAME = "PrinterStatusOnlineAisle";

    /* (non-Javadoc)
     * @see oracle.retail.stores.foundation.tour.application.LaneActionAdapter#traverse(oracle.retail.stores.foundation.tour.ifc.BusIfc)
     */
    @Override
    public void traverse(BusIfc bus)
    {
        POSUIManagerIfc ui = (POSUIManagerIfc)bus.getManager(UIManagerIfc.TYPE);

        // Update printer status
        StatusBeanModel statusModel = new StatusBeanModel();
        statusModel.setStatus(POSUIManagerIfc.PRINTER_STATUS, POSUIManagerIfc.ONLINE);
        POSBaseBeanModel baseModel = new POSBaseBeanModel();
        baseModel.setStatusBeanModel(statusModel);
        ui.setModel(POSUIManagerIfc.SHOW_STATUS_ONLY, baseModel);
    }
}
