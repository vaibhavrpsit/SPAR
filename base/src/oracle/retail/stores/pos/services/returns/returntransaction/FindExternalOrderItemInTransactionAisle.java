/* ===========================================================================
* Copyright (c) 2010, 2011, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/returns/returntransaction/FindExternalOrderItemInTransactionAisle.java /rgbustores_13.4x_generic_branch/1 2011/05/05 14:05:57 mszekely Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    jswan     06/17/10 - Checkin external order integration files for
 *                         refresh.
 *    jswan     06/05/10 - Added for External Order integration.
 *
 * ===========================================================================
 */
package oracle.retail.stores.pos.services.returns.returntransaction;

// java imports
import java.util.List;

import oracle.retail.stores.domain.lineitem.SaleReturnLineItemIfc;
import oracle.retail.stores.domain.stock.PLUItemIfc;
import oracle.retail.stores.foundation.tour.application.Letter;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.pos.services.common.CommonLetterIfc;
import oracle.retail.stores.pos.services.returns.returnoptions.ValidateItemNumberAisle;

//--------------------------------------------------------------------------
/**
    This aisle gets the items the user has selected from the UI.
**/
//--------------------------------------------------------------------------
public class FindExternalOrderItemInTransactionAisle extends ValidateItemNumberAisle
{
    /** serialVersionUID */
    private static final long serialVersionUID = -4542437871647664329L;

    //----------------------------------------------------------------------
    /**
       This aisle gets the items the user has selected from the UI.
       <P>
       @param  bus     Service Bus
    **/
    //----------------------------------------------------------------------
    public void traverse(BusIfc bus)
    {
        ReturnTransactionCargo cargo = (ReturnTransactionCargo) bus.getCargo();
        String letter = null;
        
        // Is there a matching item in the list of items not yet displayed?
        List<SaleReturnLineItemIfc> matchedItemList = cargo.
            getMatchingItemsFromItemsNotDisplayed(cargo.getPLUItemID());
        
        PLUItemIfc returnPLUItem = null;
        if (matchedItemList.size() > 0)
        {
            // we only need one PLUitem from any of the matched items. take
            // the first
            returnPLUItem = matchedItemList.get(0).getPLUItem();
        }
        if (returnPLUItem == null)
        {
            letter = CommonLetterIfc.NOMATCH;
        }
        else 
        if (returnPLUItem.isItemSizeRequired())
        {
            // retrieve size info
            letter = CommonLetterIfc.SIZE;
        }
        else
        if (cargo.isSerialNumberRequired(matchedItemList))
        {
            letter = CommonLetterIfc.SERIAL_NUMBER;
        }
        else
        {
            letter = CommonLetterIfc.ADD;
        }

        bus.mail(new Letter(letter), BusIfc.CURRENT);
    }
}
