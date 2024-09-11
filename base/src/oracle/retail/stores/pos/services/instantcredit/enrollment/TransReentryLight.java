/* ===========================================================================
* Copyright (c) 2004, 2011, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/instantcredit/enrollment/TransReentryLight.java /rgbustores_13.4x_generic_branch/1 2011/05/05 14:06:14 mszekely Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    cgreene   05/26/10 - convert to oracle packaging
 *    abondala  01/03/10 - update header date
 *
 * ===========================================================================
 * $Log:
 *  3    360Commerce 1.2         3/31/2005 4:30:37 PM   Robert Pearse   
 *  2    360Commerce 1.1         3/10/2005 10:26:28 AM  Robert Pearse   
 *  1    360Commerce 1.0         2/11/2005 12:15:19 PM  Robert Pearse   
 * $
 * Revision 1.5  2004/09/23 00:07:10  kmcbride
 * @scr 7211: Adding static serialVersionUIDs to all POS Serializable objects, minus the JComponents
 *
 * Revision 1.4  2004/03/26 21:18:20  cdb
 * @scr 4204 Removing Tabs.
 *
 * Revision 1.3  2004/03/26 15:56:29  bjosserand
 * @scr 4093 Transaction Reentry
 *
 * 
 * Revision 1.2  2004/03/25 23:42:10  bjosserand
 * @scr 4093 Transaction Reentry
 *
 * Revision 1.1  2004/03/25 20:01:55  bjosserand
 * @scr 4093 Transaction Reentry
 *
 * ===========================================================================
 */
package oracle.retail.stores.pos.services.instantcredit.enrollment;

import org.apache.log4j.Logger;

import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.foundation.tour.ifc.TrafficLightIfc;
import oracle.retail.stores.pos.services.instantcredit.InstantCreditCargo;

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
    static final long serialVersionUID = -5611867054048838882L;

    /**
     * The logger to which log messages will be sent.
     */
    protected static Logger logger =
        Logger.getLogger(oracle.retail.stores.pos.services.instantcredit.enrollment.TransReentryLight.class);

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
        InstantCreditCargo cargo = (InstantCreditCargo) bus.getCargo();
        boolean transReentryMode = cargo.getRegister().getWorkstation().isTransReentryMode();
        return transReentryMode;
    }
}
