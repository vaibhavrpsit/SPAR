/* ===========================================================================
* Copyright (c) 1998, 2011, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/modules/domain/src/oracle/retail/stores/domain/ixretail/utility/LogGiftCard.java /rgbustores_13.4x_generic_branch/4 2011/07/07 12:20:05 cgreene Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    cgreene   07/07/11 - convert entryMethod to an enum
 *    asinton   06/29/11 - Refactored to use EntryMethod and
 *                         AuthorizationMethod enums.
 *    cgreene   05/20/11 - implemented enums for reponse code and giftcard
 *                         status code
 *    cgreene   05/26/10 - convert to oracle packaging
 *    cgreene   04/28/10 - updating deprecated names
 *    abondala  01/03/10 - update header date
 *
 * ===========================================================================
 * $Log:
 *    3    360Commerce 1.2         3/31/2005 4:28:54 PM   Robert Pearse   
 *    2    360Commerce 1.1         3/10/2005 10:23:12 AM  Robert Pearse   
 *    1    360Commerce 1.0         2/11/2005 12:12:23 PM  Robert Pearse   
 *
 *   Revision 1.3  2004/02/12 17:13:49  mcs
 *   Forcing head revision
 *
 *   Revision 1.2  2004/02/11 23:26:32  bwf
 *   @scr 0 Organize imports.
 *
 *   Revision 1.1.1.1  2004/02/11 01:04:31  cschellenger
 *   updating to pvcs 360store-current
 *
 *
 * 
 *    Rev 1.0   Aug 29 2003 15:36:52   CSchellenger
 * Initial revision.
 * 
 *    Rev 1.2   Feb 15 2003 14:52:08   mpm
 * Merged 5.1 changes.
 * Resolution for Domain SCR-104: Merge 5.1/5.5 into 6.0
 *
 *    Rev 1.1   Jan 22 2003 10:01:00   mpm
 * Preliminary merging of 5.1/5.5 code.
 * Resolution for Domain SCR-104: Merge 5.1/5.5 into 6.0
 *
 *    Rev 1.0   Sep 05 2002 11:13:04   msg
 * Initial revision.
 *
 *    Rev 1.3   Aug 11 2002 18:47:04   vpn-mpm
 * Modified to support extensibility.
 *
 *    Rev 1.2   Jul 05 2002 15:39:10   vpn-mpm
 * Refined gift card reads, writes.
 *
 *    Rev 1.1   Jul 04 2002 12:04:06   vpn-mpm
 * Added fields to gift card.
 *
 *    Rev 1.0   Apr 25 2002 09:02:46   mpm
 * Initial revision.
 * Resolution for Domain SCR-45: TLog facility
 * ===========================================================================
 */
package oracle.retail.stores.domain.ixretail.utility;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import oracle.retail.stores.domain.ixretail.IXRetailConstantsIfc;
import oracle.retail.stores.domain.ixretail.log.AbstractIXRetailTranslator;
import oracle.retail.stores.domain.utility.EntryMethod;
import oracle.retail.stores.domain.utility.GiftCardIfc;
import oracle.retail.stores.foundation.utility.Util;
import oracle.retail.stores.foundation.utility.xml.XMLConversionException;

/**
 * This class creates the elements for a GiftCard element.
 * 
 * @version $Revision: /rgbustores_13.4x_generic_branch/4 $
 */
public class LogGiftCard extends AbstractIXRetailTranslator implements LogGiftCardIfc
{
    /**
     * revision number supplied by source-code-control system
     */
    public static final String revisionNumber = "$Revision: /rgbustores_13.4x_generic_branch/4 $";

    /**
     * Constructs LogGiftCard object.
     */
    public LogGiftCard()
    {
    }

