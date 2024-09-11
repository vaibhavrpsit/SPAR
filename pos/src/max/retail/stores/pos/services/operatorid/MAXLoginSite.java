/* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *

  /** * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * ************************
 * Copyright (c) 2016-2017 Max Hypermarket.    All Rights Reserved. 
 * Rev 1.0  26 Oct, 2016              Nadia              MAX-POS-LOGIN-FESV1 0.doc requirement.
 * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * ***************************/
package max.retail.stores.pos.services.operatorid;


import java.io.UnsupportedEncodingException;

import max.retail.stores.pos.ui.beans.MAXLoginBeanModel;
import oracle.retail.stores.commerceservices.security.EmployeeStatusEnum;
import oracle.retail.stores.common.parameter.ParameterConstantsIfc;
import oracle.retail.stores.domain.employee.EmployeeIfc;
import oracle.retail.stores.domain.financial.RegisterIfc;
import oracle.retail.stores.domain.manager.ifc.PaymentManagerIfc;
import oracle.retail.stores.domain.store.WorkstationIfc;
import oracle.retail.stores.foundation.manager.device.DeviceException;
import oracle.retail.stores.foundation.manager.gui.UIException;
import oracle.retail.stores.foundation.manager.gui.UISubsystem;
import oracle.retail.stores.foundation.manager.ifc.UIManagerIfc;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.foundation.tour.service.SessionBusIfc;
import oracle.retail.stores.pos.ado.utility.Utility;
import oracle.retail.stores.pos.ado.utility.tdo.PasswordPolicyTDOIfc;
import oracle.retail.stores.pos.config.bundles.BundleConstantsIfc;
import oracle.retail.stores.pos.device.POSDeviceActions;
import oracle.retail.stores.pos.device.cidscreens.CIDAction;
import oracle.retail.stores.pos.services.PosSiteActionAdapter;
import oracle.retail.stores.pos.services.common.CommonActionsIfc;
import oracle.retail.stores.pos.services.operatorid.OperatorIdCargo;
import oracle.retail.stores.pos.ui.POSUIManagerIfc;
import oracle.retail.stores.pos.ui.UIUtilities;
import oracle.retail.stores.pos.ui.beans.NavigationButtonBeanModel;
import oracle.retail.stores.pos.ui.beans.PromptAndResponseModel;
import oracle.retail.stores.pos.utility.FingerprintUtility;

/**
 * This site displays the LOGIN screen.
 */
public class MAXLoginSite extends PosSiteActionAdapter
{

	private static final long serialVersionUID = -4415995541495667236L;

	public static final String revisionNumber = "$Revision: /main/14 $";

	public static final String CHANGE_PASSWORD_BUTTON = "ChangePassword";
	public static final String SET_FINGERPRINT_BUTTON = "SetFingerprint";

	/**
	 * Displays the LOGIN screen.
	 *
	 * @param bus Service Bus
	 */
	@Override
	public void arrive(BusIfc bus)
	{
		deactivateFingerprintReader(bus);

		resetCargo(bus);

		OperatorIdCargo cargo = (OperatorIdCargo) bus.getCargo();
		showCPOILogo(bus, cargo);

		hideOnscreenKeyboard();

		// build screen model
		MAXLoginBeanModel model = new MAXLoginBeanModel();

		NavigationButtonBeanModel nbbModel = getNavButtons(bus);
		model.setLocalButtonBeanModel(nbbModel);

		NavigationButtonBeanModel globalModel = getGlobalNavigationButtonBeanModel(bus);
		model.setGlobalButtonBeanModel(globalModel);

		PromptAndResponseModel parModel = getPromptAndResponseModel(bus);
		model.setPromptAndResponseModel(parModel);

		showScreen(bus, model);
		
		POSDeviceActions pda = new POSDeviceActions((SessionBusIfc) bus);
        try
        {
        	// Changes starts for cod emerging(depricated class so using 
            //CIDAction showAction = new CIDAction(IngenicoLogo.SCREEN_NAME, CIDAction.SHOW);
        	CIDAction showAction = new CIDAction(CIDAction.CPOI_TWO_BUTTON_SCREEN_NAME, CIDAction.SHOW);
        // Changes ends for code merging
            pda.cidScreenPerformAction(showAction);
        } 
        catch (DeviceException e)
        {
            logger.warn(
                        "Error using customer interface device: " + e.getMessage() + "");
        }
	}

