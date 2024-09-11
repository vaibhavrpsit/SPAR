/* ===========================================================================
* Copyright (c) 1998, 2011, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/modules/domain/src/oracle/retail/stores/domain/ixretail/financial/v21/LogFinancialTotals.java /main/12 2011/12/05 12:16:14 cgreene Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    cgreene   12/05/11 - updated from deprecated packages and used more
 *                         bigdecimal constants
 *    cgreene   05/26/10 - convert to oracle packaging
 *    abondala  01/03/10 - update header date
 *
 * ===========================================================================
 * $Log:
 *    9    360Commerce 1.8         4/3/2008 1:21:25 PM    Jack G. Swan    Added
 *          restocking fee for non taxable items to POSLog export and import.
 *    8    360Commerce 1.7         6/26/2007 11:13:58 AM  Ashok.Mondal    I18N
 *         changes to export and import POSLog.
 *    7    360Commerce 1.6         6/5/2007 2:04:43 PM    Ranjan X Ojha   Code
 *         Review updates to POSLog for VAT
 *    6    360Commerce 1.5         5/22/2007 9:14:25 AM   Sandy Gu        Check
 *          in PosLog enhancement for VAT
 *    5    360Commerce 1.4         4/25/2007 10:00:50 AM  Anda D. Cadar   I18N
 *         merge
 *    4    360Commerce 1.3         1/22/2006 11:41:34 AM  Ron W. Haight
 *         Removed references to com.ibm.math.BigDecimal
 *    3    360Commerce 1.2         3/31/2005 4:28:54 PM   Robert Pearse   
 *    2    360Commerce 1.1         3/10/2005 10:23:11 AM  Robert Pearse   
 *    1    360Commerce 1.0         2/11/2005 12:12:23 PM  Robert Pearse   
 *
 *   Revision 1.6  2004/08/10 07:17:10  mwright
 *   Merge (3) with top of tree
 *
 *   Revision 1.5.2.2  2004/08/06 02:25:45  mwright
 *   Removed task tags for completed tasks
 *
 *   Revision 1.5.2.1  2004/07/29 00:58:35  mwright
 *   Added tax totals element
 *   Added employee elements: discounts voided, transaction discounts, transaction discounts voided
 *   Added till price overrides
 *
 *   Revision 1.5  2004/06/24 09:15:09  mwright
 *   POSLog v2.1 (second) merge with top of tree
 *
 *
 *   Revision 1.4  2004/05/11 23:03:02  jdeleau
 *   @scr 4218 Backout recent changes to remove TransactionDiscounts,
 *   going to go a different route and remove the newly added
 *   voids and grosses instead.
 *
 *   Revision 1.3  2004/05/11 20:36:16  jdeleau
 *   @scr 4218 AmountTransactionDiscounts (and NumberTransactionDiscounts)
 *    is no longer a data variable and should not be included in POSLog v21.
 *   They have been deprecated in favor of AmountGrossTransactionDiscount
 *   and UnitsGrossTransactionDiscount
 *
 *   Revision 1.2.2.2  2004/06/23 00:21:00  mwright
 *   Corrected total transaction count calculation to cater for voiding transactions
 *
 *   Revision 1.2.2.1  2004/06/10 10:48:38  mwright
 *   Updated to use schema types in commerce services
 *
 *
 *   Revision 1.2  2004/05/06 03:11:12  mwright
 *   Initial revision for POSLog v2.1 merge with top of tree
 *
 *   Revision 1.1.2.3  2004/04/26 07:23:26  mwright
 *   Changed financial totals from being mirror of 360 v1.0 logger, to actually implement the v2.1 TCSettle object:
 *   Added POSLogTCTotalMeasures and POSLogTCPettyCashDisbursments objects to element, with methods to populate them.
 *   Several pairs of count/amount are now total objects.
 *
 *   Revision 1.1.2.2  2004/04/20 11:46:34  mwright
 *   Updated to use TCSettle object instead of FinancialTotals object
 *
 *   Revision 1.1.2.1  2004/04/19 07:06:02  mwright
 *   Initial revision for v2.1
 *
 * ===========================================================================
 */
package oracle.retail.stores.domain.ixretail.financial.v21;

import java.util.List;
import java.util.ArrayList;

import java.math.BigDecimal;

// XML imports
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import oracle.retail.stores.foundation.utility.xml.XMLConversionException;

//import oracle.retail.stores.domain.financial.FinancialCountTenderItemIfc;
import oracle.retail.stores.domain.financial.FinancialTotalsIfc;
import oracle.retail.stores.domain.financial.ReconcilableCountIfc;
import oracle.retail.stores.domain.financial.TaxTotalsContainerIfc;
import oracle.retail.stores.domain.financial.TaxTotalsIfc;

import oracle.retail.stores.domain.ixretail.IXRetailGateway;
import oracle.retail.stores.domain.ixretail.log.AbstractIXRetailTranslator;
import oracle.retail.stores.domain.ixretail.financial.LogFinancialTotalsIfc;
import oracle.retail.stores.domain.ixretail.financial.LogFinancialCountIfc;

