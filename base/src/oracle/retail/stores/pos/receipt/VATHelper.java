/* ===========================================================================
* Copyright (c) 2007, 2013, Oracle and/or its affiliates. All rights reserved.
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/receipt/VATHelper.java /main/22 2013/09/05 10:36:16 abondala Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    abondala  09/04/13 - initialize collections
 *    rgour     07/31/13 - putting check when tax amount is zero
 *    rgour     01/04/13 - setting correct taxable amount and tax amount in
 *                         receipt when negative tax is involved
 *    rgour     08/22/12 - WPTG changes phase 2
 *    yiqzhao   04/03/12 - refactor store send for cross channel
 *    cgreene   02/18/11 - refactor printing for switching character sets
 *    acadar    06/10/10 - use default locale for currency display
 *    acadar    06/09/10 - XbranchMerge acadar_tech30 from
 *                         st_rgbustores_techissueseatel_generic_branch
 *    cgreene   05/26/10 - convert to oracle packaging
 *    acadar    04/09/10 - optimize calls to LocaleMAp
 *    abondala  01/03/10 - update header date
 *    vchengeg  02/27/09 - To format EJournal entries
 *    asinton   01/29/09 - Updated VAT Summary formatting for Type 2 VAT
 *                         receipts.
 *    deghosh   01/16/09 - EJ i18n defect fixes
 *    cgreene   01/08/09 - removed reference to getSendPackagesVector()
 *    cgreene   01/08/09 - removed reference to getSendPackagesVector()
 *    vchengeg  12/10/08 - EJ defect fixes
 *    cgreene   10/21/08 - fix setting includingAmount was being set to
 *                         excludingAmount
 *    cgreene   10/17/08 - refactored getVATSummary method
 *
 * ===========================================================================
 * $Log:
 *  21   360Commerce 1.20        4/18/2008 4:39:26 PM   Sandy Gu        back
 *       out v12x port to write VAT discount amount in EJ.
 *  20   360Commerce 1.19        4/8/2008 2:13:05 PM    Charles D. Baker CR
 *       30995 - Made end-of-line behavior for journalling vat tax totals
 *       consistent with Sale Return Transaction totals. Code review by Ownen
 *       Horne.
 *  19   360Commerce 1.18        3/25/2008 1:40:56 PM   Mathews Kochummen
 *       forward port changes from v12x to trunk
 *  18   360Commerce 1.17        8/13/2007 10:23:02 AM  Alan N. Sinton  CR
 *       28240 Removed the "VAT" part of the label on VAT Summary of Type 2
 *       receipts per new requirements.
 *  17   360Commerce 1.16        8/9/2007 4:10:37 PM    Charles D. Baker CR
 *       27638 - Updated alignment to match new requirements.
 *  16   360Commerce 1.15        7/31/2007 3:56:46 PM   Alan N. Sinton  CR
 *       27191 The much less risky approach to fixing this defect: If the tax
 *       and the item's price are of opposite sign, negate the tax.
 *  15   360Commerce 1.14        7/20/2007 3:46:38 PM   Alan N. Sinton  CR
 *       27397 - Adjusted for Tax Status flags to be upto 2 characters in
 *       length.
 *  14   360Commerce 1.13        7/18/2007 3:09:23 PM   Charles D. Baker CR
 *       27638 - Removed unneeded TODO
 *  13   360Commerce 1.12        7/18/2007 10:59:41 AM  Alan N. Sinton  CR
 *       27672 Added the transaction's VAT breakdown in EJournal.
 *  12   360Commerce 1.11        7/17/2007 5:50:41 PM   Charles D. Baker CR
 *       27638 - aligning amounts for VAT EJournal printing.
 *  11   360Commerce 1.10        6/26/2007 5:23:58 PM   Alan N. Sinton  CR
 *       27416 - Print the 'N' for non-taxable items when the parameter set to
 *        allow printing of the tax flag.
 *  10   360Commerce 1.9         6/26/2007 5:04:07 PM   Alan N. Sinton  CR
 *       27402 - Do not tally the PriceAdjustmentLineItem items.
 *  9    360Commerce 1.8         6/19/2007 5:14:31 PM   Maisa De Camargo Added
 *       logic to handle the vatCodeReceiptPrinting Parameter. This parameter
 *       indicates if the vatCode should be printed in the receipt for the
 *       item level or not.
 *  8    360Commerce 1.7         6/7/2007 8:01:09 PM    Alan N. Sinton  CR
 *       26486 - Fixed EJ alignment.
 *  7    360Commerce 1.6         6/5/2007 4:21:56 PM    Alan N. Sinton  CR
 *       26485 - Ignore PriceAdjustmentLineItem in printVATSummary.
 *  6    360Commerce 1.5         6/5/2007 8:31:12 AM    Alan N. Sinton  CR
 *       26485 - fixed an oops.
 *  5    360Commerce 1.4         6/4/2007 6:38:22 PM    Alan N. Sinton  CR
 *       26485 - Changes per review comments.
 *  4    360Commerce 1.3         6/4/2007 6:01:32 PM    Alan N. Sinton  CR
 *       26486 - Changes per review comments.
 *  3    360Commerce 1.2         5/30/2007 2:20:09 PM   Rohit Sachdeva  26364:
 *       Set up to use Spring for LocaleMapLoader in Dispatcher
 *  2    360Commerce 1.1         5/17/2007 12:17:59 PM  Alan N. Sinton  CR
 *       26485 - Removed check for getTaxable on PLU items and shipping method
 *        instances.
 *  1    360Commerce 1.0         5/14/2007 2:33:48 PM   Alan N. Sinton  CR
 *       26486 - EJournal enhancements for VAT.
 * $
 * ===========================================================================
 */
