/* ===========================================================================
* Copyright (c) 2003, 2012, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/instantcredit/enrollment/CompleteEnrollmentSite.java /main/20 2012/09/12 11:57:22 blarsen Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    blarsen   08/28/12 - Merge project Echo (MPOS) into trunk.
 *    cgreene   12/05/11 - updated from deprecated packages and used more
 *                         bigdecimal constants
 *    sgu       08/16/11 - print temporary shopping pass only once
 *    sgu       05/16/11 - move instant credit approval status to its own class
 *    cgreene   02/15/11 - move constants into interfaces and refactor
 *    cgreene   05/26/10 - convert to oracle packaging
 *    cgreene   04/26/10 - XbranchMerge cgreene_tech43 from
 *                         st_rgbustores_techissueseatel_generic_branch
 *    cgreene   04/02/10 - remove deprecated LocaleContantsIfc and currencies
 *    cgreene   03/24/10 - add missing EOL to Authorization Response entry in
 *                         journal
 *    abondala  01/03/10 - update header date
 *    glwang    02/06/09 - add isTrainingMode into
 *                         PrintableDocumentParameterBeanIfc
 *    kulu      01/27/09 - Minor change due to code review
 *    kulu      01/27/09 - minor modification based on review
 *    kulu      01/26/09 - Guard against null
 *    kulu      01/26/09 - Fix the bug that House Account enroll response data
 *                         don't have padding translation at enroll franking
 *                         slip
 *    cgreene   11/13/08 - configure print beans into Spring context
 *
 * ===========================================================================
 * $Log:
 * 5    360Commerce 1.4         7/19/2007 1:52:28 PM   Mathews Kochummen format
 *       date,time
 * 4    360Commerce 1.3         1/25/2006 4:10:53 PM   Brett J. Larsen merge
 *      7.1.1 changes (aka. 7.0.3 fixes) into 360Commerce view
 * 3    360Commerce 1.2         3/31/2005 4:27:29 PM   Robert Pearse
 * 2    360Commerce 1.1         3/10/2005 10:20:20 AM  Robert Pearse
 * 1    360Commerce 1.0         2/11/2005 12:10:08 PM  Robert Pearse
 *:
 * 4    .v700     1.2.1.0     11/4/2005 11:44:44     Jason L. DeLeau 4202: Fix
 *      extensibility issues for instant credit service
 * 3    360Commerce1.2         3/31/2005 15:27:29     Robert Pearse
 * 2    360Commerce1.1         3/10/2005 10:20:20     Robert Pearse
 * 1    360Commerce1.0         2/11/2005 12:10:08     Robert Pearse
 *
 *Revision 1.7  2004/07/02 13:28:50  lzhao
 *@scr 3919: use cancel replace continue.
 *
 *Revision 1.6  2004/04/05 23:03:00  jdeleau
 *@scr 4218 JavaDoc fixes associated with RegisterReports changes
 *
 *Revision 1.5  2004/04/02 23:07:51  jdeleau
 *@scr 4218 Register Reports - House Account and initial changes to
 *the way SummaryReports are built.
 *
 *Revision 1.4  2004/03/03 23:15:15  bwf
 *@scr 0 Fixed CommonLetterIfc deprecations.
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
 *    Rev 1.9   Jan 15 2004 15:51:06   nrao
 * Formatted transaction information and repositioned it to appear below the header.
 *
 *    Rev 1.8   Jan 14 2004 15:30:38   nrao
 * Fixed printing type from slip printer to receipt. Fixed printer offline flow.
 *
 *    Rev 1.7   Jan 13 2004 16:05:10   nrao
 * Added transaction information to House Account Enrollment Temporary Pass. Part of House Account Enroll rework.
 *
 *    Rev 1.6   Jan 13 2004 13:51:54   nrao
 * Change in House Account Enroll Requirements require all printing to be performed before tendering. Enabling printing when enrolling within a transaction.
 *
 *    Rev 1.5   Jan 09 2004 15:23:48   nrao
 * Fix for SCR 3699. Fixed journaling for House Account Enroll.
 * Resolution for 3699: House Account Enroll- E. Jouranl is not correct.
 *
 *    Rev 1.4   Dec 30 2003 15:57:44   nrao
 * Set sales associate id so it is written correctly to the database when tendering with instant credit.
 *
 *    Rev 1.3   Nov 24 2003 19:37:18   nrao
 * Code Review Changes.
 *
 * ===========================================================================
 */
