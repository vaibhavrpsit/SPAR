/********************************************************************************
 *   
 *	Copyright (c) 2016 - 2017 MAX Hypermarket, Inc    All Rights Reserved.
 *	
 *  Rev 1.4     Apr 06, 2017		Hitesh Dua		comment the extra code than v12
 *  Bug :After changing the value of RU_PRDV.QU_LM_APLY, Wrong discount is coming on receipt while it is correct on sell item screen
 *  
 *	Rev 1.3		Mar 23, 2017		Mansi Goel		Changes to fix discount price is not calculated properly for suspended transaction
 *  Rev 1.2     Mar 17, 2017        Nitika Arora    Changes for bug(scann the promo item and do the layaway, promo item amt is getting changed in the screen)
 *	Rev 1.1		Dec 20, 2016		Mansi Goel		Changes for Gift Card FES
 *	Rev	1.0 	Nov 07, 2016		Mansi Goel		Changes for Discount Rule FES	
 *
 ********************************************************************************/


package max.retail.stores.domain.lineitem;

// java imports
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;

import max.retail.stores.domain.discount.MAXAdvancedPricingRuleIfc;
import max.retail.stores.domain.discount.MAXBestDealGroup;
import max.retail.stores.domain.discount.MAXDiscountRuleConstantsIfc;
import max.retail.stores.domain.stock.MAXGiftCardPLUItem;
import max.retail.stores.domain.stock.MAXPLUItem;
import max.retail.stores.domain.stock.MAXPLUItemIfc;
import max.retail.stores.domain.tax.MAXTaxAssignment;
import max.retail.stores.domain.tax.MAXTaxAssignmentIfc;
import oracle.retail.stores.commerceservices.common.currency.CurrencyIfc;
import oracle.retail.stores.commerceservices.externalorder.ExternalOrderItemIfc;
import oracle.retail.stores.common.utility.BigDecimalConstants;
import oracle.retail.stores.common.utility.LocaleMap;
import oracle.retail.stores.common.utility.Util;
import oracle.retail.stores.domain.DomainGateway;
import oracle.retail.stores.domain.comparators.BestDealDiscountDescending;
import oracle.retail.stores.domain.comparators.Comparators;
import oracle.retail.stores.domain.customer.CustomerIfc;
import oracle.retail.stores.domain.discount.AdvancedPricingRuleIfc;
import oracle.retail.stores.domain.discount.BestDealGroupIfc;
import oracle.retail.stores.domain.discount.CustomerDiscountByPercentageIfc;
import oracle.retail.stores.domain.discount.DiscountListIfc;
import oracle.retail.stores.domain.discount.DiscountRuleConstantsIfc;
import oracle.retail.stores.domain.discount.DiscountRuleIfc;
import oracle.retail.stores.domain.discount.DiscountSourceIfc;
import oracle.retail.stores.domain.discount.DiscountTargetIfc;
import oracle.retail.stores.domain.discount.SuperGroup;
import oracle.retail.stores.domain.discount.SuperGroupIfc;
import oracle.retail.stores.domain.discount.TransactionDiscountByPercentageIfc;
import oracle.retail.stores.domain.discount.TransactionDiscountStrategyIfc;
import oracle.retail.stores.domain.event.PriceChangeIfc;
import oracle.retail.stores.domain.lineitem.AbstractTransactionLineItemIfc;
import oracle.retail.stores.domain.lineitem.ItemContainerProxy;
import oracle.retail.stores.domain.lineitem.ItemTaxIfc;
import oracle.retail.stores.domain.lineitem.KitComponentLineItemIfc;
import oracle.retail.stores.domain.lineitem.KitHeaderLineItemIfc;
import oracle.retail.stores.domain.lineitem.PriceAdjustmentLineItemIfc;
import oracle.retail.stores.domain.lineitem.SaleReturnLineItem;
import oracle.retail.stores.domain.lineitem.SaleReturnLineItemIfc;
import oracle.retail.stores.domain.stock.GiftCertificateItem;
import oracle.retail.stores.domain.stock.KitComponent;
import oracle.retail.stores.domain.stock.PLUItemIfc;
import oracle.retail.stores.domain.utility.CodeConstantsIfc;
import oracle.retail.stores.domain.utility.EYSDate;
import oracle.retail.stores.domain.utility.LocaleConstantsIfc;

import org.apache.log4j.Logger;

//--------------------------------------------------------------------------
/**
 * This class provides the implementation for an entity which uses a list of
 * items. Entities which would use this class are SaleReturnTransaction and
 * Order.
 * <P>
 * The ItemContainerProxy class contains the array of items plus the other
 * modifiers which operate on the using entity, such as discounts and tax. These
 * are contained herein because these modifiers force a change in the items in
 * the container (such as when a transaction discount or transaction tax
 * modifier is pro-rated across the items).
 * 
 * @see com.extendyourstore.domain.lineitem.ItemContainerProxyIfc
 * @see com.extendyourstore.domain.transatcion.SaleReturnTransactionIfc
 * @see com.extendyourstore.domain.order.OrderIfc
 * @version $Revision: 1.6 $ S
 **/
// --------------------------------------------------------------------------
public class MAXItemContainerProxy extends ItemContainerProxy implements MAXItemContainerProxyIfc {

	private static final long serialVersionUID = -3659370695704187091L;

	private static Logger logger = Logger.getLogger(max.retail.stores.domain.lineitem.MAXItemContainerProxy.class);

	private final CurrencyIfc ZERO;
	protected List exclusionList = new ArrayList();
	protected boolean taxApplied = false;
	boolean UOMItem = false;

	public MAXItemContainerProxy() {
		ZERO = DomainGateway.getBaseCurrencyInstance();
		initialize();
	}

	/**
	 * Initialize values.
	 */
	protected void initialize() {
		lineItemsVector = new Vector<AbstractTransactionLineItemIfc>(2);
		transactionDiscountsVector = new Vector<TransactionDiscountStrategyIfc>(2);
		transactionTax = DomainGateway.getFactory().getTransactionTaxInstance();
		advancedPricingRules = new HashMap<String, DiscountRuleIfc>(0);
		bestDealGroups = new ArrayList<BestDealGroupIfc>(2);
		bestDealWinners = new ArrayList<BestDealGroupIfc>(2);
		amountLineVoids = DomainGateway.getBaseCurrencyInstance();
		unitsLineVoids = BigDecimal.ZERO;
		discountLimits = DomainGateway.getFactory().getDiscountListInstance();
		itemsByTaxGroup = new Hashtable<String, Vector<SaleReturnLineItemIfc>>(0);
		cspAdvancedPricingRules = new HashMap<String, DiscountRuleIfc>(2);
	}

	public Object clone() {
		MAXItemContainerProxy itemContainerProxy = new MAXItemContainerProxy();
		setCloneAttributes(itemContainerProxy);
		//rev for 1.4
		// regenerate best deal discounts on the cloned items
		//itemContainerProxy.calculateBestDeal();
		return itemContainerProxy;
	}

	protected void setCloneAttributes(MAXItemContainerProxy newClass) {
		super.setCloneAttributes(newClass);
		if (exclusionList != null) {
			newClass.exclusionList = exclusionList;
		}
	} // end setCloneAttributes()

