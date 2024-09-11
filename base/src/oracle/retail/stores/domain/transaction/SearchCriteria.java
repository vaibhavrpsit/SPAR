/* ===========================================================================
* Copyright (c) 2008, 2014, Oracle and/or its affiliates. All rights reserved.
 * ===========================================================================
 * $Header: rgbustores/modules/domain/src/oracle/retail/stores/domain/transaction/SearchCriteria.java /main/26 2014/03/20 16:53:21 cgreene Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    cgreene   03/20/14 - implement search for tranasctions by external order
 *                         id
 *    jswan     08/30/12 - Result of merge with repository.
 *    jswan     08/29/12 - Modified to separate Item specific criteria into a
 *                         separate class for web app support;
 *                         SearchCriteria.java now extends
 *                         ItemSearchCriteria.java.
 *    ohorne    01/26/12 - XbranchMerge ohorne_bug-13619784 from
 *                         rgbustores_13.4x_generic_branch
 *    ohorne    01/25/12 - added MaskedMICRNumber
 *    cgreene   09/19/11 - set searchBy flags when setting non-null attributes
 *    ohorne    08/10/11 - masked aba and account number for check
 *    ohorne    02/22/11 - ItemNumber can be ItemID or PosItemID
 *    hyin      02/17/11 - added fingerprint attribute
 *    asinton   12/20/10 - XbranchMerge asinton_bug-10407292 from
 *                         rgbustores_13.3x_generic_branch
 *    asinton   12/17/10 - deprecated hashed account ID.
 *    cgreene   05/27/10 - convert to oracle packaging
 *    cgreene   05/26/10 - convert to oracle packaging
 *    abondala  01/03/10 - update header date
 *    nkgautam  12/15/09 - Added attribute itemSerialNumber
 *    sgu       12/04/09 - add clone and equal methos
 *    sgu       12/04/09 - add java doc for PLU requestor getter and setter
 *    sgu       11/30/09 - add plu requestor to return plu information
 *                         selectively
 *    blarsen   11/04/08 - Added role as a search criteria - this simplifies
 *                         operations in jdbc role and employeelookup
 *    akandru   10/31/08 - EJ Changes_I18n
 *    akandru   10/30/08 - EJ changes
 *    ddbaker   10/23/08 - Final updates for localized item description support
 *    akandru   10/20/08 - EJ -- I18N
 *    akandru   10/20/08 -
 *    abondala  10/17/08 - I18Ning manufacturer name
 *    abondala  10/14/08 - I18Ning manufacturer name
 *    acadar    10/15/08 - I18n changes for discount rules: code reviews
 *                         comments
 *    acadar    10/13/08 - updates for reading localized information
 *    acadar    10/09/08 - updates the sites to set the LocaleRequestor
 *    acadar    10/08/08 - use LocaleRequestor to read the localzed name and
 *                         description for advanced pricing rules
 *    mchellap  09/30/08 - Updated copy right header
 *
 *     $Log:
 *      6    360Commerce 1.5         12/12/2007 6:55:10 PM  Michael P. Barnett In
 *            toString, return a more intuitive value if card number is null.
 *      5    360Commerce 1.4         11/21/2007 2:01:51 AM  Deepti Sharma   CR
 *           29598: changes for credit/debit PABP
 *      4    360Commerce 1.3         12/13/2005 4:43:52 PM  Barry A. Pape
 *           Base-lining of 7.1_LA
 *      3    360Commerce 1.2         3/31/2005 4:29:51 PM   Robert Pearse
 *      2    360Commerce 1.1         3/10/2005 10:25:06 AM  Robert Pearse
 *      1    360Commerce 1.0         2/11/2005 12:14:06 PM  Robert Pearse
 *     $
 *     Revision 1.8.2.1  2004/10/15 18:50:25  kmcbride
 *     Merging in trunk changes that occurred during branching activity
 *
 *     Revision 1.9  2004/10/11 22:00:49  jdeleau
 *     @scr 7306 Fix roles not appearing after they are created
 *
 *     Revision 1.8  2004/09/23 00:30:52  kmcbride
 *     @scr 7211: Inserting serialVersionUIDs in these Serializable classes
 *
 *     Revision 1.7  2004/07/17 15:57:05  lzhao
 *     @scr 6319: add clone.
 *
 *     Revision 1.6  2004/05/27 16:59:23  mkp1
 *     @scr 2775 Checking in first revision of new tax engine.
 *
 *     Revision 1.5  2004/02/19 21:55:02  aarvesen
 *     @scr 3561 Changed ItemSize to ItemSizeCode
 *
 *     Revision 1.4  2004/02/17 16:18:52  rhafernik
 *     @scr 0 log4j conversion
 *
 *     Revision 1.3  2004/02/12 17:14:42  mcs
 *     Forcing head revision
 *
 *     Revision 1.2  2004/02/11 23:28:51  bwf
 *     @scr 0 Organize imports.
 *
 *     Revision 1.1.1.1  2004/02/11 01:04:34  cschellenger
 *     updating to pvcs 360store-current
 *
 *
 *
 *    Rev 1.3   Dec 29 2003 15:32:28   baa
 * return enhancements
 *
 *    Rev 1.2   18 Dec 2003 23:16:02   baa
 * return enhancements
 *
 *    Rev 1.1   16 Dec 2003 00:36:06   baa
 * enhancemnts for return feature
 *
 *    Rev 1.0   Aug 29 2003 15:41:04   CSchellenger
 * Initial revision.
 *
 *    Rev 1.4   Apr 07 2003 10:36:20   bwf
 * Database Internationalization
 * Resolution for 1866: I18n Database  support
 *
 *    Rev 1.3   Feb 26 2003 11:08:44   bwf
 * Added Employee
 * Resolution for 1866: I18n Database  support
 *
 *    Rev 1.2   Jan 30 2003 16:01:14   adc
 * Changes for BackOffice 2.0
 * Resolution for 1846: Advanced Pricing Updates
 *
 *    Rev 1.1   Dec 30 2002 11:00:10   RSachdeva
 * Database Internationalization
 * Resolution for POS SCR-1866: I18n Database  support
 *
 *    Rev 1.0   Jun 03 2002 17:06:16   msg
 * Initial revision.
 *
 *    Rev 1.1   Mar 18 2002 23:11:58   msg
 * - updated copyright
 *
 *    Rev 1.0   Mar 18 2002 12:30:52   msg
 * Initial revision.
 *
 *    Rev 1.2   05 Nov 2001 17:34:24   baa
 * Implement code review changes. Customer & Inquiry Options
 * Resolution for POS SCR-244: Code Review  changes
 *
 *    Rev 1.1   24 Oct 2001 17:11:02   baa
 * customer history. Allow all transactions but void to be display.
 * Resolution for POS SCR-209: Customer History
 *
 *    Rev 1.0   Sep 20 2001 16:06:00   msg
 * Initial revision.
 *
 *    Rev 1.1   Sep 17 2001 12:40:02   msg
 * header update
 * ===========================================================================
 */