package oracle.retail.stores.pos.receipt;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Locale;
import java.util.Set;

import oracle.retail.stores.commerceservices.common.currency.CurrencyIfc;
import oracle.retail.stores.common.utility.LocaleMap;
import oracle.retail.stores.common.utility.Util;
import oracle.retail.stores.domain.DomainGateway;
import oracle.retail.stores.domain.shipping.ShippingMethodIfc;
import oracle.retail.stores.domain.lineitem.AbstractTransactionLineItemIfc;
import oracle.retail.stores.domain.lineitem.ItemPriceIfc;
import oracle.retail.stores.domain.lineitem.KitComponentLineItemIfc;
import oracle.retail.stores.domain.lineitem.PriceAdjustmentLineItemIfc;
import oracle.retail.stores.domain.lineitem.SaleReturnLineItemIfc;
import oracle.retail.stores.domain.lineitem.SendPackageLineItemIfc;
import oracle.retail.stores.domain.stock.PLUItemIfc;
import oracle.retail.stores.domain.tax.TaxIfc;
import oracle.retail.stores.domain.tax.TaxInformationContainerIfc;
import oracle.retail.stores.domain.tax.TaxInformationIfc;
import oracle.retail.stores.domain.transaction.RetailTransactionIfc;
import oracle.retail.stores.domain.transaction.SaleReturnTransactionIfc;
import oracle.retail.stores.domain.utility.LocaleConstantsIfc;
import oracle.retail.stores.foundation.tour.gate.Gateway;
import oracle.retail.stores.utility.I18NConstantsIfc;
import oracle.retail.stores.utility.I18NHelper;
import oracle.retail.stores.utility.JournalConstantsIfc;

/**
 * Class to provide VAT receipt and journal text.
 *
 * $Revision: /main/22 $
 */
public class VATHelper
{
    /**
     * Right justify for first (0 based) column on VAT summary
     * @deprecated as of 13.1 due to new receipt blueprint framework.
     */
    protected static final int VAT_SUMMARY_FIRST_RIGHT_JUSTIFY = 22;

    /**
     * Right justify for second (0 based) column on VAT summary
     * @deprecated as of 13.1 due to new receipt blueprint framework.
     */
    protected static final int VAT_SUMMARY_SECOND_RIGHT_JUSTIFY = 30;

    /**
     * Right justify for third (0 based) column on VAT summary
     * @deprecated as of 13.1 due to new receipt blueprint framework.
     */
    protected static final int VAT_SUMMARY_THIRD_RIGHT_JUSTIFY = 40;

    /**
     * Total width of line including a trailing currency amount.
     * @deprecated as of 13.1 due to new receipt blueprint framework.
     */
    public static final int  LENGTH_OF_CURRENCY_DISPLAY_LINE = 38;

