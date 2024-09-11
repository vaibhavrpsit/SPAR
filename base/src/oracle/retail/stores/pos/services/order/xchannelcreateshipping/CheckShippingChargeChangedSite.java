/*===========================================================================
* Copyright (c) 2012, 2013, Oracle and/or its affiliates. All rights reserved.
* ===========================================================================
* $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/order/xchannelcreateshipping/CheckShippingChargeChangedSite.java /main/2 2013/05/16 14:08:36 mkutiana Exp $
* ===========================================================================
* NOTES
* <other useful comments, qualifications, etc.>
*
* MODIFIED    (MM/DD/YY)
*    mkutiana  05/16/13 - Added the missing page header
*    mkutiana  05/16/13 - retaining the values of the ShippingBeanModel upon
*                         error on the SelectShippingMethodSite
*                         
* ===========================================================================
*/
package oracle.retail.stores.pos.services.order.xchannelcreateshipping;

import oracle.retail.stores.common.utility.Util;
import oracle.retail.stores.foundation.manager.ifc.UIManagerIfc;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.pos.services.PosSiteActionAdapter;
import oracle.retail.stores.pos.services.common.CommonLetterIfc;
import oracle.retail.stores.pos.ui.DialogScreensIfc;
import oracle.retail.stores.pos.ui.POSUIManagerIfc;
import oracle.retail.stores.pos.ui.beans.DialogBeanModel;
import oracle.retail.stores.pos.ui.beans.ShippingMethodBeanModel;

/**
 * Check the reason code has been specified or not if the shipping charge is overridden.
 * @author yiqzhao
 *
 */
public class CheckShippingChargeChangedSite extends PosSiteActionAdapter 
{
    
    /**
    class name constant
    **/
    public static final String SITENAME = "CheckShippingChargeChangedSite";
    /**
        revision number for this class
    **/
    public static final String revisionNumber = "$Revision: /main/2 $";
    
    
    //--------------------------------------------------------------------------
    /**
       <p>
       @param bus the bus arriving at this site
    **/
    //--------------------------------------------------------------------------
    public void arrive(BusIfc bus)
    {
        POSUIManagerIfc ui = (POSUIManagerIfc)bus.getManager(UIManagerIfc.TYPE);
        
        ShippingMethodBeanModel model = (ShippingMethodBeanModel)ui.getModel();

        XChannelShippingCargo shipCargo = (XChannelShippingCargo)bus.getCargo();
        
        if ( Util.isEmpty(model.getSelectedReason()) && !model.getShippingCharge().equals(model.getSelectedShipMethod().getCalculatedShippingCharge()) ) 
        {
            displayNoReasonCodeDialog(bus);
            
            //back to previous shipping option
            shipCargo.setCurrentOptionIndex(shipCargo.getCurrentOptionIndex()-1);
        }
        else
        {
            bus.mail(bus.getCurrentLetter().getName());
        }
    }
    
    protected void displayNoReasonCodeDialog(BusIfc bus)
    {
        POSUIManagerIfc ui = (POSUIManagerIfc)bus.getManager(UIManagerIfc.TYPE);

        DialogBeanModel model = new DialogBeanModel();
        model.setResourceID("ReasonCodeRequired");
        model.setType(DialogScreensIfc.ERROR);
        model.setButtonLetter(DialogScreensIfc.BUTTON_OK, CommonLetterIfc.NOT_FOUND);
        ui.showScreen(POSUIManagerIfc.DIALOG_TEMPLATE, model);  
    }
}
