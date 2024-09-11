/* ===========================================================================
* Copyright (c) 1998, 2011, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/modules/domain/src/oracle/retail/stores/domain/arts/JdbcIncrementWorkstationSequenceNumber.java /rgbustores_13.4x_generic_branch/1 2011/05/04 11:49:05 mszekely Exp $
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
 *    4    360Commerce 1.3         1/25/2006 4:11:08 PM   Brett J. Larsen merge
 *          7.1.1 changes (aka. 7.0.3 fixes) into 360Commerce view
 *    3    360Commerce 1.2         3/31/2005 4:28:37 PM   Robert Pearse   
 *    2    360Commerce 1.1         3/10/2005 10:22:38 AM  Robert Pearse   
 *    1    360Commerce 1.0         2/11/2005 12:11:54 PM  Robert Pearse   
 *:
 *    4    .v700     1.2.1.0     11/16/2005 16:26:18    Jason L. DeLeau 4215:
 *         Get rid of redundant ArtsDatabaseifc class
 *    3    360Commerce1.2         3/31/2005 15:28:37     Robert Pearse
 *    2    360Commerce1.1         3/10/2005 10:22:38     Robert Pearse
 *    1    360Commerce1.0         2/11/2005 12:11:54     Robert Pearse
 *
 *   Revision 1.7  2004/08/13 13:42:11  kll
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
 *   Revision 1.1.1.1  2004/02/11 01:04:26  cschellenger
 *   updating to pvcs 360store-current
 *
 *
 *
 *    Rev 1.0   Aug 29 2003 15:30:44   CSchellenger
 * Initial revision.
 *
 *    Rev 1.1   Dec 18 2002 16:58:48   baa
 * Add employee/customer language preferrence support
 * Resolution for POS SCR-1843: Multilanguage support
 *
 *    Rev 1.0   Jun 03 2002 16:36:20   msg
 * Initial revision.
 *
 *    Rev 1.1   Mar 18 2002 22:46:38   msg
 * - updated copyright
 *
 *    Rev 1.0   Mar 18 2002 12:06:46   msg
 * Initial revision.
 *
 *    Rev 1.0   Dec 04 2001 10:04:22   jfv
 * Initial revision.
 *
 *
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
import oracle.retail.stores.foundation.manager.ifc.data.DataActionIfc;
import oracle.retail.stores.foundation.manager.ifc.data.DataConnectionIfc;
import oracle.retail.stores.foundation.manager.ifc.data.DataTransactionIfc;

/**
 * This operation increments by one the workstation sequence number.
 * 
 * @version $Revision: /rgbustores_13.4x_generic_branch/1 $
 */
public class JdbcIncrementWorkstationSequenceNumber extends JdbcDataOperation
    implements ARTSDatabaseIfc
{
    private static final long serialVersionUID = 1562148361929576781L;

    /**
     * The logger to which log messages will be sent.
     */
    private static final Logger logger = Logger.getLogger(JdbcIncrementWorkstationSequenceNumber.class);

    /**
     * revision number supplied by source-code-control system
     */
    public static final String revisionNumber = "$Revision: /rgbustores_13.4x_generic_branch/1 $";

    /**
     * Class constructor.
     */
    public JdbcIncrementWorkstationSequenceNumber()
    {
        super();
        setName("JdbcIncrementWorkstationSequenceNumber");
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
        if (logger.isDebugEnabled()) logger.debug( "JdbcIncrementWorkstationSequenceNumber.execute()");
        Long seqNumber = new Long(0);

        // get transaction object
        TransactionIfc transaction = (TransactionIfc) action.getDataObject();

        seqNumber = IncrementWorkstationSequenceNumber(dataConnection, transaction);

        dataTransaction.setResult(seqNumber);

        if (logger.isDebugEnabled()) logger.debug( "JdbcIncrementWorkstationSequenceNumber.execute()");
    }

    /**
       Increments the workstation sequence number by one and then reads
       the new value.
       To do: Make sure that the new value does not exceed
       the maximum sequence number allowed by the application.  It would
       need to be rolled over to 0.<P>
       @param  dataConnection      The connection to the data source
       @param  transaction         The transaction
       @return next sequence number
       @exception DataException upon error
     */
    public Long  IncrementWorkstationSequenceNumber(DataConnectionIfc dataConnection,
                                               TransactionIfc transaction)
        throws DataException
    {
        Long seqNumber = new Long(0);
        int intSeqNumber = 0;

        String storeID = getStoreID(transaction);
        String workstationID = getWorkstationID(transaction);
        /*
         * Increment sequence number by one before reading it back.
         */
        updateWorkstationSequenceNumber(dataConnection, storeID, workstationID);

        /*
         * Now read the new value.  Doing this in the same
         * data action will ensure that we'll get the just
         * updated value.
         */
        SQLSelectStatement sql = new SQLSelectStatement();

        /*
         * Define the table
         */
        sql.setTable(TABLE_WORKSTATION);

        /*
         * Add column
         */
        sql.addColumn(FIELD_TRANSACTION_SEQUENCE_NUMBER);

        /*
         * Add Qualifier(s)
         */
        sql.addQualifier(FIELD_RETAIL_STORE_ID + " = " + storeID);
        sql.addQualifier(FIELD_WORKSTATION_ID + " = " + workstationID);


        try
        {
            dataConnection.execute(sql.getSQLString());

            ResultSet resultSet = (ResultSet)dataConnection.getResult();

            if (resultSet.next())
            {
                // Gets an int, convert to Long (serializable)
                // to be able to return it using the valet.
                intSeqNumber = resultSet.getInt(1);
                seqNumber = new Long(intSeqNumber);
            }

            resultSet.close();

        }
        catch (SQLException se)
        {
            logger.error(
                         se.toString());
            throw new DataException(DataException.SQL_ERROR, "Select Sequence Number", se);
        }

        return seqNumber;
    }

    /**
       Increments the workstation sequence number by one
       To do: Make sure that the new value does not exceed
       the maximum sequence number allowed by the application.  It would
       need to be rolled over to 0.<P>
       @param  dataConnection
       @param  storeID
       @param workstationID
       @exception DataException upon error
     */
    private void  updateWorkstationSequenceNumber(DataConnectionIfc dataConnection,
                          String storeID,
                          String workstationID)
        throws DataException

    {

        // Update the workstation table by incrementing the
        // current sequence number
        String sqlString = "\n"
            + "UPDATE " + TABLE_WORKSTATION
            + "\nSET " + FIELD_WORKSTATION_SEQUENCE_NUMBER
            + " = " + FIELD_WORKSTATION_SEQUENCE_NUMBER + " + 1 "
            + "WHERE " + FIELD_RETAIL_STORE_ID + " = " + storeID
            + "AND " + FIELD_WORKSTATION_ID + " = " + workstationID
            + " \n";

        dataConnection.execute(sqlString);

    }

    /**
       Returns the Workstation ID enclosed in quotes
       <P>
       @param  transaction a pos transaction
       @return  The Workstation ID
     */
    public String getWorkstationID(TransactionIfc transaction)
    {
        return("'" + transaction.getWorkstation().getWorkstationID() + "'");
    }

    /**
       Returns the Store ID enclosed in quotes
       <P>
       @param  transaction a pos transaction
       @return  The Store ID
     */
    public String getStoreID(TransactionIfc transaction)
    {
        return("'" + transaction.getWorkstation().getStoreID() + "'");
    }

    /**
       Returns the source-code-control system revision number. <P>
       @return String representation of revision number
     */
    public String getRevisionNumber()
    {
        return(revisionNumber);
    }
}
