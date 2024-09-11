/* ===========================================================================
* Copyright (c) 2003, 2013, Oracle and/or its affiliates. All rights reserved.
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/instantcredit/enrollment/AuthorizeEnrollmentSite.java /main/20 2013/10/15 14:16:20 asinton Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    asinton   10/10/13 - removed references to social security number and
 *                         replaced with locale agnostic government id
 *    tksharma  12/10/12 - commons-lang update 3.1
 *    blarsen   08/27/12 - Merge from project Echo (MPOS) into trunk.
 *    cgreene   04/03/12 - removed deprecated methods
 *    cgreene   12/05/11 - updated from deprecated packages and used more
 *                         bigdecimal constants
 *    sgu       10/29/11 - put back the call center flwo for enrollment
 *    icole     10/14/11 - remove call referal when server is unavailable
 *    blarsen   07/28/11 - Auth timeout parameters delete in 13.4. These were
 *                         moved into the payment technician layer.
 *    cgreene   07/26/11 - moved StatusCode to GiftCardIfc
 *    blarsen   06/30/11 - Setting ui's financial network status flag based on
 *                         payment manager response. This will update the
 *                         online/offline indicator on POS UI.
 *    sgu       06/21/11 - handle the case that response status will be null
 *    cgreene   06/15/11 - implement gift card for servebase and training mode
 *    sgu       06/13/11 - set training mode flag
 *    cgreene   05/27/11 - move auth response objects into domain
 *    sgu       05/24/11 - remove custom id from authorize instant credit
 *                         request
 *    cgreene   05/20/11 - implemented enums for reponse code and giftcard
 *                         status code
 *    sgu       05/16/11 - move instant credit approval status to its own class
 *    sgu       05/12/11 - refactor instant credit formatters
 *    sgu       05/11/11 - define approval status for instant credit as enum
 *                         type
 *    sgu       05/11/11 - fix instant credit cargo to use the new reponse
 *                         object
 *    sgu       05/05/11 - refactor payment technician commext framework
 *    sgu       05/02/11 - use the new AuthorizeInstantCreditResponseIfc in the
 *                         instant credit cargo
 *    sgu       04/25/11 - check in all
 *    asinton   08/05/10 - Changed which constant is used to indicate how
 *                         credit card data was entered. This makes it
 *                         consistent with the ISDITKResponseFormatter class.
 *    npoola    07/28/10 - forward port to fix the format of the instant credit
 *                         card
 *    cgreene   05/26/10 - convert to oracle packaging
 *    cgreene   04/28/10 - updating deprecated names
 *    abondala  01/03/10 - update header date
 *    kulu      01/26/09 - Fix the bug that House Account enroll response data
 *                         don't have padding translation at enroll franking
 *                         slip
 *    kulu      01/22/09 - Switch to use response text instead of use status
 *                         string based on status.
 *
 * ===========================================================================
 * $Log:
 * 10   360Commerce 1.9         5/29/2008 4:07:26 PM   Deepti Sharma   CR-31672
 *       changes for instant credit enrollment. Code reviewed by Alan Sinton
 * 9    360Commerce 1.8         5/7/2008 8:55:11 PM    Alan N. Sinton  CR
 *      30295: Code modified to present  Function Unavailable dialog for House
 *       Account and Instant Credit when configured with ISD.  Code reviewed
 *      by Anda Cadar.
 * 8    360Commerce 1.7         4/27/2008 4:40:12 PM   Alan N. Sinton  CR
 *      30295: Improvements to gift card handling, instant credit and house
 *      account in the ISD interface.  Code was reviewed by Brett Larsen.
 * 7    360Commerce 1.6         3/12/2008 12:34:41 PM  Deepti Sharma   changes
 *      to display house account number correctly
 * 6    360Commerce 1.5         12/18/2007 5:47:48 PM  Alan N. Sinton  CR
 *      29661: Changes per code review.
 * 5    360Commerce 1.4         6/9/2006 3:22:03 PM    Brett J. Larsen CR 18490
 *       - UDM - instant credit auth code changed to varchar from int
 * 4    360Commerce 1.3         1/25/2006 4:10:49 PM   Brett J. Larsen merge
 *      7.1.1 changes (aka. 7.0.3 fixes) into 360Commerce view
 * 3    360Commerce 1.2         3/31/2005 4:27:15 PM   Robert Pearse
 * 2    360Commerce 1.1         3/10/2005 10:19:46 AM  Robert Pearse
 * 1    360Commerce 1.0         2/11/2005 12:09:33 PM  Robert Pearse
 *:
 * 4    .v700     1.2.1.0     11/4/2005 11:44:43     Jason L. DeLeau 4202: Fix
 *      extensibility issues for instant credit service
 * 3    360Commerce1.2         3/31/2005 15:27:15     Robert Pearse
 * 2    360Commerce1.1         3/10/2005 10:19:46     Robert Pearse
 * 1    360Commerce1.0         2/11/2005 12:09:33     Robert Pearse
 *
 *Revision 1.8  2004/09/23 15:28:48  jdeleau
 *@scr 7166 Make sure that the same error occurs if an attempt to authorize was
 *made and fails, when it retries with new data it should not automatically succeed.
 *
 *Revision 1.7  2004/09/13 21:16:14  bwf
 *@scr 7167 Make sure an sales assoc is attached to the transaction.
 *
 *Revision 1.6  2004/06/03 21:54:56  nrao
 *@scr 3916
 *Added a case for Call Error condition.
 *
 *Revision 1.5  2004/04/05 23:03:00  jdeleau
 *@scr 4218 JavaDoc fixes associated with RegisterReports changes
 *
 *Revision 1.4  2004/04/02 23:07:51  jdeleau
 *@scr 4218 Register Reports - House Account and initial changes to
 *the way SummaryReports are built.
 *
 *Revision 1.3  2004/02/12 16:50:42  mcs
 *Forcing head revision
 *
 *Revision 1.2  2004/02/11 21:51:22  rhafernik
 *@scr 0 Log4J conversion and code cleanup
 *
 *Revision 1.1.1.1  2004/02/11 01:04:16  cschellenger
 *updating to pvcs 360store-current
 *
 *
 *
 *    Rev 1.8   Dec 04 2003 15:08:12   nrao
 * Condensed size of method & sent timeout parameter to the authorizer.
 *
 *    Rev 1.7   Nov 24 2003 19:34:32   nrao
 * Code Review Changes.
 *
 * ===========================================================================
 */
