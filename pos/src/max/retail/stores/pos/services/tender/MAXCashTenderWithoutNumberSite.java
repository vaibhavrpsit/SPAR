package max.retail.stores.pos.services.tender;

import max.retail.stores.domain.arts.MAXDataTransactionKeys;
import max.retail.stores.domain.arts.MAXHotKeysTransaction;
import oracle.retail.stores.commerceservices.common.currency.CurrencyIfc;
import oracle.retail.stores.domain.DomainGateway;
import oracle.retail.stores.domain.arts.DataTransactionFactory;
import oracle.retail.stores.foundation.manager.ifc.ParameterManagerIfc;
import oracle.retail.stores.foundation.manager.ifc.UIManagerIfc;
import oracle.retail.stores.foundation.manager.parameter.ParameterException;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.pos.services.PosSiteActionAdapter;
import oracle.retail.stores.pos.ui.DialogScreensIfc;
import oracle.retail.stores.pos.ui.POSUIManagerIfc;
import oracle.retail.stores.pos.ui.beans.DialogBeanModel;

public class MAXCashTenderWithoutNumberSite extends PosSiteActionAdapter {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 5989865551651880481L;

	public void arrive(BusIfc bus) 
	{
		MAXTenderCargo cargo = (MAXTenderCargo) bus.getCargo();
		//ParameterManagerIfc pm = (ParameterManagerIfc) bus.getManager(ParameterManagerIfc.TYPE);
		ParameterManagerIfc pm = (ParameterManagerIfc) bus.getManager(ParameterManagerIfc.TYPE);
		POSUIManagerIfc ui = (POSUIManagerIfc) bus.getManager(UIManagerIfc.TYPE);
		String maxiAmount = null;
		//maximumAmount = pm.getCurrency("MaximumCashAccepted");
		try {
			maxiAmount = pm.getStringValue("MaximumCashWithoutNumberAccepted");
			CurrencyIfc maximumAmount = DomainGateway.getBaseCurrencyInstance(maxiAmount);
			
			
			if(cargo.getTransaction().getTenderTransactionTotals().getBalanceDue().compareTo(maximumAmount)==-1||cargo.getTransaction().getTenderTransactionTotals().getBalanceDue().compareTo(maximumAmount)==0)
			{
				bus.mail("Yes");
			}
			else
			{
				DialogBeanModel dialogModel = new DialogBeanModel();
				dialogModel.setResourceID("CashLimitWithoutPh.No");
				dialogModel.setDescription("Total Balance is more than 50k!!");
				dialogModel.setType(DialogScreensIfc.ACKNOWLEDGEMENT);
				dialogModel.setArgs(null);
				dialogModel.setButtonLetter(DialogScreensIfc.BUTTON_OK, "No");
				ui.showScreen(POSUIManagerIfc.DIALOG_TEMPLATE, dialogModel);
				/*
				 * CurrencyIfc totalAmount =
				 * cargo.getTransaction().getTenderTransactionTotals().getSubtotal().subtract(
				 * maximumAmount);
				 * cargo.getTransaction().getTenderTransactionTotals().setSubtotal(totalAmount);
				 * bus.mail("No");
				 */
			}
		} catch (ParameterException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
}
