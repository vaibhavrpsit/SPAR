/* ===========================================================================
* Copyright (c) 1998, 2011, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/tender/coupon/GetCouponNumberSite.java /rgbustores_13.4x_generic_branch/2 2011/07/07 12:20:03 cgreene Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    cgreene   07/07/11 - convert entryMethod to an enum
 *    cgreene   05/26/10 - convert to oracle packaging
 *    abondala  01/03/10 - update header date
 *
 * ===========================================================================
 * $Log:
 *    3    360Commerce 1.2         3/31/2005 4:28:14 PM   Robert Pearse   
 *    2    360Commerce 1.1         3/10/2005 10:21:48 AM  Robert Pearse   
 *    1    360Commerce 1.0         2/11/2005 12:11:10 PM  Robert Pearse   
 *
 *   Revision 1.2  2004/07/30 18:49:13  epd
 *   @scr 6323 Now saves entry method in couponRDO. I don't know if this fixes poslog, but it's a first step
 *
 *   Revision 1.1  2004/04/02 20:17:27  epd
 *   @scr 4263 Refactored coupon tender into sub service
 *
 *   Revision 1.3  2004/02/12 16:48:22  mcs
 *   Forcing head revision
 *
 *   Revision 1.2  2004/02/11 21:22:51  rhafernik
 *   @scr 0 Log4J conversion and code cleanup
 *
 *   Revision 1.1.1.1  2004/02/11 01:04:12  cschellenger
 *   updating to pvcs 360store-current
 *
 *
 * 
 *    Rev 1.0   Nov 04 2003 11:17:44   epd
 * Initial revision.
 * 
 *    Rev 1.0   Oct 30 2003 13:01:08   crain
 * Initial revision.
 * ===========================================================================
 */
package oracle.retail.stores.pos.services.tender.coupon;

import oracle.retail.stores.domain.utility.EntryMethod;
import oracle.retail.stores.foundation.manager.ifc.UIManagerIfc;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.foundation.tour.ifc.LetterIfc;
import oracle.retail.stores.pos.ado.tender.TenderConstants;
import oracle.retail.stores.pos.services.PosSiteActionAdapter;
import oracle.retail.stores.pos.services.common.CommonLetterIfc;
import oracle.retail.stores.pos.services.tender.TenderCargo;
import oracle.retail.stores.pos.ui.POSUIManagerIfc;
import oracle.retail.stores.pos.ui.beans.POSBaseBeanModel;

/**
 * This class displays the screen to get the coupon number and then reads it in.
 * 
 * @version $Revision: /rgbustores_13.4x_generic_branch/2 $
 */
@SuppressWarnings("serial")
public class GetCouponNumberSite extends PosSiteActionAdapter
{
    /** revision number */
    public static final String revisionNumber = "$Revision: /rgbustores_13.4x_generic_branch/2 $";

    /**
     * Arrive method displays screen.
     * 
     * @param bus Service Bus
     */
    @Override
    public void arrive(BusIfc bus)
    {
        POSUIManagerIfc ui = (POSUIManagerIfc)bus.getManager(UIManagerIfc.TYPE);
        ui.showScreen(POSUIManagerIfc.COUPON_ENTRY, new POSBaseBeanModel());
    }

    /**
     * Depart method retrieves input.
     * 
     * @param bus Service Bus
     */
    @Override
    public void depart(BusIfc bus)
    {
        LetterIfc letter = bus.getCurrentLetter();

        // If the user entered a coupon number
        if (letter.getName().equals(CommonLetterIfc.NEXT))
        {
            TenderCargo cargo = (TenderCargo)bus.getCargo();
            POSUIManagerIfc ui = (POSUIManagerIfc)bus.getManager(UIManagerIfc.TYPE);
            // Get the coupon number
            cargo.getTenderAttributes().put(TenderConstants.COUPON_NUMBER, ui.getInput().trim());
            POSBaseBeanModel model = (POSBaseBeanModel)ui.getModel(POSUIManagerIfc.COUPON_ENTRY);
            if (model.getPromptAndResponseModel().isScanned())
            {
                cargo.getTenderAttributes().put(TenderConstants.ENTRY_METHOD, EntryMethod.Automatic);
            }
            else
            {
                cargo.getTenderAttributes().put(TenderConstants.ENTRY_METHOD, EntryMethod.Manual);
            }
        }
    }
}
