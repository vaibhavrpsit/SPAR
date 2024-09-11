/* ===========================================================================
* Copyright (c) 1998, 2011, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/modules/domain/src/oracle/retail/stores/domain/transaction/RegisterOpenCloseTransaction.java /rgbustores_13.4x_generic_branch/1 2011/05/04 11:48:46 mszekely Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    cgreene   05/27/10 - convert to oracle packaging
 *    cgreene   05/26/10 - convert to oracle packaging
 *    abondala  01/03/10 - update header date
 *
 * ===========================================================================
 * $Log:
 *    6    360Commerce 1.5         4/12/2008 5:44:57 PM   Christian Greene
 *         Upgrade StringBuffer to StringBuilder
 *    5    360Commerce 1.4         6/13/2007 9:34:00 AM   Rohit Sachdeva
 *         26364:Deprecate methods related to currency read/write xml type
 *         lists status
 *    4    360Commerce 1.3         4/26/2007 6:10:07 PM   Ashok.Mondal    CR
 *         19537 :V7.2.2 merge to trunk.
 *    3    360Commerce 1.2         3/31/2005 4:29:37 PM   Robert Pearse   
 *    2    360Commerce 1.1         3/10/2005 10:24:39 AM  Robert Pearse   
 *    1    360Commerce 1.0         2/11/2005 12:13:38 PM  Robert Pearse   
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
 *    Rev 1.0   Aug 29 2003 15:41:00   CSchellenger
 * Initial revision.
 *
 *    Rev 1.1   Jul 06 2002 17:32:18   vpn-mpm
 * Implemented EndOfDayTotalsIfc
 *
 *    Rev 1.1   May 11 2002 08:30:12   mpm
 * Implemented register open/close transaction.
 * Resolution for Domain SCR-45: TLog facility
 *
 *    Rev 1.0   May 06 2002 19:43:10   mpm
 * Initial revision.
 * Resolution for Domain SCR-45: TLog facility
 * ===========================================================================
 */
package oracle.retail.stores.domain.transaction;
// foundation imports
import oracle.retail.stores.domain.financial.FinancialTotalsIfc;
import oracle.retail.stores.domain.financial.RegisterIfc;
import oracle.retail.stores.foundation.utility.Util;

//--------------------------------------------------------------------------
/**
    This is the transaction used to start the business day in a store. <P>
    @version $Revision: /rgbustores_13.4x_generic_branch/1 $
**/
//--------------------------------------------------------------------------
public class RegisterOpenCloseTransaction
extends Transaction
implements RegisterOpenCloseTransactionIfc
{
    /**
        revision number of this class
    **/
    public static String revisionNumber = "$Revision: /rgbustores_13.4x_generic_branch/1 $";
    /**
        register object
    **/
    protected RegisterIfc register = null;
    
    /**
     * boolean. True signifies file PrimaryCurrencyTypeList.xml has been updated
     * @deprecated As of Bahamas Release.  CurrencyTypeList lookup functions have been migrated to the DomainGateway
     */
    protected boolean status = false;


    /**
     *
     * @return boolean status
     * @deprecated As of Bahamas Release.  CurrencyTypeList lookup functions have been migrated to the DomainGateway
     */
    public boolean isStatus()
    {
		return status;
	}

    /**
     *
     * @param status
     * @deprecated As of Bahamas Release.  CurrencyTypeList lookup functions have been migrated to the DomainGateway
     */
    public void setStatus(boolean status)
    {
		this.status = status;
	}



    //---------------------------------------------------------------------
    /**
        Constructs RegisterOpenCloseTransaction object.
    **/
    //---------------------------------------------------------------------
    public RegisterOpenCloseTransaction()
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
        setTransactionType(TransactionIfc.TYPE_OPEN_REGISTER);
    }

    //---------------------------------------------------------------------
    /**
        Clones RegisterOpenCloseTransaction object.
        @return instance of RegisterOpenCloseTransaction object
    **/
    //---------------------------------------------------------------------
    public Object clone()
    {
        // instantiate new object
        RegisterOpenCloseTransaction trans = new RegisterOpenCloseTransaction();

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
    public void setCloneAttributes(RegisterOpenCloseTransaction newClass)
    {                                   // begin setCloneAttributes()
        // set attributes in super class
        super.setCloneAttributes(newClass);
        if (getRegister() != null)
        {
            newClass.setRegister((RegisterIfc) getRegister().clone());
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

        if (obj instanceof RegisterOpenCloseTransaction)
        {
            RegisterOpenCloseTransactionIfc d = (RegisterOpenCloseTransactionIfc) obj;

            if (super.equals(obj) &&
                Util.isObjectEqual(getRegister(),
                                   d.getRegister()))
            {
                isEqual = true;
            }
        }

        return isEqual;
    }

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
        Sets financial totals.
        @param value financial totals
    **/
    //---------------------------------------------------------------------
    public void setFinancialTotals(FinancialTotalsIfc value)
    {                                   // begin setFinancialTotals()
        register.setTotals(value);
    }                                   // end setFinancialTotals()

    //---------------------------------------------------------------------
    /**
        Returns financial totals.
        @return financial totals
    **/
    //---------------------------------------------------------------------
    public FinancialTotalsIfc getFinancialTotals()
    {                                   // begin getFinancialTotals()
        return(register.getTotals());
    }                                   // end getFinancialTotals()

    //---------------------------------------------------------------------
    /**
        Returns end-of-day financial totals.
        @return end-of-day financial totals
    **/
    //---------------------------------------------------------------------
    public FinancialTotalsIfc getEndOfDayTotals()
    {                                   // begin getEndOfDayTotals()
        return(getFinancialTotals());
    }                                   // end getEndOfDayTotals()

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
          Util.classToStringHeader("RegisterOpenCloseTransaction",
                                   getRevisionNumber(),
                                   hashCode());
        strResult.append(super.toString())
                 .append(Util.formatToStringEntry("Register",
                                                  getRegister()));
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
        RegisterOpenCloseTransaction main method.
        <p>
        @param args     command-line parameters
    **/
    //---------------------------------------------------------------------
    public static void main(String args[])
    {
        // instantiate class
        RegisterOpenCloseTransaction t = new RegisterOpenCloseTransaction();
        System.out.println(t.getRevisionNumber());
    }

}
