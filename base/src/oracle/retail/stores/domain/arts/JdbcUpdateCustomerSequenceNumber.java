/* ===========================================================================
* Copyright (c) 2008, 2011, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/modules/domain/src/oracle/retail/stores/domain/arts/JdbcUpdateCustomerSequenceNumber.java /rgbustores_13.4x_generic_branch/1 2011/05/04 11:49:01 mszekely Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    cgreene   05/26/10 - convert to oracle packaging
 *    cgreene   04/26/10 - XbranchMerge cgreene_tech75 from
 *                         st_rgbustores_techissueseatel_generic_branch
 *    cgreene   03/30/10 - remove deprecated ARTSDatabaseIfcs and change
 *                         SQLException to DataException
 *    abondala  01/03/10 - update header date
 *    mahising  12/09/08 - rework of base issue
 *    mahising  12/04/08 - JUnit fix and SQL fix
 *    mahising  11/21/08 - Updated comments
 *    mahising  11/19/08 - Updated for review comments
 *    mahising  11/19/08 - Added for customer module
 *
 * ===========================================================================
 */
package oracle.retail.stores.domain.arts;

import org.apache.log4j.Logger;

import oracle.retail.stores.common.sql.SQLUpdateStatement;
import oracle.retail.stores.domain.financial.RegisterIfc;
import oracle.retail.stores.foundation.manager.data.DataException;
import oracle.retail.stores.foundation.manager.data.JdbcDataConnection;
import oracle.retail.stores.foundation.manager.ifc.data.DataActionIfc;
import oracle.retail.stores.foundation.manager.ifc.data.DataConnectionIfc;
import oracle.retail.stores.foundation.manager.ifc.data.DataTransactionIfc;

/**
 * This operation update customer sequence number database.
 * <P>
 *
 * @version $Revision: /rgbustores_13.4x_generic_branch/1 $
 * @since 13.1
 */
public class JdbcUpdateCustomerSequenceNumber extends JdbcReadTransaction
{
    private static final long serialVersionUID = -484826185744316768L;

    /**
     * The logger to which log messages will be sent.
     */
    private static final Logger logger = Logger
            .getLogger(oracle.retail.stores.domain.arts.JdbcUpdateCustomerSequenceNumber.class);

    /**
     * revision number
     */
    public static final String revisionNumber = "$Revision: /rgbustores_13.4x_generic_branch/1 $";

    /**
     * Class constructor.
     */
    public JdbcUpdateCustomerSequenceNumber()
    {
        super();
        setName("JdbcUpdateCustomerSequenceNumber");
    }

    /**
     * Executes the SQL statements against the database.
     * <P>
     *
     * @param dataTransaction The data transaction
     * @param dataConnection The connection to the data source
     * @param action The information passed by the valet
     * @exception DataException upon error
     */
    public void execute(DataTransactionIfc dataTransaction, DataConnectionIfc dataConnection, DataActionIfc action)
            throws DataException
    {
        if (logger.isDebugEnabled())
            logger.debug("JdbcUpdateCustomerSequenceNumber.execute");

        // set data connection
        JdbcDataConnection connection = (JdbcDataConnection)dataConnection;

        // update customer sequence number
        updateCustomerSequenceNumber(connection, (RegisterIfc)action.getDataObject());

        if (logger.isDebugEnabled())
            logger.debug("JdbcUpdateCustomerSequenceNumber.execute");
    }

    /**
     * Updates customer sequence number in workstation table.
     * <P>
     *
     * @param dataConnection connection to the db
     * @param register the register information
     * @param updateSettings if true, include register settings
     * @exception DataException upon error
     */
    public void updateCustomerSequenceNumber(JdbcDataConnection dataConnection, RegisterIfc register)
            throws DataException
    {
        SQLUpdateStatement sql = new SQLUpdateStatement();
        sql.setTable(TABLE_WORKSTATION);

        sql.addColumn(FIELD_CUSTOMER_SEQUENCE_NUMBER, register.getLastCustomerSequenceNumber());

        /*
         * Add Qualifier(s)
         */
        sql.addQualifier(FIELD_RETAIL_STORE_ID + " = " + inQuotes(register.getWorkstation().getStoreID()));
        sql.addQualifier(FIELD_WORKSTATION_ID + " = " + inQuotes(register.getWorkstation().getWorkstationID()));

        dataConnection.execute(sql.getSQLString());

        if (0 >= dataConnection.getUpdateCount())
        {
            throw new DataException(DataException.NO_DATA, "Update customer sequence number in Workstation table");
        }
    }
}
