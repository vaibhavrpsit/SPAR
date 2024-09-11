package oracle.retail.stores.domain.arts;
/* ===========================================================================
* Copyright (c) 2013, Oracle and/or its affiliates. All rights reserved.
 * ===========================================================================
 * $$Log:$$
 * ===========================================================================
 */
import java.sql.ResultSet;
import java.sql.SQLException;

import org.apache.log4j.Logger;

import oracle.retail.stores.common.sql.SQLSelectStatement;
import oracle.retail.stores.domain.transaction.TransactionIfc;
import oracle.retail.stores.foundation.manager.data.DataException;
import oracle.retail.stores.foundation.manager.data.JdbcDataConnection;
import oracle.retail.stores.foundation.manager.ifc.data.DataActionIfc;
import oracle.retail.stores.foundation.manager.ifc.data.DataConnectionIfc;
import oracle.retail.stores.foundation.manager.ifc.data.DataOperationIfc;
import oracle.retail.stores.foundation.manager.ifc.data.DataTransactionIfc;
import oracle.retail.stores.persistence.utility.ARTSDatabaseIfc;

/**
 * DataOperation implementation to verify that a record is present in the
 * TR_TRN table for the provided transaction
 * 
 * @author rhaight
 * @since 14.0
 */
public class JdbcReadVerifyRetailTransaction extends JdbcDataOperation implements DataOperationIfc, ARTSDatabaseIfc {

    /** Serial Version ID */
    private static final long serialVersionUID = -6334811050551489577L;

    /** Log4J Logger */
    protected static Logger logger = Logger.getLogger(JdbcReadVerifyRetailTransaction.class);
    
    /** Constant for Data Operation Name */
    public static final String DATA_OP_NAME = "ReadVerifyRetailTransaction";
    
    /** Data Operation Name */
    protected String opName = DATA_OP_NAME;
    
    
    /**
     * Constructor for JdbcReadVerifyRetailTransaction
     */
    public JdbcReadVerifyRetailTransaction()
    {
        opName = DATA_OP_NAME;
    }
   
    @Override
    public void setName(String name)
    {
        opName = name;
    }
    
    @Override
    public String getName() {
        return opName;
    }

    @Override
    public void initialize() throws DataException {
        // nothing to initialize
    }

    @Override
    public void execute(DataTransactionIfc dTran, DataConnectionIfc con,
            DataActionIfc da) throws DataException {

        JdbcDataConnection jdbcCon = (JdbcDataConnection) con;
        
        TransactionIfc tran = (TransactionIfc) da.getDataObject();
        
        String strQry = this.buildSQLStatement(tran);
        
        dTran.setResult(Boolean.FALSE);
        
        jdbcCon.execute(strQry);
        
        ResultSet rslt = (ResultSet)jdbcCon.getResult();
        try
        {
            // There will only be a single row in result set if 
            // transaction is found
            if (rslt.next())
            {
              dTran.setResult(Boolean.TRUE);
                if (logger.isDebugEnabled())
                {
                    logger.debug("JdbcReadVerificationTransaction verified transaction " + tran.getTransactionID());
                }
            }
            else
            {
                if (logger.isDebugEnabled())
                {
                    logger.debug("JdbcReadVerificationTransaction did not verify transaction " + tran.getTransactionID());
                }
            }
            
        }
        catch (SQLException esql)
        {
            String msg = "Unable to read verification result set for " + tran.getTransactionID();
            logger.warn(msg, esql);
            throw new DataException(DataException.UNEXPECTED_ERROR,msg, esql);
        }
        finally
        {
            try {
                rslt.close();
            }
            catch (Throwable ethrow)
            {
                // ignore on closing result set
            }
        }
        
    }
    
    
    protected String buildSQLStatement(TransactionIfc tran) throws DataException
    {
        SQLSelectStatement sql = new SQLSelectStatement();

        sql.addTable(TABLE_TRANSACTION);

        sql.setTable(TABLE_TRANSACTION);

        // Fields
        sql.addColumn(TABLE_TRANSACTION, FIELD_RETAIL_STORE_ID); //, getStoreID(transaction));
        sql.addColumn(TABLE_TRANSACTION,FIELD_WORKSTATION_ID); //, getWorkstationID(transaction));
        sql.addColumn(TABLE_TRANSACTION,FIELD_TRANSACTION_SEQUENCE_NUMBER); //, getTransactionSequenceNumber(transaction));
        sql.addColumn(TABLE_TRANSACTION,FIELD_BUSINESS_DAY_DATE); //, getBusinessDayString(transaction));
 
        sql.addQualifier(FIELD_RETAIL_STORE_ID + "=" + getSQLStoreID(tran));
        sql.addQualifier(FIELD_WORKSTATION_ID + "=" + getSQLWorkstationID(tran));
        sql.addQualifier(FIELD_TRANSACTION_SEQUENCE_NUMBER + "=" + getSQLTransactionSequenceNumber(tran));
        sql.addQualifier(FIELD_BUSINESS_DAY_DATE + "=" + getSQLBusinessDayString(tran));
        
        if (logger.isDebugEnabled())
        {
            logger.debug("JdbcReadVerification SQL Statement: " + sql.getSQLString());
        }
        return sql.getSQLString();
        
    }
    
    
    protected String getSQLWorkstationID(TransactionIfc transaction)
    {
        return ("'" + transaction.getWorkstation().getWorkstationID() + "'");
    }

    protected String getSQLStoreID(TransactionIfc transaction)
    {
        return ("'" + transaction.getWorkstation().getStoreID() + "'");
    }
    
    protected String getSQLTransactionSequenceNumber(TransactionIfc transaction)
    {
        return (String.valueOf(transaction.getTransactionSequenceNumber()));
    }
    
    protected String getSQLBusinessDayString(TransactionIfc transaction)
    {
        return (dateToSQLDateString(transaction.getBusinessDay()));
    }
}
