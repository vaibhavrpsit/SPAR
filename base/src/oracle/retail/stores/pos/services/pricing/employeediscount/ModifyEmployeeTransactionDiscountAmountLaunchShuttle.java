/* ===========================================================================
* Copyright (c) 2004, 2011, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/pricing/employeediscount/ModifyEmployeeTransactionDiscountAmountLaunchShuttle.java /rgbustores_13.4x_generic_branch/1 2011/05/05 16:17:09 mszekely Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    cgreene   05/26/10 - convert to oracle packaging
 *    cgreene   04/27/10 - updating deprecated names
 *    abondala  01/03/10 - update header date
 *
 * ===========================================================================
 * $Log:
 *    3    360Commerce 1.2         3/31/2005 4:29:03 PM   Robert Pearse   
 *    2    360Commerce 1.1         3/10/2005 10:23:33 AM  Robert Pearse   
 *    1    360Commerce 1.0         2/11/2005 12:12:39 PM  Robert Pearse   
 *
 *   Revision 1.2  2004/03/22 03:49:28  cdb
 *   @scr 3588 Code Review Updates
 *
 *   Revision 1.1  2004/03/03 21:03:45  cdb
 *   @scr 3588 Added employee transaction discount service.
 *
 *
 * ===========================================================================
 */
package oracle.retail.stores.pos.services.pricing.employeediscount;

import oracle.retail.stores.domain.discount.DiscountRuleConstantsIfc;
import oracle.retail.stores.domain.discount.TransactionDiscountStrategyIfc;
import oracle.retail.stores.domain.transaction.SaleReturnTransactionIfc;
import oracle.retail.stores.pos.services.pricing.ModifyTransactionDiscountAmountLaunchShuttle;

//--------------------------------------------------------------------------
/**
    Loads transaction discount data into shuttle. <P>
    @version $Revision: /rgbustores_13.4x_generic_branch/1 $
**/
//--------------------------------------------------------------------------
public class ModifyEmployeeTransactionDiscountAmountLaunchShuttle
    extends ModifyTransactionDiscountAmountLaunchShuttle
{
    /**
       revision number of this class
    **/
    public static final String revisionNumber = "$Revision: /rgbustores_13.4x_generic_branch/1 $";
    
    //----------------------------------------------------------------------
    /**
     Gets Manual Discounts by Amount from transaction. <P>
     @param  transaction  SaleReturnTransaction with potential discounts
     @return An array of transaction discount strategies
     **/
    //----------------------------------------------------------------------
    public TransactionDiscountStrategyIfc[] getDiscounts(SaleReturnTransactionIfc transaction)
    {
        TransactionDiscountStrategyIfc[] discountArray =
            transaction.getTransactionDiscounts(
                    DiscountRuleConstantsIfc.DISCOUNT_METHOD_AMOUNT,
                    DiscountRuleConstantsIfc.ASSIGNMENT_EMPLOYEE);
        return discountArray;
    }
}
