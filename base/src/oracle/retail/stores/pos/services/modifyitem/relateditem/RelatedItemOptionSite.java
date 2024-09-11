package oracle.retail.stores.pos.services.modifyitem.relateditem;

/*===========================================================================
* Copyright (c) 2012, Oracle and/or its affiliates. All rights reserved. 
* ===========================================================================
* $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/modifyitem/relateditem/RelatedItemOptionSite.java /main/1 2012/09/28 17:32:41 yiqzhao Exp $
* ===========================================================================
* NOTES
* <other useful comments, qualifications, etc.>
*
* MODIFIED    (MM/DD/YY)
* yiqzhao     09/27/12 - diable and enable related item buttons.
* yiqzhao     09/26/12 - refactor related item to add cross sell, upsell and
*                        substitute, remove pick one and pick many
* yiqzhao     09/20/12 - Creation
* ===========================================================================
*/
import java.util.HashMap;
import java.util.Locale;

import oracle.retail.stores.common.utility.LocaleMap;
import oracle.retail.stores.domain.lineitem.SaleReturnLineItemIfc;
import oracle.retail.stores.domain.stock.PLUItemIfc;
import oracle.retail.stores.domain.stock.RelatedItemGroupIfc;
import oracle.retail.stores.domain.utility.CodeConstantsIfc;
import oracle.retail.stores.domain.utility.CodeListIfc;
import oracle.retail.stores.domain.utility.LocaleConstantsIfc;
import oracle.retail.stores.foundation.manager.ifc.UIManagerIfc;
import oracle.retail.stores.foundation.tour.gate.Gateway;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.pos.manager.ifc.UtilityManagerIfc;
import oracle.retail.stores.pos.services.PosSiteActionAdapter;
import oracle.retail.stores.pos.services.common.CommonActionsIfc;
import oracle.retail.stores.pos.ui.POSUIManagerIfc;
import oracle.retail.stores.pos.ui.beans.NavigationButtonBeanModel;
import oracle.retail.stores.pos.ui.beans.POSBaseBeanModel;
import oracle.retail.stores.pos.ui.beans.PromptAndResponseModel;
//--------------------------------------------------------------------------
/**
     This site is to display related item option screen.
     $Revision: /main/1 $
 **/
//--------------------------------------------------------------------------
public class RelatedItemOptionSite extends PosSiteActionAdapter
{
    
    //----------------------------------------------------------------------
    /**
        This method determines if there are related items available.
        @param bus
        @see oracle.retail.stores.foundation.tour.ifc.SiteActionIfc#arrive(oracle.retail.stores.foundation.tour.ifc.BusIfc)
    **/
    //----------------------------------------------------------------------
    public void arrive(BusIfc bus)
    {
        POSUIManagerIfc     ui      = (POSUIManagerIfc) bus.getManager(UIManagerIfc.TYPE);
        RelatedItemCargo cargo = (RelatedItemCargo)bus.getCargo();
        
        UtilityManagerIfc utility = (UtilityManagerIfc)  Gateway.getDispatcher().getManager(UtilityManagerIfc.TYPE);
        // add default to department list hash tables
        // retrieve department code list from reason codes.
        CodeListIfc deptMap=utility.getReasonCodes(cargo.getStoreID(),CodeConstantsIfc.CODE_LIST_DEPARTMENT);
        Locale locale = LocaleMap.getLocale(LocaleConstantsIfc.USER_INTERFACE);
        
        // update bean model with matching items list
        PromptAndResponseModel responseModel = new PromptAndResponseModel();
        String[] args = new String[2];
        // get the related item summaries from the related items
        SaleReturnLineItemIfc srli = (SaleReturnLineItemIfc) cargo.getTransaction().getLineItems()[cargo.getPrimaryItemSequenceNumber()];
        PLUItemIfc pluItem = srli.getPLUItem();
        args[0] = pluItem.getPosItemID();
        args[1] = pluItem.getDescription(locale);        
        responseModel.setArguments(args);

        HashMap<String, RelatedItemGroupIfc> relateItemContainer = cargo.getRelatedItemContainer();
        NavigationButtonBeanModel nbbModel = new NavigationButtonBeanModel();
        nbbModel.setButtonEnabled(CommonActionsIfc.ADD, false);
        nbbModel.setButtonEnabled(RelatedItemGroupIfc.CROSS_SELL, true);
        nbbModel.setButtonEnabled(RelatedItemGroupIfc.UPSELL, true);
        nbbModel.setButtonEnabled(RelatedItemGroupIfc.SUBSTITUTE, true);
        if ( relateItemContainer.get(RelatedItemGroupIfc.CROSS_SELL)==null)
        {
        	nbbModel.setButtonEnabled(RelatedItemGroupIfc.CROSS_SELL, false);
        }
        if ( relateItemContainer.get(RelatedItemGroupIfc.UPSELL)==null)
        {
        	nbbModel.setButtonEnabled(RelatedItemGroupIfc.UPSELL, false);
        } 
        if ( relateItemContainer.get(RelatedItemGroupIfc.SUBSTITUTE)==null)
        {
        	nbbModel.setButtonEnabled(RelatedItemGroupIfc.SUBSTITUTE, false);
        } 
        
        POSBaseBeanModel model = (POSBaseBeanModel)ui.getModel(POSUIManagerIfc.RELATED_ITEM_OPTION);
        model.setLocalButtonBeanModel(nbbModel);
        
        model.setPromptAndResponseModel(responseModel);

        ui.showScreen(POSUIManagerIfc.RELATED_ITEM_OPTION, model);                    
    }
}
