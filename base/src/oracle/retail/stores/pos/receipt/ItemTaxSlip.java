/* ===========================================================================
* Copyright (c) 2004, 2013, Oracle and/or its affiliates. All rights reserved.
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/receipt/ItemTaxSlip.java /main/19 2013/09/05 10:36:15 abondala Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    abondala  09/04/13 - initialize collections
 *    cgreene   12/05/11 - updated from deprecated packages and used more
 *                         bigdecimal constants
 *    cgreene   02/15/11 - move constants into interfaces and refactor
 *    acadar    06/10/10 - use default locale for currency display
 *    acadar    06/09/10 - XbranchMerge acadar_tech30 from
 *                         st_rgbustores_techissueseatel_generic_branch
 *    cgreene   05/26/10 - convert to oracle packaging
 *    cgreene   04/26/10 - XbranchMerge cgreene_tech43 from
 *                         st_rgbustores_techissueseatel_generic_branch
 *    acadar    04/09/10 - optimize calls to LocaleMAp
 *    acadar    04/08/10 - merge to tip
 *    acadar    04/05/10 - use default locale for currency and date/time
 *                         display
 *    cgreene   04/02/10 - remove deprecated LocaleContantsIfc and currencies
 *    abondala  01/03/10 - update header date
 *    nganesh   04/15/09 - Formatted item tax for EJ
 *    nganesh   04/15/09 - Item Tax information is formatted for EJ
 *    nganesh   02/20/09 - Reverted Fix for Tax Group Name journaling
 *    nganesh   02/10/09 - Modified Total Sales Tax Amount in Journal text for
 *                         internationalization
 *    nganesh   02/03/09 - Added a new method getTaxTotalsByRuleForJournal for
 *                         Journaling Tax information without format
 *
 * ===========================================================================
 * $Log:
 *    11   360Commerce 1.10        7/3/2007 4:14:19 PM    Charles D. Baker CR
 *         27266 - Updated currency formatting for ItemDisounts with
 *         PrintItemDiscount parameter to true
 *    10   360Commerce 1.9         4/30/2007 7:01:38 PM   Alan N. Sinton  CR
 *         26485 - Merge from v12.0_temp.
 *    9    360Commerce 1.8         4/25/2007 8:52:39 AM   Anda D. Cadar   I18N
 *         merge
 *
 *    8    360Commerce 1.7         1/4/2007 1:36:42 PM    Charles D. Baker CR
 *         22822 - Backed out change for CR 22822 as incorrect due to flawed
 *         requirements. Requirements updated. Changes backed out.
 *    7    360Commerce 1.6         1/2/2007 1:55:28 PM    Charles D. Baker CR
 *         22822 - Updating to get absolute of sign for item tax summary lines
 *          of receipt.
 *    6    360Commerce 1.5         1/25/2006 4:11:05 PM   Brett J. Larsen merge
 *          7.1.1 changes (aka. 7.0.3 fixes) into 360Commerce view
 *    5    360Commerce 1.4         1/22/2006 11:45:04 AM  Ron W. Haight
 *         removed references to com.ibm.math.BigDecimal
 *    4    360Commerce 1.3         12/13/2005 4:42:38 PM  Barry A. Pape
 *         Base-lining of 7.1_LA
 *    3    360Commerce 1.2         3/31/2005 4:28:35 PM   Robert Pearse
 *    2    360Commerce 1.1         3/10/2005 10:22:33 AM  Robert Pearse
 *    1    360Commerce 1.0         2/11/2005 12:11:42 PM  Robert Pearse
 *: ItemTaxSlip.java,v $
 *    9    .v710     1.2.2.3     11/15/2005 12:34:13    Charles Suehs   Use
 *         real percentage if percentage is not zero, even if amount of tax
 *         comes out to zero (threshold tax edge case).
 *    8    .v710     1.2.2.2     11/15/2005 10:36:02    Charles Suehs   Print
 *         negative tax line first
 *    7    .v710     1.2.2.1     10/25/2005 17:22:08    Charles Suehs   Merge
 *         from v700
 *    6    .v710     1.2.2.0     10/24/2005 15:44:22    Charles Suehs   Merge
 *         from ItemTaxSlip.java, Revision 1.2.1.0
 *    5    .v700     1.2.1.1     12/22/2005 12:09:53    Deepanshu       CR
 *         4067: Print negative tax line first
 *    4    .v700     1.2.1.0     9/28/2005 12:25:55     Jason L. DeLeau 4067:
 *         Fix the way a receipt prints for a price adjusted threshold item,
 *         where the price adjustment is below the threshold,
 *         and the original price is above it.
 *    3    360Commerce1.2         3/31/2005 15:28:35     Robert Pearse
 *    2    360Commerce1.1         3/10/2005 10:22:33     Robert Pearse
 *    1    360Commerce1.0         2/11/2005 12:11:42     Robert Pearse
 *
 *   Revision 1.2.2.1  2004/11/03 21:13:16  lzhao
 *   @scr 7601: print different language based on customer's language.
 *
 *   Revision 1.2  2004/10/01 18:03:39  jdeleau
 *   @scr 7263 Fix npe
 *
 *   Revision 1.1  2004/09/30 20:21:51  jdeleau
 *   @scr 7263 Make printItemTax apply to e-journal as well as receipts.
 *
 * ===========================================================================
 */
