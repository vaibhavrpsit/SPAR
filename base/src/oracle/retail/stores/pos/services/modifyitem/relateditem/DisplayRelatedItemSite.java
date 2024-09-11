/*===========================================================================
* Copyright (c) 2012, 2013, Oracle and/or its affiliates. All rights reserved.
* ===========================================================================
* $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/modifyitem/relateditem/DisplayRelatedItemSite.java /main/3 2013/09/05 10:36:16 abondala Exp $
* ===========================================================================
* NOTES
* <other useful comments, qualifications, etc.>
*
* MODIFIED    (MM/DD/YY)
* abondala    09/04/13 - initialize collections
* yiqzhao     11/08/12 - Using different sites to display different types of
*                        related items.
* yiqzhao     09/28/12 - use letter from CommonLetterIfc
* yiqzhao     09/27/12 - add handling no auto type code related item.
* yiqzhao     09/26/12 - refactor related item to add cross sell, upsell and
*                        substitute, remove pick one and pick many
* yiqzhao     09/20/12 - Creation
* ===========================================================================
*/
package oracle.retail.stores.pos.services.modifyitem.relateditem;

import java.util.HashMap;
import java.util.Locale;

import oracle.retail.stores.common.utility.LocaleMap;
import oracle.retail.stores.domain.lineitem.SaleReturnLineItemIfc;
import oracle.retail.stores.domain.stock.PLUItemIfc;
import oracle.retail.stores.domain.stock.RelatedItemGroupIfc;
import oracle.retail.stores.domain.stock.RelatedItemIfc;
import oracle.retail.stores.domain.stock.RelatedItemSummary;
import oracle.retail.stores.domain.stock.RelatedItemSummaryIfc;
import oracle.retail.stores.domain.utility.CodeConstantsIfc;
import oracle.retail.stores.domain.utility.CodeEntryIfc;
import oracle.retail.stores.domain.utility.CodeListIfc;
import oracle.retail.stores.domain.utility.LocaleConstantsIfc;
import oracle.retail.stores.foundation.manager.ifc.UIManagerIfc;
import oracle.retail.stores.foundation.tour.application.Letter;
import oracle.retail.stores.foundation.tour.gate.Gateway;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.foundation.tour.ifc.LetterIfc;
import oracle.retail.stores.pos.manager.ifc.UtilityManagerIfc;
import oracle.retail.stores.pos.services.PosSiteActionAdapter;
import oracle.retail.stores.pos.services.common.CommonActionsIfc;
import oracle.retail.stores.pos.services.common.CommonLetterIfc;
import oracle.retail.stores.pos.services.modifyitem.relateditem.RelatedItemCargo;
import oracle.retail.stores.pos.ui.POSUIManagerIfc;
import oracle.retail.stores.pos.ui.beans.NavigationButtonBeanModel;
import oracle.retail.stores.pos.ui.beans.PromptAndResponseModel;
import oracle.retail.stores.pos.ui.beans.RelatedItemListBeanModel;
//--------------------------------------------------------------------------
/**
     This site is to display related item option screen.
     $Revision: /main/3 $
 **/
//--------------------------------------------------------------------------
public class DisplayRelatedItemSite extends PosSiteActionAdapter
{
    
