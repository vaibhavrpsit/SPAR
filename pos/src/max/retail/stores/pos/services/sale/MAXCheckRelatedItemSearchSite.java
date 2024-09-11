/* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
  Copyright (c) 2012-2013 MAX, Inc.    All Rights Reserved.
  Rev. 1.2 		Tanmaya		28/05/2013		Bug 6034 - POS crashed while click on Quantity button in Scan&Void Screen 
  Rev. 1.1 		Tanmaya		09/05/2013		Bug - 5661
  Rev. 1.0 		Tanmaya		05/04/2013		Initial Draft: Change for Scan and void
 * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
package max.retail.stores.pos.services.sale;

import java.util.Vector;

import max.retail.stores.pos.ui.MAXPOSUIManagerIfc;
import oracle.retail.stores.domain.lineitem.AbstractTransactionLineItemIfc;
import oracle.retail.stores.domain.lineitem.SaleReturnLineItem;
import oracle.retail.stores.domain.lineitem.SaleReturnLineItemIfc;
import oracle.retail.stores.domain.stock.PLUItemIfc;
import oracle.retail.stores.domain.transaction.SaleReturnTransactionIfc;
import oracle.retail.stores.domain.transaction.TransactionConstantsIfc;
import oracle.retail.stores.foundation.manager.ifc.ParameterManagerIfc;
import oracle.retail.stores.foundation.manager.ifc.UIManagerIfc;
import oracle.retail.stores.foundation.manager.parameter.ParameterException;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.pos.services.PosSiteActionAdapter;
import oracle.retail.stores.pos.services.sale.SaleCargoIfc;
import oracle.retail.stores.pos.ui.POSUIManagerIfc;
import oracle.retail.stores.pos.ui.beans.LineItemsModel;
import oracle.retail.stores.pos.ui.beans.NavigationButtonBeanModel;
import oracle.retail.stores.pos.ui.beans.POSBaseBeanModel;
import oracle.retail.stores.pos.ui.beans.PromptAndResponseModel;

public class MAXCheckRelatedItemSearchSite extends PosSiteActionAdapter {
	/**
	 * 
	 */
	private static final long serialVersionUID = 4369844446033359081L;
	// ----------------------------------------------------------------------
	/**
	 * This defines the dialog screen to display from the bundles.
	 */
	public static String DELETE_INVALID = "DeleteInvalid";

	public void arrive(BusIfc bus) {
		POSUIManagerIfc ui = (POSUIManagerIfc) bus
				.getManager(UIManagerIfc.TYPE);
		PromptAndResponseModel parModel = ((POSBaseBeanModel) ui
				.getModel(MAXPOSUIManagerIfc.LINEITEM_VOID))
				.getPromptAndResponseModel();
		String txt = parModel.getResponseText();

		SaleCargoIfc cargo = (SaleCargoIfc) bus.getCargo();
		SaleReturnTransactionIfc transaction = cargo.getTransaction();
		AbstractTransactionLineItemIfc[] itm = transaction.getLineItems();
		PLUItemIfc[] allSelected1 = new PLUItemIfc[itm.length];
		;
		AbstractTransactionLineItemIfc[] selectedLineItems = new AbstractTransactionLineItemIfc[itm.length];
		int j = 0;
		for (int i = 0; i < itm.length; i++) {
			PLUItemIfc plu = ((SaleReturnLineItemIfc) itm[i]).getPLUItem();
			if (txt.equals(plu.getPosItemID()) || txt.equals(plu.getItemID())) {
				{
					selectedLineItems[j++] = itm[i];
					allSelected1[i] = plu;
				}
			}
		}
		AbstractTransactionLineItemIfc[] allSelectedLineItems = new AbstractTransactionLineItemIfc[j];
		for (int i = 0; i < j; i++) {
			allSelectedLineItems[i] = selectedLineItems[i];
		}
		// MAX Rev 1.1: Change: start
		if (((allSelectedLineItems == null) || (allSelectedLineItems != null && allSelectedLineItems.length == 0))
				&& isValidWeightedBarcode(txt, bus)) {
			bus.mail("Weighted");
		} else {
			LineItemsModel lineModel = new LineItemsModel();
			lineModel.setLineItems(allSelectedLineItems);
			// MAX Rev 1.2: Change: starts
			NavigationButtonBeanModel localNav = new NavigationButtonBeanModel();
			if (allSelectedLineItems.length == 0) {

				localNav.setButtonEnabled("Quantity", false);

			} else {
				localNav.setButtonEnabled("Quantity", true);
			}
			
			if(transaction !=null)
	    	{
	    		if(transaction.getTransactionType() == TransactionConstantsIfc.TYPE_RETURN)
	    		{
	    			if(!isRetrievedReturnItem(transaction))
	    				localNav.setButtonEnabled("Quantity", true);
	    			else
	    				localNav.setButtonEnabled("Quantity", false);
	    		}
	    	}
			
			// MAX Rev 1.2: Change: ends
			lineModel.setSelectedRow(0);
			lineModel.setLocalButtonBeanModel(localNav);
			ui.setModel(MAXPOSUIManagerIfc.LINEITEM_VOID_LIST, lineModel);
			ui.showScreen(MAXPOSUIManagerIfc.LINEITEM_VOID_LIST);
		}
		// MAX Rev 1.1: Change: ends
	}

	private boolean isValidWeightedBarcode(String itemIDToBeDeleted, BusIfc bus) {
		// TODO Auto-generated method stub
		ParameterManagerIfc pm = (ParameterManagerIfc) bus
				.getManager(ParameterManagerIfc.TYPE);
		try {
			if (itemIDToBeDeleted.length() == pm.getIntegerValue(
					MAXSaleConstantsIfc.WEIGHT_BARCODE_LENGTH).intValue())
				return true;
			else
				return false;
		} catch (ParameterException e) {
			// TODO Auto-generated catch block
			return false;
		}

	}
	
	protected boolean isRetrievedReturnItem(SaleReturnTransactionIfc transaction )
    {
        boolean retrievedItem = false;
        Vector lineItemsVector = transaction.getLineItemsVector();

        SaleReturnLineItemIfc lineItem = null;
        for(int i=0;i<lineItemsVector.size();i++)
        {
        	if(lineItemsVector.get(i) instanceof SaleReturnLineItem)
        		lineItem = (SaleReturnLineItemIfc)lineItemsVector.get(i);
        	if (lineItem.isReturnLineItem())
        	{
        		retrievedItem = (lineItem.getReturnItem().getOriginalTransactionID() != null);
        		if(!retrievedItem)
        			break;
        	}
        }
        return(retrievedItem);
    }
}
