/* ===========================================================================
* Copyright (c) 2004, 2014, Oracle and/or its affiliates. All rights reserved.
 * ===========================================================================
 * $Header: rgbustores/modules/domain/src/oracle/retail/stores/domain/lineitem/PriceAdjustmentLineItem.java /main/20 2014/06/16 13:56:05 sgu Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    cgreen    08/05/14 - remove deprecated methods
 *    sgu       06/15/14 - fix transaction total and discount calculation for
 *                         order pickup transaction
 *    abhinavs  12/13/12 - Fixing HP Fortify missing null check issues
 *    kelesika  10/27/10 - Empty TaxInformation when post voiding transaction.
 *    acadar    06/10/10 - use default locale for currency display
 *    acadar    06/09/10 - XbranchMerge acadar_tech30 from
 *                         st_rgbustores_techissueseatel_generic_branch
 *    cgreene   05/26/10 - convert to oracle packaging
 *    acadar    04/09/10 - optimize calls to LocaleMAp
 *    acadar    04/05/10 - use default locale for currency and date/time
 *                         display
 *    abondala  01/03/10 - update header date
 *    nganesh   05/05/09 - Using Sales_association constant to journal Sales
 *                         Associate ID
 *    vikini    04/27/09 - EJ Changes
 *    cgreene   03/30/09 - set return item dscounts onto the price adj so they
 *                         print
 *    deghosh   02/12/09 - Cleaning the deprecated method toJournalString()
 *    akandru   10/31/08 - EJ Changes_I18n
 *
 * ===========================================================================
 * $Log:
 *  12   360Commerce 1.11        8/7/2007 3:54:15 PM    Ashok.Mondal    CR
 *       28222 :Display correct formatted item price for price adjustment on
 *       eJournal.
 *  11   360Commerce 1.10        5/21/2007 9:17:03 AM   Anda D. Cadar   Ej
 *       changes and cleanup
 *  10   360Commerce 1.9         4/25/2007 10:00:39 AM  Anda D. Cadar   I18N
 *       merge
 *  9    360Commerce 1.8         2/6/2007 11:14:32 AM   Anil Bondalapati Merge
 *       from PriceAdjustmentLineItem.java, Revision 1.6.1.0
 *  8    360Commerce 1.7         12/5/2006 2:50:17 PM   Brendan W. Farrell Add
 *       empty line to journal.
 *  7    360Commerce 1.6         5/12/2006 5:26:32 PM   Charles D. Baker
 *       Merging with v1_0_0_53 of Returns Managament
 *  6    360Commerce 1.5         1/25/2006 4:11:38 PM   Brett J. Larsen merge
 *       7.1.1 changes (aka. 7.0.3 fixes) into 360Commerce view
 *  5    360Commerce 1.4         1/22/2006 11:41:41 AM  Ron W. Haight   Removed
 *        references to com.ibm.math.BigDecimal
 *  4    360Commerce 1.3         12/13/2005 4:43:49 PM  Barry A. Pape
 *       Base-lining of 7.1_LA
 *  3    360Commerce 1.2         3/31/2005 4:29:28 PM   Robert Pearse
 *  2    360Commerce 1.1         3/10/2005 10:24:19 AM  Robert Pearse
 *  1    360Commerce 1.0         2/11/2005 12:13:22 PM  Robert Pearse
 * $: PriceAdjustmentLineItem.java,v $
 *  6    .v710     1.2.2.1     10/25/2005 17:30:20    Charles Suehs   Merge
 *       from v700
 *  5    .v710     1.2.2.0     10/25/2005 17:24:00    Charles Suehs   Merge
 *       from PriceAdjustmentLineItem.java, Revision 1.2.1.0
 *  4    .v700     1.2.1.0     9/28/2005 12:28:02     Jason L. DeLeau 4067: Fix
 *       the way a receipt prints for a price adjusted threshold item, where
 *       the price adjustment is below the threshold,
 *       and the original price is above it.
 *  3    360Commerce1.2         3/31/2005 15:29:28     Robert Pearse
 *  2    360Commerce1.1         3/10/2005 10:24:19     Robert Pearse
 *  1    360Commerce1.0         2/11/2005 12:13:22     Robert Pearse
 * $
 * Revision 1.16.2.4  2004/11/11 20:07:55  cdb
 * @scr 7620 Suppress threshold tax from ejournal when taxable amount of sale item is 0.00.
 *
 * Revision 1.16.2.3  2004/11/10 21:29:22  cdb
 * @scr 7620 Reversed previous change.
 *
 * Revision 1.16.2.1  2004/11/05 21:58:20  jdeleau
 * @scr 7345 Make percentage print correctly for tax in ejournal.
 *
 * Revision 1.16  2004/09/29 20:27:29  jdeleau
 * @scr 7266 Remove the  percent tax line from journal entry on
 * price adjustment, when the tax is not percentage based.
 *
 * Revision 1.15  2004/07/30 15:32:49  khassen
 * @scr 4984 - Missing entry for price adjustment amount in toJournalString().
 *
 * Revision 1.14  2004/07/28 13:54:52  jriggins
 * @scr 4206 Javadoc updates
 *
 * Revision 1.13  2004/07/22 21:05:13  jriggins
 * @scr 6327 Added getTaxInformationContainer() to PriceAdjustmentLineItem. In PriceAdjustementUtilities.createReturnItem(), added additional data necessary to correctly compute the tax on a return item.
 *
 * Revision 1.12  2004/07/19 21:53:44  jdeleau
 * @scr 6329 Fix the way post-void taxes were being retrieved.
 * Fix for tax overrides, fix for post void receipt printing, add new
 * tax rules for reverse transaction types.
 *
 * Revision 1.11  2004/07/01 00:25:58  jriggins
 * @scr 4984 Updated tax journalling
 *
 * Revision 1.10  2004/06/24 20:53:46  jriggins
 * @scr 4984 Removed some of the left padding
 *
 * Revision 1.9  2004/06/24 20:38:00  jriggins
 * @scr 4984 Modified the journalling output
 *
 * Revision 1.8  2004/04/28 19:50:00  jriggins
 * @scr 3979 Code review cleanup and removed the get/setLinkedItems methods introduced in price adjustment development
 *
 * Revision 1.7  2004/04/21 23:07:14  jriggins
 * @scr 3979 Removed unnecessary comment entry
 *
 * Revision 1.5  2004/04/20 12:50:59  jriggins
 * @scr 3979 Removed setIsPartOfPriceAdjustment()
 *
 * Revision 1.4  2004/04/19 03:24:54  jriggins
 * @scr 3979 Added more items to initialize()
 *
 * Revision 1.3  2004/04/15 15:35:26  jriggins
 * @scr 3979 Changed parameter list for initialize()
 * Revision 1.2 2004/04/06 20:19:18 jriggins @scr 3979 Reworked initialize()
 *
 * Revision 1.1 2004/04/03 00:21:15 jriggins @scr 3979 Price Adjustment feature dev
 *
 *
 * ===========================================================================
 */
