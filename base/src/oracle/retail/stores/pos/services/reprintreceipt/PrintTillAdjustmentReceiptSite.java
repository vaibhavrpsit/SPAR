/* ===========================================================================
* Copyright (c) 1998, 2011, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/reprintreceipt/PrintTillAdjustmentReceiptSite.java /main/12 2011/02/16 09:13:29 cgreene Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    cgreene   02/15/11 - move constants into interfaces and refactor
 *    cgreene   05/26/10 - convert to oracle packaging
 *    abondala  01/03/10 - update header date
 *    cgreene   04/21/09 - added setDuplicateReceipt so that receipt will print
 *                         dup banner
 *    ranojha   02/12/09 - Incorporated code review comments
 *    ranojha   02/12/09 - Fixed printReport to make call to the Receipt
 *                         Builder API.
 *
 * ===========================================================================
 * $Log:
 *    3    360Commerce 1.2         3/31/2005 4:29:31 PM   Robert Pearse   
 *    2    360Commerce 1.1         3/10/2005 10:24:25 AM  Robert Pearse   
 *    1    360Commerce 1.0         2/11/2005 12:13:27 PM  Robert Pearse   
 *
 *   Revision 1.7  2004/07/22 04:56:57  khassen
 *   @scr 6296/6297/6298 - Updating pay in, pay out, payroll pay out:
 *   Adding database fields, print and reprint receipt functionality to reflect
 *   persistence of additional data in transaction.
 *
 *   Revision 1.6  2004/04/22 17:39:00  dcobb
 *   @scr 4452 Feature Enhancement: Printing
 *   Added REPRINT_SELECT screen and flow to Reprint Receipt use case..
 *
 *   Revision 1.5  2004/02/20 21:19:09  dcobb
 *   @scr 3381 Feature Enhancement:  Till Pickup and Loan
 *   Code review cleanup.
 *
 *   Revision 1.4  2004/02/13 23:09:14  dcobb
 *   @scr 3381 Feature Enhancement:  Till Pickup and Loan
 *   Add signature count and remove footer for duplicate receipt..
 *
 *   Revision 1.3  2004/02/12 16:51:42  mcs
 *   Forcing head revision
 *
 *   Revision 1.2  2004/02/11 21:52:29  rhafernik
 *   @scr 0 Log4J conversion and code cleanup
 *
 *   Revision 1.1.1.1  2004/02/11 01:04:20  cschellenger
 *   updating to pvcs 360store-current
 *
 *
 * 
 *    Rev 1.0   Aug 29 2003 16:05:38   CSchellenger
 * Initial revision.
 * 
 *    Rev 1.0   Apr 29 2002 15:07:08   msg
 * Initial revision.
 * 
 *    Rev 1.1   22 Mar 2002 14:51:26   pdd
 * Converted to use TenderTypeMapIfc.
 * Resolution for POS SCR-1564: Remove uses of TenderLineItemIfc.TENDER_TYPE_DESCRIPTOR from pos
 * 
 *    Rev 1.0   Mar 18 2002 11:44:48   msg
 * Initial revision.
 * 
 *    Rev 1.3   Mar 10 2002 18:01:14   mpm
 * Externalized text in dialog messages.
 * Resolution for POS SCR-351: Internationalization
 *
 *    Rev 1.2   Mar 10 2002 09:37:24   mpm
 * Externalized text.
 * Resolution for POS SCR-351: Internationalization
 *
 *    Rev 1.1   05 Mar 2002 17:22:00   epd
 * fixed duplicate receipt printing
 * Resolution for POS SCR-939: *DUPLICATE RECEIPT* prints above logo for till payin/payout
 *
 *    Rev 1.0   Sep 21 2001 11:22:58   msg
 * Initial revision.
 *
 *    Rev 1.1   Sep 17 2001 13:12:18   msg
 * header update
 * ===========================================================================
 */
package oracle.retail.stores.pos.services.reprintreceipt;

import oracle.retail.stores.common.context.BeanLocator;
import oracle.retail.stores.domain.transaction.TillAdjustmentTransactionIfc;
import oracle.retail.stores.domain.transaction.TransactionIfc;
import oracle.retail.stores.foundation.manager.device.DeviceException;
import oracle.retail.stores.foundation.manager.ifc.UIManagerIfc;
import oracle.retail.stores.foundation.tour.application.Letter;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.foundation.tour.service.SessionBusIfc;
import oracle.retail.stores.foundation.utility.Util;
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
 * Prints receipt for a till adjustment transaction.
 * 
 * @version $Revision: /main/12 $
 */
public class PrintTillAdjustmentReceiptSite extends PosSiteActionAdapter
{
    private static final long serialVersionUID = 39318009176786052L;

    /**
     * revision number supplied by Team Connection
     */
    public static final String revisionNumber = "$Revision: /main/12 $";

