/** * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * ************************
 * Copyright (c) 2016-2017 Max Hypermarket.    All Rights Reserved. 
 *
 * Rev  	1.0  	21 Dec, 2016              Ashish Yadav              Credit Card FES
 * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * ***************************/
package max.retail.stores.pos.services.tender.creditdebit;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;

import oracle.retail.stores.domain.manager.debit.DebitBinRangeManager;
import oracle.retail.stores.domain.utility.EYSDate;
import oracle.retail.stores.foundation.manager.device.EncipheredCardDataIfc;
import oracle.retail.stores.foundation.manager.device.MSRModel;
import oracle.retail.stores.foundation.tour.application.Letter;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.pos.ado.ADOException;
import oracle.retail.stores.pos.ado.context.ADOContextIfc;
import oracle.retail.stores.pos.ado.context.ContextFactory;
import oracle.retail.stores.pos.ado.factory.ADOFactoryComplex;
import oracle.retail.stores.pos.ado.factory.TenderFactoryIfc;
import oracle.retail.stores.pos.ado.tender.TenderADOIfc;
import oracle.retail.stores.pos.ado.tender.TenderConstants;
import oracle.retail.stores.pos.ado.tender.TenderErrorCodeEnum;
import oracle.retail.stores.pos.ado.tender.TenderException;
import oracle.retail.stores.pos.ado.utility.Utility;
import oracle.retail.stores.pos.ado.utility.UtilityIfc;
import oracle.retail.stores.pos.manager.ifc.UtilityManagerIfc;
import oracle.retail.stores.pos.services.PosSiteActionAdapter;
import oracle.retail.stores.pos.services.common.CommonLetterIfc;
import oracle.retail.stores.pos.services.tender.TenderCargo;
import oracle.retail.stores.pos.ui.POSUIManagerIfc;
import oracle.retail.stores.pos.ui.beans.DialogBeanModel;

public class MAXValidateExpirationDateSite extends PosSiteActionAdapter {
	public void arrive(BusIfc bus) {
		TenderCargo cargo = (TenderCargo) bus.getCargo();
		//HashMap tenderAttributes = cargo.getTenderAttributes();
		HashMap<String,Object> tenderAttributes = cargo.getTenderAttributes();
		TenderADOIfc cardTender = null;
		String expirationDate = (String)tenderAttributes.get(TenderConstants.EXPIRATION_DATE);
		EncipheredCardDataIfc cardData = (EncipheredCardDataIfc)tenderAttributes.get(TenderConstants.ENCIPHERED_CARD_DATA);
		try {
			TenderFactoryIfc factory = (TenderFactoryIfc) ADOFactoryComplex.getFactory("factory.tender");
			cardTender = factory.createTender(tenderAttributes);
			validateExpirationDate(expirationDate);
			/*if (cardTender instanceof AbstractCardTender) {
				((AbstractCardTender) cardTender).validateExpirationDate();
			}*/
            if(tenderAttributes.get(TenderConstants.ENCIPHERED_CARD_DATA)!=null){
            if (cardData != null)
            {
                cardData.setEncryptedExpirationDate(expirationDate);
            }
            else
            {
                String message = "EncipheredCardData not found.  This should not happen";
                logger.error(message);
                throw new RuntimeException(message);
           }
            }
			cargo.setTenderADO(cardTender);
		} catch (ADOException adoe) {
			adoe.printStackTrace();
		} catch (TenderException e) {
			TenderErrorCodeEnum error = e.getErrorCode();
			POSUIManagerIfc ui = (POSUIManagerIfc) bus.getManager("UIManager");
			UtilityManagerIfc utility = (UtilityManagerIfc) bus.getManager("UtilityManager");

			if (error == TenderErrorCodeEnum.EXPIRED) {
				cargo.setPreTenderMSRModel(null);
				showExpiredCardDialog(utility, ui, bus,cardData);
				return;
			}
			if (error == TenderErrorCodeEnum.INVALID_EXPIRATION_DATE) {
				logger.error(e);
			} else {
				 String message = "Unhandled exception.  This should not happen";
	                logger.error(message, e);
	                throw new RuntimeException(message, e);
			}
		}

		if (("SwipeWithOutExp".equals(bus.getCurrentLetter().getName())))
			bus.mail(new Letter("WithOutExpVal"), BusIfc.CURRENT);
		else
        bus.mail(new Letter(CommonLetterIfc.CONTINUE), BusIfc.CURRENT);
		//bus.mail(new Letter("Continue"), BusIfc.CURRENT);
	}
	