package oracle.retail.stores.domain.transaction;

import oracle.retail.stores.domain.customer.CustomerIfc;
import oracle.retail.stores.domain.employee.EmployeeIfc;
import oracle.retail.stores.domain.employee.RoleIfc;
import oracle.retail.stores.domain.stock.ItemSearchCriteria;
import oracle.retail.stores.domain.stock.ItemSearchCriteriaIfc;
import oracle.retail.stores.domain.utility.EYSDate;

/**
 * This class is used for setting a search criteria.
 */
public class SearchCriteria extends ItemSearchCriteria implements SearchCriteriaIfc
{
    // This id is used to tell the compiler not to generate a new serialVersionUID.
    static final long serialVersionUID = 1080300102133298793L;

    /**
     * revision number supplied by Team Connection
     */
    public static final String revisionNumber = "$Revision: /main/26 $";

    /**
     * customer as CustomerIfc
     */
    protected CustomerIfc customer = null;

    /**
     * customer as CustomerID
     */
    protected String customerID = null;

    /**
     * transaction exclusion flag
     */
    protected boolean excludeTrans = true;

    /**
     * date range as array of EYSDate
     */
    protected EYSDate[] dateRange = null;

    /**
     * training mode as boolean
     */
    protected String trainingMode = null;

    /**
     * name of the reason code group to be retrieved from the database
     */
    protected String codeMapName = null;

    /**
     * employee as employeeIfc
     */
    protected EmployeeIfc employee = null;

    /**
     * role as roleIfc
     */
    protected RoleIfc role = null;

    /**
     * transaction as transactionSummaryIfc
     */
    protected TransactionSummaryIfc transactionSummary = null;

    /**
     * masked account number
     */
    protected String maskedAccountNumber = "";

    /**
     * masked MICR number
     */
    protected String maskedMICRNumber = "";

    /**
     * account number token
     */
    protected String accountNumberToken = "";

    /**
     * gift card number
     */
    protected String maskedGiftcardNumber = "";

    /**
     * ABA number
     */
    protected String maskedABANumber = null;

    /**
     * ApplicationID
     */
    protected int applicationId = -1;

    /**
     * order id for systems like Siebel, ATG
     */
    protected String externalOrderID;

    /**
     * fingerprintFullEmployeeListMode
     */
    protected boolean fingerprintFullEmployeeListMode = false;

