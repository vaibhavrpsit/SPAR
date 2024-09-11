package oracle.retail.stores.pos.services.send.displaysendmethod;

import org.apache.commons.lang3.StringUtils;

import oracle.retail.stores.foundation.manager.ifc.UIManagerIfc;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.pos.services.PosSiteActionAdapter;
import oracle.retail.stores.pos.services.common.CommonLetterIfc;
import oracle.retail.stores.pos.ui.DialogScreensIfc;
import oracle.retail.stores.pos.ui.POSUIManagerIfc;
import oracle.retail.stores.pos.ui.beans.DialogBeanModel;
import oracle.retail.stores.pos.ui.beans.ShippingMethodBeanModel;

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
        
        if ( StringUtils.isBlank(model.getSelectedReason()) && !model.getShippingCharge().equals(model.getSelectedShipMethod().getCalculatedShippingCharge()) ) 
        {
            displayNoReasonCodeDialog(bus);
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
