/* ===========================================================================
* Copyright (c) 1998, 2011, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/admin/queue/DisplayQueueOptionsSite.java /rgbustores_13.4x_generic_branch/1 2011/05/05 14:06:08 mszekely Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    nkgautam  08/20/10 - fixed disabled delete and clear buttons when
 *                         transactions are there in queue and the client is
 *                         not restarted
 *    cgreene   05/26/10 - convert to oracle packaging
 *    cgreene   01/15/10 - disable delete and clear buttons per Queue
 *                         Management.doc, 2.6 Special Requirements
 *    abondala  01/03/10 - update header date
 *
 * ===========================================================================
 * $Log:
 *    3    360Commerce 1.2         3/31/2005 4:27:49 PM   Robert Pearse   
 *    2    360Commerce 1.1         3/10/2005 10:21:05 AM  Robert Pearse   
 *    1    360Commerce 1.0         2/11/2005 12:10:40 PM  Robert Pearse   
 *
 *   Revision 1.3  2004/02/12 16:48:52  mcs
 *   Forcing head revision
 *
 *   Revision 1.2  2004/02/11 21:35:20  rhafernik
 *   @scr 0 Log4J conversion and code cleanup
 *
 *   Revision 1.1.1.1  2004/02/11 01:04:13  cschellenger
 *   updating to pvcs 360store-current
 *
 *
 * 
 *    Rev 1.2   Oct 02 2003 10:06:30   bwf
 * Removed deprecation because flow has been reinstated.  Also removed unused imports.
 * 
 *    Rev 1.1   Sep 25 2003 12:25:18   bwf
 * Deprecated.
 * Resolution for 3334: Feature Enhancement:  Queue Exception Handling
 * 
 *    Rev 1.0   Aug 29 2003 15:53:14   CSchellenger
 * Initial revision.
 * 
 *    Rev 1.0   Apr 29 2002 15:38:30   msg
 * Initial revision.
 * 
 *    Rev 1.1   Mar 18 2002 23:06:18   msg
 * - updated copyright
 * 
 *    Rev 1.0   Mar 18 2002 11:20:26   msg
 * Initial revision.
 * 
 *    Rev 1.0   Sep 21 2001 11:12:28   msg
 * Initial revision.
 * 
 *    Rev 1.1   Sep 17 2001 13:11:56   msg
 * header update
 * ===========================================================================
 */
package oracle.retail.stores.pos.services.admin.queue;

import oracle.retail.stores.foundation.manager.ifc.DataManagerIfc;
import oracle.retail.stores.foundation.manager.ifc.UIManagerIfc;
import oracle.retail.stores.foundation.manager.ifc.data.DataTransactionQueueIfc;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.pos.services.PosSiteActionAdapter;
import oracle.retail.stores.pos.services.common.CommonActionsIfc;
import oracle.retail.stores.pos.ui.POSUIManagerIfc;
import oracle.retail.stores.pos.ui.beans.NavigationButtonBeanModel;
import oracle.retail.stores.pos.ui.beans.POSBaseBeanModel;

/**
 * This site displays the options available from the Queue Options screen.
 * 
 * @version $Revision: /rgbustores_13.4x_generic_branch/1 $
 */
public class DisplayQueueOptionsSite extends PosSiteActionAdapter
{ 
    private static final long serialVersionUID = -794004754074262761L;
    /** revision number supplied by source-code-control system */
    public static final String revisionNumber = "$Revision: /rgbustores_13.4x_generic_branch/1 $";

    /** site name constant */
    public static final String SITENAME = "DisplayQueueOptionsSite";

    /**
     * Displays queue options menu.
     * 
     * @param bus the bus arriving at this site
     */
    @Override
    public void arrive(BusIfc bus)
    {
        // disable buttons if queue is empty
        checkQueueEntries(bus);

        // get ui reference and display screen
        POSUIManagerIfc ui = (POSUIManagerIfc)bus.getManager(UIManagerIfc.TYPE);
        ui.showScreen(POSUIManagerIfc.QUEUE_OPTIONS);
    }

    /**
     * Disable buttons if queue is empty.
     * 
     * @param ui
     */
    protected void checkQueueEntries(BusIfc bus)
    {
        // Get the data manager and dialog model
        DataManagerIfc dm = (DataManagerIfc)bus.getManager(DataManagerIfc.TYPE);
        POSBaseBeanModel pModel = new POSBaseBeanModel();
        NavigationButtonBeanModel localModel = new NavigationButtonBeanModel();
        DataTransactionQueueIfc[] queues = dm.getDataTransactionQueues();
        if (queues != null)
        {
            for (int i = 0; i < queues.length; i++)
            {
                if (!queues[i].isEmpty())
                {
                    localModel.setButtonEnabled(CommonActionsIfc.DELETE, true);
                    localModel.setButtonEnabled(CommonActionsIfc.CLEAR, true);
                    pModel.setLocalButtonBeanModel(localModel);

                    POSUIManagerIfc ui = (POSUIManagerIfc)bus.getManager(UIManagerIfc.TYPE);
                    ui.setModel(POSUIManagerIfc.QUEUE_OPTIONS, pModel);
                    return; // do not invoke the disable button code below
                }
            }
        }

        
        localModel.setButtonEnabled(CommonActionsIfc.DELETE, false);
        localModel.setButtonEnabled(CommonActionsIfc.CLEAR, false);
        
        pModel.setLocalButtonBeanModel(localModel);

        POSUIManagerIfc ui = (POSUIManagerIfc)bus.getManager(UIManagerIfc.TYPE);
        ui.setModel(POSUIManagerIfc.QUEUE_OPTIONS, pModel);
    }

    /**
     * Returns the revision number of the class.
     * 
     * @return String representation of revision number
     */
    public String getRevisionNumber()
    {
        // return string
        return (revisionNumber);
    }
}