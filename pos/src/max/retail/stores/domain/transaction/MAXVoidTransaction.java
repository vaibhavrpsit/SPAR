/*************************************************************************************
 *   
 *	Copyright (c) 2016 - 2017 MAX Hypermarket, Inc    All Rights Reserved.
 *
 *	Rev 1.2		Jun 16, 2017			Jyoti Yadav			To get MAX instance of trx after cloning
 *	Rev 1.1		Apr 27, 2017			Mansi Goel			Changes to resolve tax is getting 
 *															deducted from gross amount in Void Txn
 *  Rev 1.0     Sep 08, 2016	        Nitesh Kumar		Code Merging 
 *
 ***********************************************************************************/

package max.retail.stores.domain.transaction;

import java.math.BigDecimal;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

import oracle.retail.stores.commerceservices.common.currency.CurrencyIfc;
import oracle.retail.stores.domain.DomainGateway;
import oracle.retail.stores.domain.discount.DiscountRuleIfc;
import oracle.retail.stores.domain.discount.TransactionDiscountStrategyIfc;
import oracle.retail.stores.domain.financial.FinancialTotalsIfc;
import oracle.retail.stores.domain.tax.TaxIfc;
import oracle.retail.stores.domain.tender.TenderAlternateCurrencyIfc;
import oracle.retail.stores.domain.tender.TenderChargeIfc;
import oracle.retail.stores.domain.tender.TenderCoupon;
import oracle.retail.stores.domain.tender.TenderDescriptorIfc;
import oracle.retail.stores.domain.tender.TenderGiftCardIfc;
import oracle.retail.stores.domain.tender.TenderGiftCertificateIfc;
import oracle.retail.stores.domain.tender.TenderLineItemIfc;
import oracle.retail.stores.domain.tender.TenderStoreCreditIfc;
import oracle.retail.stores.domain.tender.TenderTypeMapIfc;
import oracle.retail.stores.domain.transaction.RedeemTransactionIfc;
import oracle.retail.stores.domain.transaction.SaleReturnTransactionIfc;
import oracle.retail.stores.domain.transaction.TransactionIfc;
import oracle.retail.stores.domain.transaction.VoidTransaction;
import oracle.retail.stores.domain.utility.GiftCardIfc;
import oracle.retail.stores.domain.utility.StoreCreditIfc;

public class MAXVoidTransaction extends VoidTransaction {

	private static final long serialVersionUID = -5753538559110097588L;


