/********************************************************************************
 *   
 *	Copyright (c) 2016 - 2017 MAX Hypermarket, Inc    All Rights Reserved.
 *	
 *
 *	Rev 1.1		Mar 24, 2017		Mansi Goel		Changes to resolve manual discount gets removed when bill buster rules are active
 *	Rev	1.0 	Nov 30, 2016		Mansi Goel		Changes for Discount Rule FES	
 *
 ********************************************************************************/

package max.retail.stores.pos.services.pricing;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import max.retail.stores.domain.discount.MAXAdvancedPricingRuleIfc;
import max.retail.stores.domain.discount.MAXDiscountRuleConstantsIfc;
import max.retail.stores.domain.employee.MAXRoleFunctionIfc;
import max.retail.stores.domain.stock.MAXPLUItem;
import max.retail.stores.pos.services.modifytransaction.discount.MAXModifyTransactionDiscountCargo;
import max.retail.stores.pos.services.sale.MAXSaleCargo;
import oracle.retail.stores.commerceservices.common.currency.CurrencyIfc;
import oracle.retail.stores.domain.DomainGateway;
import oracle.retail.stores.domain.discount.DiscountRuleConstantsIfc;
import oracle.retail.stores.domain.discount.TransactionDiscountStrategyIfc;
import oracle.retail.stores.domain.employee.EmployeeIfc;
import oracle.retail.stores.domain.employee.RoleFunctionIfc;
import oracle.retail.stores.domain.lineitem.SaleReturnLineItemIfc;
import oracle.retail.stores.domain.transaction.SaleReturnTransactionIfc;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.pos.services.common.FinancialCargoShuttle;

import org.apache.log4j.Logger;

public class MAXModifyInvoiceTypeTransactionDiscountPercentLaunchShuttle extends FinancialCargoShuttle {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * The logger to which log messages will be sent.
	 **/
	protected static Logger logger = Logger
			.getLogger(max.retail.stores.pos.services.pricing.MAXModifyTransactionDiscountPercentLaunchShuttle.class);

	/**
	 * revision number of this class
	 **/
	public static final String revisionNumber = "$Revision: 1.2 $";

	/**
	 * the default sales associate
	 **/
	protected EmployeeIfc salesAssociate;

	/**
	 * discount object
	 **/
	protected TransactionDiscountStrategyIfc discountPercent;

	// Added for invoice type rules..start
	List invoiceRuleList = new ArrayList();
	List eligibleRuleList = new ArrayList();
	protected ArrayList<MAXAdvancedPricingRuleIfc> invoiceRules;
	boolean flag = false;
	protected boolean createTransaction;

	/**
	 * Employee Discount ID
	 **/
	protected String employeeDiscountID;

	/**
	 * Transaction to check against for transaction discounts
	 */
	protected SaleReturnTransactionIfc transaction;

	protected boolean invoiceRuleAlreadyApplied = false;
	protected BigDecimal invoiceRuleAppliedRate = new BigDecimal("0.00");
	CurrencyIfc discountAmount = DomainGateway.getBaseCurrencyInstance();

	// ----------------------------------------------------------------------
	/**
	 * Loads data from service.
	 * <P>
	 * 
	 * @param bus
	 *            Service Bus
	 **/
	// ----------------------------------------------------------------------
	public void load(BusIfc bus) {

		super.load(bus);

		MAXSaleCargo cargo = (MAXSaleCargo) bus.getCargo();

		cargo.setInitialOriginStationLetter(bus.getCurrentLetter().getName());
		// Changes for Rev 1.0 : Starts
		try {
			invoiceRules = ((MAXPLUItem) cargo.getPLUItem()).getInvoiceDiscounts();
		} catch (Exception e) {

		}
		if (invoiceRules != null && invoiceRules.size() != 0) {
			invoiceRuleAppliedRate = invoiceRules.get(0).getDiscountRate();
			discountAmount = invoiceRules.get(0).getDiscountAmount();
		}

		// get transaction
		transaction = (SaleReturnTransactionIfc) cargo.getTransaction();

		if (transaction != null) {
			TransactionDiscountStrategyIfc[] discountArray;
			discountArray = getDiscounts(transaction);
			//Changes for Rev 1.1 : Starts
			if (invoiceRules != null)
				this.invoiceRules.size();
			//Changes for Rev 1.1 : Ends
			discountPercent = null;
			if (discountArray != null) {
				if (discountArray.length > 0) {
					discountPercent = discountArray[0];
					// fix for bug 7407
					if (invoiceRules != null && invoiceRules.size() != 0) {
						clearDiscounts(transaction);
					}
				}
			}
			createTransaction = false;
		} else {
			createTransaction = true;
		}
		// Changes for Rev 1.0 : Ends
		 
	}

