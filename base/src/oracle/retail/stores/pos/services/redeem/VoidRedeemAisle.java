/* ===========================================================================
* Copyright (c) 1998, 2011, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/redeem/VoidRedeemAisle.java /rgbustores_13.4x_generic_branch/1 2011/05/05 14:06:10 mszekely Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    cgreene   05/26/10 - convert to oracle packaging
 *    abondala  01/03/10 - update header date
 *
 * ===========================================================================
 */
package oracle.retail.stores.pos.services.redeem;

import oracle.retail.stores.domain.utility.GiftCardIfc;
import oracle.retail.stores.foundation.tour.application.Letter;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.pos.services.PosLaneActionAdapter;

public class VoidRedeemAisle extends PosLaneActionAdapter {

	
	public void traverse(BusIfc bus) 
	{
		String letterName = "ContinueRedeem";
		RedeemCargo cargo = (RedeemCargo) bus.getCargo();
		if(cargo.getGiftCard().getRequestType() == GiftCardIfc.GIFT_CARD_REDEEM_VOID) 
		{
			letterName = "VoidRedeem";
		}
		bus.mail(new Letter(letterName), BusIfc.CURRENT);
	}

}
