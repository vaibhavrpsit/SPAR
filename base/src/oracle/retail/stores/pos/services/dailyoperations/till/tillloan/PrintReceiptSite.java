/* ===========================================================================
* Copyright (c) 1998, 2013, Oracle and/or its affiliates. All rights reserved.
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/dailyoperations/till/tillloan/PrintReceiptSite.java /main/15 2013/08/26 12:03:46 mkutiana Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    mkutiana  08/26/13 - fixed PromptandResponse panel display text
 *    cgreene   02/15/11 - move constants into interfaces and refactor
 *    cgreene   05/26/10 - convert to oracle packaging
 *    abondala  01/03/10 - update header date
 *    cgreene   11/25/08 - switch to specific TillLoan doc type
 *    cgreene   11/13/08 - configure print beans into Spring context
 *
 * ===========================================================================
 * $Log:
 *    3    360Commerce 1.2         3/31/2005 4:29:30 PM   Robert Pearse   
 *    2    360Commerce 1.1         3/10/2005 10:24:24 AM  Robert Pearse   
 *    1    360Commerce 1.0         2/11/2005 12:13:26 PM  Robert Pearse   
 *
 *   Revision 1.6  2004/07/22 00:06:34  jdeleau
 *   @scr 3665 Standardize on I18N standards across all properties files.
 *   Use {0}, {1}, etc instead of remaining <ARG> or #ARG# variables.
 *
 *   Revision 1.5  2004/04/06 19:55:03  mweis
 *   @scr 4305  Sale:  indicators when till balance is negative
 *
 *   Revision 1.4  2004/02/17 18:47:24  dcobb
 *   @scr 3381 Feature Enhancement:  Till Pickup and Loan
 *   Initialize receiptCount.
 *
 *   Revision 1.3  2004/02/12 16:49:59  mcs
 *   Forcing head revision
 *
 *   Revision 1.2  2004/02/11 21:46:54  rhafernik
 *   @scr 0 Log4J conversion and code cleanup
 *
 *   Revision 1.1.1.1  2004/02/11 01:04:15  cschellenger
 *   updating to pvcs 360store-current
 *
 *
 * 
 *    Rev 1.1   Jan 28 2004 17:34:12   DCobb
 * Added Pickup and Loan parameters.
 * Resolution for 3381: Feature Enhancement:  Till Pickup and Loan
 * 
 *    Rev 1.0   Aug 29 2003 15:57:50   CSchellenger
 * Initial revision.
 * 
 *    Rev 1.4   Mar 05 2003 20:44:40   KLL
 * integration of code review results
 * Resolution for POS SCR-1884: Printing Functional Requirements
 *
 *    Rev 1.2   Jan 03 2003 08:33:36   KLL
 * Parameter control for Number of receipts
 *
 *    Rev 1.1   Aug 23 2002 08:59:36   RSachdeva
 * Code conversion
 * Resolution for POS SCR-1740: Code base Conversions
 *
 *    Rev 1.0   Apr 29 2002 15:28:08   msg
 * Initial revision.
 *
 *    Rev 1.0   Mar 18 2002 11:29:12   msg
 * Initial revision.
 *
 *    Rev 1.6   Mar 12 2002 14:09:26   mpm
 * Externalized text in receipts and documents.
 * Resolution for POS SCR-351: Internationalization
 *
 *    Rev 1.5   Mar 10 2002 18:00:14   mpm
 * Externalized text in dialog messages.
 * Resolution for POS SCR-351: Internationalization
 *
 *    Rev 1.4   Mar 09 2002 17:17:28   mpm
 * Text externalization.
 * Resolution for POS SCR-351: Internationalization
 *
 *    Rev 1.2   08 Feb 2002 14:03:20   epd
 * Corrected UI messages
 * Resolution for POS SCR-726: Till pickup - 'Summary Count' screen is incorrect
 * Resolution for POS SCR-727: Till Loan - 'Summary Count' screen has incorrect text
 * Resolution for POS SCR-728: Till loan - 'Report Printing' screen, text is incorrect
 * Resolution for POS SCR-729: Till pickup - 'Report Printing' screen text is incorrect
 * Resolution for POS SCR-730: Till pickup - checks - 'Report Printing' screen text is incorrect
 *
 *    Rev 1.1   26 Oct 2001 15:07:36   jbp
 * Implement new reciept methodology
 * Resolution for POS SCR-221: Receipt Design Changes
 *
 *    Rev 1.0   Sep 21 2001 11:18:28   msg
 * Initial revision.
 *
 *    Rev 1.1   Sep 17 2001 13:14:30   msg
 * header update
 * ===========================================================================
 */
