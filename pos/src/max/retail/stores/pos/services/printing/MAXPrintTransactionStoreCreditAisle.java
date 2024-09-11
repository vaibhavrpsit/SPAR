/* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
   Copyright (c) 2012 - 2013 MAXHyperMarket, Inc.    All Rights Reserved.
	Rev 1.0  05/Sep/2013	Prateek		Initial Draft: Changes done to suppress receipt in training mode.
 * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */

package max.retail.stores.pos.services.printing;

//Java imports
import java.util.Properties;

import oracle.retail.stores.common.context.BeanLocator;
import oracle.retail.stores.common.parameter.ParameterConstantsIfc;
import oracle.retail.stores.common.utility.LocaleMap;
import oracle.retail.stores.domain.tender.TenderLineItemIfc;
import oracle.retail.stores.domain.tender.TenderStoreCreditIfc;
import oracle.retail.stores.domain.transaction.TenderableTransactionIfc;
import oracle.retail.stores.domain.transaction.TransactionIfc;
import oracle.retail.stores.domain.utility.LocaleConstantsIfc;
import oracle.retail.stores.foundation.manager.device.DeviceException;
import oracle.retail.stores.foundation.manager.device.LocalizedDeviceException;
import oracle.retail.stores.foundation.manager.gui.InternationalTextSupport;
import oracle.retail.stores.foundation.manager.ifc.ParameterManagerIfc;
import oracle.retail.stores.foundation.manager.ifc.UIManagerIfc;
import oracle.retail.stores.foundation.manager.parameter.ParameterException;
import oracle.retail.stores.foundation.tour.application.Letter;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.foundation.tour.service.SessionBusIfc;
import oracle.retail.stores.foundation.utility.Util;
import oracle.retail.stores.pos.config.bundles.BundleConstantsIfc;
import oracle.retail.stores.pos.device.POSDeviceActions;
import oracle.retail.stores.pos.manager.ifc.UtilityManagerIfc;
import oracle.retail.stores.pos.receipt.EYSPrintableDocumentIfc;
import oracle.retail.stores.pos.receipt.PrintableDocumentException;
import oracle.retail.stores.pos.receipt.PrintableDocumentManagerIfc;
import oracle.retail.stores.pos.receipt.ReceiptParameterBeanIfc;
import oracle.retail.stores.pos.receipt.ReceiptTypeConstantsIfc;
import oracle.retail.stores.pos.services.PosLaneActionAdapter;
import oracle.retail.stores.pos.services.common.CommonLetterIfc;
import oracle.retail.stores.pos.services.printing.PrintTransactionStoreCreditAisle;
import oracle.retail.stores.pos.services.printing.PrintingCargo;
import oracle.retail.stores.pos.ui.DialogScreensIfc;
import oracle.retail.stores.pos.ui.POSUIManagerIfc;
import oracle.retail.stores.pos.ui.beans.DialogBeanModel;
import oracle.retail.stores.pos.ui.beans.POSBaseBeanModel;
import oracle.retail.stores.pos.ui.beans.StatusBeanModel;

//------------------------------------------------------------------------------
/**
    Print the receipt.

    @version $Revision: 3$
**/
//------------------------------------------------------------------------------
public class MAXPrintTransactionStoreCreditAisle extends PrintTransactionStoreCreditAisle
{

    public static final String LANENAME = "PrintTransactionStoreCreditAisle";

