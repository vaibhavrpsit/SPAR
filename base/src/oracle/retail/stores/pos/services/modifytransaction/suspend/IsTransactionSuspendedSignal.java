/* ===========================================================================
* Copyright (c) 2013, Oracle and/or its affiliates. All rights reserved.
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/modifytransaction/suspend/IsTransactionSuspendedSignal.java /main/2 2013/07/02 13:09:09 jswan Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    jswan     07/02/13 - Modified to prevent a class cast exception in
 *                         Printing tour when printing House Account Payment
 *                         reciepts.
 *    cgreene   06/26/13 - Updated to printed only suspended receipt if the
 *                         transaction is suspended.
 *
 * ===========================================================================
 */
package oracle.retail.stores.pos.services.modifytransaction.suspend;

import oracle.retail.stores.domain.transaction.TransactionIfc;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.foundation.tour.ifc.TrafficLightIfc;
import oracle.retail.stores.pos.services.common.RetailTransactionCargoIfc;
import oracle.retail.stores.pos.services.common.TenderableTransactionCargoIfc;

import org.apache.log4j.Logger;

/**
 * This signal returns true if the cargo has a transaction which is in the
 * suspended state.
 *
 * @author cgreene
 * @since 14.0
 */
@SuppressWarnings("serial")
public class IsTransactionSuspendedSignal implements TrafficLightIfc
{
    private static final Logger logger = Logger.getLogger(IsTransactionSuspendedSignal.class);

    /* (non-Javadoc)
     * @see oracle.retail.stores.foundation.tour.ifc.TrafficLightIfc#roadClear(oracle.retail.stores.foundation.tour.ifc.BusIfc)
     */
    @Override
    public boolean roadClear(BusIfc bus)
    {
        TransactionIfc transaction = null;

        // a PrintingCargo is a RetailTransactionCargoIfc
        if (bus.getCargo() instanceof RetailTransactionCargoIfc)
        {
            RetailTransactionCargoIfc transCargo = (RetailTransactionCargoIfc)bus.getCargo();
            transaction = transCargo.getRetailTransaction();
        }
        if (transaction == null && bus.getCargo() instanceof TenderableTransactionCargoIfc)
        {
            TenderableTransactionCargoIfc transCargo = (TenderableTransactionCargoIfc)bus.getCargo();
            transaction = transCargo.getTenderableTransaction();
        }

        if (transaction == null)
        {
            logger.debug("No transaction was found in cargo to check if it is suspended.");
            return false;
        }

        return transaction.isSuspended();
    }

}
