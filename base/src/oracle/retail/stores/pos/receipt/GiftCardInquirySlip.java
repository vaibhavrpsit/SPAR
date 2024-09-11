/* ===========================================================================
* Copyright (c) 2008, 2011, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/receipt/GiftCardInquirySlip.java /rgbustores_13.4x_generic_branch/1 2011/07/26 11:52:18 cgreene Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    cgreene   07/26/11 - removed tenderauth and giftcard.activation tours and
 *                         financialnetwork interfaces.
 *    cgreene   02/15/11 - move constants into interfaces and refactor
 *    acadar    06/10/10 - use default locale for currency display
 *    acadar    06/09/10 - XbranchMerge acadar_tech30 from
 *                         st_rgbustores_techissueseatel_generic_branch
 *    cgreene   05/26/10 - convert to oracle packaging
 *    cgreene   04/26/10 - XbranchMerge cgreene_tech43 from
 *                         st_rgbustores_techissueseatel_generic_branch
 *    cgreene   04/02/10 - remove deprecated LocaleContantsIfc and currencies
 *    abondala  01/03/10 - update header date
 *    cgreene   11/13/08 - configure print beans into Spring context
 *    cgreene   10/28/08 - implement GiftCardInquirySlip as a receipt blueprint
 *    cgreene   09/19/08 - updated with changes per FindBugs findings
 *    cgreene   09/11/08 - update header
 *
 * ===========================================================================
 * $Log:
 *  9    360Commerce 1.8         3/27/2008 12:32:44 AM  Manikandan Chellapan
 *       CR#30967 Fixed Gift Card Inquiry receipt format errors. Code reviewed
 *        by anil kandru.
 *  8    360Commerce 1.7         3/19/2008 11:40:06 PM  Manikandan Chellapan
 *       CR#30967 Modified code to print truncated card number instead of
 *       clear text card number
 *  7    360Commerce 1.6         7/11/2007 11:07:29 AM  Anda D. Cadar   removed
 *        ISO currency code when using base currency
 *  6    360Commerce 1.5         5/24/2007 6:01:31 PM   Owen D. Horne
 *       CR#25639 - Merged fix from v8.0.1
 *       *  4    .v8x       1.2.2.0     4/19/2007 9:24:59 AM   Keith L.
 *       Lesikar New
 *       *       spec and simulator modified to render proper initial amounts.
 *  5    360Commerce 1.4         4/30/2007 7:01:38 PM   Alan N. Sinton  CR
 *       26485 - Merge from v12.0_temp.
 *  4    360Commerce 1.3         4/25/2007 8:52:40 AM   Anda D. Cadar   I18N
 *       merge
 *
 *  3    360Commerce 1.2         3/31/2005 4:28:16 PM   Robert Pearse
 *  2    360Commerce 1.1         3/10/2005 10:21:54 AM  Robert Pearse
 *  1    360Commerce 1.0         2/11/2005 12:11:13 PM  Robert Pearse
 * $
 * Revision 1.6  2004/06/29 19:59:03  lzhao
 * @scr 5477: add gift card inquiry in training mode.
 *
 * Revision 1.5  2004/04/09 16:55:59  cdb
 * @scr 4302 Removed double semicolon warnings.
 *
 * Revision 1.4  2004/03/30 20:34:12  bwf
 * @scr 4165 Gift Card Rework
 *
 * Revision 1.3  2004/02/12 16:48:43  mcs
 * Forcing head revision
 *
 * Revision 1.2  2004/02/11 21:34:38  rhafernik
 * @scr 0 Log4J conversion and code cleanup
 *
 * Revision 1.1.1.1  2004/02/11 01:04:13  cschellenger
 * updating to pvcs 360store-current
 *
 *
 *
 *    Rev 1.2   Feb 04 2004 11:45:18   lzhao
 * add some tags for printing gift card receipt
 * Resolution for 3371: Feature Enhancement:  Gift Card Enhancement
 *
 *    Rev 1.1   Dec 08 2003 09:01:38   lzhao
 * remove expirationDate.
 * Resolution for 3371: Feature Enhancement:  Gift Card Enhancement
 *
 *    Rev 1.0   Aug 29 2003 15:51:46   CSchellenger
 * Initial revision.
 *
 *    Rev 1.5   May 14 2003 11:23:38   KLL
 * address null EYSDate
 * Resolution for POS SCR-2310: Lock Up - When Print Gift Card Balance with GC Expiration Parameter set to NO
 *
 *    Rev 1.4   May 01 2003 14:46:18   KLL
 * inquiry slip title correction and more line feeds
 * Resolution for POS SCR-2255: Gift Card Inquiry Slip- Missing Info
 *
 *    Rev 1.3   Apr 16 2003 08:16:06   KLL
 * Prompt to print gift card inquiry slip
 * Resolution for POS SCR-2129: Gift Card - Print Balance- Missing Print Button
 *
 *    Rev 1.2   Mar 07 2003 17:11:04   baa
 * code review changes for I18n
 * Resolution for POS SCR-1740: Code base Conversions
 *
 *    Rev 1.1   Sep 26 2002 15:48:38   kmorneau
 * print an inquiry slip for customer
 * Resolution for 1816: Gift Card Balance Slip
 *
 *    Rev 1.0   Sep 25 2002 09:37:34   kmorneau
 * Initial revision.
 * Resolution for 1816: Gift Card Balance Slip
 * ===========================================================================
 */
package oracle.retail.stores.pos.receipt;

import oracle.retail.stores.domain.utility.GiftCardIfc;

/**
 * This class represents the balance slip that is printed when doing a gift card
 * inquiry. The requirements are found in the printing functional requirements.
 * 
 * @version $Revision: /rgbustores_13.4x_generic_branch/1 $
 * @since 5.2.0
 */
public class GiftCardInquirySlip extends PrintableDocumentParameterBean
{
    private static final long serialVersionUID = 5739713369450086445L;

    /**
     * The gift card being reported upon.
     */
    private GiftCardIfc giftCard;

    /**
     * Constructor GiftCardInquirySlip.
     */
    public GiftCardInquirySlip()
    {
        this(null);
    }

    /**
     * Constructor GiftCardInquirySlip.
     *
     * @param giftCard
     */
    public GiftCardInquirySlip(GiftCardIfc giftCard)
    {
        setDocumentType(ReceiptTypeConstantsIfc.GIFTCARD_INQUIRY);
        setGiftCard(giftCard);
    }

    /**
     * Set the gift card.
     *
     * @param giftCard
     */
    public void setGiftCard(GiftCardIfc giftCard)
    {
        this.giftCard = giftCard;
    }

    /**
     * Return the gift card being printed about.
     * @return
     */
    public GiftCardIfc getGiftCard()
    {
        return giftCard;
    }
}