	// ----------------------------------------------------------------------
	/**
	 * Unloads data to service.
	 * <P>
	 * 
	 * @param bus
	 *            Service Bus
	 **/
	// ----------------------------------------------------------------------
	public void unload(BusIfc bus) {

		super.unload(bus);

		// BigDecimal percent = new BigDecimal("0.00");
		MAXModifyTransactionDiscountCargo cargo;
		cargo = (MAXModifyTransactionDiscountCargo) bus.getCargo();
		

		cargo.setTransaction(this.transaction);
		//Added by Vaibhav For Bill Buster Discount
		SaleReturnLineItemIfc[] lineItems = null;
	     
        // Get the line items from cargo
        lineItems = (SaleReturnLineItemIfc[]) cargo.getTransaction().getLineItems();
        if (lineItems == null)
        {
            lineItems = new SaleReturnLineItemIfc[1];
            lineItems[0] = (SaleReturnLineItemIfc) cargo.getTransaction().getLineItemsVector().get(0);
        }
		ArrayList<String> itemList = null;
		 String itemId= null;
		 String pcttargetitm=null;
		 String doltargetitm=null;
		 CurrencyIfc targetitemprc= null;
		 BigDecimal targetItemqty=null;
		 
		 for(int p=0;p<invoiceRules.size();p++)
	        {
	        	if(invoiceRules.get(p).getDescription().equalsIgnoreCase("Buy$NorMoreGetYatZ%offTiered_BillBuster")
	        		//	&& cargo.getInvoiceDiscounts().get(p).getSourceList().getItemThreshold().compareTo(cargo.getTransaction().getTenderTransactionTotals().getBalanceDue())==-1
	        		 &&!(invoiceRules.get(p).getSourceList().getItemThreshold().toString().compareTo("0.00")==0))
	        	{
	        		itemList=(ArrayList<String>) invoiceRules.get(p).getItemList();
	        		for(int j=0;j<cargo.getTransaction().getLineItemsVector().size();j++)
	        		{
	        			itemId=cargo.getTransaction().getLineItemsVector().get(j).getItemID();
						for (int k=0 ;k<itemList.size();k++) {
							if(itemId.equalsIgnoreCase(itemList.get(k).toString())){
								pcttargetitm=itemList.get(k).toString(); 
							 targetitemprc=cargo.getTransaction().getLineItemsVector().get(j).getExtendedSellingPrice();
							 targetItemqty=cargo.getTransaction().getLineItemsVector().get(j).getItemQuantityDecimal();
							//	ifc.setDiscountRate(cargo.getInvoiceDiscounts().get(p).getDiscountRate());
		        				
								
								break;
							}
						}
	        		}
						
						}else if(invoiceRules.get(p).getDescription().equalsIgnoreCase("Buy$NorMoreGetYatZ$offTiered_BillBuster")
			        		//	&& cargo.getInvoiceDiscounts().get(p).getSourceList().getItemThreshold().compareTo(cargo.getTransaction().getTenderTransactionTotals().getBalanceDue())==-1
			        		 &&!(invoiceRules.get(p).getSourceList().getItemThreshold().toString().compareTo("0.00")==0))
			        	{
			        		itemList=(ArrayList<String>) invoiceRules.get(p).getItemList();
			        		for(int r=0;r<cargo.getTransaction().getLineItemsVector().size();r++)
			        		{
			        			itemId=cargo.getTransaction().getLineItemsVector().get(r).getItemID();
								for (int n=0 ;n<itemList.size();n++) {
									if(itemId.equalsIgnoreCase(itemList.get(n).toString())){
										doltargetitm=itemList.get(n).toString(); 
									 targetitemprc=cargo.getTransaction().getLineItemsVector().get(r).getExtendedSellingPrice();
									 targetItemqty=cargo.getTransaction().getLineItemsVector().get(r).getItemQuantityDecimal();
									//	ifc.setDiscountRate(cargo.getInvoiceDiscounts().get(p).getDiscountRate());
				        				
										
										break;
									}
								}
			        		}
			        	}
								
			        		
			        		
		// Changes for Rev 1.0 : Starts
		if (invoiceRules != null)
			cargo.setInvoiceDiscounts(invoiceRules);

		cargo.setInvoiceRuleAlreadyApplied(invoiceRuleAlreadyApplied);
		cargo.setInvoiceRuleAppliedRate(invoiceRuleAppliedRate);
		cargo.setInvoiceDiscountAmount(discountAmount);
		cargo.setGrantAccessforInvoicerules(true);
		if (invoiceRules != null && invoiceRules.size() != 0) {

			int reasonCodeType1 = 0;
			int reasonCodeType2 = 0;

			int length = invoiceRules.size();
			//reasonCodeType1 = Integer.parseInt(((this.invoiceRules.get(i))).getReason().getCode());

			for (int i = 0; i < length; i++) {
				reasonCodeType1 = Integer.parseInt(((this.invoiceRules.get(i))).getReason().getCode());
                System.out.println("239"+reasonCodeType1);
				 for(int m=0;m < lineItems.length;m++)
					 
			        {
			        	if(cargo.getTransaction().getLineItemsVector().get(m).getItemID().equalsIgnoreCase(pcttargetitm) && reasonCodeType1==33)
			        	{
			        		cargo.setDiscountType(MAXRoleFunctionIfc.INVOICE_TYPE_DISCOUNT_PERCENT_BB);
			        		break;
			        	}
			        	 if (cargo.getTransaction().getLineItemsVector().get(m).getItemID().equalsIgnoreCase(doltargetitm) &&reasonCodeType1==34){
			        		
			        		cargo.setDiscountType(MAXRoleFunctionIfc.INVOICE_TYPE_DISCOUNT_DOLLAR_BB);
			        	break;
			        	 }
			        	
			        	System.out.println("252"+cargo.getDiscountType());
				//reasonCodeType1 = Integer.parseInt(((this.invoiceRules.get(0))).getReason().getCode());

				//reasonCodeType2 = Integer.parseInt(((this.invoiceRules.get(i))).getReason().getCode());
			//	if (reasonCodeType1 != reasonCodeType2) {
				//	flag = true;
				//	break;
				//}
			
			
		
			// Changes for Rev 1.0 : Ends
			//if (!flag) {
				//String reasonCodeType = ((this.invoiceRules.get(0))).getReason().getCode();
				//cargo.setDiscountType(Integer.parseInt(reasonCodeType));
			//} else {
				//cargo.setDiscountType(MAXRoleFunctionIfc.INVOICE_TYPE_DISCOUNT);
			        	
			}}
			cargo.setAccessFunctionID(6); // 6 for Item/transaction Discount

			        } else 
			cargo.setAccessFunctionID(RoleFunctionIfc.CLOSE_TILL);

	}
						
					}

