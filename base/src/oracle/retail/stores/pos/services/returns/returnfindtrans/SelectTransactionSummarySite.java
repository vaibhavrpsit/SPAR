/* ===========================================================================
* Copyright (c) 2008, 2011, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/returns/returnfindtrans/SelectTransactionSummarySite.java /rgbustores_13.4x_generic_branch/1 2011/05/05 14:05:56 mszekely Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    cgreene   05/26/10 - convert to oracle packaging
 *
 * ===========================================================================
 * $Log:
 *   3    360Commerce 1.2         3/31/2005 4:29:55 PM   Robert Pearse   
 *   2    360Commerce 1.1         3/10/2005 10:25:10 AM  Robert Pearse   
 *   1    360Commerce 1.0         2/11/2005 12:14:08 PM  Robert Pearse   
 *  $
 *  Revision 1.4  2004/02/17 20:40:28  baa
 *  @scr 3561 returns
 *
 *  Revision 1.3  2004/02/12 16:51:48  mcs
 *  Forcing head revision
 *
 *  Revision 1.2  2004/02/11 21:52:28  rhafernik
 *  @scr 0 Log4J conversion and code cleanup
 *
 *  Revision 1.1.1.1  2004/02/11 01:04:20  cschellenger
 *  updating to pvcs 360store-current
 *
 *
 * 
 *    Rev 1.1   Dec 30 2003 16:58:42   baa
 * cleanup for return feature
 * Resolution for 3561: Feature Enhacement: Return Search by Tender
 * 
 *    Rev 1.0   Dec 29 2003 15:43:26   baa
 * Initial revision.
 * ===========================================================================
 */
package oracle.retail.stores.pos.services.returns.returnfindtrans;

import oracle.retail.stores.foundation.manager.ifc.UIManagerIfc;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.pos.services.PosSiteActionAdapter;
import oracle.retail.stores.pos.ui.POSUIManagerIfc;
import oracle.retail.stores.pos.ui.beans.ListBeanModel;

/**
 *
 * Provides a list of transaction summary information to the UI.  
 */
public class SelectTransactionSummarySite extends PosSiteActionAdapter
{

    /**
     * Raw revision number string for the site.
     */
    public static final String revisionNumber = "$Revision: /rgbustores_13.4x_generic_branch/1 $";

    /**
     * Provides a list of transaction summary information to the UI.  
     * @param bus  provides the cargo & managers necessary to provide the list to the UI.
     */
    public void arrive(BusIfc bus)
    {

        // Create the model and set the data
        ReturnFindTransCargo cargo = (ReturnFindTransCargo) bus.getCargo();
        ListBeanModel model = new ListBeanModel();
        model.setListModel(cargo.getTransactionSummaries());
        model.setSelectedRow(0);
        // Display the screen
        POSUIManagerIfc ui = (POSUIManagerIfc) bus.getManager(UIManagerIfc.TYPE);
        ui.showScreen(POSUIManagerIfc.RETURN_LINKED_TRANS, model);
    }

}
