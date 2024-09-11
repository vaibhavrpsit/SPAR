/* ===========================================================================
* Copyright (c) 1998, 2011, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/reprintreceipt/ReprintReceiptOptionsSite.java /rgbustores_13.4x_generic_branch/1 2011/05/05 14:06:30 mszekely Exp $
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
 *    3    360Commerce 1.2         3/31/2005 4:29:40 PM   Robert Pearse   
 *    2    360Commerce 1.1         3/10/2005 10:24:43 AM  Robert Pearse   
 *    1    360Commerce 1.0         2/11/2005 12:13:43 PM  Robert Pearse   
 *
 *   Revision 1.5  2004/04/26 19:51:14  dcobb
 *   @scr 4452 Feature Enhancement: Printing
 *   Add Reprint Select flow.
 *
 *   Revision 1.4  2004/04/22 17:39:00  dcobb
 *   @scr 4452 Feature Enhancement: Printing
 *   Added REPRINT_SELECT screen and flow to Reprint Receipt use case..
 *
 *   Revision 1.3  2004/02/12 16:51:42  mcs
 *   Forcing head revision
 *
 *   Revision 1.2  2004/02/11 21:52:29  rhafernik
 *   @scr 0 Log4J conversion and code cleanup
 *
 *   Revision 1.1.1.1  2004/02/11 01:04:20  cschellenger
 *   updating to pvcs 360store-current
 *
 *
 * 
 *    Rev 1.0   Aug 29 2003 16:05:42   CSchellenger
 * Initial revision.
 * 
 *    Rev 1.0   Apr 29 2002 15:07:18   msg
 * Initial revision.
 * 
 *    Rev 1.0   Mar 18 2002 11:44:54   msg
 * Initial revision.
 * 
 *    Rev 1.0   Sep 21 2001 11:23:08   msg
 * Initial revision.
 * 
 *    Rev 1.1   Sep 17 2001 13:12:16   msg
 * header update
 * ===========================================================================
 */
package oracle.retail.stores.pos.services.reprintreceipt;
// foundation imports
import oracle.retail.stores.foundation.manager.ifc.UIManagerIfc;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.pos.services.PosSiteActionAdapter;
import oracle.retail.stores.pos.ui.POSUIManagerIfc;
import oracle.retail.stores.pos.ui.beans.POSBaseBeanModel;

//--------------------------------------------------------------------------
/**
    This site displays the reprint receipt options menu.
    @version $Revision: /rgbustores_13.4x_generic_branch/1 $
**/
//--------------------------------------------------------------------------
public class ReprintReceiptOptionsSite extends PosSiteActionAdapter
{                                       // begin class ReprintReceiptOptionsSite
    /** revision number of this class */
    public static final String revisionNumber = "$Revision: /rgbustores_13.4x_generic_branch/1 $";

    //----------------------------------------------------------------------
    /**
        Shows the screen for all the options for ReprintReceipt
        @param  bus     Service Bus
    **/
    //----------------------------------------------------------------------
    public void arrive(BusIfc bus)
    {                                   // begin arrive()
        // initialize the receipt count 
        ReprintReceiptCargo cargo = (ReprintReceiptCargo)bus.getCargo();
        cargo.setReceiptPrinted(false);
        cargo.setReprintReceiptCount(0);
        
        // get the POS UI manager and display the screen
        POSUIManagerIfc ui = (POSUIManagerIfc) bus.getManager(UIManagerIfc.TYPE);
        ui.showScreen(POSUIManagerIfc.REPRINT_RECEIPT_OPTIONS,
                      new POSBaseBeanModel());
    }                                   // end arrive()

}                                       // end class ReprintReceiptOptionsSite
