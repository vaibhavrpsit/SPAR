 
/* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
   Copyright (c) 2012 - 2013 MAXHyperMarket, Inc.    All Rights Reserved.
  	Rev 1.0  15/Apr/2013	Jyoti Rawal, Initial Draft: Changes for Gift Card Functionality 
 * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */


package max.retail.stores.pos.services.giftcard;

import oracle.retail.stores.pos.services.giftcard.GiftCardConstantsIfc;

public interface MAXGiftCardConstantsIfc extends GiftCardConstantsIfc {
	
	public static final String ACTIVE_GIFT_CARD_NUMBER_ERROR = "ActiveGiftCardNumberError";
	
	public static final String DUPLICATE_GIFT_CARD_NUMBER_ERROR = "DuplicateGiftCardNumberError";
	
	public static final String CREATE_GIFTCARD_ALREADY_EXISTS = "GiftCardAlreadyExists";
	
	public static final String INVALID_CARD_CAST_NUM_LETTER = "InvalidCardCastNumber";
	
	 public static final String ButtonLabelKeysGC[] =  new String[]{
	        "First", "Second", "Third", "Fourth", "Fifth", "Sixth", "ManualEntry" };

}
