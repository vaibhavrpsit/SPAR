/* ===========================================================================
* Copyright (c) 2011, 2013, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/giftcertificate/issue/GetGiftCertificateAmountRoad.java /main/1 2013/01/04 16:42:28 asinton Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    asinton   01/04/13 - Fixed problem with pricing of a gift certificate
 *
 * ===========================================================================
 */

package oracle.retail.stores.pos.services.giftcertificate.issue;

import oracle.retail.stores.common.utility.LocaleMap;
import oracle.retail.stores.domain.DomainGateway;
import oracle.retail.stores.domain.event.PriceChangeIfc;
import oracle.retail.stores.domain.utility.LocaleUtilities;
import oracle.retail.stores.foundation.manager.ifc.UIManagerIfc;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.pos.services.PosLaneActionAdapter;
import oracle.retail.stores.pos.services.giftcard.GiftCardUtilities;
import oracle.retail.stores.pos.services.sale.SaleCargo;
import oracle.retail.stores.pos.ui.POSUIManagerIfc;

/**
 * Retrieves the amount for the gift certificate from the UI.
 * 
 * @since 14.0
 *
 */
@SuppressWarnings("serial")
public class GetGiftCertificateAmountRoad extends PosLaneActionAdapter
{

    /* (non-Javadoc)
     * @see oracle.retail.stores.foundation.tour.application.LaneActionAdapter#traverse(oracle.retail.stores.foundation.tour.ifc.BusIfc)
     */
    @Override
    public void traverse(BusIfc bus)
    {
        POSUIManagerIfc ui = (POSUIManagerIfc) bus.getManager(UIManagerIfc.TYPE);
        SaleCargo cargo = (SaleCargo) bus.getCargo();
        String currencyText = LocaleUtilities.parseCurrency(ui.getInput().trim(), LocaleMap.getLocale(LocaleMap.DEFAULT)).toString();
        PriceChangeIfc priceChange = DomainGateway.getFactory().getPriceChangeInstance();
        priceChange.setOverridePriceAmount(GiftCardUtilities.getCurrency(currencyText));
        cargo.getPLUItem().setPermanentPriceChanges(new PriceChangeIfc[]{priceChange});
    }

}