package oracle.retail.stores.pos.receipt;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;
import java.util.Properties;

import oracle.retail.stores.commerceservices.common.currency.CurrencyIfc;
import oracle.retail.stores.domain.DomainGateway;
import oracle.retail.stores.domain.lineitem.SaleReturnLineItemIfc;
import oracle.retail.stores.domain.tax.TaxInformationContainerIfc;
import oracle.retail.stores.domain.tax.TaxInformationIfc;
import oracle.retail.stores.domain.transaction.TransactionTotalsIfc;
import oracle.retail.stores.domain.utility.LocaleConstantsIfc;
import oracle.retail.stores.domain.utility.LocaleUtilities;
import oracle.retail.stores.foundation.manager.gui.InternationalTextSupport;
import oracle.retail.stores.foundation.manager.ifc.ParameterManagerIfc;
import oracle.retail.stores.foundation.manager.parameter.ParameterException;
import oracle.retail.stores.foundation.tour.gate.Gateway;
import oracle.retail.stores.common.utility.LocaleMap;
import oracle.retail.stores.foundation.utility.Util;
import oracle.retail.stores.pos.device.POSPrinterActionGroupIfc;
import oracle.retail.stores.pos.manager.ifc.UtilityManagerIfc;
import oracle.retail.stores.utility.I18NConstantsIfc;
import oracle.retail.stores.utility.I18NHelper;
import oracle.retail.stores.utility.JournalConstantsIfc;

/**
 * E-Journal and various receipts need to use the same logic to print tax by
 * items. Rather than copy and paste code all over the place, the printing logic
 * is going to be stored here in one place.
 * 
 * @version $Revision: /main/19 $
 * @since 7.0
 */
public class ItemTaxSlip
{
    /**
     * Maximum length of the tax name to print in a a receipt.
     */
    protected static final int MAX_TAX_RULE_NAME_LENGTH = 5;
    /**
     * tax flag length
     */
    protected static final int TAX_FLAG_LENGTH = 3;

