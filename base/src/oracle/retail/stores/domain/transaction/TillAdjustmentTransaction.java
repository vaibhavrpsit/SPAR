/* ===========================================================================
* Copyright (c) 1998, 2011, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/modules/domain/src/oracle/retail/stores/domain/transaction/TillAdjustmentTransaction.java /rgbustores_13.4x_generic_branch/1 2011/05/04 11:48:47 mszekely Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED (MM/DD/YY)
 *    cgreen 05/27/10 - convert to oracle packaging
 *    cgreen 05/26/10 - convert to oracle packaging
 *    abonda 01/03/10 - update header date
 *    cgreen 03/17/09 - formatting
 *    glwang 02/26/09 - get count type from TillAdjustmentTransaction instead
 *                      of FinancialCount
 *    ohorne 11/06/08 - TillPayment Tender Type now persisted as Code instead
 *                      of DisplayName. Deprecated
 *                      TillAdjustmentTransaction.tenderType, which has been
 *                      replaced with a TenderDescriptorIfc attribute.
 *    mdecam 11/07/08 - I18N - updated toString()
 *    mdecam 11/07/08 - I18N - Fixed Clone Method
 *    ohorne 10/29/08 - Localization of Till related Reason Codes
 *
 * ===========================================================================

     $Log:
      6    360Commerce 1.5         4/12/2008 5:44:57 PM   Christian Greene
           Upgrade StringBuffer to StringBuilder
      5    360Commerce 1.4         6/26/2007 11:13:58 AM  Ashok.Mondal    I18N
           changes to export and import POSLog.
      4    360Commerce 1.3         4/25/2007 10:00:18 AM  Anda D. Cadar   I18N
           merge
      3    360Commerce 1.2         3/31/2005 4:30:29 PM   Robert Pearse
      2    360Commerce 1.1         3/10/2005 10:26:10 AM  Robert Pearse
      1    360Commerce 1.0         2/11/2005 12:15:03 PM  Robert Pearse
     $
     Revision 1.7  2004/09/23 00:30:51  kmcbride
     @scr 7211: Inserting serialVersionUIDs in these Serializable classes

     Revision 1.6  2004/07/22 04:56:32  khassen
     @scr 6296/6297/6298 - Updating pay in, pay out, payroll pay out:
     Adding database fields, print and reprint receipt functionality to reflect
     persistence of additional data in transaction.

     Revision 1.5  2004/07/08 23:34:31  jdeleau
     @scr 6086 payroll till pay out on post void was crashing the system.  In fact it
     was not implemented at all.  Now its implemented just as normal till pay out.

     Revision 1.4  2004/02/16 18:12:58  dcobb
     @scr 3381 Feature Enhancement:  Till Pickup and Loan
     Add to/from register to TillAdjustmentTransactionTest.

     Revision 1.3  2004/02/12 17:14:42  mcs
     Forcing head revision

     Revision 1.2  2004/02/11 23:28:51  bwf
     @scr 0 Organize imports.

     Revision 1.1.1.1  2004/02/11 01:04:34  cschellenger
     updating to pvcs 360store-current


 *
 *    Rev 1.5   Jan 28 2004 16:36:34   DCobb
 * Added From/To register ID's for operation without a safe.
 * Resolution for 3381: Feature Enhancement:  Till Pickup and Loan
 *
 *    Rev 1.4   Dec 01 2003 13:46:18   bwf
 * Updated for echeck declines.
 *
 *    Rev 1.3   25 Nov 2003 22:51:00   baa
 * implement new methods on interface
 *
 *    Rev 1.2   25 Nov 2003 22:36:34   baa
 * address build break
 *
 *    Rev 1.1   Sep 15 2003 14:02:14   bwf
 * Put amount in toString method.
 * Resolution for 3334: Feature Enhancement:  Queue Exception Handling
 *
 *    Rev 1.0   Aug 29 2003 15:41:08   CSchellenger
 * Initial revision.
 *
 *    Rev 1.1   Feb 15 2003 14:52:30   mpm
 * Merged 5.1 changes.
 * Resolution for Domain SCR-104: Merge 5.1/5.5 into 6.0
 *
 *    Rev 1.0   Jun 03 2002 17:06:24   msg
 * Initial revision.
 * ===========================================================================
 */
