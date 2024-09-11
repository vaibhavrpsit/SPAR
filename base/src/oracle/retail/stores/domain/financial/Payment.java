/* ===========================================================================
* Copyright (c) 1998, 2014, Oracle and/or its affiliates. All rights reserved.
 * ===========================================================================
 * $Header: rgbustores/modules/domain/src/oracle/retail/stores/domain/financial/Payment.java /main/15 2014/07/17 15:09:42 yiqzhao Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    yiqzhao   07/17/14 - Checking businessDate is null or not before cloning.
 *    cgreene   07/19/11 - store layaway and order ids in separate column from
 *                         house account number.
 *    cgreene   05/27/10 - convert to oracle packaging
 *    cgreene   05/26/10 - convert to oracle packaging
 *    abondala  01/03/10 - update header date
 *    npoola    01/27/09 - Fixed the PDO receipt and PDO payment totals in
 *                         TR_LTM_PYAN
 *
 * ===========================================================================
 * $Log:
 *    8    360Commerce 1.7         4/12/2008 5:44:57 PM   Christian Greene
 *         Upgrade StringBuffer to StringBuilder
 *    7    360Commerce 1.6         1/17/2008 5:24:06 PM   Alan N. Sinton  CR
 *         29954: Refactor of EncipheredCardData to implement interface and be
 *          instantiated using a factory.
 *    6    360Commerce 1.5         12/18/2007 5:47:48 PM  Alan N. Sinton  CR
 *         29661: Changes per code review.
 *    5    360Commerce 1.4         11/27/2007 12:32:24 PM Alan N. Sinton  CR
 *         29661: Encrypting, masking and hashing account numbers for House
 *         Account.
 *    4    360Commerce 1.3         4/25/2007 10:00:55 AM  Anda D. Cadar   I18N
 *         merge
 *    3    360Commerce 1.2         3/31/2005 4:29:19 PM   Robert Pearse   
 *    2    360Commerce 1.1         3/10/2005 10:24:01 AM  Robert Pearse   
 *    1    360Commerce 1.0         2/11/2005 12:13:01 PM  Robert Pearse   
 *
 *   Revision 1.4  2004/09/23 00:30:53  kmcbride
 *   @scr 7211: Inserting serialVersionUIDs in these Serializable classes
 *
 *   Revision 1.3  2004/02/12 17:13:34  mcs
 *   Forcing head revision
 *
 *   Revision 1.2  2004/02/11 23:25:27  bwf
 *   @scr 0 Organize imports.
 *
 *   Revision 1.1.1.1  2004/02/11 01:04:30  cschellenger
 *   updating to pvcs 360store-current
 *
 *
 * 
 *    Rev 1.0   Aug 29 2003 15:35:46   CSchellenger
 * Initial revision.
 * 
 *    Rev 1.0   Jun 03 2002 16:52:22   msg
 * Initial revision.
 * 
 *    Rev 1.2   Apr 28 2002 13:31:56   mpm
 * Completed translation of sale transactions.
 * Resolution for Domain SCR-45: TLog facility
 *
 *    Rev 1.1   Mar 18 2002 23:01:12   msg
 * - updated copyright
 *
 *    Rev 1.0   Mar 18 2002 12:21:28   msg
 * Initial revision.
 *
 *    Rev 1.1   13 Mar 2002 10:54:54   vxs
 * Corrected spelling in toString()
 * Resolution for POS SCR-954: Domain - Arts Translation
 *
 *    Rev 1.0   Sep 20 2001 16:14:52   msg
 * Initial revision.
 *
 *    Rev 1.1   Sep 17 2001 12:37:28   msg
 * header update
 * ===========================================================================
 */
package oracle.retail.stores.domain.financial;

import oracle.retail.stores.commerceservices.common.currency.CurrencyIfc;
import oracle.retail.stores.domain.DomainGateway;
import oracle.retail.stores.domain.transaction.TransactionIDIfc;
import oracle.retail.stores.domain.utility.EYSDate;
import oracle.retail.stores.foundation.manager.device.EncipheredCardDataIfc;
import oracle.retail.stores.foundation.utility.Util;

/**
 * This class describes a payment for a product or service purchased on an
 * installment basis or on an account.
 * 
 * @version $Revision: /main/15 $
 * @see oracle.retail.stores.domain.financial.PaymentIfc
 */
public class Payment implements PaymentIfc
{
    // This id is used to tell the compiler not to generate a new serialVersionUID.
    static final long serialVersionUID = 118999516765979252L;

