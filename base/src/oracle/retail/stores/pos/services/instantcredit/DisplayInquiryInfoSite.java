/* ===========================================================================
* Copyright (c) 2003, 2014, Oracle and/or its affiliates. All rights reserved.
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/instantcredit/DisplayInquiryInfoSite.java /main/22 2014/03/03 12:10:02 asinton Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    asinton   03/03/14 - changed truncated to masked for journaling the
 *                         account number
 *    asinton   10/10/13 - removed references to social security number and
 *                         replaced with locale agnostic government id
 *    blarsen   08/27/12 - Merge from project Echo (MPOS) into trunk.
 *    cgreene   03/09/12 - add support for journalling queues by current
 *                         register
 *    cgreene   12/05/11 - updated from deprecated packages and used more
 *                         bigdecimal constants
 *    cgreene   08/30/11 - check for RequestNotSupported first
 *    blarsen   06/30/11 - Setting ui's financial network status flag based on
 *                         payment manager response. This will update the
 *                         online/offline indicator on POS UI.
 *    sgu       06/21/11 - handle the case that response status will be null
 *    cgreene   06/15/11 - implement gift card for servebase and training mode
 *    sgu       06/13/11 - set training mode flag
 *    cgreene   05/27/11 - move auth response objects into domain
 *    sgu       05/24/11 - remove custom id from authorize instant credit
 *                         request
 *    sgu       05/20/11 - refactor instant credit inquiry flow
 *    sgu       05/16/11 - move instant credit approval status to its own class
 *    sgu       05/12/11 - refactor instant credit formatters
 *    sgu       05/11/11 - define approval status for instant credit as enum
 *                         type
 *    sgu       05/10/11 - convert instant credit inquiry by SSN to use new
 *                         payment manager
 *    cgreene   02/15/11 - move constants into interfaces and refactor
 *    cgreene   05/26/10 - convert to oracle packaging
 *    cgreene   04/26/10 - XbranchMerge cgreene_tech43 from
 *                         st_rgbustores_techissueseatel_generic_branch
 *    cgreene   04/02/10 - remove deprecated LocaleContantsIfc and currencies
 *    dwfung    02/05/10 - write masked SS# to EJ if search done by SSN
 *    abondala  01/03/10 - update header date
 *    vchengeg  02/20/09 - for I18N of HouseAccount Enquiry journal entry
 *    kulu      01/26/09 - Fix the bug that House Account enroll response data
 *                         don't have padding translation at enroll franking
 *                         slip
 *    kulu      01/22/09 - Switch to use response text instead of use status
 *                         string based on status.
 *
 * ===========================================================================
 * $Log:
 *    13   360Commerce 1.12        5/29/2008 4:52:59 PM   Maisa De Camargo CR
 *         31672 - Setting fields required for the Instant Credit/House
 *         Account ISD Msg.
 *    12   360Commerce 1.11        5/7/2008 8:55:11 PM    Alan N. Sinton  CR
 *         30295: Code modified to present  Function Unavailable dialog for
 *         House Account and Instant Credit when configured with ISD.  Code
 *         reviewed by Anda Cadar.
 *    11   360Commerce 1.10        4/27/2008 4:40:12 PM   Alan N. Sinton  CR
 *         30295: Improvements to gift card handling, instant credit and house
 *          account in the ISD interface.  Code was reviewed by Brett Larsen.
 *    10   360Commerce 1.9         3/12/2008 12:34:41 PM  Deepti Sharma
 *         changes to display house account number correctly
 *    9    360Commerce 1.8         1/17/2008 5:24:06 PM   Alan N. Sinton  CR
 *         29954: Refactor of EncipheredCardData to implement interface and be
 *          instantiated using a factory.
 *    8    360Commerce 1.7         12/27/2007 10:39:29 AM Alan N. Sinton  CR
 *         29677: Check in changes per code review.  Reviews are Michael
 *         Barnett and Tony Zgarba.
 *    7    360Commerce 1.6         12/18/2007 5:47:48 PM  Alan N. Sinton  CR
 *         29661: Changes per code review.
 *    6    360Commerce 1.5         11/29/2007 5:15:58 PM  Alan N. Sinton  CR
 *         29677: Protect user entry fields of PAN data.
 *    5    360Commerce 1.4         11/27/2007 12:32:24 PM Alan N. Sinton  CR
 *         29661: Encrypting, masking and hashing account numbers for House
 *         Account.
 *    4    360Commerce 1.3         1/25/2006 4:10:58 PM   Brett J. Larsen merge
 *          7.1.1 changes (aka. 7.0.3 fixes) into 360Commerce view
 *    3    360Commerce 1.2         3/31/2005 4:27:48 PM   Robert Pearse
 *    2    360Commerce 1.1         3/10/2005 10:21:03 AM  Robert Pearse
 *    1    360Commerce 1.0         2/11/2005 12:10:39 PM  Robert Pearse
 *:
 *    4    .v700     1.2.1.0     11/4/2005 11:44:43     Jason L. DeLeau 4202:
 *         Fix extensibility issues for instant credit service
 *    3    360Commerce1.2         3/31/2005 15:27:48     Robert Pearse
 *    2    360Commerce1.1         3/10/2005 10:21:03     Robert Pearse
 *    1    360Commerce1.0         2/11/2005 12:10:39     Robert Pearse
 *
 *   Revision 1.6  2004/08/02 19:59:54  blj
 *   @scr 6607 - fixed broken site an simulator code.  Updated twiki page with new simulator info.
 *
 *   Revision 1.5  2004/05/20 22:54:58  cdb
 *   @scr 4204 Removed tabs from code base again.
 *
 *   Revision 1.4  2004/05/03 14:47:54  tmorris
 *   @scr 3890 -Ensures if the House Account variable is empty to respond with proper Error screen.
 *
 *   Revision 1.3  2004/02/12 16:50:40  mcs
 *   Forcing head revision
 *
 *   Revision 1.2  2004/02/11 21:51:22  rhafernik
 *   @scr 0 Log4J conversion and code cleanup
 *
 *   Revision 1.1.1.1  2004/02/11 01:04:16  cschellenger
 *   updating to pvcs 360store-current
 *
 *
 *
 *    Rev 1.11   Jan 09 2004 17:03:22   nrao
 * Formatting the journal entries.
 *
 *    Rev 1.10   Dec 19 2003 14:31:16   nrao
 * Fixed potential null pointer exception.
 *
 *    Rev 1.9   Dec 04 2003 17:31:58   nrao
 * Code Review Changes.
 *
 *    Rev 1.8   Dec 03 2003 17:25:50   nrao
 * Moved screen display to new site.
 *
 *    Rev 1.7   Dec 02 2003 18:10:26   nrao
 * Corrected journal entry.
 *
 *    Rev 1.6   Dec 02 2003 17:34:36   nrao
 * Added account number to InstantCreditAuthRequest when it is sent to the authorizer. Added error dialog message.
 *
 *    Rev 1.5   Nov 24 2003 19:16:42   nrao
 * Using UIUtilities.
 *
 *    Rev 1.4   Nov 21 2003 11:46:12   nrao
 * Added ui display for card number.
 *
 *    Rev 1.3   Nov 21 2003 11:36:02   nrao
 * Changed card number size.
 *
 *    Rev 1.2   Nov 20 2003 17:45:50   nrao
 * Populated model with first name, last name and account number.
 *
 *    Rev 1.1   Nov 20 2003 16:12:14   nrao
 * Added journaling and removed "Authorizing ..." message for Inquiry.
 *
 * ===========================================================================
 */
