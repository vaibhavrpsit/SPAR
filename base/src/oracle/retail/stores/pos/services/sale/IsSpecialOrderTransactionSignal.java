/* ===========================================================================
* Copyright (c) 2008, 2011, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/sale/IsSpecialOrderTransactionSignal.java /rgbustores_13.4x_generic_branch/1 2011/05/05 16:17:10 mszekely Exp $
 * ===========================================================================
 * NOTES
 *
 * MODIFIED    (MM/DD/YY)
 *    cgreene   05/26/10 - convert to oracle packaging
 *    cgreene   05/05/10 - remove deprecated log amanger and technician
 *    abondala  01/03/10 - update header date
 *    mchellap  09/30/08 - Added generated serialVersionUID
 *    mchellap  09/29/08 - QW-IIMO Updates for code review comments
 *    mchellap  09/19/08 - QW-IIMO
 *
 * ===========================================================================
 */
package oracle.retail.stores.pos.services.sale;

import org.apache.log4j.Logger;

import oracle.retail.stores.domain.transaction.OrderTransaction;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.foundation.tour.ifc.TrafficLightIfc;

/**
 * This signal checks to see if the SpecialOrder is cancelled.
 */
public class IsSpecialOrderTransactionSignal implements TrafficLightIfc
{
    static final long serialVersionUID = 2657612323986955398L;

    /**
     * The logger to which log messages will be sent.
     */
    protected static final Logger logger = Logger.getLogger(IsSpecialOrderTransactionSignal.class);

    /**
     * Checks to see if the till is suspended.
     * 
     * @return true if the till is suspended, false otherwise.
     */
    public boolean roadClear(BusIfc bus)
    {
        logger.info("IsSpecialOrderTransactionSignal.roadClear() - entry");

        boolean result = false;
        SaleCargoIfc cargo = (SaleCargoIfc) bus.getCargo();

        if (cargo.getTransaction() instanceof OrderTransaction)
        {
            result = true;
        }
        logger.debug("IsSpecialOrderTransactionSignal.roadClear() - exit");
        return (result);
    }

    /* (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString()
    {
        String strResult = new String("Class:  IsSpecialOrderTransactionSignal" + hashCode());
        return (strResult);
    }
}
