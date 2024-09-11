/********************************************************************************
 *   
 *	Copyright (c) 2016 - 2017 MAX Hypermarket, Inc    All Rights Reserved.
 *	Rev 1.4		May 04, 2017		Kritica Agarwal 	GST Changes
 *  Rev	1.3 	Jan 08, 2016		Hitesh Dua		added getAmountPrintedOnReceipt method to Print Selling number on receipt 
 *  Rev	1.2 	Dec 08, 2016		Hitesh Dua		added getLineNumberonReceipt to Print line number on receipt
 *	Rev	1.1 	Nov 07, 2016		Mansi Goel		Changes for Discount Rule FES
 *	Rev	1.0 	Aug 16, 2016		Nitesh Kumar	Changes for Code Merging	
 *
 ********************************************************************************/

package max.retail.stores.domain.lineitem;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;

import oracle.retail.stores.commerceservices.common.currency.CurrencyIfc;
import oracle.retail.stores.domain.discount.DiscountRuleConstantsIfc;
import oracle.retail.stores.domain.discount.ItemDiscountStrategyIfc;
import oracle.retail.stores.domain.discount.PromotionLineItemIfc;
import oracle.retail.stores.domain.employee.EmployeeIfc;
import oracle.retail.stores.domain.giftregistry.GiftRegistry;
import oracle.retail.stores.domain.lineitem.ItemPriceIfc;
import oracle.retail.stores.domain.lineitem.ItemTaxIfc;
import oracle.retail.stores.domain.lineitem.OrderItemStatusIfc;
import oracle.retail.stores.domain.lineitem.ReturnItemIfc;
import oracle.retail.stores.domain.lineitem.SaleReturnLineItemIfc;
import oracle.retail.stores.domain.registry.RegistryIDIfc;
import oracle.retail.stores.domain.stock.PLUItemIfc;
import oracle.retail.stores.domain.utility.EYSDate;
import oracle.retail.stores.domain.utility.EntryMethod;
import oracle.retail.stores.foundation.utility.xml.XMLConversionException;
import oracle.retail.stores.foundation.utility.xml.XMLConverterIfc;

//--------------------------------------------------------------------------
/**
 * Line item for sale or return.
 * <P>
 * 
 * @version $Revision: 1.2 $
 **/
// --------------------------------------------------------------------------
public interface MAXSaleReturnLineItemIfc extends SaleReturnLineItemIfc {
	// public interface SaleReturnLineItemIfc extends SaleReturnLineItemIfc{
	/**
	 * revision number supplied by source-code control system
	 **/
	public static final String revisionNumber = "$Revision: 1.2 $";

	// ---------------------------------------------------------------------
	/**
	 * Initializes a SaleReturnLineItem object, setting item, tax rate, sales
	 * associate, registry attributes and return item attributes.
	 * <P>
	 * 
	 * @param item
	 *            PLU item
	 * @param quantity
	 *            item quantity
	 * @param tax
	 *            ItemTax object
	 * @param pSalesAssociate
	 *            default sales associate
	 * @param pRegistry
	 *            default registry
	 * @param pReturnItem
	 *            return item info
	 **/
	// ---------------------------------------------------------------------

	public void initialize(PLUItemIfc item, BigDecimal quantity, ItemTaxIfc tax, EmployeeIfc pSalesAssociate,
			RegistryIDIfc pRegistry, ReturnItemIfc pReturnItem);

	
	
	
	// ---------------------------------------------------------------------
	/**
	 * Calculates and sets item price.
	 * <P>
	 **/
	// ---------------------------------------------------------------------
	public void calculateLineItemPrice();

	// ---------------------------------------------------------------------
	/**
	 * Modify item tax.
	 * <P>
	 * 
	 * @param newRate
	 *            new tax rate
	 * @param reason
	 *            code
	 **/
	// ---------------------------------------------------------------------
	public void modifyItemTaxRate(double newRate, int reasonCode);

	// ---------------------------------------------------------------------
	/**
	 * Modify item price.
	 * <P>
	 * 
	 * @param newPrice
	 *            new price
	 * @param reason
	 *            code
	 **/
	// ---------------------------------------------------------------------
	public void modifyItemPrice(CurrencyIfc newPrice, int reasonCode);

	// ---------------------------------------------------------------------
	/**
	 * Modifies item registry and sets modified flag.
	 * <P>
	 * 
	 * @param newGift
	 *            new registry
	 * @deprecated As of release 4.0.0, replaced by
	 *             {@link #modifyItemRegistry(RegistryIDIfc)}
	 **/
	// ---------------------------------------------------------------------
	public void modifyItemGiftRegistry(GiftRegistry value);

	// ---------------------------------------------------------------------
	/**
	 * Modifies item registry and sets modified flag to requested value.
	 * <P>
	 * 
	 * @param value
	 *            new registry
	 * @deprecated As of release 4.0.0, replaced by
	 *             {@link #modifyItemRegistry(RegistryIDIfc, boolean)}
	 * @param modified
	 *            modified flag
	 **/
	// ---------------------------------------------------------------------
	public void modifyItemGiftRegistry(GiftRegistry value, boolean modified);

	// ---------------------------------------------------------------------
	/**
	 * Modifies item registry and sets modified flag.
	 * <P>
	 * 
	 * @param value
	 *            new registry
	 * @deprecated As of release 4.0.0, replaced by
	 *             {@link #modifyItemRegistry(RegistryIDIfc, boolean)}
	 **/
	// ---------------------------------------------------------------------
	public void modifyItemRegistry(RegistryIDIfc value);

	// ---------------------------------------------------------------------
	/**
	 * Modifies item registry and sets modified flag to requested value.
	 * <P>
	 * 
	 * @param value
	 *            new registry
	 * @param modified
	 *            modified flag
	 **/
	// ---------------------------------------------------------------------
	public void modifyItemRegistry(RegistryIDIfc value, boolean modified);

