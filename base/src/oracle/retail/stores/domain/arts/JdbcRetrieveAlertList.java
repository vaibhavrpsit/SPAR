/* ===========================================================================
* Copyright (c) 1998, 2013, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/modules/domain/src/oracle/retail/stores/domain/arts/JdbcRetrieveAlertList.java /main/23 2013/01/22 21:01:36 sgu Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    sgu       01/22/13 - calling getOrderHistory api for order summary report
 *    yiqzhao   01/20/13 - Replace EYSDate by java.util.Date in order to use in
 *                         CO.
 *    sgu       01/09/13 - retrieve alerts using order manager api
 *    yiqzhao   12/24/12 - Refactoring xc formatter, transformer and others.
 *    cgreene   10/22/10 - update to use java.lang.Comparable
 *    cgreene   05/28/10 - convert to oracle packaging
 *    cgreene   05/27/10 - convert to oracle packaging
 *    cgreene   05/27/10 - convert to oracle packaging
 *    cgreene   05/26/10 - convert to oracle packaging
 *    cgreene   04/28/10 - updating deprecated names
 *    abondala  01/03/10 - update header date
 *    jswan     04/10/09 - Display only new orders on the Service alert screen
 *    mahising  03/13/09 - Fixed service alert issue for PDO transaction if
 *                         transaction is partially completed
 *    mahising  03/05/09 - fixed issue to see partial pickup/delivery and
 *                         special order in service alert
 *
 * ===========================================================================
 * $Log:
 *    5    360Commerce 1.4         12/19/2007 6:10:07 AM  VIVEKANAND KINI
 *         Commented a Try-Catch block showing Email in the Service Alert
 *         Screen.
 *    4    360Commerce 1.3         1/25/2006 4:11:20 PM   Brett J. Larsen merge
 *          7.1.1 changes (aka. 7.0.3 fixes) into 360Commerce view
 *    3    360Commerce 1.2         3/31/2005 4:28:42 PM   Robert Pearse
 *    2    360Commerce 1.1         3/10/2005 10:22:47 AM  Robert Pearse
 *    1    360Commerce 1.0         2/11/2005 12:12:01 PM  Robert Pearse
 *:
 *    4    .v700     1.2.1.0     11/16/2005 16:27:12    Jason L. DeLeau 4215:
 *         Get rid of redundant ArtsDatabaseifc class
 *    3    360Commerce1.2         3/31/2005 15:28:42     Robert Pearse
 *    2    360Commerce1.1         3/10/2005 10:22:47     Robert Pearse
 *    1    360Commerce1.0         2/11/2005 12:12:01     Robert Pearse
 *
 *   Revision 1.6  2004/04/09 16:55:47  cdb
 *   @scr 4302 Removed double semicolon warnings.
 *
 *   Revision 1.5  2004/02/17 17:57:38  bwf
 *   @scr 0 Organize imports.
 *
 *   Revision 1.4  2004/02/17 16:18:49  rhafernik
 *   @scr 0 log4j conversion
 *
 *   Revision 1.3  2004/02/12 17:13:17  mcs
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
 *    Rev 1.0   Aug 29 2003 15:32:28   CSchellenger
 * Initial revision.
 *
 *    Rev 1.1   Apr 07 2003 10:28:44   bwf
 * Database Internationalization
 * Resolution for 1866: I18n Database  support
 *
 *    Rev 1.0   Jun 03 2002 16:38:54   msg
 * Initial revision.
 *
 *    Rev 1.1   Mar 18 2002 22:46:06   msg
 * - updated copyright
 *
 *    Rev 1.0   Mar 18 2002 12:06:10   msg
 * Initial revision.
 *
 *    Rev 1.1   Feb 08 2002 17:03:52   dfh
 * updates to better sort alerts, oldest on top, newest/updated on bottom
 * Resolution for POS SCR-1169: Service Alert screen does not sort entries correctly
 *
 *    Rev 1.0   Sep 20 2001 15:57:30   msg
 * Initial revision.
 *
 *    Rev 1.1   Sep 17 2001 12:34:12   msg
 * header update
 * ===========================================================================
 */