    /**
     * Creates element for the specified gift card.
     * 
     * @param giftCard gift card object
     * @param elementName gift card element name
     * @param doc parent document
     * @param el parent element
     * @return Element representing gift card
     * @exception XMLConversionException thrown if error occurs
     */
    public Element createElement(GiftCardIfc giftCard, String elementName, Document doc, Element el)
            throws XMLConversionException
    {

        setParentDocument(doc);
        setParentElement(el);

        // create discount element
        Element giftCardElement = parentDocument.createElement(elementName);

        setElementType(giftCardElement);

        // set card number
        createTextNodeElement(IXRetailConstantsIfc.ELEMENT_CARD_NUMBER, giftCard.getCardNumber(), giftCardElement);

        // add date sold
        if (giftCard.getDateSold() != null)
        {
            createDateTextNodeElement(IXRetailConstantsIfc.ELEMENT_DATE_SOLD, giftCard.getDateSold(), giftCardElement);
        }

        // add date activated
        if (giftCard.getDateActivated() != null)
        {
            createDateTextNodeElement(IXRetailConstantsIfc.ELEMENT_DATE_ACTIVATED, giftCard.getDateActivated(),
                    giftCardElement);
        }

        // add date sold
        if (giftCard.getExpirationDate() != null)
        {
            createDateTextNodeElement(IXRetailConstantsIfc.ELEMENT_EXPIRATION_DATE, giftCard.getExpirationDate(),
                    giftCardElement);
        }

        // add initial balance
        if (giftCard.getInitialBalance() != null)
        {
            createTextNodeElement(IXRetailConstantsIfc.ELEMENT_INITIAL_BALANCE,
                    giftCard.getInitialBalance().toString(), giftCardElement);
        }

        // add current balance
        if (giftCard.getCurrentBalance() != null)
        {
            createTextNodeElement(IXRetailConstantsIfc.ELEMENT_CURRENT_BALANCE,
                    giftCard.getCurrentBalance().toString(), giftCardElement);
        }

        // add open-amount flag
        createTextNodeElement(IXRetailConstantsIfc.ELEMENT_OPEN_AMOUNT_FLAG,
                new Boolean(giftCard.getOpenAmount()).toString(), giftCardElement);

        // add authorization status
        createTextNodeElement(IXRetailConstantsIfc.ELEMENT_AUTHORIZATION_STATUS, giftCard.getStatus().toString(),
                giftCardElement);

        // add approval code, if it exists
        if (!Util.isEmpty(giftCard.getApprovalCode()))
        {
            createTextNodeElement(IXRetailConstantsIfc.ELEMENT_APPROVAL_CODE, giftCard.getApprovalCode(),
                    giftCardElement);
        }

        createEntryMethodElement(giftCard, giftCardElement);

        createExtendedElements(giftCard, giftCardElement);

        parentElement.appendChild(giftCardElement);

        return (giftCardElement);

    }

    /**
     * Creates a simplified element for the specified gift card.
     * 
     * @param giftCard gift card object
     * @param elementName gift card element name
     * @param doc parent document
     * @param el parent element
     * @return Element representing gift card
     * @exception XMLConversionException thrown if error occurs
     */
    public Element createGiftCardNumberElement(GiftCardIfc giftCard, String elementName, Document doc, Element el)
            throws XMLConversionException
    {

        setParentDocument(doc);
        setParentElement(el);

        // create discount element
        Element giftCardElement = parentDocument.createElement(elementName);

        setElementType(giftCardElement);

        // set card number
        createTextNodeElement(IXRetailConstantsIfc.ELEMENT_CARD_NUMBER, giftCard.getCardNumber(), giftCardElement);

        // add approval code, if it exists
        if (!Util.isEmpty(giftCard.getApprovalCode()))
        {
            createTextNodeElement(IXRetailConstantsIfc.ELEMENT_APPROVAL_CODE, giftCard.getApprovalCode(),
                    giftCardElement);
        }

        createEntryMethodElement(giftCard, giftCardElement);

        createExtendedElements(giftCard, giftCardElement);

        parentElement.appendChild(giftCardElement);

        return (giftCardElement);

    }

    /**
     * Creates element for the specified gift card.
     * 
     * @param giftCard gift card object
     * @param doc parent document
     * @param el parent element
     * @return Element representing gift card
     * @exception XMLConversionException thrown if error occurs
     */
    public Element createElement(GiftCardIfc giftCard, Document doc, Element el) throws XMLConversionException
    {
        return (createElement(giftCard, IXRetailConstantsIfc.ELEMENT_GIFT_CARD, doc, el));
    }

    /**
     * Creates entry method element.
     * 
     * @param giftCard gift card object
     * @param el parent element
     * @exception XMLConversionException is thrown if error occurs
     */
    protected void createEntryMethodElement(GiftCardIfc giftCard, Element el) throws XMLConversionException
    {
        String ixRetailEntryMethod = IXRetailConstantsIfc.ELEMENT_VALUE_KEYED;

        EntryMethod entryMethod = giftCard.getEntryMethod();
        if (Util.isObjectEqual(entryMethod, EntryMethod.Swipe))
        {
            ixRetailEntryMethod = IXRetailConstantsIfc.ELEMENT_VALUE_MSR;
        }

        // create element
        createTextNodeElement(IXRetailConstantsIfc.ELEMENT_ENTRY_METHOD, ixRetailEntryMethod, el);
    }

    /**
     * Create additional elements as needed. This is provided to facilitate
     * extensibility.
     * 
     * @param giftCard gift card object
     * @param el parent element
     * @exception XMLConversionException is thrown if error occurs
     */
    protected void createExtendedElements(GiftCardIfc giftCard, Element el) throws XMLConversionException
    {
    }

}