	// ---------------------------------------------------------------------
	/**
	 * Returns registry.
	 * <P>
	 * 
	 * @return registry
	 * @deprecated As of release 4.0.0, replaced by {@link #getRegistry()}.
	 **/
	// ---------------------------------------------------------------------
	public GiftRegistry getGiftRegistry();

	// ---------------------------------------------------------------------
	/**
	 * Returns registry.
	 * <P>
	 * 
	 * @return registry
	 **/
	// ---------------------------------------------------------------------
	public RegistryIDIfc getRegistry();

	// ---------------------------------------------------------------------
	/**
	 * Returns item price object.
	 * <P>
	 * 
	 * @return item price object
	 **/
	// ---------------------------------------------------------------------
	public ItemPriceIfc getItemPrice();

	// ---------------------------------------------------------------------
	/**
	 * Sets item price attribute.
	 * <P>
	 * 
	 * @param value
	 *            item price reference
	 **/
	// ---------------------------------------------------------------------
	public void setItemPrice(ItemPriceIfc value);

	// ---------------------------------------------------------------------
	/**
	 * Retrieves quantity returnable. This is equal to the item quantity less
	 * the item quantity returned.
	 * <P>
	 * 
	 * @return quantity returnable
	 **/
	// ---------------------------------------------------------------------
	public BigDecimal getQuantityReturnable();

	// ---------------------------------------------------------------------
	/**
	 * Retrieves quantity returned.
	 * <P>
	 * 
	 * @return quantity returned
	 **/
	// ---------------------------------------------------------------------
	public BigDecimal getQuantityReturnedDecimal();

	// ---------------------------------------------------------------------
	/**
	 * Sets quantity returned.
	 * <P>
	 * 
	 * @param qty
	 *            new quantity
	 **/
	// ---------------------------------------------------------------------
	public void setQuantityReturned(BigDecimal value);

	// ---------------------------------------------------------------------
	/**
	 * Returns a boolean indicating whether this is a sale line item. Tests for
	 * itemQuantity greater than 0.
	 * <P>
	 * 
	 * @return true if itemQuantity > 0
	 **/
	// ---------------------------------------------------------------------
	public boolean isSaleLineItem();

	// ---------------------------------------------------------------------
	/**
	 * Returns a boolean indicating whether this is a return line item. Tests
	 * for itemQuantity less than 0.
	 * <P>
	 * 
	 * @return true if itemQuantity < 0, the instance has a ReturnItemIfc
	 *         instance, and is not an instance of PriceAdjustmentLineItemIfc
	 **/
	// ---------------------------------------------------------------------
	public boolean isReturnLineItem();

	// ---------------------------------------------------------------------
	/**
	 * Returns a boolean indicating whether this is an order line item.
	 * 
	 * @return true if item is an order line item, false otherwise
	 **/
	// ---------------------------------------------------------------------
	public boolean isOrderItem();

	// ---------------------------------------------------------------------
	/**
	 * Returns a boolean indicating whether this item has a unit of measure
	 * other than the default. Tests for UnitID not equal to "UN".
	 * <P>
	 * 
	 * @return true if (! UnitID.equals("UN"))
	 **/
	// ---------------------------------------------------------------------
	public boolean isUnitOfMeasureItem();

	// ----------------------------------------------------------------------------
	/**
	 * Retrieves indicator item requires collection of a serial number.
	 * 
	 * @return indicator item requires collection of a serial number
	 **/
	// ----------------------------------------------------------------------------
	public boolean isSerializedItem();

	// --------------------------------------------------------------------------
	/**
	 * Returns true if this is a non-merchandise (service) item.
	 * 
	 * @return true if service item, false if not
	 **/
	// --------------------------------------------------------------------------
	public boolean isServiceItem();

	// ---------------------------------------------------------------------
	/**
	 * Returns PLU item.
	 * <P>
	 * 
	 * @return PLU item
	 **/
	// ---------------------------------------------------------------------
	public PLUItemIfc getPLUItem();

	// ---------------------------------------------------------------------
	/**
	 * Returns PLU item identifier.
	 * <P>
	 * 
	 * @return PLU item identifier
	 **/
	// ---------------------------------------------------------------------
	public String getPLUItemID();

	// ---------------------------------------------------------------------
	/**
	 * Returns PLU item serial number.
	 * <P>
	 * 
	 * @return PLU item serial number
	 **/
	// ---------------------------------------------------------------------
	public String getItemSerial();

	// ---------------------------------------------------------------------
	/**
	 * Sets PLU item.
	 * <P>
	 * 
	 * @param value
	 *            PLU item
	 **/
	// ---------------------------------------------------------------------
	public void setPLUItem(PLUItemIfc value);

	// ---------------------------------------------------------------------
	/**
	 * Sets PLU item ID.
	 * <P>
	 * 
	 * @param value
	 *            PLU item ID
	 **/
	// ---------------------------------------------------------------------
	public void setPLUItemID(String value);

	// ---------------------------------------------------------------------
	/**
	 * Sets PLU item serial number.
	 * <P>
	 * 
	 * @param value
	 *            PLU item serial number
	 **/
	// ---------------------------------------------------------------------
	public void setItemSerial(String value);

	// ---------------------------------------------------------------------
	/**
	 * Returns return item.
	 * <P>
	 * 
	 * @return return item
	 **/
	// ---------------------------------------------------------------------
	public ReturnItemIfc getReturnItem();

	// ---------------------------------------------------------------------
	/**
	 * Sets return item.
	 * <P>
	 * 
	 * @param value
	 *            return item
	 **/
	// ---------------------------------------------------------------------
	public void setReturnItem(ReturnItemIfc value);

