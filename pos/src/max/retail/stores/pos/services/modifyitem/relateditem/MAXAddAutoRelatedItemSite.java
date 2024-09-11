/********************************************************************************
 *   
 *	Copyright (c) 2016 - 2017 MAX Hypermarket, Inc    All Rights Reserved.
 *	
 *
 *  Rev 1.0		Feb 17, 2017		Nadia Arora		Changes for Advanced Search - item not getting added and for exception
 *
 ********************************************************************************/

package max.retail.stores.pos.services.modifyitem.relateditem;

import oracle.retail.stores.domain.lineitem.SaleReturnLineItemIfc;
import oracle.retail.stores.domain.stock.PLUItemIfc;
import oracle.retail.stores.domain.stock.RelatedItemGroupIfc;
import oracle.retail.stores.foundation.tour.application.Letter;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.foundation.tour.ifc.LetterIfc;
import oracle.retail.stores.pos.services.PosSiteActionAdapter;
import oracle.retail.stores.pos.services.modifyitem.relateditem.RelatedItemCargo;

public class MAXAddAutoRelatedItemSite  extends PosSiteActionAdapter
{
	private static final long serialVersionUID = -2257427199195734320L;

	public void arrive(BusIfc bus)
	{
		RelatedItemCargo cargo = (RelatedItemCargo)bus.getCargo();
		LetterIfc letter = new Letter("Done");

		if (cargo.isAddAutoRelatedItem())
		{
			SaleReturnLineItemIfc srli = null;
			PLUItemIfc pluItem = null;
			if(cargo.getTransaction().getLineItems() != null)
			{
				srli = (SaleReturnLineItemIfc)cargo.getTransaction().getLineItems()[cargo.getPrimaryItemSequenceNumber()];
				pluItem = srli.getPLUItem();
			}
			RelatedItemGroupIfc relatedItemGroup = (RelatedItemGroupIfc)pluItem.getRelatedItemContainer().get("AUTO");
			if ((relatedItemGroup != null) && (relatedItemGroup.getRelatedItems() != null) && (relatedItemGroup.getRelatedItems().length > 0))

			{
				cargo.setToBeAddRelatedItems(relatedItemGroup.getRelatedItems());
				letter = new Letter("Next");

			}
			else
			{
				letter = new Letter("Undo");
			}
		}

		bus.mail(letter, BusIfc.CURRENT);
	}
}