package max.retail.stores.pos.services.tender.sbi;

import java.util.HashMap;

import max.retail.stores.pos.ado.tender.MAXTenderConstants;
import max.retail.stores.pos.ado.tender.MAXTenderTypeEnum;
import max.retail.stores.pos.services.tender.MAXTenderCargo;
import max.retail.stores.pos.ui.MAXPOSUIManagerIfc;
import oracle.retail.stores.commerceservices.common.currency.CurrencyIfc;
import oracle.retail.stores.domain.DomainGateway;
import oracle.retail.stores.foundation.manager.ifc.ParameterManagerIfc;
import oracle.retail.stores.foundation.manager.ifc.UIManagerIfc;
import oracle.retail.stores.foundation.manager.parameter.ParameterException;
import oracle.retail.stores.foundation.tour.application.Letter;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.pos.ado.tender.TenderConstants;
import oracle.retail.stores.pos.ado.tender.TenderErrorCodeEnum;
import oracle.retail.stores.pos.ado.tender.TenderException;
import oracle.retail.stores.pos.ado.tender.TenderTypeEnum;
import oracle.retail.stores.pos.services.PosSiteActionAdapter;
import oracle.retail.stores.pos.services.common.CommonLetterIfc;
import oracle.retail.stores.pos.services.tender.TenderCargo;
import oracle.retail.stores.pos.ui.DialogScreensIfc;
import oracle.retail.stores.pos.ui.POSUIManagerIfc;
import oracle.retail.stores.pos.ui.beans.DialogBeanModel;
import oracle.retail.stores.pos.ui.beans.POSBaseBeanModel;
import oracle.retail.stores.pos.ui.beans.PromptAndResponseModel;

public class MAXValidateLoyaltyPointsAmountSite extends PosSiteActionAdapter {
	// ----------------------------------------------------------------------
	/**
	 * serialVersionUID long
	 */
	// ----------------------------------------------------------------------
	private static final long serialVersionUID = 7380631034386370683L;
	// ----------------------------------------------------------------------
	/**
	 * revision number
	 */
	// ----------------------------------------------------------------------
	public static final String revisionNumber = "$Revision: 1.2 $";