	// ---------------------------------------------------------------------
	/**
	 * Clones SaleReturnLineItem vector.
	 * <P>
	 * 
	 * @return clone of sale return line items vector
	 **/
	// ---------------------------------------------------------------------
	public AbstractTransactionLineItemIfc[] cloneLineItems() {
		AbstractTransactionLineItemIfc[] atli = getLineItems();
		AbstractTransactionLineItemIfc[] tclone = null;
		if (atli != null) {
			tclone = new AbstractTransactionLineItemIfc[atli.length];
			for (int i = 0; i < atli.length; i++) {
				tclone[i] = (AbstractTransactionLineItemIfc) atli[i].clone();
			}
		}

		return (tclone);
	}

	protected void completeAddPLUItem(SaleReturnLineItemIfc srli, BigDecimal qty) {
		if (srli.isKitHeader()) {
			addKitComponentItems((KitHeaderLineItemIfc) srli);
		}

		// get pricing group id
		if (customer != null && customer.getPricingGroupID() != null && !srli.hasExternalPricing()) {
			revaluateLineItemPrice(srli);
		}
		if (srli.getPLUItem().getAdvancedPricingRules().length > 0) {
			srli.getItemPrice().setExtendedSellingPrice(((MAXPLUItemIfc) srli.getPLUItem()).getMaximumRetailPrice());
			srli.getItemPrice().setSellingPrice(((MAXPLUItemIfc) srli.getPLUItem()).getMaximumRetailPrice());
		}

		// required for best deal calculations
		srli.calculateLineItemPrice();

		// if quantity is not 1, modify it
		if (qty.compareTo(BigDecimalConstants.ONE_AMOUNT) != 0) {
			srli.modifyItemQuantity(qty);
		}

		srli.setLineNumber(lineItemsVector.size());
		lineItemsVector.addElement(srli);
		resetLineItemNumbers();

		if (customer != null && customer.getPricingGroupID() != null && !srli.hasExternalPricing()) {
			constructCSPAdvancedPricingRule(srli.getPLUItem());
		}

		if (!srli.hasExternalPricing()) {
			addItemAdvancedPricingRules(srli.getPLUItem());
			// calculateBestDeal();
		}
		if (!(srli.getPLUItem() instanceof MAXGiftCardPLUItem) && !(srli.getPLUItem() instanceof GiftCertificateItem)) {
			this.exclusionList = ((MAXPLUItemIfc) srli.getPLUItem()).getItemExclusionGroupList();
		}
	}

	public SaleReturnLineItemIfc addPLUItem(PLUItemIfc pItem, BigDecimal qty, boolean isApplyBestDeal) {
		SaleReturnLineItemIfc srli = createSaleReturnLineItemInstance(pItem);
		if (srli.isKitHeader()) {
			addKitComponentItems((KitHeaderLineItemIfc) srli);
		}

		// get pricing group id
		if (customer != null && customer.getPricingGroupID() != null && !srli.hasExternalPricing()) {
			revaluateLineItemPrice(srli);
		}

		// required for best deal calculations
		srli.calculateLineItemPrice();

		// if quantity is not 1, modify it
		if (qty.compareTo(BigDecimalConstants.ONE_AMOUNT) != 0) {
			srli.modifyItemQuantity(qty);
		}

		srli.setLineNumber(lineItemsVector.size());
		// lineItemsVector.addElement(srli);
		resetLineItemNumbers();

		if (customer != null && customer.getPricingGroupID() != null && !srli.hasExternalPricing()) {
			constructCSPAdvancedPricingRule(srli.getPLUItem());
		}

		if (!(srli.getPLUItem() instanceof MAXGiftCardPLUItem) && !(srli.getPLUItem() instanceof GiftCertificateItem)) {
			this.exclusionList = ((MAXPLUItemIfc) srli.getPLUItem()).getItemExclusionGroupList();
		}

		if (!srli.hasExternalPricing()) {
			addItemAdvancedPricingRules(srli.getPLUItem());
			calculateBestDeal();
		}

		if (bestDealWinners != null && bestDealWinners.size() != 0 && srli.getAdvancedPricingDiscount() != null) {
			BestDealGroupIfc bdw = bestDealWinners.get(0);
			String descRuleDesc = (bdw.getDiscountRule().getDescription());
			srli.getAdvancedPricingDiscount().setDescription(descRuleDesc);

		}
		return srli;
	}

	public SaleReturnLineItemIfc createSaleReturnLineItemInstance(PLUItemIfc pItem,
			ExternalOrderItemIfc pExternalOrderItem) {
		pItem = determinePLUItemToUse(pItem);

		// create and initialze the line item object
		SaleReturnLineItemIfc srli = null;
		if (pItem != null) {
			if (pItem.isKitHeader()) {
				srli = DomainGateway.getFactory().getKitHeaderLineItemInstance();
				srli.initialize(
						pItem,
						BigDecimal.ONE,
						initializeItemTax(pItem), getSalesAssociate(), getDefaultRegistry(), null,
						pExternalOrderItem);
			} else if (pItem.isKitComponent()) {
				srli = DomainGateway.getFactory().getKitComponentLineItemInstance();
				srli.initialize(
						pItem,
						((KitComponent) pItem).getQuantity(),
						initializeItemTax(pItem), getSalesAssociate(), getDefaultRegistry(), null,
						pExternalOrderItem);
			} else {
				srli = DomainGateway.getFactory().getSaleReturnLineItemInstance();
				// CR 27192
				srli.initialize(
						pItem,
						BigDecimal.ONE,
						initializeItemTax(pItem), getSalesAssociate(), getDefaultRegistry(), null,
						pExternalOrderItem);
			}
		}
		return srli;
	}

	// ---------------------------------------------------------------------
	/**
	 * Splits any multi-quantity line items that are held by this transaction
	 * into single unit line items if they could be used to satisfy an advanced
	 * pricing rule's criteria .
	 **/
	// ---------------------------------------------------------------------
	public void splitSourcesAndTargets() {
		SaleReturnLineItemIfc srli = null;
		// test each item for discount potential and split if quantity is > 1
		for (Iterator i = ((Vector) lineItemsVector.clone()).iterator(); i.hasNext();) {
			srli = (SaleReturnLineItemIfc) i.next();

			if ((((MAXSaleReturnLineItemIfc) srli).getBestDealWinnerName() != null
					|| srli.getPLUItem().getAdvancedPricingRules().length != 0 || isPotentialTarget((DiscountTargetIfc) srli))
					&& srli.getItemQuantity().intValue() > 1 && !srli.isUnitOfMeasureItem())
				splitLineItem(srli);
			// }
		}
	}

	public void splitLineItem(SaleReturnLineItemIfc srli) {
		int position = 0;
		// remove the item from the line items vector
		for (Iterator<AbstractTransactionLineItemIfc> i = lineItemsVector.iterator(); i.hasNext();) {
			if (i.next() == srli) {
				i.remove();
				break;
			}
			position++;
		}

		BigDecimal qty = srli.getItemQuantityDecimal();
		BigDecimal newQty = BigDecimalConstants.ONE_AMOUNT;
		if (qty.signum() == -1) {
			newQty = newQty.negate();
		}
		// create clones of the line item, set their quantity to one
		// and add them to the vector
		for (int i = 0; i < qty.abs().intValue(); i++) {
			SaleReturnLineItemIfc newSrli = (SaleReturnLineItem) srli.clone();
			if (srli.getItemQuantityDecimal().intValue() > 0) {
				newSrli.modifyItemQuantity(BigDecimalConstants.ONE_AMOUNT);
			}

			else {
				newSrli.modifyItemQuantity(BigDecimalConstants.NEGATIVE_ONE);
			}
			newSrli.calculateLineItemPrice();
			((DiscountSourceIfc) newSrli).setSourceAvailable(true);
			lineItemsVector.insertElementAt(newSrli, position++);
			newSrli.setSelectedForItemSplit(true);
		}
		resetLineItemNumbers();
	}

