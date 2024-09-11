/* ===========================================================================
* Copyright (c) 1998, 2011, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/redeem/GiftCardRedeemReturnShuttle.java /rgbustores_13.4x_generic_branch/1 2011/05/05 14:06:09 mszekely Exp $
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
 * 4    360Commerce 1.3         4/25/2007 8:52:47 AM   Anda D. Cadar   I18N
 *      merge
 *      
 * 3    360Commerce 1.2         3/31/2005 4:28:17 PM   Robert Pearse   
 * 2    360Commerce 1.1         3/10/2005 10:21:55 AM  Robert Pearse   
 * 1    360Commerce 1.0         2/11/2005 12:11:14 PM  Robert Pearse   
 *
 *Revision 1.6  2004/09/23 00:07:16  kmcbride
 *@scr 7211: Adding static serialVersionUIDs to all POS Serializable objects, minus the JComponents
 *
 *Revision 1.5  2004/04/22 17:31:57  lzhao
 *@scr 3872: code review, remove toString()
 *
 *Revision 1.4  2004/04/13 19:02:22  lzhao
 *@scr 3872: gift card redeem.
 *
 *Revision 1.3  2004/04/08 20:33:03  cdb
 *@scr 4206 Cleaned up class headers for logs and revisions.
 *
 *Revision 1.2  2004/03/31 16:17:23  lzhao
 *@scr 3872: gift card redeem service update
 *
 *Revision 1.1  2004/03/25 23:01:23  lzhao
 *@scr #3872 Redeem Gift Card
 *
 *Revision: 4$
 *Mar 24, 2004 lzhao
 *
 * ===========================================================================
 */
package oracle.retail.stores.pos.services.redeem;


import oracle.retail.stores.commerceservices.common.currency.CurrencyIfc;
import oracle.retail.stores.pos.ado.tender.TenderConstants;
import oracle.retail.stores.pos.ado.tender.TenderTypeEnum;
import oracle.retail.stores.domain.utility.GiftCardIfc;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.foundation.tour.ifc.ShuttleIfc;

//--------------------------------------------------------------------------
/**
 This shuttle copies information from the cargo used
 in the gift card redeem service to the cargo used in the redeem service. <p>
 @version $Revision: /rgbustores_13.4x_generic_branch/1 $
 **/
//--------------------------------------------------------------------------
public class GiftCardRedeemReturnShuttle implements ShuttleIfc
{                                       
    // This id is used to tell
    // the compiler not to generate a
    // new serialVersionUID.
    //
    static final long serialVersionUID = 4774977743765101149L;

    /**
     revision number supplied by source-code-control system
     **/
    public static final String revisionNumber = "$Revision: /rgbustores_13.4x_generic_branch/1 $";
    /**
     * gift card data for redeem
     */
    protected GiftCardIfc giftCard = null;
    /**
     * amount for redeem
     */
    protected CurrencyIfc currentAmount = null;
    
    //----------------------------------------------------------------------
    /**
     Loads cargo from gift card redeem service. <P>
     <B>Pre-Condition(s)</B>
     <UL>
     <LI>Cargo will contain the retail transaction
     </UL>
     <B>Post-Condition(s)</B>
     <UL>
     <LI>
     </UL>
     @param  bus     Service Bus
     **/
    //----------------------------------------------------------------------
    public void load(BusIfc bus)
    {                                   // begin load()
        RedeemCargo cargo = (RedeemCargo) bus.getCargo();
        
        giftCard = cargo.getGiftCard();
        currentAmount = cargo.getCurrentAmount();
    }                                   // end load()

    //----------------------------------------------------------------------
    /**
     Loads cargo for redeem service. <P>
     <B>Pre-Condition(s)</B>
     <UL>
     <LI>Cargo will contain the retail transaction
     </UL>
     <B>Post-Condition(s)</B>
     <UL>
     <LI>
     </UL>
     @param  bus     Service Bus
     **/
    //----------------------------------------------------------------------
    public void unload(BusIfc bus)
    {                                   // begin unload()
        RedeemCargo cargo = (RedeemCargo) bus.getCargo();
        
        if ( giftCard != null )
        {    
            // prepare data for redeem service
            cargo.getTenderAttributes().put(TenderConstants.TENDER_TYPE, TenderTypeEnum.GIFT_CARD);
            cargo.getTenderAttributes().put(TenderConstants.NUMBER, giftCard.getCardNumber());
            if ( currentAmount != null )
            {    
                cargo.getTenderAttributes().put(TenderConstants.AMOUNT, currentAmount.getStringValue());
            }
            cargo.setGiftCard(giftCard);            
        }
    }                                   // end unload()
}
