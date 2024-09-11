/* ===========================================================================
* Copyright (c) 2003, 2013, Oracle and/or its affiliates. All rights reserved.
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/instantcredit/InstantCreditCargo.java /main/15 2013/10/15 14:16:20 asinton Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    asinton   10/10/13 - removed references to social security number and
 *                         replaced with locale agnostic government id
 *    sgu       05/23/11 - move inquiry for payment into instantcredit service
 *    sgu       05/20/11 - refactor instant credit inquiry flow
 *    sgu       05/16/11 - move instant credit approval status to its own class
 *    sgu       05/11/11 - fix instant credit cargo to use the new reponse
 *                         object
 *    sgu       05/02/11 - use the new AuthorizeInstantCreditResponseIfc in the
 *                         instant credit cargo
 *    cgreene   05/26/10 - convert to oracle packaging
 *    cgreene   04/27/10 - XbranchMerge cgreene_refactor-duplicate-pos-classes
 *                         from st_rgbustores_techissueseatel_generic_branch
 *    abondala  01/03/10 - update header date
 *    kulu      01/26/09 - Fix the bug that House Account enroll response data
 *                         don't have padding translation at enroll franking
 *                         slip
 *
 * ===========================================================================
 * $Log:
 * 4    360Commerce 1.3         4/25/2007 8:52:25 AM   Anda D. Cadar   I18N
 *      merge
 *
 * 3    360Commerce 1.2         3/31/2005 4:28:23 PM   Robert Pearse
 * 2    360Commerce 1.1         3/10/2005 10:22:07 AM  Robert Pearse
 * 1    360Commerce 1.0         2/11/2005 12:11:25 PM  Robert Pearse
 *
 *Revision 1.4  2004/09/27 22:32:04  bwf
 *@scr 7244 Merged 2 versions of abstractfinancialcargo.
 *
 *Revision 1.3  2004/02/12 16:50:40  mcs
 *Forcing head revision
 *
 *Revision 1.2  2004/02/11 21:51:22  rhafernik
 *@scr 0 Log4J conversion and code cleanup
 *
 *Revision 1.1.1.1  2004/02/11 01:04:16  cschellenger
 *updating to pvcs 360store-current
 *
 *
 *
 *    Rev 1.1   Nov 24 2003 19:22:22   nrao
 * Code Review Changes.
 *
 * ===========================================================================
 */
package oracle.retail.stores.pos.services.instantcredit;

import oracle.retail.stores.commerceservices.common.currency.CurrencyIfc;
import oracle.retail.stores.domain.customer.CustomerIfc;
import oracle.retail.stores.domain.employee.EmployeeIfc;
import oracle.retail.stores.domain.manager.payment.AuthorizeInstantCreditResponseIfc;
import oracle.retail.stores.domain.transaction.TransactionIfc;
import oracle.retail.stores.domain.utility.InstantCreditApprovalStatus;
import oracle.retail.stores.domain.utility.InstantCreditIfc;
import oracle.retail.stores.foundation.manager.device.EncipheredDataIfc;
import oracle.retail.stores.pos.services.common.AbstractFinancialCargo;
import oracle.retail.stores.pos.services.common.EmployeeCargoIfc;

/**
 * This class represents the cargo for Instant Credit. <>P>
 *
 * @version $Revision: /main/15 $
 */
public class InstantCreditCargo extends AbstractFinancialCargo implements EmployeeCargoIfc
{
    private static final long serialVersionUID = -8250554526049789492L;

    /** revision number supplied by version control **/
    public static final String revisionNumber = "$Revision: /main/15 $";

    public final static int UNASSIGNED = -1;

    // these constants determine which of the House Account Options has been
    // selected
    protected static int PROCESS_ENROLL = 0;

    protected static int PROCESS_INQUIRY = 1;

    protected static int PROCESS_TEMP_PASS = 2;

    protected static int PROCESS_REFERENCE = 3;

    protected static int PROCESS_PAYMENT = 4;

    /**
     * Reference Number
     */
    protected String referenceNumber = null;

    /**
     * default option is House Account Inquiry
     */
    protected int process = PROCESS_INQUIRY;

    /**
     * transaction
     */
    protected TransactionIfc transaction = null;

    /**
     * Flag that determines instant credit enrollment is approved
     */
    protected boolean approved = true;

    /**
     * Government ID
     */
    protected EncipheredDataIfc governmentId = null;

    /**
     * Customer
     */
    protected CustomerIfc customer = null;

    /**
     * Employee Id
     */
    protected String employeeID = null;

    /**
     * Employee
     */
    protected EmployeeIfc employee = null;

    /**
     * Card Color Selected
     */
    protected String cardColor = null;

    /**
     * This flag determines if application has been franked
     */
    protected boolean franked = false;

    /**
     * InstantCredit
     */
    protected InstantCreditIfc instantCredit = null;

    /**
     * Specific response string
     */
    protected String response = null;

    /**
     * Approval status
     */

    protected InstantCreditApprovalStatus approvalStatus;

