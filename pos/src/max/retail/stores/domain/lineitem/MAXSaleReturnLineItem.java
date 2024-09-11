/********************************************************************************
 *   
 *	Copyright (c) 2016 - 2017 MAX Hypermarket, Inc    All Rights Reserved.
 *  Rev 1.7		May 04, 2017		Kritica Agarwal 	GST Changes
 *  Rev	1.6 	Mar 15, 2017		Hitesh Dua			Capillary coupon receipt 
 *  Rev	1.5 	Mar 02, 2017		Hitesh Dua			Check for gift cert and gift card item 
 *  Rev	1.4 	feb 20, 2017		Hitesh Dua			Item level % discount with 2 or more qty, In receipt item level discount per unit is printing wrong. 
 *  Rev	1.3 	feb 01, 2017		Hitesh Dua			added getAmountPrintedOnReceipt method to Print Selling number on receipt 
 *  Rev	1.2 	Dec 08, 2016		Hitesh Dua			added getLineNumberonReceipt to Print line number on receipt
 *	Rev	1.1 	Nov 07, 2016		Mansi Goel			Changes for Discount Rule FES
 *	Rev	1.0 	Aug 16, 2016		Nitesh Kumar		Changes for Code Merging	
 *
 ********************************************************************************/

package max.retail.stores.domain.lineitem;

// java imports
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;

import org.apache.log4j.Logger;
import org.w3c.dom.Element;

import max.retail.stores.domain.MAXUtils.MAXUtils;
import max.retail.stores.domain.discount.MAXDiscountRuleConstantsIfc;
import max.retail.stores.domain.stock.MAXPLUItemIfc;
import max.retail.stores.domain.utility.MAXEntryMethodConstantsIfc;
import oracle.retail.stores.commerceservices.common.currency.CurrencyIfc;
import oracle.retail.stores.common.utility.LocaleMap;
import oracle.retail.stores.common.utility.LocalizedCodeIfc;
import oracle.retail.stores.domain.DomainGateway;
import oracle.retail.stores.domain.discount.DiscountRuleConstantsIfc;
import oracle.retail.stores.domain.discount.DiscountRuleIfc;
import oracle.retail.stores.domain.discount.ItemDiscountStrategyIfc;
import oracle.retail.stores.domain.discount.ItemTransactionDiscountAudit;
import oracle.retail.stores.domain.discount.PromotionLineItemIfc;
import oracle.retail.stores.domain.discount.TransactionDiscountStrategyIfc;
import oracle.retail.stores.domain.employee.Employee;
import oracle.retail.stores.domain.employee.EmployeeIfc;
import oracle.retail.stores.domain.financial.FinancialTotalsIfc;
import oracle.retail.stores.domain.financial.TaxTotals;
import oracle.retail.stores.domain.financial.TaxTotalsContainerIfc;
import oracle.retail.stores.domain.financial.TaxTotalsIfc;
import oracle.retail.stores.domain.giftregistry.GiftRegistry;
import oracle.retail.stores.domain.lineitem.ItemPriceIfc;
import oracle.retail.stores.domain.lineitem.ItemTax;
import oracle.retail.stores.domain.lineitem.ItemTaxIfc;
import oracle.retail.stores.domain.lineitem.OrderItemStatusIfc;
import oracle.retail.stores.domain.lineitem.ReturnItem;
import oracle.retail.stores.domain.lineitem.ReturnItemIfc;
import oracle.retail.stores.domain.lineitem.SaleReturnLineItem;
import oracle.retail.stores.domain.lineitem.SaleReturnLineItemIfc;
import oracle.retail.stores.domain.registry.RegistryIDIfc;
import oracle.retail.stores.domain.stock.GiftCardPLUItemIfc;
import oracle.retail.stores.domain.stock.GiftCertificateItem;
import oracle.retail.stores.domain.stock.GiftCertificateItemIfc;
import oracle.retail.stores.domain.stock.ItemClassificationConstantsIfc;
import oracle.retail.stores.domain.stock.ItemClassificationIfc;
import oracle.retail.stores.domain.stock.PLUItem;
import oracle.retail.stores.domain.stock.PLUItemIfc;
import oracle.retail.stores.domain.stock.ProductGroupConstantsIfc;
import oracle.retail.stores.domain.tax.ReturnTaxCalculatorIfc;
import oracle.retail.stores.domain.tax.ReverseItemTaxRuleIfc;
import oracle.retail.stores.domain.tax.ReverseTaxCalculatorIfc;
import oracle.retail.stores.domain.tax.RunTimeTaxRuleIfc;
import oracle.retail.stores.domain.tax.TaxConstantsIfc;
import oracle.retail.stores.domain.tax.TaxIfc;
import oracle.retail.stores.domain.tax.TaxInformationContainerIfc;
import oracle.retail.stores.domain.tax.TaxInformationIfc;
import oracle.retail.stores.domain.tax.TaxRuleIfc;
import oracle.retail.stores.domain.utility.CodeConstantsIfc;
import oracle.retail.stores.domain.utility.DomainUtil;
import oracle.retail.stores.domain.utility.EYSDate;
import oracle.retail.stores.domain.utility.EYSStatusIfc;
import oracle.retail.stores.domain.utility.EntryMethod;
import oracle.retail.stores.domain.utility.GiftCardIfc;
import oracle.retail.stores.domain.utility.LocaleConstantsIfc;
import oracle.retail.stores.foundation.utility.Util;
import oracle.retail.stores.foundation.utility.xml.XMLConversionException;
import oracle.retail.stores.foundation.utility.xml.XMLConverterIfc;

//--------------------------------------------------------------------------
/**
 * Line item for sale or return.
 * <P>
 * 
 * @version $Revision: 1.4 $
 **/
