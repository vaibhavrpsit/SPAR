/* ===========================================================================
* Copyright (c) 1998, 2011, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/modules/domain/src/oracle/retail/stores/domain/arts/JdbcUpdateSafeFromStoreOpenCloseTransaction.java /rgbustores_13.4x_generic_branch/1 2011/05/04 11:49:04 mszekely Exp $
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
 *    3    360Commerce 1.2         3/31/2005 4:28:46 PM   Robert Pearse   
 *    2    360Commerce 1.1         3/10/2005 10:22:53 AM  Robert Pearse   
 *    1    360Commerce 1.0         2/11/2005 12:12:06 PM  Robert Pearse   
 *
 *   Revision 1.6  2004/04/09 16:55:44  cdb
 *   @scr 4302 Removed double semicolon warnings.
 *
 *   Revision 1.5  2004/02/17 17:57:36  bwf
 *   @scr 0 Organize imports.
 *
 *   Revision 1.4  2004/02/17 16:18:45  rhafernik
 *   @scr 0 log4j conversion
 *
 *   Revision 1.3  2004/02/12 17:13:19  mcs
 *   Forcing head revision
 *
 *   Revision 1.2  2004/02/11 23:25:22  bwf
 *   @scr 0 Organize imports.
 *
 *   Revision 1.1.1.1  2004/02/11 01:04:28  cschellenger
 *   updating to pvcs 360store-current
 *
 *
 * 
 *    Rev 1.0   Aug 29 2003 15:33:36   CSchellenger
 * Initial revision.
 * 
 *    Rev 1.0   Jun 03 2002 16:41:28   msg
 * Initial revision.
 * 
 *    Rev 1.1   May 09 2002 20:36:16   mpm
 * Tweaked.
 * Resolution for Domain SCR-45: TLog facility
 *
 *    Rev 1.0   May 08 2002 20:48:24   mpm
 * Initial revision.
 * Resolution for Domain SCR-45: TLog facility
 * ===========================================================================
 */
package oracle.retail.stores.domain.arts;
// java imports
import org.apache.log4j.Logger;

import oracle.retail.stores.domain.DomainGateway;
import oracle.retail.stores.domain.financial.StoreSafeIfc;
import oracle.retail.stores.domain.financial.StoreStatusIfc;
import oracle.retail.stores.domain.transaction.StoreOpenCloseTransactionIfc;
import oracle.retail.stores.foundation.manager.data.DataException;
import oracle.retail.stores.foundation.manager.data.JdbcDataConnection;
import oracle.retail.stores.foundation.manager.ifc.data.DataActionIfc;
import oracle.retail.stores.foundation.manager.ifc.data.DataConnectionIfc;
import oracle.retail.stores.foundation.manager.ifc.data.DataTransactionIfc;

//-------------------------------------------------------------------------
/**
    This operation updates the store safe using data in the
    store open-close transaction. <P>
    @version $Revision: /rgbustores_13.4x_generic_branch/1 $
**/
//-------------------------------------------------------------------------
public class JdbcUpdateSafeFromStoreOpenCloseTransaction
extends JdbcUpdateStoreSafeTotals
{
    /**
        The logger to which log messages will be sent.
    **/
    private static Logger logger = Logger.getLogger(oracle.retail.stores.domain.arts.JdbcUpdateSafeFromStoreOpenCloseTransaction.class);

    //---------------------------------------------------------------------
    /**
       Class constructor. <P>
    **/
    //---------------------------------------------------------------------
    public JdbcUpdateSafeFromStoreOpenCloseTransaction()
    {
        super();
        setName("JdbcUpdateSafeFromStoreOpenCloseTransaction");
    }

    //---------------------------------------------------------------------
    /**
       Executes the SQL statements against the database.
       <P>
       @param  dataTransaction
       @param  dataConnection
       @param  action
       @exception DataException upon error
    **/
    //---------------------------------------------------------------------
    public void execute(DataTransactionIfc dataTransaction,
                        DataConnectionIfc dataConnection,
                        DataActionIfc action)
    throws DataException
    {
        if (logger.isDebugEnabled()) logger.debug(
                     "JdbcUpdateSafeFromStoreOpenCloseTransaction.execute()");

        // get connection, transaction
        JdbcDataConnection connection = (JdbcDataConnection) dataConnection;
        StoreOpenCloseTransactionIfc socTransaction =
          (StoreOpenCloseTransactionIfc) action.getDataObject();

        // set up safe object for update
        StoreStatusIfc storeStatus = socTransaction.getStoreStatus();
        StoreSafeIfc safe = DomainGateway.getFactory().getStoreSafeInstance();
        safe.setBusinessDay(storeStatus.getBusinessDate());
        safe.setStoreID(storeStatus.getStore().getStoreID());
        //safe.setValidTenderList(storeStatus.getSafeTenderTypeList());
        safe.setValidTenderDescList(storeStatus.getSafeTenderTypeDescList());
        if (socTransaction.getStartingSafeCount() != null)
        {
            safe.setOpenOperatingFunds(socTransaction.getStartingSafeCount());
            safe.setOperationType(StoreSafeIfc.OPEN);
        }
        if (socTransaction.getEndingSafeCount() != null)
        {
            safe.setCloseOperatingFunds(socTransaction.getEndingSafeCount());
            safe.setOperationType(StoreSafeIfc.CLOSE);
        }

        updateStoreSafeTotals(connection,
                              safe);

        if (logger.isDebugEnabled()) logger.debug(
                    "JdbcUpdateSafeFromStoreOpenCloseTransaction.execute()");
    }

}
