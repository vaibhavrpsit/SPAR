/* ===========================================================================
* Copyright (c) 2008, 2011, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/modules/domain/src/oracle/retail/stores/domain/arts/JdbcSaveNoSaleTransaction.java /rgbustores_13.4x_generic_branch/1 2011/05/04 11:49:06 mszekely Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    cgreene   05/28/10 - convert to oracle packaging
 *    cgreene   05/27/10 - convert to oracle packaging
 *    cgreene   05/27/10 - convert to oracle packaging
 *    cgreene   05/26/10 - convert to oracle packaging
 *    cgreene   04/26/10 - XbranchMerge cgreene_tech75 from
 *                         st_rgbustores_techissueseatel_generic_branch
 *    cgreene   03/30/10 - remove deprecated ARTSDatabaseIfcs and change
 *                         SQLException to DataException
 *    abondala  01/03/10 - update header date
 *    mdecama   10/21/08 - I18N - Localizing No Sale ReasonCode

     $Log:
      3    360Commerce 1.2         3/31/2005 4:28:44 PM   Robert Pearse
      2    360Commerce 1.1         3/10/2005 10:22:49 AM  Robert Pearse
      1    360Commerce 1.0         2/11/2005 12:12:03 PM  Robert Pearse
     $
     Revision 1.6  2004/04/09 16:55:47  cdb
     @scr 4302 Removed double semicolon warnings.

     Revision 1.5  2004/02/17 17:57:38  bwf
     @scr 0 Organize imports.

     Revision 1.4  2004/02/17 16:18:49  rhafernik
     @scr 0 log4j conversion

     Revision 1.3  2004/02/12 17:13:18  mcs
     Forcing head revision

     Revision 1.2  2004/02/11 23:25:26  bwf
     @scr 0 Organize imports.

     Revision 1.1.1.1  2004/02/11 01:04:28  cschellenger
     updating to pvcs 360store-current


 *
 *    Rev 1.1   Nov 25 2003 09:49:36   sfl
 * Make sure the throw the dataException during the insetion on transaction table.
 * Resolution for 3444: No Sale SQL error transaction does not generate a QueueException log, appears on reports
 *
 *    Rev 1.0   Aug 29 2003 15:32:52   CSchellenger
 * Initial revision.
 *
 *    Rev 1.0   Jun 03 2002 16:39:54   msg
 * Initial revision.
 *
 *    Rev 1.1   Mar 18 2002 22:48:28   msg
 * - updated copyright
 *
 *    Rev 1.0   Mar 18 2002 12:08:18   msg
 * Initial revision.
 *
 *    Rev 1.0   Sep 20 2001 15:59:26   msg
 * Initial revision.
 *
 *    Rev 1.1   Sep 17 2001 12:34:04   msg
 * header update
 */
package oracle.retail.stores.domain.arts;

import org.apache.log4j.Logger;

import oracle.retail.stores.common.sql.SQLInsertStatement;
import oracle.retail.stores.common.sql.SQLUpdateStatement;
import oracle.retail.stores.domain.transaction.NoSaleTransactionIfc;
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
public class JdbcSaveNoSaleTransaction extends JdbcSaveControlTransaction
{
    private static final long serialVersionUID = 1137128864803112563L;
    /**
     * The logger to which log messages will be sent.
     */
    private static final Logger logger = Logger.getLogger(JdbcSaveNoSaleTransaction.class);

    /**
     * Class constructor.
     */
    public JdbcSaveNoSaleTransaction()
    {
        super();
        setName("JdbcSaveNoSaleTransaction");
    }

