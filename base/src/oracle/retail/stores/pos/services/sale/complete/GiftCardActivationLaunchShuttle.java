/* ===========================================================================
* Copyright (c) 1998, 2012, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/sale/complete/GiftCardActivationLaunchShuttle.java /main/14 2012/09/12 11:57:20 blarsen Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    asinton   05/21/12 - transfer sales associate from salesCargo to
 *                         activationCargo to prevent NPE while journaling in
 *                         ActivationSite.
 *    asinton   05/08/12 - fixed issue with re-activating giftcards when 1st
 *                         attempt fails.
 *    asinton   04/02/12 - carry the register object into the Activation tour.
 *    asinton   02/15/12 - include transactionID in the gift card activation
 *                         requests.
 *    jswan     10/21/11 - Modified to support changing the gift card number
 *                         during activation. No longer depends and matching
 *                         the masked account number, which maybe masked with
 *                         different characters or number of digits.
 *    jswan     09/06/11 - Fixed issues with gift card balance when
 *                         issuing/reloading multiple gift cards and one card
 *                         fails.
 *    asinton   08/18/11 - added journaling of activation, deactivation,
 *                         inquiring to tender.activation service.
 *    ohorne    08/09/11 - APF:foreign currency support
 *    asinton   06/29/11 - Refactored to use EntryMethod and
 *                         AuthorizationMethod enums.
 *    cgreene   06/28/11 - refactor out tenderId in favor of token and
 *                         accountNumber
 *    cgreene   06/15/11 - implement gift card for servebase and training mode
 *    cgreene   05/27/11 - move auth response objects into domain
 *    blarsen   05/20/11 - Changed TenderType from int constants to a real
 *                         enum.
 *    sgu       05/05/11 - set request type for gift card authorize request
 *    asinton   04/26/11 - Refactor gift card for APF
 *    cgreene   05/26/10 - convert to oracle packaging
 *    abondala  01/03/10 - update header date
 *
 * ===========================================================================
 * $Log:
 *    5    360Commerce 1.4         6/18/2008 4:12:37 PM   Jack G. Swan    A
 *         similar crash (reloading a gift card with a debit card) was
 *         discovered by ISC.  This code was written by an ISC developer and
 *         reviewed/checkin by Jack Swan.
 *    4    360Commerce 1.3         2/10/2006 11:06:44 AM  Deepanshu       CR
 *         6092: Sales Assoc sould be last 4 digits of Sales Assoc ID and not
 *         of Cashier ID on the recipt
 *    3    360Commerce 1.2         3/31/2005 4:28:16 PM   Robert Pearse
 *    2    360Commerce 1.1         3/10/2005 10:21:52 AM  Robert Pearse
 *    1    360Commerce 1.0         2/11/2005 12:11:12 PM  Robert Pearse
 *
 *   Revision 1.4  2004/09/23 00:07:16  kmcbride
 *   @scr 7211: Adding static serialVersionUIDs to all POS Serializable objects, minus the JComponents
 *
 *   Revision 1.3  2004/02/12 16:48:18  mcs
 *   Forcing head revision
 *
 *   Revision 1.2  2004/02/11 21:28:20  rhafernik
 *   @scr 0 Log4J conversion and code cleanup
 *
 *   Revision 1.1.1.1  2004/02/11 01:04:11  cschellenger
 *   updating to pvcs 360store-current
 *
 *
 *
 *    Rev 1.3   Jan 30 2004 14:11:34   lzhao
 * add register and storeStatus for reload reversing tender.
 * Resolution for 3371: Feature Enhancement:  Gift Card Enhancement
 *
 *    Rev 1.2   Nov 26 2003 09:14:28   lzhao
 * remove tendering
 * Resolution for 3371: Feature Enhancement:  Gift Card Enhancement
 *
 *    Rev 1.1   08 Nov 2003 01:20:58   baa
 * cleanup -sale refactoring
 *
 *    Rev 1.0   Nov 05 2003 13:08:28   rsachdeva
 * Initial revision.
 * Resolution for POS SCR-3430: Sale Service Refactoring
 * ===========================================================================
 */
package oracle.retail.stores.pos.services.sale.complete;

import java.util.ArrayList;
import java.util.List;

import oracle.retail.stores.domain.DomainGateway;
import oracle.retail.stores.domain.employee.EmployeeIfc;
import oracle.retail.stores.domain.financial.RegisterIfc;
import oracle.retail.stores.domain.lineitem.SaleReturnLineItemIfc;
import oracle.retail.stores.domain.manager.payment.AuthorizationConstantsIfc.TenderType;
import oracle.retail.stores.domain.manager.payment.AuthorizeRequestIfc;
import oracle.retail.stores.domain.manager.payment.AuthorizeTransferRequestIfc;
import oracle.retail.stores.domain.manager.payment.AuthorizeTransferRequestIfc.RequestSubType;
import oracle.retail.stores.domain.stock.GiftCardPLUItemIfc;
import oracle.retail.stores.domain.stock.PLUItemIfc;
import oracle.retail.stores.domain.stock.ProductGroupConstantsIfc;
import oracle.retail.stores.domain.transaction.SaleReturnTransactionIfc;
import oracle.retail.stores.domain.utility.GiftCardIfc;
import oracle.retail.stores.domain.utility.GiftCardIfc.StatusCode;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.foundation.tour.ifc.ShuttleIfc;
import oracle.retail.stores.pos.services.sale.SaleCargo;
import oracle.retail.stores.pos.services.tender.activation.ActivationCargo;

