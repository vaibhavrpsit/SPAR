/* ===========================================================================
* Copyright (c) 1998, 2011, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/modules/domain/src/oracle/retail/stores/domain/ixretail/customer/v21/LogIRSCustomer.java /rgbustores_13.4x_generic_branch/3 2011/09/02 13:05:37 cgreene Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    cgreene   09/02/11 - refactored method names around enciphered objects
 *    rrkohli   07/13/11 - IRS customer related changes for encryption CR
 *    cgreene   05/26/10 - convert to oracle packaging
 *    abondala  01/03/10 - update header date
 *    abondala  11/04/08 - updated file related to customer id types reason
 *                         code.
 *
 * ===========================================================================
 * $Log:
 *    1    360Commerce 1.0         12/13/2005 4:47:57 PM  Barry A. Pape
 *
 * ===========================================================================
 */
package oracle.retail.stores.domain.ixretail.customer.v21;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import oracle.retail.stores.domain.customer.IRSCustomerIfc;
import oracle.retail.stores.foundation.utility.xml.XMLConversionException;

import oracle.retail.stores.domain.ixretail.customer.LogIRSCustomerIfc;
import oracle.retail.stores.commerceservices.ixretail.schematypes.v21.RetailTransactionIRSCustomer360Ifc;

/**
 * This class creates the elements for a RetailTransactionIRSCustomer element.
 * 
 * @version $Revision: /rgbustores_13.4x_generic_branch/3 $
 */
public class LogIRSCustomer extends LogCustomer implements LogIRSCustomerIfc
{
    /**
     * revision number supplied by source-code-control system
     */
    public static final String revisionNumber = "$Revision: /rgbustores_13.4x_generic_branch/3 $";

    /**
     * Constructs LogIRSCustomer object.
     */
    public LogIRSCustomer()
    {
    }

    /**
     * Creates element for the specified customer.
     * 
     * @param irsCustomer customer to be translated
     * @param doc parent document
     * @param el parent element
     * @return Element representing customer object
     * @exception XMLConversionException thrown if error occurs
     */
    @Override
    public Element createElement(IRSCustomerIfc irsCustomer,
                                 Document doc,
                                 Element el)
        throws XMLConversionException
    {
        RetailTransactionIRSCustomer360Ifc customerElement = (RetailTransactionIRSCustomer360Ifc)el;
        createAddressElement(irsCustomer, doc, customerElement);
        customerElement.setCustomerID(irsCustomer.getCustomerID());
        customerElement.setFirstName(irsCustomer.getFirstName());
        customerElement.setMiddleName(irsCustomer.getMiddleName());
        customerElement.setLastName(irsCustomer.getLastName());
        customerElement.setBirthdate(dateValue(irsCustomer.getBirthdate()));
        customerElement.setEncryptedTaxPayerIdNumber(irsCustomer.getEncipheredTaxID().getEncryptedNumber());
        customerElement.setMaskedTaxPayerIdNumber(irsCustomer.getEncipheredTaxID().getMaskedNumber());
        customerElement.setOccupation(irsCustomer.getOccupation());
        customerElement.setVerifyingIdType(irsCustomer.getLocalizedPersonalIDCode().getCode());
        customerElement.setEncryptedVerifyingIdNumber(irsCustomer.getVerifyingID().getEncryptedNumber());
        customerElement.setMaskedVerifyingIdNumber(irsCustomer.getVerifyingID().getMaskedNumber());
        customerElement.setVerifyingIdIssuingState(irsCustomer.getVerifyingIdIssuingState());
        customerElement.setVerifyingIdIssuingCountry(irsCustomer.getVerifyingIdIssuingCountry());
        customerElement.setDateCashReceived(dateValue(irsCustomer.getDateCashReceived()));
        return customerElement;

    }
}
