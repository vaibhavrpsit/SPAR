/* ===========================================================================
* Copyright (c) 1998, 2011, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/modules/domain/src/oracle/retail/stores/domain/arts/JdbcReadLayawayDescription.java /rgbustores_13.4x_generic_branch/1 2011/05/04 11:49:05 mszekely Exp $
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
 *    sgu       10/30/08 - check in after refresh
 *
 * ===========================================================================
 * $Log:
 *    3    360Commerce 1.2         3/31/2005 4:28:41 PM   Robert Pearse   
 *    2    360Commerce 1.1         3/10/2005 10:22:44 AM  Robert Pearse   
 *    1    360Commerce 1.0         2/11/2005 12:11:59 PM  Robert Pearse   
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
 *   Revision 1.3  2004/02/12 17:13:17  mcs
 *   Forcing head revision
 *
 *   Revision 1.2  2004/02/11 23:25:21  bwf
 *   @scr 0 Organize imports.
 *
 *   Revision 1.1.1.1  2004/02/11 01:04:27  cschellenger
 *   updating to pvcs 360store-current
 *
 *
 * 
 *    Rev 1.0   Aug 29 2003 15:31:58   CSchellenger
 * Initial revision.
 *
 *    Rev 1.0   Jun 03 2002 16:37:40   msg
 * Initial revision.
 *
 *    Rev 1.2   15 May 2002 17:11:50   vxs
 * Removed string concatenations from logging statements
 * Resolution for POS SCR-1632: Updates for Gap
 *
 *    Rev 1.1   Mar 18 2002 22:47:26   msg
 * - updated copyright
 *
 *    Rev 1.0   Mar 18 2002 12:07:28   msg
 * Initial revision.
 *
 *    Rev 1.0   Sep 20 2001 15:58:20   msg
 * Initial revision.
 *
 *    Rev 1.1   Sep 17 2001 12:34:26   msg
 * header update
 * ===========================================================================
 */
package oracle.retail.stores.domain.arts;

// foundation imports
import org.apache.log4j.Logger;

import oracle.retail.stores.common.utility.LocalizedTextIfc;
import oracle.retail.stores.domain.financial.LayawayIfc;
import oracle.retail.stores.domain.transaction.TransactionSummaryIfc;
import oracle.retail.stores.foundation.manager.data.DataAction;
import oracle.retail.stores.foundation.manager.data.DataException;
import oracle.retail.stores.foundation.manager.data.JdbcDataConnection;
import oracle.retail.stores.foundation.manager.ifc.data.DataActionIfc;
import oracle.retail.stores.foundation.manager.ifc.data.DataConnectionIfc;
import oracle.retail.stores.foundation.manager.ifc.data.DataTransactionIfc;
import oracle.retail.stores.foundation.utility.Util;

//--------------------------------------------------------------------------
/**
    This operation reads a Description of the first item of a POS transaction
    from a database.  It contains the methods that read the transaction
    tables in the database.
    It expects a previous data action to have been performed in the calling
    data transaction that sets the results to an array of
    LayawaySummaryEntryIfcs each of which contains an initial TransactionID
    (consisting of a store id, register id, and sequence number), and an
    initial business date.
    <P>
    @version $Revision: /rgbustores_13.4x_generic_branch/1 $
**/
//--------------------------------------------------------------------------
public class JdbcReadLayawayDescription
    extends JdbcReadFirstItemDescription
{
    /**
        The logger to which log messages will be sent.
    **/
    private static Logger logger = Logger.getLogger(oracle.retail.stores.domain.arts.JdbcReadLayawayDescription.class);

    //----------------------------------------------------------------------
    /**
        Class constructor.
     **/
    //----------------------------------------------------------------------
    public JdbcReadLayawayDescription()
    {
        super();
        setName("JdbcReadLayawayDescription");
    }

    //----------------------------------------------------------------------
    /**
        Executes the SQL statements against the database.
        It expects a previous data action to have been performed in the calling
        data transaction that sets the results to an array of
        LayawaySummaryEntryIfcs each of which contains an initial TransactionID
        (consisting of a store id, register id, and sequence number), and an
        initial business date.
        <P>
        @param  dataTransaction     The data transaction
        @param  dataConnection      The connection to the data source
        @param  action              The information passed by the valet
        @exception DataException upon error
    **/
    //----------------------------------------------------------------------
    public void execute(DataTransactionIfc dataTransaction,
                        DataConnectionIfc dataConnection,
                        DataActionIfc action)
                        throws DataException
    {
        if (logger.isDebugEnabled()) logger.debug( "" + "JdbcReadLayawayDescription.execute" + "");

        JdbcDataConnection connection = (JdbcDataConnection)dataConnection;

        LayawayIfc layaway;

        try
        {
            // This data operation should follow another data operation
            // that has retrieved the layaway. Get the previously resulting
            // layaway from the data transaction.
            layaway = (LayawayIfc)dataTransaction.getResult();

            // Build a transaction summary from the contents of the layaway
            TransactionSummaryIfc transactionSummary
                = instantiateTransactionSummary(
                    layaway.getInitialTransactionID(),
                    layaway.getInitialTransactionBusinessDate(),
                    layaway.getLocaleRequestor());

            // Build an appropriate data action including the
            // layaway summary just built.
            DataAction subAction = new DataAction();
            subAction.setDataOperationName("ReadFirstItemDescription");
            subAction.setDataObject(transactionSummary);

            // Call the inherited (from JdbcReadFirstItemDescription) execute
            // method with an appropriate data action.
            super.execute(dataTransaction, dataConnection, subAction);

            // The result should be a description if it was found
            LocalizedTextIfc descriptions = (LocalizedTextIfc)dataTransaction.getResult();

            // Set the description in the layaway object
            layaway.setLocalizedDescriptions(descriptions);

            // Set the result back to the original layaway
            dataTransaction.setResult(layaway);
        }
        catch (DataException de)
        {
            throw de;
        }
        catch (Exception e)
        {
            throw new DataException(DataException.UNKNOWN,
                                    "Unknown error getting layaway descriptions",
                                    e);
        }

        if (logger.isDebugEnabled()) logger.debug(
                    "JdbcReadLayawayDescription.execute");
    }

}
