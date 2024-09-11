/* ===========================================================================
* Copyright (c) 2009, 2012, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/modules/domain/src/oracle/retail/stores/domain/ixretail/log/POSLogTransactionEntry.java /main/15 2012/09/24 15:23:54 cgreene Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    cgreene   09/24/12 - Implement maximum customer record retrieval for dtm
 *                         export
 *    cgreene   05/26/10 - convert to oracle packaging
 *    abondala  01/03/10 - update header date
 *
 * ===========================================================================
 * $Log:
 *    6    360Commerce 1.5         4/12/2008 5:44:57 PM   Christian Greene
 *         Upgrade StringBuffer to StringBuilder
 *    5    360Commerce 1.4         5/1/2007 9:45:27 AM    Jack G. Swan
 *         Changes for merge to Trunk.
 *    4    360Commerce 1.3         11/9/2006 7:28:31 PM   Jack G. Swan
 *         Modifided for XML Data Replication and CTR.
 *    3    360Commerce 1.2         3/31/2005 4:29:26 PM   Robert Pearse
 *    2    360Commerce 1.1         3/10/2005 10:24:16 AM  Robert Pearse
 *    1    360Commerce 1.0         2/11/2005 12:13:18 PM  Robert Pearse
 *
 *   Revision 1.4  2004/09/23 00:30:48  kmcbride
 *   @scr 7211: Inserting serialVersionUIDs in these Serializable classes
 *
 *   Revision 1.3  2004/02/12 17:13:45  mcs
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
 *    Rev 1.0   Aug 29 2003 15:36:32   CSchellenger
 * Initial revision.
 *
 *    Rev 1.1   Jan 22 2003 09:58:44   mpm
 * Preliminary merging of 5.1/5.5 code.
 * Resolution for Domain SCR-104: Merge 5.1/5.5 into 6.0
 * ===========================================================================
 */
package oracle.retail.stores.domain.ixretail.log;

import oracle.retail.stores.domain.DomainGateway;
import oracle.retail.stores.domain.transaction.TransactionIDIfc;
import oracle.retail.stores.domain.utility.EYSDate;
import oracle.retail.stores.foundation.utility.Util;

/**
 * This class identifies a transaction entry in a TLog batch. The class consists
 * of the transaction ID, the business date and the batch identifier.
 * 
 * @version $Revision: /main/15 $
 */
public class POSLogTransactionEntry implements POSLogTransactionEntryIfc
{
    // This id is used to tell the compiler not to generate a new serialVersionUID.
    static final long serialVersionUID = 4291075973083631617L;

    /**
     * revision number supplied by source-code-control system
     */
    public static final String revisionNumber = "$Revision: /main/15 $";

    /**
     * transaction identifier
     */
    protected TransactionIDIfc transactionID = null;

    /**
     * business date
     */
    protected EYSDate businessDate = null;

    /**
     * batch identifier
     */
    protected String batchID = NO_BATCH_IDENTIFIED;

    /**
     * batch identifier
     */
    protected int columnID = USE_BATCH_ARCHIVE;

    /**
     * transaction start time
     */
    protected EYSDate startTime = null;

    /**
     * transaction end time
     */
    protected EYSDate endTime = null;

    /**
     * transaction type
     */
    protected String transactionType = null;

    /**
     * maximum transactions to export identifier.
     */
    protected int maximumTransactionsToExport = MAX_TRANSACTIONS_TO_EXPORT;

    /**
     * Constructs POSLogTransactionEntry object.
     * <P>
     */
    public POSLogTransactionEntry()
    {
    }

    /**
     * Creates clone of this object.
     * 
     * @return Object clone of this object
     */
    public Object clone()
    {
        // instantiate new object
        POSLogTransactionEntry c = new POSLogTransactionEntry();

        // set values
        setCloneAttributes(c);

        // pass back Object
        return c;
    }

    /**
     * Sets attributes in clone of this object.
     * 
     * @param newClass new instance of object
     */
    public void setCloneAttributes(POSLogTransactionEntry newClass)
    {
        if (transactionID != null)
        {
            newClass.setTransactionID((TransactionIDIfc) getTransactionID().clone());
        }
        if (businessDate != null)
        {
            newClass.setBusinessDate((EYSDate) getBusinessDate().clone());
        }
        newClass.setBatchID(getBatchID());
    }

