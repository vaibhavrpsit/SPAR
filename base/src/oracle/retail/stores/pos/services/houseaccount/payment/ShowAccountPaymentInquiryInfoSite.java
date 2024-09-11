/* ===========================================================================
* Copyright (c) 2013, Oracle and/or its affiliates. All rights reserved.
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/houseaccount/payment/ShowAccountPaymentInquiryInfoSite.java /main/1 2013/07/02 13:09:09 jswan Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    jswan     07/01/13 - Fixed failure to cancel House Account Transaction
 *                         when cancel button pressed or timout occurs.
 */
package oracle.retail.stores.pos.services.houseaccount.payment;

import oracle.retail.stores.domain.utility.InstantCreditIfc;
import oracle.retail.stores.foundation.manager.ifc.UIManagerIfc;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.pos.services.PosSiteActionAdapter;
import oracle.retail.stores.pos.ui.POSUIManagerIfc;
import oracle.retail.stores.pos.ui.beans.InstantCreditInquiryBeanModel;

//--------------------------------------------------------------------------
/**
     This site shows the account information on the screen
    @version $Revision: /main/1 $
**/
//--------------------------------------------------------------------------
@SuppressWarnings("serial")
public class ShowAccountPaymentInquiryInfoSite extends PosSiteActionAdapter
{
    //----------------------------------------------------------------------
    /**
       @param  bus     Service Bus
    **/
    //----------------------------------------------------------------------
    public void arrive(BusIfc bus)
    {
        POSUIManagerIfc ui = (POSUIManagerIfc) bus.getManager(UIManagerIfc.TYPE);
        PayHouseAccountCargo cargo = (PayHouseAccountCargo) bus.getCargo();
        InstantCreditIfc card = cargo.getInstantCredit();

        InstantCreditInquiryBeanModel model = new InstantCreditInquiryBeanModel();
        // populate bean model
        model.setFirstName(card.getCustomer().getFirstName());
        model.setLastName(card.getCustomer().getLastName());
        model.setAccountNumber(card.getEncipheredCardData().getTruncatedAcctNumber());
        model.setCurrentBalance(card.getCurrentBalance());
        model.setCreditLimit(card.getCreditLimit());
        model.setCreditAvailable(card.getCreditLimit().subtract(card.getCurrentBalance()));

        // show screen
        ui.showScreen(POSUIManagerIfc.INSTANT_CREDIT_PAYMENT_ACCOUNT, model);
    }
}
