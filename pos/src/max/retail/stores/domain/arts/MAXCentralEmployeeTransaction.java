/********************************************************************************
 *   
 *	Copyright (c) 2016 - 2017 MAX Hypermarket, Inc    All Rights Reserved.
 *	
 *
 *  Rev 1.0     Nov 22, 2016	        Ashish Yadav		Changes for Employee Discount FES
 *
 ********************************************************************************/
package max.retail.stores.domain.arts;

import java.util.HashMap;

import org.apache.log4j.Logger;

import max.retail.stores.domain.employee.MAXEmployeeIfc;
import oracle.retail.stores.domain.employee.EmployeeIfc;
import oracle.retail.stores.foundation.manager.data.DataAction;
import oracle.retail.stores.foundation.manager.data.DataException;
import oracle.retail.stores.foundation.manager.data.DataTransaction;
import oracle.retail.stores.foundation.manager.ifc.data.DataActionIfc;
import oracle.retail.stores.foundation.manager.ifc.data.DataTransactionIfc;

//------------------------------------------------------------------------------
/**
    The MAXCentralEmployeeTransaction implements the employee lookup operations
**/
//------------------------------------------------------------------------------

/**
 * @author Shruti.Singh
 */
public class MAXCentralEmployeeTransaction extends DataTransaction implements DataTransactionIfc {
	// This id is used to tell
	// the compiler not to generate a
	// new serialVersionUID.
	//
	static final long serialVersionUID = 2898953258278865990L;

	/**
	 * The logger to which log messages will be sent.
	 **/
	private static Logger logger = Logger.getLogger(max.retail.stores.domain.arts.MAXCentralEmployeeTransaction.class);

	/**
	 * revision number of this class
	 **/
	public static final String revisionNumber = "$Revision: 4$";
	/**
	 * The transactionName name links this transaction to a command within the
	 * DataScript.
	 **/
	public static String findForLoginName = "MAXCentralEmployeeTransaction";
	/**
	 * Holds the employee id, name, or number
	 **/
	protected String lookupParm;
	/**
	 * The queryType indicates the type of query.
	 **/
	protected int queryType;
	/**
	 * The employee is the employee used for a name search
	 **/
	protected EmployeeIfc employee = null;

	// ---------------------------------------------------------------------
	/**
	 * DataCommand constructor. Initializes dataOperations and
	 * dataConnectionPool.
	 **/
	// ---------------------------------------------------------------------
	public MAXCentralEmployeeTransaction() {
		super(findForLoginName);
	}

	// ---------------------------------------------------------------------
	/**
	 * DataCommand constructor. Initializes dataOperations and
	 * dataConnectionPool.
	 **/
	// ---------------------------------------------------------------------
	public MAXCentralEmployeeTransaction(String name) {
		super(name);
	}

	/**
	 * Obtains a Employee given an employee ID.
	 * <p>
	 * 
	 * @param loginID
	 *            The String lookup key.
	 * @return
	 * @exception DataException
	 *                is thrown if the Employee cannot be found.
	 **/
	// ---------------------------------------------------------------------
	/*
	public MAXEmployeeIfc getEmployeeNumber(String number) throws DataException {
		lookupParm = number;

		// creates an anynonmous DataActionIfc object.
		DataActionIfc[] dataActions = new DataActionIfc[1];
		DataAction dataAction = new DataAction();
		dataAction.setDataOperationName("centralemployeelookup");
		dataAction.setDataObject(lookupParm);
		dataActions[0] = dataAction;
		setDataActions(dataActions);

		MAXEmployeeIfc employee = null;
		try {
			employee = (MAXEmployeeIfc) getDataManager().execute(this);
		} catch (DataException e) {
			// TODO Auto-generated catch block
			throw new DataException(e.getErrorCode());
			// e.printStackTrace();
		}

		return (employee);
	}
*/
	