    /**
     * Determine if two objects are identical.
     * 
     * @param obj object to compare with
     * @return true if the objects are identical, false otherwise
     */
    @Override
    public boolean equals(Object obj)
    {
        boolean isEqual = true;
        // confirm object instanceof this object
        if (obj instanceof POSLogTransactionEntry)
        {
            POSLogTransactionEntry c = (POSLogTransactionEntry) obj; // downcast the input object

            // compare all the attributes of POSLogTransactionEntry
            if (Util.isObjectEqual(getTransactionID(), c.getTransactionID())
                    && Util.isObjectEqual(getBusinessDate(), c.getBusinessDate())
                    && Util.isObjectEqual(getBatchID(), c.getBatchID()))
            {
                isEqual = true; // set the return code to true
            }
            else
            {
                isEqual = false; // set the return code to false
            }
        }
        else
        {
            isEqual = false;
        }
        return (isEqual);
    }

    /**
     * Retrieves transaction identifier.
     * 
     * @return transaction identifier
     */
    public TransactionIDIfc getTransactionID()
    {
        return (transactionID);
    }

    /**
     * Sets transaction identifier.
     * 
     * @param value transaction identifier
     */
    public void setTransactionID(TransactionIDIfc value)
    {
        transactionID = value;
    }

    /**
     * Retrieves business date.
     * 
     * @return business date
     */
    public EYSDate getBusinessDate()
    {
        return (businessDate);
    }

    /**
     * Sets business date.
     * 
     * @param value business date
     */
    public void setBusinessDate(EYSDate value)
    {
        businessDate = value;
    }

    /**
     * Retrieves batch identifier.
     * 
     * @return batch identifier
     */
    public String getBatchID()
    {
        return (batchID);
    }

    /**
     * Sets batch identifier.
     * 
     * @param value batch identifier
     */
    public void setBatchID(String value)
    {
        batchID = value;
    }

    /**
     * Returns store identifier. If no transactionID exists, returns null.
     * 
     * @return store identifier
     */
    public String getStoreID()
    {
        String storeID = null;
        if (getTransactionID() != null)
        {
            storeID = getTransactionID().getStoreID();
        }
        return (storeID);
    }

    /**
     * Sets store identifier. If no transaction ID exists, one is instantiated.
     * 
     * @param value store identifier
     */
    public void setStoreID(String value)
    {
        if (getTransactionID() == null)
        {
            transactionID = DomainGateway.getFactory().getTransactionIDInstance();
        }
        getTransactionID().setStoreID(value);
    }

    /**
     * Retrieves column identifier.
     * 
     * @return column identifier
     */
    public int getColumnID()
    {
        return columnID;
    }

    /**
     * Sets column identifier.
     * 
     * @param value column identifier
     */
    public void setColumnID(int value)
    {
        columnID = value;
    }

    /**
     * Returns default display string.
     * 
     * @return String representation of object
     */
    @Override
    public String toString()
    {
        // build result string
        StringBuilder strResult = Util.classToStringHeader("POSLogTransactionEntry", getRevisionNumber(), hashCode());

        // add attributes to string
        if (getTransactionID() == null)
        {
            strResult.append("transactionID:                      [null]").append(Util.EOL);
        }
        else
        {
            strResult.append(getTransactionID().toString());
        }
        if (getBusinessDate() == null)
        {
            strResult.append("businessDate:                       [null]").append(Util.EOL);
        }
        else
        {
            strResult.append("businessDate:                       ").append("[").append(getBusinessDate()).append("]")
                    .append(Util.EOL);
        }
        strResult.append("batchID:                            ").append("[").append(getBatchID()).append("]")
                .append(Util.EOL);

        strResult.append("maximumTransactionsToExport:                    ").append("[")
                .append(getMaximumTransactionsToExport()).append("]").append(Util.EOL);

        // pass back result
        return (strResult.toString());
    }

    /**
     * Retrieves the source-code-control system revision number.
     * 
     * @return String representation of revision number
     */
    public String getRevisionNumber()
    {
        // return string
        return (revisionNumber);
    }

    /**
     * @return Returns the endTime.
     */
    public EYSDate getEndTime()
    {
        return endTime;
    }

    /**
     * @param endTime The endTime to set.
     */
    public void setEndTime(EYSDate endTime)
    {
        this.endTime = endTime;
    }

    /**
     * @return Returns the startTime.
     */
    public EYSDate getStartTime()
    {
        return startTime;
    }

    /**
     * @param startTime The startTime to set.
     */
    public void setStartTime(EYSDate startTime)
    {
        this.startTime = startTime;
    }

    /**
     * Retrieves MaximumTransactionsToExport .
     * 
     * @return MaximumTransactionsToExport identifier
     */
    public int getMaximumTransactionsToExport()
    {
        return maximumTransactionsToExport;
    }

    /**
     * Sets MaximumTransactionsToExport identifier.
     * 
     * @param value MaximumTransactionsToExport identifier
     */
    public void setMaximumTransactionsToExport(int value)
    {
        maximumTransactionsToExport = value;
    }
}
