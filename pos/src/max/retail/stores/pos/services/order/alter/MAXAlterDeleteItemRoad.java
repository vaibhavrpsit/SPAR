/* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
  Copyright (c) 2012-2013 MAXHyperMarket, Inc.    All Rights Reserved.
  Rev 1.0	1/May/2013	  	Tanmaya, Home Delivery Special Order
* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
package max.retail.stores.pos.services.order.alter;

// foundation imports
import java.util.ArrayList;

import max.retail.stores.domain.order.MAXOrderIfc;
import max.retail.stores.pos.services.order.common.MAXOrderCargo;
import oracle.retail.stores.domain.lineitem.AbstractTransactionLineItemIfc;
import oracle.retail.stores.domain.order.OrderIfc;
import oracle.retail.stores.foundation.manager.ifc.UIManagerIfc;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.pos.services.PosLaneActionAdapter;
import oracle.retail.stores.pos.ui.POSUIManagerIfc;
import oracle.retail.stores.pos.ui.beans.LineItemsModel;

public class MAXAlterDeleteItemRoad extends PosLaneActionAdapter

{
	private static final long serialVersionUID = -5109666290322694229L;

	public static final String revisionNumber = "$Revision: 8$";

	protected static final int NO_SELECTION = -1;

	public void traverse(BusIfc bus) {

		POSUIManagerIfc ui;
		ui = (POSUIManagerIfc) bus.getManager(UIManagerIfc.TYPE);
		
		LineItemsModel beanModel = (LineItemsModel) ui.getModel();
		int[] allSelected = beanModel.getSelectedRows();
		MAXOrderCargo cargo = (MAXOrderCargo) bus.getCargo();
		OrderIfc order = cargo.getOrder();
		ArrayList itemList = new ArrayList();
		AbstractTransactionLineItemIfc[] lineItems = order.getLineItems();
		for (int i = 0; i < allSelected.length; i++) {
			AbstractTransactionLineItemIfc item = order
					.retrieveItemByIndex(allSelected[i]);
			
			if(order instanceof MAXOrderIfc)
				((MAXOrderIfc)order).getDeletedItems().add(item);
			lineItems[allSelected[i]] = null;

		}
		int lineItemSequenceNumberCounter=0;
		for(int i =0; i<lineItems.length ; i++)
		{
			if(lineItems[i]!=null)
			{
				lineItems[i].setLineNumber(lineItemSequenceNumberCounter);
				lineItemSequenceNumberCounter++;
				itemList.add(lineItems[i]);
			}
		}
		
		
		AbstractTransactionLineItemIfc finalItems[]=(AbstractTransactionLineItemIfc[])itemList.toArray(new AbstractTransactionLineItemIfc[itemList.size()]);  
		
		order.setLineItems(finalItems);
		if(order instanceof MAXOrderIfc)
			((MAXOrderIfc)order).setAlterOrder(true);
		cargo.setOrder(order);

	}

}
