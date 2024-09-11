/********************************************************************************
 *   
 *	Copyright (c) 2019 MAX SPAR Hypermarket, Inc    All Rights Reserved.
 *	
 *	Rev	1.0 	June 16, 2019		Purushotham Reddy 	Changes for POS-Amazon Pay Integration 
 *
 ********************************************************************************/

package max.retail.stores.pos.services.tender.wallet.amazonpay;

/**
@author Purushotham Reddy Sirison
**/

import max.retail.stores.domain.MAXAmazonPayResponse;
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


public class MAXPrintAmazonPayReceiptSite extends PosSiteActionAdapter{
	
	private static final long serialVersionUID = -609800267867089201L;

	public void arrive(BusIfc bus)
	{
		boolean mailLetter = true;
		MAXTenderCargo cargo = (MAXTenderCargo) bus.getCargo();
		POSUIManagerIfc ui = (POSUIManagerIfc) bus.getManager(UIManagerIfc.TYPE);
		try {
			printAmazonPaySlip(bus, "WITHDRAW", cargo.getAmazonPayResp(), cargo.getTransaction());
		} catch (PrintableDocumentException e) {
			mailLetter = false;
            logger.error("Unable to print Paytm WIthdrawal Receipt ", e);
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
	}
	
	public void printAmazonPaySlip(BusIfc bus,String requestType, MAXAmazonPayResponse amazonPayResponse, 
			TenderableTransactionIfc trans) 
			throws ParameterException,PrintableDocumentException {
				PrintableDocumentManagerIfc pdm = (PrintableDocumentManagerIfc)bus.getManager(PrintableDocumentManagerIfc.TYPE);
				MAXReceiptParameterBeanIfc receipt=(MAXReceiptParameterBeanIfc) BeanLocator.getApplicationBean("application_ReceiptParameterBean");
				receipt.setLocale(LocaleMap.getLocale(LocaleConstantsIfc.RECEIPT));
				receipt.setAmazonPayResponse(amazonPayResponse);
				receipt.setDocumentType(MAXReceiptTypeConstantsIfc.AMAZONPAYCHARGESLIP);
				receipt.setTransaction(trans); 
				pdm.printReceipt((SessionBusIfc)bus, receipt);
	}
}
