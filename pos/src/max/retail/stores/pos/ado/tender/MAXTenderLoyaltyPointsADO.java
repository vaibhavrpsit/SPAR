/* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
  Copyright (c) 2013	MAX HyperMarkets.    All Rights Reserved.
    Rev 1.1     15/09/2015      Deepshikha   Changes done for loyalty points Redeemption
	Rev 1.0 	20/05/2013		Prateek		Initial Draft: Changes for TIC Customer Integration
* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */

package max.retail.stores.pos.ado.tender;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import max.retail.stores.domain.factory.MAXDomainObjectFactory;
import max.retail.stores.domain.tender.MAXTenderLoyaltyPointsIfc;
import oracle.retail.stores.commerceservices.common.currency.CurrencyIfc;
import oracle.retail.stores.common.utility.LocaleMap;
import oracle.retail.stores.domain.DomainGateway;
import oracle.retail.stores.domain.utility.EYSDomainIfc;
import oracle.retail.stores.domain.utility.LocaleConstantsIfc;
import oracle.retail.stores.pos.ado.journal.JournalConstants;
import oracle.retail.stores.pos.ado.tender.AbstractTenderADO;
import oracle.retail.stores.pos.ado.tender.TenderConstants;
import oracle.retail.stores.pos.ado.tender.TenderException;
import oracle.retail.stores.pos.ado.tender.TenderTypeEnum;

public class MAXTenderLoyaltyPointsADO extends AbstractTenderADO {

	public MAXTenderLoyaltyPointsADO()
	{
		//TO - DO
	}
	protected void initializeTenderRDO() {
        tenderRDO = ((MAXDomainObjectFactory)DomainGateway.getFactory()).getTenderLoyaltyPointsInstance();
	}

	public HashMap getTenderAttributes() {
		 HashMap map = new HashMap();
	     map.put(TenderConstants.TENDER_TYPE, getTenderType());
	     map.put(TenderConstants.AMOUNT, getAmount().getStringValue());
	     //Changes start for rev 1.1
	     if(((MAXTenderLoyaltyPointsIfc)tenderRDO).getLoyaltyCardNumber() != null)
	    //Changes end for rev 1.1
	     map.put(TenderConstants.NUMBER, new String (((MAXTenderLoyaltyPointsIfc)tenderRDO).getLoyaltyCardNumber()));

	     return map;
	}

	public TenderTypeEnum getTenderType() {
		return MAXTenderTypeEnum.LOYALTY_POINTS;	
	}

	public boolean isPATCash() {
		// TODO Auto-generated method stub
		return false;
	}

	public void setTenderAttributes(HashMap tenderAttributes) throws TenderException {

			CurrencyIfc amount = parseAmount((String)tenderAttributes.get(TenderConstants.AMOUNT));
			tenderRDO.setAmountTender(amount);
	        ((MAXTenderLoyaltyPointsIfc)tenderRDO).setLoyaltyCardNumber(((String)tenderAttributes.get(TenderConstants.NUMBER)));
	        ((MAXTenderLoyaltyPointsIfc)tenderRDO).setLoyaltyPointAmount(((String)tenderAttributes.get(TenderConstants.AMOUNT)));
	        ((MAXTenderLoyaltyPointsIfc)tenderRDO).setFaceValue(amount);
	}

	public void validate() throws TenderException {
		// nothing needs to be validated here
	}

	public Map getJournalMemento() {
        Map memento = getTenderAttributes();
        // add tender descriptor
        memento.put(JournalConstants.DESCRIPTOR, getTenderType().toString());
        return memento;
	}

	public void fromLegacy(EYSDomainIfc rdo) {
		tenderRDO = (MAXTenderLoyaltyPointsIfc)rdo;

	}

	public EYSDomainIfc toLegacy() {
		return tenderRDO;
	}

	public EYSDomainIfc toLegacy(Class arg0) {
		return toLegacy();
	}
    protected Locale getLocale()
    {
        return LocaleMap.getLocale(LocaleConstantsIfc.DEFAULT_LOCALE);
    }
}