package oracle.retail.stores.pos.services.instantcredit;

import java.util.Locale;

import oracle.retail.stores.common.parameter.ParameterConstantsIfc;
import oracle.retail.stores.common.utility.LocaleMap;
import oracle.retail.stores.common.utility.Util;
import oracle.retail.stores.domain.DomainGateway;
import oracle.retail.stores.domain.manager.ifc.PaymentManagerIfc;
import oracle.retail.stores.domain.manager.payment.AuthorizeInstantCreditRequestIfc;
import oracle.retail.stores.domain.manager.payment.AuthorizeInstantCreditResponseIfc;
import oracle.retail.stores.domain.manager.payment.AuthorizeRequestIfc;
import oracle.retail.stores.domain.manager.payment.AuthorizeResponseIfc;
import oracle.retail.stores.domain.manager.payment.PaymentServiceResponseIfc.ResponseCode;
import oracle.retail.stores.domain.utility.InstantCreditApprovalStatus;
import oracle.retail.stores.domain.utility.InstantCreditIfc;
import oracle.retail.stores.domain.utility.LocaleConstantsIfc;
import oracle.retail.stores.foundation.manager.ifc.JournalManagerIfc;
import oracle.retail.stores.foundation.manager.ifc.UIManagerIfc;
import oracle.retail.stores.foundation.tour.application.Letter;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.keystoreencryption.EncryptionServiceException;
import oracle.retail.stores.pos.manager.ifc.UtilityManagerIfc;
import oracle.retail.stores.pos.manager.utility.UtilityManager;
import oracle.retail.stores.pos.services.PosSiteActionAdapter;
import oracle.retail.stores.pos.services.common.CommonLetterIfc;
import oracle.retail.stores.pos.ui.DialogScreensIfc;
import oracle.retail.stores.pos.ui.POSUIManagerIfc;
import oracle.retail.stores.pos.ui.UIUtilities;
import oracle.retail.stores.pos.ui.beans.DialogBeanModel;
import oracle.retail.stores.pos.utility.ValidationUtility;
import oracle.retail.stores.utility.I18NConstantsIfc;
import oracle.retail.stores.utility.I18NHelper;
import oracle.retail.stores.utility.JournalConstantsIfc;

