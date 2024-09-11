/* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
 *  Copyright (c) 2016 - 2017 MAX Hypermarket, Inc.    All Rights Reserved.
 *  
 *	Rev 1.2    SEP 13,2017        	Karni Singh			PIN code enable.
 *	Rev 1.1     Nov 08, 2016		Ashish Yadav		Cashless Loyalty FES
 *	Rev 1.0     Oct 18, 2016		Ashish Yadav		Code Merging
 *
 * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */

package max.retail.stores.pos.services.customer.tic;

import java.util.ArrayList;
import java.util.List;

import max.retail.stores.domain.customer.MAXTICCustomerIfc;
import max.retail.stores.domain.factory.MAXDomainObjectFactory;
import max.retail.stores.domain.ticcustomer.MAXTICCustomerConfig;
import max.retail.stores.pos.services.customer.main.MAXCustomerMainCargo;
import max.retail.stores.pos.ui.MAXPOSUIManagerIfc;
import max.retail.stores.pos.ui.beans.MAXTICCustomerModel;
import max.retail.stores.pos.utility.MAXCommonValidation;
import oracle.retail.stores.domain.DomainGateway;
import oracle.retail.stores.domain.utility.Gender;
import oracle.retail.stores.foundation.tour.application.Letter;
import oracle.retail.stores.foundation.tour.gate.Gateway;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.pos.services.PosSiteActionAdapter;
import oracle.retail.stores.pos.ui.DialogScreensIfc;
import oracle.retail.stores.pos.ui.POSUIManager;
import oracle.retail.stores.pos.ui.POSUIManagerIfc;
import oracle.retail.stores.pos.ui.beans.DialogBeanModel;
import oracle.retail.stores.pos.ui.beans.POSBaseBeanModel;

public class MAXTICCRMRequestSite extends PosSiteActionAdapter {

	/**
	 * 
	 */
	private static final long serialVersionUID = 4361757247378728628L;

