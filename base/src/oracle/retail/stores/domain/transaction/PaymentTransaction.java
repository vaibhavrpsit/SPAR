/* ===========================================================================
* Copyright (c) 1998, 2011, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/modules/domain/src/oracle/retail/stores/domain/transaction/PaymentTransaction.java /rgbustores_13.4x_generic_branch/4 2011/07/19 17:10:14 cgreene Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    cgreene   07/19/11 - store layaway and order ids in separate column from
 *                         house account number.
 *    ohorne    05/27/11 - implemented isHouseAccountPayment
 *    sgu       05/16/11 - move instant credit approval status to its own class
 *    cgreene   05/27/10 - convert to oracle packaging
 *    cgreene   05/26/10 - convert to oracle packaging
 *    abondala  01/03/10 - update header date
 *    vchengeg  02/27/09 - For the HPQC EJ defect 2602
 *    deghosh   12/15/08 - EJ i18n journal text format changes
 *    akandru   10/31/08 - EJ Changes_I18n
 *
 * ===========================================================================
 * $Log:
 *    10   360Commerce 1.9         4/12/2008 5:44:57 PM   Christian Greene
 *         Upgrade StringBuilder to StringBuilder
 *    9    360Commerce 1.8         12/18/2007 5:47:48 PM  Alan N. Sinton  CR
 *         29661: Changes per code review.
 *    8    360Commerce 1.7         11/27/2007 12:32:24 PM Alan N. Sinton  CR
 *         29661: Encrypting, masking and hashing account numbers for House
 *         Account.
 *    7    360Commerce 1.6         9/20/2007 11:29:19 AM  Rohit Sachdeva
 *         28813: Initial Bulk Migration for Java 5 Source/Binary
 *         Compatibility of All Products
 *    6    360Commerce 1.5         7/9/2007 3:29:48 PM    Charles D. Baker CR
 *         27489 - Aligned currency amounts to be consistent with other
 *         journalling behavior.
 *    5    360Commerce 1.4         4/25/2007 10:00:19 AM  Anda D. Cadar   I18N
 *         merge
 *    4    360Commerce 1.3         6/8/2006 6:11:44 PM    Brett J. Larsen CR
 *         18490 - UDM - InstantCredit AuthorizationResponseCode changed to a
 *         String
 *    3    360Commerce 1.2         3/31/2005 4:29:19 PM   Robert Pearse
 *    2    360Commerce 1.1         3/10/2005 10:24:02 AM  Robert Pearse
 *    1    360Commerce 1.0         2/11/2005 12:13:01 PM  Robert Pearse
 *
 *   Revision 1.7  2004/09/23 00:30:51  kmcbride
 *   @scr 7211: Inserting serialVersionUIDs in these Serializable classes
 *
 *   Revision 1.6  2004/05/11 23:03:02  jdeleau
 *   @scr 4218 Backout recent changes to remove TransactionDiscounts,
 *   going to go a different route and remove the newly added
 *   voids and grosses instead.
 *
 *   Revision 1.4  2004/02/17 16:18:52  rhafernik
 *   @scr 0 log4j conversion
 *
 *   Revision 1.3  2004/02/12 17:14:42  mcs
 *   Forcing head revision
 *
 *   Revision 1.2  2004/02/11 23:28:50  bwf
 *   @scr 0 Organize imports.
 *
 *   Revision 1.1.1.1  2004/02/11 01:04:34  cschellenger
 *   updating to pvcs 360store-current
 *
 *
 *
 *    Rev 1.5   Dec 01 2003 13:46:20   bwf
 * Updated for echeck declines.
 *
 *    Rev 1.4   25 Nov 2003 22:50:58   baa
 * implement new methods on interface
 *
 *    Rev 1.2   Nov 21 2003 16:56:52   nrao
 * Added Javadoc.
 *
 *    Rev 1.1   Nov 03 2003 18:22:30   nrao
 * Added implements InstantCreditTransactionIfc and related methods.
 *
 *    Rev 1.0   Aug 29 2003 15:40:54   CSchellenger
 * Initial revision.
 *
 *    Rev 1.0   Jun 03 2002 17:06:02   msg
 * Initial revision.
 *
 *    Rev 1.1   Mar 18 2002 23:11:36   msg
 * - updated copyright
 *
 *    Rev 1.0   Mar 18 2002 12:30:24   msg
 * Initial revision.
 *
 *    Rev 1.7   13 Mar 2002 17:56:26   vxs
 * Fixed toString()
 * Resolution for POS SCR-954: Domain - Arts Translation
 *
 *    Rev 1.6   12 Mar 2002 09:57:10   pjf
 * Get FinancialTotals from Factory.
 * Resolution for POS SCR-1550: Use Factory to get new object instances in POS & Domain
 *
 *    Rev 1.5   08 Mar 2002 15:56:58   pdd
 * Removed non-tax sales impact of House Acct Pmt.
 * Resolution for POS SCR-1534: House Account Payments are updating the Net Trans Nontaxable line
 *
 *    Rev 1.4   01 Mar 2002 15:17:38   pdd
 * Converted to use TenderTypeMapIfc for tender codes and descriptors.
 * Resolution for POS SCR-627: Make the Tender type list extendible.
 *
 *    Rev 1.3   12 Feb 2002 15:44:14   vxs
 * Fixed clone().
 * Resolution for POS SCR-954: Domain - Arts Translation
 *
 *    Rev 1.2   Dec 19 2001 20:25:08   mpm
 * Corrected to use factory to instantiate TransactionID object.
 *
 *    Rev 1.1   30 Nov 2001 15:56:28   jbp
 * exceed methods were moved into AbstractTenderableTransaction from SaleReturnTransaction.  They were removed from PaymentTransaction.  VoidTransaction overrides addTenderLineItems so that it does not check TenderLimits.
 * Resolution for POS SCR-348: Cannot overtender with StoreCredit on a layaway payment
 *
 *    Rev 1.0   Sep 20 2001 16:06:22   msg
 * Initial revision.
 *
 *    Rev 1.1   Sep 17 2001 12:40:06   msg
 * header update
 * ===========================================================================
 */
