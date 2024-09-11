package max.retail.stores.pos.services.tender.wallet.mobikwik;

import oracle.retail.stores.common.utility.LocaleMap;






import max.retail.stores.domain.MAXMobikwikResponse;
import max.retail.stores.domain.MAXPaytmResponse;
import max.retail.stores.pos.receipt.MAXReceiptParameterBeanIfc;
import max.retail.stores.pos.receipt.MAXReceiptTypeConstantsIfc;
import max.retail.stores.pos.services.tender.MAXTenderCargo;
import oracle.retail.stores.common.context.BeanLocator;
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


public class MAXMobikwikWithdrawPrintSite extends PosSiteActionAdapter{
	
	private static final long serialVersionUID = -609800267867089201L;

	public void arrive(BusIfc bus)
	{
		boolean mailLetter = true;
		MAXTenderCargo cargo = (MAXTenderCargo) bus.getCargo();
		POSUIManagerIfc ui = (POSUIManagerIfc) bus.getManager(UIManagerIfc.TYPE);
		try {
			printMobikwikSlip(bus, "WITHDRAW", cargo.getMobikwikResp(), cargo.getTransaction());
		} catch (PrintableDocumentException e) {
			mailLetter = false;
            logger.error("Unable to print Mobikwik WIthdrawal Receipt ", e);
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
				logger.error("Unable to read parameter during Mobikwik Reversal Receipt ", e);
			}
		
		if (mailLetter)
        {
            bus.mail(new Letter(CommonLetterIfc.SUCCESS), BusIfc.CURRENT);
        }
	}
	
	public void printMobikwikSlip(BusIfc bus,String requestType,MAXMobikwikResponse mobikResponse, TenderableTransactionIfc trans) 
			throws ParameterException,PrintableDocumentException {
				PrintableDocumentManagerIfc pdm = (PrintableDocumentManagerIfc)bus.getManager(PrintableDocumentManagerIfc.TYPE);
				MAXReceiptParameterBeanIfc receipt=(MAXReceiptParameterBeanIfc) BeanLocator.getApplicationBean("application_ReceiptParameterBean");

				receipt.setLocale(LocaleMap.getLocale(LocaleConstantsIfc.RECEIPT));
				receipt.setMobikwikResponse(mobikResponse);
				receipt.setDocumentType(MAXReceiptTypeConstantsIfc.MOBIKWIKCHARGESLIP);
				receipt.setTransaction(trans); 
				
				pdm.printReceipt((SessionBusIfc)bus, receipt);
	}
}
