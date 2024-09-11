/* ===========================================================================
* Copyright (c) 1998, 2011, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/tender/travelcheck/TravelCheckUISite.java /rgbustores_13.4x_generic_branch/1 2011/05/05 14:05:48 mszekely Exp $
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
 *    3    360Commerce 1.2         3/31/2005 4:30:37 PM   Robert Pearse   
 *    2    360Commerce 1.1         3/10/2005 10:26:28 AM  Robert Pearse   
 *    1    360Commerce 1.0         2/11/2005 12:15:19 PM  Robert Pearse   
 *
 *   Revision 1.1  2004/04/13 22:10:26  bwf
 *   @scr 4263 Decomposition of travel check.
 *
 *   Revision 1.2  2004/02/12 16:48:22  mcs
 *   Forcing head revision
 *
 *   Revision 1.1.1.1  2004/02/11 01:04:12  cschellenger
 *   updating to pvcs 360store-current
 *
 *
 * 
 *    Rev 1.1   Nov 20 2003 15:27:40   cdb
 * Cleared TenderADO so that following sites would re-generate it based on quantity entered. Previously, since TenderADO existed in cargo after an invalid quantity (0), the new quantity was never used and resulted in a repetetive error.
 * Resolution for 3458: QTY of Travelers' Check is never cleared after error message
 * 
 *    Rev 1.0   Nov 04 2003 11:17:58   epd
 * Initial revision.
 * 
 *    Rev 1.1   Oct 24 2003 10:12:06   epd
 * removed dead code
 * 
 *    Rev 1.0   Oct 23 2003 17:29:56   epd
 * Initial revision.
 * 
 *    Rev 1.0   Oct 17 2003 13:06:52   epd
 * Initial revision.
 *   
 * ===========================================================================
 */
package oracle.retail.stores.pos.services.tender.travelcheck;

import oracle.retail.stores.pos.ado.tender.TenderConstants;
import oracle.retail.stores.pos.services.tender.TenderCargo;
import oracle.retail.stores.foundation.manager.ifc.UIManagerIfc;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.foundation.tour.ifc.LetterIfc;
import oracle.retail.stores.pos.services.PosSiteActionAdapter;
import oracle.retail.stores.pos.ui.POSUIManagerIfc;
import oracle.retail.stores.pos.ui.beans.POSBaseBeanModel;

/**
 *  
 */
public class TravelCheckUISite extends PosSiteActionAdapter
{
    /* (non-Javadoc)
     * @see oracle.retail.stores.foundation.tour.application.SiteActionAdapter#arrive(oracle.retail.stores.foundation.tour.ifc.BusIfc)
     */
    public void arrive(BusIfc bus)
    {
        // if we got here, we can put up UI asking the user to enter the number of
        // travellers checks they are using.
        POSUIManagerIfc ui = (POSUIManagerIfc)bus.getManager(UIManagerIfc.TYPE);
        ui.showScreen(POSUIManagerIfc.TRAVELERS_CHECK, new POSBaseBeanModel());
    }
    
    /* (non-Javadoc)
     * @see oracle.retail.stores.foundation.tour.application.SiteActionAdapter#depart(oracle.retail.stores.foundation.tour.ifc.BusIfc)
     */
    public void depart(BusIfc bus)
    {
        LetterIfc ltr = (LetterIfc) bus.getCurrentLetter();

        // If the user entered a number of Traveller Checks
        if (ltr.getName().equals("Next"))
        {
            POSUIManagerIfc ui = (POSUIManagerIfc) bus.getManager(UIManagerIfc.TYPE);
            TenderCargo cargo = (TenderCargo)bus.getCargo();

            // This is to enforce that following sites will generate a new TenderADO based
            // on the entered value.
            cargo.setTenderADO(null);
            cargo.getTenderAttributes().put(TenderConstants.COUNT, Short.valueOf(ui.getInput()));
        }
    }
    
}
