/* ===========================================================================
* Copyright (c) 1998, 2011, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/dailyoperations/till/tillloan/TillLoanCargo.java /rgbustores_13.4x_generic_branch/1 2011/05/05 14:06:17 mszekely Exp $
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
 *    3    360Commerce 1.2         3/31/2005 4:30:30 PM   Robert Pearse   
 *    2    360Commerce 1.1         3/10/2005 10:26:12 AM  Robert Pearse   
 *    1    360Commerce 1.0         2/11/2005 12:15:05 PM  Robert Pearse   
 *
 *   Revision 1.4  2004/04/30 18:16:03  dcobb
 *   @scr 4098 Open drawer before detail count screens.
 *   Loan changed to open drawer before detail count screens.
 *
 *   Revision 1.3  2004/02/12 16:49:59  mcs
 *   Forcing head revision
 *
 *   Revision 1.2  2004/02/11 21:46:54  rhafernik
 *   @scr 0 Log4J conversion and code cleanup
 *
 *   Revision 1.1.1.1  2004/02/11 01:04:15  cschellenger
 *   updating to pvcs 360store-current
 *
 *
 * 
 *    Rev 1.0   Aug 29 2003 15:57:50   CSchellenger
 * Initial revision.
 * 
 *    Rev 1.0   Apr 29 2002 15:28:12   msg
 * Initial revision.
 * 
 *    Rev 1.0   Mar 18 2002 11:29:16   msg
 * Initial revision.
 * 
 *    Rev 1.2   22 Jan 2002 12:22:44   baa
 * convert to new security model
 * Resolution for POS SCR-309: Convert to new Security Override design.
 *
 *    Rev 1.1   21 Nov 2001 14:27:32   epd
 * 1)  Creating txn at start of flow
 * 2)  Added new security access
 * 3)  Added cancel transaction site
 * Resolution for POS SCR-216: Making POS changes to accommodate OnlineOffice
 *
 *    Rev 1.0   Sep 21 2001 11:18:26   msg
 * Initial revision.
 *
 *    Rev 1.1   Sep 17 2001 13:14:30   msg
 * header update
 * ===========================================================================
 */
package oracle.retail.stores.pos.services.dailyoperations.till.tillloan;

// foundation imports
import oracle.retail.stores.domain.employee.RoleFunctionIfc;
import oracle.retail.stores.domain.financial.FinancialCountIfc;
import oracle.retail.stores.domain.financial.FinancialTotalsIfc;
import oracle.retail.stores.foundation.tour.application.tourcam.ObjectRestoreException;
import oracle.retail.stores.foundation.tour.application.tourcam.SnapshotIfc;
import oracle.retail.stores.foundation.tour.application.tourcam.TourCamSnapshot;
import oracle.retail.stores.pos.services.common.TillCargo;


//------------------------------------------------------------------------------
/**


    @version $Revision: /rgbustores_13.4x_generic_branch/1 $
**/
//------------------------------------------------------------------------------
public class TillLoanCargo extends TillCargo
{
    /**
       Loan count type.
    **/
    protected int loanCountType = FinancialCountIfc.COUNT_TYPE_NONE;

    /**
           Loan count received from counting service
        **/
    protected FinancialTotalsIfc loanTotals = null;

    //----------------------------------------------------------------------
    /**
        Returns the function ID whose access is to be checked.
        @return int Role Function ID
    **/
    //----------------------------------------------------------------------
    public int getAccessFunctionID()
    {
        return RoleFunctionIfc.TILL_PICKUP_LOAN;
    }

    //----------------------------------------------------------------------
    /**
        Returns the loan count type.
        <P>
        @return The loan count type.
    **/
    //----------------------------------------------------------------------
    public int getLoanCountType()
    {                                   // begin getLoanCountType()
        return loanCountType;
    }                                   // end getLoanCountType()

    //----------------------------------------------------------------------
    /**
        Sets the loan count type.
        <P>
        @param  value  The loan count type.
    **/
    //----------------------------------------------------------------------
    public void setLoanCountType(int value)
    {                                   // begin setLoanCountType()
        loanCountType = value;
    }                                   // end setLoanCountType()

    //----------------------------------------------------------------------
    /**
        Returns the loan financial totals.
        <P>
        @return The loan financial totals.
    **/
    //----------------------------------------------------------------------
        public FinancialTotalsIfc getLoanTotals()
    {
        return loanTotals;
    }

    //----------------------------------------------------------------------
    /**
        Sets the loan financial totals.
        <P>
        @param value  The loan financial totals
    **/
    //----------------------------------------------------------------------
    public void setLoanTotals(FinancialTotalsIfc value)
    {
        loanTotals = value;
    }

    //--------------------------------------------------------------------------
    /**
        Create a SnapshotIfc which can subsequently be used to restore
            the cargo to its current state. <P>
        <B>Pre-Condition(s)</B>
        <UL>
        <LI>The cargo is able to make a snapshot.
        </UL>
        <B>Post-Condition(s)</B>
        <UL>
        <LI>A snapshot is returned which contains enough data to restore the
            cargo to its current state.
        </UL>
        @return an object which stores the current state of the cargo.
        @see oracle.retail.stores.foundation.tour.application.tourcam.SnapshotIfc
    */
    //--------------------------------------------------------------------------
    public SnapshotIfc makeSnapshot()
    {
        return new TourCamSnapshot(this);
    }

    //--------------------------------------------------------------------------
    /**
        Reset the cargo data using the snapshot passed in. <P>
        <B>Pre-Condition(s)</B>
        <UL>
        <LI>The snapshot represents the state of the cargo, possibly relative
        to the existing state of the cargo.
        </UL>
        <B>Post-Condition(s)</B>
        <UL>
        <LI>The cargo state has been restored with the contents of the snapshot.
        </UL>
        @param snapshot is the SnapshotIfc which contains the desired state
            of the cargo.
        @exception ObjectRestoreException is thrown when the cargo cannot
            be restored with this snapshot
    */
    //--------------------------------------------------------------------------
    public void restoreSnapshot(SnapshotIfc snapshot)
        throws ObjectRestoreException
    {
    }
}
