/* ===========================================================================
* Copyright (c) 1998, 2013, Oracle and/or its affiliates. All rights reserved.
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/dailyoperations/till/tilloptions/DisplayTillFunctionsSite.java /main/11 2013/04/17 14:42:36 subrdey Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    subrdey   04/10/13 - Checking the Till if it fails.
 *    cgreene   05/26/10 - convert to oracle packaging
 *    abondala  01/03/10 - update header date
 *
 * ===========================================================================
 * $Log:
 *    5    360Commerce 1.4         2/26/2008 12:18:52 AM  Manikandan Chellapan
 *         CR#30629 Fixed Till Functions timeout 
 *    4    360Commerce 1.3         1/25/2006 4:10:58 PM   Brett J. Larsen merge
 *          7.1.1 changes (aka. 7.0.3 fixes) into 360Commerce view
 *    3    360Commerce 1.2         3/31/2005 4:27:50 PM   Robert Pearse   
 *    2    360Commerce 1.1         3/10/2005 10:21:06 AM  Robert Pearse   
 *    1    360Commerce 1.0         2/11/2005 12:10:41 PM  Robert Pearse   
 *:
 *    4    .v700     1.2.1.0     11/17/2005 16:39:32    Jason L. DeLeau 4345:
 *         Replace any uses of Gateway.log() with the log4j.
 *    3    360Commerce1.2         3/31/2005 15:27:50     Robert Pearse
 *    2    360Commerce1.1         3/10/2005 10:21:06     Robert Pearse
 *    1    360Commerce1.0         2/11/2005 12:10:41     Robert Pearse
 *
 *   Revision 1.4  2004/04/15 18:57:00  dcobb
 *   @scr 4205 Feature Enhancement: Till Options
 *   Till reconcile service is now separate from till close.
 *
 *   Revision 1.3  2004/02/12 16:50:02  mcs
 *   Forcing head revision
 *
 *   Revision 1.2  2004/02/11 21:47:34  rhafernik
 *   @scr 0 Log4J conversion and code cleanup
 *
 *   Revision 1.1.1.1  2004/02/11 01:04:15  cschellenger
 *   updating to pvcs 360store-current
 *
 *
 *
 *    Rev 1.0   Aug 29 2003 15:58:06   CSchellenger
 * Initial revision.
 *
 *    Rev 1.0   Apr 29 2002 15:27:04   msg
 * Initial revision.
 *
 *    Rev 1.0   Mar 18 2002 11:29:54   msg
 * Initial revision.
 *
 *    Rev 1.0   Sep 21 2001 11:19:06   msg
 * Initial revision.
 *
 *    Rev 1.1   Sep 17 2001 13:14:48   msg
 * header update
 * ===========================================================================
 */
package oracle.retail.stores.pos.services.dailyoperations.till.tilloptions;

// foundation imports
import oracle.retail.stores.foundation.manager.ifc.UIManagerIfc;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.pos.services.PosSiteActionAdapter;
import oracle.retail.stores.pos.ui.POSUIManagerIfc;
import oracle.retail.stores.pos.ui.beans.POSBaseBeanModel;
import oracle.retail.stores.foundation.tour.application.Letter;

//--------------------------------------------------------------------------
/**
    This site directs the user to the proper till service based upon
    selection from the Till Functions screen.
    <p>
    @version $Revision: /main/11 $
**/
//--------------------------------------------------------------------------
public class DisplayTillFunctionsSite extends PosSiteActionAdapter
{
    /**
        revision number supplied by Team Connection
    **/
    public static final String revisionNumber = "$Revision: /main/11 $";

    //--------------------------------------------------------------------------
    /**
        TillFunctionsSite
    **/
    //--------------------------------------------------------------------------
    public static final String SITENAME = "DisplayTillFunctionsSite";

    //----------------------------------------------------------------------
    /**
        <P>
        @param  bus     Service Bus
    **/
    //----------------------------------------------------------------------
    public void arrive(BusIfc bus)
    {
        if (logger.isDebugEnabled())
            logger.debug(SITENAME + ".arrive starting...");

        // show screen
        POSUIManagerIfc ui = (POSUIManagerIfc)bus.getManager(UIManagerIfc.TYPE);
        POSBaseBeanModel model = (POSBaseBeanModel)ui.getModel(POSUIManagerIfc.TILL_FUNCTIONS);
        ui.showScreen(POSUIManagerIfc.TILL_FUNCTIONS, model);

        if (logger.isDebugEnabled())
            logger.debug(SITENAME + ".arrive ending...");
    }
}
