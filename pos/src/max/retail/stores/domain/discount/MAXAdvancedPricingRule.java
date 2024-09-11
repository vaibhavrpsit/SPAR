/********************************************************************************
 *   
 *	Copyright (c) 2016 - 2017 MAX Hypermarket, Inc    All Rights Reserved.
 *	
 *	Rev 1.3		May 16, 2017		Nitesh Kumar	Changes to resolve application hang issue for weighted items.
 *	Rev 1.2		May 03, 2017		Mansi Goel		Changes to resolve null pointer exception										for unit price tiered rule
 *	Rev	1.1 	Nov 22, 2016		Ashish Yadav	Changes for Employee Discount FES
 *	Rev	1.0 	Nov 07, 2016		Mansi Goel		Changes for Discount Rule FES	
 *
 ********************************************************************************/

package max.retail.stores.domain.discount;

// java imports
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import max.retail.stores.domain.MAXUtils.MAXUtils;
import max.retail.stores.domain.comparators.MAXComparators;
import max.retail.stores.domain.lineitem.MAXSaleReturnLineItem;
import max.retail.stores.domain.lineitem.MAXSaleReturnLineItemIfc;
import max.retail.stores.domain.stock.MAXPLUItemIfc;
import oracle.retail.stores.commerceservices.common.currency.CurrencyIfc;
import oracle.retail.stores.domain.DomainGateway;
import oracle.retail.stores.domain.comparators.Comparators;
import oracle.retail.stores.domain.discount.AdvancedPricingRule;
import oracle.retail.stores.domain.discount.BestDealGroupIfc;
import oracle.retail.stores.domain.discount.DiscountRuleConstantsIfc;
import oracle.retail.stores.domain.discount.DiscountSourceIfc;
import oracle.retail.stores.domain.discount.DiscountTargetIfc;
import oracle.retail.stores.domain.discount.SuperGroupIfc;
import oracle.retail.stores.domain.lineitem.SaleReturnLineItemIfc;

import org.apache.log4j.Logger;

//------------------------------------------------------------------------------
/**
 * AdvancedPricingRule aggregates the criteria for applying an advanced discount
 * to an item or groups of items.
 * 
 * @version $Revision: 1.6 $
 **/
// ------------------------------------------------------------------------------
public class MAXAdvancedPricingRule extends AdvancedPricingRule implements MAXAdvancedPricingRuleIfc {

	/**
	 * 
	 */
	private static final long serialVersionUID = -4416196270776675144L;
	private static Logger logger = Logger.getLogger(MAXAdvancedPricingRule.class);

	public int mValue = 0;
	protected String description = "";
	protected HashMap capillaryCoupon = new HashMap();
	
	// Changes start for Rev 1.1 (Ashish : Employee discount)
	protected String customerType = null;
	// Changes ends for Rev 1.1 (Ashish : Employee discount)
	
	

	// ---------------------------------------------------------------------
	/**
	 * Default constructor.
	 **/
	// ---------------------------------------------------------------------
	public MAXAdvancedPricingRule() {
		super();
	}

	public ArrayList generateBestDealGroups(ArrayList sources, ArrayList targets, int groupLimit) {
		reset();
		excludeRuleExclusions(sources, targets);
		if (!MAXUtils.isTieredDiscount(getReasonCode())) {
			// Checking for Tiered Discounts
			// validateItemPrices(sources, targets);
		}

		validateItemPriceCategories(sources, targets);
		int numberGenerated = 0;

		while ((numberGenerated < groupLimit) && evaluateSourceAndTargets(sources, targets)) {
			// selected sources and targets can no longer be sources
			removeSelectedSources(sources);
			removeSelectedTargets(sources);
			addBestDealGroup();
			numberGenerated++;
		}

		createSuperGroups();

		return groups;
	}

