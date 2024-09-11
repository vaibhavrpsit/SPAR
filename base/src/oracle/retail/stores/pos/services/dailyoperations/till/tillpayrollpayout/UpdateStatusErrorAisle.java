/* ===========================================================================
* Copyright (c) 1998, 2011, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/dailyoperations/till/tillpayrollpayout/UpdateStatusErrorAisle.java /rgbustores_13.4x_generic_branch/1 2011/05/05 14:06:19 mszekely Exp $
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
 *    4    360Commerce 1.3         1/25/2006 4:11:54 PM   Brett J. Larsen merge
 *          7.1.1 changes (aka. 7.0.3 fixes) into 360Commerce view
 *    3    360Commerce 1.2         3/31/2005 4:30:40 PM   Robert Pearse   
 *    2    360Commerce 1.1         3/10/2005 10:26:36 AM  Robert Pearse   
 *    1    360Commerce 1.0         2/11/2005 12:15:26 PM  Robert Pearse   
 *:
 *    4    .v700     1.2.1.0     11/17/2005 16:39:31    Jason L. DeLeau 4345:
 *         Replace any uses of Gateway.log() with the log4j.
 *    3    360Commerce1.2         3/31/2005 15:30:40     Robert Pearse
 *    2    360Commerce1.1         3/10/2005 10:26:36     Robert Pearse
 *    1    360Commerce1.0         2/11/2005 12:15:26     Robert Pearse
 *
 *   Revision 1.2  2004/09/23 00:07:17  kmcbride
 *   @scr 7211: Adding static serialVersionUIDs to all POS Serializable objects, minus the JComponents
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
 *   Revision 1.1.1.1  2004/02/11 01:04:16  cschellenger
 *   updating to pvcs 360store-current
 *
 *
 *
 *    Rev 1.0   Aug 29 2003 15:58:18   CSchellenger
 * Initial revision.
 *
 *    Rev 1.0   Apr 29 2002 15:26:48   msg
 * Initial revision.
 *
 *    Rev 1.0   Mar 18 2002 11:30:26   msg
 * Initial revision.
 *
 *    Rev 1.0   Sep 21 2001 11:19:36   msg
 * Initial revision.
 *
 *    Rev 1.1   Sep 17 2001 13:14:54   msg
 * header update
 * ===========================================================================
 */
package oracle.retail.stores.pos.services.dailyoperations.till.tillpayrollpayout;

// Foundation imports
import oracle.retail.stores.foundation.manager.ifc.UIManagerIfc;
import oracle.retail.stores.foundation.tour.application.LaneActionAdapter;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.foundation.tour.ifc.LaneActionIfc;
import oracle.retail.stores.pos.ui.DialogScreensIfc;
import oracle.retail.stores.pos.ui.POSUIManagerIfc;
import oracle.retail.stores.pos.ui.beans.DialogBeanModel;

/**
 *
 * @author khassen
 *
 * Displays an error dialog if a database error occured
 * during an update.
 */
public class UpdateStatusErrorAisle extends LaneActionAdapter implements LaneActionIfc
{
    // This id is used to tell
    // the compiler not to generate a
    // new serialVersionUID.
    //
    static final long serialVersionUID = 7218592821419776919L;

    public static final String LANENAME = "UpdateStatusErrorAisle";

    /**
     * traverse method
     *
     * @param bus the bus traversing.
     */
    public void traverse(BusIfc bus)
    {
    	if (logger.isDebugEnabled()) logger.debug(LANENAME + ".traverse starting...");

        POSUIManagerIfc ui;
        ui = (POSUIManagerIfc) bus.getManager(UIManagerIfc.TYPE);

        String args[] = new String[1];
        args[0] = "Loan";

        DialogBeanModel model = new DialogBeanModel();
        model.setResourceID("TillUpdateDatabaseError");
        model.setType(DialogScreensIfc.ERROR);
        model.setArgs(args);
        ui.showScreen(POSUIManagerIfc.DIALOG_TEMPLATE, model);

        if (logger.isDebugEnabled()) logger.debug(LANENAME + ".traverse ending...");
    }

    /**
     * backup method()
     *
     * @param bus
     */
    public void backup(BusIfc bus)
    {
    	if (logger.isDebugEnabled()) logger.debug(LANENAME + ".backup starting...");



    	if (logger.isDebugEnabled()) logger.debug(LANENAME + ".backup ending...");

    }

}
