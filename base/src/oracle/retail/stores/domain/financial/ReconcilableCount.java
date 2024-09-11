/* ===========================================================================
* Copyright (c) 1998, 2012, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/modules/domain/src/oracle/retail/stores/domain/financial/ReconcilableCount.java /main/11 2012/12/14 09:46:20 abhinavs Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    abhinavs  12/11/12 - Fixing HP Fortify missing null check issues
 *    cgreene   05/26/10 - convert to oracle packaging
 *    abondala  01/03/10 - update header date
 *
 * ===========================================================================
 * $Log:
 *    3    360Commerce 1.2         3/31/2005 4:29:35 PM   Robert Pearse   
 *    2    360Commerce 1.1         3/10/2005 10:24:34 AM  Robert Pearse   
 *    1    360Commerce 1.0         2/11/2005 12:13:35 PM  Robert Pearse   
 *
 *   Revision 1.6  2004/09/23 00:30:53  kmcbride
 *   @scr 7211: Inserting serialVersionUIDs in these Serializable classes
 *
 *   Revision 1.5  2004/07/13 22:33:39  cdb
 *   @scr 5970 in Services Impact Tracker database - removed hardcoding of class names
 *   in all getHardTotalsData methods.
 *
 *   Revision 1.4  2004/07/09 18:39:18  aachinfiev
 *   @scr 6082 - Replacing "new" with DomainObjectFactory.
 *
 *   Revision 1.3  2004/02/12 17:13:34  mcs
 *   Forcing head revision
 *
 *   Revision 1.2  2004/02/11 23:25:28  bwf
 *   @scr 0 Organize imports.
 *
 *   Revision 1.1.1.1  2004/02/11 01:04:30  cschellenger
 *   updating to pvcs 360store-current
 *
 *
 * 
 *    Rev 1.0   Aug 29 2003 15:35:48   CSchellenger
 * Initial revision.
 * 
 *    Rev 1.2   Jun 10 2003 11:50:36   jgs
 * Backout hardtotals deprecations and compression change due to performance consideration.
 * 
 *    Rev 1.1   May 20 2003 07:39:40   jgs
 * Deprecated getHardTotalsData() and setHardTotalsData() methods.
 * Resolution for 2573: Modify Hardtotals compress to remove dependency on code modifications.
 * 
 *    Rev 1.0   Jun 03 2002 16:52:28   msg
 * Initial revision.
 * 
 *    Rev 1.1   Mar 18 2002 23:01:20   msg
 * - updated copyright
 * 
 *    Rev 1.0   Mar 18 2002 12:21:34   msg
 * Initial revision.
 * 
 *    Rev 1.0   Sep 20 2001 16:14:56   msg
 * Initial revision.
 * 
 *    Rev 1.1   Sep 17 2001 12:37:26   msg
 * header update
 * ===========================================================================
 */
package oracle.retail.stores.domain.financial;

//java imports
import java.io.Serializable;

import oracle.retail.stores.domain.DomainGateway;
import oracle.retail.stores.foundation.utility.Util;

//------------------------------------------------------------------------------ 
/**
    This class represents a financial entity that is counted and reconcilable.
    It consists of an entered and an expected count. 
    @version $Revision: /main/11 $
**/
//------------------------------------------------------------------------------ 
public class ReconcilableCount implements ReconcilableCountIfc
{                                       // begin class ReconcilableCount
    // This id is used to tell
    // the compiler not to generate a
    // new serialVersionUID.
    //
    static final long serialVersionUID = -5616396348313714615L;


    /**
        revision number supplied by source-code-control system
    **/
    public static String revisionNumber = "$Revision: /main/11 $";
    /**
        amount of float expected
    **/
    protected FinancialCountIfc expected = null;
    /**
        amount of float entered
    **/
    protected FinancialCountIfc entered = null;

    //---------------------------------------------------------------------
    /**
        Constructs ReconcilableCount object. <P>
        <B>Pre-Condition(s)</B>
        <UL>
        <LI>needs specification
        </UL>
        <B>Post-Condition(s)</B>
        <UL>
        <LI>needs specification
        </UL>
    **/
    //---------------------------------------------------------------------
    public ReconcilableCount()
    {                                   // begin ReconcilableCount()
        expected = instantiateFinancialCount();
        entered = instantiateFinancialCount();
    }                                  // end ReconcilableCount()

