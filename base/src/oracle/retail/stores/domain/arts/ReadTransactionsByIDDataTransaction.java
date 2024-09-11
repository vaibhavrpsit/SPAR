/* ===========================================================================
* Copyright (c) 1998, 2011, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/modules/domain/src/oracle/retail/stores/domain/arts/ReadTransactionsByIDDataTransaction.java /rgbustores_13.4x_generic_branch/1 2011/05/04 11:49:03 mszekely Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    cgreene   01/27/11 - refactor creation of data transactions to use spring
 *                         context
 *    cgreene   05/28/10 - convert to oracle packaging
 *    cgreene   05/27/10 - convert to oracle packaging
 *    cgreene   05/27/10 - convert to oracle packaging
 *    cgreene   05/26/10 - convert to oracle packaging
 *    abondala  01/03/10 - update header date
 *
 * ===========================================================================
 * $Log:
 *    3    360Commerce 1.2         3/31/2005 4:29:34 PM   Robert Pearse   
 *    2    360Commerce 1.1         3/10/2005 10:24:31 AM  Robert Pearse   
 *    1    360Commerce 1.0         2/11/2005 12:13:33 PM  Robert Pearse   
 *
 *   Revision 1.7  2004/09/23 00:30:50  kmcbride
 *   @scr 7211: Inserting serialVersionUIDs in these Serializable classes
 *
 *   Revision 1.6  2004/04/09 16:55:46  cdb
 *   @scr 4302 Removed double semicolon warnings.
 *
 *   Revision 1.5  2004/02/17 17:57:37  bwf
 *   @scr 0 Organize imports.
 *
 *   Revision 1.4  2004/02/17 16:18:47  rhafernik
 *   @scr 0 log4j conversion
 *
 *   Revision 1.3  2004/02/12 17:13:19  mcs
 *   Forcing head revision
 *
 *   Revision 1.2  2004/02/11 23:25:24  bwf
 *   @scr 0 Organize imports.
 *
 *   Revision 1.1.1.1  2004/02/11 01:04:29  cschellenger
 *   updating to pvcs 360store-current
 *
 *
 * 
 *    Rev 1.0   Aug 29 2003 15:34:00   CSchellenger
 * Initial revision.
 * 
 *    Rev 1.0   Jun 03 2002 16:42:24   msg
 * Initial revision.
 * 
 *    Rev 1.1   Mar 18 2002 22:51:04   msg
 * - updated copyright
 * 
 *    Rev 1.0   Mar 18 2002 12:10:28   msg
 * Initial revision.
 * 
 *    Rev 1.0   Sep 20 2001 15:55:48   msg
 * Initial revision.
 * 
 *    Rev 1.1   Sep 17 2001 12:33:22   msg
 * header update
 * ===========================================================================
 */
package oracle.retail.stores.domain.arts;

import oracle.retail.stores.domain.transaction.TransactionIDIfc;
import oracle.retail.stores.domain.transaction.TransactionIfc;
import oracle.retail.stores.domain.utility.EYSDate;
import oracle.retail.stores.foundation.manager.data.DataException;
import oracle.retail.stores.foundation.manager.data.DataTransaction;
import oracle.retail.stores.foundation.manager.ifc.data.DataTransactionIfc;

import org.apache.log4j.Logger;

/**
 * The DataTransaction to read transactions by the transaction ID.
 * 
 * @version $Revision: /rgbustores_13.4x_generic_branch/1 $
 * @see oracle.retail.stores.domain.arts.TransactionReadDataTransaction
 * @see oracle.retail.stores.domain.arts.TransactionWriteDataTransaction
 * @see oracle.retail.stores.domain.arts.UpdateReturnedItemsDataTransaction
 * @see oracle.retail.stores.domain.arts.TransactionHistoryDataTransaction
 */
public class ReadTransactionsByIDDataTransaction extends DataTransaction implements DataTransactionIfc
{
    // This id is used to tell the compiler not to generate a new serialVersionUID.
    static final long serialVersionUID = -6267251975868534654L;

    /**
     * The logger to which log messages will be sent.
     */
    private static final Logger logger = Logger.getLogger(ReadTransactionsByIDDataTransaction.class);

    /**
     * revision number of this class
     */
    public static final String revisionNumber = "$Revision: /rgbustores_13.4x_generic_branch/1 $";

    /**
     * The default name that links this transaction to a command within
     * DataScript.
     */
    public static String dataCommandName = "ReadTransactionsByIDDataTransaction";

    /**
     * Class constructor.
     */
    public ReadTransactionsByIDDataTransaction()
    {
        super(dataCommandName);
    }

    /**
     * Class constructor.
     * 
     * @param name transaction name
     */
    public ReadTransactionsByIDDataTransaction(String name)
    {
        super(name);
    }

    /**
     * Reads transactions matching a specific transaction ID over a given date
     * range.
     * 
     * @param transactionID transaction ID object
     * @param beginDate begin date of date range
     * @param endDate end date of date range
     * @return an array of transactions
     * @exception DataException upon error
     */
    public TransactionIfc[] readTransactionsByID(TransactionIDIfc transactionID, EYSDate beginDate, EYSDate endDate)
            throws DataException
    {
        if (logger.isDebugEnabled())
            logger.debug("ReadTransactionsByIDDataTransaction.readTransactionsByID");

        // transaction-ID-date-range key
        ARTSTransactionIDDateRange transactionIDDateRange = new ARTSTransactionIDDateRange();

        transactionIDDateRange.setTransactionID(transactionID);
        transactionIDDateRange.setBeginDate(beginDate);
        transactionIDDateRange.setEndDate(endDate);

        // Add a DataAction to update all the line items in the Transaction
        applyDataObject(transactionIDDateRange);

        // set data actions and execute
        TransactionIfc[] transactions = (TransactionIfc[]) getDataManager().execute(this);

        if (logger.isDebugEnabled())
            logger.debug("ReadTransactionsByIDDataTransaction.readTransactionsByID");

        return (transactions);
    }

    /**
     * Reads transactions matching a specific transaction ID with on specified
     * date range.
     * 
     * @param transactionID transaction ID object
     * @return an array of transactions
     * @exception DataException upon error
     * @see #readTransactionsByID(TransactionIDIfc, EYSDate, EYSDate)
     */
    public TransactionIfc[] readTransactionsByID(TransactionIDIfc transactionID) throws DataException
    {
        return (readTransactionsByID(transactionID, (EYSDate) null, (EYSDate) null));
    }

    /**
     * Returns the revision number of this class.
     * 
     * @return String representation of revision number
     */
    public String getRevisionNumber()
    {
        return (revisionNumber);
    }

    /* (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString()
    {
        // result string
        String strResult = new String("Class: ReadTransactionsByIDDataTransaction (Revision " + getRevisionNumber()
                + ") @" + hashCode());
        return (strResult);
    }
}