    /**
     * Total width of line including a trailing currency amount.
     * @deprecated as of 13.1 due to new receipt blueprint framework.
     */
    public static final int LENGTH_OF_INDENTED_VAT_DISPLAY_LINE = 35;

    /**
     * Locale is not used. This is the receipt/customer locale.
     * @deprecated as of 13.1 due to new receipt blueprint framework.
     */
    protected Locale locale = LocaleMap.getLocale(LocaleConstantsIfc.RECEIPT);

    /**
     * Handle to the PrintableDocumentManager.
     * @deprecated as of 13.1 because strings are defined in receipt blueprints.
     */
    protected PrintableDocumentManagerIfc printableDocumentManager = null;

    /**
     * VAT Store number
     */
    protected String vatNumber;

    /**
     * VAT Code Receipt Printing
     * Indicates if the vatCode should be printed in the receipt for the item level
     */
    protected boolean vatCodeReceiptPrinting = true;

    /**
     * Transaction being reported upon. May be null if EJ.
     */
    protected RetailTransactionIfc transaction;

    /**
     * The summary of VAt information for the above transaction
     */
    private TaxInformationIfc[] vatSummary;

    /**
     * Default constructor.
     */
    public VATHelper()
    {
        this(null);
    }

    /**
     * Constructor for printing.
     */
    public VATHelper(RetailTransactionIfc transaction)
    {
        this.transaction = transaction;
    }

    /**
     * Returns the tax flag for VAT.
     *
     * @param srli
     * @param plu
     * @param taxMode
     * @return
     */
    public String getVATTaxFlag(SaleReturnLineItemIfc srli, PLUItemIfc plu, int taxMode)
    {
        String taxFlag = " ";
        if (isVATCodeReceiptPrinting())
        {
            taxFlag = "N";
            TaxInformationIfc[] taxInfomations =
                srli.getTaxInformationContainer().getOrderedTaxInformation();
            if(taxInfomations.length > 0 && taxInfomations[0] != null)
            {
                if(taxInfomations[0].getInclusiveTaxFlag())
                {
                    taxFlag = taxInfomations[0].getTaxRuleName();
                    if(taxFlag.length() > 2)
                    {
                        taxFlag = taxFlag.substring(0, 2);
                    }
                }
                else
                {
                    // get the non VAT tax.
                    taxFlag = "T";

                    if (taxMode == TaxIfc.TAX_MODE_STANDARD && plu.getTaxable() == false)
                    {
                        taxFlag = TaxIfc.TAX_MODE_CHAR[TaxIfc.TAX_MODE_NON_TAXABLE];
                    }
                    else
                    {
                        taxFlag = TaxIfc.TAX_MODE_CHAR[taxMode];
                    }

                    // Pull the i18n form of the tax mode flag
                    PrintableDocumentManagerIfc printableDocumentManager =
                        (PrintableDocumentManagerIfc)Gateway.getDispatcher().getManager(PrintableDocumentManagerIfc.TYPE);
                    taxFlag = printableDocumentManager.retrieveReceiptText("TaxModeChar." + taxFlag, taxFlag);
                }
            }
        }
        if(taxFlag.length() > 2)
        {
            taxFlag = taxFlag.substring(0, 2);
        }
        return taxFlag;
    }

    /**
     * Returns the shipping tax indicator flag for VAT.
     * @return
     */
    public String getVATShippingTaxFlag(TaxInformationContainerIfc taxInfoContainer)
    {
        String taxFlag = " ";

        if (isVATCodeReceiptPrinting())
        {
            PrintableDocumentManagerIfc printableDocumentManager =
                (PrintableDocumentManagerIfc)Gateway.getDispatcher().getManager(PrintableDocumentManagerIfc.TYPE);
            taxFlag = printableDocumentManager.retrieveReceiptText("N", "N");
            if(taxInfoContainer != null)
            {
                TaxInformationIfc[] taxInfomations = taxInfoContainer.getOrderedTaxInformation();
                if(taxInfomations.length > 0 && taxInfomations[0] != null)
                {
                    if(taxInfomations[0].getInclusiveTaxFlag())
                    {
                        taxFlag = taxInfomations[0].getTaxRuleName();
                        if(taxFlag.length() > 2)
                        {
                            taxFlag = taxFlag.substring(0, 2);
                        }
                    }
                }
            }
        }
        return taxFlag;
    }

