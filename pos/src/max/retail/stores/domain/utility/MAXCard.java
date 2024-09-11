/** * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * ************************
 * Copyright (c) 2016-2017 Max Hypermarket.    All Rights Reserved. 
 *
 * Rev  	1.1  	21 Dec, 2016              Ashish Yadav              Credit Card FES
 * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * ***************************/
package max.retail.stores.domain.utility;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import oracle.retail.stores.domain.utility.Card;
import oracle.retail.stores.domain.utility.RuleIfc;

public class MAXCard extends Card implements  MAXCardIfc{

	protected List ruleList = new LinkedList();
	public boolean evaluate(String cardNumber) {
		boolean passedAllRules = true;

		Iterator iter = this.ruleList.iterator();
		while (iter.hasNext()) {
			RuleIfc rule = (RuleIfc) iter.next();

			if (!(((MAXRuleIfc)rule).evaluate(cardNumber))) {
				passedAllRules = false;
				break;
			}
		}

		return passedAllRules;
	}
}
