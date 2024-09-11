/********************************************************************************
 *   
 *	Copyright (c) 2016 - 2017 MAX Hypermarket, Inc    All Rights Reserved.
 *	
 *
 *
 *
 *	Rev 1.0		Dec 20, 2016		Mansi Goel		Changes for Gift Card FES
 *
 ********************************************************************************/

package max.retail.stores.domain.stock;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import max.retail.stores.domain.discount.MAXAdvancedPricingRuleIfc;
import max.retail.stores.domain.event.MAXPriceChange;
import max.retail.stores.domain.lineitem.MAXMaximumRetailPriceChangeIfc;
import max.retail.stores.domain.tax.MAXTaxAssignment;
import max.retail.stores.domain.tax.MAXTaxAssignmentIfc;
import max.retail.stores.domain.utility.MAXGiftCardIfc;
import oracle.retail.stores.commerceservices.common.currency.CurrencyIfc;
import oracle.retail.stores.domain.DomainGateway;
import oracle.retail.stores.domain.event.PriceChangeIfc;
import oracle.retail.stores.domain.stock.GiftCardPLUItem;
import oracle.retail.stores.domain.utility.GiftCardIfc;

public class MAXGiftCardPLUItem extends GiftCardPLUItem implements MAXPLUItemIfc {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public MAXGiftCardPLUItem() {
		super();
	}

	protected String itemGroup = "";

	protected String itemDivision = "";

	private String classid = "";

	private String brandName = "";
	protected String subClass = "";

	private String deptid = "";
	String discountCardID = "";
	String redeemptionCode = "";
	String cardType = "";
	String requestType = "";
	protected ArrayList discCardNumbers = null;

	protected List activeMaximumRetailPriceChanges;

	protected List inactiveMaximumRetailPriceChanges;

	/** Maximum Retail Price for an Item */
	protected CurrencyIfc maximumRetailPrice;

	/** Tax Category for an Item */
	protected int taxCategory;

	/** Multiple Maximum Retail Price Flag */
	protected boolean multipleMaximumRetailPriceFlag;

	/** Retail less Than MRP Flag */
	protected boolean retailLessThanMRPFlag = false;

	/**
	 * Creates clone of this object.
	 * <P>
	 * 
	 * @return Object clone of this object
	 **/
	// ----------------------------------------------------------------------------
	public Object clone() { // begin clone()
		// instantiate new object
		MAXGiftCardPLUItem c = new MAXGiftCardPLUItem();

		// set values
		setCloneAttributes(c);

		// pass back Object
		return ((Object) c);
	} // end clone()

	public void setCloneAttributes(MAXGiftCardPLUItem newClass) { // begin
		// setCloneAttributes()
		// set attributes in super class
		super.setCloneAttributes(newClass);
		newClass.setCardType(getCardType());
		newClass.setRequestType(getRequestType());
		// set gift card attribute
		if (giftCard != null) {
			newClass.setGiftCard((GiftCardIfc) getGiftCard().clone());
		}
		if (trackData != null) {
			newClass.setTrackData(trackData);
		}
	} // end setCloneAttributes()

	public void setCardType(String value) {
		cardType = value;
	}

	public String getCardType() {
		return cardType;
	}

	public void setRequestType(String value) {
		requestType = value;
	}

	public String getRequestType() {
		return requestType;
	}

	protected boolean isSwiped = false;
	protected boolean isScanned = false;

	protected String trackData = null;

	public String getTrackData() {
		return trackData;
	}

	public void setTrackData(String atrackData) {
		trackData = atrackData;
	}

	public boolean isSwiped() {
		return isSwiped;
	}

	public void setSwiped(boolean isSwiped) {
		this.isSwiped = isSwiped;
	}

	public boolean isScanned() {
		return isScanned;
	}

	public void setScanned(boolean isScanned) {
		this.isScanned = isScanned;

	}

	protected List itemExclusionGroupList = new ArrayList();

	public List getItemExclusionGroupList() {
		return itemExclusionGroupList;
	}

	public void setItemExclusionGroupList(List itemExclusionGroupList) {
		this.itemExclusionGroupList = itemExclusionGroupList;
	}

