/* ===========================================================================
* Copyright (c) 1998, 2012, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/browser/BrowserControlSite.java /main/11 2012/10/29 16:37:49 vbongu Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    vbongu    10/29/12 - deprecating class
 *    cgreene   05/26/10 - convert to oracle packaging
 *    abondala  01/03/10 - update header date
 *
 * ===========================================================================
 * $Log:
 *    5    360Commerce 1.4         1/9/2008 9:26:03 PM    Anil Bondalapati
 *         removed the commented code related to removing the parameter
 *         Browser.
 *    4    360Commerce 1.3         12/18/2007 1:23:56 PM  Anil Bondalapati
 *         Removed the Browser group as it is not supported in the base
 *         product.
 *    3    360Commerce 1.2         3/31/2005 4:27:17 PM   Robert Pearse   
 *    2    360Commerce 1.1         3/10/2005 10:19:50 AM  Robert Pearse   
 *    1    360Commerce 1.0         2/11/2005 12:09:37 PM  Robert Pearse   
 *
 *   Revision 1.3  2004/02/12 16:49:05  mcs
 *   Forcing head revision
 *
 *   Revision 1.2  2004/02/11 21:38:35  rhafernik
 *   @scr 0 Log4J conversion and code cleanup
 *
 *   Revision 1.1.1.1  2004/02/11 01:04:14  cschellenger
 *   updating to pvcs 360store-current
 *
 *
 * 
 *    Rev 1.0   Aug 29 2003 15:54:00   CSchellenger
 * Initial revision.
 * 
 *    Rev 1.0   Apr 29 2002 15:36:20   msg
 * Initial revision.
 * 
 *    Rev 1.1   Mar 18 2002 23:08:02   msg
 * - updated copyright
 * 
 *    Rev 1.0   Mar 18 2002 11:21:52   msg
 * Initial revision.
 * 
 *    Rev 1.1   29 Oct 2001 12:39:58   jbp
 * Added Functionality to enable cookies based on parameter
 * Resolution for POS SCR-238: Update browser to enable cookies
 *
 *    Rev 1.0   Sep 21 2001 11:13:02   msg
 * Initial revision.
 *
 *    Rev 1.1   Sep 17 2001 13:05:50   msg
 * header update
 * ===========================================================================
 */
package oracle.retail.stores.pos.services.browser;

// java imports
import oracle.retail.stores.foundation.manager.ifc.UIManagerIfc;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.pos.services.PosSiteActionAdapter;
import oracle.retail.stores.pos.ui.POSUIManagerIfc;
import oracle.retail.stores.pos.ui.beans.BrowserBeanModel;

//--------------------------------------------------------------------------
/**
    Gets the homeUrl from the parameter manager and sets to the bean
    model if it is valid.. otherwise a default is used.
    <p>
    @version $Revision: /main/11 $
    @deprecated as of 14.0 Use {@link oracle.retail.stores.pos.services.browserfoundation.BrowserFoundationAppSite} instead.
**/
//--------------------------------------------------------------------------
public class BrowserControlSite extends PosSiteActionAdapter
{

    /**
       revision number supplied by Team Connection
    **/
    public static final String revisionNumber = "$Revision: /main/11 $";

    //----------------------------------------------------------------------
    /**
       Gets the homeUrl from the parameter manager.
       <P>
       <B>Pre-Condition(s)</B>
       <UL>
       <LI>
       </UL>
       <B>Post-Condition(s)</B>
       <UL>
       <LI>
       </UL>
       @param bus Service Bus
    **/
    //----------------------------------------------------------------------
    public void arrive(BusIfc bus)
    {
        // get the POS UI manager
        POSUIManagerIfc ui =
            (POSUIManagerIfc)bus.getManager(UIManagerIfc.TYPE);
        BrowserBeanModel model = new BrowserBeanModel();

        String url = null;
        boolean cookies = false;

        // update model and show screen
        model.setCookiesEnabled(cookies);
        model.setHomeUrl(url);
        ui.showScreen(POSUIManagerIfc.BROWSER_MAIN, model);
    }
}