/**
 * This site sends request to the authorizor for instant credit inquiry

 */
public class DisplayInquiryInfoSite extends PosSiteActionAdapter implements ParameterConstantsIfc
{
    /**
     * Serial Version UID
     */
    private static final long serialVersionUID = -9083427640926904704L;


    /** Constant for HOUSE_ACCOUNT_INQUIRY_FUNCTION_UNAVAILABLE */
    public static final String HOUSE_ACCOUNT_INQUIRY_FUNCTION_UNAVAILABLE = "HouseAccountInquiryFunctionUnavailable";

    public static final String MASKED_GOVERNMENT_ID_NUMBER = "XXX-XX-XXXX";

    /**
     * Locale
     */
    protected static Locale journalLocale = LocaleMap.getLocale(LocaleConstantsIfc.JOURNAL);

    /* (non-Javadoc)
     * @see oracle.retail.stores.foundation.tour.application.SiteActionAdapter#arrive(oracle.retail.stores.foundation.tour.ifc.BusIfc)
     */
    @Override
    public void arrive(BusIfc bus)
    {
        POSUIManagerIfc ui = (POSUIManagerIfc) bus.getManager(UIManagerIfc.TYPE);
        InstantCreditCargo cargo = (InstantCreditCargo) bus.getCargo();
        UtilityManager utility = (UtilityManager) bus.getManager(UtilityManagerIfc.TYPE);
        PaymentManagerIfc paymentMgr = (PaymentManagerIfc)bus.getManager(PaymentManagerIfc.TYPE);

        // Create Request to send to Authorizer
        AuthorizeInstantCreditRequestIfc request = buildRequest(cargo);
        AuthorizeResponseIfc response = paymentMgr.authorize(request);

        UIUtilities.setFinancialNetworkUIStatus(response, (POSUIManagerIfc) bus.getManager(UIManagerIfc.TYPE));

        if(ResponseCode.RequestNotSupported.equals(response.getResponseCode()))
        {
            //show the account not found acknowledgement screen
            DialogBeanModel model = new DialogBeanModel();
            model.setResourceID(HOUSE_ACCOUNT_INQUIRY_FUNCTION_UNAVAILABLE);
            model.setType(DialogScreensIfc.ERROR);
            model.setButtonLetter(DialogScreensIfc.BUTTON_OK, CommonLetterIfc.FAILURE);
            ui.showScreen(POSUIManagerIfc.DIALOG_TEMPLATE, model);
        }
        else 
        {
            InstantCreditApprovalStatus status = ((AuthorizeInstantCreditResponseIfc)response).getApprovalStatus();
            cargo.setApprovalStatus(status);

            if (InstantCreditApprovalStatus.APPROVED.equals(status))
            {
                // ssn/account number found and card information available
                // build Instant Credit Card
                InstantCreditIfc card = null;
                try
                {
                    card = ValidationUtility.createInstantCredit((AuthorizeInstantCreditResponseIfc)response, null, cargo.getGovernmentId(), null);
                }
                catch(EncryptionServiceException ese)
                {
                    logger.error("Could not encrypt house account number.", ese);
                }
                if(card != null)
                {
                    cargo.setInstantCredit(card);
    
                    // write journal entry
                    writeJournal(bus, cargo, utility);
    
                    // mail "Success" letter for displaying the account information screen
                    bus.mail(new Letter(CommonLetterIfc.SUCCESS), BusIfc.CURRENT);
                }
                else
                {
                    UIUtilities.setDialogModel(ui, DialogScreensIfc.ACKNOWLEDGEMENT, "InquiryOffline",
                            null, CommonLetterIfc.NEXT);
                }
            }
            // ssn not found
            else if (InstantCreditApprovalStatus.REFERENCE_NOT_FOUND.equals(status))
            {
                UIUtilities.setDialogModel(ui, DialogScreensIfc.ACKNOWLEDGEMENT, "AccountNotFoundError",
                        null, CommonLetterIfc.NEXT);
            }
            // account number not found
            else if (InstantCreditApprovalStatus.DECLINED.equals(status))
            {
                UIUtilities.setDialogModel(ui, DialogScreensIfc.ACKNOWLEDGEMENT, "AccountNotFoundError",
                        null, CommonLetterIfc.NEXT);
            }
            else
            {
                UIUtilities.setDialogModel(ui, DialogScreensIfc.ACKNOWLEDGEMENT, "InquiryOffline",
                        null, CommonLetterIfc.NEXT);
            }
        }
    }

