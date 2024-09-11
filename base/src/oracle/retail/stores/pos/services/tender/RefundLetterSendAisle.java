/* ===========================================================================
* Copyright (c) 1998, 2012, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/tender/RefundLetterSendAisle.java /main/13 2012/11/26 09:21:02 jswan Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    asinton   10/20/14 - Prevent null pointer exception by testing
 *                         cargo.getOriginalReturnTxnADOs() before calling
 *                         cargo.getOriginalReturnTxnADOs().length.
 *    yiqzhao   10/17/14 - Calculate refund options row based on original
 *                         transaction tender types.
 *    jswan     11/15/12 - Modified to support parameter controlled return
 *                         tenders.
 *    sgu       10/17/11 - prompt for card swipe or manual entry once card
 *                         tender buttons are clicked
 *    sgu       09/08/11 - add house account as a refund tender
 *    cgreene   05/26/10 - convert to oracle packaging
 *    abondala  01/03/10 - update header date
 *
 * ===========================================================================
 * $Log:
 *    6    360Commerce 1.5         4/30/2008 1:53:28 PM   Maisa De Camargo
 *         31328 - Added a new scenario to the Refund Options Screen. The
 *         scenarios are described in the ORPOS_Tender.doc. In order to
 *         maintain the priority, I have shifted the values of the
 *         refundOptionsRow. Code Reviewed by Jack Swan.
 *    5    360Commerce 1.4         11/15/2007 9:15:02 AM  Christian Greene
 *         29135 - Fix client crash
 *    4    360Commerce 1.3         12/13/2005 4:42:35 PM  Barry A. Pape
 *         Base-lining of 7.1_LA
 *    3    360Commerce 1.2         3/31/2005 4:29:37 PM   Robert Pearse
 *    2    360Commerce 1.1         3/10/2005 10:24:37 AM  Robert Pearse
 *    1    360Commerce 1.0         2/11/2005 12:13:37 PM  Robert Pearse
 *
 *   Revision 1.1.2.1  2004/10/22 22:08:47  bwf
 *   @scr 7486, 7488 Made sure to get refund tenders during retrieve by customer.
 *
 *   Revision 1.1  2004/08/12 20:46:35  bwf
 *   @scr 6567, 6069 No longer have to swipe debit or credit for return if original
 *                               transaction tendered with one debit or credit.
 *
 * ===========================================================================
 */
package oracle.retail.stores.pos.services.tender;

import oracle.retail.stores.pos.ado.lineitem.TenderLineItemCategoryEnum;
import oracle.retail.stores.pos.ado.tender.TenderADOIfc;
import oracle.retail.stores.pos.ado.transaction.AbstractRetailTransactionADO;
import oracle.retail.stores.pos.ado.transaction.RetailTransactionADOIfc;
import oracle.retail.stores.domain.tender.TenderChargeIfc;
import oracle.retail.stores.foundation.tour.application.Letter;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.pos.services.PosLaneActionAdapter;
import oracle.retail.stores.pos.services.tender.tdo.RefundOptionsTDO;

//--------------------------------------------------------------------------
/**
    This class handles the next letter from the refund options screen.
    It mails the correct tender letter.
    $Revision: /main/13 $
 **/
//--------------------------------------------------------------------------
@SuppressWarnings("serial")
public class RefundLetterSendAisle extends PosLaneActionAdapter
{
    //----------------------------------------------------------------------
    /**
        This method mails the correct refund options letter.
        @param bus
        @see oracle.retail.stores.foundation.tour.ifc.LaneActionIfc#traverse(oracle.retail.stores.foundation.tour.ifc.BusIfc)
    **/
    //----------------------------------------------------------------------
    public void traverse(BusIfc bus)
    {
        TenderCargo cargo = (TenderCargo)bus.getCargo();
        cargo.setRefundToOriginalCard(true);
        RetailTransactionADOIfc trans = cargo.getCurrentTransactionADO();
        if (trans instanceof AbstractRetailTransactionADO)
        {
            ((AbstractRetailTransactionADO)trans).calculateRefundOptionsRow();
        }
        String nextTender = getRefundOptionsLetter(trans.getRefundOptionsRow());

        // get the original credit or debit tender to use for reversal
        if (nextTender.equals("Credit") ||
            nextTender.equals("HouseAccount"))
        {
            if (cargo.getOriginalReturnTxnADOs() != null && cargo.getOriginalReturnTxnADOs().length > 0
                    && cargo.getOriginalReturnTxnADOs()[0]
                            .getTenderLineItems(TenderLineItemCategoryEnum.POSITIVE_TENDERS).length > 0)
            {
                TenderADOIfc tenderADO = cargo.getOriginalReturnTxnADOs()[0]
                        .getTenderLineItems(TenderLineItemCategoryEnum.POSITIVE_TENDERS)[0];

                // The TenderCharge Object is shared amongst other objects in the cargo such as the
                // originalReturnTrxADOs
                // In order to change the nextTender (such as the amount from positive to negative)
                // and to keep the originalReturnTrxADOs intact, we are cloning the TenderCharge Object.
                tenderADO.fromLegacy((TenderChargeIfc) tenderADO.toLegacy().clone());
                cargo.setNextTender(tenderADO);
            }
        }
        bus.mail(new Letter(nextTender), BusIfc.CURRENT);
    }

    //----------------------------------------------------------------------
    /**
        This method gets what the next button should be mapped to.
        @param row
        @return string refund options letter.
    **/
    //----------------------------------------------------------------------
    protected String getRefundOptionsLetter(int row)
    {
        String returnLetter = null;
        // here we use the refund options row to determine which letter should be sent
        // this row correspond to the requirements row for refund options button availability.
        // The row represents a RefundOptions Scenario, described in the ORPOS_Tender.doc
        switch (row)
        {
            case RefundOptionsTDO.NEXT_FOR_CREDIT_REFUND:
                returnLetter = "Credit";
                break;
            case RefundOptionsTDO.NEXT_FOR_CASH_REFUND:
                returnLetter = "Cash";
                break;
            case RefundOptionsTDO.NEXT_FOR_HOUSE_ACCOUNT_REFUND:
                returnLetter = "HouseAccount";
                break;
            default:
                returnLetter = "Cash";
        }
        return returnLetter;
    }
}
