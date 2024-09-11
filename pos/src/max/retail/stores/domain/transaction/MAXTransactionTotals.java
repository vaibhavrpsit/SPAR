/********************************************************************************
 *   
 *	Copyright (c) 2016 - 2017 MAX Hypermarket, Inc    All Rights Reserved.
 *
 *  Rev 1.3  	hitesh dua   			22 June,2017 		Item grouping for promo items.
 * 
 *	Rev 1.2		Apr 26, 2017			Mansi Goel			Changes to resolve discount amount is coming as 0.00 in second item
 *															of send transaction for source based rules 
 *  Rev 1.1     Nov 08, 2016            Ashish Yadav		Home Delivery Send FES
 *  Rev 1.0     Nov 08, 2016	        Nadia Arora			MAX-StoreCredi_Return requirement.
 *
 ********************************************************************************/

package max.retail.stores.domain.transaction;

import java.math.BigDecimal;
import java.util.Enumeration;
import java.util.List;
import java.util.Vector;

import max.retail.stores.domain.tax.MAXInternalTaxEngine;
import oracle.retail.stores.commerceservices.common.currency.CurrencyIfc;
import oracle.retail.stores.domain.DomainGateway;
import oracle.retail.stores.domain.discount.TransactionDiscountStrategyIfc;
import oracle.retail.stores.domain.financial.ShippingMethodIfc;
import oracle.retail.stores.domain.lineitem.AbstractTransactionLineItemIfc;
import oracle.retail.stores.domain.lineitem.ItemPriceIfc;
import oracle.retail.stores.domain.lineitem.SaleReturnLineItemIfc;
import oracle.retail.stores.domain.lineitem.SendPackageLineItemIfc;
import oracle.retail.stores.domain.lineitem.TaxLineItemInformationIfc;
import oracle.retail.stores.domain.stock.AlterationPLUItemIfc;
import oracle.retail.stores.domain.stock.GiftCardPLUItemIfc;
import oracle.retail.stores.domain.tax.TaxConstantsIfc;
import oracle.retail.stores.domain.transaction.TransactionTaxIfc;
import oracle.retail.stores.domain.transaction.TransactionTotals;
import oracle.retail.stores.domain.utility.GiftCardIfc;

public class MAXTransactionTotals extends TransactionTotals implements MAXTransactionTotalsIfc {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	protected Vector sendPackages;
	protected ShippingMethodIfc shippingMethod;
	protected CurrencyIfc offTotal;

	/**
	 * String containing rounding parameters
	 */

	protected String rounding;
	// Changes start for Rev 1.1 (Ashish : Send)
	protected boolean transactionLevelSendAssigned = false;
	// Changes start for Rev 1.1 (Ashish : Send)

	/**
	 * List containing the rounding denominations.The list is expected to be
	 * sorted when set.
	 */
	protected List roundingDenominations;

	protected BigDecimal LOWER_BOUND = new BigDecimal(0.0);
	protected BigDecimal UPPER_BOUND = new BigDecimal(1.0);

	protected BigDecimal vatExtraTaxTotal = new BigDecimal(0.00);

	public MAXTransactionTotals() {
		super();
	}

	public MAXTransactionTotals(CurrencyIfc saleSub, CurrencyIfc returnSub, CurrencyIfc saleDisc,
			CurrencyIfc saleDiscAndProm, CurrencyIfc returnDisc, CurrencyIfc tax, CurrencyIfc inclusiveTax,
			CurrencyIfc grand, CurrencyIfc tendered, CurrencyIfc due) {
		super(saleSub, returnSub, saleDisc, saleDiscAndProm, returnDisc, tax, inclusiveTax, grand, tendered, due);
	}

	public Object clone() {
		// create new object
		MAXTransactionTotals t = new MAXTransactionTotals();

		// set clone attributes
		setCloneAttributes(t);

		// pass back object
		return t;

	}

	public void modifySendPackage(int index, Object element) {
		sendPackages.set(index, element);
	}

