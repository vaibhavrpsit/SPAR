/* ===========================================================================
* Copyright (c) 2012, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/modules/domain/src/oracle/retail/stores/domain/transaction/StatusChangeTransaction.java /main/2 2012/12/10 19:14:47 tksharma Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    tksharma  12/10/12 - commons-lang update 3.1
 *    jswan     01/05/12 - Refactor the status change of suspended transaction
 *                         to occur in a transaction so that status change can
 *                         be sent to CO as part of DTM.
 *                         
 * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */

package oracle.retail.stores.domain.transaction;
// java imports
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;
//--------------------------------------------------------------------------
/**
    This class helps track changes to a transaction's status when it his suspended
    and then retrieved or canceled.
    <p>
    @version $Revision: /main/2 $
**/
//--------------------------------------------------------------------------
public class StatusChangeTransaction
extends Transaction
implements StatusChangeTransactionIfc, Serializable
{
    /**
     * serialVersionUID
     */
    private static final long serialVersionUID = 62020053542725259L;
    /**
        revision number of this class
    **/
    public static String revisionNumber = "$Revision: /main/2 $";

    protected List<TransactionSummaryIfc> transactionSummaries = null;

    /**
     * Constructs VoidTransaction object.
     */
    public StatusChangeTransaction()
    {
        initialize();
    }

    /**
     * Initializes object.
     */
    protected void initialize()
    {
        transactionType = TransactionIfc.TYPE_STATUS_CHANGE;
        transactionSummaries = new ArrayList<TransactionSummaryIfc>();
    }

    //---------------------------------------------------------------------
    /**
        Clones StatusChangeTransaction object.
        <p>
        @return instance of StatusChangeTransaction object
    **/
    //---------------------------------------------------------------------
    public Object clone()
    {
        // instantiate new object
        StatusChangeTransaction trans = new StatusChangeTransaction();

        setCloneAttributes(trans);

        // pass back object
        return((Object)trans);
    }

    // ---------------------------------------------------------------------
    /**
     * Sets attributes in new instance of class.
     * <P>
     *
     * @param newClass new instance of class
     */
    // ---------------------------------------------------------------------
    public void setCloneAttributes(StatusChangeTransaction newClass)
    {
        // set attributes in super class
        super.setCloneAttributes(newClass);
        // set attributes
        for(TransactionSummaryIfc summary: transactionSummaries)
        {
            newClass.addTransactionSummary((TransactionSummaryIfc)summary.clone());
        }
    }

    /* (non-Javadoc)
     * @see java.lang.Object#equals(java.lang.Object)
     */
    @Override
    public boolean equals(Object o)
    {
        if (o instanceof StatusChangeTransaction)
        {
            StatusChangeTransaction other = (StatusChangeTransaction)o;
            EqualsBuilder builder = new EqualsBuilder();
            appendEquals(builder, other);
            return builder.isEquals();
        }
        return false;
    }

    /**
     * Add objects for comparison to the builder. Overriding methods should also
     * call super.appendEquals(EqualsBuilder, AuthorizeTransferResponse).
     *
     * @param builder
     * @see #equals(Object)
     */
    protected void appendEquals(EqualsBuilder builder, StatusChangeTransaction other)
    {
        builder.append(transactionSummaries, other.transactionSummaries);
    }
    
    /* (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString()
    {
        ToStringBuilder builder = new ToStringBuilder(this);
        appendToString(builder);
        return builder.toString();
    }

    /**
     * Add printable objects to the builder. Overriding methods should also
     * call super.appendToString(ToStringBuilder).
     *
     * @param builder
     * @see #toString()
     */
    protected void appendToString(ToStringBuilder builder)
    {
        builder.append("transactionSummaries", transactionSummaries);
    }

    /**
     * @return the transactionSummaries
     */
    public List<TransactionSummaryIfc> getTransactionSummaries()
    {
        return transactionSummaries;
    }

    /**
     * @param transactionSummaries the transactionSummaries to set
     */
    public void setTransactionSummaries(
            List<TransactionSummaryIfc> transactionSummaries)
    {
        this.transactionSummaries = transactionSummaries;
    }

    /**
     * @param transactionSummaries the transactionSummaries to set
     */
    public void addTransactionSummary(TransactionSummaryIfc transactionSummary)
    {
        transactionSummaries.add(transactionSummary);
    }
}
