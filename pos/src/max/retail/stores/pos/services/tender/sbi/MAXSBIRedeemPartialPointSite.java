/* ===========================================================================
 *  Copyright (c) 2017 Lifestyle India Pvt Ltd.    All Rights Reserved. 
 * ===========================================================================
 *
 * Rev 1.0  April 20,2021     Mohan Yadav  SBI reward points integration
 *
 * ===========================================================================
 */
package max.retail.stores.pos.services.tender.sbi;

import oracle.retail.stores.foundation.manager.ifc.UIManagerIfc;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.pos.services.PosSiteActionAdapter;
import oracle.retail.stores.pos.services.common.CommonLetterIfc;
import oracle.retail.stores.pos.ui.DialogScreensIfc;
import oracle.retail.stores.pos.ui.POSUIManagerIfc;
import oracle.retail.stores.pos.ui.beans.DialogBeanModel;

/**
 * Create a cash tender and attempt to add it to the transaction. If validation
 * fails, either punt, or attempt override, depending on the problem.
 */
public class MAXSBIRedeemPartialPointSite extends PosSiteActionAdapter
{
	private static final long serialVersionUID = 4340745363476760442L;

	@Override
	public void arrive(BusIfc bus)
	{
		POSUIManagerIfc uiManager=(POSUIManagerIfc)bus.getManager(UIManagerIfc.TYPE);
		DialogBeanModel dialogModel = new DialogBeanModel();
		dialogModel.setResourceID(" ");
		dialogModel.setType(DialogScreensIfc.CONFIRMATION);
		dialogModel.setButtonLetter(DialogScreensIfc.BUTTON_YES, CommonLetterIfc.YES);
		dialogModel.setButtonLetter(DialogScreensIfc.BUTTON_NO, CommonLetterIfc.NO);
		uiManager.showScreen(POSUIManagerIfc.DIALOG_TEMPLATE, dialogModel);

	}
	
	

}
