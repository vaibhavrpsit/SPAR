/* ===========================================================================
* Copyright (c) 1998, 2011, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/modules/domain/src/oracle/retail/stores/domain/arts/JdbcSaveLayawayPaymentHistoryInfo.java /rgbustores_13.4x_generic_branch/1 2011/05/04 11:49:05 mszekely Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    cgreene   05/27/10 - convert to oracle packaging
 *    cgreene   05/26/10 - convert to oracle packaging
 *    cgreene   04/26/10 - XbranchMerge cgreene_tech75 from
 *                         st_rgbustores_techissueseatel_generic_branch
 *    cgreene   03/30/10 - remove deprecated ARTSDatabaseIfcs and change
 *                         SQLException to DataException
 *    abondala  01/03/10 - update header date
 *
 * ===========================================================================
 * $Log:
 *    1    360Commerce 1.0         12/13/2005 4:47:56 PM  Barry A. Pape   
 *
 * ===========================================================================
 */
package oracle.retail.stores.domain.arts;


import oracle.retail.stores.persistence.utility.ARTSDatabaseIfc;

import org.apache.log4j.Logger;

import oracle.retail.stores.common.sql.SQLInsertStatement;
import oracle.retail.stores.common.sql.SQLUpdateStatement;
import oracle.retail.stores.domain.financial.LayawayIfc;
import oracle.retail.stores.domain.financial.PaymentHistoryInfoIfc;
import oracle.retail.stores.foundation.manager.data.DataException;
import oracle.retail.stores.foundation.manager.data.JdbcDataConnection;
import oracle.retail.stores.foundation.manager.ifc.data.DataActionIfc;
import oracle.retail.stores.foundation.manager.ifc.data.DataConnectionIfc;
import oracle.retail.stores.foundation.manager.ifc.data.DataTransactionIfc;
import oracle.retail.stores.foundation.utility.Util;

/**
 * This operation saves (inserts/updates) info in the layaway payment history
 * info table This info is used for IRS Patriot Act
 * 
 * @version $Revision: /rgbustores_13.4x_generic_branch/1 $
 */
public class JdbcSaveLayawayPaymentHistoryInfo extends JdbcDataOperation implements ARTSDatabaseIfc
{
    private static final long serialVersionUID = -2391247283080742476L;

    /**
     * The logger to which log messages will be sent.
     */
    private static final Logger logger = Logger.getLogger(JdbcSaveLayawayPaymentHistoryInfo.class);

    /**
     * revision number supplied by Team Connection
     */
    public static final String revisionNumber = "$Revision: /rgbustores_13.4x_generic_branch/1 $";

    /**
     * The performance logger
     */
    protected static final Logger perf = Logger.getLogger("PERF." + JdbcSaveLayawayPaymentHistoryInfo.class.getName());

    /**
     * Class constructor.
     */
    public JdbcSaveLayawayPaymentHistoryInfo()
    {
        setName("JdbcSaveLayawayPaymentHistoryInfo");
    }

