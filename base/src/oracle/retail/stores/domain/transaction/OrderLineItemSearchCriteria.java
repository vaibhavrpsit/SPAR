/* ===========================================================================
* Copyright (c) 2008, 2011, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/modules/domain/src/oracle/retail/stores/domain/transaction/OrderLineItemSearchCriteria.java /rgbustores_13.4x_generic_branch/1 2011/05/04 11:48:46 mszekely Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    cgreene   05/26/10 - convert to oracle packaging
 *    abondala  01/03/10 - update header date
 *    mchellap  12/02/08 - added orderBusinessdate,originalLineNumber and
 *                         orderID
 *    mchellap  11/13/08 - Inventory Reservation Module
 *    mchellap  11/07/08 - Criteria to search order line item status

 * ===========================================================================
 */

package oracle.retail.stores.domain.transaction;

import java.util.Date;

import oracle.retail.stores.domain.utility.EYSDate;

/**
 * This class is used for setting a order line item search criteria.
 * <P>
 */
public class OrderLineItemSearchCriteria implements OrderLineItemSearchCriteriaIfc
{ // Begin class OrderLineItemSearchCriteria

    /**
     * StoreID as String
     */
    protected String storeID = null;

    /**
     * WorkStationID as String
     */
    protected String workStationID = null;

    /**
     * BusinessDay as EYSDate
     */
    protected EYSDate businessDay = null;

    /**
     * TransactionID as String
     */
    protected String transactionSequenceNo = null;

    /**
     * orderID as String
     */
    protected String orderID = null;

    /**
     * LayawayID as String
     */
    protected String layawayID = null;

    /**
     * LineItemSequenceNumber as String
     */
    protected int lineItemSequenceNo = -1;

    /**
     * OrderBusinessDate as String;
     */
    protected Date orderBusinessDate = null;

    /**
     * OriginalLineNumber as int
     */
    protected int OriginalLineNumber = -1;

    /**
     * @return the businessDay
     */
    public EYSDate getBusinessDay()
    {
        return businessDay;
    }

    /**
     * @param businessDay the businessDay to set
     */
    public void setBusinessDay(EYSDate businessDay)
    {
        this.businessDay = businessDay;
    }

    /**
     * @return the lineItemSequenceNo
     */
    public int getLineItemSequenceNo()
    {
        return lineItemSequenceNo;
    }

    /**
     * @param lineItemSequenceNo the lineItemSequenceNo to set
     */
    public void setLineItemSequenceNo(int lineItemSequenceNo)
    {
        this.lineItemSequenceNo = lineItemSequenceNo;
    }

    /**
     * @return the storeID
     */
    public String getStoreID()
    {
        return storeID;
    }

    /**
     * @param storeID the storeID to set
     */
    public void setStoreID(String storeID)
    {
        this.storeID = storeID;
    }

    /**
     * @return the TransactionSequenceNo
     */
    public String getTransactionSequenceNo()
    {
        return transactionSequenceNo;
    }

    /**
     * @param TransactionSequenceNO the transactionID to set
     */
    public void setTransactionSequenceNo(String transactionSequenceNo)
    {
        this.transactionSequenceNo = transactionSequenceNo;
    }

    /**
     * @return the workStationID
     */
    public String getWorkStationID()
    {
        return workStationID;
    }

    /**
     * @param workStationID the workStationID to set
     */
    public void setWorkStationID(String workStationID)
    {
        this.workStationID = workStationID;
    }

    /**
     * @return the layawayID
     */
    public String getLayawayID()
    {
        return layawayID;
    }

    /**
     * @param layawayID the layawayID to set
     */
    public void setLayawayID(String layawayID)
    {
        this.layawayID = layawayID;
    }

    /**
     * @return the orderBusinessDate
     */
    public Date getOrderBusinessDate()
    {
        return orderBusinessDate;
    }

    /**
     * @param orderBusinessDate the orderBusinessDate to set
     */
    public void setOrderBusinessDate(Date orderBusinessDate)
    {
        this.orderBusinessDate = orderBusinessDate;
    }

    /**
     * @return the originalLineNumber
     */
    public int getOriginalLineNumber()
    {
        return OriginalLineNumber;
    }

    /**
     * @param originalLineNumber the originalLineNumber to set
     */
    public void setOriginalLineNumber(int originalLineNumber)
    {
        OriginalLineNumber = originalLineNumber;
    }

    /**
     * @return the orderID
     */
    public String getOrderID()
    {
        return orderID;
    }

    /**
     * @param orderID the orderID to set
     */
    public void setOrderID(String orderID)
    {
        this.orderID = orderID;
    }

} // End class OrderLineItemSearchCriteria

