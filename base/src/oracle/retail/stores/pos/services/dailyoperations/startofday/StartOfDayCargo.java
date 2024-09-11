/* ===========================================================================
* Copyright (c) 1998, 2014, Oracle and/or its affiliates. All rights reserved.
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/dailyoperations/startofday/StartOfDayCargo.java /main/11 2014/07/23 15:44:29 rhaight Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    rhaight   07/23/14 - Code review updates
 *    rhaight   07/03/14 - store offline open revisions
 *    cgreene   05/26/10 - convert to oracle packaging
 *    abondala  01/03/10 - update header date
 *
 * ===========================================================================
 * $Log:
 *    3    360Commerce 1.2         3/31/2005 4:30:09 PM   Robert Pearse   
 *    2    360Commerce 1.1         3/10/2005 10:25:27 AM  Robert Pearse   
 *    1    360Commerce 1.0         2/11/2005 12:14:23 PM  Robert Pearse   
 *
 *   Revision 1.4  2004/09/27 22:32:04  bwf
 *   @scr 7244 Merged 2 versions of abstractfinancialcargo.
 *
 *   Revision 1.3  2004/02/12 16:49:53  mcs
 *   Forcing head revision
 *
 *   Revision 1.2  2004/02/11 21:46:26  rhafernik
 *   @scr 0 Log4J conversion and code cleanup
 *
 *   Revision 1.1.1.1  2004/02/11 01:04:15  cschellenger
 *   updating to pvcs 360store-current
 *
 *
 * 
 *    Rev 1.0   Aug 29 2003 15:57:26   CSchellenger
 * Initial revision.
 * 
 *    Rev 1.1   May 23 2003 06:53:40   jgs
 * Modified to delay the end of transaction journal entry.
 * Resolution for 2543: Modify EJournal to put entries into a JMS Queue on the store server.
 * 
 *    Rev 1.0   Apr 29 2002 15:29:26   msg
 * Initial revision.
 * 
 *    Rev 1.1   Mar 18 2002 23:16:00   msg
 * - updated copyright
 * 
 *    Rev 1.0   Mar 18 2002 11:28:18   msg
 * Initial revision.
 * 
 *    Rev 1.2   12 Dec 2001 13:03:46   epd
 * Added attributes to hold Store Safe count type and financial totals
 * Resolution for POS SCR-216: Making POS changes to accommodate OnlineOffice
 * 
 *    Rev 1.1   14 Nov 2001 11:51:30   epd
 * Added Security Access code and flow
 * Resolution for POS SCR-216: Making POS changes to accommodate OnlineOffice
 * 
 *    Rev 1.0   Sep 21 2001 11:16:38   msg
 * Initial revision.
 * 
 *    Rev 1.1   Sep 17 2001 13:07:28   msg
 * header update
 * ===========================================================================
 */
package oracle.retail.stores.pos.services.dailyoperations.startofday;

// foundation imports
import oracle.retail.stores.pos.services.common.AbstractFinancialCargo;
import oracle.retail.stores.domain.employee.RoleFunctionIfc;
import oracle.retail.stores.domain.financial.FinancialCountIfc;
import oracle.retail.stores.domain.financial.FinancialTotalsIfc;
import oracle.retail.stores.domain.transaction.StoreOpenCloseTransactionIfc;
import oracle.retail.stores.domain.transaction.TransactionIfc;
import oracle.retail.stores.domain.utility.EYSDate;

//--------------------------------------------------------------------------
/**
    This cargo holds the information necessary to Start of Day service.
    <P>
    @version $Revision: /main/11 $
**/
//--------------------------------------------------------------------------
public class StartOfDayCargo extends AbstractFinancialCargo
{                                       // begin class StartOfDayCargo
    /** Serial Version ID */
	private static final long serialVersionUID = 5027245827196608237L;
	
	/**
        revision number
    **/
    public static final String revisionNumber = "$Revision: /main/11 $";
    /**
        input business date
    **/
    protected EYSDate inputBusinessDate = null;
    /**
        commit flag
    **/
    protected boolean commitFlag = false;

    /**
        Flag to indicate whether a warning message should be presented
        before opening.
    **/
    protected boolean showWarning = false;

    /**
        Count type for counting the safe
    **/
    protected int safeCountType = FinancialCountIfc.COUNT_TYPE_SUMMARY;

    /**
       Safe count received from counting service
    **/
    protected FinancialTotalsIfc safeTotals = null;
    
    /**
       Contains transaction info; saved for journaling.
    **/
    protected TransactionIfc transaction = null;
    
    /** 
     * @since 14.1
     * 
     * Store Open Mode */
    protected int storeOpenMode = StoreOpenCloseTransactionIfc.STORE_OPEN_MODE_ONLINE;
    
    

