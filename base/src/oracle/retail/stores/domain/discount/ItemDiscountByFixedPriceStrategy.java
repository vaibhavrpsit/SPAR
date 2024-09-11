/* ===========================================================================
* Copyright (c) 2008, 2013, Oracle and/or its affiliates. All rights reserved.
 * ===========================================================================
 * $Header: rgbustores/modules/domain/src/oracle/retail/stores/domain/discount/ItemDiscountByFixedPriceStrategy.java /main/18 2013/12/17 16:08:05 mjwallac Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    mjwallac  12/17/13 - fix misused calls to Boolean.getBoolean()
 *    sgu       08/17/12 - fix discount rules
 *    sgu       08/14/12 - add ItemDiscountAudit
 *    blarsen   02/22/12 - XbranchMerge
 *                         blarsen_bug13714601-order-pickup-stuck-in-tender-options
 *                         from rgbustores_13.4x_generic_branch
 *    blarsen   02/22/12 - Adding overloaded calculateItemDiscount() to comply
 *                         with changes to the interface. New method does not
 *                         use the additional param to method.
 *    blarsen   12/22/10 - Added isEmployeeDiscount() method.
 *    cgreene   05/26/10 - convert to oracle packaging
 *    abondala  01/03/10 - update header date
 *    cgreene   04/14/09 - convert pricingGroupID to integer instead of string
 *    npoola    11/30/08 - CSP POS and BO changes
 *    acadar    11/03/08 - localization of reason codes for discounts and
 *                         merging to tip
 *
 * ===========================================================================
 * $Log:
 * $ 6    360Commerce 1.5         8/10/2007 12:26:15 PM  Christian Greene 28283
 * $       - Added PromoIDs
 * $ 5    360Commerce 1.4         5/15/2007 5:53:46 PM   Maisa De Camargo Added
 * $       PromotionId, PromotionComponentId and PromotionComponentDetailId
 * $ 4    360Commerce 1.3         4/25/2007 10:01:02 AM  Anda D. Cadar   I18N
 * $      merge
 * $ 3    360Commerce 1.2         3/31/2005 4:28:30 PM   Robert Pearse
 * $ 2    360Commerce 1.1         3/10/2005 10:22:25 AM  Robert Pearse
 * $ 1    360Commerce 1.0         2/11/2005 12:11:36 PM  Robert Pearse
 * $$$
 * ===========================================================================
 */
package oracle.retail.stores.domain.discount;

import java.math.BigDecimal;

import oracle.retail.stores.commerceservices.common.currency.CurrencyIfc;
import oracle.retail.stores.common.utility.LocalizedCodeIfc;
import oracle.retail.stores.domain.DomainGateway;
import oracle.retail.stores.domain.employee.EmployeeIfc;
import oracle.retail.stores.foundation.utility.Util;
import oracle.retail.stores.foundation.utility.xml.XMLConversionException;
import oracle.retail.stores.foundation.utility.xml.XMLConverterIfc;

import org.w3c.dom.Element;

/**
 * Discount by fixed price strategy.
 *
 * @see oracle.retail.stores.domain.discount.ItemDiscountByAmountIfc
 * @see oracle.retail.stores.domain.discount.DiscountRule
 * @version $Revision: /main/18 $
 */