	// ---------------------------------------------------------------------
	/**
	 * Calculates and applies the best deal discounts for advanced pricing
	 * rules.
	 **/
	// ---------------------------------------------------------------------
	public void calculateBestDeal() {
		// remove any previously applied advanced pricing discounts
		// this call enables all sources and clears the existing best deal
		// winners
		clearBestDealDiscounts();

		// recheck Transaction Discounts in the transaction Discount vector
		checkTransactionDiscountVector();

		if (discountLimits == null) {
			discountLimits = DomainGateway.getFactory().getDiscountListInstance();
		}

		if (this.hasAdvancedPricingRules()) {
			BestDealGroupIfc group = null;
			ArrayList<DiscountSourceIfc> availableSources = null;
			ArrayList<DiscountTargetIfc> availableTargets = null;
			boolean dealApplied = true;

			// reset the limits list using current pricing rules
			initializeDiscountLimits(discountLimits);

			// test for and split items with quantity > 1
			splitSourcesAndTargets(false);

			while (dealApplied) {
				// initialize the flag to false for each iteration, will be set
				// to true if a
				// best deal is found and applied
				dealApplied = false;

				// clear any groups left over from a previous iteration
				clearBestDealGroups();

				// get just the sale source and target items
				availableSources = getDiscountSources();
				availableTargets = getDiscountTargets();

				for (int k = 0; k < availableSources.size(); k++) {
					SaleReturnLineItemIfc src = (SaleReturnLineItemIfc) availableSources.get(k);
					((MAXPLUItemIfc) src.getPLUItem()).setItemExclusionGroupList(null);
				}

				// compare the sale sources and targets against the available
				// rules
				factorAgainstRules(availableSources, availableTargets);

				// get the remaining non-receipt return source and target items
				ArrayList<DiscountSourceIfc> returnSources = getDiscountSources(true);
				ArrayList<DiscountTargetIfc> returnTargets = getDiscountTargets(true);

				// compare the return sources and targets against the available
				// rules
				factorAgainstRules(returnSources, returnTargets);

				// check to make sure a non-transaction level group isn't
				// beaten by a transaction wide group - this is necessary due
				// to the use of checkSources and sourcesAreTargets flags
				// in order to give transaction level deals all discount
				// eligible items
				// in a transaction
				filterTransactionBestDealGroups();

				// sort the bestDealGroups list in descending order by total
				// discount
				Collections.sort(bestDealGroups, Comparators.bestDealDiscountDescending);

				// add the returns back to the sales before combinging best
				// deals
				availableSources.addAll(returnSources);
				availableTargets.addAll(returnTargets);

				// generate combinations and add them to list of bestDealGroups
				combineBestDealGroups(availableSources, availableTargets);

				// apply the bestDealGroup with greatest total discount
				if (!bestDealGroups.isEmpty()) {
					group = bestDealGroups.get(0);
					group.applyAdvancedPricingDiscounts();
					bestDealWinners.add(group);
					if (group instanceof SuperGroup) {
						List subgroupList = new ArrayList();
						subgroupList = ((SuperGroup) group).getSubgroups();
						int subgroupListLength = subgroupList.size();
						for (int i = 0; i < subgroupListLength; i++) {
							try {
								MAXBestDealGroup subgroup = (MAXBestDealGroup) subgroupList.get(i);
								if (subgroup.getTotalDiscount() != null
										&& subgroup.getTotalDiscount().getDecimalValue()
												.compareTo(new BigDecimal("0.00")) != 0)
									setBestDealRuleNameToLineItemInSubGroup(subgroup);
							} catch (Exception e) {
								SuperGroup spg = (SuperGroup) subgroupList.get(i);
								setBestDealRuleNameToLineItemInSuperGroup(spg);
							}
						}

					} else {
						// Added by Gaurav to Get the best deal winner on sale
						// Item screen to display DR Name..starts
						for (int i = 0; i < group.getTargets().size(); i++) {
							try {
								SaleReturnLineItemIfc addNameLneItem = (SaleReturnLineItemIfc) (group.getTargets()
										.get(i));
								// modified by Izhar
								((MAXSaleReturnLineItemIfc) addNameLneItem).setBestDealWinnerName(bestDealWinners
										.get(0).getDiscountRule()
										.getName(LocaleMap.getLocale(LocaleConstantsIfc.JOURNAL)));

							} catch (Exception e) {

							}
						}
						if (!group.getDiscountRule().getSourcesAreTargets()) {
							for (int i = 0; i < group.getSources().size(); i++) {
								try {
									SaleReturnLineItemIfc addNameLneItem = (SaleReturnLineItemIfc) (group.getSources()
											.get(i));
									((MAXSaleReturnLineItemIfc) addNameLneItem).setBestDealWinnerName(bestDealWinners
											.get(0).getDiscountRule()
											.getName(LocaleMap.getLocale(LocaleConstantsIfc.JOURNAL)));
								} catch (Exception e) {
								}
							}

						}
					}

					updateDiscountLimits(group, discountLimits);
					dealApplied = true;
					if (logger.isDebugEnabled()) {
						StringBuffer logString = new StringBuffer();
						logString.append("Winning rule is ").append(group.getRuleID());
						logString.append(" with sources ");
						for (Iterator<DiscountSourceIfc> i = group.getSources().iterator(); i.hasNext();) {
							logString.append(((SaleReturnLineItemIfc) i.next()).getPLUItem().getPosItemID());
							logString.append(":");
						}
						logString.append(" and targets ");
						for (Iterator<DiscountTargetIfc> i = group.getTargets().iterator(); i.hasNext();) {
							logString.append(((SaleReturnLineItemIfc) i.next()).getPLUItem().getPosItemID());
							logString.append(":");
						}
						logger.debug(logString.toString());
					}
				}

			} // end while (dealApplied)
		} // end if (this.hasAdvancedPricingRules())
	}

