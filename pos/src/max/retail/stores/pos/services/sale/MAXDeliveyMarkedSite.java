/********************************************************************************
 *   
 *	Copyright (c) 2016 - 2017 MAX Hypermarket, Inc    All Rights Reserved.
 *	
 *  Rev 1.0		May 04, 2017		Kritica Agarwal 	GST Changes
 *
 ********************************************************************************/
package max.retail.stores.pos.services.sale;

import java.util.Vector;

import max.retail.stores.domain.lineitem.MAXSaleReturnLineItemIfc;
import max.retail.stores.domain.transaction.MAXSaleReturnTransactionIfc;
import oracle.retail.stores.domain.lineitem.AbstractTransactionLineItemIfc;
import oracle.retail.stores.domain.transaction.SaleReturnTransactionIfc;
import oracle.retail.stores.foundation.manager.ifc.UIManagerIfc;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.pos.services.PosSiteActionAdapter;
import oracle.retail.stores.pos.services.common.CommonLetterIfc;
import oracle.retail.stores.pos.services.sale.SaleCargoIfc;
import oracle.retail.stores.pos.ui.DialogScreensIfc;
import oracle.retail.stores.pos.ui.POSUIManagerIfc;
import oracle.retail.stores.pos.ui.beans.DialogBeanModel;

public class MAXDeliveyMarkedSite extends PosSiteActionAdapter{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	public void arrive(BusIfc bus)
    {

		SaleCargoIfc cargo = (SaleCargoIfc) bus.getCargo();
		SaleReturnTransactionIfc transaction =  cargo.getTransaction();
		POSUIManagerIfc ui = (POSUIManagerIfc) bus.getManager(UIManagerIfc.TYPE);
		if(transaction!= null){
		boolean isDeliveryMarkedToAllItems= false;
		if(transaction instanceof MAXSaleReturnTransactionIfc && ((MAXSaleReturnTransactionIfc)transaction).isIgstApplicable()){
		Vector<AbstractTransactionLineItemIfc> lineItem = transaction.getItemContainerProxy().getLineItemsVector();
		
		 for ( int i = 0; i < lineItem.size(); i++ )
	        {
			 if(lineItem.get(i) instanceof MAXSaleReturnLineItemIfc ){
	            if( (!((MAXSaleReturnLineItemIfc) lineItem.get(i)).getItemSendFlag())  && ((MAXSaleReturnLineItemIfc) lineItem.get(i)).getItemQuantityDecimal().signum()>=0)
	            {
	               // all items are not marked for IGST
	            	isDeliveryMarkedToAllItems= true;
	                break;
	            }
			 }
	        }
		}
		if(isDeliveryMarkedToAllItems && transaction instanceof MAXSaleReturnTransactionIfc && ((MAXSaleReturnTransactionIfc)transaction).isIgstApplicable()){
			ui.showScreen(POSUIManagerIfc.DIALOG_TEMPLATE, markDeliveryToAllItems());
			//bus.mail("Failure", BusIfc.CURRENT);
		}
		else{
			bus.mail("Tender", BusIfc.CURRENT);
		}
		}
    }
	public static DialogBeanModel markDeliveryToAllItems() {
		DialogBeanModel dialogModel = new DialogBeanModel();
		dialogModel.setResourceID("MARK_ALL_ITEMS_FOR_DELIVERY");
		dialogModel.setType(DialogScreensIfc.ERROR);
		dialogModel.setButtonLetter(DialogScreensIfc.BUTTON_OK,
				"Failure");

		return dialogModel;
	}
	

}
