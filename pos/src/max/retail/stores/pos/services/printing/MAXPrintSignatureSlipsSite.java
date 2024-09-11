/* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
*   Copyright (c) 2012 - 2013 MAXHyperMarket, Inc.    All Rights Reserved. 
   Rev 1.1  23-02-2017  Nitika Arora Changes for Post Void hanging issue(Update the closing bracket)
*  Rev 1.0  22/May/2013	Jyoti Rawal, Initial Draft: Changes for Credit Card Functionality 
* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
package max.retail.stores.pos.services.printing;

import oracle.retail.stores.domain.lineitem.AbstractTransactionLineItemIfc;
import oracle.retail.stores.domain.lineitem.SaleReturnLineItemIfc;
import oracle.retail.stores.domain.tender.TenderCharge;
import oracle.retail.stores.domain.tender.TenderCheck;
import oracle.retail.stores.domain.tender.TenderLineItemConstantsIfc;
import oracle.retail.stores.domain.tender.TenderLineItemIfc;
import oracle.retail.stores.domain.transaction.RetailTransactionIfc;
import oracle.retail.stores.domain.transaction.TenderableTransactionIfc;
import oracle.retail.stores.domain.transaction.TransactionIfc;
import oracle.retail.stores.foundation.manager.ifc.ParameterManagerIfc;
import oracle.retail.stores.foundation.manager.ifc.UIManagerIfc;
import oracle.retail.stores.foundation.manager.parameter.ParameterException;
import oracle.retail.stores.foundation.tour.application.Letter;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.foundation.tour.service.SessionBusIfc;
import oracle.retail.stores.pos.device.POSDeviceActions;
import oracle.retail.stores.pos.manager.ifc.UtilityManagerIfc;
import oracle.retail.stores.pos.services.PosSiteActionAdapter;
import oracle.retail.stores.pos.services.printing.PrintingCargo;
import oracle.retail.stores.pos.ui.DialogScreensIfc;
import oracle.retail.stores.pos.ui.POSUIManagerIfc;
import oracle.retail.stores.pos.ui.beans.DialogBeanModel;
// changs starts for code merging(commenting below line)
//public class MAXPrintSignatureSlipsSite extends PrintSignatureSlipsSite{
public class MAXPrintSignatureSlipsSite extends PosSiteActionAdapter{
// Changes ends for code merging
	/**
	 * 
	 */
	private static final long serialVersionUID = 8258955329430062782L;

	public void arrive(BusIfc bus)
    {
        PrintingCargo cargo = (PrintingCargo) bus.getCargo();
        boolean sendMail = true;
        TenderableTransactionIfc trans = cargo.getTransaction();

        if(trans.getTransactionType() != TransactionIfc.TYPE_VOID)
        {                                           // begin Trans Void
            POSUIManagerIfc ui = (POSUIManagerIfc) bus.getManager(UIManagerIfc.TYPE);
            ParameterManagerIfc pm = (ParameterManagerIfc) bus.getManager(ParameterManagerIfc.TYPE);
            String sigSlipConfig = cargo.getParameterValue(pm, "PrintCreditSignatureSlips");
            String sigSlipNameConfig = cargo.getParameterValue(pm, "PrintNameOnCreditSignatureSlip");
            String signatureHeight = cargo.getParameterValue(pm, "SignatureBitmapHeight");
            String signatureWidth = cargo.getParameterValue(pm, "SignatureBitmapWidth");
            String[] header = cargo.getReceiptText(pm, "ReceiptHeader");
            String[] footer = cargo.getReceiptText(pm, "ReceiptFooter");
            // determine transaction type
            int transType = determineTransType(trans);
            // get legal statement from parameter based on transaction type
          //Changes done for code merging(commenting below lines for error resolving)
            /*String[] legalStatement = setLegalStatement(transType, pm, bus.getServiceName());*/
            POSDeviceActions pda = new POSDeviceActions((SessionBusIfc) bus);

            // Get credit tenders
            try
            {
                // set signature height and width for printed receipt in each
                // charge tender that has signature data
                TenderLineItemIfc[] tenders = trans.getTenderLineItems();
                for (int i = 0; i < tenders.length; i++)
                {
                    if (tenders[i] instanceof TenderCheck &&
                        ((TenderCheck)tenders[i]).getTypeCode() == TenderLineItemConstantsIfc.TENDER_TYPE_E_CHECK)

                    {
                        String printECheckSignatureCopy = null;
                        String[] EChecklegalStatement = null;
                        TenderCheck tcheck = (TenderCheck)tenders[i];
                        if( (tcheck.getResponseType()!= null && tcheck.getResponseType().equals(TenderCheck.APPROVED)))
                        {

                            try
                            {
                                printECheckSignatureCopy = pm.getStringValue("PrintECheckSignatureSlips");
                                EChecklegalStatement = pm.getStringValues("ECheckSignatureSlip");
                            }
                            catch(ParameterException pe)
                            {
                                logger.warn( 
                                            "Parameter value Check Signature Copy Error");
                            }
                            
                            if (printECheckSignatureCopy != null &&
                                printECheckSignatureCopy.equalsIgnoreCase("Y")) 
                            {
                            	//Changes done for code merging(commenting below lines for error resolving)
                               /* tcheck.setPrintSignatureSlip(true);*/
                            }
                            else
                            {
                            	//Changes done for code merging(commenting below lines for error resolving)
                                /*tcheck.setPrintSignatureSlip(false);*/
                            }
                          //Changes done for code merging(commenting below lines for error resolving)
                            /*Properties props = InternationalTextSupport.getInternationalBeanText
                                                ("receipt",
                                                 UtilityManagerIfc.RECEIPT_BUNDLES);

                            EYSPrintableDocumentIfc ECheckreceipt =
                                new ECheckSignatureSlipReceipt(trans, (TenderCheck)tcheck, 
                                                               header, footer, EChecklegalStatement);

                            ECheckreceipt.setProps(props);
                            pda.printDocument(ECheckreceipt);*/
                        } // END: If E-Check Approval Print Signature Copy Of Receipt
                    } // END: TENDER_TYPE_CHECK
                      
                    else if (tenders[i].getTypeCode() == TenderLineItemIfc.TENDER_TYPE_CHARGE)
                    {
                        TenderCharge tc = (TenderCharge)tenders[i];
                      //Changes done for code merging(commenting below lines for error resolving)
                        /*tc.setSignatureHeight(Integer.parseInt(signatureHeight));
                        tc.setSignatureWidth(Integer.parseInt(signatureWidth));

                        if (sigSlipConfig.equalsIgnoreCase("Y"))
                        {
                            tc.setPrintSignatureSlip(true);
                        }
                        else
                        {
                            tc.setPrintSignatureSlip(false);
                        }

                        if (sigSlipNameConfig.equalsIgnoreCase("Y"))
                        {
                            tc.setPrintNameOnSignatureSlip(true);
                        }
                        else
                        {
                            tc.setPrintNameOnSignatureSlip(false);
                        }*/
                        // pass them to the POSDeviceActions object

                        // get properties for receipt
                      //Changes done for code merging(commenting below lines for error resolving)
                        /*Properties props = InternationalTextSupport.getInternationalBeanText
                                            ("receipt",
                                            UtilityManagerIfc.RECEIPT_BUNDLES);*/

                        // check manual imprint and print signature slip parameters
                        boolean checkManualImprint = false;
                        boolean printSigSlip = false;
                        try
                        {

                            printSigSlip = pm.getStringValue("PrintCreditSignatureSlips").equalsIgnoreCase("Y");
                          //Changes done for code merging(commenting below lines for error resolving)
                            /*if ( pm.getStringValue(manualCreditCardImprintCapture) != null )
                            {
                                checkManualImprint = pm.getStringValue(manualCreditCardImprintCapture).equalsIgnoreCase("Y");
                            }*/
                        }
                        catch( ParameterException pe )
                        {
                            logger.error(
                                       "" + "The requested parameters could not be retrieved." + " " + " The following exception occurred: " + " " + pe.getMessage() + "");
                        }

                        if (printSigSlip)
                        {
                        	//Changes done for code merging(commenting below lines for error resolving)
                            /*EYSPrintableDocumentIfc receipt =
                                       new MAXCreditSignatureSlipReceipt(trans, tc, header, footer, legalStatement, transType);
                            receipt.setManualCreditImprint(checkManualImprint);
                            receipt.setProps(props);
                            pda.printDocument(receipt);*/
                        }
                    }
                }
                trans.setTenderLineItems(tenders);
                // Update printer status
                ui.statusChanged(POSUIManagerIfc.PRINTER_STATUS, POSUIManagerIfc.ONLINE);
            }
          //Changes done for code merging(commenting below lines for error resolving)
            //catch (DeviceException e)
            catch(Exception e){
            {
                logger.warn(
                            "Unable to print signature slip. " + e.getMessage() + "");
                // Update printer status
                ui.statusChanged(POSUIManagerIfc.PRINTER_STATUS, POSUIManagerIfc.OFFLINE);
              //Changes done for code merging(commenting below lines for error resolving)
               /* if (e.getOrigException() != null)
                {
                    logger.warn(
                                "DeviceException.NestedException:\n" + Util.throwableToString(e.getOrigException()) + "");
                }*/

                String msg[] = new String[1];
                UtilityManagerIfc utility =
                  (UtilityManagerIfc) bus.getManager(UtilityManagerIfc.TYPE);
                msg[0] = utility.retrieveDialogText("RetryContinue.PrinterOffline",
                                                    "Printer is offline.");

                DialogBeanModel model = new DialogBeanModel();
                model.setResourceID("RetryContinue");
                model.setType(DialogScreensIfc.RETRY_CONTINUE);
                model.setArgs(msg);
                // display dialog
                ui.showScreen(POSUIManagerIfc.DIALOG_TEMPLATE, model);

                sendMail = false;
            }
        }                                           // end Trans Void
		}
        if (sendMail)
        {
            bus.mail(new Letter("Continue"), BusIfc.CURRENT);
        }
    
    }
	
	// Changes starts for code merging(below methods are added from 12 base to this file)
	protected int determineTransType(TenderableTransactionIfc trans)
    {
        int tt = TransactionIfc.TYPE_UNKNOWN;

        // if the transaction is a sale or return transaction,
        // check to see if it really is an exchange.
        if (trans.getTransactionType() == TransactionIfc.TYPE_SALE ||
            trans.getTransactionType() == TransactionIfc.TYPE_RETURN)
        {
            AbstractTransactionLineItemIfc[] lineItems = ((RetailTransactionIfc)trans).getLineItems();
            boolean saleItems = false;
            boolean returnItems = false;

            // loop through line items
            for (int i = 0; i < lineItems.length; i++)
            {
                if( ((SaleReturnLineItemIfc)lineItems[i]).isReturnLineItem() )
                {
                    returnItems = true;
                }
                else
                {
                    saleItems = true;
                }
            }

            // if there are sale and return line items
            // the transaction is an exchange.
            if(saleItems && returnItems)
            {
                tt = TransactionIfc.TYPE_EXCHANGE;
            }
            else if(saleItems)
            {
                tt = TransactionIfc.TYPE_SALE;
            }
            else if(returnItems)
            {
                tt = TransactionIfc.TYPE_RETURN;
            }
        }
        else
        {
            tt = trans.getTransactionType();
        }
        return tt;
    }
	
	
	// Changes ends for code merging
}
