/*===========================================================================
* Copyright (c) 2010, 2011, Oracle and/or its affiliates. All rights reserved. 
* ===========================================================================
* $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/externalorder/sellorderitem/CheckPLUItemSite.java /rgbustores_13.4x_generic_branch/1 2011/05/05 14:05:59 mszekely Exp $
* ===========================================================================
* NOTES
* <other useful comments, qualifications, etc.>
*
* MODIFIED    (MM/DD/YY)
* sgu         06/03/10 - refactor AddItemSite
* sgu         06/01/10 - check in after merge
* sgu         06/01/10 - check in order sell item flow
* sgu         05/26/10 - add starting site of order sell item service
* sgu         05/26/10 - Creation
* ===========================================================================
*/

package oracle.retail.stores.pos.services.externalorder.sellorderitem;

import oracle.retail.stores.domain.stock.PLUItemIfc;
import oracle.retail.stores.foundation.tour.application.Letter;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.pos.services.PosSiteActionAdapter;

public class CheckPLUItemSite extends PosSiteActionAdapter
{
    /**
	 * generated serial version UID
	 */
	private static final long serialVersionUID = 7174699861817870569L;

	/**
     * Letter for multiple items found
     */
    public static final String LETTER_MULTIPLE_ITEMS_FOUND = "MultipleItemsFound";

    /**
     * Letter for one item found
     */
    public static final String LETTER_ONE_ITEM_FOUND = "OneItemFound";

    /**
     * Letter for no item found
     */
    public static final String LETTER_ITEM_NOT_FOUND = "ItemNotFound";

    //----------------------------------------------------------------------
    /**
   		Check the PLU to find out if it is one item, multiple items, or an
   		unknown item case
        <P>
        @param  bus     Service Bus
    **/
    //----------------------------------------------------------------------
    public void arrive(BusIfc bus)
    {
        SellOrderItemCargo cargo = (SellOrderItemCargo)bus.getCargo();

    	String letter = LETTER_ITEM_NOT_FOUND;
    	PLUItemIfc[] itemList = cargo.getItemList();
        if (itemList != null && itemList.length > 1)
        {
        	letter = LETTER_MULTIPLE_ITEMS_FOUND;
        }
        else if (cargo.getPLUItem() != null)
        {
        	letter = LETTER_ONE_ITEM_FOUND;
        }


        bus.mail(new Letter(letter), BusIfc.CURRENT);

    }
}