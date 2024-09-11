/* ===========================================================================
* Copyright (c) 1998, 2011, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/modules/domain/src/oracle/retail/stores/domain/arts/JdbcReadStoreStatus.java /rgbustores_13.4x_generic_branch/1 2011/05/04 11:48:56 mszekely Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    cgreene   05/27/10 - convert to oracle packaging
 *    cgreene   05/27/10 - convert to oracle packaging
 *    cgreene   05/26/10 - convert to oracle packaging
 *    cgreene   04/26/10 - XbranchMerge cgreene_tech75 from
 *                         st_rgbustores_techissueseatel_generic_branch
 *    cgreene   03/30/10 - remove deprecated ARTSDatabaseIfcs and change
 *                         SQLException to DataException
 *    yiqzhao   03/12/10 - add readStoreStatus method with store id and date as
 *                         the parameters.
 *    abondala  01/03/10 - update header date
 *
 * ===========================================================================
 * $Log:
 *    4    360Commerce 1.3         1/25/2006 4:11:18 PM   Brett J. Larsen merge
 *          7.1.1 changes (aka. 7.0.3 fixes) into 360Commerce view
 *    3    360Commerce 1.2         3/31/2005 4:28:41 PM   Robert Pearse
 *    2    360Commerce 1.1         3/10/2005 10:22:45 AM  Robert Pearse
 *    1    360Commerce 1.0         2/11/2005 12:12:00 PM  Robert Pearse
 *:
 *    4    .v700     1.2.1.0     11/16/2005 16:26:41    Jason L. DeLeau 4215:
 *         Get rid of redundant ArtsDatabaseifc class
 *    3    360Commerce1.2         3/31/2005 15:28:41     Robert Pearse
 *    2    360Commerce1.1         3/10/2005 10:22:45     Robert Pearse
 *    1    360Commerce1.0         2/11/2005 12:12:00     Robert Pearse
 *
 *   Revision 1.6  2004/04/09 16:55:46  cdb
 *   @scr 4302 Removed double semicolon warnings.
 *
 *   Revision 1.5  2004/02/17 17:57:36  bwf
 *   @scr 0 Organize imports.
 *
 *   Revision 1.4  2004/02/17 16:18:45  rhafernik
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
 *    Rev 1.0   Aug 29 2003 15:32:10   CSchellenger
 * Initial revision.
 *
 *    Rev 1.0   Jun 03 2002 16:38:18   msg
 * Initial revision.
 *
 *    Rev 1.2   25 Mar 2002 12:30:04   epd
 * Jose asked me to check these in.  Updates to use TenderDescriptor
 * Resolution for POS SCR-216: Making POS changes to accommodate OnlineOffice
 *
 *    Rev 1.1   03 Dec 2001 16:09:34   epd
 * added code to add Store safe tender types
 * Resolution for POS SCR-216: Making POS changes to accommodate OnlineOffice
 *
 *    Rev 1.0   Sep 20 2001 15:59:52   msg
 * Initial revision.
 *
 *    Rev 1.1   Sep 17 2001 12:34:20   msg
 * header update
 * ===========================================================================
 */
package oracle.retail.stores.domain.arts;

import oracle.retail.stores.persistence.utility.ARTSDatabaseIfc;

import org.apache.log4j.Logger;

import oracle.retail.stores.domain.financial.StoreStatusIfc;
import oracle.retail.stores.domain.tender.TenderDescriptorIfc;
import oracle.retail.stores.foundation.manager.data.DataException;
import oracle.retail.stores.foundation.manager.data.JdbcDataConnection;
import oracle.retail.stores.foundation.manager.ifc.data.DataActionIfc;
import oracle.retail.stores.foundation.manager.ifc.data.DataConnectionIfc;
import oracle.retail.stores.foundation.manager.ifc.data.DataTransactionIfc;

/**
 * This data operation reads the current status of a store
 * 
 * @version $Revision: /rgbustores_13.4x_generic_branch/1 $
 */
public class JdbcReadStoreStatus extends JdbcReadStore implements ARTSDatabaseIfc
{
    private static final long serialVersionUID = -506102810843427936L;
    /**
     * The logger to which log messages will be sent.
     */
    private static final Logger logger = Logger.getLogger(JdbcReadStoreStatus.class);

    /**
     * Class constructor.
     */
    public JdbcReadStoreStatus()
    {
        super();
        setName("JdbcReadStoreStatus");
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
        if (logger.isDebugEnabled()) logger.debug( "JdbcReadStoreStatus.execute()");

        /*
         * getUpdateCount() is about the only thing outside of
         * DataConnectionIfc that we need.
         */
        JdbcDataConnection connection = (JdbcDataConnection)dataConnection;

        StoreStatusIfc status = null;
        if ( action.getDataObject() instanceof ARTSStore )
        {
        	//Read store status by store id and date
        	ARTSStore artsStore = (ARTSStore)action.getDataObject();

            status = readStoreStatus(connection,
            						artsStore.getPosStore().getStoreID(),
            						artsStore.getBusinessDate());
        }
        else
        {
        	//Read store status by store id only
	        String storeID = (String)action.getDataObject();
	        status = readStoreStatus(connection, storeID);

	        // Read and set the tendersForStoreSafe in the status object
	        //String[] tenderList = readSafeTenders(connection);
	        TenderDescriptorIfc[] tenderDescList = readSafeTenders(connection);
	        for (int i = 0; i < tenderDescList.length; i++)
	        {
	            status.addSafeTenderDesc(tenderDescList[i]);
	        }
        }

        /*
         * Send back the result
         */
        dataTransaction.setResult(status);

        if (logger.isDebugEnabled()) logger.debug( "JdbcReadStoreStatus.execute()");
    }
}
