/* ===========================================================================
* Copyright (c) 1998, 2014, Oracle and/or its affiliates. All rights reserved.
 * ===========================================================================
 * $Header: rgbustores/modules/domain/src/oracle/retail/stores/domain/financial/Till.java /main/12 2014/07/09 13:10:48 icole Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    icole     06/26/14 - Forward port fix for handling the condition of two
 *                         registers opened with same till with one or both
 *                         offline at time of open.
 *    cgreene   05/26/10 - convert to oracle packaging
 *    abondala  01/03/10 - update header date
 *    jswan     08/13/09 - Added the till reconcile amount and count to the
 *                         till, workstation and store tender history tables
 *                         and to code that reads from and writes to them.
 *
 * ===========================================================================
 * $Log:
 *    5    360Commerce 1.4         12/4/2007 11:07:15 PM  Robinson Joseph Code
 *         changed to consider till ids whose length is less than one as
 *         invalid till id.
 *    4    360Commerce 1.3         4/25/2007 10:00:52 AM  Anda D. Cadar   I18N
 *         merge
 *    3    360Commerce 1.2         3/31/2005 4:30:29 PM   Robert Pearse   
 *    2    360Commerce 1.1         3/10/2005 10:26:09 AM  Robert Pearse   
 *    1    360Commerce 1.0         2/11/2005 12:15:02 PM  Robert Pearse   
 *
 *   Revision 1.7  2004/09/23 00:30:53  kmcbride
 *   @scr 7211: Inserting serialVersionUIDs in these Serializable classes
 *
 *   Revision 1.6  2004/07/30 21:05:53  dcobb
 *   @scr 6462 Financial Totals are not correct for the detail count during Till Open/Reconcile
 *   Replaced all instances of FinancialCountIfc.getTenderItem(int, String) with getSummaryTenderItemByDescriptor(TenderDescriptorIfc).
 *
 *   Revision 1.5  2004/07/13 22:33:39  cdb
 *   @scr 5970 in Services Impact Tracker database - removed hardcoding of class names
 *   in all getHardTotalsData methods.
 *
 *   Revision 1.4  2004/06/18 22:56:43  cdb
 *   @scr 4205 Corrected problems caused by searching financial counts
 *   by tender description rather than tender descriptor - which caused problems
 *   with foreign currencies.
 *
 *   Revision 1.3  2004/02/12 17:13:34  mcs
 *   Forcing head revision
 *
 *   Revision 1.2  2004/02/11 23:25:27  bwf
 *   @scr 0 Organize imports.
 *
 *   Revision 1.1.1.1  2004/02/11 01:04:31  cschellenger
 *   updating to pvcs 360store-current
 *
 *
 * 
 *    Rev 1.0   Aug 29 2003 15:35:52   CSchellenger
 * Initial revision.
 * 
 *    Rev 1.3   Jun 10 2003 11:50:38   jgs
 * Backout hardtotals deprecations and compression change due to performance consideration.
 * 
 *    Rev 1.2   May 20 2003 07:49:10   jgs
 * Deprecated getHardTotalsData() and setHardTotalsData() methods.
 * Resolution for 2573: Modify Hardtotals compress to remove dependency on code modifications.
 * 
 *    Rev 1.1   Dec 20 2002 11:15:28   DCobb
 * Add floating till.
 * Resolution for POS SCR-1867: POS 6.0 Floating Till
 * 
 *    Rev 1.0   Jun 03 2002 16:52:58   msg
 * Initial revision.
 * 
 *    Rev 1.1   Mar 18 2002 23:00:34   msg
 * - updated copyright
 * 
 *    Rev 1.0   Mar 18 2002 12:20:38   msg
 * Initial revision.
 * 
 *    Rev 1.0   Sep 20 2001 16:14:48   msg
 * Initial revision.
 * 
 *    Rev 1.1   Sep 17 2001 12:37:20   msg
 * header update
 * ===========================================================================
 */
package oracle.retail.stores.domain.financial;

import java.io.Serializable;
import java.util.Vector;

import oracle.retail.stores.commerceservices.common.currency.CurrencyIfc;
import oracle.retail.stores.domain.DomainGateway;
import oracle.retail.stores.domain.employee.EmployeeIfc;
import oracle.retail.stores.domain.tender.TenderDescriptorIfc;
import oracle.retail.stores.common.utility.Util;

