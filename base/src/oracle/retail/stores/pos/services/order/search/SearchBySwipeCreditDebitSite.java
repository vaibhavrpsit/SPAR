/*===========================================================================
* Copyright (c) 2012, 2013, Oracle and/or its affiliates. All rights reserved.
* ===========================================================================
* $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/order/search/SearchBySwipeCreditDebitSite.java /main/4 2013/04/19 13:35:38 jswan Exp $
* ===========================================================================
* NOTES
* <other useful comments, qualifications, etc.>
*
* MODIFIED    (MM/DD/YY)
* jswan       04/18/13 - Modified to make lookup by credit card work the same
*                        way for orders and transactions.
* yiqzhao     08/01/12 - Update flow for order search by credit/debit card.
* yiqzhao     07/27/12 - modify order search flow and populate order cargo for
*                        searching
* yiqzhao     07/23/12 - modify order search flow for xchannel order and
*                        special order
* yiqzhao     07/20/12 - Creation
* ===========================================================================
*/

package oracle.retail.stores.pos.services.order.search;

import oracle.retail.stores.common.utility.Util;
import oracle.retail.stores.domain.DomainGateway;
import oracle.retail.stores.domain.manager.ifc.PaymentManagerIfc;
import oracle.retail.stores.domain.manager.payment.CardTokenRequestIfc;
import oracle.retail.stores.domain.manager.payment.CardTokenResponseIfc;
import oracle.retail.stores.domain.manager.payment.PaymentServiceResponseIfc.ResponseCode;
import oracle.retail.stores.domain.utility.CardData;
import oracle.retail.stores.domain.utility.CardDataIfc;
import oracle.retail.stores.foundation.manager.ifc.UIManagerIfc;
import oracle.retail.stores.foundation.tour.application.Letter;
import oracle.retail.stores.foundation.tour.gate.Gateway;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.pos.services.PosSiteActionAdapter;
import oracle.retail.stores.pos.services.common.CommonLetterIfc;
import oracle.retail.stores.pos.services.order.common.OrderCargo;
import oracle.retail.stores.pos.services.order.common.OrderSearchCargoIfc;
import oracle.retail.stores.pos.ui.DialogScreensIfc;
import oracle.retail.stores.pos.ui.POSUIManagerIfc;
import oracle.retail.stores.pos.ui.beans.DialogBeanModel;
import oracle.retail.stores.pos.ui.beans.POSBaseBeanModel;
import oracle.retail.stores.pos.ui.beans.StatusBeanModel;

/**
 * Displays the "ask customer to swipe card" screen and calls APF to 
 * get the card token/masked card number to perform the search.
 */
@SuppressWarnings("serial")
public class SearchBySwipeCreditDebitSite extends PosSiteActionAdapter
{
    /**
       class name constant
    **/
    public static final String SITENAME = "SearchBySwipeCreditDebitSite";

    /**
       revision number for this class
    **/
    public static final String revisionNumber = "$Revision: /main/4 $";

    //--------------------------------------------------------------------------
    /**
        Displays the "ask customer to swipe card" screen and calls APF to 
        get the card token/masked card number to perform the search.
        <P>
        @param bus the bus arriving at this site
    **/
    //--------------------------------------------------------------------------
    public void arrive(BusIfc bus)
    {
        showSwipeScreen(bus);
        
        Letter letter = getMaskedCardIDAndToken(bus);
        
        if (letter != null)
        {
            bus.mail(letter, BusIfc.CURRENT);
        }
    }

    /**
     * Display the "ask customer to swipe card" screen.
     * 
     * @param bus
     */
    protected void showSwipeScreen(BusIfc bus)
    {
        POSUIManagerIfc ui      = (POSUIManagerIfc) bus.getManager(UIManagerIfc.TYPE);

        ui.statusChanged(POSUIManagerIfc.FINANCIAL_NETWORK_STATUS,
                         POSUIManagerIfc.ONLINE);
        
        // clear the customer's name in the status area
        POSBaseBeanModel model  = new POSBaseBeanModel();
        StatusBeanModel statusModel = new StatusBeanModel();
        statusModel.setCustomerName("");
        model.setStatusBeanModel(statusModel);

        OrderCargo cargo = (OrderCargo) bus.getCargo();
        cargo.setSearchMethod(OrderSearchCargoIfc.SEARCH_BY_CREDIT_DEBIT_CARD);
        ui.showScreen(POSUIManagerIfc.SEARCH_BY_SWIPE_CARD, model);
    }

    /**
     * Call APF to get the card token/masked card number to perform the
     * search.
     * 
     * @param bus
     * @return return the letter to mail to the bus; will be null if a
     * dialog has been displayed.
     */
    protected Letter getMaskedCardIDAndToken(BusIfc bus)
    {
        Letter letter = null;
        OrderCargo cargo = (OrderCargo) bus.getCargo();

        CardTokenRequestIfc request = DomainGateway.getFactory().getCardTokenRequestInstance();
        request.setWorkstation(cargo.getRegister().getWorkstation());

        try
        {
            PaymentManagerIfc paymentManager = (PaymentManagerIfc)Gateway.getDispatcher().getManager(PaymentManagerIfc.TYPE);
            CardTokenResponseIfc response = (CardTokenResponseIfc)paymentManager.getCardToken(request);
    
            ResponseCode responseCode = response.getResponseCode();
            String acctToken = response.getAccountNumberToken();
            String maskedAcctNo = response.getMaskedAccountNumber();
            if(( ResponseCode.Approved.equals(responseCode) || ResponseCode.InquiryForTenderSucceeded.equals(responseCode)) &&
               (!Util.isEmpty(acctToken) || !Util.isEmpty(maskedAcctNo)))
            {
                CardDataIfc cardData = new CardData();
                if (!Util.isEmpty(acctToken))
                {
                    cardData.setCardToken(acctToken);
                }
                if (!Util.isEmpty(maskedAcctNo))
                {
                    if ( maskedAcctNo.length()==4 )
                    {
                        cardData.setTrailingCardNumber(maskedAcctNo);
                    }
                    else if (maskedAcctNo.length()>4)
                    {
                        cardData.setTrailingCardNumber(maskedAcctNo.substring(maskedAcctNo.length()-4));
                        if ( maskedAcctNo.length()>=10 )
                        {
                            cardData.setLeadingCardNumber(maskedAcctNo.substring(0,6));
                        }
                    }
                }
                cargo.setCardData(cardData);
                bus.mail(new Letter(CommonLetterIfc.SUCCESS), BusIfc.CURRENT);
            }
            else
            {
                logger.warn("Card read returned with an error condition or no card data was available.");
                displayErrorDialog(bus);
            }
        }
        catch (Exception e)
        {
            POSUIManagerIfc ui = (POSUIManagerIfc) bus.getManager(UIManagerIfc.TYPE);
            ui.statusChanged(POSUIManagerIfc.FINANCIAL_NETWORK_STATUS,
                    POSUIManagerIfc.OFFLINE);
            logger.warn("Exception while requesting card number token: ", e);
            displayErrorDialog(bus);
        }
        
        return letter;
    }
    
    /*
     * Display the generic error message
     */
    private void displayErrorDialog(BusIfc bus)
    {
        POSUIManagerIfc ui = (POSUIManagerIfc) bus.getManager(UIManagerIfc.TYPE);

        //display the dialog
        DialogBeanModel model = new DialogBeanModel();
        model.setResourceID("RET_TRANS_SEARCH_BY_TOKEN_ERROR");
        model.setType(DialogScreensIfc.CONFIRMATION);
        ui.showScreen(POSUIManagerIfc.DIALOG_TEMPLATE, model);
    }
}