    /**
     * Executes the SQL statements against the database.
     * 
     * @param dataTransaction The data transaction
     * @param dataConnection The connection to the data source
     * @param action The information passed by the valet
     * @exception DataException upon error
     */
    public void execute(DataTransactionIfc dataTransaction,
                        DataConnectionIfc dataConnection,
                        DataActionIfc action)
        throws DataException
    {
        if (logger.isDebugEnabled()) logger.debug( "JdbcSaveNoSaleTransaction.execute()");

        /*
         * getUpdateCount() is about the only thing outside of
         * DataConnectionIfc that we need.
         */
        JdbcDataConnection connection = (JdbcDataConnection)dataConnection;

        // Navigate the input object to obtain values that will be inserted
        // into the database.
        ARTSTransaction trans = (ARTSTransaction) action.getDataObject();
        saveNoSaleTransaction(connection,
                              (NoSaleTransactionIfc) trans.getPosTransaction());

        if (logger.isDebugEnabled()) logger.debug( "JdbcSaveNoSaleTransaction.execute()");
    }

    /**
     * Saves a no sale transaction.
     * 
     * @param dataConnection connection to the db
     * @param transaction a no sale transaction
     * @exception DataException upon error
     */
    public void saveNoSaleTransaction(JdbcDataConnection dataConnection,
                                      NoSaleTransactionIfc transaction)
        throws DataException
    {
        try
        {
            insertNoSaleTransaction(dataConnection, transaction);
        }
        catch (DataException de)
        {
            logger.error( "Couldn't save No Sale Transaction.");
            logger.error(de);
            throw de;
        }
        catch (Exception e)
        {
            logger.error( "Couldn't save No Sale Transaction.");
            logger.error(e);
            throw new DataException(DataException.UNKNOWN,
                                    "Couldn't save No Sale Transaction.",
                                    e);
        }
    }

    /**
     * Updates the no sale transaction table.
     * 
     * @param dataConnection connection to the db
     * @param transaction a no sale transaction
     * @exception DataException thrown when an error occurs.
     */
    public void updateNoSaleTransaction(JdbcDataConnection dataConnection,
                                        NoSaleTransactionIfc transaction)
        throws DataException
    {
        /*
         * Update the Control Transaction table first.
         */
        updateControlTransaction(dataConnection, transaction);

        SQLUpdateStatement sql = new SQLUpdateStatement();

        // Table
        sql.setTable(TABLE_NO_SALE_TRANSACTION);

        // Fields
        sql.addColumn(FIELD_NO_SALE_REASON_CODE, getReasonCode(transaction));

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
            throw new DataException(DataException.UNKNOWN, "updateNoSaleTransaction", e);
        }
    }

    /**
     * Inserts into the no sale transaction table.
     * 
     * @param dataConnection connection to the db
     * @param transaction a no sale transaction
     * @exception DataException thrown when an error occurs.
     */
    public void insertNoSaleTransaction(JdbcDataConnection dataConnection,
                                        NoSaleTransactionIfc transaction)
        throws DataException
    {
        /*
         * Insert the Control Transaction first.
         */
        insertControlTransaction(dataConnection, transaction);

        SQLInsertStatement sql = new SQLInsertStatement();

        // Table
        sql.setTable(TABLE_NO_SALE_TRANSACTION);

        // Fields
        sql.addColumn(FIELD_RETAIL_STORE_ID, getStoreID(transaction));
        sql.addColumn(FIELD_WORKSTATION_ID, getWorkstationID(transaction));
        sql.addColumn(FIELD_TRANSACTION_SEQUENCE_NUMBER,
                      getTransactionSequenceNumber(transaction));
        sql.addColumn(FIELD_BUSINESS_DAY_DATE, getBusinessDayString(transaction));
        sql.addColumn(FIELD_NO_SALE_REASON_CODE, getReasonCode(transaction));

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

    /**
       Returns the reason code for a no sale transaction.
       <p>
       @param  transaction a no sale transaction
       @return the reason code for a no sale transaction.
     */
    protected String getReasonCode(NoSaleTransactionIfc transaction)
    {
        return(makeSafeString(transaction.getLocalizedReasonCode().getCode()));
    }
}