package oracle.retail.stores.pos.services.instantcredit.enrollment;

import java.text.DateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.StringTokenizer;

import oracle.retail.stores.commerceservices.common.datetime.DateTimeServiceIfc;
import oracle.retail.stores.commerceservices.common.datetime.DateTimeServiceLocator;
import oracle.retail.stores.common.parameter.ParameterConstantsIfc;
import oracle.retail.stores.common.utility.LocaleMap;
import oracle.retail.stores.domain.DomainGateway;
import oracle.retail.stores.domain.financial.TillIfc;
import oracle.retail.stores.domain.transaction.InstantCreditTransactionIfc;
import oracle.retail.stores.domain.transaction.TransactionIfc;
import oracle.retail.stores.domain.utility.EYSDate;
import oracle.retail.stores.domain.utility.InstantCreditApprovalStatus;
import oracle.retail.stores.domain.utility.LocaleConstantsIfc;
import oracle.retail.stores.foundation.manager.ifc.JournalManagerIfc;
import oracle.retail.stores.foundation.manager.ifc.ParameterManagerIfc;
import oracle.retail.stores.foundation.manager.ifc.UIManagerIfc;
import oracle.retail.stores.foundation.manager.parameter.ParameterException;
import oracle.retail.stores.foundation.tour.application.Letter;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.foundation.tour.service.SessionBusIfc;
import oracle.retail.stores.foundation.utility.Util;
import oracle.retail.stores.pos.config.bundles.BundleConstantsIfc;
import oracle.retail.stores.pos.device.POSDeviceActions;
import oracle.retail.stores.pos.manager.ifc.TransactionUtilityManagerIfc;
import oracle.retail.stores.pos.manager.ifc.UtilityManagerIfc;
import oracle.retail.stores.pos.receipt.PrintableDocumentException;
import oracle.retail.stores.pos.receipt.PrintableDocumentManagerIfc;
import oracle.retail.stores.pos.receipt.ReceiptTypeConstantsIfc;
import oracle.retail.stores.pos.receipt.TempShoppingPass;
import oracle.retail.stores.pos.services.PosSiteActionAdapter;
import oracle.retail.stores.pos.services.common.CommonLetterIfc;
import oracle.retail.stores.pos.services.instantcredit.InstantCreditCargo;
import oracle.retail.stores.pos.ui.DialogScreensIfc;
import oracle.retail.stores.pos.ui.POSUIManagerIfc;
import oracle.retail.stores.pos.ui.UIUtilities;
import oracle.retail.stores.utility.I18NConstantsIfc;
import oracle.retail.stores.utility.I18NHelper;
import oracle.retail.stores.utility.JournalConstantsIfc;

/**
 * @version $Revision: /main/20 $
 */
public class CompleteEnrollmentSite extends PosSiteActionAdapter
{
    private static final long serialVersionUID = -5565758151375108829L;

    /** revision number supplied by version control **/
    public static final String revisionNumber = "$Revision: /main/20 $";

    /**
     * Size of the string that will be passed to the slip printer
     */
    protected static int ENDORSEMENT_SIZE = 328;

    /**
     * printingSpec
     */
    public static final String PRINTING_SPEC = "printingSpec";

    /**
     * transaction label property name
     */
    public static final String TRANSACTION_LABEL_PROP = "TransLabel";

    /**
     * transaction label default value
     */
    public static final String TRANSACTION_LABEL_DEFAULT = "Trans.:";

    /**
     * store label property name
     */
    public static final String STORE_LABEL_PROP = "StoreLabel";

    /**
     * store label default value
     */
    public static final String STORE_LABEL_DEFAULT = "Store:";

    /**
     * register label property name
     */
    public static final String REGISTER_LABEL_PROP = "RegisterLabel";

    /**
     * register label default value
     */
    public static final String REGISTER_LABEL_DEFAULT = "Reg.: ";

    /**
     * till label property name
     */
    public static final String TILL_LABEL_PROP = "TillLabel";

    /**
     * till label default value
     */
    public static final String TILL_LABEL_DEFAULT = "Till: ";

