/* ===========================================================================
* Copyright (c) 2008, 2013, Oracle and/or its affiliates. All rights reserved.
 * ===========================================================================
 * $Header: rgbustores/modules/domain/src/oracle/retail/stores/domain/discount/ReturnItemTransactionDiscountAudit.java /main/17 2013/12/17 16:08:05 mjwallac Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    mjwallac  12/17/13 - fix misused calls to Boolean.getBoolean()
 *    sgu       08/17/12 - refactor discount audit
 *    blarsen   02/22/12 - XbranchMerge
 *                         blarsen_bug13714601-order-pickup-stuck-in-tender-options
 *                         from rgbustores_13.4x_generic_branch
 *    blarsen   02/22/12 - Adding overloaded calculateItemDiscount() to comply
 *                         with changes to the interface. New method does not
 *                         use the additional param to method.
 *    cgreene   05/26/10 - convert to oracle packaging
 *    abondala  01/03/10 - update header date
 *    vikini    03/09/09 - Return Item with Trans Disc shows up as Item Disc in
 *                         POS report
 *    acadar    11/03/08 - updates as per code review
 *    acadar    11/03/08 - localization of reason codes for discounts and
 *                         merging to tip
 *    acadar    11/02/08 - cleanup
 *    acadar    11/02/08 - updates to unit tests
 *    acadar    10/30/08 - use localized reason codes for item and transaction
 *                         discounts
 * ===========================================================================

     $Log:
      4    360Commerce 1.3         4/25/2007 10:01:00 AM  Anda D. Cadar   I18N
           merge
      3    360Commerce 1.2         3/31/2005 4:29:45 PM   Robert Pearse
      2    360Commerce 1.1         3/10/2005 10:24:51 AM  Robert Pearse
      1    360Commerce 1.0         2/11/2005 12:13:53 PM  Robert Pearse
     $
     Revision 1.6  2004/03/22 03:48:51  cdb
     @scr 3588 Code Review Updates

     Revision 1.5  2004/03/02 18:33:42  cdb
     @scr 3588 Migrated common code to abstract class. Had
     Transaction Discounts begin preserving employee ID via
     the Audit's.

     Revision 1.4  2004/02/17 16:18:50  rhafernik
     @scr 0 log4j conversion

     Revision 1.3  2004/02/12 17:13:28  mcs
     Forcing head revision

     Revision 1.2  2004/02/11 23:25:27  bwf
     @scr 0 Organize imports.

     Revision 1.1.1.1  2004/02/11 01:04:29  cschellenger
     updating to pvcs 360store-current


 *
 *    Rev 1.1   Jan 26 2004 17:21:48   cdb
 * Added support for Employee and Damage item discounts.
 * Resolution for 3588: Discounts/MUPS - Gap Rollback
 *
 *    Rev 1.0   Aug 29 2003 15:35:04   CSchellenger
 * Initial revision.
 *
 *    Rev 1.0   Jun 03 2002 16:49:56   msg
 * Initial revision.
 *
 *    Rev 1.1   Mar 18 2002 22:58:12   msg
 * - updated copyright
 *
 *    Rev 1.0   Mar 18 2002 12:18:34   msg
 * Initial revision.
 *
 *    Rev 1.0   Sep 20 2001 16:12:48   msg
 * Initial revision.
 *
 *    Rev 1.1   Sep 17 2001 12:36:40   msg
 * header update
 * ===========================================================================
 */
package oracle.retail.stores.domain.discount;

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
    This class is used to record the discount amount imposed on a returned item by
    the application of a transaction discount. <P>
        The discount amount in this class should is expected to be negative. <P>
        This discount object always renders itself as an amount; therefore its method
        is returned as an amount.  However, the application, at times, needs to know
        what the original method was.  Therefore, the original method is also
        kept as an attribute. <P>
        @see oracle.retail.stores.domain.discount.ReturnItemTransactionDiscountAuditIfc
        @see oracle.retail.stores.domain.discount.DiscountRule
        @version $Revision: /main/17 $
