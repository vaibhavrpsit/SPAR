/* ===========================================================================
* Copyright (c) 1998, 2012, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/modules/domain/src/oracle/retail/stores/domain/arts/JdbcInsertStoreCredit.java /main/14 2012/03/27 10:57:13 jswan Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    jswan     03/21/12 - Modified to support centralized gift certificate and
 *                         store credit.
 *    cgreene   05/27/10 - convert to oracle packaging
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
 *    6    360Commerce 1.5         1/28/2008 4:31:09 PM   Sandy Gu
 *         Export foreign currency id, code and exchange rate for store credit
 *          and gift certificate foreign tender.
 *    5    360Commerce 1.4         5/12/2006 5:26:23 PM   Charles D. Baker
 *         Merging with v1_0_0_53 of Returns Managament
 *    4    360Commerce 1.3         1/25/2006 4:11:08 PM   Brett J. Larsen merge
 *          7.1.1 changes (aka. 7.0.3 fixes) into 360Commerce view
 *    3    360Commerce 1.2         3/31/2005 4:28:37 PM   Robert Pearse   
 *    2    360Commerce 1.1         3/10/2005 10:22:38 AM  Robert Pearse   
 *    1    360Commerce 1.0         2/11/2005 12:11:55 PM  Robert Pearse   
 *:
 *    4    .v700     1.2.1.0     11/17/2005 16:10:46    Jason L. DeLeau 4345:
 *         Replace any uses of Gateway.log() with the log4j.
 *    3    360Commerce1.2         3/31/2005 15:28:37     Robert Pearse
 *    2    360Commerce1.1         3/10/2005 10:22:38     Robert Pearse
 *    1    360Commerce1.0         2/11/2005 12:11:55     Robert Pearse
 *
 *   Revision 1.3  2004/02/12 17:13:14  mcs
 *   Forcing head revision
 *
 *   Revision 1.2  2004/02/11 23:25:24  bwf
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
 *    Rev 1.0   Jun 03 2002 16:36:36   msg
 * Initial revision.
 *
 *    Rev 1.1   Mar 18 2002 22:46:58   msg
 * - updated copyright
 *
 *    Rev 1.0   Mar 18 2002 12:07:02   msg
 * Initial revision.
 *
 *    Rev 1.0   Sep 20 2001 15:59:00   msg
 * Initial revision.
 *
 *    Rev 1.1   Sep 17 2001 12:34:36   msg
 * header update
 * ===========================================================================
 */
package oracle.retail.stores.domain.arts;

import org.apache.log4j.Logger;

import oracle.retail.stores.common.sql.SQLInsertStatement;
import oracle.retail.stores.domain.utility.StoreCreditIfc;
import oracle.retail.stores.foundation.manager.data.DataException;
import oracle.retail.stores.foundation.manager.data.JdbcDataConnection;
import oracle.retail.stores.foundation.manager.ifc.data.DataActionIfc;
import oracle.retail.stores.foundation.manager.ifc.data.DataConnectionIfc;
import oracle.retail.stores.foundation.manager.ifc.data.DataTransactionIfc;

/**
 * This operation saves the store credit.
 * 
 * @version $Revision: /main/14 $
 * @deprecated in 14.0; This class is not used by ORPOS
 */
public class JdbcInsertStoreCredit extends JdbcSaveStoreCredit
{
    private static final long serialVersionUID = 9146345942500125164L;

    /**
     * revision number supplied by Team Connection
     */
    public static final String revisionNumber = "$Revision: /main/14 $";

    /**
     * Logger (client.log)
     */
    private static final Logger logger = Logger.getLogger(JdbcInsertStoreCredit.class);

    /**
     * Class constructor.
     */
    public JdbcInsertStoreCredit()
    {
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
        if (logger.isDebugEnabled()) logger.debug( "JdbcInsertStoreCredit.execute()");

        // Down cast the connecion and call the select
        JdbcDataConnection connection = (JdbcDataConnection)dataConnection;

        StoreCreditIfc sc = (StoreCreditIfc) action.getDataObject();

        dataTransaction.setResult(insertStoreCredit(connection, sc));
    }

    /**
     * Executes the insert statement against the db.
     * 
     * @param connection a Jdbcconnection object
     * @param store credit the object to put in the DB.
     * @exception DataException upon error
     */
    protected Integer insertStoreCredit(JdbcDataConnection connection, StoreCreditIfc sc)
                                        throws DataException
    {
    	// build insert SQL statement
    	SQLInsertStatement sql = buildInsertStoreCreditSQL(sc);

        // Execute the SQL statement
        connection.execute(sql.getSQLString());

        if (connection.getUpdateCount() < 1)
        {
            logger.error( "Store Credit Insert count was not greater than 0");
            throw new DataException(DataException.UNKNOWN, "Store Credit Insert count was not greater than 0");
        }

        return (Integer.valueOf(connection.getUpdateCount()));
    }

    /**
     * Returns a string representation of this object.
     * 
     * @param none
     * @return String representation of object
     */
    @Override
    public String toString()
    {
        // result string
        String strResult = new String("Class:  JdbcInsertStoreCredit (Revision " +
                                      getRevisionNumber() +
                                      ")" + hashCode());

        // pass back result
        return (strResult);
    }

    /**
     * Returns the revision number of the class.
     * 
     * @param none
     * @return String representation of revision number
     */
    public String getRevisionNumber()
    {
        // return string
        return (revisionNumber);
    }

}
