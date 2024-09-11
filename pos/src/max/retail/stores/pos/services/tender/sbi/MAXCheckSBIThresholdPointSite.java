/* ===========================================================================
 *  Copyright (c) 2017 MAX Hypermarkets India Pvt Ltd.    All Rights Reserved. 
 * ===========================================================================
 *
 * Rev 1.0  June 15,2021    Kumar Vaibhav  SBI reward points integration
 *
 * ===========================================================================
 */
package max.retail.stores.pos.services.tender.sbi;

import java.text.DecimalFormat;

import max.retail.stores.domain.arts.MAXConfigParameterTransaction;
import max.retail.stores.domain.arts.MAXDataTransactionKeys;
import max.retail.stores.domain.customer.MAXCustomerIfc;
import max.retail.stores.domain.customer.MAXTICCustomer;
import max.retail.stores.domain.transaction.MAXLayawayPaymentTransaction;
import max.retail.stores.domain.transaction.MAXLayawayTransaction;
import max.retail.stores.domain.transaction.MAXSaleReturnTransaction;
import max.retail.stores.domain.utility.MAXConfigParametersIfc;
import max.retail.stores.pos.services.tender.MAXTenderCargo;
import oracle.retail.stores.domain.arts.DataTransactionFactory;
import oracle.retail.stores.domain.customer.CustomerIfc;
import oracle.retail.stores.foundation.manager.data.DataException;
import oracle.retail.stores.foundation.manager.ifc.ParameterManagerIfc;
import oracle.retail.stores.foundation.manager.ifc.UIManagerIfc;
import oracle.retail.stores.foundation.tour.application.Letter;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.pos.services.PosSiteActionAdapter;
import oracle.retail.stores.pos.services.common.CommonLetterIfc;
import oracle.retail.stores.pos.services.tender.TenderCargo;
import oracle.retail.stores.pos.ui.DialogScreensIfc;
import oracle.retail.stores.pos.ui.POSUIManagerIfc;
import oracle.retail.stores.pos.ui.beans.DialogBeanModel;

/**
 * Create a cash tender and attempt to add it to the transaction. If validation
 * fails, either punt, or attempt override, depending on the problem.
 */
public class MAXCheckSBIThresholdPointSite extends PosSiteActionAdapter
{
	private static final long serialVersionUID = 4340745363476760442L;

	@Override
	public void arrive(BusIfc bus)
	{
		TenderCargo cargo = (TenderCargo)bus.getCargo();

		//MAXConfigParametersIfc configParam = getAllConfigparameter();
		//boolean pointConFlag = configParam.isSbiPointConversion();
		//int sbiMinPoint = configParam.getSbiMinPoint();
		//int conversionRate = configParam.getSbiPointConversionRate();
		int sbiMinPoint=0 ,conversionRate=0;
		String pointConFlag = null;
		try {
			
			ParameterManagerIfc parameterManager = (ParameterManagerIfc) bus
					.getManager(ParameterManagerIfc.TYPE);
			
			sbiMinPoint = parameterManager.getIntegerValue("SbiMinPoint");
			conversionRate = parameterManager.getIntegerValue("SbiPointConversionRate");
			pointConFlag = parameterManager.getStringValue("isSbiPointConversion");
			}
			catch(Exception e)
			{
				
			}
		double totalPoints = 0;
		MAXCustomerIfc customer = null;
		if(cargo.getTransaction() instanceof MAXSaleReturnTransaction) {
			customer = (MAXCustomerIfc) ((MAXSaleReturnTransaction) cargo.getTransaction()).getCustomer();
			//((MAXSaleReturnTransaction) cargo.getTransaction()).setSbiRewardredeemFlag(true);
		}else if(cargo.getTransaction() instanceof MAXLayawayTransaction) {
			customer = (MAXCustomerIfc) ((MAXLayawayTransaction) cargo.getTransaction()).getCustomer();
			//((MAXLayawayTransaction) cargo.getTransaction()).setSbiRewardredeemFlag(true);
		}
		//totalPoints =200;  // need to comment before delivery
		if(customer != null && customer.getMAXTICCustomer() != null && ((MAXTICCustomer) customer.getMAXTICCustomer()).getSbiPointBal() != null) 
		{
			String sbiPoints = ((MAXTICCustomer) customer.getMAXTICCustomer()).getSbiPointBal();
			//String sbiPoints = "1000";
			totalPoints = Double.parseDouble(sbiPoints);
		}
		if(totalPoints > 0)
		{
		if(pointConFlag.equals("Y") && totalPoints >= sbiMinPoint && (cargo.getTransaction() instanceof MAXSaleReturnTransaction
				|| cargo.getTransaction() instanceof MAXLayawayTransaction))
		{
			//int amount = (int)Math.round((double)totalPoints/conversionRate);
			DecimalFormat decimalFormat = new DecimalFormat("##.##");
			String amount = decimalFormat.format(totalPoints/conversionRate);
			
			((MAXTenderCargo) cargo).setTotalPointAmount(amount);
			
			POSUIManagerIfc uiManager=(POSUIManagerIfc)bus.getManager(UIManagerIfc.TYPE);
			DialogBeanModel dialogModel = new DialogBeanModel();
			dialogModel.setResourceID("SBIRewardPointNotice");
			String[] msg = new String[2];
			msg[0] = totalPoints+"";
			
			msg[1] = amount+"";
			dialogModel.setArgs(msg);
			dialogModel.setType(DialogScreensIfc.CONFIRMATION);
			dialogModel.setButtonLetter(DialogScreensIfc.BUTTON_YES, CommonLetterIfc.YES);
			dialogModel.setButtonLetter(DialogScreensIfc.BUTTON_NO, CommonLetterIfc.NO);
			uiManager.showScreen(POSUIManagerIfc.DIALOG_TEMPLATE, dialogModel);
		}}
		else{
			bus.mail(new Letter("Continue"), BusIfc.CURRENT);
		}
	}


	private MAXConfigParametersIfc getAllConfigparameter() {

		MAXConfigParameterTransaction configTransaction = new MAXConfigParameterTransaction();
		MAXConfigParametersIfc configParameters = null;
		configTransaction = (MAXConfigParameterTransaction) DataTransactionFactory
				.create(MAXDataTransactionKeys.CONFIG_PARAMETER_TRANSACTION);

		try {
			configParameters = configTransaction.selectConfigParameters();
		} catch (DataException e1) {
			//e1.printStackTrace();
			logger.error(e1.getMessage());
		}
		return configParameters;
	}
	
	/*
	 * @Override public void depart(BusIfc bus) { TenderCargo cargo =
	 * (TenderCargo)bus.getCargo(); POSUIManagerIfc ui = (POSUIManagerIfc)
	 * bus.getManager(UIManagerIfc.TYPE);
	 * if(bus.getCurrentLetter().equals(CommonLetterIfc.YES)) {
	 * 
	 * int totalPoints = 0; if(cargo.getTransaction() instanceof
	 * MAXSaleReturnTransaction) { totalPoints = ((MAXSaleReturnTransaction)
	 * cargo.getTransaction()).getSbiMinRewadPoints(); }else
	 * if(cargo.getTransaction() instanceof MAXLayawayTransaction) { totalPoints =
	 * ((MAXLayawayTransaction) cargo.getTransaction()).getSbiMinRewadPoints(); }
	 * 
	 * } }
	 */

}
