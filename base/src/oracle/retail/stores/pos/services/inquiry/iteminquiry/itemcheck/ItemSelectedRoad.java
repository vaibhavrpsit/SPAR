/* ===========================================================================
* Copyright (c) 2005, 2011, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/inquiry/iteminquiry/itemcheck/ItemSelectedRoad.java /rgbustores_13.4x_generic_branch/1 2011/05/05 14:05:44 mszekely Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    cgreene   05/27/10 - convert to oracle packaging
 *    cgreene   05/26/10 - convert to oracle packaging
 *    abondala  01/03/10 - update header date
 *
 * ===========================================================================
 * $Log:
 *    1    360Commerce 1.0         1/25/2006 2:32:54 PM   Brett J. Larsen 
 *
 *
 * ===========================================================================
 */
package oracle.retail.stores.pos.services.inquiry.iteminquiry.itemcheck;

// foundation imports
import oracle.retail.stores.domain.stock.PLUItemIfc;
import oracle.retail.stores.foundation.manager.ifc.UIManagerIfc;
import oracle.retail.stores.foundation.tour.application.LaneActionAdapter;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.pos.services.inquiry.iteminquiry.ItemInquiryCargo;
import oracle.retail.stores.pos.ui.POSUIManagerIfc;
import oracle.retail.stores.pos.ui.beans.ItemListBeanModel;
import oracle.retail.stores.pos.ui.beans.PromptAndResponseModel;

/**
 * Select an item from the list of items.
 * @since 7.0.2
 * @author jdeleau
 */
public class ItemSelectedRoad extends LaneActionAdapter
{

	/**
	 * This file's revision number
	 */
    public static final String revisionNumber = "$Revision: /rgbustores_13.4x_generic_branch/1 $";

    /**
     * Traverse the road
     * @param bus The bus
     */
    public void traverse(BusIfc bus)
    {
        // Initialize bean model values
        POSUIManagerIfc ui = (POSUIManagerIfc)bus.getManager(UIManagerIfc.TYPE);
        ItemInquiryCargo cargo = (ItemInquiryCargo)bus.getCargo();
        ItemListBeanModel model = (ItemListBeanModel) ui.getModel();
        PromptAndResponseModel pModel = model.getPromptAndResponseModel();
        PLUItemIfc item = null;
        
        // If user manually typed in an item#, match on that item#
        if(pModel.getResponseText() != null && pModel.getResponseText().trim().length() > 0)
        {
        	String itemNumber = pModel.getResponseText().trim();
        	PLUItemIfc[] items = model.getItemList();
        	for(int i=0; i<items.length; i++)
        	{
        		if(items[i].getItemID().equals(itemNumber))
        		{
        			item = items[i];
        			cargo.setPLUItem(items[i]);
        			break;
        		}
        	}
        }
        // If no manually typed in number matches, or user never typed a number
        // in, then choose what was selected in the list.
        if(item == null)
        {
        	// Store item selected from item list
        	item = (PLUItemIfc)model.getSelectedItem();
        }
        cargo.setPLUItem(item);
    }
}
