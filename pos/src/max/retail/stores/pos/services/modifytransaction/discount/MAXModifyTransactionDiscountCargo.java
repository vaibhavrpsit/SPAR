/********************************************************************************
 *   
 *	Copyright (c) 2016 - 2017 MAX Hypermarket, Inc    All Rights Reserved.
 *	
 *
 *
 *	Rev	1.0 	Nov 30, 2016		Mansi Goel		Changes for Discount Rule FES	
 *
 ********************************************************************************/

package max.retail.stores.pos.services.modifytransaction.discount;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;

import org.apache.log4j.Logger;

import max.retail.stores.domain.discount.MAXAdvancedPricingRuleIfc;
import oracle.retail.stores.commerceservices.common.currency.CurrencyIfc;
import oracle.retail.stores.common.utility.LocaleMap;
import oracle.retail.stores.common.utility.Util;
import oracle.retail.stores.domain.DomainGateway;
import oracle.retail.stores.domain.discount.DiscountRuleConstantsIfc;
import oracle.retail.stores.domain.discount.ItemDiscountStrategyIfc;
import oracle.retail.stores.domain.discount.TransactionDiscountByAmountIfc;
import oracle.retail.stores.domain.discount.TransactionDiscountByPercentageIfc;
import oracle.retail.stores.domain.discount.TransactionDiscountStrategyIfc;
import oracle.retail.stores.domain.employee.EmployeeIfc;
import oracle.retail.stores.domain.lineitem.SaleReturnLineItemIfc;
import oracle.retail.stores.domain.transaction.SaleReturnTransactionIfc;
import oracle.retail.stores.domain.utility.LocaleConstantsIfc;
import oracle.retail.stores.foundation.manager.ifc.JournalManagerIfc;
import oracle.retail.stores.foundation.manager.ifc.ParameterManagerIfc;
import oracle.retail.stores.foundation.manager.parameter.ParameterException;
import oracle.retail.stores.foundation.tour.gate.Gateway;
import oracle.retail.stores.pos.journal.JournalFormatterManagerIfc;
import oracle.retail.stores.pos.services.modifytransaction.discount.ModifyTransactionDiscountCargo;
import oracle.retail.stores.pos.services.pricing.PricingCargo;
import oracle.retail.stores.utility.I18NConstantsIfc;
import oracle.retail.stores.utility.I18NHelper;
import oracle.retail.stores.utility.JournalConstantsIfc;

public class MAXModifyTransactionDiscountCargo extends ModifyTransactionDiscountCargo {

	private static final long serialVersionUID = 7257512848967013421L;
	protected boolean isAutoEmpDiscount = false;
	protected boolean grantAccessforInvoicerules = false;
	protected boolean invoiceRuleAlreadyApplied = false;
	protected ArrayList<MAXAdvancedPricingRuleIfc> invoiceDiscounts;
	protected BigDecimal invoiceRuleAppliedRate = new BigDecimal("0.00");
	protected boolean clearDiscount = false;
	
	protected String spclEmpDisc = null;
	protected String itemId = null;
	public  boolean ruleApplied=false;

	

	public boolean isRuleApplied() {
		return ruleApplied;
	}

	public void setRuleApplied(boolean ruleApplied) {
		this.ruleApplied = ruleApplied;
	}

	public String getItemId() {
		return itemId;
	}

	public void setItemId(String itemId) {
		this.itemId = itemId;
	}

	public String getSpclEmpDisc() {
		return spclEmpDisc;
	}

	public void setSpclEmpDisc(String spclEmpDisc) {
		this.spclEmpDisc = spclEmpDisc;
	}

	public boolean isAutoEmpDiscount() {
		return isAutoEmpDiscount;
	}

	public boolean isGrantAccessforInvoicerules() {
		return grantAccessforInvoicerules;
	}

	public void setGrantAccessforInvoicerules(boolean grantAccessforInvoicerules) {
		this.grantAccessforInvoicerules = grantAccessforInvoicerules;
	}

