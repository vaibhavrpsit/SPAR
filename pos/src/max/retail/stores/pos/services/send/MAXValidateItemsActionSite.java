
package max.retail.stores.pos.services.send;

import java.util.ArrayList;
import java.util.Arrays;

import org.apache.log4j.Logger;

import oracle.retail.stores.domain.lineitem.SaleReturnLineItemIfc;
import oracle.retail.stores.domain.transaction.SaleReturnTransactionIfc;
import oracle.retail.stores.foundation.manager.ifc.ParameterManagerIfc;
import oracle.retail.stores.foundation.manager.ifc.UIManagerIfc;
import oracle.retail.stores.foundation.manager.parameter.ParameterException;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.pos.appmanager.ManagerException;
import oracle.retail.stores.pos.appmanager.ManagerFactory;
import oracle.retail.stores.pos.appmanager.send.SendException;
import oracle.retail.stores.pos.appmanager.send.SendManager;
import oracle.retail.stores.pos.appmanager.send.SendManagerIfc;
import oracle.retail.stores.pos.services.PosSiteActionAdapter;
import oracle.retail.stores.pos.services.common.CommonLetterIfc;
import oracle.retail.stores.pos.services.modifyitem.ItemCargo;
import oracle.retail.stores.pos.ui.DialogScreensIfc;
import oracle.retail.stores.pos.ui.POSUIManagerIfc;
import oracle.retail.stores.pos.ui.UIUtilities;
import oracle.retail.stores.pos.ui.beans.DialogBeanModel;

//--------------------------------------------------------------------------
/**
 * The purpose of this site is to check for maximum sends allowed and make use
 * of service to verify items are sendable. A dialog is displayed showing up to
 * the first 3 items not sendable. It checks for items attached to different
 * sends also. $Revision: 3$
 **/
// --------------------------------------------------------------------------
public class MAXValidateItemsActionSite extends PosSiteActionAdapter {
	/**
	 * revision number of this class
	 **/
	public static final String revisionNumber = "$Revision: 3$";
	/**
	 * revision number of this class
	 **/
	protected static Logger logger = Logger
			.getLogger(max.retail.stores.pos.services.send.MAXValidateItemsActionSite.class);
	/**
	 * send not allowed resource id
	 **/
	public static final String SEND_NOT_ALLOWED = "SendNotAllowed";
	/**
	 * maximum sends allowed parameter
	 **/
	public static final String MAXIMUM_SENDS_ALLOWED = "MaximumSendsAllowed";
	/**
	 * error multiple sends resource id
	 **/
	public static final String CUSTOMER_PRESENT = "CustomerPresent";

	// ----------------------------------------------------------------------
	/**
	 * Checks the maximum sends and items allowed for send
	 * <P>
	 * 
	 * @param bus
	 *            Service Bus
	 **/
	// ----------------------------------------------------------------------
	public void arrive(BusIfc bus) {
		ItemCargo cargo = (ItemCargo) bus.getCargo();

		ParameterManagerIfc pm = (ParameterManagerIfc) bus
				.getManager(ParameterManagerIfc.TYPE);
		POSUIManagerIfc ui = (POSUIManagerIfc) bus
				.getManager(UIManagerIfc.TYPE);
		if (!cargo.isTransactionLevelSendInProgress()) {
			SendManagerIfc sendMgr = null;
			try {
				sendMgr = (SendManagerIfc) ManagerFactory
						.create(SendManagerIfc.MANAGER_NAME);
			} catch (ManagerException e) {
				// default to product version
				sendMgr = new SendManager();
			}
			try {
				Integer maximumSends = pm
						.getIntegerValue(MAXIMUM_SENDS_ALLOWED);
				sendMgr.checkForMaximumSends(maximumSends.intValue(), cargo);
				sendMgr.validateItemsForSend(cargo);
			} catch (ParameterException pe) {
				logger.warn(pe.getStackTraceAsString());
			} catch (SendException e) {
				if (e.getErrorType() == SendException.MAXIMUM_SENDS) {
					UIUtilities.setDialogModel(ui, DialogScreensIfc.ERROR,
							SEND_NOT_ALLOWED, null, CommonLetterIfc.FAILURE);
					return;
				}
				if (e.getErrorType() == SendException.CANNOT_SEND) {
					
					String[] invalidItemIDs = e.getInvalidItemIDs();
					ArrayList itemList = new ArrayList();
					
		            SaleReturnLineItemIfc[] cargoItems = cargo.getItems();
		            if (cargoItems != null)
		            {
		                for (int i = 0; i < cargoItems.length; i++)
		                {
		                	boolean flag= false;
		                    for(int j=0; j < invalidItemIDs.length ; j++)
		                    {
		                    	if(cargoItems[i].getItemID().equals(invalidItemIDs[j]))
		                    		flag=true;
		                    }
		                    if(!flag)
		                    {
		                    	itemList.add(cargoItems[i]);
		                    }
		                    else
		                    {
		                    	cargoItems[i].setSendLabelCount(-1);
		                    	((SaleReturnLineItemIfc)cargo.getTransaction().getLineItems()[cargoItems[i].getLineNumber()]).setSendLabelCount(-1);
		                    }
		                    
		                }
		            }
		            
		                        
		            SaleReturnLineItemIfc[] items = (SaleReturnLineItemIfc[])itemList.toArray(new SaleReturnLineItemIfc[itemList.size()]);     
		            cargo.setItems(items);
					if(items.length>0)
						displayErrorDialog(e.getInvalidItemIDs(), bus);
					else
						displayErrorFailureDialog(e.getInvalidItemIDs(), bus);
					return;
				}

			}
		}
		SaleReturnTransactionIfc transaction = (SaleReturnTransactionIfc) cargo
				.getTransaction();
		if (!transaction.checkedCustomerPresent()) {
			int[] buttons = new int[] { DialogScreensIfc.BUTTON_YES,
					DialogScreensIfc.BUTTON_NO };
			String[] letters = new String[] { CommonLetterIfc.YES,
					CommonLetterIfc.NO };
			UIUtilities.setDialogModel(ui, DialogScreensIfc.YES_NO,
					CUSTOMER_PRESENT, null, buttons, letters);
			transaction.setCheckedCustomerPresent(true);
		} else {
			bus.mail(CommonLetterIfc.CONTINUE, BusIfc.CURRENT);
		}
	}

