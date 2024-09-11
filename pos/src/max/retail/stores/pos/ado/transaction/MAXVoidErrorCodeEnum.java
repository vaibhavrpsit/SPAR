/* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
 * Copyright (c) 2015 Max India Pvt Ltd.    All Rights Reserved.
 *
 * 
 * Rev 1.0	Aug 17,2016  Ashish Yadav      Requirement: Transaction suspended in MPOS should not get altered upon retrieval in POS

 * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
package max.retail.stores.pos.ado.transaction;

import java.util.HashMap;

import oracle.retail.stores.pos.ado.transaction.VoidErrorCodeEnum;
import oracle.retail.stores.pos.ado.utility.TypesafeEnumIfc;

/**
 * Enumeration of post void error codes.
 * 
 */
public class MAXVoidErrorCodeEnum extends VoidErrorCodeEnum
{
    public MAXVoidErrorCodeEnum(String errorCode) {
		super(errorCode);
		// TODO Auto-generated constructor stub
	}

	// This id is used to tell
    // the compiler not to generate a
    // new serialVersionUID.
    //
    static final long serialVersionUID = -9161945420535353966L;

     //Change Start for Rev 1.0
    public static final MAXVoidErrorCodeEnum GIFT_CARD_NOT_ALLOWED = new MAXVoidErrorCodeEnum("GiftCardNotAllowed");
    // Chnages Ends for rev 1.0
    
    
}