	// ---------------------------------------------------------------------
	/**
	 * Returns registry-modified flag.
	 * <P>
	 * 
	 * @return registry-modified flag
	 * @deprecated As of release 4.0.0, replaced by
	 *             {@link #getRegistryModifiedFlag()}.
	 **/
	// ---------------------------------------------------------------------
	public boolean getGiftRegistryModifiedFlag();

	// ---------------------------------------------------------------------
	/**
	 * Sets registry modified flag.
	 * <P>
	 * 
	 * @param value
	 *            modified flag
	 * @deprecated As of release 4.0.0, replaced by
	 *             {@link #setRegistryModifiedFlag(boolean)}
	 **/
	// ---------------------------------------------------------------------
	public void setGiftRegistryModifiedFlag(boolean value);

	// ---------------------------------------------------------------------
	/**
	 * Returns registry-modified flag.
	 * <P>
	 * 
	 * @return registry-modified flag
	 **/
	// ---------------------------------------------------------------------
	public boolean getRegistryModifiedFlag();

	// ---------------------------------------------------------------------
	/**
	 * Sets registry modified flag.
	 * <P>
	 * 
	 * @param value
	 *            modified flag
	 **/
	// ---------------------------------------------------------------------
	public void setRegistryModifiedFlag(boolean value);

	// ---------------------------------------------------------------------
	/**
	 * Restores the object from the contents of the xml tree based on the
	 * current node property of the converter.
	 * <p>
	 * 
	 * @param converter
	 *            is the conversion utility
	 * @exception XMLConversionException
	 *                if translation fails
	 **/
	// ---------------------------------------------------------------------
	public void translateFromElement(XMLConverterIfc converter) throws XMLConversionException;

	// ---------------------------------------------------------------------
	/**
	 * Creates clone of this object.
	 * <P>
	 * 
	 * @return Object clone of this object
	 **/
	// ---------------------------------------------------------------------
	public Object clone();

	// ---------------------------------------------------------------------
	/**
	 * Returns journal string
	 * 
	 * @return journal string
	 * @deprecated in 6.0 due to difficulties with journaling markdowns and
	 *             discounts.
	 **/
	// ---------------------------------------------------------------------
	public String toJournalString(int discountType);

	// ----------------------------------------------------------------------
	/**
	 * This method journals the date information.
	 * 
	 * @param date
	 * @return
	 **/
	// ----------------------------------------------------------------------
	public String toJournalString(EYSDate date);

	// ----------------------------------------------------------------------
	/**
	 * This method journals the related item information.
	 * 
	 * @param date
	 * @param itemId
	 * @return
	 **/
	// ----------------------------------------------------------------------
	public String toJournalString(EYSDate date, String itemId);

	// ---------------------------------------------------------------------
	/**
	 * Returns journal string when removing an item.
	 * <P>
	 * 
	 * @return journal string when removing an item
	 * @deprecated in 6.0 due to difficulties with journaling markdowns and
	 *             discounts.
	 **/
	// ---------------------------------------------------------------------
	public String toJournalRemoveString(int discountType);

	// ---------------------------------------------------------------------
	/**
	 * Returns journal string when removing an item.
	 * <P>
	 * 
	 * @return journal string when removing an item
	 **/
	// ---------------------------------------------------------------------
	public String toJournalDeleteString();

	// ---------------------------------------------------------------------
	/**
	 * Returns journal string when removing an item.
	 * <P>
	 * 
	 * @return journal string when removing an item
	 * @deprecated in 6.0 due to difficulties with journaling markdowns and
	 *             discounts.
	 **/
	// ---------------------------------------------------------------------
	public String toJournalDeleteString(int discountType);

	// ---------------------------------------------------------------------
	/**
	 * Returns journal string when removing an item.
	 * <P>
	 * 
	 * @return journal string when removing an item
	 **/
	// ---------------------------------------------------------------------
	public String toJournalRemoveString();

	// ---------------------------------------------------------------------
	/**
	 * Journals discounts, if they exist.
	 * <P>
	 * 
	 * @param discount
	 *            /markdown to be journaled
	 * @return journal string when removing an item
	 **/
	// ---------------------------------------------------------------------
	public String toJournalManualDiscount(ItemDiscountStrategyIfc discount, boolean discountRemoved);

	// ---------------------------------------------------------------------
	/**
	 * Clears item discounts by percentage.
	 * <P>
	 * 
	 * @deprecated As of release 7.0.0, replaced by
	 *             {@link #clearItemDiscountsByPercentage(int, boolean)}
	 **/
	// ---------------------------------------------------------------------
	public void clearItemDiscountsByPercentage();

	// ---------------------------------------------------------------------
	/**
	 * Clears item discounts by percentage with a given basis and damage flag.
	 * <P>
	 * 
	 * @param basis
	 *            The assignment basis number
	 * @param damage
	 *            The damage flag
	 **/
	// ---------------------------------------------------------------------
	public void clearItemDiscountsByPercentage(int basis, boolean damage);

	// ---------------------------------------------------------------------
	/**
	 * Clears item discounts by percentage.
	 * <P>
	 * 
	 * @deprecated As of release 7.0.0, replaced by
	 *             {@link #clearItemDiscountsByPercentage(int, int, boolean)}
	 **/
	// ---------------------------------------------------------------------
	public void clearItemDiscountsByPercentage(int typeCode);

	// ---------------------------------------------------------------------
	/**
	 * Clears item discounts by percentage with a given type code, basis and
	 * damage flag.
	 * <P>
	 * 
	 * @param typeCode
	 *            int
	 * @param basis
	 *            The assignment basis number
	 * @param damage
	 *            The damage flag
	 **/
	// ---------------------------------------------------------------------
	public void clearItemDiscountsByPercentage(int typeCode, int basis, boolean damage);

