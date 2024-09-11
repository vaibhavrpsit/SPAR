/* ===========================================================================
* Copyright (c) 1998, 2013, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/manager/registerreports/ReportsOptionsSite.java /main/12 2013/01/15 18:46:32 sgu Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    npoola    12/20/10 - action button texts are moved to CommonActionsIfc
 *    cgreene   05/26/10 - convert to oracle packaging
 *    abondala  01/03/10 - update header date
 *
 * ===========================================================================
 * $Log:
 *    3    360Commerce 1.2         3/31/2005 4:29:39 PM   Robert Pearse   
 *    2    360Commerce 1.1         3/10/2005 10:24:42 AM  Robert Pearse   
 *    1    360Commerce 1.0         2/11/2005 12:13:43 PM  Robert Pearse   
 *
 *   Revision 1.4  2004/07/13 15:50:50  jdeleau
 *   @scr 5601 Remove F12 (Cancel) for register reports
 *
 *   Revision 1.3  2004/02/12 16:50:59  mcs
 *   Forcing head revision
 *
 *   Revision 1.2  2004/02/11 21:51:46  rhafernik
 *   @scr 0 Log4J conversion and code cleanup
 *
 *   Revision 1.1.1.1  2004/02/11 01:04:17  cschellenger
 *   updating to pvcs 360store-current
 *
 *
 * 
 *    Rev 1.0   Aug 29 2003 16:01:22   CSchellenger
 * Initial revision.
 * 
 *    Rev 1.0   Apr 29 2002 15:19:08   msg
 * Initial revision.
 * 
 *    Rev 1.0   Mar 18 2002 11:36:40   msg
 * Initial revision.
 * 
 *    Rev 1.1   Jan 10 2002 21:52:50   dfh
 * added order summary and order status reports
 * Resolution for POS SCR-260: Special Order feature for release 5.0
 * 
 *    Rev 1.0   Sep 21 2001 11:24:12   msg
 * Initial revision.
 * 
 *    Rev 1.1   Sep 17 2001 13:12:10   msg
 * header update
 * ===========================================================================
 */
package oracle.retail.stores.pos.services.manager.registerreports;

// Foundation imports
import oracle.retail.stores.foundation.manager.ifc.UIManagerIfc;
import oracle.retail.stores.foundation.tour.gate.Gateway;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.pos.services.PosSiteActionAdapter;
import oracle.retail.stores.pos.ui.POSUIManagerIfc;
import oracle.retail.stores.pos.ui.beans.NavigationButtonBeanModel;
import oracle.retail.stores.pos.services.common.CommonActionsIfc;
import oracle.retail.stores.pos.ui.beans.POSBaseBeanModel;

//------------------------------------------------------------------------------
/**
   Display the Report Options.

    @version $Revision: /main/12 $
**/
//------------------------------------------------------------------------------
public class ReportsOptionsSite extends PosSiteActionAdapter
{

    public static final String SITENAME = "ReportsOptionsSite";
    
    private static final String APPLICATION_PROPERTY_GROUP_NAME = "application";
    private static final String XCHANNEL_ENABLED = "XChannelEnabled";

    //--------------------------------------------------------------------------
    /**
       Put up the report options screen.

       @param bus the bus arriving at this site
    **/
    //--------------------------------------------------------------------------
    public void arrive(BusIfc bus)
    {
        NavigationButtonBeanModel globalNavigationModel = new NavigationButtonBeanModel();
        globalNavigationModel.setButtonEnabled(CommonActionsIfc.CANCEL, false);
        POSUIManagerIfc ui = (POSUIManagerIfc) bus.getManager(UIManagerIfc.TYPE);
        POSBaseBeanModel model = new POSBaseBeanModel();
        model.setGlobalButtonBeanModel(globalNavigationModel);

        if (isXChannelEnabled())
        {
            ui.showScreen(POSUIManagerIfc.XC_REPORT_OPTIONS, model);
        }
        else
        {
            ui.showScreen(POSUIManagerIfc.CR_REPORT_OPTIONS, model);
        }
    }
    
    /**
     * @return a flag indicating if cross channel is enabled.
     */
    protected boolean isXChannelEnabled()
    {
        return Gateway.getBooleanProperty(APPLICATION_PROPERTY_GROUP_NAME, XCHANNEL_ENABLED, false);
    }
}
