/* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *

  Copyright (c) 1998-2004 360Commerce, Inc.    All Rights Reserved.

     $Log:
      3    360Commerce 1.2         3/31/2005 4:30:42 PM   Robert Pearse   
      2    360Commerce 1.1         3/10/2005 10:26:40 AM  Robert Pearse   
      1    360Commerce 1.0         2/11/2005 12:15:28 PM  Robert Pearse   
     $
     Revision 1.4.2.1  2004/10/15 18:50:28  kmcbride
     Merging in trunk changes that occurred during branching activity

     Revision 1.5  2004/10/12 18:12:17  bwf
     @scr 3956 Remove unnecessary mail of success letter.

     Revision 1.4  2004/07/31 16:09:37  bwf
     @scr 6551 Enable credit auth charge confirmation.

     Revision 1.3  2004/07/15 16:25:26  bwf
     @scr 6049 fixed errors caused by enabling debit exp date.

     Revision 1.2  2004/07/14 18:47:09  epd
     @scr 5955 Addressed issues with Utility class by making constructor protected and changing all usages to use factory method rather than direct instantiation

     Revision 1.1  2004/07/12 21:42:19  bwf
     @scr 6125 Made available expiration validation of debit before pin.

 * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
package max.retail.stores.pos.services.tender.creditdebit;

import java.util.HashMap;

import oracle.retail.stores.domain.manager.debit.DebitBinRangeManager;
import oracle.retail.stores.foundation.manager.device.EncipheredCardDataIfc;
import oracle.retail.stores.foundation.manager.device.MSRModel;
import oracle.retail.stores.foundation.manager.ifc.UIManagerIfc;
import oracle.retail.stores.foundation.tour.application.Letter;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.pos.ado.ADOException;
import oracle.retail.stores.pos.ado.context.ADOContextIfc;
import oracle.retail.stores.pos.ado.context.ContextFactory;
import oracle.retail.stores.pos.ado.factory.ADOFactoryComplex;
import oracle.retail.stores.pos.ado.factory.TenderFactoryIfc;
import oracle.retail.stores.pos.ado.tender.AbstractCardTender;
import oracle.retail.stores.pos.ado.tender.TenderADOIfc;
import oracle.retail.stores.pos.ado.tender.TenderConstants;
import oracle.retail.stores.pos.ado.tender.TenderErrorCodeEnum;
import oracle.retail.stores.pos.ado.tender.TenderException;
import oracle.retail.stores.pos.manager.ifc.UtilityManagerIfc;
import oracle.retail.stores.pos.services.PosSiteActionAdapter;
import oracle.retail.stores.pos.services.common.CommonLetterIfc;
import oracle.retail.stores.pos.services.tender.TenderCargo;
import oracle.retail.stores.pos.ui.DialogScreensIfc;
import oracle.retail.stores.pos.ui.POSUIManagerIfc;
import oracle.retail.stores.pos.ui.beans.DialogBeanModel;

//--------------------------------------------------------------------------
/**
 * This class will validate the expiration date and throw an error message or
 * continue on. $Revision: 1.2 $
 **/
// --------------------------------------------------------------------------
public class MAXPineLabInstaCreditValidateExpirationDateSite extends PosSiteActionAdapter {
	// ----------------------------------------------------------------------
	/**
	 * This method creates a tender to check the expiration date.
	 * 
	 * @param bus
	 * @see com.extendyourstore.foundation.tour.ifc.SiteActionIfc#arrive(com.extendyourstore.foundation.tour.ifc.BusIfc)
	 **/
	// ----------------------------------------------------------------------
	public void arrive(BusIfc bus) {
		TenderCargo cargo = (TenderCargo) bus.getCargo();
		HashMap tenderAttributes = cargo.getTenderAttributes();
		TenderADOIfc cardTender = null;
		try {
			TenderFactoryIfc factory = (TenderFactoryIfc) ADOFactoryComplex.getFactory("factory.tender");
			cardTender = factory.createTender(tenderAttributes);
			if (cardTender instanceof AbstractCardTender) {
				((AbstractCardTender) cardTender).validate();
			}

			cargo.setTenderADO(cardTender);
		} catch (ADOException adoe) {
			adoe.printStackTrace();
		} catch (TenderException e) {
			TenderErrorCodeEnum error = e.getErrorCode();
			POSUIManagerIfc ui = (POSUIManagerIfc) bus.getManager(UIManagerIfc.TYPE);
			UtilityManagerIfc utility = (UtilityManagerIfc) bus.getManager(UtilityManagerIfc.TYPE);

			if (error == TenderErrorCodeEnum.EXPIRED) {
				cargo.setPreTenderMSRModel(null);
				showExpiredCardDialog(utility, ui, bus);
				return;
			} else if (error == TenderErrorCodeEnum.INVALID_EXPIRATION_DATE) {
				logger.error(e);
			} else {
				assert (false) : "Unhandled exception.  This should not happen";
				logger.error(e);
			}
		}
		if (("SwipeWithOutExp".equals(bus.getCurrentLetter().getName())))
			bus.mail(new Letter("WithOutExpVal"), BusIfc.CURRENT);
		else
			bus.mail(new Letter("Continue"), BusIfc.CURRENT);
	}

	// --------------------------------------------------------------------------
	/**
	 * Shows the expired card dialog screen.
	 */
	// --------------------------------------------------------------------------
	protected void showExpiredCardDialog(UtilityManagerIfc utility, POSUIManagerIfc ui, BusIfc bus) {
		// set screen args
		String titleTag = "ExpiredDebitCardTitle";
		String cardString = utility.retrieveDialogText("ExpiredCardError.Debit", "debit");

		// check whether it is a debit or credit card
		ADOContextIfc context = ContextFactory.getInstance().getContext();
		DebitBinRangeManager dbrManager = (DebitBinRangeManager) context.getManager(DebitBinRangeManager.TYPE);
		TenderCargo cargo = (TenderCargo) bus.getCargo();
		MSRModel msrModel = ((MSRModel) cargo.getTenderAttributes().get(TenderConstants.MSR_MODEL));
		String cardNumber = null;
		if (msrModel != null) {
			cardNumber = msrModel.getAccountNumber();
		}
        EncipheredCardDataIfc cardData = (EncipheredCardDataIfc)cargo.getTenderAttributes().get(TenderConstants.ENCIPHERED_CARD_DATA);


		// we know bin file lookup is true because this is the only place that
		// flow occurs
		if (!dbrManager.isDebitNumber(cardData)) {
			titleTag = "ExpiredCreditCardTitle";
			cardString = utility.retrieveDialogText("ExpiredCardError.Credit", "Credit");
		}

		// Display error message
		DialogBeanModel dialogModel = new DialogBeanModel();
		dialogModel.setResourceID("ExpiredCardError");
		dialogModel.setType(DialogScreensIfc.ACKNOWLEDGEMENT);
		dialogModel.setButtonLetter(DialogScreensIfc.BUTTON_OK, CommonLetterIfc.INVALID);
		dialogModel.setTitleTag(titleTag);
		String args[] = new String[2];
		args[0] = cardString;
		args[1] = cardString;
		dialogModel.setArgs(args);
		ui.showScreen(POSUIManagerIfc.DIALOG_TEMPLATE, dialogModel);
	}
}
