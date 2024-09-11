/********************************************************************************
 *   
 *	Copyright (c) 2019 MAX SPAR Hypermarket, Inc    All Rights Reserved.
 *	
 *	Rev	1.0 	June 01, 2019		Purushotham Reddy 	Changes for POS_Amazon Pay Integration
 *  Rev	2.0 	Nov 23, 2020		Kumar Vaibhav 	    Changes for POS_Amazon Pay Barcode Integration 
 *
 ********************************************************************************/

package max.retail.stores.pos.services.tender.wallet.amazonpay;

/**
@author Purushotham Reddy Sirison
**/

import max.retail.stores.domain.customer.MAXCustomer;
import max.retail.stores.domain.customer.MAXCustomerIfc;
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

public class MAXAmazonPayRequestInfoAisle extends LaneActionAdapter
{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private static Logger logger = Logger.getLogger(max.retail.stores.pos.services.tender.wallet.paytm.MAXPaytmRequestInfoAisle.class);
	
	public void traverse(BusIfc bus)
	{
		
		POSUIManagerIfc uiManager=(POSUIManagerIfc)bus.getManager(UIManagerIfc.TYPE);
		DataInputBeanModel model=(DataInputBeanModel)uiManager.getModel(MAXPOSUIManagerIfc.ENTER_AMAZON_PAY_MOBILE_NUMBER_SCREEN);

		String amazonPayMobileNumber=(String)model.getValueAsString("MobileNumberField");
		/*Rev	2.0 Kumar Vaibhav*/
		String barcode = (String)model.getValueAsString("BarcodeField");
		/*end*/
		//String amount=(String)model.getValueAsString("AmountField");
		

		MAXTenderCargo cargo=(MAXTenderCargo)bus.getCargo();
		cargo.setAmazonPayPhoneNumber(amazonPayMobileNumber);
		/*Rev	2.0 Kumar Vaibhav*/
		cargo.setBarcode(barcode);
		
		/*end*/
		//cargo.setPaytmTotp(amount);
		MAXCustomerIfc customer=null;

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
 								if(tenderChargeObj.getPaytmMobileNumber().equals(amazonPayMobileNumber))
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
		
		bus.mail(new Letter("AmazonPaySuccess"), BusIfc.CURRENT);
	}

}