	protected FinancialTotalsIfc getTenderFinancialTotals(FinancialTotalsIfc financialTotals) { // begin
																								// getTenderFinancialTotals()
		TenderLineItemIfc tli;
		// set up enumeration
		Enumeration<TenderLineItemIfc> enm = tenderLineItemsVector.elements();

		// if elements exist, loop through them
		while (enm.hasMoreElements()) { // begin loop through tender lines
			tli = (TenderLineItemIfc) enm.nextElement();

			// do not track mail bank check for counts!!!
			if (tli.getTypeCode() != TenderLineItemIfc.TENDER_TYPE_MAIL_BANK_CHECK) {
				financialTotals = financialTotals.add(getFinancialTotalsFromTender(tli));
			}
		} // end loop through tender lines

		// The customer was given change on the original transaction
		// Note: POSITIVE signum is > 0
		if (totals.getBalanceDue().signum() == CurrencyIfc.POSITIVE) {
			CurrencyIfc amtIn = totals.getBalanceDue();
			CurrencyIfc amtOut = DomainGateway.getBaseCurrencyInstance();
			TenderTypeMapIfc tenderTypeMap = DomainGateway.getFactory().getTenderTypeMapInstance();
			financialTotals.getTenderCount().addTenderItem(
					tenderTypeMap.getDescriptor(TenderLineItemIfc.TENDER_TYPE_CASH), 1, 0, amtIn, amtOut);
		}

		if (originalTransaction instanceof RedeemTransactionIfc) {
			RedeemTransactionIfc redeemTransaction = (RedeemTransactionIfc) originalTransaction;
			TenderLineItemIfc redeemTender = redeemTransaction.getRedeemTender();
			TenderTypeMapIfc tenderTypeMap = DomainGateway.getFactory().getTenderTypeMapInstance();
			CurrencyIfc amount = redeemTender.getAmountTender();
			String countryCode = amount.getCountryCode();
			CurrencyIfc zeroAmount = DomainGateway.getCurrencyInstance(countryCode);
			TenderDescriptorIfc descriptor = DomainGateway.getFactory().getTenderDescriptorInstance();
			descriptor.setTenderType(redeemTender.getTypeCode());
			descriptor.setCountryCode(countryCode);
			descriptor.setCurrencyID(amount.getType().getCurrencyId());
			financialTotals.getTenderCount().addTenderItem(descriptor, 0, 1, zeroAmount, amount,
					tenderTypeMap.getDescriptor(redeemTender.getTypeCode()), null, false);

			if (redeemTender instanceof TenderStoreCreditIfc) {
				TenderStoreCreditIfc tenderStoreCredit = (TenderStoreCreditIfc) redeemTender;
				StoreCreditIfc storeCredit = tenderStoreCredit.getStoreCredit();
				CurrencyIfc storeCreditVoidAmount = storeCredit.getAmount();
				if (tenderStoreCredit.getState().equals(TenderStoreCreditIfc.ISSUE)) {
					financialTotals.addAmountGrossStoreCreditsRedeemedVoided(storeCreditVoidAmount);
					financialTotals.addUnitsGrossStoreCreditsRedeemedVoided(new BigDecimal(1.0));
				} else if (tenderStoreCredit.getState().equals(TenderStoreCreditIfc.REDEEM)) {
					financialTotals.addAmountGrossStoreCreditsRedeemedVoided(storeCreditVoidAmount);
					financialTotals.addUnitsGrossStoreCreditsRedeemedVoided(new BigDecimal(1.0));
				}
				voidStoreCredit(redeemTender);
			} else if (redeemTender instanceof TenderGiftCardIfc) {
				TenderGiftCardIfc tenderGiftCard = (TenderGiftCardIfc) redeemTender;
				CurrencyIfc giftCardVoidAmount = tenderGiftCard.getAmountTender();
				GiftCardIfc giftCard = tenderGiftCard.getGiftCard();
				if (giftCard != null) {
					if (giftCard.getRequestType() == GiftCardIfc.GIFT_CARD_ISSUE) {
						financialTotals.addAmountGrossGiftCardItemIssueVoided(giftCardVoidAmount);
						financialTotals.addUnitsGrossGiftCardItemIssueVoided(new BigDecimal(1.0));
					} else if (giftCard.getRequestType() == GiftCardIfc.GIFT_CARD_RELOAD) {
						financialTotals.addAmountGrossGiftCardItemReloadVoided(giftCardVoidAmount);
						financialTotals.addUnitsGrossGiftCardItemReloadVoided(new BigDecimal(1.0));
					} else if (giftCard.getRequestType() == GiftCardIfc.GIFT_CARD_REDEEM) {
						financialTotals.addAmountGrossGiftCardItemRedeemedVoided(giftCardVoidAmount);
						financialTotals.addUnitsGrossGiftCardItemRedeemedVoided(new BigDecimal(1.0));
					}
				}
			} else if (redeemTender instanceof TenderGiftCertificateIfc) {
				TenderGiftCertificateIfc tenderGiftCertificate = (TenderGiftCertificateIfc) redeemTender;
				CurrencyIfc giftCertificateAmount = tenderGiftCertificate.getAmountTender();
				if (tenderGiftCertificate.getState().equals(TenderGiftCertificateIfc.REDEEMED)) {
					financialTotals.addAmountGrossGiftCertificatesRedeemedVoided(giftCertificateAmount);
					financialTotals.addUnitsGrossGiftCertificatesRedeemedVoided(new BigDecimal(1.0));
				} else {
					financialTotals.addAmountGrossGiftCertificateIssuedVoided(giftCertificateAmount);
					financialTotals.addUnitsGrossGiftCertificateIssuedVoided(new BigDecimal(1.0));
				}
			}
		}
		return financialTotals;
	}

