/* ===========================================================================
* Copyright (c) 1998, 2012, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/modules/domain/src/oracle/retail/stores/domain/financial/EmployeeActivity.java /main/11 2012/12/14 09:46:19 abhinavs Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    abhinavs  12/11/12 - Fixing HP Fortify missing null check issues
 *    masahu    07/04/11 - FORTIFY FIX: Employee Sensitive informstion is
 *                         printed in logs and in console
 *    cgreene   05/26/10 - convert to oracle packaging
 *    abondala  01/03/10 - update header date
 *
 * ===========================================================================
 * $Log:
 *    3    360Commerce 1.2         3/31/2005 4:27:56 PM   Robert Pearse
 *    2    360Commerce 1.1         3/10/2005 10:21:16 AM  Robert Pearse
 *    1    360Commerce 1.0         2/11/2005 12:10:48 PM  Robert Pearse
 *
 *   Revision 1.4  2004/09/23 00:30:54  kmcbride
 *   @scr 7211: Inserting serialVersionUIDs in these Serializable classes
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
 *    Rev 1.0   Aug 29 2003 15:35:32   CSchellenger
 * Initial revision.
 *
 *    Rev 1.0   Jun 03 2002 16:51:52   msg
 * Initial revision.
 *
 *    Rev 1.1   Mar 18 2002 23:00:24   msg
 * - updated copyright
 *
 *    Rev 1.0   Mar 18 2002 12:20:26   msg
 * Initial revision.
 *
 *    Rev 1.0   Sep 20 2001 16:14:08   msg
 * Initial revision.
 *
 *    Rev 1.1   Sep 17 2001 12:37:38   msg
 * header update
 * ===========================================================================
 */
package oracle.retail.stores.domain.financial;
// foundation imports
import oracle.retail.stores.domain.employee.EmployeeIfc;
import oracle.retail.stores.foundation.utility.Util;

//----------------------------------------------------------------------------
/**
     This class handles report data for financial activity conducted
     by a specified employee. <P>
     @version $Revision: /main/11 $
**/
//----------------------------------------------------------------------------
public class EmployeeActivity implements EmployeeActivityIfc
{                                       // begin class EmployeeActivity
    // This id is used to tell
    // the compiler not to generate a
    // new serialVersionUID.
    //
    static final long serialVersionUID = 4500371690852553671L;

    /**
        revision number supplied by source-code-control system
    **/
    public static String revisionNumber = "$Revision: /main/11 $";
    /**
        employee
    **/
    protected EmployeeIfc employee = null;
    /**
        totals for employee
    **/
    protected FinancialTotalsIfc totals = null;

        //---------------------------------------------------------------------
        /**
                Constructs EmployeeActivity object. <P>
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
        public EmployeeActivity()
        {                                   // begin EmployeeActivity()
        }                                   // end EmployeeActivity()

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
                EmployeeActivity c = new EmployeeActivity();

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
        protected void setCloneAttributes(EmployeeActivity newClass)
        {                                   // begin setCloneAttributes()
        // set values
        if (employee != null)
        {
            newClass.setEmployee((EmployeeIfc) employee.clone());
        }
        if (totals != null)
        {
            newClass.setTotals((FinancialTotalsIfc) totals.clone());
        }
        }                                   // end setCloneAttributes()

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
    	  
          if(obj == this){
          	return true;
          }
          boolean isEqual = false;
          if(obj instanceof EmployeeActivity)
          {
          EmployeeActivity c = (EmployeeActivity) obj;          // downcast the input object
          // compare all the attributes of EmployeeActivity
          if (Util.isObjectEqual(employee, c.getEmployee()) &&
              Util.isObjectEqual(totals, c.getTotals()))
          {
              isEqual = true;             // set the return code to true
          }
          }
          return(isEqual);
    }                                   // end equals()

    //----------------------------------------------------------------------------
    /**
        Retrieves employee . <P>
        <B>Pre-Condition(s)</B>
        <UL>
        <LI>none
        </UL>
        <B>Post-Condition(s)</B>
        <UL>
        <LI>none
        </UL>
        @return employee
    **/
    //----------------------------------------------------------------------------
    public EmployeeIfc getEmployee()
    {                                   // begin getEmployee()
        return(employee);
    }                                   // end getEmployee()

    //----------------------------------------------------------------------------
    /**
        Sets employee . <P>
        <B>Pre-Condition(s)</B>
        <UL>
        <LI>none
        </UL>
        <B>Post-Condition(s)</B>
        <UL>
        <LI>none
        </UL>
        @param value  employee
    **/
    //----------------------------------------------------------------------------
    public void setEmployee(EmployeeIfc value)
    {                                   // begin setEmployee()
        employee = value;
    }                                   // end setEmployee()

    //----------------------------------------------------------------------------
    /**
        Retrieves totals for employee. <P>
        <B>Pre-Condition(s)</B>
        <UL>
        <LI>none
        </UL>
        <B>Post-Condition(s)</B>
        <UL>
        <LI>none
        </UL>
        @return totals for employee
    **/
    //----------------------------------------------------------------------------
    public FinancialTotalsIfc getTotals()
    {                                   // begin getTotals()
        return(totals);
    }                                   // end getTotals()

    //----------------------------------------------------------------------------
    /**
        Sets totals for employee. <P>
        <B>Pre-Condition(s)</B>
        <UL>
        <LI>none
        </UL>
        <B>Post-Condition(s)</B>
        <UL>
        <LI>none
        </UL>
        @param value  totals for employee
    **/
    //----------------------------------------------------------------------------
    public void setTotals(FinancialTotalsIfc value)
    {                                   // begin setTotals()
        totals = value;
    }                                   // end setTotals()

    //---------------------------------------------------------------------
    /**
        Returns default display string. <P>
        @return String representation of object
    **/
    //---------------------------------------------------------------------
    public String toString()
    {                                   // begin toString()
        // build result string
        String strResult = new String("Class:  EmployeeActivity (Revision " +
                                      getRevisionNumber() +
                                      ") @" +
                                      hashCode());
        strResult += "\n";
        // add attributes to string
        if (employee == null)
        {
            strResult += "employee:                           [null]\n";
        }
        else
        {
            strResult += employee.toString() + "\n";
        }
        if (totals == null)
        {
            strResult += "totals:                             [null]\n";
        }
        else
        {
            strResult += totals.toString() + "\n";
        }
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
    }                                   // end getRevisionNumber()

    //---------------------------------------------------------------------
    /**
        EmployeeActivity main method. <P>
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
        EmployeeActivityIfc c = new EmployeeActivity();
        // output toString()
        System.out.println("EmployeeActivity Object Created");
    }                                   // end main()
}                                       // end class EmployeeActivity