    /**
     *
     * Print jurisdiction delimited tax data, if the PrintItemTax is set to true
     * in the parameter.xml file.
     *
     * @param srli Item being taxed
     * @param spacing Spaces to prepend before printing data on each line
     * @param pm Parameter manager used to determine if the printeItemTax parameter is set.
     * @return Array of tax data, each item in the array is one printable line.
     */
    public String[] getTaxForLineItem(SaleReturnLineItemIfc srli, int spacing, ParameterManagerIfc pm)
    {
        boolean printItemTax = false;
        try
        {
            printItemTax = pm.getBooleanValue("PrintItemTax").booleanValue();
        }
        catch(ParameterException pe)
        {
            // dont care, just assume false if parameter isnt setup
        }
        String[] results = new String[0];
        if(printItemTax)
        {
            results = getTaxForLineItem(srli, spacing);
        }
        return results;
    }
    /**
     * Return a String of line item taxes, with each jurisdiction separately
     * listed.
     *
     * @param srli Item being taxed
     * @param spacing Spaces to prepend before printing data on each line
     *
     * @return String[] containing the line item taxes, each item in the array
     * is a separate line to be printed on receipt or e-journal.
     */
    public String[] getTaxForLineItem(SaleReturnLineItemIfc srli, int spacing)
    {
        ArrayList<String> result = new ArrayList<String>();

        TaxInformationContainerIfc taxContainer = srli.getTaxInformationContainer();
        TaxInformationIfc taxInformation[] = taxContainer.getOrderedTaxInformation();
        String padding = "     "; // 5 spaces
        for(int i=0; i<taxInformation.length; i++)
        {
            // Tax exempt or non taxable items have no business appearing,
            // so exclude them from the printout.
            if(taxInformation[i].getNegativeTaxableAmount().signum() != CurrencyIfc.ZERO)
            {
                StringBuilder taxIdLabel = getTaxIdLabel(taxInformation[i].getNegativeTaxableAmount(),
                        taxInformation[i].getNegativeTaxAmount(), taxInformation[i], spacing);
                StringBuilder tax = getTaxAmountAsString(taxInformation[i].getNegativeTaxAmount()).append(padding);
                //Create a label like "T1=-21.99 @ 1.8500%        -6.10"
                result.add(blockLine(taxIdLabel, tax));
            }

            if(taxInformation[i].getPositiveTaxableAmount().signum() != CurrencyIfc.ZERO)
            {
            	StringBuilder taxIdLabel = getTaxIdLabel(taxInformation[i].getPositiveTaxableAmount(),
            			taxInformation[i].getPositiveTaxAmount(), taxInformation[i], spacing);
                StringBuilder tax = getTaxAmountAsString(taxInformation[i].getPositiveTaxAmount()).append(padding);
                //Create a label like "T1=21.99 @ 1.8500%        6.10"
                result.add(blockLine(taxIdLabel, tax));
            }
        }
        return result.toArray(new String[0]);
    }

