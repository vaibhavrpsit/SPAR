/* ===========================================================================
* Copyright (c) 2010, 2012, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/externalorder/processorder/ProcessOrderSendReturnShuttle.java /main/4 2012/10/22 15:36:22 yiqzhao Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    yiqzhao   10/19/12 - Refactor by using DestinationTaxRule station to get
 *                         new tax rules from shipping destination postal code.
 *    cgreene   05/26/10 - convert to oracle packaging
 *    acadar    05/14/10 - initial version for external order processing
 *    acadar    05/14/10 - initial version
  * ===========================================================================
 */
package oracle.retail.stores.pos.services.externalorder.processorder;


import oracle.retail.stores.pos.services.externalorder.processordersend.ProcessOrderSendCargo;


import oracle.retail.stores.pos.services.common.FinancialCargoShuttle;

import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.foundation.tour.ifc.ShuttleIfc;


/**
 * Shuttle from process order send to process order service
 * @author acadar
 *
 */
public class ProcessOrderSendReturnShuttle extends FinancialCargoShuttle implements ShuttleIfc
{
    /**
     * Serial version UID
     */
    private static final long serialVersionUID = 5134919484751443386L;

    /**
     * Cargo from the sell order item service
     */
    protected ProcessOrderSendCargo orderSendCargo = null;

    /**
     * Loads the shuttle
     */
    public void load(BusIfc bus)
    {
        super.load(bus);

        orderSendCargo = (ProcessOrderSendCargo) bus.getCargo();
    }

    /**
     * Unloads the sell order item cargo into the process order cargo
     */
    public void unload(BusIfc bus)
    {
        super.unload(bus);
        ProcessOrderCargo cargo = (ProcessOrderCargo) bus.getCargo();

        cargo.setTransaction(orderSendCargo.getTransaction());
        
        cargo.setShippingMethod(orderSendCargo.getShippingMethod());
    }
}