	// ---------------------------------------------------------------------
	/**
	 * Instantiates a best deal group, initializes it with source/target
	 * references, and adds it to the collection of groups maintained by this
	 * rule.
	 **/
	// ---------------------------------------------------------------------
	protected void addBestDealGroup() {
		//Changes for Rev 1.2 : Starts : 5_UnitPrice
		CurrencyIfc mrp = DomainGateway.getBaseCurrencyInstance();
		//Changes for Rev 1.2 : Ends : Patch 5_UnitPrice
		for (int k = 0; k < sources.size(); k++) {
			MAXSaleReturnLineItemIfc src = (MAXSaleReturnLineItemIfc) sources.get(k);
			mrp = ((MAXPLUItemIfc) src.getPLUItem()).getMaximumRetailPrice();
		}

		BestDealGroupIfc group = DomainGateway.getFactory().getBestDealGroupInstance();

		if ((!getDescription().equalsIgnoreCase("BuyRsNOrMoreOfXGetYatZRsTiered"))
				|| (!getFixedPrice().getStringValue().equalsIgnoreCase("0.00"))) {
			if ((!getDescription().equalsIgnoreCase("BuyNOrMoreOfXGetatUnitPriceTiered"))
					|| ((getFixedPrice().getDoubleValue() <= mrp.getDoubleValue()) && (!getFixedPrice()
							.getStringValue().equalsIgnoreCase("0.00")))) {
				group.setDiscountRule(this);

				group.calculateTotalDiscount();
				group.setSources((ArrayList) sources.clone());
				group.setTargets((ArrayList) targets.clone());

				for (int k = 0; k < sources.size(); k++) {
					SaleReturnLineItemIfc src = (SaleReturnLineItemIfc) sources.get(k);
					((MAXSaleReturnLineItemIfc) src).setTargetIdentifier(false);
					if (!((MAXPLUItemIfc) src.getPLUItem()).getMaximumRetailPrice().toString()
							.equalsIgnoreCase(src.getSellingPrice().toString())) {
						src.getItemPrice().setSellingPrice(((MAXPLUItemIfc) src.getPLUItem()).getMaximumRetailPrice());
						src.getItemPrice().setExtendedSellingPrice(
								((MAXPLUItemIfc) src.getPLUItem()).getMaximumRetailPrice());
					}
				}

				for (int t = 0; t < targets.size(); t++) {
					SaleReturnLineItemIfc tgt = (SaleReturnLineItemIfc) targets.get(t);
					((MAXSaleReturnLineItemIfc) tgt).setTargetIdentifier(true);
					if (!((MAXPLUItemIfc) tgt.getPLUItem()).getMaximumRetailPrice().toString()
							.equalsIgnoreCase(tgt.getSellingPrice().toString())) {
						tgt.getItemPrice().setSellingPrice(((MAXPLUItemIfc) tgt.getPLUItem()).getMaximumRetailPrice());
						tgt.getItemPrice().setExtendedSellingPrice(
								((MAXPLUItemIfc) tgt.getPLUItem()).getMaximumRetailPrice());
					}

				}

				if (Integer.parseInt(this.getReason().getCode()) == DiscountRuleConstantsIfc.DISCOUNT_REASON_BuyNofXgetHighestPricedXatZPctoff) {
					CurrencyIfc highestPrice = null;
					ArrayList sorttargets = targets;
					Collections.sort(sorttargets, Comparators.lineItemPriceDescending);

					for (Iterator t = sorttargets.iterator(); t.hasNext();) {
						SaleReturnLineItemIfc srli = (SaleReturnLineItemIfc) t.next();
						CurrencyIfc tempPrice = srli.getExtendedSellingPrice();
						((MAXSaleReturnLineItemIfc) srli).setTargetIdentifier(false);
						if ((highestPrice == null) || (tempPrice.compareTo(highestPrice) == 1)) {
							highestPrice = tempPrice;
							((MAXSaleReturnLineItemIfc) srli).setTargetIdentifier(true);
						}
					}
				}

				if (Integer.parseInt(this.getReason().getCode()) == DISCOUNT_REASON_BuyNofXgetLowestPricedXatZPctoff) {
					CurrencyIfc lowestPrice = null;
					ArrayList sorttargets = targets;
					Collections.sort(sorttargets, MAXComparators.lineItemPriceAscending);
					for (Iterator t = sorttargets.iterator(); t.hasNext();) {
						SaleReturnLineItemIfc srli = (SaleReturnLineItemIfc) t.next();
						CurrencyIfc tempPrice = srli.getExtendedSellingPrice();
						((MAXSaleReturnLineItemIfc) srli).setTargetIdentifier(false);
						if ((lowestPrice == null) || (tempPrice.compareTo(lowestPrice) == -1)) {
							lowestPrice = tempPrice;
							((MAXSaleReturnLineItemIfc) srli).setTargetIdentifier(true);
						}
					}
				}
				groups.add(group);
				sources.clear();
				targets.clear();
			}
			//changes for rev 1.3 starts
			else if ((getDescription().equalsIgnoreCase("BuyNOrMoreOfXGetatUnitPriceTiered"))
					&& ((getFixedPrice().getDoubleValue() > mrp.getDoubleValue()) || (getFixedPrice()
							.getStringValue().equalsIgnoreCase("0.00")))) {
				sources.clear();
				targets.clear();
			}
			//changes for rev 1.3 ends
		}
	}

