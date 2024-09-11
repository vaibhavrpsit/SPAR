/* ===========================================================================
* Copyright (c) 1998, 2011, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/tender/MAXIsCashRefundSignal.java /rgbustores_13.4x_generic_branch/2 2011/07/12 15:58:33 cgreene Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    cgreene   01/12/17 - Initial Draft : Cash Refund
 *
 * ===========================================================================
 */
package max.retail.stores.pos.services.tender;

import java.util.HashMap;

import org.apache.log4j.Logger;

import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.foundation.tour.ifc.TrafficLightIfc;
import oracle.retail.stores.pos.ado.tender.TenderConstants;

public class MAXIsCashRefundSignal implements TrafficLightIfc
{
    
    /**
     * The logger to which log messages will be sent.
     */
    protected static final Logger logger = Logger.getLogger(MAXIsCashRefundSignal.class);

    /**
     * Checks to see if the final result has sent to RM.
     * 
     * @return true if the final result has sent to RM, false otherwise.
     */
    public boolean roadClear(BusIfc bus)
    {
        logger.debug("MAXIsCashRefundSignal.roadClear() - entry");
        boolean result = false;
        MAXTenderCargo cargo = (MAXTenderCargo) bus.getCargo();        
        if (cargo.getAccessFunctionID() == 802)
        {
            result = true;
        }
        logger.debug("MAXIsCashRefundSignal.roadClear() - exit");
        return (result);
    }
}