	protected void factorAgainstRules(ArrayList<DiscountSourceIfc> availableSources,
			ArrayList<DiscountTargetIfc> availableTargets) {
		AdvancedPricingRuleIfc rule = null;

		// for each advanced pricing rule, create BestDealGroups
		// using the sources and targets currently available to the
		// transaction
		for (Iterator<DiscountRuleIfc> i = advancedPricingRules(); i.hasNext();) {
			rule = (AdvancedPricingRuleIfc) i.next();

			if (rule.isIncludedInBestDeal()) {
				String ruleID = rule.getRuleID();
				rule.getSourceList().setDescription(rule.getDescription());
				rule.getTargetList().setDescription(rule.getDescription());
				if (exclusionList != null) {
					for (Iterator j = exclusionList.iterator(); j.hasNext();) {
						String a = (String) j.next();
						int b = a.indexOf("_");
						String c = a.substring(0, b);

						if (c.equals(ruleID)) {
							for (int k = 0; k < availableSources.size(); k++) {

								SaleReturnLineItemIfc slrLocal = (SaleReturnLineItemIfc) availableSources.get(k);
								if (slrLocal.getPLUItemID().equals(a.substring(b + 1))) {
									((MAXPLUItemIfc) slrLocal.getPLUItem()).setItemExclusionGroupList(exclusionList);
								}
							}
						}
					}
				}

				if (!discountLimits.containsEntry(ruleID)) {
					addBestDealGroups(rule.generateBestDealGroups(
							(ArrayList<DiscountSourceIfc>) availableSources.clone(),
							(ArrayList<DiscountTargetIfc>) availableTargets.clone()));
				} else {
					int availableApplications = discountLimits.getQuantityRequired(ruleID)
							- discountLimits.getQuantity(ruleID);
					if (availableApplications == 99) {
						availableApplications = 10;
					}
					addBestDealGroups(rule.generateBestDealGroups(
							(ArrayList<DiscountSourceIfc>) availableSources.clone(),
							(ArrayList<DiscountTargetIfc>) availableTargets.clone(), availableApplications));
				}
			} // end if (rule.isIncludedInBestDeal())
		} // end for
	}

	public void setBestDealRuleNameToLineItemInSubGroup(MAXBestDealGroup subgroup) {
		String appliedRuleName = subgroup.getDiscountRule().getName(LocaleMap.getLocale(LocaleMap.DEFAULT));
		boolean sourceAreTargetsFlag = subgroup.getDiscountRule().getSourcesAreTargets();
		ArrayList<DiscountSourceIfc> DiscountSourceList = subgroup.getSources();
		ArrayList<DiscountTargetIfc> DiscountTargetList = subgroup.getTargets();

		for (int i = 0; i < DiscountTargetList.size(); i++) {
			((MAXSaleReturnLineItemIfc) (DiscountTargetList.get(i))).setBestDealWinnerName(appliedRuleName);
		}
		if (!sourceAreTargetsFlag) {
			for (int i = 0; i < DiscountSourceList.size(); i++) {
				((MAXSaleReturnLineItemIfc) (DiscountSourceList.get(i))).setBestDealWinnerName(appliedRuleName);
			}

		}
	}

	public void setBestDealRuleNameToLineItemInSuperGroup(SuperGroup spg) {
		List subgroupList = new ArrayList();
		subgroupList = ((SuperGroup) spg).getSubgroups();
		int subgroupListLength = subgroupList.size();
		for (int i = 0; i < subgroupListLength; i++) {
			try {
				MAXBestDealGroup subgroup = (MAXBestDealGroup) subgroupList.get(i);
				setBestDealRuleNameToLineItemInSubGroup(subgroup);
			} catch (Exception e) {
				logger.debug(e);
			}
		}

	}

	public void combineBestDealGroups(ArrayList sources, ArrayList targets) {
		// a list to hold the aggregations of non-competing groups
		ArrayList<Object> list = new ArrayList<Object>();
		// a list to hold the non-competing groups
		ArrayList<BestDealGroupIfc> nonCompeting = new ArrayList<BestDealGroupIfc>();

		// for each of the groups generated by an iteration of calculate best
		// deal
		for (Iterator<BestDealGroupIfc> s = bestDealGroups.iterator(); s.hasNext();) {
			// get the group and create a limits list for it
			BestDealGroupIfc gruppe0 = s.next();
			DiscountListIfc limitsList = DomainGateway.getFactory().getDiscountListInstance();

			// make a copy of the possible sources and targets
			ArrayList<DiscountSourceIfc> srcs = (ArrayList<DiscountSourceIfc>) sources.clone();
			ArrayList<DiscountTargetIfc> tgts = (ArrayList<DiscountTargetIfc>) targets.clone();

			// initialize the limits and update them with the data from the
			// group
			initializeDiscountLimits(limitsList);
			updateDiscountLimits(gruppe0, limitsList);

			// remove this groups sources and targets from the pool
			gruppe0.removeSourcesAndTargets(srcs, tgts);

			// if any sources remain
			if (!srcs.isEmpty()) {
				// add the group to the list of non-competitors
				nonCompeting.add(gruppe0);

				// create a temporary group
				BestDealGroupIfc gruppe1 = null;

				// while additional groups can be generated
				// generate and save the next highest group
				do {
					gruppe1 = getNextBestDealGroup(gruppe0, srcs, tgts, limitsList);
					if (gruppe1 != null) {
						gruppe1.removeSourcesAndTargets(srcs, tgts);
						updateDiscountLimits(gruppe1, limitsList);
						nonCompeting.add(gruppe1);
					}
				} while (gruppe1 != null && !srcs.isEmpty());

				// if any additional groups were created,
				// add them to the list of aggregates
				if (nonCompeting.size() > 1) {
					list.add(nonCompeting.clone());
				}

				// reset nonCompeting for next iteration
				nonCompeting.clear();

			} // end if (!srcs.isEmpty())

		} // end for (Iterator s = bestDealGroups.iterator(); s.hasNext();)

		// for all the aggregations in the list,
		// create SuperGroups and add them to the bucket of potential winners
		for (Iterator<Object> y = list.iterator(); y.hasNext();) {
			SuperGroupIfc g = DomainGateway.getFactory().getSuperGroupInstance();
			g.setSubgroups((ArrayList<BestDealGroupIfc>) y.next());
			addBestDealGroup(g);
		} // end for (Iterator y = list.iterator(); y.hasNext(); )

		// sort potential winners
		Collections.sort(bestDealGroups, Comparators.bestDealDiscountDescending);
	}

	// ---------------------------------------------------------------------
	/**
	 * Determine the next highest best deal group given a pre-existing group.
	 * 
	 * @param sources
	 *            - an ArrayList containing potential sources for a group
	 * @param targets
	 *            - an ArrayList containing potential targets for a group
	 **/
	// ---------------------------------------------------------------------
	public BestDealGroupIfc getNextBestDealGroup(BestDealGroupIfc gruppe, ArrayList srcs, ArrayList tgts,
			DiscountListIfc limits) {
		// create a list of the advanced pricing rules on the transaction
		ArrayList<DiscountRuleIfc> rules = new ArrayList<DiscountRuleIfc>(advancedPricingRules.values());

		// remove the rule that generated gruppe from the list
		for (Iterator<DiscountRuleIfc> i = rules.iterator(); i.hasNext();) {
			if (i.next() == gruppe.getDiscountRule()) {
				i.remove();
				break;
			}
		} // end for (Iterator i = rules.iterator(); i.hasNext(); )

		// create temporary storage variables
		ArrayList<BestDealGroupIfc> sortGroups = new ArrayList<BestDealGroupIfc>(25);
		AdvancedPricingRuleIfc rule = null;
		ArrayList<DiscountSourceIfc> srcs2 = null;
		ArrayList<DiscountTargetIfc> tgts2 = null;
		BestDealGroupIfc nextBest = null;

		// for each remaining rule, generate additional bestDealGroups
		for (Iterator<DiscountRuleIfc> i = rules.iterator(); i.hasNext();) {
			rule = (AdvancedPricingRuleIfc) i.next();

			if (rule.isIncludedInBestDeal()) {
				String ruleID = rule.getRuleID();
				srcs2 = (ArrayList<DiscountSourceIfc>) srcs.clone();
				tgts2 = (ArrayList<DiscountTargetIfc>) tgts.clone();
				BestDealGroupIfc bestGroupForRule = null;

				if (!limits.containsEntry(ruleID)) {
					sortGroups.addAll(rule.generateBestDealGroups(srcs2, tgts2));
				} else {
					int availableApplications = limits.getQuantityRequired(ruleID) - limits.getQuantity(ruleID);

					bestGroupForRule = ((MAXAdvancedPricingRuleIfc) rule).findBestDealGroupForRule(srcs2, tgts2,
							availableApplications);

					if (bestGroupForRule != null) {
						if (nextBest != null) {
							if (BestDealDiscountDescending.getInstance().compare(nextBest, bestGroupForRule) == 1) {
								nextBest = bestGroupForRule;
							}
						} else {
							nextBest = bestGroupForRule;
						}
					}
				}
			} // end if (rule.isIncludedInBestDeal())

		} // end for (Iterator i = rules.iterator();i.hasNext();)

		// return the group with the next highest discount
		return nextBest;
	}

