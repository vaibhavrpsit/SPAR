/* ===========================================================================
* Copyright (c) 1998, 2013, Oracle and/or its affiliates. All rights reserved.
 * ===========================================================================
 * $Header: rgbustores/modules/domain/src/oracle/retail/stores/domain/transaction/RedeemTransaction.java /main/14 2013/07/09 14:32:43 jswan Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    jswan     07/09/13 - Fixed issues saving the cash adjustment total to the
 *                         history tables for order, layaway, redeem and voided
 *                         transactions.
 *    cgreene   05/27/10 - convert to oracle packaging
 *    cgreene   05/26/10 - convert to oracle packaging
 *    abondala  01/03/10 - update header date
 *    mchellap  12/22/08 - Added java doc
 *    mchellap  12/19/08 - Added flags to indicate redeem tender type
 *
 * ===========================================================================
 * $Log:
 *  11   360Commerce 1.10        4/12/2008 5:44:57 PM   Christian Greene
 *       Upgrade StringBuffer to StringBuilder
 *  10   360Commerce 1.9         8/26/2007 4:30:07 PM   Jack G. Swan    Fixed a
 *        problem with the redeemed tender updating the till/register totals.
 *  9    360Commerce 1.8         7/30/2007 10:03:06 AM  Owen D. Horne   CR
 *       27868: now redeemed Gift Cards, Gift Certificates and Store Credits
 *       are not added to Tender Summary
 *  8    360Commerce 1.7         6/26/2007 11:13:58 AM  Ashok.Mondal    I18N
 *       changes to export and import POSLog.
 *  7    360Commerce 1.6         5/23/2007 7:10:48 PM   Jack G. Swan    Fixed
 *       issues with tills and CurrencyID.
 *  6    360Commerce 1.5         4/25/2007 10:00:19 AM  Anda D. Cadar   I18N
 *       merge
 *  5    360Commerce 1.4         8/4/2006 4:17:52 PM    Brendan W. Farrell
 *       Merge fix from v7.x to have correct values on till summary report.
 *  4    360Commerce 1.3         1/22/2006 11:41:57 AM  Ron W. Haight   Removed
 *        references to com.ibm.math.BigDecimal
 *  3    360Commerce 1.2         3/31/2005 4:29:36 PM   Robert Pearse
 *  2    360Commerce 1.1         3/10/2005 10:24:35 AM  Robert Pearse
 *  1    360Commerce 1.0         2/11/2005 12:13:36 PM  Robert Pearse
 *
 * Revision 1.18  2004/09/23 00:30:51  kmcbride
 * @scr 7211: Inserting serialVersionUIDs in these Serializable classes
 *
 * Revision 1.17  2004/06/17 16:26:14  blj
 * @scr 5678 - code cleanup
 *
 * Revision 1.16  2004/05/25 15:29:48  blj
 * @scr 5111 - fixed printing errors for redeem
 *
 * Revision 1.15  2004/05/11 16:03:39  blj
 * @scr 4603 -fixed gift card post void for issue/reload/credit/redeem
 *
 * Revision 1.14  2004/04/29 19:24:58  lzhao
 * @scr 4553: Summary Report for redeem and redeem void.
 *
 * Revision 1.13  2004/04/22 22:34:55  blj
 * @scr 3872-more cleanup
 *
 * Revision 1.12  2004/04/21 15:03:07  blj
 * @scr 3871 - foreign currency updates
 *
 * Revision 1.11  2004/04/16 14:58:26  blj
 * @scr 3872 - fixed a few flow and screen text issues.
 *
 * Revision 1.10  2004/04/08 22:04:15  bjosserand
 * @scr 4093 Transaction Reentry
 *
 * Revision 1.9  2004/04/07 22:40:03  lzhao
 * @scr 4218: take unused import.
 *
 * Revision 1.8  2004/04/07 20:56:49  lzhao
 * @scr 4218: add gift card info for summary report.
 *
 * Revision 1.7  2004/04/01 15:58:18  blj
 * @scr 3872 Added training mode, toggled the redeem button based
 * on transaction==null and fixed post void problems.
 *
 * Revision 1.6  2004/03/25 17:12:16  blj
 * @scr 3872 - fixed summary reports for store credit redeem.
 *
 * Revision 1.5  2004/03/24 17:06:37  blj
 * @scr 3871-3872 - Added the ability to reprint redeem transaction receipts and added a void receipt.
 *
 * Revision 1.4  2004/03/22 17:26:41  blj
 * @scr 3872 - added redeem security, receipt printing and saving redeem transactions.
 *
 * Revision 1.3  2004/03/16 18:27:06  cdb
 * @scr 0 Removed tabs from all java source code.
 *
 * Revision 1.2  2004/03/11 19:53:45  blj
 * @scr 3871 - Updates and additions for Redeem Transactions.
 *
 * Revision 1.1  2004/03/08 23:30:05  blj
 * @scr 3871 - Redeem transaction and interfaces.
 *
 *
 *
 * ===========================================================================
 */
