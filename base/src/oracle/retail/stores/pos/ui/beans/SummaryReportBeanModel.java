/* ===========================================================================
* Copyright (c) 1998, 2011, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/ui/beans/SummaryReportBeanModel.java /rgbustores_13.4x_generic_branch/1 2011/05/05 14:06:44 mszekely Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    cgreene   05/27/10 - convert to oracle packaging
 *    cgreene   05/27/10 - convert to oracle packaging
 *    cgreene   05/26/10 - convert to oracle packaging
 *    abondala  01/03/10 - update header date
 *
 * ===========================================================================
 * $Log:
 *    3    360Commerce 1.2         3/31/2005 4:30:16 PM   Robert Pearse   
 *    2    360Commerce 1.1         3/10/2005 10:25:40 AM  Robert Pearse   
 *    1    360Commerce 1.0         2/11/2005 12:14:34 PM  Robert Pearse   
 *
 *   Revision 1.3  2004/03/16 17:15:18  build
 *   Forcing head revision
 *
 *   Revision 1.2  2004/02/11 20:56:27  rhafernik
 *   @scr 0 Log4J conversion and code cleanup
 *
 *   Revision 1.1.1.1  2004/02/11 01:04:23  cschellenger
 *   updating to pvcs 360store-current
 *
 *
 * 
 *    Rev 1.0   Aug 29 2003 16:12:40   CSchellenger
 * Initial revision.
 * 
 *    Rev 1.1   Aug 14 2002 21:22:02   baa
 * retrieve report types from the site
 * Resolution for POS SCR-1740: Code base Conversions
 * 
 *    Rev 1.0   Apr 29 2002 14:52:02   msg
 * Initial revision.
 * 
 *    Rev 1.0   Mar 18 2002 11:57:56   msg
 * Initial revision.
 * 
 *    Rev 1.2   Mar 07 2002 20:44:42   mpm
 * Externalized text for report UI screens.
 * Resolution for POS SCR-351: Internationalization
 * ===========================================================================
 */
package oracle.retail.stores.pos.ui.beans;
// java imports
import java.util.ArrayList;

import oracle.retail.stores.domain.utility.EYSDate;

//----------------------------------------------------------------------------
/**
    This is the model for getting summary report coordinates.
    @version $Revision: /rgbustores_13.4x_generic_branch/1 $
**/
//----------------------------------------------------------------------------
public class SummaryReportBeanModel extends POSBaseBeanModel
{
    // Revision number
    public static String revisionNumber = "$Revision: /rgbustores_13.4x_generic_branch/1 $";

    // String constant for choosing a till summary report
    public static final String TILL = "Till";

    // String constant for choosing a register summary report
    public static final String REGISTER = "Register";

    // String constant for choosing a store summary report
    public static final String STORE = "Store";

    // String constant for choosing a till summary report
    public static final String TILL_LABEL = "TillLabel";

    // String constant for choosing a register summary report
    public static final String REGISTER_LABEL = "RegisterLabel";

    // String constant for choosing a store summary report
    public static final String STORE_LABEL = "StoreLabel";

    // Number of report types
    public static final int MAX_TYPES    = 3;

    // Field for entering the business date
    protected EYSDate fieldBusinessDate = null;

    // Filed for selecting the summary report type
    protected String fieldSelectedType = STORE;

    // Field for entering the till or register number
    protected String fieldTillRegNumber = "";

    protected ArrayList reportTypes = null;

    //----------------------------------------------------------------------------
    /**
        Get the value of the BusinessDate field
        @return the value of BusinessDate
    **/
    //----------------------------------------------------------------------------
    public EYSDate getBusinessDate()
    {
        return fieldBusinessDate;
    }

     //----------------------------------------------------------------------------
    /**
        Sets the report types for this bean
        @param reports an array of report types
    **/
    //----------------------------------------------------------------------------
    public void setReportTypesModel(ArrayList model)
    {
        reportTypes = model;
    }  

    //----------------------------------------------------------------------------
    /**
        Sets the report types for this bean
        @param reports an array of report types
    **/
    //----------------------------------------------------------------------------
    public ArrayList getReportTypesModel()
    {
        return reportTypes;
    } 
    
    //----------------------------------------------------------------------------
    /**
        Get the value of the SelectedType field
        @return the value of SelectedType
    **/
    //----------------------------------------------------------------------------
    public String getSelectedType()
    {
        return fieldSelectedType;
    }
    //----------------------------------------------------------------------------
    /**
        Get the value of the TillRegNumber field
        @return the value of TillRegNumber
    **/
    //----------------------------------------------------------------------------
    public String getTillRegNumber()
    {
        return fieldTillRegNumber;
    }
    //----------------------------------------------------------------------------
    /**
        Sets the BusinessDate field
        @param the value to be set for BusinessDate
    **/
    //----------------------------------------------------------------------------
    public void setBusinessDate(EYSDate BusinessDate)
    {
        fieldBusinessDate = BusinessDate;
    }
    //----------------------------------------------------------------------------
    /**
        Sets the SelectedType field
        @param the value to be set for SelectedType
    **/
    //----------------------------------------------------------------------------
    public void setSelectedType(String selectedType)
    {
        fieldSelectedType = selectedType;
    }
    //----------------------------------------------------------------------------
    /**
        Sets the TillRegNumber field
        @param the value to be set for TillRegNumber
    **/
    //----------------------------------------------------------------------------
    public void setTillRegNumber(String tillRegNumber)
    {
        fieldTillRegNumber = tillRegNumber;
    }
    //----------------------------------------------------------------------------
    /**
        Converts to a string representing the data in this Object
        @return string representing the data in this Object
    **/
    //----------------------------------------------------------------------------
    public String toString()
    {
        StringBuffer buff = new StringBuffer();

        buff.append("Class: SummaryReportBeanModel Revision: " + revisionNumber + "\n");
        buff.append("BusinessDate [" + fieldBusinessDate + "]\n");
        buff.append("SelectedType [" + fieldSelectedType + "]\n");
        buff.append("TillRegNumber [" + fieldTillRegNumber + "]\n");

        return(buff.toString());
    }
}
