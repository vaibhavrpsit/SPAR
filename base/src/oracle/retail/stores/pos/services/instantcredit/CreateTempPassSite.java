/* ===========================================================================
* Copyright (c) 2003, 2014, Oracle and/or its affiliates. All rights reserved.
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/instantcredit/CreateTempPassSite.java /main/22 2014/03/03 12:10:02 asinton Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    asinton   03/03/14 - changed journaling of account number from truncated
 *                         to masked.
 *    asinton   10/10/13 - removed references to social security number and
 *                         replaced with locale agnostic government id
 *    blarsen   08/27/12 - Merge from project Echo (MPOS) into trunk.
 *    cgreene   03/09/12 - add support for journalling queues by current
 *                         register
 *    cgreene   12/05/11 - updated from deprecated packages and used more
 *                         bigdecimal constants
 *    jswan     10/29/11 - Modified to support printing of temp pass when no
 *                         transaction is available.
 *    sgu       08/16/11 - check null for approval status
 *    cgreene   07/26/11 - moved StatusCode to GiftCardIfc
 *    blarsen   06/30/11 - Setting ui's financial network status flag based on
 *                         payment manager response. This will update the
 *                         online/offline indicator on POS UI.
 *    sgu       06/21/11 - handle the case that response status will be null
 *    cgreene   06/15/11 - implement gift card for servebase and training mode
 *    sgu       06/13/11 - set training mode flag
 *    sgu       06/02/11 - use NoMatchCardInquiry
 *    cgreene   05/27/11 - move auth response objects into domain
 *    sgu       05/24/11 - remove custom id from authorize instant credit
 *                         request
 *    sgu       05/20/11 - refactor instant credit inquiry flow
 *    sgu       05/16/11 - move instant credit approval status to its own class
 *    sgu       05/11/11 - define approval status for instant credit as enum
 *                         type
 *    sgu       05/10/11 - convert create temporary pass to use new payment
 *                         manager
 *    cgreene   02/15/11 - move constants into interfaces and refactor
 *    cgreene   05/26/10 - convert to oracle packaging
 *    cgreene   04/26/10 - XbranchMerge cgreene_tech43 from
 *                         st_rgbustores_techissueseatel_generic_branch
 *    cgreene   04/02/10 - remove deprecated LocaleContantsIfc and currencies
 *    abondala  01/03/10 - update header date
 *    kulu      01/26/09 - Guard against null
 *    kulu      01/26/09 - Fix the bug that House Account enroll response data
 *                         don't have padding translation at enroll franking
 *                         slip
 *    kulu      01/22/09 - Switch to use response text instead of use status
 *                         string based on status.
 *    cgreene   11/13/08 - configure print beans into Spring context
 *
 * ===========================================================================
 * $Log:
 *    11   360Commerce 1.10        5/29/2008 4:52:59 PM   Maisa De Camargo CR
 *         31672 - Setting fields required for the Instant Credit/House
 *         Account ISD Msg.
 *    10   360Commerce 1.9         5/7/2008 8:55:11 PM    Alan N. Sinton  CR
 *         30295: Code modified to present  Function Unavailable dialog for
 *         House Account and Instant Credit when configured with ISD.  Code
 *         reviewed by Anda Cadar.
 *    9    360Commerce 1.8         4/27/2008 4:40:12 PM   Alan N. Sinton  CR
 *         30295: Improvements to gift card handling, instant credit and house
 *          account in the ISD interface.  Code was reviewed by Brett Larsen.
 *    8    360Commerce 1.7         3/12/2008 12:34:41 PM  Deepti Sharma
 *         changes to display house account number correctly
 *    7    360Commerce 1.6         12/18/2007 5:47:48 PM  Alan N. Sinton  CR
 *         29661: Changes per code review.
 *    6    360Commerce 1.5         11/27/2007 1:03:33 PM  Alan N. Sinton  CR
 *         29661: Encrypted, masked, and hashed account numbers for House
 *         Account.
 *    5    360Commerce 1.4         7/9/2007 11:22:07 AM   Mathews Kochummen use
 *          locale format time
 *    4    360Commerce 1.3         1/25/2006 4:10:54 PM   Brett J. Larsen merge
 *          7.1.1 changes (aka. 7.0.3 fixes) into 360Commerce view
 *    3    360Commerce 1.2         3/31/2005 4:27:32 PM   Robert Pearse
 *    2    360Commerce 1.1         3/10/2005 10:20:26 AM  Robert Pearse
 *    1    360Commerce 1.0         2/11/2005 12:10:14 PM  Robert Pearse
 *:
 *    4    .v700     1.2.1.0     11/4/2005 11:44:43     Jason L. DeLeau 4202:
 *         Fix extensibility issues for instant credit service
 *    3    360Commerce1.2         3/31/2005 15:27:32     Robert Pearse
 *    2    360Commerce1.1         3/10/2005 10:20:26     Robert Pearse
 *    1    360Commerce1.0         2/11/2005 12:10:14     Robert Pearse
 *
 *   Revision 1.7  2004/07/14 17:29:12  aachinfiev
 *   @scr 5956 - TempShoppingPassFooter now uses phone # specified by
 *                        CallCenterNumber parameter instead of 1-800-xxx-xxxx
 *
 *   Revision 1.6  2004/05/20 22:54:58  cdb
 *   @scr 4204 Removed tabs from code base again.
 *
 *   Revision 1.5  2004/05/13 13:13:08  aachinfiev
 *   Fixed defect 4470. CreateTempPass had RetryCancel screen instead RetryContinue.
 *
 *   Revision 1.4  2004/03/03 23:15:08  bwf
 *   @scr 0 Fixed CommonLetterIfc deprecations.
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
 *    Rev 1.9   Jan 15 2004 15:46:36   nrao
 * Formatted transaction information and repositioned it to appear below the header.
 *
 *    Rev 1.8   Jan 12 2004 18:11:44   nrao
 * Rework for Temporary Shopping Pass. Added header to the pass.
 *
 *    Rev 1.7   Jan 09 2004 17:02:04   nrao
 * Formatting the journal entries.
 *
 *    Rev 1.6   Dec 04 2003 17:29:54   nrao
 * Code Review Changes.
 *
 *    Rev 1.5   Dec 03 2003 17:21:12   nrao
 * Changed dialog message from RetryContinue to RetryCancel.
 *
 *    Rev 1.4   Dec 02 2003 18:08:06   nrao
 * Corrected journal entry.
 *
 *    Rev 1.3   Nov 24 2003 19:15:44   nrao
 * Using UIUtilities.
 *
 *    Rev 1.2   Nov 21 2003 11:35:32   nrao
 * Changed card number size.
 *
 *    Rev 1.1   Nov 20 2003 16:15:06   nrao
 * Added journaling and removed "Authorizing ..." message for Instant Credit One Day Temp. Shopping Pass.
 *
 * ===========================================================================
 */
