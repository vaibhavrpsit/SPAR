/* ===========================================================================
* Copyright (c) 1998, 2013, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/returns/returncommon/UndoCustomerRoad.java /main/11 2013/03/19 11:29:49 abhinavs Exp $
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
 *   Revision 1.4  2004/07/26 22:26:58  mweis
 *   @scr 6147 If customer is still linked, keep them on the status panel.
 *
 *   Revision 1.3  2004/02/12 16:51:46  mcs
 *   Forcing head revision
 *
 *   Revision 1.2  2004/02/11 21:52:30  rhafernik
 *   @scr 0 Log4J conversion and code cleanup
 *
 *   Revision 1.1.1.1  2004/02/11 01:04:20  cschellenger
 *   updating to pvcs 360store-current
 *
 *
 * 
 *    Rev 1.0   Aug 29 2003 16:05:52   CSchellenger
 * Initial revision.
 * 
 *    Rev 1.0   Apr 29 2002 15:06:58   msg
 * Initial revision.
 * 
 *    Rev 1.0   Mar 18 2002 11:45:20   msg
 * Initial revision.
 * 
 *    Rev 1.2   11 Dec 2001 12:08:54   jbp
 * changes to check for setting customer name.
 * Resolution for POS SCR-418: Return Updates
 *
 *    Rev 1.1   10 Dec 2001 12:29:36   jbp
 * keeps customer from current transaction when returning from an origional transaction.
 * Resolution for POS SCR-418: Return Updates
 *
 *    Rev 1.0   Sep 21 2001 11:24:28   msg
 * Initial revision.
 *
 *    Rev 1.1   Sep 17 2001 13:12:22   msg
 * header update
 * ===========================================================================
 */
package oracle.retail.stores.pos.services.returns.returncommon;
// foundation imports
import oracle.retail.stores.foundation.manager.ifc.UIManagerIfc;
import oracle.retail.stores.foundation.tour.application.LaneActionAdapter;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.pos.ui.POSUIManagerIfc;

//--------------------------------------------------------------------------
/**
    Undo the customer name from  status bar due to Esc by the user.
    @version $Revision: /main/11 $
**/
//--------------------------------------------------------------------------
public class UndoCustomerRoad extends LaneActionAdapter
{
    /**
        class name constant
    **/
    public static final String LANENAME = "UndoCustomerRoad";

    /**
       revision number
    **/
    public static final String revisionNumber = "$Revision: /main/11 $";

    //----------------------------------------------------------------------
    /**
       Undo the customer name from  status bar due to Esc by the user.
       @param bus the bus traversing this lane
    **/
    //----------------------------------------------------------------------
    public void traverse(BusIfc bus)
    {
          // set the customer's name in the status area
        POSUIManagerIfc ui = (POSUIManagerIfc)bus.getManager(UIManagerIfc.TYPE);

        // ReturnTransactionsCargoIfc is implemented by
        // ReturnCustomerCargo and ReturnFindTransactionCargo
        ReturnTransactionsCargoIfc cargo = (ReturnTransactionsCargoIfc)bus.getCargo();
        
        if ((cargo.getTransaction() == null))
            //  Since the customer is still really linked, we shouldn't pretend they aren't.
            //  || (cargo.getTransaction() != null &&
            //      cargo.getTransaction().getCustomer() != null))
        {
            // updates the status panel to show no customer linked
            ui.customerNameChanged("");
        }
    }
}