	@Override
	protected boolean sourcesSatisfied(ArrayList<DiscountSourceIfc> possibleSources) {
		boolean satisfied = false;

		if (checkSources) {
			if (getThresholdTypeCode() == THRESHOLD_AMOUNT) {
				removeTarget(possibleSources);
			}

			// When both the source and target of the discount rule are set to
			// "Class", sources
			// are evaluated in this flow.
			if (areSourceAndTargetSetToMerchandiseClass()) {
				satisfied = sourceList.evaluateAllEligibleSourcesAndTargets(possibleSources, sources);
			} else {
				satisfied = sourceList.evaluate(possibleSources, sources);
			}

			// if the source list is satisfied for this rule, debug the names
			if (satisfied && logger.isDebugEnabled()) {
				logger.debug("The following rule has required sources: Rule ID:" + getRuleID() + ", Name: "
						+ getLocalizedNames());
			}
		} else
		// not evaluating source criteria so just add all to sources
		{
			if (!possibleSources.isEmpty()) {
				sources.addAll(possibleSources);
				possibleSources.clear();
				satisfied = true;
			} else {
				// exit the calling loop if sources have already been added
				satisfied = false;
			}
		}
		if (!satisfied) {
			for (int t = 0; t < possibleSources.size(); t++) {
				SaleReturnLineItemIfc tgt = (SaleReturnLineItemIfc) possibleSources.get(t);
				if (!tgt.getItemPrice().getSellingPrice().toString()
						.equalsIgnoreCase(tgt.getItemPrice().getPermanentSellingPrice().toString())
						&& isPotentialTarget((DiscountTargetIfc) tgt)) {
					tgt.getItemPrice().setSellingPrice(tgt.getItemPrice().getPermanentSellingPrice());
					tgt.getItemPrice().setExtendedSellingPrice(tgt.getItemPrice().getPermanentSellingPrice());
				}
			}
		}
		
		return satisfied;
	}

