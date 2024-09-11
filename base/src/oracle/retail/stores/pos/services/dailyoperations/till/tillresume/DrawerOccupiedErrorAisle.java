/* ===========================================================================
* Copyright (c) 1998, 2012, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/dailyoperations/till/tillresume/DrawerOccupiedErrorAisle.java /main/11 2012/08/07 16:19:48 rabhawsa Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    rabhawsa  06/25/12 - wptg-merged keys TILL_OPEN_ERROR_LINE1 and 2 in
 *                         property file.
 *    cgreene   05/26/10 - convert to oracle packaging
 *    abondala  01/03/10 - update header date
 *
 * ===========================================================================
 * $Log:
 *    3    360Commerce 1.2         3/31/2005 4:27:51 PM   Robert Pearse   
 *    2    360Commerce 1.1         3/10/2005 10:21:10 AM  Robert Pearse   
 *    1    360Commerce 1.0         2/11/2005 12:10:43 PM  Robert Pearse   
 *
 *   Revision 1.5  2004/09/23 00:07:13  kmcbride
 *   @scr 7211: Adding static serialVersionUIDs to all POS Serializable objects, minus the JComponents
 *
 *   Revision 1.4  2004/02/16 14:41:24  blj
 *   @scr 3838 - cleanup code
 *
 *   Revision 1.3  2004/02/12 16:50:08  mcs
 *   Forcing head revision
 *
 *   Revision 1.2  2004/02/11 21:45:18  rhafernik
 *   @scr 0 Log4J conversion and code cleanup
 *
 *   Revision 1.1.1.1  2004/02/11 01:04:16  cschellenger
 *   updating to pvcs 360store-current
 *
 *
 * 
 *    Rev 1.0   Aug 29 2003 15:58:34   CSchellenger
 * Initial revision.
 * 
 *    Rev 1.0   Apr 29 2002 15:25:52   msg
 * Initial revision.
 * 
 *    Rev 1.1   25 Mar 2002 11:44:50   baa
 * split text for till drawer error
 * Resolution for POS SCR-1563: On longer dialog messages, the Enter button is not fully displayed.  Enter works.
 *
 *    Rev 1.0   Mar 18 2002 11:30:56   msg
 * Initial revision.
 *
 *    Rev 1.4   Mar 10 2002 18:00:20   mpm
 * Externalized text in dialog messages.
 * Resolution for POS SCR-351: Internationalization
 *
 *    Rev 1.3   29 Jan 2002 13:49:08   epd
 * another update to messages
 * Resolution for POS SCR-945: Resume a suspended till while another till is open.  Get wrong error
 *
 *    Rev 1.2   29 Jan 2002 12:30:26   epd
 * Updated with changes as approved from Issue SCR 529
 * Resolution for POS SCR-945: Resume a suspended till while another till is open.  Get wrong error
 *
 *    Rev 1.1   17 Jan 2002 11:29:40   jbp
 * added arg for dialog screen.
 * Resolution for POS SCR-715: Unable to login to Crossreach.
 *
 *    Rev 1.0   12 Nov 2001 10:09:26   epd
 * Initial revision.
 * Resolution for POS SCR-216: Making POS changes to accommodate OnlineOffice
 *
 * ===========================================================================
 */
package oracle.retail.stores.pos.services.dailyoperations.till.tillresume;

import oracle.retail.stores.pos.services.common.TillCargoIfc;
import oracle.retail.stores.domain.financial.AbstractStatusEntityIfc;
import oracle.retail.stores.domain.financial.RegisterIfc;
import oracle.retail.stores.domain.financial.TillIfc;
import oracle.retail.stores.foundation.manager.ifc.UIManagerIfc;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.foundation.tour.ifc.LaneActionIfc;
import oracle.retail.stores.pos.manager.ifc.UtilityManagerIfc;
import oracle.retail.stores.pos.services.PosLaneActionAdapter;
import oracle.retail.stores.pos.ui.DialogScreensIfc;
import oracle.retail.stores.pos.ui.POSUIManagerIfc;
import oracle.retail.stores.pos.ui.beans.DialogBeanModel;

//------------------------------------------------------------------------------
/**


    @version $Revision: /main/11 $
**/
//------------------------------------------------------------------------------

public class DrawerOccupiedErrorAisle extends PosLaneActionAdapter implements LaneActionIfc
{
    // This id is used to tell
    // the compiler not to generate a
    // new serialVersionUID.
    //
    static final long serialVersionUID = -1298181802350426845L;


    public static final String LANENAME = "DrawerOccupiedErrorAisle";


    //--------------------------------------------------------------------------
    /**



       @param bus the bus traversing this lane
    **/
    //--------------------------------------------------------------------------

    public void traverse(BusIfc bus)
    {

        POSUIManagerIfc ui =
          (POSUIManagerIfc) bus.getManager(UIManagerIfc.TYPE);
        UtilityManagerIfc utility =
          (UtilityManagerIfc) bus.getManager(UtilityManagerIfc.TYPE);


        TillResumeCargo cargo = (TillResumeCargo)bus.getCargo();
        RegisterIfc register = cargo.getRegister();
        TillIfc till = register.getCurrentTill();

        // initialize args for dialog screen.
        String[] args = new String[1];
        if (till.getStatus() == AbstractStatusEntityIfc.STATUS_OPEN)
        {
            args[0] = utility.retrieveDialogText(TillCargoIfc.TILL_OPEN_ERROR_TAG_LINE,
                    TillCargoIfc.TILL_OPEN_ERROR_LINE);
        }
        else
        // suspended
        {
            args[0] = utility.retrieveDialogText(TillCargoIfc.TILL_SUSPENDED_ERROR_TAG_LINE,
                    TillCargoIfc.TILL_SUSPENDED_ERROR_LINE);

        }

        DialogBeanModel model = new DialogBeanModel();
        model.setResourceID("NoCashDrawersError");
        model.setType(DialogScreensIfc.ERROR);
        model.setArgs(args);
        ui.showScreen(POSUIManagerIfc.DIALOG_TEMPLATE, model);


    }

}
