/* *****************************************************************************************
 * Copyright (c) 2015  SPAR India Pvt. Ltd.All Rights Reserved.
 *Rev 1.1 15th NOV 2022  Kajal Nautiyal
 * Employee discount validation through OTP.

 * Rev 1.0		Aakash Gupta		17-Nov-2015
 * BugFix(BugId:1000):-Employee Details screen contents are not correct.
 *
 * Initial Draft 	October 17th,2015:	 Aakash Gupta(EYLLP)
 * *******************************************************************************************/

package max.retail.stores.enterpriseconnection.employee;

import java.io.IOException;
import java.io.StringReader;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.apache.log4j.Logger;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import max.retail.stores.domain.arts.MAXCentralEmployeeTransaction;
import max.retail.stores.domain.employee.MAXEmployee;
import max.retail.stores.domain.employee.MAXEmployeeIfc;
import max.retail.stores.domain.factory.MAXDomainObjectFactory;
import max.retail.stores.ws.employee.MAXEmployeeDiscountLimitIfc;
import max.retail.stores.ws.employee.MAXEmployeeDiscountLimitService;
import oracle.retail.stores.common.context.BeanLocator;
import oracle.retail.stores.common.jaxws.connector.JAXWSConnector;
import oracle.retail.stores.domain.DomainGateway;
import oracle.retail.stores.domain.employee.EmployeeTypeEnum;
import oracle.retail.stores.domain.utility.PersonNameIfc;
import oracle.retail.stores.enterpriseconnection.manager.EnterpriseDataOperation;
import oracle.retail.stores.foundation.manager.data.DataException;
import oracle.retail.stores.foundation.manager.ifc.data.DataActionIfc;
import oracle.retail.stores.foundation.manager.ifc.data.DataConnectionIfc;
import oracle.retail.stores.foundation.manager.ifc.data.DataTransactionIfc;

/**
 * Enterprise class that interacts with Employee Discount Limit Web Service
 * hosted at CO.
 *
 * @author Aakash Gupta
 */
@SuppressWarnings("deprecation")
public class MAXEnterpriseReadEmployee extends EnterpriseDataOperation {

	protected static final Logger logger = Logger.getLogger(MAXEnterpriseReadEmployee.class);

	JAXWSConnector connector = null;

	protected JAXWSConnector getWSConnector() throws DataException {
		if (connector == null || !connector.isInitialized()) {
			try {
				connector = (JAXWSConnector) BeanLocator.getServiceBean("service_EmployeeWS");
				connector.openConnector();

			} catch (Exception e) {
				logRemoteExceptions(e, "error getting WebService connector.", null);
			}
		}

		return connector;
	}

	@Override
	public void execute(DataTransactionIfc dataTransaction, DataConnectionIfc dataConnection, DataActionIfc dataAction)
			throws DataException {
		MAXEmployeeIfc employee = null;
		if (dataTransaction instanceof MAXCentralEmployeeTransaction) {
			employee = new MAXEmployee();
			getWSConnector();
			MAXEmployeeDiscountLimitService service = (MAXEmployeeDiscountLimitService) connector.getServiceClass();
			MAXEmployeeDiscountLimitIfc discountLimit = service.getMAXEmployeeDiscountLimitPort();
			String employeeDetails  = discountLimit.getEmployeeDetails(dataAction.getDataObject().toString());
			try {
				if (employeeDetails != null) {
					DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
					DocumentBuilder builder = factory.newDocumentBuilder();
					InputSource is = new InputSource(new StringReader(employeeDetails));
					Document document = builder.parse(is);
					Element rootNode = document.getDocumentElement();
					unmarshallEmployee(employee, rootNode);
					dataTransaction.setResult(employee);
				}
			} catch (IOException exception) {
				exception.printStackTrace();
			}
			catch (ParserConfigurationException exception) {
				exception.printStackTrace();
			}
			catch (SAXException exception) {
				exception.printStackTrace();
			}
			catch (NullPointerException exception) {
				exception.printStackTrace();
			}
		}
	}

