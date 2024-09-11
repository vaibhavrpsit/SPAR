/* ===========================================================================
* Copyright (c) 1998, 2011, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/modules/domain/src/oracle/retail/stores/domain/arts/TransactionHistoryDataTransaction.java /rgbustores_13.4x_generic_branch/1 2011/05/04 11:48:55 mszekely Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED (MM/DD/YY)
 *    cgreen 01/27/11 - refactor creation of data transactions to use spring
 *                      context
 *    cgreen 09/14/10 - simple formatting
 *    cgreen 05/28/10 - convert to oracle packaging
 *    cgreen 05/27/10 - convert to oracle packaging
 *    cgreen 05/27/10 - convert to oracle packaging
 *    cgreen 05/26/10 - convert to oracle packaging
 *    abonda 01/03/10 - update header date
 *
 * ===========================================================================

     $Log:
      3    360Commerce 1.2         3/31/2005 4:30:34 PM   Robert Pearse   
      2    360Commerce 1.1         3/10/2005 10:26:22 AM  Robert Pearse   
      1    360Commerce 1.0         2/11/2005 12:15:14 PM  Robert Pearse   
     $
     Revision 1.7  2004/09/23 00:30:50  kmcbride
     @scr 7211: Inserting serialVersionUIDs in these Serializable classes

     Revision 1.6  2004/04/09 16:55:47  cdb
     @scr 4302 Removed double semicolon warnings.

     Revision 1.5  2004/02/17 17:57:38  bwf
     @scr 0 Organize imports.

     Revision 1.4  2004/02/17 16:18:47  rhafernik
     @scr 0 log4j conversion

     Revision 1.3  2004/02/12 17:13:19  mcs
     Forcing head revision

     Revision 1.2  2004/02/11 23:25:26  bwf
     @scr 0 Organize imports.

     Revision 1.1.1.1  2004/02/11 01:04:29  cschellenger
     updating to pvcs 360store-current


 * 
 *    Rev 1.0   Aug 29 2003 15:34:20   CSchellenger
 * Initial revision.
 * 
 *    Rev 1.0   Jun 03 2002 16:43:06   msg
 * Initial revision.
 * 
 *    Rev 1.1   Mar 18 2002 22:51:54   msg
 * - updated copyright
 * 
 *    Rev 1.0   Mar 18 2002 12:11:06   msg
 * Initial revision.
 * 
 *    Rev 1.1   24 Oct 2001 17:11:00   baa
 * customer history. Allow all transactions but void to be display.
 * Resolution for POS SCR-209: Customer History
 *
 *    Rev 1.0   Sep 20 2001 15:59:50   msg
 * Initial revision.
 *
 *    Rev 1.1   Sep 17 2001 12:33:12   msg
 * header update
 */
package oracle.retail.stores.domain.arts;

import oracle.retail.stores.domain.transaction.SearchCriteriaIfc;
import oracle.retail.stores.domain.transaction.TransactionSummaryIfc;
import oracle.retail.stores.foundation.manager.data.DataException;
import oracle.retail.stores.foundation.manager.data.DataTransaction;
import oracle.retail.stores.foundation.manager.ifc.data.DataTransactionIfc;

import org.apache.log4j.Logger;

/**
 * The DataTransaction to read a transaction history for a given customer.
 * 
 * @version $Revision: /rgbustores_13.4x_generic_branch/1 $
 * @see oracle.retail.stores.domain.arts.TransactionReadDataTransaction
 * @see oracle.retail.stores.domain.arts.ReadTransactionsByIDDataTransaction
 * @see oracle.retail.stores.domain.arts.TransactionWriteDataTransaction
 * @see oracle.retail.stores.domain.arts.UpdateReturnedItemsDataTransaction
 */
public class TransactionHistoryDataTransaction extends DataTransaction implements DataTransactionIfc
{
    // This id is used to tell the compiler not to generate a new serialVersionUID.
    static final long serialVersionUID = 9194230277211115833L;

    /**
     * The logger to which log messages will be sent.
     */
    private static final Logger logger = Logger.getLogger(TransactionHistoryDataTransaction.class);

    /**
     * revision number of this class
     */
    public static final String revisionNumber = "$Revision: /rgbustores_13.4x_generic_branch/1 $";

    /**
     * The default name that links this transaction to a command within
     * DataScript.
     */
    public static final String dataCommandName = "TransactionHistoryDataTransaction";

    /**
     * Class constructor.
     */
    public TransactionHistoryDataTransaction()
    {
        super(dataCommandName);
    }

    /**
     * Class constructor.
     * 
     * @param name transaction name
     */
    public TransactionHistoryDataTransaction(String name)
    {
        super(name);
    }

    /**
     * Reads transaction summaries for a customer from a data store.
     * 
     * @param criteria
     * @return an array of transaction summaries
     * @throws DataException upon error
     */
    public TransactionSummaryIfc[] readTransactionHistory(SearchCriteriaIfc criteria) throws DataException
    {
        logger.debug("TransactionHistoryDataTransaction.readTransactionHistory(SearchCriteriaIfc criteria)");

        // Add a DataAction to update all the line items in the Transaction
        applyDataObject(criteria);

        TransactionSummaryIfc[] history = (TransactionSummaryIfc[]) getDataManager().execute(this);

        logger.debug("TransactionHistoryDataTransaction.readTransactionHistory(SearchCriteriaIfc criteria)");

        return history;
    }

    /**
     * Returns the revision number of this class.
     * 
     * @return String representation of revision number
     */
    public String getRevisionNumber()
    {
        return revisionNumber;
    }

    /**
     * Method to default display string function.
     * 
     * @return String representation of object
     */
    @Override
    public String toString()
    {
        // result string
        String strResult = new String("Class: TransactionHistoryDataTransaction (Revision " + getRevisionNumber()
                + ") @" + hashCode());
        return strResult;
    }
}