    /**
     * Constructs SearchCriteria object.
     */
    public SearchCriteria()
    {
    }

    /**
     * Sets the customer.
     * 
     * @param cust as CustomerIfc
     */
    public void setCustomer(CustomerIfc cust)
    {
        customer = cust;
    }

    /**
     * Gets the customer.
     * 
     * @return customer as CustomerIfc
     */
    public CustomerIfc getCustomer()
    {
        return customer;
    }

    /**
     * Sets the customerID.
     * 
     * @param cust as String
     */
    public void setCustomerID(String cust)
    {
        customerID = cust;
    }

    /**
     * Gets the customerID.
     * 
     * @return customer as Customer id
     */
    public String getCustomerID()
    {
        return customerID;
    }

    /**
     * Sets the date range.
     * 
     * @param date range as EYSDate []
     */
    public void setDateRange(EYSDate[] range)
    {
        dateRange = range;
    }

    /**
     * Gets the date range.
     * 
     * @return range as EYSDate []
     */
    public EYSDate[] getDateRange()
    {
        return dateRange;
    }

    /**
     * Sets the training mode.
     * 
     * @param mode as String
     */
    public void setTrainingMode(String mode)
    {
        trainingMode = mode;
    }

    /**
     * Gets the training mode.
     * 
     * @returns trainingMode as boolean
     */
    public String getTrainingMode()
    {
        return (trainingMode);

    }

    /**
     * Sets the exclusion mode.
     * 
     * @param mode as boolean
     */
    public void setExclusionMode(boolean mode)
    {
        excludeTrans = mode;
    }

    /**
     * Gets the training mode.
     * 
     * @returns trainingMode as boolean
     */
    public boolean getExclusionMode()
    {
        return (excludeTrans);
    }

    /**
     * Sets the name of the reason code Code List that needs to be retrieved
     * from the database. This is needed only when a specific group of reason
     * codes has to be retrieved from the database - not all o them.
     * 
     * @param name String
     */
    public void setCodeMapName(String name)
    {
        codeMapName = name;
    }

    /**
     * Gets the name of the reason code map that needs to be retrieved from the
     * database.
     * 
     * @returns String
     */
    public String getCodeMapName()
    {
        return codeMapName;
    }

    /**
     * Sets the employee.
     * 
     * @param empl as EmployeeID
     */
    public void setEmployee(EmployeeIfc empl)
    {
        employee = empl;
    }

    /**
     * Gets the employee.
     * 
     * @return employee as EmployeeIfc
     */
    public EmployeeIfc getEmployee()
    {
        return employee;
    }

    /**
     * Sets the role.
     * 
     * @param empl as RoleID
     */
    public void setRole(RoleIfc role)
    {
        this.role = role;
    }

    /**
     * Gets the role.
     * 
     * @return role as RoleIfc
     */
    public RoleIfc getRole()
    {
        return role;
    }

    /**
     * Sets the transactionSummary.
     * 
     * @param trans as transactionSummaryIfc
     */
    public void setTransactionSummary(TransactionSummaryIfc transSum)
    {
        transactionSummary = transSum;
    }

    /**
     * Gets the transactionSummary.
     * 
     * @return transactionSummary as TransactionSummaryIfc
     */
    public TransactionSummaryIfc getTransactionSummary()
    {
        return transactionSummary;
    }

    /**
     * Sets giftcard number.
     * 
     * @param value card number
     */
    public void setMaskedGiftCardNumber(String value)
    {
        maskedGiftcardNumber = value;
    }

    /**
     * Returns card number.
     * 
     * @return card number
     */
    public String getMaskedGiftCardNumber()
    {
        return (maskedGiftcardNumber);
    }

    /**
     * Sets masked ABA number.
     * 
     * @param value ABA number
     */
    public void setMaskedABANumber(String value)
    {
        maskedABANumber = value;
    }

    /**
     * Returns ABA number.
     * 
     * @return ABA number
     */
    public String getMaskedABANumber()
    {
        return (maskedABANumber);
    }

    /**
     * Set the applicationID. This dictates which application is making the
     * request
     * 
     * @param id
     * @see oracle.retail.stores.domain.transaction.SearchCriteriaIfc#setApplicationId(int)
     */
    public void setApplicationId(int id)
    {
        this.applicationId = id;
    }

    /**
     * Get the applicationID. This will say what application is making the
     * request.
     * 
     * @return
     */
    public int getApplicationId()
    {
        return this.applicationId;
    }

    /* (non-Javadoc)
     * @see oracle.retail.stores.domain.transaction.SearchCriteriaIfc#setMaskedAccountNumber(java.lang.String)
     */
    public void setMaskedAccountNumber(String value)
    {
        this.maskedAccountNumber = value;
    }