**/
//------------------------------------------------------------------------------
@SuppressWarnings("serial")
public class ReturnItemTransactionDiscountAudit
extends AbstractItemDiscountAuditStrategy
implements ReturnItemTransactionDiscountAuditIfc
{
    /**
        revision number supplied by source-code control system
    **/
    public static final String revisionNumber = "$Revision: /main/17 $";

    /**
     * Initializes object with amount, method and reason code. <P>
     * @param amount amount of discount
     * @param method method code
     * @param reason reason code
    */
    public void initialize(CurrencyIfc amount, LocalizedCodeIfc reason)
    {                                   // begin initialize()
        // set attributes
        this.discountAmount = amount;
        this.reason = reason;
    }

    /* (non-Javadoc)
     * @see oracle.retail.stores.domain.discount.ItemDiscountStrategyIfc#calculateItemDiscount(oracle.retail.stores.commerceservices.common.currency.CurrencyIfc, java.math.BigDecimal)
     */
    public CurrencyIfc calculateItemDiscount(CurrencyIfc itemPrice, BigDecimal itemQuantity)
    {
        // no calculated discount here
        return(getDiscountAmount());
    }

    /* (non-Javadoc)
     * @see oracle.retail.stores.domain.discount.ItemDiscountStrategyIfc#calculateItemDiscount(oracle.retail.stores.commerceservices.common.currency.CurrencyIfc)
     */
    public CurrencyIfc calculateItemDiscount(CurrencyIfc itemPrice)
    {
        // no calculated discount here
        return(getDiscountAmount());
    }

    //---------------------------------------------------------------------
    /**
        Clone this object. <P>
        @return generic object copy of this object
    **/
    //---------------------------------------------------------------------
    public Object clone()
    {
        ReturnItemTransactionDiscountAudit newClass =
                                new ReturnItemTransactionDiscountAudit();
        setCloneAttributes(newClass);
        return newClass;
    }

    //---------------------------------------------------------------------
    /**
        Retrieves discount scope. <P>
        @return scope discount scope
    **/
    //---------------------------------------------------------------------
    public int getDiscountScope()
    {
        return DISCOUNT_SCOPE_TRANSACTION;
    }

    //----------------------------------------------------------------------------
    /**
     Sets if the rule is a damage discount. <P>
     @param value
     **/
    //----------------------------------------------------------------------------
    public void setDamageDiscount(boolean value)
    {
    }

    //----------------------------------------------------------------------------
    /**
        Indicates if the rule is a damage discount. <P>
        @return true if this is a damage discount
     **/
    //----------------------------------------------------------------------------
    public boolean isDamageDiscount()
    {
        return false;
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
        boolean isEqual = false;

        // Only test for equality if the objects are instances of the
        // same class.
        if (obj instanceof ReturnItemTransactionDiscountAudit)
        {
            isEqual = super.equals(obj);
        }
        return isEqual;
    }

    //---------------------------------------------------------------------
    /**
            Method to restore the instance from an xml doc tree
            @param converter interface to the xml converter
                @exception XMLConversionException if error occurs transalating from XML
            @deprecated as of 13.1.No callers.
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
            else if ("discountMethod".equals(name))
            {
                discountMethod = Integer.parseInt(converter.getElementText(element));
            }
            else if ("originalDiscountMethod".equals(name))
            {
                originalDiscountMethod = Integer.parseInt(converter.getElementText(element));
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
        Method to default display string function. <P>
        @return String representation of object
    **/
    //---------------------------------------------------------------------
    public String toString()
    {                                   // begin toString()

        // result string
        StringBuffer strResult = new StringBuffer("Class:  ");
        strResult.append("ReturnItemTransactionDiscountAudit (Revision ")
                         .append(getRevisionNumber()).append(") @")
                                 .append(hashCode()).append(Util.EOL);
        String strDiscountMethod = "Unknown ( " + discountMethod + ")";
        String strOriginalDiscountMethod = "Unknown ( " + discountMethod + ")";

        // If the discountMethod is within the known range, get the
        // corresponding String descriptor
        if ((discountMethod > -1)
            && (discountMethod < DiscountRuleConstantsIfc.DISCOUNT_METHOD_DESCRIPTOR.length))
        {
            strDiscountMethod =
                    DiscountRuleConstantsIfc.DISCOUNT_METHOD_DESCRIPTOR[discountMethod];
        }
        if ((originalDiscountMethod > -1)
            && (originalDiscountMethod < DiscountRuleConstantsIfc.DISCOUNT_METHOD_DESCRIPTOR.length))
        {
            strOriginalDiscountMethod =
                    DiscountRuleConstantsIfc.DISCOUNT_METHOD_DESCRIPTOR[originalDiscountMethod];
        }
        strResult.append(Util.EOL)
                 .append("\tdiscountAmount:                 [")
                 .append(discountAmount).append("]").append(Util.EOL)
                 .append("\treasonCode:                     [")
                 .append(reasonCode).append("]").append(Util.EOL)
                 .append("\tdiscountMethod:                 [")
                 .append(strDiscountMethod).append("]")
                 .append("\toriginalDiscountMethod:         [")
                 .append(strOriginalDiscountMethod).append("]")
                 .append(Util.EOL)
                 .append(super.toString());
        // pass back result
        return(strResult.toString());
    }                                  // end toString()

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
        ReturnItemTransactionDiscountAudit main method. <P>
        @param String args[]  command-line parameters
    **/
    //---------------------------------------------------------------------
    public static void main(String args[])
    {                                   // begin main()
        // instantiate class
        ReturnItemTransactionDiscountAudit clsReturnItemTransactionDiscountAudit =
          new ReturnItemTransactionDiscountAudit();
        // output toString()
        System.out.println(clsReturnItemTransactionDiscountAudit.toString());
    }                                  // end main()

}