	public FinancialTotalsIfc getFinancialTotalsFromTender(TenderLineItemIfc tli) { // begin
																					// getFinancialTotalsFromTender()
		FinancialTotalsIfc financialTotals = DomainGateway.getFactory().getFinancialTotalsInstance();

		CurrencyIfc amtIn = null;
		CurrencyIfc amtOut = null;
		TenderDescriptorIfc descriptor = DomainGateway.getFactory().getTenderDescriptorInstance();
		String desc = tli.getTypeDescriptorString();

		// add individual charge card totals to the financial total
		String sDesc = null;
		if (tli.getTypeCode() == TenderLineItemIfc.TENDER_TYPE_CHARGE) {
			desc = ((TenderChargeIfc) tli).getCardType();
			sDesc = tli.getTypeDescriptorString();
			descriptor.setTenderSubType(desc);
		}

		amtIn = DomainGateway.getBaseCurrencyInstance();
		amtOut = tli.getAmountTender();

		// check if alternate tender and override amounts, if necessary
		if (tli instanceof TenderAlternateCurrencyIfc) { // begin check
															// alternate
															// currency
			// cast to alternate currency ifc
			TenderAlternateCurrencyIfc alternate = (TenderAlternateCurrencyIfc) tli;
			CurrencyIfc alternateTender = alternate.getAlternateCurrencyTendered();
			// if no alternate currency, handle as base
			if (alternateTender != null) { // begin handle alternate tender
				// set description to include nationality
				desc = alternateTender.getDescription() + "_" + desc;
				// create zero-value clone
				CurrencyIfc zero = (CurrencyIfc) alternateTender.clone();
				zero.setZero();
				amtIn = zero;
				amtOut = alternateTender;
			} // end handle alternate tender
		} // end check alternate currency
		descriptor.setCountryCode(amtOut.getCountryCode());
		descriptor.setCurrencyID(amtOut.getType().getCurrencyId());
		descriptor.setTenderType(tli.getTypeCode());

		if (tli instanceof TenderCoupon) {
			descriptor.setTenderSubType(((TenderCoupon) tli).getCouponNumber());
		}

		financialTotals.getTenderCount().addTenderItem(descriptor, 0, 1, amtIn, amtOut.negate(), desc, sDesc,
				tli.getHasDenominations());

		if (tli.getTypeCode() == TenderLineItemIfc.TENDER_TYPE_STORE_CREDIT
				&& !(originalTransaction instanceof RedeemTransactionIfc)) {
			financialTotals.addAmountGrossStoreCreditsIssuedVoided(amtOut);
			financialTotals.addUnitsGrossStoreCreditsIssuedVoided(new BigDecimal(1));
		}
		return (financialTotals);
	}
	
