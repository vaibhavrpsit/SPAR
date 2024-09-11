/* ===========================================================================
* Copyright (c) 2003, 2013, Oracle and/or its affiliates. All rights reserved.
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/instantcredit/reference/GetApplicationInfoSite.java /main/14 2013/10/15 14:16:21 asinton Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    asinton   10/10/13 - removed references to social security number and
 *                         replaced with locale agnostic government id
 *    blarsen   06/30/11 - Setting ui's financial network status flag based on
 *                         payment manager response. This will update the
 *                         online/offline indicator on POS UI.
 *    sgu       06/21/11 - handle the case that response status will be null
 *    sgu       06/13/11 - set training mode flag
 *    cgreene   05/27/11 - move auth response objects into domain
 *    sgu       05/20/11 - refactor instant credit inquiry flow
 *    sgu       05/16/11 - move instant credit approval status to its own class
 *    sgu       05/12/11 - refactor instant credit formatters
 *    sgu       05/11/11 - define approval status for instant credit as enum
 *                         type
 *    sgu       05/11/11 - refactor to use new payment manager to do the
 *                         inquiry
 *    cgreene   02/15/11 - move constants into interfaces and refactor
 *    cgreene   05/26/10 - convert to oracle packaging
 *    abondala  01/03/10 - update header date
 *    kulu      01/26/09 - Fix the bug that House Account enroll response data
 *                         don't have padding translation at enroll franking
 *                         slip
 *    kulu      01/22/09 - Switch to use response text instead of use status
 *                         string based on status.
 *
 * ===========================================================================
 * $Log:
 * 5    360Commerce 1.4         5/7/2008 8:55:11 PM    Alan N. Sinton  CR
 *      30295: Code modified to present  Function Unavailable dialog for House
 *       Account and Instant Credit when configured with ISD.  Code reviewed
 *      by Anda Cadar.
 * 4    360Commerce 1.3         1/25/2006 4:11:42 PM   Brett J. Larsen merge
 *      7.1.1 changes (aka. 7.0.3 fixes) into 360Commerce view
 * 3    360Commerce 1.2         3/31/2005 4:29:36 PM   Robert Pearse
 * 2    360Commerce 1.1         3/10/2005 10:24:36 AM  Robert Pearse
 * 1    360Commerce 1.0         2/11/2005 12:13:37 PM  Robert Pearse
 *:
 * 4    .v700     1.2.1.0     11/4/2005 11:44:46     Jason L. DeLeau 4202: Fix
 *      extensibility issues for instant credit service
 * 3    360Commerce1.2         3/31/2005 15:29:36     Robert Pearse
 * 2    360Commerce1.1         3/10/2005 10:24:36     Robert Pearse
 * 1    360Commerce1.0         2/11/2005 12:13:37     Robert Pearse
 *
 *Revision 1.3  2004/02/12 16:50:45  mcs
 *Forcing head revision
 *
 *Revision 1.2  2004/02/11 21:51:22  rhafernik
 *@scr 0 Log4J conversion and code cleanup
 *
 *Revision 1.1.1.1  2004/02/11 01:04:17  cschellenger
 *updating to pvcs 360store-current
 *
 *
 *
 *    Rev 1.6   Jan 27 2004 10:36:08   nrao
 * Undid previous change. During training mode the dummy response is used instead of the authorizer. The dummy response is now fixed to give an approved response.
 *
 *    Rev 1.5   16 Jan 2004 17:19:12   Tim Fritz
 * Reference Number Inquiry now works the same in Training Mode as when it's not in Training Mode.
 *
 *    Rev 1.4   Dec 04 2003 15:06:42   nrao
 * Condensed size of method & sent timeout parameter to the authorizer.
 *
 *    Rev 1.3   Nov 24 2003 19:59:36   nrao
 * Code Review Changes.
 *
 * ===========================================================================
 */
package oracle.retail.stores.pos.services.instantcredit.reference;

import oracle.retail.stores.domain.DomainGateway;
import oracle.retail.stores.domain.manager.ifc.PaymentManagerIfc;
import oracle.retail.stores.domain.manager.payment.AuthorizeInstantCreditRequestIfc;
import oracle.retail.stores.domain.manager.payment.AuthorizeInstantCreditResponseIfc;
import oracle.retail.stores.domain.manager.payment.AuthorizeRequestIfc;
import oracle.retail.stores.domain.manager.payment.PaymentServiceResponseIfc.ResponseCode;
import oracle.retail.stores.domain.utility.InstantCreditApprovalStatus;
import oracle.retail.stores.foundation.manager.ifc.UIManagerIfc;
import oracle.retail.stores.foundation.tour.application.Letter;
import oracle.retail.stores.foundation.tour.application.SiteActionAdapter;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.foundation.tour.ifc.LetterIfc;
import oracle.retail.stores.pos.config.bundles.BundleConstantsIfc;
import oracle.retail.stores.pos.manager.ifc.UtilityManagerIfc;
import oracle.retail.stores.pos.services.common.CommonLetterIfc;
import oracle.retail.stores.pos.services.instantcredit.InstantCreditCargo;
import oracle.retail.stores.pos.ui.DialogScreensIfc;
import oracle.retail.stores.pos.ui.POSUIManagerIfc;
import oracle.retail.stores.pos.ui.UIUtilities;
import oracle.retail.stores.pos.ui.beans.DialogBeanModel;
import oracle.retail.stores.pos.ui.beans.POSBaseBeanModel;
import oracle.retail.stores.pos.ui.beans.PromptAndResponseModel;

