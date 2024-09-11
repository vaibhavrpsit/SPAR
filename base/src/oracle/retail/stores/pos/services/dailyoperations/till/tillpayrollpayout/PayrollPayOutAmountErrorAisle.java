/* ===========================================================================
* Copyright (c) 1998, 2011, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/dailyoperations/till/tillpayrollpayout/PayrollPayOutAmountErrorAisle.java /rgbustores_13.4x_generic_branch/1 2011/05/05 14:06:19 mszekely Exp $
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
 *    3    360Commerce 1.2         3/31/2005 4:29:20 PM   Robert Pearse   
 *    2    360Commerce 1.1         3/10/2005 10:24:02 AM  Robert Pearse   
 *    1    360Commerce 1.0         2/11/2005 12:13:01 PM  Robert Pearse   
 *
 *   Revision 1.3  2004/09/23 00:07:17  kmcbride
 *   @scr 7211: Adding static serialVersionUIDs to all POS Serializable objects, minus the JComponents
 *
 *   Revision 1.2  2004/03/16 18:30:47  cdb
 *   @scr 0 Removed tabs from all java source code.
 *
 *   Revision 1.1  2004/03/12 18:19:23  khassen
 *   @scr 0 Till Pay In/Out use case
 *
 *   Revision 1.3  2004/02/12 16:50:04  mcs
 *   Forcing head revision
 *
 *   Revision 1.2  2004/02/11 21:47:51  rhafernik
 *   @scr 0 Log4J conversion and code cleanup
 *
 *   Revision 1.1.1.1  2004/02/11 01:04:15  cschellenger
 *   updating to pvcs 360store-current
 *
 *
 * 
 *    Rev 1.0   Aug 29 2003 15:58:18   CSchellenger
 * Initial revision.
 * 
 *    Rev 1.0   Apr 29 2002 15:26:40   msg
 * Initial revision.
 * 
 *    Rev 1.0   Mar 18 2002 11:30:22   msg
 * Initial revision.
 * 
 *    Rev 1.0   Sep 21 2001 11:19:36   msg
 * Initial revision.
 * 
 *    Rev 1.1   Sep 17 2001 13:14:56   msg
 * header update
 * ===========================================================================
 */
package oracle.retail.stores.pos.services.dailyoperations.till.tillpayrollpayout;

// foundation imports
import oracle.retail.stores.foundation.manager.ifc.UIManagerIfc;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.foundation.tour.ifc.LaneActionIfc;
import oracle.retail.stores.pos.services.PosLaneActionAdapter;
import oracle.retail.stores.pos.ui.DialogScreensIfc;
import oracle.retail.stores.pos.ui.POSUIManagerIfc;
import oracle.retail.stores.pos.ui.beans.DialogBeanModel;

/**
 * 
 * @author khassen
 *
 * Displays a dialog on an error when attempting to do a payroll pay out.
 */
public class PayrollPayOutAmountErrorAisle extends PosLaneActionAdapter
                                        implements LaneActionIfc
{
    // This id is used to tell
    // the compiler not to generate a
    // new serialVersionUID.
    //
    static final long serialVersionUID = 568261729532507131L;



    public static final String LANENAME = "PayrollPayOutAmountErrorAisle";

    /**
     * traverse method.
     * @param bus the bus traversing this aisle.
     */
    public void traverse(BusIfc bus)
    {
        POSUIManagerIfc ui= (POSUIManagerIfc) bus.getManager(UIManagerIfc.TYPE);

        // Using "generic dialog bean".
        DialogBeanModel model = new DialogBeanModel();

        // Set model to same name as dialog in config\posUI.properties
        // Set button and arugments
        // set and display the model
        model.setResourceID("InvalidAmount");
        model.setType(DialogScreensIfc.ACKNOWLEDGEMENT);
        ui.showScreen(POSUIManagerIfc.DIALOG_TEMPLATE, model);
    }
}