	protected void validateExpirationDate(String expirationDate)
		    throws TenderException
		    {
		        if(expirationDate == null)
		        {
		            throw new TenderException("Expiration Date must be present",TenderErrorCodeEnum.INVALID_EXPIRATION_DATE);
		        }
		        

		        UtilityIfc util = getUtility();
		       //expiration date parse issue akhilesh  start    
		        EYSDate expDate=util.parseExpirationDate("MM/yyyy", expirationDate);
		       //expiration date parse issue akhilesh end     
		        
		       validateExpirationDate1(expDate);
		    }
	protected UtilityIfc getUtility()
    {
        UtilityIfc util = null;
        try
        {
            util = Utility.createInstance();
        }
        catch (ADOException e)
        {
            logger.error(e);
            throw new RuntimeException("Configuration problem: could not create instance of UtilityIfc");
        }
        return util;
    }
	public synchronized void validateExpirationDate1(EYSDate expirationDate) throws TenderException
    {
        GregorianCalendar gc = (GregorianCalendar)Calendar.getInstance();

        // get today's date and from that get the current
        // Month and Year.
        Date today = new Date();
        gc.setTime(today);
        int thisMonth = gc.get(Calendar.MONTH);
        int thisYear  = gc.get(Calendar.YEAR);
        gc.clear();

        // reset the calendar with this tender's Exp. Date
        Calendar c = expirationDate.calendarValue();
        gc.setTime(c.getTime());

        // if expiration date is before today and
        // the month and year both do not match the
        // current month and year, the card must be expired.
        EYSDate todayEYS = new EYSDate(today);
        if (expirationDate.before(todayEYS) &&
                        !((thisMonth == gc.get(Calendar.MONTH)) &&
                                        (thisYear == gc.get(Calendar.YEAR))))
        {
            throw new TenderException("Expired", TenderErrorCodeEnum.EXPIRED);
        }
    }
	protected void showExpiredCardDialog(UtilityManagerIfc utility, POSUIManagerIfc ui, BusIfc bus,EncipheredCardDataIfc cardData) {
		String titleTag = "ExpiredDebitCardTitle";
		String cardString = utility.retrieveDialogText("ExpiredCardError.Debit", "debit");

		ADOContextIfc context = ContextFactory.getInstance().getContext();
		DebitBinRangeManager dbrManager = (DebitBinRangeManager) context.getManager("DebitBinRangeManager");
		TenderCargo cargo = (TenderCargo) bus.getCargo();
		MSRModel msrModel = (MSRModel) cargo.getTenderAttributes().get("MSR_MODEL");
		String cardNumber = null;
		if (msrModel != null) {
			cardNumber = msrModel.getAccountNumber();
		}

		if (!(dbrManager.isDebitNumber(cardData))) {
			titleTag = "ExpiredCreditCardTitle";
			cardString = utility.retrieveDialogText("ExpiredCardError.Credit", "Credit");
		}

		DialogBeanModel dialogModel = new DialogBeanModel();
		dialogModel.setResourceID("ExpiredCardError");
		dialogModel.setType(7);
		dialogModel.setButtonLetter(0, "Invalid");

		dialogModel.setTitleTag(titleTag);
		String[] args = new String[2];
		args[0] = cardString;
		args[1] = cardString;
		dialogModel.setArgs(args);
		ui.showScreen("DIALOG_TEMPLATE", dialogModel);
	}
}