    /**
     * Get the taxIdLabel for a tax line item.
     * @param taxableAmount
     * @param taxInfo
     * @param spacing
     * @return String showing tax data for a line item
     */
    public StringBuilder getTaxIdLabel(CurrencyIfc taxableAmount, CurrencyIfc tax, TaxInformationIfc taxInfo, int spacing)
    {
    	Properties props = InternationalTextSupport.getInternationalBeanText ("receipt", ReceiptConstantsIfc.RECEIPT_BUNDLES);

    	StringBuilder taxIdLabel = new StringBuilder();
    	for(int j=0; j<spacing; j++)
    	{
    		taxIdLabel.append(" ");
    	}
    	String ruleName = taxInfo.getTaxRuleName();
    	// Special Case, where we are using the default tax rule
    	if(ruleName.equals("LocTx"))
    	{
    		ruleName = retrieveText("LocalTaxRuleName", "LocTx", props);
    	}
    	// Force the rule name to fit on the receipt
    	if(ruleName.length() > MAX_TAX_RULE_NAME_LENGTH)
    	{
    		taxIdLabel.append(ruleName.substring(0, MAX_TAX_RULE_NAME_LENGTH));
    	}
    	else
    	{
    		taxIdLabel.append(ruleName);
    	}
    	if(taxInfo.getUsesTaxRate())
    	{
    		// Create a label like "T1=21.99 @ 1.8500%"
    		taxIdLabel.append("=");
    		// Append the taxable amount
    		taxIdLabel.append(roundCurrency(taxableAmount).toGroupFormattedString());

    		taxIdLabel.append(" @ ");

    		// If the tax was 0, the percentage is 0, otherwise use the real percentage.
    		// A tax of 0 when percent is used, is only likely for threshold taxes.
            // 15-Nov-2005: Which is exactly what we are having to fix today.
    		double percentage = 0.0;
    		if(tax.signum() != CurrencyIfc.ZERO || taxInfo.getTaxPercentage().signum() != 0)
    		{
    			percentage = taxInfo.getTaxPercentage().movePointLeft(2).doubleValue();
    		}
    		// Make the percentage 4 digits after decimal
    		String formatPattern = retrieveText("FourDigitPercentFormat", "#0.0000%", props);
    		DecimalFormat formatter = new DecimalFormat();
    		formatter.applyPattern(formatPattern);
    		taxIdLabel.append(formatter.format(percentage));
    	}
    	return taxIdLabel;
    }
    /**
     *
     * Print jurisdiction delimited tax data, if the PrintItemTax is set to true
     * in the parameter.xml file.
     *
     *  @param totals
     *  @param pm
     *  @return Array of tax data, each item in the array is one printable line.
     */
    public String[] getTaxTotalsByRule(TransactionTotalsIfc totals, ParameterManagerIfc pm)
    {
        boolean printItemTax = false;
        try
        {
            if(pm != null)
            {
                printItemTax = pm.getBooleanValue("PrintItemTax").booleanValue();
            }
        }
        catch(ParameterException pe)
        {
            // dont care, just assume false if parameter isnt setup
        }
        String[] results = new String[0];
        if(printItemTax)
        {
            results = getTaxTotalsByRule(totals);
        }
        return results;
    }
    /**
     * Prints the tax totals for the transaction, separating
     * each tax rule if necessary.
     *
     * @param totals
     * @return Array of strings, each line being a tax item to print
     */
    public String[] getTaxTotalsByRule(TransactionTotalsIfc totals)
    {
        return getTaxTotalsByRule(true, totals);
    }
    /**
     * Prints the tax totals for the transaction, separating
     * each tax rule if necessary.
     *
     * @param receiptFormat True if receipt formatting applies, False for Ejournal formatting applies
     * @param totals
     * @return Array of strings, each line being a tax item to print
     */
    public String[] getTaxTotalsByRule(boolean receiptFormat, TransactionTotalsIfc totals)
    {
        // First we sort the taxes by rule name.  Rule name should be something like T1, T2
        // T3, TT, etc.  Add taxes with the same rule name together.

    	if (!receiptFormat)
    		return getTaxTotalsByRuleForJournal(totals);

        Locale receiptLocale = LocaleMap.getLocale(LocaleConstantsIfc.RECEIPT);
        // get properties for receipt
        UtilityManagerIfc utility = (UtilityManagerIfc) Gateway.getDispatcher().getManager(UtilityManagerIfc.TYPE);
        Properties props = utility.getBundleProperties("receipt", ReceiptConstantsIfc.RECEIPT_BUNDLES, receiptLocale);
        ArrayList<String> taxPrintout = new ArrayList<String>();
        TaxInformationContainerIfc taxContainer = totals.getTaxInformationContainer();
        TaxInformationIfc taxInformationArray[] = taxContainer.getOrderedTaxInformation();
        HashMap<String,TaxInformationIfc> taxData = new HashMap<String,TaxInformationIfc>(0);
        for(int i=0; i<taxInformationArray.length; i++)
        {
            String taxIdLabel = taxInformationArray[i].getTaxRuleName();
            if(taxData.get(taxIdLabel) == null)
            {
                taxData.put(taxIdLabel, (TaxInformationIfc)taxInformationArray[i].clone());
            }
            else
            {
                TaxInformationIfc savedInfo = taxData.get(taxIdLabel);
                savedInfo.add(taxInformationArray[i]);
            }
        }
        // Sort the Tax Info by label
        String[] taxLabels = LocaleUtilities.sort(taxData.keySet().toArray(new String[0]), receiptLocale);
        // Print out the results
        String taxableAmountText = retrieveText("TaxableAmountText", "Taxable Amount", props);
        String taxText = retrieveText("TaxText", "Tax", props);
        String padding = "     "; // 5 spaces
        for(int i=0; i<taxLabels.length; i++)
        {
            TaxInformationIfc taxInformation = taxData.get(taxLabels[i]);

            // Tax exempt or non taxable items have no business appearing,
            // so exclude them from the printout.
            if(taxInformation.getTaxAmount().signum() != CurrencyIfc.ZERO)
            {
                //StringBuilder amount = getTaxAmountAsString(taxInformation.getTaxableAmount()).append(padding);
            	// Any individual items that had 0 tax don't get counted in the effective taxable amount.
            	StringBuilder amount = getTaxAmountAsString(taxInformation.getEffectiveTaxableAmount()).append(padding);
                StringBuilder tax = getTaxAmountAsString(taxInformation.getTaxAmount()).append(padding);
                String taxLabel = taxLabels[i];
                if(taxLabel.length() > MAX_TAX_RULE_NAME_LENGTH)
                {
                    taxLabel = taxLabel.substring(0, MAX_TAX_RULE_NAME_LENGTH);
                }
                // Make sure tax label exists
                if(taxLabel.length() > 0)
                {
                    taxPrintout.add(blockLine(new StringBuilder("   "+taxLabel+" "+taxableAmountText), amount));
                    taxPrintout.add(blockLine(new StringBuilder("   "+taxLabel+" "+taxText), tax));
                }
                // If not just use generic label
                else
                {
                    taxPrintout.add(blockLine(new StringBuilder("   "+taxableAmountText), amount));
                    taxPrintout.add(blockLine(new StringBuilder("   "+taxText), tax));
                }
            }
        }
        // Print the total tax
        StringBuilder taxLabel = new StringBuilder(retrieveText("TotalSalesTaxText", "Total Sales Tax", props));
        // block print the row
        String taxRow = null;
        if (receiptFormat)
        {
            taxRow = blockLine(taxLabel, getTaxAmountAsString(totals.getTaxTotal()));
        }

        taxPrintout.add(taxRow);
        // Blank Line
        taxPrintout.add("");
        return taxPrintout.toArray(new String[0]);
    }
    /**
     * EJournals the tax totals for the transaction, separating
     * each tax rule if necessary.
     *
     * @param totals
     * @return Array of strings, each line being a tax item to print
     */
    public String[] getTaxTotalsByRuleForJournal( TransactionTotalsIfc totals)
    {
        // First we sort the taxes by rule name.  Rule name should be something like T1, T2
        // T3, TT, etc.  Add taxes with the same rule name together.

        Locale journalLocale = LocaleMap.getLocale(LocaleConstantsIfc.JOURNAL);
        ArrayList<String> taxPrintout = new ArrayList<String>();
        TaxInformationContainerIfc taxContainer = totals.getTaxInformationContainer();
        TaxInformationIfc taxInformationArray[] = taxContainer.getOrderedTaxInformation();
        HashMap<String,TaxInformationIfc> taxData = new HashMap<String,TaxInformationIfc>(0);
        for(int i=0; i<taxInformationArray.length; i++)
        {
            String taxIdLabel = taxInformationArray[i].getTaxRuleName();
            if(taxData.get(taxIdLabel) == null)
            {
                taxData.put(taxIdLabel, (TaxInformationIfc)taxInformationArray[i].clone());
            }
            else
            {
                TaxInformationIfc savedInfo = taxData.get(taxIdLabel);
                savedInfo.add(taxInformationArray[i]);
            }
        }
        // Sort the Tax Info by label

        String[] taxLabels = LocaleUtilities.sort(taxData.keySet().toArray(new String[0]), journalLocale);


        for(int i=0; i<taxLabels.length; i++)
        {
            TaxInformationIfc taxInformation = taxData.get(taxLabels[i]);

            // Tax exempt or non taxable items have no business appearing,
            // so exclude them from the printout.
            if(taxInformation.getTaxAmount().signum() != CurrencyIfc.ZERO)
            {
            	// Any individual items that had 0 tax don't get counted in the effective taxable amount.
            	String amount = taxInformation.getEffectiveTaxableAmount().toGroupFormattedString();
                String tax = taxInformation.getTaxAmount().toGroupFormattedString();
                String taxLabel = taxLabels[i].trim();


                // Make sure tax label exists
                if(taxLabel.length() > 0)
                {

                	if(taxLabel.length()>5) taxLabel = taxLabel.substring(0,5);

                	taxLabel = taxLabel.trim();

                	Object[] dataTaxAmountArgs = new Object[]{taxLabel,amount.toString().trim()};
                    taxPrintout.add(I18NHelper.getString(I18NConstantsIfc.EJOURNAL_TYPE, JournalConstantsIfc.TAXABLE_AMOUNT_TEXT, dataTaxAmountArgs,journalLocale).trim());


                    Object[] dataTaxArgs = new Object[]{taxLabel,tax.toString().trim()};
                    taxPrintout.add(I18NHelper.getString(I18NConstantsIfc.EJOURNAL_TYPE, JournalConstantsIfc.TAX_TEXT, dataTaxArgs,journalLocale).trim());

                }
                // If not just use generic label
                else
                {

                    Object[] dataTaxAmountArgs = new Object[]{"",amount.toString().trim()};
                    taxPrintout.add(I18NHelper.getString(I18NConstantsIfc.EJOURNAL_TYPE, JournalConstantsIfc.TAXABLE_AMOUNT_TEXT, dataTaxAmountArgs,journalLocale).trim());


                    Object[] dataTaxArgs = new Object[]{"",tax.toString().trim()};
                    taxPrintout.add(I18NHelper.getString(I18NConstantsIfc.EJOURNAL_TYPE, JournalConstantsIfc.TAX_TEXT, dataTaxArgs,journalLocale).trim());
                }
            }
        }

        Object[] dataArgs = new Object[]{totals.getTaxTotal().toGroupFormattedString().trim()};
        taxPrintout.add(I18NHelper.getString(I18NConstantsIfc.EJOURNAL_TYPE, JournalConstantsIfc.TOTAL_SALES_TAX_LABEL, dataArgs,journalLocale));


        // Print the total tax
        // Blank Line
        taxPrintout.add("");
        return taxPrintout.toArray(new String[0]);
    }

