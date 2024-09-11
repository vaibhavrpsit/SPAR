/* ===========================================================================
* Copyright (c) 2004, 2011, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/postvoid/CheckForGiftCardRedeemAisle.java /rgbustores_13.4x_generic_branch/1 2011/05/05 14:05:50 mszekely Exp $
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
 *    3    360Commerce 1.2         3/31/2005 4:27:24 PM   Robert Pearse   
 *    2    360Commerce 1.1         3/10/2005 10:20:08 AM  Robert Pearse   
 *    1    360Commerce 1.0         2/11/2005 12:09:55 PM  Robert Pearse   
 *
 *   Revision 1.1  2004/05/11 16:05:29  blj
 *   @scr 4603 - fixed for post void of giftcard issue/reload/redeem/credit
 *
 * ===========================================================================
 */
package oracle.retail.stores.pos.services.postvoid;

import oracle.retail.stores.pos.ado.ADO;
import oracle.retail.stores.pos.ado.transaction.RetailTransactionADOIfc;
import oracle.retail.stores.pos.ado.transaction.TransactionPrototypeEnum;
import oracle.retail.stores.pos.services.common.CommonLetterIfc;
import oracle.retail.stores.domain.tender.TenderLineItemIfc;
import oracle.retail.stores.domain.transaction.RedeemTransactionIfc;
import oracle.retail.stores.domain.transaction.TenderableTransactionIfc;
import oracle.retail.stores.foundation.tour.application.Letter;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.pos.services.PosLaneActionAdapter;

/**
 * 
 */
public class CheckForGiftCardRedeemAisle extends PosLaneActionAdapter
{
    //----------------------------------------------------------------------
    /**
     TODO: May not be able to use this because the amount
     is zero because the redeem has taken all of the value
     off of the giftcard.
     @param  bus     Service Bus
     **/
    //----------------------------------------------------------------------
    public void traverse(BusIfc bus)
    {
        // check if redeem transaction and if redeemTender is a giftCard
        VoidCargo cargo = (VoidCargo)bus.getCargo();
        Letter letter = new Letter(CommonLetterIfc.CONTINUE);
        RetailTransactionADOIfc trans = cargo.getOriginalTransactionADO();
        
        if (trans.getTransactionType() == TransactionPrototypeEnum.REDEEM)
        {
            // if we get here we know that the original transaction is a redeem which is not a retail transaction but
            // it is a tenderable transaction.
            TenderableTransactionIfc rTransRDO = (TenderableTransactionIfc)((ADO)cargo.getOriginalTransactionADO()).toLegacy();
            //if (((RedeemTransactionADO)trans).getTenderType() == TenderTypeEnum.GIFT_CARD)
            if (((RedeemTransactionIfc)rTransRDO).getRedeemTender().getTypeCode() == TenderLineItemIfc.TENDER_TYPE_GIFT_CARD)
            {
                letter = new Letter("ActivateGCardRedeem");
            }   
        }
        // If yes, mail "ActivateGCardRedeem" letter
        // if no, mail "Continue" letter
        bus.mail(letter, BusIfc.CURRENT);
    }
}
