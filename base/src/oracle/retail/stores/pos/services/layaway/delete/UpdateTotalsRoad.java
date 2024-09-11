/* ===========================================================================
* Copyright (c) 1998, 2011, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/layaway/delete/UpdateTotalsRoad.java /rgbustores_13.4x_generic_branch/1 2011/05/05 14:06:14 mszekely Exp $
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
 *    3    360Commerce 1.2         3/31/2005 4:30:41 PM   Robert Pearse   
 *    2    360Commerce 1.1         3/10/2005 10:26:37 AM  Robert Pearse   
 *    1    360Commerce 1.0         2/11/2005 12:15:26 PM  Robert Pearse   
 *
 *   Revision 1.3  2004/02/12 16:50:48  mcs
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
 *    Rev 1.0   Aug 29 2003 16:00:34   CSchellenger
 * Initial revision.
 * 
 *    Rev 1.0   Apr 29 2002 15:21:12   msg
 * Initial revision.
 * 
 *    Rev 1.0   Mar 18 2002 11:35:00   msg
 * Initial revision.
 * 
 *    Rev 1.0   Sep 21 2001 11:21:10   msg
 * Initial revision.
 * 
 *    Rev 1.1   Sep 17 2001 13:08:26   msg
 * header update
 * ===========================================================================
 */
package oracle.retail.stores.pos.services.layaway.delete;

// foundation imports
import oracle.retail.stores.domain.financial.LayawayIfc;
import oracle.retail.stores.domain.financial.PaymentIfc;
import oracle.retail.stores.domain.transaction.TenderableTransactionIfc;
import oracle.retail.stores.foundation.tour.application.LaneActionAdapter;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.pos.services.layaway.LayawayCargo;

//------------------------------------------------------------------------------
/**
    This class sets the total amount paid to the payment amount (refund amount)
    and resets the payment count to 0. <P>
    @version $Revision: /rgbustores_13.4x_generic_branch/1 $
**/
//------------------------------------------------------------------------------
public class UpdateTotalsRoad extends LaneActionAdapter
{                                       // begin class UpdateTotalsRoad
    /**
        lane name constant
    **/
    public static final String LANENAME = "UpdateTotalsRoad";
    /**
        revision number supplied by source-code-control system
    **/
    public static String revisionNumber = "$Revision: /rgbustores_13.4x_generic_branch/1 $";

    //--------------------------------------------------------------------------
    /**
        Performs the traversal functionality for the aisle.  In this case,
        updates the total amount paid to be reversed correctly in the database.
        Sets the payment counter to 0. <P>
        @param bus the bus traversing this lane
    **/
    //--------------------------------------------------------------------------
    public void traverse(BusIfc bus)
    {                                   // begin traverse()
        LayawayCargo layawayCargo = (LayawayCargo) bus.getCargo();

        // Get Layaway and Payment after printing
        LayawayIfc layaway = layawayCargo.getLayaway();
        PaymentIfc payment = layawayCargo.getPayment();
        TenderableTransactionIfc transaction = layawayCargo.getTenderableTransaction();

        // update total for db update
        layaway.setTotalAmountPaid(payment.getPaymentAmount());
        // set payment collected counter to 0 
        layaway.setPaymentCount(0);
    }                                   // end traverse()

}                                       // end class UpdateTotalsRoad