    //--------------------------------------------------------------------------
    /**
       Print the receipt and send a letter

       @param bus the bus traversing this lane
    **/
    //--------------------------------------------------------------------------
    public void traverse(BusIfc bus)
    {

        PrintingCargo cargo = (PrintingCargo) bus.getCargo();
        TenderableTransactionIfc trans = cargo.getTransaction();
        boolean sendMail = true;
        boolean printStoreCredit = true;
        String letter = "ExitPrinting";
        
        ParameterManagerIfc pm = (ParameterManagerIfc)bus.getManager(ParameterManagerIfc.TYPE);

        POSUIManagerIfc ui = (POSUIManagerIfc) bus.getManager(UIManagerIfc.TYPE);
        try
        {
            
            // print the store credit if the store is not using pre-printed credits
            printStoreCredit = !pm.getBooleanValue(ParameterConstantsIfc.TENDER_PrePrintedStoreCredit).booleanValue();
        }
        catch (ParameterException e)
        {
            logger.warn("Unable to determine PrePrintedStoreCredit parameter", e);
        }
        
        if (printStoreCredit && trans.getTransactionType() != TransactionIfc.TYPE_VOID)
        {
            try
            {
                printStoreCredits(bus, trans);
            }
            catch (PrintableDocumentException e)
            {
                logger.error("Unable to print store credit receipt.", e);
                cargo.setPrinterError(e);
                letter = CommonLetterIfc.ERROR;
            }
        }

        
        // only print store credit if transaction is not a void
        else if (!printStoreCredit && trans.getTransactionType() != TransactionIfc.TYPE_VOID)
        {
            POSDeviceActions pda = new POSDeviceActions((SessionBusIfc) bus);

            String[] footer  = null;
            String[] header  = null;

            header = cargo.getReceiptText(pm, "ReceiptHeader");
            footer = cargo.getReceiptText(pm, "ReceiptFooter");

            // get properties for receipt
          //Changes done for code merging(commenting below lines for error resolving)
            /*Properties props = InternationalTextSupport.getInternationalBeanText
                                ("receipt",
                                UtilityManagerIfc.RECEIPT_BUNDLES);
            EYSPrintableDocumentIfc receipt = new StoreCreditReceipt(trans,header,footer);
            receipt.setProps(props);*/

            try
            {
            	//Changes done for code merging(commenting below lines for error resolving)
            	/*if(!trans.isTrainingMode())
            		pda.printDocument(receipt);*/

                // Update printer status
                //            ui.statusChanged(POSUIManagerIfc.PRINTER_STATUS,
                //                             POSUIManagerIfc.ONLINE);
                StatusBeanModel statusModel = new StatusBeanModel();
                statusModel.setStatus(POSUIManagerIfc.PRINTER_STATUS, POSUIManagerIfc.ONLINE);
                POSBaseBeanModel baseModel = new POSBaseBeanModel();
                baseModel.setStatusBeanModel(statusModel);
                ui.setModel(POSUIManagerIfc.SHOW_STATUS_ONLY, baseModel);

            }
          //Changes done for code merging(commenting below lines for error resolving)
            //catch (DeviceException e)
            catch(Exception e)
            {
                logger.warn(
                            "Unable to print receipt. " + e.getMessage() + "");

                // Update printer status
                //            ui.statusChanged(POSUIManagerIfc.PRINTER_STATUS,
                //                             POSUIManagerIfc.OFFLINE);
                StatusBeanModel statusModel = new StatusBeanModel();
                statusModel.setStatus(POSUIManagerIfc.PRINTER_STATUS, POSUIManagerIfc.OFFLINE);
                POSBaseBeanModel baseModel = new POSBaseBeanModel();
                baseModel.setStatusBeanModel(statusModel);
                ui.setModel(POSUIManagerIfc.SHOW_STATUS_ONLY, baseModel);
              //Changes done for code merging(commenting below lines for error resolving)
                /*if (e.getOrigException() != null)
                {
                    logger.warn(
                                "DeviceException.NestedException:\n" + Util.throwableToString(e.getOrigException()) + "");
                }*/

                String msg[] = new String[1];
                UtilityManagerIfc utility =
                  (UtilityManagerIfc) bus.getManager(UtilityManagerIfc.TYPE);
              
                if (e instanceof LocalizedDeviceException)
                {
                    msg[0] = e.getLocalizedMessage();
                }
              //Changes done for code merging(commenting below lines for error resolving)
               /* else if (e.getErrorCode() != DeviceException.UNKNOWN)
                {
                    msg[0] = utility.retrieveDialogText(BundleConstantsIfc.PRINTER_OFFLINE_TAG,
                                        BundleConstantsIfc.PRINTER_OFFLINE);
                }*/
                else
                {
                    msg[0] = utility.retrieveDialogText("RetryContinue.UnknownPrintingError",
                                                        "An unknown error occurred while printing.");
                }

                DialogBeanModel model = new DialogBeanModel();
                model.setResourceID("RetryContinue");
                model.setType(DialogScreensIfc.RETRY_CONTINUE);
                model.setArgs(msg);

                // display dialog
                ui.showScreen(POSUIManagerIfc.DIALOG_TEMPLATE, model);

                sendMail = false;
            }
        }

        if (sendMail)
        {
            bus.mail(new Letter("ExitPrinting"), BusIfc.CURRENT);
        }
    }
    
    /**
     * @param bus
     * @param trans
     * @throws PrintableDocumentException
     */
    protected void printStoreCredits(BusIfc bus, TenderableTransactionIfc trans)
        throws PrintableDocumentException
    {
        TenderLineItemIfc[] tenders = trans.getTenderLineItems();
        PrintingCargo cargo = (PrintingCargo)bus.getCargo();
        boolean isDuplicateReceipt = cargo.isDuplicateReceipt(); 
        for (int i = 0; i < tenders.length; i++)
        {
            if (tenders[i] instanceof TenderStoreCreditIfc && 
               ((TenderStoreCreditIfc)tenders[i]).isIssued())
            {
                ReceiptParameterBeanIfc receipt = (ReceiptParameterBeanIfc)BeanLocator.getApplicationBean(ReceiptParameterBeanIfc.BEAN_KEY);
                receipt.setLocale(LocaleMap.getLocale(LocaleConstantsIfc.RECEIPT));
                receipt.setTransaction(trans);
                receipt.setTender(tenders[i]);
                receipt.setDocumentType(ReceiptTypeConstantsIfc.STORE_CREDIT);
                receipt.setDuplicateReceipt(isDuplicateReceipt);

                PrintableDocumentManagerIfc pdm = (PrintableDocumentManagerIfc)bus.getManager(PrintableDocumentManagerIfc.TYPE);
                pdm.printReceipt((SessionBusIfc)bus, receipt);  
                cargo.setReceiptPrinted(true);               
            }
        }
    }
}
