/* ===========================================================================
* Copyright (c) 1998, 2011, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/customer/history/GetSelectedTransactionRoad.java /rgbustores_13.4x_generic_branch/1 2011/05/05 14:06:27 mszekely Exp $
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
 *    1    360Commerce 1.0         2/11/2005 12:11:12 PM  Robert Pearse   
 *
 *   Revision 1.4  2004/08/16 20:39:56  jdeleau
 *   @scr 6836 Make sure right transaction is retrieved, the list the selection is made from has to be put in the cargo, as well as the selected index.
 *
 *   Revision 1.3  2004/02/12 16:49:31  mcs
 *   Forcing head revision
 *
 *   Revision 1.2  2004/02/11 21:44:51  rhafernik
 *   @scr 0 Log4J conversion and code cleanup
 *
 *   Revision 1.1.1.1  2004/02/11 01:04:15  cschellenger
 *   updating to pvcs 360store-current
 *
 *
 * 
 *    Rev 1.0   Aug 29 2003 15:55:48   CSchellenger
 * Initial revision.
 * 
 *    Rev 1.0   Apr 29 2002 15:32:42   msg
 * Initial revision.
 * 
 *    Rev 1.1   Mar 18 2002 23:12:34   msg
 * - updated copyright
 * 
 *    Rev 1.0   Mar 18 2002 11:25:18   msg
 * Initial revision.
 * 
 *    Rev 1.3   Jan 19 2002 10:28:12   mpm
 * Initial implementation of pluggable-look-and-feel user interface.
 * Resolution for POS SCR-798: Implement pluggable-look-and-feel user interface
 * 
 *    Rev 1.2   19 Nov 2001 16:16:32   baa
 * customer & inquiry options cleanup
 * Resolution for POS SCR-293: F11enabled on Cust History/History Detail, choosing F11 hangs app
 * 
 *    Rev 1.1   05 Nov 2001 17:36:54   baa
 * Code Review changes. Customer, Customer history Inquiry Options
 * Resolution for POS SCR-244: Code Review  changes
 *
 *    Rev 1.0   19 Oct 2001 15:50:14   msg
 * Initial revision.
 * Resolution for 209: Customer History
 *
 *    Rev 1.0   19 Oct 2001 15:27:36   baa
 * Initial revision.
 * Resolution for POS SCR-209: Customer History
 * ===========================================================================
 */
package oracle.retail.stores.pos.services.customer.history;

// foundation imports
import oracle.retail.stores.domain.transaction.TransactionSummaryIfc;
import oracle.retail.stores.foundation.manager.ifc.UIManagerIfc;
import oracle.retail.stores.foundation.tour.application.LaneActionAdapter;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.pos.services.returns.returncustomer.ReturnCustomerCargo;
import oracle.retail.stores.pos.ui.POSUIManagerIfc;
import oracle.retail.stores.pos.ui.beans.ListBeanModel;
//--------------------------------------------------------------------------
/**
    $Revision: /rgbustores_13.4x_generic_branch/1 $
**/
//--------------------------------------------------------------------------
public class GetSelectedTransactionRoad extends LaneActionAdapter
{
    /**
       revision number supplied by PVCS
    **/
    public static final String revisionNumber = "$Revision: /rgbustores_13.4x_generic_branch/1 $";

    //----------------------------------------------------------------------
    /**
       Gets the selected transacation index from the ui.
       @param  bus     Service Bus
    **/
    //----------------------------------------------------------------------
    public void traverse(BusIfc bus)
    {

        // Get the index of the selected item
        POSUIManagerIfc ui;
        ui = (POSUIManagerIfc)bus.getManager(UIManagerIfc.TYPE);
        ListBeanModel model = (ListBeanModel)ui.getModel(POSUIManagerIfc.HISTORY_LIST);

        int selected = model.getSelectedRow();
        TransactionSummaryIfc[] summaries = (TransactionSummaryIfc[])model.getListVector().toArray(new TransactionSummaryIfc[0]);
        // Update the cargo
        ReturnCustomerCargo cargo = (ReturnCustomerCargo)bus.getCargo();
        cargo.setSelectedIndex(selected);
        cargo.setTransactionSummary(summaries);
     }
}