    /**
     * This flag determines whether a transaction has been saved
     */
    protected boolean transactionSaved = false;

    /**
     * Response returned by Authorizer
     */
    protected AuthorizeInstantCreditResponseIfc instantCreditResponse = null;

    /**
     * Tender amount applied to instant credit
     */
    protected CurrencyIfc tenderAmount = null;

    /**
     * Flag determines if final tender is split
     */
    protected boolean splitFinalTender = false;

    /**
     * Flag determines if tender is final
     */
    protected boolean finalTender = false;

    /**
     * Flag determines if this is first run for Instant Credit
     */
    protected boolean firstRun = true;

    /**
     * Flag determines if this is an initial request
     */
    protected boolean initialRequest = true;

    /**
     * This flag is to keep track of whether a build request is executed more
     * than once in AuthorizeEnrollment -- If it is more than once, then it is a
     * Call Reference or DataEntryRetry code which has been returned by the
     * authorizer otherwise it is any other code.
     */
    protected boolean buildRequest = false;

    /**
     * This flag indicates whether we're to turn training ON or OFF when we
     * return from overriding access to training mode settings. true -> Turn it
     * on false -> Turn it off
     */
    protected boolean trainingOnRequested = false;

    /**
     * Card name swiped
     */
    protected String cardName = null;

    /**
     * Home phone number
     */
    protected String homePhone = null;


    /**
     * zip code
     */
    protected String zipCode = null;

    /**
     * Returns the revision number of the class.
     *
     * @return String representation of revision number
     */
    public String getRevisionNumber()
    {
        return (revisionNumber);
    }

    /**
     * Gets transaction data
     *
     * @return transaction
     */
    public TransactionIfc getTransaction()
    {
        return transaction;
    }

    /**
     * Sets transaction data
     *
     * @param transaction
     */
    public void setTransaction(TransactionIfc transaction)
    {
        this.transaction = transaction;
    }

    /**
     * Determines if an application is approved
     *
     * @return approved
     */
    public boolean isApproved()
    {
        return approved;
    }

    /**
     * Sets approved value to be true or false
     *
     * @param approved
     */
    public void setApproved(boolean approved)
    {
        this.approved = approved;
    }

    /**
     * Gets AuthorizeInstantCreditResponseIfc data from Authorizer
     *
     * @return authorizeInstantCreditResponse
     */
    public AuthorizeInstantCreditResponseIfc getInstantCreditResponse()
    {
        return instantCreditResponse;
    }

    /**
     * Sets AuthorizeInstantCreditResponseIfc data
     *
     * @param response
     */
    public void setInstantCreditResponse(AuthorizeInstantCreditResponseIfc response)
    {
        this.instantCreditResponse = response;
    }

    /**
     * Returns the <code>governmentId</code> value.
     * @return the governmentId
     */
    public EncipheredDataIfc getGovernmentId()
    {
        return governmentId;
    }

    /**
     * Sets the <code>governmentId</code> value.
     * @param governmentId the governmentId to set
     */
    public void setGovernmentId(EncipheredDataIfc governmentId)
    {
        this.governmentId = governmentId;
    }

    /**
     * Gets the selected option under House Account Options
     *
     * @return process
     */
    public int getProcess()
    {
        return process;
    }

    /**
     * Sets the selected process under House Account Options
     *
     * @param process
     */
    public void setProcess(int process)
    {
        this.process = process;
    }

    /**
     * Gets the customer data
     *
     * @return customer
     */
    public CustomerIfc getCustomer()
    {
        return customer;
    }

    /**
     * Sets the customer data
     *
     * @param customer
     */
    public void setCustomer(CustomerIfc customer)
    {
        this.customer = customer;
    }

    /**
     * Gets the card color selected
     *
     * @return cardColor
     */
    public String getCardColor()
    {
        return cardColor;
    }

    /**
     * Sets the card color
     *
     * @param cardColor
     */
    public void setCardColor(String cardColor)
    {
        this.cardColor = cardColor;
    }

    /**
     * Gets the employee id number
     *
     * @see oracle.retail.stores.pos.services.common.EmployeeCargoIfc#getEmployeeID()
     */
    public String getEmployeeID()
    {
        return employeeID;
    }

    /**
     * Sets the employee id number
     *
     * @see oracle.retail.stores.pos.services.common.EmployeeCargoIfc#setEmployeeID(java.lang.String)
     */
    public void setEmployeeID(String employeeID)
    {
        this.employeeID = employeeID;
    }

    /**
     * Gets the Employee data
     *
     * @see oracle.retail.stores.pos.services.common.EmployeeCargoIfc#getEmployee()
     */
    public EmployeeIfc getEmployee()
    {
        return employee;
    }

    /**
     * Sets the Employee data
     *
     * @see oracle.retail.stores.pos.services.common.EmployeeCargoIfc#setEmployee(oracle.retail.stores.domain.employee.EmployeeIfc)
     */
    public void setEmployee(EmployeeIfc employee)
    {
        this.employee = employee;
    }

