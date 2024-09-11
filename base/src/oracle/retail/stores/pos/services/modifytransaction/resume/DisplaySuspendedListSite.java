/* ===========================================================================
* Copyright (c) 1998, 2014, Oracle and/or its affiliates. All rights reserved.
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/modifytransaction/resume/DisplaySuspendedListSite.java /main/11 2014/05/14 14:41:28 cgreene Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    cgreene   05/14/14 - rename retrieve to resume
 *    cgreene   05/26/10 - convert to oracle packaging
 *    abondala  01/03/10 - update header date
 *
 * ===========================================================================
 * $Log:
 *    3    360Commerce 1.2         3/31/2005 4:27:50 PM   Robert Pearse   
 *    2    360Commerce 1.1         3/10/2005 10:21:06 AM  Robert Pearse   
 *    1    360Commerce 1.0         2/11/2005 12:10:40 PM  Robert Pearse   
 *
 *   Revision 1.3  2004/02/12 16:51:12  mcs
 *   Forcing head revision
 *
 *   Revision 1.2  2004/02/11 21:51:45  rhafernik
 *   @scr 0 Log4J conversion and code cleanup
 *
 *   Revision 1.1.1.1  2004/02/11 01:04:18  cschellenger
 *   updating to pvcs 360store-current
 *
 *
 * 
 *    Rev 1.0   Aug 29 2003 16:02:34   CSchellenger
 * Initial revision.
 * 
 *    Rev 1.0   Apr 29 2002 15:15:48   msg
 * Initial revision.
 * 
 *    Rev 1.1   26 Apr 2002 10:58:20   vxs
 * Added cargo.setVisitedSuspendedListSite(true); in arrive()
 * Resolution for POS SCR-1612: Susp/Resume - SUSP_NOT_RETRIEVABLE - system crashes when pressing enter
 *
 *    Rev 1.0   Mar 18 2002 11:39:08   msg
 * Initial revision.
 *
 *    Rev 1.1   Jan 19 2002 10:28:22   mpm
 * Initial implementation of pluggable-look-and-feel user interface.
 * Resolution for POS SCR-798: Implement pluggable-look-and-feel user interface
 *
 *    Rev 1.0   Sep 21 2001 11:31:14   msg
 * Initial revision.
 *
 *    Rev 1.1   Sep 17 2001 13:09:46   msg
 * header update
 * ===========================================================================
 */
package oracle.retail.stores.pos.services.modifytransaction.resume;

import oracle.retail.stores.domain.transaction.TransactionSummaryIfc;
import oracle.retail.stores.foundation.manager.ifc.UIManagerIfc;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.pos.services.PosSiteActionAdapter;
import oracle.retail.stores.pos.ui.POSUIManagerIfc;
import oracle.retail.stores.pos.ui.beans.ListBeanModel;

/**
 * Displays list of suspended transactions.
 * 
 * @version $Revision: /main/11 $
 */
@SuppressWarnings("serial")
public class DisplaySuspendedListSite extends PosSiteActionAdapter
{
    /**
     * revision number supplied by source-code control system
     */
    public static final String revisionNumber = "$Revision: /main/11 $";
    /**
     * site name constant
     */
    public static final String SITENAME = "DisplaySuspendedListSite";

    /**
     * Displays list of suspended transactions.
     * 
     * @param bus the bus arriving at this site
     */
    @Override
    public void arrive(BusIfc bus)
    {
        ModifyTransactionResumeCargo cargo = (ModifyTransactionResumeCargo)bus.getCargo();
        // last site visited as of now is the displaySuspendedListSite
        cargo.setVisitedSuspendedListSite(true);
        // get suspend list and set up bean model
        TransactionSummaryIfc[] suspendList = cargo.getSuspendList();
        ListBeanModel model = new ListBeanModel();
        model.setListModel(suspendList);
        // issue message indicating report is printing
        POSUIManagerIfc ui = (POSUIManagerIfc)bus.getManager(UIManagerIfc.TYPE);
        ui.showScreen(POSUIManagerIfc.SUSPEND_LIST, model);
    }
}
