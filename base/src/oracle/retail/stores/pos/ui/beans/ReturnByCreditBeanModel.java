/* ===========================================================================
* Copyright (c) 1998, 2011, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/ui/beans/ReturnByCreditBeanModel.java /rgbustores_13.4x_generic_branch/1 2011/08/11 19:22:52 ohorne Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    ohorne    07/28/11 - intial version
 *
 * ===========================================================================
  */
package oracle.retail.stores.pos.ui.beans;

import java.util.ArrayList;

/**
 * Data transport between the bean and the application for return by credit card data
 */
@SuppressWarnings("serial")
public class ReturnByCreditBeanModel extends DataInputBeanModel
{
    protected String firstCardDigits = "";
    protected String lastCardDigits = "";
    protected String itemNumber = "";
    
    protected ArrayList<String> dateRangeList = new ArrayList<String>();
    protected int dateRangeIndex = -1;

    /**
     * Returns the First Card Digits
     * @return the digits
     */
    public String getFirstCardDigits()
    {
        return firstCardDigits;
    }
    
    /**
     * Sets the First Card Digits
     * @param digits 
     */
    public void setFirstCardDigits(String digits)
    {
        this.firstCardDigits = digits;
    }
    
    /**
     * Returns the Last Card Digits
     * @return the digits
     */
    public String getLastCardDigits()
    {
        return lastCardDigits;
    }
    
    /**
     * Sets the Last Card Digits
     * @param digits 
     */
    public void setLastCardDigits(String digits)
    {
        this.lastCardDigits = digits;
    }
    
    /**
     * Returns the Item Number
     * @return the number
     */
    public String getItemNumber()
    {
        return itemNumber;
    }
    
    /**
     * Set the item Number 
     * @param number
     */
    public void setItemNumber(String itemNumber)
    {
        this.itemNumber = itemNumber;
    }
    
    /**
     * Returns the Array of Data Range text
     * @return the data ranges
     */
    public String[] getDateRanges()
    {
        String[] dataRanges = new String[0];
        if (getDateRangeList() != null)
        {
            dataRanges = getDateRangeList().toArray(new String[getDateRangeList() .size()]);
        }
        return dataRanges ;
    }
    
    /**
     * Returns the Date Range text list 
     * @return the data ranges
     */
    public ArrayList<String> getDateRangeList()
    {
        return dateRangeList;
    }
    
    /**
     * Sets the list of Date Ranges
     * @param dateRangeList the list of date range text
     */
    public void setDateRangeList(ArrayList<String> list)
    {
        this.dateRangeList = list;
    }
    
    /**
     * Returns the index of selected Data Range 
     * @return the index of the selected item
     */
    public int getDateRangeIndex()
    {
        return dateRangeIndex;
    }
    
    /**
     * Sets the index of the selected Data Range
     * @param index the index of the selected item
     */
    public void setDateRangeIndex(int index)
    {
        this.dateRangeIndex = index;
    }
    
    @Override
    public String toString()
    {
        StringBuilder builder = new StringBuilder();
        builder.append("ReturnByCreditBeanModel [firstCardDigits=");
        builder.append(firstCardDigits);
        builder.append(", lastCardDigits=");
        builder.append(lastCardDigits);
        builder.append(", itemNumber=");
        builder.append(itemNumber);
        builder.append(", dateRangeIndex=");
        builder.append(dateRangeIndex);
        builder.append(", dateRangeList=");
        builder.append(dateRangeList);
        builder.append("]");
        return builder.toString();
    }
  
}
