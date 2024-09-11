/* ===========================================================================
* Copyright (c) 1998, 2011, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/servicealert/WebStoreLaunchShuttle.java /rgbustores_13.4x_generic_branch/1 2011/05/05 14:06:11 mszekely Exp $
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
 *    3    360Commerce 1.2         3/31/2005 4:30:48 PM   Robert Pearse   
 *    2    360Commerce 1.1         3/10/2005 10:26:49 AM  Robert Pearse   
 *    1    360Commerce 1.0         2/11/2005 12:15:36 PM  Robert Pearse   
 *
 *   Revision 1.4  2004/09/23 00:07:15  kmcbride
 *   @scr 7211: Adding static serialVersionUIDs to all POS Serializable objects, minus the JComponents
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
 *    Rev 1.0   Aug 29 2003 16:07:06   CSchellenger
 * Initial revision.
 * 
 *    Rev 1.0   Apr 29 2002 15:03:34   msg
 * Initial revision.
 * 
 *    Rev 1.0   Mar 18 2002 11:47:46   msg
 * Initial revision.
 * 
 *    Rev 1.0   Jan 10 2002 13:05:06   dfh
 * Initial revision.
 * Resolution for POS SCR-186: CR/Webstore, app hangs when unauth user enters Password screen
 *
 * ===========================================================================
 */
package oracle.retail.stores.pos.services.servicealert;

// Foundation imports
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.foundation.tour.ifc.ShuttleIfc;
import oracle.retail.stores.pos.services.browser.BrowserCargo;

//------------------------------------------------------------------------------
/**
    The Web Store Launch Shuttle carries the data required by the WebStore
    service, the operator, from the Service Alert service to the WebStore Service.

    @version $Revision: /rgbustores_13.4x_generic_branch/1 $
**/
//------------------------------------------------------------------------------
public class WebStoreLaunchShuttle implements ShuttleIfc
{
    // This id is used to tell
    // the compiler not to generate a
    // new serialVersionUID.
    //
    static final long serialVersionUID = 5879319424801921897L;

    /**
       class name constant
    **/
    public static final String SHUTTLENAME = "WebStoreLaunchShuttle";

    /**
       revision number for this class
    **/
    public static final String revisionNumber = "$Revision: /rgbustores_13.4x_generic_branch/1 $";

    // service alert cargo
    protected ServiceAlertCargo cargo = null;

    //--------------------------------------------------------------------------
    /**
       Load the data from the service alert cargo.
       <P>
       @param bus the bus being loaded
    **/
    //--------------------------------------------------------------------------
    public void load(BusIfc bus)
    {
        cargo = (ServiceAlertCargo) bus.getCargo();
    }

    //--------------------------------------------------------------------------
    /**
       Load the operator into the browser cargo.
       <P>
       @param bus the bus being unloaded
    **/
    //--------------------------------------------------------------------------
    public void unload(BusIfc bus)
    {
        BrowserCargo browserCargo = (BrowserCargo) bus.getCargo();

        // pass along operator
        browserCargo.setOperator(cargo.getOperator());
    }
}