	public CurrencyIfc getCalculatedShippingCharge() {
		CurrencyIfc calculatedShippingCharge = DomainGateway.getBaseCurrencyInstance();
		for (int i = 0; i < getItemSendPackagesCount(); i++) {
			if (getSendPackages()[i] != null) {
				ShippingMethodIfc shippingMethod = (ShippingMethodIfc) getSendPackages()[i].getShippingMethod();
				if (shippingMethod != null) {
					calculatedShippingCharge = calculatedShippingCharge.add(shippingMethod
							.getCalculatedShippingCharge());
				}
			}
		}
		return (calculatedShippingCharge);
	}

	// Changes start for Rev 1.1 (Ashish : send)

	public boolean isTransactionLevelSendAssigned() {
		return false;
	}

	// Changes start for Rev 1.1 (Ashish : send)
	// Changes start for Rev 1.1 (Send)
	// Changes for Rev 1.2 : Starts
	public SendPackageLineItemIfc[] getSendPackages() {
		Vector<Object> sendPackageVector = getSendPackageVector();
		//changes for rev 1.3 
		if(sendPackageVector==null)
			return null;
		
		SendPackageLineItemIfc[] sendPackages = sendPackageVector.toArray(new SendPackageLineItemIfc[0]);

		return sendPackages;
	}

	public Vector getSendPackageVector() {
		return sendPackages;
	}

	public int getItemSendPackagesCount() {
		return 0;
	}

	// Changes end for Rev 1.1 (Send)

	public void addSendPackage(SendPackageLineItemIfc sendPackageUsed) {
		sendPackages = new Vector();
		sendPackages.add(sendPackageUsed);
	}

	// Changes for Rev 1.2 : Ends
	// ----------------------------------------------------------------------------
	/**
	 * Retrieves the shipping method.
	 * <P>
	 * 
	 * @return shipping method
	 * @deprecated As of release 7.0, use setCalculatedShippingCharge from
	 *             ShippingMethod
	 **/
	// ----------------------------------------------------------------------------
	public ShippingMethodIfc getShippingMethod() { // begin getShippingMethod()
		return shippingMethod;
	} // end getShippingMethod()

	// ----------------------------------------------------------------------------
	/**
	 * Sets shipping method
	 * <P>
	 * 
	 * @param value
	 *            shipping charge
	 * @deprecated As of release 7.0, use setCalculatedShippingCharge from
	 *             ShippingMethod
	 **/
	// ----------------------------------------------------------------------------
	public void setShippingMethod(ShippingMethodIfc value) { // begin
																// setShippingMethod()
		shippingMethod = value;
	} // end setShippingMethod()

	public CurrencyIfc getOffTotal() {
		return offTotal;
	}

	public void setOffTotal(CurrencyIfc offTotal) {
		this.offTotal = offTotal;
	}

