/* ===========================================================================
* Copyright (c) 1998, 2011, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/modules/domain/src/oracle/retail/stores/domain/arts/JdbcSaveIXRetailXMLPosLog.java /rgbustores_13.4x_generic_branch/1 2011/05/04 11:48:57 mszekely Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    cgreene   05/26/10 - convert to oracle packaging
 *    abondala  01/03/10 - update header date
 *
 * ===========================================================================
 * $Log:
 *    4    360Commerce 1.3         12/13/2005 4:43:45 PM  Barry A. Pape
 *         Base-lining of 7.1_LA
 *    3    360Commerce 1.2         3/31/2005 4:28:44 PM   Robert Pearse   
 *    2    360Commerce 1.1         3/10/2005 10:22:48 AM  Robert Pearse   
 *    1    360Commerce 1.0         2/11/2005 12:12:03 PM  Robert Pearse   
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
 *   Revision 1.3  2004/02/12 17:13:18  mcs
 *   Forcing head revision
 *
 *   Revision 1.2  2004/02/11 23:25:24  bwf
 *   @scr 0 Organize imports.
 *
 *   Revision 1.1.1.1  2004/02/11 01:04:28  cschellenger
 *   updating to pvcs 360store-current
 *
 *
 * 
 *    Rev 1.0   Aug 29 2003 15:32:48   CSchellenger
 * Initial revision.
 * 
 *    Rev 1.1   Jul 01 2003 13:30:14   jgs
 * Modified logging statements.
 * Resolution for 1157: Add task for Importing IX Retail Transactions.
 * 
 *    Rev 1.0   Jun 12 2003 10:35:48   jgs
 * Initial revision.
 * Resolution for 1157: Add task for Importing IX Retail Transactions.
 * 
 *    Rev 1.2   Jan 22 2003 09:39:38   mpm
 * Preliminary merging of 5.1/5.5 code.
 * Resolution for Domain SCR-104: Merge 5.1/5.5 into 6.0
 * ===========================================================================
 */
package oracle.retail.stores.domain.arts;

// java imports
import java.io.StringReader;
import java.sql.Connection;

import org.apache.log4j.Logger;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import oracle.retail.stores.commerceservices.ixretail.IXRetailConstantsV21Ifc;
import oracle.retail.stores.commerceservices.xmltosql.JdbcSaveIXRetailTransactionCS;
import oracle.retail.stores.foundation.manager.data.DataException;
import oracle.retail.stores.foundation.manager.data.JdbcDataConnection;
import oracle.retail.stores.foundation.manager.ifc.data.DataActionIfc;
import oracle.retail.stores.foundation.manager.ifc.data.DataConnectionIfc;
import oracle.retail.stores.foundation.manager.ifc.data.DataTransactionIfc;
import oracle.retail.stores.foundation.manager.xml.InvalidXmlException;
import oracle.retail.stores.foundation.utility.Util;
import oracle.retail.stores.foundation.utility.xml.XMLUtility;

//-------------------------------------------------------------------------
/**
    This operation is the base operation for saving all transactions in the
    CRF POS.  It contains the method that saves to the transaction table
    in the database.
    <P>
    @version $Revision: /rgbustores_13.4x_generic_branch/1 $
**/
//-------------------------------------------------------------------------
public class JdbcSaveIXRetailXMLPosLog extends JdbcDataOperation
{
    /**
        The logger to which log messages will be sent.
    **/
    private static Logger logger = Logger.getLogger(oracle.retail.stores.domain.arts.JdbcSaveIXRetailXMLPosLog.class);

    /**
       revision number supplied by source-code-control system
    **/
    public static String revisionNumber = "$Revision: /rgbustores_13.4x_generic_branch/1 $";

    //---------------------------------------------------------------------
    /**
        Class constructor.
     **/
    //---------------------------------------------------------------------
    public JdbcSaveIXRetailXMLPosLog()
    {
        super();
        setName("JdbcSaveIXRetailXMLTLog");
    }

