/* ===========================================================================
* Copyright (c) 2003, 2011, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/instantcredit/PrintOneDayTempPassSite.java /main/15 2011/12/05 12:16:20 cgreene Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    cgreene   12/05/11 - updated from deprecated packages and used more
 *                         bigdecimal constants
 *    cgreene   05/26/10 - convert to oracle packaging
 *    cgreene   04/26/10 - XbranchMerge cgreene_tech43 from
 *                         st_rgbustores_techissueseatel_generic_branch
 *    cgreene   04/02/10 - remove deprecated LocaleContantsIfc and currencies
 *    abondala  01/03/10 - update header date
 *    glwang    02/06/09 - add isTrainingMode into
 *                         PrintableDocumentParameterBeanIfc
 *    cgreene   11/13/08 - configure print beans into Spring context
 *
 * ===========================================================================
 * $Log:
 * 4    360Commerce 1.3         1/25/2006 4:11:38 PM   Brett J. Larsen merge
 *      7.1.1 changes (aka. 7.0.3 fixes) into 360Commerce view
 * 3    360Commerce 1.2         3/31/2005 4:29:30 PM   Robert Pearse   
 * 2    360Commerce 1.1         3/10/2005 10:24:23 AM  Robert Pearse   
 * 1    360Commerce 1.0         2/11/2005 12:13:26 PM  Robert Pearse   
 *:
 * 4    .v700     1.2.1.0     11/4/2005 11:44:45     Jason L. DeLeau 4202: Fix
 *      extensibility issues for instant credit service
 * 3    360Commerce1.2         3/31/2005 15:29:30     Robert Pearse
 * 2    360Commerce1.1         3/10/2005 10:24:23     Robert Pearse
 * 1    360Commerce1.0         2/11/2005 12:13:26     Robert Pearse
 *
 *Revision 1.5  2004/05/13 13:13:08  aachinfiev
 *Fixed defect 4470. CreateTempPass had RetryCancel screen instead RetryContinue.
 *
 *Revision 1.4  2004/03/03 23:15:08  bwf
 *@scr 0 Fixed CommonLetterIfc deprecations.
 *
 *Revision 1.3  2004/02/12 16:50:40  mcs
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
 *    Rev 1.4   Jan 15 2004 15:49:02   nrao
 * Formatted transaction information and repositioned it to appear below the header.
 *
 *    Rev 1.3   Jan 12 2004 18:09:32   nrao
 * Rework for Temporary Shopping Pass. Added header to the pass.
 *
 *    Rev 1.2   Dec 03 2003 17:28:56   nrao
 * Changed dialog message from RetryContinue to RetryCancel.
 *
 *    Rev 1.1   Nov 24 2003 19:29:38   nrao
 * Code Review Changes.
 *
 * ===========================================================================
 */
package oracle.retail.stores.pos.services.instantcredit;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;
import java.util.StringTokenizer;

import oracle.retail.stores.domain.utility.LocaleConstantsIfc;
import oracle.retail.stores.pos.services.common.CommonLetterIfc;
import oracle.retail.stores.domain.DomainGateway;
import oracle.retail.stores.domain.utility.EYSDate;
import oracle.retail.stores.domain.utility.EYSTime;
import oracle.retail.stores.foundation.manager.ifc.UIManagerIfc;
import oracle.retail.stores.foundation.tour.application.Letter;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.foundation.tour.service.SessionBusIfc;
import oracle.retail.stores.common.utility.LocaleMap;
import oracle.retail.stores.pos.config.bundles.BundleConstantsIfc;
import oracle.retail.stores.pos.manager.ifc.UtilityManagerIfc;
import oracle.retail.stores.pos.receipt.PrintableDocumentException;
import oracle.retail.stores.pos.receipt.PrintableDocumentManagerIfc;
import oracle.retail.stores.pos.receipt.ReceiptTypeConstantsIfc;
import oracle.retail.stores.pos.receipt.TempShoppingPass;
import oracle.retail.stores.pos.services.PosSiteActionAdapter;
import oracle.retail.stores.pos.ui.DialogScreensIfc;
import oracle.retail.stores.pos.ui.POSUIManagerIfc;
import oracle.retail.stores.pos.ui.UIUtilities;

