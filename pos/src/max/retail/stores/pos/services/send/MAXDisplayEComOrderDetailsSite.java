/* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
  Copyright (c) 2016	MAX HyperMarkets.    All Rights Reserved.
 
	Rev 1.0 	16/07/2016		Abhishek Goyal		Initial Draft: Changes for CR
* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
package max.retail.stores.pos.services.send;

import max.retail.stores.domain.transaction.MAXSaleReturnTransaction;
import max.retail.stores.pos.ui.MAXPOSUIManagerIfc;
import max.retail.stores.pos.ui.beans.MAXEComOrderDetailsBeanModel;
import oracle.retail.stores.domain.transaction.RetailTransactionIfc;
import oracle.retail.stores.foundation.manager.ifc.ParameterManagerIfc;
import oracle.retail.stores.foundation.manager.ifc.UIManagerIfc;
import oracle.retail.stores.foundation.manager.parameter.ParameterException;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.pos.services.PosSiteActionAdapter;
import oracle.retail.stores.pos.services.modifyitem.ItemCargo;
import oracle.retail.stores.pos.services.send.address.SendCargo;
import oracle.retail.stores.pos.ui.POSUIManagerIfc;

public class MAXDisplayEComOrderDetailsSite extends PosSiteActionAdapter {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	public void arrive(BusIfc bus)
	{
		POSUIManagerIfc ui = (POSUIManagerIfc)bus.getManager(UIManagerIfc.TYPE);
		MAXEComOrderDetailsBeanModel model = new MAXEComOrderDetailsBeanModel();
		ParameterManagerIfc pm = (ParameterManagerIfc) bus
				.getManager(ParameterManagerIfc.TYPE);
		 String[]  orderTypes = null;
			try {
				orderTypes = pm.getStringValues("ECOMOrderTypes");
			} catch (ParameterException e1) {
				e1.printStackTrace();
			}
			model.setOrderTypes(orderTypes);
			model.setSelectedOrderType(0);
		ui.showScreen(MAXPOSUIManagerIfc.ECOM_ORDER_DETAILS, model);
	}
	
	public void depart(BusIfc bus)
	{
		RetailTransactionIfc trans = null;
		if(bus.getCargo() instanceof ItemCargo)
		{
			trans = ((ItemCargo)bus.getCargo()).getTransaction();
		}
		else if(bus.getCargo() instanceof SendCargo)
		{
			trans = ((SendCargo)bus.getCargo()).getTransaction();
		}
		POSUIManagerIfc ui = (POSUIManagerIfc)bus.getManager(UIManagerIfc.TYPE);
		if(ui.getModel() instanceof MAXEComOrderDetailsBeanModel)
		{
			MAXEComOrderDetailsBeanModel beanModel = (MAXEComOrderDetailsBeanModel) ui.getModel();				
			if(trans instanceof MAXSaleReturnTransaction)
			{
				MAXSaleReturnTransaction saleTrans = (MAXSaleReturnTransaction)trans;
				saleTrans.seteComOrderNumber(beanModel.getTxtEComOrderNoField());
				saleTrans.seteComOrderAmount(beanModel.getTxtEComOrderAmountField());
				saleTrans.seteComOrderTransNumber(beanModel.getTxtEComTransNoField());
				saleTrans.seteComOrderType(beanModel.getOrderType());
				saleTrans.seteComSendTransaction(true);		
			}
		}
	}
}

