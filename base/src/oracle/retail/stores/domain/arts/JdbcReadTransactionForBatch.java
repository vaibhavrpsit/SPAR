/* ===========================================================================
* Copyright (c) 1998, 2011, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/modules/domain/src/oracle/retail/stores/domain/arts/JdbcReadTransactionForBatch.java /rgbustores_13.4x_generic_branch/1 2011/05/04 11:49:03 mszekely Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    cgreene   05/27/10 - convert to oracle packaging
 *    cgreene   05/26/10 - convert to oracle packaging
 *    abondala  01/03/10 - update header date
 *
 * ===========================================================================
 * $Log:
 *    3    360Commerce 1.2         3/31/2005 4:28:42 PM   Robert Pearse   
 *    2    360Commerce 1.1         3/10/2005 10:22:46 AM  Robert Pearse   
 *    1    360Commerce 1.0         2/11/2005 12:12:00 PM  Robert Pearse   
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
 *   Revision 1.1.1.1  2004/02/11 01:04:28  cschellenger
 *   updating to pvcs 360store-current
 *
 *
 * 
 *    Rev 1.0   Aug 29 2003 15:32:16   CSchellenger
 * Initial revision.
 * 
 *    Rev 1.0   Jun 03 2002 16:38:32   msg
 * Initial revision.
 * 
 *    Rev 1.2   Apr 28 2002 13:31:54   mpm
 * Completed translation of sale transactions.
 * Resolution for Domain SCR-45: TLog facility
 *
 *    Rev 1.1   Apr 11 2002 09:18:56   mpm
 * Migrated oracle/retail/stores/domain/translation/ixretail to oracle/retail/stores/domain/ixretail
 * Resolution for Domain SCR-45: TLog facility
 *
 *    Rev 1.0   Apr 08 2002 17:21:30   mpm
 * Initial revision.
 * ===========================================================================
 */
package oracle.retail.stores.domain.arts;
// java imports
import org.apache.log4j.Logger;

import oracle.retail.stores.common.utility.LocaleRequestor;
import oracle.retail.stores.domain.transaction.TransactionIfc;
import oracle.retail.stores.domain.transaction.VoidTransactionIfc;
import oracle.retail.stores.foundation.manager.data.DataException;
import oracle.retail.stores.foundation.manager.data.JdbcDataConnection;
import oracle.retail.stores.foundation.manager.ifc.data.DataActionIfc;
import oracle.retail.stores.foundation.manager.ifc.data.DataConnectionIfc;
import oracle.retail.stores.foundation.manager.ifc.data.DataTransactionIfc;

//--------------------------------------------------------------------------
/**
    This operation reads a transaction for batch processing.  It includes
    training-mode and voided transactions.
    @version $Revision: /rgbustores_13.4x_generic_branch/1 $
**/
//--------------------------------------------------------------------------
public class JdbcReadTransactionForBatch
extends JdbcReadTransaction
{
    /**
        The logger to which log messages will be sent.
    **/
    private static Logger logger = Logger.getLogger(oracle.retail.stores.domain.arts.JdbcReadTransactionForBatch.class);
    /**
       revision number supplied by source-code-control system
    **/
    public static String revisionNumber = "$Revision: /rgbustores_13.4x_generic_branch/1 $";

    //----------------------------------------------------------------------
    /**
       Class constructor.
    **/
    //----------------------------------------------------------------------
    public JdbcReadTransactionForBatch()
    {
        super();
        setName("JdbcReadTransactionForBatch");
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
        if (logger.isDebugEnabled()) logger.debug( "JdbcReadTransactionForBatch.execute");

        // set data connection
        JdbcDataConnection connection = (JdbcDataConnection) dataConnection;

        // grab arguments and call readTransactionForBatch()
        TransactionIfc searchTransaction = (TransactionIfc) action.getDataObject();
        LocaleRequestor localeRequestor = this.getLocaleRequestor(searchTransaction);
        TransactionIfc transaction =
          readTransactionForBatch(connection,
                                  searchTransaction, localeRequestor);
        // if void transaction, handle original transaction
        if (transaction instanceof VoidTransactionIfc)
        {
            setOriginalTransaction(connection,
                                   (VoidTransactionIfc) transaction, localeRequestor);
        }

        // return array
        dataTransaction.setResult(transaction);

        if (logger.isDebugEnabled()) logger.debug( "JdbcReadTransactionForBatch.execute");
    }
}
