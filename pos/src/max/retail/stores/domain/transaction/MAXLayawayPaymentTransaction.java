/* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
 * Copyright (c) 2010 Lifestyle India Pvt Ltd.    All Rights Reserved.
 * Rev 1.5	7th Nov 2019	Vidhya Kommareddi
 * MPOS REQ:  MPOS Transaction Indentifier
 * Rev 1.4  Jul 10,2019	    Nitika Arora  E-Wallet Integration
 * Rev 1.3 	Aug 10, 2018	Jyoti Yadav	  Quoting PAN CR
 * Rev 1.2	  Aug 25,2017  Kritica Agarwal Change to capture GSTIN number	
 * Rev 1.1 GST Changes
 * 
 * Rev 1.0  April 14, 2011 05:00:30 PM Tarun.Gupta
 * Initial revision.
 *
 * Resolution for LMGI-562
 * Receipt Soft Copy document is not created for the Layaway Partial Payment 
 * 
 * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */

package max.retail.stores.domain.transaction;
import java.util.HashMap;
import java.util.Map;


// foundation imports
//import max.retail.stores.domain.businessassociate.MAXBusinessAssociateIfc;
import max.retail.stores.domain.customer.MAXCustomerType;
//import max.retail.stores.domain.ewallet.MAXEWalletAPIWebProperties;
//import max.retail.stores.domain.tender.MAXTenderEWalletIfc;
//import max.retail.stores.domain.tender.MAXTenderGiftCertificateIfc;
import max.retail.stores.domain.tender.MAXTenderLineItemIfc;
import oracle.retail.stores.commerceservices.common.currency.CurrencyIfc;
import oracle.retail.stores.common.utility.BigDecimalConstants;
import oracle.retail.stores.domain.DomainGateway;
import oracle.retail.stores.domain.financial.FinancialTotalsIfc;
import oracle.retail.stores.domain.lineitem.AbstractTransactionLineItemIfc;
import oracle.retail.stores.domain.tender.TenderAlternateCurrencyIfc;
import oracle.retail.stores.domain.tender.TenderChargeIfc;
import oracle.retail.stores.domain.tender.TenderDescriptorIfc;
import oracle.retail.stores.domain.tender.TenderGiftCardIfc;
import oracle.retail.stores.domain.tender.TenderGiftCertificateIfc;
import oracle.retail.stores.domain.tender.TenderLineItemIfc;
import oracle.retail.stores.domain.tender.TenderStoreCreditIfc;
import oracle.retail.stores.domain.tender.TenderTravelersCheckIfc;
import oracle.retail.stores.domain.tender.TenderTypeMapIfc;
import oracle.retail.stores.domain.transaction.LayawayPaymentTransaction;
import oracle.retail.stores.domain.transaction.TransactionConstantsIfc;
import oracle.retail.stores.domain.transaction.TransactionIfc;
import oracle.retail.stores.domain.utility.CountryCodeMap;

//----------------------------------------------------------------------------
/**
     This class describes a layaway payment transaction.  A layaway payment
     transaction reflects a payment on a layaway.  This is not used for the
     initial and final payments on a layaway (which reflect movement of stock).
     This is only used for the intermediate payments. <P>
     @see oracle.retail.stores.domain.financial.LayawayIfc
     @see oracle.retail.stores.domain.transaction.LayawayTransactionIfc
     @see oracle.retail.stores.domain.transaction.LayawayPaymentTransactionIfc
     @see oracle.retail.stores.domain.transaction.PaymentTransactionIfc
     @version $Revision: /main/13 $
 **/
//----------------------------------------------------------------------------
public class MAXLayawayPaymentTransaction extends LayawayPaymentTransaction implements MAXLayawayPaymentTransactionIfc
{                                       // begin class LayawayPaymentTransaction
	// This id is used to tell
	// the compiler not to generate a
	// new serialVersionUID.
	//
	static final long serialVersionUID = -7635593625345792158L;
	protected StringBuffer strBuffer = new StringBuffer();

	protected int count = 0;
	private String feedback = null;
	private String eReceiptConf;
	private String eReceiptTrantype;
	private boolean isEReceiptValueSelected = false;
	
	//changes for paytmqr
	protected String paytmQROrderId = null;

	//Added By Chiranjibi Routray For Business Associate Starts Here 
	//protected MAXBusinessAssociateIfc businessAssociate;
	//Added By Chiranjibi Routray For Business Associate Ends Here
	
	public HashMap<String,String> gcCustomer = new HashMap<String,String>();

	public HashMap<String, String> getGcCustomer() {
		return gcCustomer;
	}

	public void setGcCustomer(HashMap<String, String> gcCustomer) {
		this.gcCustomer = gcCustomer;
	}
	
	//Rev 1.5 start
	protected boolean isMposInitiated;
	//Rev 1.5 end

