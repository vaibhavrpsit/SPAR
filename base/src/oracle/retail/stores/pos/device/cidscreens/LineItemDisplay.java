/* ===========================================================================
* Copyright (c) 1998, 2011, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/device/cidscreens/LineItemDisplay.java /main/14 2011/12/05 12:16:17 cgreene Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    cgreene   12/05/11 - updated from deprecated packages and used more
 *                         bigdecimal constants
 *    acadar    06/10/10 - use default locale for currency display
 *    acadar    06/09/10 - XbranchMerge acadar_tech30 from
 *                         st_rgbustores_techissueseatel_generic_branch
 *    cgreene   05/26/10 - convert to oracle packaging
 *    cgreene   04/26/10 - XbranchMerge cgreene_tech43 from
 *                         st_rgbustores_techissueseatel_generic_branch
 *    cgreene   04/02/10 - remove deprecated LocaleContantsIfc and currencies
 *    abondala  01/03/10 - update header date
 *
 * ===========================================================================
 * $Log:
 *    4    360Commerce 1.3         4/25/2007 8:52:40 AM   Anda D. Cadar   I18N
 *         merge
 *
 *    3    360Commerce 1.2         3/31/2005 4:28:51 PM   Robert Pearse
 *    2    360Commerce 1.1         3/10/2005 10:23:06 AM  Robert Pearse
 *    1    360Commerce 1.0         2/11/2005 12:12:19 PM  Robert Pearse
 *
 *   Revision 1.5  2004/07/30 21:40:10  rzurga
 *   @scr 6546 Cannot invoke the Price Adjusted Items screen on CPOI device
 *   Added price adjustment items as return/purchase items with appropriate messages to CPOI
 *   Also changed the discount, taxes and total values to conform to locale settings
 *
 *   Revision 1.4  2004/07/28 23:29:56  rzurga
 *   @scr 6545 Taxable status indicator is not displayed in Sale items screen on CPOI
 *   Added taxable status indicator to Sale items screen on CPOI
 *
 *   Revision 1.3  2004/07/07 22:15:03  rzurga
 *   @scr 3676 Ingenico does not update balance due if split tender is performed
 *
 *   Enabled adding tender line items to the scrolling screen as line items
 *
 *   Revision 1.2  2004/04/05 15:47:54  jdeleau
 *   @scr 4090 Code review comments incorporated into the codebase
 *
 *   Revision 1.1  2004/03/25 20:25:15  jdeleau
 *   @scr 4090 Deleted items appearing on Ingenico, I18N, perf improvements.
 *   See the scr for more info.
 *
 * ===========================================================================
 */
package oracle.retail.stores.pos.device.cidscreens;

import java.util.Locale;

import oracle.retail.stores.commerceservices.common.currency.CurrencyIfc;
import oracle.retail.stores.domain.tax.TaxConstantsIfc;
import oracle.retail.stores.domain.utility.LocaleConstantsIfc;
import oracle.retail.stores.pos.ado.tender.TenderADOIfc;
import oracle.retail.stores.domain.lineitem.SaleReturnLineItemIfc;
import oracle.retail.stores.domain.stock.PLUItemIfc;
import oracle.retail.stores.domain.utility.LocaleUtilities;
import oracle.retail.stores.common.utility.LocaleMap;

/**
 * Implementation of the LineItemDisplayIfc which is part of the LineItemModel, this
 * tells the device what Strings to display for description of the item, and price.
 * It has two constructors for the different types of line items it may encounter,
 * SaleReturnLineItemIfc and PLUItemIfc objects.
 * 
 * $Revision: /main/14 $
 */
public class LineItemDisplay implements LineItemDisplayIfc
{
    private String displayString;
    private CurrencyIfc price;
    private String displayTaxableIndicator;

    /**
     * Create a LineItemDisplay object
     *
     * @param item Line item to show on device
     */
    public LineItemDisplay(PLUItemIfc item)
    {
        setCPOIDisplayString(item.getDescription(LocaleMap.getLocale(LocaleConstantsIfc.DEVICES)));
        setCPOIDisplayPrice(item.getPrice());
        String taxIndicator;
        if (item.getTaxable())
        {
            taxIndicator = TaxConstantsIfc.TAX_MODE_CHAR[TaxConstantsIfc.TAX_MODE_STANDARD];
        }
        else
        {
            taxIndicator = TaxConstantsIfc.TAX_MODE_CHAR[TaxConstantsIfc.TAX_MODE_NON_TAXABLE];
        }
        setCPOIDisplayTaxableIndicator(taxIndicator);
    }

