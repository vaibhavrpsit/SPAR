/* ===========================================================================
* Copyright (c) 1998, 2011, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/modules/domain/src/oracle/retail/stores/domain/arts/UpdateReturnedItemsDataTransaction.java /rgbustores_13.4x_generic_branch/1 2011/05/04 11:49:03 mszekely Exp $
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
 *    3    360Commerce 1.2         3/31/2005 4:30:40 PM   Robert Pearse   
 *    2    360Commerce 1.1         3/10/2005 10:26:35 AM  Robert Pearse   
 *    1    360Commerce 1.0         2/11/2005 12:15:26 PM  Robert Pearse   
 *
 *   Revision 1.7  2004/09/23 00:30:50  kmcbride
 *   @scr 7211: Inserting serialVersionUIDs in these Serializable classes
 *
 *   Revision 1.6  2004/04/09 16:55:47  cdb
 *   @scr 4302 Removed double semicolon warnings.
 *
 *   Revision 1.5  2004/02/17 17:57:38  bwf
 *   @scr 0 Organize imports.
 *
 *   Revision 1.4  2004/02/17 16:18:50  rhafernik
 *   @scr 0 log4j conversion
 *
 *   Revision 1.3  2004/02/12 17:13:20  mcs
 *   Forcing head revision
 *
 *   Revision 1.2  2004/02/11 23:25:26  bwf
 *   @scr 0 Organize imports.
 *
 *   Revision 1.1.1.1  2004/02/11 01:04:29  cschellenger
 *   updating to pvcs 360store-current
 *
 *
 * 
 *    Rev 1.0   Aug 29 2003 15:34:24   CSchellenger
 * Initial revision.
 * 
 *    Rev 1.0   Jun 03 2002 16:43:16   msg
 * Initial revision.
 * 
 *    Rev 1.1   Mar 18 2002 22:52:06   msg
 * - updated copyright
 * 
 *    Rev 1.0   Mar 18 2002 12:11:18   msg
 * Initial revision.
 * 
 *    Rev 1.0   Sep 20 2001 15:55:46   msg
 * Initial revision.
 * 
 *    Rev 1.1   Sep 17 2001 12:33:08   msg
 * header update
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
    The DataTransaction to update returned items on original transactions.
    @version $Revision: /rgbustores_13.4x_generic_branch/1 $
        @see oracle.retail.stores.domain.arts.ReadTransactionsByIDDataTransaction
        @see oracle.retail.stores.domain.arts.TransactionReadDataTransaction
        @see oracle.retail.stores.domain.arts.TransactionWriteDataTransaction
        @see oracle.retail.stores.domain.arts.TransactionHistoryDataTransaction
**/
//-------------------------------------------------------------------------
public class UpdateReturnedItemsDataTransaction extends DataTransaction
                                        implements DataTransactionIfc
{
    // This id is used to tell
    // the compiler not to generate a
    // new serialVersionUID.
    //
    static final long serialVersionUID = 845599148521697218L;

    /** 
        The logger to which log messages will be sent.
    **/
    private static Logger logger = Logger.getLogger(oracle.retail.stores.domain.arts.UpdateReturnedItemsDataTransaction.class);

    /**
       revision number of this class
    **/
    public static String revisionNumber = "$Revision: /rgbustores_13.4x_generic_branch/1 $";
    /**
       The default name that links this transaction to a command within DataScript.
    **/
    public static String dataCommandName = "UpdateReturnedItemsDataTransaction";

    //---------------------------------------------------------------------
    /**
       Class constructor. <P>
    **/
    //---------------------------------------------------------------------
    public UpdateReturnedItemsDataTransaction()
    {
        super(dataCommandName);
    }

    //---------------------------------------------------------------------
    /**
       Class constructor. <P>
       @param name transaction name
    **/
    //---------------------------------------------------------------------
    public UpdateReturnedItemsDataTransaction(String name)
    {
        super(name);
    }

    //---------------------------------------------------------------------
    /**
       Updates returned-item quantity on original transactions, using
       return transactions.
       @param  transaction The Transaction object to save
       @exception  DataException when an error occurs.
    **/
    //---------------------------------------------------------------------
    public void updateReturnedItems(SaleReturnTransactionIfc[] transactions) throws DataException
    {
        if (logger.isDebugEnabled()) logger.debug(
                     "UpdateReturnedItemsDataTransaction.updateReturnedItems");

        // Add a DataAction to update all the line items in the Transaction
        DataActionIfc[] dataActions = new DataActionIfc[1];
        DataAction da = new DataAction();
        da.setDataOperationName("UpdateReturnedLineItems");
        da.setDataObject(transactions);
        dataActions[0] = da;
        setDataActions(dataActions);
        getDataManager().execute(this);

        if (logger.isDebugEnabled()) logger.debug(
                    "UpdateReturnedItemsDataTransaction.updateReturnedItems");
    }

    //---------------------------------------------------------------------
    /**
       Updates returned-item quantity on original transactions, using
       return transactions.
       @param  transaction The Transaction object to save
       @exception  DataException when an error occurs.
    **/
    //---------------------------------------------------------------------
    public void updateVoidedReturnedItems(AbstractTransactionLineItemIfc[] lineItems) throws DataException
    {
        if (logger.isDebugEnabled()) logger.debug(
                     "UpdateReturnedItemsDataTransaction.updateVoidedReturnedItems");

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
            da.setDataOperationName("UpdateVoidedReturnedLineItems");
            da.setDataObject(returnLineItems);
            dataActions[0] = da;
            setDataActions(dataActions);
            getDataManager().execute(this);
        }

        if (logger.isDebugEnabled()) logger.debug(
                    "UpdateReturnedItemsDataTransaction.updateVoidedReturnedItems");
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
        String strResult = new String("Class: UpdateReturnedItemsDataTransaction (Revision "
                                      + getRevisionNumber() + ") @" + hashCode());
        return(strResult);
    }
}
