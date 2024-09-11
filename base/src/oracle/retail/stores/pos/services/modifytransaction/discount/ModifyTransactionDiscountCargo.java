/* ===========================================================================
* Copyright (c) 1998, 2011, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/modifytransaction/discount/ModifyTransactionDiscountCargo.java /main/13 2011/02/16 09:13:32 cgreene Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED (MM/DD/YY)
 *    cgreen 02/15/11 - move constants into interfaces and refactor
 *    cgreen 05/27/10 - convert to oracle packaging
 *    cgreen 05/26/10 - convert to oracle packaging
 *    cgreen 04/27/10 - updating deprecated names
 *    abonda 01/03/10 - update header date
 *    acadar 10/30/08 - cleanup
 *    acadar 10/30/08 - localization of reason codes for manual transaction
 *                      discounts
 * ===========================================================================
     $Log:
      4    360Commerce 1.3         4/25/2007 8:52:22 AM   Anda D. Cadar   I18N
           merge

      3    360Commerce 1.2         3/31/2005 4:29:04 PM   Robert Pearse
      2    360Commerce 1.1         3/10/2005 10:23:35 AM  Robert Pearse
      1    360Commerce 1.0         2/11/2005 12:12:41 PM  Robert Pearse
     $
     Revision 1.7  2004/09/27 22:32:04  bwf
     @scr 7244 Merged 2 versions of abstractfinancialcargo.

     Revision 1.6  2004/07/06 16:50:06  cdb
     @scr 5337 General cleanup.

     Revision 1.5  2004/03/22 19:27:00  cdb
     @scr 3588 Updating javadoc comments

     Revision 1.4  2004/03/03 21:03:45  cdb
     @scr 3588 Added employee transaction discount service.

     Revision 1.3  2004/02/12 16:51:10  mcs
     Forcing head revision

     Revision 1.2  2004/02/11 21:51:48  rhafernik
     @scr 0 Log4J conversion and code cleanup

     Revision 1.1.1.1  2004/02/11 01:04:18  cschellenger
     updating to pvcs 360store-current


 *
 *    Rev 1.3   Nov 25 2003 13:09:08   nrao
 * Javadoc.
 *
 *    Rev 1.2   Oct 31 2003 12:31:08   nrao
 * Added methods to facilitate Instant Credit Enrollment.
 *
 *    Rev 1.1   Oct 17 2003 10:50:48   bwf
 * Added employeeDiscountID.
 * Resolution for 3412: Feature Enhancement: Employee Discount
 *
 *    Rev 1.0   Aug 29 2003 16:02:26   CSchellenger
 * Initial revision.
 *
 *    Rev 1.2   Jun 02 2003 15:35:30   bwf
 * Add an item total for validation purposes.
 * Resolution for 2522: Changing the transaction discount from an amount of 9999999.99 to 100% results in a discount of 9999999.99 plus the price of the item.
 *
 *    Rev 1.1   Feb 13 2003 15:40:24   crain
 * Removed deprecated calls from getReasonCodes()
 * Resolution for 1907: Remove deprecated calls to AbstractFinancialCargo.getCodeListMap()
 *
 *    Rev 1.0   Apr 29 2002 15:16:30   msg
 * Initial revision.
 *
 *    Rev 1.0   Mar 18 2002 11:38:50   msg
 * Initial revision.
 *
 *    Rev 1.1   17 Jan 2002 14:14:20   pjf
 * Modified to use new security access service, deprecated previous security classes, corrected SCRs 404,405.
 * Resolution for POS SCR-404: Security Override continually loops in Trans Disc Amt
 *
 *    Rev 1.0   Sep 21 2001 11:30:42   msg
 * Initial revision.
 *
 *    Rev 1.1   Sep 17 2001 13:09:36   msg
 * header update
 */
package oracle.retail.stores.pos.services.modifytransaction.discount;

