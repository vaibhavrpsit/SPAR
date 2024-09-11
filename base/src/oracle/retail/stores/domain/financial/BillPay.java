/* ===========================================================================
* Copyright (c) 2009, 2011, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    nkgautam  07/06/10 - bill pay changes
 *    nkgautam  06/22/10 - bill pay changes
 *    nkgautam  06/21/10 - initial version
 * ===========================================================================
 */
package oracle.retail.stores.domain.financial;

import java.util.ArrayList;

import oracle.retail.stores.commerceservices.common.currency.CurrencyIfc;
import oracle.retail.stores.domain.DomainGateway;
import oracle.retail.stores.domain.transaction.TransactionIDIfc;
import oracle.retail.stores.domain.utility.EYSDate;

public class BillPay implements BillPayIfc
{

    
    private static final long serialVersionUID = 4988317610419872653L;

    /**
     * List of Customer Bills
     */
    protected ArrayList<BillIfc> billsList = new ArrayList<BillIfc>();
    
    /**
     * Balance Due
     */
    protected CurrencyIfc balanceDue = null;

    /**
     * Business date
     */
    protected EYSDate businessDate = null;
    
    /**
     * Payment date
     */
    protected EYSDate paymentDate = null;


    /**
     * Account Number
     */
    protected String accountNumber = null;

    /**
     * Plan
     */
    protected String customerPlan = null;

    /**
     * First Name
     */
    protected String firstName = null;

    /**
     * Last Name
     */
    protected String lastName = null;

    /**
     * Address Line 1
     */
    protected String addressLine1 = null;

    /**
     * Address Line 2
     */
    protected String addressLine2 = null;

    /**
     * Postal code
     */
    protected String postalCode = null;

    /**
     * City
     */
    protected String city = null;

    /**
     * State
     */
    protected String state = null;
    
    /**
     * description
     */
    protected String description = "";
    
    /**
     * Customer Full Name
     */
    protected String customerName = null;
    
    /**
     * Constructor
     */
    public BillPay()
    {
        initialize();
    }

    /**
     * Initializes the class
     */
    public void initialize()
    {                                 
        businessDate = DomainGateway.getFactory().getEYSDateInstance();
        businessDate.setType(EYSDate.TYPE_DATE_ONLY);
        balanceDue = DomainGateway.getBaseCurrencyInstance();
        balanceDue.setZero();
    }   

    public Object clone()
    {                                   // begin clone()
        // instantiate new object
        BillPay c = new BillPay();
        // set values
        setCloneAttributes(c);
        // pass back Object
        return((Object) c);
    }

    /**
     * Gets the business date
     * @return
     */
    public EYSDate getBusinessDate()
    {
        return businessDate;
    }

    /**
     * Sets the business date
     * @param businessDate
     */
    public void setBusinessDate(EYSDate businessDate)
    {
        this.businessDate = businessDate;
    }

    /**
     * Gets the account number
     */
    public String getAccountNumber()
    {
        return accountNumber;
    }

    /**
     * Sets the account number
     */
    public void setAccountNumber(String accountNumber)
    {
        this.accountNumber = accountNumber;
    }

    /**
     * Gets the customer plan
     */
    public String getCustomerPlan()
    {
        return customerPlan;
    }

    /**
     * Sets the customer plan
     */
    public void setCustomerPlan(String customerPlan)
    {
        this.customerPlan = customerPlan;
    }

    /**
     * Gets tha balance due
     */
    public CurrencyIfc getBalanceDue()
    {
        return balanceDue;
    }

    /**
     * sets the balance due
     */
    public void setBalanceDue(CurrencyIfc balanceDue)
    {
        this.balanceDue = balanceDue;
    }

    /**
     * Gets the first name
     */
    public String getFirstName()
    {
        return firstName;
    }

    /**
     * Sete the first name
     */
    public void setFirstName(String firstName)
    {
        this.firstName = firstName;
    }

    /**
     * Gets the last name
     */
    public String getLastName()
    {
        return lastName;
    }

    /**
     * sets the last name
     */
    public void setLastName(String lastName)
    {
        this.lastName = lastName;
    }

    /**
     * Gets the address line 1
     */
    public String getAddressLine1()
    {
        return addressLine1;
    }

    /**
     * Sets the address line 1
     */
    public void setAddressLine1(String addressLine1)
    {
        this.addressLine1 = addressLine1;
    }

    /**
     * Gets the address line 2
     */
    public String getAddressLine2()
    {
        return addressLine2;
    }

    /**
     * Sets tha address line 2
     */
    public void setAddressLine2(String addressLine2)
    {
        this.addressLine2 = addressLine2;
    }

    /**
     * Gets the postal code
     */
    public String getPostalCode()
    {
        return postalCode;
    }

    /**
     * Sets the postal code
     */
    public void setPostalCode(String postalCode)
    {
        this.postalCode = postalCode;
    }

    /**
     * gets the city
     */
    public String getCity()
    {
        return city;
    }

    /**
     * sets the city
     */
    public void setCity(String city)
    {
        this.city = city;
    }

    /**
     * gets state
     */
    public String getState()
    {
        return state;
    }

    /**
     * sets state
     */
    public void setState(String state)
    {
        this.state = state;
    }
    
    /**
     * Gets the list of bill pertaining to a customer
     */
    public ArrayList<BillIfc> getBillsList()
    {
        return billsList;
    }

    /**
     * Sets the list of bill pertaining to a customer
     */
    public void setBillsList(ArrayList<BillIfc> billsList)
    {
        this.billsList = billsList;
    }

    /**
     * Gets the full Customer name
     */
    public String getFirstLastName()
    {
        if(getFirstName() != null)
        {
            String firstLastName = getFirstName() + " " + getLastName();
            return firstLastName;
        }
        else
        {
            return  customerName;
        }
    }
    
    /**
     * Sets the full name
     */
    public void setFirstLastName(String Name)
    {
        customerName = Name;
    }
    
    /**
     * Gets the bill description
     */
    public String getDescription()
    {
        return description;
    }

    /**
     * Sets the bill description
     */
    public void setDescription(String description)
    {
        this.description = description;
    }
    
    /**
     * @return the paymentDate
     */
    public EYSDate getPaymentDate()
    {
        return paymentDate;
    }

    /**
     * @param paymentDate the paymentDate to set
     */
    public void setPaymentDate(EYSDate paymentDate)
    {
        this.paymentDate = paymentDate;
    }
    
    /**
     * Sets clone attributes
     * @param newClass
     */
    public void setCloneAttributes(BillPay newClass)
    {                                   // begin setCloneAttributes()

    }



}