	public void arrive(BusIfc bus) {

		List configList = null;
		boolean isTicVisibleDB = false;
		boolean isTicMandatoryDB = false;
		// String dialogMessage = "";

		List<String> errorList = new ArrayList<String>();
		MAXCustomerMainCargo cargo = (MAXCustomerMainCargo) bus.getCargo();
		// /crm request codes
		MAXDomainObjectFactory domainFactory = (MAXDomainObjectFactory) DomainGateway
				.getFactory();
		MAXTICCustomerIfc ticcustomer = (MAXTICCustomerIfc) domainFactory
				.getTICCustomerInstance();

		POSUIManagerIfc ui = (POSUIManagerIfc) bus
				.getManager(POSUIManager.TYPE);

		POSBaseBeanModel model = (POSBaseBeanModel) ui
				.getModel(MAXPOSUIManagerIfc.ADD_TIC_CUSTOMER_OPTIONS);
		if (model instanceof MAXTICCustomerModel) {
			MAXTICCustomerModel ticCustomerModel = (MAXTICCustomerModel) model;
			String firstName = ticCustomerModel.getFirstName();
			String lastName = ticCustomerModel.getLastName();
			String mobile = ticCustomerModel.getMobile();
			// Changes start for Rev 1.1 (Cardlless Loyalty)
			String birthDate = ticCustomerModel.getBirthDate();
			String birthYear = ticCustomerModel.getBirthYear();
			String dobCombined = "";
			// Changes ends for Rev 1.1 (Cardlless Loyalty)
			/*String dob = ticCustomerModel.getDob();*/
			String pinCode = ticCustomerModel.getPinCode();
			String gender = ticCustomerModel.getGender();
			String email = ticCustomerModel.getEmail();
			configList = ticCustomerModel.getConfigList();

			String firstNameLabel = Gateway.getProperty("customerText",
					"firstNamelabel", "First Name");
			String lastNameLabel = Gateway.getProperty("customerText",
					"lastNamelabel", "Last Name");
			String mobileLabel = Gateway.getProperty("customerText",
					"mobNolabel", "Mobile No");
			String pinCodeLabel = Gateway.getProperty("customerText",
					"pinCodelabel", "Pin Code");
			// Changes start for Rev 1.1 (Cardless Loyalty)
			/*String dobLabel = Gateway.getProperty("customerText", "doblabel",
					"Date of Birth");*/
			String birthDateLabel = Gateway.getProperty("customerText",
					"birthDateLabel", "Birth Date");
			String birthYearLabel = Gateway.getProperty("customerText",
					"birthYearLabel", "Birth Year");
			String dobLabel = Gateway.getProperty("customerText", "dobLabel",
					"Date of Birth");
			// Changes start for Rev 1.1 (Cardless Loyalty)
			String emailLabel = Gateway.getProperty("customerText",
					"emaillabel", "E-mail");

			if (configList != null && configList.size() > 0) {
				MAXTICCustomerConfig ticCustomer = (MAXTICCustomerConfig) configList
						.get(0);

				if (ticCustomer != null
						&& (ticCustomer.getDisplayField().equalsIgnoreCase("Y") || ticCustomer
								.getDisplayField().equalsIgnoreCase("T"))) {
					isTicVisibleDB = true;
				}
				if (ticCustomer != null
						&& (ticCustomer.getMandatoryField().equalsIgnoreCase(
								"Y") || ticCustomer.getMandatoryField()
								.equalsIgnoreCase("T"))) {
					isTicMandatoryDB = true;
				}
			}

			// validation codes: for first Name
			MAXCommonValidation validation = new MAXCommonValidation();

			if (!validation.blankCheck(firstNameLabel, firstName)
					.equalsIgnoreCase("")) {
				errorList.add(validation.blankCheck(firstNameLabel, firstName));
				// dialogMessage += validation.blankCheck(firstNameLabel,
				// firstName);

			} else if (!validation.minLengthValidate(firstNameLabel, firstName,
					new Integer(1)).equalsIgnoreCase("")) {
				errorList.add(validation.minLengthValidate(firstNameLabel,
						firstName, new Integer(1)));
				// dialogMessage += validation.minLengthValidate(firstNameLabel,
				// firstName,new Integer(1));

			}

			// / validation for the last name

			if (!validation.blankCheck(lastNameLabel, lastName)
					.equalsIgnoreCase("")) {
				// dialogMessage += validation.blankCheck(lastNamelabel,
				// lastName);
				errorList.add(validation.blankCheck(lastNameLabel, lastName));

			} else if (!validation.minLengthValidate(lastNameLabel, lastName,
					new Integer(1)).equalsIgnoreCase("")) {
				// dialogMessage += validation.minLengthValidate(lastNamelabel,
				// lastName,new Integer(1));
				errorList.add(validation.minLengthValidate(lastNameLabel,
						lastName, new Integer(1)));

			}

			// / validation for the mobile No

			if (!validation.blankCheck(mobileLabel, mobile)
					.equalsIgnoreCase("")) {
				// dialogMessage += validation.blankCheck(mobileLabel, mobile);
				errorList.add(validation.blankCheck(mobileLabel, mobile));

			} else if (!validation.exactCheck(mobileLabel, mobile,
					new Integer(10)).equalsIgnoreCase("")) {
				// dialogMessage += validation.exactCheck(mobileLabel, mobile,
				// new
				// Integer(10));
				errorList.add(validation.exactCheck(mobileLabel, mobile,
						new Integer(10)));

			} else if (!validation.minValueCheck(mobileLabel, mobile,
					"1000000000").equalsIgnoreCase("")) {

				errorList.add(validation.minValueCheck(mobileLabel, mobile,
						"1000000000"));
			}

			// / validation for the mobile No
			
			// Rev 1.2 changes start here 
			// /for pincode

				if (!validation.blankCheck(pinCodeLabel, pinCode)
						.equalsIgnoreCase("")) {
					// dialogMessage += validation.blankCheck(pinCodeLabel,
					// pinCode);
					errorList.add(validation.blankCheck(pinCodeLabel, pinCode));

				} else if (validation.blankCheck(pinCodeLabel, pinCode)
						.equalsIgnoreCase("")
						&& !validation.exactCheck(pinCodeLabel, pinCode,
								new Integer(6)).equalsIgnoreCase("")) {
					// dialogMessage += validation.exactCheck(pinCodeLabel,
					// pinCode,new Integer(6));
					errorList.add(validation.exactCheck(pinCodeLabel, pinCode,
							new Integer(6)));

				}
				
				if (validation.blankCheck(emailLabel, email).equalsIgnoreCase(
						"")
						&& !validation.isValidEmail(emailLabel, email)
								.equalsIgnoreCase("")) {
					// dialogMessage += validation.isValidEmail(emailLabel,
					// email);
					errorList.add(validation.isValidEmail(emailLabel, email));
				}
			// Rev 1.2 changes end here	

			if (isTicVisibleDB) {

				if ((gender.equalsIgnoreCase(new Integer(
						Gender.GENDER_UNSPECIFIED).toString()) || gender
						.equalsIgnoreCase("Unspecified"))
						&& isTicMandatoryDB) {
					// dialogMessage +=" Please select Gender \n";
					errorList.add(" Please select Gender");
				}

				// /for pincode

				// Rev 1.2 changes start here
				
				/*if (!validation.blankCheck(pinCodeLabel, pinCode)
						.equalsIgnoreCase("") && isTicMandatoryDB) {
					// dialogMessage += validation.blankCheck(pinCodeLabel,
					// pinCode);
					errorList.add(validation.blankCheck(pinCodeLabel, pinCode));

				} else if (validation.blankCheck(pinCodeLabel, pinCode)
						.equalsIgnoreCase("")
						&& !validation.exactCheck(pinCodeLabel, pinCode,
								new Integer(6)).equalsIgnoreCase("")) {
					// dialogMessage += validation.exactCheck(pinCodeLabel,
					// pinCode,new Integer(6));
					errorList.add(validation.exactCheck(pinCodeLabel, pinCode,
							new Integer(6)));

				} */
				// Rev 1.2 changes end here

				// /for dob

				// Changes start for Rev 1.1 (Cardless Loyalty)
				if (isTicMandatoryDB) {

					if (!validation.blankCheck(birthDateLabel, birthDate)
							.equalsIgnoreCase("")) {
						// dialogMessage += validation.blankCheck(dobLabel,
						// dob);
						errorList.add(validation.blankCheck(birthDateLabel,
								birthDate));

					}

					if (!validation.blankCheck(birthYearLabel, birthYear)
							.equalsIgnoreCase("")) {
						// dialogMessage += validation.blankCheck(dobLabel,
						// dob);
						errorList.add(validation.blankCheck(birthYearLabel,
								birthYear));

					}
				} else {
					if (validation.blankCheck(birthYearLabel, birthYear)
							.equalsIgnoreCase("")) {

						if (!validation.blankCheck(birthDateLabel, birthDate)
								.equalsIgnoreCase("")) {
							// dialogMessage += validation.blankCheck(dobLabel,
							// dob);
							errorList.add(validation.blankCheck(birthDateLabel,
									birthDate));

						}

					}

					if (validation.blankCheck(birthDateLabel, birthDate)
							.equalsIgnoreCase("")) {
						if (!validation.blankCheck(birthYearLabel, birthYear)
								.equalsIgnoreCase("")) {
							// dialogMessage += validation.blankCheck(dobLabel,
							// dob);
							errorList.add(validation.blankCheck(birthYearLabel,
									birthYear));

				}
					}
				}

				if (validation.blankCheck(birthYearLabel, birthYear)
						.equalsIgnoreCase("")
						&& validation.blankCheck(birthDateLabel, birthDate)
								.equalsIgnoreCase("")) {

					dobCombined = birthDate + "/" + birthYear;
					if (!validation.validateDateFuture(dobLabel, dobCombined)
								.equalsIgnoreCase("")) {
						// dialogMessage
						// +=validation.validateDateFuture(dobLabel,
					// dob);
						errorList.add(validation.validateDateFuture(dobLabel,
								dobCombined));
				}
				}
				// Changes ends for Rev 1.1 (Cardless Loyalty)
				
				// Rev 1.2 changes start here
				// /email validation
				/*
				if (validation.blankCheck(emailLabel, email).equalsIgnoreCase(
						"")
						&& !validation.isValidEmail(emailLabel, email)
								.equalsIgnoreCase("")) {
					// dialogMessage += validation.isValidEmail(emailLabel,
					// email);
					errorList.add(validation.isValidEmail(emailLabel, email));
				} */
				// Rev 1.2 changes end here
			
			}

			String[] dialogArgs = new String[7];

			for (int i = 0; i < dialogArgs.length; i++) {
				if (errorList.size() > i) {
					dialogArgs[i] = (String) errorList.get(i);
				} else {
					dialogArgs[i] = "";
				}
			}

			// String[] dialogArgs = new String[] { dialogMessage };

			// For the dialog entry

			if (errorList.size() > 0) {

				DialogBeanModel dialogModel = new DialogBeanModel();
				dialogModel.setResourceID("TicCustomerAddError");
				dialogModel.setType(DialogScreensIfc.ERROR);
				dialogModel.setArgs(dialogArgs);

				dialogModel.setButtonLetter(DialogScreensIfc.BUTTON_OK,
						"Failure");

				ui.showScreen(POSUIManagerIfc.DIALOG_TEMPLATE, dialogModel);
			} else {

				ticcustomer.setTICFirstName(firstName);
				ticcustomer.setTICLastName(lastName);
				ticcustomer.setTICMobileNumber(mobile);
				// Changes start for Rev 1.1 (Cardless Loyalty)
				ticcustomer.setTICbirthdate(dobCombined);
				// Changes ends for Rev 1.1 (Cardless Loyalty)
				ticcustomer.setTICPinNumber(pinCode);
				ticcustomer.setTICGender(gender);
				ticcustomer.setTICEmail(email);
				/* Rev 1.0 start */
				ticcustomer.setCustomerType("T");
				/* Rev 1.0 end */
				cargo.setTICCustomer(ticcustomer);

				bus.mail(new Letter("ProcessRequest"), BusIfc.CURRENT);
			}
		}
	}
}
