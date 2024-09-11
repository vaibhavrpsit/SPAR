/* ===========================================================================
* Copyright (c) 2004, 2014, Oracle and/or its affiliates. All rights reserved.
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/modifytransaction/resume/RefreshSuspendedLineItemsSite.java /main/14 2014/07/14 13:41:15 jswan Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 * 	  Rev 1.1   Kamlesh Pant  Sep 22, 2022		CapLimit Enforcement for Liquor
 * 
 *    nsrao     11/18/14 - Added null pointer check to prevent Unexpected Exception.
 *                         Formatted conditions for better readability
 *    abananan  11/14/14 - Fix to prevent recalculation of price of a non retrieved
 *                         return of gift receipt item while resuming.
 *    cgreene   10/17/14 - Removed unneeded calls to clear discount rules
 *    jswan     07/14/14 - Modified to merge extended item data from retrieved
 *                         plu item into the line item from the original
 *                         transaction.
 *    jswan     06/10/14 - Modified to support retrieving extended data for
 *                         instore items.
 *    cgreene   05/14/14 - rename retrieve to resume
 *    abhinavs  08/08/13 - Fix to apply threshold discounts on suspended txns
 *    yiqzhao   08/08/13 - calculate item price based on retrievedPluItem.
 *    yiqzhao   08/06/13 - Reading pricing group id from promotion line item.
 *    yiqzhao   08/06/13 - Using pricing group id when retrieving suspended
 *                         transaction.
 *    yiqzhao   07/15/13 - Get items from local store or web store when
 *                         retrieving suspended transactions.
 *    mkutiana  05/07/13 - Adding the image information to the linitem
 *                         retrieved from the suspended transaction
 *    jswan     05/02/13 - Modified to prevent updating the price of external
 *                         order items.
 *    mchellap  03/25/13 - Do not reset selling price for priceEntryRequired
 *                         items
 *    jswan     03/13/13 - Fixed issue with deleting coupon item from the sale
 *                         item screen after retrieving a suspended
 *                         transaction. The item pricing was on completely
 *                         reconstructed.
 *    yiqzhao   02/18/13 - Add related item information for plu item when
 *                         retrieve the suspended txn.
 *    yiqzhao   01/04/13 - Refactoring ItemManager
 *    tksharma  12/17/12 - Clearance Return Price and Clearance
 *                         DiscountAmount/DiscountPercent fix
 *    jswan     09/13/12 - Added to support deprecation of JdbcPLUOperation.
 * ===========================================================================
 */
package oracle.retail.stores.pos.services.modifytransaction.resume;


import max.retail.stores.domain.MaxLiquorDetails;
import max.retail.stores.domain.arts.MAXDataTransactionKeys;
import max.retail.stores.domain.arts.MAXHotKeysTransaction;
import max.retail.stores.domain.lineitem.MAXSaleReturnLineItem;
import max.retail.stores.domain.transaction.MAXSaleReturnTransactionIfc;
import oracle.retail.stores.common.utility.Util;
import oracle.retail.stores.domain.DomainGateway;
import oracle.retail.stores.domain.arts.DataTransactionFactory;
import oracle.retail.stores.domain.arts.PLURequestor;
import oracle.retail.stores.domain.lineitem.AbstractTransactionLineItemIfc;
import oracle.retail.stores.domain.lineitem.ItemContainerProxyIfc;
import oracle.retail.stores.domain.lineitem.SaleReturnLineItemIfc;
import oracle.retail.stores.domain.manager.item.ItemManagerIfc;
import oracle.retail.stores.domain.stock.ItemClassificationConstantsIfc;
import oracle.retail.stores.domain.stock.PLUItemIfc;
import oracle.retail.stores.domain.transaction.RetailTransactionIfc;
import oracle.retail.stores.domain.transaction.SaleReturnTransactionIfc;
import oracle.retail.stores.domain.transaction.SearchCriteriaIfc;
import oracle.retail.stores.foundation.manager.data.DataException;
import oracle.retail.stores.foundation.tour.application.Letter;
import oracle.retail.stores.foundation.tour.gate.Gateway;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.pos.manager.ifc.UtilityManagerIfc;
import oracle.retail.stores.pos.services.PosSiteActionAdapter;
import oracle.retail.stores.pos.services.common.CommonLetterIfc;