    /**
     * revision number supplied by source-code-control system
     */
    public static final String revisionNumber = "$Revision: /main/15 $";
    /**
     * payment amount
     */
    protected CurrencyIfc paymentAmount = null;
    /**
     * balance due
     */
    protected CurrencyIfc balanceDue = null;
    /**
     * The Reference Number for this payment. Should not be sensitive.
     */
    protected String referenceNumber;
    /**
     * Handle to EncipheredCardData. Do not lazily init. Should only be set if
     * payment type is for house account.
     */
    protected EncipheredCardDataIfc encipheredCardData;
    /**
     * description
     */
    protected String description = "";
    /**
     * transaction identifier
     */
    protected TransactionIDIfc transactionID = null;
    /**
     * business date
     */
    protected EYSDate businessDate = null;
    /**
     * payment account type
     */
    protected String paymentAccountType = PaymentConstantsIfc.ACCOUNT_TYPE_UNSPECIFIED;
    /**
     * pickup and delivery item deposit amount
     */
    protected CurrencyIfc pickupDeliveryItemDepositAmount = null;
    /**
     * set flag to true if its PDO payment
     */
    boolean isPDOPayment = false;

    /**
     * Constructs Payment object.
     */
    public Payment()
    {
        initialize();
    }

    /**
     * Initializes values.
     */
    public void initialize()
    {
        paymentAmount = DomainGateway.getBaseCurrencyInstance();
        businessDate = DomainGateway.getFactory().getEYSDateInstance();
        businessDate.setType(EYSDate.TYPE_DATE_ONLY);
        transactionID = DomainGateway.getFactory().getTransactionIDInstance();
        balanceDue = DomainGateway.getBaseCurrencyInstance();
        balanceDue.setZero();
    }

