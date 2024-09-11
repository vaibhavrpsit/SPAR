/* ===========================================================================
* Copyright (c) 1998, 2011, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/modules/domain/src/oracle/retail/stores/domain/ixretail/financial/v21/LogBillPayment.java /rgbustores_13.4x_generic_branch/1 2011/05/04 11:49:07 mszekely Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    nkgautam  10/13/10 - fix for pos log not getting generated for cancel
 *                         transactions
 *    mchellap  07/08/10 - Fix total payment collected
 *    mchellap  07/08/10 - Add customer name, account number
 *    mchellap  06/23/10 - Billpay changes
 *
 * ===========================================================================
 */
package oracle.retail.stores.domain.ixretail.financial.v21;

import java.math.BigDecimal;
import java.util.ArrayList;

import oracle.retail.stores.commerceservices.ixretail.schematypes.v21.RetailTransactionBill360Ifc;
import oracle.retail.stores.commerceservices.ixretail.schematypes.v21.RetailTransactionBillPayment360Ifc;
import oracle.retail.stores.domain.financial.BillIfc;
import oracle.retail.stores.domain.financial.BillPayIfc;
import oracle.retail.stores.domain.ixretail.log.AbstractIXRetailTranslator;
import oracle.retail.stores.foundation.utility.xml.XMLConversionException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 * This class creates the elements for a BillPayment
 */
public class LogBillPayment extends AbstractIXRetailTranslator implements LogBillPaymentIfc
{

    /**
     * Constructs LogBillPayment object.
     *
     */
    public LogBillPayment()
    {
    }

    /**
     * Creates element for the specified bill payment.
     * <P>
     *
     * @param payment payment object
     * @param doc parent document
     * @param el parent element
     * @param sequenceNumber sequence number
     * @return Element representing PaymentLineItem
     * @exception XMLConversionException thrown if error occurs
     */
    public Element createElement(BillPayIfc payment, Document doc, Element el)
            throws XMLConversionException
    {

        RetailTransactionBillPayment360Ifc billPayment = (RetailTransactionBillPayment360Ifc) el;
        BigDecimal totalAmountPaid = new BigDecimal(0);
        billPayment.setAccountNumber(payment.getAccountNumber());
        billPayment.setCustomerName(payment.getFirstLastName());
        if(payment.getPaymentDate() != null)
        {
            billPayment.setPaymentDate(payment.getPaymentDate().dateValue());
        }

        ArrayList<BillIfc> paidBills = payment.getBillsList();
        RetailTransactionBill360Ifc[] billArray = new RetailTransactionBill360Ifc[paidBills.size()];
        int seqNumber = 1;
        int size = 0;

        for(BillIfc bill : paidBills)
        {
            RetailTransactionBill360Ifc billElement = getSchemaTypesFactory().getRetailTransactionBillInstance();
            billElement.setSequenceNumber(seqNumber++);
            billElement.setAccountNumber(bill.getAccountNumber());
            billElement.setCustomerName(bill.getCustomerName());
            billElement.setBillNumber(bill.getBillNumber());
            billElement.setDueDate(bill.getDueDate().dateValue());
            billElement.setPaymentCollected(bill.getBillAmountPaid().getDecimalValue());
            totalAmountPaid = totalAmountPaid.add(bill.getBillAmountPaid().getDecimalValue());
            billArray[size++] = billElement;
        }

        billPayment.setTotalPaymentCollected(totalAmountPaid);

        billPayment.setBills(billArray);

        return billPayment;
    }

}
