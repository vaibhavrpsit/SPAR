/*===========================================================================
* Copyright (c) 2012, 2013, Oracle and/or its affiliates. All rights reserved.
* ===========================================================================
* $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/inquiry/iteminquiry/DisplayRelatedItemSite.java /main/2 2013/09/05 10:36:16 abondala Exp $
* ===========================================================================
* NOTES
* <other useful comments, qualifications, etc.>
*
* MODIFIED    (MM/DD/YY)
* abondala    09/04/13 - initialize collections
* yiqzhao     11/08/12 - Using different sites to display different types of
*                        related items.
* yiqzhao     11/07/12 - Display related items for item look up.
* yiqzhao     10/25/12 - Creation
* ===========================================================================
*/


package oracle.retail.stores.pos.services.inquiry.iteminquiry;


import java.util.HashMap;
import java.util.Locale;

import oracle.retail.stores.common.item.ItemSearchResult;
import oracle.retail.stores.common.item.RelatedItemSearchResult;
import oracle.retail.stores.common.utility.LocaleMap;
import oracle.retail.stores.domain.DomainGateway;
import oracle.retail.stores.domain.stock.RelatedItemGroupIfc;
import oracle.retail.stores.domain.stock.RelatedItemIfc;
import oracle.retail.stores.domain.stock.RelatedItemSummary;
import oracle.retail.stores.domain.stock.RelatedItemSummaryIfc;
import oracle.retail.stores.domain.utility.CodeConstantsIfc;
import oracle.retail.stores.domain.utility.CodeEntryIfc;
import oracle.retail.stores.domain.utility.CodeListIfc;
import oracle.retail.stores.domain.utility.LocaleConstantsIfc;
import oracle.retail.stores.foundation.manager.gui.UIModelIfc;
import oracle.retail.stores.foundation.manager.ifc.UIManagerIfc;

import oracle.retail.stores.foundation.tour.gate.Gateway;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.foundation.tour.ifc.LetterIfc;
import oracle.retail.stores.pos.manager.ifc.UtilityManagerIfc;
import oracle.retail.stores.pos.services.PosSiteActionAdapter;
import oracle.retail.stores.pos.ui.POSUIManagerIfc;
import oracle.retail.stores.pos.ui.beans.NavigationButtonBeanModel;
import oracle.retail.stores.pos.ui.beans.PromptAndResponseModel;
import oracle.retail.stores.pos.ui.beans.RelatedItemListBeanModel;
import oracle.retail.stores.pos.ui.beans.SearchItemListBeanModel;
//--------------------------------------------------------------------------
/**
     This site is to display related item option screen.
     $Revision: /main/2 $
 **/