	// ----------------------------------------------------------------------
	/**
	 * Capture user's selection for customer present or not
	 * <P>
	 * 
	 * @param bus
	 *            Service Bus
	 **/
	// ----------------------------------------------------------------------
	public void depart(BusIfc bus) {
		// update this only when dialog for this was displayed
		// This would happen if this site departs with yes or no letters
		if (bus.getCurrentLetter().getName().equals(CommonLetterIfc.YES)
				|| bus.getCurrentLetter().getName().equals(CommonLetterIfc.NO)) {
			ItemCargo cargo = (ItemCargo) bus.getCargo();
			boolean customerPhysicallyPresent = false;
			SaleReturnTransactionIfc transaction = (SaleReturnTransactionIfc) cargo
					.getTransaction();
			if (bus.getCurrentLetter().getName().equals(CommonLetterIfc.YES)) {
				customerPhysicallyPresent = true;
			}
			transaction.setCustomerPhysicallyPresent(customerPhysicallyPresent);
		}
	}

	// ----------------------------------------------------------------------
	/**
	 * Displays Error Dialog to tell which items are not sendable
	 * <P>
	 * 
	 * @param invalidIDs
	 *            invalid ids
	 * @param bus
	 *            Service Bus
	 **/
	// ----------------------------------------------------------------------
	protected void displayErrorDialog(String[] invalidIDs, BusIfc bus) {
		// If there is any part of selected items are detected as not sendable
		// items,
		// display the error screen to tell which items are not sendable.
		String[] args = new String[3];
		Arrays.fill(args, "");
		for (int i = 0; i < invalidIDs.length && i < 3; i++) {
			args[i] = invalidIDs[i];
		}

		DialogBeanModel dialogModel = new DialogBeanModel();
		dialogModel.setResourceID("FewItemNotEligible");
		dialogModel.setType(DialogScreensIfc.YES_NO);
		dialogModel.setArgs(args);
		dialogModel.setButtonLetter(DialogScreensIfc.BUTTON_NO,
				CommonLetterIfc.FAILURE);
		dialogModel.setButtonLetter(DialogScreensIfc.BUTTON_YES,
				CommonLetterIfc.CONTINUE);
		// display dialog
		POSUIManagerIfc ui = (POSUIManagerIfc) bus
				.getManager(UIManagerIfc.TYPE);
		ui.showScreen(POSUIManagerIfc.DIALOG_TEMPLATE, dialogModel);
	}
	
	protected void displayErrorFailureDialog(String[] invalidIDs, BusIfc bus)
    {
        // If there is any part of selected items are detected as not sendable items,
        // display the error screen to tell which items are not sendable.
        String[] args = new String[3];
        Arrays.fill(args, "");
        for (int i = 0; i < invalidIDs.length && i < 3; i++)
        {
            args[i] = invalidIDs[i];
        }
        
        DialogBeanModel dialogModel = new DialogBeanModel();
        dialogModel.setResourceID("NoItemNotEligible");
        dialogModel.setType(DialogScreensIfc.ERROR);
        dialogModel.setArgs(args);
        dialogModel.setButtonLetter(DialogScreensIfc.BUTTON_OK, CommonLetterIfc.FAILURE);

        //display dialog
        POSUIManagerIfc ui = (POSUIManagerIfc)bus.getManager(UIManagerIfc.TYPE);
        ui.showScreen(POSUIManagerIfc.DIALOG_TEMPLATE, dialogModel);
    }
}
