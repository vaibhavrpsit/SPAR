/* ===========================================================================
* Copyright (c) 1998, 2013, Oracle and/or its affiliates. All rights reserved.
 * ===========================================================================
 * $Header: rgbustores/modules/domain/src/oracle/retail/stores/domain/discount/ItemDiscountByAmountStrategy.java /main/14 2013/12/17 16:08:05 mjwallac Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    mjwallac  12/17/13 - fix misused calls to Boolean.getBoolean()
 *    sgu       08/14/12 - add ItemDiscountAudit
 *    blarsen   02/22/12 - XbranchMerge
 *                         blarsen_bug13714601-order-pickup-stuck-in-tender-options
 *                         from rgbustores_13.4x_generic_branch
 *    blarsen   02/22/12 - Adding overloaded calculateItemDiscount() to comply
 *                         with changes to the interface. New method does not
 *                         use the additional param to method.
 *    cgreene   05/26/10 - convert to oracle packaging
 *    abondala  01/03/10 - update header date
 *    acadar    11/03/08 - localization of reason codes for discounts and
 *                         merging to tip
 *
 * ===========================================================================
 * $Log:
 *    5    360Commerce 1.4         5/15/2007 5:53:46 PM   Maisa De Camargo
 *         Added PromotionId, PromotionComponentId and
 *         PromotionComponentDetailId
 *    4    360Commerce 1.3         4/25/2007 10:01:02 AM  Anda D. Cadar   I18N
 *         merge
 *    3    360Commerce 1.2         3/31/2005 4:28:30 PM   Robert Pearse
 *    2    360Commerce 1.1         3/10/2005 10:22:24 AM  Robert Pearse
 *    1    360Commerce 1.0         2/11/2005 12:11:36 PM  Robert Pearse
 *
 *   Revision 1.6.6.1  2004/11/04 20:11:25  rsachdeva
 *   @scr 4985 Price Adjustment Receipt Discount Scope
 *
 *   Revision 1.6  2004/05/19 18:33:31  cdb
 *   @scr 5103 Updating to more correctly handle register reports.
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
 *    Rev 1.2   Feb 04 2004 16:32:22   cdb
 * Updated "toString" to supply discount employee and damage discount flag.
 * Resolution for 3588: Discounts/MUPS - Gap Rollback
 *
 *    Rev 1.1   Jan 26 2004 17:21:54   cdb
 * Added support for Employee and Damage item discounts.
 * Resolution for 3588: Discounts/MUPS - Gap Rollback
 *
 *    Rev 1.0   Aug 29 2003 15:35:02   CSchellenger
 * Initial revision.
 *
 *    Rev 1.0   Jun 03 2002 16:49:40   msg
 * Initial revision.
 *
 *    Rev 1.1   Mar 18 2002 22:57:54   msg
 * - updated copyright
 *
 *    Rev 1.0   Mar 18 2002 12:18:16   msg
 * Initial revision.
 *
 *    Rev 1.0   Sep 20 2001 16:12:52   msg
 * Initial revision.
 *
 *    Rev 1.1   Sep 17 2001 12:36:44   msg
 * header update
 * ===========================================================================
 */
package oracle.retail.stores.domain.discount;

// w3c imports
import java.math.BigDecimal;

import oracle.retail.stores.commerceservices.common.currency.CurrencyIfc;
import oracle.retail.stores.common.utility.LocalizedCodeIfc;
import oracle.retail.stores.domain.DomainGateway;
import oracle.retail.stores.foundation.utility.Util;
import oracle.retail.stores.foundation.utility.xml.XMLConversionException;
import oracle.retail.stores.foundation.utility.xml.XMLConverterIfc;

import org.w3c.dom.Element;

