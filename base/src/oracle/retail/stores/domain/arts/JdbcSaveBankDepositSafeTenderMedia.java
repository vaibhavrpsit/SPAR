/* ===========================================================================
* Copyright (c) 1998, 2011, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/modules/domain/src/oracle/retail/stores/domain/arts/JdbcSaveBankDepositSafeTenderMedia.java /rgbustores_13.4x_generic_branch/1 2011/05/04 11:49:06 mszekely Exp $
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
 *    3    360Commerce 1.2         3/31/2005 4:28:43 PM   Robert Pearse   
 *    2    360Commerce 1.1         3/10/2005 10:22:47 AM  Robert Pearse   
 *    1    360Commerce 1.0         2/11/2005 12:12:01 PM  Robert Pearse   
 *
 *   Revision 1.6  2004/04/09 16:55:47  cdb
 *   @scr 4302 Removed double semicolon warnings.
 *
 *   Revision 1.5  2004/02/17 17:57:38  bwf
 *   @scr 0 Organize imports.
 *
 *   Revision 1.4  2004/02/17 16:18:48  rhafernik
 *   @scr 0 log4j conversion
 *
 *   Revision 1.3  2004/02/12 17:13:18  mcs
 *   Forcing head revision
 *
 *   Revision 1.2  2004/02/11 23:25:26  bwf
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
 *    Rev 1.0   May 23 2002 14:11:08   mpm
 * Initial revision.
 * Resolution for Domain SCR-45: TLog facility
 * ===========================================================================
 */
package oracle.retail.stores.domain.arts;
// foundation imports
import org.apache.log4j.Logger;

import oracle.retail.stores.domain.financial.FinancialCountTenderItemIfc;
import oracle.retail.stores.domain.transaction.BankDepositTransactionIfc;
import oracle.retail.stores.foundation.manager.data.DataException;
import oracle.retail.stores.foundation.manager.data.JdbcDataConnection;
import oracle.retail.stores.foundation.manager.ifc.data.DataActionIfc;
import oracle.retail.stores.foundation.manager.ifc.data.DataConnectionIfc;
import oracle.retail.stores.foundation.manager.ifc.data.DataTransactionIfc;

//-------------------------------------------------------------------------
/**
    This class is the data operation for saving tender media line items
    to the database.
    <P>
    @version $Revision: /rgbustores_13.4x_generic_branch/1 $
**/
//-------------------------------------------------------------------------
public class JdbcSaveBankDepositSafeTenderMedia
extends JdbcSaveTenderMediaLineItems
{
    /**
        The logger to which log messages will be sent.
    **/
    private static Logger logger = Logger.getLogger(oracle.retail.stores.domain.arts.JdbcSaveBankDepositSafeTenderMedia.class);

    //---------------------------------------------------------------------
    /**
        Class constructor.
     **/
    //---------------------------------------------------------------------
    public JdbcSaveBankDepositSafeTenderMedia()
    {
        super();
        setName("JdbcSaveBankDepositSafeTenderMedia");
    }

    //---------------------------------------------------------------------
    /**
        Execute the SQL statements against the database.
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
        if (logger.isDebugEnabled()) logger.debug(
                    "JdbcSaveBankDepositSafeTenderMedia.execute()");

        JdbcDataConnection connection = (JdbcDataConnection) dataConnection;

        BankDepositTransactionIfc transaction =
          (BankDepositTransactionIfc) action.getDataObject();
        // get count to be recorded to database
        if (transaction.getDepositCount() != null)
        {
            try
            {
                FinancialCountTenderItemIfc[] fcti =
                  transaction.getDepositCount().getTenderItems();
                insertTenderMediaLineItems(connection,
                                           transaction,
                                           fcti);
            }
            catch(DataException e)
            {
                // exception already recorded
                throw e;
            }
        }
        else
        {
            if (logger.isDebugEnabled()) logger.debug(
                         "No tender items to be recorded for bank deposit transaction.");
        }

        if (logger.isDebugEnabled()) logger.debug(
                    "JdbcSaveBankDepositSafeTenderMedia.execute()");

    }


}
