/** * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * ************************
 * Copyright (c) 2016-2017 Max Hypermarket.    All Rights Reserved. 
 *
 * Rev  	1.1  	21 Dec, 2016              Ashish Yadav              Credit Card FES
 * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * ***************************/
package max.retail.stores.domain.utility;

import oracle.retail.stores.domain.utility.CardTypeIfc;

public interface MAXCardTypeIfc extends CardTypeIfc{

	public abstract String identifyCardType(String paramString1, String paramString2);
}
