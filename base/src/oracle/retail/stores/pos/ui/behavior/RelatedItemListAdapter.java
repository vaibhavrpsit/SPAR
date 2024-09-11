/*===========================================================================
* Copyright (c) 2012, 2013, Oracle and/or its affiliates. All rights reserved.
* ===========================================================================
* $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/ui/behavior/RelatedItemListAdapter.java /main/2 2013/04/16 13:32:44 vtemker Exp $
* ===========================================================================
* NOTES
* <other useful comments, qualifications, etc.>
*
* MODIFIED    (MM/DD/YY)
* vtemker     04/16/13 - Moved constants in OrderLineItemIfc to
*                        OrderConstantsIfc in common project
* yiqzhao     11/08/12 - Conditionally enable related item status buttons.
* yiqzhao     11/06/12 - Creation
* ===========================================================================
*/

package oracle.retail.stores.pos.ui.behavior;

// javax imports
import java.util.List;

import javax.swing.JList;

import oracle.retail.stores.common.item.ItemSearchResult;
import oracle.retail.stores.common.item.RelatedItemSearchResult;
import oracle.retail.stores.domain.lineitem.OrderItemStatusIfc;
import oracle.retail.stores.domain.lineitem.OrderLineItemIfc;
import oracle.retail.stores.domain.lineitem.SaleReturnLineItemIfc;
import oracle.retail.stores.domain.order.OrderConstantsIfc;
import oracle.retail.stores.domain.stock.RelatedItemGroupIfc;
import oracle.retail.stores.pos.ui.beans.EYSList;

//--------------------------------------------------------------------------
/**
 *  Toggles the Item button on the sale screen based on the selection
 *  in the sale list.
 *  @version $Revision: /main/2 $
 */
//--------------------------------------------------------------------------
public class RelatedItemListAdapter extends AbstractListAdapter
{
    /** revision number of this class  **/
    public static final String revisionNumber = "$Revision: /main/2 $";

    /** Constants for item action.  */
    private static final String AUTO_DISABLED = "AUTO[false]";
    private static final String AUTO_ENABLED   = "AUTO[true]";
    private static final String CROSSSELL_DISABLED = "CROSSSELL[false]";
    private static final String CROSSSELL_ENABLED   = "CROSSSELL[true]";
    private static final String UPSELL_DISABLED = "UPSELL[false]";
    private static final String UPSELL_ENABLED   = "UPSELL[true]";
    private static final String SUBSTITUTE_DISABLED = "SUBSTITUTE[false]";
    private static final String SUBSTITUTE_ENABLED   = "SUBSTITUTE[true]";

    

//------------------------------------------------------------------------------
/**
 *  Builds a button state string based on the selections in the list.
 *  @param list the JList that triggered the event
 */
    public String determineButtonState(JList list)
    {
        // default is to disable related item type button
        StringBuffer result = new StringBuffer()
        		.append(AUTO_DISABLED).append(",")
        		.append(CROSSSELL_DISABLED).append(",") 
        		.append(UPSELL_DISABLED).append(",")
        		.append(SUBSTITUTE_DISABLED);        		
        // make sure that we have a valid object
        if (list == null)
        {
            return result.toString();
        }

        int currentItem = ((EYSList)list).getSelectedRow();
        boolean enableAuto = false;
        boolean enableCrossSell = false;
        boolean enableUpsell = false;
        boolean enableSubstitute = false;
 
        // check for enable selection
        if (currentItem != -1)
        {
            if (list.getModel().getElementAt(currentItem) instanceof ItemSearchResult)
            {
            	ItemSearchResult itemSearchResult = (ItemSearchResult)list.getModel().getElementAt(currentItem);

            	List<RelatedItemSearchResult> relatedItemSearchResults = itemSearchResult.getRelatedItemSearchResult();
            	if ( relatedItemSearchResults!=null && relatedItemSearchResults.size()>0)
            	{
            		

                	for (RelatedItemSearchResult relatedItemSearchResult: relatedItemSearchResults)
    		    	{
                		if (relatedItemSearchResult.getRelatedItemTypeCode().equals(RelatedItemGroupIfc.AUTOMATIC))
                			enableAuto = true;
                		if (relatedItemSearchResult.getRelatedItemTypeCode().equals(RelatedItemGroupIfc.CROSS_SELL))
                			enableCrossSell = true;	
                		if (relatedItemSearchResult.getRelatedItemTypeCode().equals(RelatedItemGroupIfc.UPSELL))
                			enableUpsell = true;	 
                		if (relatedItemSearchResult.getRelatedItemTypeCode().equals(RelatedItemGroupIfc.SUBSTITUTE))
                			enableSubstitute = true;	 	                		
                	}
            	}
            }
        }
        result = new StringBuffer();
		if (enableAuto)
			result = result.append(AUTO_ENABLED);
		else
			result = result.append(AUTO_DISABLED);
		if (enableCrossSell)
			result = result.append(",").append(CROSSSELL_ENABLED);
		else
			result = result.append(",").append(CROSSSELL_DISABLED);
		if (enableUpsell)
			result = result.append(",").append(UPSELL_ENABLED);
		else
			result = result.append(",").append(UPSELL_DISABLED);
		if (enableSubstitute)
			result = result.append(",").append(SUBSTITUTE_ENABLED);
		else
			result = result.append(",").append(SUBSTITUTE_DISABLED);            


        return result.toString();
    }

    protected boolean isDisabled(SaleReturnLineItemIfc srli)
    {
    	boolean result = false;
        OrderItemStatusIfc orderItemStatus = srli.getOrderItemStatus();
        boolean isDeliveryItem = orderItemStatus.getItemDispositionCode() == OrderConstantsIfc.ORDER_ITEM_DISPOSITION_DELIVERY;
        // Can't change price of a xc order ship item. This is because the pricing change may impact
        // shipping method and cost.
        if(orderItemStatus.isCrossChannelItem() && isDeliveryItem)
        {
        	result = true;
        }
        return result;
    }
}