package oracle.retail.stores.pos.services.instantcredit;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;
import java.util.StringTokenizer;

import oracle.retail.stores.common.utility.LocaleMap;
import oracle.retail.stores.common.utility.Util;
import oracle.retail.stores.domain.DomainGateway;
import oracle.retail.stores.domain.manager.ifc.PaymentManagerIfc;
import oracle.retail.stores.domain.manager.payment.AuthorizeInstantCreditRequestIfc;
import oracle.retail.stores.domain.manager.payment.AuthorizeInstantCreditResponseIfc;
import oracle.retail.stores.domain.manager.payment.AuthorizeRequestIfc;
import oracle.retail.stores.domain.manager.payment.PaymentServiceResponseIfc.ResponseCode;
import oracle.retail.stores.domain.transaction.TransactionIDIfc;
import oracle.retail.stores.domain.transaction.TransactionIfc;
import oracle.retail.stores.domain.utility.EYSDate;
import oracle.retail.stores.domain.utility.EYSTime;
import oracle.retail.stores.domain.utility.InstantCreditApprovalStatus;
import oracle.retail.stores.domain.utility.InstantCreditIfc;
import oracle.retail.stores.domain.utility.LocaleConstantsIfc;
import oracle.retail.stores.foundation.manager.ifc.JournalManagerIfc;
import oracle.retail.stores.foundation.manager.ifc.ParameterManagerIfc;
import oracle.retail.stores.foundation.manager.ifc.UIManagerIfc;
import oracle.retail.stores.foundation.manager.parameter.ParameterException;
import oracle.retail.stores.foundation.tour.application.Letter;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.foundation.tour.service.SessionBusIfc;
import oracle.retail.stores.keystoreencryption.EncryptionServiceException;
import oracle.retail.stores.pos.config.bundles.BundleConstantsIfc;
import oracle.retail.stores.pos.manager.ifc.UtilityManagerIfc;
import oracle.retail.stores.pos.manager.utility.UtilityManager;
import oracle.retail.stores.pos.receipt.PrintableDocumentException;
import oracle.retail.stores.pos.receipt.PrintableDocumentManagerIfc;
import oracle.retail.stores.pos.receipt.TempShoppingPass;
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
 * 
 */
