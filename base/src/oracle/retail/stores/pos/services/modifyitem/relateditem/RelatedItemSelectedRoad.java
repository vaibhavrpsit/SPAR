/*===========================================================================
* Copyright (c) 2012, Oracle and/or its affiliates. All rights reserved. 
* ===========================================================================
* $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/modifyitem/relateditem/RelatedItemSelectedRoad.java /main/2 2012/12/10 19:16:13 tksharma Exp $
* ===========================================================================
* NOTES
* <other useful comments, qualifications, etc.>
*
* MODIFIED    (MM/DD/YY)
* tksharma    12/10/12 - commons-lang update 3.1
* yiqzhao     09/26/12 - refactor related item to add cross sell, upsell and
*                        substitute, remove pick one and pick many
* yiqzhao     09/20/12 - Creation
* ===========================================================================
*/

package oracle.retail.stores.pos.services.modifyitem.relateditem;

import org.apache.commons.lang3.StringUtils;

import oracle.retail.stores.domain.lineitem.SaleReturnLineItemIfc;
import oracle.retail.stores.domain.stock.RelatedItemGroupIfc;
import oracle.retail.stores.domain.stock.RelatedItemIfc;
import oracle.retail.stores.domain.stock.RelatedItemSummaryIfc;
import oracle.retail.stores.foundation.manager.ifc.UIManagerIfc;
import oracle.retail.stores.foundation.tour.application.LaneActionAdapter;
import oracle.retail.stores.foundation.tour.application.Letter;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.pos.services.PosSiteActionAdapter;
import oracle.retail.stores.pos.ui.POSUIManagerIfc;
import oracle.retail.stores.pos.ui.beans.RelatedItemListBeanModel;

/**
 * This site determines if there are any related items left that have been
 * chosen or are automatic. If there are send the next one to the item lookup
 * service.
 * 
 * @version $Revision: /main/2 $
 */
@SuppressWarnings("serial")
public class RelatedItemSelectedRoad extends LaneActionAdapter
{
    /**
     * Check the related items left, get the next one to process or send done
     * letter. If there are any left, set that item to the sale line item and
     * send to lookup station.
     * 
     * @param bus
     * @see oracle.retail.stores.foundation.tour.ifc.SiteActionIfc#arrive(oracle.retail.stores.foundation.tour.ifc.BusIfc)
     */
    @Override
    public void traverse(BusIfc bus)
    {
        RelatedItemCargo cargo = (RelatedItemCargo)bus.getCargo();
        POSUIManagerIfc  ui    = (POSUIManagerIfc) bus.getManager(UIManagerIfc.TYPE);
        
        
        RelatedItemListBeanModel relatedModel = null;
        if ( cargo.getRelatedItemGroupName().equals(RelatedItemGroupIfc.CROSS_SELL))
        	relatedModel = (RelatedItemListBeanModel) ui.getModel(POSUIManagerIfc.CROSS_SELL_RELATED_ITEMS);
        else if ( cargo.getRelatedItemGroupName().equals(RelatedItemGroupIfc.UPSELL))
        	relatedModel = (RelatedItemListBeanModel) ui.getModel(POSUIManagerIfc.UPSELL_RELATED_ITEMS);        	
        else if ( cargo.getRelatedItemGroupName().equals(RelatedItemGroupIfc.SUBSTITUTE))
        	relatedModel = (RelatedItemListBeanModel) ui.getModel(POSUIManagerIfc.SUBSTITUTE_RELATED_ITEMS);        	
        
        if ( relatedModel != null )
        {
	        RelatedItemSummaryIfc ri[] = relatedModel.getSelectedItems();
	        //Get selected related item from related item summary
	        if(ri != null)
	        {
	            RelatedItemIfc[] relatedItems = new RelatedItemIfc[ri.length];
	
	            SaleReturnLineItemIfc item = (SaleReturnLineItemIfc) cargo.getTransaction().getLineItems()[cargo.getPrimaryItemSequenceNumber()];
	            RelatedItemGroupIfc relatedItemGroup = null;
	            if ( cargo.getRelatedItemGroupName().equals(RelatedItemGroupIfc.CROSS_SELL) )
	            {
	            	relatedItemGroup = item.getPLUItem().getRelatedItemContainer().get(RelatedItemGroupIfc.CROSS_SELL);
	            }
	            else if ( cargo.getRelatedItemGroupName().equals(RelatedItemGroupIfc.UPSELL) )
	            {
	            	relatedItemGroup = item.getPLUItem().getRelatedItemContainer().get(RelatedItemGroupIfc.UPSELL);
	            }
	            else if ( cargo.getRelatedItemGroupName().equals(RelatedItemGroupIfc.SUBSTITUTE) )
	            {
	            	relatedItemGroup = item.getPLUItem().getRelatedItemContainer().get(RelatedItemGroupIfc.SUBSTITUTE);
	            }	            
	            RelatedItemIfc[] allRelatedItems = relatedItemGroup.getRelatedItems();
	            for (int i = 0; i < ri.length; i++)
	            {
	                for (int j = 0; j < allRelatedItems.length; j++)
	                {
	                    if (allRelatedItems[j].getRelatedItemSummary().equals(ri[i]))
	                    {
	                        relatedItems[i] = allRelatedItems[j];
	                        break;
	                    }
	                }
	            }
	        
	            cargo.setToBeAddRelatedItems(relatedItems);
	        }
        }
    }

}