/* ===========================================================================
* Copyright (c) 1998, 2011, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/pricing/ModifyTransactionDiscountAmountLaunchShuttle.java /rgbustores_13.4x_generic_branch/1 2011/05/05 14:06:01 mszekely Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    cgreene   05/26/10 - convert to oracle packaging
 *    cgreene   04/27/10 - XbranchMerge cgreene_refactor-duplicate-pos-classes
 *                         from st_rgbustores_techissueseatel_generic_branch
 *    cgreene   04/27/10 - updating deprecated names
 *    abondala  01/03/10 - update header date
 *
 * ===========================================================================
 * $Log:
 *    4    360Commerce 1.3         4/25/2007 8:52:18 AM   Anda D. Cadar   I18N
 *         merge
 *         
 *    3    360Commerce 1.2         3/31/2005 4:29:04 PM   Robert Pearse   
 *    2    360Commerce 1.1         3/10/2005 10:23:35 AM  Robert Pearse   
 *    1    360Commerce 1.0         2/11/2005 12:12:40 PM  Robert Pearse   
 *
 *   Revision 1.6.2.1  2004/11/09 20:34:40  jdeleau
 *   @scr 7661 Make sure the discount already applied screen appears when
 *   the user first does an item discount, followed by a transaction discount
 *   on an item selected other than the one that had the item discount applied.
 *
 *   Revision 1.6  2004/07/22 22:10:49  dcobb
 *   @scr 3312 Cannot specify transaction level discount before entering an item
 *   Enabled Trans. Amt. button when there are no line items
 *
 *   Revision 1.5  2004/03/03 21:03:45  cdb
 *   @scr 3588 Added employee transaction discount service.
 *
 *   Revision 1.4  2004/02/13 22:24:29  cdb
 *   @scr 3588 Added dialog to indicate when discount will reduce
 *   some prices below zero but not others.
 *
 *   Revision 1.3  2004/02/12 16:51:34  mcs
 *   Forcing head revision
 *
 *   Revision 1.2  2004/02/11 21:52:06  rhafernik
 *   @scr 0 Log4J conversion and code cleanup
 *
 *   Revision 1.1.1.1  2004/02/11 01:04:19  cschellenger
 *   updating to pvcs 360store-current
 *
 *
 * 
 *    Rev 1.1   Oct 17 2003 10:24:58   bwf
 * Add employeeDiscountID and remove unused imports.
 * Resolution for 3412: Feature Enhancement: Employee Discount
 * 
 *    Rev 1.0   Aug 29 2003 16:05:16   CSchellenger
 * Initial revision.
 * 
 *    Rev 1.1   Jun 02 2003 15:33:12   bwf
 * Get item discount subtotal to pass to transaction discount functionality.
 * Resolution for 2522: Changing the transaction discount from an amount of 9999999.99 to 100% results in a discount of 9999999.99 plus the price of the item.
 * 
 *    Rev 1.0   02 May 2002 17:39:08   jbp
 * Initial revision.
 * Resolution for POS SCR-1626: Pricing Feature
 *
 * ===========================================================================
 */
package oracle.retail.stores.pos.services.pricing;

import org.apache.log4j.Logger;

import oracle.retail.stores.commerceservices.common.currency.CurrencyIfc;
import oracle.retail.stores.pos.services.common.FinancialCargoShuttle;
import oracle.retail.stores.domain.discount.DiscountRuleConstantsIfc;
import oracle.retail.stores.domain.discount.TransactionDiscountStrategyIfc;
import oracle.retail.stores.domain.employee.EmployeeIfc;
import oracle.retail.stores.domain.transaction.SaleReturnTransactionIfc;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.pos.services.modifytransaction.discount.ModifyTransactionDiscountCargo;

//--------------------------------------------------------------------------
/**
    Loads transaction discount data into shuttle. <P>
    @version $Revision: /rgbustores_13.4x_generic_branch/1 $
**/
//--------------------------------------------------------------------------
public class ModifyTransactionDiscountAmountLaunchShuttle extends FinancialCargoShuttle
{
    /**
        The logger to which log messages will be sent.
    **/
    protected static Logger logger = Logger.getLogger(oracle.retail.stores.pos.services.pricing.ModifyTransactionDiscountAmountLaunchShuttle.class);

    /**
       revision number of this class
    **/
    public static final String revisionNumber = "$Revision: /rgbustores_13.4x_generic_branch/1 $";

    /**
       the default sales associate
    **/
    protected EmployeeIfc salesAssociate;

    /**
       the amount discount
    **/
    protected TransactionDiscountStrategyIfc discountAmount;

    /**
       Flag to determine whether a transaction can be created by the
       child service
    **/
    protected boolean createTransaction;
    
    /**
       the item sub total  
     **/
    protected CurrencyIfc itemTotal;
    
    /**
        Employee Discount ID      
     **/
    protected String employeeDiscountID;

    /**
     * Transaction discount is being applied to
     */
    protected SaleReturnTransactionIfc transaction;
    //----------------------------------------------------------------------
    /**
       Loads data into shuttle.
       @param  bus     Service Bus
    **/
    //----------------------------------------------------------------------
    public void load(BusIfc bus)
    {

        // load financial cargo
        super.load(bus);

        // retrieve the parent cargo
        PricingCargo cargo = (PricingCargo)bus.getCargo();
        salesAssociate = (cargo.getTransaction() != null) ? cargo.getTransaction().getSalesAssociate() :
                                                            null;
        
        employeeDiscountID = cargo.getEmployeeDiscountID();  
        // the amount discount is retrieved; if none exists a null is returned
        // In Quarry, discount is first in array.
        transaction = (SaleReturnTransactionIfc)cargo.getTransaction();

        if (transaction != null)
        {
            TransactionDiscountStrategyIfc[] discountArray;
            discountArray = getDiscounts(transaction);

            if (discountArray != null && discountArray.length > 0)
            {
                discountAmount = discountArray[0];
            }
            createTransaction = false;
            
            itemTotal = transaction.getTransactionTotals().getDiscountEligibleSubtotal();
        }
        else
        {            
            createTransaction = true;
        }

    }

    //----------------------------------------------------------------------
    /**
       Unloads data from shuttle. <P>
       @param  bus     Service Bus
    **/
    //----------------------------------------------------------------------
    public void unload(BusIfc bus)
    {

        // unload financial cargo
        super.unload(bus);

        // retrieve the child cargo
        ModifyTransactionDiscountCargo cargo;
        cargo = (ModifyTransactionDiscountCargo)bus.getCargo();

        //cargo.setSalesAssociate(salesAssociate);
        cargo.setDiscount(discountAmount);
        cargo.setDiscountType(DiscountRuleConstantsIfc.DISCOUNT_METHOD_AMOUNT);
        cargo.setCreateTransaction(createTransaction);
        cargo.setItemTotal(itemTotal);
        cargo.setEmployeeDiscountID(employeeDiscountID);
        if(createTransaction == false)
        {
            cargo.setTransaction(transaction);
        }

    }
    
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
                    DiscountRuleConstantsIfc.ASSIGNMENT_MANUAL);
        return discountArray;
    }
}
