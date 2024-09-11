/* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
 *Copyright (c) 2010 Lifestyle India Pvt Ltd.    All Rights Reserved.
 *Upgraded to ORPOS 14.0.1 from Lifestyle ORPOS 12.0.9IN: AAKASH GUPTA(EYLLP):Aug-11-2015
 *   
 * Rev 1.0  	Dec 08, 2014  	Shavinki Goyal  Resolution for LSIPL-FES:-Multiple Tender using Innoviti  
 * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
package max.retail.stores.pos.services.tender.creditdebit;

import java.util.HashMap;

import max.retail.stores.pos.services.tender.MAXTenderCargo;
import oracle.retail.stores.foundation.manager.ifc.UIManagerIfc;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.pos.ado.tender.TenderConstants;
import oracle.retail.stores.pos.ado.tender.TenderErrorCodeEnum;
import oracle.retail.stores.pos.ado.tender.TenderException;
import oracle.retail.stores.pos.services.PosLaneActionAdapter;
import oracle.retail.stores.pos.services.common.CommonLetterIfc;
import oracle.retail.stores.pos.ui.DialogScreensIfc;
import oracle.retail.stores.pos.ui.POSUIManagerIfc;
import oracle.retail.stores.pos.ui.beans.DialogBeanModel;

/**
 * @author shavinki.goyal
 *
 */
public class MAXPineLabCreditEMIAmountEnteredAisle extends PosLaneActionAdapter 
{
	/**
	 * 
	 */
	private static final long serialVersionUID = 6196691313957433236L;

	public void traverse(BusIfc bus) 
	{
		MAXTenderCargo tenderCargo = (MAXTenderCargo)bus.getCargo();
		HashMap tenderAttributes = tenderCargo.getTenderAttributes();

		POSUIManagerIfc ui = (POSUIManagerIfc) bus.getManager(UIManagerIfc.TYPE);
		String amt =  ui.getInput();
		tenderAttributes.put(TenderConstants.AMOUNT, amt);
		try 
		{
			// invoke tender limit validation
			tenderCargo.getCurrentTransactionADO().validateTenderLimits(tenderAttributes);
		} 
		catch (TenderException te) 
		{
			TenderErrorCodeEnum errorCode = te.getErrorCode();
			if (errorCode == TenderErrorCodeEnum.OVERTENDER_ILLEGAL) 
			{
				displayErrorDialog(ui, "OvertenderNotAllowed", null, DialogScreensIfc.ERROR);
				return;
			}
		}
		bus.mail("EMI", BusIfc.CURRENT);

	}

	// ----------------------------------------------------------------------
	/**
	 * Displays the specified Dialog.
	 * 
	 * @param ui
	 *            UI Manager to handle the IO
	 * @param name
	 *            name of the Error Dialog to display
	 * @param args
	 *            arguments for the dialog screen
	 * @param type
	 *            the dialog type
	 *  **/
	// ----------------------------------------------------------------------
	private void displayErrorDialog(POSUIManagerIfc ui, String name, String[] args, int type) 
	{
		DialogBeanModel dialogModel = new DialogBeanModel();
		dialogModel.setResourceID(name);

		if (args != null)
		{
			dialogModel.setArgs(args);
		}
		dialogModel.setType(type);
		dialogModel.setButtonLetter(DialogScreensIfc.BUTTON_OK, CommonLetterIfc.FAILURE);
		ui.showScreen(POSUIManagerIfc.DIALOG_TEMPLATE, dialogModel);
	}

}
