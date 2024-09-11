/* ===========================================================================
* Copyright (c) 2008, 2013, Oracle and/or its affiliates. All rights reserved.
 * ===========================================================================
 * $Header: rgbustores/modules/domain/src/oracle/retail/stores/domain/financial/FinancialTotals.java /main/23 2013/06/28 12:20:15 subrdey Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    subrdey   06/27/13 - Removing the break down of orders in till summary
 *                         Report
 *    mkutiana  02/14/13 - cash rounded amount change in and out setters and
 *                         getters created
 *    jswan     02/20/13 - Modified for Currency Rounding.
 *    mjwallac  04/24/12 - Fixes for Fortify redundant null check
 *    cgreene   12/05/11 - updated from deprecated packages and used more
 *                         bigdecimal constants
 *    hyin      10/04/11 - Forward port: POS layaway new amount is not printed
 *    mchellap  09/29/11 - Fixed VAT total in summary reports
 *    nkgautam  08/04/10 - fixed junit failure issue
 *    nkgautam  08/03/10 - added bill payment amount and count
 *    cgreene   05/26/10 - convert to oracle packaging
 *    cgreene   04/28/10 - updating deprecated names
 *    abondala  01/03/10 - update header date
 *    cgreene   04/16/09 - refactor buildCombinedExpectedCount methods into one
 *    miparek   12/11/08 - fixing d# 830, INCORRECT DEPARTMENT SALES REPORT
 *                         FROM REGISTER WHEN ADDED RESTOCKING FEE
 *    cgreene   09/19/08 - updated with changes per FindBugs findings
 *    cgreene   09/11/08 - update header
 *
 * ===========================================================================
 * $Log:
      12   360Commerce 1.11        7/9/2007 4:02:27 PM    Anda D. Cadar   I18N
           change for CR 27494 - POS 1st initialization when server is offline
      11   360Commerce 1.10        6/26/2007 11:13:58 AM  Ashok.Mondal    I18N
           changes to export and import POSLog.
      10   360Commerce 1.9         5/22/2007 12:07:38 PM  Jack G. Swan
           Hardtotals fields must be read in the same order they are written.
            Fixed an ordering issue that was introduced by the previous
           change.
      9    360Commerce 1.8         5/16/2007 7:56:04 PM   Brett J. Larsen
           CR 26903 - 8.0.1 merge to trunk

           BackOffice <ARG> Summary Report overhaul (many CRs fixed)

      8    360Commerce 1.7         5/14/2007 6:08:34 PM   Sandy Gu
           update inclusive information in financial totals and history tables
      7    360Commerce 1.6         4/25/2007 10:00:57 AM  Anda D. Cadar   I18N
           merge
      6    360Commerce 1.5         2/6/2007 11:13:19 AM   Anil Bondalapati
           Merge from FinancialTotals.java, Revision 1.3.1.0
      5    360Commerce 1.4         12/8/2006 5:01:16 PM   Brendan W. Farrell
           Read the tax history when creating pos log for openclosetill
           transactions.  Rewrite of some code was needed.
      4    360Commerce 1.3         1/22/2006 11:41:29 AM  Ron W. Haight
           Removed references to com.ibm.math.BigDecimal
      3    360Commerce 1.2         3/31/2005 4:28:11 PM   Robert Pearse
      2    360Commerce 1.1         3/10/2005 10:21:41 AM  Robert Pearse
      1    360Commerce 1.0         2/11/2005 12:11:05 PM  Robert Pearse
     $
 * ===========================================================================
 */
package oracle.retail.stores.domain.financial;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.List;

import oracle.retail.stores.commerceservices.common.currency.CurrencyIfc;
import oracle.retail.stores.domain.DomainGateway;
import oracle.retail.stores.domain.financial.TaxTotalsContainerIfc;
import oracle.retail.stores.common.utility.Util;
import java.math.BigDecimal;

import org.apache.log4j.Logger;


/**
 * This class represents the financial totals maintained by the application at
 * the store, register and till level.
 * <P>
 * Totals in this class are broken down into many categories. Sales are
 * accumulated for taxable and non-taxable, taxed and tax-exempt and sales and
 * returns at the item and transaction levels. For each of those categories, a
 * unit count and a summary amount are kept.
 * <P>
 * In addition, the count and amounts of line voids, discounts and other items
 * are accumulated.
 * <P>
 * Transactions which are counted and reconciled, such as starting and ending
 * float, till pickups and till loans, are accumulated in ReconcilableCountIfc
 * classes. A combinedCount member is used to perform a complete reconciliation
 * of the entity's financial entity.
 * <P>
 * Activity resulting in tenders from sales and returns are managed in
 * tenderCount, a FinancialCountIfc class. Tendered activity is never reconciled
 * by itself; rather, it is reconciled as part of the combined count.
 * <P>
 *
 * @see oracle.retail.stores.domain.financial.ReconcilableCount
 * @see oracle.retail.stores.domain.financial.FinancialCount
 * @version $Revision: /main/23 $
 */
public class FinancialTotals implements FinancialTotalsIfc
{
    // This id is used to tell the compiler not to generate a new serialVersionUID.
    static final long serialVersionUID = 6463220832067966717L;

    /**
     * revision number supplied by source-code-control system
     */
    public static String revisionNumber = "$Revision: /main/23 $";

    /**
     * count of transactions
     */
    protected int transactionCount = 0;

    /**
     * count of transactions with return items
     */
    protected int transactionsWithReturnedItemsCount = 0;

    /**
     * amount of gross taxable item sales (tax excluded)
     */
    protected CurrencyIfc amountGrossTaxableItemSales = null;

    /**
     * number of units in gross taxable item sales
     */
    protected BigDecimal unitsGrossTaxableItemSales = null;

    /**
     * amount of gross non-taxable item sales
     */
    protected CurrencyIfc amountGrossNonTaxableItemSales = null;

    /**
     * number of units in gross non-taxable item sales
     */
    protected BigDecimal unitsGrossNonTaxableItemSales = null;

    /**
     * amount of gross tax-exempt item sales
     */
    protected CurrencyIfc amountGrossTaxExemptItemSales = null;

    /**
     * number of units in gross tax-exempt item sales
     */
    protected BigDecimal unitsGrossTaxExemptItemSales = null;

    /**
     * amount of gross taxable item sales voided
     */
    protected CurrencyIfc amountGrossTaxableItemSalesVoided = null;

    /**
     * number of units in gross taxable item sales voided
     */
    protected BigDecimal unitsGrossTaxableItemSalesVoided = null;

    /**
     * amount of gross non-taxable item sales voided
     */
    protected CurrencyIfc amountGrossNonTaxableItemSalesVoided = null;

    /**
     * number of units in gross non-taxable item sales voided
     */
    protected BigDecimal unitsGrossNonTaxableItemSalesVoided = null;

    /**
     * amount of gross tax-exempt item sales voided
     */
    protected CurrencyIfc amountGrossTaxExemptItemSalesVoided = null;

    /**
     * number of units in gross tax-exempt item sales voided
     */
    protected BigDecimal unitsGrossTaxExemptItemSalesVoided = null;

    /**
     * amount of gross taxable item returns (tax excluded)
     */
    protected CurrencyIfc amountGrossTaxableItemReturns = null;

    /**
     * number of units in gross taxable item returns
     */
    protected BigDecimal unitsGrossTaxableItemReturns = null;

    /**
     * amount of gross non-taxable item returns
     */
    protected CurrencyIfc amountGrossNonTaxableItemReturns = null;

    /**
     * number of units in gross non-taxable item returns
     */
    protected BigDecimal unitsGrossNonTaxableItemReturns = null;

    /**
     * amount of gross tax-exempt item returns
     */
    protected CurrencyIfc amountGrossTaxExemptItemReturns = null;

    /**
     * number of units in gross tax-exempt item returns
     */
    protected BigDecimal unitsGrossTaxExemptItemReturns = null;

    /**
     * amount of gross taxable item returns voided
     */
    protected CurrencyIfc amountGrossTaxableItemReturnsVoided = null;

    /**
     * number of units in gross taxable item returns voided
     */
    protected BigDecimal unitsGrossTaxableItemReturnsVoided = null;

    /**
     * amount of gross non-taxable item returns voided
     */
    protected CurrencyIfc amountGrossNonTaxableItemReturnsVoided = null;

    /**
     * number of units in gross non-taxable item returns voided
     */
    protected BigDecimal unitsGrossNonTaxableItemReturnsVoided = null;

    /**
     * amount of gross tax-exempt item returns voided
     */
    protected CurrencyIfc amountGrossTaxExemptItemReturnsVoided = null;

    /**
     * number of units in gross tax-exempt item returns voided
     */
    protected BigDecimal unitsGrossTaxExemptItemReturnsVoided = null;

    /**
     * amount of tax collected for items sold
     */
    protected CurrencyIfc amountTaxItemSales = null;

    /**
     * amount of tax collected for items sold
     */
    protected CurrencyIfc amountInclusiveTaxItemSales = null;

    /**
     * amount of tax on items returned
     */
    protected CurrencyIfc amountTaxItemReturns = null;

    /**
     * amount of tax on items returned
     */
    protected CurrencyIfc amountInclusiveTaxItemReturns = null;

    /**
     * amount of tax collected for on sale transactions
     */
    protected CurrencyIfc amountTaxTransactionSales = null;

    /**
     * amount of tax collected for on sale transactions
     */
    protected CurrencyIfc amountInclusiveTaxTransactionSales = null;

    /**
     * amount of tax on items returned
     */
    protected CurrencyIfc amountTaxTransactionReturns = null;

    /**
     * amount of tax on items returned
     */
    protected CurrencyIfc amountInclusiveTaxTransactionReturns = null;

    /**
     * amount of gross taxable sale transactions (tax excluded)
     */
    protected CurrencyIfc amountGrossTaxableTransactionSales = null;

    /**
     * count of taxable sale transactions
     */
    protected int countGrossTaxableTransactionSales = 0;

    /**
     * amount of gross non-taxable sale transactions
     */
    protected CurrencyIfc amountGrossNonTaxableTransactionSales = null;

    /**
     * count of non-taxable sale transactions
     */
    protected int countGrossNonTaxableTransactionSales = 0;

    /**
     * amount of gross tax-exempt sale transactions
     */
    protected CurrencyIfc amountGrossTaxExemptTransactionSales = null;

    /**
     * count of tax-exempt sale transactions
     */
    protected int countGrossTaxExemptTransactionSales = 0;

    /**
     * amount of gross taxable transaction sales voided
     */
    protected CurrencyIfc amountGrossTaxableTransactionSalesVoided = null;

    /**
     * count of taxable transaction sales voided
     */
    protected int countGrossTaxableTransactionSalesVoided = 0;

    /**
     * amount of gross non-taxable transaction sales voided
     */
    protected CurrencyIfc amountGrossNonTaxableTransactionSalesVoided = null;

    /**
     * count of non-taxable transaction sales voided
     */
    protected int countGrossNonTaxableTransactionSalesVoided = 0;

    /**
     * amount of gross tax-exempt transaction sales voided
     */
    protected CurrencyIfc amountGrossTaxExemptTransactionSalesVoided = null;

    /**
     * count of tax-exempt transaction sales voided
     */
    protected int countGrossTaxExemptTransactionSalesVoided = 0;

    /**
     * amount of gross taxable return transactions (tax excluded)
     */
    protected CurrencyIfc amountGrossTaxableTransactionReturns = null;

    /**
     * count of taxable return transactions
     */
    protected int countGrossTaxableTransactionReturns = 0;

    /**
     * amount of gross non-taxable return transactions
     */
    protected CurrencyIfc amountGrossNonTaxableTransactionReturns = null;

    /**
     * count of non-taxable return transactions
     */
    protected int countGrossNonTaxableTransactionReturns = 0;

    /**
     * amount of gross tax-exempt return transactions
     */
    protected CurrencyIfc amountGrossTaxExemptTransactionReturns = null;

    /**
     * count of tax-exempt return transactions
     */
    protected int countGrossTaxExemptTransactionReturns = 0;

    /**
     * amount of gross taxable return transactions voided
     */
    protected CurrencyIfc amountGrossTaxableTransactionReturnsVoided = null;

    /**
     * count of gross taxable return transactions voided
     */
    protected int countGrossTaxableTransactionReturnsVoided = 0;

    /**
     * amount of gross non-taxable return transactions voided
     */
    protected CurrencyIfc amountGrossNonTaxableTransactionReturnsVoided = null;

    /**
     * count of gross non-taxable return transactions voided
     */
    protected int countGrossNonTaxableTransactionReturnsVoided = 0;

    /**
     * amount of gross tax-exemt return transactions voided
     */
    protected CurrencyIfc amountGrossTaxExemptTransactionReturnsVoided = null;

    /**
     * count of gross tax-exemt return transactions voided
     */
    protected int countGrossTaxExemptTransactionReturnsVoided = 0;

    /**
     * amount of gift certificate sales
     */
    protected CurrencyIfc amountGiftCertificateSales = null;

    /**
     * units of gift certificate sales
     */
    protected int unitsGiftCertificateSales = 0;

    /**
     * amount of gross house payments transactions
     */
    protected CurrencyIfc amountHousePayments = null;

    /**
     * count of house payments transactions
     */
    protected int countHousePayments = 0;
    
    /**
     * count of bill payments transactions
     */
    protected int countBillPayments = 0;

    /**
     * amount of transaction discounts
     */
    protected CurrencyIfc amountTransactionDiscounts = null;

    /**
     * number of transaction discounts
     */
    protected int numberTransactionDiscounts = 0;

    /**
     * amount of item discounts
     */
    protected CurrencyIfc amountItemDiscounts = null;

    /**
     * number of item discounts
     */
    protected int numberItemDiscounts = 0;

    /**
     * amount of item markdowns
     */
    protected CurrencyIfc amountItemMarkdowns = null;

    /**
     * number of item markdowns
     */
    protected int numberItemMarkdowns = 0;

    /**
     * amount of restocking fees
     */
    protected CurrencyIfc amountRestockingFees = null;

    /**
     * number of restocking fees
     */
    protected BigDecimal unitsRestockingFees = null;

    /**
     * amount of restocking fees
     */
    protected CurrencyIfc amountRestockingFeesFromNonTaxableItems = null;

    /**
     * number of restocking fees
     */
    protected BigDecimal unitsRestockingFeesFromNonTaxableItems = null;

    /**
     * amount of shipping charges
     */
    protected CurrencyIfc amountShippingCharges = null;

    /**
     * number of shipping charges
     */
    protected int numberShippingCharges = 0;

    /**
     * amount of shipping charges tax
     */
    protected CurrencyIfc amountTaxShippingCharges = null;

    /**
     * amount of shipping charges inclusive tax
     */
    protected CurrencyIfc amountInclusiveTaxShippingCharges = null;

    /**
     * StoreCouponDiscounts
     */
    /**
     * amount of item Store Coupons
     */
    protected CurrencyIfc amountItemDiscStoreCoupons = null;

    /**
     * number of item Store Coupons
     */
    protected int numberItemDiscStoreCoupons = 0;

    /**
     * amount of transaction Store Coupons
     */
    protected CurrencyIfc amountTransactionDiscStoreCoupons = null;

    /**
     * number of transaction Store Coupons
     */
    protected int numberTransactionDiscStoreCoupons = 0;

    /**
     * amount of transaction post-voids
     */
    protected CurrencyIfc amountPostVoids = null;

    /**
     * number of transaction post-voids
     */
    protected int numberPostVoids = 0;

    /**
     * number of no-sale transactions
     */
    protected int numberNoSales = 0;

    /**
     * amount of line voids (deleted lines)
     */
    protected CurrencyIfc amountLineVoids = null;

    /**
     * units on line voids (deleted lines)
     */
    protected BigDecimal unitsLineVoids = null;

    /**
     * amount of cancelled transactions
     */
    protected CurrencyIfc amountCancelledTransactions = null;

    /**
     * number of cancelled transactions
     */
    protected int numberCancelledTransactions = 0;

    /**
     * amount of gross taxable non-mechandise sales
     */
    protected CurrencyIfc amountGrossTaxableNonMerchandiseSales = null;

    /**
     * units of gross taxable non-Merchandise sales
     */
    protected BigDecimal unitsGrossTaxableNonMerchandiseSales = null;

    /**
     * amount of gross taxable non-Merchandise sales
     */
    protected CurrencyIfc amountGrossNonTaxableNonMerchandiseSales = null;

    /**
     * units of gross taxable non-Merchandise sales
     */
    protected BigDecimal unitsGrossNonTaxableNonMerchandiseSales = null;

    /**
     * amount of gross taxable non-mechandise sales voided
     */
    protected CurrencyIfc amountGrossTaxableNonMerchandiseSalesVoided = null;

    /**
     * number of units in gross taxable non-mechandise sales voided
     */
    protected BigDecimal unitsGrossTaxableNonMerchandiseSalesVoided = null;

    /**
     * amount of gross non-taxable non-mechandise sales voided
     */
    protected CurrencyIfc amountGrossNonTaxableNonMerchandiseSalesVoided = null;

    /**
     * number of units in gross non-taxable non-mechandise sales voided
     */
    protected BigDecimal unitsGrossNonTaxableNonMerchandiseSalesVoided = null;

    /**
     * amount of gross taxable non-Merchandise returns
     */
    protected CurrencyIfc amountGrossTaxableNonMerchandiseReturns = null;

    /**
     * units of gross taxable non-Merchandise returns
     */
    protected BigDecimal unitsGrossTaxableNonMerchandiseReturns = null;

    /**
     * amount of gross non taxable non-Merchandise Returns
     */
    protected CurrencyIfc amountGrossNonTaxableNonMerchandiseReturns = null;

    /**
     * units of gross non taxable non-Merchandise Returns
     */
    protected BigDecimal unitsGrossNonTaxableNonMerchandiseReturns = null;

    /**
     * amount of gross taxable non-Merchandise returns voided
     */
    protected CurrencyIfc amountGrossTaxableNonMerchandiseReturnsVoided = null;

    /**
     * number of units in gross taxable non-Merchandise returns voided
     */
    protected BigDecimal unitsGrossTaxableNonMerchandiseReturnsVoided = null;

    /**
     * amount of gross non-taxable non-Merchandise returns voided
     */
    protected CurrencyIfc amountGrossNonTaxableNonMerchandiseReturnsVoided = null;

    /**
     * number of units in gross non-taxable non-Merchandise returns voided
     */
    protected BigDecimal unitsGrossNonTaxableNonMerchandiseReturnsVoided = null;

    /**
     * amount of gross gift card item Sales
     */
    protected CurrencyIfc amountGrossGiftCardItemSales = null;

    /**
     * units of gross gift card item Sales
     */
    protected BigDecimal unitsGrossGiftCardItemSales = null;

    /**
     * amount of gross gift card item Sales voided
     */
    protected CurrencyIfc amountGrossGiftCardItemSalesVoided = null;

    /**
     * number of units in gross gift card item Sales voided
     */
    protected BigDecimal unitsGrossGiftCardItemSalesVoided = null;

    /**
     * amount of gross gift card item Returns
     */
    protected CurrencyIfc amountGrossGiftCardItemReturns = null;

    /**
     * units of gross gift card item Returns
     */
    protected BigDecimal unitsGrossGiftCardItemReturns = null;

    /**
     * amount of gross gift card item Returns voided
     */
    protected CurrencyIfc amountGrossGiftCardItemReturnsVoided = null;

    /**
     * number of units in gross gift card item Returns voided
     */
    protected BigDecimal unitsGrossGiftCardItemReturnsVoided = null;

    /**
     * number of gross gift card item issued
     */
    protected CurrencyIfc amountGrossGiftCardItemIssued = null;

    /**
     * unit of gross gift card item issued
     */
    protected BigDecimal unitsGrossGiftCardItemIssued = null;

    /**
     * number of gross gift card item reloaded
     */
    protected CurrencyIfc amountGrossGiftCardItemReloaded = null;

    /**
     * unit of gross gift card item reloaded
     */
    protected BigDecimal unitsGrossGiftCardItemReloaded = null;

    /**
     * number of gross gift card item redeemed
     */
    protected CurrencyIfc amountGrossGiftCardItemRedeemed = null;

    /**
     * unit of gross gift card item redeemed
     */
    protected BigDecimal unitsGrossGiftCardItemRedeemed = null;

    /**
     * number of gross gift card item issue voided
     */
    protected CurrencyIfc amountGrossGiftCardItemIssueVoided = null;

    /**
     * unit of gross gift card item issue voided
     */
    protected BigDecimal unitsGrossGiftCardItemIssueVoided = null;

    /**
     * number of gross gift card item reload voided
     */
    protected CurrencyIfc amountGrossGiftCardItemReloadVoided = null;

    /**
     * unit of gross gift card item reload voided
     */
    protected BigDecimal unitsGrossGiftCardItemReloadVoided = null;

    /**
     * number of gross gift card item redeem voided
     */
    protected CurrencyIfc amountGrossGiftCardItemRedeemedVoided = null;

    /**
     * unit of gross gift card item redeem voided
     */
    protected BigDecimal unitsGrossGiftCardItemRedeemedVoided = null;

    /**
     * starting float count
     */
    protected ReconcilableCountIfc startingFloatCount = null;

    /**
     * ending float count
     */
    protected ReconcilableCountIfc endingFloatCount = null;

    /**
     * starting safe count
     */
    protected ReconcilableCountIfc startingSafeCount = null;

    /**
     * ending safe count
     */
    protected ReconcilableCountIfc endingSafeCount = null;

    /**
     * float count
     *
     * @deprecated Use #startingFloatCount or #endingFloatCount
     */
    protected ReconcilableCountIfc floatCount = null;

    /**
     * till pickups ArrayList
     */
    protected List<ReconcilableCountIfc> tillPickupsList = null;

    /**
     * count of till pickups
     */
    protected Hashtable<String,Integer> countTillPickups = null;

    /**
     * till loans ArrayList
     */
    protected List<ReconcilableCountIfc> tillLoansList = null;

    /**
     * count of till loans
     */
    protected int countTillLoans = 0;

    /**
     * till pay-ins ArrayList
     */
    protected List<ReconcilableCountIfc> tillPayInsList = null;

    /**
     * till pay-outs ArrayList
     */
    protected List<ReconcilableCountIfc> tillPayOutsList = null;

    /**
     * tender count
     */
    protected FinancialCountIfc tenderCount = null;

    /**
     * financial combined counts
     */
    protected ReconcilableCountIfc combinedCount = null;

    /**
     * amount of Till Pay-Ins
     *
     * @deprecated Use #tillPayInsList
     */
    protected CurrencyIfc amountTillPayIns = null;

    /**
     * number of Till Pay-Ins
     *
     * @deprecated Use #tillPayInsList
     */
    protected int countTillPayIns = 0;

    /**
     * amount of Till Pay-Out
     *
     * @deprecated Use #tillPayOutsList
     */
    protected CurrencyIfc amountTillPayOuts = null;

    /**
     * number of Till Pay-Out
     *
     * @deprecated Use #tillPayOutsList
     */
    protected int countTillPayOuts = 0;

    /**
     * amount of layaway payments
     */
    protected CurrencyIfc amountLayawayPayments = null;

    /**
     * count of layaway payments
     */
    protected int countLayawayPayments = 0;

    /**
     * amount of layaway new payments made
     */
    protected CurrencyIfc amountLayawayNew = null;

    /**
     * amount of new Special Orders
     */
    protected CurrencyIfc amountSpecialOrderNew = null;

    /**
     * amount of Special Order partial payments
     */
    protected CurrencyIfc amountSpecialOrderPartial = null;

    /**
     * count if layaway new payments made
     */
    protected int countLayawayNew = 0;

    /**
     * amount of layaway new payments made
     */
    protected CurrencyIfc amountLayawayPickup = null;

    /**
     * count if layaway new payments made
     */
    protected int countLayawayPickup = 0;

    /**
     * amount of layaway payments refunded for deletions
     */
    protected CurrencyIfc amountLayawayDeletions = null;

    /**
     * count of layaway deletions
     */
    protected int countLayawayDeletions = 0;

    /**
     * amount of layaway initiation fees
     */
    protected CurrencyIfc amountLayawayInitiationFees = null;

    /**
     * count of layaway initiation fees
     */
    protected int countLayawayInitiationFees = 0;

    /**
     * amount of layaway deletion fees
     */
    protected CurrencyIfc amountLayawayDeletionFees = null;

    /**
     * count of layaway deletion fees
     */
    protected int countLayawayDeletionFees = 0;

    /**
     * amount of special order payments
     */
    protected CurrencyIfc amountOrderPayments = null;

    /**
     * count of special order payments
     */
    protected int countOrderPayments = 0;

    /**
     * amount of special order payments refunded for cancellations
     */
    protected CurrencyIfc amountOrderCancels = null;

    /**
     * count of special order cancellations
     */
    protected int countOrderCancels = 0;

    /**
     * amount of employee discounts
     *
     * @deprecated As of 7.0.0, replaced by
     *             {@link #amountGrossItemEmployeeDiscount} and
     *             {@link #amountGrossTransactionEmployeeDiscount}
     */
    protected CurrencyIfc amountEmployeeDiscounts = null;

    /**
     * units of employee discounts
     */
    protected BigDecimal unitsEmployeeDiscounts = null;

    /**
     * amount of customer discounts
     */
    protected CurrencyIfc amountCustomerDiscounts = null;

    /**
     * units of customer discounts
     */
    protected BigDecimal unitsCustomerDiscounts = null;

    /**
     * amount of price overrides
     */
    protected CurrencyIfc amountPriceOverrides = null;

    /**
     * units of price overrides
     */
    protected BigDecimal unitsPriceOverrides = null;

    /**
     * units of price adjustments
     */
    protected BigDecimal unitsPriceAdjustments = null;

    /**
     * amount of gross gift certificate issued
     */
    protected CurrencyIfc amountGrossGiftCertificateIssued = null;

    /**
     * units of gross gift certificate issued
     */
    protected BigDecimal unitsGrossGiftCertificateIssued = null;

    /**
     * amount of gross gift certificate issued voided
     */
    protected CurrencyIfc amountGrossGiftCertificateIssuedVoided = null;

    /**
     * number of units in gross gift certificate issued voided
     */
    protected BigDecimal unitsGrossGiftCertificateIssuedVoided = null;

    /**
     * amount of gross gift certificate tendered
     */
    protected CurrencyIfc amountGrossGiftCertificateTendered = null;

    /**
     * units of gross gift certificate tendered
     */
    protected BigDecimal unitsGrossGiftCertificateTendered = null;

    /**
     * amount of gross gift certificate tendered voided
     */
    protected CurrencyIfc amountGrossGiftCertificateTenderedVoided = null;

    /**
     * number of units in gross gift certificate tendered voided
     */
    protected BigDecimal unitsGrossGiftCertificateTenderedVoided = null;

    /**
     * Total number of house enrollments approved
     */
    protected int houseCardEnrollmentsApproved = 0;

    /**
     * Total number of house enrollments declined
     */
    protected int houseCardEnrollmentsDeclined = 0;

    /**
     * Gross Amount of Gift Card Item Credits
     */
    protected CurrencyIfc amountGrossGiftCardItemCredit = null;

    /**
     * Gross Units of Gift Card Item Credits
     */
    protected BigDecimal unitsGrossGiftCardItemCredit = null;

    /**
     * Gross Amount of Gift Card Item Credits Voided
     */
    protected CurrencyIfc amountGrossGiftCardItemCreditVoided = null;

    /**
     * Gross Units of Gift Card Item Credits Voided
     */
    protected BigDecimal unitsGrossGiftCardItemCreditVoided = null;

    /**
     * Gross Amount of Gift Certificates Redeemed
     */
    protected CurrencyIfc amountGrossGiftCertificatesRedeemed = null;

    /**
     * Gross Units of Gift Certificates Redeemed
     */
    protected BigDecimal unitsGrossGiftCertificatesRedeemed = null;

    /**
     * Gross Amount of Gift Certificates Redeemed Voided
     */
    protected CurrencyIfc amountGrossGiftCertificatesRedeemedVoided = null;

    /**
     * Gross Units of Gift Certificates Redeemed Voided
     */
    protected BigDecimal unitsGrossGiftCertificatesRedeemedVoided = null;

    /**
     * Gross Amount of Store Credits Issued
     */
    protected CurrencyIfc amountGrossStoreCreditsIssued = null;

    /**
     * Gross Units of Store Credits Issued
     */
    protected BigDecimal unitsGrossStoreCreditsIssued = null;

    /**
     * Gross Amount of Store Credits Issued Voided
     */
    protected CurrencyIfc amountGrossStoreCreditsIssuedVoided = null;

    /**
     * Gross Units of Store Credits Issued Voided
     */
    protected BigDecimal unitsGrossStoreCreditsIssuedVoided = null;

    /**
     * Gross Amount of Store Credits Redeemed
     */
    protected CurrencyIfc amountGrossStoreCreditsRedeemed = null;

    /**
     * Gross Units of Store Credits Redeemed
     */
    protected BigDecimal unitsGrossStoreCreditsRedeemed = null;

    /**
     * Gross Amount of Store Credits Redeemed Voided
     */
    protected CurrencyIfc amountGrossStoreCreditsRedeemedVoided = null;

    /**
     * Gross Units of Store Credits Redeemed Voided
     */
    protected BigDecimal unitsGrossStoreCreditsRedeemedVoided = null;

    /**
     * Gross Amount of Item Employee Discounts
     */
    protected CurrencyIfc amountGrossItemEmployeeDiscount = null;

    /**
     * Gross Units of Item Employee Discounts
     */
    protected BigDecimal unitsGrossItemEmployeeDiscount = null;

    /**
     * Gross Amount of Item Employee Discounts Voided
     */
    protected CurrencyIfc amountGrossItemEmployeeDiscountVoided = null;

    /**
     * Gross Units of Item Employee Discounts Voided
     */
    protected BigDecimal unitsGrossItemEmployeeDiscountVoided = null;

    /**
     * Gross Amount of Transaction Employee Discounts
     */
    protected CurrencyIfc amountGrossTransactionEmployeeDiscount = null;

    /**
     * Gross Units of Transaction Employee Discounts
     */
    protected BigDecimal unitsGrossTransactionEmployeeDiscount = null;

    /**
     * Gross Amount of Transaction Employee Discounts Voided
     */
    protected CurrencyIfc amountGrossTransactionEmployeeDiscountVoided = null;

    /**
     * Gross Units of Transaction Employee Discounts Voided
     */
    protected BigDecimal unitsGrossTransactionEmployeeDiscountVoided = null;
    
    /**
     * amount of bill payments transactions
     */
    protected CurrencyIfc amountBillPayments = null;

    /**
     * amount of change rounded in
     */
    protected CurrencyIfc amountChangeRoundedIn = null;

    /**
     * amount of change rounded out
     */
    protected CurrencyIfc amountChangeRoundedOut = null;

    /**
     * Container holding tax totals
     */
    protected TaxTotalsContainerIfc taxTotalsContainer = null;

    /**
     * Currency ID
     */
    protected int currencyID; // I18N
    
    /**
     * Check if VAT is enabled
     */
    boolean vatEnabled = false;

    /** The logger to which log messages will be sent. */
    static Logger logger = Logger.getLogger(oracle.retail.stores.domain.financial.FinancialTotals.class);

    /**
     * Constructs FinancialTotals object.
     */
    public FinancialTotals()
    {
        startingFloatCount = instantiateReconcilableCountIfc();
        endingFloatCount = instantiateReconcilableCountIfc();
        startingSafeCount = instantiateReconcilableCountIfc();
        endingSafeCount = instantiateReconcilableCountIfc();
        tenderCount = instantiateFinancialCountIfc();
        combinedCount = instantiateReconcilableCountIfc();
        tillLoansList = new ArrayList<ReconcilableCountIfc>();
        tillPickupsList = new ArrayList<ReconcilableCountIfc>();
        tillPayInsList = new ArrayList<ReconcilableCountIfc>();
        tillPayOutsList = new ArrayList<ReconcilableCountIfc>();
        countTillPickups = new Hashtable<String,Integer>(5);
        // this line is deprecated
        floatCount = instantiateReconcilableCountIfc();
        // $$ financialTotals1.move(0,0);
    }

    /**
     * Resets totals, setting all values to zero and eliminating array of
     * FinancialCountTenderItem entries.
     */
    public void resetTotals()
    {
        transactionsWithReturnedItemsCount = 0;
        transactionCount = 0;
        amountGrossTaxableItemSales = null;
        unitsGrossTaxableItemSales = null;
        amountGrossNonTaxableItemSales = null;
        unitsGrossNonTaxableItemSales = null;
        amountGrossTaxExemptItemSales = null;
        unitsGrossTaxExemptItemSales = null;
        amountGrossTaxableItemReturns = null;
        unitsGrossTaxableItemReturns = null;
        amountGrossNonTaxableItemReturns = null;
        unitsGrossNonTaxableItemReturns = null;
        amountGrossTaxExemptItemReturns = null;
        unitsGrossTaxExemptItemReturns = null;
        amountGrossTaxableItemSalesVoided = null;
        unitsGrossTaxableItemSalesVoided = null;
        amountGrossNonTaxableItemSalesVoided = null;
        unitsGrossNonTaxableItemSalesVoided = null;
        amountGrossTaxExemptItemSalesVoided = null;
        unitsGrossTaxExemptItemSalesVoided = null;
        amountGrossTaxableItemReturnsVoided = null;
        unitsGrossTaxableItemReturnsVoided = null;
        amountGrossNonTaxableItemReturnsVoided = null;
        unitsGrossNonTaxableItemReturnsVoided = null;
        amountGrossTaxExemptItemReturnsVoided = null;
        unitsGrossTaxExemptItemReturnsVoided = null;
        amountTaxItemSales = null;
        amountInclusiveTaxItemSales = null;
        amountTaxItemReturns = null;
        amountInclusiveTaxItemReturns = null;
        amountTaxTransactionSales = null;
        amountInclusiveTaxTransactionSales = null;
        amountTaxTransactionReturns = null;
        amountInclusiveTaxTransactionReturns = null;
        amountGrossTaxableTransactionSales = null;
        countGrossTaxableTransactionSales = 0;
        amountGrossNonTaxableTransactionSales = null;
        countGrossNonTaxableTransactionSales = 0;
        amountGrossTaxExemptTransactionSales = null;
        countGrossTaxExemptTransactionSales = 0;
        amountGrossTaxableTransactionReturns = null;
        countGrossTaxableTransactionReturns = 0;
        amountGrossNonTaxableTransactionReturns = null;
        countGrossNonTaxableTransactionReturns = 0;
        amountGrossTaxExemptTransactionReturns = null;
        countGrossTaxExemptTransactionReturns = 0;
        amountGrossTaxableTransactionSalesVoided = null;
        countGrossTaxableTransactionSalesVoided = 0;
        amountGrossNonTaxableTransactionSalesVoided = null;
        countGrossNonTaxableTransactionSalesVoided = 0;
        amountGrossTaxExemptTransactionSalesVoided = null;
        countGrossTaxExemptTransactionSalesVoided = 0;
        amountGrossTaxableTransactionReturnsVoided = null;
        countGrossTaxableTransactionReturnsVoided = 0;
        amountGrossNonTaxableTransactionReturnsVoided = null;
        countGrossNonTaxableTransactionReturnsVoided = 0;
        amountGrossTaxExemptTransactionReturnsVoided = null;
        countGrossTaxExemptTransactionReturnsVoided = 0;
        amountGiftCertificateSales = null;
        setUnitsGiftCertificateSales(0);
        amountTransactionDiscounts = null;
        numberTransactionDiscounts = 0;
        amountItemDiscounts = null;
        numberItemDiscounts = 0;
        // restocking fees
        amountRestockingFees = null;
        unitsRestockingFees = null;
        amountRestockingFeesFromNonTaxableItems = null;
        unitsRestockingFeesFromNonTaxableItems = null;
        // shipping charges
        amountShippingCharges = null;
        numberShippingCharges = 0;
        amountTaxShippingCharges = null;
        amountInclusiveTaxShippingCharges = null;
        amountTransactionDiscStoreCoupons = null;
        numberTransactionDiscStoreCoupons = 0;
        amountItemDiscStoreCoupons = null;
        numberItemDiscStoreCoupons = 0;
        amountPostVoids = null;
        numberPostVoids = 0;
        numberNoSales = 0;
        countHousePayments = 0;
        amountLineVoids = null;
        unitsLineVoids = null;
        amountCancelledTransactions = null;
        numberCancelledTransactions = 0;
        amountGrossTaxableNonMerchandiseSales = null;
        amountGrossNonTaxableNonMerchandiseSales = null;
        amountGrossTaxableNonMerchandiseReturns = null;
        amountGrossNonTaxableNonMerchandiseReturns = null;
        amountGrossTaxableNonMerchandiseSalesVoided = null;
        amountGrossNonTaxableNonMerchandiseSalesVoided = null;
        amountGrossTaxableNonMerchandiseReturnsVoided = null;
        amountGrossNonTaxableNonMerchandiseReturnsVoided = null;
        amountGrossGiftCardItemSales = null;
        amountGrossGiftCardItemReturns = null;
        amountGrossGiftCardItemSalesVoided = null;
        amountGrossGiftCardItemReturnsVoided = null;
        amountGrossGiftCardItemIssued = null;
        amountGrossGiftCardItemReloaded = null;
        amountGrossGiftCardItemRedeemed = null;
        amountGrossGiftCardItemIssueVoided = null;
        amountGrossGiftCardItemReloadVoided = null;
        amountGrossGiftCardItemRedeemedVoided = null;
        amountHousePayments = null;
        amountItemDiscStoreCoupons = null;
        numberItemDiscStoreCoupons = 0;
        amountTillPayIns = null;
        countTillPayIns = 0;
        amountTillPayOuts = null;
        countTillPayOuts = 0;
        amountLayawayPayments = null;
        countLayawayPayments = 0;
        amountLayawayNew = null;
        countLayawayNew = 0;
        amountLayawayPickup = null;
        countLayawayPickup = 0;
        amountLayawayDeletions = null;
        countLayawayDeletions = 0;
        amountLayawayInitiationFees = null;
        countLayawayInitiationFees = 0;
        amountLayawayDeletionFees = null;
        countLayawayDeletionFees = 0;
        amountSpecialOrderNew = null;
        amountSpecialOrderPartial = null;
        amountOrderPayments = null;
        countOrderPayments = 0;
        amountOrderCancels = null;
        countOrderCancels = 0;
        unitsGrossTaxableNonMerchandiseSales = null;
        unitsGrossNonTaxableNonMerchandiseSales = null;
        unitsGrossTaxableNonMerchandiseReturns = null;
        unitsGrossNonTaxableNonMerchandiseReturns = null;
        unitsGrossGiftCardItemSales = null;
        unitsGrossGiftCardItemReturns = null;
        unitsGrossTaxableNonMerchandiseSalesVoided = null;
        unitsGrossNonTaxableNonMerchandiseSalesVoided = null;
        unitsGrossTaxableNonMerchandiseReturnsVoided = null;
        unitsGrossNonTaxableNonMerchandiseReturnsVoided = null;
        unitsGrossGiftCardItemSalesVoided = null;
        unitsGrossGiftCardItemReturnsVoided = null;
        unitsGrossGiftCardItemIssued = null;
        unitsGrossGiftCardItemReloaded = null;
        unitsGrossGiftCardItemRedeemed = null;
        unitsGrossGiftCardItemIssueVoided = null;
        unitsGrossGiftCardItemReloadVoided = null;
        unitsGrossGiftCardItemRedeemedVoided = null;
        startingFloatCount.resetTotals();
        endingFloatCount.resetTotals();
        startingSafeCount.resetTotals();
        endingSafeCount.resetTotals();
        tenderCount.resetTotals();
        combinedCount.resetTotals();
        resetTillPickups();
        resetTillLoans();
        resetTillPayIns();
        resetTillPayOuts();
        amountEmployeeDiscounts = null;
        unitsEmployeeDiscounts = null;
        amountCustomerDiscounts = null;
        unitsCustomerDiscounts = null;
        amountPriceOverrides = null;
        unitsPriceOverrides = null;
        unitsPriceAdjustments = null;
        amountItemMarkdowns = null;
        numberItemMarkdowns = 0;
        amountGrossGiftCertificateIssued = null;
        unitsGrossGiftCertificateIssued = null;
        amountGrossGiftCertificateIssuedVoided = null;
        unitsGrossGiftCertificateIssuedVoided = null;
        amountGrossGiftCertificateTendered = null;
        unitsGrossGiftCertificateTendered = null;
        amountGrossGiftCertificateTenderedVoided = null;
        unitsGrossGiftCertificateTenderedVoided = null;
        houseCardEnrollmentsApproved = 0;
        houseCardEnrollmentsDeclined = 0;
        taxTotalsContainer = null;
        amountGrossGiftCardItemCredit = null;
        unitsGrossGiftCardItemCredit = null;
        amountGrossGiftCardItemCreditVoided = null;
        unitsGrossGiftCardItemCreditVoided = null;
        amountGrossGiftCertificatesRedeemed = null;
        unitsGrossGiftCertificatesRedeemed = null;
        amountGrossGiftCertificatesRedeemedVoided = null;
        unitsGrossGiftCertificatesRedeemedVoided = null;
        amountGrossStoreCreditsIssued = null;
        unitsGrossStoreCreditsIssued = null;
        amountGrossStoreCreditsIssuedVoided = null;
        unitsGrossStoreCreditsIssuedVoided = null;
        amountGrossStoreCreditsRedeemed = null;
        unitsGrossStoreCreditsRedeemed = null;
        amountGrossStoreCreditsRedeemedVoided = null;
        unitsGrossStoreCreditsRedeemedVoided = null;
        amountGrossItemEmployeeDiscount = null;
        unitsGrossItemEmployeeDiscount = null;
        amountGrossItemEmployeeDiscountVoided = null;
        unitsGrossItemEmployeeDiscountVoided = null;
        amountGrossTransactionEmployeeDiscount = null;
        unitsGrossTransactionEmployeeDiscount = null;
        amountGrossTransactionEmployeeDiscountVoided = null;
        unitsGrossTransactionEmployeeDiscountVoided = null;
        amountBillPayments = null;
        countBillPayments = 0;
        amountChangeRoundedIn = null;
        amountChangeRoundedOut = null;
    }

    /**
     * Reset till pickups.
     */
    protected void resetTillPickups()
    {
        countTillPickups.clear();
        tillPickupsList.clear();
    }

    /**
     * Reset till loans.
     */
    protected void resetTillLoans()
    {
        tillLoansList.clear();
        countTillLoans = 0;
    }

    /**
     * Reset till pay-ins.
     */
    protected void resetTillPayIns()
    {
        tillPayInsList.clear();
    }

    /**
     * Reset till loans.
     */
    protected void resetTillPayOuts()
    {
        tillPayOutsList.clear();
    }

    /**
     * Adds a FinancialTotals object to this object and returns result.
     *
     * @param t FinancialTotals object to be added to this object
     * @return resulting FinancialTotals object
     */
    public FinancialTotalsIfc add(FinancialTotalsIfc t)
    {
        addAmountGrossTaxableItemSales(t.getAmountGrossTaxableItemSales());
        addUnitsGrossTaxableItemSales(t.getUnitsGrossTaxableItemSales());
        addAmountGrossNonTaxableItemSales(t.getAmountGrossNonTaxableItemSales());
        addUnitsGrossNonTaxableItemSales(t.getUnitsGrossNonTaxableItemSales());
        addAmountGrossTaxExemptItemSales(t.getAmountGrossTaxExemptItemSales());
        addUnitsGrossTaxExemptItemSales(t.getUnitsGrossTaxExemptItemSales());
        addAmountGrossTaxableItemReturns(t.getAmountGrossTaxableItemReturns());
        addUnitsGrossTaxableItemReturns(t.getUnitsGrossTaxableItemReturns());
        addAmountGrossNonTaxableItemReturns(t.getAmountGrossNonTaxableItemReturns());
        addUnitsGrossNonTaxableItemReturns(t.getUnitsGrossNonTaxableItemReturns());
        addAmountGrossTaxExemptItemReturns(t.getAmountGrossTaxExemptItemReturns());
        addUnitsGrossTaxExemptItemReturns(t.getUnitsGrossTaxExemptItemReturns());
        addAmountGrossTaxableItemSalesVoided(t.getAmountGrossTaxableItemSalesVoided());
        addUnitsGrossTaxableItemSalesVoided(t.getUnitsGrossTaxableItemSalesVoided());
        addAmountGrossNonTaxableItemSalesVoided(t.getAmountGrossNonTaxableItemSalesVoided());
        addUnitsGrossNonTaxableItemSalesVoided(t.getUnitsGrossNonTaxableItemSalesVoided());
        addAmountGrossTaxExemptItemSalesVoided(t.getAmountGrossTaxExemptItemSalesVoided());
        addUnitsGrossTaxExemptItemSalesVoided(t.getUnitsGrossTaxExemptItemSalesVoided());
        addAmountGrossTaxableItemReturnsVoided(t.getAmountGrossTaxableItemReturnsVoided());
        addUnitsGrossTaxableItemReturnsVoided(t.getUnitsGrossTaxableItemReturnsVoided());
        addAmountGrossNonTaxableItemReturnsVoided(t.getAmountGrossNonTaxableItemReturnsVoided());
        addUnitsGrossNonTaxableItemReturnsVoided(t.getUnitsGrossNonTaxableItemReturnsVoided());
        addAmountGrossTaxExemptItemReturnsVoided(t.getAmountGrossTaxExemptItemReturnsVoided());
        addUnitsGrossTaxExemptItemReturnsVoided(t.getUnitsGrossTaxExemptItemReturnsVoided());
        addAmountTaxItemSales(t.getAmountTaxItemSales());
        addAmountInclusiveTaxItemSales(t.getAmountInclusiveTaxItemSales());
        addAmountTaxItemReturns(t.getAmountTaxItemReturns());
        addAmountInclusiveTaxItemReturns(t.getAmountInclusiveTaxItemReturns());
        addAmountTaxTransactionSales(t.getAmountTaxTransactionSales());
        addAmountInclusiveTaxTransactionSales(t.getAmountInclusiveTaxTransactionSales());
        addAmountTaxTransactionReturns(t.getAmountTaxTransactionReturns());
        addAmountInclusiveTaxTransactionReturns(t.getAmountInclusiveTaxTransactionReturns());
        addAmountGrossTaxableTransactionSales(t.getAmountGrossTaxableTransactionSales());
        addCountGrossTaxableTransactionSales(t.getCountGrossTaxableTransactionSales());
        addAmountGrossNonTaxableTransactionSales(t.getAmountGrossNonTaxableTransactionSales());
        addCountGrossNonTaxableTransactionSales(t.getCountGrossNonTaxableTransactionSales());
        addAmountGrossTaxExemptTransactionSales(t.getAmountGrossTaxExemptTransactionSales());
        addCountGrossTaxExemptTransactionSales(t.getCountGrossTaxExemptTransactionSales());
        addAmountGrossTaxableTransactionReturns(t.getAmountGrossTaxableTransactionReturns());
        addCountGrossTaxableTransactionReturns(t.getCountGrossTaxableTransactionReturns());
        addAmountGrossNonTaxableTransactionReturns(t.getAmountGrossNonTaxableTransactionReturns());
        addCountGrossNonTaxableTransactionReturns(t.getCountGrossNonTaxableTransactionReturns());
        addAmountGrossTaxExemptTransactionReturns(t.getAmountGrossTaxExemptTransactionReturns());
        addCountGrossTaxExemptTransactionReturns(t.getCountGrossTaxExemptTransactionReturns());
        addAmountGrossTaxableTransactionSalesVoided(t.getAmountGrossTaxableTransactionSalesVoided());
        addCountGrossTaxableTransactionSalesVoided(t.getCountGrossTaxableTransactionSalesVoided());
        addAmountGrossNonTaxableTransactionSalesVoided(t.getAmountGrossNonTaxableTransactionSalesVoided());
        addCountGrossNonTaxableTransactionSalesVoided(t.getCountGrossNonTaxableTransactionSalesVoided());
        addAmountGrossTaxExemptTransactionSalesVoided(t.getAmountGrossTaxExemptTransactionSalesVoided());
        addCountGrossTaxExemptTransactionSalesVoided(t.getCountGrossTaxExemptTransactionSalesVoided());
        addAmountGrossTaxableTransactionReturnsVoided(t.getAmountGrossTaxableTransactionReturnsVoided());
        addCountGrossTaxableTransactionReturnsVoided(t.getCountGrossTaxableTransactionReturnsVoided());
        addAmountGrossNonTaxableTransactionReturnsVoided(t.getAmountGrossNonTaxableTransactionReturnsVoided());
        addCountGrossNonTaxableTransactionReturnsVoided(t.getCountGrossNonTaxableTransactionReturnsVoided());
        addAmountGrossTaxExemptTransactionReturnsVoided(t.getAmountGrossTaxExemptTransactionReturnsVoided());
        addCountGrossTaxExemptTransactionReturnsVoided(t.getCountGrossTaxExemptTransactionReturnsVoided());
        addAmountGiftCertificateSales(t.getAmountGiftCertificateSales());
        addUnitsGiftCertificateSales(t.getUnitsGiftCertificateSales());
        addAmountHousePayments(t.getAmountHousePayments());
        addCountHousePayments(t.getCountHousePayments());
        addAmountTillPayIns(t.getAmountTillPayIns());
        addCountTillPayIns(t.getCountTillPayIns());
        addAmountTillPayOuts(t.getAmountTillPayOuts());
        addCountTillPayOuts(t.getCountTillPayOuts());
        addAmountTransactionDiscounts(t.getAmountTransactionDiscounts());
        addNumberTransactionDiscounts(t.getNumberTransactionDiscounts());
        addAmountItemDiscounts(t.getAmountItemDiscounts());
        addNumberItemDiscounts(t.getNumberItemDiscounts());
        addAmountItemMarkdowns(t.getAmountItemMarkdowns());
        addNumberItemMarkdowns(t.getNumberItemMarkdowns());
        // add restocking fees
        addAmountRestockingFees(t.getAmountRestockingFees());
        addUnitsRestockingFees(t.getUnitsRestockingFees());
        addAmountRestockingFeesFromNonTaxableItems(t.getAmountRestockingFeesFromNonTaxableItems());
        addUnitsRestockingFeesFromNonTaxableItems(t.getUnitsRestockingFeesFromNonTaxableItems());
        // add shipping charges
        addAmountShippingCharges(t.getAmountShippingCharges());
        addNumberShippingCharges(t.getNumberShippingCharges());
        // add shipping chareges tax
        addAmountTaxShippingCharges(t.getAmountTaxShippingCharges());
        addAmountInclusiveTaxShippingCharges(t.getAmountInclusiveTaxShippingCharges());
        // StoreCouponDiscounts
        addAmountTransactionDiscStoreCoupons(t.getAmountTransactionDiscStoreCoupons());
        addNumberTransactionDiscStoreCoupons(t.getNumberTransactionDiscStoreCoupons());
        addAmountItemDiscStoreCoupons(t.getAmountItemDiscStoreCoupons());
        addNumberItemDiscStoreCoupons(t.getNumberItemDiscStoreCoupons());
        addAmountPostVoids(t.getAmountPostVoids());
        addNumberPostVoids(t.getNumberPostVoids());
        addAmountLineVoids(t.getAmountLineVoids());
        addNumberNoSales(t.getNumberNoSales());
        addUnitsLineVoids(t.getUnitsLineVoids());
        addAmountCancelledTransactions(t.getAmountCancelledTransactions());
        addNumberCancelledTransactions(t.getNumberCancelledTransactions());
        addTransactionCount(t.getTransactionCount());
        addStartingFloatCount(t.getStartingFloatCount());
        addEndingFloatCount(t.getEndingFloatCount());
        addStartingSafeCount(t.getStartingSafeCount());
        addEndingSafeCount(t.getEndingSafeCount());
        addTenderCount(t.getTenderCount());

        Enumeration<String> enumer = t.getCountTillPickupsTable().keys();
        String countryCode = null;

        while (enumer.hasMoreElements())
        {
            countryCode = enumer.nextElement();
            addCountTillPickups(countryCode, t.getCountTillPickups(countryCode));
        }

        addTillPickups(t.getTillPickups());
        addCountTillLoans(t.getCountTillLoans());
        addTillLoans(t.getTillLoans());
        addTillPayIns(t.getTillPayIns());
        addTillPayOuts(t.getTillPayOuts());
        addCombinedCount(t.getCombinedCount());
        addAmountLayawayNew(t.getAmountLayawayNew());
        addCountLayawayNew(t.getCountLayawayNew());        
        addAmountLayawayPayments(t.getAmountLayawayPayments());
        addCountLayawayPayments(t.getCountLayawayPayments());
        addAmountLayawayDeletions(t.getAmountLayawayDeletions());
        addCountLayawayDeletions(t.getCountLayawayDeletions());
        addAmountLayawayInitiationFees(t.getAmountLayawayInitiationFees());
        addCountLayawayInitiationFees(t.getCountLayawayInitiationFees());
        addAmountLayawayDeletionFees(t.getAmountLayawayDeletionFees());
        addCountLayawayDeletionFees(t.getCountLayawayDeletionFees());
        addAmountSpecialOrderNew(t.getAmountSpecialOrderNew());
        addAmountSpecialOrderPartial(t.getAmountSpecialOrderPartial());
        addAmountOrderPayments(t.getAmountOrderPayments());
        addCountOrderPayments(t.getCountOrderPayments());
        addAmountOrderCancels(t.getAmountOrderCancels());
        addCountOrderCancels(t.getCountOrderCancels());
        addAmountGrossTaxableNonMerchandiseSales(t.getAmountGrossTaxableNonMerchandiseSales());
        addAmountGrossNonTaxableNonMerchandiseSales(t.getAmountGrossNonTaxableNonMerchandiseSales());
        addAmountGrossTaxableNonMerchandiseReturns(t.getAmountGrossTaxableNonMerchandiseReturns());
        addAmountGrossNonTaxableNonMerchandiseReturns(t.getAmountGrossNonTaxableNonMerchandiseReturns());
        addAmountGrossTaxableNonMerchandiseSalesVoided(t.getAmountGrossTaxableNonMerchandiseSalesVoided());
        addAmountGrossNonTaxableNonMerchandiseSalesVoided(t.getAmountGrossNonTaxableNonMerchandiseSalesVoided());
        addAmountGrossTaxableNonMerchandiseReturnsVoided(t.getAmountGrossTaxableNonMerchandiseReturnsVoided());
        addAmountGrossNonTaxableNonMerchandiseReturnsVoided(t.getAmountGrossNonTaxableNonMerchandiseReturnsVoided());
        addAmountGrossGiftCardItemSales(t.getAmountGrossGiftCardItemSales());
        addAmountGrossGiftCardItemReturns(t.getAmountGrossGiftCardItemReturns());
        addAmountGrossGiftCardItemSalesVoided(t.getAmountGrossGiftCardItemSalesVoided());
        addAmountGrossGiftCardItemReturnsVoided(t.getAmountGrossGiftCardItemReturnsVoided());
        addAmountGrossGiftCardItemIssued(t.getAmountGrossGiftCardItemIssued());
        addAmountGrossGiftCardItemReloaded(t.getAmountGrossGiftCardItemReloaded());
        addAmountGrossGiftCardItemRedeemed(t.getAmountGrossGiftCardItemRedeemed());
        addAmountGrossGiftCardItemIssueVoided(t.getAmountGrossGiftCardItemIssueVoided());
        addAmountGrossGiftCardItemReloadVoided(t.getAmountGrossGiftCardItemReloadVoided());
        addAmountGrossGiftCardItemRedeemedVoided(t.getAmountGrossGiftCardItemRedeemedVoided());
        addUnitsGrossTaxableNonMerchandiseSales(t.getUnitsGrossTaxableNonMerchandiseSales());
        addUnitsGrossNonTaxableNonMerchandiseSales(t.getUnitsGrossNonTaxableNonMerchandiseSales());
        addUnitsGrossTaxableNonMerchandiseReturns(t.getUnitsGrossTaxableNonMerchandiseReturns());
        addUnitsGrossNonTaxableNonMerchandiseReturns(t.getUnitsGrossNonTaxableNonMerchandiseReturns());
        addUnitsGrossTaxableNonMerchandiseSalesVoided(t.getUnitsGrossTaxableNonMerchandiseSalesVoided());
        addUnitsGrossNonTaxableNonMerchandiseSalesVoided(t.getUnitsGrossNonTaxableNonMerchandiseSalesVoided());
        addUnitsGrossTaxableNonMerchandiseReturnsVoided(t.getUnitsGrossTaxableNonMerchandiseReturnsVoided());
        addUnitsGrossNonTaxableNonMerchandiseReturnsVoided(t.getUnitsGrossNonTaxableNonMerchandiseReturnsVoided());
        addUnitsGrossGiftCardItemSales(t.getUnitsGrossGiftCardItemSales());
        addUnitsGrossGiftCardItemReturns(t.getUnitsGrossGiftCardItemReturns());
        addUnitsGrossGiftCardItemSalesVoided(t.getUnitsGrossGiftCardItemSalesVoided());
        addUnitsGrossGiftCardItemReturnsVoided(t.getUnitsGrossGiftCardItemReturnsVoided());
        addUnitsGrossGiftCardItemIssued(t.getUnitsGrossGiftCardItemIssued());
        addUnitsGrossGiftCardItemReloaded(t.getUnitsGrossGiftCardItemReloaded());
        addUnitsGrossGiftCardItemRedeemed(t.getUnitsGrossGiftCardItemRedeemed());
        addUnitsGrossGiftCardItemIssueVoided(t.getUnitsGrossGiftCardItemIssueVoided());
        addUnitsGrossGiftCardItemReloadVoided(t.getUnitsGrossGiftCardItemReloadVoided());
        addUnitsGrossGiftCardItemRedeemedVoided(t.getUnitsGrossGiftCardItemRedeemedVoided());
        addAmountEmployeeDiscounts(t.getAmountEmployeeDiscounts());
        addUnitsEmployeeDiscounts(t.getUnitsEmployeeDiscounts());
        addAmountCustomerDiscounts(t.getAmountCustomerDiscounts());
        addUnitsCustomerDiscounts(t.getUnitsCustomerDiscounts());
        addAmountPriceOverrides(t.getAmountPriceOverrides());
        addUnitsPriceOverrides(t.getUnitsPriceOverrides());
        addUnitsPriceAdjustments(t.getUnitsPriceAdjustments());
        addAmountGrossGiftCertificateIssued(t.getAmountGrossGiftCertificateIssued());
        addUnitsGrossGiftCertificateIssued(t.getUnitsGrossGiftCertificateIssued());
        addAmountGrossGiftCertificateIssuedVoided(t.getAmountGrossGiftCertificateIssuedVoided());
        addUnitsGrossGiftCertificateIssuedVoided(t.getUnitsGrossGiftCertificateIssuedVoided());
        addAmountGrossGiftCertificateTendered(t.getAmountGrossGiftCertificateTendered());
        addUnitsGrossGiftCertificateTendered(t.getUnitsGrossGiftCertificateTendered());
        addAmountGrossGiftCertificateTenderedVoided(t.getAmountGrossGiftCertificateTenderedVoided());
        addUnitsGrossGiftCertificateTenderedVoided(t.getUnitsGrossGiftCertificateTenderedVoided());

        addTaxes(t.getTaxes());

        addAmountGrossGiftCardItemCredit(t.getAmountGrossGiftCardItemCredit());
        addUnitsGrossGiftCardItemCredit(t.getUnitsGrossGiftCardItemCredit());

        addAmountGrossGiftCardItemCreditVoided(t.getAmountGrossGiftCardItemCreditVoided());
        addUnitsGrossGiftCardItemCreditVoided(t.getUnitsGrossGiftCardItemCreditVoided());

        addAmountGrossGiftCertificatesRedeemed(t.getAmountGrossGiftCertificatesRedeemed());
        addUnitsGrossGiftCertificatesRedeemed(t.getUnitsGrossGiftCertificatesRedeemed());

        addAmountGrossGiftCertificatesRedeemedVoided(t.getAmountGrossGiftCertificatesRedeemedVoided());
        addUnitsGrossGiftCertificatesRedeemedVoided(t.getUnitsGrossGiftCertificatesRedeemedVoided());

        addAmountGrossStoreCreditsIssued(t.getAmountGrossStoreCreditsIssued());
        addUnitsGrossStoreCreditsIssued(t.getUnitsGrossStoreCreditsIssued());

        addAmountGrossStoreCreditsIssuedVoided(t.getAmountGrossStoreCreditsIssuedVoided());
        addUnitsGrossStoreCreditsIssuedVoided(t.getUnitsGrossStoreCreditsIssuedVoided());

        addAmountGrossStoreCreditsRedeemed(t.getAmountGrossStoreCreditsRedeemed());
        addUnitsGrossStoreCreditsRedeemed(t.getUnitsGrossStoreCreditsRedeemed());

        addAmountGrossStoreCreditsRedeemedVoided(t.getAmountGrossStoreCreditsRedeemedVoided());
        addUnitsGrossStoreCreditsRedeemedVoided(t.getUnitsGrossStoreCreditsRedeemedVoided());

        addAmountGrossItemEmployeeDiscount(t.getAmountGrossItemEmployeeDiscount());
        addUnitsGrossItemEmployeeDiscount(t.getUnitsGrossItemEmployeeDiscount());

        addAmountGrossItemEmployeeDiscountVoided(t.getAmountGrossItemEmployeeDiscountVoided());
        addUnitsGrossItemEmployeeDiscountVoided(t.getUnitsGrossItemEmployeeDiscountVoided());

        addAmountGrossTransactionEmployeeDiscount(t.getAmountGrossTransactionEmployeeDiscount());
        addUnitsGrossTransactionEmployeeDiscount(t.getUnitsGrossTransactionEmployeeDiscount());

        addAmountGrossTransactionEmployeeDiscountVoided(t.getAmountGrossTransactionEmployeeDiscountVoided());
        addUnitsGrossTransactionEmployeeDiscountVoided(t.getUnitsGrossTransactionEmployeeDiscountVoided());
        
        addAmountBillPayments(t.getAmountBillPayments());
        addCountBillPayments(t.getCountBillPayments());

        addAmountChangeRoundedIn(t.getAmountChangeRoundedIn());
        addAmountChangeRoundedOut(t.getAmountChangeRoundedOut());

        FinancialTotalsIfc newTotals = (FinancialTotalsIfc)clone();
        return newTotals;
    }

    /**
     * Creates clone of this object.
     *
     * @return Object clone of this object
     */
    public Object clone()
    {
        // instantiate new object
        FinancialTotals c = new FinancialTotals();

        // set clone attributes
        setCloneAttributes(c);

        // pass back Object
        return c;
    }

    /**
     * Sets attributes in clone.
     *
     * @param newClass new instance of class
     */
    protected void setCloneAttributes(FinancialTotals newClass)
    {
        // set values
        newClass.setTransactionCount(transactionCount);
        newClass.setTransactionsWithReturnedItemsCount(transactionsWithReturnedItemsCount);
        newClass.setCurrencyID(currencyID); // I18N
        if (amountGrossTaxableItemSales != null)
        {
            newClass.setAmountGrossTaxableItemSales((CurrencyIfc)amountGrossTaxableItemSales.clone());
        }
        newClass.setUnitsGrossTaxableItemSales(unitsGrossTaxableItemSales);
        if (amountGrossNonTaxableItemSales != null)
        {
            newClass.setAmountGrossNonTaxableItemSales((CurrencyIfc)amountGrossNonTaxableItemSales.clone());
        }
        newClass.setUnitsGrossNonTaxableItemSales(unitsGrossNonTaxableItemSales);
        if (amountGrossTaxExemptItemSales != null)
        {
            newClass.setAmountGrossTaxExemptItemSales((CurrencyIfc)amountGrossTaxExemptItemSales.clone());
        }

        newClass.setUnitsGrossTaxExemptItemSales(unitsGrossTaxExemptItemSales);
        if (amountGrossTaxableItemReturns != null)
        {
            newClass.setAmountGrossTaxableItemReturns((CurrencyIfc)amountGrossTaxableItemReturns.clone());
        }
        newClass.setUnitsGrossTaxableItemReturns(unitsGrossTaxableItemReturns);
        if (amountGrossNonTaxableItemReturns != null)
        {
            newClass.setAmountGrossNonTaxableItemReturns((CurrencyIfc)amountGrossNonTaxableItemReturns.clone());
        }
        newClass.setUnitsGrossNonTaxableItemReturns(unitsGrossNonTaxableItemReturns);
        if (amountGrossTaxExemptItemReturns != null)
        {
            newClass.setAmountGrossTaxExemptItemReturns((CurrencyIfc)amountGrossTaxExemptItemReturns.clone());
        }
        newClass.setUnitsGrossTaxExemptItemReturns(unitsGrossTaxExemptItemReturns);
        if (amountGrossTaxableItemSalesVoided != null)
        {
            newClass.setAmountGrossTaxableItemSalesVoided((CurrencyIfc)amountGrossTaxableItemSalesVoided.clone());
        }
        newClass.setUnitsGrossTaxableItemSalesVoided(unitsGrossTaxableItemSalesVoided);
        if (amountGrossNonTaxableItemSalesVoided != null)
        {
            newClass.setAmountGrossNonTaxableItemSalesVoided((CurrencyIfc)amountGrossNonTaxableItemSalesVoided.clone());
        }
        newClass.setUnitsGrossNonTaxableItemSalesVoided(unitsGrossNonTaxableItemSalesVoided);
        if (amountGrossTaxExemptItemSalesVoided != null)
        {
            newClass.setAmountGrossTaxExemptItemSalesVoided((CurrencyIfc)amountGrossTaxExemptItemSalesVoided.clone());
        }
        newClass.setUnitsGrossTaxExemptItemSalesVoided(unitsGrossTaxExemptItemSalesVoided);
        if (amountGrossTaxableItemReturnsVoided != null)
        {
            newClass.setAmountGrossTaxableItemReturnsVoided((CurrencyIfc)amountGrossTaxableItemReturnsVoided.clone());
        }
        newClass.setUnitsGrossTaxableItemReturnsVoided(unitsGrossTaxableItemReturnsVoided);
        if (amountGrossNonTaxableItemReturnsVoided != null)
        {
            newClass.setAmountGrossNonTaxableItemReturnsVoided((CurrencyIfc)amountGrossNonTaxableItemReturnsVoided
                    .clone());
        }
        newClass.setUnitsGrossNonTaxableItemReturnsVoided(unitsGrossNonTaxableItemReturnsVoided);
        if (amountGrossTaxExemptItemReturnsVoided != null)
        {
            newClass.setAmountGrossTaxExemptItemReturnsVoided((CurrencyIfc)amountGrossTaxExemptItemReturnsVoided
                    .clone());
        }
        newClass.setUnitsGrossTaxExemptItemReturnsVoided(unitsGrossTaxExemptItemReturnsVoided);
        if (amountTaxItemSales != null)
        {
            newClass.setAmountTaxItemSales((CurrencyIfc)amountTaxItemSales.clone());
        }
        if (amountInclusiveTaxItemSales != null)
        {
            newClass.setAmountInclusiveTaxItemSales((CurrencyIfc)amountInclusiveTaxItemSales.clone());
        }
        if (amountTaxItemReturns != null)
        {
            newClass.setAmountTaxItemReturns((CurrencyIfc)amountTaxItemReturns.clone());
        }
        if (amountInclusiveTaxItemReturns != null)
        {
            newClass.setAmountInclusiveTaxItemReturns((CurrencyIfc)amountInclusiveTaxItemReturns.clone());
        }
        if (amountTaxTransactionSales != null)
        {
            newClass.setAmountTaxTransactionSales((CurrencyIfc)amountTaxTransactionSales.clone());
        }
        if (amountInclusiveTaxTransactionSales != null)
        {
            newClass.setAmountInclusiveTaxTransactionSales((CurrencyIfc)amountInclusiveTaxTransactionSales.clone());
        }
        if (amountTaxTransactionReturns != null)
        {
            newClass.setAmountTaxTransactionReturns((CurrencyIfc)amountTaxTransactionReturns.clone());
        }
        if (amountInclusiveTaxTransactionReturns != null)
        {
            newClass.setAmountInclusiveTaxTransactionReturns((CurrencyIfc)amountInclusiveTaxTransactionReturns.clone());
        }
        if (amountGrossTaxableTransactionSales != null)
        {
            newClass.setAmountGrossTaxableTransactionSales((CurrencyIfc)amountGrossTaxableTransactionSales.clone());
        }
        newClass.setCountGrossTaxableTransactionSales(countGrossTaxableTransactionSales);
        if (amountGrossNonTaxableTransactionSales != null)
        {
            newClass.setAmountGrossNonTaxableTransactionSales((CurrencyIfc)amountGrossNonTaxableTransactionSales
                    .clone());
        }
        newClass.setCountGrossNonTaxableTransactionSales(countGrossNonTaxableTransactionSales);
        if (amountGrossTaxExemptTransactionSales != null)
        {
            newClass.setAmountGrossTaxExemptTransactionSales((CurrencyIfc)amountGrossTaxExemptTransactionSales.clone());
        }
        newClass.setCountGrossTaxExemptTransactionSales(countGrossTaxExemptTransactionSales);
        if (amountGrossTaxableTransactionReturns != null)
        {
            newClass.setAmountGrossTaxableTransactionReturns((CurrencyIfc)amountGrossTaxableTransactionReturns.clone());
        }
        newClass.setCountGrossTaxableTransactionReturns(countGrossTaxableTransactionReturns);
        if (amountGrossNonTaxableTransactionReturns != null)
        {
            newClass.setAmountGrossNonTaxableTransactionReturns((CurrencyIfc)amountGrossNonTaxableTransactionReturns
                    .clone());
        }
        newClass.setCountGrossNonTaxableTransactionReturns(countGrossNonTaxableTransactionReturns);
        if (amountGrossTaxExemptTransactionReturns != null)
        {
            newClass.setAmountGrossTaxExemptTransactionReturns((CurrencyIfc)amountGrossTaxExemptTransactionReturns
                    .clone());
        }
        newClass.setCountGrossTaxExemptTransactionReturns(countGrossTaxExemptTransactionReturns);
        if (amountGrossTaxableTransactionSalesVoided != null)
        {
            newClass.setAmountGrossTaxableTransactionSalesVoided((CurrencyIfc)amountGrossTaxableTransactionSalesVoided
                    .clone());
        }
        newClass.setCountGrossTaxableTransactionSalesVoided(countGrossTaxableTransactionSalesVoided);
        if (amountGrossNonTaxableTransactionSalesVoided != null)
        {
            newClass
                    .setAmountGrossNonTaxableTransactionSalesVoided((CurrencyIfc)amountGrossNonTaxableTransactionSalesVoided
                            .clone());
        }
        newClass.setCountGrossNonTaxableTransactionSalesVoided(countGrossNonTaxableTransactionSalesVoided);
        if (amountGrossTaxExemptTransactionSalesVoided != null)
        {
            newClass
                    .setAmountGrossTaxExemptTransactionSalesVoided((CurrencyIfc)amountGrossTaxExemptTransactionSalesVoided
                            .clone());
        }
        newClass.setCountGrossTaxExemptTransactionSalesVoided(countGrossTaxExemptTransactionSalesVoided);
        if (amountGrossTaxableTransactionReturnsVoided != null)
        {
            newClass
                    .setAmountGrossTaxableTransactionReturnsVoided((CurrencyIfc)amountGrossTaxableTransactionReturnsVoided
                            .clone());
        }
        newClass.setCountGrossTaxableTransactionReturnsVoided(countGrossTaxableTransactionReturnsVoided);
        if (amountGrossNonTaxableTransactionReturnsVoided != null)
        {
            newClass
                    .setAmountGrossNonTaxableTransactionReturnsVoided((CurrencyIfc)amountGrossNonTaxableTransactionReturnsVoided
                            .clone());
        }
        newClass.setCountGrossNonTaxableTransactionReturnsVoided(countGrossNonTaxableTransactionReturnsVoided);
        if (amountGrossTaxExemptTransactionReturnsVoided != null)
        {
            newClass
                    .setAmountGrossTaxExemptTransactionReturnsVoided((CurrencyIfc)amountGrossTaxExemptTransactionReturnsVoided
                            .clone());
        }
        newClass.setCountGrossTaxExemptTransactionReturnsVoided(countGrossTaxExemptTransactionReturnsVoided);
        if (amountHousePayments != null)
        {
            newClass.setAmountHousePayments((CurrencyIfc)amountHousePayments.clone());
        }
        newClass.setCountHousePayments(countHousePayments);
        if (amountTillPayIns != null)
        {
            newClass.setAmountTillPayIns((CurrencyIfc)amountTillPayIns.clone());
        }
        newClass.setCountTillPayIns(countTillPayIns);
        if (amountTillPayOuts != null)
        {
            newClass.setAmountTillPayOuts((CurrencyIfc)amountTillPayOuts.clone());
        }
        newClass.setCountTillPayOuts(countTillPayOuts);
        if (amountGiftCertificateSales != null)
        {
            newClass.setAmountGiftCertificateSales((CurrencyIfc)amountGiftCertificateSales.clone());
        }
        newClass.setUnitsGiftCertificateSales(unitsGiftCertificateSales);
        if (amountTransactionDiscounts != null)
        {
            newClass.setAmountTransactionDiscounts((CurrencyIfc)amountTransactionDiscounts.clone());
        }
        newClass.setNumberTransactionDiscounts(numberTransactionDiscounts);
        if (amountItemDiscounts != null)
        {
            newClass.setAmountItemDiscounts((CurrencyIfc)amountItemDiscounts.clone());
        }
        newClass.setNumberItemDiscounts(numberItemDiscounts);
        if (amountItemMarkdowns != null)
        {
            newClass.setAmountItemMarkdowns((CurrencyIfc)amountItemMarkdowns.clone());
        }
        newClass.setNumberItemMarkdowns(numberItemMarkdowns);
        if (amountPostVoids != null)
        {
            newClass.setAmountPostVoids((CurrencyIfc)amountPostVoids.clone());
        }
        newClass.setNumberPostVoids(numberPostVoids);
        if (amountLineVoids != null)
        {
            newClass.setAmountLineVoids((CurrencyIfc)amountLineVoids.clone());
        }
        newClass.setUnitsLineVoids(unitsLineVoids);
        if (amountCancelledTransactions != null)
        {
            newClass.setAmountCancelledTransactions((CurrencyIfc)amountCancelledTransactions.clone());
        }
        newClass.setNumberCancelledTransactions(numberCancelledTransactions);
        newClass.setFloatCount((ReconcilableCountIfc)floatCount.clone());
        newClass.setStartingFloatCount((ReconcilableCountIfc)startingFloatCount.clone());
        newClass.setEndingFloatCount((ReconcilableCountIfc)endingFloatCount.clone());
        newClass.setStartingSafeCount((ReconcilableCountIfc)startingSafeCount.clone());
        newClass.setEndingSafeCount((ReconcilableCountIfc)endingSafeCount.clone());
        newClass.setTenderCount((FinancialCountIfc)tenderCount.clone());
        newClass.setCombinedCount((ReconcilableCountIfc)combinedCount.clone());
        newClass.setNumberNoSales(numberNoSales); // NoSales

        if (amountGrossTaxableNonMerchandiseSales != null)
        {
            newClass.setAmountGrossTaxableNonMerchandiseSales((CurrencyIfc)amountGrossTaxableNonMerchandiseSales
                    .clone());
        }
        if (unitsGrossTaxableNonMerchandiseSales != null)
        {
            newClass.setUnitsGrossTaxableNonMerchandiseSales(unitsGrossTaxableNonMerchandiseSales);
        }
        if (amountGrossNonTaxableNonMerchandiseSales != null)
        {
            newClass.setAmountGrossNonTaxableNonMerchandiseSales((CurrencyIfc)amountGrossNonTaxableNonMerchandiseSales
                    .clone());
        }
        if (unitsGrossNonTaxableNonMerchandiseSales != null)
        {
            newClass.setUnitsGrossNonTaxableNonMerchandiseSales(unitsGrossNonTaxableNonMerchandiseSales);
        }
        if (amountGrossTaxableNonMerchandiseReturns != null)
        {
            newClass.setAmountGrossTaxableNonMerchandiseReturns((CurrencyIfc)amountGrossTaxableNonMerchandiseReturns
                    .clone());
        }
        if (unitsGrossTaxableNonMerchandiseReturns != null)
        {
            newClass.setUnitsGrossTaxableNonMerchandiseReturns(unitsGrossTaxableNonMerchandiseReturns);
        }
        if (amountGrossNonTaxableNonMerchandiseReturns != null)
        {
            newClass
                    .setAmountGrossNonTaxableNonMerchandiseReturns((CurrencyIfc)amountGrossNonTaxableNonMerchandiseReturns
                            .clone());
        }
        if (unitsGrossNonTaxableNonMerchandiseReturns != null)
        {
            newClass.setUnitsGrossNonTaxableNonMerchandiseReturns(unitsGrossNonTaxableNonMerchandiseReturns);
        }
        if (amountGrossTaxableNonMerchandiseSalesVoided != null)
        {
            newClass
                    .setAmountGrossTaxableNonMerchandiseSalesVoided((CurrencyIfc)amountGrossTaxableNonMerchandiseSalesVoided
                            .clone());
        }
        newClass.setUnitsGrossTaxableNonMerchandiseSalesVoided(unitsGrossTaxableNonMerchandiseSalesVoided);
        if (amountGrossNonTaxableNonMerchandiseSalesVoided != null)
        {
            newClass
                    .setAmountGrossNonTaxableNonMerchandiseSalesVoided((CurrencyIfc)amountGrossNonTaxableNonMerchandiseSalesVoided
                            .clone());
        }
        newClass.setUnitsGrossNonTaxableNonMerchandiseSalesVoided(unitsGrossNonTaxableNonMerchandiseSalesVoided);
        if (amountGrossTaxableNonMerchandiseReturnsVoided != null)
        {
            newClass
                    .setAmountGrossTaxableNonMerchandiseReturnsVoided((CurrencyIfc)amountGrossTaxableNonMerchandiseReturnsVoided
                            .clone());
        }
        newClass.setUnitsGrossTaxableNonMerchandiseReturnsVoided(unitsGrossTaxableNonMerchandiseReturnsVoided);
        if (amountGrossNonTaxableNonMerchandiseReturnsVoided != null)
        {
            newClass
                    .setAmountGrossNonTaxableNonMerchandiseReturnsVoided((CurrencyIfc)amountGrossNonTaxableNonMerchandiseReturnsVoided
                            .clone());
        }
        newClass.setUnitsGrossNonTaxableNonMerchandiseReturnsVoided(unitsGrossNonTaxableNonMerchandiseReturnsVoided);
        if (amountGrossGiftCardItemSales != null)
        {
            newClass.setAmountGrossGiftCardItemSales((CurrencyIfc)amountGrossGiftCardItemSales.clone());
        }
        if (unitsGrossGiftCardItemSales != null)
        {
            newClass.setUnitsGrossGiftCardItemSales(unitsGrossGiftCardItemSales);
        }
        if (amountGrossGiftCardItemReturns != null)
        {
            newClass.setAmountGrossGiftCardItemReturns((CurrencyIfc)amountGrossGiftCardItemReturns.clone());
        }
        if (unitsGrossGiftCardItemReturns != null)
        {
            newClass.setUnitsGrossGiftCardItemReturns(unitsGrossGiftCardItemReturns);
        }
        if (amountGrossGiftCardItemSalesVoided != null)
        {
            newClass.setAmountGrossGiftCardItemSalesVoided((CurrencyIfc)amountGrossGiftCardItemSalesVoided.clone());
        }
        newClass.setUnitsGrossGiftCardItemSalesVoided(unitsGrossGiftCardItemSalesVoided);
        if (amountGrossGiftCardItemReturnsVoided != null)
        {
            newClass.setAmountGrossGiftCardItemReturnsVoided((CurrencyIfc)amountGrossGiftCardItemReturnsVoided.clone());
        }
        newClass.setUnitsGrossGiftCardItemReturnsVoided(unitsGrossGiftCardItemReturnsVoided);
        // StoreCouponDiscounts
        if (amountTransactionDiscStoreCoupons != null)
        {
            newClass.setAmountTransactionDiscStoreCoupons((CurrencyIfc)amountTransactionDiscStoreCoupons.clone());
        }
        newClass.setNumberTransactionDiscStoreCoupons(numberTransactionDiscStoreCoupons);

        if (amountItemDiscStoreCoupons != null)
        {
            newClass.setAmountItemDiscStoreCoupons((CurrencyIfc)amountItemDiscStoreCoupons.clone());
        }
        newClass.setNumberItemDiscStoreCoupons(numberItemDiscStoreCoupons);

        if (countTillPickups != null)
        {
            Enumeration<String> enumer = countTillPickups.keys();
            String countryCode = null;
            Integer count = null;

            while (enumer.hasMoreElements())
            {
                countryCode = enumer.nextElement();
                count = countTillPickups.get(countryCode);
                newClass.addCountTillPickups(countryCode, count.intValue());
            }
        }

        // clone till pickups
        ReconcilableCountIfc[] t = getTillPickups();
        if (t != null)
        {
            ReconcilableCountIfc[] tclone = new ReconcilableCountIfc[t.length];
            for (int i = 0; i < t.length; i++)
            {
                tclone[i] = (ReconcilableCountIfc)t[i].clone();
            }
            newClass.setTillPickups(tclone);
        }

        newClass.setCountTillLoans(getCountTillLoans());
        // clone till loans
        t = getTillLoans();
        if (t != null)
        {
            ReconcilableCountIfc[] tclone = new ReconcilableCountIfc[t.length];
            for (int i = 0; i < t.length; i++)
            {
                tclone[i] = (ReconcilableCountIfc)t[i].clone();
            }
            newClass.setTillLoans(tclone);
        }
        // clone till pay-ins
        t = getTillPayIns();
        if (t != null)
        {
            ReconcilableCountIfc[] tclone = new ReconcilableCountIfc[t.length];
            for (int i = 0; i < t.length; i++)
            {
                tclone[i] = (ReconcilableCountIfc)t[i].clone();
            }
            newClass.setTillPayIns(tclone);
        }
        // clone till Payouts
        t = getTillPayOuts();
        if (t != null)
        {
            ReconcilableCountIfc[] tclone = new ReconcilableCountIfc[t.length];
            for (int i = 0; i < t.length; i++)
            {
                tclone[i] = (ReconcilableCountIfc)t[i].clone();
            }
            newClass.setTillPayOuts(tclone);
        }

        // clone restocking fees
        if (amountRestockingFees != null)
        {
            newClass.setAmountRestockingFees((CurrencyIfc)amountRestockingFees.clone());
        }
        newClass.setUnitsRestockingFees(unitsRestockingFees);

        // clone restocking fees
        if (amountRestockingFeesFromNonTaxableItems != null)
        {
            newClass.setAmountRestockingFeesFromNonTaxableItems((CurrencyIfc)amountRestockingFeesFromNonTaxableItems
                    .clone());
        }
        newClass.setUnitsRestockingFeesFromNonTaxableItems(unitsRestockingFeesFromNonTaxableItems);

        // clone shipping fees
        if (amountShippingCharges != null)
        {
            newClass.setAmountShippingCharges((CurrencyIfc)amountShippingCharges.clone());
        }
        newClass.setNumberShippingCharges(numberShippingCharges);

        // clone shipping charges tax
        if (amountTaxShippingCharges != null)
        {
            newClass.setAmountTaxShippingCharges((CurrencyIfc)amountTaxShippingCharges.clone());
        }
        if (amountInclusiveTaxShippingCharges != null)
        {
            newClass.setAmountInclusiveTaxShippingCharges((CurrencyIfc)amountInclusiveTaxShippingCharges.clone());
        }
        // clone layaway payments
        if (amountLayawayPayments != null)
        {
            newClass.setAmountLayawayPayments((CurrencyIfc)amountLayawayPayments.clone());
        }
        newClass.setCountLayawayPayments(countLayawayPayments);

        // clone layaway payments
        if (amountLayawayNew != null)
        {
            newClass.setAmountLayawayNew((CurrencyIfc)amountLayawayNew.clone());
        }
        newClass.setCountLayawayNew(countLayawayNew);

        // clone layaway payments
        if (amountLayawayPickup != null)
        {
            newClass.setAmountLayawayPickup((CurrencyIfc)amountLayawayPickup.clone());
        }
        newClass.setCountLayawayPickup(countLayawayPickup);

        // clone layaway deletions
        if (amountLayawayDeletions != null)
        {
            newClass.setAmountLayawayDeletions((CurrencyIfc)amountLayawayDeletions.clone());
        }
        newClass.setCountLayawayDeletions(countLayawayDeletions);
        // clone layaway initiation fees
        if (amountLayawayInitiationFees != null)
        {
            newClass.setAmountLayawayInitiationFees((CurrencyIfc)amountLayawayInitiationFees.clone());
        }
        newClass.setCountLayawayInitiationFees(countLayawayInitiationFees);
        // clone layaway deletion fees
        if (amountLayawayDeletionFees != null)
        {
            newClass.setAmountLayawayDeletionFees((CurrencyIfc)amountLayawayDeletionFees.clone());
        }
        newClass.setCountLayawayDeletionFees(countLayawayDeletionFees);

        // clone Special Order New
        if (amountSpecialOrderNew != null)
        {
            newClass.setAmountSpecialOrderNew((CurrencyIfc)amountSpecialOrderNew.clone());
        }
        // clone Special Order Partial
        if (amountSpecialOrderPartial != null)
        {
            newClass.setAmountSpecialOrderPartial((CurrencyIfc)amountSpecialOrderPartial.clone());
        }
        // clone order payments
        if (amountOrderPayments != null)
        {
            newClass.setAmountOrderPayments((CurrencyIfc)getAmountOrderPayments().clone());
        }
        newClass.setCountOrderPayments(getCountOrderPayments());
        // clone order cancels
        if (amountOrderCancels != null)
        {
            newClass.setAmountOrderCancels((CurrencyIfc)getAmountOrderCancels().clone());
        }
        newClass.setCountOrderCancels(getCountOrderCancels());
        if (amountEmployeeDiscounts != null)
        {
            newClass.setAmountEmployeeDiscounts((CurrencyIfc)amountEmployeeDiscounts.clone());
        }
        if (unitsEmployeeDiscounts != null)
        {
            newClass.setUnitsEmployeeDiscounts(unitsEmployeeDiscounts);
        }
        if (amountCustomerDiscounts != null)
        {
            newClass.setAmountCustomerDiscounts((CurrencyIfc)amountCustomerDiscounts.clone());
        }
        if (unitsCustomerDiscounts != null)
        {
            newClass.setUnitsCustomerDiscounts(unitsCustomerDiscounts);
        }
        if (amountPriceOverrides != null)
        {
            newClass.setAmountPriceOverrides((CurrencyIfc)amountPriceOverrides.clone());
        }
        if (unitsPriceOverrides != null)
        {
            newClass.setUnitsPriceOverrides(unitsPriceOverrides);
        }
        if (unitsPriceAdjustments != null)
        {
            newClass.setUnitsPriceAdjustments(unitsPriceAdjustments);
        }

        // gift certificate

        if (amountGrossGiftCertificateIssued != null)
        {
            newClass.setAmountGrossGiftCertificateIssued(amountGrossGiftCertificateIssued);
        }
        if (unitsGrossGiftCertificateIssued != null)
        {
            newClass.setUnitsGrossGiftCertificateIssued(unitsGrossGiftCertificateIssued);
        }
        if (amountGrossGiftCertificateIssuedVoided != null)
        {
            newClass.setAmountGrossGiftCertificateIssuedVoided(amountGrossGiftCertificateIssuedVoided);
        }
        if (unitsGrossGiftCertificateIssuedVoided != null)
        {
            newClass.setUnitsGrossGiftCertificateIssuedVoided(unitsGrossGiftCertificateIssuedVoided);
        }
        if (amountGrossGiftCertificateTendered != null)
        {
            newClass.setAmountGrossGiftCertificateTendered(amountGrossGiftCertificateTendered);
        }
        if (unitsGrossGiftCertificateTendered != null)
        {
            newClass.setUnitsGrossGiftCertificateTendered(unitsGrossGiftCertificateTendered);
        }
        if (amountGrossGiftCertificateTenderedVoided != null)
        {
            newClass.setAmountGrossGiftCertificateTenderedVoided(amountGrossGiftCertificateTenderedVoided);
        }
        if (unitsGrossGiftCertificateTenderedVoided != null)
        {
            newClass.setUnitsGrossGiftCertificateTenderedVoided(unitsGrossGiftCertificateTenderedVoided);
        }
        newClass.setHouseCardEnrollmentsApproved(houseCardEnrollmentsApproved);
        newClass.setHouseCardEnrollmentsDeclined(houseCardEnrollmentsDeclined);

        // gift card
        if (amountGrossGiftCardItemIssued != null)
        {
            newClass.setAmountGrossGiftCardItemIssued(amountGrossGiftCardItemIssued);
        }
        if (unitsGrossGiftCardItemIssued != null)
        {
            newClass.setUnitsGrossGiftCardItemIssued(unitsGrossGiftCardItemIssued);
        }
        if (amountGrossGiftCardItemReloaded != null)
        {
            newClass.setAmountGrossGiftCardItemReloaded(amountGrossGiftCardItemReloaded);
        }
        if (unitsGrossGiftCardItemReloaded != null)
        {
            newClass.setUnitsGrossGiftCardItemReloaded(unitsGrossGiftCardItemReloaded);
        }
        if (amountGrossGiftCardItemRedeemed != null)
        {
            newClass.setAmountGrossGiftCardItemRedeemed(amountGrossGiftCardItemRedeemed);
        }
        if (unitsGrossGiftCardItemRedeemed != null)
        {
            newClass.setUnitsGrossGiftCardItemRedeemed(unitsGrossGiftCardItemRedeemed);
        }
        // gift card
        if (amountGrossGiftCardItemIssueVoided != null)
        {
            newClass.setAmountGrossGiftCardItemIssueVoided(amountGrossGiftCardItemIssueVoided);
        }
        if (unitsGrossGiftCardItemIssueVoided != null)
        {
            newClass.setUnitsGrossGiftCardItemIssueVoided(unitsGrossGiftCardItemIssueVoided);
        }
        if (amountGrossGiftCardItemReloadVoided != null)
        {
            newClass.setAmountGrossGiftCardItemReloadVoided(amountGrossGiftCardItemReloadVoided);
        }
        if (unitsGrossGiftCardItemReloadVoided != null)
        {
            newClass.setUnitsGrossGiftCardItemReloadVoided(unitsGrossGiftCardItemReloadVoided);
        }
        if (amountGrossGiftCardItemRedeemedVoided != null)
        {
            newClass.setAmountGrossGiftCardItemRedeemedVoided(amountGrossGiftCardItemRedeemedVoided);
        }
        if (unitsGrossGiftCardItemRedeemedVoided != null)
        {
            newClass.setUnitsGrossGiftCardItemRedeemedVoided(unitsGrossGiftCardItemRedeemedVoided);
        }
        if (taxTotalsContainer != null)
        {
            newClass.setTaxes(taxTotalsContainer);
        }
        if (amountGrossGiftCardItemCredit != null)
        {
            newClass.setAmountGrossGiftCardItemCredit(amountGrossGiftCardItemCredit);
        }
        newClass.setUnitsGrossGiftCardItemCredit(unitsGrossGiftCardItemCredit);
        if (amountGrossGiftCardItemCreditVoided != null)
        {
            newClass.setAmountGrossGiftCardItemCreditVoided(amountGrossGiftCardItemCreditVoided);
        }
        newClass.setUnitsGrossGiftCardItemCreditVoided(unitsGrossGiftCardItemCreditVoided);
        if (amountGrossGiftCertificatesRedeemed != null)
        {
            newClass.setAmountGrossGiftCertificatesRedeemed(amountGrossGiftCertificatesRedeemed);
        }
        newClass.setUnitsGrossGiftCertificatesRedeemed(unitsGrossGiftCertificatesRedeemed);
        if (amountGrossGiftCertificatesRedeemedVoided != null)
        {
            newClass.setAmountGrossGiftCertificatesRedeemedVoided(amountGrossGiftCertificatesRedeemedVoided);
        }
        newClass.setUnitsGrossGiftCertificatesRedeemedVoided(unitsGrossGiftCertificatesRedeemedVoided);
        if (amountGrossStoreCreditsIssued != null)
        {
            newClass.setAmountGrossStoreCreditsIssued(amountGrossStoreCreditsIssued);
        }
        newClass.setUnitsGrossStoreCreditsIssued(unitsGrossStoreCreditsIssued);
        if (amountGrossStoreCreditsIssuedVoided != null)
        {
            newClass.setAmountGrossStoreCreditsIssuedVoided(amountGrossStoreCreditsIssuedVoided);
        }
        newClass.setUnitsGrossStoreCreditsIssuedVoided(unitsGrossStoreCreditsIssuedVoided);
        if (amountGrossStoreCreditsRedeemed != null)
        {
            newClass.setAmountGrossStoreCreditsRedeemed(amountGrossStoreCreditsRedeemed);
        }
        newClass.setUnitsGrossStoreCreditsRedeemed(unitsGrossStoreCreditsRedeemed);
        if (amountGrossStoreCreditsRedeemedVoided != null)
        {
            newClass.setAmountGrossStoreCreditsRedeemedVoided(amountGrossStoreCreditsRedeemedVoided);
        }
        newClass.setUnitsGrossStoreCreditsRedeemedVoided(unitsGrossStoreCreditsRedeemedVoided);
        if (amountGrossItemEmployeeDiscount != null)
        {
            newClass.setAmountGrossItemEmployeeDiscount(amountGrossItemEmployeeDiscount);
        }
        newClass.setUnitsGrossItemEmployeeDiscount(unitsGrossItemEmployeeDiscount);
        if (amountGrossItemEmployeeDiscountVoided != null)
        {
            newClass.setAmountGrossItemEmployeeDiscountVoided(amountGrossItemEmployeeDiscountVoided);
        }
        newClass.setUnitsGrossItemEmployeeDiscountVoided(unitsGrossItemEmployeeDiscountVoided);
        if (amountGrossTransactionEmployeeDiscount != null)
        {
            newClass.setAmountGrossTransactionEmployeeDiscount(amountGrossTransactionEmployeeDiscount);
        }
        newClass.setUnitsGrossTransactionEmployeeDiscount(unitsGrossTransactionEmployeeDiscount);
        if (amountGrossTransactionEmployeeDiscountVoided != null)
        {
            newClass.setAmountGrossTransactionEmployeeDiscountVoided(amountGrossTransactionEmployeeDiscountVoided);
        }
        newClass.setUnitsGrossTransactionEmployeeDiscountVoided(unitsGrossTransactionEmployeeDiscountVoided);
        
        //clone bill payments
        if(amountBillPayments != null)
        {
            newClass.setAmountBillPayments(amountBillPayments);
        }
        
        if (amountChangeRoundedIn != null)
        {
            newClass.setAmountChangeRoundedIn(amountChangeRoundedIn);
        }
        if (amountChangeRoundedOut != null)
        {
            newClass.setAmountChangeRoundedOut(amountChangeRoundedOut);
        }

        newClass.setCountBillPayments(countBillPayments);
    }

    /**
     * Instantiates financial count class. This is isolated so that the actual
     * implementation of FinancialCountIfc can be overridden easily.
     *
     * @return FinancialCountIfc object
     */
    public FinancialCountIfc instantiateFinancialCountIfc()
    {
        // instantiate base financial count class
        return new FinancialCount();
    }

    /**
     * Instantiates reconcilable count class. This is isolated so that the
     * actual implementation of ReconcilableCountIfc can be overridden easily.
     *
     * @return ReconcilableCountIfc object
     */
    public ReconcilableCountIfc instantiateReconcilableCountIfc()
    {
        // instantiate base reconcilable count class
        return new ReconcilableCount();
    }

    /**
     * Determine if two objects are identical.
     *
     * @param obj object to compare with
     * @return true if the objects are identical, false otherwise
     */
    public boolean equals(Object obj)
    {
        // quick exit
        if (this == obj)
            return true;

        boolean isEqual = false;

        if (obj instanceof FinancialTotals)
        {
            FinancialTotals c = (FinancialTotals)obj;
            // downcast the input object
            // compare all the attributes of FinancialTotals
            if (getTransactionCount() == c.getTransactionCount()
                    && getTransactionsWithReturnedItemsCount() == c.getTransactionsWithReturnedItemsCount()
                    && getCountGrossTaxableTransactionSales() == c.getCountGrossTaxableTransactionSales()
                    && getCountGrossNonTaxableTransactionSales() == c.getCountGrossNonTaxableTransactionSales()
                    && getCountGrossTaxExemptTransactionSales() == c.getCountGrossTaxExemptTransactionSales()
                    && getCountGrossTaxableTransactionReturns() == c.getCountGrossTaxableTransactionReturns()
                    && getCountGrossNonTaxableTransactionReturns() == c.getCountGrossNonTaxableTransactionReturns()
                    && getCountGrossTaxExemptTransactionReturns() == c.getCountGrossTaxExemptTransactionReturns()
                    && getCountGrossTaxableTransactionSalesVoided() == c.getCountGrossTaxableTransactionSalesVoided()
                    && getCountGrossNonTaxableTransactionSalesVoided() == c
                            .getCountGrossNonTaxableTransactionSalesVoided()
                    && getCountGrossTaxExemptTransactionSalesVoided() == c
                            .getCountGrossTaxExemptTransactionSalesVoided()
                    && getCountGrossTaxableTransactionReturnsVoided() == c
                            .getCountGrossTaxableTransactionReturnsVoided()
                    && getCountGrossNonTaxableTransactionReturnsVoided() == c
                            .getCountGrossNonTaxableTransactionReturnsVoided()
                    && getCountGrossTaxExemptTransactionReturnsVoided() == c
                            .getCountGrossTaxExemptTransactionReturnsVoided()
                    && getCountHousePayments() == c.getCountHousePayments()
                    && getCountTillPayIns() == c.getCountTillPayIns()
                    && getCountTillPayOuts() == c.getCountTillPayOuts()
                    && getUnitsGiftCertificateSales() == c.getUnitsGiftCertificateSales()
                    && getNumberTransactionDiscounts() == c.getNumberTransactionDiscounts()
                    && getNumberItemMarkdowns() == c.getNumberItemMarkdowns()
                    && getNumberItemDiscounts() == c.getNumberItemDiscounts()
                    && // StoreCouponDiscounts
                    getNumberTransactionDiscStoreCoupons() == c.getNumberTransactionDiscStoreCoupons()
                    && getNumberItemDiscStoreCoupons() == c.getNumberItemDiscStoreCoupons()
                    && getNumberPostVoids() == c.getNumberPostVoids()
                    && getNumberCancelledTransactions() == c.getNumberCancelledTransactions()
                    && getNumberNoSales() == c.getNumberNoSales()
                    && Util.isObjectEqual(getUnitsGrossTaxableItemSalesVoided(), c
                            .getUnitsGrossTaxableItemSalesVoided())
                    && Util.isObjectEqual(getUnitsGrossNonTaxableItemSalesVoided(), c
                            .getUnitsGrossNonTaxableItemSalesVoided())
                    && Util.isObjectEqual(getUnitsGrossTaxExemptItemSalesVoided(), c
                            .getUnitsGrossTaxExemptItemSalesVoided())
                    && Util.isObjectEqual(getUnitsGrossTaxableItemReturnsVoided(), c
                            .getUnitsGrossTaxableItemReturnsVoided())
                    && Util.isObjectEqual(getUnitsGrossNonTaxableItemReturnsVoided(), c
                            .getUnitsGrossNonTaxableItemReturnsVoided())
                    && Util.isObjectEqual(getUnitsGrossTaxExemptItemReturnsVoided(), c
                            .getUnitsGrossTaxExemptItemReturnsVoided())
                    && Util.isObjectEqual(getUnitsGrossTaxableNonMerchandiseSalesVoided(), c
                            .getUnitsGrossTaxableNonMerchandiseSalesVoided())
                    && Util.isObjectEqual(getUnitsGrossNonTaxableNonMerchandiseSalesVoided(), c
                            .getUnitsGrossNonTaxableNonMerchandiseSalesVoided())
                    && Util.isObjectEqual(getUnitsGrossTaxableNonMerchandiseReturnsVoided(), c
                            .getUnitsGrossTaxableNonMerchandiseReturnsVoided())
                    && Util.isObjectEqual(getUnitsGrossNonTaxableNonMerchandiseReturnsVoided(), c
                            .getUnitsGrossNonTaxableNonMerchandiseReturnsVoided())
                    && Util.isObjectEqual(getUnitsGrossGiftCardItemSalesVoided(), c
                            .getUnitsGrossGiftCardItemSalesVoided())
                    && Util.isObjectEqual(getUnitsGrossGiftCardItemReturnsVoided(), c
                            .getUnitsGrossGiftCardItemReturnsVoided())
                    && Util.isObjectEqual(getAmountGrossTaxableItemSales(), c.getAmountGrossTaxableItemSales())
                    && Util.isObjectEqual(getUnitsGrossTaxableItemSales(), c.getUnitsGrossTaxableItemSales())
                    && Util.isObjectEqual(getAmountGrossNonTaxableItemSales(), c.getAmountGrossNonTaxableItemSales())
                    && Util.isObjectEqual(getUnitsGrossNonTaxableItemSales(), c.getUnitsGrossNonTaxableItemSales())
                    && Util.isObjectEqual(getAmountGrossTaxExemptItemSales(), c.getAmountGrossTaxExemptItemSales())
                    && Util.isObjectEqual(getUnitsGrossTaxExemptItemSales(), c.getUnitsGrossTaxExemptItemSales())
                    && Util.isObjectEqual(getAmountGrossTaxableItemReturns(), c.getAmountGrossTaxableItemReturns())
                    && Util.isObjectEqual(getUnitsGrossTaxableItemReturns(), c.getUnitsGrossTaxableItemReturns())
                    && Util.isObjectEqual(getAmountGrossNonTaxableItemReturns(), c
                            .getAmountGrossNonTaxableItemReturns())
                    && Util.isObjectEqual(getUnitsGrossNonTaxableItemReturns(), c.getUnitsGrossNonTaxableItemReturns())
                    && Util.isObjectEqual(getAmountGrossTaxExemptItemReturns(), c.getAmountGrossTaxExemptItemReturns())
                    && Util.isObjectEqual(getUnitsGrossTaxExemptItemReturns(), c.getUnitsGrossTaxExemptItemReturns())
                    && Util.isObjectEqual(getAmountTaxItemSales(), c.getAmountTaxItemSales())
                    && Util.isObjectEqual(getAmountInclusiveTaxItemSales(), c.getAmountInclusiveTaxItemSales())
                    && Util.isObjectEqual(getAmountTaxItemReturns(), c.getAmountTaxItemReturns())
                    && Util.isObjectEqual(getAmountInclusiveTaxItemReturns(), c.getAmountInclusiveTaxItemReturns())
                    && Util.isObjectEqual(getAmountTaxTransactionSales(), c.getAmountTaxTransactionSales())
                    && Util.isObjectEqual(getAmountInclusiveTaxTransactionSales(), c
                            .getAmountInclusiveTaxTransactionSales())
                    && Util.isObjectEqual(getAmountTaxTransactionReturns(), c.getAmountTaxTransactionReturns())
                    && Util.isObjectEqual(getAmountInclusiveTaxTransactionReturns(), c
                            .getAmountInclusiveTaxTransactionReturns())
                    && Util.isObjectEqual(getAmountGrossTaxableTransactionSales(), c
                            .getAmountGrossTaxableTransactionSales())
                    && Util.isObjectEqual(getAmountGrossNonTaxableTransactionSales(), c
                            .getAmountGrossNonTaxableTransactionSales())
                    && Util.isObjectEqual(getAmountGrossTaxExemptTransactionSales(), c
                            .getAmountGrossTaxExemptTransactionSales())
                    && Util.isObjectEqual(getAmountGrossTaxableTransactionReturns(), c
                            .getAmountGrossTaxableTransactionReturns())
                    && Util.isObjectEqual(getAmountGrossNonTaxableTransactionReturns(), c
                            .getAmountGrossNonTaxableTransactionReturns())
                    && Util.isObjectEqual(getAmountGrossTaxExemptTransactionReturns(), c
                            .getAmountGrossTaxExemptTransactionReturns())
                    && Util.isObjectEqual(getAmountGiftCertificateSales(), c.getAmountGiftCertificateSales())
                    && Util.isObjectEqual(getAmountTransactionDiscounts(), c.getAmountTransactionDiscounts())
                    && Util.isObjectEqual(getAmountItemDiscounts(), c.getAmountItemDiscounts())
                    && Util.isObjectEqual(getAmountItemMarkdowns(), c.getAmountItemMarkdowns())
                    && // StoreCouponDiscounts
                    Util
                            .isObjectEqual(getAmountTransactionDiscStoreCoupons(), c
                                    .getAmountTransactionDiscStoreCoupons())
                    && Util.isObjectEqual(getAmountItemDiscStoreCoupons(), c.getAmountItemDiscStoreCoupons())
                    && Util.isObjectEqual(getAmountPostVoids(), c.getAmountPostVoids())
                    && Util.isObjectEqual(getAmountLineVoids(), c.getAmountLineVoids())
                    && Util.isObjectEqual(getUnitsLineVoids(), c.getUnitsLineVoids())
                    && Util.isObjectEqual(getAmountCancelledTransactions(), c.getAmountCancelledTransactions())
                    && Util.isObjectEqual(floatCount, c.getFloatCount())
                    && Util.isObjectEqual(startingFloatCount, c.getStartingFloatCount())
                    && Util.isObjectEqual(endingFloatCount, c.getEndingFloatCount())
                    && Util.isObjectEqual(startingSafeCount, c.getStartingSafeCount())
                    && Util.isObjectEqual(endingSafeCount, c.getEndingSafeCount())
                    && Util.isObjectEqual(tenderCount, c.getTenderCount())
                    && Util.isObjectEqual(getCombinedCount(), c.getCombinedCount())
                    && getCountTillPickupsTable().equals(c.getCountTillPickupsTable())
                    && Util.isObjectEqual(getTillPickups(), c.getTillPickups())
                    && getCountTillLoans() == c.getCountTillLoans()
                    && Util.isObjectEqual(getTillLoans(), c.getTillLoans())
                    && Util.isObjectEqual(getTillPayIns(), c.getTillPayIns())
                    && Util.isObjectEqual(getTillPayOuts(), c.getTillPayOuts())
                    && Util.isObjectEqual(getAmountGrossTaxableNonMerchandiseSales(), c
                            .getAmountGrossTaxableNonMerchandiseSales())
                    && Util.isObjectEqual(getUnitsGrossTaxableNonMerchandiseSales(), c
                            .getUnitsGrossTaxableNonMerchandiseSales())
                    && Util.isObjectEqual(getAmountGrossNonTaxableNonMerchandiseSales(), c
                            .getAmountGrossNonTaxableNonMerchandiseSales())
                    && Util.isObjectEqual(getUnitsGrossNonTaxableNonMerchandiseSales(), c
                            .getUnitsGrossNonTaxableNonMerchandiseSales())
                    && Util.isObjectEqual(getAmountGrossTaxableNonMerchandiseReturns(), c
                            .getAmountGrossTaxableNonMerchandiseReturns())
                    && Util.isObjectEqual(getUnitsGrossTaxableNonMerchandiseReturns(), c
                            .getUnitsGrossTaxableNonMerchandiseReturns())
                    && Util.isObjectEqual(getAmountGrossNonTaxableNonMerchandiseReturns(), c
                            .getAmountGrossNonTaxableNonMerchandiseReturns())
                    && Util.isObjectEqual(getUnitsGrossNonTaxableNonMerchandiseReturns(), c
                            .getUnitsGrossNonTaxableNonMerchandiseReturns())
                    && Util.isObjectEqual(getAmountGrossGiftCardItemSales(), c.getAmountGrossGiftCardItemSales())
                    && Util.isObjectEqual(getUnitsGrossGiftCardItemSales(), c.getUnitsGrossGiftCardItemSales())
                    && Util.isObjectEqual(getAmountGrossGiftCardItemReturns(), c.getAmountGrossGiftCardItemReturns())
                    && Util.isObjectEqual(getUnitsGrossGiftCardItemReturns(), c.getUnitsGrossGiftCardItemReturns())
                    && Util.isObjectEqual(getTillLoans(), c.getTillLoans())
                    && Util.isObjectEqual(getAmountGrossTaxableItemSalesVoided(), c
                            .getAmountGrossTaxableItemSalesVoided())
                    && Util.isObjectEqual(getAmountGrossNonTaxableItemSalesVoided(), c
                            .getAmountGrossNonTaxableItemSalesVoided())
                    && Util.isObjectEqual(getAmountGrossTaxExemptItemSalesVoided(), c
                            .getAmountGrossTaxExemptItemSalesVoided())
                    && Util.isObjectEqual(getAmountGrossTaxableItemReturnsVoided(), c
                            .getAmountGrossTaxableItemReturnsVoided())
                    && Util.isObjectEqual(getAmountGrossNonTaxableItemReturnsVoided(), c
                            .getAmountGrossNonTaxableItemReturnsVoided())
                    && Util.isObjectEqual(getAmountGrossTaxExemptItemReturnsVoided(), c
                            .getAmountGrossTaxExemptItemReturnsVoided())
                    && Util.isObjectEqual(getAmountGrossTaxableNonMerchandiseSalesVoided(), c
                            .getAmountGrossTaxableNonMerchandiseSalesVoided())
                    && Util.isObjectEqual(getAmountGrossNonTaxableNonMerchandiseSalesVoided(), c
                            .getAmountGrossNonTaxableNonMerchandiseSalesVoided())
                    && Util.isObjectEqual(getAmountGrossTaxableNonMerchandiseReturnsVoided(), c
                            .getAmountGrossTaxableNonMerchandiseReturnsVoided())
                    && Util.isObjectEqual(getAmountGrossNonTaxableNonMerchandiseReturnsVoided(), c
                            .getAmountGrossNonTaxableNonMerchandiseReturnsVoided())
                    && Util.isObjectEqual(getAmountGrossGiftCardItemSalesVoided(), c
                            .getAmountGrossGiftCardItemSalesVoided())
                    && Util.isObjectEqual(getAmountGrossGiftCardItemReturnsVoided(), c
                            .getAmountGrossGiftCardItemReturnsVoided())
                    && Util.isObjectEqual(getAmountGrossTaxableTransactionSalesVoided(), c
                            .getAmountGrossTaxableTransactionSalesVoided())
                    && Util.isObjectEqual(getAmountGrossNonTaxableTransactionSalesVoided(), c
                            .getAmountGrossNonTaxableTransactionSalesVoided())
                    && Util.isObjectEqual(getAmountGrossTaxExemptTransactionSalesVoided(), c
                            .getAmountGrossTaxExemptTransactionSalesVoided())
                    && Util.isObjectEqual(getAmountGrossTaxableTransactionReturnsVoided(), c
                            .getAmountGrossTaxableTransactionReturnsVoided())
                    && Util.isObjectEqual(getAmountGrossNonTaxableTransactionReturnsVoided(), c
                            .getAmountGrossNonTaxableTransactionReturnsVoided())
                    && Util.isObjectEqual(getAmountGrossTaxExemptTransactionReturnsVoided(), c
                            .getAmountGrossTaxExemptTransactionReturnsVoided())
                    && Util.isObjectEqual(getUnitsRestockingFees(), c.getUnitsRestockingFees())
                    && Util.isObjectEqual(getAmountRestockingFees(), c.getAmountRestockingFees())
                    && Util.isObjectEqual(getUnitsRestockingFeesFromNonTaxableItems(), c
                            .getUnitsRestockingFeesFromNonTaxableItems())
                    && Util.isObjectEqual(getAmountRestockingFeesFromNonTaxableItems(), c
                            .getAmountRestockingFeesFromNonTaxableItems())
                    && getNumberShippingCharges() == c.getNumberShippingCharges()
                    && Util.isObjectEqual(getAmountShippingCharges(), c.getAmountShippingCharges())
                    && Util.isObjectEqual(getAmountTaxShippingCharges(), c.getAmountTaxShippingCharges())
                    && Util.isObjectEqual(getAmountInclusiveTaxShippingCharges(), c
                            .getAmountInclusiveTaxShippingCharges())
                    && Util.isObjectEqual(getAmountLayawayPayments(), c.getAmountLayawayPayments())
                    && getCountLayawayPayments() == c.getCountLayawayPayments()
                    && Util.isObjectEqual(getAmountLayawayNew(), c.getAmountLayawayNew())
                    && getCountLayawayNew() == c.getCountLayawayNew()
                    && Util.isObjectEqual(getAmountLayawayPickup(), c.getAmountLayawayPickup())
                    && getCountLayawayPickup() == c.getCountLayawayPickup()
                    && Util.isObjectEqual(getAmountLayawayDeletions(), c.getAmountLayawayDeletions())
                    && getCountLayawayDeletions() == c.getCountLayawayDeletions()
                    && Util.isObjectEqual(getAmountLayawayInitiationFees(), c.getAmountLayawayInitiationFees())
                    && getCountLayawayInitiationFees() == c.getCountLayawayInitiationFees()
                    && Util.isObjectEqual(getAmountLayawayDeletionFees(), c.getAmountLayawayDeletionFees())
                    && getCountLayawayDeletionFees() == c.getCountLayawayDeletionFees()
                    && Util.isObjectEqual(getAmountSpecialOrderNew(), c.getAmountSpecialOrderNew())
                    && Util.isObjectEqual(getAmountSpecialOrderPartial(), c.getAmountSpecialOrderPartial())
                    && Util.isObjectEqual(getAmountOrderPayments(), c.getAmountOrderPayments())
                    && getCountOrderPayments() == c.getCountOrderPayments()
                    && Util.isObjectEqual(getAmountOrderCancels(), c.getAmountOrderCancels())
                    && getCountOrderCancels() == c.getCountOrderCancels()
                    && Util.isObjectEqual(getAmountEmployeeDiscounts(), c.getAmountEmployeeDiscounts())
                    && Util.isObjectEqual(getUnitsEmployeeDiscounts(), c.getUnitsEmployeeDiscounts())
                    && Util.isObjectEqual(getAmountCustomerDiscounts(), c.getAmountCustomerDiscounts())
                    && Util.isObjectEqual(getUnitsCustomerDiscounts(), c.getUnitsCustomerDiscounts())
                    && Util.isObjectEqual(getAmountPriceOverrides(), c.getAmountPriceOverrides())
                    && Util.isObjectEqual(getUnitsPriceOverrides(), c.getUnitsPriceOverrides())
                    && Util.isObjectEqual(getUnitsPriceAdjustments(), c.getUnitsPriceAdjustments())
                    && Util.isObjectEqual(getAmountGrossGiftCertificateIssued(), c
                            .getAmountGrossGiftCertificateIssued())
                    && Util.isObjectEqual(getUnitsGrossGiftCertificateIssued(), c.getUnitsGrossGiftCertificateIssued())
                    && Util.isObjectEqual(getAmountGrossGiftCertificateIssuedVoided(), c
                            .getAmountGrossGiftCertificateIssuedVoided())
                    && Util.isObjectEqual(getUnitsGrossGiftCertificateIssuedVoided(), c
                            .getUnitsGrossGiftCertificateIssuedVoided())
                    && Util.isObjectEqual(getAmountGrossGiftCertificateTendered(), c
                            .getAmountGrossGiftCertificateTendered())
                    && Util.isObjectEqual(getUnitsGrossGiftCertificateTendered(), c
                            .getUnitsGrossGiftCertificateTendered())
                    && Util.isObjectEqual(getAmountGrossGiftCertificateTenderedVoided(), c
                            .getAmountGrossGiftCertificateTenderedVoided())
                    && Util.isObjectEqual(getUnitsGrossGiftCertificateTenderedVoided(), c
                            .getUnitsGrossGiftCertificateTenderedVoided())
                    && Util.isObjectEqual(getAmountGrossGiftCardItemIssued(), c.getAmountGrossGiftCardItemIssued())
                    && Util.isObjectEqual(getUnitsGrossGiftCardItemIssued(), c.getUnitsGrossGiftCardItemIssued())
                    && Util.isObjectEqual(getAmountGrossGiftCardItemIssueVoided(), c
                            .getAmountGrossGiftCardItemIssueVoided())
                    && Util.isObjectEqual(getUnitsGrossGiftCardItemIssueVoided(), c
                            .getUnitsGrossGiftCardItemIssueVoided())
                    && Util.isObjectEqual(getAmountGrossGiftCardItemReloaded(), c.getAmountGrossGiftCardItemReloaded())
                    && Util.isObjectEqual(getUnitsGrossGiftCardItemReloaded(), c.getUnitsGrossGiftCardItemReloaded())
                    && Util.isObjectEqual(getAmountGrossGiftCardItemReloadVoided(), c
                            .getAmountGrossGiftCardItemReloadVoided())
                    && Util.isObjectEqual(getUnitsGrossGiftCardItemReloadVoided(), c
                            .getUnitsGrossGiftCardItemReloadVoided())
                    && getHouseCardEnrollmentsApproved() == c.getHouseCardEnrollmentsApproved()
                    && getHouseCardEnrollmentsDeclined() == c.getHouseCardEnrollmentsDeclined()
                    && Util.isObjectEqual(getTaxes(), c.getTaxes())
                    && Util.isObjectEqual(getAmountGrossGiftCardItemCredit(), c.getAmountGrossGiftCardItemCredit())
                    && Util.isObjectEqual(getUnitsGrossGiftCardItemCredit(), c.getUnitsGrossGiftCardItemCredit())
                    && Util.isObjectEqual(getAmountGrossGiftCardItemCreditVoided(), c
                            .getAmountGrossGiftCardItemCreditVoided())
                    && Util.isObjectEqual(getUnitsGrossGiftCardItemCreditVoided(), c
                            .getUnitsGrossGiftCardItemCreditVoided())
                    && Util.isObjectEqual(getAmountGrossGiftCertificatesRedeemed(), c
                            .getAmountGrossGiftCertificatesRedeemed())
                    && Util.isObjectEqual(getUnitsGrossGiftCertificatesRedeemed(), c
                            .getUnitsGrossGiftCertificatesRedeemed())
                    && Util.isObjectEqual(getAmountGrossGiftCertificatesRedeemedVoided(), c
                            .getAmountGrossGiftCertificatesRedeemedVoided())
                    && Util.isObjectEqual(getUnitsGrossGiftCertificatesRedeemedVoided(), c
                            .getUnitsGrossGiftCertificatesRedeemedVoided())
                    && Util.isObjectEqual(getAmountGrossStoreCreditsIssued(), c.getAmountGrossStoreCreditsIssued())
                    && Util.isObjectEqual(getUnitsGrossStoreCreditsIssued(), c.getUnitsGrossStoreCreditsIssued())
                    && Util.isObjectEqual(getAmountGrossStoreCreditsIssuedVoided(), c
                            .getAmountGrossStoreCreditsIssuedVoided())
                    && Util.isObjectEqual(getUnitsGrossStoreCreditsIssuedVoided(), c
                            .getUnitsGrossStoreCreditsIssuedVoided())
                    && Util.isObjectEqual(getAmountGrossStoreCreditsRedeemed(), c.getAmountGrossStoreCreditsRedeemed())
                    && Util.isObjectEqual(getUnitsGrossStoreCreditsRedeemed(), c.getUnitsGrossStoreCreditsRedeemed())
                    && Util.isObjectEqual(getAmountGrossStoreCreditsRedeemedVoided(), c
                            .getAmountGrossStoreCreditsRedeemedVoided())
                    && Util.isObjectEqual(getUnitsGrossStoreCreditsRedeemedVoided(), c
                            .getUnitsGrossStoreCreditsRedeemedVoided())
                    && Util.isObjectEqual(getAmountGrossItemEmployeeDiscount(), c.getAmountGrossItemEmployeeDiscount())
                    && Util.isObjectEqual(getUnitsGrossItemEmployeeDiscount(), c.getUnitsGrossItemEmployeeDiscount())
                    && Util.isObjectEqual(getAmountGrossItemEmployeeDiscountVoided(), c
                            .getAmountGrossItemEmployeeDiscountVoided())
                    && Util.isObjectEqual(getUnitsGrossItemEmployeeDiscountVoided(), c
                            .getUnitsGrossItemEmployeeDiscountVoided())
                    && Util.isObjectEqual(getAmountGrossTransactionEmployeeDiscount(), c
                            .getAmountGrossTransactionEmployeeDiscount())
                    && Util.isObjectEqual(getUnitsGrossTransactionEmployeeDiscount(), c
                            .getUnitsGrossTransactionEmployeeDiscount())
                    && Util.isObjectEqual(getAmountGrossTransactionEmployeeDiscountVoided(), c
                            .getAmountGrossTransactionEmployeeDiscountVoided())
                    && Util.isObjectEqual(getUnitsGrossTransactionEmployeeDiscountVoided(), c
                            .getUnitsGrossTransactionEmployeeDiscountVoided())
                    && Util.isObjectEqual(getAmountBillPayments(), c
                            .getAmountBillPayments())
                    && Util.isObjectEqual(getAmountChangeRoundedIn(), c
                            .getAmountChangeRoundedIn())
                    && Util.isObjectEqual(getAmountChangeRoundedOut(), c
                            .getAmountChangeRoundedOut())
                    && Util.isObjectEqual(getCountBillPayments(), c
                        .getCountBillPayments()))
            {
                isEqual = true; // set the return code to true
            }
            else
            {
                isEqual = false; // set the return code to false
            }
        }
        return isEqual;
    }

    /**
     * Returns net taxable sales transactions amount (gross sales taxable less
     * gross returns taxable) (tax excluded).
     *
     * @return net sales amount
     */
    public CurrencyIfc getAmountNetTaxableTransactionSales()
    {
        CurrencyIfc netTaxableTransactionSales = getAmountGrossTaxableTransactionSales().subtract(
                getAmountGrossTaxableTransactionSalesVoided()).subtract(
                getAmountGrossTaxableTransactionReturns().subtract(getAmountGrossTaxableTransactionReturnsVoided()));

        return netTaxableTransactionSales;
    }

    /**
     * Returns count of net taxable sales transactions (gross sales taxable less
     * gross returns taxable) (tax excluded).
     *
     * @return count of net sales transactions
     */
    public int getCountNetTaxableTransactionSales()
    {
        int netTaxableTransactionSales = (getCountGrossTaxableTransactionSales() - getCountGrossTaxableTransactionSalesVoided())
                - (getCountGrossTaxableTransactionReturns() - getCountGrossTaxableTransactionReturnsVoided());

        return netTaxableTransactionSales;
    }

    /**
     * Returns net non-taxable sales transactions amount (gross sales
     * non-taxable less gross returns non-taxable) (tax excluded).
     *
     * @return net sales amount
     */
    public CurrencyIfc getAmountNetNonTaxableTransactionSales()
    {
        CurrencyIfc netNonTaxableTransactionSales = getAmountGrossNonTaxableTransactionSales().subtract(
                getAmountGrossNonTaxableTransactionSalesVoided()).subtract(
                getAmountGrossNonTaxableTransactionReturns().subtract(
                        getAmountGrossNonTaxableTransactionReturnsVoided())).subtract(
                getNetAmountGiftCardItemRedeemed());

        return netNonTaxableTransactionSales;
    }

    /**
     * Returns count of net non-taxable sales transactions (gross sales
     * non-taxable less gross returns non-taxable) (tax excluded).
     *
     * @return count of net sales transactions
     */
    public int getCountNetNonTaxableTransactionSales()
    {
        int netNonTaxableTransactionSales = (getCountGrossNonTaxableTransactionSales() - getCountGrossNonTaxableTransactionSalesVoided())
                - (getCountGrossNonTaxableTransactionReturns() - getCountGrossNonTaxableTransactionReturnsVoided());

        return netNonTaxableTransactionSales;
    }

    /**
     * Returns net sales transactions amount (gross sales taxable and non
     * taxable less gross returns taxable and non taxable) (tax excluded).
     *
     * @return net sales amount
     */
    public CurrencyIfc getAmountNetTransactionSales()
    {
        CurrencyIfc netTransactionSales = getAmountNetTaxableTransactionSales().add(
                getAmountNetNonTaxableTransactionSales());

        return netTransactionSales;
    }

    /**
     * Returns count of net sales transactions (gross sales taxable and non
     * taxable less gross returns taxable and non taxable) (tax excluded).
     *
     * @return count of net sales transactions
     */
    public int getCountNetTransactionSales()
    {
        int netTransactionSales = getCountNetTaxableTransactionSales() + getCountNetNonTaxableTransactionSales();

        return netTransactionSales;
    }

    /**
     * Returns net item taxable sales amount (gross sales taxable less gross
     * returns taxable) (tax excluded).
     *
     * @return net sales amount
     */
    public CurrencyIfc getAmountNetTaxableItemSales()
    {
        CurrencyIfc netTaxableItemSales = getAmountGrossTaxableItemSales().subtract(
                getAmountGrossTaxableItemSalesVoided()).subtract(
                getAmountGrossTaxableItemReturns().subtract(getAmountGrossTaxableItemReturnsVoided()));

        return netTaxableItemSales;
    }

    /**
     * Returns total units of net taxable sales transactions (gross sales
     * taxable less gross returns taxable) (tax excluded).
     *
     * @return total units of net sales transactions
     */
    public BigDecimal getUnitsNetTaxableItemSales()
    {
        BigDecimal netTaxableItemSales = getUnitsGrossTaxableItemSales()
                .subtract(getUnitsGrossTaxableItemSalesVoided()).subtract(
                        getUnitsGrossTaxableItemReturns().subtract(getUnitsGrossTaxableItemReturnsVoided()));

        return netTaxableItemSales;
    }

    /**
     * Returns net item non-taxable sales amount (gross sales non-taxable less
     * gross returns non-taxable) (tax excluded).
     *
     * @return net sales amount
     */
    public CurrencyIfc getAmountNetNonTaxableItemSales()
    {
        CurrencyIfc netNonTaxableItemSales = getAmountGrossNonTaxableItemSales().subtract(
                getAmountGrossNonTaxableItemSalesVoided()).subtract(
                getAmountGrossNonTaxableItemReturns().subtract(getAmountGrossNonTaxableItemReturnsVoided()));

        return netNonTaxableItemSales;
    }

    /**
     * Returns total units of net non-taxable sales transactions (gross sales
     * non-taxable less gross returns non-taxable) (tax excluded).
     *
     * @return total units of net sales transactions
     */
    public BigDecimal getUnitsNetNonTaxableItemSales()
    {
        BigDecimal netNonTaxableItemSales = getUnitsGrossNonTaxableItemSales().subtract(
                getUnitsGrossNonTaxableItemSalesVoided()).subtract(
                getUnitsGrossNonTaxableItemReturns().subtract(getUnitsGrossNonTaxableItemReturnsVoided()));

        return netNonTaxableItemSales;
    }

    /**
     * Returns net item sales amount (gross sales taxable and non taxable less
     * gross returns taxable and non taxable) (tax excluded).
     *
     * @return net sales amount
     */
    public CurrencyIfc getAmountNetItemSales()
    {
        CurrencyIfc netItemSales = getAmountNetTaxableItemSales().add(getAmountNetNonTaxableItemSales());

        return netItemSales;
    }

    /* (non-Javadoc)
     * @see oracle.retail.stores.domain.financial.FinancialTotalsIfc#getAmountNetItemSalesMinusRestockingFees()
     */
    public CurrencyIfc getAmountNetItemSalesMinusRestockingFees()
    {
        return getAmountNetItemSales();
    }

    /**
     * Returns total units of net sales transactions (gross sales taxable and
     * non taxable less gross returns taxable and non taxable) (tax excluded).
     *
     * @return total units of net sales transactions
     */
    public BigDecimal getUnitsNetItemSales()
    {
        BigDecimal netItemSales = getUnitsNetTaxableItemSales().add(getUnitsNetNonTaxableItemSales());

        return netItemSales;
    }

    /**
     * Retrieves sum of count of all transactions.
     *
     * @return sum of count of all transactions
     */
    public int getTransactionCount()
    {
        int count = getCountGrossTaxableTransactionSales() + getCountGrossNonTaxableTransactionSales()
                + getCountGrossTaxExemptTransactionSales() + getCountGrossTaxableTransactionReturns()
                + getCountGrossNonTaxableTransactionReturns() + getCountGrossTaxExemptTransactionReturns()
                + getCountGrossTaxableTransactionSalesVoided() + getCountGrossNonTaxableTransactionSalesVoided()
                + getCountGrossTaxExemptTransactionSalesVoided() + getCountGrossTaxableTransactionReturnsVoided()
                + getCountGrossNonTaxableTransactionReturnsVoided() + getCountGrossTaxExemptTransactionReturnsVoided()
                + getNumberNoSales() + getNumberCancelledTransactions() + getTotalCountTillPickups()
                + getCountTillLoans() + getCountTillPayIns() + getCountTillPayOuts();

        return count;
    }

    /**
     * Returns the sum of all the counts in the table of pickup counts.
     *
     * @return int count
     */
    protected int getTotalCountTillPickups()
    {
        int count = 0;
        Enumeration<String> enumer = countTillPickups.keys();

        while (enumer.hasMoreElements())
        {
            count += getCountTillPickups(enumer.nextElement());
        }

        return count;
    }

    /**
     * Sets count of transactions.
     *
     * @param value count of transactions
     * @deprecated Use setCountNetTaxableTransactionSales, etc.
     */
    public void setTransactionCount(int value)
    {
        transactionCount = value;
    }

    /**
     * Adds to count of transactions.
     *
     * @param value increment to count of transactions
     * @deprecated Use addCountNetTaxableTransactionSales, etc.
     */
    public void addTransactionCount(int value)
    {
        transactionCount = transactionCount + value;
    }

    /**
     * Sets count of transactions with returned items.
     *
     * @param value count of transactions with returned items
     */
    public void setTransactionsWithReturnedItemsCount(int value)
    {
        transactionsWithReturnedItemsCount = value;
    }

    /**
     * Returns count of transactions with returned items.
     *
     * @return count of transactions with returned items
     */
    public int getTransactionsWithReturnedItemsCount()
    {
        return transactionsWithReturnedItemsCount;
    }

    /**
     * Adds to count of transactions with returned items.
     *
     * @param value increment to count of transactions with returned items
     */
    public void addTransactionsWithReturnedItemsCount(int value)
    {
        transactionsWithReturnedItemsCount = transactionsWithReturnedItemsCount + value;
    }

    /**
     * Adds a float count object to the expected float count.
     *
     * @param addFloat float count to add
     * @deprecated Use #addStartingFloatCount or #addEndingFloatCount
     */
    public void addFloatCount(ReconcilableCountIfc addFloat)
    {
        // add to float count
        floatCount.add(addFloat);
    }

    /**
     * Retrieves expected float count for this entity.
     *
     * @return expected float count for this entity
     * @deprecated Use #getStartingFloatCount or #getEndingFloatCount
     */
    public ReconcilableCountIfc getFloatCount()
    {
        return floatCount;
    }

    /**
     * Sets expected float count for this entity.
     *
     * @param value expected float count for this entity
     * @deprecated Use #setStartingFloatCount or #setEndingFloatCount
     */
    public void setFloatCount(ReconcilableCountIfc value)
    {
        floatCount = value;
    }

    /**
     * Adds a starting float count object to the expected starting float count.
     *
     * @param addStartingFloat starting float count to add
     */
    public void addStartingFloatCount(ReconcilableCountIfc addStartingFloat)
    {
        // add to starting float count
        startingFloatCount.add(addStartingFloat);
    }

    /**
     * Retrieves expected starting float count for this entity.
     *
     * @return expected starting float count for this entity
     */
    public ReconcilableCountIfc getStartingFloatCount()
    {
        return startingFloatCount;
    }

    /**
     * Sets expected starting float count for this entity.
     *
     * @param value expected starting float count for this entity
     */
    public void setStartingFloatCount(ReconcilableCountIfc value)
    {
        startingFloatCount = value;
    }

    /**
     * Retrieves expected starting safe count for this entity.
     *
     * @return expected starting safe count for this entity
     */
    public ReconcilableCountIfc getStartingSafeCount()
    {
        return startingSafeCount;
    }

    /**
     * Sets expected starting safe count for this entity.
     *
     * @param value expected starting safe count for this entity
     */
    public void setStartingSafeCount(ReconcilableCountIfc value)
    {
        startingSafeCount = value;
    }

    /**
     * Adds a starting safe count object to the expected starting safe count.
     *
     * @param addStartingSafe ending safe count to add
     */
    public void addStartingSafeCount(ReconcilableCountIfc addStartingSafe)
    {
        startingSafeCount.add(addStartingSafe);
    }

    /**
     * Adds a ending float count object to the expected ending float count.
     *
     * @param addEndingFloat ending float count to add
     */
    public void addEndingFloatCount(ReconcilableCountIfc addEndingFloat)
    {
        // add to ending float count
        endingFloatCount.add(addEndingFloat);
    }

    /**
     * Retrieves expected ending float count for this entity.
     *
     * @return expected ending float count for this entity
     */
    public ReconcilableCountIfc getEndingFloatCount()
    {
        return endingFloatCount;
    }

    /**
     * Sets expected ending float count for this entity.
     *
     * @param value expected ending float count for this entity
     */
    public void setEndingFloatCount(ReconcilableCountIfc value)
    {
        endingFloatCount = value;
    }

    /**
     * Retrieves expected ending safe count for this entity.
     *
     * @return expected ending safe count for this entity
     */
    public ReconcilableCountIfc getEndingSafeCount()
    {
        return endingSafeCount;
    }

    /**
     * Sets expected ending safe count for this entity.
     *
     * @param value expected ending safe count for this entity
     */
    public void setEndingSafeCount(ReconcilableCountIfc value)
    {
        endingSafeCount = value;
    }

    /**
     * Adds a ending safe count object to the expected ending safe count.
     *
     * @param addEndingSafe ending safe count to add
     */
    public void addEndingSafeCount(ReconcilableCountIfc addEndingSafe)
    {
        // add to ending safe count
        endingSafeCount.add(addEndingSafe);
    }

    /**
     * Adds a tender count object to the expected tender count.
     *
     * @param addTender tender count to add
     */
    public void addTenderCount(FinancialCountIfc addTender)
    {
        // invoke set starting value method on tenderCount object
        tenderCount.add(addTender);
    }

    /**
     * Retrieves expected tender count for this entity.
     *
     * @return expected tender count for this entity
     */
    public FinancialCountIfc getTenderCount()
    {
        return tenderCount;
    }

    /**
     * Sets expected tender count for this entity.
     *
     * @param value expected tender count for this entity
     */
    public void setTenderCount(FinancialCountIfc value)
    {
        tenderCount = value;
    }

    /**
     * Adds a combined tender count object to the expected combined tender
     * count.
     *
     * @param addCombined combined tender count to add
     */
    public void addCombinedCount(ReconcilableCountIfc addCombined)
    {
        // invoke set starting value method on combinedCount object
        combinedCount.add(addCombined);
    }

    /**
     * Builds and retrieves the expected combined tender count for this entity.
     * Same as calling {@link #getCombinedCount(boolean)} with true.
     *
     * @return expected combined tender count for this entity
     */
    public ReconcilableCountIfc getCombinedCount()
    {
        return getCombinedCount(true);
    }

    /**
     * Optionally builds and retrieves the expected combined tender count for
     * this entity.
     *
     * @param buildCount
     * @return expected combined tender count for this entity
     * @see #buildCombinedExpectedCount()
     */
    public ReconcilableCountIfc getCombinedCount(boolean buildCount)
    {
        if (buildCount)
        {
            buildCombinedExpectedCount();
        }
        return combinedCount;
    }

    /**
     * Sets expected combined tender count for this entity.
     *
     * @param value expected combined tender count for this entity
     */
    public void setCombinedCount(ReconcilableCountIfc value)
    {
        combinedCount = value;
    }

    /**
     * Adds a tender amount object to the expected tender amount.
     *
     * @param addTender tender amount to add
     * @deprecated Use getTenderCount().add()
     */
    public void addExpectedTender(FinancialCountIfc addTender)
    {
        // invoke set starting value method on expectedTender object
        getTenderCount().add(addTender);
    }

    /**
     * Retrieves expected tender amount for this entity.
     *
     * @return expected tender amount for this entity
     * @deprecated Use getTenderCount().
     */
    public FinancialCountIfc getExpectedCount()
    {
        return getTenderCount();
    }

    /**
     * Sets expected tender amount for this entity.
     *
     * @param value expected tender amount for this entity
     * @deprecated Use setTenderCount().
     */
    public void setExpectedTender(FinancialCountIfc value)
    {
        setTenderCount(value);
    }

    /**
     * Adds a tender amount object to the entered tender amount.
     *
     * @param addTender tender amount to add
     * @deprecated.
     */
    public void addEnteredTender(FinancialCountIfc addTender)
    {
    }

    /**
     * Retrieves entered tender amount for this entity.
     *
     * @return entered tender amount for this entity
     * @deprecated.
     */
    public FinancialCountIfc getEnteredCount()
    {
        return null;
    }

    /**
     * Sets entered tender amount for this entity.
     *
     * @param value entered tender amount for this entity
     * @deprecated
     */
    public void setEnteredTender(FinancialCountIfc value)
    {
    }

    /**
     * Retrieves till pickups for this entity.
     *
     * @return till pickups for this entity
     */
    public ReconcilableCountIfc[] getTillPickups()
    {
        ReconcilableCountIfc[] tillPickups = new ReconcilableCountIfc[tillPickupsList.size()];
        tillPickupsList.toArray(tillPickups);
        return tillPickups;
    }

    /**
     * Sets till pickups for this entity.
     *
     * @param value till pickups for this entity
     */
    public void setTillPickups(ReconcilableCountIfc[] value)
    {
        tillPickupsList.clear();
        tillPickupsList.addAll(Arrays.asList(value));
    }

    /**
     * Adds a reconcilable count object to the till pickups.
     *
     * @param addCount reconcilable count to add
     */
    public void addTillPickups(ReconcilableCountIfc addCount)
    {
        tillPickupsList.add(addCount);
    }

    /**
     * Adds a reconcilable count ArrayList to the till pickups.
     *
     * @param addCount reconcilable count to add
     */
    public void addTillPickups(ReconcilableCountIfc[] addCount)
    {
        tillPickupsList.addAll(Arrays.asList(addCount));
    }

    /**
     * Returns total amount (entered) of till pickups.
     *
     * @return amount total amount of till pickups
     */
    public CurrencyIfc getAmountTillPickups()
    {
        String baseCurrencyCode = DomainGateway.getBaseCurrencyType().getCountryCode();

        return getAmountTillPickups(baseCurrencyCode);
    }

    /**
     * Returns total amount of till pickups.
     *
     * @param countryCode String currency country code.
     * @return amount total amount of till pickups
     */
    public CurrencyIfc getAmountTillPickups(String countryCode)
    {
        CurrencyIfc amount = DomainGateway.getCurrencyInstance(countryCode);

        for (ReconcilableCountIfc recCount : tillPickupsList)
        {
            amount = amount.add(recCount.getEntered().getAmount(countryCode));
        }

        return amount;
    }
    
    
    /**
     * retrieves total amount for alternate currency pickups
     * 
     */
    
    public CurrencyIfc[] getAmountTillPickupsAltCurrency()
    {
      Hashtable <String,Integer> currencyTable = getCountTillPickupsTable();
      CurrencyIfc[] currencyList;
      if(currencyTable != null)
      {
          currencyTable.remove(DomainGateway.getBaseCurrencyType().getCountryCode());
          CurrencyIfc[] tempCurrencyList = new CurrencyIfc[currencyTable.size()];
          for(int j = 0;j<currencyTable.size();j++)
          {
              tempCurrencyList[j]=getAmountTillPickups(currencyTable.keys().nextElement().toString());
          }
          currencyList = tempCurrencyList;
      }
      else
      {
          // if no currency table, return an array of CurrencyIfc of 0 length
          currencyList = new CurrencyIfc[0];    
      }
        
      return currencyList;

    }

    /**
     * retrieves currency labels for alternate currency pickups
     * 
     */

    public String[] getPickupsCurrencyLabel()
    {
      Hashtable <String,Integer> currencyTable = getCountTillPickupsTable();
      String[] pickupCurrency;
      if(currencyTable!=null)
      {
          currencyTable.remove(DomainGateway.getBaseCurrencyType().getCountryCode());

          String[] tempCurrency = new String[currencyTable.size()];
          for(int j = 0;j<currencyTable.size();j++)
          {
              tempCurrency[j]= (String) currencyTable.keySet().iterator().next();
          }
          pickupCurrency = tempCurrency;
      }
      else
      {
         pickupCurrency = new String[0];
      }
      
      
      return pickupCurrency;
    }

    /**
     * Sets count of till pickups.
     *
     * @param value count of till pickups
     */
    public void setCountTillPickups(int value)
    {
        String localCountryCode = DomainGateway.getBaseCurrencyType().getCountryCode();
        setCountTillPickups(localCountryCode, value);
    }

    /**
     * Sets count of till pickups.
     *
     * @param countryCode String @link
     *            oracle.retail.stores.domain.utility.CountryCodesIfc
     * @param value count of till pickups
     */
    public void setCountTillPickups(String countryCode, int value)
    {
        countTillPickups.put(countryCode, Integer.valueOf(value));
    }

    /**
     * Adds to count of till pickups.
     *
     * @param value amount to add
     */
    public void addCountTillPickups(int value)
    {
        addCountTillPickups(DomainGateway.getBaseCurrencyType().getCountryCode(), value);
    }

    /**
     * Adds to count of till pickups.
     *
     * @param countryCode String @link
     *            oracle.retail.stores.domain.utility.CountryCodesIfc
     * @param value amount to add
     */
    public void addCountTillPickups(String countryCode, int value)
    {
        Integer count = null;

        if (countTillPickups.containsKey(countryCode))
        {
            count = countTillPickups.get(countryCode);
            count = Integer.valueOf(count.intValue() + value);
        }
        else
        {
            count = Integer.valueOf(value);
        }
        countTillPickups.put(countryCode, count);
    }

    /**
     * Returns count of till pickups. This is maintained separately because when
     * we retrieve financials, we don't retrieve the individual pickups.
     *
     * @return count of till pickups
     */
    public int getCountTillPickups()
    {
        return getCountTillPickups(DomainGateway.getBaseCurrencyType().getCountryCode());
    }

    /**
     * Returns count of till pickups.
     *
     * @param countryCode String currency country code.
     * @link oracle.retail.stores.domain.utility.CountryCodesIfc
     * @return count of till pickups
     */
    public int getCountTillPickups(String countryCode)
    {
        Integer count = Integer.valueOf(0);

        if (countTillPickups.containsKey(countryCode))
        {
            count = countTillPickups.get(countryCode);
        }
        return count.intValue();
    }

    /**
     * Returns table of till pickup counts.
     *
     * @return Hashtable till pickup counts.
     */
    public Hashtable<String,Integer> getCountTillPickupsTable()
    {
        return countTillPickups;
    }

    /**
     * Retrieves till loans for this entity.
     *
     * @return till loans for this entity
     */
    public ReconcilableCountIfc[] getTillLoans()
    {
        ReconcilableCountIfc[] tillLoans = new ReconcilableCountIfc[tillLoansList.size()];
        tillLoansList.toArray(tillLoans);
        return tillLoans;
    }

    /**
     * Sets till loans for this entity.
     *
     * @param value till loans for this entity
     */
    public void setTillLoans(ReconcilableCountIfc[] value)
    {
        tillLoansList = new ArrayList<ReconcilableCountIfc>();
        addTillLoans(value);
    }

    /**
     * Adds a reconcilable count object to the till loans.
     *
     * @param addCount reconcilable count to add
     */
    public void addTillLoans(ReconcilableCountIfc addCount)
    {
        tillLoansList.add(addCount);
    }

    /**
     * Adds a reconcilable count ArrayList to the till loans.
     *
     * @param addCount reconcilable count to add
     */
    public void addTillLoans(ReconcilableCountIfc[] addCount)
    {
        tillLoansList.addAll(Arrays.asList(addCount));
    }

    /**
     * Returns total amount (entered) of till loans.
     *
     * @return amount total amount of till loans
     */
    public CurrencyIfc getAmountTillLoans()
    {
        CurrencyIfc amount = DomainGateway.getBaseCurrencyInstance();

        for (ReconcilableCountIfc r : tillLoansList)
        {
            amount = amount.add(r.getEntered().getAmount());
        }

        return amount;
    }

    /**
     * Sets count of till loans.
     *
     * @param value count of till loans
     */
    public void setCountTillLoans(int value)
    {
        countTillLoans = value;
    }

    /**
     * Adds to count of till loans.
     *
     * @param value amount to add
     */
    public void addCountTillLoans(int value)
    {
        countTillLoans = countTillLoans + value;
    }

    /**
     * Returns count of till loans. This is maintained separately because when
     * we retrieve financials, we don't retrieve the individual loans.
     *
     * @return count of till loans
     */
    public int getCountTillLoans()
    {
        // return countTillLoans;
        int loanCount = 0;

        for (ReconcilableCountIfc r : tillLoansList)
        {
            loanCount += r.getEntered().getNumberItems();
        }

        return loanCount;
    }

    /**
     * Retrieves till pay-ins for this entity.
     *
     * @return till pay-ins for this entity
     */
    public ReconcilableCountIfc[] getTillPayIns()
    {
        ReconcilableCountIfc[] tillPayins = new ReconcilableCountIfc[tillPayInsList.size()];
        tillPayInsList.toArray(tillPayins);
        return tillPayins;
    }

    /**
     * Sets till Payins for this entity.
     *
     * @param value till Payins for this entity
     */
    public void setTillPayIns(ReconcilableCountIfc[] value)
    {
        tillPayInsList.clear();
        tillPayInsList.addAll(Arrays.asList(value));
    }

    /**
     * Adds a reconcilable count object to the till Payins.
     *
     * @param addCount reconcilable count to add
     */
    public void addTillPayIns(ReconcilableCountIfc addCount)
    {
        tillPayInsList.add(addCount);
    }

    /**
     * Adds a reconcilable count ArrayList to the till Payins.
     *
     * @param addCount reconcilable count to add
     */
    public void addTillPayIns(ReconcilableCountIfc[] addCount)
    {
        tillPayInsList.addAll(Arrays.asList(addCount));
    }

    /**
     * Returns total amount (entered) of till Payins.
     *
     * @return amount total amount of till Payins
     */
    public CurrencyIfc getAmountTillPayIns()
    {
        CurrencyIfc amount = DomainGateway.getBaseCurrencyInstance();

        for (ReconcilableCountIfc r : tillPayInsList)
        {
            amount = amount.add(r.getEntered().getAmount());
        }

        return amount;
    }

    /**
     * Returns count of till Payins.
     *
     * @return count of till Payins
     */
    public int getCountTillPayIns()
    {
        int count = 0;

        for (ReconcilableCountIfc r : tillPayInsList)
        {
            count += r.getEntered().getNumberItems();
        }

        return count;
    }

    /**
     * Retrieves till Payouts for this entity.
     *
     * @return till Payouts for this entity
     */
    public ReconcilableCountIfc[] getTillPayOuts()
    {
        ReconcilableCountIfc[] tillPayouts = new ReconcilableCountIfc[tillPayOutsList.size()];
        tillPayOutsList.toArray(tillPayouts);
        return tillPayouts;
    }

    /**
     * Sets till Payouts for this entity.
     *
     * @param value till Payouts for this entity
     */
    public void setTillPayOuts(ReconcilableCountIfc[] value)
    {
        tillPayOutsList = new ArrayList<ReconcilableCountIfc>();
        addTillPayOuts(value);
    }

    /**
     * Adds a reconcilable count object to the till Payouts.
     *
     * @param addCount reconcilable count to add
     */
    public void addTillPayOuts(ReconcilableCountIfc addCount)
    {
        tillPayOutsList.add(addCount);
    }

    /**
     * Adds a reconcilable count ArrayList to the till Payouts.
     *
     * @param addCount reconcilable count to add
     */
    public void addTillPayOuts(ReconcilableCountIfc[] addCount)
    {
        tillPayOutsList.addAll(Arrays.asList(addCount));
    }

    /**
     * Returns total amount (entered) of till Payouts.
     *
     * @return amount total amount of till Payouts
     */
    public CurrencyIfc getAmountTillPayOuts()
    {
        CurrencyIfc amount = DomainGateway.getBaseCurrencyInstance();

        for (ReconcilableCountIfc r : tillPayOutsList)
        {
            amount = amount.add(r.getEntered().getAmount());
        }

        return amount;
    }

    /**
     * Returns count of till Payouts.
     *
     * @return count of till Payouts
     */
    public int getCountTillPayOuts()
    {
        int loanCount = 0;

        for (ReconcilableCountIfc r : tillPayOutsList)
        {
            loanCount += r.getEntered().getNumberItems();
        }

        return loanCount;
    }

    /**
     * Sets amount of till pay-ins.
     *
     * @param value amount of till pay-ins
     * @deprecated Use #getTillPayIns()
     */
    public void setAmountTillPayIns(CurrencyIfc value)
    {
        amountTillPayIns = value;
    }

    /**
     * Adds amount of till pay-in transactions.
     *
     * @param value amount of till pay-in transactions
     * @deprecated Use #addTillPayIns()
     */
    public void addAmountTillPayIns(CurrencyIfc value)
    {
        if (amountTillPayIns == null)
        {
            amountTillPayIns = value;
        }
        else
        {
            amountTillPayIns = amountTillPayIns.add(value);
        }
    }

    /**
     * Sets number of till pay-ins.
     *
     * @param value number of till pay-ins
     * @deprecated Use #getTillPayIns()
     */
    public void setCountTillPayIns(int value)
    {
        countTillPayIns = value;
    }

    /**
     * Adds to count of till pay-in transactions.
     *
     * @param value count of till pay-in transactions
     * @deprecated Use #addTillPayIns()
     */
    public void addCountTillPayIns(int value)
    {
        countTillPayIns = countTillPayIns + value;
    }

    /**
     * Sets amount of till pay-outs.
     *
     * @param value amount of till pay-outs
     * @deprecated Use #getTillPayOuts()
     */
    public void setAmountTillPayOuts(CurrencyIfc value)
    {
        amountTillPayOuts = value;
    }

    /**
     * Adds amount of till pay-out transactions.
     *
     * @param value amount of till pay-out transactions
     * @deprecated Use #addTillPayOuts()
     */
    public void addAmountTillPayOuts(CurrencyIfc value)
    {
        if (amountTillPayOuts == null)
        {
            amountTillPayOuts = value;
        }
        else
        {
            amountTillPayOuts = amountTillPayOuts.add(value);
        }
    }

    /**
     * Sets number of till pay-outs.
     *
     * @param value number of till pay-outs
     * @deprecated Use #getTillPayOuts()
     */
    public void setCountTillPayOuts(int value)
    {
        countTillPayOuts = value;
    }

    /**
     * Adds to count of till pay-out transactions.
     *
     * @param value count of till pay-out transactions
     * @deprecated Use #getTillPayOuts()
     */
    public void addCountTillPayOuts(int value)
    {
        countTillPayOuts = countTillPayOuts + value;
    }

    /**
     * Retrieves amount of gross non-taxable sale transactions.
     *
     * @return amount of gross non-taxable sale transactions
     */
    public CurrencyIfc getAmountHousePayments()
    {
        if (amountHousePayments == null)
        {
            amountHousePayments = DomainGateway.getBaseCurrencyInstance();
        }
        return amountHousePayments;
    }

    /**
     * Sets amount of house payments.
     *
     * @param value amount of house payments sales
     */
    public void setAmountHousePayments(CurrencyIfc value)
    {
        amountHousePayments = value;
    }

    /**
     * Adds amount of gross non-taxable sale transactions.
     *
     * @param value amount of gross non-taxable sale transactions
     */
    public void addAmountHousePayments(CurrencyIfc value)
    {
        if (amountHousePayments == null)
        {
            amountHousePayments = value;
        }
        else
        {
            amountHousePayments = getAmountHousePayments().add(value);
        }
    }

    /**
     * Retrieves count of non-taxable sale transactions.
     *
     * @return count of non-taxable sale transactions
     */
    public int getCountHousePayments()
    {
        return countHousePayments;
    }

    /**
     * Sets number of house payments.
     *
     * @param value number of house payments
     */
    public void setCountHousePayments(int value)
    {
        countHousePayments = value;
    }

    /**
     * Adds to count of non-taxable sale transactions.
     *
     * @param value count of non-taxable sale transactions
     */
    public void addCountHousePayments(int value)
    {
        countHousePayments = countHousePayments + value;
    }

    /* (non-Javadoc)
     * @see oracle.retail.stores.domain.financial.FinancialTotalsIfc#getAmountGrossTaxableSales()
     */
    public CurrencyIfc getAmountGrossTaxableSales()
    {
        return getAmountGrossTaxableItemSales().subtract(
                getAmountGrossTaxableItemSalesVoided()).subtract(
                getAmountGrossTaxableItemReturns().subtract(
                getAmountGrossTaxableItemReturnsVoided())).subtract(
                getAmountRestockingFees());
    }

    /**
     * Retrieves amount of gross taxable item sales (tax excluded).
     *
     * @return amount of gross taxable item sales (tax excluded)
     */
    public CurrencyIfc getAmountGrossTaxableItemSales()
    {
        if (amountGrossTaxableItemSales == null)
        {
            amountGrossTaxableItemSales = DomainGateway.getBaseCurrencyInstance();
        }
        return amountGrossTaxableItemSales;
    }

    /**
     * Sets amount of gross taxable item sales (tax excluded).
     *
     * @param value amount of gross taxable item sales (tax excluded)
     */
    public void setAmountGrossTaxableItemSales(CurrencyIfc value)
    {
        amountGrossTaxableItemSales = value;
    }

    /**
     * Adds to amount of gross taxable item sales (tax excluded).
     *
     * @param value increment amount of gross taxable item sales (tax excluded)
     */
    public void addAmountGrossTaxableItemSales(CurrencyIfc value)
    {
        if (amountGrossTaxableItemSales == null)
        {
            amountGrossTaxableItemSales = value;
        }
        else
        {
            amountGrossTaxableItemSales = getAmountGrossTaxableItemSales().add(value);
        }
    }

    /**
     * Retrieves number of units in gross item sales. This is the sum of gross
     * item taxable and gross item non-taxable sales.
     *
     * @return number of units in gross item sales
     */
    public BigDecimal getUnitsGrossItemSales()
    {
        return getUnitsGrossTaxableItemSales().add(getUnitsGrossNonTaxableItemSales());
    }

    /**
     * Retrieves number of units in gross taxable item sales.
     *
     * @return number of units in gross taxable item sales
     */
    public BigDecimal getUnitsGrossTaxableItemSales()
    {
        if (unitsGrossTaxableItemSales == null)
        {
            unitsGrossTaxableItemSales = BigDecimal.ZERO;
        }
        return unitsGrossTaxableItemSales;
    }

    /**
     * Sets number of units in gross taxable item sales.
     *
     * @param value number of units in gross taxable item sales
     */
    public void setUnitsGrossTaxableItemSales(BigDecimal value)
    {
        unitsGrossTaxableItemSales = value;
    }

    /**
     * Adds to number of units in gross taxable item sales.
     *
     * @param value increment number of units in gross taxable item sales
     */
    public void addUnitsGrossTaxableItemSales(BigDecimal value)
    {
        if (unitsGrossTaxableItemSales == null)
        {
            unitsGrossTaxableItemSales = value;
        }
        else
        {
            unitsGrossTaxableItemSales = unitsGrossTaxableItemSales.add(value);
        }
    }

    /* (non-Javadoc)
     * @see oracle.retail.stores.domain.financial.FinancialTotalsIfc#getAmountGrossNonTaxableSales()
     */
    public CurrencyIfc getAmountGrossNonTaxableSales()
    {
        return getAmountGrossNonTaxableItemSales().subtract(
                getAmountGrossNonTaxableItemSalesVoided()).subtract(
                getAmountGrossNonTaxableItemReturns().subtract(
                getAmountGrossNonTaxableItemReturnsVoided())).subtract(
                getAmountRestockingFeesFromNonTaxableItems());
    }

    /**
     * Retrieves amount of gross non-taxable item sales.
     *
     * @return amount of gross non-taxable item sales
     */
    public CurrencyIfc getAmountGrossNonTaxableItemSales()
    {
        if (amountGrossNonTaxableItemSales == null)
        {
            amountGrossNonTaxableItemSales = DomainGateway.getBaseCurrencyInstance();
        }
        return amountGrossNonTaxableItemSales;
    }

    /**
     * Sets amount of gross non-taxable item sales.
     *
     * @param value amount of gross non-taxable item sales
     */
    public void setAmountGrossNonTaxableItemSales(CurrencyIfc value)
    {
        amountGrossNonTaxableItemSales = value;
    }

    /**
     * Adds to amount of gross non-taxable item sales.
     *
     * @param value increment amount of gross non-taxable item sales
     */
    public void addAmountGrossNonTaxableItemSales(CurrencyIfc value)
    {
        if (amountGrossNonTaxableItemSales == null)
        {
            amountGrossNonTaxableItemSales = value;
        }
        else
        {
            amountGrossNonTaxableItemSales = amountGrossNonTaxableItemSales.add(value);
        }
    }

    /**
     * Retrieves number of units in gross non-taxable item sales.
     *
     * @return number of units in gross non-taxable item sales
     */
    public BigDecimal getUnitsGrossNonTaxableItemSales()
    {
        if (unitsGrossNonTaxableItemSales == null)
        {
            unitsGrossNonTaxableItemSales = BigDecimal.ZERO;
        }
        return unitsGrossNonTaxableItemSales;
    }

    /**
     * Sets number of units in gross non-taxable item sales.
     *
     * @param value number of units in gross non-taxable item sales
     */
    public void setUnitsGrossNonTaxableItemSales(BigDecimal value)
    {
        unitsGrossNonTaxableItemSales = value;
    }

    /**
     * Adds to number of units in gross non-taxable item sales.
     *
     * @param value increment number of units in gross non-taxable item sales
     */
    public void addUnitsGrossNonTaxableItemSales(BigDecimal value)
    {
        if (unitsGrossNonTaxableItemSales == null)
        {
            unitsGrossNonTaxableItemSales = value;
        }
        else
        {
            unitsGrossNonTaxableItemSales = unitsGrossNonTaxableItemSales.add(value);
        }
    }

    /**
     * Retrieves amount of gross tax-exempt item sales.
     *
     * @return amount of gross tax-exempt item sales
     */
    public CurrencyIfc getAmountGrossTaxExemptItemSales()
    {
        if (amountGrossTaxExemptItemSales == null)
        {
            amountGrossTaxExemptItemSales = DomainGateway.getBaseCurrencyInstance();
        }
        return amountGrossTaxExemptItemSales;
    }

    /**
     * Sets amount of gross tax-exempt item sales.
     *
     * @param value amount of gross tax-exempt item sales
     */
    public void setAmountGrossTaxExemptItemSales(CurrencyIfc value)
    {
        amountGrossTaxExemptItemSales = value;
    }

    /**
     * Adds to amount of gross tax-exempt item sales.
     *
     * @param value increment amount of gross tax-exempt item sales
     */
    public void addAmountGrossTaxExemptItemSales(CurrencyIfc value)
    {
        if (amountGrossTaxExemptItemSales == null)
        {
            amountGrossTaxExemptItemSales = value;
        }
        else
        {
            amountGrossTaxExemptItemSales = amountGrossTaxExemptItemSales.add(value);
        }
    }

    /**
     * Retrieves number of units in gross tax-exempt item sales.
     *
     * @return number of units in gross tax-exempt item sales
     */
    public BigDecimal getUnitsGrossTaxExemptItemSales()
    {
        if (unitsGrossTaxExemptItemSales == null)
        {
            unitsGrossTaxExemptItemSales = BigDecimal.ZERO;
        }
        return unitsGrossTaxExemptItemSales;
    }

    /**
     * Sets number of units in gross tax-exempt item sales.
     *
     * @param value number of units in gross tax-exempt item sales
     */
    public void setUnitsGrossTaxExemptItemSales(BigDecimal value)
    {
        unitsGrossTaxExemptItemSales = value;
    }

    /**
     * Adds to number of units in gross tax-exempt item sales.
     *
     * @param value increment number of units in gross tax-exempt item sales
     */
    public void addUnitsGrossTaxExemptItemSales(BigDecimal value)
    {
        if (unitsGrossTaxExemptItemSales == null)
        {
            unitsGrossTaxExemptItemSales = value;
        }
        else
        {
            unitsGrossTaxExemptItemSales = unitsGrossTaxExemptItemSales.add(value);
        }
    }

    /**
     * Retrieves amount of gross taxable item returns (tax excluded).
     *
     * @return amount of gross taxable item returns (tax excluded)
     */
    public CurrencyIfc getAmountGrossTaxableItemReturns()
    {
        if (amountGrossTaxableItemReturns == null)
        {
            amountGrossTaxableItemReturns = DomainGateway.getBaseCurrencyInstance();
        }
        return amountGrossTaxableItemReturns;
    }

    /**
     * Sets amount of gross taxable item returns (tax excluded).
     *
     * @param value amount of gross taxable item returns (tax excluded)
     */
    public void setAmountGrossTaxableItemReturns(CurrencyIfc value)
    {
        amountGrossTaxableItemReturns = value;
    }

    /**
     * Adds to amount of gross taxable item returns (tax excluded).
     *
     * @param value increment amount of gross taxable item returns (tax
     *            excluded)
     */
    public void addAmountGrossTaxableItemReturns(CurrencyIfc value)
    {
        if (amountGrossTaxableItemReturns == null)
        {
            amountGrossTaxableItemReturns = value;
        }
        else
        {
            amountGrossTaxableItemReturns = amountGrossTaxableItemReturns.add(value);
        }
    }

    /**
     * Retrieves number of units in gross taxable item returns.
     *
     * @return number of units in gross taxable item returns
     */
    public BigDecimal getUnitsGrossTaxableItemReturns()
    {
        if (unitsGrossTaxableItemReturns == null)
        {
            unitsGrossTaxableItemReturns = BigDecimal.ZERO;
        }
        return unitsGrossTaxableItemReturns;
    }

    /**
     * Sets number of units in gross taxable item returns.
     *
     * @param value number of units in gross taxable item returns
     */
    public void setUnitsGrossTaxableItemReturns(BigDecimal value)
    {
        unitsGrossTaxableItemReturns = value;
    }

    /**
     * Adds to number of units in gross taxable item returns.
     *
     * @param value increment number of units in gross taxable item returns
     */
    public void addUnitsGrossTaxableItemReturns(BigDecimal value)
    {
        if (unitsGrossTaxableItemReturns == null)
        {
            unitsGrossTaxableItemReturns = value;
        }
        else
        {
            unitsGrossTaxableItemReturns = unitsGrossTaxableItemReturns.add(value);
        }
    }

    /**
     * Retrieves number of units in gross item returns. This is the sum of gross
     * item taxable and gross item non-taxable returns.
     *
     * @return number of units in gross item returns
     */
    public BigDecimal getUnitsGrossItemReturns()
    {
        return getUnitsGrossTaxableItemReturns().add(getUnitsGrossNonTaxableItemReturns());
    }

    /**
     * Retrieves amount of gross non-taxable item returns.
     *
     * @return amount of gross non-taxable item returns
     */
    public CurrencyIfc getAmountGrossNonTaxableItemReturns()
    {
        if (amountGrossNonTaxableItemReturns == null)
        {
            amountGrossNonTaxableItemReturns = DomainGateway.getBaseCurrencyInstance();
        }
        return amountGrossNonTaxableItemReturns;
    }

    /**
     * Sets amount of gross non-taxable item returns.
     *
     * @param value amount of gross non-taxable item returns
     */
    public void setAmountGrossNonTaxableItemReturns(CurrencyIfc value)
    {
        amountGrossNonTaxableItemReturns = value;
    }

    /**
     * Adds to amount of gross non-taxable item returns.
     *
     * @param value increment amount of gross non-taxable item returns
     */
    public void addAmountGrossNonTaxableItemReturns(CurrencyIfc value)
    {
        if (amountGrossNonTaxableItemReturns == null)
        {
            amountGrossNonTaxableItemReturns = value;
        }
        else
        {
            amountGrossNonTaxableItemReturns = amountGrossNonTaxableItemReturns.add(value);
        }
    }

    /**
     * Retrieves number of units in gross non-taxable item returns.
     *
     * @return number of units in gross non-taxable item returns
     */
    public BigDecimal getUnitsGrossNonTaxableItemReturns()
    {
        if (unitsGrossNonTaxableItemReturns == null)
        {
            unitsGrossNonTaxableItemReturns = BigDecimal.ZERO;
        }
        return unitsGrossNonTaxableItemReturns;
    }

    /**
     * Sets number of units in gross non-taxable item returns.
     *
     * @param value number of units in gross non-taxable item returns
     */
    public void setUnitsGrossNonTaxableItemReturns(BigDecimal value)
    {
        unitsGrossNonTaxableItemReturns = value;
    }

    /**
     * Adds to number of units in gross non-taxable item returns.
     *
     * @param value increment number of units in gross non-taxable item returns
     */
    public void addUnitsGrossNonTaxableItemReturns(BigDecimal value)
    {
        if (unitsGrossNonTaxableItemReturns == null)
        {
            unitsGrossNonTaxableItemReturns = value;
        }
        else
        {
            unitsGrossNonTaxableItemReturns = unitsGrossNonTaxableItemReturns.add(value);
        }
    }

    /**
     * Retrieves amount of gross tax-exempt item returns.
     *
     * @return amount of gross tax-exempt item returns
     */
    public CurrencyIfc getAmountGrossTaxExemptItemReturns()
    {
        if (amountGrossTaxExemptItemReturns == null)
        {
            amountGrossTaxExemptItemReturns = DomainGateway.getBaseCurrencyInstance();
        }
        return amountGrossTaxExemptItemReturns;
    }

    /**
     * Sets amount of gross tax-exempt item returns.
     *
     * @param value amount of gross tax-exempt item returns
     */
    public void setAmountGrossTaxExemptItemReturns(CurrencyIfc value)
    {
        amountGrossTaxExemptItemReturns = value;
    }

    /**
     * Adds to amount of gross tax-exempt item returns.
     *
     * @param value increment amount of gross tax-exempt item returns
     */
    public void addAmountGrossTaxExemptItemReturns(CurrencyIfc value)
    {
        if (amountGrossTaxExemptItemReturns == null)
        {
            amountGrossTaxExemptItemReturns = value;
        }
        else
        {
            amountGrossTaxExemptItemReturns = amountGrossTaxExemptItemReturns.add(value);
        }
    }

    /**
     * Retrieves number of units in gross tax-exempt item returns.
     *
     * @return number of units in gross tax-exempt item returns
     */
    public BigDecimal getUnitsGrossTaxExemptItemReturns()
    {
        if (unitsGrossTaxExemptItemReturns == null)
        {
            unitsGrossTaxExemptItemReturns = BigDecimal.ZERO;
        }
        return unitsGrossTaxExemptItemReturns;
    }

    /**
     * Sets number of units in gross tax-exempt item returns.
     *
     * @param value number of units in gross tax-exempt item returns
     */
    public void setUnitsGrossTaxExemptItemReturns(BigDecimal value)
    {
        unitsGrossTaxExemptItemReturns = value;
    }

    /**
     * Adds to number of units in gross tax-exempt item returns.
     *
     * @param value increment number of units in gross tax-exempt item returns
     */
    public void addUnitsGrossTaxExemptItemReturns(BigDecimal value)
    {
        if (unitsGrossTaxExemptItemReturns == null)
        {
            unitsGrossTaxExemptItemReturns = value;
        }
        else
        {
            unitsGrossTaxExemptItemReturns = unitsGrossTaxExemptItemReturns.add(value);
        }
    }

    /**
     * Retrieves amount of tax collected for items sold.
     *
     * @return amount of tax collected for items sold
     */
    public CurrencyIfc getAmountTaxItemSales()
    {
        if (amountTaxItemSales == null)
        {
            amountTaxItemSales = DomainGateway.getBaseCurrencyInstance();
        }
        return amountTaxItemSales;
    }

    /**
     * Sets amount of tax collected for items sold.
     *
     * @param value amount of tax collected for items sold
     */
    public void setAmountTaxItemSales(CurrencyIfc value)
    {
        amountTaxItemSales = value;
    }

    /**
     * Adds to amount of tax collected for items sold.
     *
     * @param value increment amount of tax collected for items sold
     */
    public void addAmountTaxItemSales(CurrencyIfc value)
    {
        if (amountTaxItemSales == null)
        {
            amountTaxItemSales = value;
        }
        else
        {
            amountTaxItemSales = amountTaxItemSales.add(value);
        }
    }

    /**
     * Retrieves amount of inclusive tax collected for items sold.
     *
     * @return amount of inclusive tax collected for items sold
     */
    public CurrencyIfc getAmountInclusiveTaxItemSales()
    {
        if (amountInclusiveTaxItemSales == null)
        {
            amountInclusiveTaxItemSales = DomainGateway.getBaseCurrencyInstance();
        }
        return amountInclusiveTaxItemSales;

    }

    /**
     * Sets amount of inclusive tax collected for items sold.
     *
     * @param value amount of inclusive tax collected for items sold
     */
    public void setAmountInclusiveTaxItemSales(CurrencyIfc value)
    {
        amountInclusiveTaxItemSales = value;
    }

    /**
     * Adds to amount of inclusive tax collected for items sold.
     *
     * @param value increment amount of inclusive tax collected for items sold
     */
    public void addAmountInclusiveTaxItemSales(CurrencyIfc value)
    {
        if (amountInclusiveTaxItemSales == null)
        {
            amountInclusiveTaxItemSales = value;
        }
        else
        {
            amountInclusiveTaxItemSales = amountInclusiveTaxItemSales.add(value);
        }
    }

    /**
     * Retrieves amount of tax on returned items.
     *
     * @return amount of tax on returned items
     */
    public CurrencyIfc getAmountTaxItemReturns()
    {
        if (amountTaxItemReturns == null)
        {
            amountTaxItemReturns = DomainGateway.getBaseCurrencyInstance();
        }
        return amountTaxItemReturns;
    }

    /**
     * Sets amount of tax on returned items.
     *
     * @param value amount of tax on returned items
     */
    public void setAmountTaxItemReturns(CurrencyIfc value)
    {
        amountTaxItemReturns = value;
    }

    /**
     * Adds to amount of tax on returned items.
     *
     * @param value increment amount of tax on returned items
     */
    public void addAmountTaxItemReturns(CurrencyIfc value)
    {
        if (amountTaxItemReturns == null)
        {
            amountTaxItemReturns = value;
        }
        else
        {
            amountTaxItemReturns = amountTaxItemReturns.add(value);
        }
    }

    /**
     * Retrieves amount of inclusive tax on returned items.
     *
     * @return amount of inclusive tax on returned items
     */
    public CurrencyIfc getAmountInclusiveTaxItemReturns()
    {
        if (amountInclusiveTaxItemReturns == null)
        {
            amountInclusiveTaxItemReturns = DomainGateway.getBaseCurrencyInstance();
        }
        return amountInclusiveTaxItemReturns;
    }

    /**
     * Sets amount of inclusive tax on returned items.
     *
     * @param value amount of inclusive tax on returned items
     */
    public void setAmountInclusiveTaxItemReturns(CurrencyIfc value)
    {
        amountInclusiveTaxItemReturns = value;
    }

    /**
     * Adds to amount of inclusive tax on returned items.
     *
     * @param value increment amount of inclusive tax on returned items
     */
    public void addAmountInclusiveTaxItemReturns(CurrencyIfc value)
    {
        if (amountInclusiveTaxItemReturns == null)
        {
            amountInclusiveTaxItemReturns = value;
        }
        else
        {
            amountInclusiveTaxItemReturns = amountInclusiveTaxItemReturns.add(value);
        }
    }

    /**
     * Retrieves amount of tax collected on sale transactions.
     *
     * @return amount of tax collected on sale transactions
     */
    public CurrencyIfc getAmountTaxTransactionSales()
    {
        if (amountTaxTransactionSales == null)
        {
            amountTaxTransactionSales = DomainGateway.getBaseCurrencyInstance();
        }
        return amountTaxTransactionSales;
    }

    /**
     * Sets amount of tax collected on sale transactions.
     *
     * @param value amount of tax collected on sale transactions
     */
    public void setAmountTaxTransactionSales(CurrencyIfc value)
    {
        amountTaxTransactionSales = value;
    }

    /**
     * Adds to amount of tax collected on sale transactions.
     *
     * @param value increment amount of tax collected on sale transactions
     */
    public void addAmountTaxTransactionSales(CurrencyIfc value)
    {
        if (amountTaxTransactionSales == null)
        {
            amountTaxTransactionSales = value;
        }
        else
        {
            amountTaxTransactionSales = amountTaxTransactionSales.add(value);
        }
    }

    /**
     * Retrieves amount of inclusive tax collected for sale transactions.
     *
     * @return amount of inclusive tax collected for sale transactions
     */
    public CurrencyIfc getAmountInclusiveTaxTransactionSales()
    {
        if (amountInclusiveTaxTransactionSales == null)
        {
            amountInclusiveTaxTransactionSales = DomainGateway.getBaseCurrencyInstance();
        }
        return amountInclusiveTaxTransactionSales;
    }

    /**
     * Sets amount of inclusive tax collected for sale transactions.
     *
     * @param value amount of inclusive tax collected for sale transactions
     */
    public void setAmountInclusiveTaxTransactionSales(CurrencyIfc value)
    {
        amountInclusiveTaxTransactionSales = value;
    }

    /**
     * Adds to amount of inclusive tax collected for sale transactions.
     *
     * @param value increment amount of inclusive tax collected for sale
     *            transactions
     */
    public void addAmountInclusiveTaxTransactionSales(CurrencyIfc value)
    {
        if (amountInclusiveTaxTransactionSales == null)
        {
            amountInclusiveTaxTransactionSales = value;
        }
        else
        {
            amountInclusiveTaxTransactionSales = amountInclusiveTaxTransactionSales.add(value);
        }
    }

    /**
     * Retrieves amount of tax on return transactions.
     *
     * @return amount of tax on return transactions
     */
    public CurrencyIfc getAmountTaxTransactionReturns()
    {
        if (amountTaxTransactionReturns == null)
        {
            amountTaxTransactionReturns = DomainGateway.getBaseCurrencyInstance();
        }
        return amountTaxTransactionReturns;
    }

    /* (non-Javadoc)
     * @see oracle.retail.stores.domain.financial.FinancialTotalsIfc#getAmountTaxTransactions()
     */
    public CurrencyIfc getAmountTaxTransactions()
    {
        CurrencyIfc tax;
        if (vatEnabled)
        {
            tax = getAmountInclusiveTaxTransactionSales().subtract(getAmountInclusiveTaxTransactionReturns()).add(
                    getAmountInclusiveTaxShippingCharges());
        }
        else
        {
            tax = getAmountTaxTransactionSales().subtract(getAmountTaxTransactionReturns());
        }
        return tax;
    }

    /**
     * Returns 0 or 1 to select the label to be printed in Till Summary report
     * 
     * @return
     */
    public int isVATEnabled()
    {
        if (vatEnabled)
        {
            return 0;
        }
        else
        {
            return 1;
        }
    }

    /**
     * Sets amount of tax on return transactions.
     *
     * @param value amount of tax on return transactions
     */

    public void setAmountTaxTransactionReturns(CurrencyIfc value)
    {
        amountTaxTransactionReturns = value;
    }

    /**
     * Adds to amount of tax on return transactions.
     *
     * @param value increment amount of tax on return transactions
     */
    public void addAmountTaxTransactionReturns(CurrencyIfc value)
    {
        if (amountTaxTransactionReturns == null)
        {
            amountTaxTransactionReturns = value;
        }
        else
        {
            amountTaxTransactionReturns = amountTaxTransactionReturns.add(value);
        }
    }

    /**
     * Retrieves amount of inclusive tax on return transactions.
     *
     * @return amount of inclusive tax on return transactions
     */
    public CurrencyIfc getAmountInclusiveTaxTransactionReturns()
    {
        if (amountInclusiveTaxTransactionReturns == null)
        {
            amountInclusiveTaxTransactionReturns = DomainGateway.getBaseCurrencyInstance();
        }
        return amountInclusiveTaxTransactionReturns;
    }

    /**
     * Sets amount of inclusive tax on return transactions.
     *
     * @param value amount of inclusive tax on return transactions
     */
    public void setAmountInclusiveTaxTransactionReturns(CurrencyIfc value)
    {
        amountInclusiveTaxTransactionReturns = value;
    }

    /**
     * Adds to amount of inclusive tax on return transactions.
     *
     * @param value increment amount of inclusive tax on return transactions
     */
    public void addAmountInclusiveTaxTransactionReturns(CurrencyIfc value)
    {
        if (amountInclusiveTaxTransactionReturns == null)
        {
            amountInclusiveTaxTransactionReturns = value;
        }
        else
        {
            amountInclusiveTaxTransactionReturns = amountInclusiveTaxTransactionReturns.add(value);
        }
    }

    /* (non-Javadoc)
     * @see oracle.retail.stores.domain.financial.FinancialTotalsIfc#getAmountGrossTransactionSales()
     */
    public CurrencyIfc getAmountGrossTransactionSales()
    {
        return getAmountGrossTaxableTransactionSales().add(getAmountGrossNonTaxableTransactionSales());
    }

    /**
     * Retrieves amount of gross taxable sale transactions (tax excluded).
     *
     * @return amount of gross taxable sale transactions (tax excluded)
     */
    public CurrencyIfc getAmountGrossTaxableTransactionSales()
    {
        if (amountGrossTaxableTransactionSales == null)
        {
            amountGrossTaxableTransactionSales = DomainGateway.getBaseCurrencyInstance();
        }
        return amountGrossTaxableTransactionSales;
    }

    /**
     * Sets amount of gross taxable sale transactions (tax excluded).
     *
     * @param value amount of gross taxable sale transactions (tax excluded)
     */
    public void setAmountGrossTaxableTransactionSales(CurrencyIfc value)
    {
        amountGrossTaxableTransactionSales = value;
    }

    /**
     * Adds amount of gross taxable sale transactions (tax excluded).
     *
     * @param value amount of gross taxable sale transactions (tax excluded)
     */
    public void addAmountGrossTaxableTransactionSales(CurrencyIfc value)
    {
        if (amountGrossTaxableTransactionSales == null)
        {
            amountGrossTaxableTransactionSales = value;
        }
        else
        {
            amountGrossTaxableTransactionSales = amountGrossTaxableTransactionSales.add(value);
        }
    }

    /**
     * Retrieves count of taxable sale transactions.
     *
     * @return count of taxable sale transactions
     */
    public int getCountGrossTaxableTransactionSales()
    {
        return countGrossTaxableTransactionSales;
    }

    /* (non-Javadoc)
     * @see oracle.retail.stores.domain.financial.FinancialTotalsIfc#getCountGrossTransactionSales()
     */
    public int getCountGrossTransactionSales()
    {
        return countGrossTaxableTransactionSales + countGrossNonTaxableTransactionSales;
    }

    /**
     * Sets count of taxable sale transactions.
     *
     * @param value count of taxable sale transactions
     */
    public void setCountGrossTaxableTransactionSales(int value)
    {
        countGrossTaxableTransactionSales = value;
    }

    /**
     * Adds to count of taxable sale transactions.
     *
     * @param value count of taxable sale transactions
     */
    public void addCountGrossTaxableTransactionSales(int value)
    {
        countGrossTaxableTransactionSales = countGrossTaxableTransactionSales + value;
    }

    /**
     * Retrieves amount of gross non-taxable sale transactions.
     *
     * @return amount of gross non-taxable sale transactions
     */
    public CurrencyIfc getAmountGrossNonTaxableTransactionSales()
    {
        if (amountGrossNonTaxableTransactionSales == null)
        {
            amountGrossNonTaxableTransactionSales = DomainGateway.getBaseCurrencyInstance();
        }
        return amountGrossNonTaxableTransactionSales;
    }

    /**
     * Sets amount of gross non-taxable sale transactions.
     *
     * @param value amount of gross non-taxable sale transactions
     */
    public void setAmountGrossNonTaxableTransactionSales(CurrencyIfc value)
    {
        amountGrossNonTaxableTransactionSales = value;
    }

    /**
     * Adds amount of gross non-taxable sale transactions.
     *
     * @param value amount of gross non-taxable sale transactions
     */
    public void addAmountGrossNonTaxableTransactionSales(CurrencyIfc value)
    {
        if (amountGrossNonTaxableTransactionSales == null)
        {
            amountGrossNonTaxableTransactionSales = value;
        }
        else
        {
            amountGrossNonTaxableTransactionSales = amountGrossNonTaxableTransactionSales.add(value);
        }
    }

    /**
     * Retrieves count of non-taxable sale transactions.
     *
     * @return count of non-taxable sale transactions
     */
    public int getCountGrossNonTaxableTransactionSales()
    {
        return countGrossNonTaxableTransactionSales;
    }

    /**
     * Sets count of non-taxable sale transactions.
     *
     * @param value count of non-taxable sale transactions
     */
    public void setCountGrossNonTaxableTransactionSales(int value)
    {
        countGrossNonTaxableTransactionSales = value;
    }

    /**
     * Adds to count of non-taxable sale transactions.
     *
     * @param value count of non-taxable sale transactions
     */
    public void addCountGrossNonTaxableTransactionSales(int value)
    {
        countGrossNonTaxableTransactionSales = countGrossNonTaxableTransactionSales + value;
    }

    /**
     * Retrieves amount of gross tax-exempt sale transactions.
     *
     * @return amount of gross tax-exempt sale transactions
     */
    public CurrencyIfc getAmountGrossTaxExemptTransactionSales()
    {
        if (amountGrossTaxExemptTransactionSales == null)
        {
            amountGrossTaxExemptTransactionSales = DomainGateway.getBaseCurrencyInstance();
        }
        return amountGrossTaxExemptTransactionSales;
    }

    /**
     * Sets amount of gross tax-exempt sale transactions.
     *
     * @param value amount of gross tax-exempt sale transactions
     */
    public void setAmountGrossTaxExemptTransactionSales(CurrencyIfc value)
    {
        amountGrossTaxExemptTransactionSales = value;
    }

    /**
     * Adds amount of gross tax-exempt sale transactions.
     *
     * @param value amount of gross tax-exempt sale transactions
     */
    public void addAmountGrossTaxExemptTransactionSales(CurrencyIfc value)
    {
        if (amountGrossTaxExemptTransactionSales == null)
        {
            amountGrossTaxExemptTransactionSales = value;
        }
        else
        {
            amountGrossTaxExemptTransactionSales = amountGrossTaxExemptTransactionSales.add(value);
        }
    }

    /**
     * Retrieves count of tax-exempt sale transactions.
     *
     * @return count of tax-exempt sale transactions
     */
    public int getCountGrossTaxExemptTransactionSales()
    {
        return countGrossTaxExemptTransactionSales;
    }

    /**
     * Sets count of tax-exempt sale transactions.
     *
     * @param value count of tax-exempt sale transactions
     */
    public void setCountGrossTaxExemptTransactionSales(int value)
    {
        countGrossTaxExemptTransactionSales = value;
    }

    /**
     * Adds to count of tax-exempt sale transactions.
     *
     * @param value count of tax-exempt sale transactions
     */
    public void addCountGrossTaxExemptTransactionSales(int value)
    {
        countGrossTaxExemptTransactionSales = countGrossTaxExemptTransactionSales + value;
    }

    /**
     * Retrieves amount of gross taxable return transactions (tax excluded).
     *
     * @return amount of gross taxable return transactions (tax excluded)
     */
    public CurrencyIfc getAmountGrossTaxableTransactionReturns()
    {
        if (amountGrossTaxableTransactionReturns == null)
        {
            amountGrossTaxableTransactionReturns = DomainGateway.getBaseCurrencyInstance();
        }
        return amountGrossTaxableTransactionReturns;
    }

    /* (non-Javadoc)
     * @see oracle.retail.stores.domain.financial.FinancialTotalsIfc#getAmountGrossTransactionReturns()
     */
    public CurrencyIfc getAmountGrossTransactionReturns()
    {
        return getAmountGrossTaxableTransactionReturns().add(getAmountGrossNonTaxableTransactionReturns());
    }

    /**
     * Sets amount of gross taxable return transactions (tax excluded).
     *
     * @param value amount of gross taxable return transactions (tax excluded)
     */
    public void setAmountGrossTaxableTransactionReturns(CurrencyIfc value)
    {
        amountGrossTaxableTransactionReturns = value;
    }

    /**
     * Adds amount of gross taxable return transactions (tax excluded).
     *
     * @param value amount of gross taxable return transactions (tax excluded)
     */
    public void addAmountGrossTaxableTransactionReturns(CurrencyIfc value)
    {
        if (amountGrossTaxableTransactionReturns == null)
        {
            amountGrossTaxableTransactionReturns = value;
        }
        else
        {
            amountGrossTaxableTransactionReturns = amountGrossTaxableTransactionReturns.add(value);
        }
    }

    /**
     * Retrieves count of taxable return transactions.
     *
     * @return count of taxable return transactions
     */
    public int getCountGrossTaxableTransactionReturns()
    {
        return countGrossTaxableTransactionReturns;
    }

    /* (non-Javadoc)
     * @see oracle.retail.stores.domain.financial.FinancialTotalsIfc#getCountGrossTransactionReturns()
     */
    public int getCountGrossTransactionReturns()
    {
        return getCountGrossTaxableTransactionReturns() +
            getCountGrossNonTaxableTransactionReturns();
    }

    /**
     * Sets count of taxable return transactions.
     *
     * @param value count of taxable return transactions
     */
    public void setCountGrossTaxableTransactionReturns(int value)
    {
        countGrossTaxableTransactionReturns = value;
    }

    /**
     * Adds to count of taxable return transactions.
     *
     * @param value count of taxable return transactions
     */
    public void addCountGrossTaxableTransactionReturns(int value)
    {
        countGrossTaxableTransactionReturns = countGrossTaxableTransactionReturns + value;
    }

    /**
     * Retrieves amount of gross non-taxable return transactions.
     *
     * @return amount of gross non-taxable return transactions
     */
    public CurrencyIfc getAmountGrossNonTaxableTransactionReturns()
    {
        if (amountGrossNonTaxableTransactionReturns == null)
        {
            amountGrossNonTaxableTransactionReturns = DomainGateway.getBaseCurrencyInstance();
        }
        return amountGrossNonTaxableTransactionReturns;
    }

    /**
     * Sets amount of gross non-taxable return transactions.
     *
     * @param value amount of gross non-taxable return transactions
     */
    public void setAmountGrossNonTaxableTransactionReturns(CurrencyIfc value)
    {
        amountGrossNonTaxableTransactionReturns = value;
    }

    /**
     * Adds amount of gross non-taxable return transactions.
     *
     * @param value amount of gross non-taxable return transactions
     */
    public void addAmountGrossNonTaxableTransactionReturns(CurrencyIfc value)
    {
        if (amountGrossNonTaxableTransactionReturns == null)
        {
            amountGrossNonTaxableTransactionReturns = value;
        }
        else
        {
            amountGrossNonTaxableTransactionReturns = amountGrossNonTaxableTransactionReturns.add(value);
        }
    }

    /**
     * Retrieves count of non-taxable return transactions.
     *
     * @return count of non-taxable return transactions
     */
    public int getCountGrossNonTaxableTransactionReturns()
    {
        return countGrossNonTaxableTransactionReturns;
    }

    /**
     * Sets count of non-taxable return transactions.
     *
     * @param value count of non-taxable return transactions
     */
    public void setCountGrossNonTaxableTransactionReturns(int value)
    {
        countGrossNonTaxableTransactionReturns = value;
    }

    /**
     * Adds to count of non-taxable return transactions.
     *
     * @param value count of non-taxable return transactions
     */
    public void addCountGrossNonTaxableTransactionReturns(int value)
    {
        countGrossNonTaxableTransactionReturns = countGrossNonTaxableTransactionReturns + value;
    }

    /**
     * Retrieves amount of gross tax-exempt return transactions.
     *
     * @return amount of gross tax-exempt return transactions
     */
    public CurrencyIfc getAmountGrossTaxExemptTransactionReturns()
    {
        if (amountGrossTaxExemptTransactionReturns == null)
        {
            amountGrossTaxExemptTransactionReturns = DomainGateway.getBaseCurrencyInstance();
        }
        return amountGrossTaxExemptTransactionReturns;
    }

    /**
     * Sets amount of gross tax-exempt return transactions.
     *
     * @param value amount of gross tax-exempt return transactions
     */
    public void setAmountGrossTaxExemptTransactionReturns(CurrencyIfc value)
    {
        amountGrossTaxExemptTransactionReturns = value;
    }

    /**
     * Adds amount of gross tax-exempt return transactions.
     *
     * @param value amount of gross tax-exempt return transactions
     */
    public void addAmountGrossTaxExemptTransactionReturns(CurrencyIfc value)
    {
        if (amountGrossTaxExemptTransactionReturns == null)
        {
            amountGrossTaxExemptTransactionReturns = value;
        }
        else
        {
            amountGrossTaxExemptTransactionReturns = amountGrossTaxExemptTransactionReturns.add(value);
        }
    }

    /**
     * Retrieves count of tax-exempt return transactions.
     *
     * @return count of tax-exempt return transactions
     */
    public int getCountGrossTaxExemptTransactionReturns()
    {
        return countGrossTaxExemptTransactionReturns;
    }

    /**
     * Sets count of tax-exempt return transactions.
     *
     * @param value count of tax-exempt return transactions
     */
    public void setCountGrossTaxExemptTransactionReturns(int value)
    {
        countGrossTaxExemptTransactionReturns = value;
    }

    /**
     * Adds to count of tax-exempt return transactions.
     *
     * @param value count of tax-exempt return transactions
     */
    public void addCountGrossTaxExemptTransactionReturns(int value)
    {
        countGrossTaxExemptTransactionReturns = countGrossTaxExemptTransactionReturns + value;
    }

    /**
     * Retrieves amount of gift certificate sales.
     *
     * @return amount of gift certificate sales
     */
    public CurrencyIfc getAmountGiftCertificateSales()
    {
        if (amountGiftCertificateSales == null)
        {
            amountGiftCertificateSales = DomainGateway.getBaseCurrencyInstance();
        }
        return amountGiftCertificateSales;
    }

    /**
     * Sets amount of gift certificate sales.
     *
     * @param value amount of gift certificate sales
     */
    public void setAmountGiftCertificateSales(CurrencyIfc value)
    {
        amountGiftCertificateSales = value;
    }

    /**
     * Adds to amount of gift certificate sales.
     *
     * @param value increment amount of gift certificate sales
     */
    public void addAmountGiftCertificateSales(CurrencyIfc value)
    {
        if (amountGiftCertificateSales == null)
        {
            amountGiftCertificateSales = value;
        }
        else
        {
            amountGiftCertificateSales = amountGiftCertificateSales.add(value);
        }
    }

    /**
     * Retrieves units of gift certificate sales.
     *
     * @return units of gift certificate sales
     */
    public int getUnitsGiftCertificateSales()
    {
        return unitsGiftCertificateSales;
    }

    /**
     * Sets units of gift certificate sales.
     *
     * @param value units of gift certificate sales
     */
    public void setUnitsGiftCertificateSales(int value)
    {
        unitsGiftCertificateSales = value;
    }

    /**
     * Adds to units of gift certificate sales.
     *
     * @param value increment units of gift certificate sales
     */
    public void addUnitsGiftCertificateSales(int value)
    {
        unitsGiftCertificateSales = unitsGiftCertificateSales + value;
    }

    /**
     * Retrieves amount of transaction discounts.
     *
     * @return amount of transaction discounts
     */
    public CurrencyIfc getAmountTransactionDiscounts()
    {
        if (amountTransactionDiscounts == null)
        {
            amountTransactionDiscounts = DomainGateway.getBaseCurrencyInstance();
        }
        return amountTransactionDiscounts;
    }

    /**
     * Sets amount of transaction discounts.
     *
     * @param value amount of transaction discounts
     */
    public void setAmountTransactionDiscounts(CurrencyIfc value)
    {
        amountTransactionDiscounts = value;
    }

    /**
     * Adds to amount of transaction discounts.
     *
     * @param value increment amount of transaction discounts
     */
    public void addAmountTransactionDiscounts(CurrencyIfc value)
    {
        if (amountTransactionDiscounts == null)
        {
            amountTransactionDiscounts = value;
        }
        else
        {
            amountTransactionDiscounts = amountTransactionDiscounts.add(value);
        }
    }

    /**
     * Retrieves number of transaction discounts.
     *
     * @return number of transaction discounts
     */
    public int getNumberTransactionDiscounts()
    {
        return numberTransactionDiscounts;
    }

    /**
     * Sets number of transaction discounts.
     *
     * @param value number of transaction discounts
     */
    public void setNumberTransactionDiscounts(int value)
    {
        numberTransactionDiscounts = value;
    }

    /**
     * Adds to number of transaction discounts.
     *
     * @param value increment number of transaction discounts
     */
    public void addNumberTransactionDiscounts(int value)
    {
        numberTransactionDiscounts = numberTransactionDiscounts + value;
    }

    /**
     * Retrieves amount of item discounts.
     *
     * @return amount of item discounts
     */
    public CurrencyIfc getAmountItemDiscounts()
    {
        if (amountItemDiscounts == null)
        {
            amountItemDiscounts = DomainGateway.getBaseCurrencyInstance();
        }
        return amountItemDiscounts;
    }

    /**
     * Sets amount of item discounts.
     *
     * @param value amount of item discounts
     */
    public void setAmountItemDiscounts(CurrencyIfc value)
    {
        amountItemDiscounts = value;
    }

    /**
     * Adds to amount of item discounts.
     *
     * @param value increment amount of item discounts
     */
    public void addAmountItemDiscounts(CurrencyIfc value)
    {
        if (amountItemDiscounts == null)
        {
            amountItemDiscounts = value;
        }
        else
        {
            amountItemDiscounts = amountItemDiscounts.add(value);
        }
    }

    /**
     * Retrieves number of item discounts.
     *
     * @return number of item discounts
     */
    public int getNumberItemDiscounts()
    {
        return numberItemDiscounts;
    }

    /**
     * Sets number of item discounts.
     *
     * @param value number of item discounts
     */
    public void setNumberItemDiscounts(int value)
    {
        numberItemDiscounts = value;
    }

    /**
     * Adds to number of item discounts.
     *
     * @param value increment number of item discounts
     */
    public void addNumberItemDiscounts(int value)
    {
        numberItemDiscounts = numberItemDiscounts + value;
    }

    /**
     * Retrieves amount of item markdowns.
     *
     * @return amount of item markdowns
     */
    public CurrencyIfc getAmountItemMarkdowns()
    {
        if (amountItemMarkdowns == null)
        {
            amountItemMarkdowns = DomainGateway.getBaseCurrencyInstance();
        }
        return amountItemMarkdowns;
    }

    /**
     * Sets amount of item markdowns.
     *
     * @param value amount of item markdowns
     */
    public void setAmountItemMarkdowns(CurrencyIfc value)
    {
        amountItemMarkdowns = value;
    }

    /**
     * Adds to amount of item markdowns.
     *
     * @param value increment amount of item markdowns
     */
    public void addAmountItemMarkdowns(CurrencyIfc value)
    {
        if (amountItemMarkdowns == null)
        {
            amountItemMarkdowns = value;
        }
        else
        {
            amountItemMarkdowns = amountItemMarkdowns.add(value);
        }
    }

    /**
     * Retrieves number of item markdowns.
     *
     * @return number of item markdowns
     */
    public int getNumberItemMarkdowns()
    {
        return numberItemMarkdowns;
    }

    /**
     * Sets number of item markdowns.
     *
     * @param value number of item markdowns
     */
    public void setNumberItemMarkdowns(int value)
    {
        numberItemMarkdowns = value;
    }

    /**
     * Adds to number of item markdowns.
     *
     * @param value increment number of item markdowns
     */
    public void addNumberItemMarkdowns(int value)
    {
        numberItemMarkdowns = numberItemMarkdowns + value;
    }

    /**
     * Retrieves amount of restocking fees.
     *
     * @return amount of restocking fees
     */
    public CurrencyIfc getAmountRestockingFees()
    {
        if (amountRestockingFees == null)
        {
            amountRestockingFees = DomainGateway.getBaseCurrencyInstance();
        }
        return amountRestockingFees;
    }

    /**
     * Sets amount of restocking fees.
     *
     * @param value amount of restocking fees
     */
    public void setAmountRestockingFees(CurrencyIfc value)
    {
        amountRestockingFees = value;
    }

    /**
     * Adds to amount of restocking fees.
     *
     * @param value increment amount of restocking fees
     */
    public void addAmountRestockingFees(CurrencyIfc value)
    {
        if (amountRestockingFees == null)
        {
            amountRestockingFees = value;
        }
        else
        {
            amountRestockingFees = amountRestockingFees.add(value);
        }
    }

    /**
     * Retrieves number of restocking fees.
     *
     * @return number of restocking fees
     */
    public BigDecimal getUnitsRestockingFees()
    {
        if (unitsRestockingFees == null)
        {
            unitsRestockingFees = BigDecimal.ZERO;
        }
        return unitsRestockingFees;
    }

    /**
     * Sets number of restocking fees.
     *
     * @param value number of restocking fees
     */
    public void setUnitsRestockingFees(BigDecimal value)
    {
        unitsRestockingFees = value;
    }

    /**
     * Adds to number of restocking fees.
     *
     * @param value increment number of restocking fees
     */
    public void addUnitsRestockingFees(BigDecimal value)
    {
        if (unitsRestockingFees == null)
        {
            unitsRestockingFees = value;
        }
        else
        {
            unitsRestockingFees = unitsRestockingFees.add(value);
        }
    }

    /**
     * Retrieves amount of shipping charges.
     *
     * @return amount of shippiing charges
     */
    public CurrencyIfc getAmountShippingCharges()
    {
        if (amountShippingCharges == null)
        {
            amountShippingCharges = DomainGateway.getBaseCurrencyInstance();
        }
        return amountShippingCharges;
    }

    /**
     * Sets amount of shipping charges.
     *
     * @param value amount of shipping charges
     */
    public void setAmountShippingCharges(CurrencyIfc value)
    {
        amountShippingCharges = value;
    }

    /**
     * Adds to amount of shipping charges.
     *
     * @param value increment amount of shipping charges
     */
    public void addAmountShippingCharges(CurrencyIfc value)
    {
        if (amountShippingCharges == null)
        {
            amountShippingCharges = value;
        }
        else
        {
            amountShippingCharges = amountShippingCharges.add(value);
        }
    }

    /**
     * Retrieves number of shipping charges.
     *
     * @return number of shipping charges
     */
    public int getNumberShippingCharges()
    {
        return numberShippingCharges;
    }

    /**
     * Sets number of shipping charges.
     *
     * @param value number of shipping charges
     */
    public void setNumberShippingCharges(int value)
    {
        numberShippingCharges = value;
    }

    /**
     * Adds to number of shipping charges.
     *
     * @param value increment number of shipping charges
     */
    public void addNumberShippingCharges(int value)
    {
        numberShippingCharges = numberShippingCharges + value;
    }

    /**
     * Retrieves amount of shipping charges tax.
     *
     * @return amount of shippiing charges tax
     */
    public CurrencyIfc getAmountTaxShippingCharges()
    {
        if (amountTaxShippingCharges == null)
        {
            amountTaxShippingCharges = DomainGateway.getBaseCurrencyInstance();
        }
        return amountTaxShippingCharges;
    }

    /**
     * Sets amount of shipping charges tax.
     *
     * @param value amount of shipping charges tax
     */
    public void setAmountTaxShippingCharges(CurrencyIfc value)
    {
        amountTaxShippingCharges = value;
    }

    /**
     * Adds to amount of shipping charges tax.
     *
     * @param value increment amount of shipping charges tax
     */
    public void addAmountTaxShippingCharges(CurrencyIfc value)
    {
        if (amountTaxShippingCharges == null)
        {
            amountTaxShippingCharges = value;
        }
        else
        {
            amountTaxShippingCharges = amountTaxShippingCharges.add(value);
        }
    }

    /**
     * Retrieves amount of shipping charges inclusive tax.
     *
     * @return amount of shippiing charges inclusive tax
     */
    public CurrencyIfc getAmountInclusiveTaxShippingCharges()
    {
        if (amountInclusiveTaxShippingCharges == null)
        {
            amountInclusiveTaxShippingCharges = DomainGateway.getBaseCurrencyInstance();
        }
        return amountInclusiveTaxShippingCharges;
    }

    /**
     * Sets amount of shipping charges inclusive tax.
     *
     * @param value amount of shipping charges inclusive tax
     */
    public void setAmountInclusiveTaxShippingCharges(CurrencyIfc value)
    {
        amountInclusiveTaxShippingCharges = value;
    }

    /**
     * Adds to amount of shipping charges inclusive tax.
     *
     * @param value increment amount of shipping charges inclusive tax
     */
    public void addAmountInclusiveTaxShippingCharges(CurrencyIfc value)
    {
        if (amountInclusiveTaxShippingCharges == null)
        {
            amountInclusiveTaxShippingCharges = value;
        }
        else
        {
            amountInclusiveTaxShippingCharges = amountInclusiveTaxShippingCharges.add(value);
        }
    }

    /**
     * Retrieves amount of transaction post-voids.
     *
     * @return amount of transaction post-voids
     */
    public CurrencyIfc getAmountPostVoids()
    {
        if (amountPostVoids == null)
        {
            amountPostVoids = DomainGateway.getBaseCurrencyInstance();
        }
        return amountPostVoids;
    }

    /**
     * Sets amount of transaction post-voids.
     *
     * @param value amount of transaction post-voids
     */
    public void setAmountPostVoids(CurrencyIfc value)
    {
        amountPostVoids = value;
    }

    /**
     * Adds to amount of transaction post-voids.
     *
     * @param value increment amount of transaction post-voids
     */
    public void addAmountPostVoids(CurrencyIfc value)
    {
        if (amountPostVoids == null)
        {
            amountPostVoids = value;
        }
        else
        {
            amountPostVoids = amountPostVoids.add(value);
        }
    }

    /**
     * Retrieves number of transaction post-voids.
     *
     * @return number of transaction post-voids
     */
    public int getNumberPostVoids()
    {
        return numberPostVoids;
    }

    /**
     * Sets number of transaction post-voids.
     *
     * @param value number of transaction post-voids
     */
    public void setNumberPostVoids(int value)
    {
        numberPostVoids = value;
    }

    /**
     * Adds to number of transaction post-voids.
     *
     * @param value increment number of transaction post-voids
     */
    public void addNumberPostVoids(int value)
    {
        numberPostVoids = numberPostVoids + value;
    }

    /**
     * Retrieves number of no-sale transactions.
     *
     * @return number of no-sale transactions
     */
    public int getNumberNoSales()
    {
        return numberNoSales;
    }

    /**
     * Sets number of no-sale transactions.
     *
     * @param value number of no-sale transactions
     */
    public void setNumberNoSales(int value)
    {
        numberNoSales = value;
    }

    /**
     * Adds to number of no-sale transactions.
     *
     * @param value increment number of no-sale transactions
     */
    public void addNumberNoSales(int value)
    {
        numberNoSales = numberNoSales + value;
    }

    /**
     * Retrieves amount of line voids (deleted lines).
     *
     * @return amount of line voids (deleted lines)
     */
    public CurrencyIfc getAmountLineVoids()
    {
        if (amountLineVoids == null)
        {
            amountLineVoids = DomainGateway.getBaseCurrencyInstance();
        }
        return amountLineVoids;
    }

    /**
     * Sets amount of line voids (deleted lines).
     *
     * @param value amount of line voids (deleted lines)
     */
    public void setAmountLineVoids(CurrencyIfc value)
    {
        amountLineVoids = value;
    }

    /**
     * Adds to amount of line voids (deleted lines).
     *
     * @param value increment amount of line voids (deleted lines)
     */
    public void addAmountLineVoids(CurrencyIfc value)
    {
        if (amountLineVoids == null)
        {
            amountLineVoids = value;
        }
        else
        {
            amountLineVoids = amountLineVoids.add(value);
        }
    }

    /**
     * Retrieves units on line voids (deleted lines).
     *
     * @return units on line voids (deleted lines)
     */
    public BigDecimal getUnitsLineVoids()
    {
        if (unitsLineVoids == null)
        {
            unitsLineVoids = BigDecimal.ZERO;
        }
        return unitsLineVoids;
    }

    /**
     * Sets units on line voids (deleted lines).
     *
     * @param value units on line voids (deleted lines)
     */
    public void setUnitsLineVoids(BigDecimal value)
    {
        unitsLineVoids = value;
    }

    /**
     * Adds to units on line voids (deleted lines).
     *
     * @param value increment units on line voids (deleted lines)
     */
    public void addUnitsLineVoids(BigDecimal value)
    {
        if (unitsLineVoids == null)
        {
            unitsLineVoids = value;
        }
        else
        {
            unitsLineVoids = unitsLineVoids.add(value);
        }
    }

    /**
     * Retrieves amount of cancelled transactions.
     *
     * @return amount of cancelled transactions
     */
    public CurrencyIfc getAmountCancelledTransactions()
    {
        if (amountCancelledTransactions == null)
        {
            amountCancelledTransactions = DomainGateway.getBaseCurrencyInstance();
        }
        return amountCancelledTransactions;
    }

    /**
     * Sets amount of cancelled transactions.
     *
     * @param value amount of cancelled transactions
     */
    public void setAmountCancelledTransactions(CurrencyIfc value)
    {
        amountCancelledTransactions = value;
    }

    /**
     * Adds to amount of cancelled transactions.
     *
     * @param value increment amount of cancelled transactions
     */
    public void addAmountCancelledTransactions(CurrencyIfc value)
    {
        if (amountCancelledTransactions == null)
        {
            amountCancelledTransactions = value;
        }
        else
        {
            amountCancelledTransactions = amountCancelledTransactions.add(value);
        }
    }

    /**
     * Retrieves number of cancelled transactions.
     *
     * @return number of cancelled transactions
     */
    public int getNumberCancelledTransactions()
    {
        return numberCancelledTransactions;
    }

    /**
     * Sets number of cancelled transactions.
     *
     * @param value number of cancelled transactions
     */
    public void setNumberCancelledTransactions(int value)
    {
        numberCancelledTransactions = value;
    }

    /**
     * Adds to number of cancelled transactions.
     *
     * @param value increment number of cancelled transactions
     */
    public void addNumberCancelledTransactions(int value)
    {
        numberCancelledTransactions = numberCancelledTransactions + value;
    }

    /**
     * Retrieves amount of gross taxable non-Merchandise sales.
     *
     * @return amount of gross taxable non-Merchandise sales
     */
    public CurrencyIfc getAmountGrossTaxableNonMerchandiseSales()
    {
        if (amountGrossTaxableNonMerchandiseSales == null)
        {
            amountGrossTaxableNonMerchandiseSales = DomainGateway.getBaseCurrencyInstance();
        }
        return amountGrossTaxableNonMerchandiseSales;
    }

    /**
     * Sets amount of gross taxable non-Merchandise sales.
     *
     * @param value amount of gross taxable non-Merchandise sales
     */
    public void setAmountGrossTaxableNonMerchandiseSales(CurrencyIfc value)
    {
        amountGrossTaxableNonMerchandiseSales = value;
    }

    /**
     * Adds amount of gross taxable non-Merchandise sales.
     *
     * @param value amount of gross taxable non-Merchandise sales
     */
    public void addAmountGrossTaxableNonMerchandiseSales(CurrencyIfc value)
    {
        if (amountGrossTaxableNonMerchandiseSales == null)
        {
            amountGrossTaxableNonMerchandiseSales = value;
        }
        else
        {
            amountGrossTaxableNonMerchandiseSales = amountGrossTaxableNonMerchandiseSales.add(value);
        }
    }

    /**
     * Helper method returns the net taxable non merchandise sales.
     *
     * @return amount of net taxable non-Merchandise sales
     */
    public CurrencyIfc getAmountNetTaxableNonMerchandiseSales()
    {
        return getAmountGrossTaxableNonMerchandiseSales().subtract(getAmountGrossTaxableNonMerchandiseSalesVoided())
                .subtract(
                        getAmountGrossTaxableNonMerchandiseReturns().subtract(
                                getAmountGrossTaxableNonMerchandiseReturnsVoided()));
    }

    /**
     * Retrieves units of gross taxable non-Merchandise sales.
     *
     * @return units of gross taxable non-Merchandise sales
     */
    public BigDecimal getUnitsGrossTaxableNonMerchandiseSales()
    {
        if (unitsGrossTaxableNonMerchandiseSales == null)
        {
            unitsGrossTaxableNonMerchandiseSales = BigDecimal.ZERO;
        }
        return unitsGrossTaxableNonMerchandiseSales;
    }

    /**
     * Sets units of gross taxable non-Merchandise sales.
     *
     * @param value units of gross taxable non-Merchandise sales
     */
    public void setUnitsGrossTaxableNonMerchandiseSales(BigDecimal value)
    {
        unitsGrossTaxableNonMerchandiseSales = value;
    }

    /**
     * Sets units of gross taxable non-Merchandise sales.
     *
     * @param value units of gross taxable non-Merchandise sales
     */
    public void addUnitsGrossTaxableNonMerchandiseSales(BigDecimal value)
    {
        if (unitsGrossTaxableNonMerchandiseSales == null)
        {
            unitsGrossTaxableNonMerchandiseSales = value;
        }
        else
        {
            unitsGrossTaxableNonMerchandiseSales = unitsGrossTaxableNonMerchandiseSales.add(value);
        }
    }

    /**
     * Helper method returns the net taxable non merchandise units.
     *
     * @return units of net taxable non-Merchandise sales
     */
    public BigDecimal getUnitsNetTaxableNonMerchandiseSales()
    {
        return getUnitsGrossTaxableNonMerchandiseSales().subtract(getUnitsGrossTaxableNonMerchandiseSalesVoided())
                .subtract(
                        getUnitsGrossTaxableNonMerchandiseReturns().subtract(
                                getUnitsGrossTaxableNonMerchandiseReturnsVoided()));
    }

    /**
     * Retrieves amount of gross taxable non-Merchandise sales.
     *
     * @return amount of gross taxable non-Merchandise sales
     */
    public CurrencyIfc getAmountGrossNonTaxableNonMerchandiseSales()
    {
        if (amountGrossNonTaxableNonMerchandiseSales == null)
        {
            amountGrossNonTaxableNonMerchandiseSales = DomainGateway.getBaseCurrencyInstance();
        }
        return amountGrossNonTaxableNonMerchandiseSales;
    }

    /**
     * Sets amount of gross taxable non-Merchandise sales.
     *
     * @param value amount of gross taxable non-Merchandise sales
     */
    public void setAmountGrossNonTaxableNonMerchandiseSales(CurrencyIfc value)
    {
        amountGrossNonTaxableNonMerchandiseSales = value;
    }

    /**
     * Adds amount of gross taxable non-Merchandise sales.
     *
     * @param value amount of gross taxable non-Merchandise sales
     */
    public void addAmountGrossNonTaxableNonMerchandiseSales(CurrencyIfc value)
    {
        if (amountGrossNonTaxableNonMerchandiseSales == null)
        {
            amountGrossNonTaxableNonMerchandiseSales = value;
        }
        else
        {
            amountGrossNonTaxableNonMerchandiseSales = amountGrossNonTaxableNonMerchandiseSales.add(value);
        }
    }

    /**
     * Helper method returns the net non-taxable non merchandise sales.
     *
     * @return amount of net non-taxable non-Merchandise sales
     */
    public CurrencyIfc getAmountNetNonTaxableNonMerchandiseSales()
    {
        return getAmountGrossNonTaxableNonMerchandiseSales().subtract(
                getAmountGrossNonTaxableNonMerchandiseSalesVoided()).subtract(
                getAmountGrossNonTaxableNonMerchandiseReturns().subtract(
                        getAmountGrossNonTaxableNonMerchandiseReturnsVoided()));
    }

    /**
     * Retrieves units of gross taxable non-Merchandise sales.
     *
     * @return units of gross taxable non-Merchandise sales
     */
    public BigDecimal getUnitsGrossNonTaxableNonMerchandiseSales()
    {
        if (unitsGrossNonTaxableNonMerchandiseSales == null)
        {
            unitsGrossNonTaxableNonMerchandiseSales = BigDecimal.ZERO;
        }
        return unitsGrossNonTaxableNonMerchandiseSales;
    }

    /**
     * Sets units of gross taxable non-Merchandise sales.
     *
     * @param value units of gross taxable non-Merchandise sales
     */
    public void setUnitsGrossNonTaxableNonMerchandiseSales(BigDecimal value)
    {
        unitsGrossNonTaxableNonMerchandiseSales = value;
    }

    /**
     * adds units of gross taxable non-Merchandise sales.
     *
     * @param value units of gross taxable non-Merchandise sales
     */
    public void addUnitsGrossNonTaxableNonMerchandiseSales(BigDecimal value)
    {
        if (unitsGrossNonTaxableNonMerchandiseSales == null)
        {
            unitsGrossNonTaxableNonMerchandiseSales = value;
        }
        else
        {
            unitsGrossNonTaxableNonMerchandiseSales = unitsGrossNonTaxableNonMerchandiseSales.add(value);
        }
    }

    /**
     * Helper method returns the net non-taxable non merchandise units.
     *
     * @return amount of net non-taxable non-Merchandise sales
     */
    public BigDecimal getUnitsNetNonTaxableNonMerchandiseSales()
    {
        return getUnitsGrossNonTaxableNonMerchandiseSales()
                .subtract(getUnitsGrossNonTaxableNonMerchandiseSalesVoided()).subtract(
                        getUnitsGrossNonTaxableNonMerchandiseReturns().subtract(
                                getUnitsGrossNonTaxableNonMerchandiseReturnsVoided()));
    }

    /**
     * Retrieves amount of gross taxable non-Merchandise returns.
     *
     * @return amount of gross taxable non-Merchandise returns
     */
    public CurrencyIfc getAmountGrossTaxableNonMerchandiseReturns()
    {
        if (amountGrossTaxableNonMerchandiseReturns == null)
        {
            amountGrossTaxableNonMerchandiseReturns = DomainGateway.getBaseCurrencyInstance();
        }
        return amountGrossTaxableNonMerchandiseReturns;
    }

    /**
     * Sets amount of gross taxable non-Merchandise returns.
     *
     * @param value amount of gross taxable non-Merchandise returns
     */
    public void setAmountGrossTaxableNonMerchandiseReturns(CurrencyIfc value)
    {
        amountGrossTaxableNonMerchandiseReturns = value;
    }

    /**
     * Adds amount of gross taxable non-Merchandise returns.
     *
     * @param value amount of gross taxable non-Merchandise returns
     */
    public void addAmountGrossTaxableNonMerchandiseReturns(CurrencyIfc value)
    {
        if (amountGrossTaxableNonMerchandiseReturns == null)
        {
            amountGrossTaxableNonMerchandiseReturns = value;
        }
        else
        {
            amountGrossTaxableNonMerchandiseReturns = amountGrossTaxableNonMerchandiseReturns.add(value);
        }
    }

    /**
     * Retrieves units of gross taxable non-Merchandise returns.
     *
     * @return units of gross taxable non-Merchandise returns
     */
    public BigDecimal getUnitsGrossTaxableNonMerchandiseReturns()
    {
        if (unitsGrossTaxableNonMerchandiseReturns == null)
        {
            unitsGrossTaxableNonMerchandiseReturns = BigDecimal.ZERO;
        }
        return unitsGrossTaxableNonMerchandiseReturns;
    }

    /**
     * Sets units of gross taxable non-Merchandise returns.
     *
     * @param value units of gross taxable non-Merchandise returns
     */
    public void setUnitsGrossTaxableNonMerchandiseReturns(BigDecimal value)
    {
        unitsGrossTaxableNonMerchandiseReturns = value;
    }

    /**
     * Adds units of gross taxable non-Merchandise returns.
     *
     * @param value units of gross taxable non-Merchandise returns
     */
    public void addUnitsGrossTaxableNonMerchandiseReturns(BigDecimal value)
    {
        if (unitsGrossTaxableNonMerchandiseReturns == null)
        {
            unitsGrossTaxableNonMerchandiseReturns = value;
        }
        else
        {
            unitsGrossTaxableNonMerchandiseReturns = unitsGrossTaxableNonMerchandiseReturns.add(value);
        }
    }

    /**
     * Retrieves amount of gross non taxable non-Merchandise Returns.
     *
     * @return amount of gross non taxable non-Merchandise Returns
     */
    public CurrencyIfc getAmountGrossNonTaxableNonMerchandiseReturns()
    {
        if (amountGrossNonTaxableNonMerchandiseReturns == null)
        {
            amountGrossNonTaxableNonMerchandiseReturns = DomainGateway.getBaseCurrencyInstance();
        }
        return amountGrossNonTaxableNonMerchandiseReturns;
    }

    /**
     * Sets amount of gross non taxable non-Merchandise Returns.
     *
     * @param value amount of gross non taxable non-Merchandise Returns
     */
    public void setAmountGrossNonTaxableNonMerchandiseReturns(CurrencyIfc value)
    {
        amountGrossNonTaxableNonMerchandiseReturns = value;
    }

    /**
     * Adds amount of gross non taxable non-Merchandise Returns.
     *
     * @param value amount of gross non taxable non-Merchandise Returns
     */
    public void addAmountGrossNonTaxableNonMerchandiseReturns(CurrencyIfc value)
    {
        if (amountGrossNonTaxableNonMerchandiseReturns == null)
        {
            amountGrossNonTaxableNonMerchandiseReturns = value;
        }
        else
        {
            amountGrossNonTaxableNonMerchandiseReturns = amountGrossNonTaxableNonMerchandiseReturns.add(value);
        }
    }

    /**
     * Retrieves units of gross non taxable non-Merchandise Returns.
     *
     * @return units of gross non taxable non-Merchandise Returns
     */
    public BigDecimal getUnitsGrossNonTaxableNonMerchandiseReturns()
    {
        if (unitsGrossNonTaxableNonMerchandiseReturns == null)
        {
            unitsGrossNonTaxableNonMerchandiseReturns = BigDecimal.ZERO;
        }
        return unitsGrossNonTaxableNonMerchandiseReturns;
    }

    /**
     * Sets units of gross non taxable non-Merchandise Returns.
     *
     * @param value units of gross non taxable non-Merchandise Returns
     */
    public void setUnitsGrossNonTaxableNonMerchandiseReturns(BigDecimal value)
    {
        unitsGrossNonTaxableNonMerchandiseReturns = value;
    }

    /**
     * Adds units of gross non taxable non-Merchandise Returns.
     *
     * @param value units of gross non taxable non-Merchandise Returns
     */
    public void addUnitsGrossNonTaxableNonMerchandiseReturns(BigDecimal value)
    {
        if (unitsGrossNonTaxableNonMerchandiseReturns == null)
        {
            unitsGrossNonTaxableNonMerchandiseReturns = value;
        }
        else
        {
            unitsGrossNonTaxableNonMerchandiseReturns = unitsGrossNonTaxableNonMerchandiseReturns.add(value);
        }
    }

    /**
     * Retrieves amount of gross gift card item Sales.
     *
     * @return amount of gross gift card item Sales
     */
    public CurrencyIfc getAmountGrossGiftCardItemSales()
    {
        if (amountGrossGiftCardItemSales == null)
        {
            amountGrossGiftCardItemSales = DomainGateway.getBaseCurrencyInstance();
        }
        return amountGrossGiftCardItemSales;
    }

    /**
     * Sets amount of gross gift card item Sales.
     *
     * @param value amount of gross gift card item Sales
     */
    public void setAmountGrossGiftCardItemSales(CurrencyIfc value)
    {
        amountGrossGiftCardItemSales = value;
    }

    /**
     * Adds amount of gross gift card item Sales.
     *
     * @param value amount of gross gift card item Sales
     */
    public void addAmountGrossGiftCardItemSales(CurrencyIfc value)
    {
        if (amountGrossGiftCardItemSales == null)
        {
            amountGrossGiftCardItemSales = value;
        }
        else
        {
            amountGrossGiftCardItemSales = amountGrossGiftCardItemSales.add(value);
        }
    }

    /**
     * Retrieves amount of net gift card item Sales.
     *
     * @return amount of gross gift card item Sales
     */
    public CurrencyIfc getAmountNetGiftCardItemSales()
    {
        return getAmountGrossGiftCardItemSales().subtract(getAmountGrossGiftCardItemSalesVoided()).subtract(
                getAmountGrossGiftCardItemReturns().subtract(getAmountGrossGiftCardItemReturnsVoided()));
    }

    /**
     * Retrieves units of gross gift card item Sales.
     *
     * @return units of gross gift card item Sales
     */
    public BigDecimal getUnitsGrossGiftCardItemSales()
    {
        if (unitsGrossGiftCardItemSales == null)
        {
            unitsGrossGiftCardItemSales = BigDecimal.ZERO;
        }
        return unitsGrossGiftCardItemSales;
    }

    /**
     * Sets units of gross gift card item Sales.
     *
     * @param value units of gross gift card item Sales
     */
    public void setUnitsGrossGiftCardItemSales(BigDecimal value)
    {
        unitsGrossGiftCardItemSales = value;
    }

    /**
     * Adds units of gross gift card item Sales.
     *
     * @param value units of gross gift card item Sales
     */
    public void addUnitsGrossGiftCardItemSales(BigDecimal value)
    {
        if (unitsGrossGiftCardItemSales == null)
        {
            unitsGrossGiftCardItemSales = value;
        }
        else
        {
            unitsGrossGiftCardItemSales = unitsGrossGiftCardItemSales.add(value);
        }
    }

    /**
     * Retrieves units of net gift card item Sales.
     *
     * @return units of gross gift card item Sales
     */
    public BigDecimal getUnitsNetGiftCardItemSales()
    {
        return getUnitsGrossGiftCardItemSales().subtract(getUnitsGrossGiftCardItemSalesVoided()).subtract(
                getUnitsGrossGiftCardItemReturns().subtract(getUnitsGrossGiftCardItemReturnsVoided()));
    }

    /**
     * Retrieves amount of gross gift card item Returns.
     *
     * @return amount of gross gift card item Returns
     */
    public CurrencyIfc getAmountGrossGiftCardItemReturns()
    {
        if (amountGrossGiftCardItemReturns == null)
        {
            amountGrossGiftCardItemReturns = DomainGateway.getBaseCurrencyInstance();
        }
        return amountGrossGiftCardItemReturns;
    }

    /**
     * Sets amount of gross gift card item Returns.
     *
     * @param value amount of gross gift card item Returns
     */
    public void setAmountGrossGiftCardItemReturns(CurrencyIfc value)
    {
        amountGrossGiftCardItemReturns = value;
    }

    /**
     * Adds amount of gross gift card item Returns.
     *
     * @param value amount of gross gift card item Returns
     */
    public void addAmountGrossGiftCardItemReturns(CurrencyIfc value)
    {
        if (amountGrossGiftCardItemReturns == null)
        {
            amountGrossGiftCardItemReturns = value;
        }
        else
        {
            amountGrossGiftCardItemReturns = amountGrossGiftCardItemReturns.add(value);
        }
    }

    /**
     * Retrieves units of gross gift card item Returns.
     *
     * @return units of gross gift card item Returns
     */
    public BigDecimal getUnitsGrossGiftCardItemReturns()
    {
        if (unitsGrossGiftCardItemReturns == null)
        {
            unitsGrossGiftCardItemReturns = BigDecimal.ZERO;
        }
        return unitsGrossGiftCardItemReturns;
    }

    /**
     * Sets units of gross gift card item Returns.
     *
     * @param value units of gross gift card item Returns
     */
    public void setUnitsGrossGiftCardItemReturns(BigDecimal value)
    {
        unitsGrossGiftCardItemReturns = value;
    }

    /**
     * Adds units of gross gift card item Returns.
     *
     * @param value units of gross gift card item Returns
     */
    public void addUnitsGrossGiftCardItemReturns(BigDecimal value)
    {
        if (unitsGrossGiftCardItemReturns == null)
        {
            unitsGrossGiftCardItemReturns = value;
        }
        else
        {
            unitsGrossGiftCardItemReturns = unitsGrossGiftCardItemReturns.add(value);
        }
    }

    /**
     * Gets amount gross Gift Card Item Issued
     *
     * @return CurrencyIfc amount
     */
    public CurrencyIfc getAmountGrossGiftCardItemIssued()
    {
        if (amountGrossGiftCardItemIssued == null)
        {
            amountGrossGiftCardItemIssued = DomainGateway.getBaseCurrencyInstance();
        }
        return amountGrossGiftCardItemIssued;
    }

    /* (non-Javadoc)
     * @see oracle.retail.stores.domain.financial.FinancialTotalsIfc#getAmountGrossGiftCardItem()
     */
    public CurrencyIfc getAmountGrossGiftCardItem()
    {
        return getAmountGrossGiftCardItemIssued().subtract(getAmountGrossGiftCardItemIssueVoided());
    }

    /**
     * Sets amount gross Gift Card Item Issued
     *
     * @param value CurrencyIfc new value
     */
    public void setAmountGrossGiftCardItemIssued(CurrencyIfc value)
    {
        amountGrossGiftCardItemIssued = value;
    }

    /**
     * Adds amount gross Gift Card Item Issued
     *
     * @param value BigDecimal units to add
     */
    public void addAmountGrossGiftCardItemIssued(CurrencyIfc value)
    {
        if (amountGrossGiftCardItemIssued == null)
        {
            amountGrossGiftCardItemIssued = value;
        }
        else
        {
            amountGrossGiftCardItemIssued = amountGrossGiftCardItemIssued.add(value);
        }
    }

    /**
     * Gets unit gross Gift Card Item Issued
     *
     * @return value BigDecimal amount
     */
    public BigDecimal getUnitsGrossGiftCardItemIssued()
    {
        if (unitsGrossGiftCardItemIssued == null)
        {
            unitsGrossGiftCardItemIssued = BigDecimal.ZERO;
        }
        return unitsGrossGiftCardItemIssued;
    }

    /**
     * Sets unit gross Gift Card Item Issued
     *
     * @param value BigDecimal new value
     */
    public void setUnitsGrossGiftCardItemIssued(BigDecimal value)
    {
        unitsGrossGiftCardItemIssued = value;
    }

    /**
     * Adds unit gross Gift Card Item Issued
     *
     * @param value BigDecimal units to add
     */
    public void addUnitsGrossGiftCardItemIssued(BigDecimal value)
    {
        if (unitsGrossGiftCardItemIssued == null)
        {
            unitsGrossGiftCardItemIssued = value;
        }
        else
        {
            unitsGrossGiftCardItemIssued = unitsGrossGiftCardItemIssued.add(value);
        }
    }

    /**
     * Gets amount gross Gift Card Item Issue Void
     *
     * @return CurrencyIfc amount
     */
    public CurrencyIfc getAmountGrossGiftCardItemIssueVoided()
    {
        if (amountGrossGiftCardItemIssueVoided == null)
        {
            amountGrossGiftCardItemIssueVoided = DomainGateway.getBaseCurrencyInstance();
        }
        return amountGrossGiftCardItemIssueVoided;
    }

    /**
     * Sets amount gross Gift Card Item Issue Voided
     *
     * @param value CurrencyIfc new value
     */
    public void setAmountGrossGiftCardItemIssueVoided(CurrencyIfc value)
    {
        amountGrossGiftCardItemIssueVoided = value;
    }

    /**
     * Adds amount gross Gift Card Item Issue Voided
     *
     * @param value CurrencyIfc amount to add
     */
    public void addAmountGrossGiftCardItemIssueVoided(CurrencyIfc value)
    {
        if (amountGrossGiftCardItemIssueVoided == null)
        {
            amountGrossGiftCardItemIssueVoided = value;
        }
        else
        {
            amountGrossGiftCardItemIssueVoided = amountGrossGiftCardItemIssueVoided.add(value);
        }
    }

    /**
     * Gets units gross Gift Card Item Issue Voided
     *
     * @return BigDecimal amount
     */
    public BigDecimal getUnitsGrossGiftCardItemIssueVoided()
    {
        if (unitsGrossGiftCardItemIssueVoided == null)
        {
            unitsGrossGiftCardItemIssueVoided = BigDecimal.ZERO;
        }
        return unitsGrossGiftCardItemIssueVoided;
    }

    /**
     * Sets units gross Gift Card Item Issue Voided
     *
     * @param value BigDecimal new value
     */
    public void setUnitsGrossGiftCardItemIssueVoided(BigDecimal value)
    {
        unitsGrossGiftCardItemIssueVoided = value;
    }

    /**
     * Adds units gross Gift Card Item Issue Voided
     *
     * @param value BigDecimal units to add
     */
    public void addUnitsGrossGiftCardItemIssueVoided(BigDecimal value)
    {
        if (unitsGrossGiftCardItemIssueVoided == null)
        {
            unitsGrossGiftCardItemIssueVoided = value;
        }
        else
        {
            unitsGrossGiftCardItemIssueVoided = unitsGrossGiftCardItemIssueVoided.add(value);
        }
    }

    /**
     * Gets amount gross Gift Card Item Reload
     *
     * @return CurrencyIfc amount
     */
    public CurrencyIfc getAmountGrossGiftCardItemReloaded()
    {
        if (amountGrossGiftCardItemReloaded == null)
        {
            amountGrossGiftCardItemReloaded = DomainGateway.getBaseCurrencyInstance();
        }
        return amountGrossGiftCardItemReloaded;
    }

    /**
     * Sets amount gross Gift Card Item Reload
     *
     * @param value amount of gross gift card item reloaded
     */
    public void setAmountGrossGiftCardItemReloaded(CurrencyIfc value)
    {
        amountGrossGiftCardItemReloaded = value;
    }

    /**
     * Adds amount gross Gift Card Item Reload
     *
     * @param value The amount to add to gift card item reloads
     */
    public void addAmountGrossGiftCardItemReloaded(CurrencyIfc value)
    {
        if (amountGrossGiftCardItemReloaded == null)
        {
            amountGrossGiftCardItemReloaded = value;
        }
        else
        {
            amountGrossGiftCardItemReloaded = amountGrossGiftCardItemReloaded.add(value);
        }
    }

    /**
     * Retrive the net amount of GiftCardItems reloads The net result is defined
     * as gross reloads - voids.
     *
     * @return The net amount of gift card items reloaded
     */
    public CurrencyIfc getNetAmountGiftCardItemReloaded()
    {
        return getAmountGrossGiftCardItemReloaded().subtract(getAmountGrossGiftCardItemReloadVoided());
    }

    /**
     * Retrive the net amount of GiftCardItemReloaded. The net result is defined
     * as gross amount - voids.
     *
     * @return The net amount of transaction employee discount
     */
    public BigDecimal getNetUnitsGiftCardItemReloaded()
    {
        return getUnitsGrossGiftCardItemReloaded().subtract(getUnitsGrossGiftCardItemReloadVoided());
    }

    /**
     * Gets units gross Gift Card Item Reload
     *
     * @return BigDecimal value
     */
    public BigDecimal getUnitsGrossGiftCardItemReloaded()
    {
        if (unitsGrossGiftCardItemReloaded == null)
        {
            unitsGrossGiftCardItemReloaded = BigDecimal.ZERO;
        }
        return unitsGrossGiftCardItemReloaded;
    }

    /**
     * Sets units gross Gift Card Item Reload
     *
     * @param value BigDecimal value to set for gift card reloads
     */
    public void setUnitsGrossGiftCardItemReloaded(BigDecimal value)
    {
        unitsGrossGiftCardItemReloaded = value;
    }

    /**
     * Add units gross Gift Card Item Reload
     *
     * @param value BigDecimal value to add to the units of gift card items
     *            reloaded
     */
    public void addUnitsGrossGiftCardItemReloaded(BigDecimal value)
    {
        if (unitsGrossGiftCardItemReloaded == null)
        {
            unitsGrossGiftCardItemReloaded = value;
        }
        else
        {
            unitsGrossGiftCardItemReloaded = unitsGrossGiftCardItemReloaded.add(value);
        }
    }

    /**
     * Gets amount gross Gift Card Item Reload Voided
     *
     * @return CurrencyIfc amount
     */
    public CurrencyIfc getAmountGrossGiftCardItemReloadVoided()
    {
        if (amountGrossGiftCardItemReloadVoided == null)
        {
            amountGrossGiftCardItemReloadVoided = DomainGateway.getBaseCurrencyInstance();
        }
        return amountGrossGiftCardItemReloadVoided;
    }

    /**
     * Sets amount gross Gift Card Item Reload Voided
     *
     * @param value CurrencyIfc amount
     */
    public void setAmountGrossGiftCardItemReloadVoided(CurrencyIfc value)
    {
        amountGrossGiftCardItemReloadVoided = value;
    }

    /**
     * Add amount gross Gift Card Item Reload Voided
     *
     * @param value CurrencyIfc amount
     */
    public void addAmountGrossGiftCardItemReloadVoided(CurrencyIfc value)
    {
        if (amountGrossGiftCardItemReloadVoided == null)
        {
            amountGrossGiftCardItemReloadVoided = value;
        }
        else
        {
            amountGrossGiftCardItemReloadVoided = amountGrossGiftCardItemReloadVoided.add(value);
        }
    }

    /**
     * Gets units gross Gift Card Item Reload Voided
     *
     * @return BigDecimal amount
     */
    public BigDecimal getUnitsGrossGiftCardItemReloadVoided()
    {
        if (unitsGrossGiftCardItemReloadVoided == null)
        {
            unitsGrossGiftCardItemReloadVoided = BigDecimal.ZERO;
        }
        return unitsGrossGiftCardItemReloadVoided;
    }

    /**
     * Sets units gross Gift Card Item Reload Voided
     *
     * @param value BigDecimal amount
     */
    public void setUnitsGrossGiftCardItemReloadVoided(BigDecimal value)
    {
        unitsGrossGiftCardItemReloadVoided = value;
    }

    /**
     * Adds units gross Gift Card Item Reload Voided
     *
     * @param value BigDecimal amount
     */
    public void addUnitsGrossGiftCardItemReloadVoided(BigDecimal value)
    {
        if (unitsGrossGiftCardItemReloadVoided == null)
        {
            unitsGrossGiftCardItemReloadVoided = value;
        }
        else
        {
            unitsGrossGiftCardItemReloadVoided = unitsGrossGiftCardItemReloadVoided.add(value);
        }
    }

    /**
     * gift card redeem
     *
     * @return gross amount of gift card items redeemed
     */
    public CurrencyIfc getAmountGrossGiftCardItemRedeemed()
    {
        if (amountGrossGiftCardItemRedeemed == null)
        {
            amountGrossGiftCardItemRedeemed = DomainGateway.getBaseCurrencyInstance();
        }
        return amountGrossGiftCardItemRedeemed;
    }

    /**
     * Set the amount of gross gift card items redeemed
     *
     * @param value amount of gift card items redeemed
     * @see oracle.retail.stores.domain.financial.FinancialTotalsIfc#setAmountGrossGiftCardItemRedeemed(oracle.retail.stores.domain.currency.CurrencyIfc)
     */
    public void setAmountGrossGiftCardItemRedeemed(CurrencyIfc value)
    {
        amountGrossGiftCardItemRedeemed = value;
    }

    /**
     * Add to the amount of gift card item redeems already stored in financial
     * totals
     *
     * @param value Amount to add to the already existing gift card item redeem
     *            amount
     * @see oracle.retail.stores.domain.financial.FinancialTotalsIfc#addAmountGrossGiftCardItemRedeemed(oracle.retail.stores.domain.currency.CurrencyIfc)
     */
    public void addAmountGrossGiftCardItemRedeemed(CurrencyIfc value)
    {
        if (amountGrossGiftCardItemRedeemed == null)
        {
            amountGrossGiftCardItemRedeemed = value;
        }
        else
        {
            amountGrossGiftCardItemRedeemed = amountGrossGiftCardItemRedeemed.add(value);
        }
    }

    /**
     * Retrive the net amount of GiftCardItems redeems The net result is defined
     * as gross redeems - voids.
     *
     * @return The net amount of gift card items redeems
     */
    public CurrencyIfc getNetAmountGiftCardItemRedeemed()
    {
        return getAmountGrossGiftCardItemRedeemed().subtract(getAmountGrossGiftCardItemRedeemedVoided());
    }

    /**
     * Retrive the net amount of GiftCardItemRedeemed. The net result is defined
     * as gross amount - voids.
     *
     * @return The net amount of transaction employee discount
     */
    public BigDecimal getNetUnitsGiftCardItemRedeemed()
    {
        return getUnitsGrossGiftCardItemRedeemed().subtract(getUnitsGrossGiftCardItemRedeemedVoided());
    }

    /**
     * Get the number of Gross Gift Card Items Redeemed
     *
     * @return units of Gross Gift Card Items Redeemed
     * @see oracle.retail.stores.domain.financial.FinancialTotalsIfc#getUnitsGrossGiftCardItemRedeemed()
     */
    public BigDecimal getUnitsGrossGiftCardItemRedeemed()
    {
        if (unitsGrossGiftCardItemRedeemed == null)
        {
            unitsGrossGiftCardItemRedeemed = BigDecimal.ZERO;
        }
        return unitsGrossGiftCardItemRedeemed;
    }

    /**
     * Set the number of Gross Gift Card Items Redeemed
     *
     * @param value Number of gross gift card items that were redeemed
     * @see oracle.retail.stores.domain.financial.FinancialTotalsIfc#setUnitsGrossGiftCardItemRedeemed(com.ibm.math.BigDecimal)
     */
    public void setUnitsGrossGiftCardItemRedeemed(BigDecimal value)
    {
        unitsGrossGiftCardItemRedeemed = value;
    }

    /**
     * Add to the number of gross gift card items that were redeemed
     *
     * @param value Number of units to add to the total of gift card items that
     *            were redeemed
     * @see oracle.retail.stores.domain.financial.FinancialTotalsIfc#addUnitsGrossGiftCardItemRedeemed(com.ibm.math.BigDecimal)
     */
    public void addUnitsGrossGiftCardItemRedeemed(BigDecimal value)
    {
        if (unitsGrossGiftCardItemRedeemed == null)
        {
            unitsGrossGiftCardItemRedeemed = value;
        }
        else
        {
            unitsGrossGiftCardItemRedeemed = unitsGrossGiftCardItemRedeemed.add(value);
        }
    }

    /**
     * Get the amount of gross gift card item redeems that were voided
     *
     * @return amountGiftCardItemRedeemedVoided
     * @see oracle.retail.stores.domain.financial.FinancialTotalsIfc#getAmountGrossGiftCardItemRedeemedVoided()
     */
    public CurrencyIfc getAmountGrossGiftCardItemRedeemedVoided()
    {
        if (amountGrossGiftCardItemRedeemedVoided == null)
        {
            amountGrossGiftCardItemRedeemedVoided = DomainGateway.getBaseCurrencyInstance();
        }
        return amountGrossGiftCardItemRedeemedVoided;
    }

    /**
     * Set the amount of gift card item redeems that were voided
     *
     * @param value Amount of gift card item redeems voided
     * @see oracle.retail.stores.domain.financial.FinancialTotalsIfc#setAmountGrossGiftCardItemRedeemedVoided(oracle.retail.stores.domain.currency.CurrencyIfc)
     */
    public void setAmountGrossGiftCardItemRedeemedVoided(CurrencyIfc value)
    {
        amountGrossGiftCardItemRedeemedVoided = value;
    }

    /**
     * Add to the existing amount of gift card item redeems voided
     *
     * @param value Amount to add to the existing amount
     * @see oracle.retail.stores.domain.financial.FinancialTotalsIfc#addAmountGrossGiftCardItemRedeemedVoided(oracle.retail.stores.domain.currency.CurrencyIfc)
     */
    public void addAmountGrossGiftCardItemRedeemedVoided(CurrencyIfc value)
    {
        if (amountGrossGiftCardItemRedeemedVoided == null)
        {
            amountGrossGiftCardItemRedeemedVoided = value;
        }
        else
        {
            amountGrossGiftCardItemRedeemedVoided = amountGrossGiftCardItemRedeemedVoided.add(value);
        }
    }

    /**
     * Get the number of gross gift card item redeems voided
     *
     * @return units of gross gift card item redeems voided
     * @see oracle.retail.stores.domain.financial.FinancialTotalsIfc#getUnitsGrossGiftCardItemRedeemedVoided()
     */
    public BigDecimal getUnitsGrossGiftCardItemRedeemedVoided()
    {
        if (unitsGrossGiftCardItemRedeemedVoided == null)
        {
            unitsGrossGiftCardItemRedeemedVoided = BigDecimal.ZERO;
        }
        return unitsGrossGiftCardItemRedeemedVoided;
    }

    /**
     * Set the number of gross gift card item redeems voided
     *
     * @param value Number of gift card item redeems voided
     * @see oracle.retail.stores.domain.financial.FinancialTotalsIfc#setUnitsGrossGiftCardItemRedeemedVoided(com.ibm.math.BigDecimal)
     */
    public void setUnitsGrossGiftCardItemRedeemedVoided(BigDecimal value)
    {
        unitsGrossGiftCardItemRedeemedVoided = value;
    }

    /**
     * Add to the number of gift card item redeems voided
     *
     * @param value Number to add to the existing count of gross gift card item
     *            redeems voided
     * @see oracle.retail.stores.domain.financial.FinancialTotalsIfc#addUnitsGrossGiftCardItemRedeemedVoided(com.ibm.math.BigDecimal)
     */
    public void addUnitsGrossGiftCardItemRedeemedVoided(BigDecimal value)
    {
        if (unitsGrossGiftCardItemRedeemedVoided == null)
        {
            unitsGrossGiftCardItemRedeemedVoided = value;
        }
        else
        {
            unitsGrossGiftCardItemRedeemedVoided = unitsGrossGiftCardItemRedeemedVoided.add(value);
        }
    }

    /**
     * Retrieves amount of Transaction Discount Store Coupons.
     *
     * @return amount of Transaction Discount Store Coupons
     */
    public CurrencyIfc getAmountTransactionDiscStoreCoupons()
    {
        if (amountTransactionDiscStoreCoupons == null)
        {
            amountTransactionDiscStoreCoupons = DomainGateway.getBaseCurrencyInstance();
        }
        return amountTransactionDiscStoreCoupons;
    }

    /**
     * Sets amount of Transaction Discount Store Coupons.
     *
     * @param value amount of Transaction Discount Store Coupons
     */
    public void setAmountTransactionDiscStoreCoupons(CurrencyIfc value)
    {
        amountTransactionDiscStoreCoupons = value;
    }

    /**
     * Adds to amount of Transaction Discount Store Coupons.
     *
     * @param value increment amount of Transaction Discount Store Coupons
     */
    public void addAmountTransactionDiscStoreCoupons(CurrencyIfc value)
    {
        if (amountTransactionDiscStoreCoupons == null)
        {
            amountTransactionDiscStoreCoupons = value;
        }
        else
        {
            amountTransactionDiscStoreCoupons = amountTransactionDiscStoreCoupons.add(value);
        }
    }

    /**
     * Retrieves number of Store Discount Coupons.
     *
     * @return number of Store Discount Coupons
     */
    public int getNumberTransactionDiscStoreCoupons()
    {
        return numberTransactionDiscStoreCoupons;
    }

    /**
     * Sets number of Transaction Discount Store Coupons.
     *
     * @param value number of Transaction Discount Store Coupons
     */
    public void setNumberTransactionDiscStoreCoupons(int value)
    {
        numberTransactionDiscStoreCoupons = value;
    }

    /**
     * Adds to number of Transaction Discount Store Coupons.
     *
     * @param value increment number of Transaction Discount Store Coupons
     */
    public void addNumberTransactionDiscStoreCoupons(int value)
    {
        numberTransactionDiscStoreCoupons = numberTransactionDiscStoreCoupons + value;
    }

    /**
     * Retrieves amount of Item Discount Store Coupons.
     *
     * @return amount of Item Discount Store Coupons
     */
    public CurrencyIfc getAmountItemDiscStoreCoupons()
    {
        if (amountItemDiscStoreCoupons == null)
        {
            amountItemDiscStoreCoupons = DomainGateway.getBaseCurrencyInstance();
        }
        return amountItemDiscStoreCoupons;
    }

    /**
     * Sets amount of Item Store Coupons.
     *
     * @param value amount of Item StoreCoupons
     */
    public void setAmountItemDiscStoreCoupons(CurrencyIfc value)
    {
        amountItemDiscStoreCoupons = value;
    }

    /**
     * Adds to amount of Item Discount Store Coupons.
     *
     * @param value increment amount of Item Discount Store Coupons
     */
    public void addAmountItemDiscStoreCoupons(CurrencyIfc value)
    {
        if (amountItemDiscStoreCoupons == null)
        {
            amountItemDiscStoreCoupons = value;
        }
        else
        {
            amountItemDiscStoreCoupons = amountItemDiscStoreCoupons.add(value);
        }
    }

    /**
     * Retrieves number of Item Discount Store Coupons.
     *
     * @return number of Item Discount Store Coupons
     */
    public int getNumberItemDiscStoreCoupons()
    {
        return numberItemDiscStoreCoupons;
    }

    /**
     * Sets number of Item Discount Store Coupons.
     *
     * @param value number of Item Discount Store Coupons
     */
    public void setNumberItemDiscStoreCoupons(int value)
    {
        numberItemDiscStoreCoupons = value;
    }

    /**
     * Adds to number of Item Discount Store Coupons.
     *
     * @param value increment number of Item Discount Store Coupons
     */
    public void addNumberItemDiscStoreCoupons(int value)
    {
        numberItemDiscStoreCoupons = numberItemDiscStoreCoupons + value;
    }

    /**
     * Sets amount gross Taxable Item Sales Voided
     *
     * @param value CurrencyIfc new value
     */
    public void setAmountGrossTaxableItemSalesVoided(CurrencyIfc value)
    {
        amountGrossTaxableItemSalesVoided = value;
    }

    /**
     * Gets amount gross Taxable Item Sales Voided
     *
     * @return CurrencyIfc amount
     */
    public CurrencyIfc getAmountGrossTaxableItemSalesVoided()
    {
        if (amountGrossTaxableItemSalesVoided == null)
        {
            amountGrossTaxableItemSalesVoided = DomainGateway.getBaseCurrencyInstance();
        }
        return amountGrossTaxableItemSalesVoided;
    }

    /**
     * Adds amount gross Taxable Item Sales Voided
     *
     * @param value CurrencyIfc amount to add
     */
    public void addAmountGrossTaxableItemSalesVoided(CurrencyIfc value)
    {
        if (amountGrossTaxableItemSalesVoided == null)
        {
            amountGrossTaxableItemSalesVoided = value;
        }
        else
        {
            amountGrossTaxableItemSalesVoided = amountGrossTaxableItemSalesVoided.add(value);
        }
    }

    /**
     * Gets units gross Taxable Item Sales Voided
     *
     * @return BigDecimal units
     */
    public BigDecimal getUnitsGrossTaxableItemSalesVoided()
    {
        if (unitsGrossTaxableItemSalesVoided == null)
        {
            unitsGrossTaxableItemSalesVoided = BigDecimal.ZERO;
        }
        return unitsGrossTaxableItemSalesVoided;
    }

    /**
     * Sets units gross Taxable Item Sales Voided
     *
     * @param value BigDecimal new value
     */
    public void setUnitsGrossTaxableItemSalesVoided(BigDecimal value)
    {
        unitsGrossTaxableItemSalesVoided = value;
    }

    /**
     * Adds units gross Taxable Item Sales Voided
     *
     * @param value BigDecimal units to add
     */
    public void addUnitsGrossTaxableItemSalesVoided(BigDecimal value)
    {
        if (unitsGrossTaxableItemSalesVoided == null)
        {
            unitsGrossTaxableItemSalesVoided = value;
        }
        else
        {
            unitsGrossTaxableItemSalesVoided = unitsGrossTaxableItemSalesVoided.add(value);
        }
    }

    /**
     * Gets amount gross Non Taxable Item Sales Voided
     *
     * @return CurrencyIfc amount
     */
    public CurrencyIfc getAmountGrossNonTaxableItemSalesVoided()
    {
        if (amountGrossNonTaxableItemSalesVoided == null)
        {
            amountGrossNonTaxableItemSalesVoided = DomainGateway.getBaseCurrencyInstance();
        }
        return amountGrossNonTaxableItemSalesVoided;
    }

    /**
     * Sets amount gross Non Taxable Item Sales Voided
     *
     * @param value CurrencyIfc new value
     */
    public void setAmountGrossNonTaxableItemSalesVoided(CurrencyIfc value)
    {
        amountGrossNonTaxableItemSalesVoided = value;
    }

    /**
     * Adds amount gross NonTaxable Item Sales Voided
     *
     * @param value CurrencyIfc amount to add
     */
    public void addAmountGrossNonTaxableItemSalesVoided(CurrencyIfc value)
    {
        if (amountGrossNonTaxableItemSalesVoided == null)
        {
            amountGrossNonTaxableItemSalesVoided = value;
        }
        else
        {
            amountGrossNonTaxableItemSalesVoided = amountGrossNonTaxableItemSalesVoided.add(value);
        }
    }

    /**
     * Gets units gross Non Taxable Item Sales Voided
     *
     * @return BigDecimal units
     */
    public BigDecimal getUnitsGrossNonTaxableItemSalesVoided()
    {
        if (unitsGrossNonTaxableItemSalesVoided == null)
        {
            unitsGrossNonTaxableItemSalesVoided = BigDecimal.ZERO;
        }
        return unitsGrossNonTaxableItemSalesVoided;
    }

    /**
     * Sets units gross Non Taxable Item Sales Voided
     *
     * @param value BigDecimal new value
     */
    public void setUnitsGrossNonTaxableItemSalesVoided(BigDecimal value)
    {
        unitsGrossNonTaxableItemSalesVoided = value;
    }

    /**
     * Adds units gross Non Taxable Item Sales Voided
     *
     * @param value BigDecimal units to add
     */
    public void addUnitsGrossNonTaxableItemSalesVoided(BigDecimal value)
    {
        if (unitsGrossNonTaxableItemSalesVoided == null)
        {
            unitsGrossNonTaxableItemSalesVoided = value;
        }
        else
        {
            unitsGrossNonTaxableItemSalesVoided = unitsGrossNonTaxableItemSalesVoided.add(value);
        }
    }

    /**
     * Gets amount gross Tax Exempt Item Sales Voided
     *
     * @return CurrencyIfc amount
     */
    public CurrencyIfc getAmountGrossTaxExemptItemSalesVoided()
    {
        if (amountGrossTaxExemptItemSalesVoided == null)
        {
            amountGrossTaxExemptItemSalesVoided = DomainGateway.getBaseCurrencyInstance();
        }
        return amountGrossTaxExemptItemSalesVoided;
    }

    /**
     * Sets amount gross Tax Exempt Item Sales Voided
     *
     * @param value CurrencyIfc new value
     */
    public void setAmountGrossTaxExemptItemSalesVoided(CurrencyIfc value)
    {
        amountGrossTaxExemptItemSalesVoided = value;
    }

    /**
     * Adds amount gross Tax Exempt Item Sales Voided
     *
     * @param value CurrencyIfc amount to add
     */
    public void addAmountGrossTaxExemptItemSalesVoided(CurrencyIfc value)
    {
        if (amountGrossTaxExemptItemSalesVoided == null)
        {
            amountGrossTaxExemptItemSalesVoided = value;
        }
        else
        {
            amountGrossTaxExemptItemSalesVoided = amountGrossTaxExemptItemSalesVoided.add(value);
        }
    }

    /**
     * Gets units gross Tax Exempt Item Sales Voided
     *
     * @return BigDecimal units
     */
    public BigDecimal getUnitsGrossTaxExemptItemSalesVoided()
    {
        if (unitsGrossTaxExemptItemSalesVoided == null)
        {
            unitsGrossTaxExemptItemSalesVoided = BigDecimal.ZERO;
        }
        return unitsGrossTaxExemptItemSalesVoided;
    }

    /**
     * Sets units gross Tax Exempt Item Sales Voided
     *
     * @param value BigDecimal new value
     */
    public void setUnitsGrossTaxExemptItemSalesVoided(BigDecimal value)
    {
        unitsGrossTaxExemptItemSalesVoided = value;
    }

    /**
     * Adds units gross Tax Exempt Item Sales Voided
     *
     * @param value BigDecimal units to add
     */
    public void addUnitsGrossTaxExemptItemSalesVoided(BigDecimal value)
    {
        if (unitsGrossTaxExemptItemSalesVoided == null)
        {
            unitsGrossTaxExemptItemSalesVoided = value;
        }
        else
        {
            unitsGrossTaxExemptItemSalesVoided = unitsGrossTaxExemptItemSalesVoided.add(value);
        }
    }

    /**
     * Gets amount gross Taxable Item Returns Voided
     *
     * @return CurrencyIfc amount
     */
    public CurrencyIfc getAmountGrossTaxableItemReturnsVoided()
    {
        if (amountGrossTaxableItemReturnsVoided == null)
        {
            amountGrossTaxableItemReturnsVoided = DomainGateway.getBaseCurrencyInstance();
        }
        return amountGrossTaxableItemReturnsVoided;
    }

    /**
     * Sets amount gross Taxable Item Returns Voided
     *
     * @param value CurrencyIfc new value
     */
    public void setAmountGrossTaxableItemReturnsVoided(CurrencyIfc value)
    {
        amountGrossTaxableItemReturnsVoided = value;
    }

    /**
     * Adds amount gross Taxable Item Returns Voided
     *
     * @param value CurrencyIfc amount to add
     */
    public void addAmountGrossTaxableItemReturnsVoided(CurrencyIfc value)
    {
        if (amountGrossTaxableItemReturnsVoided == null)
        {
            amountGrossTaxableItemReturnsVoided = value;
        }
        else
        {
            amountGrossTaxableItemReturnsVoided = amountGrossTaxableItemReturnsVoided.add(value);
        }
    }

    /**
     * Gets units gross Taxable Item Returns Voided
     *
     * @return BigDecimal units
     */
    public BigDecimal getUnitsGrossTaxableItemReturnsVoided()
    {
        if (unitsGrossTaxableItemReturnsVoided == null)
        {
            unitsGrossTaxableItemReturnsVoided = BigDecimal.ZERO;
        }
        return unitsGrossTaxableItemReturnsVoided;
    }

    /**
     * Sets units gross Taxable Item Returns Voided
     *
     * @param value BigDecimal new value
     */
    public void setUnitsGrossTaxableItemReturnsVoided(BigDecimal value)
    {
        unitsGrossTaxableItemReturnsVoided = value;
    }

    /**
     * Adds units gross Taxable Item Returns Voided
     *
     * @param value BigDecimal units to add
     */
    public void addUnitsGrossTaxableItemReturnsVoided(BigDecimal value)
    {
        if (unitsGrossTaxableItemReturnsVoided == null)
        {
            unitsGrossTaxableItemReturnsVoided = value;
        }
        else
        {
            unitsGrossTaxableItemReturnsVoided = unitsGrossTaxableItemReturnsVoided.add(value);
        }
    }

    /**
     * Gets amount gross Non Taxable Item Returns Voided
     *
     * @return CurrencyIfc amount
     */
    public CurrencyIfc getAmountGrossNonTaxableItemReturnsVoided()
    {
        if (amountGrossNonTaxableItemReturnsVoided == null)
        {
            amountGrossNonTaxableItemReturnsVoided = DomainGateway.getBaseCurrencyInstance();
        }
        return amountGrossNonTaxableItemReturnsVoided;
    }

    /**
     * Sets amount gross Non Taxable Item Returns Voided
     *
     * @param value CurrencyIfc new value
     */
    public void setAmountGrossNonTaxableItemReturnsVoided(CurrencyIfc value)
    {
        amountGrossNonTaxableItemReturnsVoided = value;
    }

    /**
     * Adds amount gross Non Taxable Item Returns Voided
     *
     * @param value CurrencyIfc amount to add
     */
    public void addAmountGrossNonTaxableItemReturnsVoided(CurrencyIfc value)
    {
        if (amountGrossNonTaxableItemReturnsVoided == null)
        {
            amountGrossNonTaxableItemReturnsVoided = value;
        }
        else
        {
            amountGrossNonTaxableItemReturnsVoided = amountGrossNonTaxableItemReturnsVoided.add(value);
        }
    }

    /**
     * Gets units gross Non Taxable Item Returns Voided
     *
     * @return BigDecimal units
     */
    public BigDecimal getUnitsGrossNonTaxableItemReturnsVoided()
    {
        if (unitsGrossNonTaxableItemReturnsVoided == null)
        {
            unitsGrossNonTaxableItemReturnsVoided = BigDecimal.ZERO;
        }
        return unitsGrossNonTaxableItemReturnsVoided;
    }

    /**
     * Sets units gross Non Taxable Item Returns Voided
     *
     * @param value BigDecimal new value
     */
    public void setUnitsGrossNonTaxableItemReturnsVoided(BigDecimal value)
    {
        unitsGrossNonTaxableItemReturnsVoided = value;
    }

    /**
     * Adds units gross Non Taxable Item Returns Voided
     *
     * @param value BigDecimal units to add
     */
    public void addUnitsGrossNonTaxableItemReturnsVoided(BigDecimal value)
    {
        if (unitsGrossNonTaxableItemReturnsVoided == null)
        {
            unitsGrossNonTaxableItemReturnsVoided = value;
        }
        else
        {
            unitsGrossNonTaxableItemReturnsVoided = unitsGrossNonTaxableItemReturnsVoided.add(value);
        }
    }

    /**
     * Gets amount gross Tax Exempt Item Returns Voided
     *
     * @return CurrencyIfc amount
     */
    public CurrencyIfc getAmountGrossTaxExemptItemReturnsVoided()
    {
        if (amountGrossTaxExemptItemReturnsVoided == null)
        {
            amountGrossTaxExemptItemReturnsVoided = DomainGateway.getBaseCurrencyInstance();
        }
        return amountGrossTaxExemptItemReturnsVoided;
    }

    /**
     * Sets amount gross Tax Exempt Item Returns Voided
     *
     * @param value CurrencyIfc new value
     */
    public void setAmountGrossTaxExemptItemReturnsVoided(CurrencyIfc value)
    {
        amountGrossTaxExemptItemReturnsVoided = value;
    }

    /**
     * Adds amount gross Tax Exempt Item Returns Voided
     *
     * @param value CurrencyIfc amount to add
     */
    public void addAmountGrossTaxExemptItemReturnsVoided(CurrencyIfc value)
    {
        if (amountGrossTaxExemptItemReturnsVoided == null)
        {
            amountGrossTaxExemptItemReturnsVoided = value;
        }
        else
        {
            amountGrossTaxExemptItemReturnsVoided = amountGrossTaxExemptItemReturnsVoided.add(value);
        }
    }

    /**
     * Gets units gross Tax Exempt Item Returns Voided
     *
     * @return BigDecimal units
     */
    public BigDecimal getUnitsGrossTaxExemptItemReturnsVoided()
    {
        if (unitsGrossTaxExemptItemReturnsVoided == null)
        {
            unitsGrossTaxExemptItemReturnsVoided = BigDecimal.ZERO;
        }
        return unitsGrossTaxExemptItemReturnsVoided;
    }

    /**
     * Sets units gross Tax Exempt Item Returns Voided
     *
     * @param value BigDecimal new value
     */
    public void setUnitsGrossTaxExemptItemReturnsVoided(BigDecimal value)
    {
        unitsGrossTaxExemptItemReturnsVoided = value;
    }

    /**
     * Adds units gross Tax Exempt Item Returns Voided
     *
     * @param value BigDecimal units to add
     */
    public void addUnitsGrossTaxExemptItemReturnsVoided(BigDecimal value)
    {
        if (unitsGrossTaxExemptItemReturnsVoided == null)
        {
            unitsGrossTaxExemptItemReturnsVoided = value;
        }
        else
        {
            unitsGrossTaxExemptItemReturnsVoided = unitsGrossTaxExemptItemReturnsVoided.add(value);
        }
    }

    /**
     * Gets amount gross Taxable Non Merchandise Sales Voided
     *
     * @return CurrencyIfc amount
     */
    public CurrencyIfc getAmountGrossTaxableNonMerchandiseSalesVoided()
    {
        if (amountGrossTaxableNonMerchandiseSalesVoided == null)
        {
            amountGrossTaxableNonMerchandiseSalesVoided = DomainGateway.getBaseCurrencyInstance();
        }
        return amountGrossTaxableNonMerchandiseSalesVoided;
    }

    /**
     * Sets amount gross Taxable Non Merchandise Sales Voided
     *
     * @param value CurrencyIfc new value
     */
    public void setAmountGrossTaxableNonMerchandiseSalesVoided(CurrencyIfc value)
    {
        amountGrossTaxableNonMerchandiseSalesVoided = value;
    }

    /**
     * Adds amount gross Taxable Non Merchandise Sales Voided
     *
     * @param value CurrencyIfc amount to add
     */
    public void addAmountGrossTaxableNonMerchandiseSalesVoided(CurrencyIfc value)
    {
        if (amountGrossTaxableNonMerchandiseSalesVoided == null)
        {
            amountGrossTaxableNonMerchandiseSalesVoided = value;
        }
        else
        {
            amountGrossTaxableNonMerchandiseSalesVoided = amountGrossTaxableNonMerchandiseSalesVoided.add(value);
        }
    }

    /**
     * Gets units gross Taxable Non Merchandise Sales Voided
     *
     * @return BigDecimal units
     */
    public BigDecimal getUnitsGrossTaxableNonMerchandiseSalesVoided()
    {
        if (unitsGrossTaxableNonMerchandiseSalesVoided == null)
        {
            unitsGrossTaxableNonMerchandiseSalesVoided = BigDecimal.ZERO;
        }
        return unitsGrossTaxableNonMerchandiseSalesVoided;
    }

    /**
     * Sets units gross Taxable Non Merchandise Sales Voided
     *
     * @param value BigDecimal new value
     */
    public void setUnitsGrossTaxableNonMerchandiseSalesVoided(BigDecimal value)
    {
        unitsGrossTaxableNonMerchandiseSalesVoided = value;
    }

    /**
     * Adds units gross Taxable Non Merchandise Sales Voided
     *
     * @param value BigDecimal units to add
     */
    public void addUnitsGrossTaxableNonMerchandiseSalesVoided(BigDecimal value)
    {
        if (unitsGrossTaxableNonMerchandiseSalesVoided == null)
        {
            unitsGrossTaxableNonMerchandiseSalesVoided = value;
        }
        else
        {
            unitsGrossTaxableNonMerchandiseSalesVoided = unitsGrossTaxableNonMerchandiseSalesVoided.add(value);
        }
    }

    /* (non-Javadoc)
     * @see oracle.retail.stores.domain.financial.FinancialTotalsIfc#getAmountGrossNonMerchandise()
     */
    public CurrencyIfc getAmountGrossNonMerchandise()
    {
        return getAmountGrossNonTaxableNonMerchandiseSales().subtract(
                getAmountGrossNonTaxableNonMerchandiseSalesVoided()).add(
                getAmountGrossTaxableNonMerchandiseSales()).subtract(
                getAmountGrossTaxableNonMerchandiseSalesVoided());
    }

    /**
     * Gets amount gross Non Taxable Non Merchandise Sales Voided
     *
     * @return CurrencyIfc amount
     */
    public CurrencyIfc getAmountGrossNonTaxableNonMerchandiseSalesVoided()
    {
        if (amountGrossNonTaxableNonMerchandiseSalesVoided == null)
        {
            amountGrossNonTaxableNonMerchandiseSalesVoided = DomainGateway.getBaseCurrencyInstance();
        }
        return amountGrossNonTaxableNonMerchandiseSalesVoided;
    }

    /**
     * Sets amount gross Non Taxable Non Merchandise Sales Voided
     *
     * @param value CurrencyIfc new value
     */
    public void setAmountGrossNonTaxableNonMerchandiseSalesVoided(CurrencyIfc value)
    {
        amountGrossNonTaxableNonMerchandiseSalesVoided = value;
    }

    /**
     * Adds amount gross Non Taxable Non Merchandise Sales Voided
     *
     * @param value CurrencyIfc amount to add
     */
    public void addAmountGrossNonTaxableNonMerchandiseSalesVoided(CurrencyIfc value)
    {
        if (amountGrossNonTaxableNonMerchandiseSalesVoided == null)
        {
            amountGrossNonTaxableNonMerchandiseSalesVoided = value;
        }
        else
        {
            amountGrossNonTaxableNonMerchandiseSalesVoided = amountGrossNonTaxableNonMerchandiseSalesVoided.add(value);
        }
    }

    /**
     * Gets units gross Non Taxable Non Merchandise Sales Voided
     *
     * @return BigDecimal units
     */
    public BigDecimal getUnitsGrossNonTaxableNonMerchandiseSalesVoided()
    {
        if (unitsGrossNonTaxableNonMerchandiseSalesVoided == null)
        {
            unitsGrossNonTaxableNonMerchandiseSalesVoided = BigDecimal.ZERO;
        }
        return unitsGrossNonTaxableNonMerchandiseSalesVoided;
    }

    /**
     * Sets units gross Non Taxable Non Merchandise Sales Voided
     *
     * @param value BigDecimal new value
     */
    public void setUnitsGrossNonTaxableNonMerchandiseSalesVoided(BigDecimal value)
    {
        unitsGrossNonTaxableNonMerchandiseSalesVoided = value;
    }

    /**
     * Adds units gross Non Taxable Non Merchandise Sales Voided
     *
     * @param value BigDecimal units to add
     */
    public void addUnitsGrossNonTaxableNonMerchandiseSalesVoided(BigDecimal value)
    {
        if (unitsGrossNonTaxableNonMerchandiseSalesVoided == null)
        {
            unitsGrossNonTaxableNonMerchandiseSalesVoided = value;
        }
        else
        {
            unitsGrossNonTaxableNonMerchandiseSalesVoided = unitsGrossNonTaxableNonMerchandiseSalesVoided.add(value);
        }
    }

    /**
     * Gets amount gross Taxable Non Merchandise Returns Voided
     *
     * @return CurrencyIfc amount
     */
    public CurrencyIfc getAmountGrossTaxableNonMerchandiseReturnsVoided()
    {
        if (amountGrossTaxableNonMerchandiseReturnsVoided == null)
        {
            amountGrossTaxableNonMerchandiseReturnsVoided = DomainGateway.getBaseCurrencyInstance();
        }
        return amountGrossTaxableNonMerchandiseReturnsVoided;
    }

    /**
     * Sets amount gross Taxable Non Merchandise Returns Voided
     *
     * @param value CurrencyIfc new value
     */
    public void setAmountGrossTaxableNonMerchandiseReturnsVoided(CurrencyIfc value)
    {
        amountGrossTaxableNonMerchandiseReturnsVoided = value;
    }

    /**
     * Adds amount gross Taxable Non Merchandise Returns Voided
     *
     * @param value CurrencyIfc amount to add
     */
    public void addAmountGrossTaxableNonMerchandiseReturnsVoided(CurrencyIfc value)
    {
        if (amountGrossTaxableNonMerchandiseReturnsVoided == null)
        {
            amountGrossTaxableNonMerchandiseReturnsVoided = value;
        }
        else
        {
            amountGrossTaxableNonMerchandiseReturnsVoided = amountGrossTaxableNonMerchandiseReturnsVoided.add(value);
        }
    }

    /**
     * Gets units gross Taxable Non Merchandise Returns Voided
     *
     * @return BigDecimal units
     */
    public BigDecimal getUnitsGrossTaxableNonMerchandiseReturnsVoided()
    {
        if (unitsGrossTaxableNonMerchandiseReturnsVoided == null)
        {
            unitsGrossTaxableNonMerchandiseReturnsVoided = BigDecimal.ZERO;
        }
        return unitsGrossTaxableNonMerchandiseReturnsVoided;
    }

    /**
     * Sets units gross Taxable Non Merchandise Returns Voided
     *
     * @param value BigDecimal new value
     */
    public void setUnitsGrossTaxableNonMerchandiseReturnsVoided(BigDecimal value)
    {
        unitsGrossTaxableNonMerchandiseReturnsVoided = value;
    }

    /**
     * Adds units gross Taxable Non Merchandise Returns Voided
     *
     * @param value BigDecimal units to add
     */
    public void addUnitsGrossTaxableNonMerchandiseReturnsVoided(BigDecimal value)
    {
        if (unitsGrossTaxableNonMerchandiseReturnsVoided == null)
        {
            unitsGrossTaxableNonMerchandiseReturnsVoided = value;
        }
        else
        {
            unitsGrossTaxableNonMerchandiseReturnsVoided = unitsGrossTaxableNonMerchandiseReturnsVoided.add(value);
        }
    }

    /**
     * Gets amount gross Non Taxable Non Merchandise Returns Voided
     *
     * @return CurrencyIfc amount
     */
    public CurrencyIfc getAmountGrossNonTaxableNonMerchandiseReturnsVoided()
    {
        if (amountGrossNonTaxableNonMerchandiseReturnsVoided == null)
        {
            amountGrossNonTaxableNonMerchandiseReturnsVoided = DomainGateway.getBaseCurrencyInstance();
        }
        return amountGrossNonTaxableNonMerchandiseReturnsVoided;
    }

    /**
     * Sets amount gross Non Taxable Non Merchandise Returns Voided
     *
     * @param value CurrencyIfc new value
     */
    public void setAmountGrossNonTaxableNonMerchandiseReturnsVoided(CurrencyIfc value)
    {
        amountGrossNonTaxableNonMerchandiseReturnsVoided = value;
    }

    /**
     * Adds amount gross Non Taxable Non Merchandise Returns Voided
     *
     * @param value CurrencyIfc amount to add
     */
    public void addAmountGrossNonTaxableNonMerchandiseReturnsVoided(CurrencyIfc value)
    {
        if (amountGrossNonTaxableNonMerchandiseReturnsVoided == null)
        {
            amountGrossNonTaxableNonMerchandiseReturnsVoided = value;
        }
        else
        {
            amountGrossNonTaxableNonMerchandiseReturnsVoided = amountGrossNonTaxableNonMerchandiseReturnsVoided
                    .add(value);
        }
    }

    /**
     * Gets units gross Non Taxable Non Merchandise Returns Voided
     *
     * @return BigDecimal units
     */
    public BigDecimal getUnitsGrossNonTaxableNonMerchandiseReturnsVoided()
    {
        if (unitsGrossNonTaxableNonMerchandiseReturnsVoided == null)
        {
            unitsGrossNonTaxableNonMerchandiseReturnsVoided = BigDecimal.ZERO;
        }
        return unitsGrossNonTaxableNonMerchandiseReturnsVoided;
    }

    /**
     * Sets units gross Non Taxable Non Merchandise Returns Voided
     *
     * @param value BigDecimal new value
     */
    public void setUnitsGrossNonTaxableNonMerchandiseReturnsVoided(BigDecimal value)
    {
        unitsGrossNonTaxableNonMerchandiseReturnsVoided = value;
    }

    /**
     * Adds units gross Non Taxable Non Merchandise Returns Voided
     *
     * @param value BigDecimal units to add
     */
    public void addUnitsGrossNonTaxableNonMerchandiseReturnsVoided(BigDecimal value)
    {
        if (unitsGrossNonTaxableNonMerchandiseReturnsVoided == null)
        {
            unitsGrossNonTaxableNonMerchandiseReturnsVoided = value;
        }
        else
        {
            unitsGrossNonTaxableNonMerchandiseReturnsVoided = unitsGrossNonTaxableNonMerchandiseReturnsVoided
                    .add(value);
        }
    }

    /**
     * Gets amount gross Gift Card Item Sales Voided
     *
     * @return CurrencyIfc amount
     */
    public CurrencyIfc getAmountGrossGiftCardItemSalesVoided()
    {
        if (amountGrossGiftCardItemSalesVoided == null)
        {
            amountGrossGiftCardItemSalesVoided = DomainGateway.getBaseCurrencyInstance();
        }
        return amountGrossGiftCardItemSalesVoided;
    }

    /**
     * Sets amount gross Gift Card Item Sales Voided
     *
     * @param value CurrencyIfc new value
     */
    public void setAmountGrossGiftCardItemSalesVoided(CurrencyIfc value)
    {
        amountGrossGiftCardItemSalesVoided = value;
    }

    /**
     * Adds amount gross Gift Card Item Sales Voided
     *
     * @param value CurrencyIfc amount to add
     */
    public void addAmountGrossGiftCardItemSalesVoided(CurrencyIfc value)
    {
        if (amountGrossGiftCardItemSalesVoided == null)
        {
            amountGrossGiftCardItemSalesVoided = value;
        }
        else
        {
            amountGrossGiftCardItemSalesVoided = amountGrossGiftCardItemSalesVoided.add(value);
        }
    }

    /**
     * Gets units gross Gift Card Item Sales Voided
     *
     * @return BigDecimal units
     */
    public BigDecimal getUnitsGrossGiftCardItemSalesVoided()
    {
        if (unitsGrossGiftCardItemSalesVoided == null)
        {
            unitsGrossGiftCardItemSalesVoided = BigDecimal.ZERO;
        }
        return unitsGrossGiftCardItemSalesVoided;
    }

    /**
     * Sets units gross Gift Card Item Sales Voided
     *
     * @param value BigDecimal new value
     */
    public void setUnitsGrossGiftCardItemSalesVoided(BigDecimal value)
    {
        unitsGrossGiftCardItemSalesVoided = value;
    }

    /**
     * Adds units gross Gift Card Item Sales Voided
     *
     * @param value BigDecimal units to add
     */
    public void addUnitsGrossGiftCardItemSalesVoided(BigDecimal value)
    {
        if (unitsGrossGiftCardItemSalesVoided == null)
        {
            unitsGrossGiftCardItemSalesVoided = value;
        }
        else
        {
            unitsGrossGiftCardItemSalesVoided = unitsGrossGiftCardItemSalesVoided.add(value);
        }
    }

    /**
     * Gets amount gross Gift Card Item Returns Voided
     *
     * @return CurrencyIfc amount
     */
    public CurrencyIfc getAmountGrossGiftCardItemReturnsVoided()
    {
        if (amountGrossGiftCardItemReturnsVoided == null)
        {
            amountGrossGiftCardItemReturnsVoided = DomainGateway.getBaseCurrencyInstance();
        }
        return amountGrossGiftCardItemReturnsVoided;
    }

    /**
     * Sets amount gross Gift Card Item Returns Voided
     *
     * @param value CurrencyIfc new value
     */
    public void setAmountGrossGiftCardItemReturnsVoided(CurrencyIfc value)
    {
        amountGrossGiftCardItemReturnsVoided = value;
    }

    /**
     * Adds amount gross Gift Card Item Returns Voided
     *
     * @param value CurrencyIfc amount to add
     */
    public void addAmountGrossGiftCardItemReturnsVoided(CurrencyIfc value)
    {
        if (amountGrossGiftCardItemReturnsVoided == null)
        {
            amountGrossGiftCardItemReturnsVoided = value;
        }
        else
        {
            amountGrossGiftCardItemReturnsVoided = amountGrossGiftCardItemReturnsVoided.add(value);
        }
    }

    /**
     * Gets units gross Gift Card Item Returns Voided
     *
     * @return BigDecimal units
     */
    public BigDecimal getUnitsGrossGiftCardItemReturnsVoided()
    {
        if (unitsGrossGiftCardItemReturnsVoided == null)
        {
            unitsGrossGiftCardItemReturnsVoided = BigDecimal.ZERO;
        }
        return unitsGrossGiftCardItemReturnsVoided;
    }

    /**
     * Sets units gross Gift Card Item Returns Voided
     *
     * @param value BigDecimal new value
     */
    public void setUnitsGrossGiftCardItemReturnsVoided(BigDecimal value)
    {
        unitsGrossGiftCardItemReturnsVoided = value;
    }

    /**
     * Adds units gross Gift Card Item Returns Voided
     *
     * @param value BigDecimal units to add
     */
    public void addUnitsGrossGiftCardItemReturnsVoided(BigDecimal value)
    {
        if (unitsGrossGiftCardItemReturnsVoided == null)
        {
            unitsGrossGiftCardItemReturnsVoided = value;
        }
        else
        {
            unitsGrossGiftCardItemReturnsVoided = unitsGrossGiftCardItemReturnsVoided.add(value);
        }
    }

    /**
     * Gets amount gross Taxable Transaction Sales Voided
     *
     * @return CurrencyIfc amount
     */
    public CurrencyIfc getAmountGrossTaxableTransactionSalesVoided()
    {
        if (amountGrossTaxableTransactionSalesVoided == null)
        {
            amountGrossTaxableTransactionSalesVoided = DomainGateway.getBaseCurrencyInstance();
        }
        return amountGrossTaxableTransactionSalesVoided;
    }

    /**
     * Sets amount gross Taxable Transaction Sales Voided
     *
     * @param value CurrencyIfc new value
     */
    public void setAmountGrossTaxableTransactionSalesVoided(CurrencyIfc value)
    {
        amountGrossTaxableTransactionSalesVoided = value;
    }

    /**
     * Adds amount gross Taxable Transaction Sales Voided
     *
     * @param value CurrencyIfc amount to add
     */
    public void addAmountGrossTaxableTransactionSalesVoided(CurrencyIfc value)
    {
        if (amountGrossTaxableTransactionSalesVoided == null)
        {
            amountGrossTaxableTransactionSalesVoided = value;
        }
        else
        {
            amountGrossTaxableTransactionSalesVoided = amountGrossTaxableTransactionSalesVoided.add(value);
        }
    }

    /**
     * Adds number gross Taxable Transaction Sales Voided
     *
     * @param value int number to add
     */
    public void addCountGrossTaxableTransactionSalesVoided(int value)
    {
        countGrossTaxableTransactionSalesVoided = countGrossTaxableTransactionSalesVoided + value;
    }

    /**
     * Sets number gross Taxable Transaction Sales Voided
     *
     * @param value int new value
     */
    public void setCountGrossTaxableTransactionSalesVoided(int value)
    {
        countGrossTaxableTransactionSalesVoided = value;
    }

    /**
     * Gets count gross Taxable Transaction Sales Voided
     *
     * @return int count
     */
    public int getCountGrossTaxableTransactionSalesVoided()
    {
        return countGrossTaxableTransactionSalesVoided;
    }

    /**
     * Gets amount gross Non Taxable Transaction Sales Voided
     *
     * @return CurrencyIfc amount
     */
    public CurrencyIfc getAmountGrossNonTaxableTransactionSalesVoided()
    {
        if (amountGrossNonTaxableTransactionSalesVoided == null)
        {
            amountGrossNonTaxableTransactionSalesVoided = DomainGateway.getBaseCurrencyInstance();
        }
        return amountGrossNonTaxableTransactionSalesVoided;
    }

    /**
     * Sets amount gross Non Taxable Transaction Sales Voided
     *
     * @param value CurrencyIfc new value
     */
    public void setAmountGrossNonTaxableTransactionSalesVoided(CurrencyIfc value)
    {
        amountGrossNonTaxableTransactionSalesVoided = value;
    }

    /**
     * Adds amount gross Non Taxable Transaction Sales Voided
     *
     * @param value CurrencyIfc amount to add
     */
    public void addAmountGrossNonTaxableTransactionSalesVoided(CurrencyIfc value)
    {
        if (amountGrossNonTaxableTransactionSalesVoided == null)
        {
            amountGrossNonTaxableTransactionSalesVoided = value;
        }
        else
        {
            amountGrossNonTaxableTransactionSalesVoided = amountGrossNonTaxableTransactionSalesVoided.add(value);
        }
    }

    /**
     * Adds number gross Non Taxable Transaction Sales Voided
     *
     * @param value int number to add
     */
    public void addCountGrossNonTaxableTransactionSalesVoided(int value)
    {
        countGrossNonTaxableTransactionSalesVoided = countGrossNonTaxableTransactionSalesVoided + value;
    }

    /**
     * Sets number gross Non Taxable Transaction Sales Voided
     *
     * @param value int new value
     */
    public void setCountGrossNonTaxableTransactionSalesVoided(int value)
    {
        countGrossNonTaxableTransactionSalesVoided = value;
    }

    /**
     * Gets count gross Non Taxable Transaction Sales Voided
     *
     * @return int count
     */
    public int getCountGrossNonTaxableTransactionSalesVoided()
    {
        return countGrossNonTaxableTransactionSalesVoided;
    }

    /**
     * Gets amount gross Tax Exempt Transaction Sales Voided
     *
     * @return CurrencyIfc amount
     */
    public CurrencyIfc getAmountGrossTaxExemptTransactionSalesVoided()
    {
        if (amountGrossTaxExemptTransactionSalesVoided == null)
        {
            amountGrossTaxExemptTransactionSalesVoided = DomainGateway.getBaseCurrencyInstance();
        }
        return amountGrossTaxExemptTransactionSalesVoided;
    }

    /**
     * Sets amount gross Tax Exempt Transaction Sales Voided
     *
     * @param value CurrencyIfc new value
     */
    public void setAmountGrossTaxExemptTransactionSalesVoided(CurrencyIfc value)
    {
        amountGrossTaxExemptTransactionSalesVoided = value;
    }

    /**
     * Adds amount gross Tax Exempt Transaction Sales Voided
     *
     * @param value CurrencyIfc amount to add
     */
    public void addAmountGrossTaxExemptTransactionSalesVoided(CurrencyIfc value)
    {
        if (amountGrossTaxExemptTransactionSalesVoided == null)
        {
            amountGrossTaxExemptTransactionSalesVoided = value;
        }
        else
        {
            amountGrossTaxExemptTransactionSalesVoided = amountGrossTaxExemptTransactionSalesVoided.add(value);
        }
    }

    /**
     * Adds number gross Tax Exempt Transaction Sales Voided
     *
     * @param value int number to add
     */
    public void addCountGrossTaxExemptTransactionSalesVoided(int value)
    {
        countGrossTaxExemptTransactionSalesVoided = countGrossTaxExemptTransactionSalesVoided + value;
    }

    /**
     * Sets number gross Tax Exempt Transaction Sales Voided
     *
     * @param value int new value
     */
    public void setCountGrossTaxExemptTransactionSalesVoided(int value)
    {
        countGrossTaxExemptTransactionSalesVoided = value;
    }

    /**
     * Gets count gross Tax Exempt Transaction Sales Voided
     *
     * @return int count
     */
    public int getCountGrossTaxExemptTransactionSalesVoided()
    {
        return countGrossTaxExemptTransactionSalesVoided;
    }

    /**
     * Gets amount gross Taxable Transaction Returns Voided
     *
     * @return CurrencyIfc amount
     */
    public CurrencyIfc getAmountGrossTaxableTransactionReturnsVoided()
    {
        if (amountGrossTaxableTransactionReturnsVoided == null)
        {
            amountGrossTaxableTransactionReturnsVoided = DomainGateway.getBaseCurrencyInstance();
        }
        return amountGrossTaxableTransactionReturnsVoided;
    }

    /**
     * Sets amount gross Taxable Transaction Returns Voided
     *
     * @param value CurrencyIfc new value
     */
    public void setAmountGrossTaxableTransactionReturnsVoided(CurrencyIfc value)
    {
        amountGrossTaxableTransactionReturnsVoided = value;
    }

    /**
     * Adds amount gross Taxable Transaction Returns Voided
     *
     * @param value CurrencyIfc amount to add
     */
    public void addAmountGrossTaxableTransactionReturnsVoided(CurrencyIfc value)
    {
        if (amountGrossTaxableTransactionReturnsVoided == null)
        {
            amountGrossTaxableTransactionReturnsVoided = value;
        }
        else
        {
            amountGrossTaxableTransactionReturnsVoided = amountGrossTaxableTransactionReturnsVoided.add(value);
        }
    }

    /**
     * Adds number gross Taxable Transaction Returns Voided
     *
     * @param value int number to add
     */
    public void addCountGrossTaxableTransactionReturnsVoided(int value)
    {
        countGrossTaxableTransactionReturnsVoided = countGrossTaxableTransactionReturnsVoided + value;
    }

    /**
     * Sets number gross Taxable Transaction Returns Voided
     *
     * @param value int new value
     */
    public void setCountGrossTaxableTransactionReturnsVoided(int value)
    {
        countGrossTaxableTransactionReturnsVoided = value;
    }

    /**
     * Gets count gross Taxable Transaction Returns Voided
     *
     * @return int count
     */
    public int getCountGrossTaxableTransactionReturnsVoided()
    {
        return countGrossTaxableTransactionReturnsVoided;
    }

    /**
     * Gets amount gross Non Taxable Transaction Returns Voided
     *
     * @return CurrencyIfc amount
     */
    public CurrencyIfc getAmountGrossNonTaxableTransactionReturnsVoided()
    {
        if (amountGrossNonTaxableTransactionReturnsVoided == null)
        {
            amountGrossNonTaxableTransactionReturnsVoided = DomainGateway.getBaseCurrencyInstance();
        }
        return amountGrossNonTaxableTransactionReturnsVoided;
    }

    /**
     * Sets amount gross Non Taxable Transaction Returns Voided
     *
     * @param value CurrencyIfc new value
     */
    public void setAmountGrossNonTaxableTransactionReturnsVoided(CurrencyIfc value)
    {
        amountGrossNonTaxableTransactionReturnsVoided = value;
    }

    /**
     * Adds amount gross Non Taxable Transaction Returns Voided
     *
     * @param value CurrencyIfc amount to add
     */
    public void addAmountGrossNonTaxableTransactionReturnsVoided(CurrencyIfc value)
    {
        if (amountGrossNonTaxableTransactionReturnsVoided == null)
        {
            amountGrossNonTaxableTransactionReturnsVoided = value;
        }
        else
        {
            amountGrossNonTaxableTransactionReturnsVoided = amountGrossNonTaxableTransactionReturnsVoided.add(value);
        }
    }

    /**
     * Adds number gross Non Taxable Transaction Returns Voided
     *
     * @param value int number to add
     */
    public void addCountGrossNonTaxableTransactionReturnsVoided(int value)
    {
        countGrossNonTaxableTransactionReturnsVoided = countGrossNonTaxableTransactionReturnsVoided + value;
    }

    /**
     * Sets number gross Non Taxable Transaction Returns Voided
     *
     * @param value int new value
     */
    public void setCountGrossNonTaxableTransactionReturnsVoided(int value)
    {
        countGrossNonTaxableTransactionReturnsVoided = value;
    }

    /**
     * Gets count gross Non Taxable Transaction Returns Voided
     *
     * @return int count
     */
    public int getCountGrossNonTaxableTransactionReturnsVoided()
    {
        return countGrossNonTaxableTransactionReturnsVoided;
    }

    /**
     * Gets amount gross Tax Exempt Transaction Returns Voided
     *
     * @return CurrencyIfc amount
     */
    public CurrencyIfc getAmountGrossTaxExemptTransactionReturnsVoided()
    {
        if (amountGrossTaxExemptTransactionReturnsVoided == null)
        {
            amountGrossTaxExemptTransactionReturnsVoided = DomainGateway.getBaseCurrencyInstance();
        }
        return amountGrossTaxExemptTransactionReturnsVoided;
    }

    /**
     * Sets amount gross Tax Exempt Transaction Returns Voided
     *
     * @param value CurrencyIfc new value
     */
    public void setAmountGrossTaxExemptTransactionReturnsVoided(CurrencyIfc value)
    {
        amountGrossTaxExemptTransactionReturnsVoided = value;
    }

    /**
     * Adds amount gross Tax Exempt Transaction Returns Voided
     *
     * @param value CurrencyIfc amount to add
     */
    public void addAmountGrossTaxExemptTransactionReturnsVoided(CurrencyIfc value)
    {
        if (amountGrossTaxExemptTransactionReturnsVoided == null)
        {
            amountGrossTaxExemptTransactionReturnsVoided = value;
        }
        else
        {
            amountGrossTaxExemptTransactionReturnsVoided = amountGrossTaxExemptTransactionReturnsVoided.add(value);
        }
    }

    /**
     * Adds number gross Tax Exempt Transaction Returns Voided
     *
     * @param value int number to add
     */
    public void addCountGrossTaxExemptTransactionReturnsVoided(int value)
    {
        countGrossTaxExemptTransactionReturnsVoided = countGrossTaxExemptTransactionReturnsVoided + value;
    }

    /**
     * Sets number gross Tax Exempt Transaction Returns Voided
     *
     * @param value int new value
     */
    public void setCountGrossTaxExemptTransactionReturnsVoided(int value)
    {
        countGrossTaxExemptTransactionReturnsVoided = value;
    }

    /**
     * Gets count gross Tax Exempt Transaction Returns Voided
     *
     * @return int count
     */
    public int getCountGrossTaxExemptTransactionReturnsVoided()
    {
        return countGrossTaxExemptTransactionReturnsVoided;
    }

    /**
     * Gets amount layaway payments
     *
     * @return CurrencyIfc amount
     */
    public CurrencyIfc getAmountLayawayPayments()
    {
        if (amountLayawayPayments == null)
        {
            amountLayawayPayments = DomainGateway.getBaseCurrencyInstance();
        }
        return amountLayawayPayments;
    }

    /**
     * Sets amount layaway payments
     *
     * @param value CurrencyIfc new value
     */
    public void setAmountLayawayPayments(CurrencyIfc value)
    {
        amountLayawayPayments = value;
    }

    /**
     * Adds amount layaway payments
     *
     * @param value CurrencyIfc amount to add
     */
    public void addAmountLayawayPayments(CurrencyIfc value)
    {
        if (amountLayawayPayments == null)
        {
            amountLayawayPayments = value;
        }
        else
        {
            amountLayawayPayments = amountLayawayPayments.add(value);
        }
    }

    /**
     * Adds number layaway payments
     *
     * @param value int number to add
     */
    public void addCountLayawayPayments(int value)
    {
        countLayawayPayments = countLayawayPayments + value;
    }

    /**
     * Sets number layaway payments
     *
     * @param value int new value
     */
    public void setCountLayawayPayments(int value)
    {
        countLayawayPayments = value;
    }

    /**
     * Gets count layaway payments
     *
     * @return int count
     */
    public int getCountLayawayPayments()
    {
        return countLayawayPayments;
    }

    /**
     * gets the New Layaway made
     *
     * @return CurrecyIfc amount
     */
    public CurrencyIfc getAmountLayawayNew()
    {
        if (amountLayawayNew == null)
        {
            amountLayawayNew = DomainGateway.getBaseCurrencyInstance();
        }
        return amountLayawayNew;
    }

    public void setAmountLayawayNew(CurrencyIfc value)
    {
        amountLayawayNew = value;
    }

    public void addAmountLayawayNew(CurrencyIfc value)
    {
        if (amountLayawayNew == null)
        {
            amountLayawayNew = value;
        }
        else
        {
            amountLayawayNew = amountLayawayNew.add(value);
        }
    }

    /**
     * Adds number layaway payments
     *
     * @param value int number to add
     */
    public void addCountLayawayNew(int value)
    {
        countLayawayNew = countLayawayNew + value;
    }

    /**
     * Sets number layaway payments
     *
     * @param value int new value
     */
    public void setCountLayawayNew(int value)
    {
        countLayawayNew = value;
    }

    /**
     * Gets count layaway payments
     *
     * @return int count
     */
    public int getCountLayawayNew()
    {
        return countLayawayNew;
    }

    /**
     * gets the Pickup Layaway made
     *
     * @return CurrecyIfc amount
     */
    public CurrencyIfc getAmountLayawayPickup()
    {
        if (amountLayawayPickup == null)
        {
            amountLayawayPickup = DomainGateway.getBaseCurrencyInstance();
        }
        return amountLayawayPickup;
    }

    public void setAmountLayawayPickup(CurrencyIfc value)
    {
        amountLayawayPickup = value;
    }

    public void addAmountLayawayPickup(CurrencyIfc value)
    {
        if (amountLayawayPickup == null)
        {
            amountLayawayPickup = value;
        }
        else
        {
            amountLayawayPickup = amountLayawayPickup.add(value);
        }
    }

    /**
     * Adds number layaway Pickup
     *
     * @param value int number to add
     */
    public void addCountLayawayPickup(int value)
    {
        countLayawayPickup = countLayawayPickup + value;
    }

    /**
     * Sets number layaway Pickup
     *
     * @param value int new value
     */
    public void setCountLayawayPickup(int value)
    {
        countLayawayPickup = value;
    }

    /**
     * Gets count layaway pickup
     *
     * @return int count
     */
    public int getCountLayawayPickup()
    {
        return countLayawayPickup;
    }

    /**
     * Gets amount layaway deletions
     *
     * @return CurrencyIfc amount
     */
    public CurrencyIfc getAmountLayawayDeletions()
    {
        if (amountLayawayDeletions == null)
        {
            amountLayawayDeletions = DomainGateway.getBaseCurrencyInstance();
        }
        return amountLayawayDeletions;
    }

    /**
     * Sets amount layaway deletions
     *
     * @param value CurrencyIfc new value
     */
    public void setAmountLayawayDeletions(CurrencyIfc value)
    {
        amountLayawayDeletions = value;
    }

    /**
     * Adds amount layaway deletions
     *
     * @param value CurrencyIfc amount to add
     */
    public void addAmountLayawayDeletions(CurrencyIfc value)
    {
        if (amountLayawayDeletions == null)
        {
            amountLayawayDeletions = value;
        }
        else
        {
            amountLayawayDeletions = amountLayawayDeletions.add(value);
        }
    }

    /**
     * Adds number layaway deletions
     *
     * @param value int number to add
     */
    public void addCountLayawayDeletions(int value)
    {
        countLayawayDeletions = countLayawayDeletions + value;
    }

    /**
     * Sets number layaway deletions
     *
     * @param value int new value
     */
    public void setCountLayawayDeletions(int value)
    {
        countLayawayDeletions = value;
    }

    /**
     * Gets count layaway deletions
     *
     * @return int count
     */
    public int getCountLayawayDeletions()
    {
        return countLayawayDeletions;
    }

    /**
     * Gets amount layaway initiation fees
     *
     * @return CurrencyIfc amount
     */
    public CurrencyIfc getAmountLayawayInitiationFees()
    {
        if (amountLayawayInitiationFees == null)
        {
            amountLayawayInitiationFees = DomainGateway.getBaseCurrencyInstance();
        }
        return amountLayawayInitiationFees;
    }

    /**
     * Sets amount layaway initiation fees
     *
     * @param value CurrencyIfc new value
     */
    public void setAmountLayawayInitiationFees(CurrencyIfc value)
    {
        amountLayawayInitiationFees = value;
    }

    /**
     * Adds amount layaway initiation fees
     *
     * @param value CurrencyIfc amount to add
     */
    public void addAmountLayawayInitiationFees(CurrencyIfc value)
    {
        if (amountLayawayInitiationFees == null)
        {
            amountLayawayInitiationFees = value;
        }
        else
        {
            amountLayawayInitiationFees = amountLayawayInitiationFees.add(value);
        }
    }

    /**
     * Adds number layaway initiation fees
     *
     * @param value int number to add
     */
    public void addCountLayawayInitiationFees(int value)
    {
        countLayawayInitiationFees = countLayawayInitiationFees + value;
    }

    /**
     * Sets number layaway initiation fees
     *
     * @param value int new value
     */
    public void setCountLayawayInitiationFees(int value)
    {
        countLayawayInitiationFees = value;
    }

    /**
     * Gets count layaway initiation fees
     *
     * @return int count
     */
    public int getCountLayawayInitiationFees()
    {
        return countLayawayInitiationFees;
    }

    /**
     * Gets amount layaway deletion fees
     *
     * @return CurrencyIfc amount
     */
    public CurrencyIfc getAmountLayawayDeletionFees()
    {
        if (amountLayawayDeletionFees == null)
        {
            amountLayawayDeletionFees = DomainGateway.getBaseCurrencyInstance();
        }
        return amountLayawayDeletionFees;
    }

    /**
     * Sets amount layaway deletion fees
     *
     * @param value CurrencyIfc new value
     */
    public void setAmountLayawayDeletionFees(CurrencyIfc value)
    {
        amountLayawayDeletionFees = value;
    }

    /**
     * Adds amount layaway deletion fees
     *
     * @param value CurrencyIfc amount to add
     */
    public void addAmountLayawayDeletionFees(CurrencyIfc value)
    {
        if (amountLayawayDeletionFees == null)
        {
            amountLayawayDeletionFees = value;
        }
        else
        {
            amountLayawayDeletionFees = amountLayawayDeletionFees.add(value);
        }
    }

    /**
     * Adds number layaway deletion fees
     *
     * @param value int number to add
     */
    public void addCountLayawayDeletionFees(int value)
    {
        countLayawayDeletionFees = countLayawayDeletionFees + value;
    }

    /**
     * Sets number layaway deletion fees
     *
     * @param value int new value
     */
    public void setCountLayawayDeletionFees(int value)
    {
        countLayawayDeletionFees = value;
    }

    /**
     * Gets count layaway deletion fees
     *
     * @return int count
     */
    public int getCountLayawayDeletionFees()
    {
        return countLayawayDeletionFees;
    }

    /**
     * gets the New Sepcial Orders made
     *
     * @return CurrecyIfc amount
     */
    public CurrencyIfc getAmountSpecialOrderNew()
    {
        if (amountSpecialOrderNew == null)
        {
            amountSpecialOrderNew = DomainGateway.getBaseCurrencyInstance();
        }
        return amountSpecialOrderNew;
    }

    /**
     * Sets the New special Orders
     *
     * @param value
     */
    public void setAmountSpecialOrderNew(CurrencyIfc value)
    {
        amountSpecialOrderNew = value;
    }

    /**
     * Adds the Special Order to the Financial Totals
     *
     * @param value
     */
    public void addAmountSpecialOrderNew(CurrencyIfc value)
    {
        if (amountSpecialOrderNew == null)
        {
            amountSpecialOrderNew = value;
        }
        else
        {
            amountSpecialOrderNew = amountSpecialOrderNew.add(value);
        }
    }

    /**
     * gets the Special Order partial Amounts
     *
     * @return CurrecyIfc amount
     */
    public CurrencyIfc getAmountSpecialOrderPartial()
    {
        if (amountSpecialOrderPartial == null)
        {
            amountSpecialOrderPartial = DomainGateway.getBaseCurrencyInstance();
        }
        return amountSpecialOrderPartial;
    }

    /**
     * Sets the Special Order partial amount
     *
     * @param value
     */
    public void setAmountSpecialOrderPartial(CurrencyIfc value)
    {
        amountSpecialOrderPartial = value;
    }

    /**
     * Adds the Special Order Partial Amount to the Financial Totals
     *
     * @param value
     */
    public void addAmountSpecialOrderPartial(CurrencyIfc value)
    {
        if (amountSpecialOrderPartial == null)
        {
            amountSpecialOrderPartial = value;
        }
        else
        {
            amountSpecialOrderPartial = amountSpecialOrderPartial.add(value);
        }
    }

    /**
     * Gets amount order payments
     *
     * @return CurrencyIfc amount
     */
    public CurrencyIfc getAmountOrderPayments()
    {
        if (amountOrderPayments == null)
        {
            amountOrderPayments = DomainGateway.getBaseCurrencyInstance();
        }
        return amountOrderPayments;
    }

    /**
     * Sets amount order payments
     *
     * @param value CurrencyIfc new value
     */
    public void setAmountOrderPayments(CurrencyIfc value)
    {
        amountOrderPayments = value;
    }

    /**
     * Adds amount order payments
     *
     * @param value CurrencyIfc amount to add
     */
    public void addAmountOrderPayments(CurrencyIfc value)
    {
        if (amountOrderPayments == null)
        {
            amountOrderPayments = value;
        }
        else
        {
            amountOrderPayments = amountOrderPayments.add(value);
        }
    }

    /**
     * Adds number order payments
     *
     * @param value int number to add
     */
    public void addCountOrderPayments(int value)
    {
        countOrderPayments = countOrderPayments + value;
    }

    /**
     * Sets number order payments
     *
     * @param value int new value
     */
    public void setCountOrderPayments(int value)
    {
        countOrderPayments = value;
    }

    /**
     * Gets count order payments
     *
     * @return int count
     */
    public int getCountOrderPayments()
    {
        return countOrderPayments;
    }

    /**
     * Gets amount order cancels
     *
     * @return CurrencyIfc amount
     */
    public CurrencyIfc getAmountOrderCancels()
    {
        if (amountOrderCancels == null)
        {
            amountOrderCancels = DomainGateway.getBaseCurrencyInstance();
        }
        return amountOrderCancels;
    }

    /**
     * Sets amount order cancels
     *
     * @param value CurrencyIfc new value
     */
    public void setAmountOrderCancels(CurrencyIfc value)
    {
        amountOrderCancels = value;
    }

    /**
     * Adds amount order cancels
     *
     * @param value CurrencyIfc amount to add
     */
    public void addAmountOrderCancels(CurrencyIfc value)
    {
        if (amountOrderCancels == null)
        {
            amountOrderCancels = value;
        }
        else
        {
            amountOrderCancels = amountOrderCancels.add(value);
        }
    }

    /**
     * Adds number order cancels
     *
     * @param value int number to add
     */
    public void addCountOrderCancels(int value)
    {
        countOrderCancels = countOrderCancels + value;
    }

    /**
     * Sets number order cancels
     *
     * @param value int new value
     */
    public void setCountOrderCancels(int value)
    {
        countOrderCancels = value;
    }

    /**
     * Gets count order cancels
     *
     * @return int count
     */
    public int getCountOrderCancels()
    {
        return countOrderCancels;
    }

    /**
     * Retrieves amount of employee discounts.
     *
     * @return amount of employee discounts
     * @deprecated As of 7.0.0, replaced by
     *             {@link #getAmountGrossItemEmployeeDiscount()} and
     *             {@link #getAmountGrossTransactionEmployeeDiscount()}
     */
    public CurrencyIfc getAmountEmployeeDiscounts()
    {
        if (amountEmployeeDiscounts == null)
        {
            amountEmployeeDiscounts = DomainGateway.getBaseCurrencyInstance();
        }
        return amountEmployeeDiscounts;
    }

    /**
     * Sets amount of employee discounts.
     *
     * @param value amount of employee discounts
     * @deprecated As of 7.0.0, replaced by
     *             {@link #setAmountGrossItemEmployeeDiscount(CurrencyIfc)} and
     *             {@link #setAmountGrossTransactionEmployeeDiscount(CurrencyIfc)}
     */
    public void setAmountEmployeeDiscounts(CurrencyIfc value)
    {
        amountEmployeeDiscounts = value;
    }

    /**
     * Adds amount of employee discounts.
     *
     * @param value amount of employee discounts
     * @deprecated As of 7.0.0, replaced by
     *             {@link #addAmountGrossItemEmployeeDiscount()} and
     *             {@link #addAmountGrossTransactionEmployeeDiscount()}
     */
    public void addAmountEmployeeDiscounts(CurrencyIfc value)
    {
        if (amountEmployeeDiscounts == null)
        {
            amountEmployeeDiscounts = value;
        }
        else
        {
            amountEmployeeDiscounts = amountEmployeeDiscounts.add(value);
        }
    }

    /**
     * Retrieves units of employee discounts.
     *
     * @return units of employee discounts
     * @deprecated As of 7.0.0, replaced by
     *             {@link #getUnitsGrossItemEmployeeDiscount()} and
     *             {@link #getUnitsGrossTransactionEmployeeDiscount()}
     */
    public BigDecimal getUnitsEmployeeDiscounts()
    {
        if (unitsEmployeeDiscounts == null)
        {
            unitsEmployeeDiscounts = BigDecimal.ZERO;
        }
        return unitsEmployeeDiscounts;
    }

    /**
     * Sets units of employee discounts.
     *
     * @param value units of employee discounts
     * @deprecated As of 7.0.0, replaced by
     *             {@link #setUnitsGrossItemEmployeeDiscount(BigDecimal)} and
     *             {@link #setUnitsGrossTransactionEmployeeDiscount(BigDecimal)}
     */
    public void setUnitsEmployeeDiscounts(BigDecimal value)
    {
        unitsEmployeeDiscounts = value;
    }

    /**
     * Adds units of employee discounts.
     *
     * @param value units of employee discounts
     * @deprecated As of 7.0.0, replaced by
     *             {@link #addUnitsGrossItemEmployeeDiscount(BigDecimal)} and
     *             {@link #addUnitsGrossTransactionEmployeeDiscount(BigDecimal)}
     */
    public void addUnitsEmployeeDiscounts(BigDecimal value)
    {
        if (unitsEmployeeDiscounts == null)
        {
            unitsEmployeeDiscounts = value;
        }
        else
        {
            unitsEmployeeDiscounts = unitsEmployeeDiscounts.add(value);
        }
    }

    /**
     * Retrieves amount of customer discounts.
     *
     * @return amount of customer discounts
     */
    public CurrencyIfc getAmountCustomerDiscounts()
    {
        if (amountCustomerDiscounts == null)
        {
            amountCustomerDiscounts = DomainGateway.getBaseCurrencyInstance();
        }
        return amountCustomerDiscounts;
    }

    /**
     * Sets amount of customer discounts.
     *
     * @param value amount of customer discounts
     */
    public void setAmountCustomerDiscounts(CurrencyIfc value)
    {
        amountCustomerDiscounts = value;
    }

    /**
     * Adds amount of customer discounts.
     *
     * @param value amount of customer discounts
     */
    public void addAmountCustomerDiscounts(CurrencyIfc value)
    {
        if (amountCustomerDiscounts == null)
        {
            amountCustomerDiscounts = value;
        }
        else
        {
            amountCustomerDiscounts = amountCustomerDiscounts.add(value);
        }
    }

    /**
     * Retrieves units of customer discounts.
     *
     * @return units of customer discounts
     */
    public BigDecimal getUnitsCustomerDiscounts()
    {
        if (unitsCustomerDiscounts == null)
        {
            unitsCustomerDiscounts = BigDecimal.ZERO;
        }
        return unitsCustomerDiscounts;
    }

    /**
     * Sets units of customer discounts.
     *
     * @param value units of customer discounts
     */
    public void setUnitsCustomerDiscounts(BigDecimal value)
    {
        unitsCustomerDiscounts = value;
    }

    /**
     * Adds units of customer discounts.
     *
     * @param value units of customer discounts
     */
    public void addUnitsCustomerDiscounts(BigDecimal value)
    {
        if (unitsCustomerDiscounts == null)
        {
            unitsCustomerDiscounts = value;
        }
        else
        {
            unitsCustomerDiscounts = unitsCustomerDiscounts.add(value);
        }
    }

    /**
     * Retrieves amount of price overrides.
     *
     * @return amount of price overrides
     */
    public CurrencyIfc getAmountPriceOverrides()
    {
        if (amountPriceOverrides == null)
        {
            amountPriceOverrides = DomainGateway.getBaseCurrencyInstance();
        }
        return amountPriceOverrides;
    }

    /**
     * Sets amount of price overrides.
     *
     * @param value amount of price overrides
     */
    public void setAmountPriceOverrides(CurrencyIfc value)
    {
        amountPriceOverrides = value;
    }

    /**
     * Adds amount of price overrides.
     *
     * @param value amount of price overrides
     */
    public void addAmountPriceOverrides(CurrencyIfc value)
    {
        if (amountPriceOverrides == null)
        {
            amountPriceOverrides = value;
        }
        else
        {
            amountPriceOverrides = amountPriceOverrides.add(value);
        }
    }

    /**
     * Retrieves units of price overrides.
     *
     * @return units of price overrides
     */
    public BigDecimal getUnitsPriceOverrides()
    {
        if (unitsPriceOverrides == null)
        {
            unitsPriceOverrides = BigDecimal.ZERO;
        }
        return unitsPriceOverrides;
    }

    /**
     * Sets units of price overrides.
     *
     * @param value units of price overrides
     */
    public void setUnitsPriceOverrides(BigDecimal value)
    {
        unitsPriceOverrides = value;
    }

    /**
     * Adds units of price overrides.
     *
     * @param value units of price overrides
     */
    public void addUnitsPriceOverrides(BigDecimal value)
    {
        if (unitsPriceOverrides == null)
        {
            unitsPriceOverrides = value;
        }
        else
        {
            unitsPriceOverrides = unitsPriceOverrides.add(value);
        }
    }

    /**
     * Retrieves units of price Adjustments.
     *
     * @return units of price overrides
     */
    public BigDecimal getUnitsPriceAdjustments()
    {
        if (unitsPriceAdjustments == null)
        {
            unitsPriceAdjustments = BigDecimal.ZERO;
        }
        return unitsPriceAdjustments;
    }

    /**
     * Sets units of price Adjustments.
     *
     * @param value units of price overrides
     */
    public void setUnitsPriceAdjustments(BigDecimal value)
    {
        unitsPriceAdjustments = value;
    }

    /**
     * Adds units of price Adjustments.
     *
     * @param value units of price overrides
     */
    public void addUnitsPriceAdjustments(BigDecimal value)
    {
        if (unitsPriceAdjustments == null)
        {
            unitsPriceAdjustments = value;
        }
        else
        {
            unitsPriceAdjustments = unitsPriceAdjustments.add(value);
        }
    }

    /* (non-Javadoc)
     * @see oracle.retail.stores.domain.financial.FinancialTotalsIfc#getAmountGrossGiftCertificates()
     */
    public CurrencyIfc getAmountGrossGiftCertificates()
    {
        return getAmountGrossGiftCertificateIssued().subtract(getAmountGrossGiftCertificateIssuedVoided());
    }

    /**
     * Retrieves amount of gross gift certificate issued.
     *
     * @return amount of gross gift certificate issued
     */
    public CurrencyIfc getAmountGrossGiftCertificateIssued()
    {
        if (amountGrossGiftCertificateIssued == null)
        {
            amountGrossGiftCertificateIssued = DomainGateway.getBaseCurrencyInstance();
        }
        return amountGrossGiftCertificateIssued;
    }

    /**
     * Sets amount of gross gift certificate issued.
     *
     * @param value amount of gross gift certificate issued
     */
    public void setAmountGrossGiftCertificateIssued(CurrencyIfc value)
    {
        amountGrossGiftCertificateIssued = value;
    }

    /**
     * Adds amount of gross gift certificate issued.
     *
     * @param value amount of gross gift certificate issued
     */
    public void addAmountGrossGiftCertificateIssued(CurrencyIfc value)
    {
        if (amountGrossGiftCertificateIssued == null)
        {
            amountGrossGiftCertificateIssued = value;
        }
        else
        {
            amountGrossGiftCertificateIssued = amountGrossGiftCertificateIssued.add(value);
        }
    }

    /**
     * Retrieves amount of net gift certificate issued.
     *
     * @return amount of net gift certificate issued
     */
    public CurrencyIfc getAmountNetGiftCertificateIssued()
    {
        return getAmountGrossGiftCertificateIssued().subtract(getAmountGrossGiftCertificateIssuedVoided());
    }

    /**
     * Retrieves units of gross gift certificate issued.
     *
     * @return units of gross gift certificate issued
     */
    public BigDecimal getUnitsGrossGiftCertificateIssued()
    {
        if (unitsGrossGiftCertificateIssued == null)
        {
            unitsGrossGiftCertificateIssued = BigDecimal.ZERO;
        }
        return unitsGrossGiftCertificateIssued;
    }

    /**
     * Sets units of gross gift certificate issued.
     *
     * @param value units of gross gift certificate issued
     */
    public void setUnitsGrossGiftCertificateIssued(BigDecimal value)
    {
        unitsGrossGiftCertificateIssued = value;
    }

    /**
     * Adds units of gross gift certificate issued.
     *
     * @param value units of gross gift certificate issued
     */
    public void addUnitsGrossGiftCertificateIssued(BigDecimal value)
    {
        if (unitsGrossGiftCertificateIssued == null)
        {
            unitsGrossGiftCertificateIssued = value;
        }
        else
        {
            unitsGrossGiftCertificateIssued = unitsGrossGiftCertificateIssued.add(value);
        }
    }

    /**
     * Retrieves units of net gift certificate issued.
     *
     * @return units of net gift certificate issued
     */
    public BigDecimal getUnitsNetGiftCertificateIssued()
    {
        return getUnitsGrossGiftCertificateIssued().subtract(getUnitsGrossGiftCertificateIssuedVoided());
    }

    /**
     * Retrieves amount of gross gift certificate issued voided.
     *
     * @return amount of gross gift certificate issued voided
     */
    public CurrencyIfc getAmountGrossGiftCertificateIssuedVoided()
    {
        if (amountGrossGiftCertificateIssuedVoided == null)
        {
            amountGrossGiftCertificateIssuedVoided = DomainGateway.getBaseCurrencyInstance();
        }
        return amountGrossGiftCertificateIssuedVoided;
    }

    /**
     * Sets amount of gross gift certificate issued voided.
     *
     * @param value amount of gross gift certificate issued voided
     */
    public void setAmountGrossGiftCertificateIssuedVoided(CurrencyIfc value)
    {
        amountGrossGiftCertificateIssuedVoided = value;
    }

    /**
     * Adds amount of gross gift certificate issued voided.
     *
     * @param value amount of gross gift certificate issued voided
     */
    public void addAmountGrossGiftCertificateIssuedVoided(CurrencyIfc value)
    {
        if (amountGrossGiftCertificateIssuedVoided == null)
        {
            amountGrossGiftCertificateIssuedVoided = value;
        }
        else
        {
            amountGrossGiftCertificateIssuedVoided = amountGrossGiftCertificateIssuedVoided.add(value);
        }
    }

    /**
     * Retrieves units of gross gift certificate issued voided.
     *
     * @return units of gross gift certificate issued voided
     */
    public BigDecimal getUnitsGrossGiftCertificateIssuedVoided()
    {
        if (unitsGrossGiftCertificateIssuedVoided == null)
        {
            unitsGrossGiftCertificateIssuedVoided = BigDecimal.ZERO;
        }
        return unitsGrossGiftCertificateIssuedVoided;
    }

    /**
     * Sets units of gross gift certificate issued voided.
     *
     * @param value units of gross gift certificate issued voided
     */
    public void setUnitsGrossGiftCertificateIssuedVoided(BigDecimal value)
    {
        unitsGrossGiftCertificateIssuedVoided = value;
    }

    /**
     * Adds units of gross gift certificate issued voided.
     *
     * @param value units of gross gift certificate issued voided
     */
    public void addUnitsGrossGiftCertificateIssuedVoided(BigDecimal value)
    {
        if (unitsGrossGiftCertificateIssuedVoided == null)
        {
            unitsGrossGiftCertificateIssuedVoided = value;
        }
        else
        {
            unitsGrossGiftCertificateIssuedVoided = unitsGrossGiftCertificateIssuedVoided.add(value);
        }
    }

    /**
     * Retrieves amount of gross gift certificate tendered.
     *
     * @return amount of gross gift certificate tendered
     */
    public CurrencyIfc getAmountGrossGiftCertificateTendered()
    {
        if (amountGrossGiftCertificateTendered == null)
        {
            amountGrossGiftCertificateTendered = DomainGateway.getBaseCurrencyInstance();
        }
        return amountGrossGiftCertificateTendered;
    }

    /**
     * Sets amount of gross gift certificate tendered.
     *
     * @param value amount of gross gift certificate tendered
     */
    public void setAmountGrossGiftCertificateTendered(CurrencyIfc value)
    {
        amountGrossGiftCertificateTendered = value;
    }

    /**
     * Adds amount of gross gift certificate tendered.
     *
     * @param value amount of gross gift certificate tendered
     */
    public void addAmountGrossGiftCertificateTendered(CurrencyIfc value)
    {
        if (amountGrossGiftCertificateTendered == null)
        {
            amountGrossGiftCertificateTendered = value;
        }
        else
        {
            amountGrossGiftCertificateTendered = amountGrossGiftCertificateTendered.add(value);
        }
    }

    /**
     * Retrieves amount of net gift certificate tendered.
     *
     * @return amount of net gift certificate tendered
     */
    public CurrencyIfc getAmountNetGiftCertificateTendered()
    {
        return getAmountGrossGiftCertificateTendered().subtract(getAmountGrossGiftCertificateTenderedVoided());
    }

    /**
     * Retrieves units of gross gift certificate tendered.
     *
     * @return units of gross gift certificate tendered
     */
    public BigDecimal getUnitsGrossGiftCertificateTendered()
    {
        if (unitsGrossGiftCertificateTendered == null)
        {
            unitsGrossGiftCertificateTendered = BigDecimal.ZERO;
        }
        return unitsGrossGiftCertificateTendered;
    }

    /**
     * Sets units of gross gift certificate tendered.
     *
     * @param value units of gross gift certificate tendered
     */
    public void setUnitsGrossGiftCertificateTendered(BigDecimal value)
    {
        unitsGrossGiftCertificateTendered = value;
    }

    /**
     * Adds units of gross gift certificate tendered.
     *
     * @param value units of gross gift certificate tendered
     */
    public void addUnitsGrossGiftCertificateTendered(BigDecimal value)
    {
        if (unitsGrossGiftCertificateTendered == null)
        {
            unitsGrossGiftCertificateTendered = value;
        }
        else
        {
            unitsGrossGiftCertificateTendered = unitsGrossGiftCertificateTendered.add(value);
        }
    }

    /**
     * Retrieves units of net gift certificate tendered.
     *
     * @return units of net gift certificate tendered
     */
    public BigDecimal getUnitsNetGiftCertificateTendered()
    {
        return getUnitsGrossGiftCertificateTendered().subtract(getUnitsGrossGiftCertificateTenderedVoided());
    }

    /**
     * Retrieves amount of gross gift certificate tendered voided.
     *
     * @return amount of gross gift certificate tendered voided
     */
    public CurrencyIfc getAmountGrossGiftCertificateTenderedVoided()
    {
        if (amountGrossGiftCertificateTenderedVoided == null)
        {
            amountGrossGiftCertificateTenderedVoided = DomainGateway.getBaseCurrencyInstance();
        }
        return amountGrossGiftCertificateTenderedVoided;
    }

    /**
     * Sets amount of gross gift certificate tendered voided.
     *
     * @param value amount of gross gift certificate tendered voided
     */
    public void setAmountGrossGiftCertificateTenderedVoided(CurrencyIfc value)
    {
        amountGrossGiftCertificateTenderedVoided = value;
    }

    /**
     * Adds amount of gross gift certificate tendered voided.
     *
     * @param value amount of gross gift certificate tendered voided
     */
    public void addAmountGrossGiftCertificateTenderedVoided(CurrencyIfc value)
    {
        if (amountGrossGiftCertificateTenderedVoided == null)
        {
            amountGrossGiftCertificateTenderedVoided = value;
        }
        else
        {
            amountGrossGiftCertificateTenderedVoided = amountGrossGiftCertificateTenderedVoided.add(value);
        }
    }

    /**
     * Retrieves units of gross gift certificate tendered voided.
     *
     * @return units of gross gift certificate tendered voided
     */
    public BigDecimal getUnitsGrossGiftCertificateTenderedVoided()
    {
        if (unitsGrossGiftCertificateTenderedVoided == null)
        {
            unitsGrossGiftCertificateTenderedVoided = BigDecimal.ZERO;
        }
        return unitsGrossGiftCertificateTenderedVoided;
    }

    /**
     * Sets units of gross gift certificate tendered voided.
     *
     * @param value units of gross gift certificate tendered voided
     */
    public void setUnitsGrossGiftCertificateTenderedVoided(BigDecimal value)
    {
        unitsGrossGiftCertificateTenderedVoided = value;
    }

    /**
     * Adds units of gross gift certificate tendered voided.
     *
     * @param value units of gross gift certificate tendered voided
     */
    public void addUnitsGrossGiftCertificateTenderedVoided(BigDecimal value)
    {
        if (unitsGrossGiftCertificateTenderedVoided == null)
        {
            unitsGrossGiftCertificateTenderedVoided = value;
        }
        else
        {
            unitsGrossGiftCertificateTenderedVoided = unitsGrossGiftCertificateTenderedVoided.add(value);
        }
    }

    /**
     * Add a house card enrollment to financial totals
     *
     * @param value value to add to existing count
     * @see oracle.retail.stores.domain.financial.FinancialTotalsIfc#addHouseCardEnrollmentsApproved(int)
     */
    public void addHouseCardEnrollmentsApproved(int value)
    {
        this.houseCardEnrollmentsApproved += value;
    }

    /**
     * Add a declined house card enrollment to financial totals
     *
     * @param value value to add to existing count
     * @see oracle.retail.stores.domain.financial.FinancialTotalsIfc#addHouseCardEnrollmentsDeclined(int)
     */
    public void addHouseCardEnrollmentsDeclined(int value)
    {
        this.houseCardEnrollmentsDeclined += value;
    }

    /**
     * Set the number of house card enrollments that were approved
     *
     * @param approvals
     * @see oracle.retail.stores.domain.financial.FinancialTotalsIfc#setHouseCardEnrollmentsApproved(int)
     */
    public void setHouseCardEnrollmentsApproved(int approvals)
    {
        this.houseCardEnrollmentsApproved = approvals;
    }

    /**
     * Set the number of house card enrollments that were declined
     *
     * @param declinations
     * @see oracle.retail.stores.domain.financial.FinancialTotalsIfc#setHouseCardEnrollmentsDeclined(int)
     */
    public void setHouseCardEnrollmentsDeclined(int declinations)
    {
        this.houseCardEnrollmentsDeclined = declinations;
    }

    /**
     * Get the number of applications for a house credit card that were
     * approved.
     *
     * @return houseCardEnrollmentsApproved
     * @see oracle.retail.stores.domain.financial.FinancialTotalsIfc#getHouseCardEnrollmentsApproved()
     */
    public int getHouseCardEnrollmentsApproved()
    {
        return this.houseCardEnrollmentsApproved;
    }

    /**
     * Get the number of applications for a house credit card that were declined
     *
     * @return houseCardEnrollmentsDeclined
     * @see oracle.retail.stores.domain.financial.FinancialTotalsIfc#getHouseCardEnrollmentsDeclined()
     */
    public int getHouseCardEnrollmentsDeclined()
    {
        return this.houseCardEnrollmentsDeclined;
    }

    /**
     * Get the taxes
     *
     * @return container with all tax information
     */
    public TaxTotalsContainerIfc getTaxes()
    {
        if (this.taxTotalsContainer == null)
        {
            this.taxTotalsContainer = DomainGateway.getFactory().getTaxTotalsContainerInstance();
        }
        return this.taxTotalsContainer;
    }

    /**
     * Add this set of taxes to the existing taxes
     *
     * @param taxes Taxes to add to existing taxes
     */
    public void addTaxes(TaxTotalsContainerIfc taxes)
    {
        if (this.taxTotalsContainer == null)
        {
            this.taxTotalsContainer = DomainGateway.getFactory().getTaxTotalsContainerInstance();
        }
        this.taxTotalsContainer.add(taxes);
    }

    /**
     * subtract this set of taxes to the existing taxes
     *
     * @param taxes Taxes to subtract from existing taxes
     */
    public void subtractTaxes(TaxTotalsContainerIfc taxes)
    {
        if (this.taxTotalsContainer == null)
        {
            this.taxTotalsContainer = DomainGateway.getFactory().getTaxTotalsContainerInstance();
        }
        this.taxTotalsContainer.subtract(taxes);
    }

    /**
     * Set these taxes as the total taxes
     *
     * @param taxes taxes to set
     */
    public void setTaxes(TaxTotalsContainerIfc taxes)
    {
        this.taxTotalsContainer = taxes;
    }

    /**
     * Retrieve the amount of gross gift card item credit
     *
     * @return amountGrossGiftCardItemCredit
     */
    public CurrencyIfc getAmountGrossGiftCardItemCredit()
    {
        if (amountGrossGiftCardItemCredit == null)
        {
            amountGrossGiftCardItemCredit = DomainGateway.getBaseCurrencyInstance();
        }
        return amountGrossGiftCardItemCredit;
    }

    /**
     * Retrieve the units of GrossGiftCardItemCredit
     *
     * @return unitsGrossGiftCardItemCredit
     */
    public BigDecimal getUnitsGrossGiftCardItemCredit()
    {
        if (unitsGrossGiftCardItemCredit == null)
        {
            unitsGrossGiftCardItemCredit = BigDecimal.ZERO;
        }
        return unitsGrossGiftCardItemCredit;
    }

    /**
     * Sets amount of gross gift card item credits
     *
     * @param value amount of the gross gift card item credit
     */
    public void setAmountGrossGiftCardItemCredit(CurrencyIfc value)
    {
        amountGrossGiftCardItemCredit = value;
    }

    /**
     * Sets units of GrossGiftCardItemCredit
     *
     * @param value units of the GrossGiftCardItemCredit
     */
    public void setUnitsGrossGiftCardItemCredit(BigDecimal value)
    {
        unitsGrossGiftCardItemCredit = value;
    }

    /**
     * Adds an amount to the amountGrossGiftCardItemCredit already stored
     *
     * @param value amount to add to the pre-existing amount.
     */
    public void addAmountGrossGiftCardItemCredit(CurrencyIfc value)
    {
        if (amountGrossGiftCardItemCredit == null)
        {
            amountGrossGiftCardItemCredit = value;
        }
        else
        {
            amountGrossGiftCardItemCredit = getAmountGrossGiftCardItemCredit().add(value);
        }
    }

    /**
     * Adds an amount to the unitsGrossGiftCardItemCredit already stored
     *
     * @param value amount to add to the pre-existing amount.
     */
    public void addUnitsGrossGiftCardItemCredit(BigDecimal value)
    {
        if (unitsGrossGiftCardItemCredit == null)
        {
            unitsGrossGiftCardItemCredit = value;
        }
        else
        {
            unitsGrossGiftCardItemCredit = unitsGrossGiftCardItemCredit.add(value);
        }
    }

    /**
     * Retrieve the amount of gross gift card item credit voided
     *
     * @return amountGrossGiftCardItemCreditVoided
     */
    public CurrencyIfc getAmountGrossGiftCardItemCreditVoided()
    {
        if (amountGrossGiftCardItemCreditVoided == null)
        {
            amountGrossGiftCardItemCreditVoided = DomainGateway.getBaseCurrencyInstance();
        }
        return amountGrossGiftCardItemCreditVoided;
    }

    /**
     * Retrieve the units of GrossGiftCardItemCreditVoided
     *
     * @return unitsGrossGiftCardItemCreditVoided
     */
    public BigDecimal getUnitsGrossGiftCardItemCreditVoided()
    {
        if (unitsGrossGiftCardItemCreditVoided == null)
        {
            unitsGrossGiftCardItemCreditVoided = BigDecimal.ZERO;
        }
        return unitsGrossGiftCardItemCreditVoided;
    }

    /**
     * Sets amount of gross gift card item credit voideds
     *
     * @param value amount of the gross gift card item credit voided
     */
    public void setAmountGrossGiftCardItemCreditVoided(CurrencyIfc value)
    {
        amountGrossGiftCardItemCreditVoided = value;
    }

    /**
     * Sets units of GrossGiftCardItemCreditVoided
     *
     * @param value units of the GrossGiftCardItemCreditVoided
     */
    public void setUnitsGrossGiftCardItemCreditVoided(BigDecimal value)
    {
        unitsGrossGiftCardItemCreditVoided = value;
    }

    /**
     * Adds an amount to the amountGrossGiftCardItemCreditVoided already stored
     *
     * @param value amount to add to the pre-existing amount.
     */
    public void addAmountGrossGiftCardItemCreditVoided(CurrencyIfc value)
    {
        if (amountGrossGiftCardItemCreditVoided == null)
        {
            amountGrossGiftCardItemCreditVoided = value;
        }
        else
        {
            amountGrossGiftCardItemCreditVoided = getAmountGrossGiftCardItemCreditVoided().add(value);
        }
    }

    /**
     * Adds an amount to the unitsGrossGiftCardItemCreditVoided already stored
     *
     * @param value amount to add to the pre-existing amount.
     */
    public void addUnitsGrossGiftCardItemCreditVoided(BigDecimal value)
    {
        if (unitsGrossGiftCardItemCreditVoided == null)
        {
            unitsGrossGiftCardItemCreditVoided = value;
        }
        else
        {
            unitsGrossGiftCardItemCreditVoided = unitsGrossGiftCardItemCreditVoided.add(value);
        }
    }

    /**
     * Retrive the net amount of GiftCardItems credited. The net result is
     * defined as gross sales - voids.
     *
     * @return The net amount of gift card items credit
     */
    public CurrencyIfc getNetAmountGiftCardItemCredit()
    {
        return getAmountGrossGiftCardItemCredit().subtract(getAmountGrossGiftCardItemCreditVoided());
    }

    /**
     * Retrive the net amount of GiftCardItemCredit. The net result is defined
     * as gross amount - voids.
     *
     * @return The net amount of transaction employee discount
     */
    public BigDecimal getNetUnitsGiftCardItemCredit()
    {
        return getUnitsGrossGiftCardItemCredit().subtract(getUnitsGrossGiftCardItemCreditVoided());
    }

    /**
     * Retrieve the amount of gross gift certificates redeemed
     *
     * @return amountGrossGiftCertificatesRedeemed
     */
    public CurrencyIfc getAmountGrossGiftCertificatesRedeemed()
    {
        if (amountGrossGiftCertificatesRedeemed == null)
        {
            amountGrossGiftCertificatesRedeemed = DomainGateway.getBaseCurrencyInstance();
        }
        return amountGrossGiftCertificatesRedeemed;
    }

    /**
     * Retrieve the units of GrossGiftCertificatesRedeemed
     *
     * @return unitsGrossGiftCertificatesRedeemed
     */
    public BigDecimal getUnitsGrossGiftCertificatesRedeemed()
    {
        if (unitsGrossGiftCertificatesRedeemed == null)
        {
            unitsGrossGiftCertificatesRedeemed = BigDecimal.ZERO;
        }
        return unitsGrossGiftCertificatesRedeemed;
    }

    /**
     * Sets amount of gross gift certificates redeemeds
     *
     * @param value amount of the gross gift certificates redeemed
     */
    public void setAmountGrossGiftCertificatesRedeemed(CurrencyIfc value)
    {
        amountGrossGiftCertificatesRedeemed = value;
    }

    /**
     * Sets units of GrossGiftCertificatesRedeemed
     *
     * @param value units of the GrossGiftCertificatesRedeemed
     */
    public void setUnitsGrossGiftCertificatesRedeemed(BigDecimal value)
    {
        unitsGrossGiftCertificatesRedeemed = value;
    }

    /**
     * Adds an amount to the amountGrossGiftCertificatesRedeemed already stored
     *
     * @param value amount to add to the pre-existing amount.
     */
    public void addAmountGrossGiftCertificatesRedeemed(CurrencyIfc value)
    {
        if (amountGrossGiftCertificatesRedeemed == null)
        {
            amountGrossGiftCertificatesRedeemed = value;
        }
        else
        {
            amountGrossGiftCertificatesRedeemed = getAmountGrossGiftCertificatesRedeemed().add(value);
        }
    }

    /**
     * Adds an amount to the unitsGrossGiftCertificatesRedeemed already stored
     *
     * @param value amount to add to the pre-existing amount.
     */
    public void addUnitsGrossGiftCertificatesRedeemed(BigDecimal value)
    {
        if (unitsGrossGiftCertificatesRedeemed == null)
        {
            unitsGrossGiftCertificatesRedeemed = value;
        }
        else
        {
            unitsGrossGiftCertificatesRedeemed = unitsGrossGiftCertificatesRedeemed.add(value);
        }
    }

    /**
     * Retrieve the amount of gross gift certificates redeemed voided
     *
     * @return amountGrossGiftCertificatesRedeemedVoided
     */
    public CurrencyIfc getAmountGrossGiftCertificatesRedeemedVoided()
    {
        if (amountGrossGiftCertificatesRedeemedVoided == null)
        {
            amountGrossGiftCertificatesRedeemedVoided = DomainGateway.getBaseCurrencyInstance();
        }
        return amountGrossGiftCertificatesRedeemedVoided;
    }

    /**
     * Retrieve the units of GrossGiftCertificatesRedeemedVoided
     *
     * @return unitsGrossGiftCertificatesRedeemedVoided
     */
    public BigDecimal getUnitsGrossGiftCertificatesRedeemedVoided()
    {
        if (unitsGrossGiftCertificatesRedeemedVoided == null)
        {
            unitsGrossGiftCertificatesRedeemedVoided = BigDecimal.ZERO;
        }
        return unitsGrossGiftCertificatesRedeemedVoided;
    }

    /**
     * Sets amount of gross gift certificates redeemed voided
     *
     * @param value amount of the gross gift certificates redeemed voided
     */
    public void setAmountGrossGiftCertificatesRedeemedVoided(CurrencyIfc value)
    {
        amountGrossGiftCertificatesRedeemedVoided = value;
    }

    /**
     * Sets units of GrossGiftCertificatesRedeemedVoided
     *
     * @param value units of the GrossGiftCertificatesRedeemedVoided
     */
    public void setUnitsGrossGiftCertificatesRedeemedVoided(BigDecimal value)
    {
        unitsGrossGiftCertificatesRedeemedVoided = value;
    }

    /**
     * Adds an amount to the amountGrossGiftCertificatesRedeemedVoided already
     * stored
     *
     * @param value amount to add to the pre-existing amount.
     */
    public void addAmountGrossGiftCertificatesRedeemedVoided(CurrencyIfc value)
    {
        if (amountGrossGiftCertificatesRedeemedVoided == null)
        {
            amountGrossGiftCertificatesRedeemedVoided = value;
        }
        else
        {
            amountGrossGiftCertificatesRedeemedVoided = getAmountGrossGiftCertificatesRedeemedVoided().add(value);
        }
    }

    /**
     * Adds an amount to the unitsGrossGiftCertificatesRedeemedVoided already
     * stored
     *
     * @param value amount to add to the pre-existing amount.
     */
    public void addUnitsGrossGiftCertificatesRedeemedVoided(BigDecimal value)
    {
        if (unitsGrossGiftCertificatesRedeemedVoided == null)
        {
            unitsGrossGiftCertificatesRedeemedVoided = value;
        }
        else
        {
            unitsGrossGiftCertificatesRedeemedVoided = unitsGrossGiftCertificatesRedeemedVoided.add(value);
        }
    }

    /**
     * Retrive the net amount of GiftCertificatesRedeemed. The net result is
     * defined as gross amount - voids.
     *
     * @return The net amount of gift certificates redeemed
     */
    public CurrencyIfc getNetAmountGiftCertificatesRedeemed()
    {
        return getAmountGrossGiftCertificatesRedeemed().subtract(getAmountGrossGiftCertificatesRedeemedVoided());
    }

    /**
     * Retrive the net amount of GiftCertificatesRedeemed. The net result is
     * defined as gross amount - voids.
     *
     * @return The net amount of transaction employee discount
     */
    public BigDecimal getNetUnitsGiftCertificatesRedeemed()
    {
        return getUnitsGrossGiftCertificatesRedeemed().subtract(getUnitsGrossGiftCertificatesRedeemedVoided());
    }

    /**
     * Retrieve the amount of gross store credits redeemed
     *
     * @return amountGrossStoreCreditsIssued
     */
    public CurrencyIfc getAmountGrossStoreCreditsIssued()
    {
        if (amountGrossStoreCreditsIssued == null)
        {
            amountGrossStoreCreditsIssued = DomainGateway.getBaseCurrencyInstance();
        }
        return amountGrossStoreCreditsIssued;
    }

    /**
     * Retrieve the units of GrossStoreCreditsIssued
     *
     * @return unitsGrossStoreCreditsIssued
     */
    public BigDecimal getUnitsGrossStoreCreditsIssued()
    {
        if (unitsGrossStoreCreditsIssued == null)
        {
            unitsGrossStoreCreditsIssued = BigDecimal.ZERO;
        }
        return unitsGrossStoreCreditsIssued;
    }

    /**
     * Sets amount of gross store credits redeemeds
     *
     * @param value amount of the gross store credits redeemed
     */
    public void setAmountGrossStoreCreditsIssued(CurrencyIfc value)
    {
        amountGrossStoreCreditsIssued = value;
    }

    /**
     * Sets units of GrossStoreCreditsIssued
     *
     * @param value units of the GrossStoreCreditsIssued
     */
    public void setUnitsGrossStoreCreditsIssued(BigDecimal value)
    {
        unitsGrossStoreCreditsIssued = value;
    }

    /**
     * Adds an amount to the amountGrossStoreCreditsIssued already stored
     *
     * @param value amount to add to the pre-existing amount.
     */
    public void addAmountGrossStoreCreditsIssued(CurrencyIfc value)
    {
        if (amountGrossStoreCreditsIssued == null)
        {
            amountGrossStoreCreditsIssued = value;
        }
        else
        {
            amountGrossStoreCreditsIssued = getAmountGrossStoreCreditsIssued().add(value);
        }
    }

    /**
     * Adds an amount to the unitsGrossStoreCreditsIssued already stored
     *
     * @param value amount to add to the pre-existing amount.
     */
    public void addUnitsGrossStoreCreditsIssued(BigDecimal value)
    {
        if (unitsGrossStoreCreditsIssued == null)
        {
            unitsGrossStoreCreditsIssued = value;
        }
        else
        {
            unitsGrossStoreCreditsIssued = unitsGrossStoreCreditsIssued.add(value);
        }
    }

    /**
     * Retrieve the amount of gross store credits redeemed voided
     *
     * @return amountGrossStoreCreditsIssuedVoided
     */
    public CurrencyIfc getAmountGrossStoreCreditsIssuedVoided()
    {
        if (amountGrossStoreCreditsIssuedVoided == null)
        {
            amountGrossStoreCreditsIssuedVoided = DomainGateway.getBaseCurrencyInstance();
        }
        return amountGrossStoreCreditsIssuedVoided;
    }

    /**
     * Retrieve the units of GrossStoreCreditsIssuedVoided
     *
     * @return unitsGrossStoreCreditsIssuedVoided
     */
    public BigDecimal getUnitsGrossStoreCreditsIssuedVoided()
    {
        if (unitsGrossStoreCreditsIssuedVoided == null)
        {
            unitsGrossStoreCreditsIssuedVoided = BigDecimal.ZERO;
        }
        return unitsGrossStoreCreditsIssuedVoided;
    }

    /**
     * Sets amount of gross store credits redeemed voided
     *
     * @param value amount of the gross store credits redeemed voided
     */
    public void setAmountGrossStoreCreditsIssuedVoided(CurrencyIfc value)
    {
        amountGrossStoreCreditsIssuedVoided = value;
    }

    /**
     * Sets units of GrossStoreCreditsIssuedVoided
     *
     * @param value units of the GrossStoreCreditsIssuedVoided
     */
    public void setUnitsGrossStoreCreditsIssuedVoided(BigDecimal value)
    {
        unitsGrossStoreCreditsIssuedVoided = value;
    }

    /**
     * Adds an amount to the amountGrossStoreCreditsIssuedVoided already stored
     *
     * @param value amount to add to the pre-existing amount.
     */
    public void addAmountGrossStoreCreditsIssuedVoided(CurrencyIfc value)
    {
        if (amountGrossStoreCreditsIssuedVoided == null)
        {
            amountGrossStoreCreditsIssuedVoided = value;
        }
        else
        {
            amountGrossStoreCreditsIssuedVoided = getAmountGrossStoreCreditsIssuedVoided().add(value);
        }
    }

    /**
     * Adds an amount to the unitsGrossStoreCreditsIssuedVoided already stored
     *
     * @param value amount to add to the pre-existing amount.
     */
    public void addUnitsGrossStoreCreditsIssuedVoided(BigDecimal value)
    {
        if (unitsGrossStoreCreditsIssuedVoided == null)
        {
            unitsGrossStoreCreditsIssuedVoided = value;
        }
        else
        {
            unitsGrossStoreCreditsIssuedVoided = unitsGrossStoreCreditsIssuedVoided.add(value);
        }
    }

    /**
     * Retrive the net amount of StoreCreditsIssued. The net result is defined
     * as gross amount - voids.
     *
     * @return The net amount of store credits redeemed
     */
    public CurrencyIfc getNetAmountStoreCreditsIssued()
    {
        return getAmountGrossStoreCreditsIssued().subtract(getAmountGrossStoreCreditsIssuedVoided());
    }

    /**
     * Retrive the net amount of StoreCreditsIssued. The net result is defined
     * as gross amount - voids.
     *
     * @return The net amount of transaction employee discount
     */
    public BigDecimal getNetUnitsStoreCreditsIssued()
    {
        return getUnitsGrossStoreCreditsIssued().subtract(getUnitsGrossStoreCreditsIssuedVoided());
    }

    /**
     * Retrieve the amount of gross store credits redeemed
     *
     * @return amountGrossStoreCreditsRedeemed
     */
    public CurrencyIfc getAmountGrossStoreCreditsRedeemed()
    {
        if (amountGrossStoreCreditsRedeemed == null)
        {
            amountGrossStoreCreditsRedeemed = DomainGateway.getBaseCurrencyInstance();
        }
        return amountGrossStoreCreditsRedeemed;
    }

    /**
     * Retrieve the units of GrossStoreCreditsRedeemed
     *
     * @return unitsGrossStoreCreditsRedeemed
     */
    public BigDecimal getUnitsGrossStoreCreditsRedeemed()
    {
        if (unitsGrossStoreCreditsRedeemed == null)
        {
            unitsGrossStoreCreditsRedeemed = BigDecimal.ZERO;
        }
        return unitsGrossStoreCreditsRedeemed;
    }

    /**
     * Sets amount of gross store credits redeemeds
     *
     * @param value amount of the gross store credits redeemed
     */
    public void setAmountGrossStoreCreditsRedeemed(CurrencyIfc value)
    {
        amountGrossStoreCreditsRedeemed = value;
    }

    /**
     * Sets units of GrossStoreCreditsRedeemed
     *
     * @param value units of the GrossStoreCreditsRedeemed
     */
    public void setUnitsGrossStoreCreditsRedeemed(BigDecimal value)
    {
        unitsGrossStoreCreditsRedeemed = value;
    }

    /**
     * Adds an amount to the amountGrossStoreCreditsRedeemed already stored
     *
     * @param value amount to add to the pre-existing amount.
     */
    public void addAmountGrossStoreCreditsRedeemed(CurrencyIfc value)
    {
        if (amountGrossStoreCreditsRedeemed == null)
        {
            amountGrossStoreCreditsRedeemed = value;
        }
        else
        {
            amountGrossStoreCreditsRedeemed = getAmountGrossStoreCreditsRedeemed().add(value);
        }
    }

    /**
     * Adds an amount to the unitsGrossStoreCreditsRedeemed already stored
     *
     * @param value amount to add to the pre-existing amount.
     */
    public void addUnitsGrossStoreCreditsRedeemed(BigDecimal value)
    {
        if (unitsGrossStoreCreditsRedeemed == null)
        {
            unitsGrossStoreCreditsRedeemed = value;
        }
        else
        {
            unitsGrossStoreCreditsRedeemed = unitsGrossStoreCreditsRedeemed.add(value);
        }
    }

    /**
     * Retrieve the amount of gross store credits redeemed voided
     *
     * @return amountGrossStoreCreditsRedeemedVoided
     */
    public CurrencyIfc getAmountGrossStoreCreditsRedeemedVoided()
    {
        if (amountGrossStoreCreditsRedeemedVoided == null)
        {
            amountGrossStoreCreditsRedeemedVoided = DomainGateway.getBaseCurrencyInstance();
        }
        return amountGrossStoreCreditsRedeemedVoided;
    }

    /**
     * Retrieve the units of GrossStoreCreditsRedeemedVoided
     *
     * @return unitsGrossStoreCreditsRedeemedVoided
     */
    public BigDecimal getUnitsGrossStoreCreditsRedeemedVoided()
    {
        if (unitsGrossStoreCreditsRedeemedVoided == null)
        {
            unitsGrossStoreCreditsRedeemedVoided = BigDecimal.ZERO;
        }
        return unitsGrossStoreCreditsRedeemedVoided;
    }

    /**
     * Sets amount of gross store credits redeemed voided
     *
     * @param value amount of the gross store credits redeemed voided
     */
    public void setAmountGrossStoreCreditsRedeemedVoided(CurrencyIfc value)
    {
        amountGrossStoreCreditsRedeemedVoided = value;
    }

    /**
     * Sets units of GrossStoreCreditsRedeemedVoided
     *
     * @param value units of the GrossStoreCreditsRedeemedVoided
     */
    public void setUnitsGrossStoreCreditsRedeemedVoided(BigDecimal value)
    {
        unitsGrossStoreCreditsRedeemedVoided = value;
    }

    /**
     * Adds an amount to the amountGrossStoreCreditsRedeemedVoided already
     * stored
     *
     * @param value amount to add to the pre-existing amount.
     */
    public void addAmountGrossStoreCreditsRedeemedVoided(CurrencyIfc value)
    {
        if (amountGrossStoreCreditsRedeemedVoided == null)
        {
            amountGrossStoreCreditsRedeemedVoided = value;
        }
        else
        {
            amountGrossStoreCreditsRedeemedVoided = getAmountGrossStoreCreditsRedeemedVoided().add(value);
        }
    }

    /**
     * Adds an amount to the unitsGrossStoreCreditsRedeemedVoided already stored
     *
     * @param value amount to add to the pre-existing amount.
     */
    public void addUnitsGrossStoreCreditsRedeemedVoided(BigDecimal value)
    {
        if (unitsGrossStoreCreditsRedeemedVoided == null)
        {
            unitsGrossStoreCreditsRedeemedVoided = value;
        }
        else
        {
            unitsGrossStoreCreditsRedeemedVoided = unitsGrossStoreCreditsRedeemedVoided.add(value);
        }
    }

    /**
     * Retrive the net amount of StoreCreditsRedeemed. The net result is defined
     * as gross amount - voids.
     *
     * @return The net amount of store credits redeemed
     */
    public CurrencyIfc getNetAmountStoreCreditsRedeemed()
    {
        return getAmountGrossStoreCreditsRedeemed().subtract(getAmountGrossStoreCreditsRedeemedVoided());
    }

    /**
     * Retrive the net amount of StoreCreditsRedeemed. The net result is defined
     * as gross amount - voids.
     *
     * @return The net amount of transaction employee discount
     */
    public BigDecimal getNetUnitsStoreCreditsRedeemed()
    {
        return getUnitsGrossStoreCreditsRedeemed().subtract(getUnitsGrossStoreCreditsRedeemedVoided());
    }

    /**
     * Retrieve the amount of gross item employee discount
     *
     * @return amountGrossItemEmployeeDiscount
     */
    public CurrencyIfc getAmountGrossItemEmployeeDiscount()
    {
        if (amountGrossItemEmployeeDiscount == null)
        {
            amountGrossItemEmployeeDiscount = DomainGateway.getBaseCurrencyInstance();
        }
        return amountGrossItemEmployeeDiscount;
    }

    /**
     * Retrieve the units of GrossItemEmployeeDiscount
     *
     * @return unitsGrossItemEmployeeDiscount
     */
    public BigDecimal getUnitsGrossItemEmployeeDiscount()
    {
        if (unitsGrossItemEmployeeDiscount == null)
        {
            unitsGrossItemEmployeeDiscount = BigDecimal.ZERO;
        }
        return unitsGrossItemEmployeeDiscount;
    }

    /**
     * Sets amount of gross item employee discounts
     *
     * @param value amount of the gross item employee discount
     */
    public void setAmountGrossItemEmployeeDiscount(CurrencyIfc value)
    {
        amountGrossItemEmployeeDiscount = value;
    }

    /**
     * Sets units of GrossItemEmployeeDiscount
     *
     * @param value units of the GrossItemEmployeeDiscount
     */
    public void setUnitsGrossItemEmployeeDiscount(BigDecimal value)
    {
        unitsGrossItemEmployeeDiscount = value;
    }

    /**
     * Adds an amount to the amountGrossItemEmployeeDiscount already stored
     *
     * @param value amount to add to the pre-existing amount.
     */
    public void addAmountGrossItemEmployeeDiscount(CurrencyIfc value)
    {
        if (amountGrossItemEmployeeDiscount == null)
        {
            amountGrossItemEmployeeDiscount = value;
        }
        else
        {
            amountGrossItemEmployeeDiscount = getAmountGrossItemEmployeeDiscount().add(value);
        }
    }

    /**
     * Adds an amount to the unitsGrossItemEmployeeDiscount already stored
     *
     * @param value amount to add to the pre-existing amount.
     */
    public void addUnitsGrossItemEmployeeDiscount(BigDecimal value)
    {
        if (unitsGrossItemEmployeeDiscount == null)
        {
            unitsGrossItemEmployeeDiscount = value;
        }
        else
        {
            unitsGrossItemEmployeeDiscount = unitsGrossItemEmployeeDiscount.add(value);
        }
    }

    /**
     * Retrieve the amount of gross item employee discount voided
     *
     * @return amountGrossItemEmployeeDiscountVoided
     */
    public CurrencyIfc getAmountGrossItemEmployeeDiscountVoided()
    {
        if (amountGrossItemEmployeeDiscountVoided == null)
        {
            amountGrossItemEmployeeDiscountVoided = DomainGateway.getBaseCurrencyInstance();
        }
        return amountGrossItemEmployeeDiscountVoided;
    }

    /**
     * Retrieve the units of GrossItemEmployeeDiscountVoided
     *
     * @return unitsGrossItemEmployeeDiscountVoided
     */
    public BigDecimal getUnitsGrossItemEmployeeDiscountVoided()
    {
        if (unitsGrossItemEmployeeDiscountVoided == null)
        {
            unitsGrossItemEmployeeDiscountVoided = BigDecimal.ZERO;
        }
        return unitsGrossItemEmployeeDiscountVoided;
    }

    /**
     * Sets amount of gross item employee discount voided
     *
     * @param value amount of the gross item employee discount voided
     */
    public void setAmountGrossItemEmployeeDiscountVoided(CurrencyIfc value)
    {
        amountGrossItemEmployeeDiscountVoided = value;
    }

    /**
     * Sets units of GrossItemEmployeeDiscountVoided
     *
     * @param value units of the GrossItemEmployeeDiscountVoided
     */
    public void setUnitsGrossItemEmployeeDiscountVoided(BigDecimal value)
    {
        unitsGrossItemEmployeeDiscountVoided = value;
    }

    /**
     * Adds an amount to the amountGrossItemEmployeeDiscountVoided already
     * stored
     *
     * @param value amount to add to the pre-existing amount.
     */
    public void addAmountGrossItemEmployeeDiscountVoided(CurrencyIfc value)
    {
        if (amountGrossItemEmployeeDiscountVoided == null)
        {
            amountGrossItemEmployeeDiscountVoided = value;
        }
        else
        {
            amountGrossItemEmployeeDiscountVoided = getAmountGrossItemEmployeeDiscountVoided().add(value);
        }
    }

    /**
     * Adds an amount to the unitsGrossItemEmployeeDiscountVoided already stored
     *
     * @param value amount to add to the pre-existing amount.
     */
    public void addUnitsGrossItemEmployeeDiscountVoided(BigDecimal value)
    {
        if (unitsGrossItemEmployeeDiscountVoided == null)
        {
            unitsGrossItemEmployeeDiscountVoided = value;
        }
        else
        {
            unitsGrossItemEmployeeDiscountVoided = unitsGrossItemEmployeeDiscountVoided.add(value);
        }
    }

    /**
     * Retrive the net amount of ItemEmployeeDiscount. The net result is defined
     * as gross amount - voids.
     *
     * @return The net amount of item employee discount
     */
    public CurrencyIfc getNetAmountItemEmployeeDiscount()
    {
        return getAmountGrossItemEmployeeDiscount().subtract(getAmountGrossItemEmployeeDiscountVoided());
    }

    /**
     * Retrive the net units of ItemEmployeeDiscount. The net result is defined
     * as gross amount - voids.
     *
     * @return The net units of item employee discount
     */
    public BigDecimal getNetUnitsItemEmployeeDiscount()
    {
        return getUnitsGrossItemEmployeeDiscount().subtract(getUnitsGrossItemEmployeeDiscountVoided());
    }

    /**
     * Retrieve the amount of gross transaction employee discount
     *
     * @return amountGrossTransactionEmployeeDiscount
     */
    public CurrencyIfc getAmountGrossTransactionEmployeeDiscount()
    {
        if (amountGrossTransactionEmployeeDiscount == null)
        {
            amountGrossTransactionEmployeeDiscount = DomainGateway.getBaseCurrencyInstance();
        }
        return amountGrossTransactionEmployeeDiscount;
    }

    /**
     * Retrieve the units of GrossTransactionEmployeeDiscount
     *
     * @return unitsGrossTransactionEmployeeDiscount
     */
    public BigDecimal getUnitsGrossTransactionEmployeeDiscount()
    {
        if (unitsGrossTransactionEmployeeDiscount == null)
        {
            unitsGrossTransactionEmployeeDiscount = BigDecimal.ZERO;
        }
        return unitsGrossTransactionEmployeeDiscount;
    }

    /**
     * Sets amount of gross transaction employee discounts
     *
     * @param value amount of the gross transaction employee discount
     */
    public void setAmountGrossTransactionEmployeeDiscount(CurrencyIfc value)
    {
        amountGrossTransactionEmployeeDiscount = value;
    }

    /**
     * Sets units of GrossTransactionEmployeeDiscount
     *
     * @param value units of the GrossTransactionEmployeeDiscount
     */
    public void setUnitsGrossTransactionEmployeeDiscount(BigDecimal value)
    {
        unitsGrossTransactionEmployeeDiscount = value;
    }

    /**
     * Adds an amount to the amountGrossTransactionEmployeeDiscount already
     * stored
     *
     * @param value amount to add to the pre-existing amount.
     */
    public void addAmountGrossTransactionEmployeeDiscount(CurrencyIfc value)
    {
        if (amountGrossTransactionEmployeeDiscount == null)
        {
            amountGrossTransactionEmployeeDiscount = value;
        }
        else
        {
            amountGrossTransactionEmployeeDiscount = getAmountGrossTransactionEmployeeDiscount().add(value);
        }
    }

    /**
     * Adds an amount to the unitsGrossTransactionEmployeeDiscount already
     * stored
     *
     * @param value amount to add to the pre-existing amount.
     */
    public void addUnitsGrossTransactionEmployeeDiscount(BigDecimal value)
    {
        if (unitsGrossTransactionEmployeeDiscount == null)
        {
            unitsGrossTransactionEmployeeDiscount = value;
        }
        else
        {
            unitsGrossTransactionEmployeeDiscount = unitsGrossTransactionEmployeeDiscount.add(value);
        }
    }

    /**
     * Retrieve the amount of gross transaction employee discount voided
     *
     * @return amountGrossTransactionEmployeeDiscountVoided
     */
    public CurrencyIfc getAmountGrossTransactionEmployeeDiscountVoided()
    {
        if (amountGrossTransactionEmployeeDiscountVoided == null)
        {
            amountGrossTransactionEmployeeDiscountVoided = DomainGateway.getBaseCurrencyInstance();
        }
        return amountGrossTransactionEmployeeDiscountVoided;
    }

    /**
     * Retrieve the units of GrossTransactionEmployeeDiscountVoided
     *
     * @return unitsGrossTransactionEmployeeDiscountVoided
     */
    public BigDecimal getUnitsGrossTransactionEmployeeDiscountVoided()
    {
        if (unitsGrossTransactionEmployeeDiscountVoided == null)
        {
            unitsGrossTransactionEmployeeDiscountVoided = BigDecimal.ZERO;
        }
        return unitsGrossTransactionEmployeeDiscountVoided;
    }

    /**
     * Sets amount of gross transaction employee discount voided
     *
     * @param value amount of the gross transaction employee discount voided
     */
    public void setAmountGrossTransactionEmployeeDiscountVoided(CurrencyIfc value)
    {
        amountGrossTransactionEmployeeDiscountVoided = value;
    }

    /**
     * Sets units of GrossTransactionEmployeeDiscountVoided
     *
     * @param value units of the GrossTransactionEmployeeDiscountVoided
     */
    public void setUnitsGrossTransactionEmployeeDiscountVoided(BigDecimal value)
    {
        unitsGrossTransactionEmployeeDiscountVoided = value;
    }

    /**
     * Adds an amount to the amountGrossTransactionEmployeeDiscountVoided
     * already stored
     *
     * @param value amount to add to the pre-existing amount.
     */
    public void addAmountGrossTransactionEmployeeDiscountVoided(CurrencyIfc value)
    {
        if (amountGrossTransactionEmployeeDiscountVoided == null)
        {
            amountGrossTransactionEmployeeDiscountVoided = value;
        }
        else
        {
            amountGrossTransactionEmployeeDiscountVoided = getAmountGrossTransactionEmployeeDiscountVoided().add(value);
        }
    }

    /**
     * Adds an amount to the unitsGrossTransactionEmployeeDiscountVoided already
     * stored
     *
     * @param value amount to add to the pre-existing amount.
     */
    public void addUnitsGrossTransactionEmployeeDiscountVoided(BigDecimal value)
    {
        if (unitsGrossTransactionEmployeeDiscountVoided == null)
        {
            unitsGrossTransactionEmployeeDiscountVoided = value;
        }
        else
        {
            unitsGrossTransactionEmployeeDiscountVoided = unitsGrossTransactionEmployeeDiscountVoided.add(value);
        }
    }

    /**
     * Retrive the net amount of TransactionEmployeeDiscount. The net result is
     * defined as gross amount - voids.
     *
     * @return The net amount of transaction employee discount
     */
    public CurrencyIfc getNetAmountTransactionEmployeeDiscount()
    {
        return getAmountGrossTransactionEmployeeDiscount().subtract(getAmountGrossTransactionEmployeeDiscountVoided());
    }

    /**
     * Retrive the net units of TransactionEmployeeDiscount The net result is
     * defined as gross amount - voids.
     *
     * @return The net units of transaction employee discount
     */
    public BigDecimal getNetUnitsTransactionEmployeeDiscount()
    {
        return getUnitsGrossTransactionEmployeeDiscount().subtract(getUnitsGrossTransactionEmployeeDiscountVoided());
    }

    /**
     * This method converts puts hard totals information into the builder
     * object, to be used in conjunction with setHardTotalsData.
     *
     * @param builder Builder to put hard totals into
     */
    public void getHardTotalsData(HardTotalsBuilderIfc builder)
    {
        builder.appendStringObject(getClass().getName());
        builder.appendInt(transactionCount);

        // I18N change - no reason to keep going if currency info is not
        // available
        boolean isCurrencyInfoAvailable = true;

        try
        {
            DomainGateway.getBaseCurrencyInstance();
        }
        catch (Exception e)
        {

            logger.error("Currency Information is not available - server/database may be offline");
            isCurrencyInfoAvailable = false;
        }

        if (isCurrencyInfoAvailable)
        {
            builder.appendStringObject(getAmountGrossTaxableItemSales().getStringValue());
            builder.appendStringObject(getUnitsGrossTaxableItemSales().toString());

            builder.appendStringObject(getAmountGrossNonTaxableItemSales().getStringValue());
            builder.appendStringObject(getUnitsGrossNonTaxableItemSales().toString());

            builder.appendStringObject(getAmountGrossTaxExemptItemSales().getStringValue());
            builder.appendStringObject(getUnitsGrossTaxExemptItemSales().toString());

            builder.appendStringObject(getAmountGrossTaxableItemReturns().getStringValue());
            builder.appendStringObject(getUnitsGrossTaxableItemReturns().toString());

            builder.appendStringObject(getAmountGrossNonTaxableItemReturns().getStringValue());
            builder.appendStringObject(getUnitsGrossNonTaxableItemReturns().toString());

            builder.appendStringObject(getAmountGrossTaxExemptItemReturns().getStringValue());
            builder.appendStringObject(getUnitsGrossTaxExemptItemReturns().toString());

            builder.appendStringObject(getAmountGrossTaxableItemSalesVoided().getStringValue());
            builder.appendStringObject(getUnitsGrossTaxableItemSalesVoided().toString());

            builder.appendStringObject(getAmountGrossNonTaxableItemSalesVoided().getStringValue());
            builder.appendStringObject(getUnitsGrossNonTaxableItemSalesVoided().toString());

            builder.appendStringObject(getAmountGrossTaxExemptItemSalesVoided().getStringValue());
            builder.appendStringObject(getUnitsGrossTaxExemptItemSalesVoided().toString());

            builder.appendStringObject(getAmountGrossTaxableItemReturnsVoided().getStringValue());
            builder.appendStringObject(getUnitsGrossTaxableItemReturnsVoided().toString());

            builder.appendStringObject(getAmountGrossNonTaxableItemReturnsVoided().getStringValue());
            builder.appendStringObject(getUnitsGrossNonTaxableItemReturnsVoided().toString());

            builder.appendStringObject(getAmountGrossTaxExemptItemReturnsVoided().getStringValue());
            builder.appendStringObject(getUnitsGrossTaxExemptItemReturnsVoided().toString());

            builder.appendStringObject(getAmountTaxItemSales().getStringValue());
            builder.appendStringObject(getAmountInclusiveTaxItemSales().getStringValue());
            builder.appendStringObject(getAmountTaxItemReturns().getStringValue());
            builder.appendStringObject(getAmountInclusiveTaxItemReturns().getStringValue());

            builder.appendStringObject(getAmountTaxTransactionSales().getStringValue());
            builder.appendStringObject(getAmountInclusiveTaxTransactionSales().getStringValue());
            builder.appendStringObject(getAmountTaxTransactionReturns().getStringValue());
            builder.appendStringObject(getAmountInclusiveTaxTransactionReturns().getStringValue());

            builder.appendStringObject(getAmountGrossTaxableTransactionSales().getStringValue());
            builder.appendInt(countGrossTaxableTransactionSales);

            builder.appendStringObject(getAmountGrossNonTaxableTransactionSales().getStringValue());
            builder.appendInt(countGrossNonTaxableTransactionSales);

            builder.appendStringObject(getAmountGrossTaxExemptTransactionSales().getStringValue());
            builder.appendInt(countGrossTaxExemptTransactionSales);

            builder.appendStringObject(getAmountGrossTaxableTransactionReturns().getStringValue());
            builder.appendInt(countGrossTaxableTransactionReturns);

            builder.appendStringObject(getAmountGrossNonTaxableTransactionReturns().getStringValue());
            builder.appendInt(countGrossNonTaxableTransactionReturns);

            builder.appendStringObject(getAmountGrossTaxExemptTransactionReturns().getStringValue());
            builder.appendInt(countGrossTaxExemptTransactionReturns);

            builder.appendStringObject(getAmountGrossTaxableTransactionSalesVoided().getStringValue());
            builder.appendInt(countGrossTaxableTransactionSalesVoided);

            builder.appendStringObject(getAmountGrossNonTaxableTransactionSalesVoided().getStringValue());
            builder.appendInt(countGrossNonTaxableTransactionSalesVoided);

            builder.appendStringObject(getAmountGrossTaxExemptTransactionSalesVoided().getStringValue());
            builder.appendInt(countGrossTaxExemptTransactionSalesVoided);

            builder.appendStringObject(getAmountGrossTaxableTransactionReturnsVoided().getStringValue());
            builder.appendInt(countGrossTaxableTransactionReturnsVoided);

            builder.appendStringObject(getAmountGrossNonTaxableTransactionReturnsVoided().getStringValue());
            builder.appendInt(countGrossNonTaxableTransactionReturnsVoided);

            builder.appendStringObject(getAmountGrossTaxExemptTransactionReturnsVoided().getStringValue());
            builder.appendInt(countGrossTaxExemptTransactionReturnsVoided);

            builder.appendStringObject(getAmountHousePayments().getStringValue());
            builder.appendInt(countHousePayments);

            builder.appendStringObject(getAmountTillPayIns().getStringValue());
            builder.appendInt(countTillPayIns);

            builder.appendStringObject(getAmountTillPayOuts().getStringValue());
            builder.appendInt(countTillPayOuts);

            builder.appendStringObject(getAmountGiftCertificateSales().getStringValue());
            builder.appendInt(getUnitsGiftCertificateSales());

            builder.appendStringObject(getAmountTransactionDiscounts().getStringValue());
            builder.appendInt(numberTransactionDiscounts);

            builder.appendStringObject(getAmountItemDiscounts().getStringValue());
            builder.appendInt(numberItemDiscounts);

            // restocking fees
            builder.appendStringObject(getAmountRestockingFees().getStringValue());
            builder.appendStringObject(getUnitsRestockingFees().toString());

            // shipping charges
            builder.appendStringObject(getAmountShippingCharges().getStringValue());
            builder.appendInt(numberShippingCharges);

            // shipping charges tax
            builder.appendStringObject(getAmountTaxShippingCharges().getStringValue());
            builder.appendStringObject(getAmountInclusiveTaxShippingCharges().getStringValue());

            // StoreCouponDiscounts
            builder.appendStringObject(getAmountTransactionDiscStoreCoupons().getStringValue());
            builder.appendInt(numberTransactionDiscStoreCoupons);

            builder.appendStringObject(getAmountItemDiscStoreCoupons().getStringValue());
            builder.appendInt(numberItemDiscStoreCoupons);

            builder.appendStringObject(getAmountPostVoids().getStringValue());
            builder.appendInt(numberPostVoids);

            builder.appendInt(numberNoSales);
            builder.appendStringObject(getAmountLineVoids().getStringValue());
            builder.appendStringObject(getUnitsLineVoids().toString());

            builder.appendStringObject(getAmountCancelledTransactions().getStringValue());
            builder.appendInt(numberCancelledTransactions);

            builder.appendStringObject(getAmountGrossTaxableNonMerchandiseSales().getStringValue());
            builder.appendStringObject(getUnitsGrossTaxableNonMerchandiseSales().toString());

            builder.appendStringObject(getAmountGrossNonTaxableNonMerchandiseSales().getStringValue());
            builder.appendStringObject(getUnitsGrossNonTaxableNonMerchandiseSales().toString());

            builder.appendStringObject(getAmountGrossTaxableNonMerchandiseReturns().getStringValue());
            builder.appendStringObject(getUnitsGrossTaxableNonMerchandiseReturns().toString());

            builder.appendStringObject(getAmountGrossNonTaxableNonMerchandiseReturns().getStringValue());
            builder.appendStringObject(getUnitsGrossNonTaxableNonMerchandiseReturns().toString());

            builder.appendStringObject(getAmountGrossTaxableNonMerchandiseSalesVoided().getStringValue());
            builder.appendStringObject(getUnitsGrossTaxableNonMerchandiseSalesVoided().toString());

            builder.appendStringObject(getAmountGrossNonTaxableNonMerchandiseSalesVoided().getStringValue());
            builder.appendStringObject(getUnitsGrossNonTaxableNonMerchandiseSalesVoided().toString());

            builder.appendStringObject(getAmountGrossTaxableNonMerchandiseReturnsVoided().getStringValue());
            builder.appendStringObject(getUnitsGrossTaxableNonMerchandiseReturnsVoided().toString());

            builder.appendStringObject(getAmountGrossNonTaxableNonMerchandiseReturnsVoided().getStringValue());
            builder.appendStringObject(getUnitsGrossNonTaxableNonMerchandiseReturnsVoided().toString());

            builder.appendStringObject(getAmountGrossGiftCardItemSales().getStringValue());
            builder.appendStringObject(getUnitsGrossGiftCardItemSales().toString());

            builder.appendStringObject(getAmountGrossGiftCardItemReturns().getStringValue());
            builder.appendStringObject(getUnitsGrossGiftCardItemReturns().toString());

            builder.appendStringObject(getAmountGrossGiftCardItemSalesVoided().getStringValue());
            builder.appendStringObject(getUnitsGrossGiftCardItemSalesVoided().toString());

            builder.appendStringObject(getAmountGrossGiftCardItemReturnsVoided().getStringValue());
            builder.appendStringObject(getUnitsGrossGiftCardItemReturnsVoided().toString());

            builder.appendStringObject(getAmountLayawayPayments().getStringValue());
            builder.appendInt(countLayawayPayments);

            builder.appendStringObject(getAmountLayawayNew().getStringValue());
            builder.appendInt(countLayawayNew);

            builder.appendStringObject(getAmountLayawayPickup().getStringValue());
            builder.appendInt(countLayawayPickup);

            builder.appendStringObject(getAmountLayawayDeletions().getStringValue());
            builder.appendInt(countLayawayDeletions);

            builder.appendStringObject(getAmountLayawayInitiationFees().getStringValue());
            builder.appendInt(countLayawayInitiationFees);

            builder.appendStringObject(getAmountLayawayDeletionFees().getStringValue());
            builder.appendInt(countLayawayDeletionFees);

            builder.appendStringObject(getAmountOrderPayments().getStringValue());
            builder.appendInt(countOrderPayments);

            builder.appendStringObject(getAmountSpecialOrderNew().getStringValue());

            builder.appendStringObject(getAmountSpecialOrderPartial().getStringValue());

            builder.appendStringObject(getAmountOrderCancels().getStringValue());
            builder.appendInt(countOrderCancels);

            if (startingFloatCount == null)
            {
                builder.appendStringObject("null");
            }
            else
            {
                startingFloatCount.getHardTotalsData(builder);
            }

            if (endingFloatCount == null)
            {
                builder.appendStringObject("null");
            }
            else
            {
                endingFloatCount.getHardTotalsData(builder);
            }

            if (floatCount == null)
            {
                builder.appendStringObject("null");
            }
            else
            {
                floatCount.getHardTotalsData(builder);
            }

            if (tenderCount == null)
            {
                builder.appendStringObject("null");
            }
            else
            {
                tenderCount.getHardTotalsData(builder);
            }

            if (combinedCount == null)
            {
                builder.appendStringObject("null");
            }
            else
            {
                combinedCount.getHardTotalsData(builder);
            }

            int tableSize = countTillPickups.size();
            builder.appendInt(tableSize);
            Enumeration<String> enumer = countTillPickups.keys();
            Integer count = null;
            String countryCode = null;

            while (enumer.hasMoreElements())
            {
                countryCode = enumer.nextElement();
                count = countTillPickups.get(countryCode);
                builder.appendStringObject(countryCode);
                builder.appendInt(count.intValue());
            }

            int len = 0;
            ReconcilableCountIfc[] tillPickups = getTillPickups();

            if (tillPickups != null)
            {
                len = tillPickups.length;
            }
            builder.appendInt(len);
            for (int i = 0; i < len; i++)
            {
                if (tillPickups[i] != null)
                {
                    tillPickups[i].getHardTotalsData(builder);
                }
            }

            builder.appendInt(countTillLoans);
            len = 0;
            ReconcilableCountIfc[] tillLoans = getTillLoans();
            if (tillLoans != null)
            {
                len = tillLoans.length;
            }
            builder.appendInt(len);
            for (int i = 0; i < len; i++)
            {
                if (tillLoans[i] != null)
                {
                    tillLoans[i].getHardTotalsData(builder);
                }
            }

            len = 0;
            ReconcilableCountIfc[] tillPayins = getTillPayIns();
            if (tillPayins != null)
            {
                len = tillPayins.length;
            }
            builder.appendInt(len);
            for (int i = 0; i < len; i++)
            {
                if (tillPayins[i] != null)
                {
                    tillPayins[i].getHardTotalsData(builder);
                }
            }

            len = 0;
            ReconcilableCountIfc[] tillPayouts = getTillPayOuts();
            if (tillPayouts != null)
            {
                len = tillPayouts.length;
            }
            builder.appendInt(len);
            for (int i = 0; i < len; i++)
            {
                if (tillPayouts[i] != null)
                {
                    tillPayouts[i].getHardTotalsData(builder);
                }
            }

            builder.appendStringObject(getAmountEmployeeDiscounts().getStringValue());
            builder.appendStringObject(getUnitsEmployeeDiscounts().toString());
            builder.appendStringObject(getAmountCustomerDiscounts().getStringValue());
            builder.appendStringObject(getUnitsCustomerDiscounts().toString());
            builder.appendStringObject(getAmountPriceOverrides().getStringValue());
            builder.appendStringObject(getUnitsPriceOverrides().toString());
            builder.appendStringObject(getUnitsPriceAdjustments().toString());

            // gift certificate
            builder.appendStringObject(getAmountGrossGiftCertificateIssued().getStringValue());
            builder.appendStringObject(getUnitsGrossGiftCertificateIssued().toString());

            builder.appendStringObject(getAmountGrossGiftCertificateIssuedVoided().getStringValue());
            builder.appendStringObject(getUnitsGrossGiftCertificateIssuedVoided().toString());

            builder.appendStringObject(getAmountGrossGiftCertificateTendered().getStringValue());
            builder.appendStringObject(getUnitsGrossGiftCertificateTendered().toString());

            builder.appendStringObject(getAmountGrossGiftCertificateTenderedVoided().getStringValue());
            builder.appendStringObject(getUnitsGrossGiftCertificateTenderedVoided().toString());

            builder.appendStringObject(getAmountGrossGiftCardItemIssued().getStringValue());
            builder.appendStringObject(getUnitsGrossGiftCardItemIssued().toString());

            builder.appendStringObject(getAmountGrossGiftCardItemReloaded().getStringValue());
            builder.appendStringObject(getUnitsGrossGiftCardItemReloaded().toString());

            builder.appendStringObject(getAmountGrossGiftCardItemRedeemed().getStringValue());
            builder.appendStringObject(getUnitsGrossGiftCardItemRedeemed().toString());

            builder.appendStringObject(getAmountGrossGiftCardItemIssueVoided().getStringValue());
            builder.appendStringObject(getUnitsGrossGiftCardItemIssueVoided().toString());

            builder.appendStringObject(getAmountGrossGiftCardItemReloadVoided().getStringValue());
            builder.appendStringObject(getUnitsGrossGiftCardItemReloadVoided().toString());

            builder.appendStringObject(getAmountGrossGiftCardItemRedeemedVoided().getStringValue());
            builder.appendStringObject(getUnitsGrossGiftCardItemRedeemedVoided().toString());

            builder.appendInt(getHouseCardEnrollmentsApproved());
            builder.appendInt(getHouseCardEnrollmentsDeclined());

            builder.appendStringObject(getAmountGrossGiftCardItemCredit().getStringValue());
            builder.appendStringObject(getUnitsGrossGiftCardItemCredit().toString());

            builder.appendStringObject(getAmountGrossGiftCardItemCreditVoided().getStringValue());
            builder.appendStringObject(getUnitsGrossGiftCardItemCreditVoided().toString());

            builder.appendStringObject(getAmountGrossGiftCertificatesRedeemed().getStringValue());
            builder.appendStringObject(getUnitsGrossGiftCertificatesRedeemed().toString());

            builder.appendStringObject(getAmountGrossGiftCertificatesRedeemedVoided().getStringValue());
            builder.appendStringObject(getUnitsGrossGiftCertificatesRedeemedVoided().toString());

            builder.appendStringObject(getAmountGrossStoreCreditsIssued().getStringValue());
            builder.appendStringObject(getUnitsGrossStoreCreditsIssued().toString());

            builder.appendStringObject(getAmountGrossStoreCreditsIssuedVoided().getStringValue());
            builder.appendStringObject(getUnitsGrossStoreCreditsIssuedVoided().toString());

            builder.appendStringObject(getAmountGrossStoreCreditsRedeemed().getStringValue());
            builder.appendStringObject(getUnitsGrossStoreCreditsRedeemed().toString());

            builder.appendStringObject(getAmountGrossStoreCreditsRedeemedVoided().getStringValue());
            builder.appendStringObject(getUnitsGrossStoreCreditsRedeemedVoided().toString());

            builder.appendStringObject(getAmountGrossItemEmployeeDiscount().getStringValue());
            builder.appendStringObject(getUnitsGrossItemEmployeeDiscount().toString());

            builder.appendStringObject(getAmountGrossItemEmployeeDiscountVoided().getStringValue());
            builder.appendStringObject(getUnitsGrossItemEmployeeDiscountVoided().toString());

            builder.appendStringObject(getAmountGrossTransactionEmployeeDiscount().getStringValue());
            builder.appendStringObject(getUnitsGrossTransactionEmployeeDiscount().toString());

            builder.appendStringObject(getAmountGrossTransactionEmployeeDiscountVoided().getStringValue());
            builder.appendStringObject(getUnitsGrossTransactionEmployeeDiscountVoided().toString());
            
            builder.appendStringObject(getAmountBillPayments().getStringValue());
            builder.appendInt(countBillPayments);

            builder.appendStringObject(getAmountChangeRoundedIn().getStringValue());
            builder.appendStringObject(getAmountChangeRoundedOut().getStringValue());
        }
    }

    /**
     * This method populates this object from a builder object which contains
     * all the data needed to set the attributes of this object.
     *
     * @param builder Builder object containing hard totals data
     * @throws HardTotalsFormatException if there is any error reading the hard
     *             totals
     */
    public void setHardTotalsData(HardTotalsBuilderIfc builder) throws HardTotalsFormatException
    {

        transactionCount = builder.getIntField();

        boolean isCurrencyInfoAvailable = true;

        try
        {
            DomainGateway.getBaseCurrencyInstance();
        }
        catch (Exception e)
        {

            logger.error("Currency Information is not available - server/database may be offline");
            isCurrencyInfoAvailable = false;
        }

        if (isCurrencyInfoAvailable)
        {
            amountGrossTaxableItemSales = DomainGateway.getBaseCurrencyInstance(builder.getStringObject());

            unitsGrossTaxableItemSales = new BigDecimal(builder.getStringObject());

            amountGrossNonTaxableItemSales = DomainGateway.getBaseCurrencyInstance(builder.getStringObject());
            unitsGrossNonTaxableItemSales = new BigDecimal(builder.getStringObject());

            amountGrossTaxExemptItemSales = DomainGateway.getBaseCurrencyInstance(builder.getStringObject());
            unitsGrossTaxExemptItemSales = new BigDecimal(builder.getStringObject());

            amountGrossTaxableItemReturns = DomainGateway.getBaseCurrencyInstance(builder.getStringObject());
            unitsGrossTaxableItemReturns = new BigDecimal(builder.getStringObject());

            amountGrossNonTaxableItemReturns = DomainGateway.getBaseCurrencyInstance(builder.getStringObject());
            unitsGrossNonTaxableItemReturns = new BigDecimal(builder.getStringObject());

            amountGrossTaxExemptItemReturns = DomainGateway.getBaseCurrencyInstance(builder.getStringObject());
            unitsGrossTaxExemptItemReturns = new BigDecimal(builder.getStringObject());

            amountGrossTaxableItemSalesVoided = DomainGateway.getBaseCurrencyInstance(builder.getStringObject());
            unitsGrossTaxableItemSalesVoided = new BigDecimal(builder.getStringObject());

            amountGrossNonTaxableItemSalesVoided = DomainGateway.getBaseCurrencyInstance(builder.getStringObject());
            unitsGrossNonTaxableItemSalesVoided = new BigDecimal(builder.getStringObject());

            amountGrossTaxExemptItemSalesVoided = DomainGateway.getBaseCurrencyInstance(builder.getStringObject());
            unitsGrossTaxExemptItemSalesVoided = new BigDecimal(builder.getStringObject());

            amountGrossTaxableItemReturnsVoided = DomainGateway.getBaseCurrencyInstance(builder.getStringObject());
            unitsGrossTaxableItemReturnsVoided = new BigDecimal(builder.getStringObject());

            amountGrossNonTaxableItemReturnsVoided = DomainGateway.getBaseCurrencyInstance(builder.getStringObject());
            unitsGrossNonTaxableItemReturnsVoided = new BigDecimal(builder.getStringObject());

            amountGrossTaxExemptItemReturnsVoided = DomainGateway.getBaseCurrencyInstance(builder.getStringObject());
            unitsGrossTaxExemptItemReturnsVoided = new BigDecimal(builder.getStringObject());

            amountTaxItemSales = DomainGateway.getBaseCurrencyInstance(builder.getStringObject());
            amountInclusiveTaxItemSales = DomainGateway.getBaseCurrencyInstance(builder.getStringObject());
            amountTaxItemReturns = DomainGateway.getBaseCurrencyInstance(builder.getStringObject());
            amountInclusiveTaxItemReturns = DomainGateway.getBaseCurrencyInstance(builder.getStringObject());

            amountTaxTransactionSales = DomainGateway.getBaseCurrencyInstance(builder.getStringObject());
            amountInclusiveTaxTransactionSales = DomainGateway.getBaseCurrencyInstance(builder.getStringObject());
            amountTaxTransactionReturns = DomainGateway.getBaseCurrencyInstance(builder.getStringObject());
            amountInclusiveTaxTransactionReturns = DomainGateway.getBaseCurrencyInstance(builder.getStringObject());

            amountGrossTaxableTransactionSales = DomainGateway.getBaseCurrencyInstance(builder.getStringObject());
            countGrossTaxableTransactionSales = builder.getIntField();

            amountGrossNonTaxableTransactionSales = DomainGateway.getBaseCurrencyInstance(builder.getStringObject());
            countGrossNonTaxableTransactionSales = builder.getIntField();

            amountGrossTaxExemptTransactionSales = DomainGateway.getBaseCurrencyInstance(builder.getStringObject());
            countGrossTaxExemptTransactionSales = builder.getIntField();

            amountGrossTaxableTransactionReturns = DomainGateway.getBaseCurrencyInstance(builder.getStringObject());
            countGrossTaxableTransactionReturns = builder.getIntField();

            amountGrossNonTaxableTransactionReturns = DomainGateway.getBaseCurrencyInstance(builder.getStringObject());
            countGrossNonTaxableTransactionReturns = builder.getIntField();

            amountGrossTaxExemptTransactionReturns = DomainGateway.getBaseCurrencyInstance(builder.getStringObject());
            countGrossTaxExemptTransactionReturns = builder.getIntField();

            amountGrossTaxableTransactionSalesVoided = DomainGateway.getBaseCurrencyInstance(builder.getStringObject());
            countGrossTaxableTransactionSalesVoided = builder.getIntField();

            amountGrossNonTaxableTransactionSalesVoided = DomainGateway.getBaseCurrencyInstance(builder
                    .getStringObject());
            countGrossNonTaxableTransactionSalesVoided = builder.getIntField();

            amountGrossTaxExemptTransactionSalesVoided = DomainGateway.getBaseCurrencyInstance(builder
                    .getStringObject());
            countGrossTaxExemptTransactionSalesVoided = builder.getIntField();

            amountGrossTaxableTransactionReturnsVoided = DomainGateway.getBaseCurrencyInstance(builder
                    .getStringObject());
            countGrossTaxableTransactionReturnsVoided = builder.getIntField();

            amountGrossNonTaxableTransactionReturnsVoided = DomainGateway.getBaseCurrencyInstance(builder
                    .getStringObject());
            countGrossNonTaxableTransactionReturnsVoided = builder.getIntField();

            amountGrossTaxExemptTransactionReturnsVoided = DomainGateway.getBaseCurrencyInstance(builder
                    .getStringObject());
            countGrossTaxExemptTransactionReturnsVoided = builder.getIntField();

            amountHousePayments = DomainGateway.getBaseCurrencyInstance(builder.getStringObject());
            countHousePayments = builder.getIntField();

            amountTillPayIns = DomainGateway.getBaseCurrencyInstance(builder.getStringObject());
            countTillPayIns = builder.getIntField();

            amountTillPayOuts = DomainGateway.getBaseCurrencyInstance(builder.getStringObject());
            countTillPayOuts = builder.getIntField();

            amountGiftCertificateSales = DomainGateway.getBaseCurrencyInstance(builder.getStringObject());
            unitsGiftCertificateSales = builder.getIntField();

            amountTransactionDiscounts = DomainGateway.getBaseCurrencyInstance(builder.getStringObject());
            numberTransactionDiscounts = builder.getIntField();

            amountItemDiscounts = DomainGateway.getBaseCurrencyInstance(builder.getStringObject());
            numberItemDiscounts = builder.getIntField();

            // restocking fees
            amountRestockingFees = DomainGateway.getBaseCurrencyInstance(builder.getStringObject());
            unitsRestockingFees = new BigDecimal(builder.getStringObject());

            // shipping charges
            amountShippingCharges = DomainGateway.getBaseCurrencyInstance(builder.getStringObject());
            numberShippingCharges = builder.getIntField();

            // shipping charegs tax
            amountTaxShippingCharges = DomainGateway.getBaseCurrencyInstance(builder.getStringObject());
            amountInclusiveTaxShippingCharges = DomainGateway.getBaseCurrencyInstance(builder.getStringObject());

            // StoreCouponDiscount
            amountTransactionDiscStoreCoupons = DomainGateway.getBaseCurrencyInstance(builder.getStringObject());
            numberTransactionDiscStoreCoupons = builder.getIntField();

            amountItemDiscStoreCoupons = DomainGateway.getBaseCurrencyInstance(builder.getStringObject());
            numberItemDiscStoreCoupons = builder.getIntField();

            amountPostVoids = DomainGateway.getBaseCurrencyInstance(builder.getStringObject());
            numberPostVoids = builder.getIntField();

            numberNoSales = builder.getIntField();
            amountLineVoids = DomainGateway.getBaseCurrencyInstance(builder.getStringObject());
            unitsLineVoids = new BigDecimal(builder.getStringObject());
            amountCancelledTransactions = DomainGateway.getBaseCurrencyInstance(builder.getStringObject());
            numberCancelledTransactions = builder.getIntField();

            amountGrossTaxableNonMerchandiseSales = DomainGateway.getBaseCurrencyInstance(builder.getStringObject());
            unitsGrossTaxableNonMerchandiseSales = new BigDecimal(builder.getStringObject());

            amountGrossNonTaxableNonMerchandiseSales = DomainGateway.getBaseCurrencyInstance(builder.getStringObject());
            unitsGrossNonTaxableNonMerchandiseSales = new BigDecimal(builder.getStringObject());

            amountGrossTaxableNonMerchandiseReturns = DomainGateway.getBaseCurrencyInstance(builder.getStringObject());
            unitsGrossTaxableNonMerchandiseReturns = new BigDecimal(builder.getStringObject());

            amountGrossNonTaxableNonMerchandiseReturns = DomainGateway.getBaseCurrencyInstance(builder
                    .getStringObject());
            unitsGrossNonTaxableNonMerchandiseReturns = new BigDecimal(builder.getStringObject());

            amountGrossTaxableNonMerchandiseSalesVoided = DomainGateway.getBaseCurrencyInstance(builder
                    .getStringObject());
            unitsGrossTaxableNonMerchandiseSalesVoided = new BigDecimal(builder.getStringObject());

            amountGrossNonTaxableNonMerchandiseSalesVoided = DomainGateway.getBaseCurrencyInstance(builder
                    .getStringObject());
            unitsGrossNonTaxableNonMerchandiseSalesVoided = new BigDecimal(builder.getStringObject());

            amountGrossTaxableNonMerchandiseReturnsVoided = DomainGateway.getBaseCurrencyInstance(builder
                    .getStringObject());
            unitsGrossTaxableNonMerchandiseReturnsVoided = new BigDecimal(builder.getStringObject());

            amountGrossNonTaxableNonMerchandiseReturnsVoided = DomainGateway.getBaseCurrencyInstance(builder
                    .getStringObject());
            unitsGrossNonTaxableNonMerchandiseReturnsVoided = new BigDecimal(builder.getStringObject());

            amountGrossGiftCardItemSales = DomainGateway.getBaseCurrencyInstance(builder.getStringObject());
            unitsGrossGiftCardItemSales = new BigDecimal(builder.getStringObject());

            amountGrossGiftCardItemReturns = DomainGateway.getBaseCurrencyInstance(builder.getStringObject());
            unitsGrossGiftCardItemReturns = new BigDecimal(builder.getStringObject());

            amountGrossGiftCardItemSalesVoided = DomainGateway.getBaseCurrencyInstance(builder.getStringObject());
            unitsGrossGiftCardItemSalesVoided = new BigDecimal(builder.getStringObject());

            amountGrossGiftCardItemReturnsVoided = DomainGateway.getBaseCurrencyInstance(builder.getStringObject());
            unitsGrossGiftCardItemReturnsVoided = new BigDecimal(builder.getStringObject());

            amountLayawayPayments = DomainGateway.getBaseCurrencyInstance(builder.getStringObject());
            countLayawayPayments = builder.getIntField();

            amountLayawayNew = DomainGateway.getBaseCurrencyInstance(builder.getStringObject());
            countLayawayNew = builder.getIntField();

            amountLayawayPickup = DomainGateway.getBaseCurrencyInstance(builder.getStringObject());
            countLayawayPickup = builder.getIntField();

            amountLayawayDeletions = DomainGateway.getBaseCurrencyInstance(builder.getStringObject());
            countLayawayDeletions = builder.getIntField();

            amountLayawayInitiationFees = DomainGateway.getBaseCurrencyInstance(builder.getStringObject());
            countLayawayInitiationFees = builder.getIntField();

            amountLayawayDeletionFees = DomainGateway.getBaseCurrencyInstance(builder.getStringObject());
            countLayawayDeletionFees = builder.getIntField();

            amountOrderPayments = DomainGateway.getBaseCurrencyInstance(builder.getStringObject());
            countOrderPayments = builder.getIntField();

            amountSpecialOrderNew = DomainGateway.getBaseCurrencyInstance(builder.getStringObject());

            amountSpecialOrderPartial = DomainGateway.getBaseCurrencyInstance(builder.getStringObject());

            amountOrderCancels = DomainGateway.getBaseCurrencyInstance(builder.getStringObject());
            countOrderCancels = builder.getIntField();

            // Get the Count Objects
            startingFloatCount = (ReconcilableCountIfc)builder.getFieldAsClass();
            if (startingFloatCount != null)
            {
                startingFloatCount.setHardTotalsData(builder);
            }

            endingFloatCount = (ReconcilableCountIfc)builder.getFieldAsClass();
            if (endingFloatCount != null)
            {
                endingFloatCount.setHardTotalsData(builder);
            }

            floatCount = (ReconcilableCountIfc)builder.getFieldAsClass();
            if (floatCount != null)
            {
                floatCount.setHardTotalsData(builder);
            }

            tenderCount = (FinancialCountIfc)builder.getFieldAsClass();
            if (tenderCount != null)
            {
                tenderCount.setHardTotalsData(builder);
            }

            combinedCount = (ReconcilableCountIfc)builder.getFieldAsClass();
            if (combinedCount != null)
            {
                combinedCount.setHardTotalsData(builder);
            }

            // Build the till pickup count table
            int tableSize = builder.getIntField();
            String countryCode = null;

            for (int i = 0; i < tableSize; i++)
            {
                countryCode = builder.getStringObject();
                countTillPickups.put(countryCode, Integer.valueOf(builder.getIntField()));
            }

            // Get the till pickup objects
            int number = builder.getIntField();
            for (int i = 0; i < number; i++)
            {
                ReconcilableCountIfc count = (ReconcilableCountIfc)builder.getFieldAsClass();
                if (count != null)
                {
                    count.setHardTotalsData(builder);
                }
                addTillPickups(count);
            }

            // Get the till pickup objects
            countTillLoans = builder.getIntField();
            // Get the till loan objects
            number = builder.getIntField();
            for (int i = 0; i < number; i++)
            {
                ReconcilableCountIfc count = (ReconcilableCountIfc)builder.getFieldAsClass();
                if (count != null)
                {
                    count.setHardTotalsData(builder);
                }
                addTillLoans(count);
            }

            // Get the till payin objects
            number = builder.getIntField();
            for (int i = 0; i < number; i++)
            {
                ReconcilableCountIfc count = (ReconcilableCountIfc)builder.getFieldAsClass();
                if (count != null)
                {
                    count.setHardTotalsData(builder);
                }
                addTillPayIns(count);
            }

            // Get the till payout objects
            number = builder.getIntField();
            for (int i = 0; i < number; i++)
            {
                ReconcilableCountIfc count = (ReconcilableCountIfc)builder.getFieldAsClass();
                if (count != null)
                {
                    count.setHardTotalsData(builder);
                }
                addTillPayOuts(count);
            }

            amountEmployeeDiscounts = DomainGateway.getBaseCurrencyInstance(builder.getStringObject());
            unitsEmployeeDiscounts = new BigDecimal(builder.getStringObject());
            amountCustomerDiscounts = DomainGateway.getBaseCurrencyInstance(builder.getStringObject());
            unitsCustomerDiscounts = new BigDecimal(builder.getStringObject());
            amountPriceOverrides = DomainGateway.getBaseCurrencyInstance(builder.getStringObject());
            unitsPriceOverrides = new BigDecimal(builder.getStringObject());
            unitsPriceAdjustments = new BigDecimal(builder.getStringObject());

            // gift certificate
            amountGrossGiftCertificateIssued = DomainGateway.getBaseCurrencyInstance(builder.getStringObject());
            unitsGrossGiftCertificateIssued = new BigDecimal(builder.getStringObject());

            amountGrossGiftCertificateIssuedVoided = DomainGateway.getBaseCurrencyInstance(builder.getStringObject());
            unitsGrossGiftCertificateIssuedVoided = new BigDecimal(builder.getStringObject());

            amountGrossGiftCertificateTendered = DomainGateway.getBaseCurrencyInstance(builder.getStringObject());
            unitsGrossGiftCertificateTendered = new BigDecimal(builder.getStringObject());

            amountGrossGiftCertificateTenderedVoided = DomainGateway.getBaseCurrencyInstance(builder.getStringObject());
            unitsGrossGiftCertificateTenderedVoided = new BigDecimal(builder.getStringObject());

            // gift card
            amountGrossGiftCardItemIssued = DomainGateway.getBaseCurrencyInstance(builder.getStringObject());
            unitsGrossGiftCardItemIssued = new BigDecimal(builder.getStringObject());

            amountGrossGiftCardItemReloaded = DomainGateway.getBaseCurrencyInstance(builder.getStringObject());
            unitsGrossGiftCardItemReloaded = new BigDecimal(builder.getStringObject());

            amountGrossGiftCardItemRedeemed = DomainGateway.getBaseCurrencyInstance(builder.getStringObject());
            unitsGrossGiftCardItemRedeemed = new BigDecimal(builder.getStringObject());

            amountGrossGiftCardItemIssueVoided = DomainGateway.getBaseCurrencyInstance(builder.getStringObject());
            unitsGrossGiftCardItemIssueVoided = new BigDecimal(builder.getStringObject());

            amountGrossGiftCardItemReloadVoided = DomainGateway.getBaseCurrencyInstance(builder.getStringObject());
            unitsGrossGiftCardItemReloadVoided = new BigDecimal(builder.getStringObject());

            amountGrossGiftCardItemRedeemedVoided = DomainGateway.getBaseCurrencyInstance(builder.getStringObject());
            unitsGrossGiftCardItemRedeemedVoided = new BigDecimal(builder.getStringObject());

            houseCardEnrollmentsApproved = builder.getIntField();
            houseCardEnrollmentsDeclined = builder.getIntField();

            amountGrossGiftCardItemCredit = DomainGateway.getBaseCurrencyInstance(builder.getStringObject());
            unitsGrossGiftCardItemCredit = new BigDecimal(builder.getStringObject());

            amountGrossGiftCardItemCreditVoided = DomainGateway.getBaseCurrencyInstance(builder.getStringObject());
            unitsGrossGiftCardItemCreditVoided = new BigDecimal(builder.getStringObject());

            amountGrossGiftCertificatesRedeemed = DomainGateway.getBaseCurrencyInstance(builder.getStringObject());
            unitsGrossGiftCertificatesRedeemed = new BigDecimal(builder.getStringObject());

            amountGrossGiftCertificatesRedeemedVoided = DomainGateway
                    .getBaseCurrencyInstance(builder.getStringObject());
            unitsGrossGiftCertificatesRedeemedVoided = new BigDecimal(builder.getStringObject());

            amountGrossStoreCreditsIssued = DomainGateway.getBaseCurrencyInstance(builder.getStringObject());
            unitsGrossStoreCreditsIssued = new BigDecimal(builder.getStringObject());

            amountGrossStoreCreditsIssuedVoided = DomainGateway.getBaseCurrencyInstance(builder.getStringObject());
            unitsGrossStoreCreditsIssuedVoided = new BigDecimal(builder.getStringObject());

            amountGrossStoreCreditsRedeemed = DomainGateway.getBaseCurrencyInstance(builder.getStringObject());
            unitsGrossStoreCreditsRedeemed = new BigDecimal(builder.getStringObject());

            amountGrossStoreCreditsRedeemedVoided = DomainGateway.getBaseCurrencyInstance(builder.getStringObject());
            unitsGrossStoreCreditsRedeemedVoided = new BigDecimal(builder.getStringObject());

            amountGrossItemEmployeeDiscount = DomainGateway.getBaseCurrencyInstance(builder.getStringObject());
            unitsGrossItemEmployeeDiscount = new BigDecimal(builder.getStringObject());

            amountGrossItemEmployeeDiscountVoided = DomainGateway.getBaseCurrencyInstance(builder.getStringObject());
            unitsGrossItemEmployeeDiscountVoided = new BigDecimal(builder.getStringObject());

            amountGrossTransactionEmployeeDiscount = DomainGateway.getBaseCurrencyInstance(builder.getStringObject());
            unitsGrossTransactionEmployeeDiscount = new BigDecimal(builder.getStringObject());

            amountGrossTransactionEmployeeDiscountVoided = DomainGateway.getBaseCurrencyInstance(builder
                    .getStringObject());
            unitsGrossTransactionEmployeeDiscountVoided = new BigDecimal(builder.getStringObject());
            amountBillPayments = DomainGateway.getBaseCurrencyInstance(builder.getStringObject());
            countBillPayments = builder.getIntField();

            amountChangeRoundedIn  = DomainGateway.getBaseCurrencyInstance(builder.getStringObject());
            amountChangeRoundedOut = DomainGateway.getBaseCurrencyInstance(builder.getStringObject());
        }

    }

    /**
     * Retrieves expected combined tender count for this entity that doesn't
     * contain the starting and ending float. Used by Back office only and is
     * just a hack.
     *
     * @return expected combined tender count for this entity
     */
    public ReconcilableCountIfc getCombinedCountForTenderReconcile()
    {
        buildCombinedExpectedCountForTenderReconcile();
        return combinedCount;
    }


    /**
     * Build combined count from other counts.
     */
    protected void buildCombinedExpectedCount()
    {
        buildCombinedExpectedCount(true);
    }

    /**
     * Build a combined count that excludes the starting and ending float. Used
     * by summary report to get the expected amount for all counted tenders.
     */
    protected void buildCombinedExpectedCountForTenderReconcile()
    {
        buildCombinedExpectedCount(false);
    }

    /**
     * Build combined count from other counts.
     */
    private void buildCombinedExpectedCount(boolean includeFloat)
    {
        combinedCount.getExpected().resetTotals();
        if (includeFloat)
        {
            if (startingFloatCount != null)
            {
                combinedCount.addExpected(startingFloatCount.getEntered());
            }
            if (endingFloatCount != null)
            {
                combinedCount.addExpected(endingFloatCount.getEntered());
            }
        }
        if (tenderCount != null)
        {
            combinedCount.getExpected().add(tenderCount);
        }
        ReconcilableCountIfc[] tillLoans = getTillLoans();
        if (tillLoans != null)
        {
            for (int i = 0; i < tillLoans.length; i++)
            {
                combinedCount.addExpected(tillLoans[i].getEntered());
            }
        }
        ReconcilableCountIfc[] tillPickups = getTillPickups();
        if (tillPickups != null)
        {
            for (int i = 0; i < tillPickups.length; i++)
            {
                combinedCount.addExpected(tillPickups[i].getEntered());
            }
        }
        ReconcilableCountIfc[] tillPayins = getTillPayIns();
        if (tillPayins != null)
        {
            for (int i = 0; i < tillPayins.length; i++)
            {
                combinedCount.addExpected(tillPayins[i].getEntered());
            }
        }
        ReconcilableCountIfc[] tillPayouts = getTillPayOuts();
        if (tillPayouts != null)
        {
            for (int i = 0; i < tillPayouts.length; i++)
            {
                combinedCount.addExpected(tillPayouts[i].getEntered());
            }
        }
    }

    /**
     * Returns default display string.
     *
     * @return String representation of object
     */
    public String toString()
    {
        // build result string
        StringBuilder strResult = Util.classToStringHeader("FinancialTotals", getRevisionNumber(), hashCode());
        strResult.append(Util.EOL).append("GRAND TOTALS:").append(Util.EOL).append(
                "transactionCount:                       [").append(transactionCount).append("]").append(Util.EOL)
                .append("transactionsWithReturnedItemsCount:     [").append(transactionsWithReturnedItemsCount).append(
                        "]").append(Util.EOL).append("amountGrossTaxableItemSales:            [").append(
                        getAmountGrossTaxableItemSales().toString()).append("]").append(Util.EOL).append(
                        "unitsGrossTaxableItemSales:             [").append(getUnitsGrossTaxableItemSales())
                .append("]").append(Util.EOL).append("amountGrossNonTaxableItemSales:         [").append(
                        getAmountGrossNonTaxableItemSales().toString()).append("]").append(Util.EOL).append(
                        "unitsGrossNonTaxableItemSales:          [").append(getUnitsGrossNonTaxableItemSales()).append(
                        "]").append(Util.EOL).append("amountGrossTaxExemptItemSales:          [").append(
                        getAmountGrossTaxExemptItemSales().toString()).append("]").append(Util.EOL).append(
                        "unitsGrossTaxExemptItemSales:           [").append(getUnitsGrossTaxExemptItemSales()).append(
                        "]").append(Util.EOL).append("amountGrossTaxableItemReturns:          [").append(
                        getAmountGrossTaxableItemReturns().toString()).append("]").append(Util.EOL).append(
                        "unitsGrossTaxableItemReturns:           [").append(getUnitsGrossTaxableItemReturns()).append(
                        "]").append(Util.EOL).append("amountGrossNonTaxableItemReturns:       [").append(
                        getAmountGrossNonTaxableItemReturns().toString()).append("]").append(Util.EOL).append(
                        "unitsGrossNonTaxableItemReturns:        [").append(getUnitsGrossNonTaxableItemReturns())
                .append("]").append(Util.EOL).append("amountGrossTaxExemptItemReturns:        [").append(
                        getAmountGrossTaxExemptItemReturns().toString()).append("]").append(Util.EOL).append(
                        "unitsGrossTaxExemptItemReturns:         [").append(getUnitsGrossTaxExemptItemReturns())
                .append("]").append(Util.EOL).append("amountGrossTaxableItemSalesVoided:      [").append(
                        getAmountGrossTaxableItemSalesVoided().toString()).append("]").append(Util.EOL).append(
                        "unitsGrossTaxableItemSalesVoided:       [").append(getUnitsGrossTaxableItemSalesVoided())
                .append("]").append(Util.EOL).append("amountGrossNonTaxableItemSalesVoided:   [").append(
                        getAmountGrossNonTaxableItemSalesVoided().toString()).append("]").append(Util.EOL).append(
                        "unitsGrossNonTaxableItemSalesVoided:    [").append(getUnitsGrossNonTaxableItemSalesVoided())
                .append("]").append(Util.EOL).append("amountGrossTaxExemptItemSalesVoided:    [").append(
                        getAmountGrossTaxExemptItemSalesVoided().toString()).append("]").append(Util.EOL).append(
                        "unitsGrossTaxExemptItemSalesVoided:     [").append(getUnitsGrossTaxExemptItemSalesVoided())
                .append("]").append(Util.EOL).append("amountGrossTaxableItemReturnsVoided:    [").append(
                        getAmountGrossTaxableItemReturnsVoided().toString()).append("]").append(Util.EOL).append(
                        "unitsGrossTaxableItemReturnsVoided:     [").append(getUnitsGrossTaxableItemReturnsVoided())
                .append("]").append(Util.EOL).append("amountGrossNonTaxableItemReturnsVoided: [").append(
                        getAmountGrossNonTaxableItemReturnsVoided().toString()).append("]").append(Util.EOL).append(
                        "unitsGrossNonTaxableItemReturnsVoided:  [").append(getUnitsGrossNonTaxableItemReturnsVoided())
                .append("]").append(Util.EOL).append("amountGrossTaxExemptItemReturnsVoided:  [").append(
                        getAmountGrossTaxExemptItemReturnsVoided().toString()).append("]").append(Util.EOL).append(
                        "unitsGrossTaxExemptItemReturnsVoided:   [").append(getUnitsGrossTaxExemptItemReturnsVoided())
                .append("]").append(Util.EOL).append("amountTaxItemSales:                     [").append(
                        getAmountTaxItemSales().toString()).append("]").append(Util.EOL).append(
                        "amountInclusiveTaxItemSales:            [")
                .append(getAmountInclusiveTaxItemSales().toString()).append("]").append(Util.EOL).append(
                        "amountTaxItemReturns:                   [").append(getAmountTaxItemReturns().toString())
                .append("]").append(Util.EOL).append("amountInclusiveTaxItemReturns:          [").append(
                        getAmountInclusiveTaxItemReturns().toString()).append("]").append(Util.EOL).append(
                        "amountTaxTransactionSales:              [").append(getAmountTaxTransactionSales().toString())
                .append("]").append(Util.EOL).append("amountInclusiveTaxTransactionSales:     [").append(
                        getAmountInclusiveTaxTransactionSales().toString()).append("]").append(Util.EOL).append(
                        "amountTaxTransactionReturns:            [")
                .append(getAmountTaxTransactionReturns().toString()).append("]").append(Util.EOL).append(
                        "amountInclusiveTaxTransactionReturns:   [").append(
                        getAmountInclusiveTaxTransactionReturns().toString()).append("]").append(Util.EOL).append(
                        "amountGrossTaxableTransactionSales:     [").append(
                        getAmountGrossTaxableTransactionSales().toString()).append("]").append(Util.EOL).append(
                        "countGrossTaxableTransactionSales:      [").append(countGrossTaxableTransactionSales).append(
                        "]").append(Util.EOL).append("amountGrossNonTaxableTransactionSales:  [").append(
                        getAmountGrossNonTaxableTransactionSales().toString()).append("]").append(Util.EOL).append(
                        "countGrossNonTaxableTransactionSales:   [").append(countGrossNonTaxableTransactionSales)
                .append("]").append(Util.EOL).append("amountGrossTaxExemptTransactionSales:   [").append(
                        getAmountGrossTaxExemptTransactionSales().toString()).append("]").append(Util.EOL).append(
                        "countGrossTaxExemptTransactionSales:    [").append(countGrossTaxExemptTransactionSales)
                .append("]").append(Util.EOL).append("amountGrossTaxableTransactionReturns:   [").append(
                        getAmountGrossTaxableTransactionReturns().toString()).append("]").append(Util.EOL).append(
                        "countGrossTaxableTransactionReturns:    [").append(countGrossTaxableTransactionReturns)
                .append("]").append(Util.EOL).append("amountGrossNonTaxableTransactionReturns:[").append(
                        getAmountGrossNonTaxableTransactionReturns().toString()).append("]").append(Util.EOL).append(
                        "countGrossNonTaxableTransactionReturns: [").append(countGrossNonTaxableTransactionReturns)
                .append("]").append(Util.EOL).append("amountGrossTaxExemptTransactionReturns: [").append(
                        getAmountGrossTaxExemptTransactionReturns().toString()).append("]").append(Util.EOL).append(
                        "countGrossTaxExemptTransactionReturns:  [").append(countGrossTaxExemptTransactionReturns)
                .append("]").append(Util.EOL).append("amountGrossTaxableTransactionSalesVoided:[").append(
                        getAmountGrossTaxableTransactionSalesVoided().toString()).append("]").append(Util.EOL).append(
                        "countGrossTaxableTransactionSalesVoided:[").append(countGrossTaxableTransactionSalesVoided)
                .append("]").append(Util.EOL).append("amountGrossNonTaxableTransactionSalesVoided:[").append(
                        getAmountGrossNonTaxableTransactionSalesVoided().toString()).append("]").append(Util.EOL)
                .append("countGrossNonTaxableTransactionSalesVoided:[").append(
                        countGrossNonTaxableTransactionSalesVoided).append("]").append(Util.EOL).append(
                        "amountGrossTaxExemptTransactionSalesVoided:[").append(
                        getAmountGrossTaxExemptTransactionSalesVoided().toString()).append("]").append(Util.EOL)
                .append("countGrossTaxExemptTransactionSalesVoided:[")
                .append(countGrossTaxExemptTransactionSalesVoided).append("]").append(Util.EOL).append(
                        "amountGrossTaxableTransactionReturnsVoided:[").append(
                        getAmountGrossTaxableTransactionReturnsVoided().toString()).append("]").append(Util.EOL)
                .append("countGrossTaxableTransactionReturnsVoided:[")
                .append(countGrossTaxableTransactionReturnsVoided).append("]").append(Util.EOL).append(
                        "amountGrossNonTaxableTransactionReturnsVoided:[").append(
                        getAmountGrossNonTaxableTransactionReturnsVoided().toString()).append("]").append(Util.EOL)
                .append("countGrossNonTaxableTransactionReturnsVoided:[").append(
                        countGrossNonTaxableTransactionReturnsVoided).append("]").append(Util.EOL).append(
                        "amountGrossTaxExemptTransactionReturnsVoided:[").append(
                        getAmountGrossTaxExemptTransactionReturnsVoided().toString()).append("]").append(Util.EOL)
                .append("countGrossTaxExemptTransactionReturnsVoided:[").append(
                        countGrossTaxExemptTransactionReturnsVoided).append("]").append(Util.EOL).append(
                        "amountLayawayNew:[").append(getAmountLayawayNew()).append("]").append(Util.EOL).append(
                        "countLayawayNew:[").append(getCountLayawayNew()).append("]").append(Util.EOL).append(
                        "countLayawayPickup:[").append(getCountLayawayPickup()).append("]").append(Util.EOL).append(
                        "amountLayawayPickup:[").append(getAmountLayawayPickup()).append("]").append(Util.EOL).append(
                        "amountLayawayPayments:[").append(getAmountLayawayPayments()).append("]").append(Util.EOL)
                .append("countLayawayPayments:[").append(countLayawayPayments).append("]").append(Util.EOL).append(
                        "amountLayawayDeletions:[").append(getAmountLayawayDeletions()).append("]").append(Util.EOL)
                .append("countLayawayDeletions:[").append(countLayawayDeletions).append("]").append(Util.EOL).append(
                        "amountLayawayInitiationFees:[").append(getAmountLayawayInitiationFees()).append("]").append(
                        Util.EOL).append("countLayawayInitiationFees:[").append(countLayawayInitiationFees).append("]")
                .append(Util.EOL).append("amountLayawayDeletionFees:[").append(getAmountLayawayDeletionFees()).append(
                        "]").append(Util.EOL).append("countLayawayDeletionFees:[").append(countLayawayDeletionFees)
                .append("]").append(Util.EOL).append("amountSpecialOrderNew:[").append(getAmountSpecialOrderNew())
                .append("]").append(Util.EOL).append("amountSpecialOrderPartial:[").append(
                        getAmountSpecialOrderPartial()).append("]").append(Util.EOL).append("amountOrderPayments:[")
                .append(getAmountOrderPayments()).append("]").append(Util.EOL).append("countOrderPayments:[").append(
                        countOrderPayments).append("]").append(Util.EOL).append("amountOrderCancels:[").append(
                        getAmountOrderCancels()).append("]").append(Util.EOL).append("countOrderCancels:[").append(
                        countOrderCancels).append("]").append(Util.EOL).append("amountHousePayments:[").append(
                        getAmountHousePayments()).append("]").append(Util.EOL).append("countHousePayments:[").append(
                        countHousePayments).append("]").append("amountBillPayments:[").append(getAmountBillPayments()).append("]").
                        append(Util.EOL).append("countBillPayments:[").append(countBillPayments).append("]").append(Util.EOL).
                        append("amountTillPayIns:[").append(getAmountTillPayIns()).append("]").append(Util.EOL).append("countTillPayIns:[").
                        append(countTillPayIns).append("]").append(Util.EOL).append("amountTillPayOuts:[").append(
                        getAmountTillPayOuts()).append("]").append(Util.EOL).append("countTillPayOuts:[").append(
                        countTillPayOuts).append("]").append(Util.EOL).append(
                        "amountTransactionDiscounts:             [").append(getAmountTransactionDiscounts().toString())
                .append("]").append(Util.EOL).append("numberTransactionDiscounts:             [").append(
                        numberTransactionDiscounts).append("]").append(Util.EOL).append(
                        "amountItemDiscounts:                    [").append(getAmountItemDiscounts().toString())
                .append("]").append(Util.EOL).append("numberItemDiscounts:                    [").append(
                        numberItemDiscounts).append("]").append(Util.EOL).append(
                        "amountItemMarkdowns:                    [")
                .append("amountChangeRoundedIn:[").append(getAmountChangeRoundedIn()).append("]").append(Util.EOL)
                .append("amountChangeRoundedOut:[").append(getAmountChangeRoundedOut()).append("]").append(Util.EOL)
                .append(getAmountItemMarkdowns().toString())
                .append("]")
                .append(Util.EOL)
                .append("numberItemMarkdowns:                    [")
                .append(numberItemMarkdowns)
                .append("]")
                .append(Util.EOL)
                // restocking fees
                .append("amountRestockingFees:                    [").append(getAmountRestockingFees().toString())
                .append("]")
                .append(Util.EOL)
                .append("unitsRestockingFees:                    [")
                .append(getUnitsRestockingFees())
                .append("]")
                .append(Util.EOL)
                // shipping charges
                .append("amountShippingCharges:                  [").append(getAmountShippingCharges().toString())
                .append("]").append(Util.EOL)
                .append("numberShippingCharges:                  [")
                .append(numberShippingCharges)
                .append("]")
                .append(Util.EOL)
                // shipping charges tax
                .append("amountTaxShippingCharges:                  [")
                .append(getAmountTaxShippingCharges().toString()).append("]").append(Util.EOL).append(
                        "amountInclusiveTaxShippingCharges:                  [")
                .append(getAmountInclusiveTaxShippingCharges().toString())
                .append("]")
                .append(Util.EOL)
                // StoreCouponDiscounts
                .append("amountTransactionDiscStoreCoupons:                    [").append(
                        getAmountTransactionDiscStoreCoupons().toString()).append("]").append(Util.EOL).append(
                        "numberTransactionDiscStoreCoupons:                    [").append(
                        numberTransactionDiscStoreCoupons).append("]").append(Util.EOL).append(
                        "amountItemDiscStoreCoupons:                    [").append(
                        getAmountItemDiscStoreCoupons().toString()).append("]").append(Util.EOL).append(
                        "numberItemDiscStoreCoupons:                    [").append(numberItemDiscStoreCoupons).append(
                        "]").append(Util.EOL).append("amountPostVoids:                        [").append(
                        getAmountPostVoids().toString()).append("]").append(Util.EOL).append(
                        "numberPostVoids:                        [").append(numberPostVoids).append("]").append(
                        Util.EOL).append("numberNoSales:                          [").append(numberNoSales).append("]")
                .append(Util.EOL).append("amountLineVoids:                        [").append(
                        getAmountLineVoids().toString()).append("]").append(Util.EOL).append(
                        "unitsLineVoids:                         [").append(getUnitsLineVoids()).append("]").append(
                        Util.EOL).append("amountCancelledTransactions:            [").append(
                        getAmountCancelledTransactions().toString()).append("]").append(Util.EOL).append(
                        "numberCancelledTransactions:            [").append(numberCancelledTransactions).append("]")
                .append(Util.EOL).append(Util.EOL).append("COMBINED COUNT:").append(Util.EOL).append(
                        getCombinedCount().toString()).append(Util.EOL).append(Util.EOL)
                .append("STARTING FLOAT COUNT:").append(Util.EOL).append(startingFloatCount.toString())
                .append(Util.EOL).append(Util.EOL).append("ENDING FLOAT COUNT:").append(Util.EOL).append(
                        endingFloatCount.toString()).append(Util.EOL).append(Util.EOL).append("TENDER COUNT:").append(
                        Util.EOL).append(tenderCount.toString()).append(Util.EOL);

        if (amountGrossTaxableNonMerchandiseSales == null)
        {
            strResult.append("amountGrossTaxableNonMerchandiseSales: [null]\n");
        }
        else
        {
            strResult.append("amountGrossTaxableNonMerchandiseSales: [").append(amountGrossTaxableNonMerchandiseSales)
                    .append("]\n");
        }
        if (unitsGrossTaxableNonMerchandiseSales == null)
        {
            strResult.append("unitsGrossTaxableNonMerchandiseSales: [null]\n");
        }
        else
        {
            strResult.append("unitsGrossTaxableNonMerchandiseSales: [").append(unitsGrossTaxableNonMerchandiseSales)
                    .append("]\n");
        }
        if (amountGrossNonTaxableNonMerchandiseSales == null)
        {
            strResult.append("amountGrossNonTaxableNonMerchandiseSales: [null]\n");
        }
        else
        {
            strResult.append("amountGrossNonTaxableNonMerchandiseSales: [").append(
                    amountGrossNonTaxableNonMerchandiseSales).append("]\n");
        }
        if (unitsGrossNonTaxableNonMerchandiseSales == null)
        {
            strResult.append("unitsGrossNonTaxableNonMerchandiseSales: [null]\n");
        }
        else
        {
            strResult.append("unitsGrossNonTaxableNonMerchandiseSales: [").append(
                    unitsGrossNonTaxableNonMerchandiseSales).append("]\n");
        }
        if (amountGrossTaxableNonMerchandiseReturns == null)
        {
            strResult.append("amountGrossTaxableNonMerchandiseReturns: [null]\n");
        }
        else
        {
            strResult.append("amountGrossTaxableNonMerchandiseReturns: [").append(
                    amountGrossTaxableNonMerchandiseReturns).append("]\n");
        }
        if (unitsGrossTaxableNonMerchandiseReturns == null)
        {
            strResult.append("unitsGrossTaxableNonMerchandiseReturns: [null]\n");
        }
        else
        {
            strResult.append("unitsGrossTaxableNonMerchandiseReturns: [")
                    .append(unitsGrossTaxableNonMerchandiseReturns).append("]\n");
        }
        if (amountGrossNonTaxableNonMerchandiseReturns == null)
        {
            strResult.append("amountGrossNonTaxableNonMerchandiseReturns: [null]\n");
        }
        else
        {
            strResult.append("amountGrossNonTaxableNonMerchandiseReturns: [").append(
                    amountGrossNonTaxableNonMerchandiseReturns).append("]\n");
        }
        if (unitsGrossNonTaxableNonMerchandiseReturns == null)
        {
            strResult.append("unitsGrossNonTaxableNonMerchandiseReturns: [null]\n");
        }
        else
        {
            strResult.append("unitsGrossNonTaxableNonMerchandiseReturns: [").append(
                    unitsGrossNonTaxableNonMerchandiseReturns).append("]\n");
        }
        strResult.append("amountGrossTaxableNonMerchandiseSalesVoided:  [").append(
                getAmountGrossTaxableNonMerchandiseSalesVoided().toString()).append("]").append(Util.EOL).append(
                "unitsGrossTaxableNonMerchandiseSalesVoided:[").append(getUnitsGrossTaxableNonMerchandiseSalesVoided())
                .append("]").append(Util.EOL).append("amountGrossNonTaxableNonMerchandiseSalesVoided:[").append(
                        getAmountGrossNonTaxableNonMerchandiseSalesVoided().toString()).append("]").append(Util.EOL)
                .append("unitsGrossNonTaxableNonMerchandiseSalesVoided:[").append(
                        getUnitsGrossNonTaxableNonMerchandiseSalesVoided()).append("]").append(Util.EOL).append(
                        "amountGrossTaxableNonMerchandiseReturnsVoided:[").append(
                        getAmountGrossTaxableNonMerchandiseReturnsVoided().toString()).append("]").append(Util.EOL)
                .append("unitsGrossTaxableNonMerchandiseReturnsVoided:[").append(
                        getUnitsGrossTaxableNonMerchandiseReturnsVoided()).append("]").append(Util.EOL).append(
                        "amountGrossNonTaxableNonMerchandiseReturnsVoided:[").append(
                        getAmountGrossNonTaxableNonMerchandiseReturnsVoided().toString()).append("]").append(Util.EOL)
                .append("unitsGrossNonTaxableNonMerchandiseReturnsVoided:[").append(
                        getUnitsGrossNonTaxableNonMerchandiseReturnsVoided()).append("]").append(Util.EOL);

        if (amountGrossGiftCardItemIssued == null)
        {
            strResult.append("amountGrossGiftCardItemIssued:       [null]\n");
        }
        else
        {
            strResult.append("amountGrossGiftCardItemIssued:       [").append(amountGrossGiftCardItemIssued).append(
                    "]\n");
        }
        if (amountGrossGiftCardItemIssueVoided == null)
        {
            strResult.append("amountGrossGiftCardItemIssueVoided:       [null]\n");
        }
        else
        {
            strResult.append("amountGrossGiftCardItemIssueVoided:       [").append(amountGrossGiftCardItemIssueVoided)
                    .append("]\n");
        }
        if (unitsGrossGiftCardItemIssued == null)
        {
            strResult.append("unitsGrossGiftCardItemIssued:       [null]\n");
        }
        else
        {
            strResult.append("unitsGrossGiftCardItemIssued:       [").append(unitsGrossGiftCardItemIssued)
                    .append("]\n");
        }
        if (unitsGrossGiftCardItemIssueVoided == null)
        {
            strResult.append("unitsGrossGiftCardItemIssueVoided:       [null]\n");
        }
        else
        {
            strResult.append("unitsGrossGiftCardItemIssueVoided:       [").append(unitsGrossGiftCardItemIssueVoided)
                    .append("]\n");
        }
        if (amountGrossGiftCardItemReloaded == null)
        {
            strResult.append("amountGrossGiftCardItemReloaded:       [null]\n");
        }
        else
        {
            strResult.append("amountGrossGiftCardItemReloaded:       [").append(amountGrossGiftCardItemReloaded)
                    .append("]\n");
        }
        if (amountGrossGiftCardItemReloadVoided == null)
        {
            strResult.append("amountGrossGiftCardItemReloadVoided:       [null]\n");
        }
        else
        {
            strResult.append("amountGrossGiftCardItemReloadVoided:       [")
                    .append(amountGrossGiftCardItemReloadVoided).append("]\n");
        }
        if (unitsGrossGiftCardItemReloaded == null)
        {
            strResult.append("unitsGrossGiftCardItemReloaded:       [null]\n");
        }
        else
        {
            strResult.append("unitsGrossGiftCardItemReloaded:       [").append(unitsGrossGiftCardItemReloaded).append(
                    "]\n");
        }
        if (unitsGrossGiftCardItemReloadVoided == null)
        {
            strResult.append("unitsGrossGiftCardItemReloadVoided:       [null]\n");
        }
        else
        {
            strResult.append("unitsGrossGiftCardItemReloadVoided:       [").append(unitsGrossGiftCardItemReloadVoided)
                    .append("]\n");
        }

        if (amountGrossGiftCardItemSales == null)
        {
            strResult.append("amountGrossGiftCardItemSales:       [null]\n");
        }
        else
        {
            strResult.append("amountGrossGiftCardItemSales:       [").append(amountGrossGiftCardItemSales)
                    .append("]\n");
        }
        if (unitsGrossGiftCardItemSales == null)
        {
            strResult.append("unitsGrossGiftCardItemSales:       [null]\n");
        }
        else
        {
            strResult.append("unitsGrossGiftCardItemSales:       [").append(unitsGrossGiftCardItemSales).append("]\n");
        }
        if (amountGrossGiftCardItemReturns == null)
        {
            strResult.append("amountGrossGiftCardItemReturns:     [null]\n");
        }
        else
        {
            strResult.append("amountGrossGiftCardItemReturns:     [").append(amountGrossGiftCardItemReturns).append(
                    "]\n");
        }
        if (unitsGrossGiftCardItemReturns == null)
        {
            strResult.append("unitsGrossGiftCardItemReturns:     [null]\n");
        }
        else
        {
            strResult.append("unitsGrossGiftCardItemReturns:     [").append(unitsGrossGiftCardItemReturns)
                    .append("]\n");
        }

        strResult.append("amountGrossGiftCardItemSalesVoided:[").append(
                getAmountGrossGiftCardItemSalesVoided().toString()).append("]").append(Util.EOL).append(
                "unitsGrossGiftCardItemSalesVoided:      [").append(getUnitsGrossGiftCardItemSalesVoided()).append("]")
                .append(Util.EOL).append("amountGrossGiftCardItemReturnsVoided:   [").append(
                        getAmountGrossGiftCardItemReturnsVoided().toString()).append("]").append(Util.EOL).append(
                        "unitsGrossGiftCardItemReturnsVoided:    [").append(getUnitsGrossGiftCardItemReturnsVoided())
                .append("]").append(Util.EOL).append("countTillPickups:").append(Util.EOL);
        if (amountEmployeeDiscounts == null)
        {
            strResult.append("amountEmployeeDiscounts:       [null]\n");
        }
        else
        {
            strResult.append("amountEmployeeDiscounts:       [").append(amountEmployeeDiscounts).append("]\n");
        }
        if (unitsEmployeeDiscounts == null)
        {
            strResult.append("unitsEmployeeDiscounts:       [null]\n");
        }
        else
        {
            strResult.append("unitsEmployeeDiscounts:       [").append(unitsEmployeeDiscounts).append("]\n");
        }
        if (amountCustomerDiscounts == null)
        {
            strResult.append("amountCustomerDiscounts:       [null]\n");
        }
        else
        {
            strResult.append("amountCustomerDiscounts:       [").append(amountCustomerDiscounts).append("]\n");
        }
        if (unitsCustomerDiscounts == null)
        {
            strResult.append("unitsCustomerDiscounts:       [null]\n");
        }
        else
        {
            strResult.append("unitsCustomerDiscounts:       [").append(unitsCustomerDiscounts).append("]\n");
        }
        if (amountPriceOverrides == null)
        {
            strResult.append("amountPriceOverrides:       [null]\n");
        }
        else
        {
            strResult.append("amountPriceOverrides:       [").append(amountPriceOverrides).append("]\n");
        }
        if (unitsPriceOverrides == null)
        {
            strResult.append("unitsPriceOverrides:       [null]\n");
        }
        else
        {
            strResult.append("unitsPriceOverrides:       [").append(unitsPriceOverrides).append("]\n");
        }
        if (unitsPriceAdjustments == null)
        {
            strResult.append("unitsPriceAdjustments:       [null]\n");
        }
        else
        {
            strResult.append("unitsPriceAdjustments:       [").append(unitsPriceAdjustments).append("]\n");
        }

        Enumeration<String> enumer = countTillPickups.keys();
        String countryCode = null;

        while (enumer.hasMoreElements())
        {
            countryCode = enumer.nextElement();
            strResult.append("     ").append(countryCode).append("  ").append(
                    "" + countTillPickups.get(countryCode).intValue()).append(Util.EOL);
        }

        if (getTillPickups() == null)
        {
            strResult.append(Util.EOL).append("TILL PICKUP:                        [null]").append(Util.EOL);
        }
        else
        {
            ReconcilableCountIfc[] tillPickups = getTillPickups();
            if (tillPickups.length == 0)
            {
                strResult.append(Util.EOL).append("TILL PICKUP:                        [null]").append(Util.EOL);
            }
            for (int i = 0; i < tillPickups.length; i++)
            {
                strResult.append(Util.EOL).append("TILL PICKUP: ").append(i + 1).append(Util.EOL).append(
                        tillPickups[i].toString()).append(Util.EOL);
            }
        }

        strResult.append("countTillLoans:                         [").append(countTillLoans).append("]").append(
                Util.EOL);

        if (getTillLoans() == null)
        {
            strResult.append(Util.EOL).append("TILL LOAN:                          [null]").append(Util.EOL);
        }
        else
        {
            ReconcilableCountIfc[] tillLoans = getTillLoans();
            if (tillLoans.length == 0)
            {
                strResult.append(Util.EOL).append("TILL LOAN:                        [null]").append(Util.EOL);
            }
            for (int i = 0; i < tillLoans.length; i++)
            {
                strResult.append(Util.EOL).append("TILL LOAN: ").append(i + 1).append(Util.EOL).append(
                        tillLoans[i].toString()).append(Util.EOL);
            }
        }
        //
        if (getTillPayIns() == null)
        {
            strResult.append(Util.EOL).append("TILL PAYIN:                        [null]").append(Util.EOL);
        }
        else
        {
            ReconcilableCountIfc[] tillPayins = getTillPayIns();
            if (tillPayins.length == 0)
            {
                strResult.append(Util.EOL).append("TILL PAYINS:                        [null]").append(Util.EOL);
            }
            for (int i = 0; i < tillPayins.length; i++)
            {
                strResult.append(Util.EOL).append("TILL PAYIN: ").append(i + 1).append(Util.EOL).append(
                        tillPayins[i].toString()).append(Util.EOL);
            }
        }
        if (getTillPayOuts() == null)
        {
            strResult.append(Util.EOL).append("TILL PAYOUT:                          [null]").append(Util.EOL);
        }
        else
        {
            ReconcilableCountIfc[] tillPayouts = getTillPayOuts();
            if (tillPayouts.length == 0)
            {
                strResult.append(Util.EOL).append("TILL PAYOUT:                        [null]").append(Util.EOL);
            }
            for (int i = 0; i < tillPayouts.length; i++)
            {
                strResult.append(Util.EOL).append("TILL PAYOUT: ").append(i + 1).append(Util.EOL).append(
                        tillPayouts[i].toString()).append(Util.EOL);
            }
        }

        // gift certificate
        if (amountGrossGiftCertificateIssued == null)
        {
            strResult.append("amountGrossGiftCertificateIssued:       [null]\n");
        }
        else
        {
            strResult.append("amountGrossGiftCertificateIssued:       [").append(amountGrossGiftCertificateIssued)
                    .append("]\n");
        }
        if (unitsGrossGiftCertificateIssued == null)
        {
            strResult.append("unitsGrossGiftCertificateIssued:       [null]\n");
        }
        else
        {
            strResult.append("unitsGrossGiftCertificateIssued:       [").append(unitsGrossGiftCertificateIssued)
                    .append("]\n");
        }

        if (amountGrossGiftCertificateIssuedVoided == null)
        {
            strResult.append("amountGrossGiftCertificateIssuedVoided:       [null]\n");
        }
        else
        {
            strResult.append("amountGrossGiftCertificateIssuedVoided:       [").append(
                    amountGrossGiftCertificateIssuedVoided).append("]\n");
        }
        if (unitsGrossGiftCertificateIssuedVoided == null)
        {
            strResult.append("unitsGrossGiftCertificateIssuedVoided:       [null]\n");
        }
        else
        {
            strResult.append("unitsGrossGiftCertificateIssuedVoided:       [").append(
                    unitsGrossGiftCertificateIssuedVoided).append("]\n");
        }

        if (amountGrossGiftCertificateTendered == null)
        {
            strResult.append("amountGrossGiftCertificateTendered:       [null]\n");
        }
        else
        {
            strResult.append("amountGrossGiftCertificateTendered:       [").append(amountGrossGiftCertificateTendered)
                    .append("]\n");
        }
        if (unitsGrossGiftCertificateTendered == null)
        {
            strResult.append("unitsGrossGiftCertificateTendered:       [null]\n");
        }
        else
        {
            strResult.append("unitsGrossGiftCertificateTendered:       [").append(unitsGrossGiftCertificateTendered)
                    .append("]\n");
        }

        if (amountGrossGiftCertificateTenderedVoided == null)
        {
            strResult.append("amountGrossGiftCertificateTenderedVoided:       [null]\n");
        }
        else
        {
            strResult.append("amountGrossGiftCertificateTenderedVoided:       [").append(
                    amountGrossGiftCertificateTenderedVoided).append("]\n");
        }
        if (unitsGrossGiftCertificateTenderedVoided == null)
        {
            strResult.append("unitsGrossGiftCertificateTenderedVoided:       [null]\n");
        }
        else
        {
            strResult.append("unitsGrossGiftCertificateTenderedVoided:       [").append(
                    unitsGrossGiftCertificateTenderedVoided).append("]\n");
        }
        strResult.append("houseCardEnrollmentsApproved" + getHouseCardEnrollmentsApproved() + "\n");
        strResult.append("houseCardEnrollmentsDeclined" + getHouseCardEnrollmentsDeclined() + "\n");

        strResult.append(getTaxes().toString());

        if (amountGrossGiftCardItemCredit == null)
        {
            strResult.append("amountGrossGiftCardItemCredit: [null]\n");
        }
        else
        {
            strResult.append("amountGrossGiftCardItemCredit: [").append(amountGrossGiftCardItemCredit).append("]\n");
        }
        if (unitsGrossGiftCardItemCredit == null)
        {
            strResult.append("unitsGrossGiftCardItemCredit:       [null]\n");
        }
        else
        {
            strResult.append("unitsGrossGiftCardItemCredit:       [").append(unitsGrossGiftCardItemCredit)
                    .append("]\n");
        }
        if (amountGrossGiftCardItemCreditVoided == null)
        {
            strResult.append("amountGrossGiftCardItemCreditVoided: [null]\n");
        }
        else
        {
            strResult.append("amountGrossGiftCardItemCreditVoided: [").append(amountGrossGiftCardItemCreditVoided)
                    .append("]\n");
        }
        if (unitsGrossGiftCardItemCreditVoided == null)
        {
            strResult.append("unitsGrossGiftCardItemCreditVoided:       [null]\n");
        }
        else
        {
            strResult.append("unitsGrossGiftCardItemCreditVoided:       [").append(unitsGrossGiftCardItemCreditVoided)
                    .append("]\n");
        }
        if (amountGrossGiftCertificatesRedeemed == null)
        {
            strResult.append("amountGrossGiftCertificatesRedeemed: [null]\n");
        }
        else
        {
            strResult.append("amountGrossGiftCertificatesRedeemed: [").append(amountGrossGiftCertificatesRedeemed)
                    .append("]\n");
        }
        if (unitsGrossGiftCertificatesRedeemed == null)
        {
            strResult.append("unitsGrossGiftCertificatesRedeemed:       [null]\n");
        }
        else
        {
            strResult.append("unitsGrossGiftCertificatesRedeemed:       [").append(unitsGrossGiftCertificatesRedeemed)
                    .append("]\n");
        }

        if (amountGrossGiftCertificatesRedeemedVoided == null)
        {
            strResult.append("amountGrossGiftCertificatesRedeemedVoided: [null]\n");
        }
        else
        {
            strResult.append("amountGrossGiftCertificatesRedeemedVoided: [").append(
                    amountGrossGiftCertificatesRedeemedVoided).append("]\n");
        }
        if (unitsGrossGiftCertificatesRedeemedVoided == null)
        {
            strResult.append("unitsGrossGiftCertificatesRedeemedVoided:       [null]\n");
        }
        else
        {
            strResult.append("unitsGrossGiftCertificatesRedeemedVoided:       [").append(
                    unitsGrossGiftCertificatesRedeemedVoided).append("]\n");
        }
        if (amountGrossStoreCreditsIssued == null)
        {
            strResult.append("amountGrossStoreCreditsIssued: [null]\n");
        }
        else
        {
            strResult.append("amountGrossStoreCreditsIssued: [").append(amountGrossStoreCreditsIssued).append("]\n");
        }
        if (unitsGrossStoreCreditsIssued == null)
        {
            strResult.append("unitsGrossStoreCreditsIssued:       [null]\n");
        }
        else
        {
            strResult.append("unitsGrossStoreCreditsIssued:       [").append(unitsGrossStoreCreditsIssued)
                    .append("]\n");
        }

        if (amountGrossStoreCreditsIssuedVoided == null)
        {
            strResult.append("amountGrossStoreCreditsIssuedVoided: [null]\n");
        }
        else
        {
            strResult.append("amountGrossStoreCreditsIssuedVoided: [").append(amountGrossStoreCreditsIssuedVoided)
                    .append("]\n");
        }
        if (unitsGrossStoreCreditsIssuedVoided == null)
        {
            strResult.append("unitsGrossStoreCreditsIssuedVoided:       [null]\n");
        }
        else
        {
            strResult.append("unitsGrossStoreCreditsIssuedVoided:       [").append(unitsGrossStoreCreditsIssuedVoided)
                    .append("]\n");
        }

        if (amountGrossStoreCreditsRedeemed == null)
        {
            strResult.append("amountGrossStoreCreditsRedeemed: [null]\n");
        }
        else
        {
            strResult.append("amountGrossStoreCreditsRedeemed: [").append(amountGrossStoreCreditsRedeemed)
                    .append("]\n");
        }
        if (unitsGrossStoreCreditsRedeemed == null)
        {
            strResult.append("unitsGrossStoreCreditsRedeemed:       [null]\n");
        }
        else
        {
            strResult.append("unitsGrossStoreCreditsRedeemed:       [").append(unitsGrossStoreCreditsRedeemed).append(
                    "]\n");
        }

        if (amountGrossStoreCreditsRedeemedVoided == null)
        {
            strResult.append("amountGrossStoreCreditsRedeemedVoided: [null]\n");
        }
        else
        {
            strResult.append("amountGrossStoreCreditsRedeemedVoided: [").append(amountGrossStoreCreditsRedeemedVoided)
                    .append("]\n");
        }
        if (unitsGrossStoreCreditsRedeemedVoided == null)
        {
            strResult.append("unitsGrossStoreCreditsRedeemedVoided:       [null]\n");
        }
        else
        {
            strResult.append("unitsGrossStoreCreditsRedeemedVoided:       [").append(
                    unitsGrossStoreCreditsRedeemedVoided).append("]\n");
        }
        if (amountGrossItemEmployeeDiscount == null)
        {
            strResult.append("amountGrossItemEmployeeDiscount: [null]\n");
        }
        else
        {
            strResult.append("amountGrossItemEmployeeDiscount: [").append(amountGrossItemEmployeeDiscount)
                    .append("]\n");
        }
        if (unitsGrossItemEmployeeDiscount == null)
        {
            strResult.append("unitsGrossItemEmployeeDiscount:       [null]\n");
        }
        else
        {
            strResult.append("unitsGrossItemEmployeeDiscount:       [").append(unitsGrossItemEmployeeDiscount).append(
                    "]\n");
        }
        if (amountGrossItemEmployeeDiscountVoided == null)
        {
            strResult.append("amountGrossItemEmployeeDiscountVoided: [null]\n");
        }
        else
        {
            strResult.append("amountGrossItemEmployeeDiscountVoided: [").append(amountGrossItemEmployeeDiscountVoided)
                    .append("]\n");
        }
        if (unitsGrossItemEmployeeDiscountVoided == null)
        {
            strResult.append("unitsGrossItemEmployeeDiscountVoided:       [null]\n");
        }
        else
        {
            strResult.append("unitsGrossItemEmployeeDiscountVoided:       [").append(
                    unitsGrossItemEmployeeDiscountVoided).append("]\n");
        }
        if (amountGrossTransactionEmployeeDiscount == null)
        {
            strResult.append("amountGrossTransactionEmployeeDiscount: [null]\n");
        }
        else
        {
            strResult.append("amountGrossTransactionEmployeeDiscount: [")
                    .append(amountGrossTransactionEmployeeDiscount).append("]\n");
        }
        if (unitsGrossTransactionEmployeeDiscount == null)
        {
            strResult.append("unitsGrossTransactionEmployeeDiscount:       [null]\n");
        }
        else
        {
            strResult.append("unitsGrossTransactionEmployeeDiscount:       [").append(
                    unitsGrossTransactionEmployeeDiscount).append("]\n");
        }
        if (amountGrossTransactionEmployeeDiscountVoided == null)
        {
            strResult.append("amountGrossTransactionEmployeeDiscountVoided: [null]\n");
        }
        else
        {
            strResult.append("amountGrossTransactionEmployeeDiscountVoided: [").append(
                    amountGrossTransactionEmployeeDiscountVoided).append("]\n");
        }
        if (unitsGrossTransactionEmployeeDiscountVoided == null)
        {
            strResult.append("unitsGrossTransactionEmployeeDiscountVoided:       [null]\n");
        }
        else
        {
            strResult.append("unitsGrossTransactionEmployeeDiscountVoided:       [").append(
                    unitsGrossTransactionEmployeeDiscountVoided).append("]\n");
        }
        
        return strResult.toString();
    }

    /**
     * Retrieves the source-code-control system revision number.
     *
     * @return String representation of revision number
     */
    public String getRevisionNumber()
    {
        return Util.parseRevisionNumber(revisionNumber);
    }

    // I18N
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

    /**
     * @return Returns the unitsRestockingFeesFromNonTaxableItems.
     */
    public BigDecimal getUnitsRestockingFeesFromNonTaxableItems()
    {
        if (unitsRestockingFeesFromNonTaxableItems == null)
        {
            unitsRestockingFeesFromNonTaxableItems = BigDecimal.ZERO;
        }
        return unitsRestockingFeesFromNonTaxableItems;
    }

    /**
     * @param unitsRestockingFeesFromNonTaxableItems The
     *            unitsRestockingFeesFromNonTaxableItems to set.
     */
    public void setUnitsRestockingFeesFromNonTaxableItems(BigDecimal unitsRestockingFeesFromNonTaxableItems)
    {
        this.unitsRestockingFeesFromNonTaxableItems = unitsRestockingFeesFromNonTaxableItems;
    }

    /**
     * @return Returns the amountRestockingFeesFromNonTaxableItems.
     */
    public CurrencyIfc getAmountRestockingFeesFromNonTaxableItems()
    {
        if (amountRestockingFeesFromNonTaxableItems == null)
        {
            amountRestockingFeesFromNonTaxableItems = DomainGateway.getBaseCurrencyInstance();
        }
        return amountRestockingFeesFromNonTaxableItems;
    }

    /**
     * @param amountRestockingFeesFromNonTaxableItems The
     *            amountRestockingFeesFromNonTaxableItems to set.
     */
    public void setAmountRestockingFeesFromNonTaxableItems(CurrencyIfc amountRestockingFeesFromNonTaxableItems)
    {
        this.amountRestockingFeesFromNonTaxableItems = amountRestockingFeesFromNonTaxableItems;
    }

    /**
     * Add the units to unitsRestockingFeesFromNonTaxableItems
     *
     * @param units
     */
    public void addUnitsRestockingFeesFromNonTaxableItems(BigDecimal units)
    {
        if (unitsRestockingFeesFromNonTaxableItems == null)
        {
            unitsRestockingFeesFromNonTaxableItems = units;
        }
        else
        {
            unitsRestockingFeesFromNonTaxableItems = unitsRestockingFeesFromNonTaxableItems.add(units);
        }
    }

    /**
     * Add the amount to the amountRestockingFeesFromNonTaxableItems
     *
     * @param amount
     */
    public void addAmountRestockingFeesFromNonTaxableItems(CurrencyIfc amount)
    {
        if (amountRestockingFeesFromNonTaxableItems == null)
        {
            amountRestockingFeesFromNonTaxableItems = amount;
        }
        else
        {
            amountRestockingFeesFromNonTaxableItems = amountRestockingFeesFromNonTaxableItems.add(amount);
        }
    }

    /**
     * Add the amount to the amountBillPayments
     * @param amount
     */
    public void addAmountBillPayments(CurrencyIfc amount)
    {
        if (amountBillPayments == null)
        {
            amountBillPayments = amount;
        }
        else
        {
            amountBillPayments = getAmountBillPayments().add(amount);
        }
    }

    /**
     * gets the amountBillPayments
     * @param amount
     */
    public CurrencyIfc getAmountBillPayments()
    {
        if (amountBillPayments == null)
        {
            amountBillPayments = DomainGateway.getBaseCurrencyInstance();
        }
        return amountBillPayments;
    }

    /**
     * sets the amountBillPayments
     * @param amount
     */
    public void setAmountBillPayments(CurrencyIfc amount)
    {
        amountBillPayments = amount;
    }

    /**
     * Adds to count of bill payment transactions.
     *
     * @param count count of bill payment transactions
     */
    public void addCountBillPayments(int count)
    {
        countBillPayments = countBillPayments + count;
    }
    
    /**
     * Gets the count of bill payments
     */
    public int getCountBillPayments()
    {
        return countBillPayments;
    }

    /**
     * sets the count of bill payments
     * @param countBillPayments
     */
    public void setCountBillPayments(int countBillPayments)
    {
        this.countBillPayments = countBillPayments;
    }

    /*
     * (non-Javadoc)
     * @see oracle.retail.stores.domain.financial.FinancialTotalsIfc#addAmountChangeRoundedIn(oracle.retail.stores.commerceservices.common.currency.CurrencyIfc)
     */
    public void addAmountChangeRoundedIn(CurrencyIfc amount)
    {
        if (amountChangeRoundedIn == null)
        {
            amountChangeRoundedIn = amount;
        }
        else
        {
            amountChangeRoundedIn = getAmountChangeRoundedIn().add(amount);
        }
    }

    /*
     * (non-Javadoc)
     * @see oracle.retail.stores.domain.financial.FinancialTotalsIfc#getAmountChangeRoundedIn()
     */
    public CurrencyIfc getAmountChangeRoundedIn()
    {
        if (amountChangeRoundedIn == null)
        {
            amountChangeRoundedIn = DomainGateway.getBaseCurrencyInstance();
        }
        return amountChangeRoundedIn;
    }

    /*
     * (non-Javadoc)
     * @see oracle.retail.stores.domain.financial.FinancialTotalsIfc#setAmountChangeRoundedIn(oracle.retail.stores.commerceservices.common.currency.CurrencyIfc)
     */
    public void setAmountChangeRoundedIn(CurrencyIfc amountChangeRoundedIn)
    {
        this.amountChangeRoundedIn = amountChangeRoundedIn;
    }

    /**
     * Add the amount to the amountBillPayments
     * @param amount
     */
    public void addAmountChangeRoundedOut(CurrencyIfc amount)
    {
        if (amountChangeRoundedOut == null)
        {
            amountChangeRoundedOut = amount;
        }
        else
        {
            amountChangeRoundedOut = getAmountChangeRoundedOut().add(amount);
        }
    }

    /*
     * (non-Javadoc)
     * @see oracle.retail.stores.domain.financial.FinancialTotalsIfc#getAmountChangeRoundedOut()
     */
    public CurrencyIfc getAmountChangeRoundedOut()
    {
        if (amountChangeRoundedOut == null)
        {
            amountChangeRoundedOut = DomainGateway.getBaseCurrencyInstance();
        }
        return amountChangeRoundedOut;
    }

    /*
     * (non-Javadoc)
     * @see oracle.retail.stores.domain.financial.FinancialTotalsIfc#setAmountChangeRoundedOut(oracle.retail.stores.commerceservices.common.currency.CurrencyIfc)
     */
    public void setAmountChangeRoundedOut(CurrencyIfc amountChangeRoundedOut)
    {
        this.amountChangeRoundedOut = amountChangeRoundedOut;
    }

    /*
     * (non-Javadoc)
     * @see oracle.retail.stores.domain.financial.FinancialTotalsIfc#calulateAmountChangeRounded()
     */
    public CurrencyIfc calulateAmountChangeRounded()
    {
        return getAmountChangeRoundedIn().subtract(getAmountChangeRoundedOut());
    }
    
    /**
     * Returns VAT enabled flag
     * 
     * @return boolean
     */
    public boolean isVatEnabled()
    {
        return vatEnabled;
    }

    /**
     * Sets VAT enabled flag
     * 
     * @param boolean
     */
    public void setVatEnabled(boolean vatEnabled)
    {
        this.vatEnabled = vatEnabled;
    }    
    
    /**
     * Gets the amount of orders
     */
    public CurrencyIfc getAmountOrders()
    {
        return getAmountSpecialOrderNew().add(
                getAmountSpecialOrderPartial().add(getAmountOrderPayments().add(getAmountOrderCancels())));
    }
}