    /**
     * Get a tax info that adds all the VAT summary infos together.
     *
     * @return
     */
    public TaxInformationIfc getVATSummaryTotal()
    {
        TaxInformationIfc totalTax = DomainGateway.getFactory().getTaxInformationInstance();
        // accumulate totals
        TaxInformationIfc[] taxInfos = getVATSummary();
        for (int i = taxInfos.length - 1; i >= 0; i--)
        {
            updateInclusiveTaxableAmount(totalTax, taxInfos[i].getInclusiveTaxableAmount());
            updateTaxAmount(totalTax, taxInfos[i].getTaxAmount());
            updateTaxableAmount(totalTax, taxInfos[i].getTaxableAmount());
        }
        return totalTax;
    }

    /**
     * Get a summary of VAT information for the transaction. If it is
     *
     * @return
     */
    public TaxInformationIfc[] getVATSummary()
    {
        if (vatSummary == null)
        {
            HashMap<String,TaxInformationIfc> taxRateCodeMap = new HashMap<String,TaxInformationIfc>(0);
            // accumulate the totals for each VAT code from the sale return line items
            for (Enumeration<AbstractTransactionLineItemIfc> e = transaction.getLineItemsVector().elements(); e.hasMoreElements();)
            {
                SaleReturnLineItemIfc srli = (SaleReturnLineItemIfc)e.nextElement();
                // Don't want to add the PriceAdjustmentLineItem since it's already accounted for
                // in the other line items.
                if(!(srli instanceof PriceAdjustmentLineItemIfc))
                {
                    TaxInformationContainerIfc taxContainer = srli.getTaxInformationContainer();
                    TaxInformationIfc[] taxInfomations = taxContainer.getOrderedTaxInformation();
                    if(taxInfomations.length > 0 && taxInfomations[0] != null)
                    {
                        TaxInformationIfc ti = taxInfomations[0];
                        if(ti.getInclusiveTaxFlag())
                        {
                            String uniqueID = ti.getUniqueID();
                            String vatCode = ti.getTaxRuleName();
                            if(vatCode.length() > 2)
                            {
                                vatCode = vatCode.substring(0,2);
                            }
                            TaxInformationIfc taxInfo = taxRateCodeMap.get(uniqueID);
                            if (taxInfo == null)
                            {
                                BigDecimal bd = ti.getTaxPercentage();
                                String bdString = trimTrailingZeros(bd.toString());
                                taxInfo = DomainGateway.getFactory().getTaxInformationInstance();
                                taxInfo.setTaxPercentageAsString(bdString);
                                taxInfo.setTaxRuleName(vatCode);
                                taxInfo.setUniqueID(uniqueID);
                                taxRateCodeMap.put(uniqueID, taxInfo);
                            }
                            // get amounts
                            CurrencyIfc includingTax = srli.getExtendedDiscountedSellingPrice();
                            CurrencyIfc tax = taxContainer.getInclusiveTaxAmount();

                            // BEGIN HACK: For CR 27191 This fix is much less risky then to change
                            // the code that negates the taxes, but only if the original values are
                            // positive.  There are other places where similar hacks are implemented
                            // only to correct the tax values in the case of a POST VOID of a RETURN.
                            // Receipt printing and financial totals are examples of where these other
                            // hacks exist. The classes where the conditional negation is implemented
                            // are: TransactionTax.negate(), ItemTax.setItemTaxableAmount(), and
                            // TaxInformationContainer.negate()
                            // - Alan Sinton
                            // If they're not the same sign, negate the tax
                            if(includingTax.signum() != tax.signum())
                            {
                                tax = tax.negate();
                            }
                            // END HACK
                            CurrencyIfc excludingTax = includingTax.subtract(tax);

                            // accumulate for each vat code
                            updateInclusiveTaxableAmount(taxInfo, includingTax);
                            updateTaxAmount(taxInfo, tax);
                            updateTaxableAmount(taxInfo, excludingTax);
                        }
                    }
                }
            }

            vatSummary = new TaxInformationIfc[taxRateCodeMap.size()];
            taxRateCodeMap.values().toArray(vatSummary);
            Arrays.sort(vatSummary);
        }
        return vatSummary;
    }