package oracle.retail.stores.domain.transaction;

// Foundation Imports
import java.math.BigDecimal;

import oracle.retail.stores.commerceservices.common.currency.CurrencyIfc;
import oracle.retail.stores.common.utility.Util;
import oracle.retail.stores.domain.DomainGateway;
import oracle.retail.stores.domain.customer.CustomerIfc;
import oracle.retail.stores.domain.financial.FinancialTotals;
import oracle.retail.stores.domain.financial.FinancialTotalsIfc;
import oracle.retail.stores.domain.tender.TenderDescriptorIfc;
import oracle.retail.stores.domain.tender.TenderGiftCardIfc;
import oracle.retail.stores.domain.tender.TenderGiftCertificateIfc;
import oracle.retail.stores.domain.tender.TenderLineItemIfc;
import oracle.retail.stores.domain.tender.TenderStoreCreditIfc;

//------------------------------------------------------------------------------
/**
    This class represents a redeem transaction. <P>
    @version $Revision: /main/14 $
**/
//------------------------------------------------------------------------------
public class RedeemTransaction extends AbstractTenderableTransaction implements RedeemTransactionIfc
{
    // This id is used to tell
    // the compiler not to generate a
    // new serialVersionUID.
    //
    static final long serialVersionUID = 5727315133973182224L;

    /**
        The revision number assigned by the revision control system.
    **/
    public static final String revisionNumber = "$Revision: /main/14 $";

    /**
        The redeemable document.
    **/
    //RedeemableIfc document = null;

    /**
     The redeemable document.
     **/
    TenderLineItemIfc redeemTender = null;

    /**
     The redeem ID.
     **/
    String redeemID = "";

    /**
       Flag indicating that the transaction is re-entry mode.
    **/
    boolean reentryMode = false;

    /**
       Currency ID
    **/
    protected int currencyID;

    /**
     * Flag indicating that the transaction is a gift card redeem.
     */
    boolean giftCardRedeem = false;

    /**
     * Flag indicating that the transaction is a gift certificate redeem.
     */
    boolean giftCertificateRedeem = false;

    /**
     * Flag indicating that the transaction is a store credit redeem.
     */
    boolean storeCreditRedeem = false;

    //---------------------------------------------------------------------
    /**
        Default constructor
        Initializes transaction
    **/
    //---------------------------------------------------------------------
    public RedeemTransaction()
    {
        super.initialize();
        setTransactionType(TransactionIfc.TYPE_REDEEM);
    }

    //---------------------------------------------------------------------
    /**
     Sets the redeemable document
     @param value RedeemableIfc
     **/
    //---------------------------------------------------------------------
    public void setRedeemTender(TenderLineItemIfc value)
    {
        redeemTender = value;
        //TenderLineItemIfc redeemTender = getRedeemTender();
        if (value instanceof TenderStoreCreditIfc)
        {
            setStoreCreditRedeem(true);
        }
        else if (value instanceof TenderGiftCardIfc)
        {
            setGiftCardRedeem(true);
        }
        else if (value instanceof TenderGiftCertificateIfc)
        {
            setGiftCertificateRedeem(true);
        }
    }

    /**
     * Adds the redeem tender line item and updates
     * transaction totals for the redeem tender amount as
     * a payment.
     */
    public void addRedeemTender(TenderLineItemIfc item)
    {
        // Add redeem tender to the transaction
        setRedeemTender(item);

        // update transaction totals
        updateRedeemTenderTotals(item.getAmountTender().negate());
    }

