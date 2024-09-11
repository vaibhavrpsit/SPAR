/*===========================================================================
* Copyright (c) 2012, Oracle and/or its affiliates. All rights reserved. 
* ===========================================================================
* $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/modifyitem/relateditem/AddAutoRelatedItemSite.java /main/1 2012/09/28 17:32:42 yiqzhao Exp $
* ===========================================================================
* NOTES
* <other useful comments, qualifications, etc.>
*
* MODIFIED    (MM/DD/YY)
* yiqzhao     09/28/12 - use letter from CommonLetterIfc
* yiqzhao     09/26/12 - refactor related item to add cross sell, upsell and
*                        substitute, remove pick one and pick many
* yiqzhao     09/26/12 - Creation
* ===========================================================================
*/

package oracle.retail.stores.pos.services.modifyitem.relateditem;


import oracle.retail.stores.domain.lineitem.SaleReturnLineItemIfc;
import oracle.retail.stores.domain.stock.PLUItemIfc;
import oracle.retail.stores.domain.stock.RelatedItemGroupIfc;
import oracle.retail.stores.foundation.tour.application.Letter;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.foundation.tour.ifc.LetterIfc;
import oracle.retail.stores.pos.services.PosSiteActionAdapter;
import oracle.retail.stores.pos.services.common.CommonLetterIfc;

//--------------------------------------------------------------------------
/**
     This site is to add related items with type code of AUTO. Related items with TypeCode is equal to AUTO will
     be automatically added into transaction. If acting from Item/Related Items, optional related items will be added.
     The roadmap will bypass this site based on isAddAutoRelatedItem flag set from different RelatedItemLaunchShuttle classes.
     $Revision: /main/1 $
 **/
//--------------------------------------------------------------------------
public class AddAutoRelatedItemSite extends PosSiteActionAdapter
{
    
    //----------------------------------------------------------------------
    /**
        This method determines if there are AUTO related items available.
        @param bus
        @see oracle.retail.stores.foundation.tour.ifc.SiteActionIfc#arrive(oracle.retail.stores.foundation.tour.ifc.BusIfc)
    **/
    //----------------------------------------------------------------------
    public void arrive(BusIfc bus)
    {
        RelatedItemCargo cargo = (RelatedItemCargo)bus.getCargo();
        LetterIfc letter = new Letter(CommonLetterIfc.DONE); //Assume it is calling from modifyitem station
        
        if ( cargo.isAddAutoRelatedItem() )
        {
        	//It is calling from sale and modifytransaction station
	        // get the related item summaries from the related items
	        SaleReturnLineItemIfc srli = (SaleReturnLineItemIfc) cargo.getTransaction().getLineItems()[cargo.getPrimaryItemSequenceNumber()];
	        PLUItemIfc pluItem = srli.getPLUItem();
	
	        RelatedItemGroupIfc	relatedItemGroup = pluItem.getRelatedItemContainer().get(RelatedItemGroupIfc.AUTOMATIC);
	        if ( relatedItemGroup != null && relatedItemGroup.getRelatedItems() != null && relatedItemGroup.getRelatedItems().length > 0)
	        {
	        	//lookup the items and add them to the transaction
	        	cargo.setToBeAddRelatedItems(relatedItemGroup.getRelatedItems());
	        	letter = new Letter(CommonLetterIfc.NEXT);
	        }
	        else
	        {
	        	//there is no AUTO TypeCode related item for this pluItem, return to the previous station
	        	letter = new Letter(CommonLetterIfc.UNDO);
	        }
        }

       	bus.mail(letter, BusIfc.CURRENT); 
    }
}