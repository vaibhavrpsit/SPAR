/* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
  Copyright (c) 2016	MAX HyperMarkets.    All Rights Reserved.
 
	Rev 1.0 	12/07/2016		Abhishek Goyal		Initial Draft: Changes for CR
* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
package max.retail.stores.pos.ado.tender;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import max.retail.stores.domain.factory.MAXDomainObjectFactory;
import max.retail.stores.domain.tender.MAXTenderEComPrepaidIfc;
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

public class MAXTenderEComPrepaidADO extends AbstractTenderADO{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public MAXTenderEComPrepaidADO()
	{
		//TO - DO
	}
	
	protected void initializeTenderRDO() {
        tenderRDO = ((MAXDomainObjectFactory)DomainGateway.getFactory()).getTenderEComPrepaidInstance();
	}
	
	public HashMap getTenderAttributes() {
		// TODO Auto-generated method stub
		 HashMap map = new HashMap();
	     map.put(TenderConstants.TENDER_TYPE, getTenderType());
	     map.put(TenderConstants.AMOUNT, getAmount().getStringValue());
	     return map;
	}

	public TenderTypeEnum getTenderType() {
		// TODO Auto-generated method stub
		return MAXTenderTypeEnum.ECOM_PREPAID;	
	}

	public boolean isPATCash() {
		// TODO Auto-generated method stub
		return false;
	}

	public void setTenderAttributes(HashMap tenderAttributes) throws TenderException {
		// TODO Auto-generated method stub
		CurrencyIfc amount = parseAmount((String)tenderAttributes.get(TenderConstants.AMOUNT));
		tenderRDO.setAmountTender(amount);
	}

	public void validate() throws TenderException {
		// TODO Auto-generated method stub
		
	}

	public Map getJournalMemento() {
		// TODO Auto-generated method stub
		 Map memento = getTenderAttributes();
	        // add tender descriptor
	     memento.put(JournalConstants.DESCRIPTOR, getTenderType().toString());
	     return memento;
	}

	public void fromLegacy(EYSDomainIfc rdo) {
		// TODO Auto-generated method stub
		tenderRDO = (MAXTenderEComPrepaidIfc)rdo;
	}

	public EYSDomainIfc toLegacy() {
		// TODO Auto-generated method stub
		return tenderRDO;
	}

	public EYSDomainIfc toLegacy(Class arg0) {
		// TODO Auto-generated method stub
		return toLegacy();
	}
	
	protected Locale getLocale()
	{
		return LocaleMap.getLocale(LocaleConstantsIfc.DEFAULT_LOCALE);
    }
}
