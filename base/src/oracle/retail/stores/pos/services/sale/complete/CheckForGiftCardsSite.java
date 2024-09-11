/* ===========================================================================
* Copyright (c) 1998, 2014, Oracle and/or its affiliates. All rights reserved.
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/sale/complete/CheckForGiftCardsSite.java /main/12 2014/02/24 14:03:36 asinton Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    asinton   02/20/14 - added flag to suppress gift card activations in the
 *                         sale complete tour when a call referral was
 *                         performed
 *    cgreene   08/09/11 - formatting and removed deprecated code
 *    cgreene   05/26/10 - convert to oracle packaging
 *    abondala  01/03/10 - update header date
 *
 * ===========================================================================
 * $Log:
 *  3    360Commerce 1.2         3/31/2005 4:27:24 PM   Robert Pearse   
 *  2    360Commerce 1.1         3/10/2005 10:20:08 AM  Robert Pearse   
 *  1    360Commerce 1.0         2/11/2005 12:09:55 PM  Robert Pearse   
 * $
 * Revision 1.7  2004/07/16 22:12:05  epd
 * @scr 4268 Changing flows to add gift card credit
 *
 * Revision 1.6  2004/03/26 21:32:18  cdb
 * @scr 4204 Removing Tabs.
 *
 * Revision 1.5  2004/03/26 20:48:46  bjosserand
 * @scr 4093 Transaction Reentry
 * Revision 1.4 2004/03/03 23:15:12 bwf @scr 0 Fixed CommonLetterIfc deprecations.
 * 
 * Revision 1.3 2004/02/12 16:48:18 mcs Forcing head revision
 * 
 * Revision 1.2 2004/02/11 21:28:20 rhafernik @scr 0 Log4J conversion and code cleanup
 * 
 * Revision 1.1.1.1 2004/02/11 01:04:11 cschellenger updating to pvcs 360store-current
 * 
 * 
 * 
 * Rev 1.1 08 Nov 2003 01:20:34 baa cleanup -sale refactoring
 * 
 * ===========================================================================
 */
package oracle.retail.stores.pos.services.sale.complete;

import oracle.retail.stores.pos.services.common.CommonLetterIfc;
import oracle.retail.stores.pos.services.sale.SaleCargoIfc;
import oracle.retail.stores.domain.lineitem.SaleReturnLineItemIfc;
import oracle.retail.stores.domain.stock.ProductGroupConstantsIfc;
import oracle.retail.stores.domain.tender.TenderLineItemIfc;
import oracle.retail.stores.domain.transaction.RetailTransactionIfc;
import oracle.retail.stores.foundation.tour.application.Letter;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.foundation.tour.ifc.LetterIfc;
import oracle.retail.stores.pos.services.PosSiteActionAdapter;

/**
 * This site Checks at least one gift card exists. If there is gift card this mails gift card letter for
 * ActivationStation. If no gift card exists this mails continue letter for Printing Station.
 * 
 * @version $Revision: /main/12 $
 */
@SuppressWarnings("serial")
public class CheckForGiftCardsSite extends PosSiteActionAdapter
{
    /**
     * revision number supplied by source-code-control system
     */
    public static final String revisionNumber = "$Revision: /main/12 $";

    /**
     * Checks for the ActivationStation/Printing Station.
     * 
     * @param bus Service Bus
     */
    @Override
    public void arrive(BusIfc bus)
    {
        LetterIfc letter = new Letter(CommonLetterIfc.GIFTCARD);
        SaleCargoIfc cargo = (SaleCargoIfc) bus.getCargo();

        boolean transactionReentryMode = cargo.getRegister().getWorkstation().isTransReentryMode();

        // We must check for both Gift Card items and tenders
        RetailTransactionIfc retailTransaction = cargo.getRetailTransaction();
        SaleReturnLineItemIfc[] items =
            retailTransaction.getProductGroupLineItems(ProductGroupConstantsIfc.PRODUCT_GROUP_GIFT_CARD);
        
        TenderLineItemIfc[] tenders = retailTransaction.getTenderLineItems();
        boolean hasGiftCardTender = false;
        for (int i=0; i<tenders.length; i++)
        {
            if (tenders[i].getTypeCode() == TenderLineItemIfc.TENDER_TYPE_GIFT_CARD)
            {
                hasGiftCardTender = true;
                break;
            }
        }
        if ((items == null && hasGiftCardTender == false) ||
            transactionReentryMode || // skip activation of trans. reentry mode
            cargo.isSuppressGiftCardActivation()) // manually activated from within MPOS UI 
        {
            letter = new Letter(CommonLetterIfc.CONTINUE);
        }
        bus.mail(letter, BusIfc.CURRENT);
    }
}
