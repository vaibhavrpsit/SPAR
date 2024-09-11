/* ===========================================================================
* Copyright (c) 2012, 2013, Oracle and/or its affiliates. All rights reserved.
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/order/xchannelcreatepickup/SelectStoreForPickupSite.java /main/9 2013/09/18 17:26:17 abhinavs Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    abhinavs  09/18/13 - Fix to empty the lineitem bucket on undo action
 *    abhinavs  08/25/13 - Xchannel Inventory lookup enhancement phase I
 *    yiqzhao   08/13/13 - Enable/Disable button status at the site.
 *    yiqzhao   08/12/13 - Remove StoreListBeanModel.
 *    yiqzhao   08/12/13 - Enable or disable Pickup button after loading
 *                         available inventory line item.
 *    abhinavs  06/04/13 - Fix to update item attributes on undo action of
 *                         xchannel pickup
 *    abhinavs  03/07/13 - Fix to display correct pickup date on the basis of
 *                         item availability
 *    abhinavs  02/28/13 - Fix to enable and disable pickup buttons on qty and
 *                         date availability
 *    sgu       01/11/13 - set xchannel order item flag based on if item is
 *                         avilable in the store inventory
 *    jswan     05/02/12 - Added to support cross channel create pickup order
 *                         feature.
 *    jswan     04/18/12 - Added to support cross channel create pickup order
 *                         feature.
 * ===========================================================================
 */
package oracle.retail.stores.pos.services.order.xchannelcreatepickup;

import java.math.BigDecimal;
import java.text.DateFormat;
import java.util.List;
import java.util.Vector;

import org.apache.commons.lang3.StringUtils;

import oracle.retail.stores.commerceservices.common.datetime.DateTimeServiceIfc;
import oracle.retail.stores.commerceservices.common.datetime.DateTimeServiceLocator;
import oracle.retail.stores.common.utility.LocaleMap;
import oracle.retail.stores.domain.inventoryinquiry.promise.AvailableToPromiseInventoryIfc;
import oracle.retail.stores.domain.store.StoreIfc;
import oracle.retail.stores.domain.utility.EYSDate;
import oracle.retail.stores.domain.utility.LocaleConstantsIfc;
import oracle.retail.stores.foundation.manager.ifc.UIManagerIfc;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.pos.config.bundles.BundleConstantsIfc;
import oracle.retail.stores.pos.manager.ifc.UtilityManagerIfc;
import oracle.retail.stores.pos.services.PosSiteActionAdapter;
import oracle.retail.stores.pos.services.common.CommonActionsIfc;
import oracle.retail.stores.pos.ui.POSUIManagerIfc;
import oracle.retail.stores.pos.ui.beans.AvailableToPromiseInventoryLineItemModel;
import oracle.retail.stores.pos.ui.beans.ListBeanModel;
import oracle.retail.stores.pos.ui.beans.NavigationButtonBeanModel;
import oracle.retail.stores.pos.ui.beans.PromptAndResponseModel;

//--------------------------------------------------------------------------
/**
    This site calls the UI manager to list the available inventory for a 
    single item for each store in the list. 
    @version $Revision: /main/9 $
 **/
//--------------------------------------------------------------------------
@SuppressWarnings("serial")
public class SelectStoreForPickupSite extends PosSiteActionAdapter
{
	//----------------------------------------------------------------------
	/**
        This method calls the UI manager to list the available inventory for a 
        single item for each store in the list.
        @param  bus     Service Bus
	 **/
	//----------------------------------------------------------------------
	public static final String AVAILABLE_DATE_TODAY_TAG = "Today";
	public static final String AVAILABLE_DATE_TODAY_TEXT = "Today";

