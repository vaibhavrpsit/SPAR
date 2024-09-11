/* ===========================================================================
* Copyright (c) 1998, 2011, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/modules/domain/src/oracle/retail/stores/domain/arts/JdbcInsertOrderByTransaction.java /rgbustores_13.4x_generic_branch/1 2011/05/04 11:48:57 mszekely Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    cgreene   05/28/10 - convert to oracle packaging
 *    cgreene   05/27/10 - convert to oracle packaging
 *    cgreene   05/27/10 - convert to oracle packaging
 *    cgreene   05/26/10 - convert to oracle packaging
 *    abondala  01/03/10 - update header date
 *
 * ===========================================================================
 * $Log:
 *    3    360Commerce 1.2         3/31/2005 4:28:37 PM   Robert Pearse   
 *    2    360Commerce 1.1         3/10/2005 10:22:38 AM  Robert Pearse   
 *    1    360Commerce 1.0         2/11/2005 12:11:55 PM  Robert Pearse   
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
 *   Revision 1.1.1.1  2004/02/11 01:04:27  cschellenger
 *   updating to pvcs 360store-current
 *
 *
 * 
 *    Rev 1.0   Aug 29 2003 15:30:48   CSchellenger
 * Initial revision.
 * 
 *    Rev 1.0   Jun 03 2002 16:36:28   msg
 * Initial revision.
 * 
 *    Rev 1.1   Mar 18 2002 22:46:48   msg
 * - updated copyright
 * 
 *    Rev 1.0   Mar 18 2002 12:06:54   msg
 * Initial revision.
 * 
 *    Rev 1.0   27 Nov 2001 06:25:40   mpm
 * Initial revision.
 * Resolution for POS SCR-260: Special Order feature for release 5.0
 * ===========================================================================
 */
package oracle.retail.stores.domain.arts;
// bedrock imports
import org.apache.log4j.Logger;

import oracle.retail.stores.domain.transaction.OrderTransactionIfc;
import oracle.retail.stores.foundation.manager.data.DataException;
import oracle.retail.stores.foundation.manager.data.JdbcDataConnection;
import oracle.retail.stores.foundation.manager.ifc.data.DataActionIfc;
import oracle.retail.stores.foundation.manager.ifc.data.DataConnectionIfc;
import oracle.retail.stores.foundation.manager.ifc.data.DataTransactionIfc;

//--------------------------------------------------------------------------
/**
    This operation creates an order in the order/transaction database. <P>
    Note that this data operation includes columns used only in web-channel
    orders as well as those required for special order. <P>
    @version $Revision: /rgbustores_13.4x_generic_branch/1 $
**/
//--------------------------------------------------------------------------
public class JdbcInsertOrderByTransaction
extends JdbcSaveOrderByTransaction
{
    /**
        The logger to which log messages will be sent.
    **/
    private static Logger logger = Logger.getLogger(oracle.retail.stores.domain.arts.JdbcInsertOrderByTransaction.class);

    //----------------------------------------------------------------------
    /**
       Class constructor.
    **/
    //----------------------------------------------------------------------
    public JdbcInsertOrderByTransaction()
    {
        super();
        setName("JdbcInsertOrderByTransaction");
    }

    //----------------------------------------------------------------------
    /**
       Executes the SQL statements against the database.
       <P>
       @param  dataTransaction
       @param  dataConnection
       @param  action
    **/
    //----------------------------------------------------------------------
    public void execute(DataTransactionIfc dataTransaction,
                        DataConnectionIfc dataConnection,
                        DataActionIfc action)
        throws DataException
    {
        if (logger.isDebugEnabled()) logger.debug(
                     "JdbcInsertOrderByTransaction.execute");

        // set data connection
        JdbcDataConnection connection = (JdbcDataConnection) dataConnection;

        // retrieve input from data object
        OrderTransactionIfc orderTransaction =
          (OrderTransactionIfc) action.getDataObject();
        insertOrder(connection, orderTransaction);

        if (logger.isDebugEnabled()) logger.debug(
                    "JdbcInsertOrderByTransaction.execute");
    }



}