	protected boolean targetsSatisfied(ArrayList<DiscountTargetIfc> possibleTargets) {
		boolean value = false;

		if (sourcesAreTargets) {
			value = convertSourcesToTargets();
		} else {
			// When both the source and target of the discount rule are set to
			// "Class", or when EOLV is enabled targets
			// are evaluated in this flow.
			if (areSourceAndTargetSetToMerchandiseClass() || isEqualOrLesserValue()) {
				value = targetList.evaluateAllEligibleSourcesAndTargets(possibleTargets, targets);
			} else {
				// sources are not targets so check against the criteria in the
				// list
				removeSelectedSources(possibleTargets);
				value = targetList.evaluate(possibleTargets, targets);
				if (value == true) {
					for (int t = 0; t < targets.size(); t++) {

						SaleReturnLineItemIfc tgt = (SaleReturnLineItemIfc) targets.get(t);
						((MAXSaleReturnLineItemIfc) tgt).setTargetIdentifier(true);
						if (!((MAXPLUItemIfc) tgt.getPLUItem()).getMaximumRetailPrice().toString()
								.equalsIgnoreCase(tgt.getSellingPrice().toString())) {
							tgt.getItemPrice().setSellingPrice(
									((MAXPLUItemIfc) tgt.getPLUItem()).getMaximumRetailPrice());
							tgt.getItemPrice().setExtendedSellingPrice(
									((MAXPLUItemIfc) tgt.getPLUItem()).getMaximumRetailPrice());

						}
					}
				} else {
					for (int t = 0; t < possibleTargets.size(); t++) {

						SaleReturnLineItemIfc tgt = (SaleReturnLineItemIfc) possibleTargets.get(t);

						if (!tgt.getItemPrice().getSellingPrice().toString()
								.equalsIgnoreCase(tgt.getItemPrice().getPermanentSellingPrice().toString())
								&& isPotentialTarget((DiscountTargetIfc) tgt)) {
							tgt.getItemPrice().setSellingPrice(tgt.getItemPrice().getPermanentSellingPrice());
							tgt.getItemPrice().setExtendedSellingPrice(tgt.getItemPrice().getPermanentSellingPrice());
						}
					}

					for (int t = 0; t < targets.size(); t++) {

						SaleReturnLineItemIfc tgt = (SaleReturnLineItemIfc) targets.get(t);
						if (!tgt.getItemPrice().getSellingPrice().toString()
								.equalsIgnoreCase(tgt.getItemPrice().getPermanentSellingPrice().toString())
								&& isPotentialTarget((DiscountTargetIfc) tgt)) {
							tgt.getItemPrice().setSellingPrice(tgt.getItemPrice().getPermanentSellingPrice());
							tgt.getItemPrice().setExtendedSellingPrice(tgt.getItemPrice().getPermanentSellingPrice());
						}
					}
				}
			}
		}
		return value;
	}

	public Object clone() {
		MAXAdvancedPricingRule newClass = new MAXAdvancedPricingRule();
		setCloneAttributes(newClass);
		return newClass;
	}

	// ---------------------------------------------------------------------
	/**
	 * Sets the clone attributes for this object.
	 * 
	 * @param AdvancedPricingRule
	 *            to set the clone attributes on
	 **/
	// ---------------------------------------------------------------------
	public void setCloneAttributes(MAXAdvancedPricingRule newClass) {
		// clone the superclass attributes
		super.setCloneAttributes(newClass);
		// Changes start for Rev 1.1 (Ashish : Employee Discount)
		newClass.customerType = customerType;
		// Changes ends for Rev 1.1 (Ashish : Employee Discount)
		newClass.setmValue(this.mValue);
		newClass.setDescription(description);
		newClass.setCapillaryCoupon(capillaryCoupon);
	}

	@Override
	public boolean isPotentialSource(DiscountSourceIfc source) {
		boolean isSource = false;

		if (checkSources) {
			MAXSaleReturnLineItem source1 = (MAXSaleReturnLineItem) source;
			isSource = ((MAXDiscountListIfc) sourceList).uses(source1, true);
			if (!isSource && isStoreLevelDiscountRule()) {
				isSource = true;
			}

		} else {
			// everything is a source
			isSource = true;
		}

		return isSource;
	}

	@Override
	public boolean isPotentialTarget(DiscountTargetIfc target) {
		MAXSaleReturnLineItem target1 = (MAXSaleReturnLineItem) target;
		return ((MAXDiscountListIfc) targetList).uses(target1, true);
	}

