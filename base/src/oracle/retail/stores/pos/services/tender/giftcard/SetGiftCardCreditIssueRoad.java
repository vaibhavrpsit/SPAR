/* ===========================================================================
* Copyright (c) 2007, 2011, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/tender/giftcard/SetGiftCardCreditIssueRoad.java /rgbustores_13.4x_generic_branch/1 2011/05/05 14:05:48 mszekely Exp $
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
 *  1    360Commerce 1.0         5/7/2008 12:06:36 PM   Jack G. Swan    Added
 *       to support putting returned deposits from Layaway and Order
 *       delete/cancel transactions onto new and existing giftcards.  This
 *       code was reviewed by Brett Larson.
 * $
 * ===========================================================================
 */
package oracle.retail.stores.pos.services.tender.giftcard;

import oracle.retail.stores.pos.services.tender.TenderCargo;
import oracle.retail.stores.domain.utility.GiftCardIfc;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.pos.services.PosLaneActionAdapter;


//--------------------------------------------------------------------------
/**
    This road sets the flag on the tender cargo that indicates this is a refund
    amount being added to the gift card.
    <p>
    @version $Revision: /rgbustores_13.4x_generic_branch/1 $
**/
//--------------------------------------------------------------------------
public class SetGiftCardCreditIssueRoad extends PosLaneActionAdapter
{
    /** Version ID */
    private static final long serialVersionUID = 1472185697935684712L;
    /**
       revision number
    **/
    public static final String revisionNumber = "$Revision: /rgbustores_13.4x_generic_branch/1 $";

    //----------------------------------------------------------------------
    /**
       This this road sets the gift card request type to GIFT_CARD_CREDIT_ISSUE.
       <P>
       @param  bus     Service Bus
    **/
    //----------------------------------------------------------------------
    public void traverse(BusIfc bus)
    {
        TenderCargo cargo = (TenderCargo)bus.getCargo();
        cargo.setGiftCardCreditIssue(true);
    }
}
