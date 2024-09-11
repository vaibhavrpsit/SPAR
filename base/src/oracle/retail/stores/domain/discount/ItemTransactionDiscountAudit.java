/* ===========================================================================
* Copyright (c) 1998, 2014, Oracle and/or its affiliates. All rights reserved.
 * ===========================================================================
 * $Header: rgbustores/modules/domain/src/oracle/retail/stores/domain/discount/ItemTransactionDiscountAudit.java /main/16 2014/06/23 13:03:35 sgu Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    sgu       06/18/14 - convert transactional item discount to item discount
 *                         for order pickup/cancel
 *    jswan     12/14/12 - Modified to convert Transaction Discounts into Item
 *                         discount with returning items from and order.
 *    sgu       08/17/12 - refactor discount audit
 *    sgu       08/17/12 - fix discount rules
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
 *    acadar    11/02/08 - cleanup
 *    acadar    11/02/08 - updates to unit tests
 *    acadar    10/30/08 - use localized reason codes for item and transaction
 *                         discounts
 * ===========================================================================

     $Log:
      4    360Commerce 1.3         4/25/2007 10:01:02 AM  Anda D. Cadar   I18N
           merge
      3    360Commerce 1.2         3/31/2005 4:28:35 PM   Robert Pearse
      2    360Commerce 1.1         3/10/2005 10:22:33 AM  Robert Pearse
      1    360Commerce 1.0         2/11/2005 12:11:43 PM  Robert Pearse
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
 *    Rev 1.1   Jan 26 2004 17:21:58   cdb
 * Added support for Employee and Damage item discounts.
 * Resolution for 3588: Discounts/MUPS - Gap Rollback
 *
 *    Rev 1.0   Aug 29 2003 15:35:02   CSchellenger
 * Initial revision.
 *
 *    Rev 1.0   Jun 03 2002 16:49:52   msg
 * Initial revision.
 *
 *    Rev 1.1   Mar 18 2002 22:58:06   msg
 * - updated copyright
 *
 *    Rev 1.0   Mar 18 2002 12:18:28   msg
 * Initial revision.
 *
 *    Rev 1.0   Sep 20 2001 16:12:44   msg
 * Initial revision.
 *
 *    Rev 1.1   Sep 17 2001 12:36:40   msg
 * header update
 * ===========================================================================
 */
package oracle.retail.stores.domain.discount;
// w3c import
import java.math.BigDecimal;

import oracle.retail.stores.commerceservices.common.currency.CurrencyIfc;
import oracle.retail.stores.common.utility.LocalizedCodeIfc;
import oracle.retail.stores.domain.DomainGateway;
import oracle.retail.stores.common.utility.Util;

//------------------------------------------------------------------------------
/**
    This class is used to record the discount amount imposed on an item by
    the application of a transaction discount. <P>
        @see oracle.retail.stores.domain.discount.ItemTransactionDiscountAuditIfc
        @see oracle.retail.stores.domain.discount.DiscountRule
        @version $Revision: /main/16 $
**/
//------------------------------------------------------------------------------
@SuppressWarnings("serial")
public class ItemTransactionDiscountAudit
extends AbstractItemDiscountAuditStrategy
implements ItemTransactionDiscountAuditIfc
{
    /**
        revision number supplied by source-code control system
    **/
    public static final String revisionNumber = "$Revision: /main/16 $";

    //---------------------------------------------------------------------
    /**
        Constructs ItemTransactionDiscountAudit object. <P>
    **/
    //---------------------------------------------------------------------
    public ItemTransactionDiscountAudit()
    {
        super();
        itemDiscountAmount      = DomainGateway.getBaseCurrencyInstance();
        discountAmount          = DomainGateway.getBaseCurrencyInstance();
        assignmentBasis = ASSIGNMENT_MANUAL;
    }

    /**
     * Initializes the ItemTransactionDiscountAudit object,
     * setting amount, method, reason and trigger attributes. <P>
     * @param amount discount amount
     * @param method discount method
     * @param reason reason code
     * @param basis discount assignment basis
    */
    public void initialize(CurrencyIfc amount, LocalizedCodeIfc reason, int basis)
    {
        this.discountAmount = amount;
        this.reason = reason;
        this.assignmentBasis = basis;
    }

    /* (non-Javadoc)
     * @see oracle.retail.stores.domain.discount.ItemDiscountStrategyIfc#calculateItemDiscount(oracle.retail.stores.commerceservices.common.currency.CurrencyIfc, java.math.BigDecimal)
     */
    public CurrencyIfc calculateItemDiscount(CurrencyIfc itemPrice, BigDecimal itemQuantity)
    {
        // no calculated discount here
        return(DomainGateway.getBaseCurrencyInstance());
    }

    /* (non-Javadoc)
     * @see oracle.retail.stores.domain.discount.ItemDiscountStrategyIfc#calculateItemDiscount(oracle.retail.stores.commerceservices.common.currency.CurrencyIfc)
     */
    public CurrencyIfc calculateItemDiscount(CurrencyIfc itemPrice)
    {
        // no calculated discount here
        return(DomainGateway.getBaseCurrencyInstance());
    }

    //---------------------------------------------------------------------
    /**
        Clone this object. <P>
        @return generic object copy of this object
    **/
    //---------------------------------------------------------------------
    public Object clone()
    {
        ItemTransactionDiscountAudit newAudit = new ItemTransactionDiscountAudit();
        setCloneAttributes(newAudit);
        return(newAudit);
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

    //---------------------------------------------------------------------
    /**
     * @return a new prorated item discount audit strategy
     */
    //---------------------------------------------------------------------
    protected ItemDiscountAuditStrategyIfc createNewProratedItemDiscountAudit()
    {
        ItemDiscountAuditStrategyIfc itemDiscountAudit = null;

        itemDiscountAudit = DomainGateway.getFactory().getItemDiscountAuditInstance();
        setCloneAttributes((ItemDiscountAudit)itemDiscountAudit);

        return itemDiscountAudit;
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
        return (obj instanceof ItemTransactionDiscountAudit)
                                ?
                    super.equals(obj) : false;
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
          new StringBuffer("Class:  ");
        strResult.append("ItemTransactionDiscountAudit (Revision ")
                 .append(getRevisionNumber()).append(") @")
                 .append(hashCode()).append(Util.EOL);
        String strDiscountMethod = "Unknown ( " + discountMethod + ")";

        // If the discountMethod is within the known range, get the
        // corresponding String descriptor
        if ((discountMethod > -1)
            && (discountMethod < DiscountRuleConstantsIfc.DISCOUNT_METHOD_DESCRIPTOR.length))
        {
            strDiscountMethod =
                    DiscountRuleConstantsIfc.DISCOUNT_METHOD_DESCRIPTOR[discountMethod];
        }
        strResult.append("discountAmount:                 [")
                 .append(discountAmount).append("]")
                 .append(Util.EOL)
                 .append("discountMethod:                 [")
                 .append(strDiscountMethod).append("]")
                 .append("discount assignmentBasis:               [")
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
    {
        // return string
        return(revisionNumber);
    }

    //---------------------------------------------------------------------
    /**
        ItemTransactionDiscountAudit main method. <P>
        @param String args[]  command-line parameters
    **/
    //---------------------------------------------------------------------
    public static void main(String args[])
    {
        // instantiate class
        ItemTransactionDiscountAudit clsItemTransactionDiscountAudit =
          new ItemTransactionDiscountAudit();
        // output toString()
        System.out.println(clsItemTransactionDiscountAudit.toString());
    }

}

