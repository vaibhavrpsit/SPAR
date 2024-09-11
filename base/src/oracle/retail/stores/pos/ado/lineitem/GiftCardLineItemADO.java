/* ===========================================================================
* Copyright (c) 1998, 2013, Oracle and/or its affiliates. All rights reserved.
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/ado/lineitem/GiftCardLineItemADO.java /main/15 2013/09/05 10:36:15 abondala Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    abondala  09/04/13 - initialize collections
 *    blarsen   07/28/11 - Auth timeout parameters delete in 13.4. These were
 *                         moved into the payment technician layer.
 *    cgreene   07/26/11 - moved StatusCode to GiftCardifc
 *    cgreene   07/26/11 - removed tenderauth and giftcard.activation tours and
 *                         financialnetwork interfaces.
 *    asinton   06/29/11 - Refactored to use EntryMethod and
 *                         AuthorizationMethod enums.
 *    cgreene   05/27/11 - move auth response objects into domain
 *    cgreene   05/20/11 - implemented enums for reponse code and giftcard
 *                         status code
 *    cgreene   05/26/10 - convert to oracle packaging
 *    dwfung    03/10/10 - Handling Training Mode for Gift Inquiry Requests
 *    abondala  01/03/10 - update header date
 *
 * ===========================================================================
 * $Log:
 *  11   360Commerce 1.10        6/11/2008 5:14:23 PM   Alan N. Sinton  CR
 *       32014: Throw AuthorizationException from performInquiry so that the
 *       correct dialog is shown.
 *  10   360Commerce 1.9         6/10/2008 6:21:39 PM   Alan N. Sinton  CR
 *       32010: Fixed timeout for gift card inquiry. Code reviewed by Anda
 *       Cadar.
 *  9    360Commerce 1.8         4/21/2008 6:20:39 PM   Alan N. Sinton  CR
 *       31378: Fixed the logic in the isUsed method for support of the post
 *       void operation on gift card issue and reload transactions.  Code
 *       reviewed by Michael Barnett.
 *  8    360Commerce 1.7         4/14/2008 1:41:58 AM   Manas Sahu      The
 *       following condition checks if postvoid is of last reload then it
 *       should be allowed. To check that get the ItemTotal price of the
 *       PLUItem add that to the initial balance and compare that to current
 *       balance. In case of last reload the initial balance will be the
 *       amount before the reload, the PLUItem total price will be the last
 *       reload amount and the current balance will be initial balance before
 *       reload + the reload amount. e.g. Initial balance 100, first reload 25
 *        and second reload of 25 again. Now the last reload postvoid should
 *       be allowed. In that case when you try to postvoid the first reload
 *       this will return true as 100 + 25 != 150 (current). But if you try to
 *        postvoid the second reload then 125 + 25 = 150 and hence this
 *       condition will return false.
 *  7    360Commerce 1.6         3/25/2008 6:21:53 AM   Manas Sahu      Check
 *       the initial balance of the Transaction to be voided against the
 *       Current Balance of the Gift Card.
 *  6    360Commerce 1.5         3/11/2008 6:03:21 PM   Michael P. Barnett In
 *       getJournalMemento(), use truncated gift card account number rather
 *       than encrypted value.
 *  5    360Commerce 1.4         5/16/2007 4:00:28 PM   Owen D. Horne   CR25922
 *        - Merged fix from v8.0.1
 *       4    .v8x       1.2.1.0     4/19/2007 2:16:17 PM   Keith L. Lesikar
 *       Modified check to see if card has been used.
 *  4    360Commerce 1.3         4/25/2007 8:52:56 AM   Anda D. Cadar   I18N
 *       merge
 *
 *  3    360Commerce 1.2         3/31/2005 4:28:17 PM   Robert Pearse
 *  2    360Commerce 1.1         3/10/2005 10:21:54 AM  Robert Pearse
 *  1    360Commerce 1.0         2/11/2005 12:11:14 PM  Robert Pearse
 *
 * Revision 1.12  2004/09/23 00:07:11  kmcbride
 * @scr 7211: Adding static serialVersionUIDs to all POS Serializable objects, minus the JComponents
 *
 * Revision 1.11  2004/07/28 22:21:58  lzhao
 * @scr 6592: change for fit ISD.
 *
 * Revision 1.10  2004/07/17 16:00:28  lzhao
 * @scr 6315: void returned gift card
 *
 * Revision 1.9  2004/07/15 16:43:02  jdeleau
 * @scr 5421 Remove unused import
 *
 * Revision 1.8  2004/07/15 16:13:22  kmcbride
 * @scr 5954 (Services Impact): Adding logging to these ADOs, also fixed some exception handling issues.
 *
 * Revision 1.7  2004/07/14 20:30:42  lzhao
 * @scr 6257: check for null before use currentAmount
 *
 * Revision 1.6  2004/07/13 20:12:46  lzhao
 * @scr 6142: returned gift card cannot be voided.
 *
 * Revision 1.5  2004/06/14 18:53:05  kll
 * @scr 5072: do not journal an inquiry
 *
 * Revision 1.4  2004/05/11 16:05:28  blj
 * @scr 4603 - fixed for post void of giftcard issue/reload/redeem/credit
 *
 * Revision 1.3  2004/04/08 20:33:02  cdb
 * @scr 4206 Cleaned up class headers for logs and revisions.
 *
 *
 * Rev 1.4 Jan 21 2004 14:50:04 epd updated to use new GiftCardLineItemADOIfc
 *
 * Rev 1.3 Nov 13 2003 17:03:00 epd Refactoring: updated to use new method to
 * access context
 *
 * Rev 1.2 Nov 12 2003 10:07:18 rwh Added getContext() method to ADO base class
 * Resolution for Foundation SCR-265: Add ADOContext reference to ADO base
 * class
 *
 * Rev 1.1 Nov 12 2003 09:26:34 rwh Added setChildContexts() method Resolution
 * for Foundation SCR-265: Add ADOContext reference to ADO base class
 *
 * Rev 1.0 Nov 04 2003 11:11:42 epd Initial revision.
 *
 * Rev 1.1 Oct 30 2003 18:05:48 epd commented out broken code for the time
 * being.
 *
 * Rev 1.0 Oct 17 2003 12:31:56 epd Initial revision.
 * ===========================================================================
 */