	// ---------------------------------------------------------------------
	/**
	 * Remove a line from the transaction.
	 * <P>
	 * 
	 * @param int index
	 **/
	// ---------------------------------------------------------------------
	public void removeLineItem(int index) {
		// first save count and amounts of line items deleted to be used by
		// reports
		incrementLineVoid(lineItemsVector.get(index));

		SaleReturnLineItemIfc item = (SaleReturnLineItemIfc) lineItemsVector.elementAt(index);

		// Find the PriceAdjustmentLineItemIfc instance for the component so
		// that
		// all related references can be removed
		if (item.isPartOfPriceAdjustment()) {
			int refID = item.getPriceAdjustmentReference();
			PriceAdjustmentLineItemIfc priceAdjLineItem = retrievePriceAdjustmentByReference(refID);

			if (priceAdjLineItem != null) {
				removeLineItem(priceAdjLineItem.getLineNumber());
				return;
			}
		}

		// Remove price adjustment line items and their components
		if (item instanceof PriceAdjustmentLineItemIfc) {
			PriceAdjustmentLineItemIfc priceAdjLineItem = (PriceAdjustmentLineItemIfc) item;
			// remove components and reset the line numbers
			lineItemsVector.removeElementAt(priceAdjLineItem.getPriceAdjustReturnItem().getLineNumber());
			resetLineItemNumbers();
			lineItemsVector.removeElementAt(priceAdjLineItem.getPriceAdjustSaleItem().getLineNumber());
			resetLineItemNumbers();
			lineItemsVector.removeElementAt(priceAdjLineItem.getLineNumber());
			resetLineItemNumbers();
			// recalculate the best deal and update transaction totals
			calculateBestDeal();
			return;
		}

		// if item is a kit header, have it remove its components first
		if (item.isKitHeader()) {
			((KitHeaderLineItemIfc) item).removeKitComponentLineItems(lineItemsVector.iterator());
		}

		// if item is a related item, make sure to remove primary items
		// relationship
		if (item.getRelatedItemSequenceNumber() >= 0) {
			SaleReturnLineItemIfc primaryItem = (SaleReturnLineItemIfc) lineItemsVector.get(item
					.getRelatedItemSequenceNumber());
			SaleReturnLineItemIfc[] relatedItems = primaryItem.getRelatedItemLineItems();
			ArrayList<AbstractTransactionLineItemIfc> relatedItemsList = new ArrayList<AbstractTransactionLineItemIfc>();
			if (relatedItems != null) {
				relatedItemsList.addAll(Arrays.asList(relatedItems));
				for (int i = 0; i < relatedItems.length; i++) {
					if (item.getLineNumber() == relatedItems[i].getLineNumber()) {
						relatedItemsList.remove(i);
						break;
					}
				}
			}
			primaryItem.setRelatedItemLineItems(toItemArray(relatedItemsList));
		}
		// remove element and reset the line numbers
		lineItemsVector.removeElementAt(index);
		if (lineItemsVector != null && lineItemsVector.size() != 0)
			for (int i = 0; i < lineItemsVector.size(); i++) {
				((MAXSaleReturnLineItem) lineItemsVector.get(i)).setBestDealWinnerName(null);
			}

		boolean isDuplicateAdvPricingRule = false;
		if (item.getPLUItem().advancedPricingRules() != null && item.getPLUItem().advancedPricingRules().hasNext()) {
			String itemID = item.getPLUItem().getItemID();
			for (Iterator<AbstractTransactionLineItemIfc> iter = lineItemsVector.iterator(); iter.hasNext();) {
				SaleReturnLineItemIfc element = (SaleReturnLineItemIfc) iter.next();
				String elementItemID = element.getPLUItem().getItemID();
				if (itemID.equals(elementItemID)) {
					isDuplicateAdvPricingRule = true;
				}
			}
		}
		// skip Removing Advance pricing rule if there are any Duplicate Advance
		// pricing rule
		// present in the transaction
		if (!isDuplicateAdvPricingRule) {
			// attempt to remove any of the rules associated with the PLUItem
			// that might impact another item
			for (Iterator<AdvancedPricingRuleIfc> i = item.getPLUItem().advancedPricingRules(); i.hasNext();) {
				// addAdvancedPricingRule() only removes the rule
				// if it is already associated with the transaction
				removeAdvancedPricingRule(i.next());
			}
		}

		resetLineItemNumbers();
		// recalculate the best deal and update transaction totals
		calculateBestDeal();

		// look through the list on more time for price adjustment line items.
		// remove it if it no longer is a better deal.
		for (Iterator<AbstractTransactionLineItemIfc> iter = lineItemsVector.iterator(); iter.hasNext();) {
			SaleReturnLineItemIfc element = (SaleReturnLineItemIfc) iter.next();
			CurrencyIfc zeroCurrency = ZERO;// DomainGateway.
			// getBaseCurrencyInstance();
			if (element.isPriceAdjustmentLineItem() && element.getSellingPrice().compareTo(zeroCurrency) >= 0) {
				removeLineItem(element.getLineNumber());
				return;
			}
		}

	}

	public boolean isTaxApplied() {
		return taxApplied;
	}

	public void setTaxApplied(boolean taxApplied) {
		this.taxApplied = taxApplied;
	}


