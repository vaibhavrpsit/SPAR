/********************************************************************************
 *   
 *	Copyright (c) 2016 - 2017 MAX Hypermarket, Inc    All Rights Reserved.
 *	
 *
 *
 *	Rev 1.1		Dec 13, 2016		Mansi Goel		Changes for Manager Override Issue
 *	Rev	1.0 	Oct 26, 2016		Nadia Arora		Changes for Login FES	
 *
 ********************************************************************************/

package max.retail.stores.pos.services.operatorid;

import java.io.UnsupportedEncodingException;

import max.retail.stores.pos.ui.beans.MAXLoginBeanModel;
import oracle.retail.stores.common.utility.Util;
import oracle.retail.stores.domain.employee.EmployeeIfc;
import oracle.retail.stores.foundation.manager.device.MSRModel;
import oracle.retail.stores.foundation.manager.ifc.KeyStoreEncryptionManagerIfc;
import oracle.retail.stores.foundation.manager.ifc.ParameterManagerIfc;
import oracle.retail.stores.foundation.manager.ifc.UIManagerIfc;
import oracle.retail.stores.foundation.manager.parameter.ParameterException;
import oracle.retail.stores.foundation.tour.application.LaneActionAdapter;
import oracle.retail.stores.foundation.tour.gate.Gateway;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.keystoreencryption.EncryptionServiceException;
import oracle.retail.stores.pos.manager.ifc.UtilityManagerIfc;
import oracle.retail.stores.pos.services.operatorid.OperatorIdCargo;
import oracle.retail.stores.pos.ui.POSUIManagerIfc;
import oracle.retail.stores.pos.ui.beans.PromptAndResponseModel;
import oracle.retail.stores.pos.utility.FingerprintUtility;

import org.apache.commons.codec.binary.Base64;

public class MAXLoginEnteredRoad extends LaneActionAdapter {
	/**
	 * 
	 */
	private static final long serialVersionUID = 3585241529790017176L;