    /* (non-Javadoc)
     * @see oracle.retail.stores.domain.transaction.SearchCriteriaIfc#getMaskedAccountNumber()
     */
    public String getMaskedAccountNumber()
    {
        return this.maskedAccountNumber;
    }

    /* (non-Javadoc)
     * @see oracle.retail.stores.domain.transaction.SearchCriteriaIfc#setMaskedMICRNumber(java.lang.String)
     */
    public void setMaskedMICRNumber(String value)
    {
        this.maskedMICRNumber = value;
    }

    /* (non-Javadoc)
     * @see oracle.retail.stores.domain.transaction.SearchCriteriaIfc#getMaskedMICRNumber()
     */
    public String getMaskedMICRNumber()
    {
        return this.maskedMICRNumber;
    }

    /**
     * @return the fingerprintFullEmployeeListMode
     */
    public boolean isFingerprintFullEmployeeListMode()
    {
        return fingerprintFullEmployeeListMode;
    }

    /**
     * @param fingerprintFullEmployeeListMode the
     *            fingerprintFullEmployeeListMode to set
     */
    public void setFingerprintFullEmployeeListMode(boolean fingerprintMode)
    {
        this.fingerprintFullEmployeeListMode = fingerprintMode;
    }

    /**
     * @return the cardNumberToken
     */
    public String getAccountNumberToken()
    {
        return accountNumberToken;
    }

    /**
     * @param cardNumberToken the cardNumberToken to set
     */
    public void setAccountNumberToken(String accountNumberToken)
    {
        this.accountNumberToken = accountNumberToken;
    }

    /* (non-Javadoc)
     * @see oracle.retail.stores.domain.transaction.SearchCriteriaIfc#getExternalOrderID()
     */
    @Override
    public String getExternalOrderID()
    {
        return externalOrderID;
    }

    /* (non-Javadoc)
     * @see oracle.retail.stores.domain.transaction.SearchCriteriaIfc#setExternalOrderID(java.lang.String)
     */
    @Override
    public void setExternalOrderID(String externalOrderID)
    {
        this.externalOrderID = externalOrderID;
    }

    /* (non-Javadoc)
     * @see oracle.retail.stores.domain.transaction.SearchCriteriaIfc#getItemSearchCriteria()
     */
    public ItemSearchCriteriaIfc getItemSearchCriteria()
    {
        return (ItemSearchCriteriaIfc)super.clone();
    }

    /**
     * clone searchCriteria object
     */
    public Object clone()
    {
        SearchCriteriaIfc newClass = new SearchCriteria();
        cloneAttributes(newClass);
        return newClass;
    }

    protected void cloneAttributes(SearchCriteriaIfc clone)
    {
        super.cloneAttributes(clone);
        clone.setCustomer(customer);
        clone.setCustomerID(customerID);
        clone.setExclusionMode(excludeTrans);
        clone.setTrainingMode(trainingMode);
        clone.setEmployee(employee);
        clone.setCodeMapName(codeMapName);
        clone.setTransactionSummary(transactionSummary);
        clone.setMaskedAccountNumber(maskedAccountNumber);
        clone.setMaskedMICRNumber(maskedMICRNumber);
        clone.setAccountNumberToken(accountNumberToken);
        clone.setMaskedGiftCardNumber(maskedGiftcardNumber);
        clone.setMaskedABANumber(maskedABANumber);
        clone.setApplicationId(applicationId);
        clone.setExternalOrderID(externalOrderID);

        if (dateRange != null)
        {
            EYSDate[] newDataRange = new EYSDate[dateRange.length];
            System.arraycopy(dateRange, 0, newDataRange, 0, dateRange.length);
            clone.setDateRange(newDataRange);
        }
    }

    /* (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString()
    {
        StringBuilder values = new StringBuilder(super.toString());
        values.append("customer: [" + customer + "]\n");
        values.append("accountNumberToken: [" + accountNumberToken + "]\n");
        values.append("maskedAccountNumber: [" + maskedAccountNumber + "]\n");
        values.append("maskedABANumber: [" + maskedABANumber + "]\n");
        values.append("maskedMicrNumber: [" + maskedMICRNumber + "]\n");
        values.append("applicationId: [" + applicationId + "]\n");
        values.append("externalOrderID: [" + externalOrderID + "]\n");
        values.append("dateRange:");

        if (dateRange != null)
        {
            for (int i = 0; i < dateRange.length; i++)
            {
                if (dateRange[i] != null)
                {
                    values.append("\nDate Range[" + i + "]:  [" + dateRange[i].toFormattedString() + "]");
                }
                else
                {
                    values.append("\nDate Range[" + i + "]:  [null]\n");
                }
            }
        }
        else
        {
            values.append("  [null]\n");
        }

        return values.toString();
    }
}
