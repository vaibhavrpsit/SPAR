package max.retail.stores.domain.utility;

import java.util.Iterator;

import oracle.retail.stores.domain.utility.RuleMask;

public class MAXRuleMask extends RuleMask implements MAXRuleIfc{

	private boolean shorterCardNumberMatches = false;
	public boolean evaluate(String cardNumber) {
		if (cardNumber == null) {
			return false;
		}

		boolean maskMatches = false;

		Iterator iter = this.maskList.iterator();

		while ((iter.hasNext()) && (!(maskMatches))) {
			String ruleMask = (String) iter.next();

			if ((!(this.shorterCardNumberMatches)) && (ruleMask.length() > cardNumber.length())) {
				continue;
			}

			boolean cardNumberEndReached = false;

			for (int i = 0; i < ruleMask.length(); ++i) {
				if (i >= cardNumber.length()) {
					cardNumberEndReached = true;
				}

				if (ruleMask.charAt(i) == wildCardCharacter) {
					continue;
				}

				if ((this.shorterCardNumberMatches) && (cardNumberEndReached)) {
					maskMatches = false;
					break;
				}

				if (ruleMask.charAt(i) != cardNumber.charAt(i)) {
					maskMatches = false;
					break;
				}

				if (i != ruleMask.length() - 1)
					continue;
				maskMatches = true;
			}

			if (maskMatches) {
				break;
			}

		}

		return maskMatches;
	}
}