	//----------------------------------------------------------------------------
	/**
        Constructs LayawayPaymentTransaction object  <P>
	 **/
	//----------------------------------------------------------------------------
	public MAXLayawayPaymentTransaction()
	{                                   // begin LayawayPaymentTransaction()
	}                                   // end LayawayPaymentTransaction()

	//----------------------------------------------------------------------------
	/**
        Initilaizes LayawayPaymentTransaction object
        using the seed transaction  <P>
	 **/
	//----------------------------------------------------------------------------
	public void initialize(TransactionIfc transaction)
	{                                   // begin LayawayPaymentTransaction()
		transaction.setTransactionAttributes(this);
	}                                   // end LayawayPaymentTransaction()


	//----------------------------------------------------------------------------
	/**
        Creates clone of this object. <P>
        @return Object clone of this object
	 **/
	//----------------------------------------------------------------------------
	public Object clone()
	{                                   // begin clone()
		// instantiate new object
		MAXLayawayPaymentTransaction c = new MAXLayawayPaymentTransaction();

		// set values
		setCloneAttributes(c);

		// pass back Object
		return((Object) c);
	}                                   // end clone()

	//----------------------------------------------------------------------------
	/**
        Sets attributes in clone of this object. <P>
        @param newClass new instance of object
	 **/
	//----------------------------------------------------------------------------
	protected void setCloneAttributes(MAXLayawayPaymentTransaction newClass)
	{                                   // begin setCloneAttributes()
		// set attributes
		super.setCloneAttributes(newClass);

		//Added By Chiranjib for BA Starts Here
		/*if(businessAssociate !=null)
		{
			newClass.setBusinessAssociate(businessAssociate);
		}*/
		//Added By Akanksha for BA Ends Here
		if (gcCustomer != null) {
			newClass.setGcCustomer(gcCustomer);
		}
		/*if(apiWebProperties!=null)
			newClass.setApiWebProperties(apiWebProperties);*/
		
		if(isMposInitiated)
			newClass.setIsMposInitiated(isMposInitiated);
		if(eReceiptConf != null)
			newClass.seteReceiptConf(eReceiptConf);
		if(eReceiptTrantype != null)
			newClass.seteReceiptTrantype(eReceiptTrantype);
		if(isEReceiptValueSelected)
			newClass.setEReceiptValueSelected(isEReceiptValueSelected);

	} // end setCloneAttributes()

	//Added By Chiranjibi Routray For Business Associate Ends Here


	// ---------------------------------------------------------------------
	/**
	 * @return strBuffer
	 **/
	// ---------------------------------------------------------------------

	public StringBuffer getStringBuffer() {
		return strBuffer;
	}

	// ---------------------------------------------------------------------
	/**
	 * Sets the receipt data to the StringBuffer strBuffer
	 * <P>
	 **/
	// ---------------------------------------------------------------------
	public void setStringBuffer(StringBuffer sb) {
		this.strBuffer = sb;

	}

	public int getCount() {
		return count;
	}

	public void setCount(int count) {
		this.count = count;
	}

	//Added By Chiranjibi Routray For Business Associate Starts Here

	/*public MAXBusinessAssociateIfc getBusinessAssociate() {
		return businessAssociate;
	}

	public void setBusinessAssociate(MAXBusinessAssociateIfc businessAssociate) {
		this.businessAssociate = businessAssociate;
	}*/

	///Rev 1.1 Starts
	@Override
	public AbstractTransactionLineItemIfc[] getLineItem() {
		return this.lineItem;
	}

	@Override
	public void setLineItem(AbstractTransactionLineItemIfc[] lineItem) {
		this.lineItem = lineItem;
	}

	protected AbstractTransactionLineItemIfc[] lineItem;
	@Override
	public boolean isGstEnable() {
		return gstEnable;
	}
	@Override
	public void setGstEnable(boolean gstEnable) {
		this.gstEnable = gstEnable;
	}
	public boolean gstEnable = false;
	protected Map<String, String> taxCode = new HashMap<String, String>();
	public Map<String, String> getTaxCode()
	{
		return taxCode;
	}

	public void setTaxCode(Map<String, String> taxCode)
	{
		this.taxCode = taxCode;
	}
	//Rev 1.1 Ends

	//Change for Rev 1.2 : Starts
	public String externalOrderId;
	/* 
	 * @Override by kritica.agarwal
	 * @see max.retail.stores.domain.transaction.MAXLayawayPaymentTransactionIfc#getExternalOrderID()
	 */
	@Override
	public String getExternalOrderID() {
		// TODO Auto-generated method stub
		return externalOrderId;
	}

	/* 
	 * @Override by kritica.agarwal
	 * @see max.retail.stores.domain.transaction.MAXLayawayPaymentTransactionIfc#setExternalOrderID(java.lang.String)
	 */
	@Override
	public void setExternalOrderID(String externalOrder) {
		externalOrderId=externalOrder;

	}
	//Change for Rev 1.2 : Ends  

