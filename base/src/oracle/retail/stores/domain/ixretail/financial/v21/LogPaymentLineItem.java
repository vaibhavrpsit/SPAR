/* ===========================================================================
* Copyright (c) 1998, 2011, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/modules/domain/src/oracle/retail/stores/domain/ixretail/financial/v21/LogPaymentLineItem.java /rgbustores_13.4x_generic_branch/3 2011/10/13 10:48:40 rsnayak Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    rsnayak   10/13/11 - Account number fix
 *    cgreene   07/19/11 - store layaway and order ids in separate column from
 *                         house account number.
 *    cgreene   05/26/10 - convert to oracle packaging
 *    abondala  01/03/10 - update header date
 *
 * ===========================================================================
 * $Log:
 *    4    360Commerce 1.3         2/7/2008 3:25:10 PM    Alan N. Sinton  CR
 *         30132: updated database (tr_ltm_pyan) to save encrypted, hashed,
 *         and masked house account card values.  Code was reviewed by Anil
 *         Bondalapati.
 *    3    360Commerce 1.2         3/31/2005 4:28:56 PM   Robert Pearse   
 *    2    360Commerce 1.1         3/10/2005 10:23:15 AM  Robert Pearse   
 *    1    360Commerce 1.0         2/11/2005 12:12:25 PM  Robert Pearse   
 *
 *   Revision 1.3  2004/06/24 09:15:09  mwright
 *   POSLog v2.1 (second) merge with top of tree
 *
 *   Revision 1.2.2.1  2004/06/10 10:48:38  mwright
 *   Updated to use schema types in commerce services
 *
 *   Revision 1.2  2004/05/06 03:11:12  mwright
 *   Initial revision for POSLog v2.1 merge with top of tree
 *
 *   Revision 1.1.2.1  2004/04/13 07:11:31  mwright
 *   Initial revision for v2.1
 *
 * ===========================================================================
 */
package oracle.retail.stores.domain.ixretail.financial.v21;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import oracle.retail.stores.commerceservices.ixretail.schematypes.v21.RetailTransactionLineItemIfc;
import oracle.retail.stores.commerceservices.ixretail.schematypes.v21.RetailTransactionPaymentOnAccountIfc;
import oracle.retail.stores.domain.financial.PaymentIfc;
import oracle.retail.stores.domain.ixretail.financial.LogPaymentLineItemIfc;
import oracle.retail.stores.domain.ixretail.lineitem.v21.LogLineItem;
import oracle.retail.stores.foundation.manager.device.EncipheredCardDataIfc;
import oracle.retail.stores.foundation.utility.xml.XMLConversionException;

/**
 * This class creates the elements for a PaymentLineItem
 * 
 * @version $Revision: /rgbustores_13.4x_generic_branch/3 $
 */
public class LogPaymentLineItem extends LogLineItem implements LogPaymentLineItemIfc
{
    /**
     * revision number supplied by source-code-control system
     */
    public static final String revisionNumber = "$Revision: /rgbustores_13.4x_generic_branch/3 $";

    /**
     * Constructs LogPaymentLineItem object.
     */
    public LogPaymentLineItem()
    {
    }

    /**
     * Creates element for the specified payment.
     * 
     * @param payment payment object
     * @param doc parent document
     * @param el parent element
     * @param sequenceNumber sequence number
     * @return Element representing PaymentLineItem
     * @exception XMLConversionException thrown if error occurs
     */
    @Override
    public Element createElement(PaymentIfc payment, Document doc, Element el, int sequenceNumber)
            throws XMLConversionException
    {
        RetailTransactionLineItemIfc lineItemElement = (RetailTransactionLineItemIfc)el;

        // this calls the parent class to initialize the sequence number and
        // void flag on the line item element:
        createElement(doc, lineItemElement, null, sequenceNumber);

        RetailTransactionPaymentOnAccountIfc paymentElement = getSchemaTypesFactory()
                .getRetailTransactionPaymentOnAccountInstance();

        lineItemElement.setPaymentOnAccount(paymentElement);
        EncipheredCardDataIfc cardData = payment.getEncipheredCardData();
        if (cardData != null && !cardData.getEncryptedAcctNumber().equals("") && !cardData.getMaskedAcctNumber().equals(""))
        {
            paymentElement.setAccountNumber(cardData.getEncryptedAcctNumber());
            paymentElement.setMaskedAccountNumber(cardData.getMaskedAcctNumber());
        }
        else
        {
            paymentElement.setAccountNumber(payment.getReferenceNumber());
        }
        paymentElement.setAmount(currency(payment.getPaymentAmount()));
        paymentElement.setAccountCode(payment.getAccountTypeDescriptor());
        paymentElement.setBalanceDue(currency(payment.getBalanceDue()));

        return lineItemElement;

    }

}