/**
 * Refreshes each line item with current information from the item
 * repository.
 */
@SuppressWarnings("serial")
public class RefreshSuspendedLineItemsSite extends PosSiteActionAdapter
{
    /**
     * Constant for application property group
     */
    public static final String APPLICATION_PROPERTY_GROUP_NAME = "application";

    /**
     * Constant for cross channel enabled.
     */
    public static final String XCHANNEL_ENABLED = "XChannelEnabled";

    /**
     * Refreshes each line item with current information from the item
     * repository.
     * @param bus the bus arriving at this site
     */
    public void arrive(BusIfc bus)
    {
        // Initialize method variables
        ModifyTransactionResumeCargo cargo = (ModifyTransactionResumeCargo) bus.getCargo();
        RetailTransactionIfc transaction = cargo.getTransaction();
        
        // If this transaction can have line items
        if (transaction instanceof MAXSaleReturnTransactionIfc)
        {
            // Get the item container proxy and clone the line items.
            ItemContainerProxyIfc proxy = ((MAXSaleReturnTransactionIfc)transaction).getItemContainerProxy();
            AbstractTransactionLineItemIfc[] lineItems = proxy.getLineItems();
            
            // Iterate through the list of line items
            for(AbstractTransactionLineItemIfc lineItem: lineItems)
            {
                // If the current line item is a sale or return
                if (lineItem instanceof SaleReturnLineItemIfc)
                {
                    // Retrieve the item and update line item
                    SaleReturnLineItemIfc srli = (SaleReturnLineItemIfc)lineItem;
                    if (Util.isEmpty(srli.getPLUItem().getStoreID()))
                    {
                        srli.getPLUItem().setStoreID(transaction.getTransactionIdentifier().getStoreID());
                    }
                    PLUItemIfc retrievedPluItem = getItem(bus, srli);
                    if (retrievedPluItem != null)
                    {
                        mergePluWithLineItem(cargo, retrievedPluItem, srli, proxy);
                    }
                    
                    //Rev 1.1 : Starts
                    
                    PLUItemIfc pluItem = srli.getPLUItem();
                    MAXSaleReturnTransactionIfc transaction1 = (MAXSaleReturnTransactionIfc)cargo.getTransaction();
                    MaxLiquorDetails liquorDetail = null;
                    MAXHotKeysTransaction hotKeysTransaction = (MAXHotKeysTransaction) DataTransactionFactory
                 		   .create(MAXDataTransactionKeys.MAX_HOT_KEYS_LOOKUP_TRANSACTION);
                    try {
                 	   liquorDetail = ((MAXHotKeysTransaction) hotKeysTransaction)
                 			   .getLiquorUMAndCategory(pluItem.getItemID());
                    } 
                    catch (DataException e) {
                    	logger.warn(e.getMessage());
                    }
                    float beertot = 0;
					float InLiqtot = 0;
					float frnLiqtot = 0;
					float liquorTotal = 0;
					//System.out.println("transaction1.getLineItemsVector().size()::"+transaction1.getLineItemsVector().size());
                    if(liquorDetail.getDepartment()!=null && liquorDetail.getDepartment().equals("41"))
                    {
                    	//System.out.println("Inside Liquor");
                    	if(liquorDetail.getLiquorCategory().equalsIgnoreCase("BEER")){
                    		float value =  Float.parseFloat(liquorDetail.getLiqUMinLtr());
                    		float quantity = Float.parseFloat(srli.getItemQuantityDecimal().toString());
                    		beertot = beertot + value*quantity;
                    		transaction1.setBeertot(beertot);
                    	}
                    	else if(liquorDetail.getLiquorCategory().equalsIgnoreCase("INDN LIQR")){
                    		float value =  Float.parseFloat(liquorDetail.getLiqUMinLtr());
                    		float quantity = Float.parseFloat(srli.getItemQuantityDecimal().toString());
                    		InLiqtot = InLiqtot + value*quantity;
                    		transaction1.setInLiqtot(InLiqtot);
                    		//transaction1.setliquortot(transaction1.getliquortot());
                    	}
                    	else if(liquorDetail.getLiquorCategory().equalsIgnoreCase("FORN LIQR")){
                    		float value =  Float.parseFloat(liquorDetail.getLiqUMinLtr());
                    		float quantity = Float.parseFloat(srli.getItemQuantityDecimal().toString());
                    		frnLiqtot = value*quantity;
                    		transaction1.setfrnLiqtot(frnLiqtot);
                    	}
                    	transaction1.setliquortot(transaction1.getBeertot()+transaction1.getInLiqtot()+transaction1.getfrnLiqtot());
                    }
                   
                      }
              //Rev 1.1 : Ends
            }
            
            // Recalculate pricing
            ((SaleReturnTransactionIfc)transaction).calculateBestDeal();
        }
        
        bus.mail(new Letter(CommonLetterIfc.SUCCESS), BusIfc.CURRENT);
    }

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
    }

    /**
     * Gets the PLU Item from one of the item data repositories
     * 
     * @param bus
     * @param pluItem
     * @return the retrieved PLUItemIfc object; null if not found or error
     */
    protected PLUItemIfc getItem(BusIfc bus, SaleReturnLineItemIfc srli)
    {
        PLUItemIfc pluItem = srli.getPLUItem();
        // letter to be sent
        UtilityManagerIfc utility = (UtilityManagerIfc)bus.getManager(UtilityManagerIfc.TYPE);
        SearchCriteriaIfc inquiry = DomainGateway.getFactory().getSearchCriteriaInstance();
        ModifyTransactionResumeCargo cargo = (ModifyTransactionResumeCargo)bus.getCargo();
        PLUItemIfc retrievedPluItem = null;

        try
        {
             ItemManagerIfc mgr = (ItemManagerIfc) bus.getManager(ItemManagerIfc.TYPE);

             // get list of items matching search criteria from database
             inquiry.setLocaleRequestor(utility.getRequestLocales());
             // set storeID
             inquiry.setStoreNumber(pluItem.getStoreID());
             // Set POS item ID
             inquiry.setPosItemID(pluItem.getPosItemID());
             inquiry.setSearchItemByPosItemID(true);
             inquiry.setGeoCode(cargo.getStoreStatus().getStore().getGeoCode());
             inquiry.setRetrieveFromStore(true);
             inquiry.setPLURequestor(new PLURequestor());
             if (pluItem.getItemClassification().getItemType() == 
                 ItemClassificationConstantsIfc.TYPE_STORE_COUPON)
             {
                 inquiry.setLookupStoreCoupon(true);
             }
             boolean retrieveExtendedDataOnLocalPLULookup = Gateway.getBooleanProperty(
                     Gateway.APPLICATION_PROPERTIES_GROUP, "RetrieveExtendedDataOnLocalPLULookup", false);
             inquiry.setRetrieveExtendedDataOnLocalPLULookup(retrieveExtendedDataOnLocalPLULookup);
             int maxRecommendedItemsListSize = Integer.parseInt(Gateway.getProperty(Gateway.APPLICATION_PROPERTIES_GROUP, "MaxRecommendedItemsListSize", "100"));
             inquiry.setMaxRecommendedItemsListSize(maxRecommendedItemsListSize);

             retrievedPluItem = mgr.getPluItem(inquiry);
         }
         catch (DataException de)
         {
             boolean isXChannelEnabled = Gateway.getBooleanProperty(APPLICATION_PROPERTY_GROUP_NAME, XCHANNEL_ENABLED, false);
             if (isXChannelEnabled)
             {
                 try
                 {
                     // look for item information from web store.
                     ItemManagerIfc mgr = (ItemManagerIfc) bus.getManager(ItemManagerIfc.TYPE);
                     inquiry.setRetrieveFromStore(false);
                     retrievedPluItem = mgr.getPluItem(inquiry);
                 }
                 catch (DataException dez)
                 {
                     logger.info("Error reading Item: " + pluItem.getItemID() + ".", de);
                 }
             }
             else
             {
                 logger.info("Error reading Item: " + pluItem.getItemID() + ".", de);
             }
         }

         return retrievedPluItem;
     }
}