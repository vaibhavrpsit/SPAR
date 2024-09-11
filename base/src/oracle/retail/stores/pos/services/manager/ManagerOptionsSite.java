/* ===========================================================================
* Copyright (c) 1998, 2012, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/manager/ManagerOptionsSite.java /main/11 2012/10/16 17:37:32 cgreene Exp $
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
 *    3    360Commerce 1.2         3/31/2005 4:29:00 PM   Robert Pearse   
 *    2    360Commerce 1.1         3/10/2005 10:23:27 AM  Robert Pearse   
 *    1    360Commerce 1.0         2/11/2005 12:12:32 PM  Robert Pearse   
 *
 *   Revision 1.4  2004/09/23 00:07:14  kmcbride
 *   @scr 7211: Adding static serialVersionUIDs to all POS Serializable objects, minus the JComponents
 *
 *   Revision 1.3  2004/02/12 16:50:58  mcs
 *   Forcing head revision
 *
 *   Revision 1.2  2004/02/11 21:51:37  rhafernik
 *   @scr 0 Log4J conversion and code cleanup
 *
 *   Revision 1.1.1.1  2004/02/11 01:04:17  cschellenger
 *   updating to pvcs 360store-current
 *
 *
 * 
 *    Rev 1.0   Aug 29 2003 16:01:16   CSchellenger
 * Initial revision.
 * 
 *    Rev 1.0   Apr 29 2002 15:18:46   msg
 * Initial revision.
 * 
 *    Rev 1.0   Mar 18 2002 11:36:22   msg
 * Initial revision.
 * 
 *    Rev 1.0   Sep 21 2001 11:23:54   msg
 * Initial revision.
 * 
 *    Rev 1.1   Sep 17 2001 13:08:56   msg
 * header update
 * ===========================================================================
 */
package oracle.retail.stores.pos.services.manager;

import oracle.retail.stores.foundation.manager.ifc.UIManagerIfc;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.foundation.tour.ifc.SiteActionIfc;
import oracle.retail.stores.pos.services.PosSiteActionAdapter;
import oracle.retail.stores.pos.ui.POSUIManagerIfc;
import oracle.retail.stores.pos.ui.beans.POSBaseBeanModel;

/**
 * @version $Revision: /main/11 $
 */
@SuppressWarnings("serial")
public class ManagerOptionsSite extends PosSiteActionAdapter implements SiteActionIfc
{
    public static final String SITENAME = "ManagerOptionsSite";

    /**
     * @param bus the bus arriving at this site
     */
    @Override
    public void arrive(BusIfc bus)
    {
        POSUIManagerIfc ui = (POSUIManagerIfc) bus.getManager(UIManagerIfc.TYPE);
        ui.showScreen(POSUIManagerIfc.MANAGER_OPTIONS, new POSBaseBeanModel());
    }

}
