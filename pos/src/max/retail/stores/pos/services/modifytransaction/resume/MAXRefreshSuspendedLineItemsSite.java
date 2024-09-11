/********************************************************************************
 *   
 *	Copyright (c) 2016 - 2017 MAX Hypermarket, Inc    All Rights Reserved.
 *	Rev 1.1	    May 04, 2017	    Kritica Agarwal 	GST Changes
 *	Rev	1.0 	Aug 16, 2016		Nitesh Kumar	Changes for Properties assignment in MaxPLUItem	
 *
 ********************************************************************************/
package max.retail.stores.pos.services.modifytransaction.resume;


import max.retail.stores.domain.manager.item.MAXItemManagerIfc;
import max.retail.stores.domain.stock.MAXGiftCardPLUItem;
import max.retail.stores.domain.stock.MAXPLUItem;
import max.retail.stores.domain.transaction.MAXSaleReturnTransactionIfc;
import max.retail.stores.domain.transaction.MAXSearchCriteriaIfc;
import oracle.retail.stores.domain.DomainGateway;
import oracle.retail.stores.domain.arts.PLURequestor;
import oracle.retail.stores.domain.lineitem.ItemContainerProxyIfc;
import oracle.retail.stores.domain.lineitem.SaleReturnLineItemIfc;
import oracle.retail.stores.domain.manager.item.ItemManagerIfc;
import oracle.retail.stores.domain.stock.PLUItemIfc;
import oracle.retail.stores.domain.transaction.SearchCriteriaIfc;
import oracle.retail.stores.foundation.manager.data.DataException;
import oracle.retail.stores.foundation.tour.gate.Gateway;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.pos.manager.ifc.UtilityManagerIfc;
import oracle.retail.stores.pos.services.modifytransaction.resume.ModifyTransactionResumeCargo;
import oracle.retail.stores.pos.services.modifytransaction.resume.RefreshSuspendedLineItemsSite;

/**
 * Refreshes each line item with current information from the item
 * repository.
 */