package oracle.retail.stores.pos.services.instantcredit.enrollment;

import java.util.Calendar;

import oracle.retail.stores.common.utility.LocaleMap;
import oracle.retail.stores.domain.DomainGateway;
import oracle.retail.stores.domain.customer.CustomerIfc;
import oracle.retail.stores.domain.employee.EmployeeIfc;
import oracle.retail.stores.domain.manager.ifc.PaymentManagerIfc;
import oracle.retail.stores.domain.manager.payment.AuthorizeInstantCreditRequestIfc;
import oracle.retail.stores.domain.manager.payment.AuthorizeInstantCreditResponseIfc;
import oracle.retail.stores.domain.manager.payment.AuthorizeRequestIfc;
import oracle.retail.stores.domain.manager.payment.PaymentServiceResponseIfc.ResponseCode;
import oracle.retail.stores.domain.transaction.InstantCreditTransactionIfc;
import oracle.retail.stores.domain.utility.AddressConstantsIfc;
import oracle.retail.stores.domain.utility.AddressIfc;
import oracle.retail.stores.domain.utility.EYSDate;
import oracle.retail.stores.domain.utility.InstantCreditApprovalStatus;
import oracle.retail.stores.domain.utility.InstantCreditIfc;
import oracle.retail.stores.domain.utility.PhoneConstantsIfc;
import oracle.retail.stores.domain.utility.PhoneIfc;
import oracle.retail.stores.foundation.manager.ifc.ParameterManagerIfc;
import oracle.retail.stores.foundation.manager.ifc.UIManagerIfc;
import oracle.retail.stores.foundation.manager.parameter.ParameterException;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.keystoreencryption.EncryptionServiceException;
import oracle.retail.stores.pos.config.bundles.BundleConstantsIfc;
import oracle.retail.stores.pos.manager.ifc.UtilityManagerIfc;
import oracle.retail.stores.pos.manager.utility.UtilityManager;
import oracle.retail.stores.pos.services.PosSiteActionAdapter;
import oracle.retail.stores.pos.services.instantcredit.InstantCreditCargo;
import oracle.retail.stores.pos.ui.DialogScreensIfc;
import oracle.retail.stores.pos.ui.POSUIManagerIfc;
import oracle.retail.stores.pos.ui.UIUtilities;
import oracle.retail.stores.pos.ui.beans.DialogBeanModel;
import oracle.retail.stores.pos.ui.beans.POSBaseBeanModel;
import oracle.retail.stores.pos.ui.beans.PromptAndResponseModel;
import oracle.retail.stores.pos.utility.ValidationUtility;

