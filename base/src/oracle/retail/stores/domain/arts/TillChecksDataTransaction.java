/* ===========================================================================
* Copyright (c) 1998, 2013, Oracle and/or its affiliates. All rights reserved.
 * ===========================================================================
 * $Header: rgbustores/modules/domain/src/oracle/retail/stores/domain/arts/TillChecksDataTransaction.java /main/14 2013/09/05 10:36:14 abondala Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    abondala  09/04/13 - initialize collections
 *    cgreene   05/27/10 - convert to oracle packaging
 *    cgreene   05/27/10 - convert to oracle packaging
 *    cgreene   05/26/10 - convert to oracle packaging
 *    abondala  01/03/10 - update header date
 *
 * ===========================================================================
 * $Log:
 *    3    360Commerce 1.2         3/31/2005 4:30:29 PM   Robert Pearse   
 *    2    360Commerce 1.1         3/10/2005 10:26:10 AM  Robert Pearse   
 *    1    360Commerce 1.0         2/11/2005 12:15:03 PM  Robert Pearse   
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
 *    Rev 1.4   Jan 20 2004 16:58:00   kll
 * Exclusion of ECheck tenders from till counts via a persistent boolean value 
 * Resolution for 3604: Pick-up Till Report does not distinguish between Checks and e-Checks and Total is incorrect
 * 
 *    Rev 1.3   Jan 13 2004 10:25:02   blj
 * modified so that till pickup time lookups use tenderName hardcoded to check b/c money orders are picked up as checks.
 * Resolution for 3481: Money Orders do not appear in Pick-up Till Report
 * 
 *    Rev 1.2   Dec 10 2003 14:54:56   blj
 * updated with code review suggestions.
 * 
 *    Rev 1.1   Nov 04 2003 17:12:22   blj
 * updated for money order
 * 
 *    Rev 1.0   Aug 29 2003 15:34:18   CSchellenger
 * Initial revision.
 * 
 *    Rev 1.1   Jun 23 2003 13:33:24   DCobb
 * Canadian Check Till Pickup
 * Resolution for POS SCR-2484: Canadian Check Till Pickup
 * 
 *    Rev 1.0   Jun 03 2002 16:43:00   msg
 * Initial revision.
 * 
 *    Rev 1.1   Mar 18 2002 22:51:48   msg
 * - updated copyright
 * 
 *    Rev 1.0   Mar 18 2002 12:11:02   msg
 * Initial revision.
 * 
 *    Rev 1.1   11 Mar 2002 16:34:12   epd
 * fixed check pickups where business date differs from system date
 * Resolution for POS SCR-1545: Till Pickup for Check only works on 1st till of the day
 * 
 *    Rev 1.0   Sep 20 2001 15:56:12   msg
 * Initial revision.
 * 
 *    Rev 1.1   Sep 17 2001 12:33:14   msg
 * header update
 * ===========================================================================
 */
package oracle.retail.stores.domain.arts;

import java.util.HashMap;

import oracle.retail.stores.domain.tender.TenderTypeMap;
import oracle.retail.stores.domain.tender.TenderCheckIfc;
import oracle.retail.stores.domain.tender.TenderLineItemIfc;
import oracle.retail.stores.domain.tender.TenderMoneyOrderIfc;
import oracle.retail.stores.domain.utility.EYSDate;
import oracle.retail.stores.foundation.manager.data.DataAction;
import oracle.retail.stores.foundation.manager.data.DataException;
import oracle.retail.stores.foundation.manager.data.DataTransaction;
import oracle.retail.stores.foundation.manager.ifc.data.DataActionIfc;

/**
 * We are looking for all checks written in the current Till.
 */
public class TillChecksDataTransaction extends DataTransaction
{
    static final long serialVersionUID = 2046881535148688773L;
    /**
     * revision number of this class
     */
    public static final String revisionNumber = "$Revision: /main/14 $";

    /**
     * The name that links this transaction to a command within the DataScript.
     */
    protected static String dataCommandName = "TillChecksDataTransaction";

    /**
     * DataCommand constructor. Initializes dataOperations and
     * dataConnectionPool.
     */
    public TillChecksDataTransaction()
    {
        super(dataCommandName);
    }

    /**
     * Returns the list of pickup checks from the Till
     * 
     * @return the store credit.
     * @exception DataException is thrown if the store credit cannot be found.
     */
    public TenderCheckIfc[] getChecksForTill(String tillId, String storeId, EYSDate businessDate, String tenderName,
            String tenderNationality) throws DataException
    {
        String dataOpName = "SelectChecksFromTill";
        TillChecksDataTransaction dataAction = getCommonDataForTill(tillId, storeId, businessDate, tenderName,
                tenderNationality, dataOpName);
        TenderCheckIfc[] checks = (TenderCheckIfc[]) getDataManager().execute(dataAction);
        if (checks == null)
        {
            throw new DataException(DataException.NO_DATA);
        }
        return checks;
    }

    /**
     * Returns the list of pickup checks from the Till. INCLUDES check filtering
     * 
     * @return the store credit.
     * @exception DataException is thrown if the store credit cannot be found.
     */
    public TenderCheckIfc[] getChecksForTill(String tillId, String storeId, EYSDate businessDate, String tenderName,
            String tenderNationality, boolean checkFilter) throws DataException
    {
        String dataOpName = "SelectChecksFromTill";
        TillChecksDataTransaction dataAction = getCommonDataForTill(tillId, storeId, businessDate, tenderName,
                tenderNationality, checkFilter, dataOpName);
        TenderCheckIfc[] checks = (TenderCheckIfc[]) getDataManager().execute(dataAction);
        if (checks == null)
        {
            throw new DataException(DataException.NO_DATA);
        }
        return checks;
    }