    //---------------------------------------------------------------------
    /**
        Executes the SQL statements against the database.
        <P>
        @param  dataTransaction     The data transaction
        @param  dataConnection      The connection to the data source
        @param  action              The information passed by the valet
        @exception DataException upon error
    **/
    //---------------------------------------------------------------------
    public void execute(DataTransactionIfc dataTransaction,
                        DataConnectionIfc dataConnection,
                        DataActionIfc action)
                        throws DataException
    {
        if (logger.isDebugEnabled()) logger.debug( "JdbcSaveIXRetailXMLPosLog.execute()");

        JdbcDataConnection connection = (JdbcDataConnection)dataConnection;
        // Create an XML Document from the XML String.
        String       sTrans  = (String)action.getDataObject();
        StringReader rTrans  = new StringReader(sTrans);
        // validateXML must come from a user setable source such as a property.
        boolean  validateXML = false;

        try
        { 
            Document document    = XMLUtility.getDocument(rTrans, validateXML, true);
            Element transaction  = document.getDocumentElement();
            String attribute = transaction.getAttribute(IXRetailConstantsV21Ifc.ELEMENT_EXTENDED_NAMESPACE_TAG);
            // Determine if POSLog 2.1 or 1.0
            if(IXRetailConstantsV21Ifc.ATTRIBUTE_EXTENDED_NAMESPACE.equals(attribute))
            {
                processPosLog21(connection, transaction);
            }
            else
            {
                processPosLog(connection, transaction);
            }
        }
        catch (InvalidXmlException ixe)
        {
            logger.error( "" + ixe + "");
            throw new DataException(DataException.DATA_FORMAT, "insertRetailTransaction", ixe);
        }

        if (logger.isDebugEnabled()) logger.debug( "JdbcSaveIXRetailXMLPosLog.execute()");
    }

    /**
     * Process the import from POSLog v 1.0
     * 
     * @param connection
     * @param transaction
     * @throws DataException
     * @since NEP67
     */
    public void processPosLog(JdbcDataConnection connection, Element transaction) throws DataException
    {
        // This class must be called out explicitly because there is class with
        // the same name in this package.
        
        JdbcSaveIXRetailTransactionCS jsrt = new JdbcSaveIXRetailTransactionCS();
        Connection conn = connection.getConnection();
        jsrt.insertTransaction(conn, transaction);
    }
    
    /**
     * Process the import from PosLog v 2.1.  This is used in
     * centralized returns.
     * @param connection
     * @param posLog
     * @throws DataException
     * @since NEP67
     */
    public void processPosLog21(JdbcDataConnection connection, Element posLog) throws DataException
    {
        // This class must be called out explicitly because there is import with
        // the same name in this class (for version 1.0).
     
        NodeList children = posLog.getChildNodes();
        for(int i=0; i<children.getLength(); i++)
        {
            if(children.item(i).getNodeName().equals(IXRetailConstantsV21Ifc.ELEMENT_TRANSACTION))
            {
                Element transaction = (Element) children.item(i);
                oracle.retail.stores.commerceservices.xmltosql.v21.JdbcSaveIXRetailTransactionCS jsrt = null;
                jsrt = new oracle.retail.stores.commerceservices.xmltosql.v21.JdbcSaveIXRetailTransactionCS();
                Connection conn = connection.getConnection();        
                jsrt.insertTransaction(conn, transaction);
            }
        }
    }
    //---------------------------------------------------------------------
    /**
        Method to default display string function. <P>
        @return String representation of object
    **/
    //---------------------------------------------------------------------
    public String toString()
    {
        // result string
        StringBuffer strResult = new StringBuffer("Class:  ");
        strResult.append(getClass().getName() + " (Revision ")
                 .append(revisionNumber)
                 .append(") @").append(hashCode())
                 .append(Util.EOL);

        return(strResult.toString());
    }

    //---------------------------------------------------------------------
    /**
       Returns the source-code-control system revision number. <P>
       @return String representation of revision number
    **/
    //---------------------------------------------------------------------
    public String getRevisionNumber()
    {
        return(revisionNumber);
    }
}
