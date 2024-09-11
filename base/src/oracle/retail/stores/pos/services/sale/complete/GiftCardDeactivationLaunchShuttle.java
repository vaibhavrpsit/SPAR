/* ===========================================================================
* Copyright (c) 2010, 2013, Oracle and/or its affiliates. All rights reserved.
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/sale/complete/GiftCardDeactivationLaunchShuttle.java /main/2 2013/09/09 14:05:50 asinton Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    asinton   09/06/13 - made the canceling of transaction with deactivation
 *                         of giftcards more robust
 *    asinton   08/29/11 - Added the transaction to the ActivationCargo.
 *    ohorne    08/09/11 - APF:foreign currency support
 *    rrkohli   07/27/11 - build break fix
 *    kelesika  12/06/10 - Multiple reversal of gift cards
 *    kelesika  12/03/10 - Multiple gift card reversals
 *
 * ===========================================================================
 */
package oracle.retail.stores.pos.services.sale.complete;

import java.util.ArrayList;
import java.util.List;

import oracle.retail.stores.domain.DomainGateway;
import oracle.retail.stores.domain.financial.RegisterIfc;
import oracle.retail.stores.domain.lineitem.SaleReturnLineItemIfc;
import oracle.retail.stores.domain.manager.payment.AuthorizationConstantsIfc.TenderType;
import oracle.retail.stores.domain.manager.payment.AuthorizeRequestIfc;
import oracle.retail.stores.domain.manager.payment.AuthorizeTransferRequestIfc;
import oracle.retail.stores.domain.manager.payment.AuthorizeTransferRequestIfc.RequestSubType;
import oracle.retail.stores.domain.stock.GiftCardPLUItemIfc;
import oracle.retail.stores.domain.stock.PLUItemIfc;
import oracle.retail.stores.domain.transaction.SaleReturnTransactionIfc;
import oracle.retail.stores.domain.utility.GiftCardIfc;
import oracle.retail.stores.domain.utility.GiftCardIfc.StatusCode;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.foundation.tour.ifc.ShuttleIfc;
import oracle.retail.stores.pos.ado.lineitem.LineItemTypeEnum;
import oracle.retail.stores.pos.services.sale.SaleCargoIfc;
import oracle.retail.stores.pos.services.tender.activation.ActivationCargo;

import org.apache.log4j.Logger;

/**
 * Finds activated gift cards to deactivate.
 * @since 13.4
 */
@SuppressWarnings("serial")
public class GiftCardDeactivationLaunchShuttle implements ShuttleIfc
{
    /**
     * The logger to which log messages will be sent.
     */
    protected static Logger logger = Logger.getLogger(oracle.retail.stores.pos.services.postvoid.GiftCardDeactivationLaunchShuttle.class);

    /**
     * List of AuthorizeTransferRequestIfc objects
     */
    protected List<AuthorizeTransferRequestIfc> requestList;

    /**
     * handle to the transaction.
     */
    protected SaleReturnTransactionIfc transaction;

    /**
     * Handle to the register object to provide the context.
     * @since 14.0
     */
    protected RegisterIfc register;

    /*
     * (non-Javadoc)
     * @see oracle.retail.stores.foundation.tour.ifc.ShuttleIfc#load(oracle.retail.stores.foundation.tour.ifc.BusIfc)
     */
    public void load(BusIfc bus)
    {
        SaleCargoIfc saleCargo = (SaleCargoIfc) bus.getCargo();
        register = saleCargo.getRegister();
        transaction = saleCargo.getTransaction();
        SaleReturnLineItemIfc[] giftCardLineItems = transaction.getProductGroupLineItems(LineItemTypeEnum.TYPE_GIFT_CARD.toString());
        requestList = new ArrayList<AuthorizeTransferRequestIfc>();
        if(giftCardLineItems != null)
        {
            for(int i = 0; i < giftCardLineItems.length; i++)
            {
                SaleReturnLineItemIfc item = giftCardLineItems[i];
                PLUItemIfc pluItem = item.getPLUItem();
                if(pluItem instanceof GiftCardPLUItemIfc)
                {
                    GiftCardPLUItemIfc giftCardPLUItem = (GiftCardPLUItemIfc)pluItem;
                    if(StatusCode.Active.equals(giftCardPLUItem.getGiftCard().getStatus()))
                    {
                        // create instance of the request object
                        AuthorizeTransferRequestIfc request = DomainGateway.getFactory().getAuthorizeTransferRequestInstance();
                        // populate the request with the necessary information
                        request.setWorkstation(saleCargo.getRegister().getWorkstation());
                        request.setAccountNumber(giftCardPLUItem.getGiftCard().getCardNumber());
                        request.setEntryMethod(giftCardPLUItem.getGiftCard().getEntryMethod());
                        switch(giftCardPLUItem.getGiftCard().getRequestType())
                        {
                            case GiftCardIfc.GIFT_CARD_ISSUE_REVERSE :
                                request.setRequestSubType(RequestSubType.Deactivate);
                                break;
                            case GiftCardIfc.GIFT_CARD_RELOAD_REVERSE :
                                request.setRequestSubType(RequestSubType.ReloadVoid);
                                break;
                            default :
                                logger.error("Unknown request type: " + giftCardPLUItem.getGiftCard().getRequestType());
                                break;
                        }
                        request.setRequestTenderType(TenderType.GIFT_CARD);
                        request.setRequestType(AuthorizeRequestIfc.RequestType.GiftCard);
                        request.setBaseAmount(pluItem.getPrice().abs());
                        // add it to the list
                        requestList.add(request);
                    }
                }
            }
        }
    }

    /*
     * (non-Javadoc)
     * @see oracle.retail.stores.foundation.tour.ifc.ShuttleIfc#unload(oracle.retail.stores.foundation.tour.ifc.BusIfc)
     */
    public void unload(BusIfc bus)
    {
        ActivationCargo activationCargo = (ActivationCargo)bus.getCargo();
        activationCargo.setRegister(register);
        activationCargo.setTransaction(transaction);
        activationCargo.setRequestList(requestList);
    }

}