	public void setAutoEmpDiscount(boolean isAutoEmpDiscount) {
		this.isAutoEmpDiscount = isAutoEmpDiscount;
	}

	protected String empOrgnID = null;

	public String getEmpOrgnID() {
		return empOrgnID;
	}

	public void setEmpOrgnID(String empOrgnID) {
		this.empOrgnID = empOrgnID;
	}

	protected boolean employeeRemoveSelected = false;

	public boolean isEmployeeRemoveSelected() {
		return employeeRemoveSelected;
	}

	public void setEmployeeRemoveSelected(boolean employeeRemoveSelected) {
		this.employeeRemoveSelected = employeeRemoveSelected;
	}

	protected String employeeDiscountMethod = null;

	public String getEmployeeDiscountMethod() {
		return employeeDiscountMethod;
	}

	public void setEmployeeDiscountMethod(String employeeDiscountMethod) {
		this.employeeDiscountMethod = employeeDiscountMethod;
	}

	public BigDecimal getInvoiceRuleAppliedRate() {
		return invoiceRuleAppliedRate;
	}

	public void setInvoiceRuleAppliedRate(BigDecimal invoiceRuleAppliedRate) {
		this.invoiceRuleAppliedRate = invoiceRuleAppliedRate;
	}

	public boolean isInvoiceRuleAlreadyApplied() {
		return invoiceRuleAlreadyApplied;
	}

	public void setInvoiceRuleAlreadyApplied(boolean invoiceRuleAlreadyApplied) {
		this.invoiceRuleAlreadyApplied = invoiceRuleAlreadyApplied;
	}

	CurrencyIfc invoiceDiscountAmount = DomainGateway.getBaseCurrencyInstance();

	public CurrencyIfc getInvoiceDiscountAmount() {
		return invoiceDiscountAmount;
	}

	public void setInvoiceDiscountAmount(CurrencyIfc invoiceDiscountAmount) {
		this.invoiceDiscountAmount = invoiceDiscountAmount;
	}

	//Changes for Rev 1.0 : Starts
	public ArrayList<MAXAdvancedPricingRuleIfc> getInvoiceDiscounts() {
		return invoiceDiscounts;
	}

	public void setInvoiceDiscounts(ArrayList<MAXAdvancedPricingRuleIfc> invoiceDiscounts) {
		this.invoiceDiscounts = invoiceDiscounts;
	}
	//Changes for Rev 1.0 : Ends

	public void setClearDiscount(boolean value) {
		clearDiscount = value;
	}
	
	//changes by kamlesh for billbuster starts
	protected SaleReturnLineItemIfc[] items;
	
	protected SaleReturnLineItemIfc item;
	
	 public SaleReturnLineItemIfc[] getItems()
	    {
	        return items;
	    }

	    /**
	     * Sets list of line items.
	     * 
	     * @param values of list of line item
	     */
	    public void setItems(SaleReturnLineItemIfc[] values)
	    {
	        items = values;
	    }
	    
	    public void setItem(SaleReturnLineItemIfc value)
	    {
	        item = value;
	    }

	    /**
	     * Retrieves line item.
	     * 
	     * @return line item
	     */
	    public SaleReturnLineItemIfc getItem()
	    {
	        return item;
	    }
	    
