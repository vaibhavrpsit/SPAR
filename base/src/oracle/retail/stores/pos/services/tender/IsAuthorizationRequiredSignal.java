/* ===========================================================================
* Copyright (c) 1998, 2011, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/tender/IsAuthorizationRequiredSignal.java /rgbustores_13.4x_generic_branch/2 2011/07/12 15:58:33 cgreene Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    cgreene   07/12/11 - update generics
 *    ohorne    04/28/11 - created class
 *
 * ===========================================================================
 */
package oracle.retail.stores.pos.services.tender;

import java.util.HashMap;

import org.apache.log4j.Logger;

import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.foundation.tour.ifc.TrafficLightIfc;
import oracle.retail.stores.pos.ado.tender.TenderConstants;

/**
 * Checks the AUTH_REQUIRED value of cargo's tenderAttributes map
 * to determine if tender Authorization is required
 * @see TenderConstants.AUTH_REQUIRED
 *
 */
public class IsAuthorizationRequiredSignal implements TrafficLightIfc
{
    private static final long serialVersionUID = -3479056293708703486L;
    
    /**
     * The logger to which log messages will be sent.
     */
    protected static final Logger logger = Logger.getLogger(IsAuthorizationRequiredSignal.class);

    /**
     * Checks to see if the final result has sent to RM.
     * 
     * @return true if the final result has sent to RM, false otherwise.
     */
    public boolean roadClear(BusIfc bus)
    {
        logger.debug("IsAuthorizationRequiredSignal.roadClear() - entry");
        boolean result = false;
        TenderCargo cargo = (TenderCargo) bus.getCargo();
        HashMap<String,Object> tenderAttributes = cargo.getTenderAttributes();
        
        if (tenderAttributes != null && TenderConstants.TRUE.equals(tenderAttributes.get(TenderConstants.AUTH_REQUIRED)))
        {
            result = true;
        }
        logger.debug("IsAuthorizationRequiredSignal.roadClear() - exit");
        return (result);
    }
}
