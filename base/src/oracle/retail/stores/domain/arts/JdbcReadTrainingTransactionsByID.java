/* ===========================================================================
* Copyright (c) 1998, 2014, Oracle and/or its affiliates. All rights reserved.
 * ===========================================================================
 * $Header: rgbustores/modules/domain/src/oracle/retail/stores/domain/arts/JdbcReadTrainingTransactionsByID.java $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    crain    10/01/14 - Fix to prevent user to return items (non retrieved
 *                         receipted return) from training mode in the normal
 *                         mode and vice versa.
 *
 * ===========================================================================
 */
package oracle.retail.stores.domain.arts;

import org.apache.log4j.Logger;

import oracle.retail.stores.common.utility.LocaleRequestor;
import oracle.retail.stores.domain.transaction.TransactionIfc;
import oracle.retail.stores.foundation.manager.data.DataException;
import oracle.retail.stores.foundation.manager.data.JdbcDataConnection;
import oracle.retail.stores.foundation.manager.ifc.data.DataActionIfc;
import oracle.retail.stores.foundation.manager.ifc.data.DataConnectionIfc;
import oracle.retail.stores.foundation.manager.ifc.data.DataTransactionIfc;

//--------------------------------------------------------------------------
/**
    This operation reads all of the transactions matching a specified
    transaction ID.
     <P>
     @version $Revision: $
     @since 14.1
**/
//--------------------------------------------------------------------------
public class JdbcReadTrainingTransactionsByID extends JdbcReadTransaction
{
    /**
     * 
     */
    private static final long serialVersionUID = -4655344305598918836L;
    /** 
        The logger to which log messages will be sent.
    **/
    private static Logger logger = Logger.getLogger(oracle.retail.stores.domain.arts.JdbcReadTransactionsByID.class);

    //----------------------------------------------------------------------
    /**
       Class constructor.
    **/
    //----------------------------------------------------------------------
    public JdbcReadTrainingTransactionsByID()
    {
        super();
        setName("JdbcReadTrainingTransactionsByID");
    }

    //---------------------------------------------------------------------
    /**
       Executes the SQL statements against the database.
       <P>
       @param  dataTransaction     The data transaction
       @param  dataConnection      The connection to the data source
       @param  action              The information passed by the valet
       @exception DataException upon error
       @since 14.1
    **/
    //---------------------------------------------------------------------
    public void execute(DataTransactionIfc dataTransaction,
                        DataConnectionIfc dataConnection,
                        DataActionIfc action)
        throws DataException
    {
        if (logger.isDebugEnabled()) logger.debug( "JdbcReadTrainingTransactionsByID.execute");

        // set data connection
        JdbcDataConnection connection = (JdbcDataConnection) dataConnection;

        // grab arguments and call readTransactionsByID()
        TransactionIfc transaction = (TransactionIfc)action.getDataObject();
        LocaleRequestor localeRequestor = getLocaleRequestor(transaction);

        TransactionIfc[] transactions = readTrainingTransactionsByID(connection, transaction, localeRequestor);

        // return array
        dataTransaction.setResult(transactions);

        if (logger.isDebugEnabled()) logger.debug( "JdbcReadTrainingTransactionsByID.execute");
    }
}