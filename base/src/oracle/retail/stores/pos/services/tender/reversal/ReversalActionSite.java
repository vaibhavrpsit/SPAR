/* ===========================================================================
* Copyright (c) 1998, 2013, Oracle and/or its affiliates. All rights reserved.
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/tender/reversal/ReversalActionSite.java /main/2 2013/11/15 17:35:54 asinton Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    asinton   11/14/13 - modified to manage updates to the archived
 *                         transaction for possible reversal of pending
 *                         authorizations in the case of application crash
 *    blarsen   07/22/11 - Move the request building into ReversalCargo which
 *                         is called by the shuttles.
 *    cgreene   07/21/11 - remove DebitBinFileLookup and DebitCardsAccepted
 *                         parameters for APF
 *    blarsen   07/19/11 - Enhancing to consider reversals from post-void
 *                         (VOID_AUTH_PENDING). Updated to use new reversal
 *                         service cargo (ReversalCargo)
 *    blarsen   07/15/11 - Renamed rawJournalKey to journalKey.
 *    blarsen   07/15/11 - Initial version.
 *
 * ===========================================================================
 */
package oracle.retail.stores.pos.services.tender.reversal;

import oracle.retail.stores.common.context.BeanLocator;
import oracle.retail.stores.domain.manager.ifc.PaymentManagerIfc;
import oracle.retail.stores.domain.manager.payment.ReversalRequestIfc;
import oracle.retail.stores.foundation.tour.application.Letter;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.pos.manager.archive.AuthorizedTendersTransactionArchiveHelperIfc;
import oracle.retail.stores.pos.services.PosSiteActionAdapter;


/**
 * Send reversals to payment manager.
 */
@SuppressWarnings("serial")
public class ReversalActionSite extends PosSiteActionAdapter
{
    /**
     * Send reversals to the payment manager.
     */
    @Override
    public void arrive(BusIfc bus)
    {
        String letter = "Success";

        ReversalCargo cargo = (ReversalCargo)bus.getCargo();

        for (ReversalRequestIfc request: cargo.getRequestList())
        {
            PaymentManagerIfc paymentManager = (PaymentManagerIfc)bus.getManager(PaymentManagerIfc.TYPE);
            paymentManager.reversal(request);
            AuthorizedTendersTransactionArchiveHelperIfc helper = (AuthorizedTendersTransactionArchiveHelperIfc)BeanLocator.getServiceBean(AuthorizedTendersTransactionArchiveHelperIfc.AUTHORIZED_TENDERS_TRANSACTION_ARCHIVE_HELPER_BEAN_KEY);
            helper.removePendingAuthorizationFromTransactionArchive(bus, request);
        }

        if (letter != null)
        {
            bus.mail(new Letter(letter), BusIfc.CURRENT);
        }
    }

}
