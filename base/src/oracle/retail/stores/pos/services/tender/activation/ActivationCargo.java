/* ===========================================================================
* Copyright (c) 2011, 2014, Oracle and/or its affiliates. All rights reserved.
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/tender/activation/ActivationCargo.java /main/2 2014/02/10 15:44:37 asinton Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    asinton   02/06/14 - reworked flow for gift card activation error
 *                         scenarios
 *    jswan     10/21/11 - Modified to support changing the gift card number
 *                         during activation. No longer depends and matching
 *                         the masked account number, which maybe masked with
 *                         different characters or number of digits.
 *    asinton   08/18/11 - added journaling of activation, deactivation,
 *                         inquiring to tender.activation service.
 *    asinton   08/02/11 - fixed case where new response for failed request was
 *                         not updated at the matching index in
 *                         ActivationCargo.
 *    cgreene   05/27/11 - move auth response objects into domain
 *    asinton   05/04/11 - New activation service for APF.
 *    asinton   05/03/11 - New activation service for APF.
 *    asinton   05/03/11 - New activation service for APF.
 *
 * ===========================================================================
 */
package oracle.retail.stores.pos.services.tender.activation;

import java.util.ArrayList;
import java.util.List;

import oracle.retail.stores.domain.manager.payment.AuthorizeTransferRequestIfc;
import oracle.retail.stores.domain.manager.payment.AuthorizeTransferResponseIfc;
import oracle.retail.stores.domain.transaction.TenderableTransactionIfc;
import oracle.retail.stores.pos.services.common.AbstractFinancialCargo;

/**
 * Cargo for the Activation service.  This cargo maintains the request and response
 * lists for authorization.
 * 
 * @author asinton
 * @since 13.4
 */
public class ActivationCargo extends AbstractFinancialCargo
{
    /**
     * serialVersionUID
     */
    private static final long serialVersionUID = 5447640703788528442L;

    /** List of Request objects */
    protected List<AuthorizeTransferRequestIfc> requestList;

    /**
     * List of line item sequence numbers that correspond to gift cards in the request list.
     * This required in case the gift card activate is declined and another gift card is substituted
     * for the original. 
     */
    protected List<Integer> lineNumberList;

    /** List of Response objects */
    protected List<AuthorizeTransferResponseIfc> responseList;

    /** current index of requests */
    protected int currentIndex = -1;

    /** Handle to TenderableTransaction for journaling */
    protected TenderableTransactionIfc transaction;

    /**
     * Flag to indicate that there is a transaction in progress
     */
    protected boolean transactionInProgress;

    /**
     * List of failed gift card activations for removal.
     */
    protected List<Integer> failedLineNumbersList;

    /**
     * Returns the current request object to be processed.
     * @return
     */
    public AuthorizeTransferRequestIfc getCurrentRequest()
    {
        AuthorizeTransferRequestIfc currentReqeust = null;
        if(requestList != null && requestList.size() > currentIndex)
        {
            currentReqeust = requestList.get(currentIndex);
        }
        return currentReqeust;
    }

    /**
     * Returns the current request object to be processed.
     * @return
     */
    public Integer getCurrentLineNumber()
    {
        Integer current = null;
        if(lineNumberList != null && lineNumberList.size() > currentIndex)
        {
            current = lineNumberList.get(currentIndex);
        }
        return current;
    }

    /**
     * Returns the current response object.
     * @return the current response object.
     */
    public AuthorizeTransferResponseIfc getCurrentResponse()
    {
        AuthorizeTransferResponseIfc currentResponse = null;
        if(getResponseList().size() > currentIndex)
        {
            currentResponse = getResponseList().get(currentIndex);
        }
        return currentResponse;
    }

    /**
     * Returns the list of response objects.
     * @return the list of response objects.
     */
    public List<AuthorizeTransferResponseIfc> getResponseList()
    {
        if(responseList == null)
        {
            responseList = new ArrayList<AuthorizeTransferResponseIfc>();
        }
        return responseList;
    }

    /**
     * Adds a response object to the list.
     * @param response
     */
    public void addResponse(AuthorizeTransferResponseIfc response)
    {
        getResponseList().add(response);
    }

    /**
     * Sets the list of request objects in this cargo.
     * @param list
     */
    public void setRequestList(List<AuthorizeTransferRequestIfc> list)
    {
        this.requestList = list;
    }

    /**
     * @return the lineNumberList
     */
    public List<Integer> getLineNumberList()
    {
        return lineNumberList;
    }

    /**
     * @param lineNumberList the lineNumberList to set
     */
    public void setLineNumberList(List<Integer> lineNumberList)
    {
        this.lineNumberList = lineNumberList;
    }

    /**
     * Increments the current index.  This method should only be called by the
     * EvaluateRequestListSite.
     */
    public void incrementCurrentIndex()
    {
        currentIndex++;
    }

    /**
     * Removes the Response instance at the current index.
     */
    public void removeCurrentResponse()
    {
        getResponseList().remove(currentIndex);
    }

    /**
     * @return the transaction
     */
    public TenderableTransactionIfc getTransaction()
    {
        return transaction;
    }

    /**
     * @param transaction the transaction to set
     */
    public void setTransaction(TenderableTransactionIfc transaction)
    {
        this.transaction = transaction;
    }

    /**
     * @return the transactionInProgress
     */
    public boolean isTransactionInProgress()
    {
        return transactionInProgress;
    }

    /**
     * @param transactionInProgress the transactionInProgress to set
     */
    public void setTransactionInProgress(boolean transactionInProgress)
    {
        this.transactionInProgress = transactionInProgress;
    }

    /**
     * Returns a list of failed gift card activation transaction line numbers.
     * @return a list of failed gift card activation transaction line numbers.
     */
    public List<Integer> getFailedLineNumbersList()
    {
        if(failedLineNumbersList == null)
        {
            if(getLineNumberList() != null)
            {
                failedLineNumbersList = new ArrayList<Integer>(getLineNumberList().size());
            }
            else
            {
                failedLineNumbersList = new ArrayList<Integer>();
            }
        }
        return failedLineNumbersList;
    }

    /**
     * Adds a transaction line number to the list of failed line numbers.
     * @param failedLineNumber the failed transaction line number to add to the list
     */
    public void addFailedLineNumber(int failedLineNumber)
    {
        List<Integer> list = getFailedLineNumbersList();
        list.add(failedLineNumber);
    }
}