public class CreateTempPassSite extends PosSiteActionAdapter
{
    /** Serial Version UID */
    private static final long serialVersionUID = -2557773472532781433L;



    /**
     * Size of the string that will be passed to the slip printer
     * @deprecated as of 13.1 in favor of BPT framework
     */
    protected static int ENDORSEMENT_SIZE = 328;

    /**
     * printingSpec
     * @deprecated as of 13.1 in favor of BPT framework
     */
    public static final String PRINTING_SPEC = "printingSpec";

    /**
     * transaction label property name
     * @deprecated as of 13.1 in favor of BPT framework
     */
    public static final String TRANSACTION_LABEL_PROP = "TransLabel";

    /**
     * transaction label default value
     * @deprecated as of 13.1 in favor of BPT framework
     */
    public static final String TRANSACTION_LABEL_DEFAULT = "Trans.:";

    /**
     * store label property name
     * @deprecated as of 13.1 in favor of BPT framework
     */
    public static final String STORE_LABEL_PROP = "StoreLabel";

    /**
     * store label default value
     * @deprecated as of 13.1 in favor of BPT framework
     */
    public static final String STORE_LABEL_DEFAULT = "Store:";

    /**
     * register label property name
     * @deprecated as of 13.1 in favor of BPT framework
     */
    public static final String REGISTER_LABEL_PROP = "RegisterLabel";

    /**
     * register label default value
     * @deprecated as of 13.1 in favor of BPT framework
     */
    public static final String REGISTER_LABEL_DEFAULT = "Reg.: ";

    /**
     * till label property name
     * @deprecated as of 13.1 in favor of BPT framework
     */
    public static final String TILL_LABEL_PROP = "TillLabel";

    /**
     * till label default value
     * @deprecated as of 13.1 in favor of BPT framework
     */
    public static final String TILL_LABEL_DEFAULT = "Till: ";

    /**
     * cashier label property name
     * @deprecated as of 13.1 in favor of BPT framework
     */
    public static final String CASHIER_LABEL_PROP = "CashierLabel";

    /**
     * cashier label default value
     * @deprecated as of 13.1 in favor of BPT framework
     */
    public static final String CASHIER_LABEL_DEFAULT = "Cashier: ";

    /**
     * sales associate label property name
     * @deprecated as of 13.1 in favor of BPT framework
     */
    public static final String SALES_ASSOCIATE_LABEL_PROP = "SalesAssociateLabel";

    /**
     * sales associate label default value
     * @deprecated as of 13.1 in favor of BPT framework
     */
    public static final String SALES_ASSOCIATE_LABEL_DEFAULT = "Sales: ";

    /**
     * single space default text
     * @deprecated as of 13.1 in favor of BPT framework
     */
    public static final String SINGLE_SPACE_TEXT = " ";

    /**
     * printer line size
     * @deprecated as of 13.1 in favor of BPT framework
     */
    public static final int PRINTER_LINE_SIZE = 40;