//----------------------------------------------------------------------------
/**
     This class represents the state of the till during POS operations. <P>
     Included as attributes in this class are the open and close time, 
     business day and drawer ID.  The previous status
     and time of the previous status change are also retained. <P>
     There are also two FinancialTotals classes (one for expected totals, one
     for entered totals) and an array of cashiers associated with this class. <P>
     @version $Revision: /main/12 $
**/
//----------------------------------------------------------------------------
public class Till extends AbstractFinancialEntity implements TillIfc
{                                         // begin class Till
    // This id is used to tell
    // the compiler not to generate a
    // new serialVersionUID.
    //
    static final long serialVersionUID = -2694548773662863380L;

    /**
        revision number supplied by source-code-control system
    **/
    public static String revisionNumber = "$Revision: /main/12 $";
    /**
        till identifier
    **/
    protected String tillID = "";
    /**
        drawer identifier
    **/
    protected String drawerID = "";
    /**
        register identifier
    **/
    protected String registerID = "";
    /**
        cashiers vector
    **/
    protected Vector cashiersVector = new Vector();
    /**
        register accountability flag
        @see oracle.retail.stores.domain.financial.AbstractStatusEntityIfc
    **/
    protected int regAccountability = AbstractStatusEntityIfc.ACCOUNTABILITY_REGISTER;
    /**
        till type flag
        @see oracle.retail.stores.domain.financial.AbstractStatusEntityIfc
    **/
    protected int tillType = AbstractStatusEntityIfc.TILL_TYPE_STATIONARY;

        //---------------------------------------------------------------------
        /**
                Constructs Till object. <P>
                <B>Pre-Condition(s)</B>
                <UL>
                <LI>none
                </UL>
                <B>Post-Condition(s)</B>
                <UL>
                <LI>none
                </UL>
        **/
        //---------------------------------------------------------------------
        public Till()
        {                                   // begin Till()
        }                                  // end Till()

    //---------------------------------------------------------------------
    /**
        Creates clone of this object. <P>
        <B>Pre-Condition(s)</B>
        <UL>
        <LI>none
        </UL>
        <B>Post-Condition(s)</B>
        <UL>
        <LI>none
        </UL>
        @return Object clone of this object
    **/
    //--------------------------------------------------------------------- 
    public Object clone()
    {                                   // begin clone()
        // instantiate new object
                Till c = new Till();
                
                // set clone attributes
                setCloneAttributes(c);

        // pass back Object
        return((Object) c);
    }                                   // end clone()

    //---------------------------------------------------------------------
    /**
        Determine if two objects are identical. <P>
        @param obj object to compare with
        @return true if the objects are identical, false otherwise
    **/
    //--------------------------------------------------------------------- 
    public boolean equals(Object obj)
    {                                   // begin equals()
        boolean isEqual = false;
        
        if (obj instanceof Till)
        {
            Till c = (Till) obj;          // downcast the input object
            // compare all the attributes of FinancialTotals
            if (super.equals(obj) &&
                Util.isObjectEqual(getTillID(), c.getTillID()) &&
                Util.isObjectEqual(getDrawerID(), c.getDrawerID()) &&
                Util.isObjectEqual(getCashiers(), c.getCashiers()))
            {
                isEqual = true;             // set the return code to true
            }
            else
            {
                isEqual = false;            // set the return code to false
            }
            return(isEqual);
        }
        
        return isEqual;
    }                                   // end equals()
    
        //---------------------------------------------------------------------
        /**
                Sets attributes in clone. <P>
        @param newClass new instance of class
        **/
        //--------------------------------------------------------------------- 
        protected void setCloneAttributes(Till newClass)
        {                                   // begin setCloneAttributes()
        // set values
        super.setCloneAttributes((AbstractFinancialEntity) newClass);
        if (tillID != null)
        {
            newClass.setTillID(new String(tillID));
        }
        if (drawerID != null)
        {
            newClass.setDrawerID(new String(drawerID));
        }
        // clone cashiers
        EmployeeIfc[] t = getCashiers();
        if (t != null)
        {
            EmployeeIfc[] tclone = new EmployeeIfc[t.length];
            for (int i = 0; i < t.length; i++)
            {
                tclone[i] = (EmployeeIfc) t[i].clone();
            }
            newClass.setCashiers(tclone);
        }
        
        newClass.setRegisterAccountability(getRegisterAccountability());
        newClass.setTillType(getTillType());
        }                                   // end setCloneAttributes()

