/* ===========================================================================
* Copyright (c) 1998, 2012, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/dailyoperations/till/tillclose/TillCloseCargo.java /main/13 2012/09/12 11:57:09 blarsen Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    blarsen   03/20/12 - Added alwaysPromptForTillId flag. EnterTillSite
 *                         checks this flag. MPOS sets this flag to false. For
 *                         MPOS if there is a single open till, it will be
 *                         closed w/o prompting operator.
 *    cgreene   05/26/10 - convert to oracle packaging
 *    cgreene   04/27/10 - XbranchMerge cgreene_refactor-duplicate-pos-classes
 *                         from st_rgbustores_techissueseatel_generic_branch
 *    abondala  01/03/10 - update header date
 *
 * ===========================================================================
 * $Log:
 *    3    360Commerce 1.2         3/31/2005 4:30:29 PM   Robert Pearse
 *    2    360Commerce 1.1         3/10/2005 10:26:10 AM  Robert Pearse
 *    1    360Commerce 1.0         2/11/2005 12:15:03 PM  Robert Pearse
 *
 *   Revision 1.4  2004/06/30 00:21:24  dcobb
 *   @scr 5165 - Allowed to reconcile till when database is offline.
 *   @scr 5167 - Till Close and Till Reconcile will both be journaled.
 *
 *   Revision 1.3  2004/02/12 16:49:57  mcs
 *   Forcing head revision
 *
 *   Revision 1.2  2004/02/11 21:47:18  rhafernik
 *   @scr 0 Log4J conversion and code cleanup
 *
 *   Revision 1.1.1.1  2004/02/11 01:04:15  cschellenger
 *   updating to pvcs 360store-current
 *
 *
 *
 *    Rev 1.0   Aug 29 2003 15:57:42   CSchellenger
 * Initial revision.
 *
 *    Rev 1.0   Apr 29 2002 15:29:00   msg
 * Initial revision.
 *
 *    Rev 1.0   Mar 18 2002 11:28:58   msg
 * Initial revision.
 *
 *    Rev 1.3   22 Jan 2002 15:54:52   baa
 * convert to new security model, Role/Security updates
 * Resolution for POS SCR-309: Convert to new Security Override design.
 * Resolution for POS SCR-714: Roles/Security 5.0 Updates
 *
 *    Rev 1.2   09 Jan 2002 10:30:48   epd
 * added till reconiled flag (versus closed)
 * Resolution for POS SCR-216: Making POS changes to accommodate OnlineOffice
 *
 *    Rev 1.1   14 Nov 2001 11:51:32   epd
 * Added Security Access code and flow
 * Resolution for POS SCR-216: Making POS changes to accommodate OnlineOffice
 *
 *    Rev 1.0   Sep 21 2001 11:18:18   msg
 * Initial revision.
 *
 *    Rev 1.1   Sep 17 2001 13:14:18   msg
 * header update
 * ===========================================================================
 */
package oracle.retail.stores.pos.services.dailyoperations.till.tillclose;

// foundation imports
import oracle.retail.stores.domain.employee.RoleFunctionIfc;
import oracle.retail.stores.domain.financial.FinancialCountIfc;
import oracle.retail.stores.domain.financial.FinancialTotalsIfc;
import oracle.retail.stores.domain.financial.RegisterIfc;
import oracle.retail.stores.foundation.tour.application.tourcam.ObjectRestoreException;
import oracle.retail.stores.foundation.tour.application.tourcam.SnapshotIfc;
import oracle.retail.stores.foundation.tour.application.tourcam.TourCamSnapshot;
import oracle.retail.stores.pos.services.common.DBErrorCargoIfc;
import oracle.retail.stores.pos.services.common.TillCargo;

//------------------------------------------------------------------------------
/**
 * Till Close Cargo
 *
 * @version $Revision: /main/13 $
 **/
// ------------------------------------------------------------------------------
@SuppressWarnings("serial")
public class TillCloseCargo extends TillCargo implements DBErrorCargoIfc
{
    /**
     * Float count type.
     **/
    protected int floatCountType = FinancialCountIfc.COUNT_TYPE_NONE;

    /**
     * Till count type.
     **/
    protected int tillCountType = FinancialCountIfc.COUNT_TYPE_NONE;

    /**
     * Float count received from counting service
     **/
    protected FinancialTotalsIfc floatTotals = null;