    /**
     * Constant for HOUSE_ACCOUNT_INQUIRY_FUNCTION_UNAVAILABLE
     */
    public static final String HOUSE_ACCOUNT_INQUIRY_FUNCTION_UNAVAILABLE = "HouseAccountInquiryFunctionUnavailable";

    /**
     * @param bus Service Bus
     */
    @Override
    public void arrive(BusIfc bus)
    {
        POSUIManagerIfc ui = (POSUIManagerIfc)bus.getManager(UIManagerIfc.TYPE);
        ParameterManagerIfc pm = (ParameterManagerIfc)bus.getManager(ParameterManagerIfc.TYPE);
        InstantCreditCargo cargo = (InstantCreditCargo)bus.getCargo();
        UtilityManager util = (UtilityManager)bus.getManager(UtilityManagerIfc.TYPE);
        PaymentManagerIfc paymentMgr = (PaymentManagerIfc)bus.getManager(PaymentManagerIfc.TYPE);

        boolean deviceError = false;
        Letter letter = new Letter(CommonLetterIfc.NEXT);

        // Create Request to send to Authorizer
        AuthorizeInstantCreditRequestIfc request = buildRequest(cargo);
        AuthorizeInstantCreditResponseIfc response = (AuthorizeInstantCreditResponseIfc)paymentMgr.authorize(request);

        InstantCreditApprovalStatus status = response.getApprovalStatus();
        cargo.setApprovalStatus(status);

        UIUtilities.setFinancialNetworkUIStatus(response, (POSUIManagerIfc) bus.getManager(UIManagerIfc.TYPE));

        if (InstantCreditApprovalStatus.APPROVED.equals(status))
        {
            // ssn found and printing temp. shopping pass
            // prints one day temp pass
            try
            {
                InstantCreditIfc card = ValidationUtility.createInstantCredit(response, null, cargo.getGovernmentId(), null);
                cargo.setInstantCredit(card);

                // write Journal entry
                writeJournal(bus, cargo, util);

                // check temp shopping pass parameter
                int validFor = pm.getIntegerValue("TempShoppingPassIssueExp").intValue();
                EYSDate expDate = DomainGateway.getFactory().getEYSDateInstance();
                expDate.add(Calendar.DAY_OF_YEAR, (validFor - 1));

                // print pass
                boolean trainingMode = cargo.getRegister().getWorkstation().isTrainingMode();
                TransactionIfc transaction = cargo.getTransaction();
                if (transaction == null)
                {
                    // Create a dummy transaction to carry data needed to print the temp pass.
                    transaction = DomainGateway.getFactory().getInstantCreditTransactionInstance();
                    transaction.setCashier(cargo.getOperator());
                    transaction.setTillID(cargo.getRegister().getCurrentTillID());
                    TransactionIDIfc transID = DomainGateway.getFactory().getTransactionIDInstance();
                    transID.setWorkstationID(cargo.getRegister().getWorkstation().getWorkstationID());
                    transID.setStoreID(cargo.getStoreStatus().getStore().getStoreID());
                    transaction.initialize(transID);
                }
                
                TempShoppingPass pass = new TempShoppingPass(cargo.getInstantCredit(), transaction,
                        expDate, trainingMode, true);
                PrintableDocumentManagerIfc pdm = (PrintableDocumentManagerIfc)bus
                .getManager(PrintableDocumentManagerIfc.TYPE);
                pdm.printReceipt((SessionBusIfc)bus, pass);
            }
            catch (EncryptionServiceException ese)
            {
                logger.error("Could not encrypt house account number", ese);
            }
            catch (ParameterException pe)
            {
                logger.error(pe.getStackTraceAsString());
            }
            catch (PrintableDocumentException e)
            {
                deviceError = true;
                logger.error("unable to print temp shopping pass", e);
            }
            catch (Exception ex)
            {
                logger.error(ex.getStackTrace());
            }

            if (deviceError)
            {
                UtilityManagerIfc utility = (UtilityManagerIfc)bus.getManager(UtilityManagerIfc.TYPE);
                String argText = utility.retrieveDialogText("RetryContinue.PrinterOffline", "Printer is offline.");
                String args2[] = { argText };
                UIUtilities.setDialogModel(ui, DialogScreensIfc.RETRY_CONTINUE, "RetryContinue", args2);
            }
            else
            {
                bus.mail(letter, BusIfc.CURRENT);
            }
        }
        // ssn not found
        else if (InstantCreditApprovalStatus.REFERENCE_NOT_FOUND.equals(status))
        {
            UIUtilities.setDialogModel(ui, DialogScreensIfc.CONFIRMATION, "NoMatchCardInquiry", null);
        }
        // default
        else
        {
            if (ResponseCode.RequestNotSupported == response.getResponseCode())
            {
                // show the account not found acknowledgement screen
                DialogBeanModel model = new DialogBeanModel();
                model.setResourceID(HOUSE_ACCOUNT_INQUIRY_FUNCTION_UNAVAILABLE);
                model.setType(DialogScreensIfc.ERROR);
                model.setButtonLetter(DialogScreensIfc.BUTTON_OK, CommonLetterIfc.FAILURE);
                ui.showScreen(POSUIManagerIfc.DIALOG_TEMPLATE, model);
            }
            else
            {
                String[] args = new String[1];
                InstantCreditApprovalStatus approvalStatus = cargo.getApprovalStatus();
                if (approvalStatus != null)
                {
                    args[0] = util.retrieveCommonText(approvalStatus.getResourceKey());
                }
                UIUtilities.setDialogModel(ui, DialogScreensIfc.ACKNOWLEDGEMENT, "InquiryOffline", args);
            }
        }
    }

