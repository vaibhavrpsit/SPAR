/* ===========================================================================
* Copyright (c) 1998, 2011, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/admin/resethardtotals/ResetHardTotalsConfirmSite.java /rgbustores_13.4x_generic_branch/1 2011/05/05 14:06:07 mszekely Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    cgreene   05/26/10 - convert to oracle packaging
 *    abondala  01/03/10 - update header date
 *    cgreene   02/04/09 - switch UI call to showScreen
 *
 * ===========================================================================
 * $Log:
 *    3    360Commerce 1.2         3/31/2005 4:29:40 PM   Robert Pearse   
 *    2    360Commerce 1.1         3/10/2005 10:24:44 AM  Robert Pearse   
 *    1    360Commerce 1.0         2/11/2005 12:13:44 PM  Robert Pearse   
 *
 *   Revision 1.4  2004/09/23 00:07:17  kmcbride
 *   @scr 7211: Adding static serialVersionUIDs to all POS Serializable objects, minus the JComponents
 *
 *   Revision 1.3  2004/02/12 16:48:54  mcs
 *   Forcing head revision
 *
 *   Revision 1.2  2004/02/11 21:36:47  rhafernik
 *   @scr 0 Log4J conversion and code cleanup
 *
 *   Revision 1.1.1.1  2004/02/11 01:04:14  cschellenger
 *   updating to pvcs 360store-current
 *
 *
 * 
 *    Rev 1.0   Aug 29 2003 15:53:20   CSchellenger
 * Initial revision.
 * 
 *    Rev 1.0   Apr 29 2002 15:38:12   msg
 * Initial revision.
 * 
 *    Rev 1.1   Mar 18 2002 23:06:40   msg
 * - updated copyright
 * 
 *    Rev 1.0   Mar 18 2002 11:20:44   msg
 * Initial revision.
 * 
 *    Rev 1.1   28 Nov 2001 12:59:50   epd
 * Separating manual from automatic resetting of Hard Totals
 * Resolution for POS SCR-216: Making POS changes to accommodate OnlineOffice
 * 
 *    Rev 1.0   Sep 21 2001 11:11:02   msg
 * Initial revision.
 * 
 *    Rev 1.1   Sep 17 2001 13:12:20   msg
 * header update
 * ===========================================================================
 */
package oracle.retail.stores.pos.services.admin.resethardtotals;

import oracle.retail.stores.foundation.manager.ifc.UIManagerIfc;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.foundation.tour.ifc.SiteActionIfc;
import oracle.retail.stores.pos.services.PosSiteActionAdapter;
import oracle.retail.stores.pos.ui.DialogScreensIfc;
import oracle.retail.stores.pos.ui.POSUIManagerIfc;
import oracle.retail.stores.pos.ui.beans.DialogBeanModel;

/**
 * Initial site for reset hard totals service.
 * 
 * @version $Revision: /rgbustores_13.4x_generic_branch/1 $
 */
public class ResetHardTotalsConfirmSite extends PosSiteActionAdapter implements SiteActionIfc
{
    static final long serialVersionUID = -1994398516703601865L;

    public static final String SITENAME = "ResetHardTotalsConfirmSite";

    /**
     * Display the screen to ask for resetting hard totals.
     * 
     * @param bus the bus arriving at this site
     */
    public void arrive(BusIfc bus)
    {
        POSUIManagerIfc ui;
        ui = (POSUIManagerIfc)bus.getManager(UIManagerIfc.TYPE);

        DialogBeanModel model = new DialogBeanModel();
        model.setResourceID("ResetHardTotals");
        model.setType(DialogScreensIfc.CONFIRMATION);
        ui.setModel(POSUIManagerIfc.DIALOG_TEMPLATE, model);

        // display dialog
        ui.showScreen(POSUIManagerIfc.DIALOG_TEMPLATE);
    }
}