/**
 * @version $Revision: /main/14 $
 */
public class GetApplicationInfoSite extends SiteActionAdapter
{
    /**
     * Serial Version UID
     */
    private static final long serialVersionUID = -7584835470704706635L;

    /**
      Constant for HOUSE_ACCOUNT_INQUIRY_FUNCTION_UNAVAILABLE
    **/
    public static final String HOUSE_ACCOUNT_INQUIRY_FUNCTION_UNAVAILABLE = "HouseAccountInquiryFunctionUnavailable";

    /* (non-Javadoc)
     * @see oracle.retail.stores.foundation.tour.application.SiteActionAdapter#arrive(oracle.retail.stores.foundation.tour.ifc.BusIfc)
     */
    @Override
    public void arrive(BusIfc bus)
    {
        InstantCreditCargo cargo = (InstantCreditCargo) bus.getCargo();
        PaymentManagerIfc paymentMgr = (PaymentManagerIfc)bus.getManager(PaymentManagerIfc.TYPE);

        displayAuthorizationScreen(bus);

        // Create Request to send to Authorizer
        AuthorizeInstantCreditRequestIfc request = buildRequest(cargo);
        AuthorizeInstantCreditResponseIfc response = (AuthorizeInstantCreditResponseIfc)paymentMgr.authorize(request);

        UIUtilities.setFinancialNetworkUIStatus(response, (POSUIManagerIfc) bus.getManager(UIManagerIfc.TYPE));

        cargo.setInstantCreditResponse(response);

        //default to call customer service screen
        LetterIfc letter = new Letter("Call");
        InstantCreditApprovalStatus status = response.getApprovalStatus();

        cargo.setApprovalStatus(status);

        boolean mail = true;
        if (InstantCreditApprovalStatus.APPROVED.equals(status))
        {
            letter = new Letter("Approved");
        }
        else if (InstantCreditApprovalStatus.DECLINED.equals(status))
        {
            letter = new Letter("Declined");
        }
        else if (InstantCreditApprovalStatus.REFERENCE_NOT_FOUND.equals(status))
        {
            letter = new Letter("NotFound");
            // Since authorizer is manually programmed to give back hard coded response codes,
            // when an error code is returned, set inputed reference number to "" to prevent it
            // from being franked
            cargo.setReferenceNumber("");
        }
        else
        {
            if(ResponseCode.RequestNotSupported.equals(response.getResponseCode()))
            {
                //show the account not found acknowledgement screen
                DialogBeanModel model = new DialogBeanModel();
                model.setResourceID(HOUSE_ACCOUNT_INQUIRY_FUNCTION_UNAVAILABLE);
                model.setType(DialogScreensIfc.ERROR);
                model.setButtonLetter(DialogScreensIfc.BUTTON_OK, CommonLetterIfc.FAILURE);
                POSUIManagerIfc ui = (POSUIManagerIfc) bus.getManager(UIManagerIfc.TYPE);
                ui.showScreen(POSUIManagerIfc.DIALOG_TEMPLATE, model);
                mail = false;
            }
            else
            {
                // Since authorizer is manually programmed to give back hard coded response codes,
                // when an error code is returned, set inputed reference number to "" to prevent it
                // from being franked
                cargo.setReferenceNumber("");
            }
        }

        if (mail)
        {
            bus.mail(letter, BusIfc.CURRENT);
        }
    }

    /**
     * Builds request to be sent to the authorizer
     * @param  InstantCreditCargo cargo
     * @param  int timeout
     * @return InstantCreditAuthRequest request
     */
    protected AuthorizeInstantCreditRequestIfc buildRequest(InstantCreditCargo cargo)
    {
        AuthorizeInstantCreditRequestIfc request = DomainGateway.getFactory().getAuthorizeInstantCreditRequestInstance();
        request.setAppReferenceNumber(cargo.getReferenceNumber());
        request.setZipCode(cargo.getZipCode());
        request.setHomePhone(cargo.getHomePhone());
        request.setGovernmentId(cargo.getGovernmentId());

        // set store ID and register ID
        request.setWorkstation(cargo.getRegister().getWorkstation());

        // set the request sub type
        request.setRequestType(AuthorizeRequestIfc.RequestType.InstantCreditApplicationInquiry);
        return request;
    }

    /**
     * Displays the "Authorizing..." screen.
     *
     * @param BusIfc to retrieve Managers
     */
    void displayAuthorizationScreen(BusIfc bus)
    {
        // get manager for ui and put up "authorizing..." screen
        POSUIManagerIfc ui = (POSUIManagerIfc)bus.getManager(UIManagerIfc.TYPE);
        UtilityManagerIfc utility = (UtilityManagerIfc)bus.getManager(UtilityManagerIfc.TYPE);

        PromptAndResponseModel parModel = new PromptAndResponseModel();
        // get text
        String promptText = utility.retrieveText(POSUIManagerIfc.PROMPT_AND_RESPONSE_SPEC,
                BundleConstantsIfc.COMMON_BUNDLE_NAME, "InstantCreditAuthorizationPrompt",
                "Please wait:  Authorizing Instant Credit Enrollment ...");
        parModel.setPromptText(promptText);

        POSBaseBeanModel baseModel = new POSBaseBeanModel();
        baseModel.setPromptAndResponseModel(parModel);
        ui.showScreen(POSUIManagerIfc.AUTHORIZATION, baseModel);
    }

}
