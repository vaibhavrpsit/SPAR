/* ===========================================================================
* Copyright (c) 1998, 2011, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/sale/CancelSelectedAisle.java /rgbustores_13.4x_generic_branch/1 2011/05/05 14:06:01 mszekely Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    npoola    12/16/10 - set the transaction status to cancel
 *    cgreene   05/26/10 - convert to oracle packaging
 *    abondala  01/03/10 - update header date
 *
 * ===========================================================================
 * $Log:
 *    3    360Commerce 1.2         3/31/2005 4:27:20 PM   Robert Pearse   
 *    2    360Commerce 1.1         3/10/2005 10:19:58 AM  Robert Pearse   
 *    1    360Commerce 1.0         2/11/2005 12:09:46 PM  Robert Pearse   
 *
 *   Revision 1.4  2004/09/23 00:07:11  kmcbride
 *   @scr 7211: Adding static serialVersionUIDs to all POS Serializable objects, minus the JComponents
 *
 *   Revision 1.3  2004/02/12 16:48:17  mcs
 *   Forcing head revision
 *
 *   Revision 1.2  2004/02/11 21:22:50  rhafernik
 *   @scr 0 Log4J conversion and code cleanup
 *
 *   Revision 1.1.1.1  2004/02/11 01:04:11  cschellenger
 *   updating to pvcs 360store-current
 *
 *
 * 
 *    Rev 1.1   Nov 07 2003 12:36:22   baa
 * use SaleCargoIfc
 * Resolution for 3430: Sale Service Refactoring
 * 
 *    Rev 1.0   Nov 05 2003 14:13:46   baa
 * Initial revision.
 * 
 *    Rev 1.0   Aug 29 2003 16:04:10   CSchellenger
 * Initial revision.
 * 
 *    Rev 1.0   Aug 28 2002 14:14:28   HDyer
 * Initial revision.
 * Resolution for 1800: Add Manager Override Cancel Transaction feature
 * ===========================================================================
 */
package oracle.retail.stores.pos.services.sale;

// foundation imports
import oracle.retail.stores.domain.employee.RoleFunctionIfc;
import oracle.retail.stores.domain.transaction.TransactionConstantsIfc;
import oracle.retail.stores.foundation.tour.application.LaneActionAdapter;
import oracle.retail.stores.foundation.tour.application.Letter;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.foundation.tour.ifc.LaneActionIfc;

//------------------------------------------------------------------------------
/**
    This aisle class handles processing when the cancel key is pressed.
    @version $Revision: /rgbustores_13.4x_generic_branch/1 $
**/
//------------------------------------------------------------------------------

public class CancelSelectedAisle extends LaneActionAdapter implements LaneActionIfc
{
    // This id is used to tell
    // the compiler not to generate a
    // new serialVersionUID.
    //
    static final long serialVersionUID = 43618830911952058L;


    //--------------------------------------------------------------------------
    /**

            This aisle is traversed when the cancel key is pressed. It sets the
            role function ID in the cargo, and sends a letter to move to the
            next site.
            @param bus the bus traversing this lane
    **/
    //--------------------------------------------------------------------------

    public void traverse(BusIfc bus)
    {
        // Set up role id and send bus to check access site
        SaleCargoIfc cargo = (SaleCargoIfc) bus.getCargo();
        cargo.getTransaction().setTransactionStatus(TransactionConstantsIfc.STATUS_CANCELED);
        cargo.setAccessFunctionID(RoleFunctionIfc.CANCEL_TRANSACTION);
        bus.mail(new Letter("Override"), BusIfc.CURRENT);
    }

}
