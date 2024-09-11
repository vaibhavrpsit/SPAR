/* ===========================================================================
* Copyright (c) 1998, 2013, Oracle and/or its affiliates. All rights reserved.
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/houseaccount/payment/PayHouseAccountCargo.java /main/13 2013/07/02 13:09:09 jswan Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    jswan     07/01/13 - Fixed failure to cancel House Account Transaction
 *                         when cancel button pressed or timout occurs.
 *    cgreene   07/26/11 - repacked into houseaccount.payment
 *    cgreene   05/26/10 - convert to oracle packaging
 *    cgreene   04/27/10 - XbranchMerge cgreene_refactor-duplicate-pos-classes
 *                         from st_rgbustores_techissueseatel_generic_branch
 *    abondala  01/03/10 - update header date
 *
 * ===========================================================================
 * $Log:
 *    8    360Commerce 1.7         1/17/2008 5:24:06 PM   Alan N. Sinton  CR
 *         29954: Refactor of EncipheredCardData to implement interface and be
 *          instantiated using a factory.
 *    7    360Commerce 1.6         12/18/2007 5:47:48 PM  Alan N. Sinton  CR
 *         29661: Changes per code review.
 *    6    360Commerce 1.5         11/27/2007 12:32:24 PM Alan N. Sinton  CR
 *         29661: Encrypting, masking and hashing account numbers for House
 *         Account.
 *    5    360Commerce 1.4         4/25/2007 8:52:18 AM   Anda D. Cadar   I18N
 *         merge
 *         
 *    4    360Commerce 1.3         1/22/2006 11:45:15 AM  Ron W. Haight
 *         removed references to com.ibm.math.BigDecimal
 *    3    360Commerce 1.2         3/31/2005 4:29:19 PM   Robert Pearse   
 *    2    360Commerce 1.1         3/10/2005 10:24:01 AM  Robert Pearse   
 *    1    360Commerce 1.0         2/11/2005 12:13:00 PM  Robert Pearse   
 *
 *   Revision 1.4  2004/09/27 22:32:04  bwf
 *   @scr 7244 Merged 2 versions of abstractfinancialcargo.
 *
 *   Revision 1.3  2004/02/12 16:51:29  mcs
 *   Forcing head revision
 *
 *   Revision 1.2  2004/02/11 21:52:05  rhafernik
 *   @scr 0 Log4J conversion and code cleanup
 *
 *   Revision 1.1.1.1  2004/02/11 01:04:19  cschellenger
 *   updating to pvcs 360store-current
 *
 *
 * 
 *    Rev 1.2   Jan 19 2004 15:55:06   DCobb
 * Use TenderableTransactionCargoIfc from oracle/retail/stores/pos/services/common/.
 * Resolution for 3701: Timing problem can occur in CancelTransactionSite (multiple).
 * 
 *    Rev 1.1   Dec 01 2003 18:44:24   nrao
 * Added changes for House Account Payment.
 * 
 *    Rev 1.0   Aug 29 2003 16:04:02   CSchellenger
 * Initial revision.
 * 
 *    Rev 1.0   Apr 29 2002 15:11:12   msg
 * Initial revision.
 * 
 *    Rev 1.0   Mar 18 2002 11:42:08   msg
 * Initial revision.
 * 
 *    Rev 1.1   Feb 05 2002 16:42:58   mpm
 * Modified to use IBM BigDecimal.
 * Resolution for POS SCR-1121: Employ IBM BigDecimal
 * 
 *    Rev 1.0   Sep 21 2001 11:32:20   msg
 * Initial revision.
 * 
 *    Rev 1.1   Sep 17 2001 13:10:50   msg
 * header update
 * ===========================================================================
 */
package oracle.retail.stores.pos.services.houseaccount.payment;

import java.math.BigDecimal;

import oracle.retail.stores.commerceservices.common.currency.CurrencyIfc;
import oracle.retail.stores.pos.services.common.AbstractFinancialCargo;
import oracle.retail.stores.pos.services.common.EmployeeCargoIfc;
import oracle.retail.stores.pos.services.common.TenderableTransactionCargoIfc;
import oracle.retail.stores.domain.employee.EmployeeIfc;
import oracle.retail.stores.domain.transaction.PaymentTransactionIfc;
import oracle.retail.stores.domain.transaction.SaleReturnTransactionIfc;
import oracle.retail.stores.domain.transaction.TenderableTransactionIfc;
import oracle.retail.stores.domain.utility.EYSDate;
import oracle.retail.stores.domain.utility.InstantCreditIfc;
import oracle.retail.stores.foundation.factory.FoundationObjectFactory;
import oracle.retail.stores.foundation.factory.FoundationObjectFactoryIfc;
import oracle.retail.stores.foundation.manager.device.EncipheredCardDataIfc;