	public void replaceLineItem(AbstractTransactionLineItemIfc lineItem, int index) {
		// save a reference to the old line item
		SaleReturnLineItemIfc oldItem = (SaleReturnLineItemIfc) lineItemsVector.elementAt(index);
		SaleReturnLineItemIfc newItem = (SaleReturnLineItemIfc) lineItem;

		// newItem.removeAdvancedPricingDiscount();

		SaleReturnLineItem newItemSource = (SaleReturnLineItem) lineItem;

		CurrencyIfc oldPrice = oldItem.getPLUItem().getPrice();
		CurrencyIfc newPrice = newItem.getExtendedDiscountedSellingPrice();
		// oldPrice is zero for an item selected from a receipt for a return,
		// thus existing discounts
		// need to be preserved
		if (oldPrice.compareTo(ZERO) != 0 && !oldPrice.getDecimalValue().abs().equals(newPrice.getDecimalValue().abs())) {
			newItem.removeAdvancedPricingDiscount();
		} else {
			newItemSource.setSourceAvailable(true);
		}

		// replace element
		if (oldItem instanceof MAXSaleReturnLineItemIfc) {
			if (((MAXSaleReturnLineItemIfc) oldItem).getBestDealWinnerName() != null
					&& !(("").equals(((MAXSaleReturnLineItemIfc) oldItem).getBestDealWinnerName()))) {
				((MAXSaleReturnLineItemIfc) newItem).setBestDealWinnerName(((MAXSaleReturnLineItemIfc) oldItem)
						.getBestDealWinnerName());
			}
		}

		newItem.setLineNumber(index);
		lineItemsVector.setElementAt(newItem, index);

		// clear best deals after replacing the lineitem, else newItem isn't
		// cleared properly
		clearBestDealDiscounts();

		// remove kit component line items if necessary
		if (oldItem.isKitHeader()) {
			((KitHeaderLineItemIfc) oldItem).removeKitComponentLineItems(lineItemsVector.iterator());
		}

		// if new item is kit header, add its components
		if (newItem.isKitHeader()) {
			Iterator<?> i = ((KitHeaderLineItemIfc) newItem).getKitComponentLineItems();
			while (i.hasNext()) {
				lineItemsVector.add((KitComponentLineItemIfc) i.next());
			}
		}

		// Remove all related components of the old price adjustment
		if (oldItem.isPriceAdjustmentLineItem()) {
			PriceAdjustmentLineItemIfc priceAdjLineItem = (PriceAdjustmentLineItemIfc) oldItem;
			lineItemsVector.remove(priceAdjLineItem.getPriceAdjustReturnItem());
			lineItemsVector.remove(priceAdjLineItem.getPriceAdjustSaleItem());
		}

		// Add all related components of the new price adjustment
		if (newItem.isPriceAdjustmentLineItem()) {
			PriceAdjustmentLineItemIfc priceAdjLineItem = (PriceAdjustmentLineItemIfc) newItem;

			SaleReturnLineItemIfc returnItem = priceAdjLineItem.getPriceAdjustReturnItem();
			lineItemsVector.add(returnItem);

			SaleReturnLineItemIfc saleItem = priceAdjLineItem.getPriceAdjustSaleItem();
			lineItemsVector.add(saleItem);
		}

		resetLineItemNumbers();
		for (Iterator<AdvancedPricingRuleIfc> i = newItem.getPLUItem().advancedPricingRules(); i.hasNext();) {
			AdvancedPricingRuleIfc rule = i.next();
			// if the rule has pricIngGroupId than it must be calculated only
			// when customer is linked since it is related to pricingGroupID
			if (rule.getPricingGroupID() == -1 || rule.getPricingGroupID() == 0) {
				if (rule.isScopeTransaction()) {
					checkAddTransactionDiscount(rule);
				}
			}
		}
	}

	public void clearEmployeeDiscount() {
		for (int i = 0; i < transactionDiscountsVector.size(); i++) {
			if (((transactionDiscountsVector.get(i)) instanceof TransactionDiscountByPercentageIfc)
					&& (((TransactionDiscountByPercentageIfc) transactionDiscountsVector.get(i)).getDiscountEmployee() != null)
					&& (((TransactionDiscountByPercentageIfc) transactionDiscountsVector.get(i)).getDiscountEmployee()
							.getEmployeeID() != null)) {
				((TransactionDiscountByPercentageIfc) transactionDiscountsVector.get(i))
						.setDiscountRate(new BigDecimal("0.00"));

			}

		}
	}

	public void addAdvancedPricingRule(AdvancedPricingRuleIfc rule) {
		if (rule.isIncludedInBestDeal()) {
			if (!advancedPricingRules.containsKey(rule.getRuleID())) {
				if (Integer.parseInt(rule.getReason().getCode()) == MAXDiscountRuleConstantsIfc.DISCOUNT_REASON_Buy$NatZPctoffTiered)
					advancedPricingRules.clear();
				advancedPricingRules.put(rule.getRuleID(), rule);
			} else // a copy of this rule already exists in the transaction's
					// map.
			{
				if (rule.isStoreCoupon() && !rule.isScopeTransaction()) {
					/*
					 * All rules that are coupon-based and item-scoped have to
					 * have their ReferenceID set. This code ensures that the
					 * rule has that value set in the case it was retrieved
					 * without it.
					 */
					DiscountRuleIfc previouslyCachedRule = advancedPricingRules.get(rule.getRuleID());
					if (previouslyCachedRule.getReferenceID() == null && rule.getReferenceID() != null) {
						previouslyCachedRule.setReferenceID(rule.getReferenceID());
						previouslyCachedRule.setReferenceIDCode(rule.getReferenceIDCode());
					}
				}
			}
		} else if (rule.isScopeTransaction()) {
			checkAddTransactionDiscount(rule);
		}
	}

	// ---------------------------------------------------------------------
	/**
	 * Removes any advanced pricing discounts currently applied to the targets
	 * within the transaction. Calling this method does not have any effect on
	 * the transaction totals.
	 **/
	// ---------------------------------------------------------------------
	public void clearBestDealDiscounts() {
		for (Iterator<BestDealGroupIfc> i = bestDealWinners.iterator(); i.hasNext();) {
			BestDealGroupIfc winner = i.next();
			winner.removeAdvancedPricingDiscounts();

			// Clear the deal information in the targets
			ArrayList<DiscountTargetIfc> targets = winner.getTargets();
			if (targets != null) {
				Iterator<DiscountTargetIfc> it = targets.iterator();

				// make sure the original line items have been updated
				while (it.hasNext()) {
					int lineNo = ((AbstractTransactionLineItemIfc) it.next()).getLineNumber();
					AbstractTransactionLineItemIfc originalLine = retrieveLineItemByID(lineNo);
					// The discounts from retrieved transaction line items are
					// not recalculated; do
					// remove the discount from the line item or mark as an
					// available source.
					if (originalLine != null && !isRetrievedReturnLineItem(originalLine)) {
						((DiscountTargetIfc) originalLine).removeAdvancedPricingDiscount();
						((DiscountSourceIfc) originalLine).setSourceAvailable(true);
					}

				} // end while(i.hasNext())
			} // if (targets != null)

			ArrayList<DiscountSourceIfc> sources = winner.getSources();
			if (sources != null) {
				Iterator<DiscountSourceIfc> it = sources.iterator();

				// make sure the original line items have been updated
				while (it.hasNext()) {
					int lineNo = ((AbstractTransactionLineItemIfc) it.next()).getLineNumber();
					AbstractTransactionLineItemIfc originalLine = retrieveLineItemByID(lineNo);
					// The discounts from retrieved transaction line items
					// cannot serve as a
					// discount source.
					if (originalLine != null && !isRetrievedReturnLineItem(originalLine)) {
						((DiscountTargetIfc) originalLine).removeAdvancedPricingDiscount();
						((DiscountSourceIfc) originalLine).setSourceAvailable(true);
					}

				} // end while(i.hasNext())
			} // if (sources != null)

		} // end for (Iterator i = bestDealWinners.iterator(); i.hasNext(); )
		bestDealWinners.clear();

	}

