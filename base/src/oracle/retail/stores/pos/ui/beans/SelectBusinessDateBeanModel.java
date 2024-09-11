/* ===========================================================================
* Copyright (c) 1998, 2011, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/ui/beans/SelectBusinessDateBeanModel.java /rgbustores_13.4x_generic_branch/1 2011/05/05 14:06:52 mszekely Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    cgreene   05/28/10 - convert to oracle packaging
 *    cgreene   05/27/10 - convert to oracle packaging
 *    cgreene   05/27/10 - convert to oracle packaging
 *    cgreene   05/26/10 - convert to oracle packaging
 *    abondala  01/03/10 - update header date
 *
 * ===========================================================================
 */
package oracle.retail.stores.pos.ui.beans;

// java imports
import java.io.Serializable;

import oracle.retail.stores.domain.utility.EYSDate;
import oracle.retail.stores.foundation.utility.Util;

//----------------------------------------------------------------------------
/**
     Bean model for SelectBusinessDateBean. <P>
     @version $KW=@(#); $Ver=pos_4.5.0:71; $EKW;
**/
//----------------------------------------------------------------------------
public class SelectBusinessDateBeanModel extends POSBaseBeanModel implements Serializable
{                                       // begin class SelectBusinessDateBeanModel
    // This id is used to tell
    // the compiler not to generate a
    // new serialVersionUID.
    //
    static final long serialVersionUID = -2820602212897450542L;

    /**
        revision number supplied by source-code-control system
    **/
    public static String revisionNumber = "$KW=@(#); $Ver=pos_4.5.0:71; $EKW;";
    /**
        selected business date
    **/
    protected EYSDate fieldSelectedDate = null;
    /**
        array of business dates
    **/
    protected EYSDate[] fieldBusinessDates = null;

    //---------------------------------------------------------------------
    /**
        Constructs SelectBusinessDateBeanModel object. <P>
    **/
    //---------------------------------------------------------------------
    public SelectBusinessDateBeanModel()
    {                                   // begin SelectBusinessDateBeanModel()
    }                                   // end SelectBusinessDateBeanModel()

    //----------------------------------------------------------------------------
    /**
        Retrieves selected business date. <P>
        @return selected business date
    **/
    //----------------------------------------------------------------------------
    public EYSDate getSelectedDate()
    {                                   // begin getSelectedDate()
        return(fieldSelectedDate);
    }                                   // end getSelectedDate()

    //----------------------------------------------------------------------------
    /**
        Sets selected business date. <P>
        @param value  selected business date
    **/
    //----------------------------------------------------------------------------
    public void setSelectedDate(EYSDate value)
    {                                   // begin setSelectedDate()
        fieldSelectedDate = value;
    }                                   // end setSelectedDate()

    //----------------------------------------------------------------------------
    /**
        Retrieves array of business dates. <P>
        @return array of business dates
    **/
    //----------------------------------------------------------------------------
    public EYSDate[] getBusinessDates()
    {                                   // begin getBusinessDates()
        return(fieldBusinessDates);
    }                                   // end getBusinessDates()

    //----------------------------------------------------------------------------
    /**
        Sets array of business dates. <P>
        @param value  array of business dates
    **/
    //----------------------------------------------------------------------------
    public void setBusinessDates(EYSDate[] value)
    {                                   // begin setBusinessDates()
        fieldBusinessDates = value;
    }                                   // end setBusinessDates()
  
    //---------------------------------------------------------------------
    /**
        Returns default display string. <P>
        @return String representation of object
    **/
    //---------------------------------------------------------------------
    public String toString()
    {                                   // begin toString()
        // build result string
        String strResult = new String("Class:  SelectBusinessDateBeanModel (Revision " + 
                                      getRevisionNumber() +
                                      ") @" + 
                                      hashCode());
        strResult += "\n";
        // add attributes to string
        if (fieldSelectedDate == null)
        {
            strResult += "fieldSelectedDate:                  [null]";
        } 
        else
        {
            strResult += fieldSelectedDate.toString();
        }
        if (fieldBusinessDates == null)
        {
            strResult += "fieldBusinessDates:                 [null]";
        } 
        else
        {
            strResult += fieldBusinessDates.toString();
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
        return(Util.parseRevisionNumber(revisionNumber));
    }                                   // end getRevisionNumber()

    //---------------------------------------------------------------------
    /**
        SelectBusinessDateBeanModel main method. <P>
        @param String args[]  command-line parameters
    **/
    //---------------------------------------------------------------------
    public static void main(String args[])
    {                                   // begin main()
        // instantiate class
        SelectBusinessDateBeanModel c = new SelectBusinessDateBeanModel();
        // output toString()
        System.out.println(c.toString());
    }                                   // end main()
}                                       // end class SelectBusinessDateBeanModel