    //---------------------------------------------------------------------
    /**
        Retrieves EmployeeIfc object matching specified identifier.  If no
        match is found, null is returned. <P>
        <B>Pre-Condition(s)</B>
        <UL>
        <LI>none
        </UL>
        <B>Post-Condition(s)</B>
        <UL>
        <LI>none
        </UL>
        @param cashierID requested employee ID
        @return EmployeeIfc object if found; null if not found
    **/
    //--------------------------------------------------------------------- 
    public EmployeeIfc getCashierByID(String cashierID)
    {                                   // begin getCashierByID()
        EmployeeIfc t = null;
        EmployeeIfc listCashier = null;    
        // loop through cashiers    
        for (int i = 0; i < cashiersVector.size(); i++)
        {
            // pull cashier from vector
            listCashier = (EmployeeIfc) cashiersVector.elementAt(i);
            // if match found, set cashierID and exit
            if (listCashier.getEmployeeID().equals(cashierID))
            {
                t = listCashier;
                i = cashiersVector.size();
            }
        }
        return(t);
    }                                   // end getCashierByID()


    //----------------------------------------------------------------------------
    /**
        Retrieves till identifier. <P>
        @return till identifier
    **/
    //----------------------------------------------------------------------------
    public String getTillID()
    {                                   // begin getTillID()
        return(tillID);
    }                                   // end getTillID()

    //----------------------------------------------------------------------------
    /**
        Sets till identifier. <P>
        <B>Pre-Condition(s)</B>
        <UL>
        <LI>none 
        </UL>
        <B>Post-Condition(s)</B>
        <UL>
        <LI>Till identifier set
        </UL>
        @param value  till identifier
    **/
    //----------------------------------------------------------------------------
    public void setTillID(String value)
    {                                   // begin setTillID()
        tillID = value;
    }                                   // end setTillID()

    //----------------------------------------------------------------------------
    /**
        Retrieves drawer identifier. <P>
        @return drawer identifier
    **/
    //----------------------------------------------------------------------------
    public String getDrawerID()
    {                                   // begin getDrawerID()
        return(drawerID);
    }                                   // end getDrawerID()

    //----------------------------------------------------------------------------
    /**
        Sets drawer identifier. <P>
        @param value  drawer identifier
    **/
    //----------------------------------------------------------------------------
    public void setDrawerID(String value)
    {                                   // begin setDrawerID()
        drawerID = value;
    }                                   // end setDrawerID()

    //----------------------------------------------------------------------------
    /**
        Retrieves cashiers for this till, loading cashiers array <code>cashiers</code> 
        from <code>cashiersVector</code>. <P>
        @return cashiers for this till
    **/
    //----------------------------------------------------------------------------
    public EmployeeIfc[] getCashiers()
    {                                   // begin getCashiers()
        EmployeeIfc[] cashiers = new EmployeeIfc[cashiersVector.size()];
        cashiersVector.copyInto((EmployeeIfc[]) cashiers);
        return(cashiers);
    }                                   // end getCashiers()

    //----------------------------------------------------------------------------
    /**
        Sets cashiers for this till, loading cashiers array <code>cashiers</code>
        into <code>cashiersVector</code>. <P>
        <B>Pre-Condition(s)</B>
        <UL>
        <LI>none 
        </UL>
        <B>Post-Condition(s)</B>
        <UL>
        <LI>none
        </UL>
        @param value  cashiers for this till
    **/
    //----------------------------------------------------------------------------
    public void setCashiers(EmployeeIfc[] value)
    {                                   // begin setCashiers()
        cashiersVector = new Vector();
        for (int i = 0; i < value.length; i++)
        {
            cashiersVector.addElement((Object) value[i]);
        }    
    }                                   // end setCashiers()