package oracle.retail.stores.pos.ado.lineitem;

import java.util.HashMap;
import java.util.Map;

import oracle.retail.stores.commerceservices.common.currency.CurrencyIfc;
import oracle.retail.stores.domain.lineitem.SaleReturnLineItemIfc;
import oracle.retail.stores.domain.stock.GiftCardPLUItem;
import oracle.retail.stores.domain.utility.EYSDomainIfc;
import oracle.retail.stores.domain.utility.GiftCardIfc;
import oracle.retail.stores.domain.utility.GiftCardIfc.StatusCode;
import oracle.retail.stores.pos.ado.ADO;
import oracle.retail.stores.pos.ado.journal.JournalableADOIfc;
import oracle.retail.stores.pos.ado.journal.LineItemFormatter;

import org.apache.log4j.Logger;

/**
 * GiftCardLineItemADO represents the ADO for Gift Card Line Item.
 * @version $Revision: /main/15 $
 */
public class GiftCardLineItemADO
    extends ADO
    implements GiftCardLineItemADOIfc, JournalableADOIfc
{
    // This id is used to tell
    // the compiler not to generate a
    // new serialVersionUID.
    //
    static final long serialVersionUID = 7476796259437750844L;

    /** The RDO object */
    protected SaleReturnLineItemIfc giftCardLineItemRDO;

    /**
     *  our logger
     **/
    protected transient Logger logger = Logger.getLogger(GiftCardLineItemADO.class);

    /**
     * No-arg constructor NOTE: protected by design so that factory must be
     * used for instantiation.
     */
    protected GiftCardLineItemADO()
    {
    }

    /**
     * Convenience method to get Gift Card PLU from line item
     *
     * @return
     */
    protected GiftCardIfc getGiftCard()
    {
        GiftCardPLUItem giftCardPLU =
            (GiftCardPLUItem) giftCardLineItemRDO.getPLUItem();
        return giftCardPLU.getGiftCard();
    }

    /**
     * Returns the Gift Card status
     *
     * @return
     */
    public StatusCode getStatus()
    {
        return getGiftCard().getStatus();
    }

    /**
     * If the current balance is different from the initial balance then this
     * card has been used unless the card has been reloaded.
     *
     * @param gift card
     *            The gift card from ISD
     * @return boolean flag indicating usage
     */
    public boolean isUsed(GiftCardIfc giftCard)
    {
        boolean returnValue = true;
        // Gift Card Issue
        if ( giftCard != null &&
             giftCard.getCurrentBalance() != null &&
             giftCard.getRequestType() != GiftCardIfc.GIFT_CARD_RETURN )
        {
            // get the actual intial balance from this GiftCardLineItemADO
            CurrencyIfc initial = getGiftCard().getInitialBalance();
            // get the actual current balance from the passed giftCard
            CurrencyIfc current = giftCard.getCurrentBalance();
            // get the item price from this
            CurrencyIfc price   = giftCardLineItemRDO.getItemPrice().getItemTotal();

            // if initial == price == curent, first Issue of gift card
            if( initial.compareTo(current) == CurrencyIfc.EQUALS &&
                initial.compareTo(price) == CurrencyIfc.EQUALS)
            {
                returnValue = false;
            }

            // if the update was strictly because of a reload, allow the post void
            else if (initial.add(price).compareTo(current) == CurrencyIfc.EQUALS)
            {
                returnValue = false;
            }
        }
        return returnValue;
    }

    /*
     * (non-Javadoc)
     *
     * @see oracle.retail.stores.ado.journal.JournalableADOIfc#getJournalMemento()
     */
    public Map getJournalMemento()
    {
        HashMap<String, String> map = new HashMap<String, String>(1);

        map.put(LineItemFormatter.ID, getGiftCard().getEncipheredCardData().getTruncatedAcctNumber());

        return map;
    }

    /**
     * @see oracle.retail.stores.ado.ADOIfc#toLegacy()
     */
    public EYSDomainIfc toLegacy()
    {
        return giftCardLineItemRDO;
    }

    /**
     * @see oracle.retail.stores.ado.ADOIfc#toLegacy(java.lang.Class)
     */
    public EYSDomainIfc toLegacy(Class type)
    {
        return toLegacy();
    }

    /**
     * @see oracle.retail.stores.ado.ADOIfc#fromLegacy(oracle.retail.stores.domain.utility.EYSDomainIfc)
     */
    public void fromLegacy(EYSDomainIfc rdo)
    {
        giftCardLineItemRDO = (SaleReturnLineItemIfc) rdo;
    }
}
