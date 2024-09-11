/* ===========================================================================
* Copyright (c) 1998, 2013, Oracle and/or its affiliates. All rights reserved.
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/operatorid/LoginEnteredRoad.java /main/11 2013/11/20 13:04:09 cgreene Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    cgreene   11/20/13 - refactoring to allow for fingerprint mgr override
 *    tksharma  12/10/12 - commons-lang update 3.1
 *    mkutiana  09/12/12 - Modifications to support Biometrics Quickwin -
 *                         support for multiple FP per employee
 *    blarsen   02/28/11 - Changed code to use the new verifyFingerprintMatch
 *                         POSDeviceAction rather than accessing the session
 *                         directly. This is more consistent with other devices
 *                         and avoids saving the session in the model.
 *    hyin      02/18/11 - redo the db call part to improve the performance
 *    cgreene   02/15/11 - move constants into interfaces and refactor
 *    blarsen   02/07/11 - Adding support for login id entry via barcode scan.
 *    blarsen   02/04/11 - a fingerprint reader method was renamed to aid
 *                         clarity.
 *    blarsen   05/17/10 - login entered road
 *
 * ===========================================================================
 */
package oracle.retail.stores.pos.services.operatorid;


import java.util.List;

import oracle.retail.stores.common.parameter.ParameterConstantsIfc;
import oracle.retail.stores.common.utility.Util;
import oracle.retail.stores.domain.DomainGateway;
import oracle.retail.stores.domain.arts.DataTransactionFactory;
import oracle.retail.stores.domain.arts.DataTransactionKeys;
import oracle.retail.stores.domain.arts.EmployeeTransaction;
import oracle.retail.stores.domain.employee.EmployeeIfc;
import oracle.retail.stores.domain.transaction.SearchCriteriaIfc;
import oracle.retail.stores.foundation.manager.data.DataException;
import oracle.retail.stores.foundation.manager.device.DeviceException;
import oracle.retail.stores.foundation.manager.device.FingerprintReaderModel;
import oracle.retail.stores.foundation.manager.device.MSRModel;
import oracle.retail.stores.foundation.manager.ifc.KeyStoreEncryptionManagerIfc;
import oracle.retail.stores.foundation.manager.ifc.ParameterManagerIfc;
import oracle.retail.stores.foundation.manager.ifc.UIManagerIfc;
import oracle.retail.stores.foundation.manager.parameter.ParameterException;
import oracle.retail.stores.foundation.tour.application.LaneActionAdapter;
import oracle.retail.stores.foundation.tour.gate.Gateway;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.foundation.tour.service.SessionBusIfc;
import oracle.retail.stores.keystoreencryption.EncryptionServiceException;
import oracle.retail.stores.pos.device.POSDeviceActions;
import oracle.retail.stores.pos.manager.ifc.UtilityManagerIfc;
import oracle.retail.stores.pos.ui.POSUIManagerIfc;
import oracle.retail.stores.pos.ui.beans.POSBaseBeanModel;
import oracle.retail.stores.pos.ui.beans.PromptAndResponseModel;
import oracle.retail.stores.pos.utility.FingerprintUtility;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.lang3.StringUtils;

/**
 * This road is traveled when the employee ID has been entered. It stores the
 * employee ID in the cargo.
 * 
 * @version $Revision: /main/11 $
 */
public class LoginEnteredRoad extends LaneActionAdapter implements ParameterConstantsIfc
{

    private static final long serialVersionUID = -2058715063092291343L;

    public static String revisionNumber = "$Revision: /main/11 $";

    /**
     * Stores the employee ID in the cargo.
     * 
     * @param bus Service Bus
     */
    @Override
    public void traverse(BusIfc bus)
    {
        OperatorIdCargo cargo = (OperatorIdCargo)bus.getCargo();
        PromptAndResponseModel pAndRModel = getPromptModel(bus);

        try
        {
            if (pAndRModel.isFingerprintRead())
            {
                matchFingerprint(bus, pAndRModel);
            }
            else if (pAndRModel.isScanned())
            {
                cargo.setEmployeeID(pAndRModel.getResponseText());
            }
            else if (pAndRModel.isSwiped()) 
            {
                decodeSwipe(bus, pAndRModel);
            }
        }
        catch (ParameterException pe)
        {
            logger.error("Error getting parameter AutomaticEntryID", pe);
        }
        catch (DeviceException de)
        {
            logger.error("Error getting using device for login,", de);
        }
    }