    //----------------------------------------------------------------------------
    /**
        Retrieves input business date. <P>
        @return input business date
    **/
    //----------------------------------------------------------------------------
    public EYSDate getInputBusinessDate()
    {
        return(inputBusinessDate);
    }

    //----------------------------------------------------------------------------
    /**
        Sets input business date. <P>
        @param value  input business date
    **/
    //----------------------------------------------------------------------------
    public void setInputBusinessDate(EYSDate value)
    {
        inputBusinessDate = value;
    }

    //----------------------------------------------------------------------------
    /**
        Retrieves default business date. <P>
        @return default business date
    **/
    //----------------------------------------------------------------------------
    public boolean isCommitFlag()
    {
        return(commitFlag);
    }

    //----------------------------------------------------------------------------
    /**
        Sets default business date. <P>
        @param value  default business date
    **/
    //----------------------------------------------------------------------------
    public void setCommitFlag(boolean value)
    {
        commitFlag = value;
    }

    //----------------------------------------------------------------------
    /**
        Returns whether a warning should be displayed before opening. <P>
        @return whether a warning should be displayed before opening.
    **/
    //----------------------------------------------------------------------
    public boolean getShowWarning()
    {
        return showWarning;
    }

    //----------------------------------------------------------------------
    /**
        Sets whether a warning should be displayed before opening. <P>
        @param value true, if a warning should be displayed, false otherwise
    **/
    //----------------------------------------------------------------------
    public void setShowWarning(boolean value)
    {
        showWarning = value;
    }

    //----------------------------------------------------------------------
    /**
        Returns the safe count type. <P>
        @return The safe count type.
    **/

    //----------------------------------------------------------------------
    public int getSafeCountType()
    {                                   // begin getSafeCountType()
        return safeCountType;
    }                                   // end getSafeCountType()

    //----------------------------------------------------------------------
    /**
        Sets the safe count type. <P>
        @param  value  The safe count type.
    **/
    //----------------------------------------------------------------------
    public void setSafeCountType(int value)
    {                                   // begin setSafeCountType()
        safeCountType = value;
    }                                   // end setSafeCountType()

    //----------------------------------------------------------------------
    /**
        Returns the safe financial totals. <P>
        @return The safe financial totals.
    **/
    //----------------------------------------------------------------------
        public FinancialTotalsIfc getSafeTotals()
    {
        return safeTotals;
    }

    //----------------------------------------------------------------------
    /**
        Sets the safe financial totals. <P>
        @param value financial totals
    **/
    //----------------------------------------------------------------------
    public void setSafeTotals(FinancialTotalsIfc value)
    {
        safeTotals = value;
    }
    
    //----------------------------------------------------------------------
    /**
        Returns the function ID whose access is to be checked.
        @return int function ID 
    **/
    //----------------------------------------------------------------------
    public int getAccessFunctionID()
    {
        return RoleFunctionIfc.START_OF_DAY;
    }
    
    //----------------------------------------------------------------------
    /**
     * Returns the transaction.
     * @return TransactionIfc
     */
    //----------------------------------------------------------------------
    public TransactionIfc getTransaction()
    {
        return transaction;
    }

    //----------------------------------------------------------------------
    /**
     * Sets the transaction.
     * @param transaction The transaction to set
     */
    //----------------------------------------------------------------------
    public void setTransaction(TransactionIfc transaction)
    {
        this.transaction = transaction;
    }
    
    /**
     * @since 14.1
     * 
     * Sets the store open mode. 
     * This is one of the StoreOpenCloseTransactionIfc.STORE_OPEN_MODE constants
     * @param mode
     */
    public void setStoreOpenMode(int mode)
    {
    	storeOpenMode = mode;
    }
    
    /**
     * @since 14.1
     * 
     * Returns the Store open mode
     * @return
     */
    public int getStoreOpenMode()
    {
    	return storeOpenMode;
    }

    //----------------------------------------------------------------------
    /**
        Returns a string representation of this object. <P>
        @return String representation of object
    **/
    //----------------------------------------------------------------------
    public String toString()
    {
        String strResult = new String("Class:  StartOfDayCargo (Revision " +
                                      getRevisionNumber() +
                                      ") @" + hashCode());
        strResult += "\n" + abstractToString() + "\n";
        strResult += "Commit flag:                            [" + commitFlag + "]";

        return(strResult);
    }

    //----------------------------------------------------------------------
    /**
        Returns the revision number of the class. <P>
        @return String representation of revision number
    **/
    //----------------------------------------------------------------------
    public String getRevisionNumber()
    {
        return(revisionNumber);
    }
}                                       // end class StartOfDayCargo
