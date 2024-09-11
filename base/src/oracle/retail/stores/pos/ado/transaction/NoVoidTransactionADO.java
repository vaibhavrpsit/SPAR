/* ===========================================================================
* Copyright (c) 2009, 2011, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/ado/transaction/NoVoidTransactionADO.java /rgbustores_13.4x_generic_branch/1 2011/05/05 14:05:41 mszekely Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    cgreene   05/26/10 - convert to oracle packaging
 *    abondala  01/03/10 - update header date
 *
 * ===========================================================================
 */
package oracle.retail.stores.pos.ado.transaction;

import oracle.retail.stores.pos.ado.store.RegisterADO;
import oracle.retail.stores.domain.DomainGateway;
import oracle.retail.stores.domain.transaction.TransactionIfc;
import oracle.retail.stores.domain.utility.EYSDomainIfc;
import oracle.retail.stores.foundation.manager.data.DataException;

/**
 * NoVoidTransactionADO class is dummy ADO class to support transaction types 
 * not listed in TransactionPrototypeEnum and by default 
 * not post voidable.
 * 
 */

public class NoVoidTransactionADO extends AbstractRetailTransactionADO {

	private static final long serialVersionUID = 1L;

	@Override
	protected TransactionIfc instantiateTransactionRDO() {
        transactionRDO = DomainGateway.getFactory().getTransactionInstance();
        return transactionRDO;

	}

	public void save(RegisterADO registerADO) throws DataException {

	}

	public void fromLegacy(EYSDomainIfc rdo) {
	}

	public EYSDomainIfc toLegacy() {
		return transactionRDO;
	}

	public EYSDomainIfc toLegacy(Class type) {
		return toLegacy();
	}
}