    //---------------------------------------------------------------------
    /**
        Resets totals, setting all values to zero and eliminating array
        of FinancialCountTenderItem entries. <P>
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
    public void resetTotals()
    {                                   // begin resetTotals()
        expected.resetTotals();
        entered.resetTotals();
    }                                   // end resetTotals()

    //---------------------------------------------------------------------
    /**
        Adds a ReconcilableCount object to this object and returns result. <P>
        <B>Pre-Condition(s)</B>
        <UL>
        <LI>none
        </UL>
        <B>Post-Condition(s)</B>
        <UL>
        <LI>none
        </UL>
        @param t ReconcilableCount object to be added to this object
        @return resulting ReconcilableCount object
    **/
    //--------------------------------------------------------------------- 
    public ReconcilableCountIfc add(ReconcilableCountIfc t)
    {                                   // begin add()
        addExpected(t.getExpected());
        addEntered(t.getEntered());
        ReconcilableCountIfc newTotals = (ReconcilableCountIfc) clone();
        return(newTotals);
    }                                   // end add()

    //---------------------------------------------------------------------
    /**
        Creates a negative copy of this object. <P>
        <B>Pre-Condition(s)</B>
        <UL>
        <LI>none
        </UL>
        <B>Post-Condition(s)</B>
        <UL>
        <LI>none
        </UL>
        @return resulting FinancialCount object
    **/
    //--------------------------------------------------------------------- 
    public ReconcilableCountIfc negate()
    {                                   // begin negate()
        ReconcilableCountIfc newCount = DomainGateway.getFactory().getReconcilableCountInstance();
        newCount.setExpected(getExpected().negate());    
        newCount.setEntered(getEntered().negate());    
        return(newCount);
    }                                   // end negate()

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
                ReconcilableCount c = new ReconcilableCount();
                
                // set clone attributes
                setCloneAttributes(c);