    /**
     * Till count received from counting service
     **/
    protected FinancialTotalsIfc tillTotals = null;

    /**
     * localRegister is a clone of Register in case the user Cancels during
     * float/till counting
     **/
    protected RegisterIfc localRegister = null;

    /**
     * Flag which tells us whether the till was reconciled (used in
     * UpdateStatusSite.java)
     **/
    protected boolean tillReconciled = false;

    /**
     * Indicates whether Till Close or Till Reconcile was the requested service.
     */
    protected int requestedService = RoleFunctionIfc.FUNCTION_UNDEFINED;

    /**
     * Always prompt for till id.
     *
     * Prompt for till id even if there is only one till to close.
     *
     * This flag is useful for MPOS since it is not till aware.  Tills are
     * automatically opened for MPOS and when a till is closed, no prompting is
     * performed (the one open till is simply closed).
     */
    protected boolean alwaysPromptForTillId = true;

    /**
     * Returns the float count type.
     * <P>
     *
     * @return The float count type.
     **/

    public int getFloatCountType()
    { // begin getFloatCountType()
        return floatCountType;
    } // end getFloatCountType()

    /**
     * Sets the float count type.
     * <P>
     *
     * @param value The float count type.
     **/
    public void setFloatCountType(int value)
    { // begin setFloatCountType()
        floatCountType = value;
    } // end setFloatCountType()

    /**
     * Returns the till count type.
     * <P>
     *
     * @return The till count type.
     **/

    public int getTillCountType()
    { // begin getTillCountType()
        return tillCountType;
    } // end getTillCountType()

    /**
     * Sets the till count type.
     * <P>
     *
     * @param value The till count type.
     **/
    public void setTillCountType(int value)
    { // begin setTillCountType()
        tillCountType = value;
    } // end setTillCountType()

    /**
     * Returns the float financial totals.
     * <P>
     *
     * @return The float financial totals.
     **/
    public FinancialTotalsIfc getFloatTotals()
    {
        return floatTotals;
    }

    /**
     * Sets the float financial totals.
     * <P>
     *
     * @param float financial totals
     **/
    public void setFloatTotals(FinancialTotalsIfc value)
    {
        floatTotals = value;
    }

    /**
     * Returns the till financial totals.
     * <P>
     *
     * @return The till financial totals.
     **/
    public FinancialTotalsIfc getTillTotals()
    {
        return tillTotals;
    }

    /**
     * Sets the till financial totals.
     * <P>
     *
     * @param till financial totals
     **/
    public void setTillTotals(FinancialTotalsIfc value)
    {
        tillTotals = value;
    }

    /**
     * Determines if we need to clone the Register due to user cancelling the
     * float/till counts.
     * <P>
     *
     * @return The localRegister clone of parent Register
     **/
    public RegisterIfc getRegister()
    {
        if (localRegister == null)
        {
            localRegister = (RegisterIfc) super.getRegister().clone();
        }

        return (localRegister);
    }

    /**
     * Sets till reconciled flag
     **/
    public void setTillReconciledFlag(boolean value)
    {
        tillReconciled = value;
    }

    /**
     * Returns till reconciled flag
     **/
    public boolean getTillReconciledFlag()
    {
        return tillReconciled;
    }

    /**
     * Sets the requested service indicator.
     *
     * @param value int indicator of requested service (@see RoleFunctionIfc)
     **/
    public void setRequestedService(int value)
    {
        requestedService = value;
    }

    /**
     * Returns the requested service indicator
     *
     * @return requested service indicator (@see RoleFunctionIfc)
     **/
    public int getRequestedService()
    {
        return requestedService;
    }

    /**
     * Sets the alwaysPromptForTillId flag
     *
     * @param alwaysPromptForTillId
     **/
    public void setAlwaysPromptForTillId(boolean alwaysPromptForTillId)
    {
        this.alwaysPromptForTillId = alwaysPromptForTillId;
    }

    /**
     * Returns the alwaysPromptForTillId flag
     *
     * @return alwaysPromptForTillId flag
     **/
    public boolean alwaysPromptForTillId()
    {
        return alwaysPromptForTillId;
    }

    /**
     * Create a SnapshotIfc which can subsequently be used to restore the cargo
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
     * Reset the cargo data using the snapshot passed in.
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