	// ---------------------------------------------------------------------
	/**
	 * Clears item discounts by amount.
	 * <P>
	 * 
	 * @deprecated As of release 7.0.0, replaced by
	 *             {@link #clearItemDiscountsByAmount(int, boolean)}
	 **/
	// ---------------------------------------------------------------------
	public void clearItemDiscountsByAmount();

	// ---------------------------------------------------------------------
	/**
	 * Clears item discounts by amount with a given basis and damage flag.
	 * <P>
	 * 
	 * @param basis
	 *            The assignment basis number
	 * @param damage
	 *            The damage flag
	 **/
	// ---------------------------------------------------------------------
	public void clearItemDiscountsByAmount(int basis, boolean damage);

	// ---------------------------------------------------------------------
	/**
	 * Clears item discounts by amount.
	 * 
	 * @param int
	 *            typeCode
	 * @deprecated As of release 7.0.0, replaced by
	 *             {@link #clearItemDiscountsByAmount(int, int, boolean)}
	 **/
	// ---------------------------------------------------------------------
	public void clearItemDiscountsByAmount(int typeCode);

	// ---------------------------------------------------------------------
	/**
	 * Clears item discounts by amount with a given type code, basis and damage
	 * flag.
	 * 
	 * @param typeCode
	 *            int
	 * @param basis
	 *            The assignment basis number
	 * @param damage
	 *            The damage flag
	 **/
	// ---------------------------------------------------------------------
	public void clearItemDiscountsByAmount(int typeCode, int basis, boolean damage);

	// ---------------------------------------------------------------------
	/**
	 * Clears item markdowns by percentage.
	 * <P>
	 **/
	// ---------------------------------------------------------------------
	public void clearItemMarkdownsByPercentage();

	// ---------------------------------------------------------------------
	/**
	 * Clears item markdowns by percentage.
	 * <P>
	 **/
	// ---------------------------------------------------------------------
	public void clearItemMarkdownsByPercentage(int typeCode);

	// ---------------------------------------------------------------------
	/**
	 * Clears item markdowns by amount.
	 * <P>
	 **/
	// ---------------------------------------------------------------------
	public void clearItemMarkdownsByAmount();

	// ---------------------------------------------------------------------
	/**
	 * Clears item markdowns by amount.
	 * 
	 * @param int
	 *            typeCode
	 **/
	// ---------------------------------------------------------------------
	public void clearItemMarkdownsByAmount(int typeCode);

	// ---------------------------------------------------------------------
	/**
	 * Clears ItemDiscountStrategyIfcs with corresponding discountRuleID.
	 * 
	 * @param String
	 *            id
	 **/
	// ---------------------------------------------------------------------
	public void clearItemDiscounts(String discountRuleID);

	// ----------------------------------------------------------------------------
	/**
	 * Retrieves itemTaxMethod attribute.
	 * <P>
	 * 
	 * @return itemTaxMethod attribute
	 **/
	// ---------------------------------------------------------------------
	public int getItemTaxMethod();

	// ---------------------------------------------------------------------
	/**
	 * Sets itemTaxMethod attribute.
	 * <P>
	 **/
	// ---------------------------------------------------------------------
	public void setItemTaxMethod(int flag);

	// ---------------------------------------------------------------------
	/**
	 * Tests the item tax amount to see if it is greater than the extended
	 * selling price of the item.
	 * <P>
	 * 
	 * @return boolean
	 **/
	// ---------------------------------------------------------------------
	public boolean taxExceedsSellingPrice();

	// ---------------------------------------------------------------------
	/**
	 * Tests the item's discount amount to see if it is greater than the
	 * extended selling price of the item.
	 * <P>
	 * 
	 * @return boolean
	 **/
	// ---------------------------------------------------------------------
	public boolean discountExceedsSellingPrice();

	// --------------------------------------------------------------------------
	/*
	 * Returns the advanced pricing rule which was applied to this target.
	 * 
	 * @return AdvancedPricingRuleIfc
	 */
	// --------------------------------------------------------------------------
	public ItemDiscountStrategyIfc getAdvancedPricingDiscount();

	// --------------------------------------------------------------------------
	/*
	 * Removes an advanced pricing discount applied to this target.
	 */
	// --------------------------------------------------------------------------
	public void removeAdvancedPricingDiscount();

	// ---------------------------------------------------------------------
	/**
	 * Returns the original price for a single item.
	 * <p>
	 * 
	 * @return CurrencyIfc
	 **/
	// ---------------------------------------------------------------------
	public CurrencyIfc getSellingPrice();

	// ---------------------------------------------------------------------
	/**
	 * Returns the extended price before discounts. (original price * item
	 * quantity)
	 * 
	 * @return CurrencyIfc
	 **/
	// ---------------------------------------------------------------------
	public CurrencyIfc getExtendedSellingPrice();

	// ---------------------------------------------------------------------
	/**
	 * Returns the extended price after discounts. ((original price * item
	 * quantity) - item Discount total)
	 * 
	 * @return CurrencyIfc
	 **/
	// ---------------------------------------------------------------------
	public CurrencyIfc getExtendedDiscountedSellingPrice();

	// ---------------------------------------------------------------------
	/**
	 * Returns the item discount total
	 * <p>
	 * 
	 * @return CurrencyIfc
	 **/
	// ---------------------------------------------------------------------
	public CurrencyIfc getItemDiscountTotal();

	// ---------------------------------------------------------------------
	/**
	 * Returns the total discount amount for discounts matching specified
	 * parameters.
	 * <p>
	 * 
	 * @return CurrencyIfc
	 **/
	// ---------------------------------------------------------------------
	public CurrencyIfc getDiscountAmount(int discountScope, int assignmentBasis);