package oracle.retail.stores.pos.services.dailyoperations.till.tillloan;

import oracle.retail.stores.common.context.BeanLocator;
import oracle.retail.stores.domain.financial.FinancialCountIfc;
import oracle.retail.stores.domain.financial.FinancialTotalsIfc;
import oracle.retail.stores.domain.financial.ReconcilableCountIfc;
import oracle.retail.stores.foundation.manager.device.DeviceException;
import oracle.retail.stores.foundation.manager.ifc.UIManagerIfc;
import oracle.retail.stores.foundation.tour.application.Letter;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.foundation.tour.service.SessionBusIfc;
import oracle.retail.stores.pos.config.bundles.BundleConstantsIfc;
import oracle.retail.stores.pos.manager.ifc.UtilityManagerIfc;
import oracle.retail.stores.pos.receipt.PrintableDocumentException;
import oracle.retail.stores.pos.receipt.PrintableDocumentManagerIfc;
import oracle.retail.stores.pos.receipt.ReceiptParameterBeanIfc;
import oracle.retail.stores.pos.receipt.ReceiptTypeConstantsIfc;
import oracle.retail.stores.pos.services.PosSiteActionAdapter;
import oracle.retail.stores.pos.services.common.CommonLetterIfc;
import oracle.retail.stores.pos.ui.DialogScreensIfc;
import oracle.retail.stores.pos.ui.POSUIManagerIfc;
import oracle.retail.stores.pos.ui.beans.DialogBeanModel;
import oracle.retail.stores.pos.ui.beans.POSBaseBeanModel;
import oracle.retail.stores.pos.ui.beans.PromptAndResponseModel;
import oracle.retail.stores.pos.ui.beans.StatusBeanModel;

/**
    Prints receipt when issuing a loan from the till.
    <P>
    @version $Revision: /main/15 $
**/
//------------------------------------------------------------------------------
public class PrintReceiptSite extends PosSiteActionAdapter
{
    private static final long serialVersionUID = -4108676433581781237L;

    /**
       revision number supplied by Team Connection
    **/
    public static final String revisionNumber = "$Revision: /main/15 $";
    /**
     * PrintReceiptSite
     */
    public static final String SITENAME = "PrintReceiptSite";

    /**
     * parameter string for Till Loan Receipts
     * 
     * @deprecated as of 13.1 use blueprint configuration instead
     */
    public static final String TILL_LOAN_RECEIPT_COUNT = "NumberTillLoanReceipts";

    /**
     * parameter string for Pickup and Loan Receipt Signature Line Printing
     * 
     * @deprecated as of 13.1 use blueprint configuration instead
     */
    protected static final String PICKUP_AND_LOAN_PRINT_SIGNATURE = "PickupAndLoanReceiptSignatureLinePrinting";

    /**
     * parameter string for Operate with Safe
     */
    protected static final String OPERATE_WITH_SAFE = "OperateWithSafe";

    /**
     * cash loan text tag
     * 
     * @deprecated as of 13.1 use blueprint configuration instead
     */
    protected static final String LOAN_TEXT_TAG = "CashLoanText";

    /**
     * cash loan text(with the dot)
     * 
     * @deprecated as of 13.1 use blueprint configuration instead
     */
    protected static final String LOAN_TEXT = "Cash loan";

