/* ===========================================================================
* Copyright (c) 1998, 2011, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/tender/purchaseorder/GetPurchaseOrderNumberSite.java /rgbustores_13.4x_generic_branch/1 2011/05/05 14:05:47 mszekely Exp $
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
 *    3    360Commerce 1.2         3/31/2005 4:28:15 PM   Robert Pearse   
 *    2    360Commerce 1.1         3/10/2005 10:21:50 AM  Robert Pearse   
 *    1    360Commerce 1.0         2/11/2005 12:11:11 PM  Robert Pearse   
 *
 *   Revision 1.1  2004/04/02 22:13:51  epd
 *   @scr 4263 Updates to move Purchase Order tender to its own tour
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
 *    Rev 1.0   Oct 24 2003 14:54:52   bwf
 * Initial revision.
 * Resolution for 3418: Purchase Order Tender Refactor
 * ===========================================================================
 */
package oracle.retail.stores.pos.services.tender.purchaseorder;

import oracle.retail.stores.pos.ado.tender.TenderConstants;
import oracle.retail.stores.pos.services.tender.TenderCargo;
import oracle.retail.stores.foundation.manager.ifc.UIManagerIfc;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.foundation.tour.ifc.LetterIfc;
import oracle.retail.stores.pos.services.PosSiteActionAdapter;
import oracle.retail.stores.pos.ui.POSUIManagerIfc;
import oracle.retail.stores.pos.ui.beans.POSBaseBeanModel;

//--------------------------------------------------------------------------
/**
    This class displays the screen to get the PO number and then reads it in.
    $Revision: /rgbustores_13.4x_generic_branch/1 $
**/
//--------------------------------------------------------------------------
public class GetPurchaseOrderNumberSite extends PosSiteActionAdapter
{
    /** revision number **/
    public static final String revisionNumber = "$Revision: /rgbustores_13.4x_generic_branch/1 $";
    
    //----------------------------------------------------------------------
    /**
        Arrive method displays screen.
        @param  bus     Service Bus
    **/
    //----------------------------------------------------------------------
    public void arrive(BusIfc bus)
    {
        POSUIManagerIfc ui = (POSUIManagerIfc) bus.getManager(UIManagerIfc.TYPE);
        ui.showScreen(POSUIManagerIfc.PURCHASE_ORDER_NUMBER, new POSBaseBeanModel());        
    }   
    
    //----------------------------------------------------------------------
    /**
        Depart method retrieves input.
        @param  bus     Service Bus
    **/
    //----------------------------------------------------------------------
    public void depart(BusIfc bus)
    {
        LetterIfc letter = (LetterIfc) bus.getCurrentLetter();

        // If the user entered a number of Traveller Checks
        if (letter.getName().equals("Next"))
        {
            POSUIManagerIfc ui = (POSUIManagerIfc) bus.getManager(UIManagerIfc.TYPE);
            // Get the purchase order number
            String poNumber = ui.getInput();
            poNumber = poNumber.trim();
            TenderCargo cargo = (TenderCargo)bus.getCargo();
            cargo.getTenderAttributes().put(TenderConstants.NUMBER, poNumber); 
        }
    }
    
}
