/* ===========================================================================
* Copyright (c) 1998, 2011, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/reprintreceipt/TransactionIDEnteredAisle.java /rgbustores_13.4x_generic_branch/1 2011/05/05 14:06:30 mszekely Exp $
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
 *    3    360Commerce 1.2         3/31/2005 4:30:34 PM   Robert Pearse   
 *    2    360Commerce 1.1         3/10/2005 10:26:22 AM  Robert Pearse   
 *    1    360Commerce 1.0         2/11/2005 12:15:14 PM  Robert Pearse   
 *
 *   Revision 1.5  2004/04/22 17:39:00  dcobb
 *   @scr 4452 Feature Enhancement: Printing
 *   Added REPRINT_SELECT screen and flow to Reprint Receipt use case..
 *
 *   Revision 1.4  2004/03/03 23:15:09  bwf
 *   @scr 0 Fixed CommonLetterIfc deprecations.
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
 *    Rev 1.0   Apr 29 2002 15:07:20   msg
 * Initial revision.
 * 
 *    Rev 1.0   Mar 18 2002 11:44:56   msg
 * Initial revision.
 * 
 *    Rev 1.1   Feb 04 2002 11:05:44   dfh
 * set transactionIDEntered to true (by user)
 * Resolution for POS SCR-260: Special Order feature for release 5.0
 * 
 *    Rev 1.0   Sep 21 2001 11:22:54   msg
 * Initial revision.
 * 
 *    Rev 1.1   Sep 17 2001 13:12:14   msg
 * header update
 * ===========================================================================
 */
package oracle.retail.stores.pos.services.reprintreceipt;
// foundation imports
import oracle.retail.stores.pos.services.common.CommonLetterIfc;
import oracle.retail.stores.foundation.manager.ifc.UIManagerIfc;
import oracle.retail.stores.foundation.tour.application.LaneActionAdapter;
import oracle.retail.stores.foundation.tour.application.Letter;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.pos.ui.POSUIManagerIfc;

//--------------------------------------------------------------------------
/**
    This road is traveled when the transaction ID has been entered.
    @version $Revision: /rgbustores_13.4x_generic_branch/1 $
**/
//--------------------------------------------------------------------------
public class TransactionIDEnteredAisle extends LaneActionAdapter
{                                       // begin TransactionIDEnteredAisle        
    /** revision number */
    public static final String revisionNumber = "$Revision: /rgbustores_13.4x_generic_branch/1 $";

    //----------------------------------------------------------------------
    /**
        Retrieves the transaction ID from the user interface and stores 
        it in the cargo.   A Continue letter is mailed. <P>
        @param  bus     Service Bus
    **/
    //----------------------------------------------------------------------
    public void traverse(BusIfc bus)
    {                                   // begin traverse()
        POSUIManagerIfc ui = (POSUIManagerIfc) bus.getManager(UIManagerIfc.TYPE);
        ReprintReceiptCargo cargo = (ReprintReceiptCargo) bus.getCargo();
        cargo.setTransactionID(ui.getInput());
        cargo.setTransactionIDEntered(true);
        bus.mail(new Letter(CommonLetterIfc.CONTINUE), BusIfc.CURRENT);
    }                                   // end traverse()

}                                       // end class TransactionIDEnteredAisle