@SuppressWarnings("serial")
public class MAXRefreshSuspendedLineItemsSite extends RefreshSuspendedLineItemsSite
{
    /**
     * Merge the data from the retrieved PLUItem into the existing line item.
     * @param cargo
     * @param retrievedPluItem
     * @param srli
     * @param proxy
     */
    protected void mergePluWithLineItem(ModifyTransactionResumeCargo cargo,
            PLUItemIfc retrievedPluItem, SaleReturnLineItemIfc srli,
            ItemContainerProxyIfc proxy)
    {
        // If the line item is a return and the price comes from a retrieved
        // transaction,or the line item is a non retrieved gift receipt item
        //don't monkey with the price.
        boolean replacePrice = true;
        if ((srli.isReturnLineItem() && srli.isFromTransaction()) || srli.hasExternalPricing()
                || (srli.getReturnItem() != null && srli.getReturnItem().haveReceipt() && srli.getReturnItem().getOriginalTransactionID() != null
                && srli.isGiftReceiptItem()))
        {
            replacePrice = false;
        }
        // If the pricing should be replaced, add the perm and temp 
        // prices to the pluItem, set the current price on the Item
        // Price object and recalculate the price.
        if (replacePrice)
        {
            srli.getPLUItem().setPermanentPriceChanges(
                    retrievedPluItem.getPermanentPriceChanges());
            srli.getPLUItem().setTemporaryPriceChangesAndTemporaryPriceChangesForReturns(
                    retrievedPluItem.getTemporaryPriceChanges());
            srli.getPLUItem().setClearancePriceChangesAndClearancePriceChangesForReturns(
                    retrievedPluItem.getClearancePriceChanges());
            srli.getPLUItem().addAdvancedPricingRules(retrievedPluItem.getAdvancedPricingRules());
            proxy.addAdvancedPricingRules(retrievedPluItem.getAdvancedPricingRules());
            if(srli.getPLUItem().getAdvancedPricingRules()!= null)
            {
                proxy.addItemAdvancedPricingRules(srli.getPLUItem());
            }
            // Do not reset the selling price if price is overridden or price is manually entered.
            if (!srli.getItemPrice().isPriceOverride() && !retrievedPluItem.getItemClassification().isPriceEntryRequired())
            {
                proxy.revaluateLineItemPrice(srli, retrievedPluItem);
                srli.calculateLineItemPrice();
            }
        }        
        
        // If there are localized descriptions available, set them on the 
        // embedded PLU Item.
        if (retrievedPluItem.getItem().getLocalizedDescriptions().size() > 0)
        {
            srli.getPLUItem().getItem().setLocalizedDescriptions(
                    retrievedPluItem.getItem().getLocalizedDescriptions());
        }
        
        // If there are localized descriptions available, set them on the 
        // embedded PLU Item.
        if (retrievedPluItem.getItem().getShortLocalizedDescriptions().size() > 0)
        {
            srli.getPLUItem().getItem().setShortLocalizedDescriptions(
                    retrievedPluItem.getItem().getShortLocalizedDescriptions());
        }
        
        // Add in the item messages, classification, tax rules and department.
        srli.getPLUItem().setAllItemLevelMessages(
                retrievedPluItem.getAllItemLevelMessages());
        srli.getPLUItem().setItemClassification(
                retrievedPluItem.getItemClassification());
        srli.getPLUItem().setDepartment(retrievedPluItem.getDepartment());
        srli.getPLUItem().setTaxRules(retrievedPluItem.getTaxRules());
        srli.getPLUItem().setRelatedItemContainer(retrievedPluItem.getRelatedItemContainer());
        
        if (retrievedPluItem.getExtendedItemDataContainer() != null)
        {
            srli.getPLUItem().setExtendedItemDataContainer(retrievedPluItem.getExtendedItemDataContainer());
            srli.getPLUItem().setExtendedImageOnItem(retrievedPluItem.getExtendedItemDataContainer());
        }
        
        //Add the item image
        srli.getPLUItem().setItemImage(retrievedPluItem.getItemImage());
        //changes for rev 1.0 starts
        if(srli.getPLUItem() instanceof MAXPLUItem){
	        ((MAXPLUItem)srli.getPLUItem()).setSubClass(((MAXPLUItem)retrievedPluItem).getSubClass());
	        ((MAXPLUItem)srli.getPLUItem()).setItemGroup(((MAXPLUItem)retrievedPluItem).getItemGroup());
	        ((MAXPLUItem)srli.getPLUItem()).setItemDivision(((MAXPLUItem)retrievedPluItem).getItemDivision());
	        ((MAXPLUItem)srli.getPLUItem()).setBrandName(((MAXPLUItem)retrievedPluItem).getBrandName());
	        ((MAXPLUItem)srli.getPLUItem()).setDepartmentID(((MAXPLUItem)retrievedPluItem).getDepartmentID());
	        ((MAXPLUItem)srli.getPLUItem()).setActiveMaximumRetailPriceChanges(((MAXPLUItem)retrievedPluItem).getActiveMaximumRetailPriceChanges());
	        ((MAXPLUItem)srli.getPLUItem()).setInActiveMaximumRetailPriceChanges(((MAXPLUItem)retrievedPluItem).getInActiveMaximumRetailPriceChanges());
	        ((MAXPLUItem)srli.getPLUItem()).setTaxAssignments(((MAXPLUItem)retrievedPluItem).getTaxAssignments());
	        ((MAXPLUItem)srli.getPLUItem()).setMultipleMaximumRetailPriceFlag(((MAXPLUItem)retrievedPluItem).getMultipleMaximumRetailPriceFlag());
	        // Changes for Rev 1.2	@Puru
	        //((MAXPLUItem)srli.getPLUItem()).setMaximumRetailPrice(((MAXPLUItem)retrievedPluItem).getMaximumRetailPrice());
	        ((MAXPLUItem)srli.getPLUItem()).setRetailLessThanMRPFlag(((MAXPLUItem)retrievedPluItem).getRetailLessThanMRPFlag());
	        ((MAXPLUItem)srli.getPLUItem()).setTaxCategory(((MAXPLUItem)retrievedPluItem).getTaxCategory());
	        ((MAXPLUItem)srli.getPLUItem()).setItemGroups(((MAXPLUItem)retrievedPluItem).getItemGroups());
	        ((MAXPLUItem)srli.getPLUItem()).setInvoiceDiscounts(((MAXPLUItem)retrievedPluItem).getInvoiceDiscounts());
	        ((MAXPLUItem)srli.getPLUItem()).setSubClass(((MAXPLUItem)retrievedPluItem).getSubClass());
	        ((MAXPLUItem)srli.getPLUItem()).setItemSizeDesc(((MAXPLUItem)retrievedPluItem).getItemSizeDesc());
        }
        //changes for rev 1.0 ends
	     else if(srli.getPLUItem() instanceof MAXGiftCardPLUItem){
	    	 ((MAXGiftCardPLUItem)srli.getPLUItem()).setSubClass(((MAXGiftCardPLUItem)retrievedPluItem).getSubClass());
        ((MAXGiftCardPLUItem)srli.getPLUItem()).setItemGroup(((MAXGiftCardPLUItem)retrievedPluItem).getItemGroup());
        ((MAXGiftCardPLUItem)srli.getPLUItem()).setItemDivision(((MAXGiftCardPLUItem)retrievedPluItem).getItemDivision());
        ((MAXGiftCardPLUItem)srli.getPLUItem()).setBrandName(((MAXGiftCardPLUItem)retrievedPluItem).getBrandName());
        ((MAXGiftCardPLUItem)srli.getPLUItem()).setDepartmentID(((MAXGiftCardPLUItem)retrievedPluItem).getDepartmentID());
        //((MAXGiftCardPLUItem)srli.getPLUItem()).setActiveMaximumRetailPriceChanges(((MAXGiftCardPLUItem)retrievedPluItem).getActiveMaximumRetailPriceChanges());
        //((MAXGiftCardPLUItem)srli.getPLUItem()).setInActiveMaximumRetailPriceChanges(((MAXGiftCardPLUItem)retrievedPluItem).getInActiveMaximumRetailPriceChanges());
        ((MAXGiftCardPLUItem)srli.getPLUItem()).setTaxAssignments(((MAXGiftCardPLUItem)retrievedPluItem).getTaxAssignments());
        ((MAXGiftCardPLUItem)srli.getPLUItem()).setMultipleMaximumRetailPriceFlag(((MAXGiftCardPLUItem)retrievedPluItem).getMultipleMaximumRetailPriceFlag());
        ((MAXGiftCardPLUItem)srli.getPLUItem()).setMaximumRetailPrice(((MAXGiftCardPLUItem)retrievedPluItem).getMaximumRetailPrice());
        ((MAXGiftCardPLUItem)srli.getPLUItem()).setRetailLessThanMRPFlag(((MAXGiftCardPLUItem)retrievedPluItem).getRetailLessThanMRPFlag());
        ((MAXGiftCardPLUItem)srli.getPLUItem()).setTaxCategory(((MAXGiftCardPLUItem)retrievedPluItem).getTaxCategory());
        ((MAXGiftCardPLUItem)srli.getPLUItem()).setItemGroups(((MAXGiftCardPLUItem)retrievedPluItem).getItemGroups());
        ((MAXGiftCardPLUItem)srli.getPLUItem()).setInvoiceDiscounts(((MAXGiftCardPLUItem)retrievedPluItem).getInvoiceDiscounts());
        ((MAXGiftCardPLUItem)srli.getPLUItem()).setSubClass(((MAXGiftCardPLUItem)retrievedPluItem).getSubClass());
        ((MAXGiftCardPLUItem)srli.getPLUItem()).setItemSizeDesc(((MAXGiftCardPLUItem)retrievedPluItem).getItemSizeDesc());
	     } 	 
    }
    