package oracle.retail.stores.domain.lineitem;

import java.math.BigDecimal;
import java.util.Locale;

import oracle.retail.stores.utility.I18NConstantsIfc;
import oracle.retail.stores.utility.I18NHelper;
import oracle.retail.stores.utility.JournalConstantsIfc;
import oracle.retail.stores.commerceservices.common.currency.CurrencyIfc;
import oracle.retail.stores.common.utility.LocaleMap;
import oracle.retail.stores.common.utility.Util;
import oracle.retail.stores.domain.utility.LocaleConstantsIfc;
import oracle.retail.stores.domain.DomainGateway;
import oracle.retail.stores.domain.customer.CustomerIfc;
import oracle.retail.stores.domain.tax.TaxInformationContainerIfc;
import oracle.retail.stores.domain.tax.TaxInformationIfc;
import oracle.retail.stores.domain.transaction.SaleReturnTransactionIfc;
import oracle.retail.stores.domain.transaction.TransactionIDIfc;

/**
 * The PriceAdjustmentLineItem class is a Composite class which houses
 * individual sale and return SaleReturnLineItemIfc instances.
 *
 * The original idea was to use this class solely and make use of the individual
 * sale and return components when necessary. Due to the dependance on positioning
 * for line numbers in the SaleReturnTransaction class, I had to make more use of
 * those components than originally planned.
 *
 * This class is now mainly used for price adjustment display and printing purposes and
 * is filtered out of other things like transaction totals by using the
 * SaleReturnLineItemIfc.isPriceAdjustment() call.
 *
 * $Revision: /main/20 $
 */
