/* ===========================================================================
* Copyright (c) 1998, 2011, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/modules/domain/src/oracle/retail/stores/domain/ixretail/financial/LogFinancialTotals.java /rgbustores_13.4x_generic_branch/1 2011/05/04 11:49:07 mszekely Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    cgreene   05/26/10 - convert to oracle packaging
 *    cgreene   04/28/10 - updating deprecated names
 *    abondala  01/03/10 - update header date
 *
 * ===========================================================================
 * $Log:
 *    3    360Commerce 1.2         3/31/2005 4:28:54 PM   Robert Pearse   
 *    2    360Commerce 1.1         3/10/2005 10:23:11 AM  Robert Pearse   
 *    1    360Commerce 1.0         2/11/2005 12:12:23 PM  Robert Pearse   
 *
 *   Revision 1.8  2004/06/15 00:44:32  jdeleau
 *   @scr 2775 Support register reports and financial totals with the new
 *   tax engine.
 *
 *   Revision 1.7  2004/05/12 15:03:58  jdeleau
 *   @scr 4218 Remove GrossTransactionDiscount Amounts, Units, UnitsVoid, 
 *   and AmountVoids in favor of the already existing AmountTransactionDiscounts
 *   and NumberTransactionDiscounts, which end up already being NET totals.
 *
 *   Revision 1.6  2004/05/11 23:03:02  jdeleau
 *   @scr 4218 Backout recent changes to remove TransactionDiscounts,
 *   going to go a different route and remove the newly added
 *   voids and grosses instead.
 *
 *   Revision 1.4  2004/04/28 19:56:21  jdeleau
 *   @scr 4218 POS Log changes for new items in financial totals
 *
 *   Revision 1.3  2004/02/12 17:13:42  mcs
 *   Forcing head revision
 *
 *   Revision 1.2  2004/02/11 23:25:28  bwf
 *   @scr 0 Organize imports.
 *
 *   Revision 1.1.1.1  2004/02/11 01:04:31  cschellenger
 *   updating to pvcs 360store-current
 *
 *
 * 
 *    Rev 1.0   Aug 29 2003 15:36:16   CSchellenger
 * Initial revision.
 * 
 *    Rev 1.2   Jul 01 2003 14:09:28   jgs
 * Modifications for new 6.0 data.
 * Resolution for 1157: Add task for Importing IX Retail Transactions.
 * 
 *    Rev 1.1   Jan 22 2003 09:57:24   mpm
 * Preliminary merging of 5.1/5.5 code.
 * Resolution for Domain SCR-104: Merge 5.1/5.5 into 6.0
 * 
 *    Rev 1.0   Sep 05 2002 11:12:42   msg
 * Initial revision.
 * 
 *    Rev 1.2   May 09 2002 18:27:02   mpm
 * Completed re-factoring of store open/close transaction.
 * Resolution for Domain SCR-45: TLog facility
 *
 *    Rev 1.1   May 02 2002 17:29:06   mpm
 * Completed financial totals.
 * Resolution for Domain SCR-45: TLog facility
 *
 *    Rev 1.0   May 01 2002 18:13:14   mpm
 * Initial revision.
 * Resolution for Domain SCR-45: TLog facility
 * ===========================================================================
 */
package oracle.retail.stores.domain.ixretail.financial;
// XML imports
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import oracle.retail.stores.domain.financial.FinancialTotalsIfc;
import oracle.retail.stores.domain.financial.ReconcilableCountIfc;
import oracle.retail.stores.domain.ixretail.IXRetailConstantsIfc;
import oracle.retail.stores.domain.ixretail.IXRetailGateway;
import oracle.retail.stores.domain.ixretail.log.AbstractIXRetailTranslator;
import oracle.retail.stores.foundation.utility.xml.XMLConversionException;

