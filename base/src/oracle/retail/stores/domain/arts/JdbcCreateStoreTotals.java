/* ===========================================================================
* Copyright (c) 1998, 2012, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/modules/domain/src/oracle/retail/stores/domain/arts/JdbcCreateStoreTotals.java /main/14 2012/09/12 11:57:12 blarsen Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    cgreene   03/06/12 - removed call to deprecated method
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
 *    4    360Commerce 1.3         1/25/2006 4:11:07 PM   Brett J. Larsen merge
 *          7.1.1 changes (aka. 7.0.3 fixes) into 360Commerce view
 *    3    360Commerce 1.2         3/31/2005 4:28:36 PM   Robert Pearse   
 *    2    360Commerce 1.1         3/10/2005 10:22:36 AM  Robert Pearse   
 *    1    360Commerce 1.0         2/11/2005 12:11:53 PM  Robert Pearse   
 *:
 *    4    .v700     1.2.1.0     11/16/2005 16:28:10    Jason L. DeLeau 4215:
 *         Get rid of redundant ArtsDatabaseifc class
 *    3    360Commerce1.2         3/31/2005 15:28:36     Robert Pearse
 *    2    360Commerce1.1         3/10/2005 10:22:36     Robert Pearse
 *    1    360Commerce1.0         2/11/2005 12:11:53     Robert Pearse
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
 *   Revision 1.3  2004/02/12 17:13:13  mcs
 *   Forcing head revision
 *
 *   Revision 1.2  2004/02/11 23:25:23  bwf
 *   @scr 0 Organize imports.
 *
 *   Revision 1.1.1.1  2004/02/11 01:04:26  cschellenger
 *   updating to pvcs 360store-current
 *
 *
 *
 *    Rev 1.0   Aug 29 2003 15:30:26   CSchellenger
 * Initial revision.
 *
 *    Rev 1.0   Jun 03 2002 16:35:52   msg
 * Initial revision.
 *
 *    Rev 1.1   Mar 18 2002 22:46:36   msg
 * - updated copyright
 *
 *    Rev 1.0   Mar 18 2002 12:06:46   msg
 * Initial revision.
 *
 *    Rev 1.0   Sep 20 2001 15:58:10   msg
 * Initial revision.
 *
 *    Rev 1.1   Sep 17 2001 12:34:48   msg
 * header update
 * ===========================================================================
 */
package oracle.retail.stores.domain.arts;

import oracle.retail.stores.domain.store.StoreIfc;
import oracle.retail.stores.domain.utility.EYSDate;
import oracle.retail.stores.foundation.manager.data.DataException;
import oracle.retail.stores.foundation.manager.data.JdbcDataConnection;
import oracle.retail.stores.foundation.manager.ifc.data.DataActionIfc;
import oracle.retail.stores.foundation.manager.ifc.data.DataConnectionIfc;
import oracle.retail.stores.foundation.manager.ifc.data.DataTransactionIfc;
import oracle.retail.stores.persistence.utility.ARTSDatabaseIfc;

import org.apache.log4j.Logger;

/**
 * This operation performs inserts into the store history and reporting period
 * tables.
 * 
 * @version $Revision: /main/14 $
 */
public class JdbcCreateStoreTotals extends JdbcSaveStore implements ARTSDatabaseIfc
{
    private static final long serialVersionUID = 7049443666586961377L;
    /** The logger to which log messages will be sent. */
    private static final Logger logger = Logger.getLogger(JdbcCreateStoreTotals.class);

    /**
     * Class constructor.
     */
    public JdbcCreateStoreTotals()
    {
        setName("JdbcCreateStoreTotals");
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
            logger.debug("JdbcCreateStoreTotals.execute()");

        // getUpdateCount() is about the only thing outside of DataConnectionIfc
        // that we need.
        JdbcDataConnection connection = (JdbcDataConnection)dataConnection;

        // Navigate the input object to obtain values that will be inserted
        // into the database.
        ARTSStore artsStore = (ARTSStore)action.getDataObject();

        try
        {
            createStoreTotals(connection, artsStore.getPosStore(), artsStore.getBusinessDate());
        }
        catch (DataException de)
        {
            // check for REFERENTIAL_INTEGRITY exception, which indicates store
            // was already opened
            if (de.getErrorCode() == DataException.REFERENTIAL_INTEGRITY_ERROR)
            {
                logger.warn("Referential integrity error on store open is ignored.");
            }
            else
            {
                throw de;
            }
        }

        if (logger.isDebugEnabled())
            logger.debug("JdbcCreateStoreTotals.execute()");
    }

    /**
     * Creates all of the records needed to open a store for a new business day.
     * 
     * @param dataConnection connection to the db
     * @param store The store
     * @param businessDate The business date
     * @return true if successful
     * @exception DataException upon error
     */
    public void createStoreTotals(JdbcDataConnection dataConnection, StoreIfc store, EYSDate businessDate)
            throws DataException
    {
        // Create the business day first. This should not already exist.
        createBusinessDay(dataConnection, businessDate);

        // insert the Store History record.
        insertStoreHistory(dataConnection, store, businessDate, null);
    }
}
