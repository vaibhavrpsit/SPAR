/* ===========================================================================
* Copyright (c) 2012, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/order/xchannelcreatepickup/PrintAvailableToPromiseInventorySite.java /main/1 2012/05/02 14:07:49 jswan Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    jswan     05/01/12 - Added to support the cross channel feature create
 *                         pickup order.
 *    jswan     04/29/12 - Added to support cross channel create pickup order
 *                         feature.
 * ===========================================================================
 */
package oracle.retail.stores.pos.services.order.xchannelcreatepickup;

// foundation imports
import java.util.ArrayList;
import java.util.List;

import oracle.retail.stores.domain.DomainGateway;
import oracle.retail.stores.domain.inventoryinquiry.promise.AvailableToPromiseInventoryIfc;
import oracle.retail.stores.domain.inventoryinquiry.promise.StoreItemAvailableToPromiseInventoryIfc;
import oracle.retail.stores.domain.store.StoreIfc;
import oracle.retail.stores.foundation.manager.ifc.UIManagerIfc;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.foundation.tour.service.SessionBusIfc;
import oracle.retail.stores.pos.receipt.AvailableToPromiseInventorySlip;
import oracle.retail.stores.pos.receipt.PrintableDocumentException;
import oracle.retail.stores.pos.receipt.PrintableDocumentManagerIfc;
import oracle.retail.stores.pos.services.PosSiteActionAdapter;
import oracle.retail.stores.pos.services.common.CommonLetterIfc;
import oracle.retail.stores.pos.ui.DialogScreensIfc;
import oracle.retail.stores.pos.ui.POSUIManagerIfc;
import oracle.retail.stores.pos.ui.beans.DialogBeanModel;

//------------------------------------------------------------------------------
/**
    This class prints available for promise inventory.
    @version $Revision: /main/1 $
**/
//------------------------------------------------------------------------------
@SuppressWarnings("serial")
public class PrintAvailableToPromiseInventorySite extends PosSiteActionAdapter
{
    /**
        class name constant
    **/
    public static final String SITENAME = "PrintAvailableForPromiseInventorySite";
    /**
        revision number for this class
    **/
    public static final String revisionNumber = "$Revision: /main/1 $";

    //--------------------------------------------------------------------------
    /**
        This method prints available for promise inventory.
       <p>
       @param bus the bus arriving at this site
    **/
    //--------------------------------------------------------------------------
    public void arrive(BusIfc bus)
    {
        POSUIManagerIfc ui = (POSUIManagerIfc) bus.getManager(UIManagerIfc.TYPE);
        XChannelCreatePickupOrderCargo cargo = (XChannelCreatePickupOrderCargo) bus.getCargo();
        try
        {
            ui.statusChanged(POSUIManagerIfc.PRINTER_STATUS, POSUIManagerIfc.ONLINE);

            PrintableDocumentManagerIfc pdm = (PrintableDocumentManagerIfc)bus.getManager(PrintableDocumentManagerIfc.TYPE);
            AvailableToPromiseInventorySlip slip = new AvailableToPromiseInventorySlip();

            slip.setRegister(cargo.getRegister());
            slip.setCashier(cargo.getOperator());
            slip.setItem(cargo.getItem());

            ArrayList<StoreItemAvailableToPromiseInventoryIfc> sialist = 
                new ArrayList<StoreItemAvailableToPromiseInventoryIfc>();
            List<AvailableToPromiseInventoryIfc> ialist = cargo.getItemAvailablityList();
            StoreIfc[] storeGroups = cargo.getStoreGroup();
            for (StoreIfc store : storeGroups)
            {
                StoreItemAvailableToPromiseInventoryIfc sia = 
                    DomainGateway.getFactory().getStoreItemAvailableToPromiseInventoryInstance();
                sia.setStore(store);
                sia.setAvailableToPromiseInventory(cargo.getStoreItemAvailablity(store.getStoreID(), ialist));
                sialist.add(sia);
            }
            
            StoreItemAvailableToPromiseInventoryIfc[] aiaArray = 
                sialist.toArray(new StoreItemAvailableToPromiseInventoryIfc[sialist.size()]);
            slip.setStoreItemAvailableToPromiseInventoryList(aiaArray);
            pdm.printReceipt((SessionBusIfc)bus, slip);
            
            bus.mail(CommonLetterIfc.NEXT, BusIfc.CURRENT);
        }
        catch (PrintableDocumentException de)
        {
            logger.warn(bus.getServiceName() + 
                    " : Unable to print Available Inventory slip: ", de);
            dialogForPrinterError(bus);
        }
    }
    
    /**
     * Display error dialog
     * @param bus Service bus.
     */
    protected void dialogForPrinterError(BusIfc bus)
    {
        POSUIManagerIfc ui = (POSUIManagerIfc)bus.getManager(UIManagerIfc.TYPE);
        ui.statusChanged(POSUIManagerIfc.PRINTER_STATUS, POSUIManagerIfc.ONLINE);

        DialogBeanModel model = new DialogBeanModel();
        model.setResourceID("PrintErrorNoRetry");
        model.setType(DialogScreensIfc.ACKNOWLEDGEMENT);
        model.setButtonLetter(DialogScreensIfc.BUTTON_OK, CommonLetterIfc.ERROR);
        ui.showScreen(POSUIManagerIfc.DIALOG_TEMPLATE, model);
    }
    
}