	protected String existingItemGroups = null;

	public String getExistingItemGroups() {
		return existingItemGroups;
	}

	public void setExistingItemGroups(String existingItemGroups) {
		this.existingItemGroups = existingItemGroups;
	}

	protected boolean alreadyGotItemGroups = false;

	public boolean isAlreadyGotItemGroups() {
		return alreadyGotItemGroups;
	}

	public void setAlreadyGotItemGroups(boolean alreadyGotItemGroups) {
		this.alreadyGotItemGroups = alreadyGotItemGroups;
	}

	protected boolean isWeightedBarCode;

	private ArrayList taxAssignments;

	public void setWeightedBarCode(boolean isWeightedBarCode) {
		this.isWeightedBarCode = isWeightedBarCode;
	}

	public boolean IsWeightedBarCode() {

		return this.isWeightedBarCode;
	}

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

	public GiftCardIfc getGiftCard() {
		return (MAXGiftCardIfc) (giftCard);
	}

	public void setGiftCard(MAXGiftCardIfc value) {
		giftCard = value;
	}

	public CurrencyIfc getMaximumRetailPrice() {
		// If MRP was not overriden i.e selected then return the Primary
		// MaximumRetailPrice
		if (maximumRetailPrice == null) {
			maximumRetailPrice = getPrimaryMaximumRetailPrice();
		}
		return maximumRetailPrice;
	}

	// Changes for Rev 1.0 : Starts
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

	// Changes for Rev 1.0 : Ends

	public void setMaximumRetailPrice(CurrencyIfc maximumRetailPrice) {
		this.maximumRetailPrice = maximumRetailPrice;
	}

	public int getTaxCategory() {
		return taxCategory;
	}

	public void setTaxCategory(int taxCategory) {
		this.taxCategory = taxCategory;
	}

	public boolean getRetailLessThanMRPFlag() {
		return retailLessThanMRPFlag;
	}

	public void setRetailLessThanMRPFlag(boolean retailLessThanMRPFlag) {
		this.retailLessThanMRPFlag = retailLessThanMRPFlag;
	}

	public boolean getMultipleMaximumRetailPriceFlag() {
		return multipleMaximumRetailPriceFlag;
	}

	public void setMultipleMaximumRetailPriceFlag(boolean multipleMaximumRetailPriceFlag) {
		this.multipleMaximumRetailPriceFlag = multipleMaximumRetailPriceFlag;
	}

	public void addTaxAssignment(MAXTaxAssignmentIfc taxAssignment) {
		if (this.taxAssignments == null) {
			this.taxAssignments = new ArrayList();
		}
		this.taxAssignments.add(taxAssignment);

	}

	public void addTaxAssignments(MAXTaxAssignmentIfc[] taxAssignment) {
		if (this.taxAssignments == null) {
			this.taxAssignments = new ArrayList();
		}

		if (taxAssignment != null) {
			this.taxAssignments.addAll(Arrays.asList(taxAssignment));
		}

	}

	public MAXTaxAssignmentIfc[] getTaxAssignments() {
		MAXTaxAssignmentIfc[] tempTaxAssignments = null;
		if (this.taxAssignments != null && this.taxAssignments.size() > 0) {
			tempTaxAssignments = (MAXTaxAssignmentIfc[]) this.taxAssignments.toArray(new MAXTaxAssignment[0]);
		}
		return tempTaxAssignments;
	}

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

	public void clearTaxAssignments() {
		if (this.taxAssignments != null) {
			this.taxAssignments.clear();
		}
	}

	public CurrencyIfc getSoldMRP() {
		return ((MAXPLUItem) item).getSoldMRP();
	}

	public void setSoldMRP(CurrencyIfc value) {
		((MAXPLUItem) item).setSoldMRP(value);

	}

	public MAXMaximumRetailPriceChangeIfc[] getActiveMaximumRetailPriceChanges() {
		return (MAXMaximumRetailPriceChangeIfc[]) activeMaximumRetailPriceChanges
				.toArray(new MAXMaximumRetailPriceChangeIfc[activeMaximumRetailPriceChanges.size()]);
	}