// --------------------------------------------------------------------------
public class MAXSaleReturnLineItem extends SaleReturnLineItem
		implements MAXSaleReturnLineItemIfc, MAXDiscountRuleConstantsIfc {
	//private static final long serialVersionUID = -5963907420463410104L;
	private static final long serialVersionUID = -2565853519293402864L;

	/**
	 * The logger to which log messages will be sent.
	 **/
	protected static Logger logger = Logger.getLogger(max.retail.stores.domain.lineitem.MAXSaleReturnLineItem.class);
	
	//protected ItemPriceIfc itemPrice =null;

	/**
	 * revision number supplied by source-code control system
	 **/
	public static final String revisionNumber = "$Revision: 1.4 $";

	// Added by vaibhav
	protected CurrencyIfc promoDiscountForReceipt = null;
	
	protected boolean isVatExtraApplied;

	/** rev 1.3 change starts */
	protected boolean isExtendedPriceModified = false;
	/* rev 1.5 added vat extra dept wise */
	protected boolean vatCollectionApplied;
	public BigDecimal vatCollectionAmount=new BigDecimal(0.00);
	
	protected String shippingMethodType;
	/*
	 * protected boolean isPriceOverrideRequired = false;
	 * 
	 * public boolean isPriceOverrideRequired() { return
	 * isPriceOverrideRequired; }
	 * 
	 * public void setPriceOverrideRequired(boolean isPriceOverrideRequired) {
	 * this.isPriceOverrideRequired = isPriceOverrideRequired; }
	 */

	public String getShippingMethodType() {
		return shippingMethodType;
	}

	public void setShippingMethodType(String shippingMethodType) {
		this.shippingMethodType = shippingMethodType;
	}

	public CurrencyIfc getPromoDiscountForReceipt() {
		return promoDiscountForReceipt;
	}

	public void setPromoDiscountForReceipt(CurrencyIfc promoDiscountForReceipt) {
		this.promoDiscountForReceipt = promoDiscountForReceipt;
	}

	// end
	protected List excList = new ArrayList();

	public List getExcList() {
		return excList;
	}

	public void setExcList(List excList) {
		this.excList = excList;
	}

	/**
	 * Rev 1.2 changes start here
	 */
	protected String bestDealWinnerName = null;

	public String getBestDealWinnerName() {
		return bestDealWinnerName;
	}

	public void setBestDealWinnerName(String bestDealWinnerName) {
		this.bestDealWinnerName = bestDealWinnerName;
	}
	
	protected boolean targetIdentifier = false;

	public boolean isTargetIdentifier() {
		return targetIdentifier;
	}

	public void setTargetIdentifier(boolean targetIdentifier) {
		this.targetIdentifier = targetIdentifier;
	}

	protected CurrencyIfc discountAmountforRTLOG = null;
	protected CurrencyIfc amountVFP = null;
	protected String vendorID = null;

	public String getVendorID() {
		return vendorID;
	}

	public void setVendorID(String vendorID) {
		this.vendorID = vendorID;
	}

	public CurrencyIfc getAmountVFP() {
		return amountVFP;
	}

	public void setAmountVFP(CurrencyIfc amountVFP) {
		this.amountVFP = amountVFP;
	}

	public CurrencyIfc getDiscountAmountforRTLOG() {
		return discountAmountforRTLOG;
	}

	public void setDiscountAmountforRTLOG(CurrencyIfc discountAmountforRTLOG) {
		this.discountAmountforRTLOG = discountAmountforRTLOG;
	}

	protected List bdwList = new ArrayList();

	public List getBdwList() {
		return bdwList;
	}

	public void setBdwList(List bdwList) {
		this.bdwList = bdwList;
	}

	/**
	 * Rev 1.2 changes end here
	 */

	/**
	 * flag indicating availability as discount source
	 **/
	private boolean sourceAvailable = true;

	/**
	 * item tax rate method - set to default rate initially
	 **/
	protected int itemTaxMethod = ItemTaxIfc.ITEM_TAX_DEFAULT_RATE;
	/**
	 * item send flag - set to false as default
	 **/
	protected boolean itemSendFlag = false;
	/**
	 * gift receipt flag
	 **/
	protected boolean giftReceipt = false;
	/**
	 * alteration item flag
	 **/
	protected boolean alterationItemFlag = false;
	/**
	 * order item status
	 **/
	protected OrderItemStatusIfc orderItemStatus = DomainGateway.getFactory().getOrderItemStatusInstance();
	/**
	 * line item reference
	 **/
	protected String lineReference = "";
	/**
	 * order line item reference
	 **/
	protected int orderLineReference = 0;
	/**
	 * kit header reference
	 **/
	protected int kitHeaderReference = 0;
	/**
	 * entry method
	 * 
	 * @see com.extendyourstore.domain.utility.EntryMethodConstantsIfc
	 **/
	protected EntryMethod entryMethod = EntryMethod.getEntryMethod(MAXEntryMethodConstantsIfc.ENTRY_METHOD_KEYED);

	/**
	 * this is the string code that represents an item size
	 **/
	protected String itemSizeCode;
	/**
	 * this is the flag that indicates whether the tax has been changed in any
	 * way
	 **/

	/**
	 * Reference to the "parent" PriceAdjustmentLineItemIfc object which
	 * contains this line item
	 */
	protected int priceAdjustmentReference = -1;

	/** The marker used to indicate if a price was overridden. */
	public static final String OVERRIDE_MARKER = DomainUtil.retrieveOverrideMarker("journal");

	/**
	 * Original line number of this line item. This member was introduced in
	 * order for price adjustment updates to locate the original sale line item
	 * since its in-memory line number can change as it changes positions in the
	 * current transaction.
	 */
	protected int originalLineNumber = -1;

	/**
	 * Original transaction number of this line item. This member was introduced
	 * in order for price adjustment updates to update the retail price modifier
	 * for the original sale item.
	 */
	private long transactionSequenceNumber = -1;

	/**
	 * send label count
	 **/
	protected int sendLabelCount = 0;

	/**
	 * Flag to indicate whether or not an item has been manually set to be a
	 * part of a price adjustment Otherwise isPartOfPriceAdjustment() method
	 * uses other fields to determine whether or not an item is part of a price
	 * adjustment.
	 * 
	 * A call to setIsPartOfPriceAdjustment() will also set this flag to true.
	 * 
	 * This method would normally be used for display purposes when a price
	 * adjustment component may need to appear in the UI where it may normally
	 * have been filtered out (i.e. in the ShowSaleScreenSite after a previously
	 * price adjusted line item has been returned.
	 */
	protected boolean manuallySetPartOfPriceAdjustmentFlag = false;

	/**
	 * Keep track of whether or not this line item is from a transaction. If it
	 * is, the tax amounts will be preserved and not recalculated.
	 */
	protected boolean fromTransaction = false;

	/**
	 * Determine whether a related item is returnable
	 * 
	 * @since NEP67
	 */
	protected boolean relatedItemReturnable = true;
	/**
	 * Which sequence number is the related item associated with.
	 * 
	 * @since NEP67
	 */
	protected int relatedItemSequenceNumber = -1;

	/**
	 * Determine whether or not a related item is deletable
	 * 
	 * @since NEP67
	 */
	protected boolean relatedItemDeleteable = true;

	/**
	 * line items array list
	 **/
	protected ArrayList relatedItemLineItems;

	protected String employeeDiscountID = null;

	protected String discountPercentage = null;

	// ---------------------------------------------------------------------
	/**
	 * Constructs SaleReturnLineItem object.
	 * <P>
	 **/
	// ---------------------------------------------------------------------
	public MAXSaleReturnLineItem() {
	}

	// ---------------------------------------------------------------------
	/**
	 * SaleReturnLineItem object, setting item, tax rate and sales associate
	 * attributes.
	 * <P>
	 * 
	 * @param item
	 *            PLU item
	 * @param tax
	 *            ItemTax object
	 * @param pSalesAssociate
	 *            default sales associate
	 * @deprecated as of release 6.1.0 replaced by initialize() method
	 **/
	// ---------------------------------------------------------------------
	public MAXSaleReturnLineItem(PLUItemIfc item, ItemTaxIfc tax, EmployeeIfc pSalesAssociate) {
		initialize(item, BigDecimal.ONE, tax, pSalesAssociate, null, null);
	}

	// ---------------------------------------------------------------------
	/**
	 * Constructs SaleReturnLineItem object, setting item, tax rate, sales
	 * associate and registry attributes.
	 * <P>
	 * 
	 * @param item
	 *            PLU item
	 * @param tax
	 *            ItemTax object
	 * @param pSalesAssociate
	 *            default sales associate
	 * @param pRegistry
	 *            default registry
	 * @deprecated as of release 6.1.0 replaced by initialize() method
	 **/
	// ---------------------------------------------------------------------
	public MAXSaleReturnLineItem(PLUItemIfc item, ItemTaxIfc tax, EmployeeIfc pSalesAssociate,
			RegistryIDIfc pRegistry) {
		initialize(item, BigDecimal.ONE, tax, pSalesAssociate, pRegistry, null);
	}

	// ---------------------------------------------------------------------
	/**
	 * Constructs SaleReturnLineItem object, setting item, tax rate, sales
	 * associate, registry attributes and return item attributes.
	 * <P>
	 * 
	 * @param item
	 *            PLU item
	 * @param tax
	 *            ItemTax object
	 * @param pSalesAssociate
	 *            default sales associate
	 * @param pRegistry
	 *            default registry
	 * @param pReturnItem
	 *            return item info
	 * @deprecated as of release 6.1.0 replaced by initialize() method
	 **/
	// ---------------------------------------------------------------------
	public MAXSaleReturnLineItem(PLUItemIfc item, ItemTaxIfc tax, EmployeeIfc pSalesAssociate, RegistryIDIfc pRegistry,
			ReturnItemIfc pReturnItem) {
		initialize(item, BigDecimal.ONE, tax, pSalesAssociate, pRegistry, pReturnItem);
	}

	// ---------------------------------------------------------------------
	/**
	 * Constructs SaleReturnLineItem object, setting item, tax rate, sales
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
	 * @deprecated as of release 6.1.0 replaced by initialize() method
	 **/
	// ---------------------------------------------------------------------
	public MAXSaleReturnLineItem(PLUItemIfc item, BigDecimal quantity, ItemTaxIfc tax, EmployeeIfc pSalesAssociate,
			RegistryIDIfc pRegistry, ReturnItemIfc pReturnItem) {

		initialize(item, quantity, tax, pSalesAssociate, pRegistry, pReturnItem);
	}

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
			RegistryIDIfc pRegistry, ReturnItemIfc pReturnItem) { // begin
																	// SaleReturnLineItem()
																	// set
																	// attribute
																	// values
		itemQuantity = quantity;
		pluItem = item;
		salesAssociate = pSalesAssociate;
		returnItem = pReturnItem;
		//Added by Kamlesh
		item.setSpclEmpDisc(pluItem.getSpclEmpDisc());
		//System.out.println("MAXSaleReturnLineItem 492============= "+item.getSpclEmpDisc());
		//System.out.println("MAXSaleReturnLineItem 493============= "+pluItem.getSpclEmpDisc());
         
		// create itemPrice and set values to defaults or to those from pluItem
		itemPrice = DomainGateway.getFactory().getItemPriceInstance();

		itemPrice.setItemQuantity(itemQuantity);
		itemPrice.setItemTax(tax);
		if (pluItem != null) {
			itemPrice.setSellingPrice(pluItem.getPrice());
			itemPrice.setDiscountEligible(pluItem.isDiscountEligible());
			itemPrice.setEmployeeDiscountEligible(pluItem.getItemClassification().getEmployeeDiscountAllowedFlag());
			itemPrice.setDamageDiscountEligible(pluItem.getDamageDiscountEligible());
			itemPrice.getItemTax().setTaxGroupId(pluItem.getTaxGroupID());
			itemPrice.setPermanentSellingPrice(pluItem.getPermanentPrice(new EYSDate()));

		}

		// set price from return item, if available
		if (returnItem != null) {
			itemPrice.setSellingPrice(returnItem.getPrice());
			itemPrice.setRestockingFee(returnItem.getRestockingFee());
			itemSerial = returnItem.getSerialNumber();
		}

		if (pluItem == null || pluItem.getItemClassification().isRegistryEligible()) {
			registry = pRegistry;

		}
	} // end SaleReturnLineItem()

	// ---------------------------------------------------------------------
	/**
	 * Calculates and sets item price.
	 * <P>
	 **/
	// ---------------------------------------------------------------------
	public void calculateLineItemPrice() {
		itemPrice.calculateItemTotal();
	}

	// ---------------------------------------------------------------------
	/**
	 * Copies object.
	 * <P>
	 * 
	 * @return generic object copy of this SaleReturnLineItem object
	 **/

	// ---------------------------------------------------------------------
	/*
	 * public Object clone() { SaleReturnLineItem newSrli = new
	 * SaleReturnLineItem();
	 * 
	 * setCloneAttributes(newSrli);
	 * 
	 * return newSrli; }
	 */
	/**
	 * Rev 1.1 changes start here
	 */
	public Object clone() {
		MAXSaleReturnLineItem newSrli = new MAXSaleReturnLineItem();

		setCloneAttributes(newSrli);

		return newSrli;
	}

	/**
	 * Rev 1.1 changes end here
	 */
	// ---------------------------------------------------------------------
	/**
	 * Clones the attributes of this class attributes. This is to be called by
	 * the clone of the children with an new instance of this class.
	 * <p>
	 * 
	 * @param newSrli
	 *            new SaleReturnLineItem instance
	 **/
	// ---------------------------------------------------------------------
	protected void setCloneAttributes(MAXSaleReturnLineItem newSrli) {
		// clone superclass attributes
		super.setCloneAttributes(newSrli);

		// clone PLU item, if valid
		newSrli.pluItemID = pluItemID;
		if (pluItem != null) {
			newSrli.pluItem = (PLUItemIfc) pluItem.clone();
		}
		if (itemSerial != null) {
			newSrli.itemSerial = new String(itemSerial);
		}
		// clone return item, if valid
		if (returnItem != null) {
			newSrli.returnItem = (ReturnItemIfc) returnItem.clone();
		}
		// clone registry, if valid
		if (registry != null) {
			newSrli.registry = (RegistryIDIfc) registry.clone();
		}

		// build new item price, if old one exists
		if (itemPrice != null) {
			newSrli.setItemPrice((ItemPriceIfc) itemPrice.clone());
		}

		// build new advanced pricing discount, if old one exists
		if (advancedPricingDiscount != null) {
			newSrli.advancedPricingDiscount = (ItemDiscountStrategyIfc) advancedPricingDiscount.clone();
		}
		// izharDR start
		if (this.getBdwList().size() > 0) {
			newSrli.setBdwList(this.getBdwList());
		}
		if (this.getBestDealWinnerName() != null) {
			newSrli.setBestDealWinnerName(this.getBestDealWinnerName());
		}
		newSrli.setTargetIdentifier(this.isTargetIdentifier());

		if (this.getExcList().size() > 0) {
			newSrli.setExcList(this.getExcList());
		}
		if (this.getDiscountAmountforRTLOG() != null) {
			newSrli.setDiscountAmountforRTLOG(this.getDiscountAmountforRTLOG());
		}
		if (this.getAmountVFP() != null) {
			newSrli.setDiscountAmountforRTLOG(this.getAmountVFP());
		}
		if (this.getVendorID() != null) {
			newSrli.setVendorID(this.getVendorID());
		}
		// Added by vaibhav
		if (this.getPromoDiscountForReceipt() != null) {
			newSrli.setPromoDiscountForReceipt(this.getPromoDiscountForReceipt());
		}
		// izharDR end;
		// set other attributes
		newSrli.setItemQuantity(itemQuantity);
		newSrli.setQuantityReturned(quantityReturned);
		newSrli.setGiftRegistryModifiedFlag(registryModifiedFlag);
		newSrli.setItemTaxMethod(itemTaxMethod);
		newSrli.setSourceAvailable(sourceAvailable);
		newSrli.setItemSendFlag(itemSendFlag);
		newSrli.setFromTransaction(fromTransaction);

		newSrli.setSendLabelCount(sendLabelCount);

		newSrli.setGiftReceiptItem(giftReceipt);
		newSrli.setAlterationItemFlag(alterationItemFlag);
		newSrli.setItemSizeCode(itemSizeCode);
		if (orderItemStatus != null) {
			newSrli.setOrderItemStatus((OrderItemStatusIfc) orderItemStatus.clone());
		}
		newSrli.setLineReference(getLineReference());
		newSrli.setOrderLineReference(getOrderLineReference());
		newSrli.setKitHeaderReference(kitHeaderReference);
		newSrli.setEntryMethod(getEntryMethod());
		newSrli.setTaxChanged(isTaxChanged());
		newSrli.setIsPriceAdjustmentLineItem(isPriceAdjustmentLineItem());
		newSrli.setPriceAdjustmentReference(getPriceAdjustmentReference());

		newSrli.manuallySetPartOfPriceAdjustmentFlag = this.manuallySetPartOfPriceAdjustmentFlag;
		if (newSrli.manuallySetPartOfPriceAdjustmentFlag) {
			newSrli.setIsPartOfPriceAdjustment(this.isPartOfPriceAdjustment);
		}
		newSrli.setRelatedItemSequenceNumber(this.relatedItemSequenceNumber);
		newSrli.setRelatedItemReturnable(this.relatedItemReturnable);
		newSrli.setRelatedItemDeleteable(this.relatedItemDeleteable);
		SaleReturnLineItemIfc[] relatedItems = getRelatedItemLineItems();
		if (relatedItems != null) {
			for (int i = 0; i < relatedItems.length; i++) {
				newSrli.addRelatedItemLineItem((SaleReturnLineItem) relatedItems[i].clone());
			}
		}
		/** Changes for Rev 1.4 : Starts **/
		newSrli.setEmployeeDiscountID(employeeDiscountID);
		newSrli.setDiscountPercentage(discountPercentage);
		/** Changes for Rev 1.4 : Ends **/
		//Change for Rev 1.7 :Starts
		newSrli.setTaxType(this.taxType);
		newSrli.setProportionatePrice(proportionatePrice);
		newSrli.setHSNNumber(hsnNumber);
		
		newSrli.setliquom(liquom);
		newSrli.setliqcat(liqcat);
		
		//Change for Rev 1.7 : Ends
		newSrli.setShippingMethodType(shippingMethodType);
	}

	// ---------------------------------------------------------------------
	/**
	 * Modifies item quantity, reset item total.
	 * <P>
	 * 
	 * @param newQty
	 *            new quantity
	 **/
	// ---------------------------------------------------------------------
	public void modifyItemQuantity(BigDecimal newQty) {
		itemQuantity = new BigDecimal(newQty.toString());
		itemPrice.setItemQuantity(itemQuantity);
		itemPrice.calculateItemTotal();
	}

	// ---------------------------------------------------------------------
	/**
	 * Returns the total price of this line item with all modifications applied.
	 * <p>
	 * 
	 * @return CurrencyIfc total
	 **/
	// ---------------------------------------------------------------------
	public CurrencyIfc getLineItemAmount() {
		return itemPrice.getItemTotal();
	}

	// ---------------------------------------------------------------------
	/**
	 * Returns the text description that is to be used for the UI and the
	 * receipt.
	 * <p>
	 * 
	 * @return String description
	 **/
	// ---------------------------------------------------------------------
	public String getItemDescription() {
		return pluItem == null ? null : pluItem.getDescription(LocaleMap.getLocale(LocaleConstantsIfc.JOURNAL));
	}

	// ---------------------------------------------------------------------
	/**
	 * Returns the ID for this item.
	 * <p>
	 * 
	 * @return String identifier
	 **/
	// ---------------------------------------------------------------------
	public String getItemID() {
		String id = "";

		if (pluItem != null) {
			id = pluItem.getItemID();
		} else if (pluItemID != null) {
			id = pluItemID;
		}

		return id;
	}

	// ---------------------------------------------------------------------
	/**
	 * Returns the original price for a single item.
	 * <p>
	 * 
	 * @return CurrencyIfc
	 **/
	// ---------------------------------------------------------------------
	public CurrencyIfc getSellingPrice() {
		return itemPrice.getSellingPrice();
	}

	// ---------------------------------------------------------------------
	/**
	 * Returns the extended price before discounts. (original price * item
	 * quantity)
	 * 
	 * @return CurrencyIfc
	 **/
	// ---------------------------------------------------------------------
	public CurrencyIfc getExtendedSellingPrice() {
		return itemPrice.getExtendedSellingPrice();
	}

	// ---------------------------------------------------------------------
	/**
	 * Returns the extended price after discounts. ((original price * item
	 * quantity) - item discount total)
	 * 
	 * @return CurrencyIfc
	 **/
	// ---------------------------------------------------------------------
	public CurrencyIfc getExtendedDiscountedSellingPrice() {
		return itemPrice.getExtendedDiscountedSellingPrice();
	}

	// ---------------------------------------------------------------------
	/**
	 * Returns the item discount
	 * <p>
	 * 
	 * @return CurrencyIfc
	 **/
	// ---------------------------------------------------------------------
	public CurrencyIfc getItemDiscountAmount() {
		return itemPrice.getItemDiscountAmount();
	}

	// ---------------------------------------------------------------------
	/**
	 * Returns the total discount amount for discounts matching specified
	 * parameters.
	 * <p>
	 * 
	 * @param discountScope
	 *            discount scope
	 * @param assignmentBasis
	 *            assignment basis
	 * @return The total discount amount for discounts matching specified
	 *         parameters.
	 **/
	// ---------------------------------------------------------------------
	public CurrencyIfc getDiscountAmount(int discountScope, int assignmentBasis) {
		return itemPrice.getDiscountAmount(discountScope, assignmentBasis);
	}

	// ---------------------------------------------------------------------
	/**
	 * Returns the item discount total
	 * <p>
	 * 
	 * @return CurrencyIfc
	 **/
	// ---------------------------------------------------------------------
	public CurrencyIfc getItemDiscountTotal() {
		return itemPrice.getItemDiscountTotal();
	}

	// ---------------------------------------------------------------------
	/**
	 * Clears ItemDiscountStrategyIfcs with the corresponding discountRuleID
	 * from itemPrice.itemDiscountsVector
	 * 
	 * @param discountRuleID
	 *            the discount rule ID
	 **/
	// ---------------------------------------------------------------------
	public void clearItemDiscounts(String discountRuleID) {
		// have the itemPrice clear discounts with corresponding ruleID
		itemPrice.clearItemDiscounts(discountRuleID);
		// clear the advancedPricingDiscount and make item available as source
		// if necessary
		if (advancedPricingDiscount != null && advancedPricingDiscount.getRuleID().equals(discountRuleID)) {
			advancedPricingDiscount = null;
			setSourceAvailable(true);
		}
		// recalculate item $ values
		calculateLineItemPrice();
	}

	// ---------------------------------------------------------------------
	/**
	 * Retrieves array of item discounts by percentage.
	 * <P>
	 * 
	 * @return array of disc item discount objects, null if not found
	 **/
	// ---------------------------------------------------------------------
	public ItemDiscountStrategyIfc[] getItemDiscountsByPercentage() {
		return itemPrice.getItemDiscountsByPercentage();
	}

	// ---------------------------------------------------------------------
	/**
	 * Retrieves array of item discounts by amount.
	 * <P>
	 * 
	 * @return array of disc item discount objects, null if not found
	 **/
	// ---------------------------------------------------------------------
	public ItemDiscountStrategyIfc[] getItemDiscountsByAmount() {
		return itemPrice.getItemDiscountsByAmount();
	}

	// ---------------------------------------------------------------------
	/**
	 * Retrieves array of return item discounts.
	 * <P>
	 * 
	 * @return array of disc item discount objects, null if not found
	 **/
	// ---------------------------------------------------------------------
	public ItemDiscountStrategyIfc[] getReturnItemDiscounts() {
		return itemPrice.getReturnItemDiscounts();
	}

	// ---------------------------------------------------------------------
	/**
	 * Retrieves array of transaction discount audit objects.
	 * <P>
	 * 
	 * @return array of transasction discount audit objects, null if not found
	 **/
	// ---------------------------------------------------------------------
	public ItemDiscountStrategyIfc[] getTransactionDiscounts() {
		return itemPrice.getTransactionDiscounts();
	}

	// ---------------------------------------------------------------------
	/**
	 * Clears the transaction discounts
	 **/
	// ---------------------------------------------------------------------
	public void clearTransactionDiscounts() {
		itemPrice.clearTransactionDiscounts();
		itemPrice.setItemTransactionDiscountAmount(DomainGateway.getBaseCurrencyInstance());
	}

	// ---------------------------------------------------------------------
	/**
	 * Clears the transaction discounts
	 **/
	// ---------------------------------------------------------------------
	/*
	 * public void clearTransactionDiscountsWithoutDiscountCard() {
	 * itemPrice.clearTransactionDiscountsWithoutDiscountCard();
	 * itemPrice.setItemTransactionDiscountAmount(DomainGateway.
	 * getBaseCurrencyInstance()); //
	 * itemPrice.setItemDiscountCardAmount(DomainGateway //
	 * .getBaseCurrencyInstance()); }
	 */

	// ---------------------------------------------------------------------
	/**
	 * Recalculates the item total
	 **/
	// ---------------------------------------------------------------------
	public void recalculateItemTotal() {
		itemPrice.recalculateItemTotal();
	}

	// ---------------------------------------------------------------------
	/**
	 * Sets the discount total
	 * <p>
	 * 
	 * @param itemDiscount
	 *            discount total
	 **/
	// ---------------------------------------------------------------------
	public void setItemDiscountTotal(CurrencyIfc itemDiscount) {
		itemPrice.setItemDiscountTotal(itemDiscount);
	}

	// ---------------------------------------------------------------------
	/**
	 * Returns the transaction discount amount
	 * <p>
	 * 
	 * @return CurrencyIfc
	 **/
	// ---------------------------------------------------------------------
	public CurrencyIfc getItemTransactionDiscountAmount() {
		return itemPrice.getItemTransactionDiscountAmount();
	}

	// ---------------------------------------------------------------------
	/**
	 * Adds a transaction discount.
	 * <p>
	 * 
	 * @param value
	 *            discount amount
	 * @param td
	 *            strategy to collect discount attributes from
	 **/
	// ---------------------------------------------------------------------
	public void addTransactionDiscount(CurrencyIfc value, TransactionDiscountStrategyIfc td) {
		itemPrice.addTransactionDiscount(value, td);
	}

	// ---------------------------------------------------------------------
	/**
	 * Adds a transaction discount.
	 * <p>
	 * 
	 * @param value
	 *            discount amount
	 * @param td
	 *            strategy to collect discount attributes from
	 **/
	// ---------------------------------------------------------------------
	public void addDiscountCard(CurrencyIfc value, TransactionDiscountStrategyIfc td) {
		itemPrice.addTransactionDiscount(value, td);
	}

	// ---------------------------------------------------------------------
	/**
	 * Gets the item tax amount.
	 * <P>
	 * 
	 * @return CurrencyIfc
	 **/
	// ---------------------------------------------------------------------
	public CurrencyIfc getItemTaxAmount() {
		return itemPrice.getItemTaxAmount();
	}

	// ---------------------------------------------------------------------
	/**
	 * Sets the item tax amount
	 * <P>
	 * 
	 * @param value
	 *            item tax amount
	 **/
	// ---------------------------------------------------------------------
	public void setItemTaxAmount(CurrencyIfc value) {
		itemPrice.setItemTaxAmount(value);
	}

	// ---------------------------------------------------------------------
	/**
	 * Gets the item inclusive tax amount.
	 * <P>
	 * 
	 * @return CurrencyIfc
	 **/
	// ---------------------------------------------------------------------
	public CurrencyIfc getItemInclusiveTaxAmount() {
		return itemPrice.getItemInclusiveTaxAmount();
	}

	// ---------------------------------------------------------------------
	/**
	 * Sets the item inclusive tax amount
	 * <P>
	 * 
	 * @param value
	 *            item inclusive tax amount
	 **/
	// ---------------------------------------------------------------------
	public void setItemInclusiveTaxAmount(CurrencyIfc value) {
		itemPrice.setItemInclusiveTaxAmount(value);
	}

	/**
	 * Set the scope of the tax
	 * 
	 * @param scope
	 * @see com.extendyourstore.domain.lineitem.TaxLineItemInformationIfc#setTaxScope(int)
	 */
	public void setTaxScope(int scope) {
		itemPrice.getItemTax().setTaxScope(scope);
	}

	/**
	 * Get the tax scope
	 * 
	 * @return scope
	 * @see com.extendyourstore.domain.lineitem.TaxLineItemInformationIfc#getTaxScope()
	 */
	public int getTaxScope() {
		return itemPrice.getItemTax().getTaxScope();
	}

	// ---------------------------------------------------------------------
	/**
	 * Returns the item tax
	 * 
	 * @return ItemTax
	 **/
	// ---------------------------------------------------------------------
	public ItemTaxIfc getItemTax() {
		return itemPrice.getItemTax();
	}

	// ---------------------------------------------------------------------
	/**
	 * Returns the item tax mode
	 * <p>
	 * 
	 * @return int tax mode
	 **/
	// ---------------------------------------------------------------------
	public int getTaxMode() {
		return getItemTax().getTaxMode();
	}

	/**
	 * Set the tax mode
	 * 
	 * @param value
	 *            Value to set
	 */
	public void setTaxMode(int value) {
		getItemTax().setTaxMode(value);
	}

	// --------------------------------------------------------------------------
	/**
	 * Determines the tax status descriptor flag.
	 * 
	 * @return a string value to describe taxability status
	 */
	// --------------------------------------------------------------------------
	public String getTaxStatusDescriptor() {
		return TaxIfc.TAX_MODE_CHAR[getTaxMode()];
	}

	// ---------------------------------------------------------------------
	/**
	 * Clears the taxes
	 **/
	// ---------------------------------------------------------------------
	public void clearTaxAmounts() {
		getItemTax().clearTaxAmounts();
	}

	// ---------------------------------------------------------------------
	/**
	 * Returns the taxable flag
	 * <p>
	 * 
	 * @return boolean
	 **/
	// ---------------------------------------------------------------------
	public boolean getTaxable() {
		return pluItem.getTaxable();
	}

	// ---------------------------------------------------------------------
	/**
	 * Sets the taxable flag
	 * <p>
	 * 
	 * @param value
	 *            The boolean value for the taxable flag
	 **/
	// ---------------------------------------------------------------------
	public void setTaxable(boolean value) {
		pluItem.setTaxable(value);
	}

	// ---------------------------------------------------------------------
	/**
	 * Returns the tax group ID
	 * <p>
	 * 
	 * @return int
	 **/
	// ---------------------------------------------------------------------
	public int getTaxGroupID() {
		return pluItem.getTaxGroupID();
	}

	// ---------------------------------------------------------------------
	/**
	 * Tests the item's tax amount to see if it is greater than the extended
	 * selling price of the item.
	 * <P>
	 * 
	 * @return boolean
	 **/
	// ---------------------------------------------------------------------

	public boolean taxExceedsSellingPrice() {
		boolean value = false;

		if (!this.isKitHeader()) {
			if (this.isSaleLineItem()
					&& getExtendedSellingPrice().compareTo(getItemTaxAmount()) == CurrencyIfc.LESS_THAN) {
				value = true;
			} else if (this.isReturnLineItem()
					&& getItemTaxAmount().compareTo(getExtendedSellingPrice()) == CurrencyIfc.LESS_THAN) {
				value = true;
			}
		}

		return value;
	}

	// ---------------------------------------------------------------------
	/**
	 * Tests the item's discount amount to see if it is greater than the
	 * extended selling price of the item.
	 * <P>
	 * 
	 * @return boolean
	 **/
	// ---------------------------------------------------------------------
	public boolean discountExceedsSellingPrice() {
		boolean value = false;

		if (this.isSaleLineItem()
				&& getExtendedSellingPrice().compareTo(getItemDiscountTotal()) == CurrencyIfc.LESS_THAN) {
			value = true;
		} else if (this.isReturnLineItem()
				&& getItemDiscountTotal().compareTo(getExtendedSellingPrice()) == CurrencyIfc.LESS_THAN) {
			value = true;
		}

		return value;
	}

	// ---------------------------------------------------------------------
	/**
	 * Returns the extended amount for this item with everything applied but tax
	 * <p>
	 * 
	 * @return CurrencyIfc amount
	 **/
	// ---------------------------------------------------------------------
	public CurrencyIfc getFinalPreTaxAmount() {
		return itemPrice.getExtendedDiscountedSellingPrice();
	}

	// ---------------------------------------------------------------------
	/**
	 * Determines if this class is discountable by amount
	 * <p>
	 * 
	 * @return boolean
	 **/
	// ---------------------------------------------------------------------
	public boolean isDiscountableByAmount() {
		return true;
	}

	// ---------------------------------------------------------------------
	/**
	 * Determines if this class is discountable by percentage
	 * <p>
	 * 
	 * @return boolean
	 **/
	// ---------------------------------------------------------------------
	public boolean isDiscountableByPercentage() {
		return true;
	}

	// ---------------------------------------------------------------------
	/**
	 * Adds item discount object.
	 * <P>
	 * 
	 * @param discount
	 *            ItemDiscountStrategyIfc object
	 **/
	// ---------------------------------------------------------------------
	public void addItemDiscount(ItemDiscountStrategyIfc discount) {
		itemPrice.addItemDiscount(discount);
		itemPrice.calculateItemTotal();
	}

	// ---------------------------------------------------------------------
	/**
	 * Clears item discounts by percentage.
	 * <P>
	 * 
	 * @deprecated As of release 7.0.0, replaced by
	 *             {@link #clearItemDiscountsByPercentage(int, boolean)}
	 **/
	// ---------------------------------------------------------------------
	public void clearItemDiscountsByPercentage() {
		//Commented For upgradation
		//itemPrice.clearItemDiscountsByPercentage();
		itemPrice.calculateItemTotal();
	}

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
	public void clearItemDiscountsByPercentage(int basis, boolean damage) {
		itemPrice.clearItemDiscountsByPercentage(basis, damage);
		itemPrice.calculateItemTotal();
	}

	// ---------------------------------------------------------------------
	/**
	 * Clears item discounts by percentage.
	 * <P>
	 * 
	 * @param typeCode
	 *            type code
	 * @deprecated As of release 7.0.0, replaced by
	 *             {@link #clearItemDiscountsByPercentage(int, int, boolean)}
	 **/
	// ---------------------------------------------------------------------
	public void clearItemDiscountsByPercentage(int typeCode) {
		itemPrice.clearItemDiscountsByPercentage(typeCode, false);
		itemPrice.calculateItemTotal();
	}

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
	public void clearItemDiscountsByPercentage(int typeCode, int basis, boolean damage) {
		itemPrice.clearItemDiscountsByPercentage(typeCode, basis, damage);
		itemPrice.calculateItemTotal();
	}

	// ---------------------------------------------------------------------
	/**
	 * Clears item markdowns by percentage.
	 * <P>
	 **/
	// ---------------------------------------------------------------------
	public void clearItemMarkdownsByPercentage() {
		itemPrice.clearItemMarkdownsByPercentage();
		itemPrice.calculateItemTotal();
	}

	// ---------------------------------------------------------------------
	/**
	 * Clears item markdowns by percentage.
	 * <P>
	 * 
	 * @param typeCode
	 *            type code
	 **/
	// ---------------------------------------------------------------------
	public void clearItemMarkdownsByPercentage(int typeCode) {
		itemPrice.clearItemMarkdownsByPercentage(typeCode);
		itemPrice.calculateItemTotal();
	}

	// ---------------------------------------------------------------------
	/**
	 * Clears item discounts by amount.
	 * <P>
	 * 
	 * @deprecated As of release 7.0.0, replaced by
	 *             {@link #clearItemDiscountsByAmount(int, boolean)}
	 **/
	// ---------------------------------------------------------------------
	public void clearItemDiscountsByAmount() {
		//Commented For upgradation
		//itemPrice.clearItemDiscountsByAmount();
		itemPrice.calculateItemTotal();
	}

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
	public void clearItemDiscountsByAmount(int basis, boolean damage) {
		itemPrice.clearItemDiscountsByAmount(basis, damage);
		itemPrice.calculateItemTotal();
	}

	// ---------------------------------------------------------------------
	/**
	 * Clears item discounts by amount.
	 * <P>
	 * 
	 * @param typeCode
	 *            type code
	 * @deprecated As of release 7.0.0, replaced by
	 *             {@link #clearItemDiscountsByAmount(int, int, boolean)}
	 **/
	// ---------------------------------------------------------------------
	public void clearItemDiscountsByAmount(int typeCode) {
		itemPrice.clearItemDiscountsByAmount(typeCode, false);
		itemPrice.calculateItemTotal();
	}

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
	public void clearItemDiscountsByAmount(int typeCode, int basis, boolean damage) {
		itemPrice.clearItemDiscountsByAmount(typeCode, basis, damage);
		itemPrice.calculateItemTotal();

	}

	// ---------------------------------------------------------------------
	/**
	 * Clears item markdowns by amount.
	 * <P>
	 **/
	// ---------------------------------------------------------------------
	public void clearItemMarkdownsByAmount() {
		itemPrice.clearItemMarkdownsByAmount();
		itemPrice.calculateItemTotal();
	}

	// ---------------------------------------------------------------------
	/**
	 * Clears item markdowns by amount.
	 * <P>
	 * 
	 * @param typeCode
	 *            type code
	 **/
	// ---------------------------------------------------------------------
	public void clearItemMarkdownsByAmount(int typeCode) {
		itemPrice.clearItemMarkdownsByAmount(typeCode);
		itemPrice.calculateItemTotal();
	}

	// ---------------------------------------------------------------------
	/**
	 * Sets array of item discounts by amount.
	 * <P>
	 * 
	 * @param value
	 *            array of disc item discount objects, null if not found
	 **/
	// ---------------------------------------------------------------------
	public void setItemDiscountsByAmount(ItemDiscountStrategyIfc[] value) {
		itemPrice.setItemDiscountsByAmount(value);
		itemPrice.calculateItemTotal();
	}

	// ---------------------------------------------------------------------
	/**
	 * Sets array of item discounts by percentage.
	 * <P>
	 * 
	 * @param value
	 *            array of disc item discount objects, null if not found
	 **/
	// ---------------------------------------------------------------------
	public void setItemDiscountsByPercentage(ItemDiscountStrategyIfc[] value) {
		itemPrice.setItemDiscountsByPercentage(value);
		itemPrice.calculateItemTotal();
	}

	// ---------------------------------------------------------------------
	/**
	 * Modifies item tax.
	 * <P>
	 * 
	 * @param newRate
	 *            new tax rate
	 * @param reasonCode
	 *            reason code
	 **/
	// ---------------------------------------------------------------------
	public void modifyItemTaxRate(double newRate, int reasonCode) {
		itemPrice.overrideTaxRate(newRate, reasonCode);
	}

	// ---------------------------------------------------------------------
	/**
	 * Modifies item price.
	 * <P>
	 * 
	 * @param newPrice
	 *            new price
	 * @param reasonCode
	 *            reason code
	 **/
	// ---------------------------------------------------------------------
	public void modifyItemPrice(CurrencyIfc newPrice, LocalizedCodeIfc reasonCode) {
		itemPrice.overridePrice(newPrice, reasonCode);
	}

	// ---------------------------------------------------------------------
	/**
	 * Modifies item registry and sets modified flag.
	 * <P>
	 * 
	 * @param newGift
	 *            new registry
	 * @deprecated As of release 7.0.0, replaced by
	 *             {@link #modifyItemRegistry(RegistryIDIfc, boolean)}
	 **/
	// ---------------------------------------------------------------------
	public void modifyItemRegistry(RegistryIDIfc newGift) {
		// set new registry, modified flag to true
		modifyItemRegistry(newGift, true);
	}

	// ---------------------------------------------------------------------
	/**
	 * Modifies item registry and sets modified flag to requested value.
	 * <P>
	 * 
	 * @param newGift
	 *            new registry
	 * @param modified
	 *            modified flag
	 **/
	// ---------------------------------------------------------------------
	public void modifyItemRegistry(RegistryIDIfc newGift, boolean modified) {
		// set new registry, modified flag
		setRegistry(newGift);
		setRegistryModifiedFlag(modified);
	}

	// ---------------------------------------------------------------------
	/**
	 * Force implementation of getFinancialTotals by subclasses
	 * 
	 * @return totals FinancialTotalsIfc object for this line item
	 **/
	// ---------------------------------------------------------------------
	public FinancialTotalsIfc getFinancialTotals() {
		// Calling method assumes this transaction is either a Sale
		// or a Return transaction and not a Post Void Transaction.
		return getFinancialTotals(true);
	}

	// ---------------------------------------------------------------------
	/**
	 * Returns financial totals for the line item.
	 * <P>
	 * 
	 * @param isSale
	 *            boolean
	 * @return totals FinancialTotalsIfc object for this line item
	 **/
	// ---------------------------------------------------------------------
	public FinancialTotalsIfc getFinancialTotals(boolean isSaleOrReturnTransaction) { // begin
																						// getFinancialTotals()

		// In order to clearify the processing of this function, I changed the
		// name of the
		// of parameter. This needs to know if its dealing with a regular
		// SaleReturn
		// transaction or a voided SaleReturn transaction. If the parameter
		// isSaleOrReturnTransaction is false, then this transaction is a
		// PostVoid.

		FinancialTotalsIfc totals = DomainGateway.getFactory().getFinancialTotalsInstance();
		ItemPriceIfc ip = getItemPrice();
		ItemTaxIfc it = ip.getItemTax();
		ItemClassificationIfc sc = getPLUItem().getItemClassification();
		PLUItemIfc pluItem = getPLUItem();

		// divvy up gross based on quantity sign, taxable, non-taxable
		BigDecimal units = getItemQuantityDecimal();
		// extract gross sales
		CurrencyIfc gross = ip.getExtendedDiscountedSellingPrice();
		/* India Localization Changes - Starts here */
		if (gross != null && it.getItemInclusiveTaxAmount() != null) {
			// For India L10N VAT, Taxable amount is defined as FinalSaleAmount
			// minus ItemInclusiveTaxAmount.
			gross = gross.subtract(it.getItemInclusiveTaxAmount());
		}
		/* India Localization Changes - Ends here */
		// get the extended restocking fee
		CurrencyIfc extendedRestockingFee = ip.getExtendedRestockingFee();

		if (units.signum() > 0) {
			// begin handle positive gross, divvy up sales and units
			switch (it.getTaxMode()) {
			// Evaluate on tax mode
			case TaxIfc.TAX_MODE_EXEMPT:

				if (isSaleOrReturnTransaction) // Not a PostVoid, since the
												// quantity is positive it must
												// be a SALE item
				{
					// Gift Cards and Gift Certificates are not counted in item
					// sales
					if (!isGiftItem(pluItem)) {
						totals.addAmountGrossTaxExemptItemSales(gross);
						totals.addUnitsGrossTaxExemptItemSales(units);

						// since tax-exempt sales are a subset of nontaxable,
						// the totals are also included for non-taxable sales
						totals.addAmountGrossNonTaxableItemSales(gross);
						totals.addUnitsGrossNonTaxableItemSales(units);
					}

					if (sc.getItemType() == ItemClassificationConstantsIfc.TYPE_SERVICE) {
						totals.addAmountGrossNonTaxableNonMerchandiseSales(gross);
						totals.addUnitsGrossNonTaxableNonMerchandiseSales(units);
					}
				} else // This is a post void; since the quantity is positive it
						// must be a RETURN item
				{
					// Gift Cards and Gift Certificates are not counted in item
					// sales
					if (!isGiftItem(pluItem)) {
						totals.addAmountGrossTaxExemptItemReturnsVoided(gross.abs());
						totals.addUnitsGrossTaxExemptItemReturnsVoided(units);

						// since tax-exempt sales are a subset of nontaxable,
						// the totals are also included for non-taxable sales
						totals.addAmountGrossNonTaxableItemReturnsVoided(gross.abs());
						totals.addUnitsGrossNonTaxableItemReturnsVoided(units);
					}

					if (sc.getItemType() == ItemClassificationConstantsIfc.TYPE_SERVICE) {
						totals.addAmountGrossNonTaxableNonMerchandiseReturnsVoided(gross.abs());
						totals.addUnitsGrossNonTaxableNonMerchandiseReturnsVoided(units);
					}
				}
				break;
			case TaxIfc.TAX_MODE_TOGGLE_OFF:
			case TaxIfc.TAX_MODE_NON_TAXABLE:

				if (isSaleOrReturnTransaction) // Not a PostVoid; since the
												// quantity is positive it must
												// be a SALE item
				{
					// Gift Cards and Gift Certificates are not counted in item
					// sales
					if (!isGiftItem(pluItem)) {
						totals.addAmountGrossNonTaxableItemSales(gross);
						totals.addUnitsGrossNonTaxableItemSales(units);
					}

					if (sc.getItemType() == ItemClassificationConstantsIfc.TYPE_SERVICE) {
						totals.addAmountGrossNonTaxableNonMerchandiseSales(gross);
						totals.addUnitsGrossNonTaxableNonMerchandiseSales(units);
					}
				} else // This is a post void; since the quantity is positive it
						// must be a RETURN item
				{
					// Gift Cards and Gift Certificates are not counted in item
					// sales
					if (!isGiftItem(pluItem)) {
						totals.addAmountGrossNonTaxableItemReturnsVoided(gross.abs());
						totals.addUnitsGrossNonTaxableItemReturnsVoided(units);
					}

					if (sc.getItemType() == ItemClassificationConstantsIfc.TYPE_SERVICE) {
						totals.addAmountGrossNonTaxableNonMerchandiseReturnsVoided(gross.abs());
						totals.addUnitsGrossNonTaxableNonMerchandiseReturnsVoided(units);
					}
				}
				break;
			default:
				if (isSaleOrReturnTransaction) // Not a PostVoid; since the
												// quantity is positive it must
												// be a SALE item
				{
					// Gift Cards and Gift Certificates are not counted in item
					// sales
					if (!isGiftItem(pluItem)) {
						totals.addAmountGrossTaxableItemSales(gross);
						totals.addUnitsGrossTaxableItemSales(units);
						totals.addAmountTaxItemSales(ip.getItemTaxAmount());
						totals.addAmountInclusiveTaxItemSales(ip.getItemInclusiveTaxAmount());

						// Save all the separate tax rules into totals
						TaxInformationIfc[] taxInformation = it.getTaxInformationContainer().getTaxInformation();
						TaxTotalsContainerIfc container = DomainGateway.getFactory().getTaxTotalsContainerInstance();
						for (int i = 0; i < taxInformation.length; i++) {
							TaxTotalsIfc taxTotalsItem = new TaxTotals(taxInformation[i]);
							container.addTaxTotals(taxTotalsItem);
						}
						totals.addTaxes(container);
					}

					if (sc.getItemType() == ItemClassificationConstantsIfc.TYPE_SERVICE) {
						totals.addAmountGrossTaxableNonMerchandiseSales(gross);
						totals.addUnitsGrossTaxableNonMerchandiseSales(units);
					}
				} else // This is a post void; since the quantity is positive it
						// must be a RETURN item
				{
					// Gift Cards and Gift Certificates are not counted in item
					// sales
					if (!isGiftItem(pluItem)) {
						totals.addAmountGrossTaxableItemReturnsVoided(gross.abs());
						totals.addUnitsGrossTaxableItemReturnsVoided(units);
						totals.addAmountTaxItemReturns(ip.getItemTaxAmount().abs().negate());
						totals.addAmountInclusiveTaxItemReturns(ip.getItemInclusiveTaxAmount().abs().negate());

						// Save all the separate tax rules into totals
						TaxInformationIfc[] taxInformation = it.getTaxInformationContainer().getTaxInformation();
						TaxTotalsContainerIfc container = DomainGateway.getFactory().getTaxTotalsContainerInstance();
						for (int i = 0; i < taxInformation.length; i++) {
							TaxTotalsIfc taxTotalsItem = new TaxTotals(taxInformation[i]);
							container.addTaxTotals(taxTotalsItem);
						}
						totals.subtractTaxes(container);
					}

					if (sc.getItemType() == ItemClassificationConstantsIfc.TYPE_SERVICE) {
						totals.addAmountGrossTaxableNonMerchandiseReturnsVoided(gross.abs());
						totals.addUnitsGrossTaxableNonMerchandiseReturnsVoided(units);
					}
				}
				break;
			// process price overrides
			} // end evaluate tax mode

			// Update gift certificate issued
			if (getPLUItem() instanceof GiftCardPLUItemIfc) {
				if (isSaleOrReturnTransaction) {
					GiftCardPLUItemIfc giftCardPLUItem = (GiftCardPLUItemIfc) getPLUItem();
					if (giftCardPLUItem != null) {
						GiftCardIfc giftCard = giftCardPLUItem.getGiftCard();
						if (giftCard != null) {
							if (giftCard.getRequestType() == GiftCardIfc.GIFT_CARD_ISSUE) {
								totals.addAmountGrossGiftCardItemIssued(gross);
								totals.addUnitsGrossGiftCardItemIssued(units);
								totals.addAmountGrossGiftCardItemSales(gross);
								totals.addUnitsGrossGiftCardItemSales(units);
							} else if (giftCard.getRequestType() == GiftCardIfc.GIFT_CARD_RELOAD) {
								totals.addAmountGrossGiftCardItemReloaded(gross);
								totals.addUnitsGrossGiftCardItemReloaded(units);
							} else if (giftCard.getRequestType() == GiftCardIfc.GIFT_CARD_CREDIT_ISSUE
									|| giftCard.getRequestType() == GiftCardIfc.GIFT_CARD_CREDIT_RELOAD) {
								totals.addAmountGrossGiftCardItemCredit(gross);
								totals.addUnitsGrossGiftCardItemCredit(units);
							}

							// totals.addAmountGrossNonTaxableItemSales(gross);
						}
					}
				}
			}

			// This is a void transaction and there is a restocking fee value.
			if ((!isSaleOrReturnTransaction) && (extendedRestockingFee != null)) {
				if (extendedRestockingFee.compareTo(DomainGateway.getBaseCurrencyInstance()) != CurrencyIfc.EQUALS) {
					totals.addAmountRestockingFees(extendedRestockingFee.negate());
					totals.addUnitsRestockingFees(units.negate());
				}
			}

			// Update gift certificate issued
			if (pluItem instanceof GiftCertificateItemIfc) {
				if (isSaleOrReturnTransaction) {
					totals.addAmountGrossGiftCertificateIssued(gross);
					totals.addUnitsGrossGiftCertificateIssued(units);
					// totals.addAmountGrossNonTaxableItemSales(gross);
				}
			}

		} // end handle positive gross
		else {
			BigDecimal retUnits = units.abs();
			CurrencyIfc retGross = gross.abs();

			// divvy up sales, units
			switch (it.getTaxMode()) { // begin evaluate tax mode
			case TaxIfc.TAX_MODE_EXEMPT:

				if (isSaleOrReturnTransaction) // Not a PostVoid, since the
												// quantity is negative it must
												// be a RETUTN item
				{
					// Gift Cards and Gift Certificates are not counted in item
					// sales
					if (!isGiftItem(pluItem)) {
						totals.addAmountGrossTaxExemptItemReturns(retGross);
						totals.addUnitsGrossTaxExemptItemReturns(retUnits);

						// since tax-exempt sales are a subset of nontaxable,
						// the totals are also included for non-taxable sales
						totals.addAmountGrossNonTaxableItemReturns(retGross);
						totals.addUnitsGrossNonTaxableItemReturns(retUnits);
					}

					if (sc.getItemType() == ItemClassificationConstantsIfc.TYPE_SERVICE) {
						totals.addAmountGrossNonTaxableNonMerchandiseReturns(retGross);
						totals.addUnitsGrossNonTaxableNonMerchandiseReturns(retUnits);
					}
				} else // PostVoid; the quantity is negative, so it must be a
						// SALE item
				{
					// Gift Cards and Gift Certificates are not counted in item
					// sales
					if (!isGiftItem(pluItem)) {
						totals.addAmountGrossTaxExemptItemSalesVoided(retGross);
						totals.addUnitsGrossTaxExemptItemSalesVoided(retUnits);

						// since tax-exempt sales are a subset of nontaxable,
						// the totals are also included for non-taxable sales
						totals.addAmountGrossNonTaxableItemSalesVoided(retGross);
						totals.addUnitsGrossNonTaxableItemSalesVoided(retUnits);
					}

					if (sc.getItemType() == ItemClassificationConstantsIfc.TYPE_SERVICE) {
						totals.addAmountGrossNonTaxableNonMerchandiseSalesVoided(retGross);
						totals.addUnitsGrossNonTaxableNonMerchandiseSalesVoided(retUnits);
					}
				}
				break;
			case TaxIfc.TAX_MODE_TOGGLE_OFF:
			case TaxIfc.TAX_MODE_NON_TAXABLE:
				if (isSaleOrReturnTransaction) // Not a PostVoid, since the
												// quantity is negative it must
												// be a RETUTN item
				{
					// Gift Cards and Gift Certificates are not counted in item
					// sales
					if (!isGiftItem(pluItem)) {
						totals.addAmountGrossNonTaxableItemReturns(retGross);
						totals.addUnitsGrossNonTaxableItemReturns(retUnits);
					}

					if (sc.getItemType() == ItemClassificationConstantsIfc.TYPE_SERVICE) {
						totals.addAmountGrossNonTaxableNonMerchandiseReturns(retGross);
						totals.addUnitsGrossNonTaxableNonMerchandiseReturns(retUnits);
					}
				} else // PostVoid; the quantity is negative, so it must be a
						// SALE item
				{
					if (!isGiftItem(pluItem)) {
						totals.addAmountGrossNonTaxableItemSalesVoided(retGross);
						totals.addUnitsGrossNonTaxableItemSalesVoided(retUnits);
					}

					if (sc.getItemType() == ItemClassificationConstantsIfc.TYPE_SERVICE) {
						totals.addAmountGrossNonTaxableNonMerchandiseSalesVoided(retGross);
						totals.addUnitsGrossNonTaxableNonMerchandiseSalesVoided(retUnits);
					}
				}
				break;
			case TaxIfc.TAX_MODE_RETURN_RATE:
				if (isSaleOrReturnTransaction) // Not a PostVoid, since the
												// quantity is negative it must
												// be a RETUTN item
				{
					// check if rate is nonzero and handle appropriately
					if (returnItem.getTaxRate() == 0.00) {
						// Gift Cards and Gift Certificates are not counted in
						// item sales
						if (!isGiftItem(pluItem)) {
							totals.addAmountGrossNonTaxableItemReturns(retGross);
							totals.addUnitsGrossNonTaxableItemReturns(retUnits);
						}

						if (sc.getItemType() == ItemClassificationConstantsIfc.TYPE_SERVICE) {
							totals.addAmountGrossNonTaxableNonMerchandiseReturns(retGross);
							totals.addUnitsGrossNonTaxableNonMerchandiseReturns(retUnits);
						}
					} else {
						// Gift Cards and Gift Certificates are not counted in
						// item sales
						if (!isGiftItem(pluItem)) {
							totals.addAmountGrossTaxableItemReturns(retGross);
							totals.addUnitsGrossTaxableItemReturns(retUnits);
							totals.addAmountTaxItemReturns(ip.getItemTaxAmount().abs());
							totals.addAmountInclusiveTaxItemReturns(ip.getItemInclusiveTaxAmount().abs());

							// Save all the separate tax rules into totals
							TaxInformationIfc[] taxInformation = it.getTaxInformationContainer().getTaxInformation();
							TaxTotalsContainerIfc container = DomainGateway.getFactory()
									.getTaxTotalsContainerInstance();
							for (int i = 0; i < taxInformation.length; i++) {
								TaxTotalsIfc taxTotalsItem = new TaxTotals(taxInformation[i]);
								container.addTaxTotals(taxTotalsItem);
							}
							totals.addTaxes(container);
						}

						if (sc.getItemType() == ItemClassificationConstantsIfc.TYPE_SERVICE) {
							totals.addAmountGrossTaxableNonMerchandiseReturns(retGross);
							totals.addUnitsGrossTaxableNonMerchandiseReturns(retUnits);
						}
					}
				} else // PostVoid; the quantity is negative, so it must be a
						// SALE item
				{
					// check if rate is nonzero and handle appropriately
					if (returnItem.getTaxRate() == 0.00) {
						if (!isGiftItem(pluItem)) {
							totals.addAmountGrossNonTaxableItemSalesVoided(retGross);
							totals.addUnitsGrossNonTaxableItemSalesVoided(retUnits);
						}

						if (sc.getItemType() == ItemClassificationConstantsIfc.TYPE_SERVICE) {
							totals.addAmountGrossNonTaxableNonMerchandiseSalesVoided(retGross);
							totals.addUnitsGrossNonTaxableNonMerchandiseSalesVoided(retUnits);
						}
					} else {
						if (!isGiftItem(pluItem)) {
							totals.addAmountGrossTaxableItemSalesVoided(retGross);
							totals.addUnitsGrossTaxableItemSalesVoided(retUnits);
							totals.addAmountTaxItemSales(ip.getItemTaxAmount().abs().negate());
							totals.addAmountInclusiveTaxItemSales(ip.getItemInclusiveTaxAmount().abs().negate());

							// Save all the separate tax rules into totals
							TaxInformationIfc[] taxInformation = it.getTaxInformationContainer().getTaxInformation();
							TaxTotalsContainerIfc container = DomainGateway.getFactory()
									.getTaxTotalsContainerInstance();
							for (int i = 0; i < taxInformation.length; i++) {
								TaxTotalsIfc taxTotalsItem = new TaxTotals(taxInformation[i]);
								container.addTaxTotals(taxTotalsItem);
							}
							totals.addTaxes(container);
						}

						if (sc.getItemType() == ItemClassificationConstantsIfc.TYPE_SERVICE) {
							totals.addAmountGrossTaxableNonMerchandiseSalesVoided(retGross);
							totals.addUnitsGrossTaxableNonMerchandiseSalesVoided(retUnits);
						}
					}
				}
				break;

			default:
				if (isSaleOrReturnTransaction) // Not a PostVoid, since the
												// quantity is negative it must
												// be a RETUTN item
				{
					// Gift Cards and Gift Certificates are not counted in item
					// sales
					if (!isGiftItem(pluItem)) {
						totals.addAmountGrossTaxableItemReturns(retGross);
						totals.addUnitsGrossTaxableItemReturns(retUnits);
						totals.addAmountTaxItemReturns(ip.getItemTaxAmount().abs());
						totals.addAmountInclusiveTaxItemReturns(ip.getItemInclusiveTaxAmount().abs());

						// Save all the separate tax rules into totals
						TaxInformationIfc[] taxInformation = it.getTaxInformationContainer().getTaxInformation();
						TaxTotalsContainerIfc container = DomainGateway.getFactory().getTaxTotalsContainerInstance();
						for (int i = 0; i < taxInformation.length; i++) {
							TaxTotalsIfc taxTotalsItem = new TaxTotals(taxInformation[i]);
							container.addTaxTotals(taxTotalsItem);
						}
						totals.addTaxes(container);
					}

					if (sc.getItemType() == ItemClassificationConstantsIfc.TYPE_SERVICE) {
						totals.addAmountGrossTaxableNonMerchandiseReturns(retGross);
						totals.addUnitsGrossTaxableNonMerchandiseReturns(retUnits);
					}
				} else // PostVoid; the quantity is negative, so it must be a
						// SALE item
				{
					if (!isGiftItem(pluItem)) {
						totals.addAmountGrossTaxableItemSalesVoided(retGross);
						totals.addUnitsGrossTaxableItemSalesVoided(retUnits);
						totals.addAmountTaxItemSales(ip.getItemTaxAmount().abs().negate());
						totals.addAmountInclusiveTaxItemSales(ip.getItemInclusiveTaxAmount().abs().negate());

						// Save all the separate tax rules into totals
						TaxInformationIfc[] taxInformation = it.getTaxInformationContainer().getTaxInformation();
						TaxTotalsContainerIfc container = DomainGateway.getFactory().getTaxTotalsContainerInstance();
						for (int i = 0; i < taxInformation.length; i++) {
							TaxTotalsIfc taxTotalsItem = new TaxTotals(taxInformation[i]);
							container.addTaxTotals(taxTotalsItem);
						}
						totals.addTaxes(container);
					}

					if (sc.getItemType() == ItemClassificationConstantsIfc.TYPE_SERVICE) {
						totals.addAmountGrossTaxableNonMerchandiseSalesVoided(retGross);
						totals.addUnitsGrossTaxableNonMerchandiseSalesVoided(retUnits);
					}
				}
				break;
			} // end evaluate tax mode

			// Update Gift Card Returns
			if (isGiftItem()) {
				if (isSaleOrReturnTransaction) // Not a PostVoid, since the
												// quantity is negative it must
												// be a RETUTN item
				{
					totals.addAmountGrossGiftCardItemReturns(retGross);
					totals.addUnitsGrossGiftCardItemReturns(retUnits);
				} else // assuming it's a void of a SALE
				{
					GiftCardPLUItemIfc giftCardPLUItem = (GiftCardPLUItemIfc) getPLUItem();
					GiftCardIfc giftCard = giftCardPLUItem.getGiftCard();
					if (giftCard != null) {
						switch (giftCard.getRequestType()) {
						case (GiftCardIfc.GIFT_CARD_ISSUE_VOID): {
							totals.addAmountGrossGiftCardItemIssueVoided(retGross);
							totals.addUnitsGrossGiftCardItemIssueVoided(retUnits);
							break;
						}
						case (GiftCardIfc.GIFT_CARD_RELOAD_VOID): {
							totals.addAmountGrossGiftCardItemReloadVoided(retGross);
							totals.addUnitsGrossGiftCardItemReloadVoided(retUnits);
							break;
						}
						case (GiftCardIfc.GIFT_CARD_CREDIT_ISSUE_VOID): {
							totals.addAmountGrossGiftCardItemCreditVoided(retGross);
							totals.addUnitsGrossGiftCardItemCreditVoided(retUnits);
							break;
						}
						case (GiftCardIfc.GIFT_CARD_CREDIT_RELOAD_VOID): {
							totals.addAmountGrossGiftCardItemCreditVoided(retGross);
							totals.addUnitsGrossGiftCardItemCreditVoided(retUnits);
							break;
						}
						}
					}
				}

				// totals.addAmountGrossNonTaxableItemSales(gross);
			}

			// update voided gift certificates issued
			if (getPLUItem() instanceof GiftCertificateItemIfc) {
				if (isSaleOrReturnTransaction) // Not a PostVoid, since the
												// quantity is negative it must
												// be a RETUTN item
				{
					totals.addAmountGrossGiftCertificateIssuedVoided(retGross);
					totals.addUnitsGrossGiftCertificateIssuedVoided(retUnits);
					// totals.addAmountGrossNonTaxableItemSales(gross);
				}
			}

			// For a return increase the total restocking fees amount and units
			// sold
			if ((isSaleOrReturnTransaction) && (extendedRestockingFee != null)) {
				if (extendedRestockingFee.compareTo(DomainGateway.getBaseCurrencyInstance()) != CurrencyIfc.EQUALS) {
					totals.addAmountRestockingFees(extendedRestockingFee.abs());
					totals.addUnitsRestockingFees(retUnits);
				}
			}
		}

		// process item discounts
		FinancialTotalsIfc dsft = null;
		dsft = ip.getItemDiscountsFinancialTotals();
		totals.add(dsft);

		// For the void of a return decrease the total restocking fees amount
		// and units sold
		if (ip.getItemPriceOverrideReasonCode() != CodeConstantsIfc.CODE_INTEGER_UNDEFINED) {
			totals.addUnitsPriceOverrides(units);
			totals.addAmountPriceOverrides(gross);
		}

		// pass back totals
		return (totals);
	} // end getFinancialTotals()
		// ---------------------------------------------------------------------

	/**
	 * Returns registry.
	 * <P>
	 * 
	 * @return registry
	 **/
	// ---------------------------------------------------------------------
	public RegistryIDIfc getRegistry() {
		return (registry);
	}

	// ---------------------------------------------------------------------
	/**
	 * Sets registry.
	 * <P>
	 * 
	 * @param reg
	 *            registry object
	 **/
	// ---------------------------------------------------------------------
	public void setRegistry(RegistryIDIfc reg) {
		registry = reg;
	}

	// ---------------------------------------------------------------------
	/**
	 * Retrieves item quantity.
	 * <P>
	 * 
	 * @return item quantity
	 **/
	// ---------------------------------------------------------------------
	public BigDecimal getItemQuantityDecimal() {
		return (itemQuantity);
	}

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
	public void setItemQuantity(BigDecimal value) {
		itemQuantity = value;
	}

	// ---------------------------------------------------------------------
	/**
	 * Retrieves quantity returnable. This is equal to the item quantity less
	 * the item quantity returned.
	 * <P>
	 * 
	 * @return quantity returnable
	 **/
	// ---------------------------------------------------------------------
	public BigDecimal getQuantityReturnable() {
		return itemQuantity.subtract(quantityReturned);
	}

	// ---------------------------------------------------------------------
	/**
	 * Retrieves quantity returned.
	 * <P>
	 * 
	 * @return quantity returned
	 **/
	// ---------------------------------------------------------------------
	public BigDecimal getQuantityReturnedDecimal() {
		return (quantityReturned);
	}

	// ---------------------------------------------------------------------
	/**
	 * Sets quantity returned.
	 * <P>
	 * 
	 * @param value
	 *            new quantity
	 **/
	// ---------------------------------------------------------------------
	public void setQuantityReturned(BigDecimal value) {
		quantityReturned = value;
	}

	// ---------------------------------------------------------------------
	/**
	 * Returns item price object.
	 * <P>
	 * 
	 * @return item price object
	 **/
	// ---------------------------------------------------------------------
	public ItemPriceIfc getItemPrice() {
		return (itemPrice);
	}

	// ---------------------------------------------------------------------
	/**
	 * Sets item price attribute.
	 * <P>
	 * 
	 * @param value
	 *            item price reference
	 **/
	// ---------------------------------------------------------------------
	public void setItemPrice(ItemPriceIfc value) {
		itemPrice = value;
	}

	// ---------------------------------------------------------------------
	/**
	 * Returns a boolean indicating whether this is a sale line item. Tests for
	 * itemQuantity greater than 0.
	 * <P>
	 * 
	 * @return true if itemQuantity > 0
	 **/
	// ---------------------------------------------------------------------
	public boolean isSaleLineItem() {
		return (itemQuantity.signum() > 0);
	}

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
	public boolean isReturnLineItem() {
		return (!isPriceAdjustmentLineItem() && itemQuantity.signum() < 0 && returnItem != null);
	}

	// ---------------------------------------------------------------------
	/**
	 * Returns a boolean indicating whether this is an order line item.
	 * 
	 * @return true if item is an order line item, false otherwise
	 **/
	// ---------------------------------------------------------------------
	public boolean isOrderItem() { // begin isOrderItem()
		boolean returnFlag = true;
		if (getOrderItemStatus().getStatus().getStatus() == EYSStatusIfc.STATUS_UNDEFINED) {
			returnFlag = false;
		}
		return (returnFlag);
	} // end isOrderItem()

	// ---------------------------------------------------------------------
	/**
	 * Returns a boolean indicating whether this item has a unit of measure
	 * other than the default. Tests for UnitID not equal to "UN".
	 * <P>
	 * 
	 * @return true if (! UnitID.equals("UN"))
	 **/
	// ---------------------------------------------------------------------
	public boolean isUnitOfMeasureItem() {
		// Delegate
		return DomainUtil.isUnitOfMeasureItem(pluItem);
	}

	// ---------------------------------------------------------------------
	/**
	 * Returns PLU item.
	 * <P>
	 * 
	 * @return PLU item
	 **/
	// ---------------------------------------------------------------------
	/*
	 * public PLUItemIfc getPLUItem() { return (pluItem); }
	 */
	// <!-- MAX Rev 1.0 Change : Start -->
	public PLUItemIfc getPLUItem() {
		return (PLUItemIfc) pluItem;
	}

	// <!-- MAX Rev 1.0 Change : end -->
	// ---------------------------------------------------------------------
	/**
	 * Returns PLU item identifier.
	 * <P>
	 * 
	 * @return PLU item identifier
	 **/
	// ---------------------------------------------------------------------
	public String getPLUItemID() {
		if (pluItem != null && pluItemID == null) {
			pluItemID = pluItem.getPosItemID();
		}
		return (pluItemID);
	}

	// ---------------------------------------------------------------------
	/**
	 * Returns item serial number.
	 * <P>
	 * 
	 * @return item serial number
	 **/
	// ---------------------------------------------------------------------
	public String getItemSerial() {
		return (itemSerial);
	}

	// ---------------------------------------------------------------------
	/**
	 * Sets PLU item.
	 * <P>
	 * 
	 * @param value
	 *            PLU item
	 **/
	// ---------------------------------------------------------------------
	public void setPLUItem(PLUItemIfc value) {
		pluItem = value;
	}

	// ---------------------------------------------------------------------
	/**
	 * Sets PLU item ID.
	 * <P>
	 * 
	 * @param value
	 *            PLU item ID
	 **/
	// ---------------------------------------------------------------------
	public void setPLUItemID(String value) {
		pluItemID = value;
	}

	// ---------------------------------------------------------------------
	/**
	 * Sets item serial number.
	 * <P>
	 * 
	 * @param value
	 *            item serial number
	 **/
	// ---------------------------------------------------------------------
	public void setItemSerial(String value) {
		itemSerial = value;
	}

	// ---------------------------------------------------------------------
	/**
	 * Returns return item.
	 * <P>
	 * 
	 * @return return item
	 */
	// ---------------------------------------------------------------------
	public ReturnItemIfc getReturnItem() {
		return (returnItem);
	}

	// ---------------------------------------------------------------------
	/**
	 * Sets return item.
	 * <P>
	 * 
	 * @param value
	 *            return item
	 */
	// ---------------------------------------------------------------------
	public void setReturnItem(ReturnItemIfc value) {
		returnItem = value;
	}

	/**
	 * Set whether or not this sale item came from an already tendered
	 * transaction.
	 * 
	 * @param val
	 *            true or false
	 */
	public void setFromTransaction(boolean val) {
		this.fromTransaction = val;
	}

	/**
	 * Get whether or not this line item came from an already tendered
	 * transaction.
	 * 
	 * @return true or false
	 */
	public boolean isFromTransaction() {
		return this.fromTransaction;
	}

	// ---------------------------------------------------------------------
	/**
	 * Returns order item status.
	 * <P>
	 * 
	 * @return order item status
	 */
	// ---------------------------------------------------------------------
	public OrderItemStatusIfc getOrderItemStatus() {
		return (orderItemStatus);
	}

	// ---------------------------------------------------------------------
	/**
	 * Sets order item status.
	 * <P>
	 * 
	 * @param value
	 *            order item status
	 */
	// ---------------------------------------------------------------------
	public void setOrderItemStatus(OrderItemStatusIfc value) {
		orderItemStatus = value;
	}

	// ---------------------------------------------------------------------
	/**
	 * Returns line item reference.
	 * <P>
	 * 
	 * @return line item reference
	 */
	// ---------------------------------------------------------------------
	public String getLineReference() {
		return (lineReference);
	}

	// ---------------------------------------------------------------------
	/**
	 * Sets order item status.
	 * <P>
	 * 
	 * @param value
	 *            order item status
	 */
	// ---------------------------------------------------------------------
	public void setLineReference(String value) {
		lineReference = value;
	}

	// ---------------------------------------------------------------------
	/**
	 * Returns order item reference.
	 * <P>
	 * 
	 * @return order item reference
	 */
	// ---------------------------------------------------------------------
	public int getOrderLineReference() {
		return (orderLineReference);
	}

	// ---------------------------------------------------------------------
	/**
	 * Sets order item reference.
	 * <P>
	 * 
	 * @param value
	 *            order item reference
	 */
	// ---------------------------------------------------------------------
	public void setOrderLineReference(int value) {
		orderLineReference = value;
	}

	// ---------------------------------------------------------------------
	/**
	 * Returns entry method.
	 * <P>
	 * 
	 * @return entry method
	 */
	// ---------------------------------------------------------------------
	public EntryMethod getEntryMethod() {
		return (entryMethod);
	}

	// ---------------------------------------------------------------------
	/**
	 * Sets entry method.
	 * <P>
	 * 
	 * @param value
	 *            entry method
	 */
	// ---------------------------------------------------------------------
	public void setEntryMethod(EntryMethod value) {
		entryMethod = value;
	}

	// ----------------------------------------------------------------------------
	/**
	 * Retrieves indicator item is eligible for discounting.
	 * <P>
	 * 
	 * @return indicator item is eligible for discounting
	 */
	// ----------------------------------------------------------------------------
	public boolean getDiscountEligible() {
		// default discount eligible to true
		boolean discountEligible = true;
		// get from PLU item if possible
		if (getPLUItem() != null) {
			discountEligible = getPLUItem().getDiscountEligible();
		}
		return (discountEligible);
	}

	// ----------------------------------------------------------------------------
	/**
	 * Retrieves indicator item is eligible for discounting.
	 * <P>
	 * 
	 * @return indicator item is eligible for discounting
	 */
	// ----------------------------------------------------------------------------
	public boolean isDiscountEligible() {
		return (getDiscountEligible());
	}

	// ---------------------------------------------------------------------
	/**
	 * Determines if two objects are identical.
	 * <P>
	 * 
	 * @param obj
	 *            object to compare with
	 * @return true if the objects are identical, false otherwise
	 */
	// ---------------------------------------------------------------------
	public boolean equals(Object obj) { // begin equals()
		boolean isEqual = false;

		try {
			MAXSaleReturnLineItem c = (MAXSaleReturnLineItem) obj;

			if (this == obj) {
				isEqual = true;
			}
			// compare all the attributes of SaleReturnLineItem
			else if (!super.equals(obj)) {
				isEqual = false;
			} else if (!Util.isObjectEqual(getItemQuantityDecimal(), c.getItemQuantityDecimal())) {
				isEqual = false;
			} else if (!Util.isObjectEqual(getQuantityReturnedDecimal(), c.getQuantityReturnedDecimal())) {
				isEqual = false;
			} else if (!Util.isObjectEqual(registry, c.getRegistry())) {
				isEqual = false;
			} else if (!Util.isObjectEqual(itemPrice, c.getItemPrice())) {
				isEqual = false;
			} else if (!Util.isObjectEqual(pluItem, c.getPLUItem())) {
				isEqual = false;
			} else if (!Util.isObjectEqual(returnItem, c.getReturnItem())) {
				isEqual = false;
			} else if (!Util.isObjectEqual(advancedPricingDiscount, c.getAdvancedPricingDiscount())) {
				isEqual = false;
			} else if (!Util.isObjectEqual(itemSerial, c.getItemSerial())) {
				isEqual = false;
			} else if (!sourceAvailable == c.sourceAvailable) {
				isEqual = false;
			} else if (!itemSendFlag == c.itemSendFlag) {
				isEqual = false;
			} else if (!fromTransaction == c.fromTransaction) {
				isEqual = false;
			} else if (!giftReceipt == c.giftReceipt) {
				isEqual = false;
			} else if (!Util.isObjectEqual(getOrderItemStatus(), c.getOrderItemStatus())) {
				isEqual = false;
			} else if (!Util.isObjectEqual(getLineReference(), c.getLineReference())) {
				isEqual = false;
			} else if (!(orderLineReference == c.getOrderLineReference())) {
				isEqual = false;
			} else if (!(kitHeaderReference == c.getKitHeaderReference())) {
				isEqual = false;
			} else if (!(entryMethod == c.getEntryMethod())) {
				isEqual = false;
			} else if (this.relatedItemReturnable != c.isRelatedItemReturnable()) {
				isEqual = false;
			} else if (this.relatedItemSequenceNumber != c.getRelatedItemSequenceNumber()) {
				isEqual = false;
			} else if (this.relatedItemDeleteable != c.isRelatedItemDeleteable()) {
				isEqual = false;
			} else {
				isEqual = true;
			}
		} catch (Exception e) // catching classcastexceptions is faster than
								// instanceof
		{
			isEqual = false;
		}
		return (isEqual);
	} // end equals()

	// ----------------------------------------------------------------------------
	/**
	 * Retrieves itemTaxMethod attribute.
	 * <P>
	 * 
	 * @return itemTaxMethod attribute
	 */
	// ---------------------------------------------------------------------
	public int getItemTaxMethod() {
		return itemTaxMethod;
	}

	// ---------------------------------------------------------------------
	/**
	 * Sets itemTaxMethod attribute.
	 * <P>
	 * 
	 * @param value
	 *            attribute
	 */
	// ---------------------------------------------------------------------
	public void setItemTaxMethod(int value) {
		itemTaxMethod = value;
	}

	// --------------------------------------------------------------------------
	/**
	 * Sets the available flag for this source. This flag indicates whether the
	 * source is currently being used in an advanced pricing strategy.
	 * 
	 * @param value
	 *            boolean indicating availability
	 */
	// --------------------------------------------------------------------------
	public void setSourceAvailable(boolean value) {
		sourceAvailable = value;
	}

	// --------------------------------------------------------------------------
	/**
	 * Returns the available flag for this source.
	 * 
	 * @return boolean indicating source availability
	 */

	// --------------------------------------------------------------------------
	/**
	 * Rev 1.2 changes start here
	 */
	public boolean isSourceAvailable() {
		return sourceAvailable // true if this is already not part of a pricing
								// rule
				&& (isSaleLineItem())// || !isFromTransaction()) // sale items
										// only, unless is a non-receipt return
		// commented by Izhar for enabling weighted items part of DR
		// && !isUnitOfMeasureItem() // uom items are not allowed as part
		// of rules
				&& !isKitHeader(); // kit header items are not allowed as part
									// of rules
	}
	/**
	 * Rev 1.2 changes end here
	 */
	// ---------------------------------------------------------------------
	/**
	 * Tests if this object can be used as a target for an advanced pricing
	 * rule.
	 * 
	 * @return - boolean indicating whether the target functionality described
	 *         by this interface is available.
	 */

	// ---------------------------------------------------------------------
	/**
	 * Rev 1.2 changes start here
	 */
	public boolean isTargetEnabled() {
		// commented by Izhar for enabling weighted items part of DR
		// return getDiscountEligible() && (advancedPricingDiscount == null) &&
		// (!isUnitOfMeasureItem()) && (!isKitHeader());
		return getDiscountEligible() && (advancedPricingDiscount == null) && (!isKitHeader());
	}

	/**
	 * Rev 1.2 changes end here
	 */
	// --------------------------------------------------------------------------
	/**
	 * Returns the quantity of items for this target.
	 * 
	 * @return BigDecimal quantity
	 */
	// --------------------------------------------------------------------------
	public BigDecimal getTargetQuantity() {
		return getItemQuantityDecimal();
	}

	// ---------------------------------------------------------------------
	/**
	 * Returns a String used to test for source or target equality. The String
	 * returned is based on the value of the comparisonBasis argument.
	 * 
	 * @param comparisonBasis
	 *            basis for comparison
	 * @return String value to be used for comparison
	 */
	// ---------------------------------------------------------------------
	public String getComparator(int comparisonBasis) {
		String value = null;

		switch (comparisonBasis) {
		case COMPARISON_BASIS_ITEM_ID:
			value = getPLUItem().getItemID();
			break;
		case COMPARISON_BASIS_DEPARTMENT:
			value = getPLUItem().getDepartmentID();
			break;
		case COMPARISON_BASIS_SUBCLASS:
			value = getPLUItem().getMerchandiseCodesString();
			break;
		default:
			break;

		}
		return value;
	}

	// ---------------------------------------------------------------------
	/**
	 * Returns true if specified classification ID is in list, false otherwise
	 * 
	 * @param classificationID
	 *            classification ID
	 * @return true if specified classification ID is in list, false otherwise
	 */
	// ---------------------------------------------------------------------
	public boolean isClassifiedAs(String classificationID) {
		return this.getPLUItem().getItemClassification().isClassifiedAs(classificationID);
	}

	// --------------------------------------------------------------------------
	/**
	 * Returns the advanced pricing rule which was applied to this target.
	 * 
	 * @return AdvancedPricingRuleIfc
	 */
	// --------------------------------------------------------------------------
	public ItemDiscountStrategyIfc getAdvancedPricingDiscount() {
		return advancedPricingDiscount;
	}

	// --------------------------------------------------------------------------
	/**
	 * Returns the discountRuleID for this target.
	 * 
	 * @return String ruleID
	 */
	// --------------------------------------------------------------------------
	public String getAdvancedPricingRuleID() {
		return (advancedPricingDiscount == null) ? null : advancedPricingDiscount.getRuleID();
	}

	// ---------------------------------------------------------------------
	/**
	 * Modifies the price of a DiscountTarget. Applies a discount to the target
	 * for an advanced pricing rule.
	 * 
	 * @param discount
	 *            the rule to apply to this target
	 */
	// ---------------------------------------------------------------------
	public void applyAdvancedPricingDiscount(ItemDiscountStrategyIfc discount) {
		advancedPricingDiscount = discount;
		itemPrice.addItemDiscount(advancedPricingDiscount);
		calculateLineItemPrice();
		setSourceAvailable(false);
	}

	// --------------------------------------------------------------------------
	/**
	 * Removes an advanced pricing rule that was previously applied to this
	 * target.
	 */
	// --------------------------------------------------------------------------
	public void removeAdvancedPricingDiscount() {
		if (advancedPricingDiscount != null) {
			ArrayList discounts = new ArrayList(Arrays.asList(itemPrice.getItemDiscounts()));
			DiscountRuleIfc rule = null;

			for (Iterator i = discounts.iterator(); i.hasNext();) {
				rule = (DiscountRuleIfc) i.next();
				if (rule.getRuleID().equals(advancedPricingDiscount.getRuleID())) {
					i.remove();
				}
			}

			ItemDiscountStrategyIfc[] da = new ItemDiscountStrategyIfc[discounts.size()];
			discounts.toArray(da);
			itemPrice.setItemDiscounts(da);

			advancedPricingDiscount = null;

			calculateLineItemPrice();

			setSourceAvailable(true);

		} // end if (advancedPricingDiscount != null)
	}

	// ----------------------------------------------------------------------------
	/**
	 * Returns true if this PLUItem is a kit header item, false otherwise.
	 * 
	 * @return boolean
	 */
	// ----------------------------------------------------------------------------
	public boolean isKitHeader() {
		return false;
	}

	// ----------------------------------------------------------------------------
	/**
	 * Returns true if this PLUItem is a kit component item, false otherwise.
	 * 
	 * @return boolean
	 */
	// ----------------------------------------------------------------------------
	public boolean isKitComponent() {
		return false;
	}

	// ----------------------------------------------------------------------------
	/**
	 * Retrieves the kit header reference value if item is part of a kit or -1
	 * if it is not.
	 * 
	 * @return int ID
	 */
	// ----------------------------------------------------------------------------
	public int getKitHeaderReference() {
		return kitHeaderReference;
	}

	// ----------------------------------------------------------------------------
	/**
	 * Sets the kit header reference value for this kit component. Value is
	 * derived from the hashCode of the kit header item.
	 * <P>
	 * 
	 * @param id
	 *            the kit header reference
	 */
	// ----------------------------------------------------------------------------
	public void setKitHeaderReference(int id) {
		kitHeaderReference = id;
	}

	// ----------------------------------------------------------------------------
	/**
	 * Retrieves indicator item requires collection of a serial number.
	 * 
	 * @return indicator item requires collection of a serial number
	 */
	// ----------------------------------------------------------------------------
	public boolean isSerializedItem() {
		return pluItem.isSerializedItem();
	}

	// --------------------------------------------------------------------------
	/**
	 * Returns true if this is a non-merchandise item.
	 * 
	 * @return true if non-merchandise (service) item, false if not
	 **/
	// --------------------------------------------------------------------------
	public boolean isServiceItem() {
		boolean serviceItem = false;
		if (pluItem != null
				&& (pluItem.getItemClassification().getItemType() == ItemClassificationConstantsIfc.TYPE_SERVICE)) {
			serviceItem = true;
		}
		return serviceItem;
	}

	// ----------------------------------------------------------------------------
	/**
	 * Retrieves indicator item is eligible for special order.
	 * 
	 * @return indicator item is eligible for special order
	 */
	// ----------------------------------------------------------------------------
	public boolean isSpecialOrderEligible() {
		return pluItem.isSpecialOrderEligible();
	}

	// ---------------------------------------------------------------------
	/**
	 * This method is used as a test method to know if this is a Send Item. It
	 * checks if this line item is associated with a particular Send Count
	 * greater than 0 and also that it is not a return line item.
	 * 
	 * @return boolean true if this is a send item, false otherwise
	 */
	// ---------------------------------------------------------------------
	public boolean getItemSendFlag() {
		return (itemSendFlag && (sendLabelCount > 0) && !isReturnLineItem());
	}

	// ---------------------------------------------------------------------
	/**
	 * Sets item send flag attribute.
	 * <P>
	 * 
	 * @param value
	 *            send flag
	 */
	// ---------------------------------------------------------------------
	public void setItemSendFlag(boolean value) {
		itemSendFlag = value;
	}

	// ---------------------------------------------------------------------
	/**
	 * Returns true if this item can be sent, false otherwise.
	 * 
	 * @return boolean
	 */
	// ---------------------------------------------------------------------
	public boolean isEligibleForSend() {
		if (getPLUItem().getItemClassification().getItemType() == ItemClassificationConstantsIfc.TYPE_SERVICE) {
			return false;
		} else {
			return true;
		}
	}

	// --------------------------------------------------------------------------
	/**
	 * Sets the gift receipt flag for this item. This flag indicates whether the
	 * item needs to have a gift receipt printed.
	 * 
	 * @param value
	 *            boolean indicating if a gift receipt should be printed.
	 */
	// --------------------------------------------------------------------------
	public void setGiftReceiptItem(boolean value) {
		giftReceipt = value;
	}

	// --------------------------------------------------------------------------
	/**
	 * Returns the gift receipt flag for this item.
	 * 
	 * @return boolean indicating if a gift receipt should be printed.
	 */
	// --------------------------------------------------------------------------
	public boolean isGiftReceiptItem() {
		return giftReceipt;

	}

	// --------------------------------------------------------------------------
	/**
	 * Sets the alteration item flag for this item. This flag indicates whether
	 * the item needs to have an alteration receipt printed.
	 * <P>
	 * 
	 * @param value
	 *            boolean indicating if an alteration receipt should be printed.
	 */
	// --------------------------------------------------------------------------
	public void setAlterationItemFlag(boolean value) {
		alterationItemFlag = value;
	}

	// --------------------------------------------------------------------------
	/**
	 * Gets the alteration item flag for this item. This flag indicates whether
	 * the item needs to have an alteration receipt printed.
	 * <P>
	 * 
	 * @return true if an alteration receipt should be printed.
	 */
	// --------------------------------------------------------------------------
	public boolean getAlterationItemFlag() {
		return alterationItemFlag;
	}

	// --------------------------------------------------------------------------
	/**
	 * Returns the alteration flag for this item.
	 * <P>
	 * 
	 * @return Whether the item is an alteration item.
	 */
	// --------------------------------------------------------------------------
	public boolean isAlterationItem() {
		return alterationItemFlag;
	}

	// ----------------------------------------------------------------------
	/**
	 * This method checks to see if the item is a gift card isssue. We assume it
	 * is an issue if the status is active as apposed to reload.
	 * 
	 * @return Whether the item is a gift card issue.
	 */
	// ----------------------------------------------------------------------
	public boolean isGiftCardIssue() {
		boolean returnCode = false;
		PLUItemIfc item = getPLUItem();
		if (item instanceof GiftCardPLUItemIfc) {
			GiftCardIfc giftCard = ((GiftCardPLUItemIfc) item).getGiftCard();
			if (giftCard.getRequestType() == GiftCardIfc.GIFT_CARD_ISSUE) {
				returnCode = true;
			}
		}
		return returnCode;
	}

	// ----------------------------------------------------------------------
	/**
	 * This method checks to see if the item is a gift card reload.
	 * 
	 * @return Whether the item is a gift card reload.
	 */
	// ----------------------------------------------------------------------
	public boolean isGiftCardReload() {
		boolean returnCode = false;
		PLUItemIfc item = getPLUItem();
		if (item instanceof GiftCardPLUItemIfc) {
			GiftCardIfc giftCard = ((GiftCardPLUItemIfc) item).getGiftCard();
			if (giftCard.getRequestType() == GiftCardIfc.GIFT_CARD_RELOAD) {
				returnCode = true;
			}
		}
		return returnCode;
	}

	// ----------------------------------------------------------------------
	/**
	 * Check through all discount to see if there is a damage discount related
	 * to this item.
	 * 
	 * @return isDamageDiscount boolean
	 */
	// ----------------------------------------------------------------------
	public boolean hasDamageDiscount() {
		boolean returnCode = false;
		ItemDiscountStrategyIfc[] discArray = getItemDiscountsByAmount();
		// check item discounts by amount
		for (int i = 0; i < discArray.length; i++) {
			ItemDiscountStrategyIfc discount = discArray[i];
			if (discount.isDamageDiscount()) {
				returnCode = true;
				break;
			}
		}
		// check item discount by percentage
		if (returnCode == false) {
			discArray = getItemDiscountsByPercentage();
			for (int i = 0; i < discArray.length; i++) {
				ItemDiscountStrategyIfc discount = discArray[i];
				if (discount.isDamageDiscount()) {
					returnCode = true;
					break;
				}
			}
		}
		return returnCode;
	}

	// ----------------------------------------------------------------------
	/**
	 * Checks through all discounts to see if there is an employee discount
	 * related to this item.
	 * 
	 * @return boolean true only if this item has an employee discount applied
	 */
	// ----------------------------------------------------------------------
	public boolean hasEmployeeDiscount() {
		boolean returnCode = false;
		ItemDiscountStrategyIfc[] discArray = getItemDiscountsByAmount();
		// check item discounts by amount
		for (int i = 0; i < discArray.length; i++) {
			ItemDiscountStrategyIfc discount = discArray[i];
			if (discount.getDiscountEmployee() != null
					&& !Util.isEmpty(discount.getDiscountEmployee().getEmployeeID())) {
				returnCode = true;
				break;
			}
		}
		// check item discount by percentage
		if (returnCode == false) {
			discArray = getItemDiscountsByPercentage();
			for (int i = 0; i < discArray.length; i++) {
				ItemDiscountStrategyIfc discount = discArray[i];
				if (discount.getDiscountEmployee() != null
						&& !Util.isEmpty(discount.getDiscountEmployee().getEmployeeID())) {
					returnCode = true;
					break;
				}
			}
		}
		return returnCode;
	}

	// ---------------------------------------------------------------------
	/**
	 * Check if this item meets the returnable criteria
	 * 
	 * @return is item returnable
	 */
	// ---------------------------------------------------------------------
	public boolean isReturnable() {
		boolean isReturnableItem = false;
		if (getPLUItem() != null) {
			// To determine if an item is returnable, must fulfill all the
			// following conditions:
			// 1. is not a return item
			// 2. is return elegible
			// 3. has a return quatity available
			// 4. does not have a damage discount
			// 5. is not a gift certificate
			boolean returnElegible = getPLUItem().getItemClassification().getReturnEligible();
			if (!isReturnLineItem() && returnElegible && (getQuantityReturnable().signum() > 0) && !hasDamageDiscount()
					&& !(getPLUItem() instanceof GiftCertificateItemIfc)) {
				isReturnableItem = true;
			}
		}
		return isReturnableItem;
	}

	// ---------------------------------------------------------------------
	/**
	 * Returns string representation of object.
	 * <P>
	 * 
	 * @return String representation of object
	 */
	// ---------------------------------------------------------------------
	public String toString() { // begin toString()
								// result string
		String giftReceiptStr = "false";
		String strResult = new String(
				"Class:  SaleReturnLineItem (Revision " + getRevisionNumber() + ") @" + hashCode());
		strResult += "\n" + super.toString();

		strResult += "\n\tQuantity:          [" + itemQuantity + "]";
		strResult += "\n\tQuantity returned: [" + quantityReturned + "]";
		strResult += "\n\tEntry method:      [" + entryMethod.toString() + "]";
		strResult += "\n\tItemTaxMethod: [" + ItemTaxIfc.ITEM_TAX_METHOD_DESCRIPTORS[itemTaxMethod] + "]";
		if (giftReceipt) {
			giftReceiptStr = "true";
		}
		strResult += "\n\tGift Receipt Flag: [" + giftReceiptStr + "]";
		if (pluItem == null) {
			strResult += "\n\n\tPLU Item:\t(null)";
		} else {
			strResult += "\n\n\t" + pluItem.toString();
		}
		if (itemSerial != null) {
			strResult += "\n\tItem Serial Number:" + itemSerial;
		}
		if (returnItem == null) {
			strResult += "\n\n\tReturn Item:\t(null)";
		} else {
			strResult += "\n\n\t" + returnItem.toString();
		}
		if (itemPrice == null) {
			strResult += "\n\n\tItem Price:\t(null)";
		} else {
			strResult += "\n\n\t" + itemPrice.toString();
		}
		if (registry == null) {
			strResult += "\n\n\tGift Registry:\t(null)";
		} else {
			strResult += "\n\n\t" + registry.toString();
		}
		if (advancedPricingDiscount == null) {
			strResult += "\n\n\tAdvanced Pricing Discount:\t(null)";
		} else {
			strResult += "\n\n\t" + advancedPricingDiscount.toString();
		}
		if (orderItemStatus == null) {
			strResult += "\n\n\tOrder Item Status:\t(null)";
		} else {
			strResult += "\n\n\t" + orderItemStatus.toString();
		}
		strResult += "\n\tLine reference:    [" + getLineReference() + "]";
		strResult += "\n\tOrder line reference:    [" + getOrderLineReference() + "]";
		strResult += "\n\tIs Price Adjustment Line Item:    [" + isPriceAdjustmentLineItem() + "]";
		strResult += "\n\tIs Price Adjustment Line Item Component:    [" + isPartOfPriceAdjustment() + "]";
		strResult += "\n\tRelated Item Sequence Number:    [" + this.getRelatedItemSequenceNumber() + "]";
		strResult += "\n\tRelated Item is Returnable:      [" + this.isRelatedItemReturnable() + "]";
		// pass back result
		return (strResult);
	} // end toString()

	// ---------------------------------------------------------------------
	/**
	 * Returns default journal string.
	 * <P>
	 * 
	 * @return default journal string
	 */
	// ---------------------------------------------------------------------
	public String toJournalString() {
		StringBuffer strResult = new StringBuffer();
		ItemPriceIfc ip = getItemPrice();

		int taxMode = ip.getItemTax().getTaxMode();
		int taxScope = ip.getItemTax().getTaxScope();

		// Item number
		CurrencyIfc itemPrice = ip.getExtendedSellingPrice();
		Locale locale = LocaleMap.getLocale(LocaleConstantsIfc.JOURNAL);
		String priceString = itemPrice.toGroupFormattedString(locale);
		String priceStringNegated = itemPrice.negate().toGroupFormattedString(locale);
		int signum = itemPrice.getDecimalValue().signum();

		// This EOL is responsible for separating each line item
		// Do not remove it for the sake of removing spare line
		// between header and line items.
		strResult.append(Util.EOL);
		strResult.append("ITEM: ").append(pluItem.getItemID());

		if (pluItem.getItemID() != null) {
			strResult.append(Util.SPACES.substring(pluItem.getItemID().length(), ITEM_NUMBER_LENGTH));
		}

		// Assume quantity is in decimals.
		BigDecimal quantity = getItemQuantityDecimal();
		quantity = quantity.setScale(2);
		if (!isUnitOfMeasureItem()) {
			// However, if we aren't a UoM item, display quantity as an integer
			if (quantity.intValue() == quantity.doubleValue()) {
				quantity = quantity.setScale(0);
			}
		}
		String quantityString = quantity.toString();

		// price -part 1
		int whiteSpace = ITEM_PRICE_LENGTH;
		if (quantityString.startsWith("-")) {
			quantityString = quantityString.replace('-', '(');
			quantityString = quantityString + ")";
			/*
			 * if (!priceString.startsWith("(") && !priceString.startsWith("-"))
			 */
			if (signum >= 0) {
				priceString = priceStringNegated;
			}
			whiteSpace++;
		}

		// price -part 2
		if (priceString.length() < whiteSpace) {
			strResult.append(Util.SPACES.substring(priceString.length(), whiteSpace));
		}
		strResult.append(priceString);

		// Tax Mode
		String taxFlag = new String("T");
		if (taxMode == TaxIfc.TAX_MODE_STANDARD && pluItem.getTaxable() == false) {
			taxFlag = TaxIfc.TAX_MODE_CHAR[TaxIfc.TAX_MODE_NON_TAXABLE];
		} else {
			taxFlag = TaxIfc.TAX_MODE_CHAR[taxMode];
		}
		strResult.append(" ").append(taxFlag);

		// Item description
		strResult.append(Util.EOL).append("  ").append(pluItem.getDescription(locale));

		// Item size
		if (!Util.isEmpty(itemSizeCode)) {
			strResult.append(Util.EOL).append("  Size: ").append(itemSizeCode);
		}

		// Item Quantity and Unit Price
		strResult.append(Util.EOL).append("  Qty: ").append(quantityString).append(" @ ");
		// ++ CR 27545
		String sellingPrice = ip.getSellingPrice().toGroupFormattedString(locale);
		strResult.append(sellingPrice);
		// -- CR 27545
		// If we have a price override, use the override marker.
		String overrideMarker = "";
		if (ip.isPriceOverride()) {
			overrideMarker = OVERRIDE_MARKER;
		}
		strResult.append(overrideMarker);

		// Item serial number
		if (!Util.isEmpty(itemSerial)) {
			strResult.append(Util.EOL).append("  Serial Number: ").append(Util.EOL).append("  ").append(itemSerial);
		}

		// if the PLUItem is a GiftCardPLUItem journal gift card information
		if (pluItem instanceof GiftCardPLUItemIfc) {
			strResult.append(((GiftCardPLUItemIfc) pluItem).getGiftCard().toJournalString());
		}

		// journal non-standard tax
		if (taxMode != TaxIfc.TAX_MODE_STANDARD) {
			if (taxScope == TaxIfc.TAX_SCOPE_ITEM) // tax overirde is at the
													// item level
			{
				strResult.append(ip.getItemTax().toJournalString());
			}
		}

		// Journal return items specific info
		if (isReturnLineItem()) {
			// journal original Trans.
			ReturnItemIfc returnItem = this.getReturnItem();
			if (returnItem.getOriginalTransactionID() != null) {
				strResult.append(Util.EOL).append("  Orig. Trans: ")
						.append(returnItem.getOriginalTransactionID().getTransactionIDString()).append(" ")
						.append(returnItem.getOriginalTransactionID().getBusinessDateString()).append(Util.EOL)
						.append("  Retrieved: ");
				if (returnItem.isFromRetrievedTransaction()) {
					strResult.append("Yes");
				} else {
					strResult.append("No");
				}

				if (returnItem.isFromGiftReceipt()) {
					strResult.append(Util.EOL).append("  Gift Receipt");
				}

			}
			strResult.append(Util.EOL).append("  Item Returned");
		}

		if (getRegistry() != null) {
			strResult.append(Util.EOL).append("  Gift Reg.: ").append(getRegistry().getID());
		}

		// if sales associate modified, write it
		if (getSalesAssociateModifiedFlag() && getSalesAssociate() != null) {
			strResult.append(Util.EOL).append("  Sales Assoc. ").append(getSalesAssociate().getEmployeeID());
		} else {
			ReturnItemIfc ri = getReturnItem();
			// if return, get sales associate
			if (getItemQuantityDecimal().signum() < 0 && ri != null && ri.getSalesAssociate() != null) {
				strResult.append(Util.EOL).append("  Sales Assoc. ").append(ri.getSalesAssociate().getEmployeeID());
			}
		}
		// pass back result

		return (strResult.toString());
	}

	// ----------------------------------------------------------------------
	/**
	 * This method journals the date information.
	 * 
	 * @param date
	 * @return
	 **/
	// ----------------------------------------------------------------------
	public String toJournalString(EYSDate date) {
		String journalString = toJournalString();
		StringBuffer strResult = new StringBuffer();
		strResult.append(journalString).append(Util.EOL);
		// if the item has a restrictive age
		// journal the date or skipped
		if (pluItem.getRestrictiveAge() > 0) {
			strResult.append("Minimum age: ").append(pluItem.getRestrictiveAge()).append(Util.EOL);
			strResult.append("Date of Birth: ");
			// if year less than 1000 then it was skipped
			if (date.getYear() < 1000) {
				strResult.append("Skipped");
			} else {
				Locale locale = LocaleMap.getLocale(LocaleConstantsIfc.DEFAULT_LOCALE);
				strResult.append(date.toFormattedString(locale));
			}
		}

		return (strResult.toString());
	}

	// ----------------------------------------------------------------------
	/**
	 * Journal related item information.
	 * 
	 * @param date
	 * @param itemId
	 * @return
	 **/
	// ----------------------------------------------------------------------
	public String toJournalString(EYSDate date, String itemId) {
		String journalString;
		if (date == null) {
			journalString = toJournalString();
		} else {
			journalString = toJournalString(date);
		}
		StringBuffer strResult = new StringBuffer();
		strResult.append(journalString).append(Util.EOL);
		if (itemId != null && relatedItemSequenceNumber > -1) {
			strResult.append("Related Item for Item ").append(itemId);
		}
		return (strResult.toString());
	}

	// ---------------------------------------------------------------------
	/**
	 * Returns journal string when removing an item.
	 * <P>
	 * 
	 * @return journal string when removing an item
	 */
	// ---------------------------------------------------------------------
	public String toJournalDeleteString() {
		ItemPriceIfc ip = getItemPrice();
		CurrencyIfc itemPrice = ip.getExtendedSellingPrice();
		// itemPrice.setDefaultFormat(CurrencyIfc.DEFAULT_FORMAT);
		Locale locale = LocaleMap.getLocale(LocaleConstantsIfc.JOURNAL);
		String priceString = itemPrice.toGroupFormattedString(locale);
		int signum = itemPrice.getDecimalValue().signum();
		StringBuffer strResult = new StringBuffer();

		// Item number
		strResult.append(toItemJournal());

		BigDecimal quantity = getItemQuantityDecimal();

		Integer qtyInt = new Integer(quantity.intValue());
		String quantityString = qtyInt.toString();
		int whiteSpace = ITEM_PRICE_LENGTH;
		if (quantityString.startsWith("-")) {
			quantityString = quantityString.substring(1);
			if (signum == CurrencyIfc.NEGATIVE)// if
												// (priceString.startsWith("("))
			{
				priceString = itemPrice.negate().toGroupFormattedString(locale);// priceString.substring(1,
																				// priceString.length()
																				// -
																				// 1);
			}
		} else {
			quantityString = "(" + quantityString + ")";
			if (signum > CurrencyIfc.NEGATIVE)// if
												// (!priceString.startsWith("("))
			{
				priceString = itemPrice.negate().toGroupFormattedString(locale);// "("
																				// +
																				// priceString
																				// +
																				// ")";
			}
			whiteSpace++;
		}

		// price
		if (priceString.length() < whiteSpace) {
			strResult.append(Util.SPACES.substring(priceString.length(), whiteSpace));
		}
		strResult.append(priceString);

		// Tax flag
		if (this.isReturnLineItem() && this.getTaxable()) {
			strResult.append(" T");
		}

		// Item description
		strResult.append(Util.EOL).append("  ").append(pluItem.getDescription(locale))

				// Item Quantity and Unit Price
				.append(Util.EOL).append("  Qty: ").append(quantityString).append(" @ ");

		strResult.append(ip.getSellingPrice().toGroupFormattedString(locale));

		// If we have a price override, use the override marker.
		String overrideMarker = "";
		if (ip.isPriceOverride()) {
			overrideMarker = OVERRIDE_MARKER;
		}
		strResult.append(overrideMarker);

		// Item tax
		if ((getItemTax() != null) && this.isReturnLineItem() && this.getTaxable()) {
			strResult.append(Util.EOL).append("  Tax: ").append(this.getItemTaxAmount().negate());
		}

		// Item serial number
		if (itemSerial != null) {
			strResult.append(Util.EOL).append("  Serial Number: ").append(Util.EOL).append("  ").append(itemSerial);
		}

		return (strResult.toString());
	}

	// ---------------------------------------------------------------------
	/**
	 * Journals discounts, if they exist.
	 * <P>
	 * 
	 * @param discount
	 *            the discount to be journaled
	 * @param discountRemoved
	 *            true if discout is being removed, false if added.
	 * @return Journal string.
	 */
	// ---------------------------------------------------------------------
	public String toJournalManualDiscount(ItemDiscountStrategyIfc discount, boolean discountRemoved) { // begin
																										// journalDiscounts()
		boolean damageDiscount = false;
		if ((discount.getAssignmentBasis() == DiscountRuleConstantsIfc.ASSIGNMENT_MANUAL)
				&& discount.isDamageDiscount()) {
			damageDiscount = true;
		}

		StringBuffer strJournal = new StringBuffer();
		int method = discount.getDiscountMethod();
		int reason = discount.getReasonCode();

		strJournal.append(toItemJournal());

		String discountDescription;
		if (method == DiscountRuleConstantsIfc.DISCOUNT_METHOD_AMOUNT
				|| method == DiscountRuleConstantsIfc.DISCOUNT_METHOD_FIXED_PRICE) {
			discountDescription = "Amt.";
		} else if (method == DiscountRuleConstantsIfc.DISCOUNT_METHOD_PERCENTAGE) {
			BigDecimal discountRate = discount.getDiscountRate();
			discountRate = discountRate.movePointRight(2);
			discountRate = discountRate.setScale(0, BigDecimal.ROUND_HALF_UP);
			discountDescription = discountRate.toString() + "%";
		} else {
			discountDescription = "Unknown";
		}

		CurrencyIfc discountAmount = (CurrencyIfc) discount.getDiscountAmount().clone();
		discountAmount = discountAmount.multiply(getItemQuantityDecimal());
		// discountAmount.setDefaultFormat("(#0.00);#0.00");
		String price;
		Locale locale = LocaleMap.getLocale(LocaleConstantsIfc.JOURNAL);
		if (discountRemoved) {
			// discountAmount.setDefaultFormat("#0.00;#0.00");
			price = discountAmount.toGroupFormattedString(locale);
			strJournal.append(Util.SPACES.substring(price.length(), ITEM_PRICE_LENGTH));
			discountDescription = discountDescription + " Deleted";
		} else {
			price = discountAmount.negate().toGroupFormattedString(locale);
			strJournal.append(Util.SPACES.substring(price.length(), ITEM_PRICE_LENGTH + 1));
		}

		String accntDescription = "  Discount: ";
		String rsnDescription = "  Disc. Rsn.: ";
		if (discount.getAccountingMethod() == DiscountRuleConstantsIfc.ACCOUNTING_METHOD_MARKDOWN) {
			accntDescription = "  Markdown: ";
			rsnDescription = "  Mrkd. Rsn.: ";
		} else if (discount.getAssignmentBasis() == DiscountRuleConstantsIfc.ASSIGNMENT_EMPLOYEE) {
			accntDescription = "  Employee Discount: ";
			rsnDescription = "  Emp. ID.: ";
		} else if (damageDiscount) {
			rsnDescription = "  Damage Discount";
		}

		// Put the pieces together
		strJournal.append(price).append(Util.EOL);
		strJournal.append(accntDescription);
		strJournal.append(discountDescription).append(Util.EOL)
				// need to expand reason
				.append(rsnDescription);
		if (discount.getAssignmentBasis() == DiscountRuleConstantsIfc.ASSIGNMENT_EMPLOYEE) {
			strJournal.append(discount.getDiscountEmployeeID());
		} else if (!damageDiscount) {
			strJournal.append(new Integer(reason).toString());
			String reasonCodeText = discount.getReasonCodeText();
			if (reasonCodeText != null) {
				if (reasonCodeText.length() > 18) {
					reasonCodeText = reasonCodeText.substring(0, 18);
				}
				strJournal.append("-").append(reasonCodeText);
			}
		}

		return strJournal.toString();

	}

	// ---------------------------------------------------------------------
	/**
	 * Method toItemJournal.
	 * 
	 * @return Journal formatted string for the item number
	 */
	// ---------------------------------------------------------------------
	private String toItemJournal() {
		StringBuffer strJournal = new StringBuffer();
		strJournal.append(Util.EOL).append("ITEM: ").append(pluItem.getItemID())
				.append(Util.SPACES.substring(pluItem.getItemID().length(), ITEM_NUMBER_LENGTH));
		return strJournal.toString();
	}

	// ---------------------------------------------------------------------
	/**
	 * Returns journal string when returning an item.
	 * <P>
	 * 
	 * @return journal string when returning an item
	 */
	// ---------------------------------------------------------------------
	public String toJournalRemoveString() {
		StringBuffer sb = new StringBuffer();
		sb.append(toJournalDeleteString()).append(Util.EOL).append("  Item Deleted");
		return sb.toString();
	}

	// ---------------------------------------------------------------------
	/**
	 * Retrieves Pos Item identifier.
	 * <P>
	 * 
	 * @return Pos Item identifier
	 */
	// ---------------------------------------------------------------------
	public String getPosItemID() {
		String posItemID = "";
		if (pluItem != null) {
			posItemID = pluItem.getPosItemID();
		}
		/*
		 * POSLog expects POSItemID to have a value, setting posItemID to
		 * getItemID() in case value is empty. Specifically, this is to address
		 * unknown items sold within POS.
		 */
		if ((posItemID == null) || (posItemID == "")) {
			posItemID = getItemID();
		}
		return posItemID;
	}

	// ---------------------------------------------------------------------
	/**
	 * Restores the object from the contents of the xml tree based on the
	 * current node property of the converter.
	 * 
	 * @param converter
	 *            is the conversion utility
	 * @exception XMLConversionException
	 *                if error occurs transalating from XML
	 */
	// ---------------------------------------------------------------------
	public void translateFromElement(XMLConverterIfc converter) throws XMLConversionException {
		try {
			Element top = converter.getCurrentElement();
			Element[] properties = converter.getChildElements(top, XMLConverterIfc.TAG_PROPERTY);

			// Retrieve and store the values for each property
			for (int i = 0; i < properties.length; i++) {
				Element element = properties[i];
				String name = element.getAttribute("name");

				if ("itemQuantity".equals(name)) {
					itemQuantity = new BigDecimal(converter.getElementText(element));
				} else if ("quantityReturned".equals(name)) {
					quantityReturned = new BigDecimal(converter.getElementText(element));
				} else if ("registry".equals(name)) {
					registry = (RegistryIDIfc) converter.getPropertyObject(element);
				} else if ("itemPrice".equals(name)) {
					itemPrice = (ItemPriceIfc) converter.getPropertyObject(element);
				} else if ("pluItem".equals(name)) {
					pluItem = (PLUItemIfc) converter.getPropertyObject(element);
				} else if ("returnItem".equals(name)) {
					returnItem = (ReturnItemIfc) converter.getPropertyObject(element);
				} else if ("salesAssociate".equals(name)) {
					salesAssociate = (EmployeeIfc) converter.getPropertyObject(element);
				} else if ("lineNumber".equals(name)) {
					lineNumber = new Integer(converter.getElementText(element)).intValue();
				} else {
					// System.out.println(name);
				}
			}
		} catch (Exception e) {
			throw new XMLConversionException(e.toString());
		}
	}

	// ---------------------------------------------------------------------
	/**
	 * Returns the revision number.
	 * <P>
	 * 
	 * @return String representation of revision number
	 */
	// ---------------------------------------------------------------------
	public String getRevisionNumber() {
		return revisionNumber;
	}

	/**
	 * Returns the item's type.
	 * 
	 * @return The item's type.
	 * 
	 * @see com.extendyourstore.domain.lineitem.SaleReturnLineItemIfc#getItemType()
	 */
	public int getItemType() {
		return getPLUItem().getItemClassification().getItemType();
	}

	// ---------------------------------------------------------------------
	/**
	 * @return Returns the itemSizeCode.
	 */
	// ---------------------------------------------------------------------
	public String getItemSizeCode() {
		return itemSizeCode;
	}

	// ---------------------------------------------------------------------
	/**
	 * @param itemSizeCode
	 *            The itemSizeCode to set.
	 */
	// ---------------------------------------------------------------------
	public void setItemSizeCode(String itemSizeCode) {
		this.itemSizeCode = itemSizeCode;
	}

	// ----------------------------------------------------------------------------
	/**
	 * Returns true if this item is a price adjustment item, false otherwise.
	 * 
	 * @return boolean
	 */
	// ----------------------------------------------------------------------------
	public boolean isPriceAdjustmentLineItem() {
		return isPriceAdjustmentLineItem;
	}

	// --------------------------------------------------------------------------
	/**
	 * Returns a boolean indicating whether or not this SaleReturnLineItem is a
	 * part of a price adjustment
	 * 
	 * @return true only if a this instance is not itself a price adjustment and
	 *         the price adjustment reference number is greater than 0
	 * @see com.extendyourstore.domain.lineitem.SaleReturnLineItemIfc#isPartOfPriceAdjustment()
	 **/
	// --------------------------------------------------------------------------
	public boolean isPartOfPriceAdjustment() {
		boolean isPartOfPriceAdjustment = false;

		// The manually set status trumps the calculation for
		// price adjustment components.
		if (manuallySetPartOfPriceAdjustmentFlag) {
			isPartOfPriceAdjustment = this.isPartOfPriceAdjustment;
		} else {
			isPartOfPriceAdjustment = getPriceAdjustmentReference() > 0 && !isPriceAdjustmentLineItem();
		}

		return (isPartOfPriceAdjustment);
	}

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
	public void setIsPartOfPriceAdjustment(boolean isPartOfPriceAdjustment) {
		// Indicate that we are manually forcing the status of whether or not
		// this item is a price adjustment
		manuallySetPartOfPriceAdjustmentFlag = true;

		this.isPartOfPriceAdjustment = isPartOfPriceAdjustment;
	}

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
	public void setIsPriceAdjustmentLineItem(boolean isPriceAdjustmentLineItem) {
		this.isPriceAdjustmentLineItem = isPriceAdjustmentLineItem;
	}

	// ---------------------------------------------------------------------
	/*******************************************************************************************************************
	 * THE FOLLOWING METHODS HAVE BEEN DEPRECATED. *
	 ******************************************************************************************************************/

	// ---------------------------------------------------------------------
	/**
	 * Constructs SaleReturnLineItem object, setting item, tax rate and sales
	 * associate attributes.
	 * <P>
	 * 
	 * @param item
	 *            PLU item
	 * @param tax
	 *            ItemTax object
	 * @param pSalesAssociate
	 *            default sales associate
	 * @deprecated As of release 4.0.0, replaced by
	 *             {@link #SaleReturnLineItem(PLUItemIfc, ItemTaxIfc, EmployeeIfc)}
	 *             .
	 */
	// ---------------------------------------------------------------------
	public MAXSaleReturnLineItem(PLUItem item, ItemTax tax, Employee pSalesAssociate) { // begin
																						// SaleReturnLineItem()
		this(item, BigDecimal.ONE, tax, pSalesAssociate, (RegistryIDIfc) null, (ReturnItemIfc) null);
	} // end SaleReturnLineItem()

	// ---------------------------------------------------------------------
	/**
	 * Constructs SaleReturnLineItem object, setting item, tax rate, sales
	 * associate and registry attributes.
	 * <P>
	 * 
	 * @param item
	 *            PLU item
	 * @param tax
	 *            ItemTax object
	 * @param pSalesAssociate
	 *            default sales associate
	 * @param pRegistry
	 *            default registry
	 * @deprecated As of release 4.0.0, replaced by
	 *             {@link #SaleReturnLineItem(PLUItemIfc, ItemTaxIfc, EmployeeIfc, RegistryIDIfc)}
	 *             .
	 */
	// ---------------------------------------------------------------------
	public MAXSaleReturnLineItem(PLUItem item, ItemTax tax, Employee pSalesAssociate, GiftRegistry pRegistry) { // begin
																												// SaleReturnLineItem()
																												// set
																												// member
																												// attributes
		this(item, tax, pSalesAssociate, pRegistry, (ReturnItemIfc) null);
	} // end SaleReturnLineItem()

	// ---------------------------------------------------------------------
	/**
	 * Constructs SaleReturnLineItem object, setting item, tax rate, sales
	 * associate, registry attributes and return item attributes.
	 * <P>
	 * 
	 * @param item
	 *            PLU item
	 * @param tax
	 *            ItemTax object
	 * @param pSalesAssociate
	 *            default sales associate
	 * @param pRegistry
	 *            default registry
	 * @param pReturnItem
	 *            return item info
	 * @deprecated As of release 4.0.0, replaced by
	 *             {@link #SaleReturnLineItem(PLUItemIfc, ItemTaxIfc, EmployeeIfc, RegistryIDIfc, ReturnItemIfc)}
	 *             .
	 */
	// ---------------------------------------------------------------------
	public MAXSaleReturnLineItem(PLUItem item, ItemTax tax, Employee pSalesAssociate, GiftRegistry pRegistry,
			ReturnItem pReturnItem) { // begin
										// SaleReturnLineItem()
		this((PLUItemIfc) item, (ItemTaxIfc) tax, (EmployeeIfc) pSalesAssociate, (RegistryIDIfc) pRegistry,
				(ReturnItemIfc) pReturnItem);
	} // end SaleReturnLineItem()
		// ---------------------------------------------------------------------

	/**
	 * Constructs SaleReturnLineItem object, setting item, tax rate, sales
	 * associate, registry attributes and return item attributes.
	 * <P>
	 * 
	 * @param item
	 *            PLU item
	 * @param itemQuantity
	 *            item quantity
	 * @param tax
	 *            ItemTax object
	 * @param pSalesAssociate
	 *            default sales associate
	 * @param pRegistry
	 *            default registry
	 * @param pReturnItem
	 *            return item info
	 * @deprecated As of release 4.0.0, replaced by
	 *             {@link #SaleReturnLineItem(PLUItemIfc, BigDecimal, ItemTaxIfc, EmployeeIfc, RegistryIDIfc, ReturnItemIfc)}
	 */
	// ---------------------------------------------------------------------
	public MAXSaleReturnLineItem(PLUItem item, long itemQuantity, ItemTax tax, Employee pSalesAssociate,
			GiftRegistry pRegistry, ReturnItem pReturnItem) { // begin
																// SaleReturnLineItem()
																// set
																// member
																// attributes
		this(item, BigDecimal.valueOf(itemQuantity), tax, pSalesAssociate, pRegistry, pReturnItem);
	} // end SaleReturnLineItem()

	// ---------------------------------------------------------------------
	/**
	 * Constructs SaleReturnLineItem object, setting item, tax rate, sales
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
	 * @deprecated As of release 4.0.0, replaced by
	 *             {@link #SaleReturnLineItem(PLUItemIfc, BigDecimal, ItemTaxIfc, EmployeeIfc, RegistryIDIfc, ReturnItemIfc)}
	 *             .
	 */
	// ---------------------------------------------------------------------
	public MAXSaleReturnLineItem(PLUItem item, BigDecimal quantity, ItemTax tax, Employee pSalesAssociate,
			GiftRegistry pRegistry, ReturnItem pReturnItem) { // begin
																// SaleReturnLineItem()
		this((PLUItemIfc) item, quantity, (ItemTaxIfc) tax, (EmployeeIfc) pSalesAssociate, (RegistryIDIfc) pRegistry,
				(ReturnItemIfc) pReturnItem);
	} // end SaleReturnLineItem()
		// ---------------------------------------------------------------------

	/**
	 * Modifies item quantity, reset item total.
	 * <P>
	 * 
	 * @param newQty
	 *            new quantity
	 * @deprecated As of release 4.0.0, replaced by
	 *             {@link #modifyItemQuantity(BigDecimal)}
	 */
	// ---------------------------------------------------------------------
	public void modifyItemQuantity(long newQty) {
		itemQuantity = BigDecimal.valueOf(newQty);
		itemPrice.setItemQuantity(itemQuantity);
		itemPrice.calculateItemTotal();
	}

	// ---------------------------------------------------------------------
	/**
	 * Modifies item registry and sets modified flag.
	 * <P>
	 * 
	 * @param newGift
	 *            new registry
	 * @deprecated As of release 4.0.0, replaced by
	 *             {@link #modifyItemRegistry(RegistryIDIfc)}
	 */
	// ---------------------------------------------------------------------
	public void modifyItemGiftRegistry(GiftRegistry newGift) { // begin
																// modifyItemGiftRegistry()
																// set new
																// registry,
																// modified flag
																// to true
		modifyItemRegistry(newGift, true);
	} // end modifyItemGiftRegistry()
		// ---------------------------------------------------------------------

	/**
	 * Modifies item registry and sets modified flag to requested value.
	 * <P>
	 * 
	 * @param newGift
	 *            new registry
	 * @param modified
	 *            modified flag
	 * @deprecated As or release 4.0.0, replaced by
	 *             {@link #modifyItemRegistry(RegistryIDIfc, boolean)}
	 */
	// ---------------------------------------------------------------------
	public void modifyItemGiftRegistry(GiftRegistry newGift, boolean modified) { // begin
																					// modifyItemGiftRegistry()
																					// set
																					// new
																					// registry,
																					// modified
																					// flag
		setRegistry(newGift);
		setRegistryModifiedFlag(modified);
	} // end modifyItemGiftRegistry()
		// ---------------------------------------------------------------------

	/**
	 * Returns registry.
	 * <P>
	 * 
	 * @return registry
	 * @deprecated As of release 4.0.0, replaced by {@link #getRegistry()}.
	 */
	// ---------------------------------------------------------------------
	public GiftRegistry getGiftRegistry() {
		GiftRegistry reg = null;
		if (getRegistry() != null) {
			reg = new GiftRegistry();
			reg.setRegistryID(getRegistry().getID());
		}
		return (reg);
	}

	// ---------------------------------------------------------------------
	/**
	 * Sets registry.
	 * <P>
	 * 
	 * @param reg
	 *            registry object
	 * @deprecated As of release 4.0.0, replaced by
	 *             {@link #setRegistry(RegistryIDIfc)}.
	 */
	// ---------------------------------------------------------------------
	protected void setGiftRegistry(GiftRegistry reg) {
		registry = (RegistryIDIfc) reg;
	}

	// ---------------------------------------------------------------------
	/**
	 * Retrieves item quantity.
	 * <P>
	 * 
	 * @return item quantity
	 * @deprecated As of release 4.0.0, replaced by
	 *             {@link #getItemQuantityDecimal()}
	 */
	// ---------------------------------------------------------------------
	public Number getItemQuantity() {
		BigDecimal roundQty = itemQuantity.add(Util.I_ROUND_HALF);
		return (roundQty.longValue());
	}

	// ---------------------------------------------------------------------
	/**
	 * Sets item quantity, but does not re-calculate totals.
	 * <P>
	 * 
	 * @param qty
	 *            new quantity
	 * @deprecated As of release 4.0.0, replaced by
	 *             {@link #setItemQuantity(BigDecimal)}
	 */
	// ---------------------------------------------------------------------
	protected void setItemQuantity(long qty) {
		itemQuantity = BigDecimal.valueOf(qty);
	}

	// ---------------------------------------------------------------------
	/**
	 * Returns registry-modified flag.
	 * <P>
	 * 
	 * @return registry-modified flag
	 * @deprecated As of release 4.0.0, replaced by
	 *             {@link #getRegistryModifiedFlag()}.
	 */
	// ---------------------------------------------------------------------
	public boolean getGiftRegistryModifiedFlag() {
		return (registryModifiedFlag);
	}

	// ---------------------------------------------------------------------
	/**
	 * Sets registry modified flag.
	 * <P>
	 * 
	 * @param value
	 *            modified flag
	 * @deprecated As of release 4.0.0, replaced by
	 *             {@link #setRegistryModifiedFlag(boolean)}
	 */
	// ---------------------------------------------------------------------
	public void setGiftRegistryModifiedFlag(boolean value) {
		registryModifiedFlag = value;
	}

	// ---------------------------------------------------------------------
	/**
	 * Returns registry-modified flag.
	 * <P>
	 * 
	 * @return registry-modified flag
	 */
	// ---------------------------------------------------------------------
	public boolean getRegistryModifiedFlag() {
		return (registryModifiedFlag);
	}

	// ---------------------------------------------------------------------
	/**
	 * Sets registry modified flag.
	 * <P>
	 * 
	 * @param value
	 *            modified flag
	 */
	// ---------------------------------------------------------------------
	public void setRegistryModifiedFlag(boolean value) { // begin
															// setRegistryModifiedFlag()
		registryModifiedFlag = value;
	} // end setRegistryModifiedFlag()

	// ---------------------------------------------------------------------
	/**
	 * Returns default journal string.
	 * <P>
	 * 
	 * @param discountType
	 *            the discount type
	 * @return default journal string
	 * @deprecated in 6.0: Due a the addition of Markdowns to discount
	 *             processing, this appoach to journaling items has been
	 *             invalidated. Initial journaling of items should alwasy call
	 *             toJournalString.
	 */
	// ---------------------------------------------------------------------
	public String toJournalString(int discountType) { // begin toJournalString()
		StringBuffer strResult = new StringBuffer();
		ItemPriceIfc ip = getItemPrice();

		ItemDiscountStrategyIfc[] discounts = ip.getItemDiscounts();
		int taxMode = ip.getItemTax().getTaxMode();
		int taxScope = ip.getItemTax().getTaxScope();

		// journal discounts
		if (discounts != null && discounts.length > 0 && discountType != 0) {
			if (isDiscountEligible()) {
				journalDiscounts(ip, discounts, strResult, discountType);
			} else {
				// This combination indicates that multiple items where select
				// for
				// manual discount, and this particular item is not eligible.
				strResult.append(Util.EOL).append("ITEM: ").append(pluItem.getItemID())
						.append(Util.SPACES.substring(pluItem.getItemID().length(), ITEM_NUMBER_LENGTH));
				strResult.append(Util.EOL).append("  Not discount/markdown eligible.");
			}
		} else {
			// Item number
			CurrencyIfc itemPrice = ip.getExtendedSellingPrice();
			int signum = itemPrice.getDecimalValue().signum();
			// itemPrice.setDefaultFormat(CurrencyIfc.DEFAULT_FORMAT);
			Locale locale = LocaleMap.getLocale(LocaleConstantsIfc.JOURNAL);
			String priceString = itemPrice.toGroupFormattedString(locale);
			String priceStringNegated = itemPrice.negate().toGroupFormattedString(locale);

			strResult.append(Util.EOL).append("ITEM: ").append(pluItem.getItemID())
					.append(Util.SPACES.substring(pluItem.getItemID().length(), ITEM_NUMBER_LENGTH));

			BigDecimal quantity = getItemQuantityDecimal();
			quantity = quantity.setScale(2);
			String quantityString = quantity.toString();
			int whiteSpace = ITEM_PRICE_LENGTH;
			if (quantityString.startsWith("-")) {
				quantityString = quantityString.replace('-', '(');
				quantityString = quantityString + ")";
				if (signum >= 0)// if (!priceString.startsWith("("))
				{
					priceString = priceStringNegated;// "(" + priceString + ")";
				}
				whiteSpace++;
			}

			// price
			if (priceString.length() < whiteSpace) {
				strResult.append(Util.SPACES.substring(priceString.length(), whiteSpace));
			}
			strResult.append(priceString);

			// Tax Mode
			String taxFlag = new String("T");
			if (taxMode == TaxIfc.TAX_MODE_STANDARD && pluItem.getTaxable() == false) {
				taxFlag = TaxIfc.TAX_MODE_CHAR[TaxIfc.TAX_MODE_NON_TAXABLE];
			} else {
				taxFlag = TaxIfc.TAX_MODE_CHAR[taxMode];
			}
			strResult.append(" ").append(taxFlag);

			// Item description
			strResult.append(Util.EOL).append("  ").append(pluItem.getDescription(locale));

			// Item Quantity and Unit Price
			strResult.append(Util.EOL).append("  Qty: ").append(quantityString).append(" @ ");
			String sellingPriceString = ip.getSellingPrice().toGroupFormattedString(locale);
			strResult.append(sellingPriceString);

			// Item serial number
			if (itemSerial != null) {
				strResult.append(Util.EOL).append("  Serial Number: ").append(Util.EOL).append("  ").append(itemSerial);
			}

		}

		// if the PLUItem is a GiftCardPLUItem journal gift card information
		if (pluItem instanceof GiftCardPLUItemIfc) {
			strResult.append(((GiftCardPLUItemIfc) pluItem).getGiftCard().toJournalString());
		}

		// journal non-standard tax
		if (taxMode != TaxIfc.TAX_MODE_STANDARD) {
			if (taxScope == TaxIfc.TAX_SCOPE_ITEM) // tax overirde is at the
													// item level
			{
				strResult.append(ip.getItemTax().toJournalString());
			}
		}

		if (getRegistry() != null) {
			strResult.append(Util.EOL).append("  Gift Reg.: ").append(getRegistry().getID());
		}

		// if sales associate modified, write it
		if (getSalesAssociateModifiedFlag() && getSalesAssociate() != null) {
			strResult.append(Util.EOL).append("  Sales Assoc. ").append(getSalesAssociate().getEmployeeID());
		} else {
			ReturnItemIfc ri = getReturnItem();
			// if return, get sales associate
			if (getItemQuantityDecimal().signum() < 0 && ri != null && ri.getSalesAssociate() != null) {
				strResult.append(Util.EOL).append("  Sales Assoc. ").append(ri.getSalesAssociate().getEmployeeID());
			}
		}
		// pass back result

		return (strResult.toString());
	} // end toJournalString()

	// ---------------------------------------------------------------------
	/**
	 * Returns journal string when removing an item.
	 * <P>
	 * 
	 * @param discountType
	 *            the discount type
	 * @return journal string when removing an item
	 * @deprecated in 6.0: Due a the addition of Markdowns to discount
	 *             processing, this appoach to journaling items has been
	 *             invalidated. Initial journaling of items should alwasy call
	 *             toJournalString.
	 */
	// ---------------------------------------------------------------------
	public String toJournalDeleteString(int discountType) {

		ItemPriceIfc ip = getItemPrice();
		CurrencyIfc itemPrice = ip.getExtendedSellingPrice();

		Locale locale = LocaleMap.getLocale(LocaleConstantsIfc.JOURNAL);
		int signum = itemPrice.getDecimalValue().signum();
		String priceString = itemPrice.toGroupFormattedString(locale);
		ItemDiscountStrategyIfc[] discounts = ip.getItemDiscounts();
		StringBuffer strResult = new StringBuffer();

		// Item number
		strResult.append(Util.EOL).append("ITEM: ").append(pluItem.getItemID())
				.append(Util.SPACES.substring(pluItem.getItemID().length(), ITEM_NUMBER_LENGTH));

		// Discounts
		if (discounts != null && discounts.length > 0 && discountType != 0) {
			journalRemoveDiscounts(ip, discounts, strResult, discountType);
		} else {
			BigDecimal quantity = getItemQuantityDecimal();
			quantity = quantity.setScale(2);
			String quantityString = quantity.toString();
			int whiteSpace = ITEM_PRICE_LENGTH;
			if (quantityString.startsWith("-")) {
				quantityString = quantityString.substring(1);
				if (signum == CurrencyIfc.NEGATIVE) // (priceString.startsWith("("))
				{
					priceString = itemPrice.negate().toGroupFormattedString(locale); // priceString.substring(1,
																						// priceString.length()
																						// -
																						// 1);
				}
			} else {
				quantityString = "(" + quantityString + ")";
				if (signum > CurrencyIfc.NEGATIVE) // !priceString.startsWith("("))
				{
					priceString = itemPrice.negate().toGroupFormattedString(locale); // "("
																						// +
																						// priceString
																						// +
																						// ")";
				}
				whiteSpace++;
			}

			// price
			if (priceString.length() < whiteSpace) {
				strResult.append(Util.SPACES.substring(priceString.length(), whiteSpace));
			}
			strResult.append(priceString);

			// Tax flag
			if (this.isReturnLineItem() && this.getTaxable()) {
				strResult.append(" T");
			}

			// Item description
			strResult.append(Util.EOL).append("  ").append(pluItem.getDescription(locale))

					// Item Quantity and Unit Price
					.append(Util.EOL).append("  Qty: ").append(quantityString).append(" @ ");

			strResult.append(ip.getSellingPrice().toGroupFormattedString(locale));

			// Item tax
			if ((getItemTax() != null) && this.isReturnLineItem() && this.getTaxable()) {
				strResult.append(Util.EOL).append("  Tax: ").append(this.getItemTaxAmount().negate());
			}

			// Item serial number
			if (itemSerial != null) {
				strResult.append(Util.EOL).append("  Serial Number: ").append(Util.EOL).append("  ").append(itemSerial);
			}
		}
		// pass back result

		return (strResult.toString());
	}

	// ---------------------------------------------------------------------
	/**
	 * Returns journal string when returning an item.
	 * <P>
	 * 
	 * @param discountType
	 *            the discount type
	 * @return journal string when returning an item
	 * @deprecated in 6.0: Due a the addition of Markdowns to discount
	 *             processing, this appoach to journaling items has been
	 *             invalidated.
	 */
	// ---------------------------------------------------------------------
	public String toJournalRemoveString(int discountType) {
		StringBuffer sb = new StringBuffer();
		sb.append(toJournalDeleteString(discountType))

				// Item Removed
				.append(Util.EOL).append("  Item Deleted");
		return sb.toString();
	}

	// ---------------------------------------------------------------------
	/**
	 * Journals discounts, if they exist.
	 * <P>
	 * 
	 * @param ip
	 *            ItemPriceIfc object
	 * @param discounts
	 *            array of discounts
	 * @param strJournal
	 *            journal string
	 * @param discountType
	 *            the discount type
	 * @deprecated in 6.0: Due a the addition of Markdowns to discount
	 *             processing, this appoach to journaling items has been
	 *             invalidated. Initial journaling of items should alwasy call
	 *             toJournalString.
	 */
	// ---------------------------------------------------------------------
	public void journalDiscounts(ItemPriceIfc ip, ItemDiscountStrategyIfc[] discounts, StringBuffer strJournal,
			int discountType) { // begin
								// journalDiscounts()
		int discountCount = 0;
		ArrayList orderedDiscounts = new ArrayList();

		// first get the best deal discount if any
		// Calculate the discount that coresponds to the best deal
		ItemDiscountStrategyIfc bd = ip.getBestDealDiscount();
		if (bd != null) {
			orderedDiscounts.add(bd);
		}
		ItemDiscountStrategyIfc[] pcdDiscounts = ip.getItemDiscountsByPercentage();
		if (pcdDiscounts != null) {
			for (int i = 0; i < pcdDiscounts.length; i++) {
				orderedDiscounts.add(pcdDiscounts[i]);
			}
		}
		ItemDiscountStrategyIfc[] amtDiscounts = ip.getItemDiscountsByAmount();
		if (amtDiscounts != null) {
			for (int i = 0; i < amtDiscounts.length; i++) {
				orderedDiscounts.add(amtDiscounts[i]);
			}
		}
		CurrencyIfc totalItemDiscount = DomainGateway.getBaseCurrencyInstance();
		for (int i = 0; i < orderedDiscounts.size(); i++) { // begin handle
															// discounts
															// build discount
															// line(s)
			ItemDiscountStrategyIfc d = (ItemDiscountStrategyIfc) orderedDiscounts.get(i);
			int method = d.getDiscountMethod();

			int reason = d.getReasonCode();

			CurrencyIfc c = null;
			// skip transaction discount audit records
			if (!(d instanceof ItemTransactionDiscountAudit)) {
				CurrencyIfc tempPrice = ip.getExtendedSellingPrice().subtract(totalItemDiscount);
				c = d.calculateItemDiscount(tempPrice);
				totalItemDiscount = totalItemDiscount.add(c);

				String discountDescription;
				if (method == DiscountRuleConstantsIfc.DISCOUNT_METHOD_AMOUNT
						|| method == DiscountRuleConstantsIfc.DISCOUNT_METHOD_FIXED_PRICE) {
					c = c.multiply(ip.getItemQuantityDecimal().abs());
					discountDescription = "Amt.";
				} else if (method == DiscountRuleConstantsIfc.DISCOUNT_METHOD_PERCENTAGE) {
					BigDecimal discountRate = d.getDiscountRate();
					discountRate = discountRate.movePointRight(2);
					discountRate = discountRate.setScale(0, BigDecimal.ROUND_HALF_UP);
					discountDescription = discountRate.toString() + "%";
				} else {
					discountDescription = "Unknown";
				}

				if (discountType == DISCOUNT_BOTH
						|| (discountType == DiscountRuleConstantsIfc.DISCOUNT_METHOD_AMOUNT
								&& method == DiscountRuleConstantsIfc.DISCOUNT_METHOD_AMOUNT)
						|| (discountType == DiscountRuleConstantsIfc.DISCOUNT_METHOD_FIXED_PRICE
								&& method == DiscountRuleConstantsIfc.DISCOUNT_METHOD_FIXED_PRICE)
						|| (discountType == DiscountRuleConstantsIfc.DISCOUNT_METHOD_PERCENTAGE
								&& method == DiscountRuleConstantsIfc.DISCOUNT_METHOD_PERCENTAGE)) {
					if (discountCount > 0) {
						strJournal.append(Util.EOL).append("ITEM: ").append(pluItem.getItemID())
								.append(Util.SPACES.substring(pluItem.getItemID().length(), ITEM_NUMBER_LENGTH));
					}
					if (!d.isAdvancedPricingRule()) {
						discountCount++;
					}

					// c.setDefaultFormat("(#0.00);#0.00");
					Locale locale = LocaleMap.getLocale(LocaleConstantsIfc.JOURNAL);
					String price = c.toGroupFormattedString(locale);
					// c.setDefaultFormat(CurrencyIfc.DEFAULT_FORMAT);
					if (c.signum() > CurrencyIfc.NEGATIVE) {
						price = c.negate().toGroupFormattedString(locale);
						strJournal.append(Util.SPACES.substring(price.length(), ITEM_PRICE_LENGTH + 1));
					} else {
						strJournal.append(Util.SPACES.substring(price.length(), ITEM_PRICE_LENGTH));
						discountDescription = discountDescription + " Deleted";
					}
					if (!d.isAdvancedPricingRule()) {
						strJournal.append(Util.EOL).append("ITEM: ").append(pluItem.getItemID())
								.append(Util.SPACES.substring(pluItem.getItemID().length(), ITEM_NUMBER_LENGTH));
						String accntDescription = "  Discount: ";
						String rsnDescription = "  Disc. Rsn.: ";
						if (d.getAccountingMethod() == DiscountRuleConstantsIfc.ACCOUNTING_METHOD_MARKDOWN) {
							accntDescription = "  Markdown: ";
							rsnDescription = "  Mrkd. Rsn.: ";
						}
						strJournal.append(price).append(Util.EOL);
						strJournal.append(accntDescription);
						strJournal.append(discountDescription).append(Util.EOL)
								// need to expand reason
								.append(rsnDescription).append((d.getReasonCodeText() == null)
										? new Integer(reason).toString() : d.getReasonCodeText());
					}
				}
			}
		} // end handle discounts
	}

	// ---------------------------------------------------------------------
	/**
	 * Journals removal of discounts, if they exist.
	 * <P>
	 * 
	 * @param ip
	 *            ItemPriceIfc object
	 * @param discounts
	 *            array of discounts
	 * @param strJournal
	 *            journal string
	 * @param discountType
	 *            the discount type
	 * @deprecated in 6.0: Due a the addition of Markdowns to discount
	 *             processing, this appoach to journaling items has been
	 *             invalidated. Initial journaling of items should alwasy call
	 *             toJournalString. Manual Item Discounts/Markdowns should call
	 *             journalDiscount() and journalRemoveDiscount.
	 */
	// ---------------------------------------------------------------------
	public void journalRemoveDiscounts(ItemPriceIfc ip, ItemDiscountStrategyIfc[] discounts, StringBuffer strJournal,
			int discountType) { // begin
								// journalDiscounts()
		String price;
		int numDiscounts = discounts.length;
		int itemDiscountsFound = 0;
		int discountCount = 0;
		for (int i = 0; i < numDiscounts; i++) { // begin handle discounts
													// build discount line(s)
			DiscountRuleIfc d = (DiscountRuleIfc) discounts[i];
			int method = d.getDiscountMethod();
			int reason = d.getReasonCode();
			String reasonText = d.getReasonCodeText();

			CurrencyIfc c = null;
			// skip transaction discount audit records
			if (!(d instanceof ItemTransactionDiscountAudit)) {
				itemDiscountsFound++;

				// get data for journaling discount
				ItemDiscountStrategyIfc id = (ItemDiscountStrategyIfc) d;
				c = id.calculateItemDiscount(ip.getExtendedSellingPrice());

				String discountDescription;
				if (method == DiscountRuleConstantsIfc.DISCOUNT_METHOD_AMOUNT) {
					c = c.multiply(ip.getItemQuantityDecimal().abs());
					discountDescription = "Amt.";
				} else if (method == DiscountRuleConstantsIfc.DISCOUNT_METHOD_PERCENTAGE) {
					BigDecimal discountRate = d.getDiscountRate();
					discountRate = discountRate.movePointRight(2);
					discountRate = discountRate.setScale(0, BigDecimal.ROUND_HALF_UP);
					discountDescription = discountRate.toString() + "%";
				} else {
					discountDescription = "Unknown";
				}

				if (discountType == DISCOUNT_BOTH
						|| (discountType == DiscountRuleConstantsIfc.DISCOUNT_METHOD_AMOUNT
								&& method == DiscountRuleConstantsIfc.DISCOUNT_METHOD_AMOUNT)
						|| (discountType == DiscountRuleConstantsIfc.DISCOUNT_METHOD_PERCENTAGE
								&& method == DiscountRuleConstantsIfc.DISCOUNT_METHOD_PERCENTAGE)) {
					if (discountCount > 0) {
						// Item number
						strJournal.append(Util.EOL).append("ITEM: ").append(pluItem.getItemID())
								.append(Util.SPACES.substring(pluItem.getItemID().length(), ITEM_NUMBER_LENGTH));
					}
					discountCount++;

					price = c.toGroupFormattedString(LocaleMap.getLocale(LocaleConstantsIfc.JOURNAL));
					if (c.signum() > CurrencyIfc.NEGATIVE) {
						strJournal.append(Util.SPACES.substring(price.length(), ITEM_PRICE_LENGTH));
						discountDescription = discountDescription + " Deleted";
					} else {
						strJournal.append(Util.SPACES.substring(price.length(), ITEM_PRICE_LENGTH + 1));
						discountDescription = discountDescription + " Deleted";
					}

					strJournal.append(price).append(Util.EOL).append("  Discount: ").append(discountDescription)
							.append(Util.EOL)
							// need to expand reason
							.append("  Disc. Rsn.: ")
							.append((reasonText == null) ? Integer.toString(reason) : reasonText);
				}
			}
		} // end handle discounts
	} // end journalRemoveDiscounts()

	// ---------------------------------------------------------------------
	/*
	 * END DEPRECATED METHODS
	 */
	// ---------------------------------------------------------------------

	/**
	 * @return Returns the taxChanged.
	 */
	public boolean isTaxChanged() {
		return taxChanged;
	}

	/**
	 * @param taxChanged
	 *            The taxChanged to set.
	 */
	public void setTaxChanged(boolean taxChanged) {
		this.taxChanged = taxChanged;
	}

	// --------------------------------------------------------------------------
	/**
	 * Returns the price adjutment line item reference
	 * 
	 * @return Returns the priceAdjustmentReference.
	 **/
	// --------------------------------------------------------------------------
	public int getPriceAdjustmentReference() {
		return priceAdjustmentReference;
	}

	// --------------------------------------------------------------------------
	/**
	 * Sets the price adjustment line item reference
	 * 
	 * @param priceAdjustmentReference
	 *            The priceAdjustmentReference to set.
	 **/
	// --------------------------------------------------------------------------
	public void setPriceAdjustmentReference(int priceAdjustmentReference) {
		this.priceAdjustmentReference = priceAdjustmentReference;
	}

	// --------------------------------------------------------------------------
	/**
	 * Returns original line number. Not affiliated with the original line
	 * number in return item.
	 * 
	 * @return Returns the originalLineNumber or -1 if does not apply
	 **/
	// --------------------------------------------------------------------------
	public int getOriginalLineNumber() {
		return originalLineNumber;
	}

	// --------------------------------------------------------------------------
	/**
	 * Sets original line number. Not affiliated with the original line number
	 * in return item.
	 * 
	 * @param originalLineNumber
	 *            The originalLineNumber to set.
	 **/
	// --------------------------------------------------------------------------
	public void setOriginalLineNumber(int originalLineNumber) {
		this.originalLineNumber = originalLineNumber;
	}

	// --------------------------------------------------------------------------
	/**
	 * Returns original transaction number. Not affiliated with the original
	 * transaction number in return item.
	 * 
	 * @return Returns the transactionSequenceNumber or -1 if does not apply
	 **/
	// --------------------------------------------------------------------------
	public long getOriginalTransactionSequenceNumber() {
		return transactionSequenceNumber;
	}

	// --------------------------------------------------------------------------
	/**
	 * Sets original transaction number. Not affiliated with the original
	 * transaction number in return item.
	 * 
	 * @param transactionSequenceNumber
	 *            The transactionSequenceNumber to set.
	 **/
	// --------------------------------------------------------------------------
	public void setOriginalTransactionSequenceNumber(long transactionSequenceNumber) {
		this.transactionSequenceNumber = transactionSequenceNumber;
	}

	// ---------------------------------------------------------------------
	/**
	 * Sets send label count associated with this line item
	 * <P>
	 * 
	 * @param sendLabelCount
	 *            send label count
	 **/
	// ---------------------------------------------------------------------
	public void setSendLabelCount(int sendLabelCount) {
		this.sendLabelCount = sendLabelCount;
	}

	// ---------------------------------------------------------------------
	/**
	 * Gets send label count associated with this line item
	 * <P>
	 * 
	 * @return int send label count
	 **/
	// ---------------------------------------------------------------------
	public int getSendLabelCount() {
		return this.sendLabelCount;
	}

	/**
	 * Get the identifier the uniquely identifies this item
	 * 
	 * @return unique identifier for this tax line item
	 */
	public int getLineItemTaxIdentifier() {
		return itemPrice.getLineItemTaxIdentifier();
	}

	/**
	 * Retrieve the current active tax rules.
	 * 
	 * @return The active tax rules
	 */
	public RunTimeTaxRuleIfc[] getActiveTaxRules() {
		RunTimeTaxRuleIfc[] taxRules = null;
		// if it is a retrieved return item never been suspended
		if (isReturnLineItem() && getReturnItem() != null && getReturnItem().isItemTaxRetrieved()) {
			taxRules = getRetrievedReturnTaxRules();
		}
		// if it is a line item retrieved from db, such as a voided item,
		// a retrieved return item from suspension, layway retrival, or order
		// retrival.
		else if (isFromTransaction()) {
			taxRules = getReverseTaxRules();
		}
		// Kit headers have no rules
		else if (isKitHeader()) {
			taxRules = new RunTimeTaxRuleIfc[0];
		} else {
			taxRules = itemPrice.getActiveTaxRules();

			if (taxRules == null && pluItem != null) {
				taxRules = pluItem.getTaxRules();
			}
			// Use default tax rules if we don't find one, unless its a kit
			// header which is expected
			// to have no rules (the individual kit items have the rules)
			if (taxRules == null) {
				logger.info("Using Default tax rules, plu did not have any tax rules");
				taxRules = getDefaultTaxRules();
			}
		}

		return taxRules;
	}

	/**
	 * Get the default tax rules, when none can be found in the DB
	 * 
	 * @return list of tax rules
	 * @see com.extendyourstore.domain.lineitem.TaxLineItemInformationIfc#getDefaultTaxRules()
	 */
	public TaxRuleIfc[] getDefaultTaxRules() {
		return (TaxRuleIfc[]) itemPrice.getDefaultTaxRules();
	}

	/**
	 * Tax rules applied on reverse transactions other than returns
	 * 
	 * @return
	 */
	protected ReverseItemTaxRuleIfc[] getReverseTaxRules() {
		if (reverseTaxRules == null) {
			ArrayList rules = new ArrayList();
			if (itemPrice != null && itemPrice.getItemTax() != null
					&& itemPrice.getItemTax().getTaxInformationContainer() != null) {
				TaxInformationIfc[] originalTaxes = itemPrice.getItemTax().getTaxInformationContainer()
						.getTaxInformation();
				if (originalTaxes != null) {
					for (int i = 0; i < originalTaxes.length; i++) {
						ReverseItemTaxRuleIfc taxRule = DomainGateway.getFactory().getReturnItemTaxRuleInstance();
						taxRule.setOrder(i);
						taxRule.setTaxRuleName(originalTaxes[i].getTaxRuleName());
						taxRule.setInclusiveTaxFlag(originalTaxes[i].getInclusiveTaxFlag());
						taxRule.setUniqueID(String.valueOf(taxRule.hashCode()));
						ReverseTaxCalculatorIfc calculator = DomainGateway.getFactory()
								.getReverseTaxCalculatorInstance();
						taxRule.setTaxCalculator(calculator);
						calculator.setCalculationParameters(new TaxInformationIfc[] { originalTaxes[i] });
						rules.add(taxRule);
					}
				}
			} else {
				logger.error(
						"Could not set up return item tax rule.  Either itemPrice, itemTax, or the tax information container is null.");
			}

			this.reverseTaxRules = (ReverseItemTaxRuleIfc[]) rules.toArray(new ReverseItemTaxRuleIfc[0]);
		}
		return this.reverseTaxRules;

	}

	/**
	 * Tax rules applied on returns
	 * 
	 * @return
	 */
	protected ReverseItemTaxRuleIfc[] getRetrievedReturnTaxRules() {
		if (reverseTaxRules == null || (reverseTaxRules.length > 0 && !reverseTaxRules[0].isReturn())) {
			ArrayList rules = new ArrayList();
			if (returnItem != null && returnItem.getItemTax() != null
					&& returnItem.getItemTax().getTaxInformationContainer() != null) {
				TaxInformationIfc[] originalTaxes = returnItem.getItemTax().getTaxInformationContainer()
						.getTaxInformation();
				if (originalTaxes != null) {
					for (int i = 0; i < originalTaxes.length; i++) {
						ReverseItemTaxRuleIfc taxRule = DomainGateway.getFactory().getReturnItemTaxRuleInstance();
						taxRule.setOrder(i);
						taxRule.setTaxRuleName(originalTaxes[i].getTaxRuleName());
						taxRule.setInclusiveTaxFlag(originalTaxes[i].getInclusiveTaxFlag());
						// Every return item must be unique, otherwise they get
						// merged into one taxRuleContainer and the wrong
						// calculator
						// is used for some items
						taxRule.setUniqueID(String.valueOf(taxRule.hashCode()));
						ReturnTaxCalculatorIfc calculator = DomainGateway.getFactory().getReturnTaxCalculatorInstance();
						originalTaxes[i].negate();
						calculator.setCalculationParameters(new TaxInformationIfc[] { originalTaxes[i] });
						if (returnItem != null) {
							calculator.setQuantityReturnable(returnItem.getQuantityReturnable());
							calculator.setQuantityPurchased(returnItem.getQuantityPurchased());
							calculator.setQuantityBeingReturned(returnItem.getItemQuantity());
						}
						taxRule.setTaxCalculator(calculator);
						taxRule.setReturn(true);
						rules.add(taxRule);
					}
				}
			} else {
				logger.error(
						"Could not set up return item tax rule.  Either itemPrice, itemTax, or the tax information container is null.");
			}

			this.reverseTaxRules = (ReverseItemTaxRuleIfc[]) rules.toArray(new ReverseItemTaxRuleIfc[0]);
		}
		return reverseTaxRules;
	}

	/**
	 * Can the transaction override the tax rules on this item. A transaction
	 * override should not affect some items. For example if the line item is a
	 * return that was retrieved then the tax should stay the same for that line
	 * item.
	 * 
	 * @return True if can override the items tax rules. False, otherwise.
	 */
	public boolean canTransactionOverrideTaxRules() {
		boolean transactionCanOverride = false;

		// Tax exempt is on transaction level only, so this must return true or
		// the tax exempt rules wont take, they are never associated with an
		// item level
		if (getTaxMode() == TaxConstantsIfc.TAX_MODE_EXEMPT) {
			transactionCanOverride = true;
		}
		// If this is true, transaction level tax override was specifically set.
		// We must do this
		// Or else getActiveTaxRules on the item level will get the overridden
		// transaction tax and
		// treat it as a line item override.
		if (getTaxScope() == TaxConstantsIfc.TAX_SCOPE_TRANSACTION) {
			transactionCanOverride = true;
		}
		if (getTaxMode() == TaxConstantsIfc.TAX_MODE_NON_TAXABLE) {
			transactionCanOverride = false;
		}
		if (isReturnLineItem()) {
			transactionCanOverride = false;
		}
		if (getItemSendFlag()) {
			transactionCanOverride = false;
		}

		return transactionCanOverride;
	}

	/**
	 * Retrieve the tax information container that the tax calculation results
	 * should be placed.
	 * 
	 * @return
	 */
	public TaxInformationContainerIfc getTaxInformationContainer() {
		TaxInformationContainerIfc taxInformationContainer = null;
		if (itemPrice != null && itemPrice.getItemTax() != null) {
			taxInformationContainer = itemPrice.getItemTax().getTaxInformationContainer();
		}

		return taxInformationContainer;
	}

	/**
	 * Returns if item is a giftcard merch type
	 * 
	 * @return
	 */
	public boolean isGiftItem() {
		if (getPLUItem().getProductGroupID().equals(ProductGroupConstantsIfc.PRODUCT_GROUP_GIFT_CARD))
			return true;
		else
			return false;

	}

	// ----------------------------------------------------------------------
	/**
	 * Checks the pluItem is gift certificate or gift card plu item. These items
	 * will not treat as unit of sale.
	 * 
	 * @param pluItem
	 *            PLUItemIfc
	 * @return isGiftPLUItem
	 **/
	// ----------------------------------------------------------------------
	protected boolean isGiftItem(PLUItemIfc pluItem) {
		boolean isGiftPLUItem = false;

		if (pluItem instanceof GiftCardPLUItemIfc || pluItem instanceof GiftCertificateItemIfc) {
			isGiftPLUItem = true;
		}
		return isGiftPLUItem;
	}

	// ----------------------------------------------------------------------------
	/**
	 * Retrieves indicator item is eligible for employee discounting.
	 * 
	 * @return indicator item is eligible for employee discounting
	 */
	// ----------------------------------------------------------------------------
	public boolean isEmployeeDiscountEligible() {
		// default employee discount eligible to true
		boolean employeeDiscountEligible = true;
		// get from PLU item if possible
		if (getPLUItem() != null) {
			employeeDiscountEligible = getPLUItem().getEmployeeDiscountEligible();
		}
		return (employeeDiscountEligible);
	}

	/**
	 * Tell whether or not this item is returnable, assuming it is a related
	 * item.
	 * 
	 * @return true or false
	 * @see com.extendyourstore.domain.lineitem.SaleReturnLineItemIfc#isRelatedItemReturnable()
	 * @since NEP67
	 */
	public boolean isRelatedItemReturnable() {
		return this.relatedItemReturnable;
	}

	/**
	 * Set this related item as being returnable or not
	 * 
	 * @param value
	 *            true or false
	 * @see com.extendyourstore.domain.lineitem.SaleReturnLineItemIfc#setRelatedItemReturnable(boolean)
	 * @since NEP67
	 */
	public void setRelatedItemReturnable(boolean value) {
		this.relatedItemReturnable = value;
	}

	/**
	 * Set the sequence number this line item is related to. This should be -1
	 * if this is not a related item.
	 * 
	 * @param seqNum
	 *            Sequence number.
	 * @see com.extendyourstore.domain.lineitem.SaleReturnLineItemIfc#setRelatedItemSequenceNumber(int)
	 * @since NEP67
	 */
	public void setRelatedItemSequenceNumber(int seqNum) {
		this.relatedItemSequenceNumber = seqNum;
	}

	/**
	 * Get the sequence number this line item is related to. This should be -1
	 * if this is not a related item.
	 * 
	 * @return sequence number
	 * @see com.extendyourstore.domain.lineitem.SaleReturnLineItemIfc#getRelatedItemSequenceNumber()
	 * @since NEP67
	 */
	public int getRelatedItemSequenceNumber() {
		return this.relatedItemSequenceNumber;
	}

	/**
	 * Flag that tracks whether or not this item is deleteable. This value is
	 * persisted to the database, for suspend/retrieve transactions, but is not
	 * used in the POSLog.
	 * 
	 * @return true or false.
	 * @see com.extendyourstore.domain.lineitem.SaleReturnLineItemIfc#isRelatedItemDeleteable()
	 * @since NEP67
	 */
	public boolean isRelatedItemDeleteable() {
		return this.relatedItemDeleteable;
	}

	/**
	 * Set whether or not this related item is deleteable. This value is
	 * persisted to the database, for suspend/retrieve transactions, but is not
	 * used in the POSLog.
	 * 
	 * @param relatedItemDeleteable
	 * @see com.extendyourstore.domain.lineitem.SaleReturnLineItemIfc#setRelatedItemDeleteable(boolean)
	 * @since NEP67
	 */
	public void setRelatedItemDeleteable(boolean relatedItemDeleteable) {
		this.relatedItemDeleteable = relatedItemDeleteable;
	}

	// ----------------------------------------------------------------------
	/**
	 * Adds a related item line item to the vector.
	 * 
	 * @param lineItem
	 **/
	// ----------------------------------------------------------------------
	public void addRelatedItemLineItem(SaleReturnLineItemIfc lineItem) {
		if (relatedItemLineItems == null) {
			relatedItemLineItems = new ArrayList();
		}
		relatedItemLineItems.add(lineItem);
	}

	// ----------------------------------------------------------------------
	/**
	 * Returns the vector of related item line items.
	 * 
	 * @return
	 **/
	// ----------------------------------------------------------------------
	public SaleReturnLineItemIfc[] getRelatedItemLineItems() {
		SaleReturnLineItemIfc[] relatedItems = null;
		if (relatedItemLineItems != null) {
			// create an array containing line items
			relatedItems = (SaleReturnLineItemIfc[]) relatedItemLineItems.toArray(new SaleReturnLineItem[0]);
		}

		return relatedItems;
	}

	// ----------------------------------------------------------------------
	/**
	 * Sets the related item line items.
	 * 
	 * @param relatedItems
	 **/
	// ----------------------------------------------------------------------
	public void setRelatedItemLineItems(SaleReturnLineItemIfc[] relatedItems) {
		if (relatedItemLineItems == null) {
			relatedItemLineItems = new ArrayList();
		} else {
			relatedItemLineItems.clear();
		}
		relatedItemLineItems.addAll(Arrays.asList(relatedItems));
	}

	// ----------------------------------------------------------------------
	/**
	 * Retuns all the promotionLineItems for the Sale Return Line Item
	 * 
	 * @return Returns the promotionLineItems.
	 */
	// ----------------------------------------------------------------------
	public PromotionLineItemIfc[] getPromotionLineItems() {
		return itemPrice.getPromotionLineItems();
	}

	// ----------------------------------------------------------------------
	/**
	 * Adds a Promotion Line Item to the Sale Return Line Item
	 * 
	 * @param promotionLineItem
	 */
	// ----------------------------------------------------------------------
	public void addPromotionLineItem(PromotionLineItemIfc promotionLineItem) {
		itemPrice.addPromotionLineItem(promotionLineItem);
	}

	public MAXLineItemTaxBreakUpDetailIfc[] getLineItemTaxBreakUpDetails() {
		
		
		if(! (itemPrice.getItemTax() instanceof MAXItemTaxIfc)){
			System.out.println(itemPrice.getItemTax());
		}
		return ((MAXItemTaxIfc) itemPrice.getItemTax()).getLineItemTaxBreakUpDetail();
	}

	public void setLineItemTaxBreakUpDetails(MAXLineItemTaxBreakUpDetailIfc[] taxBreakUpDetails) {
		((MAXItemTaxIfc) itemPrice.getItemTax()).setLineItemTaxBreakUpDetail(taxBreakUpDetails);

	}

	//Changes for Rev 1.1 : Starts
	public String getComparator(HashMap map) {
		int comparisonBasis = Integer.parseInt((String) map.get(COMPARISION_BASIS));
		String criterion = (String) map.get(CRITERION);
		String criterion1 = "'" + (String) map.get(CRITERION) + "'";
		String value = null;

		switch (comparisonBasis) {
		case COMPARISON_BASIS_ITEM_ID:
			value = getPLUItem().getItemID();
			break;
		case COMPARISON_BASIS_DEPARTMENT:
			value = MAXUtils.getDepartmentID(pluItem.getItem().getDepartment().getDepartmentID());
			break;
		// ADDED BY IZHAR
		case COMPARISON_BASIS_MERCHANDISE_CLASS:
			value = MAXUtils.getMerchandiseClass(pluItem.getItemClassification().getMerchandiseHierarchyGroup());
			break;
		case COMPARISON_BASIS_SUBCLASS:
			value = pluItem.getItemClassification().getMerchandiseHierarchyGroup();
			break;
		case COMPARISON_BASIS_ITEM_GROUP:
			String itemGroups = null;
				if(((MAXPLUItemIfc)pluItem).getItemGroups() != null){
					itemGroups = ((MAXPLUItemIfc)pluItem).getItemGroups();
				}
			if (itemGroups != null)
				value = (itemGroups.indexOf(criterion1) != -1) ? criterion : null;
			break;
		case COMPARISON_BASIS_BRAND:
			value = MAXUtils.getBrand(pluItem);
			break;

		default:
			break;
		}
		return value;
	}
	//Changes for Rev 1.1 : Ends

	public void calculateLineItemPrice(CurrencyIfc currencyIfc) {
		// TODO Auto-generated method stub

	}

	/** Changes for Rev 1.4 : Starts **/
	public String getEmployeeDiscountID() {
		return employeeDiscountID;
	}

	public void setEmployeeDiscountID(String employeeDiscountID) {
		this.employeeDiscountID = employeeDiscountID;
	}

	public String getDiscountPercentage() {
		return discountPercentage;
	}

	public void setDiscountPercentage(String discountPercentage) {
		this.discountPercentage = discountPercentage;
	}
	/** Changes for Rev 1.4 : Ends **/

	@Override
	public void modifyItemPrice(CurrencyIfc newPrice, int reasonCode) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void setEntryMethod(int value) {
		// TODO Auto-generated method stub
		
	}

	/**
	 * Returns the transaction discount amount
	 * <p>
	 * 
	 * @return CurrencyIfc
	 **/
	// ---------------------------------------------------------------------
	/*
	 * public CurrencyIfc getItemDiscountCardAmount() { return
	 * itemPrice.getItemDiscountCardAmount(); }
	 */

	// v12 compare changes start
		/** Change for Rev 1.5 : Start */
		public boolean isVatCollectionApplied()
		{

			return vatCollectionApplied;
		}

		public void setVatCollectionApplied(boolean vatCollectionApplied)
		{

			this.vatCollectionApplied = vatCollectionApplied;

		}


		public BigDecimal getVatCollectionAmount()
		{
			return vatCollectionAmount;
		}


		public void setVatCollectionAmount(BigDecimal vatCollectionAmount)
		{

			this.vatCollectionAmount = vatCollectionAmount;

		}
		

		public boolean isVatExtraApplied()
		{
			return isVatExtraApplied;
		}

		public BigDecimal vatExtraAmount = new BigDecimal(0.00);

		public BigDecimal getVatExtraAmount()
		{
			return vatExtraAmount;
		}

		public void setVatExtraAmount(BigDecimal vatExtraAmount)
		{
			this.vatExtraAmount = vatExtraAmount;
		}

		
		public void setVatExtraApplied(boolean isVatExtraApplied)
		{
			this.isVatExtraApplied = isVatExtraApplied;
		}
		
		public boolean isExtendedPriceModified()
		{
			return isExtendedPriceModified;
		}

		public void setExtendedPriceModified(boolean isExtendedPriceModified)
		{
			this.isExtendedPriceModified = isExtendedPriceModified;
		}
		//changes for rev 1.2		
		@Override
		public int getLineNumberonReceipt() {
			return getLineNumber()+1;
		}
		//changes for rev 1.3 && 1.4 && 1.6
		@Override
		public CurrencyIfc getAmountPrintedOnReceipt(){
			if(itemPrice instanceof MAXItemPriceIfc && getPLUItem() instanceof MAXPLUItemIfc)
			{
				if((((MAXPLUItemIfc)getPLUItem()).getMaximumRetailPrice().multiply(getItemQuantityDecimal())).compareTo(itemPrice.getExtendedDiscountedSellingPrice().abs())<0 && itemPrice.isPriceOverride())
					return (((MAXPLUItemIfc)getPLUItem()).getMaximumRetailPrice().multiply(getItemQuantityDecimal())).add(((MAXItemPriceIfc)itemPrice).getDiscountAmount().multiply(getItemQuantityDecimal()));
				else
				return (((MAXPLUItemIfc)getPLUItem()).getMaximumRetailPrice().multiply(getItemQuantityDecimal())).subtract(((MAXItemPriceIfc)itemPrice).getDiscountAmount().multiply(getItemQuantityDecimal()));
			}
			else
				return (getPrintedSellingPrice().multiply(getItemQuantityDecimal())).subtract(getItemDiscountAmount());
		}

		//changes for rev 1.5 start
		public boolean isGiftCert(){
			if(getPLUItem() instanceof GiftCertificateItem)
			return true;
			return false;
		}

		// ----------------------------------------------------------------------
		/**
		 * Checks the pluItem is gift certificate or gift card plu item.
		 * 
		 * @param pluItem
		 *            PLUItemIfc
		 * @return isGiftPLUItem
		 **/
		// ----------------------------------------------------------------------
		public boolean checkGiftItem() {
			boolean isGiftPLUItem = false;

			if (pluItem instanceof GiftCardPLUItemIfc || pluItem instanceof GiftCertificateItemIfc) {
				isGiftPLUItem = true;
			}
			return isGiftPLUItem;
		}
		//changes for rev 1.5 end
		
		//Change for ReV 1.7 : STARTS
		protected String taxType;
		/*protected Map<String, String> taxCode = new HashMap<String, String>();

		public Map<String, String> getTaxCode()
		{
			return taxCode;
		}

		public void setTaxCode(Map<String, String> taxCode)
		{
			this.taxCode = taxCode;
		}*/
		public String getTaxType()
		{
			return taxType;
		}

		public void setTaxType(String taxType)
		{
			this.taxType = taxType;
		}
		public String  hsnNumber;
		public String getHSNNumber(){
			return hsnNumber;
		}
		public void setHSNNumber(String hsnNumner){
			this.hsnNumber=hsnNumner;
		}
		public String proportionatePrice;

		/**
		 * @param proportionatePrice the proportionatePrice to set
		 */
		public void setProportionatePrice(String proportionatePrice) {
			this.proportionatePrice = proportionatePrice;
		}

		/**
		 * @return the proportionatePrice
		 */
		public String getProportionatePrice() {		
			return proportionatePrice;
			
		}
		//Change for ReV 1.7 : ENDS
		
		public String  categoryID;
		public String  categoryDesc;
		public String getScansheetCategoryID() {
			return categoryID;
		}
		@Override
		public String getScansheetCategoryDesc() {
			return categoryDesc;
		}
		@Override
		public void setScansheetCategoryID(String categoryID) {
			this.categoryID=categoryID;
		}
		@Override
		public void setScansheetCategoryDesc(String categoryDesc) {
			this.categoryDesc=categoryDesc;
		}
		
		private boolean isEdgeItem = false;
		public boolean isEdgeItem() {
			return isEdgeItem;
		}
		public void setEdgeItem(boolean isEdgeItem) {
			this.isEdgeItem = isEdgeItem;
		}

		float beertot;
		public float getBeertot() {
			return beertot;
		}
		public void setBeertot(float beertot) {
			this.beertot = beertot;
		}

		protected String liquom;
		protected String liqcat;

		public void setliquom(String liquom) {
			this.liquom = liquom;			
		}
		public String getliquom() {
			return liquom;
		}

		
		public void setliqcat(String liqcat) {
			this.liqcat = liqcat;	
		}

		
		public String getliqcat() {
			return liqcat;
		}

		
} // end class SaleReturnLineItem