    /**
     * Builds request to be sent to the authorizer
     *
     * @param InstantCreditCargo cargo
     * @param int timeout
     * @return InstantCreditAuthRequest req
     */
    protected AuthorizeInstantCreditRequestIfc buildRequest(InstantCreditCargo cargo)
    {
        AuthorizeInstantCreditRequestIfc req = DomainGateway.getFactory().getAuthorizeInstantCreditRequestInstance();

        req.setGovernmentId(cargo.getGovernmentId());
        req.setZipCode(cargo.getZipCode());
        req.setHomePhone(cargo.getHomePhone());

        if (cargo.getOperator() != null)
        {
            req.setEmployeeID(cargo.getOperator().getEmployeeID());
        }

        // set store ID and register ID
        req.setWorkstation(cargo.getRegister().getWorkstation());

        // set the request sub type
        req.setRequestType(AuthorizeRequestIfc.RequestType.InstantCreditInquiry);

        return req;
    }

    /**
     * Writes the journal entry
     *
     * @param cargo InstantCreditCargo
     */
    protected void writeJournal(BusIfc bus, InstantCreditCargo cargo, UtilityManager utility)
    {
        JournalManagerIfc journal = (JournalManagerIfc)bus.getManager(JournalManagerIfc.TYPE);

        if (journal != null)
        {
            StringBuilder sb = new StringBuilder();
            sb.append(Util.EOL
                    + I18NHelper.getString(I18NConstantsIfc.EJOURNAL_TYPE,
                            JournalConstantsIfc.HOUSE_ACCOUNT_TEMP_PASS_LABEL,
                            null) + Util.EOL);
            Object[] dataArgs = new Object[2];
            dataArgs[0] = cargo.getOperator().getLoginID();
            sb.append(I18NHelper.getString(I18NConstantsIfc.EJOURNAL_TYPE,
                    JournalConstantsIfc.CASHIER_ID_LABEL, dataArgs));
            sb.append(Util.EOL);
            dataArgs[0] = cargo.getInstantCredit().getEncipheredCardData()
                    .getMaskedAcctNumber();
            sb.append(I18NHelper.getString(I18NConstantsIfc.EJOURNAL_TYPE,
                    JournalConstantsIfc.ACCOUNT_NUMBER_LABEL, dataArgs));
            journal.journal(sb.toString());
        }
    }

