/* ===========================================================================
* Copyright (c) 2001, 2014, Oracle and/or its affiliates. All rights reserved.
 * ===========================================================================
 * $Header: rgbustores/modules/domain/src/oracle/retail/stores/domain/transaction/AbstractTenderableTransaction.java /main/38 2014/06/06 16:27:50 cgreene Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    cgreene   06/06/14 - set line number when adding to the line vector
 *    bhsuthar  08/23/13 - Made the getIssuedStoreCredit method to return array
 *                         instead of single object.
 *    jswan     02/13/13 - Modified for Currency Rounding.
 *    mkutiana  02/11/13 - Change Given after Rounding of Change due calculated
 *    tzgarba   08/27/12 - Merge project Echo (MPOS) into trunk.
 *    mjwallac  01/31/12 - XbranchMerge mjwallac_forward_port_bug_13603967 from
 *                         rgbustores_13.4x_generic_branch
 *    mjwallac  01/31/12 - incorporate code review comments.
 *    mjwallac  01/27/12 - Forward port: SQL Exception when trying to save a
 *                         resumed order transaction that had been linked to a
 *                         customer, but customer was deleted before resuming.
 *    cgreene   12/05/11 - updated from deprecated packages and used more
 *                         bigdecimal constants
 *    jswan     09/16/11 - Reworked credited giftcard totals issues.
 *    jswan     08/22/11 - Fixes issues with gift card totals.
 *    jswan     08/04/11 - Modified to count the use of gift cards in Layaway
 *                         Delete and Order Cancel Transactions in the gift
 *                         card credited bucket.
 *    blarsen   07/28/11 - MaximumCreditAmount parameter removed as part of
 *                         13.4 Advanced Payment Foundation.
 *    acadar    11/05/10 - when tendering with card, only add amount to the
 *                         gift card credited bucket if transaction is return
 *    jswan     08/31/10 - Code review changes.
 *    jswan     08/31/10 - Fixed problems with print tender change and totals
 *                         on the receipt.
 *    dwfung    07/16/10 - Use of StoreCredit as tender should not be considered
 *                         as Redeem (Redeem means Cash Redeem)
 *    cgreene   05/26/10 - convert to oracle packaging
 *    cgreene   04/28/10 - updating deprecated names
 *    abondala  01/03/10 - update header date
 *    asinton   05/06/09 - Mall Gift Certificate is stored in the till tender
 *                         history table as Mall Gift Certificate tender and
 *                         not Check or Purchase Order.
 *    mjadiyav  04/10/09 - the monetary value added to a GiftCard was being set
 *                         into GiftCardItemCredit only when the GiftCard
 *                         Request Type is Gift Card Credit Issue or Gift Card
 *                         Credit Reload, But this should happen for all the
 *                         giftcard request types. Hence the condition is
 *                         removed.
 *    cgreene   04/01/09 - implement method getReceiptTenderLineItems which
 *                         returns getCollectedTenderLineItems except for order
 *                         cancels which return all tenders
 *    cgreene   03/31/09 - added method isRefundDue() to to OrderTransactionIfc
 *                         and hasCollectedTnderLineItems to
 *                         TenderTransactionIfc for use in printing order
 *                         receipts
 *    mweis     03/17/09 - ensure 'Change Due' line does not appear for
 *                         canceled transactions
 *    cgreene   03/10/09 - prevent NPE during cannon testing if tender is null
 *    jswan     02/23/09 - Modifications to support Mail Bank Checks in summary
 *                         reports.
 *    jswan     02/02/09 - Merged during refresh.
 *    jswan     01/30/09 - Modifications to correctly print the change due on
 *                         Order Reciepts.
 *    jswan     01/29/09 - Results of merge
 *    jswan     01/29/09 - Modified to correct issues with printing store
 *                         credit.
 *    mdecama   01/27/09 - Accounting for the Store Credit Redeemed Tenders
 *    rkar      11/17/08 - View refresh to 081112.2142 label
 *    cgreene   11/06/08 - add isCollected to tenders for printing just
 *                         collected tenders
 *    rkar      11/04/08 - Added code for POS-RM integration
 *
 * ===========================================================================
 * $Log:
 * 9    360Commerce 1.8         4/12/2008 5:44:57 PM   Christian Greene Upgrade
 *       StringBuffer to StringBuilder
 * 8    360Commerce 1.7         7/9/2007 6:03:26 PM    Alan N. Sinton  CR 27494
 *       - Removed constaint TenderLimitsIfc.TENDER_NO_LIMIT_AMOUNT in favor
 *      of a lazy init value returned by static method
 *      TenderLimits.getTenderNoLimitAmount().
 * 7    360Commerce 1.6         5/22/2007 1:07:51 PM   Peter J. Fierro Receipt
 *      changes, save currency id in till tender history.
 * 6    360Commerce 1.5         4/25/2007 10:00:20 AM  Anda D. Cadar   I18N
 *      merge
 * 5    360Commerce 1.4         1/22/2006 11:41:56 AM  Ron W. Haight   Removed
 *      references to com.ibm.math.BigDecimal
 * 4    360Commerce 1.3         12/13/2005 4:43:51 PM  Barry A. Pape
 *      Base-lining of 7.1_LA
 * 3    360Commerce 1.2         3/31/2005 4:27:07 PM   Robert Pearse
 * 2    360Commerce 1.1         3/10/2005 10:19:28 AM  Robert Pearse
 * 1    360Commerce 1.0         2/11/2005 12:09:21 PM  Robert Pearse
 *
 *Revision 1.10.2.1  2004/11/17 16:21:30  jdeleau
 *@scr 7739 Correct the way receipts are printing change on a post-void.
 *
 *Revision 1.10  2004/10/06 18:44:06  bwf
 *@scr 7274 This makes sure to not count negative cash as change during a return.
 *
 *Revision 1.9  2004/09/23 00:30:51  kmcbride
 *@scr 7211: Inserting serialVersionUIDs in these Serializable classes
 *
 *Revision 1.8  2004/09/17 15:49:59  jdeleau
 *@scr 7146 Define a taxable transaction, for reporting purposes.
 *
 *Revision 1.7  2004/07/17 18:12:29  epd
 *@scr 4268 Updated financial totals for issued tender gift cards
 *
 *Revision 1.6  2004/06/11 23:25:40  cdb
 *@scr 5559 Updated to record store credit issues (during returns) and voids to financial totals.
 *
 *Revision 1.5  2004/05/11 15:15:37  jdeleau
 *@scr 4888 Hard Totals for 7.0, dont treat money orders as checks here
 *or they are checks in financial totals and hard totals.  Do it at the end point
 *instead, like in the Register Report.
 *
 *Revision 1.4  2004/04/27 20:50:10  epd
 *@scr 4513 Fixes for printing and journalling when forced cash change is present
 *
 *Revision 1.3  2004/02/17 16:18:52  rhafernik
 *@scr 0 log4j conversion
 *
 *Revision 1.2  2004/02/12 17:14:42  mcs
 *Forcing head revision
 *
 *Revision 1.1.1.1  2004/02/11 01:04:34  cschellenger
 *updating to pvcs 360store-current
 *
 *
 *
 *    Rev 1.11   Feb 10 2004 14:34:48   bwf
 * Refactor Echeck.
 *
 *    Rev 1.10   Jan 29 2004 14:55:10   lzhao
 * add all tender line items for gift card reload reverse tender.
 *
 *    Rev 1.9   Jan 28 2004 07:27:42   blj
 * fixed a problem with money order tender totals not being added to check totals.
 *
 *    Rev 1.8   Dec 11 2003 15:30:00   bwf
 * update for code review.
 * Resolution for 3429: Check/ECheck Tender
 *
 *    Rev 1.7   Dec 11 2003 11:47:44   bwf
 * Add mall gift certificate report functionality.
 * Resolution for 3538: Mall Certificate Tender
 *
 *    Rev 1.6   Dec 11 2003 09:21:36   Tim Fritz
 * Change tender type to E-Check, if the check has been approved as an E-Check
 * Resolution for 3557: Deposited Checks and e-Checks are not reported separately in Reports
 *
 *    Rev 1.5   Dec 03 2003 11:07:56   bwf
 * Updated cloneattributes.
 *
 *    Rev 1.4   Dec 01 2003 13:46:22   bwf
 * Updated for echeck declines.
 *
 *    Rev 1.3   Nov 17 2003 14:49:10   blj
 * refactored and added updateTenderTotals method
 *
 *    Rev 1.2   Oct 30 2003 20:31:22   epd
 * added new method to remove tenders from transaction
 *
 *    Rev 1.1   Oct 02 2003 10:45:40   baa
 * fix for rss defect 1406
 * Resolution for 3402: RSS  PRF 1406 Tender Information on Void transactions does not looks correct
 *
 *    Rev 1.0   Aug 29 2003 15:40:46   CSchellenger
 * Initial revision.
 *
 *    Rev 1.7   Jul 24 2003 10:01:34   DCobb
 * Use CountryCodeMap to get country descriptor.
 * Resolution for POS SCR-2484: Canadian Check Till Pickup
 *
 *    Rev 1.6   Jul 21 2003 18:22:44   sfl
 * To avoid incorrect description value "CAD_Canadian Cash" to be generated.
 * Resolution for POS SCR-3168: Server Offline- Unable to Pickup Canadian Cash.
 *
 *    Rev 1.5   Jun 26 2003 16:09:08   DCobb
 * Modifications for Canadian Store front.
 * Resolution for POS SCR-2484: Canadian Check Till Pickup
 *
 *    Rev 1.4   Jun 23 2003 13:33:24   DCobb
 * Canadian Check Till Pickup
 * Resolution for POS SCR-2484: Canadian Check Till Pickup
 *
 *    Rev 1.3   Sep 17 2002 16:53:30   pjf
 * Add convenience methods for testing tender type.
 *
 *    Rev 1.2   Sep 03 2002 15:43:40   baa
 * Externalize domain constants
 * Resolution for POS SCR-1740: Code base Conversions
 *
 *    Rev 1.1   24 Jun 2002 11:48:40   jbp
 * merge from 5.1 SCR 1726
 * Resolution for POS SCR-1726: Void - Void of new special order gets stuck in the queue in DB2
 *
 *    Rev 1.0   Jun 03 2002 17:05:42   msg
 * Initial revision.
 *
 *    Rev 1.1   Mar 18 2002 23:11:24   msg
 * - updated copyright
 *
 *    Rev 1.0   Mar 18 2002 12:30:10   msg
 * Initial revision.
 *
 *    Rev 1.7   01 Mar 2002 15:17:36   pdd
 * Converted to use TenderTypeMapIfc for tender codes and descriptors.
 * Resolution for POS SCR-627: Make the Tender type list extendible.
 *
 *    Rev 1.6   04 Jan 2002 09:08:46   pdd
 * Modified getFinancialTotalsFromTender() to use TenderDescriptorIfc and distinguish credit cards with the subtype.
 * Resolution for POS SCR-370: 5.0 Summary report tender summary updates
 *
 *    Rev 1.5   20 Dec 2001 22:10:22   pdd
 * Updated to getFinancialTotalsFromTender() to use appropriate currency.
 * Resolution for POS SCR-370: 5.0 Summary report tender summary updates
 *
 *    Rev 1.4   30 Nov 2001 15:56:28   jbp
 * exceed methods were moved into AbstractTenderableTransaction from SaleReturnTransaction.  They were removed from PaymentTransaction.  VoidTransaction overrides addTenderLineItems so that it does not check TenderLimits.
 * Resolution for POS SCR-348: Cannot overtender with StoreCredit on a layaway payment
 *
 *    Rev 1.3   09 Nov 2001 15:17:14   pdd
 * Cleanup.
 * Resolution for POS SCR-219: Add Tender Limit Override
 *
 *    Rev 1.2   01 Nov 2001 09:57:54   pdd
 * Added the tender limits to the tender line item in addTender().
 * This is still needed for authorization.
 * Resolution for POS SCR-219: Add Tender Limit Override
 *
 *    Rev 1.1   29 Oct 2001 18:19:30   pdd
 * Added addTender() to add a tender without checking the limits.
 * Resolution for POS SCR-219: Add Tender Limit Override
 *
 *    Rev 1.0   Sep 20 2001 16:06:16   msg
 * Initial revision.
 *
 *    Rev 1.1   Sep 17 2001 12:40:12   msg
 * header update
 * ===========================================================================
 */
