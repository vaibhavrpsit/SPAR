/* ===========================================================================
* Copyright (c) 2008, 2012, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/modules/domain/src/oracle/retail/stores/domain/arts/JdbcReadCaptureCustomer.java /main/15 2012/05/21 15:50:18 cgreene Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    cgreene   05/21/12 - XbranchMerge cgreene_bug-13951397 from
 *                         rgbustores_13.5x_generic
 *    cgreene   05/16/12 - arrange order of businessDay column to end of
 *                         primary key to improve performance since most
 *                         receipt lookups are done without the businessDay
 *    cgreene   05/28/10 - convert to oracle packaging
 *    cgreene   05/27/10 - convert to oracle packaging
 *    cgreene   05/27/10 - convert to oracle packaging
 *    cgreene   05/26/10 - convert to oracle packaging
 *    cgreene   04/26/10 - XbranchMerge cgreene_tech75 from
 *                         st_rgbustores_techissueseatel_generic_branch
 *    cgreene   03/30/10 - remove deprecated ARTSDatabaseIfcs and change
 *                         SQLException to DataException
 *    abondala  01/03/10 - update header date
 *
 * ===========================================================================
 */
package oracle.retail.stores.domain.arts;

import java.sql.ResultSet;
import java.sql.SQLException;

import oracle.retail.stores.persistence.utility.ARTSDatabaseIfc;

import oracle.retail.stores.common.sql.SQLSelectStatement;
import oracle.retail.stores.domain.customer.CaptureCustomerIfc;
import oracle.retail.stores.foundation.manager.data.DataException;
import oracle.retail.stores.foundation.manager.data.JdbcDataConnection;
import oracle.retail.stores.foundation.manager.ifc.data.DataActionIfc;
import oracle.retail.stores.foundation.manager.ifc.data.DataConnectionIfc;
import oracle.retail.stores.foundation.manager.ifc.data.DataTransactionIfc;

/**
 * @author khassen
 */
public class JdbcReadCaptureCustomer extends JdbcDataOperation implements ARTSDatabaseIfc
{
    private static final long serialVersionUID = -8983774924209520548L;


    /* (non-Javadoc)
     * @see oracle.retail.stores.foundation.manager.ifc.data.DataOperationIfc#execute(oracle.retail.stores.foundation.manager.ifc.data.DataTransactionIfc, oracle.retail.stores.foundation.manager.ifc.data.DataConnectionIfc, oracle.retail.stores.foundation.manager.ifc.data.DataActionIfc)
     */
    public void execute(DataTransactionIfc dt, DataConnectionIfc dc, DataActionIfc da) throws DataException
    {
        CaptureCustomerIfc customer = (CaptureCustomerIfc) da.getDataObject();

        String transactionID = customer.getTransactionID();
        
        selectCaptureCustomer((JdbcDataConnection) dc, transactionID);
    }


    protected void selectCaptureCustomer(JdbcDataConnection dc, String transactionID) throws DataException
    {
        SQLSelectStatement sql = new SQLSelectStatement();
        
        sql.setTable(TABLE_CAPTURE_CUSTOMER);
        
        sql.addQualifier(FIELD_TRANSACTION_SEQUENCE_NUMBER, transactionID);

        sql.addColumn(FIELD_CAPTURE_CUSTOMER_FIRST_NAME);
        sql.addColumn(FIELD_CAPTURE_CUSTOMER_LAST_NAME);
        sql.addColumn(FIELD_CAPTURE_CUSTOMER_ADDRESS_LINE_1);
        sql.addColumn(FIELD_CAPTURE_CUSTOMER_ADDRESS_LINE_2);
        sql.addColumn(FIELD_CAPTURE_CUSTOMER_CITY);
        sql.addColumn(FIELD_CAPTURE_CUSTOMER_COUNTRY);
        sql.addColumn(FIELD_CAPTURE_CUSTOMER_STATE);
        sql.addColumn(FIELD_CAPTURE_CUSTOMER_POSTAL);
        sql.addColumn(FIELD_CAPTURE_CUSTOMER_POSTAL_EXT);
        sql.addColumn(FIELD_CAPTURE_CUSTOMER_AREACODE);
        sql.addColumn(FIELD_CAPTURE_CUSTOMER_PHONE_TYPE);
        sql.addColumn(FIELD_CAPTURE_CUSTOMER_PHONE);
        sql.addColumn(FIELD_CAPTURE_CUSTOMER_IDTYPE);
        
        try
        {
            dc.execute(sql.getSQLString());

            ResultSet rs = (ResultSet) dc.getResult();

            if ( !rs.next() )
            {
                logger.warn("JdbcReadCaptureCustomer: transaction not found!");
                throw new DataException(DataException.NO_DATA, "transaction not found");
            }

            int index = 0;
            String firstName     = getSafeString(rs, ++index);
            String lastName      = getSafeString(rs, ++index);
            String addressLine1  = getSafeString(rs, ++index);
            String addressLine2  = getSafeString(rs, ++index);
            String city          = getSafeString(rs, ++index);
            String country       = getSafeString(rs, ++index);
            String state         = getSafeString(rs, ++index);
            String postalCode    = getSafeString(rs, ++index);
            String postalCodeExt = getSafeString(rs, ++index);
            String areaCode      = getSafeString(rs, ++index);
            String phoneType     = getSafeString(rs, ++index);
            String phone         = getSafeString(rs, ++index);
            String idType        = getSafeString(rs, ++index);
        }
        catch (DataException de)
        {
            logger.warn(de.toString());
            throw de;
        }
        catch (SQLException se)
        {
            dc.logSQLException(se, "capture customer table");
            throw new DataException(DataException.SQL_ERROR, "capture customer table", se);
        }
        catch (Exception e)
        {
            throw new DataException(DataException.UNKNOWN, "capture customer table", e);
        }
    }
}
