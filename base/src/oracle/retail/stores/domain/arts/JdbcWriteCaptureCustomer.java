/* ===========================================================================
* Copyright (c) 2008, 2012, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/modules/domain/src/oracle/retail/stores/domain/arts/JdbcWriteCaptureCustomer.java /main/17 2012/09/12 11:57:21 blarsen Exp $
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
 *    cgreene   04/03/12 - removed deprecated methods
 *    cgreene   05/28/10 - convert to oracle packaging
 *    cgreene   05/27/10 - convert to oracle packaging
 *    cgreene   05/27/10 - convert to oracle packaging
 *    cgreene   05/26/10 - convert to oracle packaging
 *    cgreene   04/26/10 - XbranchMerge cgreene_tech75 from
 *                         st_rgbustores_techissueseatel_generic_branch
 *    cgreene   03/30/10 - remove deprecated ARTSDatabaseIfcs and change
 *                         SQLException to DataException
 *    abondala  01/03/10 - update header date
 *    mdecama   10/27/08 - I18N - Refactoring Reason Codes for
 *                         CaptureCustomerIDTypes
 * ===========================================================================

$$Log:
$ 3    360Commerce 1.2         3/31/2005 4:28:46 PM   Robert Pearse
$ 2    360Commerce 1.1         3/10/2005 10:22:55 AM  Robert Pearse
$ 1    360Commerce 1.0         2/11/2005 12:12:07 PM  Robert Pearse
$$
$Revision 1.4  2004/06/18 05:34:30  khassen
$@scr 5684 - Feature enhancements for capture customer use case.
$
$Revision 1.3  2004/06/17 20:37:12  khassen
$@scr 5684 - Feature enhancements for capture customer info use case.
$$

* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */

package oracle.retail.stores.domain.arts;

import oracle.retail.stores.persistence.utility.ARTSDatabaseIfc;

import oracle.retail.stores.common.sql.SQLInsertStatement;
import oracle.retail.stores.domain.utility.PhoneConstantsIfc;
import oracle.retail.stores.domain.customer.CaptureCustomer;
import oracle.retail.stores.domain.customer.CaptureCustomerIfc;
import oracle.retail.stores.domain.utility.CodeConstantsIfc;
import oracle.retail.stores.foundation.manager.data.DataException;
import oracle.retail.stores.foundation.manager.data.JdbcDataConnection;
import oracle.retail.stores.foundation.manager.ifc.data.DataActionIfc;
import oracle.retail.stores.foundation.manager.ifc.data.DataConnectionIfc;
import oracle.retail.stores.foundation.manager.ifc.data.DataTransactionIfc;

/**
 * @author khassen
 */
public class JdbcWriteCaptureCustomer extends JdbcDataOperation implements ARTSDatabaseIfc, CodeConstantsIfc
{
    /**
     * Generated SerialVersionUID
     */
    private static final long serialVersionUID = -5384969602892056893L;

    public JdbcWriteCaptureCustomer()
    {
        super();
        setName("JdbcWriteCaptureCustomer");
    }


    /* (non-Javadoc)
     * @see oracle.retail.stores.foundation.manager.ifc.data.DataOperationIfc#execute(oracle.retail.stores.foundation.manager.ifc.data.DataTransactionIfc, oracle.retail.stores.foundation.manager.ifc.data.DataConnectionIfc, oracle.retail.stores.foundation.manager.ifc.data.DataActionIfc)
     */
    public void execute(DataTransactionIfc dt, DataConnectionIfc dc, DataActionIfc da) throws DataException
    {
        if (logger.isDebugEnabled()) logger.debug( "JdbcWriteCaptureCustomer.execute");

        /*
         * getUpdateCount() is about the only thing outside of
         * DataConnectionIfc that we need.
         */
        JdbcDataConnection connection = (JdbcDataConnection)dc;
        CaptureCustomer customer = (CaptureCustomer)da.getDataObject();

        saveCaptureCustomer(connection, customer);
        if (logger.isDebugEnabled()) logger.debug( "JdbcWriteCaptureCustomer.execute");
    }

    protected void saveCaptureCustomer(JdbcDataConnection dc, CaptureCustomer customer)
    throws DataException
    {
        try
        {
            insertCaptureCustomer(dc, customer);
        }
        catch (DataException de)
        {
            throw de;
        }
        catch (Exception e)
        {
            throw new DataException(DataException.UNKNOWN);
        }
    }