	public void arrive(BusIfc bus)
	{
		XChannelCreatePickupOrderCargo cargo = (XChannelCreatePickupOrderCargo)bus.getCargo(); 
		ListBeanModel model = getListModel(bus);

		// Set the item number and description on the prompt and response model
		PromptAndResponseModel responseModel = new PromptAndResponseModel();
		String[] args = new String[2];
		//responseModel.setArguments(args);
		args[0] =  cargo.getLineItemsBucket().get(cargo.lineItemIndex).getItemBucket().get(0).getItemID();
		args[1] =  cargo.getLineItemsBucket().get(cargo.lineItemIndex).getItemBucket().get(0).getItemDescription(LocaleMap.getLocale(LocaleConstantsIfc.USER_INTERFACE));
        responseModel.setArguments(args);
		// Set the prompt and response model list bean model
		model.setPromptAndResponseModel(responseModel);
		
		NavigationButtonBeanModel localModel = getLocalButtonBeanModel(model);
		model.setLocalButtonBeanModel(localModel);

		POSUIManagerIfc uiManager = (POSUIManagerIfc)bus.getManager(UIManagerIfc.TYPE);
		uiManager.showScreen(POSUIManagerIfc.XC_PICKUP_STORE_SELECT, model);
	}

	/**
	 * Create the ListBeanModel from the list of store and available item inventory
	 * @param bus
	 * @return
	 */
	@SuppressWarnings ("unchecked")
	protected ListBeanModel getListModel(BusIfc bus)
	{
		XChannelCreatePickupOrderCargo cargo = (XChannelCreatePickupOrderCargo)bus.getCargo();

		StoreIfc[] storeList = cargo.getStoreGroup();
		List<AvailableToPromiseInventoryIfc> iaList = cargo.getItemAvailablityList();
        ListBeanModel model = new ListBeanModel();
        
		Vector modelList = new Vector();
		BigDecimal quantityRequired = new BigDecimal(cargo.getLineItemsBucket().get(cargo.lineItemIndex).getItemBucket().size());
		for(StoreIfc store: storeList)
		{
			AvailableToPromiseInventoryIfc itemAvailablity = 
					cargo.getStoreItemAvailablity(store.getStoreID(), iaList);
			AvailableToPromiseInventoryLineItemModel afpm = 
					getStoreInventoryLineItemModel(store, itemAvailablity, bus,quantityRequired);
			
			modelList.addElement(afpm);
			
			if ( store.equals(storeList[0]) )
			{
			    model.setSelectedValue(afpm);
			}		    
		}

	      // Add the list of store available inventory to the ListBeanModel
        model.setListModel(modelList);
        
		return model;
	}

