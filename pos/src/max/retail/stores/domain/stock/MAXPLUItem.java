/********************************************************************************
 *   
 *	Copyright (c) 2016 - 2017 MAX Hypermarket, Inc    All Rights Reserved.

 *  Rev 1.5     Sep 06, 2022		Kamlesh Pant	CapLimit Enforcement for Liquor
 *  Rev 1.4     May 04, 2017		Kritica Agarwal GST Changes
 *	Rev 1.3		Mar 20, 2016		Nitesh Kumar	Changes for MMRP
 *	Rev 1.2		Dec 20, 2016		Mansi Goel		Changes for Gift Card FES
 *	Rev 1.1		Nov 07, 2016		Mansi Goel		Changes for Discount Rule FES
 *	Rev	1.0 	Aug 16, 2016		Nitesh Kumar	Changes for Code Merging	
 *
 ********************************************************************************/

package max.retail.stores.domain.stock;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import max.retail.stores.domain.discount.MAXAdvancedPricingRuleIfc;
import max.retail.stores.domain.event.MAXPriceChange;
import max.retail.stores.domain.event.MAXPriceChangeIfc;
import max.retail.stores.domain.factory.MAXDomainObjectFactoryIfc;
import max.retail.stores.domain.lineitem.MAXMaximumRetailPriceChangeIfc;
import max.retail.stores.domain.tax.MAXTaxAssignment;
import max.retail.stores.domain.tax.MAXTaxAssignmentIfc;
import oracle.retail.stores.commerceservices.common.currency.CurrencyIfc;
import oracle.retail.stores.domain.DomainGateway;
import oracle.retail.stores.domain.event.PriceChangeIfc;
import oracle.retail.stores.domain.lineitem.ItemContainerProxyIfc;
import oracle.retail.stores.domain.stock.ItemIfc;
import oracle.retail.stores.domain.stock.PLUItem;
import oracle.retail.stores.domain.utility.EYSDate;
import oracle.retail.stores.foundation.utility.Util;

public class MAXPLUItem extends PLUItem implements MAXPLUItemIfc {

	private static final long serialVersionUID = -4619006822560311529L;

	private static final String merchandiseHierarchyGroupId = null;

	protected String itemGroup = "";

	protected String itemDivision = "";

	private String classid = "";

	private String brandName = "";
	protected String subClass = "";

	private String deptid = "";

	private List<MAXMaximumRetailPriceChangeIfc> activeMaximumRetailPriceChanges;

	private List<MAXMaximumRetailPriceChangeIfc> inactiveMaximumRetailPriceChanges;

	protected ArrayList<MAXTaxAssignmentIfc> taxAssignments;

	protected CurrencyIfc maximumRetailPrice;

	private boolean multipleMaximumRetailPriceFlag;

	protected boolean retailLessThanMRPFlag = false;

	private int taxCategory;

	protected String itemGroups = null;

	protected ArrayList<MAXAdvancedPricingRuleIfc> invoiceDiscounts;

	protected String itemSizeDesc;
	/**
	 *
	 */
	public MAXPLUItem() {
		super();
		activeMaximumRetailPriceChanges = new ArrayList<MAXMaximumRetailPriceChangeIfc>();
		inactiveMaximumRetailPriceChanges = new ArrayList<MAXMaximumRetailPriceChangeIfc>();
	}

	public Object clone() {
		MAXPLUItem newItem = new MAXPLUItem();
		setCloneAttributes(newItem);
		return newItem;
	}