    protected void insertCaptureCustomer(JdbcDataConnection dc, CaptureCustomer customer)
    throws DataException
    {
        SQLInsertStatement sql = new SQLInsertStatement();

        sql.setTable(TABLE_CAPTURE_CUSTOMER);

        sql.addColumn(FIELD_CAPTURE_CUSTOMER_FIRST_NAME, getCustomerFirstName(customer));
        sql.addColumn(FIELD_CAPTURE_CUSTOMER_LAST_NAME, getCustomerLastName(customer));
        sql.addColumn(FIELD_CAPTURE_CUSTOMER_ADDRESS_LINE_1, getCustomerAddress1(customer));
        sql.addColumn(FIELD_CAPTURE_CUSTOMER_ADDRESS_LINE_2, getCustomerAddress2(customer));
        sql.addColumn(FIELD_CAPTURE_CUSTOMER_CITY, getCustomerCity(customer));
        sql.addColumn(FIELD_CAPTURE_CUSTOMER_COUNTRY, getCustomerCountry(customer));
        sql.addColumn(FIELD_CAPTURE_CUSTOMER_STATE, getCustomerState(customer));
        sql.addColumn(FIELD_CAPTURE_CUSTOMER_POSTAL, getCustomerPostal(customer));
        sql.addColumn(FIELD_CAPTURE_CUSTOMER_POSTAL_EXT, getCustomerPostalExt(customer));
        sql.addColumn(FIELD_CAPTURE_CUSTOMER_PHONE_TYPE, getCustomerPhoneType(customer));
        sql.addColumn(FIELD_CAPTURE_CUSTOMER_PHONE, getCustomerPhone(customer));
        sql.addColumn(FIELD_CAPTURE_CUSTOMER_IDTYPE, getCustomerIDType(customer));

        sql.addColumn(FIELD_RETAIL_STORE_ID, getStoreID(customer));
        sql.addColumn(FIELD_WORKSTATION_ID, getWorkstationID(customer));
        sql.addColumn(FIELD_TRANSACTION_SEQUENCE_NUMBER, getTransactionID(customer));
        sql.addColumn(FIELD_BUSINESS_DAY_DATE, getBusinessDay(customer));

        try
        {
            dc.execute(sql.getSQLString());
        }
        catch (DataException de)
        {
            logger.error("" + de + "");
            throw de;
        }
        catch (Exception e)
        {
            logger.error("" + "");
            throw new DataException(DataException.UNKNOWN);
        }
    }

    protected String getStoreID(CaptureCustomerIfc customer)
    {
        return ("'" + customer.getStoreID() + "'");
    }

    protected String getWorkstationID(CaptureCustomerIfc customer)
    {
        return ("'" + customer.getWsID() + "'");
    }

    protected String getBusinessDay(CaptureCustomerIfc customer)
    {
        return dateToSQLDateString(customer.getBusinessDay());
    }

    protected String getTransactionID(CaptureCustomerIfc customer)
    {
        return (customer.getTransactionID());
    }

    protected String getCustomerFirstName(CaptureCustomerIfc customer)
    {
        return (makeSafeString(customer.getFirstName()));
    }

    protected String getCustomerLastName(CaptureCustomerIfc customer)
    {
        return (makeSafeString(customer.getLastName()));
    }


    protected String getCustomerAddress1(CaptureCustomerIfc customer)
    {
        return (makeSafeString(customer.getAddressLine(0)));
    }

    protected String getCustomerAddress2(CaptureCustomerIfc customer)
    {
        return (makeSafeString(customer.getAddressLine(1)));
    }

    protected String getCustomerCity(CaptureCustomerIfc customer)
    {
        return (makeSafeString(customer.getCity()));
    }

    protected String getCustomerCountry(CaptureCustomerIfc customer)
    {
        return (makeSafeString(customer.getCountry()));
    }

    protected String getCustomerState(CaptureCustomerIfc customer)
    {
        return (makeSafeString(customer.getState()));
    }

    protected String getCustomerPostal(CaptureCustomerIfc customer)
    {
        return (makeSafeString(customer.getPostalCode()));
    }

    protected String getCustomerPostalExt(CaptureCustomerIfc customer)
    {
        return (makeSafeString(customer.getPostalCodeExt()));
    }

    protected String getCustomerPhoneType(CaptureCustomerIfc customer)
    {
        if ((customer.getPhoneType() < 0) || (customer.getPhoneType() > PhoneConstantsIfc.PHONE_TYPE_DESCRIPTOR.length))
        {
            return makeSafeString("");
        }
        
        return makeSafeString(PhoneConstantsIfc.PHONE_TYPE_DESCRIPTOR[customer.getPhoneType()]);
    }

    protected String getCustomerPhone(CaptureCustomerIfc customer)
    {
        return makeSafeString(customer.getPhoneNumber());
    }

    protected String getCustomerIDType(CaptureCustomerIfc customer)
    {
        String customerIDType = "";
        if (customer.getPersonalIDType() != null)
            customerIDType = (makeSafeString(customer.getPersonalIDType().getCode()));
        return customerIDType;
    }

}
