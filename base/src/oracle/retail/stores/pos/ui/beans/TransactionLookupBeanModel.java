/* ===========================================================================
* Copyright (c) 1998, 2011, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/ui/beans/TransactionLookupBeanModel.java /rgbustores_13.4x_generic_branch/1 2011/05/05 14:06:59 mszekely Exp $
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
 */
package oracle.retail.stores.pos.ui.beans;

// domain imports
import oracle.retail.stores.domain.DomainGateway;
import oracle.retail.stores.domain.utility.EYSDate;

//--------------------------------------------------------------------------
/**
    This is the model used to search for transactions in the ejournal
    <P>
    @version $KW=@(#); $Ver=pos_4.5.0:50; $EKW;
**/
//--------------------------------------------------------------------------
public class TransactionLookupBeanModel extends POSBaseBeanModel
{
    /**
        Revision number for this class
    **/
    public static final String revisionNumber = "$KW=@(#); $Ver=pos_4.5.0:50; $EKW:";
    /** start transaction field with initial value **/
    protected String fieldStartTransaction = new String("");
    /** end transaction field with initial value **/
    protected String fieldEndTransaction = new String("");
    /** start date field  **/
    protected EYSDate fieldStartDate = null;
    /** end date field **/
    protected EYSDate fieldEndDate = null;
    /** start time field **/
    protected EYSDate fieldStartTime = null;
    /** end time field **/
    protected EYSDate fieldEndTime = null;
    
    /** register number **/
    protected String fieldRegisterNumber = new String("");
    
    /** casher id field with initial value **/
    protected String fieldCashierID = new String("");
    /** sales associate id field with initial value **/
    protected String fieldSalesAssociateID = new String("");
    /** focus field with initial value **/
    protected String focusField = new String("");
    
    protected boolean allowRegisterNumberField = true;

    /** Whether or not two dates should be linked **/
    protected boolean datesLinked = false;
    /** Date to return if dates are linked, and both dates are blank **/
    protected EYSDate defaultDate = null;