import org.apache.commons.lang3.StringUtils;

/**
 *
 */
public class AuthorizeEnrollmentSite extends PosSiteActionAdapter
{
    /**
     * Serial Version UID
     */
    private static final long serialVersionUID = -6558526664693070702L;


    // static strings for parameters and date format
    /**
     * Format for date of birth
     */
    public static final String DOB_FORMAT = "yyyyMMdd";

    /**
     * processor offline
     */
    public static final String INSTANT_CREDIT_PROCESSOR_OFFLINE = "InstantCreditProcessorOffline";

    /**
     * Error getting instant credit
     */
    public static final String INSTANT_CREDIT_CARD_ERROR = "InstantCreditCardError";

    /**
     * reference instant credit call
     */
    public static final String INSTANT_CREDIT_CALL_REFERENCE = "InstantCreditCallRef";

    /**
     * instant credit not approved
     */
    public static final String INSTANT_CREDIT_NOT_APPROVED = "InstantCreditNotApprovedPrompt";

    /**
     * instant credit approved
     */
    public static final String INSTANT_CREDIT_APPROVED = "InstantCreditApprovedPrompt";

    /**
     * retry/cancel
     */
    public static final String REFERENCE_RETRY_CANCEL = "ReferenceRetryCancel";

    /**
     * retry/cancel not found
     */
    public static final String REFERENCE_RETRY_CANCEL_NOT_FOUND = "ReferenceRetryCancel.NotFound";

    /**
     * temporary shopping pass enrollment
     */
    public static final String TEMPORARY_SHOPPING_PASS_ENROLLMENT = "TempShoppingPassEnrollmentExp";

    /**
     * instant credit authorization
     */
    public static final String INSTANT_CREDIT_AUTHORIZATION = "InstantCreditAuthorizationPrompt";

    // static strings for letters

    /**
     * Frank letter
     */
    public static final String FRANK = "Frank";

    /**
     * Call reference letter
     */
    public static final String CALL_REFERENCE = "CallRef";

    /**
     * Call Error letter
     */
    public static final String CALL_ERROR = "CallErr";

    /**
     * Now letter
     */
    public static final String NOW = "Now";

    /**
     * Later letter
     */
    public static final String LATER = "Later";

    // static strings for error field determination

    /**
     * Zip code
     */
    public static final String ZIP = "ZIP";

    /**
     * Zip code field
     */
    public static final String ZIP_CODE_FIELD = "zipCodeField";

    /**
     * Area code
     */
    public static final String AREA = "AREA";

    /**
     * Home telephone number
     */
    public static final String HOME_TEL_FIELD = "homeTelField";

    /**
     * Government ID number
     */
    public static final String GOVERNMENT_ID = "GOVERNMENT_ID";

    /**
     * Government ID field
     */
    public static final String GOVERNMENT_ID_FIELD = "GovernmentIdField";

    /**
     * first name field
     */
    public static final String FIRST_NAME_FIELD = "firstNameField";

    /**
     * Constant for HOUSE_ACCOUNT_ENROLLMENT_FUNCTION_UNAVAILABLE
     */
    public static final String HOUSE_ACCOUNT_ENROLLMENT_FUNCTION_UNAVAILABLE = "HouseAccountEnrollmentFunctionUnavailable";

