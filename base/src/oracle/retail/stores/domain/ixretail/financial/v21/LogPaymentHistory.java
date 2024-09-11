/* ===========================================================================
* Copyright (c) 2005, 2011, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/modules/domain/src/oracle/retail/stores/domain/ixretail/financial/v21/LogPaymentHistory.java /rgbustores_13.4x_generic_branch/1 2011/05/04 11:49:07 mszekely Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    cgreene   05/26/10 - convert to oracle packaging
 *    abondala  01/03/10 - update header date
 *
 * ===========================================================================
 * $Log:
 *    1    360Commerce 1.0         12/13/2005 4:47:57 PM  Barry A. Pape   
 *
 *
 * ===========================================================================
 */
package oracle.retail.stores.domain.ixretail.financial.v21;
// XML imports
import java.util.Iterator;
import java.util.List;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import oracle.retail.stores.commerceservices.ixretail.schematypes.v21.RetailTransactionIRSPayment360Ifc;
import oracle.retail.stores.commerceservices.ixretail.schematypes.v21.RetailTransactionIRSPaymentHistory360Ifc;
import oracle.retail.stores.domain.financial.PaymentHistoryInfoIfc;
import oracle.retail.stores.domain.ixretail.financial.LogPaymentHistoryIfc;
import oracle.retail.stores.domain.ixretail.log.AbstractIXRetailTranslator;
import oracle.retail.stores.foundation.utility.xml.XMLConversionException;

//--------------------------------------------------------------------------
/**
    This class creates the basic elements for a payment history
    @version $Revision: /rgbustores_13.4x_generic_branch/1 $
**/
//--------------------------------------------------------------------------
public class LogPaymentHistory
extends AbstractIXRetailTranslator
implements LogPaymentHistoryIfc
{
    /** revision number supplied by source-code-control system **/
    public static String revisionNumber = "$Revision: /rgbustores_13.4x_generic_branch/1 $";

    //----------------------------------------------------------------------------
    /**
        Constructs LogPayment History object. <P>
    **/
    //----------------------------------------------------------------------------
    public LogPaymentHistory()
    {
    }

    //---------------------------------------------------------------------
    /**
       Creates element for the specified payment history.
       <P>
       @param doc parent document
       @param el parent element
       @param paymentHistoryInfoCollection object
       @param typeCode The payment history type
       @return Element representing payment history
       @exception XMLConversionException thrown if error occurs
    **/
    //---------------------------------------------------------------------
    public Element createElement(Document doc,
                                 Element el,
                                 List paymentHistoryInfoCollection,
                                 String typeCode)
    throws XMLConversionException
    {                                   // begin createElement()
        RetailTransactionIRSPaymentHistory360Ifc paymentHistoryElement = (RetailTransactionIRSPaymentHistory360Ifc)el;
        paymentHistoryElement.setTypeCode(typeCode);
        
        for (Iterator i = paymentHistoryInfoCollection.iterator(); i.hasNext(); )
        {
            PaymentHistoryInfoIfc phi = (PaymentHistoryInfoIfc)i.next();
            RetailTransactionIRSPayment360Ifc paymentElement = getSchemaTypesFactory().getPaymentElementInstance();

            paymentElement.setTenderType(phi.getTenderType());
            paymentElement.setTenderAmount(getSchemaTypesFactory().getPOSLogAmountInstance().initialize(currency(phi.getTenderAmount())));
            paymentElement.setCountryCode(phi.getCountryCode());
            paymentHistoryElement.addPayment(paymentElement);

        }

        return(paymentHistoryElement);
    }                                   // end createElement()
}
