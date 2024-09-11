/* ===========================================================================
* Copyright (c) 1998, 2011, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/modules/domain/src/oracle/retail/stores/domain/ixretail/log/BatchTotal.java /rgbustores_13.4x_generic_branch/1 2011/05/04 11:49:08 mszekely Exp $
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
 *    5    360Commerce 1.4         4/12/2008 5:44:57 PM   Christian Greene
 *         Upgrade StringBuffer to StringBuilder
 *    4    360Commerce 1.3         4/25/2007 10:00:46 AM  Anda D. Cadar   I18N
 *         merge
 *    3    360Commerce 1.2         3/31/2005 4:27:16 PM   Robert Pearse   
 *    2    360Commerce 1.1         3/10/2005 10:19:48 AM  Robert Pearse   
 *    1    360Commerce 1.0         2/11/2005 12:09:34 PM  Robert Pearse   
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
 *    Rev 1.0   Aug 29 2003 15:36:28   CSchellenger
 * Initial revision.
 * 
 *    Rev 1.1   Jan 22 2003 09:58:50   mpm
 * Preliminary merging of 5.1/5.5 code.
 * Resolution for Domain SCR-104: Merge 5.1/5.5 into 6.0
 * ===========================================================================
 */
package oracle.retail.stores.domain.ixretail.log;
// java imports
import java.text.SimpleDateFormat;

import oracle.retail.stores.commerceservices.common.currency.CurrencyIfc;
import oracle.retail.stores.domain.DomainGateway;
import oracle.retail.stores.domain.utility.EYSDate;
import oracle.retail.stores.foundation.utility.Util;

