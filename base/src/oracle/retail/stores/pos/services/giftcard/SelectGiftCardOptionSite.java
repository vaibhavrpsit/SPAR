/* ===========================================================================
* Copyright (c) 1998, 2013, Oracle and/or its affiliates. All rights reserved.
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/giftcard/SelectGiftCardOptionSite.java /main/11 2013/05/22 10:41:02 asinton Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    asinton   05/21/13 - added GIFT_CARD_OPTIONS_SCREEN to be used when
 *                         appropriate instead of the POPUPMENU for same
 *    cgreene   05/26/10 - convert to oracle packaging
 *    abondala  01/03/10 - update header date
 *
 * ===========================================================================
 * $Log:
 *    3    360Commerce 1.2         3/31/2005 4:29:54 PM   Robert Pearse   
 *    2    360Commerce 1.1         3/10/2005 10:25:08 AM  Robert Pearse   
 *    1    360Commerce 1.0         2/11/2005 12:14:08 PM  Robert Pearse   
 *
 *   Revision 1.3  2004/02/12 16:50:20  mcs
 *   Forcing head revision
 *
 *   Revision 1.2  2004/02/11 21:51:11  rhafernik
 *   @scr 0 Log4J conversion and code cleanup
 *
 *   Revision 1.1.1.1  2004/02/11 01:04:16  cschellenger
 *   updating to pvcs 360store-current
 *
 *
 * 
 *    Rev 1.1   Nov 26 2003 09:16:32   lzhao
 * remove unused code.
 * Resolution for 3371: Feature Enhancement:  Gift Card Enhancement
 * 
 *    Rev 1.0   Nov 21 2003 14:46:12   lzhao
 * Initial revision.
 * 
 * ===========================================================================
 */
package oracle.retail.stores.pos.services.giftcard;

// foundation imports
import oracle.retail.stores.foundation.manager.ifc.UIManagerIfc;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.pos.services.PosSiteActionAdapter;
import oracle.retail.stores.pos.ui.POSUIManagerIfc;

/**
 *  This site displays the Gift Card options
 *  @version $Revision: /main/11 $
 */
@SuppressWarnings("serial")
public class SelectGiftCardOptionSite extends PosSiteActionAdapter
{
    /*
     * (non-Javadoc)
     * @see oracle.retail.stores.foundation.tour.application.SiteActionAdapter#arrive(oracle.retail.stores.foundation.tour.ifc.BusIfc)
     */
    @Override
    public void arrive(BusIfc bus)
    {
        GiftCardCargo cargo = (GiftCardCargo)bus.getCargo();
        POSUIManagerIfc ui = (POSUIManagerIfc) bus.getManager(UIManagerIfc.TYPE);
        if(cargo.isGiftCardOptionScreen())
        {
            ui.showScreen(POSUIManagerIfc.GIFT_CARD_OPTIONS_SCREEN);
        }
        else
        {
            ui.showScreen(POSUIManagerIfc.GIFT_CARD_OPTIONS);
        }
    }

}
