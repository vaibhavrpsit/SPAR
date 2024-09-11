
package oracle.retail.stores.domain.discount;

import java.math.BigDecimal;
import java.util.HashMap;

import org.w3c.dom.Element;

import oracle.retail.stores.commerceservices.common.currency.CurrencyIfc;
import oracle.retail.stores.common.utility.LocalizedCodeIfc;
import oracle.retail.stores.domain.DomainGateway;
import oracle.retail.stores.domain.employee.EmployeeIfc;
import oracle.retail.stores.foundation.utility.Util;
import oracle.retail.stores.foundation.utility.xml.XMLConversionException;
import oracle.retail.stores.foundation.utility.xml.XMLConverterIfc;

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
    
	public void setCloneAttributes(ItemDiscountByFixedPriceStrategy newClass) {
		super.setCloneAttributes(newClass);
		//Changes to resolve missing txn issues in prod : Starts
		newClass.setCapillaryCoupon(capillaryCoupon);
		//Changes to resolve missing txn issues in prod : Ends
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

	//Changes to resolve missing txn issues in prod : Starts
    protected HashMap capillaryCoupon = new HashMap();
	
	public HashMap getCapillaryCoupon() {
		return capillaryCoupon;
	}

	public void setCapillaryCoupon(HashMap capillaryCoupon) {
		this.capillaryCoupon = capillaryCoupon;
	}
	//Changes to resolve missing txn issues in prod : Ends
}