	    public boolean isOnlyOneDiscountAllowed(ParameterManagerIfc pm, Logger logger)
	    {
	        boolean isOnlyOneDiscount = false;
	        String parameterValue = "";

	        // retrieve Maximum Number of Discounts allowed from parameter file
	        try
	        {
	            parameterValue = pm.getStringValue(PricingCargo.MAX_DISCOUNTS_ALLOWED);
	            parameterValue.trim();
	            if (PricingCargo.ONE_TOTAL.equals(parameterValue))
	            {
	                isOnlyOneDiscount = true;
	            }
	            else if (!PricingCargo.ONE_OF_EACH_TYPE.equals(parameterValue))
	            {
	                logger.error(
	                        "Parameter read: "
	                        + PricingCargo.MAX_DISCOUNTS_ALLOWED
	                        + "=[" + parameterValue + "]");
	            }
	        }
	        catch (ParameterException e)
	        {
	            logger.error(Util.throwableToString(e));
	        }

	        return isOnlyOneDiscount;
	    }

	    
	    public void removeAllManualDiscounts(SaleReturnLineItemIfc srli, JournalManagerIfc journal)
	    {
	        Locale locale  =LocaleMap.getLocale(LocaleConstantsIfc.JOURNAL);
	        SaleReturnLineItemIfc[] lineItems = null;
	        // Set the entered Sale Return Line Item in an array of one,
	        // or if it's null, get the line items from the transaction
	        if (srli == null && getTransaction() != null)
	        {
	            lineItems = (SaleReturnLineItemIfc[])((SaleReturnTransactionIfc)getTransaction()).getLineItems();
	            setItems(lineItems);
	        }
	        else
	        {
	            lineItems = new SaleReturnLineItemIfc[]{srli};
	        }
	        removeAllManualDiscounts(lineItems, journal);

	        if (getTransaction() != null)
	        {
	            // Journal deletion of transaction discounts by amount
	            TransactionDiscountStrategyIfc[] discounts1 = ((SaleReturnTransactionIfc)getTransaction()).getTransactionDiscounts(
	                    DiscountRuleConstantsIfc.DISCOUNT_METHOD_AMOUNT,
	                    DiscountRuleConstantsIfc.ASSIGNMENT_MANUAL);
	            TransactionDiscountStrategyIfc[] discounts2 = ((SaleReturnTransactionIfc)getTransaction()).getTransactionDiscounts(
	                    DiscountRuleConstantsIfc.DISCOUNT_METHOD_AMOUNT,
	                    DiscountRuleConstantsIfc.ASSIGNMENT_EMPLOYEE);
	            TransactionDiscountStrategyIfc[] discounts = new TransactionDiscountStrategyIfc[discounts1.length + discounts2.length];
	            System.arraycopy(discounts1,0,discounts,0,discounts1.length);
	            System.arraycopy(discounts2,0,discounts,discounts1.length,discounts2.length);
	            if (journal != null)
	            {
	                for (int i = 0; i < discounts.length; i++)
	                {
	                    TransactionDiscountStrategyIfc discount = discounts[i];

	                    if (discount instanceof TransactionDiscountByAmountIfc)
	                    {
	                        TransactionDiscountByAmountIfc discountAmount =
	                            (TransactionDiscountByAmountIfc)discount;
	                        CurrencyIfc discountCurr = discountAmount.getDiscountAmount();
	                        String discountAmountStr =
	                            discountCurr.toGroupFormattedString().trim();
	                        StringBuffer msg = new StringBuffer();

	                        String msgType = "";
	                        msg.append(Util.EOL);
	                        if (discount.getAssignmentBasis() == DiscountRuleConstantsIfc.ASSIGNMENT_EMPLOYEE)
	                        {
	                            msgType = JournalConstantsIfc.EMPLOYEE_DISCOUNT;
	                        }
	                        else
	                        {
	                            msgType=JournalConstantsIfc.TRANS_DISCOUNT;
	                        }
	                        Object discountDataArgs[] = {discountAmountStr};

	                        String discountType = I18NHelper.getString(I18NConstantsIfc.EJOURNAL_TYPE, msgType, discountDataArgs);

	                        String discDeleted = I18NHelper.getString(I18NConstantsIfc.EJOURNAL_TYPE, JournalConstantsIfc.DISC_DELETED, null);

	                        msg.append(discountType);
	                        msg.append(Util.EOL);
	                        msg.append(discDeleted);
	                        msg.append(Util.EOL);

	                        if (discount.getAssignmentBasis() == DiscountRuleConstantsIfc.ASSIGNMENT_EMPLOYEE)
	                        {
	                            Object empDataArgs[] = {discount.getDiscountEmployeeID()};
	                            String empId=I18NHelper.getString(I18NConstantsIfc.EJOURNAL_TYPE, JournalConstantsIfc.EMPLOYEE_ID, empDataArgs);

	                            msg.append(empId);
	                        }
	                        else
	                        {
	                            // This needs to be modified
	                            String reasonCodeText = discountAmount.getReason().getText(locale);
	                            Object reasonCodeDataArgs[]={reasonCodeText};


	                            String reasonCode=I18NHelper.getString(I18NConstantsIfc.EJOURNAL_TYPE, JournalConstantsIfc.DISC_REASON_CODE_LABEL, reasonCodeDataArgs);

	                            msg.append(reasonCode);
	                        }

	                        String str = "";
	                        journal.journal(str, str, msg.toString());
	                    } // if the discount is a transaction discount by amount
	                } // end discounts for loop
	            } // end if journal not null
	            // Clear transaction of discounts by amount
	            ((SaleReturnTransactionIfc)getTransaction()).clearTransactionDiscounts(DiscountRuleConstantsIfc.DISCOUNT_METHOD_AMOUNT,
	                    DiscountRuleConstantsIfc.ASSIGNMENT_MANUAL);
	            ((SaleReturnTransactionIfc)getTransaction()).clearTransactionDiscounts(DiscountRuleConstantsIfc.DISCOUNT_METHOD_AMOUNT,
	                    DiscountRuleConstantsIfc.ASSIGNMENT_EMPLOYEE);

	            // Journal deletion of transaction discounts by percentage
	            discounts1 = ((SaleReturnTransactionIfc)getTransaction()).getTransactionDiscounts(
	                    DiscountRuleConstantsIfc.DISCOUNT_METHOD_PERCENTAGE,
	                    DiscountRuleConstantsIfc.ASSIGNMENT_MANUAL);
	            discounts2 = ((SaleReturnTransactionIfc)getTransaction()).getTransactionDiscounts(
	                    DiscountRuleConstantsIfc.DISCOUNT_METHOD_PERCENTAGE,
	                    DiscountRuleConstantsIfc.ASSIGNMENT_EMPLOYEE);
	            discounts = new TransactionDiscountStrategyIfc[discounts1.length + discounts2.length];
	            System.arraycopy(discounts1,0,discounts,0,discounts1.length);
	            System.arraycopy(discounts2,0,discounts,discounts1.length,discounts2.length);
	            if (journal != null)
	            {
	                for (int i = 0; i < discounts.length; i++)
	                {
	                    TransactionDiscountStrategyIfc discount = discounts[i];

	                    if (discount instanceof TransactionDiscountByPercentageIfc)
	                    {
	                        TransactionDiscountByPercentageIfc discountPercent =
	                            (TransactionDiscountByPercentageIfc)discount;
	                        double discountPercentDbl = 100.0 * discountPercent.getDiscountRate().doubleValue();
	                        String discountPercentStr = (new Double(discountPercentDbl)).toString().trim();

	                        // journal removal of discount
	                        StringBuffer msg = new StringBuffer();
	                        msg.append(Util.EOL);
	                        String msgType ="";
	                        if (discount.getAssignmentBasis() == DiscountRuleConstantsIfc.ASSIGNMENT_EMPLOYEE)
	                        {
	                            msgType = JournalConstantsIfc.EMPLOYEE_DISCOUNT;
	                        }
	                        else
	                        {
	                            msgType=JournalConstantsIfc.TRANS_DISCOUNT;
	                        }
	                        Object discountDataArgs[] = {""};

	                        String discountType = I18NHelper.getString(I18NConstantsIfc.EJOURNAL_TYPE, msgType, discountDataArgs);

	                        Object discPercDataArgs[]= {discountPercentStr};

	                        String discDeleted = I18NHelper.getString(I18NConstantsIfc.EJOURNAL_TYPE, JournalConstantsIfc.DISC_PER_DELETED, discPercDataArgs);

	                        msg.append(discountType);
	                        msg.append(Util.EOL);
	                        msg.append(discDeleted);
	                        msg.append(Util.EOL);

	                        if (discount.getAssignmentBasis() == DiscountRuleConstantsIfc.ASSIGNMENT_EMPLOYEE)
	                        {
	                            Object empDataArgs[] = {discount.getDiscountEmployeeID()};
	                            String empId=I18NHelper.getString(I18NConstantsIfc.EJOURNAL_TYPE, JournalConstantsIfc.EMPLOYEE_ID, empDataArgs);

	                            msg.append(empId);
	                        }
	                        else
	                        {
	                            // This needs to be modified
	                            String reasonCodeText = discountPercent.getReason().getText(locale);
	                            Object reasonCodeDataArgs[]={reasonCodeText};


	                            String reasonCode=I18NHelper.getString(I18NConstantsIfc.EJOURNAL_TYPE, JournalConstantsIfc.DISC_REASON_CODE_LABEL, reasonCodeDataArgs);

	                            msg.append(reasonCode);
	                        }

	                        String str = "";
	                        journal.journal(str, str, msg.toString());
	                    } // end if discount is transaction discount by percentage
	                } // end discounts for loop
	            } // end if journal isn't null

	            // Clear transaction of discounts by percentage
	            ((SaleReturnTransactionIfc)getTransaction()).clearTransactionDiscounts(DiscountRuleConstantsIfc.DISCOUNT_METHOD_PERCENTAGE,
	                    DiscountRuleConstantsIfc.ASSIGNMENT_MANUAL);
	            ((SaleReturnTransactionIfc)getTransaction()).clearTransactionDiscounts(DiscountRuleConstantsIfc.DISCOUNT_METHOD_PERCENTAGE,
	                    DiscountRuleConstantsIfc.ASSIGNMENT_EMPLOYEE);
	        } // end if a transaction exists
	    }
	    
