/* ===========================================================================
* Copyright (c) 1998, 2011, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/modules/domain/src/oracle/retail/stores/domain/ixretail/utility/v21/LogGiftCard.java /rgbustores_13.4x_generic_branch/3 2011/07/18 16:21:31 cgreene Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    cgreene   07/18/11 - remove hashed number column from gift card tables
 *    cgreene   05/20/11 - implemented enums for reponse code and giftcard
 *                         status code
 *    cgreene   05/26/10 - convert to oracle packaging
 *    jswan     01/12/10 - Fix POSLog amount requested.
 *    abondala  01/03/10 - update header date
 *
 * ===========================================================================
 * $Log:
 *    8    360Commerce 1.7         4/30/2008 7:57:20 AM   Jack G. Swan    These
 *          changes export reload/issue indicator to POSLog for sales of
 *         giftcards.  It also uses the indicator to set the request type in
 *         the gift card table correctly.  Code review by Christion Greene.
 *    7    360Commerce 1.6         3/11/2008 5:37:47 PM   Michael P. Barnett
 *         Add hashed and masked gift card numbers to the GiftCard element.
 *    6    360Commerce 1.5         11/15/2007 11:43:26 AM Christian Greene
 *         Belize merge - add settlement data
 *    5    360Commerce 1.4         6/26/2007 11:13:58 AM  Ashok.Mondal    I18N
 *         changes to export and import POSLog.
 *    4    360Commerce 1.3         12/13/2005 4:43:49 PM  Barry A. Pape
 *         Base-lining of 7.1_LA
 *    3    360Commerce 1.2         3/31/2005 4:28:54 PM   Robert Pearse   
 *    2    360Commerce 1.1         3/10/2005 10:23:12 AM  Robert Pearse   
 *    1    360Commerce 1.0         2/11/2005 12:12:23 PM  Robert Pearse   
 *
 *   Revision 1.5.4.2  2004/10/28 04:15:24  mwright
 *   Merge from top of tree
 *
 *   Revision 1.7  2004/10/28 01:43:50  mwright
 *   Modified to use new sale of gift card line item
 *
 *   Revision 1.6  2004/10/26 08:02:10  mwright
 *   Ensure that all mandatory elements are set in the authorization element
 *
 *   Revision 1.5  2004/06/24 09:15:09  mwright
 *   POSLog v2.1 (second) merge with top of tree
 *
 *   Revision 1.4.2.2  2004/06/23 00:45:37  mwright
 *   Ensure that authorization date is set to non-null value.
 *   Ensure that approval code is set
 *
 *   Revision 1.4.2.1  2004/06/10 10:55:54  mwright
 *   Updated to use schema types in commerce services
 *
 *   Revision 1.4  2004/05/06 03:43:36  mwright
 *   POSLog v2.1 merge with top of tree
 *
 *   Revision 1.1.2.3  2004/04/19 07:42:02  mwright
 *   Added mandatory authorization date and reference number elements
 *
 *   Revision 1.1.2.2  2004/04/13 06:53:03  mwright
 *   Removed tabs
 *   Ready for testing
 *
 *   Revision 1.1.2.1  2004/03/21 14:35:23  mwright
 *   Initial revision for POSLog v2.1
 *
 *
 * ===========================================================================
 */
package oracle.retail.stores.domain.ixretail.utility.v21;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import oracle.retail.stores.commerceservices.common.currency.CurrencyIfc;
import oracle.retail.stores.commerceservices.ixretail.schematypes.v21.SaleOfGiftCard360Ifc;
import oracle.retail.stores.commerceservices.ixretail.schematypes.v21.TenderAuthorizationIfc;
import oracle.retail.stores.commerceservices.ixretail.schematypes.v21.TenderGiftCardIfc;
import oracle.retail.stores.domain.ixretail.IXRetailConstantsV21Ifc;
import oracle.retail.stores.domain.ixretail.log.AbstractIXRetailTranslator;
import oracle.retail.stores.domain.ixretail.utility.LogGiftCardIfc;
import oracle.retail.stores.domain.utility.EYSDate;
import oracle.retail.stores.domain.utility.GiftCardIfc;
import oracle.retail.stores.foundation.utility.Util;
import oracle.retail.stores.foundation.utility.xml.XMLConversionException;

