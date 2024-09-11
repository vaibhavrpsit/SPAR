/* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
 *      Copyright (c) 2022-2023 MAXHyperMarket, Inc.    All Rights Reserved.  
 * Rev 1.0 		Nov 02, 2022    Kamlesh Pant   Mall Certificate Withdraw Print Slip Integration
 * Initial revision.
 * 
 * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
package max.retail.stores.pos.services.printing;

import org.apache.log4j.Logger;

import max.retail.stores.domain.transaction.MAXSaleReturnTransaction;
import max.retail.stores.domain.transaction.MAXSaleReturnTransactionIfc;
import max.retail.stores.pos.receipt.MAXReceiptParameterBeanIfc;
import max.retail.stores.pos.receipt.MAXReceiptTypeConstantsIfc;
import oracle.retail.stores.common.context.BeanLocator;
import oracle.retail.stores.common.utility.LocaleMap;
import oracle.retail.stores.domain.utility.LocaleConstantsIfc;
import oracle.retail.stores.foundation.manager.ifc.ParameterManagerIfc;
import oracle.retail.stores.foundation.manager.ifc.UIManagerIfc;
import oracle.retail.stores.foundation.manager.parameter.ParameterException;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.foundation.tour.service.SessionBusIfc;
import oracle.retail.stores.pos.config.bundles.BundleConstantsIfc;
import oracle.retail.stores.pos.manager.ifc.UtilityManagerIfc;
import oracle.retail.stores.pos.receipt.PrintableDocumentException;
import oracle.retail.stores.pos.receipt.PrintableDocumentManagerIfc;
import oracle.retail.stores.pos.services.PosLaneActionAdapter;
import oracle.retail.stores.pos.ui.DialogScreensIfc;
import oracle.retail.stores.pos.ui.POSUIManagerIfc;
import oracle.retail.stores.pos.ui.beans.DialogBeanModel;
import oracle.retail.stores.pos.ui.beans.StatusBeanModel;

public class MAXMallCertificateReceiptPrintAisle extends PosLaneActionAdapter {
	/**
	 * 
	 */
	private static final long serialVersionUID = 4642970617268274835L;
	private static Logger logger = Logger.getLogger(MAXMallCertificateReceiptPrintAisle.class);

	public void traverse(BusIfc bus)
	{
		boolean mailLetter = true;
	//	MAXTenderCargo cargo = (MAXTenderCargo) bus.getCargo();
		MAXPrintingCargo cargo = (MAXPrintingCargo) bus.getCargo();
		POSUIManagerIfc ui = (POSUIManagerIfc) bus.getManager(UIManagerIfc.TYPE);
		MAXSaleReturnTransaction transaction=null;
		//ArrayList list=new ArrayList();
		final String ENABLE_MALL_CERT_RECEIPT = "MallCertificateAccepted";
		ParameterManagerIfc pm = (ParameterManagerIfc)bus.getManager(ParameterManagerIfc.TYPE);
			
		try {
			Boolean param=pm.getBooleanValue(ENABLE_MALL_CERT_RECEIPT);
				MAXSaleReturnTransaction txn = (MAXSaleReturnTransaction)(cargo.getTransaction());
				
				if(cargo.includesMallCertificate())
				{	
						printMallCertReceiptSlip(bus, "WITHDRAW", txn);
				}
		}
		catch (PrintableDocumentException e) {
			mailLetter = false;
	
            logger.error("Unable to print Mall Certificate Withdrawal Receipt ", e);
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
           // model.setButtonLetter(DialogScreensIfc.BUTTON_CONTINUE, "Success");
            
            ui.showScreen(POSUIManagerIfc.DIALOG_TEMPLATE, model);
       
     
            
		} catch (ParameterException e) {
				logger.error("Unable to print Mall Certificate Withdrawal Receipt ", e);
			}
		
		
		if (mailLetter)
        {
			 bus.mail("ExitPrinting");
        }
		
	}
	
	
	public void printMallCertReceiptSlip(BusIfc bus,String requestType,MAXSaleReturnTransactionIfc trans ) 
			throws ParameterException,PrintableDocumentException {
				PrintableDocumentManagerIfc pdm = (PrintableDocumentManagerIfc)bus.getManager(PrintableDocumentManagerIfc.TYPE);
				MAXReceiptParameterBeanIfc receipt=(MAXReceiptParameterBeanIfc) BeanLocator.getApplicationBean("application_ReceiptParameterBean");

				receipt.setLocale(LocaleMap.getLocale(LocaleConstantsIfc.RECEIPT));
				receipt.setDocumentType(MAXReceiptTypeConstantsIfc.MALLCERTIFICATE);
				receipt.setTransaction(trans); 
		
				pdm.printReceipt((SessionBusIfc)bus, receipt);
	}


}
