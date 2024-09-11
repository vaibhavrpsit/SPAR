/* ===========================================================================
* Copyright (c) 2008, 2014, Oracle and/or its affiliates. All rights reserved.
 * ===========================================================================
 * $Header: rgbustores/modules/domain/src/oracle/retail/stores/domain/arts/JdbcSaveTaxMaintenance.java /main/16 2014/01/09 16:23:23 mjwallac Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    mjwallac  01/09/14 - fix null dereferences
 *    cgreene   05/27/10 - convert to oracle packaging
 *    cgreene   05/27/10 - convert to oracle packaging
 *    cgreene   05/26/10 - convert to oracle packaging
 *    cgreene   04/28/10 - updating deprecated names
 *    cgreene   04/26/10 - XbranchMerge cgreene_tech75 from
 *                         st_rgbustores_techissueseatel_generic_branch
 *    cgreene   03/30/10 - remove deprecated ARTSDatabaseIfcs and change
 *                         SQLException to DataException
 *    abondala  01/03/10 - update header date
 *
 * ===========================================================================
 */
package oracle.retail.stores.domain.arts;

import java.io.File;
import java.io.FileReader;

import oracle.retail.stores.persistence.utility.ARTSDatabaseIfc;

import org.apache.log4j.Logger;
import org.w3c.dom.Document;

import oracle.retail.stores.commerceservices.taximport.TaxImportResults;
import oracle.retail.stores.domain.ixretail.IXRetailConstantsIfc;
import oracle.retail.stores.foundation.manager.data.DataException;
import oracle.retail.stores.foundation.manager.data.JdbcDataConnection;
import oracle.retail.stores.foundation.manager.ifc.data.DataActionIfc;
import oracle.retail.stores.foundation.manager.ifc.data.DataConnectionIfc;
import oracle.retail.stores.foundation.manager.ifc.data.DataTransactionIfc;
import oracle.retail.stores.foundation.utility.xml.XMLUtility;

/**
 * This operation is used to save Tax data to the various Tax tables. This class
 * parses an XML Tax File and inserts data into the Tax Authority, Taxable
 * Group, Tax Group Rule, and Tax Rate Rule tables.
 * 
 * @version $Revision: /main/16 $
 * @deprecated as of 13.3. Tax import is done through DIMP.
 */
public class JdbcSaveTaxMaintenance extends JdbcDataOperation implements ARTSDatabaseIfc, IXRetailConstantsIfc
{
    private static final long serialVersionUID = 8640848660357067095L;

    /**
     * The logger to which log messages will be sent.
     */
    private static final Logger logger = Logger.getLogger(JdbcSaveTaxMaintenance.class);

    /**
     * revision number supplied by source-code-control system
     */
    public static final String revisionNumber = "$Revision: /main/16 $";

    /**
     * Class constructor.
     */
    public JdbcSaveTaxMaintenance()
    {
        super();
        setName("JdbcSaveTaxMaintenance");
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
        if (logger.isDebugEnabled()) logger.debug( "JdbcSaveTaxMaintenance.execute()");
        JdbcDataConnection connection = (JdbcDataConnection)dataConnection;

        // Get the XML Tax File from the DataAction.
        File taxFile = (File)action.getDataObject();
        if (taxFile == null)
        {
            String msg = "JdbcSaveTaxMaintenance: tax file not found!";
            logger.error(msg);
            return;
        }

        String msg = "JdbcSaveTaxMaintenance: tax file received!";
        logger.info(msg);

        FileReader reader = null;
        TaxImportResults results = null;
        try
        {
            reader = new FileReader(taxFile);
            Document document = XMLUtility.getDocument(reader, false, true);

            results = 
                (new oracle.retail.stores.commerceservices.taximport.JdbcSaveTaxMaintenance())
                    .importTaxRules(connection.getConnection(), document);                        

        }
        catch (DataException de)
        {
            logger.error(de);
            throw de;
        }
        catch (Exception e)
        {
            logger.error(e);             
            throw new DataException(DataException.UNKNOWN, "JdbcSaveTaxMaintenance", e);
        }
        finally
        {
            if (reader != null)
            {
                try
                {
                    reader.close();
                }
                catch(Throwable t) 
                {                  
                };
            }
        }                

        if (logger.isDebugEnabled()) logger.debug( "JdbcSaveTaxMaintenance.execute()");
        
        dataTransaction.setResult(results);
    }

    /**
        Method to default display string function. <P>
        @return String representation of object
     */
    @Override
    public String toString()
    {

        StringBuffer strResult = new StringBuffer("Class:  ");
        strResult.append(getClass().getName() + " (Revision ");
        strResult.append(revisionNumber);
        strResult.append(")");

        return strResult.toString();
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