    /**
     * Update negated redeem tender amount as a payment.
     * @param amount
     */
    public void updateRedeemTenderTotals(CurrencyIfc amount)
    {
        //totals.updateTenderTotals(getTenderLineItems());
        totals.updateTransactionTotalsForPayment(amount);
    }

//---------------------------------------------------------------------
    /**
     Calculates the change due according to the transaction type. This
     method is intended to be overwritten by transactions for which the
     change due is not the same as the balance due PLUS the forced cash
     change amount (negative cash change) due to depleted gift card tenders.
     Redeem Transactions override this method.<P>
     @return changeDue as CurrencyIfc
     **/
    //---------------------------------------------------------------------
    public CurrencyIfc calculateChangeDue()
    {
        CurrencyIfc changeDue = totals.getBalanceDue();

        // calculate sum of negative cash tenders
        // changeDue = changeDue.add(getNegativeCashTotal());

        return(changeDue);
    }

    //--------------------------------------------------------------------------
    /**
        Return the redeemable document for this transaction. <P>
        @return the redeemable document

    //--------------------------------------------------------------------------
    public RedeemableIfc getDocument()
    {
        return document;
    }
    **/
    //--------------------------------------------------------------------------
    /**
     Return the redeemable document for this transaction. <P>
     @return the redeemable document
     **/
    //--------------------------------------------------------------------------
    public TenderLineItemIfc getRedeemTender()
    {
        return redeemTender;
    }

    //---------------------------------------------------------------------
    /**
        Sets customer attribute. <P>
        @param value customer
    **/
    //---------------------------------------------------------------------
    public void linkCustomer(CustomerIfc value)
    {
        customer = value;
    }

    //---------------------------------------------------------------------
    /**
        Calculates FinancialTotals based on current transaction. <P>
        @return FinancialTotalsIfc object
    **/
    //---------------------------------------------------------------------
    public FinancialTotalsIfc getFinancialTotals()
    {
        // cancel must be handled differently
        if (transactionStatus == STATUS_CANCELED)
        {
            return processCanceledStatus();
        }
        else
        {
            return processRedeemTotals();
        }
    }

    /**
     * This method processes redeem transaction totals.
     * @return
     */
    protected FinancialTotalsIfc processRedeemTotals()
    {
        FinancialTotalsIfc financialTotals =
            (FinancialTotals) getTenderFinancialTotals(getTenderLineItems(), getTransactionTotals());
        //do the out tenders
        //count the redeem amount coming in as tender
        CurrencyIfc amount = redeemTender.getAmountTender();
        String countryCode = amount.getCountryCode();
        CurrencyIfc zeroAmount = DomainGateway.getCurrencyInstance(countryCode);
        TenderDescriptorIfc descriptor = DomainGateway.getFactory().getTenderDescriptorInstance();
        descriptor.setTenderType(redeemTender.getTypeCode());
        descriptor.setCountryCode(countryCode);
        descriptor.setCurrencyID(amount.getType().getCurrencyId());

        //TenderLineItemIfc redeemTender = getRedeemTender();
        if (redeemTender instanceof TenderStoreCreditIfc)
        {
            TenderStoreCreditIfc tenderStoreCredit = (TenderStoreCreditIfc) redeemTender;
            CurrencyIfc storeCreditRedeemAmount = tenderStoreCredit.getAmountTender();
            financialTotals.addAmountGrossStoreCreditsRedeemed(storeCreditRedeemAmount);
            financialTotals.addUnitsGrossStoreCreditsRedeemed(new BigDecimal(1.0));
        }
        else if (redeemTender instanceof TenderGiftCardIfc)
        {
            TenderGiftCardIfc tenderGiftCard = (TenderGiftCardIfc) redeemTender;
            CurrencyIfc giftCardRedeemAmount = tenderGiftCard.getAmountTender();
            financialTotals.addAmountGrossGiftCardItemRedeemed(giftCardRedeemAmount);
            financialTotals.addUnitsGrossGiftCardItemRedeemed(new BigDecimal(1.0));
        }
        else if (redeemTender instanceof TenderGiftCertificateIfc)
        {
            TenderGiftCertificateIfc tenderGiftCertificate = (TenderGiftCertificateIfc) redeemTender;
            CurrencyIfc giftCertificateRedeemAmount = tenderGiftCertificate.getAmountTender();
            financialTotals.addAmountGrossGiftCertificatesRedeemed(giftCertificateRedeemAmount);
            financialTotals.addUnitsGrossGiftCertificatesRedeemed(new BigDecimal(1.0));
            financialTotals.addAmountGrossNonTaxableTransactionSales(giftCertificateRedeemAmount.negate());
        }

        // Add the rounded (cash) change amount to the financial totals object.
        if (getTransactionTotals().getCashChangeRoundingAdjustment().signum() == CurrencyIfc.NEGATIVE)
        {
            financialTotals.addAmountChangeRoundedOut(getTransactionTotals().getCashChangeRoundingAdjustment().abs());
        }
        if (getTransactionTotals().getCashChangeRoundingAdjustment().signum() == CurrencyIfc.POSITIVE)
        {
            financialTotals.addAmountChangeRoundedIn(getTransactionTotals().getCashChangeRoundingAdjustment().abs());
        }

        FinancialTotalsIfc redeemTotals = DomainGateway.getFactory().getFinancialTotalsInstance();
        redeemTotals.getTenderCount().addTenderItem(descriptor,
                1,
                0,
                redeemTender.getAmountTender(),
                zeroAmount,
                redeemTender.getTypeDescriptorString(),
                "",
                false);

        financialTotals.add(redeemTotals);

        /**
        if ( reentryMode )
        {
            financialTotals.addNumberOfReentryTransactions(1);
            financialTotals.addAmountReentryTransactions(amount);
        }
        **/
        return financialTotals;
    }