    /**
     * Decode a swipe into an employee ID.
     *
     * @param bus
     * @param pAndRModel
     */
    protected void decodeSwipe(BusIfc bus, PromptAndResponseModel pAndRModel)
            throws ParameterException
    {
        OperatorIdCargo cargo = (OperatorIdCargo)bus.getCargo();
        ParameterManagerIfc pm = (ParameterManagerIfc)bus.getManager(ParameterManagerIfc.TYPE);
        if (pm.getStringValue("AutomaticEntryID").equals("User"))
        {
            String loginID = null;
            UtilityManagerIfc util = (UtilityManagerIfc)bus.getManager(UtilityManagerIfc.TYPE);
            loginID = util.getEmployeeFromModel(pAndRModel);
            cargo.setEmployeeID(loginID);
        }
        else
        {
            MSRModel msrModel = pAndRModel.getMSRModel();
            String employeeID = null;
            try
            {
                KeyStoreEncryptionManagerIfc encryptionManager = (KeyStoreEncryptionManagerIfc)Gateway.getDispatcher()
                        .getManager(KeyStoreEncryptionManagerIfc.TYPE);
                byte[] eID = encryptionManager.decrypt(Base64.decodeBase64(msrModel.getEncipheredCardData()
                        .getEncryptedAcctNumber().getBytes()));
                // What if operator mistakenly swipes a credit card on this
                // screen. We can only assume that what was swiped is an employee
                // card. Since employee ID are 10 chars or less we can chop off
                // and throw away the excess.
                if (eID.length > 10)
                {
                    // not using System.arraycopy() since this is potentially
                    // sensitive data.
                    byte[] tmpID = new byte[10];
                    for (int i = 0; i < tmpID.length; i++)
                    {
                        tmpID[i] = eID[i];
                    }
                    employeeID = new String(tmpID);
                    // clear eID
                    Util.flushByteArray(eID);
                    eID = null;
                    // clear tmpID
                    Util.flushByteArray(tmpID);
                    tmpID = null;
                }
                else
                {
                    employeeID = new String(eID);
                }
                cargo.setEmployeeID(employeeID);
            }
            catch (EncryptionServiceException ese)
            {
                logger.error("Could not decrypt employee ID", ese);
            }
        }
    }

    /**
     * Find employee that matches a fingerprint.
     *
     * @param bus
     * @param pAndRModel
     * @throws ParameterException
     */
    protected void matchFingerprint(BusIfc bus, PromptAndResponseModel pAndRModel)
            throws ParameterException, DeviceException
    {
        OperatorIdCargo cargo = (OperatorIdCargo)bus.getCargo();

        EmployeeIfc[] possibleMatches = null;
        EmployeeTransaction empTransaction = (EmployeeTransaction) DataTransactionFactory.create(DataTransactionKeys.EMPLOYEE_TRANSACTION);
        SearchCriteriaIfc inquiry = DomainGateway.getFactory().getSearchCriteriaInstance();

        ParameterManagerIfc pm = (ParameterManagerIfc)bus.getManager(ParameterManagerIfc.TYPE);
        String fingerprintOption = pm.getStringValue(OPERATORID_FingerprintLoginOptions);
        boolean isLoginIDRequired = 
                OPERATORID_FingerprintLoginOptions_NO_FINGERPRINT.equals(fingerprintOption) ||
                OPERATORID_FingerprintLoginOptions_ID_AND_FINGERPRINT.equals(fingerprintOption);
        try
        {
            if (StringUtils.isEmpty(cargo.getEmployeeID()) && isLoginIDRequired)
            {
                logger.warn("Login ID required, but no login ID was specified.");
            }
            else if (!StringUtils.isEmpty(cargo.getEmployeeID()))
            {
                EmployeeIfc employee = empTransaction.getEmployee(cargo.getEmployeeID());
                possibleMatches = new EmployeeIfc[] { employee };
            }
            else
            {
                inquiry.setFingerprintFullEmployeeListMode(true);
                possibleMatches = empTransaction.selectEmployees(inquiry);
            }
        }
        catch (DataException e)
        {
            logger.error("Problem retrieving employees for fingerprint comparison.", e);
        }

        FingerprintReaderModel fingerprintModel = pAndRModel.getFingerprintModel();
        if (possibleMatches == null)
        {
            logger.error("No employees to compare fingerprint against.");
        }
        else if (fingerprintModel == null || fingerprintModel.getFingerprintData() == null
                || fingerprintModel.getFingerprintData().length == 0)
        {
            logger.error("No fingerprint to compare against employees.");
        }
        else
        {
            POSDeviceActions pda = new POSDeviceActions((SessionBusIfc) bus);
            cargo.setEmployeeID("");
            EMPLOYEE_LOOP:
            for (EmployeeIfc employee : possibleMatches)
            {
                List<byte[]> enrolledFingerprints = employee.getFingerprintBiometrics();
                byte[] attemptFingerprint = fingerprintModel.getFingerprintData();
                for (byte[] onFileFingerprint : enrolledFingerprints)
                {
                    if (FingerprintUtility.verifyFingerprintMatch(pda, onFileFingerprint, attemptFingerprint))
                    {
                        cargo.setEmployeeID(employee.getLoginID());
                        logger.debug("Fingerprint match found.");
                        break EMPLOYEE_LOOP;
                    }
                }
            }
        }
    }

    /**
     * Get the model from the bus cargo depending on screen options.
     * 
     * @param bus
     * @return
     */
    protected PromptAndResponseModel getPromptModel(BusIfc bus)
    {
        POSBaseBeanModel model = null;
        POSUIManagerIfc ui = (POSUIManagerIfc)bus.getManager(UIManagerIfc.TYPE);
        OperatorIdCargo cargo = (OperatorIdCargo)bus.getCargo();
        if (cargo.getSecurityOverrideFlag())
        {
            if (FingerprintUtility.isFingerprintAllowed(bus))
            {
                model = (POSBaseBeanModel)ui.getModel(POSUIManagerIfc.OVERRIDE_FINGERPRINT_LOGIN_DIALOG);
            }
            else
            {
                model = (POSBaseBeanModel)ui.getModel(POSUIManagerIfc.OVERRIDE_LOGIN_DIALOG);
            }
        }
        else
        {
            model = (POSBaseBeanModel)ui.getModel(POSUIManagerIfc.OPERATOR_LOGIN);
        }
        return (model != null)? model.getPromptAndResponseModel() : null;
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