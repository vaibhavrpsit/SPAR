/*===========================================================================
* Copyright (c) 2013, 2014, Oracle and/or its affiliates. All rights reserved.
* ===========================================================================
* $Header: rgbustores/modules/domain/src/oracle/retail/stores/domain/transform/entity/order/OrderSummaryTransformer.java /main/7 2014/06/17 15:26:37 abhinavs Exp $
* ===========================================================================
* NOTES
* <other useful comments, qualifications, etc.>
*
* MODIFIED    (MM/DD/YY)
* abhinavs    06/16/14 - CAE Order summary enhancement phase I
* mkutiana    12/11/13 - Added Javadocs
* abhinavs    06/11/13 - Fix to set orderType in the domain object
* abondala    01/27/13 - extending JPA
* sgu         01/25/13 - use currency in order summary
* yiqzhao     01/20/13 - Refactoring order summary transformers.
* yiqzhao     01/16/13 - Creation
* ===========================================================================
*/
package oracle.retail.stores.domain.transform.entity.order;


import java.text.ParseException;
import java.util.Date;

import oracle.retail.stores.commerceservices.common.currency.CurrencyIfc;
import oracle.retail.stores.commerceservices.common.datetime.DateTimeServiceLocator;
import oracle.retail.stores.common.utility.StringUtils;
import oracle.retail.stores.domain.DomainGateway;
import oracle.retail.stores.domain.order.OrderSummaryEntryIfc;
import oracle.retail.stores.domain.transaction.TransactionIDIfc;
import oracle.retail.stores.domain.utility.EYSDate;
import oracle.retail.stores.storeservices.entities.order.OrderSummary;
import oracle.retail.stores.transform.TransformerIfc;

/**
 * The OrderSummaryTransformer is a utility class for converting between 
 * JPA Entity objects {@link oracle.retail.stores.storeservices.entities.order.OrderSummary} and
 * oracle.retail.stores.domain.order.OrderSummaryEntryIfc Domain objects
 * <p>
 * The OrderSummaryTransformer is a spring loaded bean defined in TransformerContext.xml
 * @since 14.0
 */
public class OrderSummaryTransformer implements OrderSummaryTransformerIfc, TransformerIfc
{

    /*
     * (non-Javadoc)
     * @see
     * oracle.retail.stores.domain.transform.entity.order.OrderSummaryTransformerIfc
     * #toDomain(oracle.retail.stores.storeservices.entities.order.OrderSummary)
     */
    public OrderSummaryEntryIfc toDomain(OrderSummary orderSummary)
    {
        OrderSummaryEntryIfc orderSummaryEntry = DomainGateway.getFactory().getOrderSummaryEntryInstance();

        orderSummaryEntry.setOrderID(orderSummary.getOrderId());
        orderSummaryEntry.setOrderDescription(orderSummary.getOrderDescrption());
        orderSummaryEntry.setCustomerFirstName(orderSummary.getContactFirstName());
        orderSummaryEntry.setCustomerLastName(orderSummary.getContactLastName());
        orderSummaryEntry.setCustomerMiddleInitial(orderSummary.getContactMiddleInitial());
        orderSummaryEntry.setCustomerCompanyName(orderSummary.getContactBusinessName());
        orderSummaryEntry.setStoreOrderStatus(orderSummary.getStoreOrderStatus());
        orderSummaryEntry.setXChannelStatus(orderSummary.getXChannelStatus());
        orderSummaryEntry.setOrderType(orderSummary.getOrderType());

        CurrencyIfc storeTotal = DomainGateway.getCurrencyInstance(orderSummary.getCurrencyCode());
        storeTotal.setDecimalValue(orderSummary.getStoreOrderTotalAmt());
        orderSummaryEntry.setStoreOrderTotal(storeTotal);

        CurrencyIfc xchannelTotal = DomainGateway.getCurrencyInstance(orderSummary.getCurrencyCode());
        xchannelTotal.setDecimalValue(orderSummary.getXChannelTotalAmt());
        orderSummaryEntry.setXChannelTotal(xchannelTotal);

        if (orderSummary.getCreateTimeStamp() != null)
        {
            orderSummaryEntry.setTimestampCreated(new EYSDate(orderSummary.getCreateTimeStamp()));
        }
        if (orderSummary.getModifyTimeStamp() != null)
        {
            orderSummaryEntry.setTimestampModified(new EYSDate(orderSummary.getModifyTimeStamp()));
        }

        TransactionIDIfc recordingTransactionID = DomainGateway.getFactory().getTransactionIDInstance();
        recordingTransactionID.setStoreID(orderSummary.getRecordStoreId());
        recordingTransactionID.setWorkstationID(orderSummary.getRecordWorkstationId());
        if (orderSummary.getRecordSequenceNumber() != null)
        {
            recordingTransactionID.setSequenceNumber(orderSummary.getRecordSequenceNumber().longValue());
        }

        if (!StringUtils.isEmpty(orderSummary.getRecordBusinessDate()))
        {
            orderSummaryEntry.setRecordingTransactionBusinessDate(new EYSDate(parseRecordingBusinessDate(orderSummary
                    .getRecordBusinessDate())));
        }
        orderSummaryEntry.setRecordingTransactionID(recordingTransactionID);

        return orderSummaryEntry;
    }

    /**
     * This method parses String to {@link #Date} object
     * 
     * @param businessDate
     * @return
     */
    private Date parseRecordingBusinessDate(String businessDate)
    {
        try
        {
            return DateTimeServiceLocator.getDateTimeService().parseBusinessDate(businessDate);

        }
        catch (ParseException e)
        {
            e.printStackTrace();
        }
        return null;
    }
}