    /**
     * This method processes cancelled redeem transactions.
     * @return
     */
    protected FinancialTotalsIfc processCanceledStatus()
    {
        FinancialTotalsIfc financialTotals = (FinancialTotals) DomainGateway.getFactory().getFinancialTotalsInstance();
        // for a cancelled transaction, just set up the cancelled
        // transaction item totals - this prevents the amounts of the
        // redeemed document from getting into the hard totals
        TransactionTotals transactionTotals = (TransactionTotals) getTransactionTotals();
        //CurrencyIfc amount = transactionTotals.getGrandTotal().abs();
        //financialTotals.addAmountGrossMinus(amount);
        //financialTotals.addAmountGrossPlus(amount);
        financialTotals.addNumberCancelledTransactions(1);
        financialTotals.addAmountCancelledTransactions(transactionTotals.getGrandTotal().negate());

        return financialTotals;
    }

    /**
     * Get the redeem ID.
     * @return redeemID
     */
    public String getRedeemID()
    {
        return redeemID;
    }

    /**
     * Set the redeem ID
     * @param value The redeem ID.
     */
    public void setRedeemID(String value)
    {
        redeemID = value;
    }

    /**
     * This is a utility method to get the redeem ID.
     * @return String redeemID
     * @param TenderLineItemIfc value The redeem tender.
     */
    public String retrieveRedeemID(TenderLineItemIfc value)
    {
        if ("".equals(redeemID) || redeemID == null)
        {
            if (value instanceof TenderStoreCreditIfc)
            {
                redeemID = ((TenderStoreCreditIfc) value).getStoreCreditID();
            }
            else if (value instanceof TenderGiftCardIfc)
            {
                redeemID = ((TenderGiftCardIfc) value).getCardNumber();
            }
            else
            {
                redeemID = ((TenderGiftCertificateIfc) value).getGiftCertificateNumber();
            }
        }

        return redeemID;
    }

    //---------------------------------------------------------------------
    /**
        Clones RedeemTransaction object <P>
        @return instance of RedeemTransaction object
    **/
    //---------------------------------------------------------------------
    public Object clone()
    {
        RedeemTransaction redeemTrans = new RedeemTransaction();
        setCloneAttributes(redeemTrans);
        return redeemTrans;
    }

    //---------------------------------------------------------------------
    /**
        Sets clone attributes.  This method is provided to facilitate
        extensibility. <P>
        @param newClass new instance of SaleReturnTransaction
    **/
    //---------------------------------------------------------------------
    public void setCloneAttributes(RedeemTransaction newClass)
    { // begin setCloneAttributes()

        // set attributes
        super.setCloneAttributes((AbstractTenderableTransaction) newClass);
        newClass.setRedeemTender((TenderLineItemIfc) redeemTender.clone());
        //newClass.setDocument((RedeemableIfc) document.clone());
        newClass.redeemID = redeemID;
        newClass.reentryMode = reentryMode;
    } // end setCloneAttributes()

