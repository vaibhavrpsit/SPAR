/* ===========================================================================
* Copyright (c) 2011, 2014, Oracle and/or its affiliates. All rights reserved.
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/ui/timer/TimeoutSettingsUtility.java /main/2 2014/07/08 11:41:53 cgreene Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    cgreene   07/08/14 - refactor default timer model to default to 15
 *                         minutes timeout and be able to find parametermanager
 *                         from dispatcher
 *    cgreene   08/16/11 - initial version
 *
 * ===========================================================================
 */
package oracle.retail.stores.pos.ui.timer;

import oracle.retail.stores.pos.services.common.CommonLetterIfc;

/**
 * Utility to contain specific global options for what should happen when the
 * current timer expires.
 * <P>
 * Note: This utility is a workaround that done before Echo/MPOS that allowed
 * for the creation of {@link DefaultTimerModel}s outside of the tour framework
 * and have if be able to pick the correct timeout parameter. This utility does
 * not work in a multi-threaded environment like MPOS, but since MPOS's client
 * does not timeout in the middle of a tour, it should be okay to leave as-is.
 *
 * @author cgreene
 * @since 13.4
 */
public final class TimeoutSettingsUtility
{

    /**
     * If <code>true</code>, indicates the timer should consider a sensitive
     * operation is in effect, such as ringing a transaction or traversing the
     * Admin menus and mail an {@link CommonLetterIfc#TIMEOUT} letter.
     * <p>
     * If <code>false</code>, the timer should mail an
     * {@link CommonLetterIfc#UNDO} letter to cause the bus to go back one site.
     */
    private static boolean transactionActive;

    /**
     * Hidden constructor.
     */
    private TimeoutSettingsUtility()
    {
    }

    /**
     * @return the transactionActive
     */
    public static boolean isTransactionActive()
    {
        return transactionActive;
    }

    /**
     * @param transactionActive the transactionActive to set
     */
    public static void setTransactionActive(boolean transactionActive)
    {
        TimeoutSettingsUtility.transactionActive = transactionActive;
    }
    
}
