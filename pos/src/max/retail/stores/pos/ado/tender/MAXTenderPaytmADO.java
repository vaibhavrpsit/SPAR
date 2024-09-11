/* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
  Copyright (c) 2013	MAX HyperMarkets.    All Rights Reserved.
    
   	Rev 1.0 	20/05/2013		Bhanu Priya 		Initial Draft: Paytm Changes 
* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */

package max.retail.stores.pos.ado.tender;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import max.retail.stores.domain.factory.MAXDomainObjectFactory;
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

public class MAXTenderPaytmADO extends AbstractTenderADO {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	public MAXTenderPaytmADO()
	{
		//TO - DO
	}
	protected void initializeTenderRDO() {
        tenderRDO = ((MAXDomainObjectFactory)DomainGateway.getFactory()).getTenderPaytmInstance();
	}

	public HashMap getTenderAttributes() {
		 HashMap map = new HashMap();
	     map.put(TenderConstants.TENDER_TYPE, getTenderType());
	     map.put(TenderConstants.AMOUNT, getAmount().getStringValue());
	     //Changes start for rev 1.1
	     if(((MAXTenderPaytmIfc)tenderRDO).getPaytmMobileNumber() != null)
	    //Changes end for rev 1.1
	     map.put(TenderConstants.NUMBER, new String (((MAXTenderPaytmIfc)tenderRDO).getPaytmMobileNumber()));	
	     if(((MAXTenderPaytmIfc)tenderRDO).getOrderID() != null){
	    	 map.put(MAXTenderConstants.WALLET_ORDERID, new String (((MAXTenderPaytmIfc)tenderRDO).getOrderID()));
	     }else{
	     map.put(MAXTenderConstants.WALLET_ORDERID, "0");
	     }
	     if(((MAXTenderPaytmIfc)tenderRDO).getPaytmWalletTransactionID()!= null){
	     map.put(MAXTenderConstants.WALLET_TRANSACTIONID, new String (((MAXTenderPaytmIfc)tenderRDO).getPaytmWalletTransactionID()));
	     map.put(TenderConstants.AUTH_CODE, new String (((MAXTenderPaytmIfc)tenderRDO).getPaytmWalletTransactionID()));
	     }
	     else{
		     map.put(MAXTenderConstants.WALLET_TRANSACTIONID, "0");
			 map.put(TenderConstants.AUTH_CODE, "0");
		     }
	     return map;
	}

	public TenderTypeEnum getTenderType() {
		return MAXTenderTypeEnum.PAYTM;	
	}

	public boolean isPATCash() {
		// TODO Auto-generated method stub
		return false;
	}

	public void setTenderAttributes(HashMap tenderAttributes) throws TenderException 
	{
		CurrencyIfc amount = parseAmount((String)tenderAttributes.get(TenderConstants.AMOUNT));
			tenderRDO.setAmountTender(amount);
	        ((MAXTenderPaytmIfc)tenderRDO).setPaytmMobileNumber(((String)tenderAttributes.get(TenderConstants.NUMBER)));
	        ((MAXTenderPaytmIfc)tenderRDO).setPaytmAmount(((String)tenderAttributes.get(TenderConstants.AMOUNT)));
	        ((MAXTenderPaytmIfc)tenderRDO).setFaceValue(amount);
	      
	        ((MAXTenderPaytmIfc)tenderRDO).setPaytmOrderID(((String)tenderAttributes.get(MAXTenderConstants.WALLET_ORDERID)));
	        ((MAXTenderPaytmIfc)tenderRDO).setPaytmWalletTransactionID(((String)tenderAttributes.get(MAXTenderConstants.WALLET_TRANSACTIONID)));
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
		tenderRDO = (MAXTenderPaytmIfc)rdo;

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
