/********************************************************************************
 *   
 *	Copyright (c) 2016 - 2017 MAX Hypermarket, Inc    All Rights Reserved.
 *	
 *
 *	Rev 1.0		Nov 17, 2016        Ashish Yadav    Employee Discount FES
 ********************************************************************************/

package max.retail.stores.domain.arts;

// java imports

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;

import max.retail.stores.domain.employee.MAXEmployee;
import max.retail.stores.domain.employee.MAXEmployeeIfc;
import max.retail.stores.persistence.utility.MAXARTSDatabaseIfc;
import oracle.retail.stores.common.data.JdbcUtilities;
import oracle.retail.stores.common.sql.SQLSelectStatement;
import oracle.retail.stores.domain.DomainGateway;
import oracle.retail.stores.domain.arts.JdbcDataOperation;
import oracle.retail.stores.domain.employee.EmployeeTypeEnum;
import oracle.retail.stores.domain.utility.EYSDate;
import oracle.retail.stores.domain.utility.LocaleUtilities;
import oracle.retail.stores.domain.utility.PersonNameIfc;
import oracle.retail.stores.foundation.manager.data.DataException;
import oracle.retail.stores.foundation.manager.data.JdbcDataConnection;
import oracle.retail.stores.foundation.manager.ifc.data.DataActionIfc;
import oracle.retail.stores.foundation.manager.ifc.data.DataConnectionIfc;
import oracle.retail.stores.foundation.manager.ifc.data.DataTransactionIfc;
import oracle.retail.stores.foundation.utility.Util;

import org.apache.log4j.Logger;

//--------------------------------------------------------------------------
/**
 * The JdbcEmployeeLookupOperation implements the employee lookup JDBC data
 * store operation.
 * 
 */
// --------------------------------------------------------------------------

public class MAXJdbcCentralEmployeeLookupOperation extends JdbcDataOperation implements MAXARTSDatabaseIfc

{
	private static final long serialVersionUID = 1L;
	private static Logger logger = Logger
			.getLogger(max.retail.stores.domain.arts.MAXJdbcCentralEmployeeLookupOperation.class);

	public MAXJdbcCentralEmployeeLookupOperation() {
		super();
		setName("centralemployeelookup");

	}

	// ---------------------------------------------------------------------
	/**
	 * Set all data members should be set to their initial state.
	 * <P>
	 *
	 * @exception DataException
	 */
	// ---------------------------------------------------------------------
	public void initialize() throws DataException { // begin initialize()
													// no action taken here
	} // end initialize()

	// ---------------------------------------------------------------------
	/**
	 * Sets up the where clause to find employee by Employee id
	 * 
	 * @return Employee employee object
	 * @param connection
	 *            data connection interface object
	 * @param employeeID
	 *            employee ID
	 * @exception DataException
	 */
	// ---------------------------------------------------------------------
	/*
	public MAXEmployeeIfc selectEmployeeByNumber(DataConnectionIfc connection, String employeeID) throws DataException {
		String whereClause = "ID_EM=" + makeSafeString(employeeID);
		return (selectEmployee(connection, whereClause));
	}
*/
	