//----------------------------------------------------------------------------
/**
     This class is used to record totals and other information about a batch of logged transactions. <P>
     @version $Revision: /rgbustores_13.4x_generic_branch/1 $
**/
//----------------------------------------------------------------------------
public class BatchTotal
implements BatchTotalIfc
{                                       // begin class BatchTotal
    // This id is used to tell
    // the compiler not to generate a
    // new serialVersionUID.
    //
    static final long serialVersionUID = 4759737445992934192L;

    /**
        revision number supplied by source-code-control system
    **/
    public static String revisionNumber = "$Revision: /rgbustores_13.4x_generic_branch/1 $";
    /**
        batch identifier
    **/
    protected String batchID = "";
    /**
        store identifier
    **/
    protected String storeID = "";
    /**
        timestamp of first transaction
    **/
    protected EYSDate firstTransactionTimestamp = null;
    /**
        timestamp of last transaction
    **/
    protected EYSDate lastTransactionTimestamp = null;
    /**
        timestamp for which batch is completed
    **/
    protected EYSDate batchCompleteTimestamp = null;
    /**
        count of transactions
    **/
    protected int transactionCount = 0;
    /**
        total of transactions
    **/
    protected CurrencyIfc transactionTotal = null;

    //----------------------------------------------------------------------------
    /**
        Constructs BatchTotal object. <P>
    **/
    //----------------------------------------------------------------------------
    public BatchTotal()
    {                                   // begin BatchTotal()
        transactionTotal = DomainGateway.getBaseCurrencyInstance();
    }                                   // end BatchTotal()

    //---------------------------------------------------------------------
    /**
       Adds the values for a transaction to the current batch object.
       @param transactionTimestamp timestamp of transaction to be added
       @param total total of transaction
    **/
    //---------------------------------------------------------------------
    public void addTransaction(EYSDate transactionTimestamp,
                               CurrencyIfc total)
    {                                   // begin addTransaction()
        if (transactionTimestamp != null)
        {
            if (firstTransactionTimestamp == null ||
                transactionTimestamp.before(firstTransactionTimestamp))
            {
                firstTransactionTimestamp = transactionTimestamp;
            }
            if (lastTransactionTimestamp == null ||
                transactionTimestamp.after(lastTransactionTimestamp))
            {
                lastTransactionTimestamp = transactionTimestamp;
            }
        }
        transactionTotal = transactionTotal.add(total);
        transactionCount++;
    }                                   // end addTransaction()

    //---------------------------------------------------------------------
    /**
       Adds the values for a given batch to this batch.<P>
       If the first transaction timestamp in the given batch is prior to the
       timestamp in this batch, the first transaction timestamp is set to the
       value in the given batch.  The same holds true for the last transaction
       timestamp. <P>
       The batch ID and batchCompleteTimestamp are not affected.
       @param addBatch batch to be added
    **/
    //---------------------------------------------------------------------
    public void add(BatchTotalIfc addBatch)
    {                                   // begin add()
        if (addBatch.getFirstTransactionTimestamp() != null &&
            (firstTransactionTimestamp == null ||
             addBatch.getFirstTransactionTimestamp().before(firstTransactionTimestamp)))
        {
            firstTransactionTimestamp = addBatch.getFirstTransactionTimestamp();
        }
        if (addBatch.getLastTransactionTimestamp() != null &&
            (lastTransactionTimestamp == null ||
             addBatch.getLastTransactionTimestamp().after(lastTransactionTimestamp)))
        {
            lastTransactionTimestamp = addBatch.getLastTransactionTimestamp();
        }
        transactionTotal = transactionTotal.add(addBatch.getTransactionTotal());
        transactionCount = transactionCount + addBatch.getTransactionCount();
    }                                   // end add()

    //---------------------------------------------------------------------
    /**
       Builds batch identifier from current timestamp.
    **/
    //---------------------------------------------------------------------
    public void buildBatchID()
    {                                   // begin buildBatchID()
        EYSDate currentTime = DomainGateway.getFactory().getEYSDateInstance();
        SimpleDateFormat dateFormat = new SimpleDateFormat(BATCH_ID_DATE_FORMAT);
        setBatchID(dateFormat.format(currentTime.dateValue()));
        StringBuffer buffer = new StringBuffer(getStoreID())
          .append(dateFormat.format(currentTime.dateValue()));
        setBatchID(buffer.toString());
    }                                   // end buildBatchID()

    //----------------------------------------------------------------------------
    /**
        Creates clone of this object. <P>
        @return Object clone of this object
    **/
    //----------------------------------------------------------------------------
    public Object clone()
    {                                   // begin clone()
        // instantiate new object
        BatchTotal c = new BatchTotal();

        // set values
        setCloneAttributes(c);

        // pass back Object
        return((Object) c);
    }                                   // end clone()

    //----------------------------------------------------------------------------
    /**
        Sets attributes in clone of this object. <P>
        @param newClass new instance of object
    **/
    //----------------------------------------------------------------------------
    public void setCloneAttributes(BatchTotal newClass)
    {                                   // begin setCloneAttributes()
        newClass.setBatchID(getBatchID());
        if (firstTransactionTimestamp != null)
        {
            newClass.setFirstTransactionTimestamp((EYSDate) getFirstTransactionTimestamp().clone());
        }
        if (lastTransactionTimestamp != null)
        {
            newClass.setLastTransactionTimestamp((EYSDate) getLastTransactionTimestamp().clone());
        }
        if (batchCompleteTimestamp != null)
        {
            newClass.setBatchCompleteTimestamp((EYSDate) getBatchCompleteTimestamp().clone());
        }
        newClass.setTransactionCount(getTransactionCount());
        if (transactionTotal != null)
        {
            newClass.setTransactionTotal((CurrencyIfc) getTransactionTotal().clone());
        }
        newClass.setStoreID(storeID);
    }                                   // end setCloneAttributes()

    //----------------------------------------------------------------------------
    /**
        Determine if two objects are identical. <P>
        @param obj object to compare with
        @return true if the objects are identical, false otherwise
    **/
    //----------------------------------------------------------------------------
    public boolean equals(Object obj)
    {                                   // begin equals()
        boolean isEqual = true;
        // confirm object instanceof this object
        if (obj instanceof BatchTotal)
        {                                   // begin compare objects

            BatchTotal c = (BatchTotal) obj;      // downcast the input object

            // compare all the attributes of BatchTotal
            if (Util.isObjectEqual(getBatchID(), c.getBatchID()) &&
                Util.isObjectEqual(getFirstTransactionTimestamp(),
                                   c.getFirstTransactionTimestamp()) &&
                Util.isObjectEqual(getLastTransactionTimestamp(),
                                   c.getLastTransactionTimestamp()) &&
                Util.isObjectEqual(getBatchCompleteTimestamp(),
                                   c.getBatchCompleteTimestamp()) &&
                Util.isObjectEqual(getStoreID(), c.getStoreID()) &&
                getTransactionCount() == c.getTransactionCount() &&
                Util.isObjectEqual(getTransactionTotal(), c.getTransactionTotal()))
            {
                isEqual = true;             // set the return code to true
            }
            else
            {
                isEqual = false;            // set the return code to false
            }
        }                                   // end compare objects
        else
        {
            isEqual = false;
        }
        return(isEqual);
    }                                   // end equals()

    //----------------------------------------------------------------------------
    /**
        Retrieves batch identifier. <P>
        @return batch identifier
    **/
    //----------------------------------------------------------------------------
    public String getBatchID()
    {                                   // begin getBatchID()
        return(batchID);
    }                                   // end getBatchID()

    //----------------------------------------------------------------------------
    /**
        Sets batch identifier. <P>
        @param value  batch identifier
    **/
    //----------------------------------------------------------------------------
    public void setBatchID(String value)
    {                                   // begin setBatchID()
        batchID = value;
    }                                   // end setBatchID()

    //----------------------------------------------------------------------------
    /**
        Retrieves store identifier. <P>
        @return store identifier
    **/
    //----------------------------------------------------------------------------
    public String getStoreID()
    {                                   // begin getStoreID()
        return(storeID);
    }                                   // end getStoreID()

    //----------------------------------------------------------------------------
    /**
        Sets store identifier. <P>
        @param value  store identifier
    **/
    //----------------------------------------------------------------------------
    public void setStoreID(String value)
    {                                   // begin setStoreID()
        storeID = value;
    }                                   // end setStoreID()

    //----------------------------------------------------------------------------
    /**
        Retrieves timestamp of first transaction. <P>
        @return timestamp of first transaction
    **/
    //----------------------------------------------------------------------------
    public EYSDate getFirstTransactionTimestamp()
    {                                   // begin getFirstTransactionTimestamp()
        return(firstTransactionTimestamp);
    }                                   // end getFirstTransactionTimestamp()

    //----------------------------------------------------------------------------
    /**
        Sets timestamp of first transaction. <P>
        @param value  timestamp of first transaction
    **/
    //----------------------------------------------------------------------------
    public void setFirstTransactionTimestamp(EYSDate value)
    {                                   // begin setFirstTransactionTimestamp()
        firstTransactionTimestamp = value;
    }                                   // end setFirstTransactionTimestamp()

    //----------------------------------------------------------------------------
    /**
        Retrieves timestamp of last transaction. <P>
        @return timestamp of last transaction
    **/
    //----------------------------------------------------------------------------
    public EYSDate getLastTransactionTimestamp()
    {                                   // begin getLastTransactionTimestamp()
        return(lastTransactionTimestamp);
    }                                   // end getLastTransactionTimestamp()

    //----------------------------------------------------------------------------
    /**
        Sets timestamp of last transaction. <P>
        @param value  timestamp of last transaction
    **/
    //----------------------------------------------------------------------------
    public void setLastTransactionTimestamp(EYSDate value)
    {                                   // begin setLastTransactionTimestamp()
        lastTransactionTimestamp = value;
    }                                   // end setLastTransactionTimestamp()

    //----------------------------------------------------------------------------
    /**
        Retrieves timestamp for which batch is completed. <P>
        @return timestamp for which batch is completed
    **/
    //----------------------------------------------------------------------------
    public EYSDate getBatchCompleteTimestamp()
    {                                   // begin getBatchCompleteTimestamp()
        return(batchCompleteTimestamp);
    }                                   // end getBatchCompleteTimestamp()

    //----------------------------------------------------------------------------
    /**
        Sets timestamp for which batch is completed. <P>
        @param value  timestamp for which batch is completed
    **/
    //----------------------------------------------------------------------------
    public void setBatchCompleteTimestamp(EYSDate value)
    {                                   // begin setBatchCompleteTimestamp()
        batchCompleteTimestamp = value;
    }                                   // end setBatchCompleteTimestamp()

    //----------------------------------------------------------------------------
    /**
        Sets timestamp for which batch is completed to current time. <P>
    **/
    //----------------------------------------------------------------------------
    public void setBatchCompleteTimestamp()
    {                                   // begin setBatchCompleteTimestamp()
        batchCompleteTimestamp = DomainGateway.getFactory().getEYSDateInstance();
    }                                   // end setBatchCompleteTimestamp()

    //----------------------------------------------------------------------------
    /**
        Retrieves count of transactions. <P>
        @return count of transactions
    **/
    //----------------------------------------------------------------------------
    public int getTransactionCount()
    {                                   // begin getTransactionCount()
        return(transactionCount);
    }                                   // end getTransactionCount()

    //----------------------------------------------------------------------------
    /**
        Sets count of transactions. <P>
        @param value  count of transactions
    **/
    //----------------------------------------------------------------------------
    public void setTransactionCount(int value)
    {                                   // begin setTransactionCount()
        transactionCount = value;
    }                                   // end setTransactionCount()

    //----------------------------------------------------------------------------
    /**
        Retrieves total of transactions. <P>
        @return total of transactions
    **/
    //----------------------------------------------------------------------------
    public CurrencyIfc getTransactionTotal()
    {                                   // begin getTransactionTotal()
        return(transactionTotal);
    }                                   // end getTransactionTotal()

    //----------------------------------------------------------------------------
    /**
        Sets total of transactions. <P>
        @param value  total of transactions
    **/
    //----------------------------------------------------------------------------
    public void setTransactionTotal(CurrencyIfc value)
    {                                   // begin setTransactionTotal()
        transactionTotal = value;
    }                                   // end setTransactionTotal()

    //----------------------------------------------------------------------------
    /**
        Returns default display string. <P>
        @return String representation of object
    **/
    //----------------------------------------------------------------------------
    public String toString()
    {                                   // begin toString()
        // build result string
        StringBuilder strResult =
          Util.classToStringHeader("BatchTotal",
                                    getRevisionNumber(),
                                    hashCode());
        // add attributes to string
        strResult.append(Util.formatToStringEntry("batchID",
                                                  getBatchID()))
                 .append(Util.formatToStringEntry("storeID",
                                                  getStoreID()))
                 .append(Util.formatToStringEntry("firstTransactionTimestamp",
                                                  getFirstTransactionTimestamp()))
                 .append(Util.formatToStringEntry("lastTransactionTimestamp",
                                                  getLastTransactionTimestamp()))
                 .append(Util.formatToStringEntry("batchCompleteTimestamp",
                                                  getBatchCompleteTimestamp()))
                 .append(Util.formatToStringEntry("transactionCount",
                                                  getTransactionCount()))
                 .append(Util.formatToStringEntry("transactionTotal",
                                                  getTransactionTotal()));
        // pass back result
        return(strResult.toString());
    }                                   // end toString()

    //----------------------------------------------------------------------------
    /**
        Retrieves the source-code-control system revision number. <P>
        @return String representation of revision number
    **/
    //----------------------------------------------------------------------------
    public String getRevisionNumber()
    {                                   // begin getRevisionNumber()
        // return string
        return(revisionNumber);
    }                                   // end getRevisionNumber()


    //----------------------------------------------------------------------------
    /**
        BatchTotalmain method. <P>
        @param String args[]  command-line parameters
    **/
    //----------------------------------------------------------------------------
    public static void main(String args[])
    {                                   // begin main()
        // instantiate class
        BatchTotal c = new BatchTotal();
        // output toString()
        System.out.println(c.toString());
    }                                   // end main()
}                                       // end class BatchTotal
