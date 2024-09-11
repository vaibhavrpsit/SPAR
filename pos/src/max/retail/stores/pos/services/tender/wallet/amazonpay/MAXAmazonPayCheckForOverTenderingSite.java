package max.retail.stores.pos.services.tender.wallet.amazonpay;

import java.util.HashMap;

import max.retail.stores.pos.ado.tender.MAXTenderTypeEnum;
import oracle.retail.stores.foundation.manager.ifc.UIManagerIfc;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.pos.ado.tender.TenderConstants;
import oracle.retail.stores.pos.ado.tender.TenderErrorCodeEnum;
import oracle.retail.stores.pos.ado.tender.TenderException;
import oracle.retail.stores.pos.services.PosSiteActionAdapter;
import oracle.retail.stores.pos.services.common.CommonLetterIfc;
import oracle.retail.stores.pos.services.tender.TenderCargo;
import oracle.retail.stores.pos.ui.DialogScreensIfc;
import oracle.retail.stores.pos.ui.POSUIManagerIfc;
import oracle.retail.stores.pos.ui.beans.DialogBeanModel;

public class MAXAmazonPayCheckForOverTenderingSite extends PosSiteActionAdapter{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public void arrive(BusIfc bus)
	{
		TenderCargo cargo = (TenderCargo) bus.getCargo();
		HashMap<String, Object> tenderAttributes = cargo.getTenderAttributes();


		tenderAttributes.put(TenderConstants.TENDER_TYPE,MAXTenderTypeEnum.AMAZON_PAY);
		
				

		/*try {
			cargo.getCurrentTransactionADO().validateTenderLimits(tenderAttributes);
					
		} catch (TenderException e) {
			TenderErrorCodeEnum errorCode = e.getErrorCode();

			POSUIManagerIfc ui = (POSUIManagerIfc) bus.getManager(UIManagerIfc.TYPE);
					
			if (errorCode == TenderErrorCodeEnum.OVERTENDER_ILLEGAL) {
				DialogBeanModel model = new DialogBeanModel();
				model.setResourceID("OvertenderNotAllowed");
				model.setType(DialogScreensIfc.ERROR);
				model.setButtonLetter(DialogScreensIfc.BUTTON_OK,
						CommonLetterIfc.FAILURE);
				ui.showScreen(POSUIManagerIfc.DIALOG_TEMPLATE, model);
				return;
			}
		}*/
		bus.mail("Continue");
	}

}