//--------------------------------------------------------------------------
public abstract class DisplayRelatedItemSite extends PosSiteActionAdapter
{
    
    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	//----------------------------------------------------------------------
    /**
        This method create related item list bean model which is used by its subclasses.
        @param bus
    **/
    //----------------------------------------------------------------------
    public RelatedItemListBeanModel getRelatedItemListBeanModel(BusIfc bus)
    {
    	LetterIfc letter = bus.getCurrentLetter();
    	
    	POSUIManagerIfc     ui      = (POSUIManagerIfc) bus.getManager(UIManagerIfc.TYPE);
    	ItemInquiryCargo    cargo   = (ItemInquiryCargo)bus.getCargo();
    	
    	ItemSearchResult itemSearchResult = null;
    	
    	if (cargo.getItem()!=null)
    	{
    		//previous screen should be ShowItemSite
    		itemSearchResult = cargo.getItem();
    	}
    	else
    	{
    		//previous screen should be ShowItemListSite
	    	UIModelIfc	 model = ui.getModel(POSUIManagerIfc.ITEMS_LIST);
	    	if ( model instanceof SearchItemListBeanModel)
	    	{
	    		itemSearchResult = ((SearchItemListBeanModel)model).getSelectedItem();
	    	}
    	}
    	
    	RelatedItemGroupIfc	relatedItemGroup = DomainGateway.getFactory().getRelatedItemGroupInstance();
    	for (RelatedItemSearchResult relatedItemSearchResult: itemSearchResult.getRelatedItemSearchResult())
    	{
    		if ( relatedItemSearchResult.getRelatedItemTypeCode().equals(letter.getName()) )
    		{
	    		RelatedItemIfc relatedItem = DomainGateway.getFactory().getRelatedItemInstance();
	    		RelatedItemSummaryIfc relatedItemSummary = DomainGateway.getFactory().getRelatedItemSummaryInstance();
	    		relatedItemSummary.setItemID(relatedItemSearchResult.getItemID());
	    		relatedItemSummary.setDepartmentID(relatedItemSearchResult.getDepartmentID());
	    		relatedItemSummary.setDescription(LocaleMap.getLocale(LocaleConstantsIfc.USER_INTERFACE), relatedItemSearchResult.getItemDescription());
	    		relatedItemSummary.setPrice(DomainGateway.getBaseCurrencyInstance(relatedItemSearchResult.getPrice()));
	    		relatedItem.setRelatedItemSummary(relatedItemSummary);
	    		
    			relatedItemGroup.addRelatedItem(relatedItem);    			
    		}
    	}
    	
    	// add default to department list hash tables
        // retrieve department code list from reason codes.
        UtilityManagerIfc utility = (UtilityManagerIfc)  Gateway.getDispatcher().getManager(UtilityManagerIfc.TYPE);
        CodeListIfc deptMap=utility.getReasonCodes(cargo.getStoreStatus().getStore().getStoreID(),CodeConstantsIfc.CODE_LIST_DEPARTMENT);
        Locale locale = LocaleMap.getLocale(LocaleConstantsIfc.USER_INTERFACE);
        // retrieve department entries
        CodeEntryIfc[] deptEntries = deptMap.getEntries();
        HashMap<String, String> map = new HashMap<String, String>(0);
        for(int i=0; i < deptEntries.length; i++)
        {
            map.put(deptEntries[i].getCode(), deptEntries[i].getText(locale));
        }
        RelatedItemSummary.setDepartmentHash(map);
        
        // update bean model with matching items list
        RelatedItemListBeanModel relatedModel = new RelatedItemListBeanModel();
        PromptAndResponseModel responseModel = new PromptAndResponseModel();
        relatedModel.setPromptAndResponseModel(responseModel);
        
        if ( relatedItemGroup != null )
        {
        	RelatedItemIfc[] relatedItems = relatedItemGroup.getRelatedItems();
	        RelatedItemSummaryIfc relateItemSummaries[] = new RelatedItemSummaryIfc[relatedItems.length];
	        
	        for (int i = 0; i < relatedItems.length; i++)
	        {
	        	relateItemSummaries[i] = relatedItems[i].getRelatedItemSummary();
	        }	
	        	        
	        String[] args = new String[2];
	        args[0] = cargo.getPLUItem().getPosItemID();
	        args[1] = cargo.getPLUItem().getDescription(LocaleMap.getLocale(LocaleConstantsIfc.USER_INTERFACE));
	        responseModel.setArguments(args);
	        relatedModel.setItemList(relateItemSummaries);
	    }

        NavigationButtonBeanModel localNavigationModel = new NavigationButtonBeanModel();;
        localNavigationModel.setButtonEnabled(RelatedItemGroupIfc.AUTOMATIC, false);
        localNavigationModel.setButtonEnabled(RelatedItemGroupIfc.CROSS_SELL, false);
        localNavigationModel.setButtonEnabled(RelatedItemGroupIfc.UPSELL, false);
        localNavigationModel.setButtonEnabled(RelatedItemGroupIfc.SUBSTITUTE, false);
    	for (RelatedItemSearchResult relatedItemSearchResult: itemSearchResult.getRelatedItemSearchResult())
    	{
    		if (!relatedItemSearchResult.getRelatedItemTypeCode().equals(letter.getName()))
    		{
	    		if (relatedItemSearchResult.getRelatedItemTypeCode().equals(RelatedItemGroupIfc.AUTOMATIC))
	    			localNavigationModel.setButtonEnabled(RelatedItemGroupIfc.AUTOMATIC, true);
	    		else if (relatedItemSearchResult.getRelatedItemTypeCode().equals(RelatedItemGroupIfc.CROSS_SELL))
	    			localNavigationModel.setButtonEnabled(RelatedItemGroupIfc.CROSS_SELL, true);
	    		else if (relatedItemSearchResult.getRelatedItemTypeCode().equals(RelatedItemGroupIfc.UPSELL))
	    			localNavigationModel.setButtonEnabled(RelatedItemGroupIfc.UPSELL, true);   	
	    		else if (relatedItemSearchResult.getRelatedItemTypeCode().equals(RelatedItemGroupIfc.SUBSTITUTE))
	    			localNavigationModel.setButtonEnabled(RelatedItemGroupIfc.SUBSTITUTE, true);    
    		}
    	}
    	relatedModel.setLocalButtonBeanModel(localNavigationModel);
    	
    	return relatedModel;  	
    }
}