    /**
     * Create a LineItemDisplay object
     *
     * @param item Line item to dshow on device
     */
    public LineItemDisplay(SaleReturnLineItemIfc item)
    {
        String description = item.getPLUItem().getDescription(LocaleMap.getLocale(LocaleConstantsIfc.DEVICES));
        setCPOIDisplayString(description);
        setCPOIDisplayPrice(item.getExtendedDiscountedSellingPrice());
        setCPOIDisplayTaxableIndicator(TaxConstantsIfc.TAX_MODE_CHAR[item.getItemTax().getTaxMode()]);
    }

    /**
     * Create a LineItemDisplay object
     *
     * @param item Line item to dshow on device
     */
    public LineItemDisplay(TenderADOIfc item)
    {
        String description = item.getTenderType().toString();
        setCPOIDisplayString(description);
        setCPOIDisplayPrice(item.getAmount());
    }

    /**
     * Create a LineItemDisplay object
     *
     * @param item Line item to show on device
     */
    public LineItemDisplay(String item)
    {
        setCPOIDisplayString(item);
    }

    /**
     * Set the price to show on the CPOI device for the
     * line item in question
     *
     *  @param price Price to set
     */
    public void setCPOIDisplayPrice(CurrencyIfc price)
    {
        this.price = price;
    }

    /**
     * Get the price to display on the CPOI device for this line item
     *
     * @param locale
     * @return Price as line item (internationalized) for display on CPOI
     * @see oracle.retail.stores.pos.device.cidscreens.LineItemDisplayIfc#getCPOIDisplayPrice(java.util.Locale)
     */
    public String getCPOIDisplayPrice(Locale locale)
    {
        String displayPrice = "";
        if(price != null)
        {
            displayPrice = price.toFormattedString(locale);
        }
        return displayPrice;
    }

    /**
     * Set the string to appear for an item's description
     *
     *  @param displayString
     */
    public void setCPOIDisplayString(String displayString)
    {
        this.displayString = displayString;
    }

    /**
     *  Get the item description to display on the CPOI Device
     *
     * @return String to display as line item
     * @see oracle.retail.stores.pos.device.cidscreens.LineItemDisplayIfc#getCPOIDisplayDescription()
     */
    public String getCPOIDisplayDescription()
    {
        if(displayString == null)
        {
            displayString = "";
        }
        return displayString;
    }

    /**
     * Get the CPOI item taxable indicator
     *
     * @return Returns the displayTaxableIndicator.
     */
    public String getCPOIDisplayTaxableIndicator()
    {
        return displayTaxableIndicator;
    }
    /**
     * Set the CPOI items taxable indicator
     *
     * @param displayTaxableIndicator The displayTaxableIndicator to set.
     */
    public void setCPOIDisplayTaxableIndicator(String displayTaxableIndicator)
    {
        this.displayTaxableIndicator = displayTaxableIndicator;
    }

    /**
     * Get the price object for this Display Item
     *
     *  @return price
     */
    public CurrencyIfc getPrice()
    {
        return this.price;
    }

    /**
     * Compare if two line item ifcs are equal
     *
     *  @param obj Object to compare to this one
     *  @return true or false
     */
    public boolean equals(Object obj)
    {
        if(obj instanceof LineItemDisplayIfc)
        {
            LineItemDisplayIfc lid = (LineItemDisplayIfc) obj;

            String s1 = getCPOIDisplayDescription();
            String s2 = lid.getCPOIDisplayDescription();

            Locale locale = LocaleMap.getLocale(LocaleMap.DEFAULT);
            String p1 = getCPOIDisplayPrice(locale);
            String p2 = lid.getCPOIDisplayPrice(locale);
            return LocaleUtilities.equals(p1, p2, locale) && LocaleUtilities.equals(s1, s2, locale);
        }
        return false;
    }

}
