/* ===========================================================================
* Copyright (c) 1998, 2011, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/operatorid/CheckLoginSite.java /main/4 2011/02/16 09:13:24 cgreene Exp $
 * ===========================================================================
 * NOTES
 * 
 * This class was copied from CheckEmployeeIDSite which was eliminated in 13.4.
 *
 * MODIFIED    (MM/DD/YY)
 *    cgreene   02/15/11 - move constants into interfaces and refactor
 *    blarsen   02/04/11 - setting the login device on the cargo. This is
 *                         required for proper audit logging.
 *    blarsen   05/17/10 - check login site
 *
 * ===========================================================================
 */
package oracle.retail.stores.pos.services.operatorid;

import oracle.retail.stores.domain.employee.EmployeeIfc;
import oracle.retail.stores.domain.utility.LoginIdDeviceIfc;
import oracle.retail.stores.common.parameter.ParameterConstantsIfc;
import oracle.retail.stores.foundation.manager.ifc.ParameterManagerIfc;
import oracle.retail.stores.foundation.manager.ifc.UIManagerIfc;
import oracle.retail.stores.foundation.manager.parameter.ParameterException;
import oracle.retail.stores.foundation.tour.application.Letter;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.pos.services.PosSiteActionAdapter;
import oracle.retail.stores.pos.services.common.CommonLetterIfc;
import oracle.retail.stores.pos.ui.POSUIManagerIfc;
import oracle.retail.stores.pos.ui.beans.POSBaseBeanModel;
import oracle.retail.stores.pos.ui.beans.PromptAndResponseModel;

/**
 * Test the Employee ID to see if it is in the list and determine the next step
 * to take.
 * 
 * @version $Revision: /main/4 $
 */
public class CheckLoginSite extends PosSiteActionAdapter
{
    private static final long serialVersionUID = -7102350527948162414L;

    public static String revisionNumber = "$Revision: /main/4 $";

    /**
     * Test the Employee ID to see if it is in the list and determine the next
     * step to take.
     * 
     * @param bus Service Bus
     */
    @Override
    public void arrive(BusIfc bus)
    {
        // Get the cargo
        OperatorIdCargo cargo = (OperatorIdCargo) bus.getCargo();
        EmployeeIfc employees[] = cargo.getEmployees();
        String id = cargo.getEmployeeID();
        String letter = "Validate";
        boolean employeeInList = false;
        ParameterManagerIfc pm = (ParameterManagerIfc) bus.getManager(ParameterManagerIfc.TYPE);
        POSUIManagerIfc ui = (POSUIManagerIfc) bus.getManager(UIManagerIfc.TYPE);
        POSBaseBeanModel model = (POSBaseBeanModel) ui.getModel(POSUIManagerIfc.OPERATOR_LOGIN);
        PromptAndResponseModel parModel = model.getPromptAndResponseModel();   
        String passwordParm;
        String idParm;
        
        cargo.setLoginDevice(getLoginDevice(parModel));
        
        if (cargo.isEntryModeAutomatic())
        {
            passwordParm = ParameterConstantsIfc.OPERATORID_AutomaticEntryRequiresPassword;
            idParm = ParameterConstantsIfc.OPERATORID_AutomaticEntryID;
        }
        else
        {
            passwordParm = ParameterConstantsIfc.OPERATORID_ManualEntryRequiresPassword;
            idParm = ParameterConstantsIfc.OPERATORID_ManualEntryID;
        }        

        try
        {
            if (parModel.isFingerprintRead())
            {
                cargo.setIDType(ParameterConstantsIfc.OPERATORID_ManualEntryID_USER); // User is the login id
            }
            else
            {
                cargo.setIDType(pm.getStringValue(idParm));
            }
            
        }
        catch (ParameterException pe)
        {
            logger.error("Error getting parameter: " + idParm, pe);
        }

        // Look for the employee in the list
        if (employees != null)
        {
            for (int i = 0; i < employees.length; i++)
            {
                if ((id.equals(employees[i].getLoginID()) && cargo.getIDType().equals(ParameterConstantsIfc.OPERATORID_ManualEntryID_USER)) ||
                    (id.equals(employees[i].getEmployeeID()) && cargo.getIDType().equals(ParameterConstantsIfc.OPERATORID_ManualEntryID_EMPLOYEE)) ||
                    (parModel.isFingerprintRead() && id.equals(employees[i].getLoginID())))
                {
                    cargo.setSelectedEmployee(employees[i]);
                    // Look for an active employee
                    if (employees[i].getLoginStatus() == EmployeeIfc.LOGIN_STATUS_ACTIVE)
                    {
                        employeeInList = true;
                    }
                    // fall out
                    i = employees.length;
                }
            }
        }

        String fingerprintOption = null;
        try
        {
            fingerprintOption = pm.getStringValue(ParameterConstantsIfc.OPERATORID_FingerprintLoginOptions);
        }
        catch (ParameterException e)
        {
            logger.error("Could not get value for paramter: " + ParameterConstantsIfc.OPERATORID_FingerprintLoginOptions, e);
        }
        
        if (employeeInList && !cargo.getPasswordRequiredWithList())
        {
            letter = CommonLetterIfc.SUCCESS;
        }
        // password not required when fingerprint was read and a fingerprint is part of the login
        else if (parModel.isFingerprintRead() && 
                !ParameterConstantsIfc.OPERATORID_FingerprintLoginOptions_NO_FINGERPRINT.equals(fingerprintOption))
        {
            cargo.setPasswordRequired(false);
        }
        else
        {
        
            try
            {
                // If password is not required based on parameter and it's not an override
                if (!pm.getBooleanValue(passwordParm) && !cargo.getSecurityOverrideFlag())
                {
                    cargo.setPasswordRequired(false);
                }
            }
            catch (ParameterException pe)
            {
                logger.warn("Could not determine whether password is required based on " + passwordParm + ". Defaulting to requiring a password.");
            }
        }

        bus.mail(new Letter(letter), BusIfc.CURRENT);
    }

    /**
     * Determines which login device was used based on the flags in the
     * PromptAndResponseModel.
     * 
     * @param parModel prompt and response model
     * @return the device used to obtain the login id
     */
    protected LoginIdDeviceIfc.LoginIdDevice getLoginDevice(PromptAndResponseModel parModel)
    {
        LoginIdDeviceIfc.LoginIdDevice loginDevice = LoginIdDeviceIfc.LoginIdDevice.UNKNOWN;
        
        if (parModel.isScanned())
        {
            loginDevice = LoginIdDeviceIfc.LoginIdDevice.BARCODE;
        }
        else if (parModel.isSwiped())
        {
            loginDevice = LoginIdDeviceIfc.LoginIdDevice.MAG_STRIPE;
        }
        else if (parModel.isFingerprintRead())
        {
            loginDevice = LoginIdDeviceIfc.LoginIdDevice.FINGERPRINT;
        }
        // assume if the above mechanisms were not used, that the default keyboard was used
        else
        {
            loginDevice = LoginIdDeviceIfc.LoginIdDevice.KEYBOARD;
        }
            
        return loginDevice;
    }

    /**
     * Calls <code>arrive</code>
     * 
     * @param bus Service Bus
     */
    @Override
    public void reset(BusIfc bus)
    {
        arrive(bus);

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