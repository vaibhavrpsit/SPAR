/* ===========================================================================
* Copyright (c) 1998, 2011, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/common/TillCargo.java /rgbustores_13.4x_generic_branch/1 2011/05/05 14:05:53 mszekely Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    cgreene   05/26/10 - convert to oracle packaging
 *    cgreene   04/27/10 - XbranchMerge cgreene_refactor-duplicate-pos-classes
 *                         from st_rgbustores_techissueseatel_generic_branch
 *    cgreene   02/12/10 - removed duplicate dataExceptionErrorCode that is
 *                         also in superclass
 *    abondala  01/03/10 - update header date
 *
 * ===========================================================================
 * $Log:
 *    3    360Commerce 1.2         3/31/2005 4:30:29 PM   Robert Pearse   
 *    2    360Commerce 1.1         3/10/2005 10:26:10 AM  Robert Pearse   
 *    1    360Commerce 1.0         2/11/2005 12:15:03 PM  Robert Pearse   
 *
 *   Revision 1.4  2004/04/15 18:57:01  dcobb
 *   @scr 4205 Feature Enhancement: Till Options
 *   Till reconcile service is now separate from till close.
 *
 *   Revision 1.3  2004/02/12 16:49:08  mcs
 *   Forcing head revision
 *
 *   Revision 1.2  2004/02/11 21:38:50  rhafernik
 *   @scr 0 Log4J conversion and code cleanup
 *
 *   Revision 1.1.1.1  2004/02/11 01:04:14  cschellenger
 *   updating to pvcs 360store-current
 *
 *
 * 
 *    Rev 1.2   Jan 21 2004 16:15:24   DCobb
 * Implemented TenderableTransactionCargoIfc.
 * Resolution for 3701: Timing problem can occur in CancelTransactionSite (multiple).
 * 
 *    Rev 1.1   Nov 10 2003 07:09:02   baa
 * move constants to TillCargoIfc
 * Resolution for 3430: Sale Service Refactoring
 * 
 *    Rev 1.0   Aug 29 2003 15:54:52   CSchellenger
 * Initial revision.
 * 
 *    Rev 1.3   Mar 11 2003 08:35:00   KLL
 * integrating Code Review results
 * Resolution for POS SCR-1959: Printing: Cancel Transactions
 *
 *    Rev 1.2   Feb 17 2003 15:43:26   DCobb
 * Added Register Open flow to Resume Till service.
 * Resolution for POS SCR-1867: POS 6.0 Floating Till
 *
 *    Rev 1.1   Jan 23 2003 16:59:28   KLL
 * Printing: cancel transactions: getParameterValue() mtd
 * Resolution for POS SCR-1959: Printing: Cancel Transactions
 *
 *    Rev 1.0   Apr 29 2002 15:35:20   msg
 * Initial revision.
 *
 *    Rev 1.2   25 Mar 2002 11:44:46   baa
 * split text for till drawer error
 * Resolution for POS SCR-1563: On longer dialog messages, the Enter button is not fully displayed.  Enter works.
 *
 *    Rev 1.1   Mar 18 2002 23:10:36   msg
 * - updated copyright
 *
 *    Rev 1.0   Mar 18 2002 11:23:28   msg
 * Initial revision.
 *
 *    Rev 1.2   29 Nov 2001 08:19:48   epd
 * No extends StoreStatusCargo
 * Resolution for POS SCR-216: Making POS changes to accommodate OnlineOffice
 *
 *    Rev 1.1   21 Nov 2001 14:24:50   epd
 * Removed redundant code and added transaction attribute
 * Resolution for POS SCR-216: Making POS changes to accommodate OnlineOffice
 *
 *    Rev 1.0   Sep 21 2001 11:14:16   msg
 * Initial revision.
 *
 *    Rev 1.1   Sep 17 2001 13:06:02   msg
 * header update
 * ===========================================================================
 */
package oracle.retail.stores.pos.services.common;

