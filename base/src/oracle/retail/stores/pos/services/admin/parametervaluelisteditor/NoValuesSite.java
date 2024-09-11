/* ===========================================================================
* Copyright (c) 1998, 2011, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/admin/parametervaluelisteditor/NoValuesSite.java /rgbustores_13.4x_generic_branch/1 2011/05/05 14:06:04 mszekely Exp $
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
 *    3    360Commerce 1.2         3/31/2005 4:29:10 PM   Robert Pearse   
 *    2    360Commerce 1.1         3/10/2005 10:23:44 AM  Robert Pearse   
 *    1    360Commerce 1.0         2/11/2005 12:12:47 PM  Robert Pearse   
 *
 *   Revision 1.5  2004/09/23 00:07:12  kmcbride
 *   @scr 7211: Adding static serialVersionUIDs to all POS Serializable objects, minus the JComponents
 *
 *   Revision 1.4  2004/05/20 13:30:04  dfierling
 *   @scr 4066 - changed dialog flag from ACKNOWLEDGEMENT to ERROR
 *
 *   Revision 1.3  2004/02/12 16:48:51  mcs
 *   Forcing head revision
 *
 *   Revision 1.2  2004/02/11 21:36:26  rhafernik
 *   @scr 0 Log4J conversion and code cleanup
 *
 *   Revision 1.1.1.1  2004/02/11 01:04:13  cschellenger
 *   updating to pvcs 360store-current
 *
 *
 * 
 *    Rev 1.0   Aug 29 2003 15:53:06   CSchellenger
 * Initial revision.
 * 
 *    Rev 1.0   Apr 29 2002 15:38:44   msg
 * Initial revision.
 * 
 *    Rev 1.1   Mar 18 2002 23:05:56   msg
 * - updated copyright
 * 
 *    Rev 1.0   Mar 18 2002 11:20:12   msg
 * Initial revision.
 * 
 *    Rev 1.0   10 Feb 2002 14:29:02   KAC
 * Initial revision.
 * Resolution for POS SCR-1226: Update list parameter value editor per new requirements
 * ===========================================================================
 */
package oracle.retail.stores.pos.services.admin.parametervaluelisteditor;

import oracle.retail.stores.foundation.manager.ifc.UIManagerIfc;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.foundation.tour.ifc.SiteActionIfc;
import oracle.retail.stores.pos.services.PosSiteActionAdapter;
import oracle.retail.stores.pos.ui.DialogScreensIfc;
import oracle.retail.stores.pos.ui.POSUIManagerIfc;
import oracle.retail.stores.pos.ui.beans.DialogBeanModel;


//------------------------------------------------------------------------------
/**
   This site informs the user that he must have at least one parameter value.
    @version $Revision: /rgbustores_13.4x_generic_branch/1 $
**/
//------------------------------------------------------------------------------

public class NoValuesSite
extends PosSiteActionAdapter
implements SiteActionIfc
{
    // This id is used to tell
    // the compiler not to generate a
    // new serialVersionUID.
    //
    static final long serialVersionUID = -8828898615148915075L;


    public static final String SITENAME = "NoValuesSite";

    //--------------------------------------------------------------------------
    /**
       This site informs the user that he must have at least one parameter
           value.
       @param bus the bus arriving at this site
    **/
    //--------------------------------------------------------------------------

    public void arrive(BusIfc bus)
    {
        POSUIManagerIfc ui
            = (POSUIManagerIfc)bus.getManager(UIManagerIfc.TYPE);
        DialogBeanModel model = new DialogBeanModel();
        model.setResourceID("NoParameterValues");
        model.setType(DialogScreensIfc.ERROR);
        ui.setModel(POSUIManagerIfc.DIALOG_TEMPLATE, model);
        ui.showScreen(POSUIManagerIfc.DIALOG_TEMPLATE);
    }

    //--------------------------------------------------------------------------
    /**
       Do nothing.
       @param bus the bus departing from this site
    **/
    //--------------------------------------------------------------------------

    public void depart(BusIfc bus)
    {
    }


    //--------------------------------------------------------------------------
    /**
       Do nothing.
       @param bus the bus undoing its actions
    **/
    //--------------------------------------------------------------------------

    public void undo(BusIfc bus)
    {
    }

    //--------------------------------------------------------------------------
    /**
       Do nothing.
       @param bus the bus being reset
    **/
    //--------------------------------------------------------------------------

    public void reset(BusIfc bus)
    {
    }

}
