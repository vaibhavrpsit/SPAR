/* ===========================================================================
* Copyright (c) 2010, 2011, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/externalorder/processorder/SellOrderItemReturnShuttle.java /rgbustores_13.4x_generic_branch/1 2011/05/05 14:05:59 mszekely Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    cgreene   05/26/10 - convert to oracle packaging
 *    acadar    05/14/10 - initial version for external order processing
 *    acadar    05/14/10 - initial version
  * ===========================================================================
 */
package oracle.retail.stores.pos.services.externalorder.processorder;


import oracle.retail.stores.pos.services.externalorder.sellorderitem.SellOrderItemCargo;

import oracle.retail.stores.pos.services.common.FinancialCargoShuttle;

import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.foundation.tour.ifc.ShuttleIfc;


/**
 * Shuttle from process order service to sell order item service
 * @author acadar
 *
 */
public class SellOrderItemReturnShuttle extends FinancialCargoShuttle implements ShuttleIfc
{

    /**
     * Serial Version UID
     */
    private static final long serialVersionUID = -8389966128995612139L;


    /**
     * Cargo from the sell order item service
     */
    protected SellOrderItemCargo orderItemCargo = null;

    /**
     * Loads the shuttle
     */
    public void load(BusIfc bus)
    {
        super.load(bus);

        orderItemCargo = (SellOrderItemCargo) bus.getCargo();
    }

    /**
     * Unloads the sell order item cargo into the process order cargo
     */
    public void unload(BusIfc bus)
    {
        super.unload(bus);
        ProcessOrderCargo cargo = (ProcessOrderCargo) bus.getCargo();

        cargo.setTransaction(orderItemCargo.getTransaction());
    }
}
