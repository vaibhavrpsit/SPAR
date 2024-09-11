/** * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * ************************
 * Copyright (c) 2018-2019 Max SPAR Hypermarket.    All Rights Reserved. 
 * 
 * Rev 1.4		Jun 01, 2019			Purushotham Reddy   	Changes  for POS-Amazon Pay Integration
 * Rev 1.3  	23 Nov, 2017            Bhanu Priya          	Changes done for Mobikwik FES
 * Rev 1.2  	16 Oct, 2017            Bhanu Priya          	Changes done for paytm fes
 * Rev 1.0  	08 Nov, 2016            Nadia              		MAX-StoreCredi_Return requirement.
 * 
 * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * ***************************/

package max.retail.stores.domain.factory;

import java.util.Locale;

import max.retail.stores.domain.lineitem.MAXMaximumRetailPriceChangeIfc;
import max.retail.stores.domain.manager.centralvalidation.MAXCentralizedDataEntryIfc;
import max.retail.stores.domain.tax.MAXTaxAssignmentIfc;
import max.retail.stores.domain.tax.MAXTaxInformationIfc;
import max.retail.stores.domain.tender.MAXTenderAmazonPayIfc;
import max.retail.stores.domain.tender.MAXTenderLoyaltyPointsIfc;
import max.retail.stores.domain.tender.MAXTenderMobikwikIfc;
import max.retail.stores.domain.tender.MAXTenderPaytmIfc;
import max.retail.stores.domain.transaction.MAXTransactionTotalsIfc;
import oracle.retail.stores.domain.factory.DomainObjectFactoryIfc;
import oracle.retail.stores.domain.stock.GiftCardPLUItemIfc;
import oracle.retail.stores.domain.tender.TenderChargeIfc;
import oracle.retail.stores.domain.tender.TenderPurchaseOrderIfc;
import oracle.retail.stores.domain.transaction.SaleReturnTransactionIfc;
import oracle.retail.stores.domain.utility.GiftCardIfc;

public interface MAXDomainObjectFactoryIfc extends DomainObjectFactoryIfc {

	/** Rev 1.0 Change : Start **/
	public SaleReturnTransactionIfc getSaleReturnTransactionInstance(Locale locale);

	public GiftCardIfc getGiftCardInstance(Locale locale);

	public GiftCardPLUItemIfc getGiftCardPLUItemInstance(Locale locale);

	public TenderChargeIfc getTenderChargeInstance(Locale locale);

	/** Rev 1.1 Change : End **/

	// MAX Changes for Rev 1.1 - Start
	public MAXCentralizedDataEntryIfc getCentralizedDataEntryInstance(Locale locale);
	// MAX Changes for Rev 1.1 - End

	/**
	 * Rev 1.2 changes start here
	 */
	public TenderPurchaseOrderIfc getTenderPurchaseOrderInstance();

	public TenderPurchaseOrderIfc getTenderPurchaseOrderInstance(Locale locale);

	/**
	 * Rev 1.2 changes end here
	 */
	/** MAX Rev 1.3 Change : Start **/
	public MAXTenderLoyaltyPointsIfc getTenderLoyaltyPointsInstance();

	/** MAX Rev 1.3 Change : End **/

	public MAXTaxAssignmentIfc getTaxAssignmentInstance();

	public MAXMaximumRetailPriceChangeIfc getMaximumRetailPriceChangeInstance();
	
	public MAXTaxInformationIfc getTaxInformationInstance();

	public MAXTaxInformationIfc getTaxInformationInstance(Locale locale);
	
	public MAXTransactionTotalsIfc getTransactionTotalsInstance();

	public MAXTransactionTotalsIfc getTransactionTotalsInstance(Locale locale);
	// MAX Changes for Rev 1.2 - Start
	public MAXTenderPaytmIfc getTenderPaytmInstance();
	// MAX Changes for Rev 1.2 - Ends
	// MAX Changes for Rev 1.3 - Start
	public MAXTenderMobikwikIfc getTenderMobikwikInstance();
	// MAX Changes for Rev 1.3 - Ends

	MAXTenderAmazonPayIfc getTenderAmazonPayInstance();
	
}