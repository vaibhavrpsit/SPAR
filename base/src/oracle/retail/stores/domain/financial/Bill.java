/* ===========================================================================
* Copyright (c) 2009, 2011, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    nkgautam  07/02/10 - bill pay report changes
 *    nkgautam  06/22/10 - bill pay changes
 *    nkgautam  06/21/10 - initial draft
 * ===========================================================================
 */
package oracle.retail.stores.domain.financial;

import oracle.retail.stores.commerceservices.common.currency.CurrencyIfc;
import oracle.retail.stores.domain.utility.EYSDate;


public class Bill implements BillIfc
{

    /**
     * 
     */
    private static final long serialVersionUID = -2712182987085450608L;

    /**
     * Account Number
     */
    protected String accountNumber = null;

    /**
     * Bill Number
     */
    protected String billNumber = null;
    
    /**
     * Service Type
     */
    protected String serviceType = null;

    /**
     * Customer Bill Start date
     */
    protected EYSDate startDate =  null;

    /**
     * Customer Bill End date
     */
    protected EYSDate endDate = null;
    
    /**
     * Bill Due Date
     */
    protected EYSDate dueDate = null;

    /**
     * Total bill Amount 
     */
    protected CurrencyIfc billAmount = null;
    
    /*
     * Balance due
     */
    protected CurrencyIfc balanceDue = null;
    
    /**
     * Minimum Payment due
     */
    protected CurrencyIfc minimumPaymentDue = null;
    
    /**
     * Bill Amount paid
     */
    protected CurrencyIfc billAmountPaid =  null;
    
    /**
     * Telephone Number
     */
    protected String telephoneNumber  = null;
    
    /**
     * Bill Status
     */
    protected String billStatus = null;
    
    /**
     * Bill Date
     */
    protected EYSDate billDate = null;
    
    /**
     * Customer Name
     */
    protected String customerName = null;
    
    /**
     * Transaction identifier
     */
    protected String transactionID = null;

    /**
     * gets the Transaction ID
     * @return
     */
    public String getTransactionID()
    {
        return transactionID;
    }

    /**
     * sets the transaction ID
     * @param transactionID
     */
    public void setTransactionID(String transactionID)
    {
        this.transactionID = transactionID;
    }

    /**
     * gets the Customer name
     * @return
     */
    public String getCustomerName()
    {
        return customerName;
    }

    /**
     * Sets the customer name
     * @param customerName
     */
    public void setCustomerName(String customerName)
    {
        this.customerName = customerName;
    }

    /**
     * gets the billing date
     */
    public EYSDate getBillDate()
    {
        return billDate;
    }

    /**
     * sets the billing date
     */
    public void setBillDate(EYSDate billDate)
    {
        this.billDate = billDate;
    }

    /**
     * Gets the Account Number
     * @return
     */
    public String getAccountNumber()
    {
        return accountNumber;
    }

    /**
     * Sets the Account Number
     * @param accountNumber
     */
    public void setAccountNumber(String accountNumber)
    {
        this.accountNumber = accountNumber;
    }
    
    /**
     * Gets the bill number
     * @return
     */
    public String getBillNumber()
    {
        return billNumber;
    }

    /**
     * Sets the bill Number
     * @param billNumber
     */
    public void setBillNumber(String billNumber)
    {
        this.billNumber = billNumber;
    }

    /**
     * Gets the start Date
     * @return
     */
    public EYSDate getStartDate()
    {
        return startDate;
    }

    /**
     * Sets the start date
     * @param startDate
     */
    public void setStartDate(EYSDate startDate)
    {
        this.startDate = startDate;
    }

    /**
     * Gets the End date
     * @return
     */
    public EYSDate getEndDate()
    {
        return endDate;
    }

    /**
     * Sets the End Date
     * @param endDate
     */
    public void setEndDate(EYSDate endDate)
    {
        this.endDate = endDate;
    }

    /**
     * gets the bill Amount
     * @return
     */
    public CurrencyIfc getBillAmount()
    {
        return billAmount;
    }

    /**
     * sets the bill amount
     * @param billAmount
     */
    public void setBillAmount(CurrencyIfc billAmount)
    {
        this.billAmount = billAmount;
    }

    /**
     * gets the balance due
     * @return
     */
    public CurrencyIfc getBalanceDue()
    {
        return balanceDue;
    }

    /**
     * sets the balance due
     * @param balanceDue
     */
    public void setBalanceDue(CurrencyIfc balanceDue)
    {
        this.balanceDue = balanceDue;
    }

    /**
     * gets Minimum payment due
     * @return
     */
    public CurrencyIfc getMinimumPaymentDue()
    {
        return minimumPaymentDue;
    }

    /**
     * sets the minimum payment due
     * @param minimumPaymentDue
     */
    public void setMinimumPaymentDue(CurrencyIfc minimumPaymentDue)
    {
        this.minimumPaymentDue = minimumPaymentDue;
    }

    /**
     * Gets the bill amt paid
     * @return
     */
    public CurrencyIfc getBillAmountPaid()
    {
        return billAmountPaid;
    }

    /**
     * Sets the bill amount paid
     * @param billAmountPaid
     */
    public void setBillAmountPaid(CurrencyIfc billAmountPaid)
    {
        this.billAmountPaid = billAmountPaid;
    }

    /**
     * Gets the telephone number
     * @return
     */
    public String getTelephoneNumber()
    {
        return telephoneNumber;
    }

    /**
     * Sets the telephone Number
     * @param telephoneNumber
     */
    public void setTelephoneNumber(String telephoneNumber)
    {
        this.telephoneNumber = telephoneNumber;
    }
    
    /**
     * Gets the service type
     * @return
     */
    public String getServiceType()
    {
        return serviceType;
    }

    /**
     * Sets the Service type
     * @param serviceType
     */
    public void setServiceType(String serviceType)
    {
        this.serviceType = serviceType;
    }

    /**
     * Gets the due date
     * @return
     */
    public EYSDate getDueDate()
    {
        return dueDate;
    }

    /**
     * Sets the bill due date
     * @param dueDate
     */
    public void setDueDate(EYSDate dueDate)
    {
        this.dueDate = dueDate;
    }

    /**
     * Gets the bill Status
     * @return
     */
    public String getBillStatus()
    {
        return billStatus;
    }

    /**
     * Sets the bill status
     * @param billStatus
     */
    public void setBillStatus(String billStatus)
    {
        this.billStatus = billStatus;
    }



}