/**
 * @version $Revision: /main/15 $
 */
public class PrintOneDayTempPassSite extends PosSiteActionAdapter
{
    private static final long serialVersionUID = -6430907831590073996L;

    /** revision number supplied by version control **/
    public static final String revisionNumber = "$Revision: /main/15 $";

    /**
     * Size of the string that will be passed to the slip printer
     * @deprecated as of 13.1 in favor of BPT framework
     */
    protected static int ENDORSEMENT_SIZE = 328;

    /**
     * printingSpec
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
     * @param bus Service Bus
     */
    @Override
    public void arrive(BusIfc bus)
    {
        POSUIManagerIfc ui = (POSUIManagerIfc)bus.getManager(UIManagerIfc.TYPE);
        InstantCreditCargo cargo = (InstantCreditCargo)bus.getCargo();

        boolean deviceError = false;
        boolean trainingMode = cargo.getRegister().getWorkstation().isTrainingMode();

        // prints one day temp pass
        try
        {
            EYSDate expDate = DomainGateway.getFactory().getEYSDateInstance();
            expDate.add(Calendar.DAY_OF_YEAR, 1);

            // print shopping pass
            PrintableDocumentManagerIfc pdm = (PrintableDocumentManagerIfc)bus.getManager(PrintableDocumentManagerIfc.TYPE);
            TempShoppingPass pass = (TempShoppingPass)pdm.getParameterBeanInstance(ReceiptTypeConstantsIfc.TEMPSHOPPINGPASS);
            pass.setTrainingMode(cargo.getRegister().getWorkstation().isTrainingMode());
            pass.setInstantCredit(cargo.getInstantCredit());
            pass.setEnrollmentTransaction(cargo.getTransaction());
            pass.setExpDate(expDate);
            pass.setTrainingMode(trainingMode);
            pass.setOneDayTempPass(true);
            pdm.printReceipt((SessionBusIfc)bus, pass);
        }
        catch (PrintableDocumentException e)
        {
            deviceError = true;
            logger.error("unable to print temp shopping pass", e);
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
            bus.mail(new Letter(CommonLetterIfc.SUCCESS), BusIfc.CURRENT);
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
        String timeString = time.toFormattedTimeString(SimpleDateFormat.LONG, locale);

        // Need to block print.
        // Put spaces between the date and time.
        StringBuffer blockDateStr = new StringBuffer(stringLength);
        blockDateStr.append(dateString);
        int dateStringLength = dateString.length();
        int numSpaces = stringLength - (dateStringLength + timeString.length());
        for (int x = 0; x < numSpaces; x++)
            blockDateStr.append(" ");
        blockDateStr.append(timeString);

        // Return the string.
        return blockDateStr.toString();
    }

    /**
     * Builds the transaction/store line for standard header for franking
     * 
     * @param StringBuffer header
     * @param String transactionNumberStr
     * @param String storeIDStr
     * @param UtilityManagerIfc utility
     * @return StringBuffer header
     * @deprecated as of 13.1 in favor of BPT framework
     */
    protected StringBuffer buildTransactionStoreHeader(StringBuffer header, String transactionNumberStr,
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
     * @param StringBuffer header
     * @param String workstationIDStr
     * @param String tillIDStr
     * @param String cashierIDstr
     * @param String salesAssociateIDStr
     * @param UtilityManagerIfc utility
     * @return StringBuffer header
     * @deprecated as of 13.1 in favor of BPT framework
     */
    protected StringBuffer buildRegisterTillCashierSalesAssociateHeader(StringBuffer header, String workstationIDStr,
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
     * @param buffer StringBuffer
     * @param spaces int
     * @deprecated as of 13.1 in favor of BPT framework
     */
    protected void pad(StringBuffer buffer, int spaces)
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
     * @return StringBuffer header
     * @deprecated as of 13.1 in favor of BPT framework
     */
    protected StringBuffer buildHeader(UtilityManagerIfc utility, InstantCreditCargo cargo)
    {
        StringBuffer header = new StringBuffer(ENDORSEMENT_SIZE);
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
