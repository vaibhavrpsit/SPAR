/********************************************************************************
 *   
 *	Copyright (c) 2016 - 2017 MAX Hypermarket, Inc    All Rights Reserved.
 *	
 *
 *	Rev 1.1		Dec 20, 2016			Mansi Goel			Changes for Gift Card FES
 *  Rev 1.0     Oct 22, 2016	        Ashish Yadav		Changes for Code Merging
 *
 ********************************************************************************/

package max.retail.stores.pos.services.tender.activation;

import oracle.retail.stores.domain.transaction.RetailTransactionIfc;
import oracle.retail.stores.domain.utility.EYSDomainIfc;
import oracle.retail.stores.domain.utility.GiftCardIfc;
import oracle.retail.stores.pos.services.tender.activation.ActivationCargo;

public class MAXActivationCargo extends ActivationCargo {
	private static final long serialVersionUID = -3465612180008381818L;
	// Changes starts for code merging(added below methods as they are not
	// present in base 14)
	protected int lineItemCounter = 0;
	protected RetailTransactionIfc retailTransaction = null;
	protected GiftCardIfc giftCard;
	protected EYSDomainIfc currentActivationGiftCard;
	protected boolean isReverseGiftCard = false;
	protected int reverseCount = 0;
	protected boolean isGiftCardApproved = false;

	public int getLineItemCounter() {
		return lineItemCounter;
	}

	public RetailTransactionIfc getRetailTransaction() {
		return retailTransaction;
	}

	public GiftCardIfc getGiftCard() {
		return giftCard;
	}

	public EYSDomainIfc getCurrentActivationGiftCard() {
		return currentActivationGiftCard;
	}

	public void setCurrentActivationGiftCard(EYSDomainIfc currentActivationGiftCard) {
		this.currentActivationGiftCard = currentActivationGiftCard;
	}

	public void setLineItemCounter(int i) {
		lineItemCounter = i;
	}

	public boolean isReverseGiftCard() {
		return isReverseGiftCard;
	}

	public int getReverseCount() {
		return reverseCount;
	}

	public void setReverseGiftCard(boolean value) {
		isReverseGiftCard = value;
	}

	public void setRetailTransaction(RetailTransactionIfc trans) {
		retailTransaction = trans;
	}

	public void setGiftCard(GiftCardIfc value) {
		giftCard = value;
	}
	// Changes ends for code merging

	//Changes for Rev 1.1 : Starts
	public boolean isGiftCardApproved() {
		return isGiftCardApproved;
	}

	public void setGiftCardApproved(boolean isGiftCardApproved) {
		this.isGiftCardApproved = isGiftCardApproved;
	}
	//Changes for Rev 1.1 : Ends	

}
