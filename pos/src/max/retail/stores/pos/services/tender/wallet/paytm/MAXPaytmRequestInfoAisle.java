/********************************************************************************
 *   
 *	Copyright (c) 2016 - 2017 MAX Hypermarket, Inc    All Rights Reserved.
 *	
 *
 *	Rev	1.0 	Jan 06, 2017		Atul Shukla		Changes for pos Paytm Integration FES
 *
 ********************************************************************************/

package max.retail.stores.pos.services.tender.wallet.paytm;

import max.retail.stores.domain.customer.MAXCustomer;
import max.retail.stores.domain.customer.MAXCustomerIfc;
import max.retail.stores.domain.tender.MAXTenderChargeIfc;
import max.retail.stores.domain.tender.MAXTenderPaytmIfc;
import max.retail.stores.domain.tender.paytm.MAXPaytmTenderConstants;
import max.retail.stores.domain.transaction.MAXSaleReturnTransaction;
import max.retail.stores.pos.ado.tender.MAXTenderPaytmADO;
import max.retail.stores.pos.services.tender.MAXTenderCargo;
import max.retail.stores.pos.ui.MAXPOSUIManagerIfc;
import oracle.retail.stores.foundation.manager.ifc.UIManagerIfc;
import oracle.retail.stores.foundation.tour.application.LaneActionAdapter;
import oracle.retail.stores.foundation.tour.application.Letter;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.pos.ado.lineitem.TenderLineItemCategoryEnum;
import oracle.retail.stores.pos.ado.tender.TenderADOIfc;
import oracle.retail.stores.pos.ui.DialogScreensIfc;
import oracle.retail.stores.pos.ui.POSUIManagerIfc;
import oracle.retail.stores.pos.ui.beans.DataInputBeanModel;
import oracle.retail.stores.pos.ui.beans.DialogBeanModel;

import org.apache.log4j.Logger;

public class MAXPaytmRequestInfoAisle extends LaneActionAdapter
{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private static Logger logger = Logger.getLogger(max.retail.stores.pos.services.tender.wallet.paytm.MAXPaytmRequestInfoAisle.class);
	
	public void traverse(BusIfc bus)
	{
		
		POSUIManagerIfc uiManager=(POSUIManagerIfc)bus.getManager(UIManagerIfc.TYPE);
		DataInputBeanModel model=(DataInputBeanModel)uiManager.getModel(MAXPOSUIManagerIfc.ENTER_WALLET_MOBILE_NUMBER_AND_TOTP);
		//PromptAndResponseModel prModel=(PromptAndResponseModel)model.getPromptAndResponseModel();
	//	String totp=prModel.getResponseTex
		String paytm_mobile_nuber=(String)model.getValueAsString("MobileNumberField");
		String totp=(String)model.getValueAsString("TotpCodeField");
		
		//System.out.println("mobile Number="+mobile_nuber+"TOTP"+MobikwikTotp);
			
	//	int totp1=Integer.parseInt(totp);
	//	System.out.println("value of toptp="+totp1);
	//	prModel.setResponseText("");
		MAXTenderCargo cargo=(MAXTenderCargo)bus.getCargo();
		cargo.setPaytmPhoneNumber(paytm_mobile_nuber);
		cargo.setPaytmTotp(totp);
		MAXCustomerIfc customer=null;
	//	cargo.getCurrentTransactionADO().toLegacy()
		//MAXSaleReturnTransaction trx=(MAXSaleReturnTransaction)cargo.getTransaction();
		MAXSaleReturnTransaction trx=(MAXSaleReturnTransaction)cargo.getTransaction();
		//customer=(MAXCustomerIfc)trx.getCustomer();
		if(trx.getCustomer() != null) 
		{
			customer=(MAXCustomerIfc)trx.getCustomer();
		//customer.setMobikwikTotp(totp1);
		}else if(trx.getTicCustomer() !=null)
		{
			customer=(MAXCustomerIfc)trx.getTicCustomer();
		}else
		{
			customer=new MAXCustomer();
			
		}
		// below code is added by atul shukla for same mobile number used dialog
		 if(cargo.getCurrentTransactionADO() != null && 
         		cargo.getCurrentTransactionADO().getTenderLineItems(TenderLineItemCategoryEnum.ALL) != null && 
         		cargo.getCurrentTransactionADO().getTenderLineItems(TenderLineItemCategoryEnum.ALL).length != 0)
         {
         	TenderADOIfc[] tenderVector = cargo.getCurrentTransactionADO().getTenderLineItems(TenderLineItemCategoryEnum.ALL);
 			if (tenderVector != null && tenderVector.length > 0)
 			{
 				for (int i=0; i < tenderVector.length; i++)
 				{
 					TenderADOIfc tenderObject = tenderVector[i];
 					if (tenderObject instanceof MAXTenderPaytmADO)
 					{
 						//tenderObject.toLegacy();
 							//MAXTenderChargeIfc tenderChargeObj = (MAXTenderChargeIfc) tenderObject.toLegacy();
 						MAXTenderPaytmIfc tenderChargeObj = (MAXTenderPaytmIfc) tenderObject.toLegacy();
 						//	if (tenderChargeObj.getCardType().equalsIgnoreCase("PAYTM"))
 							//{
 								if(tenderChargeObj.getPaytmMobileNumber().equals(paytm_mobile_nuber))
 								{
 									DialogBeanModel dialogModel = new DialogBeanModel();
 									dialogModel.setResourceID(MAXPaytmTenderConstants.PAYTMSAMEWALLETERROR);
 									dialogModel.setType(DialogScreensIfc.ERROR);
 									uiManager.showScreen(POSUIManagerIfc.DIALOG_TEMPLATE, dialogModel);
 									
 									return;
 								}
 							//}
 					 }
 				 }
 						
 			}
         }
		//customer.setMobikwikTotp(totp1);
//	String mobikwikCustomerPhone="8826094620";
	
	//	Phone mphone=(Phone)mobikwikCustomerPhone;
		//PhoneIfc phone=new Phone(
		//customer.setMobikwikCustomerPhoneNumber(mobikwikCustomerPhone);
	//	customer.setc
		//trx.setCustomer(customer);
		//cargo.setTenderAttributes(tenderAttributes);
		
		//System.out.println(((MAXCustomerIfc)(((MAXSaleReturnTransaction)cargo.getTransaction()).getTicCustomer());
		// cargo.getTenderAttributes().put(MAXTenderConstants.PAYTM_MOBILE_NUMBER, paytm_mobile_nuber);
		// cargo.getTenderAttributes().put(MAXTenderConstants.PAYTM_TOTP,totp);
		bus.mail(new Letter("TotpSuccess"), BusIfc.CURRENT);
		logger.debug("AKS letter TotpSuccess fired");
	}

}