    /* (non-Javadoc)
     * @see oracle.retail.stores.foundation.tour.application.SiteActionAdapter#arrive(oracle.retail.stores.foundation.tour.ifc.BusIfc)
     */
    @Override
    public void arrive(BusIfc bus)
    {
        POSUIManagerIfc ui = (POSUIManagerIfc) bus.getManager(UIManagerIfc.TYPE);
        InstantCreditCargo cargo = (InstantCreditCargo)bus.getCargo();
        ParameterManagerIfc pm = (ParameterManagerIfc)bus.getManager(ParameterManagerIfc.TYPE);
        UtilityManager util = (UtilityManager)bus.getManager(UtilityManagerIfc.TYPE);
        PaymentManagerIfc paymentMgr = (PaymentManagerIfc)bus.getManager(PaymentManagerIfc.TYPE);

        boolean approved = true;

        // Create Request to send to Authorizer
        AuthorizeInstantCreditRequestIfc req = buildRequest(cargo, pm);

        // display interim screen while authorization is taking place
        displayAuthorizationScreen(bus);

        // send request to the authorizer
        AuthorizeInstantCreditResponseIfc response = (AuthorizeInstantCreditResponseIfc)paymentMgr.authorize(req);

        // Parse Response & set values
        InstantCreditApprovalStatus status = response.getApprovalStatus();

        UIUtilities.setFinancialNetworkUIStatus(response, (POSUIManagerIfc) bus.getManager(UIManagerIfc.TYPE));

        cargo.setReferenceNumber(response.getApprovalCode());
        cargo.setInstantCreditResponse(response);
        cargo.setApprovalStatus(status);

        String responseDisp = response.getResponseCode().toString();

        InstantCreditIfc card = null;
        setSalesAssociateToInstantCredit(cargo, card);
        try
        {
            card = ValidationUtility.createInstantCredit(response,cargo.getCustomer(), cargo.getGovernmentId(),null);
        }
        catch(EncryptionServiceException ese)
        {
            logger.error("Could not encrypt house account number.", ese);
        }

        if(card != null)
        {
            if (InstantCreditApprovalStatus.TIMEOUT.equals(status) ||
                    InstantCreditApprovalStatus.OFFLINE.equals(status) ||
                    InstantCreditApprovalStatus.INSTANT_CREDIT_PROCESSOR_OFFLINE.equals(status) ||
                    // If the authorizer is offline, saftor returns a call center response with no referrence number.
                    // In this case, we treat it as an offline, not a call center. A valid reference number has
                    // to be present if you call the the customer service.
                    (InstantCreditApprovalStatus.CALL_CENTER.equals(status) && StringUtils.isBlank(cargo.getReferenceNumber())))
            {
                if (ResponseCode.RequestNotSupported == response.getResponseCode())
                {
                    //show the account not found acknowledgement screen
                    DialogBeanModel model = new DialogBeanModel();
                    model.setResourceID(HOUSE_ACCOUNT_ENROLLMENT_FUNCTION_UNAVAILABLE);
                    model.setType(DialogScreensIfc.ERROR);
                    model.setButtonLetter(DialogScreensIfc.BUTTON_OK, "NotSupported");
                    ui.showScreen(POSUIManagerIfc.DIALOG_TEMPLATE, model);
                    approved = false;
                }
                else
                {
                    // Timeout or system offline or instant credit processor offline
                    approved = false;
                    UIUtilities.setDialogModel(ui, DialogScreensIfc.ERROR, INSTANT_CREDIT_PROCESSOR_OFFLINE, null, FRANK);
                }
            }
            else if (InstantCreditApprovalStatus.ENROLL_BY_PHONE.equals(status))
            {
                // Enrollment by phone
                approved = false;
                String args2[] = {responseDisp};
                UIUtilities.setDialogModel(ui, DialogScreensIfc.ERROR, INSTANT_CREDIT_CARD_ERROR, args2, FRANK);
            }
            else if (InstantCreditApprovalStatus.CALL_CENTER.equals(status))
            {
                // Call Reference
                approved = false;
                cargo.setInitialRequest(false);
                String args4[] = {cargo.getReferenceNumber()};
                UIUtilities.setDialogModel(ui, DialogScreensIfc.NOW_LATER, INSTANT_CREDIT_CALL_REFERENCE, args4, CALL_REFERENCE);
            }
            else if (InstantCreditApprovalStatus.CALL_ERROR.equals(status))
            {
                // Call Error
                approved = false;
                String args5[] = {responseDisp};
                UIUtilities.setDialogModel(ui, DialogScreensIfc.ERROR, INSTANT_CREDIT_CARD_ERROR, args5, CALL_ERROR);
            }
            else if (InstantCreditApprovalStatus.DECLINED.equals(status))
            {
                // Declined
                approved = false;
                POSBaseBeanModel model1 = new POSBaseBeanModel();
                PromptAndResponseModel pModel1 = new PromptAndResponseModel();
                String promptText1 = util.retrieveText
                (POSUIManagerIfc.PROMPT_AND_RESPONSE_SPEC,
                        BundleConstantsIfc.COMMON_BUNDLE_NAME,
                        INSTANT_CREDIT_NOT_APPROVED,
                        "The enrollment application was Not Approved. Press Next to continue.");
                pModel1.setPromptText(promptText1);
                model1.setPromptAndResponseModel(pModel1);
                ui.showScreen(POSUIManagerIfc.ENROLL_RESPONSE, model1);
            }
            else if (InstantCreditApprovalStatus.APPROVED.equals(status))
            {
                // approved
                POSBaseBeanModel model = new POSBaseBeanModel();
                PromptAndResponseModel pModel = new PromptAndResponseModel();
                String promptText = util.retrieveText
                (POSUIManagerIfc.PROMPT_AND_RESPONSE_SPEC,
                        BundleConstantsIfc.COMMON_BUNDLE_NAME,
                        INSTANT_CREDIT_APPROVED,
                        "The enrollment application was Approved. Press Next to continue.");
                pModel.setPromptText(promptText);
                model.setPromptAndResponseModel(pModel);
                ui.showScreen(POSUIManagerIfc.ENROLL_RESPONSE, model);
            }
            else
            {
                // should not get here.
                approved = false;
                UIUtilities.setDialogModel(ui, DialogScreensIfc.ERROR, INSTANT_CREDIT_PROCESSOR_OFFLINE, null, FRANK);
            }

            card.setApprovalStatus(status);
        }
        // set sales associate to instant credit card
        setSalesAssociateToInstantCredit(cargo, card);
        // set card to transaction
        InstantCreditTransactionIfc instantCreditTrans = (InstantCreditTransactionIfc) cargo.getTransaction();
        instantCreditTrans.setInstantCredit(card);

        // set card to cargo
        cargo.setInstantCredit(card);
        cargo.setApproved(approved);
        // In case the auth request failed, this must be set to false so that
        // if an auth request is rebuilt, the correct flags are set. scr 7166.
        cargo.setBuildRequest(false);
    }

