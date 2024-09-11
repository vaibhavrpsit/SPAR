/* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
  Copyright (c) 2012-2013 MAXHyperMarket, Inc.    All Rights Reserved.
  Rev 1.0	Jyoti Rawal		09/04/2013		Initial Draft: Changes for Employee Discount
* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */

package max.retail.stores.pos.services.pricing.employeediscount;

import max.retail.stores.pos.services.pricing.MAXModifyTransactionDiscountPercentReturnShuttle;
import oracle.retail.stores.domain.discount.DiscountRuleConstantsIfc;
import oracle.retail.stores.domain.discount.TransactionDiscountStrategyIfc;
import oracle.retail.stores.domain.transaction.SaleReturnTransactionIfc;

//--------------------------------------------------------------------------
/**
    Shuttles data from ModifyTransacationDiscountPercent service to
    ModifyTransaction service.
    @version $Revision: 3$
**/
//--------------------------------------------------------------------------
public class MAXModifyEmployeeTransactionDiscountPercentReturnShuttle
    extends MAXModifyTransactionDiscountPercentReturnShuttle
{
    /**
	 * 
	 */
	private static final long serialVersionUID = 1111323304633445927L;
	/**
       revision number of this class
    **/
    public static final String revisionNumber = "$Revision: 3$";
    
    //----------------------------------------------------------------------
    /**
     Gets Manual Discounts by Percentage from transaction. <P>
     @param  transaction  SaleReturnTransaction with potential discounts
     @return An array of transaction discount strategies
     **/
    //----------------------------------------------------------------------
    public TransactionDiscountStrategyIfc[] getDiscounts(SaleReturnTransactionIfc transaction)
    {
        TransactionDiscountStrategyIfc[] discountArray =
            transaction.getTransactionDiscounts(
                    DiscountRuleConstantsIfc.DISCOUNT_METHOD_PERCENTAGE,
                    DiscountRuleConstantsIfc.ASSIGNMENT_EMPLOYEE);
        return discountArray;
    }
   
    //----------------------------------------------------------------------
    /**
     Clears Manual Discounts by Percentage from transaction. <P>
     @param  transaction  SaleReturnTransaction with potential discounts
     **/
    //----------------------------------------------------------------------
    public void clearDiscounts(SaleReturnTransactionIfc transaction)
    {
        transaction.clearTransactionDiscounts(DiscountRuleConstantsIfc.DISCOUNT_METHOD_PERCENTAGE,
                DiscountRuleConstantsIfc.ASSIGNMENT_EMPLOYEE);
       
    }
    
}
