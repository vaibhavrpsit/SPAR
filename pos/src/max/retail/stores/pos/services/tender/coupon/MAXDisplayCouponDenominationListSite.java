package max.retail.stores.pos.services.tender.coupon;


import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;

import org.apache.log4j.Logger;

import max.retail.stores.domain.arts.MAXDataTransactionKeys;
import max.retail.stores.domain.arts.MAXHotKeysTransaction;
import max.retail.stores.pos.services.tender.MAXTenderCargo;
import max.retail.stores.pos.ui.MAXPOSUIManagerIfc;
import max.retail.stores.pos.ui.beans.MAXCouponBeanModel;
import max.retail.stores.pos.ui.beans.MAXCouponDenominationBeanModel;
import max.retail.stores.pos.ui.beans.MAXCouponDenominationCountBeanModel;
import oracle.retail.stores.commerceservices.common.currency.CurrencyIfc;
import oracle.retail.stores.domain.DomainGateway;
import oracle.retail.stores.domain.arts.DataTransactionFactory;
import oracle.retail.stores.domain.tender.TenderCoupon;
import oracle.retail.stores.foundation.manager.data.DataException;
import oracle.retail.stores.foundation.manager.ifc.ParameterManagerIfc;
import oracle.retail.stores.foundation.manager.ifc.UIManagerIfc;
import oracle.retail.stores.foundation.manager.parameter.ParameterException;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.pos.ado.tender.TenderCouponADO;
import oracle.retail.stores.pos.manager.ifc.UtilityManagerIfc;
import oracle.retail.stores.pos.services.PosSiteActionAdapter;
import oracle.retail.stores.pos.services.tender.TenderCargo;
import oracle.retail.stores.pos.ui.DialogScreensIfc;
import oracle.retail.stores.pos.ui.POSUIManagerIfc;
import oracle.retail.stores.pos.ui.beans.DataInputBeanModel;
import oracle.retail.stores.pos.ui.beans.DialogBeanModel;
import oracle.retail.stores.pos.ui.beans.NavigationButtonBeanModel;
import oracle.retail.stores.pos.ui.beans.POSBaseBeanModel;
import oracle.retail.stores.pos.ui.beans.PromptAndResponseModel;

public class MAXDisplayCouponDenominationListSite extends PosSiteActionAdapter {

	private static final long serialVersionUID = 7107260166081047694L;
	static final int FOOD_COUPON_TYPE = 1;

	static final int NON_FOOD_COUPON_TYPE = 2;

