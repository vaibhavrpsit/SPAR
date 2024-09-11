/** * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * ************************
 * Copyright (c) 2016-2017 Max Hypermarket.    All Rights Reserved. 
 *
 * Rev  	1.1  	21 Dec, 2016              Ashish Yadav              Credit Card FES
 * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * ***************************/
package max.retail.stores.domain.utility;

import java.util.Iterator;

import oracle.retail.stores.domain.utility.RuleLength;

public class MAXRuleLength extends RuleLength implements MAXRuleIfc{

	public boolean evaluate(String cardNumber) {
		boolean lengthMatches = false;

		Iterator iter = this.lengthList.iterator();
		while (iter.hasNext()) {
			int length = ((Integer) iter.next()).intValue();
			if (cardNumber.length() == length) {
				lengthMatches = true;
				break;
			}
		}

		return lengthMatches;
	}
}
