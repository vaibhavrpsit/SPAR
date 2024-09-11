/* ===========================================================================
* Copyright (c) 1998, 2011, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/modules/domain/src/oracle/retail/stores/domain/arts/JdbcSaveControlTransaction.java /rgbustores_13.4x_generic_branch/1 2011/05/04 11:49:02 mszekely Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    cgreene   05/27/10 - convert to oracle packaging
 *    cgreene   05/26/10 - convert to oracle packaging
 *    cgreene   04/26/10 - XbranchMerge cgreene_tech75 from
 *                         st_rgbustores_techissueseatel_generic_branch
 *    cgreene   03/30/10 - remove deprecated ARTSDatabaseIfcs and change
 *                         SQLException to DataException
 *    abondala  01/03/10 - update header date
 *
 * ===========================================================================
 * $Log:
 *    3    360Commerce 1.2         3/31/2005 4:28:43 PM   Robert Pearse   
 *    2    360Commerce 1.1         3/10/2005 10:22:48 AM  Robert Pearse   
 *    1    360Commerce 1.0         2/11/2005 12:12:01 PM  Robert Pearse   
 *
 *   Revision 1.6  2004/04/09 16:55:46  cdb
 *   @scr 4302 Removed double semicolon warnings.
 *
 *   Revision 1.5  2004/02/17 17:57:37  bwf
 *   @scr 0 Organize imports.
 *
 *   Revision 1.4  2004/02/17 16:18:47  rhafernik
 *   @scr 0 log4j conversion
 *
 *   Revision 1.3  2004/02/12 17:13:18  mcs
 *   Forcing head revision
 *
 *   Revision 1.2  2004/02/11 23:25:24  bwf
 *   @scr 0 Organize imports.
 *
 *   Revision 1.1.1.1  2004/02/11 01:04:28  cschellenger
 *   updating to pvcs 360store-current
 *
 *
 * 
 *    Rev 1.0   Aug 29 2003 15:32:40   CSchellenger
 * Initial revision.
 * 
 *    Rev 1.0   Jun 03 2002 16:39:28   msg
 * Initial revision.
 * 
 *    Rev 1.1   Mar 18 2002 22:48:10   msg
 * - updated copyright
 * 
 *    Rev 1.0   Mar 18 2002 12:08:02   msg
 * Initial revision.
 * 
 *    Rev 1.0   Sep 20 2001 15:57:14   msg
 * Initial revision.
 * 
 *    Rev 1.1   Sep 17 2001 12:34:08   msg
 * header update
 * ===========================================================================
 */
package oracle.retail.stores.domain.arts;

import java.sql.SQLException;

import org.apache.log4j.Logger;

import oracle.retail.stores.common.sql.SQLInsertStatement;
import oracle.retail.stores.common.sql.SQLUpdateStatement;
import oracle.retail.stores.domain.transaction.TransactionIfc;
import oracle.retail.stores.foundation.manager.data.DataException;
import oracle.retail.stores.foundation.manager.data.JdbcDataConnection;
import oracle.retail.stores.foundation.manager.ifc.data.DataActionIfc;
import oracle.retail.stores.foundation.manager.ifc.data.DataConnectionIfc;
import oracle.retail.stores.foundation.manager.ifc.data.DataTransactionIfc;

/**
 * This operation performs inserts into the transaction and control transaction
 * tables.
 * 
 * @version $Revision: /rgbustores_13.4x_generic_branch/1 $
 */
public class JdbcSaveControlTransaction extends JdbcSaveTransaction
{
    private static final long serialVersionUID = 8664906687054550225L;
    /**
     * The logger to which log messages will be sent.
     */
    private static final Logger logger = Logger.getLogger(JdbcSaveControlTransaction.class);

    /**
     * Class constructor.
     * <P>
     */
    public JdbcSaveControlTransaction()
    {
        super();
        setName("JdbcSaveControlTransaction");
    }