	public void updateTransactionTotals(AbstractTransactionLineItemIfc[] lineItems,
			TransactionDiscountStrategyIfc[] discounts, TransactionTaxIfc tax) {
		// remove non-totalable line items
		Vector<AbstractTransactionLineItemIfc> totalableLineItems = new Vector<AbstractTransactionLineItemIfc>();

		// walk current lineItems, add non-canceled and not-price adjustment
		// ones to totalableLineItems
		for (AbstractTransactionLineItemIfc lineItem : lineItems) {
			if (lineItem.isTotalable()) {
				totalableLineItems.add(lineItem);
			}
		}

		// reset new lineItems size
		numItems = totalableLineItems.size();

		// initialize values
		subtotal.setZero();
		saleSubtotal.setZero();
		returnSubtotal.setZero();
		discountTotal.setZero();
		saleDiscountTotal.setZero();
		saleDiscountAndPromotionTotal.setZero();
		transactionDiscountTotal.setZero();
		itemDiscountTotal.setZero();
		restockingFeeTotal.setZero();
		returnDiscountTotal.setZero();
		taxTotal.setZero();
		inclusiveTaxTotal.setZero();
		taxTotalUI.setZero();
		taxExceptionsTotal.setZero();
		quantityTotal = BigDecimal.ZERO;
		quantitySale = BigDecimal.ZERO;
		taxInformationContainer.reset();
		shippingChargeTotal.setZero();

		// calculate sub totals
		calculateSubtotals(totalableLineItems);

		// set grand total
		calculateGrandTotal();

		if (discounts != null) {
			if (discounts.length > 0) {
				// calculate discounts
				discountCalculator.calculateDiscounts(this, discounts, totalableLineItems);
			}
		}

		// add the discount total to the promotions
		amountOffTotal = amountOffTotal.add(discountTotal);

		// update taxes if not tax exempt
		if (tax != null) {
			// update taxes
			// calculateTaxes(totalableLineItems, tax);
			if (tax.getTaxMode() == TaxConstantsIfc.TAX_MODE_EXEMPT) {
				Vector<AbstractTransactionLineItemIfc> exemptLineItems = new Vector<AbstractTransactionLineItemIfc>();
				Vector<AbstractTransactionLineItemIfc> nonExemptLineItems = new Vector<AbstractTransactionLineItemIfc>();
				for (AbstractTransactionLineItemIfc lineItem : totalableLineItems) {
					if (lineItem.canTransactionExemptTaxRules()) {
						exemptLineItems.add(lineItem);
					} else {
						nonExemptLineItems.add(lineItem);
					}
				}

				if (exemptLineItems.size() > 0) {
					computeExemptTaxes(exemptLineItems, tax);
				}
				if (nonExemptLineItems.size() > 0) {
					CurrencyIfc exemptTaxTotal = getExemptTaxTotal();
					if (exemptTaxTotal != null) {
						exemptTaxTotal = (CurrencyIfc) exemptTaxTotal.clone();
					}

					// For line items whose tax cannot be exempted (receipted
					// return or pickup without reprice),
					// calculate their tax as usual
					computeTaxes(nonExemptLineItems, tax);

					// computeTaxes reset the container. set the
					// exemptedTaxTotal back.
					if (exemptTaxTotal != null) {
						getTaxInformationContainer().addTaxExemptInformation(exemptTaxTotal);
						setExemptTaxTotal(exemptTaxTotal);
					}
				}
			} else {
				computeTaxes(totalableLineItems, tax);
			}
		} else {
			throw new NullPointerException("Null TransactionTax in TransactionTotals.updateTransactionTotals()");
		}

		// reset grand total
		calculateGrandTotal();

		/* India Localization -Tax Changes Starts Here */
		// From GrandTotal Subtract the total tax Amount since for India
		// Localization, Selling Retail is always inclusive of Tax.

		grandTotal = grandTotal.subtract(taxTotal);

		/* India Localization -Tax Changes ends Here */

		// set balance due to grand total
		balanceDue.setStringValue(grandTotal.getStringValue());
		// subtract amount tendered (calculated elsewhere)
		balanceDue = grandTotal.subtract(amountTender);
	}