/**
 * Holds all cargo information relevant for PayHouseAccount service.
 * 
 * @version $Revision: /main/13 $
 */
public class PayHouseAccountCargo extends AbstractFinancialCargo
    implements EmployeeCargoIfc, TenderableTransactionCargoIfc
{
    private static final long serialVersionUID = -7036847436892871883L;

    /**
     * revision number supplied by source-code control system
     */
    public static final String revisionNumber = "$Revision: /main/13 $";

    /**
     * first name
     */
    protected String firstName;

    /**
     * last name
     */
    protected String lastName;

    /**
     * Handle to EnciphredCardData
     */
    protected EncipheredCardDataIfc encipheredCardData;

    /**
     * employee ID
     */
    protected String employeeID;

    /**
     * balance due
     */
    protected BigDecimal balanceDue;

    /**
     * minimum payment due
     */
    protected BigDecimal minPaymentDue;

    /**
     * payment amount
     */
    protected BigDecimal paymentAmount;

    /**
     * credit limit
     */
    protected CurrencyIfc creditLimit;

    /**
     * last payment amount
     */
    protected CurrencyIfc lastPaymentAmount;

    /**
     * last payment date
     */
    protected EYSDate lastPaymentDate;

    /**
     * employee
     */
    protected EmployeeIfc employee;

    /**
     * Not opening the cash drawer, use this to make sure the user has time to
     * press Enter before leaving.
     */
    protected boolean waitForNext = false;

    /**
     * Indicator if UI needs to be refreshed with the recently linked customer
     * to the transaction.
     */
    boolean customerLinkRefreshUI = false;

    /**
     * Payment Transaction
     */
    protected PaymentTransactionIfc transaction = null;

    /**
     * InstantCredit
     */
    protected InstantCreditIfc instantCredit = null;

    /**
     * Sets the first name
     * 
     * @param firstName String representing first name
     */
    public void setFirstName(String firstName)
    {
        this.firstName = firstName;
    }

    /**
     * Returns the first name
     * 
     * @return String representing first name
     */
    public String getFirstName()
    {
        return firstName;
    }

    /**
     * Sets the last name
     * 
     * @param lastName String representing last name
     */
    public void setLastName(String lastName)
    {
        this.lastName = lastName;
    }

    /**
     * Returns the last name
     * 
     * @return String representing last name
     */
    public String getLastName()
    {
        return lastName;
    }

    /**
     * Retrieves the EncipheredCardData instance.
     * 
     * @return card number
     */
    public EncipheredCardDataIfc getEncipheredCardData()
    {
        if (encipheredCardData == null)
        {
            FoundationObjectFactoryIfc factory = FoundationObjectFactory.getFactory();
            encipheredCardData = factory.createEncipheredCardDataInstance();
        }
        return encipheredCardData;
    }

    /**
     * Sets the EncipheredCardData instance.
     * 
     * @param EncipheredCardData instance.
     */
    public void setEncipheredCardData(EncipheredCardDataIfc encipheredCardData)
    {
        this.encipheredCardData = encipheredCardData;
    }

    /**
     * Sets the balance due
     * 
     * @param balanceDue BigDecimal representing balance due
     */
    public void setBalanceDue(BigDecimal balanceDue)
    {
        this.balanceDue = balanceDue;
    }

    /**
     * Returns the balance due
     * 
     * @return BigDecimal representing balance due
     */
    public BigDecimal getBalanceDue()
    {
        return balanceDue;
    }

    /**
     * Sets the minimum payment due
     * 
     * @param minPaymentDue BigDecimal representing minimum payment due
     */
    public void setMinPaymentDue(BigDecimal minPaymentDue)
    {
        this.minPaymentDue = minPaymentDue;
    }

    /**
     * Returns the minimum payment due
     * 
     * @return BigDecimal representing minimum payment due
     */
    public BigDecimal getMinPaymentDue()
    {
        return minPaymentDue;
    }

    /**
     * Sets the payment amount
     * 
     * @param paymentAmount BigDecimal representing payment amount
     */
    public void setPaymentAmount(BigDecimal paymentAmount)
    {
        this.paymentAmount = paymentAmount;
    }

    /**
     * Returns the payment amount
     * 
     * @return BigDecimal representing payment amount
     */
    public BigDecimal getPaymentAmount()
    {
        return paymentAmount;
    }

    /**
     * Sets the transaction
     * 
     * @param transaction PaymentTransactionIfc representing transaction
     */
    public void setTransaction(PaymentTransactionIfc transaction)
    {
        this.transaction = transaction;
    }

    /**
     * Returns the transaction
     * 
     * @return PaymentTransactionIfc representing transaction
     */
    public PaymentTransactionIfc getTransaction()
    {
        return transaction;
    }

    /**
     * Returns the transaction
     * 
     * @return TenderableTransactionIfc
     */
    public TenderableTransactionIfc getTenderableTransaction()
    {
        return getTransaction();
    }

    /**
     * Returns the till ID
     * 
     * @return String representing till ID
     */
    public String getTillID()
    {
        return getRegister().getCurrentTill().getTillID();
    }

    /**
     * Returns the cashier
     * 
     * @return EmployeeIfc representing cashier
     */
    public EmployeeIfc getCashier()
    {
        return (getOperator());
    }

    /**
     * Sets the cashier
     * 
     * @param cashier String representing cashier
     */
    public void setCashier(EmployeeIfc cashier)
    {
        setOperator(cashier);
    }

    /**
     * Returns the employee ID
     * 
     * @return String representing employee ID
     */
    public String getEmployeeID()
    {
        return employeeID;
    }

    /**
     * Sets the employee ID
     * 
     * @param employeeID String representing employee ID
     */
    public void setEmployeeID(String employeeID)
    {
        this.employeeID = employeeID;
    }

    /**
     * Returns the employee
     * 
     * @return EmployeeIfc representing employee
     */
    public EmployeeIfc getEmployee()
    {
        return employee;
    }

    /**
     * Sets the employee
     * 
     * @param employee String representing employee
     */
    public void setEmployee(EmployeeIfc employee)
    {
        this.employee = employee;
    }

    /**
     * Returns the waitForNext flag
     * 
     * @return the waitForNext boolean flag
     */
    public boolean isWaitForNext()
    {
        return waitForNext;
    }

    /**
     * Sets the waitForNext flag
     * 
     * @param boolean value for the flag
     */
    public void setWaitForNext(boolean value)
    {
        waitForNext = value;
    }

    /**
     * Returns the customerLinkRefreshUI flag
     * 
     * @return boolean flag representing if UI needs to be refreshed.
     *         Specifically, the status bar needs to be updated with linked
     *         customer name.
     */
    public boolean isCustomerLinkRefreshUI()
    {
        return customerLinkRefreshUI;
    }

    /**
     * Sets the customerLinkRefreshUI flag
     * 
     * @param boolean value for the flag
     */
    public void setCustomerLinkRefreshUI(boolean value)
    {
        customerLinkRefreshUI = value;
    }

    /**
     * Still need this to conform with the interfaces
     * 
     * @return null
     */
    public SaleReturnTransactionIfc[] getOriginalReturnTransactions()
    {
        return null;
    }

    /**
     * Retrieves the source-code-control system revision number.
     * 
     * @return String representation of revision number
     */
    public String getRevisionNumber()
    {
        // return string
        return (revisionNumber);
    }

    /**
     * Gets the credit limit amount
     * 
     * @return creditLimit
     */
    public CurrencyIfc getCreditLimit()
    {
        return creditLimit;
    }

    /**
     * Gets the last payment amount
     * 
     * @return lastPaymentAmount
     */
    public CurrencyIfc getLastPaymentAmount()
    {
        return lastPaymentAmount;
    }

    /**
     * Gets the last payment date
     * 
     * @return lastPaymentDate
     */
    public EYSDate getLastPaymentDate()
    {
        return lastPaymentDate;
    }

    /**
     * Sets the credit limit
     * 
     * @param creditLimit CurrencyIfc
     */
    public void setCreditLimit(CurrencyIfc creditLimit)
    {
        this.creditLimit = creditLimit;
    }

    /**
     * Sets the last payment amount
     * 
     * @param lastPaymentAmount CurrencyIfc
     */
    public void setLastPaymentAmount(CurrencyIfc lastPaymentAmount)
    {
        this.lastPaymentAmount = lastPaymentAmount;
    }

    /**
     * Sets the last payment date
     * 
     * @param date EYSDate
     */
    public void setLastPaymentDate(EYSDate date)
    {
        lastPaymentDate = date;
    }

    /**
     * @return the instantCredit
     */
    public InstantCreditIfc getInstantCredit()
    {
        return instantCredit;
    }

    /**
     * @param instantCredit the instantCredit to set
     */
    public void setInstantCredit(InstantCreditIfc instantCredit)
    {
        this.instantCredit = instantCredit;
    }

} // end class PayHouseAccountCargo
