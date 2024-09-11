/* ===========================================================================
* Copyright (c) 1998, 2011, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/dailyoperations/businessdate/BusinessDateCargo.java /rgbustores_13.4x_generic_branch/1 2011/05/05 14:06:17 mszekely Exp $
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
 *    3    360Commerce 1.2         3/31/2005 4:27:18 PM   Robert Pearse   
 *    2    360Commerce 1.1         3/10/2005 10:19:53 AM  Robert Pearse   
 *    1    360Commerce 1.0         2/11/2005 12:09:42 PM  Robert Pearse   
 *
 *   Revision 1.3  2004/02/12 16:49:35  mcs
 *   Forcing head revision
 *
 *   Revision 1.2  2004/02/11 21:45:31  rhafernik
 *   @scr 0 Log4J conversion and code cleanup
 *
 *   Revision 1.1.1.1  2004/02/11 01:04:15  cschellenger
 *   updating to pvcs 360store-current
 *
 *
 * 
 *    Rev 1.0   Aug 29 2003 15:56:14   CSchellenger
 * Initial revision.
 * 
 *    Rev 1.0   Apr 29 2002 15:31:40   msg
 * Initial revision.
 * 
 *    Rev 1.1   Mar 18 2002 23:13:18   msg
 * - updated copyright
 * 
 *    Rev 1.0   Mar 18 2002 11:26:08   msg
 * Initial revision.
 * 
 *    Rev 1.0   Sep 21 2001 11:16:50   msg
 * Initial revision.
 * 
 *    Rev 1.1   Sep 17 2001 13:05:58   msg
 * header update
 * ===========================================================================
 */
package oracle.retail.stores.pos.services.dailyoperations.businessdate;

// foundation imports
import oracle.retail.stores.domain.utility.EYSDate;
import oracle.retail.stores.foundation.tour.ifc.CargoIfc;

//------------------------------------------------------------------------------
/**
    This cargo holds the information necessary to the BusinessDate service. <P>
    It consists of a businessDateList, which is populated by the launch
    shuttle.  If more than one entries are in the list, a screen is provided
    which allows the operator to choose from one of the dates. <P>
    If only one entry is provided, the operator is prompted to accept the default date 
    or enter a new date.  The advance-date-flag indicates whether the default date
    should be advanced one day. <P>
    @version $Revision: /rgbustores_13.4x_generic_branch/1 $
**/
//------------------------------------------------------------------------------
public class BusinessDateCargo implements CargoIfc
{                                       // begin class BusinessDateCargo
    /**
        revision number supplied by Team Connection
    **/
    public static final String revisionNumber = "$Revision: /rgbustores_13.4x_generic_branch/1 $";
    /**
        business date list
    **/
    protected EYSDate[] businessDateList = null;
    /**
        default business date
    **/
    protected EYSDate defaultBusinessDate = null;
    /**
        indicates default business date is to be advanced from date in list
    **/
    protected boolean advanceDateFlag = true;
    /**
        input business date
    **/
    protected EYSDate inputBusinessDate = null;
    /**
        selected business date
    **/
    protected EYSDate selectedBusinessDate = null;
    
    // database offline flag
    protected boolean databaseOffline = false;
    
    //----------------------------------------------------------------------------
    /**
        Retrieves default business date. <P>
        @return business date list
    **/
    //----------------------------------------------------------------------------
    public EYSDate[] getBusinessDateList()
    {                                   // begin getBusinessDateList()
        return(businessDateList);
    }                                   // end getBusinessDateList()

    //----------------------------------------------------------------------------
    /**
        Sets default business date. <P>
        @param value  business date list
    **/
    //----------------------------------------------------------------------------
    public void setBusinessDateList(EYSDate[] value) 
    {                                   // begin setBusinessDateList()
        businessDateList = value;
    }                                   // end setBusinessDateList()

    //----------------------------------------------------------------------------
    /**
        Retrieves advance-date indicator. <P>
        @return advance-date indicator 
    **/
    //----------------------------------------------------------------------------
    public boolean isAdvanceDateFlag()
    {                                   // begin isAdvanceDateFlag()
        return(advanceDateFlag);
    }                                   // end isAdvanceDateFlag()

    //----------------------------------------------------------------------------
    /**
        Sets advance-date indicator. <P>
        @param value  advance-date indicator 
    **/
    //----------------------------------------------------------------------------
    public void setAdvanceDateFlag(boolean value) 
    {                                   // begin setAdvanceDateFlag()
        advanceDateFlag = value;
    }                                   // end setAdvanceDateFlag()