	//Changes for getting the Concept based on the register id starts
	private String concept;
	/**
	 * @return the concept
	 */
	public String getConcept() {
		return concept;
	}

	/**
	 * @param concept the concept to set
	 */
	public void setConcept(String concept) {
		this.concept = concept;
	}
	//Changes for getting the Concept based on the register id ends
	/* 
	 * @Override by Sarath.Sasikumar
	 * @see max.retail.stores.domain.transaction.MAXLayawayPaymentTransactionIfc#setCustomerFeedback(java.lang.String)
	 */
	@Override
	public void setCustomerFeedback(String feedback) {
		// TODO Auto-generated method stub
		this.feedback = feedback;
	}

	/* 
	 * @Override by Sarath.Sasikumar
	 * @see max.retail.stores.domain.transaction.MAXLayawayPaymentTransactionIfc#getCustomerFeedback()
	 */
	@Override
	public String getCustomerFeedback() {
		// TODO Auto-generated method stub
		return this.feedback;
	}

	/*Change for Rev 1.3: Start*/
	protected MAXCustomerType[] customerTypeDetails;
	public MAXCustomerType[] getCustomerTypeDetails() {
		return customerTypeDetails;
	}

	public void setCustomerTypeDetails(MAXCustomerType[] customerTypeDetails) {
		this.customerTypeDetails = customerTypeDetails;
	}
	/*Change for Rev 1.3: End*/
	
	//Change for Rev 1.4 Start
	/*private MAXEWalletAPIWebProperties apiWebProperties;	
	public MAXEWalletAPIWebProperties getApiWebProperties() {
		return apiWebProperties;
	}

	public void setApiWebProperties(MAXEWalletAPIWebProperties apiWebProperties) {
		this.apiWebProperties = apiWebProperties;
	}*/
	//Change for Rev 1.4 Ends

	//Rev 1.5 start
	/**
	 * @return the isMposInitiated
	 */
	public boolean getIsMposInitiated() {
		return isMposInitiated;
	}
	/**
	 * @param isMposInitiated the isMposInitiated to set
	 */
	public void setIsMposInitiated(boolean isMposInitiated) {
		this.isMposInitiated = isMposInitiated;
	}
	//Rev 1.5 end

