/* ===========================================================================
* Copyright (c) 1998, 2013, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/ui/beans/OrderStatusReportBeanModel.java /main/1 2013/01/15 18:46:25 sgu Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    sgu       01/15/13 - add back order status report
 *    sgu       01/15/13 - add back order status report
 *    cgreene   05/27/10 - convert to oracle packaging
 *    cgreene   05/26/10 - convert to oracle packaging
 *    abondala  01/03/10 - update header date
 *
 * ===========================================================================
 */
package oracle.retail.stores.pos.ui.beans;

import oracle.retail.stores.domain.utility.EYSDate;

//----------------------------------------------------------------------------
/**
    This is model for requesting order reports.
    @version $KW=@(#); $Ver=pos_4.5.0:2; $EKW;
**/
//----------------------------------------------------------------------------
public class OrderStatusReportBeanModel extends POSBaseBeanModel
{
    /**
        revision number supplied by source-code-control system
    **/
    public static final String revisionNumber = "$KW=@(#); $Ver=pos_4.5.0:2; $EKW;";
    /**
        This member holds the status selected value.
    **/
    protected String selectedOrderStatus = null;
    /**
        Start business date
    **/
    EYSDate fieldStartBusinessDate = null;
    /**
        End business date
    **/
    EYSDate fieldEndBusinessDate = null;

    //----------------------------------------------------------------------------
    /**
        Get the value of the SelectedOrderStatus field
        @return the value of SelectedOrderStatus
    **/
    //----------------------------------------------------------------------------
    public String getSelectedOrderStatus()
    {
        return selectedOrderStatus;
    }
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
        Sets the SelectedOrderStatus field
        @param the value to be set for SelectedOrderStatus
    **/
    //----------------------------------------------------------------------------
    public void setSelectedOrderStatus(String value)
    {
        selectedOrderStatus = value;
    }
    //----------------------------------------------------------------------------
    /**
        Sets the StartBusinessDate field
        @param the value to be set for StartBusinessDate
    **/
    //----------------------------------------------------------------------------
    public void setStartBusinessDate(EYSDate startBusinessDate)
    {
        fieldStartBusinessDate = startBusinessDate;
    }
    //----------------------------------------------------------------------------
    /**
        Sets the EndBusinessDate field
        @param the value to be set for EndBusinessDate
    **/
    //----------------------------------------------------------------------------
    public void setEndBusinessDate(EYSDate endBusinessDate)
    {
        fieldEndBusinessDate = endBusinessDate;
    }
    //----------------------------------------------------------------------------
    /**
        Converts to a String representing the data in this Object
        @returns String representing the data in this Object
    **/
    //----------------------------------------------------------------------------
    public String toString()
    {
        StringBuffer buff = new StringBuffer();

        buff.append("Class: OrderStatusReportBeanModel Revision: " + revisionNumber + "\n");
        buff.append("OrderStatus [" + selectedOrderStatus + "]\n");
        buff.append("StartBusinessDate [" + fieldStartBusinessDate + "]\n");
        buff.append("EndBusinessDate [" + fieldEndBusinessDate + "]\n");

        return(buff.toString());
    }
}