    //---------------------------------------------------------------------
    /**
        Adds cashier to array. If cashier already in list, nothing happens. <P> 
        <B>Pre-Condition(s)</B>
        <UL>
        <LI>none
        </UL>
        <B>Post-Condition(s)</B>
        <UL>
        <LI>none
        </UL>
        @param cashier EmployeeIfc object to be added to list
    **/
    //--------------------------------------------------------------------- 
    public void addCashier(EmployeeIfc cashier)
    {                                   // begin addCashier()
        // confirm cashier doesn't already exist
        if (getCashierByID(cashier.getEmployeeID()) == null)
        {
            // add element to vector, list
            cashiersVector.addElement((Object) cashier);
        }
    }                                   // end addCashier()


    //---------------------------------------------------------------------
    /**
        Determines if till id is valid. <P> 
        <B>Pre-Condition(s)</B>
        <UL>
        <LI>none
        </UL>
        <B>Post-Condition(s)</B>
        <UL>
        <LI>none
        </UL>
        @param String tillId
    **/
    //--------------------------------------------------------------------- 
    public boolean isIdValid(String tillId)
        {
            // Only criteria so far is Max Lenght = 5 chars
                boolean b = true;

        if ((tillId.length() < 1) || tillId.length() > 5)
                    b = false;

                return (b);
        }

    //---------------------------------------------------------------------
    /**
     Returns the current amount for a supplied tender in this till
     @param String name of the desired tender
     @return The current amount for a given tender in this till
     **/
    //--------------------------------------------------------------------- 
    public CurrencyIfc getAmountTotal(TenderDescriptorIfc tenderDesc)
    {
        FinancialCountTenderItemIfc fcti = getTotals().
        getCombinedCount().
        getExpected().
        getSummaryTenderItemByDescriptor(tenderDesc);

        CurrencyIfc amount = null;
        if (fcti != null)
        {
            amount = fcti.getAmountTotal();
        }
        else
        {
            amount = DomainGateway.getBaseCurrencyInstance();
        }

        return amount;
    }

    //---------------------------------------------------------------------
    /**
        This method converts hard totals information to a comma delimited
        String. <P>
        @return String
    **/
    //--------------------------------------------------------------------- 
    public void getHardTotalsData(HardTotalsBuilderIfc builder)
    {
        builder.appendStringObject(getClass().getName());
        super.getHardTotalsData(builder);
        builder.appendStringObject(tillID);
        builder.appendStringObject(drawerID);

        int len = 0;
        EmployeeIfc[] cashiers = getCashiers();
        if (cashiers != null)
        {
            len = cashiersVector.size();
        }
        builder.appendInt(len);        
        for(int i = 0; i < len; i++)
        {
            cashiers[i].getHardTotalsData(builder);
        }
    }

    //---------------------------------------------------------------------
    /**
        This method populates this object from a comma delimited string.
        <P>
        @param String   String containing hard totals data.
    **/
    //--------------------------------------------------------------------- 
    public void setHardTotalsData(HardTotalsBuilderIfc builder) throws HardTotalsFormatException
    {
        super.setHardTotalsData(builder);
        tillID   = builder.getStringObject();
        drawerID = builder.getStringObject();

        // Get the Cashiers
        int number = builder.getIntField();
        for(int i = 0; i < number; i++)
        {
            EmployeeIfc cashier = (EmployeeIfc)builder.getFieldAsClass();
            if (cashier != null)
            {
                cashier.setHardTotalsData(builder);
            }
            addCashier(cashier);
        }
    }
    
    //----------------------------------------------------------------------------
    /**
        Retrieves register accountability flag (see AbstractFinancialEntityIfc). <P>
        @return register accountability flag (see AbstractFinancialEntityIfc)
    **/
    //----------------------------------------------------------------------------
    public int getRegisterAccountability()
    {
        return(regAccountability);
    }

    //----------------------------------------------------------------------------
    /**
        Sets register accountability flag (see AbstractFinancialEntityIfc). <P>
        @param value  register accountability flag (see AbstractFinancialEntityIfc)
    **/
    //----------------------------------------------------------------------------
    public void setRegisterAccountability(int value)
    {
        regAccountability = value;
    }
    
