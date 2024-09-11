/* ===========================================================================
* Copyright (c) 1998, 2013, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/returns/returnfindorder/SelectOrderSite.java /main/1 2013/03/19 11:55:20 sgu Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 * 
 * ===========================================================================
 */
package oracle.retail.stores.pos.services.returns.returnfindorder;

import oracle.retail.stores.foundation.manager.ifc.UIManagerIfc;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.pos.services.PosSiteActionAdapter;
import oracle.retail.stores.pos.services.returns.returnfindtrans.ReturnFindTransCargo;
import oracle.retail.stores.pos.ui.POSUIManagerIfc;
import oracle.retail.stores.pos.ui.beans.ListBeanModel;

/**
 * This site display a list of orders
 * 
 * @author sgu
 *
 */
public class SelectOrderSite extends PosSiteActionAdapter
{
    /**
     * serial version UID
     */
    private static final long serialVersionUID = -3363005070658161039L;

    //--------------------------------------------------------------------------
    /**
       This site displays a list of orders.
       @param bus the bus arriving at this site
    **/
    //--------------------------------------------------------------------------
    public void arrive(BusIfc bus)
    {
        // Create the model and set the data
        ReturnFindTransCargo cargo = (ReturnFindTransCargo) bus.getCargo();
        ListBeanModel model = new ListBeanModel();
        model.setListModel(cargo.getOrderSummaries());
        model.setSelectedRow(0);

        // Display the screen
        POSUIManagerIfc ui = (POSUIManagerIfc) bus.getManager(UIManagerIfc.TYPE);
        ui.showScreen(POSUIManagerIfc.RETURN_LINKED_TRANS, model);
    }
}