	// Changes for Rev 1.1 : Starts
	public TransactionDiscountStrategyIfc[] getDiscounts(SaleReturnTransactionIfc transaction) {
		TransactionDiscountStrategyIfc[] discountArray;
		if (transaction
				.getTransactionDiscounts(DiscountRuleConstantsIfc.DISCOUNT_METHOD_PERCENTAGE,
						DiscountRuleConstantsIfc.ASSIGNMENT_MANUAL).toString().equalsIgnoreCase("[]"))
			discountArray = transaction.getTransactionDiscounts(DiscountRuleConstantsIfc.DISCOUNT_METHOD_PERCENTAGE,
					DiscountRuleConstantsIfc.ASSIGNMENT_MANUAL);
		else
			discountArray = transaction.getTransactionDiscounts(DiscountRuleConstantsIfc.DISCOUNT_METHOD_AMOUNT,
					DiscountRuleConstantsIfc.ASSIGNMENT_MANUAL);
		return discountArray;
	}

	public void clearDiscounts(SaleReturnTransactionIfc transaction) {
		if (discountPercent.getReasonCode() == MAXDiscountRuleConstantsIfc.DISCOUNT_REASON_Buy$NatZPctoffTiered) {
			transaction.clearTransactionDiscounts(DiscountRuleConstantsIfc.DISCOUNT_METHOD_PERCENTAGE,
					DiscountRuleConstantsIfc.ASSIGNMENT_MANUAL);
		} else if (discountPercent.getReasonCode() == MAXDiscountRuleConstantsIfc.DISCOUNT_REASON_Buy$NatZ$offTiered) {
			transaction.clearTransactionDiscounts(DiscountRuleConstantsIfc.DISCOUNT_METHOD_AMOUNT,
					DiscountRuleConstantsIfc.ASSIGNMENT_MANUAL);
		}
	}
	// Changes for Rev 1.1 : Ends
}
