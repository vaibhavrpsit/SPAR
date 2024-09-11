/*===========================================================================
* Copyright (c) 2012, 2013, Oracle and/or its affiliates. All rights reserved. 
* ===========================================================================
* $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/modifyitem/relateditem/RelatedItemLookupSite.java /main/2 2013/02/20 11:55:40 yiqzhao Exp $
* ===========================================================================
* NOTES
* <other useful comments, qualifications, etc.>
*
* MODIFIED    (MM/DD/YY)
* yiqzhao     02/19/13 - Display AddRelatedItem dialog.
* yiqzhao     09/28/12 - use letter from CommonLetterIfc
* yiqzhao     09/26/12 - refactor related item to add cross sell, upsell and
*                        substitute, remove pick one and pick many
* yiqzhao     09/20/12 - Creation
* ===========================================================================
*/
package oracle.retail.stores.pos.services.modifyitem.relateditem;





import oracle.retail.stores.common.utility.StringUtils;
import oracle.retail.stores.foundation.manager.ifc.UIManagerIfc;
import oracle.retail.stores.foundation.tour.application.Letter;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.pos.services.PosSiteActionAdapter;
import oracle.retail.stores.pos.services.common.CommonLetterIfc;
import oracle.retail.stores.pos.ui.DialogScreensIfc;
import oracle.retail.stores.pos.ui.POSUIManagerIfc;
import oracle.retail.stores.pos.ui.beans.DialogBeanModel;

//--------------------------------------------------------------------------
/**
     This site is to display related item option screen.
     $Revision: /main/2 $
 **/
//--------------------------------------------------------------------------
public class RelatedItemLookupSite extends PosSiteActionAdapter
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
    	RelatedItemCargo cargo = (RelatedItemCargo)bus.getCargo();
    	int index = cargo.getNextRelatedItem();
    	if (cargo.getToBeAddRelatedItems().length > index)
    	{
    		cargo.setRelatedItem(cargo.getToBeAddRelatedItems()[index]);
    		cargo.setNextRelatedItem(cargo.getNextRelatedItem()+1);
    	
	        String itemId = cargo.getRelatedItem().getRelatedItemSummary().getPosItemID();
	        if (StringUtils.isEmpty(itemId))
	        {
	            itemId = cargo.getRelatedItem().getRelatedItemSummary().getItemID();
	        }
	        cargo.setPLUItemID(itemId);
	        
	        if ( cargo.isAddAutoRelatedItem() )
	        {
	            POSUIManagerIfc ui = (POSUIManagerIfc)bus.getManager(UIManagerIfc.TYPE);
    	        
    	        DialogBeanModel dialogModel = new DialogBeanModel();
    	        dialogModel.setResourceID("AddAutoRelatedItem");
    	        dialogModel.setType(DialogScreensIfc.ACKNOWLEDGEMENT);
    	        dialogModel.setButtonLetter(DialogScreensIfc.BUTTON_OK, CommonLetterIfc.LOOKUP);
    	        // Display the dialog.
    	        ui.showScreen(POSUIManagerIfc.DIALOG_TEMPLATE, dialogModel);
	        }
	        else
	        {
	            bus.mail(new Letter(CommonLetterIfc.LOOKUP), BusIfc.CURRENT);
	        }
    	}
    	else
    	{
    		bus.mail(new Letter(CommonLetterIfc.DONE), BusIfc.CURRENT);
    	}
    }
}