package oracle.retail.stores.domain.transaction;

import java.io.Serializable;
import java.util.Enumeration;
import java.util.Locale;
import java.util.StringTokenizer;

import oracle.retail.stores.commerceservices.common.currency.CurrencyIfc;
import oracle.retail.stores.domain.DomainGateway;
import oracle.retail.stores.domain.customer.CustomerIfc;
import oracle.retail.stores.domain.employee.EmployeeIfc;
import oracle.retail.stores.domain.financial.FinancialTotalsIfc;
import oracle.retail.stores.domain.financial.PaymentIfc;
import oracle.retail.stores.domain.store.WorkstationIfc;
import oracle.retail.stores.domain.tax.TaxIfc;
import oracle.retail.stores.domain.tender.TenderLimitsIfc;
import oracle.retail.stores.domain.tender.TenderLineItemIfc;
import oracle.retail.stores.domain.tender.TenderTypeMapIfc;
import oracle.retail.stores.domain.utility.EYSDate;
import oracle.retail.stores.domain.utility.InstantCreditIfc;
import oracle.retail.stores.foundation.utility.Util;
import oracle.retail.stores.foundation.utility.xml.XMLConversionException;
import oracle.retail.stores.foundation.utility.xml.XMLConverterIfc;
import oracle.retail.stores.utility.I18NConstantsIfc;
import oracle.retail.stores.utility.I18NHelper;
import oracle.retail.stores.utility.JournalConstantsIfc;

import org.w3c.dom.Element;

/**
 * Sale or return transaction object. Remember to set transaction type when
 * creating an instance or set it after instantiation.
 * 
 * @see oracle.retail.stores.domain.transaction.SaleReturnTransactionIfc
 * @version $Revision: /rgbustores_13.4x_generic_branch/4 $
 */
