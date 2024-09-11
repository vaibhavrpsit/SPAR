/** * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * ************************
 * Copyright (c) 2013 Max Hypermarket.    All Rights Reserved. 
 *   Rev 1.1  08/june/2015	Mohd Arif   : Change for CRM Integration functionality.
 * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * ***************************/

package max.retail.stores.domain.arts;

import max.retail.stores.domain.discount.MAXDiscountRuleConstantsIfc;
import max.retail.stores.domain.utility.MAXCodeConstantsIfc;
import oracle.retail.stores.domain.arts.JdbcReadCodeList;
import oracle.retail.stores.foundation.utility.Util;

public class MAXJdbcReadCodeListMap extends JdbcReadCodeList
		implements MAXDiscountRuleConstantsIfc, MAXCodeConstantsIfc {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public String getDiscountCodeListName(int scope, int method, int basis) { // begin
																				// getDiscountCodeListName()
		String listName = null;
		if (basis == ASSIGNMENT_CUSTOMER) {
			listName = CODE_LIST_PREFERRED_CUSTOMER_DISCOUNT;
		} else {
			switch (scope) {
			case DISCOUNT_SCOPE_TRANSACTION:
				switch (method) {
				case DISCOUNT_METHOD_PERCENTAGE:
					listName = CODE_LIST_TRANSACTION_DISCOUNT_BY_PERCENTAGE;
					break;
				case DISCOUNT_METHOD_AMOUNT:
					listName = CODE_LIST_TRANSACTION_DISCOUNT_BY_AMOUNT;
					break;
				// case DISCOUNT_SCOPE_DISCOUNT_CARD:
				// listName = CODE_LIST_DISCOUNT_CARD_DISCOUNT;
				// break;
				/* Rev 1.1 start */
				case DISCOUNT_SCOPE_CAPILLARY_COUPON:
					listName = CODE_LIST_CAPILLARY_COUPON_DISCOUNT;
					break;
				/* Rev 1.1 End */
				}
				break;
			case DISCOUNT_SCOPE_ITEM:
				switch (method) {
				case DISCOUNT_METHOD_PERCENTAGE:
					listName = CODE_LIST_ITEM_DISCOUNT_BY_PERCENTAGE;
					break;
				case DISCOUNT_METHOD_AMOUNT:
					listName = CODE_LIST_ITEM_DISCOUNT_BY_AMOUNT;
					break;
				}
				break;

			}
		}
		// list name not filled, handle it safely
		if (Util.isEmpty(listName)) {
			try {
				listName = DISCOUNT_SCOPE_DESCRIPTOR[scope].concat(" Discount ")
						.concat(DISCOUNT_METHOD_DESCRIPTOR[method]);
			} catch (ArrayIndexOutOfBoundsException e) {
				listName = "Discount scope ".concat(Integer.toString(scope)).concat(" method ")
						.concat(Integer.toString(method));
			}
		}
		return (listName);
	}

}
