/* ===========================================================================
* Copyright (c) 2006, 2011, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/modules/domain/src/oracle/retail/stores/domain/arts/JdbcReadDataReplicationEntity.java /rgbustores_13.4x_generic_branch/1 2011/05/04 11:49:02 mszekely Exp $
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
 *
 * ===========================================================================
 * $Log:
 *    1    360Commerce 1.0         11/9/2006 6:42:35 PM   Jack G. Swan    
 *
 * ===========================================================================
 */
package oracle.retail.stores.domain.arts;

import java.sql.Connection;
import java.sql.SQLException;

import oracle.retail.stores.persistence.utility.ARTSDatabaseIfc;

import org.apache.log4j.Logger;

import oracle.retail.stores.foundation.manager.data.DataException;
import oracle.retail.stores.foundation.manager.data.JdbcDataConnection;
import oracle.retail.stores.foundation.manager.ifc.data.DataActionIfc;
import oracle.retail.stores.foundation.manager.ifc.data.DataConnectionIfc;
import oracle.retail.stores.foundation.manager.ifc.data.DataTransactionIfc;
import oracle.retail.stores.xmlreplication.ExtractorObjectFactory;
import oracle.retail.stores.xmlreplication.ReplicationObjectFactoryContainer;
import oracle.retail.stores.xmlreplication.extractor.EntityReaderCatalogIfc;
import oracle.retail.stores.xmlreplication.extractor.EntitySearchIfc;
import oracle.retail.stores.xmlreplication.extractor.ReplicationExportException;
import oracle.retail.stores.xmlreplication.result.EntityIfc;

/**
 * This class provides the methods needed to read a data replication entity.
 * 
 * @version $Revision: /rgbustores_13.4x_generic_branch/1 $
 */
public class JdbcReadDataReplicationEntity extends JdbcDataOperation implements ARTSDatabaseIfc
{
    private static final long serialVersionUID = 5995098930325642401L;

    /**
     * revision number of this class
     */
    public static final String revisionNumber = "$Revision: /rgbustores_13.4x_generic_branch/1 $";

    /**
     * The logger to which log messages will be sent.
     */
    private static final Logger logger = Logger.getLogger(JdbcReadDataReplicationEntity.class);

    protected String name = "ReadTransactionsForDataReplication";

    /**
     * This methods reads the data associated with the
     * DataReplicationSearchCriteria object.
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
        if (logger.isDebugEnabled()) logger.debug("Entry");
        
        // Instanciate the importer object factory and set it on the 
        // ReplicationOjectFactoryContainer;  The container is singleton
        // which gives all classes access to the factory.
        if (ReplicationObjectFactoryContainer.getInstance().getExtractorObjectFactory() == null)
        {
            ExtractorObjectFactory factory = new ExtractorObjectFactory();
            ReplicationObjectFactoryContainer.getInstance().setExtractorObjectFactory(factory);
        }

        // Get the objects require to perform the querry
        Connection connection = ((JdbcDataConnection)dataConnection).getConnection();
        DataReplicationSearchCriteria criteria = (DataReplicationSearchCriteria)action.getDataObject();
        EntityReaderCatalogIfc entityReaderCatalog = criteria.getCatalog();
		EntitySearchIfc entitySearch = criteria.getEntitySearch();

		// For each transaction ID in the file, generate an entity, i.e.
        // a class that contains all the information associated with a 
        // a single transaction.  Save each transaction in an EntityBatch. 
		EntityIfc entity = ReplicationObjectFactoryContainer.getInstance().
        getExtractorObjectFactory().getEntityInstance("Transaction");
		try
		{
	        if (logger.isDebugEnabled()) logger.debug(entitySearch);
			entityReaderCatalog.readEntity(entity, entitySearch, connection);
		}
		catch (ReplicationExportException dee)
		{
			int type = DataException.UNKNOWN;
			if (dee.getCause() instanceof SQLException)
			{
				type = DataException.SQL_ERROR;
			}
			throw new DataException(type, "Error reading replication data.", dee);
		}
		
		dataTransaction.setResult(entity);
		
        if (logger.isDebugEnabled()) logger.debug("Exit");
    }

	    /**
       Retrieves the source-code-control system revision number. <P>
       @return String representation of revision number
     */
    public String getRevisionNumber()
    {
        // return string
        return(revisionNumber);
    }
}