	protected void calculateSubtotals(Vector<AbstractTransactionLineItemIfc> lineItems) {
		// local reference to line item
		SaleReturnLineItemIfc li = null;
		CurrencyIfc extendedSellingPrice = DomainGateway.getBaseCurrencyInstance();
		CurrencyIfc itemDiscountAmount = DomainGateway.getBaseCurrencyInstance();
		Enumeration<AbstractTransactionLineItemIfc> e = lineItems.elements();
		discountEligibleSubtotal.setZero();
		amountOffTotal.setZero();
		// initially assume that all items have unit of measure as units
		setAllItemUOMUnits(true);
		// loop through line items , capturing sale, return subtotals and item
		// discounts
		while (e.hasMoreElements()) {
			li = (SaleReturnLineItemIfc) e.nextElement();
			if (li.isUnitOfMeasureItem()) {
				setAllItemUOMUnits(false);
			} else if (li.isShippingCharge()) {
				shippingChargeTotal = shippingChargeTotal.add(li.getExtendedDiscountedSellingPrice());
			}

			if (!li.isKitHeader() && !li.isPriceAdjustmentLineItem()) {
				BigDecimal itemQuantity = li.getItemQuantityDecimal();

				extendedSellingPrice = li.getExtendedSellingPrice();
				itemDiscountAmount = li.getItemDiscountAmount();
				subtotal = subtotal.add(extendedSellingPrice);
				discountTotal = discountTotal.add(itemDiscountAmount);
				boolean incrementQuantity = true;
				// check if sale or returned item
				if (itemQuantity.signum() > 0) {
					if (li.getPLUItem() != null && li.getPLUItem().hasTemporaryPriceChanges()) {
						// get the permanent price and mutliply it by the
						// quantity.
						CurrencyIfc extendedPermanentPrice = li.getItemPrice().getPermanentSellingPrice()
								.multiply(li.getItemQuantityDecimal()).abs();
						// subtract the selling price from the full permanent
						// price
						amountOffTotal = amountOffTotal
								.add(extendedPermanentPrice.subtract(extendedSellingPrice.abs()));
						saleDiscountAndPromotionTotal = saleDiscountAndPromotionTotal.add(extendedPermanentPrice
								.subtract(extendedSellingPrice.abs()));
					}

					saleSubtotal = saleSubtotal.add(extendedSellingPrice);
					saleDiscountTotal = saleDiscountTotal.add(itemDiscountAmount);
					saleDiscountAndPromotionTotal = saleDiscountAndPromotionTotal.add(itemDiscountAmount);

					// reset item transaction discount
					li.clearTransactionDiscounts();
					li.recalculateItemTotal();

					// bump up item quantity
					if (incrementQuantity && li.isServiceItem()) {
						incrementQuantity = this.isNonMerchandiseQuantityIncremented();
					}
					// gift card reload will not count as
					// units sold
					// Alterations will not count as units sold
					if (li.getPLUItem() instanceof GiftCardPLUItemIfc) {
						incrementQuantity = true;
						GiftCardIfc giftCard = ((GiftCardPLUItemIfc) li.getPLUItem()).getGiftCard();
						/*
						 * if (giftCard.getRequestType() ==
						 * GiftCardIfc.GIFT_CARD_RELOAD) { incrementQuantity =
						 * false; }
						 */
					}
					if (li.getPLUItem() instanceof AlterationPLUItemIfc) {
						incrementQuantity = false;
					}
					if (incrementQuantity) {
						quantityTotal = quantityTotal.add(itemQuantity);
						if (!li.getPLUItem().isStoreCoupon()) {
							quantitySale = quantitySale.add(itemQuantity);
						}
					}

					// if item eligible for discounts, save total
					if (li.isDiscountEligible()) {
						discountEligibleSubtotal = discountEligibleSubtotal.add(extendedSellingPrice);
						discountEligibleSubtotal = discountEligibleSubtotal.subtract(itemDiscountAmount);
					}

				}
				// handle returned item
				else {
					returnSubtotal = returnSubtotal.add(extendedSellingPrice);
					returnDiscountTotal = returnDiscountTotal.add(itemDiscountAmount);

					// get item quantity totaled, but use absolute value
					// Non Merchandise Items could be returned, hence
					// checking
					if (li.isServiceItem() && !this.isNonMerchandiseQuantityIncremented()) {
						incrementQuantity = false;
					}
					if (incrementQuantity) {
						/* India Localization Changes- Starts Here */
						/*
						 * This is a base bug. During returns the itemQuantity
						 * is negative and should be added instead of
						 * subtracting i.e X+(-Y)=X-Y instead of X-(-Y)=X+Y
						 */
						// quantityTotal =
						// quantityTotal.subtract(itemQuantity);
						quantityTotal = quantityTotal.add(itemQuantity);
						/* India Localization Changes- Ends Here */
					}
				}

				// reset item discount total
				li.setItemDiscountTotal(itemDiscountAmount);

				// roll up item discounts
				itemDiscountTotal = itemDiscountTotal.add(itemDiscountAmount);

				// if it is a returned item calculate the restocking fee total
				if (itemQuantity.signum() < 0) {
					ItemPriceIfc itemPrice = li.getItemPrice();
					if (itemPrice != null) {
						CurrencyIfc itemExtendedRestockingFee = itemPrice.getExtendedRestockingFee();
						if (itemExtendedRestockingFee != null) {
							restockingFeeTotal = restockingFeeTotal.add(itemExtendedRestockingFee);
						}
					}
				}
			}
		}

	}

	protected void computeTaxes(Vector<AbstractTransactionLineItemIfc> lineItems, TransactionTaxIfc tax) {
		TaxLineItemInformationIfc[] items = lineItems.toArray(new TaxLineItemInformationIfc[lineItems.size()]);
		/* India Localization- Tax related changes starts here */
		// taxEngine.calculateTax(items, this,tax);
		/* Transaction Tax will not be used for the calculating the tax */
		MAXInternalTaxEngine taxEngine1 = new MAXInternalTaxEngine();
		taxEngine1.calculateTax(items, this);
		/* India Localization- Tax related changes ends here */
	}

