/* ===========================================================================
* Copyright (c) 1998, 2011, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/modules/domain/src/oracle/retail/stores/domain/transaction/TillOpenCloseTransaction.java /rgbustores_13.4x_generic_branch/1 2011/05/04 11:48:47 mszekely Exp $
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
 *    4    360Commerce 1.3         4/12/2008 5:44:57 PM   Christian Greene
 *         Upgrade StringBuffer to StringBuilder
 *    3    360Commerce 1.2         3/31/2005 4:30:30 PM   Robert Pearse   
 *    2    360Commerce 1.1         3/10/2005 10:26:13 AM  Robert Pearse   
 *    1    360Commerce 1.0         2/11/2005 12:15:06 PM  Robert Pearse   
 *
 *   Revision 1.4  2004/09/23 00:30:51  kmcbride
 *   @scr 7211: Inserting serialVersionUIDs in these Serializable classes
 *
 *   Revision 1.3  2004/02/12 17:14:42  mcs
 *   Forcing head revision
 *
 *   Revision 1.2  2004/02/11 23:28:51  bwf
 *   @scr 0 Organize imports.
 *
 *   Revision 1.1.1.1  2004/02/11 01:04:34  cschellenger
 *   updating to pvcs 360store-current
 *
 *
 * 
 *    Rev 1.0   Aug 29 2003 15:41:10   CSchellenger
 * Initial revision.
 * 
 *    Rev 1.0   Jun 03 2002 17:06:26   msg
 * Initial revision.
 * 
 *    Rev 1.1   May 12 2002 20:17:22   mpm
 * Implemented re-factored till transactions.
 * Resolution for Domain SCR-45: TLog facility
 *
 *    Rev 1.0   May 06 2002 19:43:12   mpm
 * Initial revision.
 * Resolution for Domain SCR-45: TLog facility
 * ===========================================================================
 */
package oracle.retail.stores.domain.transaction;
// java imports
import java.util.ArrayList;

import oracle.retail.stores.domain.financial.FinancialCountIfc;
import oracle.retail.stores.domain.financial.ReconcilableCountIfc;
import oracle.retail.stores.domain.financial.RegisterIfc;
import oracle.retail.stores.domain.financial.TillIfc;
import oracle.retail.stores.domain.tender.TenderDescriptorIfc;
import oracle.retail.stores.foundation.utility.Util;