    //----------------------------------------------------------------------------
    /**
        Retrieves default business date. <P>
        @return default business date 
    **/
    //----------------------------------------------------------------------------
    public EYSDate getDefaultBusinessDate()
    {                                   // begin getDefaultBusinessDate()
        return(defaultBusinessDate);
    }                                   // end getDefaultBusinessDate()

    //----------------------------------------------------------------------------
    /**
        Sets default business date. <P>
        @param value  default business date 
    **/
    //----------------------------------------------------------------------------
    public void setDefaultBusinessDate(EYSDate value) 
    {                                   // begin setDefaultBusinessDate()
        defaultBusinessDate = value;
    }                                   // end setDefaultBusinessDate()
    
    //----------------------------------------------------------------------------
    /**
        Retrieves input business date. <P>
        @return input business date 
    **/
    //----------------------------------------------------------------------------
    public EYSDate getInputBusinessDate()
    {                                   // begin getInputBusinessDate()
        return(inputBusinessDate);
    }                                   // end getInputBusinessDate()

    //----------------------------------------------------------------------------
    /**
        Sets input business date. <P>
        @param value  input business date 
    **/
    //----------------------------------------------------------------------------
    public void setInputBusinessDate(EYSDate value) 
    {                                   // begin setInputBusinessDate()
        inputBusinessDate = value;
    }                                   // end setInputBusinessDate()

    //----------------------------------------------------------------------------
    /**
        Retrieves selected business date. <P>
        @return selected business date 
    **/
    //----------------------------------------------------------------------------
    public EYSDate getSelectedBusinessDate()
    {                                   // begin getSelectedBusinessDate()
        return(selectedBusinessDate);
    }                                   // end getSelectedBusinessDate()

    //----------------------------------------------------------------------------
    /**
        Sets selected business date. <P>
        @param value  selected business date 
    **/
    //----------------------------------------------------------------------------
    public void setSelectedBusinessDate(EYSDate value) 
    {                                   // begin setSelectedBusinessDate()
        selectedBusinessDate = value;       
    }                                   // end setSelectedBusinessDate()

    //----------------------------------------------------------------------------
    /**
        Sets selected databaseOffline flag. <P>
        @param value  boolean value 
    **/
    //----------------------------------------------------------------------------
    public void setDatabaseOffline(boolean value)
    {
        databaseOffline = value;   
    }
    
    //----------------------------------------------------------------------------
    /**
        Returns true if the database is offline, false otherwise. <P>
        @return flag indicating if the database is offline 
    **/
    //----------------------------------------------------------------------------
    public boolean isDatabaseOffline()
    {
        boolean offlineFlag = false;
        
        if (databaseOffline)
        {
            offlineFlag = true;
        }
        
        return(offlineFlag);
    }
        
    //----------------------------------------------------------------------
    /**
        Returns a string representation of this object. <P>
        @return String representation of object
    **/
    //----------------------------------------------------------------------
    public String toString()
    {                                   // begin toString()
        // result string
        String strResult = new String("Class:  BusinessDateCargo (Revision " +
                                      getRevisionNumber() +
                                      ") @" + hashCode());
        if (businessDateList == null)
        {
            strResult += "Business date list:                    [null]\n";
        }
        else
        {
            strResult += "Business date list:\n";
            int n = businessDateList.length;
            for (int i = 0; i < n; i++)
            {
                strResult += "  Entry " + i + ":                             [" + businessDateList[i] + "]\n";
            }
        }
        if (defaultBusinessDate == null)
        {
            strResult += "Default business date:                 [null]\n";
        }
        else
        {
            strResult += "Default business date:                 [" + defaultBusinessDate + "]\n";
        }
        strResult += "Advance date flag:                     [" + advanceDateFlag + "]\n";
        if (inputBusinessDate == null)
        {
            strResult += "Input business date:                   [null]\n";
        }
        else
        {
            strResult += "Input business date:                   [" + inputBusinessDate + "]\n";
        }
        // pass back result
        return(strResult);
    }                                   // end toString()

    //----------------------------------------------------------------------
    /**
        Returns the revision number of the class. <P>
        @return String representation of revision number
    **/
    //----------------------------------------------------------------------
    public String getRevisionNumber()
    {                                   // begin getRevisionNumber()
        // return string
        return(revisionNumber);
    }                                   // end getRevisionNumber()

}                                       // end class BusinessDateCargo
