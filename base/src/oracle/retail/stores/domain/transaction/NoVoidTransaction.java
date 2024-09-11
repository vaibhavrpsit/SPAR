/* ===========================================================================
* Copyright (c) 2009, 2011, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/modules/domain/src/oracle/retail/stores/domain/transaction/NoVoidTransaction.java /rgbustores_13.4x_generic_branch/1 2011/05/04 11:48:47 mszekely Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    cgreene   05/27/10 - convert to oracle packaging
 *    cgreene   05/26/10 - convert to oracle packaging
 *    abondala  01/03/10 - update header date
 *
 * ===========================================================================
 */
package oracle.retail.stores.domain.transaction;

import oracle.retail.stores.domain.customer.CustomerIfc;
import oracle.retail.stores.domain.financial.FinancialTotalsIfc;

 /**
  * NoVoidTransaction class is dummy class to support transaction types 
  * not listed in TransactionPrototypeEnum and by default 
  * Not post voidable.
  * 
  */

public class NoVoidTransaction extends AbstractTenderableTransaction {
	private static final long serialVersionUID = 1L;

	@Override
	public FinancialTotalsIfc getFinancialTotals() {
		return null;
	}

	public void linkCustomer(CustomerIfc value) {

	}
}