//------------------------------------------------------------------------------
/**
    Discount by amount strategy. <P>
        @see oracle.retail.stores.domain.discount.ItemDiscountByAmountIfc
        @see oracle.retail.stores.domain.discount.ItemDiscountStrategyIfc
        @see oracle.retail.stores.domain.discount.DiscountRule
    @version $Revision: /main/14 $
**/
//------------------------------------------------------------------------------
public class ItemDiscountByAmountStrategy
extends AbstractItemDiscountStrategy
implements  ItemDiscountByAmountIfc,
            ItemDiscountStrategyIfc
{
    /** Generated Serial Version UID */
    private static final long serialVersionUID = 1260499728467853003L;

    /**
        revision number supplied by source-code control system
    **/
    public static final String revisionNumber = "$Revision: /main/14 $";

    /**
        default discount scope for this
    **/
    protected int discountScope = DISCOUNT_SCOPE_ITEM;

    //---------------------------------------------------------------------
    /**
        Constructs ItemDiscountByAmountStrategy object. <P>
    **/
    //---------------------------------------------------------------------
    public ItemDiscountByAmountStrategy()
    {

    }

    //---------------------------------------------------------------------
    /**
        Constructs ItemDiscountByAmountStrategy object, setting amount and reason code attributes. <P>
        @param amount discount amount
        @param reason code
        @deprecated as of 13.1. Use {@link ItemDiscountByAmountStrategy(CurrencyIfc discountAmount, LocalizedCodeIfc reasonCode)
    **/
    //---------------------------------------------------------------------
    public ItemDiscountByAmountStrategy(CurrencyIfc discountAmount, int reasonCode)
    {
        this.discountAmount = discountAmount;
        this.reasonCode     = reasonCode;
        this.getReason().setCode(Integer.toString(reasonCode));
    }

    /**
     * Constructs ItemDiscountByAmountStrategy object, setting amount and reason code attributes. <P>
     * @param amount discount amount
     * @param reason code
    */
    public ItemDiscountByAmountStrategy(CurrencyIfc discountAmount, LocalizedCodeIfc reason)
    {
        this.discountAmount = discountAmount;
        this.reason    = reason;

    }

    //---------------------------------------------------------------------
    /**
        Constructs ItemDiscountByAmountStrategy object, setting amount,
        reason code and ruleID attributes. <P>
        @param amount discount amount
        @param reason code
                @param ruleID
        @deprecated as of 13.1. Use {@link ItemDiscountByAmountStrategy(CurrencyIfc discountAmount, LocalizedCodeIfc reasonCode, String ruleID)
    **/
    //---------------------------------------------------------------------
    public ItemDiscountByAmountStrategy(CurrencyIfc discountAmount, int reasonCode, String ruleID)
    {
        this.discountAmount = discountAmount;
        this.reasonCode     = reasonCode;
        this.ruleID         = ruleID ;
        this.getReason().setCode(Integer.toString(reasonCode));
    }

    /**
     * Constructs ItemDiscountByAmountStrategy object, setting amount,
     * reason code and ruleID attributes. <P>
     * @param amount discount amount
     * @param reason code
     * @param ruleID
   */
    public ItemDiscountByAmountStrategy(CurrencyIfc discountAmount, LocalizedCodeIfc reason, String ruleID)
    {
        this.discountAmount = discountAmount;
        this.reason    = reason;
        this.ruleID    = ruleID ;

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
        return(discountAmount);
    }

    //---------------------------------------------------------------------
    /**
        Clone this object. <P>
        @return generic object copy of this object
    **/
    //---------------------------------------------------------------------
    public Object clone()
    {
        ItemDiscountByAmountStrategy newClass = new ItemDiscountByAmountStrategy();
        setCloneAttributes(newClass);
        return newClass;
    }

    //---------------------------------------------------------------------
    /**
        Retrieves discount method. <P>
        @return method discount method
    **/
    //---------------------------------------------------------------------
    public int getDiscountMethod()
    {
        return DISCOUNT_METHOD_AMOUNT;
    }

    //---------------------------------------------------------------------
    /**
        Retrieves discount scope. Default is DISCOUNT_SCOPE_ITEM<P>
        @return scope discount scope
    **/
    //---------------------------------------------------------------------
    public int getDiscountScope()
    {
        return this.discountScope;
    }

    //---------------------------------------------------------------------
    /**
       Sets discount scope. Used by Price adjustment. <P>
       @param discountScope scope discount scope
    **/
    //---------------------------------------------------------------------
    public void setDiscountScope(int discountScope)
    {
        this.discountScope = discountScope;
    }

    //----------------------------------------------------------------------------
    /**
        Sets attributes in clone of this object. <P>
        @param newClass new instance of object
    **/
    //----------------------------------------------------------------------------
    public void setCloneAttributes(ItemDiscountByAmountStrategy newClass)
    {
        super.setCloneAttributes(newClass);
        newClass.setDiscountScope(getDiscountScope());
    }

    //--------------------------------------------------------------------------
    /**
        Determine whether the provided object is the same type and
        has the same field values as this one. <P>
        @param obj the object to compare
        @return true if the fields are equal; false otherwise
    **/
    //--------------------------------------------------------------------------

    public boolean equals(Object obj)
    {
        boolean objectIsEqual = (obj instanceof ItemDiscountByAmountStrategy)
            ?
            super.equals(obj) : false;

        return objectIsEqual;
    }


    //---------------------------------------------------------------------
    /**
        Method to default display string function. <P>
        @return String representation of object
    **/
    //---------------------------------------------------------------------
    public String toString()
    {                                   // begin toString()
        // result string
        StringBuffer strResult =
          new StringBuffer("Class:  ItemDiscountByAmountStrategy ");
        strResult.append("(Revision ").append(getRevisionNumber())
                 .append(") @").append(hashCode())
                 .append(Util.EOL)
                 .append("discountAmount:                 [")
                 .append(discountAmount).append("]")
                 .append(Util.EOL)
                 .append(super.toString());
        // pass back result
        return(strResult.toString());
    }                                  // end toString()

    //---------------------------------------------------------------------
    /**
        Method to restore the instance from an xml doc tree
        @param converter interface to the xml converter
            @exception XMLConversionException if translation fails
            @deprecated as of 13.1. No callers.
    **/
    //---------------------------------------------------------------------
    public void translateFromElement(XMLConverterIfc converter) throws XMLConversionException
    {
        Element top = converter.getCurrentElement();
        Element[] properties = converter.getChildElements(top,XMLConverterIfc.TAG_PROPERTY);

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
                    discountAmount = (CurrencyIfc) converter.getObject(discounts[0]);
                }
                else
                {
                    discountAmount = DomainGateway.getBaseCurrencyInstance();
                }
            }
        }
    }


    //---------------------------------------------------------------------
    /**
        Retrieves the Team Connection revision number. <P>
        @return String representation of revision number
    **/
    //---------------------------------------------------------------------
    public String getRevisionNumber()
    {                                   // begin getRevisionNumber()
        // return string
        return(revisionNumber);
    }                                  // end getRevisionNumber()

    //---------------------------------------------------------------------
    /**
        ItemDiscountByAmountStrategy main method. <P>
        @param String args[]  command-line parameters
    **/
    //---------------------------------------------------------------------
    public static void main(String args[])
    {                                   // begin main()
        // instantiate class
        ItemDiscountByAmountStrategy clsItemDiscountByAmountStrategy =
          new ItemDiscountByAmountStrategy();
        // output toString()
        System.out.println(clsItemDiscountByAmountStrategy.toString());
    }                                  // end main()

}

