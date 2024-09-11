/* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
  Copyright (c) 2013	MAX HyperMarkets.    All Rights Reserved.
	Rev 1.0 	13/08/2013		Prateek		Initial Draft: Changes for TIC Customer CR
* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */

package max.retail.stores.pos.services.sale.validate;

import max.retail.stores.domain.customer.MAXCustomer;
import max.retail.stores.domain.customer.MAXCustomerConstantsIfc;
import max.retail.stores.domain.transaction.MAXSaleReturnTransaction;
import max.retail.stores.domain.transaction.MAXSaleReturnTransactionIfc;
import max.retail.stores.pos.ui.MAXPOSUIManagerIfc;
import oracle.retail.stores.domain.DomainGateway;
import oracle.retail.stores.domain.customer.CustomerIfc;
import oracle.retail.stores.domain.transaction.SaleReturnTransactionIfc;
import oracle.retail.stores.domain.transaction.TenderableTransactionIfc;
import oracle.retail.stores.domain.utility.LocaleUtilities;
import oracle.retail.stores.foundation.manager.ifc.ParameterManagerIfc;
import oracle.retail.stores.foundation.manager.ifc.UIManagerIfc;
import oracle.retail.stores.foundation.manager.parameter.ParameterException;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.pos.config.bundles.BundleConstantsIfc;
import oracle.retail.stores.pos.manager.ifc.UtilityManagerIfc;
import oracle.retail.stores.pos.services.PosSiteActionAdapter;
import oracle.retail.stores.pos.services.common.TagConstantsIfc;
import oracle.retail.stores.pos.services.sale.SaleCargoIfc;
import oracle.retail.stores.pos.ui.DialogScreensIfc;
import oracle.retail.stores.pos.ui.POSUIManagerIfc;
import oracle.retail.stores.pos.ui.beans.DialogBeanModel;
import oracle.retail.stores.pos.ui.beans.LineItemsModel;
import oracle.retail.stores.pos.ui.beans.POSBaseBeanModel;
import oracle.retail.stores.pos.ui.beans.PromptAndResponseModel;
import oracle.retail.stores.pos.ui.beans.StatusBeanModel;

public class MAXAddTICCustomerSite extends PosSiteActionAdapter {

	public void arrive(BusIfc bus)
	{
		POSUIManagerIfc ui = (POSUIManagerIfc)bus.getManager(UIManagerIfc.TYPE);
		POSBaseBeanModel model = (POSBaseBeanModel) ui.getModel(MAXPOSUIManagerIfc.ENTER_TIC_CUSTOMER_ID);
		
		PromptAndResponseModel prmodel = model.getPromptAndResponseModel();
		String id = prmodel.getResponseText();
		ParameterManagerIfc pm =(ParameterManagerIfc)bus.getManager(ParameterManagerIfc.TYPE);
		int min = 0, max =0;
		try
		{
			min = pm.getIntegerValue("MinTicCardLength").intValue();
			max = pm.getIntegerValue("MaxTicCardLength").intValue();
		}
		catch(ParameterException e)
		{
			logger.error(e);
		}
		if(id.length()>= min && id.length()<= max)
		{
			MAXSaleReturnTransactionIfc trxn = null;
			SaleCargoIfc cargo = (SaleCargoIfc)bus.getCargo();
			if(cargo.getTransaction() instanceof TenderableTransactionIfc)
			{
				trxn = (MAXSaleReturnTransaction) cargo.getTransaction();
				CustomerIfc customer = DomainGateway.getFactory().getCustomerInstance();
				customer.setCustomerID(id);
				customer.setFirstName(id);			
				((MAXCustomer)customer).setCustomerType(MAXCustomerConstantsIfc.CRM);
				trxn.setTicCustomer(customer);
				cargo.setTransaction((SaleReturnTransactionIfc) trxn);
				
				/** Setting UP Customer Details in Status Panel **/
				UtilityManagerIfc utility = (UtilityManagerIfc) bus
						.getManager(UtilityManagerIfc.TYPE);
				StatusBeanModel statusModel = new StatusBeanModel();
				String[] vars = { customer.getFirstName(),
						customer.getLastName() };
				String pattern = utility.retrieveText("CustomerAddressSpec",
						BundleConstantsIfc.CUSTOMER_BUNDLE_NAME,
						TagConstantsIfc.CUSTOMER_NAME_TAG,
						TagConstantsIfc.CUSTOMER_NAME_PATTERN_TAG);
				String customerName = LocaleUtilities.formatComplexMessage(
						pattern, vars);
				statusModel.setCustomerName(customerName);
				LineItemsModel baseModel = new LineItemsModel();
				baseModel.setStatusBeanModel(statusModel);
				ui.setModel(POSUIManagerIfc.SHOW_STATUS_ONLY, baseModel);
				
				bus.mail("Next");
			}
		}
		else
		{
			displayErrorDialogInvalid(ui);
		}
	}
	protected void displayErrorDialogInvalid(POSUIManagerIfc ui) {
		DialogBeanModel dialogModel = new DialogBeanModel();
		dialogModel.setResourceID("InvalidTicCard");
		
		dialogModel.setType(DialogScreensIfc.ERROR);
		ui.showScreen(POSUIManagerIfc.DIALOG_TEMPLATE, dialogModel);
	}
}