import org.apache.log4j.Logger;

/**
 * This shuttle copies information from the cargo used
 * in the Sale service to the cargo used in the Gift Card Activation service.
 */
public class GiftCardActivationLaunchShuttle implements ShuttleIfc
{
    /**
     * serialVersionUID
     */
    private static final long serialVersionUID = -4354971620529895640L;

    /**
     * Logger
     */
    private static final Logger logger = Logger.getLogger(GiftCardActivationLaunchShuttle.class);

    /**
     * List of AuthorizeTransferRequestIfc objects
     */
    protected List<AuthorizeTransferRequestIfc> requestList;

    /**
     * List of line item sequence numbers that correspond to gift cards in the request list.
     * This required in case the gift card activate is declined and another gift card is substituted
     * for the original. 
     */
    protected List<Integer> lineNumberList;

    /**
     * Transaction
     */
    protected SaleReturnTransactionIfc transaction;

    /**
     * Handle to the Register object.
     */
    protected RegisterIfc register;

    /**
     * Handle to the Employee object.
     */
    protected EmployeeIfc salesAssociate;

    /* (non-Javadoc)
     * @see oracle.retail.stores.foundation.tour.ifc.ShuttleIfc#load(oracle.retail.stores.foundation.tour.ifc.BusIfc)
     */
    @Override
    public void load(BusIfc bus)
    {
        SaleCargo saleCargo = (SaleCargo)bus.getCargo();
        transaction = saleCargo.getTransaction();
        register = saleCargo.getRegister();
        salesAssociate = saleCargo.getSalesAssociate();
        SaleReturnLineItemIfc[] giftCardLineItems = transaction.getProductGroupLineItems(ProductGroupConstantsIfc.PRODUCT_GROUP_GIFT_CARD);
        requestList = new ArrayList<AuthorizeTransferRequestIfc>();
        lineNumberList = new ArrayList<Integer>();
        if(giftCardLineItems != null)
        {
            for(int i = 0; i < giftCardLineItems.length; i++)
            {
                SaleReturnLineItemIfc item = giftCardLineItems[i];
                PLUItemIfc pluItem = item.getPLUItem();
                if(pluItem instanceof GiftCardPLUItemIfc)
                {
                    GiftCardPLUItemIfc giftCardPLUItem = (GiftCardPLUItemIfc)pluItem;
                    GiftCardIfc giftCard = giftCardPLUItem.getGiftCard();
                    if(!StatusCode.Active.equals(giftCard.getStatus()))
                    {
                        // create instance of the request object
                        AuthorizeTransferRequestIfc request = DomainGateway.getFactory().getAuthorizeTransferRequestInstance();
                        // populate the request with the necessary information
                        request.setWorkstation(saleCargo.getRegister().getWorkstation());
                        request.setAccountNumber(giftCardPLUItem.getGiftCard().getCardNumber());
                        request.setEntryMethod(giftCardPLUItem.getGiftCard().getEntryMethod());
                        request.setTransactionID(transaction.getTransactionIdentifier().getTransactionIDString());
                        switch(giftCardPLUItem.getGiftCard().getRequestType())
                        {
                            case GiftCardIfc.GIFT_CARD_ISSUE :
                                request.setRequestSubType(RequestSubType.Activate);
                                break;
                            case GiftCardIfc.GIFT_CARD_RELOAD :
                                request.setRequestSubType(RequestSubType.ReloadGiftCard);
                                break;
                            default :
                                logger.error("Unknown request type: " + giftCardPLUItem.getGiftCard().getRequestType());
                                break;
                        }
                        request.setRequestTenderType(TenderType.GIFT_CARD);
                        if (giftCardPLUItem.getGiftCard().getReqestedAmount() != null)
                        {
                            request.setBaseAmount(giftCardPLUItem.getGiftCard().getReqestedAmount().abs());
                        }
                        request.setRequestType(AuthorizeRequestIfc.RequestType.GiftCard);
                        // add it to the list
                        requestList.add(request);
                        lineNumberList.add(item.getLineNumber());
                    }
                }
            }
        }
    }

    /* (non-Javadoc)
     * @see oracle.retail.stores.foundation.tour.ifc.ShuttleIfc#unload(oracle.retail.stores.foundation.tour.ifc.BusIfc)
     */
    @Override
    public void unload(BusIfc bus)
    {
        ActivationCargo activationCargo = (ActivationCargo)bus.getCargo();
        activationCargo.setRequestList(requestList);
        activationCargo.setTransaction(transaction);
        activationCargo.setRegister(register);
        activationCargo.setSalesAssociate(salesAssociate);
        activationCargo.setLineNumberList(lineNumberList);
    }
}

