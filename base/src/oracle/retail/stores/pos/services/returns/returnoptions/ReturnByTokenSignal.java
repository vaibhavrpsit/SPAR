/* ===========================================================================
* Copyright (c) 2011, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/returns/returnoptions/ReturnByTokenSignal.java /rgbustores_13.4x_generic_branch/1 2011/07/15 10:58:28 jswan Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *
 * ===========================================================================
 */
package oracle.retail.stores.pos.services.returns.returnoptions;

import org.apache.log4j.Logger;

import oracle.retail.stores.foundation.tour.gate.Gateway;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.foundation.tour.ifc.TrafficLightIfc;


//------------------------------------------------------------------------------
/**
 * This signal returns true if the configuration setting for return by account
 * number token is true.
 */
//------------------------------------------------------------------------------

public class ReturnByTokenSignal implements TrafficLightIfc
{
    /**
     * serialVersionUID
     */
    private static final long serialVersionUID = 7202977458343480156L;

    /**
     * The logger to which log messages will be sent.
     */
    protected static Logger logger =
        Logger.getLogger(oracle.retail.stores.pos.services.returns.returnfindtrans.TransReentryLight.class);

    public static final String SIGNALNAME = "ReturnByTokenSignal";

    //--------------------------------------------------------------------------
    /**
     * This method returns true if the configuration setting for return by account
     * number token is true.
     * 
     * @param bus
     *            the bus trying to proceed
     * @return true if the configuration setting for return by account number token is true.
     */
    //--------------------------------------------------------------------------

    public boolean roadClear(BusIfc bus)
    {
        String sigCapEnabled = Gateway.getProperty("application", "ReturnByAccountNumberToken", "true");
        return Boolean.valueOf(sigCapEnabled).booleanValue();
    }
}
