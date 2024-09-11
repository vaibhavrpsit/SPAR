/** * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * ************************
 * Copyright (c) 2016-2017 Max Hypermarket.    All Rights Reserved. 
 *
 * Rev  	1.1  	21 Dec, 2016              Ashish Yadav              Credit Card FES
 * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * ***************************/
package max.retail.stores.domain.utility;

import java.util.Iterator;

import oracle.retail.stores.domain.utility.RuleBinRange;

public class MAXRuleBinRange extends  RuleBinRange implements MAXRuleIfc{
	
	public boolean evaluate(String cardNumber) {
		if (cardNumber.length() < 1) {
			return false;
		}

		boolean cardInRange = false;

		Iterator iter = this.binRangeList.iterator();
		while (iter.hasNext()) {
			BinRange binRange = (BinRange) iter.next();

			String minStr = String.valueOf(binRange.getMinValue());
			int minLen = minStr.length();
			String maxStr = String.valueOf(binRange.getMaxValue());
			int maxLen = maxStr.length();
			int len = Math.max(minLen, maxLen);

			if (len > cardNumber.length()) {
				continue;
			}

			String cardNumberSubstring = cardNumber.substring(0, len);
			long cardBin = new Long(cardNumberSubstring).longValue();

			if ((cardBin >= binRange.getMinValue()) && (cardBin <= binRange.getMaxValue())) {
				cardInRange = true;
				break;
			}

		}

		return cardInRange;
	}

}