    /**
     * Trims the trailing zeros from the end of the string.
     *
     * @param string
     * @return
     */
    public String trimTrailingZeros(String string)
    {
        int pointIndex = string.indexOf(".");
        int endIndex = string.length();
        if(pointIndex > 0)
        {
            // chop off insignificant digits
            if((pointIndex + 4) < endIndex)
            {
                string = string.substring(0, pointIndex + 4);
                endIndex = string.length();
            }
            //move up until non-zero
            while(string.charAt(endIndex - 1) == '0')
            {
                endIndex--;
            }
            // remove decimal point if no fractional part remains
            if(endIndex == pointIndex + 1)
            {
                endIndex--;
            }
        }
        return string.substring(0, endIndex);
    }

    /**
     * Prints the first argument left justified and the remaining three arguments
     * in right justified columns.
     *
     * @param a
     * @param b
     * @param c
     * @param d
     * @return The formatted string.
     */
    protected String printFormattedLine(String a, String b, String c, String d)
    {
        StringBuffer sb = new StringBuffer(a);
        if(VAT_SUMMARY_FIRST_RIGHT_JUSTIFY > sb.length() + b.length())
        {
            sb.append(Util.SPACES.substring(0, (VAT_SUMMARY_FIRST_RIGHT_JUSTIFY - (sb.length() + b.length()))));
        }
        sb.append(b);
        if(VAT_SUMMARY_SECOND_RIGHT_JUSTIFY > sb.length() + c.length())
        {
            sb.append(Util.SPACES.substring(0, (VAT_SUMMARY_SECOND_RIGHT_JUSTIFY - (sb.length() + c.length()))));
        }
        sb.append(c);
        if(VAT_SUMMARY_THIRD_RIGHT_JUSTIFY > sb.length() + d.length())
        {
            sb.append(Util.SPACES.substring(0, (VAT_SUMMARY_THIRD_RIGHT_JUSTIFY - (sb.length() + d.length()))));
        }
        sb.append(d);
        return sb.toString();
    }

    /**
     * Updates the saved amount with the new amount in the hashmap for the given key.
     * If there is no saved amount for the key, then one is created.
     *
     * @param map
     * @param taxInfo
     * @param key
     * @deprecated as of 13.1 map no longer used
     */
    protected void updateAmount(HashMap<Object,CurrencyIfc> map, CurrencyIfc amount, Object key)
    {
        CurrencyIfc saved = map.get(key);
        if(saved == null)
        {
            saved = EYSPrintableDocument.getZeroAmount();
        }
        amount = saved.add(amount);
        map.put(key, amount);
    }

    /**
     * Returns the VAT number.
     *
     * @return The VAT number.
     */
    public String getVATNumber()
    {
        return this.vatNumber;
    }

    /**
     * Sets the VAT number.
     *
     * @param number
     */
    public void setVATNumber(String number)
    {
        this.vatNumber = number;
    }

    /**
     * @return Returns the vatCodeReceiptPrinting.
     */
    public boolean isVATCodeReceiptPrinting()
    {
        return vatCodeReceiptPrinting;
    }

    /**
     * @param vatCodeReceiptPrinting The vatCodeReceiptPrinting to set.
     */
    public void setVATCodeReceiptPrinting(boolean vatCodeReceiptPrinting)
    {
        this.vatCodeReceiptPrinting = vatCodeReceiptPrinting;
    }


