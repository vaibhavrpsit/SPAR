/* ===========================================================================
* Copyright (c) 2012, 2013, Oracle and/or its affiliates. All rights reserved.
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/order/xchannelcreatepickup/RetrieveAvailableToPromiseInventorySite.java /main/5 2013/09/18 11:11:20 abhinavs Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    abhinavs  09/18/13 - Fix to remove decimal and trailing zeros from the
 *                         qty
 *    abhinavs  08/24/13 - Xchannel Inventory lookup enhancement phase I
 *    ohorne    05/23/12 - removed AvailableToPromiseInventoryException
 *    jswan     04/18/12 - Added to support cross channel create pickup order
 *                         feature.
 * ===========================================================================
 */
package oracle.retail.stores.pos.services.order.xchannelcreatepickup;

// foundation imports
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import oracle.retail.stores.domain.DomainGateway;
import oracle.retail.stores.domain.inventoryinquiry.promise.AvailableToPromiseInventoryIfc;
import oracle.retail.stores.domain.inventoryinquiry.promise.AvailableToPromiseInventorySearchCriteriaIfc;
import oracle.retail.stores.domain.lineitem.SaleReturnLineItemIfc;
import oracle.retail.stores.domain.manager.inventory.AvailableToPromiseInventoryManagerIfc;
import oracle.retail.stores.domain.store.StoreIfc;
import oracle.retail.stores.foundation.manager.data.DataException;
import oracle.retail.stores.foundation.manager.ifc.UIManagerIfc;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.pos.services.PosSiteActionAdapter;
import oracle.retail.stores.pos.services.common.CommonLetterIfc;
import oracle.retail.stores.pos.ui.DialogScreensIfc;
import oracle.retail.stores.pos.ui.POSUIManagerIfc;
import oracle.retail.stores.pos.ui.beans.DialogBeanModel;

//------------------------------------------------------------------------------
/**
    This class gets inventory information for the current item.
    @version $Revision: /main/5 $
**/
//------------------------------------------------------------------------------
@SuppressWarnings("serial")
public class RetrieveAvailableToPromiseInventorySite extends PosSiteActionAdapter
{
    /**
        class name constant
    **/
    public static final String SITENAME = "RetrieveAvailableForPromiseInventorySite";
    /**
        revision number for this class
    **/
    public static final String revisionNumber = "$Revision: /main/5 $";

    //--------------------------------------------------------------------------
    /**
        This method gets inventory information for the current item.
       <p>
       @param bus the bus arriving at this site
    **/
    //--------------------------------------------------------------------------
    public void arrive(BusIfc bus)
    {
        XChannelCreatePickupOrderCargo cargo = (XChannelCreatePickupOrderCargo)bus.getCargo();
        
        // Construct StoreInventorySearchCriteriaIfc from line item details.
        SaleReturnLineItemIfc item = cargo.getLineItemsBucket().get(cargo.lineItemIndex).getItemBucket().get(0);
        AvailableToPromiseInventorySearchCriteriaIfc searchCriteria = 
            DomainGateway.getFactory().getAvailableToPromiseInventorySearchCriteriaInstance();
        searchCriteria.setItemID(item.getItemID());
        if(cargo.getLineItemsBucket().get(cargo.lineItemIndex).getMultiQuantityBundled())
        {
            searchCriteria.setQuantity( cargo.getLineItemsBucket().get(cargo.lineItemIndex).getItemBucket().get(0).getItemQuantityDecimal().setScale(0,BigDecimal.ROUND_DOWN)); 
        }
        else
        {
            if(cargo.getLineItemsBucket().get(cargo.lineItemIndex).getItemBucket().size()==1)
            {
                searchCriteria.setQuantity(cargo.getLineItemsBucket().get(cargo.lineItemIndex).getItemBucket().get(0).getItemQuantityDecimal().setScale(0,BigDecimal.ROUND_DOWN));
            }
            else
            {
                searchCriteria.setQuantity(new BigDecimal(cargo.getLineItemsBucket().get(cargo.lineItemIndex).getItemBucket().size()));
            }
        }
        List<String> storeIDs = new ArrayList<String>();
        StoreIfc[] storeList = cargo.getStoreGroup();
        for (StoreIfc store: storeList)
        {
            storeIDs.add(store.getStoreID());
        }
        searchCriteria.setStoreIDs(storeIDs);

        AvailableToPromiseInventoryManagerIfc em = 
            (AvailableToPromiseInventoryManagerIfc)bus.getManager
                (AvailableToPromiseInventoryManagerIfc.TYPE);

        try
        {
            List<AvailableToPromiseInventoryIfc> itemAvailablityList = em.query(searchCriteria);
            cargo.setItemAvailablityList(itemAvailablityList);
            bus.mail(CommonLetterIfc.CONTINUE, BusIfc.CURRENT);
        }
        catch (DataException e)
        {
            logger.info("The available inventory system is not available.", e);
            dialogForAvailableInventoryError(bus);
        }

    }
    /**
     * Display error dialog
     * @param bus Service bus.
     */
    protected void dialogForAvailableInventoryError(BusIfc bus)
    {
        DialogBeanModel model = new DialogBeanModel();
        model.setResourceID("OmsOrInvOffline");
        model.setType(DialogScreensIfc.ACKNOWLEDGEMENT);
        model.setButtonLetter(DialogScreensIfc.BUTTON_OK, CommonLetterIfc.ERROR);
        POSUIManagerIfc ui = (POSUIManagerIfc)bus.getManager(UIManagerIfc.TYPE);
        ui.showScreen(POSUIManagerIfc.DIALOG_TEMPLATE, model);
    }
    
}
