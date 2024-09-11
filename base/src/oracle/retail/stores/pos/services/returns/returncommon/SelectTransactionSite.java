/* ===========================================================================
* Copyright (c) 1998, 2012, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/returns/returncommon/SelectTransactionSite.java /main/11 2012/10/29 12:55:22 jswan Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    jswan     10/25/12 - Modified to support returns by order.
 *    cgreene   05/26/10 - convert to oracle packaging
 *    abondala  01/03/10 - update header date
 *
 * ===========================================================================
 * $Log:
 *    3    360Commerce 1.2         3/31/2005 4:29:55 PM   Robert Pearse   
 *    2    360Commerce 1.1         3/10/2005 10:25:10 AM  Robert Pearse   
 *    1    360Commerce 1.0         2/11/2005 12:14:08 PM  Robert Pearse   
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
 *    Rev 1.1   Jan 07 2004 13:09:06   baa
 * fix null pointer
 * Resolution for 3561: Feature Enhacement: Return Search by Tender
 * 
 *    Rev 1.0   Aug 29 2003 16:05:52   CSchellenger
 * Initial revision.
 * 
 *    Rev 1.0   Apr 29 2002 15:06:54   msg
 * Initial revision.
 * 
 *    Rev 1.0   Mar 18 2002 11:45:18   msg
 * Initial revision.
 * 
 *    Rev 1.1   Jan 19 2002 10:28:26   mpm
 * Initial implementation of pluggable-look-and-feel user interface.
 * Resolution for POS SCR-798: Implement pluggable-look-and-feel user interface
 * 
 *    Rev 1.0   Sep 21 2001 11:24:38   msg
 * Initial revision.
 * 
 *    Rev 1.1   Sep 17 2001 13:12:24   msg
 * header update
 * ===========================================================================
 */
package oracle.retail.stores.pos.services.returns.returncommon;

// foundation imports
import oracle.retail.stores.foundation.manager.ifc.UIManagerIfc;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.pos.services.PosSiteActionAdapter;
import oracle.retail.stores.pos.ui.POSUIManagerIfc;
import oracle.retail.stores.pos.ui.beans.ListBeanModel;

//------------------------------------------------------------------------------
/**
    This site displays a list of transactions.
**/
//--------------------------------------------------------------------------
@SuppressWarnings("serial")
public class SelectTransactionSite extends PosSiteActionAdapter
{
    //--------------------------------------------------------------------------
    /**
       This site displays a list of transactions.
       @param bus the bus arriving at this site
    **/
    //--------------------------------------------------------------------------
    public void arrive(BusIfc bus)
    {

        // Create the model and set the data
        ReturnTransactionsCargoIfc cargo = (ReturnTransactionsCargoIfc) bus.getCargo();
        ListBeanModel model = new ListBeanModel();
        model.setListModel(cargo.getTransactionSummary());
        model.setSelectedRow(0);

        // Display the screen
        POSUIManagerIfc ui = (POSUIManagerIfc) bus.getManager(UIManagerIfc.TYPE);
        ui.showScreen(POSUIManagerIfc.RETURN_LINKED_TRANS, model);
    }
}