public class PriceAdjustmentLineItem extends SaleReturnLineItem
    implements PriceAdjustmentLineItemIfc
{
    private static final long serialVersionUID = 7280830432889848480L;

    /**
     * Represents the sale item of a price adjustment
     */
    private SaleReturnLineItemIfc priceAdjustSaleItem = null;

    /**
     * Represents the return item of a price adjustment
     */
    private SaleReturnLineItemIfc priceAdjustReturnItem = null;

    /**
     * Creates a PriceAdjustmentLineItem instance
     */
    public PriceAdjustmentLineItem()
    {
        // Indicate that this SaleReturnLineItem is a price adjustment
        this.setIsPriceAdjustmentLineItem(true);
        setPriceAdjustmentReference(hashCode());
    }

    /**
     * @return Returns the priceAdjustReturnItem.
     */
    public SaleReturnLineItemIfc getPriceAdjustReturnItem()
    {
        return priceAdjustReturnItem;
    }

    /**
     * @param priceAdjustReturnItem
     *            The priceAdjustReturnItem to set.
     */
    public void setPriceAdjustReturnItem(SaleReturnLineItemIfc priceAdjustReturnItem)
    {
        this.priceAdjustReturnItem = priceAdjustReturnItem;
    }

    /**
     * @return Returns the priceAdjustSaleItem.
     */
    public SaleReturnLineItemIfc getPriceAdjustSaleItem()
    {
        return priceAdjustSaleItem;
    }

    /**
     * @param priceAdjustSaleItem
     *            The priceAdjustSaleItem to set.
     */
    public void setPriceAdjustSaleItem(SaleReturnLineItemIfc priceAdjustSaleItem)
    {
        this.priceAdjustSaleItem = priceAdjustSaleItem;
    }

    /* (non-Javadoc)
     * @see oracle.retail.stores.domain.lineitem.SaleReturnLineItem#toString()
     */
    @Override
    public String toString()
    {
        StringBuffer output = new StringBuffer();

        // Print the return info
        output.append("Price Adjustment Return Item:\n\t");
        if (priceAdjustReturnItem != null)
        {
            output.append(priceAdjustReturnItem.toString());
        }
        else
        {
            output.append("null");
        }

        // Print the sale info
        output.append("\nPrice Adjustment Sale Item:\n\t");
        if (priceAdjustSaleItem != null)
        {
            output.append(priceAdjustSaleItem.toString());
        }
        else
        {
            output.append("null");
        }

        // Print this container instance
        output.append("\nPrice Adjustment Container Info:\n\t");
        output.append(super.toString());

        return output.toString();
    }

    /**
     * Initializes a PriceAdjustmentLineItemIfc object
     *
     * @param priceAdjustSaleItem
     *            sale component of price adjustment. Reference is modified.
     * @param priceAdjustReturnItem
     *            return component of price adjustment. Reference is modified.
     * @see oracle.retail.stores.domain.lineitem.PriceAdjustmentLineItemIfc#initialize(oracle.retail.stores.domain.lineitem.SaleReturnLineItemIfc,
     *      oracle.retail.stores.domain.lineitem.SaleReturnLineItemIfc)
     */
    public void initialize(SaleReturnLineItemIfc priceAdjustSaleItem, SaleReturnLineItemIfc priceAdjustReturnItem)
    {
        // Set PriceAdjustmentLineItem specific data
        setPriceAdjustSaleItem(priceAdjustSaleItem);
        setPriceAdjustReturnItem(priceAdjustReturnItem);

        // Indicate that the sale and return components are a part of a price
        // adjustment
        priceAdjustReturnItem.setPriceAdjustmentReference(getPriceAdjustmentReference());
        priceAdjustSaleItem.setPriceAdjustmentReference(getPriceAdjustmentReference());

        // Set info for this PriceAdjustmentLineItem container
        setPLUItem(priceAdjustSaleItem.getPLUItem());
        setPLUItemID(priceAdjustSaleItem.getPLUItemID());
        setItemQuantity(priceAdjustSaleItem.getItemQuantityDecimal());

        // Selling Price
        ItemPriceIfc itemPrice = DomainGateway.getFactory().getItemPriceInstance();
        itemPrice.setSellingPrice(priceAdjustSaleItem.getSellingPrice().subtract(
                priceAdjustReturnItem.getSellingPrice().abs()));

        // Extended Selling Price
        itemPrice.setExtendedSellingPrice(priceAdjustSaleItem.getItemPrice().getExtendedSellingPrice().subtract(
                priceAdjustReturnItem.getItemPrice().getExtendedSellingPrice().abs()));

        // Extended Discount Selling Price
        itemPrice.setExtendedDiscountedSellingPrice(priceAdjustSaleItem.getItemPrice()
                .getExtendedDiscountedSellingPrice().subtract(
                        priceAdjustReturnItem.getItemPrice().getExtendedDiscountedSellingPrice().abs()));
        CurrencyIfc zeroCurrency = DomainGateway.getBaseCurrencyInstance(BigDecimal.ZERO);
        itemPrice.setRestockingFee(zeroCurrency);
        itemPrice.setDiscountEligible(false);
        itemPrice.setEmployeeDiscountEligible(false);
        itemPrice.setDamageDiscountEligible(false);
        itemPrice.getItemTax().setTaxScope(priceAdjustReturnItem.getTaxScope());
        itemPrice.getItemTax().setTaxGroupId(pluItem.getTaxGroupID());
        itemPrice.getItemTax().setTaxInformationContainer(getTaxInformationContainer());
        itemPrice.getItemTax().setTaxMode(priceAdjustReturnItem.getTaxMode());
        itemPrice.getItemTax().setDefaultTaxRules(priceAdjustReturnItem.getDefaultTaxRules());

        // set the returned items discounts onto this for printing
        itemPrice.setItemDiscounts(priceAdjustReturnItem.getItemPrice().getItemDiscounts());

        itemPrice.getItemTax().setDefaultRate(
                priceAdjustReturnItem.getItemPrice().getItemTax().getDefaultRate());

        setItemPrice(itemPrice);

        // Return Item Info
        setReturnItem(priceAdjustReturnItem.getReturnItem());
    }

    /* (non-Javadoc)
     * @see oracle.retail.stores.domain.lineitem.SaleReturnLineItem#clone()
     */
    @Override
    public Object clone()
    {
        PriceAdjustmentLineItem priceAdjustmentLineItem = new PriceAdjustmentLineItem();

        setCloneAttributes(priceAdjustmentLineItem);

        return priceAdjustmentLineItem;
    }

    /**
     * Clones the attributes of this object into another instance
     *
     * @param newPriceAdjustmentLineItem
     *            Object to which the attributes of this instance are cloned
     * @see oracle.retail.stores.domain.lineitem.SaleReturnLineItem#setCloneAttributes(oracle.retail.stores.domain.lineitem.SaleReturnLineItem)
     */
    protected void setCloneAttributes(PriceAdjustmentLineItem newPriceAdjustmentLineItem)
    {
        // Call the parent class method
        super.setCloneAttributes(newPriceAdjustmentLineItem);

        // Clone the subclass' components
        SaleReturnLineItemIfc saleComponent = (SaleReturnLineItemIfc) this.getPriceAdjustSaleItem().clone();
        SaleReturnLineItemIfc returnComponent = (SaleReturnLineItemIfc) this.getPriceAdjustReturnItem().clone();

        // Use those to initialize the new object
        newPriceAdjustmentLineItem.initialize(saleComponent, returnComponent);
    }

    /**
     * @return false
     * @see oracle.retail.stores.domain.lineitem.SaleReturnLineItemIfc#isReturnLineItem()
     */
    public boolean isReturnLineItem()
    {
        return false;
    }

    /**
     * @return false
     * @see oracle.retail.stores.domain.lineitem.SaleReturnLineItemIfc#isSaleLineItem()
     */
    public boolean isSaleLineItem()
    {
        return false;
    }

    /**
     * Creates a journal string of this price adjusted line item
     *
     * @param currentTransaction Current transaction
     * @return Journal string
     * @deprecated as of 13.1 use {@link #toJournalString(SaleReturnTransactionIfc, Locale)} instead
     */
    public String toJournalString(SaleReturnTransactionIfc currentTransaction)
    {

        return (toJournalString(currentTransaction,LocaleMap.getLocale(LocaleConstantsIfc.JOURNAL)));
    }

    /**
     * Creates a journal string of this price adjusted line item
     *
     * @param currentTransaction Current transaction
     * @param journalLocale client's journal locale
     * @return Journal string
     */
    public String toJournalString(SaleReturnTransactionIfc currentTransaction, Locale journalLocale)
    {
        StringBuffer sb = new StringBuffer();

        String leftPad = "  ";
        String sellingPrice = "";

        ReturnItemIfc returnItem = this.getReturnItem();
        SaleReturnLineItemIfc saleComponent = this.getPriceAdjustSaleItem();
        SaleReturnLineItemIfc returnComponent = this.getPriceAdjustReturnItem();
        TransactionIDIfc transactionID = returnItem.getOriginalTransactionID();

        sb.append(Util.EOL).append(I18NHelper.getString(I18NConstantsIfc.EJOURNAL_TYPE,
        		JournalConstantsIfc.PRICE_ADJUSTMENT_LABEL, null,
        		journalLocale)).append(Util.EOL);
        Object[] dataArgs = new Object[]{transactionID.getTransactionIDString()};
        sb.append(I18NHelper.getString(I18NConstantsIfc.EJOURNAL_TYPE,
        		JournalConstantsIfc.ORIGINAL_TRANS_LABEL, dataArgs,
        		journalLocale)).append(Util.EOL);

        dataArgs[0] = returnComponent.getPLUItem().getItemID();
        sb.append(I18NHelper.getString(I18NConstantsIfc.EJOURNAL_TYPE,
        		JournalConstantsIfc.ITEM_LABEL, dataArgs,
        		journalLocale)).append(Util.EOL);

        sb.append(leftPad).append(returnComponent.getPLUItem().getDescription(Locale.ENGLISH)).append(Util.EOL);

        if (saleComponent.getSellingPrice() != null)
        {
          sellingPrice = saleComponent.getSellingPrice().toGroupFormattedString();
        }

        Object[] dataArgs2 = new Object[] {this.getItemQuantityDecimal(), sellingPrice};

        sb.append(leftPad).append(I18NHelper.getString(I18NConstantsIfc.EJOURNAL_TYPE,
        		JournalConstantsIfc.QUANTITY_LABEL, dataArgs2,
        		journalLocale)).append(Util.EOL);

        TaxInformationIfc[] taxInfo = saleComponent.getItemTax().getTaxInformationContainer().getTaxInformation();
        for (int i = 0; i < taxInfo.length; i++)
        {
            if(taxInfo[i].getUsesTaxRate() && taxInfo[i].getTaxableAmount().signum() != 0)
            {
                float taxRate = taxInfo[i].getTaxPercentage().floatValue();
                sb.append(leftPad).append(taxInfo[i].getTaxableAmount().toGroupFormattedString()).append(JournalConstantsIfc.AT_SYMBOL)
                    .append(taxRate).append(JournalConstantsIfc.PERCENTILE_SYMBOL).append(Util.EOL);
            }
        }

        CurrencyIfc returnPrice  = returnComponent.getExtendedDiscountedSellingPrice();
        CurrencyIfc salePrice    = saleComponent.getExtendedDiscountedSellingPrice();
        CurrencyIfc adjustAmount = returnPrice.abs().subtract(salePrice);

        dataArgs[0] = returnPrice.abs().toGroupFormattedString();
        sb.append(I18NHelper.getString(I18NConstantsIfc.EJOURNAL_TYPE,
        		JournalConstantsIfc.PURCHASE_PRICE_LABEL, dataArgs,
        		journalLocale)).append(Util.EOL);

        dataArgs[0] = salePrice.toGroupFormattedString();
        sb.append(I18NHelper.getString(I18NConstantsIfc.EJOURNAL_TYPE,
        		JournalConstantsIfc.CURRENT_PRICE_LABEL, dataArgs,
        		journalLocale)).append(Util.EOL);

        dataArgs[0] = adjustAmount.toGroupFormattedString();
        sb.append(I18NHelper.getString(I18NConstantsIfc.EJOURNAL_TYPE,
        		JournalConstantsIfc.ADJUSTMENT_AMOUNT_LABEL, dataArgs,
        		journalLocale)).append(Util.EOL);

        CurrencyIfc itemDiscAmt = saleComponent.getItemDiscountAmount();
        if (itemDiscAmt != null && itemDiscAmt.signum() > 0)
        {
        	dataArgs[0] = itemDiscAmt.toGroupFormattedString();
            sb.append(I18NHelper.getString(I18NConstantsIfc.EJOURNAL_TYPE,
            		JournalConstantsIfc.ITEM_DISCOUNT_AMOUNT_LABEL, dataArgs,
            		journalLocale)).append(Util.EOL);
        }

        CurrencyIfc transDiscAmt = saleComponent.getItemTransactionDiscountAmount();
        if (transDiscAmt != null && transDiscAmt.signum() > 0)
        {
        	dataArgs[0] = transDiscAmt.toGroupFormattedString();
            sb.append(I18NHelper.getString(I18NConstantsIfc.EJOURNAL_TYPE,
            		JournalConstantsIfc.TRANS_DISCOUNT_AMOUNT_LABEL, dataArgs,
            		journalLocale)).append(Util.EOL);
        }

        String salesAssocID = currentTransaction.getSalesAssociateID();
        if (!Util.isEmpty(salesAssocID))
        {
        	dataArgs[0] = salesAssocID;
            sb.append(I18NHelper.getString(I18NConstantsIfc.EJOURNAL_TYPE,
            		JournalConstantsIfc.SALES_ASSOCIATION, dataArgs,
            		journalLocale)).append(Util.EOL);
        }

        CustomerIfc customer = currentTransaction.getCustomer();
        if (customer != null)
        {
        	Object[] dataArgs3 = new Object[]{customer.getCustomerID(), customer.getCustomerName()};
            sb.append(I18NHelper.getString(I18NConstantsIfc.EJOURNAL_TYPE,
            		JournalConstantsIfc.CUSTOMER_LABEL, dataArgs3,
            		journalLocale));
        }

        return sb.toString();
    }

    /* (non-Javadoc)
     * @see oracle.retail.stores.domain.lineitem.SaleReturnLineItem#equals(java.lang.Object)
     */
    @Override
    public boolean equals(Object obj)
    {
    	// Make sure the supercleass equals() is true
    	boolean equals=false;
    	if(obj instanceof PriceAdjustmentLineItem)
    	{
    		equals = super.equals(obj);
    	}

    	// Then check the components of this class
    	if (equals)
    	{
    		PriceAdjustmentLineItem item = (PriceAdjustmentLineItem) obj;

    		// Check the components

    		// Sale Compnent
    		SaleReturnLineItemIfc saleComponent = item.getPriceAdjustSaleItem();
    		equals = this.getPriceAdjustSaleItem().equals(saleComponent);

    		// Return Component
    		if (equals)
    		{
    			SaleReturnLineItemIfc returnComponent = item.getPriceAdjustReturnItem();
    			equals = this.getPriceAdjustReturnItem().equals(returnComponent);
    		}
    	}

    	return equals;
    }

    /**
     * Retrieve the tax information container that the tax calculation
     * results should be placed.
     * @return
     */
    public TaxInformationContainerIfc getTaxInformationContainer()
    {
        ItemPriceIfc returnItemPrice = getPriceAdjustReturnItem().getItemPrice();
        ItemPriceIfc saleItemPrice = getPriceAdjustSaleItem().getItemPrice();

        TaxInformationContainerIfc taxInformationContainer =
            (TaxInformationContainerIfc)saleItemPrice.getItemTax().getTaxInformationContainer().clone();

        // If tax info exists, add the return tax info to it
        if (taxInformationContainer != null)
        {
            TaxInformationIfc returnTaxInfo[] =
                returnItemPrice.getItemTax().getTaxInformationContainer().getTaxInformation();

            if (returnTaxInfo != null)
            {
                taxInformationContainer.addTaxInformation(returnTaxInfo);
            }
        }
        // Otherwise just return the return tax info
        else
        {
            taxInformationContainer =
                (TaxInformationContainerIfc)returnItemPrice.getItemTax().getTaxInformationContainer().clone();
        }

        return taxInformationContainer;
    }
    
    //----------------------------------------------------------------------------
    /**
     * Retrieve indicator item is totalable in transaction totals
     * @return indicator item is totalable in transaction totals
     */
    //----------------------------------------------------------------------------
    public boolean isTotalable()
    {
        return false; // a price adjustment item is not totalable
    }
}
