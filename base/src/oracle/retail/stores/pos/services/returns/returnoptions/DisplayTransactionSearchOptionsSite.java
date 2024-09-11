/* ===========================================================================
* Copyright (c) 2010, 2013, Oracle and/or its affiliates. All rights reserved.
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/returns/returnoptions/DisplayTransactionSearchOptionsSite.java /main/4 2013/04/26 15:30:45 abhinavs Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED (MM/DD/YY)
 *    abhina 04/26/13 - Fix to disable Order button in Xchannel enabled
 *                      environment
 *    cgreen 05/26/10 - convert to oracle packaging
 *    jswan  05/11/10 - Pre code reveiw clean up.
 * 
 * ===========================================================================
 */
package oracle.retail.stores.pos.services.returns.returnoptions;
// foundation imports
import oracle.retail.stores.foundation.manager.ifc.UIManagerIfc;
import oracle.retail.stores.foundation.tour.gate.Gateway;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.pos.services.PosSiteActionAdapter;
import oracle.retail.stores.pos.services.common.CommonActionsIfc;
import oracle.retail.stores.pos.ui.POSUIManagerIfc;
import oracle.retail.stores.pos.ui.beans.NavigationButtonBeanModel;
import oracle.retail.stores.pos.ui.beans.POSBaseBeanModel;

//------------------------------------------------------------------------------
/**
    This site displays the options available for searching for transactions.
**/
//--------------------------------------------------------------------------
public class DisplayTransactionSearchOptionsSite extends PosSiteActionAdapter
{ 
    /** serialVersionUID */
    private static final long serialVersionUID = 9103697407982388375L;
    
    /** site name constant **/
    public static final String SITENAME = "DisplayNoReceiptOptionsSite";
    
    private static final String APPLICATION_PROPERTY_GROUP_NAME = "application";
    private static final String XCHANNEL_ENABLED = "XChannelEnabled";

    //--------------------------------------------------------------------------
    /**
       Displays return transaction search options menu.
       @param bus the bus arriving at this site
    **/
    //--------------------------------------------------------------------------
    public void arrive(BusIfc bus)
    {
        // get ui reference and display screen
        POSUIManagerIfc ui = (POSUIManagerIfc) bus.getManager(UIManagerIfc.TYPE);
        boolean xchannelEnabled= Gateway.getBooleanProperty(APPLICATION_PROPERTY_GROUP_NAME, XCHANNEL_ENABLED, false);
        POSBaseBeanModel beanModel = new POSBaseBeanModel();
        NavigationButtonBeanModel nModel = new NavigationButtonBeanModel();
        nModel.setButtonEnabled(CommonActionsIfc.ORDER, xchannelEnabled);
        beanModel.setLocalButtonBeanModel(nModel);
        ui.showScreen(POSUIManagerIfc.RETURN_TRANSACTION_SEARCH, beanModel);
    }
}