	public void traverse(BusIfc bus) {
		OperatorIdCargo cargo = (OperatorIdCargo) bus.getCargo();
		PromptAndResponseModel pAndRModel = getPromptModel(bus);
		POSUIManagerIfc ui = (POSUIManagerIfc) bus.getManager(UIManagerIfc.TYPE);

		// the following is here only so that testing can be done on
		// swiping of credit cards as employee login cards. this
		// can be removed once we have real employee login cards.
		ParameterManagerIfc pm = (ParameterManagerIfc) bus.getManager(ParameterManagerIfc.TYPE);
		try {

			if (pAndRModel.isSwiped()) {
				if (pm.getStringValue("AutomaticEntryID").equals("User")) {
					String loginID = null;
					UtilityManagerIfc util = (UtilityManagerIfc) bus.getManager(UtilityManagerIfc.TYPE);
					loginID = util.getEmployeeFromModel(pAndRModel);
					cargo.setEmployeeID(loginID);
				} else {
					MSRModel msrModel = pAndRModel.getMSRModel();
					String employeeID = null;
					try {
						KeyStoreEncryptionManagerIfc encryptionManager = (KeyStoreEncryptionManagerIfc) Gateway
								.getDispatcher().getManager(KeyStoreEncryptionManagerIfc.TYPE);
						byte[] eID = encryptionManager.decrypt(Base64.decodeBase64(msrModel.getEncipheredCardData()
								.getEncryptedAcctNumber().getBytes()));
						if (eID.length > 10) {
							byte[] tmpID = new byte[10];
							for (int i = 0; i < tmpID.length; i++) {
								tmpID[i] = eID[i];
							}
							employeeID = new String(tmpID);
							Util.flushByteArray(eID);
							eID = null;
							Util.flushByteArray(tmpID);
							tmpID = null;
						} else {
							employeeID = new String(eID);
						}
						cargo.setEmployeeID(employeeID);
					} catch (EncryptionServiceException ese) {
						logger.error("Could not decrypt employee ID", ese);
					}
				}
			} else {
				String employeeId = "", password = "", scanId = "";
				MAXLoginBeanModel loginModel = (MAXLoginBeanModel) ui.getModel(POSUIManagerIfc.OPERATOR_LOGIN);
				if (cargo.getSecurityOverrideFlag()) {
					if (FingerprintUtility.isFingerprintAllowed(bus)) {
						loginModel = (MAXLoginBeanModel) ui.getModel(POSUIManagerIfc.OVERRIDE_FINGERPRINT_LOGIN_DIALOG);
					} else {
						//Code merging changes start
						//loginModel = (MAXLoginBeanModel)ui.getModel("OVERRIDE_LOGIN_DIALOG");
						loginModel = (MAXLoginBeanModel) ui.getModel(POSUIManagerIfc.OPERATOR_LOGIN);
						//Code merging changes Ends
					}
				}

				scanId = loginModel.getPassword();

				//Code merging changes start patch 6_Scanner_Issue
				   if ((scanId.equalsIgnoreCase("")) || (scanId == null)) {
				          scanId = ui.getInput();
				        }

					//Code merging changes End : 6_Scanner_Issue

				if (scanId.indexOf('_') != -1 && scanId.indexOf('@') != -1) {
					int beginIndex = scanId.indexOf('_');
					int endIndex = scanId.indexOf('@');

					employeeId = scanId.substring(beginIndex + 1, endIndex);
					password = scanId.substring(endIndex + 1);
				}

				cargo.setEmployeeID(employeeId);
				try {
					cargo.setEmployeePasswordBytes(password.getBytes(EmployeeIfc.PASSWORD_CHARSET));
				}

				catch (UnsupportedEncodingException e) {
					logger.error("Unable to use correct password character set", e);
					if (logger.isDebugEnabled())
						logger.debug("Defaulting to system character set: " + ui.getInput());
					cargo.setEmployeePasswordBytes(ui.getInput().getBytes());
				}
				// the following line will probably be only line needed when
				// using real employee login cards
				cargo.setEmployeeID(employeeId);

			}

		} catch (ParameterException pe) {
			System.out.println("*** error getting parameter AutomaticEntryID");
		}
	}

	public String toString() { 
		// result string
		String strResult = new String("Class:  MAXLoginEnteredRoad (Revision " + getRevisionNumber() + ")" + hashCode());
		return (strResult);
	}

	protected PromptAndResponseModel getPromptModel(BusIfc bus) {
		MAXLoginBeanModel model = null;
		POSUIManagerIfc ui = (POSUIManagerIfc) bus.getManager(UIManagerIfc.TYPE);
		OperatorIdCargo cargo = (OperatorIdCargo) bus.getCargo();
		if (cargo.getSecurityOverrideFlag()) {
			if (FingerprintUtility.isFingerprintAllowed(bus)) {
				model = (MAXLoginBeanModel) ui.getModel(POSUIManagerIfc.OVERRIDE_FINGERPRINT_LOGIN_DIALOG);
			} else {
				//Code merging changes start : Patch 8_111Scanner_Issue
				//model = (MAXLoginBeanModel) ui.getModel(POSUIManagerIfc.OVERRIDE_LOGIN_DIALOG);
				model = (MAXLoginBeanModel) ui.getModel(POSUIManagerIfc.OPERATOR_LOGIN);
				//Code merging changes Ends : Patch 8_111Scanner_Issue
			}
		} else {
			model = (MAXLoginBeanModel) ui.getModel(POSUIManagerIfc.OPERATOR_LOGIN);
		}
		return (model != null) ? model.getPromptAndResponseModel() : null;
	}

	public String getRevisionNumber() {
		return (revisionNumber);
	}

	// ----------------------------------------------------------------------
	/**
	 * Main to run a test..
	 * <P>
	 * 
	 * @param args
	 *            Command line parameters
	 **/
	// ----------------------------------------------------------------------
	public static void main(String args[]) { // begin main()
		// instantiate class
		MAXLoginEnteredRoad obj = new MAXLoginEnteredRoad();

		// output toString()
		System.out.println(obj.toString());
	} // end main()
}