    /**
     * This method is common to both the money order and check tender types and
     * will setup the dataoperation.
     * 
     * @param tillId
     * @param storeId
     * @param businessDate
     * @param tenderName
     * @param tenderNationality
     * @param dataOperationName
     * @return TillChecksDataTransaction
     * @throws DataException
     */
    protected TillChecksDataTransaction getCommonDataForTill(String tillId, String storeId, EYSDate businessDate,
            String tenderName, String tenderNationality, String dataOperationName) throws DataException
    {
        // first try and find time of latest till pickup
        DataAction dataAction = new DataAction();
        dataAction.setDataOperationName("FindLatestTillPickupTime");

        // We have to hardcode the tenderName for the FindLatestTillPickupTime
        // database
        // lookup to be a check because the "pickup table" doesnt know about
        // money order.
        // Per the requirements for POS 7.0, money orders are picked up as
        // checks.
        String tenderNameforPickup = TenderTypeMap.getTenderTypeMap()
                .getDescriptor(TenderLineItemIfc.TENDER_TYPE_CHECK);
        HashMap<String,Object> params = new HashMap<String,Object>(7);
        params.put("tillId", tillId);
        params.put("storeId", storeId);
        params.put("businessDate", businessDate);
        params.put("tenderName", tenderNameforPickup);
        params.put("tenderNationality", tenderNationality);

        dataAction.setDataObject(params);

        DataActionIfc[] dataActions = new DataActionIfc[1];
        dataActions[0] = dataAction;

        setDataActions(dataActions);
        EYSDate pickupTime = (EYSDate) getDataManager().execute(this);

        if (pickupTime != null)
        {
            params.put("pickupTime", pickupTime);
        }

        // Now we update the param hashMap to be MoneyOrder
        params.put("tenderName", tenderName);

        dataAction = new DataAction();
        dataAction.setDataOperationName(dataOperationName);

        dataAction.setDataObject(params);

        dataActions = new DataActionIfc[1];
        dataActions[0] = dataAction;

        setDataActions(dataActions);

        // For now, only return the first match
        return (this);
    }

    /**
     * This method is common to both the money order and check tender types and
     * will setup the dataoperation. INCLUDES check filtering
     * 
     * @param tillId
     * @param storeId
     * @param businessDate
     * @param tenderName
     * @param tenderNationality
     * @param dataOperationName
     * @return TillChecksDataTransaction
     * @throws DataException
     */
    protected TillChecksDataTransaction getCommonDataForTill(String tillId, String storeId, EYSDate businessDate,
            String tenderName, String tenderNationality, boolean checkFilter, String dataOperationName)
            throws DataException
    {
        // first try and find time of latest till pickup
        DataAction dataAction = new DataAction();
        dataAction.setDataOperationName("FindLatestTillPickupTime");

        // We have to hardcode the tenderName for the FindLatestTillPickupTime
        // database
        // lookup to be a check because the "pickup table" doesnt know about
        // money order.
        // Per the requirements for POS 7.0, money orders are picked up as
        // checks.
        String tenderNameforPickup = TenderTypeMap.getTenderTypeMap()
                .getDescriptor(TenderLineItemIfc.TENDER_TYPE_CHECK);
        HashMap<String,Object> params = new HashMap<String,Object>(8);
        params.put("tillId", tillId);
        params.put("storeId", storeId);
        params.put("businessDate", businessDate);
        params.put("tenderName", tenderNameforPickup);
        params.put("tenderNationality", tenderNationality);
        if (!checkFilter)
        {
            params.put("checkFilter", Boolean.FALSE);
        }
        else
        {
            params.put("checkFilter", Boolean.TRUE);
        }

        dataAction.setDataObject(params);

        DataActionIfc[] dataActions = new DataActionIfc[1];
        dataActions[0] = dataAction;

        setDataActions(dataActions);
        EYSDate pickupTime = (EYSDate) getDataManager().execute(this);

        if (pickupTime != null)
        {
            params.put("pickupTime", pickupTime);
        }

        // Now we update the param hashMap to be MoneyOrder
        params.put("tenderName", tenderName);

        dataAction = new DataAction();
        dataAction.setDataOperationName(dataOperationName);

        dataAction.setDataObject(params);

        dataActions = new DataActionIfc[1];
        dataActions[0] = dataAction;

        setDataActions(dataActions);

        // For now, only return the first match
        return this;
    }

    /**
     * Returns the list of money orders from the Till
     * 
     * @return the money order.
     * @exception DataException is thrown if the money order cannot be found.
     */
    public TenderMoneyOrderIfc[] getMoneyOrdersForTill(String tillId, String storeId, EYSDate businessDate,
            String tenderName, String tenderNationality) throws DataException
    {
        String dataOpName = "SelectMoneyOrdersFromTill";
        TillChecksDataTransaction dataAction = getCommonDataForTill(tillId, storeId, businessDate, tenderName,
                tenderNationality, dataOpName);
        TenderMoneyOrderIfc[] moneyOrders = (TenderMoneyOrderIfc[]) getDataManager().execute(this);
        if (moneyOrders == null)
        {
            throw new DataException(DataException.NO_DATA);
        }
        return moneyOrders;
    }

    /**
     * Returns the revision number of this class.
     * 
     * @return String representation of revision number
     */
    public String getRevisionNumber()
    {
        return (revisionNumber);
    }

    /**
     * Returns the string representation of this object.
     * 
     * @return String representation of object
     */
    public String toString()
    {
        String strResult = new String("Class: TillChecksDataTransaction (Revision " + getRevisionNumber() + ") @"
                + hashCode());
        return (strResult);
    }
}