	@Override
	public FinancialTotalsIfc getFinancialTotalsFromTender(TenderLineItemIfc tli)
	{
		CurrencyIfc amount = tli.getAmountTender();
		TenderDescriptorIfc descriptor = DomainGateway.getFactory().getTenderDescriptorInstance();
		String desc = tli.getTypeDescriptorString();

		// Get the appropriate amount based on the currency used.
		if (tli instanceof TenderAlternateCurrencyIfc)
		{
			TenderAlternateCurrencyIfc alternate = (TenderAlternateCurrencyIfc)tli;
			CurrencyIfc alternateAmount = alternate.getAlternateCurrencyTendered();

			// if tendered in alternate currency, use that amount.
			if (alternateAmount != null)
			{
				amount = alternate.getAlternateCurrencyTendered();
				// Prepend nationality to the description.
				String countryCode = amount.getCountryCode();
				String countryDescriptor = CountryCodeMap.getCountryDescriptor(countryCode);
				if (countryDescriptor == null)
				{
					countryDescriptor = countryCode;
				}
				desc = countryDescriptor + " " + desc;
			}
		}

		String currencyCode = amount.getCountryCode();
		descriptor.setCountryCode(currencyCode);
		descriptor.setCurrencyID(amount.getType().getCurrencyId());
		int tenderType = tli.getTypeCode();

		descriptor.setTenderType(tenderType);
		int numberItems = 1;

		// Traveler's checks have multiple checks for one line item.
		if (tenderType == TenderLineItemIfc.TENDER_TYPE_TRAVELERS_CHECK)
		{
			numberItems = ((TenderTravelersCheckIfc)tli).getNumberChecks();
		}

		String sDesc = null;

		// Add individual credit card totals to the financial totals
		if (tenderType == TenderLineItemIfc.TENDER_TYPE_CHARGE)
		{
			// Assuming that Credit cards are always handled in the local
			// currency,
			// so there is no conflict between this description and the
			// alternate above.
			desc = ((TenderChargeIfc)tli).getCardType();
			sDesc = tli.getTypeDescriptorString();
			descriptor.setTenderSubType(desc);
		}

		if (tenderType == TenderLineItemIfc.TENDER_TYPE_MALL_GIFT_CERTIFICATE)
		{
			String mallCertType = ((TenderGiftCertificateIfc)tli).getCertificateType();
			if (mallCertType != null && !mallCertType.equals(""))
			{
				TenderTypeMapIfc tenderTypeMap = DomainGateway.getFactory().getTenderTypeMapInstance();
				descriptor.setTenderType(tenderType);
				desc = tenderTypeMap.getDescriptor(tenderType);
				sDesc = tenderTypeMap.getDescriptor(tenderType);
			}/*else if (mallCertType != null && mallCertType.equals("")) {
				desc = ((MAXTenderGiftCertificateIfc) tli).getGiftCertificateCode();
				sDesc = tli.getTypeDescriptorString();
				descriptor.setTenderSubType(desc);
			}*/

		}
		
		/*if (tenderType == MAXTenderLineItemIfc.TENDER_TYPE_EWALLET) {
			// Assuming that Credit cards are always handled in the local
			// currency,
			// so there is no conflict between this description and the
			// alternate above.
			desc = ((MAXTenderEWalletIfc) tli).getWalletType();
			sDesc = tli.getTypeDescriptorString();
			descriptor.setTenderSubType(desc);
		}*/

		CurrencyIfc amtIn = null;
		CurrencyIfc amtOut = null;
		int cntIn = 0;
		int cntOut = 0;

		// Set amounts, count in/count out
		if (tli.getAmountTender().signum() == CurrencyIfc.POSITIVE)
		{
			amtIn = amount;
			amtOut = DomainGateway.getCurrencyInstance(currencyCode);
			cntIn = numberItems;
		}
		else
		{
			amtIn = DomainGateway.getCurrencyInstance(currencyCode);
			amtOut = amount.negate();
			cntOut = numberItems;
		}

		FinancialTotalsIfc financialTotals = DomainGateway.getFactory().getFinancialTotalsInstance();
		financialTotals.getTenderCount().addTenderItem(descriptor, cntIn, cntOut, amtIn, amtOut, desc, sDesc,
				tli.getHasDenominations());

		if (tli instanceof TenderStoreCreditIfc)
		{
			//Add Store Credit Issued ONLY.
			if (tli.getAmountTender().signum() == CurrencyIfc.NEGATIVE)
			{
				financialTotals.addAmountGrossStoreCreditsIssued(amtOut);
				financialTotals.addUnitsGrossStoreCreditsIssued(BigDecimalConstants.ONE_AMOUNT);
			}
			//Else it is an use of StoreCredit as tender; should not considered as Redeem (Cash Redeem).
		}
		else if (tli instanceof TenderGiftCardIfc)
		{
			//if transaction is a refund, add the gift card amount to the item credit bucket
			if(getTransactionType() == TransactionConstantsIfc.TYPE_RETURN ||
					getTransactionType() == TransactionConstantsIfc.TYPE_LAYAWAY_DELETE ||
					getTransactionType() == TransactionConstantsIfc.TYPE_ORDER_CANCEL)
			{
				financialTotals.addAmountGrossGiftCardItemCredit(tli.getAmountTender().abs());
				financialTotals.addUnitsGrossGiftCardItemCredit(BigDecimalConstants.ONE_AMOUNT);
			}

			// If the transaction is a sale and the tender amount is less than 0
			if (getTransactionType() == TransactionConstantsIfc.TYPE_SALE &&
					tli.getAmountTender().signum() < 0)
			{
				financialTotals.addAmountGrossGiftCardItemCredit(tli.getAmountTender().abs());
				financialTotals.addUnitsGrossGiftCardItemCredit(BigDecimalConstants.ONE_AMOUNT);
			}
		}

		return (financialTotals);

	}

	//Chanegs starts for Rev 1.1 (Ashish:  EReceipt)

		
		public String geteReceiptConf() {
			return eReceiptConf;
		}
		public void seteReceiptConf(String eReceiptConf) {
			this.eReceiptConf = eReceiptConf;
		}
		public String geteReceiptTrantype() {
			return eReceiptTrantype;
		}
		public void seteReceiptTrantype(String eReceiptTrantype) {
			this.eReceiptTrantype = eReceiptTrantype;
		}
		public boolean isEReceiptValueSelected() {
			return isEReceiptValueSelected;
		}
		public void setEReceiptValueSelected(boolean isEReceiptValueSelected) {
			this.isEReceiptValueSelected = isEReceiptValueSelected;
		}

		@Override
		public boolean isDuplicateReceipt() {
			// TODO Auto-generated method stub
			return false;
		}

		@Override
		public void setDuplicateReceipt(boolean duplicateReceipt) {
			// TODO Auto-generated method stub
			
		}

		
		
		//Chanegs ends for Rev 1.1 (Ashish:  EReceipt)
		
		//changes for paytmqr
		public String getPaytmQROrderId() {
			return paytmQROrderId;
		}

		public void setPaytmQROrderId(String paytmQROrderId) {
			this.paytmQROrderId = paytmQROrderId;
		}
		
}                                       // end class LayawayPaymentTransaction