    //------------------------------------------------------------------------------
    /**
       TransactionLookupBeanModel constructor. Sets the initial start date month to
       -1 and the initial start time hour to -1, to indicate these are the default
       values and to display a blank string.
    **/
    //------------------------------------------------------------------------------
    public TransactionLookupBeanModel()
    {
        super();
        fieldStartDate = DomainGateway.getFactory().getEYSDateInstance();
        fieldStartTime = DomainGateway.getFactory().getEYSDateInstance();
        fieldEndDate = DomainGateway.getFactory().getEYSDateInstance();
        fieldEndTime = DomainGateway.getFactory().getEYSDateInstance();
        fieldStartDate.initialize(EYSDate.TYPE_DATE_ONLY);
        fieldEndDate.initialize(EYSDate.TYPE_DATE_ONLY);
        fieldStartTime.initialize(EYSDate.TYPE_TIME_ONLY);
        fieldEndTime.initialize(EYSDate.TYPE_TIME_ONLY);
    }
    //------------------------------------------------------------------------------
    /**
       Gets the StartTransaction field value.
       @return The StartTransaction field value.
       @see #setStartTransaction
    **/
    //------------------------------------------------------------------------------
    public String getStartTransaction()
    {
        return fieldStartTransaction;
    }
    //------------------------------------------------------------------------------
    /**
       Gets the EndTransaction field value.
       @return The EndTransaction field value.
       @see #setEndTransaction
    **/
    //------------------------------------------------------------------------------
    public String getEndTransaction()
    {
        return fieldEndTransaction;
    }
    //------------------------------------------------------------------------------
    /**
       Gets the StartDate field value.
       @return The StartDate field value.
       @see #setStartDate
    **/
    //------------------------------------------------------------------------------
    public EYSDate getStartDate()
    {
        EYSDate returnDate = fieldStartDate;
        if(getDatesLinked() && fieldStartDate == null)
        {
            returnDate = defaultDate;
        }
        return returnDate;
    }
    //------------------------------------------------------------------------------
    /**
       Gets the EndDate field value.
       @return The EndDate field value.
       @see #setEndDate
    **/
    //------------------------------------------------------------------------------
    public EYSDate getEndDate()
    {
        EYSDate returnDate = fieldEndDate;
        if(getDatesLinked() && fieldEndDate == null)
        {
            returnDate = defaultDate;
        }
        return returnDate;
    }
    //------------------------------------------------------------------------------
    /**
       Gets the StartTime field value.
       @return The StartTime field value.
       @see #setStartTime
    **/
    //------------------------------------------------------------------------------
    public EYSDate getStartTime() {
        return fieldStartTime;
    }
    //------------------------------------------------------------------------------
    /**
       Gets the EndTime field value.
       @return The EndTime field value.
       @see #setEndTime
    **/
    //------------------------------------------------------------------------------
    public EYSDate getEndTime()
    {
        return fieldEndTime;
    }
    //------------------------------------------------------------------------------
    /**
       Gets the RegisterNumber field value.
       @return The RegisterNumber field value.
       @see #setRegisterNumber
    **/
    //------------------------------------------------------------------------------
    public String getRegisterNumber()
    {
        return fieldRegisterNumber;
    }
    //------------------------------------------------------------------------------
    /**
       Gets the CashierID field value.
       @return The CashierID field value.
       @see #setCashierID
    **/
    //------------------------------------------------------------------------------
    public String getCashierID()
    {
        return fieldCashierID;
    }
    //------------------------------------------------------------------------------
    /**
       Gets the SalesAssociateID field value.
       @return The SalesAssociateID field value.
       @see #setSalesAssociateID
    **/
    //------------------------------------------------------------------------------
    public String getSalesAssociateID()
    {
        return fieldSalesAssociateID;
    }
    //------------------------------------------------------------------------------
    /**
       Gets the focusField property(java.lang.String) value.
       @return The focusField field value.
       @see #setFocusField
    **/
    //------------------------------------------------------------------------------
    public String getFocusField()
    {
        return focusField;
    }
    
    //------------------------------------------------------------------------------
    /**
       Gets the allowRegisterNumberField property value.
       @return The allowRegisterNumberField property value.
    **/
    //------------------------------------------------------------------------------
    public boolean getAllowRegisterNumberField()
    {
        return allowRegisterNumberField;
    }
    