    /**
     * Prints date and time in block format (date on left side, and time on
     * right side separated by spaces).
     *
     * @param date EYSDate to print
     * @param int stringLength length of the overall string
     * @param locale Locale for formatting
     * @deprecated as of 13.1 in favor of BPT framework
     */
    protected String blockFormatDate(EYSDate date, int stringLength, Locale locale)
    {
        // Format the date
        String dateString = date.toFormattedString(SimpleDateFormat.SHORT, locale);

        // Format the time
        EYSTime time = new EYSTime(date);
        String timeString = time.toFormattedTimeString(SimpleDateFormat.SHORT, locale);

        // Need to block print.
        // Put spaces between the date and time.
        StringBuilder blockDateStr = new StringBuilder(stringLength);
        blockDateStr.append(dateString);
        int dateStringLength = dateString.length();
        int numSpaces = stringLength - (dateStringLength + timeString.length());
        for (int x = 0; x < numSpaces; x++)
            blockDateStr.append(SINGLE_SPACE_TEXT);
        blockDateStr.append(timeString);

        // Return the string.
        return blockDateStr.toString();
    }

    /**
     * Builds the transaction/store line for standard header for franking
     *
     * @param StringBuilder header
     * @param String transactionNumberStr
     * @param String storeIDStr
     * @param UtilityManagerIfc utility
     * @return StringBuilder header
     * @deprecated as of 13.1 in favor of BPT framework
     */
    protected StringBuilder buildTransactionStoreHeader(StringBuilder header, String transactionNumberStr,
            String storeIDStr, UtilityManagerIfc utility)
    {
        String transTxt = utility.retrieveText(PRINTING_SPEC, BundleConstantsIfc.PRINTING_BUNDLE_NAME,
                TRANSACTION_LABEL_PROP, TRANSACTION_LABEL_DEFAULT);
        String storeTxt = utility.retrieveText(PRINTING_SPEC, BundleConstantsIfc.PRINTING_BUNDLE_NAME,
                STORE_LABEL_PROP, STORE_LABEL_DEFAULT);

        header.append(transTxt);
        header.append(transactionNumberStr);
        pad(header,
                (PRINTER_LINE_SIZE - transTxt.length() - storeTxt.length() - transactionNumberStr.length() - storeIDStr
                        .length()));

        header.append(storeTxt);
        header.append(storeIDStr);
        header.append("\n");

        return header;
    }

    /**
     * Builds the register/till/cashier/sales associate line for standard header
     * for franking
     * <P>
     *
     * @param StringBuilder header
     * @param String workstationIDStr
     * @param String tillIDStr
     * @param String cashierIDstr
     * @param String salesAssociateIDStr
     * @param UtilityManagerIfc utility
     * @return StringBuilder header
     * @deprecated as of 13.1 in favor of BPT framework
     */
    protected StringBuilder buildRegisterTillCashierSalesAssociateHeader(StringBuilder header, String workstationIDStr,
            String tillIDStr, String cashierIDstr, String salesAssociateIDStr, UtilityManagerIfc utility)
    {
        String registerTxt = utility.retrieveText(PRINTING_SPEC, BundleConstantsIfc.PRINTING_BUNDLE_NAME,
                REGISTER_LABEL_PROP, REGISTER_LABEL_DEFAULT);
        String tillTxt = utility.retrieveText(PRINTING_SPEC, BundleConstantsIfc.PRINTING_BUNDLE_NAME, TILL_LABEL_PROP,
                TILL_LABEL_DEFAULT);
        String cashierTxt = utility.retrieveText(PRINTING_SPEC, BundleConstantsIfc.PRINTING_BUNDLE_NAME,
                CASHIER_LABEL_PROP, CASHIER_LABEL_DEFAULT);
        String salesAssociateTxt = utility.retrieveText(PRINTING_SPEC, BundleConstantsIfc.PRINTING_BUNDLE_NAME,
                SALES_ASSOCIATE_LABEL_PROP, SALES_ASSOCIATE_LABEL_DEFAULT);

        header.append(registerTxt);
        header.append(workstationIDStr);
        pad(header,
                (PRINTER_LINE_SIZE - registerTxt.length() - tillTxt.length() - workstationIDStr.length() - tillIDStr
                        .length()));

        header.append(tillTxt);
        header.append(tillIDStr);
        header.append("\n");

        header.append(cashierTxt);
        header.append(cashierIDstr);
        pad(
                header,
                (PRINTER_LINE_SIZE - cashierTxt.length() - salesAssociateTxt.length() - cashierIDstr.length() - salesAssociateIDStr
                        .length()));

        header.append(salesAssociateTxt);
        header.append(salesAssociateIDStr);
        header.append("\n");

        return header;
    }

