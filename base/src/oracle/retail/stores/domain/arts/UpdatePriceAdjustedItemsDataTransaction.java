/* ===========================================================================
* Copyright (c) 2004, 2011, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/modules/domain/src/oracle/retail/stores/domain/arts/UpdatePriceAdjustedItemsDataTransaction.java /rgbustores_13.4x_generic_branch/1 2011/05/04 11:49:05 mszekely Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    cgreene   05/27/10 - convert to oracle packaging
 *    cgreene   05/26/10 - convert to oracle packaging
 *    abondala  01/03/10 - update header date
 *
 * ===========================================================================
 * $Log:
 *    3    360Commerce 1.2         3/31/2005 4:30:40 PM   Robert Pearse   
 *    2    360Commerce 1.1         3/10/2005 10:26:35 AM  Robert Pearse   
 *    1    360Commerce 1.0         2/11/2005 12:15:25 PM  Robert Pearse   
 *
 *   Revision 1.2  2004/09/23 00:30:50  kmcbride
 *   @scr 7211: Inserting serialVersionUIDs in these Serializable classes
 *
 *   Revision 1.1  2004/04/20 12:49:25  jriggins
 *   @scr 3979 Added UpdatePriceAdjustedItemsDataTransaction and associated operations
 *
 * ===========================================================================
 */
package oracle.retail.stores.domain.arts;
// java imports
import java.util.Vector;

import org.apache.log4j.Logger;

import oracle.retail.stores.domain.lineitem.AbstractTransactionLineItemIfc;
import oracle.retail.stores.domain.lineitem.ReturnItemIfc;
import oracle.retail.stores.domain.lineitem.SaleReturnLineItemIfc;
import oracle.retail.stores.domain.transaction.SaleReturnTransactionIfc;
import oracle.retail.stores.foundation.manager.data.DataAction;
import oracle.retail.stores.foundation.manager.data.DataException;
import oracle.retail.stores.foundation.manager.data.DataTransaction;
import oracle.retail.stores.foundation.manager.ifc.data.DataActionIfc;
import oracle.retail.stores.foundation.manager.ifc.data.DataTransactionIfc;

//-------------------------------------------------------------------------
/**
    The DataTransaction to update price adjusted items on original transactions.
    @version $Revision: /rgbustores_13.4x_generic_branch/1 $
        @see oracle.retail.stores.domain.arts.ReadTransactionsByIDDataTransaction
        @see oracle.retail.stores.domain.arts.TransactionReadDataTransaction
        @see oracle.retail.stores.domain.arts.TransactionWriteDataTransaction
        @see oracle.retail.stores.domain.arts.TransactionHistoryDataTransaction
**/
//-------------------------------------------------------------------------
public class UpdatePriceAdjustedItemsDataTransaction extends DataTransaction
                                        implements DataTransactionIfc
{
    // This id is used to tell
    // the compiler not to generate a
    // new serialVersionUID.
    //
    static final long serialVersionUID = -5876963664595558298L;

    /** 
        The logger to which log messages will be sent.
    **/
    private static Logger logger = Logger.getLogger(oracle.retail.stores.domain.arts.UpdatePriceAdjustedItemsDataTransaction.class);

    /**
       revision number of this class
    **/
    public static String revisionNumber = "$Revision: /rgbustores_13.4x_generic_branch/1 $";
    /**
       The default name that links this transaction to a command within DataScript.
    **/
    public static String dataCommandName = "UpdatePriceAdjustedItemsDataTransaction";

    //---------------------------------------------------------------------
    /**
       Class constructor. <P>
    **/
    //---------------------------------------------------------------------
    public UpdatePriceAdjustedItemsDataTransaction()
    {
        super(dataCommandName);
    }

    //---------------------------------------------------------------------
    /**
       Class constructor. <P>
       @param name transaction name
    **/
    //---------------------------------------------------------------------
    public UpdatePriceAdjustedItemsDataTransaction(String name)
    {
        super(name);
    }

    //---------------------------------------------------------------------
    /**
       Updates price adjusted prices on original transactions
       @param  transaction The Transaction object to update
       @exception  DataException when an error occurs.
    **/
    //---------------------------------------------------------------------
    public void updatePriceAdjustedItems(SaleReturnTransactionIfc[] transactions) throws DataException
    {
        if (logger.isDebugEnabled()) logger.debug(
                     "UpdatePriceAdjustedItemsDataTransaction.updateReturnedItems");

        // Add a DataAction to update all the line items in the Transaction
        DataActionIfc[] dataActions = new DataActionIfc[1];
        DataAction da = new DataAction();
        da.setDataOperationName("UpdatePriceAdjustedLineItems");
        da.setDataObject(transactions);
        dataActions[0] = da;
        setDataActions(dataActions);
        getDataManager().execute(this);

        if (logger.isDebugEnabled()) logger.debug(
                    "UpdatePriceAdjustedItemsDataTransaction.updateReturnedItems");
    }

    //---------------------------------------------------------------------
    /**
       Updates price adjusted prices on original transactions
       @param  transaction The Transaction object to save
       @exception  DataException when an error occurs.
    **/
    //---------------------------------------------------------------------
    public void updateVoidedPriceAdjustedItems(AbstractTransactionLineItemIfc[] lineItems) throws DataException
    {
        if (logger.isDebugEnabled()) logger.debug(
                     "UpdatePriceAdjustedItemsDataTransaction.updateVoidedPriceAdjustedItems");

        // Find all the line items that can be updated.
        Vector lineItemVector = new Vector();
        for (int i = 0; i < lineItems.length; i++)
        {
            // Is there a return item with original transaction info?
            if (lineItems[i] instanceof SaleReturnLineItemIfc)
            {
                ReturnItemIfc ri = ((SaleReturnLineItemIfc) lineItems[i]).getReturnItem();
                if (ri != null && ri.getOriginalLineNumber() > -1)
                {
                    lineItemVector.addElement(lineItems[i]);
                }
            }
        }

        if (lineItemVector.size() > 0)
        {
            SaleReturnLineItemIfc[] returnLineItems =
                new SaleReturnLineItemIfc[lineItemVector.size()];
            lineItemVector.copyInto(returnLineItems);

            // Add a DataAction to update all the line items in the array
            DataActionIfc[] dataActions = new DataActionIfc[1];
            DataAction da = new DataAction();
            da.setDataOperationName("UpdateVoidedPriceAdjustedLineItems");
            da.setDataObject(returnLineItems);
            dataActions[0] = da;
            setDataActions(dataActions);
            getDataManager().execute(this);
        }

        if (logger.isDebugEnabled()) logger.debug(
                    "UpdatePriceAdjustedItemsDataTransaction.updateVoidedPriceAdjustedItems");
    }

    //---------------------------------------------------------------------
    /**
       Returns the revision number of this class.
       @return String representation of revision number
    **/
    //---------------------------------------------------------------------
    public String getRevisionNumber()
    {
        return(revisionNumber);
    }

    //---------------------------------------------------------------------
    /**
       Method to default display string function.
       @return String representation of object
    **/
    //---------------------------------------------------------------------
    public String toString()
    {
        // result string
        String strResult = new String("Class: UpdatePriceAdjustedItemsDataTransaction (Revision "
                                      + getRevisionNumber() + ") @" + hashCode());
        return(strResult);
    }
}