    /**
     * Retrieves a localized text string from properties
     *
     * @param tag
     *            a property bundle tag for localized text
     * @param defaultText
     *            a plain text value to use as a default
     * @param props
     *            properties containing localized text
     * @return a localized text string
     */
    protected String retrieveText(String tag, String defaultText, Properties props)
    {
        String result = defaultText;

        if (props != null && tag != null)
        {
            result = props.getProperty(tag, defaultText);
        }

        return result;
    }

    /**
     * Round the 5 decimal digit currency value to 2 decimal digit precision.
     * <P>
     *
     * @param input
     *            currency value to be rounded
     * @return Rounded currency value
     */
    protected CurrencyIfc roundCurrency(CurrencyIfc input)
    {
        // Adjust the precision Need to do rounding in two steps, starting from
        // the 3rd decimal digit first, then round again at the 2nd decimal
        // digit.
        BigDecimal bd = new BigDecimal(input.getStringValue());
        BigDecimal bOne = new BigDecimal(1);
        bd = bd.divide(bOne, 3, BigDecimal.ROUND_HALF_UP);
        CurrencyIfc roundedCurrency = DomainGateway.getBaseCurrencyInstance(bd);

        BigDecimal bd2 = new BigDecimal(roundedCurrency.getStringValue());
        bd2 = bd2.divide(bOne, TransactionTotalsIfc.UI_PRINT_TAX_DISPLAY_SCALE, BigDecimal.ROUND_HALF_UP);

        roundedCurrency = DomainGateway.getBaseCurrencyInstance(bd2);
        return (roundedCurrency);
    }

