/*===========================================================================
* Copyright (c) 2012, Oracle and/or its affiliates. All rights reserved. 
* ===========================================================================
* $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/modifyitem/relateditem/DisplayCrossSellRelatedItemSite.java /main/1 2012/11/08 17:29:02 yiqzhao Exp $
* ===========================================================================
* NOTES
* <other useful comments, qualifications, etc.>
*
* MODIFIED    (MM/DD/YY)
* yiqzhao     11/08/12 - Using different sites to display different types of
*                        related items.
* yiqzhao     11/08/12 - Creation
* ===========================================================================
*/
package oracle.retail.stores.pos.services.modifyitem.relateditem;

import oracle.retail.stores.foundation.manager.ifc.UIManagerIfc;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.pos.ui.POSUIManagerIfc;
import oracle.retail.stores.pos.ui.beans.RelatedItemListBeanModel;


public class DisplayCrossSellRelatedItemSite extends DisplayRelatedItemSite
{
    
    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;

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

    	RelatedItemListBeanModel relatedItemListBeanModel = getRelatedItemListBeanModel(bus);
    	ui.showScreen(POSUIManagerIfc.CROSS_SELL_RELATED_ITEMS, relatedItemListBeanModel);
    }
 }