    //---------------------------------------------------------------------
    /**
        Determine if two objects are identical. <P>
        @param obj object to compare with
        @return true if the objects are identical, false otherwise
    **/
    //---------------------------------------------------------------------
    public boolean equals(Object obj)
    {
        boolean equal = false;

        // If it's a RedeemTransaction, compare its attributes
        if (obj instanceof RedeemTransaction)
        {
            // downcast the input object
            RedeemTransaction c = (RedeemTransaction) obj;
            if (!super.equals(obj))
            {
                equal = false;
            }
            else if (!Util.isObjectEqual(getRedeemTender(), c.getRedeemTender()))
            {
                equal = false;
            }
            else
            {
                equal = true;
            }

            if (redeemID.equals(c.redeemID))
            {
                equal = true;
            }
            else
            {
                equal = false;
            }

            if (reentryMode == c.reentryMode)
            {
                equal = true;
            }
            else
            {
                equal = false;
            }
        }

        return equal;
    }

    //--------------------------------------------------------------------------
    /**
        Return a string representing this class. <P>
        @return a debugging string
    **/
    //--------------------------------------------------------------------------
    public String toString()
    {
        StringBuilder strResult =
            Util.classToStringHeader(Util.getSimpleClassName(getClass()), getRevisionNumber(), hashCode());
        strResult.append(super.toString()).append(Util.formatToStringEntry("RedeemTender ", getRedeemTender()));
        strResult.append(" Redeem ID " + redeemID);

        return strResult.toString();
    }

    //--------------------------------------------------------------------------
    /**
        Return the revision number assigned by the revision control system. <P>
        @return the revision number
    **/
    //--------------------------------------------------------------------------
    public String getRevisionNumber()
    {
        return Util.parseRevisionNumber(revisionNumber);
    }

    /**
    public int getNumberOfReentryTransactions()
    {
        return 1;
    }

    public CurrencyIfc getReentryAmount()
    {
        return DomainGateway.getBaseCurrencyInstance();
    }
    **/
    /**
     * @return Returns the reentryMode.
     */
    public boolean isReentryMode()
    {
        return reentryMode;
    }

    /**
     * @param reentryMode The reentryMode to set.
     */
    public void setReentryMode(boolean reentryMode)
    {
        this.reentryMode = reentryMode;
    }

    //I18N
    //---------------------------------------------------------------------
    /**
     * Retrieve Currency ID
     * @return currencyID
    **/
    //---------------------------------------------------------------------
    public int getCurrencyID()
    {
        return currencyID;
    }

    //---------------------------------------------------------------------
    /**
     * Set Currency ID
     * @param currencyID
    **/
    //---------------------------------------------------------------------
    public void setCurrencyID(int currencyID)
    {
        this.currencyID = currencyID;
    }

    /**
     * Gets gift card redeem flag
     * @return the giftCardRedeem
     */
    public boolean isGiftCardRedeem()
    {
        return giftCardRedeem;
    }

    /**
     * Sets gift card redeem flag
     * @param giftCardRedeem the giftCardRedeem to set
     */
    public void setGiftCardRedeem(boolean giftCardRedeem)
    {
        this.giftCardRedeem = giftCardRedeem;
    }

    /**
     * Gets gift certificate redeem flag
     * @return the giftCertificateRedeem
     */
    public boolean isGiftCertificateRedeem()
    {
        return giftCertificateRedeem;
    }

    /**
     * Sets gift certificate redeem flag
     * @param giftCertificateRedeem the giftCertificateRedeem to set
     */
    public void setGiftCertificateRedeem(boolean giftCertificateRedeem)
    {
        this.giftCertificateRedeem = giftCertificateRedeem;
    }

    /**
     * Gets store credit redeem flag
     * @return the storeCreditRedeem
     */
    public boolean isStoreCreditRedeem()
    {
        return storeCreditRedeem;
    }

    /**
     * Sets store credit redeem flag
     * @param storeCreditRedeem the storeCreditRedeem to set
     */
    public void setStoreCreditRedeem(boolean storeCreditRedeem)
    {
        this.storeCreditRedeem = storeCreditRedeem;
    }
}