    /**
     * Given a currency, get the properly formatted String value to print
     * on the receipt, when printing out tax info.
     *
     *  @param amount currency
     *  @return String value of that currency, padded on the right as necessary
     */
    protected StringBuilder getTaxAmountAsString(CurrencyIfc amount)
    {
         // prepare the tax amount.
        StringBuilder amountString = new StringBuilder(roundCurrency(amount).toGroupFormattedString());

        // pad for the tax flag column so that the tax amount will line
        // up with the other values in the column.
        for (int i = 0; i < TAX_FLAG_LENGTH + 1; i++)
        {
             amountString.append(" ");
        }
        return amountString;
    }

    /**
     * Builds a line with the left and right strings separated by spaces.
     * @param left StringBuilder
     * @param right StringBuilder
     * @return String formatted line
     */
    protected String blockLine(StringBuilder left, StringBuilder right)
    {
        StringBuilder s = new StringBuilder(POSPrinterActionGroupIfc.LINE_LENGTH_DEFAULT);
        s.append(left);

        // pad with spaces
        for (int i = left.length(); i < POSPrinterActionGroupIfc.LINE_LENGTH_DEFAULT - right.length(); i++)
        {
            s.append(" ");
        }

        s.append(right);

        return s.toString();
    }

    /**
     * Builds a line with the label allocated 26 spaces and an amount allocated 12 spaces.
     * @param label StringBuilder contaiing line label
     * @param amount CurrencyIfc representing amount to be printed
     * @return String formatted line
     */
    protected String eJournalLine(StringBuilder label, CurrencyIfc amount)
    {
        StringBuilder strResult = new StringBuilder().append(label);
        final int labelLength = 26;
        strResult.append(Util.SPACES.substring(label.length(), labelLength));

        final int amountLength = 12;
        // prepare the tax amount.
        StringBuilder amountString = new StringBuilder(roundCurrency(amount).toGroupFormattedString());
        //amount
        if (amountString.length() < amountLength)
        {
            strResult.append(Util.SPACES.substring(amountString.length(), amountLength));
        }
        strResult.append(amountString);
        return strResult.toString();
    }


}