    /**
     * Builds request to be sent to the authorizer
     * @param  InstantCreditCargo cargo
     * @param  int timeout
     * @return InstantCreditAuthRequest req
     */
    protected AuthorizeInstantCreditRequestIfc buildRequest(InstantCreditCargo cargo)
    {
        AuthorizeInstantCreditRequestIfc req = DomainGateway.getFactory().getAuthorizeInstantCreditRequestInstance();

        // set the request sub type for the inquiry
        req.setRequestType(AuthorizeRequestIfc.RequestType.InstantCreditInquiry);

        // set ssn
        req.setGovernmentId(cargo.getGovernmentId());
        req.setZipCode(cargo.getZipCode());
        req.setHomePhone(cargo.getHomePhone());

        if (cargo.getOperator() != null)
        {
            req.setEmployeeID(cargo.getOperator().getEmployeeID());
        }

        // set store ID and register ID
        req.setWorkstation(cargo.getRegister().getWorkstation());

        return req;
    }

    /**
     * Writes the journal entry
     * @param cargo InstantCreditCargo
     */
    protected void writeJournal(BusIfc bus, InstantCreditCargo cargo, UtilityManager utility)
    {
        JournalManagerIfc journal = (JournalManagerIfc)bus.getManager(JournalManagerIfc.TYPE);

        if (journal != null)
        {
            StringBuffer sb = new StringBuffer();
            Object data[];
            sb.append(Util.EOL);
            sb.append(I18NHelper.getString(I18NConstantsIfc.EJOURNAL_TYPE,
                    JournalConstantsIfc.HOUSE_ACCOUNT_INQUIRY_LABEL, null, journalLocale));

            sb.append(Util.EOL);
            data = new Object[] { cargo.getOperator().getLoginID() };
            sb.append(I18NHelper.getString(I18NConstantsIfc.EJOURNAL_TYPE, JournalConstantsIfc.CASHIER_ID_LABEL, data,
                    journalLocale));

            sb.append(Util.EOL);
            data = new Object[] { cargo.getInstantCredit().getEncipheredCardData().getMaskedAcctNumber() };
            sb.append(I18NHelper.getString(I18NConstantsIfc.EJOURNAL_TYPE, JournalConstantsIfc.ACCOUNT_NUMBER_LABEL,
                    data, journalLocale));

            if (cargo.getZipCode() != null)
            {
                sb.append(Util.EOL);
                data = new Object[] { cargo.getZipCode() };
                sb.append(I18NHelper.getString(I18NConstantsIfc.EJOURNAL_TYPE,
                        JournalConstantsIfc.ZIP_LABEL, data, journalLocale));
            }

            if (cargo.getHomePhone() != null)
            {
                sb.append(Util.EOL);
                data = new Object[] { cargo.getHomePhone() };
                sb.append(I18NHelper.getString(I18NConstantsIfc.EJOURNAL_TYPE,
                        JournalConstantsIfc.PHONE_NUMBER_LABEL, data, journalLocale));
            }

            if (cargo.getGovernmentId() != null)
            {
                sb.append(Util.EOL);
                data = new Object[] { MASKED_GOVERNMENT_ID_NUMBER };
                sb.append(I18NHelper.getString(I18NConstantsIfc.EJOURNAL_TYPE,
                        JournalConstantsIfc.GOVERNMENT_ID_NUMBER_LABEL, data, journalLocale));
            }

            sb.append(Util.EOL);

            journal.journal(sb.toString());
        }
    }
}