/**
 * This class creates the elements for a GiftCard element.
 * 
 * @version $Revision: /rgbustores_13.4x_generic_branch/3 $
 */
public class LogGiftCard extends AbstractIXRetailTranslator
    implements LogGiftCardIfc, IXRetailConstantsV21Ifc
{
    /**
     * revision number supplied by source-code-control system
     */
    public static final String revisionNumber = "$Revision: /rgbustores_13.4x_generic_branch/3 $";

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
     * @param doc parent document
     * @param el parent element
     * @return Element representing gift card
     * @exception XMLConversionException thrown if error occurs
     */
    public Element createElement(GiftCardIfc giftCard, Document doc, Element el)
        throws XMLConversionException
    {
        return(createElement(giftCard,
                             ELEMENT_GIFT_CARD,
                             doc,
                             el));
    }

    /**
     * Both methods that set the authorization element need the mandatory bits
     * filled in by this method. The date/time sold, reference number and
     * approval code are all mandatory in the schema.
     * 
     * @param authorization Element being populated.
     * @param giftCard Source for element being populated.
     */
    protected void setMandatoryAuthorizationElements(TenderAuthorizationIfc authorization, GiftCardIfc giftCard)
    {
        // the auth date is mandatory in the schema, but we might not have one ... 
        EYSDate sold = giftCard.getDateSold();
        if (sold == null)
        {
            sold = giftCard.getDateActivated();
            if (sold == null)
            {
                sold = new EYSDate(1980,1,1,0,0,0);     // this does not get used in the import, but it is mandatory
            }
        }
        
        if (giftCard.getAuthorizedDateTime() == null)
        {
            authorization.setAuthorizationDateTime(sold.dateValue());
        }
        else
        {
            authorization.setAuthorizationDateTime(giftCard.getAuthorizedDateTime().dateValue());
        }
        authorization.setAuthorizationSettlementData(giftCard.getSettlementData());
        
        // The reference number is mandatory, but not available:
        authorization.setReferenceNumber(IXRetailConstantsV21Ifc.ELEMENT_VALUE_NOT_SUPPORTED);

        // add approval code, if it exists
        String approval = giftCard.getApprovalCode();
        if (Util.isEmpty(approval))
        {
            // auth code is mandatory, so set it to "not found"
            approval = IXRetailConstantsV21Ifc.ELEMENT_VALUE_NOT_FOUND;
        }
        authorization.setAuthorizationCode(approval);
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
    public Element createElement(GiftCardIfc giftCard,
                                 String elementName,        // unused
                                 Document doc,              // unused
                                 Element el)
    throws XMLConversionException
    {

        // throw exception if not called with correct type of element:
        TenderGiftCardIfc giftCardElement = (TenderGiftCardIfc)el;
        
        // no longer required   setElementType(giftCardElement);

        // add the gift card number in all its various forms
        giftCardElement.setCardNumber(giftCard.getEncipheredCardData().getEncryptedAcctNumber());
        giftCardElement.setMaskedCardNumber(giftCard.getEncipheredCardData().getMaskedAcctNumber());

        // add Currency ID
        giftCardElement.setCurrencyID(giftCard.getCurrencyID()); //I18N

        // add date sold
        giftCardElement.setDateSold(dateValue(giftCard.getDateSold()));    // null-safe, most of what follows may be null

        // add date activated
        giftCardElement.setDateActivated(dateValue(giftCard.getDateActivated()));

        // add date sold
        giftCardElement.setExpirationDate(dateValue(giftCard.getExpirationDate()));

        // add initial balance
        giftCardElement.setInitialBalance(getSchemaTypesFactory().getPOSLogAmountInstance().initialize(currency(giftCard.getInitialBalance())));

        
        // add current balance
        giftCardElement.setCurrentBalance(getSchemaTypesFactory().getPOSLogAmountInstance().initialize(currency(giftCard.getCurrentBalance())));

        // add open-amount flag
        giftCardElement.setOpenAmountFlag(new Boolean(giftCard.getOpenAmount()));

        // add authorization status
        TenderAuthorizationIfc authorization = getSchemaTypesFactory().getTenderAuthorizationInstance();
        CurrencyIfc requestedAmount = giftCard.getInitialBalance().subtract(giftCard.getCurrentBalance());
        authorization.setRequestedAmount(getSchemaTypesFactory().getPOSLogAmountInstance().initialize(currency(requestedAmount)));
        authorization.setAuthorizationStatus(giftCard.getStatus().toString());

        setMandatoryAuthorizationElements(authorization, giftCard);
        
        giftCardElement.setAuthorization(authorization);
        
        createEntryMethodElement(giftCard, giftCardElement);  // should not be required here, already set?

        return giftCardElement;
    }

    /**
     * Creates a simplified element for the specified gift card.
     * 
     * @param giftCard gift card object
     * @param elementName not used
     * @param doc parent document
     * @param el A RetailTransactionGiftCertificateIfc element to be populated
     *            with data
     * @return Element representing gift card
     * @exception XMLConversionException thrown if error occurs
     */
    public Element createGiftCardNumberElement(GiftCardIfc giftCard,
                                               String elementName,  // not used
                                               Document doc,
                                               Element el)
    throws XMLConversionException
    {
        // this one is used by LogSaleReturnLineItem

        setParentDocument(doc);

        // will throw exception if wrong element was passed in:
        //RetailTransactionGiftCertificateIfc giftCardElement = (RetailTransactionGiftCertificateIfc)el;
        SaleOfGiftCard360Ifc saleOfGiftCardElement = (SaleOfGiftCard360Ifc)el;
        TenderGiftCardIfc giftCardElement = getSchemaTypesFactory().getTenderGiftCardInstance();
        saleOfGiftCardElement.setGiftCard(giftCardElement);
        if (giftCard.getRequestType() == GiftCardIfc.GIFT_CARD_RELOAD)
        {
            saleOfGiftCardElement.setGiftCardReloaded(Boolean.TRUE);
        }
        giftCardElement.setCardNumber(giftCard.getEncipheredCardData().getEncryptedAcctNumber());
        giftCardElement.setMaskedCardNumber(giftCard.getEncipheredCardData().getMaskedAcctNumber());

        giftCardElement.setCurrencyID(giftCard.getCurrencyID()); //I18N

        // add approval code, if it exists
        TenderAuthorizationIfc authorization = getSchemaTypesFactory().getTenderAuthorizationInstance();
        authorization.setRequestedAmount(getSchemaTypesFactory().getPOSLogAmountInstance().initialize(currency(giftCard.getInitialBalance())));
        authorization.setAuthorizationStatus(giftCard.getStatus().toString());
        
        setMandatoryAuthorizationElements(authorization, giftCard);

        giftCardElement.setAuthorization(authorization);

        createEntryMethodElement(giftCard, giftCardElement);        // probably not required

        return giftCardElement;
    }

    /**
     * Creates entry method element.
     * 
     * @param giftCard gift card object
     * @param el parent element
     * @exception XMLConversionException is thrown if error occurs
     */
    protected void createEntryMethodElement(GiftCardIfc giftCard, Element el)
        throws XMLConversionException
    {
/*
        String ixRetailEntryMethod = ELEMENT_VALUE_KEYED;

        String entryMethod = giftCard.getEntryMethod();
        if (Util.isObjectEqual(entryMethod, TenderLineItemIfc.ENTRY_METHOD_MAGSWIPE))
        {
            ixRetailEntryMethod = ELEMENT_VALUE_MSR;
        }
        

        // create element
        createTextNodeElement
          (IXRetailConstantsIfc.ELEMENT_ENTRY_METHOD,
           ixRetailEntryMethod,
           el);
*/           
    }

}