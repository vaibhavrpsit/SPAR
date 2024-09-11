/* ===========================================================================
* Copyright (c) 1998, 2011, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/modules/domain/src/oracle/retail/stores/domain/arts/JdbcRetrieveEMessagesByStatus.java /main/14 2011/12/05 12:16:25 cgreene Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    cgreene   12/05/11 - updated from deprecated packages and used more
 *                         bigdecimal constants
 *    cgreene   05/26/10 - convert to oracle packaging
 *    cgreene   04/26/10 - XbranchMerge cgreene_tech43 from
 *                         st_rgbustores_techissueseatel_generic_branch
 *    cgreene   04/02/10 - remove deprecated LocaleContantsIfc and currencies
 *    abondala  01/03/10 - update header date
 *    djenning  12/01/08 - wrapping pos sql timestamp inserts/updates/selects
 *                         with the proper db function for oracle 11g
 *
 * ===========================================================================
 * $Log:
 *    7    360Commerce 1.6         3/29/2008 9:50:08 AM   Dwight D. Jennings
 *         changing column holding content of the EMessage from varchar 250 to
 *          a blob.
 *
 *         Reviewed by Dan Baker. Luis approved the column type change.
 *    6    360Commerce 1.5         6/30/2006 4:13:59 PM   Brendan W. Farrell
 *         Update for UDM.
 *    5    360Commerce 1.4         6/9/2006 2:38:36 PM    Brett J. Larsen CR
 *         18490 - UDM
 *         FL_NM_.* changed to NM_.*
 *         timestamp fields in DO_EMSG renamed to ts_..._emsg for consistency
 *    4    360Commerce 1.3         1/25/2006 4:11:20 PM   Brett J. Larsen merge
 *          7.1.1 changes (aka. 7.0.3 fixes) into 360Commerce view
 *    3    360Commerce 1.2         3/31/2005 4:28:42 PM   Robert Pearse   
 *    2    360Commerce 1.1         3/10/2005 10:22:47 AM  Robert Pearse   
 *    1    360Commerce 1.0         2/11/2005 12:12:01 PM  Robert Pearse   
 *:
 *    4    .v700     1.2.1.0     11/16/2005 16:27:42    Jason L. DeLeau 4215:
 *         Get rid of redundant ArtsDatabaseifc class
 *    3    360Commerce1.2         3/31/2005 15:28:42     Robert Pearse
 *    2    360Commerce1.1         3/10/2005 10:22:47     Robert Pearse
 *    1    360Commerce1.0         2/11/2005 12:12:01     Robert Pearse
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
 *   Revision 1.3  2004/02/12 17:13:17  mcs
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
 *    Rev 1.0   Aug 29 2003 15:32:32   CSchellenger
 * Initial revision.
 *
 *    Rev 1.1   Apr 07 2003 10:29:38   bwf
 * Database Internationalization and Deprication Fixes
 * Resolution for 1866: I18n Database  support
 *
 *    Rev 1.0   Jun 03 2002 16:39:04   msg
 * Initial revision.
 *
 *    Rev 1.2   17 May 2002 17:13:10   sfl
 * Single quote fix for DB2
 * Resolution for POS SCR-1667: Layaway - SQL error when trying to select an existing layaway
 *
 *    Rev 1.1   Mar 18 2002 22:46:10   msg
 * - updated copyright
 *
 *    Rev 1.0   Mar 18 2002 12:06:16   msg
 * Initial revision.
 *
 *    Rev 1.1   Feb 08 2002 17:03:52   dfh
 * updates to better sort alerts, oldest on top, newest/updated on bottom
 * Resolution for POS SCR-1169: Service Alert screen does not sort entries correctly
 *
 *    Rev 1.0   Sep 20 2001 15:57:26   msg
 * Initial revision.
 *
 *    Rev 1.1   Sep 17 2001 12:34:12   msg
 * header update
 * ===========================================================================
 */
package oracle.retail.stores.domain.arts;
// java imports
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Locale;
import java.util.Vector;

import org.apache.log4j.Logger;

import oracle.retail.stores.persistence.utility.ARTSDatabaseIfc;
import oracle.retail.stores.domain.DomainGateway;
import oracle.retail.stores.domain.emessage.EMessageIfc;
import oracle.retail.stores.domain.utility.EYSDate;
import oracle.retail.stores.domain.utility.LocaleConstantsIfc;
import oracle.retail.stores.foundation.manager.data.DataException;
import oracle.retail.stores.foundation.manager.data.JdbcDataConnection;
import oracle.retail.stores.foundation.manager.ifc.data.DataActionIfc;
import oracle.retail.stores.foundation.manager.ifc.data.DataConnectionIfc;
import oracle.retail.stores.foundation.manager.ifc.data.DataTransactionIfc;
import oracle.retail.stores.common.utility.LocaleMap;
import oracle.retail.stores.common.sql.SQLSelectStatement;