package oracle.retail.stores.domain.transaction;

import oracle.retail.stores.commerceservices.common.currency.CurrencyIfc;
import oracle.retail.stores.common.utility.LocaleMap;
import oracle.retail.stores.common.utility.LocaleMapConstantsIfc;
import oracle.retail.stores.common.utility.LocalizedCodeIfc;
import oracle.retail.stores.common.utility.LocalizedTextIfc;
import oracle.retail.stores.domain.DomainGateway;
import oracle.retail.stores.domain.customer.CustomerIfc;
import oracle.retail.stores.domain.financial.FinancialCount;
import oracle.retail.stores.domain.financial.FinancialCountIfc;
import oracle.retail.stores.domain.financial.FinancialTotalsIfc;
import oracle.retail.stores.domain.financial.ReconcilableCountIfc;
import oracle.retail.stores.domain.tender.TenderDescriptorIfc;
import oracle.retail.stores.domain.tender.TenderLineItemIfc;
import oracle.retail.stores.foundation.utility.Util;

/**
 * This class describes a till adjustment transaction. A till adjustment
 * transaction is used for loans, pickups, pay-ins and pay-outs.
 * 
 * @see oracle.retail.stores.domain.transaction.TillAdjustmentTransactionIfc
 * @see oracle.retail.stores.domain.transaction.AbstractTenderableTransaction
 * @version $Revision: /rgbustores_13.4x_generic_branch/1 $
 */
