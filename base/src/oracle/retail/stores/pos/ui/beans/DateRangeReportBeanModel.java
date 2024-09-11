/* ===========================================================================
* Copyright (c) 1998, 2011, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/ui/beans/DateRangeReportBeanModel.java /rgbustores_13.4x_generic_branch/1 2011/05/05 14:06:43 mszekely Exp $
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
 * $Log:
 *   3    360Commerce 1.2         3/31/2005 4:27:41 PM   Robert Pearse   
 *   2    360Commerce 1.1         3/10/2005 10:20:49 AM  Robert Pearse   
 *   1    360Commerce 1.0         2/11/2005 12:10:29 PM  Robert Pearse   
 *
 *  Revision 1.4  2004/04/09 13:59:07  cdb
 *  @scr 4206 Cleaned up class headers for logs and revisions.
 *
 *
 * ===========================================================================
 */
package oracle.retail.stores.pos.ui.beans;

// Domain imports
import oracle.retail.stores.domain.utility.EYSDate;

//----------------------------------------------------------------------------
/**
    This is the model for DateRangeReportBean
    @version $KW=@(#); $Ver=pos_4.5.0:2; $EKW;
**/
//----------------------------------------------------------------------------
public class DateRangeReportBeanModel extends POSBaseBeanModel
{
    // Revision number
    public static String revisionNumber = "$KW=@(#); $Ver=pos_4.5.0:2; $EKW;";

    // Start Business Date
    EYSDate fieldStartBusinessDate = null;
    // End Business Date
    EYSDate fieldEndBusinessDate = null;

    //----------------------------------------------------------------------------
    /**
        Get the value of the StartBusinessDate field
        @return the value of StartBusinessDate
    **/
    //----------------------------------------------------------------------------
    public EYSDate getStartBusinessDate()
    {
        return fieldStartBusinessDate;
    }

    //----------------------------------------------------------------------------
    /**
        Get the value of the EndBusinessDate field
        @return the value of EndBusinessDate
    **/
    //----------------------------------------------------------------------------
    public EYSDate getEndBusinessDate()
    {
        return fieldEndBusinessDate;
    }
    
    //----------------------------------------------------------------------------
    /**
        Sets the StartBusinessDate field
        @param startBusinessDate value to be set for StartBusinessDate
    **/
    //----------------------------------------------------------------------------
    public void setStartBusinessDate(EYSDate startBusinessDate)
    {
        fieldStartBusinessDate = startBusinessDate;
    }
    
    //----------------------------------------------------------------------------
    /**
        Sets the EndBusinessDate field
        @param endBusinessDate value to be set for EndBusinessDate
    **/
    //----------------------------------------------------------------------------
    public void setEndBusinessDate(EYSDate endBusinessDate)
    {
        fieldEndBusinessDate = endBusinessDate;
    }
    
    //----------------------------------------------------------------------------
    /**
        Converts to a String representing the data in this Object
        @return String representing the data in this Object
    **/
    //----------------------------------------------------------------------------
    public String toString()
    {
        StringBuffer buff = new StringBuffer();

        buff.append("Class: DateRangeReportBeanModel Revision: " + revisionNumber + "\n");
        buff.append("StartBusinessDate [" + fieldStartBusinessDate + "]\n");
        buff.append("EndBusinessDate [" + fieldEndBusinessDate + "]\n");

        return(buff.toString());
    }
}
