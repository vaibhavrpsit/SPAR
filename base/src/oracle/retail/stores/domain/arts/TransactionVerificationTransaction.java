package oracle.retail.stores.domain.arts;
/* ===========================================================================
* Copyright (c) 2013, Oracle and/or its affiliates. All rights reserved.
 * ===========================================================================
 * $$Log:$$
 * ===========================================================================
 */

import oracle.retail.stores.foundation.manager.data.DataAction;
import oracle.retail.stores.foundation.manager.data.DataException;
import oracle.retail.stores.foundation.manager.data.DataTransaction;
import oracle.retail.stores.foundation.manager.ifc.data.DataActionIfc;

import oracle.retail.stores.domain.transaction.TransactionIfc;

/**
 * TransactionVerificationTransaction provides an api to verify the
 * persistence of a transaction in the store database
 * 
 * @author rhaight
 * @since 14.0
 */
public class TransactionVerificationTransaction extends DataTransaction {

    /** Name of the Data Transaction */
    public static final String VERIFICATION_TRANSACTION_NAME = "VerificationTransaction";
    
    /** Serial Version ID */
    private static final long serialVersionUID = -427084608763764385L;

    /**
     * Constructor for TransactionVerificationTransaction. 
     * Sets the transaction name to VERIFICATION_TRANSACTION_NAME
     */
    public TransactionVerificationTransaction()
    {
        super(VERIFICATION_TRANSACTION_NAME);
    }
    
    /**
     * Returns true if the transaction has a record in the store database
     * @param tran to verifiy
     * @return true if persisted, false if not persisted
     * @throws DataException
     */
    public boolean verifyTransaction(TransactionIfc tran) throws DataException
    {
        DataAction da = new DataAction();
        da.setDataOperationName(JdbcReadVerifyRetailTransaction.DATA_OP_NAME);
        da.setDataObject(tran);
        dataActions = new DataActionIfc[1];
        dataActions[0] = da;
        
        return ((Boolean) getDataManager().execute(this)).booleanValue();
    }
}
