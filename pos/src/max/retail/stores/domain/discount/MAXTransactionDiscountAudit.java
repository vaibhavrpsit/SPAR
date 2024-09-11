/********************************************************************************
 *   
 *	Copyright (c) 2016 - 2017 MAX Hypermarket, Inc    All Rights Reserved.
 *	
 *
 *
 *	Rev	1.0 	Mar 30, 2016		Mansi Goel		Changes to resolve bill buster amount is  
 *													added twice during postvoid & reprint 
 *
 ********************************************************************************/

package max.retail.stores.domain.discount;

import oracle.retail.stores.common.utility.LocalizedCodeIfc;
import oracle.retail.stores.domain.discount.ItemTransactionDiscountAuditIfc;
import oracle.retail.stores.domain.discount.TransactionDiscountAudit;
import oracle.retail.stores.foundation.utility.Util;

public class MAXTransactionDiscountAudit extends TransactionDiscountAudit {

	private static final long serialVersionUID = 831736216925966202L;

	public MAXTransactionDiscountAudit() {
		super();
	}

	@Override
	public boolean belongToTransactionDiscount(ItemTransactionDiscountAuditIfc itemDiscount) {
		String reasonCode = null, anotherReasonCode = null;
		boolean result = false;

		LocalizedCodeIfc reason = this.getReason();
		if (reason != null) {
			reasonCode = reason.getCode();
		}
		LocalizedCodeIfc anotherReason = itemDiscount.getReason();
		if (anotherReason != null) {
			anotherReasonCode = anotherReason.getCode();
		}
		//Changes for Rev 1.0 : Starts
		if ((this.getAccountingMethod() == itemDiscount.getAccountingMethod())
				&& (this.getAssignmentBasis() == itemDiscount.getAssignmentBasis())
				&& (this.getOriginalDiscountMethod() == itemDiscount.getOriginalDiscountMethod())
				&& Util.isObjectEqual(this.getDiscountEmployeeID(), itemDiscount.getDiscountEmployeeID())
				&& Util.isObjectEqual(this.getRuleID(), itemDiscount.getRuleID())
				&& Util.isObjectEqual(reasonCode, anotherReasonCode)
				&& Util.isObjectEqual(this.getDiscountRate(), itemDiscount.getDiscountRate())
				&& Util.isObjectEqual(this.getReferenceID(), itemDiscount.getReferenceID())
				&& (this.getReferenceIDCode() == itemDiscount.getReferenceIDCode())
				&& (this.getPromotionId() == itemDiscount.getPromotionId() || ((itemDiscount.getReason().getCode()
						.equalsIgnoreCase(String
								.valueOf(MAXDiscountRuleConstantsIfc.DISCOUNT_REASON_Buy$NatZPctoffTiered))) || (itemDiscount
						.getReason().getCode().equalsIgnoreCase(String
						.valueOf(MAXDiscountRuleConstantsIfc.DISCOUNT_REASON_Buy$NatZ$offTiered)))))) {
			//Changes for Rev 1.0 : Ends
			result = true;
		}

		return result;
	}
}