package oracle.retail.stores.domain.transaction;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;

import oracle.retail.stores.commerceservices.common.currency.CurrencyIfc;
import oracle.retail.stores.common.utility.BigDecimalConstants;
import oracle.retail.stores.domain.DomainGateway;
import oracle.retail.stores.domain.customer.CustomerIfc;
import oracle.retail.stores.domain.customer.IRSCustomerIfc;
import oracle.retail.stores.domain.financial.FinancialTotalsIfc;
import oracle.retail.stores.domain.tax.TaxConstantsIfc;
import oracle.retail.stores.domain.tax.TaxInformationIfc;
import oracle.retail.stores.domain.tender.TenderAlternateCurrencyIfc;
import oracle.retail.stores.domain.tender.TenderCashIfc;
import oracle.retail.stores.domain.tender.TenderChargeIfc;
import oracle.retail.stores.domain.tender.TenderDescriptorIfc;
import oracle.retail.stores.domain.tender.TenderGiftCardIfc;
import oracle.retail.stores.domain.tender.TenderGiftCertificateIfc;
import oracle.retail.stores.domain.tender.TenderLimits;
import oracle.retail.stores.domain.tender.TenderLineItemIfc;
import oracle.retail.stores.domain.tender.TenderStoreCreditIfc;
import oracle.retail.stores.domain.tender.TenderTravelersCheckIfc;
import oracle.retail.stores.domain.tender.TenderTypeMapIfc;
import oracle.retail.stores.domain.utility.CountryCodeMap;
import oracle.retail.stores.common.utility.Util;