//--------------------------------------------------------------------------
/**
    This operation reads all of the emessages from the emessage table, matching
    a specified status with optional storeid, begin date, and end date.
    <P>
    @version $Revision: /main/14 $
**/
//--------------------------------------------------------------------------
public class JdbcRetrieveEMessagesByStatus extends JdbcDataOperation implements ARTSDatabaseIfc
{
    /**
        The logger to which log messages will be sent.
    **/
    private static Logger logger = Logger.getLogger(oracle.retail.stores.domain.arts.JdbcRetrieveEMessagesByStatus.class);

    //----------------------------------------------------------------------
    /**
       Class constructor.
    **/
    //----------------------------------------------------------------------
    public JdbcRetrieveEMessagesByStatus()
    {
        super();
        setName("JdbcRetrieveEMessagesByStatus");
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
        if (logger.isDebugEnabled()) logger.debug( "JdbcRetrieveEMessagesByStatus.execute");

        // set data connection
        JdbcDataConnection connection = (JdbcDataConnection) dataConnection;

        // grab arguments and call retrieveEMessagesByStatus()
        OrderSearchKey orderSearchKey = (OrderSearchKey)action.getDataObject();
        Vector emessageVector = retrieveEMessagesByStatus(connection, orderSearchKey);
        EMessageIfc[] emessageList = new EMessageIfc[emessageVector.size()];
        emessageVector.copyInto(emessageList);

        dataTransaction.setResult(emessageList);

        if (logger.isDebugEnabled()) logger.debug( "JdbcRetrieveEMessagesByStatus.execute");
    }

