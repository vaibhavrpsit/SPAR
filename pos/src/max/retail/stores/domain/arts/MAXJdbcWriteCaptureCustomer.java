/* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
 * Copyright (c) 2013 Max Hypermarket.    All Rights Reserved. 
 *  Rev 1.0  20/May/2013               Tanmaya 				Bug 5791 - Pos Is Going Offline After performing transaction. 
 * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
package max.retail.stores.domain.arts;

import max.retail.stores.persistence.utility.MAXARTSDatabaseIfc;
import oracle.retail.stores.common.sql.SQLInsertStatement;
import oracle.retail.stores.domain.arts.JdbcDataOperation;
import oracle.retail.stores.domain.customer.CaptureCustomerIfc;
import oracle.retail.stores.domain.utility.CodeConstantsIfc;
import oracle.retail.stores.domain.utility.PhoneConstantsIfc;
import oracle.retail.stores.foundation.manager.data.DataException;
import oracle.retail.stores.foundation.manager.data.JdbcDataConnection;
import oracle.retail.stores.foundation.manager.ifc.data.DataActionIfc;
import oracle.retail.stores.foundation.manager.ifc.data.DataConnectionIfc;
import oracle.retail.stores.foundation.manager.ifc.data.DataTransactionIfc;

public class MAXJdbcWriteCaptureCustomer extends JdbcDataOperation implements MAXARTSDatabaseIfc, CodeConstantsIfc {

	private static final long serialVersionUID = -2338574033450449036L;

	public MAXJdbcWriteCaptureCustomer() {
		super();
		setName("MAXJdbcWriteCaptureCustomer");
	}

	public void execute(DataTransactionIfc dt, DataConnectionIfc dc, DataActionIfc da) throws DataException {
		if (logger.isDebugEnabled())
			logger.debug("MAXJdbcWriteCaptureCustomer.execute");

		JdbcDataConnection connection = (JdbcDataConnection) dc;
		CaptureCustomerIfc customer = (CaptureCustomerIfc) da.getDataObject();

		saveCaptureCustomer(connection, customer);
		if (logger.isDebugEnabled())
			logger.debug("MAXJdbcWriteCaptureCustomer.execute");
	}

	protected void saveCaptureCustomer(JdbcDataConnection dc, CaptureCustomerIfc customer) throws DataException {
		try {
			insertCaptureCustomer(dc, customer);
		} catch (DataException de) {
			throw de;
		} catch (Exception e) {
			throw new DataException(DataException.UNKNOWN);
		}
	}

	protected void insertCaptureCustomer(JdbcDataConnection dc, CaptureCustomerIfc customer) throws DataException {
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
		sql.addColumn(FIELD_CAPTURE_CUSTOMER_AREACODE, getCustomerAreaCode(customer));
		sql.addColumn(FIELD_CAPTURE_CUSTOMER_PHONE_TYPE, getCustomerPhoneType(customer));
		sql.addColumn(FIELD_CAPTURE_CUSTOMER_PHONE, getCustomerPhone(customer));
		sql.addColumn(FIELD_CAPTURE_CUSTOMER_IDTYPE, getCustomerIDType(customer));

		sql.addColumn(FIELD_CAPTURE_CUSTOMER_STORE_ID, getStoreID(customer));
		sql.addColumn(FIELD_CAPTURE_CUSTOMER_WS_ID, getWorkstationID(customer));
		sql.addColumn(FIELD_CAPTURE_CUSTOMER_BUSINESS_DAY, getBusinessDay(customer));
		sql.addColumn(FIELD_CAPTURE_CUSTOMER_TRANSACTION_ID, getTransactionID(customer));

		try {
			dc.execute(sql.getSQLString());
		} catch (DataException de) {
			logger.error("" + de + "");
			throw de;
		} catch (Exception e) {
			logger.error("" + "");
			throw new DataException(DataException.UNKNOWN);
		}
	}

	protected String getStoreID(CaptureCustomerIfc customer) {
		return ("'" + customer.getStoreID() + "'");
	}

	protected String getWorkstationID(CaptureCustomerIfc customer) {
		return ("'" + customer.getWsID() + "'");
	}

	protected String getBusinessDay(CaptureCustomerIfc customer) {
		return dateToSQLDateString(customer.getBusinessDay());
	}

	protected String getTransactionID(CaptureCustomerIfc customer) {
		return (customer.getTransactionID());
	}

	protected String getCustomerFirstName(CaptureCustomerIfc customer) {
		return (makeSafeString(customer.getFirstName()));
	}

	protected String getCustomerLastName(CaptureCustomerIfc customer) {
		return (makeSafeString(customer.getLastName()));
	}

	protected String getCustomerAddress1(CaptureCustomerIfc customer) {
		return (makeSafeString(customer.getAddressLine(0)));
	}

	protected String getCustomerAddress2(CaptureCustomerIfc customer) {
		return (makeSafeString(customer.getAddressLine(1)));
	}

	protected String getCustomerCity(CaptureCustomerIfc customer) {
		return (makeSafeString(customer.getCity()));
	}

	protected String getCustomerCountry(CaptureCustomerIfc customer) {
		return (makeSafeString(customer.getCountry()));
	}

	protected String getCustomerState(CaptureCustomerIfc customer) {
		return (makeSafeString(customer.getState()));
	}

	protected String getCustomerPostal(CaptureCustomerIfc customer) {
		return (makeSafeString(customer.getPostalCode()));
	}

	protected String getCustomerPostalExt(CaptureCustomerIfc customer) {
		return (makeSafeString(customer.getPostalCodeExt()));
	}

	protected String getCustomerAreaCode(CaptureCustomerIfc customer) {
		return makeSafeString(customer.getCountry());
	}

	protected String getCustomerPhoneType(CaptureCustomerIfc customer) {
		if ((customer.getPhoneType() < 0)
				|| (customer.getPhoneType() > PhoneConstantsIfc.PHONE_TYPE_DESCRIPTOR.length)) {
			return makeSafeString("");
		} else {
			return makeSafeString(PhoneConstantsIfc.PHONE_TYPE_DESCRIPTOR[customer.getPhoneType()]);
		}
	}

	protected String getCustomerPhone(CaptureCustomerIfc customer) {
		return makeSafeString(customer.getPhoneNumber());
	}

	protected String getCustomerIDType(CaptureCustomerIfc customer) {
		//Commented For upgradation
		//return (makeSafeString(customer.getIDType()));
		return null;
	}

}