    /**
     * Collects the Loan count then calls printReport to print the count.
     * Displays a screen that printing is occurring. Catches device exceptions
     * and displays a dialog to allow the user to Retry or Cancel. If parameter
     * TillCountTillLoan is set to No then do not count the loan amount,
     * otherwise count the loan amount based upon this parameter's value
     * (Summary/Detail).
     * 
     * @param bus the bus arriving at this site
     */
    @Override
    public void arrive(BusIfc bus)
    {
        Letter letter = null;
        POSUIManagerIfc ui = (POSUIManagerIfc)bus.getManager(UIManagerIfc.TYPE);
        UtilityManagerIfc utility =
          (UtilityManagerIfc) bus.getManager(UtilityManagerIfc.TYPE);
        POSBaseBeanModel pbbModel = new POSBaseBeanModel();
        PromptAndResponseModel pnrModel = new PromptAndResponseModel();
        StatusBeanModel sbModel = new StatusBeanModel();
        StringBuffer argumentText = new StringBuffer();
        argumentText.append(utility.retrieveText(
                POSUIManagerIfc.PROMPT_AND_RESPONSE_SPEC,
                BundleConstantsIfc.DAILY_OPERATIONS_BUNDLE_NAME,
                LOAN_TEXT_TAG,LOAN_TEXT));
        pnrModel.setArguments(argumentText.toString());
        pbbModel.setPromptAndResponseModel(pnrModel);
        pbbModel.setStatusBeanModel(sbModel);
        sbModel.setStatus(POSUIManagerIfc.PRINTER_STATUS, POSUIManagerIfc.ONLINE);
      
        // Side-effect: Allow the status model to dynamically compute a negative till balance.
        sbModel.setRegister(((TillLoanCargo) bus.getCargo()).getRegister());
        
        ui.showScreen(POSUIManagerIfc.REPORT_PRINTING, pbbModel);

        try
        {
            printReport(bus);
            sbModel.setStatus(POSUIManagerIfc.PRINTER_STATUS, POSUIManagerIfc.ONLINE);
            letter = new Letter(CommonLetterIfc.SUCCESS);
        }
        catch (PrintableDocumentException e)
        {
            // Update printer status
            logger.error("PrintReceipt exception");
            logger.error(e);
            logger.error(e.getNestedException());
            sbModel.setStatus(POSUIManagerIfc.PRINTER_STATUS, POSUIManagerIfc.OFFLINE);

            String msg[] = new String[1];
            msg[0] = utility.retrieveDialogText(BundleConstantsIfc.PRINTER_OFFLINE_TAG,
                                                BundleConstantsIfc.PRINTER_OFFLINE);

            DialogBeanModel model = new DialogBeanModel();
            model.setResourceID("RetryContinue");
            model.setType(DialogScreensIfc.RETRY_CONTINUE);
            model.setArgs(msg);
            ui.showScreen(POSUIManagerIfc.DIALOG_TEMPLATE, model);
        }

        if (letter != null)
        {
            bus.mail(letter, BusIfc.CURRENT);
        }
    }

    /**
     * Print the report.
     * 
     * @param bus The bus.
     * @throws DeviceException upon error.
     */
    protected void printReport(BusIfc bus) throws PrintableDocumentException
    {
        TillLoanCargo cargo = (TillLoanCargo) bus.getCargo();

        // load count for printing report
        FinancialCountIfc count = null;
        int countType = cargo.getTransaction().getCountType();

        if (countType != FinancialCountIfc.COUNT_TYPE_NONE)
        {
            // Get financial count
            FinancialTotalsIfc fc = cargo.getRegister().getTillByID(cargo.getTillID()).getTotals();

            // Get all the loans in this till
            ReconcilableCountIfc[] tillLoans = fc.getTillLoans();

            // This loan is the last in the Loans array
            count = tillLoans[tillLoans.length-1].getEntered();
        }

        ReceiptParameterBeanIfc receipt = (ReceiptParameterBeanIfc)BeanLocator.getApplicationBean(ReceiptParameterBeanIfc.BEAN_KEY);
        receipt.setTransaction(cargo.getTransaction());
        receipt.setDocumentType(ReceiptTypeConstantsIfc.TILLLOAN);
        receipt.setFinancialCount(count);
        PrintableDocumentManagerIfc pdm = (PrintableDocumentManagerIfc)bus.getManager(PrintableDocumentManagerIfc.TYPE);
        pdm.printReceipt((SessionBusIfc)bus, receipt);
    }
}
