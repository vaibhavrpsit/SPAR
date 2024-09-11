/* ===========================================================================
* Copyright (c) 2004, 2011, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/returns/returnfindtrans/TransReentryLight.java /rgbustores_13.4x_generic_branch/1 2011/05/05 14:05:55 mszekely Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    cgreene   05/26/10 - convert to oracle packaging
 *
 * ===========================================================================
 */
package oracle.retail.stores.pos.services.returns.returnfindtrans;

import org.apache.log4j.Logger;

import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.foundation.tour.ifc.TrafficLightIfc;
//import oracle.retail.stores.pos.services.instantcredit.InstantCreditCargo;

//------------------------------------------------------------------------------
/**
 * This signal is green if Transaction Reentry is on.
 */
//------------------------------------------------------------------------------

public class TransReentryLight implements TrafficLightIfc
{
    // This id is used to tell
    // the compiler not to generate a
    // new serialVersionUID.
    //
    static final long serialVersionUID = 5299844046882035901L;

    /**
     * The logger to which log messages will be sent.
     */
    protected static Logger logger =
        Logger.getLogger(oracle.retail.stores.pos.services.returns.returnfindtrans.TransReentryLight.class);

    public static final String SIGNALNAME = "TransReentryLight";

    //--------------------------------------------------------------------------
    /**
     * roadClear determines whether it is safe for the bus to proceed
     * 
     * @param bus
     *            the bus trying to proceed
     * @return true if we are in transaction reentry mode; false otherwise
     */
    //--------------------------------------------------------------------------

    public boolean roadClear(BusIfc bus)
    {
        ReturnFindTransCargo cargo = (ReturnFindTransCargo) bus.getCargo();
        boolean transReentryMode = cargo.getRegister().getWorkstation().isTransReentryMode();
        return transReentryMode;
    }
}