    /**
     * PrintTillAdjustmentReceiptSite
     */
    public static final String SITENAME = "PrintTillAdjustmentReceiptSite";

    /**
     * parameter string for Pickup and Loan Receipt Signature Line Printing
     */
    protected static final String PICKUP_AND_LOAN_PRINT_SIGNATURE = "PickupAndLoanReceiptSignatureLinePrinting";

    /**
     * parameter string for Operate with Safe
     */
    protected static final String OPERATE_WITH_SAFE = "OperateWithSafe";

    /**
     * Collects the count and then calls printReport to print the report.
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
        UtilityManagerIfc utility = (UtilityManagerIfc) bus.getManager(UtilityManagerIfc.TYPE);
        
        POSBaseBeanModel pbbModel = new POSBaseBeanModel();
        PromptAndResponseModel pnrModel = new PromptAndResponseModel();
        StatusBeanModel sbModel = new StatusBeanModel();
        pnrModel.setPromptText(utility.retrieveText(POSUIManagerIfc.PROMPT_AND_RESPONSE_SPEC,
                                                    BundleConstantsIfc.COMMON_BUNDLE_NAME,
                                                    BundleConstantsIfc.REPORT_PRINTING_MESSAGE_TAG,
                                                    BundleConstantsIfc.REPORT_PRINTING_MESSAGE));
        pbbModel.setPromptAndResponseModel(pnrModel);
        pbbModel.setStatusBeanModel(sbModel);
        sbModel.setStatus(POSUIManagerIfc.PRINTER_STATUS, POSUIManagerIfc.ONLINE);
        ui.showScreen(POSUIManagerIfc.REPORT_PRINTING, pbbModel);

        try
        {
            printReport(bus);
            sbModel.setStatus(POSUIManagerIfc.PRINTER_STATUS, POSUIManagerIfc.ONLINE);
            letter = new Letter(CommonLetterIfc.SUCCESS);
        }
        catch (DeviceException e)
        {
            // Update printer status
            logger.error("PrintTillAdjustmentReceipt exception", e);
            logger.error(Util.throwableToString(e.getCause()));
            sbModel.setStatus(POSUIManagerIfc.PRINTER_STATUS, POSUIManagerIfc.OFFLINE);
            String msg[] = new String[1];
            msg[0] = utility.retrieveDialogText("RetryContinue.PrinterOffline",
                                                "Printer is offline.");
            DialogBeanModel model = new DialogBeanModel();
            model.setResourceID("RetryCancel");
            model.setType(DialogScreensIfc.RETRY_CANCEL);
            model.setArgs(msg);
            ui.showScreen(POSUIManagerIfc.DIALOG_TEMPLATE, model);
        }
        catch (Exception xe)
        {
            logger.error( Util.throwableToString(xe));
        }

        if (letter != null)
        {
            bus.mail(letter, BusIfc.CURRENT);
        }

    }

    /**
     * Print the report
     * 
     * @param bus service bus
     */
    protected void printReport(BusIfc bus) throws DeviceException
    {
        ReprintReceiptCargo cargo = (ReprintReceiptCargo) bus.getCargo();
        TillAdjustmentTransactionIfc transaction = (TillAdjustmentTransactionIfc) cargo.getTransaction();
        int transactionType = transaction.getTransactionType();

        try
        {
            ReceiptParameterBeanIfc receipt = (ReceiptParameterBeanIfc)BeanLocator.getApplicationBean(ReceiptParameterBeanIfc.BEAN_KEY);
            receipt.setTransaction(transaction);
            receipt.setDuplicateReceipt(cargo.isDuplicateReceipt());
            switch (transactionType)
            {
                case TransactionIfc.TYPE_LOAN_TILL:
                	receipt.setDocumentType(ReceiptTypeConstantsIfc.TILLLOAN);
                	break;
                case TransactionIfc.TYPE_PICKUP_TILL:
                	receipt.setDocumentType(ReceiptTypeConstantsIfc.TILLPICKUP);
                	break;
                case TransactionIfc.TYPE_PAYIN_TILL:
                	receipt.setDocumentType(ReceiptTypeConstantsIfc.TILLPAYIN);
                	break;
                case TransactionIfc.TYPE_PAYOUT_TILL:
                	receipt.setDocumentType(ReceiptTypeConstantsIfc.TILLPAYOUT);
                	break;
                case TransactionIfc.TYPE_PAYROLL_PAYOUT_TILL:
                	receipt.setDocumentType(ReceiptTypeConstantsIfc.TILLPAYOUT_PAYROLL);
                	break;
            }   	
            PrintableDocumentManagerIfc pdm = (PrintableDocumentManagerIfc)bus.getManager(PrintableDocumentManagerIfc.TYPE);
            pdm.printReceipt((SessionBusIfc)bus, receipt);
        }
        catch (PrintableDocumentException e)
        {
        	logger.error("Error printing till adjustment report", e);
            logger.error(e.getNestedException());
        }
    }

}