    //----------------------------------------------------------------------
    /**
       Creates the SQL statements against the database. Retrieves emessage
       objects based upon the orderSearchKey: status[],storeID,beginDate,endDate.
       The storeId, beginDate, and endDate are optional.
       <P>
       @param  dataConnection
       @param  orderSearchKey
    **/
    //----------------------------------------------------------------------
    public Vector retrieveEMessagesByStatus(JdbcDataConnection connection,
                                            OrderSearchKey orderSearchKey) throws DataException
    {
        if (logger.isDebugEnabled()) logger.debug( "JdbcOrderReadTransaction.RetrieveEMessagesByStatus()");

        SQLSelectStatement sql = new SQLSelectStatement();
        String statusQualifier = null; // OR statement qualifier for SELECT

        // add tables
        sql.addTable(TABLE_EMESSAGE, ALIAS_EMESSAGE);

        // add columns
        sql.addColumn(FIELD_EMESSAGE_TEXT);

        sql.addColumn(FIELD_EMESSAGE_ID);
        sql.addColumn(FIELD_ORDER_ID);
        sql.addColumn(FIELD_EMESSAGE_STATUS);
        sql.addColumn(FIELD_EMESSAGE_CREATED);
        sql.addColumn(FIELD_EMESSAGE_SENT);
        sql.addColumn(FIELD_EMESSAGE_UPDATED);
        sql.addColumn(FIELD_EMESSAGE_SOURCE);
        sql.addColumn(FIELD_EMESSAGE_DESTINATION);
        sql.addColumn(FIELD_EMESSAGE_SUBJECT);
        sql.addColumn(FIELD_RETAIL_STORE_ID);
        sql.addColumn(FIELD_CUSTOMER_ID);
        sql.addColumn(FIELD_CUSTOMER_NAME);
        sql.addColumn(FIELD_ORDER_LOCATION);

        // test and add qualifiers for the stati - OR clauses
        int[] statusList = orderSearchKey.getStatuses(); // required
        if (statusList.length > 1)
        {
            statusQualifier = "(" + FIELD_EMESSAGE_STATUS + " = " + statusList[0];
            for (int i = 1; i < statusList.length; i++)  // build rest of qualifier
            {
                statusQualifier +=  " OR ";
                statusQualifier += FIELD_EMESSAGE_STATUS + " = " + statusList[i];
            }
            statusQualifier += ")\n";

            sql.getQualifierList().add(statusQualifier);
        }
        else
        {
            sql.addQualifier(FIELD_EMESSAGE_STATUS + " = " + statusList[0]);
        }

        String storeID = orderSearchKey.getStoreID();
        if (storeID != null)
        {
            sql.addQualifier(FIELD_RETAIL_STORE_ID + " = '" + storeID + "'");
        }

        EYSDate beginDate = orderSearchKey.getBeginDate();
        if (beginDate != null)
        {
            sql.addQualifier(FIELD_EMESSAGE_CREATED + " >= " + dateToSQLTimestampFunction(beginDate));
        }

        EYSDate endDate = orderSearchKey.getEndDate();
        if (endDate != null)
        {
            sql.addQualifier(FIELD_EMESSAGE_CREATED + " <= " + dateToSQLTimestampFunction(endDate));
        }

        // Ordering
        sql.addOrdering(FIELD_EMESSAGE_CREATED + " ASC");

        // Instantiate Order
        int rsStatus = 0;
        Vector emessageVector = new Vector();
        try
        {
            // execute sql and get result set
            connection.execute(sql.getSQLString());
            ResultSet rs = (ResultSet)connection.getResult();

            int index;

            // loop through result set
            while (rs.next())
            {                 // begin loop through result set
                ++rsStatus;
                String text = getBlobStringFromResultSetWithoutNext(rs);

                // since we already processed the first column as a blob, start index at 1
                index = 1;
                String emsgIDString = getSafeString(rs, ++index);
                String orderIDString = getSafeString(rs, ++index);
                int emsgStatus = rs.getInt(++index);
                EYSDate beginDay = timestampToEYSDate(rs, ++index);
                EYSDate sentDay = timestampToEYSDate(rs, ++index);
                EYSDate updateDate = timestampToEYSDate(rs, ++index);
                String src = getSafeString(rs, ++index);
                String dest = getSafeString(rs, ++index);
                String subject = getSafeString(rs, ++index);
                storeID = getSafeString(rs, ++index);
                String custID = getSafeString(rs, ++index);
                String custName = getSafeString(rs, ++index);
                String storeLoc = getSafeString(rs, ++index);

                // Instantiate emessage
                EMessageIfc emessage = DomainGateway.getFactory().getEMessageInstance();
                emessage.setMessageID(emsgIDString);
                emessage.setOrderID(orderIDString);
                emessage.setMessageStatus(emsgStatus);
                emessage.setTimestampBegin(beginDay);
                emessage.setTimestampSent(sentDay);
                emessage.setTimestampUpdate(updateDate);
                emessage.setMessageText(text);
                emessage.setSender(src);
                emessage.addRecipient(dest);
                emessage.setSubject(subject);
                emessage.setShipToStoreID(storeID);
                emessage.setCustomerID(custID);
                emessage.setCustomerName(custName);
                emessage.setShipToLocationName(storeLoc);

                emessageVector.addElement(emessage);
            }
            // end loop through result set
            // close result set
            rs.close();
            
        }
        catch (DataException de)
        {
            logger.warn( "" + de + "");
            if (de.getErrorCode() == DataException.UNKNOWN)
            {
                throw new DataException(DataException.CONNECTION_ERROR, "Connection lost");
            }
            else
            {
                throw de;
            }
        }
        catch (SQLException se)
        {
            connection.logSQLException(se, "emessage table");
            throw new DataException(DataException.SQL_ERROR, "emessage table", se);
        }

        if (rsStatus == 0)
        {
            logger.warn( "No emessages found");
            throw new DataException(DataException.NO_DATA, "No emessagers found");
        }

        if (logger.isDebugEnabled()) logger.debug( "JdbcOrderReadTransaction.RetrieveEMessagesByStatus()");

        return(emessageVector);

    }
    
    //----------------------------------------------------------------------
    /**
       Creates the SQL statements against the database. Retrieves emessage
       objects based upon the orderSearchKey: status[],storeID,beginDate,endDate.
       The storeId, beginDate, and endDate are optional.
       <P>
       @param  dataConnection
       @param  orderSearchKey
       @deprecated As of release 13.1 Use @link #RetrieveEMessagesByStatus(JdbcDataConnection, OrderSearchKey)
    **/
    //----------------------------------------------------------------------
    public Vector RetrieveEMessagesByStatus(JdbcDataConnection connection,
                                            OrderSearchKey orderSearchKey,
                                            Locale sqlLocale) throws DataException
    {
    	return retrieveEMessagesByStatus(connection, orderSearchKey);
    }                                   // end RetrieveEMessagesByStatus()
}