    /**
     * Executes the SQL statements against the database.
     * 
     * @param dataTransaction
     * @param dataConnection
     * @param action
     * @exception DataException upon error
     */
    public void execute(DataTransactionIfc dataTransaction,
                        DataConnectionIfc dataConnection,
                        DataActionIfc action)
        throws DataException
    {
        if (logger.isDebugEnabled()) logger.debug( "JdbcSaveControlTransaction.execute()");

        /*
         * getUpdateCount() is about the only thing outside of
         * DataConnectionIfc that we need.
         */
        JdbcDataConnection connection = (JdbcDataConnection)dataConnection;

        // Navigate the input object to obtain values that will be inserted
        // into the database.
        ARTSTransaction trans = (ARTSTransaction)action.getDataObject();
        saveControlTransaction(connection,
                               trans.getPosTransaction());

        if (logger.isDebugEnabled()) logger.debug( "JdbcSaveControlTransaction.execute()");
    }

    /**
     * Saves a control transaction.
     * 
     * @param dataConnection connection to the db
     * @param transaction a control transaction
     * @exception DataException upon error
     */
    public void saveControlTransaction(JdbcDataConnection dataConnection,
                                       TransactionIfc transaction)
        throws DataException
    {
        /*
         * If the insert fails, then try to update the transaction
         */
        try
        {
            insertControlTransaction(dataConnection, transaction);
        }
        catch (DataException de)
        {
            //updateControlTransaction(dataConnection, transaction);
            /*
             * Shouldn't be updating this type of transaction.
             * Pass back exception instead.
             */
            logger.error(de);
            throw de;
        }
        catch (Exception e)
        {
            logger.error( "Couldn't save control transaction.");
            logger.error(e);
            throw new DataException(DataException.UNKNOWN,
                                    "Couldn't save control transaction.",
                                    e);
        }
    }

    /**
     * Updates the control transaction table.
     * 
     * @param dataConnection connection to the db
     * @param transactionType The type of control transaction.
     * @param transaction a control transaction
     * @exception DataException thrown when an error occurs.
     */
    public void updateControlTransaction(JdbcDataConnection dataConnection,
                                         TransactionIfc transaction)
        throws DataException
    {
        /*
         * Update the transaction table first.
         */
        updateTransaction(dataConnection, transaction);

        SQLUpdateStatement sql = new SQLUpdateStatement();

        // Table
        sql.setTable(TABLE_CONTROL_TRANSACTION);

        // Fields
        sql.addColumn(FIELD_CONTROL_TRANSACTION_TYPE_CODE, getTransactionType(transaction));

        // Qualifiers
        sql.addQualifier(FIELD_RETAIL_STORE_ID + " = " + getStoreID(transaction));
        sql.addQualifier(FIELD_WORKSTATION_ID + " = " + getWorkstationID(transaction));
        sql.addQualifier(FIELD_TRANSACTION_SEQUENCE_NUMBER
                         + " = " + getTransactionSequenceNumber(transaction));
        sql.addQualifier(FIELD_BUSINESS_DAY_DATE
                         + " = " + getBusinessDayString(transaction));

        try
        {
            dataConnection.execute(sql.getSQLString());
        }
        catch (DataException de)
        {
            logger.error(de);
            throw de;
        }
        catch (Exception e)
        {
            throw new DataException(DataException.UNKNOWN, "updateControlTransaction", e);
        }
    }

    /**
       Inserts into the control transaction table.
       <P>
       @param  dataConnection  connection to the db
       @param  transaction     a control transaction
       @exception DataException thrown when an error occurs.
     */
    public void insertControlTransaction(JdbcDataConnection dataConnection,
                                         TransactionIfc transaction)
        throws DataException
    {
        /*
         * Insert the transaction in the Transaction table first.
         */
        insertTransaction(dataConnection, transaction);

        SQLInsertStatement sql = new SQLInsertStatement();

        // Table
        sql.setTable(TABLE_CONTROL_TRANSACTION);

        // Fields
        sql.addColumn(FIELD_RETAIL_STORE_ID, getStoreID(transaction));
        sql.addColumn(FIELD_WORKSTATION_ID, getWorkstationID(transaction));
        sql.addColumn(FIELD_TRANSACTION_SEQUENCE_NUMBER,
                      getTransactionSequenceNumber(transaction));
        sql.addColumn(FIELD_BUSINESS_DAY_DATE, getBusinessDayString(transaction));
        sql.addColumn(FIELD_CONTROL_TRANSACTION_TYPE_CODE, getTransactionType(transaction));

        try
        {
            dataConnection.execute(sql.getSQLString());
        }
        catch (DataException de)
        {
            logger.error(de);
            throw de;
        }
        catch (Exception e)
        {
            throw new DataException(DataException.UNKNOWN, "insertControlTransaction", e);
        }
    }
}
