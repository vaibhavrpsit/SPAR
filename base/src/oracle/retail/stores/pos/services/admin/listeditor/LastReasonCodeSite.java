/* ===========================================================================
* Copyright (c) 1998, 2011, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/admin/listeditor/LastReasonCodeSite.java /rgbustores_13.4x_generic_branch/1 2011/05/05 14:06:04 mszekely Exp $
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
 *    3    360Commerce 1.2         3/31/2005 4:28:49 PM   Robert Pearse   
 *    2    360Commerce 1.1         3/10/2005 10:23:01 AM  Robert Pearse   
 *    1    360Commerce 1.0         2/11/2005 12:12:15 PM  Robert Pearse   
 *
 *   Revision 1.4  2004/09/23 00:07:13  kmcbride
 *   @scr 7211: Adding static serialVersionUIDs to all POS Serializable objects, minus the JComponents
 *
 *   Revision 1.3  2004/02/12 16:48:49  mcs
 *   Forcing head revision
 *
 *   Revision 1.2  2004/02/11 21:35:34  rhafernik
 *   @scr 0 Log4J conversion and code cleanup
 *
 *   Revision 1.1.1.1  2004/02/11 01:04:13  cschellenger
 *   updating to pvcs 360store-current
 *
 *
 * 
 *    Rev 1.0   Aug 29 2003 15:52:24   CSchellenger
 * Initial revision.
 * 
 *    Rev 1.0   Apr 29 2002 15:40:24   msg
 * Initial revision.
 * 
 *    Rev 1.1   Mar 18 2002 23:03:30   msg
 * - updated copyright
 * 
 *    Rev 1.0   Mar 18 2002 11:18:46   msg
 * Initial revision.
 * 
 *    Rev 1.1   20 Feb 2002 16:31:32   jbp
 * added dialog screen
 * Resolution for POS SCR-1338: Deleting Reason Code loses focus, selecting Delete again causes application to crash
 *
 *    Rev 1.0   Sep 21 2001 11:11:06   msg
 * Initial revision.
 *
 *    Rev 1.1   Sep 17 2001 13:05:26   msg
 * header update
 * ===========================================================================
 */
package oracle.retail.stores.pos.services.admin.listeditor;

import oracle.retail.stores.foundation.manager.ifc.UIManagerIfc;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.foundation.tour.ifc.SiteActionIfc;
import oracle.retail.stores.pos.services.PosSiteActionAdapter;
import oracle.retail.stores.pos.ui.DialogScreensIfc;
import oracle.retail.stores.pos.ui.POSUIManagerIfc;
import oracle.retail.stores.pos.ui.beans.DialogBeanModel;
//------------------------------------------------------------------------------
/**
   This site informs the user that the new value he attempted
   to give to the reason code is illegitimate.


    @version $Revision: /rgbustores_13.4x_generic_branch/1 $
**/
//------------------------------------------------------------------------------

public class LastReasonCodeSite
extends PosSiteActionAdapter
implements SiteActionIfc
{
    // This id is used to tell
    // the compiler not to generate a
    // new serialVersionUID.
    //
    static final long serialVersionUID = -1117387894793229728L;


    public static final String SITENAME = "LastReasonCodeSite";
    public static final String LAST_REASON_CODE = "DeleteLastReasonCodeValue";
    //--------------------------------------------------------------------------
    /**
       Inform the user that the new value he attempted
       to give to the reason code is illegitimate.
       @param bus the bus arriving at this site
    **/
    //--------------------------------------------------------------------------

    public void arrive(BusIfc bus)
    {

        POSUIManagerIfc ui
            = (POSUIManagerIfc)bus.getManager(UIManagerIfc.TYPE);
        DialogBeanModel model = new DialogBeanModel();
        model.setResourceID(LAST_REASON_CODE);
        model.setType(DialogScreensIfc.ACKNOWLEDGEMENT);
        ui.showScreen(POSUIManagerIfc.DIALOG_TEMPLATE, model);
    }

    //--------------------------------------------------------------------------
    /**
       Log the departure.
       @param bus the bus departing from this site
    **/
    //--------------------------------------------------------------------------

    public void depart(BusIfc bus)
    {
    }


    //--------------------------------------------------------------------------
    /**
       Log the undo.
       @param bus the bus undoing its actions
    **/
    //--------------------------------------------------------------------------

    public void undo(BusIfc bus)
    {
    }

    //--------------------------------------------------------------------------
    /**
       Log the reset.
       @param bus the bus being reset
    **/
    //--------------------------------------------------------------------------

    public void reset(BusIfc bus)
    {
    }

}