	/**
	 * Unmarshalls the employee object.
	 *
	 * @author Aakash Gupta
	 * @param employee
	 * @param rootNode
	 */
	protected void unmarshallEmployee(MAXEmployeeIfc employee, Element rootNode) {
		String empID = rootNode.getElementsByTagName("Employee_ID").item(0).getTextContent();
		String loginID = rootNode.getElementsByTagName("Login_ID").item(0).getTextContent();
		String alternateID = rootNode.getElementsByTagName("Alternate_ID").item(0).getTextContent();
		// Change for Rev 1.0:Starts
		String designation = rootNode.getElementsByTagName("Designation").item(0).getTextContent();
		// Change for Rev 1.0:Ends
		String firstName = rootNode.getElementsByTagName("First_Name").item(0).getTextContent();
		String lastName = rootNode.getElementsByTagName("Last_Name").item(0).getTextContent();
		// Change for Rev 1.0:Starts
		String location = rootNode.getElementsByTagName("Location").item(0).getTextContent();
		// Change for Rev 1.0:Ends
		String employeeType = rootNode.getElementsByTagName("Employee_Type").item(0).getTextContent();

		String newPasswordFlag = rootNode.getElementsByTagName("NewPassword_Flag").item(0).getTextContent();

		String failedPasswordCount = rootNode.getElementsByTagName("Failed_Password_Count").item(0).getTextContent();
		String specialDiscountFlag = rootNode.getElementsByTagName("Special_Discount_Flag").item(0).getTextContent();
		String eligibleAmount = rootNode.getElementsByTagName("Eligible_Discount_Amount").item(0).getTextContent();
		String availableAmount = rootNode.getElementsByTagName("Available_Discount_Amount").item(0).getTextContent();
		// String storeID =
		// rootNode.getElementsByTagName("Store_ID").item(0).getTextContent();
		// String partyID =
		// rootNode.getElementsByTagName("Party_ID").item(0).getTextContent();
		// String workGroupID =
		// rootNode.getElementsByTagName("Workgroup_ID").item(0).getTextContent();
		//Rev 1.1 startS
		String locale = null;
		if(rootNode.getElementsByTagName("Locale") != null && rootNode.getElementsByTagName("Locale").item(0) != null ) {
		  locale = rootNode.getElementsByTagName("Locale").item(0).getTextContent();
		}
		String validInvalidCheck = null;
		if(rootNode.getElementsByTagName("Valid_Invalid_Check") != null && rootNode.getElementsByTagName("Valid_Invalid_Check").item(0) != null ) {
			validInvalidCheck = rootNode.getElementsByTagName("Valid_Invalid_Check").item(0).getTextContent();
		}
		String statusCode = null;
		if(rootNode.getElementsByTagName("Status_code") != null && rootNode.getElementsByTagName("Status_code").item(0) != null ) {
			statusCode = rootNode.getElementsByTagName("Status_code").item(0).getTextContent();
		}
	//	String statusCode= rootNode.getElementsByTagName("Status_code").item(0).getTextContent();
		//String validInvalidCheck=rootNode.getElementsByTagName("Valid_Invalid_Check").item(0).getTextContent();
		//Rev 1.1 ends
		 // String passwordCreateDate =
		// rootNode.getElementsByTagName("Password_Create_Date").item(0).getTextContent();
		MAXDomainObjectFactory domainObjectFactory = (MAXDomainObjectFactory) DomainGateway.getFactory();
		PersonNameIfc personName = domainObjectFactory.getPersonNameInstance();

		employee.setEmployeeID(empID);
		employee.setLoginID(loginID);
		employee.setAlternateID(alternateID);
		// Change for Rev 1.0:Starts
		employee.setFullName(designation);
		// Change for Rev 1.0:Ends
		employee.setName(personName);
		personName.setFirstName(firstName);
		personName.setLastName(lastName);
		// Change for Rev 1.0:Starts
		personName.setMiddleName(location);
		personName.setFullName(designation);
		// Change for Rev 1.0:Starts
		employee.setPersonName(personName);
		employee.setType(employeeType.equals("0") ? EmployeeTypeEnum.STANDARD : EmployeeTypeEnum.TEMPORARY);
		employee.setPasswordChangeRequired(new Boolean(newPasswordFlag));
		employee.setNumberFailedPasswords(new Integer(failedPasswordCount));
		//employee.setSpecialDiscountFlag(specialDiscountFlag.equals("true"));// AAKASH
		employee.setEligibleAmount(eligibleAmount);
		employee.setAvailableAmount(availableAmount);
		employee.setLocale(locale);//Rev 1.1 
		employee.setValidInvalidCheck(validInvalidCheck);//Rev 1.1 
		employee.setStatusCode(statusCode);//Rev 1.1 
		//System.out.println("-----------------------------"+statusCode);

		//System.out.println(validInvalidCheck);
		// employee.setPasswordCreationDate(new Date().setDate(date););
		// employee.setStoreID(storeID);
	}

}