	/**
	 * 
	 */
	protected void hideOnscreenKeyboard()
	{
		UISubsystem uiSubSys = UISubsystem.getInstance();
		try
		{
			uiSubSys.showOnScreenKeyboard(false);
		}
		catch (UIException e)
		{
			logger.error("Unable to hide popup keyboard dialog.", e);
		}
	}

	/**
	 * @param bus
	 * @param cargo
	 */
	protected void showCPOILogo(BusIfc bus, OperatorIdCargo cargo)
	{
		RegisterIfc register = cargo.getRegister();

		//In a manager override scenario the register object is null - besides
		//protecting against npe; cpoi should not be cleared during man override
		if (register != null)
		{
			WorkstationIfc workstation = register.getWorkstation();
			PaymentManagerIfc paymentManager = (PaymentManagerIfc)bus.getManager(PaymentManagerIfc.TYPE);
			paymentManager.clearSwipeAheadData(workstation);
			paymentManager.showLogo(workstation);
		}
	}

	/**
	 * Deactivate the fingerprint reader based the fingerprint login options
	 * parameter.
	 *
	 * @param bus
	 */
	protected void deactivateFingerprintReader(BusIfc bus)
	{
		POSDeviceActions deviceActions = new POSDeviceActions((SessionBusIfc) bus);
		if (!FingerprintUtility.isFingerprintAllowed(bus) && isFingerprintReaderOnline(deviceActions))
		{
			try
			{
				deviceActions.deactivateFingerprintReader();
			}
			catch (DeviceException de)
			{
				logger.error("Device exception deactivating the fingerprintReader " + de);
			}
		}
	}

	/**
	 * Show the screen for accepting user name and password.
	 *
	 * @param bus
	 * @param model
	 */
	protected void showScreen(BusIfc bus, MAXLoginBeanModel model)
	{
		POSUIManagerIfc ui = (POSUIManagerIfc)bus.getManager(UIManagerIfc.TYPE);
		OperatorIdCargo cargo = (OperatorIdCargo) bus.getCargo();
		if (cargo.getSecurityOverrideFlag())
		{
			model.setUnlockContainer(cargo.isUnlockScreenAfterDialog());
			//If fingerprints are allowed then need to remove the next button 
			if (FingerprintUtility.isFingerprintAllowed(bus))
			{
				ui.showDialogAndWait(POSUIManagerIfc.OVERRIDE_FINGERPRINT_LOGIN_DIALOG, model, true);
			}
			else
			{
				//Code merging changes start
			//	ui.showDialogAndWait(POSUIManagerIfc.OVERRIDE_LOGIN_DIALOG, model, true);
				   ui.showScreen("OPERATOR_LOGIN", model);
				//Code merging changes Ends

			}
		}
		else
		{
			ui.showScreen(POSUIManagerIfc.OPERATOR_LOGIN, model);
		}
	}

	/**
	 * Checks and returns if the fingerprint reader is online
	 * 
	 * @param deviceActions
	 * @return boolean isFingerprintReaderOnline
	 */
	protected Boolean isFingerprintReaderOnline(POSDeviceActions deviceActions)
	{
		boolean isFingerprintReaderOnline = false;
		try
		{
			isFingerprintReaderOnline = deviceActions.isFingerprintReaderOnline();
		}
		catch (DeviceException e)
		{
			// implies fingerprint reader is offline
		}
		return isFingerprintReaderOnline;
	}