	    private void removeAllManualDiscounts(SaleReturnLineItemIfc[] lineItems, JournalManagerIfc journal)
	    {
	        JournalFormatterManagerIfc formatter =
	            (JournalFormatterManagerIfc)Gateway.getDispatcher().getManager(JournalFormatterManagerIfc.TYPE);
	        for (int x = 0; lineItems != null && x < lineItems.length; x++)
	        {
	            SaleReturnLineItemIfc srli = lineItems[x];
	            if (srli != null)
	            {
	                if (journal != null)
	                {
	                    // Journal the removal of item discounts by amount (includes Markdowns)
	                    ItemDiscountStrategyIfc[] currentDiscounts = srli.getItemPrice().getItemDiscountsByAmount();
	                    if((currentDiscounts != null) && (currentDiscounts.length > 0))
	                    {
	                        // find the percent discount stategy that is a discount.
	                        for (int j = 0; j < currentDiscounts.length; j++)
	                        {
	                            if (currentDiscounts[j].getAccountingMethod() ==
	                                DiscountRuleConstantsIfc.ACCOUNTING_METHOD_DISCOUNT ||
	                                currentDiscounts[j].getAccountingMethod() ==
	                                    DiscountRuleConstantsIfc.ACCOUNTING_METHOD_MARKDOWN)
	                            {
	                                // write to the journal
	                                journal.journal(getOperator().getEmployeeID(),
	                                        getTransaction().getTransactionID(),
	                                        formatter.toJournalManualDiscount(srli, currentDiscounts[j], true));
	                            }
	                        }
	                    } // end if current discounts isn't empty - looking for discounts by amount

	                    // Journal the removal of item discounts by percentage (includes Markdowns)
	                    currentDiscounts = srli.getItemPrice().getItemDiscountsByPercentage();
	                    if((currentDiscounts != null) && (currentDiscounts.length > 0))
	                    {
	                        // find the percent discount stategy that is a discount.
	                        for (int j = 0; j < currentDiscounts.length; j++)
	                        {
	                            if (currentDiscounts[j].getAccountingMethod() ==
	                                DiscountRuleConstantsIfc.ACCOUNTING_METHOD_DISCOUNT ||
	                                currentDiscounts[j].getAccountingMethod() ==
	                                    DiscountRuleConstantsIfc.ACCOUNTING_METHOD_MARKDOWN)
	                            {

	                                // write to the journal
	                                journal.journal(getOperator().getEmployeeID(),
	                                		getTransaction().getTransactionID(),
	                                        formatter.toJournalManualDiscount(srli, currentDiscounts[j], true));
	                            }
	                        }
	                    } // end if current discounts not empty - looking for discounts by percentage
	                } // end if journal is not null

	                // Clear Employee discounts
	                srli.clearItemDiscountsByAmount(DiscountRuleConstantsIfc.DISCOUNT_APPLICATION_TYPE_ITEM,
	                        DiscountRuleConstantsIfc.ASSIGNMENT_EMPLOYEE,
	                        false);
	                srli.clearItemDiscountsByPercentage(DiscountRuleConstantsIfc.DISCOUNT_APPLICATION_TYPE_ITEM,
	                        DiscountRuleConstantsIfc.ASSIGNMENT_EMPLOYEE,
	                        false);

	                // clear item discounts
	                srli.clearItemDiscountsByAmount(DiscountRuleConstantsIfc.DISCOUNT_APPLICATION_TYPE_ITEM,
	                        DiscountRuleConstantsIfc.ASSIGNMENT_MANUAL,
	                        false);
	                srli.clearItemDiscountsByPercentage(DiscountRuleConstantsIfc.DISCOUNT_APPLICATION_TYPE_ITEM,
	                        DiscountRuleConstantsIfc.ASSIGNMENT_MANUAL,
	                        false);

	                // Clear Damage Discounts
	                srli.clearItemDiscountsByAmount(DiscountRuleConstantsIfc.DISCOUNT_APPLICATION_TYPE_ITEM,
	                        DiscountRuleConstantsIfc.ASSIGNMENT_MANUAL,
	                        true);
	                srli.clearItemDiscountsByPercentage(DiscountRuleConstantsIfc.DISCOUNT_APPLICATION_TYPE_ITEM,
	                        DiscountRuleConstantsIfc.ASSIGNMENT_MANUAL,
	                        true);

	                // Clear markdowns
	                srli.clearItemMarkdownsByAmount(DiscountRuleConstantsIfc.DISCOUNT_APPLICATION_TYPE_ITEM);
	                srli.clearItemMarkdownsByPercentage(DiscountRuleConstantsIfc.DISCOUNT_APPLICATION_TYPE_ITEM);
	            } // end if srli not null
	        } // end line item for loop
	    }
	    
	    protected HashMap<Integer,ItemDiscountStrategyIfc> validatedDiscountHashMap = new HashMap<Integer,ItemDiscountStrategyIfc>(1);

	    public HashMap<Integer,ItemDiscountStrategyIfc> getValidDiscounts()
	    {
	        return validatedDiscountHashMap;
	    }

	    /**
	     * Sets list of validated discounts.
	     * 
	     * @param value of validated discounts
	     */
	    public void setValidDiscounts(HashMap<Integer,ItemDiscountStrategyIfc> value)
	    {
	        if (value != null)
	        {
	            validatedDiscountHashMap = value;
	        }
	        else
	        {
	            validatedDiscountHashMap.clear();
	        }
	    }

	    //ends
	 // Added by kamlesh pant for manager override
 		protected EmployeeIfc lastOperator = null;
 		
 		public void setLastOperator(EmployeeIfc value) {
 			       this.lastOperator = value;
 			   }
 		public EmployeeIfc getLastOperator() {
 			       return this.lastOperator;
 			   }
}
