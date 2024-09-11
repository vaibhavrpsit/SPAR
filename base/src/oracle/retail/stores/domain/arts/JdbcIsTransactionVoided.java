/* ===========================================================================
* Copyright (c) 1998, 2011, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/modules/domain/src/oracle/retail/stores/domain/arts/JdbcIsTransactionVoided.java /rgbustores_13.4x_generic_branch/1 2011/05/04 11:48:55 mszekely Exp $
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
 *    4    360Commerce 1.3         1/25/2006 4:11:09 PM   Brett J. Larsen merge
 *          7.1.1 changes (aka. 7.0.3 fixes) into 360Commerce view
 *    3    360Commerce 1.2         3/31/2005 4:28:37 PM   Robert Pearse   
 *    2    360Commerce 1.1         3/10/2005 10:22:38 AM  Robert Pearse   
 *    1    360Commerce 1.0         2/11/2005 12:11:55 PM  Robert Pearse   
 *:
 *    4    .v700     1.2.1.0     11/16/2005 16:28:01    Jason L. DeLeau 4215:
 *         Get rid of redundant ArtsDatabaseifc class
 *    3    360Commerce1.2         3/31/2005 15:28:37     Robert Pearse
 *    2    360Commerce1.1         3/10/2005 10:22:38     Robert Pearse
 *    1    360Commerce1.0         2/11/2005 12:11:55     Robert Pearse
 *
 *   Revision 1.7  2004/08/13 13:53:51  kll
 *   @scr 0: deprecation fixes
 *
 *   Revision 1.6  2004/04/09 16:55:47  cdb
 *   @scr 4302 Removed double semicolon warnings.
 *
 *   Revision 1.5  2004/02/17 17:57:38  bwf
 *   @scr 0 Organize imports.
 *
 *   Revision 1.4  2004/02/17 16:18:47  rhafernik
 *   @scr 0 log4j conversion
 *
 *   Revision 1.3  2004/02/12 17:13:14  mcs
 *   Forcing head revision
 *
 *   Revision 1.2  2004/02/11 23:25:26  bwf
 *   @scr 0 Organize imports.
 *
 *   Revision 1.1.1.1  2004/02/11 01:04:27  cschellenger
 *   updating to pvcs 360store-current
 *
 *
 *
 *    Rev 1.0   Aug 29 2003 15:30:56   CSchellenger
 * Initial revision.
 *
 *    Rev 1.0   Jun 03 2002 16:36:40   msg
 * Initial revision.
 *
 *    Rev 1.1   Mar 18 2002 22:47:00   msg
 * - updated copyright
 *
 *    Rev 1.0   Mar 18 2002 12:07:04   msg
 * Initial revision.
 *
 *    Rev 1.0   Sep 20 2001 15:55:44   msg
 * Initial revision.
 *
 *    Rev 1.1   Sep 17 2001 12:34:36   msg
 * header update
 * ===========================================================================
 */
package oracle.retail.stores.domain.arts;

import java.sql.ResultSet;
import java.sql.SQLException;

import oracle.retail.stores.persistence.utility.ARTSDatabaseIfc;

import org.apache.log4j.Logger;

import oracle.retail.stores.common.sql.SQLSelectStatement;
import oracle.retail.stores.domain.transaction.TransactionIfc;
import oracle.retail.stores.foundation.manager.data.DataException;
import oracle.retail.stores.foundation.manager.data.JdbcDataConnection;
import oracle.retail.stores.foundation.manager.ifc.data.DataActionIfc;
import oracle.retail.stores.foundation.manager.ifc.data.DataConnectionIfc;
import oracle.retail.stores.foundation.manager.ifc.data.DataTransactionIfc;

/**
    This operation checks the Post Void Transaction table to see if a
    transaction has been voided.
    <P>
    @version $Revision: /rgbustores_13.4x_generic_branch/1 $
**/
public class JdbcIsTransactionVoided extends JdbcReadTransaction
                                     implements ARTSDatabaseIfc
{
    /**
        The logger to which log messages will be sent.
     */
    private static final Logger logger = Logger.getLogger(JdbcIsTransactionVoided.class);

    /**
       Class constructor. <P>
     */
    public JdbcIsTransactionVoided()
    {
        super();
        setName("JdbcIsTransactionVoided");
    }

    /**
       Execute the SQL statements against the database. <P>
       @param  dataTransaction     The data transaction
       @param  dataConnection      The connection to the data source
       @param  action              The information passed by the valet
       @exception DataException upon error
     */
    public void execute(DataTransactionIfc dataTransaction,
                        DataConnectionIfc dataConnection,
                        DataActionIfc action)
        throws DataException
    {
        if (logger.isDebugEnabled()) logger.debug( "JdbcIsTransactionVoided.execute");

        JdbcDataConnection connection = (JdbcDataConnection)dataConnection;
        TransactionIfc transaction = (TransactionIfc)action.getDataObject();

        // See if the transaction is voided
        Boolean returnValue = new Boolean(isTransactionVoided(connection,
                                                              transaction));
        // Set the result
        dataTransaction.setResult(returnValue);

        if (logger.isDebugEnabled()) logger.debug( "JdbcIsTransactionVoided.execute");
    }

    /**
       Checks the Post Void Transaction table to see if an entry exits
       for the given transaction.
       <p>
       @param  dataConnection  The connection to the data source
       @param  transaction     a pos transaction
       @return True, if the transaction has been voided, false otherwise
       @exception DataException upon error
     */
    public boolean isTransactionVoided(JdbcDataConnection dataConnection,
                                       TransactionIfc transaction)
        throws DataException
    {
        SQLSelectStatement sql = new SQLSelectStatement();

        /*
         * Define table(s) to select from
         */
        sql.addTable(TABLE_POST_VOID_TRANSACTION);

        /*
         * Add Column(s)
         */
        sql.addColumn(FIELD_TRANSACTION_SEQUENCE_NUMBER);

        /*
         * Add Qualifiers
         */
        sql.addQualifier(FIELD_RETAIL_STORE_ID + " = " + getStoreID(transaction));
        sql.addQualifier(FIELD_WORKSTATION_ID + " = " + getWorkstationID(transaction));
        sql.addQualifier(FIELD_BUSINESS_DAY_DATE + " = " + getBusinessDayString(transaction));
        sql.addQualifier(FIELD_VOIDED + " = " + getTransactionSequenceNumber(transaction));

        boolean isVoided = false;

        try
        {
            dataConnection.execute(sql.getSQLString());

            ResultSet resultSet = (ResultSet)dataConnection.getResult();
            while(resultSet.next())
            {
                long sequenceNumber = resultSet.getLong(1);
                isVoided = true;
            }
            resultSet.close();
        }
        catch (SQLException e)
        {
            dataConnection.logSQLException(e, "Processing result set.");
            throw new DataException(DataException.SQL_ERROR,
                                    "An SQL Error occurred proccessing the result set from selecting a void transaction in JdbcIsTransactionVoid.", e);
        }

        return(isVoided);
    }
}