import oracle.retail.stores.commerceservices.common.currency.CurrencyIfc;
import oracle.retail.stores.commerceservices.ixretail.schematypes.v21.POSLogTCSettleIfc;
import oracle.retail.stores.commerceservices.ixretail.schematypes.v21.ReconcilableCountElement360Ifc;
import oracle.retail.stores.commerceservices.ixretail.schematypes.v21.FinancialCountElement360Ifc;
import oracle.retail.stores.commerceservices.ixretail.schematypes.v21.POSLogAmountIfc;
import oracle.retail.stores.commerceservices.ixretail.schematypes.v21.POSLogTotalsIfc;
import oracle.retail.stores.commerceservices.ixretail.schematypes.v21.POSLogTCTenderPickupIfc;
import oracle.retail.stores.commerceservices.ixretail.schematypes.v21.POSLogTCTenderTotalIfc;
import oracle.retail.stores.commerceservices.ixretail.schematypes.v21.POSLogTCStoreCouponIfc;
import oracle.retail.stores.commerceservices.ixretail.schematypes.v21.POSLogTCCouponSummaryIfc;
import oracle.retail.stores.commerceservices.ixretail.schematypes.v21.POSLogTCMiscellaneousFeesIfc;
import oracle.retail.stores.commerceservices.ixretail.schematypes.v21.POSLogTCPaymentsCollectedIfc;
import oracle.retail.stores.commerceservices.ixretail.schematypes.v21.POSLogTCTotalMeasuresIfc;
import oracle.retail.stores.commerceservices.ixretail.schematypes.v21.POSLogTCPettyCashDisbursmentsIfc;
import oracle.retail.stores.commerceservices.ixretail.schematypes.v21.TaxTotalElement360Ifc;
//--------------------------------------------------------------------------
/**
    This class creates the elements for a FinancialTotals. <P>

    @version $Revision: /main/12 $
**/
//--------------------------------------------------------------------------
public class LogFinancialTotals
extends AbstractIXRetailTranslator
implements LogFinancialTotalsIfc
{
    /**
       revision number supplied by source-code-control system
    **/
    public static String revisionNumber = "$Revision: /main/12 $";

    //----------------------------------------------------------------------------
    /**
        Constructs LogFinancialTotals object. <P>
    **/
    //----------------------------------------------------------------------------
    public LogFinancialTotals()
    {
    }

    //---------------------------------------------------------------------
    /**
       Creates element for the specified financial totals object. <P>
       @param financialTotals financial totals reference
       @param doc parent document (unused)
       @param el Element to update
       @param nodeName node name (unused)
       @return Updated element
       @exception XMLConversionException thrown if error occurs
    **/
    //---------------------------------------------------------------------
    public Element createElement(FinancialTotalsIfc financialTotals,
                                 Document doc,      // unused
                                 Element el,
                                 String nodeName)   // unused
    throws XMLConversionException
    {

        POSLogTCSettleIfc financialTotalsElement = (POSLogTCSettleIfc)el;

        createSimpleTypeElements(financialTotals, financialTotalsElement);

        createCountElements(financialTotals, financialTotalsElement);

        addTotalMeasures(financialTotals, financialTotalsElement);
        
        accumulateTotals(financialTotalsElement);
        
        createTaxElements(financialTotals, financialTotalsElement);
        
        
        return financialTotalsElement;
    }

    //---------------------------------------------------------------------
    /**
       Creates element for the specified financial totals object. <P>
       @param financialTotals financial totals reference
       @param doc parent document
       @param el parent element
       @return Element representing quantity
       @exception XMLConversionException thrown if error occurs
    **/
    //---------------------------------------------------------------------
    public Element createElement(FinancialTotalsIfc financialTotals,
                                 Document doc,
                                 Element el)
    throws XMLConversionException
    {
        return createElement(financialTotals, null, el, null);
    }

    
    //---------------------------------------------------------------------
    /**
       Creates and poplates the total measures element
       @param financialTotalsElement financial totals elements
       @param financialTotals financial totals object
    **/
    //---------------------------------------------------------------------
    protected void addTotalMeasures(FinancialTotalsIfc financialTotals, POSLogTCSettleIfc financialTotalsElement)
    {
        POSLogTCTotalMeasuresIfc measures = getSchemaTypesFactory().getPOSLogTCTotalMeasuresInstance();
        
        measures.setNoSaleTransactionCount(financialTotals.getNumberNoSales());
        
        financialTotalsElement.setTotalMeasures(measures);
    }
    
    
    //---------------------------------------------------------------------
    /**
       Adds up several totals into accumulators in the ixretail-specified part of the TCSettle object.<P>
       @param financialTotalsElement financial totals elements
    **/
    //---------------------------------------------------------------------
    protected void accumulateTotals(POSLogTCSettleIfc financialTotalsElement)
    {

        POSLogAmountIfc grossPositiveAmount = getSchemaTypesFactory().getPOSLogAmountInstance();
        grossPositiveAmount.add(financialTotalsElement.getTotalNetSalesAmount());
        grossPositiveAmount.add(financialTotalsElement.getAmountGrossNonTaxableItemSales());
        grossPositiveAmount.add(financialTotalsElement.getTotalGrossSalesExemptTaxAmount());
        grossPositiveAmount.add(financialTotalsElement.getAmountGrossTaxableItemReturnsVoided());
        grossPositiveAmount.add(financialTotalsElement.getAmountGrossNonTaxableItemReturnsVoided());
        grossPositiveAmount.add(financialTotalsElement.getAmountGrossTaxExemptItemReturnsVoided());
        financialTotalsElement.setGrossPositiveAmount(grossPositiveAmount);

        POSLogAmountIfc grossNegativeAmount = getSchemaTypesFactory().getPOSLogAmountInstance();
        grossNegativeAmount.add(financialTotalsElement.getAmountGrossTaxableItemReturns());
        grossNegativeAmount.add(financialTotalsElement.getAmountGrossNonTaxableItemReturns());
        grossNegativeAmount.add(financialTotalsElement.getAmountGrossTaxExemptItemReturns());
        grossNegativeAmount.add(financialTotalsElement.getAmountGrossTaxableItemSalesVoided());
        grossNegativeAmount.add(financialTotalsElement.getAmountGrossNonTaxableItemSalesVoided());
        grossNegativeAmount.add(financialTotalsElement.getAmountGrossTaxExemptItemSalesVoided());
        financialTotalsElement.setGrossNegativeAmount(grossNegativeAmount);

        POSLogAmountIfc miscDiscountAmount = getSchemaTypesFactory().getPOSLogAmountInstance();
        miscDiscountAmount.add(financialTotalsElement.getAmountTransactionDiscounts());
        miscDiscountAmount.add(financialTotalsElement.getAmountItemDiscounts());
        String miscDiscountCount = Integer.toString(  financialTotalsElement.getNumberTransactionDiscounts() 
                                                    + financialTotalsElement.getNumberItemDiscounts());
        financialTotalsElement.setMiscellaneousDiscounts(makeTotal(miscDiscountAmount, miscDiscountCount));
        
        POSLogTCStoreCouponIfc storeCoupon = getSchemaTypesFactory().getPOSLogTCStoreCouponInstance();
        POSLogAmountIfc storeCouponAmount = getSchemaTypesFactory().getPOSLogAmountInstance();
        storeCouponAmount.add(financialTotalsElement.getAmountItemDiscStoreCoupons());
        storeCouponAmount.add(financialTotalsElement.getAmountTransactionDiscStoreCoupons());
        int storeCouponCount = financialTotalsElement.getNumberItemDiscStoreCoupons()
                             + financialTotalsElement.getNumberTransactionDiscStoreCoupons();
        financialTotalsElement.setStoreCoupons(makeStoreCoupon(storeCouponAmount, storeCouponCount));
        
        
        POSLogAmountIfc miscFeeAmount = getSchemaTypesFactory().getPOSLogAmountInstance();
        miscFeeAmount.add(financialTotalsElement.getAmountRestockingFees());
        miscFeeAmount.add(financialTotalsElement.getAmountRestockingFeesForNonTaxable());
        miscFeeAmount.add(financialTotalsElement.getAmountShippingCharges());
        BigDecimal miscFeeCount;
        if (financialTotalsElement.getUnitsRestockingFees() != null)
        {
            miscFeeCount = financialTotalsElement.getUnitsRestockingFees();
        }
        else
        {
            miscFeeCount = BigDecimal.ZERO;
        }
        if (financialTotalsElement.getUnitsRestockingFeesForNonTaxable() != null)
        {
            miscFeeCount = miscFeeCount.add(financialTotalsElement.getUnitsRestockingFees());
        }
        miscFeeCount = miscFeeCount.add(new BigDecimal(Integer.toString(financialTotalsElement.getNumberShippingCharges())));
        
        financialTotalsElement.setMiscellaneousFees(makeMiscFees(miscFeeAmount, miscFeeCount.toBigInteger().toString()));  // BigInteger strips zeroes
        
    }

    
    //---------------------------------------------------------------------
    /**
       Creates elements for counts. <P>
       @param financialTotals financial totals object
       @param financialTotalsElement financial totals elements
       @exectpion XMLConversionException thrown if error occurs
    **/
    //---------------------------------------------------------------------
    protected void createCountElements(FinancialTotalsIfc financialTotals, POSLogTCSettleIfc financialTotalsElement)
    throws XMLConversionException
    {
        ReconcilableCountElement360Ifc count;
        boolean something;
        
        count = getSchemaTypesFactory().getReconcilableCountElement360Instance();
        
        something = createReconcilableCountElement(financialTotals.getStartingFloatCount(), count);
        if (something)
        {
            financialTotalsElement.setStartingFloatCount(count);
            count = getSchemaTypesFactory().getReconcilableCountElement360Instance();
        }

        something = createReconcilableCountElement(financialTotals.getEndingFloatCount(), count);
        if (something)
        {
            financialTotalsElement.setEndingFloatCount(count);
            count = getSchemaTypesFactory().getReconcilableCountElement360Instance();
        }

        something = createReconcilableCountElement(financialTotals.getStartingSafeCount(), count);
        if (something)
        {
            financialTotalsElement.setStartingSafeCount(count);
            count = getSchemaTypesFactory().getReconcilableCountElement360Instance();
        }

        something = createReconcilableCountElement(financialTotals.getEndingSafeCount(), count);
        if (something)
        {
            financialTotalsElement.setEndingSafeCount(count);
            count = getSchemaTypesFactory().getReconcilableCountElement360Instance();
        }

        something = createReconcilableCountElement(financialTotals.getCombinedCount(), count);
        if (something)
        {
            financialTotalsElement.setCombinedCount(count);
            count = getSchemaTypesFactory().getReconcilableCountElement360Instance();
        }
        
        if (financialTotals.getTenderCount() != null)
        {
            LogFinancialCountIfc logCount = IXRetailGateway.getFactory().getLogFinancialCountInstance();
            FinancialCountElement360Ifc countElement = getSchemaTypesFactory().getFinancialCountElement360Instance();
            logCount.createElement(financialTotals.getTenderCount(), null, countElement, null);
            financialTotalsElement.setTenderSummary360(countElement);
        }

        /* returns nothing when there is a pickup in the table
        ReconcilableCountIfc[] pickups = financialTotals.getTillPickups();
        System.out.println("Pickups: count = " + pickups.length);
        for (int i = 0; i < pickups.length; i++)
        {
            System.out.println("entered amount:       " + pickups[i].getEntered().getAmount().toString());
            System.out.println("entered item count:  " + pickups[i].getEntered().getNumberItems());
            FinancialCountTenderItemIfc[] items = pickups[i].getEntered().getTenderItems();
            for (int j = 0; j < items.length; j++)
            {
                System.out.println("  Amount in:   "  + items[j].getAmountIn().toString());    
                System.out.println("  Amount out:  "  + items[j].getAmountOut().toString());    
                System.out.println("  Items in:    "  + items[j].getNumberItemsIn());    
                System.out.println("  Items out:   "  + items[j].getNumberItemsOut());    
            }
            System.out.println("expected amount:       " + pickups[i].getExpected().getAmount().toString());
            System.out.println("expected item count:  " + pickups[i].getExpected().getNumberItems());
            FinancialCountTenderItemIfc[] itemsX = pickups[i].getEntered().getTenderItems();
            for (int j = 0; j < itemsX.length; j++)
            {
                System.out.println("  Amount in:   "  + itemsX[j].getAmountIn().toString());    
                System.out.println("  Amount out:  "  + itemsX[j].getAmountOut().toString());    
                System.out.println("  Items in:    "  + itemsX[j].getNumberItemsIn());    
                System.out.println("  Items out:   "  + itemsX[j].getNumberItemsOut());    
            }
            System.out.println();    
        }
        */
        financialTotalsElement.setTillPickupsList(createArrayReconcilableCountElements(financialTotals.getTillPickups()));
        financialTotalsElement.setCountTillPickups(financialTotals.getCountTillPickups());

        /*
        ReconcilableCountIfc[] loans = financialTotals.getTillLoans();
        System.out.println("Loans: count = " + loans.length);
        */
        financialTotalsElement.setTillLoansList(createArrayReconcilableCountElements(financialTotals.getTillLoans()));
        financialTotalsElement.setCountTillLoans(financialTotals.getCountTillLoans());

        /*
        ReconcilableCountIfc[] payIns = financialTotals.getTillPayIns();
        System.out.println("PayIns: count = " + payIns.length);
        */
        financialTotalsElement.setTillPayInsList(createArrayReconcilableCountElements(financialTotals.getTillPayIns()));
        
        /*
        ReconcilableCountIfc[] payOuts = financialTotals.getTillPayOuts();
        System.out.println("PayOuts: count = " + payOuts.length);
        */
        financialTotalsElement.setTillPayOutsList(createArrayReconcilableCountElements(financialTotals.getTillPayOuts()));
    }

    //---------------------------------------------------------------------
    /**
       Creates element for reconcilable count. <P>
       @param reconcilableCount reconcilable count object
       @param el parent element
       @param elementName count element name
       @exception XMLConversionException thrown if error occurs
    **/
    //---------------------------------------------------------------------
    public boolean createReconcilableCountElement(ReconcilableCountIfc reconcilableCount, ReconcilableCountElement360Ifc el)
    throws XMLConversionException
    {
        boolean something = false;
        LogFinancialCountIfc logCount = IXRetailGateway.getFactory().getLogFinancialCountInstance();

        // only log count if it exists
        if (reconcilableCount.getEntered() != null)
        {
            FinancialCountElement360Ifc countElement = getSchemaTypesFactory().getFinancialCountElement360Instance();
            logCount.createElement(reconcilableCount.getEntered(), null, countElement, null);
            el.setEntered(countElement);
            something = true;
        }
        if (reconcilableCount.getExpected() != null)
        {
            FinancialCountElement360Ifc countElement = getSchemaTypesFactory().getFinancialCountElement360Instance();
            logCount.createElement(reconcilableCount.getExpected(), null, countElement, null);
            el.setExpected(countElement);
            something = true;
        }
        return something;
    }

    //---------------------------------------------------------------------
    /**
       Creates elements for an array of reconcilable counts. <P>
       @param countArray array of reconcilable counts
       @return a list of ReconcilableCountElement360Ifc elements, or null if none were found
       @exception XMLConversionException thrown if error occrs
    **/
    //---------------------------------------------------------------------
    protected List createArrayReconcilableCountElements(ReconcilableCountIfc[] countArray)
    throws XMLConversionException
    {
        List list = null;
        for (int i = 0; i < countArray.length; i++)
        {
            ReconcilableCountElement360Ifc count = getSchemaTypesFactory().getReconcilableCountElement360Instance();
            boolean something = createReconcilableCountElement(countArray[i], count);
            if (something)
            {
                if (list == null)
                {
                    list = new ArrayList();
                }
                list.add(count);
            }
        }
        return list;
    }

    POSLogAmountIfc makeAmount(CurrencyIfc amount)
    {
        if (amount == null)
        {
            return null;
        }
        return getSchemaTypesFactory().getPOSLogAmountInstance().initialize(currency(amount));
    }
    

    POSLogTotalsIfc makeTotal(CurrencyIfc amount, String count)
    {
        if (amount == null)
        {
            return null;
        }
        POSLogAmountIfc posLogAmount = getSchemaTypesFactory().getPOSLogAmountInstance().initialize(currency(amount));
        return makeTotal(posLogAmount, count);
    }
    
    POSLogTotalsIfc makeTotal(POSLogAmountIfc amount, String count)
    {
        if (amount == null)
        {
            return null;
        }
        POSLogTotalsIfc total = getSchemaTypesFactory().getPOSLogTotalsInstance();
        total.setAmount(amount);
        total.setCount(count);
        total.setReason("");     // the reason is mandatory, but we don;t have anything to put in
        
        return total;
    }
    
    POSLogTCTenderPickupIfc makeTenderPickup(CurrencyIfc amount, int count)
    {
        if (amount == null)
        {
            return null;
        }
        POSLogTCTenderPickupIfc pickup      = getSchemaTypesFactory().getPOSLogTCTenderPickupInstance();
        POSLogTCTenderTotalIfc  tenderTotal = getSchemaTypesFactory().getPOSLogTCTenderTotalInstance();
        POSLogTotalsIfc         total       = makeTotal(amount, Integer.toString(count));
        
        // This tender total is bogus - it is just a copy of the total amount
        tenderTotal.setTenderTotal(total);
        pickup.setTotals(total);
        pickup.addTender(tenderTotal);
        
        return pickup;
    }

    POSLogTCMiscellaneousFeesIfc makeMiscFees(POSLogAmountIfc amount, String count)
    {
        if (amount == null)
        {
            return null;
        }
        POSLogTCMiscellaneousFeesIfc fees        = getSchemaTypesFactory().getPOSLogTCMiscellaneousFeesInstance();
        POSLogTCTenderTotalIfc       tenderTotal = getSchemaTypesFactory().getPOSLogTCTenderTotalInstance();
        POSLogTotalsIfc              total       = makeTotal(amount, count);
        
        // This tender total is bogus - it is just a copy of the total amount
        tenderTotal.setTenderTotal(total);
        fees.setTotals(total);
        fees.addTender(tenderTotal);
        
        return fees;
    }

    POSLogTCPaymentsCollectedIfc makePaymentsCollected(CurrencyIfc amount, String count)
    {
        if (amount == null)
        {
            return null;
        }
        POSLogTCPaymentsCollectedIfc payments    = getSchemaTypesFactory().getPOSLogTCPaymentsCollectedInstance();
        POSLogTCTenderTotalIfc       tenderTotal = getSchemaTypesFactory().getPOSLogTCTenderTotalInstance();
        POSLogTotalsIfc              total       = makeTotal(amount, count);
        
        // This tender total is bogus - it is just a copy of the total amount
        tenderTotal.setTenderTotal(total);
        payments.setTotals(total);
        payments.addTender(tenderTotal);
        
        return payments;
    }
    
    
    POSLogTCStoreCouponIfc makeStoreCoupon(POSLogAmountIfc amount, int count)
    {
        if (amount == null)
        {
            return null;
        }
        POSLogTCStoreCouponIfc   coupon  = getSchemaTypesFactory().getPOSLogTCStoreCouponInstance();
        POSLogTCCouponSummaryIfc summary = getSchemaTypesFactory().getPOSLogTCCouponSummaryInstance();
        POSLogTotalsIfc          total   = makeTotal(amount, Integer.toString(count));
        
        summary.setCouponType("");      // we don't know what the type is, but it is mandatory
        summary.setTotal(total);        // duplicate of total in parent element
        
        coupon.setTotals(total);
        coupon.addCouponSummary(summary);
        
        return coupon;
    }
    
    POSLogTCPettyCashDisbursmentsIfc makePettyCashDisbursment(CurrencyIfc amount, String count)
    {
        if (amount == null)
        {
            return null;
        }
        POSLogTCPettyCashDisbursmentsIfc petty = getSchemaTypesFactory().getPOSLogTCPettyCashDisbursmentsInstance();
        POSLogTCTenderTotalIfc           tenderTotal = getSchemaTypesFactory().getPOSLogTCTenderTotalInstance();
        POSLogTotalsIfc                  total       = makeTotal(amount, count);
        
        // This tender total is bogus - it is just a copy of the total amount
        tenderTotal.setTenderTotal(total);
        petty.setTotals(total);
        
        
        return petty;
    }

    //---------------------------------------------------------------------
    /**
       Creates one element for new tax history table. 
       @param taxTotal TaxTotalsIfc object from financial totals
       @exception XMLConversionException thrown if error occurs
    **/
    //---------------------------------------------------------------------
    protected TaxTotalElement360Ifc createTaxHistoryElement(TaxTotalsIfc taxTotal)
    throws XMLConversionException
    {
        TaxTotalElement360Ifc taxHistoryElement = getSchemaTypesFactory().getTaxTotalElement360Instance();
        
        // This is a dummy value that will be changed by the caller for till close transactions.
        // The tax totals are only imported for till close transactions.
        taxHistoryElement.setTillID("0");
        
        taxHistoryElement.setAmount(makeAmount(taxTotal.getTaxAmount()));
        taxHistoryElement.setAuthorityID(Integer.toString(taxTotal.getTaxAuthorityId()));
        taxHistoryElement.setCount(taxTotal.getTaxCount());
        taxHistoryElement.setGroupID(Integer.toString(taxTotal.getTaxGroupId()));
        taxHistoryElement.setTaxType(Integer.toString(taxTotal.getTaxType()));
        taxHistoryElement.setTaxHoliday(new Boolean(taxTotal.isTaxHoliday()));
        taxHistoryElement.setInclusiveTaxFlag(new Boolean(taxTotal.getInclusiveTaxFlag()));
        
        
        /*
        taxTotal.getTaxRuleName()
        taxTotal.getUniqueId()
        taxTotal.getTaxAuthorityName()
        */
        return taxHistoryElement;
    }
    
    //---------------------------------------------------------------------
    /**
       Creates elements for new tax history table. 
       @param financialTotals financial totals object
       @param financialTotalsElement financial totals element
       @exception XMLConversionException thrown if error occurs
    **/
    //---------------------------------------------------------------------
    protected void createTaxElements(FinancialTotalsIfc financialTotals, POSLogTCSettleIfc financialTotalsElement)
    throws XMLConversionException
    {
        
        TaxTotalsContainerIfc container = financialTotals.getTaxes();
        TaxTotalsIfc[]        totals    = container.getTaxTotals();
        
        // need: store, ws, date, till - they are all in the transaction already
        for (int i = 0; i < totals.length; i++)
        {
            TaxTotalElement360Ifc taxTotal = createTaxHistoryElement(totals[i]);
            financialTotalsElement.addTaxTotal(taxTotal);
            
        }
    }
    
    //---------------------------------------------------------------------
    /**
       Creates elements for simple types in totals. 
       @param financialTotals financial totals object
       @param financialTotalsElement financial totals element
       @exception XMLConversionException thrown if error occurs
    **/
    //---------------------------------------------------------------------
    protected void createSimpleTypeElements(FinancialTotalsIfc financialTotals, POSLogTCSettleIfc financialTotalsElement)
    throws XMLConversionException
    {
        /**
         * Total transactions should be number of non-voided plus number of voided plus number of voiding (control) transactions.
         * The domain object does not count the voiding transactions, but the total that includes them is stored in the table.
         * We add the number of voided transactions to the total, because there are as many voiding transactions as there are voided ones.
         */
        int totalTransactionsVoided = financialTotals.getTransactionCount();
        
        int countGrossTaxableTransactionSalesVoided         = financialTotals.getCountGrossTaxableTransactionSalesVoided();
        int countGrossNonTaxableTransactionSalesVoided      = financialTotals.getCountGrossNonTaxableTransactionSalesVoided();
        int countGrossTaxExemptTransactionSalesVoided       = financialTotals.getCountGrossTaxExemptTransactionSalesVoided();
        int countGrossTaxableTransactionReturnsVoided       = financialTotals.getCountGrossTaxableTransactionReturnsVoided();
        int countGrossNonTaxableTransactionReturnsVoided    = financialTotals.getCountGrossNonTaxableTransactionReturnsVoided();
        int countGrossTaxExemptTransactionReturnsVoided     = financialTotals.getCountGrossTaxExemptTransactionReturnsVoided();

/*        
        System.out.println("Initial value of trx count:" + totalTransactionsVoided);
        System.out.println("countGrossTaxableTransactionSalesVoided:" + countGrossTaxableTransactionSalesVoided);
        System.out.println("countGrossNonTaxableTransactionSalesVoided:" + countGrossNonTaxableTransactionSalesVoided);
        System.out.println("countGrossTaxExemptTransactionSalesVoided:" + countGrossTaxExemptTransactionSalesVoided);
        System.out.println("countGrossTaxableTransactionReturnsVoided:" + countGrossTaxableTransactionReturnsVoided);
        System.out.println("countGrossNonTaxableTransactionReturnsVoided:" + countGrossNonTaxableTransactionReturnsVoided);
        System.out.println("countGrossTaxExemptTransactionReturnsVoided:" + countGrossTaxExemptTransactionReturnsVoided);
*/        
        
        totalTransactionsVoided +=   countGrossTaxableTransactionSalesVoided
                                   + countGrossNonTaxableTransactionSalesVoided
                                   + countGrossTaxExemptTransactionSalesVoided
                                   + countGrossTaxableTransactionReturnsVoided
                                   + countGrossNonTaxableTransactionReturnsVoided
                                   + countGrossTaxExemptTransactionReturnsVoided;
        
        financialTotalsElement.setTransactionCount(totalTransactionsVoided);
        financialTotalsElement.setCurrencyID(financialTotals.getCurrencyID()); //I18N
        
        financialTotalsElement.setTotalNetSalesAmount(makeAmount(financialTotals.getAmountGrossTaxableItemSales()));
        financialTotalsElement.setUnitsGrossTaxableItemSales(financialTotals.getUnitsGrossTaxableItemSales());
        financialTotalsElement.setAmountGrossNonTaxableItemSales(makeAmount(financialTotals.getAmountGrossNonTaxableItemSales()));
        financialTotalsElement.setUnitsGrossNonTaxableItemSales(financialTotals.getUnitsGrossNonTaxableItemSales());
        financialTotalsElement.setTotalGrossSalesExemptTaxAmount(makeAmount(financialTotals.getAmountGrossTaxExemptItemSales()));
        financialTotalsElement.setUnitsGrossTaxExemptItemSales(financialTotals.getUnitsGrossTaxExemptItemSales());

        financialTotalsElement.setAmountGrossTaxableItemSalesVoided(makeAmount(financialTotals.getAmountGrossTaxableItemSalesVoided()));
        financialTotalsElement.setUnitsGrossTaxableItemSalesVoided(financialTotals.getUnitsGrossTaxableItemSalesVoided());
        financialTotalsElement.setAmountGrossNonTaxableItemSalesVoided(makeAmount(financialTotals.getAmountGrossNonTaxableItemSalesVoided()));
        financialTotalsElement.setUnitsGrossNonTaxableItemSalesVoided(financialTotals.getUnitsGrossNonTaxableItemSalesVoided());
        financialTotalsElement.setAmountGrossTaxExemptItemSalesVoided(makeAmount(financialTotals.getAmountGrossTaxExemptItemSalesVoided()));
        financialTotalsElement.setUnitsGrossTaxExemptItemSalesVoided(financialTotals.getUnitsGrossTaxExemptItemSalesVoided());
           
        financialTotalsElement.setAmountGrossTaxableItemReturns(makeAmount(financialTotals.getAmountGrossTaxableItemReturns()));
        financialTotalsElement.setUnitsGrossTaxableItemReturns(financialTotals.getUnitsGrossTaxableItemReturns());
        financialTotalsElement.setAmountGrossNonTaxableItemReturns(makeAmount(financialTotals.getAmountGrossNonTaxableItemReturns()));
        financialTotalsElement.setUnitsGrossNonTaxableItemReturns(financialTotals.getUnitsGrossNonTaxableItemReturns());
        financialTotalsElement.setAmountGrossTaxExemptItemReturns(makeAmount(financialTotals.getAmountGrossTaxExemptItemReturns()));
        financialTotalsElement.setUnitsGrossTaxExemptItemReturns(financialTotals.getUnitsGrossTaxExemptItemReturns());

        financialTotalsElement.setAmountGrossTaxableItemReturnsVoided(makeAmount(financialTotals.getAmountGrossTaxableItemReturnsVoided()));
        financialTotalsElement.setUnitsGrossTaxableItemReturnsVoided(financialTotals.getUnitsGrossTaxableItemReturnsVoided());
        financialTotalsElement.setAmountGrossNonTaxableItemReturnsVoided(makeAmount(financialTotals.getAmountGrossNonTaxableItemReturnsVoided()));
        financialTotalsElement.setUnitsGrossNonTaxableItemReturnsVoided(financialTotals.getUnitsGrossNonTaxableItemReturnsVoided());
        financialTotalsElement.setAmountGrossTaxExemptItemReturnsVoided(makeAmount(financialTotals.getAmountGrossTaxExemptItemReturnsVoided()));
        financialTotalsElement.setUnitsGrossTaxExemptItemReturnsVoided(financialTotals.getUnitsGrossTaxExemptItemReturnsVoided());

        financialTotalsElement.setTotalTaxAmount(makeAmount(financialTotals.getAmountTaxItemSales()));
        financialTotalsElement.setAmountInclusiveTaxItemSales(makeAmount(financialTotals.getAmountInclusiveTaxItemSales()));
        financialTotalsElement.setAmountTaxItemReturns(makeAmount(financialTotals.getAmountTaxItemReturns()));
        financialTotalsElement.setAmountInclusiveTaxItemReturns(makeAmount(financialTotals.getAmountInclusiveTaxItemReturns()));
        financialTotalsElement.setAmountTaxTransactionSales(makeAmount(financialTotals.getAmountTaxTransactionSales()));
        financialTotalsElement.setAmountInclusiveTaxTransactionSales(makeAmount(financialTotals.getAmountInclusiveTaxTransactionSales()));
        financialTotalsElement.setAmountTaxTransactionReturns(makeAmount(financialTotals.getAmountTaxTransactionReturns()));
        financialTotalsElement.setAmountInclusiveTaxTransactionReturns(makeAmount(financialTotals.getAmountInclusiveTaxTransactionReturns()));
        
        financialTotalsElement.setAmountGrossTaxableTransactionSales(makeAmount(financialTotals.getAmountGrossTaxableTransactionSales()));
        financialTotalsElement.setCountGrossTaxableTransactionSales(financialTotals.getCountGrossTaxableTransactionSales());
        financialTotalsElement.setAmountGrossNonTaxableTransactionSales(makeAmount(financialTotals.getAmountGrossNonTaxableTransactionSales()));
        financialTotalsElement.setCountGrossNonTaxableTransactionSales(financialTotals.getCountGrossNonTaxableTransactionSales());
        financialTotalsElement.setAmountGrossTaxExemptTransactionSales(makeAmount(financialTotals.getAmountGrossTaxExemptTransactionSales()));
        financialTotalsElement.setCountGrossTaxExemptTransactionSales(financialTotals.getCountGrossTaxExemptTransactionSales());

        financialTotalsElement.setAmountGrossTaxableTransactionSalesVoided(makeAmount(financialTotals.getAmountGrossTaxableTransactionSalesVoided()));
        financialTotalsElement.setCountGrossTaxableTransactionSalesVoided(financialTotals.getCountGrossTaxableTransactionSalesVoided());
        financialTotalsElement.setAmountGrossNonTaxableTransactionSalesVoided(makeAmount(financialTotals.getAmountGrossNonTaxableTransactionSalesVoided()));
        financialTotalsElement.setCountGrossNonTaxableTransactionSalesVoided(financialTotals.getCountGrossNonTaxableTransactionSalesVoided());
        financialTotalsElement.setAmountGrossTaxExemptTransactionSalesVoided(makeAmount(financialTotals.getAmountGrossTaxExemptTransactionSalesVoided()));
        financialTotalsElement.setCountGrossTaxExemptTransactionSalesVoided(financialTotals.getCountGrossTaxExemptTransactionSalesVoided());

        financialTotalsElement.setAmountGrossTaxableTransactionReturns(makeAmount(financialTotals.getAmountGrossTaxableTransactionReturns()));
        financialTotalsElement.setCountGrossTaxableTransactionReturns(financialTotals.getCountGrossTaxableTransactionReturns());
        financialTotalsElement.setAmountGrossNonTaxableTransactionReturns(makeAmount(financialTotals.getAmountGrossNonTaxableTransactionReturns()));
        financialTotalsElement.setCountGrossNonTaxableTransactionReturns(financialTotals.getCountGrossNonTaxableTransactionReturns());
        financialTotalsElement.setAmountGrossTaxExemptTransactionReturns(makeAmount(financialTotals.getAmountGrossTaxExemptTransactionReturns()));
        financialTotalsElement.setCountGrossTaxExemptTransactionReturns(financialTotals.getCountGrossTaxExemptTransactionReturns());

        financialTotalsElement.setAmountGrossTaxableTransactionReturnsVoided(makeAmount(financialTotals.getAmountGrossTaxableTransactionReturnsVoided()));
        financialTotalsElement.setCountGrossTaxableTransactionReturnsVoided(financialTotals.getCountGrossTaxableTransactionReturnsVoided());
        financialTotalsElement.setAmountGrossNonTaxableTransactionReturnsVoided(makeAmount(financialTotals.getAmountGrossNonTaxableTransactionReturnsVoided()));
        financialTotalsElement.setCountGrossNonTaxableTransactionReturnsVoided(financialTotals.getCountGrossNonTaxableTransactionReturnsVoided());
        financialTotalsElement.setAmountGrossTaxExemptTransactionReturnsVoided(makeAmount(financialTotals.getAmountGrossTaxExemptTransactionReturnsVoided()));
        financialTotalsElement.setCountGrossTaxExemptTransactionReturnsVoided(financialTotals.getCountGrossTaxExemptTransactionReturnsVoided());

        financialTotalsElement.setAmountGiftCertificateSales(makeAmount(financialTotals.getAmountGiftCertificateSales()));
        financialTotalsElement.setUnitsGiftCertificateSales(financialTotals.getUnitsGiftCertificateSales());

        financialTotalsElement.setPaymentsCollected(makePaymentsCollected(financialTotals.getAmountHousePayments(), Integer.toString(financialTotals.getCountHousePayments())));

        financialTotalsElement.setAmountItemDiscounts(makeAmount(financialTotals.getAmountItemDiscounts()));
        financialTotalsElement.setNumberItemDiscounts(financialTotals.getNumberItemDiscounts());

        financialTotalsElement.setMarkdowns(makeTotal(financialTotals.getAmountItemMarkdowns(), Integer.toString(financialTotals.getNumberItemMarkdowns())));

        financialTotalsElement.setAmountRestockingFees(makeAmount(financialTotals.getAmountRestockingFees()));
        financialTotalsElement.setUnitsRestockingFees(financialTotals.getUnitsRestockingFees());
        financialTotalsElement.setAmountRestockingFeesForNonTaxable(makeAmount(financialTotals.getAmountRestockingFeesFromNonTaxableItems()));
        financialTotalsElement.setUnitsRestockingFeesForNonTaxable(financialTotals.getUnitsRestockingFeesFromNonTaxableItems());

        financialTotalsElement.setAmountShippingCharges(makeAmount(financialTotals.getAmountShippingCharges()));
        financialTotalsElement.setNumberShippingCharges(financialTotals.getNumberShippingCharges());
        financialTotalsElement.setAmountTaxShippingCharges(makeAmount(financialTotals.getAmountTaxShippingCharges()));
        financialTotalsElement.setAmountInclusiveTaxShippingCharges(makeAmount(financialTotals.getAmountInclusiveTaxShippingCharges()));

        financialTotalsElement.setAmountItemDiscStoreCoupons(makeAmount(financialTotals.getAmountItemDiscStoreCoupons()));
        financialTotalsElement.setNumberItemDiscStoreCoupons(financialTotals.getNumberItemDiscStoreCoupons());

        financialTotalsElement.setAmountTransactionDiscStoreCoupons(makeAmount(financialTotals.getAmountTransactionDiscStoreCoupons()));
        financialTotalsElement.setNumberTransactionDiscStoreCoupons(financialTotals.getNumberTransactionDiscStoreCoupons());

        
        financialTotalsElement.setPostTransactionVoids(makeTotal(financialTotals.getAmountPostVoids(), Integer.toString(financialTotals.getNumberPostVoids())));

        //financialTotalsElement.setNumberNoSales(financialTotals.getNumberNoSales());
           
        financialTotalsElement.setLineItemVoids(makeTotal(financialTotals.getAmountLineVoids(), financialTotals.getUnitsLineVoids().toBigInteger().toString()));  // to strip decimals

        financialTotalsElement.setTransactionVoids(makeTotal(financialTotals.getAmountCancelledTransactions(), Integer.toString(financialTotals.getNumberCancelledTransactions())));

        financialTotalsElement.setAmountGrossTaxableNonMerchandiseSales(makeAmount(financialTotals.getAmountGrossTaxableNonMerchandiseSales()));
        financialTotalsElement.setUnitsGrossTaxableNonMerchandiseSales(financialTotals.getUnitsGrossTaxableNonMerchandiseSales());
        financialTotalsElement.setAmountGrossNonTaxableNonMerchandiseSales(makeAmount(financialTotals.getAmountGrossNonTaxableNonMerchandiseSales()));
        financialTotalsElement.setUnitsGrossNonTaxableNonMerchandiseSales(financialTotals.getUnitsGrossNonTaxableNonMerchandiseSales());
           
        financialTotalsElement.setAmountGrossTaxableNonMerchandiseSalesVoided(makeAmount(financialTotals.getAmountGrossTaxableNonMerchandiseSalesVoided()));
        financialTotalsElement.setUnitsGrossTaxableNonMerchandiseSalesVoided(financialTotals.getUnitsGrossTaxableNonMerchandiseSalesVoided());
        financialTotalsElement.setAmountGrossNonTaxableNonMerchandiseSalesVoided(makeAmount(financialTotals.getAmountGrossNonTaxableNonMerchandiseSalesVoided()));
        financialTotalsElement.setUnitsGrossNonTaxableNonMerchandiseSalesVoided(financialTotals.getUnitsGrossNonTaxableNonMerchandiseSalesVoided());

        financialTotalsElement.setAmountGrossTaxableNonMerchandiseReturns(makeAmount(financialTotals.getAmountGrossTaxableNonMerchandiseReturns()));
        financialTotalsElement.setUnitsGrossTaxableNonMerchandiseReturns(financialTotals.getUnitsGrossTaxableNonMerchandiseReturns());
        financialTotalsElement.setAmountGrossNonTaxableNonMerchandiseReturns(makeAmount(financialTotals.getAmountGrossNonTaxableNonMerchandiseReturns()));
        financialTotalsElement.setUnitsGrossNonTaxableNonMerchandiseReturns(financialTotals.getUnitsGrossNonTaxableNonMerchandiseReturns());

        financialTotalsElement.setAmountGrossTaxableNonMerchandiseReturnsVoided(makeAmount(financialTotals.getAmountGrossTaxableNonMerchandiseReturnsVoided()));
        financialTotalsElement.setUnitsGrossTaxableNonMerchandiseReturnsVoided(financialTotals.getUnitsGrossTaxableNonMerchandiseReturnsVoided());
        financialTotalsElement.setAmountGrossNonTaxableNonMerchandiseReturnsVoided(makeAmount(financialTotals.getAmountGrossNonTaxableNonMerchandiseReturnsVoided()));
        financialTotalsElement.setUnitsGrossNonTaxableNonMerchandiseReturnsVoided(financialTotals.getUnitsGrossNonTaxableNonMerchandiseReturnsVoided());

        financialTotalsElement.setAmountGrossGiftCardItemSales(makeAmount(financialTotals.getAmountGrossGiftCardItemSales()));
        financialTotalsElement.setUnitsGrossGiftCardItemSales(financialTotals.getUnitsGrossGiftCardItemSales());

        financialTotalsElement.setAmountGrossGiftCardItemSalesVoided(makeAmount(financialTotals.getAmountGrossGiftCardItemSalesVoided()));
        financialTotalsElement.setUnitsGrossGiftCardItemSalesVoided(financialTotals.getUnitsGrossGiftCardItemSalesVoided());

        financialTotalsElement.setAmountGrossGiftCardItemReturns(makeAmount(financialTotals.getAmountGrossGiftCardItemReturns()));
        financialTotalsElement.setUnitsGrossGiftCardItemReturns(financialTotals.getUnitsGrossGiftCardItemReturns());

        financialTotalsElement.setAmountGrossGiftCardItemReturnsVoided(makeAmount(financialTotals.getAmountGrossGiftCardItemReturnsVoided()));
        financialTotalsElement.setUnitsGrossGiftCardItemReturnsVoided(financialTotals.getUnitsGrossGiftCardItemReturnsVoided());

        financialTotalsElement.setTenderPickup(makeTenderPickup(financialTotals.getAmountTillPayIns(), financialTotals.getCountTillPayIns()));

        makePettyCashDisbursment(financialTotals.getAmountTillPayOuts(), Integer.toString(financialTotals.getCountTillPayOuts()));

        financialTotalsElement.setAmountLayawayPayments(makeAmount(financialTotals.getAmountLayawayPayments()));
        financialTotalsElement.setCountLayawayPayments(financialTotals.getCountLayawayPayments());

        financialTotalsElement.setAmountLayawayDeletions(makeAmount(financialTotals.getAmountLayawayDeletions()));
        financialTotalsElement.setCountLayawayDeletions(financialTotals.getCountLayawayDeletions());

        financialTotalsElement.setAmountLayawayInitiationFees(makeAmount(financialTotals.getAmountLayawayInitiationFees()));
        financialTotalsElement.setCountLayawayInitiationFees(financialTotals.getCountLayawayInitiationFees());

        financialTotalsElement.setAmountLayawayDeletionFees(makeAmount(financialTotals.getAmountLayawayDeletionFees()));
        financialTotalsElement.setCountLayawayDeletionFees(financialTotals.getCountLayawayDeletionFees());

        financialTotalsElement.setAmountOrderPayments(makeAmount(financialTotals.getAmountOrderPayments()));
        financialTotalsElement.setCountOrderPayments(financialTotals.getCountOrderPayments());

        financialTotalsElement.setAmountOrderCancels(makeAmount(financialTotals.getAmountOrderCancels()));
        financialTotalsElement.setCountOrderCancels(financialTotals.getCountOrderCancels());
        
/*        
        
      createTextNodeElement
        (IXRetailConstantsIfc.ELEMENT_TRANSACTIONS_WITH_RETURNED_ITEMS_COUNT,
        financialTotals.getTransactionsWithReturnedItemsCount(),
        financialTotalsElement);
      
      createTextNodeElement
        (IXRetailConstantsIfc.ELEMENT_AMOUNT_GROSS_GIFT_CARD_ITEM_ISSUED,
        financialTotals.getAmountGrossGiftCardItemIssued(),
        financialTotalsElement);

      createTextNodeElement
        (IXRetailConstantsIfc.ELEMENT_UNITS_GROSS_GIFT_CARD_ITEM_ISSUED,
        financialTotals.getUnitsGrossGiftCardItemIssued(),
        financialTotalsElement);

      createTextNodeElement
        (IXRetailConstantsIfc.ELEMENT_AMOUNT_GROSS_GIFT_CARD_ITEM_RELOADED,
        financialTotals.getAmountGrossGiftCardItemReloaded(),
        financialTotalsElement);

      createTextNodeElement
        (IXRetailConstantsIfc.ELEMENT_UNITS_GROSS_GIFT_CARD_ITEM_RELOADED,
        financialTotals.getUnitsGrossGiftCardItemReloaded(),
        financialTotalsElement);

      createTextNodeElement
        (IXRetailConstantsIfc.ELEMENT_AMOUNT_GROSS_GIFT_CARD_ITEM_REDEEMED,
        financialTotals.getAmountGrossGiftCardItemRedeemed(),
        financialTotalsElement);

      createTextNodeElement
        (IXRetailConstantsIfc.ELEMENT_UNITS_GROSS_GIFT_CARD_ITEM_REDEEMED,
        financialTotals.getUnitsGrossGiftCardItemRedeemed(),
        financialTotalsElement);

      createTextNodeElement
        (IXRetailConstantsIfc.ELEMENT_AMOUNT_GROSS_GIFT_CARD_ITEM_ISSUE_VOIDED,
        financialTotals.getAmountGrossGiftCardItemIssueVoided(),
        financialTotalsElement);

      createTextNodeElement
        (IXRetailConstantsIfc.ELEMENT_UNITS_GROSS_GIFT_CARD_ITEM_ISSUE_VOIDED,
        financialTotals.getUnitsGrossGiftCardItemIssueVoided(),
        financialTotalsElement);

      createTextNodeElement
        (IXRetailConstantsIfc.ELEMENT_AMOUNT_GROSS_GIFT_CARD_ITEM_RELOAD_VOIDED,
        financialTotals.getAmountGrossGiftCardItemReloadVoided(),
        financialTotalsElement);

      createTextNodeElement
        (IXRetailConstantsIfc.ELEMENT_UNITS_GROSS_GIFT_CARD_ITEM_RELOAD_VOIDED,
        financialTotals.getUnitsGrossGiftCardItemReloadVoided(),
        financialTotalsElement);

      createTextNodeElement
        (IXRetailConstantsIfc.ELEMENT_AMOUNT_GROSS_GIFT_CARD_ITEM_REDEEMED_VOIDED,
        financialTotals.getAmountGrossGiftCardItemRedeemedVoided(),
        financialTotalsElement);

      createTextNodeElement
        (IXRetailConstantsIfc.ELEMENT_UNITS_GROSS_GIFT_CARD_ITEM_REDEEMED_VOIDED,
        financialTotals.getUnitsGrossGiftCardItemRedeemedVoided(),
        financialTotalsElement);

      createTextNodeElement
        (IXRetailConstantsIfc.ELEMENT_AMOUNT_EMPLOYEE_DISCOUNTS,
        financialTotals.getAmountEmployeeDiscounts(),
        financialTotalsElement);

      createTextNodeElement
        (IXRetailConstantsIfc.ELEMENT_UNITS_EMPLOYEE_DISCOUNTS,
        financialTotals.getUnitsEmployeeDiscounts(),
        financialTotalsElement);

      createTextNodeElement
        (IXRetailConstantsIfc.ELEMENT_AMOUNT_CUSTOMER_DISCOUNTS,
        financialTotals.getAmountCustomerDiscounts(),
        financialTotalsElement);

      createTextNodeElement
        (IXRetailConstantsIfc.ELEMENT_UNITS_CUSTOMER_DISCOUNTS,
        financialTotals.getUnitsCustomerDiscounts(),
        financialTotalsElement);

      createTextNodeElement
        (IXRetailConstantsIfc.ELEMENT_AMOUNT_PRICE_OVERRIDES,
        financialTotals.getAmountPriceOverrides(),
        financialTotalsElement);

      createTextNodeElement
        (IXRetailConstantsIfc.ELEMENT_UNITS_PRICE_OVERRIDES,
        financialTotals.getUnitsPriceOverrides(),
        financialTotalsElement);

      createTextNodeElement
        (IXRetailConstantsIfc.ELEMENT_AMOUNT_GROSS_GIFT_CERTIFICATE_ISSUED,
        financialTotals.getAmountGrossGiftCertificateIssued(),
        financialTotalsElement);

      createTextNodeElement
        (IXRetailConstantsIfc.ELEMENT_UNITS_GROSS_GIFT_CERTIFICATE_ISSUED,
        financialTotals.getUnitsGrossGiftCertificateIssued(),
        financialTotalsElement);

      createTextNodeElement
        (IXRetailConstantsIfc.ELEMENT_AMOUNT_GROSS_GIFT_CERTIFICATE_ISSUED_VOIDED,
        financialTotals.getAmountGrossGiftCertificateIssuedVoided(),
        financialTotalsElement);

      createTextNodeElement
        (IXRetailConstantsIfc.ELEMENT_UNITS_GROSS_GIFT_CERTIFICATE_ISSUED_VOIDED,
        financialTotals.getUnitsGrossGiftCertificateIssuedVoided(),
        financialTotalsElement);

      createTextNodeElement
        (IXRetailConstantsIfc.ELEMENT_AMOUNT_GROSS_GIFT_CERTIFICATE_TENDERED,
        financialTotals.getAmountGrossGiftCertificateTendered(),
        financialTotalsElement);

      createTextNodeElement
        (IXRetailConstantsIfc.ELEMENT_UNITS_GROSS_GIFT_CERTIFICATE_TENDERED,
        financialTotals.getUnitsGrossGiftCertificateTendered(),
        financialTotalsElement);

      createTextNodeElement
        (IXRetailConstantsIfc.ELEMENT_AMOUNT_GROSS_GIFT_CERTIFICATE_TENDERED_VOIDED,
        financialTotals.getAmountGrossGiftCertificateTenderedVoided(),
        financialTotalsElement);

      createTextNodeElement
        (IXRetailConstantsIfc.ELEMENT_UNITS_GROSS_GIFT_CERTIFICATE_TENDERED_VOIDED,
        financialTotals.getUnitsGrossGiftCertificateTenderedVoided(),
        financialTotalsElement);

      createTextNodeElement
        (IXRetailConstantsIfc.ELEMENT_HOUSE_ENROLLMENTS_APPROVED,
        financialTotals.getHouseCardEnrollmentsApproved(),
        financialTotalsElement);

      createTextNodeElement
        (IXRetailConstantsIfc.ELEMENT_HOUSE_ENROLLMENTS_DECLINED,
        financialTotals.getHouseCardEnrollmentsDeclined(),
        financialTotalsElement);

      createTextNodeElement
        (IXRetailConstantsIfc.ELEMENT_AMOUNT_CANADIAN_GST,
        financialTotals.getAmountCanadianGST(),
        financialTotalsElement);

      createTextNodeElement
        (IXRetailConstantsIfc.ELEMENT_UNITS_CANADIAN_GST,
        financialTotals.getUnitsCanadianGST(),
        financialTotalsElement);

      createTextNodeElement
        (IXRetailConstantsIfc.ELEMENT_AMOUNT_CANADIAN_PST,
        financialTotals.getAmountCanadianPST(),
        financialTotalsElement);

      createTextNodeElement
        (IXRetailConstantsIfc.ELEMENT_UNITS_CANADIAN_PST,
        financialTotals.getUnitsCanadianPST(),
        financialTotalsElement);

      createTextNodeElement
        (IXRetailConstantsIfc.ELEMENT_AMOUNT_CANADIAN_HST,
        financialTotals.getAmountCanadianHST(),
        financialTotalsElement);

      createTextNodeElement
        (IXRetailConstantsIfc.ELEMENT_UNITS_CANADIAN_HST,
        financialTotals.getUnitsCanadianHST(),
        financialTotalsElement);

      createTextNodeElement
        (IXRetailConstantsIfc.ELEMENT_AMOUNT_TAX_JURISDICTION_ONE,
        financialTotals.getAmountTaxJurisdictionOne(),
        financialTotalsElement);

      createTextNodeElement
        (IXRetailConstantsIfc.ELEMENT_UNITS_TAX_JURISDICTION_ONE,
        financialTotals.getUnitsTaxJurisdictionOne(),
        financialTotalsElement);

      createTextNodeElement
        (IXRetailConstantsIfc.ELEMENT_AMOUNT_TAX_JURISDICTION_TWO,
        financialTotals.getAmountTaxJurisdictionTwo(),
        financialTotalsElement);

      createTextNodeElement
        (IXRetailConstantsIfc.ELEMENT_UNITS_TAX_JURISDICTION_TWO,
        financialTotals.getUnitsTaxJurisdictionTwo(),
        financialTotalsElement);

      createTextNodeElement
        (IXRetailConstantsIfc.ELEMENT_AMOUNT_TAX_JURISDICTION_THREE,
        financialTotals.getAmountTaxJurisdictionThree(),
        financialTotalsElement);

      createTextNodeElement
        (IXRetailConstantsIfc.ELEMENT_UNITS_TAX_JURISDICTION_THREE,
        financialTotals.getUnitsTaxJurisdictionThree(),
        financialTotalsElement);

      createTextNodeElement
        (IXRetailConstantsIfc.ELEMENT_AMOUNT_TAX_JURISDICTION_FOUR,
        financialTotals.getAmountTaxJurisdictionFour(),
        financialTotalsElement);

      createTextNodeElement
        (IXRetailConstantsIfc.ELEMENT_UNITS_TAX_JURISDICTION_FOUR,
        financialTotals.getUnitsTaxJurisdictionFour(),
        financialTotalsElement);

      createTextNodeElement
        (IXRetailConstantsIfc.ELEMENT_AMOUNT_GROSS_GIFT_CARD_ITEM_CREDIT,
        financialTotals.getAmountGrossGiftCardItemCredit(),
        financialTotalsElement);

      createTextNodeElement
        (IXRetailConstantsIfc.ELEMENT_UNITS_GROSS_GIFT_CARD_ITEM_CREDIT,
        financialTotals.getUnitsGrossGiftCardItemCredit(),
        financialTotalsElement);

      createTextNodeElement
        (IXRetailConstantsIfc.ELEMENT_AMOUNT_GROSS_GIFT_CARD_ITEM_CREDIT_VOIDED,
        financialTotals.getAmountGrossGiftCardItemCreditVoided(),
        financialTotalsElement);

      createTextNodeElement
        (IXRetailConstantsIfc.ELEMENT_UNITS_GROSS_GIFT_CARD_ITEM_CREDIT_VOIDED,
        financialTotals.getUnitsGrossGiftCardItemCreditVoided(),
        financialTotalsElement);

      createTextNodeElement
        (IXRetailConstantsIfc.ELEMENT_AMOUNT_GROSS_GIFT_CERTIFICATES_REDEEMED,
        financialTotals.getAmountGrossGiftCertificatesRedeemed(),
        financialTotalsElement);

      createTextNodeElement
        (IXRetailConstantsIfc.ELEMENT_UNITS_GROSS_GIFT_CERTIFICATES_REDEEMED,
        financialTotals.getUnitsGrossGiftCertificatesRedeemed(),
        financialTotalsElement);

      createTextNodeElement
        (IXRetailConstantsIfc.ELEMENT_AMOUNT_GROSS_GIFT_CERTIFICATES_REDEEMED_VOIDED,
        financialTotals.getAmountGrossGiftCertificatesRedeemedVoided(),
        financialTotalsElement);

      createTextNodeElement
        (IXRetailConstantsIfc.ELEMENT_UNITS_GROSS_GIFT_CERTIFICATES_REDEEMED_VOIDED,
        financialTotals.getUnitsGrossGiftCertificatesRedeemedVoided(),
        financialTotalsElement);
      
      createTextNodeElement
        (IXRetailConstantsIfc.ELEMENT_AMOUNT_GROSS_STORE_CREDITS_ISSUED,
        financialTotals.getAmountGrossStoreCreditsIssued(),
        financialTotalsElement);
      
      createTextNodeElement
        (IXRetailConstantsIfc.ELEMENT_UNITS_GROSS_STORE_CREDITS_ISSUED,
        financialTotals.getUnitsGrossStoreCreditsIssued(),
        financialTotalsElement);
      
      createTextNodeElement
        (IXRetailConstantsIfc.ELEMENT_AMOUNT_GROSS_STORE_CREDITS_ISSUED_VOIDED,
        financialTotals.getAmountGrossStoreCreditsIssuedVoided(),
        financialTotalsElement);
      
      createTextNodeElement
        (IXRetailConstantsIfc.ELEMENT_UNITS_GROSS_STORE_CREDITS_ISSUED_VOIDED,
        financialTotals.getUnitsGrossStoreCreditsIssuedVoided(),
        financialTotalsElement);
      
      
      createTextNodeElement
        (IXRetailConstantsIfc.ELEMENT_AMOUNT_GROSS_STORE_CREDITS_REDEEMED,
        financialTotals.getAmountGrossStoreCreditsRedeemed(),
        financialTotalsElement);
      
      createTextNodeElement
        (IXRetailConstantsIfc.ELEMENT_UNITS_GROSS_STORE_CREDITS_REDEEMED,
        financialTotals.getUnitsGrossStoreCreditsRedeemed(),
        financialTotalsElement);

      createTextNodeElement
        (IXRetailConstantsIfc.ELEMENT_AMOUNT_GROSS_STORE_CREDITS_REDEEMED_VOIDED,
        financialTotals.getAmountGrossStoreCreditsRedeemedVoided(),
        financialTotalsElement);

      createTextNodeElement
        (IXRetailConstantsIfc.ELEMENT_UNITS_GROSS_STORE_CREDITS_REDEEMED_VOIDED,
        financialTotals.getUnitsGrossStoreCreditsRedeemedVoided(),
        financialTotalsElement);

      createTextNodeElement
        (IXRetailConstantsIfc.ELEMENT_AMOUNT_GROSS_ITEM_EMPLOYEE_DISCOUNT,
        financialTotals.getAmountGrossItemEmployeeDiscount(),
        financialTotalsElement);

      createTextNodeElement
        (IXRetailConstantsIfc.ELEMENT_UNITS_GROSS_ITEM_EMPLOYEE_DISCOUNT,
        financialTotals.getUnitsGrossItemEmployeeDiscount(),
        financialTotalsElement);

      createTextNodeElement
        (IXRetailConstantsIfc.ELEMENT_AMOUNT_GROSS_ITEM_EMPLOYEE_DISCOUNT_VOIDED,
        financialTotals.getAmountGrossItemEmployeeDiscountVoided(),
        financialTotalsElement);

      createTextNodeElement
        (IXRetailConstantsIfc.ELEMENT_UNITS_GROSS_ITEM_EMPLOYEE_DISCOUNT_VOIDED,
        financialTotals.getUnitsGrossItemEmployeeDiscountVoided(),
        financialTotalsElement);

      createTextNodeElement
        (IXRetailConstantsIfc.ELEMENT_AMOUNT_GROSS_TRANSACTION_EMPLOYEE_DISCOUNT,
        financialTotals.getAmountGrossTransactionEmployeeDiscount(),
        financialTotalsElement);

      createTextNodeElement
        (IXRetailConstantsIfc.ELEMENT_UNITS_GROSS_TRANSACTION_EMPLOYEE_DISCOUNT,
        financialTotals.getUnitsGrossTransactionEmployeeDiscount(),
        financialTotalsElement);

      createTextNodeElement
        (IXRetailConstantsIfc.ELEMENT_AMOUNT_GROSS_TRANSACTION_EMPLOYEE_DISCOUNT_VOIDED,
        financialTotals.getAmountGrossTransactionEmployeeDiscountVoided(),
        financialTotalsElement);

      createTextNodeElement
        (IXRetailConstantsIfc.ELEMENT_UNITS_GROSS_TRANSACTION_EMPLOYEE_DISCOUNT_VOIDED,
        financialTotals.getUnitsGrossTransactionEmployeeDiscountVoided(),
        financialTotalsElement);

      createTextNodeElement
        (IXRetailConstantsIfc.ELEMENT_AMOUNT_GROSS_TRANSACTION_DISCOUNT,
        financialTotals.getAmountGrossTransactionDiscount(),
        financialTotalsElement);

      createTextNodeElement
        (IXRetailConstantsIfc.ELEMENT_UNITS_GROSS_TRANSACTION_DISCOUNT,
        financialTotals.getUnitsGrossTransactionDiscount(),
        financialTotalsElement);

      createTextNodeElement
        (IXRetailConstantsIfc.ELEMENT_AMOUNT_GROSS_TRANSACTION_DISCOUNT_VOIDED,
        financialTotals.getAmountGrossTransactionDiscountVoided(),
        financialTotalsElement);

      createTextNodeElement
        (IXRetailConstantsIfc.ELEMENT_UNITS_GROSS_TRANSACTION_DISCOUNT_VOIDED,
        financialTotals.getUnitsGrossTransactionDiscountVoided(),

        
*/        

        financialTotalsElement.setAmountTransactionDiscounts(makeAmount(financialTotals.getAmountTransactionDiscounts()));
        financialTotalsElement.setNumberTransactionDiscounts(financialTotals.getNumberTransactionDiscounts());
        
        financialTotalsElement.setEmployeeDiscounts(
                makeTotal(financialTotals.getAmountGrossItemEmployeeDiscount(), 
                          financialTotals.getUnitsGrossItemEmployeeDiscount().toString()));

        financialTotalsElement.setEmployeeDiscountsVoided(
                makeTotal(financialTotals.getAmountGrossItemEmployeeDiscountVoided(), 
                          financialTotals.getUnitsGrossItemEmployeeDiscountVoided().toString()));

        financialTotalsElement.setEmployeeTransactionDiscounts(
                makeTotal(financialTotals.getAmountGrossTransactionEmployeeDiscount(), 
                          financialTotals.getUnitsGrossTransactionEmployeeDiscount().toString()));

        financialTotalsElement.setEmployeeTransactionDiscountsVoided(
                makeTotal(financialTotals.getAmountGrossTransactionEmployeeDiscountVoided(), 
                          financialTotals.getUnitsGrossTransactionEmployeeDiscountVoided().toString()));
        
        financialTotalsElement.setTillPriceOverrides(
                makeTotal(financialTotals.getAmountPriceOverrides(), 
                          financialTotals.getUnitsPriceOverrides().toString()));
        
    }

}