public class TillAdjustmentTransaction extends AbstractTenderableTransaction
    implements TillAdjustmentTransactionIfc
{
    // This id is used to tell the compiler not to generate a new serialVersionUID.
    static final long serialVersionUID = 372533717535381568L;

    /**
     * revision number supplied by source-code-control system
     */
    public static String revisionNumber = "$Revision: /rgbustores_13.4x_generic_branch/1 $";

    /**
     * tender type
     * 
     * @deprecated As of 13.1 use {@link #tender}
     */
    protected String tenderType = DomainGateway.getFactory().getTenderTypeMapInstance().getDescriptor(
            TenderLineItemIfc.TENDER_TYPE_CASH);

    protected TenderDescriptorIfc tender;

    /**
     * till adjustment amount
     */
    protected CurrencyIfc adjustmentAmount;

    /**
     * till adjustment count
     */
    protected int adjustmentCount;

    /**
     * till expected amount
     */
    protected CurrencyIfc expectedAmount;

    /**
     * till expected count
     */
    protected int expectedCount;

    /**
     * till adjustment reason code
     * 
     * @deprecated as of 13.1 Use {@link #localizedReasonCode}
     */
    protected String reasonCode;

    /**
     * till adjustment reason code text
     * 
     * @deprecated as of 13.1 Use {@link #localizedReasonCode}
     */
    protected String reasonCodeText;

    /**
     * Localized till adjustment reason code
     */
    protected LocalizedCodeIfc localizedReasonCode = null;

    /**
     * Localized till adjustment approval code
     */
    protected LocalizedCodeIfc localizedApprovalCode = null;

    /**
     * count type
     */
    protected int countType = FinancialCountIfc.COUNT_TYPE_NONE;

    /**
     * tender count
     */
    protected FinancialCountIfc tenderCount = null;

    /**
     * 'from' register ID when operating without a safe
     */
    protected String fromRegister = null;

    /**
     * 'to' register ID when operating without a safe
     */
    protected String toRegister = null;

    /**
     * Currency ID
     */
    protected int currencyID; // I18N

    /**
     * Approval code:
     * 
     * @deprecated as of 13.1 Use {@link #localizedApprovalCode}
     */
    String approvalCode;

    // Employee ID:
    String employeeID;

    // Full name of payee:
    String payeeName;

    // Address line 1:
    String[] addressLines = new String[3];

    // Comments:
    String comments;

    /**
     * Constructs TillAdjustmentTransaction object
     */
    public TillAdjustmentTransaction()
    {
        initialize();
    }

    /**
     * Initializes object with local attributes.
     */
    public void initialize()
    {
        super.initialize();
        adjustmentAmount = DomainGateway.getBaseCurrencyInstance();
        expectedAmount = DomainGateway.getBaseCurrencyInstance();
        tenderCount = DomainGateway.getFactory().getFinancialCountInstance();

        tender = DomainGateway.getFactory().getTenderDescriptorInstance();
        tender.setTenderType(TenderLineItemIfc.TENDER_TYPE_CASH);
        tender.setCountryCode(DomainGateway.getBaseCurrencyInstance().getCountryCode());
        tender.setCurrencyID(DomainGateway.getBaseCurrencyType().getCurrencyId());
    }

    /**
     * Creates clone of this object.
     *
     * @return Object clone of this object
     */
    public Object clone()
    {
        // instantiate new object
        TillAdjustmentTransaction c = new TillAdjustmentTransaction();

        // set values
        setCloneAttributes(c);

        // pass back Object
        return c;
    }

    /**
     * Sets attributes in clone of this object.
     *
     * @param newClass new instance of object
     */
    protected void setCloneAttributes(TillAdjustmentTransaction newClass)
    {
        super.setCloneAttributes(newClass);
        newClass.setTender((TenderDescriptorIfc)tender.clone());
        newClass.setAdjustmentAmount((CurrencyIfc)adjustmentAmount.clone());
        newClass.setExpectedAmount((CurrencyIfc)expectedAmount.clone());
        newClass.expectedCount = expectedCount;
        newClass.adjustmentCount = adjustmentCount;
        if (this.localizedReasonCode != null)
        {
            newClass.setReason((LocalizedCodeIfc)getReason().clone());
        }
        newClass.setCountType(getCountType());
        if (getTenderCount() != null)
        {
            newClass.setTenderCount((FinancialCountIfc)getTenderCount().clone());
        }
        if (fromRegister != null)
        {
            newClass.setFromRegister(new String(fromRegister));
        }
        if (toRegister != null)
        {
            newClass.setToRegister(new String(toRegister));
        }
    }

    /**
     * Determine if two objects are identical.
     *
     * @param obj object to compare with
     * @return true if the objects are identical, false otherwise
     */
    public boolean equals(Object obj)
    {
        boolean isEqual = true;
        // confirm object instanceof this object
        if (obj instanceof TillAdjustmentTransaction)
        {
            // downcast the input object
            TillAdjustmentTransaction c = (TillAdjustmentTransaction)obj;
            // compare all the attributes of Layaway
            if (// super.equals() &&
            Util.isObjectEqual(c.tender, tender) && Util.isObjectEqual(c.adjustmentAmount, adjustmentAmount)
                    && Util.isObjectEqual(c.expectedAmount, expectedAmount)
                    && Util.isObjectEqual(c.localizedReasonCode, localizedReasonCode)
                    && Util.isObjectEqual(c.getTenderCount(), getTenderCount()) && c.adjustmentCount == adjustmentCount
                    && c.getCountType() == getCountType() && c.expectedCount == expectedCount
                    && Util.isObjectEqual(c.fromRegister, fromRegister) && Util.isObjectEqual(c.toRegister, toRegister))
            {
                isEqual = true; // set the return code to true
            }
            else
            {
                isEqual = false; // set the return code to false
            }
        }
        else
        {
            isEqual = false;
        }
        return (isEqual);
    }

    /**
     * Retrieves tender type for this transaction.
     *
     * @return tender type for this transaction
     * @deprecated As of release 13.1. Use {@link #getTender()}
     */
    public String getTenderType()
    {
        return (tenderType);
    }

    /**
     * Sets the tender type for this transaction.
     *
     * @param value tender type for this transaction
     * @deprecated As of release 13.1. Use
     *             {@link #setTender(TenderDescriptorIfc)}
     */
    public void setTenderType(String value)
    {
        tenderType = value;
    }

    /**
     * @return the tender
     */
    public TenderDescriptorIfc getTender()
    {
        return tender;
    }

    /**
     * @param tender the tender to set
     */
    public void setTender(TenderDescriptorIfc tender)
    {
        this.tender = tender;
    }

    /**
     * Gets the adjustment amount for this transaction.
     * 
     * @return adjustment amount for this transaction
     */
    public CurrencyIfc getAdjustmentAmount()
    {
        return adjustmentAmount;
    }

    /**
     * Sets the adjustment amount for this transaction.
     *
     * @param value adjustment amount for this transaction
     */
    public void setAdjustmentAmount(CurrencyIfc value)
    {
        adjustmentAmount = value;
    }

    /**
     * Gets the adjustment count for this transaction.
     * 
     * @return adjustment count for this transaction
     */
    public int getAdjustmentCount()
    {
        return adjustmentCount;
    }

    /**
     * Sets the adjustment count for this transaction.
     *
     * @param value adjustment count for this transaction
     */
    public void setAdjustmentCount(int value)
    {
        adjustmentCount = value;
    }

    /**
     * Gets the expected amount for this transaction.
     * 
     * @return expected amount for this transaction
     */
    public CurrencyIfc getExpectedAmount()
    {
        return expectedAmount;
    }

    /**
     * Sets the expected amount for this transaction.
     * 
     * @param value expected amount for this transaction
     */
    public void setExpectedAmount(CurrencyIfc value)
    {
        expectedAmount = value;
    }

    /**
     * Gets the expected count for this transaction.
     * 
     * @return expected count for this transaction
     */
    public int getExpectedCount()
    {
        return expectedCount;
    }

    /**
     * Sets the expected count for this transaction.
     *
     * @param value expected count for this transaction
     */
    public void setExpectedCount(int value)
    {
        expectedCount = value;
    }

    /**
     * Returns financial totals associated with this transaction.
     * 
     * @return financial totals associated with this transaction
     */
    public FinancialTotalsIfc getFinancialTotals()
    {
        FinancialTotalsIfc adjustmentTotals = (DomainGateway.getFactory().getFinancialTotalsInstance());

        ReconcilableCountIfc[] rCountArray = new ReconcilableCountIfc[getAdjustmentCount()];
        ReconcilableCountIfc rCount = DomainGateway.getFactory().getReconcilableCountInstance();
        FinancialCountIfc fCount = rCount.getEntered();
        CurrencyIfc zeroCurrency = DomainGateway.getBaseCurrencyInstance();

        switch (getTransactionType())
        {
            case TYPE_PAYIN_TILL:
                fCount.addTenderItem(getTender(), 1, 0, getAdjustmentAmount().abs(), zeroCurrency, DomainGateway
                        .getFactory().getTenderTypeMapInstance().getDescriptor(getTender().getTenderType()), "", false);
                rCountArray[0] = rCount;
                adjustmentTotals.addTillPayIns(rCountArray);
                break;
            case TYPE_PAYOUT_TILL:
            case TYPE_PAYROLL_PAYOUT_TILL:
                fCount.addTenderItem(getTender(), 0, 1, zeroCurrency, getAdjustmentAmount().abs(), DomainGateway
                        .getFactory().getTenderTypeMapInstance().getDescriptor(getTender().getTenderType()), "", false);
                rCountArray[0] = rCount;
                adjustmentTotals.addTillPayOuts(rCountArray);
                break;
            case TYPE_PICKUP_TILL:
                rCount.setEntered(getTenderCount());
                rCountArray[0] = rCount;
                adjustmentTotals.addTillPickups(rCountArray);
                break;
            case TYPE_LOAN_TILL:
                adjustmentTotals.setCountTillLoans(1);
                rCount.setEntered(getTenderCount());
                rCountArray[0] = rCount;
                adjustmentTotals.addTillLoans(rCountArray);
                break;
            default:
                break;
        }

        return adjustmentTotals;
    }

    /**
     * Returns till adjustment reason code.
     *
     * @return reason code
     * @deprecated as of 13.1 Use {@link #getReason()}
     */
    public String getReasonCode()
    {
        if (localizedReasonCode != null)
        {
            return localizedReasonCode.getCode();
        }
        
        return null;
    }

    /**
     * Sets till adjustment reason code.
     *
     * @param value reason code
     * @deprecated as of 13.1 Use {@link #setReason(LocalizedCodeIfc)}
     */
    public void setReasonCode(String value)
    {
        if (localizedReasonCode == null)
        {
            localizedReasonCode = DomainGateway.getFactory().getLocalizedCode();
        }
        localizedReasonCode.setCode(value);
    }

    /**
     * Returns till adjustment reason code text.
     *
     * @return reason code text
     * @deprecated as of 13.1 Use {@link #getReason()}
     */
    public String getReasonCodeText()
    {
        if (localizedReasonCode != null)
        {
            return localizedReasonCode.getText(LocaleMap.getLocale(LocaleMapConstantsIfc.DEFAULT));
        }
        
        return null;
    }

    /**
     * Sets till adjustment reason code text.
     *
     * @param value reason code
     * @deprecated as of 13.1 Use {@link #setReason(LocalizedCodeIfc)}
     */
    public void setReasonCodeText(String value)
    {
        if (localizedReasonCode == null)
        {
            localizedReasonCode = DomainGateway.getFactory().getLocalizedCode();
        }

        LocalizedTextIfc localizedText = DomainGateway.getFactory().getLocalizedText();
        localizedText.putText(LocaleMap.getLocale(LocaleMapConstantsIfc.DEFAULT), value);
        localizedReasonCode.setText(localizedText);
    }

    /* (non-Javadoc)
     * @see oracle.retail.stores.domain.transaction.TillAdjustmentTransactionIfc#getReason()
     */
    public LocalizedCodeIfc getReason()
    {
        return localizedReasonCode;
    }

    /* (non-Javadoc)
     * @see oracle.retail.stores.domain.transaction.TillAdjustmentTransactionIfc#setReason(oracle.retail.stores.common.utility.LocalizedCodeIfc)
     */
    public void setReason(LocalizedCodeIfc reason)
    {
        this.localizedReasonCode = reason;
    }

    /* (non-Javadoc)
     * @see oracle.retail.stores.domain.transaction.TillAdjustmentTransactionIfc#getApproval()
     */
    public LocalizedCodeIfc getApproval()
    {
        return localizedApprovalCode;
    }

    /* (non-Javadoc)
     * @see oracle.retail.stores.domain.transaction.TillAdjustmentTransactionIfc#setApproval(oracle.retail.stores.common.utility.LocalizedCodeIfc)
     */
    public void setApproval(LocalizedCodeIfc approval)
    {
        this.localizedApprovalCode = approval;
    }

    /**
     * Retrieves the total amount for this transaction as a
     * TransactionTotalsIfc. Only the grandTotal attribute of the
     * TransactionTotalsIfc is set.
     * 
     * @return TransactionTotalsIfc
     */
    public TransactionTotalsIfc getTenderTransactionTotals()
    {
        TransactionTotalsIfc totals = DomainGateway.getFactory().getTransactionTotalsInstance();
        totals.setGrandTotal(adjustmentAmount);
        return totals;
    }

    /**
     * This method is added to support the AbstractTenderableTransaction
     * interface.
     * 
     * @param value customer
     */
    public void linkCustomer(CustomerIfc value)
    {
    }

    /**
     * Gets the count type for this transaction .
     * 
     * @return count type for this transaction
     */
    public int getCountType()
    {
        return countType;
    }

    /**
     * @return true if till count type is none
     */
    public boolean isCountTypeNone()
    {
        return getCountType() == FinancialCount.COUNT_TYPE_NONE;
    }

    /**
     * @return true if till count type is summary
     */
    public boolean isCountTypeSummary()
    {
        return getCountType() == FinancialCount.COUNT_TYPE_SUMMARY;
    }

    /**
     * @return true if till count type is detail
     */
    public boolean isCountTypeDetail()
    {
        return getCountType() == FinancialCount.COUNT_TYPE_DETAIL;
    }

    /**
     * Sets the count type for this transaction .
     *
     * @param value count type for this transaction
     */
    public void setCountType(int value)
    {
        countType = value;
    }

    /**
     * Retrieves tender count for this transaction.
     *
     * @return tender count for this transaction
     */
    public FinancialCountIfc getTenderCount()
    {
        return (tenderCount);
    }

    /**
     * Sets the tender count for this transaction.
     *
     * @param value tender count for this transaction
     */
    public void setTenderCount(FinancialCountIfc value)
    {
        tenderCount = value;
    }

    /**
     * Retrieves 'from' register ID for this transaction.
     *
     * @return 'from' register ID for this transaction
     */
    public String getFromRegister()
    {
        return fromRegister;
    }

    /**
     * Sets the from register ID for this transaction.
     *
     * @param value from register ID for this transaction
     */
    public void setFromRegister(String value)
    {
        fromRegister = value;
    }

    /**
     * Retrieves 'to' register ID for this transaction.
     *
     * @return 'to' register ID for this transaction
     */
    public String getToRegister()
    {
        return toRegister;
    }

    /**
     * Sets the 'to' register ID for this transaction.
     *
     * @param value 'to' register ID for this transaction
     */
    public void setToRegister(String value)
    {
        toRegister = value;
    }

    /**
     * Returns default display string.
     *
     * @return String representation of object
     */
    public String toString()
    {
        // build result string
        StringBuilder strResult = Util
                .classToStringHeader("TillAdjustmentTransaction", getRevisionNumber(), hashCode());
        strResult.append(Util.formatToStringEntry("tender", tender)).append(
                Util.formatToStringEntry("Adjustment Amount:", adjustmentAmount.toString())).append(
                Util.formatToStringEntry("Reason:", localizedReasonCode)).append(super.toString());

        // pass back result
        return (strResult.toString());
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
     * Layawaymain method.
     *
     * @param String args[] command-line parameters
     */
    public static void main(String args[])
    {
        // instantiate class
        TillAdjustmentTransaction c = new TillAdjustmentTransaction();
        // output toString()
        System.out.println(c.toString());
    }

    /**
     * @return Returns the address.
     */
    public String getAddressLine(int i)
    {
        return addressLines[i];
    }

    /**
     * @param address The address to set.
     */
    public void setAddressLine(String address, int i)
    {
        this.addressLines[i] = address;
    }

    public String[] getAddressLines()
    {
        return addressLines;
    }

    /**
     * @return Returns the approvalCode.
     * @deprecated As of Release 13.1 use {@link #getApproval(LocalizedCodeIfc)}
     */
    public String getApprovalCode()
    {
        return approvalCode;
    }

    /**
     * @param approvalCode The approvalCode to set.
     * @deprecated As of Release 13.1 use {@link #setApproval(LocalizedCodeIfc)}
     */
    public void setApprovalCode(String approvalCode)
    {
        this.approvalCode = approvalCode;
    }

    /**
     * @return Returns the comments.
     */
    public String getComments()
    {
        return comments;
    }

    /**
     * @param comments The comments to set.
     */
    public void setComments(String comments)
    {
        this.comments = comments;
    }

    /**
     * @return Returns the employeeID.
     */
    public String getEmployeeID()
    {
        return employeeID;
    }

    /**
     * @param employeeID The employeeID to set.
     */
    public void setEmployeeID(String employeeID)
    {
        this.employeeID = employeeID;
    }

    /**
     * @return Returns the payeeName.
     */
    public String getPayeeName()
    {
        return payeeName;
    }

    /**
     * @param payeeName The payeeName to set.
     */
    public void setPayeeName(String payeeName)
    {
        this.payeeName = payeeName;
    }

    /**
     * Retrieve Currency ID
     * 
     * @return currencyID
     */
    public int getCurrencyID()
    {
        return currencyID;
    }

    /**
     * Set Currency ID
     * 
     * @param currencyID
     */
    public void setCurrencyID(int currencyID)
    {
        this.currencyID = currencyID;
    }
}