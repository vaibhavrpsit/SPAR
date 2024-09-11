/* ===========================================================================
* Copyright (c) 2011, 2013, Oracle and/or its affiliates. All rights reserved.
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/houseaccount/payment/CompletePaymentSite.java /main/2 2013/08/12 15:09:46 asinton Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    asinton   08/08/13 - adding house account payment support to be called
 *                         from site code instead of the
 *                         RetailTransactionTechnician
 *    cgreene   07/26/11 - repacked into houseaccount.payment
 *    ohorne    05/20/11 - created class
 *
 * ===========================================================================
 */
package oracle.retail.stores.pos.services.houseaccount.payment;

import oracle.retail.stores.domain.DomainGateway;
import oracle.retail.stores.domain.manager.ifc.PaymentManagerIfc;
import oracle.retail.stores.domain.manager.payment.HouseAccountPaymentRequestIfc;
import oracle.retail.stores.domain.transaction.PaymentTransactionIfc;
import oracle.retail.stores.foundation.tour.application.Letter;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.pos.services.PosSiteActionAdapter;

/**
 * Completes House Account Payment.
 * This site calls the {@link PaymentManagerIfc#sendPaymentRequest(oracle.retail.stores.domain.manager.payment.HouseAccountPaymentRequestIfc)
 * to execute the payment authorization.
 *
 * @since 13.4
 */
@SuppressWarnings("serial")
public class CompletePaymentSite extends PosSiteActionAdapter
{
    /** Constant for Exit Payment letter */
    public static final String EXIT_PAYMENT = "ExitPayment";

    /* (non-Javadoc)
     * @see oracle.retail.stores.foundation.tour.application.SiteActionAdapter#arrive(oracle.retail.stores.foundation.tour.ifc.BusIfc)
     */
    @Override
    public void arrive(BusIfc bus)
    {
        PayHouseAccountCargo cargo = (PayHouseAccountCargo)bus.getCargo();
        PaymentTransactionIfc paymentTransaction = cargo.getTransaction();
        PaymentManagerIfc paymentManager = (PaymentManagerIfc)bus.getManager(PaymentManagerIfc.TYPE);
        HouseAccountPaymentRequestIfc paymentRequest = DomainGateway.getFactory().getHouseAccountPaymentRequestInstance();
        paymentRequest.setPaymentTransaction(paymentTransaction);
        paymentRequest.setWorkstation(cargo.getRegister().getWorkstation());
        paymentManager.sendPaymentRequest(paymentRequest);
        bus.mail(new Letter(EXIT_PAYMENT), BusIfc.CURRENT);
    }

}