        // pass back Object
        return((Object) c);
    }                                   // end clone()

        //---------------------------------------------------------------------
        /**
                Sets attributes in clone. <P>
        @param newClass new instance of class
        **/
        //--------------------------------------------------------------------- 
        protected void setCloneAttributes(ReconcilableCount newClass)
        {                                   // begin setCloneAttributes()
        // set values
        newClass.setExpected((FinancialCountIfc) expected.clone());
        newClass.setEntered((FinancialCountIfc) entered.clone());
        }                                   // end setCloneAttributes()

    //---------------------------------------------------------------------
    /**
        Instantiates financial count class.  This is isolated so that
        the actual implementation of FinancialCountIfc can be overridden
        easily. <P>
        <B>Pre-Condition(s)</B>
        <UL>
        <LI>none
        </UL>
        <B>Post-Condition(s)</B>
        <UL>
        <LI>none
        </UL>
        @return FinancialCountIfc object
    **/
    //--------------------------------------------------------------------- 
    protected FinancialCountIfc instantiateFinancialCount()
    {                                   // begin instantiateFinancialCount()
        // instantiate base financial count class
        return(DomainGateway.getFactory().getFinancialCountInstance());
    }                                   // end instantiateFinancialCount()

    //---------------------------------------------------------------------
    /**
        Determine if two objects are identical. <P>
        <B>Pre-Condition(s)</B>
        <UL>
        <LI>none
        </UL>
        <B>Post-Condition(s)</B>
        <UL>
        <LI>none
        </UL>
        @param obj object to compare with
        @return true if the objects are identical, false otherwise
    **/
    //--------------------------------------------------------------------- 
    public boolean equals(Object obj)
    {                                   // begin equals()
    	if(obj == this)
    	{
    		return true;
    	}
    	boolean isEqual = false;
    	if(obj instanceof ReconcilableCount)
    	{
    		ReconcilableCount c = (ReconcilableCount) obj;          // downcast the input object
    		// compare all the attributes of ReconcilableCount
    		if (Util.isObjectEqual(expected, c.getExpected()) &&
    				Util.isObjectEqual(entered, c.getEntered()))
    		{
    			isEqual = true;             // set the return code to true
    		}
    	}
    	return(isEqual);
    }                                   // end equals()
    
    //---------------------------------------------------------------------
    /**
        Adds a count amount object to the expected count amount. <P>
        <B>Pre-Condition(s)</B>
        <UL>
        <LI>none
        </UL>
        <B>Post-Condition(s)</B>
        <UL>
        <LI>none
        </UL>
        @param addCount count amount to add
    **/
    //--------------------------------------------------------------------- 
    public void addExpected(FinancialCountIfc addCount)
    {                                   // begin addExpected()
        // invoke set starting value method on expected object
        expected.add(addCount);
    }                                   // end addExpected()

    //---------------------------------------------------------------------
    /**
        Adds a count amount object to the entered count amount. <P>
        <B>Pre-Condition(s)</B>
        <UL>
        <LI>none
        </UL>
        <B>Post-Condition(s)</B>
        <UL>
        <LI>none
        </UL>
        @param addCount count amount to add
    **/
    //--------------------------------------------------------------------- 
    public void addEntered(FinancialCountIfc addCount)
    {                                   // begin addEntered()
        // invoke set starting value method on entered object
        entered.add(addCount);
    }                                   // end addEntered()

    //----------------------------------------------------------------------------
    /**
        Retrieves expected count amount for this store. <P>
        @return expected count amount for this store
    **/
    //----------------------------------------------------------------------------
    public FinancialCountIfc getExpected()
    {                                   // begin getExpected()
        return(expected);
    }                                   // end getExpected()

    //----------------------------------------------------------------------------
    /**
        Sets expected count amount for this store. <P>
        @param value  expected count amount for this store
    **/
    //----------------------------------------------------------------------------
    public void setExpected(FinancialCountIfc value)
    {                                   // begin setExpected()
        expected = value;
    }                                   // end setExpected()

    //----------------------------------------------------------------------------
    /**
        Retrieves entered count amount for this store. <P>
        @return entered count amount for this store
    **/
    //----------------------------------------------------------------------------
    public FinancialCountIfc getEntered()
    {                                   // begin getEntered()
        return(entered);
    }                                   // end getEntered()

    //----------------------------------------------------------------------------
    /**
        Sets entered count amount for this store. <P>
        @param value  entered count amount for this store
    **/
    //----------------------------------------------------------------------------
    public void setEntered(FinancialCountIfc value)
    {                                   // begin setEntered()
        entered = value;
    }                                   // end setEntered()

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

        if (expected == null)
        {
            builder.appendStringObject("null");
        }
        else
        {
            expected.getHardTotalsData(builder);
        }

        if (entered == null)
        {
            builder.appendStringObject("null");
        }
        else
        {
            entered.getHardTotalsData(builder);
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
        // Get the count objects
        expected = (FinancialCountIfc)builder.getFieldAsClass();
        if (expected != null)
        {
            expected.setHardTotalsData(builder);
        }

        entered = (FinancialCountIfc)builder.getFieldAsClass();
        if (entered != null)
        {
            entered.setHardTotalsData(builder);
        }
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
        String strResult = new String("Class:  ReconcilableCount (Revision " + 
                                      getRevisionNumber() +
                                      ") @" + 
                                      hashCode());
        strResult += "\n";
        // add attributes to string
        strResult += "Entered count:\nSub" + entered.toString() + "\n";
        strResult += "Expected count:\nSub" + expected.toString() + "\n";
        // pass back result
        return(strResult);
    }                                   // end toString()

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
        ReconcilableCount main method. <P>
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
        ReconcilableCount clsReconcilableCount = new ReconcilableCount();
        // output toString()
        System.out.println(clsReconcilableCount.toString());
        try
        {
            // instantiate class
            Serializable obj                = null;
            HardTotalsStringBuilder builder = null;
            ReconcilableCount a1            = new ReconcilableCount();
            ReconcilableCountIfc a2         = null;
        
            builder = new HardTotalsStringBuilder();
            a1.getHardTotalsData(builder);
            obj     = builder.getHardTotalsOutput();
            builder.setHardTotalsInput(obj);
            a2      = (ReconcilableCountIfc)builder.getFieldAsClass();
            a2.setHardTotalsData(builder);

            if (a1.equals(a2))
            {
                System.out.println("Empty ReconcilableCounts are equal");
            }
            else
            {
                System.out.println("Empty ReconcilableCounts are NOT equal");
                System.out.println("RC 1 = " + a1.toString());
                System.out.println("RC 2 = " + a2.toString());
            }

            // instantiate class
            FinancialCount fc = new FinancialCount();
            FinancialCount.setTestData(fc);
            a1.setExpected(fc);
            fc = new FinancialCount();
            FinancialCount.setTestData(fc);
            a1.setEntered(fc);
            
            builder = new HardTotalsStringBuilder();
            a1.getHardTotalsData(builder);
            obj     = builder.getHardTotalsOutput();
            builder.setHardTotalsInput(obj);
            a2      = (ReconcilableCountIfc)builder.getFieldAsClass();
            a2.setHardTotalsData(builder);
            
            if (a1.equals(a2))
            {
                System.out.println("Full ReconcilableCounts are equal");
            }
            else
            {
                System.out.println("Full ReconcilableCounts are NOT equal");
                System.out.println("RC 1 = " + a1.toString());
                System.out.println("RC 2 = " + a2.toString());
            }
        }
        catch (HardTotalsFormatException iae)
        {
            System.out.println("ReconcilableCount convertion failed:");
            iae.printStackTrace();
        }
    }                                  // end main()
    
}                                      // end class ReconcilableCount