/**
 * This class contains the behavior associated with a tenderable transaction,
 * i.e., a transaction that involves the tendering of money.
 *
 * @see oracle.retail.stores.domain.transaction.TenderableTransactionIfc
 */
public abstract class AbstractTenderableTransaction extends Transaction
    implements TenderableTransactionIfc, Cloneable, Serializable
{
    // This id is used to tell the compiler not to generate a new serialVersionUID.
    static final long serialVersionUID = 7452948367237270458L;

    /**
     * tender line items
     */
    protected Vector<TenderLineItemIfc> tenderLineItemsVector;

    /**
     * transaction totals
     */
    protected TransactionTotalsIfc totals;

    /**
     * customer assignation
     */
    protected CustomerIfc customer;

    /**
     * irs customer assignation
     */
    protected IRSCustomerIfc irsCustomer;

    /**
     * uniqueId -- used to store ID for layaway, special order, and store
     * credits.
     */
    protected String uniqueID;

    /**
     * This vector contains all echeck declined that need to be franked.
     */
    protected Vector<TenderLineItemIfc> ECheckDeclinedItems;

    /**
     * return ticket id received from Returns Management server
     */
    protected String returnTicket = null;

    protected String customerId = null;

    /**
    **/
    protected void initialize()
    {
        tenderLineItemsVector = new Vector<TenderLineItemIfc>();
        totals = DomainGateway.getFactory().getTransactionTotalsInstance();
    }

    /**
     * Sets clone attributes. This method is provided to facilitate
     * extensibility.
     *
     * @param newClass new instance of AbstractTenderableTransaction
     */
    protected void setCloneAttributes(AbstractTenderableTransaction newClass)
    {
        // set attributes
        super.setCloneAttributes(newClass);

        newClass.setTransactionTotals((TransactionTotalsIfc)(totals.clone()));
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
        if (customer != null)
        {
            newClass.setCustomer((CustomerIfc)customer.clone());
        }
        if (irsCustomer != null)
        {
            newClass.setIRSCustomer((IRSCustomerIfc)irsCustomer.clone());
        }
        if (uniqueID != null)
        {
            newClass.setUniqueID(this.getUniqueID());
        }

        if (this.getECheckDeclinedItems() != null)
        {
            int declinedEchecks = this.getECheckDeclinedItems().size();
            if (declinedEchecks > 0)
            {
                for (int i = 0; i < declinedEchecks; i++)
                {
                    newClass.addECheckDeclinedItems(getECheckDeclinedItems().get(i));
                }
            }
        }
    }

    /**
     * Determines whether this transaction has any tender line items that match
     * the given tender type code. Type codes are defined in TenderLineItemIfc.
     *
     * @param tenderType int tender type code to use in the test.
     * @return boolean if the line item collection includes one or more tender
     *         line items of the given type.
     */
    public boolean containsTenderLineItems(int tenderType)
    {
        boolean value = false;

        for (Iterator<TenderLineItemIfc> i = tenderLineItemsVector.iterator(); i.hasNext();)
        {
            if (tenderType == i.next().getTypeCode())
            {
                value = true;
                break;
            }
        }
        return value;
    }

    /**
     * Returns an array containing the line items of a given tender type which
     * are contained by this transaction. If no items of the given type are
     * found, the returned array will have a length of 0.
     *
     * @param tenderType int indicating the tender line item type to iterate
     *            over.
     * @return TenderLineItemIfc[] containing tender line items of the given
     *         type.
     */
    public TenderLineItemIfc[] getTenderLineItemArray(int tenderType)
    {
        Collection<TenderLineItemIfc> c = getTenderLineItems(tenderType);
        TenderLineItemIfc[] array = new TenderLineItemIfc[c.size()];
        c.toArray(array);
        return array;
    }

    /**
     * Returns an iterator over the line items of a given tender type which are
     * contained by this transaction. If no items of the given type are found,
     * Iterator.hasNext() will return false.
     *
     * @param tenderType int indicating the tender line item type to iterate
     *            over.
     * @return Iterator over tender line items of the given type.
     */
    public Iterator<TenderLineItemIfc> getTenderLineItemIterator(int tenderType)
    {
        return getTenderLineItems(tenderType).iterator();
    }

    /**
     * Returns a collection of tender line items of a given tender type which
     * are contained by this transaction. If no items of the given type are
     * found, Collection.isEmpty() will return true.
     *
     * @param tenderType int indicating the tender line item type to return.
     * @return Collection containing tender line items of the given type.
     */
    public Collection<TenderLineItemIfc> getTenderLineItems(int tenderType)
    {
        Collection<TenderLineItemIfc> items = new ArrayList<TenderLineItemIfc>();
        TenderLineItemIfc item = null;

        for (Iterator<TenderLineItemIfc> i = tenderLineItemsVector.iterator(); i.hasNext();)
        {
            item = i.next();
            if (tenderType == item.getTypeCode())
            {
                items.add(item);
            }
        }
        return items;
    }

    /**
     * Removes all tender line items of the given tender type from this
     * transaction. If no items of the given type are found, no action is taken.
     *
     * @param tenderType int type code indicating the tender line item type to
     *            remove.
     */
    public void removeTenderLineItems(int tenderType)
    {
        for (Iterator<TenderLineItemIfc> i = tenderLineItemsVector.iterator(); i.hasNext();)
        {
            if (tenderType == i.next().getTypeCode())
            {
                i.remove();
            }
        }
        updateTenderTotals();
    }

    /**
     * Removes all tender line items
     */
    public void removeTenderLineItems()
    {
        tenderLineItemsVector.removeAllElements();
        updateTenderTotals();
    }

    /**
     * Update totals in the Transaction totals object.
     */
    public void updateTenderTotals()
    {
        totals.updateTenderTotals(getTenderLineItems());
    }

    /**
     * Calculates FinancialTotals based on current transaction.
     *
     * @return FinancialTotalsIfc object
     */
    public abstract FinancialTotalsIfc getFinancialTotals();

    /**
     * Derive additive financial totals from tender line items.
     *
     * @param tenderLineItems array of tender line items
     * @param transTotals transaction totals
     * @return additive financial totals
     */
    protected FinancialTotalsIfc getTenderFinancialTotals(TenderLineItemIfc[] tenderLineItems,
            TransactionTotalsIfc transTotals)
    {
        FinancialTotalsIfc financialTotals = DomainGateway.getFactory().getFinancialTotalsInstance();
        // get size of array
        int numTenderLineItems = 0;

        if (tenderLineItems != null)
        {
            numTenderLineItems = tenderLineItems.length;
        }

        // if elements exist, loop through them
        for (int i = 0; i < numTenderLineItems; i++)
        {
            if (tenderLineItems[i] != null)
            {
                financialTotals = financialTotals.add(getFinancialTotalsFromTender(tenderLineItems[i]));
            }
        }

        return (financialTotals);
    }

    /**
     * Derive the additive financial totals from a given tender line item.
     *
     * @param tli TenderLineItemIfc entry
     * @return FinancialTotalsIfc
     */
    public FinancialTotalsIfc getFinancialTotalsFromTender(TenderLineItemIfc tli)
    {
        CurrencyIfc amount = tli.getAmountTender();
        TenderDescriptorIfc descriptor = DomainGateway.getFactory().getTenderDescriptorInstance();
        String desc = tli.getTypeDescriptorString();

        // Get the appropriate amount based on the currency used.
        if (tli instanceof TenderAlternateCurrencyIfc)
        {
            TenderAlternateCurrencyIfc alternate = (TenderAlternateCurrencyIfc)tli;
            CurrencyIfc alternateAmount = alternate.getAlternateCurrencyTendered();

            // if tendered in alternate currency, use that amount.
            if (alternateAmount != null)
            {
                amount = alternate.getAlternateCurrencyTendered();
                // Prepend nationality to the description.
                String countryCode = amount.getCountryCode();
                String countryDescriptor = CountryCodeMap.getCountryDescriptor(countryCode);
                if (countryDescriptor == null)
                {
                    countryDescriptor = countryCode;
                }
                desc = countryDescriptor + " " + desc;
            }
        }

        String currencyCode = amount.getCountryCode();
        descriptor.setCountryCode(currencyCode);
        descriptor.setCurrencyID(amount.getType().getCurrencyId());
        int tenderType = tli.getTypeCode();

        descriptor.setTenderType(tenderType);
        int numberItems = 1;

        // Traveler's checks have multiple checks for one line item.
        if (tenderType == TenderLineItemIfc.TENDER_TYPE_TRAVELERS_CHECK)
        {
            numberItems = ((TenderTravelersCheckIfc)tli).getNumberChecks();
        }

        String sDesc = null;

        // Add individual credit card totals to the financial totals
        if (tenderType == TenderLineItemIfc.TENDER_TYPE_CHARGE)
        {
            // Assuming that Credit cards are always handled in the local
            // currency,
            // so there is no conflict between this description and the
            // alternate above.
            desc = ((TenderChargeIfc)tli).getCardType();
            sDesc = tli.getTypeDescriptorString();
            descriptor.setTenderSubType(desc);
        }

        if (tenderType == TenderLineItemIfc.TENDER_TYPE_MALL_GIFT_CERTIFICATE)
        {
            String mallCertType = ((TenderGiftCertificateIfc)tli).getCertificateType();
            if (mallCertType != null && !mallCertType.equals(""))
            {
                TenderTypeMapIfc tenderTypeMap = DomainGateway.getFactory().getTenderTypeMapInstance();
                descriptor.setTenderType(tenderType);
                desc = tenderTypeMap.getDescriptor(tenderType);
                sDesc = tenderTypeMap.getDescriptor(tenderType);
            }

        }

        CurrencyIfc amtIn = null;
        CurrencyIfc amtOut = null;
        int cntIn = 0;
        int cntOut = 0;

        // Set amounts, count in/count out
        if (tli.getAmountTender().signum() == CurrencyIfc.POSITIVE)
        {
            amtIn = amount;
            amtOut = DomainGateway.getCurrencyInstance(currencyCode);
            cntIn = numberItems;
        }
        else
        {
            amtIn = DomainGateway.getCurrencyInstance(currencyCode);
            amtOut = amount.negate();
            cntOut = numberItems;
        }

        FinancialTotalsIfc financialTotals = DomainGateway.getFactory().getFinancialTotalsInstance();
        financialTotals.getTenderCount().addTenderItem(descriptor, cntIn, cntOut, amtIn, amtOut, desc, sDesc,
                tli.getHasDenominations());

        if (tli instanceof TenderStoreCreditIfc)
        {
        	//Add Store Credit Issued ONLY.
        	if (tli.getAmountTender().signum() == CurrencyIfc.NEGATIVE)
        	{
                financialTotals.addAmountGrossStoreCreditsIssued(amtOut);
                financialTotals.addUnitsGrossStoreCreditsIssued(BigDecimalConstants.ONE_AMOUNT);
        	}
        	//Else it is an use of StoreCredit as tender; should not considered as Redeem (Cash Redeem).
        }
        else if (tli instanceof TenderGiftCardIfc)
        {
            //if transaction is a refund, add the gift card amount to the item credit bucket
            if(getTransactionType() == TransactionConstantsIfc.TYPE_RETURN ||
                    getTransactionType() == TransactionConstantsIfc.TYPE_LAYAWAY_DELETE ||
                    getTransactionType() == TransactionConstantsIfc.TYPE_ORDER_CANCEL)
            {
                financialTotals.addAmountGrossGiftCardItemCredit(tli.getAmountTender().abs());
                financialTotals.addUnitsGrossGiftCardItemCredit(BigDecimalConstants.ONE_AMOUNT);
            }
            
            // If the transaction is a sale and the tender amount is less than 0
            if (getTransactionType() == TransactionConstantsIfc.TYPE_SALE &&
                    tli.getAmountTender().signum() < 0)
            {
                financialTotals.addAmountGrossGiftCardItemCredit(tli.getAmountTender().abs());
                financialTotals.addUnitsGrossGiftCardItemCredit(BigDecimalConstants.ONE_AMOUNT);
            }
        }

        return (financialTotals);

    }

    /* (non-Javadoc)
     * @see oracle.retail.stores.domain.transaction.TenderableTransactionIfc#getCollectedTenderLineItems()
     */
    public TenderLineItemIfc[] getCollectedTenderLineItems()
    {
        List<TenderLineItemIfc> tenders = new ArrayList<TenderLineItemIfc>(tenderLineItemsVector.size());
        for (TenderLineItemIfc tender : tenderLineItemsVector)
        {
            if (tender.isCollected())
            {
                tenders.add(tender);
            }
        }
        return tenders.toArray(new TenderLineItemIfc[tenders.size()]);
    }

    /* (non-Javadoc)
     * @see oracle.retail.stores.domain.transaction.TenderableTransactionIfc#getIssuedStoreCredit()
     */
    public TenderStoreCreditIfc[] getIssuedStoreCredit()
    {
        List<TenderStoreCreditIfc> tenderStoreCredit = new ArrayList<TenderStoreCreditIfc>();
        for (TenderLineItemIfc item : tenderLineItemsVector)
        {
            if (item instanceof TenderStoreCreditIfc &&
                    ((TenderStoreCreditIfc)item).isIssued())
            {
                tenderStoreCredit.add((TenderStoreCreditIfc)item);
            }
        }
        return tenderStoreCredit.toArray(new TenderStoreCreditIfc[tenderStoreCredit.size()]);
    }

    /* (non-Javadoc)
     * @see oracle.retail.stores.domain.transaction.TenderableTransactionIfc#getCollectedTenderTotalAmount()
     */
    public CurrencyIfc getCollectedTenderTotalAmount()
    {
        CurrencyIfc collectedTotal = DomainGateway.getBaseCurrencyInstance();
        TenderLineItemIfc[] tli = getCollectedTenderLineItems();
        for (int i = 0; i < tli.length; i++)
        {
            collectedTotal = collectedTotal.add(tli[i].getAmountTender());
        }
        return collectedTotal;
    }

    /* (non-Javadoc)
     * @see oracle.retail.stores.domain.transaction.TenderableTransactionIfc#getTenderTotalAmountPlusChangeDue()
     */
    public CurrencyIfc getTenderTotalAmountPlusChangeDue()
    {
        CurrencyIfc total = DomainGateway.getBaseCurrencyInstance();
        TenderLineItemIfc[] tli = getTenderLineItems();
        for (int i = 0; i < tli.length; i++)
        {
            total = total.add(tli[i].getAmountTender());
        }
        // Since the change due is negative, subtracting this amount will increase
        // the total by this amount.
        return total.subtract(calculateChangeDue()).subtract(getTenderTransactionTotals().getCashChangeRoundingAdjustment());
    }

    /* (non-Javadoc)
     * @see oracle.retail.stores.domain.transaction.TenderableTransactionIfc#hasCollectedTenderLineItems()
     */
    public boolean hasCollectedTenderLineItems()
    {
        return (getCollectedTenderLineItems().length > 0);
    }

    /**
     * Retrieves tender line items array in vector form. Don't modify this
     * vector!
     *
     * @return vector of tender line items for this transaction
     */
    public Vector<TenderLineItemIfc> getTenderLineItemsVector()
    {
        return tenderLineItemsVector;
    }

    /**
     * Retrieves tender line items array.
     *
     * @return tender line items for this transaction
     */
    public TenderLineItemIfc[] getTenderLineItems()
    {
        TenderLineItemIfc[] items = new TenderLineItemIfc[tenderLineItemsVector.size()];
        tenderLineItemsVector.toArray(items);
        return items;
    }

    /**
     * Set tender line items array and update totals.
     *
     * @param tli array tender line items
     */
    public void setTenderLineItems(TenderLineItemIfc[] tli)
    {
        tenderLineItemsVector.clear();
        if (tli != null)
        {
            for (TenderLineItemIfc tender : tli)
            {
                tenderLineItemsVector.add(tender);
                tender.setLineNumber(tenderLineItemsVector.size() - 1);
            }
        }
        updateTenderTotals();
    }

    /**
     * Adds tender line item if it is within Tender Limits.
     *
     * @param item oracle.retail.stores.domain.tender.TenderLineItemIfc The
     *            tender line item to be added
     * @exception IllegalArgumentException if tender line item cannot be added
     */
    public void addTenderLineItem(TenderLineItemIfc item) throws IllegalArgumentException
    {
        // if no tender limits on this object, add them
        if (item.getTenderLimits() == null)
        {
            item.setTenderLimits(getTenderLimits());
        }

        if (totals.getGrandTotal().signum() == CurrencyIfc.POSITIVE)
        {
            // test maximum change for the transaction
            if (exceedsMaxChangeLimit(item))
            {
                throw new IllegalArgumentException("New tender item amount [" + item.getAmountTender()
                        + "] would exceed cash back limit for " + item.getTypeDescriptorString() + " tender.");

            }

            // test maximum amount for the transaction
            if (exceedsMaxAmountLimit(item))
            {
                throw new IllegalArgumentException("New tender item amount [" + item.getAmountTender()
                        + "] would exceed maximum allowable amount for " + item.getTypeDescriptorString() + " tender.");
            }
        }
        // add element to vector
        addTender(item);
    }

    /**
     * Tests tender line item to see if it can be added. This tests to see if
     * maximum change value has been overrun.
     *
     * @param item TenderLineItemIfc item to be added
     * @return boolean true if limit exceeded.
     */
    public boolean exceedsMaxChangeLimit(TenderLineItemIfc item)
    {
        boolean value = false;
        boolean exempt = false;
        // get change amount parameter for each tender line item
        CurrencyIfc maximumChange = DomainGateway.getBaseCurrencyInstance();
        CurrencyIfc noLimit = (CurrencyIfc)TenderLimits.getTenderNoLimitAmount().clone();
        CurrencyIfc itemMaximumChange = null;
        TenderLineItemIfc testItem = null;
        int numberLines = tenderLineItemsVector.size();
        // loop through number of lines and then additional line
        for (int i = 0; i < (numberLines + 1); i++)
        {
            // if still in vector, use item from vector
            if (i < numberLines)
            {
                testItem = tenderLineItemsVector.get(i);
            }
            // if done with vector, use additional item
            else
            {
                testItem = item;
            }

            // find out if exempt
            if (testItem.IsExemptFromMaxCashLimit())
            {
                exempt = true;
                break; // no point in continuing for loop
            }

            // get maximum change property for tender line item
            itemMaximumChange = testItem.getAmountMaximumChange();

            // if no limit set, then it's zero, so we do nothing
            if (itemMaximumChange != null && !itemMaximumChange.equals(noLimit))
            // if limit exists, accumulate it
            {
                maximumChange = maximumChange.add(itemMaximumChange);
            }
        }

        // check against overpayment
        CurrencyIfc amountTendered = totals.getAmountTender().add(item.getAmountTender());
        CurrencyIfc amountOverpay = amountTendered.subtract(totals.getGrandTotal());

        // compare overpay to maximum change
        if (amountOverpay.compareTo(maximumChange) == CurrencyIfc.GREATER_THAN && !exempt)
        {
            value = true;
        }
        return value;
    }

    /**
     * Tests a tender line item to see if it can be added. This tests to see if
     * adding the item to the transaction will exceed the maximum amount for the
     * item's tender type.
     *
     * @param item TenderLineItemIfc item to be added
     * @return boolean true if limit exceeded.
     */
    public boolean exceedsMaxAmountLimit(TenderLineItemIfc item)
    {
        boolean value = false;
        // get maximum amount parameter for each tender line item
        CurrencyIfc currentAmount = DomainGateway.getBaseCurrencyInstance();
        CurrencyIfc maximumAmount = null;
        TenderLineItemIfc temp = null;

        switch (item.getTypeCode())
        {
            case TenderLineItemIfc.TENDER_TYPE_CASH:
                maximumAmount = getTenderLimits().getCurrencyLimit("MaximumCashAccepted");
                break;
            case TenderLineItemIfc.TENDER_TYPE_CHECK:
                maximumAmount = getTenderLimits().getCurrencyLimit("MaximumCheckAmount");
                break;
            case TenderLineItemIfc.TENDER_TYPE_TRAVELERS_CHECK:
                maximumAmount = getTenderLimits().getCurrencyLimit("MaximumTravelersCheckAmount");
                break;
            default:
                return value;
        }

        // loop through current tender items and sum the amounts for the target
        // item's tender type
        for (Enumeration<TenderLineItemIfc> e = tenderLineItemsVector.elements(); e.hasMoreElements();)
        {
            temp = e.nextElement();
            if (temp.getTypeCode() == item.getTypeCode())
            {
                currentAmount = currentAmount.add(temp.getAmountTender());
            }
        }

        // add item amount to the current total for the type
        currentAmount = currentAmount.add(item.getAmountTender());

        // compare to maximum amount
        if (currentAmount.compareTo(maximumAmount) == CurrencyIfc.GREATER_THAN)
        {
            value = true;
        }
        return value;
    }

    /**
     * Tests tender line item to see if it can be added. This tests to see if
     * maximum refund value has been overrun.
     *
     * @param item TenderLineItemIfc item to be added
     * @return boolean true if limit exceeded.
     */
    public boolean exceedsMaxCashRefundLimit(TenderLineItemIfc item)
    {
        boolean value = false;

        // Don't bother if it's not a return or not cash
        if (transactionType == TYPE_RETURN && item.getTypeCode() == TenderLineItemIfc.TENDER_TYPE_CASH)
        {
            CurrencyIfc maximumRefund = ((TenderCashIfc)item).getAmountMaximumRefund();
            CurrencyIfc totalRefundAmount = item.getAmountTender();
            TenderLineItemIfc testItem = null;
            int numberLines = tenderLineItemsVector.size();

            // accumulate the cash refund amounts
            for (int i = 0; i < numberLines; i++)
            {
                testItem = tenderLineItemsVector.get(i);

                // if it's cash add it to the cash refund total
                if (testItem.getTypeCode() == TenderLineItemIfc.TENDER_TYPE_CASH)
                {
                    totalRefundAmount = totalRefundAmount.add(testItem.getAmountTender());
                }
            }

            // Is the total over the limit?
            if (totalRefundAmount.compareTo(maximumRefund.negate()) == CurrencyIfc.LESS_THAN)
            {
                value = true;
            }
        }
        return value;
    }

    /**
     * Adds tender line item.
     *
     * @param tender The tender line item to be added
     */
    public void addTender(TenderLineItemIfc tender)
    {
        // If no tender limits on this object, add them
        if (tender.getTenderLimits() == null)
        {
            tender.setTenderLimits(getTenderLimits());
        }
        // add element to vector
        tenderLineItemsVector.addElement(tender);
        tender.setLineNumber(tenderLineItemsVector.size() - 1);
        // update totals
        updateTenderTotals();
    }

    /**
     * Remove a tender line from the transaction.
     *
     * @param index Index of the item to remove
     */
    public void removeTenderLineItem(int index)
    {
        tenderLineItemsVector.removeElementAt(index);
        updateTenderTotals();
    }

    /**
     * Remove a tender line from the transaction.
     *
     * @param tenderToRemove Tender line item to remove from the list
     */
    public void removeTenderLineItem(TenderLineItemIfc tenderToRemove)
    {
        // first try to remove the tender based on object equality
        if (tenderLineItemsVector.contains(tenderToRemove))
        {
            tenderLineItemsVector.remove(tenderToRemove);
        }
        // next try based on line number
        else
        {
            tenderLineItemsVector.removeElementAt(tenderToRemove.getLineNumber());
        }
        updateTenderTotals();
    }

    /**
     * Retrieves customer.
     *
     * @return customer
     */
    public CustomerIfc getCustomer()
    {
        return (customer);
    }

    /**
     * Sets customer attribute. Note that this does not perform any other
     * customer-related operations on the transaction, such as establishing
     * discount rules for customer-based discounts.
     *
     * @param value customer
     */
    public void setCustomer(CustomerIfc value)
    {
        customer = value;
    }

    /**
     * Retrieves irs customer.
     *
     * @return IRSCustomerIfc irs customer reference
     */
    public IRSCustomerIfc getIRSCustomer()
    {
        return (irsCustomer);
    }

    /**
     * Sets irs customer
     *
     * @param value irs customer
     */
    public void setIRSCustomer(IRSCustomerIfc value)
    {
        irsCustomer = value;
    }

    /**
     * Gets uniqueID.
     *
     * @return uniqueID
     */
    public String getUniqueID()
    {
        return this.uniqueID;
    }

    /**
     * Sets the UniqueID
     *
     * @param uniqueID value to set
     */
    public void setUniqueID(String uniqueID)
    {
        this.uniqueID = uniqueID;
    }

    /**
     * Get return ticket ID, which was created by RM.
     *
     * @see oracle.retail.stores.domain.transaction.TenderableTransactionIfc#getReturnTicket()
     */
    public String getReturnTicket() {
        return returnTicket;
    }

    /**
     * Set return ticket ID, which was created by RM.
     *
     * @see oracle.retail.stores.domain.transaction.TenderableTransactionIfc#setReturnTicket(java.lang.String)
     */
    public void setReturnTicket(String returnTicket) {
        this.returnTicket = returnTicket;
    }

    /**
     * Calculates the change due according to the transaction type. This method
     * is intended to be overwritten by transactions for which the change due is
     * not the same as the balance due PLUS the forced cash change amount
     * (negative cash change) due to depleted gift card tenders.
     *
     * @return changeDue as CurrencyIfc
     */
    public CurrencyIfc calculateChangeDue()
    {
        CurrencyIfc changeDue = totals.getBalanceDue();

        // if we are in a return then the negative cash is not change it is a
        // refund
        if (!(getTransactionType() == TransactionConstantsIfc.TYPE_RETURN
                || getTransactionType() == TransactionConstantsIfc.TYPE_ORDER_CANCEL
                || getTransactionType() == TransactionConstantsIfc.TYPE_REDEEM || getTransactionType() == TransactionConstantsIfc.TYPE_LAYAWAY_DELETE))
        {
            // calculate sum of negative cash tenders
            changeDue = changeDue.add(getNegativeCashTotal());
        }

        // HPQC-2894 / BugDB-8331862 : 'Change Due' line shows on a canceled transaction
        if (this.getTransactionStatus() == TransactionConstantsIfc.STATUS_CANCELED)
        {
        	changeDue.setZero();	// force the change due back to zero, since no tender was taken
        }

        return (changeDue);
    }
    
    /**
     * Calculates the change given on a transaction considering the Rounded Change Amount
     *
     * @return changeGiven as CurrencyIfc
     */
    public CurrencyIfc calculateChangeGiven()
    {
        CurrencyIfc adjustment = getTenderTransactionTotals().getCashChangeRoundingAdjustment();       
        return calculateChangeDue().add(adjustment);
    }

    /**
     * Calculates sum of all negative cash tenders
     *
     * @return
     */
    public CurrencyIfc getNegativeCashTotal()
    {
        TenderLineItemIfc[] cashTenders = getTenderLineItemArray(TenderLineItemIfc.TENDER_TYPE_CASH);
        CurrencyIfc result = DomainGateway.getBaseCurrencyInstance();
        for (int i = 0; cashTenders != null && i < cashTenders.length; i++)
        {
            if (cashTenders[i].getAmountTender().signum() == CurrencyIfc.NEGATIVE)
            {
                result = result.add(cashTenders[i].getAmountTender());
            }
        }
        return result;
    }

    /**
     * Retrieves transaction totals object.
     *
     * @return transaction totals object
     */
    public TransactionTotalsIfc getTransactionTotals()
    {
        return (totals);
    }

    /**
     * Sets transaction totals object.
     *
     * @param value transaction totals object to set
     */
    public void setTransactionTotals(TransactionTotalsIfc value)
    {
        totals = value;
    }

    /**
     * Retrieves tender display transaction totals. In this case, these are the
     * same as the standard transaction totals.
     *
     * @return tender display transaction totals
     */
    public TransactionTotalsIfc getTenderTransactionTotals()
    {
        return (getTransactionTotals());
    }

    /**
     * Retrieves size of tenderLineItemsVector vector.
     *
     * @return tender line items vector size
     */
    public int getTenderLineItemsSize()
    {
        return tenderLineItemsVector.size();
    }

    /**
     * The metod set the echeck declined items.
     *
     * @param item
     */
    public void addECheckDeclinedItems(TenderLineItemIfc item)
    {
        if (ECheckDeclinedItems == null)
        {
            ECheckDeclinedItems = new Vector<TenderLineItemIfc>();
        }
        if (item != null)
        {
            ECheckDeclinedItems.addElement(item);
        }
    }

    /**
     * This method returns all echecks that were declined.
     *
     * @return vector Decline echecks
     */
    public Vector<TenderLineItemIfc> getECheckDeclinedItems()
    {
        return ECheckDeclinedItems;
    }

    /**
     * Check to see if the transaction is taxable. A transaction is taxable is
     * assumed taxable, unless every item in the transaction is a non-taxable
     * item.
     *
     * @return true or false
     * @since 7.0
     */
    public boolean isTaxableTransaction()
    {
        boolean taxableItemFound = false;
        boolean nonTaxableItemFound = false;
        if (getTransactionTotals() != null && getTransactionTotals().getTaxInformationContainer() != null)
        {
            TaxInformationIfc[] taxInfo = getTransactionTotals().getTaxInformationContainer().getTaxInformation();
            if (taxInfo != null)
            {
                for (int i = 0; i < taxInfo.length; i++)
                {
                    if (taxInfo[i].getTaxMode() == TaxConstantsIfc.TAX_MODE_NON_TAXABLE
                            || taxInfo[i].getTaxMode() == TaxConstantsIfc.TAX_MODE_EXEMPT
                            || taxInfo[i].getTaxMode() == TaxConstantsIfc.TAX_MODE_TOGGLE_OFF)
                    {
                        nonTaxableItemFound = true;
                    }
                    else
                    {
                        taxableItemFound = true;
                    }
                }
            }
        }

        boolean taxable = true; // default
        if (!taxableItemFound && nonTaxableItemFound)
        {
            taxable = false;
        }
        return taxable;
    }

    /**
     * Method to default display string function.
     *
     * @return String representation of object
     */
    public String toString()
    {
        // set result string
        StringBuilder strResult = Util.classToStringHeader("AbstractTenderableTransaction", null,
                hashCode());
        strResult.append(super.toString()).append(
                Util.formatToStringEntry("Tender line items", getTenderLineItems())).append(
                Util.formatToStringEntry("Customer", getCustomer())).append(
                Util.formatToStringEntry("IRSCustomer", getIRSCustomer())).append(
                Util.formatToStringEntry("Transaction totals", getTransactionTotals())).append(
                Util.formatToStringEntry("UniqueID", getUniqueID()));
        // pass back result
        return (strResult.toString());
    }

    /* (non-Javadoc)
     * @see oracle.retail.stores.domain.transaction.Transaction#equals(java.lang.Object)
     */
    @Override
    public boolean equals(Object obj)
    {
        boolean equal = false;

        // If it's a AbstractTenderableTransaction, compare its attributes
        if (obj instanceof AbstractTenderableTransaction)
        {
            // downcast the input object
            AbstractTenderableTransaction c = (AbstractTenderableTransaction)obj;
            if (!super.equals(obj))
            {
                equal = false;
            }
            else if (!Util.isObjectEqual(tenderLineItemsVector, c.tenderLineItemsVector))
            {
                equal = false;
            }
            else if (!Util.isObjectEqual(totals, c.totals))
            {
                equal = false;
            }
            else if (!Util.isObjectEqual(customer, c.customer))
            {
                equal = false;
            }
            else if (!Util.isObjectEqual(irsCustomer, c.irsCustomer))
            {
                equal = false;
            }
            else if (!Util.isObjectEqual(uniqueID, c.uniqueID))
            {
                equal = false;
            }
            else
            {
                equal = true;
            }
        }

        return (equal);
    }

    /**
     * Retrieves id of customer associated with transaction.
     */
    
    public String getCustomerId()
    {
        return customerId;
    }
    /**
     * Sets id of customer associated with transaction.
     */
    public void setCustomerId(String customerId)
    {
        this.customerId = customerId;
    }
}