public class PaymentTransaction extends AbstractTenderableTransaction
       implements PaymentTransactionIfc, Cloneable, Serializable, InstantCreditTransactionIfc
{
    // This id is used to tell the compiler not to generate a new serialVersionUID.
    static final long serialVersionUID = -1620879657382436737L;

    /**
     * revision number supplied by source-code control system
     */
    public static final String revisionNumber = "$Revision: /rgbustores_13.4x_generic_branch/4 $";
    /**
     * sales associate
     */
    protected EmployeeIfc salesAssociate;
    /**
     * transaction tax data
     */
    protected TransactionTaxIfc transactionTax = null;
    /**
     * order id
     */
    protected String orderID = null;
    /**
     * payment container object which holds info such as payment amount, account
     * num, description.
     */
    protected PaymentIfc paymentInfo = null;
    /**
     * spaces buffer used by pad method
     */
    protected static String SPACES = "                                        ";

    /**
     * Instant Credit Enrollment
     */
    protected InstantCreditIfc instantCredit = null;

    /**
     * Constructs SaleReturnTransaction object.
     */
    public PaymentTransaction()
    {
        initialize();
    }

    /**
     * Constructs SaleReturnTransaction object.
     */
    public PaymentTransaction(int transType)
    {
        this();
        transactionType = transType;
    }

    /**
     */
    @Override
    protected void initialize()
    {
        super.initialize();
        paymentInfo = DomainGateway.getFactory().getPaymentInstance();
        paymentInfo.setDescription("House Account Payment");
        transactionTax = DomainGateway.getFactory().getTransactionTaxInstance();
        transactionTax.setTaxMode(TaxIfc.TAX_MODE_NON_TAXABLE);
    }

    /**
     * Sets transaction ID.
     *
     * @param value transaction identifier
     */
    @Override
    public void setTransactionID(String value)
    {
        super.setTransactionID(value);
        // set the transactionID by filling up TransactionIDIfc and setting it
        // into PaymentIfc.
        paymentInfo.setTransactionID(getTransactionIdentifier());
    }

    /**
     * Sets business day to specified value.
     *
     * @param value timestamp setting
     */
    @Override
    public void setBusinessDay(EYSDate value)
    {
        super.setBusinessDay(value);
        paymentInfo.setBusinessDate(value);
    }

    /**
     * Sets the container payment info object.
     *
     * @param value PaymentIfc representing payment info such as payment amount,
     *            account num, description
     */
    public void setPayment(PaymentIfc value)
    {
        paymentInfo = value;
        totals.updateTransactionTotalsForPayment(paymentInfo.getPaymentAmount());
    }

    /**
     * Gets the container payment info object.
     *
     * @return PaymentIfc representing payment info such as payment amount,
     *         account num, description
     */
    public PaymentIfc getPayment()
    {
        return paymentInfo;
    }

    /**
     * Sets payment amount. A convenience method.
     *
     * @param paymentAmount a CurrencyIfc object representing payment amount.
     */
    public void setPaymentAmount(CurrencyIfc value)
    {
        paymentInfo.setPaymentAmount(value);
        totals.updateTransactionTotalsForPayment(paymentInfo.getPaymentAmount());
    }

    /**
     * Gets payment amount. A convenience method.
     *
     * @return a CurrencyIfc object representing payment amount.
     */
    public CurrencyIfc getPaymentAmount()
    {
        return paymentInfo.getPaymentAmount();
    }

    /**
     * Sets account number
     *
     * @param accountNum String representing account number.
     */
    public void setAccountNum(String accountNum)
    {
        paymentInfo.setReferenceNumber(accountNum);
    }

    /**
     * Gets account number
     *
     * @return String representing account number.
     */
    public String getAccountNum()
    {
        return paymentInfo.getReferenceNumber();
    }

    /**
     * Gets description
     *
     * @return String representing description.
     */
    public String getDescription()
    {
        return paymentInfo.getDescription();
    }

    /**
     * Clones SaleReturnTransaction object
     *
     * @return instance of PaymentTransaction object
     */
    @Override
    public Object clone()
    {
        // instantiate new object
        PaymentTransaction pTrans = new PaymentTransaction();
        // set attributes
        setCloneAttributes(pTrans);

        // pass back object
        return pTrans;
    }

    /**
     * Sets clone attributes. This method is provided to facilitate
     * extensibility.
     *
     * @param newClass new instance of PaymentTransaction
     */
    protected void setCloneAttributes(PaymentTransaction newClass)
    {

        // set attributes
        super.setCloneAttributes((Transaction)newClass);
        newClass.setPayment((PaymentIfc)(getPayment().clone()));
        newClass.setTransactionTotals((TransactionTotalsIfc)(totals.clone()));
        newClass.setTransactionTax((TransactionTaxIfc)transactionTax.clone());
        // clone tender line items
        TenderLineItemIfc[] tli = getTenderLineItems();
        if (tli != null)
        {
            TenderLineItemIfc[] tclone = new TenderLineItemIfc[tli.length];
            for (int i = 0; i < tli.length; i++)
            {
                tclone[i] = (TenderLineItemIfc)tli[i].clone();
            }
            newClass.setTenderLineItems(tclone);
        }
        // confirm customer exists before cloning
        if (customer != null)
        {
            newClass.setCustomer((CustomerIfc)customer.clone());
        }
    }

    /**
     * Calculates FinancialTotals based on current transaction.
     *
     * @return FinancialTotalsIfc object
     */
    public FinancialTotalsIfc getFinancialTotals()
    {
        FinancialTotalsIfc financialTotals =
          DomainGateway.getFactory().getFinancialTotalsInstance();

        // gross total is item subtotal with discount applied but no discount in this transaction
        // so don't have to subtract discount total.
        CurrencyIfc gross = totals.getSubtotal();
        // handle transaction values
        if (transactionType == TransactionIfc.TYPE_HOUSE_PAYMENT)
        {                              

          //House payments amount and count are a subset of Net Trans. Nontaxable
          //in Transaction Summary section of summary reports.
          financialTotals.addAmountHousePayments(getPaymentAmount());
          financialTotals.addCountHousePayments(1);
        }                              
        else
        {
            // handle discount amounts
            financialTotals.setAmountTransactionDiscounts(totals.getTransactionDiscountTotal());
            financialTotals.addNumberTransactionDiscounts(0);//no discounts

            //apply payment towards non taxable transaction sales
            financialTotals.addAmountGrossNonTaxableTransactionSales(gross);
            financialTotals.addCountGrossNonTaxableTransactionSales(1);
        }

        TenderLineItemIfc tli;
        // set up enumeration
        Enumeration<TenderLineItemIfc> enumer= tenderLineItemsVector.elements();
        // if elements exist, loop through them
        while (enumer.hasMoreElements())
        {                              
            tli = enumer.nextElement();
            // do not track mail bank check for counts!!!
            if (tli.getTypeCode() != TenderLineItemIfc.TENDER_TYPE_MAIL_BANK_CHECK)
            {
                financialTotals =
                  financialTotals.add(getFinancialTotalsFromTender(tli));
            }
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

        return (financialTotals);
    }

    /**
     * Check line quantities and reset transaction type, if needed.
     */
    protected void resetTransactionType()
    {
        // set default transaction type
        setTransactionType(TransactionIfc.TYPE_HOUSE_PAYMENT);
    }

    /**
     * Tests tender line item to see if it can be added. This tests to see if
     * maximum refund value has been overrun. This transaction type doesn't have
     * to mess with refund but needs to implement since implementing
     * RetailTransactionIfc
     *
     * @param item TenderLineItemIfc item to be added
     * @return boolean true if limit exceeded.
     */
    public boolean exceedsMaxCashRefundLimit(TenderLineItemIfc item)
    {
        return false;
    }

    /**
     * Sets customer attribute and performs other operations associated with
     * assigning a customer to a transaction, such as setting discount rules.
     *
     * @param value customer
     */
    public void linkCustomer(CustomerIfc value)
    {
        setCustomer(value);
    }

    /**
     * Retrieves tax object.
     *
     * @return tax object
     */
    public TransactionTaxIfc getTransactionTax()
    {
        return (transactionTax);
    }

    /**
     * Sets tax attribute.
     *
     * @param value tax
     */
    public void setTransactionTax(TransactionTaxIfc value)
    {
        transactionTax = value;
    }

    /* (non-Javadoc)
     * @see oracle.retail.stores.domain.transaction.PaymentTransactionIfc#isHouseAccountPayment()
     */
    @Override
    public boolean isHouseAccountPayment()
    {
        return TransactionConstantsIfc.TYPE_HOUSE_PAYMENT == getTransactionType();
    }

    /**
     * Method to default display string function.
     *
     * @return String representation of object
     */
    public String toString()
    {
        StringBuilder strResult = Util.classToStringHeader("PaymentTransaction", getRevisionNumber(), hashCode());
        strResult.append(super.toString())
            .append(Util.formatToStringEntry("Transaction Tax", getTransactionTax()))
            .append(Util.formatToStringEntry("Payment", getPayment()));
        // pass back result
        return (strResult.toString());
    }

    /**
     * Returns default journal string.
     *
     * @param journalLocale locale received from the client
     * @return default journal string
     */
    public String toJournalString(Locale journalLocale)
    {
        StringBuilder strResult = new StringBuilder();
        // start with Transaction journal data
        strResult.append(super.toJournalString(journalLocale));

        // journal transaction modifiers
        strResult.append(journalTransactionModifiers(journalLocale));

        // journal line items
        //strResult.append(journalLineItems());

        // tender line items not journaled here (yet)

        // pass back result
        return(strResult.toString());
    }

    /**
     * Write journal header to specified string buffer.
     *
     * @return journal fragment string
     */
    public String journalHeader(Locale journalLocale)
    {
        StringBuilder strResult = new StringBuilder();
        strResult.append(super.toJournalString(journalLocale));
        // add cashier/sales associate data
        Object[] dataArgs = new Object[]{getCashier().getEmployeeID()};
        strResult.append(I18NHelper.getString(I18NConstantsIfc.EJOURNAL_TYPE,
				JournalConstantsIfc.CASHIER_LABEL, dataArgs,
				journalLocale));
        // pass back result
        return(strResult.toString());
    }

    /**
     * Write transaction modifiers to journal string.
     *
     * @param journalLocale locale received from the client
     * @return journal fragment string
     */
    public String journalTransactionModifiers(Locale journalLocale)
    {
        StringBuilder strResult = new StringBuilder();
        if (getCustomer() != null)
        {
        	Object[] dataArgs = new Object[]{getCustomer().getCustomerID().trim()};
            strResult.append(Util.EOL)
                     .append(I18NHelper.getString(I18NConstantsIfc.EJOURNAL_TYPE,
             				JournalConstantsIfc.LINK_CUSTOMER_LABEL, dataArgs,
            				journalLocale));
        }
        return(strResult.toString());
    }

    /**
     * Write line item eqivalent of payment to journal string.
     *
     * @param journalLocale locale received from the client
     * @result journal fragment string
     */
    public String journalLineItems(Locale journalLocale)
    {
        int PLU_DESCRIPTION_LENGTH = 26;
        StringBuilder strResult =   new StringBuilder();

        // build description, price line
        // truncate description, if necessary
        String  desc    =   getDescription();
        int     len     =   desc.length();
        if (len > PLU_DESCRIPTION_LENGTH)
        {
            desc = desc.substring(0, PLU_DESCRIPTION_LENGTH);
            len  = PLU_DESCRIPTION_LENGTH;
        }
        strResult.append(Util.EOL);
        //strResult.append(desc);
        // insert price two spaces from end
        CurrencyIfc currencyPrice   = getPaymentAmount();
        String      strPrice        = "";
        strPrice = currencyPrice.toFormattedString();
        //strResult.append(strPrice);
       // strResult.append(" ");

        // insert tax mode at end
        String  taxFlag     = new String ("N");
        //strResult.append(taxFlag);

        StringTokenizer st = new StringTokenizer(desc," ",false);
        String t="";
        while (st.hasMoreElements()) t += st.nextElement();
        Object[] dataArgstemp = new Object[2];
        dataArgstemp[0] = strPrice;
        dataArgstemp[1] = taxFlag;
        strResult.append(I18NHelper.getString(I18NConstantsIfc.EJOURNAL_TYPE,
 				JournalConstantsIfc.JOURNAL_ENTRY_PREFIX+t, dataArgstemp,
				journalLocale));

        strResult.append(Util.EOL);
        Object[] dataArgs = new Object[]{ getPayment().getReferenceNumber() };
        strResult.append(I18NHelper.getString(I18NConstantsIfc.EJOURNAL_TYPE,
 				JournalConstantsIfc.ACCOUNT_NUMBER_LABEL, dataArgs,
				journalLocale));

        return strResult.toString();
    }

    /**
     * Creates string of spaces to pad string buffer on right. </P>
     * 
     * @param maxLength maximum length of field
     * @param dataLength length of data in field
     * @param numSpaces number of spaces to follow field
     * @return padded string
     */
    protected String pad(int maxLength, int dataLength, int numSpaces)
    {                                  
        int offset = maxLength - dataLength + numSpaces;
        if (offset < 0)
        {
            offset = 0;
        }
        return(SPACES.substring(0, offset));
    }                                  

    // Methods to support conversion and restoration to/from XML

    /**
     * Restores the object from the contents of the xml tree based on the
     * current node property of the converter.
     * 
     * @param converter is the conversion utility
     * @exception XMLConversionException if error translating from XML
     */
    public void translateFromElement(XMLConverterIfc converter) throws XMLConversionException
    {
        Element top = converter.getCurrentElement();
        Element[] properties = converter.getChildElements(top,XMLConverterIfc.TAG_PROPERTY);

        // Retrieve and store the values for each property
        for (int i = 0; i < properties.length; i++)
        {
            Element element = properties[i];
            String name = element.getAttribute("name");

            if ("transactionIdentifier".equals(name))
            {
                setTransactionIdentifier((TransactionIDIfc) converter.getPropertyObject(element));
            }
            else if ("transactionTax".equals(name))
            {
                transactionTax = (TransactionTaxIfc) converter.getPropertyObject(element);
            }
            else if ("transactionType".equals(name))
            {
                transactionType = Integer.parseInt(converter.getElementText(element));
            }
            else if ("transactionStatus".equals(name))
            {
                transactionStatus = Integer.parseInt(converter.getElementText(element));
            }
            else if ("previousTransactionStatus".equals(name))
            {
                previousTransactionStatus = Integer.parseInt(converter.getElementText(element));
            }
            else if ("timestampBegin".equals(name))
            {
                timestampBegin = (EYSDate) converter.getPropertyObject(element);
            }
            else if ("timestampEnd".equals(name))
            {
                timestampEnd = (EYSDate) converter.getPropertyObject(element);
            }
            else if ("businessDay".equals(name))
            {
                businessDay  = (EYSDate) converter.getPropertyObject(element);
            }
            else if ("workstation".equals(name))
            {
                workstation = (WorkstationIfc) converter.getPropertyObject(element);
            }
            else if ("cashier".equals(name))
            {
                cashier = (EmployeeIfc) converter.getPropertyObject(element);
            }
            else if ("tillID".equals(name))
            {
                tillID = (String) converter.getPropertyObject(element);
            }
            else if ("tenderLimits".equals(name))
            {
                setTenderLimits((TenderLimitsIfc) converter.getPropertyObject(element));
            }
            else if ("tenderLineItemsVector".equals(name))
            {
                setTenderLineItems((TenderLineItemIfc[]) converter.getPropertyObject(element));
            }
            else if ("transactionTotals".equals(name))
            {
                totals = (TransactionTotalsIfc) converter.getPropertyObject(element);
            }
            else if ("salesAssociate".equals(name))
            {
                salesAssociate = (EmployeeIfc) converter.getPropertyObject(element);
            }
            else if ("customer".equals(name))
            {
                customer = (CustomerIfc) converter.getPropertyObject(element);
            }
            else if ("transactonTax".equals(name))
            {
                transactionTax = (TransactionTaxIfc) converter.getPropertyObject(element);
            }
        }
    }

    /**
     * Get instant credit
     * 
     * @return instant credit
     */
    public InstantCreditIfc getInstantCredit()
    {
        return this.instantCredit;
    }

    /**
     * Set instant credit
     * 
     * @param instantCredit instant credit
     */
    public void setInstantCredit(InstantCreditIfc instantCredit)
    {
        this.instantCredit = instantCredit;
    }

    /**
     * Determine if two objects are identical.
     *
     * @param obj object to compare with
     * @return true if the objects are identical, false otherwise
     */
    @Override
    public boolean equals(Object obj)
    {
        boolean equal = false;

        // If it's a PaymentTransaction, compare its attributes
        if (obj instanceof PaymentTransaction)
        {
            // downcast the input object
            PaymentTransaction c = (PaymentTransaction) obj;
            if (!super.equals(obj))
            {
                equal = false;
            }
            //now no line items, however still need to compare payment amounts and such.
            else if (!Util.isObjectEqual(paymentInfo, c.paymentInfo))
            {
                equal = false;
            }
            else if (!Util.isObjectEqual(transactionTax, c.transactionTax))
            {
                equal = false;
            }
            else
            {
                equal = true;
            }
        }

        return(equal);
    }

    /**
     * Retrieves the Team Connection revision number.
     *
     * @return String representation of revision number
     */
    public String getRevisionNumber()
    {
        // return string
        return (revisionNumber);
    }

}
