/* ===========================================================================
* Copyright (c) 2009, 2013, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    yiqzhao   08/18/14 - set transaction type
 *    icole     08/05/14 - corrected bill pay journaling.
 *    ckancher  08/16/13 - Added currency information
 *    ohorne    05/27/11 - removed unused imports
 *    sgu       05/16/11 - move instant credit approval status to its own class
 *    nkgautam  09/03/10 - implemented InstantcredittransactionIfc
 *    nkgautam  07/28/10 - Bill Payment Report changes
 *    nkgautam  07/16/10 - EJ changes for bill payments
 *    nkgautam  06/22/10 - bill pay changes
 *    nkgautam  06/21/10 - Bill Pay Transaction class creation
 * ===========================================================================
 */
package oracle.retail.stores.domain.transaction;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Locale;

import oracle.retail.stores.commerceservices.common.currency.CurrencyIfc;
import oracle.retail.stores.commerceservices.common.currency.CurrencyTypeIfc;
import oracle.retail.stores.domain.DomainGateway;
import oracle.retail.stores.domain.customer.CustomerIfc;
import oracle.retail.stores.domain.financial.BillIfc;
import oracle.retail.stores.domain.financial.BillPayIfc;
import oracle.retail.stores.domain.financial.FinancialTotalsIfc;
import oracle.retail.stores.domain.tax.TaxIfc;
import oracle.retail.stores.domain.tender.TenderLineItemIfc;
import oracle.retail.stores.domain.tender.TenderTypeMapIfc;
import oracle.retail.stores.domain.utility.InstantCreditIfc;
import oracle.retail.stores.common.utility.Util;
import oracle.retail.stores.utility.I18NConstantsIfc;
import oracle.retail.stores.utility.I18NHelper;
import oracle.retail.stores.utility.JournalConstantsIfc;

