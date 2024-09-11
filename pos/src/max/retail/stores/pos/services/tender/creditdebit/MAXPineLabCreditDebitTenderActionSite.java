/* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
 *     Copyright (c) 2010 Lifestyle India Pvt Ltd.    All Rights Reserved.
 * 
 * Rev 1.2 Jul 12th 2018	Vidhya Kommareddi
 * REQ: CC Offline CR. 
 * 
 * Rev 1.1  07-12-2015 geetika.chugh 	offline credit card	
 * Rev 1.0  Aug 12, 2015 2:06:04 PM Priyanka Singh
 * Initial revision.
 * Resolution for port in 14 version
 * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
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

/**
 * Determines whether the tender is a debit or a credit
 */
public class MAXPineLabCreditDebitTenderActionSite extends PosSiteActionAdapter {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	// ----------------------------------------------------------------------
	/**
	 * This arrive method determines where to go next.
	 * 
	 * @param bus
	 *  **/
	// ----------------------------------------------------------------------
	//commented code for credit debit
	public void arrive(BusIfc bus) {
		// Get the card number from the tender attributes
		TenderCargo cargo = (TenderCargo) bus.getCargo();
		HashMap tenderAttributes = cargo.getTenderAttributes();
		// get TDO to help get card number
		CardNumberTDO tdo = null;
		try {
			tdo = (CardNumberTDO) TDOFactory.create("tdo.tender.CardNumber");
		} catch (TDOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			// create desired TDO
			tdo = new CardNumberTDO();
		}

		// evaluate card number for debit and creditfunctionality
		 String cardNumber = ((String)cargo.getTenderAttributes().get(TenderConstants.NUMBER));
        //rev 1.1 change
        EncipheredCardDataIfc cardData = (EncipheredCardDataIfc)cargo.getTenderAttributes().get(TenderConstants.ENCIPHERED_CARD_DATA);
        String truncatedNumber = cardData.getTruncatedAcctNumber();
		boolean debitable = isCardDebitable(cardData);
		boolean swiped = tdo.isCardSwiped(tenderAttributes);
		//geetika
          
	    
		// check to see if the card is a debit only or if it is creditable
		UtilityManagerIfc utility = (UtilityManagerIfc) bus.getManager(UtilityManagerIfc.TYPE);
		boolean notCreditable = false;
		if (swiped) {
			notCreditable = utility.determineCreditType(cardData).equals(CreditTypeEnum.UNKNOWN);
		} else {
			notCreditable = utility.determineCreditType(cardData).equals(CreditTypeEnum.UNKNOWN);
		}

		// 1) if card is debitable and swiped, treat as debit
		// 2) or the card is only debitable
		// 3) otherwise treat as credit
		//Rev 1.2 start --no need check of debitable here because offline(not swiped) always goes as credit. 
		 if (!swiped && notCreditable)
			{
					tenderAttributes.put(TenderConstants.TENDER_TYPE, TenderTypeEnum.CREDIT);
					bus.mail(new Letter("Next"), BusIfc.CURRENT);
			}
		//Rev 1.2 end
		 else if ((debitable && swiped) || (debitable && notCreditable)) {			
			tenderAttributes.put(TenderConstants.TENDER_TYPE, TenderTypeEnum.DEBIT);
			bus.mail(new Letter("Debit"), BusIfc.CURRENT);
		    } 		
		else {
			tenderAttributes.put(TenderConstants.TENDER_TYPE, TenderTypeEnum.CREDIT);
			bus.mail(new Letter("Credit"), BusIfc.CURRENT);
			 }
	}

	/**
	 * Determines whether a given card number is found to be debitable. This
	 * takes traning mode into account
	 * 
	 * @param cardNumber
	 *            The card number to be evaluated
	 * @return flag indicating debitable status.
	 */
	protected boolean isCardDebitable(EncipheredCardDataIfc cardNumber) {
		boolean inTraining = ContextFactory.getInstance().getContext().getRegisterADO().isInMode(RegisterMode.TRAINING);
		// all 9's in training mode is a debit card
		if (inTraining && cardNumber.equals("9999999999999999")) {
			return true;
		}

		// Use debit manager to evaluate card number
		boolean result = false;
		ADOContextIfc context = ContextFactory.getInstance().getContext();
		DebitBinRangeManager dbrManager = (DebitBinRangeManager) context.getManager(DebitBinRangeManager.TYPE);
		if (dbrManager.isDebitNumber(cardNumber)) {
			result = true;
		}
		return result;
	}
}