	/* (non-Javadoc)
	 * @see oracle.retail.stores.foundation.tour.application.SiteActionAdapter#depart(oracle.retail.stores.foundation.tour.ifc.BusIfc)
	 */
	@Override
	@SuppressWarnings("deprecation")
	public void depart(BusIfc bus)
	{
		OperatorIdCargo cargo = (OperatorIdCargo)bus.getCargo();
		POSUIManagerIfc ui = (POSUIManagerIfc)bus.getManager(UIManagerIfc.TYPE);
		MAXLoginBeanModel loginModel = (MAXLoginBeanModel) ui.getModel(POSUIManagerIfc.OPERATOR_LOGIN);
		if (cargo.getSecurityOverrideFlag())
		{
			if (FingerprintUtility.isFingerprintAllowed(bus))
			{
				loginModel = (MAXLoginBeanModel)ui.getModel(POSUIManagerIfc.OVERRIDE_FINGERPRINT_LOGIN_DIALOG);
			}
			else
			{
				
				//Code merging changes start : Patch 8_111Scanner_Issue
			//	loginModel = (MAXLoginBeanModel)ui.getModel(POSUIManagerIfc.OVERRIDE_LOGIN_DIALOG);
				loginModel = (MAXLoginBeanModel)ui.getModel(POSUIManagerIfc.OPERATOR_LOGIN);
					  // ui.showScreen("OPERATOR_LOGIN", model);
					//Code merging changes Ends: Patch 8_111Scanner_Issue
			}

		}
		String scanId = "", employeeId = "", password = "";
		scanId = loginModel.getPassword();
		//Code merging changes start : 6_Scanner_Issue
	    if ((scanId.equalsIgnoreCase("")) || (scanId == null)) {
	        scanId = ui.getInput();
	      }
		//Code merging Changes End : 6_Scanner_Issue
		if(scanId.indexOf('_')!=-1 && scanId.indexOf('@')!=-1 )
		{
			int beginIndex=scanId.indexOf('_');
			int endIndex=scanId.indexOf('@');

			employeeId=scanId.substring(beginIndex+1, endIndex);
			password=scanId.substring(endIndex+1);
		}

		cargo.setEmployeeID(employeeId);
		try
		{
			cargo.setEmployeePasswordBytes(password.getBytes(EmployeeIfc.PASSWORD_CHARSET));
		}

		catch (UnsupportedEncodingException e)
		{
			logger.error("Unable to use correct password character set", e);
			if (logger.isDebugEnabled())
				logger.debug("Defaulting to system character set: " + ui.getInput());
			cargo.setEmployeePasswordBytes(ui.getInput().getBytes());
		}
		cargo.setEmployeeID(employeeId);
        try {
			cargo.setEmployeePasswordBytes(password.getBytes(EmployeeIfc.PASSWORD_CHARSET));
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	protected PromptAndResponseModel getPromptAndResponseModel(BusIfc bus)
	{
		PromptAndResponseModel parModel = new PromptAndResponseModel();

		String fingerprintLoginOption = FingerprintUtility.getFingerprintOption(bus);

		String parText = null;
		if (ParameterConstantsIfc.OPERATORID_FingerprintLoginOptions_NO_FINGERPRINT.equals(fingerprintLoginOption))
		{
			parText = UIUtilities.retrieveText("PromptAndResponsePanelSpec", BundleConstantsIfc.OPERATORID_BUNDLE_NAME, "OperatorLoginPrompt", "OperatorLoginPrompt");
		}
		else if (ParameterConstantsIfc.OPERATORID_FingerprintLoginOptions_ID_AND_FINGERPRINT.equals(fingerprintLoginOption))
		{
			parText = UIUtilities.retrieveText("PromptAndResponsePanelSpec", BundleConstantsIfc.OPERATORID_BUNDLE_NAME, "OperatorLoginPromptWithFinger", "OperatorLoginPromptWithFinger");
		}
		else if (ParameterConstantsIfc.OPERATORID_FingerprintLoginOptions_FINGERPRINT_ONLY.equals(fingerprintLoginOption))
		{
			parText = UIUtilities.retrieveText("PromptAndResponsePanelSpec", BundleConstantsIfc.OPERATORID_BUNDLE_NAME, "OperatorLoginPromptFingerOnly", "OperatorLoginPromptFingerOnly");
		}

		//parModel.setPromptText("Enter a user ID.");

		return parModel;
	}

	/**
	 * Get the Navigation Button Bean Model with the "Next" button enabled
	 * appropriately for biometrics.
	 */
	protected NavigationButtonBeanModel getGlobalNavigationButtonBeanModel(BusIfc bus)
	{
		NavigationButtonBeanModel globalNavModel = new NavigationButtonBeanModel();

		String fingerprintLoginOption = FingerprintUtility.getFingerprintOption(bus);

		if (ParameterConstantsIfc.OPERATORID_FingerprintLoginOptions_NO_FINGERPRINT.equals(fingerprintLoginOption))
		{
			globalNavModel.setButtonEnabled(CommonActionsIfc.NEXT, true);
		}
		else if (ParameterConstantsIfc.OPERATORID_FingerprintLoginOptions_ID_AND_FINGERPRINT.equals(fingerprintLoginOption))
		{
			globalNavModel.setButtonEnabled(CommonActionsIfc.NEXT, false);
		}
		else if (ParameterConstantsIfc.OPERATORID_FingerprintLoginOptions_FINGERPRINT_ONLY.equals(fingerprintLoginOption))
		{
			globalNavModel.setButtonEnabled(CommonActionsIfc.NEXT, false);
		}

		return globalNavModel;
	}


	/**
	 * This resets cargo for settings that will be set again
	 * @param bus reference to bus
	 */
	protected void resetCargo(BusIfc bus)
	{
		OperatorIdCargo cargo = (OperatorIdCargo) bus.getCargo();
		cargo.setLoginValidationChangePassword(false);
		cargo.setLockOut(false);
		cargo.setEmployeeID("");
		cargo.setEmployeePasswordBytes(null);
		cargo.setSelectedEmployee(null);
		cargo.setEvaluateStatusEnum(EmployeeStatusEnum.ACTIVE);
	}

	/**
	 * This checks if Change Password and Set Fingerprint buttons need to be enabled
	 * @param bus reference to bus
	 * @return  NavigationButtonBeanModel navigation bean model
	 */
	protected NavigationButtonBeanModel getNavButtons(BusIfc bus)
	{
		NavigationButtonBeanModel nbbModel = new NavigationButtonBeanModel();
		nbbModel.setButtonEnabled(CHANGE_PASSWORD_BUTTON, isChangePasswordAllowed(bus));
		nbbModel.setButtonEnabled(SET_FINGERPRINT_BUTTON, FingerprintUtility.isFingerprintAllowed(bus));

		return nbbModel;
	}

	/**
	 * Is user allowed to reset the password?
	 *
	 * @param bus
	 * @return
	 */
	protected boolean isChangePasswordAllowed(BusIfc bus)
	{
		boolean enableChangePasswordButton = true;
		PasswordPolicyTDOIfc tdo = getPasswordPolicyTDO();
		boolean employeeComplianceAllowed = tdo.checkEmployeeComplianceEvaluationAllowed(bus);
		if (!employeeComplianceAllowed)
		{
			enableChangePasswordButton = false;
		}
		boolean passwordRequired = tdo.checkPasswordParameter(bus);
		if(!passwordRequired)
		{
			enableChangePasswordButton = false;
		}
		return enableChangePasswordButton;
	}

	/**
	 * Creates Instance of Password Policy TDO.
	 *
	 * @return PasswordPolicyTDOIfc instance of Password Policy TDO
	 */
	private PasswordPolicyTDOIfc getPasswordPolicyTDO()
	{
		return Utility.getUtil().getPasswordPolicyTDO();
	}

	/**
	 * Returns the revision number of the class.
	 *
	 * @return String representation of revision number
	 */
	public String getRevisionNumber()
	{
		// return string
		return (revisionNumber);
	}
}