	public List getInactiveMaximumRetailPriceChanges() {
		return inactiveMaximumRetailPriceChanges;
	}

	public void setActivePriceChangesMaximumRetailPrice(List priceChangesMaximumRetailPrice) {
		this.activeMaximumRetailPriceChanges = priceChangesMaximumRetailPrice;
	}

	public void setInactivePriceChangesMaximumRetailPrice(List priceChangesMaximumRetailPrice) {
		this.inactiveMaximumRetailPriceChanges = priceChangesMaximumRetailPrice;
	}

	public void setActiveMaximumRetailPriceChanges(MAXMaximumRetailPriceChangeIfc[] changes) {
		activeMaximumRetailPriceChanges.clear();
		activeMaximumRetailPriceChanges.addAll(Arrays.asList(changes));
		Collections.sort(activeMaximumRetailPriceChanges);
	}

	public void addActiveMaximumRetailPriceChange(MAXMaximumRetailPriceChangeIfc priceChange) {
		activeMaximumRetailPriceChanges.add(priceChange);
		Collections.sort(activeMaximumRetailPriceChanges);
	}

	public void addActiveMaximumRetailPriceChanges(MAXMaximumRetailPriceChangeIfc[] changes) {
		activeMaximumRetailPriceChanges.addAll(Arrays.asList(changes));
		Collections.sort(activeMaximumRetailPriceChanges);
	}

	public void addInActiveMaximumRetailPriceChange(MAXMaximumRetailPriceChangeIfc priceChange) {
		inactiveMaximumRetailPriceChanges.add(priceChange);
		Collections.sort(inactiveMaximumRetailPriceChanges);
	}

	public void addInactiveMaximumRetailPriceChanges(MAXMaximumRetailPriceChangeIfc[] changes) {
		inactiveMaximumRetailPriceChanges.addAll(Arrays.asList(changes));
		Collections.sort(inactiveMaximumRetailPriceChanges);
	}

	public boolean hasInActiveMaximumRetailPriceChanges() {
		return inactiveMaximumRetailPriceChanges.size() != 0 ? true : false;
	}

	public MAXMaximumRetailPriceChangeIfc[] getInActiveMaximumRetailPriceChanges() {
		return (MAXMaximumRetailPriceChangeIfc[]) this.inactiveMaximumRetailPriceChanges
				.toArray(new MAXMaximumRetailPriceChangeIfc[inactiveMaximumRetailPriceChanges.size()]);
	}

	public void setInActiveMaximumRetailPriceChanges(MAXMaximumRetailPriceChangeIfc[] changes) {
		inactiveMaximumRetailPriceChanges.clear();
		inactiveMaximumRetailPriceChanges.addAll(Arrays.asList(changes));
		Collections.sort(inactiveMaximumRetailPriceChanges);

	}

	// Changes for Rev 1.0 : Starts
	@Override
	public String getItemGroups() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void setItemGroups(String itemGroups) {
		// TODO Auto-generated method stub

	}

	@Override
	public ArrayList<MAXAdvancedPricingRuleIfc> getInvoiceDiscounts() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void setInvoiceDiscounts(ArrayList<MAXAdvancedPricingRuleIfc> invoiceDiscounts) {
		// TODO Auto-generated method stub

	}

	@Override
	public String getItemSizeDesc() {
		return null;
	}

	@Override
	public void setItemSizeDesc(String itemSize) {
	}
	// Changes for Rev 1.0 : Ends

	public String hsnNum;
	@Override
	public String getHsnNum() {
		// TODO Auto-generated method stub
		return hsnNum;
	}

	@Override
	public void setHsnNum(String hsnNum) {
		// TODO Auto-generated method stub
		this.hsnNum=hsnNum;
	}

	@Override
	public boolean isEdgeItem() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void setEdgeItem(boolean edgeItem) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void setliquom(String liquom) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public String getliquom() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void setliqcat(String liqcat) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public String getliqcat() {
		// TODO Auto-generated method stub
		return null;
	}
}
