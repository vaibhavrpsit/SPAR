/* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
  Copyright (c) 2013	MAX HyperMarkets.    All Rights Reserved.
    
   	Rev 1.0 	20/05/2013		Bhanu Priya 		Initial Draft: Mobikwik Changes 
* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */

package max.retail.stores.pos.ado.tender;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import max.retail.stores.domain.factory.MAXDomainObjectFactory;
import max.retail.stores.domain.tender.MAXTenderMobikwikIfc;
import max.retail.stores.domain.tender.MAXTenderPaytmIfc;
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

public class MAXTenderMobikwikADO extends AbstractTenderADO {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	public MAXTenderMobikwikADO()
	{
		//TO - DO
	}
	protected void initializeTenderRDO() {
        tenderRDO = ((MAXDomainObjectFactory)DomainGateway.getFactory()).getTenderMobikwikInstance();
	}

	public HashMap getTenderAttributes() {
		 HashMap map = new HashMap();
	     map.put(TenderConstants.TENDER_TYPE, getTenderType());
	     map.put(TenderConstants.AMOUNT, getAmount().getStringValue());
	     //Changes start for rev 1.1
	     if(((MAXTenderMobikwikIfc)tenderRDO).getMobikwikMobileNumber() != null)
	    //Changes end for rev 1.1
	     map.put(TenderConstants.NUMBER, new String (((MAXTenderMobikwikIfc)tenderRDO).getMobikwikMobileNumber()));	
	     if(((MAXTenderMobikwikIfc)tenderRDO).getMobikwikOrderID() != null){
	    	 map.put(MAXTenderConstants.WALLET_ORDERID, new String (((MAXTenderMobikwikIfc)tenderRDO).getMobikwikOrderID()));
	     }else{
	     map.put(MAXTenderConstants.WALLET_ORDERID, "0");
	     }
	     if(((MAXTenderMobikwikIfc)tenderRDO).getMobikwikWalletTransactionID()!= null){
	     map.put(MAXTenderConstants.WALLET_TRANSACTIONID, new String (((MAXTenderMobikwikIfc)tenderRDO).getMobikwikWalletTransactionID()));
	     map.put(TenderConstants.AUTH_CODE, new String (((MAXTenderMobikwikIfc)tenderRDO).getMobikwikWalletTransactionID()));
	     }
	     else{
		     map.put(MAXTenderConstants.WALLET_TRANSACTIONID, "0");
		     map.put(TenderConstants.AUTH_CODE, "0");
		     }
	     return map;
	}

	public TenderTypeEnum getTenderType() {
		return MAXTenderTypeEnum.MOBIKWIK;	
	}

	public boolean isPATCash() {
		// TODO Auto-generated method stub
		return false;
	}

	public void setTenderAttributes(HashMap tenderAttributes) throws TenderException 
	{
		CurrencyIfc amount = parseAmount((String)tenderAttributes.get(TenderConstants.AMOUNT));
			tenderRDO.setAmountTender(amount);
	        ((MAXTenderMobikwikIfc)tenderRDO).setMobikwikMobileNumber(((String)tenderAttributes.get(TenderConstants.NUMBER)));
	        ((MAXTenderMobikwikIfc)tenderRDO).setMobikwikAmount(((String)tenderAttributes.get(TenderConstants.AMOUNT)));
	        ((MAXTenderMobikwikIfc)tenderRDO).setFaceValue(amount);
	      
	        ((MAXTenderMobikwikIfc)tenderRDO).setMobikwikOrderID(((String)tenderAttributes.get(MAXTenderConstants.WALLET_ORDERID)));
	        ((MAXTenderMobikwikIfc)tenderRDO).setMobikwikWalletTransactionID(((String)tenderAttributes.get(MAXTenderConstants.WALLET_TRANSACTIONID)));
	}

	public void validate() throws TenderException {
	
	}

	public Map getJournalMemento() {
        Map memento = getTenderAttributes();
        // add tender descriptor
        memento.put(JournalConstants.DESCRIPTOR, getTenderType().toString());
        return memento;
	}

	public void fromLegacy(EYSDomainIfc rdo) {
		tenderRDO = (MAXTenderMobikwikIfc)rdo;

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
