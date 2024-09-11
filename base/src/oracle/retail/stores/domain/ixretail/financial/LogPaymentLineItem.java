/* ===========================================================================
* Copyright (c) 1998, 2011, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/modules/domain/src/oracle/retail/stores/domain/ixretail/financial/LogPaymentLineItem.java /rgbustores_13.4x_generic_branch/1 2011/05/04 11:49:08 mszekely Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    cgreene   05/26/10 - convert to oracle packaging
 *    cgreene   04/28/10 - updating deprecated names
 *    abondala  01/03/10 - update header date
 *
 * ===========================================================================
 * $Log:
 *    3    360Commerce 1.2         3/31/2005 4:28:56 PM   Robert Pearse   
 *    2    360Commerce 1.1         3/10/2005 10:23:15 AM  Robert Pearse   
 *    1    360Commerce 1.0         2/11/2005 12:12:25 PM  Robert Pearse   
 *
 *   Revision 1.3  2004/02/12 17:13:42  mcs
 *   Forcing head revision
 *
 *   Revision 1.2  2004/02/11 23:25:28  bwf
 *   @scr 0 Organize imports.
 *
 *   Revision 1.1.1.1  2004/02/11 01:04:31  cschellenger
 *   updating to pvcs 360store-current
 *
 *
 * 
 *    Rev 1.0   Aug 29 2003 15:36:18   CSchellenger
 * Initial revision.
 * 
 *    Rev 1.1   Jan 22 2003 09:57:20   mpm
 * Preliminary merging of 5.1/5.5 code.
 * Resolution for Domain SCR-104: Merge 5.1/5.5 into 6.0
 * 
 *    Rev 1.0   Sep 05 2002 11:12:44   msg
 * Initial revision.
 * 
 *    Rev 1.1   May 07 2002 18:05:22   mpm
 * Completed till suspend, resume.
 * Resolution for Domain SCR-45: TLog facility
 *
 *    Rev 1.0   Apr 28 2002 13:35:40   mpm
 * Initial revision.
 * Resolution for Domain SCR-45: TLog facility
 * ===========================================================================
 */
package oracle.retail.stores.domain.ixretail.financial;
// XML imports
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import oracle.retail.stores.domain.financial.PaymentIfc;
import oracle.retail.stores.domain.ixretail.IXRetailConstantsIfc;
import oracle.retail.stores.domain.ixretail.lineitem.LogLineItem;
import oracle.retail.stores.foundation.utility.xml.XMLConversionException;

//--------------------------------------------------------------------------
/**
    This class creates the elements for a PaymentLineItem
    @version $Revision: /rgbustores_13.4x_generic_branch/1 $
**/
//--------------------------------------------------------------------------
public class LogPaymentLineItem
extends LogLineItem
implements LogPaymentLineItemIfc
{
    /**
       revision number supplied by source-code-control system
    **/
    public static String revisionNumber = "$Revision: /rgbustores_13.4x_generic_branch/1 $";

    //----------------------------------------------------------------------------
    /**
        Constructs LogPaymentLineItem object. <P>
    **/
    //----------------------------------------------------------------------------
    public LogPaymentLineItem()
    {                                   // begin LogPaymentLineItem()
    }                                   // end LogPaymentLineItem()

    //---------------------------------------------------------------------
    /**
       Creates element for the specified payment. <P>
       @param payment payment object
       @param doc parent document
       @param el parent element
       @param sequenceNumber sequence number
       @return Element representing PaymentLineItem
       @exception XMLConversionException thrown if error occurs
    **/
    //---------------------------------------------------------------------
    public Element createElement(PaymentIfc payment,
                                 Document doc,
                                 Element el,
                                 int sequenceNumber)
    throws XMLConversionException
    {                                   // begin createElement()
        setParentDocument(doc);
        setParentElement(el);

        // create appropriate sale element
        Element paymentElement = parentDocument.createElement
          (IXRetailConstantsIfc.ELEMENT_PAYMENT_ON_ACCOUNT);

        paymentElement.setAttribute
          (IXRetailConstantsIfc.ATTRIBUTE_SCHEMA_TYPE_TAG,
           IXRetailConstantsIfc.TYPE_RETAIL_TRANSACTION_PAYMENT_ON_ACCOUNT_360);

        Element lineItemElement =
          createElement(doc,
                        el,
                        IXRetailConstantsIfc.ELEMENT_PAYMENT_ON_ACCOUNT,
                        sequenceNumber);

        createTextNodeElement
          (IXRetailConstantsIfc.ELEMENT_ACCOUNT_NUMBER,
           payment.getReferenceNumber(),
           paymentElement);

        createTextNodeElement
          (IXRetailConstantsIfc.ELEMENT_AMOUNT,
           payment.getPaymentAmount(),
           paymentElement);

        createTextNodeElement
          (IXRetailConstantsIfc.ELEMENT_ACCOUNT_CODE,
           payment.getAccountTypeDescriptor(),
           paymentElement);

        createTextNodeElement
          (IXRetailConstantsIfc.ELEMENT_BALANCE_DUE,
           payment.getBalanceDue(),
           paymentElement);

        lineItemElement.appendChild(paymentElement);

        parentElement.appendChild(lineItemElement);

        return(lineItemElement);

    }                                   // end createElement()

}
