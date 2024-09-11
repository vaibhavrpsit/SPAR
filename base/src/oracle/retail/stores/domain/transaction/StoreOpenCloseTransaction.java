/* ===========================================================================
* Copyright (c) 1998, 2014, Oracle and/or its affiliates. All rights reserved.
 * ===========================================================================
 * $Header: rgbustores/modules/domain/src/oracle/retail/stores/domain/transaction/StoreOpenCloseTransaction.java /main/13 2014/07/23 15:44:28 rhaight Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    rhaight   07/23/14 - Code review updates
 *    rhaight   07/03/14 - store offline open revisions
 *    cgreene   05/27/10 - convert to oracle packaging
 *    cgreene   05/26/10 - convert to oracle packaging
 *    abondala  01/03/10 - update header date
 *
 * ===========================================================================
 * $Log:
 *    4    360Commerce 1.3         4/12/2008 5:44:57 PM   Christian Greene
 *         Upgrade StringBuffer to StringBuilder
 *    3    360Commerce 1.2         3/31/2005 4:30:13 PM   Robert Pearse   
 *    2    360Commerce 1.1         3/10/2005 10:25:35 AM  Robert Pearse   
 *    1    360Commerce 1.0         2/11/2005 12:14:29 PM  Robert Pearse   
 *
 *   Revision 1.3  2004/02/12 17:14:42  mcs
 *   Forcing head revision
 *
 *   Revision 1.2  2004/02/11 23:28:50  bwf
 *   @scr 0 Organize imports.
 *
 *   Revision 1.1.1.1  2004/02/11 01:04:34  cschellenger
 *   updating to pvcs 360store-current
 *
 *
 * 
 *    Rev 1.0   Aug 29 2003 15:41:06   CSchellenger
 * Initial revision.
 * 
 *    Rev 1.1   Jul 06 2002 17:32:24   vpn-mpm
 * Implemented EndOfDayTotalsIfc
 * 
 *    Rev 1.3   May 09 2002 20:36:18   mpm
 * Tweaked.
 * Resolution for Domain SCR-45: TLog facility
 *
 *    Rev 1.2   May 08 2002 20:47:08   mpm
 * Began re-factoring of store open/close transaction.
 * Resolution for Domain SCR-45: TLog facility
 *
 *    Rev 1.1   May 01 2002 18:11:24   mpm
 * Added partial support for financial totals in store open, close.
 * Resolution for Domain SCR-45: TLog facility
 *
 *    Rev 1.0   Apr 29 2002 13:53:18   mpm
 * Initial revision.
 * Resolution for Domain SCR-45: TLog facility
 * ===========================================================================
 */
package oracle.retail.stores.domain.transaction;
// foundation imports
import oracle.retail.stores.domain.financial.FinancialCountIfc;
import oracle.retail.stores.domain.financial.FinancialTotalsIfc;
import oracle.retail.stores.domain.financial.StoreStatusIfc;
import oracle.retail.stores.foundation.utility.Util;

