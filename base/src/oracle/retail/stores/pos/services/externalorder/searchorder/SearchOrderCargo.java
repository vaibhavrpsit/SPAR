/* ===========================================================================
* Copyright (c) 2010, 2011, Oracle and/or its affiliates. All rights reserved. 
* ===========================================================================
* $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/externalorder/searchorder/SearchOrderCargo.java /rgbustores_13.4x_generic_branch/1 2011/05/05 14:05:58 mszekely Exp $
* ===========================================================================
* NOTES
* <other useful comments, qualifications, etc.>
*
* MODIFIED (MM/DD/YY)
*    sgu    07/22/10 - pass original return transaction from external order to
*                      the sale cargo
*    cgreen 05/26/10 - convert to oracle packaging
*    abonda 05/19/10 - search flow update
*    acadar 05/17/10 - incorporated feedback from code review
*    acadar 05/17/10 - additional logic added for processing orders
*    acadar 05/14/10 - initial version for external order processing
*    sgu    05/14/10 - repackage external order classes
*    abonda 05/12/10 - Search external orders flow
*    acadar 05/03/10 - added logic for searching for external orders by the
*                      default search criteria
*    acadar 05/03/10 - initial checkin for external order search
*    acadar 05/03/10 - external order search initial check in
* ===========================================================================
*/
package oracle.retail.stores.pos.services.externalorder.searchorder;

// java imports
import java.io.Serializable;
import java.util.List;
import java.util.Vector;

import oracle.retail.stores.commerceservices.externalorder.ExternalOrderIfc;
import oracle.retail.stores.commerceservices.externalorder.ExternalOrderSearchCriteriaIfc;

import oracle.retail.stores.pos.services.common.AbstractFinancialCargo;
import oracle.retail.stores.pos.services.common.TimedCargoIfc;
import oracle.retail.stores.domain.transaction.SaleReturnTransactionIfc;
import oracle.retail.stores.foundation.tour.ifc.CargoIfc;

/**
    This is the cargo used by the external order search service. <p>
**/
public class SearchOrderCargo extends AbstractFinancialCargo implements TimedCargoIfc,CargoIfc, Serializable
{

    private static final long serialVersionUID = 2894186472608151690L;


    /**
     * Show whether or not this transaction has timed out
     */
    protected boolean timeout = false;

    /**
     * External Order search criteria
     */

    protected ExternalOrderSearchCriteriaIfc searchCriteria;

    /**
     * External Order
     */
    protected ExternalOrderIfc externalOrder;

    /**
     * Sale Return Transaction
     */
    protected SaleReturnTransactionIfc transaction;


    /**
     * External Orders list
     */
    protected List<ExternalOrderIfc> externalOrdersList;

    /**
     * Exception error code returned during search
     */
    protected int exceptionErrorCode;

    /**
     * This vector contains a list of SaleReturnTransacions on which
     * returns have been completed.
     **/
    protected Vector<SaleReturnTransactionIfc> originalReturnTransactions = null;

	/**
     * Tell whether or not a timeout has
     * occurred
     *
     * @return
     * @see oracle.retail.stores.pos.services.common.TimedCargoIfc#isTimeout()
     */
    public boolean isTimeout()
    {
        return this.timeout;
    }

    /**
     * Set whether or not a timeout
     * has occurred
     *
     * @param aValue
     * @see oracle.retail.stores.pos.services.common.TimedCargoIfc#setTimeout(boolean)
     */
    public void setTimeout(boolean aValue)
    {
        this.timeout = aValue;
    }

    /**
     * @return the searchCriteria
     */
    public ExternalOrderSearchCriteriaIfc getSearchCriteria()
    {
        return searchCriteria;
    }

    /**
     * @param searchCriteria the searchCriteria to set
     */
    public void setSearchCriteria(ExternalOrderSearchCriteriaIfc searchCriteria)
    {
        this.searchCriteria = searchCriteria;
    }

    /**
     * @return the externalOrder
     */
    public ExternalOrderIfc getExternalOrder()
    {
        return externalOrder;
    }

    /**
     * @param externalOrder the externalOrder to set
     */
    public void setExternalOrder(ExternalOrderIfc externalOrder)
    {
        this.externalOrder = externalOrder;
    }

    /**
     * @return the transaction
     */
    public SaleReturnTransactionIfc getTransaction()
    {
        return transaction;
    }

    /**
     * @param transaction the transaction to set
     */
    public void setTransaction(SaleReturnTransactionIfc transaction)
    {
        this.transaction = transaction;
    }

    /**
     * @return the externalOrdersList as an array
     */
    public ExternalOrderIfc[] getExternalOrdersList()
    {
    	ExternalOrderIfc[] temp = null;

        temp = new ExternalOrderIfc[externalOrdersList.size()];
        externalOrdersList.toArray(temp);

		return temp;
	}

    /**
     * @param externalOrdersList
     */
	public void setExternalOrdersList(List<ExternalOrderIfc> externalOrdersList)
	{
		this.externalOrdersList = externalOrdersList;
	}

	/**
	 * @return exception error code
	 */
	public int getExceptionErrorCode()
	{
		return exceptionErrorCode;
	}

	/**
	 * @param exceptionErrorCode
	 */
	public void setExceptionErrorCode(int exceptionErrorCode)
	{
		this.exceptionErrorCode = exceptionErrorCode;
	}

    //--------------------------------------------------------------------------
    /**
       Retrieve the array of transactions on which items have been returned.
       This cargo does not track this data.

       @return SaleReturnTransactionIfc[]
    **/
    //--------------------------------------------------------------------------
    public SaleReturnTransactionIfc[] getOriginalReturnTransactions()
    {
        SaleReturnTransactionIfc[] transactions = null;

        if (originalReturnTransactions != null)
        {
            transactions = new SaleReturnTransactionIfc[originalReturnTransactions.size()];
            originalReturnTransactions.copyInto((SaleReturnTransactionIfc[]) transactions);
        }
        return transactions;
    }

    //--------------------------------------------------------------------------
    /**
       Sets the array of transactions on which items have been returned.
       This cargo does not track this data.

       @param origTxns retrieved return transactions
    **/
    //--------------------------------------------------------------------------
    public void setOriginalReturnTransactions(SaleReturnTransactionIfc[] origTxns)
    {
        originalReturnTransactions = new Vector<SaleReturnTransactionIfc>();
        for(int i = 0; (origTxns != null) && (i < origTxns.length); i++)
        {
            originalReturnTransactions.addElement(origTxns[i]);
        }
    }
}