    /**
       Builds request to be sent to the authorizer
       @param  cargo InstantCreditCargo
       @param  timeout Timeout
       @param  pm Parameter Manager to get values from
       @return credit authorization request
    */
    protected AuthorizeInstantCreditRequestIfc buildRequest(InstantCreditCargo cargo, ParameterManagerIfc pm)
    {
        CustomerIfc cust = cargo.getCustomer();
        String employeeId = cargo.getEmployeeID();

        AuthorizeInstantCreditRequestIfc req = DomainGateway.getFactory().getAuthorizeInstantCreditRequestInstance();

        // if trying to build request multiple times in a single enrollment, then set flag
        if (cargo.isBuildRequest())
        {
            req.setFlag(true);
        }

        cargo.setBuildRequest(true);

        // populate request
        req.setFirstName(cust.getFirstName());
        req.setMiddleName(cust.getMiddleName());
        req.setLastName(cust.getLastName());
        req.setName(cust.getLastName() + "/" + cust.getFirstName());
        // pull income, app signed and reference number from the customer
        req.setYearlyIncome(cust.getYearlyIncome());
        req.setAppReferenceNumber(cust.getAppReferenceNumber());
        req.setAppSigned(cust.getAppSigned());

        // set store ID and register ID
        req.setWorkstation(cargo.getRegister().getWorkstation());

        // address info
        AddressIfc address = cust.getAddressByType(AddressConstantsIfc.ADDRESS_TYPE_HOME);
        String addressLine1 = address.getLine1();
        String addressLine2 = address.getLine2();
        req.setStreetNumber(addressLine1);
        req.setAddressLine1(addressLine1);
        req.setAddressLine2(addressLine2);
        req.setZipCode(address.getPostalCode());
        req.setExtZipCode(address.getPostalCodeExtension());
        req.setCity(address.getCity());
        req.setState(address.getState());

        // set DOB info
        EYSDate date = DomainGateway.getFactory().getEYSDateInstance();
        date = cust.getBirthdate();
        String dateString = date.toFormattedString(DOB_FORMAT, LocaleMap.getLocale(LocaleMap.DEFAULT));
        req.setDateOfBirth(dateString);

        // Government ID
        req.setGovernmentId(cargo.getGovernmentId());

        // phone info
        PhoneIfc phone = cust.getPhoneByType(PhoneConstantsIfc.PHONE_TYPE_HOME);
        PhoneIfc busPhone = cust.getPhoneByType(PhoneConstantsIfc.PHONE_TYPE_WORK);

        if (phone != null)
        {
            req.setHomePhone(phone.getPhoneNumber());
        }

        if(busPhone != null)
        {
            req.setWorkPhone(busPhone.getPhoneNumber());
        }

        // set card name swiped
        req.setCardName(cargo.getCardName());

        // employee id info
        req.setEmployeeID(employeeId);
        int validFor = 14;
        try
        {
            // get expiration date from parameter and set request
            validFor = pm.getIntegerValue(TEMPORARY_SHOPPING_PASS_ENROLLMENT).intValue();
        }
        catch(ParameterException pe)
        {
            logger.warn( pe.getStackTraceAsString());
        }

        // set card expiry date info
        EYSDate expDate = DomainGateway.getFactory().getEYSDateInstance();
        expDate.add(Calendar.DAY_OF_YEAR, validFor);
        String dateStr = expDate.toFormattedString(DOB_FORMAT);
        req.setExpirationDate(dateStr);

        // set the request sub type
        req.setRequestType(AuthorizeRequestIfc.RequestType.InstantCreditApplication);

        return req;
    }