	// ---------------------------------------------------------------------
	/**
	 * Retrieves array of item discounts by percentage.
	 * <P>
	 * 
	 * @return array of disc item discount objects, null if not found
	 **/
	// ---------------------------------------------------------------------
	public ItemDiscountStrategyIfc[] getItemDiscountsByPercentage();

	// ---------------------------------------------------------------------
	/**
	 * Retrieves array of item discounts by amount.
	 * <P>
	 * 
	 * @return array of disc item discount objects, null if not found
	 **/
	// ---------------------------------------------------------------------
	public ItemDiscountStrategyIfc[] getItemDiscountsByAmount();

	// ---------------------------------------------------------------------
	/**
	 * Retrieves array of return item discounts.
	 * <P>
	 * 
	 * @return array of disc item discount objects, null if not found
	 **/
	// ---------------------------------------------------------------------
	public ItemDiscountStrategyIfc[] getReturnItemDiscounts();

	// ---------------------------------------------------------------------
	/**
	 * Retrieves array of transaction discount audit objects.
	 * <P>
	 * 
	 * @return array of transasction discount audit objects, null if not found
	 **/
	// ---------------------------------------------------------------------
	public ItemDiscountStrategyIfc[] getTransactionDiscounts();

	// ---------------------------------------------------------------------
	/**
	 * Returns true if specified classification ID is in list, false otherwise
	 * 
	 * @return true if specified classification ID is in list, false otherwise
	 *         <P>
	 **/
	// ---------------------------------------------------------------------
	public boolean isClassifiedAs(String classificationID);

	// --------------------------------------------------------------------------
	/**
	 * Returns a String value that is to be used to compare an attribute of this
	 * item with the criteria for an advanced pricing rule in order to test for
	 * discount source or target equality. The String returned is based on the
	 * value of the comparisonBasis argument.
	 * 
	 * @see DiscountRuleConstantsIfc#COMPARISON_BASIS_UNINITIALIZED
	 * @see DiscountRuleConstantsIfc#COMPARISON_BASIS_ITEM_ID
	 * @see DiscountRuleConstantsIfc#COMPARISON_BASIS_DEPARTMENT_ID
	 * @param int
	 *            comparisonBasis - the comparison basis to use
	 * @return String value to be used for comparison
	 **/
	public String getComparator(int comparisonBasis);

	// ----------------------------------------------------------------------------
	/**
	 * Returns true if this item is a kit header, false otherwise.
	 * 
	 * @return boolean
	 **/
	// ----------------------------------------------------------------------------
	public boolean isKitHeader();

	// ----------------------------------------------------------------------------
	/**
	 * Returns true if this item is a kit component, false otherwise.
	 * 
	 * @return boolean
	 **/
	// ----------------------------------------------------------------------------
	public boolean isKitComponent();

	// ----------------------------------------------------------------------------
	/**
	 * Retrieves the kit header reference if the line item is part of a kit or
	 * -1 if it is not.
	 * 
	 * @return int ID
	 **/
	// ----------------------------------------------------------------------------
	public int getKitHeaderReference();

	// ----------------------------------------------------------------------------
	/**
	 * Sets the kit header reference value.
	 * 
	 * @param int
	 *            referenceValue
	 **/
	// ----------------------------------------------------------------------------
	public void setKitHeaderReference(int referenceValue);

	// ---------------------------------------------------------------------
	/**
	 * Returns the text description that is to be used for the UI and the
	 * receipt.
	 * <p>
	 * 
	 * @return String description
	 **/
	// ---------------------------------------------------------------------
	public String getItemDescription();

	// ---------------------------------------------------------------------
	/**
	 * Returns the ID for this item.
	 * <p>
	 * 
	 * @return String identifier
	 **/
	// ---------------------------------------------------------------------
	public String getItemID();

	// ---------------------------------------------------------------------
	/**
	 * Retrieves item quantity.
	 * <P>
	 * 
	 * @return item quantity
	 **/
	// ---------------------------------------------------------------------
	public BigDecimal getItemQuantityDecimal();

	// ---------------------------------------------------------------------
	/**
	 * Returns the item tax mode
	 * <p>
	 * 
	 * @return int tax mode
	 **/
	// ---------------------------------------------------------------------
	public int getTaxMode();

	// ---------------------------------------------------------------------
	/**
	 * Returns the taxable flag
	 * <p>
	 * 
	 * @return boolean
	 **/
	// ---------------------------------------------------------------------
	public boolean getTaxable();

	// --------------------------------------------------------------------------
	/**
	 * Determines the tax status indicator flag.
	 * 
	 * @return a string value to describe taxability status
	 */
	// --------------------------------------------------------------------------
	public String getTaxStatusDescriptor();

	// ---------------------------------------------------------------------
	/**
	 * Returns true if this item can be sent, false otherwise.
	 * 
	 * @return boolean
	 **/
	// ---------------------------------------------------------------------
	public boolean isEligibleForSend();

	// ---------------------------------------------------------------------
	/**
	 * Returns item send flag.
	 * <P>
	 * 
	 * @return itemSendFlag attribute
	 **/
	// ---------------------------------------------------------------------
	public boolean getItemSendFlag();

	// ----------------------------------------------------------------------------
	/**
	 * Retrieves indicator item is eligible for special order.
	 * 
	 * @return indicator item is eligible for special order
	 **/
	// ----------------------------------------------------------------------------
	public boolean isSpecialOrderEligible();

	// ---------------------------------------------------------------------
	/**
	 * Sets item send flag attribute.
	 * <P>
	 * 
	 * @param value
	 *            send flag
	 **/
	// ---------------------------------------------------------------------
	public void setItemSendFlag(boolean value);

