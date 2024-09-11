/* ===========================================================================
* Copyright (c) 1998, 2013, Oracle and/or its affiliates. All rights reserved.
 * ===========================================================================
 * $Header: rgbustores/modules/domain/src/oracle/retail/stores/domain/discount/ItemDiscountByPercentageStrategy.java /main/15 2013/12/17 16:08:05 mjwallac Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    mjwallac  12/17/13 - fix misused calls to Boolean.getBoolean()
 *    cgreene   03/29/12 - added serialveruid
 *    blarsen   02/20/12 - Overloading calculateItemDiscount() with a method that
 *                         calculates discounts more consistently for items with quantity > 1.
 *    blarsen   02/20/12 - Reverting call to multiplywithoutRounding back to
 *                         simply . This was recently added and it is
 *                         the only place in the code that does not round to
 *                         the currency's precision. This causes a sale to be
 *                         untenderable if there's a tiny fraction remaining in
 *                         the totals.
 *    mchellap  08/16/11 - Fixed rounding issue with item discount
 *    cgreene   05/26/10 - convert to oracle packaging
 *    abondala  01/03/10 - update header date
 *    acadar    11/03/08 - localization of reason codes for discounts and
 *                         merging to tip
 *
 * ===========================================================================
 * $Log:
 *    5    360Commerce 1.4         4/25/2007 10:01:02 AM  Anda D. Cadar   I18N
 *         merge
 *    4    360Commerce 1.3         1/22/2006 11:41:28 AM  Ron W. Haight
 *         Removed references to com.ibm.math.BigDecimal
 *    3    360Commerce 1.2         3/31/2005 4:28:30 PM   Robert Pearse
 *    2    360Commerce 1.1         3/10/2005 10:22:25 AM  Robert Pearse
 *    1    360Commerce 1.0         2/11/2005 12:11:37 PM  Robert Pearse
 *
 *   Revision 1.8.6.1  2004/11/04 20:11:25  rsachdeva
 *   @scr 4985 Price Adjustment Receipt Discount Scope
 *
 *   Revision 1.8  2004/05/20 22:54:57  cdb
 *   @scr 4204 Removed tabs from code base again.
 *
 *   Revision 1.7  2004/05/19 18:33:31  cdb
 *   @scr 5103 Updating to more correctly handle register reports.
 *
 *   Revision 1.6  2004/05/18 00:35:12  cdb
 *   @scr 5103    Corrected behavior of item and transaction discounts.
 *
 *   Revision 1.5  2004/03/02 18:33:42  cdb
 *   @scr 3588 Migrated common code to abstract class. Had
 *   Transaction Discounts begin preserving employee ID via
 *   the Audit's.
 *
 *   Revision 1.4  2004/02/17 16:18:50  rhafernik
 *   @scr 0 log4j conversion
 *
 *   Revision 1.3  2004/02/12 17:13:28  mcs
 *   Forcing head revision
 *
 *   Revision 1.2  2004/02/11 23:25:27  bwf
 *   @scr 0 Organize imports.
 *
 *   Revision 1.1.1.1  2004/02/11 01:04:29  cschellenger
 *   updating to pvcs 360store-current
 *
 *
 *
 *    Rev 1.2   Feb 04 2004 16:32:26   cdb
 * Updated "toString" to supply discount employee and damage discount flag.
 * Resolution for 3588: Discounts/MUPS - Gap Rollback
 *
 *    Rev 1.1   Jan 26 2004 17:21:56   cdb
 * Added support for Employee and Damage item discounts.
 * Resolution for 3588: Discounts/MUPS - Gap Rollback
 *
 *    Rev 1.0   Aug 29 2003 15:35:02   CSchellenger
 * Initial revision.
 *
 *    Rev 1.0   Jun 03 2002 16:49:46   msg
 * Initial revision.
 *
 *    Rev 1.1   Mar 18 2002 22:57:58   msg
 * - updated copyright
 *
 *    Rev 1.0   Mar 18 2002 12:18:22   msg
 * Initial revision.
 *
 *    Rev 1.2   21 Feb 2002 21:01:18   pjf
 * Changes for POS receipt requirements.
 * Resolution for POS SCR-1303: Item % disct on a kit comp is applied before AP disct and is rounding up
 * Resolution for POS SCR-1304: Store Coupon trans level does not print the coupon number on the receipt.
 *
 *    Rev 1.1   Feb 05 2002 16:34:20   mpm
 * Modified to use IBM BigDecimal class.
 * Resolution for Domain SCR-27: Employ IBM BigDecimal class
 *
 *    Rev 1.0   Sep 20 2001 16:12:40   msg
 * Initial revision.
 *
 *    Rev 1.1   Sep 17 2001 12:36:42   msg
 * header update
 * ===========================================================================
 */
