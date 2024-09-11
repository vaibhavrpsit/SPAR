/* ===========================================================================
* Copyright (c) 1998, 2011, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/dailyoperations/endofday/EndOfDayCargo.java /rgbustores_13.4x_generic_branch/1 2011/05/05 14:06:22 mszekely Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    acadar    10/27/10 - changes to reset external order status when
 *                         canceling suspended transactions
 *    cgreene   05/26/10 - convert to oracle packaging
 *    cgreene   04/27/10 - XbranchMerge cgreene_refactor-duplicate-pos-classes
 *                         from st_rgbustores_techissueseatel_generic_branch
 *    abondala  01/03/10 - update header date
 *
 * ===========================================================================
 * $Log:
 *    3    360Commerce 1.2         3/31/2005 4:28:00 PM   Robert Pearse
 *    2    360Commerce 1.1         3/10/2005 10:21:23 AM  Robert Pearse
 *    1    360Commerce 1.0         2/11/2005 12:10:53 PM  Robert Pearse
 *
 *   Revision 1.4  2004/09/27 22:32:05  bwf
 *   @scr 7244 Merged 2 versions of abstractfinancialcargo.
 *
 *   Revision 1.3  2004/02/12 16:49:37  mcs
 *   Forcing head revision
 *
 *   Revision 1.2  2004/02/11 21:46:17  rhafernik
 *   @scr 0 Log4J conversion and code cleanup
 *
 *   Revision 1.1.1.1  2004/02/11 01:04:15  cschellenger
 *   updating to pvcs 360store-current
 *
 *
 *
 *    Rev 1.0   Aug 29 2003 15:56:26   CSchellenger
 * Initial revision.
 *
 *    Rev 1.1   May 22 2003 17:07:46   jgs
 * Added transaction and accessors.
 * Resolution for 2543: Modify EJournal to put entries into a JMS Queue on the store server.
 *
 *    Rev 1.0   Apr 29 2002 15:31:08   msg
 * Initial revision.
 *
 *    Rev 1.1   Mar 18 2002 23:13:46   msg
 * - updated copyright
 *
 *    Rev 1.0   Mar 18 2002 11:26:34   msg
 * Initial revision.
 *
 *    Rev 1.2   12 Dec 2001 13:01:26   epd
 * added attributes for count type and financial totals
 * Resolution for POS SCR-216: Making POS changes to accommodate OnlineOffice
 *
 *    Rev 1.1   14 Nov 2001 11:51:26   epd
 * Added Security Access code and flow
 * Resolution for POS SCR-216: Making POS changes to accommodate OnlineOffice
 *
 *    Rev 1.0   Sep 21 2001 11:16:20   msg
 * Initial revision.
 *
 *    Rev 1.1   Sep 17 2001 13:07:22   msg
 * header update
 * ===========================================================================
 */
package oracle.retail.stores.pos.services.dailyoperations.endofday;

import oracle.retail.stores.pos.services.common.AbstractFinancialCargo;
import oracle.retail.stores.pos.services.common.DBErrorCargoIfc;
import oracle.retail.stores.domain.employee.RoleFunctionIfc;
import oracle.retail.stores.domain.financial.FinancialCountIfc;
import oracle.retail.stores.domain.financial.FinancialTotalsIfc;
import oracle.retail.stores.domain.transaction.TransactionIfc;
import oracle.retail.stores.domain.transaction.TransactionSummaryIfc;
import oracle.retail.stores.pos.reports.RegisterReport;
import oracle.retail.stores.pos.services.manager.registerreports.RegisterReportsCargoIfc;

//------------------------------------------------------------------------------
/**
 * This cargo holds the information necessary to Daily Operations service.
 * <P>
 *
 * @version $Revision: /rgbustores_13.4x_generic_branch/1 $
 **/
// ------------------------------------------------------------------------------
public class EndOfDayCargo extends AbstractFinancialCargo implements RegisterReportsCargoIfc, DBErrorCargoIfc
{ // begin class EndOfDayCargo
    /**
     *
     */
    private static final long serialVersionUID = 1L;

    /**
     * revision number supplied by Team Connection
     **/
    public static final String revisionNumber = "$Revision: /rgbustores_13.4x_generic_branch/1 $";

    /**
     * commit flag
     **/
    protected boolean commitFlag = false;

    /**
     * Holds store totals
     **/
    protected FinancialTotalsIfc storeTotals = null;

    /**
     * current report type
     **/
    protected int currentReportType = RegisterReportsCargoIfc.REPORT_UNDEFINED;

    /**
     * register report reference
     **/
    protected RegisterReport report = null;

    /**
     * Count type for counting the safe
     **/
    protected int safeCountType = FinancialCountIfc.COUNT_TYPE_SUMMARY;

    /**
     * Safe count received from counting service
     **/
    protected FinancialTotalsIfc safeTotals = null;

    /**
     * Contains transaction info; saved for journaling.
     **/
    protected TransactionIfc transaction = null;

    /**
     * Contains a list of suspended transaction summaries
     *
     */
    protected TransactionSummaryIfc[] suspendedTransactionList;

    // ----------------------------------------------------------------------------
    /**
     * Retrieves default business date.
     * <P>
     *
     * @return default business date
     **/
    // ----------------------------------------------------------------------------
    public boolean isCommitFlag()
    { // begin isCommitFlag()
        return (commitFlag);
    } // end isCommitFlag()