	// ---------------------------------------------------------------------
	/**
	 * Sets send label count associated with this line item
	 * <P>
	 * 
	 * @param sendLabelCount
	 *            send label count
	 **/
	// ---------------------------------------------------------------------
	public void setSendLabelCount(int sendLabelCount);

	// ---------------------------------------------------------------------
	/**
	 * Gets send label count associated with this line item
	 * <P>
	 * 
	 * @return int send label count
	 **/
	// ---------------------------------------------------------------------
	public int getSendLabelCount();

	// --------------------------------------------------------------------------
	/*
	 * Sets the gift receipt flag for this item. This flag indicates whether the
	 * item needs to have a gift receipt printed.
	 * 
	 * @param boolean indicating if a gift receipt should be printed.
	 */
	// --------------------------------------------------------------------------
	public void setGiftReceiptItem(boolean value);

	// --------------------------------------------------------------------------
	/*
	 * Returns the gift receipt flag for this item.
	 * 
	 * @return boolean indicating if a gift receipt should be printed.
	 */
	// --------------------------------------------------------------------------
	public boolean isGiftReceiptItem();

	// --------------------------------------------------------------------------
	/*
	 * Sets the alteration item flag for this item. This flag indicates whether
	 * the item needs to have an alteration receipt printed.<P>
	 * 
	 * @param boolean indicating if an alteration receipt should be printed.
	 */
	// --------------------------------------------------------------------------
	public void setAlterationItemFlag(boolean value);

	// --------------------------------------------------------------------------
	/*
	 * Gets the alteration item flag for this item. This flag indicates whether
	 * the item needs to have an alteration receipt printed.<P>
	 * 
	 * @return true if an alteration receipt should be printed.
	 */
	// --------------------------------------------------------------------------
	public boolean getAlterationItemFlag();

	// --------------------------------------------------------------------------
	/*
	 * Returns the alteration flag for this item.<P>
	 * 
	 * @return boolean indicating if an alteration receipt should be printed.
	 */
	// --------------------------------------------------------------------------
	public boolean isAlterationItem();

	// ---------------------------------------------------------------------
	/**
	 * Returns order item status.
	 * <P>
	 * 
	 * @return order item status
	 **/
	// ---------------------------------------------------------------------
	public OrderItemStatusIfc getOrderItemStatus();

	// ---------------------------------------------------------------------
	/**
	 * Sets order item status.
	 * <P>
	 * 
	 * @param value
	 *            order item status
	 **/
	// ---------------------------------------------------------------------
	public void setOrderItemStatus(OrderItemStatusIfc value);

	// ---------------------------------------------------------------------
	/**
	 * Returns line item reference.
	 * <P>
	 * 
	 * @return line item reference
	 **/
	// ---------------------------------------------------------------------
	public String getLineReference();

	// ---------------------------------------------------------------------
	/**
	 * Sets order item status.
	 * <P>
	 * 
	 * @param value
	 *            order item status
	 **/
	// ---------------------------------------------------------------------
	public void setLineReference(String value);

	// ---------------------------------------------------------------------
	/**
	 * Returns order item reference.
	 * <P>
	 * 
	 * @return order item reference
	 **/
	// ---------------------------------------------------------------------
	public int getOrderLineReference();

	// ---------------------------------------------------------------------
	/**
	 * Sets order item reference.
	 * <P>
	 * 
	 * @param value
	 *            order item reference
	 **/
	// ---------------------------------------------------------------------
	public void setOrderLineReference(int value);

	// ---------------------------------------------------------------------
	/**
	 * Returns entry method.
	 * <P>
	 * 
	 * @return entry method
	 **/
	// ---------------------------------------------------------------------
	public EntryMethod getEntryMethod();

	// ---------------------------------------------------------------------
	/**
	 * Sets entry method.
	 * <P>
	 * 
	 * @param value
	 *            entry method
	 **/
	// ---------------------------------------------------------------------
	public void setEntryMethod(int value);

	// ---------------------------------------------------------------------
	/**
	 * Retrieves Pos Item identifier.
	 * <P>
	 * 
	 * @return Pos Item identifier
	 **/
	// ---------------------------------------------------------------------
	public String getPosItemID();

	// ----------------------------------------------------------------------
	/**
	 * This method checks to see if the item is a gift card isssue. We assume it
	 * is an issue if the status is active as apposed to reload.
	 * 
	 * @return
	 **/
	// ----------------------------------------------------------------------
	public boolean isGiftCardIssue();

	// ----------------------------------------------------------------------
	/**
	 * This method checks to see if the item is a gift card reload.
	 * 
	 * @return whether the item is gift card reload
	 **/
	// ----------------------------------------------------------------------
	public boolean isGiftCardReload();

	/**
	 * Returns if item is a giftcard merch type
	 * 
	 * @return
	 */
	public boolean isGiftItem();

	// ----------------------------------------------------------------------
	/**
	 * Check through all discount to see if there is a damage discount related
	 * to this item.
	 * 
	 * @return isDamageDiscount boolean
	 **/
	// ----------------------------------------------------------------------
	public boolean hasDamageDiscount();

	// ----------------------------------------------------------------------
	/**
	 * Checks through all discounts to see if there is an employee discount
	 * related to this item.
	 * 
	 * @return boolean true only if this item has an employee discount applied
	 */
	// ----------------------------------------------------------------------
	public boolean hasEmployeeDiscount();

	// ----------------------------------------------------------------------
	/**
	 * Returns the Item Type for the PLU item this line item represents.
	 * 
	 * @return integer item type
	 **/
	// ----------------------------------------------------------------------
	public int getItemType();

	// ---------------------------------------------------------------------
	/**
	 * @return Returns the code of the item size that is associated here.
	 */
	// ---------------------------------------------------------------------
	public String getItemSizeCode();

