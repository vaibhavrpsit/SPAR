/* ===========================================================================
* Copyright (c) 1998, 2011, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/modules/domain/src/oracle/retail/stores/domain/arts/JdbcReadTransactionSequenceNum.java /rgbustores_13.4x_generic_branch/1 2011/05/04 11:48:58 mszekely Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    cgreene   05/26/10 - convert to oracle packaging
 *    cgreene   04/26/10 - XbranchMerge cgreene_tech75 from
 *                         st_rgbustores_techissueseatel_generic_branch
 *    cgreene   03/30/10 - remove deprecated ARTSDatabaseIfcs and change
 *                         SQLException to DataException
 *    abondala  01/03/10 - update header date
 *
 * ===========================================================================
 * $Log:
 *    4    360Commerce 1.3         1/25/2006 4:11:19 PM   Brett J. Larsen merge
 *          7.1.1 changes (aka. 7.0.3 fixes) into 360Commerce view
 *    3    360Commerce 1.2         3/31/2005 4:28:42 PM   Robert Pearse   
 *    2    360Commerce 1.1         3/10/2005 10:22:46 AM  Robert Pearse   
 *    1    360Commerce 1.0         2/11/2005 12:12:00 PM  Robert Pearse   
 *:
 *    4    .v700     1.2.1.0     11/16/2005 16:27:07    Jason L. DeLeau 4215:
 *         Get rid of redundant ArtsDatabaseifc class
 *    3    360Commerce1.2         3/31/2005 15:28:42     Robert Pearse
 *    2    360Commerce1.1         3/10/2005 10:22:46     Robert Pearse
 *    1    360Commerce1.0         2/11/2005 12:12:00     Robert Pearse
 *
 *   Revision 1.3  2004/02/12 17:13:17  mcs
 *   Forcing head revision
 *
 *   Revision 1.2  2004/02/11 23:25:23  bwf
 *   @scr 0 Organize imports.
 *
 *   Revision 1.1.1.1  2004/02/11 01:04:28  cschellenger
 *   updating to pvcs 360store-current
 *
 *
 *
 *    Rev 1.0   Aug 29 2003 15:32:18   CSchellenger
 * Initial revision.
 *
 *    Rev 1.0   Jun 03 2002 16:38:42   msg
 * Initial revision.
 *
 *    Rev 1.1   Mar 18 2002 22:48:04   msg
 * - updated copyright
 *
 *    Rev 1.0   Mar 18 2002 12:07:58   msg
 * Initial revision.
 *
 *    Rev 1.0   Sep 20 2001 16:00:10   msg
 * Initial revision.
 *
 *    Rev 1.1   Sep 17 2001 12:34:14   msg
 * header update
 * ===========================================================================
 */
package oracle.retail.stores.domain.arts;

import java.sql.ResultSet;
import java.sql.SQLException;

import oracle.retail.stores.persistence.utility.ARTSDatabaseIfc;

import oracle.retail.stores.domain.store.WorkstationIfc;
import oracle.retail.stores.domain.transaction.TransactionIfc;
import oracle.retail.stores.foundation.manager.data.DataException;
import oracle.retail.stores.foundation.manager.data.JdbcDataConnection;
import oracle.retail.stores.foundation.manager.ifc.data.DataActionIfc;
import oracle.retail.stores.foundation.manager.ifc.data.DataConnectionIfc;
import oracle.retail.stores.foundation.manager.ifc.data.DataTransactionIfc;

/**
 * This operation is the base operation for saving all transactions in the CRF
 * POS. It contains the method that saves to the transaction table in the
 * database.
 * 
 * @version $Revision: /rgbustores_13.4x_generic_branch/1 $
 */
public class JdbcReadTransactionSequenceNum extends JdbcDataOperation implements ARTSDatabaseIfc
{
    private static final long serialVersionUID = 459697234576535884L;

    /**
     * Class constructor.
     */
    public JdbcReadTransactionSequenceNum()
    {
        super();
        setName("JdbcReadTransactionSequenceNum");
    }

    /**
     * Execute the SQL statements against the database.
     * 
     * @param dataTransaction
     * @param dataConnection
     * @param action
     * @return This operation returns a null Object
     */
    public void execute(DataTransactionIfc dataTransaction,
                        DataConnectionIfc dataConnection,
                        DataActionIfc action)
        throws DataException
    {
        Long returnSequenceNum = null;
        long sequenceNumber = 0;

        TransactionIfc    transaction = (TransactionIfc) action.getDataObject();
        WorkstationIfc workstation = transaction.getWorkstation();

        try
        {
            String sqlString = "\n" +
                "update ai_trn_gen set ai_trn = ai_trn+1 " +
                "where " +
                "id_str_rt = " + workstation.getStoreID() + " and " +
                "id_ws = " + workstation.getWorkstationID() + "\n";

            dataConnection.execute(sqlString);

            int updateCount =
                ((JdbcDataConnection)dataConnection).getUpdateCount();

            if(updateCount == 0)
            {
                sqlString = "\n" +
                    "insert into ai_trn_gen " +
                    "(id_str_rt, id_ws, ai_trn) values (" +
                    workstation.getStoreID() + ", " +
                    workstation.getWorkstationID() + ", 0)\n";

                dataConnection.execute(sqlString);

                // Update the ai_trn_gen table
                sqlString = "\n" +
                    "update ai_trn_gen set ai_trn = ai_trn+1 " +
                    "where " +
                    "id_str_rt = " + workstation.getStoreID() + " and " +
                    "id_ws = " + workstation.getWorkstationID() + "\n";

                dataConnection.execute(sqlString);
            }

            // Select the transaction sequence number
            sqlString =
                "select ai_trn from ai_trn_gen " +
                "where " +
                "id_str_rt = " + workstation.getStoreID() + " and " +
                "id_ws = " + workstation.getWorkstationID() + " \n";

            dataConnection.execute(sqlString);

            ResultSet resultSet = (ResultSet)dataConnection.getResult();
            while(resultSet.next())
            {
                sequenceNumber = resultSet.getLong(1);
                returnSequenceNum = new Long(sequenceNumber);
            }
            resultSet.close();

            if (returnSequenceNum == null)
            {
                throw new DataException(DataException.NO_DATA,
                    "No transaction sequence number was found proccessing the result set in JdbcReadTransactionSequenceNum.");
            }

            dataTransaction.setResult(returnSequenceNum);
        }
        catch(SQLException e)
        {
            ((JdbcDataConnection)dataConnection).logSQLException(e, "Processing result set.");
            throw new DataException(DataException.SQL_ERROR,
                "An SQL Error occurred proccessing the result set from selecting a transaction sequence number in JdbcReadTransactionSequenceNum.", e);
        }
    }

    /**
        Set all data members should be set to their initial state.
        <P>
        @exception DataException
     */
    public void initialize() throws DataException
    {
    }

}