	@Override
	protected void addLineItemForDiscount(ArrayList list, AbstractTransactionLineItemIfc lineItem,
			boolean nonReceiptReturns) {
		if (lineItem instanceof SaleReturnLineItemIfc) {
			SaleReturnLineItemIfc slri = (SaleReturnLineItemIfc) lineItem;
			// if we want only non-receipted return line items, then add it
			if (nonReceiptReturns) {
				// If this is a non reciepted return, that is 1) the sale return
				// line item is a return, and it not (from
				// a retrieved transaction or a line item for which an original
				// reciept id has been entered).
				if (slri.isReturnLineItem()
						&& !(slri.isFromTransaction() || slri.getReturnItem().isNonRetrievedReceiptedItem())) {
					list.add(lineItem);
				}
			}
			// else, if we don't, only add it if its not a return
			else if (!slri.isReturnLineItem()) {
				AdvancedPricingRuleIfc[] rules = ((MAXPLUItemIfc) slri.getPLUItem()).getAdvancedPricingRules();
				if (rules != null && rules.length > 0 && slri.isUnitOfMeasureItem()) {
					for (int j = 0; j < rules.length; j++) {
						if (rules[j].getSourceThreshold().getDecimalValue().compareTo(slri.getItemQuantityDecimal()) == 1) {
						} else
							list.add(lineItem);
					}
				} else
					list.add(lineItem);
			}
		}
	}

	public void linkCustomer(CustomerIfc value) {
        if (value != null)
        {

            // assigning the value to globally declared customer object which we
            // use it at addPLUItem to get the pricingGroupID
            customer = value;
            // customer has changed. apply or remove customer-specific pricing
            /* Changes for Release 1.2 fix(commenting the below code not to calculate best deal again */
           // revaluateLineItemPriceOnTransaction();

            // clear the customer specific advanced pricing rule
            clearCSPAdvancedPricingRule();
            if (customer.getPricingGroupID() != null)
            {
                constructCSPAdvancedPricingRuleForTransaction();
            }

            DiscountRuleIfc discount = null;
            DiscountRuleIfc[] discountList = value.getFirstGroupsDiscountRules();

            // check for discount group rule
            if (discountList != null && discountList.length > 0)
            {
                discount = discountList[0];
            }

            if (discount != null)
            {
                // boolean to indicate if the customer discount
                // is applied using best deal processing
                boolean applied = false;

                if (discount.isIncludedInBestDeal())
                {
                    // create a pricing rule using attributes from the customer
                    // discount
                    AdvancedPricingRuleIfc rule = DomainGateway.getFactory().getAdvancedPricingRuleInstance();

                    rule.setDiscountMethod(discount.getDiscountMethod());
                    rule.setRuleID(discount.getRuleID());
                    rule.setReason(discount.getReason());
                    rule.setAssignmentBasis(DiscountRuleConstantsIfc.ASSIGNMENT_CUSTOMER);
                    rule.setDiscountScope(discount.getDiscountScope());
                    rule.setDiscountRate(discount.getDiscountRate());
                    rule.setIncludedInBestDeal(discount.isIncludedInBestDeal());
                    rule.activateTransactionDiscount();

                    // check to see if it was applied
                    applied = isBestDealWinner(rule);
                }
                /* Changes for Release 1.2 fix(commenting the below code not to calculate best deal again */
                /*else
                {
                	
                    calculateBestDeal();
                }*/

                // if not a best deal winner, the customer discount needs to be
                // applied to any items that are not discounted by advanced
                // pricing rules
                if (!applied)
                {
                    // create a CustomerDiscount strategy
                    CustomerDiscountByPercentageIfc disc = DomainGateway.getFactory()
                            .getCustomerDiscountByPercentageInstance();
                    disc.initialize(discount.getDiscountRate(), discount.getReason(), discount.getRuleID(),
                            discount.isIncludedInBestDeal());

                    // add it to the transaction discounts
                    //addTransactionDiscount(disc);
                }

            } // end if (discount != null)
            /* Changes for Release 1.2 fix(commenting the below code not to calculate best deal again */
           /* else
            {
                calculateBestDeal();
            }*/
        }
    }

	public boolean itemsSellingPriceExceedsMRP() {
		boolean exceeds = false;
		SaleReturnLineItemIfc item = null;
		Iterator itemIter = getLineItemsIterator();

		while (itemIter.hasNext()) {
			item = (SaleReturnLineItemIfc) itemIter.next();

			if (item.isSaleLineItem()
					&& ((MAXPLUItemIfc) item.getPLUItem()).getRetailLessThanMRPFlag()
					&& (item.getExtendedSellingPrice().divide(item.getItemQuantityDecimal()))
							.compareTo(((MAXPLUItemIfc) item.getPLUItem()).getMaximumRetailPrice()) == CurrencyIfc.GREATER_THAN) {
				exceeds = true;
			}

		}
		return exceeds;
	}

