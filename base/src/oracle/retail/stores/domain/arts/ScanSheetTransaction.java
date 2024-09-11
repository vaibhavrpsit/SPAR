/* ===========================================================================
* Copyright (c) 2008, 2011, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/modules/domain/src/oracle/retail/stores/domain/arts/ScanSheetTransaction.java /main/1 2011/03/10 11:12:30 jkoppolu Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED (MM/DD/YY)
 * 
 *    jkoppo 03/02/11 - New transaction, for Jdbc look up of scan sheet items.
 *
 * ===========================================================================
 */
package oracle.retail.stores.domain.arts;

import oracle.retail.stores.domain.stock.ScanSheet;
import oracle.retail.stores.foundation.manager.data.DataAction;
import oracle.retail.stores.foundation.manager.data.DataException;
import oracle.retail.stores.foundation.manager.data.DataTransaction;
import oracle.retail.stores.foundation.manager.ifc.data.DataActionIfc;
import oracle.retail.stores.foundation.manager.ifc.data.DataTransactionIfc;

public class ScanSheetTransaction extends DataTransaction implements DataTransactionIfc
{
    private static final long serialVersionUID = 5486930153274380987L;
    protected static final String dataCommandName = "ScanSheetTransaction";

    public ScanSheetTransaction()
    {
        super(dataCommandName);
    }

    public ScanSheet getScanSheet(String locale) throws DataException
    {
        DataAction dataAction = new DataAction();
        dataAction.setDataOperationName("ReadScanSheet");
        DataActionIfc[] dataActions = new DataActionIfc[1];
        dataActions[0] = dataAction;
        dataAction.setDataObject(locale);
        setDataActions(dataActions);
        return (ScanSheet) getDataManager().execute(this);
    }
}