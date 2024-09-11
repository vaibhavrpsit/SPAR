/**
 * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
  Copyright (c) 2013 MAXHyperMarkets, Inc.    All Rights Reserved.
   
*  Rev 1.0   12/08/2014  Shruti Singh   Initial Draft	Centralized Employee Discount 
* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
package max.retail.stores.domain.arts;

import org.apache.log4j.Logger;

import oracle.retail.stores.domain.employee.EmployeeIfc;
import oracle.retail.stores.foundation.manager.data.DataAction;
import oracle.retail.stores.foundation.manager.data.DataException;
import oracle.retail.stores.foundation.manager.data.DataTransaction;
import oracle.retail.stores.foundation.manager.ifc.data.DataActionIfc;
import oracle.retail.stores.foundation.manager.ifc.data.DataTransactionIfc;

//------------------------------------------------------------------------------
/**
    The MAXCentralUpdationEmployeeTransaction implements the employee amount update operation
**/
//------------------------------------------------------------------------------

/**
 * @author Shruti.Singh
 */

public class MAXCentralUpdationEmployeeTransaction extends DataTransaction implements DataTransactionIfc {
	// This id is used to tell
	// the compiler not to generate a
	// new serialVersionUID.
	//
	static final long serialVersionUID = 2898953258278865990L;

	/**
	 * The logger to which log messages will be sent.
	 **/
	private static Logger logger = Logger
			.getLogger(max.retail.stores.domain.arts.MAXCentralUpdationEmployeeTransaction.class);

	/**
	 * revision number of this class
	 **/
	public static final String revisionNumber = "$Revision: 4$";
	/**
	 * The transactionName name links this transaction to a command within the
	 * DataScript.
	 **/
	public static String findForLoginName = "MAXCentralUpdationEmployeeTransaction";

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
	public MAXCentralUpdationEmployeeTransaction() {
		super(findForLoginName);
	}

	// ---------------------------------------------------------------------
	/**
	 * DataCommand constructor. Initializes dataOperations and
	 * dataConnectionPool.
	 **/
	// ---------------------------------------------------------------------
	public MAXCentralUpdationEmployeeTransaction(String name) {
		super(name);
	}

	// ---------------------------------------------------------------------
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
	public void updateEmloyeeAmount(String employeeId, String amount, String companyName) throws DataException {
		lookupParm = employeeId + " " + amount;

		// creates an anynonmous DataActionIfc object.
		DataActionIfc[] dataActions = new DataActionIfc[1];
		DataAction dataAction = new DataAction();
		dataAction.setDataOperationName("centralupdateemployee");
		dataAction.setDataObject(lookupParm);
		dataActions[0] = dataAction;
		setDataActions(dataActions);
		getDataManager().execute(this);

		// MAXEmployeeIfc employee = (MAXEmployeeIfc)
		// getDataManager().execute(this);
		// return(employee);
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
				"Class: MAXCentralUpdationEmployeeTransaction (Revision " + getRevisionNumber() + ") @" + hashCode());

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
} // end class MAXCentralUpdationEmployeeTransaction