	/**
	 * Determine if two PersonName objects are identical.
	 * 
	 * @param obj
	 *            object to compare with
	 * @return boolean true if the objects are identical
	 */
	public boolean equals(Object obj) {
		boolean result = false;
		MAXPLUItem itemIn = null;

		try {
			if (this == obj) {
				result = true;
			} else {
				// compare all the attributes of SaleReturnLineItem
				itemIn = (MAXPLUItem) obj;
				// compare all the attributes of PLUItem
				if (Util.isObjectEqual(item, itemIn.item)
						&&
						// comparing the price can end in a loop as the price is
						// calculated
						// with rules that might compare the price again 26OCT07
						// CMG
						Util.isObjectEqual(taxGroupName, itemIn.taxGroupName)
						&& Util.isObjectEqual(getRelatedItemContainer(), itemIn.getRelatedItemContainer())
						&& Util.isObjectEqual(getPosItemID(), itemIn.getPosItemID())
						//Changes for rev 1.3 starts
						&& Util.isObjectEqual(getSellingPrice(),
								 itemIn.getSellingPrice())) {
					//changes for rev 1.3 ends
					result = true;
				}
			}
		} catch (Exception e) // catching classcastexceptions is faster than
		// instanceof
		{
			result = false;
		}
		return result;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.extendyourstore.domain.stock.ItemIfc#getItemID()
	 */
	public String getItemID() {
		return item.getItemID();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.extendyourstore.domain.stock.ItemIfc#getItemWeight()
	 */
	public BigDecimal getItemWeight() {
		return item.getItemWeight();
	}

	/**
	 * Calls {@link #getPermanentPrice(EYSDate)} with {@link EYSDate#EYSDate()}
	 * as the parameter.
	 * 
	 * @see com.extendyourstore.domain.stock.ItemIfc#getPermanentPrice()
	 */
	public CurrencyIfc getPermanentPrice() {
		return getPermanentPrice(new EYSDate(), this.getMaximumRetailPrice());
	}

	/**
	 * Returns the Permanent Price for the for a given date
	 * 
	 * @param date
	 * @return
	 */
	public CurrencyIfc getPermanentPrice(EYSDate date) {
		return getPermanentPrice(date, this.getMaximumRetailPrice());
	}

	/**
	 * Returns the Permanent Price for the for a given date,MaximumRetailPrice
	 * 
	 * @param date
	 * @param selectedMaximimumRetailPrice
	 * @return CurrencyIfc
	 * @since 12.0.9IN
	 */
	public CurrencyIfc getPermanentPrice(EYSDate when, CurrencyIfc selectedMaximimumRetailPrice) {
		CurrencyIfc price = null;

		CurrencyIfc mrp = selectedMaximimumRetailPrice != null ? selectedMaximimumRetailPrice : this
				.getMaximumRetailPrice();

		if (hasPermanentPriceChanges()) {
			for (Iterator<PriceChangeIfc> iter = priceChangesPermanent(); iter.hasNext();) {
				PriceChangeIfc priceChange = (PriceChangeIfc) iter.next();
				// Latest permanent price change for a given MRP.
				if (priceChange.isInEffect(when)
						&& ((MAXPriceChange) priceChange).getMaximumRetailPriceChange() != null
						&& mrp.compareTo(((MAXPriceChange) priceChange).getMaximumRetailPriceChange()
								.getMaximumRetailPrice()) == 0) {
					// because we sort price changes by effective date
					// picking the first is appropriate.
					price = priceChange.getNewPrice();
					break;
				}
			}
		}
		return (price != null) ? price : item.getPermanentPrice();
	}

	/**
	 * Calls {@link #getPrice(EYSDate)} with {@link EYSDate#EYSDate()} as the
	 * parameter.
	 * 
	 * @see com.extendyourstore.domain.stock.PLUItemIfc#getPrice()
	 */
	public CurrencyIfc getPrice() {
		return getPrice(new EYSDate(), this.getMaximumRetailPrice());
	}

	/**
	 * Calls {@link #getEffectiveTemporaryPriceChange(EYSDate) to check for
	 * effective temporarty promotions, then {@link #getPermanentPrice()} if the
	 * first is null.
	 * <p>
	 * If there are no price changes, {@link ItemIfc#getSellingPrice()} is
	 * returned.
	 * 
	 * @see com.extendyourstore.domain.stock.ItemIfc#getPrice()
	 */
	public CurrencyIfc getPrice(EYSDate when) {
		CurrencyIfc price = null;
		PriceChangeIfc priceChange = getEffectiveTemporaryPriceChange(when, this.getMaximumRetailPrice());
		if (priceChange != null) {
			price = priceChange.getNewPrice();
		}
		if (price == null) {
			price = getPermanentPrice();
		}
		return (price != null) ? price : item.getSellingPrice();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.extendyourstore.domain.stock.PLUItemIfc#getPermanentPriceChanges()
	 */
	public PriceChangeIfc[] getPermanentPriceChanges() {
		PriceChangeIfc[] array = new PriceChangeIfc[priceChangesPermanent.size()];
		priceChangesPermanent.toArray(array);
		return array;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.extendyourstore.domain.stock.PLUItemIfc#getTemporaryPriceChanges()
	 */
	public PriceChangeIfc[] getTemporaryPriceChanges() {
		PriceChangeIfc[] array = new PriceChangeIfc[priceChangesTemporary.size()];
		priceChangesTemporary.toArray(array);
		return array;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.extendyourstore.domain.stock.PLUItemIfc#
	 * getEffectiveTemporaryPriceChange ()
	 */
	public PriceChangeIfc getEffectiveTemporaryPriceChange() {
		return getEffectiveTemporaryPriceChange(new EYSDate());
	}

	/**
	 * If this item {@link #hasTemporaryPriceChanges()} then this method loops
	 * through {@link #priceChangesTemporary()} returning the first price change
	 * that is effective.
	 * 
	 * @see com.extendyourstore.domain.stock.PLUItemIfc#getEffectiveTemporaryPriceChange(com.extendyourstore.domain.utility.EYSDate)
	 */
	public PriceChangeIfc getEffectiveTemporaryPriceChange(EYSDate when) {

		if (hasTemporaryPriceChanges()) {
			for (Iterator iter = priceChangesTemporary(); iter.hasNext();) {
				PriceChangeIfc priceChange = (PriceChangeIfc) iter.next();
				if (priceChange.isInEffect(when)
						&& this.getMaximumRetailPrice()
								.compareTo(
										((MAXPriceChangeIfc) priceChange).getMaximumRetailPriceChange()
												.getMaximumRetailPrice()) == 0) {
					// because we sort price changes by effective date
					// picking the first is appropriate.
					return priceChange;
				}
			}
		}
		return null;
	}

	/**
	 * If this item {@link #hasTemporaryPriceChanges()} then this method loops
	 * through {@link #priceChangesTemporary()} returning the first price change
	 * that is effective.
	 * 
	 * @see com.extendyourstore.domain.stock.PLUItemIfc#getEffectiveTemporaryPriceChange(com.extendyourstore.domain.utility.EYSDate)
	 * @since 12.0.9IN
	 */
	public PriceChangeIfc getEffectiveTemporaryPriceChange(EYSDate when, CurrencyIfc selectedMaximumRetailPrice) {
		if (hasTemporaryPriceChanges()) {
			for (Iterator iter = priceChangesTemporary(); iter.hasNext();) {
				PriceChangeIfc priceChange = (PriceChangeIfc) iter.next();
				if (priceChange.isInEffect(when)
						&& ((MAXPriceChangeIfc) priceChange).getMaximumRetailPriceChange().getMaximumRetailPrice()
								.compareTo(selectedMaximumRetailPrice) == 0) {
					// because we sort price changes by effective date
					// picking the first is appropriate.
					return priceChange;
				}
			}
		}
		return null;
	}

	/**
	 * Returns the selling price for a given a MRP.
	 * 
	 * @param maximumRetailPrice
	 * @return CurrencyIfc
	 * @since 12.0.9IN
	 */
	public CurrencyIfc getSellingPrice(CurrencyIfc maximumRetailPrice) {
		CurrencyIfc sellingPrice = null;

		if (this.hasAdvancedPricingRules()) {
			ItemContainerProxyIfc proxy = DomainGateway.getFactory().getItemContainerProxyInstance();
			// Override the passed MRP in the PLUItem object
			this.setMaximumRetailPrice(maximumRetailPrice);
			sellingPrice = proxy.addPLUItem(this).getExtendedDiscountedSellingPrice();
		} else {
			sellingPrice = getPrice(new EYSDate(), maximumRetailPrice);
		}
		return sellingPrice;
	}

	/**
	 * Returns the Price for a given date and maximumRetailPrice
	 * 
	 * @param maximumRetailPrice
	 * @return CurrencyIfc
	 * @since 12.0.9IN
	 */
	public CurrencyIfc getPrice(EYSDate date, CurrencyIfc maximumRetailPrice) {
		CurrencyIfc price = null;
		MAXPriceChangeIfc priceChange = (MAXPriceChangeIfc) getEffectiveTemporaryPriceChange(date, maximumRetailPrice);
		if (priceChange != null) {
			price = priceChange.getNewPrice();
		}
		if (price == null) {
			price = getPermanentPrice(date, maximumRetailPrice);
		}
		return price;
	}

	public void setAlterationItemFlag(boolean value) {
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.extendyourstore.domain.stock.PLUItemIfc#setCloneAttributes(com.
	 * extendyourstore.domain.stock.PLUItem)
	 */
	public void setCloneAttributes(PLUItem newClass) {
		super.setCloneAttributes(newClass);

		MAXMaximumRetailPriceChangeIfc maximumRetailPriceChange = null;
		// Active MaximumRetailPrice Changes
		for (Iterator i = activeMaximumRetailPriceChanges.iterator(); i.hasNext();) {
			maximumRetailPriceChange = (MAXMaximumRetailPriceChangeIfc) i.next();
			if (maximumRetailPriceChange != null)
				((MAXPLUItem) newClass)
						.addActiveMaximumRetailPriceChange((MAXMaximumRetailPriceChangeIfc) maximumRetailPriceChange
								.clone());
		}

		// InActive MaximumRetailPrice Changes
		for (Iterator i = inactiveMaximumRetailPriceChanges.iterator(); i.hasNext();) {
			maximumRetailPriceChange = (MAXMaximumRetailPriceChangeIfc) i.next();
			if (maximumRetailPriceChange != null)
				((MAXPLUItem) newClass)
						.addInActiveMaximumRetailPriceChange((MAXMaximumRetailPriceChangeIfc) maximumRetailPriceChange
								.clone());
		}

		// fix for bug 7376
		if (this.itemExclusionGroupList != null) {
			((MAXPLUItem) newClass).setItemExclusionGroupList(this.itemExclusionGroupList);
		}
		// end fix for bug 7376
		/* India Localization - Tax,MRP Changes Starts Here */
		MAXTaxAssignmentIfc txAssignment = null;
		if (taxAssignments != null) {
			for (Iterator i = taxAssignments.iterator(); i.hasNext();) {
				txAssignment = (MAXTaxAssignmentIfc) i.next();
				((MAXPLUItem) newClass).addTaxAssignment(txAssignment == null ? null
						: (MAXTaxAssignmentIfc) txAssignment.clone());
			}
		}
		((MAXPLUItem) newClass).setMaximumRetailPrice(maximumRetailPrice);
		((MAXPLUItem) newClass).setTaxCategory(taxCategory);
		((MAXPLUItem) newClass).setRetailLessThanMRPFlag(retailLessThanMRPFlag);
		((MAXPLUItem) newClass).setMultipleMaximumRetailPriceFlag(multipleMaximumRetailPriceFlag);
		((MAXPLUItem) newClass).setItemGroup(itemGroup);
		((MAXPLUItem) newClass).setItemDivision(itemDivision);
		((MAXPLUItem) newClass).setItemDepartmentId(deptid);
		((MAXPLUItem) newClass).setItemClassId(classid);
		((MAXPLUItem) newClass).setSubClass(subClass);
		((MAXPLUItem) newClass).setBrandName(brandName);
		// Changes for Rev 1.1 : Starts
		((MAXPLUItem) newClass).setItemGroups(itemGroups);
		((MAXPLUItem) newClass).setInvoiceDiscounts(invoiceDiscounts);
		// Changes for Rev 1.1 : Ends
		// Changes for Rev 1.2 : Starts
		((MAXPLUItem) newClass).setItemSizeDesc(itemSizeDesc);
		((MAXPLUItem) newClass).setHsnNum(hsnNum);
		// Changes for Rev 1.2 : Ends
		
		((MAXPLUItem) newClass).setliquom(liquom);
		((MAXPLUItem) newClass).setliqcat(liqcat);
		
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.extendyourstore.domain.stock.ItemIfc#setSellingPrice(com._360commerce
	 * .commerceservices.common.currency.CurrencyIfc)
	 * 
	 * @param value current selling price
	 */
	public void setSellingPrice(CurrencyIfc value) {
		item.setSellingPrice(value);
		// set its price
		PriceChangeIfc pc = DomainGateway.getFactory().getPriceChangeInstance();
		pc.setOverridePriceAmount(value);
		// Sets the MaximumRetailPriceChange object with the mrp set
		MAXMaximumRetailPriceChangeIfc mrpc = ((MAXDomainObjectFactoryIfc) DomainGateway.getFactory())
				.getMaximumRetailPriceChangeInstance();
		mrpc.setMaximumRetailPrice(this.getMaximumRetailPrice());
		((MAXPriceChangeIfc) pc).setMaximumRetailPriceChange(mrpc);
		addTemporaryPriceChange(pc);
	}

	public String toString() { // begin toString()

		// result string
		StringBuffer strResult = new StringBuffer("Class:  PLUItem ");
		strResult.append("(Revision ").append(getRevisionNumber()).append(") @").append(hashCode()).append(Util.EOL)
				.append("\titemID:        [").append(getItemID()).append("]").append(Util.EOL)
				.append("\tiPosItemID:    [").append(getPosItemID()).append("]").append(Util.EOL)
				.append("\tdescription:   [").append(getLocalizedDescriptions()).append("]").append(Util.EOL)
				.append("\tprice:         [").append(getPrice()).append("]").append(Util.EOL)
				.append("\ttaxGroupID:    [").append(getTaxGroupID()).append("]").append(Util.EOL)
				.append("\ttaxable:       [").append(getTaxable()).append("]").append(Util.EOL)
				.append("\titemWeight:    [").append(getItemWeight()).append("]").append(Util.EOL);
		if (getItemClassification() == null) {
			strResult.append("\tclassification:[null]");
		} else {
			strResult.append("\tclassification:").append(Util.EOL).append(getItemClassification().toString());
		}

		if (getRelatedItemContainer() == null) {
			strResult.append("\tRelatedItemContainer:[null]").append(Util.EOL);
		} else {
			strResult.append("\tRelatedItemContainer:").append(Util.EOL);
			strResult.append(getRelatedItemContainer().toString());
		}
		/* India Localization Changes- MRP,Tax Related Changes Start here */
		if (getMaximumRetailPrice() == null) {
			strResult.append("\tMaximumRetailPrice:[null]").append(Util.EOL);
		} else {
			strResult.append("\tMaximumRetailPrice:").append(Util.EOL);
			strResult.append(getMaximumRetailPrice().toString());
		}

		strResult.append("\tTaxCategory:").append(Util.EOL);
		strResult.append(getTaxCategory());
		strResult.append("\tRetailLessThanMRP").append(Util.EOL);
		strResult.append(getRetailLessThanMRPFlag());
		strResult.append("\tMultipleMRPFlag:").append(Util.EOL);
		strResult.append(this.getMultipleMaximumRetailPriceFlag());
		/* India Localization Changes- MRP,Tax Related Changes ends here */

		// pass back result
		return strResult.toString();
	} // end toString()

	public CurrencyIfc getMaximumRetailPrice() {
		// If MRP was not overriden i.e selected then return the Primary
		// MaximumRetailPrice
		if (maximumRetailPrice == null) {
			maximumRetailPrice = getPrimaryMaximumRetailPrice();
		}
		return maximumRetailPrice;
	}

	/**
	 * Returns the latest primary maximumRetailPrice from the effective active
	 * permanent price changes for all the MRP's
	 * 
	 * @return
	 * @since 12.0.9IN
	 */
	public CurrencyIfc getPrimaryMaximumRetailPrice() {
		MAXMaximumRetailPriceChangeIfc maximumRetailPriceChange = null;
		for (Iterator i = priceChangesPermanent(); i.hasNext();) {
			PriceChangeIfc priceChange = (PriceChangeIfc) i.next();
			if (((MAXPriceChange) priceChange).getMaximumRetailPriceChange() != null
					&& ((MAXPriceChange) priceChange).getMaximumRetailPriceChange().isPrimary()) {
				maximumRetailPriceChange = ((MAXPriceChange) priceChange).getMaximumRetailPriceChange();
				break;
			}
		}
		// For Items having retailLessThanMRP return the latest MRP
		// associated with the Price Change.
		if (maximumRetailPriceChange == null && this.getPermanentPriceChanges().length != 0) {
			PriceChangeIfc[] priceChange = this.getPermanentPriceChanges();
			maximumRetailPriceChange = ((MAXPriceChange) priceChange[0]).getMaximumRetailPriceChange();
		}
		return maximumRetailPriceChange != null ? maximumRetailPriceChange.getMaximumRetailPrice() : DomainGateway
				.getBaseCurrencyInstance();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.extendyourstore.domain.stock.PLUItemIfc#setMaximumRetailPrice(com
	 * ._360commerce.commerceservices.common.currency.CurrencyIfc)
	 */
	public void setMaximumRetailPrice(CurrencyIfc maximumRetailPrice) {
		this.maximumRetailPrice = maximumRetailPrice;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.extendyourstore.domain.stock.ItemIfc#getTaxCategory()
	 */

	public int getTaxCategory() {
		return taxCategory;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.extendyourstore.domain.stock.ItemIfc#setTaxCategory(int)
	 */
	public void setTaxCategory(int taxCategory) {
		this.taxCategory = taxCategory;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.extendyourstore.domain.stock.ItemIfc#getRetailLessThanMRPFlag()
	 */
	public boolean getRetailLessThanMRPFlag() {
		return retailLessThanMRPFlag;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.extendyourstore.domain.stock.ItemIfc#setRetailLessThanMRPFlag(boolean
	 * )
	 */
	public void setRetailLessThanMRPFlag(boolean retailLessThanMRPFlag) {
		this.retailLessThanMRPFlag = retailLessThanMRPFlag;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.extendyourstore.domain.stock.ItemIfc#
	 * getMultipleMaximumRetailPriceFlag ()
	 */
	public boolean getMultipleMaximumRetailPriceFlag() {
		return multipleMaximumRetailPriceFlag;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.extendyourstore.domain.stock.ItemIfc#
	 * setMultipleMaximumRetailPriceFlag (boolean)
	 */
	public void setMultipleMaximumRetailPriceFlag(boolean multipleMaximumRetailPriceFlag) {
		this.multipleMaximumRetailPriceFlag = multipleMaximumRetailPriceFlag;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.extendyourstore.domain.stock.PLUItemIfc#addTaxAssignment(com.
	 * extendyourstore.domain.stock.TaxAssignmentIfc)
	 */
	public void addTaxAssignment(MAXTaxAssignmentIfc taxAssignment) {
		if (this.taxAssignments == null) {
			this.taxAssignments = new ArrayList();
		}
		this.taxAssignments.add(taxAssignment);

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.extendyourstore.domain.stock.PLUItemIfc#addTaxAssignments(com.
	 * extendyourstore.domain.tax.TaxAssignmentIfc[])
	 */
	public void addTaxAssignments(MAXTaxAssignmentIfc[] taxAssignment) {
		if (this.taxAssignments == null) {
			this.taxAssignments = new ArrayList();
		}

		if (taxAssignment != null) {
			this.taxAssignments.addAll(Arrays.asList(taxAssignment));
		}

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.extendyourstore.domain.stock.PLUItemIfc#getTaxAssignments()
	 */
	public MAXTaxAssignmentIfc[] getTaxAssignments() {
		MAXTaxAssignmentIfc[] tempTaxAssignments = null;
		if (this.taxAssignments != null && this.taxAssignments.size() > 0) {
			tempTaxAssignments = (MAXTaxAssignmentIfc[]) this.taxAssignments.toArray(new MAXTaxAssignment[0]);
		}
		return tempTaxAssignments;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.extendyourstore.domain.stock.PLUItemIfc#setTaxAssignments(com.
	 * extendyourstore.domain.stock.TaxAssignmentIfc[])
	 */
	public void setTaxAssignments(MAXTaxAssignmentIfc[] taxAssignment) {
		if (taxAssignment != null) {
			if (this.taxAssignments != null) {
				this.taxAssignments.clear();
			} else {
				this.taxAssignments = new ArrayList();
			}
			this.taxAssignments.addAll(Arrays.asList(taxAssignment));

		} else {
			if (this.taxAssignments == null) {
				this.taxAssignments = new ArrayList();
			} else {
				this.taxAssignments.clear();
			}
		}

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.extendyourstore.domain.stock.PLUItemIfc#setTaxAssignments(com.
	 * extendyourstore.domain.stock.TaxAssignmentIfc[])
	 */
	public void clearTaxAssignments() {
		if (this.taxAssignments != null) {
			this.taxAssignments.clear();
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.extendyourstore.domain.stock.ItemIfc#getSoldMRP()
	 */
	public CurrencyIfc getSoldMRP() {
		return ((MAXPLUItem) item).getSoldMRP();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.extendyourstore.domain.stock.ItemIfc#setSoldMRP()
	 */
	public void setSoldMRP(CurrencyIfc value) {
		((MAXPLUItem) item).setSoldMRP(value);

	}

	/**
	 * Returns the Active MaximumRetailPriceChanges.
	 * 
	 * @return List
	 * @since 12.0.9IN
	 */
	public MAXMaximumRetailPriceChangeIfc[] getActiveMaximumRetailPriceChanges() {
		return (MAXMaximumRetailPriceChangeIfc[]) activeMaximumRetailPriceChanges
				.toArray(new MAXMaximumRetailPriceChangeIfc[activeMaximumRetailPriceChanges.size()]);
	}

	/**
	 * Returns the InActive MaximumRetailPriceChanges.
	 * 
	 * @return List
	 * @since 12.0.9IN
	 */
	public List getInactiveMaximumRetailPriceChanges() {
		return inactiveMaximumRetailPriceChanges;
	}

	/**
	 * Sets the Active MaximumRetailPriceChanges.
	 * 
	 * @param priceChangesMaximumRetailPrice
	 * @since 12.0.9IN
	 */
	public void setActivePriceChangesMaximumRetailPrice(List priceChangesMaximumRetailPrice) {
		this.activeMaximumRetailPriceChanges = priceChangesMaximumRetailPrice;
	}

	/**
	 * Sets the Inactive MaximumRetailPriceChanges.
	 * 
	 * @param priceChangesMaximumRetailPrice
	 * @since 12.0.9IN
	 */
	public void setInactivePriceChangesMaximumRetailPrice(List priceChangesMaximumRetailPrice) {
		this.inactiveMaximumRetailPriceChanges = priceChangesMaximumRetailPrice;
	}

	/**
	 * Sets the MaximumRetailPriceChanges,resettign the previous MRP Changes
	 * 
	 * @param changes
	 * @since 12.0.9IN
	 */
	public void setActiveMaximumRetailPriceChanges(MAXMaximumRetailPriceChangeIfc[] changes) {
		activeMaximumRetailPriceChanges.clear();
		activeMaximumRetailPriceChanges.addAll(Arrays.asList(changes));
		// Rev 1.2 change
		// Collections.sort(activeMaximumRetailPriceChanges);
	}

	/**
	 * Add the Active MaximumRetailPriceChanges.
	 * 
	 * @param changes
	 * @since 12.0.9IN
	 */
	public void addActiveMaximumRetailPriceChange(MAXMaximumRetailPriceChangeIfc priceChange) {
		activeMaximumRetailPriceChanges.add(priceChange);
		// uncommented by Vaibhav
		Collections.sort(activeMaximumRetailPriceChanges);
		// end
	}

	/**
	 * Adds the Active MaximumRetailPriceChanges.
	 * 
	 * @param changes
	 * @since 12.0.9IN
	 */
	public void addActiveMaximumRetailPriceChanges(MAXMaximumRetailPriceChangeIfc[] changes) {
		activeMaximumRetailPriceChanges.addAll(Arrays.asList(changes));
		Collections.sort(activeMaximumRetailPriceChanges);
	}

	/**
	 * Add the InActive MaximumRetailPriceChanges.
	 * 
	 * @param changes
	 * @since 12.0.9IN
	 */
	public void addInActiveMaximumRetailPriceChange(MAXMaximumRetailPriceChangeIfc priceChange) {
		inactiveMaximumRetailPriceChanges.add(priceChange);
		Collections.sort(inactiveMaximumRetailPriceChanges);
	}

	/**
	 * Adds the InActive MaximumRetailPriceChanges.
	 * 
	 * @param changes
	 * @since 12.0.9IN
	 */
	public void addInactiveMaximumRetailPriceChanges(MAXMaximumRetailPriceChangeIfc[] changes) {
		inactiveMaximumRetailPriceChanges.addAll(Arrays.asList(changes));
		Collections.sort(inactiveMaximumRetailPriceChanges);
	}

	/**
	 * Indicates whether PLUItem has hasInActiveMaximumRetailPriceChanges
	 * 
	 * @return
	 * @since 12.0.9IN
	 */
	public boolean hasInActiveMaximumRetailPriceChanges() {
		return inactiveMaximumRetailPriceChanges.size() != 0 ? true : false;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.extendyourstore.domain.stock.PLUItemIfc#
	 * getInActiveMaximumRetailPriceChanges()
	 */
	public MAXMaximumRetailPriceChangeIfc[] getInActiveMaximumRetailPriceChanges() {
		return (MAXMaximumRetailPriceChangeIfc[]) this.inactiveMaximumRetailPriceChanges
				.toArray(new MAXMaximumRetailPriceChangeIfc[inactiveMaximumRetailPriceChanges.size()]);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.extendyourstore.domain.stock.PLUItemIfc#
	 * setInActiveMaximumRetailPriceChanges
	 * (com.extendyourstore.domain.lineitem.MaximumRetailPriceChangeIfc[])
	 */
	public void setInActiveMaximumRetailPriceChanges(MAXMaximumRetailPriceChangeIfc[] changes) {
		inactiveMaximumRetailPriceChanges.clear();
		inactiveMaximumRetailPriceChanges.addAll(Arrays.asList(changes));
		Collections.sort(inactiveMaximumRetailPriceChanges);

	}

	// Changes for Rev 1.1 : Starts
	public ArrayList<MAXAdvancedPricingRuleIfc> getInvoiceDiscounts() {
		return invoiceDiscounts;
	}

	public void setInvoiceDiscounts(ArrayList<MAXAdvancedPricingRuleIfc> invoiceDiscounts) {
		this.invoiceDiscounts = invoiceDiscounts;
	}

	// Changes for Rev 1.1 : Ends
	protected List itemExclusionGroupList = new ArrayList();

	public List getItemExclusionGroupList() {
		return itemExclusionGroupList;
	}

	public void setItemExclusionGroupList(List itemExclusionGroupList) {
		this.itemExclusionGroupList = itemExclusionGroupList;
	}

	/* 12.0.9IN- Tax,MRP Changes ends Here */

	// <!-- MAX Rev 1.0 Change : Start -->
	protected boolean isWeightedBarCode;

	public void setWeightedBarCode(boolean isWeightedBarCode) {
		this.isWeightedBarCode = isWeightedBarCode;
	}

	public boolean IsWeightedBarCode() {

		return this.isWeightedBarCode;
	}

	// <!-- MAX Rev 1.0 Change : end -->
	// added for Capillary Coupon (Dipak Goit)
	public String getItemGroup() {
		return itemGroup;
	}

	public void setItemGroup(String itemGroup) {
		this.itemGroup = itemGroup;
	}

	public String getItemDivision() {
		return itemDivision;
	}

	public void setItemDivision(String itemDivision) {
		this.itemDivision = itemDivision;
	}

	// End for Capillary Coupon Discount

	public void setSubClass(String merchandiseHierarchyGroupId) {
		this.subClass = merchandiseHierarchyGroupId;

	}

	public String getSubClass() {
		// TODO Auto-generated method stub
		return subClass;
	}

	public void setItemDepartmentId(String deptid) {
		this.deptid = deptid;

	}

	public String getItemDepartmentId() {
		// TODO Auto-generated method stub
		return deptid;
	}

	public void setItemClassId(String classId) {
		// TODO Auto-generated method stub
		this.classid = classId;
	}

	public String getClassId() {
		// TODO Auto-generated method stub
		return classid;
	}

	public void setBrandName(String brandName) {
		// TODO Auto-generated method stub
		this.brandName = brandName;
	}

	public String getBrandName() {
		// TODO Auto-generated method stub
		return brandName;
	}

	// Changes for Rev 1.1 : Starts
	public String getItemGroups() {
		return itemGroups;
	}

	public void setItemGroups(String itemGroups) {
		this.itemGroups = itemGroups;
	}
	// Changes for Rev 1.1 : Ends

	// Changes for Rev 1.2 : Starts
	public String getItemSizeDesc() {
		return itemSizeDesc;
	}

	public void setItemSizeDesc(String itemSizeDesc) {
		this.itemSizeDesc = itemSizeDesc;
	}
	// Changes for Rev 1.2 : Ends
	
	/* Rev 1.4 changes starts*/
	protected String hsnNum;
	
	public String getHsnNum() {
		return hsnNum;
	}

	public void setHsnNum(String hsnNum) {
		this.hsnNum = hsnNum;
	}
	/* Rev 1.4 changes ends */

	@Override
	public boolean isEdgeItem() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void setEdgeItem(boolean edgeItem) {
		// TODO Auto-generated method stub
		
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
	
	/* Rev 1.5 changes ends */
}
