/* =============================================================================
* Copyright (c) 2008, 2011, Oracle and/or its affiliates. All rights reserved. 
 * =============================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/returnauthorization/ReturnAuthorizationCargo.java /rgbustores_13.4x_generic_branch/1 2011/05/05 14:05:50 mszekely Exp $
 * =============================================================================
 * NOTES
 * Created by Lucy Zhao (Oracle Consulting) for POS-RM integration.
 *
 * MODIFIED    (MM/DD/YY)
 *    cgreene   05/27/10 - convert to oracle packaging
 *    cgreene   05/26/10 - convert to oracle packaging
 *    abondala  01/03/10 - update header date
 *    abondala  12/02/08 - RM-POS integration
 *    rkar      11/07/08 - Additions/changes for POS-RM integration
 *
 * =============================================================================
 */
package oracle.retail.stores.pos.services.returnauthorization;


import oracle.retail.stores.pos.services.common.AbstractFinancialCargo;
import oracle.retail.stores.pos.services.common.AbstractFinancialCargoIfc;
import oracle.retail.stores.domain.lineitem.ReturnResponseLineItemIfc;
import oracle.retail.stores.domain.manager.rm.RPIMoreCustomerInfo;
import oracle.retail.stores.domain.manager.rm.RPIFinalResultIfc;
import oracle.retail.stores.domain.manager.rm.RPIPositiveID;
import oracle.retail.stores.domain.manager.rm.RPIRequestIfc;
import oracle.retail.stores.domain.manager.rm.RPIResponseIfc;
import oracle.retail.stores.domain.transaction.RetailTransactionIfc;
import oracle.retail.stores.domain.transaction.SaleReturnTransactionIfc;

/**
 *  Cargo class, that carries information to Returns Management server
 *
 *  @version $Revision: /rgbustores_13.4x_generic_branch/1 $
 */
public class ReturnAuthorizationCargo extends AbstractFinancialCargo
									  implements ReturnAuthorizationCargoIfc, AbstractFinancialCargoIfc
{
	private static final long serialVersionUID = 1L;
	
	protected SaleReturnTransactionIfc[] originalReturnTransactions;
	
	protected RetailTransactionIfc 		 transaction;
	
	protected RPIPositiveID positiveID;
	
	protected RPIMoreCustomerInfo moreCustomerInfo;
	
	protected RPIResponseIfc returnResponse;
	
	protected RPIRequestIfc  returnRequest;
	
	protected RPIFinalResultIfc returnResult;
	
	protected ReturnResponseLineItemIfc[] returnResponseLineItems;
	
	protected int[] selectedRows;
	
	protected String customerType = null;
	public SaleReturnTransactionIfc[] getOriginalReturnTransactions() {
		return originalReturnTransactions;
	}

	public void setOriginalReturnTransactions(SaleReturnTransactionIfc[] originalReturnTransactions) {
		this.originalReturnTransactions = originalReturnTransactions;
	}

	public RetailTransactionIfc getTransaction() {
		return transaction;
	}

	public void setTransaction(RetailTransactionIfc transaction) {
		this.transaction = transaction;
	}

	public RPIPositiveID getPositiveID() {
		return positiveID;
	}

	public void setPositiveID(RPIPositiveID positiveID) {
		this.positiveID = positiveID;
	}

	public RPIResponseIfc getReturnResponse() {
		return returnResponse;
	}

	public void setReturnResponse(RPIResponseIfc returnResponse) {
		this.returnResponse = returnResponse;
	}

	public ReturnResponseLineItemIfc[] getReturnResponseLineItems() {
		return returnResponseLineItems;
	}

	public void setReturnResponseLineItems(
			ReturnResponseLineItemIfc[] returnResponseLineItems) {
		this.returnResponseLineItems = returnResponseLineItems;
	}

	public int[] getSelectedRows() {
		return selectedRows;
	}

	public void setSelectedRows(int[] selectedRows) {
		this.selectedRows = selectedRows;
	}

	public RPIRequestIfc getReturnRequest() {
		return returnRequest;
	}

	public void setReturnRequest(RPIRequestIfc returnRequest) {
		this.returnRequest = returnRequest;
	}

	public RPIFinalResultIfc getReturnResult() {
		return returnResult;
	}

	public void setReturnResult(RPIFinalResultIfc returnResult) {
		this.returnResult = returnResult;
	}

	public RPIMoreCustomerInfo getMoreCustomerInfo() {
		return moreCustomerInfo;
	}

	public void setMoreCustomerInfo(RPIMoreCustomerInfo moreCustomerInfo) {
		this.moreCustomerInfo = moreCustomerInfo;
	}
	public String getCustomerType() 
	{
		return customerType;
	}

	public void setCustomerType(String customerType) 
	{
		this.customerType = customerType;
	}
}
