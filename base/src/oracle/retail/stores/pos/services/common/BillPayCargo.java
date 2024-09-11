/* ===========================================================================
* Copyright (c) 2009, 2011, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    nkgautam  09/20/10 - refractored code to use a single class for checking
 *                         cash in drawer
 *    nkgautam  07/27/10 - bill pay enhancement changes
 *    nkgautam  06/24/10 - bill pay changes
 *    nkgautam  06/21/10 - A new tour cargo class for bill pay
 * ===========================================================================
 */
package oracle.retail.stores.pos.services.common;

import java.util.ArrayList;

import oracle.retail.stores.domain.employee.EmployeeIfc;
import oracle.retail.stores.domain.financial.BillPayIfc;
import oracle.retail.stores.domain.transaction.BillPayTransactionIfc;
import oracle.retail.stores.foundation.tour.ifc.CargoIfc;
import oracle.retail.stores.pos.services.common.AbstractFinancialCargo;


public class BillPayCargo extends AbstractFinancialCargo implements CargoIfc 
{

    private static final long serialVersionUID = 5328782452053541757L;

    /**
     * transaction object
     */
    protected BillPayTransactionIfc transaction;
    
    /**
     * List of Bill pay customers
     */
    protected ArrayList<BillPayIfc> billPayMultiCustomers = null;

    /**
     * sales associate
     */
    protected EmployeeIfc salesAssociate;

    /**
     * employee granting Security override
     */
    protected EmployeeIfc securityOverrideEmployee;

    /**
     * employee attempting Security override
     */
    protected EmployeeIfc securityOverrideRequestEmployee;

    /**
     * employee attempting Access
     */
    protected EmployeeIfc accessEmployee;
    
    /**
     * transaction initialized indicator
     */
    protected boolean transactionInitialized = false;

    /**
     * Bill Number
     */
    protected String billNumber=null;
    
    /**
     * Account Number
     */
    protected String accountNumber=null;
    
    /**
     * Telephone Number
     */
    protected String telephoneNumber=null;
    
    /**
     * First Name
     */
    protected String firstName=null;
    
    /**
     * Last Name
     */
    protected String lastName=null;
    
    /**
     * Postal Code
     */
    protected String postalCode=null;
    
    /**
     * Email ID
     */
    protected String email=null;
    
    /**
     * Selected row 
     */
    protected int selectRow = -1;
    
    /**
     * Billing System Online/Offline Status
     */
    protected boolean billingSystemOnline = false;
    
    
    /**
     * Is billing system online
     * @return
     */
    public boolean isBillingSystemOnline()
    {
        return billingSystemOnline;
    }

    /**
     * Sets the billing system status
     * @param billingSystemOnline
     */
    public void setBillingSystemOnline(boolean billingSystemOnline)
    {
        this.billingSystemOnline = billingSystemOnline;
    }

    /**
     * gets the selected row
     * @return
     */
    public int getSelectRow()
    {
        return selectRow;
    }

    /**
     * set teh selected row
     * @param selectRow
     */
    public void setSelectRow(int selectRow)
    {
        this.selectRow = selectRow;
    }

    /**
     * The list of Bills
     */
    protected BillPayIfc[] billPayInfoList = null;

    /**
     * gets the list of billpayifc
     * @return
     */
    public BillPayIfc[] getBillPayInfoList()
    {
        return billPayInfoList;
    }

    /**
     * sets the billpayifc list
     * @param a_billPayInfoList
     */
    public void setBillPayInfoList(BillPayIfc[] a_billPayInfoList)
    {
        if (a_billPayInfoList == null)
            return;
        // update list
        billPayInfoList = new BillPayIfc[a_billPayInfoList.length];
        System.arraycopy(a_billPayInfoList, 0, billPayInfoList, 0, a_billPayInfoList.length);
    }

    /**
     * sets BillPayTransaction
     * @param value
     */
    public void setTransaction(BillPayTransactionIfc value) {
        transaction = value;
    }

    /**
     * gets the BillpayTransaction
     * @return
     */
    public BillPayTransactionIfc getTransaction() {
        return (transaction);
    }

    /**
     * sets the sales associate
     */
    public void setSalesAssociate(EmployeeIfc value) {
        salesAssociate = value;
    }

    /**
     * gets the sales associate
     */
    public EmployeeIfc getSalesAssociate() {
        return (salesAssociate);
    }

