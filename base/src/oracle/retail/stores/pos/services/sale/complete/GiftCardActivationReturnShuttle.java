/* ===========================================================================
* Copyright (c) 1998, 2014, Oracle and/or its affiliates. All rights reserved.
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/sale/complete/GiftCardActivationReturnShuttle.java /main/14 2014/02/10 15:44:37 asinton Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    asinton   02/06/14 - reworked flow for gift card activation error
 *                         scenarios
 *    icole     02/28/13 - Forward Port Print trace number on receipt for gift
 *                         cards, required by ACI.
 *    asinton   05/08/12 - fixed issue with re-activating giftcards when 1st
 *                         attempt fails.
 *    jswan     09/06/11 - Fixed issues with gift card balance when
 *                         issuing/reloading multiple gift cards and one card
 *                         fails.
 *    sgu       07/19/11 - donot throw parse exception for datetime translation
 *    cgreene   05/27/11 - move auth response objects into domain
 *    asinton   04/26/11 - Refactor gift card for APF
 *    cgreene   05/26/10 - convert to oracle packaging
 *    abondala  01/03/10 - update header date
 *
 * ===========================================================================
 * $Log:
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
 *    Rev 1.2   Nov 26 2003 09:14:26   lzhao
 * remove tendering
 * Resolution for 3371: Feature Enhancement:  Gift Card Enhancement
 *
 *    Rev 1.1   08 Nov 2003 01:21:02   baa
 * cleanup -sale refactoring
 *
 *    Rev 1.0   Nov 05 2003 13:05:52   rsachdeva
 * Initial revision.
 * Resolution for POS SCR-3430: Sale Service Refactoring
 * ===========================================================================
 */
package oracle.retail.stores.pos.services.sale.complete;

// foundation imports
import java.util.Arrays;
import java.util.List;

import oracle.retail.stores.domain.lineitem.SaleReturnLineItemIfc;
import oracle.retail.stores.domain.manager.payment.AuthorizeTransferResponseIfc;
import oracle.retail.stores.domain.stock.GiftCardPLUItemIfc;
import oracle.retail.stores.domain.stock.PLUItemIfc;
import oracle.retail.stores.domain.transaction.SaleReturnTransactionIfc;
import oracle.retail.stores.domain.utility.EYSDate;
import oracle.retail.stores.domain.utility.GiftCardIfc;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.foundation.tour.ifc.ShuttleIfc;
import oracle.retail.stores.pos.services.sale.SaleCargoIfc;
import oracle.retail.stores.pos.services.tender.activation.ActivationCargo;

import org.apache.log4j.Logger;

/**
 * This shuttle copies information from the cargo used
 * in the GiftCardActivation service to the cargo used in the POS service.
 */
@SuppressWarnings("serial")
public class GiftCardActivationReturnShuttle implements ShuttleIfc
{                                       // begin class GiftCardActivationReturnShuttle()
    /**
     * List of AuthorizeTransferResponseIfc from the Authorization Service.
     */
    protected List<AuthorizeTransferResponseIfc> responseList;
    /**
     * List of line numbers from the activation tour.
     */
    protected List<Integer> lineNumbersList;

    protected List<Integer> failedNumbersList;
    /*
     * (non-Javadoc)
     * @see oracle.retail.stores.foundation.tour.ifc.ShuttleIfc#load(oracle.retail.stores.foundation.tour.ifc.BusIfc)
     */
    public void load(BusIfc bus)
    {
        ActivationCargo activationCargo = (ActivationCargo)bus.getCargo();
        responseList = activationCargo.getResponseList();
        lineNumbersList = activationCargo.getLineNumberList();
        failedNumbersList = activationCargo.getFailedLineNumbersList();
    }

    /*
     * (non-Javadoc)
     * @see oracle.retail.stores.foundation.tour.ifc.ShuttleIfc#unload(oracle.retail.stores.foundation.tour.ifc.BusIfc)
     */
    public void unload(BusIfc bus)
    {
        if(responseList != null && !responseList.isEmpty())
        {
            SaleCargoIfc saleCargo = (SaleCargoIfc)bus.getCargo();
            SaleReturnTransactionIfc transaction = saleCargo.getTransaction();
            SaleReturnLineItemIfc lineItem = null;
            // retrieve the response from the list
            for(int index = 0; index < responseList.size() && index < lineNumbersList.size(); index++)
            {
                // get the response and it's corresponding line number
                AuthorizeTransferResponseIfc response = responseList.get(index);
                int lineNumber = lineNumbersList.get(index);

                // get the line item for the line number
                lineItem = (SaleReturnLineItemIfc)transaction.getLineItems()[lineNumber];
                PLUItemIfc pluItem = lineItem.getPLUItem();
                // update the giftcard PLU object
                if(pluItem instanceof GiftCardPLUItemIfc)
                {
                    GiftCardPLUItemIfc giftCardPLUItem = (GiftCardPLUItemIfc)pluItem;
                    updateGiftCard(giftCardPLUItem.getGiftCard(), response);                    
                }
            }
            // remove failed gift card activations
            if(!failedNumbersList.isEmpty())
            {
                int[] failedNumbersArray = new int[failedNumbersList.size()];
                for(int i = 0; i < failedNumbersArray.length; i++)
                {
                    failedNumbersArray[i] = failedNumbersList.get(i);
                }
                transaction.removeLineItems(failedNumbersArray);
            }
        }
    }

    /**
     * This method updates the gift card with the values from the response.
     * @param giftCard
     * @param response
     */
    protected void updateGiftCard(GiftCardIfc giftCard, AuthorizeTransferResponseIfc response)
    {
        EYSDate eysDate = response.getResponseTime();
        giftCard.setAuthorizedDateTime(eysDate);
        giftCard.setApprovalCode(response.getApprovalCode());
        giftCard.setStatus(response.getStatus());
        giftCard.setCurrentBalance(response.getCurrentBalance());
        giftCard.setTraceNumber(response.getTraceNumber());
    }
}                                       // end class GiftCardActivationReturnShuttle
