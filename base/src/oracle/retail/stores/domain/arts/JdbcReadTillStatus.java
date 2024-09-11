/* ===========================================================================
* Copyright (c) 1998, 2011, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/modules/domain/src/oracle/retail/stores/domain/arts/JdbcReadTillStatus.java /rgbustores_13.4x_generic_branch/1 2011/05/04 11:48:58 mszekely Exp $
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
 *    4    360Commerce 1.3         1/25/2006 4:11:18 PM   Brett J. Larsen merge
 *          7.1.1 changes (aka. 7.0.3 fixes) into 360Commerce view
 *    3    360Commerce 1.2         3/31/2005 4:28:42 PM   Robert Pearse   
 *    2    360Commerce 1.1         3/10/2005 10:22:46 AM  Robert Pearse   
 *    1    360Commerce 1.0         2/11/2005 12:12:00 PM  Robert Pearse   
 *:
 *    4    .v700     1.2.1.0     11/16/2005 16:26:21    Jason L. DeLeau 4215:
 *         Get rid of redundant ArtsDatabaseifc class
 *    3    360Commerce1.2         3/31/2005 15:28:42     Robert Pearse
 *    2    360Commerce1.1         3/10/2005 10:22:46     Robert Pearse
 *    1    360Commerce1.0         2/11/2005 12:12:00     Robert Pearse
 *
 *   Revision 1.6  2004/04/09 16:55:46  cdb
 *   @scr 4302 Removed double semicolon warnings.
 *
 *   Revision 1.5  2004/02/17 17:57:37  bwf
 *   @scr 0 Organize imports.
 *
 *   Revision 1.4  2004/02/17 16:18:46  rhafernik
 *   @scr 0 log4j conversion
 *
 *   Revision 1.3  2004/02/12 17:13:17  mcs
 *   Forcing head revision
 *
 *   Revision 1.2  2004/02/11 23:25:23  bwf
 *   @scr 0 Organize imports.
 *
 *   Revision 1.1.1.1  2004/02/11 01:04:27  cschellenger
 *   updating to pvcs 360store-current
 *
 *
 *
 *    Rev 1.0   Aug 29 2003 15:32:14   CSchellenger
 * Initial revision.
 *
 *    Rev 1.0   Jun 03 2002 16:38:24   msg
 * Initial revision.
 *
 *    Rev 1.1   Mar 18 2002 22:47:46   msg
 * - updated copyright
 *
 *    Rev 1.0   Mar 18 2002 12:07:44   msg
 * Initial revision.
 *
 *    Rev 1.0   Sep 20 2001 16:00:16   msg
 * Initial revision.
 *
 *    Rev 1.1   Sep 17 2001 12:34:18   msg
 * header update
 * ===========================================================================
 */
package oracle.retail.stores.domain.arts;

import oracle.retail.stores.persistence.utility.ARTSDatabaseIfc;

import org.apache.log4j.Logger;

import oracle.retail.stores.domain.financial.TillIfc;
import oracle.retail.stores.foundation.manager.data.DataException;
import oracle.retail.stores.foundation.manager.data.JdbcDataConnection;
import oracle.retail.stores.foundation.manager.ifc.data.DataActionIfc;
import oracle.retail.stores.foundation.manager.ifc.data.DataConnectionIfc;
import oracle.retail.stores.foundation.manager.ifc.data.DataTransactionIfc;

/**
 * This operation reads the workstation information.
 * 
 * @version $Revision: /rgbustores_13.4x_generic_branch/1 $
 */
public class JdbcReadTillStatus extends JdbcReadTill implements ARTSDatabaseIfc
{
    private static final long serialVersionUID = -3680535471370701701L;

    /**
     * The logger to which log messages will be sent.
     */
    private static final Logger logger = Logger.getLogger(JdbcReadTillStatus.class);

    /**
     * Class constructor.
     */
    public JdbcReadTillStatus()
    {
        super();
        setName("JdbcReadTillStatus");
    }

    /**
     * Executes the SQL statements against the database.
     * 
     * @param dataTransaction The data transaction
     * @param dataConnection The connection to the data source
     * @param action The information passed by the valet
     * @exception DataException upon error
     */
    public void execute(DataTransactionIfc dataTransaction, DataConnectionIfc dataConnection, DataActionIfc action)
            throws DataException
    {
        if (logger.isDebugEnabled())
            logger.debug("JdbcReadTillStatus.execute()");

        /*
         * getUpdateCount() is about the only thing outside of DataConnectionIfc
         * that we need.
         */
        JdbcDataConnection connection = (JdbcDataConnection) dataConnection;

        // Navigate the input object to obtain values that will be inserted
        // into the database.
        ARTSTill artsTill = (ARTSTill) action.getDataObject();
        TillIfc till = readTillStatus(connection, artsTill.getStoreID(), artsTill.getTillID());

        /*
         * Send back the result
         */
        dataTransaction.setResult(till);

        if (logger.isDebugEnabled())
            logger.debug("JdbcReadTillStatus.execute()");
    }

    /**
     * Returns the current status of the till
     * 
     * @param dataConnection connection to the db
     * @param storeID The store ID
     * @param tillID The till ID
     * @return the current status of the till
     * @exception DataException upon error
     */
    public TillIfc readTillStatus(JdbcDataConnection dataConnection, String storeID, String tillID)
            throws DataException
    {
        return (selectTill(dataConnection, storeID, tillID));
    }
}