import oracle.retail.stores.commerceservices.common.currency.CurrencyIfc;
import oracle.retail.stores.domain.discount.TransactionDiscountStrategyIfc;
import oracle.retail.stores.domain.employee.EmployeeIfc;
import oracle.retail.stores.domain.employee.RoleFunctionIfc;
import oracle.retail.stores.domain.transaction.RetailTransactionIfc;
import oracle.retail.stores.domain.transaction.SaleReturnTransactionIfc;
import oracle.retail.stores.domain.utility.CodeListIfc;
import oracle.retail.stores.pos.services.common.AbstractFinancialCargo;
import oracle.retail.stores.pos.services.common.TransactionCargoIfc;
import oracle.retail.stores.pos.services.pricing.DiscountCargoIfc;

/**
 * This class holds data for the Modify Transaction Discount service.
 * 
 * @version $Revision: /main/13 $
 */
public class ModifyTransactionDiscountCargo extends AbstractFinancialCargo
    implements TransactionCargoIfc, DiscountCargoIfc
{
    private static final long serialVersionUID = -3631731300386585239L;

    /**
     * revision number of this class
     */
    public static final String revisionNumber = "$Revision: /main/13 $";

    /**
     * the transaction
     */
    protected RetailTransactionIfc transaction;

    /**
     * the sales associate
     */
    protected EmployeeIfc salesAssociate;

    /**
     * flag to indicate whether the service created a transaction
     */
    protected boolean transactionCreated = false;

    /**
     * flag to indicate whether the service needs to create a transaction
     */
    protected boolean createTransaction = false;

    /**
     * flag to see if there was a discount done in the service
     */
    protected boolean doDiscount = false;

    /**
     * interface for the transaction discount the interface is null if no
     * existing discount exists, set in the launch shuttle of the calling
     * service
     */
    protected TransactionDiscountStrategyIfc discount;

    /**
     * old discount
     */
    protected TransactionDiscountStrategyIfc oldDiscount;

    /**
     * This is the type of discount - set in the launch shuttle
     */
    protected int discountType;

    /**
     * manual transaction discount amount reason codes
     */
    protected CodeListIfc localizedDiscountAmountReasonCodes;

    /**
     * manual transaction discount percent reason codes
     */
    protected CodeListIfc localizedDiscountPercentReasonCodes;

    /**
     * transaction subtotal
     */
    protected CurrencyIfc itemTotal = null;

    /**
     * employee discount id
     */
    protected String employeeDiscountID = null;

    /**
     * The original sale return transaction
     */
    protected SaleReturnTransactionIfc originalTransaction;

    /**
     * instant credit discount
     */
    protected boolean instantCreditDiscount = false;

    /**
     * Gets the manual discount percent reason codes
     */
    public CodeListIfc getLocalizedDiscountPercentReasonCodes()
    {
        return localizedDiscountPercentReasonCodes;
    }

    /**
     * Sets the manual discount percent reason codes
     */
    public void setLocalizedDiscountPercentReasonCodes(CodeListIfc list)
    {
        localizedDiscountPercentReasonCodes = list;
    }

    /**
     * Gets the manual discount percent reason codes
     */
    public CodeListIfc getLocalizedDiscountAmountReasonCodes()
    {
        return localizedDiscountAmountReasonCodes;
    }

    /**
     * Sets the manual discount percent reason codes
     */
    public void setLocalizedDiscountAmountReasonCodes(CodeListIfc list)
    {
        localizedDiscountAmountReasonCodes = list;
    }

    /**
     * Returns the function ID whose access is to be checked.
     * 
     * @return int Role Function ID
     */
    public int getAccessFunctionID()
    {
        return RoleFunctionIfc.DISCOUNT;
    }

    /**
     * Sets the sales associate.
     * 
     * @param employee the sales associate
     */
    public void setSalesAssociate(EmployeeIfc employee)
    {
        salesAssociate = employee;
    }

    /**
     * Returns the sales associate.
     * 
     * @return the sales associate
     */
    public EmployeeIfc getSalesAssociate()
    {
        return (salesAssociate);
    }

    /**
     * Sets the retail transaction.
     * 
     * @param trans the retail transaction
     */
    public void setTransaction(RetailTransactionIfc trans)
    {
        transaction = trans;
    }

    /**
     * Returns the retail transaction.
     * 
     * @return the retail transaction
     */
    public RetailTransactionIfc getTransaction()
    {
        return (transaction);
    }

    /**
     * Sets whether the retail transaction was created in this service.
     * 
     * @param value true if the transaction was created, false otherwise
     */
    public void setTransactionCreated(boolean value)
    {
        transactionCreated = value;
    }

    /**
     * Returns whether the retail transaction was created in this service.
     * 
     * @return true if the transaction was created, false otherwise
     */
    public boolean getTransactionCreated()
    {
        return (transactionCreated);
    }

    /**
     * Sets whether the retail transaction can be created in this service.
     * 
     * @param value true if the transaction can be created, false otherwise
     */
    public void setCreateTransaction(boolean value)
    {
        createTransaction = value;
    }

    /**
     * Returns whether the retail transaction can be created in this service.
     * 
     * @return true if the transaction can be created, false otherwise
     */
    public boolean createTransaction()
    {
        return (createTransaction);
    }

    /**
     * Sets the discount-done flag.
     * 
     * @param value the discount-done flag
     */
    public void setDoDiscount(boolean value)
    {
        doDiscount = value;
    }

    /**
     * Returns the discount-done flag.
     * 
     * @return the discount-done flag
     */
    public boolean getDoDiscount()
    {
        return (doDiscount);
    }


    /**
     * Sets the discount object.
     * 
     * @param value the discount object
     */
    public void setDiscount(TransactionDiscountStrategyIfc value)
    {
        // first, save old discount
        oldDiscount = getDiscount();
        // then set to new discount
        discount = value;
    }

    /**
     * Returns the discount object.
     * 
     * @return the discount object
     */
    public TransactionDiscountStrategyIfc getDiscount()
    {
        return (discount);
    }

    /**
     * Returns the old discount object.
     * 
     * @return the old discount object
     */
    public TransactionDiscountStrategyIfc getOldDiscount()
    {
        return (oldDiscount);
    }

    /**
     * Sets the discount type.
     * 
     * @param value the discount type
     */
    public void setDiscountType(int value)
    {
        discountType = value;
    }

    /**
     * Returns the discount type.
     * 
     * @return the discount type
     */
    public int getDiscountType()
    {
        return (discountType);
    }

    /**
     * Sets the item total of the transaction.
     * 
     * @param itmTtls the CurrencyIfc value
     */
    public void setItemTotal(CurrencyIfc itmTtls)
    {
        itemTotal = itmTtls;
    }

    /**
     * Returns the item total of the transaction.
     * 
     * @return the CurrencyIfc value
     */
    public CurrencyIfc getItemTotal()
    {
        return itemTotal;
    }

    /**
     * Returns the employee discount id.
     * 
     * @return employeeID String;
     */
    public String getEmployeeDiscountID()
    {
        return employeeDiscountID;
    }

    /**
     * Sets the employee discount id.
     * 
     * @param employeeDiscountID The employee id associated with an employee
     *            discount
     */
    public void setEmployeeDiscountID(String employeeDiscountID)
    {
        this.employeeDiscountID = employeeDiscountID;
    }

    /**
     * Returns original Transaction
     * 
     * @return SaleReturnTransactionIfc
     */
    public SaleReturnTransactionIfc getOriginalTransaction()
    {
        return originalTransaction;
    }

    /**
     * Sets original Transaction
     * 
     * @param value SaleReturnTransactionIfc
     */
    public void setOriginalTransaction(SaleReturnTransactionIfc value)
    {
        originalTransaction = value;
    }

    /**
     * Returns boolean value for instantCreditDiscount
     * 
     * @return boolean
     */
    public boolean isInstantCreditDiscount()
    {
        return instantCreditDiscount;
    }

    /**
     * sets boolean value for instantCreditDiscount
     * 
     * @param ic boolean
     */
    public void setInstantCreditDiscount(boolean ic)
    {
        instantCreditDiscount = ic;
    }
}
