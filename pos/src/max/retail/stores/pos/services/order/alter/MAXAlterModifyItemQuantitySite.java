/* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
  Copyright (c) 2012-2013 MAXHyperMarket, Inc.    All Rights Reserved.
  Rev 1.0	1/May/2013	  	Tanmaya, Home Delivery Special Order
* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
package max.retail.stores.pos.services.order.alter;

import max.retail.stores.pos.services.order.common.MAXOrderCargo;
import oracle.retail.stores.domain.lineitem.OrderLineItemIfc;
import oracle.retail.stores.domain.stock.UnitOfMeasureIfc;
import oracle.retail.stores.foundation.manager.ifc.UIManagerIfc;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.pos.manager.ifc.UtilityManagerIfc;
import oracle.retail.stores.pos.services.PosSiteActionAdapter;
import oracle.retail.stores.pos.ui.POSUIManagerIfc;
import oracle.retail.stores.pos.ui.beans.NavigationButtonBeanModel;
import oracle.retail.stores.pos.ui.beans.POSBaseBeanModel;
import oracle.retail.stores.pos.ui.beans.PromptAndResponseModel;

public class MAXAlterModifyItemQuantitySite extends PosSiteActionAdapter
{
	private static final long serialVersionUID = -7677939223186120857L;
    public static final String revisionNumber = "$Revision: 3$";
    public static final String UNITID_TAG = "UnitId";
    public static final String UNITID_TEXT = "UN";
    public void arrive(BusIfc bus)
    {
        String screenID = POSUIManagerIfc.ITEM_QUANTITY;
        POSUIManagerIfc ui = (POSUIManagerIfc)bus.getManager(UIManagerIfc.TYPE);
        MAXOrderCargo cargo = (MAXOrderCargo)bus.getCargo();
        
		OrderLineItemIfc lineItem = cargo.getLineItem();
        UnitOfMeasureIfc uom = lineItem.getPLUItem().getUnitOfMeasure();

        POSBaseBeanModel baseModel = new POSBaseBeanModel();
        PromptAndResponseModel beanModel = new PromptAndResponseModel();
        UtilityManagerIfc utility = 
          (UtilityManagerIfc) bus.getManager(UtilityManagerIfc.TYPE);
        String unitId = UNITID_TEXT;
        if ((uom == null) || (unitId.equals(uom.getUnitID())))
        {
            beanModel.setResponseText(Integer.toString(lineItem.getItemQuantityDecimal().intValue()));
        }
        else
        {
            beanModel.setArguments(uom.getUnitName());
            beanModel.setResponseText(lineItem.getItemQuantityDecimal().toString());
            screenID = POSUIManagerIfc.ITEM_QUANTITY_UOM;
        }
        
        
        NavigationButtonBeanModel globalButtonBeanModel = new NavigationButtonBeanModel();
        globalButtonBeanModel.setButtonEnabled("Cancel",false);
        baseModel.setGlobalButtonBeanModel(globalButtonBeanModel);
        baseModel.setPromptAndResponseModel(beanModel);
        ui.showScreen(screenID, baseModel);
    }
}
