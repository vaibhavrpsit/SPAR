/* ===========================================================================
* Copyright (c) 1998, 2011, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/modules/domain/src/oracle/retail/stores/domain/arts/JdbcSaveCanceledTransaction.java /rgbustores_13.4x_generic_branch/1 2011/05/04 11:48:58 mszekely Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    cgreene   05/27/10 - convert to oracle packaging
 *    cgreene   05/27/10 - convert to oracle packaging
 *    cgreene   05/26/10 - convert to oracle packaging
 *    abondala  01/03/10 - update header date
 *
 * ===========================================================================
 * $Log:
 *    3    360Commerce 1.2         3/31/2005 4:28:43 PM   Robert Pearse   
 *    2    360Commerce 1.1         3/10/2005 10:22:47 AM  Robert Pearse   
 *    1    360Commerce 1.0         2/11/2005 12:12:01 PM  Robert Pearse   
 *
 *   Revision 1.6  2004/04/09 16:55:44  cdb
 *   @scr 4302 Removed double semicolon warnings.
 *
 *   Revision 1.5  2004/02/17 17:57:35  bwf
 *   @scr 0 Organize imports.
 *
 *   Revision 1.4  2004/02/17 16:18:44  rhafernik
 *   @scr 0 log4j conversion
 *
 *   Revision 1.3  2004/02/12 17:13:18  mcs
 *   Forcing head revision
 *
 *   Revision 1.2  2004/02/11 23:25:21  bwf
 *   @scr 0 Organize imports.
 *
 *   Revision 1.1.1.1  2004/02/11 01:04:28  cschellenger
 *   updating to pvcs 360store-current
 *
 *
 * 
 *    Rev 1.0   Aug 29 2003 15:32:38   CSchellenger
 * Initial revision.
 * 
 *    Rev 1.0   Jun 03 2002 16:39:24   msg
 * Initial revision.
 * 
 *    Rev 1.1   Mar 18 2002 22:46:20   msg
 * - updated copyright
 * 
 *    Rev 1.0   Mar 18 2002 12:06:28   msg
 * Initial revision.
 * 
 *    Rev 1.0   Sep 20 2001 15:57:18   msg
 * Initial revision.
 * 
 *    Rev 1.1   Sep 17 2001 12:34:08   msg
 * header update
 * ===========================================================================
 */
package oracle.retail.stores.domain.arts;

import org.apache.log4j.Logger;

import oracle.retail.stores.domain.transaction.TransactionIfc;
import oracle.retail.stores.foundation.manager.data.DataException;
import oracle.retail.stores.foundation.manager.data.JdbcDataConnection;
import oracle.retail.stores.foundation.manager.ifc.data.DataActionIfc;
import oracle.retail.stores.foundation.manager.ifc.data.DataConnectionIfc;
import oracle.retail.stores.foundation.manager.ifc.data.DataTransactionIfc;

//-------------------------------------------------------------------------
/**
    This operation performs inserts into the transaction table.
    <P>
    @version $Revision: /rgbustores_13.4x_generic_branch/1 $
**/
//-------------------------------------------------------------------------
public class JdbcSaveCanceledTransaction extends JdbcSaveTransaction
{
    /** 
        The logger to which log messages will be sent.
    **/
    private static Logger logger = Logger.getLogger(oracle.retail.stores.domain.arts.JdbcSaveCanceledTransaction.class);

    //---------------------------------------------------------------------
    /**
       Class constructor. <P>
    **/
    //---------------------------------------------------------------------
    public JdbcSaveCanceledTransaction()
    {
        super();
        setName("JdbcSaveCanceledTransaction");
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
        if (logger.isDebugEnabled()) logger.debug( "JdbcSaveCanceledTransaction.execute()");

        /*
         * getUpdateCount() is about the only thing outside of
         * DataConnectionIfc that we need.
         */
        JdbcDataConnection connection = (JdbcDataConnection)dataConnection;

        // Navigate the input object to obtain values that will be inserted
        // into the database.
        ARTSTransaction trans = (ARTSTransaction)action.getDataObject();
        insertCanceledTransaction(connection,
                                  trans.getPosTransaction());

        if (logger.isDebugEnabled()) logger.debug( "JdbcSaveCanceledTransaction.execute()");
    }

    //---------------------------------------------------------------------
    /**
       Adds a canceled transaction.
       <P>
       @param  dataConnection  connection to the db
       @param  transaction     a canceled transaction
       @return true if successful
       @exception DataException upon error
    **/
    //---------------------------------------------------------------------
    public void insertCanceledTransaction(JdbcDataConnection dataConnection,
                                          TransactionIfc transaction)
        throws DataException
    {
        /*
         * Only an entry in the transaction table
         */
        insertTransaction(dataConnection, transaction);
    }
}
