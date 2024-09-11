/* ===========================================================================
* Copyright (c) 1998, 2011, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/servicealert/SetSelectedAlertRoad.java /main/10 2011/02/16 09:13:25 cgreene Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    cgreene   02/15/11 - move constants into interfaces and refactor
 *    cgreene   05/26/10 - convert to oracle packaging
 *    abondala  01/03/10 - update header date
 *
 * ===========================================================================
 * $Log:
 *    5    360Commerce 1.4         1/9/2008 9:26:03 PM    Anil Bondalapati
 *         removed the commented code related to removing the parameter
 *         Browser.
 *    4    360Commerce 1.3         12/18/2007 1:23:56 PM  Anil Bondalapati
 *         Removed the Browser group as it is not supported in the base
 *         product.
 *    3    360Commerce 1.2         3/31/2005 4:29:57 PM   Robert Pearse   
 *    2    360Commerce 1.1         3/10/2005 10:25:15 AM  Robert Pearse   
 *    1    360Commerce 1.0         2/11/2005 12:14:12 PM  Robert Pearse   
 *
 *   Revision 1.3.4.2  2004/11/05 21:54:44  bwf
 *   @scr 7529 Save screen used to use in next site to avoid reoccuring crash when site is changed.
 *
 *   Revision 1.3.4.1  2004/10/27 16:12:40  bwf
 *   @scr 7529 Check if browserBeanModel installed since it is checked in creating screen.
 *                     Otherwise we get a class cast exception, becuase we are trying to get from the wrong screen.
 *
 *   Revision 1.3  2004/02/12 16:51:58  mcs
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
 *    Rev 1.1   Oct 21 2003 17:47:22   sfl
 * Read the parameter value to decide which service alert screen to be displayed.
 * Resolution for POS SCR-3414: Parameterizing Web Access
 * 
 *    Rev 1.0   Aug 29 2003 16:07:00   CSchellenger
 * Initial revision.
 * 
 *    Rev 1.0   Apr 29 2002 15:03:24   msg
 * Initial revision.
 * 
 *    Rev 1.0   Mar 18 2002 11:47:42   msg
 * Initial revision.
 * 
 *    Rev 1.1   16 Feb 2002 18:14:38   baa
 * more ui fixes
 * Resolution for POS SCR-798: Implement pluggable-look-and-feel user interface
 *
 *    Rev 1.0   Jan 09 2002 12:35:28   dfh
 * Initial revision.
 * Resolution for POS SCR-179: CR/Order, after Svc Alert queue empty, db error, then app hung
 *
 * ===========================================================================
 */
package oracle.retail.stores.pos.services.servicealert;

import oracle.retail.stores.domain.alert.AlertEntryIfc;
import oracle.retail.stores.foundation.manager.ifc.UIManagerIfc;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.pos.services.PosLaneActionAdapter;
import oracle.retail.stores.pos.ui.POSUIManagerIfc;
import oracle.retail.stores.pos.ui.beans.ListBeanModel;

/**
 * Road to set the selected service alert entry from the Service Alert screen to
 * service alert cargo.
 * 
 * @version $Revision: /main/10 $
 */
public class SetSelectedAlertRoad extends PosLaneActionAdapter
{
    private static final long serialVersionUID = -1126577210085768192L;

    /**
     * road name constant
     */
    public static final String LANENAME = "SetSelectedAlertRoad";

    /**
     * revision number
     */
    public static final String revisionNumber = "$Revision: /main/10 $";

    /**
     * Sets the selected service alert entry from the Service Alert screen to
     * service alert cargo.
     * 
     * @param bus Service Bus
     */
    @Override
    public void traverse(BusIfc bus)
    {
        ServiceAlertCargo cargo  = (ServiceAlertCargo) bus.getCargo();

        POSUIManagerIfc  ui = (POSUIManagerIfc) bus.getManager(UIManagerIfc.TYPE);

        // get the screen name from cargo that was displayed
        String serviceAlertScreenName = cargo.getScreenNameUsed();
        
        ListBeanModel  beanModel =
            (ListBeanModel) ui.getModel(serviceAlertScreenName);

        cargo.setSelectedEntry((AlertEntryIfc)beanModel.getSelectedValue());
    }
}