    //----------------------------------------------------------------------------
    /**
        Retrieves till type flag (see AbstractFinancialEntityIfc). <P>
        @return till type flag (see AbstractFinancialEntityIfc)
    **/
    //----------------------------------------------------------------------------
    public int getTillType()
    {
        return(tillType);
    }
    
    //----------------------------------------------------------------------------
    /**
        Sets till type flag (see AbstractFinancialEntityIfc). <P>
        @param value  till type flag (see AbstractFinancialEntityIfc)
    **/
    //----------------------------------------------------------------------------
    public void setTillType(int value)
    {
        tillType = value;
    }       
    
    //----------------------------------------------------------------------------
    /**
     * @return Returns the registerID.
     */
    public String getRegisterID()
    {
        return registerID;
    }
  //----------------------------------------------------------------------------
    /**
     * @param registerID The registerID to set.
     */
    public void setRegisterID(String registerID)
    {
        this.registerID = registerID;
    }
    
    //---------------------------------------------------------------------
    /**
        Returns default display string. <P>
        @return String representation of object
    **/
    //---------------------------------------------------------------------
    public String toString()
    {                                   // begin toString()
        // build result string
        String strResult = "**** START TILL       **** START TILL       **** START TILL    \n";
        strResult += "Class:  Till (Revision " + getRevisionNumber() + ") @" + hashCode()+ "\n";
        // add attributes to string
        if (tillID == null)
        {
            strResult += "tillID:                             [null]\n";
        } 
        else
        {
            strResult += "tillID:                             [" + tillID + "]\n";
        }
        if (drawerID == null)
        {
            strResult += "drawerID:                           [null]\n";
        } 
        else
        {
            strResult += "drawerID:                           [" + drawerID + "]\n";
        }
        if (getCashiers() == null)
        {
            strResult += "cashiers:                                  [null]\n";
        } 
        else
        {
            strResult += "Cashier List:\n";
            EmployeeIfc[] cashiers = getCashiers();
            for (int i = 0; i < cashiers.length; i++)
            {
                strResult += "Cashier " + (i + 1) + "\n";
                strResult += cashiers[i].toString() + "\n";
            }
        }
        
        strResult += "Register Accountability:                [" + regAccountability + "]\n";
        strResult += "Till Type:                              [" + tillType + "n";
        strResult += "\n" + attributesToString() + "\n";
        strResult += "\n\n**** END TILL     **** END TILL     **** END TILL    ";
        
        // pass back result
        return(strResult);
    }                                  // end toString()

    //---------------------------------------------------------------------
    /**
        Retrieves the source-code-control system revision number. <P>
        @return String representation of revision number
    **/
    //---------------------------------------------------------------------
    public String getRevisionNumber()
    {                                   // begin getRevisionNumber()
        // return string
        return(revisionNumber);
    }                                  // end getRevisionNumber()

    //---------------------------------------------------------------------
    /**
        Till main method. <P>
        <B>Pre-Condition(s)</B>
        <UL>
        <LI>none
        </UL>
        <B>Post-Condition(s)</B>
        <UL>
        <LI>toString() output
        </UL>
        @param String args[]  command-line parameters
    **/
    //---------------------------------------------------------------------
    public static void main(String args[])
    {                                   // begin main()
        // instantiate class
        Till c = new Till();
        // output toString()
        // System.out.println(c.toString());
        try
        {
            // instantiate class
            HardTotalsStringBuilder builder = null;
            Serializable obj                = null;
            Till a1     = new Till();
            TillIfc a2  = null;
        
            builder = new HardTotalsStringBuilder();
            a1.getHardTotalsData(builder);
            obj     = builder.getHardTotalsOutput();
            builder.setHardTotalsInput(obj);
            a2      = (TillIfc)builder.getFieldAsClass();
            a2.setHardTotalsData(builder);

            if (a1.equals(a2))
            {
                System.out.println("Empty Tills are equal");
            }
            else
            {
                System.out.println("Empty Tills are NOT equal");
                System.out.println("Till 1 = " + a1);
                System.out.println("Till 2 = " + a2);
            }
        }
        catch (HardTotalsFormatException iae)
        {
            System.out.println("Till convertion failed:");
            iae.printStackTrace();
        }
    }                                  // end main()
}                                                  // end class Till
