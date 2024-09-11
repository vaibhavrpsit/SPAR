/* ===========================================================================
* Copyright (c) 1998, 2011, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/common/WriteHardTotalsErrorAisle.java /rgbustores_13.4x_generic_branch/1 2011/05/05 14:05:53 mszekely Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    cgreene   05/27/10 - convert to oracle packaging
 *    cgreene   05/27/10 - convert to oracle packaging
 *    cgreene   05/26/10 - convert to oracle packaging
 *    abondala  01/03/10 - update header date
 *    cgreene   02/04/09 - updated UIManager call to showScreen
 *
 * ===========================================================================
 * $Log:
 *    3    360Commerce 1.2         3/31/2005 4:30:49 PM   Robert Pearse   
 *    2    360Commerce 1.1         3/10/2005 10:26:53 AM  Robert Pearse   
 *    1    360Commerce 1.0         2/11/2005 12:15:40 PM  Robert Pearse   
 *
 *   Revision 1.3  2004/02/12 16:49:08  mcs
 *   Forcing head revision
 *
 *   Revision 1.2  2004/02/11 21:38:50  rhafernik
 *   @scr 0 Log4J conversion and code cleanup
 *
 *   Revision 1.1.1.1  2004/02/11 01:04:14  cschellenger
 *   updating to pvcs 360store-current
 *
 *
 * 
 *    Rev 1.0   Aug 29 2003 15:54:56   CSchellenger
 * Initial revision.
 * 
 *    Rev 1.0   Apr 29 2002 15:35:32   msg
 * Initial revision.
 * 
 *    Rev 1.1   Mar 18 2002 23:10:50   msg
 * - updated copyright
 * 
 *    Rev 1.0   Mar 18 2002 11:23:36   msg
 * Initial revision.
 * 
 *    Rev 1.0   Sep 21 2001 11:13:12   msg
 * Initial revision.
 * 
 *    Rev 1.1   Sep 17 2001 13:06:00   msg
 * header update
 * ===========================================================================
 */
package oracle.retail.stores.pos.services.common;

import oracle.retail.stores.foundation.manager.ifc.UIManagerIfc;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.pos.services.PosLaneActionAdapter;
import oracle.retail.stores.pos.ui.DialogScreensIfc;
import oracle.retail.stores.pos.ui.POSUIManagerIfc;
import oracle.retail.stores.pos.ui.beans.DialogBeanModel;

/**
 * Aisle to traverse if the hard totals could not be written.
 * 
 * @version $Revision: /rgbustores_13.4x_generic_branch/1 $
 */
public class WriteHardTotalsErrorAisle extends PosLaneActionAdapter
{
    private static final long serialVersionUID = 1207619235842321605L;

    /**
     * revision number supplied by Team Connection
     */
    public static final String revisionNumber = "$Revision: /rgbustores_13.4x_generic_branch/1 $";

    /**
     * Writes hard totals.
     * 
     * @param bus Service Bus
     */
    @Override
    public void traverse(BusIfc bus)
    {
        // get reference to ui and bean model
        POSUIManagerIfc ui = (POSUIManagerIfc)bus.getManager(UIManagerIfc.TYPE);

        // set bean model
        DialogBeanModel model = new DialogBeanModel();
        model.setResourceID("WriteHardTotalsError");
        model.setType(DialogScreensIfc.ERROR);
        ui.setModel(POSUIManagerIfc.DIALOG_TEMPLATE, model);

        // show dialog
        ui.showScreen(POSUIManagerIfc.DIALOG_TEMPLATE);
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