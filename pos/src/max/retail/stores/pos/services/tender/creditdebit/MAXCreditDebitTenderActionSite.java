/** * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * ************************
 * Copyright (c) 2016-2017 Max Hypermarket.    All Rights Reserved. 
 *
 * Rev  	1.0  	21 Dec, 2016              Ashish Yadav              Credit Card FES
 * Rev  	1.1  	01 Sep, 2020              Kumar Vaibhav             Pinelabs Integration
 * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * ***************************/
package max.retail.stores.pos.services.tender.creditdebit;

import java.util.HashMap;

import oracle.retail.stores.domain.manager.debit.DebitBinRangeManager;
import oracle.retail.stores.foundation.manager.device.EncipheredCardDataIfc;
import oracle.retail.stores.foundation.tour.application.Letter;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.pos.ado.context.ADOContextIfc;
import oracle.retail.stores.pos.ado.context.ContextFactory;
import oracle.retail.stores.pos.ado.store.RegisterMode;
import oracle.retail.stores.pos.ado.tender.CreditTypeEnum;
import oracle.retail.stores.pos.ado.tender.TenderConstants;
import oracle.retail.stores.pos.ado.tender.TenderTypeEnum;
import oracle.retail.stores.pos.manager.ifc.UtilityManagerIfc;
import oracle.retail.stores.pos.services.PosSiteActionAdapter;
import oracle.retail.stores.pos.services.tender.TenderCargo;
import oracle.retail.stores.pos.services.tender.tdo.CardNumberTDO;
import oracle.retail.stores.pos.tdo.TDOException;
import oracle.retail.stores.pos.tdo.TDOFactory;

public class MAXCreditDebitTenderActionSite extends PosSiteActionAdapter {
	public void arrive(BusIfc bus) {
		TenderCargo cargo = (TenderCargo) bus.getCargo();
		HashMap tenderAttributes = cargo.getTenderAttributes();

		CardNumberTDO tdo = null;
		try {
			tdo = (CardNumberTDO) TDOFactory.create("tdo.tender.CardNumber");
		} catch (TDOException e) {
			e.printStackTrace();

			tdo = new CardNumberTDO();
		}
		EncipheredCardDataIfc cardData = (EncipheredCardDataIfc)cargo.getTenderAttributes().get(TenderConstants.ENCIPHERED_CARD_DATA);
		boolean debitable = isCardDebitable(cardData);
		boolean swiped = tdo.isCardSwiped(tenderAttributes);

		UtilityManagerIfc utility = (UtilityManagerIfc) bus.getManager("UtilityManager");
		boolean notCreditable = false;
		if (swiped) {
			notCreditable = utility.determineCreditType(cardData)
					.equals(CreditTypeEnum.UNKNOWN);
		} else {
			notCreditable = utility.determineCreditType(cardData)
					.equals(CreditTypeEnum.UNKNOWN);
		}

		if (((debitable) && (swiped)) || ((debitable) && (notCreditable))) {
			tenderAttributes.put("TENDER_TYPE", TenderTypeEnum.DEBIT);
			bus.mail(new Letter("Debit"), BusIfc.CURRENT);
		} else {
			tenderAttributes.put("TENDER_TYPE", TenderTypeEnum.CREDIT);
			bus.mail(new Letter("Credit"), BusIfc.CURRENT);
		}
	}

	protected boolean isCardDebitable(EncipheredCardDataIfc cardNumber) {
		boolean inTraining = ContextFactory.getInstance().getContext().getRegisterADO().isInMode(RegisterMode.TRAINING);

		if ((inTraining) && (cardNumber.equals("9999999999999999"))) {
			return true;
		}

		boolean result = false;
		ADOContextIfc context = ContextFactory.getInstance().getContext();
		DebitBinRangeManager dbrManager = (DebitBinRangeManager) context.getManager("DebitBinRangeManager");
		if (dbrManager.isDebitNumber(cardNumber)) {
			result = true;
		}
		return result;
	}
}