/* ===========================================================================
* Copyright (c) 1998, 2011, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/dailyoperations/till/tillopen/TillOpenCargo.java /rgbustores_13.4x_generic_branch/1 2011/05/05 14:06:21 mszekely Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    cgreene   05/26/10 - convert to oracle packaging
 *    cgreene   02/15/10 - switch overridden getAccessFunctionID to setting the
 *                         function id in the constructor.
 *    abondala  01/03/10 - update header date
 *
 * ===========================================================================
 * $Log:
 *    3    360Commerce 1.2         3/31/2005 4:30:30 PM   Robert Pearse   
 *    2    360Commerce 1.1         3/10/2005 10:26:13 AM  Robert Pearse   
 *    1    360Commerce 1.0         2/11/2005 12:15:06 PM  Robert Pearse   
 *
 *   Revision 1.4  2004/07/07 18:27:11  dcobb
 *   @scr 1734 Wrong error message when attempt to open another till in reg acct.
 *   Fixed in CheckTillStatusSite. Moved deprecated TillOpenCargo to the deprecation tree and imported new TillCargo from _360commerce tree..
 *
 *   Revision 1.3  2004/02/12 16:48:04  mcs
 *   Forcing head revision
 *
 *   Revision 1.2  2004/02/11 21:29:48  rhafernik
 *   @scr 0 Log4J conversion and code cleanup
 *
 *   Revision 1.1.1.1  2004/02/11 01:04:11  cschellenger
 *   updating to pvcs 360store-current
 * 
 *   Rev 1.1   08 Nov 2003 01:11:16   baa
 *   cleanup -sale refactoring
 * ===========================================================================
 */
package oracle.retail.stores.pos.services.dailyoperations.till.tillopen;

import oracle.retail.stores.domain.employee.RoleFunctionIfc;
import oracle.retail.stores.domain.financial.FinancialCountIfc;
import oracle.retail.stores.domain.financial.FinancialTotalsIfc;
import oracle.retail.stores.foundation.tour.application.tourcam.ObjectRestoreException;
import oracle.retail.stores.foundation.tour.application.tourcam.SnapshotIfc;
import oracle.retail.stores.foundation.tour.application.tourcam.TourCamSnapshot;
import oracle.retail.stores.pos.services.common.TillCargo;

/**
 * @version $Revision: /rgbustores_13.4x_generic_branch/1 $
 */
public class TillOpenCargo extends TillCargo implements TillOpenCargoIfc
{
    private static final long serialVersionUID = 8458912437089883921L;

    /**
     * Float count type.
     */
    protected int floatCountType = FinancialCountIfc.COUNT_TYPE_NONE;

    /**
     * Float count received from counting service
     */
    protected FinancialTotalsIfc floatTotals = null;

    /**
     * Error screen name.
     */
    protected String errorScreenName = null;

    /**
     * Error screen args
     */
    protected String[] errorScreenArgs = null;

    /**
     * Till ID verified flag used for Signal
     */
    protected boolean tillIdVerified = true;

    /**
     * Default constructor. Sets the function id of the tour's cargo to
     * {@link RoleFunctionIfc#OPEN_TILL}.
     */
    public TillOpenCargo()
    {
        setAccessFunctionID(RoleFunctionIfc.OPEN_TILL);
    }

    /**
     * Returns till ID verified flag
     */
    public boolean isTillIdVerified()
    {
        return tillIdVerified;
    }

    /**
     * Sets till ID verified flag
     */
    public void setTillIdVerified(boolean value)
    {
        tillIdVerified = value;
    }

    /**
     * Returns the float count type.
     * 
     * @return The float count type.
     */
    public int getFloatCountType()
    {
        return floatCountType;
    }

    /**
     * Sets the float count type.
     * 
     * @param value The float count type.
     */
    public void setFloatCountType(int value)
    {
        floatCountType = value;
    }

    /**
     * Returns the float financial totals.
     * 
     * @return The float financial totals.
     */
    public FinancialTotalsIfc getFloatTotals()
    {
        return floatTotals;
    }

    /**
     * Sets the float financial totals.
     * 
     * @param value financial totals
     */
    public void setFloatTotals(FinancialTotalsIfc value)
    {
        floatTotals = value;
    }

    /**
     * Returns the current error screen name.
     * 
     * @return the current error screen name.
     */
    public String getErrorScreenName()
    {
        return (errorScreenName);
    }

    /**
     * Sets the current error screen name.
     * 
     * @param the current error screen name.
     */
    public void setErrorScreenName(String value)
    {
        errorScreenName = value;
    }

    /**
     * Returns the current error screen args.
     * 
     * @return the current error screen args.
     */
    public String[] getErrorScreenArgs()
    {
        return (errorScreenArgs);
    }

    /**
     * Sets the current error screen args.
     * 
     * @param the current error screen args.
     */
    public void setErrorScreenArgs(String[] args)
    {
        errorScreenArgs = args;
    }

    /**
     * Creates a SnapshotIfc which can subsequently be used to restore the cargo
     * to its current state.
     * <P>
     * <B>Pre-Condition(s)</B>
     * <UL>
     * <LI>The cargo is able to make a snapshot.
     * </UL>
     * <B>Post-Condition(s)</B>
     * <UL>
     * <LI>A snapshot is returned which contains enough data to restore the
     * cargo to its current state.
     * </UL>
     * 
     * @return an object which stores the current state of the cargo.
     * @see oracle.retail.stores.foundation.tour.application.tourcam.SnapshotIfc
     */
    public SnapshotIfc makeSnapshot()
    {
        return new TourCamSnapshot(this);
    }

    /**
     * Resets the cargo data using the snapshot passed in.
     * <P>
     * <B>Pre-Condition(s)</B>
     * <UL>
     * <LI>The snapshot represents the state of the cargo, possibly relative to
     * the existing state of the cargo.
     * </UL>
     * <B>Post-Condition(s)</B>
     * <UL>
     * <LI>The cargo state has been restored with the contents of the snapshot.
     * </UL>
     * 
     * @param snapshot is the SnapshotIfc which contains the desired state of
     *            the cargo.
     * @exception ObjectRestoreException is thrown when the cargo cannot be
     *                restored with this snapshot
     */
    public void restoreSnapshot(SnapshotIfc snapshot) throws ObjectRestoreException
    {
    }
}