	// ---------------------------------------------------------------------
	/**
	 * The item size code reflects an encoded string that is in the database
	 * that shows the size of an item
	 * 
	 * @param itemSizeCode
	 *            The itemSizeCode to set.
	 */
	// ---------------------------------------------------------------------
	public void setItemSizeCode(String itemSizeCode);

	// ---------------------------------------------------------------------
	/**
	 * Check if this item meets the returnable criteria
	 * 
	 * @return is item returnable
	 */
	// ---------------------------------------------------------------------
	public boolean isReturnable();

	// ----------------------------------------------------------------------------
	/**
	 * Returns true if this item is a price adjustment item, false otherwise.
	 * 
	 * @return boolean
	 */
	// ----------------------------------------------------------------------------
	public boolean isPriceAdjustmentLineItem();

	// --------------------------------------------------------------------------
	/**
	 * Returns a flag indicating whether or not this SaleReturnLineItem is a
	 * part of a price adjustment
	 * 
	 * @return flag indicating whether or not this SaleReturnLineItem is a part
	 *         of a price adjustment
	 * @see com.extendyourstore.domain.lineitem.SaleReturnLineItemIfc#isPartOfPriceAdjustment()
	 */
	// --------------------------------------------------------------------------
	public boolean isPartOfPriceAdjustment();

	// --------------------------------------------------------------------------
	/**
	 * Manually sets flag to indicate whether or not an item is part of a price
	 * adjustment See the isPartOfPriceAdjustment() method for more details as
	 * to how this status is evaluated.
	 * 
	 * Normally this method is not used. Instead isPartOfPriceAdjustment() uses
	 * other data elements to determine the status of this item. This method is
	 * intended to be used when you need to explicitly change the status of this
	 * item with regards to price adjustments
	 * 
	 * @param isPartOfPriceAdjustment
	 *            flag indicating whether or not this item should be viewed as a
	 *            part of a price adjustment
	 */
	// --------------------------------------------------------------------------
	public void setIsPartOfPriceAdjustment(boolean isPartOfPriceAdjustment);

	// ----------------------------------------------------------------------------
	/**
	 * Sets the isPriceAdjustmentLineItem flag in order to indicate whether this
	 * line item is price adjustable. This method is meant to be used only for
	 * cloning and interested subclasses.
	 * 
	 * @param isPriceAdjustmentLineItem
	 *            whether this item is price adjustable
	 */
	// ----------------------------------------------------------------------------
	public void setIsPriceAdjustmentLineItem(boolean isPriceAdjustmentLineItem);

	// --------------------------------------------------------------------------
	/**
	 * Returns the price adjutment line item reference
	 * 
	 * @return Returns the priceAdjustmentReference.
	 **/
	// --------------------------------------------------------------------------
	public int getPriceAdjustmentReference();

	// --------------------------------------------------------------------------
	/**
	 * Sets the price adjustment line item reference
	 * 
	 * @param priceAdjustmentReference
	 *            The priceAdjustmentReference to set.
	 **/
	// --------------------------------------------------------------------------
	public void setPriceAdjustmentReference(int priceAdjustmentReference);

	// ---------------------------------------------------------------------
	/**
	 * Sets item quantity, but does not re-calculate totals.
	 * <P>
	 * 
	 * @param value
	 *            new quantity
	 * @see #modifyItemQuantity(BigDecimal).
	 **/
	// ---------------------------------------------------------------------
	public void setItemQuantity(BigDecimal value);

	// --------------------------------------------------------------------------
	/**
	 * Returns original line number. Not affiliated with the original line
	 * number in return item.
	 * 
	 * @return Returns the originalLineNumber or -1 if does not apply
	 **/
	// --------------------------------------------------------------------------
	public int getOriginalLineNumber();

	// --------------------------------------------------------------------------
	/**
	 * Sets original line number. Not affiliated with the original line number
	 * in return item.
	 * 
	 * @param originalLineNumber
	 *            The originalLineNumber to set.
	 **/
	// --------------------------------------------------------------------------
	public void setOriginalLineNumber(int originalLineNumber);

	// --------------------------------------------------------------------------
	/**
	 * Returns original transaction number. Not affiliated with the original
	 * transaction number in return item.
	 * 
	 * @return Returns the transactionSequenceNumber or -1 if does not apply
	 **/
	// --------------------------------------------------------------------------
	public long getOriginalTransactionSequenceNumber();

	// --------------------------------------------------------------------------
	/**
	 * Sets original transaction number. Not affiliated with the original
	 * transaction number in return item.
	 * 
	 * @param transactionSequenceNumber
	 *            The transactionSequenceNumber to set.
	 **/
	// --------------------------------------------------------------------------
	public void setOriginalTransactionSequenceNumber(long transactionSequenceNumber);

	/**
	 * Set whether or not this line item was read in from an already tendered
	 * transaction
	 * 
	 * @param value
	 *            true or false
	 */
	public void setFromTransaction(boolean value);

	/**
	 * Get whether or not this line item was read in from an already tendered
	 * transaction
	 * 
	 * @return true or false
	 */
	public boolean isFromTransaction();

	/**
	 * Tell whether or not this item is returnable, assuming it is a related
	 * item.
	 * 
	 * @return true or false
	 * @since NEP67
	 */
	public boolean isRelatedItemReturnable();

	/**
	 * Set this related item as being returnable or not
	 * 
	 * @param value
	 *            true or falsed
	 * @since NEP67
	 */
	public void setRelatedItemReturnable(boolean value);

	/**
	 * Set the sequence number this line item is related to. This should be -1
	 * if this is not a related item.
	 * 
	 * @param seqNum
	 *            Sequence number.
	 * @since NEP67
	 */
	public void setRelatedItemSequenceNumber(int seqNum);