    /**
     * Protected method generates the VAT tax totals for the transaction and
     * writes to the given StringBuffer.
     *
     * @param sb
     * @param saleReturnTransaction
     * @param negate
     */
    public void journalVATTaxTotals(StringBuffer sb, SaleReturnTransactionIfc saleReturnTransaction, boolean negate)
    {
        boolean printSummary = false;
        HashMap<Integer,String> taxRateCodeMap = new HashMap<Integer,String>(0);
        HashMap taxableAmountMap = new HashMap(0);
        HashMap taxAmountMap = new HashMap(0);
        CurrencyIfc taxAmountTotal = DomainGateway.getBaseCurrencyInstance();

        String vat = I18NHelper.getString(I18NConstantsIfc.EJOURNAL_TYPE, JournalConstantsIfc.VATCODE_LABEL, null);
        Object dataArgs[] = null;
        // accumulate the totals for each VAT code from the sale return line items
        for (Enumeration e = saleReturnTransaction.getLineItemsVector().elements(); e.hasMoreElements();)
        {
            SaleReturnLineItemIfc srli = (SaleReturnLineItemIfc)e.nextElement();
            if(!(srli instanceof PriceAdjustmentLineItemIfc))
            {
                TaxInformationContainerIfc taxContainer = srli.getTaxInformationContainer();
                TaxInformationIfc[] taxInfomations = taxContainer.getOrderedTaxInformation();
                if(taxInfomations.length > 0 && taxInfomations[0] != null)
                {
                    TaxInformationIfc ti = taxInfomations[0];
                    if(ti.getInclusiveTaxFlag())
                    {
                        Integer taxGroupID = new Integer(ti.getTaxGroupID());
                        String vatCode = ti.getTaxRuleName();
                        if(vatCode.length() > 2)
                        {
                            vatCode = vatCode.substring(0,2);
                        }
                        String rateName = (String)taxRateCodeMap.get(taxGroupID);
                        if(rateName == null)
                        {
                        	taxRateCodeMap.put(taxGroupID, vat + " " + vatCode);
                        }
                        // get amounts
                        CurrencyIfc incTax = srli.getExtendedDiscountedSellingPrice();
                        CurrencyIfc tax = taxContainer.getInclusiveTaxAmount();
                        CurrencyIfc exTax = incTax.subtract(tax);
                        // accumulate totals
                        taxAmountTotal = taxAmountTotal.add(tax);
                        // accumulate for each vat code
                        updateAmount(taxableAmountMap, exTax, taxGroupID);
                        updateAmount(taxAmountMap, tax, taxGroupID);
                        printSummary = true;
                    }
                }
            }
        }

        if(printSummary)
        {

            sb.append(Util.EOL);
            // Now Print the totals for each vat code.
            // and accumulate the totals
            Set<Integer> keys = taxRateCodeMap.keySet();
            for (Iterator<Integer> iter = keys.iterator(); iter.hasNext();)
            {
                Object key = iter.next();
                String vatLabel = taxRateCodeMap.get(key);
                CurrencyIfc taxableAmount = (CurrencyIfc)taxableAmountMap.get(key);
                CurrencyIfc taxAmount = (CurrencyIfc)taxAmountMap.get(key);
                if(negate == true)
                {
                    taxableAmount = taxableAmount.negate();
                    taxAmount = taxAmount.negate();
                    taxAmountTotal = taxAmountTotal.negate();
                }
                // print the taxable amount for the current VAT code
                dataArgs = new Object[]{vatLabel, taxableAmount.toGroupFormattedString()};
                sb.append(I18NHelper.getString(I18NConstantsIfc.EJOURNAL_TYPE, JournalConstantsIfc.TAXABLE_AMOUNTS_LABEL, dataArgs));
                sb.append(Util.EOL);
                // print the tax amount for the current VAT code
                dataArgs = new Object[]{vatLabel, taxAmount.toGroupFormattedString()};
                sb.append(I18NHelper.getString(I18NConstantsIfc.EJOURNAL_TYPE, JournalConstantsIfc.VAT_TAX_LABEL, dataArgs));
                sb.append(Util.EOL);
            }
            // print the Total VAT Tax
            dataArgs = new Object[]{taxAmountTotal.toGroupFormattedString()};
            sb.append(I18NHelper.getString(I18NConstantsIfc.EJOURNAL_TYPE, JournalConstantsIfc.TOTAL_VAT_TAX_LABEL, dataArgs));
            // CR 30995
            // Updated this to behave identically to SaleReturnTransactionJournalFormatter.formatTotals()
            // which always has a trailing end-of-line following the Total Tax Line
            sb.append(Util.EOL);
        }
    }