	@Override
	protected void getSaleReturnFinancialTotals(FinancialTotalsIfc financialTotals) {
		// Back out the sales totals and taxes
		CurrencyIfc tax = totals.getTaxTotal();
		CurrencyIfc inclusiveTax = totals.getInclusiveTaxTotal();
		CurrencyIfc gross = totals.getSubtotal().subtract(totals.getDiscountTotal());
		
		//Changes for Rev 1.1 : Starts
		if (gross != null && inclusiveTax != null) {
			// For India L10N VAT, Taxable amount is defined as FinalSaleAmount
			// minus ItemInclusiveTaxAmount.
			gross = gross.subtract(inclusiveTax);
		}
		//Changes for Rev 1.1 : Ends

		// Back out shipping tax since shipping tax are tracked separately in
		// financial totals.
		tax = tax.subtract(financialTotals.getAmountTaxShippingCharges());
		inclusiveTax = inclusiveTax.subtract(financialTotals.getAmountInclusiveTaxShippingCharges());

		if ((tax != null) && (inclusiveTax != null)) {
			if (getTransactionTax().getTaxMode() == TaxIfc.TAX_MODE_EXEMPT) {
				if (originalTransactionType == TransactionIfc.TYPE_SALE
						|| originalTransactionType == TransactionIfc.TYPE_EXCHANGE) {
					financialTotals.addAmountGrossTaxExemptTransactionSalesVoided(gross.abs());
					financialTotals.addCountGrossTaxExemptTransactionSalesVoided(1);
				} else if (originalTransactionType == TransactionIfc.TYPE_RETURN) {
					financialTotals.addAmountGrossTaxExemptTransactionReturnsVoided(gross.abs());
					financialTotals.addCountGrossTaxExemptTransactionReturnsVoided(1);
				}
			}
			// Tax exempt is being handled as non-taxable for now
			if (isTaxableTransaction()) {
				if (originalTransactionType == TransactionIfc.TYPE_SALE
						|| originalTransactionType == TransactionIfc.TYPE_EXCHANGE) {
					financialTotals.addAmountGrossTaxableTransactionSalesVoided(gross.abs());
					financialTotals.addCountGrossTaxableTransactionSalesVoided(1);
					financialTotals.addAmountTaxTransactionSales(tax);
					financialTotals.addAmountInclusiveTaxTransactionSales(inclusiveTax);
				} else if (originalTransactionType == TransactionIfc.TYPE_RETURN) {
					financialTotals.addAmountGrossTaxableTransactionReturnsVoided(gross.abs());
					financialTotals.addCountGrossTaxableTransactionReturnsVoided(1);
					financialTotals.addAmountTaxTransactionReturns(tax.abs().negate());
					financialTotals.addAmountInclusiveTaxTransactionReturns(inclusiveTax.abs().negate());
				}
			} else {
				if (originalTransactionType == TransactionIfc.TYPE_SALE
						|| originalTransactionType == TransactionIfc.TYPE_EXCHANGE) {
					financialTotals.addAmountGrossNonTaxableTransactionSalesVoided(gross.abs());
					financialTotals.addCountGrossNonTaxableTransactionSalesVoided(1);
				} else if (originalTransactionType == TransactionIfc.TYPE_RETURN) {
					financialTotals.addAmountGrossNonTaxableTransactionReturnsVoided(gross.abs());
					financialTotals.addCountGrossNonTaxableTransactionReturnsVoided(1);
				}
			}
		}

		SaleReturnTransactionIfc srt = (SaleReturnTransactionIfc) originalTransaction;
		TransactionDiscountStrategyIfc[] discounts = srt.getTransactionDiscounts();
		if (discounts != null) {
			for (int x = 0; x < discounts.length; x++) {
				if (discounts[x].getAssignmentBasis() == DiscountRuleIfc.ASSIGNMENT_EMPLOYEE) {
					financialTotals.addUnitsGrossTransactionEmployeeDiscount(new BigDecimal(-1));
				} else {
					financialTotals.addNumberTransactionDiscounts(-1);
				}
			}
		}
	}
	protected Map<String, String> taxCode = new HashMap<String, String>();
	public Map<String, String> getTaxCode()
	{
		return taxCode;
	}
	public void setTaxCode(Map<String, String> taxCode)
	{
		this.taxCode = taxCode;
	}
	/*Change for Rev 1.2: Start*/
	public Object clone() {
		MAXVoidTransaction voidTrx = new MAXVoidTransaction();

		setCloneAttributes(voidTrx);

		return voidTrx;
	}
	protected void setCloneAttributes(MAXVoidTransaction voidTrx) {
		// clone superclass attributes
		super.setCloneAttributes(voidTrx);
		
		voidTrx.setTaxCode(taxCode);	
	}
	/*Change for Rev 1.2: End*/
}