package oracle.retail.stores.domain.discount;

import java.math.BigDecimal;

import oracle.retail.stores.commerceservices.common.currency.CurrencyIfc;
import oracle.retail.stores.common.utility.LocalizedCodeIfc;
import oracle.retail.stores.foundation.utility.Util;
import oracle.retail.stores.foundation.utility.xml.XMLConversionException;
import oracle.retail.stores.foundation.utility.xml.XMLConverterIfc;

import org.w3c.dom.Element;

/**
 * Discount by percentage strategy.
 *
 * @see oracle.retail.stores.domain.discount.AbstractDiscount
 * @version $Revision: /main/15 $
 */
public class ItemDiscountByPercentageStrategy extends AbstractItemDiscountStrategy
    implements ItemDiscountByPercentageIfc, ItemDiscountStrategyIfc
{
    private static final long serialVersionUID = -6751051370649960853L;

    /**
     * revision number supplied by source-code control system
     */
    public static final String revisionNumber = "$Revision: /main/15 $";

    /**
     * default discount scope for this
     */
    protected int discountScope = DISCOUNT_SCOPE_ITEM;

    /**
     * Constructs default ItemDiscountByPercentageStrategy object.
     */
    public ItemDiscountByPercentageStrategy()
    {

    }

    /**
     * Constructs ItemDiscountByPercentageStrategy object, setting rate and
     * reason code attributes.
     *
     * @param rate discount rate
     * @param reason code
     * @deprecated as of 13.1. Use {@link
     *             ItemDiscountByPercentageStrategy(BigDecimal discountRate,
     *             LocalizedCodeIfc reason)}
     */
    public ItemDiscountByPercentageStrategy(BigDecimal discountRate, int reasonCode)
    {
        this.discountRate = discountRate;
        this.reasonCode = reasonCode;
        this.reason.setCode(Integer.toString(reasonCode));
    }

    /**
     * Constructs ItemDiscountByPercentageStrategy object, setting rate and
     * reason code attributes.
     *
     * @param rate discount rate
     * @param reason code
     */
    public ItemDiscountByPercentageStrategy(BigDecimal discountRate, LocalizedCodeIfc reason)
    {
        this.discountRate = discountRate;
        this.reason = reason;

    }

    /**
     * Constructs ItemDiscountByPercentageStrategy object, setting amount,
     * reason code and ruleID attributes.
     *
     * @param amount discount amount
     * @param reason code
     * @param ruleID
     * @deprecated as of 13.1. Use {@link
     *             ItemDiscountByPercentageStrategy(BigDecimal discountRate,
     *             LocalizedCodeIfc reason, String ruleID)}
     */
    public ItemDiscountByPercentageStrategy(BigDecimal discountRate, int reasonCode, String ruleID)
    {
        this.discountRate = discountRate;
        this.reasonCode = reasonCode;
        this.ruleID = ruleID;
    }

    /**
     * Constructs ItemDiscountByPercentageStrategy object, setting amount,
     * reason code and ruleID attributes.
     *
     * @param amount discount amount
     * @param reason code
     * @param ruleID
     */
    public ItemDiscountByPercentageStrategy(BigDecimal discountRate, LocalizedCodeIfc reason, String ruleID)
    {
        this.discountRate = discountRate;
        this.reason = reason;
    }

    /* (non-Javadoc)
     * @see oracle.retail.stores.domain.discount.ItemDiscountStrategyIfc#calculateItemDiscount(oracle.retail.stores.commerceservices.common.currency.CurrencyIfc, java.math.BigDecimal)
     */
    public CurrencyIfc calculateItemDiscount(CurrencyIfc itemPrice, BigDecimal itemQuantity)
    {
        // If itemPrice is negative, discount amount returned is negative

        BigDecimal quantity = itemQuantity.abs();
        // for typical quantity == 1 or UOM not unit, use the extended selling price to calculate discount
        if (quantity.equals(BigDecimal.ONE) || !isWhole(quantity) )
        {
            discountAmount = itemPrice.multiply(discountRate);
            return discountAmount;
        }
        // if the item has > 1 quantity, then calculate discount in such a way to to get same discount for
        // quantity x individual items
        else
        {
            CurrencyIfc individualPrice = itemPrice.divide(quantity);
            discountAmount = individualPrice.multiply(discountRate).multiply(quantity);
            return discountAmount;
        }
    }

    /* (non-Javadoc)
     * @see oracle.retail.stores.domain.discount.ItemDiscountStrategyIfc#calculateItemDiscount(oracle.retail.stores.commerceservices.common.currency.CurrencyIfc)
     */
    public CurrencyIfc calculateItemDiscount(CurrencyIfc itemPrice)
    {
        // If itemPrice is negative, discount amount returned is negative
        discountAmount = itemPrice.multiply(discountRate);
        return discountAmount;
    }

    /**
     *  Is the specified quantity a whole number?
     *  @param quantity the item quantity
     *  @return boolean Is the specified quantity a whole number?
     */
    private boolean isWhole(BigDecimal quantity)
    {
        BigDecimal scaledZero = BigDecimal.ZERO.setScale(quantity.scale());
        // parts[0] is whole part, parts[1] is remainder
        BigDecimal parts[] = quantity.divideAndRemainder(BigDecimal.ONE);
        boolean isWhole = scaledZero.equals(parts[1]);
        return isWhole;
    }

    /**
     * Clone this object.
     *
     * @return generic object copy of this object
     */
    public Object clone()
    {
        ItemDiscountByPercentageStrategy newClass = new ItemDiscountByPercentageStrategy();
        setCloneAttributes(newClass);
        newClass.setDiscountScope(getDiscountScope());
        return newClass;
    }

    /**
     * Retrieves discount method.
     *
     * @return method discount method
     */
    public int getDiscountMethod()
    {
        return (DiscountRuleConstantsIfc.DISCOUNT_METHOD_PERCENTAGE);
    }

    /**
     * Retrieves discount scope.
     *
     * @return scope discount scope
     */
    public int getDiscountScope()
    {
        return this.discountScope;
    }

    /**
     * Sets discount scope. Used by Price adjustment.
     *
     * @param discountScope scope discount scope
     */
    public void setDiscountScope(int discountScope)
    {
        this.discountScope = discountScope;
    }

    /**
     * Method to restore the instance from an xml doc tree
     *
     * @param converter interface to the xml converter
     * @exception XMLConversionException if translation fails
     * @deprecated as of 13.1. No callers
     */
    public void translateFromElement(XMLConverterIfc converter) throws XMLConversionException
    {
        Element top = converter.getCurrentElement();
        Element[] properties = converter.getChildElements(top, XMLConverterIfc.TAG_PROPERTY);

        // Retrieve and store the values for each property
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
            else if ("discountRate".equals(name))
            {
                discountRate = new BigDecimal(converter.getElementText(element));
            }
        }
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
        boolean objectIsEqual = (obj instanceof ItemDiscountByPercentageStrategy)? super.equals(obj) : false;

        return objectIsEqual;

    }

    /* (non-Javadoc)
     * @see oracle.retail.stores.domain.discount.AbstractItemDiscountStrategy#toString()
     */
    @Override
    public String toString()
    {
        // result string
        StringBuilder strResult = new StringBuilder("Class:  ");
        strResult.append("ItemDiscountByPercentageStrategy ").append("(Revision ").append(getRevisionNumber())
                .append(") @").append(hashCode()).append(Util.EOL).append("discountRate:                   [")
                .append(getDiscountRate()).append("]").append(Util.EOL).append(super.toString());
        // pass back result
        return (strResult.toString());
    }

    /**
     * Retrieves the Team Connection revision number.
     *
     * @return String representation of revision number
     */
    public String getRevisionNumber()
    {
        // return string
        return (revisionNumber);
    }

    /**
     * ItemDiscountByPercentageStrategy main method.
     *
     * @param String args[] command-line parameters
     */
    public static void main(String args[])
    {
        // instantiate class
        ItemDiscountByPercentageStrategy clsItemDiscountByPercentageStrategy = new ItemDiscountByPercentageStrategy();
        // output toString()
        System.out.println(clsItemDiscountByPercentageStrategy.toString());
    }

}