    /**
     * This method is used to pad the string buffer with a given number of
     * spaces
     *
     * @param buffer StringBuilder
     * @param spaces int
     * @deprecated as of 13.1 in favor of BPT framework
     */
    protected void pad(StringBuilder buffer, int spaces)
    {
        for (int i = 0; i < spaces; i++)
        {
            buffer.append(SINGLE_SPACE_TEXT);
        }
    }

    /**
     * Builds the standard header for franking
     *
     * @param utility UtilityManagerIfc
     * @param cargo InstantCreditCargo
     * @return StringBuilder header
     * @deprecated as of 13.1 in favor of BPT framework
     */
    protected StringBuilder buildHeader(UtilityManagerIfc utility, InstantCreditCargo cargo)
    {
        StringBuilder header = new StringBuilder(ENDORSEMENT_SIZE);
        String transactionNumberStr, storeIDStr, workstationIDStr, tillIDStr, cashierIDstr, salesAssociateIDStr;

        Locale locale = LocaleMap.getLocale(LocaleConstantsIfc.DEFAULT_LOCALE);

        header.append(blockFormatDate(new EYSDate(), PRINTER_LINE_SIZE, locale));
        header.append("\n");

        if (cargo.getTransaction() != null)
        {
            transactionNumberStr = checkNull(SINGLE_SPACE_TEXT
                    + cargo.getTransaction().getFormattedTransactionSequenceNumber());
        }
        else
        {
            transactionNumberStr = "";
        }
        storeIDStr = checkNull(SINGLE_SPACE_TEXT + cargo.getStoreStatus().getStore().getStoreID());
        workstationIDStr = checkNull(SINGLE_SPACE_TEXT + cargo.getRegister().getWorkstation().getWorkstationID());
        tillIDStr = checkNull(SINGLE_SPACE_TEXT + cargo.getRegister().getCurrentTillID());
        cashierIDstr = checkNull(SINGLE_SPACE_TEXT + cargo.getOperator().getEmployeeID());

        if (cargo.getEmployee() != null)
        {
            salesAssociateIDStr = checkNull(SINGLE_SPACE_TEXT + cargo.getEmployee().getEmployeeID());
        }
        else
        {
            salesAssociateIDStr = cashierIDstr;
        }

        buildTransactionStoreHeader(header, transactionNumberStr, storeIDStr, utility);
        buildRegisterTillCashierSalesAssociateHeader(header, workstationIDStr, tillIDStr, cashierIDstr,
                salesAssociateIDStr, utility);

        return header;
    }

    /**
     * This method returns the same String it was passed if that string is non-
     * null or an empty string.
     *
     * @param s String to check
     * @return String safe non-null string
     */
    protected String checkNull(String s)
    {
        if (s == null)
        {
            s = new String("");
        }

        return s;
    }

    /**
     * Builds String array from given String based on delimiters using
     * StringTokenizer
     *
     * @param info String
     * @return transInfo String[]
     * @deprecated as of 13.1 in favor of BPT framework
     */
    protected String[] buildStringArray(String info)
    {
        StringTokenizer st = new StringTokenizer(info, "\n");
        int numTokens = st.countTokens();
        String[] transInfo = new String[numTokens];

        for (int i = 0; i < numTokens; i++)
        {
            transInfo[i] = st.nextToken();
        }
        return transInfo;
    }
}