    /**
     * cashier label property name
     */
    public static final String CASHIER_LABEL_PROP = "CashierLabel";

    /**
     * cashier label default value
     */
    public static final String CASHIER_LABEL_DEFAULT = "Cashier: ";

    /**
     * sales associate label property name
     */
    public static final String SALES_ASSOCIATE_LABEL_PROP = "SalesAssociateLabel";

    /**
     * sales associate label default value
     */
    public static final String SALES_ASSOCIATE_LABEL_DEFAULT = "Sales: ";

    /**
     * single space default text
     */
    public static final String SINGLE_SPACE_TEXT = " ";

    /**
     * printer line size
     */
    public static final int PRINTER_LINE_SIZE = 40;

    /**
     * @param bus Service Bus
     */
    @Override
    public void arrive(BusIfc bus)
    {
        ParameterManagerIfc pm = (ParameterManagerIfc)bus.getManager(ParameterManagerIfc.TYPE);
        InstantCreditCargo cargo = (InstantCreditCargo)bus.getCargo();
        POSUIManagerIfc ui = (POSUIManagerIfc)bus.getManager(UIManagerIfc.TYPE);
        JournalManagerIfc jm = (JournalManagerIfc)bus.getManager(JournalManagerIfc.TYPE);
        UtilityManagerIfc utility = (UtilityManagerIfc)bus.getManager(UtilityManagerIfc.TYPE);

        Letter letter = new Letter(CommonLetterIfc.FAILURE);

        // Journal Transaction
        journalTransaction(cargo, jm, utility);

        boolean deviceError = false;
        if (cargo.isApproved())
        {
            letter = new Letter(CommonLetterIfc.CONTINUE);

            // get Device Actions session
            POSDeviceActions pda = new POSDeviceActions((SessionBusIfc)bus);

            // print temporary pass
            deviceError = printTemporaryPass(bus, pm, cargo, deviceError, pda, utility);
        }

        // save transaction
        saveTransaction(bus, cargo);

        if (deviceError)
        {
            String argText = utility.retrieveDialogText("RetryContinue.PrinterOffline", "Printer is offline.");

            String args[] = { argText };
            UIUtilities.setDialogModel(ui, DialogScreensIfc.RETRY_CONTINUE, "RetryContinue", args, new int[] {
                    DialogScreensIfc.BUTTON_RETRY, DialogScreensIfc.BUTTON_CONTINUE },
                    new String[] { "Retry", "Cancel" });
        }
        else
        {
            bus.mail(letter, BusIfc.CURRENT);
        }
    }

