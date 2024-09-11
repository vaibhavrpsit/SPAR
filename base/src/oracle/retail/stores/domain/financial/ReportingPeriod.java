/* ===========================================================================
* Copyright (c) 1998, 2011, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/modules/domain/src/oracle/retail/stores/domain/financial/ReportingPeriod.java /rgbustores_13.4x_generic_branch/1 2011/05/04 11:49:13 mszekely Exp $
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
 *    3    360Commerce 1.2         3/31/2005 4:29:39 PM   Robert Pearse   
 *    2    360Commerce 1.1         3/10/2005 10:24:42 AM  Robert Pearse   
 *    1    360Commerce 1.0         2/11/2005 12:13:41 PM  Robert Pearse   
 *
 *   Revision 1.3  2004/09/23 00:30:53  kmcbride
 *   @scr 7211: Inserting serialVersionUIDs in these Serializable classes
 *
 *   Revision 1.2  2004/02/12 17:13:34  mcs
 *   Forcing head revision
 *
 *   Revision 1.1.1.1  2004/02/11 01:04:30  cschellenger
 *   updating to pvcs 360store-current
 *
 *
 * 
 *    Rev 1.0   Aug 29 2003 15:35:48   CSchellenger
 * Initial revision.
 * 
 *    Rev 1.0   Jun 03 2002 16:52:36   msg
 * Initial revision.
 * 
 *    Rev 1.1   Mar 18 2002 23:01:30   msg
 * - updated copyright
 * 
 *    Rev 1.0   Mar 18 2002 12:21:44   msg
 * Initial revision.
 * 
 *    Rev 1.0   Sep 20 2001 16:14:36   msg
 * Initial revision.
 * 
 *    Rev 1.1   Sep 17 2001 12:37:24   msg
 * header update
 * ===========================================================================
 */
package oracle.retail.stores.domain.financial;

// foundation imports
import oracle.retail.stores.foundation.utility.Util;

//--------------------------------------------------------------------------
/**
     A time interval bounded by a starting and ending calendar date,
     used to divide the retailer's fiscal year into periods.
     <P>
     @version $Revision: /rgbustores_13.4x_generic_branch/1 $
**/
//--------------------------------------------------------------------------
public class ReportingPeriod implements ReportingPeriodIfc
{                                   // begin class ReportingPeriod
    // This id is used to tell
    // the compiler not to generate a
    // new serialVersionUID.
    //
    static final long serialVersionUID = -2559963929312878181L;

    /**
        revision number supplied by source-code-control system
    **/
    public static String revisionNumber = "$Revision: /rgbustores_13.4x_generic_branch/1 $";

    /**
        The identifier for a particular reporting period
    **/
    protected String reportingPeriodID;

    /**
        The type of reporting period
    **/
    protected int reportingPeriodType;

    /**
        An identifier that uniquely identifies a particular
        fiscal year in CCYY format.
    **/
    protected String fiscalYear;

    //----------------------------------------------------------------------
    /**
        Class constructor.
    **/
    //----------------------------------------------------------------------
    public ReportingPeriod()
    {
    }

    //----------------------------------------------------------------------
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
    //----------------------------------------------------------------------
    public Object clone()
    {
        // insantiate new object
        ReportingPeriodIfc o = new ReportingPeriod();
                
                // set clone attributes
                setCloneAttributes(o);

        // pass back Object
        return((Object)o);
    }

        //---------------------------------------------------------------------
        /**
                Sets attributes in clone. <P>
        @param newClass new instance of class
        **/
        //--------------------------------------------------------------------- 
        protected void setCloneAttributes(ReportingPeriodIfc newClass)
        {                                   // begin setCloneAttributes()
        // set values
        newClass.setReportingPeriodID(reportingPeriodID);
        newClass.setReportingPeriodType(reportingPeriodType);
        newClass.setFiscalYear(fiscalYear);
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
        boolean isEqual = true;

        // Make sure it's a ReportingPeriod
        if (obj instanceof ReportingPeriod)
        {
            // downcast the input object
            ReportingPeriod c = (ReportingPeriod) obj;

            // compare all the attributes of ReportingPeriod

            if (Util.isObjectEqual(getFiscalYear(), c.getFiscalYear()) &&
                Util.isObjectEqual(getReportingPeriodID(), c.getReportingPeriodID()) &&
                getReportingPeriodType() == c.getReportingPeriodType() )
            {
                isEqual = true;
            }
            else
            {
                isEqual = false;
            }
        }
        else
        {
            isEqual = false;
        }

        return(isEqual);
    }                                   // end equals()