package oracle.retail.stores.domain.arts;

import java.util.Date;
import java.util.Locale;

import oracle.retail.stores.persistence.utility.ARTSDatabaseIfc;

import org.apache.log4j.Logger;

import oracle.retail.stores.domain.DomainGateway;
import oracle.retail.stores.domain.alert.AlertEntryIfc;
import oracle.retail.stores.domain.alert.AlertListIfc;
import oracle.retail.stores.domain.order.OrderConstantsIfc;
import oracle.retail.stores.domain.order.OrderSearchCriteria;
import oracle.retail.stores.domain.order.OrderSearchCriteriaIfc;
import oracle.retail.stores.domain.order.OrderSummaryEntryIfc;
import oracle.retail.stores.domain.store.StoreIfc;
import oracle.retail.stores.domain.transaction.SearchCriteriaIfc;
import oracle.retail.stores.foundation.manager.data.DataException;
import oracle.retail.stores.foundation.manager.data.JdbcDataConnection;
import oracle.retail.stores.foundation.manager.ifc.data.DataActionIfc;
import oracle.retail.stores.foundation.manager.ifc.data.DataConnectionIfc;
import oracle.retail.stores.foundation.manager.ifc.data.DataTransactionIfc;
import oracle.retail.stores.foundation.utility.SortedVector;
import oracle.retail.stores.persistence.utility.ARTSDatabaseIfc;

import org.apache.log4j.Logger;

/**
 * This returns an AlertListIfc objects containing new orders and new emessages
 * in descending time stamp creation order. ID.
 *
 * @version $Revision: /main/23 $
 * @deprecated as of 14.0.  This class is no longer needed.
 * Use order manager to retrieve order alerts.
 */
public class JdbcRetrieveAlertList extends JdbcRetrieveOrderSummary implements ARTSDatabaseIfc
{
    /** serialVersionUID */
    private static final long serialVersionUID = 6334181831836994217L;

    /** The logger to which log messages will be sent. */
    private static final Logger logger = Logger.getLogger(JdbcRetrieveAlertList.class);

    protected static final String TYPE_EMAIL = "E-Mail";
    protected static final String TYPE_PICKUP = "Pickup";

    /**
     * Class constructor.
     */
    public JdbcRetrieveAlertList()
    {
        super();
        setName("JdbcRetrieveAlertList");
    }

    /**
     * Executes the SQL statements against the database.
     *
     * @param dataTransaction
     * @param dataConnection
     * @param action
     */
    public void execute(DataTransactionIfc dataTransaction, DataConnectionIfc dataConnection, DataActionIfc action)
            throws DataException
    {
        if (logger.isDebugEnabled())
            logger.debug("JdbcRetrieveAlertList.execute");

        // set data connection
        JdbcDataConnection connection = (JdbcDataConnection)dataConnection;

        String storeID = null;
        boolean trainingMode = false;

        if (action.getDataObject() instanceof SearchCriteriaIfc)
        {
            SearchCriteriaIfc inquiry = (SearchCriteriaIfc)action.getDataObject();
            storeID = inquiry.getStoreNumber();
            if (inquiry.getTrainingMode().equals("1"))
            {
            	trainingMode = true;
            }
        }
        else
        {
            storeID = (String)action.getDataObject();
        }

        // grab arguments and call () retrieveAlertList
        AlertListIfc alertList = retrieveAlertList(connection, storeID, trainingMode);

        dataTransaction.setResult(alertList);

        if (logger.isDebugEnabled())
            logger.debug("JdbcRetrieveAlertList.execute");
    }