	/**
	 * Convert the store and itemAvailablity objects into
	 * a StoreInventoryLineItemModel
	 * @param store
	 * @param itemAvailablity
	 * @param bus
	 * @return StoreInventoryLineItemModel
	 */
	private AvailableToPromiseInventoryLineItemModel getStoreInventoryLineItemModel(
			StoreIfc store, AvailableToPromiseInventoryIfc itemAvailablity, BusIfc bus, BigDecimal quantityRequired)
	{
		AvailableToPromiseInventoryLineItemModel afpm = new AvailableToPromiseInventoryLineItemModel();

		afpm.setStoreID(store.getStoreID());
		afpm.setStoreName(store.getLocationName(
				LocaleMap.getLocale(LocaleConstantsIfc.USER_INTERFACE)));
		afpm.setAddress(store.getAddress());
		afpm.setStorePhone(store.getPrimaryPhone());

		afpm.setQuantityAvailable("");
		afpm.setDateAvailable("");
		afpm.setQuantityRequired(quantityRequired);
		EYSDate currentDate;
		if (itemAvailablity != null)
		{
			currentDate = new EYSDate(EYSDate.TYPE_DATE_ONLY);
			if (itemAvailablity.getAvailableQty() != null)
			{
				afpm.setQuantityAvailable(itemAvailablity.getAvailableQty().toPlainString());
			}

			// If the available date is equal to today's date, display "Today"
			// rather than the date.
			if(!StringUtils.isEmpty(afpm.getQuantityAvailable()) && new BigDecimal(afpm.getQuantityAvailable()).compareTo(afpm.getQuantityRequired()) >= 0)
			{
				
					// If availableDate is today or in the past, the item inventory is 
					// considered to be available as of now. 
					// The UI simply shows TODAY as the available date.
					UtilityManagerIfc utility =
							(UtilityManagerIfc) bus.getManager(UtilityManagerIfc.TYPE);
					String today =
							utility.retrieveText(BundleConstantsIfc.COMMON,
									BundleConstantsIfc.ORDER_BUNDLE_NAME,
									AVAILABLE_DATE_TODAY_TAG,
									AVAILABLE_DATE_TODAY_TEXT,
									LocaleMap.getLocale(LocaleConstantsIfc.USER_INTERFACE));
					afpm.setDateAvailable(today);
					afpm.setAvailableNow(true);
					afpm.setAvailableInFuture(false);
					itemAvailablity.setDate(currentDate);

			}
			else if(itemAvailablity.getDate() != null)
			{
				EYSDate availableDate = itemAvailablity.getDate();
			    availableDate.initialize(availableDate.getYear(), availableDate.getMonth(), 
						availableDate.getDay(), EYSDate.TYPE_DATE_ONLY);
				if(currentDate.before(availableDate))
				{
					DateTimeServiceIfc dateTimeService = DateTimeServiceLocator.getDateTimeService();
					afpm.setDateAvailable(dateTimeService.formatDate(availableDate.dateValue(), 
							LocaleMap.getLocale(LocaleMap.DEFAULT), DateFormat.SHORT));
					afpm.setAvailableNow(false);
					afpm.setAvailableInFuture(true);
				}
			}

			else
			{
				afpm.setAvailableNow(false);
				afpm.setAvailableInFuture(false);
			}

		}

		return afpm;
	}
	
	/**
	 * return local button bean model which has all the button status set
	 * @param model
	 * @return
	 */
	protected NavigationButtonBeanModel getLocalButtonBeanModel(ListBeanModel model)
	{
	    NavigationButtonBeanModel localButtonBeanModel = new NavigationButtonBeanModel();
	    
	    localButtonBeanModel.setButtonEnabled(CommonActionsIfc.PICKUP, false);
        localButtonBeanModel.setButtonEnabled(CommonActionsIfc.STORE_SEARCH, false);	    
        localButtonBeanModel.setButtonEnabled(CommonActionsIfc.PRINT, false);	
        
        if ( model.getListArray().length>0 )
        {
            localButtonBeanModel.setButtonEnabled(CommonActionsIfc.STORE_SEARCH, true);        
            localButtonBeanModel.setButtonEnabled(CommonActionsIfc.PRINT, true);   
        }
        
        AvailableToPromiseInventoryLineItemModel lineItem = (AvailableToPromiseInventoryLineItemModel)model.getSelectedValue();
        if ( lineItem != null )
        {
            String avaliable = lineItem.getQuantityAvailable();
            try
            {
                Integer availableQuantity = Integer.valueOf(avaliable);
                if (availableQuantity.intValue()>0)
                {
                    localButtonBeanModel.setButtonEnabled(CommonActionsIfc.PICKUP, true);
                }
            } catch (NumberFormatException e)
            {
                logger.error("Available Quantity: Unable to format " + avaliable + " to Integer");
            }
        }
        
	    return localButtonBeanModel;
	}
	
	 /**
     * Sets the XchannelCreatePickupExecuted boolean flag to true or false.
     *
     * @param bus Service Bus
     */
    @Override
    public void depart(BusIfc bus)
    {
    	String letterName = bus.getCurrentLetter().getName();
    	if (letterName.equals("Undo"))
    	{
    		XChannelCreatePickupOrderCargo cargo = (XChannelCreatePickupOrderCargo) bus.getCargo();
    		cargo.setXchannelCreatePickupExecuted(false);
    		cargo.getLineItemsBucket().clear();
    	}
    }
}