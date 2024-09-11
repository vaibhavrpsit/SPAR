/* ===========================================================================
* Copyright (c) 2010, 2011, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/inquiry/iteminquiry/PriceEnteredItemInquiryAisle.java /rgbustores_13.4x_generic_branch/1 2011/05/05 14:05:44 mszekely Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    jswan     11/01/10 - Fixed issues with UNDO and CANCEL letters; this
 *                         includes properly canceling transactions when a user
 *                         presses the cancel button in the item inquiry and
 *                         item inquiry sub tours.
 *    jswan     10/29/10 - Added to mail a specific letter (Add) when the user
 *                         enters a price. This change, along with a
 *                         modification to the tour script, allows tour cam to
 *                         backup through the tour correctly.
 * ===========================================================================
 */
package oracle.retail.stores.pos.services.inquiry.iteminquiry;

import max.retail.stores.pos.services.sale.MAXSaleCargo;
// foundation imports
import oracle.retail.stores.commerceservices.common.currency.CurrencyIfc;
import oracle.retail.stores.common.utility.LocaleMap;
import oracle.retail.stores.domain.DomainGateway;
import oracle.retail.stores.domain.stock.PLUItemIfc;
import oracle.retail.stores.domain.utility.LocaleUtilities;
import oracle.retail.stores.foundation.manager.ifc.UIManagerIfc;
import oracle.retail.stores.foundation.tour.application.Letter;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.pos.services.PosLaneActionAdapter;
import oracle.retail.stores.pos.services.common.CommonLetterIfc;
import oracle.retail.stores.pos.ui.POSUIManagerIfc;

//--------------------------------------------------------------------------
/**
    This aisle is traversed when the Item price has been entered.
    <p>
**/
//--------------------------------------------------------------------------
public class PriceEnteredItemInquiryAisle extends PosLaneActionAdapter
{
    /**
     * serialVersionUID
     */
    private static final long serialVersionUID = -7646292495326561860L;

    //----------------------------------------------------------------------
    /**
       Sets the price of the item.
       <P>
       @param  bus     Service Bus
    **/
    //----------------------------------------------------------------------
    public void traverse(BusIfc bus)
    {
        // Grab the item from the cargo
        ItemInquiryCargo cargo = (ItemInquiryCargo)bus.getCargo();
        MAXSaleCargo cargo = (MAXSaleCargo)bus.getCargo(); 
        System.out.println("PriceEnteredItemInquiryAisle :"+cargo);
        PLUItemIfc pluItem = cargo.getPLUItem();
        POSUIManagerIfc ui=(POSUIManagerIfc)bus.getManager(UIManagerIfc.TYPE);
        String stringValue = LocaleUtilities.parseCurrency(ui.getInput(),LocaleMap.getLocale(LocaleMap.DEFAULT)).toString();
        CurrencyIfc price = DomainGateway.getBaseCurrencyInstance(stringValue);
        pluItem.setPrice(price);
     
        bus.mail(new Letter(CommonLetterIfc.ADD), BusIfc.CURRENT);
    }
}