	public MAXEmployeeIfc getEmployeeNumber(String employeeID, String companyName) throws DataException {
		//lookupParm = number;

		// creates an anynonmous DataActionIfc object.
		HashMap<String,String> employeeData= new HashMap<String,String>();
		employeeData.put("employeeID", employeeID);
	//	employeeData.put("companyName", companyName);
		DataActionIfc[] dataActions = new DataActionIfc[1];
		DataAction dataAction = new DataAction();
		dataAction.setDataOperationName("centralemployeelookup");
		dataAction.setDataObject(employeeData);
		//dataAction.setDataObject(empDetailData);
		dataActions[0] = dataAction;
		setDataActions(dataActions);

		MAXEmployeeIfc employee = null;
		try {
			employee = (MAXEmployeeIfc) getDataManager().execute(this);
			
		} catch (DataException e) {
			logger.error(e);
			throw new DataException(e.getErrorCode());
		}

		return (employee);
	}
	
	// ---------------------------------------------------------------------
	/**
	 * Returns the revision number of this class.
	 * <P>
	 * 
	 * @return String representation of revision number
	 **/
	// ---------------------------------------------------------------------
	public String getRevisionNumber() {
		return (revisionNumber);
	}

	// ---------------------------------------------------------------------
	/**
	 * Returns the string representation of this object.
	 * <P>
	 * 
	 * @return String representation of object
	 **/
	// ---------------------------------------------------------------------
	public String toString() {
		String strResult = new String(
				"Class: MAXCentralEmployeeTransaction (Revision " + getRevisionNumber() + ") @" + hashCode());

		strResult += "\nlookupParm = " + lookupParm + "\n";

		strResult += "\nqueryType =" + queryType + "\n";

		if (employee != null) {

			strResult += "\nemployee = \n";
			strResult += employee + "\n";
		} else {
			strResult += "\nemployee = null\n";
		}
		return (strResult);
	}
	
	// Changes starts for Rev 1.0 (Ashish : Employee Discount)
	public boolean updateEmployeeDetails(String employeeID,Double transactionAmount, String companyName){
		Boolean updateResult=false;
		HashMap<String,Object> employeeDetails= new HashMap<String,Object>();
		employeeDetails.put("EmployeeID",employeeID);
		employeeDetails.put("TransactionAmount",transactionAmount);
		// below code is added by atul shukla for company Name
		employeeDetails.put("CompanyName", companyName);
		DataActionIfc[] dataActions = new DataActionIfc[1];
		DataAction dataAction = new DataAction();
		dataAction.setDataOperationName("centralemployeeupdate");
		dataAction.setDataObject(employeeDetails);
		dataActions[0] = dataAction;
		setDataActions(dataActions);

		try {
			updateResult=(Boolean)getDataManager().execute(this);
		} catch (DataException e) {
			logger.error(e);
		}

		return updateResult;
	}
	
	public MAXEmployeeIfc getEmployeeID(String employeeID, String companyName) throws DataException {
		//lookupParm = number;

		// creates an anynonmous DataActionIfc object.
		HashMap<String,String> employeeData= new HashMap<String,String>();
		employeeData.put("employeeID", employeeID);
	//	employeeData.put("companyName", companyName);
		DataActionIfc[] dataActions = new DataActionIfc[1];
		DataAction dataAction = new DataAction();
		dataAction.setDataOperationName("centralemployeelookup");
		dataAction.setDataObject(employeeData);
		//dataAction.setDataObject(empDetailData);
		dataActions[0] = dataAction;
		setDataActions(dataActions);

		
		
		MAXEmployeeIfc employee = null;
		try {
			employee = (MAXEmployeeIfc) getDataManager().execute(this);
		} catch (DataException e) {
			logger.error(e);
			throw new DataException(e.getErrorCode());
		}

		return (employee);
	}
	
	// Chnages ends for Rev 1.0 (Ashish : Employee Discount)
} // end class MAXCentralEmployeeTransaction

