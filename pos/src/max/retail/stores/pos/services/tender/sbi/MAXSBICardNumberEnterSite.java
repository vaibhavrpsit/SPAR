/* ===========================================================================
 *  Copyright (c) 2017 MAX Hypermarkets Pvt Ltd.    All Rights Reserved. 
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
import max.retail.stores.domain.utility.MAXConfigParametersIfc;
import max.retail.stores.pos.services.tender.MAXTenderCargo;
import max.retail.stores.pos.ui.MAXPOSUIManagerIfc;
import oracle.retail.stores.domain.DomainGateway;
import oracle.retail.stores.domain.arts.DataTransactionFactory;
import oracle.retail.stores.foundation.manager.data.DataException;
import oracle.retail.stores.foundation.manager.ifc.UIManagerIfc;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.pos.services.PosSiteActionAdapter;
import oracle.retail.stores.pos.services.common.CommonLetterIfc;
import oracle.retail.stores.pos.services.tender.TenderCargo;
import oracle.retail.stores.pos.ui.DialogScreensIfc;
import oracle.retail.stores.pos.ui.POSUIManagerIfc;
import oracle.retail.stores.pos.ui.beans.DialogBeanModel;
import oracle.retail.stores.pos.ui.beans.POSBaseBeanModel;
import oracle.retail.stores.pos.ui.beans.PromptAndResponseModel;

/**
 * Create a cash tender and attempt to add it to the transaction. If validation
 * fails, either punt, or attempt override, depending on the problem.
 */
public class MAXSBICardNumberEnterSite extends PosSiteActionAdapter
{
	private static final long serialVersionUID = 4340745363476760442L;

	@Override
	public void arrive(BusIfc bus)
	{
		TenderCargo cargo = (TenderCargo)bus.getCargo();
		POSUIManagerIfc ui = (POSUIManagerIfc) bus.getManager(UIManagerIfc.TYPE);
		String amount ="0.00";
		if(bus.getCurrentLetter().getName().equals(CommonLetterIfc.NO)) {
			amount = ((MAXTenderCargo)cargo).getTotalPointAmount();
		}else {
			amount = ui.getInput();
		}
		String totalRwrdAmt = ((MAXTenderCargo)cargo).getTotalPointAmount();
		
		DecimalFormat decimalFormat = new DecimalFormat("##");
		MAXConfigParametersIfc configParam = getAllConfigparameter();
		int conversionRate = configParam.getSbiPointConversionRate();
		int pointsconvert = Integer.parseInt(decimalFormat.format(((int)((Double.parseDouble(amount)*conversionRate)/12))));
		
		
		if(pointsconvert <1) {
			DialogBeanModel beanModel = new DialogBeanModel();
			beanModel.setResourceID("SBIRewardPointLowLimit");
			String[] msg = new String[2];
			msg[0] = amount;
			msg[1] = DomainGateway.getBaseCurrencyInstance(12/conversionRate+"").getStringValue();
			beanModel.setArgs(msg);
			beanModel.setType(DialogScreensIfc.ERROR);
			beanModel.setButtonLetter(DialogScreensIfc.BUTTON_OK, CommonLetterIfc.FAILURE);
			ui.showScreen(POSUIManagerIfc.DIALOG_TEMPLATE, beanModel);
		}else 
		if(DomainGateway.getBaseCurrencyInstance(amount).compareTo(DomainGateway.getBaseCurrencyInstance(totalRwrdAmt)) == 1) {

			DialogBeanModel beanModel = new DialogBeanModel();
			beanModel.setResourceID("SBIRewardPointOverLimit");
			String[] msg = new String[2];
			msg[0] = amount;
			msg[1] = totalRwrdAmt;
			beanModel.setArgs(msg);
			beanModel.setType(DialogScreensIfc.ERROR);
			beanModel.setButtonLetter(DialogScreensIfc.BUTTON_OK, CommonLetterIfc.FAILURE);
			ui.showScreen(POSUIManagerIfc.DIALOG_TEMPLATE, beanModel);
		}else {
			((MAXTenderCargo)cargo).setRedeemPointAmount(amount);
			POSUIManagerIfc uiManager = (POSUIManagerIfc)bus.getManager(UIManagerIfc.TYPE);
			POSBaseBeanModel model = new POSBaseBeanModel();
			PromptAndResponseModel prModel = new PromptAndResponseModel();
			model.setPromptAndResponseModel(prModel);
			uiManager.showScreen(MAXPOSUIManagerIfc.SBI_CARD_NUMBER, model);
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

}
