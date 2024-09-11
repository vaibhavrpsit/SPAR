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
import oracle.retail.stores.domain.utility.CardType;
import oracle.retail.stores.foundation.manager.device.EncipheredCardDataIfc;

public class MAXCardType extends CardType implements MAXCardTypeIfc {
	protected List cardList = new LinkedList();

	public String identifyCardType(String accountNumber, String cardType) {
		String cardName = "Unknown";

		Iterator iter = this.cardList.iterator();
		while (iter.hasNext()) {
			Card card = (Card) iter.next();

			if (!(card.getCardType().equals(cardType))) {
				continue;
			}

			if (((MAXCardIfc)card).evaluate(accountNumber)) {
				cardName = card.getCardName();
				break;
			}
		}

		return cardName;
	}

	@Override
	public void setPattern(String paramString) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public String getPattern() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String identifyCardType(EncipheredCardDataIfc paramEncipheredCardDataIfc, String paramString) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean isCardValid(String paramString) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public String getRevisionNumber() {
		// TODO Auto-generated method stub
		return null;
	}
}
