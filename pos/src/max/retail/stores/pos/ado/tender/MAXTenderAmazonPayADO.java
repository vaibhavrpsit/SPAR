/********************************************************************************
 *   
 *	Copyright (c) 2019 MAX SPAR Hypermarket, Inc    All Rights Reserved.
 *	
 *	Rev	1.0 	Jul 01, 2019		Purushotham Reddy 		Changes for POS_Amazon Pay Integration 
 *
 ********************************************************************************/

package max.retail.stores.pos.ado.tender;

/**
@author Purushotham Reddy Sirison
**/

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import max.retail.stores.domain.factory.MAXDomainObjectFactory;
import max.retail.stores.domain.tender.MAXTenderAmazonPayIfc;
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

public class MAXTenderAmazonPayADO extends AbstractTenderADO {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	public MAXTenderAmazonPayADO()
	{
		//TO - DO
	}
	protected void initializeTenderRDO() {
        tenderRDO = ((MAXDomainObjectFactory)DomainGateway.getFactory()).getTenderAmazonPayInstance();
	}

	public HashMap getTenderAttributes() {
		HashMap map = new HashMap();
		map.put(TenderConstants.TENDER_TYPE, getTenderType());
		map.put(TenderConstants.AMOUNT, getAmount().getStringValue());
		// Changes start for rev 1.1
		if (((MAXTenderAmazonPayIfc) tenderRDO).getAmazonPayMobileNumber() != null)
			// Changes end for rev 1.1
			map.put(TenderConstants.NUMBER,
					new String(((MAXTenderAmazonPayIfc) tenderRDO)
							.getAmazonPayMobileNumber()));
		if (((MAXTenderAmazonPayIfc) tenderRDO).getAmazonPayOrderID() != null) {
			map.put(MAXTenderConstants.WALLET_ORDERID, new String(
					((MAXTenderAmazonPayIfc) tenderRDO).getAmazonPayOrderID()));
		} else {
			map.put(MAXTenderConstants.WALLET_ORDERID, "0");
		}
		if (((MAXTenderAmazonPayIfc) tenderRDO)
				.getAmazonPayWalletTransactionID() != null) {
			map.put(MAXTenderConstants.WALLET_TRANSACTIONID,
					new String(((MAXTenderAmazonPayIfc) tenderRDO)
							.getAmazonPayWalletTransactionID()));
			map.put(TenderConstants.AUTH_CODE,
					new String(((MAXTenderAmazonPayIfc) tenderRDO)
							.getAmazonPayWalletTransactionID()));
		} else {
			map.put(MAXTenderConstants.WALLET_TRANSACTIONID, "0");
			map.put(TenderConstants.AUTH_CODE, "0");
		}
		return map;
	}

	public TenderTypeEnum getTenderType() {
		return MAXTenderTypeEnum.AMAZON_PAY;	
	}

	public boolean isPATCash() {
		return false;
	}

	public void setTenderAttributes(HashMap tenderAttributes) throws TenderException 
	{
		CurrencyIfc amount = parseAmount((String)tenderAttributes.get(TenderConstants.AMOUNT));
			tenderRDO.setAmountTender(amount);
	        ((MAXTenderAmazonPayIfc)tenderRDO).setAmazonPayMobileNumber(((String)tenderAttributes.get(TenderConstants.NUMBER)));
	        ((MAXTenderAmazonPayIfc)tenderRDO).setAmazonPayAmount(((String)tenderAttributes.get(TenderConstants.AMOUNT)));
	        ((MAXTenderAmazonPayIfc)tenderRDO).setFaceValue(amount);
	        ((MAXTenderAmazonPayIfc)tenderRDO).setAmazonPayOrderID(((String)tenderAttributes.get(MAXTenderConstants.WALLET_ORDERID)));
	        ((MAXTenderAmazonPayIfc)tenderRDO).setAmazonPayWalletTransactionID(((String)tenderAttributes.get(MAXTenderConstants.AUTH_CODE)));
	}

	public void validate() throws TenderException {
	
	}

	public Map getJournalMemento() {
        Map memento = getTenderAttributes();
        // add tender descriptor
     //   memento.put(JournalConstants.DESCRIPTOR, getTenderType().toString());
        return memento;
	}

	public void fromLegacy(EYSDomainIfc rdo) {
		tenderRDO = (MAXTenderAmazonPayIfc)rdo;

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