    /**
     * Gets the boolean whether franking is done
     *
     * @return franked
     */
    public boolean isFranked()
    {
        return franked;
    }

    /**
     * Sets the boolean for franking
     *
     * @param franked
     */
    public void setFranked(boolean franked)
    {
        this.franked = franked;
    }

    /**
     * Gets the InstantCredit data
     *
     * @return instantCredit
     */
    public InstantCreditIfc getInstantCredit()
    {
        return instantCredit;
    }

    /**
     * Sets the InstantCredit data
     *
     * @param instantCredit
     */
    public void setInstantCredit(InstantCreditIfc instantCredit)
    {
        this.instantCredit = instantCredit;
    }

    /**
     * Gets the reference number
     *
     * @return referenceNumber
     */
    public String getReferenceNumber()
    {
        return referenceNumber;
    }

    /**
     * Sets the reference number
     *
     * @param referenceNumber
     */
    public void setReferenceNumber(String referenceNumber)
    {
        this.referenceNumber = referenceNumber;
    }

    /**
     * Gets the response String
     *
     * @return response
     */
    public String getResponse()
    {
        return response;
    }

    /**
     * Sets the response String
     *
     * @param response
     */
    public void setResponse(String response)
    {
        this.response = response;
    }

    /**
     * Determines whether transaction is saved
     *
     * @return transactionSaved
     */
    public boolean isTransactionSaved()
    {
        return transactionSaved;
    }

    /**
     * Sets the transactionSaved boolean value
     *
     * @param transactionSaved
     */
    public void setTransactionSaved(boolean transactionSaved)
    {
        this.transactionSaved = transactionSaved;
    }

    /**
     * Gets the tender amount
     *
     * @return tenderAmount
     */
    public CurrencyIfc getTenderAmount()
    {
        return tenderAmount;
    }

    /**
     * Sets the tender amount
     *
     * @param tenderAmount
     */
    public void setTenderAmount(CurrencyIfc tenderAmount)
    {
        this.tenderAmount = tenderAmount;
    }

    /**
     * Determines if tender amount is split between instant credit & other
     * tenders
     *
     * @return splitFinalTender
     */
    public boolean isSplitFinalTender()
    {
        return splitFinalTender;
    }

    /**
     * Sets the boolean whether tender amount is split between instant credit &
     * other tenders or not
     *
     * @param splitFinalTender
     */
    public void setSplitFinalTender(boolean splitFinalTender)
    {
        this.splitFinalTender = splitFinalTender;
    }

    /**
     * Determines whether tender is final
     *
     * @return finalTender
     */
    public boolean isFinalTender()
    {
        return finalTender;
    }

    /**
     * Sets whether tender is final
     *
     * @param finalTender
     */
    public void setFinalTender(boolean finalTender)
    {
        this.finalTender = finalTender;
    }

    /**
     * Determines whether instant credit enrollment has been run before
     *
     * @return firstRun
     */
    public boolean isFirstRun()
    {
        return firstRun;
    }

    /**
     * Sets firstRun value
     *
     * @param firstRun
     */
    public void setFirstRun(boolean firstRun)
    {
        this.firstRun = firstRun;
    }

    /**
     * Determines whether instant credit enrollment is first request
     *
     * @return initialRequest
     */
    public boolean isInitialRequest()
    {
        return initialRequest;
    }

    /**
     * Sets initialRequest value
     *
     * @param initialRequest
     */
    public void setInitialRequest(boolean initialRequest)
    {
        this.initialRequest = initialRequest;
    }

    /**
     * Sets the trainingOnRequested flag.
     *
     * @param value boolean
     */
    public void setTrainingOnRequested(boolean value)
    {
        this.trainingOnRequested = value;
    }

    /**
     * Returns the trainingOnRequested flag.
     *
     * @return boolean
     */
    public boolean isTrainingOnRequested()
    {
        return trainingOnRequested;
    }

    /**
     * Determines if buildRequest has executed before or not
     *
     * @return buildRequest
     */
    public boolean isBuildRequest()
    {
        return buildRequest;
    }

    /**
     * Sets the value of buildRequest
     *
     * @param br
     */
    public void setBuildRequest(boolean br)
    {
        buildRequest = br;
    }

    /**
     * Gets the card name swiped
     *
     * @return cardName
     */
    public String getCardName()
    {
        return cardName;
    }

    /**
     * Sets the card name swiped
     *
     * @param cardName
     */
    public void setCardName(String cardName)
    {
        this.cardName = cardName;
    }

    public InstantCreditApprovalStatus getApprovalStatus()
    {
        return approvalStatus;
    }

    public void setApprovalStatus(InstantCreditApprovalStatus approvalStatus)
    {
        this.approvalStatus = approvalStatus;
    }

    public String getHomePhone()
    {
        return homePhone;
    }

    public void setHomePhone(String homePhone)
    {
        this.homePhone = homePhone;
    }

    public String getZipCode()
    {
        return zipCode;
    }

    public void setZipCode(String zipCode)
    {
        this.zipCode = zipCode;
    }
}