    /* (non-Javadoc)
     * @see java.lang.Object#clone()
     */
    @Override
    public Object clone()
    {
        // instantiate new object
        Payment c = new Payment();

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
    public void setCloneAttributes(Payment newClass)
    {
        if (paymentAmount != null)
        {
            newClass.setPaymentAmount((CurrencyIfc)getPaymentAmount().clone());
        }
        if (transactionID != null)
        {
            newClass.setTransactionID((TransactionIDIfc)transactionID.clone());
        }
        newClass.setReferenceNumber(getReferenceNumber());
        newClass.setEncipheredCardData(encipheredCardData);
        newClass.setDescription(getDescription());
        if (businessDate != null)
        {
            newClass.setBusinessDate((EYSDate)getBusinessDate().clone());
        }
        newClass.setPaymentAccountType(getPaymentAccountType());
        newClass.setBalanceDue(getBalanceDue());
        newClass.setPickupDeliveryItemDepositAmount(getPickupDeliveryItemDepositAmount());
    }

    /* (non-Javadoc)
     * @see java.lang.Object#equals(java.lang.Object)
     */
    @Override
    public boolean equals(Object obj)
    {
        boolean isEqual = true;
        // confirm object instanceof this object
        if (obj instanceof PaymentIfc)
        {

            PaymentIfc c = (PaymentIfc)obj; // downcast the input object

            // compare all the attributes of Payment
            if (Util.isObjectEqual(getPaymentAmount(), c.getPaymentAmount())
                    && Util.isObjectEqual(getReferenceNumber(), c.getReferenceNumber())
                    && Util.isObjectEqual(getEncipheredCardData(), c.getEncipheredCardData())
                    && Util.isObjectEqual(getDescription(), c.getDescription())
                    && Util.isObjectEqual(getTransactionID(), c.getTransactionID())
                    && Util.isObjectEqual(getBusinessDate(), c.getBusinessDate())
                    && Util.isObjectEqual(getPaymentAccountType(), c.getPaymentAccountType())
                    && Util.isObjectEqual(getBalanceDue(), c.getBalanceDue())
                    && Util.isObjectEqual(getPickupDeliveryItemDepositAmount(), c.getPickupDeliveryItemDepositAmount()))
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
        return isEqual;
    }

    /**
     * Retrieves payment amount.
     * 
     * @return payment amount
     */
    public CurrencyIfc getPaymentAmount()
    {
        return paymentAmount;
    }

    /**
     * Sets payment amount.
     * 
     * @param value payment amount
     */
    public void setPaymentAmount(CurrencyIfc value)
    {
        paymentAmount = value;
    }

    /**
     * Retrieves balance due.
     * 
     * @return balance due
     */
    public CurrencyIfc getBalanceDue()
    {
        return balanceDue;
    }

    /**
     * Sets balance due.
     * 
     * @param value balance due
     */
    public void setBalanceDue(CurrencyIfc value)
    {
        balanceDue = value;
    }

    /**
     * Retrieves the EncipheredCardData instance.
     * 
     * @return card number
     */
    public EncipheredCardDataIfc getEncipheredCardData()
    {
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
     * Retrieves reference number. In the case of a layaway payment, this
     * returns the layaway identifier.
     * 
     * @return reference number
     */
    public String getReferenceNumber()
    {
        return referenceNumber;
    }

    /**
     * Sets reference number. In the case of a layaway payment, this sets the
     * layaway identifier.
     * 
     * @param referenceNumber reference number
     */
    public void setReferenceNumber(String referenceNumber)
    {
        this.referenceNumber = referenceNumber;
    }

    /**
     * Retrieves description.
     * 
     * @return description
     */
    public String getDescription()
    {
        return description;
    }

    /**
     * Sets description.
     * 
     * @param value description
     */
    public void setDescription(String value)
    {
        description = value;
    }

    /**
     * Retrieves transaction identifier.
     * 
     * @return transaction identifier
     */
    public TransactionIDIfc getTransactionID()
    {
        return transactionID;
    }

    /**
     * Sets transaction identifier.
     * 
     * @param value transaction identifier
     */
    public void setTransactionID(TransactionIDIfc value)
    {
        transactionID = value;
    }

    /**
     * Retrieves business date.
     * 
     * @return business date
     */
    public EYSDate getBusinessDate()
    {
        return businessDate;
    }

    /**
     * Sets business date.
     * 
     * @param value business date
     */
    public void setBusinessDate(EYSDate value)
    {
        businessDate = value;
    }

    /**
     * Retrieves payment account type.
     * 
     * @return payment account type
     */
    public String getPaymentAccountType()
    {
        return paymentAccountType;
    }

    /**
     * Sets payment account type.
     * 
     * @param value payment account type
     */
    public void setPaymentAccountType(String value)
    {
        paymentAccountType = value;
    }

    /**
     * Retrieves Pickup and Delivery item amount.
     * 
     * @return Pickup and Delivery item amount
     */
    public CurrencyIfc getPickupDeliveryItemDepositAmount()
    {
        return pickupDeliveryItemDepositAmount;
    }

    /**
     * Sets Pickup and Delivery item amount.
     * 
     * @param value Pickup and Delivery item amount
     */
    public void setPickupDeliveryItemDepositAmount(CurrencyIfc value)
    {
        pickupDeliveryItemDepositAmount = value;
    }

    /**
     * Returns account-type descriptor associated with specified account type
     * code.
     * 
     * @param accountType account-type code
     * @return account-type descriptor
     */
    public String getAccountTypeDescriptor(String accountType)
    {
        int accountTypeCode = PaymentConstantsIfc.ACCOUNT_TYPE_CODE_UNSPECIFIED;
        if (accountType.equals(PaymentConstantsIfc.ACCOUNT_TYPE_HOUSE_ACCOUNT))
        {
            accountTypeCode = PaymentConstantsIfc.ACCOUNT_TYPE_CODE_HOUSE_ACCOUNT;
        }
        else if (accountType.equals(PaymentConstantsIfc.ACCOUNT_TYPE_LAYAWAY))
        {
            accountTypeCode = PaymentConstantsIfc.ACCOUNT_TYPE_CODE_LAYAWAY;
        }
        else if (accountType.equals(PaymentConstantsIfc.ACCOUNT_TYPE_ORDER))
        {
            accountTypeCode = PaymentConstantsIfc.ACCOUNT_TYPE_CODE_ORDER;
        }
        return (PaymentConstantsIfc.ACCOUNT_TYPE_DESCRIPTORS[accountTypeCode]);

    }

    /**
     * Returns account-type descriptor associated with this object's account
     * type code.
     * 
     * @return account-type descriptor
     */
    public String getAccountTypeDescriptor()
    {
        return (getAccountTypeDescriptor(paymentAccountType));
    }

    /**
     * Sets true if its is PDO transaction payment else false
     */
    public void setIsPDOPayment(boolean isPDOPayment)
    {
        this.isPDOPayment = isPDOPayment;
    }

    /**
     * Returns the flag for PDO transaction payment.
     * 
     * @return True if its PDO payment.
     */
    public boolean isPDOPayment()
    {
        return isPDOPayment;
    }

    /* (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString()
    {
        // build result string
        StringBuilder strResult = Util.classToStringHeader("Payment", getRevisionNumber(), hashCode());
        // add attributes to string
        strResult.append(Util.formatToStringEntry("paymentAmount", getPaymentAmount()))
                .append(Util.formatToStringEntry("referenceNumber", getReferenceNumber()))
                .append(Util.formatToStringEntry("description", getDescription()))
                .append(Util.formatToStringEntry("transactionID", getTransactionID()))
                .append(Util.formatToStringEntry("businessDate", getBusinessDate()))
                .append(Util.formatToStringEntry("paymentAccountType", getPaymentAccountType()))
                .append(Util.formatToStringEntry("balanceDue", getBalanceDue()));;
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
        return revisionNumber;
    }

}
