/* ===========================================================================
* Copyright (c) 1998, 2011, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/layaway/payment/UndoPaymentRoad.java /rgbustores_13.4x_generic_branch/1 2011/05/05 14:06:13 mszekely Exp $
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
 *    3    360Commerce 1.2         3/31/2005 4:30:38 PM   Robert Pearse   
 *    2    360Commerce 1.1         3/10/2005 10:26:31 AM  Robert Pearse   
 *    1    360Commerce 1.0         2/11/2005 12:15:22 PM  Robert Pearse   
 *
 *   Revision 1.4  2004/07/06 22:20:41  aachinfiev
 *   @scr 5257 - Added a line to reset layawayFee. It was accumulating without
 *   being reset, therefore resulting in incorrect balance.
 *
 *   Revision 1.3  2004/02/12 16:50:53  mcs
 *   Forcing head revision
 *
 *   Revision 1.2  2004/02/11 21:51:22  rhafernik
 *   @scr 0 Log4J conversion and code cleanup
 *
 *   Revision 1.1.1.1  2004/02/11 01:04:17  cschellenger
 *   updating to pvcs 360store-current
 *
 *
 * 
 *    Rev 1.0   Aug 29 2003 16:00:54   CSchellenger
 * Initial revision.
 * 
 *    Rev 1.0   Apr 29 2002 15:20:22   msg
 * Initial revision.
 * 
 *    Rev 1.0   Mar 18 2002 11:35:40   msg
 * Initial revision.
 * 
 *    Rev 1.0   Sep 21 2001 11:21:44   msg
 * Initial revision.
 * 
 *    Rev 1.1   Sep 17 2001 13:08:40   msg
 * header update
 * ===========================================================================
 */
package oracle.retail.stores.pos.services.layaway.payment;

// foundation imports
import oracle.retail.stores.domain.financial.LayawayIfc;
import oracle.retail.stores.domain.transaction.LayawayTransactionIfc;
import oracle.retail.stores.domain.transaction.TenderableTransactionIfc;
import oracle.retail.stores.domain.transaction.TransactionIfc;
import oracle.retail.stores.domain.transaction.TransactionTotalsIfc;
import oracle.retail.stores.foundation.tour.application.LaneActionAdapter;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.pos.services.layaway.LayawayCargo;

//------------------------------------------------------------------------------
/**
    This road is traversed when the user presses undo from the payment site. <P>
    @version $Revision: /rgbustores_13.4x_generic_branch/1 $
**/
//------------------------------------------------------------------------------
public class UndoPaymentRoad extends LaneActionAdapter
{                                       // begin class UndoPaymentRoad
    /**
        lane name constant
    **/
    public static final String LANENAME = "UndoPaymentRoad";
    /**
        revision number supplied by source-code-control system
    **/
    public static String revisionNumber = "$Revision: /rgbustores_13.4x_generic_branch/1 $";
    

    //--------------------------------------------------------------------------
    /**
        Performs the traversal functionality for the aisle.  In this case,
        The layaway totals are set to there origional values when sent back 
        to pos service. The Layaway Creation Fee is removed.<P>
        @param bus the bus traversing this lane
    **/
    //--------------------------------------------------------------------------
    public void traverse(BusIfc bus)
    {                                   // begin traverse()
        LayawayCargo layawayCargo = (LayawayCargo)bus.getCargo();     

        TenderableTransactionIfc trans = layawayCargo.getTenderableTransaction();
        
        //  Removes the Layaway Creation Fee from the grand total
        if ( trans.getTransactionType() == TransactionIfc.TYPE_LAYAWAY_INITIATE )
        {
            LayawayTransactionIfc layawayTransaction = ( LayawayTransactionIfc )trans;
            TransactionTotalsIfc totals = layawayTransaction.getTransactionTotals();
            LayawayIfc layaway = layawayTransaction.getLayaway();
            totals.setGrandTotal( totals.getGrandTotal().subtract( layaway.getCreationFee() ) );
            totals.setLayawayFee(totals.getLayawayFee().subtract(layaway.getCreationFee()));
        }             
    }                                   // end traverse()        
}                                       // end class UndoPaymentRoad
