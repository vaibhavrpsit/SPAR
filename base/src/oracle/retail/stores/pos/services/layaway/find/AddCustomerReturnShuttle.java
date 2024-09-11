/* ===========================================================================
* Copyright (c) 2013, Oracle and/or its affiliates. All rights reserved.
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/layaway/find/AddCustomerReturnShuttle.java /main/1 2013/07/30 15:31:23 icole Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    icole     07/30/13 - Remove letter checking in unload.
 *    icole     06/17/13 - Initial
 *
 * ===========================================================================
 * $Log: $
 * ===========================================================================
 */
package oracle.retail.stores.pos.services.layaway.find;

import org.apache.log4j.Logger;

import oracle.retail.stores.pos.services.common.CommonLetterIfc;
import oracle.retail.stores.pos.services.layaway.LayawayCargo;
import oracle.retail.stores.pos.services.tender.TenderCargo;
import oracle.retail.stores.domain.arts.DataTransactionFactory;
import oracle.retail.stores.domain.arts.DataTransactionKeys;
import oracle.retail.stores.domain.arts.LayawayDataTransaction;
import oracle.retail.stores.domain.customer.CustomerIfc;
import oracle.retail.stores.foundation.manager.data.DataException;
import oracle.retail.stores.foundation.manager.ifc.UIManagerIfc;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.foundation.tour.ifc.LetterIfc;
import oracle.retail.stores.foundation.tour.ifc.ShuttleIfc;
import oracle.retail.stores.pos.services.customer.common.CustomerCargo;
import oracle.retail.stores.pos.ui.POSUIManagerIfc;
import oracle.retail.stores.pos.ui.beans.POSBaseBeanModel;
import oracle.retail.stores.pos.ui.beans.StatusBeanModel;

//------------------------------------------------------------------------------
/**
 * Return shuttle from CustomerAdd to Layaway not found.
 *
 * @since Release 14
 */
//------------------------------------------------------------------------------
public class AddCustomerReturnShuttle implements ShuttleIfc
{
    // This id is used to tell
    // the compiler not to generate a
    // new serialVersionUID.
    //
    static final long serialVersionUID = 6755938181916764009L;

    /**
     * The logger to which log messages will be sent.
     */
    protected static Logger logger =
        Logger.getLogger(oracle.retail.stores.pos.services.layaway.find.AddCustomerReturnShuttle.class);

    // Shuttle name
    public static final String SHUTTLENAME = "AddCustomerReturnShuttle";
    // Customer cargo
    protected CustomerCargo customerCargo = null;

    //--------------------------------------------------------------------------
    /**
     * Load CustomerAddCargo for use in unload().
     *
     * @param bus  the bus being loaded
     */
    //--------------------------------------------------------------------------
    public void load(BusIfc bus)
    {
        customerCargo = (CustomerCargo) bus.getCargo();
    }

    //--------------------------------------------------------------------------
    /**
     * Copy relevant Customer info to LayawayCargo. The Customer is linked to the Transaction.
     *
     * @param bus
     *            the bus being unloaded
     */
    //--------------------------------------------------------------------------
    public void unload(BusIfc bus)
    {
        LayawayCargo layawayCargo = (LayawayCargo) bus.getCargo();
        CustomerIfc customer = customerCargo.getCustomer();
        if (customer != null)
        {
            layawayCargo.setCustomer(customer);
            layawayCargo.getLayaway().setCustomer(customer);
            // set the customer's name in the status area
            POSUIManagerIfc ui = (POSUIManagerIfc) bus.getManager(UIManagerIfc.TYPE);
            StatusBeanModel statusModel = new StatusBeanModel();
            statusModel.setCustomerName(customer.getCustomerName());
            POSBaseBeanModel baseModel = new POSBaseBeanModel();
            baseModel.setStatusBeanModel(statusModel);
            ui.setModel(POSUIManagerIfc.SHOW_STATUS_ONLY, baseModel);
        }
    }
}