	// --------------------------------------------------------------------------
	/**
	 * @param bus
	 *            the bus arriving at this site
	 */
	// --------------------------------------------------------------------------
	public void arrive(BusIfc bus) {

		CurrencyIfc enteredAmount = DomainGateway.getBaseCurrencyInstance();
		CurrencyIfc capillaryAmount = DomainGateway.getBaseCurrencyInstance();
		POSUIManagerIfc ui = (POSUIManagerIfc) bus.getManager(UIManagerIfc.TYPE);
		ParameterManagerIfc pm = (ParameterManagerIfc) bus.getManager(ParameterManagerIfc.TYPE);
		TenderCargo cargo = (TenderCargo) bus.getCargo();
		HashMap tenderAttributes = cargo.getTenderAttributes();
		tenderAttributes.put(TenderConstants.TENDER_TYPE, MAXTenderTypeEnum.LOYALTY_POINTS);
		System.out.println("tenderAttributes :"+tenderAttributes);
		String amt = "0.00";
		if(bus.getCurrentLetter().getName().equalsIgnoreCase("Converted") && ((MAXTenderCargo) cargo).getSbiPointResp()!= null) {
			enteredAmount = ((MAXTenderCargo) cargo).getSbiPointResp().getConvertedAmt();
			capillaryAmount = ((MAXTenderCargo) cargo).getSbiPointResp().getConvertedAmt();
			amt = enteredAmount.toString();
		}else if(bus.getCurrentLetter().getName().equalsIgnoreCase("Total") && ((MAXTenderCargo) cargo).getSbiPointResp() != null) {
			enteredAmount = ((MAXTenderCargo) cargo).getSbiPointResp().getTotalPoint();
			capillaryAmount =((MAXTenderCargo) cargo).getSbiPointResp().getTotalPoint();
			amt = enteredAmount.toString();
		}else {
		POSBaseBeanModel model = (POSBaseBeanModel) ui.getModel(MAXPOSUIManagerIfc.ENTER_LOYALTY_POINTS_AMOUNT);
		PromptAndResponseModel prModel = model.getPromptAndResponseModel();
		 amt = prModel.getResponseText();
			enteredAmount = DomainGateway.getBaseCurrencyInstance(amt);
			capillaryAmount = DomainGateway.getBaseCurrencyInstance(amt);
		}
		CurrencyIfc amount;

		// Rev 1.2 Start 
		CurrencyIfc loyaltyPointsConversionFactor = null;

		try {
			loyaltyPointsConversionFactor = DomainGateway.getBaseCurrencyInstance(pm.getStringValue("LoyaltyPointsConversionFactor"));
		} catch (ParameterException pe) {
			logger.error("Error retrieving parameter:" + pe.getMessage() + "");
		}

		boolean isCapillaryAPI = false;

		try {
			isCapillaryAPI=	pm.getBooleanValue("IsCapillaryAPIAllowed")
					.booleanValue();
		} catch (ParameterException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}	

		// Rev 1.2 End 

		String modeOfLoyaltyPointsTender = null;
		try {
			modeOfLoyaltyPointsTender = pm.getStringValue("ModeOfLoyaltyPointsTender");
		} catch (ParameterException pe) {
			logger.error("Error retrieving parameter:" + pe.getMessage() + "");
		}

		if (modeOfLoyaltyPointsTender.equalsIgnoreCase("Points")) {

			CurrencyIfc enteredPoints = DomainGateway.getBaseCurrencyInstance(amt);
			// Rev 1.2 Start 
			//CurrencyIfc loyaltyPointsConversionFactor = null;

			/*try {
				loyaltyPointsConversionFactor = DomainGateway.getBaseCurrencyInstance(pm.getStringValue("LoyaltyPointsConversionFactor"));
			} catch (ParameterException pe) {
				logger.error("Error retrieving parameter:" + pe.getMessage() + "");
			}*/

			if(isCapillaryAPI)
				amount=	enteredPoints;	
			else
				// amount = (100*pointsEntered)/conversionFactor
				// e.g 143 points = 100 INR then amount=(100)*(entered points)/143

				amount = (DomainGateway.getBaseCurrencyInstance("100").multiply(enteredPoints)).divide(loyaltyPointsConversionFactor);

			// Rev 1.2 End 
		} else {
			// Rev 1.2 Start 

			if(isCapillaryAPI)
				// 166*entered amount/100
			{
				amount= DomainGateway.getBaseCurrencyInstance(amt);
				capillaryAmount = (loyaltyPointsConversionFactor.multiply(enteredAmount)).divide(DomainGateway.getBaseCurrencyInstance("100"));
			}
			else
				amount = DomainGateway.getBaseCurrencyInstance(amt);

			// Rev 1.2 End 
		}

		CurrencyIfc minimumLoyaltyPointsTenderAmount = null;
		try {

			minimumLoyaltyPointsTenderAmount = DomainGateway.getBaseCurrencyInstance(pm.getStringValue("MinimumLoyaltyPointsTenderAmount"));
		} catch (ParameterException pe) {
			logger.error("" + pe.getMessage() + "");
		}

		// Display dialog message if tender amount is less then
		// minimumLoyaltyPointsTenderAmount
		if (amount.compareTo(minimumLoyaltyPointsTenderAmount) == CurrencyIfc.LESS_THAN) {
			DialogBeanModel beanModel = new DialogBeanModel();
			beanModel.setResourceID("LoyaltyPointsAmountLowNotice");
			beanModel.setType(DialogScreensIfc.ERROR);
			beanModel.setButtonLetter(DialogScreensIfc.BUTTON_OK, "InsufficientPoints");
			ui.showScreen(POSUIManagerIfc.DIALOG_TEMPLATE, beanModel);
		}
		// Display dialog message if over tendering is done in case of loyalty
		// tender
		else {
			tenderAttributes.put(TenderConstants.AMOUNT, amount.toString());
			// Rev 1.2 Start 
			tenderAttributes.put(MAXTenderConstants.POINTS, capillaryAmount.toString());
			// Rev 1.2 End 
			// Use transaction to validate limits for loyalty Points
			System.out.println(tenderAttributes.put(TenderConstants.AMOUNT, amount.toString()));
			System.out.println(tenderAttributes.put(MAXTenderConstants.POINTS, capillaryAmount.toString()));
			System.out.println("amount :"+amount.toString());
			System.out.println("capillaryAmount:"+capillaryAmount.toString());
			try {
				cargo.getCurrentTransactionADO().validateTenderLimits(tenderAttributes);
			} catch (TenderException e) {
				TenderErrorCodeEnum error = e.getErrorCode();
				
				 // System.out.println("Error 168 :"+e.getErrorCode()); 
				  if (error ==TenderErrorCodeEnum.OVERTENDER_ILLEGAL) 
				  {
					  displayErrorDialog(bus,"OvertenderNotAllowed", DialogScreensIfc.ERROR); 
					  return; 
				 }
				/*
				 * CurrencyIfc b =((MAXTenderCargo)
				 * cargo).getTransaction().getTransactionTotals().getGrandTotal();
				 * 
				 * if (error == TenderErrorCodeEnum.OVERTENDER_ILLEGAL || error ==
				 * TenderErrorCodeEnum.INVALID_AMOUNT && amount.compareTo(b)==-1 &&
				 * bus.getCurrentLetter().getName().equalsIgnoreCase("Converted"))
				 * 
				 * {
				 * 
				 * }
				 * 
				 * else if(error == TenderErrorCodeEnum.OVERTENDER_ILLEGAL || error ==
				 * TenderErrorCodeEnum.INVALID_AMOUNT && amount.compareTo(b)==-1 &&
				 * bus.getCurrentLetter().getName().equalsIgnoreCase("Total")) {
				 * 
				 * }
				 */			}


			// Rev 1.2 Start 

			if(isCapillaryAPI)
			{   

				bus.mail(new Letter("ProcessCapillaryRequest"),BusIfc.CURRENT);
			}
			else
			{				
				// Rev 1.2 End 
				bus.mail(new Letter("Success"), BusIfc.CURRENT);
			}
		}

	}
	protected void displayErrorDialog(BusIfc bus, String name, int dialogType) {
		POSUIManagerIfc ui = (POSUIManagerIfc) bus.getManager(UIManagerIfc.TYPE);
		DialogBeanModel dialogModel = new DialogBeanModel();
		dialogModel.setResourceID(name);
		dialogModel.setType(dialogType);

		if (dialogType == DialogScreensIfc.ERROR) {
			dialogModel.setButtonLetter(DialogScreensIfc.BUTTON_OK, CommonLetterIfc.FAILURE);
		}

		ui.showScreen(POSUIManagerIfc.DIALOG_TEMPLATE, dialogModel);
	}
}