	/**
	 * Get the sequence number this line item is related to. This should be -1
	 * if this is not a related item.
	 * 
	 * @return sequence number
	 * @since NEP67
	 */
	public int getRelatedItemSequenceNumber();

	/**
	 * If this object is a related item, this sets whether or not its
	 * deleteable. This attribute is persisted to the database, but is not in
	 * the POSLog. It must be stored only because of suspend/retrieve
	 * transactions.
	 * 
	 * @param relatedItemDeleteable
	 * @since NEP67
	 */
	public void setRelatedItemDeleteable(boolean relatedItemDeleteable);

	/**
	 * If this object is a related item, this sets whether or not its
	 * deleteable. This attribute is persisted to the database, but is not in
	 * the POSLog. It must be stored only because of suspend/retrieve
	 * transactions.
	 * 
	 * @since NEP67
	 * @return Whether or not this related item is deleteable
	 */
	public boolean isRelatedItemDeleteable();

	// ----------------------------------------------------------------------
	/**
	 * Adds a related item line item to the vector.
	 * 
	 * @param lineItem
	 **/
	// ----------------------------------------------------------------------
	public void addRelatedItemLineItem(SaleReturnLineItemIfc lineItem);

	// ----------------------------------------------------------------------
	/**
	 * Returns the vector of related item line items.
	 * 
	 * @return
	 **/
	// ----------------------------------------------------------------------
	public SaleReturnLineItemIfc[] getRelatedItemLineItems();

	// ----------------------------------------------------------------------
	/**
	 * Sets the related item line items.
	 * 
	 * @param relatedItems
	 **/
	// ----------------------------------------------------------------------
	public void setRelatedItemLineItems(SaleReturnLineItemIfc[] relatedItems);

	// ----------------------------------------------------------------------
	/**
	 * Retuns all the promotionLineItems for the Sale Return Line Item
	 * 
	 * @return Returns the promotionLineItems.
	 */
	// ----------------------------------------------------------------------
	public PromotionLineItemIfc[] getPromotionLineItems();

	// ----------------------------------------------------------------------
	/**
	 * Adds a Promotion Line Item to the Sale Return Line Item
	 * 
	 * @param promotionLineItem
	 */
	// ----------------------------------------------------------------------
	public void addPromotionLineItem(PromotionLineItemIfc promotionLineItem);

	/**
	 * Returns the LineItemTaxBreakUpDetails
	 * 
	 * @return LineItemTaxBreakUpDetail
	 */
	public MAXLineItemTaxBreakUpDetailIfc[] getLineItemTaxBreakUpDetails();

	/**
	 * Sets the LineItemTaxBreakUpDetails
	 * 
	 * @param LineItemTaxBreakUpDetail
	 *            []
	 */
	public void setLineItemTaxBreakUpDetails(MAXLineItemTaxBreakUpDetailIfc[] taxBreakUpDetails);

	public void setExcList(List excList);

	public String getBestDealWinnerName();

	public void setBestDealWinnerName(String bestDealWinnerName);

	// added by Izhar
	public CurrencyIfc getDiscountAmountforRTLOG();

	public boolean isTargetIdentifier();

	public void setTargetIdentifier(boolean targetIdentifier);

	public void setDiscountAmountforRTLOG(CurrencyIfc discountAmountforRTLOG);

	public CurrencyIfc getAmountVFP();

	public void setAmountVFP(CurrencyIfc amountVFP);

	public String getVendorID();

	public void setVendorID(String vendorID);

	// Added by vaibhav
	public CurrencyIfc getPromoDiscountForReceipt();

	public void setPromoDiscountForReceipt(CurrencyIfc promoDiscountForReceipt);

	// end
	/** Changes for Rev 1.1 : Starts **/
	public String getEmployeeDiscountID();

	public void setEmployeeDiscountID(String employeeDiscountID);

	public String getDiscountPercentage();

	public void setDiscountPercentage(String discountPercentage);
	/** Changes for Rev 1.1 : Ends **/
	
	public boolean isVatCollectionApplied();

	public void setVatCollectionApplied(boolean vatCollectionApplied);


	public BigDecimal getVatCollectionAmount();


	public void setVatCollectionAmount(BigDecimal vatCollectionAmount);
	
	public boolean isVatExtraApplied();

	public BigDecimal vatExtraAmount = new BigDecimal(0.00);

	public BigDecimal getVatExtraAmount();

	public void setVatExtraAmount(BigDecimal vatExtraAmount);

	
	public void setVatExtraApplied(boolean isVatExtraApplied);

	public boolean isExtendedPriceModified();

	public void setExtendedPriceModified(boolean isExtendedPriceModified);

	//Changes for Rev 1.1 : Starts
	public String getComparator(HashMap map);
	//Changes for Rev 1.1 : Ends

	//changes for rev 1.2
	public int getLineNumberonReceipt();
	//changes for rev 1.3
	public CurrencyIfc getAmountPrintedOnReceipt();
		
	//Change Starts for Rev 1.4
	public String getTaxType();
	public void setTaxType(String taxType);
	public String getProportionatePrice();
	public void setProportionatePrice(String proportionatePrice);
	public void setHSNNumber(String hsnNumner);
	public String getHSNNumber();
	
	public void setliquom(String liquom);
	public String getliquom();
	
	public void setliqcat(String liqcat);
	public String getliqcat();
	
	public String getScansheetCategoryID();
	public String getScansheetCategoryDesc();
	public void setScansheetCategoryID(String CategoryID);
	public void setScansheetCategoryDesc(String scanCategoryDesc);
		//Change End for Rev 1.4
	
	//public float getLiqQuantity();
	//public void setLiqQuantity(float liqQuantity);
} // end interface SaleReturnLineItemIfc
