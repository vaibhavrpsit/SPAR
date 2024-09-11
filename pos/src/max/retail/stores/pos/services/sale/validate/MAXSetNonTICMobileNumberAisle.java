/* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
 * Copyright (c) 2010 MAX Hypermarket India Pvt Ltd.    All Rights Reserved.
 * Rev 1.0        Deepshikha Singh  Change for potential bug in Non TIC customer transaction
 * 
 * 
 * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
package max.retail.stores.pos.services.sale.validate;

import java.util.Vector;

import max.retail.stores.domain.customer.MAXCustomerIfc;
import max.retail.stores.domain.transaction.MAXSaleReturnTransactionIfc;
import max.retail.stores.pos.ui.MAXPOSUIManagerIfc;
import oracle.retail.stores.common.utility.LocaleMap;
import oracle.retail.stores.domain.DomainGateway;
import oracle.retail.stores.domain.customer.CustomerInfoIfc;
import oracle.retail.stores.domain.factory.DomainObjectFactoryIfc;
import oracle.retail.stores.domain.utility.LocaleConstantsIfc;
import oracle.retail.stores.domain.utility.PhoneIfc;
import oracle.retail.stores.foundation.manager.ifc.UIManagerIfc;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.pos.services.PosLaneActionAdapter;
import oracle.retail.stores.pos.services.common.CommonLetterIfc;
import oracle.retail.stores.pos.services.sale.SaleCargoIfc;
import oracle.retail.stores.pos.ui.POSUIManagerIfc;
import oracle.retail.stores.pos.ui.beans.POSBaseBeanModel;
import oracle.retail.stores.pos.ui.beans.PromptAndResponseModel;
/**
 * This site sets NON-TIC Customer's phone number in the transaction.
 * 
 * @author Deepshikha Singh
 * @since 13-06-20115
 * @version 1.0
 */
public class MAXSetNonTICMobileNumberAisle extends PosLaneActionAdapter {

	/**
	 * serialVersionUID long
	 */
	private static final long serialVersionUID = -797841239227465746L;
	public void traverse(BusIfc bus){
		
		SaleCargoIfc saleCargo=(SaleCargoIfc)bus.getCargo();
		MAXSaleReturnTransactionIfc saleReturnTransaction=(MAXSaleReturnTransactionIfc)saleCargo.getTransaction();
		CustomerInfoIfc customerInfo = DomainGateway.getFactory().getCustomerInfoInstance();
		POSUIManagerIfc uiManager=(POSUIManagerIfc)bus.getManager(UIManagerIfc.TYPE);
		setCustomerInfo(customerInfo,uiManager);	
		//If <code>Customer</code> Object is null,set the <code>Customer</code> phone number with <code>CustomerInfo</code> phone number
		if(saleReturnTransaction.getCustomer()==null)   
			setCustomer(saleReturnTransaction,customerInfo);
		saleReturnTransaction.setCustomerInfo(customerInfo);
		//change for rev 1.0:Start
		saleCargo.setCustomerInfo(customerInfo);
		//change for rev 1.0:end
		bus.mail(CommonLetterIfc.OK);
	}
	
	/**
	 * This method retrieves the NON-TIC custonmer's mobile number from the screen and sets it in
	 * <code>CustomerInfoIfc</code> object.
	 * @param customer
	 * @param uiManager
	 */
	public void setCustomerInfo(CustomerInfoIfc customerInfo,POSUIManagerIfc uiManager){
		String mobileNumber;
		PhoneIfc phone=DomainGateway.getFactory().getPhoneInstance();
		POSBaseBeanModel model=(POSBaseBeanModel)uiManager.getModel(MAXPOSUIManagerIfc.NON_TIC_MOBILE_NUMBER);
		PromptAndResponseModel promptResponseModel=model.getPromptAndResponseModel();
		mobileNumber=promptResponseModel.getResponseText();
		promptResponseModel.setResponseText("");
		phone.setPhoneNumber(mobileNumber);
		customerInfo.setPhoneNumber(phone);
		customerInfo.setCustomerInfoType(CustomerInfoIfc.CUSTOMER_INFO_TYPE_PHONE_NUMBER);
		customerInfo.setCustomerInfo(CustomerInfoIfc.CUSTOMER_INFO_TYPE_PHONE_NUMBER, phone.getPhoneNumber());
	}
	
	/**
	 * This method sets the <code>Customer</code> phone number with <code>CustomerInfo</code> phone number.
	 *  
	 * @param saleReturnTransaction
	 * @param customerInfo
	 */
	public void setCustomer(MAXSaleReturnTransactionIfc saleReturnTransaction,CustomerInfoIfc customerInfo){
		DomainObjectFactoryIfc domainObjectFactory = (DomainObjectFactoryIfc)DomainGateway.getFactory();
		MAXCustomerIfc customer=(MAXCustomerIfc)domainObjectFactory.
										getCustomerInstance(LocaleMap
												.getLocale(LocaleConstantsIfc.DEFAULT_LOCALE)
												);
		Vector phoneVector=new Vector();
		phoneVector.add(customerInfo.getPhoneNumber());
		customer.setPhones(phoneVector);
		customer.setCustomerType("L");
		saleReturnTransaction.setCustomer(customer);
	}
}