public class BillPayTransaction extends AbstractTenderableTransaction
implements BillPayTransactionIfc, InstantCreditTransactionIfc
{


    /**
     * Generated Serial UID
     */
    private static final long serialVersionUID = 6522787799644689691L;

    /**
     * Bill Pay Container object containing bill info
     */
    protected BillPayIfc billPayInfo = null;

    /**
     * transaction tax data
     */
    protected TransactionTaxIfc transactionTax = null;

    /**
     * Instant Credit Enrollment
     */
    protected InstantCreditIfc instantCredit = null;

    /**
     * Currency type for Transaction
     */
    protected CurrencyTypeIfc currencyType; 
   
    /**
     * Country Code for the transaction
     */
    protected String transactionCountryCode;

    /**
     * Constructor
     */
    public BillPayTransaction()
    {
        initialize();
    }

    /**
     *  Initializes Transaction values
     */
    public void initialize()
    {
        super.initialize();
        billPayInfo = DomainGateway.getFactory().getBillPayInstance();
        transactionType = TransactionConstantsIfc.TYPE_BILL_PAY;
        billPayInfo.setDescription("Bill Payment");
        transactionTax = DomainGateway.getFactory().getTransactionTaxInstance();
        transactionTax.setTaxMode(TaxIfc.TAX_MODE_NON_TAXABLE);
        CurrencyTypeIfc baseCurrency = DomainGateway.getBaseCurrencyType();
        currencyType=baseCurrency;
    }

    /**
     * Gets the Financial Totals
     */
    public FinancialTotalsIfc getFinancialTotals()
    {
        FinancialTotalsIfc financialTotals = DomainGateway.getFactory().getFinancialTotalsInstance();

        CurrencyIfc gross = totals.getSubtotal();
        financialTotals.addAmountBillPayments(gross);
        financialTotals.addCountBillPayments(1);
        financialTotals.addAmountGrossNonTaxableTransactionSales(gross);
        financialTotals.addCountGrossNonTaxableTransactionSales(1);

        TenderLineItemIfc tli;
        // set up enumeration
        Enumeration enumer= (Enumeration)(tenderLineItemsVector.elements());
        // if elements exist, loop through them
        while (enumer.hasMoreElements())
        {                               // begin loop through tender lines
            tli = (TenderLineItemIfc)enumer.nextElement();

            financialTotals =
                financialTotals.add(getFinancialTotalsFromTender(tli));
        }

        // The customer has been given change in cash, account for it the
        // the cash tender accumulator.
        if (totals.getBalanceDue().signum() == CurrencyIfc.NEGATIVE)
        {
            CurrencyIfc amtIn  = DomainGateway.getBaseCurrencyInstance();
            CurrencyIfc amtOut = totals.getBalanceDue().negate();
            TenderTypeMapIfc tenderTypeMap = DomainGateway.getFactory().getTenderTypeMapInstance();
            financialTotals.getTenderCount().addTenderItem(
                    tenderTypeMap.getDescriptor(TenderLineItemIfc.TENDER_TYPE_CASH),
                    0, 1, amtIn, amtOut);
        }
        return financialTotals;
    }

    /**
     * Links Customer
     */
    public void linkCustomer(CustomerIfc value)
    {
        setCustomer(value);
    }

    /**
     * Gets the BillPayIfc
     */
    public BillPayIfc getBillPayInfo()
    {
        return billPayInfo;
    }


    /**
     * Sets the BillPayIfc and transaction totals
     */
    public void setBillPayInfo(BillPayIfc billPayInfo)
    {
        this.billPayInfo = billPayInfo;
        updateTransactionTotals();
    }

    /**
     * Gets updated bills Amount paid
     * @return
     */
    protected void updateTransactionTotals()
    {
        totals.getSubtotal().setZero();
        totals.getSaleSubtotal().setZero();
        totals.setQuantityTotal(BigDecimal.ZERO);
        totals.setQuantitySale(BigDecimal.ZERO);
        CurrencyIfc paymentAmount = DomainGateway.getBaseCurrencyInstance();
        ArrayList<BillIfc> billsList =  billPayInfo.getBillsList();
        if(billsList.size() != 0)
        {
            for(int billCount=0 ; billCount < billsList.size() ; billCount++)
            {
                BillIfc bill = (BillIfc)billsList.get(billCount);
                if(bill.getBillAmountPaid() != null)
                {
                    totals.updateTransactionTotalsForBillPayment(bill.getBillAmountPaid());
                }
            }
        }
        else
        {
            paymentAmount.setZero();
        }
    }

    /**
     * Gets the Tender Transaction totals
     */
    public TransactionTotalsIfc getTenderTransactionTotals()
    {
        return totals;
    }

    /**
     * Journals bill details
     * 
     * @param journalLocale
     * @return
     */
    public String journalBills(Locale journalLocale)
    {

        StringBuffer result = new StringBuffer();
        CurrencyIfc zeroAmount = DomainGateway.getBaseCurrencyInstance();
        zeroAmount.setZero();
        BillPayIfc billPaymentInfo = this.getBillPayInfo();

        result.append(Util.EOL);

        Object data[];
        data = new Object[] { billPaymentInfo.getFirstLastName()};
        result.append(I18NHelper.getString(I18NConstantsIfc.EJOURNAL_TYPE, JournalConstantsIfc.CUSTOMER_NAME_LABEL, data,
                journalLocale));
        result.append(Util.EOL);

        ArrayList<BillIfc> bills = billPaymentInfo.getBillsList();
        for (BillIfc posPaidBill : bills)
        {
            if(posPaidBill.getBillAmountPaid()!= null && posPaidBill.getBillAmountPaid().compareTo(zeroAmount) > 0)
            {
                result.append(Util.EOL);
                data = new Object[] { posPaidBill.getAccountNumber()};
                result.append(I18NHelper.getString(I18NConstantsIfc.EJOURNAL_TYPE, JournalConstantsIfc.BILLPAY_ACCOUNT_NUMBER_LABEL, data,
                        journalLocale));
                result.append(Util.EOL);

                data = new Object[] { posPaidBill.getBillNumber()};
                result.append(I18NHelper.getString(I18NConstantsIfc.EJOURNAL_TYPE, JournalConstantsIfc.BILLPAY_BILL_NUMBER_LABEL, data,
                        journalLocale));
                result.append(Util.EOL);

                data = new Object[] { posPaidBill.getDueDate()};
                result.append(I18NHelper.getString(I18NConstantsIfc.EJOURNAL_TYPE, JournalConstantsIfc.BILLPAY_DUE_DATE_LABEL, data,
                        journalLocale));
                result.append(Util.EOL);

                data = new Object[] { posPaidBill.getBillAmountPaid().toFormattedString(journalLocale)};
                result.append(I18NHelper.getString(I18NConstantsIfc.EJOURNAL_TYPE, JournalConstantsIfc.BILLPAY_PAID_AMOUNT_LABEL, data,
                        journalLocale));
                result.append(Util.EOL);
            }
        }
        return result.toString();
    }

    /**
     * Journal individual bill payment details.
     * 
     * @param posPaidBill
     * @param journalLocale
     * @return
     * 
     * @since 14.1
     */
    public String journalBill(BillIfc posPaidBill, Locale journalLocale)
    {
        StringBuffer result = new StringBuffer();
        Object data[];
        result.append(Util.EOL);
        data = new Object[] { posPaidBill.getAccountNumber()};
        result.append(I18NHelper.getString(I18NConstantsIfc.EJOURNAL_TYPE, JournalConstantsIfc.BILLPAY_ACCOUNT_NUMBER_LABEL, data,
                journalLocale));
        result.append(Util.EOL);

        data = new Object[] { posPaidBill.getBillNumber()};
        result.append(I18NHelper.getString(I18NConstantsIfc.EJOURNAL_TYPE, JournalConstantsIfc.BILLPAY_BILL_NUMBER_LABEL, data,
                journalLocale));
        result.append(Util.EOL);

        data = new Object[] { posPaidBill.getDueDate()};
        result.append(I18NHelper.getString(I18NConstantsIfc.EJOURNAL_TYPE, JournalConstantsIfc.BILLPAY_DUE_DATE_LABEL, data,
                journalLocale));
        result.append(Util.EOL);

        data = new Object[] { posPaidBill.getBillAmountPaid().toFormattedString(journalLocale)};
        result.append(I18NHelper.getString(I18NConstantsIfc.EJOURNAL_TYPE, JournalConstantsIfc.BILLPAY_PAID_AMOUNT_LABEL, data,
                journalLocale));
        result.append(Util.EOL);
        return result.toString();
    }
    
    /**
     * Journals the bill pay customer.
     * 
     * @param journalLocale
     * @return
     * 
     * @since 14.1
     */
    public String journalBillPayCustomer(Locale journalLocale)
    {
        StringBuffer result = new StringBuffer();
        CurrencyIfc zeroAmount = DomainGateway.getBaseCurrencyInstance();
        zeroAmount.setZero();
        BillPayIfc billPaymentInfo = this.getBillPayInfo();
        result.append(Util.EOL);
        Object data[];
        data = new Object[] { billPaymentInfo.getFirstLastName()};
        result.append(I18NHelper.getString(I18NConstantsIfc.EJOURNAL_TYPE, JournalConstantsIfc.CUSTOMER_NAME_LABEL, data,
                journalLocale));
        result.append(Util.EOL);
        return result.toString();
    }

    /**
     * Gets the transaction tax
     * @return
     */
    public TransactionTaxIfc getTransactionTax()
    {
        return transactionTax;
    }


    /**
     * Sets the transaction tax
     * @param transactionTax
     */
    public void setTransactionTax(TransactionTaxIfc transactionTax)
    {
        this.transactionTax = transactionTax;
    }

    /**
     * Clones
     */
    public Object clone()
    {
        BillPayTransaction c = new BillPayTransaction();
        setCloneAttributes(c);
        return ((Object) c);
    }

    /**
     * Sets attributes in clone of this object.
     * @param newClass
     *            new instance of object
     */
    public void setCloneAttributes(BillPayTransaction newClass)
    {
        super.setCloneAttributes((Transaction) newClass);
        newClass.setBillPayInfo(getBillPayInfo());
        newClass.setTransactionTotals((TransactionTotalsIfc) (totals.clone()));
        newClass.setTransactionTax((TransactionTaxIfc) transactionTax.clone());
        newClass.setCurrencyType(this.getCurrencyType());
        newClass.setTransactionCountryCode(this.getTransactionCountryCode());
    }

    /**
     * Return an instant credit object associated with this transaction. This
     * value is populated if a customer has applied for a houseCard.
     * @return instantCredit credit card object
     * @see oracle.retail.stores.domain.transaction.InstantCreditTransactionIfc#getInstantCredit()
     */
    public InstantCreditIfc getInstantCredit()
    {
        return this.instantCredit;
    }

    /**
     * Set the instant credit object. This is set when a customer has applied
     * for a house card.
     * @param instantCredit credit card
     * @see oracle.retail.stores.domain.transaction.InstantCreditTransactionIfc#setInstantCredit(oracle.retail.stores.domain.utility.InstantCreditIfc)
     */
    public void setInstantCredit(InstantCreditIfc instantCredit)
    {
        this.instantCredit = instantCredit;
    }
    /** Returns the transaction country code.
    *
    *  @return String
    */
    public String getTransactionCountryCode()
    {
        return transactionCountryCode;
    }

    /** Sets the ISO country code for this transaction.
    *
    *  @param String transactionCountryCode
    */
    public void setTransactionCountryCode(String transactionCountryCode)
    {
        this.transactionCountryCode = transactionCountryCode;
    }   
    
    /** Returns the transaction currency type.
    *
    *  @return CurrencyTypeIfc
    */
    public CurrencyTypeIfc getCurrencyType()
    {
        return currencyType;
    }

    /** Sets the currency type for this transaction.
    *
    *  @param CurrencyTypeIfc currencyType
    */
    public void setCurrencyType(CurrencyTypeIfc currencyType)
    {
        this.currencyType = currencyType;
    }

}
