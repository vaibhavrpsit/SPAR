package max.retail.stores.pos.services.tender;

import max.retail.stores.domain.customer.MAXCustomer;
import max.retail.stores.domain.customer.MAXCustomerIfc;
import max.retail.stores.domain.customer.MAXTICCustomer;
import max.retail.stores.domain.transaction.MAXSaleReturnTransaction;
import oracle.retail.stores.domain.tender.TenderLineItemConstantsIfc;
import oracle.retail.stores.domain.tender.TenderLineItemIfc;
import oracle.retail.stores.domain.transaction.TransactionConstantsIfc;
import oracle.retail.stores.foundation.tour.application.Letter;
import oracle.retail.stores.foundation.tour.gate.Gateway;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.pos.services.PosSiteActionAdapter;
import oracle.retail.stores.pos.services.tender.TenderCargo;
import oracle.retail.stores.pos.ui.DialogScreensIfc;
import oracle.retail.stores.pos.ui.POSUIManagerIfc;
import oracle.retail.stores.pos.ui.beans.DialogBeanModel;

public class MAXCustomerNumberSite extends PosSiteActionAdapter  {
	


	private static final long serialVersionUID = 1L;

	public void arrive(BusIfc bus) {
		
		TenderCargo cargo = (TenderCargo) bus.getCargo();
		MAXSaleReturnTransaction transaction = null;
		String customerID = null;
		
		POSUIManagerIfc ui=(POSUIManagerIfc)bus.getManager(POSUIManagerIfc.TYPE);
		
		/*MAXConfigParametersIfc configParam=getConfigparameter();*/
		
		//changes for rev 1.0 start 
		boolean isIssueCouponEnable=false;
		int maxCustomerItemsPerListSize = Integer.parseInt(Gateway.getProperty(Gateway.APPLICATION_PROPERTIES_GROUP, "IssueCapillaryCoupon", "1"));
		if(maxCustomerItemsPerListSize ==1){
			isIssueCouponEnable = true;
		}
		else if(maxCustomerItemsPerListSize ==0){
			isIssueCouponEnable = false;
		}
		/*if(configParam!=null){
			isIssueCouponEnable=configParam.isIssueCouponEnable();
		}*/
		//changes for rev 1.0 ends 
		if (cargo.getTransType() == TransactionConstantsIfc.TYPE_SALE && isIssueCouponEnable 	//changes for rev 1.1 end 
				&& cargo.getTransaction() != null
				&& cargo.getTransaction() instanceof MAXSaleReturnTransaction) {
			
			MAXCustomerIfc customerIfc=null;
			transaction = (MAXSaleReturnTransaction) cargo.getTransaction();
			
			TenderLineItemIfc[] storeCreditTenderLineItems=transaction.getTenderLineItemArray(TenderLineItemConstantsIfc.TENDER_TYPE_STORE_CREDIT);
			
			if(transaction.getCustomer()!=null && transaction.getCustomer() instanceof MAXCustomer){
				customerIfc=(MAXCustomerIfc)transaction.getCustomer();
			}
			
			if(storeCreditTenderLineItems==null ||(storeCreditTenderLineItems!=null && storeCreditTenderLineItems.length==0)){
								
				if (customerIfc != null
						&& !customerIfc.getCustomerID()
								.equalsIgnoreCase("") && customerIfc.getCustomerType().equalsIgnoreCase("T")) {
								//||customerIfc.getCustomerType().equalsIgnoreCase("L")) {
					customerID = customerIfc.getCustomerID();
				} else if (transaction.getMAXTICCustomer() != null
						&& ((MAXTICCustomer) transaction.getMAXTICCustomer())
								.getTICCustomerID() != null
						&& !((MAXTICCustomer) transaction.getMAXTICCustomer())
								.getTICCustomerID().equalsIgnoreCase("")) {
					customerID = ((MAXTICCustomer) transaction.getMAXTICCustomer()).getTICCustomerID();
				}

			///if customerID is attached go with the capillary request else get Phone Number 	
				if (customerID != null
						&& !customerID.trim().equalsIgnoreCase("")) {
					bus.mail(new Letter("CapillaryRequest"), BusIfc.CURRENT);
				} else {
					displayMobileEnterPrompt(ui);
				}
			} else {
				//Rev	1.1 Defect fix
				bus.mail(new Letter("ExitTender"), BusIfc.CURRENT);
			}
		} else {
			bus.mail(new Letter("ExitTender"), BusIfc.CURRENT);
		}
	}

	/*private MAXConfigParametersIfc getConfigparameter() {

		MAXConfigParameterTransaction configTransaction = new MAXConfigParameterTransaction();
		MAXConfigParametersIfc configParameters = null;
		configTransaction = (MAXConfigParameterTransaction) DataTransactionFactory
				.create(MAXDataTransactionKeys.CONFIG_PARAMETER_TRANSACTION);

		try {
			configParameters = configTransaction.selectConfigParameters();
		} catch (DataException e1) {
			e1.printStackTrace();
		}

		return configParameters;

	}*/

	private void displayStoreCreditError(POSUIManagerIfc ui) {
		DialogBeanModel dialogModel = new DialogBeanModel();
		dialogModel.setResourceID("McouponStoreCreditError");
		dialogModel.setType(DialogScreensIfc.ERROR);
		dialogModel.setArgs(null);
		dialogModel.setButtonLetter(DialogScreensIfc.BUTTON_OK, "ExitTender");
		ui.showScreen(POSUIManagerIfc.DIALOG_TEMPLATE, dialogModel);
	}
	
	private void displayMobileEnterPrompt(POSUIManagerIfc ui) {
		DialogBeanModel dialogModel = new DialogBeanModel();
		dialogModel.setResourceID("McouponMobileEnterPrompt");
		dialogModel.setType(DialogScreensIfc.YES_NO);
		dialogModel.setArgs(null);
		dialogModel.setButtonLetter(DialogScreensIfc.BUTTON_YES, "MobleNumberEnter");
		dialogModel.setButtonLetter(DialogScreensIfc.BUTTON_NO, "No");
		ui.showScreen(POSUIManagerIfc.DIALOG_TEMPLATE, dialogModel);
	}


}
