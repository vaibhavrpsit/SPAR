/* ===========================================================================
* Copyright (c) 2005, 2011, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/inquiry/iteminquiry/itemcheck/SelectItemSite.java /rgbustores_13.4x_generic_branch/1 2011/05/05 14:05:44 mszekely Exp $
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
 *    1    360Commerce 1.0         1/25/2006 2:32:55 PM   Brett J. Larsen 
 *
 * ===========================================================================
 */
package oracle.retail.stores.pos.services.inquiry.iteminquiry.itemcheck;

import oracle.retail.stores.domain.stock.PLUItemIfc;
import oracle.retail.stores.foundation.manager.ifc.UIManagerIfc;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.pos.services.PosSiteActionAdapter;
import oracle.retail.stores.pos.services.inquiry.iteminquiry.ItemInquiryCargo;
import oracle.retail.stores.pos.ui.POSUIManagerIfc;
import oracle.retail.stores.pos.ui.beans.ItemListBeanModel;

/**
 * This site is called if one id_itm_pos in the database maps to more than
 * one id_itm.  Then the user must select which item to add to the transaction.
 * 
 * @author jdeleau
 * @since 7.0.2
 */
public class SelectItemSite extends PosSiteActionAdapter 
{
	/**
	 * Arrive site.
	 * @param bus Bus
	 */
	public void arrive(BusIfc bus)
	{
		ItemInquiryCargo cargo = (ItemInquiryCargo)bus.getCargo();
		PLUItemIfc items[] = cargo.getItemList();

		ItemListBeanModel beanModel = new ItemListBeanModel();
		beanModel.setItemList(items);
        
        // Show the result
        displayScreen(bus, beanModel);
	}
	
	/**
	 * Display the Screen with a list of items for the
	 * user to select from
	 * @param bus
	 * @param beanModel
	 * @since 7.0.2
	 */
	public void displayScreen(BusIfc bus, ItemListBeanModel beanModel)
	{
        // Display the screen
        POSUIManagerIfc ui = (POSUIManagerIfc) bus.getManager(UIManagerIfc.TYPE);

        ui.setModel(POSUIManagerIfc.SELECT_ITEM, beanModel);
        ui.showScreen(POSUIManagerIfc.SELECT_ITEM, beanModel);
	}
}