//--------------------------------------------------------------------------
/**
    This is the transaction used to open and close a till.
    @version $Revision: /rgbustores_13.4x_generic_branch/1 $
**/
//--------------------------------------------------------------------------
public class TillOpenCloseTransaction
extends Transaction
implements TillOpenCloseTransactionIfc
{
    // This id is used to tell
    // the compiler not to generate a
    // new serialVersionUID.
    //
    static final long serialVersionUID = -5289976455330895199L;

    /**
        revision number of this class
    **/
    public static String revisionNumber = "$Revision: /rgbustores_13.4x_generic_branch/1 $";
    /**
        till object
    **/
    protected TillIfc till = null;
    /**
        register object
    **/
    protected RegisterIfc register = null;
    /**
        starting float count
    **/
    protected FinancialCountIfc startingFloatCount = null;
    /**
        ending till float count
    **/
    protected ReconcilableCountIfc endingFloatCount = null;
    /**
        ending combined entered count
    **/
    protected FinancialCountIfc endingCombinedEnteredCount = null;
    /**
        tender descriptor list
    **/
    protected ArrayList tenderDescriptorArrayList = null;

    //---------------------------------------------------------------------
    /**
        Constructs TillOpenCloseTransaction object.
    **/
    //---------------------------------------------------------------------
    public TillOpenCloseTransaction()
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
        setTransactionType(TransactionIfc.TYPE_OPEN_TILL);
        tenderDescriptorArrayList = new ArrayList();
    }

    //---------------------------------------------------------------------
    /**
        Clones TillOpenCloseTransaction object.
        @return instance of TillOpenCloseTransaction object
    **/
    //---------------------------------------------------------------------
    public Object clone()
    {
        // instantiate new object
        TillOpenCloseTransaction trans = new TillOpenCloseTransaction();

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
    public void setCloneAttributes(TillOpenCloseTransaction newClass)
    {                                   // begin setCloneAttributes()
        // set attributes in super class
        super.setCloneAttributes(newClass);
        if (getTill() != null)
        {
            newClass.setTill((TillIfc) getTill().clone());
        }
        if (getRegister() != null)
        {
            newClass.setRegister((RegisterIfc) getRegister().clone());
        }
        if (getStartingFloatCount() != null)
        {
            newClass.setStartingFloatCount((FinancialCountIfc) getStartingFloatCount().clone());
        }
        if (getEndingFloatCount() != null)
        {
            newClass.setEndingFloatCount((ReconcilableCountIfc) getEndingFloatCount().clone());
        }
        if (getEndingCombinedEnteredCount() != null)
        {
            newClass.setEndingCombinedEnteredCount((FinancialCountIfc) getEndingCombinedEnteredCount().clone());
        }
        if (getTenderDescriptorArrayList() != null)
        {
            newClass.setTenderDescriptorArrayList((ArrayList) getTenderDescriptorArrayList().clone());
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

        if (obj instanceof TillOpenCloseTransaction)
        {
            TillOpenCloseTransactionIfc d = (TillOpenCloseTransactionIfc) obj;

            if (super.equals(obj) &&
                Util.isObjectEqual(getTill(),
                                   d.getTill()) &&
                Util.isObjectEqual(getRegister(),
                                   d.getRegister()) &&
                Util.isObjectEqual(getStartingFloatCount(),
                                   d.getStartingFloatCount()) &&
                Util.isObjectEqual(getEndingFloatCount(),
                                   d.getEndingFloatCount()) &&
                Util.isObjectEqual(getEndingCombinedEnteredCount(),
                                   d.getEndingCombinedEnteredCount()) &&
                Util.isObjectEqual(getTenderDescriptorArrayList(),
                                   d.getTenderDescriptorArrayList()))
            {
                isEqual = true;
            }
        }

        return isEqual;
    }

    //---------------------------------------------------------------------
    /**
        Sets till.
        @param value till
    **/
    //---------------------------------------------------------------------
    public void setTill(TillIfc value)
    {                                   // begin setTill()
        till = value;
    }                                   // end setTill()

    //---------------------------------------------------------------------
    /**
        Returns till.
        @return till
    **/
    //---------------------------------------------------------------------
    public TillIfc getTill()
    {                                   // begin getTill()
        return(till);
    }                                   // end getTill()

    //---------------------------------------------------------------------
    /**
        Sets register.
        @param value register
    **/
    //---------------------------------------------------------------------
    public void setRegister(RegisterIfc value)
    {                                   // begin setRegister()
        register = value;
    }                                   // end setRegister()

    //---------------------------------------------------------------------
    /**
        Returns register.
        @return register
    **/
    //---------------------------------------------------------------------
    public RegisterIfc getRegister()
    {                                   // begin getRegister()
        return(register);
    }                                   // end getRegister()

    //---------------------------------------------------------------------
    /**
        Sets starting float count.
        @param value starting float count
    **/
    //---------------------------------------------------------------------
    public void setStartingFloatCount(FinancialCountIfc value)
    {                                   // begin setStartingFloatCount()
        startingFloatCount = value;
    }                                   // end setStartingFloatCount()

    //---------------------------------------------------------------------
    /**
        Returns starting float count.
        @return starting float count
    **/
    //---------------------------------------------------------------------
    public FinancialCountIfc getStartingFloatCount()
    {                                   // begin getStartingFloatCount()
        return(startingFloatCount);
    }                                   // end getStartingFloatCount()

    //---------------------------------------------------------------------
    /**
        Sets ending till float count.
        @param value ending till float count
    **/
    //---------------------------------------------------------------------
    public void setEndingFloatCount(ReconcilableCountIfc value)
    {                                   // begin setEndingFloatCount()
        endingFloatCount = value;
    }                                   // end setEndingFloatCount()

    //---------------------------------------------------------------------
    /**
        Returns ending till float count.
        @return ending till float count
    **/
    //---------------------------------------------------------------------
    public ReconcilableCountIfc getEndingFloatCount()
    {                                   // begin getEndingFloatCount()
        return(endingFloatCount);
    }                                   // end getEndingFloatCount()

    //---------------------------------------------------------------------
    /**
        Sets ending combined entered count.
        @param value ending combined entered count
    **/
    //---------------------------------------------------------------------
    public void setEndingCombinedEnteredCount(FinancialCountIfc value)
    {                                   // begin setEndingCombinedEnteredCount()
        endingCombinedEnteredCount = value;
    }                                   // end setEndingCombinedEnteredCount()

    //---------------------------------------------------------------------
    /**
        Returns ending combined entered count.
        @return ending combined entered count
    **/
    //---------------------------------------------------------------------
    public FinancialCountIfc getEndingCombinedEnteredCount()
    {                                   // begin getEndingCombinedEnteredCount()
        return(endingCombinedEnteredCount);
    }                                   // end getEndingCombinedEnteredCount()

    //---------------------------------------------------------------------
    /**
        Sets tender descriptor list.
        @param value tender descriptor list
    **/
    //---------------------------------------------------------------------
    public void setTenderDescriptorArrayList(ArrayList value)
    {                                   // begin setTenderDescriptorArrayList()
        tenderDescriptorArrayList = value;
    }                                   // end setTenderDescriptorArrayList()

    //---------------------------------------------------------------------
    /**
        Returns tender descriptor list as ArrayList.
        @return tender descriptor list
    **/
    //---------------------------------------------------------------------
    public ArrayList getTenderDescriptorArrayList()
    {                                   // begin getTenderDescriptorArrayList()
        return(tenderDescriptorArrayList);
    }                                   // end getTenderDescriptorArrayList()

    //---------------------------------------------------------------------
    /**
        Returns tender descriptor list as array.
        @return tender descriptor list
    **/
    //---------------------------------------------------------------------
    public TenderDescriptorIfc[] getTenderDescriptorArray()
    {
        TenderDescriptorIfc[] returnArray =
          new TenderDescriptorIfc[tenderDescriptorArrayList.size()];
        tenderDescriptorArrayList.toArray(returnArray);
        return returnArray;
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
          Util.classToStringHeader("TillOpenCloseTransaction",
                                   getRevisionNumber(),
                                   hashCode());
        strResult.append(super.toString())
                 .append(Util.formatToStringEntry("Till",
                                                  getTill()))
                 .append(Util.formatToStringEntry("Register",
                                                  getRegister()))
                 .append(Util.formatToStringEntry("StartingFloatCount",
                                                  getStartingFloatCount()))
                 .append(Util.formatToStringEntry("EndingCombinedEnteredCount",
                                                  getEndingCombinedEnteredCount()));
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
        TillOpenCloseTransaction main method.
        <p>
        @param args     command-line parameters
    **/
    //---------------------------------------------------------------------
    public static void main(String args[])
    {
        // instantiate class
        TillOpenCloseTransaction t = new TillOpenCloseTransaction();
        System.out.println(t.getRevisionNumber());
    }

}