	public void arrive(BusIfc bus) {
		MAXTenderCargo cargo = (MAXTenderCargo)bus.getCargo();
		ParameterManagerIfc pm = (ParameterManagerIfc) bus.getManager(ParameterManagerIfc.TYPE);
		POSUIManagerIfc ui = (POSUIManagerIfc) bus.getManager(UIManagerIfc.TYPE);
		UtilityManagerIfc utility = (UtilityManagerIfc) bus.getManager(UtilityManagerIfc.TYPE);
		MAXCouponBeanModel couponBeanModel = (MAXCouponBeanModel) ui.getModel(MAXPOSUIManagerIfc.COUPON_LIST);
		String selectedCoupon = couponBeanModel.getSelectedCoupon();
		
		MAXCouponDenominationBeanModel model = null;
		int noOfCouponDeno = 0;
		try {
			Serializable[] parameterValue = pm.getParameterValues("NoOfCouponDenomination");
			if (parameterValue != null && parameterValue.length == 1 && parameterValue[0] instanceof String)
				noOfCouponDeno = Integer.parseInt((String) parameterValue[0]);
		} catch (ParameterException e1) {
			e1.printStackTrace();
		}

		CurrencyIfc currencyArray[] = new CurrencyIfc[noOfCouponDeno];
		if (ui.getModel(MAXPOSUIManagerIfc.COUPON_DENOMINATION) instanceof MAXCouponDenominationBeanModel) {
			model = (MAXCouponDenominationBeanModel) ui.getModel(MAXPOSUIManagerIfc.COUPON_DENOMINATION);
		} else {
			model = new MAXCouponDenominationBeanModel();

			for (int j = 0; j < noOfCouponDeno; j++) {
				Serializable[] parameterValues;
				try {
					parameterValues = pm.getParameterValues("CouponDenomination" + (j + 1));
					if (parameterValues != null && parameterValues.length == 1 && parameterValues[0] instanceof String)
						currencyArray[j] = DomainGateway.getBaseCurrencyInstance((String) parameterValues[0]);
				} catch (ParameterException e) {
					e.printStackTrace();
				}

			}
			model.setCouponName(selectedCoupon);
			if (selectedCoupon != null && selectedCoupon.toString().equalsIgnoreCase("CARRY BAG DISCOUNT COUPON"))
				noOfCouponDeno = 3;

			try {
				Serializable[] foodCouponTypeList = pm.getParameterValues("FoodCouponTypeList");
				Serializable[] nonFoodCouponTypeList = pm.getParameterValues("NonFoodCouponTypeList");
				boolean flag = false;
				for (int i = 0; i < foodCouponTypeList.length; i++) {
					Serializable param = foodCouponTypeList[i];
					if (param instanceof String) {
						if (selectedCoupon.equals(param)) {
							model.setCouponType(FOOD_COUPON_TYPE);
							flag = true;
							break;
						}
					}
				}
				if (!flag) {

					for (int i = 0; i < nonFoodCouponTypeList.length; i++) {
						Serializable param = nonFoodCouponTypeList[i];
						if (selectedCoupon.equals(param)) {
							model.setCouponType(NON_FOOD_COUPON_TYPE);
							break;
						}
					}
				}

			}

			catch (Exception e) {

			}

			ArrayList countModelList = new ArrayList();

			int index = 0;
			for (int j = 0; j < noOfCouponDeno; j++) {
				MAXCouponDenominationCountBeanModel countModel = new MAXCouponDenominationCountBeanModel();
				countModel.setAmount(DomainGateway.getBaseCurrencyInstance());
				countModel.setDescription(currencyArray[j].getStringValue());
				countModel.setFieldDisabled(true);
				countModel.setFieldHidden(false);
				countModel.setLabel(currencyArray[j].getStringValue());
				countModel.setLabelTag(currencyArray[j].getStringValue());
				countModel.setQuantity(0);
				countModel.setTotalAmount(DomainGateway.getBaseCurrencyInstance());
				if (selectedCoupon.toString() != null
						&& selectedCoupon.toString().equalsIgnoreCase("CARRY BAG DISCOUNT COUPON") && j == 2) {
					noOfCouponDeno = j + 1;
					countModelList.add(index++, countModel);
					break;
				}
				countModelList.add(index++, countModel);
			}
			model.setCouponDenominationCountBeanModel(countModelList);
		}
		NavigationButtonBeanModel localButtonBeanModel = null;
		if (selectedCoupon.toString() != null
				&& selectedCoupon.toString().equalsIgnoreCase("CARRY BAG DISCOUNT COUPON")) {
			if (model.getTotal() == null) {
				localButtonBeanModel = getCouponDenominationsModel(utility, pm, logger, 3);
			} else {
				noOfCouponDeno = 0;
				localButtonBeanModel = getCouponDenominationsModel(utility, pm, logger, noOfCouponDeno);
			}
		} else {
			localButtonBeanModel = getCouponDenominationsModel(utility, pm, logger, noOfCouponDeno);
		}
		if (localButtonBeanModel != null)
			model.setLocalButtonBeanModel(localButtonBeanModel);
		NavigationButtonBeanModel globalButtonBeanModel = new NavigationButtonBeanModel();
		BigDecimal zeroDecimal = new BigDecimal("0.00");
		PromptAndResponseModel promptAndResponseModel = model.getPromptAndResponseModel();
		if (promptAndResponseModel == null)
			promptAndResponseModel = new PromptAndResponseModel();
		if (selectedCoupon.toString() != null
				&& selectedCoupon.toString().equalsIgnoreCase("CARRY BAG DISCOUNT COUPON")) {
			promptAndResponseModel.setResponseEditable(false);
			promptAndResponseModel.setResponseText("1");
		} else {
			promptAndResponseModel.setResponseText("");
		}
		
		//changes by shyvanshu mehra
		
		model.setPromptAndResponseModel(promptAndResponseModel);
		DialogBeanModel model2 = new DialogBeanModel();
		if (selectedCoupon.equalsIgnoreCase("parking"))
		{
			if(!(cargo.getCouponName()!=null && (cargo.getCouponName().equalsIgnoreCase("parking"))))
			{	
				String parCou=null;
				try {
					parCou = (pm.getStringValue("ParkingCouponValidation"));
				} catch (ParameterException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				CurrencyIfc parkingCoupon = DomainGateway.getBaseCurrencyInstance(parCou);
				//System.out.println("179 ======="+parkingCoupon);
				//System.out.println("180 ======="+cargo.getTransaction().getTenderTransactionTotals().getBalanceDue());
				//System.out.println("181 ======="+parkingCoupon);
				 if(cargo.getTransaction().getTenderTransactionTotals().getSubtotal()!=null &&
						  cargo.getTransaction().getTenderTransactionTotals().getSubtotal().compareTo(parkingCoupon)==1)
						  {
				DataInputBeanModel model1 =new DataInputBeanModel();
				
				POSBaseBeanModel beanmodel = (POSBaseBeanModel) ui.getModel("MCOUPON_PHONE_NUMBER");
				String k = ui.getInput();
				int customerDetails ;
				
				//cargo.getCustomerInfo().getPhoneNumber().getPhoneNumber()== cargo.getTransaction().getCustomerInfo().getPhoneNumber().getPhoneNumber()
			
				if(k!=null || !(cargo.getCustomerInfo().getPhoneNumber().getPhoneNumber().equals("")) )
				{
					if(k!=null)
					{
						cargo.getCustomerInfo().getPhoneNumber().setPhoneNumber(k);
					}
					if(cargo.getCustomerInfo().getPhoneNumber().getPhoneNumber()!=null)
					{
						MAXHotKeysTransaction hotKeysTransaction = (MAXHotKeysTransaction) DataTransactionFactory
								.create(MAXDataTransactionKeys.MAX_HOT_KEYS_LOOKUP_TRANSACTION);
						try {
							cargo.getTransaction().setCustomerInfo(cargo.getCustomerInfo());
							customerDetails = ((MAXHotKeysTransaction) hotKeysTransaction)
									.getParkingCertTransactionDetails(cargo.getTransaction());
							if(customerDetails>0)
							{
								String[] msg = new String[4];
								//msg[0] = String.valueOf(transaction.getBeertot()); 
								msg[1] = "ParkingCouponAlreadyRedeemed"; 
								//msg[2] =parameterManager.getStringValue("BeerLiqureTotal"); 
								msg[3] ="Press Enter to Select Different Tender Mode."; 
								model2.setArgs(msg);
								model2.setResourceID("ParkingCouponNotValid");
								model2.setType(DialogScreensIfc.ACKNOWLEDGEMENT);
								model2.setButtonLetter(DialogScreensIfc.BUTTON_OK, "Failure");
								ui.showScreen(POSUIManagerIfc.DIALOG_TEMPLATE, model2);
								//beertot = beertot - value;
								//transaction.setBeertot(beertot);
								return;
								//bus.mail("Ok");
							}
							else
							{
								POSUIManagerIfc uiManager=(POSUIManagerIfc)bus.getManager(UIManagerIfc.TYPE);
								//model.setPromptAndResponseModel(promptAndResponseModel);
								uiManager.showScreen("PARKING_COUPON_SCREEN", model1);
							}
						} catch (DataException e) {
							logger.warn(e.getMessage());
						}
					}
				}
				else if(cargo.getCustomerInfo().getPhoneNumber().getPhoneNumber()==null || cargo.getCustomerInfo().getPhoneNumber().getPhoneNumber().equals(""))
			{
					bus.mail("Yes");
			}
				
				else
				{
					POSUIManagerIfc uiManager=(POSUIManagerIfc)bus.getManager(UIManagerIfc.TYPE);
					//model.setPromptAndResponseModel(promptAndResponseModel);
					uiManager.showScreen("PARKING_COUPON_SCREEN", model1);
				}
				//bus.mail("No");
			}
				 else
				 {
					 String[] msg = new String[4];
					 
					msg[1] = "Total Tender Amount is less than 250 "; 
					msg[2] = "Can't Redeem Parking Coupon "; 
					msg[3] ="Press Enter to Return Previous Screen."; 
					model2.setArgs(msg);
					model2.setResourceID("ParkingCouponlimit");
					model2.setType(DialogScreensIfc.ACKNOWLEDGEMENT);
					model2.setButtonLetter(DialogScreensIfc.BUTTON_OK, "Failure");
					ui.showScreen(POSUIManagerIfc.DIALOG_TEMPLATE, model2);
					
					return;
				//bus.mail("Failure");
				 }
			}
			else
			{
				 String[] msg = new String[4];
				 
				/*
				 * msg[1] = "Total Tender Amount is less than 250 "; msg[2] = "Already Use ";
				 * msg[3] ="Press Enter to Return Previous Screen."; model2.setArgs(msg);
				 */
					model2.setResourceID("ParkingErrorMessage");
					
					//change this dialogbox
					model2.setType(DialogScreensIfc.ACKNOWLEDGEMENT);
					model2.setButtonLetter(DialogScreensIfc.BUTTON_OK, "Failure");
					ui.showScreen(POSUIManagerIfc.DIALOG_TEMPLATE, model2);
					
					return;
			}
	}
		else
		{
			ui.showScreen("COUPON_DENOMINATION", model);
		}
		
		
	}

	public void depart(BusIfc bus) {
		super.depart(bus);
	}

	public static NavigationButtonBeanModel getCouponDenominationsModel(
			UtilityManagerIfc utility, ParameterManagerIfc pm, Logger logger,
			int noOfDeno) {
		NavigationButtonBeanModel localNavigationButtonBeanModel = new NavigationButtonBeanModel();
		noOfDeno=noOfDeno+1;
		try {
			int buttonCounter=2;



			for (int i = 0; i < noOfDeno; i++) {
				String buttonLabelParm ="";
				if(i!=13){
					buttonLabelParm = pm.getStringValue("CouponDenomination"+(i+1));



					localNavigationButtonBeanModel.addButton("Currency"+(i+1),
							buttonLabelParm ,true, null, "F"+buttonCounter, null);
					localNavigationButtonBeanModel.setButtonLabel(
							"Currency"+(i+1), buttonLabelParm);
				}
				if(i==13){ localNavigationButtonBeanModel.addButton("UserDefined",buttonLabelParm ,true, null, "F"+buttonCounter, null);
				localNavigationButtonBeanModel.setButtonLabel(
						"UserDefined", "UserDefined");
				} buttonCounter++;
				if(buttonCounter>=9)
					buttonCounter=2;
			}
		} catch (ParameterException pe) {
			logger.warn(pe.getMessage());
		}
		if(noOfDeno == 0) 
			localNavigationButtonBeanModel.addButton("Currency2", "0.00", false, null, "F2", null);
		      return localNavigationButtonBeanModel;
		      }
	
}