    //----------------------------------------------------------------------
    /**
        This method create related item list bean model which is used by its subclasses.
        @param bus
    **/
    //----------------------------------------------------------------------
    public RelatedItemListBeanModel getRelatedItemListBeanModel(BusIfc bus)
    {
    	POSUIManagerIfc     ui      = (POSUIManagerIfc) bus.getManager(UIManagerIfc.TYPE);
    	RelatedItemCargo    cargo   = (RelatedItemCargo)bus.getCargo();
    	
    	LetterIfc letter = bus.getCurrentLetter();

        UtilityManagerIfc utility = (UtilityManagerIfc)  Gateway.getDispatcher().getManager(UtilityManagerIfc.TYPE);
        // add default to department list hash tables
        // retrieve department code list from reason codes.
        CodeListIfc deptMap=utility.getReasonCodes(cargo.getStoreID(),CodeConstantsIfc.CODE_LIST_DEPARTMENT);
        Locale locale = LocaleMap.getLocale(LocaleConstantsIfc.USER_INTERFACE);
        // retrieve department entries
        CodeEntryIfc[] deptEntries = deptMap.getEntries();
        HashMap map = new HashMap(0);
        for(int i=0; i < deptEntries.length; i++)
        {
            map.put(deptEntries[i].getCode(), deptEntries[i].getText(locale));
        }
        RelatedItemSummary.setDepartmentHash(map);

        // get the related item summaries from the related items
        SaleReturnLineItemIfc srli = (SaleReturnLineItemIfc) cargo.getTransaction().getLineItems()[cargo.getPrimaryItemSequenceNumber()];
        PLUItemIfc pluItem = srli.getPLUItem();
        RelatedItemGroupIfc relatedItemGroup = null;
        if (letter.getName().equals(RelatedItemGroupIfc.CROSS_SELL))
        {
        	relatedItemGroup = pluItem.getRelatedItemContainer().get(RelatedItemGroupIfc.CROSS_SELL);
        	cargo.setRelatedItemGroupName(RelatedItemGroupIfc.CROSS_SELL);
        }
        else if (letter.getName().equals(RelatedItemGroupIfc.UPSELL))
        {
        	relatedItemGroup = pluItem.getRelatedItemContainer().get(RelatedItemGroupIfc.UPSELL);
        	cargo.setRelatedItemGroupName(RelatedItemGroupIfc.UPSELL);
        }
        else if (letter.getName().equals(RelatedItemGroupIfc.SUBSTITUTE))
        {
        	relatedItemGroup = pluItem.getRelatedItemContainer().get(RelatedItemGroupIfc.SUBSTITUTE);
        	cargo.setRelatedItemGroupName(RelatedItemGroupIfc.SUBSTITUTE);
        }

        
        RelatedItemIfc[] relatedItems = relatedItemGroup.getRelatedItems();
        RelatedItemSummaryIfc sampleItems[] = new RelatedItemSummaryIfc[relatedItems.length];
        for (int i = 0; i < relatedItems.length; i++)
        {
            sampleItems[i] = relatedItems[i].getRelatedItemSummary();
        }

        // update bean model with matching items list
        RelatedItemListBeanModel relatedModel = new RelatedItemListBeanModel();
        
        PromptAndResponseModel responseModel = new PromptAndResponseModel();
        String[] args = new String[2];
        args[0] = pluItem.getPosItemID();
        args[1] = pluItem.getDescription(locale);
        responseModel.setArguments(args);
        relatedModel.setItemList(sampleItems);
        
        //The first item is default selected
        if ( relatedItems.length>0 )
        {
        	RelatedItemSummaryIfc ri[] = new RelatedItemSummaryIfc[1];
        	ri[0]=sampleItems[0];
        	relatedModel.setSelectedItems(ri);
        }
        	
        
        relatedModel.setPromptAndResponseModel(responseModel);
        
        NavigationButtonBeanModel nbbModel = new NavigationButtonBeanModel();
        nbbModel.setButtonEnabled(CommonActionsIfc.ADD, true);
        nbbModel.setButtonEnabled(RelatedItemGroupIfc.CROSS_SELL, true);
        nbbModel.setButtonEnabled(RelatedItemGroupIfc.UPSELL, true);
        nbbModel.setButtonEnabled(RelatedItemGroupIfc.SUBSTITUTE, true);
        if ( pluItem.getRelatedItemContainer().get(RelatedItemGroupIfc.CROSS_SELL)==null)
        {
        	nbbModel.setButtonEnabled(RelatedItemGroupIfc.CROSS_SELL, false);
        }
        if ( pluItem.getRelatedItemContainer().get(RelatedItemGroupIfc.UPSELL)==null)
        {
        	nbbModel.setButtonEnabled(RelatedItemGroupIfc.UPSELL, false);
        } 
        if ( pluItem.getRelatedItemContainer().get(RelatedItemGroupIfc.SUBSTITUTE)==null)
        {
        	nbbModel.setButtonEnabled(RelatedItemGroupIfc.SUBSTITUTE, false);
        } 
        nbbModel.setButtonEnabled(letter.getName(), false);
        
        relatedModel.setLocalButtonBeanModel(nbbModel);
                     
        return relatedModel;
    }
    
    
}