//--------------------------------------------------------------------------
/**
    This class creates the elements for a FinancialTotals. <P>
    @version $Revision: /rgbustores_13.4x_generic_branch/1 $
**/
//--------------------------------------------------------------------------
public class LogFinancialTotals
extends AbstractIXRetailTranslator
implements LogFinancialTotalsIfc
{
    /**
       revision number supplied by source-code-control system
    **/
    public static String revisionNumber = "$Revision: /rgbustores_13.4x_generic_branch/1 $";

    //----------------------------------------------------------------------------
    /**
        Constructs LogFinancialTotals object. <P>
    **/
    //----------------------------------------------------------------------------
    public LogFinancialTotals()
    {                                   // begin LogFinancialTotals()
    }                                   // end LogFinancialTotals()

    //---------------------------------------------------------------------
    /**
       Creates element for the specified financial totals object. <P>
       @param financialTotals financial totals reference
       @param doc parent document
       @param el parent element
       @param nodeName node name
       @return Element representing quantity
       @exception XMLConversionException thrown if error occurs
    **/
    //---------------------------------------------------------------------
    public Element createElement(FinancialTotalsIfc financialTotals,
                                 Document doc,
                                 Element el,
                                 String nodeName)
    throws XMLConversionException
    {                                   // begin createElement()
        setParentDocument(doc);
        setParentElement(el);

        Element financialTotalsElement = parentDocument.createElement(nodeName);

        createSimpleTypeElements(financialTotals,
                                 financialTotalsElement);

        createCountElements(financialTotals,
                            financialTotalsElement);

        parentElement.appendChild(financialTotalsElement);

        return(financialTotalsElement);
    }                                   // end createElement()

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
    {                                   // begin createElement()
        return(createElement(financialTotals,
                             doc,
                             el,
                             IXRetailConstantsIfc.ELEMENT_FINANCIAL_TOTALS));
    }                                   // end createElement()

    //---------------------------------------------------------------------
    /**
       Creates elements for counts. <P>
       @param financialTotals financial totals object
       @param financialTotalsElement financial totals elements
       @throws XMLConversionException thrown if error occurs
    **/
    //---------------------------------------------------------------------
    protected void createCountElements(FinancialTotalsIfc financialTotals,
                                       Element financialTotalsElement)
    throws XMLConversionException
    {                                   // begin createCountElements()
        createReconcilableCountElement(financialTotals.getStartingFloatCount(),
                                       financialTotalsElement,
                                       IXRetailConstantsIfc.ELEMENT_STARTING_FLOAT_COUNT);

        createReconcilableCountElement(financialTotals.getEndingFloatCount(),
                                       financialTotalsElement,
                                       IXRetailConstantsIfc.ELEMENT_ENDING_FLOAT_COUNT);

        createReconcilableCountElement(financialTotals.getStartingSafeCount(),
                                       financialTotalsElement,
                                       IXRetailConstantsIfc.ELEMENT_STARTING_SAFE_COUNT);

        createReconcilableCountElement(financialTotals.getEndingSafeCount(),
                                       financialTotalsElement,
                                       IXRetailConstantsIfc.ELEMENT_ENDING_SAFE_COUNT);

        createReconcilableCountElement(financialTotals.getCombinedCount(),
                                       financialTotalsElement,
                                       IXRetailConstantsIfc.ELEMENT_COMBINED_COUNT);

        if (financialTotals.getTenderCount() != null)
        {
            LogFinancialCountIfc logCount =
              IXRetailGateway.getFactory().getLogFinancialCountInstance();

            logCount.createElement(financialTotals.getTenderCount(),
                                   parentDocument,
                                   financialTotalsElement,
                                   IXRetailConstantsIfc.ELEMENT_TENDER_COUNT);
        }

        createArrayReconcilableCountElements(financialTotals.getTillPickups(),
                                             financialTotalsElement,
                                             IXRetailConstantsIfc.ELEMENT_TILL_PICKUPS_LIST);

        createTextNodeElement
          (IXRetailConstantsIfc.ELEMENT_COUNT_TILL_PICKUPS,
           financialTotals.getCountTillPickups(),
           financialTotalsElement);

        createArrayReconcilableCountElements(financialTotals.getTillLoans(),
                                             financialTotalsElement,
                                             IXRetailConstantsIfc.ELEMENT_TILL_LOANS_LIST);

        createTextNodeElement
          (IXRetailConstantsIfc.ELEMENT_COUNT_TILL_LOANS,
           financialTotals.getCountTillLoans(),
           financialTotalsElement);

        createArrayReconcilableCountElements(financialTotals.getTillPayIns(),
                                             financialTotalsElement,
                                             IXRetailConstantsIfc.ELEMENT_TILL_PAY_INS_LIST);

        createArrayReconcilableCountElements(financialTotals.getTillPayOuts(),
                                             financialTotalsElement,
                                             IXRetailConstantsIfc.ELEMENT_TILL_PAY_OUTS_LIST);


    }                                   // end createCountElements()

    //---------------------------------------------------------------------
    /**
       Creates element for reconcilable count. <P>
       @param reconcilableCount reconcilable count object
       @param el parent element
       @param elementName count element name
       @exception XMLConversionException thrown if error occurs
    **/
    //---------------------------------------------------------------------
    public void createReconcilableCountElement
      (ReconcilableCountIfc reconcilableCount,
       Element el,
       String elementName)
    throws XMLConversionException
    {                                   // begin createReconcilableCountElement()
        Element countElement = parentDocument.createElement(elementName);

        LogFinancialCountIfc logCount =
          IXRetailGateway.getFactory().getLogFinancialCountInstance();

        // only log count if it exists
        if (reconcilableCount.getEntered() != null)
        {
            logCount.createElement(reconcilableCount.getEntered(),
                                   parentDocument,
                                   countElement,
                                   IXRetailConstantsIfc.ELEMENT_ENTERED);
        }
        if (reconcilableCount.getExpected() != null)
        {
            logCount.createElement(reconcilableCount.getExpected(),
                                   parentDocument,
                                   countElement,
                                   IXRetailConstantsIfc.ELEMENT_EXPECTED);
        }

        el.appendChild(countElement);

    }                                   // end createReconcilableCountElement()

    //---------------------------------------------------------------------
    /**
       Creates elements for an array of reconcilable counts. <P>
       @param countArray array of reconcilable counts
       @param el financial totals element
       @param name of name of array element
       @exception XMLConversionException thrown if error occrs
    **/
    //---------------------------------------------------------------------
    protected void createArrayReconcilableCountElements
      (ReconcilableCountIfc[] countArray,
       Element el,
       String name)
    throws XMLConversionException
    {                                   // begin createArrayReconcilableCountElements()
        Element countElement = parentDocument.createElement(name);
        for (int i = 0; i < countArray.length; i++)
        {
            createReconcilableCountElement(countArray[i],
                                           countElement,
                                           IXRetailConstantsIfc.ELEMENT_RECONCILABLE_COUNT);
        }
        el.appendChild(countElement);
    }                                   // end createArrayReconcilableCountElements()

    //---------------------------------------------------------------------
    /**
       Creates elements for simple types in totals.  This method is provided
       primarily to facilitate readability.
       @param financialTotals financial totals object
       @param financialTotalsElement financial totals element
       @exception XMLConversionException thrown if error occurs
    **/
    //---------------------------------------------------------------------
    protected void createSimpleTypeElements(FinancialTotalsIfc financialTotals,
                                            Element financialTotalsElement)
    throws XMLConversionException
    {                                   // begin createSimpleTypeElements()
        createTextNodeElement
          (IXRetailConstantsIfc.ELEMENT_TRANSACTION_COUNT,
           financialTotals.getTransactionCount(),
           financialTotalsElement);

        createTextNodeElement
          (IXRetailConstantsIfc.ELEMENT_AMOUNT_GROSS_TAXABLE_ITEM_SALES,
           financialTotals.getAmountGrossTaxableItemSales(),
           financialTotalsElement);

        createTextNodeElement
          (IXRetailConstantsIfc.ELEMENT_UNITS_GROSS_TAXABLE_ITEM_SALES,
           financialTotals.getUnitsGrossTaxableItemSales(),
           financialTotalsElement);

        createTextNodeElement
          (IXRetailConstantsIfc.ELEMENT_AMOUNT_GROSS_NON_TAXABLE_ITEM_SALES,
           financialTotals.getAmountGrossNonTaxableItemSales(),
           financialTotalsElement);

        createTextNodeElement
          (IXRetailConstantsIfc.ELEMENT_UNITS_GROSS_NON_TAXABLE_ITEM_SALES,
           financialTotals.getUnitsGrossNonTaxableItemSales(),
           financialTotalsElement);

        createTextNodeElement
          (IXRetailConstantsIfc.ELEMENT_AMOUNT_GROSS_TAX_EXEMPT_ITEM_SALES,
           financialTotals.getAmountGrossTaxExemptItemSales(),
           financialTotalsElement);

        createTextNodeElement
          (IXRetailConstantsIfc.ELEMENT_UNITS_GROSS_TAX_EXEMPT_ITEM_SALES,
           financialTotals.getUnitsGrossTaxExemptItemSales(),
           financialTotalsElement);

        createTextNodeElement
          (IXRetailConstantsIfc.ELEMENT_AMOUNT_GROSS_TAXABLE_ITEM_SALES_VOIDED,
           financialTotals.getAmountGrossTaxableItemSalesVoided(),
           financialTotalsElement);

        createTextNodeElement
          (IXRetailConstantsIfc.ELEMENT_UNITS_GROSS_TAXABLE_ITEM_SALES_VOIDED,
           financialTotals.getUnitsGrossTaxableItemSalesVoided(),
           financialTotalsElement);

        createTextNodeElement
          (IXRetailConstantsIfc.ELEMENT_AMOUNT_GROSS_NON_TAXABLE_ITEM_SALES_VOIDED,
           financialTotals.getAmountGrossNonTaxableItemSalesVoided(),
           financialTotalsElement);

        createTextNodeElement
          (IXRetailConstantsIfc.ELEMENT_UNITS_GROSS_NON_TAXABLE_ITEM_SALES_VOIDED,
           financialTotals.getUnitsGrossNonTaxableItemSalesVoided(),
           financialTotalsElement);

        createTextNodeElement
          (IXRetailConstantsIfc.ELEMENT_AMOUNT_GROSS_TAX_EXEMPT_ITEM_SALES_VOIDED,
           financialTotals.getAmountGrossTaxExemptItemSalesVoided(),
           financialTotalsElement);

        createTextNodeElement
          (IXRetailConstantsIfc.ELEMENT_UNITS_GROSS_TAX_EXEMPT_ITEM_SALES_VOIDED,
           financialTotals.getUnitsGrossTaxExemptItemSalesVoided(),
           financialTotalsElement);

        createTextNodeElement
          (IXRetailConstantsIfc.ELEMENT_AMOUNT_GROSS_TAXABLE_ITEM_RETURNS,
           financialTotals.getAmountGrossTaxableItemReturns(),
           financialTotalsElement);

        createTextNodeElement
          (IXRetailConstantsIfc.ELEMENT_UNITS_GROSS_TAXABLE_ITEM_RETURNS,
           financialTotals.getUnitsGrossTaxableItemReturns(),
           financialTotalsElement);

        createTextNodeElement
          (IXRetailConstantsIfc.ELEMENT_AMOUNT_GROSS_NON_TAXABLE_ITEM_RETURNS,
           financialTotals.getAmountGrossNonTaxableItemReturns(),
           financialTotalsElement);

        createTextNodeElement
          (IXRetailConstantsIfc.ELEMENT_UNITS_GROSS_NON_TAXABLE_ITEM_RETURNS,
           financialTotals.getUnitsGrossNonTaxableItemReturns(),
           financialTotalsElement);

        createTextNodeElement
          (IXRetailConstantsIfc.ELEMENT_AMOUNT_GROSS_TAX_EXEMPT_ITEM_RETURNS,
           financialTotals.getAmountGrossTaxExemptItemReturns(),
           financialTotalsElement);

        createTextNodeElement
          (IXRetailConstantsIfc.ELEMENT_UNITS_GROSS_TAX_EXEMPT_ITEM_RETURNS,
           financialTotals.getUnitsGrossTaxExemptItemReturns(),
           financialTotalsElement);

        createTextNodeElement
          (IXRetailConstantsIfc.ELEMENT_AMOUNT_GROSS_TAXABLE_ITEM_RETURNS_VOIDED,
           financialTotals.getAmountGrossTaxableItemReturnsVoided(),
           financialTotalsElement);

        createTextNodeElement
          (IXRetailConstantsIfc.ELEMENT_UNITS_GROSS_TAXABLE_ITEM_RETURNS_VOIDED,
           financialTotals.getUnitsGrossTaxableItemReturnsVoided(),
           financialTotalsElement);

        createTextNodeElement
          (IXRetailConstantsIfc.ELEMENT_AMOUNT_GROSS_NON_TAXABLE_ITEM_RETURNS_VOIDED,
           financialTotals.getAmountGrossNonTaxableItemReturnsVoided(),
           financialTotalsElement);

        createTextNodeElement
          (IXRetailConstantsIfc.ELEMENT_UNITS_GROSS_NON_TAXABLE_ITEM_RETURNS_VOIDED,
           financialTotals.getUnitsGrossNonTaxableItemReturnsVoided(),
           financialTotalsElement);

        createTextNodeElement
          (IXRetailConstantsIfc.ELEMENT_AMOUNT_GROSS_TAX_EXEMPT_ITEM_RETURNS_VOIDED,
           financialTotals.getAmountGrossTaxExemptItemReturnsVoided(),
           financialTotalsElement);

        createTextNodeElement
          (IXRetailConstantsIfc.ELEMENT_UNITS_GROSS_TAX_EXEMPT_ITEM_RETURNS_VOIDED,
           financialTotals.getUnitsGrossTaxExemptItemReturnsVoided(),
           financialTotalsElement);

        createTextNodeElement
          (IXRetailConstantsIfc.ELEMENT_AMOUNT_TAX_ITEM_SALES,
           financialTotals.getAmountTaxItemSales(),
           financialTotalsElement);

        createTextNodeElement
          (IXRetailConstantsIfc.ELEMENT_AMOUNT_TAX_ITEM_RETURNS,
           financialTotals.getAmountTaxItemReturns(),
           financialTotalsElement);

        createTextNodeElement
          (IXRetailConstantsIfc.ELEMENT_AMOUNT_TAX_TRANSACTION_SALES,
           financialTotals.getAmountTaxTransactionSales(),
           financialTotalsElement);

        createTextNodeElement
          (IXRetailConstantsIfc.ELEMENT_AMOUNT_TAX_TRANSACTION_RETURNS,
           financialTotals.getAmountTaxTransactionReturns(),
           financialTotalsElement);

        createTextNodeElement
          (IXRetailConstantsIfc.ELEMENT_AMOUNT_GROSS_TAXABLE_TRANSACTION_SALES,
           financialTotals.getAmountGrossTaxableTransactionSales(),
           financialTotalsElement);

        createTextNodeElement
          (IXRetailConstantsIfc.ELEMENT_COUNT_GROSS_TAXABLE_TRANSACTION_SALES,
           financialTotals.getCountGrossTaxableTransactionSales(),
           financialTotalsElement);

        createTextNodeElement
          (IXRetailConstantsIfc.ELEMENT_AMOUNT_GROSS_NON_TAXABLE_TRANSACTION_SALES,
           financialTotals.getAmountGrossNonTaxableTransactionSales(),
           financialTotalsElement);

        createTextNodeElement
          (IXRetailConstantsIfc.ELEMENT_COUNT_GROSS_NON_TAXABLE_TRANSACTION_SALES,
           financialTotals.getCountGrossNonTaxableTransactionSales(),
           financialTotalsElement);

        createTextNodeElement
          (IXRetailConstantsIfc.ELEMENT_AMOUNT_GROSS_TAX_EXEMPT_TRANSACTION_SALES,
           financialTotals.getAmountGrossTaxExemptTransactionSales(),
           financialTotalsElement);

        createTextNodeElement
          (IXRetailConstantsIfc.ELEMENT_COUNT_GROSS_TAX_EXEMPT_TRANSACTION_SALES,
           financialTotals.getCountGrossTaxExemptTransactionSales(),
           financialTotalsElement);

        createTextNodeElement
          (IXRetailConstantsIfc.ELEMENT_AMOUNT_GROSS_TAXABLE_TRANSACTION_SALES_VOIDED,
           financialTotals.getAmountGrossTaxableTransactionSalesVoided(),
           financialTotalsElement);

        createTextNodeElement
          (IXRetailConstantsIfc.ELEMENT_COUNT_GROSS_TAXABLE_TRANSACTION_SALES_VOIDED,
           financialTotals.getCountGrossTaxableTransactionSalesVoided(),
           financialTotalsElement);

        createTextNodeElement
          (IXRetailConstantsIfc.ELEMENT_AMOUNT_GROSS_NON_TAXABLE_TRANSACTION_SALES_VOIDED,
           financialTotals.getAmountGrossNonTaxableTransactionSalesVoided(),
           financialTotalsElement);

        createTextNodeElement
          (IXRetailConstantsIfc.ELEMENT_COUNT_GROSS_NON_TAXABLE_TRANSACTION_SALES_VOIDED,
           financialTotals.getCountGrossNonTaxableTransactionSalesVoided(),
           financialTotalsElement);

        createTextNodeElement
          (IXRetailConstantsIfc.ELEMENT_AMOUNT_GROSS_TAX_EXEMPT_TRANSACTION_SALES_VOIDED,
           financialTotals.getAmountGrossTaxExemptTransactionSalesVoided(),
           financialTotalsElement);

        createTextNodeElement
          (IXRetailConstantsIfc.ELEMENT_COUNT_GROSS_TAX_EXEMPT_TRANSACTION_SALES_VOIDED,
           financialTotals.getCountGrossTaxExemptTransactionSalesVoided(),
           financialTotalsElement);

        createTextNodeElement
          (IXRetailConstantsIfc.ELEMENT_AMOUNT_GROSS_TAXABLE_TRANSACTION_RETURNS,
           financialTotals.getAmountGrossTaxableTransactionReturns(),
           financialTotalsElement);

        createTextNodeElement
          (IXRetailConstantsIfc.ELEMENT_COUNT_GROSS_TAXABLE_TRANSACTION_RETURNS,
           financialTotals.getCountGrossTaxableTransactionReturns(),
           financialTotalsElement);

        createTextNodeElement
          (IXRetailConstantsIfc.ELEMENT_AMOUNT_GROSS_NON_TAXABLE_TRANSACTION_RETURNS,
           financialTotals.getAmountGrossNonTaxableTransactionReturns(),
           financialTotalsElement);

        createTextNodeElement
          (IXRetailConstantsIfc.ELEMENT_COUNT_GROSS_NON_TAXABLE_TRANSACTION_RETURNS,
           financialTotals.getCountGrossNonTaxableTransactionReturns(),
           financialTotalsElement);

        createTextNodeElement
          (IXRetailConstantsIfc.ELEMENT_AMOUNT_GROSS_TAX_EXEMPT_TRANSACTION_RETURNS,
           financialTotals.getAmountGrossTaxExemptTransactionReturns(),
           financialTotalsElement);

        createTextNodeElement
          (IXRetailConstantsIfc.ELEMENT_COUNT_GROSS_TAX_EXEMPT_TRANSACTION_RETURNS,
           financialTotals.getCountGrossTaxExemptTransactionReturns(),
           financialTotalsElement);

        createTextNodeElement
          (IXRetailConstantsIfc.ELEMENT_AMOUNT_GROSS_TAXABLE_TRANSACTION_RETURNS_VOIDED,
           financialTotals.getAmountGrossTaxableTransactionReturnsVoided(),
           financialTotalsElement);

        createTextNodeElement
          (IXRetailConstantsIfc.ELEMENT_COUNT_GROSS_TAXABLE_TRANSACTION_RETURNS_VOIDED,
           financialTotals.getCountGrossTaxableTransactionReturnsVoided(),
           financialTotalsElement);

        createTextNodeElement
          (IXRetailConstantsIfc.ELEMENT_AMOUNT_GROSS_NON_TAXABLE_TRANSACTION_RETURNS_VOIDED,
           financialTotals.getAmountGrossNonTaxableTransactionReturnsVoided(),
           financialTotalsElement);

        createTextNodeElement
          (IXRetailConstantsIfc.ELEMENT_COUNT_GROSS_NON_TAXABLE_TRANSACTION_RETURNS_VOIDED,
           financialTotals.getCountGrossNonTaxableTransactionReturnsVoided(),
           financialTotalsElement);

        createTextNodeElement
          (IXRetailConstantsIfc.ELEMENT_AMOUNT_GROSS_TAX_EXEMPT_TRANSACTION_RETURNS_VOIDED,
           financialTotals.getAmountGrossTaxExemptTransactionReturnsVoided(),
           financialTotalsElement);

        createTextNodeElement
          (IXRetailConstantsIfc.ELEMENT_COUNT_GROSS_TAX_EXEMPT_TRANSACTION_RETURNS_VOIDED,
           financialTotals.getCountGrossTaxExemptTransactionReturnsVoided(),
           financialTotalsElement);

        createTextNodeElement
          (IXRetailConstantsIfc.ELEMENT_AMOUNT_GIFT_CERTIFICATE_SALES,
           financialTotals.getAmountGiftCertificateSales(),
           financialTotalsElement);

        createTextNodeElement
          (IXRetailConstantsIfc.ELEMENT_UNITS_GIFT_CERTIFICATE_SALES,
           financialTotals.getUnitsGiftCertificateSales(),
           financialTotalsElement);

        createTextNodeElement
          (IXRetailConstantsIfc.ELEMENT_AMOUNT_HOUSE_PAYMENTS,
           financialTotals.getAmountHousePayments(),
           financialTotalsElement);

        createTextNodeElement
          (IXRetailConstantsIfc.ELEMENT_COUNT_HOUSE_PAYMENTS,
           financialTotals.getCountHousePayments(),
           financialTotalsElement);

        createTextNodeElement
          (IXRetailConstantsIfc.ELEMENT_AMOUNT_TRANSACTION_DISCOUNTS,
           financialTotals.getAmountTransactionDiscounts(),
           financialTotalsElement);

        createTextNodeElement
          (IXRetailConstantsIfc.ELEMENT_NUMBER_TRANSACTION_DISCOUNTS,
           financialTotals.getNumberTransactionDiscounts(),
           financialTotalsElement);

        createTextNodeElement
          (IXRetailConstantsIfc.ELEMENT_AMOUNT_ITEM_DISCOUNTS,
           financialTotals.getAmountItemDiscounts(),
           financialTotalsElement);

        createTextNodeElement
          (IXRetailConstantsIfc.ELEMENT_NUMBER_ITEM_DISCOUNTS,
           financialTotals.getNumberItemDiscounts(),
           financialTotalsElement);

        createTextNodeElement
          (IXRetailConstantsIfc.ELEMENT_AMOUNT_ITEM_MARKDOWNS,
           financialTotals.getAmountItemMarkdowns(),
           financialTotalsElement);

        createTextNodeElement
          (IXRetailConstantsIfc.ELEMENT_NUMBER_ITEM_MARKDOWNS,
           financialTotals.getNumberItemMarkdowns(),
           financialTotalsElement);

        createTextNodeElement
          (IXRetailConstantsIfc.ELEMENT_AMOUNT_RESTOCKING_FEES,
           financialTotals.getAmountRestockingFees(),
           financialTotalsElement);

        createTextNodeElement
          (IXRetailConstantsIfc.ELEMENT_UNITS_RESTOCKING_FEES,
           financialTotals.getUnitsRestockingFees(),
           financialTotalsElement);

        createTextNodeElement
          (IXRetailConstantsIfc.ELEMENT_AMOUNT_SHIPPING_CHARGES,
           financialTotals.getAmountShippingCharges(),
           financialTotalsElement);

        createTextNodeElement
          (IXRetailConstantsIfc.ELEMENT_NUMBER_SHIPPING_CHARGES,
           financialTotals.getNumberShippingCharges(),
           financialTotalsElement);

        createTextNodeElement
          (IXRetailConstantsIfc.ELEMENT_AMOUNT_ITEM_DISC_STORE_COUPONS,
           financialTotals.getAmountItemDiscStoreCoupons(),
           financialTotalsElement);

        createTextNodeElement
          (IXRetailConstantsIfc.ELEMENT_NUMBER_ITEM_DISC_STORE_COUPONS,
           financialTotals.getNumberItemDiscStoreCoupons(),
           financialTotalsElement);

        createTextNodeElement
          (IXRetailConstantsIfc.ELEMENT_AMOUNT_TRANSACTION_DISC_STORE_COUPONS,
           financialTotals.getAmountTransactionDiscStoreCoupons(),
           financialTotalsElement);

        createTextNodeElement
          (IXRetailConstantsIfc.ELEMENT_NUMBER_TRANSACTION_DISC_STORE_COUPONS,
           financialTotals.getNumberTransactionDiscStoreCoupons(),
           financialTotalsElement);

        createTextNodeElement
          (IXRetailConstantsIfc.ELEMENT_AMOUNT_POST_VOIDS,
           financialTotals.getAmountPostVoids(),
           financialTotalsElement);

        createTextNodeElement
          (IXRetailConstantsIfc.ELEMENT_NUMBER_POST_VOIDS,
           financialTotals.getNumberPostVoids(),
           financialTotalsElement);

        createTextNodeElement
          (IXRetailConstantsIfc.ELEMENT_NUMBER_NO_SALES,
           financialTotals.getNumberNoSales(),
           financialTotalsElement);

        createTextNodeElement
          (IXRetailConstantsIfc.ELEMENT_AMOUNT_LINE_VOIDS,
           financialTotals.getAmountLineVoids(),
           financialTotalsElement);

        createTextNodeElement
          (IXRetailConstantsIfc.ELEMENT_UNITS_LINE_VOIDS,
           financialTotals.getUnitsLineVoids(),
           financialTotalsElement);

        createTextNodeElement
          (IXRetailConstantsIfc.ELEMENT_AMOUNT_CANCELLED_TRANSACTIONS,
           financialTotals.getAmountCancelledTransactions(),
           financialTotalsElement);

        createTextNodeElement
          (IXRetailConstantsIfc.ELEMENT_NUMBER_CANCELLED_TRANSACTIONS,
           financialTotals.getNumberCancelledTransactions(),
           financialTotalsElement);

        createTextNodeElement
          (IXRetailConstantsIfc.ELEMENT_AMOUNT_GROSS_TAXABLE_NON_MERCHANDISE_SALES,
           financialTotals.getAmountGrossTaxableNonMerchandiseSales(),
           financialTotalsElement);

        createTextNodeElement
          (IXRetailConstantsIfc.ELEMENT_UNITS_GROSS_TAXABLE_NON_MERCHANDISE_SALES,
           financialTotals.getUnitsGrossTaxableNonMerchandiseSales(),
           financialTotalsElement);

        createTextNodeElement
          (IXRetailConstantsIfc.ELEMENT_AMOUNT_GROSS_NON_TAXABLE_NON_MERCHANDISE_SALES,
           financialTotals.getAmountGrossNonTaxableNonMerchandiseSales(),
           financialTotalsElement);

        createTextNodeElement
          (IXRetailConstantsIfc.ELEMENT_UNITS_GROSS_NON_TAXABLE_NON_MERCHANDISE_SALES,
           financialTotals.getUnitsGrossNonTaxableNonMerchandiseSales(),
           financialTotalsElement);

        createTextNodeElement
          (IXRetailConstantsIfc.ELEMENT_AMOUNT_GROSS_TAXABLE_NON_MERCHANDISE_SALES_VOIDED,
           financialTotals.getAmountGrossTaxableNonMerchandiseSalesVoided(),
           financialTotalsElement);

        createTextNodeElement
          (IXRetailConstantsIfc.ELEMENT_UNITS_GROSS_TAXABLE_NON_MERCHANDISE_SALES_VOIDED,
           financialTotals.getUnitsGrossTaxableNonMerchandiseSalesVoided(),
           financialTotalsElement);

        createTextNodeElement
          (IXRetailConstantsIfc.ELEMENT_AMOUNT_GROSS_NON_TAXABLE_NON_MERCHANDISE_SALES_VOIDED,
           financialTotals.getAmountGrossNonTaxableNonMerchandiseSalesVoided(),
           financialTotalsElement);

        createTextNodeElement
          (IXRetailConstantsIfc.ELEMENT_UNITS_GROSS_NON_TAXABLE_NON_MERCHANDISE_SALES_VOIDED,
           financialTotals.getUnitsGrossNonTaxableNonMerchandiseSalesVoided(),
           financialTotalsElement);

        createTextNodeElement
          (IXRetailConstantsIfc.ELEMENT_AMOUNT_GROSS_TAXABLE_NON_MERCHANDISE_RETURNS,
           financialTotals.getAmountGrossTaxableNonMerchandiseReturns(),
           financialTotalsElement);

        createTextNodeElement
          (IXRetailConstantsIfc.ELEMENT_UNITS_GROSS_TAXABLE_NON_MERCHANDISE_RETURNS,
           financialTotals.getUnitsGrossTaxableNonMerchandiseReturns(),
           financialTotalsElement);

        createTextNodeElement
          (IXRetailConstantsIfc.ELEMENT_AMOUNT_GROSS_NON_TAXABLE_NON_MERCHANDISE_RETURNS,
           financialTotals.getAmountGrossNonTaxableNonMerchandiseReturns(),
           financialTotalsElement);

        createTextNodeElement
          (IXRetailConstantsIfc.ELEMENT_UNITS_GROSS_NON_TAXABLE_NON_MERCHANDISE_RETURNS,
           financialTotals.getUnitsGrossNonTaxableNonMerchandiseReturns(),
           financialTotalsElement);

        createTextNodeElement
          (IXRetailConstantsIfc.ELEMENT_AMOUNT_GROSS_TAXABLE_NON_MERCHANDISE_RETURNS_VOIDED,
           financialTotals.getAmountGrossTaxableNonMerchandiseReturnsVoided(),
           financialTotalsElement);

        createTextNodeElement
          (IXRetailConstantsIfc.ELEMENT_UNITS_GROSS_TAXABLE_NON_MERCHANDISE_RETURNS_VOIDED,
           financialTotals.getUnitsGrossTaxableNonMerchandiseReturnsVoided(),
           financialTotalsElement);

        createTextNodeElement
          (IXRetailConstantsIfc.ELEMENT_AMOUNT_GROSS_NON_TAXABLE_NON_MERCHANDISE_RETURNS_VOIDED,
           financialTotals.getAmountGrossNonTaxableNonMerchandiseReturnsVoided(),
           financialTotalsElement);

        createTextNodeElement
          (IXRetailConstantsIfc.ELEMENT_UNITS_GROSS_NON_TAXABLE_NON_MERCHANDISE_RETURNS_VOIDED,
           financialTotals.getUnitsGrossNonTaxableNonMerchandiseReturnsVoided(),
           financialTotalsElement);

        createTextNodeElement
          (IXRetailConstantsIfc.ELEMENT_AMOUNT_GROSS_GIFT_CARD_ITEM_SALES,
           financialTotals.getAmountGrossGiftCardItemSales(),
           financialTotalsElement);

        createTextNodeElement
          (IXRetailConstantsIfc.ELEMENT_UNITS_GROSS_GIFT_CARD_ITEM_SALES,
           financialTotals.getUnitsGrossGiftCardItemSales(),
           financialTotalsElement);

        createTextNodeElement
          (IXRetailConstantsIfc.ELEMENT_AMOUNT_GROSS_GIFT_CARD_ITEM_SALES_VOIDED,
           financialTotals.getAmountGrossGiftCardItemSalesVoided(),
           financialTotalsElement);

        createTextNodeElement
          (IXRetailConstantsIfc.ELEMENT_UNITS_GROSS_GIFT_CARD_ITEM_SALES_VOIDED,
           financialTotals.getUnitsGrossGiftCardItemSalesVoided(),
           financialTotalsElement);

        createTextNodeElement
          (IXRetailConstantsIfc.ELEMENT_AMOUNT_GROSS_GIFT_CARD_ITEM_RETURNS,
           financialTotals.getAmountGrossGiftCardItemReturns(),
           financialTotalsElement);

        createTextNodeElement
          (IXRetailConstantsIfc.ELEMENT_UNITS_GROSS_GIFT_CARD_ITEM_RETURNS,
           financialTotals.getUnitsGrossGiftCardItemReturns(),
           financialTotalsElement);

        createTextNodeElement
          (IXRetailConstantsIfc.ELEMENT_AMOUNT_GROSS_GIFT_CARD_ITEM_RETURNS_VOIDED,
           financialTotals.getAmountGrossGiftCardItemReturnsVoided(),
           financialTotalsElement);

        createTextNodeElement
          (IXRetailConstantsIfc.ELEMENT_UNITS_GROSS_GIFT_CARD_ITEM_RETURNS_VOIDED,
           financialTotals.getUnitsGrossGiftCardItemReturnsVoided(),
           financialTotalsElement);

        createTextNodeElement
          (IXRetailConstantsIfc.ELEMENT_AMOUNT_TILL_PAY_INS,
           financialTotals.getAmountTillPayIns(),
           financialTotalsElement);

        createTextNodeElement
          (IXRetailConstantsIfc.ELEMENT_COUNT_TILL_PAY_INS,
           financialTotals.getCountTillPayIns(),
           financialTotalsElement);

        createTextNodeElement
          (IXRetailConstantsIfc.ELEMENT_AMOUNT_TILL_PAY_OUTS,
           financialTotals.getAmountTillPayOuts(),
           financialTotalsElement);

        createTextNodeElement
          (IXRetailConstantsIfc.ELEMENT_COUNT_TILL_PAY_OUTS,
           financialTotals.getCountTillPayOuts(),
           financialTotalsElement);

        createTextNodeElement
          (IXRetailConstantsIfc.ELEMENT_AMOUNT_LAYAWAY_PAYMENTS,
           financialTotals.getAmountLayawayPayments(),
           financialTotalsElement);

        createTextNodeElement
          (IXRetailConstantsIfc.ELEMENT_COUNT_LAYAWAY_PAYMENTS,
           financialTotals.getCountLayawayPayments(),
           financialTotalsElement);

        createTextNodeElement
          (IXRetailConstantsIfc.ELEMENT_AMOUNT_LAYAWAY_DELETIONS,
           financialTotals.getAmountLayawayDeletions(),
           financialTotalsElement);

        createTextNodeElement
          (IXRetailConstantsIfc.ELEMENT_COUNT_LAYAWAY_DELETIONS,
           financialTotals.getCountLayawayDeletions(),
           financialTotalsElement);

        createTextNodeElement
          (IXRetailConstantsIfc.ELEMENT_AMOUNT_LAYAWAY_INITIATION_FEES,
           financialTotals.getAmountLayawayInitiationFees(),
           financialTotalsElement);

        createTextNodeElement
          (IXRetailConstantsIfc.ELEMENT_COUNT_LAYAWAY_INITIATION_FEES,
           financialTotals.getCountLayawayInitiationFees(),
           financialTotalsElement);

        createTextNodeElement
          (IXRetailConstantsIfc.ELEMENT_AMOUNT_LAYAWAY_DELETION_FEES,
           financialTotals.getAmountLayawayDeletionFees(),
           financialTotalsElement);

        createTextNodeElement
          (IXRetailConstantsIfc.ELEMENT_COUNT_LAYAWAY_DELETION_FEES,
           financialTotals.getCountLayawayDeletionFees(),
           financialTotalsElement);

        createTextNodeElement
          (IXRetailConstantsIfc.ELEMENT_AMOUNT_ORDER_PAYMENTS,
           financialTotals.getAmountOrderPayments(),
           financialTotalsElement);

        createTextNodeElement
          (IXRetailConstantsIfc.ELEMENT_COUNT_ORDER_PAYMENTS,
           financialTotals.getCountOrderPayments(),
           financialTotalsElement);

        createTextNodeElement
          (IXRetailConstantsIfc.ELEMENT_AMOUNT_ORDER_CANCELS,
           financialTotals.getAmountOrderCancels(),
           financialTotalsElement);

        createTextNodeElement
          (IXRetailConstantsIfc.ELEMENT_COUNT_ORDER_CANCELS,
           financialTotals.getCountOrderCancels(),
           financialTotalsElement);
        
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

    }                                   // end createSimpleTypeElements()

}