    /**
     * gets the security override employee
     * @return
     */
    public EmployeeIfc getSecurityOverrideEmployee()
    {                                   // begin getSecurityOverrideEmployee()
        return securityOverrideEmployee;
    }                                   // end getSecurityOverrideEmployee()

    /**
     * Sets the security override employee object.
     * @param  value  The security override employee object.
     */
    public void setSecurityOverrideEmployee(EmployeeIfc value)
    {                                   // begin setSecurityOverrideEmployee()
        securityOverrideEmployee = value;
    }                                   // end setSecurityOverrideEmployee()
    
    /**
     * Returns the securityOverrideRequestEmployee object.
     * @return The securityOverrideRequestEmployee object.
     */
    public EmployeeIfc getSecurityOverrideRequestEmployee()
    {                                   // begin getSecurityOverrideRequestEmployee()
        return securityOverrideRequestEmployee;
    }                                   // end getSecurityOverrideRequestEmployee()

    /**
     *  Sets the securityOverrideRequestEmployee object.
     *  @param  value  securityOverrideRequestEmployee object.
     */
    public void setSecurityOverrideRequestEmployee(EmployeeIfc value)
    {                                   // begin setSecurityOverrideRequestEmployee()
        securityOverrideRequestEmployee = value;
    }                                   // end setSecurityOverrideRequestEmployee()

    /**
     * The access employee returned by this cargo is the currently
     * logged on cashier or an Override Security Employee
     */
    public void setAccessEmployee(EmployeeIfc value)
    {
        accessEmployee = value;
    }
    
    /**
     * The access employee returned by this cargo is the currently
     * logged on cashier or an Override Security Employee
     * @return the EmployeeIfc value
     */
    public EmployeeIfc getAccessEmployee()
    {
        return accessEmployee;
    }

    /**
     * sets the bill number
     * @param billNumber
     */
    public void setBillNumber(String billNumber) {
        this.billNumber = billNumber;
    }

    /**
     * gets the bill number
     * @return
     */
    public String getBillNumber() {
        return billNumber;
    }

    /**
     * sets the account number
     * @param accountNumber
     */
    public void setAccountNumber(String accountNumber) {
        this.accountNumber = accountNumber;
    }

    /**
     * gets the account number
     * @return
     */
    public String getAccountNumber() {
        return accountNumber;
    }

    /**
     * sets the First name
     * @param firstName
     */
    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    /**
     * gets the first name
     * @return
     */
    public String getFirstName() {
        return firstName;
    }

    /**
     * sets the last name
     * @param lastName
     */
    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    /**
     * gets the last name
     * @return
     */
    public String getLastName() {
        return lastName;
    }

    /**
     * sets the postal code
     * @param postalCode
     */
    public void setPostalCode(String postalCode) {
        this.postalCode = postalCode;
    }

    /**
     * gets the postal code
     * @return
     */
    public String getPostalCode() {
        return postalCode;
    }

    /**
     * sets Email
     * @param email
     */
    public void setEmail(String email) {
        this.email = email;
    }

    /**
     * gets the Email
     * @return
     */
    public String getEmail() {
        return email;
    }

    /**
     * sets the telephone Number
     * @param telephoneNumber
     */
    public void setTelephoneNumber(String telephoneNumber) {
        this.telephoneNumber = telephoneNumber;
    }

    /**
     * gets the telephone number
     * @return
     */
    public String getTelephoneNumber() {
        return telephoneNumber;
    }
    
    /**
     * gets the list of BillPayIfc 
     * @return
     */
    public ArrayList<BillPayIfc> getBillPayMultiCustomers() {
        return billPayMultiCustomers;
    }

    /**
     * sets the list of billpayifc
     * @param billPayMultiCustomers
     */
    public void setBillPayMultiCustomers(ArrayList<BillPayIfc> billPayMultiCustomers) {
        this.billPayMultiCustomers = billPayMultiCustomers;
    }
    
    /**
     * gets the till id
     * @return
     */
    public String getTillID()
    {
        return getRegister().getCurrentTill().getTillID();
    }

    /**
     * Returns whether transaction is initialized or not
     * @return
     */
    public boolean isTransactionInitialized()
    {
        return transactionInitialized;
    }

    /**
     * sets the transaction initialized boolean
     * @param transactionInitialized
     */
    public void setTransactionInitialized(boolean transactionInitialized)
    {
        this.transactionInitialized = transactionInitialized;
    }
   
}
