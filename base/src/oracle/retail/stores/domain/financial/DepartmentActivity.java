/* ===========================================================================
* Copyright (c) 1998, 2012, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/modules/domain/src/oracle/retail/stores/domain/financial/DepartmentActivity.java /main/12 2012/12/14 09:46:19 abhinavs Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    abhinavs  12/11/12 - Fixing HP Fortify missing null check issues
 *    cgreene   05/27/10 - convert to oracle packaging
 *    cgreene   05/26/10 - convert to oracle packaging
 *    abondala  01/03/10 - update header date
 *
 * ===========================================================================
 * $Log:
 *    3    360Commerce 1.2         3/31/2005 4:27:44 PM   Robert Pearse   
 *    2    360Commerce 1.1         3/10/2005 10:20:54 AM  Robert Pearse   
 *    1    360Commerce 1.0         2/11/2005 12:10:33 PM  Robert Pearse   
 *
 *   Revision 1.4  2004/09/23 00:30:53  kmcbride
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
 *    Rev 1.0   Aug 29 2003 15:35:30   CSchellenger
 * Initial revision.
 * 
 *    Rev 1.0   Jun 03 2002 16:51:44   msg
 * Initial revision.
 * 
 *    Rev 1.1   Mar 18 2002 23:00:14   msg
 * - updated copyright
 * 
 *    Rev 1.0   Mar 18 2002 12:20:18   msg
 * Initial revision.
 * 
 *    Rev 1.0   Sep 20 2001 16:14:06   msg
 * Initial revision.
 * 
 *    Rev 1.1   Sep 17 2001 12:37:38   msg
 * header update
 * ===========================================================================
 */
package oracle.retail.stores.domain.financial;
// foundation imports
import oracle.retail.stores.domain.store.DepartmentIfc;
import oracle.retail.stores.foundation.utility.Util;

//----------------------------------------------------------------------------
/**
     This class handles report data for financial activity conducted
     by a specified department. <P>
     @version $Revision: /main/12 $
**/
//----------------------------------------------------------------------------
public class DepartmentActivity implements DepartmentActivityIfc
{                                       // begin class DepartmentActivity
    // This id is used to tell
    // the compiler not to generate a
    // new serialVersionUID.
    //
    static final long serialVersionUID = 3017958301077347008L;

    /**
        revision number supplied by source-code-control system
    **/
    public static String revisionNumber = "$Revision: /main/12 $";
    /**
        department 
    **/
    protected DepartmentIfc department = null;
    /**
        totals for department
    **/
    protected FinancialTotalsIfc totals = null;

        //---------------------------------------------------------------------
        /**
                Constructs DepartmentActivity object. <P>
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
        public DepartmentActivity()
        {                                   // begin DepartmentActivity()
        }                                   // end DepartmentActivity()

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
                DepartmentActivity c = new DepartmentActivity();
                
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
        protected void setCloneAttributes(DepartmentActivity newClass)
        {                                   // begin setCloneAttributes()
        // set values
        if (department != null)
        {
            newClass.setDepartment((DepartmentIfc) department.clone());
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
       
        if(obj == this)
        {
        	return true;
        }
        boolean isEqual = false;
        if(obj instanceof DepartmentActivity)
        {
        DepartmentActivity c = (DepartmentActivity) obj;          // downcast the input object
        // compare all the attributes of DepartmentActivity
        if (Util.isObjectEqual(department, c.getDepartment()) &&
            Util.isObjectEqual(totals, c.getTotals()))
        {
            isEqual = true;             // set the return code to true
        }
        }
        return(isEqual);
    }                                   // end equals()

    //----------------------------------------------------------------------------
    /**
        Retrieves department . <P>
        <B>Pre-Condition(s)</B>
        <UL>
        <LI>none
        </UL>
        <B>Post-Condition(s)</B>
        <UL>
        <LI>none
        </UL>
        @return department 
    **/
    //----------------------------------------------------------------------------
    public DepartmentIfc getDepartment()
    {                                   // begin getDepartment()
        return(department);
    }                                   // end getDepartment()

    //----------------------------------------------------------------------------
    /**
        Sets department . <P>
        <B>Pre-Condition(s)</B>
        <UL>
        <LI>none
        </UL>
        <B>Post-Condition(s)</B>
        <UL>
        <LI>none
        </UL>
        @param value  department 
    **/
    //----------------------------------------------------------------------------
    public void setDepartment(DepartmentIfc value)
    {                                   // begin setDepartment()
        department = value;
    }                                   // end setDepartment()

    //----------------------------------------------------------------------------
    /**
        Retrieves totals for department. <P>
        <B>Pre-Condition(s)</B>
        <UL>
        <LI>none
        </UL>
        <B>Post-Condition(s)</B>
        <UL>
        <LI>none
        </UL>
        @return totals for department
    **/
    //----------------------------------------------------------------------------
    public FinancialTotalsIfc getTotals()
    {                                   // begin getTotals()
        return(totals);
    }                                   // end getTotals()

    //----------------------------------------------------------------------------
    /**
        Sets totals for department. <P>
        <B>Pre-Condition(s)</B>
        <UL>
        <LI>none
        </UL>
        <B>Post-Condition(s)</B>
        <UL>
        <LI>none
        </UL>
        @param value  totals for department
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
        String strResult = new String("Class:  DepartmentActivity (Revision " + 
                                      getRevisionNumber() +
                                      ") @" + 
                                      hashCode());
        strResult += "\n";
        // add attributes to string
        if (department == null)
        {
            strResult += "department:                           [null]\n";
        } 
        else
        {
            strResult += department.toString() + "\n";
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
        DepartmentActivity main method. <P>
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
        DepartmentActivityIfc c = new DepartmentActivity();
        // output toString()
        System.out.println(c.toString());
    }                                   // end main()
}                                       // end class DepartmentActivity
