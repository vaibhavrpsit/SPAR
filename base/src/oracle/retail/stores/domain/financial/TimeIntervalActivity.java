/* ===========================================================================
* Copyright (c) 1998, 2012, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/modules/domain/src/oracle/retail/stores/domain/financial/TimeIntervalActivity.java /main/11 2012/12/14 09:46:21 abhinavs Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    abhinavs  12/12/12 - Fixing HP Fortify missing null check issues
 *    cgreene   05/26/10 - convert to oracle packaging
 *    abondala  01/03/10 - update header date
 *
 * ===========================================================================
 * $Log:
 *    3    360Commerce 1.2         3/31/2005 4:30:32 PM   Robert Pearse   
 *    2    360Commerce 1.1         3/10/2005 10:26:16 AM  Robert Pearse   
 *    1    360Commerce 1.0         2/11/2005 12:15:08 PM  Robert Pearse   
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
 *   Revision 1.1.1.1  2004/02/11 01:04:31  cschellenger
 *   updating to pvcs 360store-current
 *
 *
 * 
 *    Rev 1.0   Aug 29 2003 15:35:54   CSchellenger
 * Initial revision.
 * 
 *    Rev 1.0   Jun 03 2002 16:53:02   msg
 * Initial revision.
 * 
 *    Rev 1.1   Mar 18 2002 23:00:40   msg
 * - updated copyright
 * 
 *    Rev 1.0   Mar 18 2002 12:20:46   msg
 * Initial revision.
 * 
 *    Rev 1.0   Sep 20 2001 16:14:50   msg
 * Initial revision.
 * 
 *    Rev 1.1   Sep 17 2001 12:37:20   msg
 * header update
 * ===========================================================================
 */
package oracle.retail.stores.domain.financial;
// foundation imports
import oracle.retail.stores.domain.utility.EYSDate;
import oracle.retail.stores.foundation.utility.Util;

//----------------------------------------------------------------------------
/**
     This class handles report data for financial activity over a specified
     time interval. <P>
     @version $Revision: /main/11 $
**/
//----------------------------------------------------------------------------
public class TimeIntervalActivity implements TimeIntervalActivityIfc
{                                       // begin class TimeIntervalActivity
    // This id is used to tell
    // the compiler not to generate a
    // new serialVersionUID.
    //
    static final long serialVersionUID = -4352775699369088955L;

    /**
        revision number supplied by source-code-control system
    **/
    public static String revisionNumber = "$Revision: /main/11 $";
    /**
        timestamp of start of interval
    **/
    protected EYSDate startTime = null;
    /**
        timestamp of end of interval
    **/
    protected EYSDate endTime = null;
    /**
        totals for time interval
    **/
    protected FinancialTotalsIfc totals = null;

        //---------------------------------------------------------------------
        /**
                Constructs TimeIntervalActivity object. <P>
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
        public TimeIntervalActivity()
        {                                   // begin TimeIntervalActivity()
        }                                   // end TimeIntervalActivity()

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
                TimeIntervalActivity c = new TimeIntervalActivity();
                
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
        protected void setCloneAttributes(TimeIntervalActivity newClass)
        {                                   // begin setCloneAttributes()
        // set values
        if (startTime != null)
        {
            newClass.setStartTime((EYSDate) startTime.clone());
        }
        if (endTime != null)
        {
            newClass.setEndTime((EYSDate) endTime.clone());
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
        	boolean isEqual =false;
        	if(obj instanceof TimeIntervalActivity)
        	{
        		TimeIntervalActivity c = (TimeIntervalActivity) obj;          // downcast the input object
        		// compare all the attributes of TimeIntervalActivity
        		if (Util.isObjectEqual(startTime, c.getStartTime()) &&
        				Util.isObjectEqual(endTime, c.getEndTime()) &&
        				Util.isObjectEqual(totals, c.getTotals()))
        		{
        			isEqual = true;             // set the return code to true
        		}
        	}
        	return(isEqual);
        }                                   // end equals()

    //----------------------------------------------------------------------------
    /**
        Retrieves timestamp of start of interval. <P>
        <B>Pre-Condition(s)</B>
        <UL>
        <LI>none
        </UL>
        <B>Post-Condition(s)</B>
        <UL>
        <LI>none
        </UL>
        @return timestamp of start of interval
    **/
    //----------------------------------------------------------------------------
    public EYSDate getStartTime()
    {                                   // begin getStartTime()
        return(startTime);
    }                                   // end getStartTime()

    //----------------------------------------------------------------------------
    /**
        Sets timestamp of start of interval. <P>
        <B>Pre-Condition(s)</B>
        <UL>
        <LI>none
        </UL>
        <B>Post-Condition(s)</B>
        <UL>
        <LI>none
        </UL>
        @param value  timestamp of start of interval
    **/
    //----------------------------------------------------------------------------
    public void setStartTime(EYSDate value)
    {                                   // begin setStartTime()
        startTime = value;
    }                                   // end setStartTime()

    //----------------------------------------------------------------------------
    /**
        Retrieves timestamp of end of interval. <P>
        <B>Pre-Condition(s)</B>
        <UL>
        <LI>none
        </UL>
        <B>Post-Condition(s)</B>
        <UL>
        <LI>none
        </UL>
        @return timestamp of end of interval
    **/
    //----------------------------------------------------------------------------
    public EYSDate getEndTime()
    {                                   // begin getEndTime()
        return(endTime);
    }                                   // end getEndTime()

    //----------------------------------------------------------------------------
    /**
        Sets timestamp of end of interval. <P>
        <B>Pre-Condition(s)</B>
        <UL>
        <LI>none
        </UL>
        <B>Post-Condition(s)</B>
        <UL>
        <LI>none
        </UL>
        @param value  timestamp of end of interval
    **/
    //----------------------------------------------------------------------------
    public void setEndTime(EYSDate value)
    {                                   // begin setEndTime()
        endTime = value;
    }                                   // end setEndTime()

    //----------------------------------------------------------------------------
    /**
        Retrieves totals for time interval. <P>
        <B>Pre-Condition(s)</B>
        <UL>
        <LI>none
        </UL>
        <B>Post-Condition(s)</B>
        <UL>
        <LI>none
        </UL>
        @return totals for time interval
    **/
    //----------------------------------------------------------------------------
    public FinancialTotalsIfc getTotals()
    {                                   // begin getTotals()
        return(totals);
    }                                   // end getTotals()

    //----------------------------------------------------------------------------
    /**
        Sets totals for time interval. <P>
        <B>Pre-Condition(s)</B>
        <UL>
        <LI>none
        </UL>
        <B>Post-Condition(s)</B>
        <UL>
        <LI>none
        </UL>
        @param value  totals for time interval
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
        String strResult = new String("Class:  TimeIntervalActivity (Revision " + 
                                      getRevisionNumber() +
                                      ") @" + 
                                      hashCode());
        strResult += "\n";
        // add attributes to string
        if (startTime == null)
        {
            strResult += "startTime:                          [null]\n";
        } 
        else
        {
            strResult += "startTime:                          [" + startTime.toString() + "]\n";
        }
        if (endTime == null)
        {
            strResult += "endTime:                            [null]\n";
        } 
        else
        {
            strResult += "endTime:                            [" + endTime.toString() + "]\n";
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
        TimeIntervalActivity main method. <P>
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
        TimeIntervalActivityIfc c = new TimeIntervalActivity();
        // output toString()
        System.out.println(c.toString());
    }                                   // end main()
}                                       // end class TimeIntervalActivity
