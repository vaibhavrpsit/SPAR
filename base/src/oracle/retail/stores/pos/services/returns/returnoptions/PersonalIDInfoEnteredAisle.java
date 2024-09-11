/* ===========================================================================
* Copyright (c) 2008, 2012, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/returns/returnoptions/PersonalIDInfoEnteredAisle.java /main/16 2012/03/29 15:26:13 vtemker Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    vtemker   03/29/12 - Sensitive data from getDecryptedData() of
 *                         EncipheredData class fetched into byte array and
 *                         later, deleted
 *    cgreene   09/02/11 - refactored method names around enciphered objects
 *    rrkohli   07/19/11 - encryption CR
 *    cgreene   05/26/10 - convert to oracle packaging
 *    abondala  01/03/10 - update header date
 *    mahising  02/17/09 - fixed personal id business customer issue
 *    mdecama   12/03/08 - Setting firstName and lastName
 *    abondala  11/06/08 - updated files related to reason codes
 *    abondala  11/05/08 - updated files related to reason code
 *    mdecama   10/28/08 - I18N - Reason Codes for Customer Types.
 * ===========================================================================

     $Log:
      3    360Commerce 1.2         3/31/2005 4:29:20 PM   Robert Pearse
      2    360Commerce 1.1         3/10/2005 10:24:03 AM  Robert Pearse
      1    360Commerce 1.0         2/11/2005 12:13:02 PM  Robert Pearse
     $
     Revision 1.4  2004/03/03 23:15:11  bwf
     @scr 0 Fixed CommonLetterIfc deprecations.

     Revision 1.3  2004/02/12 16:51:52  mcs
     Forcing head revision

     Revision 1.2  2004/02/11 21:52:25  rhafernik
     @scr 0 Log4J conversion and code cleanup

     Revision 1.1.1.1  2004/02/11 01:04:20  cschellenger
     updating to pvcs 360store-current


 *
 *    Rev 1.0   Aug 29 2003 16:06:18   CSchellenger
 * Initial revision.
 *
 *    Rev 1.3   Jul 10 2003 07:55:52   jgs
 * Add country code information to the DL validation request.
 * Resolution for 3072: Selecting any country other than US/Canada on Check Entry results in Invalid DL appearing
 *
 *    Rev 1.2   Jul 03 2003 10:23:40   jgs
 * Added code to support Driver's licence validation.
 * Resolution for 1874: Add Driver's License Validation to Return Prompt for ID
 *
 *    Rev 1.1   Apr 09 2003 14:44:12   HDyer
 * Cleanup from code review.
 * Resolution for POS SCR-1854: Return Prompt for ID feature for POS 6.0
 *
 *    Rev 1.0   Dec 16 2002 09:48:04   HDyer
 * Initial revision.
 * Resolution for POS-SCR 1854: Return Prompt for ID feature for POS 6.0
* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */

package oracle.retail.stores.pos.services.returns.returnoptions;

import oracle.retail.stores.common.utility.LocalizedCodeIfc;
import oracle.retail.stores.pos.services.common.CommonLetterIfc;
import oracle.retail.stores.domain.DomainGateway;
import oracle.retail.stores.domain.customer.CustomerInfoIfc;
import oracle.retail.stores.domain.employee.RoleFunctionIfc;
import oracle.retail.stores.domain.manager.ifc.ValidationManagerIfc;
import oracle.retail.stores.domain.utility.CodeEntryIfc;
import oracle.retail.stores.domain.utility.CodeListIfc;
import oracle.retail.stores.foundation.manager.ifc.ParameterManagerIfc;
import oracle.retail.stores.foundation.manager.ifc.UIManagerIfc;
import oracle.retail.stores.foundation.manager.parameter.ParameterException;
import oracle.retail.stores.foundation.tour.application.Letter;
import oracle.retail.stores.foundation.tour.conduit.Dispatcher;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.foundation.utility.Util;
import oracle.retail.stores.pos.services.PosLaneActionAdapter;
import oracle.retail.stores.pos.ui.DialogScreensIfc;
import oracle.retail.stores.pos.ui.POSUIManagerIfc;
import oracle.retail.stores.pos.ui.UIUtilities;
import oracle.retail.stores.pos.ui.beans.PersonalIDEntryBeanModel;

/**
 * Takes personal ID information from the form and stores it in the cargo.
 * 
 * @version $Revision: /main/16 $
 */
public class PersonalIDInfoEnteredAisle extends PosLaneActionAdapter
{
    /**
     * SerialVersionUID
     */
    private static final long serialVersionUID = -1160495090778369865L;

