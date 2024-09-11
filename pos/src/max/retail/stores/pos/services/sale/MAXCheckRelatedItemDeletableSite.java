/* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
  Copyright (c) 2012-2013 MAX, Inc.    All Rights Reserved.
  
  Rev. 1.1		Tanmaya		10/05/2013		Bug 5660 - Scan & Void items to delete.
  Rev. 1.0 		Tanmaya		05/04/2013		Initial Draft: Change for Scan and void
 * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */

package max.retail.stores.pos.services.sale;

import java.util.ArrayList;

import max.retail.stores.pos.ui.MAXPOSUIManagerIfc;
import oracle.retail.stores.domain.lineitem.AbstractTransactionLineItemIfc;
import oracle.retail.stores.domain.lineitem.SaleReturnLineItemIfc;
import oracle.retail.stores.domain.transaction.SaleReturnTransactionIfc;
import oracle.retail.stores.foundation.manager.ifc.UIManagerIfc;
import oracle.retail.stores.foundation.tour.application.Letter;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.pos.services.PosSiteActionAdapter;
import oracle.retail.stores.pos.services.common.CommonLetterIfc;
import oracle.retail.stores.pos.services.sale.SaleCargoIfc;
import oracle.retail.stores.pos.ui.DialogScreensIfc;
import oracle.retail.stores.pos.ui.POSUIManagerIfc;
import oracle.retail.stores.pos.ui.UIUtilities;
import oracle.retail.stores.pos.ui.beans.LineItemsModel;

//--------------------------------------------------------------------------
/**
 * This site checks if a related item is deletable. If it is not, then we
 * display a message saying it is not. $Revision: 1$
 **/
// --------------------------------------------------------------------------
public class MAXCheckRelatedItemDeletableSite extends PosSiteActionAdapter {

	/**
	 * 
	 */
	private static final long serialVersionUID = -5856724573018743138L;
	/**
	 * This defines the dialog screen to display from the bundles.
	 */
	public static String DELETE_INVALID = "DeleteInvalid";

	// ----------------------------------------------------------------------
	/**
	 * This method checks if any of the items are not deletable because of the
	 * related item flag.
	 * 
	 * @param bus
	 * @see com.extendyourstore.foundation.tour.ifc.SiteActionIfc#arrive(com.extendyourstore.foundation.tour.ifc.BusIfc)
	 **/
	// ----------------------------------------------------------------------
	public void arrive(BusIfc bus) {
		POSUIManagerIfc ui = (POSUIManagerIfc) bus
				.getManager(UIManagerIfc.TYPE);
		LineItemsModel beanModel = (LineItemsModel) ui.getModel();
		int[] rowsToDelete = beanModel.getSelectedRows();
		AbstractTransactionLineItemIfc[] lineItems = beanModel.getLineItems();
		for(int i=0 ;i< rowsToDelete.length ; i++)
		{
			if(rowsToDelete[i]>=lineItems.length)
				rowsToDelete[i]=-1;
		}
		
		ArrayList selectedArrayList = new ArrayList();
		for (int i = 0; i < rowsToDelete.length; i++) {
			if (rowsToDelete[i] != -1) {
				int lineNumber = lineItems[rowsToDelete[i]].getLineNumber();
				selectedArrayList.add(new Integer(lineNumber));
			}
		}
		
		  Object[] array = selectedArrayList.toArray();
		
		 int[] allSelected = new int[array.length];
		 
		 
		 for(int i =0;i<array.length;i++)
		 {
			 allSelected[i] = ((Integer)array[i]).intValue();
		 }
		 
		beanModel.setRowsToDelete(allSelected);
		ui.setModel(MAXPOSUIManagerIfc.LINEITEM_VOID_LIST, beanModel);
		boolean deleteAllowed = true;
		SaleCargoIfc cargo = (SaleCargoIfc) bus.getCargo();
		SaleReturnTransactionIfc transaction = cargo.getTransaction();
		for (int i = 0; i < allSelected.length; i++) {
			AbstractTransactionLineItemIfc item = transaction
					.retrieveItemByIndex(allSelected[i]);
			if (item instanceof SaleReturnLineItemIfc) {
				if (!((SaleReturnLineItemIfc) item).isRelatedItemDeleteable()) {
					deleteAllowed = false;
					break;
				}
			}
		}

		if (!deleteAllowed) {
			UIUtilities.setDialogModel(ui, DialogScreensIfc.ERROR,
					DELETE_INVALID, null, CommonLetterIfc.FAILURE);
			return;
		}

		Letter letter = new Letter("Continue");
		bus.mail(letter, BusIfc.CURRENT);
	}
}