    /**
     * Executes the SQL statements against the database.
     * 
     * @param dataTransaction The data transaction
     * @param dataConnection The connection to the data source
     * @param action The information passed by the valet
     * @exception DataException upon error
     */
    public void execute(DataTransactionIfc dataTransaction,
                        DataConnectionIfc dataConnection,
                        DataActionIfc action)
        throws DataException
    {
        if (logger.isDebugEnabled()) logger.debug( "JdbcSaveLayawayPaymentHistoryInfo.execute()");
        if (perf.isDebugEnabled())
        {
            perf.debug("Entering JdbcSaveLayawayPaymentHistoryInfo.execute");
        }

        JdbcDataConnection connection = (JdbcDataConnection) dataConnection;

        // Navigate the input object to obtain values that will be inserted
        // into the database.
        LayawayIfc item = (LayawayIfc) action.getDataObject();        
        for (int listIndex = 0; listIndex < item.getPaymentHistoryInfoCollection().size(); listIndex++)
        {
            this.insertLayawayPaymentHistoryInfo(connection, 
                                                 item, 
                                                 listIndex);
        }    
        if (perf.isDebugEnabled())
        {
            perf.debug("Exiting JdbcSaveLayawayPaymentHistoryInfo.execute");
        }
        if (logger.isDebugEnabled()) logger.debug( "JdbcSaveLayawayPaymentHistoryInfo.execute()");
    }
    
    
    /**
        Perform layaway payment history info insert. <P>
        @param dataConnection JdbcDataConnection
        @param layaway LayawayIfc reference
        @param listIndex retrieve index from list
        @exception DataException thrown if error occurs
     */
    public void insertLayawayPaymentHistoryInfo(JdbcDataConnection dataConnection,
                                                LayawayIfc layaway,
                                                int listIndex)
                                                throws DataException
    {
        boolean updateRequired = false;
        
        SQLInsertStatement sql = new SQLInsertStatement();
        PaymentHistoryInfoIfc paymentHistory = layaway.getPaymentHistoryInfoCollection().get(listIndex);
        // Table
        sql.setTable(TABLE_IRS_LAYAWAY);

        // Fields
        sql.addColumn(FIELD_LAYAWAY_ID, makeSafeString(layaway.getLayawayID()));
        sql.addColumn(FIELD_TENDER_TYPE_CODE, makeSafeString(paymentHistory.getTenderType()));        
        sql.addColumn(FIELD_PAYMENT_HISTORY_INFO_COUNTRY_CODE, makeSafeString(paymentHistory.getCountryCode()));
        sql.addColumn(FIELD_PAYMENT_HISTORY_INFO_TENDER_AMOUNT, paymentHistory.getTenderAmount().toString());
        try
        {
            dataConnection.execute(sql.getSQLString());
        }
        catch (DataException de)
        {
            // if integrity error occurred, request update
            if (de.getErrorCode() == DataException.REFERENTIAL_INTEGRITY_ERROR)
            {
                updateRequired = true;
            }
            else
            {
                logger.error( de.toString());
                throw de;
            }
        }
        catch (Exception e)
        {
            logger.error(e);
            throw new DataException(DataException.UNKNOWN, "insertLayawayPaymentHistoryInfo", e);
        }
        // update payment history info, if necessary
        if (updateRequired)
        {
            updateLayawayPaymentHistoryInfo(dataConnection, layaway, listIndex);
        }
        
    }
    
    /**
        Perform layaway payment history info update. <P>
        @param dataConnection JdbcDataConnection
        @param layaway LayawayIfc reference
        @param listIndex retrieve index from list
        @exception DataException thrown if error occurs
     */
    public void updateLayawayPaymentHistoryInfo(JdbcDataConnection dataConnection,
                                                LayawayIfc layaway,
                                                int listIndex)
                                                throws DataException
    {
        SQLUpdateStatement sql = new SQLUpdateStatement();
        PaymentHistoryInfoIfc paymentHistory = layaway.getPaymentHistoryInfoCollection().get(listIndex);
        // Table
        sql.setTable(TABLE_IRS_LAYAWAY);

        // Fields        
        sql.addColumn(FIELD_PAYMENT_HISTORY_INFO_TENDER_AMOUNT, paymentHistory.getTenderAmount().toString());
        
        sql.addQualifier(FIELD_LAYAWAY_ID,  makeSafeString(layaway.getLayawayID()));
        sql.addQualifier(FIELD_TENDER_TYPE_CODE, makeSafeString(paymentHistory.getTenderType()));
        sql.addQualifier(FIELD_PAYMENT_HISTORY_INFO_COUNTRY_CODE, makeSafeString(paymentHistory.getCountryCode()));
   
        try
        {
            dataConnection.execute(sql.getSQLString());
        }
        catch (DataException de)
        {
            logger.error(de);
            throw de;
        }
        catch (Exception e)
        {
            logger.error(e);
            throw new DataException(DataException.UNKNOWN, "updateLayawayPaymentHistoryInfo", e);
        }

    }

    /**
       Retrieves the Team Connection revision number. <P>
       @return String representation of revision number
     */
    public String getRevisionNumber()
    {
        // return string
        return(revisionNumber);
    }

    /**
       Returns the string representation of this object.
       @return String representation of object
     */
    @Override
    public String toString()
    {
        return(Util.classToStringHeader("JdbcSaveLayawayPaymentHistoryInfo",
                                        getRevisionNumber(),
                                        hashCode()).toString());
    }
}