    protected PLUItemIfc getItem(BusIfc bus, SaleReturnLineItemIfc srli) {
		PLUItemIfc pluItem = srli.getPLUItem();

		UtilityManagerIfc utility = (UtilityManagerIfc) bus.getManager("UtilityManager");
		MAXSearchCriteriaIfc inquiry = (MAXSearchCriteriaIfc) DomainGateway.getFactory().getSearchCriteriaInstance();
		ModifyTransactionResumeCargo cargo = (ModifyTransactionResumeCargo) bus.getCargo();
		PLUItemIfc retrievedPluItem = null;
		try {
			MAXItemManagerIfc mgr = (MAXItemManagerIfc) bus.getManager("ItemManager");

			inquiry.setLocaleRequestor(utility.getRequestLocales());

			inquiry.setStoreNumber(pluItem.getStoreID());

			inquiry.setPosItemID(pluItem.getPosItemID());
			inquiry.setSearchItemByPosItemID(true);
			inquiry.setGeoCode(cargo.getStoreStatus().getStore().getGeoCode());
			inquiry.setRetrieveFromStore(true);
			inquiry.setPLURequestor(new PLURequestor());
			if (pluItem.getItemClassification().getItemType() == 3) {
				inquiry.setLookupStoreCoupon(true);
			}
			boolean retrieveExtendedDataOnLocalPLULookup = Gateway.getBooleanProperty("application",
					"RetrieveExtendedDataOnLocalPLULookup", false);

			inquiry.setRetrieveExtendedDataOnLocalPLULookup(retrieveExtendedDataOnLocalPLULookup);
			int maxRecommendedItemsListSize = Integer
					.parseInt(Gateway.getProperty("application", "MaxRecommendedItemsListSize", "100"));
			inquiry.setMaxRecommendedItemsListSize(maxRecommendedItemsListSize);
			//Change for Rev 1.1 : Starts
            if(cargo.getTransaction() instanceof MAXSaleReturnTransactionIfc && ((MAXSaleReturnTransactionIfc)cargo.getTransaction()).isIgstApplicable()){
            	((MAXSearchCriteriaIfc)inquiry).setInterStateDelivery(((MAXSaleReturnTransactionIfc)cargo.getTransaction()).isIgstApplicable());
            	if(((MAXSaleReturnTransactionIfc)cargo.getTransaction()).isIgstApplicable()){
            		((MAXSearchCriteriaIfc)inquiry).setFromRegion(((MAXSaleReturnTransactionIfc)cargo.getTransaction()).getHomeState());
            		((MAXSearchCriteriaIfc)inquiry).setToRegion(((MAXSaleReturnTransactionIfc)cargo.getTransaction()).getToState());
            	}
            }
          //Change for Rev 1.1 : Ends
			retrievedPluItem = mgr.getPluItem(inquiry);
		} catch (DataException de) {
			boolean isXChannelEnabled = Gateway.getBooleanProperty("application", "XChannelEnabled", false);
			if (isXChannelEnabled) {
				try {
					ItemManagerIfc mgr = (ItemManagerIfc) bus.getManager("ItemManager");
					inquiry.setRetrieveFromStore(false);
					retrievedPluItem = mgr.getPluItem(inquiry);
				} catch (DataException dez) {
					logger.info("Error reading Item: " + pluItem.getItemID() + ".", de);
				}

			} else {
				logger.info("Error reading Item: " + pluItem.getItemID() + ".", de);
			}
		}

		return retrievedPluItem;
	}
    
}