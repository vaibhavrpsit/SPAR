/* ===========================================================================
* Copyright (c) 1998, 2011, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/modules/domain/src/oracle/retail/stores/domain/transaction/BankDepositTransaction.java /rgbustores_13.4x_generic_branch/1 2011/05/04 11:48:48 mszekely Exp $
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
 *    3    360Commerce 1.2         3/31/2005 4:27:16 PM   Robert Pearse   
 *    2    360Commerce 1.1         3/10/2005 10:19:47 AM  Robert Pearse   
 *    1    360Commerce 1.0         2/11/2005 12:09:33 PM  Robert Pearse   
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
 *    Rev 1.0   Aug 29 2003 15:40:46   CSchellenger
 * Initial revision.
 * 
 *    Rev 1.0   Jun 03 2002 17:05:42   msg
 * Initial revision.
 * 
 *    Rev 1.1   May 23 2002 14:09:18   mpm
 * Added support for bank-deposit transaction.
 * Resolution for Domain SCR-45: TLog facility
 *
 *    Rev 1.0   May 22 2002 18:36:14   mpm
 * Initial revision.
 * Resolution for Domain SCR-45: TLog facility
 * ===========================================================================
 */
package oracle.retail.stores.domain.transaction;
// java imports
import java.util.ArrayList;

import oracle.retail.stores.domain.financial.FinancialCountIfc;
import oracle.retail.stores.domain.tender.TenderDescriptorIfc;
import oracle.retail.stores.foundation.utility.Util;

//--------------------------------------------------------------------------
/**
    This is the transaction used to start and end the business day in a store. <P>
    @version $Revision: /rgbustores_13.4x_generic_branch/1 $
**/
//--------------------------------------------------------------------------
public class BankDepositTransaction
extends Transaction
implements BankDepositTransactionIfc
{
    // This id is used to tell
    // the compiler not to generate a
    // new serialVersionUID.
    //
    static final long serialVersionUID = 8836723804532573003L;

    /**
        revision number of this class
    **/
    public static String revisionNumber = "$Revision: /rgbustores_13.4x_generic_branch/1 $";
    /**
        count associated with deposit
    **/
    protected FinancialCountIfc depositCount = null;
    /**
        tender descriptor list
    **/
    protected ArrayList tenderDescriptorArrayList = null;

    //---------------------------------------------------------------------
    /**
        Constructs BankDepositTransaction object.
    **/
    //---------------------------------------------------------------------
    public BankDepositTransaction()
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
        setTransactionType(TransactionIfc.TYPE_BANK_DEPOSIT_STORE);
        tenderDescriptorArrayList = new ArrayList();
    }

    //---------------------------------------------------------------------
    /**
        Clones BankDepositTransaction object.
        @return instance of BankDepositTransaction object
    **/
    //---------------------------------------------------------------------
    public Object clone()
    {
        // instantiate new object
        BankDepositTransaction trans = new BankDepositTransaction();

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
    public void setCloneAttributes(BankDepositTransaction newClass)
    {                                   // begin setCloneAttributes()
        // set attributes in super class
        super.setCloneAttributes(newClass);
        if (getDepositCount() != null)
        {
            newClass.setDepositCount((FinancialCountIfc) getDepositCount().clone());
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

        if (obj instanceof BankDepositTransaction)
        {
            BankDepositTransactionIfc d = (BankDepositTransactionIfc) obj;

            if (super.equals(obj) &&
                Util.isObjectEqual(getDepositCount(),
                                   d.getDepositCount()) &&
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
        Sets count associated with deposit.
        @param value count associated with deposit
    **/
    //---------------------------------------------------------------------
    public void setDepositCount(FinancialCountIfc value)
    {                                   // begin setDepositCount()
        depositCount = value;
    }                                   // end setDepositCount()

    //---------------------------------------------------------------------
    /**
        Returns count associated with deposit.
        @return count associated with deposit
    **/
    //---------------------------------------------------------------------
    public FinancialCountIfc getDepositCount()
    {                                   // begin getDepositCount()
        return(depositCount);
    }                                   // end getDepositCount()

    //---------------------------------------------------------------------
    /**
        Sets tender descriptor array.
        @param value tender descriptor array
    **/
    //---------------------------------------------------------------------
    public void setTenderDescriptorArray(TenderDescriptorIfc[] value)
    {                                   // begin setTenderDescriptorArray()
        tenderDescriptorArrayList.clear();
        int numItems = 0;
        if (value != null)
        {
            numItems = value.length;
        }
        for (int i = 0; i < numItems; i++)
        {
            tenderDescriptorArrayList.add(value[i]);

        }
    }                                   // end setTenderDescriptorArray()

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
          Util.classToStringHeader("BankDepositTransaction",
                                   getRevisionNumber(),
                                   hashCode());
        strResult.append(super.toString())
                 .append(Util.formatToStringEntry("DepositCount",
                                                  getDepositCount()));
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
        BankDepositTransaction main method.
        <p>
        @param args     command-line parameters
    **/
    //---------------------------------------------------------------------
    public static void main(String args[])
    {
        // instantiate class
        BankDepositTransaction t = new BankDepositTransaction();
        System.out.println(t.getRevisionNumber());
    }

}