	public CurrencyIfc adjustRounding(CurrencyIfc amount, int scale) {
		BigDecimal bd = amount.getDecimalValue();
		BigDecimal bOne = BigDecimal.ONE;

		// Need to do rounding in two steps, starting from the 3rd decimal digit
		// first,
		// then round again at the 2nd decimal digit.
		bd = bd.divide(bOne, 3, BigDecimal.ROUND_HALF_UP);
		CurrencyIfc roundedCurrency = DomainGateway.getBaseCurrencyInstance(bd);

		BigDecimal bd2 = roundedCurrency.getDecimalValue();
		bd2 = bd2.divide(bOne, scale, BigDecimal.ROUND_HALF_UP);

		// code for India Localization Price rounding
		if (rounding != null)
			bd2 = adjustRounding(bd2);

		roundedCurrency = DomainGateway.getBaseCurrencyInstance(bd2);
		return (roundedCurrency);
	}

	/**
	 * CODE for India localization price rounding Method to calculate the
	 * rounded part based on the rounding parameters.
	 * 
	 * @param BigDecimal
	 *            oldValue - the bigDecimal value whose dec part has to be
	 *            rounded
	 * @return BigDecimal rounded value according to the set parameters.
	 */
	private BigDecimal adjustRounding(BigDecimal bd2) {
		BigDecimal integerPart = new BigDecimal(bd2.toBigInteger());
		BigDecimal fractionPart = bd2.subtract(integerPart);

		BigDecimal roundedFraction = null;

		if ("roundingUp".equalsIgnoreCase(rounding)) {
			for (int i = 0; i < roundingDenominations.size(); i++) {
				if ((fractionPart.compareTo((BigDecimal) roundingDenominations.get(i))) <= 0) {
					roundedFraction = (BigDecimal) roundingDenominations.get(i);
					break;
				}

			}
		} else if ("roundingDown".equalsIgnoreCase(rounding)) {
			// roundingDenominations.add(0,LOWER_BOUND);
			// roundingDenominations.add(roundingDenominations.size(),UPPER_BOUND);
			for (int i = 0; i < roundingDenominations.size(); i++) {

				if ((fractionPart.compareTo((BigDecimal) roundingDenominations.get(i))) >= 0)
					continue;

				if (i > 0) {
					roundedFraction = (BigDecimal) roundingDenominations.get(i - 1);
					break;
				}
			}
		} else if ("roundingNearest".equalsIgnoreCase(rounding)) {

			BigDecimal lowerBound = LOWER_BOUND;
			BigDecimal upperBound = UPPER_BOUND;
			for (int i = 0; i < roundingDenominations.size(); i++) {

				if (fractionPart.compareTo((BigDecimal) roundingDenominations.get(i)) < 0) {
					if (i > 0) {
						lowerBound = lowerBound.compareTo(LOWER_BOUND) == 0.0 ? LOWER_BOUND
								: (BigDecimal) roundingDenominations.get(i - 1);
					} else {
						lowerBound = LOWER_BOUND;
					}
					upperBound = (BigDecimal) roundingDenominations.get(i);
					break;
				}

				lowerBound = (BigDecimal) roundingDenominations.get(i);
			}

			roundedFraction = Double.compare(Math.abs((upperBound.subtract(fractionPart)).doubleValue()),
					Math.abs((lowerBound.subtract(fractionPart)).doubleValue())) > 0 ? lowerBound : upperBound;
		}

		else if ("noRounding".equalsIgnoreCase(rounding)) {

			roundedFraction = fractionPart;
		}

		return integerPart.add(roundedFraction);
	}

	public BigDecimal getVatExtraTaxTotal() {
		return vatExtraTaxTotal;
	}

	/**
	 * @param vatExtraTaxTotal
	 *            the vatExtraTaxTotal to set
	 */
	public void setVatExtraTaxTotal(BigDecimal vatExtraTaxTotal) {
		this.vatExtraTaxTotal = vatExtraTaxTotal;
	}
}