    /**
     * Journals the tax information.
     * @param buffer
     * @param saleReturnLineItem
     * @param ip
     * @param taxMode
     * @param taxScope
     */
    public void journalVATLineItemTax(StringBuffer buffer,
                                         SaleReturnLineItemIfc saleReturnLineItem,
                                         ItemPriceIfc itemPrice,
                                         int taxMode,
                                         int taxScope)
    {
    	String vat = I18NHelper.getString(I18NConstantsIfc.EJOURNAL_TYPE, JournalConstantsIfc.VATCODE_LABEL, null);
    	Object dataArgs[]=null;

    	TaxInformationContainerIfc taxInformationContainer = saleReturnLineItem.getTaxInformationContainer();
        TaxInformationIfc[] taxInformations = taxInformationContainer.getOrderedTaxInformation();
        if(taxInformations != null &&
                taxInformations.length > 0 &&
                taxInformations[0].getInclusiveTaxFlag())
        {
            String vatRate = trimTrailingZeros(taxInformations[0].getTaxPercentage().toString());
            String vatCode = taxInformations[0].getTaxRuleName();
            if(vatCode != null && vatCode.length() > 2)
            {
                vatCode = vatCode.substring(0,2);
            }
            CurrencyIfc tax = taxInformations[0].getTaxAmount();
            CurrencyIfc price = itemPrice.getExtendedDiscountedSellingPrice();
            CurrencyIfc excludingTax = price.subtract(tax);
            PrintableDocumentManagerIfc printableDocumentManager =
                (PrintableDocumentManagerIfc)Gateway.getDispatcher().getManager(PrintableDocumentManagerIfc.TYPE);
            String vatText = printableDocumentManager.retrieveReceiptText("VAT", vat);

            dataArgs = new Object[]{vatRate,vatText,vatCode};
            String rateCode = I18NHelper.getString(I18NConstantsIfc.EJOURNAL_TYPE, JournalConstantsIfc.RATECODE_LABEL, dataArgs);
            printItemVATSummary(buffer, tax, price, excludingTax, rateCode, vatText);
        }
    }

    /**
     * Prints the item's VAT summary information.
     * @param buffer
     * @param tax
     * @param price
     * @param excludingTax
     * @param rateCode
     */
    protected void printItemVATSummary(StringBuffer buffer, CurrencyIfc tax, CurrencyIfc price, CurrencyIfc excludingTax, String rateCode, String vatText)
    {
    	Object dataArgs[]=null;


    	buffer.append(Util.EOL);

        dataArgs = new Object[]{rateCode,tax.toGroupFormattedString()};
        buffer.append(I18NHelper.getString(I18NConstantsIfc.EJOURNAL_TYPE, JournalConstantsIfc.RATECODETAX_LABEL, dataArgs));
        buffer.append(Util.EOL);

        dataArgs = new Object[]{excludingTax.toGroupFormattedString()};
        buffer.append(I18NHelper.getString(I18NConstantsIfc.EJOURNAL_TYPE, JournalConstantsIfc.PRICE_EXCLUDING_LABEL, dataArgs));
        buffer.append(Util.EOL);

        dataArgs = new Object[]{price.toGroupFormattedString()};
        buffer.append(I18NHelper.getString(I18NConstantsIfc.EJOURNAL_TYPE, JournalConstantsIfc.PRICE_INCLUDING_LABEL, dataArgs));
        // buffer.append(priceIncluded);
    }