	public MAXEmployeeIfc selectEmployeeByNumber(DataConnectionIfc connection, HashMap<String, String> employeeDetail) throws DataException
	{
		// below code is added by atul shukla
		String employeeID = (String) employeeDetail.get("employeeID");
		String companyName = (String) employeeDetail.get("companyName");
		//String employeeID=empDetailData[0];
		//String companyName=empDetailData[1];
		String whereClause = "ID_EM=" + makeSafeString(employeeID) +" and "+"COMPANY_NAME="+ makeSafeString(companyName);
		
	//	String whereClause = "ID_EM=" + makeSafeString(employeeID);
		return (selectEmployee(connection, whereClause));
	}
	// ---------------------------------------------------------------------
	/**
	 * Executes the select employee SQL statement based on the where clause
	 * 
	 * @return Employee employee object
	 * @param connection
	 *            data connection interface object
	 * @param whereClause
	 *            one of the where clauses created above
	 * @exception DataException
	 */
	// ---------------------------------------------------------------------
	public MAXEmployeeIfc selectEmployee(DataConnectionIfc connection, String whereClause) throws DataException {

		SQLSelectStatement sql = new SQLSelectStatement();

		/*
		 * Define table
		 */
		sql.addTable(TABLE_EMPLOYEE, ALIAS_EMPLOYEE);

		/*
		 * Add columns and their values
		 */
		sql.addColumn(ALIAS_EMPLOYEE + "." + FIELD_EMPLOYEE_ID);
		sql.addColumn(ALIAS_EMPLOYEE + "." + FIELD_EMPLOYEE_ID_ALT);
		sql.addColumn(ALIAS_EMPLOYEE + "." + FIELD_EMPLOYEE_ACCESS_PASSWORD);
		sql.addColumn(ALIAS_EMPLOYEE + "." + FIELD_EMPLOYEE_ID_LOGIN);
		sql.addColumn(ALIAS_EMPLOYEE + "." + FIELD_EMPLOYEE_NAME);
		sql.addColumn(ALIAS_EMPLOYEE + "." + FIELD_EMPLOYEE_LAST_NAME);
		sql.addColumn(ALIAS_EMPLOYEE + "." + FIELD_EMPLOYEE_FIRST_NAME);
		sql.addColumn(ALIAS_EMPLOYEE + "." + FIELD_EMPLOYEE_MIDDLE_NAME);
		sql.addColumn(ALIAS_EMPLOYEE + "." + FIELD_EMPLOYEE_SOCIAL_SECURITY_NUMBER);
		sql.addColumn(ALIAS_EMPLOYEE + "." + FIELD_EMPLOYEE_STATUS_CODE);
		sql.addColumn(ALIAS_EMPLOYEE + "." + FIELD_LOCALE);
		sql.addColumn(ALIAS_EMPLOYEE + "." + FIELD_EMPLOYEE_ROLE);
		sql.addColumn(ALIAS_EMPLOYEE + "." + FIELD_EMPLOYEE_NUMBER_OF_DAYS_VALID);
		sql.addColumn(ALIAS_EMPLOYEE + "." + FIELD_EMPLOYEE_EXPIRATION_DATE);
		sql.addColumn(ALIAS_EMPLOYEE + "." + FIELD_EMPLOYEE_TYPE);
		sql.addColumn(ALIAS_EMPLOYEE + "." + FIELD_EMPLOYEE_STORE_ID);
		sql.addColumn(ALIAS_EMPLOYEE + "." + FIELD_EMPLOYEE_NEW_PASSWORD_REQUIRED);
		sql.addColumn(ALIAS_EMPLOYEE + "." + FIELD_EMPLOYEE_NUMBER_FAILED_PASSWORDS);
		sql.addColumn(ALIAS_EMPLOYEE + "." + FIELD_EMPLOYEE_PASSWORD_CREATED_TIMESTAMP);

		sql.addColumn(ALIAS_EMPLOYEE + "." + FIELD_ID_TR_EN);
		/*
		 * sql.addColumn(ALIAS_EMPLOYEE + "." + FIELD_SPL_EMP_DSC);
		 */
		sql.addColumn(ALIAS_EMPLOYEE + "." + FIELD_ELG_AMT);
		sql.addColumn(ALIAS_EMPLOYEE + "." + FIELD_AVL_AMT);

		sql.addQualifier(whereClause);

		// Extract data from the result set.
		MAXEmployeeIfc employee = null;
		PersonNameIfc employeeName = null;

		try {
			connection.execute(sql.getSQLString());
			ResultSet rs = (ResultSet) connection.getResult();
			EYSDate systemDate = DomainGateway.getFactory().getEYSDateInstance();
			if (rs.next()) {
				// Filter out expired employees
				EYSDate expirationDate = getEYSDateFromString(rs, 14);
				boolean expired = true;
				if (expirationDate == null || systemDate.before(expirationDate)) {
					expired = false;
				}
				if (!expired) {

					// employee = (MAXEmployeeIfc)
					// DomainGateway.getFactory().getEmployeeInstance();
					employee = new MAXEmployee();
					employeeName = DomainGateway.getFactory().getPersonNameInstance();
					// set data elements in the employee. The numbers are the
					// column
					// order in the table.
					employee.setEmployeeID(getSafeString(rs, 1));
					employee.setAlternateID(getSafeString(rs, 2));
					String hex = rs.getString(3);
					// Changes start for Rev 1.0 (Ashish : Employee Discount)
					employee.setPasswordBytes(JdbcUtilities.base64decode(hex));
					// Changes ends for Rev 1.0 (Ashish : Employee Discount)
					employee.setLoginID(getSafeString(rs, 4));
					employeeName.setFullName(getSafeString(rs, 5));
					employeeName.setLastName(getSafeString(rs, 6));
					employeeName.setFirstName(getSafeString(rs, 7));
					employeeName.setMiddleName(getSafeString(rs, 8));
					employee.setSocialSecurityNumber(getSafeString(rs, 9));
					employee.setLoginStatus(Integer.parseInt(getSafeString(rs, 10)));

					String language = getSafeString(rs, 11);

					// Do these out of order because the call after this block
					// will
					// cause the ResultSet to be closed
					employee.setDaysValid(rs.getInt(13));
					employee.setExpirationDate(expirationDate);
					employee.setType(EmployeeTypeEnum.getEnumForDBVal(rs.getInt(15)));
					employee.setStoreID(getSafeString(rs, 16));
					employee.setPasswordChangeRequired(getBooleanFromString(rs, 17));
					employee.setNumberFailedPasswords(rs.getInt(18));
					employee.setPasswordCreationDate(timestampToDate(rs.getTimestamp(19)));
					if (!Util.isEmpty(language)) {
						try {
							employee.setPreferredLocale(LocaleUtilities.getLocaleFromString(language));
						} catch (IllegalArgumentException e) {
							logger.warn("JdbcEmployeeLookupOperation.execute(): Employee preferredLocale is not valid");
						}
					}

					employee.setPersonName(employeeName);
					// employee.setSpecialEmployeeDiscountValue(rs.getString(FIELD_SPL_EMP_DSC).trim());
					employee.setEligibleAmount(rs.getString(FIELD_ELG_AMT).trim());
					employee.setAvailableAmount(rs.getString(FIELD_AVL_AMT).trim());
					// below code is added by atul shukla for employee company name
					employee.setCompanyName(rs.getString(FIELD_COMPANY_NAME).trim());
				}
			}
		} catch (SQLException e) {
			((JdbcDataConnection) connection).logSQLException(e, "Processing result set.");
			throw new DataException(DataException.SQL_ERROR,
					"An SQL Error occurred proccessing the result set from selecting an employee in JdbcEmployeeLookupOperation.",
					e);
		} catch (NumberFormatException e) {
			logger.error("JdbcEmployeeLookupOperation.execute(): NumberFormatException.");
			logger.error("" + e + "");
			throw new DataException(DataException.DATA_FORMAT,
					"Found an unexpected numeric data format in JdbcEmployeeLookupOperation.", e);
		}

		if (employee == null) {
			throw new DataException(DataException.NO_DATA,
					"No employee was found proccessing the result set in JdbcEmployeeLookupOperation.");
		}

		return (employee);
	}

	public void execute(DataTransactionIfc transaction, DataConnectionIfc connection, DataActionIfc action)
			throws DataException {

		JdbcDataConnection dataConnection = (JdbcDataConnection) connection;
	//	MAXEmployeeIfc employee = selectEmployeeByNumber(dataConnection, action.getDataObject().toString());
		MAXEmployeeIfc employee = selectEmployeeByNumber(dataConnection, (HashMap<String, String>) action.getDataObject());
		transaction.setResult(employee);
	}

}