    /**
     * Extract personal ID information from the screen and store in cargo.
     * 
     * @param bus the bus traversing this lane
     */
    @Override
    public void traverse(BusIfc bus)
    {
        POSUIManagerIfc ui = (POSUIManagerIfc) bus.getManager(UIManagerIfc.TYPE);
        PersonalIDEntryBeanModel model = (PersonalIDEntryBeanModel) ui.getModel(POSUIManagerIfc.PERSONALID_ENTRY);

        // Get the ID info from the screen and save
        ReturnOptionsCargo cargo = (ReturnOptionsCargo)bus.getCargo();
        CustomerInfoIfc customerInfo = cargo.getCustomerInfo();

        // Create CustomerInfo if necessary
        if (customerInfo == null)
        {
            customerInfo = DomainGateway.getFactory().getCustomerInfoInstance();
        }
        customerInfo.setPersonalIDCountry(model.getCountry());
        customerInfo.setPersonalID(model.getPersonalID());
       	customerInfo.setFirstName(model.getFirstName());
       	customerInfo.setLastName(model.getLastName());
    	customerInfo.setCompanyName(model.getOrgName());

        String idType = model.getSelectedReasonKey();

        CodeListIfc   list = cargo.getIdTypes();

        if (list != null)
        {
            CodeEntryIfc entry = list.findListEntryByCode (idType);
            LocalizedCodeIfc localizedCode = DomainGateway.getFactory().getLocalizedCode();
            localizedCode.setCode(idType);
            localizedCode.setCodeName(entry.getCodeName());
            localizedCode.setText(entry.getLocalizedText());
            customerInfo.setLocalizedPersonalIDType(localizedCode);
        }

        customerInfo.setPersonalIDState(model.getState());
        cargo.setCustomerInfo(customerInfo);

        boolean isValid = isDriversLicenceValid(bus, customerInfo);

        if (isValid)
        {
            cargo.setCustomerInfoCollected(true);
            bus.mail(new Letter(CommonLetterIfc.CONTINUE), BusIfc.CURRENT);
        }
        else
        {
            // Prepare for possible need to override invalid drivers licence number
            // and display invalid confirmation screen.
            cargo.setDrivesLicenceValidationOverride(true);
            cargo.setAccessFunctionID(RoleFunctionIfc.ACCEPT_INVALID_DL_FORMAT);
            UIUtilities.setDialogModel(ui,DialogScreensIfc.CONFIRMATION,"InvalidLicense");
        }
    }

    /**
     * Method validateDriversLicence.
     * 
     * @param bus the Bus
     * @param The type of personal id
     * @param The value of the personal id
     * @param The issuing state for the id
     * @return Letter to mail
     */
    private boolean isDriversLicenceValid(BusIfc bus, CustomerInfoIfc customerInfo)
    {
        boolean isValidationRequired = false;
        boolean isDriversLicence     = false;
        boolean isValid              = true;
        String personalIDType        = customerInfo.getLocalizedPersonalIDType().getCodeName();
        byte[] personalID            = customerInfo.getPersonalID().getDecryptedNumber();
        String state                 = customerInfo.getPersonalIDState();
        String country               = customerInfo.getPersonalIDCountry();

        // Determine if a driver's licence must be validated.  The default is no;
        // this the backward compatability is preserved.
        try
        {
            ParameterManagerIfc pm =
                (ParameterManagerIfc)bus.getManager(ParameterManagerIfc.TYPE);
            if (pm.getStringValue("ValidateDriverLicenseFormat").equalsIgnoreCase("Y"))
            {
                isValidationRequired = true;
            }
        }
        catch (ParameterException e)
        {
            logger.error( "" + Util.throwableToString(e) + "");
        }

        // Determine if the id is a driver's licence.
        if (personalIDType.equals("DriversLicense"))
        {
            isDriversLicence = true;
        }

        // If the id is a driver's licence and validation is required, validate the id.
        if (isValidationRequired && isDriversLicence)
        {
            ValidationManagerIfc validationManager =
                (ValidationManagerIfc)Dispatcher.getDispatcher().
                    getManager(ValidationManagerIfc.DRIVERS_LICENECE_TYPE);

            String maskName = country + ValidationManagerIfc.DL_MASK_NAME_POSTFIX;
            if (!validationManager.validateString(state, personalID, maskName))
            {
                isValid = false;
            }

        }
        
        Util.flushByteArray(personalID);

        return isValid;
    }
}