import oracle.retail.stores.pos.services.common.StoreStatusCargo;
import oracle.retail.stores.pos.services.common.TenderableTransactionCargoIfc;
import oracle.retail.stores.pos.services.common.TillCargoIfc;
import oracle.retail.stores.domain.employee.EmployeeIfc;
import oracle.retail.stores.domain.financial.TillIfc;
import oracle.retail.stores.domain.transaction.TenderableTransactionIfc;
import oracle.retail.stores.domain.transaction.TillAdjustmentTransactionIfc;
import oracle.retail.stores.foundation.tour.application.tourcam.ObjectRestoreException;
import oracle.retail.stores.foundation.tour.application.tourcam.SnapshotIfc;
import oracle.retail.stores.foundation.tour.application.tourcam.TourCamIfc;
import oracle.retail.stores.foundation.tour.application.tourcam.TourCamSnapshot;

/**
 * @version $Revision: /rgbustores_13.4x_generic_branch/1 $
 */
public class TillCargo extends StoreStatusCargo
    implements TourCamIfc, TillCargoIfc, TenderableTransactionCargoIfc
{
    private static final long serialVersionUID = 2478918843667245896L;
    /** revision number */
    public static final String revisionNumber = "$Revision: /rgbustores_13.4x_generic_branch/1 $";
    /** error types */
    public final static int TILL_NO_ERROR_TYPE              = 0;
    public final static int TILL_ID_INVALID_ERROR_TYPE      = 1;
    public final static int TILL_NOT_FOUND_ERROR_TYPE       = 2;
    public final static int TILL_DATABASE_ERROR_TYPE        = 3;
    public final static int TILL_SUSPENDED_ERROR_TYPE       = 4;
    public final static int TILL_ALREADY_RECONCILED_TYPE    = 5;
    public final static int TILL_ACCOUNTABILITY_ERROR_TYPE  = 6;
    public final static int TILL_CASHIER_ERROR_TYPE         = 7;
    public final static int TILL_DRAWER_ERROR_TYPE          = 8;
    public final static int TILL_REGISTER_CLOSED_ERROR_TYPE = 9;    
    public final static int TILL_NOT_SUSPENDED_ERROR_TYPE   = 10;
    public final static int TILL_NOT_FLOATING_ERROR_TYPE    = 11;
    public final static int TILL_NONE_OPEN_ERROR_TYPE       = 12;
    public final static int TILL_CLOSED_ERROR_TYPE          = 13;
    
    /** error indicator */
    protected int errorType = TILL_NO_ERROR_TYPE;

    /** error screen args */
    protected String[] errorScreenArgs = null;

    /**
     * Temporary holder for till being used
     */
    protected TillIfc till;

    /**
     * current till id
     */
    protected String tillID = null;

    /**
     * till error code
     */
    protected boolean tillFatalError = false;

    /**
     * false if no override is requested, true is override is needed
     */
    protected boolean securityOverrideFlag = false;

    /**
     * employee Granting Security override
     */
    protected EmployeeIfc securityOverrideEmployee;

    /**
     * employee attempting Security override
     */
    protected EmployeeIfc securityOverrideRequestEmployee;

    /**
     * Security override Return Letter
     */
    protected String securityOverrideReturnLetter;

    /**
     * The till adjustment transaction
     */
    protected TillAdjustmentTransactionIfc transaction = null;

    /**
     * Flag to indicate whether a warning message should be presented before
     * opening or resuming the till.
     */
    protected boolean showWarning = false;

    /**
     * Returns the temporary till.
     * 
     * @return The temporary till.
     */
    public TillIfc getTill()
    {
        return till;
    }

    /**
     * Sets the temporary till.
     * 
     * @param value The temporary till.
     */
    public void setTill(TillIfc value)
    {
        till = value;
    }

    /**
     * Returns the till id.
     * 
     * @return The till id.
     */
    public String getTillID()
    {
        return (tillID);
    }

    /**
     * Sets the till id.
     * 
     * @param value till id.
     */
    public void setTillID(String value)
    {
        tillID = value;
    }

    /**
     * Returns true if till fatal error.
     * 
     * @return true if error is fatal
     */
    public boolean isTillFatalError()
    {
        return tillFatalError;
    }

    /**
     * Sets the till fatal error code
     */
    public void setTillFatalError()
    {
        tillFatalError = true;
    }

    /**
     * Returns the transaction.
     * 
     * @return the transaction.
     */
    public TillAdjustmentTransactionIfc getTransaction()
    {
        return transaction;
    }

    /**
     * Sets the transaction.
     * 
     * @param vale the name of the transaction.
     */
    public void setTransaction(TillAdjustmentTransactionIfc value)
    {
        transaction = value;
    }

    /**
     * Retrieves the saved transaction
     * 
     * @return the TenderableTransactionIfc that is being printed
     */
    public TenderableTransactionIfc getTenderableTransaction()
    {
        return getTransaction();
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

    /**
     * Returns the securityOverrideFlag boolean.
     * 
     * @return The securityOverrideFlag boolean.
     */
    public boolean getSecurityOverrideFlag()
    {
        return securityOverrideFlag;
    }

    /**
     * Sets the securityOverrideFlag boolean.
     * 
     * @param value The ssecurityOverrideFlag boolean.
     */
    public void setSecurityOverrideFlag(boolean value)
    {
        securityOverrideFlag = value;
       
    }

    /**
     * Returns the securityOverrideEmployee object.
     * 
     * @return The securityOverrideEmployee object.
     */
    public EmployeeIfc getSecurityOverrideEmployee()
    {
        return securityOverrideEmployee;
    }

    /**
     * Sets the security override employee object.
     * 
     * @param value The security override employee object.
     */
    public void setSecurityOverrideEmployee(EmployeeIfc value)
    {
        securityOverrideEmployee = value;
    }

    /**
     * Returns the securityOverrideRequestEmployee object.
     * 
     * @return The securityOverrideRequestEmployee object.
     */
    public EmployeeIfc getSecurityOverrideRequestEmployee()
    {
        return securityOverrideRequestEmployee;
    }

    /**
     * Sets the securityOverrideRequestEmployee object.
     * 
     * @param value securityOverrideRequestEmployee object.
     */
    public void setSecurityOverrideRequestEmployee(EmployeeIfc value)
    {
        securityOverrideRequestEmployee = value;
    }

    /**
     * The securityOverrideReturnLetter returned by this cargo is to indecated
     * where the security override will return
     * 
     * @return the void
     */
    public void setSecurityOverrideReturnLetter(String value)
    {
        securityOverrideReturnLetter = value;
    }

    /**
     * The securityOverrideReturnLetter returned by this cargo is to indecated
     * where the security override will return
     * 
     * @return the String value
     */
    public String getSecurityOverrideReturnLetter()
    {
        return securityOverrideReturnLetter;
    }

    /**
     * Returns whether a warning should be displayed before opening or resuming
     * a till.
     * 
     * @return whether a warning should be displayed.
     */
    public boolean getShowWarning()
    {
        return showWarning;
    }

    /**
     * Sets whether a warning should be displayed before opening or resuming a
     * till.
     * 
     * @param value true, if a warning should be displayed, false otherwise
     */
    public void setShowWarning(boolean value)
    {
        showWarning = value;
    }

    /**
     * Retrieves error type
     * 
     * @return int error type (see TillResumeCargo)
     */
    public int getErrorType()
    {
        return errorType;
    }

    /**
     * Sets error type
     * 
     * @param value int error type (see TillReconcileCargo)
     */
    public void setErrorType(int value)
    {
        errorType = value;
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
     * @param args the current error screen args.
     */
    public void setErrorScreenArgs(String[] args)
    {
        errorScreenArgs = args;
    }
}