    //------------------------------------------------------------------------------
    /**
       Sets the StartTransaction field value.
       @param starttransaction The new value for the property.
       @see #getStartTransaction
    **/
    //------------------------------------------------------------------------------
    public void setStartTransaction(String starttransaction)
    {
        fieldStartTransaction = starttransaction;
    }
    //------------------------------------------------------------------------------
    /**
       Sets the EndTransaction field value.
       @param endtransaction The new value for the property.
       @see #getEndTransaction
    **/
    //------------------------------------------------------------------------------
    public void setEndTransaction(String endtransaction)
    {
        fieldEndTransaction = endtransaction;
    }
    //------------------------------------------------------------------------------
    /**
       Sets the StartDate field value.
       @param startdate The new value for the property.
       @see #getStartDate
    **/
    //------------------------------------------------------------------------------
    public void setStartDate(EYSDate startdate)
    {
        fieldStartDate = startdate;
    }
    //------------------------------------------------------------------------------
    /**
       Sets the EndDate field value.
       @param enddate The new value for the property.
       @see #getEndDate
    **/
    //------------------------------------------------------------------------------
    public void setEndDate(EYSDate enddate)
    {
        fieldEndDate = enddate;
    }
    //------------------------------------------------------------------------------
    /**
       Sets the StartTime field value.
       @param starttime The new value for the property.
       @see #getStartTime
    **/
    //------------------------------------------------------------------------------
    public void setStartTime(EYSDate starttime)
    {
        fieldStartTime = starttime;
    }
    //------------------------------------------------------------------------------
    /**
       Sets the EndTime field value.
       @param endtime The new value for the property.
       @see #getEndTime
    **/
    //------------------------------------------------------------------------------
    public void setEndTime(EYSDate endtime)
    {
        fieldEndTime = endtime;
    }
    //------------------------------------------------------------------------------
    /**
       Sets the RegisterNumber field value.
       @param registerNumber The new value for the property.
       @see #getRegisterNumber
    **/
    //------------------------------------------------------------------------------
    public void setRegisterNumber(String registerNumber)
    {
        fieldRegisterNumber = registerNumber;
    }
    //------------------------------------------------------------------------------
    /**
       Sets the CashierID field value.
       @param cashierID The new value for the property.
       @see #getCashierID
    **/
    //------------------------------------------------------------------------------
    public void setCashierID(String cashierID)
    {
        fieldCashierID = cashierID;
    }
    //------------------------------------------------------------------------------
    /**
       Sets the SalesAssociateID field value.
       @param salesassociateID The new value for the property.
       @see #getSalesAssociateID
    **/
    //------------------------------------------------------------------------------
    public void setSalesAssociateID(String salesassociateID)
    {
        fieldSalesAssociateID = salesassociateID;
    }
    //------------------------------------------------------------------------------
    /**
       Sets the focusField field value.
       @param focusField The new value for the property.
       @see #getFocusField
    **/
    //------------------------------------------------------------------------------
    public void setFocusField(String focusfield)
    {
        focusField = focusfield;
    }
    
    //------------------------------------------------------------------------------
    /**
       Sets the allowRegisterNumberField value.
       @param show true or false
    **/
    //------------------------------------------------------------------------------
    public void setAllowRegisterNumberField(boolean show)
    {
        allowRegisterNumberField = show;
    }
    
    /**
     * If dates are linked, then if both start date and end date are
     * empty, validation should succeed. If only one field is non-blank
     * then a validation error should occur.
     * 
     * If dates are not linked, then a blank field generates an validation error
     * no matter what.
     *  
     *  @return datesLinked
     */
    public boolean getDatesLinked()
    {
        return this.datesLinked;
    }
    
    /**
     * Set whether to link the dates.
     * If dates are linked, then if both start date and end date are
     * empty, validation should succeed. If only one field is non-blank
     * then a validation error should occur.
     * 
     * If dates are not linked, then a blank field generates an validation error
     * no matter what.
     *  
     * @param value
     * @param defaultDate date to use if both dates are blank
     */
    public void setupLinkedDates(boolean value, EYSDate defaultDate)
    {
        this.datesLinked = value;
        this.defaultDate = defaultDate;
    }
    
    /**
     * Return the defaultDate to use when both dates are blank
     *  
     *  @return defaultDate
     */
    public EYSDate getDefaultDate()
    {
        return this.defaultDate;
    }
    //------------------------------------------------------------------------------
    /**
       Returns a String that represents the value of this object.
       @return a string representation of the receiver
    **/
    //------------------------------------------------------------------------------
    public String toString()
    {
        StringBuffer buf=new StringBuffer("TransactionLookupBeanModel{\n");
        buf.append("Start Transaction=").append(getStartTransaction()).append("\n");
        buf.append("End Transaction=").append(getEndTransaction()).append("\n");
        buf.append("Start Date=").append(getStartDate()).append("\n");
        buf.append("End Date=").append(getEndDate()).append("\n");
        buf.append("Start Time=").append(getStartTime()).append("\n");
        buf.append("End Time=").append(getEndTime()).append("\n");
        buf.append("Register Number=").append(getRegisterNumber()).append("\n");
        buf.append("User ID=").append(getCashierID()).append("\n");
        buf.append("Sales Associate ID=").append(getSalesAssociateID()).append("\n");
        buf.append("focusfield=").append(getFocusField()).append("\n");
        buf.append("}\n");
        return buf.toString();
    }
}
