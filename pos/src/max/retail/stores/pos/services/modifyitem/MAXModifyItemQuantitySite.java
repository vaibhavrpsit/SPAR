/* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *

  
* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
/** * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * ************************
 * Copyright (c) 2013 Max Hypermarket.    All Rights Reserved. 
 *  Rev 1.0  12/April/2013               Himanshu              MAX-POS-PLU-ITEM-FES_v1.0.doc requirement.
 * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * ***************************/


package max.retail.stores.pos.services.modifyitem;

import max.retail.stores.domain.MaxLiquorDetails;
import max.retail.stores.domain.arts.MAXDataTransactionKeys;
import max.retail.stores.domain.arts.MAXHotKeysTransaction;
import max.retail.stores.domain.lineitem.MAXSaleReturnLineItemIfc;
import max.retail.stores.domain.stock.MAXPLUItemIfc;
import max.retail.stores.domain.transaction.MAXSaleReturnTransactionIfc;
import max.retail.stores.pos.services.sale.MAXSaleCargo;
import oracle.retail.stores.domain.arts.DataTransactionFactory;
import oracle.retail.stores.domain.lineitem.SaleReturnLineItemIfc;
import oracle.retail.stores.domain.stock.PLUItemIfc;
import oracle.retail.stores.domain.stock.UnitOfMeasureIfc;
import oracle.retail.stores.foundation.manager.data.DataException;
import oracle.retail.stores.foundation.manager.ifc.ParameterManagerIfc;
import oracle.retail.stores.foundation.manager.ifc.UIManagerIfc;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.pos.manager.ifc.UtilityManagerIfc;
import oracle.retail.stores.pos.services.PosSiteActionAdapter;
import oracle.retail.stores.pos.services.modifyitem.ItemCargo;
import oracle.retail.stores.pos.ui.DialogScreensIfc;
import oracle.retail.stores.pos.ui.POSUIManagerIfc;
import oracle.retail.stores.pos.ui.beans.DialogBeanModel;
import oracle.retail.stores.pos.ui.beans.POSBaseBeanModel;
import oracle.retail.stores.pos.ui.beans.PromptAndResponseModel;

//--------------------------------------------------------------------------
/**
 *   This site displays the ITEM_QUANTITY screen.
 *   <p>
 *   @version $Revision: 3$
 */
//--------------------------------------------------------------------------
public class MAXModifyItemQuantitySite extends PosSiteActionAdapter
{
    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	//--------------------------------------------------------------------------
    /**
    *   Revision Number furnished by TeamConnection. <P>
    */
    //--------------------------------------------------------------------------
    public static final String revisionNumber = "$Revision: 3$";
    /**
        Unit id tag
    **/
    public static final String UNITID_TAG = "UnitId";
    /**
        Unit id text
    **/
    public static final String UNITID_TEXT = "UN";
    //----------------------------------------------------------------------
    /**
     *   Displays the ITEM_QUANTITY screen.
     *   <P>
     *   @param  bus Service Bus
     */
    //----------------------------------------------------------------------
    public void arrive(BusIfc bus)
    {
    	//MAXSaleCargo cargo1 = (MAXSaleCargo)bus.getCargo();
        String screenID = POSUIManagerIfc.ITEM_QUANTITY;
        POSUIManagerIfc ui = (POSUIManagerIfc)bus.getManager(UIManagerIfc.TYPE);
        MAXItemCargo cargo = (MAXItemCargo)bus.getCargo();
        MAXSaleReturnLineItemIfc lineItem = (MAXSaleReturnLineItemIfc) cargo.getItem();
        PLUItemIfc pluItem = lineItem.getPLUItem();
        UnitOfMeasureIfc uom = lineItem.getPLUItem().getUnitOfMeasure();

        POSBaseBeanModel baseModel = new POSBaseBeanModel();
        PromptAndResponseModel beanModel = new PromptAndResponseModel();
        UtilityManagerIfc utility = 
          (UtilityManagerIfc) bus.getManager(UtilityManagerIfc.TYPE);
        String unitId = UNITID_TEXT;      
             //System.out.println("cargo.getItem()"+cargo.getItem().getItemQuantity()); 
      		
         if ((uom == null) || (unitId.equals(uom.getUnitID())))
        {
            beanModel.setResponseText(Integer.toString(lineItem.getItemQuantityDecimal().intValue()));
            //System.out.println("ResponseText"+Integer.toString(lineItem.getItemQuantityDecimal().intValue())); 
        }
        
        // <!-- MAX Rev 1.0 Change : Start -->
        else if (pluItem instanceof MAXPLUItemIfc && ((MAXPLUItemIfc)pluItem).IsWeightedBarCode())
        {
        	DialogBeanModel dModel = new DialogBeanModel();
            dModel.setType(DialogScreensIfc.ACKNOWLEDGEMENT);
            dModel.setResourceID("CustomerIsNotLinked");
            dModel.setButtonLetter(DialogScreensIfc.BUTTON_OK, "NoLink");
            ui.showScreen(POSUIManagerIfc.DIALOG_TEMPLATE,dModel);
        	
        }
       // <!-- MAX Rev 1.0 Change : end -->
        else
        {
            beanModel.setArguments(uom.getUnitName());
            beanModel.setResponseText(lineItem.getItemQuantityDecimal().toString());
            screenID = POSUIManagerIfc.ITEM_QUANTITY_UOM;
        }

        baseModel.setPromptAndResponseModel(beanModel);
        ui.showScreen(screenID, baseModel);
    }
    
    
}
