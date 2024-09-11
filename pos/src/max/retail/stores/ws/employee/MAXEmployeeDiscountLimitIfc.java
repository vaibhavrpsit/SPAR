/* *****************************************************************************************
 * Copyright (c) 2015   Lifestyle India Pvt. Ltd.All Rights Reserved.
 *
 * Initial Draft 	October 17th,2015:	 Aakash Gupta(EYLLP)
 * *******************************************************************************************/
package max.retail.stores.ws.employee;

import java.sql.Connection;
import java.sql.ResultSet;

import javax.jws.WebMethod;
import javax.jws.WebService;

/**
 * Service Endpoint Interface that hosts the web service for Employee Discount
 * Limit Functionality.
 *
 * @author Aakash Gupta
 */
@WebService
public interface MAXEmployeeDiscountLimitIfc {

	/**
	 * POS uses this method to get Employee Details for specified Employee ID.
	 *
	 * @author Aakash Gupta
	 * @return String
	 * @param empID
	 */
	public String getEmployeeDetails(String empID);

	/**
	 * Retrieves employee Details from database for specified employee id.
	 *
	 * @author Aakash Gupta
	 * @return ResultSet
	 * @param empID
	 */
	@WebMethod(exclude = true)
	public ResultSet getEmployee(String empID);

	/**
	 * Returns employee details as String in xml format.
	 *
	 * @author Aakash Gupta
	 * @param empDetails
	 * @return String
	 */
	@WebMethod(exclude = true)
	public String getEmployeeAsXMLString(MAXEmployeeDetails empDetails);

	/**
	 * POS uses this method to update the available employee discount.</br>
	 * Returns true upon successful update of employee discount limit, false
	 * otherwise.
	 *
	 * @author Aakash Gupta
	 * @param empID
	 * @param returnedAmount
	 * @return boolean
	 */
	public boolean updateEmployee(String empID, Double returnedAmount);

	/**
	 * Returns true upon successful update of employee discount limit, false
	 * otherwise.
	 *
	 * @author Aakash Gupta
	 * @param availableAmount
	 * @return boolean
	 */
	@WebMethod(exclude = true)
	public boolean updateEmployeeDetail(String empID, Double returnedAmount);

	/**
	 * Helper Method to connect to database.
	 *
	 * @author Aakash Gupta
	 * @return Connection
	 */
	@WebMethod(exclude = true)
	public Connection getConnection();
	
	
	public boolean isRollOutComplete();
}