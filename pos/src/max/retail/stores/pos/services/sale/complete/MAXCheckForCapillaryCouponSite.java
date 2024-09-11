/** * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
  *Copyright (c) 2012 - 2013 MAXHyperMarket, Inc.    All Rights Reserved.
  *
 * Rev. 1.0	 Deepshikha		08/06/2015		Initial Draft: Changes for Capillary Coupon
 * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
package max.retail.stores.pos.services.sale.complete;

import java.util.HashMap;
import java.util.Vector;

import max.retail.stores.domain.customer.MAXCustomerIfc;
import max.retail.stores.domain.customer.MAXTICCustomer;
import max.retail.stores.domain.discountCoupon.MAXDiscountCouponIfc;
import max.retail.stores.domain.transaction.MAXSaleReturnTransactionIfc;
import max.retail.stores.pos.services.capillary.MAXCapillaryHelperUtility;
import oracle.retail.stores.domain.customer.CustomerInfoIfc;
import oracle.retail.stores.domain.transaction.SaleReturnTransactionIfc;
import oracle.retail.stores.domain.utility.PhoneIfc;
import oracle.retail.stores.foundation.manager.ifc.UIManagerIfc;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.pos.services.PosSiteActionAdapter;
import oracle.retail.stores.pos.services.sale.SaleCargoIfc;
import oracle.retail.stores.pos.ui.DialogScreensIfc;
import oracle.retail.stores.pos.ui.POSUIManagerIfc;
import oracle.retail.stores.pos.ui.beans.DialogBeanModel;

public class MAXCheckForCapillaryCouponSite extends PosSiteActionAdapter{

	/**This Site is used For Sending Redeem request to Capillary
	 */
	private static final long serialVersionUID = 1L;

	public void arrive(BusIfc bus) {
		String mobileNumber = ""; 
		String external_id="";
		String customertype ="";
		String BillNumber="";
		String couponNumber="";
		String resourceID="";
		MAXCustomerIfc customer=null;
		String[] msg = new String[2];	//AAKASH
		String transactionAmt;
		SaleCargoIfc cargo=(SaleCargoIfc)bus.getCargo();
		if(cargo.getTransaction()instanceof SaleReturnTransactionIfc)
		{
			MAXSaleReturnTransactionIfc transaction=(MAXSaleReturnTransactionIfc)cargo.getTransaction();
			Vector capillaryCouponVector=transaction.getCapillaryCouponsApplied();
			if(capillaryCouponVector.size()!=0){
				if(transaction instanceof MAXSaleReturnTransactionIfc){
					if(transaction.getCustomer() instanceof MAXCustomerIfc){
					 customer=(MAXCustomerIfc) transaction.getCustomer();
					external_id=customer.getCustomerID();
					customertype=customer.getCustomerType();
					}
				}
				POSUIManagerIfc uiManager = (POSUIManagerIfc) bus
				.getManager(UIManagerIfc.TYPE);
				CustomerInfoIfc phones= (CustomerInfoIfc)transaction.getCustomerInfo();
				if(phones!=null&& phones instanceof CustomerInfoIfc){
					PhoneIfc phone=cargo.getCustomerInfo().getPhoneNumber();
					// Changes starts for code merging(commenting below line)
					//mobileNumber = phone.getAreaCode()+phone.getPhoneNumber();
					mobileNumber = phone.getPhoneNumber();
					// Changes ends for code emrging
					
					//If customer is linked in transaction then get phone number
				}

				BillNumber=transaction.getTransactionID().substring(5);
				transactionAmt=transaction.getTransactionTotals().getSubtotal().toString();
				HashMap request = new HashMap();
				
				if(customer!=null&&customertype!=null&&customertype.equalsIgnoreCase("T")){ 
					
					if(customer.getCustomerID()!=null &&(!(customer.getCustomerID().trim().equalsIgnoreCase("")))){
						external_id = customer.getCustomerID();
					}
					else if(customer instanceof MAXTICCustomer){
						external_id = ((MAXTICCustomer)customer).getTICCustomerID();
					} 
				request.put("customer id", external_id);  
				//Rev 1.2                             //If Tic customer linked take CustmerID else mobileNo

				}
				else{

					request.put("mobile number", mobileNumber);   
				}
				//Rev 1.4 start
				
				/*MAXCapillaryCRM capCRM = new MAXCapillaryCRM();
				capCRM.setRequestAction(MAXUtilityConstantsIfc.CRM_CAPILLARY_ACTION_COUPON_REDEEM); //set coupon request type
                                */
                                //Rev 1.4 End

				if(capillaryCouponVector!=null){
					Object coupons[]=capillaryCouponVector.toArray();
					for(int i=0;i<coupons.length;i++){
						couponNumber=((MAXDiscountCouponIfc)coupons[i]).getCouponNumber();
							
						request.put("transaction amount", transactionAmt);   
						request.put("bill number", BillNumber); 		
						request.put("coupon number", couponNumber);
						//Rev 1.4 start
						//capCRM.setTransData(request);
						//MAXConnectCapillaryCRM connectCapCRM = new MAXConnectCapillaryCRM();
						//capCRM=connectCapCRM.processCRMRequest(capCRM); 
						//send request to Capillary
						//String rescode=capCRM.getResponseCode();
						HashMap responseMap = new HashMap();
						MAXCapillaryHelperUtility coupon = new MAXCapillaryHelperUtility();
						boolean capCRM=coupon.redeemCoupon(request, responseMap);
						String ResponseCode="";
						String Responsemsg="";
						String ItemStatusCode="";
						String ItemStatusMessage="";
						if(responseMap.get("Response Code")!=null&&responseMap.get("Response Code")!="")
						{
						 ResponseCode= responseMap
						.get("Response Code").toString();
						}
						if(responseMap.get("Response Message")!=null&&responseMap.get("Response Message")!="")
						{
							 Responsemsg=responseMap
							.get("Response Message").toString();
						}
						if(responseMap.get("ItemStatusCode")!=null&&responseMap.get("ItemStatusCode")!="")
						{
							 ItemStatusCode=responseMap
							.get("ItemStatusCode").toString();
						}
						if(responseMap.get("ItemStatusMessage")!=null&&responseMap.get("ItemStatusMessage")!="")
						{
							ItemStatusMessage=responseMap
							.get("ItemStatusMessage").toString();
						}
						//Changes for Rev 1.0:Start
						msg[0]=couponNumber;
						/*Changes for Rev 1.3:Start*/
						//if(capCRM){	
						/*Changes for Rev 1.3:End*/	

						if ((ResponseCode.equals("200"))
								|| ResponseCode.equals("500")) {

							if (ItemStatusCode.equals("700")) {

							msg[1]="successfully";
							((MAXDiscountCouponIfc)coupons[i]).setRedeemstatus(true);
							resourceID=	"CouponRedemption";
							displaySucessMessage(resourceID,uiManager,  msg);
							}
							else{
							msg[1]="not";
							((MAXDiscountCouponIfc)coupons[i]).setRedeemstatus(false);
							resourceID="CouponRedemption";
							displayErrorMessage(resourceID,uiManager,msg);
						}
					}
						else {

							msg[1] = Responsemsg.toString();
							//Rev 1.4 End
							resourceID = "CapillaryOffline";
							displayofflineMessage(resourceID,uiManager,msg);
							return;
						}
				logger.info("capillary request ========:: "+request);
				logger.info("capillary response ========:: "+responseMap);
				bus.mail("Continue", BusIfc.CURRENT); 				
				}
			}
			bus.mail("Continue", BusIfc.CURRENT); //pos hangs in normal transaction
		}else{
			bus.mail("Continue", BusIfc.CURRENT); 
		}
	}
	}
	private void displayofflineMessage(String resourceID,
			POSUIManagerIfc uiManager, String[] msg) {
		String letter="Continue";
		DialogBeanModel dialogModel=new DialogBeanModel();
		dialogModel.setResourceID(resourceID);
		dialogModel.setArgs(msg);
		dialogModel.setType(DialogScreensIfc.ERROR);
		dialogModel.setButtonLetter(DialogScreensIfc.BUTTON_OK, letter);
		uiManager.showScreen(POSUIManagerIfc.DIALOG_TEMPLATE, dialogModel);
		
	}
	private void displayErrorMessage(String resourceID,POSUIManagerIfc uiManager ,String[] capCRMerrormsg) {
		DialogBeanModel dialogModel=new DialogBeanModel();
		dialogModel.setResourceID(resourceID);
		dialogModel.setArgs(capCRMerrormsg);
		dialogModel.setType(DialogScreensIfc.ACKNOWLEDGEMENT);
		uiManager.showScreen(POSUIManagerIfc.DIALOG_TEMPLATE, dialogModel);
	}

	private void displaySucessMessage(String resourceID, 
			POSUIManagerIfc uiManager ,String[] capCRMerrormsg) {
		DialogBeanModel dialogModel=new DialogBeanModel();
		dialogModel.setResourceID(resourceID);
		dialogModel.setArgs(capCRMerrormsg);
		dialogModel.setType(DialogScreensIfc.ACKNOWLEDGEMENT);
		uiManager.showScreen(POSUIManagerIfc.DIALOG_TEMPLATE, dialogModel);
	}	
}