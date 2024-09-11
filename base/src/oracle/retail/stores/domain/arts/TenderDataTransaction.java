/* ===========================================================================
* Copyright (c) 1998, 2012, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/modules/domain/src/oracle/retail/stores/domain/arts/TenderDataTransaction.java /main/13 2012/03/27 10:57:13 jswan Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    jswan     03/21/12 - Modified to support centralized gift certificate and
 *                         store credit.
 *    cgreene   05/26/10 - convert to oracle packaging
 *    abondala  01/03/10 - update header date
 *    abondala  11/06/08 - updated files related to reason codes
 *    abondala  11/05/08 - updated files related to the reason codes
 *                         CheckIDTypes and MailBankCheckIDTypes
 *
 * ===========================================================================
 * $Log:
 *    3    360Commerce 1.2         3/31/2005 4:30:23 PM   Robert Pearse
 *    2    360Commerce 1.1         3/10/2005 10:25:56 AM  Robert Pearse
 *    1    360Commerce 1.0         2/11/2005 12:14:50 PM  Robert Pearse
 *
 *   Revision 1.4  2004/02/17 16:18:47  rhafernik
 *   @scr 0 log4j conversion
 *
 *   Revision 1.3  2004/02/12 17:13:19  mcs
 *   Forcing head revision
 *
 *   Revision 1.2  2004/02/11 23:25:26  bwf
 *   @scr 0 Organize imports.
 *
 *   Revision 1.1.1.1  2004/02/11 01:04:29  cschellenger
 *   updating to pvcs 360store-current
 *
 *
 *
 *    Rev 1.0   Aug 29 2003 15:34:18   CSchellenger
 * Initial revision.
 *
 *    Rev 1.0   Jun 03 2002 16:42:56   msg
 * Initial revision.
 *
 *    Rev 1.1   Mar 18 2002 22:51:42   msg
 * - updated copyright
 *
 *    Rev 1.0   Mar 18 2002 12:10:58   msg
 * Initial revision.
 *
 *    Rev 1.2   10 Jan 2002 09:31:20   vxs
 * Added readStoreCredit(String storeCreditID) back because needed by JdbcReadTransaction even though not needed by JdbcReadStoreCredit
 * Resolution for POS SCR-596: Store Credit package training mode updates
 *
 *    Rev 1.1   09 Jan 2002 17:23:16   vxs
 * Store Credit training mode functionality in place.
 * Resolution for POS SCR-596: Store Credit package training mode updates
 *
 *    Rev 1.0   Sep 20 2001 15:56:08   msg
 * Initial revision.
 *
 *    Rev 1.1   Sep 17 2001 12:33:14   msg
 * header update
 * ===========================================================================
 */
package oracle.retail.stores.domain.arts;

// java imports
import oracle.retail.stores.common.utility.LocalizedCodeIfc;
import oracle.retail.stores.domain.utility.StoreCreditIfc;
import oracle.retail.stores.foundation.manager.data.DataAction;
import oracle.retail.stores.foundation.manager.data.DataException;
import oracle.retail.stores.foundation.manager.data.DataTransaction;
import oracle.retail.stores.foundation.manager.ifc.data.DataActionIfc;

//--------------------------------------------------------------------------
/**
    The PLUTransaction implements the price lookup data store operation.
    @deprecated in 14.0; there are no references to this class in ORPOS
 **/
//--------------------------------------------------------------------------
public class TenderDataTransaction extends DataTransaction
{
    /**
        revision number of this class
    **/
    public static final String revisionNumber = "$Revision: /main/13 $";

    /**
        The name that links this transaction to a command within the
        DataScript.
    **/
    protected static String dataCommandName="TenderDataTransaction";

    //---------------------------------------------------------------------
    /**
        DataCommand constructor.  Initializes dataOperations and
        dataConnectionPool.
    **/
    //---------------------------------------------------------------------
    public TenderDataTransaction()
    {
        super(dataCommandName);
    }

    //---------------------------------------------------------------------
    /**
        Returns the store credit
        <p>
        @return the store credit.
        @exception DataException is thrown if the store credit cannot be found.
        @deprecated as of 13.1 Use {@link #readStoreCredit(StoreCreditIfc)}
    **/
    //---------------------------------------------------------------------
    public StoreCreditIfc readStoreCredit(String storeCreditID) throws DataException
    {
        DataAction dataAction = new DataAction();
        dataAction.setDataObject(storeCreditID);
        dataAction.setDataOperationName("ReadStoreCredit");

        DataActionIfc[] dataActions = new DataActionIfc[1];
        dataActions[0] = dataAction;

        setDataActions(dataActions);
        StoreCreditIfc sc = (StoreCreditIfc) getDataManager().execute(this);

        if (sc == null)
        {
            throw new DataException(DataException.NO_DATA);
        }

        // For now, only return the first match
        return(sc);
    }

    //---------------------------------------------------------------------
    /**
        Returns the store credit
        <p>
        @return the store credit.
        @exception DataException is thrown if the store credit cannot be found.
    **/
    //---------------------------------------------------------------------
    public StoreCreditIfc readStoreCredit(StoreCreditIfc inputsc) throws DataException
    {
        DataAction dataAction = new DataAction();
        dataAction.setDataObject(inputsc);
        dataAction.setDataOperationName("ReadStoreCredit");

        DataActionIfc[] dataActions = new DataActionIfc[1];
        dataActions[0] = dataAction;

        setDataActions(dataActions);
        StoreCreditIfc sc = (StoreCreditIfc) getDataManager().execute(this);

        if (sc == null)
        {
            throw new DataException(DataException.NO_DATA);
        }

        // For now, only return the first match
        return(sc);
    }

    //---------------------------------------------------------------------
    /**
        saves the store credit
        <p>
        @exception DataException is thrown if the store credit cannot be saved.
    **/
    //---------------------------------------------------------------------
    public void insertStoreCredit(StoreCreditIfc storeCredit) throws DataException
    {
        DataAction dataAction = new DataAction();
        dataAction.setDataObject(storeCredit);
        dataAction.setDataOperationName("InsertStoreCredit");

        DataActionIfc[] dataActions = new DataActionIfc[1];
        dataActions[0] = dataAction;

        setDataActions(dataActions);
        getDataManager().execute(this);
    }

    //---------------------------------------------------------------------
    /**
        updates the store credit
        <p>
        @exception DataException is thrown if the store credit cannot be updated.
    **/
    //---------------------------------------------------------------------
    public void updateStoreCredit(StoreCreditIfc storeCredit) throws DataException
    {
        DataAction dataAction = new DataAction();
        dataAction.setDataObject(storeCredit);
        dataAction.setDataOperationName("UpdateStoreCredit");

        DataActionIfc[] dataActions = new DataActionIfc[1];
        dataActions[0] = dataAction;

        setDataActions(dataActions);
        getDataManager().execute(this);
    }

    //---------------------------------------------------------------------
    /**
        Returns the revision number of this class.
        <P>
        @return String representation of revision number
    **/
    //---------------------------------------------------------------------
    public String getRevisionNumber()
    {
        return(revisionNumber);
    }

    //---------------------------------------------------------------------
    /**
       Returns the string representation of this object.
       <P>
       @return String representation of object
    **/
    //---------------------------------------------------------------------
    public String toString()
    {
        String strResult = new String("Class: TenderDataTransaction (Revision "
                                        + getRevisionNumber() + ") @"
                                        + hashCode());
        return(strResult);
    }
}
