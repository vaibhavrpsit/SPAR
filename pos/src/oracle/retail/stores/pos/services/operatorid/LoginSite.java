/* ===========================================================================
* Copyright (c) 1998, 2014, Oracle and/or its affiliates. All rights reserved.
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/operatorid/LoginSite.java /main/14 2014/01/28 11:05:40 vtemker Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    vtemker   01/24/14 - Fortify: Prevent heap inspection of passwords by
 *                         avoiding using Strings
 *    cgreene   11/18/13 - create unlockScreen method for cargo that needs to
 *                         control the ui not to unlock until it is done.
 *    mkutiana  11/04/13 - Refactoring some FP methods to FingerprintUtiltiy
 *                         and modifying UI screen based on FP selections
 *    cgreene   06/04/13 - implement manager override as dialogs
 *    cgreene   03/13/12 - Deprecate pos ADOContext code in favor of foundation
 *                         TourContext class
 *    icole     02/28/12 - XbranchMerge icole_bug-13699752 from
 *                         rgbustores_13.4x_generic_branch
 *    mkutiana  02/23/12 - In manager override scenario required to protect
 *                         against npe
 *    mkutiana  02/22/12 - XbranchMerge
 *                         mkutiana_bug13728958-disable_fpdevice_based_on_parm
 *                         from rgbustores_13.4x_generic_branch
 *    mkutiana  02/21/12 - disable the fingerprintreader (at login) when
 *                         parameter is set to NoFingerprint
 *    icole     02/09/12 - Ensure CPOI is cleared at login site when in
 *                         accountability mode.
 *    yiqzhao   10/07/11 - hide OnScreenKeyboard on loginSite
 *    cgreene   06/07/11 - update to first pass of removing pospal project
 *    mkutiana  02/22/11 - Modified to handle multiple password policies
 *                         (introduction of biometrics)
 *    cgreene   02/15/11 - move constants into interfaces and refactor
 *    blarsen   05/12/10 - login (enter uername and password) site
 *
 * ===========================================================================
 */
package oracle.retail.stores.pos.services.operatorid;


import oracle.retail.stores.commerceservices.security.EmployeeStatusEnum;
import oracle.retail.stores.common.parameter.ParameterConstantsIfc;
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
import oracle.retail.stores.pos.services.PosSiteActionAdapter;
import oracle.retail.stores.pos.services.common.CommonActionsIfc;
import oracle.retail.stores.pos.ui.POSUIManagerIfc;
import oracle.retail.stores.pos.ui.UIUtilities;
import oracle.retail.stores.pos.ui.beans.LoginBeanModel;
import oracle.retail.stores.pos.ui.beans.NavigationButtonBeanModel;
import oracle.retail.stores.pos.ui.beans.PromptAndResponseModel;
import oracle.retail.stores.pos.utility.FingerprintUtility;

/**
 * This site displays the LOGIN screen.
 */
public class LoginSite extends PosSiteActionAdapter
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
        LoginBeanModel model = new LoginBeanModel();

        NavigationButtonBeanModel nbbModel = getNavButtons(bus);
        model.setLocalButtonBeanModel(nbbModel);

        NavigationButtonBeanModel globalModel = getGlobalNavigationButtonBeanModel(bus);
        model.setGlobalButtonBeanModel(globalModel);

        PromptAndResponseModel parModel = getPromptAndResponseModel(bus);
        model.setPromptAndResponseModel(parModel);

        showScreen(bus, model);
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
    protected void showScreen(BusIfc bus, LoginBeanModel model)
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
                ui.showDialogAndWait(POSUIManagerIfc.OVERRIDE_LOGIN_DIALOG, model, true);
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
        LoginBeanModel loginModel = (LoginBeanModel) ui.getModel(POSUIManagerIfc.OPERATOR_LOGIN);
        if (cargo.getSecurityOverrideFlag())
        {
            if (FingerprintUtility.isFingerprintAllowed(bus))
            {
                loginModel = (LoginBeanModel)ui.getModel(POSUIManagerIfc.OVERRIDE_FINGERPRINT_LOGIN_DIALOG);
            }
            else
            {
                loginModel = (LoginBeanModel)ui.getModel(POSUIManagerIfc.OVERRIDE_LOGIN_DIALOG);
            }
            
        }

        cargo.setEmployeeID(loginModel.getLoginID());
        cargo.setEmployeePasswordBytes(loginModel.getPassword());
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

        parModel.setPromptText(parText);

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