    /**
     * Print Temporary Pass
     *
     * @param pm ParameterManagerIfc
     * @param cargo InstantCreditCargo
     * @param deviceError boolean
     * @param pda PosDeviceActions
     * @param util Used to get I18N strings in temporary pass
     * @return boolean
     */
    protected boolean printTemporaryPass(BusIfc bus, ParameterManagerIfc pm,
            InstantCreditCargo cargo, boolean deviceError,
            POSDeviceActions pda, UtilityManagerIfc util)
    {
        try
        {
            if (cargo.getInstantCredit() != null)
            {
                // check temp shopping pass parameter
                int validFor = pm.getIntegerValue(ParameterConstantsIfc.HOUSEACCOUNT_TempShoppingPassEnrollmentExp).intValue();
                EYSDate expDate = DomainGateway.getFactory().getEYSDateInstance();
                expDate.add(Calendar.DAY_OF_YEAR, validFor);

                // print pass
                PrintableDocumentManagerIfc pdm = (PrintableDocumentManagerIfc)bus.getManager(PrintableDocumentManagerIfc.TYPE);
                TempShoppingPass pass = (TempShoppingPass)pdm.getParameterBeanInstance(ReceiptTypeConstantsIfc.TEMPSHOPPINGPASS);
                pass.setTrainingMode(cargo.getRegister().getWorkstation().isTrainingMode());
                pass.setInstantCredit(cargo.getInstantCredit());
                pass.setEnrollmentTransaction(cargo.getTransaction());
                pass.setExpDate(expDate);
                pass.setTrainingMode(cargo.getRegister().getWorkstation().isTrainingMode());
                pdm.printReceipt((SessionBusIfc)bus, pass);
            }
            else
            {
                cargo.setTransactionSaved(true);
            }
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
        return deviceError;
    }

    /**
     * Save Transaction and write hard totals
     *
     * @param bus BusIfc
     * @param cargo InstantCreditCargo
     */
    protected void saveTransaction(BusIfc bus, InstantCreditCargo cargo)
    {
        InstantCreditTransactionIfc instantCreditTransaction = (InstantCreditTransactionIfc)cargo.getTransaction();
        // get sales associate id & set to transaction
        if (instantCreditTransaction.getInstantCredit() != null)
        {
            instantCreditTransaction.setSalesAssociate(instantCreditTransaction.getInstantCredit()
                    .getInstantCreditSalesAssociate());
        }

        // Make call to JDBCSaveInstantCreditTransaction
        if (!cargo.isTransactionSaved()
                && cargo.getTransaction().getTransactionType() == TransactionIfc.TYPE_INSTANT_CREDIT_ENROLLMENT)
        {
            try
            {
                TillIfc till = cargo.getRegister().getCurrentTill();
                // TODO: ASIN
                TransactionUtilityManagerIfc utility = (TransactionUtilityManagerIfc)bus.getManager(TransactionUtilityManagerIfc.TYPE);
                utility.saveTransaction(instantCreditTransaction, instantCreditTransaction.getFinancialTotals(), till,
                        cargo.getRegister());
                utility.writeHardTotals();
                cargo.setTransactionSaved(true);
            }
            catch (Exception e)
            {
                logger.error(e.toString());
            }
        }
    }

    /**
     * Journal Transaction
     *
     * @param cargo InstantCreditCargo
     * @param jm JournalManagerIfc
     */
    protected void journalTransaction(InstantCreditCargo cargo, JournalManagerIfc jm, UtilityManagerIfc util)
    {
        // build arguments
        Object[] dataArgs = new Object[2];

        InstantCreditApprovalStatus approvalStatus = cargo.getApprovalStatus();
        if (approvalStatus != null)
        {
            dataArgs[0] = util.retrieveCommonText(approvalStatus.getResourceKey(), approvalStatus.getResourceKey(), LocaleConstantsIfc.JOURNAL);
        }

        // build journal text
        StringBuilder sb = new StringBuilder();
        sb.append(Util.EOL).append(I18NHelper.getString(I18NConstantsIfc.EJOURNAL_TYPE,
                JournalConstantsIfc.AUTHORIZATION_RESPONSE_LABEL, dataArgs));
        dataArgs[0] = cargo.getReferenceNumber();
        sb.append(Util.EOL).append(I18NHelper.getString(I18NConstantsIfc.EJOURNAL_TYPE,
                JournalConstantsIfc.REFERENCE_NUMBER_LABEL, dataArgs)).append(Util.EOL);
        jm.journal(sb.toString());
    }

    /**
     * Prints date and time in block format (date on left side, and time on
     * right side separated by spaces).
     *
     * @param date EYSDate to print
     * @param stringLength length of the overall string
     * @param locale Locale for formatting
     * @return date and time in block format
     * @deprecated as of 13.1 in favor of BPT framework
     */
    protected String blockFormatDate(EYSDate date, int stringLength, Locale locale)
    {
        Date dateObj = date.dateValue();
        DateTimeServiceIfc dateTimeService = DateTimeServiceLocator.getDateTimeService();
        String dateString = dateTimeService.formatDate(dateObj, locale, DateFormat.SHORT);
        String timeString = dateTimeService.formatTime(dateObj, locale, DateFormat.SHORT);

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
     * @param header
     * @param transactionNumberStr
     * @param storeIDStr
     * @param utility
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
     *
     * @param header
     * @param workstationIDStr
     * @param tillIDStr
     * @param cashierIDstr
     * @param salesAssociateIDStr
     * @param utility Used to extract I18N text
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

        if (cargo.getInstantCredit() != null)
        {
            if (cargo.getInstantCredit().getInstantCreditSalesAssociate() != null)
            {
                salesAssociateIDStr = checkNull(SINGLE_SPACE_TEXT
                        + cargo.getInstantCredit().getInstantCreditSalesAssociate().getEmployeeID());
            }
            else
            {
                salesAssociateIDStr = cashierIDstr;
            }
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
     * @deprecated as of 13.1 in favor of BPT framework
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
