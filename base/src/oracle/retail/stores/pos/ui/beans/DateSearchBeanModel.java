/* ===========================================================================
* Copyright (c) 1998, 2011, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/ui/beans/DateSearchBeanModel.java /rgbustores_13.4x_generic_branch/1 2011/05/05 14:06:55 mszekely Exp $
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
 *   3    360Commerce 1.2         3/31/2005 4:27:41 PM   Robert Pearse   
 *   2    360Commerce 1.1         3/10/2005 10:20:49 AM  Robert Pearse   
 *   1    360Commerce 1.0         2/11/2005 12:10:29 PM  Robert Pearse   
 *
 *  Revision 1.5  2004/04/09 13:59:07  cdb
 *  @scr 4206 Cleaned up class headers for logs and revisions.
 *
 *
 * ===========================================================================
 */
package oracle.retail.stores.pos.ui.beans;

// java imports
import oracle.retail.stores.domain.DomainGateway;
import oracle.retail.stores.domain.utility.EYSDate;

//----------------------------------------------------------------------------
/** This bean model is used by DateSearch bean.
    @version $KW=@(#); $Ver=pos_4.5.0:80; $EKW;
**/
//----------------------------------------------------------------------------
public class DateSearchBeanModel extends POSBaseBeanModel
{
    /** 
        Indicates whether to clear the date fields, default true.
    **/
    private boolean clearUIFields = true;
    /**
        revision number
    **/    
    public static String revisionNumber = "$KW=@(#); $Ver=pos_4.5.0:80; $EKW;";
    // start date field
    EYSDate startDateField = null;
    // end date field
    EYSDate endDateField   = null;
    
     //----------------------------------------------------------------------------
    /**
        Get the value of the StartDate field
        @return the value of StartDate
    **/
    //----------------------------------------------------------------------------
    public EYSDate getStartDate()
    {
        return startDateField;
    }
     //----------------------------------------------------------------------------
    /**
        Updates the StartDate field by setting the hours to 0, the minutes to 0,
        and the seconds to 0. Returns the updated start date for the search.
        Does not change the value of the start date field.
        @return the updated value of StartDate - mm/dd/yyyy 00:00:00
    **/
    //----------------------------------------------------------------------------
    public EYSDate getUpdatedStartDate()
    {
        EYSDate updatedStartDate = DomainGateway.getFactory().getEYSDateInstance();
        if (startDateField != null)
        {
            updatedStartDate.initialize(startDateField.getYear(),
                       startDateField.getMonth(),
                       startDateField.getDay(),
                       00,
                       00,
                       00);
        }
        
        return updatedStartDate;
    }
    //----------------------------------------------------------------------------
    /**
        Get the value of the EndDate field with 23:59:59 added to it.
        @return the value of EndDate
    **/
    //----------------------------------------------------------------------------
    public EYSDate getEndDate()
    {
        return endDateField;
    }
     //----------------------------------------------------------------------------
    /**
        Updates the EndDate field by setting the hours to 0, the minutes to 0,
        and the seconds to 0. Returns the updated end date for the search.
        Does not change the value of the end date field.
        @return the updated value of EndDate - mm/dd/yyyy 23:59:59
    **/
    //----------------------------------------------------------------------------
    public EYSDate getUpdatedEndDate()
    {
        EYSDate updatedEndDate = DomainGateway.getFactory().getEYSDateInstance();
        if (endDateField != null)
        {
            updatedEndDate.initialize(endDateField.getYear(),
                       endDateField.getMonth(),
                       endDateField.getDay(),
                       23,
                       59,
                       59);
        }

        return updatedEndDate;
    }
    //----------------------------------------------------------------------------        
    /**
        Sets the StartDate field
        @param the value to be set for StartDate
    **/
    //----------------------------------------------------------------------------
    public void setStartDate(EYSDate startDate)
    {
        startDateField = startDate;
    }
    //----------------------------------------------------------------------------
    /**
        Sets the EndDate field
        @param the value to be set for endDate
    **/
    //----------------------------------------------------------------------------
    public void setEndDate(EYSDate endDate)
    {
        endDateField = endDate;
    }
    /**
        Set clearUIFields flag to determine whether to clear the date fields. <P>
        @param boolean.
    **/
    //--------------------------------------------------------------------- 
    public void setclearUIFields(boolean value)
    {                                  // begin setclearUIFields()
        clearUIFields = value;
    }                                  // end setclearUIFields()

    //---------------------------------------------------------------------
    /**
        Returns the current valud of clearUIFields.<P>
        @return value of clearUIFields flag.
    **/
    //--------------------------------------------------------------------- 
    public boolean getclearUIFields()
    {                                  // begin getclearUIFields()
        return(clearUIFields);
    }                                  // end getclearUIFields()                                  // end getEmailDetail()
    
    //----------------------------------------------------------------------------
    /**
        Converts to a String representing the data in this Object
        @returns String representing the data in this Object
    **/
    //----------------------------------------------------------------------------
    public String toString()
    {
        StringBuffer buff = new StringBuffer();

        buff.append("Class: DateSearchBeanModel Revision: " + revisionNumber + "\n");
        buff.append("StartDate [" + startDateField + "]\n");
        buff.append("EndDate [" + endDateField + "]\n");

        return(buff.toString());
    }
} ///:~ end class DateSearchBeanModel
