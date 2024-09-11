/* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
   Copyright (c) 2012 - 2013 MAXHyperMarket, Inc.    All Rights Reserved.
       Rev 1.1  08/08/2013     Jyoti Rawal, Changed the Gift Card Tender flow
  	Rev 1.0  15/Apr/2013	Jyoti Rawal, Initial Draft: Changes for Gift Card Functionality 
 * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */

package max.retail.stores.domain.utility;

import oracle.retail.stores.commerceservices.common.currency.CurrencyIfc;
import oracle.retail.stores.domain.utility.EYSDate;
import oracle.retail.stores.domain.utility.GiftCard;

//--------------------------------------------------------------------------
/**
 * This class is a utility for gift card operations, extra variables used when
 * implementing integration of Gift Card Third Party API.
 * <p>
 * 
 * @version $Revision: 1.0$
 **/
// --------------------------------------------------------------------------

public class MAXGiftCard extends GiftCard implements MAXGiftCardIfc {

	/**
	 * 
	 */
	private static final long serialVersionUID = 703899713373014105L;

	public MAXGiftCard() {

	}

	public Object clone() {

		MAXGiftCard c = new MAXGiftCard();
		setCloneAttributes(c);
		c.setExpirationDate(this.getExpirationDate());
		return ((Object) c);
	}

	protected String qcApprovalCode = null;
	protected String qcInvoiceNumber = null;
	protected String qcTransactionId = null;
	protected String qcBatchNumber = null;
	protected String qcCardType = null;
	protected boolean isSwiped = false;
	protected boolean isScanned = false;
	protected String trackData = null;
	protected EYSDate expiry = null;
	protected String cardPin = null;

	public String getTrackData() {
		return trackData;
	}

	public void setTrackData(String trackData) {
		this.trackData = trackData;
	}

	public boolean isSwiped() {
		return isSwiped;
	}

	public void setSwiped(boolean isSwiped) {
		this.isSwiped = isSwiped;
	}

	public String getQcApprovalCode() {
		return qcApprovalCode;
	}

	public void setQcApprovalCode(String qcApprovalCode) {
		this.qcApprovalCode = qcApprovalCode;
	}

	public String getQcInvoiceNumber() {
		return qcInvoiceNumber;
	}

	public void setQcInvoiceNumber(String qcInvoiceNumber) {
		this.qcInvoiceNumber = qcInvoiceNumber;
	}

	public String getQcTransactionId() {
		return qcTransactionId;
	}

	public void setQcTransactionId(String qcTransactionId) {
		this.qcTransactionId = qcTransactionId;
	}

	public String getQcBatchNumber() {
		return qcBatchNumber;
	}

	public void setQcBatchNumber(String qcBatchNumber) {
		this.qcBatchNumber = qcBatchNumber;
	}

	public String getQcCardType() {
		return qcCardType;
	}

	public void setQcCardType(String qcCardType) {
		this.qcCardType = qcCardType;
	}

	public boolean isScanned() {
		return isScanned;
	}

	public void setScanned(boolean isScanned) {
		this.isScanned = isScanned;

	}

	public EYSDate getExpirationDate() {
		return (expirationDate);
	}

	public void setExpirationDate(EYSDate value) {
		expirationDate = value;
	}
	// Rev 1.1 changes start

	public void setCloneAttributes(MAXGiftCard newClass) { // begin
															// setCloneAttributes()
		newClass.setCardNumber(getCardNumber());
		newClass.setStatus(getStatus());
		if (dateSold != null) {
			newClass.setDateSold((EYSDate) getDateSold().clone());
		}
		if (dateActivated != null) {
			newClass.setDateActivated((EYSDate) getDateActivated().clone());
		}
		if (initialBalance != null) {
			newClass.setInitialBalance((CurrencyIfc) getInitialBalance().clone());
		}
		if (currentBalance != null) {
			newClass.setCurrentBalance((CurrencyIfc) getCurrentBalance().clone());
		}
		if (balanceForInquiryFailure != null) {
			newClass.setBalanceForInquiryFailure((CurrencyIfc) getBalanceForInquiryFailure().clone());
		}
		if (trackData != null) {
			newClass.setTrackData(trackData);
		}
		if (qcApprovalCode != null) {
			newClass.setQcApprovalCode(qcApprovalCode);
		}
		if (qcInvoiceNumber != null) {
			newClass.setQcInvoiceNumber(qcInvoiceNumber);
		}
		if (qcTransactionId != null) {
			newClass.setQcTransactionId(qcTransactionId);
		}
		if (qcBatchNumber != null) {
			newClass.setQcBatchNumber(qcBatchNumber);
		}
		if (qcCardType != null) {
			newClass.setQcCardType(qcCardType);
		}
		if (expirationDate != null) {
			newClass.setExpirationDate(expirationDate);
		}
		newClass.setSwiped(isSwiped);
		newClass.setScanned(isScanned);
		newClass.setRequestType(requestType);
		newClass.setOpenAmount(openAmount);
		newClass.setEntryMethod(entryMethod);
		newClass.setIssueEntryType(issueEntryType);
		newClass.setApprovalCode(approvalCode);
		newClass.setInquireAmountForTender(inquireAmountForTender);
		if (settlementData != null) {
			newClass.setSettlementData(new String(settlementData));
		}
		if (authorizedDateTime != null) {
			newClass.setAuthorizedDateTime((EYSDate) authorizedDateTime.clone());
		}
	}
	@Override
	public String getCardPin() {
		return this.cardPin;
	}

	@Override
	public void setCardPin(String cardPin) {
		this.cardPin = cardPin;
	}
	// Rev 1.1 changes end
}