public class ItemDiscountByFixedPriceStrategy extends AbstractItemDiscountStrategy
    implements ItemDiscountByAmountIfc
{
    private static final long serialVersionUID = -8783682316217573473L;

    /**
     * revision number supplied by source-code control system
     */
    public static final String revisionNumber = "$Revision: /main/18 $";

    private int promotionId;
    private int promotionComponentId;
    private int promotionComponentDetailId;
    private int pricingGroupID;
    /**
     * Constructs ItemDiscountByFixedPriceStrategy object.
     */
    public ItemDiscountByFixedPriceStrategy()
    {

    }

    /**
     * Constructs ItemDiscountByFixedPriceStrategy object, setting amount and
     * reason code attributes.
     *
     * @param amount discount amount
     * @param reason code
     * @deprecated as of 13.1. Use {@link ItemDiscountByFixedPriceStrategy(CurrencyIfc discountAmount, LocalizedCodeIfc reason)}
     */
    public ItemDiscountByFixedPriceStrategy(CurrencyIfc discountAmount, int reasonCode)
    {
        this.discountAmount = discountAmount;
        this.reasonCode = reasonCode;
    }

    /**
     * Constructs ItemDiscountByFixedPriceStrategy object, setting amount and
     * reason code attributes.
     * @param amount discount amount
     * @param reason code
     */
    public ItemDiscountByFixedPriceStrategy(CurrencyIfc discountAmount, LocalizedCodeIfc reason)
    {
        this.discountAmount = discountAmount;
        this.reason = reason;
    }

    /**
     * Constructs ItemDiscountByFixedPriceStrategy object, setting amount,
     * reason code and ruleID attributes.
     * @param amount discount amount
     * @param reason code
     * @param ruleID
     * @deprecated as of 13.1. Use {@link ItemDiscountByFixedPriceStrategy(CurrencyIfc discountAmount, int reasonCode, String ruleID)}
     */
    public ItemDiscountByFixedPriceStrategy(CurrencyIfc discountAmount, int reasonCode, String ruleID)
    {
        this.discountAmount = discountAmount;
        this.reasonCode = reasonCode;
        this.ruleID = ruleID;
    }

    /**
     * Constructs ItemDiscountByFixedPriceStrategy object, setting amount,
     * reason code and ruleID attributes.
     * @param amount discount amount
     * @param reason code
     * @param ruleID
    */
    public ItemDiscountByFixedPriceStrategy(CurrencyIfc discountAmount, LocalizedCodeIfc reason, String ruleID)
    {
        this.discountAmount = discountAmount;
        this.reason = reason;
        this.ruleID = ruleID;
    }

    /* (non-Javadoc)
     * @see oracle.retail.stores.domain.discount.ItemDiscountStrategyIfc#calculateItemDiscount(oracle.retail.stores.commerceservices.common.currency.CurrencyIfc, java.math.BigDecimal)
     */
    public CurrencyIfc calculateItemDiscount(CurrencyIfc itemPrice, BigDecimal itemQuantity)
    {
        // If itemPrice is negative, discount amount returned is negative
        discountAmount = discountAmount.abs();
        if (itemPrice.signum() < 0)
        {
            discountAmount = discountAmount.negate();
        }

        CurrencyIfc itemDiscount = discountAmount.multiply(itemQuantity.abs());
        return(itemDiscount);
    }

    /* (non-Javadoc)
     * @see oracle.retail.stores.domain.discount.ItemDiscountStrategyIfc#calculateItemDiscount(oracle.retail.stores.commerceservices.common.currency.CurrencyIfc)
     */
    public CurrencyIfc calculateItemDiscount(CurrencyIfc itemPrice)
    {
        // If itemPrice is negative, discount amount returned is negative
        discountAmount = discountAmount.abs();
        if (itemPrice.signum() < 0)
        {
            discountAmount = discountAmount.negate();
        }
        return (discountAmount);
    }

    /**
     * Clone this object.
     *
     * @return generic object copy of this object
     */
    public Object clone()
    {
        ItemDiscountByFixedPriceStrategy newClass = new ItemDiscountByFixedPriceStrategy();
        setCloneAttributes(newClass);
        return newClass;
    }

    /**
     * Determine whether the provided object is the same type and has the same
     * field values as this one.
     *
     * @param obj the object to compare
     * @return true if the fields are equal; false otherwise
     */
    public boolean equals(Object obj)
    {
        return (obj instanceof ItemDiscountByFixedPriceStrategy) ? super.equals(obj) : false;
    }

    /**
     * Retrieves employee discount employee.
     *
     * @return employee discount employee
     */
    public EmployeeIfc getDiscountEmployee()
    {
        return null;
    }

    /**
     * Returns identifier for employee discount employee
     *
     * @return identifier for employee discount employee
     */
    public String getDiscountEmployeeID()
    {
        return "";
    }

    /**
     * Retrieves discount method.
     *
     * @return method discount method
     */
    public int getDiscountMethod()
    {
        return DISCOUNT_METHOD_FIXED_PRICE;
    }

    /**
     * Retrieves discount scope.
     *
     * @return scope discount scope
     */
    public int getDiscountScope()
    {
        return DISCOUNT_SCOPE_ITEM;
    }

    /**
     * Get the Promotion Component Detail Id
     *
     * @return
     */
    public int getPromotionComponentDetailId()
    {
        return promotionComponentDetailId;
    }

    /**
     * Get the Promotion Component Id
     *
     * @return
     */
    public int getPromotionComponentId()
    {
        return promotionComponentId;
    }

    /**
     * Get the PromotionId
     *
     * @return
     */
    public int getPromotionId()
    {
        return promotionId;
    }

    /**
     * Retrieves the Team Connection revision number.
     *
     * @return String representation of revision number
     */
    public String getRevisionNumber()
    {
        return (revisionNumber);
    }

    /**
     * Indicates if the rule is a damage discount.
     *
     * @param value
     */
    public boolean isDamageDiscount()
    {
        return false;
    }

    /**
     * Indicates if the rule is a employee discount.
     *
     * @param value
     */
    public boolean isEmployeeDiscount()
    {
        return false;
    }

    /**
     * Sets if the rule is a damage discount.
     *
     * @param value
     */
    public void setDamageDiscount(boolean value)
    {
    }

    /**
     * Sets employee discount employee.
     *
     * @param value employee discount employee
     */
    public void setDiscountEmployee(EmployeeIfc value)
    {
    }

    /**
     * Sets employee discount employee.
     *
     * @param value employee discount employee ID
     */
    public void setDiscountEmployee(String value)
    {
    }

    /**
     * Set the Promotion Component Detail Id
     *
     * @param promotionComponentDetailId
     */
    public void setPromotionComponentDetailId(int promotionComponentDetailId)
    {
        this.promotionComponentDetailId = promotionComponentDetailId;
    }

    /**
     * Set the Promotion Component Id
     *
     * @param promotionComponentId
     */
    public void setPromotionComponentId(int promotionComponentId)
    {
        this.promotionComponentId = promotionComponentId;
    }

    /**
     * Set the PromotionId
     *
     * @param promotionId
     */
    public void setPromotionId(int promotionId)
    {
        this.promotionId = promotionId;
    }

    /**
     * Method to default display string function.
     *
     * @return String representation of object
     */
    public String toString()
    {
        StringBuffer strResult = new StringBuffer("Class:  ItemDiscountByFixedPriceStrategy ");
        strResult.append("(Revision ").append(getRevisionNumber())
            .append(") @").append(hashCode()).append(Util.EOL)
            .append("discountAmount: [").append(discountAmount).append("]").append(Util.EOL)
            .append(super.toString());
        // pass back result
        return (strResult.toString());
    }

    /**
     * Method to restore the instance from an xml doc tree
     *
     * @param converter interface to the xml converter
     * @exception XMLConversionException if translation fails
     * @deprecated as of 13.1.No callers
     */
    public void translateFromElement(XMLConverterIfc converter) throws XMLConversionException
    {
        Element top = converter.getCurrentElement();
        Element[] properties = converter.getChildElements(top, XMLConverterIfc.TAG_PROPERTY);

        for (int i = 0; i < properties.length; i++)
        {
            Element element = properties[i];
            String name = element.getAttribute("name");

            if ("reasonCode".equals(name))
            {
                reasonCode = Integer.parseInt(converter.getElementText(element));
            }
            else if ("enabled".equals(name))
            {
                enabled = Boolean.valueOf(converter.getElementText(element));
            }
            else if ("discountAmount".equals(name))
            {
                Element[] discounts = converter.getChildElements(element);
                if (discounts.length > 0)
                {
                    discountAmount = (CurrencyIfc)converter.getObject(discounts[0]);
                }
                else
                {
                    discountAmount = DomainGateway.getBaseCurrencyInstance();
                }
            }
        }
    }

	/**
	 * Retrieves the PricingGroupID.
	 *
	 * @return  pricingGroupID
	 */
	public int getPricingGroupID()
	{
		return pricingGroupID;
	}

	/**
	 * Sets the PricingGroupID
	 *
	 * @param pricingGroupID
	 */
	public void setPricingGroupID(int pricingGroupID)
	{
		this.pricingGroupID = pricingGroupID;
	}
    /**
     * ItemDiscountByFixedPriceStrategy main method.
     *
     * @param String args[] command-line parameters
     */
    public static void main(String args[])
    {
        // instantiate class
        ItemDiscountByFixedPriceStrategy clsItemDiscountByFixedPriceStrategy = new ItemDiscountByFixedPriceStrategy();
        // output toString()
        System.out.println(clsItemDiscountByFixedPriceStrategy.toString());
    }

}