//--------------------------------------------------------------------------
/**
    This is the transaction used to start and end the business day in a store. <P>
    @version $Revision: /main/13 $
**/
//--------------------------------------------------------------------------
public class StoreOpenCloseTransaction
extends Transaction
implements StoreOpenCloseTransactionIfc
{
    /** Serial Version ID */
	private static final long serialVersionUID = 6596965512567217662L;
	
	/**
        revision number of this class
    **/
    public static String revisionNumber = "$Revision: /main/13 $";
    /**
        store status object
    **/
    protected StoreStatusIfc storeStatus = null;
    /**
        starting safe count
    **/
    protected FinancialCountIfc startingSafeCount = null;
    /**
        ending safe count
    **/
    protected FinancialCountIfc endingSafeCount = null;
    /**
        end-of-day financial totals object
    **/
    protected FinancialTotalsIfc endOfDayTotals = null;
    
    /** 
     * @since 14.1
     * Store Open Close Mode - Online, Offline, or Duplicate 
     * */
    protected int storeOpenMode = StoreOpenCloseTransactionIfc.STORE_OPEN_MODE_ONLINE;

    //---------------------------------------------------------------------
    /**
        Constructs StoreOpenCloseTransaction object.
    **/
    //---------------------------------------------------------------------
    public StoreOpenCloseTransaction()
    {
        initialize();
    }

    //---------------------------------------------------------------------
    /**
        Initializes the protected data members of the object.
    **/
    //---------------------------------------------------------------------
    protected void initialize()
    {
        setTransactionType(TransactionIfc.TYPE_OPEN_STORE);
    }

    //---------------------------------------------------------------------
    /**
        Clones StoreOpenCloseTransaction object.
        @return instance of StoreOpenCloseTransaction object
    **/
    //---------------------------------------------------------------------
    public Object clone()
    {
        // instantiate new object
        StoreOpenCloseTransaction trans = new StoreOpenCloseTransaction();

        setCloneAttributes(trans);

        // pass back object
        return((Object)trans);
    }

    //---------------------------------------------------------------------
    /**
        Sets attributes in new instance of class. <P>
        @param newClass new instance of class
    **/
    //---------------------------------------------------------------------
    public void setCloneAttributes(StoreOpenCloseTransaction newClass)
    {                                   // begin setCloneAttributes()
        // set attributes in super class
        super.setCloneAttributes(newClass);
        if (getStoreStatus() != null)
        {
            newClass.setStoreStatus((StoreStatusIfc) getStoreStatus().clone());
        }
        if (getStartingSafeCount() != null)
        {
            newClass.setStartingSafeCount((FinancialCountIfc) getStartingSafeCount().clone());
        }
        if (getEndingSafeCount() != null)
        {
            newClass.setEndingSafeCount((FinancialCountIfc) getEndingSafeCount().clone());
        }
        if (getEndOfDayTotals() != null)
        {
            newClass.setEndOfDayTotals((FinancialTotalsIfc) getEndOfDayTotals().clone());
        }
    }                                   // end setCloneAttributes()

    //---------------------------------------------------------------------
    /**
        Determine if two objects are identical. <P>
        @param obj object to compare with
        @return true if the objects are identical, false otherwise
    **/
    //---------------------------------------------------------------------
    public boolean equals(Object obj)
    {
        boolean isEqual = false;

        if (obj instanceof StoreOpenCloseTransaction)
        {
            StoreOpenCloseTransactionIfc d = (StoreOpenCloseTransactionIfc) obj;

            if (super.equals(obj) &&
                Util.isObjectEqual(getStoreStatus(),
                                   d.getStoreStatus()) &&
                Util.isObjectEqual(getStartingSafeCount(),
                                   d.getStartingSafeCount()) &&
                Util.isObjectEqual(getEndingSafeCount(),
                                   d.getEndingSafeCount()) &&
                Util.isObjectEqual(getEndOfDayTotals(),
                                   d.getEndOfDayTotals()))
            {
                isEqual = true;
            }
        }

        return isEqual;
    }

    //---------------------------------------------------------------------
    /**
        Sets store status.
        @param value store status
    **/
    //---------------------------------------------------------------------
    public void setStoreStatus(StoreStatusIfc value)
    {                                   // begin setStoreStatus()
        storeStatus = value;
    }                                   // end setStoreStatus()

    //---------------------------------------------------------------------
    /**
        Returns store status.
        @return store status
    **/
    //---------------------------------------------------------------------
    public StoreStatusIfc getStoreStatus()
    {                                   // begin getStoreStatus()
        return(storeStatus);
    }                                   // end getStoreStatus()

    //---------------------------------------------------------------------
    /**
        Sets starting safe count.
        @param value starting safe count
    **/
    //---------------------------------------------------------------------
    public void setStartingSafeCount(FinancialCountIfc value)
    {                                   // begin setStartingSafeCount()
        startingSafeCount = value;
    }                                   // end setStartingSafeCount()

    //---------------------------------------------------------------------
    /**
        Returns starting safe count.
        @return starting safe count
    **/
    //---------------------------------------------------------------------
    public FinancialCountIfc getStartingSafeCount()
    {                                   // begin getStartingSafeCount()
        return(startingSafeCount);
    }                                   // end getStartingSafeCount()

    //---------------------------------------------------------------------
    /**
        Sets ending safe count.
        @param value ending safe count
    **/
    //---------------------------------------------------------------------
    public void setEndingSafeCount(FinancialCountIfc value)
    {                                   // begin setEndingSafeCount()
        endingSafeCount = value;
    }                                   // end setEndingSafeCount()

    //---------------------------------------------------------------------
    /**
        Returns ending safe count.
        @return ending safe count
    **/
    //---------------------------------------------------------------------
    public FinancialCountIfc getEndingSafeCount()
    {                                   // begin getEndingSafeCount()
        return(endingSafeCount);
    }                                   // end getEndingSafeCount()

    //---------------------------------------------------------------------
    /**
        Sets end-of-day financial totals.
        @param value end-of-day financial totals
    **/
    //---------------------------------------------------------------------
    public void setEndOfDayTotals(FinancialTotalsIfc value)
    {                                   // begin setEndOfDayTotals()
        endOfDayTotals = value;
    }                                   // end setEndOfDayTotals()

    //---------------------------------------------------------------------
    /**
        Returns end-of-day financial totals.
        @return end-of-day financial totals
    **/
    //---------------------------------------------------------------------
    public FinancialTotalsIfc getEndOfDayTotals()
    {                                   // begin getEndOfDayTotals()
        return(endOfDayTotals);
    }                                   // end getEndOfDayTotals()

    /**
     * @since 14.1
     * 
     * Sets the Store Open mode - Online, Offline, Duplicate
     * @param offline
     */
    @Override
    public void setStoreOpenMode(int mode)
    {
    	switch (mode)
    	{
	    	case StoreOpenCloseTransactionIfc.STORE_OPEN_MODE_ONLINE:
	    	case StoreOpenCloseTransactionIfc.STORE_OPEN_MODE_OFFLINE:
	    	case StoreOpenCloseTransactionIfc.STORE_OPEN_MODE_DUPLICATE:
	    	{
	    		storeOpenMode = mode;
	    		break;
	    	}
	    	default:
	    	{
	    		throw new IllegalStateException("StoreOpenMode value must be one of the StoreOpenCloseTransactionIfc.STORE_OPEN_ values");
	    	}
    		
    	}
    }
    
    /**
     * @since 14.1
     * 
     * Returns Store Open mode - Online, Offline, Duplicate
     * @return 
     */
    @Override
    public int getStoreOpenMode()
    {
    	return storeOpenMode;
    }
    
    //---------------------------------------------------------------------
    /**
        Returns string representation of object. <P>
        @return String representation of object
    **/
    //---------------------------------------------------------------------
    public String toString()
    {
        // result string
        StringBuilder strResult =
          Util.classToStringHeader("StoreOpenCloseTransaction",
                                   getRevisionNumber(),
                                   hashCode());
        strResult.append(super.toString())
                 .append(Util.formatToStringEntry("StoreStatus",
                                                  getStoreStatus()))
                 .append(Util.formatToStringEntry("StartingSafeCount",
                                                  getStartingSafeCount()))
                 .append(Util.formatToStringEntry("EndingSafeCount",
                                                  getEndingSafeCount()))
                 .append(Util.formatToStringEntry("endOfDayTotals",
                                                  getEndOfDayTotals()));
        // pass back result
        return(strResult.toString());
    }

    //---------------------------------------------------------------------
    /**
        Returns the revision number.
        @return String representation of revision number
    **/
    //---------------------------------------------------------------------
    public String getRevisionNumber()
    {
        return(Util.parseRevisionNumber(revisionNumber));
    }

    //---------------------------------------------------------------------
    /**
        StoreOpenCloseTransaction main method.
        <p>
        @param args     command-line parameters
    **/
    //---------------------------------------------------------------------
    public static void main(String args[])
    {
        // instantiate class
        StoreOpenCloseTransaction t = new StoreOpenCloseTransaction();
        System.out.println(t.getRevisionNumber());
    }

}