	public CurrencyIfc getDiscountAmountOnMLowestPricedItem() {
		CurrencyIfc[] lowestPrice = getMLowestTargetPrice(targets);
		CurrencyIfc discountAmount = DomainGateway.getBaseCurrencyInstance();
		CurrencyIfc discountAmountFinal = DomainGateway.getBaseCurrencyInstance();
		if (lowestPrice != null) {
			if (getDiscountRate() != null) {
				for (int i = 0; i < ((MAXAdvancedPricingRule) this).getmValue(); i++) {
					discountAmount = lowestPrice[i].multiply(getDiscountRate());
					discountAmountFinal = discountAmountFinal.add(discountAmount);
				}
			}
		}
		return discountAmountFinal;
	}

	private boolean convertSourcesToTargets() {
		for (DiscountSourceIfc source : sources) {
			DiscountTargetIfc target = (DiscountTargetIfc) source;
			if (target.isTargetEnabled()) {
				targets.add(target);
			}
		}
		return !targets.isEmpty();
	}

	public CurrencyIfc[] getMLowestTargetPrice(ArrayList pTargets) {
		List lowestPriceList = new ArrayList();
		CurrencyIfc[] retArry = null;
		Collections.sort(pTargets, MAXComparators.lineItemPriceAscending);
		for (Iterator t = pTargets.iterator(); t.hasNext();) {
			SaleReturnLineItemIfc srli = (SaleReturnLineItemIfc) t.next();
			CurrencyIfc tempPrice = srli.getExtendedSellingPrice();
			lowestPriceList.add(tempPrice);
		}
		retArry = (CurrencyIfc[]) lowestPriceList.toArray(new CurrencyIfc[lowestPriceList.size()]);
		return retArry;
	}

	public BestDealGroupIfc findBestDealGroupForRule(ArrayList sources, ArrayList targets, int groupLimit) {
		reset();
		if (!MAXUtils.isTieredDiscount(getReasonCode())) {
			// Checking for Tiered Discounts
			validateItemPrices(sources, targets);
		}
		int numberGenerated = 0;

		while ((numberGenerated < groupLimit) && evaluateSourceAndTargets(sources, targets)) {
			// selected sources and targets can no longer be sources
			removeSelectedSources(sources);
			removeSelectedTargets(sources);
			addBestDealGroup();
			numberGenerated++;
		}
		int groupSize = groups.size();

		// create SuperGroups if there was more than one set
		if (groupSize > 1) {
			ArrayList list = new ArrayList();

			for (int j = 0; j < groupSize; j++) {
				list.add(groups.get(j));
			}
			SuperGroupIfc supergroup = DomainGateway.getFactory().getSuperGroupInstance();
			supergroup.setSubgroups(list);
			supergroup.setDiscountRule(this);
			return supergroup;
		} else if (groupSize == 1) {
			return ((BestDealGroupIfc) groups.get(0));
		} else {
			return null;
		}
	}

	public void setmValue(int Value) {
		mValue = Value;
	}

	public int getmValue() {
		return mValue;
	}

	// end
	public String getDescription() {
		return (description);
	}

	public void setDescription(String value) {
		description = value;
	}

	public HashMap getCapillaryCoupon() {
		return capillaryCoupon;
	}
// Changes start for rev 1.1 (Ashish : Employee Discount)
	public void setCapillaryCoupon(HashMap capillaryCoupon) {
		this.capillaryCoupon = capillaryCoupon;
	}

	public String getCustomerType() {
		return customerType;
	}

	public void setCustomerType(String customerType) {
		this.customerType = customerType;
	}
	// Changes ends for rev 1.1 (Ashish : Employee Discount)
	//changes by kamlesh 
		protected String targetItemId = null;

		public String getTargetItemId() {
			return targetItemId;
		}

		public String setTargetItemId(String targetItemId) {
			return this.targetItemId = targetItemId;
		}
       
		protected List<String> itemList = null;
		public List<String> getItemList() {
			// TODO Auto-generated method stub
			return itemList;
		}

		@Override
		public List<String> setItemList(List<String> itemList) {
			// TODO Auto-generated method stub
			return this.itemList=itemList;
			}
}