	/*public void replaceLineItemWithoutBestDeal(AbstractTransactionLineItemIfc lineItem, int index) {
		// save a reference to the old line item
		SaleReturnLineItemIfc oldItem = (SaleReturnLineItemIfc) lineItemsVector.elementAt(index);
		SaleReturnLineItemIfc newItem = (SaleReturnLineItemIfc) lineItem;

		// newItem.removeAdvancedPricingDiscount();

		SaleReturnLineItem newItemSource = (SaleReturnLineItem) lineItem;

		CurrencyIfc oldPrice = oldItem.getPLUItem().getPrice();
		CurrencyIfc newPrice = newItem.getExtendedDiscountedSellingPrice();
		// oldPrice is zero for an item selected from a receipt for a return,
		// thus existing discounts
		// need to be preserved
		if (oldPrice.compareTo(ZERO) != 0 && !oldPrice.getDecimalValue().abs().equals(newPrice.getDecimalValue().abs())) {
			newItem.removeAdvancedPricingDiscount();
		} else {
			newItemSource.setSourceAvailable(true);
		}

		// replace element
		if (oldItem instanceof MAXSaleReturnLineItemIfc) {
			if (((MAXSaleReturnLineItemIfc) oldItem).getBestDealWinnerName() != null
					&& !(("").equals(((MAXSaleReturnLineItemIfc) oldItem).getBestDealWinnerName()))) {
				((MAXSaleReturnLineItemIfc) newItem).setBestDealWinnerName(((MAXSaleReturnLineItemIfc) oldItem)
						.getBestDealWinnerName());
			}
		}

		newItem.setLineNumber(index);
		lineItemsVector.setElementAt(newItem, index);

		// clear best deals after replacing the lineitem, else newItem isn't
		// cleared properly
		clearBestDealDiscounts();

		// remove kit component line items if necessary
		if (oldItem.isKitHeader()) {
			((KitHeaderLineItemIfc) oldItem).removeKitComponentLineItems(lineItemsVector.iterator());
		}

		// if new item is kit header, add its components
		if (newItem.isKitHeader()) {
			Iterator<?> i = ((KitHeaderLineItemIfc) newItem).getKitComponentLineItems();
			while (i.hasNext()) {
				lineItemsVector.add((KitComponentLineItemIfc) i.next());
			}
		}

		// Remove all related components of the old price adjustment
		if (oldItem.isPriceAdjustmentLineItem()) {
			PriceAdjustmentLineItemIfc priceAdjLineItem = (PriceAdjustmentLineItemIfc) oldItem;
			lineItemsVector.remove(priceAdjLineItem.getPriceAdjustReturnItem());
			lineItemsVector.remove(priceAdjLineItem.getPriceAdjustSaleItem());
		}

		// Add all related components of the new price adjustment
		if (newItem.isPriceAdjustmentLineItem()) {
			PriceAdjustmentLineItemIfc priceAdjLineItem = (PriceAdjustmentLineItemIfc) newItem;

			SaleReturnLineItemIfc returnItem = priceAdjLineItem.getPriceAdjustReturnItem();
			lineItemsVector.add(returnItem);

			SaleReturnLineItemIfc saleItem = priceAdjLineItem.getPriceAdjustSaleItem();
			lineItemsVector.add(saleItem);
		}

		resetLineItemNumbers();
		for (Iterator<AdvancedPricingRuleIfc> i = newItem.getPLUItem().advancedPricingRules(); i.hasNext();) {
			AdvancedPricingRuleIfc rule = i.next();
			// if the rule has pricIngGroupId than it must be calculated only
			// when customer is linked since it is related to pricingGroupID
			if (rule.getPricingGroupID() == -1 || rule.getPricingGroupID() == 0) {
				if (rule.isScopeTransaction()) {
					checkAddTransactionDiscount(rule);
				}
			}
		}
	}*/
	//changes for Tax starts
	public ItemTaxIfc initializeItemTax(PLUItemIfc pluItem) {
		// construct item tax object based on transaction values
		MAXItemTaxIfc it = (MAXItemTaxIfc) DomainGateway.getFactory().getItemTaxInstance();
		MAXTaxAssignmentIfc[] taxAssignments = null;
		if (pluItem instanceof MAXPLUItemIfc && ((MAXPLUItemIfc) pluItem).getTaxAssignments() != null) {
			taxAssignments = ((MAXPLUItemIfc) pluItem).getTaxAssignments();
		} else if (pluItem instanceof MAXGiftCardPLUItem
				&& ((MAXGiftCardPLUItem) pluItem).getTaxAssignments() != null) {
			taxAssignments = ((MAXGiftCardPLUItem) pluItem).getTaxAssignments();

		} else {
			/*
			 * If there are Zero TaxAssignments for a given Tax Category then
			 * the Item is non Taxable i.e TaxAmountFactor is Zero. so create a
			 * dummy taxAssignment object.
			 */
			taxAssignments = new MAXTaxAssignment[1];
			taxAssignments[0] = new MAXTaxAssignment();
			taxAssignments[0].setTaxAmountFactor(Util.I_BIG_DECIMAL_ZERO);
			taxAssignments[0].setTaxableAmountFactor(Util.I_BIG_DECIMAL_ZERO);
			//Changes for Rev 1.1 : Starts
			if (pluItem instanceof MAXPLUItem) {
				taxAssignments[0].setTaxCategory(((MAXPLUItem) pluItem).getTaxCategory());
			} else if (pluItem instanceof MAXGiftCardPLUItem) {
				taxAssignments[0].setTaxCategory(((MAXGiftCardPLUItem) pluItem).getTaxCategory());
			}
			//Changes for Rev 1.1 : Ends
			taxAssignments[0].setTaxCode(Util.EMPTY_STRING);
			taxAssignments[0].setTaxCodeDescription(Util.EMPTY_STRING);
			taxAssignments[0].setTaxRate(Util.I_BIG_DECIMAL_ZERO);
		}

		/*
		 * Each item has a TaxCategory associated with it and each tax category
		 * will have multiple tax assignments asssociated with it.
		 * TaxBreakupDetails are calculated based on the TaxAssignments.
		 */
		MAXLineItemTaxBreakUpDetailIfc[] lineItemTaxBreakUpDetails = new MAXLineItemTaxBreakUpDetail[taxAssignments.length];
		for (int i = 0; i < taxAssignments.length; i++) {
			lineItemTaxBreakUpDetails[i] = new MAXLineItemTaxBreakUpDetail();
			lineItemTaxBreakUpDetails[i].setTaxAssignment(taxAssignments[i]);
			lineItemTaxBreakUpDetails[i].setTaxableAmount(DomainGateway.getBaseCurrencyInstance(((MAXTaxAssignment)taxAssignments[0]).getTaxableAmountFactor()));
			lineItemTaxBreakUpDetails[i].setTaxAmount(DomainGateway.getBaseCurrencyInstance(((MAXTaxAssignment)taxAssignments[0]).getTaxAmountFactor()));
			lineItemTaxBreakUpDetails[i].setSaleReturnTaxFlag(1);
		}

		it.setLineItemTaxBreakUpDetail(lineItemTaxBreakUpDetails);
		it.setTaxable(pluItem.getTaxable());

		return (it);
	}
	//changes for Tax Ends
	
	// Changes for Rev 1.3 : Starts
	public void splitSourcesAndTargets(boolean forTransactionLevelRules) {
		SaleReturnLineItemIfc srli = null;
		// test each item for discount potential and split if quantity is > 1
		for (Iterator i = ((Vector) lineItemsVector.clone()).iterator(); i.hasNext();) {
			srli = (SaleReturnLineItemIfc) i.next();
			if (!forTransactionLevelRules) {
				if ((((MAXSaleReturnLineItemIfc) srli).getBestDealWinnerName() != null
						|| srli.getPLUItem().getAdvancedPricingRules().length != 0 || isPotentialTarget((DiscountTargetIfc) srli))
						&& srli.getItemQuantity().intValue() > 1 && !srli.isUnitOfMeasureItem()) {
					splitLineItem(srli);
				}
			} else {
				if (isPotentialSourceForTransactionRule((DiscountSourceIfc) srli)
						&& ((srli.getItemQuantityDecimal().intValue() > 1 || srli.getItemQuantityDecimal().intValue() < -1) && (!srli
								.isUnitOfMeasureItem()))) {
					splitLineItem(srli);
				}
			}
		}
	}

	public void revaluateLineItemPrice(SaleReturnLineItemIfc srli, PLUItemIfc pluItem) {
		if (srli.getItemPrice().getItemPriceOverrideReason().getCode().equals(CodeConstantsIfc.CODE_UNDEFINED)) {
			EYSDate when = new EYSDate();
			int prcGrpId = -1;
			if (customer != null && customer.getPricingGroupID() != null) {
				prcGrpId = customer.getPricingGroupID();
			}

			CurrencyIfc customerPrice = pluItem.getPrice(when, prcGrpId);
			CurrencyIfc defaultSellingPrice = pluItem.getPrice();
			if (customerPrice.compareTo(defaultSellingPrice) == CurrencyIfc.LESS_THAN) {
				if (!pluItem.getItemClassification().isPriceEntryRequired()) {
					srli.getItemPrice().setSellingPrice(customerPrice);
					PriceChangeIfc promotionalPriceChange = srli.getPLUItem().getEffectivePromotionalPrice(when,
							prcGrpId);
					srli.getItemPrice().setAppliedPromotion(promotionalPriceChange);
				}
			} else {
				if (!pluItem.getItemClassification().isPriceEntryRequired()) {
					/*if (srli.getPLUItem().getAdvancedPricingRules().length == 0)
						srli.getItemPrice().setSellingPrice(defaultSellingPrice);*/
					PriceChangeIfc promotionalPriceChange = srli.getPLUItem().getEffectivePromotionalPrice(when);
					srli.getItemPrice().setAppliedPromotion(promotionalPriceChange);
				}
			}
		}
	}
	// Changes for Rev 1.3 : Ends
}