    //----------------------------------------------------------------------
    /**
        Returns the fiscal year.
        <p>
        <B>Pre-Condition(s)</B>
        <UL>
        <LI>none
        </UL>
        <B>Post-Condition(s)</B>
        <UL>
        <LI>none
        </UL>
        @return the fiscal year.
    **/
    //----------------------------------------------------------------------
    public String getFiscalYear()
    {
        return(fiscalYear);
    }

    //----------------------------------------------------------------------
    /**
        Sets the fiscal year.
        <p>
        <B>Pre-Condition(s)</B>
        <UL>
        <LI>none
        </UL>
        <B>Post-Condition(s)</B>
        <UL>
        <LI>none
        </UL>
        @param value  the fiscal year
    **/
    //----------------------------------------------------------------------
    public void setFiscalYear(String value)
    {
        fiscalYear = value;
    }

    //----------------------------------------------------------------------
    /**
        Returns the reporting period type.
        <p>
        <B>Pre-Condition(s)</B>
        <UL>
        <LI>none
        </UL>
        <B>Post-Condition(s)</B>
        <UL>
        <LI>none
        </UL>
        @return the reporting period type.
    **/
    //----------------------------------------------------------------------
    public int getReportingPeriodType()
    {
        return(reportingPeriodType);
    }

    //----------------------------------------------------------------------
    /**
        Sets the reporting period type.
        <p>
        <B>Pre-Condition(s)</B>
        <UL>
        <LI>none
        </UL>
        <B>Post-Condition(s)</B>
        <UL>
        <LI>none
        </UL>
        @param value  the reporting period type
    **/
    //----------------------------------------------------------------------
    public void setReportingPeriodType(int value)
    {
        reportingPeriodType = value;
    }

    //----------------------------------------------------------------------
    /**
        Returns the reporting period id.
        <p>
        <B>Pre-Condition(s)</B>
        <UL>
        <LI>none
        </UL>
        <B>Post-Condition(s)</B>
        <UL>
        <LI>none
        </UL>
        @return the reporting period id.
    **/
    //----------------------------------------------------------------------
    public String getReportingPeriodID()
    {
        return(reportingPeriodID);
    }

    //----------------------------------------------------------------------
    /**
        Sets the reporting period id.
        <p>
        <B>Pre-Condition(s)</B>
        <UL>
        <LI>none
        </UL>
        <B>Post-Condition(s)</B>
        <UL>
        <LI>none
        </UL>
        @param value  the reporting period id
    **/
    //----------------------------------------------------------------------
    public void setReportingPeriodID(String value)
    {
        reportingPeriodID = value;
    }

    //---------------------------------------------------------------------
    /**
        Displays string representation of the object.
        <P>
        @return String representation of object
    **/
    //---------------------------------------------------------------------
    public String toString()
    {                                   // begin toString()
        // result string
        String strResult = new String("Class:  ReportingPeriod (Revision " +
                                      getRevisionNumber() +
                                      ") @" +
                                      hashCode());
        strResult += "\n";                              
        // add attributes to string
        strResult += "reportingPeriodID                [" + reportingPeriodID + "]\n";    
        strResult += "reportingPeriodType              [" + reportingPeriodType + "]\n";    
        strResult += "fiscalYear                       [" + fiscalYear + "]\n";    

        // pass back result
        return(strResult);
    }                                  // end toString()

    //---------------------------------------------------------------------
    /**
        Returns the source-code-control system revision number.
        <P>
        @return String representation of revision number
    **/
    //---------------------------------------------------------------------
    public String getRevisionNumber()
    {                                   // begin getRevisionNumber()
        // return string
        return(revisionNumber);
    }                                  // end getRevisionNumber()
}                                   // end class ReportingPeriod