    /**
     * Displays the "Authorizing.." screen.
     *
     * @param bus BusIfc to retrieve Managers
     */
    void displayAuthorizationScreen(BusIfc bus)
    {
        //get manager for ui and put up "authorizing..." screen
        POSUIManagerIfc ui = (POSUIManagerIfc) bus.getManager(UIManagerIfc.TYPE);
        UtilityManagerIfc utility =
          (UtilityManagerIfc) bus.getManager(UtilityManagerIfc.TYPE);
        PromptAndResponseModel parModel = new PromptAndResponseModel();
        // get text
        String promptText = utility.retrieveText
          (POSUIManagerIfc.PROMPT_AND_RESPONSE_SPEC,
           BundleConstantsIfc.COMMON_BUNDLE_NAME,
            INSTANT_CREDIT_AUTHORIZATION,
           "Please wait:  Authorizing Instant Credit Enrollment ...");
        parModel.setPromptText(promptText);

        POSBaseBeanModel baseModel = new POSBaseBeanModel();
        baseModel.setPromptAndResponseModel(parModel);
        ui.showScreen(POSUIManagerIfc.AUTHORIZATION, baseModel);
    }

    /**
     * Sales Associate Id, if available, is set to the InstantCreditIfc Object
     * @param cargo InstantCreditCargo
     * @param card  InstantCreditIfc
    */
    void setSalesAssociateToInstantCredit(InstantCreditCargo cargo, InstantCreditIfc card)
    {
        if (card != null)
        {
            if(cargo.getEmployeeID() != null)
            {
                EmployeeIfc salesAssoc = DomainGateway.getFactory().getEmployeeInstance();
                salesAssoc.setEmployeeID(cargo.getEmployeeID());
                card.setInstantCreditSalesAssociate(salesAssoc);
            }
            else
            {
                card.setInstantCreditSalesAssociate(cargo.getOperator());
            }
        }
    }
}