    /**
     * Creates the journal for the given array of KitComponentLineItemIfc.
     *
     * @param strResult
     * @param kitComponentLineItemArray
     */
    public void journalVATKitComponentLineItemsTax(StringBuffer buffer, KitComponentLineItemIfc[] kitComponentLineItems)
    {
        if(kitComponentLineItems != null)
        {
            buffer.append(Util.EOL);
            HashMap rateCodeMap = new HashMap(0);
            HashMap priceIncTaxMap = new HashMap(0);
            HashMap priceExclTaxMap = new HashMap(0);
            HashMap taxTotalMap = new HashMap(0);
            for(int i = 0; i < kitComponentLineItems.length; i++)
            {
                TaxInformationContainerIfc taxInformationContainer = kitComponentLineItems[i].getTaxInformationContainer();
                TaxInformationIfc[] taxInformations = taxInformationContainer.getOrderedTaxInformation();
                if(taxInformations != null && taxInformations.length > 0)
                {
                    TaxInformationIfc ti = taxInformations[0];
                    Integer taxGroupID = new Integer(ti.getTaxGroupID());
                    String rateCode = (String)rateCodeMap.get(taxGroupID);
                    if(rateCode == null)
                    {
                        String vatRate = trimTrailingZeros(ti.getTaxPercentage().toString());
                        String vatCode = ti.getTaxRuleName();
                        if(vatCode != null && vatCode.length() > 2)
                        {
                            vatCode = vatCode.substring(0,2);
                        }
                        PrintableDocumentManagerIfc printableDocumentManager =
                            (PrintableDocumentManagerIfc)Gateway.getDispatcher().getManager(PrintableDocumentManagerIfc.TYPE);
                        rateCode = vatRate + "% " + printableDocumentManager.retrieveReceiptText("VAT", "VAT") + " " + vatCode;
                        rateCodeMap.put(taxGroupID, rateCode);
                    }
                    CurrencyIfc price = kitComponentLineItems[i].getExtendedDiscountedSellingPrice();
                    CurrencyIfc tax = ti.getTaxAmount();
                    updateAmount(priceIncTaxMap, price, taxGroupID);
                    updateAmount(taxTotalMap, tax, taxGroupID);
                    updateAmount(priceExclTaxMap, price.subtract(tax), taxGroupID);
                }
            }
            Set keys = rateCodeMap.keySet();
            for (Iterator iter = keys.iterator(); iter.hasNext();)
            {
                Object key = iter.next();
                String rateCode = (String)rateCodeMap.get(key);
                CurrencyIfc priceExclTax = (CurrencyIfc)priceExclTaxMap.get(key);
                CurrencyIfc taxTotal = (CurrencyIfc)taxTotalMap.get(key);
                CurrencyIfc priceIncTax = (CurrencyIfc)priceIncTaxMap.get(key);

                String vat = I18NHelper.getString(I18NConstantsIfc.EJOURNAL_TYPE, JournalConstantsIfc.VATCODE_LABEL, null);
                PrintableDocumentManagerIfc printableDocumentManager =
                  (PrintableDocumentManagerIfc)Gateway.getDispatcher().getManager(PrintableDocumentManagerIfc.TYPE);
                String vatText = printableDocumentManager.retrieveReceiptText("VAT", vat);
                printItemVATSummary(buffer, taxTotal, priceIncTax, priceExclTax, rateCode, vatText);
            }
        }
    }

    private void updateTaxableAmount(TaxInformationIfc taxInfo, CurrencyIfc excludingTax)
    {
        if (excludingTax.signum() == CurrencyIfc.POSITIVE)
        {
            if (taxInfo.getPositiveTaxableAmount() == null)
            {
                taxInfo.setTaxableAmount(excludingTax);
            }
            else
            {
                taxInfo.setTaxableAmount(excludingTax.add(taxInfo.getPositiveTaxableAmount()));
            }
        }
        else if (excludingTax.signum() == CurrencyIfc.NEGATIVE)
        {
            if (taxInfo.getNegativeTaxableAmount() == null)
            {
                taxInfo.setTaxableAmount(excludingTax);
            }
            else
            {
                taxInfo.setTaxableAmount(excludingTax.add(taxInfo.getNegativeTaxableAmount()));
            }
        }
    }

    private void updateTaxAmount(TaxInformationIfc taxInfo, CurrencyIfc tax)
    {
        if (tax.signum() == CurrencyIfc.POSITIVE)
        {
            if (taxInfo.getPositiveTaxAmount() == null)
            {
                taxInfo.setTaxAmount(tax);
            }
            else
            {
                taxInfo.setTaxAmount(tax.add(taxInfo.getPositiveTaxAmount()));
            }
        }
        else if(tax.signum()== CurrencyIfc.NEGATIVE)
        {
            if (taxInfo.getNegativeTaxAmount() == null)
            {
                taxInfo.setTaxAmount(tax);
            }
            else
            {
                taxInfo.setTaxAmount(tax.add(taxInfo.getNegativeTaxAmount()));
            }
        }
    }

    private void updateInclusiveTaxableAmount(TaxInformationIfc taxInfo, CurrencyIfc includingTax)
    {
        if (taxInfo.getInclusiveTaxableAmount() == null)
        {
            taxInfo.setInclusiveTaxableAmount(includingTax);
        }
        else
        {
            taxInfo.setInclusiveTaxableAmount(includingTax.add(taxInfo.getInclusiveTaxableAmount()));
        }
    }
}