    // ----------------------------------------------------------------------------
    /**
     * Sets default business date.
     * <P>
     *
     * @param value default business date
     **/
    // ----------------------------------------------------------------------------
    public void setCommitFlag(boolean value)
    { // begin setCommitFlag()
        commitFlag = value;
    } // end setCommitFlag()

    // ----------------------------------------------------------------------------
    /**
     * Gets store totals.
     * <P>
     *
     * @return store totals
     **/
    // ----------------------------------------------------------------------------
    public FinancialTotalsIfc getStoreTotals()
    { // begin isCommitFlag()
        return (storeTotals);
    } // end isCommitFlag()

    // ----------------------------------------------------------------------------
    /**
     * Sets store totals.
     * <P>
     *
     * @param value store totals
     **/
    // ----------------------------------------------------------------------------
    public void setStoreTotals(FinancialTotalsIfc value)
    { // begin setCommitFlag()
        storeTotals = value;
    } // end setCommitFlag()

    // --------------------------------------------------------------------------
    /**
     * Set the current report type
     * <P>
     *
     * @param type int value (ex: REPORT_SUMMARY, REPORT_DEPTSALES)
     **/
    // --------------------------------------------------------------------------
    public void setReportType(int value)
    {
        currentReportType = value;
    }

    // --------------------------------------------------------------------------
    /**
     * Get the current report type
     * <P>
     *
     * @return int type value (ex: REPORT_UNDEFINED, REPORT_SUMMARY)
     **/
    // --------------------------------------------------------------------------
    public int getReportType()
    {
        return (currentReportType);
    }

    // --------------------------------------------------------------------------
    /**
     * Set the report
     * <P>
     *
     * @param rr RegisterReport object
     */
    // --------------------------------------------------------------------------
    public void setReport(RegisterReport value)
    {
        report = value;
    }

    // --------------------------------------------------------------------------
    /**
     * Get the current report
     * <P>
     *
     * @return RegisterReport value
     */
    // --------------------------------------------------------------------------
    public RegisterReport getReport()
    {
        return report;
    }

    // ----------------------------------------------------------------------
    /**
     * Returns the function ID whose access is to be checked.
     *
     * @return int function ID
     **/
    // ----------------------------------------------------------------------
    public int getAccessFunctionID()
    {
        return RoleFunctionIfc.END_OF_DAY;
    }

    // ----------------------------------------------------------------------
    /**
     * Returns the safe count type.
     * <P>
     *
     * @return The safe count type.
     **/

    // ----------------------------------------------------------------------
    public int getSafeCountType()
    { // begin getSafeCountType()
        return safeCountType;
    } // end getSafeCountType()

    // ----------------------------------------------------------------------
    /**
     * Sets the safe count type.
     * <P>
     *
     * @param value The safe count type.
     **/
    // ----------------------------------------------------------------------
    public void setSafeCountType(int value)
    { // begin setSafeCountType()
        safeCountType = value;
    } // end setSafeCountType()

    // ----------------------------------------------------------------------
    /**
     * Returns the safe financial totals.
     * <P>
     *
     * @return The safe financial totals.
     **/
    // ----------------------------------------------------------------------
    public FinancialTotalsIfc getSafeTotals()
    {
        return safeTotals;
    }

    // ----------------------------------------------------------------------
    /**
     * Sets the safe financial totals.
     * <P>
     *
     * @param value financial totals
     **/
    // ----------------------------------------------------------------------
    public void setSafeTotals(FinancialTotalsIfc value)
    {
        safeTotals = value;
    }

    // ----------------------------------------------------------------------
    /**
     * Returns the transaction.
     *
     * @return TransactionIfc
     */
    // ----------------------------------------------------------------------
    public TransactionIfc getTransaction()
    {
        return transaction;
    }

    // ----------------------------------------------------------------------
    /**
     * Sets the transaction.
     *
     * @param transaction The transaction to set
     */
    // ----------------------------------------------------------------------
    public void setTransaction(TransactionIfc transaction)
    {
        this.transaction = transaction;
    }

    // ----------------------------------------------------------------------
    /**
     * Returns a string representation of this object.
     * <P>
     *
     * @return String representation of object
     **/
    // ----------------------------------------------------------------------
    public String toString()
    { // begin toString()
        // result string
        String strResult = new String("Class:  EndOfDayCargo (Revision " + getRevisionNumber() + ") @" + hashCode());
        strResult += "\n" + "commit flag:                                [" + commitFlag + "]\n" + abstractToString();
        // pass back result
        return (strResult);
    } // end toString()

    // ----------------------------------------------------------------------
    /**
     * Returns the revision number of the class.
     * <P>
     *
     * @return String representation of revision number
     **/
    // ----------------------------------------------------------------------
    public String getRevisionNumber()
    { // begin getRevisionNumber()
        // return string
        return (revisionNumber);
    } // end getRevisionNumber()

    /**
     * @return the suspendedTransactionList
     */
    public TransactionSummaryIfc[] getSuspendedTransactionList()
    {
        return suspendedTransactionList;
    }

    /**
     * @param suspendedTransactionList the suspendedTransactionList to set
     */
    public void setSuspendedTransactionList(TransactionSummaryIfc[] suspendedTransactionList)
    {
        this.suspendedTransactionList = suspendedTransactionList;
    }

} // end class EndOfDayCargo
