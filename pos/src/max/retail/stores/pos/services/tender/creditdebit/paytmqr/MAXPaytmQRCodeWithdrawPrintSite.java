/* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
 *      Copyright (c) 2022-2023 MAXHyperMarket, Inc.    All Rights Reserved.   
 * Rev 1.0 		March 29, 2022    Kamlesh Pant   Paytm QR Integration
 * Initial revision.
 * 
 * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
package max.retail.stores.pos.services.tender.creditdebit.paytmqr;

import Paytm.QrDisplay;
import max.retail.stores.domain.paytm.MAXPaytmQRCodeResponse;
import max.retail.stores.domain.paytm.MAXPaytmResponse;
import max.retail.stores.domain.tender.paytmqr.MAXPaytmQRCodeTenderConstants;
import max.retail.stores.pos.receipt.MAXReceiptParameterBeanIfc;
import max.retail.stores.pos.receipt.MAXReceiptTypeConstantsIfc;
import max.retail.stores.pos.services.tender.MAXTenderCargo;
import oracle.retail.stores.common.context.BeanLocator;
import oracle.retail.stores.common.utility.LocaleMap;
import oracle.retail.stores.domain.transaction.TenderableTransactionIfc;
import oracle.retail.stores.domain.utility.LocaleConstantsIfc;
import oracle.retail.stores.foundation.manager.ifc.UIManagerIfc;
import oracle.retail.stores.foundation.manager.parameter.ParameterException;
import oracle.retail.stores.foundation.tour.application.Letter;
import oracle.retail.stores.foundation.tour.gate.Gateway;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.foundation.tour.service.SessionBusIfc;
import oracle.retail.stores.pos.config.bundles.BundleConstantsIfc;
import oracle.retail.stores.pos.manager.ifc.UtilityManagerIfc;
import oracle.retail.stores.pos.receipt.PrintableDocumentException;
import oracle.retail.stores.pos.receipt.PrintableDocumentManagerIfc;
import oracle.retail.stores.pos.services.PosSiteActionAdapter;
import oracle.retail.stores.pos.services.common.CommonLetterIfc;
import oracle.retail.stores.pos.ui.DialogScreensIfc;
import oracle.retail.stores.pos.ui.POSUIManagerIfc;
import oracle.retail.stores.pos.ui.beans.DialogBeanModel;
import oracle.retail.stores.pos.ui.beans.StatusBeanModel;

public class MAXPaytmQRCodeWithdrawPrintSite extends PosSiteActionAdapter{
	
	private static final long serialVersionUID = -609800267867089201L;

	public void arrive(BusIfc bus)
	{
		//System.out.println("MAXPaytmQRCodeWithdrawPrintSite");
		boolean mailLetter = true;
		MAXTenderCargo cargo = (MAXTenderCargo) bus.getCargo();
		POSUIManagerIfc ui = (POSUIManagerIfc) bus.getManager(UIManagerIfc.TYPE);
		try {
			//System.out.println("cargo.getPaytmQRCodeResp()"+cargo.getPaytmQRCodeResp());
			//System.out.println("cargo.getTransaction()"+cargo.getTransaction());
			printPaytmSlip(bus, "WITHDRAW", cargo.getPaytmQRCodeResp(), cargo.getTransaction());
		} catch (PrintableDocumentException e) {
			mailLetter = false;
            logger.error("Unable to print Paytm QR Code WIthdrawal Receipt ", e);
            StatusBeanModel statusModel = new StatusBeanModel();
            // Update printer status
            statusModel.setStatus(POSUIManagerIfc.PRINTER_STATUS, POSUIManagerIfc.OFFLINE);

            UtilityManagerIfc utility = (UtilityManagerIfc)bus.getManager(UtilityManagerIfc.TYPE);

            String msg[] = new String[1];
            msg[0] = utility.retrieveDialogText(BundleConstantsIfc.PRINTER_OFFLINE_TAG,
                    BundleConstantsIfc.PRINTER_OFFLINE);

            DialogBeanModel model = new DialogBeanModel();
            model.setResourceID("RetryContinue");
            model.setType(DialogScreensIfc.RETRY_CONTINUE);
            model.setArgs(msg);
            model.setButtonLetter(DialogScreensIfc.BUTTON_CONTINUE, "Success");
            ui.showScreen(POSUIManagerIfc.DIALOG_TEMPLATE, model);
            
		} catch (ParameterException e) {
				logger.error("Unable to read parameter during Paytm Reversal Receipt ", e);
			}
		
		if (mailLetter)
        {
            bus.mail(new Letter(CommonLetterIfc.SUCCESS), BusIfc.CURRENT);
        }
		
		String uri = Gateway.getProperty("application", "transactionStatusPaytmQRCodeURL", "");
		
		MAXPaytmQRCodeResponse response;
		try {
			response = MAXPaytmQRCodeHelperUtiltiy.checkTransactionStatus(uri, cargo.getPaytmQRCodeResp().getOrderId(), cargo.getTillID(), cargo.getStoreStatus().getStore().getStoreID());
			QrDisplay qrDisplay = new QrDisplay(); 
			String mid = MAXPaytmQRCodeConfig.get(MAXPaytmQRCodeTenderConstants.MID);
			String port = MAXPaytmQRCodeConfig.get(MAXPaytmQRCodeTenderConstants.DEVICEPORT);
			int baudRate = Integer.parseInt(MAXPaytmQRCodeConfig.get(MAXPaytmQRCodeTenderConstants.BAUDRATE));
			int parity = Integer.parseInt(MAXPaytmQRCodeConfig.get(MAXPaytmQRCodeTenderConstants.PARITY));
			int dataBits = Integer.parseInt(MAXPaytmQRCodeConfig.get(MAXPaytmQRCodeTenderConstants.DATABITS));
			int stopBits = Integer.parseInt(MAXPaytmQRCodeConfig.get(MAXPaytmQRCodeTenderConstants.STOPBITS));
			int debugMode = Integer.parseInt(MAXPaytmQRCodeConfig.get(MAXPaytmQRCodeTenderConstants.DEBUGMODE));
			
			Boolean sucessScreen=qrDisplay.showHomeScreen(mid, port,baudRate, parity, dataBits, stopBits, debugMode,response.getRegisterId());
			
		} catch (Exception e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
	}
	
	public void printPaytmSlip(BusIfc bus,String requestType,MAXPaytmQRCodeResponse paytmResponse, TenderableTransactionIfc trans) 
			throws ParameterException,PrintableDocumentException {
				PrintableDocumentManagerIfc pdm = (PrintableDocumentManagerIfc)bus.getManager(PrintableDocumentManagerIfc.TYPE);
				MAXReceiptParameterBeanIfc receipt=(MAXReceiptParameterBeanIfc) BeanLocator.getApplicationBean("application_ReceiptParameterBean");

				receipt.setLocale(LocaleMap.getLocale(LocaleConstantsIfc.RECEIPT));
				receipt.setPaytmQRCodeResponse(paytmResponse);
				receipt.setDocumentType(MAXReceiptTypeConstantsIfc.PAYTMQRCODECHARGESLIP);
				receipt.setTransaction(trans); 
				pdm.printReceipt((SessionBusIfc)bus, receipt);
				
				
				
	}
}
