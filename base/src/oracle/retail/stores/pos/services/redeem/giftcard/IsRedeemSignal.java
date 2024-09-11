/* ===========================================================================
* Copyright (c) 1998, 2011, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/redeem/giftcard/IsRedeemSignal.java /rgbustores_13.4x_generic_branch/1 2011/05/05 14:06:10 mszekely Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    cgreene   05/26/10 - convert to oracle packaging
 *    abondala  01/03/10 - update header date
 *
 * ===========================================================================
 * $Log:
 * 3    360Commerce 1.2         3/31/2005 4:28:28 PM   Robert Pearse   
 * 2    360Commerce 1.1         3/10/2005 10:22:20 AM  Robert Pearse   
 * 1    360Commerce 1.0         2/11/2005 12:11:34 PM  Robert Pearse   
 *
 *Revision 1.6  2004/09/23 00:07:17  kmcbride
 *@scr 7211: Adding static serialVersionUIDs to all POS Serializable objects, minus the JComponents
 *
 *Revision 1.5  2004/04/22 17:33:20  lzhao
 *@scr 3872: code review. remove unused code.
 *
 *Revision 1.4  2004/04/14 20:10:07  lzhao
 *@scr  3872 Redeem, change gift card request type from String to in.
 *
 *Revision 1.3  2004/04/13 19:02:22  lzhao
 *@scr 3872: gift card redeem.
 *
 *Revision 1.2  2004/04/08 20:33:02  cdb
 *@scr 4206 Cleaned up class headers for logs and revisions.
 *
 *Revision 1.1  2004/04/07 21:10:09  lzhao
 *@scr 3872: gift card redeem and revise gift card activation
 *
 *Revision: 3$
 *Apr 2, 2004 lzhao
 *
 * ===========================================================================
 */
package oracle.retail.stores.pos.services.redeem.giftcard;

import oracle.retail.stores.pos.services.redeem.RedeemCargo;
import oracle.retail.stores.domain.utility.GiftCardIfc;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.foundation.tour.ifc.TrafficLightIfc;

//--------------------------------------------------------------------------
/**
 Clears if it is redeem gift card.
 @version $Revision: /rgbustores_13.4x_generic_branch/1 $
**/
//--------------------------------------------------------------------------
public class IsRedeemSignal implements TrafficLightIfc 
{
    // This id is used to tell
    // the compiler not to generate a
    // new serialVersionUID.
    //
    static final long serialVersionUID = -6494778546793035171L;

    /**
     * version number
     */
    public static final String revisionNumber = "$Revision: /rgbustores_13.4x_generic_branch/1 $";
    /**
     signal name.
     **/
    public static final String SIGNALNAME = "IsRedeemSignal";

    //--------------------------------------------------------------------------
    /**
     roadClear determines whether it is gift card redeem after doing
     gift card inquiry.
     @param bus Service bus
     @return true if it is redeem, otherwise return false
     **/
    //--------------------------------------------------------------------------
    public boolean roadClear(BusIfc bus)
    {

        RedeemCargo cargo = (RedeemCargo) bus.getCargo();

        GiftCardIfc giftCard = cargo.getGiftCard();
        if ( giftCard != null &&
             giftCard.getRequestType() == GiftCardIfc.GIFT_CARD_REDEEM)
        {
            return true;
        }
        else
        {    
            return false;
        }
    }
}