    /**
     * Creates the SQL statements against the database. Retrieves AlertList
     * objects based upon the storeID. The AlertList contains orders with a
     * status of new and emessages with a status of new, sorted by creation
     * date.
     *
     * @param dataConnection
     * @param storeID
     */
    public AlertListIfc retrieveAlertList(JdbcDataConnection connection, String storeID, boolean trainingMode) throws DataException
    {
        if (logger.isDebugEnabled())
            logger.debug("JdbcAlertReadTransaction.RetrieveAlertList()");
        AlertListIfc alertList = DomainGateway.getFactory().getAlertListInstance();
        int numberReturned = 0;
        OrderSummaryEntryIfc[] orderSumList = null;
        int[] statusNew = new int[1]; // order summary stati
        statusNew[0] = OrderConstantsIfc.ORDER_STATUS_NEW;
        SortedVector<AlertEntryIfc> alertsVector = new SortedVector<AlertEntryIfc>();
        int alertCnt = 5555;
        boolean summariesFound = true; // found new summaries flag
        OrderSearchCriteriaIfc orderSearchCriteria = new OrderSearchCriteria();
        orderSearchCriteria.configure(statusNew, null, null, storeID, trainingMode);

        try
        {
            orderSumList = retrieveOrderSummaries(connection, orderSearchCriteria);
            numberReturned = orderSumList.length;
            if (summariesFound)
            {
                for (int i = 0; i < numberReturned; i++)
                {
                    AlertEntryIfc alert = DomainGateway.getFactory().getAlertEntryInstance();
                    alert.setAlertType(AlertEntryIfc.ALERT_TYPE_ORDER_PICKUP);
                    alert.setItemID(orderSumList[i].getOrderID());
                    alert.setTimeIssued(orderSumList[i].getTimestampCreated());
                    alert.setSummary("#" + alert.getItemID() + ": " + orderSumList[i].getOrderDescription());
                    alert.setAlertID(Integer.toString(alertCnt++));
                    alertsVector.add(alert);
                }
            }
        }
        catch (DataException de) // handle data base exceptions
        {
            logger.error("No Summaries found - Access error: " + de + "");
            summariesFound = false;
        }

        // retrieve emessages by status : new status, store id

        /*
         * The Entire Try-Catch Block below has been commented due to a
         * requirement in PABP regarding email (Access grants)/ (Reply rights),
         * FR51. No emails should be editable nor viewable by any Till User.
         */

        /*
         * try { JdbcRetrieveEMessagesByStatus retrieveEmsgs = new
         * JdbcRetrieveEMessagesByStatus(); emsgVec =
         * retrieveEmsgs.RetrieveEMessagesByStatus(connection,key, locale);
         * emsgList = new EMessageIfc[emsgVec.size()];
         * emsgVec.copyInto(emsgList); numberReturned = emsgList.length; for
         * (int i = 0; i < numberReturned; i++) { entry =
         * emsgList[i].getAlertEntry();
         * entry.setAlertID(Integer.toString(alertCnt++));
         * alertsVector.addElement((ComparableObjectIfc) entry); } } catch
         * (DataException deAlert) // handle data base exceptions {
         * logger.error( "No emessages found Access error: "); if ((orderError
         * != null) && (orderError.getErrorCode() ==
         * DataException.CONNECTION_ERROR)) { logger.error( "" +
         * orderError.getMessage() + ""); throw orderError; } logger.error( "" +
         * deAlert.getMessage() + ""); if (deAlert.getErrorCode() ==
         * DataException.NO_DATA && !summariesFound) { throw new
         * DataException(DataException.NO_DATA, "No service alerts found"); }
         * else if (deAlert.getErrorCode() == DataException.UNKNOWN) { throw new
         * DataException(DataException.CONNECTION_ERROR, "Connection lost"); } }
         */
        // build alertList
        AlertEntryIfc[] alerts = new AlertEntryIfc[alertsVector.size()];
        StoreIfc store = DomainGateway.getFactory().getStoreInstance();
        store.setStoreID(storeID);
        alertList.setStore(store);
        alertList.setSearchCriteria("new orders and emails");
        alertList.setTimeRetrieved();
        alertsVector.copyInto(alerts);
        alertList.setEntries(alerts);

        if (logger.isDebugEnabled())
            logger.debug("JdbcAlertReadTransaction.RetrieveAlertList()");

        return (alertList);
    } // end RetrieveAlertList()

}
