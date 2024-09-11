/* ===========================================================================
* Copyright (c) 1998, 2011, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/redeem/giftcard/EnterGiftCardNumberSite.java /rgbustores_13.4x_generic_branch/2 2011/06/07 16:44:02 cgreene Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    cgreene   06/07/11 - update to first pass of removing pospal project
 *    blarsen   12/08/10 - cpoi msr not being activated. added call to
 *                         pda.showMSREntryPrompt() which actually does the
 *                         activation.
 *    cgreene   05/26/10 - convert to oracle packaging
 *    abondala  01/03/10 - update header date
 *
 * ===========================================================================
 * $Log:
 * 3    360Commerce 1.2         3/31/2005 4:28:01 PM   Robert Pearse   
 * 2    360Commerce 1.1         3/10/2005 10:21:25 AM  Robert Pearse   
 * 1    360Commerce 1.0         2/11/2005 12:10:54 PM  Robert Pearse   
 *
 * Revision 1.5  2004/04/22 17:33:20  lzhao
 * @scr 3872: code review. remove unused code.
 *
 * Revision 1.4  2004/04/08 20:33:02  cdb
 * @scr 4206 Cleaned up class headers for logs and revisions.
 *
 * Revision 1.3  2004/04/07 21:10:09  lzhao
 * @scr 3872: gift card redeem and revise gift card activation
 *
 * Revision 1.2  2004/03/25 23:01:23  lzhao
 * @scr #3872 Redeem Gift Card
 *
 * Revision 1.1  2004/03/22 23:59:08  lzhao
 * @scr 3872 - add gift card redeem (initial)
 *
 * Revision: 3$
 *
 * ===========================================================================
 */
package oracle.retail.stores.pos.services.redeem.giftcard;

import oracle.retail.stores.domain.manager.payment.AuthorizationConstantsIfc;
import oracle.retail.stores.foundation.manager.ifc.UIManagerIfc;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.pos.services.PosSiteActionAdapter;
import oracle.retail.stores.pos.ui.POSUIManagerIfc;
import oracle.retail.stores.pos.ui.beans.POSBaseBeanModel;

/**
 * Provide a screen for entering gift card number for redeem
 * 
 * @version $Revision: /rgbustores_13.4x_generic_branch/2 $
 **/
public class EnterGiftCardNumberSite extends PosSiteActionAdapter implements AuthorizationConstantsIfc
{
    private static final long serialVersionUID = -7190620792573325531L;

    /**
     * revision number
     */
    public static final String revisionNumber = "$Revision: /rgbustores_13.4x_generic_branch/2 $";

    /**
     * Displays entering gift card number screen.
     * 
     * @param bus Service Bus
     */
    public void arrive(BusIfc bus)
    {
        POSUIManagerIfc ui = (POSUIManagerIfc)bus.getManager(UIManagerIfc.TYPE);
        ui.showScreen(POSUIManagerIfc.GIFT_CARD_REDEEM_INQUIRY, new POSBaseBeanModel());
    }
}
